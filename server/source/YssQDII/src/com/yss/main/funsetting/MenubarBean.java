package com.yss.main.funsetting;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.syssetting.RightBean;
import com.yss.util.*;

public class MenubarBean
    extends BaseDataSettingBean implements IDataSetting {

    private String menubarCode = ""; //菜单条代码
    private String menubarName = ""; //菜单条名称
    private String groupCode = ""; //上级代码
    private String iconPath = ""; //图标路径
    private boolean enabled; //是否可用
    private int orderCode; //排序号
    private String dllName = ""; //DLL名
    private String className = ""; //类名
    private String methodName = ""; //方法名
    private String params = ""; //参数
    private String refInvokeCode = ""; //调用代码
    private String refInvokeName = ""; //调用名称
    private String operTypeCode = ""; //操作类型代码
    private String operTypeName = ""; //操作类型名称
    private String desc = ""; //菜单条描述
    private String status = ""; //是否记入系统信息状态  lzp 11.29 add
    private String oldmenubarCode = "";
    private int oldorderCode;
    private String rightType = "";
    private String rightTypeName = ""; //权限类型名称 add by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
    
    private String innerGroup = "" ;
    
    //---add by songjie 2012.02.11 STORY #1917 QDV4赢时胜(上海开发部)2011年11月24日02_A start---//
    private String searchContent = "";//搜索菜单内容
    
    public String getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
	}
	//---add by songjie 2012.02.11 STORY #1917 QDV4赢时胜(上海开发部)2011年11月24日02_A end---//
	
	//add by guolongchao 20110914 STORY 1285 添加菜单条对应的主表名称
    private String tabMainCode;
    private String tabMainName;
    
    public String getTabMainCode() {
		return tabMainCode;
	}

	public void setTabMainCode(String tabMainCode) {
		this.tabMainCode = tabMainCode;
	}

	public String getTabMainName() {
		return tabMainName;
	}

	public void setTabMainName(String tabMainName) {
		this.tabMainName = tabMainName;
	}
	//add by guolongchao 20110914 STORY 1285 添加菜单条对应的主表名称---end
	
	public void setYssPub(YssPub pub) {
        super.setYssPub(pub);
    }

    public String getMenubarCode() {
        return menubarCode;
    }

    public void setMenubarCode(String menubarCode) {
        this.menubarCode = menubarCode;
    }

    public String getMenubarName() {
        return menubarName;
    }

    public void setMenubarName(String menubarName) {
        this.menubarName = menubarName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String geticonPath() {
        return iconPath;
    }

    public void seticon(String iconPath) {
        this.iconPath = iconPath;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public String getdllName() {
        return dllName;
    }

    public void setdllName(String dllName) {
        this.dllName = dllName;
    }

    public String getclassName() {
        return className;
    }

    public void setclassName(String className) {
        this.className = className;
    }

    public String getmethodName() {
        return methodName;
    }

    public void setmethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getparams() {
        return params;
    }

    public void setparams(String params) {
        this.params = params;
    }

    public String getrefInvokeCode() {
        return refInvokeCode;
    }

    public void setrefInvokeCode(String refInvokeCode) {
        this.refInvokeCode = refInvokeCode;
    }

    public String getrefInvokeName() {
        return refInvokeName;
    }

    public void setrefInvokeName(String refInvokeName) {
        this.refInvokeName = refInvokeName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

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
            this.menubarCode = reqAry[0];
            this.menubarName = reqAry[1];
            this.groupCode = reqAry[2];
            this.iconPath = reqAry[3];
            this.enabled = reqAry[4].equalsIgnoreCase("true");
            this.orderCode = Integer.parseInt(reqAry[5]);
            this.dllName = reqAry[6];
            this.className = reqAry[7];
            this.methodName = reqAry[8];
            this.params = reqAry[9];
            this.refInvokeCode = reqAry[10];
            this.refInvokeName = reqAry[11];
            //add by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
            this.operTypeCode = reqAry[12];
            this.operTypeName = reqAry[13];
            this.desc = reqAry[14];
            this.rightType = reqAry[15];
            this.oldmenubarCode = reqAry[16];
            this.oldorderCode = Integer.parseInt(reqAry[17]);
            this.tabMainCode=reqAry[18]; //add by guolongchao 20110914 STORY 1285 添加菜单条对应的主表名称
            this.tabMainName=reqAry[19]; //add by guolongchao 20110914 STORY 1285 添加菜单条对应的主表名称
            this.tabMainName=reqAry[20]; //add by yeshenghong 20121217
            this.status = reqAry[21]; //lzp add 11.29
        } catch (Exception e) {
            throw new YssException("解析菜单条设置协议出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.menubarCode.trim()).append("\t");
        buf.append(this.menubarName.trim()).append("\t");
        buf.append(this.groupCode.trim()).append("\t");
        buf.append(this.orderCode).append("\t");
        buf.append(this.enabled).append("\t");
        buf.append(this.iconPath.trim()).append("\t");
        buf.append(this.dllName.trim()).append("\t");
        buf.append(this.className.trim()).append("\t");
        buf.append(this.methodName.trim()).append("\t");
        buf.append(this.params.trim()).append("\t");
        buf.append(this.refInvokeCode.trim()).append("\t");
        buf.append(this.refInvokeName.trim()).append("\t");
        //add by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
        buf.append(this.operTypeCode.trim()).append("\t");
        buf.append(this.operTypeName.trim()).append("\t");
        buf.append(this.rightType.trim()).append("\t");
        buf.append(this.rightTypeName.trim()).append("\t");
        //----- modify by wangzuochun 2010.04.15 MS01081    系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB   
        buf.append(this.desc.trim()).append("\t");        
        //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
        buf.append(this.tabMainCode.trim()).append("\t");        
        buf.append(this.tabMainName.trim()).append("\t"); 
        //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称----end  
        //ADD BY YESHENGHONG  INNER GROUP CODE
        if(this.innerGroup==null)//add by yeshenghong to avoid null pointer exception 20121225
        {
        	buf.append("").append("\t");
        }else
        {
        	buf.append(this.innerGroup.trim()).append("\t");
        }
        buf.append(this.checkStateId).append("\tnull");// add checkstate
        //--------------------MS01081-----------------//
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        String strSql = "";
        String tmpValue = "";
        if (!this.menubarCode.equalsIgnoreCase(this.oldmenubarCode)) {
            strSql = "select FBarCode from Tb_Fun_Menubar where FBarCode = '" +
                this.menubarCode + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
            	//edit by songjie 2011.08.16 BUG QDV4赢时胜(测试)2011年7月5日06_B
                throw new YssException("菜单代码【" + this.menubarCode + "】已被占用，请重新输入菜单代码");
            }
        }
        if (this.oldorderCode != this.orderCode) {
            strSql = "select fordercode from Tb_Fun_Menubar where fordercode = '" +
                dbFun.treeBuildOrderCode("Tb_Fun_Menubar", "fbarcode",
                                         this.groupCode, this.orderCode) + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("菜单排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入菜单排序号");
            }
        }

    }

    public String addSetting() throws YssException {
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        /*   String chkordercode = dbFun.treeBuildOrderCode("Tb_Fun_Menubar",
                 "fbarcode", this.groupCode, this.orderCode);
           if (chkordercode.length() > 6) {
              throw new YssException("只能新建一级和二级菜单");
           }*/
        try {
            // checkInput();
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            //update by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            PreparedStatement pst = conn.prepareStatement(
                "insert into Tb_Fun_Menubar" +
                "(FBarCode,FBarName,FBarGroupCode,FIconPath,FEnabled,FOrderCode,FRefInvokeCode, FOperTypeCode,FDesc,FRightType,FTabMainCode,FTabMainName,FInnerGroup)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.menubarCode);
            pst.setString(2, this.menubarName);
            pst.setString(3, this.groupCode);
            pst.setString(4, this.iconPath);
            pst.setBoolean(5, this.enabled);

            pst.setString(6,
                          dbFun.treeBuildOrderCode("Tb_Fun_Menubar", "fbarcode",
                this.groupCode,
                this.orderCode));
            pst.setString(7, this.refInvokeCode);
            //2009-02-27 蒋锦 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
            //插入操作类型
            pst.setString(8, this.operTypeCode);
            pst.setString(9, this.desc);
            pst.setString(10, this.rightType);
            pst.setString(11, this.tabMainCode);//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            pst.setString(12, this.tabMainName);//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            pst.setString(13, this.innerGroup);//add by yeshenghong 2917
            pst.executeUpdate();
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                String strSql = "insert into Tb_Fun_Menubar" +
                    "(FBarCode,FBarName,FBarGroupCode,FIconPath,FEnabled,FOrderCode,FRefInvokeCode, FOperTypeCode, FDesc,FRightType,FTabMainCode,FTabMainName,FInnerGroup)" +
                    " values(" + dbl.sqlString(this.menubarCode) + "," +
                    dbl.sqlString(this.menubarName) + "," +
                    dbl.sqlString(this.groupCode) + "," +
                    dbl.sqlString(this.iconPath) + "," +
                    dbl.sqlBoolean(this.enabled) +
                    "," +
                    dbl.sqlString(dbFun.treeBuildOrderCode("Tb_Fun_Menubar",
                    "fbarcode",
                    this.groupCode, this.orderCode)) + ", " +
                    dbl.sqlString(this.refInvokeCode) + ", " +
                    dbl.sqlString(this.operTypeCode) + ", " + //2009-02-27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                    dbl.sqlString(this.desc) + "," +
                    dbl.sqlString(this.rightType) + //2009-03-03 caocheng 添加 权限类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                    dbl.sqlString(this.tabMainCode) + //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                    dbl.sqlString(this.tabMainName) + //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                    dbl.sqlString(this.innerGroup) +//add by yeshenghong 2917 
                    ")";
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-系统菜单条设定");
                sysdata.setStrCode(this.menubarCode);
                sysdata.setStrName(this.menubarName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new YssException("增加菜单条设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);

        }
        return null;

    }

    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        // chechInput();
        if (this.orderCode != this.oldorderCode) {
            dbFun.treeAdjustOrder("Tb_Fun_Menubar", "fbarcode",
                                  this.oldmenubarCode, this.orderCode);
        }
        dbFun.treeAdjustParentCode("Tb_Fun_Menubar", "FBarGroupCode",
                                   this.oldmenubarCode, this.menubarCode);

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            PreparedStatement pst = conn.prepareStatement(
                "update Tb_Fun_Menubar set fbarcode = ?, fbarname = ?, fbargroupcode = ?, ficonpath = ?, " +
                "fenabled = ?, fordercode = ?, frefinvokecode = ?, FOperTypeCode = ?, fdesc = ?,FRightType=?," +
                " FTabMainCode = ?, FTabMainName = ?,FInnerGroup = ? "+//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                "where fbarcode = '" + this.oldmenubarCode + "'",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            pst.setString(1, this.menubarCode);
            pst.setString(2, this.menubarName);
            pst.setString(3, this.groupCode);
            pst.setString(4, this.iconPath);
            pst.setBoolean(5, this.enabled);
            pst.setString(6,
                          dbFun.treeBuildOrderCode("Tb_Fun_Menubar", "fbarcode",
                this.groupCode,
                this.orderCode));
            pst.setString(7, this.refInvokeCode);
            pst.setString(8, this.operTypeCode); //2009-02-27 蒋锦 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
            pst.setString(9, this.desc);
            pst.setString(10, this.rightType); //2009.03.03 caocheng 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
            pst.setString(11, this.tabMainCode);//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            pst.setString(12, this.tabMainName);//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            pst.setString(13, this.innerGroup);//add by yeshenghong to add inner group code 20121225  story2917
            pst.executeUpdate();
            dbl.closeStatementFinal(pst);//关闭语句行 by leeyu 201000909 
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                String strSql = "update Tb_Fun_Menubar set fbarcode = " +
                    dbl.sqlString(this.menubarCode) + ", fbarname = " +
                    dbl.sqlString(this.menubarName) +
                    ", fbargroupcode = " + dbl.sqlString(this.groupCode) +
                    ", ficonpath = " +
                    dbl.sqlString(this.iconPath) + ", fenabled = " +
                    dbl.sqlBoolean(this.enabled) +
                    ", fordercode = " +
                    dbl.sqlString(dbFun.treeBuildOrderCode("Tb_Fun_Menubar",
                    "fbarcode",
                    this.groupCode, this.orderCode)) +
                    ", frefinvokecode = " + dbl.sqlString(this.refInvokeCode) +
                    ", FOperTypeCode = " + dbl.sqlString(this.operTypeCode) + //2009-02-27 蒋锦 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                    ", fdesc = " +
                    dbl.sqlString(this.desc) +
                    ",FRightType=" +
                    dbl.sqlString(this.rightType) + //by caocheng 2009.03.03 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                    ",FTabMainCode=" +dbl.sqlString(this.tabMainCode) + //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                    ",FTabMainName=" +dbl.sqlString(this.tabMainName) + //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                    dbl.sqlString(this.innerGroup) +//add by yeshenghong 2917 
                    " where fbarcode = " + dbl.sqlString(this.oldmenubarCode);

                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-系统菜单条设定");
                sysdata.setStrCode(this.oldmenubarCode);
                sysdata.setStrName(this.menubarName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改菜单条设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public void delSetting() throws YssException {
        String strSql;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "delete from Tb_Fun_Menubar where FBarcode = '" +
                this.menubarCode + "'";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.29
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-系统菜单条设定");
                sysdata.setStrCode(this.menubarCode);
                sysdata.setStrName(this.menubarName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除菜单条设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return null;
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
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
            sHeader = "菜单条代码\t菜单条名称\t结点描述";
            
            strSql = "select fbarcode,fbarname,fbargroupcode, " + 
            		" case when fbargroupcode = '[root]' then '根节点' else '' end as fbargroupname " +
            		" from tb_fun_menubar order by fordercode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("Fbarcode")).append("\t");
                bufShow.append(rs.getString("FbarName")).append("\t");
                bufShow.append(rs.getString("Fbargroupname")).
                    append(YssCons.YSS_LINESPLITMARK);
                this.menubarCode = rs.getString("Fbarcode");
                this.menubarName = rs.getString("FbarName");
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
                "Fbarcode\tFbarName\tFbargroupname";

        } catch (Exception ex) {
            throw new YssException("获取菜单条数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "菜单编号\t菜单名称";
            strSql = "select * from Tb_Fun_Menubar a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode" +
                " where a.FRefInvokeCode<>'null' order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FBarCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FBarName") + "").trim());
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                setMenubarAttr(rs);
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
            throw new YssException("获取菜单信息数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public void setMenubarAttr(ResultSet rs) throws SQLException {

        this.menubarCode = rs.getString("FBarCode") + ""; //菜单条代码
        this.menubarName = rs.getString("FBarName") + ""; //菜单条名称
        this.groupCode = rs.getString("FBarGroupCode") + ""; //上级代码
        this.iconPath = rs.getString("FIconPath") + ""; //图标路径
        this.enabled = rs.getBoolean("FEnabled"); //是否可用
        this.orderCode = rs.getInt("FOrderCode"); //排序号
        this.refInvokeCode = rs.getString("FRefInvokeCode") + ""; //调用代码
        //2009.02.27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
        this.operTypeCode = rs.getString("FOperTypeCode") + ""; //操作类型代码
        this.desc = rs.getString("FDesc") + ""; //菜单条描述
        //--- 2009.03.03 caocheng 添加 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》----//
        this.rightType = rs.getString("FRightType") + ""; //权限类型
        //--------------------------------------------------------------------------//
        //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
        this.tabMainCode= rs.getString("FTabMainCode") + "";        
        this.tabMainName= rs.getString("FTabMainName") + "";
        //add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称----end
        this.innerGroup = rs.getString("FInnerGroup");//add by yeshenghong 2917 
    }

    //update by guolongchao 20110905 STORY 1285 选择导入数据后能够直接浏览数据的菜单条代码
    public String getListViewData3() throws YssException {
    	 String sHeader = "";
         String sShowDataStr = "";
         String sAllDataStr = "";
         StringBuffer bufShow = new StringBuffer();
         StringBuffer bufAll = new StringBuffer();
         ResultSet rs = null;
         String strSql = "";
         try {
             sHeader = "菜单编号\t菜单名称";
             strSql = "select a.* " +
             		  " from Tb_Fun_Menubar a  where a.FRefInvokeCode<>'null'" +
                      " and a.fbarcode in('security','OperationData','marketvalue','exchangerate','futuretrade','tatrade')" +
                      " order by a.FOrderCode";
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                 bufShow.append( (rs.getString("FBarCode") + "").trim()).append(
                     "\t");
                 bufShow.append( (rs.getString("FBarName") + "").trim());                
                 bufShow.append(YssCons.YSS_LINESPLITMARK);
                 setMenubarAttr(rs);
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
             throw new YssException("获取菜单信息数据出错", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
    }

    public String getListViewData4() throws YssException {
        return null;
    }
    /*
     * by zhouwei 20111124 QDV4赢时胜(上海开发部)2011年11月18日01_A
     * 切换组合群时，获取该组合群下该用户具有的菜单权限和报表权限
     * */
    public String getMenuRightOfAssetGroupCode() throws YssException{
    	ResultSet rs=null;
    	String menuStr="";
		//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    	RightBean right = new RightBean();
    	right.setYssPub(pub);
		//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    	try{
    		right.setUserCode(pub.getUserCode());
    		//非报表类
    		String sql="select a.*,(case when f.frighttypecode is null then a.fbarcode else f.frighttypecode end) as frighttypecode from Tb_Fun_Menubar a inner join (select distinct FRightCode "
    			      +"from tb_sys_userRight d where ((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode ="+ dbl.sqlString(pub.getAssetGroupCode())+") or"
    			      +"(FRightType = 'group' AND FAssetGroupCode = "+dbl.sqlString(pub.getAssetGroupCode())+")) AND d.FuserCode ="+dbl.sqlString(pub.getUserCode())+" and FRightInd = 'Right')"
    			      //20121129 modified by liubo.Story #2737
    			      //获取权限时需要考虑权限继承的问题，包括用户赋予的权限和角色权限。
    			      //即获取权限继承设置中，当天日期大于起始日期，小于结束日期，受托人中包含当前用户的数据，将该条数据记录的属于当前组合群的组合，与委托人代码拼接成OR语句
    			      //===============================
    			      + right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + right.getInheritedRights(pub.getAssetGroupCode(),"public","","login")
    			      +" union select distinct d.Frightcode from (select *  from tb_sys_userright  where FRightInd = 'Role' and (fusercode = "+dbl.sqlString(pub.getUserCode()) + right.getInheritedRights(pub.getAssetGroupCode(), "") + ") "
    			    //===============end================
    			      +" AND (FRightType <> 'group' OR (FRightType = 'group' AND FAssetGroupCode = "+dbl.sqlString(pub.getAssetGroupCode())+"))) c"
    			      +" join (select * from tb_sys_roleright) d on c.frightcode =d.frolecode join (select * from tb_fun_menubar) f on d.frightcode =f.fbarcode and instr(f.frighttype,"
    			      +" c.frighttype) > 0) e on a.fbarcode =e.frightcode left join " 
    			      +" (select r2.frefinvokecode, substr(r2.FParams,instr(r2.FParams,'Frm'),length(r2.FParams)) as frighttypecode from " + 
    			       " (select r.frefinvokecode,substr(r.fparams,instr(r.fparams,',',1,2)+1,(instr(r.fparams,',',1,3)-1-instr(r.fparams,',',1,2)))  as FParams " +
    			       "  from tb_fun_refinvoke r where r.fparams <> 'null' and r.fparams is not null ) r2 where r2.FParams is not null) " 
		      		  +" f on f.frefinvokecode = a.frefinvokecode  order by a.FOrderCode";//add by yeshenghong  权限全部通过barcode获取
	    	rs=dbl.openResultSet(sql);
	    	while(rs.next()){
	    		//菜单条代码与窗体名称      窗体名称+“View” 与菜单条代码（对于视图不考虑）
	    		String frighttypecode=rs.getString("frighttypecode");
	    		if(frighttypecode==null || frighttypecode.equals("")){
	    			frighttypecode=rs.getString("fbarcode");
	    		}
	    		menuStr+=rs.getString("fbarcode")+"\f\f"+frighttypecode+"\r\n";
	    				// +"\r\n"+rs.getString("frighttypecode")+"View"+"\f\f"+rs.getString("fbarcode")+"\r\n";
	    	}
	    	dbl.closeResultSetFinal(rs);
	    	//有权限的报表类
	    	sql="select x.* from (select a.*,b.FRepGrpName as FRepGrpNameParent from "+pub.yssGetTableName("Tb_Rep_Group")+" a left join (select FRepGrpCode, FRepGrpName from "+pub.yssGetTableName("Tb_Rep_Group")
			//---edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
//	    	    +") b on a.FParentCode = b.FRepGrpCode) x join (select FRightCode from tb_sys_userright where fusercode ="+dbl.sqlString(pub.getUserCode())+" and frightind = 'Report' and FAssetGroupCode ="
	    	//20121129 modified by liubo.Story #2737
	    	//获取权限时需要考虑权限继承的问题，包括用户赋予的权限和角色权限。
    		//即获取权限继承设置中，当天日期大于起始日期，小于结束日期，受托人中包含当前用户的数据，将该条数据记录的属于当前组合群的组合，与委托人代码拼接成OR语句
    		//===============================
	    	    +") b on a.FParentCode = b.FRepGrpCode) x join (select FRightCode from tb_sys_userright where (fusercode ="+dbl.sqlString(pub.getUserCode())+ right.getInheritedRights(pub.getAssetGroupCode(), "") + ") and frightind = 'Report' and FAssetGroupCode ="
			//---edit by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	    	    +dbl.sqlString(pub.getAssetGroupCode())+" ) y on x.FRepGrpCode = y.FRightCode where x.fsubrepcodes is not null  order by x.FOrderCode";
	    	rs=dbl.openResultSet(sql);
	    	while(rs.next()){
	    		//取出报表代码+报表标示 "PageReportMark"
	    		String fsubrepcode=rs.getString("fsubrepcodes");
	    		String[] fsubrepcodes=fsubrepcode.split(",");
	    		for(int i=0;i<fsubrepcodes.length;i++){
		    		menuStr+=fsubrepcodes[i]+"\f\f"+"PageReportMark"+"\r\n";
	    		}
	    	}
	    	dbl.closeResultSetFinal(rs);
	    	//不能关闭的报表类
	    	if(menuStr.length()>0){
	    		menuStr=menuStr.substring(0,menuStr.length()-2);
	    	}
	    	menuStr=menuStr+"\f\n";
	    	sql="select x.* from  "+pub.yssGetTableName("Tb_Rep_Group")+" x where x.fsubrepcodes is not null  order by x.FOrderCode";
	    	rs=dbl.openResultSet(sql);
	    	while(rs.next()){
	    		//取出报表代码+报表标示 "PageReportMark"
	    		String fsubrepcode=rs.getString("fsubrepcodes");
	    		String[] fsubrepcodes=fsubrepcode.split(",");
	    		for(int i=0;i<fsubrepcodes.length;i++){
	    			menuStr+=fsubrepcodes[i]+"\f\f"+"PageReportMark"+"\r\n";	    		
	    		}
	    	}
	    	if(menuStr.length()>0){
	    		menuStr=menuStr.substring(0,menuStr.length()-2);
	    	}
    	}catch(Exception e){
    		throw new YssException("获取可显示菜单出错"+e.getMessage(), e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	return menuStr;
    }
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        //----add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
        HashMap hmOper = new HashMap();
        HashMap hmVoc = new HashMap();
        //----add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
        try {
        	//----add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
        	strSql = " select FOperTypeName,FOperTypeCode FROM Tb_Sys_OperationType ";
        	rs = dbl.openResultSet(strSql);
        	while (rs.next()) {
        		if(hmOper.get(rs.getString("FOperTypeCode")) == null){
        			hmOper.put(rs.getString("FOperTypeCode"), rs.getString("FOperTypeName"));
        		}
        	}
        	
			dbl.closeResultSetFinal(rs);
			rs = null;
			
        	strSql = " SELECT FVocCode,FVocName FROM Tb_Fun_Vocabulary WHERE FVocTypeCode = 'sys_righttype' AND FCheckState = 1 ";
        	rs = dbl.openResultSet(strSql);
        	while (rs.next()) {
        		if(hmVoc.get(rs.getString("FVocCode")) == null){
        			hmVoc.put(rs.getString("FVocCode"), rs.getString("FVocName"));
        		}
        	}
        	
			dbl.closeResultSetFinal(rs);
			rs = null;
        	//----add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
			
            strSql = "select * from Tb_Fun_Menubar a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode" +
                " order by a.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.menubarCode = rs.getString("FBarCode") + "";
                this.menubarName = rs.getString("FBarName") + "";
                this.groupCode = rs.getString("FBarGroupCode") + "";
                this.iconPath = rs.getString("FIconPath") + "";
                this.enabled = rs.getBoolean("FEnabled");
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString("FOrderCode").length() - 3));
                this.dllName = rs.getString("FDllName") + "";
                this.className = rs.getString("FClassName") + "";
                this.methodName = rs.getString("FMethodName") + "";
                this.params = rs.getString("FParams") + "";
                this.refInvokeCode = rs.getString("FRefInvokeCode") + "";
                this.refInvokeName = rs.getString("FRefInvokeName") + "";
                //2009.02.27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                this.operTypeCode = rs.getString("Fopertypecode") + "";
                //add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A
                this.operTypeName = getOperTypeNames(this.operTypeCode, hmOper);
                //delete by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A
//                this.operTypeName = getOperTypeNameByCode(rs.getString("Fopertypecode")) + "";
                this.desc = rs.getString("FDesc") + "";
                this.rightType = rs.getString("FRightType") + ""; //2009.02.27 caocheng 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                //add by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A
                this.rightTypeName = getVocNames(this.rightType, hmVoc);
                //delete by songjie 2011.03.31 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A
//                this.rightTypeName = getRightTypeNameBy(rs.getString("FRightType")) + "";
                this.tabMainCode=rs.getString("FTabMainCode")!=null?rs.getString("FTabMainCode"):"";//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                this.tabMainName=rs.getString("FTabMainName")!=null?rs.getString("FTabMainName"):"";//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取所有菜单条出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 add by sunkey 集成发现问题 20090518
        }

    }
    
    /**
     * add by songjie 2011.03.31
     * 需求 127 
     * QDV4赢时胜(开发部)2010年09月09日01_A
     * @param operTypeCode
     * @param hmOper
     * @return
     * @throws YssException
     */
    private String getVocNames(String rightType,HashMap hmVoc) throws YssException{
    	String rightNames = "";
    	String[] rightTypes = null;
    	try{
    		if(rightType == null){
    			return "";
    		}
            if(rightType.indexOf(",") != -1){
            	rightTypes = rightType.split(",");
            }else{
            	rightNames = (hmVoc.get(rightType) == null)?"":(String)hmVoc.get(rightType);
            	return rightNames;
            }
            
            for(int i = 0; i < rightTypes.length; i++){
            	rightNames += (hmVoc.get(rightTypes[i]) == null)?"":(String)hmVoc.get(rightTypes[i]) + ",";
            }
            
            if(rightNames.length() > 0){
            	rightNames = rightNames.substring(0,rightNames.length() - 1);
            }
    		
    		return rightNames;
    	}catch(Exception e){
    		throw new YssException("获取词汇名称出错", e);
    	}
    }
    
    /**
     * add by songjie 2011.03.31
     * 需求 127 
     * QDV4赢时胜(开发部)2010年09月09日01_A
     * @param operTypeCode
     * @param hmOper
     * @return
     * @throws YssException
     */
    private String getOperTypeNames(String operTypeCode, HashMap hmOper)throws YssException {
    	String operTypeNames = "";
    	String[] operTypeCodes = null;
    	try{
    		if(operTypeCode == null){
    			return "";
    		}
            if(operTypeCode.indexOf(",") != -1){
            	operTypeCodes = operTypeCode.split(",");
            }else{
            	operTypeNames = (hmOper.get(operTypeCode) == null)?"":(String)hmOper.get(operTypeCode);
            	return operTypeNames;
            }
            
            for(int i = 0; i < operTypeCodes.length; i++){
            	operTypeNames += (hmOper.get(operTypeCodes[i])== null)?"":(String)hmOper.get(operTypeCodes[i]) + ",";
            }
            
            if(operTypeNames.length() > 0){
            	operTypeNames = operTypeNames.substring(0,operTypeNames.length() - 1);
            }
            
    		return operTypeNames;
    	}catch(Exception e){
    		throw new YssException("获取操作类型名称出错", e);
    	}
    }

    //add by zhouxiang 2010.12.15 日终处理，报表显示
    public String getTreeViewData2() throws YssException {
    	 String strSql = "";
         String result = "";
         ResultSet rs = null;
         try {
             strSql = "select * from Tb_Fun_Menubar a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode" +
                 " where a.fbarcode='dayfinish' or a.fbargroupcode='dayfinish' order by a.FOrderCode";
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                 this.menubarCode = rs.getString("FBarCode") + "";
                 this.menubarName = rs.getString("FBarName") + "";
                 this.groupCode = rs.getString("FBarGroupCode") + "";
                 this.iconPath = rs.getString("FIconPath") + "";
                 this.enabled = rs.getBoolean("FEnabled");
                 this.orderCode = Integer.parseInt(rs.getString("FOrderCode").substring(rs.getString("FOrderCode").length() - 3));
                 this.dllName = rs.getString("FDllName") + "";
                 this.className = rs.getString("FClassName") + "";
                 this.methodName = rs.getString("FMethodName") + "";
                 this.params = rs.getString("FParams") + "";
                 this.refInvokeCode = rs.getString("FRefInvokeCode") + "";
                 this.refInvokeName = rs.getString("FRefInvokeName") + "";
                 this.operTypeCode = rs.getString("Fopertypecode") + "";
                 this.operTypeName = getOperTypeNameByCode(rs.getString("Fopertypecode")) + "";
                 this.desc = rs.getString("FDesc") + "";
                 this.rightType = rs.getString("FRightType") + ""; //2009.02.27 caocheng 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                 this.rightTypeName = getRightTypeNameBy(rs.getString("FRightType")) + "";
                 //---add by songjie 2012.06.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                 this.tabMainCode = rs.getString("FTabMainCode") + "";
                 this.tabMainName = rs.getString("FTabMainName") + "";
                 //---add by songjie 2012.06.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                 result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
             }
             if (result.length() > 2) {
                 return result.substring(0, result.length() - 2);
             } else {
                 return "";
             }
         } catch (Exception ex) {
             throw new YssException("获取所有菜单条出错", ex);
         } finally {
             dbl.closeResultSetFinal(rs); 
         }
    }
    //end by zhouxiang 2010.12.15 日终处理，报表显示

    public String getTreeViewData3() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        String[] tmpAry = null;
        String tmpStr = "";
        RightBean right = new RightBean();
        
        try {
            //2008.03.02 蒋锦 修改 因为 DB2 的 WHERE IN 语句的限制问题，使用 WHERE OR 代替 WHERE IN
            tmpAry = pub.getUserMenubarCodes().split(",");
            for (int i = 0; i < tmpAry.length; i++) {
                tmpStr += "FBarCode = " + dbl.sqlString(tmpAry[i]) + " OR ";
            }

            if (tmpStr.length() > 1) {
                tmpStr = tmpStr.substring(0, tmpStr.length() - 4);
            }

            pub.getUserMenubarCodes();

            //20120814 modified by liubo.Story #2737
            //===============================
            right.setYssPub(pub);
            right.setUserCode(pub.getUserCode());
            //==============end=================

            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 start---//
            boolean judgeCompany = true;
            if(YssCons.companyName.indexOf("赢时胜") != -1){
            	judgeCompany = false;
            }
            //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 end---//
            
//---------------by caocheng 2009.03.22 MS00001 QDV4.1调整加载菜单操作类型的sql语句 与Tb_Sys_RightType关联-------------//
            //2009.04.29 蒋锦 修改 原来 e 表中 Distinct 的字段过多，会出现重复的菜单条代码 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
            //同时不再使用组合群代码作为查询条件
            if(this.searchContent.equals(""))
            {
	            strSql = " select distinct a.*,b.*, c.Fopertypename from " + 
	            		" (select distinct * from tb_fun_navmenubar start with fbarcode in " +
                " (select distinct FRightCode from tb_sys_userRight d " + 
                //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 start---//
                " join (select FBarCode from Tb_fun_menubar " + 
                (judgeCompany ? 
                " where FCompanyName is null or (FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
                ") m1 on d.frightcode = m1.fbarcode where " +
                //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
                //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
                "((d.FRightType = 'public' or d.FRightType = 'system' or (d.FRightType = 'port' AND d.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
                //============================================================================================================================================================
                "(d.FRightType = 'group' AND d.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
                "AND d.FuserCode='" + pub.getUserCode() +
                //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
                	
                //20120814 modified by liubo.Story #2737
                //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
                //=====================================
                //"' and FRightInd = 'Right') " +
                "' and d.FRightInd = 'Right') " + 
                //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 end---//
                right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
                right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
                right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
                //=====================================
                
                " union " + //合并角色中有的那部分权限
                " select distinct d.Frightcode " +
                " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
                " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
              //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                //并上权限继承中委托人的角色权限
                //-----------------------------------
                " " 
                + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
                + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
                +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
                + ") c " + 

                //--------------end---------------------
                
                " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
                " join (select * from tb_fun_menubar where frefinvokecode is not null and frefinvokecode <> 'null' " + //add by yeshenghong 排除非明细菜单
                //edit by songjie 2013.01.23 排除不关联公司名称的菜单
                (judgeCompany ? 
                " and (FCompanyName is null or (FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0))" : "") +  
                ") f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0 " + //增加对不同级别的筛选
                ") connect by prior fbargroupcode = fbarcode) a " +
                
                //------------------------------------------------------------------------------------------------------------------------------//
                " left join (select * from Tb_Fun_RefInvoke) b on a.FRefInvokeCode = b.FRefInvokeCode " +
                " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode" +
                // add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A
                " order by a.FOrderCode";
            }else
            {
	            strSql = " select distinct a.*,b.*, c.Fopertypename from tb_fun_navmenubar a " +
	            		
			            " left join (select * from Tb_Fun_RefInvoke) b on a.FRefInvokeCode = b.FRefInvokeCode " +
			            " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode" +
			      	  	" where a.fbarcode in " + 
			      		" (select distinct fbargroupcode as fbarcode from tb_fun_navmenubar n " +
			      		//--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 start---//
	            		" join (select fbarcode from Tb_fun_menubar " + 
	                    (judgeCompany ? 
	                    " where FCompanyName is null or (FCompanyName is not null and instr(" + 
	                    dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
	                    " ) m1 on n.fbarcode = m1.fbarcode " +
	                    //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 end---//
			      		" where n.fbarcode in " +
			            	" (select distinct FRightCode from tb_sys_userRight d where " +
			            //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
			            //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
			            "((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
			            //============================================================================================================================================================
			            "(FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
			            "AND FuserCode='" + pub.getUserCode() +
			            //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
			            
			            //20120814 modified by liubo.Story #2737
			            //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
			            //=====================================
			            //"' and FRightInd = 'Right') " +
			            "' and FRightInd = 'Right') " + 
			            right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
			            right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
			            right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
			            //=====================================
			            
			            " union " + //合并角色中有的那部分权限
			            " select distinct d.Frightcode " +
			            " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
			            " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
			          //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
			            //并上权限继承中委托人的角色权限
			            //-----------------------------------
			            " " 
			            + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
			            + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
			            +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
			            + ") c " + 
			            
			            " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
			            " join (select * from tb_fun_navmenubar) f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0) " + //增加对不同级别的筛选
			            " and Fbarname like '%'||  upper(" + dbl.sqlString(this.searchContent) + ")||'%' and Frefinvokecode <> 'null' and Fenabled = 1 " + 
			            
			            " union " + //以上是查询父级菜单 ，下面是查询子菜单
			            
			            " select distinct n.fbarcode from tb_fun_navmenubar n " +
			            //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 start---//
	            		" join (select fbarcode from Tb_fun_menubar " + 
	                    (judgeCompany ? 
	                    " where FCompanyName is null or (FCompanyName is not null and instr(" + 
	                    dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
	                    " ) m1 on n.fbarcode = m1.fbarcode " +
	                    //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 end---//
			            " where n.fbarcode in " +
			          	" (select distinct FRightCode from tb_sys_userRight d where " +
			          //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
			          //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
			          "((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
			          //============================================================================================================================================================
			          "(FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
			          "AND FuserCode='" + pub.getUserCode() +
			          //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
			          
			          //20120814 modified by liubo.Story #2737
			          //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
			          //=====================================
			          //"' and FRightInd = 'Right') " +
			          "' and FRightInd = 'Right') " + 
			          right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
			          right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
			          right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
			          //=====================================
			          
			          " union " + //合并角色中有的那部分权限
			          " select distinct d.Frightcode " +
			          " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
			          " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
			        //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
			          //并上权限继承中委托人的角色权限
			          //-----------------------------------
			          " " 
			          + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
			          + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
			          +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
			          + ") c " + 
			          
			          " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
			          " join (select * from tb_fun_navmenubar) f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0) " + //增加对不同级别的筛选
			          " and Fbarname like '%'||  upper(" + dbl.sqlString(this.searchContent) + ")||'%' and Frefinvokecode <> 'null' and Fenabled = 1 )" +
			            " order by a.FOrderCode"; //add by yeshenghong  查询 时带出父级菜单  --end
            }
        
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                buf.append(rs.getString("FBarCode")).append("\t");
                buf.append(rs.getString("FBarName")).append("\t");
                buf.append(rs.getString("FBarGroupCode")).append("\t");
                buf.append(Integer.parseInt(rs.getString("FOrderCode").
                                            substring(rs.getString(
                                                "FOrderCode").length() - 2))).
                    append("\t");

                buf.append(rs.getBoolean("FEnabled")).append("\t");
                buf.append(rs.getString("FIconPath")).append("\t");
                buf.append(rs.getString("FDllName")).append("\t");
                buf.append(rs.getString("FClassName")).append("\t");
                buf.append(rs.getString("FMethodName")).append("\t");
                buf.append(rs.getString("FParams")).append("\t");
                buf.append(rs.getString("FREFINVOKECODE")).append("\t");
                buf.append(rs.getString("FREFINVOKENAME")).append("\t");
                //2009.02.27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                buf.append(rs.getString("Fopertypecode")).append("\t");
                //获取操作类型代码
                buf.append(getOperTypeNameByCode(rs.getString("Fopertypecode"))).append("\t");
                buf.append(rs.getString("FDESC")).append("\t");
                
                buf.append(rs.getString("FRightType")).append("\t"); //2009.03.03 caocheng 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                buf.append(this.innerGroup).append(YssCons.YSS_LINESPLITMARK);;
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取用户菜单条出错", ex);
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
        finally {
            dbl.closeResultSetFinal(rs); //用于关闭结果集
        }
        //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException{
    	//---add by songjie 2012.05.07 STORY #2487 QDV4赢时胜(上海开发部)2012年4月9日01_A start---//
    	if(sType != null && sType.equals("getRight")){
    		return getUpMenuRight();
    	}
    	//---add by songjie 2012.05.07 STORY #2487 QDV4赢时胜(上海开发部)2012年4月9日01_A end---//
        return "";
    }
    
    /**
     * add by songjie 2012.05.07
     * STORY #2487 QDV4赢时胜(上海开发部)2012年4月9日01_A
     * 获取父节点权限类型数据
     * @return
     * @throws YssException
     */
    private String getUpMenuRight()throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	String rightType = "";
    	try{
    		strSql = " select * from Tb_fun_menubar where FBarCode = " + dbl.sqlString(this.menubarCode);
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			rightType = rs.getString("FRightType");
    		}
    		return rightType;
    	}catch(Exception e){
    		throw new YssException("获取权限数据出错！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        MenubarBean befEditBean = new MenubarBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*, b.*, c.Fopertypename from Tb_Fun_Menubar a left join Tb_Fun_RefInvoke b on a.FRefInvokeCode = b.FRefInvokeCode " +
                " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode" +
                " where  a.FBarCode =" + dbl.sqlString(this.oldmenubarCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.menubarCode = rs.getString("FBarCode") + "";
                befEditBean.menubarName = rs.getString("FBarName") + "";
                befEditBean.groupCode = rs.getString("FBarGroupCode") + "";
                befEditBean.iconPath = rs.getString("FIconPath") + "";
                befEditBean.enabled = rs.getBoolean("FEnabled");
                befEditBean.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                    substring(rs.getString(
                        "FOrderCode").length() - 3));
                befEditBean.dllName = rs.getString("FDllName") + "";
                befEditBean.className = rs.getString("FClassName") + "";
                befEditBean.methodName = rs.getString("FMethodName") + "";
                befEditBean.params = rs.getString("FParams") + "";
                befEditBean.refInvokeCode = rs.getString("FRefInvokeCode") + "";
                befEditBean.refInvokeName = rs.getString("FRefInvokeName") + "";
                //2009.02.27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                befEditBean.operTypeCode = rs.getString("Fopertypecode") + "";
                befEditBean.operTypeName = rs.getString("Fopertypename") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
                befEditBean.desc = rs.getString("FRightType") + ""; //2009.03.03 caocheng 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                befEditBean.tabMainCode=rs.getString("FTabMainCode")!=null?rs.getString("FTabMainCode"):"";//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
                befEditBean.tabMainName=rs.getString("FTabMainName")!=null?rs.getString("FTabMainName"):"";//add by guolongchao STORY 1285 添加添加菜单条对应的主表代码，名称
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 add by sunkey 集成发现问题 20090518
        }
    }

    public String getStatus() {
        return status;
    }

    public String getOperTypeCode() {
        return operTypeCode;
    }

    public String getOperTypeName() {
        return operTypeName;
    }

    public String getRightType() {
        return rightType;
    }

    public String getRightTypeName() {
        return rightTypeName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOperTypeCode(String operTypeCode) {
        this.operTypeCode = operTypeCode;
    }

    public void setOperTypeName(String operTypeName) {
        this.operTypeName = operTypeName;
    }

    public void setRightType(String rightType) {
        this.rightType = rightType;
    }

    public void setRightTypeName(String rightTypeName) {
        this.rightTypeName = rightTypeName;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * 2009.04.22 蒋锦 添加 获取权限类型名称
     * MS00010 权限明细到组合
     * 传入以逗号隔开的权限类型代码，返回以逗号隔开的权限类型名称。
     * @param sRightTypeCode String
     * @return String
     * @throws YssException
     */
    public String getRightTypeNameBy(String sRightTypeCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        try {
            if (sRightTypeCode == null) {
                return "";
            }
            strSql = "SELECT FVocName FROM Tb_Fun_Vocabulary WHERE FVocTypeCode =" +
                dbl.sqlString(YssCons.YSS_SYS_RIGHT_RIGHTTYPE) +
                " AND FVocCode IN (" + operSql.sqlCodes(sRightTypeCode) + ")" +
                " AND FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += (rs.getString("FVocName") + ",");
            }
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
        } catch (Exception ex) {
            throw new YssException("获取权限类型名称出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 2009.02.27 蒋锦 添加 操作类型
     * MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
     * 查询操作类型名称
     * 传入以逗号隔开的操作类型代码，返回以逗号隔开的操作类型名称。
     * @param sOperTypeCode String
     * @return String
     * @throws YssException
     */
    public String getOperTypeNameByCode(String sOperTypeCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "";
        try {
            if (sOperTypeCode == null) {
                return "";
            }
            strSql = "SELECT FOperTypeName FROM Tb_Sys_OperationType" +
                " WHERE FOperTypeCode IN (" + operSql.sqlCodes(sOperTypeCode) + ")" +
                " ORDER BY FOperTypeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += (rs.getString("FOperTypeName") + ",");
            }
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
        } catch (Exception e) {
            throw new YssException("获取操作名称类型出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
    /*
     * modified by  yeshenghong to get mainmenu data
     * story2917
     * 
     * */
    public String getTreeViewGroupData1() throws YssException {
    	 String strSql = "";
         String sResult = "";
         ResultSet rs = null;
         StringBuffer buf = new StringBuffer();
         String[] tmpAry = null;
         String tmpStr = "";
         RightBean right = new RightBean();
         
         try {
             //2008.03.02 蒋锦 修改 因为 DB2 的 WHERE IN 语句的限制问题，使用 WHERE OR 代替 WHERE IN
             tmpAry = pub.getUserMenubarCodes().split(",");
             for (int i = 0; i < tmpAry.length; i++) {
                 tmpStr += "FBarCode = " + dbl.sqlString(tmpAry[i]) + " OR ";
             }

             if (tmpStr.length() > 1) {
                 tmpStr = tmpStr.substring(0, tmpStr.length() - 4);
             }

             pub.getUserMenubarCodes();

             //20120814 modified by liubo.Story #2737
             //===============================
             right.setYssPub(pub);
             right.setUserCode(pub.getUserCode());
             //==============end=================

             //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 start---//
             boolean judgeCompany = true;
             if(YssCons.companyName.indexOf("赢时胜") != -1){
             	judgeCompany = false;
             }
             //--- add by songjie 2013.01.25 是否根据 FCompanyName 判断菜单显示 end---//
             
 //---------------by caocheng 2009.03.22 MS00001 QDV4.1调整加载菜单操作类型的sql语句 与Tb_Sys_RightType关联-------------//
             //2009.04.29 蒋锦 修改 原来 e 表中 Distinct 的字段过多，会出现重复的菜单条代码 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
             //同时不再使用组合群代码作为查询条件
//             strSql = "select a.*, b.*, c.Fopertypename from Tb_Fun_Menubar a " +
//                 "inner join(select distinct FRightCode from tb_sys_userRight d where " +
//                 //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
//                 //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
//                 "((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
//                 //============================================================================================================================================================
//                 "(FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
//                 "AND d.FuserCode='" + pub.getUserCode() +
//                 //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
//                 
//                 //20120814 modified by liubo.Story #2737
//                 //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
//                 //=====================================
//                 //"' and FRightInd = 'Right') " +
//                 "' and FRightInd = 'Right') " + right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
//                 //=====================================
//                 " union " +
//                 " select distinct m.fbarcode from tb_fun_menubar m join " +
//                 " (select m1.fbargroupcode from tb_sys_userRight u1 join tb_fun_menubar m1 on u1.frightcode = m1.fbarcode " +
//                 "  where u1.fusercode = '" + pub.getUserCode() + "' ) u " +
//                 " on instr(u.fbargroupcode,m.fbarcode)>0 where m.fbargroupcode = '[root]' " +
//                 " union " + //合并角色中有的那部分权限
//                 " select distinct d.Frightcode " +
//                 " from (select *  from tb_sys_userright where FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
//                 " AND (FRightType <> 'group' OR (FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) +
//                 "))) c " + //获取此用户角色的权限
//                 " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
//                 " join (select * from tb_fun_menubar) f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0 " + //增加对不同级别的筛选
//                 ") e " + "on a.fbarcode=e.frightcode" +
//                 //------------------------------------------------------------------------------------------------------------------------------//
//                 " left join (select * from Tb_Fun_RefInvoke) b on a.FRefInvokeCode = b.FRefInvokeCode " +
//                 " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode" +
//                 // add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A
//                 (this.searchContent.equals("") ? "" : " where a.Fbarname like '%" + this.searchContent + "%' and a.Frefinvokecode <> 'null' and a.Fenabled = 1 ") + 
//                 " order by a.FOrderCode";
 //-------------------------------------------------------------------------------------------------------//
             //----modified by yeshenghong 主菜单的查询也通过递归查询实现-----------------------------------------------------//
             //---------------by caocheng 2009.03.22 MS00001 QDV4.1调整加载菜单操作类型的sql语句 与Tb_Sys_RightType关联-------------//
               //2009.04.29 蒋锦 修改 原来 e 表中 Distinct 的字段过多，会出现重复的菜单条代码 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
               //同时不再使用组合群代码作为查询条件
               if(this.searchContent.equals(""))
               {
               strSql = " select distinct a.*,b.*, c.Fopertypename from " + 
               		" (select distinct * from tb_fun_menubar start with fbarcode in " +
                   	" (select distinct FRightCode from tb_sys_userRight d " +
                   	//--- edit by songjie 2013.01.23 排除不关联公司名称的菜单  start---//
                   	" join " + 
                   	" (select FBarCode from Tb_fun_menubar " + 
                    (judgeCompany ? 
                    " where FCompanyName is null or (FCompanyName is not null and instr(" + 
                    dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0)" : "") + 
                   	") m1 on m1.fbarcode = d.frightcode  where " +
                   //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
                   //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
                   "((d.FRightType = 'public' or d.FRightType = 'system' or (d.FRightType = 'port' AND d.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
                   //============================================================================================================================================================
                   "(d.FRightType = 'group' AND d.FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
                   "AND d.FuserCode='" + pub.getUserCode() +
                   //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
                   
                   //20120814 modified by liubo.Story #2737
                   //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
                   //=====================================
                   //"' and FRightInd = 'Right') " +
                   "' and d.FRightInd = 'Right') " + 
                   //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单  end---//
                   right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
                   right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
                   right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
                   //=====================================
                   
                   " union " + //合并角色中有的那部分权限
                   " select distinct d.Frightcode " +
                   " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
                   " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
                 //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                   //并上权限继承中委托人的角色权限
                   //-----------------------------------
                   " " 
                   + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
                   + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
                   +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
                   + ") c " + 
                   
                   " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
                   " join (select * from tb_fun_menubar where frefinvokecode is not null and frefinvokecode <> 'null'" + //add by yeshenghong 排除非明细菜单
                   //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 start---//
                   (judgeCompany ? 
                   " and (FCompanyName is null or (FCompanyName is not null and instr(" + 
                   dbl.sqlString(YssCons.companyName) + ",FCompanyName) > 0))" : "") + 
                   //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 end---//
                   ") f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0 " + //增加对不同级别的筛选
                   ") connect by prior fbargroupcode = fbarcode) a " +
                   
                   //------------------------------------------------------------------------------------------------------------------------------//
                   " left join (select * from Tb_Fun_RefInvoke) b on a.FRefInvokeCode = b.FRefInvokeCode " +
                   " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode" +
                   // add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A
                   " order by a.FOrderCode" +
                   "  ";
               //-----end add by yeshenghong 主菜单也通过递归查询实现   20130118
               }else
               {//add by yeshenghong  查询 时带出父级菜单  --start
            	  strSql = " select distinct a.*,b.*, c.Fopertypename from (select * from tb_fun_menubar a " +
            	  " where a.fbarcode in " + 
            	  " (select distinct fbargroupcode as fbarcode from tb_fun_menubar where fbarcode in " +
                  " (select distinct FRightCode from tb_sys_userRight d where " +
                  //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
                  //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
                  "((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
                  //============================================================================================================================================================
                  "(FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
                  "AND FuserCode='" + pub.getUserCode() +
                  //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
                  
                  //20120814 modified by liubo.Story #2737
                  //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
                  //=====================================
                  //"' and FRightInd = 'Right') " +
                  "' and FRightInd = 'Right') " + 
                  right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
                  right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
                  right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
                  //=====================================
                  
                  " union " + //合并角色中有的那部分权限
                  " select distinct d.Frightcode " +
                  " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
                  " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
                //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                  //并上权限继承中委托人的角色权限
                  //-----------------------------------
                  " " 
                  + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
                  + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
                  +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
                  + ") c " + 
                  
                  " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
                  " join (select * from tb_fun_menubar) f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0) " + //增加对不同级别的筛选
                  " and Fbarname like '%'||  upper(" + dbl.sqlString(this.searchContent) + ")||'%' and Frefinvokecode <> 'null' and Fenabled = 1 " + 
                  
                  " union " + //以上是查询父级菜单 ，下面是查询子菜单
                  
                  " select distinct fbarcode from tb_fun_menubar where fbarcode in " +
                	" (select distinct FRightCode from tb_sys_userRight d where " +
                //"(FRightType = 'public' or FRightType = 'system' or FRightType = 'port' or " +
                //fanghaoln 20090720 MS00540 QDV4赢时胜（上海）2009年6月21日05_B 新建组合群后登陆系统菜单没有根据权限设置的情况加载  组合代码也要加上当前组合群这个条件区分显示各个组合群的菜单条
                "((FRightType = 'public' or FRightType = 'system' or (FRightType = 'port' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ") or " +
                //============================================================================================================================================================
                "(FRightType = 'group' AND FAssetGroupCode = " + dbl.sqlString(pub.getAssetGroupCode()) + ")) " +
                "AND FuserCode='" + pub.getUserCode() +
                //---- MS00578 QDV4赢时胜（上海）2009年7月24日02_B  sj modified -------------------------------------------------------------------//
                
                //20120814 modified by liubo.Story #2737
                //某个用户初始化主窗体的菜单条时，考虑该用户是否为某条权限继承设置中的受托人的情况
                //=====================================
                //"' and FRightInd = 'Right') " +
                "' and FRightInd = 'Right') " + 
                right.getInheritedRights(pub.getAssetGroupCode(),"port","","login") + 
                right.getInheritedRights(pub.getAssetGroupCode(),"group","","login") + 
                right.getInheritedRights(pub.getAssetGroupCode(),"public","","login") +
                //=====================================
                
                " union " + //合并角色中有的那部分权限
                " select distinct d.Frightcode " +
                " from (select *  from tb_sys_userright where (FRightInd = 'Role' and fusercode = " + dbl.sqlString(pub.getUserCode()) +
                " AND (FRightType in ('datainterface','report','public') or (FRightType in ('group','port') and (FAssetGroupCode = '" + pub.getAssetGroupCode() + "' or FAssetGroupCode = ' '))))" +//获取此用户角色的权限
              //20121127 added by liubo.56sp3海富通测试问题：权限继承的时候需要获取委托人的角色权限
                //并上权限继承中委托人的角色权限
                //-----------------------------------
                " " 
                + right.getInheritedRoleRights(pub.getAssetGroupCode(), "port", "", "") 
                + right.getInheritedRoleRights(pub.getAssetGroupCode(), "group", "", "") 
                +right.getInheritedRoleRights(pub.getAssetGroupCode(), "public", "", "") 
                + ") c " + 
                
                " join (select * from tb_sys_roleright) d on c.frightcode = d.frolecode " + //获取此用户的角色
                " join (select * from tb_fun_menubar) f on  d.frightcode = f.fbarcode and instr(f.frighttype,c.frighttype) > 0) " + //增加对不同级别的筛选
                " and Fbarname like '%'||  upper(" + dbl.sqlString(this.searchContent) + ")||'%' and Frefinvokecode <> 'null' and Fenabled = 1 )" +
                " ) a" +
                " left join (select * from Tb_Fun_RefInvoke) b on a.FRefInvokeCode = b.FRefInvokeCode " +
                " LEFT JOIN Tb_Sys_Operationtype c ON a.Fopertypecode = c.FOperTypeCode " +
                //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 start---//
                (judgeCompany ? 
                " where a.FCompanyName is null or (a.FCompanyName is not null and instr(" + 
                dbl.sqlString(YssCons.companyName) + ",a.FCompanyName) > 0)" : "") + 
                " order by a.FOrderCode ";
          	  //--- edit by songjie 2013.01.23 排除不关联公司名称的菜单 end---//;
                //add by yeshenghong  查询 时带出父级菜单  --end
               }
             // add dongqingsong 2013-10-12 STORY #12967组合设置权限要求明细到组合
             boolean flag =this.isExistSystemAndPort(strSql);//判断是否同时存在系统级和组合级权限 ，true =同时存在，false =存在其一
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
            	 if(flag&&rs.getString("FBarCode").equalsIgnoreCase("m_portfolio")){continue;} 
             // end dongqingsong 2013-10-12 STORY #12967组合设置权限要求明细到组合	 
                 buf.append(rs.getString("FBarCode")).append("\t");
                 buf.append(rs.getString("FBarName")).append("\t");
                 buf.append(rs.getString("FBarGroupCode")).append("\t");
                 buf.append(Integer.parseInt(rs.getString("FOrderCode").
                                             substring(rs.getString(
                                                 "FOrderCode").length() - 3))).
                     append("\t");

                 buf.append(rs.getBoolean("FEnabled")).append("\t");
                 buf.append(rs.getString("FIconPath")).append("\t");
                 buf.append(rs.getString("FDllName")).append("\t");
                 buf.append(rs.getString("FClassName")).append("\t");
                 buf.append(rs.getString("FMethodName")).append("\t");
                 buf.append(rs.getString("FParams")).append("\t");
                 buf.append(rs.getString("FREFINVOKECODE")).append("\t");
                 buf.append(rs.getString("FREFINVOKENAME")).append("\t");
                 //2009.02.27 蒋锦 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                 buf.append(rs.getString("Fopertypecode")).append("\t");
                 //获取操作类型代码
                 buf.append(getOperTypeNameByCode(rs.getString("Fopertypecode"))).append("\t");
                 buf.append(rs.getString("FDESC")).append("\t");
                 buf.append(rs.getString("FRightType")).append("\t"); //2009.03.03 caocheng 添加 操作类型 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
                 buf.append(rs.getString("FInnerGroup")).append(YssCons.YSS_LINESPLITMARK);
             }
             if (buf.toString().length() > 2) {
                 sResult = buf.toString().substring(0, buf.toString().length() - 2);
             }
             return sResult;
         } catch (Exception ex) {
             throw new YssException("获取用户菜单条出错", ex);
         }
         //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
         finally {
             dbl.closeResultSetFinal(rs); //用于关闭结果集
         }
         //----MS00010 QDV4赢时胜（上海）2009年02月01日10_A add by songjie 2009-05-18----//
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
    /**
     *add dongqingsong 2013-10-12 STORY #12967组合设置权限要求明细到组合
     *判断是否同时存在组合级【组合设置】和系统级【组合设置】菜单    如果同时存在 为true 
     *1.系统级【组合设置】和组合级【组合设置】菜单同时存在时，只加载系统级组合设置菜单
     *2.系统级【组合设置】存在和组合级【组合设置】不存在时，只加载系统级组合设置菜单
     * 始终在菜单条中显示一个【组合设置】
     * @param rs
     * @return
     * @throws YssException 
     * @throws SQLException 
     */
    private boolean isExistSystemAndPort(String sql) throws Exception{
    	boolean flag = false;
    	ArrayList list = new ArrayList();
    		try {
    			ResultSet rs= dbl.openResultSet(sql);
    			if(rs!=null){
					while(rs.next()){
						String barcode = rs.getString("FBarCode");
						list.add(barcode);
					}
					if(list.contains("portfolio")&&list.contains("m_portfolio")){
						flag = true;
					}
    			}
    			rs.close();
			} catch (Exception e) {
				System.out.println("判断是否存在组合级和系统级的组合设置菜单条");
			}
    	return flag ;
    }
}
