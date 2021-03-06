package com.yss.main.cusreport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.syssetting.RightBean;
import com.yss.serve.cusreport.CusReportServer;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;

public class RepGroupSetBean
    extends BaseDataSettingBean implements
    IDataSetting {

    private String RepGrpCode = "";
    private String RepGrpName = "";
    private String ParentCode = "";
    private String ParentName = "";
    private int OrderCode;
    private String Desc = "";
    private String SubRepCodes = "";
    private String sRoleCode = "";

    private String oldRepGrpCode = "";
    private int oldOrderCode;
    
    RightBean right = new RightBean();
    
    /**shashijie 2012-11-12 STORY 3187 服务器路径 */
	private final String path = YssUtil.getPath(CusReportServer.requestPath) 
			+ "WEB-INF" + File.separator + "baobiaopiliangdaochu.xml";
	/**end shashijie 2012-11-12 STORY */
    
    public RepGroupSetBean() {
    }

    /**
     * 报表名
     * author fanghaoln edittime:20091209
     * @return 报表名的词汇
     * @throws YssException
     *  A  : MS00794 QDV4华夏2009年11月02日01_A 
     */
    public String getListViewData1() throws YssException{
    	VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        String sVocStr = vocabulary.getVoc(YssCons.YSS_REP_REPORTNAME);//调用查词汇方法
        return "voc" + sVocStr;//返回报表名的词汇
    }
    //------------------------------end MS00794--------------------------------------------------------
    /** fanghaoln 20100108  A  : MS00794 QDV4华夏2009年11月02日01_A 
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException{
        String strSql = "";//SQL语句
        ResultSet rs = null;//结果集
        String result = "";//返回组合代码组合名称组合简称
        try {
           
                strSql = "select FportCode,FportName,FportShortName from "+pub.yssGetTableName("Tb_Para_Portfolio")+" where 1=1" ;
                if(this.RepGrpName.length()>1){
                	strSql=strSql+" and FportName='"+this.RepGrpName+"'";
                }else if(this.RepGrpCode.length()>1){
                	strSql=strSql+" and FportCode='"+this.RepGrpCode+"'";
                }else{
                	strSql=strSql+"and 1=2";
                }
                rs = dbl.openResultSet(strSql);//执行SQL语句 
                while (rs.next()) {
                	result = result+rs.getString("FportCode") + "\t";//组合代码
                	result = result+rs.getString("FportName") + "\t";//组合名称
                	result = result+rs.getString("FportShortName") + "\t";//组合简称
                }
                dbl.closeResultSetFinal(rs);//关闭游标
        } catch (Exception e) {
            throw new YssException("获取词汇对照信息出错", e);
        }
        return result;
    }

    /**
     * getListViewData3
     * 用户报表权限
     * ====================2009年5月修改=======================
     * BugNO  : MS00010-QDV4赢时胜（上海）2009年02月01日10_A
     * 修改原因：此类中存在游标未关闭影响了权限明细到组合的修改
     * modify by sunkey、jiangjin、wangzuochun--关闭游标
     * =======================================================
     * @return String
     */
    public String getListViewData3() throws YssException {
        //-------------------------彭鹏 2008.3.5 BUG0000069 给用户设置角色出错-----------------------------//
        String strSql = "";
        String strSql1 = "";
        String strSql2 = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {
            strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_Group") + " a" +
                " left join(select FRepGrpCode,FRepGrpName from " +
                pub.yssGetTableName("Tb_Rep_Group") +
                ") b on a.FParentCode = b.FRepGrpCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where a.FCheckState <> 2 order by a.FOrderCode"; //by leeyu 080829
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setResultSetAttr(rs);
                strSql1 =
                    "select FRightCode from TB_SYS_USERRIGHT where FUserCode = " +
                    dbl.sqlString(pub.getUserCode()) +
                    " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " and FRightInd = 'Role'";
                rs1 = dbl.openResultSet(strSql1);
                this.checkStateId = 0;
                while (rs1.next()) {
                    strSql2 =
                        "select FRightCode from TB_SYS_ROLERIGHT where FRoleCode = " +
                        dbl.sqlString(rs1.getString("FRightCode")) +
                        " and FOperTypes = 'Role' and FRightCode = " +
                        dbl.sqlString(this.RepGrpCode);
                    rs2 = dbl.openResultSet(strSql2);
                    if (rs2.next()) {
                        this.checkStateId = 1;
                    } else {
                        this.checkStateId = 0;
                    }
                    if (this.checkStateId == 1) {
                        break;
                    }
                    //2009.05.31 蒋锦 添加 关闭游标
                    dbl.closeResultSetFinal(rs2);
                }
                //关闭游标 modify by sunkey 2009.05.31
                dbl.closeResultSetFinal(rs1);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs, rs1, rs2); //close rs 20080716 sj
        }
        //-------------------------------------------------------------------------------//
    }
    
    //------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
    //------ 获取报表角色权限名称
	public String getReportRoleName(String strReportRoleName) throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        String strReportName = "";
        String[] arrReportName = null;
        
        try {
        	arrReportName = strReportRoleName.split(",");
        	
        	for (int i = 0; i < arrReportName.length; i++){
        		
        		strReportName = arrReportName[i];
        		
        		strSql = " select a.*, b.FRoleName from (select * from TB_SYS_ROLERIGHT where FRoleCode = "
        				+ dbl.sqlString(strReportName) + " and FOperTypes = 'Role' ) a " 
        				+ " join (select * from tb_sys_role where FRoleCode = " + dbl.sqlString(strReportName) 
        				+ " ) b on a.FRoleCode = b.FRoleCode";
        		
        		rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    
                	result += rs.getString("FRoleName") + ",";
                	
                }
        		dbl.closeResultSetFinal(rs);
        	}
        	
        	
            
            if (result.length() > 0) {
                return result.substring(0, result.length() - 1);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取报表组角色名称出错", ex);
        }
        finally {
            dbl.closeResultSetFinal(rs);
        }
    } 
    

    /**
     * getListViewData4
     * 角色报表权限
     * @return String
     */
    public String getListViewData4() throws YssException {
        /**shashijie 2012-8-6 STORY 2661 重载全部复制到getRoleRight方法中去了 */
		return getRoleRight(1);
		/**end*/
    }
    
    /**
     * getListViewData5
     * 获得资产代码
     * add by fangjiang 2010.09.07 MS01596 QDV4华夏2010年08月17日01_A 
     * @return String
     */
    public String getListViewData5() throws YssException{
        String strSql = "";//SQL语句
        ResultSet rs = null;//结果集
        String result = "";//返回组合代码组合名称组合简称
        try {
            strSql = "select FAssetCode from "+pub.yssGetTableName("Tb_Para_Portfolio")+" where 1=1" ;
            if(this.RepGrpName.length()>1){
            	strSql += " and FportName='"+this.RepGrpName+"'";
            }else if(this.RepGrpCode.length()>1){
            	strSql += " and FportCode='"+this.RepGrpCode+"'";
            }else{
            	strSql += " and 1=2 ";
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	result = rs.getString("FAssetCode");
            }
            dbl.closeResultSetFinal(rs);//关闭游标
        } catch (Exception e) {
            throw new YssException("获取词汇对照信息出错", e);
        }
        return result;
    }
    
    /**
     * add by fangjiang 2010.11.10 BUG #282 用户的报表权限没有跟随角色权限一起更新 
     * 获得用户的报表权限（包括角色的报表权限）
     * @return String
     */
    public String getTreeViewDataForUser(String temUserCode) throws YssException{
		//------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
    	String strSql = "";
        String strSql1 = "";
        String strSqlGroup = "";
        String strGroupCode = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rsGroup = null;
        ArrayList listGroup = new ArrayList();
		//------------------ #404 ----------------//
        try {
        	//------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
        	strSqlGroup = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	
        	rsGroup = dbl.openResultSet(strSqlGroup);
        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Rep_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}
        	dbl.closeResultSetFinal(rsGroup);
        	
        	for(int i = 0; i < listGroup.size(); i++){
        		
        		strGroupCode = (String)listGroup.get(i);
        		
        		strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName,'[" + strGroupCode + "]' as FGroupCode from " +
        			" Tb_" + strGroupCode + "_Rep_Group a" +
	                " left join(select FRepGrpCode,FRepGrpName from " +
	                " Tb_" + strGroupCode + "_Rep_Group " +
	                ") b on a.FParentCode = b.FRepGrpCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
	                " where a.FCheckState <>2 order by a.FOrderCode";
        		
        		rs = dbl.openResultSet(strSql);
        		
                while (rs.next()) {
                	
                	if (rs.getString("FRepGrpCode").indexOf("[") >= 0){
                    	this.RepGrpCode = rs.getString("FRepGrpCode") + "";
                    }
                    else{
                    	this.RepGrpCode = rs.getString("FGroupCode") + rs.getString("FRepGrpCode") + "";
                    }
                    
                    this.RepGrpName = rs.getString("FRepGrpName") + "";
                    
                    if (rs.getString("FParentCode").indexOf("[") >= 0){
                    	this.ParentCode = rs.getString("FParentCode") + "";
                    }
                    else{
                    	this.ParentCode = rs.getString("FGroupCode") + rs.getString("FParentCode") + "";
                    }
                    
                    
                    this.ParentName = rs.getString("FRepGrpNameParent") + "";
                    if (this.ParentCode.equalsIgnoreCase("[root]")) {
                        this.ParentName = "所有项目";
                    }
                    this.OrderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                      substring(rs.getString(
                                                          "FOrderCode").length() - 3));
                    this.Desc = rs.getString("FDesc") + "";
                    this.SubRepCodes = rs.getString("FSubRepCodes") + "";
                    super.setRecLog(rs);
                    
                    
                    String userCode = "";
                    
                    if (temUserCode != null && temUserCode.trim().length() > 0) { //如果选择了用户，就查询相应用户的权限
                    	userCode = temUserCode;
                    } else { //如果没有选择用户，就默认查询当前登录系统的用户的相应权限
                    	userCode = pub.getUserCode();
                    }
                    
                    
                    strSql1 = " select frightcode from TB_SYS_USERRIGHT where fusercode = " + dbl.sqlString(userCode) 
                    		+ " and frightind = 'Report' and fassetgroupcode = " + dbl.sqlString(strGroupCode)
                    		+ " and frightcode = " + dbl.sqlString(rs.getString("FRepGrpCode"))
                    		+ " union "
                    		+ " select b.frightcode from " 
                    		+ " (select * from TB_SYS_USERRIGHT where fusercode = " + dbl.sqlString(userCode) 
                    		+ " and frightind = 'Role' and  frighttype = 'report' " 
                    		+ " and fassetgroupcode = " + dbl.sqlString(strGroupCode) + ") a"
                    		+ " join (select * from Tb_Sys_Roleright where fopertypes = 'Role' "
                    		+ " and frightcode = " + dbl.sqlString(rs.getString("FRepGrpCode").indexOf("[") >= 0 ? rs.getString("FRepGrpCode") : rs.getString("FGroupCode") + rs.getString("FRepGrpCode"))
                    		+ " ) b on a.frightcode = b.frolecode";
                    
                    rs1 = dbl.openResultSet(strSql1);
                    //如果有记录设置审核状态为1，表示有权限，否则设置为0，无权限
                    if (rs1.next()) {
                        this.checkStateId = 1;
                    } else {             	
                        this.checkStateId = 0;
                    }
                    //重新为属性赋值，本次主要是设置checkstateId，其他属性上面已经设置过了
                    dbl.closeResultSetFinal(rs1);
                    //将数据进行组装
                    result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                }
                dbl.closeResultSetFinal(rs);
        	}
			//------------------ #404 ----------------//
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }

        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs1, rs);
        }
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Rep_Group") +
                " (FRepGrpCode,FRepGrpName,FParentCode,FOrderCode,FDesc,FSubRepCodes,FCheckState,FCreator,FCreateTime)" +
                " values( " + dbl.sqlString(this.RepGrpCode) +
                "," + dbl.sqlString(this.RepGrpName) +
                "," + dbl.sqlString(this.ParentCode) +
                "," +
                dbl.sqlString(dbFun.treeBuildOrderCode(pub.yssGetTableName(
                    "Tb_Rep_Group"),
                "FRepGrpCode",
                this.ParentCode, this.OrderCode)) +
                "," + dbl.sqlString(this.Desc) +
                "," + dbl.sqlString(this.SubRepCodes) +
                ",0" + //Modify by Mao Qiwen  20080806  bug:0000353
                "," + dbl.sqlString(this.creatorCode) +
                "," + dbl.sqlString(this.creatorTime) +
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();

        } catch (Exception e) {
            throw new YssException("新增报表组数据出错", e);
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
        String strSql = "";
        String tmpValue = "";
        if (btOper != YssCons.OP_DEL) {
            //----------------------------------------------------------------------------------------
            //MS00190 QDV4交银施罗德2009年01月15日01_B 2009.02.11方浩
            //传入一false判断是报表组设置就不进行审核判断，就是说不论审核与否都能修改，因为报表组设置根本就没有审核与未审核。
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Rep_Group"),
                                   "FRepGrpCode",
                                   this.RepGrpCode, this.oldRepGrpCode, null, null, false);
            //----------------------------------------------------------------------------------------
        }

        if (this.oldOrderCode != this.OrderCode) {
            strSql = "select FOrderCode from " +
                pub.yssGetTableName("Tb_Rep_Group") +
                " where FOrderCode = '" +
                dbFun.treeBuildOrderCode(pub.yssGetTableName(
                    "Tb_Rep_Group"),
                                         "FRepGrpCode",
                                         this.ParentCode, this.OrderCode) +
                "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("报表组排序号【" + this.OrderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入菜单排序号");
            }
        }

    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false;
        try {
            /*
             strSql = "update " + pub.yssGetTableName("Tb_Rep_Group") +
                 " set FCheckState = " + this.checkStateId +
                 ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
             ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                 "' where FRepGrpCode = " + dbl.sqlString(this.RepGrpCode);
             */
            //重写了SQL语句使删除就是从数据库中删除而不更新到回站里去！MS00190 QDV4交银施罗德2009年01月15日01_B 2009.01.19 方浩
            strSql = "delete from  " + pub.yssGetTableName("Tb_Rep_Group") +
                " where FRepGrpCode = " + dbl.sqlString(this.RepGrpCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除报表组数据出错", e);
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
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Rep_Group") +
                " set FRepGrpCode = " + dbl.sqlString(this.RepGrpCode) +
                ",FRepGrpName = " + dbl.sqlString(this.RepGrpName) +
                ",FParentCode = " + dbl.sqlString(this.ParentCode) +
                ",FOrderCode = " +
                dbl.sqlString(dbFun.treeBuildOrderCode(pub.
                yssGetTableName("Tb_Rep_Group"),
                "FRepGrpCode",
                this.ParentCode, this.OrderCode)) +
                ",FDesc = " + dbl.sqlString(this.Desc) +
                ",FSubRepCodes = " + dbl.sqlString(this.SubRepCodes) +
                ",FCheckstate = 0" + //(pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FRepGrpCode = " + dbl.sqlString(this.oldRepGrpCode);
            //===数据修改，状态不变。也就是 说在报表组设置中不做该组的状态判断。Mao Qiwen  20080806  bug:0000353===//

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();

        } catch (Exception e) {
            throw new YssException("修改报表组设置出错", e);
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
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_Group") + " a" +
                " left join(select FRepGrpCode,FRepGrpName from " +
                pub.yssGetTableName("Tb_Rep_Group") +
                ") b on a.FParentCode = b.FRepGrpCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                //" where FCheckState <> '2'" +      //====Modify by Mao Qiwen  20080806  bug:0000353====//
                " where FCheckState <> 2" + //不能为'2' by leeyu 080829
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setResultSetAttr(rs);
                this.checkStateId = 1; //这里置1,做为前台可以修改的数据 by leeyu 080829 0000436
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }

        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        }
        // 关闭结果集 2009.05.08 wangzuochun
        finally {
            dbl.closeResultSetFinal(rs);
        }
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
        String strSql = "";
        String strSql1 = "";
        String strRepCodes = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_Group") + " a" +
                " left join(select FRepGrpCode,FRepGrpName from " +
                pub.yssGetTableName("Tb_Rep_Group") +
                ") b on a.FParentCode = b.FRepGrpCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                //" where a.FCheckState = 1 order by a.FOrderCode";
                " where a.FCheckState <>2 order by a.FOrderCode"; // by leeyu 080829 BUG:000436
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setResultSetAttr(rs);
                strSql1 =
                    "select FRightCode from TB_SYS_USERRIGHT where FUserCode = " +
                    dbl.sqlString(pub.getUserCode()) +
                    " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    " and FRightCode = " +
                    dbl.sqlString(this.RepGrpCode) +
                    " and FRightInd = 'Report'";
                rs1 = dbl.openResultSet(strSql1);
                if (rs1.next()) {
                    this.checkStateId = 1;
                } else {
                    this.checkStateId = 0;
                }
                //2009.05.31 蒋锦 添加 关闭游标
                dbl.closeResultSetFinal(rs1);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }

        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        }
        // 关闭结果集 2009.05.08 wangzuochun
        finally {
            dbl.closeResultSetFinal(rs1, rs);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.RepGrpCode.trim()).append("\t");
        buf.append(this.RepGrpName.trim()).append("\t");
        buf.append(this.ParentCode.trim()).append("\t");
        buf.append(this.OrderCode).append("\t");
        buf.append(this.ParentName.trim()).append("\t");
        buf.append(this.SubRepCodes.trim()).append("\t");
        buf.append(this.Desc.trim()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    String sCurrentUser = "";
    
    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {   

        String strSql = "", sReturn = "", sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String fsubreps[] = null;
        String fsubrep = "'all',";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
        	right.setYssPub(pub);
        	right.setUserCode(pub.getUserCode());
        	
        	//-------------------------彭鹏 2008.3.5 BUG0000069 给用户设置角色出错-----------------------------//
            if (sType.split("!!")[0].equalsIgnoreCase("Role")) {
                sRoleCode = sType.split("!!")[1];
                //------ add by wangzuochun 2010.07.28  MS01494  当用户代码为中文时即使有权限也因权限判断问题无法登录    QDV4赢时胜上海2010年07月27日10_B   
                sRoleCode = new String(sRoleCode.getBytes("ISO-8859-1"),"GBK");
                //----------------------------MS01494------------------------------//
                return getListViewData4();
            }
            //----------------------------------------------------------------------------------------------//
            /**shashijie 2012-8-6 STORY 2661 重载获取角色权限 */
            else if (sType.split("!!")[0].equalsIgnoreCase("Role2")) {
	            sRoleCode = sType.split("!!")[1];
	            sRoleCode = new String(sRoleCode.getBytes("ISO-8859-1"),"GBK");
	            return getRoleRight(3);
            }
			/**end*/
            //------ add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
			else if (sType.split("!!")[0].equalsIgnoreCase("ReportRole")) {
                sRoleCode = sType.split("!!")[1];
                sRoleCode = new String(sRoleCode.getBytes("ISO-8859-1"),"GBK");
                return getReportRoleName(sRoleCode);
            }
			//------------------ #404 ----------------//
            //by xuxuming, 2009.08.03. 根据 所选择的用户，查询其 权限
            else if (sType.split("!!")[0].equalsIgnoreCase("SelUserCode")) {
                String temUserCode = sType.split("!!")[1];           
                //------ add by wangzuochun 2010.07.28  MS01494  当用户代码为中文时即使有权限也因权限判断问题无法登录    QDV4赢时胜上海2010年07月27日10_B   
                temUserCode = new String(temUserCode.getBytes("ISO-8859-1"),"GBK");
                //----------------------------MS01494------------------------------//
                return this.getTreeViewDataForSelUser(temUserCode);
            }

	    	//20121129 modified by liubo.Story #2737
	    	//在前台的权限设置界面，报表权限的显示需要带入继承到的报表权限，包括用户赋予的与角色赋予的
    		//===============================
            else if (sType.split("!!")[0].equalsIgnoreCase("PerInheritance")) {
                sCurrentUser = sType.split("!!")[1];           
                
                return this.getTreeViewGroupData1();
            }
           //==============end===============
            //------ add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
            if (sType.equalsIgnoreCase("checkrepgrpcode")) {
            	checkRepGrpCode();
            }
			//------------------ #404 ----------------//
        	
            if (sType.equalsIgnoreCase("setsubrepcodes")) {
                //给报表组设置子报表
                sReturn = "false";
                strSql = "update " + pub.yssGetTableName("Tb_Rep_Group") +
                    " set FSubRepCodes = " +
                    dbl.sqlString(this.SubRepCodes) +
                    " where FRepGrpCode = " +
                    dbl.sqlString(this.RepGrpCode);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
                sReturn = "true";
                return sReturn;
            }
            if (sType.equalsIgnoreCase("getsubrepcodes")) {
                //获取报表组的子报表
                sHeader = "明细报表代码\t明细报表名称";
                if (!this.SubRepCodes.equalsIgnoreCase("")) {
                    fsubreps = this.SubRepCodes.split(",");
                    for (int i = 0; i < fsubreps.length; i++) {
                        fsubrep = fsubrep + dbl.sqlString(fsubreps[i]) +
                            ",";
                    }
                }
                if (fsubrep.length() > 0) {
                    fsubrep = YssFun.left(fsubrep, fsubrep.length() - 1);
                }

                strSql = "select y.* from " +
                    "(select FCusRepCode,FCheckState from " +
                    pub.yssGetTableName("Tb_Rep_Custom") +
                    " where FCheckState <> 2 and FCusRepCode in ("
                    + fsubrep +
                    " ) group by FCusRepCode,FCheckState " +
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

                    RepCustomBean repcustom = new RepCustomBean();
                    repcustom.setYssPub(pub);
                    repcustom.setResultSetAttr(rs);

                    bufAll.append(repcustom.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
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
            if (sType.equalsIgnoreCase("getrepcodes")) {
                //获取有权限的报表组数据
                strSql = "select distinct x.* from (select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " + //modify huangqirong 2012-11-30 bug #6475 添加去重复 distinct  
                    pub.yssGetTableName("Tb_Rep_Group") + " a" +
                    " left join(select FRepGrpCode,FRepGrpName from " +
                    pub.yssGetTableName("Tb_Rep_Group") +
                    ") b on a.FParentCode = b.FRepGrpCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode ) x" +
                    " join" +
                    " (select FRightCode from tb_sys_userright  where (fusercode = " +
                    //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
                    //获取报表组时需要包含权限继承继承下来的报表组，包括委托人赋予的和角色赋予的
                    //==================================
                    dbl.sqlString(pub.getUserCode()) + right.getInheritedRights(pub.getAssetGroupCode(), "") + ")" +
                    " and frightind = 'Report' and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    /**shashijie 2012-6-4 STORY 2661 添加角色权限判断 */
					" Union" +
					" Select B1.Frightcode" +
					" From (Select a.Frightcode" +
					" From Tb_Sys_Userright a" +
					" Where a.Frightind = 'Role'" +
					" And (a.Fusercode = "+dbl.sqlString(pub.getUserCode())+ right.getInheritedRights(pub.getAssetGroupCode(), "a") + ")" +
                    //===============end===================
					" And a.Frighttype = 'port') A1" +
					" Join (Select b.Frolecode, b.Frightcode" +
					" From Tb_Sys_Roleright b" +
					" Where b.Fopertypes = 'Role') B1 On A1.Frightcode = B1.Frolecode "+
                    ") y on (x.FRepGrpCode = y.FRightCode" +
                    " Or '[" + pub.getAssetGroupCode() + "]' || x.Frepgrpcode = y.Frightcode) "+ //modified by yeshenghong BUG7185 20130227
                    " order by x.FOrderCode" 
                    /**end*/
                    ;
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    setResultSetAttr(rs);
                    this.checkStateId = 1; //by leeyu 000436 080829
                    sReturn += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                }
                if (sReturn.length() > 2) {
                    return sReturn.substring(0, sReturn.length() - 2);
                } else {
                    return "";
                }

            }
            // add by fangjiang 2010.09.07 MS01596 QDV4华夏2010年08月17日01_A 
            if (sType.equalsIgnoreCase("assetCode")) {
                return getListViewData5();
            }
            //----------------
            //add by fangjiang 2010.11.10 BUG #282 用户的报表权限没有跟随角色权限一起更新
            if (sType.split("!!")[0].equalsIgnoreCase("UserCode")) {
                String temUserCode = sType.split("!!")[1];           
                temUserCode = new String(temUserCode.getBytes("ISO-8859-1"),"GBK");
                return this.getTreeViewDataForUser(temUserCode);
            }
            //------------------------
            
            /**shashijie 2012-11-9 STORY 3187 保存跨组合群导出设置*/
			if (sType.equalsIgnoreCase("saveExpOption")) {
				return saveExpOption();
			}
			/**end shashijie 2012-11-9 STORY */
            
			/**shashijie 2012-11-12 STORY 3187 根据XML的节点获取其值*/
			if (sType.equalsIgnoreCase("getNodeValue")) {
				return getNodeValue();
			}
			/**end shashijie 2012-11-12 STORY */
			
			/**shashijie 2012-11-13 STORY 3187 根据key报表代码统一从常用字汇中取数*/
			if (sType.equalsIgnoreCase("ReportCodeExp")) {
				return getReportCodeExpValue(sType);
			}
			/**end shashijie 2012-11-13 STORY */
			
            return sReturn;
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.endTransFinal(conn, bTrans);
            //2009.05.31 蒋锦 修改 关闭游标
            dbl.closeResultSetFinal(rs);
        }

    }

    /**shashijie 2012-11-13 STORY 3187 统一从常用字汇中取数 */
	private String getReportCodeExpValue(String sType) throws YssException {
		String sReturn = "";
		if (sType==null || sType.trim().equals("")) {
			return sReturn;
		}
		
        ResultSet rs = null;//结果集
        
        try {
            String query = getVocabularyQuery(sType);
            rs = dbl.openResultSet(query);
            while (rs.next()) {
            	sReturn += rs.getString("FVocCode") + "--" + rs.getString("FVocName") + ",";
            }
            sReturn = YssFun.getSubString(sReturn);
        } catch (Exception e) {
            throw new YssException("获取词汇信息出错", e);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }
		return sReturn;
	}

	/**shashijie 2012-11-13 STORY 3187 获取词汇代码 SQL */
	private String getVocabularyQuery(String sType) {
		String sqlString = "Select b.* From " +
			" Tb_Fun_Vocabularytype a " +
			" Join Tb_Fun_Vocabulary b On a.Fvoctypecode = b.Fvoctypecode" +
			" Where a.Fvoctypecode = "+dbl.sqlString(sType)+
			" And a.FCheckState = 1 ";
		return sqlString;
	}

	/**shashijie 2012-11-12 STORY 3187 根据XML的节点获取其值*/
	private String getNodeValue() throws YssException {
		String sReturn = "";
		try {
			//读取文件的具体某个节点的值
			sReturn = xmlReadDemo(this.SubRepCodes);
		} catch (Exception e) {
			throw new YssException("根据XML的节点获取其值出错!", e);
		} finally {
			
		}
		return sReturn;
	}

	/**shashijie 2012-11-12 STORY 3187  读取文件的具体某个节点的值  */ 
	private String xmlReadDemo(String nodeKey) throws YssException {
		String sReturn = "";
		Document doc = load(path);
		if (doc==null) {
			return sReturn;
		}
		/** 先用xpath查找所有div节点,并获取它的name属性值 */
		List list = doc.selectNodes("/config/div");
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element ftpElement = (Element) it.next();
			String ftp_name = ftpElement.attribute("name").getValue();
			if (nodeKey.equals(ftp_name)) {
				sReturn += ftpElement.elementText("path")+",";
				sReturn += ftpElement.elementText("pathName")+",";
				sReturn += ftpElement.elementText("excel")+",";
				sReturn += ftpElement.elementText("pdf");
			}
		}
		return sReturn;
	}

	/**shashijie 2012-11-12 STORY 3187 载入一个xml文档 ,成功返回Document对象，失败返回null */
	private Document load(String filename) {
		Document document = null;
		try {
			SAXReader saxReader = new SAXReader();
			document = saxReader.read(new File(filename));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return document;
	}
	   
	/**shashijie 2012-11-9 STORY 3187 保存跨组合群导出设置 */
	private String saveExpOption() throws YssException {
		String sReturn = "";
		try {
			//处理String字符串转换成XML文件字符串
			String xmlValue = xmlWriteDemoByString(this.SubRepCodes);
			//将xml格式的字符串转换成Document对象
			Document doc = string2XmlFile(xmlValue);
			//将文件生成到classes文件夹里
			boolean falg = doc2XmlFile(doc, path);
			if (falg) {
				sReturn = "true";
			} else {
				sReturn = "false";
			}
		} catch (Exception e) {
			sReturn = "false";
			throw new YssException("保存跨组合群导出设置出错!", e);
		} finally {
			
		}
		return sReturn;
	}
	
	/**shashijie 2012-11-9 STORY 3187  将String转换为xml文件的字符串  */
	private String xmlWriteDemoByString(String value) throws YssException {
		if (value == null || value.trim().equals("")) {
			return "";
		}
		//xml格式标题
		String styleValue = "<?xml version='1.0' encoding='GB2312'?>\r\n";//导出的xml文件内容
		styleValue += getAddAttribute("config","",""); 
		
		String assetGroupCodeName = "";//组合群代码名称
		String groupCodeName = "";//组合代码名称
		String reporCodeName = "";//报表代码名称
		
		boolean portCodemode = false;//组合代码名称获取方式比较复杂,这里定义一个标示
		boolean reportCodemode = false;//报表代码名称获取方式比较复杂,这里定义一个标示
		String[] Asset = value.split("<--Asset-->");
		//组合群
		for (int i = 0; i < Asset.length; i++) {
			portCodemode = true;
			assetGroupCodeName = Asset[i].split(",")[0];
			
			//组合
			String[] group = Asset[i].split("<--Group-->");
			for (int j = 0; j < group.length; j++) {
				reportCodemode = true;
				//若是每次循环组合群后第一次循环组合,组合代码在第二个下标,否则都是第一个下表为组合代码
				if (portCodemode) {
					groupCodeName = group[j].split(",")[1];
				} else {
					groupCodeName = group[j].split(",")[0];
				}
				
				//每行数据
				String[] row = group[j].split("\r\n");
				for (int k = 0; k < row.length; k++) {
					//导出路径
					String path = "";
					//导出文件名
					String pathName = "";
					//导出格式
					String excel = "";
					String pdf = "";
					
					//若是每次循环组合群后第一次循环组合再第一次循环每行数据,报表代码在第三个下标
					//若是每次循环组合群后第二次循环组合再第一次循环每行数据,报表代码在第二个下标,否则都是第一个下表为报表代码
					if (portCodemode) {
						reporCodeName = row[k].split(",")[2];
						path = row[k].split(",")[3];
						pathName = row[k].split(",")[4];
						excel = row[k].split(",")[5].split("-")[1];
						pdf = row[k].split(",")[6].split("-")[1];
					} else if (reportCodemode) {
						reporCodeName = row[k].split(",")[1];
						path = row[k].split(",")[2];
						pathName = row[k].split(",")[3];
						excel = row[k].split(",")[4].split("-")[1];
						pdf = row[k].split(",")[5].split("-")[1];
					} else {
						reporCodeName = row[k].split(",")[0];
						path = row[k].split(",")[1];
						pathName = row[k].split(",")[2];
						excel = row[k].split(",")[3].split("-")[1];
						pdf = row[k].split(",")[4].split("-")[1];
					}
					//组合群+组合+报表(节点)
					String name = assetGroupCodeName.trim()+","+groupCodeName.trim()+","+reporCodeName.trim();
					styleValue += getAddAttribute("div","\t",name);
					
					//属性值
					styleValue += getAddNode("path",path,"\t\t");
					styleValue += getAddNode("pathName",pathName,"\t\t");
					styleValue += getAddNode("excel",excel,"\t\t");
					styleValue += getAddNode("pdf",pdf,"\t\t");
					
					styleValue += getAddAttribute("/div","\t","");
					portCodemode = false;
					reportCodemode = false;
				}
				portCodemode = false;
				reportCodemode = false;
			}
		}
		styleValue += getAddAttribute("/config","","");
		return styleValue;
	}

	/**shashijie 2012-11-12 STORY 3187 拼接子节点*/
	private String getAddNode(String node, String value,String empty) {
		//转义字符
		String change = value.replace("<", "&lt;").replace(">", "&gt;");
		String nodevalue = "<"+node+">" + change + "</"+node+">\r\n";
		nodevalue = empty + nodevalue;
		return nodevalue;
	}

	/**shashijie 2012-11-12 STORY 3187 拼接属性节点,empty表示节点前空格,nameAtt表示属性的值*/
	private String getAddAttribute(String Attribute,String empty,String nameAtt) {
		String node = "";
		if (nameAtt!=null && !nameAtt.trim().equals("")) {
			node = "<"+Attribute+" name = '"+nameAtt+"' >\r\n";
		} else {
			node = "<"+Attribute+">\r\n";
		}
		node = empty + node;
		
		return node;
	}

	/**shashijie 2012-11-9 STORY 3187  将xml格式的字符串保存为本地文件，如果字符串格式不符合xml规则，则返回失败 */
	private Document string2XmlFile(String str) throws YssException {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(str);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return doc;
	}
	   
	/**shashijie 2012-11-9 STORY 3187 将Document对象保存为一个xml文件到本地 */
	private boolean doc2XmlFile(Document document, String filename) throws YssException {
		boolean flag = true;
		XMLWriter writer = null;
		try {
			/* 将document中的内容写入文件中 */
			// 默认为UTF-8格式，指定为"GB2312"
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			writer = new XMLWriter(new FileWriter(new File(filename)), format);
			writer.write(document);
			writer.close();
		} catch (Exception ex) {
			flag = false;
			ex.printStackTrace();
		} finally{
			if (writer!=null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	

	/**
     * 根据用户代码查询用户权限
     * 在管理页面 选择 用户 后，根据 所选择 用户 查询 其 权限
     * 2008.08.03, Add by xuxuming MS00589:QDV4赢时胜（上海）2009年7月24日08_B
     * @param temUserCode String 用户代码
     * @return String 根据用户 得到其权限
     */
    public String getTreeViewDataForSelUser(String temUserCode) throws YssException {
        //------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
		String strSql = "";
        String strSql1 = "";
        String result = "";
        String strSqlGroup = "";
        String strGroupCode = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rsGroup = null;
        ArrayList listGroup = new ArrayList();
        try {
        	
        	strSqlGroup = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	
        	rsGroup = dbl.openResultSet(strSqlGroup);
        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Rep_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}
        	dbl.closeResultSetFinal(rsGroup);
        	
        	for(int i = 0; i < listGroup.size(); i++){
        		
        		strGroupCode = (String)listGroup.get(i);
        		
        		strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName,'[" + strGroupCode + "]' as FGroupCode from " +
        			" Tb_" + strGroupCode + "_Rep_Group a" +
	                " left join(select FRepGrpCode,FRepGrpName from " +
	                " Tb_" + strGroupCode + "_Rep_Group " +
	                ") b on a.FParentCode = b.FRepGrpCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
	                " where a.FCheckState <>2 order by a.FOrderCode";
        		
        		rs = dbl.openResultSet(strSql);
        		
                while (rs.next()) {
                	
                	if (rs.getString("FRepGrpCode").indexOf("[") >= 0){
                    	this.RepGrpCode = rs.getString("FRepGrpCode") + "";
                    }
                    else{
                    	//edit by songjie 2011.12.13 BUG 3286 QDV4赢时胜(测试)2011年12月1日01_B
                    	this.RepGrpCode = rs.getString("FGroupCode").trim() + rs.getString("FRepGrpCode") + "";
                    }
                    
                    this.RepGrpName = rs.getString("FRepGrpName") + "";
                    
                    if (rs.getString("FParentCode").indexOf("[") >= 0){
                    	this.ParentCode = rs.getString("FParentCode") + "";
                    }
                    else{
                    	this.ParentCode = rs.getString("FGroupCode") + rs.getString("FParentCode") + "";
                    }
                    
                    
                    this.ParentName = rs.getString("FRepGrpNameParent") + "";
                    if (this.ParentCode.equalsIgnoreCase("[root]")) {
                        this.ParentName = "所有项目";
                    }
                    this.OrderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                      substring(rs.getString(
                                                          "FOrderCode").length() - 3));
                    this.Desc = rs.getString("FDesc") + "";
                    this.SubRepCodes = rs.getString("FSubRepCodes") + "";
                    super.setRecLog(rs);
                    
                    
                    //2.从用户权限表中查询用户报表权限
                    strSql1 = "select FRightCode from TB_SYS_USERRIGHT where FUserCode = ";
                    if (temUserCode != null && temUserCode.trim().length() > 0) { //如果 选择了 用户，就 查询 相应用户的权限
                        strSql1 += dbl.sqlString(temUserCode);
                    } else { //如果 没有选择用户，就默认 查询 当前登录系统 的用户 的 相应权限
                        dbl.sqlString(pub.getUserCode());
                    }
                    //匹配当前登录组合群和当前外围游标里的报表
                    strSql1 += " and FAssetGroupCode = " + dbl.sqlString(strGroupCode) +
                        " and FRightCode = " + dbl.sqlString(rs.getString("FRepGrpCode")) +
                        " and FRightInd = 'Report'";

                    rs1 = dbl.openResultSet(strSql1);
                    //如果有记录设置审核状态为1，表示有权限，否则设置为0，无权限
                    if (rs1.next()) {
                        this.checkStateId = 1;
                    } else {
                        this.checkStateId = 0;
                    }
                    //重新为属性赋值，本次主要是设置checkstateId，其他属性上面已经设置过了
                    dbl.closeResultSetFinal(rs1);
                    //将数据进行组装
                    result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                }
                dbl.closeResultSetFinal(rs);
        	}
        	//------------------ #404 ----------------//
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }

        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs1, rs);
        }

    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.RepGrpCode = reqAry[0];
            this.RepGrpName = reqAry[1];
            this.ParentCode = reqAry[2];
            this.OrderCode = Integer.parseInt(reqAry[3]);
            this.SubRepCodes = reqAry[4];
            this.Desc = reqAry[5];
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.oldRepGrpCode = reqAry[7];
            this.oldOrderCode = Integer.parseInt(reqAry[8]);
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析报表组设置请求出错", e);
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

    public void setResultSetAttr(ResultSet rs) throws SQLException,
        YssException {
        this.RepGrpCode = rs.getString("FRepGrpCode") + "";
        this.RepGrpName = rs.getString("FRepGrpName") + "";
        this.ParentCode = rs.getString("FParentCode") + "";
        this.ParentName = rs.getString("FRepGrpNameParent") + "";
        if (this.ParentCode.equalsIgnoreCase("[root]")) {
            this.ParentName = "所有项目";
        }
        this.OrderCode = Integer.parseInt(rs.getString("FOrderCode").
                                          substring(rs.getString(
                                              "FOrderCode").length() - 3));
        this.Desc = rs.getString("FDesc") + "";
        this.SubRepCodes = rs.getString("FSubRepCodes") + "";
        super.setRecLog(rs);

    }

    public String getUserReport(String userCode, String assetGroupCode) throws
        YssException {
        String strSql = "";
        String strSql1 = "";
        String strRepCodes = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        try {
            strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Rep_Group") + " a" +
                " left join(select FRepGrpCode,FRepGrpName from " +
                pub.yssGetTableName("Tb_Rep_Group") +
                ") b on a.FParentCode = b.FRepGrpCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                //" where a.FCheckState = 1 order by a.FOrderCode";
                " where a.FCheckState <> 2 order by a.FOrderCode"; //by leeyu 080829
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                setResultSetAttr(rs);
                strSql1 =
                    "select FRightCode from TB_SYS_USERRIGHT where FUserCode = " +
                    dbl.sqlString(userCode) + " and FAssetGroupCode = " +
                    dbl.sqlString(assetGroupCode) + " and FRightCode = " +
                    dbl.sqlString(this.RepGrpCode) +
                    " and FRightInd = 'Report'";
                rs1 = dbl.openResultSet(strSql1);
                if (rs1.next()) {
                    this.checkStateId = 1;
                } else {
                    this.checkStateId = 0;
                }
                //2009.05.31 蒋锦 添加 关闭游标
                dbl.closeResultSetFinal(rs1);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        }
        // 关闭结果集 2009.05.08 wangzuochun
        finally {
            dbl.closeResultSetFinal(rs1, rs);
        }

    }
    
	//------ add by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
    //------ 检查当前组合群报表特定数据是否存在，不存在则插入；
    public void checkRepGrpCode() throws YssException{
		String strSql = "";
		ResultSet rs = null;
    	
		try{
			
			if (dbl.yssTableExist(pub.yssGetTableName("Tb_Rep_Group"))){
				strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Group")
						+ " where FRepGrpCode = '[" + pub.getAssetGroupCode() + "]'";
				rs = dbl.openResultSet(strSql);
				
				if(!rs.next()){
					strSql = " insert into " + pub.yssGetTableName("Tb_Rep_Group") +
		                " (FRepGrpCode,FRepGrpName,FParentCode,FOrderCode,FCheckState,FCreator,FCreateTime)" +
		                " values( " + dbl.sqlString("[" + pub.getAssetGroupCode() + "]") +
		                "," + dbl.sqlString(pub.getAssetGroupCode() + "组合群报表组") +
		                "," + dbl.sqlString("[root]") +
		                "," + dbl.sqlString("000") +
		                ",1" + 
		                "," + dbl.sqlString("admin") +
		                "," + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
		                ")";
	   		
					dbl.executeSql(strSql);
				}
				dbl.closeResultSetFinal(rs);
			}
    	
		} catch (Exception ex) {
			throw new YssException("插入报表租数据出错", ex);
		}
	    finally {
	        dbl.closeResultSetFinal(rs);
	    }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }


	//20121129 modified by liubo.Story #2737
    //前台在加载权限设置界面时，报表权限部分需要带入继承到的报表权限，包括用户赋予的与角色赋予的
    //继承到的报表权限，在权限设置界面将被打上灰色的勾（与角色中带的报表权限类似）
	//===============================
    public String getTreeViewGroupData1() throws YssException {
    	String strSql = "";
        ResultSet rs = null;
        ResultSet rsmd = null;
        String sReturn = "";
        String sUserList = "";
      
        try {
        	
        	strSql = "Select distinct FTrustor as FTrustor from tb_sys_PerInheritance " +
			 " where FStartDate <=" + dbl.sqlDate(new java.util.Date()) +
			 " and FEndDate >= " + dbl.sqlDate(new java.util.Date()) + 
			 " and FTrustee like '%" + this.sCurrentUser + "%' and FCHECKSTATE = 1";
        	rs = dbl.queryByPreparedStatement(strSql);
        	
        	while(rs.next())
        	{
        		sUserList += rs.getString("FTrustor") + ",";
        	}
            dbl.closeResultSetFinal(rs);
            
            if (sUserList == null || sUserList.trim().equals(""))
            {
            	return " ";
            }
            
            sUserList = sUserList.substring(0,sUserList.length() - 1);

            //20121225 modified by liubo.Bug #6594
            //使用operSql.sqlCodes方法替代以前的dbl.sqlstring方法，避免一个用户继承多个委托人的权限时出问题
            //====================================
            strSql = " select FRightCode from TB_SYS_ROLERIGHT where FRoleCode in " +
            		"(select distinct FRightCode from Tb_Sys_UserRight where FUserCode in (" + operSql.sqlCodes(sUserList) +
            		") ) and FOperTypes = 'Role' union " +
            		" select '[' || FAssetGroupCode || ']' || FRightCode as FRightCode from Tb_Sys_UserRight where FUserCode in (" + operSql.sqlCodes(sUserList) +
            		") and FRightInd = 'Report'";
            //===================end=================
            rsmd = dbl.queryByPreparedStatement(strSql);

            while (rsmd.next()) 
            {
            	sReturn += rsmd.getString("FRightCode") + "\t";
            }
            
            if (sReturn != null && sReturn.trim().length() > 2)
            {
            	sReturn = sReturn.substring(0,sReturn.length() - 1);
            }

            return sReturn;

        }catch (Exception e) {
            throw new YssException("获取继承权限的报表权限出错：" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs,rsmd);
        }
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

  //add by huangqirong 2011-09-20 story #1284
    public String getRepGroups(String assetGroupCode) throws YssException {
    	StringBuffer data=new StringBuffer();
    	String result="";
    	String strSql="";
    	ResultSet rs = null;
    	strSql = "select x.* from (select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
        "Tb_"+assetGroupCode+"_Rep_Group a" +
        " left join(select FRepGrpCode,FRepGrpName from " +
        "Tb_"+assetGroupCode+"_Rep_Group" +
        ") b on a.FParentCode = b.FRepGrpCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode ) x" +
        " join" +
        " (select FRightCode from tb_sys_userright where fusercode = " +
        dbl.sqlString(pub.getUserCode()) +
        " and frightind = 'Report' and FAssetGroupCode = " +
        dbl.sqlString(assetGroupCode) +
        ") y on x.FRepGrpCode = y.FRightCode" +
        " order by x.FOrderCode";
    	try {
    		rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	data.append(rs.getString("FRepGrpCode")).append("\t");
            	data.append(rs.getString("FRepGrpName")).append("\t");
            	data.append(rs.getString("FParentCode")).append("\t");
            	data.append(rs.getString("FSubRepCodes"));
            	//data.append(rs.getString("FOrderCode")).append("\t");
            	//data.append(rs.getString("FDesc")).append("\t");            	
            	data.append(YssCons.YSS_LINESPLITMARK);
            }
            if(data.length()>0)
            	result=data.substring(0,data.toString().length()-2);
		} catch (Exception e) {
			throw new  YssException("获取报表组出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}    	
    	return result;
    }  

    /**shashijie 2012-8-6 STORY 2661 获取角色权限(重载) */
    private String getRoleRight(int checkStateId) throws YssException {
    	String strSql = "";
        String strSqlGroup = "";
        String strSql1 = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rsGroup = null;
        String strGroupCode = "";
        ArrayList listGroup = new ArrayList();
		//------------------ #404 ----------------//
        
        try {
        	
        	//------ modify by wangzuochun 2011.04.15 STORY #404 建议设置角色权限时，角色的报表权限明细到组合群下的报表组
        	strSqlGroup = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	
        	rsGroup = dbl.openResultSet(strSqlGroup);
        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Rep_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}
        	dbl.closeResultSetFinal(rsGroup);
        	
        	for (int i = 0; i < listGroup.size(); i++){
        		
        		strGroupCode = (String)listGroup.get(i);
        		
        		strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName,'[" + strGroupCode + "]' as FGroupCode from " +
	    			" Tb_" + strGroupCode + "_Rep_Group a" +
	                " left join(select FRepGrpCode,FRepGrpName from " +
	                " Tb_" + strGroupCode + "_Rep_Group " +
	                ") b on a.FParentCode = b.FRepGrpCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
	                " where a.FCheckState <>2 order by a.FOrderCode";
        		
        		rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    if (rs.getString("FRepGrpCode").indexOf("[") >= 0){
                    	this.RepGrpCode = rs.getString("FRepGrpCode") + "";
                    }
                    else{
                    	this.RepGrpCode = rs.getString("FGroupCode") + rs.getString("FRepGrpCode") + "";
                    }
                    
                    this.RepGrpName = rs.getString("FRepGrpName") + "";
                    
                    if (rs.getString("FParentCode").indexOf("[") >= 0){
                    	this.ParentCode = rs.getString("FParentCode") + "";
                    }
                    else{
                    	this.ParentCode = rs.getString("FGroupCode") + rs.getString("FParentCode") + "";
                    }
                    
                    
                    this.ParentName = rs.getString("FRepGrpNameParent") + "";
                    if (this.ParentCode.equalsIgnoreCase("[root]")) {
                        this.ParentName = "所有项目";
                    }
                    this.OrderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                      substring(rs.getString(
                                                          "FOrderCode").length() - 3));
                    this.Desc = rs.getString("FDesc") + "";
                    this.SubRepCodes = rs.getString("FSubRepCodes") + "";
                    super.setRecLog(rs);
                    
                    
                    // 用户有可能 有多个角色，以下改动 为了 查询 多角色用户 的角色权限 by xuxuming, 2009.08.03
                    // MS00589 报表权限设置角色有误 QDV4赢时胜（上海）2009年7月24日08_B
                    strSql1 = "select FRightCode from TB_SYS_ROLERIGHT where FRoleCode in (" + operSql.sqlCodes(this.sRoleCode) +
                        ") and FOperTypes = 'Role' and FRightCode = " + dbl.sqlString(this.RepGrpCode);

                    rs1 = dbl.openResultSet(strSql1);
                    if (rs1.next()) {
                    	/**shashijie 2012-8-3 STORY 2661 */
                    	//this.checkStateId = 1;
                    	this.checkStateId = checkStateId;
						/**end*/
                    } else {
                        this.checkStateId = 0;
                    }
                    //2009.05.31 蒋锦 添加 关闭游标
                    dbl.closeResultSetFinal(rs1);
                    result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                }
        		dbl.closeResultSetFinal(rs);
        	}
        	
            
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取报表组数据出错", ex);
        }
        // 关闭结果集 2009.05.08 wangzuochun
        finally {
            dbl.closeResultSetFinal(rs1, rs);
            dbl.closeResultSetFinal(rsGroup);
        }
		//------------------ #404 ----------------//
        //-------------------------------------------------------------------------------//
    }
}
