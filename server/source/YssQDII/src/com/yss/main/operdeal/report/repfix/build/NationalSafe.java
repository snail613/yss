package com.yss.main.operdeal.report.repfix.build;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.operdata.BonusShareBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.parasetting.SecurityBean;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2013-05-09 STORY 3926 需求上海-(国泰基金)QDIIV4.0(紧急)20130507001*/
public class NationalSafe extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String selectDate = "";//起始日期
    private String FPortCode = "";//组合代码(多选)
    private String isNoDate = "";//是否显示没有替代申购的日期
    private String isComment = "";//是否调整报表日下一工作日权益
    
    public NationalSafe() {
    }

    /**程序入口 shashijie 2013-05-09 STORY 3926 需求上海-(国泰基金)QDIIV4.0(紧急)20130507001 */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
    	
        //获取要处理的列数据(日期倒推)
        Date date = getDateList();
		//获取要处理的行数据(股票蓝数据)
        ArrayList stockList = getStockList(date);
        //获取显示数据内容
        sResult += getInfo(stockList,date);
        
        return sResult;
    }

	/**shashijie 2013-5-9 STORY 3926 根据ETF参数倒推出补票开始日期
	 *@return Date 申购日期
	 */
	private Date getDateList() throws YssException {
		Date date = null;
		ResultSet rs = null;//定义游标
		try {
			//ETF参数对象
			ETFParamSetAdmin admin = new ETFParamSetAdmin();
			admin.setYssPub(pub);
			
			String query = getETFParam(this.FPortCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//公共方法获取补票工作日考虑进内外
				String sDate = admin.getWorkDay(YssFun.toDate(this.selectDate), 
						rs.getString("Fholidayscode"), rs.getInt("Flastestdealdaynum") * -1, 
						rs.getString("Fholidayscode2"), rs.getInt("Flastestdealdaynum2") * -1);
				date = YssFun.toDate(sDate);
			}
		} catch (Exception e) {
			throw new YssException("根据ETF参数倒推出补票开始日期出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return date;
	}

	/**shashijie 2013-5-9 STORY 3926 获取ETF参数SQL */
	private String getETFParam(String fPortCode2) {
		String sql = " Select b.Fholidayscode," +
			" c.Fholidayscode As Fholidayscode2," +
			" a.Flastestdealdaynum," +
			" a.Flastestdealdaynum2" +
			" From "+pub.yssGetTableName("Tb_Etf_Param")+" a" +
			" Left Join (Select B1.Fportcode, B1.Fholidayscode" +
			" From "+pub.yssGetTableName("Tb_Etf_Paramhoildays")+" B1" +
			" Where B1.Fcheckstate = 1" +
			" And B1.Fovertype = 'lastestdealdaynum') b On a.Fportcode = b.Fportcode" +
			" Left Join (Select B1.Fportcode, B1.Fholidayscode" +
			" From "+pub.yssGetTableName("Tb_Etf_Paramhoildays")+" B1" +
			" Where B1.Fcheckstate = 1" +
			" And B1.Fovertype = 'lastestdealdaynum2') c On a.Fportcode = c.Fportcode" +
			" Where a.Fcheckstate = 1 And a.fportcode In ("+operSql.sqlCodes(fPortCode2)+") ";
		return sql;
	}

	/**shashijie 2013-05-09 STORY 3926 获取报表内容*/
	private String getInfo(ArrayList list,Date date) throws YssException {
		String str = "";
		if (list == null || list.isEmpty()) {
			return str;
		}
		try {
			//获取TA结算链接设置节假日
			String FHolidaysCode = getTALinkFHolidaysCode(this.FPortCode);
			//获取开始日期到结算日期之间的自然天数
			int number = YssFun.dateDiff(date,YssFun.toDate(this.selectDate));
			//循环行数(股票蓝)
			for (int i = 0; i < list.size(); i++) {
				if (i==0) {//拼接第一行标题
					String title = getTitle(number,date,FHolidaysCode,this.FPortCode);
					str += buildRowCompResult(title,"ETFYingBuGuPiao")+"\r\n";
				}
				String FSecurityCode = (String)list.get(i);//证券代码
				
				String strSture = getStrstureTitle(FSecurityCode);//每行数据
				//根据证券基本信息中的节假日对应下一个工作日获取送股权益对象
				BonusShareBean share = getBonusShareBean(FSecurityCode,YssFun.toDate(this.selectDate));
				//循环每一列数据
				for (int j = 0; j < number; j++) {
					//当前处理日期
					Date operDate = YssFun.addDay(date, j);
					//判断是否需要拼接该列数据
					if (isRowDataSpeling(operDate,FHolidaysCode,this.FPortCode)) {
						//拼接每个单元格数据
						strSture += getStrSture(FSecurityCode,operDate,this.FPortCode,YssFun.toDate(this.selectDate),
								share);
					}
				}
				//带格式拼接
				str += buildRowCompResult(strSture,"ETFYingBuGuPiao")+"\r\n";
			}
		} catch (Exception e) {
			throw new YssException("获取报表内容出错!",e);
		} finally {
			
		}
		return str;
	}

	/**shashijie 2013-5-10 STORY 3926 获取送股权益对象
	 * @param fSecurityCode
	 * @return BonusShareBean
	 */
	private BonusShareBean getBonusShareBean(String fSecurityCode,Date toDate) throws YssException {
		//送股对象
		BonusShareBean share = new BonusShareBean();
		share.setYssPub(pub);
		
		//获取证券基本信息对象
		SecurityBean secBean = getSecurityBean(fSecurityCode);
		//获取选择日期的后一个工作日
		Date dDate = getWorkDayByWhere(secBean.getStrHolidaysCode(), toDate, 1);
		
		ResultSet rs = null;//定义游标
		try {
			String query = getBonusShareBeanQuery(fSecurityCode,dDate);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//对象赋值
				share.setResultSetAttr(rs);
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return share;
	}

	/**shashijie 2013-5-10 STORY 获取SQL */
	private String getBonusShareBeanQuery(String fSecurityCode, Date dDate) {
		String sql = " Select a.*,b.FSecurityName,c.FSecurityName As FSSecName,d.FPortName,e.FRoundName, " +
			" ' ' As FAssetGroupName,' ' as FCreatorName,' ' as FCheckUserName "+
			" From "+pub.yssGetTableName("Tb_Data_Bonusshare")+" a" +
			" Join "+pub.yssGetTableName("Tb_Para_Security")+" b On a.Ftsecuritycode = b.Fsecuritycode" +
			" Join "+pub.yssGetTableName("Tb_Para_Security")+" c On a.Fssecuritycode = c.Fsecuritycode" +
			" Left Join "+pub.yssGetTableName("Tb_Para_Portfolio")+" d On a.FPortCode = d.FPortCode "+
			" Join "+pub.yssGetTableName("Tb_Para_Rounding")+" e On a.FRoundCode = e.FRoundCode" +
			" Where a.Fcheckstate = 1"+
			" And a.Fexrightdate = "+dbl.sqlDate(dDate)+
			" And a.Ftsecuritycode = "+dbl.sqlString(fSecurityCode);
		return sql;
	}

	/**shashijie 2013-5-10 STORY 3926 获取每行数据的头几列值
	 * @param fSecurityCode 证券代码
	 * @return String
	 */
	private String getStrstureTitle(String fSecurityCode) {
		//拼接,股票代码\t股票名称\t
		String value = fSecurityCode + "\t";
		//获取证券基本信息对象
		SecurityBean secBean = getSecurityBean(fSecurityCode);
		//证券名称
		value += secBean.getStrSecurityName() + "\t";
		
		return value;
	}

	/**shashijie 2013-5-10 STORY 3926 判断列拼接条件
	 * @param operDate
	 * @param fHolidaysCode
	 * @param fPortCode2
	 * @return boolean
	 */
	private boolean isRowDataSpeling(Date operDate, String FHolidaysCode,
			String portCode) throws YssException {
		boolean flag = false;
		/*1.判断当天是TA结算联接设置的工作日
		 *2.判断是否显示 "没有替代申购的日期",若不显示则查询当天是否有申购数据
		 */
		if (isWorkDate(operDate,FHolidaysCode) && 
			(this.isNoDate.equals("0") || //0表示"显示"
			(this.isNoDate.equals("1") && isHaveDate(operDate,portCode)))
		){
			flag = true;
		}
		return flag;
	}

	/**shashijie 2013-5-10 STORY 3926 拼接每个单元格数据
	 *@param fSecurityCode 证券代码
	 *@param operDate 当前处理日期
	 *@param fPortCode2 组合代码
	 *@return strSture 每个单元格的数据 /t 拼接
	 */
	private String getStrSture(String fSecurityCode, Date operDate,
			String portCode, Date selectDate,BonusShareBean share) throws YssException {
		String value = "";
		//获取剩余数量
		double tRemaindAmount = getStandingBookAmount(fSecurityCode,operDate,portCode,selectDate,"FRemaindAmount");
		//是否调整报表日下一交易日权益
		if (this.isComment.equals("0")) {//是
			tRemaindAmount = getNextRightData(tRemaindAmount,
					Double.valueOf(share.getAfterTaxRatio()),share.getRoundCode());
		} 
		value = tRemaindAmount + "\t";
		
		return value;
	}

	/**shashijie 2013-5-10 STORY 3926 根据权益比例计算实际数量 */
	private double getNextRightData(double tRealAmount, double ratio,
			String roundCode) throws YssException {
		double value = 0;
		if (ratio == 0) {
			return tRealAmount;
		}
		//剩余数量 * (1+权益比例)
		value = YssD.add(tRealAmount,
				YssD.mul(tRealAmount, ratio));
		//舍入设置
		value = this.getSettingOper().reckonRoundMoney(
				roundCode ,
				value);
		
		return value;
	}

	/**shashijie 2013-5-10 STORY 3926 获取台长表中的数量 */
	private double getStandingBookAmount(String fSecurityCode, Date operDate,
			String portCode, Date selectDate2, String tag) throws YssException {
		double amount = 0;
		ResultSet rs = null;//定义游标
		try {
			String query = getAmountQuery(fSecurityCode,operDate,portCode,selectDate2);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				amount = rs.getDouble(tag);
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return amount;
	}

	/**shashijie 2013-5-10 STORY 获取sql */
	private String getAmountQuery(String fSecurityCode, Date operDate,
			String portCode, Date selectDate2) {
		String Sql = "Select Sum(a.FRemaindAmount) As FRemaindAmount " +
			" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" a" +
			" Where a.Fportcode = "+dbl.sqlString(portCode)+
			" And a.Fbuydate = "+dbl.sqlDate(operDate)+
			" " +
			" And a.Fsecuritycode = "+dbl.sqlString(fSecurityCode)+
			" And a.Fstockholdercode = ' '" +
			" And a.Fdate In (Select Max(b.Fdate) As Fdate" +
			" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" b"+
            " Where b.Fportcode = "+dbl.sqlString(portCode)+
            " And b.Fbuydate = "+dbl.sqlDate(operDate)+
            " And b.Fdate <= "+dbl.sqlDate(selectDate2)+
            " "+
            " And b.Fsecuritycode = "+dbl.sqlString(fSecurityCode)+
            " And b.Fstockholdercode = ' ')";
		return Sql;
	}

	/**shashijie 2013-5-10 STORY 3926 获取证券基本信息对象
	 * @param fSecurityCode 证券代码
	 * @return SecurityBean
	 */
	private SecurityBean getSecurityBean(String fSecurityCode) {
		SecurityBean secBean = new SecurityBean();
		secBean.setYssPub(pub);
		secBean.setSecurityCode(fSecurityCode);
		secBean.getSetting();
		return secBean;
	}

	/**shashijie 2013-5-10 STORY 3926  获取TA结算链接设置节假日
	 *@return FHolidayscode 节假日代码
	 */
	private String getTALinkFHolidaysCode(String PortCode) throws YssException {
		ResultSet rs = null;//定义游标
		String FHolidayscode = "";
		try {
			String query = " Select a.Fholidayscode " +
				" From "+pub.yssGetTableName("Tb_Ta_Cashsettle")+" a"+
				" Where a.Fcheckstate = 1" +
				" And a.Fportcode In (' ', "+dbl.sqlString(PortCode)+")" +
				//01表示申购
				" And a.Fselltypecode = '01'";
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				FHolidayscode = rs.getString("FHolidayscode");
			}
		} catch (Exception e) {
			throw new YssException("获取TA结算链接设置节假日出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return FHolidayscode;
	}

	/**shashijie 2013-05-09 STORY 3926 获取第一行*/
	private String getTitle(int number,Date date,String FHolidaysCode,String portCode) throws YssException {
		String str = "股票代码\t股票名称\t";
		
		for (int i = 0; i < number; i++) {
			//当前处理日期
			Date operDate = YssFun.addDay(date, i);
			//判断是否需要拼接该列数据
			if (isRowDataSpeling(operDate, FHolidaysCode, portCode)) {
				String unit = YssFun.formatDate(operDate);
				str += unit + "\t";
			}
		}
		str = YssFun.getSubString(str);
		return str;
	}

	/**shashijie 2013-5-10 STORY 3926 判断当天是否有申赎数据
	 * @param operDate 日期
	 * @return boolean 有数据则返回true
	 */
	private boolean isHaveDate(Date operDate,String portCode) throws YssException {
		boolean flag = false;
		ResultSet rs = null;//定义游标
		try {
			String query = "Select a.* From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" a" +
				" Where a.Fportcode = "+dbl.sqlString(portCode)+
				" And a.Fbuydate = "+dbl.sqlDate(operDate)+
				" And a.fstockholdercode = ' '"+
				" ";
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e) {
			throw new YssException("判断当天是否有申赎数据出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;
	}

	/**shashijie 2013-05-09 STORY 3926 判断是否是工作日,是返回true*/
	private boolean isWorkDate(Date pDate,String FHolidaysCode) throws YssException {
		boolean flag = false;
		if (YssFun.dateDiff(pDate,getWorkDayByWhere(FHolidaysCode, pDate, 0)) == 0) {
			flag = true;
		}
		return flag;
	}

	/**shashijie 2013-05-09 STORY 3926 获取股票蓝数据*/
	private ArrayList getStockList(Date date) throws YssException {
		ArrayList list = new ArrayList();
		ResultSet rs = null;//定义游标
		try {
			String query = getStockSql(date,YssFun.toDate(this.selectDate),this.FPortCode);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				list.add(rs.getString("Fsecuritycode"));
			}
		} catch (Exception e) {
			throw new YssException("获取股票蓝数据出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return list;
	}
	
	/**shashijie 2013-5-9 STORY 获取股票蓝SQL
	 *@param date
	 *@param date2
	 *@param fPortCode2
	 *@return String
	 */
	private String getStockSql(Date date, Date eDate, String fPortCode2) {
		String sql = "Select Distinct a.Fsecuritycode" +
			" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" a" +
			" Where a.Fportcode = "+dbl.sqlString(fPortCode2)+
			" And a.Fdate >= "+dbl.sqlDate(date)+
			" And a.Fdate < "+dbl.sqlDate(eDate) +
			//5表示可以现金替代
			" And a.FReplaceMark = '5' " +
			" Order by a.Fsecuritycode";
		return sql;
	}

	/**shashijie 2013-05-09 STORY 3926 获取工作日方法 */
	private Date getWorkDayByWhere(String sHolidayCode, Date dDate, int dayInt)
			throws YssException {
		Date mDate = null;// 工作日
		// 公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
		return mDate;
	}

	/**shashijie 2013-05-09 STORY 3926 把内容拼接上格式 */
	private String buildRowCompResult(String str,String FRelaCode) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            //获取格式
            hmCellStyle = getCellStyles(FRelaCode);
            for (int i = 0; i < sArry.length; i++) {
                sKey = FRelaCode + "\tDSF\t-1\t" + i;
                //拼接格式
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            //若有数据则去除最后一个\t
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
        
        selectDate = reqAry[0].split("\r")[1];//起始日期
        FPortCode = reqAry[1].split("\r")[1];//组合
        isNoDate = reqAry[2].split("\r")[1];//是否显示没有替代申购的日期
        isComment = reqAry[3].split("\r")[1];//是否调整报表日下一工作日权益
        
    }

    public String saveReport(String sReport) {
        return "";
    }

	public String getFStartDate() {
		return selectDate;
	}

	public void setFStartDate(String fStartDate) {
		selectDate = fStartDate;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	/**add---shashijie 2013-5-9 返回 repBean 的值*/
	public CommonRepBean getRepBean() {
		return repBean;
	}

	/**add---shashijie 2013-5-9 传入repBean 设置  repBean 的值*/
	public void setRepBean(CommonRepBean repBean) {
		this.repBean = repBean;
	}

	/**add---shashijie 2013-5-9 返回 isNoDate 的值*/
	public String getIsNoDate() {
		return isNoDate;
	}

	/**add---shashijie 2013-5-9 传入isNoDate 设置  isNoDate 的值*/
	public void setIsNoDate(String isNoDate) {
		this.isNoDate = isNoDate;
	}

	/**add---shashijie 2013-5-9 返回 isComment 的值*/
	public String getIsComment() {
		return isComment;
	}

	/**add---shashijie 2013-5-9 传入isComment 设置  isComment 的值*/
	public void setIsComment(String isComment) {
		this.isComment = isComment;
	}




}
