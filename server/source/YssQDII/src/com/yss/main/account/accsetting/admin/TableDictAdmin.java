package com.yss.main.account.accsetting.admin;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.account.accsetting.pojo.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: 系统表字典</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author CTQ
 * @version 1.0
 */
public class TableDictAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private ItemInfoBean m_Info;
    private TableDictBean m_Data;
    private TableDictBean m_Filter;

    public TableDictAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
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

    public String getListViewData1() throws YssException {
        return "";
    }

    /**
     * 将请求数据给全局变量赋值
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            if (this.m_Filter == null) {
                this.m_Filter = new TableDictBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new TableDictBean();
                this.m_Data.setYssPub(pub);
            }

            reqAry = sRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            for (int i = 0; i < reqAry.length; i++) {
                if (reqAry[i].startsWith("filter:")) {
                    this.m_Filter.parseRowStr(reqAry[i].substring(7));
                } else if (reqAry[i].startsWith("data:")) {
                    this.m_Data.parseRowStr(reqAry[i].substring(5));
                }
            }
        } catch (Exception e) {
            throw new YssException("解析系统表字典信息出错", e);
        }
    }

    /**
     * 获取所有数据表信息
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (m_Info == null) {
                m_Info = new ItemInfoBean();
                m_Info.setYssPub(pub);
            }

            sHeader = "表名\t描述";
            strSql = "select distinct ftablecode as fcode,ftablename as fname,ftabledesc as fdesc from tb_fun_tabledict order by ftablecode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);
                m_Info.setValues(rs);
                bufAll.append(m_Info.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取系统表字典出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据数据表名获取所有字段信息
     * @return String
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (m_Info == null) {
                m_Info = new ItemInfoBean();
                m_Info.setYssPub(pub);
            }

            sHeader = "字段\t描述";
            strSql = "select ffieldcode,ffieldname,ffielddesc,ffieldtype from tb_fun_tabledict " +
                this.buildFilterSql() + " order by ffieldcode";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("ffieldcode")).trim()).append(
                    "\t");
                bufShow.append( (rs.getString("ffieldname")).trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                m_Info.setCode(rs.getString("ffieldcode"));
                m_Info.setName(rs.getString("ffieldname"));
                m_Info.setDesc(rs.getString("ffielddesc"));
                m_Info.setDesc(rs.getString("ffieldtype"));

                bufAll.append(m_Info.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取系统表字典出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData4() throws YssException {
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    /**
     * 生成过滤条件子句
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.m_Filter != null) {
            sResult = " where 1=1";

            if (this.m_Filter.getTableCode().length() != 0) {
                sResult = sResult + " and ftablecode = '" +
                    this.m_Filter.getTableCode().replaceAll("'", "''") + "'";
            }
        }
        return sResult;
    }
}
