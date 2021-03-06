/**   
* @Title: ManageFeeCF322.java 
* @Package com.yss.main.operdeal.report.repfix 
* @Description: TODO( ) 
* @author KR
* @date 2013-2-5 上午11:39:58 
* @version V4.0   
*/
package com.yss.main.operdeal.report.repfix;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/** 
 * @ClassName: ManageFeeCF322 
 * @Description: TODO( add by huangqirong 2013-02-05 story #3401 853套账新增报表 ) 
 * @author huangqirong 
 * @date 2013-2-5 上午11:39:58 
 *  
 */
public class ManageFee extends BaseBuildCommonRep{
	
	protected CommonRepBean repBean;
	private String portCode = "";	//组合
	private String startDate = "";	//开始日期 
	private String endDate = "";		//结束日期
	
	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public ManageFee() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.operdeal.report.BaseBuildCommonRep#initBuildReport(com.yss.dsub.BaseBean)
	 */
	@Override
	public void initBuildReport(BaseBean bean) throws YssException {
		// TODO Auto-generated method stub		
		repBean = (CommonRepBean) bean;
		this.parseRowStr(this.repBean.getRepCtlParam());
	}
	
	public void parseRowStr(String sRowStr) throws YssException {
		 if (sRowStr.equals("")) {
             return;
         }
         String reqAry[] = null;
         reqAry = sRowStr.split("\n"); //这里是要获得参数
         this.portCode = reqAry[0].split("\r")[1];
         this.startDate = reqAry[1].split("\r")[1];
         this.endDate = reqAry[2].split("\r")[1];         
	}
	
	/* (non-Javadoc)
	 * @see com.yss.main.operdeal.report.BaseBuildCommonRep#buildReport(java.lang.String)
	 */
	@Override
	public String buildReport(String sType) throws YssException {
		StringBuffer result = new StringBuffer();
		StringBuffer tempData = new StringBuffer();
		ResultSet rs = null;
		double manageFeeTotal1 = 0 ;
		double manageFeeTotal2 = 0 ;
		double manageFeeTotal = 0 ;
		double tempValueX = 0 ; //临界金额
		
		String sql = " select dnv.*,inv2.*, ppf.Fpertype as Fpertype, " +
   			 " (case when ppi.fperiodtype = 1 then " +
   			 " ADD_MONTHS(TRUNC(FNavDate, 'YYYY'), 12) - " +
   			 " TRUNC(FNavDate, 'YYYY') else ppi.fdayofyear  end ) as fdayofyear " + 	               
   			 " from (select * from " + pub.yssGetTableName("Tb_Data_NetValue") + " where FNavDate between to_date('" + this.startDate + "','yyyy-MM-dd') " +
             " and to_date('" + this.endDate + "','yyyy-MM-dd') " +
             " and FPortCode = " + dbl.sqlString(this.portCode) + " and FType = '01' order by FnavDate asc " +
             " ) dnv left join " +
			 " (select FIVPAYCATCODE, max(FSTARTDATE) as FSTARTDATE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " +
			 " from " + pub.yssGetTableName("tb_para_investpay") + " pip where pip.FIvPayCatCode = 'IV001' " +
			 " and pip.fportcode = " + dbl.sqlString(this.portCode) + 
			 " and pip.fcheckstate = 1 " +
			 " and pip.fstartdate <= " + dbl.sqlDate(this.startDate) + 
			 " group by FIVPAYCATCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FPORTCODE, FPORTCLSCODE " +
			 " ) inv1" +
			 " on dnv.FPortCode = inv1.FPortCode " +
			 " left join " + pub.yssGetTableName("tb_para_investpay") + " inv2 " +
			 " on inv1.FIVPAYCATCODE = inv2.fivpaycatcode " + 
			 " and inv1.FSTARTDATE = inv2.fstartdate " +
			 " and inv1.FANALYSISCODE1 = inv2.FANALYSISCODE1 " +
			 " and inv1.FANALYSISCODE2 = inv2.Fanalysiscode2 " +
			 " and inv1.FANALYSISCODE3 = inv2.Fanalysiscode3 " +
			 " and inv1.FPORTCODE = inv2.FPORTCODE " +
			 " and inv1.FPORTCLSCODE = inv2.FPORTCLSCODE " +
			 " left join( select * from " + pub.yssGetTableName("Tb_Para_Period") + " where fcheckstate = 1 ) ppi " + 
			 " on inv2.FPeriodCode = ppi.FPeriodCode " +
			 " left join ( " +
			 " select * from " + pub.yssGetTableName("Tb_Para_Performula") + " where fcheckstate = 1) ppf " +
			 " on inv2.fperexpcode = ppf.fformulacode ";
		try {
			rs = dbl.openResultSet(sql);
			while(rs.next()){				
				double fixRate = rs.getDouble("FFixRate"); //固定比率				
				double navValue = rs.getDouble("FPortNetValue"); //资产净值
				
				//比率最小值
				double minValue = Double.parseDouble(this.getDatabySql("select nvl(min(FRangeMoney),0) as FRangeMoney from " + 
											pub.yssGetTableName("Tb_Para_Performula_Rela") + 
											" ppfr where ppfr.FRangeDate <= " + dbl.sqlDate(this.startDate) + 
											" and ppfr.fformulacode = " + dbl.sqlString(rs.getString("fperexpcode")) + 
											" order by FRangeMoney asc", "FRangeMoney", "0")) ;
				//比率次小值
				double secondMinValue = Double.parseDouble(this.getDatabySql("select nvl(min(FRangeMoney),0) as FRangeMoney from " + 
											pub.yssGetTableName("Tb_Para_Performula_Rela") + 
											" ppfr where ppfr.FRangeDate <= " + dbl.sqlDate(this.startDate) + 
											" and ppfr.fformulacode = " + dbl.sqlString(rs.getString("fperexpcode"))+ 
											" and FRangeMoney > " + minValue + " order by FRangeMoney asc" , "FRangeMoney", "0")) ;
				
				double tempValue = 0 ; //金额 B
				
				//管理费年费率1 C1
				double perValue1 = Double.parseDouble(this.getDatabySql( " select nvl(FPerValue,0) as FPerValue from " + 
											pub.yssGetTableName("Tb_Para_Performula_Rela") + " ppfr " +
											" where ppfr.FRangeDate <= " + dbl.sqlDate(this.startDate) + 
											" and ppfr.fformulacode = " + dbl.sqlString(rs.getString("fperexpcode"))+ 
											" and ppfr.frangemoney =  " + minValue +
											" order by FRangeMoney asc " , "FPerValue", "0"));
					
				//管理费年费率2 C2
				double perValue2 = Double.parseDouble(this.getDatabySql( " select nvl(FPerValue,0) as FPerValue from " + 
											pub.yssGetTableName("Tb_Para_Performula_Rela") + " ppfr " +
											" where ppfr.FRangeDate <= " + dbl.sqlDate(this.startDate) + 
											" and ppfr.fformulacode = " + dbl.sqlString(rs.getString("fperexpcode"))+ 
											" and ppfr.frangemoney =  " + secondMinValue +
											" order by FRangeMoney asc " , "FPerValue", "0"));	
				
					 
				int yearDays = rs.getInt("FDayOfYear"); //年天数
				
				int number = Integer.parseInt(this.getDatabySql(" select count(*) as FCount from " + pub.yssGetTableName("Tb_Para_Performula_Rela") + 
											" ppfr where ppfr.fformulacode = "  + dbl.sqlString(rs.getString("fperexpcode")) + 
											" and ppfr.frangedate <= " + dbl.sqlDate(this.startDate) , "FCount" , "0")) ;
				
				//存在固定比率
				if(fixRate > 0){
					if( tempValueX == 0 )
						tempValueX = 99999999999d ; //临界金额 X
					perValue1 = fixRate ;
					perValue2 = 0 ;
				} else if("0".equalsIgnoreCase(rs.getString("Fpertype")) || number == 1){ //绝对类型 或 只有一条数据
					if( tempValueX == 0 )
						tempValueX = 99999999999d ; //临界金额 X
					perValue2 = 0 ;
				} else{
					if( tempValueX == 0 )
						tempValueX = minValue ; //临界金额 X
				}
				
				tempValue = navValue > tempValueX ? tempValueX : navValue; //金额 B				
				
				double manageFee1 = YssD.round(YssD.div(YssD.mul(tempValue, perValue1), yearDays) , 2); //管理费1
				
				double overPart = (navValue - tempValueX) > 0 ? (navValue - tempValueX) : 0 ; //超出部分 X
				
				double manageFee2 = YssD.round(YssD.div(YssD.mul(overPart, perValue2), yearDays) , 2); //管理费2
				
				double dayManageFeeTotal = YssD.round( YssD.div(YssD.mul(tempValue, perValue1) + YssD.mul(overPart, perValue2) , yearDays) , 2 ); //当天管理费合计
				
				manageFeeTotal1 = YssD.add( manageFeeTotal1, manageFee1 ) ; //管理费1合计
				manageFeeTotal2 = YssD.add( manageFeeTotal2, manageFee2 ) ; //管理费2合计				
				manageFeeTotal = YssD.add( manageFeeTotal, dayManageFeeTotal ) ; //当天管理费 汇总
				
				tempData.append( YssFun.formatDate(rs.getDate("FNavDate"), "yyyy-MM-dd") ).append("\t"); //净值日期
				tempData.append( YssFun.formatNumber(navValue, "#,##0.####") ).append("\t"); //资产净值
				tempData.append( tempValue ).append("\t"); //金额B
				tempData.append( perValue1 ).append("\t");  //管理费率 1
				tempData.append( manageFee1 ).append("\t"); //管理费1
				tempData.append( YssFun.formatNumber(overPart, "#,##0.####")).append("\t"); //超出部分
				tempData.append( perValue2 ).append("\t"); //管理费率2
				tempData.append( manageFee2 ).append("\t"); //管理费2
				tempData.append( dayManageFeeTotal).append("\t"); //当天管理费合计
				
				result.append( this.buildRowCompResult( tempData.toString() ) ).append("\r\n");
				tempData.setLength(0); //清空动态字符串
			}
			//合计项
			result.append(
					this.buildRowCompResult(
							tempData.append("合计\t \t \t \t" + 
									manageFeeTotal1 + "\t \t \t " + manageFeeTotal2 + " \t" + manageFeeTotal).toString())).append("\r\n");
			tempData.setLength(0);
			tempData.append("日期\t资产净值\t").append("低于 " + YssFun.formatNumber(tempValueX ,"#,##0.####") + " 部分(含）\t管理费年费率1\t管理费1\t").append("超出 " + YssFun.formatNumber(tempValueX ,"#,##0.####") + " 部分\t");
			tempData.append("管理费年费率2\t管理费2\t管理费合计");
			
		} catch (Exception e) {
			throw new YssException("获取CF322-管理费报表数据出错：\n"+e.getMessage());
		}
		return tempData + "\f\f\r\r0\f\n-1,-1,0,-1\f\f" + result.toString();
	}
	
	/**
	 * 传入sql 取值
	 * field 需要取的字段
	 * defaultValue 没找到记录的默认值
	 * */
	private String getDatabySql(String sql, String field , String defaultValue) throws YssException {
		ResultSet rs = null;
		String result = "";
		PreparedStatement psp = null;
		try {
			psp = dbl.getPreparedStatement(sql);			
			rs = psp.executeQuery();
			if(rs.next()){
				if(rs.getString(field) != null)
					result = rs.getString(field);
			}else if(defaultValue != null && defaultValue.trim().length() > 0){
				result = defaultValue;
			}
			rs.close();
			psp.close();
		}catch (Exception e) {
			System.out.println(e.getMessage());
			throw new YssException("获取CF322-管理费报表数据出错：\n"+e.getMessage());			
		}finally{
			if(psp!= null){
				try {
					psp.close();
				} catch (Exception e2) {
					System.out.println(e2.getMessage());
				}
			}
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	
	
	private String buildRowCompResult(String str) throws YssException {
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";
        RepTabCellBean rtc = null;
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("DS_CF322");
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DS_CF322") +
                " and FCheckState = 1 order by FOrderIndex";
            rs = dbl.openResultSet(strSql);
            for (int i = 0; i < sArry.length; i++) {
                sKey = "DS_CF322" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append(
                    "\t");
            }
            if (buf.toString().trim().length() > 1) {
                strReturn = buf.toString().substring(0,
                    buf.toString().length() - 1);
            }
            rs.close();
            return strReturn;
        } catch (Exception e) {
            throw new YssException("获取CF322-管理费报表格式出错：\n"+e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }	
}
