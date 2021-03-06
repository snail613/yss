package com.yss.main.datainterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.compare.DaoCompareExSet;
import com.yss.main.syssetting.RightBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;

public class DaoGroupSetBean
    extends BaseDataSettingBean implements
    IDataSetting {
    public DaoGroupSetBean() {
    }

    private String abc = "";
    private String GroupCode = "";
    private String GroupName = "";
    private String ParentCode = "";
    private String ParentName = "";
    private int OrderCode;
    private String Desc = "";
    private String GroupType = ""; //新增字段　接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
    private String SubRepCodes = "";

    private String AssetGroupCode = ""; //组合群代码  MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-05-25
    private String AssetGroupName = ""; //组合群名称  MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-05-25
    private boolean isGroup = false; //判断是否为跨组合群 MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-05-25

    private String oldGroupCode = "";
    private int oldOrderCode;
  //MS01272    add by zhangfa  2010.07.11   QDV4招商基金2010年6月8日01_A   
    private String flowcode="";
    private String flowpointid="";
    private String CurrentUser = "";	//Story #1313
    private String sRoleCode="";  //STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发
    //--------------------------------------------------------------------  
    
    RightBean right = new RightBean();

      public String getCurrentUser() {
  		return CurrentUser;
  	}
  	public void setCurrentUser(String currentUser) {
  		CurrentUser = currentUser;
  	}
  //--------------------------------------------------------------------  

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException{
    	String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
      
        try {
            sHeader = "接口类型\t接口类型名称";
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                " where fcheckstate = 1 order by FGroupCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FGroupCode")).append("\t");
                bufShow.append(rs.getString("FGroupName") +
                               "").append(YssCons.YSS_LINESPLITMARK);

                this.GroupCode = rs.getString("FGroupCode") + "";

                this.GroupName = rs.getString("FGroupName") + "";
                
                this.checkStateId = 1;

                //this.strKey = "";

                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (buf.toString().length() > 2) {
                sAllDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * MS01272  add by zhangfa  2010.07.09     QDV4招商基金2010年6月8日01_A
     * @throws YssException 
     * @方法名：getGroupCode
     * @参数：FlowPointID 流程的序号,flowCode 流程代码
     * @返回类型：String
     * @说明：从Tb_Fun_Flow表中获取接口群代码
     */  
    public String getGroupCode(String flowCode ,String flowPointID) throws YssException{
    	StringBuffer sbf=new StringBuffer();
    	 String strSql = "";
    	 ResultSet rs = null;
    	 
    	 if((flowCode==null||flowCode.length()==0)&&(flowPointID==null||flowPointID.length()==0)){
    		 return "";
    	 }
    	 try{
    		 strSql="select FDaoGroup from Tb_Fun_Flow "+
             "where fflowcode='"+flowCode+"' and fflowpointid='"+flowPointID+"'";
    		 rs = dbl.openResultSet(strSql);
             while (rs.next()) {
            	 sbf.append(rs.getString("FDaoGroup"));
             }
             
    	 }catch(Exception e){
    		 throw new YssException("查询接口群出错", e);
    	 }
    	 
		return sbf.toString();
    	
    }
  //-------------------------------------------------------------------  

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "接口类型\t接口类型名称";
            strSql = "select DISTINCT FGroupCode,FGroupName from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                (GroupType.trim().length() == 0 ? "" : (" where FGroupType=" + dbl.sqlString(GroupType))) + //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                " group by FGroupCode,FGroupName order by FGroupCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FGroupCode")).append("\t");
                bufShow.append(rs.getString("FGroupName") +
                               "").append(YssCons.YSS_LINESPLITMARK);

                this.GroupCode = rs.getString("FGroupCode") + "";

                this.GroupName = rs.getString("FGroupName") + "";

                //this.strKey = "";

                buf.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (buf.toString().length() > 2) {
                sAllDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
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
     * getListViewData4   STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发
     * 角色接口群权限
     * @return String
     * @throws YssException 
     * @throws YssException 
     */
    public String getListViewData4() throws YssException{    
    	/**shashijie 2012-8-6 STORY 2661 全部复制到getRoleRithtData()*/
    	return getRoleRightData(1);
    	/**end*/
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
            strSql = "insert into " + pub.yssGetTableName("Tb_Dao_Group") +
//                     " (FGroupCode,FGroupName,FParentCode,FOrderCode,FDesc,FCheckState,FCreator,FCreateTime,FCusConfigCodes)" +
                " (FGroupCode,FGroupName,FParentCode,FOrderCode,FDesc,FCheckState,FCreator,FCreateTime,FCusConfigCodes,FGroupType)" + //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                " values( " + dbl.sqlString(this.GroupCode) +
                "," + dbl.sqlString(this.GroupName) +
                "," + dbl.sqlString(this.ParentCode) +
                "," +
                dbl.sqlString(dbFun.treeBuildOrderCode(pub.yssGetTableName(
                    "Tb_Dao_Group"),
                "FGroupCode",
                this.ParentCode, this.OrderCode)) +
                "," + dbl.sqlString(this.Desc) +
                //-----------------------------------------------------------------------------------------------
                //MS00244 QDV4赢时胜（上海）2009年02月12日01_B 2009.02.12 方浩
                //FCheckState=0时图标颜色跟原来的颜色不同,前面的checkInput()方法已不在对审核条件进行判断了
                ",1" + //======Modify by Mao Qiwen  20080805   bug:0000353======新建的数据接口群都应该为未审核的状态//
                //------------------------------------------------------------------------------------------------
                "," + dbl.sqlString(this.creatorCode) +
                "," + dbl.sqlString(this.creatorTime) +
                "," + dbl.sqlString(this.SubRepCodes) +
                "," + dbl.sqlString(this.GroupType) + //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();

        } catch (Exception e) {
            throw new YssException("新增接口群数据代码出错", e);
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
            //传入一false判断是接口群设置就不进行审核判断，就是说不论审核与否都能修改，因为接口群设置根本就没有审核与未审核。
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Dao_Group"),
//                                   "FGroupCode",
//                                   this.GroupCode, this.oldGroupCode,null,null,false);
                                   "FGroupCode" + "," + "FGroupType",
                                   this.GroupCode + "," + this.GroupType,
                                   this.oldGroupCode + "," + this.GroupType, null, null, false); //增加接口群类型字段
            //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            //----------------------------------------------------------------------------------------

        }
        /*
              if (this.oldOrderCode != this.OrderCode) {
           strSql = "select FOrderCode from " +
                 pub.yssGetTableName("Tb_Rep_Group") +
                 " where FOrderCode = '" +
                 dbFun.treeBuildOrderCode(pub.yssGetTableName("Tb_Rep_Group"),
                                          "FRepGrpCode",
         this.ParentCode, this.OrderCode) + "'";
           tmpValue = dbFun.GetValuebySql(strSql);
           if (tmpValue.trim().length() > 0) {
              throw new YssException("报表组排序号【" + this.OrderCode +
                                     "】已被【" + tmpValue + "】占用，请重新输入菜单排序号");
           }
              }*/

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
                     strSql = "update " + pub.yssGetTableName("Tb_Dao_Group") +
                  " set FCheckState = " + this.checkStateId +
                  ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
             ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                  "' where FGroupCode = " + dbl.sqlString(this.oldGroupCode);
             */
            //重写了SQL语句使删除就是从数据库中删除而不更新到回站里去！MS00190 QDV4交银施罗德2009年01月15日01_B 2009.01.19 方浩
            strSql = "delete from " + pub.yssGetTableName("Tb_Dao_Group") +
                " where FGroupCode = " + dbl.sqlString(this.oldGroupCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除接口群数据代码出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Dao_Group") +
                " set FGroupCode = " + dbl.sqlString(this.GroupCode) +
                ",FGroupName = " + dbl.sqlString(this.GroupName) +
                ",FParentCode = " + dbl.sqlString(this.ParentCode) +
                ",FOrderCode = " +
                dbl.sqlString(dbFun.treeBuildOrderCode(pub.
                yssGetTableName("Tb_Dao_Group"),
                "FGroupCode",
                this.ParentCode, this.OrderCode)) +
                ",FDesc = " + dbl.sqlString(this.Desc) +
                ",FCusConfigCodes=" + dbl.sqlString(this.SubRepCodes) +
                //-----------------------------------------------------------------------------------------------
                //MS00244 QDV4赢时胜（上海）2009年02月12日01_B 2009.02.12 方浩
                //FCheckState=0时图标颜色跟原来的颜色不同,前面的checkInput()方法已不在对审核条件进行判断了
                ",FCheckstate = 1" + // (pub.getSysCheckState() ? "0" : "1") +
                //------------------------------------------------------------------------------------------------
                ",FGroupType=" + dbl.sqlString(GroupType) + //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "''" :
                 dbl.sqlString(this.creatorCode)) +
                " where FGroupCode = " + dbl.sqlString(this.oldGroupCode) +
                " and FGroupType =" + dbl.sqlString(GroupType); //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            ; //===== 20080805   bug:0000353=======//
            //====只有处于未审核状态（FCheckState = 0）的数据才能进行修改,在此处修改数据其状态不发生变化，仍然为未审核，便于再进行修改====//
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
      //MS01272    add by zhangfa  2010.07.11   QDV4招商基金2010年6月8日01_A 
        String groups=getGroupCode(this.flowcode, this.flowpointid);
        if(groups!=null&&groups.length()!=0){
        	StringBuffer sbf=new StringBuffer();
        	String subf="";
        	for(int i=0;i<groups.split(",").length;i++){
        		sbf.append("'").append(groups.split(",")[i]).append("'").append(",");
        	}
             subf=sbf.substring(0, sbf.length()-1);
        	 try {
                 strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
                 strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                     pub.yssGetTableName("Tb_Dao_Group") + " a" +
                     " left join(select FGroupCode,FGroupName from " +
                     pub.yssGetTableName("Tb_Dao_Group") +
                     ") b on a.FParentCode = b.FGroupCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                     " where FCheckState <> '2'" +
                     " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + //添加接口类型字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                     "and a.fgroupcode in ("+subf+
                     //" order by a.FGroupCode"; //=====Modify by Mqo Qiwen  20080805  bug:0000353========//
                     ")  order by a.FOrderCode, a.FGroupCode"; //这里采用按节点排序，避免前台加载时过滤掉了最明细数据　QDV4赢时胜（上海）2009年02月19日02_B MS00260　by leeyu 20090223
                 //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
                 strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
                 rs = dbl.openResultSet(strSql);
                 while (rs.next()) {
                     this.AssetGroupCode = rs.getString("FAssetGroupCode");
                     this.AssetGroupName = rs.getString("FAssetGroupName");
                     setResultSetAttr(rs);
                     result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                 }
                 if (result.length() > 2) {
                     return result.substring(0, result.length() - 2);
                 } else {
                     return "";
                 }
             } catch (Exception ex) {
                 throw new YssException("获取接口群出错", ex);
             } finally {
                 dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01
             }
        	
        }
      //-------------------------------------------------------------------  
        try {
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_Group") + " a" +
                " left join(select FGroupCode,FGroupName from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                ") b on a.FParentCode = b.FGroupCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where FCheckState <> '2'" +
                " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + //添加接口类型字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                //" order by a.FGroupCode"; //=====Modify by Mqo Qiwen  20080805  bug:0000353========//
                " order by a.FOrderCode, a.FGroupCode"; //这里采用按节点排序，避免前台加载时过滤掉了最明细数据　QDV4赢时胜（上海）2009年02月19日02_B MS00260　by leeyu 20090223
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.AssetGroupCode = rs.getString("FAssetGroupCode");
                this.AssetGroupName = rs.getString("FAssetGroupName");
                setResultSetAttr(rs);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取接口群出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01
        }
    }

    /**
     * getTreeViewData2
     * MS01272  add by zhangfa  2010.07.09     QDV4招商基金2010年6月8日01_A
     * @return String
     * @throws YssException 
     */
    //Story #1313
    public String getTreeViewData2() throws YssException {
    	
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rsRight = null;
        
        try {
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_Group") + " a" +
                " left join(select FGroupCode,FGroupName from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                ") b on a.FParentCode = b.FGroupCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where FCheckState <> '2'" +
                " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + //添加接口类型字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                //" order by a.FGroupCode"; //=====Modify by Mqo Qiwen  20080805  bug:0000353========//
                " order by a.FOrderCode, a.FGroupCode"; //这里采用按节点排序，避免前台加载时过滤掉了最明细数据　QDV4赢时胜（上海）2009年02月19日02_B MS00260　by leeyu 20090223
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + (this.AssetGroupCode==null || this.AssetGroupCode.equals("") ? pub.getPrefixTB() : this.AssetGroupCode) + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.AssetGroupCode = rs.getString("FAssetGroupCode");
                this.AssetGroupName = rs.getString("FAssetGroupName");
                setResultSetAttr(rs);              
                //2.从用户权限表中查询用户接口权限
                strSql = "select FRightCode from TB_SYS_USERRIGHT where FUserCode = ";
                if (this.CurrentUser != null && this.CurrentUser.trim().length() > 0) { //如果 选择了 用户，就 查询 相应用户的权限
                    strSql += dbl.sqlString(this.CurrentUser);
                } else { //如果 没有选择用户，就默认 查询 当前登录系统 的用户 的 相应权限
                    dbl.sqlString(pub.getUserCode());
                }
                //匹配当前登录组合群和当前外围游标里的报表
                strSql += " and FAssetGroupCode = " + dbl.sqlString(pub.getPrefixTB()) +
                    " and FRightInd = 'DataInterface' and FRightCode = '" + pub.getPrefixTB() + "-" + rs.getString("FGroupCode") + "'";
                rsRight = dbl.openResultSet(strSql);
                //如果有记录设置审核状态为1，表示有权限，否则设置为0，无权限
                if (rsRight.next()) 
                    this.checkStateId = 1;
                else 
                    this.checkStateId = 0;                             
                dbl.closeResultSetFinal(rsRight);                
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取接口群出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01
        }
        
    }
    /**
     * getTreeViewData3
     *
     * @return String
     */
    
    //Story #1313
    public String getTreeViewData3() throws YssException
    {
    	String strSql = "";
        String result = "";
        ResultSet rs = null;
        RightBean right = new RightBean();
        right.setYssPub(pub);
      //MS01272    add by zhangfa  2010.07.11   QDV4招商基金2010年6月8日01_A 
        String groups=getGroupCode(this.flowcode, this.flowpointid);
        if(groups!=null&&groups.length()!=0){
        	StringBuffer sbf=new StringBuffer();
        	String subf="";
        	for(int i=0;i<groups.split(",").length;i++){
        		sbf.append("'").append(groups.split(",")[i]).append("'").append(",");
        	}
             subf=sbf.substring(0, sbf.length()-1);
        	 try {
        		 right.setYssPub(pub);
        		 right.setUserCode(pub.getUserCode());
        		 
                 strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
                 strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                     pub.yssGetTableName("Tb_Dao_Group") + " a" +
                     " left join(select FGroupCode,FGroupName from " +
                     pub.yssGetTableName("Tb_Dao_Group") +
                     ") b on a.FParentCode = b.FGroupCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                     " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
//                     " left join (select frightcode from tb_sys_userright where frightind = 'DataInterface' and FUserCode = '" + pub.getUserCode() + "' and FAssetGroupCode = '" + pub.getPrefixTB() + "') right on right.FRightCode = '" + pub.getPrefixTB() + "' || a.FGroupCode" + 	//Story #1313
                     " left join (select frightcode from tb_sys_userright where frightind = 'DataInterface' and (FUserCode = " + dbl.sqlString(pub.getUserCode()) + right.getTrustors(pub.getUserCode(),"Login") + ") and FAssetGroupCode = '" + pub.getPrefixTB() + "') right on right.FRightCode = '" + pub.getPrefixTB() + "' || a.FGroupCode" +
                     " where FCheckState <> '2'" +
                     " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + //添加接口类型字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                     "and a.fgroupcode in ("+subf+
                     //" order by a.FGroupCode"; //=====Modify by Mqo Qiwen  20080805  bug:0000353========//
                     ")  order by a.FOrderCode, a.FGroupCode"; //这里采用按节点排序，避免前台加载时过滤掉了最明细数据　QDV4赢时胜（上海）2009年02月19日02_B MS00260　by leeyu 20090223
                 //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
                 strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
                 rs = dbl.openResultSet(strSql);
                 while (rs.next()) {
                     this.AssetGroupCode = rs.getString("FAssetGroupCode");
                     this.AssetGroupName = rs.getString("FAssetGroupName");
                     setResultSetAttr(rs);
                     result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                 }
                 if (result.length() > 2) {
                     return result.substring(0, result.length() - 2);
                 } else {
                     return "";
                 }
             } catch (Exception ex) {
                 throw new YssException("获取接口群出错", ex);
             } finally {
                 dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01
             }
        	
        }
      //-------------------------------------------------------------------  
        try {
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_Group") + " a" +
                " left join(select FGroupCode,FGroupName from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                ") b on a.FParentCode = b.FGroupCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where FCheckState <> '2' " +

				" and '" + pub.getPrefixTB() + "-' || a.FGroupCode in (select frightcode from tb_sys_userright where frightind = 'DataInterface'  " +

                //20121129 modified by liubo.Story #2737 权限继承海富通测试问题
                //获取接口群时需要包含权限继承继承下来的接口群，包括委托人赋予的和角色赋予的
                //==================================
				" and (FUserCode = '" + pub.getUserCode() + "' " + right.getInheritedRights(pub.getPrefixTB(), "") + ")" +
				" and FAssetGroupCode = '" + pub.getPrefixTB() + "' " +  //Story #1313
                /**shashijie 2012-6-4 STORY 2661 添加角色权限判断 */
				" Union " +
				" Select B1.Frightcode" +
				" From (Select a.Frightcode" +
				" From Tb_Sys_Userright a" +
				" Where a.Frightind = 'Role'" +
				" And (a.Fusercode = "+dbl.sqlString(pub.getUserCode())+ right.getInheritedRights(pub.getPrefixTB(), "a") + ")" +
                //=================end=================
                " And a.Frighttype = 'datainterface') A1" +
                " Join (Select b.Frolecode, b.Frightcode" +
                " From Tb_Sys_Roleright b" +
                " Where b.Fopertypes = 'DataInterface') B1 On A1.Frightcode =  B1.Frolecode)"+
				// " and '" + pub.getPrefixTB() +
				// "-' || a.FGroupCode in (select frightcode from tb_sys_userright where frightind = 'DataInterface'  and FUserCode = '"
				// + pub.getUserCode() + "' and FAssetGroupCode = '" +
				// pub.getPrefixTB() + "')" + //Story #1313
				// " and '" + pub.getPrefixTB() +
				// "-' || a.FGroupCode in (select frightcode from tb_sys_userright where frightind = 'DataInterface'  and (FUserCode = "
				// + dbl.sqlString(pub.getUserCode()) +
				// right.getTrustors(pub.getUserCode()) +
				// ") and FAssetGroupCode = '" + pub.getPrefixTB() + "')" +
                /**end*/
                
                " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + //添加接口类型字段 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
                //" order by a.FGroupCode"; //=====Modify by Mqo Qiwen  20080805  bug:0000353========//
                " order by a.FOrderCode, a.FGroupCode"; //这里采用按节点排序，避免前台加载时过滤掉了最明细数据　QDV4赢时胜（上海）2009年02月19日02_B MS00260　by leeyu 20090223
            //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + pub.getPrefixTB() + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.AssetGroupCode = rs.getString("FAssetGroupCode");
                this.AssetGroupName = rs.getString("FAssetGroupName");
                setResultSetAttr(rs);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取接口群出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01
        }
    }
    
//    public String getTreeViewData3() throws YssException {
//        String strSql = "";
//        String strSql1 = "";
//        String strRepCodes = "";
//        String result = "";
//        ResultSet rs = null;
//        ResultSet rs1 = null;
        /* try {
            strSql = "select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                  pub.yssGetTableName("Tb_Rep_Group") + " a" +
                  " left join(select FRepGrpCode,FRepGrpName from " +
                  pub.yssGetTableName("Tb_Rep_Group") +
                  ") b on a.FParentCode = b.FRepGrpCode" +
                  " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                  " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                  " where FCheckState = 1 order by FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
               setResultSetAttr(rs);
               strSql1 =
         "select FRightCode from TB_SYS_USERRIGHT where FUserCode = " +
         dbl.sqlString(pub.getUserCode()) + " and FAssetGroupCode = " +
         dbl.sqlString(pub.getAssetGroupCode()) + " and FRightCode = " +
         dbl.sqlString(this.RepGrpCode) + " and FRightInd = 'Report'";
               rs1 = dbl.openResultSet(strSql1);
               if (rs1.next()) {
                  this.checkStateId = 1;
               }
               else {
                  this.checkStateId = 0;
               }
               result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
               return result.substring(0, result.length() - 2);
            }
            else {
               return "";
            }

         }
         catch (Exception ex) {
            throw new YssException("获取自定义接口类型数据出错", ex);
         }*/
//        return "";
//    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        if (isGroup) { //跨组合群返回到前台的数据，MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            buf.append(this.AssetGroupCode.trim() + "-" + this.GroupCode.trim()).append("\t");
            buf.append(this.GroupName.trim()).append("\t");
            if (this.ParentCode.equalsIgnoreCase("[root]")) {
                buf.append(this.AssetGroupCode.trim()).append("\t");
                buf.append(this.OrderCode).append("\t");
                buf.append(this.AssetGroupName.trim()).append("\t");
            } else {
                buf.append(this.AssetGroupCode.trim() + "-" + this.ParentCode.trim()).append("\t");
                buf.append(this.OrderCode).append("\t");
                buf.append(this.ParentName.trim()).append("\t");
            }
        } else {
            buf.append(this.GroupCode.trim()).append("\t");
            buf.append(this.GroupName.trim()).append("\t");
            buf.append(this.ParentCode.trim()).append("\t");
            buf.append(this.OrderCode).append("\t");
            buf.append(this.ParentName.trim()).append("\t");
        }
        buf.append(this.SubRepCodes.trim()).append("\t");
        buf.append(this.Desc.trim()).append("\t");
        buf.append(this.GroupType).append("\t"); //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        buf.append(this.AssetGroupCode.trim()).append("\t"); //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
        buf.append(this.AssetGroupName.trim()).append("\t"); //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
        buf.append(super.buildRecLog());

        return buf.toString();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    private String getValue() {
        return "";
    }

    //该方法用于判断是否跨组合群，原有方法由getOperValueOverGroup(String sType)代替
    //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify  2009-06-02
	public String getOperValue(String sType) throws YssException {
		String sResult = "";
		// ---STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发
		// start---//
		try {
			if (sType.split("!!")[0].equalsIgnoreCase("SelUserCode")) {
				String temUserCode = sType.split("!!")[1];
				temUserCode = new String(temUserCode.getBytes("ISO-8859-1"),
						"GBK");
				this.CurrentUser = temUserCode;
				return this.getTreeViewGroupData2();
			}
			if (sType.split("!!")[0].equalsIgnoreCase("Role")) { // STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发
				String sRoleCode = sType.split("!!")[1];
				sRoleCode = new String(sRoleCode.getBytes("ISO-8859-1"), "GBK");
				this.sRoleCode = sRoleCode;
				return this.getTreeViewGroupData3();
			}
			/**shashijie 2012-8-6 STORY 2661 重载获取角色接口权限 */
			else if (sType.split("!!")[0].equalsIgnoreCase("Role2")) {
				String sRoleCode = sType.split("!!")[1];
				sRoleCode = new String(sRoleCode.getBytes("ISO-8859-1"), "GBK");
				this.sRoleCode = sRoleCode;
				return this.getRoleRight(3);
            }
			/**end */

			else if (sType.split("!!")[0].equalsIgnoreCase("PerInheritance")) {
				this.CurrentUser = sType.split("!!")[1];
				return this.getListViewGroupData2();
			}
		} catch (Exception e) {
			throw new YssException("");
		}
		// ---STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发 end---//
		if (this.AssetGroupCode.trim().length() > 0) { // 如果组合群代码不为空即跨组合群
														// 国内：MS00001
														// QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
														// panjunfang add
														// 2009-06-01
			String prefixTB = pub.getPrefixTB();// 取得原有表前缀
			try {
				pub.setPrefixTB(this.AssetGroupCode);// 将当前组合群代码设为表前缀
				sResult = getOperValueOverGroup(sType);// 调用处理方法
			} catch (Exception e) {
				throw new YssException(e);
			} finally {
				pub.setPrefixTB(prefixTB);// 设回表前缀
			}
		} else {
			sResult = getOperValueOverGroup(sType);
		}
		return sResult;
	}

	//国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang modify  2009-06-02
    public String getOperValueOverGroup(String sType) throws YssException {
        String strSql = "", sReturn = "", sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        //无用注释
        /*String fsubreps[] = null;
        String fsubrep = "'all',";*/
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;

        try {
            if (sType.equalsIgnoreCase("set")) {
                //给接口群关联接口
                sReturn = "false";
                strSql = "update " + pub.yssGetTableName("Tb_Dao_Group") +
                    " set FCusConfigCodes = " + dbl.sqlString(this.SubRepCodes) +
                    " where FGroupCode = " + dbl.sqlString(this.oldGroupCode);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
                sReturn = "true";
                return sReturn;
            }
            if (sType.equalsIgnoreCase("get")) {
                //获取报表组的子报表
                sHeader = "接口代码\t接口名称\t文件路径";//edit by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                
                /**add---shashijie 2013-2-27 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
                //系统已有公共方法实现,无用注释,并且之前无注释
                /*if (!this.SubRepCodes.equalsIgnoreCase("")) {
                    fsubreps = this.SubRepCodes.split(",");
                    for (int i = 0; i < fsubreps.length; i++) {
                        fsubrep = fsubrep + dbl.sqlString(fsubreps[i]) +
                            ",";
                    }
                }
                if (fsubrep.length() > 0) {
                    fsubrep = YssFun.left(fsubrep, fsubrep.length() - 1);
                }*/

        		//组合群SQL
                strSql = " Select * From ( "; 
            	strSql += getSelectListCusconfig(pub.yssGetTableName("Tb_Dao_CusConfig"),"0");
                
                strSql += " Union All ";
                //公共表SQL
                strSql += getSelectListCusconfig("Tb_Dao_CusConfig","1");
                strSql += " ) y order by y.FCusCfgCode ";
        		/**end---shashijie 2013-2-27 STORY 3366 */
                
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append( (rs.getString("FCusCfgCode") + "").trim()).append("\t");
                    //add by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                    if((rs.getString("FFileNameDesc") + "").trim().equals("NULL")){
                        bufShow.append( (rs.getString("FCusCfgName") + "").trim()).append("\t")
                        .append(" ").append(YssCons.YSS_LINESPLITMARK);
                    }
                    else{
                        bufShow.append( (rs.getString("FCusCfgName") + "").trim()).append("\t")
                            .append( (rs.getString("FFileNameDesc") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
                    }

                    DaoCusConfigureBean group = new DaoCusConfigureBean();
                    group.setYssPub(pub);
                    group.setEntityAttr(rs);

                    bufAll.append(group.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }

                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
                }

                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            if (sType.equalsIgnoreCase("getrepcodes")) {
                //获取有权限的报表组数据
                strSql = "select x.* from (select a.*,b.FRepGrpName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                    pub.yssGetTableName("Tb_Rep_Group") + " a" +
                    " left join(select FRepGrpCode,FRepGrpName from " +
                    pub.yssGetTableName("Tb_Rep_Group") +
                    ") b on a.FParentCode = b.FRepGrpCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode ) x" +
                    " join" +
                    " (select FRightCode from tb_sys_userright  where fusercode = " +
                    dbl.sqlString(pub.getUserCode()) +
                    " and frightind = 'Report' and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode()) +
                    ") y on x.FRepGrpCode = y.FRightCode" +
                    " order by x.FOrderCode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    setResultSetAttr(rs);
                    sReturn += buildRowStr() + YssCons.YSS_LINESPLITMARK;
                }
                if (sReturn.length() > 2) {
                    return sReturn.substring(0, sReturn.length() - 2);
                } else {
                    return "";
                }
            }
            //获取核对数据源 xuqiji 2009 03 19 QDV4深圳2009年01月13日01_RA MS00192
            if (sType.equalsIgnoreCase("getCom")) {
                DaoCompareExSet compSet = new DaoCompareExSet();
                compSet.setYssPub(pub);
                compSet.setSCompCode(SubRepCodes.length() == 0 ? " " : SubRepCodes);
                return compSet.getListViewData2();
            }
            return sReturn;
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

	/**shashijie 2013-2-27 STORY 3366 获取sql */
	private String getSelectListCusconfig(String tableName, String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select Distinct '组合群' as saveType , ";
		} else {
			sql = " select Distinct '公共' as saveType , ";
		}
		sql += " y.*,' ' as FTabDesc from " +
	        " (select FCusCfgCode,FCheckState from " +
	        tableName +
	        /*将FCheckState<>2 改为 FCheckState = 1 edit by songjie 20090813 国内：MS00012 QDV4.1赢时胜（上海）2009年4月20日12_A
	        *因为在接口处理中，双击某个接口群，应该会显示接口群下已审核的自定义接口配置，不应该显示未审核的自定义接口配置，若显示了未审核的自定义接口，
	        *并选中未审核的自定义接口进行数据导入的话，就会报错，现在已经改为双击某个接口群的话，显示的相关自定义接口都为已审核的自定义接口。*/
	        " where FCheckState = 1 and FCusCfgCode in (" + operSql.sqlCodes(this.SubRepCodes) +
	        ") group by FCusCfgCode,FCheckState" +
	        " )x join (select a.*,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
	        tableName + " a " +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode " +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode " +
	        " ) y on x.FCusCfgCode=y.FCusCfgCode ";
		return sql;
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
                //   if(sRowStr.split("\r\t")>1

            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.GroupCode = reqAry[0];
            this.GroupName = reqAry[1];
            this.ParentCode = reqAry[2];
            this.OrderCode = Integer.parseInt(reqAry[3]);
            this.SubRepCodes = reqAry[4];
            this.Desc = reqAry[5];
            this.checkStateId = Integer.parseInt(reqAry[6]);
            this.oldGroupCode = reqAry[7];
            this.oldOrderCode = Integer.parseInt(reqAry[8]);
            this.GroupType = reqAry[9]; //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
            if (reqAry[9].trim().length() == 0) {
                this.GroupType = "Cus"; //默认值
            }
            this.AssetGroupCode = reqAry[10]; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-05-25

            //MS01272    add by zhangfa  2010.07.11   QDV4招商基金2010年6月8日01_A 
            if(reqAry.length>=13&&(reqAry[11]!=null||reqAry[11].length()!=0)&&(reqAry[12]!=null||reqAry[12].length()!=0)){
            	this.flowcode=reqAry[11];
            	this.flowpointid=reqAry[12];
            }
            //-------------------------------------------------------------------

            this.CurrentUser = reqAry[13];		//Story #1313
            
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
        this.GroupCode = rs.getString("FGroupCode") + "";
        this.GroupName = rs.getString("FGroupName") + "";
        this.ParentCode = rs.getString("FParentCode") + "";
        this.ParentName = rs.getString("FRepGrpNameParent") + "";
        if (this.ParentCode.equalsIgnoreCase("[root]")) {
            this.ParentName = "所有项目";
        }
        this.OrderCode = Integer.parseInt(rs.getString("FOrderCode").
                                          substring(rs.getString(
                                              "FOrderCode").length() - 3));
        this.Desc = rs.getString("FDesc") + "";
        this.SubRepCodes = rs.getString("FCusConfigCodes") + "";
        this.GroupType = rs.getString("FGroupType") + ""; //接口群类型 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        if (this.GroupType == null || this.GroupType.trim().length() == 0) {
            this.GroupType = "Cus"; //赋默认值
        }
        super.setRecLog(rs);

    }

    public void setSubRepCodes(String SubRepCodes) {
        this.SubRepCodes = SubRepCodes;
    }

    public String getSubRepCodes() {
        return SubRepCodes;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /// <summary>
    /// 修改人：panjunfang
    /// 修改人时间:20090525
    /// MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出跨组合群的内容
    public String getTreeViewGroupData1() throws YssException {
        this.isGroup = true;
        DaoGroupSetBean daoGroup = new DaoGroupSetBean();
        String sGroups = "";//用于保存处理结果数据的变量
        String AssetGroupNode = "";
        String sPrefixTB = pub.getPrefixTB();//取得原有表前缀
        try {
            String[] AssetGroupCodes = this.AssetGroupCode.split(YssCons.YSS_GROUPSPLITMARK);//解析组合群代码
            for (int i = 0; i < AssetGroupCodes.length; i++) {//遍历所有组合群
                this.AssetGroupCode = AssetGroupCodes[i];//取得一个组合群代码
                pub.setPrefixTB(this.AssetGroupCode);//将该组合群代码设为表前缀
                String sGroup = this.getTreeViewData3();//调用处理方法
                if(sGroup.length() == 0) continue;//若该组合群下没有数据接口则进行下一个组合群的查询
                daoGroup.GroupCode = this.AssetGroupCode;//接口群代码设置成当前组合群代码
                daoGroup.GroupName = this.AssetGroupName;//接口群名称设置成当前组合群名称
                daoGroup.ParentCode = "[root]";//父节点代码
                daoGroup.ParentName = "所有项目";//父节点名称
                if(sGroups.length() == 0){
                    sGroups = daoGroup.buildRowStr() + YssCons.YSS_LINESPLITMARK + sGroup + YssCons.YSS_LINESPLITMARK;
                }else{
                    sGroups = sGroups + daoGroup.buildRowStr() + YssCons.YSS_LINESPLITMARK + sGroup + YssCons.YSS_LINESPLITMARK;//将各组合群的处理数据用\f\f隔开
                }
                //sGroups = daoGroup.buildRowStr() + YssCons.YSS_LINESPLITMARK + sGroups + sGroup + YssCons.YSS_LINESPLITMARK;//将各组合群的处理数据用\f\f隔开
            }
            if (sGroups.length() > 4) {
                sGroups = sGroups.substring(0, sGroups.length() - 4);//去除尾部多余的分隔符
            }
        } catch (Exception e) {
            throw new YssException("跨组合群查询数据接口出错！",e);
        } finally {
            pub.setPrefixTB(sPrefixTB);
        }
        return sGroups;//将处理结果返回到前台
    }

    
    //Story #1313
    public String getTreeViewGroupData2() throws YssException {
    	this.isGroup = true;
        DaoGroupSetBean daoGroup = new DaoGroupSetBean();
        String sGroups = "";//用于保存处理结果数据的变量 
        String sPrefixTB = pub.getPrefixTB();//取得原有表前缀
        String strSqlGroup = "";
        String strGroupCode = "";
        ResultSet rsGroup = null;
        ResultSet rs = null;
        ArrayList listGroup = new ArrayList();
        
        try {
        	strSqlGroup = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	rsGroup = dbl.openResultSet(strSqlGroup);
        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
				//STORY 1781 add by guolongchao 20111124
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Dao_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}        	
        	dbl.closeResultSetFinal(rsGroup);
        	
            for (int i = 0; i < listGroup.size(); i++) {//遍历所有组合群
                this.AssetGroupCode = (String)listGroup.get(i);//取得一个组合群代码
                pub.setPrefixTB(this.AssetGroupCode);//将该组合群代码设为表前缀
                String sGroup = this.getTreeViewData2();//调用处理方法
                if(sGroup.length() == 0) continue;//若该组合群下没有数据接口则进行下一个组合群的查询
                daoGroup.GroupCode = this.AssetGroupCode;//接口群代码设置成当前组合群代码
                daoGroup.GroupName = this.AssetGroupName;//接口群名称设置成当前组合群名称
                //---STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发 start---//
                String sql=" select * from TB_SYS_USERRIGHT where FUserCode = "+dbl.sqlString(this.CurrentUser)+
                           " and FAssetGroupCode = " + dbl.sqlString(pub.getPrefixTB()) +
                           " and FRightInd = 'DataInterface' and FRightCode like '" + pub.getPrefixTB() + "%'"; 
                rs = dbl.openResultSet(sql);
                if(rs.next())
                    daoGroup.checkStateId = 1;
                else
                	daoGroup.checkStateId = 0;
                dbl.closeResultSetFinal(rs);
				//---STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发 end---//
                daoGroup.ParentCode = "[root]";//父节点代码
                daoGroup.ParentName = "所有项目";//父节点名称              
                sGroups = sGroups + daoGroup.buildRowStr() + YssCons.YSS_LINESPLITMARK + sGroup + YssCons.YSS_LINESPLITMARK;
            }
            if (sGroups.length() > 4) {
                sGroups = sGroups.substring(0, sGroups.length() - 4);//去除尾部多余的分隔符
            }
        } catch (Exception e) {
            throw new YssException("跨组合群查询数据接口出错！",e);
        } finally {
            pub.setPrefixTB(sPrefixTB);
            dbl.closeResultSetFinal(rsGroup);
        }
        return sGroups;
    }

	//STORY 1781 add by guolongchao 20111124 增加对角色的接口权限设置的功能开发
    public String getTreeViewGroupData3() throws YssException {
    	/**shashijie 2012-8-6 STORY 2661 全部复制到getRoleRight()方法中去了*/
		return getRoleRight(1);
		/**end*/
    }
    
    //20111205 modified by liubo.Story #1916
    //根据组合群代码，返回个组合群下的当前用户有权限的接口群信息
    public String getListViewGroupData1() throws YssException {
    	String sHeader = "";
        String strSql = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
      
        try {
            sHeader = "接口名称\t接口代码\t序号\t接口路径\t组合群";
            rs = dbl.openResultSet(getSqlForSchProject());
                DaoCusConfigureBean group = new DaoCusConfigureBean();
                group.setYssPub(pub);

            while (rs.next()) 
            {
            	//add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B//
            	bufShow.append( (rs.getString("FCusCfgName") + "").trim()).append("\t");
            	bufShow.append( (rs.getString("FCusCfgCode") + "").trim()).append("\t");
            	bufShow.append( (rs.getString("FAssetGroupName") + "").trim()).append("\t");
            	bufShow.append( (rs.getString("FFileNameDesc") + "").trim()).append("\t")
            	.append( (rs.getString("FAssetGroupCode") + "").trim()).append(YssCons.YSS_LINESPLITMARK);
	            group.setCusCfgName(rs.getString("FCusCfgName"));
	            group.setCusCfgCode(rs.getString("FCusCfgCode"));
	            group.setFileNameDesc(rs.getString("FFileNameDesc"));
	            group.setGroupCode(rs.getString("FAssetGroupCode"));
	    		group.setRecLog(rs);
                //---- add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ----//
                bufAll.append(group.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            rs.close();
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        }catch (Exception e) {
            throw new YssException("获取数据字典数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	//20121129 modified by liubo.Story #2737
    //前台在加载权限设置界面时，数据接口权限部分需要带入继承到的报表权限，包括用户赋予的与角色赋予的
    //继承到的数据接口权限，在权限设置界面将被打上灰色的勾（与角色中带的数据接口权限类似）
	//===============================
    public String getListViewGroupData2() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsmd = null;
        String sReturn = "";
        String sUserList = "";
      
        try {
        	
        	strSql = "Select distinct FTrustor as FTrustor from tb_sys_PerInheritance " +
			 " where FStartDate <=" + dbl.sqlDate(new java.util.Date()) +
			 " and FEndDate >= " + dbl.sqlDate(new java.util.Date()) + 
			 " and FTrustee like '%" + this.CurrentUser + "%' and FCHECKSTATE = 1";
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
            		") ) and FOperTypes = 'DataInterface' union " +
            		" select distinct FRightCode from Tb_Sys_UserRight where FUserCode in (" + operSql.sqlCodes(sUserList) +
            		") and FRightInd = 'DataInterface'";
            //================end====================
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
            throw new YssException("获取继承权限的数据接口权限出错：" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs,rsmd);
        }
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
    
    /**
	 * 20111205 modified by liubo.Story #1916
	 * 查询当前库中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAllAssetGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup where FAssetGroupCode = '" + pub.getAssetGroupCode() + "' order by FAssetGroupCode";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+",";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	public String getSqlForSchProject() throws YssException
	{
		String strSql = "";
		String sReturn = "";
		try
		{
			String[] sAssetGroupCode = getAllAssetGroup().split(",");
			for (int i = 0;i < sAssetGroupCode.length;i++)
			{

				sReturn = sReturn + " Select '" + sAssetGroupCode[i] + "' as FAssetGroupCode,'0' as FAssetGroupName," +
						" FCusCfgCode,FCusCfgName,FFileNameDesc,FCheckState,FCreator,' ' as FCreatorName,FcreateTime,FCheckUser,' ' as FCheckUserName,FCheckTime from Tb_" + sAssetGroupCode[i] + "_Dao_CusConfig " +
						" where FCheckState = 1 union";
			}
			
		
		return sReturn.substring(0,sReturn.length() - 5);
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		
	}
	
	/**
	 * added by liubo. #story 1916.20111206
	 * 通过此方法，将接口群记载的类似“001,002”以逗号分隔开的接口代码，转换成类似“'001','002'”这种形式的可供数据库识别的形式
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getCusCfgCodeForSql(String sDaoCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(sDaoCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			if (sDaoCode == null)
			{
				return "''";
			}
			groupCode = sDaoCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			return "".equals(requestGroupCode.trim()) ? "" : requestGroupCode.substring(0,requestGroupCode.length() - 1);
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2012-8-6 STORY 2661 重载获取角色接口权限 */
	private String getRoleRight(int checkStateId) throws YssException {
		this.isGroup = true;
        DaoGroupSetBean daoGroup = new DaoGroupSetBean();
        String sGroups = "";//用于保存处理结果数据的变量
        String sPrefixTB = pub.getPrefixTB();//取得原有表前缀
        String strSqlGroup = "";
        String strGroupCode = "";
        ResultSet rsGroup = null;
        ResultSet rsRight = null;
        ArrayList listGroup = new ArrayList();
        
        try {
        	strSqlGroup = " select FAssetGroupCode from tb_sys_assetgroup order by fassetgroupcode";
        	rsGroup = dbl.openResultSet(strSqlGroup);        	
        	while(rsGroup.next()){
        		strGroupCode = rsGroup.getString("FAssetGroupCode");
        		if (dbl.yssTableExist("Tb_" + strGroupCode + "_Dao_Group")){
        			listGroup.add(strGroupCode);
        		}
        	}
        	dbl.closeResultSetFinal(rsGroup);        	
            for (int i = 0; i < listGroup.size(); i++) {//遍历所有组合群
                this.AssetGroupCode = (String)listGroup.get(i);//取得一个组合群代码
                pub.setPrefixTB(this.AssetGroupCode);//将该组合群代码设为表前缀
                String sGroup = this.getRoleRightData(checkStateId);//调用处理方法
                if(sGroup.length() == 0) continue;//若该组合群下没有数据接口则进行下一个组合群的查询
                daoGroup.GroupCode = this.AssetGroupCode;//接口群代码设置成当前组合群代码
                daoGroup.GroupName = this.AssetGroupName;//接口群名称设置成当前组合群名称
                
                String strSql = "select * from TB_SYS_ROLERIGHT where FRoleCode in (" + operSql.sqlCodes(this.sRoleCode) +
                                " ) and FOperTypes = 'DataInterface' and FRightCode like '" + pub.getPrefixTB() + "%'";
                rsRight = dbl.openResultSet(strSql);
			    if (rsRight.next()){//如果有记录设置审核状态为1，表示有权限，否则设置为0，无权限
			    	/**shashijie 2012-8-3 STORY 2661 */
                	//daoGroup.checkStateId = 1;
			    	daoGroup.checkStateId = checkStateId;
					/**end*/
			    } else { 
			        daoGroup.checkStateId = 0;
			    }
			    dbl.closeResultSetFinal(rsRight);
                
                daoGroup.ParentCode = "[root]";//父节点代码
                daoGroup.ParentName = "所有项目";//父节点名称               
                sGroups = sGroups + daoGroup.buildRowStr() + YssCons.YSS_LINESPLITMARK + sGroup + YssCons.YSS_LINESPLITMARK;
            }
            if (sGroups.length() > 4) 
                sGroups = sGroups.substring(0, sGroups.length() - 4);   
        } catch (Exception e) {
            throw new YssException("跨组合群查询数据接口出错！",e);
        } finally {
            pub.setPrefixTB(sPrefixTB);
            dbl.closeResultSetFinal(rsGroup);
        }
        return sGroups;
	}

    /**shashijie 2012-8-6 STORY 2661 重载获取角色接口权限 */
	private String getRoleRightData(int checkStateId) throws YssException {
		String strSql = "";
        String result = "";
        ResultSet rs = null;
        ResultSet rsRight = null;          
        try {
            strSql = "select show.*,gp.FAssetGroupCode,gp.FAssetGroupName from( "; //MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add 20090525
            strSql = strSql + "select a.*,b.FGroupName as FRepGrpNameParent,c.FUserName as FCreatorName,d.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Dao_Group") + " a" +
                " left join(select FGroupCode,FGroupName from " +
                pub.yssGetTableName("Tb_Dao_Group") +
                ") b on a.FParentCode = b.FGroupCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode" +
                " where FCheckState <> '2'" +
                " " + ( (this.GroupType == null || this.GroupType.length() == 0) ? "" : " and a.FGroupType=" + dbl.sqlString(this.GroupType)) + 
                " order by a.FOrderCode, a.FGroupCode";              
            strSql = strSql + ") show join TB_SYS_ASSETGROUP gp on gp.fassetgroupcode='" + (this.AssetGroupCode==null || this.AssetGroupCode.equals("") ? pub.getPrefixTB() : this.AssetGroupCode) + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.AssetGroupCode = rs.getString("FAssetGroupCode");
                this.AssetGroupName = rs.getString("FAssetGroupName");
                setResultSetAttr(rs);                 
                strSql = "select FRightCode from TB_SYS_ROLERIGHT where FRoleCode in (" + operSql.sqlCodes(this.sRoleCode) +
                          " ) and FOperTypes = 'DataInterface' and FRightCode = '" + pub.getPrefixTB() + "-" + rs.getString("FGroupCode") + "'";              
                rsRight = dbl.openResultSet(strSql);                 
                if (rsRight.next()){ //如果有记录设置审核状态为1，表示有权限，否则设置为0，无权限
              	  	/**shashijie 2012-8-3 STORY 2661 */
                	//this.checkStateId = 1;
              	  	this.checkStateId = checkStateId;
                    /**end*/
                } else {
                    this.checkStateId = 0;
                }
                dbl.closeResultSetFinal(rsRight);                  
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) 
                return result.substring(0, result.length() - 2);
             else 
                return "";
        } catch (Exception ex) {
      	  throw new YssException("获取接口群出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); 
        }
	}
	
}
