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

/**shashijie 2012-3-3 STORY 1962 */
public class ListProperty extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//起始日期
    private String FEndDate = "";//截止日期
    private String FPortCode = "";//组合代码(多选)
    private String FCuryCode = "";//币种代码
    
    YssFinance fc = null;//通过套帐号获取组合代码
    private FixPub fixPub = null;//获取基金成立日那天的金额
    public ListProperty() {
    }

    /**程序入口 shashijie 2012-3-3 STORY 1962*/
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

	/**shashijie 2012-3-3 STORY 是否是合法期间*/
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

	/**shashijie 2012-3-3 STORY 1962 查询月份是否超出余额表
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

	/**shashijie 2012-3-3 STORY 1962 获取报表内容*/
	private String getInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
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

	/**shashijie 2012-3-3 STORY 1962 */
	private String doProcess(HashMap map) {
		String str = "";
		
		BigDecimal allStart = new BigDecimal(0);//总计期初余额C
		BigDecimal allEnd = new BigDecimal(0);//总计期末余额I
		BigDecimal allAdd = new BigDecimal(0);//总计本年增加数D
		BigDecimal allTotal = new BigDecimal(0);//总计合计项目H
		
		//拼接报表具体内容
		str += operionStr("一、贷款损失准备合计","1",0.00,0.00,0.00);
		str += operionStr("（一）保户质押贷款","2",0.00,0.00,0.00);
		str += operionStr("（二）其他贷款","3",0.00,0.00,0.00);
		str += operionStr("二、坏账准备合计","4",0.00,0.00,0.00);
		str += operionStr("（一）活期银行存款","5",0.00,0.00,0.00);
		str += operionStr("（二）定期银行存款","6",0.00,0.00,0.00);
		str += operionStr("（三）其他货币资金","7",0.00,0.00,0.00);
		str += operionStr("（四）应收保费","8",0.00,0.00,0.00);
		str += operionStr("（五）应收股利","9",0.00,0.00,0.00);
		str += operionStr("（六）应收利息","10",0.00,0.00,0.00);
		str += operionStr("（七）其他应收款","11",0.00,0.00,0.00);
		str += operionStr("（八）长期应收款","12",0.00,0.00,0.00);
		str += operionStr("（九）拆出资金","13",0.00,0.00,0.00);
		str += operionStr("（十）其他","14",0.00,0.00,0.00);
		
		BigDecimal start = getStart(map,"150311",0,FStartDate);//期初余额C
		BigDecimal end = getStart(map,"150311",1,FEndDate);//期末余额I
		BigDecimal add = getAdd(map,"150311",2);//本期借方发生额D
		BigDecimal total = getAdd(map,"150311",3);//本期贷方发生额H
		
		str += operionStr("三、可供出售金融资产减值准备","15",start,add.doubleValue() > 0 ? add : new BigDecimal(0),
				total,end);
		
		allStart = allStart.add(start);//总计期初余额C
		allEnd = allEnd.add(end);//总计期末余额I
		allAdd = allAdd.add(add);//总计本期借方发生额D
		allTotal = allTotal.add(total);//总计本期贷方发生额H
		
		str += operionStr("四、持有至到期投资减值准备","16",0.00,0.00,0.00);
		str += operionStr("五、长期股权投资减值准备","17",0.00,0.00,0.00);
		str += operionStr("六、投资性房地产减值准备","18",0.00,0.00,0.00);
		str += operionStr("七、固定资产减值准备合计","19",0.00,0.00,0.00);
		str += operionStr("    其中：房屋、建筑物","20",0.00,0.00,0.00);
		str += operionStr("八、在建工程减值准备","21",0.00,0.00,0.00);
		str += operionStr("九、无形资产减值准备","22",0.00,0.00,0.00);
		str += operionStr("十、商誉减值准备","23",0.00,0.00,0.00);
		str += operionStr("十一、抵债资产减值准备","24",0.00,0.00,0.00);
		str += operionStr("十二、其他资产减值准备","25",0.00,0.00,0.00);
		
		
		str += operionStr("总计","26",allStart,allAdd,allTotal,allEnd);
		
		return str;
	}

	/**shashijie 2012-3-3 STORY 1962 重载
	* @param string
	* @param string2
	* @param start
	* @param capital
	* @return*/
	private String operionStr(String string, String string2, BigDecimal start,
			BigDecimal add,BigDecimal H,BigDecimal end) {
		//不统计项 0 
		BigDecimal zero = new BigDecimal(0);
		
		String str = "";
		
		str += operionStr(string, string2, start, add, zero, zero, zero, H, end, add);
		
		return str;
	}

	/**shashijie 2012-3-3 STORY 1962 拼接每行数据
	* @param row1
	* @param string2
	* @param start
	* @param add
	* @return*/
	private String operionStr(String row1, String row2, BigDecimal start,
			BigDecimal add,BigDecimal E,BigDecimal F,BigDecimal G,BigDecimal H,BigDecimal end,BigDecimal J) {
		String str = "";
		
		str += row1 + "\t";//第一列
		
		str += row2 + "\t";//第二列
		
		str += start.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";//期初余额
		
		str += add.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";//本年增加数
		
		str += E.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += F.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += G.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += H.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += end.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += J.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
	/**shashijie 2012-3-3 STORY 1962 重载
	* @param row1
	* @param row2
	* @param start , double add , double end
	* @param lastTime
	* @return*/
	private String operionStr(String row1, String row2, double start , double add , double end) {
		BigDecimal zero = new BigDecimal(0);
		
		BigDecimal start1 = new BigDecimal(start);
		BigDecimal add1 = new BigDecimal(add);
		BigDecimal end1 = new BigDecimal(end);
		
		String str = operionStr(row1, row2, start1, add1,zero,zero,zero,zero,end1,zero);
		
		return str;
	}

	/**shashijie 2012-3-3 STORY 1962 获取map金额 */
	private BigDecimal getAdd(HashMap map,String key,int falg) {
		BigDecimal money = new BigDecimal(0);//同组合下金额
		BigDecimal moneyValue = new BigDecimal(0);//总计金额
		String portCode = "";//组合
		String groupCode = "";//组合群
		try {
			String[] portCodes = FPortCode.split(",");
	        for (int k = 0; k < portCodes.length; k++) {//循环组合
	        	groupCode = portCodes[k].split("-")[0];
	        	portCode = portCodes[k].split("-")[1];
				
				//获取每个科目的余额
				String[] keys = key.split(",");
				for (int i = 0; i < keys.length; i++) {//科目
					
					//起始日~截止日之间的月份
					int iBigMonth = YssFun.monthDiff(YssFun.parseDate(FStartDate), YssFun.parseDate(FEndDate));
					for (int j = 0; j <= iBigMonth; j++) {//日期
						int month = YssFun.getMonth(YssFun.parseDate(FEndDate))-j;//注意:这里取的是结束日期月份用递减的方式
						String keyWord = keys[i]+"\t"+month+"\t"+portCode;//科目代码+月份+组合
						
						//如果有余额
						if (map.containsKey(keyWord)) {
							String value = map.get(keyWord).toString();
							money = money.add(new BigDecimal(value.split("\t")[falg]));//获取map金额
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

	/**shashijie 2012-3-14 STORY 1962 获取map金额
	* @param map
	* @param key 凭证科目
	* @param falg 标示,0表示期初,1表示期末
	* @return*/
	private BigDecimal getStart(HashMap map, String key, int falg,String dDate) {
		BigDecimal money = new BigDecimal(0);//同组合下金额
		BigDecimal moneyValue = new BigDecimal(0);//总计金额
		String portCode = "";//组合
		String groupCode = "";//组合群
		try {
			String[] portCodes = FPortCode.split(",");
	        for (int k = 0; k < portCodes.length; k++) {//循环组合
	        	groupCode = portCodes[k].split("-")[0];
	        	portCode = portCodes[k].split("-")[1];
				int month = YssFun.getMonth(YssFun.parseDate(dDate));//日期月份
				//获取每个科目的余额
				String[] keys = key.split(",");
				for (int i = 0; i < keys.length; i++) {
					String keyWord = keys[i]+"\t"+month+"\t"+portCode;//科目代码+月份+组合
					
					//如果有余额
					if (map.containsKey(keyWord)) {
						String value = map.get(keyWord).toString();
						money = money.add(new BigDecimal(value.split("\t")[falg]));//获取map金额
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

	/**shashijie 2012-3-3 STORY 1962 */
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

	/**shashijie 2012-3-3 STORY 获取报表内容*/
	private void doOperStr(ResultSet rs,HashMap map,String portCode) throws YssException,SQLException{
		String portcode = portCode.split("-")[1];//组合代码
		//FAcctCode 科目代码,FMonth 月份,portcode 组合
		String key = rs.getString("FAcctCode")+"\t"+rs.getString("FMonth")+"\t"+portcode;
		
		//FBStartBal 起初余额,FBEndBal 期末余额	,FBAccDebit 本期借方发生额	,FBAccCredit 本期贷方发生额	
		String value = rs.getString("FBStartBal")+"\t"+rs.getString("FBEndBal")+"\t"+rs.getString("FBAccDebit")+
			"\t"+rs.getString("FBAccCredit");
		
		map.put(key, value);
		
	}

	/**shashijie 2012-3-3 STORY 1962 获取报表头部*/
	private String getHead() throws YssException {
		String str = "";
		/*ResultSet rs = null;
		try {
			String endDate = YssFun.formatDate(
					YssFun.parseDate(this.FEndDate)
					,"yyyy年MM月");//选择日期
			String CuryCodes = "单位:"+this.FCuryCode;//币种
			
			//拼接格式
			str += buildRowCompResult("")+"\r\n";
		} catch (Exception e) {
			throw new YssException("获取报表头部出错： \n");
		} finally {
			dbl.closeResultSetFinal(rs);
		}*/
		return str;
	}
	
	/**shashijie 2012-3-3 STORY 1962*/
	private String getInfoSQl(String portCode) throws YssException{
		
		String string = " Select a.Facctcode, a.Facctlevel, a.Facctname, a.Facctdetail, b.Fmonth, "+
	        " Sum(b.FBStartBal) As FBStartBal, Sum(b.FBEndBal) As FBEndBal,Sum(b.FBAccDebit) as FBAccDebit," +
	        " Sum(b.FBAccCredit) as FBAccCredit "+
			" From "+getTableName("A<YEAR><SET>laccount", portCode)+" a "+
			" Left Join "+getTableName("A<YEAR><SET>lbalance", portCode)+" b On a.Facctcode = b.Facctcode "+
			" Left Join "+getTableName("A<YEAR><SET>lcurrency", portCode)+" c On a.Fcurcode = c.Fcurcode "+
			" Where a.Facctcode In ( " +operSql.sqlCodes("150311")+ " ) "+
			" Group By a.Facctcode, a.Facctlevel, a.Facctname, a.Facctdetail, b.Fmonth ";
		return string;
	}

	/**shashijie 2012-3-3 STORY 1962 获取财务表明*/
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

	/**shashijie 2012-3-3 STORY 1962 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("ListProperty");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "ListProperty" + "\tDSF\t-1\t" + i;
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

	/** shashijie 2012-3-5 STORY 1962  获取套账号 
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
	
    /**shashijie 2012-3-5 STORY 1962 获取套帐代码
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
