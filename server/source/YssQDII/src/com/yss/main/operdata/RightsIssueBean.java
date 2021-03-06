package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: 配股权益处理</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RightsIssueBean
    extends BaseDataSettingBean implements IDataSetting {
    public RightsIssueBean() {
    }

    private String strSecurityCode = ""; //证券代码
    private String strTSecurityCode = ""; //标的证券
    private String strSecurityName = ""; //证券名称
    private String strTSecurityName = ""; //标的证券名称
    private String strRoundCode = ""; //舍入代码
    private String strRoundName = ""; //舍入名称
    private String strRecordDate = ""; //权益确认日 xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    private String strExRightDate = ""; //除权日
    private String strExpirationDate = ""; //缴款截至日
    private String AfficheDate = ""; //公告日
    private String PayDate = ""; //到帐日
//----------------------2007.11.02 添加 蒋锦 -------------------------//
    private String BeginScriDat = ""; //认购起始日
    private String EndScriDate = ""; //认购截止日
    private String BeginTradeDate = ""; //交易起始日
    private String EndTradeDate = ""; //交易截止日
//-------------------------------------------------------------------//
    private String strRIPrice; //配股价格
    private String strDesc = ""; //描述

    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    private String PreTaxRatio = "0"; //税前权益比例
    private String AfterTaxRatio = "0"; //税后权益比例
    private String PortCode = ""; //组合代码
    private String PortName = ""; //组合名称
    private String AssetGroupCode = ""; //组合群代码
    private String AssetGroupName = ""; //组合群名称		//added by liubo.Story #1770
    private String OldAssetGroupCode = ""; //组合群代码
    private String OldPortCode = ""; //组合代码
    //----------------------------end-----------------------------//

    private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串
    private String sCuryCode = ""; // 单亮 2008-4-22 添加币种设置
    private String sCuryName = ""; // 单亮 2008-4-22 添加币种设置
    private String strOldSecurityCode = "";
    private String strOldRecordDate = "";
    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
    private String oldPayDate="";
    //------------------------
    private RightsIssueBean filterType;
    //MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A  
    private String tradeCode="";
    private String tradeName="";
    //------------------------------------------------------------------------------
    private String multAuditString = "";    //批量处理数据 xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能
	private SingleLogOper logOper;
	private String Group;//add by baopingping #story 1167 20110718  是否进行跨组合操作
	private String Iscopy;//add by baopingping #story 1167 20110718  是否进行覆盖
	 
    public String getIscopy() {
		return Iscopy;
	}

	public void setIscopy(String iscopy) {
		Iscopy = iscopy;
	}

	/**
     * addOperData
     * 增加配股权益设置
     * 修改日期：2007-11-02
     * 修改人：蒋锦
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                //-------------------------------------2007.11.02 修改 蒋锦 ----------------------------------------//
                "insert into " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                "(FSecurityCode,FTSecurityCode,FRecordDate,FExRightDate,FExpirationDate,FAfficheDate,FPayDate,FBeginScriDate,FEndScriDate,FBeginTradeDate,FEndTradeDate" + //添加字段
                ",FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FRIPrice,FRoundCode," +
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                "FTradeCode,FTradeName,"+
                //-------------------------------------------------------------------------------
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRiCuryCode)" +
                " values(" + dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlString(this.strTSecurityCode) + "," +
                dbl.sqlDate(this.strRecordDate) + "," +
                dbl.sqlDate(this.strExRightDate) + "," +
                dbl.sqlDate(this.strExpirationDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //添加//
                dbl.sqlDate(this.BeginScriDat) + "," +
                dbl.sqlDate(this.EndScriDate) + "," +
                dbl.sqlDate(this.BeginTradeDate) + "," +
                dbl.sqlDate(this.EndTradeDate) + "," +
                //----//
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio + "," +
                this.AfterTaxRatio + "," +
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------end--------------------//
                this.strRIPrice + "," +
                dbl.sqlString(this.strRoundCode) + "," +
                
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                dbl.sqlString(this.tradeCode) + "," +
                dbl.sqlString(this.tradeName) + "," +
                //------------------------------------------------------------------------
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.sCuryCode) +
                ")";
            //-------------------------------------------------------------------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * buildRowStr
     * 获取数据字符串
     * 修改日期:2007-11-02
     * 修改人:蒋锦
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strSecurityCode).append("\t");
        buf.append(this.strTSecurityCode).append("\t");
        buf.append(this.strSecurityName).append("\t");
        buf.append(this.strTSecurityName).append("\t");
        buf.append(this.strRecordDate).append("\t");
        buf.append(this.strExRightDate).append("\t");
        buf.append(this.strExpirationDate).append("\t");
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        buf.append(this.PreTaxRatio).append("\t");
        buf.append(this.AfterTaxRatio).append("\t");
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.AssetGroupCode).append("\t");
        buf.append(this.AssetGroupName).append("\t");		//added by liubo.Story #1770
        //-----------------------end-----------------------//
        buf.append(this.strRIPrice).append("\t");
        buf.append(this.strRoundCode).append("\t");
        buf.append(this.strRoundName).append("\t");
        buf.append(this.strDesc).append("\t");
        buf.append(this.AfficheDate).append("\t");
        buf.append(this.PayDate).append("\t");
        //----------------------------2007.11.02 添加 蒋锦 ---------------------------//
        buf.append(this.BeginScriDat).append("\t");
        buf.append(this.EndScriDate).append("\t");
        buf.append(this.BeginTradeDate).append("\t");
        buf.append(this.EndTradeDate).append("\t");
        //---------------------------------------------------------------------------//
        // 单亮 2008-4-23 添加币种设置  begin
        buf.append(this.sCuryCode).append("\t");
        buf.append(this.sCuryName).append("\t");
        //  end
      //MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A
        buf.append(this.tradeCode).append("\t");
        buf.append(this.tradeName).append("\t");
      //------------------------------------------------------------------------------ 
        
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * checkInput
     * 验证配股权益设置
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
//        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_RightsIssue"),
//                               "FSecurityCode,FRecordDate,FPortCode,FASSETGROUPCODE,FPAYDATE",
//                               this.strSecurityCode + "," + this.strRecordDate+","+
//                               //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
//                               (this.PortCode.length() == 0 ? " " :this.PortCode)+","+AssetGroupCode
//                               //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//                               //,
//                               +","+this.PayDate,
//                               //-----------------------------------------
//                               this.strOldSecurityCode + "," +
//                               this.strOldRecordDate+","+(this.OldPortCode.length() == 0 ? " " :this.OldPortCode) + "," +OldAssetGroupCode
//                               //-----------------------end--------------------//
//                               //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//                               +","+this.oldPayDate);
                               //------
		String[] sAssetGroup = null;
		//---add by songjie 2012.02.13 BUG QDV4赢时胜(上海)2012年01月20日02_B start---//
		boolean noAssetTable = false;
		String errorInfo = "";
		//---add by songjie 2012.02.13 BUG QDV4赢时胜(上海)2012年01月20日02_B end---//
		if("".equals(AssetGroupCode.trim())){
    		dbFun.checkInputCommon(btOper,
    					pub.yssGetTableName("Tb_Data_RightsIssue"),
    					"FSecurityCode,FRecordDate,FPortCode,FPAYDATE",
    					this.strSecurityCode + "," + this.strRecordDate+","+
                      //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                      (this.PortCode.length() == 0 ? " " :this.PortCode)
                      //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                      //,
                      +","+this.PayDate,
                      //-----------------------------------------
                      this.strOldSecurityCode + "," +
                      this.strOldRecordDate+","+(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
                      //-----------------------end--------------------//
                      //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                      +","+this.oldPayDate);
		}else{
			sAssetGroup =  AssetGroupCode.split(",");
    		for (int i = 0;i < sAssetGroup.length;i++)
    		{
    	    	try
    	    	{
    	    		//---add by songjie 2012.02.13 BUG QDV4赢时胜(上海)2012年01月20日02_B start---//
    	    		if(!dbl.yssTableExist("Tb_" + sAssetGroup[i] + "_Data_BonusShare")){
    	    			noAssetTable = true;
    	    			errorInfo = "组合群【" + sAssetGroup[i] + "】相关表未创建！";
    	    		}
    	    		//---add by songjie 2012.02.13 BUG QDV4赢时胜(上海)2012年01月20日02_B end---//
    	    		
    	    		dbFun.checkInputCommon(btOper,
        					"Tb_" + sAssetGroup[i] + "_Data_RightsIssue",
        					"FSecurityCode,FRecordDate,FPortCode,FPAYDATE",
        					this.strSecurityCode + "," + this.strRecordDate+","+
                          (this.PortCode.length() == 0 ? " " :this.PortCode)
                          //,
                          +","+this.PayDate,
                          //-----------------------------------------
                          this.strOldSecurityCode + "," +
                          this.strOldRecordDate+","+(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
                          //-----------------------end--------------------//
                          //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                          +","+this.oldPayDate);

    	    	}
    	    	catch(Exception e){
    	    		if(noAssetTable){
    	    		throw new YssException(errorInfo);
    	    		}else{
    	    			if(this.PortCode.length() == 0){
        	    			throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券代码为【" + 
        	    					this.strSecurityCode + "】,权益登记日为【" + strRecordDate + "】" +
        	    					"，到帐日为【" + this.PayDate + "】的数据！");
    	    			}else{
        	    			throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券代码为【" + 
        	    					this.strSecurityCode + "】,权益登记日为【" + strRecordDate + "】" +
        	    					"，投资组合为【"+ (this.PortCode.length() == 0 ? " " :this.PortCode) + 
        	    					"】，到帐日为【" + this.PayDate + "】的数据！");
    	    			}
    	    		}
    	    	}
    		}
		}
    }

    /**
     * 修改时间：2008年3月27号
     * 修改人：单亮
     * 原方法功能：只能处理配股权益设置的审核和未审核的单条信息。
     * 新方法功能：可以处理配股权益设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理配股权益设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
        String[] assetGroupCodes = null;//保存组合群数组
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
						assetGroupCodes = this.AssetGroupCode.trim().split(",");
						for(int j = 0; j < assetGroupCodes.length; j++){
		                    strSql = " update Tb_" + assetGroupCodes[j] + "_Data_RightsIssue " +
	                        " set FCheckState = " + this.checkStateId +
	                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                        ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
	                        " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
	                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
	                        " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")+
	                        //------------------------end----------------//
	                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                        " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
	                        //----------------------------------
	                        
		                    dbl.executeSql(strSql);							
						}
					}else{
	                    strSql = " update " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                        " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")+
                        //------------------------end----------------//
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
                        //----------------------------------
                        
	                    dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
              //如果sRecycled为空，而strSecurityCode不为空，则按照strSecurityCode来执行sql语句
            } else if ( strSecurityCode != null&&(!strSecurityCode.equalsIgnoreCase(""))) { 
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
            	if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		            	strSql = " update Tb_" + assetGroupCodes[j] + "_Data_RightsIssue" +
	                    " set FCheckState = " + this.checkStateId +
	                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
	                    " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
	                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
	                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                    //------------------------end----------------//
	                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                    " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
	                    //----------------------------------
	                    
		            	dbl.executeSql(strSql);						
					}
				}else{
	            	strSql = " update " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                    //------------------------end----------------//
                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                    " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
                    //----------------------------------
                    
	            	dbl.executeSql(strSql);					
				}
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除数据，即放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                //------------------------end----------------//
            	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
            	+" and FPAYDATE="+dbl.sqlDate(this.PayDate)
            	//----------------------------------
            	;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editOperData
     * 修改配股权益设置
     * 修改日期:2007-11-02
     * 修改人:蒋锦
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                " set FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                ",FTSecurityCode = " + dbl.sqlString(this.strTSecurityCode) +
                ",FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                ",FExRightDate = " + dbl.sqlDate(this.strExRightDate) +
                ",FExpirationDate = " + dbl.sqlDate(this.strExpirationDate) +
                ",FAfficheDate = " + dbl.sqlDate(this.AfficheDate) +
                ",FPayDate = " + dbl.sqlDate(this.PayDate) +
                //--------------------2007.11.02 添加 蒋锦 -----------------------//
                ",FBeginScriDate = " + dbl.sqlDate(this.BeginScriDat) +
                ",FEndScriDate = " + dbl.sqlDate(this.EndScriDate) +
                ",FBeginTradeDate = " + dbl.sqlDate(this.BeginTradeDate) +
                ",FEndTradeDate = " + dbl.sqlDate(this.EndTradeDate) +
                //---------------------------------------------------------------//
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
               ",FPreTaxRatio = " + this.PreTaxRatio +
               ",FAfterTaxRatio ="+this.AfterTaxRatio+
               ",FPortCode = "+ dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
               ",FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
               //--------------end----------------------//

                ",FRIPrice = " + this.strRIPrice + // lzp modify  20080122  数字型
                ",FRoundCode = " + dbl.sqlString(this.strRoundCode) +
                //MS01354   add by zhangfa 20100713 MS01354    QDV4赢时胜(上海)2010年06月25日01_A 
                ",FTradeCode = " + dbl.sqlString(this.tradeCode) +
                ",FTradeName = " + dbl.sqlString(this.tradeName) +
                //------------------------------------------------------------------------------
                ",FDesc = " + dbl.sqlString(this.strDesc) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ",Fricurycode = " + dbl.sqlString(this.sCuryCode) +
                " where FSecurityCode = " +
                dbl.sqlString(this.strOldSecurityCode) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
               " and FRecordDate = " + dbl.sqlDate(this.strOldRecordDate) +
               " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
               " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
               //------------------------end----------------//
               //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
               +" and FPAYDATE="+dbl.sqlDate(this.oldPayDate)
               //----------------------------------
               ;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * 此方法已被修改
     * 修改时间：2008年2月27号
     * 修改人：单亮
     * 原方法的功能：查询出配股权益业务数据并以一定格式显示，但不能显示回收站的数据
     * 新方法的功能：原功能的基础上，可以显示回收站的数据
     * 修改原因：原方法能显示回收站的数据
     * @throws YssException
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.strIsOnlyColumns.equals("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select y.* from " +
                "(select FSecurityCode from " +
                pub.yssGetTableName("Tb_Data_RightsIssue") +
                //修改前的代码
                //" where FCheckState <> 2 group by FSecurityCode) x join" +
                //修改后的代码
                //----------------------------begin
                "  group by FSecurityCode) x join" +
                //----------------------------end
                "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName,d.FIsinCode as FIsinCode" + //add by xuqiji 20090414:QDV4南方2009年04月01日01_AB  MS00361    南方要求权益信息中显示证券的ISIN码
                ",ass.FAssetGroupName as FAssetGroupName " +	//added by liubo.Story #1770
                ",e.FRoundName as FRoundName,f.FTSecurityName as FTSecurityName, x.FCuryName as FCuryName,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_RightsIssue") + " a " +
                //------------------------------------------------------
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode" +	//added by liubo.Story #1770
                //------------------------------------------------------
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //------------------------------------------------------
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName,o.FIsinCode as FIsinCode from " + //add by xuqiji 20090414:QDV4南方2009年04月01日01_AB  MS00361    南方要求权益信息中显示证券的ISIN码
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                //-------------------------------------------------------
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FTSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) f on a.FTSecurityCode = f.FSecurityCode" +
                //-------------------------------------------------------
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                //--2008-4-23- 单亮  begin
                " left join (select FCuryCode,FCuryName from " +
                pub.yssGetTableName("Tb_Para_Currency") +
                " where FCheckState = 1) x on a.FRiCuryCode = x.fcurycode " +
                //  end
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        
                " left join (select FPortCode ,FPortName  from  " +
                pub.yssGetTableName("tb_para_portfolio") + "  where FCheckState =1 ) h on a.FPortCode = h.FPortCode" +
               //end by lidaolong 
               //-------------------------------end-------------------------------//
                buildFilterSql() +
                
                //modified by liubo.Story #1770
                //================================
                //(this.filterType.AssetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='" + pub.getAssetGroupCode() + "')" :"")+//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                (this.filterType.AssetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')" :"") +
                //===============end=================
                
                ") y on x.FSecurityCode = y.FSecurityCode " +
                " order by y.FCheckState, y.FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("RightsIssue");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName)).
                    append(YssCons.YSS_LINESPLITMARK);
				//=============end===================

                setResultSetAttr(rs);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取配股权益设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() {
        return "";
    }

    /**
     * getOperData
     */
    public void getOperData() {
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
     * 筛选条件
     * @return String
     */

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.strIsOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.strSecurityCode.length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.strSecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strRecordDate.length() != 0 &&
                !this.filterType.strRecordDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FRecordDate = " +
                    dbl.sqlDate(filterType.strRecordDate);
            }
            if (this.filterType.strExRightDate.length() != 0 &&
                !this.filterType.strExRightDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FExRightDate = " +
                    dbl.sqlDate(filterType.strExRightDate);
            }
            if (this.filterType.strExpirationDate.length() != 0 &&
                !this.filterType.strExpirationDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FExpirationDate = " +
                    dbl.sqlDate(filterType.strExpirationDate);
            }

            if (this.filterType.AfficheDate.length() != 0 &&
                !this.filterType.AfficheDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FAfficheDate = " +
                    dbl.sqlDate(filterType.AfficheDate);
            }

            if (this.filterType.PayDate.length() != 0 &&
                !this.filterType.PayDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FPayDate = " +
                    dbl.sqlDate(filterType.PayDate);
            }

            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            if (!this.filterType.PreTaxRatio.equals("0")) {
                sResult = sResult + " and a.FPreTaxRatio like '" +
                    filterType.PreTaxRatio.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.AfterTaxRatio.equals("0")) {
                sResult = sResult + " and a.FAfterTaxRatio like '" +
                    filterType.AfterTaxRatio.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.PortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.PortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.AssetGroupCode.trim().length() != 0) {
                sResult = sResult + " and a.FASSETGROUPCODE ='" +
                    filterType.AssetGroupCode.replaceAll("'", "''") + "'";
            }
            //-----------------------------end---------------------------//

            if (this.filterType.strRIPrice.length() != 0) {
                sResult = sResult + " and a.FRIPrice like '" +
                    filterType.strRIPrice.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strRoundCode.length() != 0) {
                sResult = sResult + " and a.FRoundCode like '" +
                    filterType.strRoundCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.strDesc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * parseRowStr
     * 解析配股权益设置
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能
        try {
//        	xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
            if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];
                multAuditString = sMutiAudit;
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];
            }
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.strSecurityCode = reqAry[0];
            this.strTSecurityCode = reqAry[1];
            this.strSecurityName = reqAry[2];
            this.strTSecurityName = reqAry[3];
            this.strRecordDate = reqAry[4];
            this.strExRightDate = reqAry[5];
            this.strExpirationDate = reqAry[6];
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            this.PreTaxRatio = reqAry[7];
            this.AfterTaxRatio = reqAry[8];
            this.PortCode=reqAry[9];
            this.OldPortCode=reqAry[10];
            this.AssetGroupCode=reqAry[11];
            this.AssetGroupName=reqAry[12];		//added by liubo.Story #1770
            this.OldAssetGroupCode=reqAry[13];

            this.strRIPrice = reqAry[14];
            this.strRoundCode = reqAry[15];
            this.strRoundName = reqAry[16];
            this.strDesc = reqAry[17];
            this.checkStateId = Integer.parseInt(reqAry[18]);
            this.strOldSecurityCode = reqAry[19];
            this.strOldRecordDate = reqAry[20];
            this.strIsOnlyColumns = reqAry[21];
            this.AfficheDate = reqAry[22];
            this.PayDate = reqAry[23];
            //----------------------2007.11.02 添加 蒋锦 -------------------------//
            this.BeginScriDat = reqAry[24];
            this.EndScriDate = reqAry[25];
            this.BeginTradeDate = reqAry[26];
            this.EndTradeDate = reqAry[27];
            this.sCuryCode = reqAry[28];
            //-------------------------------------------------------------------//
          //MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A
            if(reqAry.length>=30&&reqAry[29]!=null&&reqAry[30]!=null){
             this.tradeCode=reqAry[29];	
             this.tradeName=reqAry[30];
            }
          //------------------------------------------------------------------------------  
          //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
            this.oldPayDate=reqAry[31];
          //----------------------------------
           this.Group=reqAry[32];//add baopingping #story 1167 接收前台传来是否进行跨组合操作
           this.Iscopy=reqAry[33];//add baopingping #story 1167 接收前台传来是否覆盖
			super.parseRecLog();
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RightsIssueBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析配股权益数据信息出错", e);
        }

    }

    /**
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) {
        return "";
    }

    /**
     * setResultSetAttr
     * 修改日期:2007-11-02
     * 修改人:蒋锦
     * @param rs ResultSet
     * @throws SQLException
     * @throws YssException
     */
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strSecurityCode = rs.getString("FSecurityCode") + "";
        this.strTSecurityCode = rs.getString("FTSecurityCode") + "";
        this.strSecurityName = rs.getString("FSecurityName") + "";
        this.strTSecurityName = rs.getString("FTSecurityName") + "";
        this.strRecordDate = rs.getDate("FRecordDate") + "";
        this.strExRightDate = rs.getDate("FExRightDate") + "";
        this.strExpirationDate = rs.getDate("FExpirationDate") + "";

        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        this.PortCode = rs.getString("FPortCode") + "";
        this.PortName = rs.getString("FPortName") + "";
        this.AssetGroupCode = rs.getString("FASSETGROUPCODE");
        this.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
        this.AfterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
        //-------------------------end--------------------//

        this.strRIPrice = rs.getString("FRIPrice") + "";
        this.strRoundCode = rs.getString("FRoundCode") + "";
        this.strRoundName = rs.getString("FRoundName") + "";
        this.strDesc = rs.getString("FDesc") + "";
        this.sCuryCode = rs.getString("FRiCuryCode") + ""; //单亮 2008-4-22 添加币种设置
        this.sCuryName = rs.getString("FCuryName") + ""; //单亮 2008-4-22 添加币种设置
        if (rs.getDate("FAfficheDate") == null) {
            AfficheDate = "1900-01-01";
        } else {
            this.AfficheDate = rs.getString("FAfficheDate") + "";
        }
        if (rs.getDate("FPayDate") == null) {
            PayDate = "1900-01-01";
        } else {
        	//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
            //this.PayDate = rs.getString("FPayDate") + "";
        	this.PayDate = rs.getDate("FPayDate") + "";
        	//-------------------------
        }
        //----------------------2007.11.02 添加 蒋锦 ---------------------------//
        if (rs.getDate("FBeginScriDate") == null) {
            this.BeginScriDat = "1900-01-01";
        } else {
            this.BeginScriDat = rs.getString("FBeginScriDate") + "";
        }
        if (rs.getDate("FEndScriDate") == null) {
            this.EndScriDate = "1900-01-01";
        } else {
            this.EndScriDate = rs.getString("FEndScriDate") + "";
        }
        if (rs.getDate("FBeginTradeDate") == null) {
            this.BeginTradeDate = "1900-01-01";
        } else {
            this.BeginTradeDate = rs.getString("FBeginTradeDate") + "";
        }
        if (rs.getDate("FEndTradeDate") == null) {
            this.EndTradeDate = "1900-01-01";
        } else {
            this.EndTradeDate = rs.getString("FEndTradeDate") + "";
        }
        //---------------------------------------------------------------------//
        //MS01354   add by zhangfa 20100713 MS01354    QDV4赢时胜(上海)2010年06月25日01_A 
        this.tradeCode=rs.getString("FTradeCode")==null ? "": rs.getString("FTradeCode");
        this.tradeName=rs.getString("FTradeName")==null ? "": rs.getString("FTradeName");
        //------------------------------------------------------------------------------
        super.setRecLog(rs);

    }

    /**
     * getOperValue
     * xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能  
     * @param sType String
     * @return String
     * @throws YssException 
     */
    public String getOperValue(String sType) throws YssException {
    	try{
    		if (sType.equalsIgnoreCase("multauditTradeSub")) 
    		{
                if (multAuditString.length() > 0) 
                {
                	//modify baopingping #story 1167 20110720 添加对跨组合群的处理
                	MarketValueBean Mark=new MarketValueBean();
            		Mark.setYssPub(pub);
            		if (sType.equalsIgnoreCase("multauditTradeSub")) 
            		{
                        if (multAuditString.length() > 0) 
                        {
							if (this.Group.equalsIgnoreCase("ok")) {
								String FAssetGroupCode = Mark.getAssdeGroup();
								String[] GroupCode = null;
								String ss = "";
								if (FAssetGroupCode != null) {
									GroupCode = FAssetGroupCode.split("\t");
									for (int i = 0; i < GroupCode.length; i++) {
										this.auditData(this.multAuditString,
												GroupCode[i]);// 跨组合执行批量审核/反审核
									}
								}
							} else { // ------end-------
								return this.auditMutli(this.multAuditString); // 执行批量审核/反审核/删除
							}
            	        }
            		}
                
                }
             }
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
        return "";
    }

    

	/**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        RightsIssueBean befEditBean = new RightsIssueBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select y.* from " +
                "(select FSecurityCode from " +
                pub.yssGetTableName("Tb_Data_RightsIssue") +
                " where FCheckState <> 2 group by FSecurityCode) x join" +
                "(select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName" +
                ",e.FRoundName as FRoundName,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_RightsIssue") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                
             // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
     
                " left join (select FPortCode ,FPortName  from  " +
                pub.yssGetTableName("tb_para_portfolio") + "  where FCheckState =1 ) h on a.FPortCode = h.FPortCode" +
              
                //end by lidaolong 
                //-------------------------------end-------------------------------//
                " where  a.FSecurityCode =" + dbl.sqlString(this.strOldSecurityCode) + //lzp modify 20080122 FSecurityCode指代不明改为 a.FSecurityCode
                " and FRecordDate=" + dbl.sqlDate(YssFun.toDate(this.strOldRecordDate)) +
                (this.OldPortCode.length() > 0 ? "and a.FPortCode=" + dbl.sqlString(this.OldPortCode) : "and 1=1") +//xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                ") y on x.FSecurityCode = y.FSecurityCode " +
                " order by y.FCheckState, y.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.strSecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.strTSecurityCode = rs.getString("FTSecurityCode") + "";
                befEditBean.strSecurityName = rs.getString("FSecurityName") + "";
                befEditBean.strRecordDate = rs.getDate("FRecordDate") + "";
                befEditBean.strExRightDate = rs.getDate("FExRightDate") + "";
                befEditBean.strExpirationDate = rs.getDate("FExpirationDate") + "";
                //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                befEditBean.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
                befEditBean.AfterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
                befEditBean.PortCode = rs.getString("FPortCode") + "";
                befEditBean.PortName = rs.getString("FPortName") + "";
                //-------------------------end----------------------------//

                befEditBean.strRIPrice = rs.getString("FRIPrice") + "";
                befEditBean.strRoundCode = rs.getString("FRoundCode") + "";
                befEditBean.strRoundName = rs.getString("FRoundName") + "";
                befEditBean.strDesc = rs.getString("FDesc") + "";
                befEditBean.AfficheDate = rs.getString("FAfficheDate");
                befEditBean.PayDate = rs.getString("FPayDate");

            }
            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
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
	 * add baopingping 20110715 #story 1167
	 * 跨组合 添加，删除，修改，复制，审核的入口
	 * @return String
     * @throws YssException 
	 */
    public String saveMutliSetting(String flag) throws YssException {
    	MarketValueBean Mark =new MarketValueBean();
		Mark.setYssPub(pub);
		String msg="";
		//modified by liubo.Story #1770
		//================================
//		String FAssetGroupCode=Mark.getAssdeGroup();
    	String[] GroupCode=null;
//		if(FAssetGroupCode!=null)
//		{
//		   GroupCode=FAssetGroupCode.split("\t");
//		}
    	if (flag.equalsIgnoreCase("copy")||flag.equalsIgnoreCase("add")){
    		checkInput("".equals(this.AssetGroupCode.trim()) ? YssCons.OP_ADD : YssCons.OP_MutliAdd);
    	}else if(flag.equalsIgnoreCase("edit")){
    		checkInput("".equals(this.AssetGroupCode.trim()) ? YssCons.OP_EDIT : YssCons.OP_MutliEdit);
    	}


    	GroupCode = ("".equals(this.AssetGroupCode.trim()) ? pub.getAssetGroupCode() : this.AssetGroupCode).split(",");
    	//===============end==================
	   
		for(int i=0;i<GroupCode.length;i++)
		{
    		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
    		if(!dbl.yssTableExist("Tb_" + GroupCode[i] + "_Data_BonusShare")){
    			throw new YssException("组合群【" + GroupCode[i] + "】相关表未创建！");
    		}
    		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
			String tableName=this.getTable(GroupCode[i]);
			if(flag.equalsIgnoreCase("add")){
				if(this.Iscopy.equalsIgnoreCase("yes")){
				if(tableName.equalsIgnoreCase("none"))
				{
			     this.addData(GroupCode[i]);
				}else{
					this.editData(GroupCode[i]);
				}
				}else{
					if(tableName.equalsIgnoreCase("none"))
					{
				     this.addData(GroupCode[i]);
					}
				}
			}else if(flag.equalsIgnoreCase("edit")){
				this.editData(GroupCode[i]);
			}else if(flag.equalsIgnoreCase("copy")){
				if(this.Iscopy.equalsIgnoreCase("yes")){
					if(tableName.equalsIgnoreCase("none"))
					{
						this.copyData(GroupCode[i]);
					
					}else{
						this.editData(GroupCode[i]);
					}
				}else{
					if(tableName.equalsIgnoreCase("none"))
					{
				    this.copyData(GroupCode[i]);
					}else{
						//this.editData(GroupCode[i]);
					}
				}
			}else if(flag.equalsIgnoreCase("checked")){
				
				msg+=this.getGroup(GroupCode[i]);
				
			}
		}
		if(msg.endsWith(",")){
			msg = msg.substring(0, msg.length()-1);
		}
			return msg;
    }


	/**
     * 从回收站删除数据，即彻底从数据库删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        //add by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A
        String[] assetGroupCodes = null;//保存组合群数组
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
                    //---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
					if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
						assetGroupCodes = this.AssetGroupCode.trim().split(",");
						for(int j = 0; j < assetGroupCodes.length; j++){
		                    strSql = " delete from Tb_" + assetGroupCodes[j] + "_Data_RightsIssue" +
	                        " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
	                        //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
	                        " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                        //--------------------------------end----------------------------//
	                    	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                        " and FPAYDATE = " +dbl.sqlDate(this.PayDate);

		                    //执行sql语句
		                    dbl.executeSql(strSql);							
						}
					}else{
	                    strSql = " delete from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                        " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                        //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                        " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                        //--------------------------------end----------------------------//
                    	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        " and FPAYDATE = " +dbl.sqlDate(this.PayDate);

	                    //执行sql语句
	                    dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strSecurityCode != "" && strSecurityCode != null) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		                strSql = " delete from Tb_" + assetGroupCodes[j] + "_Data_RightsIssue" +
	                    " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
	                    //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
	                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                    //--------------------------------end----------------------------//
	                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                    " and FPAYDATE = " + dbl.sqlDate(this.PayDate);

		                //执行sql语句
		                dbl.executeSql(strSql);						
					}
				}else{
	                strSql = " delete from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                    " where FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
                    //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                    //--------------------------------end----------------------------//
                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                    " and FPAYDATE = " + dbl.sqlDate(this.PayDate);

	                //执行sql语句
	                dbl.executeSql(strSql);					
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
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
    //xuqiji 20090720 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    public String getAfterTaxRatio() {
        return AfterTaxRatio;
    }

    public void setAfterTaxRatio(String AfterTaxRatio) {
        this.AfterTaxRatio = AfterTaxRatio;
    }

    public String getAssetGroupCode() {
        return AssetGroupCode;
    }

    public void setAssetGroupCode(String AssetGroupCode) {
        this.AssetGroupCode = AssetGroupCode;
    }

    public String getOldAssetGroupCode() {
        return OldAssetGroupCode;
    }

    public void setOldAssetGroupCode(String OldAssetGroupCode) {
        this.OldAssetGroupCode = OldAssetGroupCode;
    }

    public String getOldPortCode() {
        return OldPortCode;
    }

    public void setOldPortCode(String OldPortCode) {
        this.OldPortCode = OldPortCode;
    }

    public String getPortCode() {
        return PortCode;
    }

    public void setPortCode(String PortCode) {
        this.PortCode = PortCode;
    }

    public String getPortName() {
        return PortName;
    }

    public void setPortName(String PortName) {
        this.PortName = PortName;
    }

    public String getPreTaxRatio() {
        return PreTaxRatio;
    }

    public void setPreTaxRatio(String PreTaxRatio) {
        this.PreTaxRatio = PreTaxRatio;
    }
    //-----------------------end-------------------------//
    public String getAfficheDate() {
        return AfficheDate;
    }

    public void setAfficheDate(String AfficheDate) {
        this.AfficheDate = AfficheDate;
    }

    public String getBeginScriDat() {
        return BeginScriDat;
    }

    public void setBeginScriDat(String BeginScriDat) {
        this.BeginScriDat = BeginScriDat;
    }

    public String getBeginTradeDate() {
        return BeginTradeDate;
    }

    public void setBeginTradeDate(String BeginTradeDate) {
        this.BeginTradeDate = BeginTradeDate;
    }

    public String getEndScriDate() {
        return EndScriDate;
    }

    public void setEndScriDate(String EndScriDate) {
        this.EndScriDate = EndScriDate;
    }

    public String getEndTradeDate() {
        return EndTradeDate;
    }

    public void setEndTradeDate(String EndTradeDate) {
        this.EndTradeDate = EndTradeDate;
    }

    public RightsIssueBean getFilterType() {
        return filterType;
    }
    public String getStrDesc() {
        return strDesc;
    }

    public String getStrExpirationDate() {
        return strExpirationDate;
    }

    public String getStrExRightDate() {
        return strExRightDate;
    }

    public String getStrOldRecordDate() {
        return strOldRecordDate;
    }

    public String getStrOldSecurityCode() {
        return strOldSecurityCode;
    }

    public String getStrRecordDate() {
        return strRecordDate;
    }

    public String getStrRIPrice() {
        return strRIPrice;
    }

    public String getStrRoundCode() {
        return strRoundCode;
    }

    public String getStrRoundName() {
        return strRoundName;
    }

    public String getStrSecurityCode() {
        return strSecurityCode;
    }

    public String getStrSecurityName() {
        return strSecurityName;
    }

    public String getStrTSecurityCode() {
        return strTSecurityCode;
    }

    public String getStrTSecurityName() {
        return strTSecurityName;
    }

    public String getSCuryCode() {
        return sCuryCode;
    }

    public String getSCuryName() {
        return sCuryName;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setFilterType(RightsIssueBean filterType) {
        this.filterType = filterType;
    }
    public void setStrDesc(String strDesc) {
        this.strDesc = strDesc;
    }

    public void setStrExpirationDate(String strExpirationDate) {
        this.strExpirationDate = strExpirationDate;
    }

    public void setStrExRightDate(String strExRightDate) {
        this.strExRightDate = strExRightDate;
    }

    public void setStrOldRecordDate(String strOldRecordDate) {
        this.strOldRecordDate = strOldRecordDate;
    }

    public void setStrOldSecurityCode(String strOldSecurityCode) {
        this.strOldSecurityCode = strOldSecurityCode;
    }

    public void setStrRecordDate(String strRecordDate) {
        this.strRecordDate = strRecordDate;
    }

    public void setStrRIPrice(String strRIPrice) {
        this.strRIPrice = strRIPrice;
    }

    public void setStrRoundCode(String strRoundCode) {
        this.strRoundCode = strRoundCode;
    }

    public void setStrRoundName(String strRoundName) {
        this.strRoundName = strRoundName;
    }

    public void setStrSecurityCode(String strSecurityCode) {
        this.strSecurityCode = strSecurityCode;
    }

    public void setStrSecurityName(String strSecurityName) {
        this.strSecurityName = strSecurityName;
    }

    public void setStrTSecurityCode(String strTSecurityCode) {
        this.strTSecurityCode = strTSecurityCode;
    }

    public void setStrTSecurityName(String strTSecurityName) {
        this.strTSecurityName = strTSecurityName;
    }

    public void setSCuryCode(String sCuryCode) {
        this.sCuryCode = sCuryCode;
    }

    public void setSCuryName(String sCuryName) {
        this.sCuryName = sCuryName;
    }

    public void setPayDate(String PayDate) {
        this.PayDate = PayDate;
    }
  //MS01354   add by zhangfa 20100712 MS01354    QDV4赢时胜(上海)2010年06月25日01_A    
    public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	//------------------------------------------------------------------------------
	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	public String getOldPayDate() {
		return oldPayDate;
	}

	public void setOldPayDate(String oldPayDate) {
		this.oldPayDate = oldPayDate;
	}
	//----------------------------------
	/**
     * xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
     * @param sMutilRowStr String
     * @return String
     * @throws YssException
     */
    public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        RightsIssueBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = ? and FRecordDate = ? and FPortCode= ?"
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +" and FPayDate= ?"
                //----------------------
                ;

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new RightsIssueBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getStrSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getStrRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        psmt.setDate(4, YssFun.toSqlDate(data.getPayDate()));
                        //------------------
                        psmt.addBatch();
                   //---增加批量删除的日志记录功能----guojianhua add 20100907-------//
                        logOper = SingleLogOper.getInstance();
                       data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId  == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
                        // -----------------------------------------//
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("批量处理数据出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
	 * add baopingping #story 1167 20110717
	 * 删除 ，审核，反审核 所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
     * @throws YssException 
	 */
    private String  auditData(String sMutilRowStr, String FAssetGroupCode) throws YssException {
    	boolean checkTrue=false;
    	if(this.checkStateId==2)
    	{
    		 checkTrue=true;
    	}
    	Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        RightsIssueBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
            if(!dbl.yssTableExist("Tb_"+FAssetGroupCode+"_Data_RightsIssue")){
            	return "";
            }
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
            
            sqlStr = "update " + ("Tb_"+FAssetGroupCode+"_Data_RightsIssue") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = ? and FRecordDate = ? and FPortCode= ?"
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +" and FPayDate= ?"+ (checkTrue ? " and FCheckState='0'" : " ");//add by baopingping #story 1167 20110720 只删除未审核的数据 
                //----------------------
                

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new RightsIssueBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getStrSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getStrRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        psmt.setDate(4, YssFun.toSqlDate(data.getPayDate()));
                        //------------------
                        psmt.addBatch();
                   //---增加批量删除的日志记录功能----guojianhua add 20100907-------//
                        logOper = SingleLogOper.getInstance();
                       data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId  == 1) {
							data.checkStateId = 1;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						} else if (this.checkStateId == 0) {
							data.checkStateId = 0;
							logOper.setIData(data, YssCons.OP_AUDIT, pub);
						}
                        // -----------------------------------------//
                    }
                }
                conn.setAutoCommit(false);
                bTrans = true;
                psmt.executeBatch();
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
        	//edit by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B
            throw new YssException("批量处理数据出错!" , e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
	}
    public String getGroup() {
		return Group;
	}

	public void setGroup(String group) {
		Group = group;
	}

	/**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 * @throws YssException 
	 */
    public String addData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                //-------------------------------------2007.11.02 修改 蒋锦 ----------------------------------------//
                "insert into " + ("Tb_"+FAssetGroupCode+"_Data_RightsIssue") +
                "(FSecurityCode,FTSecurityCode,FRecordDate,FExRightDate,FExpirationDate,FAfficheDate,FPayDate,FBeginScriDate,FEndScriDate,FBeginTradeDate,FEndTradeDate" + //添加字段
                ",FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FRIPrice,FRoundCode," +
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                "FTradeCode,FTradeName,"+
                //-------------------------------------------------------------------------------
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRiCuryCode)" +
                " values(" + dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlString(this.strTSecurityCode) + "," +
                dbl.sqlDate(this.strRecordDate) + "," +
                dbl.sqlDate(this.strExRightDate) + "," +
                dbl.sqlDate(this.strExpirationDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //添加//
                dbl.sqlDate(this.BeginScriDat) + "," +
                dbl.sqlDate(this.EndScriDate) + "," +
                dbl.sqlDate(this.BeginTradeDate) + "," +
                dbl.sqlDate(this.EndTradeDate) + "," +
                //----//
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio + "," +
                this.AfterTaxRatio + "," +
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------end--------------------//
                this.strRIPrice + "," +
                dbl.sqlString(this.strRoundCode) + "," +
                
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                dbl.sqlString(this.tradeCode) + "," +
                dbl.sqlString(this.tradeName) + "," +
                //------------------------------------------------------------------------
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.sCuryCode) +
                ")";
            //-------------------------------------------------------------------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

		
	}
    
    /**
	 * add baopingping #story 1167 20110717
	 * 修改所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
     * @throws YssException 
	 */
    private String editData(String FAssetGroupCode) throws YssException {
    	 Connection conn = dbl.loadConnection();
         boolean bTrans = false;
         String strSql = "";
 		 String sTmpAssetGroupCode = "";
         try {
        	 String[] sOldAssetGroup = null;
        	 String[] sNewAssetGroup = null;

        	 if (!OldAssetGroupCode.equals(AssetGroupCode))
        	 {
	        	 sOldAssetGroup = ("".equals(OldAssetGroupCode.trim()) ? pub.getAssetGroupCode() : OldAssetGroupCode).split(",");
	    		 sNewAssetGroup = ("".equals(AssetGroupCode.trim()) ? pub.getAssetGroupCode() : AssetGroupCode).split(",");
	    		 
	    		 for (int i = 0;i < sOldAssetGroup.length;i++)
	    		 {
	
		        	 strSql = "delete from " +
		             "Tb_" + sOldAssetGroup[i] + "_Data_RightsIssue" +
		             " where FSecurityCode = " +
		             dbl.sqlString(this.strSecurityCode) +
		              " and FRecordDate = " + dbl.sqlDate(this.strOldRecordDate) +
		             " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
		             " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
		             +" and FPAYDATE=" +dbl.sqlDate(this.oldPayDate);
		        	 
		        	 dbl.executeSql(strSql);
	    		 }
	    		 for (int i = 0;i < sNewAssetGroup.length;i++)
        		 {
	    			 if (!FAssetGroupCode.equals(sTmpAssetGroupCode))
	    			 {
				       	addData(FAssetGroupCode);
				       	sTmpAssetGroupCode = FAssetGroupCode;
	    			 }
        		 }
        	 }
        	 else
        	 {
	             strSql =
	                 "update " +  ("Tb_"+FAssetGroupCode+"_Data_RightsIssue") +
	                 " set FSecurityCode = " + dbl.sqlString(this.strSecurityCode) +
	                 ",FTSecurityCode = " + dbl.sqlString(this.strTSecurityCode) +
	                 ",FRecordDate = " + dbl.sqlDate(this.strRecordDate) +
	                 ",FExRightDate = " + dbl.sqlDate(this.strExRightDate) +
	                 ",FExpirationDate = " + dbl.sqlDate(this.strExpirationDate) +
	                 ",FAfficheDate = " + dbl.sqlDate(this.AfficheDate) +
	                 ",FPayDate = " + dbl.sqlDate(this.PayDate) +
	                 //--------------------2007.11.02 添加 蒋锦 -----------------------//
	                 ",FBeginScriDate = " + dbl.sqlDate(this.BeginScriDat) +
	                 ",FEndScriDate = " + dbl.sqlDate(this.EndScriDate) +
	                 ",FBeginTradeDate = " + dbl.sqlDate(this.BeginTradeDate) +
	                 ",FEndTradeDate = " + dbl.sqlDate(this.EndTradeDate) +
	                 //---------------------------------------------------------------//
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                ",FPreTaxRatio = " + this.PreTaxRatio +
	                ",FAfterTaxRatio ="+this.AfterTaxRatio+
	                ",FPortCode = "+ dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                ",FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                //--------------end----------------------//
	
	                 ",FRIPrice = " + this.strRIPrice + // lzp modify  20080122  数字型
	                 ",FRoundCode = " + dbl.sqlString(this.strRoundCode) +
	                 //MS01354   add by zhangfa 20100713 MS01354    QDV4赢时胜(上海)2010年06月25日01_A 
	                 ",FTradeCode = " + dbl.sqlString(this.tradeCode) +
	                 ",FTradeName = " + dbl.sqlString(this.tradeName) +
	                 //------------------------------------------------------------------------------
	                 ",FDesc = " + dbl.sqlString(this.strDesc) +
	                 ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
	                 ",FCreator = " + dbl.sqlString(this.creatorCode) +
	                 ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
	                 ",FCheckUser = " +
	                 (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                 ",Fricurycode = " + dbl.sqlString(this.sCuryCode) +
	                 //modified by liubo.Story #1770
	                 //=============================
	                 " where FSecurityCode = " +
	                 dbl.sqlString(this.strOldSecurityCode) +
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                " and FRecordDate = " + dbl.sqlDate(this.strOldRecordDate) +
	                " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
	                " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
	                //------------------------end----------------//
	                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                +" and FPAYDATE="+dbl.sqlDate(this.oldPayDate)+" and FCheckState='0'"
	                //----------------------------------
	                //===============end==============
	                ;
	
	             conn.setAutoCommit(false);
	             bTrans = true;
	             dbl.executeSql(strSql);
	             conn.commit();
	             bTrans = false;
	             conn.setAutoCommit(true);
        	 }
             return buildRowStr();
         } catch (Exception e) {
             throw new YssException("修改配股权益业务数据出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
		
	}
    
    /**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 */
    private String copyData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                //-------------------------------------2007.11.02 修改 蒋锦 ----------------------------------------//
                "insert into " + ("Tb_"+FAssetGroupCode+"_Data_RightsIssue") +
                "(FSecurityCode,FTSecurityCode,FRecordDate,FExRightDate,FExpirationDate,FAfficheDate,FPayDate,FBeginScriDate,FEndScriDate,FBeginTradeDate,FEndTradeDate" + //添加字段
                ",FPreTaxRatio,FAfterTaxRatio,FPortCode,FAssetGroupCode,FRIPrice,FRoundCode," +
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                "FTradeCode,FTradeName,"+
                //-------------------------------------------------------------------------------
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRiCuryCode)" +
                " values(" + dbl.sqlString(this.strSecurityCode) + "," +
                dbl.sqlString(this.strTSecurityCode) + "," +
                dbl.sqlDate(this.strRecordDate) + "," +
                dbl.sqlDate(this.strExRightDate) + "," +
                dbl.sqlDate(this.strExpirationDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //添加//
                dbl.sqlDate(this.BeginScriDat) + "," +
                dbl.sqlDate(this.EndScriDate) + "," +
                dbl.sqlDate(this.BeginTradeDate) + "," +
                dbl.sqlDate(this.EndTradeDate) + "," +
                //----//
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio + "," +
                this.AfterTaxRatio + "," +
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------end--------------------//
                this.strRIPrice + "," +
                dbl.sqlString(this.strRoundCode) + "," +
                
                //MS01354    add by zhangfa 20100713    QDV4赢时胜(上海)2010年06月25日01_A    
                dbl.sqlString(this.tradeCode) + "," +
                dbl.sqlString(this.tradeName) + "," +
                //------------------------------------------------------------------------
                dbl.sqlString(this.strDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + "," +
                dbl.sqlString(this.sCuryCode) +
                ")";
            //-------------------------------------------------------------------------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("复制配股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
    
    
    /**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存在这个组合的数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getTable(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="none";
		try{
			sql="select * from TB_"+TableName+"_Data_RightsIssue where " +
			"FSecurityCode='"+this.strSecurityCode+"'and FPayDate="+
			dbl.sqlDate(this.PayDate)+" and FRecordDate="+dbl.sqlDate(this.strRecordDate)+" and " +
			" FPortCode="+ dbl.sqlString((this.PortCode.length() == 0) ? " "
			: this.PortCode) +" and FAssetGroupCode="+dbl
			.sqlString((this.AssetGroupCode.length() == 0) ? " "
			: this.AssetGroupCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name="full";
			}
			return name;
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	/**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否组合的数据存在
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getGroup(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="";
		try{
			sql="select * from TB_"+TableName+"_Data_RightsIssue where Fcheckstate = 0 " +
			" and FSecurityCode='"+this.strSecurityCode+"'and FPayDate="+
			dbl.sqlDate(this.PayDate)+" and FRecordDate="+dbl.sqlDate(this.strRecordDate)+" and " +
			" FPortCode="+ dbl.sqlString((this.PortCode.length() == 0) ? " "
			: this.PortCode) +" and FAssetGroupCode="+dbl
			.sqlString((this.AssetGroupCode.length() == 0) ? " "
			: this.AssetGroupCode);
			rs=dbl.openResultSet(sql);
			while(rs.next())
			{
				name=TableName;
			}
			return (name.length() > 0 ? name + "," : name);
		}
		catch(Exception e){
			throw new YssException("获取组合群出错！\t");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * added by liubo. #story 1770.20111123
	 * 通过此方法，查询出类似“001,002”以逗号分隔开的组合群代号所代表的组合群代码，同样以逗号分隔开
	 * FAssetGroupCode 组合群代码
	 * return String
	 * @throws YssException 
	 */
	
	private String getGroupNameFromGroupCode(String FAssetGroupCode) throws YssException
	{
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		String[] groupCode = null;
		String requestGroupCode = "";
		try
		{
//			groupCode = ("".equals(FAssetGroupCode.trim()) ? pub.getAssetGroupCode() : FAssetGroupCode).split(",");
			groupCode = FAssetGroupCode.split(",");
			for (int i = 0;i<groupCode.length;i++)
			{
				requestGroupCode = requestGroupCode +"'" + groupCode[i] + "',";
			}
			
			strSql = "select * from tb_sys_AssetGroup where FAssetGroupCode in (" + requestGroupCode.substring(0,requestGroupCode.length() - 1) + ")";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
			}
			
			return sReturn;
		
		}
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
}
