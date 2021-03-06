package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DaoXMLConfigureBean
    extends BaseDataSettingBean implements IDataSetting {
    private String cuscfgcode = "";
    private String oldcuscfgcode = "";
    private String cuscfgname = "";
    private String readtype = "";
    private String fileconlissepsign = "";
    private String lissep = "";
    private String desc = "";
    public DaoXMLConfigureBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        String sql = null;
        boolean isCommit = false;
        try {
            dbl.loadConnection().setAutoCommit(false);
            sql = "Insert Into " + pub.yssGetTableName("Tb_Dao_XMLCusConfig") +
                " values('" + cuscfgcode + "','" + cuscfgname + "','" + readtype + "','" + fileconlissepsign +
                "','" + lissep + "','" + (desc.trim().length() > 0 ? desc : " ") +
                "' , " + (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ",' ')";
            dbl.executeSql(sql);
            dbl.loadConnection().commit();
            isCommit = true;
        } catch (SQLException sqle) {
            throw new YssException("保存XML文件接口配置出错！");
        } catch (Exception e) {
            throw new YssException("保存XML文件接口配置出错！");
        } finally {
            try {
                if (!isCommit) {
                    dbl.loadConnection().rollback();
                }
            } catch (Exception e) {}
            try {
                dbl.loadConnection().setAutoCommit(true);
            } catch (Exception e) {throw new YssException();}
        }
        return "";
    }

    public String editSetting() throws YssException {
        String sql = null;
        boolean isCommit = false;
        try {
            dbl.loadConnection().setAutoCommit(false);
            sql = "Update " + pub.yssGetTableName("Tb_Dao_XMLCusConfig") + " set fcuscfgcode='" + cuscfgcode + "',fcuscfgname='" + cuscfgname + "',freadtype='" + readtype + "',ffileconlissepsign='" + fileconlissepsign + "',flissep='" + lissep + "',fdesc='" + (desc.length() > 0 ? desc : " ") + 
			"',fcreator='" + creatorCode + "',fcreatetime='" + creatorTime + "'  where fcuscfgcode='" + oldcuscfgcode + "'";
            dbl.executeSql(sql);
            dbl.loadConnection().commit();
            isCommit = true;
        } catch (SQLException sqle) {
            throw new YssException("修改XML接口配置出错！");
        } catch (Exception e) {
            throw new YssException("修改XML接口配置出错！");
        } finally {
            try {
                if (!isCommit) {
                    dbl.loadConnection().rollback();
                }
            } catch (Exception e) {}
            try {
                dbl.loadConnection().setAutoCommit(true);
            } catch (Exception e) {throw new YssException();}
        }
        return "";
    }

    public void delSetting() throws YssException {
        String sql = null;
        boolean isCommit = false;
        try {
            dbl.loadConnection().setAutoCommit(false);
            sql = "Delete from " + pub.yssGetTableName("Tb_Dao_XMLCusConfig") + " where fcuscfgcode='" + cuscfgcode + "'"; ;
            dbl.executeSql(sql);
            dbl.loadConnection().commit();
            isCommit = true;
        } catch (SQLException sqle) {
            throw new YssException("删除XML接口配置出错！");
        } catch (Exception e) {
            throw new YssException("删除XML接口配置出错！");
        } finally {
            try {
                if (!isCommit) {
                    dbl.loadConnection().rollback();
                }
            } catch (Exception e) {}
            try {
                dbl.loadConnection().setAutoCommit(true);
            } catch (Exception e) {throw new YssException();}
        }
    }

    public void checkSetting() throws YssException {
        String sql = null;
        boolean isCommit = false;
        try {
            dbl.loadConnection().setAutoCommit(false);
            sql = "Update " + pub.yssGetTableName("Tb_Dao_XMLCusConfig") + " set  FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where fcuscfgcode='" + cuscfgcode + "'";
            dbl.executeSql(sql);
            dbl.loadConnection().commit();
            isCommit = true;
        } catch (SQLException sqle) {
            throw new YssException("审核XML接口配置出错！");
        } catch (Exception e) {
            throw new YssException("审核XML接口配置出错！");
        } finally {
            try {
                if (!isCommit) {
                    dbl.loadConnection().rollback();
                }
            } catch (Exception e) {}
            try {
                dbl.loadConnection().setAutoCommit(true);
            } catch (Exception e) {throw new YssException();}
        }

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

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getListViewData1() throws YssException {
        StringBuffer buffer = new StringBuffer();
        String sql = null;
        ResultSet rs = null;
        try {
            sql = "Select * from " + pub.yssGetTableName("Tb_Dao_XMLCusConfig");
            buffer.append(builderListViewData(sql));
        } catch (Exception e) {
            throw new YssException("获取XML接口配置信息出错！");
        }
        return buffer.toString();
    }

    public String getListViewData2() throws YssException {
        StringBuffer buffer = new StringBuffer();
        String sql = null;
        ResultSet rs = null;
        try {
            buffer.append("");
            sql = "Select a.* from " + pub.yssGetTableName("tb_dao_xmlcusconfig") + " a inner join " + pub.yssGetTableName("Tb_Dao_CusConfig") + " b on a.fcuscfgcode=b.ffilecuscfg  where b.fcuscfgcode='" + cuscfgcode + "'";
            rs = dbl.openResultSet(sql);
            if (rs.next()) {
                buffer.append(rs.getString("freadtype")).append("﹀").append(rs.getString("ffileconlissepsign")).append("﹀").append(rs.getString("flissep"));
            }
        } catch (SQLException sqle) {
            throw new YssException("获取XML文件自定义接口配置出错！");
        } catch (Exception e) {
            throw new YssException("获取XML文件自定义接口配置出错！");
        }
        return buffer.toString();
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] sArray = sRowStr.split("\t", -1);
        parseRecLog();
        if (sArray.length == 1) {
            cuscfgcode = sArray[0];
        }
        if (sArray.length == 2) {
            cuscfgcode = sArray[0];
            checkStateId = Integer.parseInt(sArray[1]);
        }
        if (sArray.length >= 6) {
            cuscfgcode = sArray[0];
            cuscfgname = sArray[1];
            readtype = sArray[2];
            fileconlissepsign = sArray[3];
            lissep = sArray[4];
            desc = sArray[5];
        }
        if (sArray.length == 7) {
            oldcuscfgcode = sArray[6];
        }

    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                bufAll.append(super.buildRowShowStr(rs, this.getListView1ShowCols() + "\tfcheckstate")).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取接口信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
