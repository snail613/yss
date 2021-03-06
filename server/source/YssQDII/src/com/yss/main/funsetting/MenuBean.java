package com.yss.main.funsetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class MenuBean
    extends BaseDataSettingBean implements IDataSetting {

    private String parentCode = ""; //上级代码
    private String menuCode = ""; //菜单代码
    private String menuName = ""; //菜单名称
    private String iconPath = ""; //图标路径
    private boolean enabled; //是否可用
    private String dllName = ""; //DLL名
    private String className = ""; //类名
    private String methodName = ""; //方法名
    private String params = ""; //参数
    private String desc = ""; //菜单描述
    private boolean check; //是否为CHECK型菜单
    private String shortCut = ""; //快捷键
    private int orderCode; //排序号
    private String invokeCode = ""; //调用代码
    private String invokeName = ""; //调用名称

    private String oldMenuCode = ""; //用于验证菜单编号是否重复
    private int oldOrderCode; //用于验证排序编号是否重复
    private String status = ""; //是否记入系统信息状态  lzp 11.28 add
    
    //---add by songjie 2012.02.11 STORY #1917 QDV4赢时胜(上海开发部)2011年11月24日02_A start---//
    private String searchContent = "";//搜索菜单内容
    public String getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}
	//---add by songjie 2012.02.11 STORY #1917 QDV4赢时胜(上海开发部)2011年11月24日02_A end---//
	
	public MenuBean() {}

    public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
    }

    public String getInvokeCode() {
        return invokeCode;
    }

    public void setInvokeCode(String invokeCode) {
        this.invokeCode = invokeCode;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public void setInvokeName(String invokeName) {
        this.invokeName = invokeName;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public String getShortCut() {
        return shortCut;
    }

    public void setShortCut(String shortCut) {
        this.shortCut = shortCut;
    }

    public boolean getCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDllName() {
        return dllName;
    }

    public void setDllName(String dllName) {
        this.dllName = dllName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public void checkInput(byte btOper) throws YssException {
        String sql = "";
        String tmpValue = "";
        if (!menuCode.equalsIgnoreCase(this.oldMenuCode)) {
            sql = "select FMenuName from Tb_Fun_Menu where FMenuCode = '" +
                this.menuCode + "'";
            tmpValue = dbFun.GetValuebySql(sql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("菜单代码【" + this.menuCode + "】已被菜单【" +
                                       tmpValue + "】占用，请重新输入菜单代码");
            }
        }
        if (this.oldOrderCode != this.orderCode) {
            sql = "select FMenuName from Tb_Fun_Menu where FOrderCode = '" +
                dbFun.treeBuildOrderCode("Tb_Fun_Menu", "FMenuCode",
                                         this.parentCode,
                                         this.orderCode) + "'";
            tmpValue = dbFun.GetValuebySql(sql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("菜单排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入菜单排序号");
            }
        }

    }

    /**
     * protocolParse
     * 解析菜单设置数据
     * @param sReq String
     * @throws YssException
     */

    public void parseRowStr(String sReq) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sReq.trim().length() == 0) {
                return;
            }
            if (sReq.indexOf("\r\t") >= 0) {
                sTmpStr = sReq.split("\r\t")[0];
            } else {
                sTmpStr = sReq;
            }
            reqAry = sReq.split("\t");

            reqAry = sReq.split("\t");
            this.menuCode = reqAry[0];
            this.menuName = reqAry[1];
            this.parentCode = reqAry[2];
            this.iconPath = reqAry[3];
            this.desc = reqAry[4];
            this.shortCut = reqAry[5];
            this.enabled = reqAry[6].equalsIgnoreCase("true");
            this.check = reqAry[7].equalsIgnoreCase("true");
            this.dllName = reqAry[8];
            this.className = reqAry[9];
            this.methodName = reqAry[10];
            this.params = reqAry[11];
            this.invokeCode = reqAry[12];
            this.invokeName = reqAry[13];
            this.orderCode = Integer.parseInt(reqAry[14]);
            this.oldMenuCode = reqAry[15];
            this.oldOrderCode = Integer.parseInt(reqAry[16]);
            this.status = reqAry[17]; //lzp add 11.28
        } catch (Exception e) {
            throw new YssException("解析菜单设置协议出错", e);
        }
    }

    /**
     * getAllMenus
     * 获取所有菜单数据
     * @throws YssException
     * @return String
     */
    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @throws YssException
     */


    /**
     * addMenus
     * 增加菜单
     * @throws YssException
     */
    public String addSetting() throws YssException {
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Fun_Menu" +
                "(FMenuCode,FMenuName,FParentCode,FCheck,FIconPath,FShortCutKey,FEnabled,FRefInvokeCode,FDesc,FOrderCode)" +
                " values(?,?,?,?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.menuCode);
            pst.setString(2, this.menuName);
            pst.setString(3, this.parentCode);
            pst.setBoolean(4, this.check);
            pst.setString(5, this.iconPath);
            pst.setString(6, this.shortCut);
            pst.setBoolean(7, this.enabled);
            pst.setString(8, this.invokeCode);
            pst.setString(9, this.desc);
            pst.setString(10,
                          dbFun.treeBuildOrderCode("Tb_Fun_Menu", "FMenuCode",
                this.parentCode,
                this.orderCode));
            pst.executeUpdate();
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                String strSql = "insert into Tb_Fun_Menu" +
                    "(FMenuCode,FMenuName,FParentCode,FCheck,FIconPath,FShortCutKey,FEnabled,FRefInvokeCode,FDesc,FOrderCode)" +
                    " values(" + dbl.sqlString(this.menuCode) + "," + dbl.sqlString(this.menuName) + "," +
                    dbl.sqlString(this.parentCode) + "," + this.check + "," + dbl.sqlString(this.iconPath) +
                    "," +
                    dbl.sqlString(this.shortCut) + "," + dbl.sqlBoolean(this.enabled) + "," + dbl.sqlString(this.invokeCode) +
                    "," + dbl.sqlString(this.desc) + "," +
                    dbl.sqlString(dbFun.treeBuildOrderCode("Tb_Fun_Menu", "FMenuCode",
                    this.parentCode, this.orderCode)) +
                    ")";
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-系统菜单设定");
                sysdata.setStrCode(this.menuCode);
                sysdata.setStrName(this.menuName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new YssException("增加菜单设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * editMenus
     * 修改菜单
     * @throws YssException
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        if (this.oldOrderCode != this.orderCode) {
            dbFun.treeAdjustOrder("Tb_Fun_Menu", "FMenuCode", this.oldMenuCode,
                                  this.orderCode);
        }
        dbFun.treeAdjustParentCode("Tb_Fun_Menu", "FMenuCode", this.oldMenuCode,
                                   this.menuCode);

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "update Tb_Fun_Menu set FMenuCode = ?, FMenuName = ?, FParentCode = ?, FCheck = ?, FIconPath = ?, FShortCutKey = ?, " +
                " FEnabled = ?, FRefInvokeCode = ?, FDesc = ?, FOrderCode = ?" +
                " where FMenuCode = '" + this.oldMenuCode + "'",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.menuCode);
            pst.setString(2, this.menuName);
            pst.setString(3, this.parentCode);
            pst.setBoolean(4, this.check);
            pst.setString(5, this.iconPath);
            pst.setString(6, this.shortCut);
            pst.setBoolean(7, this.enabled);
            pst.setString(8, this.invokeCode);
            pst.setString(9, this.desc);
            pst.setString(10,
                          dbFun.treeBuildOrderCode("Tb_Fun_Menu", "FMenuCode",
                this.parentCode,
                this.orderCode));
            pst.executeUpdate();
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                String strSql = "update Tb_Fun_Menu set FMenuCode =" +
                    dbl.sqlString(this.menuCode) + ", FMenuName = " + dbl.sqlString(this.menuName) +
                    ", FParentCode =" + dbl.sqlString(this.parentCode) + ", FCheck = " +
                    this.check + ", FIconPath = " + dbl.sqlString(this.iconPath) +
                    ", FShortCutKey = " + dbl.sqlString(this.shortCut) + ", " +
                    " FEnabled = " + dbl.sqlBoolean(this.enabled) + ", FRefInvokeCode = " +
                    dbl.sqlString(this.invokeCode) + ", FDesc = " + dbl.sqlString(this.desc) +
                    ", FOrderCode = " +
                    dbl.sqlString(dbFun.treeBuildOrderCode("Tb_Fun_Menu", "FMenuCode",
                    this.parentCode, this.orderCode)) +
                    "" +
                    " where FMenuCode = " + dbl.sqlString(this.oldMenuCode);
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-系统菜单设定");
                sysdata.setStrCode(this.oldMenuCode);
                sysdata.setStrName(this.menuName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改菜单设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public void checkSetting() throws YssException {}

    /**
     * delMenus
     * 删除菜单
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from Tb_Fun_Menu where FMenuCode = '" + this.menuCode +
                "'";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.28
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-系统菜单设定");
                sysdata.setStrCode(this.menuCode);
                sysdata.setStrName(this.menuName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除菜单设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.menuCode.trim()).append("\t");
        buf.append(this.menuName.trim()).append("\t");
        buf.append(this.parentCode.trim()).append("\t");
        buf.append(this.orderCode).append("\t");
        buf.append(this.desc.trim()).append("\t");
        buf.append(this.shortCut.trim()).append("\t");
        buf.append(this.iconPath.trim()).append("\t");
        buf.append(this.enabled).append("\t");
        buf.append(this.check).append("\t");
        buf.append(this.dllName.trim()).append("\t");
        buf.append(this.className.trim()).append("\t");
        buf.append(this.methodName.trim()).append("\t");
        buf.append(this.params.trim()).append("\t");
        buf.append(this.invokeCode.trim()).append("\t");
        //----- modify by wangzuochun 2010.04.15 MS01081   系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB   
        buf.append(this.invokeName.trim()).append("\t");
        buf.append(this.checkStateId).append("\tnull");// add checkstate
        //------------------MS01081-------------------//
        return buf.toString();
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return null;
    }

    /**
     * getSetting ：
     * 获取一条设置信息
     * @return ParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        return null;
    }
    
    /**
     * modify by wangzuochun 2010.04.15 MS01081   系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB 
     */
    public String getListViewData1() throws YssException {
    	String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "菜单代码\t菜单名称\t结点描述";
            
            strSql = "select fmenucode,fmenuname,fparentcode, " + 
            		" case when fparentcode = '[root]' then '根节点' else '' end as fparentname " +
            		" from tb_fun_menu order by fordercode";
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FMenuCode")).append("\t");
                bufShow.append(rs.getString("FMenuName")).append("\t");
                bufShow.append(rs.getString("FParentname")).append(YssCons.YSS_LINESPLITMARK);
                this.menuCode = rs.getString("FMenuCode");
                this.menuName = rs.getString("FMenuName");
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                "FMenuCode\tFMenuName\tFParentname";

        } catch (Exception ex) {
            throw new YssException("获取菜单数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2 ：
     * 获取选择listView控件的数据
     * @return String
     */
    public String getListViewData2() throws YssException {
        return null;
    }

    public String getListViewData4() throws YssException {
        return null;
    }

    /**
     * getTreeViewData1 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        try {
            strSql = "select * from Tb_Fun_Menu  a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.menuCode = rs.getString("FMenuCode") + "";
                this.menuName = rs.getString("FMenuName") + "";
                this.parentCode = rs.getString("FParentCode") + "";
                this.check = rs.getBoolean("FCheck");
                this.enabled = rs.getBoolean("FEnabled");
                this.iconPath = rs.getString("FIconPath") + "";
                this.dllName = rs.getString("FDllName") + "";
                this.className = rs.getString("FClassName") + "";
                this.methodName = rs.getString("FMethodName") + "";
                this.params = rs.getString("FParams") + "";
                this.desc = rs.getString("FDesc") + "";
                this.shortCut = rs.getString("FShortCutKey") + "";
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString(
                    "FOrderCode").length() - 3));
                this.invokeCode = rs.getString("FRefInvokeCode") + "";
                this.invokeName = rs.getString("FRefInvokeName") + "";
                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                return sResult.substring(0, sResult.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取所有菜单出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
        }
    }

    /**
     * getTreeViewData2 ：
     * 获取选择TreeView控件的数据
     * @return String
     */
    public String getTreeViewData2() throws YssException {
        return null;
    }

    /**
     * getTreeViewData3 ：
     * 获取选择TreeView控件的数据
     * yeshenghong anotation: the tb_fun_munu is useless
     * @return String
     */
    public String getTreeViewData3() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        String[] tmpAry = null;
        String tmpStr = "";
        try {
            tmpAry = pub.getUserMenuCodes().split(",");
            //2008.03.02 蒋锦 修改 因为 DB2 的 WHERE IN 语句的限制问题，使用 WHERE OR 代替 WHERE IN
            for (int i = 0; i < tmpAry.length; i++) {
                tmpStr += "FMenuCode = " + dbl.sqlString(tmpAry[i]) + " OR ";
            }

            if (tmpStr.length() > 1) {
                tmpStr = tmpStr.substring(0, tmpStr.length() - 4);
            }

            strSql = "select * from Tb_Fun_Menu a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode " +
                "where " + tmpStr + 
                // add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A
                (this.searchContent.equals("")? "":" and FMENUNAME like '%" + this.searchContent + "%'") + 
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.menuCode = rs.getString("FMenuCode") + "";
                this.menuName = rs.getString("FMenuName") + "";
                this.parentCode = rs.getString("FParentCode") + "";
                this.check = rs.getBoolean("FCheck");
                this.enabled = rs.getBoolean("FEnabled");
                this.iconPath = rs.getString("FIconPath") + "";
                this.dllName = rs.getString("FDllName") + "";
                this.className = rs.getString("FClassName") + "";
                this.methodName = rs.getString("FMethodName") + "";
                this.params = rs.getString("FParams") + "";
                this.desc = rs.getString("FDesc") + "";
                this.shortCut = rs.getString("FShortCutKey") + "";
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString("FOrderCode").length() - 3));
                this.invokeCode = rs.getString("FRefInvokeCode") + "";
                this.invokeName = rs.getString("FRefInvokeName") + "";
                sResult += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (sResult.length() > 2) {
                return sResult.substring(0, sResult.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取用户菜单出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); // close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        MenuBean befEditBean = new MenuBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = strSql = "select * from Tb_Fun_Menu  a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode " +
                " where  a.FMenuCode =" + dbl.sqlString(this.oldMenuCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.menuCode = rs.getString("FMenuCode") + "";
                befEditBean.menuName = rs.getString("FMenuName") + "";
                befEditBean.parentCode = rs.getString("FParentCode") + "";
                befEditBean.check = rs.getBoolean("FCheck");
                befEditBean.enabled = rs.getBoolean("FEnabled");
                befEditBean.iconPath = rs.getString("FIconPath") + "";
                befEditBean.dllName = rs.getString("FDllName") + "";
                befEditBean.className = rs.getString("FClassName") + "";
                befEditBean.methodName = rs.getString("FMethodName") + "";
                befEditBean.params = rs.getString("FParams") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.shortCut = rs.getString("FShortCutKey") + "";
                befEditBean.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                    substring(rs.getString(
                        "FOrderCode").length() - 3));
                befEditBean.invokeCode = rs.getString("FRefInvokeCode") + "";
                befEditBean.invokeName = rs.getString("FRefInvokeName") + "";

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
      	}finally {
    		  dbl.closeResultSetFinal(rs);//add by liuwei 20100604  合并太平版本
     	}

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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
