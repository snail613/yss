package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class IndexFuturesBean
    extends BaseDataSettingBean implements IDataSetting {
    private String securityCode = "";
    private String securityName = "";
    private String indexCode = "";
    private String indexName = "";

    private String depDurCode = "";
    private String depDurName = "";

    private double multiple; //放大倍数
    private String bailType = ""; //保证金类型
    private String fuType = ""; //保证金类型
    private String bailTypeName = "";
    private String fuTypeName = "";
    private double bailScale; //保证金比例
    private double bailFix; //每手固定保证金
    private double beginBail; //初始保证金

    private String desc = "";
    private String oldSecurityCode = "";
    
    private String sRecycled = "";
    private IndexFuturesBean filterType;
    // add by yangheng MS01439 QDV4博时2010年7月14日02_A
    private String subCategoryCode="";//子品种代码
    private String subCategoryName="";//子品种名称
    //----------
    //add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A 
    private String isOnlyColumn = "";//是否只显示列表，不显示数据
    public String getDesc() {
        return desc;
    }

    public double getBailFix() {
        return bailFix;
    }

    public String getBailType() {
        return bailType;
    }

//lzp add 11.23
    public String getFuType() {
        return fuType;
    }

//-----
    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public IndexFuturesBean getFilterType() {
        return filterType;
    }

    public String getDepDurName() {
        return depDurName;
    }

    public double getBeginBail() {
        return beginBail;
    }

    public double getBailScale() {
        return bailScale;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getDepDurCode() {
        return depDurCode;
    }

    public double getMultiple() {
        return multiple;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setBailFix(double bailFix) {
        this.bailFix = bailFix;
    }

    public void setBailType(String bailType) {
        this.bailType = bailType;
    }

//-----lzp add11.23
    public void setFuType(String fuType) {
        this.fuType = fuType;
    }

//----------
    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setFilterType(IndexFuturesBean filterType) {
        this.filterType = filterType;
    }

    public void setDepDurName(String depDurName) {
        this.depDurName = depDurName;
    }

    public void setBeginBail(double beginBail) {
        this.beginBail = beginBail;
    }

    public void setBailScale(double bailScale) {
        this.bailScale = bailScale;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public void setDepDurCode(String depDurCode) {
        this.depDurCode = depDurCode;
    }

    public void setMultiple(double multiple) {
        this.multiple = multiple;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setBailTypeName(String bailTypeName) {
        this.bailTypeName = bailTypeName;
    }

    public void setFuTypeName(String fuTypeName) {
        this.fuTypeName = fuTypeName;
    }

    public String getIndexCode() {
        return indexCode;
    }

    public String getBailTypeName() {
        return bailTypeName;
    }

    public String getFuTypeName() {
        return fuTypeName;
    }

    public IndexFuturesBean() {
    }
    
 // add by yangheng MS01439 QDV4博时2010年7月14日02_A
    public String getSubCategoryCode() {
		return subCategoryCode;
	}

	public void setSubCategoryCode(String subCategoryCode) {
		this.subCategoryCode = subCategoryCode;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
//------------

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.securityCode = reqAry[0];
            this.indexCode = reqAry[1];
            this.depDurCode = reqAry[2];
            this.multiple = YssFun.toDouble(reqAry[3]);
            this.bailType = reqAry[4];
            this.fuType = reqAry[5]; //lzp  11.23
            this.bailScale = YssFun.toDouble(reqAry[6]);
            this.bailFix = YssFun.toDouble(reqAry[7]);
            this.beginBail = YssFun.toDouble(reqAry[8]);
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B start---//
            if(reqAry[9].indexOf("【Enter】") != -1){
            	this.desc = reqAry[9].replaceAll("【Enter】", "\r\n");
            }else{
            	this.desc = reqAry[9];
            }
            //---edit by songjie 2011.08.17 BUG 2355 QDV4赢时胜(测试)2011年8月2日05_B end---//
            this.checkStateId = YssFun.toInt(reqAry[10]);

            this.oldSecurityCode = reqAry[11];
         // add by yangheng MS01439 QDV4博时2010年7月14日02_A
            this.subCategoryCode=reqAry[12];
         //----------
            //add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A 
            this.isOnlyColumn = reqAry[13];

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new IndexFuturesBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析股指期货信息设置出错", e);
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.securityCode);
        buf.append("\t");
        buf.append(this.securityName);
        buf.append("\t");
        buf.append(this.indexCode);
        buf.append("\t");
        buf.append(this.indexName).append("\t");
        buf.append(this.depDurCode);
        buf.append("\t");
        buf.append(this.depDurName);
        buf.append("\t");
        buf.append(this.multiple);
        buf.append("\t");
        buf.append(this.bailType).append("\t");
        buf.append(this.fuType).append("\t"); //lzp add 11.23
        buf.append(this.bailTypeName).append("\t");
        buf.append(this.fuTypeName).append("\t"); //edit by jc
        buf.append(this.bailScale).append("\t");
        buf.append(this.bailFix).append("\t");
        buf.append(this.beginBail);
        buf.append("\t");
        buf.append(this.desc);
        buf.append("\t");
     // add by yangheng MS01439 QDV4博时2010年7月14日02_A
        buf.append(this.subCategoryCode).append("\t");
        buf.append(this.subCategoryName).append("\t");
      //------------
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Para_IndexFutures"),
                               "FSecurityCode",
                               this.securityCode,
                               this.oldSecurityCode);

    }

    public String getAllSetting() {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        try {
            if (this.filterType != null) {
                sResult = " where 1=1 ";
                //---add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A---//
    			if (this.isOnlyColumn.equals("1")) {
    				sResult = sResult + " and 1=2 ";
    				return sResult;
    			}
    			//---add by songjie 2011.05.11 需求 859 QDV4赢时胜（深圳）2011年03月30日01_A---//
                if (this.filterType.securityCode.length() != 0) {
                    sResult = sResult + " and a.FSecurityCode like '" +
                        filterType.securityCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.depDurCode.length() != 0) {
                    sResult = sResult + " and a.FDepDurCode like '" +
                        filterType.depDurCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.indexCode.length() != 0) {
                    sResult = sResult + " and a.FIndexCode like '" +
                        filterType.indexCode.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.desc.length() != 0) {
                    sResult = sResult + " and a.FDesc like '" +
                        filterType.desc.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.multiple > 0) {
                    sResult = sResult + " and a.FMultiple=" +
                        filterType.multiple;
                }
                if (this.filterType.bailType.length() != 0 && !this.filterType.bailType.equalsIgnoreCase("99")) {
                    sResult = sResult + " and a.FBailType like '" +
                        filterType.bailType.replaceAll("'", "''") + "%'";
                }

                if (this.filterType.fuType.length() != 0 && !this.filterType.fuType.equalsIgnoreCase("99")) {
                    sResult = sResult + " and a.FFUType like '" +
                        filterType.fuType.replaceAll("'", "''") + "%'";
                }
             // add by yangheng MS01439 QDV4博时2010年7月14日02_A
                if(this.filterType.subCategoryCode.length()!=0)
                	sResult=sResult+"and a.FSubCatCode ='"+
                		filterType.subCategoryCode+"'";
             //------------
                if (this.filterType.bailScale > 0) {
                    sResult = sResult + " and a.FBailScale=" +
                        filterType.bailScale;
                }
                if (this.filterType.bailFix > 0) {
                    sResult = sResult + " and a.FBailFix=" +
                        filterType.bailFix;
                }
                if (this.filterType.beginBail > 0) {
                    sResult = sResult + " and a.FBeginBail=" +
                        filterType.beginBail;
                }
            }
        } catch (Exception e) {
            throw new YssException("筛选股指期货信息设置数据出错", e);
        }
        return sResult;
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_BailType + "," + YssCons.YSS_FUN_FUType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取股指期货信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月25号
     * 修改人：单亮
     * 原方法的功能：查询出费用连接数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            " (select FSecurityCode,FCheckState from " +
            pub.yssGetTableName("Tb_Para_IndexFutures") +
            //修改前的代码
            //" where FCheckState <> 2 group by FSecurityCode,FCheckState) x join" +
            //修改后的代码
            //----------------------------begin
            " group by FSecurityCode,FCheckState) x join" +
            //----------------------------end
            " (select a.*, " +
            " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
            " e.FIndexName as FIndexName,f.FDepDurName as FDepDurName,l.FVocName as FBailTypeName,K.FVocName as FFUTypeName,m.FSubCatName as FSubCatName" + // add FSubCatName by fangjiang 2010.09.06
            " from " + pub.yssGetTableName("Tb_Para_IndexFutures") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") d  on a.FSecurityCode = d.FSecurityCode " +
            " left join (select FIndexCode,FIndexName from " +
            pub.yssGetTableName("Tb_Para_Index") +
            " where FCheckState = 1) e on e.FIndexCode = a.FIndexCode " +
            " left join (select FDepDurCode,FDepDurName from " +
            pub.yssGetTableName("Tb_Para_DepositDuration") +
            " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
            " left join Tb_Fun_Vocabulary l on a.FBailType = l.FVocCode and l.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FUN_BailType) +
            " left join Tb_Fun_Vocabulary k on a.FFUType = k.FVocCode and K.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_FUN_FUType) +
            " left join (select FSubCatCode, FSubCatName from Tb_base_subcategory where FCheckState = 1) m on a.FSubCatCode = m.FSubCatCode " + // add by fangjiang 2010.09.06 MS01439
            buildFilterSql() +
            ") y on x.FSecurityCode = y.FSecurityCode " +
            " order by y.FCheckState, y.FCreateTime";
        return this.builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        String strSql = "";
        return strSql;
    }

//by leeyu add 080907
    public String getListViewData2() throws YssException {
        String strSql = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "证券代码\t证券名称\t指数代码\t指数名称\t期限代码\t期限名称\t放大倍数;R\t保证金类型\t保证金类型名称\t期货类型\t期货类型名称\t保证金比例;R";
            strSql = "select y.* from " +
                " (select FSecurityCode,FCheckState from " +
                pub.yssGetTableName("Tb_Para_IndexFutures") +
                " group by FSecurityCode,FCheckState) x join" +
                " (select a.*, " +
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName," +
                " e.FIndexName as FIndexName,f.FDepDurName as FDepDurName,l.FVocName as FBailTypeName,K.FVocName as FFUTypeName, m.FSubCatName as FSubCatName" + // add FSubCatName by fangjiang 2010.09.06
                " from " + pub.yssGetTableName("Tb_Para_IndexFutures") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") d  on a.FSecurityCode = d.FSecurityCode " +
                " left join (select FIndexCode,FIndexName from " +
                pub.yssGetTableName("Tb_Para_Index") +
                " where FCheckState = 1) e on e.FIndexCode = a.FIndexCode " +
                " left join (select FDepDurCode,FDepDurName from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
                " left join Tb_Fun_Vocabulary l on a.FBailType = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_BailType) +
                " left join Tb_Fun_Vocabulary k on a.FFUType = k.FVocCode and K.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_FUType) +
                " left join (select FSubCatCode, FSubCatName from Tb_base_subcategory where FCheckState = 1) m on a.FSubCatCode = m.FSubCatCode " + // add by fangjiang 2010.09.06 MS01439
                //buildFilterSql() +
                " where 1=1 and a.FCheckState=1" +
                ") y on x.FSecurityCode = y.FSecurityCode " +
                " order by y.FCheckState, y.FCreateTime";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FSecurityCode")).append("\t");
                bufShow.append(rs.getString("FSecurityName")).append("\t");
                bufShow.append(rs.getString("FIndexCode")).append("\t");
                bufShow.append(rs.getString("FIndexName")).append("\t");
                bufShow.append(rs.getString("FDepDurCode")).append("\t");
                bufShow.append(rs.getString("FDepDurName")).append("\t");
                bufShow.append(rs.getDouble("FMultiple")).append("\t");
                bufShow.append(rs.getString("FBailType")).append("\t");
                bufShow.append(rs.getString("FBailTypeName")).append("\t");
                bufShow.append(rs.getString("FFUType")).append("\t");
                bufShow.append(rs.getString("FFUTypeName")).append("\t");
                bufShow.append(rs.getDouble("FBailScale")).
                    append(YssCons.YSS_LINESPLITMARK);
                setSecurityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() -
                    2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();

            //return this.builderListViewData(strSql);
        } catch (Exception ex) {
            throw new YssException("获取股指期货信息设置出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        String strSql = "";
        return "";
    }

    /**
     * getSetting
     * modify by fangjiang 2010.09.06
     * @return IParaSetting
     */
    public IDataSetting getSetting() { //alter by sunny 2007-10-21
        String strSql = "";
        ResultSet rs = null;
        try {
            //添加查询出指数名称、期限名称、保证金类型名称和期货名称 edit by jc 2008-09-25
            strSql = "select a.*,e.FIndexName as FindexName,f.FDepDurName as FDepDurName,l.FVocName as FBailTypeName,k.FVocName as FFUTypeName, m.FSubCatName as FSubCatName from " +
                pub.yssGetTableName("Tb_Para_IndexFutures") + " a " +
                " left join (select FIndexCode,FIndexName from " +
                pub.yssGetTableName("Tb_Para_Index") +
                " where FCheckState = 1) e on e.FIndexCode = a.FIndexCode " +
                " left join (select FDepDurCode,FDepDurName from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
                " left join Tb_Fun_Vocabulary l on a.FBailType = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_BailType) +
                " left join Tb_Fun_Vocabulary k on a.FFUType = k.FVocCode and K.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_FUType) +
                " left join (select FSubCatCode, FSubCatName from Tb_base_subcategory where FCheckState = 1) m on a.FSubCatCode = m.FSubCatCode " + // add by fangjiang 2010.09.06 MS01439
                " where FSecurityCode = " +
                dbl.sqlString(this.securityCode);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.securityCode = rs.getString("FSecurityCode");

                this.indexCode = rs.getString("FIndexCode");
                this.indexName = rs.getString("FIndexName"); //edit by jc
                this.depDurCode = rs.getString("FDepDurCode");
                this.depDurName = rs.getString("FDepDurName"); //edit by jc
                this.multiple = rs.getDouble("FMultiple");
                this.bailType = rs.getString("FBailType");
                this.bailTypeName = rs.getString("FBailTypeName"); //edit by jc
                this.fuType = rs.getString("FFUType"); //LZP 11.23 ADD
                this.fuTypeName = rs.getString("FFUTypeName"); //edit by jc
                this.bailScale = rs.getDouble("FBailScale");
                this.bailFix = rs.getDouble("FBailFix");
                this.beginBail = rs.getDouble("FBeginBail");

                this.desc = rs.getString("FDesc");
                this.checkStateId = rs.getInt("FCheckState");
                this.checkStateName = YssFun.getCheckStateName(rs.getInt(
                    "FCheckState"));
                this.creatorCode = rs.getString("FCreator");
                this.creatorTime = rs.getString("FcreateTime");
                this.checkUserCode = rs.getString("FCheckUser");
                this.checkTime = rs.getString("FCheckTime");
                // add by fangjiang 2010.08.20
                this.subCategoryCode = rs.getString("FSubCatCode");
                this.subCategoryName = rs.getString("FSubCatName");
                //-----------------------
            }
        } catch (Exception e) {
            throw new YssException("获取股指期货信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            return null;
        }

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
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                "(FSECURITYCODE, FIndexCode,FDepDurCode, FMultiple, FBailType,FFUType, FBailScale,FBailFix,FBeginBail," +
                "FDesc," 
                // add by yangheng MS01439 QDV4博时2010年7月14日02_A
                +"FSubCatCode,"+
                //---------
                " FCHECKSTATE, FCREATOR, FCREATETIME,FCheckUser) values(" +
                dbl.sqlString(this.securityCode) + "," +
                dbl.sqlString(this.indexCode) + "," +
                dbl.sqlString(this.depDurCode) + "," +
                this.multiple + "," +
                dbl.sqlString(this.bailType) + "," +
                dbl.sqlString(this.fuType) + "," + //lzp11.23
                this.bailScale + "," +
                this.bailFix + "," +
                this.beginBail + "," +
                dbl.sqlString(this.desc) + "," +
                // add by yangheng MS01439 QDV4博时2010年7月14日02_A
                dbl.sqlString(this.subCategoryCode) + "," +
                //------
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }

        catch (Exception e) {
            throw new YssException("增加股指期货信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                " set " +
                "  FSECURITYCODE = " + dbl.sqlString(this.securityCode) +
                ", FIndexCode = " + dbl.sqlString(this.indexCode) +
                ", FDepDurCode = " + dbl.sqlString(this.depDurCode) +
                ", FMultiple = " + this.multiple +
                ", FBailType = " + dbl.sqlString(this.bailType) +
                ", FFUType = " + dbl.sqlString(this.fuType) + //lzp 11.23
                ", FBailScale = " + this.bailScale +
                ", FBailFix = " + this.bailFix +
                ", FBeginBail = " + this.beginBail +
                ", FDesc = " + dbl.sqlString(this.desc) +
                // add by yangheng MS01439 QDV4博时2010年7月14日02_A
                ", FSubCatCode = " + dbl.sqlString(this.subCategoryCode) +
                //----------------
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                " where FSECURITYCODE = " +
                dbl.sqlString(this.oldSecurityCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改股指期货信息设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * 删除数据，即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        int Count = 0;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSECURITYCODE = " +
                dbl.sqlString(this.oldSecurityCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**修改时间：2008年3月25号
     *  修改人：单亮
     *  原方法功能：只能处理期间指数信息连接的审核和未审核的单条信息。
     *  新方法功能：可以处理期间指数信息连接审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     *  修改后不影响原方法的功能
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        //修改前的代码
//   String strSql = "";
//   boolean bTrans = false; //代表是否开始了事务
//   Connection conn = dbl.loadConnection();
//   try {
//      strSql = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
//              " set FCheckState = " + this.checkStateId +
//              ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//              ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//              "' where FSECURITYCODE = " +
//              dbl.sqlString(this.oldSecurityCode);
//        conn.setAutoCommit(false);
//        bTrans = true;
//        dbl.executeSql(strSql);
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//           throw new YssException("审核债券信息出错", e);
//      }
//        finally {
//           dbl.endTransFinal(conn, bTrans);
//        }
        //修改后的的马
        //-------------begin
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode);
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (oldSecurityCode != null && !oldSecurityCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode);
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核债券信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        //-------------------endd
    }

    // modify by fangjiang 2010.09.06
    public void setSecurityAttr(ResultSet rs) throws SQLException {
        this.securityCode = rs.getString("FSecurityCode");
        this.securityName = rs.getString("FSecurityName");
        this.indexCode = (rs.getString("FIndexCode") == null ? "" : rs.getString("FIndexCode"));
        this.indexName = (rs.getString("FIndexName") == null ? "" : rs.getString("FIndexName"));
        this.depDurCode = rs.getString("FDepDurCode");
        this.depDurName = rs.getString("FDepDurName");
        this.multiple = rs.getDouble("FMultiple");
        this.bailType = rs.getString("FBailType");
        this.fuType = rs.getString("FFUType");
        this.bailTypeName = rs.getString("FBailTypeName");
        this.fuTypeName = rs.getString("FFUTypeName"); //edit by jc
        this.bailScale = rs.getDouble("FBailScale");
        this.bailFix = rs.getDouble("FBailFix");
        this.beginBail = rs.getDouble("FBeginBail");
        if (rs.getString("FDesc") != null) {
            this.desc = rs.getString("FDesc");
        } else {
            this.desc = "";
        }
        // add by fangjiang 2010.08.20
        this.subCategoryCode=rs.getString("FSubCatCode");
        this.subCategoryName=rs.getString("FSubCatName");
        //----------------
        super.setRecLog(rs);
    }

    public String getOperValue(String sType) {
        if (sType.equalsIgnoreCase("setting")) { //彭鹏 2008.2.4 修改 根据证券代码查询股指期货
            getSetting();
        }
        return buildRowStr();
    }

    public String getBeforeEditData() throws YssException {
    	/**shashijie 2012-5-29 BUG 4668 */
    	String value = this.getSetting(this.oldSecurityCode);
        return value;
        /**end*/
    }

    /**shashijie 2012-6-7 BUG 4668 始终new出新对象,返回拼接字符串 */
	private String getSetting(String securityCode) throws YssException {
		IndexFuturesBean ifb = new IndexFuturesBean();
    	ifb.setYssPub(pub);
    	String value = "";
		String strSql = "";
        ResultSet rs = null;
        try {
            //添加查询出指数名称、期限名称、保证金类型名称和期货名称 edit by jc 2008-09-25
            strSql = "select a.*,e.FIndexName as FindexName,f.FDepDurName as FDepDurName," +
        		" l.FVocName as FBailTypeName,k.FVocName as FFUTypeName, m.FSubCatName as FSubCatName " +
        		" ,d.FSecurityName "+
        		" From " +
                pub.yssGetTableName("Tb_Para_IndexFutures") + " a " +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") d  on a.FSecurityCode = d.FSecurityCode " +
                " left join (select FIndexCode,FIndexName from " +
                pub.yssGetTableName("Tb_Para_Index") +
                " where FCheckState = 1) e on e.FIndexCode = a.FIndexCode " +
                " left join (select FDepDurCode,FDepDurName from " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " where FCheckState = 1) f on f.FDepDurCode = a.FDepDurCode " +
                " left join Tb_Fun_Vocabulary l on a.FBailType = l.FVocCode and l.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_BailType) +
                " left join Tb_Fun_Vocabulary k on a.FFUType = k.FVocCode and K.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FUN_FUType) +
                " left join (select FSubCatCode, FSubCatName from Tb_base_subcategory where FCheckState = 1) m " +
                " on a.FSubCatCode = m.FSubCatCode " +
                " where a.FSecurityCode = " +
                dbl.sqlString(securityCode);

            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
            	//设置对象副职
            	setSecurityAttrNew(rs,ifb);
            }
            value = ifb.buildRowStr();
        } catch (Exception e) {
            throw new YssException("获取股指期货信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return value;
	}
	

	/**shashijie 2012-6-7 BUG 4668 */
	private void setSecurityAttrNew(ResultSet rs, IndexFuturesBean ifb) throws Exception {
		ifb.securityCode = rs.getString("FSecurityCode");
        ifb.securityName = rs.getString("FSecurityName");
        ifb.indexCode = (rs.getString("FIndexCode") == null ? "" : rs.getString("FIndexCode"));
        ifb.indexName = (rs.getString("FIndexName") == null ? "" : rs.getString("FIndexName"));
        ifb.depDurCode = rs.getString("FDepDurCode");
        ifb.depDurName = rs.getString("FDepDurName");
        ifb.multiple = rs.getDouble("FMultiple");
        ifb.bailType = rs.getString("FBailType");
        ifb.fuType = rs.getString("FFUType");
        ifb.bailTypeName = rs.getString("FBailTypeName");
        ifb.fuTypeName = rs.getString("FFUTypeName"); //edit by jc
        ifb.bailScale = rs.getDouble("FBailScale");
        ifb.bailFix = rs.getDouble("FBailFix");
        ifb.beginBail = rs.getDouble("FBeginBail");
        if (rs.getString("FDesc") != null) {
            ifb.desc = rs.getString("FDesc");
        } else {
            ifb.desc = "";
        }
        ifb.subCategoryCode=rs.getString("FSubCatCode");
        ifb.subCategoryName=rs.getString("FSubCatName");
	}

	/**
     * 从回收站删除数据，即从数据库彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equals("")) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Para_IndexFutures") +
                        " where FSECURITYCODE = " +
                        dbl.sqlString(this.oldSecurityCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (oldSecurityCode != null && !oldSecurityCode.equals("")) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Para_IndexFutures") +
                    " where FSECURITYCODE = " +
                    dbl.sqlString(this.oldSecurityCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

}
