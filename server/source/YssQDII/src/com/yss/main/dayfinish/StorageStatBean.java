package com.yss.main.dayfinish;

import java.util.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.stgstat.*;
import com.yss.util.*;

/**
 *
 * <p>Title: StorageStatBean </p>
 * <p>Description: 库存统计 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class StorageStatBean
    extends BaseDataSettingBean implements IClientOperRequest {
    private String strOperStartDate = ""; //业务起始日期
    private String strOperEndDate = ""; //业务截止日期
    private String strPortCode = ""; //组合代码"，"间隔
    private String strOperType = ""; //业务类别
    private String reCost; //是否重新计算交易成本
    private static String strDealInfo = "";
    private String yearChange = ""; //年度结转
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    //--------------------------------------------------------------------------------
    
    //add by songjie 2012.08.31 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A
    public static HashMap hmLogSumCode = new HashMap();
    public StorageStatBean() {
    }

    public void setOperStartDate(String sOperStartDate) {
        this.strOperStartDate = sOperStartDate;
    }

    public void setOperEndDate(String sOperEndDate) {
        this.strOperEndDate = sOperEndDate;
    }

    public void setOperType(String sOperType) {
        this.strOperType = sOperType;
    }

    public void setReCost(String sReCost) {
        this.reCost = sReCost;
    }

    public void setYearChange(String sYearChange) {
        this.yearChange = sYearChange;
    }

    public String getOperStartDate() {
        return this.strOperStartDate;
    }

    public String getOperEndDate() {
        return this.strOperEndDate;
    }

    public String getOperType() {
        return this.strOperType;
    }

    public String getReCost() {
        return this.reCost;
    }

    public String getYearChange() {
        return this.yearChange;
    }

    /**
     * parseRowStr
     * 解析请求信息
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.strOperStartDate = reqAry[0];
            this.strOperEndDate = reqAry[1];
            this.strPortCode = reqAry[2]; //由，间隔
            this.strOperType = reqAry[3];
            this.reCost = reqAry[4];
            this.yearChange = reqAry[5];
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090520
            this.assetGroupCode = reqAry[6];
        } catch (Exception e) {
            throw new YssException("解析日终处理请求信息出错\r\n" + e.getMessage(), e);
        }
    }

    //获取组合代码，转换后形如 '001','002'
    public String getPortCodeSQL() {
        String strReturn = "";
        String[] sPortAry;
        if (this.strPortCode.trim().length() > 0) {
            sPortAry = this.strPortCode.split(",");
            for (int i = 0; i < sPortAry.length; i++) {
                strReturn = strReturn + "'" + sPortAry[i] + "',";
            }
            if (strReturn.length() > 0) {
                strReturn = YssFun.left(strReturn, strReturn.length() - 1);
            }
        }
        return strReturn;
    }

    public String getOperValue(String sType) {
        String strReturn = "";
        if (sType != null && sType.equalsIgnoreCase("getstate")) {
            strReturn = this.strDealInfo;
        }
        return strReturn;
    }

    public String checkRequest(String sType) throws YssException {
        return "";
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 这里分批对组合群里的数据进行统计，循环统计组合群里的内容
    public String doOperation(String sType) throws YssException {
        String isRinght = ""; //传给前台的信息判断是否成功
        String sPrefixTB = pub.getPrefixTB(); //把组合群代码更新到PUB里面去
        String[] allAssetGroupCode = this.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK); //解析前台传来的组合群代码
        String[] allPortCodes = this.strPortCode.split(YssCons.
            YSS_GROUPSPLITMARK); //解析前台传来的组合代码按组合群解析出来
        try {
            for (int i = 0; i < allAssetGroupCode.length; i++) { //循环组合群代码
                this.assetGroupCode = allAssetGroupCode[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //更新当前组合群代码
                this.strPortCode = allPortCodes[i]; //得到当前组合群代码下的组合代码
                isRinght = this.groupFinish(); //调用统计方法
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
        }
        return isRinght;
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    //---------------------------------------------------//
    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 这个统计方法就是以前单个组合群的方法
    public String groupFinish() throws YssException {
        String strError = "", strReturn = "", sReInfo = "";
        ArrayList types = new ArrayList();
        ArrayList dBeans = new ArrayList();
        String reqAry[] = null;
        String type = "";
        int iDays = 0;
        java.util.Date dStartDate = null;
        java.util.Date dEndDate = null;
        java.util.Date dDate = null;
        int operType = 0;
        BaseStgStatDeal stgstat = null;
        SingleLogOper logOper = null;
        logOper = SingleLogOper.getInstance();
        //---add by songjie 2012.08.31 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A start---//
		String key = "";
		String logSumCode = "";
  	    Date logStartTime = null;//业务子项开始时间
  	    DayFinishLogBean df = new DayFinishLogBean();
  	    df.setYssPub(pub);
		//---add by songjie 2012.08.31 STORY #2188 QDV4赢时胜(上海开发部)2012年2月3日02_A end---//
        try {
            //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29 用于判断技能同一个组合群组合下是否有与当前用户相同的操作类型,有的话就跳出提示信息
            YssGlobal.judgeIfUniqueUserAndPort(YssCons.YSS_OPER_STORAGESTAT,
                                               strPortCode, pub);
			//统计前先清除全局变量中的编号 多线程并行并发优化处理 合并太平版本代码 by leeyu 20100701
     	  	if(!YssGlobal.clearValNums){
         		  YssGlobal.clearValNums =true;
         		  YssGlobal.hmCashRecNums.clear();
         		  YssGlobal.hmSecRecNums.clear();
         	 }
     		 //统计多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码 
            operType = 6;
            reqAry = this.strOperType.split(",");
            for (int i = 0; i < reqAry.length; i++) {
                types.add(reqAry[i]);
            }
            dStartDate = YssFun.toDate(this.strOperStartDate);
            dEndDate = YssFun.toDate(this.strOperEndDate);
            //调整调整证券库存成本的统计方式，如果日期小于7天一次性调整较快，如果日期大于7天则每天调整方式更快 by leeyu 20100819 合并太平版本调整
            boolean bEveryDayAdjuestCost = YssFun.dateDiff(dStartDate, dEndDate)>=7?true:false;
            
      	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            logStartTime = new Date();

            key = dbl.getUser() + "\t" + dbl.getUrl() + "\t" 
            + pub.getUserCode() + "\t" + pub.getPrefixTB();

      	    if(strOperType.equals("start")){
          	    logSumCode = df.getLogSumCodes();
          	    hmLogSumCode.put(key, logSumCode);
          	    return "";
      	    }else if(strOperType.equals("end")){
      	    	logSumCode = (String)hmLogSumCode.get(key);
      	    	//若为结束标志，则插入汇总业务日志数据
      	        //edit by songjie 2012.11.20 添加非空判断
      	    	if(logOper != null){
      	    		logOper.setDayFinishIData(this, operType,"sum", pub, 
          	    		false, " ", dStartDate, dDate, dEndDate, " ",
          	    		logStartTime, logSumCode, new java.util.Date());
      	    	}
          	    return "";
      	    }else{
      	    	logSumCode = (String)hmLogSumCode.get(key);
      	    }
      	    //---add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
      	    
            for (int k = 0; k < types.size(); k++) { //循环统计的类型
                type = (String) types.get(k); //要根据类型去获取相应的BEAN
                stgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean(type);
                stgstat.setYssPub(pub);
				try {
					for (int j = 0; j <= YssFun.dateDiff(dStartDate, dEndDate); j++) {
					    //add by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
						logStartTime = new Date();
						dDate = YssFun.addDay(dStartDate, j);
						if (this.reCost.equalsIgnoreCase("true")
								&& this.yearChange.equalsIgnoreCase("true")) {
							stgstat.initStorageStat(dStartDate, dEndDate, this
									.getPortCodeSQL(), true, true);
						} else if (this.reCost.equalsIgnoreCase("false")
								&& this.yearChange.equalsIgnoreCase("true")) {
							stgstat.initStorageStat(dStartDate, dEndDate, this
									.getPortCodeSQL(), false, true);
						} else if (this.reCost.equalsIgnoreCase("true")
								&& this.yearChange.equalsIgnoreCase("false")) {
							stgstat.initStorageStat(dStartDate, dEndDate, this
									.getPortCodeSQL(), true, false);
						} else if (this.reCost.equalsIgnoreCase("false")
								&& this.yearChange.equalsIgnoreCase("false")) {
							stgstat.initStorageStat(dStartDate, dEndDate, this
									.getPortCodeSQL(), false, false);
						}
						dBeans = stgstat.getStorageStatData(dDate); // 获得数据
						if (dBeans != null) {
							// 增加了判断是否更新行情汇率 2009.04.25 蒋锦 添加 MS00006
							// 《QDV4.1赢时胜上海2009年2月1日05_A》多用户并发优化
							strReturn = stgstat.saveStorageStatData(dBeans,
									dDate, true).trim(); // 然后再进行保存
							strDealInfo = "true";
						}
						// 根据bEveryDayAdjuestCost参数，如果日期小于7天一次性调整较快，如果日期大于7天则每天调整方式更快，这里按每日调整
						// by leeyu 20100819 合并太平版本调整
						if (bEveryDayAdjuestCost) { // 调整证券库存成本
							stgstat.adjustStorageCost(dDate, dDate,
									this.strPortCode);
						}
						// ----------add by zhouxiang 2010.12.14-----
						//edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
						//edit by songjie 2012.11.20 添加非空判断
						if(logOper != null){
							logOper.setDayFinishIData(this,6,type, pub, false,this.strPortCode, dStartDate, dDate, dEndDate,
								" 统计成功",logStartTime,logSumCode,new Date());
						}
						// ----------add by zhouxiang 2010.12.14-----
					}
					// 添加bEveryDayAdjuestCost参数，如果日期小于7天一次性调整较快，如果日期大于7天则每天调整方式更快，这里按区间调整
					// by leeyu 20100819 合并太平版本调整
					if (stgstat instanceof StgSecurity && !bEveryDayAdjuestCost) { // 调整证券库存成本
						stgstat.adjustStorageCost(dStartDate, dEndDate,
								this.strPortCode);
					}
				} catch (Exception e) {
					//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
					try{
						//edit by songjie 2012.11.20 添加非空判断)
						if(logOper != null){
							logOper.setDayFinishIData(this,6,type, pub, true,this.strPortCode, 
								dStartDate, dDate, dEndDate, " 统计失败 \r\n " + 
								//edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A 处理日志信息 除去特殊符号
								e.getMessage().replaceAll("\t", "").replaceAll("&", "").replace("\f\f", ""),
								logStartTime,logSumCode,new Date());
						//插入汇总业务日志数据
							logOper.setDayFinishIData(this, operType,"sum", pub, 
		          	    		true, " ", dStartDate, dDate, dEndDate, " ",
		          	    		logStartTime,logSumCode, new java.util.Date());
		          	    }
					}catch(Exception ex){
						ex.printStackTrace();
					}
					//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
					finally{//添加 finally 保证可以抛出异常
						throw new YssException(e.getMessage(),e);
					}
					//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
					//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
				}
                sReInfo = "\t统计成功\r\n";
            }          
            logOper.setIData(this, 6, pub);
            return strReturn;
        } catch (Exception e) {
            try {
                logOper = SingleLogOper.getInstance();
                logOper.setIData(this, operType, pub, true);
            } catch (YssException ex) {
                ex.printStackTrace();
            }
            strDealInfo = "false";
            throw new YssException(strError, e); //by caocheng 2009.02.04 MS00004 QDV4.1-2009.2.1_09A 采用新异常处理机制后不用e.getMessage()
        }
        //----MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29----//
        finally {
            //移除当前用户当前组合群组合下操作类型方面的信息
            YssGlobal.removeRefeUserInfo(YssCons.YSS_OPER_STORAGESTAT,
                                         strPortCode, pub);
			YssGlobal.clearValNums =false;//统计前先清除全局变量中的编号 多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码 

        }
        //----MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29----//
    }
}
