package com.yss.main.syssetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * 
 * modify by wangzuochun 2010.04.14 MS01081    系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能
 * 执行IDataSetting 接口
 */
public class RightTypeBean
    extends BaseDataSettingBean implements IDataSetting {
    private String rightTypeCode = "";
    private String rightTypeName = "";
    private String moduleName = "";
    private String rightTypeStyle = "";
    private String oldRightTypeCode = "";
    private String MenubarCode = ""; // by caocheng MS00001 QDV4.1
    private RightTypeBean filterType;

    public RightTypeBean() {
    }

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
            reqAry = sTmpStr.split("\t");
            this.rightTypeCode = reqAry[0];
            this.rightTypeName = reqAry[1];
            this.moduleName = reqAry[2];
            this.rightTypeStyle = reqAry[3];
            this.oldRightTypeCode = reqAry[4];
            this.MenubarCode = reqAry[5]; // by caocheng 2009.03.29
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RightTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析权限类型设置请求出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.rightTypeCode).append("\t");
        buf.append(this.rightTypeName).append("\t");
        buf.append(this.moduleName).append("\t");
        buf.append(this.rightTypeStyle).append("\t");
        buf.append(this.MenubarCode).append("\t"); // by caocheng 2009.03.29 MS0001 QDV4.1
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getListViewData() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "权限类型代码\t权限类型名称\t模块名称\t权限类型\t菜单条代码"; //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
            strSql = "select * from Tb_Sys_RightType " + buildFilterSql() + " order by FRightTypeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FRightTypeCode")).append("\t");
                bufShow.append(rs.getString("FRightTypeName")).append("\t");
                bufShow.append(rs.getString("FFunModuleName")).append("\t");
                //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A              
                bufShow.append(getFType(rs)).append("\t");//by guyichuan 2011.07.27 BUG2220权限类型设置界面存在问题         
                bufShow.append(rs.getString("FMenuBarCode")).append(YssCons.YSS_LINESPLITMARK);
                //================================================================================

                this.rightTypeCode = rs.getString("FRightTypeCode");
                this.rightTypeName = rs.getString("FRightTypeName");
                this.moduleName = rs.getString("FFunModuleName");
                this.rightTypeStyle = rs.getString("FType");
                this.MenubarCode = rs.getString("FMenuBarCode"); //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_RIGHT_TYPE);

            //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\fFRightTypeCode\tFRightTypeName\tFFunModuleName\tFType\tFMenuBarCode" +
                "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取权限类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the cursor finally modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
        }

    }
    //by guyichuan 2011.07.27 BUG2220 界面中“权限类型”应该显示名称
    public String getFType(ResultSet rs) throws  YssException{
    	if(rs==null)return "";
    	String sResult=null;
    	try {
    		sResult=rs.getString("FType");
			if("fund".equalsIgnoreCase(sResult)){
				sResult="业务";
			}else if("system".equalsIgnoreCase(sResult)){
				sResult="系统";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new YssException("获取权限类型出错！");
		}
    	return sResult;
    }

    public void delRightType() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;

        try {
            strSql = "delete from Tb_Sys_RightType where FRightTypeCode='" + dbl.sqlString(this.rightTypeCode) + "'";
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
        } catch (Exception ex) {
        }
    }

    public String buildFilterSql() {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1 = 1";
            if (this.filterType.rightTypeCode.length() != 0) {
                sResult = sResult + " and FRightTypeCode like '%" +
                    filterType.rightTypeCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.rightTypeName.length() != 0) {
                sResult = sResult + " and FRightTypeName like '%" +
                    filterType.rightTypeName.replaceAll("'", "''") + "%'";
            }
            //by guyichuan 2011.07.28 BUG2220 支持＂所有＂的情况
            if (this.filterType.rightTypeStyle.length() != 0 && !filterType.rightTypeStyle.equalsIgnoreCase("所有")) {
                sResult = sResult + " and FType like '%" +
                    ( (filterType.rightTypeStyle).equalsIgnoreCase("系统") ? "system" : "fund").replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    public void checkInput(byte btOper) throws YssException {
        String strSql = "", strTmp = "";
        if (btOper == YssCons.OP_ADD) {
            strSql = "select FRIGHTTYPECODE from Tb_Sys_RightType where FRightTypeCode=" +
                dbl.sqlString(this.rightTypeCode.trim()) + "";
            strTmp = dbFun.GetValuebySql(strSql);
            if (strTmp.length() > 0) {
                throw new YssException("权限类型代码【" + this.rightTypeCode.trim() +
                                       "】已经被权限类型【" + strTmp + "】所占用，请重新输入");
            }
        } else if (btOper == YssCons.OP_EDIT) {
            if (!this.rightTypeCode.trim().equalsIgnoreCase(this.oldRightTypeCode)) {
                strSql = "select FRightTypeName from Tb_Sys_RightType where FRightTypeCode=" +
                    dbl.sqlString(this.rightTypeCode.trim()) + "";
                strTmp = dbFun.GetValuebySql(strSql);

                strSql = "update Tb_Sys_RightType set FRIGHTTYPECODE=" +
                    dbl.sqlString(this.rightTypeCode) + ", FRIGHTTYPENAME=" +
                    dbl.sqlString(this.rightTypeName) + ", FFUNMODULENAME=" +
                    dbl.sqlString(this.moduleName) + ", FTYPE=" +
                    dbl.sqlString(this.rightTypeStyle) + ",FMenuBarCode=" +
                    dbl.sqlString(this.MenubarCode) + //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
                    " where FRIGHTTYPECODE=" +
                    dbl.sqlString(this.oldRightTypeCode);
                try {
                    dbl.executeSql(strSql);
                } catch (Exception e) {
                    throw new YssException("修改权限类型出错！", e);
                }
            }
        }
    }

    public void saveSetting(byte btOper) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (btOper == YssCons.OP_ADD) {
                strSql = "insert into Tb_Sys_RightType(FRIGHTTYPECODE,FRIGHTTYPENAME,FFUNMODULENAME,FTYPE,FMenuBarCode)" + //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
                    " values(" + dbl.sqlString(this.rightTypeCode) + "," +
                    dbl.sqlString(this.rightTypeName) + "," +
                    dbl.sqlString(this.moduleName) + ",'" +
                    (this.rightTypeStyle.equalsIgnoreCase("系统") ? "system" : "fund") + "'," + dbl.sqlString(this.MenubarCode) + ")"; //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
            } else if (btOper == YssCons.OP_EDIT) {
                strSql = "update Tb_Sys_RightType set FRIGHTTYPECODE=" +
                    dbl.sqlString(this.rightTypeCode) + ",FRIGHTTYPENAME=" +
                    dbl.sqlString(this.rightTypeName) + ",FFUNMODULENAME=" +
                    dbl.sqlString(this.moduleName) + ",FTYPE='" +
                    (this.rightTypeStyle.equalsIgnoreCase("系统") ? "system" : "fund") +
                    "',FMenuBarCode=" + dbl.sqlString(this.MenubarCode) + " where FRIGHTTYPECODE=" + //by caocheng MS00001 QDV4.1 QDV4.1赢时胜上海2009年2月1日01_A
                    dbl.sqlString(this.oldRightTypeCode);
            } else if (btOper == YssCons.OP_DEL) {
                strSql = "delete from Tb_Sys_RightType " +
                    "where FRIGHTTYPECODE=" + dbl.sqlString(this.rightTypeCode);
            }

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存权限类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

	public String addSetting() throws YssException {
		return null;
	}

	public void checkSetting() throws YssException {
		
	}

	public void delSetting() throws YssException {
		
	}

	public void deleteRecycleData() throws YssException {
		
	}

	public String editSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String saveMutliSetting(String mutilRowStr) throws YssException {
		return null;
	}

	public String getBeforeEditData() throws YssException {
		return null;
	}

	public String getOperValue(String type) throws YssException {
		return null;
	}
	
	/**
	 * add by wangzuochun 2010.04.14 MS01081    系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能  -->
	 */
	public String getListViewData1() throws YssException {
		return getListViewData();
	}

	public String getListViewData2() throws YssException {
		return null;
	}

	public String getListViewData3() throws YssException {
		return null;
	}

	public String getListViewData4() throws YssException {
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		return null;
	}

	public String getTreeViewData1() throws YssException {
		return null;
	}

	public String getTreeViewData2() throws YssException {
		return null;
	}

	public String getTreeViewData3() throws YssException {
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		return null;
	}
}
