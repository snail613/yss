package com.yss.main.account.accsetting.admin;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import com.yss.main.account.accsetting.pojo.AccountSetBean;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import java.sql.ResultSet;
import com.yss.main.account.accsetting.pojo.AccountPeriodBean;
import java.sql.Connection;
import com.yss.util.YssFun;
import java.util.ArrayList;

/**
 * <p>Title: 套账设置</p>
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
public class AccountSetAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private AccountSetBean m_Data;
    private AccountSetBean m_OldData;
    private AccountSetBean m_Filter;

    private String m_Request;

    private String m_Command;

    public AccountSetAdmin() {
    }

    /**
     * 检查数据是否存在
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        if (!m_Command.trim().equalsIgnoreCase("ChangePeriod")) {
            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Acc_Set"), "FSetCode", m_Data.getSetCode(), m_OldData.getSetCode());
        }
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
            conn.setAutoCommit(false);
            bTrans = true;

            //插入新记录到套账信息表
            strSql = "insert into " + pub.yssGetTableName("Tb_Acc_Set") +
                "(FSetCode,FSetName,FSetType,FPortCode,FStartDate,FCodeLen,FCuryCode,FPeriod,FYear," +
                "FCheckState,FCreator,FCreateTime) " +
                "values(" +
                dbl.sqlString(m_Data.getSetCode()) + "," +
                dbl.sqlString(m_Data.getSetName()) + "," +
                dbl.sqlString(m_Data.getTypeCode()) + "," +
                dbl.sqlString(m_Data.getPortCode()) + "," +
                dbl.sqlDate(YssFun.toDate(m_Data.getStartDate())) + "," +
                dbl.sqlString(m_Data.getCodeLen()) + "," +
                dbl.sqlString(m_Data.getCuryCode()) + "," +
                m_Data.getPeriod() + "," +
                m_Data.getYear() + "," +

                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(m_Data.creatorCode) + "," +
                dbl.sqlString(m_Data.creatorTime) + ")";

            dbl.executeSql(strSql);

            //插入新纪录到会计期间表
            Object[] periods = m_Data.getPeriodArray().toArray();
            AccountPeriodBean period;
            for (int i = 0; i < periods.length; i++) {
                period = (AccountPeriodBean) periods[i];

                strSql = "insert into " + pub.yssGetTableName("Tb_Acc_Period") +
                    "(FSetCode,FYear,FPeriod,FStartDate,FEndDate) " +
                    "values(" +
                    dbl.sqlString(m_Data.getSetCode()) + "," +
                    period.getYear() + "," +
                    period.getPeriod() + "," +
                    dbl.sqlDate(YssFun.toDate(period.getStartDate())) + "," +
                    dbl.sqlDate(YssFun.toDate(period.getEndDate())) + ")";

                dbl.executeSql(strSql);
            }

            if (!m_Data.getTypeCode().equalsIgnoreCase("unit")) {
                //如果套账类型不为明细套账则插入新纪录到套账关联表
                Object[] sets = m_Data.getLinkSetArray().toArray();
                AccountSetBean set;
                for (int i = 0; i < sets.length; i++) {
                    set = (AccountSetBean) sets[i];

                    strSql = "insert into " + pub.yssGetTableName("Tb_Acc_Link") +
                        "(FParent,FChild,FType) " +
                        "values(" +
                        dbl.sqlString(m_Data.getSetCode()) + "," +
                        dbl.sqlString(set.getSetCode()) + "," +
                        dbl.sqlString("AccSet") + ")";

                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增套账设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return m_Data.buildRowStr();
    }

    /**
     * 修改设置信息
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            if (m_Command.trim().equalsIgnoreCase("ChangePeriod")) {
                //如果m_Command="ChangePeriod"则只更新会计期间
                strSql = "update " + pub.yssGetTableName("Tb_Acc_Set") +
                    " set FPeriod = " + m_Data.getPeriod() +
                    ",FYear = " + m_Data.getYear() +
                    " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());

                dbl.executeSql(strSql);
            } else {
                boolean existData = this.exisVchData(m_OldData.getSetCode());

                if (existData) {
                    //如果存在凭证数据，则不允许修改套账代码、套账类型、关联组合、本位币、启用日期
                    strSql = "update " + pub.yssGetTableName("Tb_Acc_Set") +
                        " set FSetName = " + dbl.sqlString(m_Data.getSetName()) +
                        ",FCodeLen = " + dbl.sqlString(m_Data.getCodeLen()) +
                        ",FPeriod = " + m_Data.getPeriod() +
                        ",FYear = " + m_Data.getYear() +
                        ",FCREATOR = " + dbl.sqlString(m_Data.creatorCode) +
                        ",FCREATETIME = " + dbl.sqlString(m_Data.creatorTime) +
                        " where FSetCode = " + dbl.sqlString(m_OldData.getSetCode());
                } else {
                    //修改套账信息表
                    strSql = "update " + pub.yssGetTableName("Tb_Acc_Set") +
                        " set FSetCode = " + dbl.sqlString(m_Data.getSetCode()) +
                        ",FSetName = " + dbl.sqlString(m_Data.getSetName()) +
                        ",FSetType = " + dbl.sqlString(m_Data.getTypeCode()) +
                        ",FPortCode = " + dbl.sqlString(m_Data.getPortCode()) +
                        ",FStartDate = " + dbl.sqlDate(YssFun.toDate(m_Data.getStartDate())) +
                        ",FCodeLen = " + dbl.sqlString(m_Data.getCodeLen()) +
                        ",FCuryCode = " + dbl.sqlString(m_Data.getCuryCode()) +
                        ",FPeriod = " + m_Data.getPeriod() +
                        ",FYear = " + m_Data.getYear() +
                        ",FCREATOR = " + dbl.sqlString(m_Data.creatorCode) +
                        ",FCREATETIME = " + dbl.sqlString(m_Data.creatorTime) +
                        " where FSetCode = " + dbl.sqlString(m_OldData.getSetCode());
                }
                dbl.executeSql(strSql);

                //修改会计期间信息

                //删除当会计期间信息
                strSql = "delete from " + pub.yssGetTableName("Tb_Acc_Period") +
                    " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());
                dbl.executeSql(strSql);

                Object[] periods = m_Data.getPeriodArray().toArray();
                AccountPeriodBean period;
                for (int i = 0; i < periods.length; i++) {
                    period = (AccountPeriodBean) periods[i];

                    //重新插入会计期间信息
                    strSql = "insert into " + pub.yssGetTableName("Tb_Acc_Period") +
                        "(FSetCode,FYear,FPeriod,FStartDate,FEndDate) " +
                        "values(" +
                        dbl.sqlString(m_Data.getSetCode()) + "," +
                        period.getYear() + "," +
                        period.getPeriod() + "," +
                        dbl.sqlDate(YssFun.toDate(period.getStartDate())) + "," +
                        dbl.sqlDate(YssFun.toDate(period.getEndDate())) + ")";

                    dbl.executeSql(strSql);
                }

                for (int i = 0; i < periods.length; i++) {
                    period = (AccountPeriodBean) periods[i];

                    if(period.getStatus().trim().equalsIgnoreCase("Edit") && period.getCloseStateCode()==0)
                    {
                        //移动凭证数据
                        //因为前面删除了全部会计期间信息，所以不能将本语句放在上面的循环中
                        strSql="update "+pub.yssGetTableName("tb_acc_voucher")+" t1 "+
                            " set fperiod=(select fperiod from " +pub.yssGetTableName("tb_acc_period") +" t2 "+
                            " where t2.fsetcode=t1.fsetcode and t2.fyear=t1.fyear and t2.fstartdate<=t1.fdate and t2.fenddate>=t1.fdate) "+
                            " where fsetcode=" + dbl.sqlString(m_Data.getSetCode()) + " and fyear=" + period.getYear() +
                            " and fperiod=" + period.getPeriod() +
                            " and (fdate < (select fstartdate from " + pub.yssGetTableName("tb_acc_period") +
                            " where fsetcode=" + dbl.sqlString(m_Data.getSetCode()) + " and fyear=" + period.getYear() +
                            " and fperiod=" + period.getPeriod() + ")" +
                            " or fdate > (select fenddate from " + pub.yssGetTableName("tb_acc_period") +
                            " where fsetcode=" + dbl.sqlString(m_Data.getSetCode()) + " and fyear=" + period.getYear() +
                            " and fperiod=" + period.getPeriod() + "))" ;

                            dbl.executeSql(strSql);
                    }
                }

                //修改关联套账信息
                if (!m_Data.getTypeCode().equalsIgnoreCase("unit")) {

                    //先删除关联套账表中的信息
                    strSql = "delete from " + pub.yssGetTableName("Tb_Acc_Link") +
                        " where FType = " + dbl.sqlString("AccSet") + " and FParent = " + dbl.sqlString(m_OldData.getSetCode());
                    dbl.executeSql(strSql);

                    //如果套账类型不为明细套账则插入新纪录到套账关联表
                    Object[] sets = m_Data.getLinkSetArray().toArray();
                    AccountSetBean set;
                    for (int i = 0; i < sets.length; i++) {
                        set = (AccountSetBean) sets[i];

                        strSql = "insert into " + pub.yssGetTableName("Tb_Acc_Link") +
                            "(FParent,FChild,FType) " +
                            "values(" +
                            dbl.sqlString(m_Data.getSetCode()) + "," +
                            dbl.sqlString(set.getSetCode()) + "," +
                            dbl.sqlString("AccSet") + ")";

                        dbl.executeSql(strSql);
                    }
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增套账设置信息出错", e);
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
            conn.setAutoCommit(false);
            bTrans = true;

            //更新记录的审核状态为2
            strSql = "update " + pub.yssGetTableName("Tb_Acc_Set") +
                " set FCheckState = 2" +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = " +
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());

            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除套账设置信息出错", e);
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

                    strSql = "update " + pub.yssGetTableName("Tb_Acc_Set") +
                        " set FCheckState = case fcheckstate when 0 then 1 else 0 end" +
                        ", FCheckUser = case fcheckstate when 0 then " +
                        dbl.sqlString(pub.getUserCode()) + " else null end" +
                        ", FCheckTime = case fcheckstate when 0 then " +
                        dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " else null end " +
                        " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());

                    dbl.executeSql(strSql);
                }
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核套账设置信息出错", e);
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

                //删除套账设置信息
                strSql = "delete from " + pub.yssGetTableName("Tb_Acc_Set") +
                    " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());
                dbl.executeSql(strSql);

                //删除会计期间信息
                strSql = "delete from " + pub.yssGetTableName("Tb_Acc_Period") +
                    " where FSetCode = " + dbl.sqlString(m_Data.getSetCode());
                dbl.executeSql(strSql);

                //删除关联套账信息
                strSql = "delete from " + pub.yssGetTableName("Tb_Acc_Link") +
                    " where FType= " + dbl.sqlString("AccSet") + " and  FParent = " + dbl.sqlString(m_Data.getSetCode());
                dbl.executeSql(strSql);
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
     * 套账设置信息列表,不包含会计期间信息和关联套账信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        String result="";
        ResultSet rs = null;
        ResultSet rsPeriod = null;
        ResultSet rsLinkSet = null;

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            if (m_Data == null) {
                m_Data = new AccountSetBean();
                m_Data.setYssPub(pub);
            }

            sHeader = this.getListView1Headers();

            strSql = "select * from (" +
                "select t1.fsetcode,t1.fsetname,t1.fportcode,t2.fportname," +
                "t1.fsettype as ftypecode,t3.fvocname as ftypename," +
                "t1.fstartdate,t1.fcodelen,t1.fcurycode,t6.fcuryname,t1.fyear,t1.fperiod," +
                "t1.FCheckState,t1.fcreator,t4.fusername as FCreatorName," +
                "t1.fcheckuser,t5.fusername as FCheckUserName," +
                "t1.FCreateTime,t1.FCheckTime " +
                " from " + pub.yssGetTableName("tb_acc_Set") + " t1 " +
                " left join " + pub.yssGetTableName("tb_para_portfolio") + " t2 on t1.fportcode=t2.fportcode " +
                " left join tb_fun_vocabulary  t3 on t1.fsettype=t3.fvoccode and t3.fvoctypecode=" + dbl.sqlString(YssCons.YSS_ACC_SetType) +
                " left join tb_sys_userlist t4 on t1.fcreator=t4.fusercode " +
                " left join tb_sys_userlist t5 on t1.fcheckuser=t5.fusercode " +
                " left join " + pub.yssGetTableName("tb_para_currency") + " t6 on t1.fcurycode=t6.fcurycode " +
                ") " + this.buildFilterSql() + " order by FCheckTime,FCreateTime desc";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                m_Data.setValues(rs); // 此处赋值不包含会计期间列表和关联套账列表部分

                if (this.exisVchData(rs.getString("FSetCode"))) {
                    m_Data.setExistData(true);
                } else {
                    m_Data.setExistData(false);
                }

                //如果COMMAND为GetAllInfo则获取会计期间和关联套账信息
                if (m_Command!=null && m_Command.trim().indexOf("GetAllInfo")>-1) {
                    //获取关联的会计期间列表
                    strSql = "select t1.fsetcode,t2.fsetname,t1.fyear,t1.fperiod,t1.fstartdate,t1.fenddate, case when t3.fcloser is null then 0 else 1 end as fclosestate " +
                        " from " + pub.yssGetTableName("tb_acc_period") + " t1 " +
                        " left join " + pub.yssGetTableName("Tb_Acc_Set") + " t2 on t1.fsetcode=t2.fsetcode " +
                        " left join " + pub.yssGetTableName("Tb_Acc_Close") + " t3 on t1.fsetcode=t3.fsetcode and t1.fyear=t3.fyear and t1.fperiod=t3.fperiod " +
                        " where t1.fsetcode=" + dbl.sqlString(m_Data.getSetCode()) +
                        " order by fstartdate ";

                    rsPeriod = dbl.openResultSet(strSql);

                    m_Data.getPeriodArray().clear(); //添加前先清空套账列表

                    while (rsPeriod.next()) {
                        AccountPeriodBean period = new AccountPeriodBean();

                        period.setValues(rsPeriod);

                        if (this.exisVchData(rs.getString("FSetCode"), rs.getString("FYear"), rs.getString("FPeriod"))) {
                            period.setExistData(true);
                        } else {
                            period.setExistData(false);
                        }

                        m_Data.getPeriodArray().add(period);
                    }

                    dbl.closeResultSetFinal(rsPeriod);

                    if (m_Data.getTypeCode().equalsIgnoreCase("collection")) {
                        //获取关联的套账列表
                        strSql = "select * from (" +
                            "select t1.fsetcode,t1.fsetname,t1.fportcode,t2.fportname," +
                            "t1.fsettype as ftypecode,t3.fvocname as ftypename," +
                            "t1.fstartdate,t1.fcodelen,t1.fcurycode,t6.fcuryname,t1.fyear,t1.fperiod," +
                            "t1.FCheckState,t1.fcreator,t4.fusername as FCreatorName," +
                            "t1.fcheckuser,t5.fusername as FCheckUserName," +
                            "t1.FCreateTime,t1.FCheckTime " +
                            " from " + pub.yssGetTableName("tb_acc_Set") + " t1 " +
                            " left join " + pub.yssGetTableName("tb_para_portfolio") + " t2 on t1.fportcode=t2.fportcode " +
                            " left join tb_fun_vocabulary  t3 on t1.fsettype=t3.fvoccode and t3.fvoctypecode=" + dbl.sqlString(YssCons.YSS_ACC_SetType) +
                            " left join tb_sys_userlist t4 on t1.fcreator=t4.fusercode " +
                            " left join tb_sys_userlist t5 on t1.fcheckuser=t5.fusercode " +
                            " left join " + pub.yssGetTableName("tb_para_currency") + " t6 on t1.fcurycode=t6.fcurycode " +
                            ") where fsetcode in (select FChild from " + pub.yssGetTableName("Tb_Acc_Link") +
                            " where FParent=" + dbl.sqlString(m_Data.getSetCode()) + " and FType=" + dbl.sqlString("AccSet") + ")";

                        rsLinkSet = dbl.openResultSet(strSql);

                        m_Data.getLinkSetArray().clear(); //添加前先清空套账列表

                        while (rsLinkSet.next()) {
                            AccountSetBean linkSet = new AccountSetBean();

                            linkSet.setValues(rsLinkSet);
                            m_Data.getLinkSetArray().add(linkSet);
                        }

                        dbl.closeResultSetFinal(rsLinkSet);
                    }
                }
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_ACC_SetType);

            if (m_Command != null && m_Command.trim().indexOf("GetSetDataOnly")>-1) {
                result = sAllDataStr;
            } else {
                result = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
            }

        } catch (Exception e) {
            throw new YssException("获取核算项目设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsPeriod);
            dbl.closeResultSetFinal(rsLinkSet);

        }
        return result;
    }

    /**
     *获取明细套账列表，排除已被关联的套账，不包含会计期间和关联套账信息
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
            if (m_Data == null) {
                m_Data = new AccountSetBean();
                m_Data.setYssPub(pub);
            }

            sHeader = "套账代码\t套账名称";

            //注意：一个分套账只能关联到一个汇总套账
            strSql = "select * from (" +
                "select t1.fsetcode,t1.fsetname,t1.fportcode,t2.fportname," +
                "t1.fsettype as ftypecode,t3.fvocname as ftypename," +
                "t1.fstartdate,t1.fcodelen,t1.fcurycode,t6.fcuryname,t1.fyear,t1.fperiod," +
                "t1.FCheckState,t1.fcreator,t4.fusername as FCreatorName," +
                "t1.fcheckuser,t5.fusername as FCheckUserName," +
                "t1.FCreateTime,t1.FCheckTime,t7.fchild " +
                " from " + pub.yssGetTableName("tb_acc_Set") + " t1 " +
                " left join " + pub.yssGetTableName("tb_para_portfolio") + " t2 on t1.fportcode=t2.fportcode " +
                " left join tb_fun_vocabulary  t3 on t1.fsettype=t3.fvoccode and t3.fvoctypecode=" + dbl.sqlString(YssCons.YSS_ACC_SetType) +
                " left join tb_sys_userlist t4 on t1.fcreator=t4.fusercode " +
                " left join tb_sys_userlist t5 on t1.fcheckuser=t5.fusercode " +
                " left join " + pub.yssGetTableName("tb_para_currency") + " t6 on t1.fcurycode=t6.fcurycode " +
                " left join (select distinct fchild from " + pub.yssGetTableName("tb_acc_link") + " where ftype= " + dbl.sqlString("AccSet");

            if (m_Filter != null) {
                if (m_Filter.getSetCode().trim().length() != 0) {
                    //如果是编辑状态，则在弹出列表中应包含原有的套账
                    strSql += " and fparent <> " + dbl.sqlString(m_Filter.getSetCode());
                }
            }

            strSql += ") t7 on t1.fsetcode=t7.fchild " + ") " +
                " where fcheckstate=1 and fchild is null and ftypecode=" + dbl.sqlString("Unit");

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                bufShow.append( (rs.getString("fsetcode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("fsetname") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                m_Data.setValues(rs); // 此处赋值不包含会计期间列表和关联套账列表部分

                bufAll.append(m_Data.buildBasicInfoRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取套账列表信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取所有明细套账列表
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
             if (m_Data == null) {
                 m_Data = new AccountSetBean();
                 m_Data.setYssPub(pub);
             }

             sHeader = "套账代码\t套账名称";

             strSql = "select t1.fsetcode,t1.fsetname,t1.fportcode,t2.fportname," +
                 "t1.fsettype as ftypecode,t3.fvocname as ftypename," +
                 "t1.fstartdate,t1.fcodelen,t1.fcurycode,t6.fcuryname,t1.fyear,t1.fperiod," +
                 "t1.FCheckState,t1.fcreator,t4.fusername as FCreatorName," +
                 "t1.fcheckuser,t5.fusername as FCheckUserName," +
                 "t1.FCreateTime,t1.FCheckTime " +
                 " from " + pub.yssGetTableName("tb_acc_Set") + " t1 " +
                 " left join " + pub.yssGetTableName("tb_para_portfolio") + " t2 on t1.fportcode=t2.fportcode " +
                 " left join tb_fun_vocabulary  t3 on t1.fsettype=t3.fvoccode and t3.fvoctypecode=" + dbl.sqlString(YssCons.YSS_ACC_SetType) +
                 " left join tb_sys_userlist t4 on t1.fcreator=t4.fusercode " +
                 " left join tb_sys_userlist t5 on t1.fcheckuser=t5.fusercode " +
                 " left join " + pub.yssGetTableName("tb_para_currency") + " t6 on t1.fcurycode=t6.fcurycode " +
                 " where t1.fcheckstate = 1";

             rs = dbl.openResultSet(strSql);

             while (rs.next()) {
                 bufShow.append(rs.getString("fsetcode").trim()).append(YssCons.YSS_ITEMSPLITMARK1);
                 bufShow.append(rs.getString("fsetname").trim()).append(YssCons.YSS_LINESPLITMARK);

                 m_Data.setValues(rs); // 此处赋值不包含会计期间列表和关联套账列表部分
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

             return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

         } catch (Exception e) {
             throw new YssException("获取套账设置信息出错！", e);
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

            m_Command = "";

            if (this.m_Filter == null) {
                this.m_Filter = new AccountSetBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new AccountSetBean();
                this.m_Data.setYssPub(pub);
            }

            if (this.m_OldData == null) {
                this.m_OldData = new AccountSetBean();
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
                } else if (reqAry[i].startsWith("command:")) {
                    this.m_Command = reqAry[i].substring(8);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析核算项目设置信息出错", e);
        }
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

            if (this.m_Filter.getSetCode().length() != 0) {
                sResult = sResult + " and FSetCode like " +
                    dbl.sqlString(this.m_Filter.getSetCode().replaceAll("'", "''"));
            }

            if (this.m_Filter.getSetName().length() != 0) {
                sResult = sResult + " and FSetName like " +
                    dbl.sqlString(this.m_Filter.getSetName().replaceAll("'", "''"));
            }
			/**shashijie 2012-7-2 STORY 2475 */
            if (!this.m_Filter.getTypeCode().equalsIgnoreCase("99") &&
            		this.m_Filter.getTypeCode().length() != 0) {
			/**end*/
                sResult = sResult + " and ftypecode = '" +
                    this.m_Filter.getTypeCode().replaceAll("'", "''") + "'";
            }
        }
        return sResult;
    }

    /**
     * 查询凭证表是否存在该套账的数据
     * @param setCode String
     * @return boolean
     * @throws YssException
     */
    private boolean exisVchData(String setCode, String year, String period) throws YssException {
        String strSql = "";
        try {
            //查询凭证表看是否存在套账的数据
            strSql = "select * from " + pub.yssGetTableName("Tb_Acc_Voucher") +
                " where FSetCode = " + dbl.sqlString(setCode);
            if (period != null) {
                if (period.trim().length() != 0) {
                    strSql += " and FPeriod = " + dbl.sqlString(period);
                }
            }
            if (year != null) {
                if (year.trim().length() != 0) {
                    strSql += " and FYear = " + dbl.sqlString(year);
                }
            }
            if (dbl.executeSqlwithReturnRows(strSql) > 0) {
                return true;
            }

            /*
            //查询余额表看是否存在套账的数据
            strSql = "select * from " + pub.yssGetTableName("Tb_Acc_Balance") +
                " where FSetCode = " + dbl.sqlString(setCode);
            if (period != null) {
                if (period.trim().length() != 0) {
                    strSql += " and FPeriod = " + dbl.sqlString(period);
                }
            }
            if (year != null) {
                if (year.trim().length() != 0) {
                    strSql += " and FYear = " + dbl.sqlString(year);
                }
            }
            if (dbl.executeSqlwithReturnRows(strSql) > 0) {
                return true;
            }
            */

            return false;
        } catch (Exception e) {
            throw new YssException("查询是否存在套账关联数据出错", e);
        }
    }

    private boolean exisVchData(String setCode) throws YssException {
        return exisVchData(setCode, "", "");
    }
}
