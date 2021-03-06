package com.yss.main.operdeal.report.repfix.allotinterest;

import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPub;
import com.yss.util.YssFun;
import com.yss.main.operdeal.BaseOperDeal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import com.yss.util.YssD;

public class AllotInterest
    extends BaseBuildCommonRep {
    public AllotInterest() {
    }

    private CommonRepBean repBean;
    private java.util.Date startDate = null; //期初日期
    private java.util.Date endDate = null; //期末日期
    private String sPort = ""; //组合代码
    //删除 by wuweiqi 20101109 QDV4华安基金2010年10月11日01_A 
   // private String scale = ""; //申购款比例
    private String holiday = ""; //接假日
    private FixPub fixPub = null;
    private double totalMoney = 0.0;
    private double totalInterest = 0.0;
    //----add by wuweiqi 20101109 QDV4华安基金2010年10月11日01_A -----//
    private int txtDays = 0; //计息天数
    private double selScale = 0.0; //计息利率
    private String cbxType = "";//计息方式
    private String selNet=""; //网点信息
    //--------------------------end ---------------------------------//
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
          //  this.scale = sReq[3].split("\r")[1];//修改 by  wuweiqi 20101109 QDV4华安基金2010年10月11日01_A 
            this.holiday = sReq[3].split("\r")[1];
        } catch (Exception e) {
            throw new YssException("解析参数出错", e);
        }
    }
    /**
     * add by wuweiqi 201009 QDV4华安基金2010年10月11日01_A 
     *  获取计息利率,计息天数,计息方式,网点信息
     * @param Holidays 节假日群
     * @param sPort 组合
     * @param confirDate 确定日期
     * @throws YssException
     */
    public void getSpeParaResult(String Holidays,String sPort,java.util.Date confirDate ) throws YssException {
        String sqlStr = "";
        ResultSet rsTest = null;
        String exceptionStr = null;
        String selHolidays="";
        String selPort="";
        try {  
        	sqlStr="select FHOLIDAYSNAME from Tb_Base_Holidays where FHOLIDAYSCODE= " +
        			dbl.sqlString(Holidays); 
        	rsTest = dbl.openResultSet(sqlStr);
        	 while(rsTest.next())
        	 {
        		 selHolidays=rsTest.getString("FHOLIDAYSNAME");
        	 }
        	 dbl.closeResultSetFinal(rsTest);
        	 selHolidays = Holidays + "|" + selHolidays;
        	 
        	 sqlStr="select FPORTNAME from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FPORTCODE=" +
        	 		dbl.sqlString(sPort);
        	 rsTest=dbl.openResultSet(sqlStr);
        	 while(rsTest.next())
        	 {
        		 selPort=rsTest.getString("FPORTNAME");
        	 }
        	 dbl.closeResultSetFinal(rsTest);
        	 selPort=sPort + "|" + selPort;
            sqlStr=
            	"select a.FPARAID, a.FCTLVALUE as selHolidays,b.FCTLVALUE as selPort, c.FCTLVALUE as dtpBegin,d.FCTLVALUE as cbxType,e.FCTLVALUE as txtDays,"+
                   "f.FCTLVALUE as txtValue,"+
                   "f.FCTLVALUE/g.FCTLVALUE as scale,"+
                   "g.FCTLVALUE as txtYearDays,h.FCTLVALUE as selNet from (select FPARAID, FCTLCODE, FCTLVALUE from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'selHolidays') a "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'selPort') b on a.FPARAID = b.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'dtpBegin') c on a.FPARAID = c.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'cbxType') d on a.FPARAID = d.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'txtDays') e on a.FPARAID = e.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'txtValue') f on a.FPARAID = f.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'txtYearDays') g on a.FPARAID = g.FPARAID "+
			       "left join (select FCTLVALUE, FPARAID from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'selNet') h on a.FPARAID = h.FPARAID "+
			       "where a.FCTLVALUE ="+  dbl.sqlString(selHolidays) +"and b.FCTLVALUE = "+ dbl.sqlString(selPort) +" and c.FCTLVALUE =(select max(fctlvalue)"+
			       "from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where FPubParaCode = 'TASellInterest' and FParaId <> 0 and fctlcode = 'dtpBegin' and to_date(fctlvalue, 'yyyy_MM_dd') <= " +
			       dbl.sqlDate(confirDate) +
			       ")";
            rsTest = dbl.openResultSet(sqlStr);
            if (rsTest.next()) {    
                txtDays=Integer.parseInt(rsTest.getString("txtDays"));
            	selScale=YssFun.roundIt(Double.parseDouble(rsTest.getString("scale")), 15);
            	cbxType=rsTest.getString("cbxType");
            	selNet=rsTest.getString("selNet");
            	exceptionStr = "获取通用参数出错！";
            }else{
            	exceptionStr = "TA申购款计息通用参数中必须包含相应的启用日期小于等于业务起始日期的设置！";
            	throw new YssException("TA申购款计息通用参数中必须包含相应的启用日期小于等于业务起始日期的设置！");
            }
        } catch (Exception e) {
        	//edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
        	if(exceptionStr != null){
        		throw new YssException(exceptionStr, e);
        	}
        } finally {      
            dbl.closeResultSetFinal(rsTest);
        }
    }

    
   
    protected String buildResult(java.util.Date startDate,
                                 java.util.Date endDate, String sPort) throws
        YssException {
        ResultSet rs = null;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        java.util.Date confirmDate = null; //TA确认日期
        java.util.Date confirmDate_cw = null; //财务确认日期
        java.util.Date settleDate = null; //财务结算日期
        StringBuffer buf = null;
        StringBuffer finBuf = new StringBuffer();
        int days = 0;
        double sellMoney = 0.0;
        int days_c = 0; //确认日与结算日之间的间隔
        java.util.Date confirmDate_temp = null;
        String selNet1="";//add by wuweiqi 20101109 QDV4华安基金2010年10月11日01_A 
      
        try {
            days = YssFun.dateDiff(this.startDate, this.endDate);
            if (days == 0) {
                days = 1;
            }
            for (int i = 0; i < days; i++) {
                confirmDate = YssFun.addDay(this.startDate, i); 
                getSpeParaResult(this.holiday,this.sPort,confirmDate);  // add by wuweiqi 20101108 实例化 获得计息利率，计息天数，计息方式，网点
                
                if(cbxType.equals("0,0"))
                {
                	  //startDate=YssFun.addDay(this.startDate, -1);//this.startDate 日期减一
                	  //confirmDate = YssFun.addDay(startDate, i); 
                	if(settleDate != null && settleDate.getDate() == getConfirmData_cwByConfirmDay(confirmDate,selNet1).getDate()){
                 	   continue;
                    }else{
	                	getSpeParaResult(this.holiday,this.sPort,confirmDate);
						selNet1 = selNet.substring(0, selNet.indexOf("|"));// 拆分网点信息字符串
																			// 获取网点代码
						settleDate = getConfirmData_cwByConfirmDay(confirmDate,
								selNet1);// 将网点代码传入
						if (settleDate != null) {
							days_c = YssFun.dateDiff(confirmDate, settleDate);
							for (int j = days_c; j > 0; j--) {
								confirmDate_temp = YssFun.addDay(settleDate, j - days_c -1);
								getSpeParaResult(this.holiday,this.sPort,confirmDate_temp);
								buf = new StringBuffer();
								buf.append(YssFun.formatDate(confirmDate_temp))
										.append(",");
								buf.append("直销帐户").append(",");
								sellMoney = getAllotMoney(settleDate, selNet1,
										"FSETTLEDATE");
								if (sellMoney != 0) {
									buf.append(sellMoney).append(",");
									//modify by nimengjing 2010.12.30 BUG #777 TA申购款计息报表：每日利息金额缩小了1000倍 
									buf.append(YssFun.formatNumber(selScale,"###0.###############")).append(",");// 修改 by wuweiqi 20101111
									buf.append(YssFun.formatNumber(sellMoney * selScale,"###0.####"));// 修改 by wuweiqi 20101111
									finBuf.append(fixPub.buildRowCompResult(buf.toString(), "DSAllotLX")).append("\r\n");
								}
							}
							// i = i + days_c - 1;
						} else {
							buf = new StringBuffer();
							buf.append(YssFun.formatDate(confirmDate)).append(",");
							buf.append("直销帐户").append(",");
							buf.append("0.00").append(",");
							buf.append(YssFun.formatNumber(selScale,"###0.###############")).append(",");
							buf.append("0.00");
							finBuf.append(fixPub.buildRowCompResult(buf.toString(),"DSAllotLX")).append("\r\n");
							//-----------------------------end bug#777------------------------------------
						}
                    }
                }
                else
                {
                   selNet1 = selNet.substring(0, selNet.indexOf("|"));//拆分字符串     获取网点代码      
                   confirmDate = YssFun.addDay(this.startDate, i);//this.startDate 日期加1
                   if(settleDate != null && settleDate.getDate() == getConfirmData_cwByConfirmDay(confirmDate,selNet1).getDate()){
                	   continue;
                   }else{
                	   settleDate = getConfirmData_cwByConfirmDay(confirmDate,selNet1);
                	   if(getConfirmData(confirmDate,selNet1) != null && (confirmDate.getDate() == getConfirmData(confirmDate,selNet1).getDate() || confirmDate.getDay() == 5)){
                		   confirmDate = YssFun.addDay(confirmDate, 1);
                	   }
                	   if(this.txtDays == 1 && confirmDate.getDay() != 0 && confirmDate.getDay() != 6){
                		   confirmDate = settleDate;
                	   }
                	   if (settleDate != null) {
	   	                    days_c = YssFun.dateDiff(confirmDate, settleDate);
	   	                    for (int j = days_c; j >= 0; j--) {
	   	                        confirmDate_temp = YssFun.addDay(confirmDate, j);
	   	                        getSpeParaResult(this.holiday,this.sPort,confirmDate_temp);
	   	                        buf = new StringBuffer();
	   	                        buf.append(YssFun.formatDate(confirmDate_temp)).append(",");
	   	                        buf.append("直销帐户").append(",");
	   	                        sellMoney = getAllotMoney(settleDate,selNet1,"FSETTLEDATE");
	   	                        if(sellMoney != 0){
	   	                        	buf.append(sellMoney).append(",");
	   	                       //modify by nimengjing 2010.12.30 BUG #777 TA申购款计息报表：每日利息金额缩小了1000倍 
	   		                        buf.append(YssFun.formatNumber(selScale,"###0.###############")).append(",");//修改 by wuweiqi 20101111  
	   		                        buf.append(YssFun.formatNumber(sellMoney * selScale,"###0.####"));//修改 by wuweiqi 20101111                    
	   		                        finBuf.append(fixPub.buildRowCompResult(buf.toString(), "DSAllotLX")).append("\r\n");
	   	                        }
	   	                    }
   	                    //i = i + days_c - 1;
	   	                } else {
	   	                    buf = new StringBuffer();
	   	                    buf.append(YssFun.formatDate(confirmDate)).append(",");
	   	                    buf.append("直销帐户").append(",");
	   	                    buf.append("0.00").append(",");
	   	                    buf.append(YssFun.formatNumber(selScale,"###0.###############")).append(",");
	   	                    buf.append("0.00");
	   	                    finBuf.append(fixPub.buildRowCompResult(buf.toString(), "DSAllotLX")).append("\r\n");
	   	               //-----------------------------end bug#777------------------------------------
	   	                }
                   } 
              }
            }
            buf = new StringBuffer();
            buf.append("合计：").append(",");
            buf.append("").append(",");
            buf.append(YssFun.roundIt(this.totalMoney, 2)).append(",");
            buf.append("").append(",");
            buf.append(YssFun.roundIt(this.totalInterest, 2));
            finBuf.append(fixPub.buildRowCompResult(buf.toString(), "DSAllotLX")).append("\r\n");

            return finBuf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判段是否这个确认日期有确认数据
     * @param confirmDate Date
     * @throws YssException
     * @return boolean
     */
    public java.util.Date getConfirmData_cwByConfirmDay(java.util.Date confirmDate,String selNet1) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        java.util.Date settleDate = null; //财务的确认日期
        try {
            settleDate = this.getSettingOper().getWorkDay(this.holiday, confirmDate,txtDays);//添加计息天数
            strSql = "select FSellMoney,FSettleDate from tb_ta_trade1 where FSettleDate=" + dbl.sqlDate(settleDate) + "and Fselltype='01'"+                
            		" and FSellNetCode=" +
            		dbl.sqlString(selNet1);//销售网点代码
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                settleDate = rs.getDate("FSettleDate");
            }
            return settleDate;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**
     * 判段是否这个确认日期有确认数据
     * @param confirmDate Date
     * @throws YssException
     * @return boolean
     */
    public java.util.Date getConfirmData(java.util.Date confirmDate,String selNet1) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        java.util.Date settleDate = null; //财务的确认日期
        java.util.Date confimDate = null;
        try {
            settleDate = this.getSettingOper().getWorkDay(this.holiday, confirmDate,txtDays);//添加计息天数
            strSql = "select FSellMoney,FCONFIMDATE from tb_ta_trade1 where FSettleDate=" + dbl.sqlDate(settleDate) + "and Fselltype='01'"+                
            		" and FSellNetCode=" +
            		dbl.sqlString(selNet1);//销售网点代码
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	confimDate = rs.getDate("FCONFIMDATE");
            }
            return confimDate;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    //edit by yanghaiming 20101113 动态传递根据确认日期或者结算日期取数
    public double getAllotMoney(java.util.Date settleDate,String selNet1,String dateType) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        java.util.Date beginDate = null;
        int days = 0;
        double sellMoney = 0.0;
        try {

            strSql = "select FSellMoney from tb_ta_trade1 where " + dateType + " = " + dbl.sqlDate(settleDate) + "and Fselltype='01' and FSellNetCode=" +
            dbl.sqlString(selNet1);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sellMoney = rs.getDouble("FSellMoney");
            }
            totalMoney = com.yss.util.YssD.add(totalMoney, sellMoney);  
            totalInterest = com.yss.util.YssD.add(totalInterest, sellMoney * selScale);  //修改 by wuweiqi 20101109 QDV4华安基金2010年10月11日01_A 
            return sellMoney;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
