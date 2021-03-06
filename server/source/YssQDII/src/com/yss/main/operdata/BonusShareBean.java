package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: BonusShareBean</p>
 * <p>Description: 送股权益数据</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class BonusShareBean
    extends BaseDataSettingBean implements IDataSetting {

    private String SecurityCode = ""; //证券代码
    private String SecurityName = ""; //证券名称
    private String SSecCode = ""; //原证券代码
    private String SSecName = ""; //原证券名称
    private String RecordDate = ""; //权益确认日 xuqiji 20090716
    private String ExrightDate = ""; //权益除权日

    private String RoundCode = ""; //舍入代码
    private String RoundName = ""; //舍入名称
    private String Desc = ""; //送股权益描述
    private String AfficheDate = ""; //公告日；
    private String PayDate = ""; //到帐日；

    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    private String PreTaxRatio = "0"; //税前权益比例
    private String AfterTaxRatio = "0"; //税后权益比例
    private String PortCode = ""; //组合代码
    private String PortName = ""; //组合名称
    private String AssetGroupCode = ""; //组合群代码
    private String AssetGroupName = "";	//组合群名称		//added by liubo.Story #1770
	private String OldAssetGroupCode = ""; //组合群代码
    private String OldPortCode = ""; //组合代码
    //----------------------------end-----------------------------//

    private String strIsOnlyColumns = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串

    private String oldSecurityCode = "";
    private String oldRecordDate = "";
    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    private String oldPayDate="";
    //----------------------------------------------
    /**shashijie 2011-09-08 STORY 1561 */
    private String FTaxRate = "0";//税率
    /**end*/
    //---add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A start---//
    private String costOddRate = "0";//成本剩余比例  
    
    public String getCostOddRate() {
		return costOddRate;
	}

	public void setCostOddRate(String costOddRate) {
		this.costOddRate = costOddRate;
	}
	//---add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A end---//

	private BonusShareBean filterType;
    //private String SRecycled;
    private String multAuditString = "";    //批量处理数据 xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
    private String Group;//add by baopingping #story 1167 20110722增加前台是否选择了跨组合群操作
    private String Iscopy;////add by baopingping #story 1167 20110722增加前台是否 进行覆盖操作
    private boolean  edti=false;
	public String getIscopy() {
		return Iscopy;
	}

	public void setIscopy(String iscopy) {
		Iscopy = iscopy;
	}

	private SingleLogOper logOper;
    public String getGroup() {
		return Group;
	}

	public void setGroup(String group) {
		Group = group;
	}

    public String getAssetGroupName() {
		return AssetGroupName;
	}

	public void setAssetGroupName(String assetGroupName) {
		AssetGroupName = assetGroupName;
	}

	public BonusShareBean() {
    }

    /**
     * parseRowStr
     * 解析送股权益数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
        try {
        	//xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
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
            this.SecurityCode = reqAry[0];
            this.SecurityName = reqAry[1];
            this.SSecCode = reqAry[2];
            this.SSecName = reqAry[3];
            this.RecordDate = reqAry[4];
            this.ExrightDate = reqAry[5];
            this.AfficheDate = reqAry[6]; //add code;
            this.PayDate = reqAry[7]; //add  code
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            this.PreTaxRatio = reqAry[8];
            this.AfterTaxRatio=reqAry[9];

            this.RoundCode = reqAry[10];
            this.RoundName = reqAry[11];

            this.PortCode = reqAry[12];
            this.OldPortCode= reqAry[13];
            this.AssetGroupCode=reqAry[14];
            this.AssetGroupName = reqAry[15];
            this.OldAssetGroupCode=reqAry[16];
            this.Desc = reqAry[17];
            super.checkStateId = Integer.parseInt(reqAry[18]);
            this.oldSecurityCode = reqAry[19];
            this.oldRecordDate = reqAry[20];

            this.strIsOnlyColumns = reqAry[21];
            //---------------------end----------------------//
            //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
            this.oldPayDate=reqAry[22];
            this.Group=reqAry[23];//add by baopingping #story 1167 20110718  是否进行跨组合操作
            this.Iscopy=reqAry[24];//add by baopingping #story 1167 20110718  是否进行覆盖
            /**shashijie 2011-09-08 STORY 1561 */
            this.FTaxRate = reqAry[25];//税率
            /**end*/
            //add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A
            this.costOddRate = reqAry[26];//成本剩余比例
			super.parseRecLog();
            //--------------------------------------
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new BonusShareBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析送股权益数据信息出错", e);
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.SecurityCode).append("\t");
        buf.append(this.SecurityName).append("\t");
        buf.append(this.RecordDate).append("\t");
        buf.append(this.ExrightDate).append("\t");
        buf.append(this.AfficheDate).append("\t"); //add code;4
        buf.append(this.PayDate).append("\t"); //add code;5
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        buf.append(this.PreTaxRatio).append("\t");
        buf.append(this.AfterTaxRatio).append("\t");
        //--------------------end-------------------//
        buf.append(this.RoundCode).append("\t");
        buf.append(this.RoundName).append("\t");
        buf.append(this.Desc).append("\t");
        buf.append(this.SSecCode).append("\t");
        buf.append(this.SSecName).append("\t"); // sum(11)
        buf.append(this.AssetGroupCode).append("\t");
        buf.append(this.AssetGroupName).append("\t");	//added by liubo.Story #1770
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.FTaxRate).append("\t");//税率
        //add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A
        buf.append(this.costOddRate).append("\t");//成本剩余比例
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * addOperData
     * 新增送股权益数据
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + pub.yssGetTableName("Tb_Data_BonusShare") +
                "(FTSecurityCode,FSSecurityCode,FRecordDate,FExRightDate,FAfficheDate,FPayDate,"+
                "FPreTaxRatio,FAfterTaxRatio,FRoundCode,FAssetGroupCode,FPortCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser" +
                /**shashijie 2011-09-08 STORY 1561*/
                ",FTaxRate, FCostOddRate)" +//edit by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A 添加 FCostOddRate
                /**end*/
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlString(this.SSecCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.ExrightDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio+","+
                this.AfterTaxRatio+","+
                //-------------------------end--------------//
                dbl.sqlString(this.RoundCode) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                dbl.sqlString(this.AssetGroupCode.trim().length() == 0 ? " " : this.AssetGroupCode.trim()) + "," +
                dbl.sqlString(this.PortCode.trim().length() == 0 ? " " : this.PortCode.trim()) + "," +
                //-------------------------end-----------------------//
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                /**shashijie 2011-09-08 STORY 1561*/
                ","+this.FTaxRate+ "," + this.costOddRate + //edit by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A 添加 costOddRate
                /**end*/
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增送股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 检查输入是否合法
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
    	//modified by liubo.Story #1770
    	//===========================
//        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_BonusShare"),
//			"FTSecurityCode,FRecordDate,FPortCode,FPayDate",
//			//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
//			this.SecurityCode + "," + this.RecordDate + "," + 
//			(this.PortCode.length() == 0 ? " " :this.PortCode)
//			//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//			//,
//			+","+this.PayDate,
//			//-------------------------------------
//			this.oldSecurityCode + "," + this.oldRecordDate + "," + 
//			(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
//			//---------------------------end---------------------//
//			//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
//			+","+this.oldPayDate);
			//-----
    	//=============end==============
        
		String[] sAssetGroup = null;
		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
		boolean noAssetTable = false;
		String errorInfo = "";
		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
		if("".equals(AssetGroupCode.trim())){
    		dbFun.checkInputCommon(btOper,
    					pub.yssGetTableName("Tb_Data_BonusShare"),
    					"FTSecurityCode,FRecordDate,FPortCode,FPayDate",
    					this.SecurityCode + "," + this.RecordDate + "," + 
    					(this.PortCode.length() == 0 ? " " :this.PortCode)
    					//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    					//,
    					+","+this.PayDate,
    					//-------------------------------------
    					this.oldSecurityCode + "," + this.oldRecordDate + "," + 
    					(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
    					//---------------------------end---------------------//
    					//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    					+","+this.oldPayDate);
		}else{
			sAssetGroup =  AssetGroupCode.split(",");
    		for (int i = 0;i < sAssetGroup.length;i++)
    		{
    	    	try
    	    	{
    	    		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
    	    		if(!dbl.yssTableExist("Tb_" + sAssetGroup[i] + "_Data_BonusShare")){
    	    			noAssetTable = true;
    	    			errorInfo = "组合群【" + sAssetGroup[i] + "】相关表未创建！";
    	    		}
    	    		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
    	    		
    	    		dbFun.checkInputCommon(btOper,
    	    				"Tb_" + sAssetGroup[i] + "_Data_BonusShare",
    	    				"FTSecurityCode,FRecordDate,FPortCode,FPayDate",
    	    				this.SecurityCode + "," + this.RecordDate + "," + 
    	    				(this.PortCode.length() == 0 ? " " :this.PortCode)
    	    				//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    	    				//,
    	    				+","+this.PayDate,
    	    				//-------------------------------------
    	    				this.oldSecurityCode + "," + this.oldRecordDate + "," + 
    	    				(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
    	    				//---------------------------end---------------------//
    	    				//-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    	    				+","+this.oldPayDate);

    	    	}
    	    	catch(Exception e)
    	    	{
    	    		//---edit by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
    	    		if(noAssetTable){
        	    		throw new YssException(errorInfo);
    	    		}else{
    	    			if(this.PortCode.length() == 0){
            	    		throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券代码为【" + this.SecurityCode + 
            	    				"】,权益登记日为【" + RecordDate + "】" +"，到帐日为【" + this.PayDate + "】的数据！");
    	    			}else{
            	    		throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券代码为【" + this.SecurityCode + "】,权益登记日为【" + RecordDate + "】" +
        	    					"，投资组合为【"+ (this.PortCode.length() == 0 ? " " :this.PortCode) +"】，到帐日为【" + this.PayDate + "】的数据！");
    	    			}
    	    		}
    	    		//---edit by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
    	    	}
    		}
		}
    }

    /**
     * 修改时间：2008年3月27号
     * 修改人：单亮
     * 原方法功能：只能处理送股权益业务的审核和未审核的单条信息。
     * 新方法功能：可以处理送股权益业务审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * 新方法功能：可以处理送股权益业务审核、未审核、和回收站的还原功能、还可以同时处理多条信息
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
            if (sRecycled!= null&&(!sRecycled.equalsIgnoreCase(""))) {
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
		                    strSql = "update Tb_" + assetGroupCodes[j] + "_Data_BonusShare" +
	                        " set FCheckState = " + this.checkStateId + 
	                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                        ", FCheckTime = " +dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + 
	                        " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate)+
	                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
	                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                        //------------------------end----------------//
	                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02 
	                        " and FPAYDATE = "+dbl.sqlDate(this.PayDate);

		                    dbl.executeSql(strSql);							
						}
					}else{
	                    strSql = "update " + pub.yssGetTableName("Tb_Data_BonusShare") +
                        " set FCheckState = " + this.checkStateId + 
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = " +dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + 
                        " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate)+
                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                        //------------------------end----------------//
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02 
                        " and FPAYDATE = "+dbl.sqlDate(this.PayDate);

	                    dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //如果sRecycled为空，而SecurityCode不为空，则按照SecurityCode来执行sql语句
            else if (SecurityCode!= null&&(!SecurityCode.equalsIgnoreCase("")) ) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		                strSql = "update Tb_" + assetGroupCodes[j] + "_Data_BonusShare" +
	                    " set FCheckState = " + this.checkStateId + 
	                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + 
	                    " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
	                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                    " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
		                
		                dbl.executeSql(strSql);						
					}
				}else{
	                strSql = "update " + pub.yssGetTableName("Tb_Data_BonusShare") +
                    " set FCheckState = " + this.checkStateId + 
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + 
                    " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") + 
                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                    " and FPAYDATE = "+dbl.sqlDate(this.PayDate);
	                
	                dbl.executeSql(strSql);					
				}  
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核送股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delOperData
     * 删除送股权益数据,即将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_BonusShare") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                //------------------------end----------------//
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +"and FPayDate="+dbl.sqlDate(this.PayDate)
                //----------------------------------
                ;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除送股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editOperData
     * 修改送股权益数据
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_BonusShare") +
                " set FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                ",FSSecurityCode = " + dbl.sqlString(this.SSecCode) +
                ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                ",FExRightDate = " + dbl.sqlDate(this.ExrightDate) +
                ",FAfficheDate =" + dbl.sqlDate(this.AfficheDate) +
                ",FPayDate = " + dbl.sqlDate(this.PayDate) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                ",FPreTaxRatio = " + this.PreTaxRatio +
                ",FAfterTaxRatio ="+this.AfterTaxRatio+
                //---------------------end-------------------------//
                ",FRoundCode = " + dbl.sqlString(this.RoundCode) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                ",FAssetGroupCode = " + dbl.sqlString(this.AssetGroupCode.trim().length() == 0 ? " " : this.AssetGroupCode.trim()) +
                ",FPortCode = " + dbl.sqlString(this.PortCode.trim().length() == 0 ? " " : this.PortCode.trim()) +
                //------------------------end------------------------//
                ",FDesc = " + dbl.sqlString(this.Desc) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                /**shashijie 2011-09-08 STORY 1561*/
                " ,FTaxRate = "+this.FTaxRate +
                /**end*/
                " ,FCostOddRate = " + this.costOddRate + //add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A 更新 FCostOddRate
                " where FTSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate)+
                " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) 
                
                //---------------------------end---------------------//
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +"and FPayDate= "+dbl.sqlDate(this.oldPayDate)
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
            throw new YssException("修改送股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * buildFilterSql
     * 筛选条件
     * @throws YssException
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
            if (this.filterType.SecurityCode.length() != 0) {
                sResult = sResult + " and a.FTSecurityCode like '" +
                    filterType.SecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.SSecCode.length() != 0) {
                sResult = sResult + " and a.FSSecurityCode like '" +
                    filterType.SSecCode.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.RecordDate.length() != 0 &&
                !this.filterType.RecordDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FRecordDate = " +
                    dbl.sqlDate(filterType.RecordDate);
            }
            
            /**shashijie 2011-09-07 STORY 1561 */
            if (!this.filterType.FTaxRate.equals("0")) {//税率
                sResult = sResult + " and a.FTaxRate like '" +
                    filterType.FTaxRate.replaceAll("'", "''") + "%'";
            }
			
            /**end*/
            
            if (this.filterType.ExrightDate.length() != 0 &&
                !this.filterType.ExrightDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FExRightDate = " +
                    dbl.sqlDate(filterType.ExrightDate);
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
            //-----------------------------end---------------------------//
            
            //---add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A start---//
            if (!this.filterType.costOddRate.equals("0")) {
                sResult = sResult + " and a.FCostOddRate = " + this.filterType.costOddRate;
            }
            //---add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A end---//
            
            if (this.filterType.RoundCode.length() != 0) {
                sResult = sResult + " and a.FRoundCode like '" +
                    filterType.RoundCode.replaceAll("'", "''") + "%'";
            }
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            if (this.filterType.AssetGroupCode.length() != 0) {
                sResult = sResult + " and a.FAssetGroupCode like '" +
                    filterType.AssetGroupCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.PortCode.length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.PortCode.replaceAll("'", "''") + "%'";
            }
            //----------------------end------------------------------//
            if (this.filterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.Desc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.AfficheDate.length() != 0 &&
                !this.filterType.AfficheDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FAfficheDate = " +
                    dbl.sqlDate(filterType.AfficheDate);
            }
            if (this.filterType.PayDate.length() != 0 &&
                !this.filterType.PayDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FPayDate = " + dbl.sqlDate(filterType.PayDate);
            }

        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取送股权益数据
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
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FSecurityName as FSecurityName,d.FIsinCode as FIsinCode,f.FSecurityName as FSSecName" + //add by xuqiji 20090414 :QDV4南方2009年04月01日01_AB MS00361    南方要求权益信息中显示证券的ISIN码
                ",e.FRoundName as FRoundName ,h.FPortName as FPortName ,ass.FAssetGroupName as FAssetGroupName" + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_BonusShare") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode" +	//added by liubo.Story #1770
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName,o.FIsinCode as FIsinCode from " + //add by xuqiji 20090414:QDV4南方2009年04月01日01_AB  MS00361    南方要求权益信息中显示证券的ISIN码
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " +
                " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FTSecurityCode = d.FSecurityCode" +
                " left join (select o1.FSecurityCode as FSecurityCode,o1.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o1 join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p1 " +
                " on o1.FSecurityCode = p1.FSecurityCode and o1.FStartDate = p1.FStartDate) f on a.FSSecurityCode = f.FSecurityCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select * from " +
                pub.yssGetTableName("tb_para_portfolio") + " where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //-------------------------------end-------------------------------//
                buildFilterSql() +
                
                //modified by liubo.Story #1770
                //==================================
//                (this.filterType.AssetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='" + pub.getAssetGroupCode() + "')" :"")+//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                (this.filterType.AssetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')" :"")+//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                //=================end=================
                
                " order by a.FCheckState, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("BonusShare");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName)).
                    append(YssCons.YSS_LINESPLITMARK);

				//============end====================
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
            throw new YssException("获取送股权益业务数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * getListViewData2
     * 获取已审核的送股权益数据
     * @return String
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
            sHeader = "证券品种\t权益登记日\t除权日\t描述说明";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
            "d.FSecurityName as FSecurityName,f.FSecurityName as FSSecName" +
            ",e.FRoundName as FRoundName,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            pub.yssGetTableName("Tb_Data_BonusShare") + " a " +
            " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") + " o join " +
            "(select FSecurityCode,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) p " +
            " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FTSecurityCode = d.FSecurityCode" +
            " left join (select o1.FSecurityCode as FSecurityCode,o1.FSecurityName as FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") + " o1 join " +
            "(select FSecurityCode,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) p1 " +
            " on o1.FSecurityCode = p1.FSecurityCode and o1.FStartDate = p1.FStartDate) f on a.FSSecurityCode = f.FSecurityCode" +
            " left join (select FRoundCode,FRoundName from " +
            pub.yssGetTableName("Tb_Para_Rounding") +
            " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("tb_para_portfolio") + " " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//            " and FCheckState = 1 group by FPortCode )u " +
//            " join (select * from " +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            "  v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " where FCheckState = 1 "+
            
            //modified by liubo.Story #1770
            //=======================================
            //" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE='" + pub.getAssetGroupCode() + "')"+
            " and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')"+
            //===============end=======================
            "order by a.FCheckState, a.FCreateTime desc";
                //-------------------------------end-------------------------------//
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FRecordDate"))).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FExRightDate"))).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).
                    append(YssCons.YSS_LINESPLITMARK);

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
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取送股权益业务数据出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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
     * saveMutliOperData
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliOperData(String sMutilRowStr) {
        return "";
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.SecurityCode = rs.getString("FTSecurityCode") + "";
        this.SecurityName = rs.getString("FSecurityName") + "";
        this.SSecCode = rs.getString("FSSecurityCode") + "";
        this.SSecName = rs.getString("FSSecName") + "";
        this.RecordDate = rs.getDate("FRecordDate") + "";
        this.ExrightDate = rs.getDate("FExRightDate") + "";
        if (rs.getDate("FAfficheDate") == null) {
            AfficheDate = "1900-01-01";
        } else {
            this.AfficheDate = rs.getString("FAfficheDate") + ""; //add code;
        }
        if (rs.getDate("FPayDate") == null) {
            PayDate = "1900-01-01";
        } else {
            //this.PayDate = rs.getString("FPayDate") + ""; //add code;
            //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
            this.PayDate = rs.getDate("FPayDate") + "";
            //----------------------------------------------------------
        }
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        this.PortCode = rs.getString("FPortCode") + "";
        this.PortName = rs.getString("FPortName") + "";
        this.AssetGroupCode = rs.getString("FASSETGROUPCODE");
        this.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
        this.AfterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
        //-------------------------end--------------------//
        this.RoundCode = rs.getString("FRoundCode") + "";
        this.RoundName = rs.getString("FRoundName") + "";
        this.Desc = rs.getString("FDesc") + "";
        this.AssetGroupName  = rs.getString("FAssetGroupName");	//added by liubo.Story #1770
        /**shashijie 2011-09-07 STORY 1561 */
        this.FTaxRate = rs.getBigDecimal("FTaxRate")==null? "0" : rs.getBigDecimal("FTaxRate").toString() + "";//税率
        /**end*/
        //add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A
        this.costOddRate = rs.getDouble("FCostOddRate") + "";//成本剩余比例
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
    		MarketValueBean Mark=new MarketValueBean();
    		Mark.setYssPub(pub);
			if (sType.equalsIgnoreCase("multauditTradeSub")) {
				if (multAuditString.length() > 0) {
					// modify baopingping #story 1167 20110720 添加对跨组合群的处理
					if (this.Group.equalsIgnoreCase("ok")) {
						String FAssetGroupCode = Mark.getAssdeGroup();
						String[] GroupCode = null;
						if (FAssetGroupCode != null) {
							GroupCode = FAssetGroupCode.split("\t");
							for (int i = 0; i < GroupCode.length; i++) {
								this.auditData(this.multAuditString,
										GroupCode[i]);// 跨组合执行批量审核/反审核
							}
						}

					} else {// ---------end----------
						return this.auditMutli(this.multAuditString); // 执行批量审核/反审核/删除
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
        BonusShareBean befEditBean = new BonusShareBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName" +
                ",f.FSecurityName as FSSecName,e.FRoundName as FRoundName,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_BonusShare") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " +
                " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FTSecurityCode = d.FSecurityCode" +

                " left join (select o1.FSecurityCode as FSecurityCode,o1.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o1 join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p1 " +
                " on o1.FSecurityCode = p1.FSecurityCode and o1.FStartDate = p1.FStartDate) f on a.FSSecurityCode = f.FSecurityCode" +

                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " select * from " +
                pub.yssGetTableName("tb_para_portfolio") + " where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                //----edit by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //-------------------------------end-------------------------------//
                " where  a.FTSecurityCode =" + dbl.sqlString(this.oldSecurityCode) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " and a.FRecordDate=" + dbl.sqlDate(YssFun.toDate(this.oldRecordDate))+
                (this.OldPortCode.length() > 0 ? "and a.FPortCode=" + dbl.sqlString(this.OldPortCode) : "and 1=1");
                //-------------------end-----------------------//
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

                befEditBean.SecurityCode = rs.getString("FTSecurityCode") + "";
                befEditBean.SecurityName = rs.getString("FSecurityName") + "";
                befEditBean.SSecCode = rs.getString("FSSecurityCode") + "";
                befEditBean.SSecName = rs.getString("FSSecName") + "";
                befEditBean.RecordDate = rs.getDate("FRecordDate") + "";
                befEditBean.ExrightDate = rs.getDate("FExRightDate") + "";
                befEditBean.AfficheDate = rs.getDate("FAfficheDate") + ""; //add code;
                befEditBean.PayDate = rs.getDate("FPayDate") + ""; //add code;

                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                befEditBean.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
                befEditBean.AfterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
                befEditBean.PortCode = rs.getString("FPortCode") + "";
                befEditBean.PortName = rs.getString("FPortName") + "";
                //-------------------------end----------------------------//

                befEditBean.RoundCode = rs.getString("FRoundCode") + "";
                befEditBean.RoundName = rs.getString("FRoundName") + "";
                befEditBean.Desc = rs.getString("FDesc") + "";
                /**shashijie 2011-09-08 STORY 1561*/
                befEditBean.FTaxRate = rs.getBigDecimal("FTaxRate")==null? "0" : 
                	rs.getBigDecimal("FTaxRate").toString() + "";//税率
                /**end*/
                //add by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A
                befEditBean.costOddRate = rs.getDouble("FCostOddRate") + "";
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
	 * saveMutliSetting
	 * add baopingping 20110715 #story 1167
	 * 跨组合 添加，删除，修改，复制，审核的入口
	 * @param sMutilRowStr
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
				if(this.Iscopy.equalsIgnoreCase("yes"))
				{
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
				edti=true;
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
    }

    
    public String getAllSetting()  {
    	return "";
    }

    /**
     *
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
		                    strSql = "delete from Tb_" + assetGroupCodes[j] + "_Data_BonusShare " +
	                        " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
	                        " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                        " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
	                        //--------------------------------end----------------------------//
	                    	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                        +"and FPAYDATE="+dbl.sqlDate(this.PayDate);
	                        //-----------------------
	                    	
		                    //执行sql语句
		                    dbl.executeSql(strSql);							
						}
					}else{
	                    strSql = "delete from " + pub.yssGetTableName("Tb_Data_BonusShare") + 
	                    " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                        " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                        //--------------------------------end----------------------------//
                    	//add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        +"and FPAYDATE = "+dbl.sqlDate(this.PayDate);
                        //-----------------------
                    	
	                    //执行sql语句
	                    dbl.executeSql(strSql);						
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //sRecycled如果sRecycled为空，而SecurityCode不为空，则按照SecurityCode来执行sql语句
            else if (SecurityCode != "" && SecurityCode != null) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		                strSql = "delete from Tb_" + assetGroupCodes[j] + "_Data_BonusShare " +
	                    " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
	                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
	                    //--------------------------------end----------------------------//
	                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                    +"and FPAYDATE = "+dbl.sqlDate(this.PayDate);
	                    
		                //执行sql语句
		                dbl.executeSql(strSql);						
					}
				}else{
	                strSql = "delete from " + pub.yssGetTableName("Tb_Data_BonusShare") +
                    " where FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                    //--------------------------------end----------------------------//
                    //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                    +"and FPAYDATE = "+dbl.sqlDate(this.PayDate);
                    
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
    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    public String getAssetGroupCode() {
        return AssetGroupCode;
    }

    public void setAssetGroupCode(String AssetGroupCode) {
        this.AssetGroupCode = AssetGroupCode;
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

    public String getAfterTaxRatio() {
        return AfterTaxRatio;
    }

    public void setAfterTaxRatio(String AfterTaxRatio) {
        this.AfterTaxRatio = AfterTaxRatio;
    }

    public String getPreTaxRatio() {
        return PreTaxRatio;
    }

    public void setPreTaxRatio(String PreTaxRatio) {
        this.PreTaxRatio = PreTaxRatio;
    }

    public String getOldAssetGroupCode() {
        return OldAssetGroupCode;
    }

    public String getOldPortCode() {
        return OldPortCode;
    }

    public void setOldAssetGroupCode(String OldAssetGroupCode) {
        this.OldAssetGroupCode = OldAssetGroupCode;
    }

    public void setOldPortCode(String OldPortCode) {
        this.OldPortCode = OldPortCode;
    }
    //----------------------------end----------------------------//
    public String getAfficheDate() {
        return AfficheDate;
    }

    public void setAfficheDate(String AfficheDate) {
        this.AfficheDate = AfficheDate;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public BonusShareBean getFilterType() {
        return filterType;
    }

    public String getOldRecordDate() {
        return oldRecordDate;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
    }

    public String getExrightDate() {
        return ExrightDate;
    }

    public String getSSecName() {
        return SSecName;
    }

    public String getSSecCode() {
        return SSecCode;
    }

    public String getSecurityName() {
        return SecurityName;
    }

    public String getSecurityCode() {
        return SecurityCode;
    }

    public String getRoundCode() {
        return RoundCode;
    }

    public String getRoundName() {
        return RoundName;
    }

    public String getRecordDate() {
        return RecordDate;
    }

    public String getPayDate() {
        return PayDate;
    }

    public void setFilterType(BonusShareBean filterType) {
        this.filterType = filterType;
    }

    public void setOldRecordDate(String oldRecordDate) {
        this.oldRecordDate = oldRecordDate;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setExrightDate(String ExrightDate) {
        this.ExrightDate = ExrightDate;
    }

    public void setSSecName(String SSecName) {
        this.SSecName = SSecName;
    }

    public void setSSecCode(String SSecCode) {
        this.SSecCode = SSecCode;
    }

    public void setSecurityName(String SecurityName) {
        this.SecurityName = SecurityName;
    }

    public void setSecurityCode(String SecurityCode) {
        this.SecurityCode = SecurityCode;
    }

    public void setRoundName(String RoundName) {
        this.RoundName = RoundName;
    }

    public void setRoundCode(String RoundCode) {
        this.RoundCode = RoundCode;
    }

    public void setRecordDate(String RecordDate) {
        this.RecordDate = RecordDate;
    }

    public void setPayDate(String PayDate) {
        this.PayDate = PayDate;
    }
    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	public String getOldPayDate() {
		return oldPayDate;
	}

	public void setOldPayDate(String oldPayDate) {
		this.oldPayDate = oldPayDate;
	}
	//---------------
    
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
        BonusShareBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_BonusShare") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FTSecurityCode = ? and FRecordDate = ? and FPortCode= ?"
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +" and FPayDate= ?"
                //--------------
                ;

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new BonusShareBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        psmt.setDate(4, YssFun.toSqlDate(data.getPayDate()));
                        //----------------------------
                        psmt.addBatch();
                        //---增加批量删除的日志记录功能----guojianhua add 20100907-------//
                        logOper = SingleLogOper.getInstance();
                        data=this;
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
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 */
    private String addData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "insert into " + ("Tb_"+FAssetGroupCode+"_Data_BonusShare")+
                "(FTSecurityCode,FSSecurityCode,FRecordDate,FExRightDate,FAfficheDate,FPayDate,"+
                "FPreTaxRatio,FAfterTaxRatio,FRoundCode,FAssetGroupCode,FPortCode,FDesc,FCheckState," +
                "FCreator,FCreateTime,FCheckUser" +
                /**shashijie 2011-09-08 STORY 1561 */
                " ,FTaxRate, FCostOddRate) " +//edit by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A 添加 FCostOddRate
                /**end*/
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlString(this.SSecCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.ExrightDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio+","+
                this.AfterTaxRatio+","+
                //-------------------------end--------------//
                dbl.sqlString(this.RoundCode) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                dbl.sqlString(this.AssetGroupCode.trim().length() == 0 ? " " : this.AssetGroupCode.trim()) + "," +
                dbl.sqlString(this.PortCode.trim().length() == 0 ? " " : this.PortCode.trim()) + "," +
                //-------------------------end-----------------------//
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                /**shashijie 2011-09-08 STORY 1561 */
                " , "+this.FTaxRate + " , " + this.costOddRate + //edit by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A 添加 costOddRate
                /**end*/
                ")";            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增送股权益业务数据出错", e);
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
         boolean bTrans = false; //代表是否开始了事务
         String strSql = "";
         String[] sOldAssetGroup = null;
         String[] sNewAssetGroup = null;
 		 String sTmpAssetGroupCode = "";
         try {
        	 if (!OldAssetGroupCode.equals(AssetGroupCode))
        	 {
        		 sOldAssetGroup = ("".equals(OldAssetGroupCode.trim()) ? pub.getAssetGroupCode() : OldAssetGroupCode).split(",");
        		 sNewAssetGroup = ("".equals(AssetGroupCode.trim()) ? pub.getAssetGroupCode() : AssetGroupCode).split(",");
        		 
        		 for (int i = 0;i < sOldAssetGroup.length;i++)
        		 {
		        	 strSql = "delete from " +
		             "Tb_" + sOldAssetGroup[i] + "_Data_BonusShare" +
		             " where FTSecurityCode = " +
		             dbl.sqlString(this.SecurityCode) +
		             " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
		             " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
		             " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
		             +"and FPAYDATE="+dbl.sqlDate(this.oldPayDate);
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
	                 "update " + ("Tb_"+FAssetGroupCode+"_Data_BonusShare") +
	                 " set FTSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                 ",FSSecurityCode = " + dbl.sqlString(this.SSecCode) +
	                 ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
	                 ",FExRightDate = " + dbl.sqlDate(this.ExrightDate) +
	                 ",FAfficheDate =" + dbl.sqlDate(this.AfficheDate) +
	                 ",FPayDate = " + dbl.sqlDate(this.PayDate) +
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                 ",FPreTaxRatio = " + this.PreTaxRatio +
	                 ",FAfterTaxRatio ="+this.AfterTaxRatio+
	                 //---------------------end-------------------------//
	                 ",FRoundCode = " + dbl.sqlString(this.RoundCode) +
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                 ",FAssetGroupCode = " + dbl.sqlString(this.AssetGroupCode.trim().length() == 0 ? " " : this.AssetGroupCode.trim()) +
	                 ",FPortCode = " + dbl.sqlString(this.PortCode.trim().length() == 0 ? " " : this.PortCode.trim()) +
	                 //------------------------end------------------------//
	                 ",FDesc = " + dbl.sqlString(this.Desc) +
	                 ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
	                 ",FCreator = " + dbl.sqlString(this.creatorCode) +
	                 ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
	                 ",FCheckUser = " +
	                 (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                 /**shashijie 2011-09-08 STORY 1561*/
	                 //edit by songjie 2012.03.27 STORY #2320 QDV4长信基金2012年02月28日01_A
	                 " ,FTaxRate = "+this.FTaxRate+ " ,FCostOddRate = " + this.costOddRate + 
	                 /**end*/
	                 
	                 //modified by liubo.Story #1770
	                 //==================================
	                 " where FTSecurityCode = " +(edti?dbl.sqlString(this.oldSecurityCode): dbl.sqlString(this.SecurityCode) )+
	                  //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                 " and FRecordDate = " + (edti?dbl.sqlDate(this.oldRecordDate): dbl.sqlDate(this.RecordDate))+
	                 " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : (edti?this.OldPortCode:this.PortCode)) +
	                 " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
	                 //---------------------------end---------------------//
	                 //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
	                 +"and FPayDate= "+(edti?dbl.sqlDate(this.oldPayDate):(dbl.sqlDate(this.PayDate))) +
	                 " and FCheckState='0'"
	                 //----------------------------------
	                 //==================end================
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
             throw new YssException("修改送股权益业务数据出错", e);
         } finally {
             dbl.endTransFinal(conn, bTrans);
         }
		
	}
    
    /**
	 * add baopingping #story 1167 20110717
	 * 删除 ，审核，反审核 所有组合群下所选数据主键相同的行情数据
	 * return ResultSet
     * @throws YssException 
	 */
    private String auditData(String sMutilRowStr, String FAssetGroupCode) throws YssException {
    	boolean checkTrue=false;
    	if(this.checkStateId==2)
    	{
    		 checkTrue=true;
    	}
    	Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        BonusShareBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
            if(!dbl.yssTableExist("Tb_"+FAssetGroupCode+"_Data_BonusShare")){
            	return "";
            }
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
            
            sqlStr = "update " +("Tb_"+FAssetGroupCode+"_Data_BonusShare") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FTSecurityCode = ? and FRecordDate = ? and FPortCode= ?"
                //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                +" and FPayDate= ?" +(checkTrue ? " and FCheckState='0'" : " ")+"";//add by baopingping #story 1167 20110720 只删除未审核的数据 
                //--------------
                ;

            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new BonusShareBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        //add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                        psmt.setDate(4, YssFun.toSqlDate(data.getPayDate()));
                        //----------------------------
                        psmt.addBatch();
                        //---增加批量删除的日志记录功能----guojianhua add 20100907-------//
                        logOper = SingleLogOper.getInstance();
                        data=this;
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
            throw new YssException("批量处理数据出错!", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
		
	}
    
    /**
	 * add baopingping #story 1167 20110717
	 * 给所有组合群下添加一条行情数据
	 * return ResultSet
	 */
    private String copyData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        try {
            strSql =
                "insert into " + ("Tb_"+FAssetGroupCode+"_Data_BonusShare")+
                "(FTSecurityCode,FSSecurityCode,FRecordDate,FExRightDate,FAfficheDate,FPayDate,"+
                "FPreTaxRatio,FAfterTaxRatio,FRoundCode,FAssetGroupCode,FPortCode,FDesc,FCheckState," +
                "FCreator,FCreateTime,FCheckUser" +
                /**shashijie 2011-09-08 STORY 1561 */
                " ,FTaxRate ) " +
                /**end*/
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlString(this.SSecCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.ExrightDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                dbl.sqlDate(this.PayDate) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                this.PreTaxRatio+","+
                this.AfterTaxRatio+","+
                //-------------------------end--------------//
                dbl.sqlString(this.RoundCode) + "," +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                dbl.sqlString(this.AssetGroupCode.trim().length() == 0 ? " " : this.AssetGroupCode.trim()) + "," +
                dbl.sqlString(this.PortCode.trim().length() == 0 ? " " : this.PortCode.trim()) + "," +
                //-------------------------end-----------------------//
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "0") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                /**shashijie 2011-09-08 STORY 1561 */
                " , "+this.FTaxRate+
                /**end*/
                ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("复制送股权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}
    
    
    /**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存在这个组合数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getTable(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="none";
		try{
			sql="select * from TB_"+TableName+"_Data_BonusShare where FTSecurityCode='"+
			this.SecurityCode+"'and FPayDate="+dbl.sqlDate(this.PayDate)+" and " +
			"FRecordDate="+dbl.sqlDate(this.RecordDate)+" and " +
			" FPortCode="+ dbl
			.sqlString((this.PortCode.length() == 0) ? " "
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
	 * 查看数据库中是否存在这个组合数据
	 * TableName 组合群代码
	 * return ResultSet
	 * @throws YssException 
	 */
	
	public String getGroup(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="";
		try{
			sql="select * from TB_"+TableName+"_Data_BonusShare where Fcheckstate = 0 and  FTSecurityCode='"+
			this.SecurityCode+"'and FPayDate="+dbl.sqlDate(this.PayDate)+" and " +
			"FRecordDate="+dbl.sqlDate(this.RecordDate)+" and " +
			" FPortCode="+ dbl
			.sqlString((this.PortCode.length() == 0) ? " "
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

	public String getFTaxRate() {
		return FTaxRate;
	}

	public void setFTaxRate(String fTaxRate) {
		FTaxRate = fTaxRate;
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
			
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
		
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
