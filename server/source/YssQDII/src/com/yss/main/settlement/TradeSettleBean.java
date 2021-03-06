package com.yss.main.settlement;

import com.yss.main.dao.*;
import com.yss.dsub.*;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.log.*;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import com.yss.util.YssFun;

import com.yss.main.operdata.OperationDataBean;
import com.yss.main.operdeal.BaseTradeSettlement;

public class TradeSettleBean
    extends BaseDataSettingBean implements IClientOperRequest {
    private String settleState = "";
    private String settleDate = "";
    private String nums = "";
    private String desc = "";
    private boolean compTemp;
    private String params = ""; //传实际结算帐户等参数
    //edit by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
    public SingleLogOper logOper; //add by yanghaiming 20101022 MS01538增加日志
    //---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start---//
    public String logSumCode = "";
    public boolean comeFromDD = false;
    //---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A end---//
    
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

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getNums() {
        return nums;
    }

    public boolean isCompTemp() {
        return compTemp;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public String getDesc() {
        return desc;
    }

    public String getParams() {
        return params;
    }

    public TradeSettleBean() {
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
     *
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        String strError = "";
        int operType = 0;
        String strReturn = "false";
        //---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start---//
  	    String logInfo = null;//记录runStatus 相关内容
  	    Date logStartTime = new Date();//业务子项开始时间
        DayFinishLogBean df = new DayFinishLogBean();
        boolean isError = false;//判断是否报错
        String operItem = "";//业务子项
        String errorInfo = "";
        //---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A end---//
        try {
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	df.setYssPub(pub);
        	this.setFunName("tradesettle");//设置功能调用代码
        	if(this.logSumCode.trim().length() == 0){
        		logSumCode = df.getLogSumCodes();//日志汇总编号
        	}
        	if(this.logOper == null){
        		logOper = SingleLogOper.getInstance();
        	}
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A end---//
        	
            ICashTransfer cashTran = (ICashTransfer) pub.getOperDealCtx().
                getBean("tradesettle");
            cashTran.setYssPub(pub);
            cashTran.init(this);
            if (settleState.equalsIgnoreCase("undo")) {
                strError = "交易反结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start
                operItem = "交易反结算";
                cashTran.cashOutTarget();
                //-----------------------------------------
                OperateLogBean log = new OperateLogBean();
                log.setOperateType(5);
                operType = 5;
                log.setOperateResult("1");
                log.setYssPub(pub);
                log.insertLog(this);
                //----------------------------------------

            } else if (settleState.equalsIgnoreCase("do")) {
                strError = "交易结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "交易结算";
                cashTran.cashInTarget();
                //-----------------------------------------
                OperateLogBean log = new OperateLogBean();
                log.setOperateType(4);
                operType = 4;
                log.setOperateResult("1");
                log.setYssPub(pub);
                log.insertLog(this);
            } else if (settleState.equalsIgnoreCase("LSettle")) {
                strError = "延迟结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "延迟结算";
                laterSettle(nums);
                //-----------------------------------------
//                OperateLogBean log = new OperateLogBean();
//                log.setOperateType(21);
                //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
                operType = 21;
//                log.setOperateResult("1");
//                log.setYssPub(pub);
//                log.insertLog(this);
                //add by yanghaiming 20101022 MS01538
                TradeSettleBean data = new TradeSettleBean();
				data.setYssPub(pub);
				data = this;
				//delete by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A
                //logOper = SingleLogOper.getInstance();
                logOper.setIData(data, YssCons.OP_ycjs, pub);
            } else if (settleState.equalsIgnoreCase("LSettleDay")) {
                strError = "结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "交易结算";
                /**Start 20131009 added by liubo.Bug #80715.QDV4赢时胜(上海)2013年10月08日01_B
                 * 交易数据结算的逻辑和变更实际结算日期的代码顺序调整一下，
                 * 使结算时更改实际交易日期的逻辑不会影响到这里*/
                cashTran.cashInTarget(); //延迟结算到结算时多条产生的资金调拔 1217 by liyu
                laterSettleDay(nums);
                /**End 20131009 added by liubo.Bug #80715.QDV4赢时胜(上海)2013年10月08日01_B*/
                //-----------------------------------------
                OperateLogBean log = new OperateLogBean();
                log.setOperateType(5);
                operType = 5;
                log.setOperateResult("1");
                log.setYssPub(pub);
                log.insertLog(this);
            } 
            //---------------add by guojianhua 2010 09 28 MS01538 完善操作类型日志
            else if (settleState.equalsIgnoreCase("rollback")) {
                strError = "回转出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "交易回转";
                rollback(nums);
                
//                OperateLogBean log = new OperateLogBean();
//                log.setOperateType(22);
                //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
                operType = 22;
//                log.setOperateResult("1");
//                log.setYssPub(pub);
//                log.insertLog(this);
              //add by yanghaiming 20101022 MS01538
                TradeSettleBean data = new TradeSettleBean();
				data.setYssPub(pub);
				data = this;
				//delete by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A
                //logOper = SingleLogOper.getInstance();
                logOper.setIData(data, YssCons.OP_hz, pub);
            }else if (settleState.equalsIgnoreCase("unrollback")) {
             strError = "反回转出错";
             //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
             operItem = "交易反回转";
             rollback(nums);
             //cashTran.cashInTarget();
             //-----------------------------------------
//             OperateLogBean log = new OperateLogBean();
//             log.setOperateType(23);
             //add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A
             operType = 23;
//             log.setOperateResult("1");
//             log.setYssPub(pub);
//             log.insertLog(this);
           //add by yanghaiming 20101022 MS01538
             TradeSettleBean data = new TradeSettleBean();
			 data.setYssPub(pub);
			 data = this;
			 //delete by songjie 2013.01.07 STORY #2343 QDV4建行2012年3月2日04_A
             //logOper = SingleLogOper.getInstance();
             logOper.setIData(data, YssCons.OP_fhz, pub);

         }
            //-------------end---------------
            else if (settleState.equalsIgnoreCase("OneLsettle")) {
                strError = "结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "交易结算";
                rollback(nums);
                //cashTran.cashInTarget();
                //-----------------------------------------
                OperateLogBean log = new OperateLogBean();
                log.setOperateType(5);
                operType = 5;
                log.setOperateResult("1");
                log.setYssPub(pub);
                log.insertLog(this);

            } else if (settleState.equalsIgnoreCase("oneSettle")) {
                strError = "结算出错";
                //add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A
                operItem = "交易结算";
                oneSettle(nums);
                cashTran.cashInTarget(); //延迟结算到结算时单条产生的资金调拔 1217 by liyu
                OperateLogBean log = new OperateLogBean();
                log.setOperateType(5);
                operType = 5;
                log.setOperateResult("1");
                log.setYssPub(pub);
                log.insertLog(this);
            }

            strReturn = "true";
            return strReturn;
        } catch (Exception e) {
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	try{
        		isError = true;
        		errorInfo = e.getMessage();

        		OperateLogBean log = new OperateLogBean();
        		log.setOperateType(operType);
        		log.setOperateResult("0");
        		log.setYssPub(pub);
        		log.insertLog(this);
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}finally{
        		throw new YssException(strError + "\r\n" + e.getMessage(), e);
        	}
        	//---add by songjie 2013.01.04 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally{
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	if(this.logOper != null){
        		this.insertLog(this.logSumCode, operType, operItem, isError, nums, errorInfo);
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
    		String operItem, boolean isError,String tradeNum, String errorInfo) throws YssException{
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

				strSql = " select distinct a.FPortCode, a.FSecurityCode, a.FBargainDate, t.FTradeTypeName from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " a left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_Tradetype) t "
						+ " on a.FTradeTypeCode = t.FTradeTypeCode " + " where a.FNum in(" + operSql.sqlCodes(strNums)
						+ ") ";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					logInfo = "证券代码：" + rs.getString("FSecurityCode") + " - "
							+ "成交日期：" + YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd") + " - "
							+ "交易类型：" + rs.getString("FTradeTypeName");

					if (hmSec.get(rs.getString("FPortCode") + "\t"
							+ YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd")) == null) {
						hmSec.put(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"), logInfo);
					} else {
						logInfos = (String) hmSec.get(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"));
						if (logInfos.indexOf(logInfo) == -1) {
							logInfos += "\r\n" + logInfo ;
						}

						hmSec.put(rs.getString("FPortCode") + "\t"
								+ YssFun.formatDate(rs.getDate("FBargainDate"), "yyyy-MM-dd"), logInfos);
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
        settleState = sReqAry[0];
        nums = sReqAry[1].replaceAll("\t", ",");
        if (sReqAry.length > 2) {
            desc = sReqAry[2];
            settleDate = sReqAry[3];
        }
        if (sReqAry.length > 4) {
            params = sReqAry[4];
        }
    }

    public void oneSettle(String nums) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {

            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FFactSettleDate = " + dbl.sqlDate(settleDate) +
                ",FSettleState = 1" +
                ",FSettleDesc = " + dbl.sqlString(desc) +
                " where FNum in (" + operSql.sqlCodes(nums) + ")";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    public void laterSettle(String nums) throws
        YssException {

        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        //---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        boolean isError = false;
        Date logStartTime = new Date();
        String errorInfo = "";
        //---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "update " + pub.yssGetTableName("tb_Data_subTrade") +
                " set FSettleState = 3" +
                " ,FFactSettleDate = " + dbl.sqlDate("9998-12-31") +
                " where FNum in (" + operSql.sqlCodes(nums) + ")";
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	try{
        		isError = true;
        		errorInfo = e.getMessage();
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
            throw new YssException(e.getMessage(), e);
        } finally {
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A start---//
        	if(this.logOper != null){
        		if(!this.comeFromDD){
    				logOper.setDayFinishIData(this, 21, "sum", pub, isError," ", 
    						new Date(),new Date(), new Date(), 
    						errorInfo, logStartTime, logSumCode, new Date());
        		}
        		insertLog(this.logSumCode, 21,"延迟结算", isError,nums,errorInfo);
        	}
        	//---add by songjie 2013.01.06 STORY #2343 QDV4建行2012年3月2日04_A end---//
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    public void laterSettleDay(String nums) throws
        YssException {

        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {

            conn.setAutoCommit(false);
            bTrans = true;
            /**start add by huangqirong 2013-7-24 Story #4198 把当前日期改为自定义的结算日期  */
            strSql = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                " set FFactSettleDate = " + dbl.sqlDate(this.getSettleDate()) + //dbl.sqlDate(new java.util.Date()) +
                ",FSettleState = 1" +
                " where FNum in (" + operSql.sqlCodes(nums) + ")";
            /**end add by huangqirong 2013-7-24 Story #4198 把当前日期改为自定义的结算日期*/
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    public void rollback(String nums) throws YssException {
        String sqlStr = "";
        //String[] str = sMutilRowStr.split("\t");
        String[] str = nums.split("\t");
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            //外层增加了一个if
            if (settleState.equalsIgnoreCase("rollback")) {
                sqlStr = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " set FSettleState = 2, FFactSettleDate = " + dbl.sqlDate(settleDate) +
                    " ,FSettleDesc = " + dbl.sqlString(desc) +
                    " where FNum in(" + operSql.sqlCodes(nums) + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } else if (settleState.equalsIgnoreCase("unrollback")) {
                if (nums.split(",").length > 0) {
                    for (int i = 0; i < nums.split(",").length; i++) {
                        conn.setAutoCommit(false);
                        bTrans = true;
                        sqlStr = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                            " set FSettleState = 0 " +
                            " ,FFactSettleDate = ( select FSettleDate from " +
                            pub.yssGetTableName("Tb_Data_SubTrade") +
                            " where FNum = " + dbl.sqlString(nums.split(",")[i]) + ")" +
                            " where FNum in(" + dbl.sqlString(nums.split(",")[i]) + ")";
                        dbl.executeSql(sqlStr);
                    }
                    conn.commit();
                    bTrans = false;
                    conn.setAutoCommit(true);

                } else {
                    sqlStr = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                        " set FSettleState = 0 " +
                        " ,FFactSettleDate = ( select FSettleDate from " +
                        pub.yssGetTableName("Tb_Data_SubTrade") +
                        " where FNum = " + operSql.sqlCodes(nums) + ")" +
                        " where FNum in(" + operSql.sqlCodes(nums) + ")";
                }
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } else if (settleState.equalsIgnoreCase("OneLsettle")) {
                sqlStr = "update " + pub.yssGetTableName("Tb_Data_SubTrade") +
                    " set FFactSettleDate =" + dbl.sqlDate(settleDate) +
                    " ,FSettleDesc =" + dbl.sqlString(this.desc) +
                    ",FSettleState = 1" +
                    " where FNum in " + dbl.sqlString(nums);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }

        } catch (Exception ex) {
            if (str[1].equalsIgnoreCase("2")) {
                throw new YssException("回转操作失败");
            } else if (str[1].equalsIgnoreCase("3")) {
                throw new YssException("延迟结算操作失败");
            }

        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

}
