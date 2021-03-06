package com.yss.main.funsetting;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.*;

public class VocabularyBean
    extends BaseDataSettingBean implements IDataSetting {

    private String vocTypeCode = ""; //词汇关联代码
    private String vocCode = ""; //词汇代码
    private String vocName = ""; //词汇名称
    private String desc = ""; //词汇描述

    private String oldVocTypeCode = "";
    private String oldVocCode = "";

    //added by liubo.Stroy #1916
    //===============================
    private String sAssetGroupCode = "";	//组合群代码
    private String sAssetGroupName = "";	//组合群名称

    
    public String getsAssetGroupName() {
		return sAssetGroupName;
	}

	public void setsAssetGroupName(String sAssetGroupName) {
		this.sAssetGroupName = sAssetGroupName;
	}

	public String getsAssetGroupCode() {
		return sAssetGroupCode;
	}

	public void setsAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}
    //===============================

	private VocabularyBean filterType;
    public String getDesc() {
        return desc;
    }

    public VocabularyBean getFilterType() {
        return filterType;
    }

    public String getOldVocCode() {
        return oldVocCode;
    }

    public String getVocName() {
        return vocName;
    }

    public String getVocTypeCode() {
        return vocTypeCode;
    }

    public String getOldVocTypeCode() {
        return oldVocTypeCode;
    }

    public void setVocCode(String vocCode) {
        this.vocCode = vocCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setFilterType(VocabularyBean filterType) {
        this.filterType = filterType;
    }

    public void setOldVocCode(String oldVocCode) {
        this.oldVocCode = oldVocCode;
    }

    public void setVocName(String vocName) {
        this.vocName = vocName;
    }

    public void setVocTypeCode(String vocTypeCode) {
        this.vocTypeCode = vocTypeCode;
    }

    public void setOldVocTypeCode(String oldVocTypeCode) {
        this.oldVocTypeCode = oldVocTypeCode;
    }

    public String getVocCode() {
        return vocCode;
    }

    public VocabularyBean() {
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
     * parseRowStr
     * 解析常用词汇关联设置请求
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");

            this.vocCode = reqAry[0];
            this.vocName = reqAry[1];
            this.vocTypeCode = reqAry[2];
            //----edit by songjie 2011.02.28 判断数组长度，以防报数组越界错误----//
            if(reqAry != null && reqAry.length > 3){
            	this.desc = reqAry[3];
            }
            this.checkStateId = 0;
            if(reqAry != null && reqAry.length > 4){
            	this.oldVocTypeCode = reqAry[5];
            }
            if(reqAry != null && reqAry.length > 5){
            	this.oldVocCode = reqAry[6];
            }
            //----edit by songjie 2011.02.28 判断数组长度，以防报数组越界错误----//

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new VocabularyBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析常用词汇关联设置请求请求出错", e);
        }
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.vocCode).append("\t");
        buf.append(this.vocName).append("\t");
        buf.append(this.vocTypeCode).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = " where 1=1 ";
        if (this.filterType != null) {
            if (this.filterType.vocTypeCode.length() != 0) { // wdy add 20070901  添加表别名：a
                sResult = sResult + " and a.FVocTypeCode =" +
                    dbl.sqlString(this.vocTypeCode);
            }
            if (this.filterType.vocCode.length() != 0) {
                sResult = sResult + " and a.FVocCode =" +
                    dbl.sqlString(this.vocCode);
            }
            if (this.filterType.vocName.length() != 0) {
                sResult = sResult + " and a.FVocName =" +
                    dbl.sqlString(this.vocName);
            }
            if (this.filterType.desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
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

    public String getVoc(String vocType) throws YssException {
    	//----delete by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
//        int i = 0;
//        String reqAry[] = null;
    	//----delete by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
        String strSql = "";
        ResultSet rs = null;
        String result = "";
        StringBuffer buf = new StringBuffer();
        boolean bExist = false;
        try {
        	bExist = dbFun.existsTabColumn_Ora("TB_FUN_VOCABULARY", "FORDERNUM");//MS01287  QDV4赢时胜上海2010年05月31日01_B panjunfang add 20100610
            
        	//----add by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
        	int j = 0;
        	ArrayList alVoc = new ArrayList();
        	strSql = " select FVocCode,FVocName,FVocTypeCode,FDesc from Tb_Fun_Vocabulary where FVocTypeCode in (" + operSql.sqlCodes(vocType) + 
        	") and FCheckState=1 order by fvoctypecode,fvoccode" + (bExist ? "" : ",FOrderNum");
        	rs = dbl.openResultSet(strSql);
        	while (rs.next()) {
                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                if(!alVoc.contains(rs.getString("FVocTypeCode"))){ 	
                	alVoc.add(rs.getString("FVocTypeCode"));
                	if(j != 0){
                        if (buf.toString().length() > 1) {
                            buf = new StringBuffer(buf.toString().substring(0,
                                buf.toString().length() - 1));
                            buf.append(YssCons.YSS_LINESPLITMARK);
                        }
                	}
                }
                j++;
                buf.append(this.buildRowStr()).append(YssCons.YSS_ITEMSPLITMARK2);
        	}
            if (buf.toString().length() > 1) {
                buf = new StringBuffer(buf.toString().substring(0,
                    buf.toString().length() - 1));
                buf.append(YssCons.YSS_LINESPLITMARK);
            }
        	//----add by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
            //----delete by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
//        	reqAry = vocType.split(",");
//            for (i = 0; i < reqAry.length; i++) {
//                strSql = "select * from Tb_Fun_Vocabulary where FVocTypeCode = '" +
//                    //reqAry[i] + "' order by FVocCode";
//                    reqAry[i] + "' and FCheckState=1 order by fvoctypecode,fvoccode" + (bExist ? "" : ",FOrderNum"); // by leeyu alter 080725 0000342  ////MS01287  QDV4赢时胜上海2010年05月31日01_B panjunfang modify 20100610
//                rs = dbl.openResultSet(strSql);
//                while (rs.next()) {
//                    this.vocCode = rs.getString("FVocCode") + "";
//                    this.vocName = rs.getString("FVocName") + "";
//                    this.vocTypeCode = rs.getString("FVocTypeCode") + "";
//                    this.desc = rs.getString("FDesc") + "";
//                    buf.append(this.buildRowStr()).append(YssCons.YSS_ITEMSPLITMARK2);
//                }
//                if (buf.toString().length() > 1) {
//                    buf = new StringBuffer(buf.toString().substring(0,
//                        buf.toString().length() - 1));
//                    buf.append(YssCons.YSS_LINESPLITMARK);
//                }
//                dbl.closeResultSetFinal(rs);
//            }
            //----delete by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A----//
            if (buf.toString().length() > 2) {
                result = buf.toString().substring(0,
                                                  buf.toString().length() - 2);
            }  
            dbl.closeResultSetFinal(rs);//add by songjie 2011.04.01 需求 127 QDV4赢时胜(开发部)2010年09月09日01_A
        } catch (Exception e) {
            throw new YssException("获取词汇对照信息出错", e);
        }
        return result;
    }
    
    /************************************************
     * MS01715 QDV4汇添富2010年09月08日01_A
     * “估值检查”中添加一项“检查当日交易数据是否全部结算” 
     *  add by jiangshichao
     * @return
     * @throws YssException
     */
    public String getCheckVoc() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        CtlPubPara pubPara = null;
        String sCheckPara = "";
        try {
        	pubPara = new CtlPubPara();
        	pubPara.setYssPub(pub);
        	/*********************************
        	 *  如果在通用参数设置了“是(0,0)”，在“资产估值”界面打开时默认选项“检查当日交易数据是否全部结算”显示并被被勾选
             *  如果在通用参数设置了“否(1,1)”，在“资产估值”界面打开时默认选项“检查当日交易数据是否全部结算”不显示出来
        	 */
        	sCheckPara = pubPara.getRateCalculateType("TradeSettle","ComboBox1",1);;
        	
            // sHeader = this.getListView1Headers();
            sHeader = "词汇代码\t词汇名称";
            String sql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FVocTypeCode = " +
                dbl.sqlString(this.filterType.vocTypeCode) +
                ("1,1".equalsIgnoreCase(sCheckPara)?" and fvoccode<>'checksettlement' ":"")+
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FVocCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocTypeCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FDesc") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                //2008.04.15 添加 蒋锦 CheckStateID
                this.checkStateId = rs.getInt("FCheckState");
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

            return temp;

            //      return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * getListViewData1
     * 获取常用词汇信息
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        CtlPubPara pubPara = null;
        String sCheckPara = "";
        try {
        	
            // sHeader = this.getListView1Headers();
            sHeader = "词汇代码\t词汇名称";
            String sql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FVocTypeCode = " +
                dbl.sqlString(this.filterType.vocTypeCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FVocCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocName") + "").trim());
                buf.append("\t");
                /**modify by liuxiaojun 20130805  stroy 4094    界面上ta划款指令添加函数呈现的界面*/
//                buf.append( (rs.getString("FVocTypeCode") + "").trim());
//                buf.append("\t");
                /**end by liuxiaojun 20130805  stroy 4094  */
                buf.append( (rs.getString("FDesc") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                //2008.04.15 添加 蒋锦 CheckStateID
                this.checkStateId = rs.getInt("FCheckState");
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            return temp;

            //      return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * getListViewData5
     * 获取常用词汇信息
     * @return String
     */
    public String getListViewData5() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        CtlPubPara pubPara = null;
        String sCheckPara = "";
        try {
        	
            // sHeader = this.getListView1Headers();
            sHeader = "关键字代码\t关键字名称\t注释";
            String sql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FVocTypeCode = " +
                dbl.sqlString(this.filterType.vocTypeCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FVocCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocName") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocTypeCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FDesc") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                //2008.04.15 添加 蒋锦 CheckStateID
                this.checkStateId = rs.getInt("FCheckState");
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

            return temp;

            //      return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = "词汇代码\t词汇名称";
            String sql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FVocTypeCode = " +
                dbl.sqlString(this.filterType.vocTypeCode) +
                /**modify by liuxiaojun 20130731  stroy 4094             ---start*/
//                " order by a.FCheckState, a.FCreateTime desc";
                " order by  a.fvoccode asc";
            	/**modify by liuxiaojun 20130731  stroy 4094             ---end*/
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FVocCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocName") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

            return temp;

            //      return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getBookLinkName
     * 获取现金台帐定义的列名信息
     * @return String
     */
    public String getSqlBookLinkFilter(String strBookLink) throws YssException {
        String bookLinkAry[] = null;
        String reStr = "";
        if (strBookLink.length() == 0) {
            reStr = "''";
            return reStr;
        }
        bookLinkAry = strBookLink.split(";");
        for (int i = 0; i < bookLinkAry.length; i++) {
            reStr += dbl.sqlString(bookLinkAry[i]);
            if (i < bookLinkAry.length - 1) {
                reStr += ",";
            }
        }
        return reStr;
    }

    /**
     * getListViewData3
     * 获取指定范围内常用词汇信息
     * @return String
     */
    public String getListViewData3() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String strVocCodeAry[] = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        HashMap hmBookLink = new HashMap();
        ResultSet rs = null;
        try {
            sHeader = this.getListView3Headers();
            strVocCodeAry = this.vocCode.split(";");
            strSql = "select * from Tb_Fun_Vocabulary " +
                " where FVocTypeCode = " + dbl.sqlString(this.vocTypeCode);

            if (this.vocCode.indexOf("'") < 0) {
                this.vocCode = getSqlBookLinkFilter(this.vocCode);
            }
            if (this.vocCode.length() > 0) {
                strSql += " and FVocCode in (" + this.vocCode + ")" +
                    " order by FVocCode";
            }

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView3ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                bufAll.append(bufShow.toString()).append(this.buildRowStr());
                hmBookLink.put(this.vocCode, bufAll.toString());
                bufShow.setLength(0);
                bufAll.setLength(0);
            }

            for (int i = 0; i < strVocCodeAry.length; i++) {
                sAllDataStr = (String) hmBookLink.get(strVocCodeAry[i]);
                if (sAllDataStr != null &&
                    sAllDataStr.indexOf(YssCons.YSS_LINESPLITMARK) >= 0) {
                    bufShow.append(sAllDataStr.split(YssCons.YSS_LINESPLITMARK)[0])
                        .append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(sAllDataStr.split(YssCons.YSS_LINESPLITMARK)[1])
                        .append(YssCons.YSS_LINESPLITMARK);
                }
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + this.getListView3ShowCols();
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData4
     * 显示所有 by liyu
     * @return String
     */
    public String getListViewData4() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer buf = new StringBuffer(); //用于显示的属性
        StringBuffer buf1 = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = "词汇代码\t词汇名称";
            String sql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where FCheckState=1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(sql);
            while (rs.next()) {

                buf.append( (rs.getString("FVocCode") + "").trim());
                buf.append("\t");
                buf.append( (rs.getString("FVocName") + "").trim());
                buf.append(YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
                buf1.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (buf.toString().length() > 2) {
                sShowDataStr = buf.toString().substring(0,
                    buf.toString().length() - 2);
            }

            if (buf1.toString().length() > 2) {
                sAllDataStr = buf1.toString().substring(0,
                    buf1.toString().length() - 2);
            }

            String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

            return temp;
        } catch (Exception e) {
            throw new YssException("获取常用词汇信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * getTreeViewData1
     *
     * @return String
     */
    //modified by liubo.Story #1916
    //跨组合群的调度方案设置
    //==========================================
    public String getTreeViewData1() throws YssException{
    	 String sHeader = "";
         String sShowDataStr = "";
         String sAllDataStr = "";
         StringBuffer buf = new StringBuffer(); //用于显示的属性
         StringBuffer buf1 = new StringBuffer(); //所有的属性
         ResultSet rs = null;
         CtlPubPara pubPara = null;
         String sCheckPara = "";
         try {
         	
             // sHeader = this.getListView1Headers();
             sHeader = "词汇代码\t词汇名称\t组合群代码";
             
             String[] sAllAssetGroup = getAllAssetGroup().split("\t");

             String sql = "select * from (";
             for (int i = 0; i < sAllAssetGroup.length; i++)
             {
	             sql = sql + 
	                 " select a.*,'" + sAllAssetGroup[i] + "' as FAssetGroupCode,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
	                 "Tb_Fun_Vocabulary" + " a" +
	                 " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
	                 " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
	                 " where a.FVocTypeCode = " +
	                 dbl.sqlString(this.filterType.vocTypeCode) +
	                 " union";
             }

             sql = sql.substring(0,sql.length() - 5);
             sql = sql + ") allData order by allData.FAssetGroupCode,allData.FCreateTime";
         	
             rs = dbl.openResultSet(sql);
             while (rs.next()) {

                 buf.append( (rs.getString("FVocCode") + "").trim());
                 buf.append("\t");
                 buf.append( (rs.getString("FVocName") + "").trim());
                 buf.append("\t");
                 buf.append( (rs.getString("FAssetGroupCode") + "").trim());
                 buf.append("\t");
                 buf.append( (rs.getString("FVocTypeCode") + "").trim());
                 buf.append("\t");
                 buf.append( (rs.getString("FDesc") + "").trim());
                 buf.append("\t");
                 buf.append( (rs.getString("FAssetGroupCode") + "").trim());
                 buf.append(YssCons.YSS_LINESPLITMARK);

                 this.vocCode = rs.getString("FVocCode") + "";
                 this.vocName = rs.getString("FVocName") + "";
                 this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                 this.desc = rs.getString("FDesc") + "";
                 this.sAssetGroupCode = rs.getString("FAssetGroupCode");
                 //2008.04.15 添加 蒋锦 CheckStateID
                 this.checkStateId = rs.getInt("FCheckState");
                 buf1.append(this.buildRowStr()).append(YssCons.
                     YSS_LINESPLITMARK);
             }
             if (buf.toString().length() > 2) {
                 sShowDataStr = buf.toString().substring(0,
                     buf.toString().length() - 2);
             }

             if (buf1.toString().length() > 2) {
                 sAllDataStr = buf1.toString().substring(0,
                     buf1.toString().length() - 2);
             }

             String temp = sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

             return temp;

             //      return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
         } catch (Exception e) {
             throw new YssException("获取常用词汇信息出错", e);
         } finally {
             dbl.closeResultSetFinal(rs);
         }
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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr, String statu) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        String strSql = "";
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowAry[0]);

            strSql = "delete from " + "Tb_Fun_Vocabulary" +
                " where FVocTypeCode = " +
                dbl.sqlString(this.vocTypeCode);
            dbl.executeSql(strSql);
            // ---------lzp add 11.29

            if (statu.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-常用词汇");
                sysdata.setStrCode(this.vocCode);
                sysdata.setStrName(this.vocName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            strSql =
                "insert into " + "Tb_Fun_Vocabulary" +
                " (FVocCode,FVocName,FVocTypeCode," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values (?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                if (i > 0) {
                    this.parseRowStr(sMutilRowAry[i]);
                }
                if (this.vocCode.length() > 0) {
                    pstmt.setString(1, this.vocCode);
                    pstmt.setString(2, this.vocName);
                    pstmt.setString(3, this.vocTypeCode);
                    pstmt.setString(4, this.desc);
                    pstmt.setInt(5, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(6, this.creatorCode);
                    pstmt.setString(7, this.creatorTime);
                    pstmt.setString(8,
                                    (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                    // ---------lzp add 11.29
                    if (statu.equalsIgnoreCase("1")) {
                        com.yss.main.funsetting.SysDataBean sysdata = new com.yss.
                            main.funsetting.SysDataBean();
                        String sql = "insert into " + "Tb_Fun_Vocabulary" +
                            " (FVocCode,FVocName,FVocTypeCode," +
                            "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) values(" +
                            dbl.sqlString(this.vocCode) + "," + dbl.sqlString(this.vocName) + "," + dbl.sqlString(this.vocTypeCode) + "," + dbl.sqlString(this.desc) + "," + (pub.getSysCheckState() ? 0 : 1) + "," +
                            dbl.sqlString(this.creatorCode) + "," + dbl.sqlString(this.creatorTime) + "," + (pub.getSysCheckState() ? "' '" : this.creatorCode) + ")";

                        sysdata.setYssPub(pub);
                        sysdata.setStrAssetGroupCode("Common");
                        sysdata.setStrFunName("新增-常用词汇");
                        sysdata.setStrCode(this.vocCode);
                        sysdata.setStrName(this.vocName);
                        sysdata.setStrUpdateSql(sql);
                        sysdata.setStrCreator(pub.getUserName());
                        sysdata.addSetting();
                    }
                    //-----------------------


                }
            }
            return "";
        } catch (Exception e) {
            throw new YssException("保存联系人信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            
        }
    }

    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("getVoc")) {
            return "voc" + this.getVoc(this.vocTypeCode);
        } else if (sType.equalsIgnoreCase("getTemp")) {
            return this.getTemp("rep_customtemp");
        //--- MS01715 QDV4汇添富2010年09月08日01_A “估值检查”中添加一项“检查当日交易数据是否全部结算” add by jiangshichao
        } else if(sType.equalsIgnoreCase("getcheckVoc")){
        	return getCheckVoc();
        }
        //--- MS01715 QDV4汇添富2010年09月08日01_A “估值检查”中添加一项“检查当日交易数据是否全部结算” end ----------------
        return this.buildRowStr();
    }

    public String getTemp(String sType) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = "临时表代码\t临时表名称";
            strSql = "select * from Tb_Fun_Vocabulary where FVocTypeCode = '" +
                sType + "' order by FVocCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FVocCode") + "").trim()).append(
                    "\t");
                bufShow.append( (rs.getString("FVocName") + "").trim()).append(
                    YssCons.YSS_LINESPLITMARK);

                this.vocCode = rs.getString("FVocCode") + "";
                this.vocName = rs.getString("FVocName") + "";
                this.vocTypeCode = rs.getString("FVocTypeCode") + "";
                this.desc = rs.getString("FDesc") + "";
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取自定义报表的临时表出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        VocabularyBean befEditBean = new VocabularyBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.*,b.fusername as fcreatorname,c.fusername as fcheckusername from " +
                "Tb_Fun_Vocabulary" + " a" +
                " left join (select fusercode,fusername from tb_sys_userlist) b on a.fcreator = b.fusercode" +
                " left join (select fusercode,fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode" +
                " where a.FVocCode = " +
                dbl.sqlString(this.oldVocCode) +
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.vocCode = rs.getString("FVocCode") + "";
                befEditBean.vocName = rs.getString("FVocName") + "";
                befEditBean.vocTypeCode = rs.getString("FVocTypeCode") + "";
                befEditBean.desc = rs.getString("FDesc") + "";
            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
    
    /**
	 * 20111205 modified by liubo.Story #1916
	 * 查询当前库中所有组合群
	 * return ResultSet
	 * @throws YssException 
	 */
	public String getAllAssetGroup() throws YssException{
		ResultSet rs=null;
		String sql=null;
		String FAssetGroupCode="";
		try{
			sql="select * from Tb_Sys_AssetGroup where FAssetGroupCode = '" + pub.getAssetGroupCode() + "' order by FAssetGroupCode ";
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				FAssetGroupCode+=rs.getString("FAssetGroupCode")+"\t";
			}
			return FAssetGroupCode;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
