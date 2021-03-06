package com.yss.main.voucher;

import java.sql.*;
import java.text.DecimalFormat;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchTplBean
    extends BaseDataSettingBean implements IDataSetting {

    private String vchTplCode = "";
    private String vchTplName = "";

    private String srcCuryCode = "";
    private String srcCuryName = "";
    private String curyCode = "";
    private String curyName = "";
    private String linkCode = "";
    private String linkName = "";
    private String dsCode = "";
    private String dsName = "";
    private String attrCode = "";
    private String attrName = "";

    private String vchTWay = "";
    private String mode = "";

    private String dateFieldCode = "";
    private String dateFieldName = "";

    private String fields = "";
    private String desc = "";

    private String strPortCode = "";
    private String strPortName = "";

    private String oldFVchTplCode = "";

    private VchTplBean filterType = null;

    private String sRecycled = ""; // 回收站字段的处理 by leeyu 2008-10-21 BUG:0000491
    String[] allReqAry = null;
    String[] oneReqAry = null;
    private String sFormat = "0000000000"; // 357 QDV4赢时胜（深圳）2010年11月29日03_A by qiuxufeng 排序字段格式化字符串
//------------------------------------------------------------------------------
    public String getVchTplCode() {
        return vchTplCode;
    }

    public String getCuryName() {
        return curyName;
    }

    public String getDesc() {
        return desc;
    }

    public String getLinkName() {
        return linkName;
    }

    public VchTplBean getFilterType() {
        return filterType;
    }

    public String getDateFieldName() {
        return dateFieldName;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getDsCode() {
        return dsCode;
    }

    public String[] getOneReqAry() {
        return oneReqAry;
    }

    public String getSrcCuryCode() {
        return srcCuryCode;
    }

    public String getOldFVchTplCode() {
        return oldFVchTplCode;
    }

    public String getAttrCode() {
        return attrCode;
    }

    public String getFields() {
        return fields;
    }

    public String[] getAllReqAry() {
        return allReqAry;
    }

    public String getVchTWay() {
        return vchTWay;
    }

    public String getCuryCode() {
        return curyCode;
    }

    public String getMode() {
        return mode;
    }

    public String getVchTplName() {
        return vchTplName;
    }

    public String getDateFieldCode() {
        return dateFieldCode;
    }

    public String getLinkCode() {
        return linkCode;
    }

    public String getDsName() {
        return dsName;
    }

    public String getSrcCuryName() {
        return srcCuryName;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public String getStrPortName() {
        return strPortName;
    }

    public void setVchTplCode(String vchTplCode) {
        this.vchTplCode = vchTplCode;
    }

    public void setCuryName(String curyName) {
        this.curyName = curyName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public void setFilterType(VchTplBean filterType) {
        this.filterType = filterType;
    }

    public void setDateFieldName(String dateFieldName) {
        this.dateFieldName = dateFieldName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setDsCode(String dsCode) {
        this.dsCode = dsCode;
    }

    public void setOneReqAry(String[] oneReqAry) {
        this.oneReqAry = oneReqAry;
    }

    public void setSrcCuryCode(String srcCuryCode) {
        this.srcCuryCode = srcCuryCode;
    }

    public void setOldFVchTplCode(String oldFVchTplCode) {
        this.oldFVchTplCode = oldFVchTplCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public void setAllReqAry(String[] allReqAry) {
        this.allReqAry = allReqAry;
    }

    public void setVchTWay(String vchTWay) {
        this.vchTWay = vchTWay;
    }

    public void setCuryCode(String curyCode) {
        this.curyCode = curyCode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setVchTplName(String vchTplName) {
        this.vchTplName = vchTplName;
    }

    public void setDateFieldCode(String dateFieldCode) {
        this.dateFieldCode = dateFieldCode;
    }

    public void setLinkCode(String linkCode) {
        this.linkCode = linkCode;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public void setSrcCuryName(String srcCuryName) {
        this.srcCuryName = srcCuryName;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }

    public void setStrPortName(String strPortName) {
        this.strPortName = strPortName;
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
    	checkSortField(); // add by qiuxufeng 20110214 357 QDV4赢时胜（深圳）2010年11月29日03_A
        String strSql = "";
        //下面strsql语句改变因为凭证模板设置模块的原币描述栏无数据显示 BUG MS00156  2009.01.08 方浩
        strSql =
            " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCuryName as FCuryName ," +
            " e.FLinkName as FLinkName ,f.FVchDsName as FVchDsName ,p.FportName," +
            // " g.FAttrName as FAttrName ,h.FVocName as FModeValue ,i.FDesc as FDateFieldName ,j.FCuryName as FSrcCuryName from " +
            " g.FAttrName as FAttrName ,h.FVocName as FModeValue ,i.FDesc as FDateFieldName ,j.FDesc as FSrcCuryName from " + //把j.FCuryName改成了j.FDesc
            pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FCuryCode,FCuryName from " +
            pub.yssGetTableName("Tb_Para_Currency") +
            ")d on d.FCuryCode=a.FCuryCode" +
            " left join ( select distinct trim(to_char(fsetcode,'000'))  as flinkcode,fsetname as FLinkName from lsetlist ) " +
            " e on e.FLinkCode=a.FLinkCode" +//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//            " left join (select distinct FLinkCode,FLinkName from " +
//            pub.yssGetTableName("Tb_Vch_PortSetLink") +
//            ")e on e.FLinkCode=a.FLinkCode" +
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
            //   "left join (select FCuryCode,FCuryName from " +
            //    pub.yssGetTableName("Tb_Para_Currency") +
            //   ")j on j.FCuryCode=a.FSrcCury" +
            " left join (select FVchDsCode,FAliasName,FDesc from " + //把FCuryCode,FCuryName改成了FAliasName,FDesc
            pub.yssGetTableName("Tb_Vch_DsTabField") + //把Tb_Para_Currency改成了Tb_Vch_DsTabField
            ")j on j.FAliasName=a.FSrcCury and j.FVchDsCode=a.FDsCode" + //查条件主健关联
            " left join (select FPortCode,FPortName from " +
            pub.yssGetTableName("Tb_Para_PortFolio") +
            ") p on a.FPortCode=p.FPortCode " +
            buildFilterSql() +
            //" order by a.FCheckState, a.FCreateTime desc";
	        " order by a.FCheckState, a.FSort," + // 增加先通过手动排序字段排序 edit by qiuxufeng 20110216 357 QDV4赢时胜（深圳）2010年11月29日03_A
	        " a.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            if (this.filterType.vchTplCode.length() != 0) {
                sResult = sResult + " and a.FVchTplCode like '" +
                    filterType.vchTplCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.vchTplName.length() != 0) {
                sResult = sResult + " and a.FVchTplName like '" +
                    filterType.vchTplName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.linkCode.length() != 0) {
            	//edit by songjie 2012.07.11 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
                sResult = sResult + " and a.FLinkCode like '" +
                    filterType.linkCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc =" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.dateFieldCode.length() != 0) {
                sResult = sResult + " and a.FDateField like '" +
                    filterType.dateFieldCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strPortCode != null && filterType.strPortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.strPortCode.replaceAll("'", "''") + "%'";
            }
            //---add by songjie 2012.07.23 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
            if(this.filterType.attrCode != null && this.filterType.attrCode.length() != 0){
                sResult = sResult + " and a.FAttrCode like '" +
                filterType.attrCode.replaceAll("'", "''") + "%'";
            }
            //---add by songjie 2012.07.23 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
        }
        return sResult;
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql = "";
        String sHeader = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        VchDsTabFieldBean field = new VchDsTabFieldBean();
        String sShowDataStr = "";
        String sAllDataStr = "";
        try {
            this.getSetting();
            sHeader = "字段名\t字段描述";
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FVchDsCode = " + dbl.sqlString(dsCode) +
                " and FAliasName in (" + operSql.sqlCodes(this.fields) + ")";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                field.setAliasName(rs.getString("FAliasName"));
                field.setDesc(rs.getString("FDesc"));
                field.setDsVchDsCode(dsCode);
                bufShow.append(field.getAliasName()).append("\t");
                bufShow.append(field.getDesc());
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                bufAll.append(field.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
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
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        /**shashijie 2011.05.17,凭证交易方式后追加_zy表示专用,STORY #429 希望优化凭证模板的使用方案*/
        String fVchTWay = getFVchTWayValue(vchTWay,strPortCode);
        /**end*/
        String strSql = "";
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                "(FVchTplCode,FVchTplName,FCuryCode,FSrcCury,FLinkCode,FDsCode,FAttrCode," +
                " FVchTWay,FMode,FFields,FDateField,FPortCode," +
                " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser )" +
                " values(" + dbl.sqlString(this.vchTplCode) + "," +
                dbl.sqlString(this.vchTplName) + "," +

                dbl.sqlString(this.curyCode) + "," +
                dbl.sqlString(this.srcCuryCode) + "," +
                dbl.sqlString(this.linkCode) + "," +
                dbl.sqlString(this.dsCode) + "," +
                dbl.sqlString(this.attrCode) + "," +
                
                dbl.sqlString(fVchTWay) + "," +
                dbl.sqlString(this.mode) + "," +

                dbl.sqlString(this.fields) + "," +
                dbl.sqlString(this.dateFieldCode) + "," +
                dbl.sqlString(this.strPortCode) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("新增凭证模板信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
    }

    /**shashijie,2011-5-17,根据专用组合判断凭证交易方式是否追加"_zy"表示专用*/
    private String getFVchTWayValue(String vchTWay2, String strPortCode2) {
    	String vchTWay = vchTWay2;
		if (strPortCode2!=null && !strPortCode2.trim().equals("") 
				&& vchTWay2!=null && !vchTWay2.trim().equals("") ) {
			if (vchTWay2.indexOf("_zy")<0) {//若没有"_zy"则直接追加
				vchTWay = vchTWay2 + "_zy";
			} else {//有则判断是否是最后
				if (!"_zy".equals(vchTWay2.substring(vchTWay2.length()-3))) {
					vchTWay = vchTWay2 + "_zy";
				}
			}
		} 
		return vchTWay;
	}

	/**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Vch_VchTpl"),
                               "FVchTplCode", this.vchTplCode,
                               this.oldFVchTplCode);

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection con = dbl.loadConnection();
        //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		//VchEntityBean vchEntity = new VchEntityBean();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
            int iSort = Integer.parseInt(getMaxSort()) + 1;
            String tempSort = " ";
            // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
            //=======增加对回收站的处理功能  by leeyu 2008-10-21 BUG:0000491
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                changeChild(this.vchTplCode, this.checkStateId);
                tempSort = new DecimalFormat("0000000000").format(iSort); // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A
                strSql =
                    "update " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                    " set FCheckState = " + this.checkStateId +
                    // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
                    (this.checkStateId == 0 ? ", FSort = ' '" : (", FSort = " + dbl.sqlString(tempSort))) +
                    // add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                    "' where FVchTplCode = " + dbl.sqlString(this.vchTplCode);
                dbl.executeSql(strSql);
                iSort++;// add by qiuxufeng 20110215 357 QDV4赢时胜（深圳）2010年11月29日03_A 审核后排在审核的最后一个

                /*  strSql = "update " + pub.yssGetTableName("Tb_Vch_Entity") +  //上面changeChile()已经处理了 liyu 修改 1120
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                 ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                        "' where FVchTplCode = " + dbl.sqlString(this.vchTplCode) +
                        " and FCheckState <> 2";
                  dbl.executeSql(strSql);*/

            }
            //======2008-10-21
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("审核凭证模板信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FVchTplCode = " + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);
            //===========;delete 改为update by leeyu bug：000491
            //strSql = " delete from " + pub.yssGetTableName("Tb_Vch_Entity") +
            strSql = "update " + pub.yssGetTableName("Tb_Vch_Entity") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchTplCode=" + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);

            //strSql = " delete from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
            strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchTplCode=" + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);

            //strSql = " delete from " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
            strSql = "update " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchTplCode=" + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);

            //strSql = " delete from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
            strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchTplCode=" + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);

            //strSql = " delete from " + pub.yssGetTableName("Tb_Vch_EntityCond") +
            strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FVchTplCode=" + dbl.sqlString(this.vchTplCode);
            dbl.executeSql(strSql);
            //==================== 2008-10-27
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("删除凭证模板信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

    /**
     * 此方法用于处理当凭证模板更改时下的关联表的更改,可做修改与审核     add liyu 1120
     */
    private void changeChild(String sTplCode, int checkstateID) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int iCheck = 0;
        //修改说明：由于审核与还原用的是同一个方法，为了在审核时不将删除的凭证分录也审核了，因此用凭证模板的审核状态做为条件在
        //下面判断。若数据是从回收站还原的话，此时会将所有的分录都还原到未审核里 by leeyu 2008-10-27 BUG:000491
        try {
            strSql = "select FCheckState from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FVchTplCode=" + dbl.sqlString(sTplCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                iCheck = rs.getInt("FCheckState");
            }
            strSql = "select * from " + pub.yssGetTableName("tb_vch_Entity") +
                " where FVchTplCode=" + dbl.sqlString(sTplCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                    " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                    " , FEntityCode=" + dbl.sqlString(rs.getString("FEntityCode")) +
                    ",FCheckState=" + checkstateID + " where FVchTplCode=" +
                    dbl.sqlString(sTplCode) + " and FEntityCode=" +
                    dbl.sqlString(rs.getString("FEntityCode")) +
                    (iCheck == 2 ? "" :
                     " and FCheckState<>2"); //BugNo:0000252 edit by jc
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                    " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                    " , FEntityCode=" + dbl.sqlString(rs.getString("FEntityCode")) +
                    ",FCheckState=" + checkstateID + " where FVchTplCode=" +
                    dbl.sqlString(sTplCode) + " and FEntityCode=" +
                    dbl.sqlString(rs.getString("FEntityCode")) +
                    (iCheck == 2 ? "" :
                     " and FCheckState<>2"); //BugNo:0000252 edit by jc
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                    " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                    " , FEntityCode=" + dbl.sqlString(rs.getString("FEntityCode")) +
                    ",FCheckState=" + checkstateID + " where FVchTplCode=" +
                    dbl.sqlString(sTplCode) + " and FEntityCode=" +
                    dbl.sqlString(rs.getString("FEntityCode")) +
                    (iCheck == 2 ? "" :
                     " and FCheckState<>2"); //BugNo:0000252 edit by jc
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                    " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                    " , FEntityCode=" + dbl.sqlString(rs.getString("FEntityCode")) +
                    ",FCheckState=" + checkstateID + " where FVchTplCode=" +
                    dbl.sqlString(sTplCode) + " and FEntityCode=" +
                    dbl.sqlString(rs.getString("FEntityCode")) +
                    (iCheck == 2 ? "" :
                     " and FCheckState<>2"); //BugNo:0000252 edit by jc
                dbl.executeSql(strSql);
                strSql = "update " + pub.yssGetTableName("Tb_Vch_Assistant") +
                    " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                    " , FEntityCode=" + dbl.sqlString(rs.getString("FEntityCode")) +
                    ",FCheckState=" + checkstateID + " where FVchTplCode=" +
                    dbl.sqlString(sTplCode) + " and FEntityCode=" +
                    dbl.sqlString(rs.getString("FEntityCode")) +
                    (iCheck == 2 ? "" :
                     " and FCheckState<>2"); //BugNo:0000252 edit by jc
                dbl.executeSql(strSql);
            }
            strSql = "update " + pub.yssGetTableName("tb_vch_Entity") +
                " set FVchTplCode=" + dbl.sqlString(this.vchTplCode) +
                ",FcheckState=" + checkstateID +
                " where FVchTplCode=" + dbl.sqlString(sTplCode) +
                (iCheck == 2 ? "" :
                 " and FCheckState<>2"); //BugNo:0000252 edit by jc
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("处理关联表时出错" + e.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        /**shashijie 2011.05.17,凭证交易方式后追加_zy表示专用,STORY #429 希望优化凭证模板的使用方案*/
        String fVchTWay = getFVchTWayValue(vchTWay,strPortCode);
        /**end*/
        //ResultSet rs = null;//shashijie 2011.05.17没用到所以注视
        try {
            con.setAutoCommit(false);
            bTrans = true;
            changeChild(this.oldFVchTplCode, (pub.getSysCheckState() ? 0 : 1));
            strSql = "update " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " set FVchTplCode = " + dbl.sqlString(this.vchTplCode) +
                ",FVchTplName=" + dbl.sqlString(this.vchTplName) +
                ",FCuryCode=" + dbl.sqlString(this.curyCode) +
                ",FSrcCury=" + dbl.sqlString(this.srcCuryCode) +
                ",FLinkCode=" + dbl.sqlString(this.linkCode) +
                ",FDateField=" + dbl.sqlString(this.dateFieldCode) +
                ",FDsCode=" + dbl.sqlString(this.dsCode) +
                ",FAttrCode=" + dbl.sqlString(this.attrCode) +
                ",FVchTWay=" + dbl.sqlString(fVchTWay) +
                ",FMode=" + dbl.sqlString(this.mode) +
                ",FFields=" + dbl.sqlString(this.fields) +
                ",FPortCode=" + dbl.sqlString(this.strPortCode) +
                ",FDesc=" + dbl.sqlString(this.desc) + ",FCheckstate= " +
                (pub.getSysCheckState() ? "0" : "1") + ",FCreator = " +
                dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FVchTplCode = " + dbl.sqlString(this.oldFVchTplCode);
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("更改凭证模板信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
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
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FVchTplCode = " + dbl.sqlString(this.vchTplCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.vchTplName = rs.getString("FVchTplName");
                this.fields = rs.getString("FFields") + "";
                this.vchTWay = rs.getString("FVchTWay");
                this.linkCode = rs.getString("FLinkCode");
                this.dsCode = rs.getString("FDsCode");
                this.mode = rs.getString("FMode");
                this.strPortCode = rs.getString("FPortCode");
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sVocStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setVchTpl(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
//            sAllDataStr = bufAll.toString().subsring(0, bufAll.toString().length() - 2);
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_MODE);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取凭证模板信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setVchTpl(ResultSet rs) throws SQLException {
        this.vchTplCode = rs.getString("FVchTplCode") + "";
        this.vchTplName = rs.getString("FVchTplName") + "";

        this.curyCode = rs.getString("FCuryCode") + "";
        this.curyName = rs.getString("FCuryName") + "";
        this.linkCode = rs.getString("FLinkCode") + "";
        this.linkName = rs.getString("FLinkName") + "";
        this.dsCode = rs.getString("FDsCode") + "";
        this.dsName = rs.getString("FVchDsName") + "";
        this.attrCode = rs.getString("FAttrCode") + "";
        this.attrName = rs.getString("FAttrName") + "";

        this.srcCuryCode = rs.getString("FSrcCury");
        this.srcCuryName = rs.getString("FSrcCuryName");

        this.dateFieldCode = rs.getString("FDateField");
        this.dateFieldName = rs.getString("FDateFieldName");
        this.vchTWay = rs.getString("FVchTWay") + "";
        this.mode = rs.getString("FMode") + "";

        //   this.dateFieldCode = rs.getString("FDateField");

        this.fields = rs.getString("FFields") + "";
        this.desc = rs.getString("FDesc") + "";
        this.strPortCode = rs.getString("FPortCode");
        this.strPortName = rs.getString("FPortName");

        super.setRecLog(rs);

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.vchTplCode).append("\t");
        buf.append(this.vchTplName).append("\t");

        buf.append(this.curyCode).append("\t");
        buf.append(this.curyName).append("\t");
        buf.append(this.linkCode).append("\t");
        buf.append(this.linkName).append("\t");
        buf.append(this.dsCode).append("\t");
        buf.append(this.dsName).append("\t");
        buf.append(this.attrCode).append("\t");
        buf.append(this.attrName).append("\t");

        buf.append(this.vchTWay).append("\t");
        buf.append(this.mode).append("\t");

        buf.append(this.fields).append("\t");

        buf.append(this.desc).append("\t");

        buf.append(this.dateFieldCode).append("\t");
        buf.append(this.dateFieldName).append("\t");
        buf.append(this.srcCuryCode).append("\t");
        buf.append(this.srcCuryName).append("\t");
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");

        buf.append(super.buildRecLog());

        return buf.toString();

    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        try {
            if (sType != null && sType.equalsIgnoreCase("copy")) {
                //复制的操作，因为凭证模板的复制较特别，因此将在这里处理 by leeyu BUG: 0000390
                String sOldTplCode = "";
                int iCheckState = 0;
                iCheckState = (pub.getSysCheckState() ? 0 : 1);
                sOldTplCode = this.oldFVchTplCode;
                oldFVchTplCode = "";
                this.checkInput(YssCons.OP_ADD);
                copyTplData(sOldTplCode, iCheckState);
                addSetting();
                return this.getListViewData1();
            } else if (sType != null && sType.equalsIgnoreCase("itemmove")) {
            	return saveItemSort();
            }
        } catch (Exception ex) {
            throw new YssException(ex.toString());
        }
        return "";
    }

    private void copyTplData(String oldVchTplCode, int iCheckState) throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sEntity = "";
        String sResume = "";
        String sSubject = "";
        String sMa = "";
        String sCond = "";
        String sAssistant = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sEntity = "insert into " + pub.yssGetTableName("tb_vch_Entity") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FENTITYNAME,FDCWAY,FCALCWAY,FPRICEFIELD, " +
                " FENCURYCODE,FRESUMEDESC,FSUBJECTCODE,FMONEYDESC,FAMOUNTDESC, " +
                " FSETMONEYDESC,FCONDDESC,FASSISTANTDESC,FENTITYIND,FDESC,FCHECKSTATE, " +
                " FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FENTITYNAME,FDCWAY,FCALCWAY,FPRICEFIELD, " +
                " FENCURYCODE,FRESUMEDESC,FSUBJECTCODE,FMONEYDESC,FAMOUNTDESC, " +
                " FSETMONEYDESC,FCONDDESC,FASSISTANTDESC,FENTITYIND,FDESC," + iCheckState + ", " +
                " FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME from " + pub.yssGetTableName("tb_vch_Entity") + " " +
                " where FCheckState<>2 and FVchTplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sEntity);
            sResume = "insert into " + pub.yssGetTableName("tb_vch_entityresume") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FORDERNUM,FVALUETYPE,FRESUMECONENT,FRESUMEFIELD, " +
                " FRESUMEDICT,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) " +
                " select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FORDERNUM,FVALUETYPE,FRESUMECONENT,FRESUMEFIELD, " +
                " FRESUMEDICT,FDESC," + iCheckState + ",FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME " +
                " from " + pub.yssGetTableName("tb_vch_entityresume") + " where FCheckState<>2 and FVchtplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sResume);
            sSubject = "insert into " + pub.yssGetTableName("tb_vch_entitysubject") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FORDERNUM,FVALUETYPE,FSUBJECTCONENT, " +
                " FSUBJECTFIELD,FSUBJECTDICT,FDESC,FCHECKSTATE, " +
                " FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FORDERNUM,FVALUETYPE,FSUBJECTCONENT, " +
                " FSUBJECTFIELD,FSUBJECTDICT,FDESC," + iCheckState + ", " +
                " FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME from " +
                " " + pub.yssGetTableName("tb_vch_entitysubject") + " where FCheckState<>2 and FVchTplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sSubject);
            sMa = "insert into " + pub.yssGetTableName("tb_vch_entityma") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FORDERNUM,FTYPE,FMACONENT,FVALUETYPE,FMAFIELD, " +
                " FMADICT,FOPERSIGN,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) " +
                " select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FORDERNUM,FTYPE,FMACONENT,FVALUETYPE,FMAFIELD, " +
                " FMADICT,FOPERSIGN,FDESC," + iCheckState + ",FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME " +
                " from " + pub.yssGetTableName("tb_vch_entityma") + " where FCheckState<>2 and FVchTplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sMa);
            sCond = "insert into " + pub.yssGetTableName("tb_vch_entitycond") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FORDERINDEX,FCONRELA,FFIELDNAME,FSIGN,FVALUESOURCE, " +
                " FVALUE,FDESC,FCREATOR,FCREATETIME,FCHECKSTATE,FCHECKUSER,FCHECKTIME) select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FORDERINDEX,FCONRELA,FFIELDNAME,FSIGN,FVALUESOURCE, " +
                " FVALUE,FDESC,FCREATOR,FCREATETIME," + iCheckState + ",FCHECKUSER,FCHECKTIME " +
                " from " + pub.yssGetTableName("tb_vch_entitycond") + " where FCheckState<>2 and FVchTplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sCond);
            sAssistant = "insert into " + pub.yssGetTableName("tb_vch_assistant") + " " +
                " (FVCHTPLCODE,FENTITYCODE,FORDERNUM,FVALUETYPE,FASSISTANTCONENT,FASSISTANTFIELD," +
                " FASSISTANTDICT,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) select " +
                dbl.sqlString(vchTplCode) + ",FENTITYCODE,FORDERNUM,FVALUETYPE,FASSISTANTCONENT,FASSISTANTFIELD," +
                " FASSISTANTDICT,FDESC," + iCheckState + ",FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME " +
                " from " + pub.yssGetTableName("tb_vch_assistant") + " where FCheckState<>2 and FVchTplCode=" + dbl.sqlString(oldVchTplCode);
            dbl.executeSql(sAssistant);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            try {
            	if(conn != null) //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            		conn.rollback();
            } catch (SQLException sqle) {
                throw new YssException("复制凭证模板新增数据出错！");
            }
            throw new YssException("复制凭证模板数据出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
            if (sRowStr.indexOf("\r\f") >= 0) {
                allReqAry = sRowStr.split("\r\f");
                this.vchTplCode = allReqAry[0].split("\t")[0];
                this.vchTplName = allReqAry[0].split("\t")[1];
            } else {
                sTmpStr = sRowStr;
                sRecycled = sTmpStr; //增加对回收站的处理功能 by leeyu 2008-10-21 BUG:0000491
                reqAry = sTmpStr.split("\t");
                this.vchTplCode = reqAry[0];
                this.vchTplName = reqAry[1];

                this.curyCode = reqAry[2]; //修改 liyu 1015
                if (reqAry[2].trim().length() == 0) { // add liyu 1015
                    this.curyCode = " ";
                }
                this.linkCode = reqAry[3];
                this.dsCode = reqAry[4];
                this.attrCode = reqAry[5];

                this.vchTWay = reqAry[6];
                this.mode = reqAry[7];

                this.fields = reqAry[8];
                this.desc = reqAry[9];

                this.checkStateId = Integer.parseInt(reqAry[10]);
                this.oldFVchTplCode = reqAry[11];
                this.dateFieldCode = reqAry[12];
                this.srcCuryCode = reqAry[13];
                this.strPortCode = reqAry[14];
                this.strPortName = reqAry[15];

                super.parseRecLog();
                if (sRowStr.indexOf("\r\t") >= 0) {
                    if (this.filterType == null) {
                        this.filterType = new VchTplBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }

            }
        } catch (Exception e) {
            throw new YssException("解析凭证模板信息出错!");
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
     * deleteRecycleData 完善回收站的处理功能 ，by leeyu 2008-10-21 BUG:0000491
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(bTrans);
            bTrans = true;
            String[] arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityCond") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_Assistant") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("tb_vch_Entity") +
                    " where FVchTplCode=" + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
                strSql = "delete " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                    " where FVchTplCode = " + dbl.sqlString(vchTplCode);
                dbl.executeSql(strSql);
            }
            con.commit();
            con.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除凭证模板信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
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
     * 357 QDV4赢时胜（深圳）2010年11月29日03_A add by qiuxufeng 20110216
     * 检查凭证模板已审核记录中的手动排序字段是否被设置排序
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
    				pub.yssGetTableName("Tb_Vch_VchTpl") +
    				" set FSort = ? where FVchTplCode = ?";
    		pst = dbl.openPreparedStatement(strSql);
    		iSort = Integer.parseInt(getMaxSort()) + 1;
    		// 查找已审核记录中排序字段未设置的记录
			strSql = "select FVchTplCode, FSort from " +
					pub.yssGetTableName("Tb_Vch_VchTpl") +
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
					pst.setString(2, rs.getString("FVchTplCode"));
					pst.execute();
					iSort++;
				}
			}
		} catch (Exception e) {
			throw new YssException("检查凭证模板排序出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
    }
    
    /**
     * 357 QDV4赢时胜（深圳）2010年11月29日03_A add by qiuxufeng 20110216
     * 获取凭证模板已审核排序的最大值
     * @方法名：getMaxSort
     * @返回类型：String
     */
    private String getMaxSort() throws YssException {
    	String strSql = "";
    	ResultSet rs = null;
    	String strSort = sFormat;
    	try {
			// 查询凭证模板已审核手动排序的最大值
			strSql = "select * from " +
					pub.yssGetTableName("Tb_Vch_VchTpl") +
					" where FCheckState = 1 and FSort <> ' '" +
					" order by FSort Desc";
			rs = dbl.openResultSet(strSql);
			if(rs.next()) {
				strSort = rs.getString("FSort");
			}
			dbl.closeResultSetFinal(rs);
		} catch (Exception e) {
			throw new YssException("查询凭证模板排序最大值出错！", e);
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
    	String[] tplCodeAry = null;
    	String[] tplSortAry = null;
    	ResultSet rs = null;
    	PreparedStatement pst = null;
    	Connection conn = null;
        boolean bTrans = false;
    	
    	try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
			tplCodeAry = new String[2];
			tplSortAry = new String[2];
			strSql = "select FVchTplCode, FSort from " +
					pub.yssGetTableName("Tb_Vch_VchTpl") +
					" where FVchTplCode in (" + operSql.sqlCodes(this.vchTplCode) + ")";
			rs = dbl.openResultSet(strSql);
			for(int i = 0; i < 2; i++) {
				rs.next();
				tplCodeAry[i] = rs.getString("FVchTplCode");
				tplSortAry[i] = rs.getString("FSort");
			}
			dbl.closeResultSetFinal(rs);
			// 交换排序值
			String temp = tplSortAry[0];
			tplSortAry[0] = tplSortAry[1];
			tplSortAry[1] = temp;
			strSql = "update " +
					pub.yssGetTableName("Tb_Vch_VchTpl") +
					" set FSort = ? where FVchTplCode = ?";
			pst = dbl.openPreparedStatement(strSql);
			// 更新排序
			for(int i = 0; i < 2; i++) {
				pst.setString(1, tplSortAry[i]);
				pst.setString(2, tplCodeAry[i]);
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
			throw new YssException("保存凭证模板手动排序出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
		return reStr;
    }
}
