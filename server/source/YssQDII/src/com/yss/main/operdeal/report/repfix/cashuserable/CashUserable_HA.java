package com.yss.main.operdeal.report.repfix.cashuserable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.yss.dsub.BaseBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CashUserable_HA
    extends BaseBuildCommonRep {
    public CashUserable_HA() {
    }

    private CommonRepBean repBean;
    private java.util.Date startDate = null; //期初日期
    private String sPort = "", baseCury="",portCury = ""; //组合代码
    private FixPub fixPub = null;
    private String workDate; //工作日
    private String holidays = ""; //结假日代码
    private Hashtable table=new Hashtable();
    private double baseMoney=0.0;  //工行美元
    private double portMoney=0.0;  //工行人民币
    /**
     * buildReport
     *华安现金头寸预测表——杂币
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        sResult = buildResult(this.startDate, this.sPort);
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
        baseCury = pub.getPortBaseCury(this.sPort);// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
    	portCury = getPortCury(this.sPort);
    }

    public void parse(String str) throws YssException {
        String[] sReq = str.split("\n");
        try {
            this.startDate = YssFun.toDate(sReq[0].split("\r")[1]);
            this.sPort = sReq[1].split("\r")[1];
            this.holidays = sReq[2].split("\r")[1];
        } catch (Exception e) {
            throw new YssException("解析参数出错", e);
        }
    }

    public double getMoney(String curyCode)throws YssException
    {
        String strSql="",cury = "";
        double result=0.0;
        ResultSet rs=null;
        try
        {
        	if(curyCode.equals("GHbaseCury")){
        		cury = this.baseCury;
        	}else{
        		cury = this.portCury;
        	}
             strSql=" select * from (select * from "+pub.yssGetTableName("tb_stock_cash")+" where FSTORAGEDATE="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+
                       " and FCURYCODE="+dbl.sqlString(cury)+" ) a join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+
                       " where FCASHACCNAME not like '次托管行%' and fcurycode= " + dbl.sqlString(cury) +
                       " )b on b.fcashacccode=a.fcashacccode";
            rs=dbl.openResultSet(strSql);
            if(rs.next())
            {
                result=rs.getDouble("FACCBALANCE");
                //存入工行基础及组合货币的期初值
                table.put(curyCode, new Double(result));
            }else{
            	result = 0;
            	table.put(curyCode, new Double(0.00));
            }
        }catch(Exception e)
        {
        	throw new YssException(e.getMessage());
    	} finally {
    		dbl.closeResultSetFinal(rs);
    		}
        return result;
    }

    //获取组合货币
    public String getPortCury(String sPort)throws YssException
    {
        String strSql="",result="";
        ResultSet rs=null;
        try
        {
            strSql="select fportcury from "+pub.yssGetTableName("Tb_Para_Portfolio")+"  where fportcode='"+sPort+"' and FCheckState=1";
            rs=dbl.openResultSet(strSql);
            if(rs.next())
            {
            	result = rs.getString("fportcury");
            }
        }catch(Exception e){
        	throw new YssException(e.getMessage());
    	} finally {
    		dbl.closeResultSetFinal(rs);
    		}
		return result;
    }


    //获取T-1日帐户库存
    public  String[] getBeforeStorage(List arrCury)throws YssException
    {
        String[] arrResult = null;
        ResultSet rs=null;
        StringBuffer buf = new StringBuffer();
        try
        {
            buf.append("").append(",");
            buf.append("").append(",");
            buf.append("期初余额").append(",");
            for (int i = 0; i < arrCury.size(); i++) {
                    if (i == arrCury.size() - 1) {
                        buf.append("0.00").append(",");
                        //工行基础及组合货币取值
                        buf.append(this.baseMoney).append(",");
                        buf.append(this.portMoney).append("\f\f");
                        
                    } else {
                        double money=((Double)table.get((String)arrCury.get(i))).doubleValue();
                        buf.append(money).append(",");
                    }
            }
            arrResult = buf.toString().split("\f\f");
        }catch(Exception e){
        	throw new YssException(e.getMessage());
    	} finally {
    		dbl.closeResultSetFinal(rs);
    		}
		return arrResult;
    }
    
    public double getGHMoney(String curyCode,String flag)throws YssException
    {
        String strSql="",cury="";
        double result=0.0;
        double money=0.00;
        ResultSet rs=null;
        try
        {
        	if(curyCode.equals("GHbaseCury")){
        		cury = this.baseCury;
        	}else{
        		cury = this.portCury;
        	}
        	strSql = "from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" a inner join "+pub.yssGetTableName("Tb_Cash_Transfer")
        			+" b on a.fnum = b.fnum where a.FCashAccCode in (select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")
        			+" where FCASHACCNAME not like '次托管行%' and fcurycode= '"+cury+"' ) and b.ftransferdate =" + dbl.sqlDate(this.workDate);
        	if(flag.endsWith("+")){
        		strSql = "select sum(a.FMoney) FMoney "+strSql + " and FInOut=1";
        		/*strSql = "select FBCashAccCode,FBCuryCode,FBMoney fmoney from "+pub.yssGetTableName("Tb_Data_RateTrade")+" where" +
        				" FBCashAccCode in (select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" where " +
        				"FCASHACCNAME not like '次托管行%' and fcurycode= '"+cury+"') and FSettleDate = " + dbl.sqlDate(this.workDate) ;*/
        	}else{
        		strSql = "select sum(a.FMoney) FMoney "+ strSql + " and FInOut=-1";
        		/*strSql = "select FSCashAccCode,FSCuryCode,-FSMoney fmoney from "+pub.yssGetTableName("Tb_Data_RateTrade")+" where" +
						" FSCashAccCode in (select fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" where " +
						"FCASHACCNAME not like '次托管行%' and fcurycode= '"+cury+"') and FSettleDate = " + dbl.sqlDate(this.workDate) ;*/
        	}
            rs=dbl.openResultSet(strSql);
            if(rs.next()){
                    result=rs.getDouble("FMoney");
            }else{
            	result = 0;
            }

        	if(flag.endsWith("+")){
                money=((Double)table.get(curyCode)).doubleValue()+result;
        	}else{
                money=((Double)table.get(curyCode)).doubleValue()-result;
        	}
            table.put(curyCode,new Double(money));
        }catch(Exception e)
        {
        	throw new YssException(e.getMessage());
    	} finally {
    		dbl.closeResultSetFinal(rs);
    	}
        return result;
    }
    
    protected String buildResult(java.util.Date startDate,
                                 String sPort) throws
        YssException {
        String strResult = "";
        ResultSet rs = null;
        String strSql = "" ;
        String result[] = null;
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        java.util.Date titleDate = null;
        StringBuffer buf = null;
        StringBuffer finBuf = new StringBuffer();
        try {
        	        	
        	baseMoney=this.getMoney("GHbaseCury");;
        	portMoney=this.getMoney("GHportCury");
            List list = this.getTotalDate(this.holidays);
            List arrCury = new ArrayList();
            arrCury.add("baseCury");
            arrCury.add("USD");
            arrCury.add("HKD");
            arrCury.add("SGD");
            arrCury.add("MYR");
            arrCury.add("JPY");
            arrCury.add("AUD");
            arrCury.add("THB");
            arrCury.add("KRW");
            arrCury.add("IDR");
            arrCury.add("TWD");
            this.getStockBalance(arrCury);
            
            result = getBeforeStorage(arrCury);
            for (int m = 0; m < result.length; m++) {
                finBuf.append(fixPub.buildRowCompResult(result[m], "DsDays000001V1.0")).
                    append("\r\n");
            }
            
            
            for (int i = 0; i < list.size(); i++) {
            		if ( ( (String) list.get(i)).endsWith("-")) {
            			result = this.getSub(arrCury);
            		} else if ( ( (String) list.get(i)).endsWith("Balance")) {
            			result = this.getBalance(arrCury);
            		} else {
            			this.workDate = YssFun.formatDate(YssFun.toDate( (String) list.get(i)), "yyyy-MM-dd");
            			result = this.getAdd(arrCury);

            		}
            	
                for (int j = 0; j < result.length; j++) {
                    finBuf.append(fixPub.buildRowCompResult(result[j], "DsDays000001V1.0")).
                        append("\r\n");
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

    public String[] getAdd(List arrCury) throws YssException {
        String[] arrResult = null;
        StringBuffer buf = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {

            buf.append(this.workDate).append(",");
            buf.append(this.getDayOfWeek()).append(",");
            buf.append("+").append(",");
            for (int i = 0; i < arrCury.size(); i++) {
                if ( ( (String) arrCury.get(i)).equalsIgnoreCase("baseCury")) {
                	//非基础货币的清算款发生额折算为基础货币累加
                	String strSqlTmp1 = " select round(sum(b.inMoney*b.fbasecuryrate),2) as baseCury, a.FTRANSFERDATE as TransferDate from  ( " +
                    					" select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") + " where FTransferdate = " + dbl.sqlDate(this.workDate) +
                    					" and FCheckState = 1 and FTSFTYPECODE='05') a  join (select FNum, FMoney as inMoney ,FCashAccCode,fbasecuryrate from " 
                    					+ pub.yssGetTableName("tb_cash_subtransfer") +" where FInout = 1 and FCheckState = 1 and FCashAccCode in " +
                    					"(select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") +" where fcashaccname like '次托管行%' and FCURYCODE <>'"+this.baseCury+"') " +
                    					") b on b.FNum = a.FNum group by a.FTRANSFERDATE ";
                	//次托的基础货币发生额直接累加，无须折算
                	String strSqlTmp2  =" select round(sum(b.inMoney),2) as baseCury, a.FTRANSFERDATE as TransferDate from  ( " +
										" select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") + " where FTransferdate = " + dbl.sqlDate(this.workDate) +
										" and FCheckState = 1) a  join (select FNum, FMoney as inMoney ,FCashAccCode,fbasecuryrate from " 
										+ pub.yssGetTableName("tb_cash_subtransfer") +" where FInout = 1 and FCheckState = 1 and FCashAccCode in " +
										"(select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount") +" where fcashaccname like '次托管行%' and FCURYCODE='"+this.baseCury+"') " +
										") b on b.FNum = a.FNum group by a.FTRANSFERDATE ";
                    
                    strSql = "select sum(baseCury) as baseCury from ("+strSqlTmp1+" union "+strSqlTmp2+")";
                } else {
                    strSql =
                        " select sum(b.inMoney) as " + (String) arrCury.get(i) +
                        " , a.FTRANSFERDATE as TransferDate,b.FCashAccCode  from ( select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") +
                        " where FTransferdate =" + dbl.sqlDate(this.workDate) + " and FCheckState = 1 and FTSFTYPECODE not in('05','04')) a " +
                        " join (select FNum, FMoney as inMoney ,FCashAccCode from " + pub.yssGetTableName("tb_cash_subtransfer") +
                        " where FInout = 1 and FCheckState = 1) b on b.FNum = a.FNum " +
                        " join(select * from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCASHACCNAME like'次托管行%' and FCURYCODE= " + dbl.sqlString( (String) arrCury.get(i)) + " ) c " +
                        " on c.FCASHACCCODE=b.Fcashacccode group by b.FCASHACCCODE,a.ftransferdate ";
                }
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    if (i == arrCury.size() - 1) {
                        buf.append("0.00").append(",");
                        
                        //工行基础及组合货币取值
                        buf.append(getGHMoney("GHbaseCury" ,"+")).append(",");
                        buf.append(getGHMoney("GHportCury" ,"+")).append("\f\f");
                        
                    } else {
                        buf.append(rs.getDouble( (String) arrCury.get(i))).append(",");
                    }
                    double money=((Double)table.get((String)arrCury.get(i))).doubleValue();
                    money=money+rs.getDouble( (String) arrCury.get(i));
                    table.put(arrCury.get(i),new Double(money));

                }
               else {
                   buf.append("0.00").append(",");
                    if (i == arrCury.size() - 1) {
                    	//工行基础及组合货币取值
                        buf.append(getGHMoney("GHbaseCury" ,"+")).append(",");
                        buf.append(getGHMoney("GHportCury" ,"+")).append("\f\f");
                    } /*else {
                        buf.append("0.00").append(",");
                    }*/
                }
                dbl.closeResultSetFinal(rs);
            }
            arrResult = buf.toString().split("\f\f");

           
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return arrResult;
    }

    public String[] getSub(List arrCury) throws YssException {
        String[] arrResult = null;
        StringBuffer buf = new StringBuffer();
        String strSql = "";
        ResultSet rs = null;
        try {

            buf.append(" ").append(",");
            buf.append(" ").append(",");
            buf.append("-").append(",");
            for (int i = 0; i < arrCury.size(); i++) {
            	//若为基础货币列
                if ( ( (String) arrCury.get(i)).equalsIgnoreCase("baseCury")) {
                	//非基础货币的清算款发生额折算为基础货币累减
                	String strSqlTmp1 = " select round(sum(b.inMoney*b.fbasecuryrate),2) as baseCury, a.FTRANSFERDATE as TransferDate from  ( " +
                        				" select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") + " where FTransferdate = " + dbl.sqlDate(this.workDate) +
                        				" and FCheckState = 1 and FTSFTYPECODE='05') a  join (select FNum, FMoney as inMoney ,FCashAccCode,fbasecuryrate from " + pub.yssGetTableName("tb_cash_subtransfer") +
                        				" where FInout = -1 and FCheckState = 1 and FCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount")
                        				+" where fcashaccname like '次托管行%' and FCURYCODE <>'"+this.baseCury+"')) b on b.FNum = a.FNum group by a.FTRANSFERDATE ";
                	//次托的基础货币发生额直接累减，无须折算
                	String strSqlTmp2  = " select round(sum(b.inMoney),2) as baseCury, a.FTRANSFERDATE as TransferDate from  ( " +
    									" select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") + " where FTransferdate = " + dbl.sqlDate(this.workDate) +
    									" and FCheckState = 1 ) a  join (select FNum, FMoney as inMoney ,FCashAccCode,fbasecuryrate from " + pub.yssGetTableName("tb_cash_subtransfer") +
    									" where FInout = -1 and FCheckState = 1 and FCashAccCode in (select FCashAccCode from " + pub.yssGetTableName("tb_para_cashaccount")
    									+" where fcashaccname like '次托管行%' and FCURYCODE ='"+this.baseCury+"')) b on b.FNum = a.FNum group by a.FTRANSFERDATE ";
                    strSql = "select sum(baseCury) as baseCury from ("+strSqlTmp1+" union "+strSqlTmp2+")";
                } else {
                    strSql = " select sum(b.inMoney) as " + (String) arrCury.get(i) +
                        " , a.FTRANSFERDATE as TransferDate,b.FCashAccCode  from ( select Fnum,FTransferdate from " + pub.yssGetTableName("tb_cash_transfer") +
                        " where FTransferdate =" + dbl.sqlDate(this.workDate) + " and FCheckState = 1 and FTSFTYPECODE not in('05','04')) a " +
                        " join (select FNum, FMoney as inMoney ,FCashAccCode from " + pub.yssGetTableName("tb_cash_subtransfer") +
                        " where FInout = -1 and FCheckState = 1) b on b.FNum = a.FNum " +
                        " join(select * from "+pub.yssGetTableName("tb_para_cashaccount")+" where FCASHACCNAME like'次托管行%' and FCURYCODE= " + dbl.sqlString( (String) arrCury.get(i)) + " ) c " +
                        " on c.FCASHACCCODE=b.Fcashacccode group by b.FCASHACCCODE,a.ftransferdate ";
                }
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    if (i == arrCury.size() - 1) {
                        buf.append("0.00").append(",");
                        
                        //工行基础及组合货币取值
                        buf.append(getGHMoney("GHbaseCury" ,"-")).append(",");
                        buf.append(getGHMoney("GHportCury" ,"-")).append("\f\f");
                        
                    } else {
                        buf.append(rs.getDouble((String) arrCury.get(i))).append(",");
                    }
                    double money=((Double)table.get((String)arrCury.get(i))).doubleValue();
                    money=money-rs.getDouble( (String) arrCury.get(i));
                    table.put(arrCury.get(i),new Double(money));

                }else {
                	buf.append("0.00").append(",");
                    if (i == arrCury.size() - 1) {
                    	//工行基础及组合货币取值
                        buf.append(getGHMoney("GHbaseCury" ,"-")).append(",");
                        buf.append(getGHMoney("GHportCury" ,"-")).append("\f\f");
                    } 
                }
                dbl.closeResultSetFinal(rs);
            }
            
            arrResult = buf.toString().split("\f\f");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return arrResult;

    }

    public void getStockBalance(List arrCury)throws YssException
    {
        String strSql="";
        ResultSet rs=null;
        String curyCode="";
        try
   
        {
            for(int i=0;i<arrCury.size();i++)
            {
                if(((String)arrCury.get(i)).equals("baseCury")){
            		curyCode=this.baseCury;
            	}
            	else{
            		curyCode=(String)arrCury.get(i);
            	}
                strSql=" select * from (select * from "+pub.yssGetTableName("tb_stock_cash")+" where FSTORAGEDATE="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+
                       " and FCURYCODE= "+dbl.sqlString(curyCode)+" ) a join (select * from "+pub.yssGetTableName("tb_para_cashaccount")+
                       " where FCASHACCNAME like '次托管行%' and fcurycode="+dbl.sqlString(curyCode)+
                       " )b on b.fcashacccode=a.fcashacccode";
                rs=dbl.openResultSet(strSql);
                if(rs.next())
                {
                    table.put(arrCury.get(i),new Double(rs.getDouble("FACCBALANCE")));
                }else
                {
                    table.put(arrCury.get(i),new Double(0.00));
                }
                dbl.closeResultSetFinal(rs);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public String[] getBalance(List arrCury) throws YssException {
        String[] arrResult = null;
        StringBuffer buf = new StringBuffer();
        try {
            buf.append(" ").append(",");
            buf.append(" ").append(",");
            buf.append("Balance").append(",");
            for(int i=0;i<arrCury.size();i++){
                buf.append(table.get((String)arrCury.get(i))).append(",");
            }
            buf.append(YssFun.roundIt(((Double)table.get("GHbaseCury")).doubleValue(), 2)).append(",");
            buf.append(YssFun.roundIt(((Double)table.get("GHportCury")).doubleValue(),2)).append("\f\f");

            arrResult = buf.toString().split("\f\f");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } 
        return arrResult;

    }

    public String getDayOfWeek() {

        String result = "";
        try {
            int day = YssFun.getWeekDay(YssFun.toDate(this.workDate));
            if (day == 1) {
                result = "星期天";
            } else if (day == 2) {
                result = "星期一";
            } else if (day == 3) {
                result = "星期二";
            } else if (day == 4) {
                result = "星期三";
            } else if (day == 5) {
                result = "星期四";
            } else if (day == 6) {
                result = "星期五";
            } else if (day == 7) {
                result = "星期六";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List getTotalDate(String holidayCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        List list = new ArrayList();
        BaseOperDeal deal = new BaseOperDeal();
        deal.setYssPub(pub);
        java.util.Date workDate = null;
        workDate = YssFun.addDay(this.startDate, -1);
        int i = 0;
        try {
            while (i < 9) {
                workDate = deal.getWorkDay(holidayCode, YssFun.addDay(workDate, 1), 0);
                list.add(YssFun.formatDate(workDate));
                list.add("-");
                list.add("Balance");
                i++;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return list;
    }
}
