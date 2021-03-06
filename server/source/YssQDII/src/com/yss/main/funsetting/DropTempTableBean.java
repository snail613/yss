package com.yss.main.funsetting;

import java.sql.*;
import java.util.regex.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title:DropTempTableBean </p>
 *
 * <p>Description:删除更新表结构时产生的临时表 by xuqiji 20090526 QDV4海富通2009年05月13日01_AB  </p>
 * MS00447    删除更新表结构时产生的临时表
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DropTempTableBean
    extends BaseDataSettingBean implements IDataSetting {
    private String strTabName = ""; //临时表名
    private String strTabSpaceName = ""; //临时表空间
    private String strDeleteData = ""; //清除数据
    private String strAssetGroupCode = ""; //组合群代码
    private String strAssetGroupName = ""; //组合群名称
    public DropTempTableBean() {
    }

    /**
     * parseRowStr
     * 解析前台传来的删除更新表结构时产生的临时表的数据
     * 修改日期：2009 05 26
     * 徐启吉
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String array[] = null;
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
            strDeleteData = sRowStr;
            array = sTmpStr.split("\t");
            this.strTabName = array[0];
            this.strTabSpaceName = array[1];
            if (array.length == 2) {
                this.strAssetGroupCode = array[0];
                this.strAssetGroupName = array[1];
            } else {
                this.strAssetGroupCode = array[2];
                this.strAssetGroupName = array[3];
            }
            super.parseRecLog();
        } catch (Exception e) {
            throw new YssException("解析前台传来的删除更新表结构时产生的临时表的数据出错！\r\t", e);
        }
    }

    /**
     * buildRowStr
     * 获取数据字符串 by xuqiji 2009 05 26
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strTabName).append("\t");
        buf.append(this.strTabSpaceName).append("\t");
        buf.append(this.strAssetGroupCode).append("\t");
        buf.append(this.strAssetGroupName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 获取删除临时表信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        StringBuffer buff = new StringBuffer();
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        Pattern pattern = null;
        Matcher matcher = null;
        try {
            sHeader = "临时表名称\t临时表空间";

            //如果不是按照组合群查询，查询所有tb_[0-9]* 格式的表
            if (this.strAssetGroupCode.equals("")) {
                buff.append(" select TABLE_NAME, TABLESPACE_NAME  from ");
                buff.append(" user_all_tables where Table_Name not LIKE 'TB/_%/_%' ESCAPE '/' ");
                buff.append(" and Table_Name like 'TB/_%'ESCAPE '/' ");
                rs = dbl.openResultSet(buff.toString());
                while (rs.next()) {
                    bufShow.append(rs.getString("TABLE_NAME")).append("\t")
                        .append(rs.getString("TABLESPACE_NAME")).append(YssCons.YSS_LINESPLITMARK);
                    this.strTabName = rs.getString("TABLE_NAME");
                    this.strTabSpaceName = rs.getString("TABLESPACE_NAME");
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
                dbl.closeResultSetFinal(rs);
                buff.delete(0, buff.length());
            }

            buff.append(" select table_name,tablespace_name from ");
            buff.append(" user_all_tables where Table_Name like 'TB/_%/_%' ESCAPE '/' ");
            rs = dbl.openResultSet(buff.toString());
            pattern = Pattern.compile("^[A-Z]{2}_" + (this.strAssetGroupCode.equals("") ? "\\w*" : this.strAssetGroupCode + "_") + ".*[0-9]{6,}$"); //正则表达式匹配类型如：TB_002_DATA_DA2132132132或者TB_002_DATA_2132132132
            while (rs.next()) {
                matcher = pattern.matcher(rs.getString("TABLE_NAME")); //此处和查询出的数据进行匹配
                if (matcher.find()) { //进行判断，匹配成功进入该语句
                    this.strTabName = rs.getString("TABLE_NAME");
                    this.strTabSpaceName = rs.getString("TABLESPACE_NAME");
                    bufShow.append(this.strTabName).append("\t")
                        .append(this.strTabSpaceName).append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.delete(bufShow.length() - 2, bufShow.length()).toString();
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.delete(bufAll.length() - 2, bufAll.length()).toString();
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取删除临时表信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 清除功能 by xuqiji 2009 04 03
     * @throws YssException
     */
    public void deleteData() throws YssException {
        String strSql = null;
        String[] array = null;
        boolean bTrans = true; //代表是否开始事务
        Statement st = null;
        Connection conn = dbl.loadConnection();
        try {
            if (null != strDeleteData && !"".equalsIgnoreCase(strDeleteData.trim())) { //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
                array = strDeleteData.split("\r\t"); //根据规定的符号，把多个sql语句分别放入数组
                conn.setAutoCommit(false);
                st = conn.createStatement();
                for (int i = 0; i < array.length; i++) { //循环执行这些删除语句
                    if (array[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(array[i]);
                    /**shashijie 2011-10-21 STORY 1698 */
                    strSql = dbl.doOperSqlDrop("drop table " + this.strTabName + " cascade constraints ");
                    /**end*/
                    st.addBatch(strSql);
                }
                st.executeBatch();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错！\r\t", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(st);
        }
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getStrTabName() {
        return strTabName;
    }

    public String getStrTabSpaceName() {
        return strTabSpaceName;
    }

    public String getStrDeleteData() {
        return strDeleteData;
    }

    public String getStrAssetGroupCode() {
        return strAssetGroupCode;
    }

    public String getStrAssetGroupName() {
        return strAssetGroupName;
    }

    public void setStrTabName(String strTabName) {
        this.strTabName = strTabName;
    }

    public void setStrTabSpaceName(String strTabSpaceName) {
        this.strTabSpaceName = strTabSpaceName;
    }

    public void setStrDeleteData(String strDeleteData) {
        this.strDeleteData = strDeleteData;
    }

    public void setStrAssetGroupName(String strAssetGroupName) {
        this.strAssetGroupName = strAssetGroupName;
    }

    public void setStrAssetGroupCode(String strAssetGroupCode) {
        this.strAssetGroupCode = strAssetGroupCode;
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

    /**
     * 重写基类方法，做清除数据功能
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        deleteData();
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

    public String getListViewData2() throws YssException {
        return "";
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
