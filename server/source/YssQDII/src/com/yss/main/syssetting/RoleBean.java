package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.IDataSetting;
import com.yss.util.*;

/**
 *
 * <p>Title: RoleBean </p>
 * <p>Description: 角色设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class RoleBean
    extends BaseDataSettingBean implements IDataSetting{//------------modify by guojianhua 2010 09 14 增加实现IDataSetting接口，否则进行日志时无法通过。
    private String roleCode = ""; //角色代码
    private String roleName = ""; //角色名称
    private String roleDesc = ""; //角色描述
    private String assetGroupCode = ""; //组合群代码
    //---------2009-08-04 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B--------//
    private String portCode = ""; //组合代码
    private String rightType = ""; //权限类型
    //-----------------------------------------------------------------------------//
    private String userCode = ""; //用户代码

    private String oldRoleCode = "";
    private RoleBean filterType;

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public RoleBean() {
    }

    public RoleBean(YssPub pub) {
        setYssPub(pub);
    }

    /**
     * 获取角色信息
     * @throws YssException
     * @return String
     */
    public String getListViewData() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "角色代码\t角色名称\t描述";
            strSql = "select * from Tb_Sys_Role " + buildFilterSql() + " order by FRoleCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FRoleCode")).append("\t");
                bufShow.append(rs.getString("FRoleName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);

                this.roleCode = rs.getString("FRoleCode");
                this.roleName = rs.getString("FRoleName");
                this.roleDesc = rs.getString("FDesc");
                this.checkStateId = 1;
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\fFRoleCode\tFRoleName\tFDesc";
        } catch (Exception e) {
            throw new YssException("获取用户数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    /**
     * 
     * @throws YssException 
     * @方法名：checkUser
     * @参数：
     * @返回类型：String
     * @说明：add by zhangfa 20101203 当角色下有用户时，删除此角色应该提示 
     */
    public String checkUser() throws YssException{
    	String strReturn="";
    	String strLast="";
    	String strSql = "";
    	ResultSet rs=null;
    	try{
    	strSql=" select distinct u.FUserCode from Tb_Sys_UserList  u"+
    		   " join (select FUserCode,FRIGHTCODE from TB_SYS_USERRIGHT where Frightind='Role' and frightcode="+dbl.sqlString(this.roleCode)+
    		   " ) r on u.FUserCode=r.fusercode";
    	rs=dbl.openResultSet(strSql);
    	while(rs.next()){
    		strReturn=rs.getString("FUserCode")+","+strReturn;
    	}
    	/**
    	if(strReturn.length()>0){
    		strReturn=strReturn.substring(0, strReturn.length()-1);
    	}
    	*/
    	}catch(Exception e){
    		throw new YssException("获取角色对应的所有用户出错 !");
    	}
		return strReturn;
    		
    }
    /**
     * 数据验证
     * @param bOperation byte
     * @throws YssException
     */
    public void checkInput(byte bOperation) throws YssException {
        String strSql = "";
        String tmpValue = "";
        if (bOperation == YssCons.OP_ADD) {
            strSql = "select FRoleName from Tb_Sys_Role where FRoleCode = " +
                dbl.sqlString(this.roleCode) + "";
        } else if (bOperation == YssCons.OP_EDIT) {
            if (!this.roleCode.equalsIgnoreCase(this.oldRoleCode)) {
                strSql = "select FRoleName from Tb_Sys_Role where FRoleCode = " +
                    dbl.sqlString(this.roleCode) + "";
            } else {
                return;
            }
        }
        tmpValue = dbFun.GetValuebySql(strSql);
        if (tmpValue.trim().length() > 0) {
            throw new YssException("角色代码【" + this.roleCode + "】已被角色【" +
                                   tmpValue + "】占用，请重新输入角色代码");
        }
    }

    public void protocolParse(String sReq) throws YssException {
        String reqAry[] = null;
        String strTmp = "";
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            if (sReq.indexOf("\r\t") >= 0) {
                strTmp = sReq.split("\r\t")[0];
            } else {
                strTmp = sReq;
            }

            reqAry = strTmp.split("\t");
            this.roleCode = reqAry[0];
            this.roleName = reqAry[1];
            this.roleDesc = reqAry[2];
            this.userCode = reqAry[3];
            this.assetGroupCode = reqAry[4];
            this.oldRoleCode = reqAry[5];
            if (sReq.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RoleBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.protocolParse(sReq.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析角色管理请求信息出错", e);
        }
    }

   public String buildRowStr()  {
        StringBuffer buf = new StringBuffer();
        buf.append(this.roleCode).append("\t");
        buf.append(this.roleName).append("\t");
        buf.append(this.roleDesc).append("\t");
        //-----2009-08-04 蒋锦 修改 MS00577 QDV4赢时胜（上海）2009年7月24日04_B------//
        //增加传到前台的数据
        buf.append(this.assetGroupCode).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.rightType).append("\t");
        //-----------------------------------------------------------------------//
        buf.append(super.buildRecLog());
        return buf.toString();
        
        }
   

    /**
     * 新增角色
     * @throws YssException
     * @return String
     */
    public String addRole() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        checkInput(YssCons.OP_ADD);
        try {
            strSql = "insert into Tb_Sys_Role(FRoleCode,FRoleName,FDesc) values(";
            strSql = strSql + dbl.sqlString(this.roleCode) + "," +
                dbl.sqlString(this.roleName)
                + "," + dbl.sqlString(this.roleDesc) + ")";
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return getListViewData();
        } catch (SQLException se) {
            throw new YssException("增加角色出错", se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改角色
     * @throws YssException
     * @return String
     */
    public String editRole() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
            checkInput(YssCons.OP_EDIT);
            strSql = "update Tb_Sys_Role set FRoleCode=" +
                dbl.sqlString(this.roleCode) + ", ";
            strSql = strSql + "FRoleName=" + dbl.sqlString(this.roleName) + ", ";
            strSql = strSql + "FDesc=" + dbl.sqlString(this.roleDesc) + "";
            strSql = strSql + "where FRoleCode=" + dbl.sqlString(this.oldRoleCode) +
                "";

            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

            return getListViewData();
        } catch (SQLException se) {
            throw new YssException("修改角色出错", se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除角色
     * @throws YssException
     * @return String
     */
    public String delRole() throws YssException {
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
            strSql = "delete from Tb_Sys_Role where FRoleCode = " +
                dbl.sqlString(this.roleCode) + "";

            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return getListViewData();
        } catch (Exception e) {
            throw new YssException("删除角色出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 获取用户角色信息
     * @throws YssException
     * @return String
     */
    public String getUserRoles() throws YssException {
        String strSql = "", strReturn = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            //2009.05.05 蒋锦 修改 查询角色信息的时候不再需要使用组合群代码作为查询条件
            //MS00010 《QDV4赢时胜（上海）2009年02月01日10_A》
            strSql = "select b.*, a.FPortCode, a.FAssetGroupCode, a.FRightType from Tb_Sys_Userright a" +
                " left join Tb_Sys_Role b on a.FRightCode = b.FRoleCode" +
                " where a.FUserCode = '" + userCode +
                "' and a.FRightInd = 'Role'";
            strSql += " and b.FRoleCode  is not null"; //by xuxuming, 2009.08.12,排除为空的角色;MS00625 对角色进行选择保存，出现一个NULL值  QDV4赢时胜（上海）2009年7月24日20_B
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.roleCode = rs.getString("FRoleCode");
                this.roleName = rs.getString("FRoleName");
                this.roleDesc = rs.getString("FDesc");
                this.assetGroupCode = rs.getString("FAssetGroupCode");
                this.portCode = rs.getString("FPortCode");
                this.rightType = rs.getString("FRightType");
                buf.append(buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取用户角色信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() { //由于在多表查询的时候没有使用本函数，所以不需要添加表别名  wdy
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";

            if (this.filterType.roleCode.length() != 0) {
                sResult = sResult + " and FRoleCode like '" +
                    filterType.roleCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.roleName.length() != 0) {
                sResult = sResult + " and FRoleName like '" +
                    filterType.roleName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.roleDesc.length() != 0) {
                sResult = sResult + " and FDesc like '" +
                    filterType.roleDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getRightType() {
        return rightType;
    }

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getOperValue(String sType) throws YssException {
		if(sType.equals("checkUser")){
			return this.checkUser();
		}
		return null;
	}

	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public String getRoleCode() {
		return roleCode;
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public void deleteUserRight(String roleCode) throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
            
            String query = " Delete From Tb_SYS_UserRight Where FRightCode = "+dbl.sqlString(roleCode)+
            	" And FrightInd = 'Role' ";
            dbl.executeSql(query);
            
            query = " Delete From Tb_SYS_RoleRight Where FRoleCode = "+dbl.sqlString(roleCode)
	        	;
	        dbl.executeSql(query);
            //备份表,可删可不删
	        query = " Delete From tb_sys_userright_bak Where FRightCode = "+dbl.sqlString(roleCode)+
            	" And FrightInd = 'Role' ";
            dbl.executeSql(query);
            
            query = " Delete From tb_sys_userright_bak40 Where FRightCode = "+dbl.sqlString(roleCode)+
            	" And FrightInd = 'Role' ";
            dbl.executeSql(query);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);        
		} catch (Exception e) {
			throw new YssException("删除角色权限出错！", e);
		} finally {
	        dbl.endTransFinal(conn, bTrans);
		}
	}

}
