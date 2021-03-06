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
public class repUserRights
    extends BaseBuildCommonRep {
    private CommonRepBean repBean;
    private String userCode = "";
    private String userName = "";
//   private static String GZSYSTEM = "估值系统";
//   private static String CWSYSTEM = "财务系统";
    private String role = "";
    private String right = "";

    private FixPub fixPub = null;

    public repUserRights() {
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
            userCode = reqAry[0].split("\r")[1]; //用户代码
        }
    }

    protected String buildShowData() throws //报表的显示数据
        YssException {
        StringBuffer finBuf = new StringBuffer(); //最终返回的数据
        StringBuffer titleBuf = new StringBuffer(); //表头栏

        titleBuf.append("用户名").append(",");
//      titleBuf.append("系统").append(",");
        titleBuf.append("角色/模块").append(",");
        titleBuf.append("权限").append(",");
        finBuf.append(fixPub.buildRowCompResult(titleBuf.toString(),
                                                "DSDaysJY00001")).append("\r\n");

        //如果用户代码不为空
        if (!userCode.equals("")) {
            genGzData(userCode, finBuf);
        } else {
            //为空则获取所有用户的权限数据
            List userCodesList = getAllUserCodes();
            Iterator userCodesItor = userCodesList.iterator();
            while (userCodesItor.hasNext()) {
                userCode = (String) userCodesItor.next();
                genGzData(userCode, finBuf);
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
    private void genGzData(String userCode, StringBuffer finBuf) throws YssException {
        userName = getUserNameByCode(userCode);
        //估值用户角色
        List roles = getRolesByCode(userCode);
        Iterator roleItor = roles.iterator();
        while (roleItor.hasNext()) {
            role = (String) roleItor.next();
            StringBuffer rowBuf = new StringBuffer();
            rowBuf.append(userName).append(",");
//       rowBuf.append(GZSYSTEM).append(",");
            rowBuf.append(role).append("角色").append(",");
            rowBuf.append(" ").append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DSDaysJY00001")).append("\r\n");
        }

        //估值用户权限
        Map rights = new HashMap();
        rights = getRightsByCode(userCode);
        Iterator rightsItor = rights.keySet().iterator();
        while (rightsItor.hasNext()) {
            role = (String) rightsItor.next(); //模块名
            right = rightTranslate( (String) rights.get(role)); //权限
            StringBuffer rowBuf = new StringBuffer();
            rowBuf.append(userName).append(",");
//       rowBuf.append(GZSYSTEM).append(",");
            rowBuf.append(role).append(",");
            rowBuf.append(right).append(",");
            finBuf.append(fixPub.buildRowCompResult(rowBuf.toString(),
                "DSDaysJY00001")).append(
                    "\r\n");
        }
    }

    //获取所有未被锁的用户
    private List getAllUserCodes() throws YssException {
        String strSql = "";
        List list = new ArrayList();
        ResultSet rs = null;
        try {

            strSql =
                "select a.fusercode from Tb_Sys_Userlist a where a.flocked = 0";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                list.add(rs.getString("fusercode"));
            }
            return list;
        } catch (Exception e) {
            throw new YssException("获取用户角色出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private String getUserNameByCode(String userCode) throws
        YssException {
        String userName = "";
        String strSql = "";
        ResultSet rs = null;
        try {

            strSql =
                "select fusername from Tb_Sys_UserList a where a.flocked = 0  and a.fusercode = '" + userCode + "'";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                userName = rs.getString("fusername");
            }
            return userName;
        } catch (Exception e) {
            throw new YssException("用户被锁或不存在！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取角色或模块
    private java.util.List getRolesByCode(String userCode) throws
        YssException {
        String strSql = "";
        List list = new ArrayList();
        ResultSet rs = null;
        try {

            strSql =
                "select frolename from TB_SYS_USERRIGHT a left join (select frolecode,frolename from TB_SYS_ROLE )b on a.frightcode = b.frolecode where a.frightind = 'Role' and a.fusercode = '" +
                userCode + "'";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                list.add(rs.getString("frolename"));
            }
            return list;
        } catch (Exception e) {
            throw new YssException("获取用户角色出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    private java.util.Map getRightsByCode(String userCode) throws
        YssException {
        String strSql = "";
        //List list = new ArrayList();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        Map map = new HashMap();
        ResultSet rs = null;
        try {
        	//modify by fangjiang MS01820 QDV4交银施罗德2010年09月28日02_B
            strSql =
                "select b.fbarname,a.fopertypes from TB_SYS_USERRIGHT a left join " +
                " (select fbarcode,fbarname,fordercode from Tb_Fun_Menubar) b " +
                " on a.frightcode = b.fbarcode where a.frightind = 'Right' and a.fusercode='" +
                userCode + "' order by b.fordercode ";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                String modeName = rs.getString("fbarname");
                String operType = rs.getString("fopertypes");
                map.put(modeName, operType);
            }
            //--------------------------
            return map;
        } catch (Exception e) {
            throw new YssException("获取用户权限出错！");
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
