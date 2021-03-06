package com.yss.main.operdeal.report.repfix;

import com.yss.main.operdeal.report.BaseBuildCommonRep;

import com.yss.dsub.*;

import com.yss.main.report.*;
import com.yss.main.report.CommonRepBean;
import com.yss.main.operdata.*;
import com.yss.util.YssException;
import com.yss.pojo.param.comp.*;
import java.util.*;
import com.yss.main.compliance.*;
import java.sql.*;

import com.yss.main.operdeal.report.*;
import com.yss.util.*;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.parasetting.SecurityBean;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class repRoleRights
    extends BaseBuildCommonRep {
    private CommonRepBean repBean;
    private String roleCode = "";
    private String roleName = "";
//   private static String GZSYSTEM = "估值系统";
//   private static String CWSYSTEM = "财务系统";
    private String role = "";
    private String right = "";

    private FixPub fixPub = null;

    public repRoleRights() {
    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildShowData();
        return sResult;
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        //
        if (reqAry[0].split("\r").length > 1) {
            roleCode = reqAry[0].split("\r")[1]; //权限代码
        }
    }

    protected String buildShowData() throws //报表的显示数据
        YssException {
        StringBuffer finBuf = new StringBuffer(); //最终返回的数据
        StringBuffer titleBuf = new StringBuffer(); //表头栏

        titleBuf.append("角色名").append(",");
//      titleBuf.append("系统").append(",");
        titleBuf.append("模块").append(",");
        titleBuf.append("权限").append(",");
        finBuf.append(fixPub.buildRowCompResult(titleBuf.toString(),
                                                "DSDaysJY00002")).append("\r\n");

        //如果角色代码不为空
        if (!roleCode.equals("")) {
            genGzData(roleCode, finBuf);
        } else {
            //为空则获取所有用户的权限数据
            List roleCodesList = getAllRoleCodes();
            Iterator roleCodesItor = roleCodesList.iterator();
            while (roleCodesItor.hasNext()) {
                roleCode = (String) roleCodesItor.next();
                genGzData(roleCode, finBuf);
            }

        }
        try {
            if (finBuf.toString().length() > 2) {
                return finBuf.toString().substring(0,
                    finBuf.toString().length() - 2);
            } else {
                return "";
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    //生成估值系统相关权限数据
    private void genGzData(String roleCode, StringBuffer finBuf) throws YssException {
        roleName = getRoleNameByCode(roleCode);
        //模块
        Map rights = new HashMap();
        rights = getRightsByCode(roleCode);
        Iterator rightsItor = rights.keySet().iterator();
        while (rightsItor.hasNext()) {
            role = (String) rightsItor.next(); //模块名
            right = rightTranslate( (String) rights.get(role)); //权限
            StringBuffer rowBuf = new StringBuffer();
            rowBuf.append(roleName).append(",");
            //       rowBuf.append(GZSYSTEM).append(",");
            rowBuf.append(role).append(",");
            rowBuf.append(right).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DSDaysJY00002")).append(
                    "\r\n");
        }

    }

    //获取所有未被锁的用户
    private List getAllRoleCodes() throws YssException {
        String strSql = "";
        List list = new ArrayList();
        ResultSet rs = null;
        try {

            strSql =
                "select a.frolecode from TB_SYS_ROLE a where a.fstate = 0";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                list.add(rs.getString("frolecode"));
            }
            return list;
        } catch (Exception e) {
            throw new YssException("获取用户角色出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String getRoleNameByCode(String roleCode) throws
        YssException {
        String roleName = "";
        String strSql = "";
        ResultSet rs = null;
        try {

            strSql =
                "select a.frolename from TB_SYS_ROLE a where a.fstate = 0 and a.frolecode = '" + roleCode + "'";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                roleName = rs.getString("frolename");
            }
            return roleName;
        } catch (Exception e) {
            throw new YssException("获取角色名出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private java.util.Map getRightsByCode(String roleCode) throws
        YssException {
        String strSql = "";
        //List list = new ArrayList();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        Map map = new HashMap();
        ResultSet rs = null;
        try {
        	//modify by fangjiang MS01820 QDV4交银施罗德2010年09月28日02_B
        	strSql = "select a.fopertypes,b.fbarname from ( select fopertypes,frolecode,frightcode from TB_SYS_ROLERIGHT where fopertypes <> 'Role' and frolecode = '" + roleCode 
        	+ "' ) a join (select fbarcode,fbarname,fordercode from Tb_Fun_Menubar) b on a.frightcode = b.fbarcode order by b.fordercode";
        	
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                String modeName = rs.getString("fbarname");
                String operType = rs.getString("fopertypes");
                map.put(modeName, operType);
            }
            //------------------------
            return map;
        } catch (Exception e) {
            throw new YssException("获取操作权限出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String rightTranslate(String right) {
        String trastated = "";
        trastated = right.replaceAll(",", "/");
        trastated = trastated.replaceAll("add", "增加");
        trastated = trastated.replaceAll("del", "删除");
        trastated = trastated.replaceAll("edit", "修改");
        trastated = trastated.replaceAll("brow", "浏览");
        trastated = trastated.replaceAll("execute", "行使");
        trastated = trastated.replaceAll("audit", "审核");
        trastated = trastated.replaceAll("clear", "清除");
        trastated = trastated.replaceAll("revert", "还原");
        return trastated;
    }
}
