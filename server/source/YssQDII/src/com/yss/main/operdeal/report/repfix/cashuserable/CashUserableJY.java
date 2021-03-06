package com.yss.main.operdeal.report.repfix.cashuserable;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.netvalueviewpl.*;
import com.yss.main.operdeal.report.repfix.cashuserable.pojo.*;
import com.yss.main.parasetting.*;
import com.yss.main.report.*;
import com.yss.util.*;

public class CashUserableJY
    extends BaseBuildCommonRep {
    public CashUserableJY() {
    }

    private CommonRepBean repBean;
    private java.util.Date startDate = null; //期初日期
    private java.util.Date endDate = null; //期末日期
    private String sPort = ""; //组合代码
    private String holiday = ""; //节假日
    private FixPub fixPub = null;

    private String cashAccounts = ""; //所有从前台传到后台的现金帐户
    private String preScale = ""; //预估申购款比例
    private String pScale = ""; //预估赎回款比例

    private HashMap cashEndMap = new HashMap(); //期末额
    private HashMap foreignMap = new HashMap(); //外币帐户,key是外币帐户，value是外币折算成人民币的金额
    private HashMap RmbMap = new HashMap(); //人民币帐户
    private ArrayList preTaList = new ArrayList();
    private ArrayList preCashList = new ArrayList();
    private String preADays = ""; // 预估申购结算延迟天数
    private String preRDays = ""; //赎回结算延迟天数
    private int days = 0; //预估的总天数
    private java.util.Date maxNetDate = null; //获取有最大净值日期的那一天

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildResult(this.startDate, this.endDate, this.sPort);
        return sResult;
    }

    /**
     * initBuildReport
     *
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
        repBean = (CommonRepBean) bean;
        this.parse(repBean.getRepCtlParam());
    }

    public void parse(String str) throws YssException {
        String[] sReq = str.split("\n");
        try {
            this.startDate = YssFun.toDate(sReq[0].split("\r")[1]);
            this.endDate = YssFun.toDate(sReq[1].split("\r")[1]);
            this.sPort = sReq[2].split("\r")[1];
            this.holiday = sReq[3].split("\r")[1];
            this.cashAccounts = sReq[4].split("\r")[1];
            if (sReq.length > 5) {
                this.preScale = sReq[5].split("\r")[1];
            } else {
                this.preScale = "1";
            }
            if (sReq.length > 6) {
                this.preADays = sReq[6].split("\r")[1];
            } else {
                this.preADays = "3";
            }
            if (sReq.length > 7) {
                this.preRDays = sReq[7].split("\r")[1];
            } else {
                this.preRDays = "7";
            }
            if (sReq.length > 8) {
                this.pScale = sReq[8].split("\r")[1];
            } else {
                this.pScale = "1";
            }
        } catch (Exception e) {
            throw new YssException("解析参数出错", e);
        }
    }

    protected String buildResult(java.util.Date startDate,
                                 java.util.Date endDate, String sPort) throws
        YssException {
        int n = 0; //alter by 陈嘉 这个变量用做显示 T+ n 日
        String strResult = "";
        ResultSet rs = null;
        String strSql = "";
        String result[] = null;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        //List list = new ArrayList();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        java.util.Date titleDate = null;
        StringBuffer buf = null;
        StringBuffer finBuf = new StringBuffer();
        String[] arrAcc = null; //由于从前台传过来的现金帐户是  帐户1,帐户2这样的行式的，所有要用，分割后放入数组中;
        int days = 0;
        try {
            this.days = YssFun.dateDiff(this.startDate, this.endDate) + 1; //获取在头表上显示的天数
            getMaxNetValue();
            // getDays();
            getPreTaData(); //获取需要预估的TA数据
            getPreCashData(); //获取需要预估的现金数据
            arrAcc = this.cashAccounts.split(",");
            
            //--start- dongqingsong 3013-05-25 story 3945
    		String Querydate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            this.createTmpTbale();
            this.deleteTmpTbale(Querydate, this.sPort);
            //--end-dongqingsong 3013-05-25 story 3945

            for (int i = 0; i < this.days; i++) {
                titleDate = YssFun.addDay(this.startDate, i);
                buf = new StringBuffer();
                //对日期是否是节假日进行一个判断 节假日不显示 alter by 陈嘉
                if (!this.isHoliday(titleDate) ||
                    YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                    buf.append(YssFun.formatDate(titleDate)).append(",");
                    if (i == 0) {
                        buf.append("T" + "日").append(",");
                    } else {
                        buf.append("T+" + n + "日").append(",");
                    }
                    n++; //alter by 陈嘉 只有是假日才加1
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    finBuf.append(fixPub.buildRowCompResult(buf.toString(),
                        "DS00155")).
                        append("\r\n");
                }
                for (int k = 0; k < arrAcc.length; k++) {
                    result = this.getSingleData(titleDate, arrAcc[k]);
                    //add dongqingsong  stroy 3945 
                   if(result != null){
                	  this.savaRepHXCash(titleDate,result,this.endDate,i);
                   }
                    //add dongqingsong  stroy 3945 
                    
                    if (result != null) {
                        for (int j = 0; j < result.length; j++) {
                            finBuf.append(fixPub.buildRowCompResult(result[j],
                                "DS00155")).
                                append("\r\n");
                        }

                    }
                }
                //alter by 陈嘉
                if (!this.isHoliday(titleDate) ||
                    YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                    getFinlData(finBuf);
                }
            }
            if (finBuf.toString().length() > 2) {
                strResult = finBuf.toString().substring(0,
                    finBuf.toString().length() - 2);
            }
            return strResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void getPreCashData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        CashBean tempCash = null;
        try {
            if (dbl.yssTableExist("preCash")) { //判断是否有tempta表存在
                strSql = "select * from preCash "; //读取预估的现金
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    tempCash = new CashBean();
                    tempCash.setBeginDate(rs.getDate("FBeginDate"));
                    tempCash.setEndDate(rs.getDate("FEndDate"));
                    tempCash.setPayDate(rs.getDate("FPayDate"));
                    tempCash.setAccount1(rs.getString("FCashAccount"));
//               tempCash.setAccount2(rs.getString("cashAccount2"));
                    tempCash.setResume(rs.getString("FResume"));
                    tempCash.setMoney(rs.getDouble("FMoney"));
                    tempCash.setCashWay(rs.getString("FInOut"));
                    preCashList.add(tempCash); //把读到的预估数据放入list中
                }
            }
            dbl.closeResultSetFinal(rs); //关闭记录集

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally{
        	dbl.closeResultSetFinal(rs);	//20140225 added by liubo.Bug #89368.最后需要关闭ResultSet
        }
    }

//-----------------------------------获取头寸表上的最后一天
    /*public void getDays() throws YssException {
       //String strSql = "";
       //ResultSet rs = null;
      // java.util.Date endDate = null;
       try {
          BaseOperDeal deal = new BaseOperDeal();
          deal.setYssPub(pub);
          //---------------------------------计算在头寸表上显示的最后一天---------------------
          if (dbl.yssTableExist("preTA")) { //判断是否有tempta表存在
             strSql = "select max(FApplyDate) as sqdate from preTA";
             rs = dbl.openResultSet(strSql);
             while (rs.next()) {
                if (rs.getDate("sqdate") != null) {
                   endDate = deal.getWorkDay(this.holiday, //如果有预估的ta数据就根据节假日群代码和预估数据中的最大日期推算出现金头寸表上的最后一天
                                             rs.getDate("sqdate"), 10);
                }
             }
          }
          if (endDate == null) {
     endDate = YssFun.addDay(this.startDate, 10); //如果没有预估的TA数据,那么在头寸表上体现的日期是开始日期+10天
          }
       }
       catch (Exception e) {
          throw new YssException(e.getMessage());
       }
       finally {
          dbl.closeResultSetFinal(rs);
       }
        }*/

    public void getPreTaData() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        TaBean tempTa = null;
        try {
            if (dbl.yssTableExist("preTA")) { //判断是否有tempta表存在
                strSql =
                    "select * from preTA where FApplyDate not in(select FTRADEDATE from " +
                    pub.yssGetTableName("tb_ta_trade") +
					//fanghaoln 20100226 MS00974 QDV4交银施罗德2010年2月4日01_B 集成zhouss的修改
					" where FSellType in ('01','02')) AND ffundcode = '" + sPort + "' order by FApplyDate"; //-zhouss 20090721 增加对组合代码的判断
					//------------------------------end ---MS00974---------------------------------------------------
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    tempTa = new TaBean();
                    tempTa.setFundCode(rs.getString("FFundCode")); //基金代码
                    tempTa.setTradeDate(rs.getDate("FApplyDate")); //申请日期
                    tempTa.setTradeType(rs.getString("FOperType")); //交易类型
                    if (rs.getString("FOperType").equals("022")) {
                        tempTa.setTradeAmount(rs.getDouble("FApplyAmount") *
                                              YssFun.toDouble(this.preScale));
                    } else {
                        tempTa.setTradeAmount(rs.getDouble("FApplyAmount") *
                                              YssFun.toDouble(this.pScale) *
                                              YssD.round(this.getUnitValue(this.
                            maxNetDate), 3)); //赎回有的客户要有比例，有的客户不需要
                    }
                    preTaList.add(tempTa);
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally{
        	dbl.closeResultSetFinal(rs);	//20140225 added by liubo.Bug #89368.最后需要关闭ResultSet
        }
    }

    public String[] getSingleData(java.util.Date titleDate, String cashAccCode) throws
        YssException {
        String strSql = "";
        String[] arrOccurMoney = null; //发生额
        double[] arrTotalMoney = null; //库存
        StringBuffer buf = new StringBuffer();
//----------------------------------同一个帐户的期初金额----------------------------------
        try {
            setTotalMoney(titleDate, cashAccCode, buf); //获取汇总数据
            arrOccurMoney = this.getOccurMoney(titleDate, cashAccCode, buf); //获取发生额
            setFinalData(titleDate, cashAccCode); //设置人民币+外币折算成人民币的金额
            return arrOccurMoney;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

//--------------------------------------获取汇总数据---------------------------------
    /**
     * @param titleDate
     * @param cashAccCode
     * @param buf
     * @throws YssException
     */
    public void setTotalMoney(java.util.Date titleDate, String cashAccCode,
                              StringBuffer buf) throws YssException {
        //double[] money = new double[4]; //里面放的是昨日余额，当日汇总的流出，当日汇总的流入，当日的期末余额//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        double beginMoney = 0.0; //期初余额
        double endMoney = 0.0; //期末余额
        //add by hongqingbing Story_13002_头寸预测表需求变更  20131014
        String inOutMoney = "";
        double inMoney = 0.0; //流入金额
        double outMoney = 0.0; //流出金额
        ResultSet rs = null;
        String strSql = "";

        Iterator it = null;
        TaBean tempTa = null;
        CashBean tempCash = null;
        java.util.Date taSettleDate = null;
        double preAMoney = 0.0;
        double preRMoney = 0.0;
        String curyCode = "";
        double preMoney = 0.0; //预留人民币金额
        double reMoney = 0.0; //逆回购到期流入金额

        try {
            curyCode = getCuryCode(cashAccCode);
            BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
            it = preTaList.iterator();
            while (it.hasNext()) {
//----------------------------------------------------------预估TA--------------------------
                tempTa = (TaBean) it.next();
                if (tempTa.getTradeType().equals("022")) { //申购
                    taSettleDate = deal.getWorkDay(this.holiday, tempTa.getTradeDate(),
                        YssFun.toInt(this.preADays));
                    if (taSettleDate.equals(titleDate)) {
                        if (curyCode.equals("CNY")) {
                            preAMoney = tempTa.getTradeAmount();
                        }
                    }
                } else { //赎回
                    taSettleDate = deal.getWorkDay(this.holiday, tempTa.getTradeDate(),
                        YssFun.toInt(this.preRDays));
                    if (taSettleDate.equals(titleDate)) {
                        if (curyCode.equals("CNY")) {
                            preRMoney = tempTa.getTradeAmount();
                        }
                    }
                }
            }
            /*    if (curyCode.equals("CNY") &&
                    YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                   preMoney = this.getNetValue(startDate);
                }*/
            if (titleDate.equals(this.startDate)) { //如果是头寸表上的第一天，那么昨日余额=前一天的库寸
                strSql = " select FCashAccCode,FAccBalance from " +
                    pub.yssGetTableName("tb_stock_cash") +
                    " where FCashAccCode=" + dbl.sqlString(cashAccCode) +
                    " and FStorageDate=" +
                    dbl.sqlDate(maxNetDate) + " and FPORTCODE=" +
                    dbl.sqlString(this.sPort) +
                    " and FYearMonth = " +
                    dbl.sqlString(YssFun.formatDate(maxNetDate, "yyyy") +
                                  YssFun.formatDate(maxNetDate, "MM")) +
                    " and FCheckState=1";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    beginMoney = rs.getDouble("FAccBalance");

                }
                dbl.closeResultSetFinal(rs); //关闭记录集
            } else { //如果不是头寸表上的第一天那么期初余额=前一天的期末余额
                beginMoney = ( (Double) cashEndMap.get(cashAccCode)).doubleValue();
            }
            
//----edit by hongqingbing Story_13002_头寸预测表需求变更  20131014-------------------
//----------------------------------------流入金额、流出金额--------------------------
            inOutMoney = getInOutMoney(titleDate, cashAccCode, buf);
            buf.setLength(0);
            outMoney = Double.parseDouble(inOutMoney.split("\t")[0]);
            inMoney = Double.parseDouble(inOutMoney.split("\t")[1]);                        
//----end--------------------------------------------------------------------
            
//------------------------------------------同一个帐户的期末金额-------------------

            endMoney = beginMoney + inMoney - outMoney; //期末余额=期初余额+流入-流出
            if (cashEndMap.containsKey(cashAccCode)) {
                cashEndMap.remove(cashAccCode);
            }
            cashEndMap.put(cashAccCode, new Double(endMoney));
            if (!this.isHoliday(titleDate) ||
                YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                buf.append(" ").append(",");
                buf.append(" ").append(",");
                buf.append(". " + cashAccCode).append(",");
                
       
                //MS01206    zhangfa    QDV4汇添富2010年05月26日01_AB    2010.06.04
                //通过现金帐户代码获取现金账户名称
                buf.append(this.getCashAccNameBycode(cashAccCode)).append(",");
                
                
                
                buf.append(beginMoney).append(",");
                buf.append(outMoney).append(",");
                buf.append(inMoney).append(",");
                buf.append(endMoney).append("\f\f");
            }

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //--------add by hongqingbing Story_13002_头寸预测表需求变更   20131014
	//--------------------------计算流出金额、流入金额---------------------------------------------
	private String getInOutMoney(java.util.Date titleDate, String cashAccCode,
			StringBuffer buf) throws YssException {

		ResultSet rs = null;
		String strSql = "";
		Iterator it = null;
		TaBean tempTa = null;
		java.util.Date taSettleDate = null;
		String curyCode = "";
		double preAMoney = 0.0;//申购流入金额
		double preRMoney = 0.0;//赎回流出金额
		double inMoney = 0.0;
		double outMoney = 0.0;

		try {
			curyCode = getCuryCode(cashAccCode);
			BaseOperDeal deal = new BaseOperDeal();
			deal.setYssPub(pub);
			it = preTaList.iterator();
			while (it.hasNext()) {
				// ----------------------------------------------------------预估TA--------------------------
				tempTa = (TaBean) it.next();
				if (tempTa.getTradeType().equals("022")) { // 申购
					taSettleDate = deal.getWorkDay(this.holiday,
							tempTa.getTradeDate(), YssFun.toInt(this.preADays));
					if (taSettleDate.equals(titleDate)) {
						if (curyCode.equals("CNY")) {
							preAMoney = tempTa.getTradeAmount();
						}
					}
				} else { // 赎回
					taSettleDate = deal.getWorkDay(this.holiday,
							tempTa.getTradeDate(), YssFun.toInt(this.preRDays));
					if (taSettleDate.equals(titleDate)) {
						if (curyCode.equals("CNY")) {
							preRMoney = tempTa.getTradeAmount();
						}
					}
				}
			}
				
			// -------------------------------------------基金成立(流入)=----------------------------
			strSql = " select '基金成立(流入)' as FCuryCode,FSettleMoney as inMoney, 0 as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='00' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 "
					+
					// ---------------------------------------------申购款(流入)----------------------------------
					" union "
					+ " select '申购款(流入)' as FCuryCode,sum(FSettleMoney) as inMoney, sum(0) as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='01' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FCashAccCode "
					+
					// ---------------------------------------------赎回款(流出)----------------------------------
					" union "
					+ " select '赎回款(流出)' as FCuryCode,sum(0) as inMoney ,sum(FSettleMoney) as outMoney from "
					+ pub.yssGetTableName("tb_ta_trade ")
					+ " where FSettleDate="
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSellType='02' and  "
					+ " FCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FCashAccCode "
					+
					// ------------------------------------------外汇交易买入(流入)--------------------------
					" union "
					+ " select '外汇交易(买入)' as FCuryCode, sum(FBMoney) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("tb_data_ratetrade")
					+ " where FSettleDate= "
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FBCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FBCashAccCode "
					+
					// ------------------------------------------外汇交易卖出(流出)--------------------------
					" union "
					+ " select '外汇交易(卖出)' as FCuryCode, sum(0) as inMoney ,sum(FSMoney) as outMoney from "
					+ pub.yssGetTableName("tb_data_ratetrade")
					+ " where FSettleDate= "
					+ dbl.sqlDate(titleDate)
					+ " and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FSCashAccCode="
					+ dbl.sqlString(cashAccCode)
					+ "and FCheckState=1 group by FSCashAccCode ";
			// 当是美元帐户的时候,要把全球组合的交易股票的证券清算款算进来
			if (cashAccCode.equals("020102")) {
				strSql = strSql
						+ " union "
						+ " select  '股票证券清算款(流入)' as FCuryCode,  sum(FTotalCost*fbasecuryrate) as inMoney ,sum(0) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1  and  FTradeTypeCode='02' and FPortCode = "
						+ dbl.sqlString(this.sPort)
						+ " and FCashAccCode in ( "
						+ dbl.sqlString(cashAccCode)
						+ ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')"
						+ " and FSettleDate ="
						+ dbl.sqlDate(titleDate)
						+ "group by FCashAccCode "
						+
						// ------------------------------------------股票证券清算款(流出)--------------------------
						" union "
						+ " select  '股票证券清算款(流出)' as FCuryCode, sum(0) as inMoney,sum(FTotalCost*fbasecuryrate) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1  and  FTradeTypeCode='01' and FPortCode = "
						+ dbl.sqlString(this.sPort)
						+ " and FCashAccCode in ("
						+ dbl.sqlString(cashAccCode)
						+ ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')"
						+ " and FSettleDate ="
						+ dbl.sqlDate(titleDate)
						+ "group by FCashAccCode "
						+
						// ------------------------------------------股票分红到帐(流入)--------------------------------
						" union select '股票分红到帐(流入)' as FCuryCode,  sum(FAccruedinterest*fbasecuryrate) as inMoney ,sum(0) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1 and "
						+ " FTradeTypeCode = '06' and FPortCode = "
						+ dbl.sqlString(this.sPort)
						+ " and FCashAccCode in ("
						+ dbl.sqlString(cashAccCode)
						+ ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')"
						+ " and FSettleDate = " + dbl.sqlDate(titleDate)
						+ " group by FCashAccCode ";
			} else {
				strSql = strSql
						+ " union "
						+ " select  '股票证券清算款(流入)' as FCuryCode,  sum(FTotalCost) as inMoney ,sum(0) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1  and  FTradeTypeCode='02' and FPortCode = "
						+ dbl.sqlString(this.sPort)
						+ " and FCashAccCode = "
						+ dbl.sqlString(cashAccCode)
						+ " and FSettleDate ="
						+ dbl.sqlDate(titleDate)
						+ "group by FCashAccCode "
						+
						// ------------------------------------------股票证券清算款(流出)--------------------------
						" union "
						+ " select  '股票证券清算款(流出)' as FCuryCode, sum(0) as inMoney,sum(FTotalCost) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1  and  FTradeTypeCode='01' and FPortCode = "
						+ dbl.sqlString(this.sPort)
						+ " and FCashAccCode = "
						+ dbl.sqlString(cashAccCode)
						+ " and FSettleDate ="
						+ dbl.sqlDate(titleDate)
						+ "group by FCashAccCode "
						+
						// ------------------------------------------股票分红到帐(流入)--------------------------------
						" union select '股票分红到帐(流入)' as FCuryCode,  sum(FAccruedinterest) as inMoney ,sum(0) as outMoney from "
						+ pub.yssGetTableName("Tb_Data_SubTrade")
						+ " where FCheckState = 1 and "
						+ " FTradeTypeCode = '06' and FPortCode = "
						+ dbl.sqlString(this.sPort) + " and FCashAccCode = "
						+ dbl.sqlString(cashAccCode) + " and FSettleDate = "
						+ dbl.sqlDate(titleDate) + " group by FCashAccCode ";

			}

			// ------------------------------------------逆回购证券清算款(流出)--------------------------
			strSql = strSql
					+ " union "
					+ " select  '逆回购证券清算款(流出)' as FCuryCode,sum(0) as inMoney, sum(FTotalCost) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					+ " and FSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode"
					+
					// ------------------------------------------正回购证券清算款(流入)--------------------------
					" union "
					+ " select  '正回购证券清算款(流入)' as FCuryCode, sum(FTotalCost) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='24' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					+ " and FSettleDate ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode"
					+
					// ------------------------------------------逆回购到期(流入)--------------------------
					" union "
					+ " select  '逆回购到期(流入)' as FCuryCode, sum((FTRADEMONEY+FACCRUEDINTEREST)) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = "
					+ dbl.sqlString(this.sPort)
					+ " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode)
					+ " and FMATURESETTLEDATE ="
					+ dbl.sqlDate(titleDate)
					+ "group by FCashAccCode "
					+
					// -------------------------------------------债券兑付(流入)-------------------------------
					" union "
					+ " select  '债券兑付' as FCuryCode, sum(FTradeMoney) as inMoney ,sum(0) as outMoney from "
					+ pub.yssGetTableName("Tb_Data_SubTrade")
					+ " where FCheckState = 1  and  FTradeTypeCode='17' and FPortCode = "
					+ dbl.sqlString(this.sPort) + " and FCashAccCode = "
					+ dbl.sqlString(cashAccCode) + " and FSettleDate ="
					+ dbl.sqlDate(titleDate) + "group by FCashAccCode ";

			System.out.println(strSql);
			rs = dbl.openResultSet(strSql);
			inMoney += preAMoney;//加上申购流入金额
			outMoney += preRMoney;//加上赎回流出金额

			while (rs.next()) {							
				outMoney += rs.getDouble("outMoney");
				inMoney += rs.getDouble("inMoney");
			}
			buf.append(outMoney).append("\t").append(inMoney);
			return buf.toString();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
	//--------------------end----------------------------------------
    
               
//--------------------------------------获取发生额----------------------------------
    public String[] getOccurMoney(java.util.Date titleDate, String cashAccCode,
                                  StringBuffer buf) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String[] arrResult = null;
        Iterator it = null;
        TaBean tempTa = null;
        String preTaStr1 = "";
        String preTaStr2 = "";
        java.util.Date taSettleDate = null;
        String curyCode = "";
        String preRMB = "";
        double preMoney = 0.0;
        CashBean tempCash = null;
        StringBuffer preCashBuf = new StringBuffer();
        String preStr = "";
        String preCashStr = "";
        String[] arrpreCashStr = null;
        double unSettleMoney = 0.0;
        double taTradeMoney = 0.0;
        String unSettle = "";
        try {

            curyCode = getCuryCode(cashAccCode);
            BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
            it = preTaList.iterator();
            while (it.hasNext()) {
//----------------------------------------------------------预估TA--------------------------
                tempTa = (TaBean) it.next();
                if (tempTa.getTradeType().equals("022")) { //申购
                    if (curyCode.equals("CNY")) {
                        taSettleDate = deal.getWorkDay(this.holiday,
                            tempTa.getTradeDate(),
                            YssFun.toInt(this.preADays));
                        if (taSettleDate.equals(titleDate)) {
                            preTaStr1 =
                                " union " +
                                " select '预估申购款(流入)' as FCuryCode,+" +
                                tempTa.getTradeAmount() +
                                " as inMoney, 0 as outMoney from dual";
                        }
                    }
                } else { //赎回
                    if (curyCode.equals("CNY")) {
                        taSettleDate = deal.getWorkDay(this.holiday,
                            tempTa.getTradeDate(),
                            YssFun.toInt(this.preRDays));
                        if (taSettleDate.equals(titleDate)) {
                            preTaStr2 =
                                " union " +
                                " select '预估赎回款(流出)' as FCuryCode, 0 as inMoney," +
                                tempTa.getTradeAmount() +
                                " as outMoney from dual";
                        }
                    }
                }
            }

            if (curyCode.equals("CNY") &&
                YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                preMoney = this.getNetValue(startDate);
                //unSettleMoney = this.getUnSettleMoney();
                taTradeMoney = this.getTaConfigMoney();
                /*   preRMB =
                           " union " +
                           " select '预留人民币金额(流出)' as FCuryCode, 0 as outMoney, " +
                           preMoney + " as outMoney from dual";
                 */
                /*   unSettle =
                         " union " +
                         " select '未清算金额' as FCuryCode, 0 as outMoney, " +
                 (unSettleMoney - taTradeMoney) + " as outMoney from dual";*/

            }

            it = preCashList.iterator();
            while (it.hasNext()) {
                tempCash = (CashBean) it.next();
                if (YssFun.dateDiff(tempCash.getBeginDate(), endDate) >= 0 &&
                    YssFun.dateDiff(tempCash.getEndDate(), endDate) <= 0) {
                    if (cashAccCode.equalsIgnoreCase(tempCash.getAccount1())) {
                        if (titleDate.equals(tempCash.getPayDate())) {
                            if (tempCash.getCashWay().equalsIgnoreCase("1")) {
                                preStr = " union select " +
                                    dbl.sqlString(tempCash.getResume()) +
                                    " as FCuryCode," + tempCash.getMoney() +
                                    " as inMoney,0 as outMoney from dual ";
                            } else {
                                preStr = " union select " +
                                    dbl.sqlString(tempCash.getResume()) +
                                    " as FCuryCode,0 as inMoney," + tempCash.getMoney() +
                                    " as outMoney from dual ";
                            }
                            preCashBuf.append(preStr).append("\t");
                        }
                    }
                }
            }

//-------------------------------------------基金成立(流入)=----------------------------
            strSql =
                " select '基金成立(流入)' as FCuryCode,FSettleMoney as inMoney, 0 as outMoney from " +
                pub.yssGetTableName("tb_ta_trade ") +
                " where FSettleDate=" + dbl.sqlDate(titleDate) +
                " and FPortCode = " + dbl.sqlString(this.sPort) +
                " and FSellType='00' and  " +
                " FCashAccCode=" + dbl.sqlString(cashAccCode) +
                "and FCheckState=1 " +
//---------------------------------------------申购款(流入)----------------------------------
                " union " +
                " select '申购款(流入)' as FCuryCode,sum(FSettleMoney) as inMoney, sum(0) as outMoney from " +
                pub.yssGetTableName("tb_ta_trade ") +
                " where FSettleDate=" + dbl.sqlDate(titleDate) +
                " and FPortCode = " + dbl.sqlString(this.sPort) +
                " and FSellType='01' and  " +
                " FCashAccCode=" + dbl.sqlString(cashAccCode) +
                "and FCheckState=1 group by FCashAccCode " +
//---------------------------------------------赎回款(流出)----------------------------------
                " union " +
                " select '赎回款(流出)' as FCuryCode,sum(0) as inMoney ,sum(FSettleMoney) as outMoney from " +
                pub.yssGetTableName("tb_ta_trade ") +
                " where FSettleDate=" + dbl.sqlDate(titleDate) +
                " and FPortCode = " + dbl.sqlString(this.sPort) +
                " and FSellType='02' and  " +
                " FCashAccCode=" + dbl.sqlString(cashAccCode) +
                "and FCheckState=1 group by FCashAccCode " +
//------------------------------------------外汇交易买入(流入)--------------------------
                " union " +
                " select '外汇交易(买入)' as FCuryCode, sum(FBMoney) as inMoney ,sum(0) as outMoney from " +
                pub.yssGetTableName("tb_data_ratetrade") +
                " where FSettleDate= " + dbl.sqlDate(titleDate) +
                " and FPortCode = " + dbl.sqlString(this.sPort) +
                " and FBCashAccCode=" + dbl.sqlString(cashAccCode) +
                "and FCheckState=1 group by FBCashAccCode " +
//------------------------------------------外汇交易卖出(流出)--------------------------
                " union " +
                " select '外汇交易(卖出)' as FCuryCode, sum(0) as inMoney ,sum(FSMoney) as outMoney from " +
                pub.yssGetTableName("tb_data_ratetrade") +
                " where FSettleDate= " + dbl.sqlDate(titleDate) +
                " and FPortCode = " + dbl.sqlString(this.sPort) +
                " and FSCashAccCode=" + dbl.sqlString(cashAccCode) +
                "and FCheckState=1 group by FSCashAccCode ";
            //当是美元帐户的时候,要把全球组合的交易股票的证券清算款算进来
            if (cashAccCode.equals("020102")) {
                strSql = strSql +
                    " union " +
                    " select  '股票证券清算款(流入)' as FCuryCode,  sum(FTotalCost*fbasecuryrate) as inMoney ,sum(0) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1  and  FTradeTypeCode='02' and FPortCode = " +
                    dbl.sqlString(this.sPort) +
                    " and FCashAccCode in ( " + dbl.sqlString(cashAccCode) + ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')" +
                    " and FSettleDate =" +
                    dbl.sqlDate(titleDate) + "group by FCashAccCode " +
//------------------------------------------股票证券清算款(流出)--------------------------
                    " union " +
                    " select  '股票证券清算款(流出)' as FCuryCode, sum(0) as inMoney,sum(FTotalCost*fbasecuryrate) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1  and  FTradeTypeCode='01' and FPortCode = " +
                    dbl.sqlString(this.sPort) +
                    " and FCashAccCode in (" + dbl.sqlString(cashAccCode) + ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')" +
                    " and FSettleDate =" +
                    dbl.sqlDate(titleDate) + "group by FCashAccCode " +
                    //------------------------------------------股票分红到帐(流入)--------------------------------
                    " union select '股票分红到帐(流入)' as FCuryCode,  sum(FAccruedinterest*fbasecuryrate) as inMoney ,sum(0) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1 and " +
                    " FTradeTypeCode = '06' and FPortCode = " +
                    dbl.sqlString(this.sPort) + " and FCashAccCode in (" + dbl.sqlString(cashAccCode) +
                    ",'020103','020204','020205','020206','020207','020208','020209','020210','020211','020212','020213')" +
                    " and FSettleDate = " +
                    dbl.sqlDate(titleDate) + " group by FCashAccCode ";
            } else {
                strSql = strSql +
                    " union " +
                    " select  '股票证券清算款(流入)' as FCuryCode,  sum(FTotalCost) as inMoney ,sum(0) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1  and  FTradeTypeCode='02' and FPortCode = " +
                    dbl.sqlString(this.sPort) +
                    " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                    " and FSettleDate =" +
                    dbl.sqlDate(titleDate) + "group by FCashAccCode " +
//------------------------------------------股票证券清算款(流出)--------------------------
                    " union " +
                    " select  '股票证券清算款(流出)' as FCuryCode, sum(0) as inMoney,sum(FTotalCost) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1  and  FTradeTypeCode='01' and FPortCode = " +
                    dbl.sqlString(this.sPort) +
                    " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                    " and FSettleDate =" +
                    dbl.sqlDate(titleDate) + "group by FCashAccCode " +
//------------------------------------------股票分红到帐(流入)--------------------------------
                    " union select '股票分红到帐(流入)' as FCuryCode,  sum(FAccruedinterest) as inMoney ,sum(0) as outMoney from " +
                    pub.yssGetTableName("Tb_Data_SubTrade") +
                    " where FCheckState = 1 and " +
                    " FTradeTypeCode = '06' and FPortCode = " +
                    dbl.sqlString(this.sPort) + " and FCashAccCode = " +
                    dbl.sqlString(cashAccCode) + " and FSettleDate = " +
                    dbl.sqlDate(titleDate) + " group by FCashAccCode ";

            }

//------------------------------------------逆回购证券清算款(流出)--------------------------
            strSql = strSql + " union " +
                " select  '逆回购证券清算款(流出)' as FCuryCode,sum(0) as inMoney, sum(FTotalCost) as outMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = " +
                dbl.sqlString(this.sPort) +
                " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                " and FSettleDate =" +
                dbl.sqlDate(titleDate) + "group by FCashAccCode" +
//------------------------------------------正回购证券清算款(流入)--------------------------
                " union " +
                " select  '正回购证券清算款(流入)' as FCuryCode, sum(FTotalCost) as inMoney ,sum(0) as outMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1  and  FTradeTypeCode='24' and FPortCode = " +
                dbl.sqlString(this.sPort) +
                " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                " and FSettleDate =" +
                dbl.sqlDate(titleDate) + "group by FCashAccCode" +
//------------------------------------------逆回购到期(流入)--------------------------
                " union " +
                " select  '逆回购到期(流入)' as FCuryCode, sum((FTRADEMONEY+FACCRUEDINTEREST)) as inMoney ,sum(0) as outMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1  and  FTradeTypeCode='25' and FPortCode = " +
                dbl.sqlString(this.sPort) +
                " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                " and FMATURESETTLEDATE =" +
                dbl.sqlDate(titleDate) + "group by FCashAccCode " +
//-------------------------------------------债券兑付(流入)-------------------------------
                " union " +
                " select  '债券兑付' as FCuryCode, sum(FTradeMoney) as inMoney ,sum(0) as outMoney from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " where FCheckState = 1  and  FTradeTypeCode='17' and FPortCode = " +
                dbl.sqlString(this.sPort) +
                " and FCashAccCode = " + dbl.sqlString(cashAccCode) +
                " and FSettleDate =" +
                dbl.sqlDate(titleDate) + "group by FCashAccCode " +
                preTaStr1 +
                preTaStr2 +
                preRMB +
                unSettle;
            if (preCashBuf.length() > 1) {
                preCashStr = preCashBuf.toString().substring(0,
                    preCashBuf.toString().length() - 1);
                arrpreCashStr = preCashStr.split("\t");
                for (int i = 0; i < arrpreCashStr.length; i++) {
                    strSql = strSql + arrpreCashStr[i];
                }
            }
            
            System.out.println(strSql);
            rs = dbl.openResultSet(strSql);
            //alter by 陈嘉  如果不是节假日和不是最后一天 字符串才进行拼接
            if (!this.isHoliday(titleDate) ||
                YssFun.addDay(startDate, this.days - 1).equals(titleDate)) {
                while (rs.next()) {
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(" ").append(",");
                    buf.append(". " + rs.getString("FCuryCode")).append(",");
                    buf.append("0").append(",");
                    buf.append(rs.getDouble("outMoney")).append(",");
                    buf.append(rs.getDouble("inMoney")).append(",");
                    buf.append("0").append("\f\f");
                }
                arrResult = buf.toString().split("\f\f");
                
            }
            return arrResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //获取证券清算款的SQL语句
    private String getSecRecPay() throws YssException {
        String strSql = "";
        try {
            //------------------------------------------股票证券清算款(流入)--------------------------

            return strSql;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    public String getCuryCode(String cashAccCode) throws YssException {
        try {
            String curyCode = ""; //现金帐户的货币是为了取汇率
            CashAccountBean cash = new CashAccountBean();
            cash.setYssPub(pub);
            cash.setStrCashAcctCode(cashAccCode);
            cash.getSetting();
            curyCode = cash.getStrCurrencyCode();
            return curyCode;

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public void setFinalData(java.util.Date titleDate, String cashAccCode) throws
        YssException {

        double baseRate = 0.0;
        double portRate = 0.0;
        double rmbMoney = 0.0;
        double foreignMoney = 0.0;
        String curyCode = "";
        try {
            curyCode = getCuryCode(cashAccCode);
            baseRate = this.getExchangeRate(curyCode, this.sPort, "base");
            portRate = this.getExchangeRate(curyCode, this.sPort, "port");
            if (curyCode.equals("CNY")) {
                if (this.cashEndMap.containsKey(cashAccCode)) {
                    rmbMoney = ( (Double)this.cashEndMap.get(cashAccCode)).
                        doubleValue();
                }
                if (this.RmbMap.containsKey(cashAccCode)) {
                    this.RmbMap.remove(cashAccCode);
                }
                RmbMap.put(cashAccCode, new Double(rmbMoney));
            } else {
                if (this.cashEndMap.containsKey(cashAccCode)) {
                    foreignMoney = ( (Double)this.cashEndMap.get(cashAccCode)).
                        doubleValue();
                    foreignMoney = foreignMoney * baseRate / portRate;
                }
                if (this.foreignMap.containsKey(cashAccCode)) {
                    this.foreignMap.remove(cashAccCode);
                }
                this.foreignMap.put(cashAccCode, new Double(foreignMoney));
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public void getFinlData(StringBuffer finBuf) throws YssException {
        StringBuffer bufForeign = null;
        StringBuffer bufCny = null;
        Set set = null;
        Iterator it = null;
        double foreignMoney = 0.0;
        double rmbMoney = 0.0;
        try {
            bufForeign = new StringBuffer();
            bufCny = new StringBuffer();
            set = foreignMap.keySet();
            it = set.iterator();
            while (it.hasNext()) {
                foreignMoney = foreignMoney +
                    ( (Double) foreignMap.get( (String) it.next())).doubleValue();
            }
            set = RmbMap.keySet();
            it = set.iterator();
            while (it.hasNext()) {
                rmbMoney = rmbMoney +
                    ( (Double) RmbMap.get( (String) it.next())).doubleValue();
            }
            bufForeign.append(" ").append(",");
            bufForeign.append(" ").append(",");
            bufForeign.append(" ").append(",");
            bufForeign.append(". 外币折算人民币小计").append(",");
            bufForeign.append(" ").append(",");
            bufForeign.append(" ").append(",");
            bufForeign.append(" ").append(",");
            bufForeign.append(String.valueOf(foreignMoney)).append(",");
            finBuf.append(fixPub.buildRowCompResult(bufForeign.toString(),
                "DS00155")).append("\r\n");
//-----------------------------------计算(所有外币+所有人民币)折算成人民币的金额------
            bufCny.append(" ").append(",");
            bufCny.append(" ").append(",");
            bufCny.append(" ").append(",");
            bufCny.append(". 折算人民币小计").append(",");
            bufCny.append(" ").append(",");
            bufCny.append(" ").append(",");
            bufCny.append(" ").append(",");
            bufCny.append(String.valueOf(foreignMoney + rmbMoney)).append(",");
            finBuf.append(fixPub.buildRowCompResult(bufCny.toString(), "DS00155")).
                append("\r\n");

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

//----------------------------------------获取汇率-------------------------------
    public double getExchangeRate(String curyCode, String portCode,
                                  String rateType) throws
        YssException {
        double sResult = 0.0;
        java.util.Date inceptionDate = null;
        java.util.Date navDate = null;
        try {
            inceptionDate = this.getInceptionDate();
            if (this.startDate.equals(inceptionDate)) {
                navDate = startDate;
            } else {
                navDate = YssFun.addDay(startDate, -1);
            }
            BaseOperDeal operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            sResult = operDeal.getCuryRate(navDate, curyCode,
                                           portCode, rateType);
            return sResult;

        } catch (Exception e) {
            throw new YssException("获取汇率出错!", e);
        }
    }

//--------------------------------------获取资产净值------------------------------
    public double getNetValue(java.util.Date startDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        java.util.Date inceptionDate = null;
        java.util.Date navDate = null;
        try {

            inceptionDate = this.getInceptionDate();
            if (this.startDate.equals(inceptionDate)) {
                navDate = startDate;
            } else {
                navDate = maxNetDate;
            }
            strSql = " select FPortMarketValue from " +
                pub.yssGetTableName("tb_data_navdata") +
                " where FKeyCode='TotalValue' and  FNAVDATE=" +
                dbl.sqlDate(navDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FPortMarketValue");
            }
            sResult = YssD.mul(sResult, 0.02);
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取净值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

// ---------------------------------获取单位净值----------------------------------
    public double getUnitValue(java.util.Date startDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double sResult = 0.0;
        java.util.Date inceptionDate = null;
        java.util.Date navDate = null;
        try {
            inceptionDate = this.getInceptionDate();
            if (this.startDate.equals(inceptionDate)) {
                navDate = startDate;
            } else {
                navDate = maxNetDate;
            }
            /**add---huhuichao 2014-1-6 BUG  86517 交银现金头寸预测表在计算预估赎回款时单位净值未区分组合*/
            strSql = " select FPrice from " +
                pub.yssGetTableName("tb_data_navdata") +
                " where FKeyCode='Unit' and  FNAVDATE=" + dbl.sqlDate(navDate)
                + " and FPortCode = "+ dbl.sqlString(sPort);
            /**end---huhuichao 2014-1-6 BUG  86517*/
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult = rs.getDouble("FPrice");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取单位净值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public java.util.Date getInceptionDate() throws YssException {
        PortfolioBean port = new PortfolioBean();
        try {
            port.setYssPub(pub);
            port.setPortCode(this.sPort);
            port.getSetting();
            return port.getInceptionDate();

        } catch (Exception e) {
            throw new YssException("获取成立日期报错");
        }
    }

    public void getMaxNetValue() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select max(FNAVDATE) as navdate from " +
                pub.yssGetTableName("tb_data_navdata") +
                " where FretypeCode='Total' and FKEYCODE='Unit' and FNAVDATE<" +
                dbl.sqlDate(this.startDate);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.maxNetDate = rs.getDate("navdate");
				//fanghaoln 20100226 MS00974 QDV4交银施罗德2010年2月4日01_B 集成zhouss的修改
				//- zhouss 如果选择 基金成立日， 则给出提示 20090805
				if (this.maxNetDate == null)
				{
					throw new YssException("找不到距起始日期之前最近的净值数据 请重新选择日期范围");
				}
				//-------------------------end -------MS00974----------------------------------------
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getUnSettleMoney() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double money = 0.0;
        try {
            /*  strSql = " select  sum(FTotalCost) as money from " +
                    pub.yssGetTableName("tb_data_subtrade") +
                    " where FsettleState=0  and FBARGAINDATE<=" +
                    dbl.sqlDate(this.startDate);
             */
            strSql = " select FCashAccCode,FAccBalance from " +
                pub.yssGetTableName("tb_stock_cash") +
                " where FCuryCode=" + dbl.sqlString("CNY") +
                " and FStorageDate=" +
                dbl.sqlDate(maxNetDate) + " and FPORTCODE=" +
                dbl.sqlString(this.sPort) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(maxNetDate, "yyyy") +
                              YssFun.formatDate(maxNetDate, "MM")) +
                " and FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                money = rs.getDouble("FAccBalance");
            }
            return money;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public double getTaConfigMoney() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double money = 0.0;
        try {
            strSql = " select  sum(FSettleMoney) as money from " +
                pub.yssGetTableName("tb_ta_trade") +
                " where  (FConfimDate=" +
                dbl.sqlDate(YssFun.addDay(this.maxNetDate, -4)) +
                " or FConfimDate=" +
                dbl.sqlDate(YssFun.addDay(this.maxNetDate, -3)) + ")" +
                " and FConfimDate<" + dbl.sqlDate(this.startDate) +
                " and FSELLType='02'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                money = rs.getDouble("money");
            }
            dbl.closeResultSetFinal(rs);	//20140225 added by liubo.Bug #89368.最后需要关闭ResultSet
            
            strSql = " select  sum(FSettleMoney) as money from " +
                pub.yssGetTableName("tb_ta_trade") +
                " where  FConfimDate=" +
                dbl.sqlDate(YssFun.addDay(this.maxNetDate, -2)) +
                " and FConfimDate<" + dbl.sqlDate(this.startDate) +
                " and FSELLType='01'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                money = rs.getDouble("money") + money;
            }
            return money;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判断日期是不是节假日
     * by 陈嘉
     * @throws YssException
     * @return boolean
     */
    public boolean isHoliday(java.util.Date dDate) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean isHoliday = true;
        try {
            strSql =
                "select FDate from Tb_Base_ChildHoliday where FHolidaysCode = " +
                dbl.sqlString(this.holiday) + " and FDate=" + dbl.sqlDate(dDate);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                isHoliday = true;
            } else {
                isHoliday = false;
            }
            return isHoliday;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 
     * @throws YssException 
     * @方法名：getCashAccNameBycode
     * @参数：
     * @返回类型：String
     * @说明：通过现金帐户代码获取现金账户名称
     * by 张发
     */
    //MS01206    zhangfa    QDV4汇添富2010年05月26日01_AB    2010.06.04
public String getCashAccNameBycode(String cashAccCode) throws YssException{
	String cashAccName="";
	 String strSql = "";
     ResultSet rs = null;
     
     //判断现金帐户代码是否为空
     if(cashAccCode==null||cashAccCode.length()==0){
    	 cashAccName=" ";
    	 return cashAccName;
     }
     
     try{
    	 strSql="select FCashAccName from " + pub.yssGetTableName("tb_para_cashaccount") + " t where  t.FCashAccCode='"+cashAccCode+"'";
    	 System.out.println(strSql);
    	 rs = dbl.openResultSet(strSql);
    	 if (rs.next()) {
    		 //获取现金账户名称
    		 cashAccName=rs.getString("FCashAccName").trim();
    	 }else {
    		 cashAccName=" ";
    	 }
     }catch(Exception e){
    	 throw new YssException(e.getMessage());
     }finally{
    	 dbl.closeResultSetFinal(rs);	//20140225 added by liubo.Bug #89368.最后需要关闭ResultSet
     }
     
     return cashAccName;
	
}
	/**
	 * add dongqingsong 3013-05-25 story 3945
	 * 创建临时表Tmp_RepHXCas；目的保存现金头寸信息查询的结果
	 * @throws YssException 
	 */
	public  void createTmpTbale() throws YssException{
		try {
			if(!dbl.yssTableExist("Tmp_RepHXCas")){
				System.out.println("4");
				String createSql = "create table Tmp_RepHXCas( query_date date, execute_date date, count_days varchar2(20)," +
				"end_date  date,account_code  varchar2(20),yesterday_balance number(20,3)," +
				"today_in  number(20,3),today_out  number(20,3),available number(20,3),portCode varchar2(100))"; 
				dbl.executeSql(createSql);
			}
		} catch (Exception e) {
	    	 throw new YssException("创建保存现金头寸信息表失败："+e.getMessage());
		}
	}
	
	/**
	 * add dongqingsong 3013-05-25 story 3945
	 * 按照查询日期删除掉制定的数据
	 * @param queryDate 查询日期
	 * @throws YssException 
	 */
	public void deleteTmpTbale(String queryDate,String portCode) throws YssException{
		String deleteSql = "delete from Tmp_RepHXCas t where t.query_date =to_date('"+queryDate +"','yyyy-MM-dd') and " +
				"t.portCode = "+dbl.sqlString(portCode);
		try {
			dbl.executeSql(deleteSql);
		} catch (Exception e) {
	    	 throw new YssException("删除现金头寸表信息出错："+e.getMessage());
		}
	} 
	
	/**
	 * add dongqingsong 3013-05-25 story 3945
	 * 保存头寸信息表
	 * @throws Exception 
	 */		
	public void insertTmpTbale( String Querydate,String executeDate,int countDay,Date endDate,String accountCode, 
			double yesterday_balance,double today_in,double today_out,double available) throws Exception{
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd"); 

		String insertSql = "insert into Tmp_RepHXCas(query_date,execute_date,count_days,end_date,account_code," +
		"yesterday_balance,today_in,today_out,available ,portCode)values(?,?,?,?,?,?,?,?,?,?)";
		try {
			pst = dbl.openPreparedStatement(insertSql);
			
			pst.setDate(1,YssFun.toSqlDate(Querydate));
			pst.setDate(2,YssFun.toSqlDate(executeDate));
			pst.setInt(3,countDay);
			pst.setDate(4,YssFun.toSqlDate(endDate));
			pst.setString(5,accountCode);
			pst.setDouble(6,yesterday_balance );
			pst.setDouble(7, today_in);
			pst.setDouble(8, today_out);
			pst.setDouble(9, available);
			System.out.println(this.sPort);
			pst.setString(10, this.sPort);

			
			pst.execute();
			conn.commit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally{
			dbl.closeStatementFinal(pst);	//20140225 added by liubo.Bug #89368.最后需要关闭PrepareStatement
		}
		
	}
	public void savaRepHXCash(Date execute_date,String[] dataSource,Date endDate,int num){
		String Querydate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		String executeDate = new SimpleDateFormat("yyyy-MM-dd").format(execute_date);
		String countDay ="1";
		String info = dataSource[0];
		String[] ds = info.split(",");
		String d0 = ds[0];
		String d1 = ds[1];
		String accountCode = null;
		
		if(ds[2].contains(". ")){
			accountCode = ds[2].replace(".", "").trim();
		}
		String accountName = ds[3];
		double yesterday_balance =Double.parseDouble(ds[4]);
		double today_in = Double.parseDouble(ds[5]);
		double today_out = Double.parseDouble(ds[6]);
		double available = Double.parseDouble(ds[7]);
 

		try {
			this.insertTmpTbale(Querydate,executeDate,num,endDate,accountCode,yesterday_balance,today_in,today_out,available);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

