package com.yss.main.operdeal.opermanage;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import com.yss.dsub.*;
import com.yss.main.cusreport.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.netvalueviewpl.*;
import com.yss.main.report.*;
import com.yss.util.*;

/**
 * add by zhangjun 2012-03-07 
 * STORY #1966 资产减值损失情况表
 * @author Administrator *
 */
public class OperAssetDevalue extends BaseBuildCommonRep {

	public OperAssetDevalue (){
		
	}
	private CommonRepBean repBean;//报表对象
	private String dStartDate = "";//起始日期
	private String dEndDate = "";//起始日期
	private String portCode = ""; //组合代码
	private String portArray[] = new String[2];//组合
	private String curyCode = "";//币种代码

	//private FixPub fixPub = null;//获取基金成立日那天的金额
	
	
	/**
	 * 初始化
	 */
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        dStartDate = reqAry[0].split("\r")[1];//起始日期
        dEndDate = reqAry[1].split("\r")[1];//截止日期
        portCode = reqAry[2].split("\r")[1];//组合
        curyCode = reqAry[3].split("\r")[1];//币种代码
        
        portArray = portCode.split(",");//获取组合代码
        
        //fixPub = new FixPub();
        //fixPub.setYssPub(pub);
    }
    
    /**
     * 资产减值损失情况表处理  入口
     */
	public String buildReport(String sType) throws YssException {
		String sResult = "";
		try {
			//*************************
			Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(dStartDate);
			Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(dEndDate);
			
			if(doCheck(sdate,edate)){
				//获取报表头部
		        //sResult += getHead();
		        //获取报表内容
		        sResult += dealSalesTax();
		        
		        //获取报表尾部
		        sResult += getTail();
		        if (portArray.length > 1) {
		        	sResult += "\r\n";
				}
			}else{
				throw new YssException("查询日期设置异常!");
			}
		}catch(Exception e){			
			throw new YssException("生成资产减值损失情况表出错!", e);
		}
		//return "";
		return sResult;
	}
	
	/**
	 * 检查
	 * 
	 */
	private boolean doCheck(Date sDate, Date eDate)
			throws YssException {
		
		String sql = "";
		ResultSet rs = null;
		ResultSet rSet = null;
		
		int month = YssFun.getMonth(eDate);
		int day = YssFun.getDay(sDate);
		int year = YssFun.getYear(eDate);
		//如果起始日期不是月的第一天
		if (day != 1) {
			throw new YssException("请设置完整的会计期间。 ");
		}
		//如果截止日期不是月的最后一天
		day = YssFun.getMonthLastDay(YssFun.getYear(eDate), YssFun.getMonth(eDate));
		if (day != YssFun.getDay(eDate)) {
			throw new YssException("请设置完整的会计期间。 ");
		}
		//是否跨年
		if (YssFun.getYear(sDate) != YssFun.getYear(eDate)) {
			throw new YssException("报表查询不支持跨年操作。");
		}
		//判断是否超过当前会计年份
		for (int i = 0; i < portArray.length; i++) {
			
			String groupcode=portArray[i].split("-")[0];  //组合群
        	String portcode=portArray[i].split("-")[1];   //组合    
			try {
				sql = "select Max(FYear) as FYear from lsetlist l join " + "Tb_" + groupcode + "_Para_Portfolio "+ 
        		" t on l.fsetid = t.fassetcode  where t.fportcode = " + dbl.sqlString(portcode);
				//sql = " select Max(FYear) as FYear from LSetList " ;
				rSet = dbl.openResultSet(sql);
				if(rSet.next()){
					if (year > rSet.getInt("FYear") ) {
						throw new YssException("查询日期超出当前会计期间。");
						//throw new YssException("所选择的组合【" + groupcode + "-"+portcode+ "】超出当前会计期间年份。");
					}
				}
			} catch (Exception e) {
				throw new YssException(e);
			} finally {
				dbl.closeResultSetFinal(rSet);
			}
			
		}
		
		
		//是否超出余额表
		for (int i = 0; i < portArray.length; i++) {
			try {
				String groupcode=portArray[i].split("-")[0];  //组合群
	        	String portcode=portArray[i].split("-")[1];   //组合    
	        	
				sql = " Select Max(FMonth) As FMonth From "+this.getTablePrefix(getYear(eDate), getSetCode(portArray[i]))+ "LBalance  ";
				rs = dbl.openResultSet(sql);
				if (rs.next()) {
					if (month > rs.getInt("FMonth")) {
						//throw new YssException("所选择的组合【" + groupcode + "-"+portcode+ "】超出当前会计期间月份。");
						throw new YssException("查询日期超过当前会计期间。");
					}
				}
			} catch (Exception e) {
				throw new YssException(e);
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		
		return true;
	}
	
	/**
	 * add by zhangjun
	 * 2012-2-29 STORY 1966 获取报表尾部
	 */
	private String getTail() throws YssException {
		String str = "";
		try {
			String str1 = "主管会计工作的负责人:";
			String str2 = "会计机构负责人:";
			String str3 = "会计主管:";
			String str4 = "制表人:";
			
			//拼接格式
			str += buildRowCompResult(str1+"\t \t "+str2)+"\r\n";
			str += buildRowCompResult(str3+"\t \t "+str4)+"\r\n";
		} catch (Exception e) {
			throw new YssException("获取报表尾部出错! \n",e);
		} 
		return str;
	}
	
	/**
	 * add by zhangjun
	 * 2012-2-29 STORY 1966 获取报表头部
	 */
	private String getHead() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
			String endDate = YssFun.formatDate(
					YssFun.parseDate(this.dEndDate)
					,"yyyy年MM月");//选择日期
			String souvenir = "表号:";
			String title = "编制单位：本公司汇总";
			String CuryCodes = "金额单位:"+this.curyCode;//币种
			
			//拼接格式
			str += buildRowCompResult(endDate+"\t \t \t "+souvenir)+"\r\n";
			str += buildRowCompResult(title+"\t \t \t "+CuryCodes)+"\r\n";
		} catch (Exception e) {
			throw new YssException("获取报表头部出错~! \n",e);
		} finally {
			//dbl.closeResultSetFinal(rs);
		}
		return str;
	}
	
	/**
	 *  把内容拼接上格式
	 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("AssetDevalue");  
            for (int i = 0; i < sArry.length; i++) {
                sKey = "AssetDevalue" + "\tDSF\t-1\t" + i;  //数据源代码
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
            	strReturn = YssFun.getSubString(buf.toString());
            }
            
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } 
	}
	
	
	/**
	 * 取数据
	 * @throws YssException
	 */
	private  String dealSalesTax() throws YssException{
		
		String strSql = "";
		String sql = "";
        ResultSet rs = null; 
        ResultSet rSet = null; 
		String year = "";
		String month = "";
		String str1 = "";
		
		BigDecimal dDevalue4 = new BigDecimal(0);  //670102	持有至到期投资
		BigDecimal dDevalue5 = new BigDecimal(0);  //670101	可供出售金融资产
		BigDecimal dDevalueEQ6 = new BigDecimal(0); //67010103	股票投资
		
		BigDecimal dDevalueFI8 = new BigDecimal(0);  //67010102	债券投资
		BigDecimal dRestTax = new BigDecimal(0);  //其他资产
		
		BigDecimal dTotal = new BigDecimal(0);
		
		String str = "";
		double endBal = 0.0;
		
		try{
			Date sdate = new SimpleDateFormat("yyyy-MM-dd").parse(dStartDate);
			Date edate = new SimpleDateFormat("yyyy-MM-dd").parse(dEndDate);
			
			for (int i = 0; i<portArray.length; i++)
			{
				if(portArray[i].trim().length() > 0 ){
					//-----------test-------------------------
					str1 = " select facctcode,sum(FBDebit) as FBDebit from  " +
					       this.getTablePrefix(getYear(sdate), getSetCode(portArray[i]))+ "LBalance" +
					       " where facctcode  in ('670102','670101','67010103','67010102')  " +
					       " and Fmonth >= "+ getMonth(sdate)+ "and Fmonth <="+ getMonth(edate)+
					       " group by facctcode " ;
					rs = dbl.openResultSet(str1);
					//---------------test----------------------------
					/*
					strSql= " select facctcode,FcurCode,FBEndBal from " + 
					        this.getTablePrefix(getYear(edate), getSetCode(portArray[i]))+ "LBalance  " +//本位币期末余额	综合本位币        
					        " where facctcode  in ('670102','670101','67010103','67010102') and Fmonth=" + getMonth(edate); 
					rs = dbl.openResultSet(strSql);
					*/
					sql= " select FValDate, FCuryCode,FBaseRate,FPortRate from " + pub.yssGetTableName("Tb_Data_ValRate") + 
						 " where FValDate =  (select Max(FValDate) from " +pub.yssGetTableName("Tb_Data_ValRate") + 
						 " where FValDate <= "  + dbl.sqlDate(edate) +" and FCuryCode = '"+this.curyCode+"')" +
						 " and FCuryCode = '" +this.curyCode+ "'" ;
				    rSet = dbl.openResultSet(sql);
					if (rSet.next()){
						while (rs.next() ){
							endBal = rs.getDouble("FBDebit") * rSet.getDouble("FPortRate")/rSet.getDouble("FBaseRate");
							if(rs.getString("facctcode").equals("670102")){ //670102	持有至到期投资
								
								dDevalue4 = dDevalue4.add(BigDecimal.valueOf(endBal));
								//dTotal = dTotal.add(dDevalue4);
								
							}else if (rs.getString("facctcode").equals("670101")){ ///670101	可供出售金融资产
								
								dDevalue5 = dDevalue5.add(BigDecimal.valueOf(endBal));
								//dTotal = dTotal.add(dDevalue5);
								
							}else if (rs.getString("facctcode").equals("67010103")){ //67010103	股票投资
								
								dDevalueEQ6 = dDevalueEQ6.add(BigDecimal.valueOf(endBal));
								//dTotal = dTotal.add(dDevalueEQ6);
								
							}else if (rs.getString("facctcode").equals("67010102")){  //67010102	债券投资
								
								dDevalueFI8 = dDevalueFI8.add(BigDecimal.valueOf(endBal));
								//dTotal = dTotal.add(dDevalueFI8);
								
							}else {
								dRestTax = dRestTax.add(BigDecimal.valueOf(endBal));//其他资产
								//dTotal = dTotal.add(dRestTax);
							}
						}
					}else{ 
						throw new YssException("估值汇率表没有币种【"+this.curyCode +"】的汇率资料！"+ "\r\n");
					}
						
					dbl.closeResultSetFinal(rs);
					dbl.closeResultSetFinal(rSet);
					
				}
			}
			
			dTotal = dTotal.add(dDevalue4).add(dDevalue5).add(dDevalueEQ6).add(dDevalueFI8).add(dRestTax) ;
			str += operionStr("  应收款项","1",new BigDecimal(0.00));
			str += operionStr("  低值易耗品","2",new BigDecimal(0.00));
			str += operionStr("  长期股权投资","3",new BigDecimal(0.00));
			str += operionStr("  持有至到期投资","4",dDevalue4);
			str += operionStr("  可供出售金融资产","5",dDevalue5);
			str += operionStr("  -股票","6",dDevalueEQ6);
			
			str += operionStr(" -基金","7",new BigDecimal(0.00));
			str += operionStr(" -债券","8",dDevalueFI8);
			str += operionStr(" -其他","9",new BigDecimal(0.00));
			str += operionStr("  贷款","10",new BigDecimal(0.00));
			str += operionStr("  投资性房地产","11",new BigDecimal(0.00));
			str += operionStr("  长期应收款","12",new BigDecimal(0.00));
			
			str += operionStr("  固定资产","13",new BigDecimal(0.00));
			str += operionStr("  在建工程","14",new BigDecimal(0.00));
			str += operionStr("  无形资产","15",new BigDecimal(0.00));
			str += operionStr("  商誉","16",new BigDecimal(0.00));
			str += operionStr("  抵债资产","17",new BigDecimal(0.00));
			str += operionStr("  其他资产","18",new BigDecimal(0.00));
			
			str += operionStr("  合计","19",dTotal);
			
		}catch(Exception e){
			//str = "";
			throw new YssException("获取营业税金及附加明细表数据时出现异常!", e);
		}finally{
			
		}
		return str;
	}
	
	/**
	 * 拼接数据
	 */
	private String operionStr(String row1, String row2, BigDecimal value) {
		
		String str = "";
		
		str += row1 + "\t";//第一列
		
		str += row2 + "\t";//第二列
		
		str += value.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";//合计金额
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**
     * 
     * 获取套账号
     */
    private int getSetCode( String pCode ) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        int portInfo = 0;
        //String portArrayList[] = new String[2];//组合
        
        try {
        	String groupcode=pCode.split("-")[0];  //组合群
        	String portcode=pCode.split("-")[1];   //组合    
        	//FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser
            sqlStr = "select distinct l.fsetcode from lsetlist l join " + "Tb_" + groupcode + "_Para_Portfolio "+ 
            		" t on l.fsetid = t.fassetcode  where t.fportcode = " + dbl.sqlString(portcode);
            rs = dbl.queryByPreparedStatement(sqlStr); 
            if (rs.next()) {
            	if(rs.getString("fsetcode") != null) {
            		portInfo =Integer.parseInt( rs.getString("fsetcode"));
            	}else{
            		portInfo = -1;
            	}
            	
            }
            return portInfo;
        } catch (Exception e) {
            throw new YssException("获取套账号出错！"+ "\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 获取财务表的年份
     */
    private int getYear( java.util.Date dDate ) throws YssException {
        
        int lYear = -1;
        try {
        	//***************
    		SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd");
            formate = new SimpleDateFormat("yyyy");
            lYear = Integer.parseInt(formate.format(dDate));            
    		//*****************
        } catch (Exception e) {
            throw new YssException("获取业务处理日期的年份出错！"+ "\r\n" + e.getMessage(), e);
        } 
        return lYear;
    }
    /**
     * 获取业务处理的月份
     */
    private int getMonth( java.util.Date dDate ) throws YssException {
        
        int lMonth = 0;
        try {
        	//***************
    		SimpleDateFormat formate= new SimpleDateFormat("yyyy-MM-dd");
            formate = new SimpleDateFormat("MM");
            lMonth = Integer.parseInt(formate.format(dDate));            
    		//*****************
        } catch (Exception e) {
            throw new YssException("获取业务处理日期的月份出错！"+ "\r\n" + e.getMessage(), e);
        } 
        return lMonth;
    }
    
    /**
     * 处理财务系统表前缀
     */
    protected String getTablePrefix(int lYear, int lnSet) {
		String stmp;
		if ((lYear > 999) && (lnSet != 0)) { //年份四位
			stmp = "A"  + lYear + new DecimalFormat("000").format(lnSet);
			return (stmp.length() == 1) ? "" : stmp;
		}
		return "";
	}
	
	
	/**
	 * 获取某月的最后一天
	 * @param date
	 * @return
	 */
	 private Date lastDayOfMonth(Date date)
	 {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.roll(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
	 }
	 /**
	  * 获取某月第一天
	  * @param date
	  * @return
	  */
	 private Date startDayOfMonth(Date date)
	 {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);   
        return cal.getTime();
	 }

	public String getdStartDate() {
		return dStartDate;
	}

	public void setdStartDate(String dStartDate) {
		this.dStartDate = dStartDate;
	}

	public String getdEndDate() {
		return dEndDate;
	}

	public void setdEndDate(String dEndDate) {
		this.dEndDate = dEndDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getCuryCode() {
		return curyCode;
	}

	public void setCuryCode(String curyCode) {
		this.curyCode = curyCode;
	}

	public String[] getPortArray() {
		return portArray;
	}

	public void setPortArray(String[] portArray) {
		this.portArray = portArray;
	}	
	
}
