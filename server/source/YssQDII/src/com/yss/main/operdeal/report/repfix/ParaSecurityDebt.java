package com.yss.main.operdeal.report.repfix;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**shashijie 2012-01-31 STORY 1713 */
public class ParaSecurityDebt extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    private String FSecurityCode = "";//债券代码
    YssFinance fc = null;//通过套帐号获取组合代码
    private FixPub fixPub = null;//获取基金成立日那天的金额
    public ParaSecurityDebt() {
    }

    /**程序入口 shashijie 2012-1-31 STORY 1713*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        //获取证券代码以及名称
        sResult = getFSecurityCodeAndName();
        //获取债券内容US912828QZ64
        sResult += getFSecurityCodeInfo();
        return sResult;
    }

    /**shashijie 2012-2-1 STORY 1713 获取债券内容*/
	private String getFSecurityCodeInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
			String title = "付息周期\t起息日\t截止日\t派发日\t票面利率\t100元实际派发利息";//标题头
			//拼接格式
			str = buildRowCompResult(title)+"\r\n";
			
			String strSql = getSecurityCode2();
			rs = dbl.openResultSet(strSql);
			
			while (rs.next()) {
				str += doOperStr(rs);
			}
		} catch (Exception e) {
			throw new YssException("获取债券内容出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}

	/**shashijie 2012-2-1 STORY 获取债券内容*/
	private String doOperStr(ResultSet rs) throws YssException,SQLException{
		String str = "";
		//获取付息周期
		str = getPeriod(rs.getDate("EndDate"),rs.getDate("FInsStartDate"),rs.getDate("FInsEndDate"),
				rs.getDouble("FInsFrequency"))+"\t";
		//起息日期
		str += YssFun.formatDate(rs.getDate("StartDate"))+"\t";
		//截止日期
		str += YssFun.formatDate(rs.getDate("EndDate"))+"\t";
		//派发日期
		str += YssFun.formatDate(rs.getDate("IssueDate"))+"\t";
		//票面利率
		str += rs.getDouble("FaceRate")+"\t";
		//百元实际派发利息,应优先取“100元实际派发利息（手工维护）”；如无，则取“100元实际派发利息（自动计算）
		str += rs.getDouble("FMoneyControl")==0?rs.getDouble("FMoneyAutomatic"):rs.getDouble("FMoneyControl");
		
		str = buildRowCompResult(str)+"\r\n";
		return str;
	}

	/**shashijie 2012-2-1 STORY 1713 获取付息周期
	 * @param dTheDay Date：当前日期
     * @param dBigInsStartDate Date：总的起息日
     * @param dBigInsEndDate Date：总的截止日
     * @param iFrequency double：年付息频率*/
	private String getPeriod(Date dTheDay,
				            Date dBigInsStartDate,
				            Date dBigInsEndDate,
				            double iFrequency) {
		int iBigMonth = 0; //总的起始日到总的截止日的实际相隔月份数
        int iTermA = 0; //每个付息期间的间隔月份数
        int iTermB = 0; //实际已付息的月份数
        int iDegreeC = 0; //已计息次数
        int iFxCs = 0; //总的付息次数
        
        iBigMonth = YssFun.monthDiff(dBigInsStartDate, dBigInsEndDate);
        iFxCs = (int) (iBigMonth / (12 / iFrequency));
        //如果取模不等于0，说明从起始日到截止日间相隔不是整数个付息次数，
        //那么公式 iFrequency = iBigMonth / (12 / iFrequency)，算出来的付息次数要比实际的付息次数少一个月
        if (iBigMonth % (12 / iFrequency) != 0) {
            iFxCs += 1;
        }
        
        iTermA = YssFun.monthDiff(dBigInsStartDate,
        		YssFun.addDate(dBigInsEndDate, 1, Calendar.DAY_OF_MONTH)) //截止日加一天
        		/ iFxCs;
       
       
        //如果当前日期的 DayOfMonth 小于 计息起始日的 DayOfMonth，同时当前日期的 DayOfMonth 不是本月的最后一天，
        //那么说明从计息起始日到计息截止日的最后一个月的天数不满一个月，
        //所以使用 YssFun.monthDiff() 函数计算出来的间隔月份就要减一个月才是实际的相隔月份
        if (YssFun.getDay(dTheDay) < YssFun.getDay(dBigInsStartDate) &&
            YssFun.getDay(dTheDay) != YssFun.endOfMonth(dTheDay)) {
            iTermB = YssFun.monthDiff(dBigInsStartDate, dTheDay) - 1;
        } else {
            iTermB = YssFun.monthDiff(dBigInsStartDate, dTheDay);
        }
        iDegreeC = iTermB / iTermA;
        iDegreeC = iDegreeC < 0 ? 0 : iDegreeC;

        return iDegreeC + "";
	}

	/**shashijie 2012-1-31 STORY 1713 获取证券代码以及名称*/
	private String getFSecurityCodeAndName() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
			String SecurityCode = "";//证券代码
			String SecurityName = "";//证券名称
			
			String strSql = getSecurityCode1();
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				SecurityCode = "证券代码:\t"+rs.getString("FSecurityCode");
				
				SecurityName = "证券名称:\t"+rs.getString("FSecurityName");
			}
			//拼接格式
			str = buildRowCompResult(SecurityCode)+"\r\n";
			str += buildRowCompResult(SecurityName)+"\r\n";
		} catch (Exception e) {
			throw new YssException("获取证券代码以及名称出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}

	/**shashijie 2012-1-31 STORY 1713*/
	private  String getSecurityCode1() {
		String string = " select * from "+pub.yssGetTableName("Tb_Para_Security")+" a where a.FSecurityCode = "+
			dbl.sqlString(this.FSecurityCode);
		return string;
	}
	
	/**shashijie 2012-1-31 STORY 1713*/
	private String getSecurityCode2() {
		String string = " select a.*," +
				"b.FInsStartDate as StartDate,"+
				"b.FInsEndDate as EndDate,"+
				"b.FIssueDate as IssueDate,"+
				"b.FFaceRate as FaceRate,"+
				"b.FMoneyAutomatic,"+
				"b.FMoneyControl"+
				" from "+pub.yssGetTableName("Tb_Para_FixInterest")+" a left join "+
				pub.yssGetTableName("Tb_Para_InterestTime")+" b ON "+
				" a.FSecurityCode = b.FSecurityCode where a.FSecurityCode = "+
				dbl.sqlString(this.FSecurityCode);
		return string;
	}

	/**shashijie 2012-1-31 STORY 1713 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("浮债");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "浮债" + "\tDSF\t-1\t" + i;
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
        FSecurityCode = reqAry[0].split("\r")[1];//债券代码
        
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
    }

    public String saveReport(String sReport) {
        return "";
    }

	public String getFSecurityCode() {
		return FSecurityCode;
	}

	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}


}
