package com.yss.main.operdeal.report.repfix.Chinabank;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.vsub.YssFinance;

/**shashijie 2012-01-31 STORY 1953 */
public class ProfitTable extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//起始日期
    private String FEndDate = "";//截止日期
    private String FPortCode = "";//组合代码(多选)
    private String FCuryCode = "";//币种代码
    
    YssFinance fc = null;//通过套帐号获取组合代码
    private FixPub fixPub = null;//获取基金成立日那天的金额
    public ProfitTable() {
    }

    /**程序入口 shashijie 2012-2-29 STORY 1953*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        
    	//是否是合法期间
    	if (isLastingOrOver(FStartDate,FEndDate)) {
    		return sResult;
		}
    	
		//获取报表头部
        sResult += getHead();
        //获取报表内容
        sResult += getInfo();
        
        return sResult;
    }

    /**shashijie 2012-3-1 STORY 是否是合法期间*/
	private boolean isLastingOrOver(String StartDate, String EndDate) throws YssException {
		Date start = YssFun.parseDate(StartDate);//起始日期
		Date end = YssFun.parseDate(EndDate);//截止日期
		int day = YssFun.getDay(start);
		//如果起始日期不是月的第一天
		if (day != 1) {
			throw new YssException("请设置完整的会计期间。 ");
		}
		//如果截止日期不是月的最后一天
		day = YssFun.getMonthLastDay(YssFun.getYear(end), YssFun.getMonth(end));
		if (day != YssFun.getDay(end)) {
			throw new YssException("请设置完整的会计期间。 ");
		}
		//是否跨年
		if (YssFun.getYear(start) != YssFun.getYear(end)) {
			throw new YssException("报表查询不支持跨年操作。");
		}
		//是否超出余额表
		String info = isOvertake(YssFun.getMonth(end));
		if (!info.equals("")) {
			throw new YssException("查询日期超出当前会计期间！");
		}
		
		return false;
	}

	/**shashijie 2012-3-1 STORY 1953 查询月份是否超出余额表
	* @param month
	* @return*/
	private String isOvertake(int month) throws YssException {
		String falg = "";
		ResultSet rs = null;
		
		String[] port = FPortCode.split(",");
		for (int i = 0; i < port.length; i++) {
			String portCode = port[i];
			String query = " Select Max(FMonth) As FMonth From "+getTableName("A<YEAR><SET>lbalance", portCode);
			
			try {
				rs = dbl.openResultSet(query);
				while (rs.next()) {
					if (month > rs.getInt("FMonth")) {
						falg += port[i]+",";
					}
				}
			} catch (Exception e) {
				throw new YssException("查询日期超出当前会计期间！");
			} finally {
				dbl.closeResultSetFinal(rs);
			}
		}
		return YssFun.getSubString(falg);
	}

	/**shashijie 2012-2-29 STORY 1953 获取报表内容*/
	private String getInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
			String title = "项        目\t序号\t本期金额\t上期金额";//标题头
			//拼接格式
			str = buildRowCompResult(title)+"\r\n";
			HashMap map = new HashMap();
			
			String[] portCode = FPortCode.split(",");
	        for (int i = 0; i < portCode.length; i++) {
				String port = portCode[i];
				//获取需要统计的科目
				String strSql = getInfoSQl(port);
				rs = dbl.openResultSet(strSql);
				//封装入集合中
				while (rs.next()) {
					doOperStr(rs,map,port);
				}
	        }
	        
			//拼接
			str += doProcess(map);
			
		} catch (Exception e) {
			str = "";
			//throw new YssException("获取报表内容出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}

	/**shashijie 2012-2-29 STORY 1953 */
	private String doProcess(HashMap map) {
		String str = "";
		//获取本期金额--投资收益,公允价值变动收益,汇兑收益
		BigDecimal capital = getCapital(map,"6111,6101,6061");
		//获取上期金额
		BigDecimal lastTime = getLastTime(map,"6111,6101,6061");
		
		BigDecimal yesrcapital = capital; BigDecimal yesrlastTime = lastTime;//记录以便后面计算
		
		//拼接报表具体内容
		str += operionStr("一、营业收入","1",capital,lastTime);
		
		str += operionStr("已赚保费","2",0.00,0.00);
		str += operionStr("保险业务收入","3",0.00,0.00);
		str += operionStr("其中：分保费收入","4",0.00,0.00);
		str += operionStr("减：分出保费","5",0.00,0.00);
		str += operionStr("提取未到期责任准备金","6",0.00,0.00);
		//获取本期金额
		capital = getCapital(map,"6111");
		//获取上期金额
		lastTime = getLastTime(map,"6111");
		str += operionStr("投资收益（损失以“-”号填列）","7",capital,lastTime);
		
		str += operionStr("其中：对联营企业和合营企业的投资收益","8",0.00,0.00);
		
		//获取本期金额
		capital = getCapital(map,"6101");
		//获取上期金额
		lastTime = getLastTime(map,"6101");
		str += operionStr("公允价值变动收益（损失以“-”号填列）","9",capital,lastTime);
		
		//获取本期金额
		capital = getCapital(map,"6061");
		//获取上期金额
		lastTime = getLastTime(map,"6061");
		str += operionStr("汇兑收益（损失以“-”号填列）","10",capital,lastTime);
		
		str += operionStr("其他业务收入","11",0.00,0.00);
		
		//获取本期金额
		capital = getCapital(map,"6405,6421,6402,6701");
		//获取上期金额
		lastTime = getLastTime(map,"6405,6421,6402,6701");
		
		BigDecimal yezccapital = capital; BigDecimal yezclastTime = lastTime;//记录以便后面计算
		
		str += operionStr("二、营业支出","12",capital,lastTime);
		str += operionStr("退保金","13",0.00,0.00);
		str += operionStr("赔付支出","14",0.00,0.00);
		str += operionStr("其中：死亡给付","15",0.00,0.00);
		str += operionStr("伤残给付","16",0.00,0.00);
		str += operionStr("医疗给付","17",0.00,0.00);
		str += operionStr("满期给付","18",0.00,0.00);
		str += operionStr("年金给付","19",0.00,0.00);
		str += operionStr("赔款支出","20",0.00,0.00);
		str += operionStr("部分领取","21",0.00,0.00);
		str += operionStr("分保赔款","22",0.00,0.00);
		str += operionStr("减：摊回赔付支出","23",0.00,0.00);
		str += operionStr("提取保险责任准备金","24",0.00,0.00);
		str += operionStr("其中：提取未决赔款准备金","25",0.00,0.00);
		str += operionStr("提取寿险责任准备金","26",0.00,0.00);
		str += operionStr("提取长期健康险责任准备金","27",0.00,0.00);
		str += operionStr("减：摊回保险责任准备金","28",0.00,0.00);
		str += operionStr("保单红利支出","29",0.00,0.00);
		str += operionStr("分保费用","30",0.00,0.00);
		
		//获取本期金额
		capital = getCapital(map,"6405");
		//获取上期金额
		lastTime = getLastTime(map,"6405");
		str += operionStr("营业税金及附加","31",capital,lastTime);
		
		//获取本期金额
		capital = getCapital(map,"6421");
		//获取上期金额
		lastTime = getLastTime(map,"6421");
		str += operionStr("手续费及佣金支出","32",capital,lastTime);
		str += operionStr("业务及管理费","33",0.00,0.00);
		str += operionStr("其中：保险保障基金","34",0.00,0.00);
		str += operionStr("减：摊回分保费用","35",0.00,0.00);
		
		//获取本期金额
		capital = getCapital(map,"6402");
		//获取上期金额
		lastTime = getLastTime(map,"6402");
		str += operionStr("其他业务支出","36",capital,lastTime);
		
		//获取本期金额
		capital = getCapital(map,"6701");
		//获取上期金额
		lastTime = getLastTime(map,"6701");
		str += operionStr("资产减值损失","37",capital,lastTime);
		
		//营业利润=营业收入－营业支出
		BigDecimal yelrcapital = YssD.subD(yesrcapital, yezccapital);//营业利润本期
		BigDecimal yelrlastTime = YssD.subD(yesrlastTime, yezclastTime);//营业利润上期
		str += operionStr("三、营业利润（亏损以“-”号填列）","38",yelrcapital,yelrlastTime);
		
		//获取本期金额
		capital = getCapital(map,"6301");
		//获取上期金额
		lastTime = getLastTime(map,"6301");
		BigDecimal yewsrcapital = capital; BigDecimal yewsrlastTime = lastTime;//记录方便后面计算
		str += operionStr("加：营业外收入","39",capital,lastTime);
		
		//获取本期金额
		capital = getCapital(map,"6711");
		//获取上期金额
		lastTime = getLastTime(map,"6711");
		BigDecimal yewzccapital = capital; BigDecimal yewzclastTime = lastTime;//记录方便后面计算
		str += operionStr("减：营业外支出","40",capital,lastTime);
		
		//利润总额=营业利润+营业外收入-营业外支出
		BigDecimal lrzecapital = YssD.subD(YssD.addD(yelrcapital, yewsrcapital),yewzccapital);
		BigDecimal lrzelastTime = YssD.subD(YssD.addD(yelrlastTime, yewsrlastTime),yewzclastTime);
		str += operionStr("四、利润总额（亏损以“-”号填列）","41",lrzecapital,lrzelastTime);
		
		str += operionStr("减：所得税费用","42",0.00,0.00);
		
		//净利润=利润总额-所得税费用；
		str += operionStr("五、净利润（亏损以“-”号填列）","43",lrzecapital,lrzelastTime);
		
		str += operionStr("归属于母公司所有者的净利润","44",0.00,0.00);
		str += operionStr("少数股东损益","45",0.00,0.00);
		str += operionStr("六、每股收益","46",0.00,0.00);
		str += operionStr("（一）基本每股收益","47",0.00,0.00);
		str += operionStr("（二）稀释每股收益","48",0.00,0.00);
		
		//获取本期金额
		capital = getCapital(map,"6901");
		//获取上期金额
		lastTime = getLastTime(map,"6901");
		str += operionStr("注：以前年度损益调整","49",capital,lastTime);

		return str;
	}

	/**shashijie 2012-2-29 STORY 1953 拼接每行数据
	* @param row1
	* @param string2
	* @param capital
	* @param lastTime
	* @return*/
	private String operionStr(String row1, String row2, BigDecimal capital,
			BigDecimal lastTime) {
		String str = "";
		
		str += row1 + "\t";//第一列
		
		str += row2 + "\t";//第二列
		
		str += capital.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";//本期金额
		
		str += lastTime.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";//上期金额
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**shashijie 2012-3-1 STORY 1953 重载
	* @param row1
	* @param row2
	* @param capital
	* @param lastTime
	* @return*/
	private String operionStr(String row1, String row2, double capital,
			double lastTime) {
		
		String str = operionStr(row1, row2, new BigDecimal(capital), new BigDecimal(lastTime));
		
		return str;
	}

	/**shashijie 2012-2-29 STORY 获取上期金额,起始日期上个月的余额*/
	private BigDecimal getLastTime(HashMap map,String key) {
		BigDecimal money = new BigDecimal(0);//同组合下金额
		BigDecimal moneyValue = new BigDecimal(0);//总计金额
		String portCode = "";//组合
		String groupCode = "";//组合群
		try {
			String[] portCodes = FPortCode.split(",");
	        for (int k = 0; k < portCodes.length; k++) {//循环组合
	        	groupCode = portCodes[k].split("-")[0];
	        	portCode = portCodes[k].split("-")[1];
				int month = YssFun.getMonth(YssFun.parseDate(FStartDate))-1;
				//获取每个科目上个月的余额
				String[] keys = key.split(",");
				for (int i = 0; i < keys.length; i++) {
					String keyWord = keys[i]+"\t"+month+"\t"+portCode;//科目代码+月份+组合
					
					//如果有余额
					if (map.containsKey(keyWord)) {
						String value = map.get(keyWord).toString();
						money = money.add(new BigDecimal(value.split("\t")[2]));//获取上个月的期末余额
					}
				}
				
				//获取汇率并计算
				moneyValue = moneyValue.add(getRate(money,portCode,groupCode));
				money = new BigDecimal(0);
	        }
		} catch (Exception e) {
			
		}
		return moneyValue;
	}

	/**shashijie 2012-2-29 STORY 1953 获取本期金额(发生额) */
	private BigDecimal getCapital(HashMap map,String key) {
		BigDecimal money = new BigDecimal(0);//同组合下本期金额
		BigDecimal moneyValue = new BigDecimal(0);//总计本期金额
		String portCode = "";//组合
		String groupCode = "";//组合群
		try {
			String[] portCodes = FPortCode.split(",");
	        for (int k = 0; k < portCodes.length; k++) {//循环组合
	        	groupCode = portCodes[k].split("-")[0];
	        	portCode = portCodes[k].split("-")[1];
				//统计每个科目期间月份的发生额的总和
				String[] keys = key.split(",");
				for (int i = 0; i < keys.length; i++) {//科目
					//起始日~截止日之间的月份
					int iBigMonth = YssFun.monthDiff(YssFun.parseDate(FStartDate), YssFun.parseDate(FEndDate));
					for (int j = 0; j <= iBigMonth; j++) {//日期
						int month = YssFun.getMonth(YssFun.parseDate(FEndDate))-j;
						String keyWord = keys[i]+"\t"+month+"\t"+portCode;//科目代码+月份+组合
						
						//如果有发生额
						if (map.containsKey(keyWord)) {
							String value = map.get(keyWord).toString();
							money = money.add(getMoney(value));//获取一个月的发生额
						}
			        }
				}
				//获取汇率并计算
				moneyValue = moneyValue.add(getRate(money,portCode,groupCode));
				money = new BigDecimal(0);
			}
		} catch (Exception e) {
			
		} 
		return moneyValue;
	}

	/**shashijie 2012-2-29 STORY 1953 */
	private BigDecimal getRate(BigDecimal money,String portCode,String groupCode) {
		BigDecimal value = money;//金额
		if (value.doubleValue()==0) {
			return value;
		}
		String assetGroupCode = pub.getAssetGroupCode();//保存当前组合群代码
		pub.setPrefixTB(groupCode);//设置系统组合群
		try {
			//公共获取汇率类
			double FBaseCuryRate = this.getSettingOper().getCuryRate( //基础汇率
								YssFun.parseDate(FEndDate), 
								FCuryCode, 
								portCode, 
								YssOperCons.YSS_RATE_BASE); 
			double FPortCuryRate = this.getSettingOper().getCuryRate( //组合汇率
								YssFun.parseDate(FEndDate), 
								"", 
								portCode, 
								YssOperCons.YSS_RATE_PORT);
			BigDecimal base = new BigDecimal(FBaseCuryRate);//基础汇率
			BigDecimal port = new BigDecimal(FPortCuryRate);//组合汇率
			value = YssD.divD(YssD.mulD(money, port),base);//由于线面是选择的币种作为本币,这里需要和以往计算反向一下
		} catch (Exception e) {
			
		} finally {
			pub.setPrefixTB(assetGroupCode);
		}
		return value;
	}

	/**shashijie 2012-2-29 STORY 1953 */
	private BigDecimal getMoney(String value) {
		BigDecimal money = null;//金额
		String[] values = value.split("\t");
		
		BigDecimal FBDebit = new BigDecimal(values[0]);//本期借方发生额
		//BigDecimal FBCredit = new BigDecimal(values[1]);//本期贷方发生额
		
		money = FBDebit;//YssD.addD(FBDebit, FBCredit);
		return money;
	}

	/**shashijie 2012-2-29 STORY 获取报表内容*/
	private void doOperStr(ResultSet rs,HashMap map,String portCode) throws YssException,SQLException{
		String portcode = portCode.split("-")[1];//组合代码
		//FAcctCode 科目代码,FMonth 月份,portcode 组合
		String key = rs.getString("FAcctCode")+"\t"+rs.getString("FMonth")+"\t"+portcode;
		
		//FBDebit 本期借方发生额,FBCredit 本期贷方发生额,FBEndBal 期末余额
		String value = rs.getString("FBDebit")+"\t"+rs.getString("FBCredit")+"\t"+rs.getString("FBEndBal");
		
		map.put(key, value);
		
	}

	/**shashijie 2012-2-29 STORY 1953 获取报表头部*/
	private String getHead() throws YssException {
		String str = "";
		/*ResultSet rs = null;
		try {
			String endDate = YssFun.formatDate(
					YssFun.parseDate(this.FEndDate)
					,"yyyy年MM月");//选择日期
			String souvenir = "表号:";
			String title = "中国银行托管及投资者服务部";
			String CuryCodes = "单位:"+this.FCuryCode;//币种
			
			//拼接格式
			str = buildRowCompResult(endDate+"\t \t \t"+souvenir)+"\r\n";
			str += buildRowCompResult(title+"\t \t \t"+CuryCodes)+"\r\n";
		} catch (Exception e) {
			throw new YssException("获取报表头部出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}*/
		return str;
	}
	
	/**shashijie 2012-2-29 STORY 1953*/
	private String getInfoSQl(String portCode) throws YssException{
		
		String string = " Select a.Facctcode, a.Facctlevel, a.Facctname, a.Facctdetail, b.Fmonth, "+
	        " Sum(b.Fbdebit) As Fbdebit, Sum(b.Fbcredit) As Fbcredit, Sum(b.Fbendbal) As Fbendbal "+
			" From "+getTableName("A<YEAR><SET>laccount", portCode)+" a "+
			" Left Join "+getTableName("A<YEAR><SET>lbalance", portCode)+" b On a.Facctcode = b.Facctcode "+
			" Left Join "+getTableName("A<YEAR><SET>lcurrency", portCode)+" c On a.Fcurcode = c.Fcurcode "+
			" Where a.Facctcode In ( " +operSql.sqlCodes("6111,6101,6061,6405,6421,6402,6701,6301,6711,6901")+ " ) "+
			" Group By a.Facctcode, a.Facctlevel, a.Facctname, a.Facctdetail, b.Fmonth ";
		return string;
	}

	/**shashijie 2012-2-29 STORY 1953 获取财务表明*/
	private String getTableName(String name,String portCode) {
		String tableName = "";
		try {
			//转换日期
			tableName = name.replaceAll("<YEAR>",String.valueOf(YssFun.getYear(YssFun.parseDate(FEndDate))));
			
			//转换套账号
			tableName = tableName.replaceAll("<SET>",getFSetCode(portCode));
		} catch (Exception e) {

		}
		return tableName;
	}

	/**shashijie 2012-2-29 STORY 1953 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("LiRunBiao");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "LiRunBiao" + "\tDSF\t-1\t" + i;
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
        
        FStartDate = reqAry[0].split("\r")[1];//起始日期
        FEndDate = reqAry[1].split("\r")[1];//截止日期
        FPortCode = reqAry[2].split("\r")[1];//组合
        FCuryCode = reqAry[3].split("\r")[1];//币种代码
        
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
    }

    public String saveReport(String sReport) {
        return "";
    }

	public String getFStartDate() {
		return FStartDate;
	}

	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	public String getFEndDate() {
		return FEndDate;
	}

	public void setFEndDate(String fEndDate) {
		FEndDate = fEndDate;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	public String getFCuryCode() {
		return FCuryCode;
	}

	public void setFCuryCode(String fCuryCode) {
		FCuryCode = fCuryCode;
	}

	/** shashijie 2012-3-5 STORY 1953 获取套账号 
	 * @param groupProtCode 组合群代码  - 组合代码     必须是这种拼接方式*/
	private String getFSetCode(String groupProtCode) throws YssException{
		String assetCode = "";//资产代码
		String setCode = "";//套账号
		ResultSet rs = null;
		if (groupProtCode.split("-").length < 2) {
			return "";
		}

		String assetGroups = groupProtCode.split("-")[0];//组合群代码
		String portCode = groupProtCode.split("-")[1];//组合代码
		try {
			String strSql = " select * from Tb_" + assetGroups
					+ "_Para_Portfolio where FCheckState = 1 and FEnabled =1 "
					+ " and FPortCode = " + dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				assetCode = rs.getString("FASSETCODE");//资产代码
				//获取套帐代码
				setCode = this.getSetCode(assetCode);//套账号
			}

		} catch (Exception e) {
			throw new YssException("获取套帐代码出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return setCode;
    }
	
    /**shashijie 2012-3-5 STORY 1953 获取套帐代码
    * @param assetCode
    * @return
    * @throws YssException*/
    private String getSetCode(String assetCode) throws YssException{
		ResultSet rs = null;
		String setCode = "";
		try {
			String strSql = "select * from LSetList where FsetId="
					+ dbl.sqlString(assetCode) + " order by Fyear desc";
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				setCode = YssFun.formatNumber(rs.getDouble("FSETCODE"), "000");
			}
		} catch (Exception e) {
			throw new YssException("获取套帐代码出错： \n" + e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return setCode;
    }


}
