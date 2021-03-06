package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>Title: DividendBean</p>
 * <p>Description: 分红权益数据</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class DividendBean
    extends BaseDataSettingBean implements IDataSetting {

    private String SecurityCode = ""; //证券代码
    private String SecurityName = ""; //证券名称
    private String RecordDate = ""; //权益确认日 xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    private String DividendDate = ""; //权益除权日
    private String DistributeDate = ""; //权益派息日
    private String AfficheDate = ""; //公告日

    private String RoundCode = ""; //舍入代码
    private String RoundName = ""; //舍入名称
    private String Desc = ""; //分红权益描述
    private String DividentType = ""; //分红类型；
    private String DividentCuryCode = ""; //分红币种代码		彭彪 20071117
    private String DividentCuryName = ""; //分红币种名称

//    private String strisOnlyColumnss = "0"; //在初始登陆时是否只显示列，不查询数据
    private String sRecycled = ""; //保存未解析前的字符串

    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
    private String PreTaxRatio = "0"; //税前权益比例
    private String AfterTaxRatio = "0"; //税后权益比例
    private String PortCode = ""; //组合代码
    private String PortName = ""; //组合名称
    
    //modified by liubo.Story #1770;
    //=================================
    private String AssetGroupCode = ""; //组合群代码
    private String AssetGroupName = "";	//组合群名称
	private String OldAssetGroupCode = ""; //组合群代码
    //=============end====================
    
    private String OldPortCode = ""; //组合代码
    //----------------------------end-----------------------------//
    private String oldSecurityCode = "";
    private String oldRecordDate = "";
    private String OldDividentType = "";
    private String oldCuryCode = "";
    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    private String oldDistributeDate="";
    //-------------------------------end----------------
    private String multAuditString = "";    //批量处理数据 xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能
    private DividendBean filterType;
	private SingleLogOper logOper;
	private boolean isInvest=false; 	//by guyichuan 20110510 STORY #741 是否分红转投
	private String status="";			//by guyichuan 20110510 STORY #741 标记是从筛选还是其它按扭进入的状态
	private String group="";//add by baopingping #story 1167 20110718  是否进行跨组合操作
	private String Iscopy;////add by baopingping #story 1167 20110722增加前台是否 进行覆盖操作
	
	public String getIscopy() {
		return Iscopy;
	}

	public void setIscopy(String iscopy) {
		Iscopy = iscopy;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setDividentType(String DividentType) {
        this.DividentType = DividentType;
    }

    public String getDividentType() {
        return DividentType;
    }

    public String getAssetGroupName() {
    		return AssetGroupName;
    	}

    public void setAssetGroupName(String assetGroupName) {
    	AssetGroupName = assetGroupName;
    }

    public DividendBean() {
    }
    
    //added by liubo.Story #1770
    public String getAssetGroupList() throws YssException
    {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
    	
    	try{

            sHeader = "代码\t名称";
            strSql = "select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
            rs = dbl.queryByPreparedStatement(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FAssetGroupCode")).append("\t");
                bufShow.append(rs.getString("FAssetGroupName")).append("\t");
                
                bufShow.append(YssCons.YSS_LINESPLITMARK);
                
//                bufAll.append(rs.getString("FAssetGroupCode")).append("\t");
//                bufAll.append(rs.getString("FAssetGroupName")).append("\t");
//                bufAll.append(rs.getString("FMaxNum")).append("\t");
//                bufAll.append(rs.getString("FStartDate")).append("\t");
//                bufAll.append(rs.getString("FBaseCury")).append("\t");
//                bufAll.append(rs.getString("FBaseRateSrcCode")).append("\t");
//                bufAll.append(rs.getString("FBaseRateCode")).append("\t");
//                bufAll.append(rs.getBoolean("FLocked")).append("\t");
//                bufAll.append(rs.getString("FDesc")).append("\t").append(YssCons.
//                    YSS_LINESPLITMARK);
                this.AssetGroupCode  = rs.getString("FAssetGroupCode");
                this.AssetGroupName  = rs.getString("FAssetGroupName");
                this.SecurityCode = rs.getString("FAssetGroupCode");
                this.checkStateId = 1;
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
        
    	}
    	catch(Exception e)
    	{
    		throw new YssException("获取组合群信息出错：" + e.getMessage());
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * parseRowStr
     * 解析分红权益数据
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
    	 String[] reqAry = null;
         String sTmpStr = "";
         String sMutiAudit = ""; //xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能
         try {
//         	xuqiji 20100330 MS01043 QDV4中保2010年3月22日01_A 权益信息需要添加批量审核的功能 
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
             this.RecordDate = reqAry[1];
             this.DividendDate = reqAry[2];
             this.DistributeDate = reqAry[3];
             //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
             this.PreTaxRatio = reqAry[4];
             this.AfterTaxRatio=reqAry[5];
             this.RoundCode = reqAry[6];

             this.PortCode=reqAry[7];
             this.OldPortCode=reqAry[8];
             this.AssetGroupCode=reqAry[9];
             this.AssetGroupName=reqAry[10];
             this.OldAssetGroupCode=reqAry[11];

             this.Desc = reqAry[12];
             this.checkStateId = Integer.parseInt(reqAry[13]);
             this.oldSecurityCode = reqAry[14];
             this.oldRecordDate = reqAry[15];
             this.isOnlyColumns = reqAry[16];
             this.AfficheDate = reqAry[17];
             this.DividentType = reqAry[18];
             if (reqAry[17].trim().length() == 0) {
                 this.DividentType = " ";
             }
             this.DividentCuryCode = reqAry[19];
             this.OldDividentType = reqAry[20];
             if(this.OldDividentType==null){
            	 this.OldDividentType="0";
             }
             this.oldCuryCode = reqAry[21];
             //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
             this.oldDistributeDate=reqAry[22];
             //-----------------------end-------------------------//
             this.isInvest=Boolean.valueOf(reqAry[23]).booleanValue();//by guyichuan 20110511 STORY #741
             this.status=reqAry[24];
             this.group=reqAry[25];//add baopingping #story 1167 接收前台传来是否进行跨组合操作
             this.Iscopy=reqAry[26];//add baopingping #story 1167 接收前台传来是否进行覆盖            
             super.parseRecLog();
             if (sRowStr.indexOf("\r\t") >= 0) {
                 if (this.filterType == null) {
                     this.filterType = new DividendBean();
                     this.filterType.setYssPub(pub);
                 }
                 this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
             }
         } catch (Exception e) {
             throw new YssException("解析分红权益数据信息出错", e);
         }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.SecurityCode).append("\t");
        buf.append(this.SecurityName).append("\t");
        buf.append(this.RecordDate).append("\t");
        buf.append(this.DividendDate).append("\t");
        buf.append(this.DistributeDate).append("\t");
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        buf.append(this.PreTaxRatio).append("\t");
        buf.append(this.AfterTaxRatio).append("\t");
        //------------------end------------------------//
        buf.append(this.RoundCode).append("\t");
        buf.append(this.RoundName).append("\t");
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        //modified by liubo.Story #1770
        //==================================
        buf.append(this.AssetGroupCode).append("\t");
        buf.append(this.AssetGroupName).append("\t");
        //============end======================
        //-------------------------end----------------------//
        buf.append(this.Desc).append("\t");
        buf.append(this.isInvest+"").append("\t");   //by guyichuan 20110516 STORY #741
        buf.append(this.AfficheDate).append("\t");
        buf.append(this.DividentType).append("\t");
        buf.append(this.DividentCuryCode).append("\t");
        buf.append(this.DividentCuryName).append("\t");
        
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**
     * addOperData
     * 新增分红权益数据
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                "insert into " + pub.yssGetTableName("Tb_Data_Dividend") +
                "(FSecurityCode,FRecordDate,FDividendDate,FDistributeDate,FAfficheDate,FPreTaxRatio,FAfterTaxRatio,"+
                "FPortCode,FAssetGroupCode,FRoundCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FDivdendType,FCuryCode,FIsInvest)" +
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.DividendDate) + "," +
                dbl.sqlDate(this.DistributeDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                (this.PreTaxRatio.trim().equals("0")?"0":this.PreTaxRatio) + "," +
                (this.AfterTaxRatio.trim().equals("0")?"0":this.AfterTaxRatio)+","+
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------------------end--------------------------------------//
                dbl.sqlString(this.RoundCode) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.DividentType + "" + "," +
                dbl.sqlString(this.DividentCuryCode) +","
                +(this.isInvest==true?1:0)					//by guyichuan 20110511 STORY #741
                + ")"; ////////////
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkInput
     * 检查用户输入是否合法
     * @param btOper byte
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
     */
    public void checkInput(byte btOper) throws YssException {
    	//modified by liubo.Story #1770
    	//================================
    	String[] sAssetGroup = null; 
		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
		boolean noAssetTable = false;
		String errorInfo = "";
		//---add by songjie 2012.02.13 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
    	if("".equals(AssetGroupCode.trim())){ 

    		 dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_Dividend"),
                     "FSecurityCode,FRecordDate,FDivdendType,FCuryCode,FPortCode,FDISTRIBUTEDATE",
                     this.SecurityCode + "," + this.RecordDate + "," + this.DividentType + "," + this.DividentCuryCode+","+
                     (this.PortCode.length() == 0 ? " " :this.PortCode)
                   //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                    // ,
                     +","+this.DistributeDate,
                   //----------------------------------------------------------------------
                     this.oldSecurityCode + "," + this.oldRecordDate + "," + this.OldDividentType + "," + this.oldCuryCode
                     +","+(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
                     //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                     +","+this.oldDistributeDate)
                     ;
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
    				dbFun.checkInputCommon(btOper, "Tb_"+sAssetGroup[i]+"_Data_Dividend",
                               "FSecurityCode,FRecordDate,FDivdendType,FCuryCode,FPortCode,FDISTRIBUTEDATE",
                               this.SecurityCode + "," + this.RecordDate + "," + this.DividentType + "," + this.DividentCuryCode+","+
                               (this.PortCode.length() == 0 ? " " :this.PortCode)
                             //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                              // ,
                               +","+this.DistributeDate,
                             //----------------------------------------------------------------------
                               this.oldSecurityCode + "," + this.oldRecordDate + "," + this.OldDividentType + "," + this.oldCuryCode
                               +","+(this.OldPortCode.length() == 0 ? " " :this.OldPortCode)
                               //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                               +","+this.oldDistributeDate)
                               ;
    				//================end================
   
    			}catch(Exception e){ 
    				if(noAssetTable){
    				throw new YssException(errorInfo);
    				}else{
    					if(this.PortCode.length() == 0){
    						throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券品种为【" +  
    						  this.SecurityCode + "】,权益登记日为【" + this.RecordDate + 
    						  "】,分红类型为【" + this.DividentType + "】,分红货币为【" + 
    						  this.DividentCuryCode +"】,到帐日为【"+this.DistributeDate + "】的数据！"); 
    					}else{
    						throw new YssException("组合群【" + sAssetGroup[i] + "】已存在证券品种为【" +  
    						this.SecurityCode + "】,权益登记日为【" + this.RecordDate + 
    						"】,分红类型为【" + this.DividentType + "】,分红货币为【" + 
    						this.DividentCuryCode +"】,投资组合为【"+
    						(this.PortCode.length() == 0 ? " " :this.PortCode)
                            +"】,到帐日为【"+this.DistributeDate + "】的数据！"); 
    					}
    				} 
    			}
    		}
    	}
    }

    /**
     * 修改时间：2008年3月28号
     * 修改人：单亮
     * 原方法功能：只能处理分红权益业务的审核和未审核的单条信息。
     * 新方法功能：可以处理分红权益业务审核、未审核、和回收站的还原功能、还可以同时处理多条信息
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
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
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
		                    strSql = " update Tb_" + assetGroupCodes[j] + "_Data_Dividend" +
	                        " set FCheckState = " + this.checkStateId +
	                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                        ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
	                        " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
	                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
	                        //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
	                        " and FDivdendType = " + this.DividentType + 
	                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
	                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
	                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	                        " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
	                        
		                    dbl.executeSql(strSql);
						}
					}else{
	                    strSql = " update " + pub.yssGetTableName("Tb_Data_Dividend") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                        //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
                        " and FDivdendType = " + this.DividentType + 
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                        " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
                        
	                    dbl.executeSql(strSql);
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            else if ( oldSecurityCode != null&&(!oldSecurityCode.equalsIgnoreCase(""))) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群还原功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		                strSql = " update Tb_" + assetGroupCodes[j] + "_Data_Dividend" +
	                    " set FCheckState = " + this.checkStateId +
	                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
	                    " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
	                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
	                    " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
	                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FCuryCode = " + dbl.sqlString(this.oldCuryCode) +
	                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	                    " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
	                
		                dbl.executeSql(strSql);					
					}
				}else{
	                strSql = " update " + pub.yssGetTableName("Tb_Data_Dividend") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                    " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FCuryCode = " + dbl.sqlString(this.oldCuryCode) +
                    " and FPortCode = " + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                    " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
                
	                dbl.executeSql(strSql);					
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delOperData
     * 删除分红权益数据
     */
    public void delSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("Tb_Data_Dividend") +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FSecurityCode = " +
                        dbl.sqlString(this.oldSecurityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                        " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FCuryCode =" + dbl.sqlString(this.oldCuryCode)+
                        " and FPortCode=" +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode)+
                        " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                        +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
                        //------------------------end----------------//
                        ;
                        //------------------------end----------------//
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            else if ( oldSecurityCode != null&&(!oldSecurityCode.equalsIgnoreCase("")) ) {
                strSql = "update " + pub.yssGetTableName("Tb_Data_Dividend") +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "' where FSecurityCode = " +
                    dbl.sqlString(this.oldSecurityCode) +
                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                    " and FDivdendType = " + this.DividentType + //将原来的OldDividentType修改为了DividentType（此处不用OldDividentType）
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FCuryCode =" + dbl.sqlString(this.oldCuryCode) +
                    " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                    +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
                    //------------------------end----------------//
                    ;
                    //------------------------end----------------//
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * editOperData
     * 修改分红权益数据
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =
                "update " + pub.yssGetTableName("Tb_Data_Dividend") +
                " set FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
                ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
                ",FDividendDate = " + dbl.sqlDate(this.DividendDate) +
                ",FDistributeDate = " + dbl.sqlDate(this.DistributeDate) +
                ",FAfficheDate = " + dbl.sqlDate(this.AfficheDate) +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                ",FPreTaxRatio = " + this.PreTaxRatio +
                ",FAfterTaxRatio ="+this.AfterTaxRatio+
                ",FPortCode = "+ dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                ",FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                //--------------end----------------------//
                ",FRoundCode = " + dbl.sqlString(this.RoundCode) +
                ",FDesc = " + dbl.sqlString(this.Desc) +
                ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
                ",FCreator = " + dbl.sqlString(this.creatorCode) +
                ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
                // ",FDivdendType =" +dbl.sqlString(this.DividentType+"") +   lzp  modify   200712.7   bit不能转换为STRING
                ",FDivdendType =" + this.DividentType +
                ",FCuryCode =" + dbl.sqlString(this.DividentCuryCode + "") +
                ",FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " ,FIsInvest="+(this.isInvest==true?1:0)	//by guyichuan 20110512 STORY #741
                
                //modified by liubo.Story #1770
                //============================
                +" where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                " and FDivdendType = " + this.OldDividentType + //lzp  modify   200712.7   bit不能转换为STRING
                " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
                " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ")
                //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
                //------------------------end----------------//        
                ;
                //---------------------------end---------------------//

            //================end============
            // System.out.println("SQL="+strSql);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 筛选条件
     * @throws YssException
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            if (this.filterType.SecurityCode.trim().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.SecurityCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.RecordDate.length() != 0 &&
                !this.filterType.RecordDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FRecordDate = " +
                    dbl.sqlDate(filterType.RecordDate);
            }
            if (this.filterType.DividendDate.length() != 0 &&
                !this.filterType.DividendDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FDividendDate = " +
                    dbl.sqlDate(filterType.DividendDate);
            }
            if (this.filterType.DistributeDate.length() != 0 &&
                !this.filterType.DistributeDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FDistributeDate = " +
                    dbl.sqlDate(filterType.DistributeDate);
            }
            if (this.filterType.AfficheDate.length() != 0 &&
                !this.filterType.AfficheDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FAfficheDate = " +
                    dbl.sqlDate(filterType.AfficheDate);
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
            if (this.filterType.RoundCode.length() != 0) {
                sResult = sResult + " and a.FRoundCode like '" +
                    filterType.RoundCode.replaceAll("'", "''") + "%'"; ;
            }
            if (this.filterType.Desc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    filterType.Desc.replaceAll("'", "''") + "%'";
            }
            if (! (this.filterType.DividentType.trim().equals("99") || this.filterType.DividentType.trim().length() == 0)) {
                sResult += " and a.FDivdendType =" +
                    this.filterType.DividentType.trim().replaceAll("'", "''");
            }
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            if (this.filterType.PortCode.length() != 0) {
               sResult = sResult + " and a.FPortCode like '" +
                   filterType.PortCode.replaceAll("'", "''") + "%'";
           }
           if (this.filterType.AssetGroupCode.trim().length() != 0) {
               sResult = sResult + " and a.FASSETGROUPCODE ='" +
                   filterType.AssetGroupCode.replaceAll("'", "''") + "'";
           }
            //----------------------end-------------------------//
        }
        return sResult;
    }

    /**
     * getListViewData1
     * 获取分红权益数据
     * @return String
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
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_GEN_DIVIDENDTYPE);
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //----------------------jc
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName,d.FIsinCode as FIsinCode" + //xuqiji 20090414 MS00361    南方要求权益信息中显示证券的ISIN码
            	",ass.FAssetGroupName as FAssetGroupName" + 	//added by liubo.Story #1770
                ",e.FRoundName as FRoundName,n.Fvocname as DivdendType,m.Fcuryname as Fcuryname,h.FPortName as FPortName" + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_Dividend") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName,o.FIsinCode as FIsinCode from " + //xuqiji 20090414 MS00361    南方要求权益信息中显示证券的ISIN码
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                " left join (select FAssetGroupCode,FAssetGroupName from tb_sys_AssetGroup) ass on a.FAssetGroupCode = ass.FAssetGroupCode" +	//added by liubo.Story #1770
                " left join Tb_Fun_Vocabulary n on " + dbl.sqlToChar("a.FDivdendType") + " = n.FVocCode and n.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_DB_DivdendType) +
                " left join " + pub.yssGetTableName("Tb_Para_Currency") + " m on a.Fcurycode = m.fcurycode " +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                //-------------------------------end-------------------------------//
                buildFilterSql() +
                //modified by liubo.Story #1770
                //====================================================
                (this.filterType.AssetGroupCode.trim().length()==0?" and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')" :"")+//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                //======================end==============================
                " order by a.FCheckState, a.FCreateTime desc"; // wdy modify 20070830
//         System.out.println("SQL="+strSql);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("Dividend");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
				//modified by liubo.Story #1770.
				//用tmpAssetGroupName变量保存组合群名称，然后使用基类重载的buildRowShowStr方法将组合群名称插入ListView的数据中
				//================================
				String tmpAssetGroupName = this.getGroupNameFromGroupCode(rs.getString("FASSETGROUPCODE"));	
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols(),tmpAssetGroupName)).
                    append(YssCons.YSS_LINESPLITMARK);

				//==============end==================

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
            //BugNo:0000328 edit by jc
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_GEN_DIVIDENDTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr() + "\r\f" + "voc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //----------------------jc
        } catch (Exception e) {
            throw new YssException("获取分红权益设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * getListViewData2
     * 获取已审核的分红权益数据
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
            sHeader = "证券品种\t权益登记日\t描述说明";
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName" +
            ",e.FRoundName as FRoundName,m.Fcuryname as Fcuryname,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
            pub.yssGetTableName("Tb_Data_Dividend") + " a " +
            " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") + " o join " +
            "(select FSecurityCode,max(FStartDate) as FStartDate from " +
            pub.yssGetTableName("Tb_Para_Security") +
            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
            " left join " + pub.yssGetTableName("Tb_Para_Currency") + " m on a.Fcurycode = m.fcurycode " +
            " left join (select FRoundCode,FRoundName from " +
            pub.yssGetTableName("Tb_Para_Rounding") +
            " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
            //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
           " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
           //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//           pub.yssGetTableName("tb_para_portfolio") + " " +
//           " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//           " and FCheckState = 1 group by FPortCode )u " +
//           " join (select * from " +
           //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
           //edit by songjie 2011.03.16 不以最大的启用日期查询数据
           pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
            " where a.FCheckState = 1 "+
            " and (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')"+	//modified by liubo.Story #1770
            "order by a.FCheckState, a.FCreateTime desc";
                //-------------------------------end-------------------------------//
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append( (rs.getString("FSecurityName") + "").trim()).append(
                    "\t");
                bufShow.append(YssFun.formatDate(rs.getDate("FRecordDate"))).append(
                    "\t");
                bufShow.append(YssFun.left( (rs.getString("FDesc") + "").trim(), 40)).append(YssCons.YSS_LINESPLITMARK);

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
            throw new YssException("获取分红权益设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getListViewData3
     * @author guyichuan 20110509 STORY #741 查询股票分红转投的信息
     * @return String
     * @throws YssException 
     */
    public String getListViewData3() throws YssException {
    	String sHeader = "";
		String sShowDataStr ="";
		String sAllDataStr = "";
		
		StringBuffer sConSql=new StringBuffer();
		StringBuffer bufSql=new StringBuffer();
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		
		ResultSet rs = null;
		java.util.Date theDate=null;
		try {
			sHeader = this.getListView1Headers();
			String sAry[] = null;
			theDate=new java.util.Date();
	        sAry = this.operSql.storageAnalysisSql(YssOperCons.YSS_KCLX_Security); //获得分析代码
	        
	        //如果是从筛选按扭进来，则sConSql为空
	        if(!(this.status!=null&&this.status.trim().equals("YssFilter"))){
	        	sConSql.append(" and not exists");
	        	sConSql.append(" (select bb.Fsecuritycode,bb.FPortCode,bb.FDividendDate,bb.FCuryCode,bb.FDivdendType,bb.FRecordDate");
	        	sConSql.append(" from "+pub.yssGetTableName("Tb_Data_DividendInvest") +" bb");
	        	sConSql.append(" where aa.FSecurityCode = bb.Fsecuritycode");
	        	sConSql.append(" and aa.FPortCode = bb.FPortCode");
	        	sConSql.append(" and aa.FDividendDate = bb.FDividendDate");
	        	sConSql.append(" and aa.FCuryCode = bb.FCuryCode");
	        	sConSql.append(" and aa.FDivdendType = bb.FDivdendType");	     
	        	sConSql.append(" and aa.FRecordDate = bb.FRecordDate)" );
	        }
			
	        
	        //查询所有未到到帐日的已审核的未有其他转投设置关联的已设置“分红转投”状态的基金分红权益信息	     
	        bufSql.append(" select a.*,b.FUserName as FCreatorName,");
	        bufSql.append(" c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName,");
	        bufSql.append(" d.FIsinCode as FIsinCode,e.FRoundName as FRoundName,");
	        bufSql.append(" n.Fvocname as DivdendType, m.Fcuryname as Fcuryname,");
	        bufSql.append(" h.FPortName as FPortName from");
	        bufSql.append("(select aa.*");
	        bufSql.append(" from "+pub.yssGetTableName("Tb_Data_Dividend" +" aa"));
	        bufSql.append(" where FDistributeDate> "+dbl.sqlDate(theDate)+" and FCheckState=1 and FIsInvest=1");	       
	        bufSql.append(sConSql.toString());
	        bufSql.append(	") a");	        
	        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator=b.FUserCode");
	        bufSql.append(" left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser=c.FUserCode");
	        bufSql.append(" left join (select o.FSecurityCode as FSecurityCode,");
	        bufSql.append(" o.FSecurityName as FSecurityName,o.FIsinCode as FIsinCode");
	        bufSql.append(" from "+pub.yssGetTableName("Tb_Para_Security")+" o");
	        bufSql.append(" join (select FSecurityCode, max(FStartDate) as FStartDate");
	        bufSql.append(" from "+pub.yssGetTableName("Tb_Para_Security"));
	        bufSql.append("  where FStartDate <="+dbl.sqlDate(theDate)+" and FCheckState = 1");
	        bufSql.append(" group by FSecurityCode) p on o.FSecurityCode =p.FSecurityCode");
	        bufSql.append(" and o.FStartDate =p.FStartDate) d on a.FSecurityCode = d.FSecurityCode");
	        bufSql.append("  left join (select FRoundCode, FRoundName");
	        bufSql.append(" from "+pub.yssGetTableName("Tb_Para_Rounding")+" where FCheckState = 1) e on a.FRoundCode = e.FRoundCode");
	        bufSql.append(" left join Tb_Fun_Vocabulary n on a.FDivdendType = n.FVocCode");
	        bufSql.append(" and n.FVocTypeCode = 'DivdendType'");
	        bufSql.append(" left join "+pub.yssGetTableName("Tb_Para_Currency")+" m on a.Fcurycode = m.fcurycode");
	        bufSql.append(" left join (select v.FPortCode, v.FPortName, v.FStartDate");
	        bufSql.append(" from "+pub.yssGetTableName("tb_para_portfolio")+" v where FCheckState = 1) h on a.FPortCode = h.FPortCode");
	        bufSql.append("  where ");
	        bufSql.append((this.filterType.AssetGroupCode.trim().length()==0?" (a.FASSETGROUPCODE=' ' or a.FASSETGROUPCODE like '%" + pub.getAssetGroupCode() + "%')" :""));	//modified by liubo.Story #1770
	        bufSql.append("  order by a.FCheckState, a.FCreateTime desc");
	              
	        rs = dbl.openResultSet(bufSql.toString());
			while (rs.next()) {
				appendStr(bufShow, rs);
				setResultSetAttr(rs);
				this.DividentType = rs.getString("DivdendType");
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
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
			throw new YssException("获取股票分红信息出错" + "\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }
    
    /**
     * by guyichuan 20110509 STORY #741
     * 把查询出的需要的数据拼接
     * @param strBuf
     * @param rs
     * @throws SQLException 
     */
    public void appendStr(StringBuffer strBuf,ResultSet rs)throws YssException {
    	try{ 	
    	strBuf.append((rs.getString("FSecurityCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FSecurityName"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FIsinCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FRecordDate"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FDividendDate"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FDistributeDate"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FAfficheDate"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FPreTaxRatio"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FAfterTaxRatio"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FRoundCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FRoundName"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("DivdendType"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FAssetGroupCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCuryCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCuryName"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FPortCode"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FPortName"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FDesc"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCreator"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCreateTime"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCheckUser"))+"".trim()).append("\t");
    	strBuf.append((rs.getString("FCheckTime"))+"".trim()).append("\t");
    	strBuf.append(YssCons.YSS_LINESPLITMARK);
    	}catch(Exception e){
    		throw new YssException("获取股票分红信息出错" + "\r\n" + e.getMessage(), e);
    	}
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
        this.SecurityCode = rs.getString("FSecurityCode") + "";
        this.SecurityName = rs.getString("FSecurityName") + "";
        this.RecordDate = rs.getDate("FRecordDate") + "";
        this.DividendDate = rs.getDate("FDividendDate") + "";
        this.DistributeDate = rs.getDate("FDistributeDate") + "";
        if (rs.getDate("FAfficheDate") == null) {
            AfficheDate = "1900-01-01";
        } else {
            this.AfficheDate = rs.getDate("FAfficheDate") + "";
        }
        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
        this.PortCode = rs.getString("FPortCode") + "";
        this.PortName = rs.getString("FPortName") + "";
        this.AssetGroupCode = rs.getString("FASSETGROUPCODE");
        this.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
        this.AfterTaxRatio = rs.getBigDecimal("FAfterTaxRatio").toString() + "";
        //-------------------------end--------------------//
        //QDV4嘉实基金2008年11月07日02_D需求,修改将double 类型 改为 BigDecimal 类型 20081123    王晓光
        this.RoundCode = rs.getString("FRoundCode") + "";
        this.RoundName = rs.getString("FRoundName") + "";
        this.DividentCuryCode = rs.getString("FCuryCode") + "";
        this.DividentCuryName = rs.getString("FCuryName") + "";
        this.Desc = rs.getString("FDesc") + "";
        this.DividentType = rs.getString("FDivdendType");
        this.isInvest=(rs.getInt("FIsInvest")==1?true:false);//by guyichuan 20110516 STORY #741
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
	    	if (sType.equalsIgnoreCase("multauditTradeSub")) {
	            if (multAuditString.length() > 0) {
	            	//modify baopingping #story 1167 20110720 添加对跨组合群的处理
	            	MarketValueBean Mark=new MarketValueBean();
            		Mark.setYssPub(pub);
            		if (sType.equalsIgnoreCase("multauditTradeSub")) 
            		{
						if (multAuditString.length() > 0) {
							if (this.group.equalsIgnoreCase("ok")) {
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

							} else { // -----end----------
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
        DividendBean befEditBean = new DividendBean();
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FSecurityName as FSecurityName" +
                ",e.FRoundName as FRoundName,m.Fcuryname as Fcuryname,h.FPortName as FPortName " + " from " +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                pub.yssGetTableName("Tb_Data_Dividend") + " a " +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join " + pub.yssGetTableName("Tb_Para_Currency") + " m on a.Fcurycode = m.fcurycode " +
                " left join (select o.FSecurityCode as FSecurityCode,o.FSecurityName as FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") + " o join " +
                "(select FSecurityCode,max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FSecurityCode) p " + " on o.FSecurityCode = p.FSecurityCode and o.FStartDate = p.FStartDate) d on a.FSecurityCode = d.FSecurityCode" +
                " left join (select FRoundCode,FRoundName from " +
                pub.yssGetTableName("Tb_Para_Rounding") +
                " where FCheckState = 1) e on a.FRoundCode = e.FRoundCode " +
                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select v.FPortCode ,v.FPortName, v.FStartDate  from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("tb_para_portfolio") + " " +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
//                " and FCheckState = 1 group by FPortCode )u " +
//                " join (select * from " +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                pub.yssGetTableName("tb_para_portfolio") + " v where FCheckState = 1) h on a.FPortCode = h.FPortCode" +
                //-------------------------------end-------------------------------//
                " where  a.FSecurityCode =" + dbl.sqlString(this.oldSecurityCode) +
                " and a.FRecordDate=" + dbl.sqlDate(this.oldRecordDate) +
                (this.OldPortCode.length() > 0 ? "and a.FPortCode=" + dbl.sqlString(this.OldPortCode) : "and 1=1") +//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                " order by a.FCheckState, a.FCreateTime desc";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.SecurityCode = rs.getString("FSecurityCode") + "";
                befEditBean.SecurityName = rs.getString("FSecurityName") + "";
                befEditBean.RecordDate = rs.getDate("FRecordDate") + "";
                befEditBean.DividendDate = rs.getDate("FDividendDate") + "";
                befEditBean.DistributeDate = rs.getDate("FDistributeDate") + "";
                befEditBean.AfficheDate = rs.getDate("FAfficheDate") + "";

                //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                befEditBean.PreTaxRatio = rs.getBigDecimal("FPreTaxRatio").toString() + "";
                befEditBean.AfterTaxRatio=rs.getBigDecimal("FAfterTaxRatio").toString() + "";
                befEditBean.PortCode=rs.getString("FPortCode")+"";
                befEditBean.PortName=rs.getString("FPortName")+"";
                //-------------------------end----------------------------//
                befEditBean.RoundCode = rs.getString("FRoundCode") + "";
                befEditBean.RoundName = rs.getString("FRoundName") + "";
                befEditBean.Desc = rs.getString("FDesc") + "";
                befEditBean.DividentType = rs.getString("FDivdendType");
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
//    	GroupCode=AssetGroupCode.split(",");
    		
    	//===============end==================
    	if (flag.equalsIgnoreCase("copy")){
    		checkInput(YssCons.OP_ADD);
    	}
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
				this.editData(GroupCode[i]);
			}else if(flag.equalsIgnoreCase("copy")){
				if (this.Iscopy.equalsIgnoreCase("yes")) {
					if (tableName.equalsIgnoreCase("none")) {
						this.copyData(GroupCode[i]);

					} else {
						this.editData(GroupCode[i]);
					}
				} else {
					if (tableName.equalsIgnoreCase("none")) {
						this.copyData(GroupCode[i]);
					}else{
						//this.editData(GroupCode[i]);
					}
				}
			}else if(flag.equalsIgnoreCase("checked")){
				msg=this.getGroup(GroupCode[i]);
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

    /**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * 删除回收站内的数据，即把数据从数据库彻底删除
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
		                    strSql = " delete from Tb_" + assetGroupCodes[j] + "_Data_Dividend " +
	                        " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
	                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
	                        " and FDivdendType = " + this.DividentType + //lzp  modify   200712.7   bit不能转换为STRING
	                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                        " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
	                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode) +
	                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	                        " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
	                    
		                    //执行sql语句
		                    dbl.executeSql(strSql);				
						}
					}else{
	                    strSql = " delete from " + pub.yssGetTableName("Tb_Data_Dividend") +
                        " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                        " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                        " and FDivdendType = " + this.DividentType + //lzp  modify   200712.7   bit不能转换为STRING
                        //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                        " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
                        " and FPortCode = " +dbl.sqlString(this.PortCode.length() == 0 ? " " :this.PortCode) +
                        " and FASSETGROUPCODE = " + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                        //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                        " and FDISTRIBUTEDATE = "+dbl.sqlDate(this.oldDistributeDate);
                    
	                    //执行sql语句
	                    dbl.executeSql(strSql);
					}
					//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
                }
            }
            //sRecycled如果sRecycled为空，而oldSecurityCode不为空，则按照oldSecurityCode来执行sql语句
            else if (oldSecurityCode != "" && oldSecurityCode != null) {
            	//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A start---//
				if(this.AssetGroupCode.trim().length() > 0){//添加跨组合群清除功能
					assetGroupCodes = this.AssetGroupCode.trim().split(",");
					for(int j = 0; j < assetGroupCodes.length; j++){
		                strSql = " delete from Tb_" + assetGroupCodes[j] + "_Data_Dividend " +
	                    " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
	                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
	                    " and FDivdendType = " + this.DividentType + //lzp  modify   200712.7   bit不能转换为STRING
	                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                    " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
	                    " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                    " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	                    " and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate);
	                
		                //执行sql语句
		                dbl.executeSql(strSql);					
					}
				}else{
	                strSql = " delete from " + pub.yssGetTableName("Tb_Data_Dividend") +
                    " where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
                    " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                    " and FDivdendType = " + this.DividentType + //lzp  modify   200712.7   bit不能转换为STRING
                    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                    " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
                    " and FPortCode=" + dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
                    " and FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
                    //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                    " and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate);
                
	                //执行sql语句
	                dbl.executeSql(strSql);
				}
				//---edit by songjie 2012.05.29 STORY #2466 QDV4赢时胜(上海)2012年04月05日01_A end---//
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
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
    public String getPortCode() {
        return PortCode;
    }

    public String getPortName() {
        return PortName;
    }

    public void setPortCode(String PortCode) {
        this.PortCode = PortCode;
    }

    public void setPortName(String PortName) {
        this.PortName = PortName;
    }

    public String getOldPortCode() {
        return OldPortCode;
    }

    public void setOldPortCode(String OldPortCode) {
        this.OldPortCode = OldPortCode;
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
    //--------------------------------end----------------------//
    public String getSecurityCode() {
        return SecurityCode;
    }

    public String getSecurityName() {
        return SecurityName;
    }

    public void setSecurityCode(String SecurityCode) {
        this.SecurityCode = SecurityCode;
    }

    public void setSecurityName(String SecurityName) {
        this.SecurityName = SecurityName;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getAfficheDate() {
        return AfficheDate;
    }

    public void setAfficheDate(String AfficheDate) {
        this.AfficheDate = AfficheDate;
    }

    public String getDistributeDate() {
        return DistributeDate;
    }

    public void setDistributeDate(String DistributeDate) {
        this.DistributeDate = DistributeDate;
    }

    public String getDividendDate() {
        return DividendDate;
    }

    public void setDividendDate(String DividendDate) {
        this.DividendDate = DividendDate;
    }

    public String getDividentCuryCode() {
        return DividentCuryCode;
    }

    public void setDividentCuryCode(String DividentCuryCode) {
        this.DividentCuryCode = DividentCuryCode;
    }

    public String getDividentCuryName() {
        return DividentCuryName;
    }

    public void setDividentCuryName(String DividentCuryName) {
        this.DividentCuryName = DividentCuryName;
    }

    public DividendBean getFilterType() {
        return filterType;
    }

    public String getOldRecordDate() {
        return oldRecordDate;
    }

    public String getOldSecurityCode() {
        return oldSecurityCode;
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

    public String getOldDividentType() {
        return OldDividentType;
    }

    public void setFilterType(DividendBean filterType) {
        this.filterType = filterType;
    }

    public void setOldCuryCode(String oldCuryCode) {
        this.oldCuryCode = oldCuryCode;
    }

    public void setOldRecordDate(String oldRecordDate) {
        this.oldRecordDate = oldRecordDate;
    }

    public void setOldSecurityCode(String oldSecurityCode) {
        this.oldSecurityCode = oldSecurityCode;
    }

    public void setRoundCode(String RoundCode) {
        this.RoundCode = RoundCode;
    }

    public void setRoundName(String RoundName) {
        this.RoundName = RoundName;
    }

    public void setRecordDate(String RecordDate) {
        this.RecordDate = RecordDate;
    }

    public void setOldDividentType(String OldDividentType) {
        this.OldDividentType = OldDividentType;
    }
   //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
    public String getOldDistributeDate() {
		return oldDistributeDate;
	}

	public void setOldDistributeDate(String oldDistributeDate) {
		this.oldDistributeDate = oldDistributeDate;
	}
  //-----------------------------------------
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
        DividendBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = "update " + pub.yssGetTableName("Tb_Data_Dividend") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = ? and FRecordDate = ? and FPortCode= ? and FDivdendType = ? and FCuryCode = ?" +
                //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                " and FDistributeDate=?"
                //-----------------------------
                ;           
            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new DividendBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        psmt.setString(4,data.getDividentType());
                        psmt.setString(5,data.getDividentCuryCode());
                        //----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                        psmt.setDate(6, YssFun.toSqlDate(data.getDistributeDate()));
                        //------------------------
                        psmt.addBatch();
//                      ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
                        data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId  == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
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
        	e.printStackTrace();
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
     * @throws YssException 
	 */
    private String addData(String FAssetGroupCode) throws YssException {
    	Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";      
        try {
        	 conn.setAutoCommit(false);
             bTrans = true;
            strSql =//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                "insert into "  + ("Tb_"+FAssetGroupCode+"_Data_Dividend") +
                "(FSecurityCode,FRecordDate,FDividendDate,FDistributeDate,FAfficheDate,FPreTaxRatio,FAfterTaxRatio,"+
                "FPortCode,FAssetGroupCode,FRoundCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FDivdendType,FCuryCode,FIsInvest)" +
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.DividendDate) + "," +
                dbl.sqlDate(this.DistributeDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                (this.PreTaxRatio.trim().equals("0")?"0":this.PreTaxRatio) + "," +
                (this.AfterTaxRatio.trim().equals("0")?"0":this.AfterTaxRatio)+","+
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------------------end--------------------------------------//
                dbl.sqlString(this.RoundCode) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.DividentType + "" + "," +
                dbl.sqlString(this.DividentCuryCode) +","
                +(this.isInvest==true?1:0)					//by guyichuan 20110511 STORY #741
                + ")"; ////////////
                      
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增分红权益业务数据出错", e);
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
        			 "Tb_" + sOldAssetGroup[i] + "_Data_Dividend" +
                     " where FSecurityCode = " +
                     dbl.sqlString(this.oldSecurityCode) +
                     " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
                     " and FDivdendType = " + this.OldDividentType + 
                     " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
                     " and FPortCode=" +dbl.sqlString(this.OldPortCode.length() == 0 ? " " :this.OldPortCode) +
                     " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
                     +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate);
        			 
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
	                 "update " + ("Tb_"+FAssetGroupCode+"_Data_Dividend") +
	                 " set FSecurityCode = " + dbl.sqlString(this.SecurityCode) +
	                 ",FRecordDate = " + dbl.sqlDate(this.RecordDate) +
	                 ",FDividendDate = " + dbl.sqlDate(this.DividendDate) +
	                 ",FDistributeDate = " + dbl.sqlDate(this.DistributeDate) +
	                 ",FAfficheDate = " + dbl.sqlDate(this.AfficheDate) +
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                 ",FPreTaxRatio = " + this.PreTaxRatio +
	                 ",FAfterTaxRatio ="+this.AfterTaxRatio+
	                 ",FPortCode = "+ dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) +
	                 ",FASSETGROUPCODE=" + dbl.sqlString(AssetGroupCode.trim().length() > 0 ? AssetGroupCode : " ") +
	                 //--------------end----------------------//
	                 ",FRoundCode = " + dbl.sqlString(this.RoundCode) +
	                 ",FDesc = " + dbl.sqlString(this.Desc) +
	                 ",FCheckstate = " + (pub.getSysCheckState() ? "0" : "1") +
	                 ",FCreator = " + dbl.sqlString(this.creatorCode) +
	                 ",FCreateTime = " + dbl.sqlString(this.creatorTime) +
	                 // ",FDivdendType =" +dbl.sqlString(this.DividentType+"") +   lzp  modify   200712.7   bit不能转换为STRING
	                 ",FDivdendType =" + this.DividentType +
	                 ",FCuryCode =" + dbl.sqlString(this.DividentCuryCode + "") +
	                 ",FCheckUser = " +
	                 (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
	                 " ,FIsInvest="+(this.isInvest==true?1:0)	//by guyichuan 20110512 STORY #741
	                 +" where FSecurityCode = " + dbl.sqlString(this.oldSecurityCode) +
	                 " and FRecordDate = " + dbl.sqlDate(this.oldRecordDate) +
	                 " and FDivdendType = " + this.OldDividentType + //lzp  modify   200712.7   bit不能转换为STRING
	                 " and FCuryCode = " + dbl.sqlString(this.oldCuryCode)+
	                 //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
	                 " and FPortCode=" + dbl.sqlString(this.OldPortCode.length() == 0 ? " " : this.OldPortCode) +
	                 " and FASSETGROUPCODE=" + dbl.sqlString(OldAssetGroupCode.trim().length() > 0 ? OldAssetGroupCode : " ")
	                 //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
	                 //modified by liubo.Story #1770
	                 //=======================================
	//                 +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.DistributeDate)+
	                 +"and FDISTRIBUTEDATE="+dbl.sqlDate(this.oldDistributeDate)
	                 //=======================================
	//                 " and FCheckState='0'"
	                 //------------------------end----------------//        
	                 ;
	                 //---------------------------end---------------------//
	             // System.out.println("SQL="+strSql);
	             conn.setAutoCommit(false);
	             bTrans = true;
	             dbl.executeSql(strSql);
	             conn.commit();
	             bTrans = false;
	             conn.setAutoCommit(true);
        	 }
             return buildRowStr();
         } catch (Exception e) {
             throw new YssException("修改分红权益业务数据出错", e);
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
    	}   //判断前台传来的是删除吗。
    	Connection conn = null;
        String sqlStr = "";
        java.sql.PreparedStatement psmt = null;
        boolean bTrans = false;
        DividendBean data = null;
        String[] multAudit = null;
        try {
            conn = dbl.loadConnection();
            
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B start---//
            if(!dbl.yssTableExist("Tb_"+FAssetGroupCode+"_Data_Dividend")){
            	return "";
            }
            //---add by songjie 2012.01.31 BUG 3722 QDV4赢时胜(上海)2012年01月20日02_B end---//
            
            sqlStr = "update " + ("Tb_"+FAssetGroupCode+"_Data_Dividend") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FSecurityCode = ? and FRecordDate = ? and FPortCode= ? and FDivdendType = ? and FCuryCode = ?" +
                //-----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.02
                " and FDistributeDate=?" + 
                (checkTrue ? " and FCheckState='0'" : " ");//add by baopingping #story 1167 20110720 只删除未审核的数据  不是删除测按原来的处理 
                //-----------------------------
                ;           
            psmt = conn.prepareStatement(sqlStr);
            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f");
                if (multAudit.length > 0) {
                    for (int i = 0; i < multAudit.length; i++) {
                        data = new DividendBean();
                        data.setYssPub(pub);
                        data.parseRowStr(multAudit[i]);
                        psmt.setString(1, data.getSecurityCode());
                        psmt.setDate(2,YssFun.toSqlDate(data.getRecordDate()));
                        psmt.setString(3,data.getPortCode().length() == 0 ? " " :data.getPortCode());
                        psmt.setString(4,data.getDividentType());
                        psmt.setString(5,data.getDividentCuryCode());
                        //----add by yangheng MS01670 QDV4赢时胜（深圳）2010年8月30日01_B 2010.09.01
                        psmt.setDate(6, YssFun.toSqlDate(data.getDistributeDate()));
                        //------------------------
                        psmt.addBatch();
//                      ---增加批量删除的日志记录功能----guojianhua add 20100906-------//
                        data=this;
                        logOper = SingleLogOper.getInstance();
						if (this.checkStateId  == 2) {
							logOper.setIData(data, YssCons.OP_DEL, pub);
						} else if (this.checkStateId == 1) {
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
        	e.printStackTrace();
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
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql =//xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015  国内权益处理
                "insert into "  + ("Tb_"+FAssetGroupCode+"_Data_Dividend") +
                "(FSecurityCode,FRecordDate,FDividendDate,FDistributeDate,FAfficheDate,FPreTaxRatio,FAfterTaxRatio,"+
                "FPortCode,FAssetGroupCode,FRoundCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FDivdendType,FCuryCode,FIsInvest)" +
                " values(" + dbl.sqlString(this.SecurityCode) + "," +
                dbl.sqlDate(this.RecordDate) + "," +
                dbl.sqlDate(this.DividendDate) + "," +
                dbl.sqlDate(this.DistributeDate) + "," +
                dbl.sqlDate(this.AfficheDate) + "," +
                (this.PreTaxRatio.trim().equals("0")?"0":this.PreTaxRatio) + "," +
                (this.AfterTaxRatio.trim().equals("0")?"0":this.AfterTaxRatio)+","+
                dbl.sqlString(this.PortCode.length() == 0 ? " " : this.PortCode) + "," +
                dbl.sqlString(this.AssetGroupCode.trim().length() > 0 ? this.AssetGroupCode : " ") +","+
                //---------------------------------end--------------------------------------//
                dbl.sqlString(this.RoundCode) + "," +
                dbl.sqlString(this.Desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                "," +
                this.DividentType + "" + "," +
                dbl.sqlString(this.DividentCuryCode) +","
                +(this.isInvest==true?1:0)					//by guyichuan 20110511 STORY #741
                + ")"; ////////////
            
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增分红权益业务数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
	 * add baopingping #story 1167 20110717
	 * 查看数据库中是否存在这个组合的数据
	 * return ResultSet
	 * TableName 组合群代码
	 * @throws YssException 
	 */
	
	public String getTable(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="none";
		try{
			sql="select * from TB_"+TableName+"_Data_Dividend where FSecurityCode='"+this.SecurityCode+
			//edit by songjie 2012.01.17 BUG 3657 QDV4建行2012年1月16日01_B 主键判断写的不正确
			"'and FDISTRIBUTEDATE="+dbl.sqlDate(this.DistributeDate)+" and FRecordDate="+
			dbl.sqlDate(this.RecordDate)+" and " +" FPortCode="+ dbl.sqlString((this.PortCode.length() == 0) ? " "
			: this.PortCode) +" and FAssetGroupCode="+dbl.sqlString((this.AssetGroupCode.length() == 0) ? " "
			: this.AssetGroupCode) +" and FCuryCode='"+this.DividentCuryCode+"'" + 
			//edit by songjie 2012.01.17 BUG 3657 QDV4建行2012年1月16日01_B 主键判断写的不正确
			" and FDivdendType = " + dbl.sqlString(this.DividentType);
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

	
	
	public String getGroup(String TableName) throws YssException{
		ResultSet rs=null;
		String sql=null;
		String  name="";
		try{
			sql="select * from TB_"+TableName+"_Data_Dividend where Fcheckstate = 0 and FSecurityCode='"+this.SecurityCode+
			"'and FDividendDate="+dbl.sqlDate(this.DividendDate)+" and FRecordDate="+
			dbl.sqlDate(this.RecordDate)+" and " +" FPortCode="+ dbl.sqlString((this.PortCode.length() == 0) ? " "
			: this.PortCode) +" and FAssetGroupCode="+dbl.sqlString((this.AssetGroupCode.length() == 0) ? " "
			: this.AssetGroupCode) +" and FCuryCode='"+this.DividentCuryCode+"' and FDIVDENDTYPE="+this.DividentType+"";
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
	
	//added by liubo.Story #1770
	
	public String returnAssetGroupName() throws YssException
	{
		String strSql = "";
		ResultSet rs = null;
		String sReturn = "";
		
		try
		{
			if ("".equals(this.AssetGroupCode.trim()))
			{
				return "";
			}
			String[] sGroupCode = this.AssetGroupCode.trim().split(",");
			
			for (int i = 1;i <= sGroupCode.length;i++ )
			{
				strSql = "SELECT FAssetGroupName FROM tb_sys_AssetGroup where FAssetGroupCode = '" + sGroupCode[i-1] + "'";
				rs = dbl.openResultSet(strSql);
				while (rs.next())
				{
					sReturn = sReturn + rs.getString("FAssetGroupName") + ",";
				}
				
			}
			
			return ("".equals(sReturn.trim()) ? "" : sReturn.substring(0, sReturn.length() - 1));
			
		}
		catch(Exception e)
		{
			throw new YssException("获取组合群名称出错：" + e.getMessage());
		}
		finally
		{
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
