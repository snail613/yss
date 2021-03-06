/**@author shashijie
*  @version 创建时间：2012-11-21 上午10:45:27 STORY 3220 债券派息的报表固定数据源
*  类说明:获取债券派息的报表
*/
package com.yss.main.operdeal.report.repfix.RepOverage;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class FixInterestSect extends BaseBuildCommonRep{
	
	private CommonRepBean repBean;//报表对象
	    
    private String FSectDate = "";//派息日期
	
    
    /**程序入口 shashijie 2012-11-21 STORY 3220 */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        
        //获取报表内容
        sResult = getInfo();
        
        return sResult;
    }
    
    /**shashijie 2012-11-21 STORY 3220 拼接每行数据 */
    private String operionStr(String A, BigDecimal B, BigDecimal C,
			Date D,Date E,Date F,BigDecimal G,BigDecimal H,Date I
    		) {
    	
		String str = "";
		
		str += A + "\t";//第一列
		str += B.setScale(0,BigDecimal.ROUND_DOWN) + "\t";//直接删除多余的小数位
		str += C.setScale(4,BigDecimal.ROUND_HALF_UP) + "\t";//直接删除多余的小数位
		str += YssFun.formatDate(D) + "\t";//发行日期
		str += YssFun.formatDate(E) + "\t";
		str += YssFun.formatDate(F) + "\t";
		str += G.setScale(0,BigDecimal.ROUND_DOWN) + "\t";
		str += H.setScale(12,BigDecimal.ROUND_HALF_UP) + "\t";//四舍五入
		str += YssFun.formatDate(I) + "\t";
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**shashijie 2012-11-21 STORY 3220 获取报表内容*/
	private String getInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		HashMap hmReturn = new HashMap();
		//节假日代码
		String holidayscode = "";
		
		try {
			//获取需要统计的科目
			String strSql = getInfoSQl(FSectDate);
			rs = dbl.openResultSet(strSql);
			//封装入集合中
			while (rs.next()) {
				//节假日代码
				holidayscode = rs.getString("Fholidayscode");
				//System.out.println(rs.getString("FSecurityCode")+"~~~~~~~~~~~~~~~~~~");//测试
				/** 这里说明一下,因为公共方法获得当前日期对应的付息期间,本次计息起息日,截止日和派息日(付息日),
				  * 利用这一个特性若当天是派息日(付息日)传入后计算出来的下一个计息起始日就等于当前日期
				  * 所以可以推出当天便是派息日,而hmReturn集合map中所计算出来的派息日(付息日)就等于此派息日(付息日)的下次派息日
				  */
				//节假日判断
				if (holidayscode!=null && !holidayscode.trim().equals("")) {
					//BondInsCfgFormula公共方法计算得出本次计息起始日,截止日,派息日(付息日)
					BondInsCfgFormula bond = new BondInsCfgFormula();
					bond.setYssPub(pub);
					bond.getNextStartDateAndEndDate(YssFun.toDate(FSectDate), rs.getDate("Finsstartdate"), 
							rs.getDate("Finsenddate"), rs.getDouble("Finsfrequency"), hmReturn, 
							holidayscode);
				}
				//派息日(付息日) == map中的本次计息起始日
				Date dDate1 = (Date)hmReturn.get("InsStartDate");
				
				//判断派息日是否是工作日并且正好与当前业务日期当等
				if (isWorkDayOrMakeDay(dDate1,YssFun.toDate(FSectDate),holidayscode,0)) {
					//下一个派息日(付息日) = 本次截止日 + 1天(工作日) == map中的派息日(付息日)
					Date endDate = (Date)hmReturn.get("InsFXDate");
					//拼接
					str += operionStr(rs.getString("FSecurityCode"), rs.getBigDecimal("FFaceValue"), 
							rs.getBigDecimal("Fissueprice"), rs.getDate("Fissuedate"), rs.getDate("Finsstartdate"), 
							rs.getDate("Finscashdate"), rs.getBigDecimal("Finsfrequency"), 
							rs.getBigDecimal("Ffacerate"), endDate);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取报表内容出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}
	
	/**shashijie 2012-12-26 STORY 判断派息日是否是工作日并且正好与当前业务日期当等:若相等返回true*/
	private boolean isWorkDayOrMakeDay(Date InsStartDate, Date dDate,String holidayscode,int intDay) {
		boolean flag = false;
		try {
			if (InsStartDate==null || dDate==null) {
				return flag;
			}
			//付息日期当天若是节假日则向后推一个工作日再判断
			BaseOperDeal deal = new BaseOperDeal();
	    	deal.setYssPub(this.pub);
	    	Date makeDate = deal.getWorkDay(holidayscode, InsStartDate, intDay);
	    	if (YssFun.dateDiff(dDate, makeDate)==0) {
	    		flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			return flag;
		} finally {

		}
		return flag;
	}

	/** shashijie 2012-11-21 STORY 3220 获得余额表SQL */
	private String getInfoSQl(String dDate) throws YssException {
		String SqlStr = 
			" Select a.Fsecuritycode, "+//证券代码
			" a.Fissuedate, "+//发行日期
			" a.Fissueprice, "+//发行价格
			" a.Finsstartdate, "+//计息起始日
			" a.Finsenddate, "+//计息截至日
			" a.Finscashdate, "+//兑付日期
			" a.Ffacevalue, "+//债券面值
			" a.Ffacerate, "+//税后票面利率
			" a.Finsfrequency ,"+//付息频率
			" c.Fholidayscode "+//节假日
			" From "+pub.yssGetTableName("Tb_Para_Fixinterest")+" a " +
			" Join " +
			//证券库存
			" (Select B1.Fsecuritycode, B1.Fstoragedate" +
			" From "+pub.yssGetTableName("Tb_Stock_Security")+" B1" +
			" Where B1.Fstoragedate = "+dbl.sqlDate(dDate)+
			//" --And B1.Fsecuritycode = 'USG2159FAA24'" +
			" ) b On a.Fsecuritycode = b.Fsecuritycode "+
			" Join (Select C1.Fsecuritycode, C1.Fholidayscode "+
			" From "+pub.yssGetTableName("Tb_Para_Security")+" C1) c On a.Fsecuritycode = c.Fsecuritycode"
			;
		return SqlStr;
	}
	 
    /**shashijie 2012-11-21 STORY 3220 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("Tmp_lbalance");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "Tmp_lbalance" + "\tDSF\t-1\t" + i;
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
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}
	
    /**初始数据方法*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FSectDate = reqAry[0].split("\r")[1];//派息日期
        
        
    }

    
	/**返回 fSectDate 的值*/
	public String getFSectDate() {
		return FSectDate;
	}

	/**传入fSectDate 设置  fSectDate 的值*/
	public void setFSectDate(String fSectDate) {
		FSectDate = fSectDate;
	}
    
}
