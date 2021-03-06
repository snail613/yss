package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchInterBean
    extends BaseDataSettingBean implements IDataSetting {

    private String beginDate = "";
    private String endDate = "";
    private String ports = "";
    private VchInterBean filterType = null;
    public VchInterBean() {
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        //ResultSet rs = null;
        //StringBuffer buf = new StringBuffer();
        //String vchTpls = "";
        try {
            /*列示凭证模版不需要根据凭证数据来列示的fazmm20070919
             strSql=" select distinct FVchTplCode from " +pub.yssGetTableName("Tb_Vch_Data")+
                   " where FVchDate between "+dbl.sqlDate(this.beginDate)+
                   " and "+dbl.sqlDate(this.endDate)+
                   " and FPortCode in (" +operSql.sqlCodes(this.ports)+")";
             rs=dbl.openResultSet(strSql);
             while(rs.next())
             {
                buf.append(rs.getString("FVchTplCode")).append(",");
             }
             if (buf.toString().length() > 1) {
               vchTpls = buf.toString().substring(0, buf.toString().length() - 1);
             }
             if(vchTpls.length()==0)
             {
                vchTpls="A_B_C";
             }*/
            strSql =
                " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName as FCuryName ," +
                " e.FLinkName as FLinkName ,f.FVchDsName as FVchDsName ,p.FPortName as FPortName ," +
                " g.FAttrName as FAttrName ,h.FVocName as FModeValue ,i.FDesc as FDateFieldName,j.FDesc as FSrcCuryName from " +
                pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FPortCode, FPortName from " + pub.yssGetTableName("Tb_Para_PortFolio") + ") p on a.FPortCode = p.FPortCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ")d on d.FCuryCode=a.FCuryCode" +
                " left join ( select trim(to_char(fsetcode,'000')) as flinkcode,fsetname as FLinkName from lsetlist ) " +
//                " left join (select FLinkCode,FLinkName from " +
//                pub.yssGetTableName("Tb_Vch_PortSetLink") +//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
                " e on e.FLinkCode=a.FLinkCode" +
                " left join (select FVchDsCode,FVchDsName from " +
                pub.yssGetTableName("Tb_Vch_DataSource") +
                ")f on f.FVchDsCode=a.FDsCode" +
                " left join (select FAttrCode,FAttrName from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                ")g on g.FAttrCode=a.FAttrCode" +
                " left join Tb_Fun_Vocabulary h on a.FMode = h.FVocCode and h.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MODE) +
                " left join(select FVchDsCode,FAliasName,FDesc from " +
                pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FCheckState=1)i on i.FAliasName=a.FDateField and i.FVchDsCode=a.FDsCode" +
                " left join(select FVchDsCode,FAliasName,FDesc from " +
                pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FCheckState=1)j on j.FAliasName=a.FSrcCury and j.FVchDsCode=a.FDsCode" +
                //" where 	FVchTplCode in (" +operSql.sqlCodes(vchTpls)+")";  //不需要进行判断fazmm20070912

                " where a.fcheckstate = 1";
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return buildListViewData(strSql);
    }

    private String buildListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        VchTplBean tpl = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                tpl = new VchTplBean();
                tpl.setVchTpl(rs);
                bufAll.append(tpl.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取凭证模版信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException { //在独立的窗体中获取只有两个列的数据
        String strSql = "";
        //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		 //ResultSet rs = null;
        //StringBuffer buf = new StringBuffer();
        //String vchTpls = "";
        try {
            strSql =
                " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName as FCuryName ," +
                " e.FLinkName as FLinkName ,f.FVchDsName as FVchDsName ,p.FPortName as FPortName ," +
                " g.FAttrName as FAttrName ,h.FVocName as FModeValue ,i.FDesc as FDateFieldName,j.FDesc as FSrcCuryName from " +
                pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                ")d on d.FCuryCode=a.FCuryCode" +
                " left join ( select trim(to_char(fsetcode,'000'))  as flinkcode,fsetname as FLinkName from lsetlist ) " +
                " e on e.FLinkCode=a.FLinkCode" +//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//                " left join (select FLinkCode,FLinkName from " +
//                pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                ")e on e.FLinkCode=a.FLinkCode" +
                " left join (select FVchDsCode,FVchDsName from " +
                pub.yssGetTableName("Tb_Vch_DataSource") +
                ")f on f.FVchDsCode=a.FDsCode" +
                " left join (select FAttrCode,FAttrName from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                ")g on g.FAttrCode=a.FAttrCode" +
                " left join Tb_Fun_Vocabulary h on a.FMode = h.FVocCode and h.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_MODE) +
                " left join(select FVchDsCode,FAliasName,FDesc from " +
                pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FCheckState=1)i on i.FAliasName=a.FDateField and i.FVchDsCode=a.FDsCode" +
                " left join(select FVchDsCode,FAliasName,FDesc from " +
                pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FCheckState=1)j on j.FAliasName=a.FSrcCury and j.FVchDsCode=a.FDsCode" +
                //" where 	FVchTplCode in (" +operSql.sqlCodes(vchTpls)+")";  //不需要进行判断fazmm20070912
                " left join (select FPortCode, FPortName from " +
                pub.yssGetTableName("Tb_Para_PortFolio") + ") p on a.FPortCode = p.FPortCode" +
                " where a.fcheckstate = 1";
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return buildListViewData2(strSql);

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException { //在独立的窗体中获取只有两个列的数据
        String strSql = "";
        strSql = " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
            " where 1=2";
        return buildListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        String strSql = "";
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        String sVocStr = ""; //词汇类型对照字符串
        strSql = "select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
            " where 1=2 ";
        sVocStr = vocabulary.getVoc(YssCons.YSS_VCH_INOUTMDB);
        return buildListViewData2(strSql) + "\r\f" + "voc" + sVocStr;
    }

    /**
     * 只有两个列
     * @param sqlStr String
     * @throws YssException
     * @return String
     */
    private String buildListViewData2(String sqlStr) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        VchTplBean tpl = null;
        try {
            sHeader = "模版代码\t凭证名称";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(rs.getString("FVchTplCode")).append(YssCons.YSS_ITEMSPLITMARK1);
                bufShow.append(rs.getString("FVchTplName")).append(YssCons.YSS_LINESPLITMARK);
                tpl = new VchTplBean();
                tpl.setVchTpl(rs);
                bufAll.append(tpl.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException("获取凭证模版信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
    }

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.beginDate = reqAry[0];
            this.endDate = reqAry[1];
            this.ports = reqAry[2];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VchInterBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析数据请求出错", e);
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
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
