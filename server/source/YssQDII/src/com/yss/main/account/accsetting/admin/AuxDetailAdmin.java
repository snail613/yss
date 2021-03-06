package com.yss.main.account.accsetting.admin;

import java.sql.*;
import java.util.regex.*;

import com.yss.dsub.*;
import com.yss.main.account.accsetting.pojo.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: 核算项目明细内容</p>
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
public class AuxDetailAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private AuxDetailBean m_Data;
    private AuxDetailBean m_OldData;
    private AuxDetailBean m_Filter;

    private String m_Request;
    public AuxDetailAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {

    }

    /**
     * 新增设置信息
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;

        try {
            //因为没有FCheckStat字段，不能使用dbFun.checkInputCommon()方法。所以在此检查记录是否已存在
            strSql = "select * from  Tb_Base_ExtraAuxiliary " +
                " where FAuxCode = " + dbl.sqlString(m_Data.getAuxCode()) +
                " and FCode=" + dbl.sqlString(m_Data.getItemCode());

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                throw new YssException("【" + m_Data.getItemCode() + "】信息已经存在，请重新输入");
            }

            //新增记录
            strSql = "insert into Tb_Base_ExtraAuxiliary" +
                "(FAuxCode,FCode,FName,FDesc) " +
                "values(" +
                dbl.sqlString(m_Data.getAuxCode()) + "," +
                dbl.sqlString(m_Data.getItemCode()) + "," +
                dbl.sqlString(m_Data.getItemName()) + "," +
                dbl.sqlString(m_Data.getItemDesc()) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增核算项目明细项内容出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);

        }
        return m_Data.buildRowStr();
    }

    /**
     * 编辑设置信息
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;

        try {
            //因为没有FCheckStat字段，不能使用dbFun.checkInputCommon()方法。所以在此检查记录是否已存在
            strSql = "select * from  Tb_Base_ExtraAuxiliary " +
                " where FAuxCode = " + dbl.sqlString(m_Data.getAuxCode()) +
                " and FCode=" + dbl.sqlString(m_Data.getItemCode());

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                throw new YssException("【" + m_Data.getItemCode() + "】信息已经存在，请重新输入");
            }

            //更新记录
            strSql = "update Tb_Base_ExtraAuxiliary" +
                " set FAuxCode = " + dbl.sqlString(m_Data.getAuxCode()) +
                ",FCode = " + dbl.sqlString(m_Data.getItemCode()) +
                ",FName = " + dbl.sqlString(m_Data.getItemName()) +
                ",FDesc = " + dbl.sqlString(m_Data.getItemDesc()) +
                " where FAuxCode = " + dbl.sqlString(m_OldData.getAuxCode()) +
                " and FCode=" + dbl.sqlString(m_OldData.getItemCode());

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改核算项目明细项内容出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return m_Data.buildRowStr();
    }

    /**
     * 删除设置信息
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //删除记录
            strSql = "delete from Tb_Base_ExtraAuxiliary" +
                " where FAuxCode = " + dbl.sqlString(m_Data.getAuxCode()) +
                " and FCode=" + dbl.sqlString(m_Data.getItemCode());

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除核算项目明细项内容出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String getListViewData2() throws YssException {
        return "";
    }

    /**
     * 根据筛选条件设置生成查询条件子句
     * @param filter String  筛选条件
     * @param table String   数据表
     * @return String  查询条件子句
     * @throws YssException
     */
    public String buildFilterSql(String filter, String table) throws YssException {
        String result = ""; //返回的查询条件子句

        String field = ""; //字段
        String comp = ""; //比较运算符
        String value = ""; //查询内容
        String type = ""; //字段类型
        Pattern p;
        Matcher m;

        String sql = ""; //SQL语句
        ResultSet rs = null;

        try {
            if (filter == null) {
                return result;
            }
            if (filter.trim().length() == 0) {
                return result;
            }

            //分隔筛选条件得到条件表达式数组
            String[] arrData = filter.split(" and | or ");

            //循环替换条件表达式
            for (int i = 0; i < arrData.length; i++) {
                //字段
                p = Pattern.compile("(?<=<\\s{0,9})\\S+(?=\\s*:[^>]*>)");
                m = p.matcher(arrData[i]);
                if (m.find()) {
                    field = arrData[i].substring(m.start(), m.end());
                }

                //比较运算符
                p = Pattern.compile("(?<=>\\s{0,9})(!=|>=|<=|=|>|<|in|like)(?=\\s+)");
                m = p.matcher(arrData[i]);
                if (m.find()) {
                    comp = arrData[i].substring(m.start(), m.end());
                }

                //查询内容
                p = Pattern.compile("(?<=>\\s{0,9}(?:!=|>=|<=|=|>|<|in|like)\\s{0,9})[^<>]+?(?=\\s+and|\\s+or|\\s*$)");
                m = p.matcher(arrData[i]);
                if (m.find()) {
                    value = arrData[i].substring(m.start(), m.end()).replaceAll("'", "");
                }

                //字段类型
                sql = "select FFieldType from Tb_Fun_TableDict where FTableCode=" + dbl.sqlString(table) +
                    " and FFieldCode=" + dbl.sqlString(field);
                rs = dbl.openResultSet(sql);

                while (rs.next()) {
                    type = rs.getString("FFieldType");
                }

                if (type.equalsIgnoreCase("string")) {
                    //如果是字符类型转换为相应格式
                    if (comp.toLowerCase().equalsIgnoreCase("in")) {
                        p = Pattern.compile("(?<=\\(\\s{0,9}|,\\s{0,9})\\S+?[^\\(\\),]*?\\S+?(?=\\s*,|\\s*\\))");
                        m = p.matcher(value);
                        while (m.find()) {
                            value = m.replaceAll(dbl.sqlString(value.substring(m.start(), m.end())));
                        }
                    } else {
                        value = dbl.sqlString(value.trim());
                    }
                } else if (type.equalsIgnoreCase("date")) {
                    //如果是日期转换为相应格式
                    if (comp.toLowerCase().equalsIgnoreCase("in")) {
                        p = Pattern.compile("(?<=\\(\\s{0,9}|,\\s{0,9})\\S+?[^\\(\\),]*?\\S+?(?=\\s*,|\\s*\\))");
                        m = p.matcher(value);
                        while (m.find()) {
                            value = m.replaceAll(dbl.sqlDate(YssFun.toDate(value.substring(m.start(), m.end()))));
                        }
                    } else {
                        value = dbl.sqlDate(YssFun.toDate(value.trim()));
                    }
                }
                //用转换后的表达式替换原有的表达式
                filter = filter.replaceAll(arrData[i], field + " " + comp + " " + value);
            }
            result = " where " + filter;
        } catch (Exception ex) {
            throw new YssException("解析筛选条件表达式出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }

    /**
     * 根据核算项目名称查询得到明细内容
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
        String code = "", name = "", desc = "", filter = "", table = "";
        try {
            if (m_Data == null) {
                m_Data = new AuxDetailBean();
                m_Data.setYssPub(pub);
            }

            sHeader = this.getListView3Headers();

            //获取核算项目设置信息
            strSql = "select * from Tb_Base_Auxiliary where fauxcode=" + dbl.sqlString(m_Filter.getAuxCode());
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                code = rs.getString("FCodeField");
                name = rs.getString("FNameField");
                desc = rs.getString("FDescField");
                filter = rs.getString("FFilter");
                table = rs.getString("FDataTable");
            }
            else
            {
                return "";
            }

            //获取核算项目明细内容
            strSql = "select " + code + " as FItemCode," + name + " as FItemName," + desc + " as FItemDesc," +
                dbl.sqlString(m_Filter.getAuxCode()) + " as FAuxCode " +
                " from " + pub.yssGetTableName(table) +
                this.buildFilterSql(filter, table);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView3ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                m_Data.setValues(rs);
                bufAll.append(m_Data.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
                this.getListView3ShowCols();

        } catch (Exception e) {
            throw new YssException("获取核算项目明细项出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据核算项目代码和明细项目代码取得明细项目名称
     * @param auxCode String
     * @param auxDetailCode String
     * @return String
     * @throws YssException
     */
    public String getAuxDetailName(String auxCode,String auxDetailCode) throws YssException {
         String strSql = "";
         String result = "";
         ResultSet rs = null;
         String code = "", name = "", desc = "", filter = "", table = "";
         try {

             //获取核算项目设置信息
             strSql = "select * from Tb_Base_Auxiliary where fauxcode=" + dbl.sqlString(auxCode);
             rs = dbl.openResultSet(strSql);
             if (rs.next()) {
                 code = rs.getString("FCodeField");
                 name = rs.getString("FNameField");
                 desc = rs.getString("FDescField");
                 filter = rs.getString("FFilter");
                 table = rs.getString("FDataTable");
             }
             else
             {
                 return "";
             }

             dbl.closeResultSetFinal(rs);

             //获取核算项目明细内容
             strSql = "select * from (" +
                 "select " + code + " as FItemCode," + name + " as FItemName," + desc + " as FItemDesc " +
                 " from " + pub.yssGetTableName(table) +
                 this.buildFilterSql(filter, table)+
                 " ) where FItemCode = " + dbl.sqlString(auxDetailCode);

             rs = dbl.openResultSet(strSql);

             if(rs.next())
             {
                 result=rs.getString("FItemName");
             }

         } catch (Exception e) {
             throw new YssException("获取核算项目明细项出错！", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
         return result;
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

    /**
     * 将请求数据转换为相应的变量数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            //保存Request数据
            m_Request = sRowStr;

            //实例化变量
            if (this.m_Filter == null) {
                this.m_Filter = new AuxDetailBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new AuxDetailBean();
                this.m_Data.setYssPub(pub);
            }

            if (this.m_OldData == null) {
                this.m_OldData = new AuxDetailBean();
                this.m_OldData.setYssPub(pub);
            }

            reqAry = sRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            for (int i = 0; i < reqAry.length; i++) {
                if (reqAry[i].startsWith("filter:")) {
                    this.m_Filter.parseRowStr(reqAry[i].substring(7)); //转换筛选条件
                } else if (reqAry[i].startsWith("data:")) {
                    this.m_Data.parseRowStr(reqAry[i].substring(5)); //转换新的设置信息
                } else if (reqAry[i].startsWith("olddata:")) {
                    this.m_OldData.parseRowStr(reqAry[i].substring(8)); //转换旧的设置信息
                }
            }
        } catch (Exception e) {
            throw new YssException("解析核算项目明细项信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
