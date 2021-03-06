package com.yss.main.settlement;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.log.*;
import com.yss.main.taoperation.*;

public class TATradeSettleBean
    extends BaseDataSettingBean implements IClientOperRequest {
    private String settleState = "";
    private String nums = "";
    private boolean compTemp;
    private String sAllData = "";
	//edit by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A
	public SingleLogOper logOper;//private 改为 public
    private boolean flag = true; //20121212 added by liubo.Bug #6584
	//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A start---//
    public boolean comeFromDD = false;//是否由调度方案调用
	public String logSumCode = "";//汇总日志编号
	//---add by songjie 2013.01.15 STORY #2343 QDV4建行2012年3月2日04_A end---//
	//20121212 added by liubo.Bug #6584
	//=========================
    public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	//=========================
    public String getSettleState() {
        return settleState;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }

    public void setSettleState(String settleState) {
        this.settleState = settleState;
    }

    public void setCompTemp(boolean compTemp) {
        this.compTemp = compTemp;
    }

    public void setSAllData(String sAllData) {
        this.sAllData = sAllData;
    }

    public String getNums() {
        return nums;
    }

    public boolean isCompTemp() {
        return compTemp;
    }

    public String getSAllData() {
        return sAllData;
    }

    public TATradeSettleBean() {
    }

    /**
     * checkRequest
     *
     * @return String
     */
    public String checkRequest(String sType) {
        return "";
    }

    /**
     * doOperation
     * xuqiji 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        String strError = "";
        int operType = 0;
        String strReturn = "false";
        String[] sArr = null;
        //---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        String operItem = "";
        String errorInfo = "";
        boolean isError = false;
        Date logStartTime = new Date();//业务子项开始时间
        DayFinishLogBean df = new DayFinishLogBean();
        //---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
        try {
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	df.setYssPub(pub);
        	this.setFunName("tatradesettleview");//设置功能调用代码
        	if(this.logSumCode.trim().length() == 0){
        		logSumCode = df.getLogSumCodes();//日志汇总编号
        	}
        	if(this.logOper == null){
        		logOper = SingleLogOper.getInstance();
        	}
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A end---//
        	
            TaTradeBean tatrade = new TaTradeBean();
            tatrade.setYssPub(pub);
            if (settleState.equalsIgnoreCase("do")) {
            	strError = "TA交易结算出错";
            	tatrade.setSNums(nums);//modified by yeshenghong 20120725 story2633 TA交易结算优化
            	sArr = sAllData.split("\r\n");
//                for (int i = 0; i < sArr.length; i++) {
//                    tatrade.parseRowStr(sArr[i]);
//                    //begin  zhouxiang MS01744   系统需支持基金分红时，现金分红在分红转投日之前的情况    
//                    if(tatrade.getStrSellTypeCode().equals("10")&&!getDealMode()){//如果是TA红利发放就不进行资金调拨
//                    	continue;
//                    }
//                    tatrade.doSettle("do",sAllData);
//                }
            	tatrade.setFlag(false);		//20121212 added by liubo.Bug #6584.Flag变量用于控制自动结算TA交易数据时，进行先删后增的操作
                tatrade.doSettle("do",sAllData);
                //end-- zhouxiang MS01744 	系统需支持基金分红时，现金分红在分红转投日之前的情况    
                // -----------add by guojianhua  2010 09 25 
                operType=4;
                //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "TA交易结算";
                this.setFunName("tatradesettleview");
                this.setModuleName("settlecenter");
                this.setRefName("005099");
                //delete by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A
                //logOper = SingleLogOper.getInstance();
                logOper.setIData(this,operType, pub);
                //---------------------end-------------------
                tatrade.changeSettleState("do");
            } else {
                strError = "TA交易反结算出错";
                tatrade.setSNums(nums);//modified by yeshenghong 20120725 story2633 TA交易结算优化
//                sArr = sAllData.split("\r\n");
//                for (int i = 0; i < sArr.length; i++) {
//                    tatrade.parseRowStr(sArr[i]);
//                    tatrade.doSettle("undo");
//                  
//                }
                // -----------add by guojianhua  2010 09 25 
                tatrade.doDelete();
                operType=5;
                //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "TA交易反结算";
                this.setFunName("tatradesettleview");
                this.setModuleName("settlecenter");
                this.setRefName("005099");
                //delete by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A
                //logOper = SingleLogOper.getInstance();
                logOper.setIData(this,operType, pub);
                //---------------------end-------------------
                tatrade.changeSettleState("undo");
            }
            strReturn = "true";
            return strReturn;
        } catch (Exception e) {
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	try{
        		isError = true;
        		errorInfo = e.getMessage();
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}finally{
            	throw new YssException(strError + "\r\n" + e.getMessage(), e);
            }
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally{
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	if(this.logOper != null){
        		this.insertLog(this.logSumCode, operType, operItem, isError, errorInfo);
        		if(!this.comeFromDD){
    				logOper.setDayFinishIData(this, operType, "sum", pub, isError," ", 
    						new Date(),new Date(), new Date(), 
    						errorInfo, logStartTime, logSumCode, new Date());
        		}
        	}
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
        }
    }
    
    /**
     * add by songjie 2013.01.06
     * STORY #2343 QDV4建行2012年3月2日04_A
     * 保存交易结算业务日志
     */
    public void insertLog(String logSumCode,int operType,
    		String operItem, boolean isError, String errorInfo) throws YssException{
    	String[] strArrNums = null;
    	int allRecord = 0;
    	int allCount = 0;
    	String strNums = "";
    	StringBuffer buf = new StringBuffer(); 
    	String strSql = "";
    	ResultSet rs = null;
    	HashMap hmSec = new HashMap();
    	String logInfos = "";
        String key = "";
        String[] keys = null;
        String portCode = "";
        String BargainDate = "";
        String logInfo = "";
        Date logStartTime = null;
    	try{
			if (this.nums != null && this.nums.length() > 0) {
				strArrNums = this.nums.split(",");
				allRecord = strArrNums.length; // 得到总的结算数
				allCount = (allRecord % 1000 == 0 ? allRecord / 1000 : allRecord / 1000 + 1);// 得到循环次数
			}
			for (int i = 0; i < allCount; i++) {
				if (allCount == 1) {
					strNums = this.nums;
				} else {
					int iNext = allCount - i;

					for (int j = i * 1000; j < (iNext > 1 ? (i + 1) * 1000 : strArrNums.length); j++) {
						buf.append(strArrNums[j]).append(",");
					}

					strNums = buf.toString();
					buf.delete(0, buf.length());

					if (strNums.length() > 1) {
						strNums = strNums.substring(0, strNums.length() - 1);
					}
				}

				strSql = " select distinct a.FPortCode, a.FCashAccCode, a.FTradeDate, " + 
				         " a.FPortClsCode, t.FSellTypeName from " + 
						 pub.yssGetTableName("Tb_Ta_Trade") + 
						 " a left join (select FSellTypeCode,FSellTypeName from " + 
						 pub.yssGetTableName("Tb_Ta_Selltype") + ") t "+ 
						 " on a.FSellType = t.FSellTypeCode " + 
						 " where a.FNum in(" + operSql.sqlCodes(strNums)+ ") ";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					logInfo = "现金账户："+ rs.getString("FCashAccCode") + " - "
							+ "成交日期：" + YssFun.formatDate(rs.getDate("FTradeDate"), "yyyy-MM-dd") + " - "
							+ "销售类型：" + rs.getString("FSellTypeName");

					if (hmSec.get(rs.getString("FPortCode") + "\t"
							+ YssFun.formatDate(rs.getDate("FTradeDate"), "yyyy-MM-dd")) == null) {
						hmSec.put(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FTradeDate"), "yyyy-MM-dd"), logInfo);
					} else {
						logInfos = (String) hmSec.get(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FTradeDate"), "yyyy-MM-dd"));
						if (logInfos.indexOf(logInfo) == -1) {
							logInfos += "\r\n" + logInfo ;
						}

						hmSec.put(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FTradeDate"), "yyyy-MM-dd"), logInfos);
					}
				}

				dbl.closeResultSetFinal(rs);
			}
    		
        	if(hmSec.size() > 0){
        		Iterator iter = hmSec.keySet().iterator();
        		while(iter.hasNext()){
        			key = (String)iter.next();
        			keys = key.split("\t");
        			portCode = keys[0];
        			BargainDate = keys[1];
        			logInfo = (String)hmSec.get(key);
        			logStartTime = new Date();
        			if(logOper != null){
        				logOper.setDayFinishIData(this, operType, operItem, pub, isError,portCode, 
        						YssFun.parseDate(BargainDate),new Date(), YssFun.parseDate(BargainDate), 
        						((errorInfo.trim().length() == 0) ? logInfo : (errorInfo + "\r\n" + logInfo)), 
        						logStartTime, logSumCode, new Date());
        			}
        		}
        	}
    	}catch(Exception e){
    		throw new YssException("插保存业务日志出错！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
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
    public void parseRowStr(String sRowStr) {
        String[] sReqAry = null;
        sReqAry = sRowStr.split("\f\f");
        settleState = sReqAry[2];
        sAllData = sReqAry[0];
        nums = sReqAry[1].replaceAll("\t", ",");
    }

//   public String settleData(String sRowStr) throws YssException {
//      String strSql = "", strTmpSql = "";
//      String strError = "", strReturn = "false";
//      String[] sReqAry, sTradeAry;
//      String sSettleState = "";
//      int operType = 0;
//      try {
//
//         sReqAry = sRowStr.split("\f\f");
//         sSettleState = sReqAry[0];
//         sTradeAry = sReqAry[1].split("\t");
//         strTmpSql = sReqAry[1].replaceAll("\t",",");
    /*
             for (int i = 0; i < sTradeAry.length; i++) {
                strTmpSql = strTmpSql + " FNum = '" + sTradeAry[i] + "' or";
                strSql = strSql + " '" + sTradeAry[i] + "',";
             }
             if (strTmpSql.length() > 0) {
                strTmpSql = " ( " + strTmpSql.substring(0, strTmpSql.length() - 2) +
                      " ) ";
             }
             if (strSql.length() > 0) {
                strSql = " in ( " + strSql.substring(0, strSql.length() - 1) +
                      " ) ";
             }
     */
//
//         OperFunDealBean operfun = (OperFunDealBean) pub.getOperDealCtx().
//               getBean("operfun");
//         operfun.setYssPub(pub);
//         if (sSettleState.equalsIgnoreCase("undo")) {
//            strError = "交易反结算出错";
//            operfun.unCreateCashTransfer(strTmpSql);
//            //-----------------------------------------
//            OperateLogBean log = new OperateLogBean();
//            log.setOperateType(5);
//            operType = 5;
//            log.setOperateResult("1");
//            log.setYssPub(pub);
//            log.insertLog(this);
//            //----------------------------------------
//
//         }
//         else {
//            strError = "交易结算出错";
//            operfun.createCashTransfer(strTmpSql);
//            //-----------------------------------------
//            OperateLogBean log = new OperateLogBean();
//            log.setOperateType(4);
//            operType = 4;
//            log.setOperateResult("1");
//            log.setYssPub(pub);
//            log.insertLog(this);
//            //----------------------------------------
//         }
//         strReturn = "true";
//      }
//      catch (Exception e) {
//         //-----------------------------------------
//         OperateLogBean log = new OperateLogBean();
//         log.setOperateType(operType);
//         log.setOperateResult("0");
//         log.setYssPub(pub);
//         log.insertLog(this);
//         //----------------------------------------
//
//         strReturn = "false";
//         throw new YssException(strError + "\r\n" + e.getMessage(), e);
//      }
//      finally {
//
//      }
//      return strReturn;
//
//   }
//--- 【STORY #2686 要求系统能做针对RQFII的分红，以及红利转投 】  add by jsc 20120620 start --- 
    
    
    /**
     * 通过判断交易类型来判断模式，
     * 分红类型 03  金额方向  无         数量方向 无   --- 手工   true
     * 分红类型 03  金额方向  流出    数量方向 无   --- 自动  false
     * @return
     * @throws YssException
     */
    private boolean getDealMode()throws YssException{
    	
    	
    	StringBuffer queryBuf = new StringBuffer();
    	ResultSet rs = null;
      try{
    	  
    	  queryBuf.append(" select 1 from ").append(pub.yssGetTableName("tb_ta_selltype")).append(" where fcheckstate=1 and fselltypecode='03' and fcashind=0 and famountind=0 ");
    	  
    	  rs = dbl.openResultSet(queryBuf.toString());
    	  if(rs.next()){
    		  return true;
    	  }else{
    		  return false;
    	  }
      }catch(Exception e){
    	  throw new YssException("判断Ta分红处理方式出错... ...");
      }	finally{
    	  queryBuf.setLength(0);
    	  dbl.closeResultSetFinal(rs);
      }
    }
    //--- end --- 
}
