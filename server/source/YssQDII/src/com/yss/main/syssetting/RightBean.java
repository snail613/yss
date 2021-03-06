package com.yss.main.syssetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Date;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.YssPub;
import com.yss.main.cusreport.RepGroupSetBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.FlowBean;
import com.yss.main.funsetting.MenubarBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *
 * <p>Title: RightBean </p>
 * <p>Description: 权限设置 </p>
 * <p>Copyright: Copyright (c) 2006 </p>
 * <p>Company: Ysstech </p>
 * @author not attributable
 * @version 1.0
 */
public class RightBean
    extends BaseDataSettingBean implements IDataSetting { //chenyibo 20071226  要把用户权限记入日志，一定要实现IDataSetting 这个接口
    private String funModuleName = "";  //模块名称
    private String rightTypeCode = "";  //权限代码  anotation by  yeshenghong , the right type is removed, so the attribute left unused
    private String rightTypeName = "";  //权限名称  anotation by  yeshenghong , the right type is removed, so the attribute left unused
    private String type = "";           //权限类型 "system" or "fund"
    private String operationTypes = ""; //操作类型
    private String userCode = "";       //用户代码
    private String assetGroupCode = ""; //组合群代码
    private String rightInd = "";       //权限标识 "Right" or "Role" or "Port"
    private String portCodes = "";
    private String reportCodes = "";
    //private String windowName = "";     //by caocheng MS00001 QDV4.1
    //anotation by  yeshenghong , the right type is removed, so the attribute left unused
    private String strData = "";
    private RightBean filterType;

    //----MS00010 add by songjie 2009-05-21----//
    private String strPortGroupCodes = "";  //组合分页上已选的组合群组合代码
    private String strGroupCodes = "";      //组合群分页上已选的组合群代码
    //----MS00010 add by songjie 2009-05-21----//

    /**shashijie 2012-7-11 STORY 2661 */
	private String FRoleCode = "";//角色代码
	private String FRightCode = "";//权限代码
	/**end*/
    
    public void setStrData() {

    }

    public String getStrData() {
        return this.strData;
    }

    public RightBean() {
    }

    public RightBean(YssPub pub) {
        setYssPub(pub);
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOperTypes() {
        return operationTypes;
    }

    public void setOperTypes(String operTypes) {
        this.operationTypes = operTypes;
    }

    //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-06-08----//
    public void setRightType(String rightType) {
        this.type = rightType;
    }

    public String getRightType() {
        return this.type;
    }

    public void setRightCode(String rightCode) {
        this.rightTypeCode = rightCode;
    }

    public String getRightCode() {
        return rightTypeCode;
    }

    public void setPortCode(String portCode) {
        this.portCodes = portCode;
    }

    public String getPortCode() {
        return portCodes;
    }

    public void setRightInd(String rightInd) {
        this.rightInd = rightInd;
    }

    public String getRightInd() {
        return rightInd;
    }

    //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-06-08----//

    /**
     * MS00010 add by songjie 2009-04-30
     * QDV4赢时胜（上海）2009年02月01日10_A
     * 用于复制用户权限
     * @param sReqRow String
     * @throws YssException
     */
    public void copyUserRight(String sReqRow) throws YssException {
        try {
            String strSql = "";                 //生命储存sql语句的字符串
            String oldUserCode = null;          //系统登录时的用户代码
            String newUserCodes = null;         //权限复制界面选择的用户代码组成的字符串
            String[] oldAndNewUserCodes = null; //所有被复制和药复制的用户代码
            String[] newUserCode = null;        //拆分后的权限复制界面选择的用户代码
            if (sReqRow != null) {
                oldAndNewUserCodes = sReqRow.split("\f\f");
                oldUserCode = oldAndNewUserCodes[0];    //获取系统登录时的用户代码
                newUserCodes = oldAndNewUserCodes[1];   //获取权限复制界面选择的用户代码
                if (oldUserCode == null || newUserCodes == null) {
                    return;
                }
                newUserCode = newUserCodes.split(","); //拆分权限复制界面选择的用户代码
                for (int i = 0; i < newUserCode.length; i++) { //循环选择的用语代码
                    strSql = " delete from tb_sys_userRight where FUserCode = " + dbl.sqlString(newUserCode[i].trim()) + " and FRightType <>'system'";
                    dbl.executeSql(strSql); //删除已选用户原有的权限
                    strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightType,FOperTypes,FRightCode,FRightInd) select " +
                        dbl.sqlString(newUserCode[i].trim()) + ",FPortCode,FAssetGroupCode,FRightType,FOperTypes,FRightCode,FRightInd" +
                        " from tb_sys_userRight where FUserCode = " + dbl.sqlString(oldUserCode.trim()) + " and FRightType <>'system'";
                    dbl.executeSql(strSql); //给已选用户设置和系统登录时的用户一样的权限
                }
            }
        } catch (Exception e) {
            throw new YssException("复制用户权限出错", e);
        }
    }

    /**
     * MS00010 add by songjie 2009-04-30
     * QDV4赢时胜（上海）2009年02月01日10_A
     * 用于在复制同一用户下的组合权限
     * @param sReqRow String
     * @throws YssException
     */
    public void copyUserPortRight(String sReqRow) throws YssException {
        try {
            String strSql = null;
            if (sReqRow != null) {
                String[] allInfo = sReqRow.split("\f\f");   //拆分所有信息
                String oldUserCode = allInfo[0];            //获取系统用户代码
                String oldAssetGroupCode = allInfo[1];      //获取权限设置界面选择的组合群代码
                String oldPortCode = allInfo[2];            //获取权限设置界面选择的组合代码
                String newGPInfos = allInfo[3];             //获取权限复制界面已选的组合群组合代码信息
                String[] detailInfo = null;
                String newAssetGroupCode = null;
                String newPortCode = null;
                if (oldUserCode == null || oldAssetGroupCode == null ||
                    oldPortCode == null || newGPInfos == null) {
                    return;
                }

                String[] newGPInfo = newGPInfos.split("\t");    //拆分权限复制界面已选组合群组合信息
                for (int i = 0; i < newGPInfo.length; i++) {    //循环权限复制界面已选组合群组合
                    detailInfo = newGPInfo[i].split("-");       //拆分组合群代码组合代码
                    newAssetGroupCode = detailInfo[0];          //获取当前指针指向的组合群代码
                    if (detailInfo.length > 1) {
                        newPortCode = detailInfo[1];            //获取当前指针指向的组合代码
                    }
                    if (newAssetGroupCode == null ||
                        (detailInfo.length > 1 && newPortCode == null)) {
                        return;
                    }

                    if (detailInfo.length == 1) { //表示只选择了组合群代码
                        strSql = " delete from tb_sys_userRight where FUserCode = " +
                            dbl.sqlString(oldUserCode.trim()) +
                            " and FAssetGroupCode= " +
                            dbl.sqlString(newAssetGroupCode.trim()) +
                            " and FRightType <>'system'";
                    } else { //表示选择了组合代码
                        strSql = " delete from tb_sys_userRight where FUserCode = " +
                            dbl.sqlString(oldUserCode.trim()) +
                            " and FAssetGroupCode= " +
                            dbl.sqlString(newAssetGroupCode.trim()) +
                            " and FPortCode= " + dbl.sqlString(newPortCode.trim()) +
                            " and FRightType <>'system'";
                    }
                    dbl.executeSql(strSql); //删除系统用户的已选组合群组合权限

                    if (detailInfo.length == 1) { //表示只选择了组合群代码
                        strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select FUserCode," +
                            "FPortCode," + dbl.sqlString(newAssetGroupCode.trim()) +
                            ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                            dbl.sqlString(oldUserCode.trim()) + " and FAssetGroupCode= " +
                            dbl.sqlString(oldAssetGroupCode.trim()) +
                            " and FRightType <>'system'";
                    } else { //表示选择了组合代码
                        strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select FUserCode," +
                            dbl.sqlString(newPortCode.trim()) + "," +
                            dbl.sqlString(newAssetGroupCode.trim()) +
                            ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                            dbl.sqlString(oldUserCode.trim()) + " and FAssetGroupCode= " +
                            dbl.sqlString(oldAssetGroupCode.trim()) +
                            " and FPortCode = " + dbl.sqlString(oldPortCode.trim()) +
                            " and FRightType <>'system'";
                    }
                    dbl.executeSql(strSql); //给系统用户的权限复制界面已选组合或组合群设置与权限设置界面选择的组合或组合群一样的权限
                }
            }
        } catch (Exception e) {
            throw new YssException("复制组合权限出错", e);
        }
    }

    /**
     * MS00010 add by songjie 2009-04-30
     * QDV4赢时胜（上海）2009年02月01日10_A
     * @param sReqRow String
     * @throws YssException
     */
    public void copyUserAndPortRight(String sReqRow) throws YssException {
        try {
            String strSql = null;               //用于储存sql语句
            String newAssetGroupCode = null;    //当前循环的组合群代码
            String newPortCode = null;          //当前循环的组合代码
            String oldUserCode = null;          //系统用户代码
            String oldAssetGroupCode = null;    //权限设置界面已选的组合群代码
            String oldPortCode = null;          //权限设置界面已选的组合代码
            String newUserCodes = null;         //权限复制界面已选用户代码
            String newGPInfos = null;           //权限复制界面已选组合群组合信息
            String[] allInfo = null;            //获取的所有信息
            String[] userCodes = null;          //权限复制界面已选的用户代码
            String[] gpInfo = null;             //组合群组合代码

            if (sReqRow != null) {
                allInfo = sReqRow.split("\f\f");//拆分获取的所有信息
                oldUserCode = allInfo[0];       //获取系统用户代码
                oldAssetGroupCode = allInfo[1]; //获取权限设置界面已选的组合群代码
                oldPortCode = allInfo[2];       //获取权限设置界面已选的组合代码
                newUserCodes = allInfo[3];      //获取权限复制界面已选用户代码
                newGPInfos = allInfo[4];        //获取权限复制界面已选组合群组合信息

                if (oldUserCode == null || oldAssetGroupCode == null ||
                    newGPInfos == null || newUserCodes == null) {
                    return;
                }

                userCodes = newUserCodes.split(",");    //拆分权限复制界面已选的用户代码
                gpInfo = newGPInfos.split("\t");        //拆分组合群组合代码

                for (int i = 0; i < userCodes.length; i++) {        //循环权限复制界面已选用户
                    for (int j = 0; j < gpInfo.length; j++) {       //循环权限复制界面依循组合群组合信息
                        newAssetGroupCode = gpInfo[j].split("-")[0];//获取当前循环的组合群代码
                        if (gpInfo[j].split("-").length > 1) {      //若权限复制界面已选信息包括组合信息
                            newPortCode = gpInfo[j].split("-")[1];  //获取当前循环的组合代码
                            strSql =
                                " delete from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(userCodes[i].trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                " and FPortCode= " +
                                dbl.sqlString(newPortCode.trim()) +
                                " and FRightType <>'system'";
                            dbl.executeSql(strSql); //删除当前指向的用户当前指向的组合群组合权限

                            strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                dbl.sqlString(newPortCode.trim()) + "," +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(oldAssetGroupCode.trim()) +
                                " and FPortCode = " +
                                dbl.sqlString(oldPortCode.trim()) +
                                " and FRightType <>'system'";
                            dbl.executeSql(strSql); //将当前指向的用户当前指向的组合群组合的权限设置成和权限设置界面已选的组合群组合一样的权限
                        } else { //若权限复制界面已选信息只包括组合群信息
                            strSql =
                                " delete from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(userCodes[i].trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                " and FRightType <>'system'";
                            dbl.executeSql(strSql); //删除当前指向的用户当前指向的组合群权限

                            strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                "FPortCode," +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(oldAssetGroupCode.trim()) +
                                " and FRightType <>'system'";
                            dbl.executeSql(strSql); //将当前指向的用户当前指向的组合群的权限设置成和权限设置界面已选的组合群一样的权限
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("复制用户组合权限出错", e);
        }
    }
    /**
     * STORY 1572 add by guolongchao 20111110
     * QDV4嘉实2011年08月24日01_A.xls
     * @param sReqRow String
     * @throws YssException
     */
    public void copyUserAndPortRightALL(String sReqRow) throws YssException {
        try {
            String strSql = null;               //用于储存sql语句
            String newAssetGroupCode = null;    //当前循环的组合群代码
            String newPortCode = null;          //当前循环的组合代码
            String oldUserCode = null;          //系统用户代码
            String oldAssetGroupCode = null;    //权限设置界面已选的组合群代码
            String oldPortCode = null;          //权限设置界面已选的组合代码
            String newUserCodes = null;         //权限复制界面已选用户代码
            String newGPInfos = null;           //权限复制界面已选组合群组合信息
            String[] allInfo = null;            //获取的所有信息
            String[] userCodes = null;          //权限复制界面已选的用户代码
            String[] gpInfo = null;             //组合群组合代码

            if (sReqRow != null) {
                allInfo = sReqRow.split("\f\f");//拆分获取的所有信息
                oldUserCode = allInfo[0];       //获取系统用户代码
                oldAssetGroupCode = allInfo[1]; //获取权限设置界面已选的组合群代码
                oldPortCode = allInfo[2];       //获取权限设置界面已选的组合代码
                newUserCodes = allInfo[3];      //获取权限复制界面已选用户代码
                newGPInfos = allInfo[4];        //获取权限复制界面已选组合群组合信息

                if (oldUserCode == null || oldAssetGroupCode == null ||
                    newGPInfos == null || newUserCodes == null) {
                    return;
                }

                userCodes = newUserCodes.split(",");    //拆分权限复制界面已选的用户代码
                gpInfo = newGPInfos.split("\t");        //拆分组合群组合代码
                
                //组合级权限复制
                for (int i = 0; i < userCodes.length; i++) {        //循环权限复制界面已选用户
                    for (int j = 0; j < gpInfo.length; j++) {       //循环权限复制界面依循组合群组合信息
                        newAssetGroupCode = gpInfo[j].split("-")[0];//获取当前循环的组合群代码
                        if (gpInfo[j].split("-").length > 1) //若权限复制界面已选信息包括组合信息
                        {      
                            newPortCode = gpInfo[j].split("-")[1];  //获取当前循环的组合代码
                            strSql = " delete from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(userCodes[i].trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                " and FPortCode= " +
                                dbl.sqlString(newPortCode.trim()) +
                                " and FRightType <>'system' ";
                            dbl.executeSql(strSql); //删除当前指向的用户当前指向的组合群组合权限
                            strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                dbl.sqlString(newPortCode.trim()) + "," +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(oldAssetGroupCode.trim()) +
                                " and FPortCode = " +
                                dbl.sqlString(oldPortCode.trim()) +
                                " and FRightType <>'system'";
                            dbl.executeSql(strSql); //将当前指向的用户的组合群组合的权限设置成和权限设置界面已选的组合群组合一样的权限
                        } 
                    }
                }
                //组合群级权限复制
                for (int i = 0; i < userCodes.length; i++) {        //循环权限复制界面已选用户
                    for (int j = 0; j < gpInfo.length; j++) {       //循环权限复制界面依循组合群组合信息
                        newAssetGroupCode = gpInfo[j].split("-")[0];//获取当前循环的组合群代码                      
                            strSql =
                                " delete from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(userCodes[i].trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                " and FRightType = 'group' and frightind <> 'DataInterface' and frightind <> 'Report'";
                            dbl.executeSql(strSql); //删除当前指向的用户当前指向的组合群权限
                            strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                "FPortCode," +
                                dbl.sqlString(newAssetGroupCode.trim()) +
                                ",FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and FAssetGroupCode= " +
                                dbl.sqlString(oldAssetGroupCode.trim()) +
                                " and FRightType = 'group' and frightind <> 'DataInterface' and frightind <> 'Report'";
                            dbl.executeSql(strSql); //将当前指向的用户的组合群的权限设置成和权限设置界面已选的组合群一样的权限
                    }
                }
                //公共级别权限复制
                for (int i = 0; i < userCodes.length; i++) //循环权限复制界面已选用户
                {                         
                      strSql = " delete from tb_sys_userRight where FUserCode = " + dbl.sqlString(userCodes[i].trim()) +
                                " and FRightType = 'public'";
                      dbl.executeSql(strSql); //删除当前指向的用户当前指向的公共级别权限
                      strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                "FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and FRightType = 'public'";
                      dbl.executeSql(strSql); //将当前指向的用户的公共级别权限设置成和权限设置界面已选的的用户的公共级别权限一样的权限
                }
                //报表权限复制     接口权限复制   
                for (int i = 0; i < userCodes.length; i++) //循环权限复制界面已选用户
                {                         
                      strSql = " delete from tb_sys_userRight where FUserCode = " + dbl.sqlString(userCodes[i].trim()) +
                                " and (frightind = 'DataInterface' or frightind = 'Report')";
                      dbl.executeSql(strSql); //删除当前指向的用户的报表权限 ,接口权限
                      strSql = " insert into tb_sys_userRight(FUserCode,FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType) select " +
                                dbl.sqlString(userCodes[i].trim()) + "," +
                                "FPortCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType from tb_sys_userRight where FUserCode = " +
                                dbl.sqlString(oldUserCode.trim()) +
                                " and (frightind = 'DataInterface' or frightind = 'Report')";
                      dbl.executeSql(strSql); 
                }
           }            
        } catch (Exception e) {
            throw new YssException("复制用户组合权限出错", e);
        }
    }
    
    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090422
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 解析字符串sReqRow
    /// </summary>
    public void protocolParse(String sReqRow) throws YssException {
        String reqAry[] = null;
        try {

            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sReqRow.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sReqRow.split("<Logging>")[1];
            }
            sReqRow = sReqRow.split("<Logging>")[0];
            //================end=====================
            reqAry = sReqRow.split("\t");
            this.userCode = reqAry[0];
            this.assetGroupCode = reqAry[1];
            this.rightTypeCode = reqAry[2];
            this.operationTypes = reqAry[3];
            this.type = reqAry[4];
            this.rightInd = reqAry[5];
            if (reqAry.length >= 7) {
                this.portCodes = reqAry[6];
            }
            if (reqAry.length >= 9) {
                this.reportCodes = reqAry[8];
            }
            /**shashijie 2012-7-11 STORY 2661 */
			if (reqAry.length >= 8) {
				this.FRoleCode = reqAry[7];
			}
			if (reqAry.length >= 9) {
				this.FRightCode = reqAry[8];
			}
			/**end*/
            // 以filterType作为筛选条件用于获取权限类型 add by wangzuochun 2009.04.29 MS00010
            if (sReqRow.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RightBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.protocolParse(sReqRow.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析菜单设置协议出错", e);
        }

    }

    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090430
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 连接字符串
    /// </summary>
    private String buildRespStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.funModuleName).append("\t");
        buf.append(this.rightTypeCode).append("\t");
        buf.append(this.rightTypeName).append("\t");
        buf.append(this.operationTypes).append("\t");
//        buf.append(this.windowName).append("\t"); //removed by yeshenghong story2917 20130121
        buf.append(this.portCodes).append("\t");    //fanghaoln 090422 可用组合代码 MS00010 系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
        buf.append(this.type).append("\t");         //fanghaoln 090422 权限类型 MS00010 系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
        buf.append(this.assetGroupCode).append("\t"); // wangzuochun 090430 MS00010
        buf.append(YssCons.YSS_LINESPLITMARK);
        return buf.toString();
    }

    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090422
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 传入参数为""的情况
    /// </summary>
    public String getFundRightType() throws YssException {
        return getFundRightType("");
    }

    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090422
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 根据传入参数squest从TB_FUN_MENUBAR获取记录集,拼成字符串传到前台
    /// </summary>
    public String getFundRightType(String squest) throws YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        MenubarBean menubar = null;
        try {
            strSql = "select * from Tb_Sys_OperationType where FType = 'system'";
            rs = dbl.openResultSet(strSql);
            buf.append("模块名称\t").append("权限类型\t");
            while (rs.next()) {
                buf.append(rs.getString("FOperTypeCode")).append(",").append(rs.
                    getString("FOperTypeName")).append("\t");
            }
            //关闭游标
            dbl.closeResultSetFinal(rs);
            
            /**shashijie 2013-1-25 STORY 无需求编号,将自审菜单永远显示在第一列*/
            //add by guolongchao 20120426 QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc------start
            if(buf.toString().indexOf("auditOwn")<=-1) {//若不存在自审操作类型，则添加
            	//buf.append("auditOwn,自审\t");
            	buf = getStringBuffer(buf,"auditOwn,自审\t","add");
            }
            //add by guolongchao 20120426 QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc------end
            /**end shashijie 2013-1-25 STORY */
            
            buf.append("null" + YssCons.YSS_LINESPLITMARK);

            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 start---//
            boolean judgeCompany = true;
            if(YssCons.companyName.indexOf("赢时胜") != -1){
            	judgeCompany = false;
            }
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 end---//
            
            // 查出权限类型为public,port,group记录集 2009.04.23 wangzuochun MS00010 QDV4赢时胜（上海）2009年02月01日10_A
            strSql = "SELECT * FROM TB_FUN_MENUBAR WHERE" +
                " (FRightType IS NULL OR FRightType <> 'system' OR FRightType <> '') AND FOrderCode <> '000' " +
                //--- add by songjie 2013.01.24 排除FCompanyName 不等于  fundacc.lic文件的公司名称的菜单 start---//
                (judgeCompany ? 
                " and FCompanyName is null or (FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0) " : "") +
                //--- add by songjie 2013.01.24 排除FCompanyName 不等于  fundacc.lic文件的公司名称的菜单 end---//
                " Order By FOrderCode";
            rs = dbl.openResultSet(strSql);

            // 根据squest获取对应权限类型的菜单条  2009.04.23 wangzuochun MS00010 QDV4赢时胜（上海）2009年02月01日10_A
            while (rs.next()) {
                String strRs = rs.getString("FRightType");
                if ( (strRs != null) && (strRs.indexOf(squest) != -1)) {
                    menubar = new MenubarBean();
                    menubar.setMenubarAttr(rs);
                    buf.append(menubar.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**shashijie 2013-1-25 将要插入的字符串插入到指定字符串前,此方法可以公用
	* @param buf 整体
	* @param string 插入字符
	* @param string2 被插入到该字符之前的字符
	* @return*/
	public StringBuffer getStringBuffer(StringBuffer buf, String vString,
			String cString) {
		if (buf==null) {
			return new StringBuffer();
		}
		if (buf.indexOf(cString) > -1) {
			int ide = buf.indexOf(cString);
			//前半段
			String stat = buf.substring(0, ide);
			//后半段
			String end = buf.substring(ide);
			//全部拼接
			String all = stat + vString + end;
			buf.setLength(0);
			buf.append(all);
		}
		return buf;
	}
	

	/**
     * 按组合群导出权限 add by wangzuochun 2010.03.05 
     * MS00921  增加浏览用户所有权限和导出相关信息的功能  QDV4银华2010年01月08日01_A    
     * @param squest
     * @return
     * @throws YssException
     */
    public String exportForGroup(String squest) throws YssException {
    	
    	String strSql = "";
    	String strUserCode = "";
        String strGroupCode = "";
        String squestAry[] = null;
        
        ResultSet rsUser = null;
        ResultSet rsGroup = null;
        ResultSet rsReport = null;
        StringBuffer buf = new StringBuffer();
        
        HashMap hmGroupRight = new HashMap();
        ArrayList groupList = new ArrayList();
        
    	try{
    		squestAry = squest.split("\t");
    		
    		buf.append("\f\f按组合群导出权限：\f\f");  
    		
    		for (int i = 0; i < squestAry.length; i++)
    		{
    			strGroupCode = squestAry[i];
    			
    			buf.append("\f\f组合群").append(strGroupCode).append("权限：\f\f");
    			
//    			strSql = "Select * from Tb_Sys_UserList where " +
//    					 " fportgroupcode like '%" + strGroupCode + "%' order by fusercode"; 
    			/*
				 * modified by zhaoxianlin 20120821 Story #2766 QDV4海富通2012年06月28日01_A
				 */
				strSql = "select distinct a.FUSERCODE from Tb_Sys_Userright a inner join Tb_Sys_UserList b  on  a.fusercode=b.fusercode " +
						"and b.fiscancel='0' and " + " a.FASSETGROUPCODE like '%" + strGroupCode + "%'" +
						"order by a.fusercode"; 
    			rsUser = dbl.openResultSet(strSql);
    			while(rsUser.next())
    			{
    				strUserCode = rsUser.getString("FUserCode");
    				
    				buf.append("\f\f\f\f组合群").append(strGroupCode).append("-").
    					append("用户").append(strUserCode).append("\f\f");
        			buf.append("组合群级别:\f\f");
                    buf.append("菜单名称\t").append("增加\t").append("删除\t").append("修改\t").
                    	append("浏览\t").append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");
    				
    				strSql = "SELECT rig.frightcode,rig.frighttype, rig.fopertypes, menu.fbarcode, menu.fbarname,menu.fordercode"
						+ " FROM (select fbarcode, fbarname, frighttype,fordercode"
						+ " from Tb_Fun_MenuBar where frighttype like '%group%') menu"
						+ " JOIN (SELECT frightcode,fopertypes,frighttype"
						+ " FROM Tb_Sys_Userright"
						+ " WHERE FUserCode = '"
						+ strUserCode
						+ "' AND fassetgroupcode = '"
						+ strGroupCode
						+ "' and FPortCode = ' ' and FRightInd = 'Right' and FRightType = 'group'"
						+ ") rig ON menu.fbarcode = rig.FRightCode"
						+
						// =========================================================================================================
						" UNION"
						+ " SELECT a.frightcode,a.frighttype, ur.fopertypes, m.fbarcode AS fmenubarcode, m.fbarname,m.fordercode"
						+ " FROM Tb_Sys_Userright a"
						+ " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode"
						+ " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode"
						+ " WHERE a.FUserCode = '"
						+ strUserCode
						+ "' AND a.FRightType = 'group'"
						+ " AND a.FAssetGroupCode = '"
						+ strGroupCode
						+ "' and a.FPortCode = ' ' AND a.Frightind = 'Role'"
						+ " AND m.frighttype LIKE '%group%' Order by fordercode"; // 组合群权限并上角色权限
    				
    				
    				rsGroup = dbl.openResultSet(strSql);

					while (rsGroup.next()) {
						this.rightTypeCode = rsGroup.getString("FBarCode"); // 获取菜单条代码
						if (this.rightTypeCode == null) {
							continue;
						}

						if (hmGroupRight.containsKey(this.rightTypeCode)) { // 判断hashtable里面是否含有当前对象
							RightBean right = (RightBean) hmGroupRight
									.get(this.rightTypeCode); // 得到hashtble里的对象
							String sRightTypeCode = rsGroup
									.getString("fopertypes"); // 得到对象里的权限
							if (sRightTypeCode == null) {
								continue;
							}
							String[] arrCode = sRightTypeCode.split(","); // 解析出当前对象的权限
							for (int j = 0; j < arrCode.length; j++) { // 循环当前对象的权限
								if (right.operationTypes.indexOf(arrCode[j]) == -1) { // 看hashtable里面是否有当前对象的权限
									right.operationTypes += ("," + arrCode[j]); // 没就加入
								}
							}
						} else {
							RightBean right = new RightBean(pub); // 如果hashtable里面没有当前对象就new一个
							right.rightTypeCode = rsGroup.getString("FBarCode"); // 获取菜单条代码
							right.operationTypes = rsGroup
									.getString("FOperTypes"); // 获取操作类型
							right.rightTypeName = rsGroup.getString("FBarName"); // 获取菜单条名称
							hmGroupRight.put(right.rightTypeCode, right); // 把当前对象加入到hatable里面去
							groupList.add(right);
						}
					}
					
					hmGroupRight.clear();

					for (int k = 0; k < groupList.size(); k++) {

						RightBean right = (RightBean) groupList.get(k); // 循环把hashMap里的内容取出来
						buf.append(right.rightTypeName).append("\t");
						if (right.operationTypes != null && right.operationTypes.indexOf("add") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("del") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("edit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("brow") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("execute") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("audit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("clear") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("revert") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						buf.append("\f\f");

					}
					
					groupList.clear();
					// 关闭游标
					dbl.closeResultSetFinal(rsGroup);
					
					buf.append("\f\f报表权限：\f\f");
		            buf.append("报表名称：\t").append("权 限\f\f");
		            
		            strSql = " select a.frightcode,b.FRepGrpName from (select * from Tb_Sys_Userright " +
		            		 " where FRightind = 'Report' and fusercode = '" + strUserCode + 
		            		 "' and fassetgroupcode = '" + strGroupCode +
		            		 "' ) a join (select FRepGrpCode,FRepGrpName,FOrderCode from Tb_" + strGroupCode + "_Rep_Group" + 
		                     " ) b on b.FRepGrpCode = a.frightcode order by b.fordercode ";
		            
		            rsReport = dbl.openResultSet(strSql);
		            
		            while (rsReport.next()){
		            	buf.append(rsReport.getString("frightcode")).
		            		append("-").append(rsReport.getString("FRepGrpName")).
		            		append("\t").append("√").append("\f\f");
		            }
		            dbl.closeResultSetFinal(rsReport);
    			}
    			
    			dbl.closeResultSetFinal(rsUser);
    		}
    		
    		return buf.toString();
    		
    	} catch (Exception e) {
    		throw new YssException("导出组合群权限出错", e);
    	} finally {
    		dbl.closeResultSetFinal(rsUser);
    		dbl.closeResultSetFinal(rsGroup);
    		dbl.closeResultSetFinal(rsReport);
    	}
    }
    
    /**
     * 按组合导出权限 add by wangzuochun 2010.03.05 
     * MS00921  增加浏览用户所有权限和导出相关信息的功能  QDV4银华2010年01月08日01_A    
     * @param squest
     * @return
     * @throws YssException
     */
    public String exportForPort(String squest) throws YssException {
    	String strSql = "";
    	String strUserCode = "";
    	String strPortCode = "";
        String strGroupCode = "";
        String squestAry[] = null;
        
        ResultSet rsUser = null;
        ResultSet rsPort = null;
        StringBuffer buf = new StringBuffer();
        
        HashMap hmPortRight = new HashMap();
        ArrayList portList = new ArrayList();
    	try{
    		squestAry = squest.split("\r\t");
    		
    		buf.append("\f\f按组合导出权限：\f\f");

			for (int i = 0; i < squestAry.length; i++) {
				strGroupCode = squestAry[i].split("\t")[0];
				strPortCode = squestAry[i].split("\t")[1];		

				buf.append("\f\f组合群").append(strGroupCode).append("-").
					append("组合").append(strPortCode).
					append("权限：\f\f");		
    			
//    			strSql = "Select * from Tb_Sys_UserList where " +
//    					 " fportgroupcode like '%" + strGroupCode + "%' order by fusercode"; 
				/*
				 * modified by zhaoxianlin 20120821 Story #2766 QDV4海富通2012年06月28日01_A
				 */
				strSql = "select distinct a.FUSERCODE from Tb_Sys_Userright a inner join Tb_Sys_UserList b  on  a.fusercode=b.fusercode " +
						"and b.fiscancel='0' and " + " a.FASSETGROUPCODE like '%" + strGroupCode + "%' and a.FPORTCODE='"+strPortCode+"'" 
						+"order by a.fusercode"; 
    			rsUser = dbl.openResultSet(strSql);
    			while(rsUser.next())
    			{
    				strUserCode = rsUser.getString("FUserCode");
    				
    				buf.append("\f\f\f\f组合群").append(strGroupCode).append("-").
    					append("组合").append(strPortCode).append("-").
    					append("用户").append(strUserCode).
    					append("权限：\f\f");
        			buf.append("组合级别:\f\f");
                    buf.append("菜单名称\t").
                    	append("增加\t").append("删除\t").append("修改\t").append("浏览\t").
                    	append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");
                    
                    strSql = "SELECT rig.frightcode,rig.frighttype, rig.fopertypes, menu.fbarcode, menu.fbarname,menu.fordercode"
						+ " FROM (select fbarcode, fbarname, frighttype,fordercode"
						+ " from Tb_Fun_MenuBar where frighttype like '%port%') menu"
						+ " JOIN (SELECT frightcode,fopertypes,frighttype"
						+ " FROM Tb_Sys_Userright"
						+ " WHERE FUserCode = '"
						+ strUserCode
						+ "' AND fassetgroupcode = '"
						+ strGroupCode
						+ "' and FPortCode = '"
						+ strPortCode
						+ "' and FRightInd = 'Right' and FRightType = 'port'"
						+ ") rig ON menu.fbarcode = rig.FRightCode"
						+
						// =========================================================================================================
						" UNION"
						+ " SELECT a.frightcode,a.frighttype, ur.fopertypes, m.fbarcode AS fmenubarcode, m.fbarname,m.fordercode"
						+ " FROM Tb_Sys_Userright a"
						+ " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode"
						+ " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode"
						+ " WHERE a.FUserCode = '"
						+ strUserCode
						+ "' AND a.FRightType = 'port'"
						+ " AND a.FAssetGroupCode = '"
						+ strGroupCode
						+ "' and a.FPortCode = '"
						+ strPortCode
						+ "' AND a.Frightind = 'Role'"
						+ " AND m.frighttype LIKE '%port%' Order by fordercode"; // 组合权限并上角色权限
                    // ==============================================================================

                    rsPort = dbl.openResultSet(strSql);

                    while (rsPort.next()) {
						this.rightTypeCode = rsPort.getString("FBarCode"); // 获取菜单条代码
						if (this.rightTypeCode == null) {
							continue;
						}

						if (hmPortRight.containsKey(this.rightTypeCode)) { // 判断hashtable里面是否含有当前对象
							RightBean right = (RightBean) hmPortRight
									.get(this.rightTypeCode); // 得到hashtble里的对象
							String sRightTypeCode = rsPort
									.getString("fopertypes"); // 得到对象里的权限
							if (sRightTypeCode == null) {
								continue;
							}
							String[] arrCode = sRightTypeCode.split(","); // 解析出当前对象的权限
							for (int j = 0; j < arrCode.length; j++) { // 循环当前对象的权限
								//20121113 modified by liubo.Bug #6220
								//添加非空验证，避免空指针异常
								//===============================
								//if (right.operationTypes.indexOf(arrCode[j]) == -1)
								if (right.operationTypes != null && right.operationTypes.indexOf(arrCode[j]) == -1) { // 看hashtable里面是否有当前对象的权限
								//=============end==================
									
									right.operationTypes += ("," + arrCode[j]); // 没就加入
								}
							}
						} else {
							RightBean right = new RightBean(pub); // 如果hashtable里面没有当前对象就new一个
							right.rightTypeCode = rsPort.getString("FBarCode"); // 获取菜单条代码
							right.operationTypes = rsPort
									.getString("FOperTypes"); // 获取操作类型
							right.rightTypeName = rsPort.getString("FBarName"); // 获取菜单条名称
							hmPortRight.put(right.rightTypeCode, right); // 把当前对象加入到hatable里面去
							portList.add(right);
						}
					}
					
                    hmPortRight.clear(); //清空hashtable中的内容

					for (int k = 0; k < portList.size(); k++) {

						RightBean right = (RightBean) portList.get(k); // 循环把list里的内容取出来
						buf.append(right.rightTypeName).append("\t");
						if (right.operationTypes != null && right.operationTypes.indexOf("add") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("del") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("edit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("brow") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("execute") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("audit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("clear") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("revert") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						buf.append("\f\f");

					}
					
					portList.clear(); // 清空list中的对象
				
					// 关闭游标
					dbl.closeResultSetFinal(rsPort);
    			}
			}
    	
    		return buf.toString();
    		
    	} catch (Exception e) {
    		throw new YssException("导出组合权限出错", e);
    	} finally {
    		dbl.closeResultSetFinal(rsUser);
    		dbl.closeResultSetFinal(rsPort);
    	}
    }
    
    /**
     * 按角色导出权限 add by wangzuochun 2010.03.05 
     * MS00921  增加浏览用户所有权限和导出相关信息的功能  QDV4银华2010年01月08日01_A    
     * @param squest
     * @return
     * @throws YssException
     */

    
    
    //---add by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
    public String exportForUser(String squest) throws YssException {
    	String strUserCode = "";
        String strGroupPortCode = "";
        String [] squestAry = null;
        String [] strUserCodeAry = null;
        StringBuffer buf = new StringBuffer();
        String strReturn = "";
        
        squestAry = squest.split("\r\t");        
    	strUserCode = squestAry[0];
    	strGroupPortCode = squestAry[1];
    	strUserCodeAry = strUserCode.split(",");
    	
    	buf.append("\f\f按用户导出权限：\f\f\f\f");
    	for(int i = 0; i < strUserCodeAry.length; i++){
			try {
				buf.append(exportForUser(strUserCodeAry[i], strGroupPortCode));
				buf.append("\f\f\f\f");
			} catch (Exception e) {
				throw new YssException("导出用户权限出错", e);
			}
    	}
    	if (buf.toString().length() > 18) { // 去除多余的解析符/f/f
            strReturn = buf.toString().substring(0,buf.toString().length() - 4);
        }
    	return strReturn;
    }
    //---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
    /**
     * 按用户导出权限 add by wangzuochun 2010.03.05 
     * MS00921  增加浏览用户所有权限和导出相关信息的功能  QDV4银华2010年01月08日01_A    
     * @param squest
     * @return
     * @throws YssException
     */
    public String exportForUser(String strUserCode, String strGroupPortCode) throws YssException {
        String strSql = "", strReturn = "";
        //String strUserCode = "";
        //String strGroupPortCode = "";
        //String strGroupCode = "";
        String squestAry[] = null;
        
        ResultSet rsPub = null;
        ResultSet rsPort = null;
        ResultSet rsGroup = null;
        ResultSet rsSystem = null;
        ResultSet rsReport = null;
        ResultSet rsDAO = null;
        
        StringBuffer buf = new StringBuffer();
        
        HashMap hmPubRight = new HashMap();
        HashMap hmPortRight = new HashMap();
        HashMap hmGroupRight = new HashMap();
        
        ArrayList pubList = new ArrayList();
        ArrayList portList = new ArrayList();
        ArrayList groupList = new ArrayList();
        
        try {
//        	squestAry = squest.split("\r\t");
//        	strUserCode = squestAry[0];
//        	strGroupPortCode = squestAry[1];
        	//strGroupCode = squestAry[2]; modified by  yeshenghong story2917 20130118
        	//---edit by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
        	//buf.append("\f\f按用户导出权限：\f\f\f\f");        	
        	//---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
            buf.append("用户").append(strUserCode).append("权限：\f\f");
            buf.append("\f\f公共级别:\f\f");
            buf.append("菜单名称\t").
            	append("增加\t").append("删除\t").append("修改\t").append("浏览\t").
            	append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");
        	
        	
        	strSql = "SELECT rig.frightcode,rig.frighttype, rig.fopertypes, menu.fbarcode, menu.fbarname,menu.fordercode" +
            " FROM (select fbarcode, fbarname, frighttype,fordercode" +
            " from Tb_Fun_MenuBar where frighttype like '%public%') menu" +
            " JOIN (SELECT frightcode,fopertypes,frighttype" +
            " FROM Tb_Sys_Userright" +
            " WHERE FUserCode = '" + strUserCode +
            "' AND fassetgroupcode = ' ' and FPortCode = ' ' and FRightInd = 'Right' and FRightType = 'public'" +
            ") rig ON menu.fbarcode = rig.FRightCode" +
            //=========================================================================================================
            " UNION" +
            " SELECT a.frightcode,a.frighttype, ur.fopertypes, m.fbarcode AS fmenubarcode, m.fbarname,m.fordercode" +
            " FROM Tb_Sys_Userright a" +
            " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode" +
            " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode" +
            " WHERE a.FUserCode = '" + strUserCode +
            "' AND a.FRightType = 'public'" +
            " AND a.FAssetGroupCode = ' ' and FPortCode = ' ' "+
            " AND a.Frightind = 'Role'" +
            " AND m.frighttype LIKE '%public%' Order by fordercode"; //公共权限并上角色权限
            //==============================================================================        	
        	
        	rsPub = dbl.openResultSet(strSql);
        	      	
        	while (rsPub.next()) {
            	this.rightTypeCode = rsPub.getString("FBarCode"); //获取权限类型代码
                if(this.rightTypeCode == null){
                    continue;
                }
            	
            	if (hmPubRight.containsKey(this.rightTypeCode)) { //判断hashtable里面是否含有当前对象
                    RightBean right = (RightBean) hmPubRight.get(this.
                        rightTypeCode); //得到hashtble里的对象
                    String sRightTypeCode = rsPub.getString("fopertypes"); //得到对象里的权限
					if (sRightTypeCode == null){
                    	continue;
                    }
					//---add by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
					if(right.operationTypes == null){
						right.operationTypes = "";
					}					
					//---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
                    String[] arrCode = sRightTypeCode.split(",");       //解析出当前对象的权限
                    for (int i = 0; i < arrCode.length; i++) {          //循环当前对象的权限
                        if (right.operationTypes.indexOf(arrCode[i]) == -1) { //看hashtable里面是否有当前对象的权限
                            right.operationTypes += ("," + arrCode[i]); //没就加入
                        }
                    }
                } else {
                    RightBean right = new RightBean(pub);   //如果hashtable里面没有当前对象就new一个
                    right.rightTypeCode = rsPub.getString("FBarCode");   //获取权限代码
                    right.operationTypes = rsPub.getString("FOperTypes");  //获取操作类型
                    right.rightTypeName = rsPub.getString("FBarName");  //获取窗体名称
                    hmPubRight.put(right.rightTypeCode, right);    //把当前对象加入到hatable里面去
                    pubList.add(right);
                }
            }
        	
        	for (int i = 0; i < pubList.size(); i++)
        	{
                RightBean right = (RightBean) pubList.get(i); //循环把hashMap里的内容取出来
                buf.append(right.rightTypeName).append("\t");
                if (right.operationTypes != null && right.operationTypes.indexOf("add") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("del") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("edit") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("brow") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("execute") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("audit") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("clear") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		if (right.operationTypes != null && right.operationTypes.indexOf("revert") != -1){
        			buf.append("√\t");
        		}
        		else{
        			buf.append(" \t");
        		}
        		buf.append("\f\f");
        		
            }
        	//关闭游标
            dbl.closeResultSetFinal(rsPub);
        	
            if (strGroupPortCode.equalsIgnoreCase("null")) {
            	//---edit by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
            	//return buf.toString();            	
            	//---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
			} else {
				buf.append("\f\f组合级别:\f\f");
				
				String arr[] = strGroupPortCode.split("\t");

				for (int i = 0; i < arr.length; i++) {
					String strGroup = arr[i].split("-")[0];
					String strPort = arr[i].split("-")[1];

					buf.append("\f\f组合群" + strGroup + "-" + "组合"
							+ strPort + "权限：\f\f");
					buf.append("菜单名称\t").
            			append("增加\t").append("删除\t").append("修改\t").append("浏览\t").
            			append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");

					strSql = "SELECT rig.frightcode,rig.frighttype, rig.fopertypes, menu.fbarcode, menu.fbarname,menu.fordercode"
							+ " FROM (select fbarcode, fbarname, frighttype,fordercode"
							+ " from Tb_Fun_MenuBar where frighttype like '%port%') menu"
							+ " JOIN (SELECT frightcode,fopertypes,frighttype"
							+ " FROM Tb_Sys_Userright"
							+ " WHERE FUserCode = '"
							+ strUserCode
							+ "' AND fassetgroupcode = '"
							+ strGroup
							+ "' and FPortCode = '"
							+ strPort
							+ "' and FRightInd = 'Right' and FRightType = 'port'"
							+ ") rig ON menu.fbarcode = rig.FRightCode"
							+
							// =========================================================================================================
							" UNION"
							+ " SELECT a.frightcode,a.frighttype, ur.fopertypes, m.fbarcode AS fmenubarcode, m.fbarname,m.fordercode"
							+ " FROM Tb_Sys_Userright a"
							+ " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode"
							+ " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode"
							+ " WHERE a.FUserCode = '"
							+ strUserCode
							+ "' AND a.FRightType = 'port'"
							+ " AND a.FAssetGroupCode = '"
							+ strGroup
							+ "' and a.FPortCode = '"
							+ strPort
							+ "' AND a.Frightind = 'Role'"
							+ " AND m.frighttype LIKE '%port%' Order by fordercode"; // 公共权限并上角色权限
					// ==============================================================================

					rsPort = dbl.openResultSet(strSql);

					while (rsPort.next()) {
						this.rightTypeCode = rsPort.getString("FBarCode"); // 获取权限类型代码
						if (this.rightTypeCode == null) {
							continue;
						}

						if (hmPortRight.containsKey(this.rightTypeCode)) { // 判断hashtable里面是否含有当前对象
							RightBean right = (RightBean) hmPortRight
									.get(this.rightTypeCode); // 得到hashtble里的对象
							String sRightTypeCode = rsPort
									.getString("fopertypes"); // 得到对象里的权限
							if (sRightTypeCode == null) {
								continue;
							}
							//---add by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
							if(right.operationTypes == null){
								right.operationTypes = "";
							}
							//---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
							String[] arrCode = sRightTypeCode.split(","); // 解析出当前对象的权限
							for (int j = 0; j < arrCode.length; j++) { // 循环当前对象的权限
								if (right.operationTypes.indexOf(arrCode[j]) == -1) { // 看hashtable里面是否有当前对象的权限
									right.operationTypes += ("," + arrCode[j]); // 没就加入
								}
							}
						} else {
							RightBean right = new RightBean(pub); // 如果hashtable里面没有当前对象就new一个
							right.rightTypeCode = rsPort.getString("FBarCode"); // 获取权限代码
							right.operationTypes = rsPort
									.getString("FOperTypes"); // 获取操作类型
							right.rightTypeName = rsPort.getString("FBarName"); // 获取窗体名称
							hmPortRight.put(right.rightTypeCode, right); // 把当前对象加入到hatable里面去
							portList.add(right);
						}
					}

					for (int k = 0; k < portList.size(); k++) {

						RightBean right = (RightBean) portList.get(k); // 循环把hashMap里的内容取出来
						buf.append(right.rightTypeName).append("\t");
						if (right.operationTypes != null && right.operationTypes.indexOf("add") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("del") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("edit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("brow") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("execute") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("audit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("clear") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (right.operationTypes != null && right.operationTypes.indexOf("revert") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						buf.append("\f\f");

					}
					// 关闭游标
					dbl.closeResultSetFinal(rsPort);
					//---add by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
					hmPortRight.clear();
					//---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
				}
			}
            //codes removed by yeshenghong 20130118 group right type is split, the codes is useless
//            if (strGroupCode.equalsIgnoreCase("null")) {
//				return buf.toString();
//			} else {
//				buf.append("\f\f组合群级别:\f\f");
//				
//				//String arr[] = strGroupCode.split("\t");
//
//				for (int i = 0; i < arr.length; i++) {
//					String strGroup = arr[i];

//					buf.append("\f\f组合群" + strGroup + "权限：\f\f");
//					buf.append("菜单名称\t").
//            			append("增加\t").append("删除\t").append("修改\t").append("浏览\t").
//            			append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");
//
//					strSql = "SELECT rig.frightcode,rig.frighttype, rig.fopertypes, menu.fbarcode, menu.fbarname,menu.fordercode"
//							+ " FROM (select fbarcode, fbarname, frighttype,fordercode"
//							+ " from Tb_Fun_MenuBar where frighttype like '%group%') menu"
//							+ " JOIN (SELECT frightcode,fopertypes,frighttype"
//							+ " FROM Tb_Sys_Userright"
//							+ " WHERE FUserCode = '"
//							+ strUserCode
//							+ "' AND fassetgroupcode = '"
//							+ strGroup
//							+ "' and FPortCode = ' ' and FRightInd = 'Right' and FRightType = 'group'"
//							+ ") rig ON menu.fbarcode = rig.FRightCode"
//							+
//							// =========================================================================================================
//							" UNION"
//							+ " SELECT a.frightcode,a.frighttype, ur.fopertypes, m.fbarcode AS fmenubarcode, m.fbarname,m.fordercode"
//							+ " FROM Tb_Sys_Userright a"
//							+ " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode"
//							+ " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode"
//							+ " WHERE a.FUserCode = '"
//							+ strUserCode
//							+ "' AND a.FRightType = 'group'"
//							+ " AND a.FAssetGroupCode = '"
//							+ strGroup
//							+ "' and a.FPortCode = ' ' AND a.Frightind = 'Role'"
//							+ " AND m.frighttype LIKE '%group%' Order by fordercode"; // 公共权限并上角色权限
					// ==============================================================================

//					rsGroup = dbl.openResultSet(strSql);
//
//					while (rsGroup.next()) {
//						this.rightTypeCode = rsGroup.getString("FBarCode"); // 获取权限类型代码
//						if (this.rightTypeCode == null) {
//							continue;
//						}
//
//						if (hmGroupRight.containsKey(this.rightTypeCode)) { // 判断hashtable里面是否含有当前对象
//							RightBean right = (RightBean) hmGroupRight
//									.get(this.rightTypeCode); // 得到hashtble里的对象
//							String sRightTypeCode = rsGroup
//									.getString("fopertypes"); // 得到对象里的权限
//							if (sRightTypeCode == null) {
//								continue;
//							}
//							String[] arrCode = sRightTypeCode.split(","); // 解析出当前对象的权限
//							for (int j = 0; j < arrCode.length; j++) { // 循环当前对象的权限
//								if (right.operationTypes.indexOf(arrCode[j]) == -1) { // 看hashtable里面是否有当前对象的权限
//									right.operationTypes += ("," + arrCode[j]); // 没就加入
//								}
//							}
//						} else {
//							RightBean right = new RightBean(pub); // 如果hashtable里面没有当前对象就new一个
//							right.rightTypeCode = rsGroup.getString("FBarCode"); // 获取菜单条代码
//							right.operationTypes = rsGroup
//									.getString("FOperTypes"); // 获取操作类型
//							right.rightTypeName = rsGroup.getString("FBarName"); // 获取菜单条名称
//							hmGroupRight.put(right.rightTypeCode, right); // 把当前对象加入到hatable里面去
//							groupList.add(right);
//						}
//					}

//					for (int k = 0; k < groupList.size(); k++) {
//
//						RightBean right = (RightBean) groupList.get(k); // 循环把hashMap里的内容取出来
//						buf.append(right.rightTypeName).append("\t");
//						if (right.operationTypes != null && right.operationTypes.indexOf("add") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("del") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("edit") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("brow") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("execute") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("audit") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("clear") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						if (right.operationTypes != null && right.operationTypes.indexOf("revert") != -1) {
//							buf.append("√\t");
//						} else {
//							buf.append(" \t");
//						}
//						buf.append("\f\f");
//
//					}
//					// 关闭游标
//					dbl.closeResultSetFinal(rsGroup);
					strSql = "Select * from (Select fbarcode, fbarname, frighttype, fordercode " + 
							 		"from Tb_Fun_MenuBar where frighttype = 'system') menu " +
							 "join (select frightcode,fopertypes from Tb_Sys_Userright " +
							 "where fusercode = '" + strUserCode + "' and fassetgroupcode = '" + " " +
							 "' and frighttype = 'system' and fportcode = ' ' ) a on a.frightcode = menu.fbarcode order by fordercode";
					
					buf.append("\f\f").append("系统权限： \f\f");
					buf.append("菜单名称\t").append("自审\t").
        				append("增加\t").append("删除\t").append("修改\t").append("浏览\t").
        				append("行使\t").append("审核\t").append("清除\t").append("还原\f\f");
					
					rsSystem = dbl.openResultSet(strSql);

					while (rsSystem.next()) {
						String strBarName = rsSystem.getString("fBarName");
						if (strBarName == null){
							continue;
						}
						buf.append(strBarName).append("\t");
						
						String strOperTypes = rsSystem.getString("fopertypes"); // 得到对象里的权限
						if (strOperTypes == null) {
							continue;
						}
						if(strOperTypes.indexOf("auditOwn") != -1){
							buf.append("√\t");
						}else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("add") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("del") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("edit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("brow") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("execute") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("audit") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("clear") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						if (strOperTypes.indexOf("revert") != -1) {
							buf.append("√\t");
						} else {
							buf.append(" \t");
						}
						buf.append("\f\f");
					}
					dbl.closeResultSetFinal(rsSystem);
          //String arr[] = strGroupCode.split("\t");
            //
//            				for (int i = 0; i < arr.length; i++) {
          //codes removed by yeshenghong 20130118-------------------------------------------------end
					buf.append("\f\f报表权限：\f\f");
		            buf.append("报表名称：\t").append("权 限\f\f");
		            
		            //---edit by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
		            StringBuffer tmpAssetGroupBuf = new StringBuffer();
		            String tmpAssetGroupStr = "";
		            String [] tmpAssetGroupAry = null;
		            ResultSet rs = null;
		            strSql = "select t.fassetgroupcode from tb_sys_assetgroup t";
		            rs = dbl.openResultSet(strSql);
		            		            		            
		            while(rs.next()){
		            	tmpAssetGroupBuf.append(rs.getString("fassetgroupcode")).append(",");		            	
		            }
		            dbl.closeResultSetFinal(rs);
		            
		            if(tmpAssetGroupBuf != null){
		            	tmpAssetGroupStr = tmpAssetGroupBuf.toString();
		            	tmpAssetGroupStr = tmpAssetGroupStr.substring(0,tmpAssetGroupStr.length()-1);
		            	tmpAssetGroupAry = tmpAssetGroupStr.split(",");
		            }
		            //---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
		            
		            for(int i = 0; i < tmpAssetGroupAry.length; i++){
		            	strSql = " select a.frightcode,b.FRepGrpName from (select * from Tb_Sys_Userright " +
		            		 " where FRightind = 'Report' and fusercode = '" + strUserCode + 
		            		 "' and fassetgroupcode = " + dbl.sqlString(tmpAssetGroupAry[i]) +
		            		 " ) a join (select FRepGrpCode,FRepGrpName,FOrderCode from " + "Tb_"+tmpAssetGroupAry[i]+"_Rep_Group" + 
		                     " ) b on b.FRepGrpCode = a.frightcode order by b.fordercode ";
		            
		            	rsReport = dbl.openResultSet(strSql);
		            	buf.append("\f\f");
		            	while (rsReport.next()){
		            		buf.append(rsReport.getString("frightcode")).
		            			append("-").append(rsReport.getString("FRepGrpName")).
		            			append("\t").append("√").append("\f\f");
		            	}
		            
		            	dbl.closeResultSetFinal(rsReport);
		            }
//				}
//			}           
        	
			// ---add by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
			// 接口权限		    
			buf.append("\f\f接口权限：\f\f");
			buf.append("接口名称：\t").append("权 限\f\f");
			for(int i = 0; i < tmpAssetGroupAry.length; i++){
				 strSql = " select a.subCode, a.frightcode, b.fgroupname from " +
						  " (select substr(frightcode,instr(frightcode,'-')+1) as subCode,frightcode "+
						  " from Tb_Sys_Userright "+
						  " where FRightind = 'DataInterface' and fusercode = " + 
						  dbl.sqlString(strUserCode)+ " and fassetgroupcode = " +
						  dbl.sqlString(tmpAssetGroupAry[i]) +
						  " and instr(frightcode,'-') > 0) a "+
						  " join (select t1.fgroupcode, t1.fgroupname, t1.fordercode "+
						  " from Tb_" + tmpAssetGroupAry[i] + "_Dao_Group t1) b "+
						  " on b.fgroupcode = a.subCode order by b.fordercode ";
				 
				 rsDAO = dbl.openResultSet(strSql);
				 buf.append("\f\f");
				 buf.append("[" + tmpAssetGroupAry[i] + "]-" + tmpAssetGroupAry[i] + "组合群接口群").         		    
         		    append("\t").append("").append("\f\f");
				 while(rsDAO.next()){
					 buf.append(rsDAO.getString("subCode")).
	            		append("-").append(rsDAO.getString("fgroupname")).
	            		append("\t").append("√").append("\f\f");
				 }
				 dbl.closeResultSetFinal(rsDAO);
			}
			
			// ---end by hongqingbing 2014-01-02 Story_14614_需求上海_用户权限批量导出
            if (buf.toString().length() > 2) { // 去除多余的解析符/f/f
                strReturn = buf.toString().substring(0,buf.toString().length() - 2);
            }
            
            return strReturn;
        } catch (Exception e) {
            throw new YssException("导出用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rsPub);
            dbl.closeResultSetFinal(rsPort);
            dbl.closeResultSetFinal(rsGroup);
            dbl.closeResultSetFinal(rsReport);
            dbl.closeResultSetFinal(rsSystem);
            dbl.closeResultSetFinal(rsDAO);
        }
    }

    /**
	 * 保存用户可用组合
	 * 
	 * @param sAllReq
	 *            String
	 * @throws YssException
	 * @return String
	 */
    private void saveUserPorts(String sPorts) throws YssException {
        Connection conn = dbl.loadConnection();
        String[] aryAssetGroupCode = null;
        String[] allReqAry = sPorts.split(",");
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement pst = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            aryAssetGroupCode = this.assetGroupCode.split(",");

            for (int i = 0; i <= aryAssetGroupCode.length - 1; i++) {
            	//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                pst = conn.prepareStatement(
                //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                    "insert into Tb_Sys_UserRight" +
                    "(FUserCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd)" +
                    " values(?,?,?,?,?)",
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                for (int j = 0; j <= allReqAry.length - 1; j++) {
                    pst.setString(1, this.userCode);
                    pst.setString(2, aryAssetGroupCode[i]);
                    pst.setString(3, allReqAry[j]);
                    pst.setString(4, "");
                    pst.setString(5, "Port");
                    pst.executeUpdate();
                }
                
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                dbl.closeStatementFinal(pst);
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            }
        } catch (Exception e) {
            throw new YssException("保存用户可用组合出错", e);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        finally{
        	dbl.closeStatementFinal(pst);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
    }

    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090422
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 保存用户可用报表组
    /// </summary>
    private void saveUserReports(String sReports) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String[] aryAssetGroupCode = null;
        String[] allReqAry = sReports.split(",");
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement pst = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            aryAssetGroupCode = this.assetGroupCode.split(",");
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            for (int i = 0; i <= aryAssetGroupCode.length - 1; i++) {
                strSql =
                    "delete from Tb_Sys_UserRight where FRightInd = 'Report' and FUserCode='"
                    + this.userCode + "'" + " and FAssetGroupCode ='" +
                    aryAssetGroupCode[i] + "'";

                dbl.executeSql(strSql);
                //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                pst = conn.prepareStatement(
                //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//		
                    "insert into Tb_Sys_UserRight" +
                    "(FUserCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType,FPortCode)" +
                    " values(?,?,?,?,?,?,?)",
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                // 向UserRight表中循环插入用户报表记录
                for (int j = 0; j <= allReqAry.length - 1; j++) {
                    pst.setString(1, this.userCode);
                    pst.setString(2, aryAssetGroupCode[i]);
                    pst.setString(3, allReqAry[j]);
                    pst.setString(4, "");
                    pst.setString(5, "Report");
                    pst.setString(6, "group");
                    pst.setString(7, this.portCodes);
                    pst.executeUpdate();
                }
                
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                dbl.closeStatementFinal(pst);
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("保存用户可用报表组出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //事物处理
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(pst);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    /// <summary>
    /// 修改人：wangzuochun
    /// 修改时间:20090422
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 保存角色可用报表组
    /// </summary>
    private void saveRoleReports(String sReports) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String[] allReqAry = sReports.split(",");
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            // 从ROLERIGHT表中删除角色报表记录
            strSql =                       
                "delete from TB_SYS_ROLERIGHT where FOperTypes = 'Role' and FRoleCode='"//edit by licai 20101210 BUG #473 权限分配时，报错。 
//            	"delete from TB_SYS_ROLERIGHT where FRoleCode='"//edit by licai 20101210 BUG #473 权限分配时，报错。 (之前报错原因是库中有脏数据导致冲突)
                + this.userCode + "'";
            dbl.executeSql(strSql);
            PreparedStatement pst = conn.prepareStatement(
                "insert into TB_SYS_ROLERIGHT (FRoleCode,FRightCode,FOperTypes)" +
                " values(?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            // 向ROLERIGHT表中循环插入角色报表记录
            for (int j = 0; j <= allReqAry.length - 1; j++) {
                pst.setString(1, this.userCode);
                pst.setString(2, allReqAry[j]);
                pst.setString(3, "Role");
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("保存用户可用报表组出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //事物处理
        }
    }
    //add by guolongchao STORY 1781 20111124 #1781QDV4赢时胜(上海开发部)2011年10月24日01_A代码开发 
    private void saveRoleDataInterfaces(String sDataInterfaces) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = true;
        String[] allReqAry = sDataInterfaces.split(",");
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            // 从ROLERIGHT表中删除接口群记录
            strSql = "delete from TB_SYS_ROLERIGHT where FOperTypes = 'DataInterface' and FRoleCode='"+ this.userCode + "'";
            dbl.executeSql(strSql);
            PreparedStatement pst = conn.prepareStatement("insert into TB_SYS_ROLERIGHT (FRoleCode,FRightCode,FOperTypes)values(?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            // 向ROLERIGHT表中循环插入角色报表记录
            for (int j = 0; j <= allReqAry.length - 1; j++) 
            {
                pst.setString(1, this.userCode);
                pst.setString(2, allReqAry[j]);
                pst.setString(3, "DataInterface");
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("保存用户接口群出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); 
        }
    }
    
    private void saveRolePorts(String sPorts) throws YssException {
        this.protocolParse(sPorts);
    }

    /**
     * 保存用户权限
     * 2009-05-05 蒋锦 修改保存方式，用户权限使用用户代码和权限类型来删除，原来是使用组合群代码来删除的。
     * 同时现在该方法中只保存用户权限，不涉及报表权限
     * MS00010 《QDV4赢时胜（上海）2009年02月01日10_A》
     * @param sAllReq String
     * @throws YssException
     * @return String
     */
    public String saveUserRight(String sAllReq) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        PreparedStatement pst = null;
        String sErrInfo = "";
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-21----//
        String[] allInfo = null;
        String[] detailPortCode = null;
        String portCode = "";   //组合代码
        String groupCode = "";  //组合群代码
        String[] allReqAry = null;
        int count = 0;          //计数器，用来记录批处理条数 add by sunkey 20090525 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        ResultSet rsSys = null;
        ResultSet rsRoleRight = null;	//20120703 added by liubo.Bug #4987
        Map mapGroup=new HashMap();//by zhouwei 20120129 保存组合群代码
        try {
            //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-21----//
            if (sAllReq.indexOf("\r\f") != -1) { //若权限信息包含"\r\f"字符
                allInfo = sAllReq.split("\r\f"); //拆分字符串
                if (allInfo.length > 1) {
                    if (!allInfo[1].equals("")) {
                        this.strPortGroupCodes = allInfo[1]; //获取组合群代码-组合代码信息
                    }
                }

                if (allInfo.length > 2) {
                    if (!allInfo[2].equals("")) {
                        this.strGroupCodes = allInfo[2]; //获取组合群代码信息
                    }
                }
            }

            if (allInfo == null) {
                allReqAry = sAllReq.split(YssCons.YSS_LINESPLITMARK);
            } else {
                allReqAry = allInfo[0].split(YssCons.YSS_LINESPLITMARK); //拆分要保存的权限数据
            }
            //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-21----//

            this.protocolParse(allReqAry[0]); //先解析一条数据，用来获取基本信息，如用户代码 sunkey 20090815
            //判断当前是否有用户在线
            if (pub.getOnlineUser() != null) {
                //如果被修改的用户当前处于登陆状态并且不是当前登录用户
                if (pub.getOnlineUser().get(this.userCode) != null &&
                    !this.userCode.equalsIgnoreCase(pub.getUserCode())) {
                    sErrInfo = "\r\n【" + this.userCode + "】在线，不能修改该用户的权限！";
                    throw new YssException(sErrInfo);
                }
            }
            conn.setAutoCommit(false);
            bTrans = true;

            //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-21----//
            //============组合级别权限处理===================================
            if (strPortGroupCodes.length() > 0) { //若有已选组合群代码和组合代码
                detailPortCode = strPortGroupCodes.split("\t"); //拆分已选组合群代码组合代码
                for (int i = 0; i < detailPortCode.length; i++) { //循环已选组合群代码和组合代码
                    groupCode = detailPortCode[i].split("-")[0]; //获取组合群代码
                    portCode = detailPortCode[i].split("-")[1]; //获取组合代码
                    
                    strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) + " and FRightType =" +
                        dbl.sqlString(this.type) + " and FAssetGroupCode = " + dbl.sqlString(groupCode) +
                        " and FPortCode = " + dbl.sqlString(portCode) + " and FRightCode not in('userSet','roleSet','rightSet','system')";
                    dbl.executeSql(strSql); //删除相关组合群，组合，权限类型的权限数据
                }
            }
            //=============================================================

            //====================组合群级别权限处理==========================
            if (strGroupCodes.length() > 0) { //若有已选组合群代码信息
                detailPortCode = strGroupCodes.split("\t"); //拆分组合群代码
                for (int i = 0; i < detailPortCode.length; i++) {
                    groupCode = detailPortCode[i]; //获取当前组合群代码
                    if(!mapGroup.containsKey(groupCode)){
                    	mapGroup.put(groupCode, groupCode);
                    }
                    strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +
                        " and FRightType =" + dbl.sqlString(this.type) + " and FAssetGroupCode = " + dbl.sqlString(groupCode) +
                        //------ modify by wangzuochun 2009.11.17 MS00812 当赋予某组合群权限后，系统删除其它组合群权限 QDV4中金2009年11月16日01_B 
                    	//------ 加上FRightInd字段，否则在删除组合群级别权限的时候，会把报表的权限也删除掉
                        " and FRightCode not in('userSet','roleSet','rightSet','system')" + " and FRightInd not in ('Report')";
                        //------------------------------------- MS00812------------------------------------//
                    dbl.executeSql(strSql); //删除相关组合群，权限类型的权限数据
                }
            }
            //==============================================================

            //=========删除当前用户当前组合群的权限的报表权限====================
            //------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
            
            
            //Story #1313
            //+++++++++++++++++++++++++++++++++
            /**shashijie 2012-7-12 STORY 2661 增加Role判断,角色权限授予不走这里(原流程中增加接口权限的时候会同时删除之前刚新增的报表权限)*/
            if (!"DataInterface".equals(this.rightInd) && !this.rightInd.equalsIgnoreCase("Role"))
        	/**end*/
            {
	            strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +
	                //" and FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
	                " and FRightInd = 'Report'";
	            dbl.executeSql(strSql); //删除报表权限
	            //==============================================================
	
	            //2009.08.06 蒋锦 添加 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
	            //------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
	            strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +
	                //" and FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
	                " AND FRightType = 'report'" +
	                " and FRightInd = 'Role'";
	            dbl.executeSql(strSql); //删除报表的角色权限
	            
	            strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +
                //" and FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                " and FRightInd = 'DataInterface'";
	            dbl.executeSql(strSql); //删除接口权限
	            
	            //---add  by guolongchao 2011.11.28 STORY #1781   删除接口群的角色权限 start---//
	            strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +	                
	                     " AND FRightType = 'datainterface'" +
	                     " and FRightInd = 'Role'";
	            dbl.executeSql(strSql); 
	          	//---add  by guolongchao 2011.11.28 STORY #1781   删除接口群的角色权限 end---//
	
	            // 从UserRight中删除public,port,group权限类型记录
	            if (strPortGroupCodes.length() == 0 && strGroupCodes.length() == 0) {
	                strSql = "delete from Tb_Sys_UserRight where FUserCode = " +
	                    dbl.sqlString(this.userCode) + " and FRightType =" + dbl.sqlString(this.type);
	                if(this.rightInd.trim().equalsIgnoreCase("report")){
	                	//------ modify by wangzuochun 2009.11.17 MS00812 当赋予某组合群权限后，系统删除其它组合群权限 QDV4中金2009年11月16日01_B 
	                	//------ 加上组合群代码字段，否则会删除所有组合群下报表的权限
	                    strSql = strSql + " AND FRightInd = " + dbl.sqlString(rightInd) + " and FAssetGroupCode =" + dbl.sqlString(pub.getAssetGroupCode());
	                    //------------------------------------- MS00812------------------------------------//
	                }
	                strSql = strSql +  " and FRightCode not in('userSet','roleSet','rightSet','system')";
	
	                dbl.executeSql(strSql);
	            }
            }
            //+++++++++++++++end++++++++++++++++
            /**shashijie 2012-7-12 STORY 2661 角色权限授予走这里(原流程中增加接口权限的时候会同时删除之前刚新增的报表权限)*/
            else if (this.rightInd.equalsIgnoreCase("Role")){
        	/**end*/
            	strSql = " delete from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) +	                
            		" AND FRightType = " + dbl.sqlString(type) +
	                " and FRightInd = 'Role'";
            	dbl.executeSql(strSql);
            }
            
            //-----------2009.08.18 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A----------------//
            //如果系统设置的主菜单存在且已没有子菜单存在，则删除系统设置
            strSql = "SELECT a.FUserCode FROM TB_Sys_UserRight a" +
                " JOIN TB_FUN_MENUBAR b ON a.FRightCode = b.Fbarcode" +
                " WHERE FUserCode = " + dbl.sqlString(this.userCode) +
                " AND FBarGroupCode = 'system'";
            rsSys = dbl.openResultSet(strSql);
            if(!rsSys.next()){
            	//20120713 added by liubo.Bug #4987
            	//在此处判断系统设置主菜单下是否有子菜单存在时，需要将角色权限表（tb_sys_roleright）中记载的子菜单一并考虑进去
            	//====================================
            	strSql = "SELECT * " +
						 " FROM tb_sys_roleright a " +
						 " JOIN TB_FUN_MENUBAR b ON a.FRightCode = b.Fbarcode " +
						 " WHERE a.frolecode in (select FRightCode from Tb_Sys_UserRight where FRightInd = 'Role' " +
						 " and FUserCode in (select FUserCode from Tb_Sys_UserRight where FUserCode = " + dbl.sqlString(this.userCode) + " and FrightCode = 'system')) " +
						 " AND b.FBarGroupCode = 'system'";
            	rsRoleRight = dbl.queryByPreparedStatement(strSql);
            	if (!rsRoleRight.next())
            	{
	                strSql = "DELETE FROM Tb_Sys_UserRight WHERE FUserCode = " + dbl.sqlString(this.userCode) +
	                    " AND FRightCode = 'system'";
	                dbl.executeSql(strSql);
            	}
            	//===============end=====================
            	dbl.closeResultSetFinal(rsRoleRight);
            }
            dbl.closeResultSetFinal(rsSys);
            //---------------------------------------------------------------------------------------//

            pst = conn.prepareStatement(
                "insert into Tb_Sys_UserRight" +
                "(FUserCode,FRightType,FAssetGroupCode,FRightCode,FPortCode,FRightInd,FOperTypes)" + //增加一个字段FRightType 2009.04.24 wangzuochun  MS00010
                " values(?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            for (int j = 0; j < allReqAry.length; j++) {

                this.protocolParse(allReqAry[j]);

                //MS00010 QDV4赢时胜（上海）2009年02月01日10_A  2009.08.17 蒋锦 修改
                //系统设置的权限也要可以保存
                if (this.operationTypes.length() > 0 ||
                       this.rightInd.equalsIgnoreCase("Role") ||		//Story #1313
                          this.rightInd.equalsIgnoreCase("Report") || this.rightInd.equalsIgnoreCase("DataInterface")) {
                    //System 菜单可能在保存系统权限时已经保存过了，所以要判断一下以免出现主键重复错误
                    if(rightTypeCode.equalsIgnoreCase("system")){
                        String sqlSys = "SELECT FUserCode FROM Tb_Sys_UserRight " +
                            "WHERE FUserCode = " + dbl.sqlString(userCode) +
                            " AND FRightType = " + dbl.sqlString(type) +
                            " AND FAssetGroupCode = " + dbl.sqlString(assetGroupCode) +
                            " AND FRightCode = " + dbl.sqlString(rightTypeCode) +
                            " AND FPortCode = " + dbl.sqlString(portCodes) +
                            " AND FRightInd = " + dbl.sqlString(rightInd);
                        rsSys = dbl.openResultSet(sqlSys);
                        if(rsSys.next()){
                            continue;
                        }
                        dbl.closeResultSetFinal(rsSys);
                    }
                    //by zhouwei 20120129 未被选择的组合群，它的组合群级权限（角色权限）不进行保存
                   if(!mapGroup.isEmpty() && !mapGroup.containsKey(this.assetGroupCode)){
                	   continue;
                   }
                   //----------------end-----------
                    pst.setString(1, this.userCode);
                    pst.setString(2, this.type); //插入权限类型 2009.04.24 wangzuochun  MS00010
                    //---add by songjie 2011.10.14 BUG 2753 QDV4赢时胜(测试)2011年9月14日03_B start---//
					if(this.assetGroupCode.length()> 3){
                    	continue;
                    }
					//---add by songjie 2011.10.14 BUG 2753 QDV4赢时胜(测试)2011年9月14日03_B end---//
                    pst.setString(3, this.assetGroupCode);
                    pst.setString(4, this.rightTypeCode);
                    pst.setString(5, this.portCodes);
                    pst.setString(6, this.rightInd);
                    pst.setString(7, this.operationTypes);
                    pst.addBatch();
                }
                //每1万行提交一次 add by sunkey 20090525 MS00010:QDV4赢时胜（上海）2009年02月01日10_A
                count++;
                if (count == 10000) {
                    pst.executeBatch();
                    pst.clearBatch();
                    count = 0;
                }
            }
            if (count != 0) {
                pst.executeBatch(); //批量执行sql语句 MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-22
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return "true";
        } catch (Exception e) {
            throw new YssException("保存用户权限出错" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rsSys);
        }
    }

    /**
     * 获取用户类型代码
     * add by wangzuochun MS00010 2009.05.18
     * QDV4赢时胜（上海）2009年02月01日10_A
     * @param sAllReq String
     * @return String
     * @throws YssException
     */
    public String getUserTypecode() throws YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        try {
            strSql = "select * from tb_sys_userlist where FUserTypeCode = '1'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                strReturn = "1";
            } else {
                strReturn = "0";
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户类型代码出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 保存用户系统权限
     * @param sAllReq String
     * @return String
     * @throws YssException
     */
    public String saveSysUserRight(String sAllReq) throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String[] aryAssetGroupCode = null;
        String sErrInfo = "";
        String strRightCode = "'system',"; //------ add by wangzuochun 2010.12.11 BUG #598 对用户类型为管理员或高级用户的用户重新赋系统权限时，报错 
        ResultSet rs = null; //------ add by wangzuochun 2010.12.11 BUG #598 对用户类型为管理员或高级用户的用户重新赋系统权限时，报错 
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        PreparedStatement pst = null;
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            String[] allReqAry = sAllReq.split(YssCons.YSS_LINESPLITMARK);
            this.protocolParse(allReqAry[0]); ///?????

            if (pub.getOnlineUser() != null) {
                if (pub.getOnlineUser().get(this.userCode) != null &&
                    !this.userCode.equalsIgnoreCase(pub.getUserCode())) {
                    sErrInfo = "\r\n【" + this.userCode + "】在线，不能修改该用户的权限！";
                    throw new YssException(sErrInfo);
                }
            }
            conn.setAutoCommit(false);
            bTrans = true;

            //分配系统权限时,可以多组合群一起设置,多个组合群之间","间隔
            //系统权限的权限代码以“system_”开头，与一般操作权限分开
            aryAssetGroupCode = this.assetGroupCode.split(",");

            for (int i = 0; i <= aryAssetGroupCode.length - 1; i++) {
            	//------ add by wangzuochun 2010.12.11 BUG #598 对用户类型为管理员或高级用户的用户重新赋系统权限时，报错 
            	strSql = " select * from tb_fun_menubar where frighttype = 'system'";
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()){
            		strRightCode += dbl.sqlString(rs.getString("FBarCode")) + ",";
            	}
            	if (strRightCode.endsWith(",")){
            		strRightCode = strRightCode.substring(0, strRightCode.length()-1);
            	}
            	
            	
                //=====add by xuxuming,20090918.MS00695,权限转换出现问题,QDV4华夏2009年9月11日01_B =====
                strSql = "delete from Tb_Sys_UserRight where FUserCode = '" +
                    this.userCode + "' and (FAssetGroupCode = '" +
                    aryAssetGroupCode[i] + "' or FAssetGroupCode =' "+//此处将以前保存的组合群为空格的记录也一起删除
                    //----- modify by wangzuochun 2010.03.25  MS01027  用户设置修改用户类型保存后报错    QDV4赢时胜(测试)2010年03月16日02_B  
                    "') and FRightCode in(" + strRightCode + ")";//add by xuxuming,20091112.MS00776 增加了密码复杂性系统级权限
                	//----------------------------------------MS01027-----------------------------------------//
                //===========end=====================================================================
                
                //---------- end 2010.12.11 BUG #598 对用户类型为管理员或高级用户的用户重新赋系统权限时，报错 --------------//

                dbl.executeSql(strSql);
				//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                pst = conn.prepareStatement(
				//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                    "insert into Tb_Sys_UserRight" +
                    "(FUserCode,FAssetGroupCode,FRightCode,FOperTypes,FRightInd,FRightType,FPortCode)" + //wangzuochun QDV4赢时胜（上海）2009年02月01日10_A   MS00010
                    " values(?,?,?,?,?,?,?)",
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                for (int j = 0; j <= allReqAry.length - 1; j++) { //保存用户的可用组合，最后一行数据存放可用组合信息
                    if (i > 0 || j > 0) {
                        this.protocolParse(allReqAry[j]);
                    }
                    //BugNo:0000367 edit by jc
                    if (this.rightTypeCode.startsWith("Frm")) {
                        strSql =
                            "delete from Tb_Sys_UserRight where FUserCode = '" +
                            this.userCode + "' and FAssetGroupCode = '" +
                            aryAssetGroupCode[i] + "'" +
                            " and FRightCode = " +
                            dbl.sqlString(this.rightTypeCode);
                        dbl.executeSql(strSql);
                    }
                    //---------------------jc
                    if ( (this.operationTypes.length() > 0 ||
                          this.rightInd.equalsIgnoreCase("Role"))) {
                        pst.setString(1, this.userCode);
                        pst.setString(2, aryAssetGroupCode[i]);
                        pst.setString(3, this.rightTypeCode);
                        pst.setString(4, this.operationTypes);
                        pst.setString(5, this.rightInd);
                        pst.setString(6, "system"); //caocheng QDV4.1赢时胜上海2009年2月1日01_A  MS00001
                        pst.setString(7, " ");
                        pst.executeUpdate();
                    }
                }
                
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                dbl.closeStatementFinal(pst);
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                
                String content = "add,del,edit,brow,execute,audit,clear,revert";
                String sql = "insert into tb_sys_userRight (Fusercode,FAssetGroupCode,FRightCode,FOpertypes,FRightInd,FRightType,FPortCode) values('" +
                    this.userCode + "','" + aryAssetGroupCode[i] +
                    "','system','" +
                    content + "','Right','system',' ')";
                dbl.executeSql(sql);
                //保存用户的可用组合，最后一行数据存放可用组合信息
                this.protocolParse(allReqAry[allReqAry.length - 1]);
                if (!this.portCodes.equalsIgnoreCase("[null]") &&
                    this.portCodes.length() > 0) {
                    this.saveUserPorts(this.portCodes);
                }
                if (!this.reportCodes.equalsIgnoreCase("[null]") &&
                    this.reportCodes.length() > 0) {
                    this.saveUserReports(this.reportCodes);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "true";
        } catch (SQLException e) {
            throw new YssException("保存用户权限出错" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeStatementFinal(pst);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
    }

    /**
     * 保存角色权限
     * @param sAllReq String
     * @throws YssException
     * @return String
     */
    public String saveRoleRight(String sAllReq) throws YssException {
        String[] allReqAry = sAllReq.split(YssCons.YSS_LINESPLITMARK);
        /**shashijie 2012-8-7 BUG 4956 去除重复记录,返回hashmap集合,并循环增加角色权限*/
		doOperionRoleRight(allReqAry);
		/**end*/
		
        //保存角色的可用报表组，最后一行数据存放可用报表组信息
        String strReport = allReqAry[allReqAry.length - 1].split("\r\r")[1];
        if (strReport != null) {            	
            this.saveRoleReports(strReport);
        }
		//---add  by guolongchao 2011.11.28 STORY #1781   删除接口群的角色权限 start---//
        
        //保存角色的可用接口群权限，最后一行数据存放可用接口群信息
        String sDataInterfaces = allReqAry[allReqAry.length - 1].split("\r\r")[2];
        if (sDataInterfaces != null) {            	
        	this.saveRoleDataInterfaces(sDataInterfaces);
        } 
		//---add  by guolongchao 2011.11.28 STORY #1781   删除接口群的角色权限 end---//
        
        
        return "true";
    }

    /**shashijie 2012-8-7 BUG 4956 重构增加角色权限方法*/
	private void doOperionRoleRight(String[] allReqAry) throws YssException{
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
        	conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            //去除重复记录
    		HashMap<String,String> map = getHashMap(allReqAry);
            
    		//先删后增
    		PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Sys_RoleRight" +
                "(FRoleCode,FRightCode,FOperTypes)" +
                " values(?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
    		
    		int i = 0;//计数器
    		Iterator iter = map.entrySet().iterator(); 
    		while (iter.hasNext()) { 
    		    Map.Entry entry = (Map.Entry) iter.next(); 
    		    //Object key = entry.getKey(); 
    		    Object val = entry.getValue(); 
    		    
    		    this.protocolParse(val.toString());
    			
                if (i == 0) {
                	String sStrSql = "delete from Tb_Sys_RoleRight where FRoleCode = "+dbl.sqlString(this.userCode);
	                dbl.executeSql(sStrSql);
                }
                
                if (this.operationTypes.length() > 0) {
                    pst.setString(1, this.userCode);
                    pst.setString(2, this.rightTypeCode);
                    pst.setString(3, this.operationTypes);
                    pst.executeUpdate();
                }
    		    i++;
    		}
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存角色权限出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	/**shashijie 2012-8-7 BUG 4956 去除重复记录,返回hashmap */
	private HashMap<String, String> getHashMap(String[] allReqAry) {
		HashMap<String,String> map = new HashMap<String, String>();
		if (allReqAry==null || allReqAry.length == 0) {
			return map;
		}
		try {
			for (int i = 0; i < allReqAry.length; i++) {
				String reqAry[] = null;
	            reqAry = allReqAry[i].split("\t");
	            
	            //角色代码,权限代码,操作类型
	            String key = reqAry[0]+ ","+ reqAry[2]+ ","+ reqAry[3];
	            map.put(key, allReqAry[i]);
			}
		} catch (Exception e) {
			return new HashMap<String, String>();
		} finally {
			
		}
		return map;
	}

	public String getFirstPara(String request) throws YssException {
        return request.split("/f/f/f", -1)[0];
    }

    public String getSecondPara(String request) throws YssException {
        return request.split("/f/f/f", -1)[1];
    }

    /**
     * 2009.08.06 蒋锦 添加 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
     * 获取角色权限用于前台的初始加载
     * @return String
     * @throws YssException
     */
    public String getUserRoleRight() throws YssException{
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A start---//
        String urOperTypeCode = "";
        String mbOperTypeCode = "";
        //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A end---//
        try{
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 start---//
            boolean judgeCompany = true;
            if(YssCons.companyName.indexOf("赢时胜") != -1){
            	judgeCompany = false;
            }
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 end---//
        	
            //通过角色权限表匹配菜单条和用户权限表中的角色类型权限获取角色权限
        	//edit by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A 菜单条设置中没有的权限类型应在权限设置界面显示为灰色
            strSql = "select a.FRIGHTCODE, a.FOPERTYPES, c.FAssetGroupCode, c.FPortCode, b.fopertypecode" +
                " from Tb_Sys_Roleright a" +
                //--- edit by songjie 2013.01.24 菜单公司名称字段 不等于 fundacc.lic 文件中的公司名称 则不显示在权限界面 start---//
                " join (select * from Tb_Fun_Menubar " + 
                (judgeCompany ?
                " where FCompanyName is null or (FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
                ") b on a.Frightcode = b.FBarCode" +
                //--- edit by songjie 2013.01.24 菜单公司名称字段 不等于 fundacc.lic 文件中的公司名称 则不显示在权限界面 end---//
                " JOIN (SELECT *" +
                " FROM TB_Sys_UserRight" +
                " WHERE FRightInd = 'Role') c ON a.FRoleCode = c.FRightCode" +
                " WHERE c.FRightType = " + dbl.sqlString(this.type) +
                " AND c.FUserCode = " + dbl.sqlString(this.userCode);
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
                this.rightTypeCode = rs.getString("FRIGHTCODE");
                //---edit by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A start---//
                if("logview".equals(this.rightTypeCode)){
                	urOperTypeCode = rs.getString("FOperTypes");
                	mbOperTypeCode = rs.getString("FOperTypeCode");
                	this.operationTypes = reSetOperType(urOperTypeCode,mbOperTypeCode);
                }else{
                	this.operationTypes = rs.getString("FOperTypes");
                }
                //---edit by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A end---//
                this.assetGroupCode = rs.getString("FAssetGroupCode");
                this.portCodes = rs.getString("FPortCode");
                buf.append(buildRespStr());
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
        }catch(Exception ex){
            throw new YssException("获取用户角色权限出错！", ex);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        return sResult;
    }

    /**
     * 通过角色获取用户权限
     * @throws YssException
     * @return String
     */
    public String getByRoleRigth(String request) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select FRIGHTCODE,FOPERTYPES from Tb_Sys_Roleright" +
                " a join (select * from Tb_Fun_Menubar) b on a.Frightcode = b.FBarCode" +
                " where  " +
                getWhereSt(request);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.rightTypeCode = rs.getString("FRIGHTCODE");
                this.operationTypes = rs.getString("FOPERTYPES");
                buf.append(buildRespStr());
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e); //采用标准异常处理方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 2009-05-04 蒋锦修改 调整 Where 语句的拼装方式 MS00010 《QDV4赢时胜（上海）2009年02月01日10_A》
     * @param request String：角色代码 SQLIN 的形式
     * @return String：WHERE 语句
     * @throws YssException
     */
    private String getWhereSt(String request) throws YssException {
        //去掉最后的逗号
        if (request.endsWith(",")) {
            request = request.substring(0, request.length() - 1);
        }
        String sqlcode = "FRoleCode IN (" + operSql.sqlCodes(request) + ")" +
            " AND FRightType LIKE '%" + this.type + "%'";

        return sqlcode;
    }

    /**
     * 获取用户所有权限
     * @throws YssException
     * @return String
     */
    public String getUserAllRight() throws YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;

        StringBuffer buf = new StringBuffer();
        try {

            strSql = "select FRightCode,FOperTypes from Tb_Sys_Userright" +
                " a join (select * from Tb_Fun_MenuBar) b on a.frightcode = b.FbarCode " +
                " where a.FAssetGroupCode = '" + this.assetGroupCode +
                "' and a.FUserCode = '" + this.userCode +
                "' and a.FRightInd = 'Right'" +
                " union select FRightCode,FOperTypes from Tb_Sys_Roleright " +
                "  a join (select * from Tb_Fun_MenuBar) b on a.frightcode = b.FbarCode " +
                " where a.FRoleCode in (select FRightCode from Tb_Sys_Userright where FAssetGroupCode = '" +
                this.assetGroupCode + "' and FUserCode = '" + this.userCode +
                "' and FRightInd = 'Role')" +
                " and a.FRightCode not in (select FRightCode from Tb_Sys_Userright where FAssetGroupCode = '" +
                this.assetGroupCode + "' and FUserCode = '" + this.userCode +
                "' and FRightInd = 'Right')";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.rightTypeCode = rs.getString("FRightCode");
                this.operationTypes = rs.getString("FOperTypes");
                buf.append(buildRespStr());

            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }

            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取用户权限
     * @throws YssException
     * @return String
     * add by caocheng MS00001 QDV4.1赢时胜上海2009年2月1日01_A
     */
    public String getUserRight() throws
        YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;

        StringBuffer buf = new StringBuffer();
        
        //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A start---//
        String urOperTypeCode = "";
        String mbOperTypeCode = "";
        //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A end---//
        try {
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 start---//
            boolean judgeCompany = true;
            if(YssCons.companyName.indexOf("赢时胜") != -1){
            	judgeCompany = false;
            }
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 end---//
        	
        	//edit by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A 菜单条设置中没有的权限类型应在权限设置界面显示为灰色
            strSql = "select a.FRightCode, a.FOperTypes, a.FAssetGroupCode, a.FPortCode, b.FOperTypeCode" +
                " from (select * from Tb_Sys_Userright where FRightType = '" + this.filterType.type + "')" +
                //--- edit by songjie 2013.01.24 排除不关联公司名称的菜单 start ---//
                " a join (select * from Tb_Fun_MenuBar " + 
                (judgeCompany ?
                " where FCompanyName is null or (FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
                ") b on a.frightcode = b.FbarCode" +
                //--- edit by songjie 2013.01.24 排除不关联公司名称的菜单 end ---//
                "  where a.FUserCode = '" + this.userCode +
                "' and a.FRightInd = 'Right'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.rightTypeCode = rs.getString("FRightCode");
                this.operationTypes = rs.getString("FOperTypes");
                //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A start---//
                if("logview".equals(this.rightTypeCode)){
                	urOperTypeCode = rs.getString("FOperTypes");
                	mbOperTypeCode = rs.getString("FOperTypeCode");
                	this.operationTypes = reSetOperType(urOperTypeCode,mbOperTypeCode);
                }else{
                	this.operationTypes = rs.getString("FOperTypes");
                }
                //---add by songjie 2011.12.01 需求 1971 QDV4南方2011年11月30日01_A end---//
                this.portCodes = rs.getString("FPortCode");
                this.assetGroupCode = rs.getString("FAssetGroupCode");
                buf.append(buildRespStr());

            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }

            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * edit by songjie 2011.12.01 
     * 需求 1971 QDV4南方2011年11月30日01_A
     * 排除用户或角色权限中有的 但是 菜单条设置中没有的操作类型
     * @param urOperTypeCode
     * @param mbOperTypeCode
     * @return
     * @throws YssException
     */
    private String reSetOperType(String urOperTypeCode, String mbOperTypeCode) throws YssException{
    	String[] mbOperTypeCodes = null;
    	ArrayList almbOperType = new ArrayList();
    	String[] urOperTypeCodes = urOperTypeCode.split(",");
    	String finalOper = "";
    	
    	if(!"null".equals(mbOperTypeCode)){
    		if(mbOperTypeCode.indexOf(",") != -1){
    			mbOperTypeCodes = mbOperTypeCode.split(",");
    			for(int i = 0; i < mbOperTypeCodes.length; i++){
    				almbOperType.add(mbOperTypeCodes[i]);
    			}
    			
    			for(int i = 0; i < urOperTypeCodes.length; i++){
    				if(almbOperType.contains(urOperTypeCodes[i])){
    					finalOper += urOperTypeCodes[i] + ",";
    				}
    			}
    			
    			if(finalOper.length() > 1){
    				finalOper = finalOper.substring(0, finalOper.length() - 1);
    			}
    			
    			return finalOper;
    			
    		}else{
    			if(urOperTypeCode.indexOf(mbOperTypeCode) != -1){
    				return mbOperTypeCode;
    			}else{
    				return urOperTypeCode;
    			}
    		}
    		
    	}else{
    		return urOperTypeCode;
    	}
    }
    
    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090425
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 把组合群公用窗体的权限加下组合窗体的权限传到前台还增加了角色权限
    /// </summary>
    public String getWindowRight() throws YssException, YssException {
        String strReturn = getPubRight();   //得到组合群和公用窗体的权限和角色权限的一个并集
        String sPortRight = getPortRight(); //得到组合窗体的权限的一个并集并上角色权限
        //这是为防止没有查出数据前台报数组越界错误
        if (sPortRight.length() > 0) {
            strReturn = strReturn + "\f\f" + sPortRight; //把此权限组合起来传到前台
        }
        return strReturn;
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090425
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 增加组合群,系统的和公用窗体的权限并上角色权限加载方法
    /// </summary>
    public String getPubRight() throws YssException, YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            HashMap htRole = new HashMap(); //放到此处的目的是为得到权限的一个并集
            //2009.08.06 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
            //角色权限仅查询属于公共级别和组合群级别的权限
            strSql = " select distinct a.FRightCode,a.FOperTypes from Tb_Sys_Roleright a " + //查出色的所有权限
                //" a join (select * from Tb_Sys_RightType) b on a.frightcode = b.fmenubarcode " + //MODIFIED BY YESHENGHONG TO REMOVE RIGHT TYPES 20130114 story2917
                " JOIN (select FRightCode, FRightType" +
                //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
                //获取权限时需要包含继承到的权限，包括用户权限和角色权限
                //==================================
                " from Tb_Sys_Userright" +
                " where (FUserCode = " + dbl.sqlString(this.userCode) + getInheritedRights(pub.getAssetGroupCode(), "") + ")" +
                //===============end===================
                " AND FRightInd = 'Role'" +
                " AND (FRightType LIKE '%public%' OR" +
                " (FRightType LIKE '%group%' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                " ))) f ON f.FRightcode = a.FRoleCode" +
                " JOIN (SELECT FBarCode, FRightType" +
                " FROM TB_FUN_MENUBAR" +
                " WHERE FRightType LIKE '%public%'" +
                " OR FRightType LIKE '%group%') e ON a.frightcode = e.FBarCode" +
                " AND " + dbl.sqlInstr("e.FRightType", "f.FRightType") + " > 0";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //----MS00578 QDV4赢时胜（上海）2009年7月24日02_B sj modified ---------------------
                RightBean right = new RightBean();//生成新的权限类型
                right.rightTypeCode = rs.getString("FRightCode");   //获取权限代码
                right.operationTypes = rs.getString("FOperTypes");  //获取操作类型
//                right.windowName = rs.getString("FRightTypeCode");  //获取窗体名称
                //add by wangzuochun 2009.11.12 MS00798 拥有相关权限的用户实际操作时，无法审核关联机构设置 QDV4海富通2009年11月5日01_B 
                //判断hashmap中是否已经存在key，如果存在，则更新对象中的权限
                
                if(htRole.containsKey(right.rightTypeCode)){
                	RightBean htRight = (RightBean)htRole.get(right.rightTypeCode);
                	//modifie by yeshenghong, story2917 20130114
                	String sRightTypeCode = htRight.operationTypes; //得到对象里的权限
            	/**Start 20130626 modified by liubo.Bug #8444.QDV4赢时胜(上海开发)2013年6月26日04_B
                 *  在符合以下条件的情况下，某个用户的角色权限中，审核权限将不会被传给前台：
                 *  有两个角色A、B，其中角色A设置了某个窗体的自审权限（auditOwn），没有设置审核权限（audit）。角色B设置了审核权限
                 *  在这种情况下，有可能在查询角色权限表的时候，首先取到了A的角色权限，然后保存到权限集合（htRole）中
                 *  B可能会在之后被查到，因为htRole中已有KEY值为该窗体的记录，所以会取出该窗体的RightBean对象中的权限，与B进行比对
                 *  这样会出现一个问题，比对方式是用A的"auditOwn".indexOf("audit")，最后结果始终大于0
                 *  即系统会认为角色权限中已包含了audit权限（实际上有的值是A的auditOwn权限）。如此B权限中的audit权限就不会被传回前台
                 *  要避免这种问题，可以将A的权限逐条拆分，然后全部装进arrayList对象
                 *  逐条以arrayList对象的contains方法来比对B权限拆分后的字符串数组，就可以正确得到A、B两个角色权限的并集*/
                    String[] sRightList = right.operationTypes.split(",");	//拆分角色权限
                    
                    //将拆分后的角色A权限逐条塞入arrayList对象
                    //=============================
                    ArrayList<String> assOper = new ArrayList<String>();
                    
                    for (int i = 0; i < sRightList.length; i++)
                    {
                    	assOper.add(sRightList[i]);
                    }
                    //============end=================
                	//------ add by wangzuochun 2009.11.18 MS00744 拥有多个组合权限的用户登陆系统报错 QDV4华夏2009年9月29日01_B 
                    if (sRightTypeCode == null){
                    	continue;
                    }
                    //---------------- MS00744 ---------------//
                    String[] arrCode = sRightTypeCode.split(",");       //解析出当前对象的权限
                    for (int i = 0; i < arrCode.length; i++) {          //循环当前对象的权限
                    	
                    	//8444之前有问题的处理方法
                    	//=====================================
//                        if (right.operationTypes.indexOf(arrCode[i]) == -1) { //看hashmap里面是否有当前对象的权限
//                            right.operationTypes += ("," + arrCode[i]); //没就加入
//                        }
                    	//=================end====================
                    	
                    	//将角色A的权限，逐条用arrayList.contains方法来比对角色B的权限
                        //==============================
                        	if (!assOper.contains(arrCode[i]))
                        	{
                        		right.operationTypes += ("," + arrCode[i]);
                        	}
                        }
                        //=============end=================
                /**End 20130626 modified by liubo.Bug #8444.QDV4赢时胜(上海开发)2013年6月26日04_B*/
                    }
                //------------------------------MS00798---------------------------------//
                htRole.put(right.rightTypeCode, right); //把角色的所有权限放到一个hashtable里面去
                //------------------------------------------------------------------------------
            }
            dbl.closeResultSetFinal(rs);
            
            //---add by songjie 2012.05.17 BUG 4589 QDV4赢时胜（深圳）2012年5月17日01_B start---//
            // -- start by dongqingsong 2013-10-08 新禅道STORY #12967::【需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130829001】组合设置权限要求明细到组合
            strSql = " select * from Tb_Fun_MenuBar where FbarCode like 'portfolio%' and FRightType <> 'system' ";
            // -- end by dongqingsong 2013-10-08 新禅道STORY #12967::【需求深圳-(深圳赢时胜)QDII估值系统V4.0(高)20130829001】组合设置权限要求明细到组合

            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	dbl.executeSql(" delete from Tb_Sys_Userright where FUserCode = " + 
            	dbl.sqlString(this.userCode) + " and FRightCode = 'portfolio' and FRightType = 'system'");
            }
            dbl.closeResultSetFinal(rs);
            rs = null;
            //---add by songjie 2012.05.17 BUG 4589 QDV4赢时胜（深圳）2012年5月17日01_B end---//
        
            //2009-06-08 蒋锦修改 其中组合群权限只取当前组合群的
            //添加and a.fopertypes is not null 避免查询出不合法数据 sunkey@Modify 20090924 MS00715:QDV4赢时胜（上海）2009年9月25日01_B
            strSql = " select distinct a.FRightCode,a.FOperTypes,  a.fportcode, b.frighttype from " +
            		 " (select * from Tb_Sys_Userright where fopertypes is not null) a " +
                "join (select * from Tb_Fun_MenuBar) b on a.frightcode = b.FbarCode " +
                //"left join Tb_Sys_RightType e on a.FRightCode = e.FMenuBarCode " + //MODIFIED BY YESHENGHONG TO REMOVE RIGHT TYPES 20130114 story2917
                " where ((a.frighttype like '%public%' or  a.frighttype like '%system%' or (a.frighttype like '%group%' AND a.FAssetGroupCode = " +
                dbl.sqlString(pub.getAssetGroupCode()) + ")) " + //public公共的，system系统的，group组合群的
                //20120925 modified by liubo.Story #2737.获取符合条件的权限继承数据，拼接到SQL中获取继承的权限
                //===================================================
                "and a.FUserCode = '" + this.userCode + "'  and a.fopertypes is not null)" + getInheritedRights(pub.getAssetGroupCode(), "public","a","login");
            //======================end========================
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //如果权限类型代码为null，则不必处理此条记录了，因为哈希表中存放的是权限类型代码 sunkey@Modify 20090910
                this.rightTypeCode = rs.getString("FRightCode"); //获取权限类型代码
                if(this.rightTypeCode == null){
                    continue;
                }
                if (htRole.containsKey(this.rightTypeCode)) { //判断hashtable里面是否含有当前对象
                    RightBean right = (RightBean) htRole.get(this.rightTypeCode); //得到hashtble里的对象
                    
                    /**Start 20130626 modified by liubo.Bug #8444.QDV4赢时胜(上海开发)2013年6月26日04_B
                     *  在符合以下条件的情况下，某个用户的用户权限中，审核权限将不会被传给前台：
                     *  有一个角色，该角色有A窗体的了自审（auditOwn）权限，没有A窗体的审核（audit）权限
                     *  某个用户有设置A窗体的审核的用户权限
                     *  在这种情况下，角色权限中的自审权限将被先取到，并被保存进权限集合（htRole对象）中
                     *  在这里取用户权限时，会逐条与角色权限进行比对（用角色权限的字符串来indexOf）
                     *  然后会出现一个问题，角色权限中的"auditOwn".indexOf("audit")，始终会大于0，
                     *  即系统会认为角色权限中已包含了audit权限（实际上有的值是auditOwn权限）。如此用户权限去中的audit权限就不会被传回前台
                     *  要避免这种问题，可以将角色权限逐条拆分，然后全部装进arrayList对象
                     *  逐条以arrayList对象的contains方法来比对用户权限拆分后的字符串数组，就可以正确得到用户权限和角色权限的并集*/
                    String[] sRightList = right.operationTypes.split(",");	//拆分角色权限
                    
                    //将拆分后的角色权限逐条塞入arrayList对象
                    //=============================
                    ArrayList<String> assOper = new ArrayList<String>();
                    
                    for (int i = 0; i < sRightList.length; i++)
                    {
                    	assOper.add(sRightList[i]);
                    }
                    //============end=================
                    
                    String sRightTypeCode = rs.getString("fopertypes"); //得到用户权限
                    String[] arrCode = sRightTypeCode.split(",");       //将用户权限拆分
                    for (int i = 0; i < arrCode.length; i++) {          //循环当前用户的权限
                    	
                    //8444之前有问题的处理方法
                    //=============================
//                        if (right.operationTypes.indexOf(arrCode[i]) == -1) { //看hashtable里面是否有当前对象的权限
//                            right.operationTypes += ("," + arrCode[i]); //没就加入
//                        }
                    //=============end================
                    
                    //将角色权限，逐条用arrayList.contains方法来比对用户权限
                    //==============================
                    	if (!assOper.contains(arrCode[i]))
                    	{
                    		right.operationTypes += ("," + arrCode[i]);
                    	}
                    }
                    //=============end=================
                    /**End 20130626 modified by liubo.Bug #8444.QDV4赢时胜(上海开发)2013年6月26日04_B*/
                    
                } else {
                    RightBean right = new RightBean(pub);   //如果hashtable里面没有当前对象就new一个
                    right.rightTypeCode = rs.getString("FRightCode");   //获取权限代码
                    right.operationTypes = rs.getString("FOperTypes");  //获取操作类型
//                    right.windowName = rs.getString("FRightTypeCode");  //获取窗体名称
                    right.type = rs.getString("FRightType");//获取权限类型
//                    htRole.put(right.windowName, right);    //把当前对象加入到hatable里面去
                    htRole.put(right.rightTypeCode, right);
                  //modifie by yeshenghong, story2917 20130114
                }
            }
            Iterator iterator = htRole.values().iterator(); //循环取出当前对象
            while (iterator.hasNext()) {
                RightBean right = (RightBean) iterator.next();
                buf.append(right.buildRespStr()); //组合当前对象的内容
            }
            if (buf.toString().length() > 2) { //把窗体的所有组合权限去
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return strReturn; //把当前对象的内容传到前台
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090425
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 组合窗体的权限并上角色权限的方法
    /// </summary>
    public String getPortRight() throws YssException, YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        ResultSet res = null;
        StringBuffer bufPort = new StringBuffer();
        try {
            //获取组合所有窗体
//            strSql = "select b.frighttypecode, b.fmenubarcode, g.frighttype from Tb_Sys_RightType b " +
//                " left join Tb_Fun_MenuBar g on b.fmenubarcode = g.fbarcode " +
//                " where g.frighttype like '%" + YssCons.YSS_SYS_RIGHTTYPE_PORT + "%'";
            strSql = " select fbarcode, frighttype from Tb_Fun_MenuBar where frighttype = 'port' ";
            //modifie by yeshenghong, story2917 20130114
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
//                this.windowName = rs.getString("FRightTypeCode");   //获取窗体名称
                this.rightTypeCode = rs.getString("fbarcode");  //获取权限代码
              //modifie by yeshenghong, story2917 20130114
                this.type = rs.getString("frighttype"); //获取权限类型
                HashMap htOperTypes = new HashMap();    //放到此处的目的是为得到当前窗体的组合权限的一个并集
                this.operationTypes = ""; //清空组合代码权限操作类型
                //通过窗体得到此组合窗体的所有组合的权限
                //2009.08.06 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
                //角色权限仅查询属于组合的权限
                strSql = " select a.FOperTypes " +
                    "from Tb_Sys_UserRight a " +
//                    "left join Tb_Sys_RightType b on a.frightcode = b.fmenubarcode " +
                    " where a.FRightCode = '" + this.rightTypeCode + "' " +
                  //20120925 modified by liubo.Story #2737.获取符合条件的权限继承数据，拼接到SQL中获取继承的权限
                    //************************************************
                    " and ((a.frighttype LIKE '%" +
                    YssCons.YSS_SYS_RIGHTTYPE_PORT + "%' " +
                    "and a.FUserCode = '" +
                    this.userCode + "' " +
                    //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  增加组合群代码作为条件筛选结果
				//20120925 modified by liubo.Story #2737.获取符合条件的权限继承数据，拼接到SQL中获取继承的权限
                //===================================================
                    " and a.fassetgroupcode='"+pub.getAssetGroupCode()+"') " +getInheritedRights(pub.getAssetGroupCode(), "port","a","login")  + " )"+
                //===================end================================

                    //=====================================================================================================================================

                    //**********************end**************************
                    "UNION " +
                    "SELECT b.Fopertypes " +
                    //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
                    //获取权限时需要包含继承到的权限，包括用户权限和角色权限
                    //==================================
                    "FROM ( select * from Tb_Sys_UserRight  WHERE (FUserCode = '" + this.userCode + "' " + getInheritedRights(pub.getAssetGroupCode(), "") + ") " +
                    //============end======================
                    " AND frightind = 'Role' AND FRightType LIKE '%port%' AND FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) + ") a " +
                    "LEFT JOIN Tb_Sys_Roleright b ON a.frightcode = b.frolecode " +
//                    "LEFT JOIN Tb_Sys_RightType c on b.frightcode = c.fmenubarcode " +
                  //modifie by yeshenghong, story2917 20130114
                    " WHERE " +
//                    " c.FRightTypeCode = '" + this.windowName + "'"+
                    " b.FRightCode = " + dbl.sqlString(this.rightTypeCode) + //modified by  yeshenghong bug7042
                    //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  增加组合群代码作为条件筛选结果
//                    " and " +
                    " and a.fassetgroupcode='"+pub.getAssetGroupCode()+"'";
                    //====================================================================================================================================
                res = dbl.openResultSet(strSql);
                while (res.next()) { //循环合并这些组合权限
                    String sPorts = res.getString("FOperTypes"); //获取组合代码操作类型
                    if (sPorts != null && sPorts.length() > 0) {
                        String[] sPort = sPorts.split(",");
                        for (int i = 0; i < sPort.length; i++) {
                            htOperTypes.put(sPort[i], sPort[i]);
                        }
                    }
                }
                dbl.closeResultSetFinal(res);
                Iterator iterator = htOperTypes.values().iterator();
                while (iterator.hasNext()) { //得到合并后的所有组合的权限的一个集合
                    String str = (String) iterator.next();
                    this.operationTypes = this.operationTypes + str + ",";
                }
                if (this.operationTypes.length() > 1) { //去除后面多出的一个，号
                    this.operationTypes = this.operationTypes.substring(0,
                        this.operationTypes.length() - 1);
                }
                bufPort.append(buildRespStr());
            }
            if (bufPort.toString().length() > 2) { //去除后面多出的解析字符
                strReturn = bufPort.toString().substring(0, bufPort.toString().length() - 2);
            }
            return strReturn; //把组合权限加上角色权限一起发到前台
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090525
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 有些窗体不要判断组合级别的权限。把所有组合级别的权限的窗体名称返回到前台进行判断
    /// @return String 组合级别的权限的窗体名称
    /// </summary>
    public String getAllPortRightFrmCode() throws YssException {
        String sResult = "";    //字符串用来存放查出来的窗体的名称
        String sqlStr = "";     //sql查询语句
        ResultSet rs = null;    //存放SQL语句查出来的结果集
        try {
//            sqlStr = "SELECT b.Frighttypecode" +
//                " FROM (SELECT *" +
//                " FROM TB_FUN_MENUBAR" +
//                " WHERE FRightTYpe like '%port%'" +
//                //+ dbl.sqlInstr("FRightTYpe", "'port'") + " > 0" +
//                " AND FEnabled = 1) a"
//                " LEFT JOIN (SELECT * FROM Tb_Sys_Righttype) b ON a.FBarCode = b.Fmenubarcode" +
                //" WHERE FRightTypeCode IS NOT null"; 
                //通过窗体菜单条代码查出所有组合级别的窗体的名称
            sqlStr = " select FBarCode from TB_FUN_MENUBAR WHERE FRightTYpe like '%port%' AND FEnabled = 1 ";
            //modifie by yeshenghong, story2917 20130114
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) { //循环结果集
                sResult += (rs.getString("FBarCode") + ",");      //把所有窗体名称组合起来用，号分开
            }
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);   //去除尾部多出来的，号
            }
        } catch (Exception ex) {
            throw new YssException("获取所有拥有组合权限的窗体代码出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090425
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    /// 返回窗体里选择条里面的权限并上角色权限返回到前台进行判断
    /// @return String 返回窗体的可用组合权限并上角色权限
    /// </summary>
    public String getFromPort() throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet res = null;
        ResultSet rs = null;
        StringBuffer bufPort = new StringBuffer();
        HashMap hmPortRight = new HashMap(); //定义一个hashtable用来排重复的权限和增加没有有权限
        try {
            strSql = 
                "SELECT FAssetGroupCode FROM Tb_Sys_Assetgroup WHERE FLocked = 0"; //查出当前的组合群代码
            res = dbl.openResultSet(strSql);
            while (res.next()) {
                String sGroupCode = res.getString("FAssetGroupCode"); //得到组合群代码
                if (!dbl.yssTableExist("Tb_" + sGroupCode + "_Para_Portfolio")) { //判断是否有这个组合群的表没有下面就不用做了
                    continue;
                }
                //获取组合所有窗体
                //2009.08.06 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
                //角色权限仅查询属于组合的权限
                strSql =
                    " SELECT menu.fbarcode, rig.fopertypes, rig.fportcode,  rig.fassetgroupcode" +
                    " FROM (select fbarcode, frighttype" +
                    //" from Tb_Sys_RightType b left join " +
                    //modifie by yeshenghong, story2917 20130114
                    " from Tb_Fun_MenuBar g " +
                    //" on b.fmenubarcode = g.fbarcode" +
                    " where g.frighttype like '%port%') menu" +
                    " JOIN (SELECT *" +
                    " FROM Tb_Sys_Userright" +
                    
                    //20120807 modified by liubo.Story #2737
                    //以系统当前日期、当前登录用户、当前组合群为条件，查询是否有需要继承的委托人的权限
                    //====================================
//                    " WHERE FUserCode = " + dbl.sqlString(pub.getUserCode()) +                   
//                    " and FRightType='port') rig ON menu.FMenubarCode = rig.FRightCode" + //添加frighttype=‘port’作为筛选条件，从而匹配菜单条中的数据 sunkey@Modify 20090924 MS00715:QDV4赢时胜（上海）2009年9月25日01_B
                    " WHERE (FUserCode = " + dbl.sqlString(pub.getUserCode()) +                   
                    " and FRightType='port')" + getInheritedRights(sGroupCode,"port","","login") + ") rig ON menu.FbarCode = rig.FRightCode" +
                    //================end====================
                    
                    " UNION " +
                    " SELECT m.fbarcode, ur.fopertypes, port.FPortCode, port.fassetgroupcode" +
                    " FROM Tb_Sys_Userright a" +
                    " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode" +
                    " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode" +
//                    " LEFT JOIN Tb_Sys_RightType rt ON m.fbarcode = rt.fmenubarcode" +
                    // modifie by yeshenghong, story2917 20130114
                    " LEFT JOIN Tb_" + sGroupCode + "_Para_Portfolio " +
                    " port ON a.fportcode = port.fportcode " + //modify by wangzuochun 2011.03.19 BUG #1508 需要完善会计分开做账权限的控制 
                    " WHERE (a.FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                    " AND a.FRightType = 'port'" +
                    " AND a.FAssetGroupCode = " + dbl.sqlString(sGroupCode) +
                    " AND a.Frightind = 'Role'" +
                    " AND m.frighttype LIKE '%port%'" +
                    " AND port.FCheckState = 1)"//窗体的组合权限加角色权限
                  //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                    //并上权限继承中委托人的角色权限
                    //-----------------------------------
                    + getInheritedRoleRights(pub.getAssetGroupCode(),"port","a","");    
                //----------------end-------------------
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    String sPortCode = rs.getString("fportcode");       //查出它的组合代码
                    String sAssetCode = rs.getString("fassetgroupcode");//查出它的组合群代码

                    //因为权限是以组合群代码+组合代码+窗体名称作为主键，因此将他们设置为哈希表的Key
                    //如果能根据这个主键查询出数据，并上新查询出的权限数据 sunkey@Modify 2009014
                    if (hmPortRight.containsKey(sAssetCode + sPortCode + rs.getString("FBarcode"))) {
                    	//modifie by yeshenghong, story2917 20130114
                        //从hashMap里取出权限
                        RightBean rightTmp = (RightBean) hmPortRight.get(sAssetCode + sPortCode + rs.getString("FBarCode"));
                        String sRightTypeCode = rs.getString("fopertypes"); //从数据库里查出权限
                        //------ add by wangzuochun 2009.11.18 MS00744 拥有多个组合权限的用户登陆系统报错 QDV4华夏2009年9月29日01_B 
                        if (sRightTypeCode == null){
                        	continue;
                        }
                        //---------------- MS00744 ---------------//
                        String[] arrCode = sRightTypeCode.split(",");       //解析出所有的权限
                        for (int i = 0; i < arrCode.length; i++) {
                        	//edit by songjie 2011.10.20 BUG 2969 QDV4赢时胜(上海)2011年10月17日01_B
                            if (rightTmp.operationTypes != null && rightTmp.operationTypes.indexOf(arrCode[i]) == -1) {    //如果hashMap里面没有查出的权限那么就加入到hashMap里面去
                                rightTmp.operationTypes += ("," + arrCode[i]);          //加入权限
                            }
                        }
                    } else {
                        RightBean rightTmp = new RightBean(pub);                //如果hashmap里没有权限，那么就新建一个
                        rightTmp.rightTypeCode = rs.getString("FBarCode");   //给新建的权限赋值
//                        rightTmp.rightTypeCode = rs.getString("fmenubarcode");  //给新建的权限赋值
                        rightTmp.assetGroupCode = rs.getString("fassetgroupcode"); //给新建的权限赋值
                        rightTmp.operationTypes = rs.getString("fopertypes");   //给新建的权限赋值
                        rightTmp.portCodes = sPortCode; //给新建的权限赋值
                        hmPortRight.put(rightTmp.assetGroupCode + rightTmp.portCodes +
                                        rightTmp.rightTypeCode, rightTmp); //把新建的权限放到hashMap里面
                      //modifie by yeshenghong, story2917 20130114
                    }
                }
                dbl.closeResultSetFinal(rs); //关闭资源
            }
            Iterator iterator = hmPortRight.values().iterator(); //循环遍历hashMap里的内容
            while (iterator.hasNext()) {
                RightBean right = (RightBean) iterator.next(); //循环把hashMap里的内容取出来
                bufPort.append(right.buildRespStr()); //把取出来的内容组合起来
            }
            if (bufPort.toString().length() > 2) { //把组合起来的权限去除多余的解析符/f/f
                strReturn = bufPort.toString().substring(0, bufPort.toString().length() - 2);
            }
        } catch (Exception e) {
            throw new YssException("获取用户组合级别权限出错", e);
        } finally {
            dbl.closeResultSetFinal(res, rs); //关闭资源
        }
        return strReturn;
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改时间:20090425
    /// BugNO  : MS00010  QDV4.1  系统权限明细到单个组合 QDV4赢时胜（上海）2009年02月01日10_A
    ///
    /// modify by wangzuochun 2009.12.1 MS00805 QDII估值系统行情资料权限控制不完善  QDV4华夏2009年11月05日01_B
    /// @return String 返回组合群权限并上角色权限到前台进行判断
    /// </summary>
    public String getGroupRight() throws YssException, YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        StringBuffer bufGroup = new StringBuffer();
        HashMap hmPortRight = new HashMap(); //定义一个hashtable用来排重复的权限和增加没有有权限
        try {
            //2009.08.06 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
            //角色权限仅查询属于当前组合群的权限
            strSql = "SELECT menu.fbarcode, rig.fopertypes, rig.fassetgroupcode " +
                " FROM (select g.fbarcode, g.frighttype" +
                //" from Tb_Sys_RightType b left join " +
                " from Tb_Fun_MenuBar g " +
                //" on b.fmenubarcode = g.fbarcode" +
                //modifie by yeshenghong, story2917 20130114
                " where g.frighttype like '%group%') menu" +
                " JOIN (SELECT *" +
                " FROM Tb_Sys_Userright" +
                " WHERE (FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                " AND fassetgroupcode = " + dbl.sqlString(pub.getAssetGroupCode()) +
				
				//20120925 modified by liubo.Story #2737.获取符合条件的权限继承数据，拼接到SQL中获取继承的权限
                //===================================================
                " AND frighttype='group') " + getInheritedRights(pub.getAssetGroupCode(),"group","","login") + ") rig ON menu.FbarCode = rig.FRightCode" + //添加frighttype='group'作为条件，和菜单条里的group匹配，避免查询出多余数据 sunkey@Modify 20090924 MS00715:QDV4赢时胜（上海）2009年9月25日01_B
                //==========================end=========================

                //=========================================================================================================
                " UNION" +
                " SELECT m.fbarcode, ur.fopertypes, asset.fassetgroupcode " +
                " FROM Tb_Sys_Userright a" +
                " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode" +
                " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode" +
//                " JOIN Tb_Sys_RightType rt ON m.fbarcode = rt.fmenubarcode" +
                " LEFT JOIN Tb_Sys_Assetgroup asset ON 1=1" +
                " WHERE (a.FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                " AND a.FRightType = 'group'" +
                " AND a.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
                " AND a.Frightind = 'Role'" +
                " AND m.frighttype LIKE '%group%'" +
                " AND asset.FLocked = 1" + //组合群权限并上角色权限
                //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载 增加组合群代条件
                " and asset.fassetgroupcode='" + pub.getAssetGroupCode() + "')"
              //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                //并上权限继承中委托人的角色权限
                //-----------------------------------
                + getInheritedRoleRights(pub.getAssetGroupCode(),"group","a",""); 

            //----------------end-------------------
            
                //==============================================================================
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                String sAssetCode = rs.getString("fassetgroupcode"); //查出它的组合群代码
                if (hmPortRight.containsKey(sAssetCode + rs.getString("FBarCode"))) {
                //if (hmPortRight.get(sAssetCode) != null) { //用它的组合群代码作为hashMap的key看是否有权限对象
                    RightBean right = (RightBean) hmPortRight.get(sAssetCode + rs.getString("FBarCode")); //从hashMap里取出权限
                  //modifie by yeshenghong, story2917 20130114
                    String sRightTypeCode = rs.getString("fopertypes"); //从数据库里查出权限
                    //------ add by wangzuochun 2009.11.18 MS00744 拥有多个组合权限的用户登陆系统报错 QDV4华夏2009年9月29日01_B 
                    if (sRightTypeCode == null){
                    	continue;
                    }
                    //---------------- MS00744 ---------------//
                    String[] arrCode = sRightTypeCode.split(","); //解析出查出的所有的权限
                    for (int i = 0; i < arrCode.length; i++) {
                        if (right.operationTypes.indexOf(arrCode[i]) == -1) { //如果hashMap里面没有查出的权限那么就加入到hashMap里面去
                            right.operationTypes += ("," + arrCode[i]);
                        }
                    }
                } else {
                    RightBean right = new RightBean(pub); //如果hashmap里没有权限，那么就新建一个
                    right.rightTypeCode = rs.getString("FBarCode"); //给新建的权限赋值
                  //modifie by yeshenghong, story2917 20130114
//                    right.rightTypeCode = rs.getString("fmenubarcode"); //给新建的权限赋值
                    right.operationTypes = rs.getString("fopertypes"); //给新建的权限赋值
                    right.assetGroupCode = sAssetCode; //给新建的权限赋值
                    hmPortRight.put(right.assetGroupCode + right.rightTypeCode, right); //把新建的权限放到hashMap里面
                }
            }
            Iterator iterator = hmPortRight.values().iterator(); //循环遍历hashMap里的内容
            while (iterator.hasNext()) {
                RightBean right = (RightBean) iterator.next(); //循环把hashMap里的内容取出来
                bufGroup.append(right.buildRespStr()); //把取出来的内容组合起来
            }
            if (bufGroup.toString().length() > 2) { //把组合群权限去除多余的解析符/f/f
                strReturn = bufGroup.toString().substring(0,
                    bufGroup.toString().length() - 2);
            }
        } catch (Exception e) {
            throw new YssException("获取用户组合群级别权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭资源
        }
        return strReturn; //把结果返回到前台
    }
    
    /**
     * add by wangzuochun 2009.12.1 MS00805 QDII估值系统行情资料权限控制不完善  QDV4华夏2009年11月05日01_B
     * @return
     * @throws YssException
     * @throws YssException
     */
    public String getPublicRight() throws YssException, YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        StringBuffer bufGroup = new StringBuffer();
        HashMap hmPortRight = new HashMap(); //定义一个hashtable用来排重复的权限和增加没有有权限
        try {
            //查询公共的权限
            strSql = " SELECT menu.fbarcode, rig.fopertypes " +
                " FROM (select g.fbarcode, g.frighttype" +
                " from Tb_Fun_MenuBar g where g.frighttype like '%public%') menu" +
              //modifie by yeshenghong, story2917 20130114
                " JOIN (SELECT *" +
                " FROM Tb_Sys_Userright" +
                " WHERE (FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                " AND fassetgroupcode = ' ' and FPortCode = ' ') " +
                //20120925 modified by liubo.Story #2737.获取符合条件的权限继承数据，拼接到SQL中获取继承的权限
                //===================================================
                getInheritedRights(pub.getAssetGroupCode(),"public","","login") + 
                //=====================end========================
                ") rig ON menu.FbarCode = rig.FRightCode" +
                //=========================================================================================================
                " UNION" +
                " SELECT m.fbarcode, ur.fopertypes " +
                " FROM Tb_Sys_Userright a" +
                " LEFT JOIN Tb_Sys_Roleright ur ON a.FRightCode = ur.frolecode" +
                " LEFT JOIN Tb_Fun_MenuBar m ON ur.frightcode = m.fbarcode" +
                //" JOIN Tb_Sys_RightType rt ON m.fbarcode = rt.fmenubarcode" +
              //modifie by yeshenghong, story2917 20130114
                " WHERE (a.FUserCode = " + dbl.sqlString(pub.getUserCode()) +
                " AND a.FRightType = 'public'" +
                " AND a.FAssetGroupCode = ' ' and FPortCode = ' ' "+
                " AND a.Frightind = 'Role'" +
                " AND m.frighttype LIKE '%public%')"//公共权限并上角色权限
              //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
              //并上权限继承中委托人的角色权限
              //-----------------------------------
                + getInheritedRoleRights(pub.getAssetGroupCode(),"public","a",""); 
            //-------------------end----------------
                //==============================================================================
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	this.rightTypeCode = rs.getString("FBarCode"); //获取权限类型代码
                if(this.rightTypeCode == null){
                    continue;
                }
            	
            	if (hmPortRight.containsKey(this.rightTypeCode)) { //判断hashtable里面是否含有当前对象
                    RightBean right = (RightBean) hmPortRight.get(this.
                        rightTypeCode); //得到hashtble里的对象
                    String sRightTypeCode = rs.getString("fopertypes"); //得到对象里的权限
					if (sRightTypeCode == null){
                    	continue;
                    }
                    String[] arrCode = sRightTypeCode.split(",");       //解析出当前对象的权限
                    for (int i = 0; i < arrCode.length; i++) {          //循环当前对象的权限
                        if (right.operationTypes.indexOf(arrCode[i]) == -1) { //看hashtable里面是否有当前对象的权限
                            right.operationTypes += ("," + arrCode[i]); //没就加入
                        }
                    }
                } else {
                    RightBean right = new RightBean(pub);   //如果hashtable里面没有当前对象就new一个
                    right.rightTypeCode = rs.getString("fbarcode");   //获取权限代码
                    right.operationTypes = rs.getString("FOperTypes");  //获取操作类型
//                    right.windowName = rs.getString("FRightTypeCode");  //获取窗体名称
                  //modifie by yeshenghong, story2917 20130114
                    hmPortRight.put(right.rightTypeCode, right);    //把当前对象加入到hatable里面去
                }
            }
            Iterator iterator = hmPortRight.values().iterator(); //循环遍历hashMap里的内容
            while (iterator.hasNext()) {
                RightBean right = (RightBean) iterator.next(); //循环把hashMap里的内容取出来
                bufGroup.append(right.buildRespStr()); //把取出来的内容组合起来
            }
            if (bufGroup.toString().length() > 2) { //把公共权限去除多余的解析符/f/f
                strReturn = bufGroup.toString().substring(0,
                    bufGroup.toString().length() - 2);
            }
        } catch (Exception e) {
            throw new YssException("获取用户公共级别权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭资源
        }
        return strReturn; //把结果返回到前台
    }

    /**
     * 获取用户可用的组合
     * @throws YssException
     * @return String
     */
    public String getUserPorts() throws YssException {
        String strSql = "", strHeader = "可用组合代码\t可用组合名称\t启用日期";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            //2009.04.29 蒋锦 修改 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
            //修改了 SQL 语句，获取可用组合的部分 TB_Sys_UserRight 中可用组合的储存方式已在 MS00010 中被修改
            strSql = "select y.* from " +
            
            // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
            
            "(select FPortCode from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " " +
            " where  FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
            dbl.sqlString(pub.getAssetGroupCode()) +
            " ) x join" +
            " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
            " p.FCuryName as FCuryName,"+//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
            
            " s.FSecurityName as FAimETFName," + //add by zhangjun 2012-04-26 ETF联接基金
            
            " f.FAssetGroupName as FAssetGroupName, e.FCurrencyName as FCurrencyName,f.FBaseRateSrcName,g.FPortRateSrcName  from " +
            pub.yssGetTableName("Tb_Para_Portfolio") + " a" +
       
                
                //end by lidaolong 
                //---//add by lidaolong 2011.01.25 ;QDV4上海2010年12月10日02_A
                "    left join (select FCuryName,FCuryCode from "+ pub.yssGetTableName("Tb_Para_Currency")+" ) p on p.FCuryCode = a.FCuryCode  " +
                //---end QDV4上海2010年12月10日02_A-------------
                
                //-----add by zhangjun 2012-04-26 ETF联接基金 Tb_001_Para_Security 
                " left join ( select FSecurityCode,FSecurityName from " +pub.yssGetTableName("Tb_Para_Security") + ") s on a.FAimETFCode = s.FSecurityCode " +            
                //-----add by zhangjun 2012-04-26 ETF联接基金

                
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAssetGroupCode,FAssetGroupName from Tb_Sys_AssetGroup) f on a.FAssetGroupCode = f.FAssetGroupCode" +
                " left join (select FCuryCode,FCuryName as FCurrencyName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) e on a.FPortCury = e.FCuryCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FBaseRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) f on a.FBaseRateSrcCode = f.FExRateSrcCode" +
                " left join (select FExRateSrcCode,FExRateSrcName as FPortRateSrcName from " +
                pub.yssGetTableName("Tb_Para_ExRateSource") +
                " where FCheckState = 1) g on a.FPortRateSrcCode = g.FExRateSrcCode" +

                " join (select DISTINCT FPortCode from Tb_Sys_Userright where FAssetGroupCode = '" +
                this.assetGroupCode +
                "' and FUserCode = '" + this.userCode +
                "' and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) +
                " )d on a.FPortCode = d.FPortCode where a.fcheckstate = 1 and a.FEnabled = 1" +
                // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
               /* ") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate ";*/
                ") y on x.FPortCode = y.FPortCode ";
            //end by lidaolong
            //========================== 增加组合的筛选 ===========================
            //MS00003-QDV4.1赢时胜上海2009年2月1日03_A: 参数设置布局分散不便操作
            //add by sunkey 20090411
            //流程的相关信息是存放在pub里的，如果取得到流程的信息就要对组合进行筛选
            if (pub.getFlow() != null) {
                FlowBean flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                if (flow != null) {
                    String tmpPorts = flow.getFPorts();
                    if (tmpPorts != null && !tmpPorts.trim().equals("")) {
                        //将组合作为筛选条件放入上述sql语句
                        strSql += " Where y.FPortCode in (" + operSql.sqlCodes(tmpPorts) + ")";
                    }
                }
            }
            //=============================End MS00003===========================
            strSql += " order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FPortCode") + "").trim()).append("\t");
                bufShow.append( (rs.getString("FPortName") + "").trim()).append("\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FStartDate"), YssCons.YSS_DATEFORMAT)).
                    append(YssCons.YSS_LINESPLITMARK);

                PortfolioBean portfolio = new PortfolioBean();
                portfolio.setYssPub(pub);
                portfolio.setPortfolioAttr(rs);
                bufAll.append(portfolio.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return strHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取用户可用的组合出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取角色可用的组合
     * @throws YssException
     * @return String
     */
    public String getRolePorts() throws
        YssException {
        return "";
    }

    /**
     * 获取用户角色权限
     * @throws YssException
     * @return String
     */
    public String getRoleRight() throws
        YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select FRightCode,FOperTypes from Tb_Sys_RoleRight" +
                " where FRoleCode = '" + this.userCode + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.rightTypeCode = rs.getString("FRightCode");
                this.operationTypes = rs.getString("FOperTypes");
                buf.append(buildRespStr());
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 查询权限类型记录
     * @throws YssException
     * @return String
     */
    public String getRight(String tbName, String type) throws YssException {
        StringBuffer buffer = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            if (type != null) {
                strSql = " where FType='" + type + "'";
            }
            strSql = "select * from " + tbName + strSql + " ";
            //modify by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
            if (tbName.equalsIgnoreCase("Tb_Fun_MenuBar")) {
            	//------ add by wangzuochun 2011.04.22 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
            	//------ 把组合设置菜单条拼接到第一条，方便前台设置BGRID
            	strSql = "select * from " + tbName + " where FRightType = '" + type + "'" 
            			+ " and fbarcode = 'portfolio'";
            	
            	rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            	if(rs.next()){
            		buffer.append(rs.getString("FBarCode"));
                    buffer.append("\t");
                    buffer.append(rs.getString("FBarName"));
                    buffer.append("\t");
                    buffer.append(rs.getString("FRightType"));
                    buffer.append("\t");
                    buffer.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
            	}
            	dbl.closeResultSetFinal(rs);
            	//------------------ #404 ----------------//
            	
            	//------ add by wangzuochun 2011.04.22 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
                strSql = "select * from " + tbName + " where FRightType = '" + type + "'"
                		+ " and fbarcode <> 'portfolio'";
                //------------------ #404 ----------------//
                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                while (rs.next()) {
                    buffer.append(rs.getString("FBarCode"));
                    buffer.append("\t");
                    buffer.append(rs.getString("FBarName"));
                    buffer.append("\t");
                    buffer.append(rs.getString("FRightType"));
                    buffer.append("\t");
                    buffer.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
                }
                dbl.closeResultSetFinal(rs); //add by wangzuochun
            } else {
                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
                while (rs.next()) {
                    buffer.append(rs.getString("FOperTypeCode"));
                    buffer.append("\t");
                    buffer.append(rs.getString("FOperTypeName"));
                    buffer.append("\t");
                    buffer.append(rs.getString("Ftype"));
                    buffer.append("\t");
                    buffer.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
                }
                dbl.closeResultSetFinal(rs); //add by wangzuochun
            }
            return buffer.toString();
        } catch (SQLException se) {
            throw new YssException("获取权限信息出错！", se); //注意这里抛出异常的方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String rightQuery(String usercode, String assetgroup, String type,
                             String role) throws YssException {
        StringBuffer buffer = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        String strTbName;
        String[] aSTemp;
        try {
            if (role.equalsIgnoreCase("user")) {
                strTbName = "Tb_Sys_UserRight";
            } else {
                strTbName = "Tb_Sys_UserRight";
            }
            if (usercode.length() > 0) {
                strSql = " where FUserCode='" + usercode + "'";
                if (type != null && type.length() > 0) {
                    strSql = strSql + " and FRightInd='" + type + "'";
                }
                if (assetgroup != null && assetgroup.length() > 0) {
                    strSql = strSql + " and FAssetGroupCode='" + assetgroup +
                        "'";
                }
            }

            strSql = "select * from " + strTbName +
                strSql + " order by FrightCode";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                if (rs.getString("FOperTypes") != null &&
                    rs.getString("FOperTypes").length() > 0) {
                    aSTemp = rs.getString("FOperTypes").trim().split(",");
                    for (int i = 0; i < aSTemp.length; i++) {
                        buffer.append(rs.getString("FRightCode")).append("\t");
                        buffer.append(aSTemp[i]).append("\t").append(YssCons.
                            YSS_LINESPLITMARK);
                    }
                }
            }
            return buffer.toString();
        } catch (SQLException se) {
            throw new YssException("获取系统权限信息出错！", se); //注意这里抛出异常的方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 检测系统是否初试化，导入初试化数据、建立组合群、创建初次登陆用户并设置权限
     * @throws YssException
     * @return String
     */
    public String getRightIsNull(String sTableName) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String buffer = "false";
        try {
            strSql = "select * from " + sTableName;
            rs = dbl.openResultSet(strSql);
            if (!rs.next()) {
                buffer = "true";
            }
            return buffer.toString();

        } catch (Exception e) {
            throw new YssException("检测系统是否初试化时出错！", e); //注意这里抛出异常的方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData1
     *获取组合和套帐的对应代码 add byyeshenghong 
     * @return String
     * @throws YssException 
     */
    public String getListViewData1() throws YssException {
    	StringBuffer buffer = new StringBuffer();
        ResultSet rs = null;
        ResultSet rsPort = null;
        String strSql = "";
        String strTbName;
        String assetGroup = "";
        try {
        	strSql = " select table_name from user_tables where table_name like '%PARA_PORTFOLIO' ";
        	rs = dbl.openResultSet(strSql);
        	while(rs.next())
        	{
        		strTbName = rs.getString("table_name");
        		/**Start 20131205 modified by liubo.Bug #85022.QDV4赢时胜(上海开发)2013年12月5日01_B
        		 * 用中银的库进行年结测试的时候发现，中银的第二十九个组合群，组合群代码为29，而不是一般情况下的029.
        		 * 使用之前的strTbName.substring(3, 6)方式来获取组合群代码，就会获取到“29_”，
        		 * 这样就无法将财务系统的套账号和组合群组合代码进行关联。
        		 * 在这里修改，使用解析下划线，拆分组合设置表名的，然后取下表为1的一段，来正确获取组合群代码*/
        		if (strTbName != null)
        		{
	        		strSql = "select distinct p.fportcode,to_char(l.fsetcode,'000') as fsetcode from " + strTbName 
	        				+ " p join lsetlist l on p.fassetcode = l.fsetid where p.fcheckstate = 1 ";//添加审核 add by yeshenghong 20130730 story4214
	        		
	//        		assetGroup = strTbName.substring(3, 6);
	        		String[] sCodesDetail = strTbName.split("_");
	        		if (sCodesDetail.length >= 4)
	        		{
		        		rsPort = dbl.openResultSet(strSql);
		        		while(rsPort.next())
		        		{
		        			buffer.append(rsPort.getString("fsetcode")).append("\t");
		                    buffer.append(rsPort.getString("fportcode") + sCodesDetail[1]).append("\t").append(YssCons.
		                        YSS_LINESPLITMARK);
		        		}
	        		}
	        		dbl.closeResultSetFinal(rsPort);
        		}
        		/**End 20131205 modified by liubo.Bug #85022.QDV4赢时胜(上海开发)2013年12月5日01_B*/
        	}
            
            return buffer.toString();
        } catch (SQLException se) {
            throw new YssException("获取组合套帐信息出错！", se); //注意这里抛出异常的方式
        } finally {
        	dbl.closeResultSetFinal(rsPort);
            dbl.closeResultSetFinal(rs);
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
    public void checkInput(byte btOper) {
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        return getBeforeEditData();
        /*
          StringBuffer buffer = new StringBuffer();
          buffer.append(this.userCode.trim()).append("\t");
          buffer.append(this.assetGroupCode.trim()).append("\t");
          buffer.append(this.rightTypeCode.trim()).append("\t");
          buffer.append(this.operationTypes).append("\t");
          buffer.append(this.type).append("\t");
          buffer.append(this.rightInd.trim()).append("\t");
          buffer.append(this.portCodes).append("\t");
          return buffer.toString();
         */
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 3)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            else if (sRowStr.split("<Logging>").length == 2)
            {
            	if (!this.getFunName().equals("rightSet_Role"))
            	{
            		this.sLoggingPositionData = "";
            	}
            	else
            	{
            		this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            	}
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //===================end==================
        	
            reqAry = sRowStr.split("\t");
            this.userCode = reqAry[0];
            this.assetGroupCode = reqAry[1];
            this.rightTypeCode = reqAry[2];
            this.operationTypes = reqAry[3];
            this.type = reqAry[4];
            this.rightInd = reqAry[5];
            if (reqAry.length >= 8) {
                this.portCodes = reqAry[7];
            }
            if (reqAry.length >= 9) {
                this.reportCodes = reqAry[8];
            }
        } catch (Exception e) {
            throw new YssException("解析据出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        StringBuffer buf = new StringBuffer();
        String userRole = "";
        String rightType = "";
        String ports = "";
        String userRight = "";
        String reports = "";
        String strHeader = "";
        String userCode = "";
        String assetGroupCode = "";
        String userName = "";
        String assetGroupName = "";
        try {
            strHeader = "可用组合代码\t可用组合名称\t启用日期\r\f\r\f";
            RepGroupSetBean repGroup = new RepGroupSetBean();
            repGroup.setYssPub(pub);

            RoleBean roleBean = new RoleBean();
            roleBean.setYssPub(pub);
            roleBean.setAssetGroupCode(this.assetGroupCode);//2009-08-04 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B 修改了属性名
            roleBean.setUserCode(this.userCode);

            userRole = roleBean.getUserRoles();

            rightType = getFundRightType();

            userRight = this.getUserAllRight();
            ports = this.getUserPorts();

            if (ports.equalsIgnoreCase(strHeader)) {
                ports = "";
            } else {
                ports = ports.replaceAll("\r\f", "/convert/");
            }

            reports = repGroup.getUserReport(this.userCode, this.assetGroupCode);
            userCode = this.userCode;
            assetGroupCode = this.assetGroupCode;

            buf.append(userCode).append("[------]");
            buf.append(this.getUserName()).append("[------]");
            buf.append(assetGroupCode).append("[------]");
            buf.append(this.getAssetGroupName()).append("[------]");
            buf.append(userRole.replaceAll("\f\f", "[-----]")).append(
                "[------]");
            buf.append(rightType.replaceAll("\f\f", "[-----]")).append(
                "[------]");
            buf.append(userRight.replaceAll("\f\f", "[-----]")).append(
                "[------]");
            buf.append(ports.replaceAll("\f\f", "[-----]")).append("[------]");
            buf.append(reports.replaceAll("\f\f", "[-----]"));

            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public String getUserName() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = " select FUserName from Tb_Sys_UserList" +
                " where FUserCode=" + dbl.sqlString(this.userCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getString("FUserName");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
        finally {
            dbl.closeResultSetFinal(rs); //用于关闭结果集
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
    }

    public String getAssetGroupName() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sResult = "";
        try {
            strSql = " select FAssetGroupName from TB_SYS_ASSETGROUP" +
                " where FAssetGroupCode=" +
                dbl.sqlString(this.assetGroupCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getString("FAssetGroupName");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
        finally {
            dbl.closeResultSetFinal(rs); //用于关闭结果集
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

    
    //---------------------- MS01696 QDV4赢时胜上海2010年09月03日01_AB  权限浏览可根据角色权限配置表的格式导出    add by jiangshichao ---------------------
    
    public String getMenuStr(String squest)throws YssException{
    	
    	String query = "";
    	ResultSet rs = null;
    	String menuStr="";
    	StringBuffer buf = new StringBuffer();
    	int lineNum = 1;
    	HashMap Menu_1st = new HashMap ();
    	HashMap Menu_2nd = new HashMap ();
    	HashMap RoleOperTypeMap = new HashMap();
    	//String operTypeStr = "";
    	String roleOperType = "";
    	try {
    		Menu_1st = initFatherMenu(Menu_1st,1);
    		Menu_2nd = initFatherMenu(Menu_2nd,2);
    		RoleOperTypeMap = initRoleOperType(RoleOperTypeMap,squest);
    		query = " select fbarcode,fbarname,fordercode,fopertypecode,nvl(fdesc,' ') as fdesc,frighttype from tb_fun_menubar where frefinvokecode <> 'null'" +
    				" and fopertypecode <> 'null' and fenabled = 1 order by fordercode";
    		rs = dbl.openResultSet(query);
    		while(rs.next()){
    			//operTypeStr = dealOperTypeStr(true,rs.getString("fopertypecode"));
    			roleOperType = (String)RoleOperTypeMap.get(rs.getString("fbarcode"));
    			
    			if(rs.getString("fordercode").length()==6){
    				buf.append((String)Menu_1st.get(rs.getString("fordercode").substring(0, 3))).append("\t");
    				buf.append(rs.getString("fbarname")).append("\t");
    				buf.append("").append("\t");
    				buf.append(dealOperType(rs.getString("frighttype"))).append("\t");
    				buf.append(rs.getString("fdesc").equalsIgnoreCase("null")?"":rs.getString("fdesc")).append("\t");
    				//buf.append(operTypeStr).append("\t");
    				buf.append(roleOperType).append("\r\n");
    			}else if(rs.getString("fordercode").length()==9){
    				buf.append((String)Menu_1st.get(rs.getString("fordercode").substring(0, 3))).append("\t");
    				buf.append((String)Menu_2nd.get(rs.getString("fordercode").substring(0,6))).append("\t");
    				buf.append(rs.getString("fbarname")).append("\t");
    				buf.append(dealOperType(rs.getString("frighttype"))).append("\t");
    				buf.append(rs.getString("fdesc").equalsIgnoreCase("null")?"":rs.getString("fdesc")).append("\t");
    				//buf.append(operTypeStr).append("\t");
    				buf.append(roleOperType).append("\r\n");
    			}
    			
    			
    		}
    		
    		if(buf.toString().length()>2){
    			menuStr = buf.toString().substring(0,buf.toString().length()-2);
    		}
    		//menuStr = operTypeStr+"\f\f"+buf.toString();
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
        	dbl.closeResultSetFinal(rs);
        }
    	return menuStr;
    }
    
    
    public String getOperTypeStr()throws YssException{
    	
    	String query = "";
    	ResultSet rs = null;
    	String sResult="";
    	StringBuffer buf = new StringBuffer();
    	String operTypeStr = "";
    	try {
    		query = " select fbarcode,fbarname,fordercode,fopertypecode,nvl(fdesc,' ') as fdesc,frighttype from tb_fun_menubar where frefinvokecode <> 'null'" +
    				" and fopertypecode <> 'null' and fenabled = 1 order by fordercode";
    		rs = dbl.openResultSet(query);
    		while(rs.next()){
    			operTypeStr = dealOperTypeStr(true,rs.getString("fopertypecode"));
    				buf.append(operTypeStr).append("\t");
    		}
    		if(buf.toString().length()>1){
    			sResult = buf.toString().substring(0,buf.toString().length()-1);
    		}
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
        	dbl.closeResultSetFinal(rs);
        }
    	return sResult;
    	
    }
    /************************************************************
     * 处理权限字符串
     * @param flag
     * @param operTypeStr
     * @return
     */
    private String dealOperTypeStr(boolean flag,String operTypeStr){
    	String operType = "";
    	StringBuffer buf = new StringBuffer();
    	// 清除,还原,增加,删除,修改,浏览,行使,审核
    	if(flag){
    		//处理菜单权限，如果没有则处理成 
    		if(operTypeStr.indexOf("clear")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("revert")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("add")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("del")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("edit")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("brow")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("execute")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    		if(operTypeStr.indexOf("audit")==-1){
    			buf.append("X").append(",");
    		}else{
    			buf.append("").append(",");
    		}
    	}else{
    		//处理某个角色权限
    		if(operTypeStr.indexOf("clear")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("revert")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("add")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("del")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("edit")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("brow")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("execute")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    		if(operTypeStr.indexOf("audit")!=-1){
    			buf.append("√").append("\t");
    		}else{
    			buf.append("").append("\t");
    		}
    	}
    	
    	if(buf.toString().length()>0){
    		operType = buf.toString().substring(0,buf.toString().length()-1);
    	}
    	return operType;
    }
    
    /*********************************
     * 获取菜单级别数
     */
    public int getMenuLevel() throws YssException {
    	String sql = "";
    	ResultSet rs = null;
    	int level=0;
    	try {
    		sql = " select max(length(fordercode)) as menulevel from tb_fun_menubar ";
    		rs = dbl.openResultSet(sql);
    		while(rs.next()){
    			level = rs.getInt("menulevel")/3;
    		}
    	}catch(Exception e){
    		throw new YssException("获取菜单级数出错!!!!");
    	}finally{
        	dbl.closeResultSetFinal(rs);
        }
    	return level;
    }
    
    /********************************************************
     * 获取父级菜单包含多少个子菜单
     * @param orderCode  
     * @param menuLevel
     * @return
     * @throws YssException
     */
    
    private HashMap initFatherMenu(HashMap map,int menuLevel) throws YssException{
    	
    	String query = "";
    	ResultSet rs = null;
    	String menuStr = "";
    	try{
    		query = " select fordercode,fbarname from tb_fun_menubar where length(fordercode) = "+3*menuLevel+
    		        " and frefinvokecode = 'null' and frighttype <> ' '  order by fordercode ";
    		rs = dbl.openResultSet(query);
    		while(rs.next()){
    			menuStr = rs.getString("fbarname");
    			map.put(rs.getString("fordercode"), menuStr);
    		}
    		return map;
    	}catch(Exception e){
    		throw new YssException("初始化父级菜单出错!!!");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    private HashMap initRoleOperType(HashMap map,String squest) throws YssException{
    	String squestAry[] = null;
    	String strRole = "";
    	String strRoleCode = "";
    	String strSql = "";
    	String dealStr = "";
    	String oldValue = "";
    	ResultSet rs = null;
    	try{
           squestAry = squest.split("\t");
    		
    		for (int i = 0; i < squestAry.length; i++)
    		{
    			strRole = squestAry[i];
    			if (strRole != null && strRole.length() > 0)
    			{
    				strRoleCode = strRole.split("-")[0];
    			}

    	        //---- 角色的操作类型
    	        strSql = " select a.fbarcode,a.fordercode,nvl(b.fopertypes,' ') as fopertypes from "+
    	                 " (select fordercode,fbarcode from tb_fun_menubar where frefinvokecode <> 'null' and fopertypecode <> 'null' and fenabled = 1" +
    	                 "  order by fordercode )a " +
    	                 " left join " +
    	                 " (select frightcode,fopertypes from tb_sys_roleright where frolecode="+dbl.sqlString(strRoleCode)+" and fopertypes<>'Role' )b" +
    	                 " on a.fbarcode = b.frightcode ";
    	        
    	        rs = dbl.openResultSet(strSql);
    	      	
            	while (rs.next()) {
            		if(map.containsKey(rs.getString("fbarcode"))){
            			dealStr = dealOperTypeStr(false,rs.getString("fopertypes"));
            			oldValue = (String) map.get(rs.getString("fbarcode"));
            			map.put(rs.getString("fbarcode"), oldValue+"\t"+dealStr);
            		}else{
            			dealStr = dealOperTypeStr(false,rs.getString("fopertypes"));
            			map.put(rs.getString("fbarcode"),dealStr );
            		}
            	}
    		}
    		return map;
    	}catch(Exception e){
    		throw new YssException("处理角色操作类型出错!!!");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    
    private String dealOperType(String operType) throws YssException{
    	StringBuffer buf = new StringBuffer();
    	String sOperType = "";
    	if(operType.indexOf("system")!=-1){
			buf.append("系统权限").append(",");
    	}
    	if(operType.indexOf("public")!=-1){
			buf.append("公共级别").append(",");
    	}
    	if(operType.indexOf("group")!=-1){
			buf.append("组合群级别").append(",");
    	}
    	if(operType.indexOf("port")!=-1){
			buf.append("组合级别").append(",");
    	}
    	
    	if(buf.toString().length()>1){
    		sOperType = buf.toString().substring(0,buf.toString().length()-1);
    	}
    	return sOperType;
    }
    
    public String exportForRole(String squest) throws YssException {
    	String strReturn = "";
        
    	try{
    		
    		strReturn = getMenuStr(squest);
    		
    	
    		return strReturn;
    		
    	} catch (Exception e) {
    		throw new YssException("导出角色权限出错", e);
    	}

    
    
    }
    //---------------------- MS01696 QDV4赢时胜上海2010年09月03日01_AB  权限浏览可根据角色权限配置表的格式导出    end --------------------------------------
    
    //add by huangqirong 2011-06-25 Story #937
    public String getUserPortCodes(String rightCode) throws YssException{
		ResultSet rs = null;
		String strSql = "";
		String strPortCodes = "";
		//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
		ArrayList alPortCode = new ArrayList();
		StringBuffer sqlBuf = new StringBuffer();
		//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
		try {
			strSql = "select distinct(tsu.fportcode) as fportcode from(select fportcode from Tb_Sys_Userright"
      		        		+" where ((fusercode ="+dbl.sqlString(pub.getUserCode())					    
      					    +" and frighttype = 'port') "
      	                    //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
      	                    //获取权限时需要包含继承到的权限，包括用户权限和角色权限
      	                    //==================================
      					    + getInheritedRights(pub.getAssetGroupCode(),"port","","login") + ") "
      	                    //==================end================
      					    +" and FOPERTYPES like '%brow%'"
      					    +" and frightcode = "+dbl.sqlString(rightCode)
      					    +" and fassetgroupcode="+dbl.sqlString(pub.getAssetGroupCode())+") tsu"
      					    +" inner join "+pub.yssGetTableName("tb_Para_Portfolio")
      					    +" tpp on tpp.fportcode=tsu.fportcode"
      					    +" where tpp.fenabled=1"
      					    +" and tpp.FCheckState=1";
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				strPortCodes+= rs.getString("fportcode")+",";
				//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
				if(!alPortCode.contains(rs.getString("fportcode")))
					alPortCode.add(rs.getString("fportcode"));
				//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
			}
			
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B start---//
			dbl.closeResultSetFinal(rs);
			rs = null;
			
			sqlBuf.append(" select a.FPortCode from (select distinct FRightCode, ")
			      .append(" FPortCode from Tb_Sys_Userright where (FUserCode = ")
                  //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
                  //获取权限时需要包含继承到的权限，包括用户权限和角色权限
                  //==================================
			      .append(dbl.sqlString(pub.getUserCode()))
			      .append(getInheritedRights(pub.getAssetGroupCode(),"")).append(")")
			      //===============end==================
			      .append(" and frightind = 'Role' and frighttype = 'port' ")
			 	  .append(" and FAssetGroupcode = ")
			 	  .append(dbl.sqlString(pub.getAssetGroupCode()))
			 	  .append(") a left join tb_sys_roleright ")
			 	  .append(" rol on a.Frightcode = rol.frolecode where rol.FRightCode = ")
			 	  .append(dbl.sqlString(rightCode))
			 	  .append(" and FOPERTYPES like '%brow%' ");
			
			strSql = sqlBuf.toString();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if(!alPortCode.contains(rs.getString("fportcode")))
				{
					alPortCode.add(rs.getString("fportcode"));
					strPortCodes+= rs.getString("fportcode")+",";
				}
			}
			//---add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B end---//
			
			if(strPortCodes.length()>0)
				strPortCodes=strPortCodes.substring(0, strPortCodes.length()-1);

			return strPortCodes;
		} catch (Exception e) {
			throw new YssException("查询用户可操作组合出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);//add by songjie 2011.07.12 BUG 2259 QDV4赢时胜上海2011年07月01日01_B
		}
    }
    //---end---
    
  
    //add by guolongchao 20120426 添加自审功能 QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
    //获取组合群是否启用了自审功能
    public String getAuditOwnState(String squest) throws YssException 
    {    	
    	String reStr="false";
		ResultSet rs = null;		
		try {	
			if(squest.equals("public")){
				rs = dbl.openResultSet(" select * from  TB_SYS_AssetGroup  where  fauditown='yes' ");
			}
			else{
				rs = dbl.openResultSet(" select * from  TB_SYS_AssetGroup  where  fauditown='yes' and fassetgroupcode in("+operSql.sqlCodes(squest)+")");
			}
			
			if(rs.next()){
				reStr="true";
			}	
		} catch (Exception e) {
			throw new YssException("查询组合群是否启用自审功能出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}		
		return reStr;
    }

	/**返回 fRoleCode 的值*/
	public String getFRoleCode() {
		return FRoleCode;
	}

	/**传入fRoleCode 设置  fRoleCode 的值*/
	public void setFRoleCode(String fRoleCode) {
		FRoleCode = fRoleCode;
	}
    
	/**shashijie 2012-7-11 STORY 2661 */
	public String isRoleRight() throws YssException {
		String flag = "false";
		ResultSet rs = null;
		try {
			String query = getQuery();
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				flag = "true";
			}else {
				flag = "false";
			}
			
		} catch (Exception e) {
			throw new YssException("判断角色是有拥有权限出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		
		return flag;
	}

	/**shashijie 2012-7-11 STORY 2661 */
	private String getQuery() {
		String strSql = "Select B1.Frolecode, B1.Frightcode, B1.Fopertypes " +
			" From Tb_Sys_Roleright B1 Where B1.FRoleCode = "+dbl.sqlString(this.FRoleCode)+
			" And B1.FRightCode = "+dbl.sqlString(this.FRightCode)+
			" And Case When B1.Fopertypes = 'Role' Then 'Report' Else B1.Fopertypes End = "+
			dbl.sqlString(this.rightInd)+
			" ";
		return strSql;
	}

	/**返回 fRightCode 的值*/
	public String getFRightCode() {
		return FRightCode;
	}

	/**传入fRightCode 设置  fRightCode 的值*/
	public void setFRightCode(String fRightCode) {
		FRightCode = fRightCode;
	}

	//20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
	//新增此方法，用户登录或者获取菜单条时，合并委托人的角色权限
	public String getInheritedRoleRights(String sCurrentAssetGroup, String sFlag, String sPrefix, String sInvoking) throws YssException
	{
	String sReturn = "";
	String strSql = "";
	ResultSet rs = null;
	     java.util.Date dCurrentDate = new Date();
	     if (!sPrefix.trim().equals(""))
	     {
	     sPrefix = sPrefix + ".";
	     }
	     
	try
	{
	strSql = "Select * from tb_sys_PerInheritance " +
	 " where FStartDate <=" + dbl.sqlDate(dCurrentDate) +
	 " and FEndDate >= " + dbl.sqlDate(dCurrentDate) + 
	 " and FTrustee like '%" + userCode + "%' and FCHECKSTATE = 1";
	rs = dbl.queryByPreparedStatement(strSql);
	
	while(rs.next())
	{
	     String[] sPortList;
	     //解析符合条件的委托人的组合列表字段
	     //此字段的存储规则为组合群代码1>>组合1,组合群代码2>>组合2。例如：AssetGroup1>>Port1,AsetGroup2>>Port2,AssetGroup3>>Port3
	     if (rs.getString("FPORTCODELIST") != null && !rs.getString("FPORTCODELIST").trim().equals(""))
	     {
	    	 sPortList = rs.getString("FPORTCODELIST").split(",");
	     
	    	 for (int i = 0; i < sPortList.length; i++)
	    	 {
	    		 	String[] sPortDetail = sPortList[i].split(">>");
	    		 	if (sPortDetail.length >= 2)
	    		 	{
		    		 	if (sPortDetail[0].trim().equals((sCurrentAssetGroup)))
		    		 	{
			    		 	{
			    		 		if (sFlag.equalsIgnoreCase("port"))
			    		 		{
			    		 			sReturn = sReturn +  " Or (" + sPrefix + "FRightType = 'port' and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and " + sPrefix + "FportCode = " + dbl.sqlString(sPortDetail[1]) + " and  " + sPrefix + "frightind = 'Role' and " + sPrefix + "FAssetGroupCode = '" + sCurrentAssetGroup + "') ";
			    		 		}
			    		 		else if (sFlag.equalsIgnoreCase("group"))
			    		 		{
			    		 			sReturn = sReturn + " Or (" + sPrefix + "FRightType = 'group' AND " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and " + sPrefix + "frightind = 'Role' and " + sPrefix + "FAssetGroupCode = '" + sCurrentAssetGroup + "') ";
			    		 			break;
			    		 		}
			    		 	}
		    		 	}
	    		 	}
	    	 }

			 if (sFlag.equalsIgnoreCase("public"))
			 {
				 sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' and " + sPrefix + "frightind = 'Role') and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = '" + sCurrentAssetGroup + "' or " + sPrefix + "FAssetGroupCode = ' '))";
				break;
			 }
	     }
	     
	     
		}
	}
		catch(Exception ye)
		{
			throw new YssException("获取继承的角色权限出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return sReturn;
	}
	
    //20120807 added by liubo.Story #2737
  //以系统当前日期、当前登录用户、当前组合群为条件，查询是否有需要继承的委托人的权限
    public String getInheritedRights(String sCurrentAssetGroup, String sFlag, String sPrefix, String sInvoking) throws YssException
    {
    	String sReturn = "";
    	String strSql = "";
    	ResultSet rs = null;
    	java.util.Date dCurrentDate = new Date();
    	
    	if (!sPrefix.trim().equals(""))
    	{
    		sPrefix = sPrefix + ".";
    	}
    	
    	try
    	{
    		strSql = "Select * from tb_sys_PerInheritance " +
    				 " where FStartDate <=" + dbl.sqlDate(dCurrentDate) +
    				 " and FEndDate >= " + dbl.sqlDate(dCurrentDate) + 
    				 " and FTrustee like '%" + userCode + "%' and FCHECKSTATE = 1";
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
//    			if (sFlag.equalsIgnoreCase("port") || sFlag.equalsIgnoreCase("group"))
    			{
	    			String[] sPortList;
	    			//解析符合条件的委托人的组合列表字段
	    			//此字段的存储规则为组合群代码1>>组合1,组合群代码2>>组合2。例如：AssetGroup1>>Port1,AsetGroup2>>Port2,AssetGroup3>>Port3
	    			if (rs.getString("FPORTCODELIST") != null && !rs.getString("FPORTCODELIST").trim().equals(""))
	    			{
	    				sPortList = rs.getString("FPORTCODELIST").split(",");
	    				
	    				for (int i = 0; i < sPortList.length; i++)
	    				{
	    					String[] sPortDetail = sPortList[i].split(">>");
	    					if (sPortDetail.length >= 2)
	    					{
								//在这里分为两种调用状态
								//------------------------------------------------
								
								//登录状态下，需要判断方法中传入的组合群是否与当前登录组合群匹配
	    						if (sInvoking.equalsIgnoreCase("login"))
	    						{
		    						if (sPortDetail[0].trim().equals((sCurrentAssetGroup)))
		    						{
			    						if (sFlag.equalsIgnoreCase("port"))
			    						{
			    							sReturn = sReturn +  " Or (" + sPrefix + "FRightType = 'port' and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and " + sPrefix + "FportCode = " + dbl.sqlString(sPortDetail[1]) + " and " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sPortDetail[0]) + ")";
	
			    						}
//			    						else if (sFlag.equalsIgnoreCase("group"))
//			    						{
//			    							sReturn = sReturn + " Or (" + sPrefix + "FRightType = 'group' AND " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor"))  + " and " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sPortDetail[0]) + ")";
//			    							break;
//			    						}
//			    		    			else if (sFlag.equalsIgnoreCase("public"))
//			    		    			{
//			    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system') and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " or " + sPrefix + "FAssetGroupCode = ' '))";
//			    		    			}
//			    		    			else if (sFlag.equalsIgnoreCase("pub"))
//			    		    			{
//			    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system' or (" + sPrefix + "frighttype like '%group%' AND " + sPrefix + "FAssetGroupCode = " + sCurrentAssetGroup + ")) and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + ")";
//			    		    			}
			    					}
	    						}
								//在权限设置状态下，不需要判断是否与当前组合群匹配
	    						else if (sInvoking.equalsIgnoreCase("SetUserRight"))
	    						{
	    							if (sPortDetail[0].trim().equals((sCurrentAssetGroup)))
	    							{
		    							if (sFlag.equalsIgnoreCase("port"))
			    						{
			    							sReturn = sReturn +  " Or (" + sPrefix + "FRightType = 'port' and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and " + sPrefix + "FportCode = " + dbl.sqlString(sPortDetail[1])  + " and " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sPortDetail[0]) + ")";
	
			    						}
//			    						else if (sFlag.equalsIgnoreCase("group"))
//			    						{
//			    							sReturn = sReturn + " Or (" + sPrefix + "FRightType = 'group' AND " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor"))  + " and " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sPortDetail[0]) + ")";
//			    							break;
//			    						}
//			    						else if (sFlag.equalsIgnoreCase("public"))
//			    		    			{
//			    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system') and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " or " + sPrefix + "FAssetGroupCode = ' '))";
//			    		    			}
//			    						else if (sFlag.equalsIgnoreCase("pub"))
//			    		    			{
//			    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system' or (" + sPrefix + "frighttype like '%group%' AND " + sPrefix + "FAssetGroupCode = " + sCurrentAssetGroup + ")) and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " or " + sPrefix + "FAssetGroupCode = ' ')";;
//			    		    			}
	    							}
	    						}

	    		    			if (sFlag.equalsIgnoreCase("public"))
	    		    			{
	    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system') and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " or " + sPrefix + "FAssetGroupCode = ' '))";
	    		    			}
	    		    			else if (sFlag.equalsIgnoreCase("pub"))
	    		    			{
	    		    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system' or (" + sPrefix + "frighttype like '%group%' AND " + sPrefix + "FAssetGroupCode = " + sCurrentAssetGroup + ")) and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " and (" + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " or " + sPrefix + "FAssetGroupCode = ' ')";;
	    		    			}
								
								//----------------------end--------------------------
	    					}
	    				}
	    			}
    			}
//    			else if (sFlag.equalsIgnoreCase("group"))
//    			{
//    				sReturn = sReturn + " Or (" + sPrefix + "FRightType = 'group' AND " + sPrefix + "FAssetGroupCode = " + dbl.sqlString(sCurrentAssetGroup) + " and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + ")";
//    			}
//    			else if (sFlag.equalsIgnoreCase("public"))
//    			{
//    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system') and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + ")";
//    			}
//    			else if (sFlag.equalsIgnoreCase("pub"))
//    			{
//    				sReturn = sReturn + " Or ((" + sPrefix + "FRightType = 'public' or " + sPrefix + "FRightType = 'system' or (" + sPrefix + "frighttype like '%group%' AND " + sPrefix + "FAssetGroupCode = " + sCurrentAssetGroup + ")) and " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + ")";
//    			}
    		}

	    	return sReturn;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("获取继承权限出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    }
    
    //20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
    //============================================
    public String getInheritedRights(String sCurrentAssetGroup, String sPrefix) throws YssException
    {
    	String sReturn = "";
    	
    	String strSql = "";
    	ResultSet rs = null;
    	java.util.Date dCurrentDate = new Date();
    	
    	if (!sPrefix.trim().equals(""))
    	{
    		sPrefix = sPrefix + ".";
    	}
    	
    	try
    	{
    		strSql = "Select * from tb_sys_PerInheritance " +
    				 " where FStartDate <=" + dbl.sqlDate(dCurrentDate) +
    				 " and FEndDate >= " + dbl.sqlDate(dCurrentDate) + 
    				 " and FTrustee like '%" + userCode + "%' and FCHECKSTATE = 1";
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
	    		String[] sPortList;
	    			//解析符合条件的委托人的组合列表字段
	    			//此字段的存储规则为组合群代码1>>组合1,组合群代码2>>组合2。例如：AssetGroup1>>Port1,AsetGroup2>>Port2,AssetGroup3>>Port3
	    		if (rs.getString("FPORTCODELIST") != null && !rs.getString("FPORTCODELIST").trim().equals(""))
	    		{
	    			sPortList = rs.getString("FPORTCODELIST").split(",");
	    				
	    			for (int i = 0; i < sPortList.length; i++)
	    			{
	    				String[] sPortDetail = sPortList[i].split(">>");
	    				if (sPortDetail.length >= 2)
	    				{
		    				if (sPortDetail[0].trim().equals((sCurrentAssetGroup)))
		    				{
			    				sReturn = sReturn +  " Or " + sPrefix + "FUserCode = " + dbl.sqlString(rs.getString("FTrustor")) + " ";
	
			    			}
								
	    				}
	    			}
	    		}
    		}
    	
    		return sReturn;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("根据受托人获取权限继承委托人列表出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    public String getTrustors(String sTrustee,String sFlag) throws YssException
    {
    	String sReturn = "";
    	String strSql = "";
    	ResultSet rs = null;
    	java.util.Date dCurrentDate = new Date();
    	
    	try
    	{
    		strSql = "Select * from tb_sys_PerInheritance where FCheckState = 1 and " +
    				 " FTrustee like '%" + sTrustee + "%' " +
    				 " and FStartDate <= " + dbl.sqlDate(dCurrentDate) + " and FEndDate >= " + dbl.sqlDate(dCurrentDate);
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			sReturn = sReturn + "'" + rs.getString("FTrustor") + "',";
    		}
    		
    		if (sReturn.trim().length() > 3)
    		{
    			sReturn = sReturn.substring(0,sReturn.length() - 1);
    			if (sFlag.equalsIgnoreCase("SetUserRight"))
    			{
    				sReturn = " and FUserCode in (" + sReturn + ")";
    			}
    			else
    			{
    				sReturn = " or FUserCode in (" + sReturn + ")";
    			}
    		}
    		
        	return sReturn;
    	}
    	catch(Exception ye)
    	{
    		throw new YssException("根据受托人获取权限继承委托人列表出错：" + ye.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    }
    

    /**
     * 20120910 added by liubo.Story 2737
     * 获取用户继承到的权限，返回给前台。在权限设置中，将用户本身不拥有，从其他用户中继承到的权限，以灰色的形式显示
     */
    public String getRightsInheritanced(String sRequest) throws YssException {
        String strSql = "";
        String strReturn = " ";
        ResultSet rs = null;
        java.util.Date dCurrentDate = new Date();
        StringBuffer buf = new StringBuffer();
        String strSqlTemp = "";		//用于拼接获取继承权限的SQL语句
        
        
        try {
        	
            if (sRequest.split("\t").length < 2)
            {
            	return "";
            }
            String sRequestUserCode = sRequest.split("\t")[0];
            String sCurrAssetGroupCode = sRequest.split("\t")[1];
            
        	userCode  = sRequestUserCode;
        	String strSqlPortInher = getInheritedRights(sCurrAssetGroupCode,"port","a","SetUserRight");		//存储“获取用户继承组合级权限”的SQL语句
        	//String strSqlGroupInher = getInheritedRights(sCurrAssetGroupCode,"group","a","SetUserRight");	//存储“获取用户继承组合群级权限”的SQL语句//modified by yeshenghong 2917 remove group right type 20130120
        	String strSqlPublicInher = getInheritedRights(sCurrAssetGroupCode,"public","a","SetUserRight");	//存储“获取用户继承公共级权限”的SQL语句
        	
        	if (strSqlPortInher != null && strSqlPortInher.length() > 3)	//判断是否有获取到有效的继承组合级权限的SQL语句，有则进行拼接
        	{
        		strSqlTemp = strSqlPortInher.substring(3,strSqlPortInher.length());
        	}
        	//modified by yeshenghong 2917 remove group right type 20130120
//        	if (strSqlTemp.length() < 3)	//判断有无进行拼接有效的进行获取组合级权限的SQL语句。有则将获取组合群级继承权限的SQL语句进行拼接，无则直接赋予
//        	{
//        		if (strSqlGroupInher != null && strSqlGroupInher.length() > 3)
//        		{
//        			strSqlTemp = strSqlGroupInher.substring(3,strSqlGroupInher.length());
//        		}
//        	}
//        	else
//        	{
//        		strSqlTemp = strSqlTemp + strSqlGroupInher;
//        	}
        	//-------------------------------------end
        	if (strSqlTemp.length() < 3)//判断有无进行拼接有效的进行获取组合级、组合级权限的SQL语句。有则将获取公共级继承权限的SQL语句进行拼接，无则直接赋予
        	{
        		if (strSqlPublicInher != null && strSqlPublicInher.length() > 3)
        		{
        			strSqlTemp = strSqlPublicInher.substring(3,strSqlPublicInher.length());
        		}
        	}
        	else
        	{
        		strSqlTemp = strSqlTemp + strSqlPublicInher;
        	}
        	
        	if (strSqlTemp.length() > 3)
        	{
        		strSqlTemp = " And (" + strSqlTemp + ")";
        	}
        	
        	String sRolesInherRights = getTrustors(userCode,"SetUserRight");
        	String sPortRolesInher = getInheritedRights(sCurrAssetGroupCode,"port","","SetUserRight");
        	//String sGroupRolesInher = getInheritedRights(sCurrAssetGroupCode,"group","","SetUserRight");//modified by yeshenghong 2917 remove group right type 20130120
        	
        	if (!sPortRolesInher.trim().equals(""))
        	{
        		sPortRolesInher = " or (FRightType = 'port' and (2=1 " + sPortRolesInher + "))";//modified by yeshenghong 权限继承显示错误 20130126
        	}

//        	if (!sGroupRolesInher.trim().equals("")) //modified by yeshenghong 2917 remove group right type 20130120
//        	{
//        		sGroupRolesInher = " or (FRightType = 'group' and (1=1 " + sPortRolesInher + "))";
//        	}
        	
            strSql = "select distinct a.FRightCode,a.FAssetGroupCode,a.FPortCode,a.fRightType,a.FOperTypes " +
//        	strSql = "select distinct b.FBarGroupCode,a.FAssetGroupCode,a.FPortCode,a.fRightType,a.FOperTypes " +
            		 " from (select * from Tb_Sys_Userright where FRightType in  ('public','port')) a " +//modified by yeshenghong 2917 remove group right type 20130120
            		 " join (select * from Tb_Fun_MenuBar) b on a.frightcode = b.FbarCode " +
            		 " left join tb_sys_perinheritance c on a.FUserCode = c.ftrustor  " +
            		 " where a.FRightInd = 'Right' " +
            		 " and c.fstartdate <= " + dbl.sqlDate(dCurrentDate) + " and c.fenddate >= " + dbl.sqlDate(dCurrentDate) + " " +
            		 " and c.ftrustee like '%" + sRequestUserCode + "%' and c.FCheckState = 1" +
            		 " " + strSqlTemp +
                     //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                     //并上权限继承中委托人的角色权限
                     //-----------------------------------
            		 " UNION All " +
                     " select distinct d.Frightcode,c.FAssetGroupCode,c.FPortCode,c.fRightType,d.FOperTypes " +
					 " from (select * " +
					 " from tb_sys_userright " +
					 " where (FRightInd = 'Role' " +  (sRolesInherRights.trim().equals("") ? " and 1 = 2" : sRolesInherRights) +  " AND " +
					 " (FRightType in ('public','datainterface','report') " +
					 " " + sPortRolesInher
//					 sGroupRolesInher //modified by yeshenghong 2917 remove group right type 20130120
					 + "))) c " +
					 " join (select * from tb_sys_roleright) d on c.frightcode = " +
					 " d.frolecode " +
					 " join (select * from tb_fun_menubar) f on d.frightcode = " +
					 " f.fbarcode " +
					 " and instr(f.frighttype, " +
					 " c.frighttype) > 0 " ;
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("fRightType")).append("\t");
                buf.append(rs.getString("FRightCode")).append("\t");
                buf.append(rs.getString("FAssetGroupCode")).append("\t");
                buf.append(rs.getString("FPortCode")).append("\t");
                buf.append(rs.getString("FOperTypes")).append("\f\f");
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }

            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户继承权限出错：" +  e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
}
