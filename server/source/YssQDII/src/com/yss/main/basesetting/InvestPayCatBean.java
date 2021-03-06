package com.yss.main.basesetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 *
 * <p>Title:TradeTypeBean </p>
 * <p>Description: 运营收支品种类型设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class InvestPayCatBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String FIVPayCatCode = ""; //运营收支品种代码
    private String FIVPayCatName = ""; //运营收支品种名称

    private String sAssetGroupName = "";	//added by liubo.Story #1916.组合群名称
    private String sAssetGroupCode = "";	//added by liubo.Story #1916.组合群代码

    //add by huangqirong 2012-04-13 story #2326
    private String tradeUseCode = "";
    private String tradeUseName = "";
    
    public String getTradeUseCode() {
		return tradeUseCode;
	}

	public void setTradeUseCode(String tradeUseCode) {
		this.tradeUseCode = tradeUseCode;
	}

	public String getTradeUseName() {
		return tradeUseName;
	}

	public void setTradeUseName(String tradeUseName) {
		this.tradeUseName = tradeUseName;
	}
	//---end---
	
    
    public String getsAssetGroupCode() {
		return sAssetGroupCode;
	}

	public void setsAssetGroupCode(String sAssetGroupCode) {
		this.sAssetGroupCode = sAssetGroupCode;
	}

	public String getsAssetGroupName() {
		return sAssetGroupName;
	}

	public void setsAssetGroupName(String sAssetGroupName) {
		this.sAssetGroupName = sAssetGroupName;
	}

	//------ MS00017 国内预提待摊 QDV4.1赢时胜（上海）2009年4月20日17_A
    private int FPayType = 99;      //资产类型  0-资产；1-负债 99默认为所有记录
    private String FIVType = "";    //运营品种类型 accruedFee-预提  deferredFee-待摊  managetrusteeFee-两费
    //------ End MS00017 modify by wangzuochun 2009.06.23 ----------
    
    /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
    private String FFeeType = "";
    /**end shashijie 2013-01-28 STORY 3513*/
    
    private String FDesc = ""; //描述
    private String FCreatorName = "";
    private String FCheckUserName = "";
    private String OldFIVPayCatCode = "";
    private String status = ""; //是否记入系统信息状态  lzp 11.30 add
    private InvestPayCatBean FilterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00149 QDV4南方2009年1月5日05_B 2009.01.14 方浩
    public InvestPayCatBean() {
    }

    /**
     * 修改  ：增加运营品种类型FFeeType列和词汇的处理
     * BugNO : MS00017 国内预提待摊 QDV4.1赢时胜（上海）2009年4月20日17_A
     * author：wangzuochun 2009.06.23 MS00017
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        int filerSqllen = 0;
        try {
            sHeader = this.getListView1Headers();
            filerSqllen = buildFilterSql().length();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName , " +
                /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
                " d.FVocName as FPayTypeValue,fiv.FVocName as FFeeTypeName " +
                " , fiv2.FVocName as FFeeType2" +
                /**end shashijie 2013-01-28 STORY 3513 */
                " from Tb_Base_InvestPayCat a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FPayType"，否则在使用DB2数据库时会报数据类型错误
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FPayType") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_ACCOUNT_SUBJECT) +
                " left join Tb_Fun_Vocabulary fiv on a.FIVType = fiv.FVocCode and fiv.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIV_FEETYPE) +
                //add by huangqirong 2012-04-13 story #2326
                " left join Tb_Fun_Vocabulary gcs on a.FIVType = gcs.FVocCode and gcs.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_TA_TradeUsage_GCS) +
                //---end---
                
                /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
                " left join Tb_Fun_Vocabulary fiv2 on a.Ffeetype = fiv2.FVocCode and fiv2.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FOP_FEETYPE) +
                /**end shashijie 2013-01-28 STORY 3513 */
                
                buildFilterSql() +
                "  order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).append(YssCons.
                    YSS_LINESPLITMARK);
                this.FIVPayCatCode = rs.getString("FIVPayCatCode") + "";
                this.FIVPayCatName = rs.getString("FIVPayCatName") + "";
                this.FPayType = rs.getInt("FPayType");
                this.FIVType = rs.getString("FIVType");
                this.FDesc = rs.getString("FDesc") + "";
                this.tradeUseCode =rs.getString("FTradeUsageCode") + "";//add by huangqirong 2012-04-13 story #2326
                this.tradeUseName =rs.getString("FTradeUsageName") + "";//add by huangqirong 2012-04-13 story #2326
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
				this.FFeeType = rs.getString("FFeeType") + "";
				/**end shashijie 2013-1-28 STORY 3513*/
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ACCOUNT_SUBJECT + "," +
            		//modify by huangqirong 2012-04-13 story #2326
                    YssCons.YSS_FIV_FEETYPE + ","+YssCons.YSS_TA_TradeUsage_GCS
                    /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
                    + ","+YssCons.YSS_FOP_FEETYPE
    				/**end shashijie 2013-1-28 STORY 3513*/
            		);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取运营收支品种类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "运营收支品种代码\t运营收支品种名称";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
                "  from Tb_Base_InvestPayCat a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                buildFilterSql() +
                " where a.FCheckState = 1 order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIVPayCatCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FIVPayCatName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);
                this.FIVPayCatCode = rs.getString("FIVPayCatCode") + "";
                this.FIVPayCatName = rs.getString("FIVPayCatName") + "";
                this.FIVType = rs.getString("FIVType") + "";
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
				this.FFeeType = rs.getString("FFeeType") + "";
				/**end shashijie 2013-1-28 STORY 3513*/
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            throw new YssException("获取运营收支品种类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    
    /**
     * 修改  ：只要两费运营类数据
     * baopingping #story 1183 20110718
     * @return String
     * @throws YssException
     */
    
    public String getListViewData5() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        String strSql = "";
        try {
            sHeader = "运营收支品种代码\t运营收支品种名称";
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
                "  from Tb_Base_InvestPayCat a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                buildFilterSql() +
                " where a.FCheckState = 1 and FIVTYpe = 'managetrusteeFee' order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FIVPayCatCode") + "").trim()).
                    append("\t");
                bufShow.append( (rs.getString("FIVPayCatName") + "").trim()).
                    append(YssCons.YSS_LINESPLITMARK);
                this.FIVPayCatCode = rs.getString("FIVPayCatCode") + "";
                this.FIVPayCatName = rs.getString("FIVPayCatName") + "";
                this.FIVType = rs.getString("FIVType") + "";
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
				this.FFeeType = rs.getString("FFeeType") + "";
				/**end shashijie 2013-1-28 STORY 3513*/
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
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
            throw new YssException("获取运营收支品种类型数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
//------------end-------------
    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    //modified by liubo.Story #1916
    //跨组合群的调度方案设置
    //==========================================
    public String getTreeViewData1() throws YssException{
    	String sHeader = "";
        String sShowDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        String sAllDataStr = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        int filerSqllen = 0;
        try {
            sHeader = this.getListView1Headers() + "\t组合群代码";
            filerSqllen = buildFilterSql().length();
            
            String[] sAllAssetGroup = getAllAssetGroup().split("\t");

    		strSql = "select * from (";
    		
            for (int i = 0; i < sAllAssetGroup.length; i++)
            {
	            strSql = strSql +
	                " select a.*,'" + sAllAssetGroup[i] + "' as FAssetGroupCode,b.FUserName as FCreatorName,c.FUserName as FCheckUserName , " +
	                /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
	                " d.FVocName as FPayTypeValue,fiv.FVocName as FFeeTypeName " +
	                " ,fiv2.FVocName as FFeeType2 " +
	                /**end shashijie 2013-01-28 STORY 3513 */
	                " from Tb_Base_InvestPayCat a " +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	                //2007.11.30 修改 蒋锦 使用dbl.sqlToChar()处理"a.FPayType"，否则在使用DB2数据库时会报数据类型错误
	                " left join Tb_Fun_Vocabulary d on " +
	                dbl.sqlToChar("a.FPayType") +
	                " = d.FVocCode and d.FVocTypeCode = " +
	                dbl.sqlString(YssCons.YSS_ACCOUNT_SUBJECT) +
	                " left join Tb_Fun_Vocabulary fiv on a.FIVType = fiv.FVocCode and fiv.FVocTypeCode = " +
	                dbl.sqlString(YssCons.YSS_FIV_FEETYPE) +
	                /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
	                " left join Tb_Fun_Vocabulary fiv2 on a.Ffeetype = fiv2.FVocCode and fiv2.FVocTypeCode = " +
	                dbl.sqlString(YssCons.YSS_FOP_FEETYPE) +
	                /**end shashijie 2013-01-28 STORY 3513 */
	                buildFilterSql() +
	                " union";
            }
            
    		strSql = strSql.substring(0,strSql.length() - 5);
        	strSql = strSql + ") allData order by allData.FAssetGroupCode,allData.FCreateTime";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).append("\t" + rs.getString("FAssetGroupCode")).append(YssCons.
                    YSS_LINESPLITMARK);
                this.FIVPayCatCode = rs.getString("FIVPayCatCode") + "";
                this.FIVPayCatName = rs.getString("FIVPayCatName") + "";
                this.FPayType = rs.getInt("FPayType");
                this.FIVType = rs.getString("FIVType");
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
				this.FFeeType = rs.getString("FFeeType") + "";
				/**end shashijie 2013-1-28 STORY 3513*/
                this.FDesc = rs.getString("FDesc") + "";
                this.sAssetGroupCode = rs.getString("FAssetGroupCode");                
                super.setRecLog(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_ACCOUNT_SUBJECT + "," +
                                        YssCons.YSS_FIV_FEETYPE
                                        /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
                                        + ","+YssCons.YSS_FOP_FEETYPE
                        				/**end shashijie 2013-1-28 STORY 3513*/
                                        );
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取运营收支品种类型出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getTreeViewData2() {
        return "";
    }

    public String getTreeViewData3() {
        return "";
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FIVPayCatCode.trim()).append("\t");
        buf.append(this.FIVPayCatName.trim()).append("\t");
        buf.append(this.FPayType).append("\t");
        buf.append(this.FIVType).append("\t");
        buf.append(this.FDesc.trim()).append("\t");
        buf.append(this.sAssetGroupCode).append("\t");//add by huangqirong 2012-04-13 story #2326 之前 Story #1916影响
        buf.append(this.sAssetGroupName).append("\t");//add by huangqirong 2012-04-13 story #2326之前 Story #1916影响
        buf.append(this.tradeUseCode).append("\t");	//add by huangqirong 2012-04-13 story #2326
        buf.append(this.tradeUseName).append("\t");	//add by huangqirong 2012-04-13 story #2326
        /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
        buf.append(this.FFeeType).append("\t");
		/**end shashijie 2013-1-28 STORY 3513*/
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     * 解析运营收支品种类型数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
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
            sRecycled = sRowStr; //bug MS00149 QDV4南方2009年1月5日05_B 2009.01.13 方浩
            reqAry = sRowStr.split("\t");
            this.FIVPayCatCode = reqAry[0];
            this.FIVPayCatName = reqAry[1];
            this.FPayType = YssFun.toInt(reqAry[2]);
            this.FIVType = reqAry[3];
            this.FDesc = reqAry[4];
            this.checkStateId = YssFun.toInt(reqAry[5]);
            this.OldFIVPayCatCode = reqAry[6];
            this.status = reqAry[7]; //lzp add 11.30
            this.tradeUseCode = reqAry[10];	//add by huangqirong 2012-04-13 story #2326
            this.tradeUseName = reqAry[11];	//add by huangqirong 2012-04-13 story #2326
            /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
            this.FFeeType = reqAry[12];
    		/**end shashijie 2013-1-28 STORY 3513*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new InvestPayCatBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析运营收支品种类型请出错", e);
        }
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into Tb_Base_InvestPayCat(FIVPayCatCode,FIVPayCatName,FPayType,FIVType,FDesc ," +
            		"FTradeUsageCode,FTradeUsageName ,FCheckState,FCreator,FCREATETIME,FCheckUser" +
            		/**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型*/
					" , FfeeType "+
					/**end shashijie 2013-1-28 STORY 3513*/
            		" ) " + //modify huangqirong 2012-04-13 story #2326
                " values(" + dbl.sqlString(this.FIVPayCatCode) + "," +
                dbl.sqlString(this.FIVPayCatName) + "," +
                this.FPayType + "," +
                dbl.sqlString(this.FIVType) + "," +
                dbl.sqlString(this.FDesc) + "," +
                dbl.sqlString(this.tradeUseCode) + "," +	//add by huangqirong 2012-04-13 story #2326
                dbl.sqlString(this.tradeUseName) + "," +	//add by huangqirong 2012-04-13 story #2326
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorName) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "''" :
                dbl.sqlString(this.creatorCode)) + 
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型*/
				" , "+dbl.sqlString(this.FFeeType)+
				/**end shashijie 2013-1-28 STORY 3513*/
                " )";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("新增-运营收支品种设置");
                sysdata.setStrCode(this.FIVPayCatCode);
                sysdata.setStrName(this.FIVPayCatName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
            //-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增运营收支品种设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    /**
     * editSetting：
     * 修改一条设置信息，先通过parseRowStr解析发送过来的请求，再通过类的属性修改到数据库中
     * @return String： 因为有些属性的值需要在后台进行计算，所以可能和发送过来的请求不一致，故这条信息返回给客户端。
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_InvestPayCat set FIVPayCatCode = " +
                dbl.sqlString(this.FIVPayCatCode) + ", FIVPayCatName = " +
                dbl.sqlString(this.FIVPayCatName) + ",FPayType = " +
                this.FPayType + ", FIVType = " +
                dbl.sqlString(this.FIVType) + ",FDesc= " +
                dbl.sqlString(this.FDesc) + "," +
                " FTradeUsageCode =" + dbl.sqlString(this.tradeUseCode) + "," +	//add by huangqirong 2012-04-13 story #2326
                " FTradeUsageName =" + dbl.sqlString(this.tradeUseName) + 	//add by huangqirong 2012-04-13 story #2326
                	",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                dbl.sqlString(this.creatorCode)) +
                /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
                " , FFeeType = "+dbl.sqlString(this.FFeeType)+
        		/**end shashijie 2013-1-28 STORY 3513*/
                " where FIVPayCatCode  = " +
                dbl.sqlString(this.OldFIVPayCatCode);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("修改-运营收支品种设置");
                sysdata.setStrCode(this.FIVPayCatCode);
                sysdata.setStrName(this.FIVPayCatName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新交易类型设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    /**
     * delSetting : 删除一条设置信息
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update Tb_Base_InvestPayCat set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FIVPayCatCode = " +
                dbl.sqlString(this.FIVPayCatCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            //---------lzp add 11.30
            if (this.status.equalsIgnoreCase("1")) {
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);
                sysdata.setStrAssetGroupCode("Common");
                sysdata.setStrFunName("删除-运营收支品种设置");
                sysdata.setStrCode(this.FIVPayCatCode);
                sysdata.setStrName(this.FIVPayCatName);
                sysdata.setStrUpdateSql(strSql);
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();
            }
//-----------------------

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除运营收支品种设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 原方法功能：只能处理期间连接的审核和未审核的单条信息。
     * 新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";         //定义一个字符串来放SQL语句
        String[] arrData = null;    //定义一个字符数组来循环还原
        boolean bTrans = false;     //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false);  //开启一个事物
            bTrans = true;              //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase("")) ) { //判断传来的内容是否为空//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                arrData = sRecycled.split("\r\n");  //解析它，把它还原成条目放在数组里。

                //循环数组，也就是循环还原条目
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]);   //解析这个数组里的内容
                    strSql = "update Tb_Base_InvestPayCat set FCheckState = " +
                        this.checkStateId + ", FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FIVPayCatCode = " +
                        dbl.sqlString(this.FIVPayCatCode);
                    //更新数据的SQL语句
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            //如果sRecycled为空，而FIVPayCatCode不为空，则按照FIVPayCatCode来执行sql语句
            else if (FIVPayCatCode != null && (!FIVPayCatCode.equalsIgnoreCase("")) ) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                strSql = "update Tb_Base_InvestPayCat set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FIVPayCatCode = " +
                    dbl.sqlString(this.FIVPayCatCode);
                //更新数据的SQL语句
                dbl.executeSql(strSql); //执行更新操作
            }
            if (this.status.equalsIgnoreCase("1")) { //判断status是否等于1,当传入1的时候就记录系统的信息状态
                com.yss.main.funsetting.SysDataBean sysdata = new com.yss.main.
                    funsetting.SysDataBean();
                sysdata.setYssPub(pub);                 //设置pub的值
                sysdata.setStrAssetGroupCode("Common"); //设置StrAssetGroupCode的值
                if (this.checkStateId == 1) {           //如果checkStateId==1就是它要的状态是审核状
                    sysdata.setStrFunName("审核-运营收支品种设置");   //设置StrFunName的值
                } else {
                    sysdata.setStrFunName("反审核-运营收支品种设置");  //设置StrFunName的值
                }

                sysdata.setStrCode(this.FIVPayCatCode); //设置StrCode的值
                sysdata.setStrName(this.FIVPayCatName); //设置StrName的值
                sysdata.setStrUpdateSql(strSql);        //设置StrUpdateSql的值
                sysdata.setStrCreator(pub.getUserName());
                sysdata.addSetting();   //把这些以上数据添加到系统数据表Tb_Fun_SysData
            }
            //-----------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核运营收支品种设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    /**
     * checkInput : 验证要保存的设置信息
     * @param btOper byte ： 操作类型，见YssCons中的操作类型
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "Tb_Base_InvestPayCat", "FIVPayCatCode",
                               this.FIVPayCatCode, this.OldFIVPayCatCode);
    }

    /**
     * saveMutliSetting ：
     * 多条设置信息同时保存
     * @param sMutilRowStr String ： 发送过来的多行请求
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    /**
     * getSetting ：
     * 获取一条设置信息
     * @return ParaSetting
     */
    public IDataSetting getSetting() throws YssException {
        return null;
    }

    /**
     * getAllSetting ：
     * 获取所有的设置信息
     * @return String
     */
    public String getAllSetting() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.FilterType != null) {
            sResult = " where 1=1 ";
            if (this.FilterType.checkStateId == 1) {
                sResult = sResult + " and a.FCheckState = 1 ";
            }
            if (this.FilterType.FIVPayCatCode.length() != 0) {
                sResult = sResult + " and a.FIVPayCatCode like '" +
                    FilterType.FIVPayCatCode.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.FIVPayCatName.length() != 0) {
                sResult = sResult + " and a.FIVPayCatName like '" +
                    FilterType.FIVPayCatName.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.FPayType != 99) {
                sResult = sResult + " and a.FPayType = " +
                    FilterType.FPayType;
            }
            if (this.FilterType.FIVType.length() != 0 &&
                !this.FilterType.FIVType.equals("99")) {
                sResult = sResult + " and a.FIVType like '" +
                    FilterType.FIVType.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.FDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    FilterType.FDesc.replaceAll("'", "''") + "%'";
            }
            //add by huangqirong 2012-04-13 story #2326
            if (this.FilterType.tradeUseCode.trim().length() != 0){
            	sResult = sResult + " and a.FTradeUsageCode like '" +
                	FilterType.tradeUseCode.replaceAll("'", "''") + "%'";
            }
            //---end---
            /**shashijie 2013-1-28 STORY 3513 运营费用增加费用品种类型 */
            if (this.FilterType.FFeeType != null && this.FilterType.FFeeType.trim().length() != 0) {
            	sResult = sResult + " and a.FFeeType like '" +
            		FilterType.FFeeType.replaceAll("'", "''") + "%'";
			}
    		/**end shashijie 2013-1-28 STORY 3513*/
        }
        return sResult;

    }

    public String getStatus() {
        return status;
    }

    /**
     * bug MS00149 QDV4南方2009年1月5日05_B 2009.01.15 方浩
     * 修改人：方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
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
                        pub.yssGetTableName("Tb_Base_InvestPayCat") +
                        " where FIVPayCatCode = " +
                        dbl.sqlString(this.FIVPayCatCode);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而FIVPayCatCode不为空，则按照FIVPayCatCode来执行sql语句
            else if (FIVPayCatCode != "" && FIVPayCatCode != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Base_InvestPayCat") +
                    " where FIVPayCatCode = " +
                    dbl.sqlString(this.FIVPayCatCode);

                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    public String getFIVPayCatCode() {
        return FIVPayCatCode;
    }

    public void setFIVPayCatCode(String FIVPayCatCode) {
        this.FIVPayCatCode = FIVPayCatCode;
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
     * 获取两费计提信息代码
     * add by lvhx 2010.06.24 MS01297 计息业务的明细通过业务日期和组合动态获取 QDV4赢时胜（深圳）2010年06月02日01_A 
     */
    public String getIncomeTypeData() throws YssException {
    	 String strSql = "";
         ResultSet rs = null;
         StringBuffer strResult = new StringBuffer();
         try{
           
            strSql =
                " select a.Fivpaycatcode " +
                "  from Tb_Base_InvestPayCat a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FPayType") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_ACCOUNT_SUBJECT) +
                " left join Tb_Fun_Vocabulary fiv on a.FIVType = fiv.FVocCode and fiv.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FIV_FEETYPE) +
                /**shashijie 2013-01-28 STORY 3513 运营费用增加费用品种类型 */
                " left join Tb_Fun_Vocabulary fiv2 on a.Ffeetype = fiv2.FVocCode and fiv2.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FOP_FEETYPE) +
                /**end shashijie 2013-01-28 STORY 3513 */
                buildFilterSql() +
                "  order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
	        while(rs.next()){
	        	strResult.append(rs.getString("Fivpaycatcode")).append(",");
	        }
	        if(strResult.length() > 1){
        		strResult.delete(strResult.length() - 1, strResult.length());
        	}
	        return strResult.toString();
        } catch (Exception e) {
            throw new YssException("获取两费信息代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
           

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
			sql="select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
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

	/**返回 fFeeType 的值*/
	public String getFFeeType() {
		return FFeeType;
	}

	/**传入fFeeType 设置  fFeeType 的值*/
	public void setFFeeType(String fFeeType) {
		FFeeType = fFeeType;
	}
}
