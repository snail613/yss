package com.yss.main.account.accsetting.admin;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.ResultSet;
import com.yss.main.account.accsetting.pojo.AccountPeriodBean;
import com.yss.util.YssCons;
import com.yss.util.YssFun;

/**
 * <p>Title:会计期间信息 </p>
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
public class AccountPeriodAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private AccountPeriodBean m_Data;
    private AccountPeriodBean m_OldData;
    private AccountPeriodBean m_Filter;

    private String m_Request;

    public AccountPeriodAdmin() {
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

    /**
     *根据套账代码和会计年度获取会计期间列表信息
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;

        StringBuffer bufAll = new StringBuffer();

        try {

            if (m_Data == null) {
                m_Data = new AccountPeriodBean();
                m_Data.setYssPub(pub);
            }

            strSql = "select * from (" +
                "select t1.fsetcode,t2.fsetname,t1.fyear,t1.fperiod,t1.fstartdate,t1.fenddate,t1.fclosestate " +
                " from " + pub.yssGetTableName("tb_acc_period") + " t1 " +
                " left join " + pub.yssGetTableName("Tb_Acc_Set") + " t2 on t1.fsetcode=t2.fsetcode " +
                ")" + this.buildFilterSql() + " order by fperiod ";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                m_Data.setValues(rs);

                bufAll.append(m_Data.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取会计期间信息出错！", e);
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

            if (this.m_Filter.getSetCode().length() != 0) {
                sResult = sResult + " and FSetCode = " +
                    dbl.sqlString(this.m_Filter.getSetCode().replaceAll("'", "''"));
            }

            if (this.m_Filter.getYear() != 0) {
                sResult = sResult + " and FYear = " + this.m_Filter.getYear();
            }
        }
        return sResult;
    }

    /**
     * 生成默认的会计期间信息
     * @return String
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        String sAllDataStr = "";
        String startDate; //起始日期
        String endDate; //截止日期
        ResultSet rs = null;

        StringBuffer bufAll = new StringBuffer();

        try {
            if (m_Filter == null) {
                return "";
            }

            if (m_Data == null) {
                m_Data = new AccountPeriodBean();
                m_Data.setYssPub(pub);
            }

            //设定会计期间共同项
            m_Data.setSetCode(m_Filter.getSetCode());
            m_Data.setSetName(m_Filter.getSetName());
            m_Data.setCloseStateCode(0);
            m_Data.setExistData(false);

            m_Data.setYear(m_Filter.getYear());

            for (int i = m_Filter.getPeriod(); i <= 12; i++) {
                startDate = m_Filter.getYear() + "-" + YssFun.formatNumber(i, "00") + "-01"; //默认起始日期为每月月初
                endDate = m_Filter.getYear() + "-" + YssFun.formatNumber(i, "00") + "-" + YssFun.formatNumber(YssFun.endOfMonth(m_Filter.getYear(), i), "00");

                //设定会计期间信息
                m_Data.setPeriod(i);
                m_Data.setStartDate(startDate);
                m_Data.setEndDate(endDate);

                bufAll.append(m_Data.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);

            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sAllDataStr;
        } catch (Exception e) {
            throw new YssException("生成默认会计期间信息出错！", e);
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
                this.m_Filter = new AccountPeriodBean();
                this.m_Filter.setYssPub(pub);
            }

            if (this.m_Data == null) {
                this.m_Data = new AccountPeriodBean();
                this.m_Data.setYssPub(pub);
            }

            if (this.m_OldData == null) {
                this.m_OldData = new AccountPeriodBean();
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
            throw new YssException("解析会计期间信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
}
