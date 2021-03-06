package com.yss.main.account.accsetting.admin;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.account.accsetting.pojo.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>Title: 核算项目定义</p>
 *
 * <p>Description: 核算项目定义</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author CTQ
 * @version 1.0
 */
public class AuxiliaryAdmin
    extends BaseDataSettingBean implements IDataSetting {

    private AuxiliaryBean m_Data;
    private AuxiliaryBean m_OldData;
    private AuxiliaryBean m_Filter;

    private String m_Request;

    public AuxiliaryAdmin() {
    }

    /**
     * 检查数据是否存在
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_Auxiliary", "FAuxCode", m_Data.getAuxCode(), m_OldData.getAuxCode());
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
        try {
            //插入记录
            strSql = "insert into Tb_Base_Auxiliary" +
                "(FAuxCode,FAuxName,FAuxType,FDataTable,FFilter,FCodeField,FNameField,FDescField," +
                "FCheckState,FCreator,FCreateTime) " +
                "values(" +
                dbl.sqlString(m_Data.getAuxCode()) + "," +
                dbl.sqlString(m_Data.getAuxName()) + "," +
                dbl.sqlString(m_Data.getAuxTypeCode()) + "," +
                dbl.sqlString(m_Data.getDataTableCode()) + "," +
                dbl.sqlString(m_Data.getFilter()) + "," +
                dbl.sqlString(m_Data.getCodeFieldCode()) + "," +
                dbl.sqlString(m_Data.getNameFieldCode()) + "," +
                dbl.sqlString(m_Data.getDescFieldCode()) + "," +

                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(m_Data.creatorCode) + "," +
                dbl.sqlString(m_Data.creatorTime) + ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增核算项目设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
        try {
            //更新记录
            strSql = "update Tb_Base_Auxiliary" +
                " set FAuxCode = " + dbl.sqlString(m_Data.getAuxCode()) +
                ",FAuxName = " + dbl.sqlString(m_Data.getAuxName()) +
                ",FAuxType = " + dbl.sqlString(m_Data.getAuxTypeCode()) +
                ",FDataTable = " + dbl.sqlString(m_Data.getDataTableCode()) +
                ",FFilter = " + dbl.sqlString(m_Data.getFilter()) +
                ",FCodeField = " + dbl.sqlString(m_Data.getCodeFieldCode()) +
                ",FNameField = " + dbl.sqlString(m_Data.getNameFieldCode()) +
                ",FDescField = " + dbl.sqlString(m_Data.getDescFieldCode()) +
                ",FCREATOR = " + dbl.sqlString(m_Data.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(m_Data.creatorTime) +
                " where FAuxCode = " + dbl.sqlString(m_OldData.getAuxCode());

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改核算项目设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
            //更新记录的审核状态为2
            strSql = "update Tb_Base_Auxiliary" +
                " set FCheckState = 2" +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = " +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " where FAuxCode = " + dbl.sqlString(m_Data.getAuxCode());
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除核算项目设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 审核设置信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String data = "";
        String[] arrData = null;
        String[] arrItem = null;
        boolean bTrans = false;
        Connection conn = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;

            //由于回收站还原操作也使用此方法，所以必须支持批量操作
            //注意：回收站还原操作的请求数据的格式与审核操作的请求数据格式不同
            arrData = this.m_Request.split("\r\t")[0].split(YssCons.YSS_PASSAGESPLITMARK);
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
				/**shashijie 2012-7-2 STORY 2475 */
                if (arrData[i].toLowerCase().startsWith("filter:") ||
                		arrData[i].toLowerCase().startsWith("olddate:")) {
				/**end*/
                    continue;
                } else if (arrData[i].toLowerCase().startsWith("data:")) {
                    data = arrData[i].substring(5);
                } else {
                    data = arrData[i];
                }

                arrItem = data.split("\r\n");

                //循环更新记录的审核状态
                for (int j = 0; j < arrItem.length; j++) {
                    if (arrItem[j].length() == 0) {
                        continue;
                    }

                    this.m_Data.parseRowStr(arrItem[j]);

                    strSql = "update Tb_Base_Auxiliary" +
                        " set FCheckState = case fcheckstate when 0 then 1 else 0 end" +
                        ", FCheckUser = case fcheckstate when 0 then " +
                        dbl.sqlString(pub.getUserCode()) + " else null end" +
                        ", FCheckTime = case fcheckstate when 0 then " +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " else null end" +
                        " where FAuxCode = " + dbl.sqlString(m_Data.getAuxCode());

                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核核算项目设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

    /**
     * 清空回收站记录
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = false;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;

            arrData = this.m_Request.split("\r\t")[0].split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }

                this.m_Data.parseRowStr(arrData[i]);

                //删除核算项目表数据
                strSql = "delete from Tb_Base_Auxiliary" +
                    " where FAuxCode = " + dbl.sqlString(this.m_Data.getAuxCode());
                dbl.executeSql(strSql);

                //删除扩展项目明细内容数据
                if (this.m_Data.getAuxTypeCode().equalsIgnoreCase("extra")) {
                    strSql = "delete from Tb_Base_ExtraAuxiliary" +
                        " where FAuxCode = " + dbl.sqlString(this.m_Data.getAuxCode());
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    /**
     *获取设置内容列表信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        ResultSet rs = null;

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            if (m_Data == null) {
                m_Data = new AuxiliaryBean();
                m_Data.setYssPub(pub);
            }

            sHeader = this.getListView1Headers();

            strSql = "select * from (" +
                "select t1.FAuxCode,t1.FAuxName,t1.FAuxType as FAuxTypeCode,t4.fvocname as FAuxTypeName," +
                "t1.FDataTable as FDataTableCode,t5.ftablename as FDataTableName,t1.FFilter," +
                "t1.fcodefield as FCodeFieldCode,t6.ffieldname as FCodeFieldName," +
                "t1.fnamefield as FNameFieldCode,t7.ffieldname as FNameFieldName," +
                "t1.fdescfield as FDescFieldCode,t8.ffieldname as FDescFieldName," +
                "t1.FCheckState,t1.fcreator,t2.fusername as FCreatorName," +
                "t1.fcheckuser,t3.fusername as FCheckUserName," +
                "t1.FCreateTime,t1.FCheckTime " +

                "from tb_base_auxiliary t1 " +

                "left join tb_sys_userlist t2 on t1.fcreator=t2.fusercode " +
                "left join tb_sys_userlist t3 on t1.fcheckuser=t3.fusercode " +
                "left join (select * from tb_fun_vocabulary where fvoctypecode=" +
                dbl.sqlString(YssCons.YSS_ACC_AuxType) +
                ") t4 on t1.fauxtype=t4.fvoccode " +
                "left join (select distinct ftablecode,ftablename from tb_fun_tabledict) t5 on t1.fdatatable=t5.ftablecode " +
                "left join tb_fun_tabledict t6 on t1.fdatatable=t6.ftablecode and t1.fcodefield=t6.ffieldcode " +
                "left join tb_fun_tabledict t7 on t1.fdatatable=t7.ftablecode and t1.fnamefield=t7.ffieldcode " +
                "left join tb_fun_tabledict t8 on t1.fdatatable=t8.ftablecode and t1.fdescfield=t8.ffieldcode " +
                ")" + this.buildFilterSql() + " order by FCheckTime,FCreateTime desc";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ACC_AuxType + "," + YssCons.YSS_ACC_CompOperator + "," + YssCons.YSS_ACC_LogicOperator);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取核算项目设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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

            if (this.m_Filter.getAuxCode().length() != 0) {
                sResult = sResult + " and FAuxCode like " +
                    dbl.sqlString(this.m_Filter.getAuxCode().replaceAll("'", "''"));
            }

            if (this.m_Filter.getAuxName().length() != 0) {
                sResult = sResult + " and FAuxName like " +
                    dbl.sqlString(this.m_Filter.getAuxName().replaceAll("'", "''"));
            }
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.m_Filter.getAuxTypeCode().equalsIgnoreCase("99") &&
            		this.m_Filter.getAuxTypeCode().length() != 0) {
			/**end*/
                sResult = sResult + " and FAuxTypeCode = '" +
                    this.m_Filter.getAuxTypeCode().replaceAll("'", "''") + "'";
            }

        }
        return sResult;
    }

    /**
     *获取核算项目列表
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        ResultSet rs = null;

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            if (m_Data == null) {
                m_Data = new AuxiliaryBean();
                m_Data.setYssPub(pub);
            }

            sHeader = "核算项目代码\t核算项目名称";

            strSql ="select t1.FAuxCode,t1.FAuxName,t1.FAuxType as FAuxTypeCode,t4.fvocname as FAuxTypeName," +
                "t1.FDataTable as FDataTableCode,t5.ftablename as FDataTableName,t1.FFilter," +
                "t1.fcodefield as FCodeFieldCode,t6.ffieldname as FCodeFieldName," +
                "t1.fnamefield as FNameFieldCode,t7.ffieldname as FNameFieldName," +
                "t1.fdescfield as FDescFieldCode,t8.ffieldname as FDescFieldName," +
                "t1.FCheckState,t1.fcreator,t2.fusername as FCreatorName," +
                "t1.fcheckuser,t3.fusername as FCheckUserName," +
                "t1.FCreateTime,t1.FCheckTime " +

                "from tb_base_auxiliary t1 " +

                "left join tb_sys_userlist t2 on t1.fcreator=t2.fusercode " +
                "left join tb_sys_userlist t3 on t1.fcheckuser=t3.fusercode " +
                "left join (select * from tb_fun_vocabulary where fvoctypecode=" +
                dbl.sqlString(YssCons.YSS_ACC_AuxType) +
                ") t4 on t1.fauxtype=t4.fvoccode " +
                "left join (select distinct ftablecode,ftablename from tb_fun_tabledict) t5 on t1.fdatatable=t5.ftablecode " +
                "left join tb_fun_tabledict t6 on t1.fdatatable=t6.ftablecode and t1.fcodefield=t6.ffieldcode " +
                "left join tb_fun_tabledict t7 on t1.fdatatable=t7.ftablecode and t1.fnamefield=t7.ffieldcode " +
                "left join tb_fun_tabledict t8 on t1.fdatatable=t8.ftablecode and t1.fdescfield=t8.ffieldcode " ;

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append(rs.getString("FAuxCode")).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(rs.getString("FAuxName")).append(YssCons.YSS_LINESPLITMARK);

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
                this.getListView1ShowCols() ;

        } catch (Exception e) {
            throw new YssException("获取核算结构设置信息出错！", e);
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

            //保存Request数据
            m_Request = sRowStr;

            if (this.m_Filter == null) {
                this.m_Filter = new AuxiliaryBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new AuxiliaryBean();
                this.m_Data.setYssPub(pub);
            }

            if (this.m_OldData == null) {
                this.m_OldData = new AuxiliaryBean();
                this.m_OldData.setYssPub(pub);
            }

            reqAry = sRowStr.split(YssCons.YSS_PASSAGESPLITMARK);
            for (int i = 0; i < reqAry.length; i++) {
                if (reqAry[i].startsWith("filter:")) {
                    this.m_Filter.parseRowStr(reqAry[i].substring(7));
                } else if (reqAry[i].startsWith("data:")) {
                    this.m_Data.parseRowStr(reqAry[i].substring(5));
                } else if (reqAry[i].startsWith("olddata:")) {
                    this.m_OldData.parseRowStr(reqAry[i].substring(8));
                }
            }
        } catch (Exception e) {
            throw new YssException("解析核算项目设置信息出错", e);
        }
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
