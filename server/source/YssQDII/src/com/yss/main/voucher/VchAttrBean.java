package com.yss.main.voucher;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.HashMap;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

///
///凭证属性设置////
///
public class VchAttrBean
    extends BaseDataSettingBean implements IDataSetting {
    private String attrCode = ""; //属性代码;
    private String attrName = ""; //属性名称
    private int handCheck = 0; //手工
    private String desc = "";
    private String oldAttrCode = "";
    private String vchInd;
    private VchAttrBean filterType = null;
    private String sRecycled = ""; //增加回收站的字段 by leeyu 2008-10-21 BUG:0000291
    private String sFormat = "0000000000"; // 357 QDV4赢时胜（深圳）2010年11月29日03_A by qiuxufeng 排序字段格式化字符串
    public String getAllSetting() throws YssException {
        return "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.checkStateId).append("\t");
        buf.append(this.vchInd).append("\t");
        buf.append(this.handCheck).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\f") >= 0) {
                sTmpStr = sRowStr.split("\r\f")[0];
            } else {
                sTmpStr = sRowStr;
                reqAry = sTmpStr.split("\t");
            }
            sRecycled = sTmpStr; //回收站处理 by leeyu 2008-10-21
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            if(reqAry == null)
            	return ;
            //---end---
            this.attrCode = reqAry[0];
            if (reqAry[0].length() == 0) {
                this.attrCode = " ";
            }
            this.attrName = reqAry[1];
            if (reqAry[1].length() == 0) {
                this.attrName = " ";
            }
			// ------ modify by nimengjing 2010.12.16 BUG #667 凭证属性界面问题 
			if (reqAry[2] != null) {
				if (reqAry[2].indexOf("【Enter】") >= 0) {
					this.desc = reqAry[2].replaceAll("【Enter】", "\r\n");
				} else {
					this.desc = reqAry[2];
				}
			}
			// ----------------- BUG #667----------------//
            this.oldAttrCode = reqAry[3];
            this.vchInd = reqAry[4];
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.handCheck = YssFun.toInt(reqAry[6]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchAttrBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析凭证属性设置出错!");
        }

    }

    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String strSql = "";
        try {
            conn = dbl.loadConnection();
            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_Attr") +
                " (FAttrCode,FAttrName,FDesc,FCheckState,FVchInd,FHandCheck," +
                "FCreator,FCreateTime) values(" +
                dbl.sqlString(this.attrCode) + "," +
                dbl.sqlString(this.attrName) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.vchInd) + "," +
                this.handCheck + "," +
                 dbl.sqlString(this.creatorCode) + "," +//modify by nimengjing 2010.12.16 BUG #667 凭证属性界面问题 
                "'" + YssFun.formatDatetime(new java.util.Date()) + "')";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增凭证属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Vch_Attr") +
                " set FAttrCode=" + dbl.sqlString(this.attrCode) + "," +
                " FAttrName=" + dbl.sqlString(this.attrName) + "," +
                " FVchInd=" + dbl.sqlString(this.vchInd) + "," +
                " FDesc=" + dbl.sqlString(this.desc) + "," +
                " FHandCheck = " + this.handCheck +
                " where FAttrCode=" + dbl.sqlString(this.oldAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改凭证属性设置出错", e);
        }
        return "";
    }

    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "update " + pub.yssGetTableName("Tb_Vch_Attr") +
                " set FCheckState=" + this.checkStateId + "," +
                " FCreator=" + dbl.sqlString(this.checkUserCode + " ") + "," +
                " FCreateTime=" + dbl.sqlString(this.checkTime + " ") +
                " where FAttrCode=" + dbl.sqlString(this.oldAttrCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除凭证属性设置出错", e);
        }
    }

    public void checkSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
            int iSort = Integer.parseInt(getMaxSort()) + 1;
            String tempSort = "";
            // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
            //====200-10-21 by leeyu BUG:0000491 回收站功能处理
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                tempSort = new DecimalFormat("0000000000").format(iSort);
                strSql = "update " + pub.yssGetTableName("Tb_Vch_Attr") +
                    " set FCheckState = " + this.checkStateId + "," +
                    // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
                    (this.checkStateId == 0 ? " FSort = ' '," : (" FSort = " + dbl.sqlString(tempSort) + ",")) +
                    // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
                    " FCheckUser = " + dbl.sqlString(pub.getUserCode()) + "," +
                    " FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "'" +
                    " where FAttrCode=" + dbl.sqlString(this.attrCode);
                dbl.executeSql(strSql);
                iSort++;// add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
            }
            //conn.setAutoCommit(false);
            bTrans = true;
            //dbl.executeSql(strSql);
            //=====2008-10-21
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, false);
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    private String builerFilter() {
        String reSql = "";
        if (this.filterType != null) {
            reSql = " where 1=1";
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.filterType.attrCode != null &&
            		this.filterType.attrCode.trim().length() > 0) {
                reSql += " and a.FAttrCode like '" +
                    this.filterType.attrCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.attrName != null &&
            		this.filterType.attrName.trim().length() > 0) {
                reSql += " and a.FAttrName like '" +
                    this.filterType.attrName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc != null && this.filterType.desc.trim().length() > 0) {
                reSql += " and a.FDesc like '" +
                    this.filterType.desc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.vchInd != null &&
            		this.filterType.vchInd.trim().length() > 0) {
                reSql += " and a.FVchInd like '" +
                    this.filterType.vchInd.replaceAll("'", "''") + "%'";
            }
			/**end*/
            if (this.filterType.checkStateId == 1) {
                reSql = reSql + " and a.FCheckState = 1 ";
            }

        }
        return reSql;
    }

    private void setVchAttr(ResultSet rs) throws SQLException {
        this.attrCode = rs.getString("FAttrCode");
        this.attrName = rs.getString("FAttrName");
        this.desc = rs.getString("FDesc");
        this.vchInd = rs.getString("FVchInd");
        this.handCheck = rs.getInt("FHandCheck");
        super.setRecLog(rs);
    }

    public String getListViewData1() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	checkSortField(); // add by qiuxufeng 20110214 357 QDV4赢时胜（深圳）2010年11月29日03_A
            sHeader = getListView1Headers();
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                builerFilter() +
                //" order by a.FCheckState, a.FCreateTime desc, a.FCheckTime desc"; // wdy modify 20070830
            	" order by a.FCheckState, a.FSort," + // 增加先通过手动排序字段排序 edit by qiuxufeng 20110214 357 QDV4赢时胜（深圳）2010年11月29日03_A
            	" a.FCreateTime desc, a.FCheckTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchAttr(rs);
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
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String getListViewData2() throws YssException {
        Connection conn = null;
        String sqlStr = "", sHeader = "", sShowDataStr = "", sAllDataStr = "";
        StringBuffer sData = new StringBuffer();
        StringBuffer sAllData = new StringBuffer();
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sHeader = "属性代码\t属性名称\t描述";
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " where a.FCheckState = 1 order by 1"; // wdy modify 20070830
            conn.setAutoCommit(false);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sData.append(rs.getString("FAttrCode")).append("\t");
                sData.append(rs.getString("FAttrName")).append("\t");
                sData.append(rs.getString("FDesc")).append(YssCons.
                    YSS_LINESPLITMARK);
                this.setVchAttr(rs);
                sAllData.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (sData.toString().length() > 2) {
                sShowDataStr = sData.toString().substring(0,
                    sData.toString().length() - 2);
            }

            if (sAllData.toString().length() > 2) {
                sAllDataStr = sAllData.toString().substring(0,
                    sAllData.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取属性出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getTreeViewData1() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String getTreeViewData2() {
        return "";
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("Tb_Vch_Attr"),
                               "FAttrCode",
                               this.attrCode,
                               this.oldAttrCode);
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getOperValue(String sType) {
    	if(null != sType && sType.equalsIgnoreCase("itemmove")) {
    		try {
				return saveItemSort();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
        return "";
    }

    public String getBeforeEditData() {
        return "";
    }

    public VchAttrBean() {
    }

    public void setVchInd(String vchInd) {
        this.vchInd = vchInd;
    }

    public void setOldAttrCode(String oldAttrCode) {
        this.oldAttrCode = oldAttrCode;
    }

    public void setFilterType(VchAttrBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setHandCheck(int handCheck) {
        this.handCheck = handCheck;
    }

    public String getVchInd() {
        return vchInd;
    }

    public String getOldAttrCode() {
        return oldAttrCode;
    }

    public VchAttrBean getFilterType() {
        return filterType;
    }

    public String getDesc() {
        return desc;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public int getHandCheck() {
        return handCheck;
    }

    /**
     * deleteRecycleData 完善回收站的功能 BUG:0000491
     */
    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            //====200-10-21 by leeyu BUG:0000491 回收站功能处理
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_Attr") +
                    " where FAttrCode=" + dbl.sqlString(this.attrCode);
                dbl.executeSql(strSql);
            }
            bTrans = true;
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除凭证属性设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
    
    /**
     * 357 QDV4赢时胜（深圳）2010年11月29日03_A add by qiuxufeng 20110214
     * 检查凭证属性已审核记录中的手动排序字段是否被设置排序
     * @方法名：checkSortField
     * @返回类型：void
     */
    private void checkSortField() throws YssException {
    	ResultSet rs = null;
    	PreparedStatement pst = null;
    	String strSql = "";
    	int iSort = 1;
    	try {
    		strSql = "update " +
    				pub.yssGetTableName("Tb_Vch_Attr") +
    				" set FSort = ? where FAttrCode = ?";
    		pst = dbl.openPreparedStatement(strSql);
    		iSort = Integer.parseInt(getMaxSort()) + 1;
    		// 查找已审核记录中排序字段未设置的记录
			strSql = "select FAttrCode, FSort from " +
					pub.yssGetTableName("Tb_Vch_Attr") +
					" where FCheckState = 1 and FSort = ' '";// +
					//" order by FCreateTime desc, FCheckTime desc";
			rs = dbl.openResultSet_antReadonly(strSql);
			if(!rs.next()) {
				return;
			} else {
				rs.beforeFirst();
				String tempSort = sFormat;
				// 设置排序未设置值的记录
				while(rs.next()) {
					tempSort = new DecimalFormat(sFormat).format(iSort);
					pst.setString(1, tempSort);
					pst.setString(2, rs.getString("FAttrCode"));
					pst.execute();
					iSort++;
				}
			}
		} catch (Exception e) {
			throw new YssException("检查凭证属性排序出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
    }
    
    /**
     * 357 QDV4赢时胜（深圳）2010年11月29日03_A add by qiuxufeng 20110214
     * 获取凭证属性已审核排序的最大值
     * @方法名：getMaxSort
     * @返回类型：String
     */
    private String getMaxSort() throws YssException {
    	String strSql = "";
    	ResultSet rs = null;
    	String strSort = sFormat;
    	try {
			// 查询凭证属性已审核手动排序的最大值
			strSql = "select * from " +
					pub.yssGetTableName("Tb_Vch_Attr") +
					" where FCheckState = 1 and FSort <> ' '" +
					" order by FSort Desc";
			rs = dbl.openResultSet(strSql);
			if(rs.next()) {
				strSort = rs.getString("FSort");
			}
			dbl.closeResultSetFinal(rs);
		} catch (Exception e) {
			throw new YssException("查询凭证属性排序最大值出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return strSort;
    }
    
    /**
     * 357 QDV4赢时胜（深圳）2010年11月29日03_A add by qiuxufeng 20110214
     * 保存移动后的排序
     * @方法名：saveItemSort
     * @返回类型：String
     */
    private String saveItemSort() throws YssException {
    	String reStr = "";
    	String strSql = "";
    	String[] attrCodeAry = null;
    	String[] attrSortAry = null;
    	ResultSet rs = null;
    	PreparedStatement pst = null;
    	Connection conn = null;
        boolean bTrans = false;
    	
    	try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
			attrCodeAry = new String[2];
			attrSortAry = new String[2];
			strSql = "select FAttrCode, FSort from " +
					pub.yssGetTableName("Tb_Vch_Attr") +
					" where FAttrCode in (" + operSql.sqlCodes(this.attrCode) + ")";
			rs = dbl.openResultSet(strSql);
			for(int i = 0; i < 2; i++) {
				rs.next();
				attrCodeAry[i] = rs.getString("FAttrCode");
				attrSortAry[i] = rs.getString("FSort");
			}
			dbl.closeResultSetFinal(rs);
			// 交换排序值
			String temp = attrSortAry[0];
			attrSortAry[0] = attrSortAry[1];
			attrSortAry[1] = temp;
			strSql = "update " +
					pub.yssGetTableName("Tb_Vch_Attr") +
					" set FSort = ? where FAttrCode = ?";
			pst = dbl.openPreparedStatement(strSql);
			// 更新排序
			for(int i = 0; i < 2; i++) {
				pst.setString(1, attrSortAry[i]);
				pst.setString(2, attrCodeAry[i]);
				pst.addBatch();
			}
			pst.executeBatch();
            bTrans = true;
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            reStr = "success";
		} catch (Exception e) {
			reStr = "error";
			throw new YssException("保存凭证属性手动排序出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
		return reStr;
    }
}
