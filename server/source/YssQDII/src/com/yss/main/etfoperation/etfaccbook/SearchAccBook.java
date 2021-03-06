package com.yss.main.etfoperation.etfaccbook;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.timeandaverage.TempBook;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.StandingBookBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

/**
 * 此类做台账的查询功能
 * @author xuqiji 20091026 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class SearchAccBook extends CtlETFAccBook{
	
	
	public SearchAccBook(){
		
	}
	
	/**获取台帐证券代码,根据申赎日期从股票篮文件中获取 */
	public String getSecurityCode(String sType,String sPortCode) throws YssException{
		StringBuffer buff=null;//做拼接SQL语句
		ResultSet rs=null;//声明结果集
		String []tradeDate =null;//保存前台传来的数据的数组
		SecurityBean security=null;//声明证券代码的bean
		StringBuffer bufShow =null;//保存查询出的数据
		StringBuffer bufAll =null;//保存拼接好的数据
		String sHeader = "";//表头
		String sShowDataStr = "";//返回的需要字段的值
        String sAllDataStr = "";//返回的所有字段的值
		try{
			tradeDate = sType.split("/t");//拆分前台数据
			//add by zhouxiang MS01545    ETF台账报表查询后，再点击证券代码报错  增加一个变量用来提取日期-------
			String tradeDateSplit=tradeDate[1].split("\b\b")[0];
			//end by zhouxiang MS01545    ETF台账报表查询后，再点击证券代码报错  ------------------------------
			buff = new StringBuffer(1000);//实例化
			bufShow =new StringBuffer(1000);
			bufAll = new StringBuffer(1000);
			sHeader = "证券代码\t证券名称";//赋值
			security = new SecurityBean();//实例化对象
			security.setYssPub(pub);//设置PUB
			buff.append("select s.fsecuritycode,se.fsecurityname,se.FStartDate,");
			buff.append("se.FCatCode,se.FSubCatCode,se.FExchangeCode,se.FMarketCode,");
			buff.append("se.FCheckState,se.FCreator,se.FcreateTime,se.FCheckUser,se.FCheckTime from ");
			buff.append(pub.yssGetTableName("tb_etf_stocklist")).append(" s join ");//股票篮
			buff.append(pub.yssGetTableName("tb_para_security"));
			buff.append(" se on se.fsecuritycode = s.fsecuritycode");
			buff.append(" where s.fdate = ").append(dbl.sqlDate(tradeDateSplit));
			buff.append(" and s.fportcode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and se.fcheckstate = 1");
			
			rs=dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			
			
			while(rs.next()){
			bufShow.append( (rs.getString("FSecurityCode") + "")). 
                append("\t");//证券代码
            bufShow.append( (rs.getString("FSecurityName") + "")). 
                append("\t").append(YssCons.YSS_LINESPLITMARK);//证券名称

            security.setSecurityCode(rs.getString("FSecuritycode"));		//证券代码
			security.setSecurityName(rs.getString("fsecurityname"));		//证券名称
			security.setDtStartDate(rs.getDate("FStartDate"));				//启用日期
			security.setStrCategoryCode(rs.getString("FCatCode"));			//品种类型
			
			//add by zhouxiang MS01545    ETF台账报表查询后，再点击证券代码报错  增加一个变量用来提取日期-------
			//security.setStrCategoryName(rs.getString("FCatName"));			//品种名称
			security.setStrSubCategoryCode(rs.getString("FSubCatCode"));	//品种子类型
			//security.setStrSubCategoryName(rs.getString("FSubCatName"));	//品种子类型名称
			security.setStrExchangeCode(rs.getString("FExchangeCode"));		//交易所代码
			//security.setStrExchangeName(rs.getString("FExchangeName"));		//交易所名称
			//security.setStrRegionCode(rs.getString("FRegionCode"));			//地域代码
			//security.setStrRegionName(rs.getString("FRegionName"));			//地域名称
			//security.setStrCountryCode(rs.getString("FCountryCode"));		//国家代码
			//security.setStrCountryName(rs.getString("FCountryName"));		//国家名称
			//security.setStrAreaCode(rs.getString("FAreaCode"));				//地区代码
//			//security.setStrAreaName(rs.getString("FAreaName"));     		//地区名称
			security.setStrMarketCode(rs.getString("FMarketCode"));			//上市代码
//			security.setStrTradeCuryCode(rs.getString("FTradeCury"));		//交易货币代码
//			security.setStrTradeCuryName(rs.getString("FCurrencyName"));	//交易货币名称
//			security.setStrSectorCode(rs.getString("FSectorCode"));			//行业板块代码
//			security.setStrSectorName(rs.getString("FSectorName"));			//行业板块名称
//			security.setStrSettleDayType(rs.getString("FSettleDayType"));	//结算日期类型
//			security.setIntSettleDays(rs.getInt("FSettleDays"));			//延迟结算延迟天数
//			security.setDblFactor(rs.getDouble("FFactor"));					//报价因子
//			security.setDblTotalShare(rs.getBigDecimal("FTotalShare"));		//总股本
//			security.setDblCurrentShare(rs.getBigDecimal("FCurrentShare"));	//流动股本
//			security.setDblHandAmount(rs.getBigDecimal("FHandAmount"));		//每手数量
//			security.setStrIssueCorpCode(rs.getString("FIssueCorpCode"));	//发行人代码
//			security.setStrIssueCorpName(rs.getString("FIssueCorpName"));	//发行人名称
//			security.setStrCusCatCode(rs.getString("FCusCatCode"));			//自定义子品种代码
//			security.setStrCusCatName(rs.getString("FCusCatName"));			//自定义子品种名称
//			security.setStrHolidaysCode(rs.getString("FHolidaysCode"));		//节假日群代码
//			security.setStrHolidaysName(rs.getString("FHolidaysName"));		//节假日群名称
//			security.setStrExternalCode(rs.getString("FExternalCode"));		//外部代码
//			security.setStrsiInCode(rs.getString("FISINCode")); 			//INSI 代码
//			security.setStrDesc(rs.getString("FDesc"));						//证券描述
			//end by zhouxiang MS01545    ETF台账报表查询后，再点击证券代码报错  ------------------------------
			security.checkStateId = rs.getInt("FCheckState");				//审核状态
			security.creatorCode = rs.getString("FCreator");				//创建人
			security.creatorTime = rs.getString("FcreateTime");				//创建时间
			security.checkUserCode = rs.getString("FCheckUser");			//审核人
			security.checkTime = rs.getString("FCheckTime");				//审核时间
            bufAll.append(security.buildRowStr()).append(YssCons.
                YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		}catch(Exception e){
			throw new YssException("获取需要补票的证券代码出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**获取ETF参数设置中一些数据*/
	public String getETFParamDealDayNum(String sType) throws YssException {
		StringBuffer buff=null;//做拼接SQL语句
		ResultSet rs=null;//声明结果集
		String []tradeDate =null;//保存前台传来的条件
		String ETFBookAccount ="";//补票次数
		String [] sData = null;
		try{
			tradeDate = sType.split("/t");//解析数据
			sData = tradeDate[1].split("\f\f");
			buff = new StringBuffer(100);
			buff.append(" select * from ").append(pub.yssGetTableName("Tb_ETF_Param"));//ETF参数设置表
			buff.append(" where FPortCode = ").append(dbl.sqlString(sData[1]));
			
			rs=dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				//补票次数+ETF台账报表申购数据列参数设置+ETF台账报表赎回数据列参数设置+ETF台账报表权益数据列参数设置
				//+ETF台账报表补票和强制处理数据列参数设置+ETF台账报表应退款估值增值列参数设置	
				//+ETF台账报表其它数据列参数设置+赎回应付替代结转+申购应付替代结转+单位成本保留小数位数
				//+ 补票开始日 + 补票方式
				/**shashijie 2013-1-18 STORY 3402 国泰虽然是2次补票但是前台需要显示3次补票的数列,所以这里得人为的加一次*/
				if (rs.getString("FSupplyMode") !=null && 
						rs.getString("FSupplyMode").trim().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) {
					ETFBookAccount = Integer.toString(rs.getInt("FDealDayNum") + 1) + "\f\f";
				}else {
					ETFBookAccount = Integer.toString(rs.getInt("FDealDayNum")) + "\f\f";
				}
				/**end shashijie 2013-1-18 STORY 3402 */
				ETFBookAccount += 
					  (rs.getString("FSubscribe")!=null?rs.getString("FSubscribe"):" ") + "\f\f" 
					+ (rs.getString("FRedeem")!=null?rs.getString("FRedeem"):" ") + "\f\f"
					+ (rs.getString("FRight")!=null?rs.getString("FRight"):" ") + "\f\f" 
					+ (rs.getString("FSupplyAndForce")!=null?rs.getString("FSupplyAndForce"):" ") + "\f\f" 
					+ (rs.getString("FQuitMoneyValue")!=null?rs.getString("FQuitMoneyValue"):" ") + "\f\f"
					+ (rs.getString("FOther")!=null?rs.getString("FOther"):" ") + "\f\f" 
					+ Integer.toString(rs.getInt("FSHDealReplace")) + "\f\f" 
					+ Integer.toString(rs.getInt("FSGDealReplace")) + "\f\f"
					+ Integer.toString(rs.getInt("FUnitdigit")) + "\f\f"
					+ Integer.toString(rs.getInt("FBeginSupply"))+"\f\f"
					+ rs.getString("FSupplyMode");
			}
		}catch(Exception e){
			throw new YssException("获取ETF参数设置中数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return ETFBookAccount;
	}
	
	/**shashijie 2013-1-18 STORY 3402 重构代码,查询ETF台账数据 */
	public String getETFBookData(String type) throws YssException {
		String[] sType = type.split("/t");//解析前台传来的数据
		this.parseRowStr(sType[1]);//用基类的方法解析数据
		
		ETFParamSetAdmin para = new ETFParamSetAdmin();//参数设置操作类
		para.setYssPub(pub);
		//保存参数设置的hash表
		HashMap paraMap = para.getETFParamInfo(this.portCodes);//获取保存参数设置的hash表
		ETFParamSetBean paraSetBean=(ETFParamSetBean)paraMap.get(this.portCodes);//获取参数设置实体bean
		
		//封装台账数据
		ArrayList bookList = doSetBookList(paraSetBean);
		//返回前台的数据
		StringBuffer buffAll = doGetBuffAll(paraSetBean,bookList);
		
		return buffAll.toString();
	}
	
	/*
	 * 
	 * add by yeshenghong  20120530 
	 * to get the page count of the data
	 */
	
	public String getBookDataPageCount(String type) throws YssException
	{
		String []sType=null;//解析前台传来的数据
		StringBuffer buff=null;//做拼接SQL语句
		ResultSet rs=null;//结果集
//		ETFParamSetAdmin para=null;//参数设置操作类
//		ETFParamSetBean paraSetBean=null;//参数设置实体bean
//		HashMap paraMap = null;//保存参数设置的hash表
		int pageCount = 0;
		int showDataCount = 0;
		try{
			//实例化
//			paraMap = new HashMap();
			buff = new StringBuffer(1000);
			
			sType = type.split("/t");//解析前台传来的数据
			this.parseRowStr(sType[1]);//用基类的方法解析数据
			
//			para = new ETFParamSetAdmin();
//			para.setYssPub(pub);
//			paraMap = para.getETFParamInfo(this.portCodes);//获取保存参数设置的hash表
//			
//			paraSetBean=(ETFParamSetBean)paraMap.get(this.portCodes);//获取参数设置实体bean
			if(this.securityCodes.trim().length()>0)//按证券代码查  不分页  
			{
				return "0";
			}
			buff.append(" select s.FSecurityCode, count(s.FSecurityCode) as Fcount from ").append(pub.yssGetTableName("Tb_ETF_StandingBook"));//台帐表
			buff.append(" s join (select max(fdate) as fmaxdate,fportcode from ");
			buff.append(pub.yssGetTableName("Tb_ETF_StandingBook"));
			buff.append(" where fdate <= ").append(dbl.sqlDate(this.browDate));
			buff.append(" and FportCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(" and FBuyDate =").append(dbl.sqlDate(this.startDate));
			buff.append(" and FBs =").append(dbl.sqlString(this.standingBookType));
			buff.append(" group by fportcode,fbs) sb ");
			buff.append(" on sb.FportCode = s.fportcode and sb.fmaxdate = s.fdate");
			buff.append(" where s.FportCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(" and s.FBuyDate =").append(dbl.sqlDate(this.startDate));
			buff.append(" and s.FBs =").append(dbl.sqlString(this.standingBookType));
			buff.append(" group by s.FSecurityCode order by s.FSecurityCode asc ");
			
			rs=dbl.openResultSet(buff.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs.last();
			int securityCount = rs.getRow();
			rs.beforeFirst();
			int secDataCount = 1;
			if(rs.next()){
				secDataCount = rs.getInt("Fcount");
			}
			if(secDataCount * securityCount==0)
			{
				 pageCount = 0;
				 showDataCount = 0;
			}else if(secDataCount * securityCount <=10000)//10000
			{
				pageCount = 1;
				showDataCount = secDataCount;
			}else if(secDataCount<=5000)
			{
				showDataCount = (5000/secDataCount) ;//一页显示的证券数
				if(securityCount%showDataCount==0)
				{
					pageCount = securityCount/showDataCount;
				}else
				{
					pageCount = securityCount/showDataCount + 1;
				}
				
			}else
			{
				pageCount = securityCount;
				showDataCount = secDataCount;
			}
		}catch(Exception e){
			throw new YssException("查询ETF台账数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return pageCount + "," + showDataCount;
	}
	
	/**查询临时台长表
	 * @param sType
	 * @author shashijie ,2011-11-30 , STORY 1789 
	 */
	public String getTempBookDataCount(String type) throws YssException {
		ResultSet rs = null;//声明结果集
		String[] sType = type.split("/t");//解析前台传来的数据
		this.parseRowStr(sType[1]);//用基类的方法解析数据
		
		int pageCount = 0;
		int showDataCount = 0;
		try {
			String sql = " Select FSecurityCode,count(FSecurityCode) as FCount From "+
			  pub.yssGetTableName("Tb_ETF_TempStandingBook")+
		      " where FportCode = "+dbl.sqlString(portCodes)+
		      " and FBuyDate = "+dbl.sqlDate(this.startDate)+
		      " and FBs = "+dbl.sqlString(this.standingBookType)+
		      " group by FSecurityCode ";//排序编号排序;排序编号  = 分级类型相连用”##”连接
			rs = dbl.openResultSet(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs.last();
			int securityCount = rs.getRow();
			rs.beforeFirst();
			int secDataCount = 1;
			if(rs.next()){
				secDataCount = rs.getInt("Fcount");
			}
			if(secDataCount * securityCount==0)
			{
				 pageCount = 0;
				 showDataCount = 0;
			}else if(secDataCount * securityCount <=10000)//10000
			{
				pageCount = 1;
				showDataCount = secDataCount;
			}else if(secDataCount<=5000)
			{
				showDataCount = (5000/secDataCount) ;//一页显示的证券数
				if(securityCount%showDataCount==0)
				{
					pageCount = securityCount/showDataCount;
				}else
				{
					pageCount = securityCount/showDataCount + 1;
				}
				
			}else
			{
				pageCount = securityCount;
				showDataCount = secDataCount;
			}
			return pageCount + "," + showDataCount;
		} catch (Exception e) {
			throw new YssException("获取临时台账表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2013-1-18 STORY 3402 重构代码 */
	private StringBuffer doGetBuffAll(ETFParamSetBean paraSetBean,ArrayList bookList) throws YssException {
		//声明StringBuffer最大长度
		final int INIT_SIZE = bookList.size() < Integer.MAX_VALUE ? bookList.size() : Integer.MAX_VALUE;
		StringBuffer buffAll = new StringBuffer(INIT_SIZE);
		//先拼接基本信息,用\f\f拼接
		String param = getETFParamDealDayNum(" /t \f\f"+this.portCodes+"/t");
		buffAll.append(param);
		buffAll.append("<par>");//分隔符
		
		String sCollect="0";//表示明细项
		String sTotalCode ="";//汇总项代码
		String sMXCode="";//明细项代码
					
		for(int i=0;i<bookList.size();i++){//循环保存台账bean的集合
			StandingBookBean book = (StandingBookBean)bookList.get(i);//获取实例
			
			if(book.getGradeType2()!=null&&book.getGradeType2().trim().length() > 0){//排序编号2
				sCollect = "0";//表示明细项
			}else{
				sCollect = "1";//表示汇总项
			}
			if(paraSetBean.getSBookTotalType().equalsIgnoreCase("stock")){//参数设置中的汇总方式，按股票汇总
				sTotalCode = book.getSecurityCode();//证券代码
				/**shashijie 2013-1-17 STORY 3402 增加非空判断*/
				//股东代码 = 股东代码 + 合同序号
				sMXCode = "    " + book.getStockHolderCode();
				if (book.getTradeNum()==null || book.getTradeNum().trim().equalsIgnoreCase("null")
						|| book.getTradeNum().trim().equals("")) {
					sMXCode += " ";
				}else {
					sMXCode += "-" + book.getTradeNum();
				}
				/**end shashijie 2013-1-17 STORY 3402*/
			}else{
				sTotalCode = book.getStockHolderCode();//股东代码
				sMXCode = "    " + book.getSecurityCode();//证券代码
			}
			
			//把台账数据按照”\t“拼接好传到前台
			/**shashijie 2013-1-17 STORY 3402 增加注视与排版,原先的写法过于凌乱*/
			String one,two,three,four,five,six,seven,eight,nine,ten,eleven;//表示单元格1~11
			
			one = sCollect;
			two = sCollect.equals("1") ? "-" : " ";
			three = sCollect.equals("1") && book.getTradeNum()!=null ? book.getTradeNum() : " " ;//交易编号
			four =  sCollect.equals("1") ? sTotalCode : sMXCode;//汇总方式不同添加股东代码或证券代码
			five = book.getBs().equalsIgnoreCase("B") ? "申购" : "赎回";//申赎
			//申赎
			six = book.getMakeUpAmount() + "";//补票数量
			seven = book.getUnitCost() + "";//单位成本(收盘价)
			eight = book.getExchangeRate()+ "";//汇率
			nine = book.getReplaceCash() + "";//替代金额
			ten = book.getCanReplaceCash() + "";//可退替代款
			setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,null);
			
			//权益
			one = book.getExRightDate()!=null ? book.getExRightDate().toString():" ";//除权日期
			/**add---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
			two = book.getRightRate() + "";//汇率
			three = book.getSumAmount() + "";//总数量
			four = book.getRealAmount() + "";//实际数量
			five = book.getTotalInterest() + "";//总派息
			six = book.getWarrantCost() + "";//权证价值
			/**end---shashijie 2013-4-22 STORY 3402 调整汇率位置*/
			seven = book.getBBInterest() + "";//总派息(本币)
			eight = book.getBBWarrantCost() + "";//权证价值(本币)
			setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,null,null,null);
			
			//第一次补票
			one = book.getMakeUpDate1()!=null?book.getMakeUpDate1().toString():" ";//日期
			two = book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ";//补票类型
			three = book.getMakeUpAmount1() + "";//数量
			four = book.getTradeUnitCost1() + "";//成交单价
			five = book.getFeeUnitCost1() + "";//费用单价
			six = book.getMakeUpUnitCost1()!=0?String.valueOf(book.getMakeUpUnitCost1()):" ";//单位成本
			seven = book.getoMakeUpCost1() + "";//补票总成本(原币)
			/**add---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
			eight = book.getExRate1() + "";//汇率
			nine = book.getMakeUpRepCash1() + "";//应付替代款
			/**end---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
			ten = book.getCanMkUpRepCash1() + "";//可退替代款
			eleven = book.gethMakeUpCost1() + "";//补票总成本(本币)
			setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,eleven);
			
			//第二次补票,shashijie 2013-1-17 STORY 3402 判断补票次数是否在范围内
			if(isSuppleDataValid(paraSetBean.getBeginSupply(),paraSetBean.getDealDayNum(),2)){//补票次数，最多可以补五次
				one = book.getMakeUpDate2()!=null?book.getMakeUpDate2().toString():" ";//日期
				two = book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ";//补票类型
				three = book.getMakeUpAmount2() + "";//数量
				four = book.getTradeUnitCost2() + "";//成交单价
				five = book.getFeeUnitCost2() + "";//费用单价
				//单位成本
				if(paraSetBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//实时加均摊（华夏）
					if(sCollect.equals("1")){
						six = " ";
					}else{
						six = book.getMakeUpUnitCost2() + "";
					}
				}else{
					six = book.getMakeUpUnitCost2() + "";
				}
				seven = book.getoMakeUpCost2() + "";//补票总成本(原币)
				/**add---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
				eight = book.getExRate2() + "";//汇率
				nine = book.getMakeUpRepCash2() + "";//应付替代款
				/**end---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
				ten = book.getCanMkUpRepCash2() + "";//可退替代款
				eleven = book.gethMakeUpCost2() + "";//补票总成本(本币)
				setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,eleven);
			} 
			//第三次补票,国泰有第三次补票,其实存的是第二次补票数据
			if(isSuppleDataValid(paraSetBean.getBeginSupply(),paraSetBean.getDealDayNum(),3)
					|| paraSetBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)//国泰特殊
					){
				one = book.getMakeUpDate3()!=null?book.getMakeUpDate3().toString():" ";//日期
				two = book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ";//补票类型
				three = book.getMakeUpAmount3() + "";//数量
				four = book.getTradeUnitCost3() + "";//成交单价
				five = book.getFeeUnitCost3() + "";//费用单价
				six = book.getMakeUpUnitCost3() + "";//单位成本
				seven = book.getoMakeUpCost3() + "";//补票总成本(原币)
				/**add---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
				eight = book.getExRate3() + "";//汇率
				nine = book.getMakeUpRepCash3() + "";//应付替代款
				/**end---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
				ten = book.getCanMkUpRepCash3() + "";//可退替代款
				eleven = book.gethMakeUpCost3() + "";//补票总成本(本币)
				setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,eleven);
			}//国泰没有数据得拼接空数据上去否则前台显示会错列
			/*else {
				setDatebuffAll(buffAll,"9998-12-31","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0","0.0");
			}*/
			//第四次补票
			if(isSuppleDataValid(paraSetBean.getBeginSupply(),paraSetBean.getDealDayNum(),4)){
				one = book.getMakeUpDate4()!=null?book.getMakeUpDate4().toString():" ";//日期
				two = book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ";//补票类型
				three = book.getMakeUpAmount4() + "";//数量
				four = book.getTradeUnitCost4() + "";//成交单价
				five = book.getFeeUnitCost4() + "";//费用单价
				six = book.getMakeUpUnitCost4() + "";//单位成本
				seven = book.getoMakeUpCost4() + "";//补票总成本(原币)
				eight = book.getMakeUpRepCash4() + "";//应付替代款
				nine = book.getExRate4() + "";//汇率
				ten = book.getCanMkUpRepCash4() + "";//可退替代款
				eleven = book.gethMakeUpCost4() + "";//补票总成本(本币)
				setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,eleven);
			} 
			//第五次补票
			if(isSuppleDataValid(paraSetBean.getBeginSupply(),paraSetBean.getDealDayNum(),5)){
				one = book.getMakeUpDate5()!=null?book.getMakeUpDate5().toString():" ";//日期
				two = book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ";//补票类型
				three = book.getMakeUpAmount5() + "";//数量
				four = book.getTradeUnitCost5() + "";//成交单价
				five = book.getFeeUnitCost5() + "";//费用单价
				six = book.getMakeUpUnitCost5() + "";//单位成本
				seven = book.getoMakeUpCost5() + "";//补票总成本(原币)
				eight = book.getMakeUpRepCash5() + "";//应付替代款
				nine = book.getExRate5() + "";//汇率
				ten = book.getCanMkUpRepCash5() + "";//可退替代款
				eleven = book.gethMakeUpCost5() + "";//补票总成本(本币)
				setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,nine,ten,eleven);
			} 
			
			//强制处理
			one = book.getMustMkUpDate() + "";//日期
			two = book.getMustMkUpAmount() + "";//数量
			three = book.getMustMkUpUnitCost() + "";//单位成本
			four = book.getoMustMkUpCost() + "";//补票总成本(原币)
			/**add---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
			five = book.getMustExRate() + "";//汇率
			six = book.getMustMkUpRepCash() + "";//应付替代款
			/**end---shashijie 2013-4-22 STORY 3402 调整汇率位置 */
			seven = book.getMustCMkUpRepCash() + "";//可退替代款
			eight = book.gethMustMkUpCost() + "";//补票总成本(本币)
			setDatebuffAll(buffAll,one,two,three,four,five,six,seven,eight,null,null,null);
			//剩余数据
			//SubStandingBookBean subBook = new SubStandingBookBean();
			one = book.getRemaindAmount() + "";//剩余数量
			two = book.getSumReturn() + "";//应退合计
			//暂时不考虑显示
			/*three = subBook.getExchangeRate() + "";//T+几日汇率
			four = subBook.getRateProLoss() + "";//T+几日汇兑损益
			five = subBook.getSumRefund() + "";*///T+几日应退合计
			setDatebuffAll(buffAll,one,two,null,null,null,null,null,null,null,null,null);
			//其他数据
			one = book.getBrokerCode()!=null?book.getBrokerCode():" ";//券商代码
			two = book.getRefundDate()!=null?book.getRefundDate().toString():" ";//退款日期
			three = book.getFactExRate()!=0?Double.toString(book.getFactExRate()):" ";//实际汇率
			four = book.getExRateDate()!=null?book.getExRateDate().toString():" ";//换汇日期
			five = book.getSumReturn()!=0?Double.toString(book.getSumReturn()):" ";//实际应退合计
			setDatebuffAll(buffAll,one,two,three,four,five,null,null,null,null,null,null);
			buffAll.append("\r\n");
		}
		if(buffAll.toString().endsWith("\r\n")){//每一条数据之间用”\n“拼接，去掉最后一个”\n“
			buffAll.delete(buffAll.length()-1,buffAll.length());
		}
		return buffAll;
	}

	/**shashijie 2013-1-18 STORY 3402 重构代码*/
	private ArrayList doSetBookList(ETFParamSetBean paraSetBean) throws YssException {
		ArrayList bookList = new ArrayList();
		ResultSet rs = null;
		//定义StringBuffer最大长度
		final int INIT_SIZE = bookList.size() < Integer.MAX_VALUE ? bookList.size() : Integer.MAX_VALUE;
		StringBuffer buff = new StringBuffer(INIT_SIZE);
		try {
			//modified by yeshenghong ETF台帐报错  20130418
			buff.append(" select * from ( ");
			//---end modified by yeshenghong ETF台帐报错  20130418
			//台账SQL
			setBuffSupplyDate(buff);
			
			//国泰的赎回台账要求显示必须现金替代的数据
			if (this.standingBookType!=null && this.standingBookType.trim().equals("S")
					&& paraSetBean.getSupplyMode().equals(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)
					){
				//必须现金替代
				setBuffMastDate(buff);
			}
			//modified by yeshenghong ETF台帐报错  20130418
			buff.append(") order by FOrderCode,fmarktype");
			//---end modified by yeshenghong ETF台帐报错  20130418
			rs=dbl.openResultSet(buff.toString());
			
			while(rs.next()){
				//封装台账数据
				setBookList(bookList,paraSetBean.getSupplyMode(),rs);
			}
			return bookList;
		} catch (Exception e) {
			throw new YssException("封装台账数据出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2013-1-18 STORY 3402 重构代码 */
	private void setBookList(ArrayList bookList,String supplyMode,ResultSet rs) throws Exception {
		if (bookList==null) {
			return;
		}
		StandingBookBean book = new StandingBookBean();//实例化台账实体bean
		/**shashijie 2012-01-12 STORY 1789 华夏要求赎回台账显示为正数  */
		if (rs.getString("FBs").trim().equals("S") && 
				(supplyMode.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)//实时加均摊（华夏）
				||supplyMode.equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ))//国泰
				) {
			this.setStandingBookData2(rs,book,-1);//往台账设置bean中赋值
		} else {
			this.setStandingBookData(rs,book);//往台账设置bean中赋值
		}
		/**end*/
		bookList.add(book);//把保存数据的台账bean放到集合中
	}

	/**shashijie 2013-1-18 STORY 3402 重构代码*/
	private void setBuffSupplyDate(StringBuffer buff) {
		String secStr = "";
		String sSql="";
		ResultSet rs=null;//结果集
		if(this.securityCodes.trim().length()>0){//有证券按证券查询  不分页
		buff.append(" select s.*,sb.fmaxdate from (select * from ");
		buff.append(pub.yssGetTableName("Tb_ETF_StandingBook"));//台帐表
		buff.append(" order by FOrderCode, fmarktype)");
		buff.append(" s join (select max(fdate) as fmaxdate,fportcode from ");
		buff.append(pub.yssGetTableName("Tb_ETF_StandingBook"));
		buff.append(" where fdate <= ").append(dbl.sqlDate(this.browDate));
		buff.append(" and FportCode = ").append(dbl.sqlString(this.portCodes));
		buff.append(" and FBuyDate =").append(dbl.sqlDate(this.startDate));
		buff.append(" and FBs =").append(dbl.sqlString(this.standingBookType));
		buff.append(" group by fportcode,fbs) sb ");
		buff.append(" on sb.FportCode = s.fportcode and sb.fmaxdate = s.fdate");
		buff.append(" where s.FportCode = ").append(dbl.sqlString(this.portCodes));
		buff.append(" and s.FBuyDate =").append(dbl.sqlDate(this.startDate));
		buff.append(" and s.FBs =").append(dbl.sqlString(this.standingBookType));
		buff.append(this.securityCodes.trim().length()>0?
				" and s.FSecurityCode in (" + operSql.sqlCodes(this.securityCodes) + ")":"");
		}else
		{
			if(this.dataPageCount>1){
				sSql = " select * from (select s.* , rownum as rno from (select distinct fsecuritycode," +
					   " substr(fordercode, 0, instr(fordercode, '##') - 1) as FOderIndex from " +
					   pub.yssGetTableName("Tb_ETF_StandingBook") +
					   " order by FOderIndex) s) where rno between " + ((this.pageIndex - 1) * this.onePageSecs + 1) +
					   " and " + this.pageIndex * this.onePageSecs;
				try {//分页显示用, add by yeshenghong 20120530 
					rs = dbl.openResultSet(sSql);
					while(rs.next())
					{
						secStr += dbl.sqlString(rs.getString("fsecuritycode")) + ",";
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (YssException e) {
					e.printStackTrace();
				}
			}
			if(secStr.length()>2)
			{
				secStr = secStr.substring(0, secStr.length()-1);
				secStr = "(" + secStr + ")";
			}
			buff.append(" select s.*,sb.fmaxdate from ").append(pub.yssGetTableName("Tb_ETF_StandingBook"));//台帐表
			buff.append(" s join (select max(fdate) as fmaxdate,fportcode from ");
			buff.append(pub.yssGetTableName("Tb_ETF_StandingBook"));
			buff.append(" where fdate <= ").append(dbl.sqlDate(this.browDate));
			buff.append(" and FportCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(" and FBuyDate =").append(dbl.sqlDate(this.startDate));
			buff.append(" and FBs =").append(dbl.sqlString(this.standingBookType));
			buff.append(" group by fportcode,fbs) sb ");
			buff.append(" on sb.FportCode = s.fportcode and sb.fmaxdate = s.fdate");
			buff.append(" where s.FportCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(" and s.FBuyDate =").append(dbl.sqlDate(this.startDate));
			buff.append(" and s.FBs =").append(dbl.sqlString(this.standingBookType));
			buff.append(secStr.trim().length()>0?" and s.FSecurityCode in " + secStr :"");
			
		}
	}

	/**shashijie 2013-1-18 STORY 3402 重构代码*/
	private void setBuffMastDate(StringBuffer buff) {
		buff.append(" Union" +
				" Select a.Fdate, "+dbl.sqlString(this.standingBookType)+" As Fbs, a.Fportcode, a.Fsecuritycode," +
				" a.Fstockholdercode, ' ' As Fbrokercode, ' ' As Fseatcode, 0 As Fmakeupamount, 0 As Funitcost," +
				" a.Fsumreturn As Ftotalmoney, 0 As Fcanreplacecash, To_Date('1900-01-01', 'yyyy-MM-dd') As Fexrightdate," +
				" 0 As Fsumamount, 0 As Frealamount, 0 As Ftotalinterest, 0 As Fwarrantcost, " +
				" To_Date('1900-01-01', 'yyyy-MM-dd') As Fmakeupdate1, 0 As Fmakeupamount1, 0 As Fmakeupunitcost1," +
				" 0 As Fmakeuprepcash1, 0 As Fcanmkuprepcash1, To_Date('1900-01-01', 'yyyy-MM-dd') As Fmakeupdate2," +
				" 0 As Fmakeupamount2, 0 As Fmakeupunitcost2, 0 As Fmakeuprepcash2, 0 As Fcanmkuprepcash2," +
				" To_Date('1900-01-01', 'yyyy-MM-dd') As Fmakeupdate3, 0 As Fmakeupamount3,  0 As Fmakeupunitcost3," +
				" 0 As Fmakeuprepcash3, 0 As Fcanmkuprepcash3, To_Date('1900-01-01', 'yyyy-MM-dd') As Fmakeupdate4," +
				" 0 As Fmakeupamount4, 0 As Fmakeupunitcost4, 0 As Fmakeuprepcash4, 0 As Fcanmkuprepcash4," +
				" To_Date('1900-01-01', 'yyyy-MM-dd') As Fmakeupdate5, 0 As Fmakeupamount5, 0 As Fmakeupunitcost5," +
				" 0 As Fmakeuprepcash5, 0 As Fcanmkuprepcash5, To_Date('1900-01-01', 'yyyy-MM-dd') As Fmustmkupdate," +
				" 0 As Fmustmkupamount, 0 As Fmustmkupunitcost, 0 As Fmustmkuprepcash, 0 As Fmustcmkuprepcash," +
				" 0 As Fremaindamount, a.Fsumreturn, a.Frefunddate As Frefunddate, ' ' As Fcreator, ' ' As Fcreatetime," +
				" ' ' As Fnum, 0 As Fexchangerate, 0 As Fomakeupcost1, 0 As Fhmakeupcost1, 0 As Fomakeupcost2," +
				" 0 As Fhmakeupcost2, 0 As Fomakeupcost3, 0 As Fhmakeupcost3, 0 As Fomakeupcost4," +
				" 0 As Fhmakeupcost4, 0 As Fomakeupcost5, 0 As Fhmakeupcost5, 0 As Fomustmkupcost, 0 As Fhmustmkupcost," +
				" ' ' As Fordercode, a.Fsecuritycode As Fgradetype1, a.Fstockholdercode As Fgradetype2, '' As Fgradetype3," +
				" 0 As Fexrate1, 0 As Fexrate2, 0 As Fexrate3, 0 As Fexrate4, 0 As Fexrate5, 0 As Fmustexrate," +
				" 0 As Ffactexrate, To_Date('1900-01-01', 'yyyy-MM-dd') As Fexratedate, 0 As Ffactamount," +
				" 0 As Fcashbal, 0 As Fbbinterest, 0 As Fbbwarrantcost, 0 As Frightrate, ' ' As Fratetype, " +
				" ' ' As Ftradenum, ' ' As Fmarktype, 0 As Ftradeunitcost1, 0 As Ffeeunitcost1, 0 As Ftradeunitcost2, " +
				" 0 As Ffeeunitcost2, 0 As Ftradeunitcost3, 0 As Ffeeunitcost3, 0 As Ftradeunitcost4," +
				" 0 As Ffeeunitcost4, 0 As Ftradeunitcost5, 0 As Ffeeunitcost5, 0 As Fmusttradeunitcost," +
				" 0 As Fmustfeeunitcost, 0 As Fdeflationamount, To_Date('1900-01-01', 'yyyy-MM-dd') As Fdate," +
				" To_Date('1900-01-01', 'yyyy-MM-dd') As Fmaxdate From (/*汇总必须现金替代*/ Select A1.Fdate," +
				" A1.Fportcode, A1.Fsecuritycode, A1.Ftotalmoney * b.Amount / p.Fnormscale As Fsumreturn," +
				" c.Frefunddate, ' ' As Fstockholdercode From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" A1 "+
				" /**结算明细获取申赎数量*/ Join (Select a.Fportcode, a.Fbargaindate, Sum(a.Ftradeamount) As Amount," +
				" a.Ftradetypecode From "+pub.yssGetTableName("Tb_Etf_Jsmxinterface")+" a" +
				" Where a.Frecordtype = '003'" +
				" And a.Fbargaindate = "+dbl.sqlDate(this.startDate)+
				" And a.Ftradetypecode = '103'" +//赎回
				" Group By a.Fportcode, a.Fbargaindate, a.Ftradetypecode) b On b.Fbargaindate = A1.Fdate" +
				" /**ETF基础参数*/ Join (Select p.Fportcode, p.Fnormscale From "+
				pub.yssGetTableName("Tb_Etf_Param")+" p) p " +
				" On A1.Fportcode = p.Fportcode /**台账获取退款日期*/ " +
				" Join (Select Min(m.Frefunddate) As Frefunddate, m.Fbuydate, m.Fbs" +
				" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" m" +
				" Where m.Fbuydate = "+dbl.sqlDate(this.startDate)+
				" And m.Fbs = "+dbl.sqlString(this.standingBookType)+
				" And m.Fsecuritycode != ' '" +
				" Group By m.Fbuydate, m.Fbs) c On c.Fbuydate = A1.Fdate" +
				" Where A1.Fdate = "+dbl.sqlDate(this.startDate)+
				" And A1.Freplacemark = '6'" +
				" And A1.Fportcode = "+dbl.sqlString(this.portCodes)+
				" And b.Ftradetypecode = '103'"+
				" Union /*明细投资者必须现金替代*/ Select A1.Fdate, A1.Fportcode, A1.Fsecuritycode," +
				" A1.Ftotalmoney * b.Amount / p.Fnormscale As Fsumreturn, c.Frefunddate, b.Fstockholdercode" +
				" From "+pub.yssGetTableName("Tb_Etf_Stocklist")+" A1 Join ( /**结算明细获取申赎数量*/" +
				" Select a.Fportcode, a.Fbargaindate, a.Fstockholdercode, a.Ftradetypecode, Sum(a.Ftradeamount) As Amount" +
				" From "+pub.yssGetTableName("Tb_Etf_Jsmxinterface")+" a Where a.Frecordtype = '003'" +
				" And a.Fbargaindate = "+dbl.sqlDate(this.startDate)+
                " And a.Ftradetypecode = '103' Group By a.Fportcode, a.Fbargaindate, a.Fstockholdercode," +
                " a.Ftradetypecode) b On b.Fbargaindate = A1.Fdate /**ETF基础参数*/" +
                " Join (Select p.Fportcode, p.Fnormscale From " +
                pub.yssGetTableName("Tb_Etf_Param")+" p) p On A1.Fportcode = p.Fportcode " +
        		" /**台账获取退款日期*/ Join (Select Min(m.Frefunddate) As Frefunddate, m.Fbuydate, m.Fbs" +
        		" From "+pub.yssGetTableName("Tb_Etf_Standingbook")+" m" +
        		" Where m.Fbuydate = "+dbl.sqlDate(this.startDate)+
        		" And m.Fbs = "+dbl.sqlString(this.standingBookType)+
                " And m.Fsecuritycode != ' ' Group By m.Fbuydate, m.Fbs) c On c.Fbuydate = A1.Fdate" +
                " Where A1.Fdate = "+dbl.sqlDate(this.startDate)+
                " And A1.Freplacemark = '6' " +
                " And A1.Fportcode = "+dbl.sqlString(this.portCodes)+
                " And b.Ftradetypecode = '103') a");
	}
	
	/**shashijie 2013-1-17 STORY 3402 判断补票次数是否在范围内,在范围内返回true */
	private boolean isSuppleDataValid(int bengin,int complete,int number) {
		boolean falg = false;
		try {
			//补票次数 = 补票完成日 - 补票起始日 + 1
			double supplyCount = YssD.add(YssD.sub(complete, bengin),1);
			if (supplyCount>=number) {
				falg = true;
			}
		} catch (Exception e) {
			return falg;
		} finally {

		}
		return falg;
	}
	
	/**shashijie 2013-1-17 STORY 3402 并接前台台账显示数据*/
	private void setDatebuffAll(StringBuffer buffAll, String one, String two,
			String three, String four, String five, String six, String seven,
			String eight, String nine, String ten,String eleven) {
		if (one != null) {
			buffAll.append(one).append("\t");
		}
		if (two != null) {
			buffAll.append(two).append("\t");
		}
		if (three != null) {
			buffAll.append(three).append("\t");
		}
		if (four != null) {
			buffAll.append(four).append("\t");
		}
		if (five != null) {
			buffAll.append(five).append("\t");
		}
		if (six != null) {
			buffAll.append(six).append("\t");
		}
		if (seven != null) {
			buffAll.append(seven).append("\t");
		}
		if (eight != null) {
			buffAll.append(eight).append("\t");
		}
		if (nine != null) {
			buffAll.append(nine).append("\t");
		}
		if (ten != null) {
			buffAll.append(ten).append("\t");
		}
		if (eleven != null) {
			buffAll.append(eleven).append("\t");
		}
	}
	
	/**往台账设置bean中赋值*/
	private void setStandingBookData(ResultSet rs,StandingBookBean book) throws YssException{
		
		try{
			book.setNum(rs.getString("FNum"));//申请编号
			book.setBuyDate(rs.getDate("FBuyDate"));//申赎日期
			book.setBs(rs.getString("FBs"));//台账类型
			book.setPortCode(rs.getString("FPortCode"));//组合代码
			book.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
			book.setStockHolderCode(rs.getString("FStockHolderCode")==null?"":rs.getString("FStockHolderCode"));//投资者
			book.setBrokerCode(rs.getString("FBrokerCode"));//券商
			book.setSeatCode(rs.getString("FSeatCode"));//交易席位
			book.setMakeUpAmount(rs.getDouble("FMakeUpAmount"));//申赎数量
			book.setUnitCost(rs.getDouble("FUnitCost"));//单位成本
			book.setReplaceCash(rs.getDouble("FReplaceCash"));//替代金额
			book.setCanReplaceCash(rs.getDouble("FCanReplaceCash"));//可退替代款
			book.setExRightDate(rs.getDate("FExRightDate"));//权益日期
			book.setSumAmount(rs.getDouble("FSumAmount"));//权益总数量
			book.setRealAmount(rs.getDouble("FRealAmount"));//权益实际数量
			book.setTotalInterest(rs.getDouble("FTotalInterest"));//总派息原币
			book.setWarrantCost(rs.getDouble("FWarrantCost"));//权证价值原币
			book.setBBInterest(rs.getDouble("FBBInterest"));//总派息本币
			book.setBBWarrantCost(rs.getDouble("FBBWarrantCost"));//权证价值本币
			book.setRightRate(rs.getDouble("FRightRate"));//权益汇率
			book.setMakeUpDate1(rs.getDate("FMakeUpDate1"));//第一次补票日期
			book.setMakeUpAmount1(rs.getDouble("FMakeUpAmount1"));//第一次补票数量
			book.setTradeUnitCost1(rs.getDouble("FTradeUnitCost1"));//第一次补票的成交单价
			book.setFeeUnitCost1(rs.getDouble("FFeeUnitCost1"));//第一次补票的费用单价
			book.setMakeUpUnitCost1(rs.getDouble("FMakeUpUnitCost1"));//第一次补票单位成本
			book.setoMakeUpCost1(rs.getDouble("FOMakeUpCost1"));//第一次补票总成本原币
			book.sethMakeUpCost1(rs.getDouble("FHMakeUpCost1"));//第一次补票总成本本币
			book.setMakeUpRepCash1(rs.getDouble("FMakeUpRepCash1"));//第一次补票应付替代款
			book.setCanMkUpRepCash1(rs.getDouble("FCanMkUpRepCash1"));//第一次补票可退替代款
			
			book.setMakeUpDate2(rs.getDate("FMakeUpDate2"));
			book.setMakeUpAmount2(rs.getDouble("FMakeUpAmount2"));
			book.setMakeUpUnitCost2(rs.getDouble("FMakeUpUnitCost2"));
			book.setTradeUnitCost2(rs.getDouble("FTradeUnitCost2"));
			book.setFeeUnitCost2(rs.getDouble("FFeeUnitCost2"));
			book.setoMakeUpCost2(rs.getDouble("FOMakeUpCost2"));
			book.sethMakeUpCost2(rs.getDouble("FHMakeUpCost2"));
			book.setMakeUpRepCash2(rs.getDouble("FMakeUpRepCash2"));
			book.setCanMkUpRepCash2(rs.getDouble("FCanMkUpRepCash2"));
			
			book.setMakeUpDate3(rs.getDate("FMakeUpDate3"));
			book.setMakeUpAmount3(rs.getDouble("FMakeUpAmount3"));
			book.setMakeUpUnitCost3(rs.getDouble("FMakeUpUnitCost3"));
			book.setTradeUnitCost3(rs.getDouble("FTradeUnitCost3"));
			book.setFeeUnitCost3(rs.getDouble("FFeeUnitCost3"));
			book.setoMakeUpCost3(rs.getDouble("FOMakeUpCost3"));
			book.sethMakeUpCost3(rs.getDouble("FHMakeUpCost3"));
			book.setMakeUpRepCash3(rs.getDouble("FMakeUpRepCash3"));
			book.setCanMkUpRepCash3(rs.getDouble("FCanMkUpRepCash3"));
			
			book.setMakeUpDate4(rs.getDate("FMakeUpDate4"));
			book.setMakeUpAmount4(rs.getDouble("FMakeUpAmount4"));
			book.setMakeUpUnitCost4(rs.getDouble("FMakeUpUnitCost4"));
			book.setTradeUnitCost4(rs.getDouble("FTradeUnitCost4"));
			book.setFeeUnitCost4(rs.getDouble("FFeeUnitCost4"));
			book.setoMakeUpCost4(rs.getDouble("FOMakeUpCost4"));
			book.sethMakeUpCost4(rs.getDouble("FHMakeUpCost4"));
			book.setMakeUpRepCash4(rs.getDouble("FMakeUpRepCash4"));
			book.setCanMkUpRepCash4(rs.getDouble("FCanMkUpRepCash4"));
			
			book.setMakeUpDate5(rs.getDate("FMakeUpDate5"));
			book.setMakeUpAmount5(rs.getDouble("FMakeUpAmount5"));
			book.setMakeUpUnitCost5(rs.getDouble("FMakeUpUnitCost5"));
			book.setTradeUnitCost5(rs.getDouble("FTradeUnitCost5"));
			book.setFeeUnitCost5(rs.getDouble("FFeeUnitCost5"));
			book.setoMakeUpCost5(rs.getDouble("FOMakeUpCost5"));
			book.sethMakeUpCost5(rs.getDouble("FHMakeUpCost5"));
			book.setMakeUpRepCash5(rs.getDouble("FMakeUpRepCash5"));
			book.setCanMkUpRepCash5(rs.getDouble("FCanMkUpRepCash5"));
			
			book.setMustMkUpDate(rs.getDate("FMustMkUpDate"));//强制处理日期
			book.setMustMkUpAmount(rs.getDouble("FMustMkUpAmount"));//强制处理数量
			book.setMustMkUpUnitCost(rs.getDouble("FMustMkUpUnitCost"));//强制处理单位成本
			book.setoMustMkUpCost(rs.getDouble("FOMustMkUpCost"));//强制处理总成本原币
			book.sethMustMkUpCost(rs.getDouble("FHMustMkUpCost"));//强制处理总成本本币
			book.setMustMkUpRepCash(rs.getDouble("FMustMkUpRepCash"));//强制处理应付替代款
			book.setMustCMkUpRepCash(rs.getDouble("FMustCMkUpRepCash"));//强制处理可退替代款
			
			book.setRemaindAmount(rs.getDouble("FRemaindAmount"));//剩余数量
			book.setSumReturn(rs.getDouble("FSumReturn"));//应退合计
			book.setRefundDate(rs.getDate("FRefundDate"));//退款日期
			book.setExchangeRate(rs.getDouble("FExchangeRate"));//换汇汇率
			book.setOrderCode(rs.getString("FOrderCode"));//排序编号
			book.setGradeType1(rs.getString("FGradeType1"));//分级类型1
			book.setGradeType2(rs.getString("FGradeType2"));//分级类型2
			book.setGradeType3(rs.getString("FGradeType3"));//分机类型3
			
			book.setExRate1(rs.getDouble("FExRate1"));//第一次补票汇率
			book.setExRate2(rs.getDouble("FExRate2"));//第二次补票汇率
			book.setExRate3(rs.getDouble("FExRate3"));//第三次补票汇率
			book.setExRate4(rs.getDouble("FExRate4"));//第四次补票汇率
			book.setExRate5(rs.getDouble("FExRate5"));//第五次补票汇率
			
			book.setMustExRate(rs.getDouble("FMustExRate"));//强制处理汇率
			book.setFactExRate(rs.getDouble("FFactExRate"));//实际汇率
			book.setExRateDate(rs.getDate("FExRateDate"));//换汇日期
			book.setTradeNum(rs.getString("FTradeNum"));//成交编号
			book.setRateType(rs.getString("FRateType")!=null?rs.getString("FRateType"):" ");//汇率类型t+1或者t+4
			book.setMarkType(rs.getString("FMarkType"));//标志类型 time =实时补票 ,difference = 钆差补票

		}catch (Exception e) {
			throw new YssException("往台账设置bean中赋值出错！",e);
		}
	}
	
	/**shashijie 2013-1-18 STORY 3402 重构代码,获取台账子表中每笔台账数据的应退估值增值的条数*/
	public int getSubStandingBookDataCount(String type) throws YssException{
		int iMaxCount = 0;//应退估值增值的条数
		
		/*StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		String [] sType = null;//保存前台出来的参数
		try{
			sType = type.split("/t");//解析前台传来的数据
			this.parseRowStr(sType[1]);//用基类的方法解析数据
			buff = new StringBuffer(1000);
			buff.append(" select max(count(*)) as FCount from ").append(pub.yssGetTableName("tb_etf_substandingbook"));//台账子表
			buff.append(" sub join(select fbuydate,fbs,fportcode,case when fsecuritycode is null then ' ' else fsecuritycode end as fsecuritycode, ");
			buff.append(" case when fstockholdercode is null then ' ' else fstockholdercode end as fstockholdercode,");
			buff.append(" case when ftradenum is null then ' ' else ftradenum end as ftradenum,");
			buff.append(" case when fratetype is null then ' ' else fratetype end as fratetype,");
			buff.append(" case when fmarktype is null then ' ' else fmarktype end as fmarktype");
			buff.append(" from ").append(pub.yssGetTableName("tb_etf_standingbook"));//台帐表
			buff.append(" where FBuyDate = ").append(dbl.sqlDate(this.startDate));
			buff.append(" and FBS = ").append(dbl.sqlString(this.standingBookType));
			buff.append(" and FPortCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(this.securityCodes.trim().length()>0?" and FSecurityCode =" + dbl.sqlString(this.securityCodes):"");
			buff.append(" ) book on book.fbuydate = sub.fbuydate and sub.fbs = book.FBs and sub.fportcode = book.fportcode");
			buff.append(" and sub.fsecuritycode = book.fsecuritycode and sub.fstockholdercode = book.fstockholdercode and sub.ftradenum = book.ftradenum and sub.fratetype = book.fratetype and sub.fmarktype = book.fmarktype");
			buff.append(" where sub.FportCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(" group by sub.fbuydate,sub.fbs,sub.fportcode,sub.fsecuritycode,sub.fstockholdercode,sub.ftradenum,sub.fratetype,sub.fmarktype");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				iMaxCount = rs.getInt("FCount");
			}
			
		}catch (Exception e) {
			throw new YssException("获取台账子表中每笔台账数据的应退估值增值的条数出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}*/
		return iMaxCount;
	}
	
	/**shashijie 2013-1-18 STORY 3402 重构代码,针对变态公司获取台账子表中每笔台账数据的应退估值增值的条数*/
	public int getBoshiSubStandingBookDataCount(String type) throws YssException{
		int iMaxCount = 0;//应退估值增值的条数
		/*StringBuffer buff = null;//做拼接SQL语句
		ResultSet rs = null;//声明结果集
		String [] sType = null;//保存前台出来的参数
		BaseOperDeal operdeal =null;
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
		try{
			operdeal = new BaseOperDeal();
			operdeal.setYssPub(pub);
			sType = type.split("/t");//解析前台传来的数据
			this.parseRowStr(sType[1]);//用基类的方法解析数据
			
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			paramSet = (ETFParamSetBean) etfParam.get(portCodes);
			
			buff = new StringBuffer(1000);
			buff.append(" select max(fexratedate)as maxdate,min(fexratedate) as mindate from ").append(pub.yssGetTableName("tb_etf_substandingbook"));//台账子表
			buff.append(" where FBuyDate = ").append(dbl.sqlDate(this.startDate));
			buff.append(" and FBS = ").append(dbl.sqlString(this.standingBookType));
			buff.append(" and FPortCode = ").append(dbl.sqlString(this.portCodes));
			buff.append(this.securityCodes.trim().length()>0?" and FSecurityCode =" + dbl.sqlString(this.securityCodes):"");
			
			rs = dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				if(rs.getDate("mindate")!=null&&rs.getDate("maxdate")!=null){
					iMaxCount=operdeal.workDateDiff(rs.getDate("mindate"),rs.getDate("maxdate"),paramSet.getSHolidayCode(),0);
				}				
			}
			
		}catch (Exception e) {
			throw new YssException("获取台账子表中每笔台账数据的应退估值增值的条数出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}*/
		return iMaxCount;
	}
	
	/**shashijie 2013-1-18 STORY 3402 重构代码,查询临时台长表 */
	public String getTempETFBookData(String type) throws YssException {
		StringBuffer buffAll = null;//做拼接台账数据
		ResultSet rs = null;//声明结果集
		ArrayList bookList = new ArrayList();//临时台账数据集合
		
		String[] sType = type.split("/t");//解析前台传来的数据
		this.parseRowStr(sType[1]);//用基类的方法解析数据
		
		ETFParamSetBean paraSetBean = getETFParamSetBean(this.portCodes);//获取参数设置实体bean
		
		try {
			String sql = getTempEtfBookSql();
			rs = dbl.openResultSet(sql);
			while (rs.next()) {
				TempBook book = new TempBook();
				
				/**shashijie 2012-01-12 STORY 1789 华夏要求赎回台账显示为正数  */
				if (rs.getString("FBs").trim().equals("S") && (
						paraSetBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)//实时加均摊（华夏）
						|| paraSetBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ))//国泰
				) {
					setTempBook2(book,rs);//往台账设置bean中赋值
				} else {
					setTempBook(book,rs);//设置对象赋值
				}
				/**end*/
				
				bookList.add(book);
			}
			//获取拼接台账数据
			buffAll = doGetBuffAll(paraSetBean, bookList);
		} catch (Exception e) {
			throw new YssException("获取临时台账表数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return buffAll.toString();
	}
	
	/**获取拼接台账数据
	 * @param bookList
	 * @author shashijie ,2011-11-30 , STORY 1789
	 */
	protected String getBuffAllValue(List bookList,ETFParamSetBean paraSetBean) throws YssException {
		String sTotalCode ="";//汇总项代码
		String sMXCode="";//明细项代码
		String sCollect="0";//表示明细项
		StringBuffer buffAll = new StringBuffer(bookList.size() * 100);//前台显示数据
		
		for(int i=0;i<bookList.size();i++){//循环保存台账bean的集合
			TempBook book= (TempBook) bookList.get(i);//获取实例
			
			//股东代码
			if (book.getFStockHolderCode() == null){
				book.setFStockHolderCode("");
			}
			
			if(book.getFGradeType2() != null && book.getFGradeType2().trim().length() > 0){//排序编号2
				sCollect = "0";//表示明细项
			}else{
				sCollect = "1";//表示汇总项
			}
			
			if(paraSetBean.getSBookTotalType().equalsIgnoreCase("stock")){//参数设置中的汇总方式，按股票汇总
				sTotalCode = book.getFSecurityCode();//证券代码
				sMXCode = book.getFStockHolderCode() + "-" + book.getFGradeType3();//股东代码 = 股东代码 + 合同序号
			}else{
				sTotalCode = book.getFStockHolderCode();//股东代码
				sMXCode = book.getFSecurityCode();//证券代码
			}
			
			//把台账数据按照”\t“拼接好传到前台
			buffAll.append(sCollect).append("\t").append((sCollect.equals("1")?"-":" ")).append("\t")
					.append((sCollect.equals("1")?book.getFNum():" ")).append("\t")
					.append((sCollect.equals("1")?sTotalCode:"    " + sMXCode)).append("\t")
					.append((book.getFBs().equalsIgnoreCase("B")?"申购":"赎回")).append("\t").append(book.getFMakeUpAmount()).append("\t")
					.append(book.getFUnitCost()).append("\t")
					.append(book.getFExchangeRate()).append("\t").append(book.getFReplaceCash()).append("\t").append(book.getFCanReplaceCash()).append("\t")
				
					//---------------------------------权益------------------------------------
					.append(" \t \t \t \t \t \t \t \t")
					//---------------------------------权益end--------------------------------
				
				//-------------------------------第一次补票-----------------------------//
				.append((book.getFMakeUpDate1()!=null ? book.getFMakeUpDate1().toString():" ")).append("\t")
				.append(" \t")
				.append(book.getFMakeUpAmount1()).append("\t").append(book.getFTradeUnitCost1()).append("\t").append(book.getFFeeUnitCost1()).append("\t")
				.append((book.getFMakeUpUnitCost1()!=0?String.valueOf(book.getFMakeUpUnitCost1()):" ")).append("\t")
				.append(book.getFOMakeUpCost1()).append("\t").append(book.getFMakeUpRepCash1()).append("\t")
				.append(book.getFExRate1()).append("\t").append(book.getFCanMkUpRepCash1()).append("\t")
				.append(book.getFHMakeUpCost1()).append("\t");
				//-------------------------------第一次补票end-----------------------------//
				
			//第二次补票
			if(paraSetBean.getDealDayNum() - paraSetBean.getBeginSupply() + 1 >1){
				buffAll.append(" \t \t \t \t \t \t \t \t \t \t \t");
			}
			/*if(paraSetBean.getDealDayNum() - paraSetBean.getBeginSupply() + 1 >1){//补票次数，最多可以补五次
				buffAll.append(book.getMakeUpDate2()!=null?book.getMakeUpDate2().toString():" ").append("\t").append(book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ").append("\t");
				buffAll.append(book.getMakeUpAmount2()).append("\t").append(book.getTradeUnitCost2()).append("\t");
				buffAll.append(book.getFeeUnitCost2()).append("\t");
				if(paraSetBean.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//实时加均摊（华夏）
					if(sCollect.equals("1")){
						buffAll.append(" ").append("\t");
					}else{
						buffAll.append(book.getMakeUpUnitCost2()).append("\t");
					}
				}else{
					buffAll.append(book.getMakeUpUnitCost2()).append("\t");
				}
				
				buffAll.append(book.getoMakeUpCost2()).append("\t").append(book.getMakeUpRepCash2()).append("\t");
				buffAll.append(book.getExRate2()).append("\t").append(book.getCanMkUpRepCash2()).append("\t");
				buffAll.append(book.gethMakeUpCost2()).append("\t");
			}//设置几个交易日内补票完成,来显示补票栏的个数
			if(paraSetBean.getDealDayNum() - paraSetBean.getBeginSupply() + 1>2){//第三次补票
				buffAll.append(book.getMakeUpDate3()!=null?book.getMakeUpDate3().toString():" ").append("\t").append(book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ").append("\t");
				buffAll.append(book.getMakeUpAmount3()).append("\t").append(book.getTradeUnitCost3()).append("\t");
				buffAll.append(book.getFeeUnitCost3()).append("\t").append(book.getMakeUpUnitCost3()).append("\t");
				buffAll.append(book.getoMakeUpCost3()).append("\t").append(book.getMakeUpRepCash3()).append("\t");
				buffAll.append(book.getExRate3()).append("\t").append(book.getCanMkUpRepCash3()).append("\t");
				buffAll.append(book.gethMakeUpCost3()).append("\t");
			}
			if(paraSetBean.getDealDayNum() - paraSetBean.getBeginSupply() + 1 > 3){//第四次补票
				buffAll.append(book.getMakeUpDate4()!=null?book.getMakeUpDate4().toString():" ").append("\t").append(book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ").append("\t");
				buffAll.append(book.getMakeUpAmount4()).append("\t").append(book.getTradeUnitCost4()).append("\t");
				buffAll.append(book.getFeeUnitCost4()).append("\t").append(book.getMakeUpUnitCost4()).append("\t");
				buffAll.append(book.getoMakeUpCost4()).append("\t").append(book.getMakeUpRepCash4()).append("\t");
				buffAll.append(book.getExRate4()).append("\t").append(book.getCanMkUpRepCash4()).append("\t");
				buffAll.append(book.gethMakeUpCost4()).append("\t");
			}
			if(paraSetBean.getDealDayNum() - paraSetBean.getBeginSupply() + 1 > 4){//第五次
				buffAll.append(book.getMakeUpDate5()!=null?book.getMakeUpDate5().toString():" ").append("\t").append(book.getMarkType()!= null?(book.getMarkType().equalsIgnoreCase("time")?"实时":"钆差"):" ").append("\t");
				buffAll.append(book.getMakeUpAmount5()).append("\t").append(book.getTradeUnitCost5()).append("\t");
				buffAll.append(book.getFeeUnitCost5()).append("\t").append(book.getMakeUpUnitCost5()).append("\t");
				buffAll.append(book.getoMakeUpCost5()).append("\t").append(book.getMakeUpRepCash5()).append("\t");
				buffAll.append(book.getExRate5()).append("\t").append(book.getCanMkUpRepCash5()).append("\t");
				buffAll.append(book.gethMakeUpCost5()).append("\t");
			}*/
			//强制处理
			buffAll.append(" \t \t \t \t \t \t \t \t")
			
			/*System.out.println(book.getSecurityCode()+"~~~"+book.getBuyDate()+"~~~"+book.getRemaindAmount()+
					"~~~"+book.getSumReturn());*/
			
			.append(book.getFRemaindAmount()).append("\t");//剩余数量
			if(book.getFSumReturn()!=0){  //应退合计 modified by zhaoxianlin 20121025 避免前台应退合计显示为科学计数法形式
				BigDecimal bd = new BigDecimal(book.getFSumReturn());
				buffAll.append(bd.setScale(2,BigDecimal.ROUND_HALF_UP).toString()).append("\t");
			}else {
				buffAll.append(" ").append("\t");
			}
			//.append((book.getFSumReturn()!=0?Double.toString(book.getFSumReturn()):" ")).append("\t")//应退合计
			//.append(" \t \t \t")    //edit by zhaoxianlin 20121024  
			 buffAll.append((book.getFBrokerCode()!=null?book.getFBrokerCode():" ")).append("\t")//券商代码
			.append((book.getFRefundDate()!=null?book.getFRefundDate().toString():" ")).append("\t")//退款日期
			.append(" \t")//(book.getFactExRate()!=0?Double.toString(book.getFactExRate()):" ") + "\t" +//实际汇率
			.append(" \t")//(book.getFExRateDate()!=null?book.getFExRateDate().toString():" ") + "\t" + //换汇日期
			.append((book.getFSumReturn()!=0?Double.toString(book.getFSumReturn()):" ")).append("\r\n");//应退合计
		}
		
		if(buffAll.toString().endsWith("\r\n")){//每一条数据之间用”\n“拼接，去掉最后一个”\n“
			buffAll.delete(buffAll.length()- "\r\n".length(), buffAll.length());
		}
		return buffAll.toString();
	}
	
	/**获取ETF参数设置
	 * @param portCodes
	 * @return
	 * @author shashijie ,2011-11-30 , STORY 1789 
	 * @modified 
	 */
	private ETFParamSetBean getETFParamSetBean(String portCodes) throws YssException {
		HashMap paraMap = new HashMap();
		//ETF参数设置操作类对象
		ETFParamSetAdmin para = new ETFParamSetAdmin();
		para.setYssPub(pub);
		paraMap = para.getETFParamInfo(this.portCodes);//获取保存参数设置的hash表
		//ETF参数设置Bean类
		ETFParamSetBean paraSetBean = (ETFParamSetBean)paraMap.get(this.portCodes);//获取参数设置实体bean
		
		return paraSetBean;
	}
	
	/**根据申购、赎回日期获取确认日期 */
	public Date getConfirmdate(Date dDate,String sPortCode) throws YssException {
		StringBuffer buff=null;//做拼接SQL语句
		ResultSet rs=null;//声明结果集
		Date dConfirmDate = null;
		
		try{

			buff = new StringBuffer(100);
			buff.append(" select FConfimDate from ").append(pub.yssGetTableName("Tb_TA_Trade"));
			buff.append(" where FPortCode = ").append(dbl.sqlString(sPortCode));
			buff.append(" and FTradeDate = ").append(dbl.sqlDate(dDate));
			buff.append(" and FSellType = ").append(dbl.sqlString(this.standingBookType.equals("B") ? "01" : "02"));
						
			rs=dbl.openResultSet(buff.toString());
			buff.delete(0,buff.length());
			if(rs.next()){
				dConfirmDate = rs.getDate("FConfimDate");
			}
		}catch(Exception e){
			throw new YssException("获取申购、赎回确认日期出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dConfirmDate;
	}
	
	/**给临时台账数据Bean赋值*/
	private void setTempBook(TempBook book, ResultSet rs) throws Exception {
		book.setFNum(rs.getString("FNum"));//申请编号	VARCHAR(20)
		book.setFBuyDate(rs.getDate("FBuyDate"));//申购日期	Date
		book.setFBs(rs.getString("fBs"));//交易类型	VARCHAR(1)
		book.setFPortCode(rs.getString("FPortCode"));//组合代码	VARCHAR(20)
		book.setFSecurityCode(rs.getString("fSecurityCode"));//证券代码	VARCHAR(20)
		book.setFStockHolderCode(rs.getString("FStockHolderCode"));//股东代码	VARCHAR(20)
		book.setFBrokerCode(rs.getString("fBrokerCode"));//参与券商	VARCHAR(20)
		book.setFSeatCode(rs.getString("FSeatCode"));//席位号	VARCHAR(20)
		book.setFMakeUpAmount(rs.getDouble("FMakeUpAmount"));//补票数量	NUMBER(18,0)
		book.setFUnitCost(rs.getDouble("FUnitCost"));//单位成本	NUMBER(30,15)
		book.setFReplaceCash(rs.getDouble("fReplaceCash"));//替代金额	NUMBER(18,4)
		book.setFCanReplaceCash(rs.getDouble("fCanReplaceCash"));//可退替代款	NUMBER(18,4)
		book.setFRemaindAmount(rs.getDouble("fRemaindAmount"));//剩余数量	NUMBER(18,0)
		book.setFSumReturn(rs.getDouble("fSumReturn"));//应退合计	NUMBER(18,4)
		book.setFRefundDate(rs.getDate("fRefundDate"));//退款日期	Date
		book.setFExchangeRate(rs.getDouble("fExchangeRate"));//申述数据对应的汇率	NUMBER(18,15)
		book.setFOrderCode(rs.getString("fOrderCode"));//排序编号	VARCHAR(200)
		book.setFGradeType1(rs.getString("fGradeType1"));//分级类型1	VARCHAR(40)
		book.setFGradeType2(rs.getString("fGradeType2"));//分级类型2	VARCHAR(40)
		book.setFGradeType3(rs.getString("fGradeType3"));//分级类型3	VARCHAR(40)
		book.setFMakeUpDate1(rs.getDate("fMakeUpDate1"));//第一次补票的日期	Date
		book.setFMakeUpAmount1(rs.getDouble("fMakeUpAmount1"));//第一次补票的数量	Number(18,0)
		book.setFMakeUpUnitCost1(rs.getDouble("fMakeUpUnitCost1"));//第一次补票的单位成本	Number(30,15)
		book.setFOMakeUpCost1(rs.getDouble("fOMakeUpCost1"));//第一次补票的总成本（原币）	Number(18,4)
		book.setFHMakeUpCost1(rs.getDouble("fHMakeUpCost1"));//第一次补票的总成本（本币）	Number(18,4)
		book.setFMakeUpRepCash1(rs.getDouble("fMakeUpRepCash1"));//第一次补票的应付替代款	Number(18,4)
		book.setFCanMkUpRepCash1(rs.getDouble("fCanMkUpRepCash1"));//第一次补票的可退替代款	Number(18,4)
		book.setFExRate1(rs.getDouble("fExRate1"));//第一次补票汇率	Number(18,15)
		book.setFTradeUnitCost1(rs.getDouble("fTradeUnitCost1"));//第一次补票的成交单价	Number(30,15)
		book.setFFeeUnitCost1(rs.getDouble("fFeeUnitCost1"));//第一次补票的费用单价	Number(30,15)
		
	}
	

	/**获取查询SQL
	 * @author shashijie ,2011-11-30 , STORY 1789   modified by yeshenghong   ETF台帐报表分页显示
	 * @throws YssException 
	 * @throws SQLException 
	 */
	private String getTempEtfBookSql() throws SQLException, YssException {
		String secStr = "";
		String sqlString = "";
		ResultSet rs = null;
//		String sqlString = " Select a.*,Lpad(a.FSecurityCode, 88, '0') As Order1 From "+
//		  pub.yssGetTableName("Tb_ETF_TempStandingBook")+
//	      " a where a.FportCode = "+dbl.sqlString(portCodes)+
//	      " and a.FBuyDate = "+dbl.sqlDate(this.startDate)+
//	      " and a.FBs = "+dbl.sqlString(this.standingBookType)+
//	      " order by Order1,a.FOrderCode ";//排序编号排序;排序编号  = 分级类型相连用”##”连接
		if(this.dataPageCount>1){
			sqlString = " select * from (select s.*, rownum rno from(Select distinct fsecuritycode, Lpad(FSecurityCode, 88, '0') As Order1 From " +
						pub.yssGetTableName("Tb_ETF_TempStandingBook")+
						" where FportCode = "+dbl.sqlString(portCodes)+
						" and FBuyDate = "+dbl.sqlDate(this.startDate)+
					    " and FBs = "+dbl.sqlString(this.standingBookType)+
					    " order by Order1) s) where rno between  " + ((this.pageIndex - 1) * this.onePageSecs + 1) +
						   " and " + this.pageIndex * this.onePageSecs;
			rs = dbl.openResultSet(sqlString);
			while(rs.next())
			{
				secStr += dbl.sqlString(rs.getString("fsecuritycode")) + ",";
			}
			if(secStr.length()>2)
			{
				secStr = secStr.substring(0, secStr.length()-1);
				secStr = "(" + secStr + ")";
			}
			sqlString = " Select a.*,Lpad(a.FSecurityCode, 88, '0') As Order1 From "+
						  pub.yssGetTableName("Tb_ETF_TempStandingBook")+
					      " a where a.FportCode = "+dbl.sqlString(portCodes)+
					      " and a.FBuyDate = "+dbl.sqlDate(this.startDate)+
					      " and a.FBs = "+dbl.sqlString(this.standingBookType)+
					      " and a.FSecurityCode in " + secStr +
					      " order by Order1,a.FOrderCode ";//排序编号排序;排序编号  = 分级类型相连用”##”连接
			dbl.closeResultSetFinal(rs);
		}else
		{
			sqlString = " Select a.*,Lpad(a.FSecurityCode, 88, '0') As Order1 From "+
			  		pub.yssGetTableName("Tb_ETF_TempStandingBook")+
		  			" a where a.FportCode = "+dbl.sqlString(portCodes)+
		  			" and a.FBuyDate = "+dbl.sqlDate(this.startDate)+
		  			" and a.FBs = "+dbl.sqlString(this.standingBookType)+
		  			" order by Order1,a.FOrderCode ";//排序编号排序;排序编号  = 分级类型相连用”##”连接
		}
		return sqlString;
	}
	
	/** shashijie 2012-01-12 STORY 1789 华夏要求赎回台账显示为正数   
	 * @author shashijie ,2012-1-12 , STORY 1789
	 * @author shashijie ,2013-5-16 , STORY 3713 修改为公共方法,增加结果集判断
	 */
	public void setStandingBookData2(ResultSet rs, StandingBookBean book,int adjNumber)
			throws YssException {

		try {
			if (dbl.isFieldExist(rs, "FNum")) {
				book.setNum(rs.getString("FNum"));// 申请编号
			}
			if (dbl.isFieldExist(rs, "FBuyDate")) {
				book.setBuyDate(rs.getDate("FBuyDate"));// 申赎日期
			}
			if (dbl.isFieldExist(rs, "FBs")) {
				book.setBs(rs.getString("FBs"));// 台账类型
			}
			if (dbl.isFieldExist(rs, "FPortCode")) {
				book.setPortCode(rs.getString("FPortCode"));// 组合代码
			}
			if (dbl.isFieldExist(rs, "FSecurityCode")) {
				book.setSecurityCode(rs.getString("FSecurityCode"));// 证券代码
			}
			if (dbl.isFieldExist(rs, "FStockHolderCode")) {
				book.setStockHolderCode(rs.getString("FStockHolderCode") == null
								? ""
								: rs.getString("FStockHolderCode"));// 投资者
			}
			if (dbl.isFieldExist(rs, "FBrokerCode")) {
				book.setBrokerCode(rs.getString("FBrokerCode"));// 券商
			}
			if (dbl.isFieldExist(rs, "FSeatCode")) {
				book.setSeatCode(rs.getString("FSeatCode"));// 交易席位
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount")) {
				book.setMakeUpAmount(Math.abs(rs.getDouble("FMakeUpAmount")));// 申赎数量
			}
			if (dbl.isFieldExist(rs, "FUnitCost")) {
				book.setUnitCost(rs.getDouble("FUnitCost"));// 单位成本
			}
			if (dbl.isFieldExist(rs, "FReplaceCash")) {
				book.setReplaceCash(Math.abs(rs.getDouble("FReplaceCash")));// 替代金额
			}
			if (dbl.isFieldExist(rs, "FCanReplaceCash")) {
				book.setCanReplaceCash(Math.abs(rs.getDouble("FCanReplaceCash")));// 可退替代款
			}
			if (dbl.isFieldExist(rs, "FExRightDate")) {
				book.setExRightDate(rs.getDate("FExRightDate"));// 权益日期
			}
			if (dbl.isFieldExist(rs, "FSumAmount")) {
				book.setSumAmount(Math.abs(rs.getDouble("FSumAmount")));// 权益总数量
			}
			if (dbl.isFieldExist(rs, "FRealAmount")) {
				book.setRealAmount(Math.abs(rs.getDouble("FRealAmount")));// 权益实际数量
			}
			if (dbl.isFieldExist(rs, "FTotalInterest")) {
				book.setTotalInterest(rs.getDouble("FTotalInterest"));// 总派息原币
			}
			if (dbl.isFieldExist(rs, "FWarrantCost")) {
				book.setWarrantCost(rs.getDouble("FWarrantCost"));// 权证价值原币
			}
			if (dbl.isFieldExist(rs, "FBBInterest")) {
				book.setBBInterest(Math.abs(rs.getDouble("FBBInterest")));// 总派息本币
			}
			if (dbl.isFieldExist(rs, "FBBWarrantCost")) {
				book.setBBWarrantCost(Math.abs(rs.getDouble("FBBWarrantCost")));// 权证价值本币
			}
			/**add---huhuichao 2013-8-3 STORY  4276 博时：跨境ETF补充增加一类公司行动*/
			if (dbl.isFieldExist(rs, "FOtherRight")) {
				book.setOtherRight(rs.getDouble("FOtherRight"));// 其他权益原币
			}
			if (dbl.isFieldExist(rs, "FBBOtherRight")) {
				book.setbBOtherRight(rs.getDouble("FBBOtherRight"));// 其他权益本位币
			}
			/**end---huhuichao 2013-8-3 STORY  4276*/
			if (dbl.isFieldExist(rs, "FRightRate")) {
				book.setRightRate(rs.getDouble("FRightRate"));// 权益汇率
			}
			if (dbl.isFieldExist(rs, "FMakeUpDate1")) {
				book.setMakeUpDate1(rs.getDate("FMakeUpDate1"));// 第一次补票日期
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount1")) {
				book.setMakeUpAmount1(Math.abs(rs.getDouble("FMakeUpAmount1")));// 第一次补票数量
			}
			if (dbl.isFieldExist(rs, "FTradeUnitCost1")) {
				book.setTradeUnitCost1(rs.getDouble("FTradeUnitCost1"));// 第一次补票的成交单价
			}
			if (dbl.isFieldExist(rs, "FFeeUnitCost1")) {
				book.setFeeUnitCost1(rs.getDouble("FFeeUnitCost1"));// 第一次补票的费用单价
			}
			if (dbl.isFieldExist(rs, "FMakeUpUnitCost1")) {
				book.setMakeUpUnitCost1(rs.getDouble("FMakeUpUnitCost1"));// 第一次补票单位成本
			}
			if (dbl.isFieldExist(rs, "FOMakeUpCost1")) {
				book.setoMakeUpCost1(rs.getDouble("FOMakeUpCost1"));// 第一次补票总成本原币
			}
			if (dbl.isFieldExist(rs, "FHMakeUpCost1")) {
				book.sethMakeUpCost1(rs.getDouble("FHMakeUpCost1"));// 第一次补票总成本本币
			}
			if (dbl.isFieldExist(rs, "FMakeUpRepCash1")) {
				book.setMakeUpRepCash1(rs.getDouble("FMakeUpRepCash1")
						* adjNumber);// 第一次补票应付替代款
			}
			if (dbl.isFieldExist(rs, "FCanMkUpRepCash1")) {
				book.setCanMkUpRepCash1(rs.getDouble("FCanMkUpRepCash1")
						* adjNumber);// 第一次补票可退替代款
			}
			if (dbl.isFieldExist(rs, "FMakeUpDate2")) {
				book.setMakeUpDate2(rs.getDate("FMakeUpDate2"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount2")) {
				book.setMakeUpAmount2(Math.abs(rs.getDouble("FMakeUpAmount2")));
			}
			if (dbl.isFieldExist(rs, "FMakeUpUnitCost2")) {
				book.setMakeUpUnitCost2(rs.getDouble("FMakeUpUnitCost2"));
			}
			if (dbl.isFieldExist(rs, "FTradeUnitCost2")) {
				book.setTradeUnitCost2(rs.getDouble("FTradeUnitCost2"));
			}
			if (dbl.isFieldExist(rs, "FFeeUnitCost2")) {
				book.setFeeUnitCost2(rs.getDouble("FFeeUnitCost2"));
			}
			if (dbl.isFieldExist(rs, "FOMakeUpCost2")) {
				book.setoMakeUpCost2(rs.getDouble("FOMakeUpCost2"));
			}
			if (dbl.isFieldExist(rs, "FHMakeUpCost2")) {
				book.sethMakeUpCost2(rs.getDouble("FHMakeUpCost2"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpRepCash2")) {
				book.setMakeUpRepCash2(rs.getDouble("FMakeUpRepCash2")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FCanMkUpRepCash2")) {
				book.setCanMkUpRepCash2(rs.getDouble("FCanMkUpRepCash2")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FMakeUpDate3")) {
				book.setMakeUpDate3(rs.getDate("FMakeUpDate3"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount3")) {
				book.setMakeUpAmount3(Math.abs(rs.getDouble("FMakeUpAmount3")));
			}
			if (dbl.isFieldExist(rs, "FMakeUpUnitCost3")) {
				book.setMakeUpUnitCost3(rs.getDouble("FMakeUpUnitCost3"));
			}
			if (dbl.isFieldExist(rs, "FTradeUnitCost3")) {
				book.setTradeUnitCost3(rs.getDouble("FTradeUnitCost3"));
			}
			if (dbl.isFieldExist(rs, "FFeeUnitCost3")) {
				book.setFeeUnitCost3(rs.getDouble("FFeeUnitCost3"));
			}
			if (dbl.isFieldExist(rs, "FOMakeUpCost3")) {
				book.setoMakeUpCost3(rs.getDouble("FOMakeUpCost3"));
			}
			if (dbl.isFieldExist(rs, "FHMakeUpCost3")) {
				book.sethMakeUpCost3(rs.getDouble("FHMakeUpCost3"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpRepCash3")) {
				book.setMakeUpRepCash3(rs.getDouble("FMakeUpRepCash3")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FCanMkUpRepCash3")) {
				book.setCanMkUpRepCash3(rs.getDouble("FCanMkUpRepCash3")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FMakeUpDate4")) {
				book.setMakeUpDate4(rs.getDate("FMakeUpDate4"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount4")) {
				book.setMakeUpAmount4(Math.abs(rs.getDouble("FMakeUpAmount4")));
			}
			if (dbl.isFieldExist(rs, "FMakeUpUnitCost4")) {
				book.setMakeUpUnitCost4(rs.getDouble("FMakeUpUnitCost4"));
			}
			if (dbl.isFieldExist(rs, "FTradeUnitCost4")) {
				book.setTradeUnitCost4(rs.getDouble("FTradeUnitCost4"));
			}
			if (dbl.isFieldExist(rs, "FFeeUnitCost4")) {
				book.setFeeUnitCost4(rs.getDouble("FFeeUnitCost4"));
			}
			if (dbl.isFieldExist(rs, "FOMakeUpCost4")) {
				book.setoMakeUpCost4(rs.getDouble("FOMakeUpCost4"));
			}
			if (dbl.isFieldExist(rs, "FHMakeUpCost4")) {
				book.sethMakeUpCost4(rs.getDouble("FHMakeUpCost4"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpRepCash4")) {
				book.setMakeUpRepCash4(rs.getDouble("FMakeUpRepCash4")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FCanMkUpRepCash4")) {
				book.setCanMkUpRepCash4(rs.getDouble("FCanMkUpRepCash4")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FMakeUpDate5")) {
				book.setMakeUpDate5(rs.getDate("FMakeUpDate5"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpAmount5")) {
				book.setMakeUpAmount5(Math.abs(rs.getDouble("FMakeUpAmount5")));
			}
			if (dbl.isFieldExist(rs, "FMakeUpUnitCost5")) {
				book.setMakeUpUnitCost5(rs.getDouble("FMakeUpUnitCost5"));
			}
			if (dbl.isFieldExist(rs, "FTradeUnitCost5")) {
				book.setTradeUnitCost5(rs.getDouble("FTradeUnitCost5"));
			}
			if (dbl.isFieldExist(rs, "FFeeUnitCost5")) {
				book.setFeeUnitCost5(rs.getDouble("FFeeUnitCost5"));
			}
			if (dbl.isFieldExist(rs, "FOMakeUpCost5")) {
				book.setoMakeUpCost5(rs.getDouble("FOMakeUpCost5"));
			}
			if (dbl.isFieldExist(rs, "FHMakeUpCost5")) {
				book.sethMakeUpCost5(rs.getDouble("FHMakeUpCost5"));
			}
			if (dbl.isFieldExist(rs, "FMakeUpRepCash5")) {
				book.setMakeUpRepCash5(rs.getDouble("FMakeUpRepCash5")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FCanMkUpRepCash5")) {
				book.setCanMkUpRepCash5(rs.getDouble("FCanMkUpRepCash5")
						* adjNumber);
			}
			if (dbl.isFieldExist(rs, "FMustMkUpDate")) {
				book.setMustMkUpDate(rs.getDate("FMustMkUpDate"));// 强制处理日期
			}
			if (dbl.isFieldExist(rs, "FMustMkUpAmount")) {
				book.setMustMkUpAmount(Math.abs(rs.getDouble("FMustMkUpAmount")));// 强制处理数量
			}
			if (dbl.isFieldExist(rs, "FMustMkUpUnitCost")) {
				book.setMustMkUpUnitCost(rs.getDouble("FMustMkUpUnitCost"));// 强制处理单位成本
			}
			if (dbl.isFieldExist(rs, "FOMustMkUpCost")) {
				book.setoMustMkUpCost(rs.getDouble("FOMustMkUpCost"));// 强制处理总成本原币
			}
			if (dbl.isFieldExist(rs, "FHMustMkUpCost")) {
				book.sethMustMkUpCost(rs.getDouble("FHMustMkUpCost"));// 强制处理总成本本币
			}
			if (dbl.isFieldExist(rs, "FMustMkUpRepCash")) {
				book.setMustMkUpRepCash(Math.abs(rs.getDouble("FMustMkUpRepCash")));// 强制处理应付替代款
			}
			if (dbl.isFieldExist(rs, "FMustCMkUpRepCash")) {
				book.setMustCMkUpRepCash(Math.abs(rs.getDouble("FMustCMkUpRepCash")));// 强制处理可退替代款
			}
			if (dbl.isFieldExist(rs, "FRemaindAmount")) {
				book.setRemaindAmount(Math.abs(rs.getDouble("FRemaindAmount")));// 剩余数量
			}
			if (dbl.isFieldExist(rs, "FSumReturn")) {
				/**add---huhuichao 2013-8-1 BUG  8926 ETF申赎台账中，当“实际补券总成本”大于“替代金额”时，“退款合计”项的金额不正确*/
				book.setSumReturn(rs.getDouble("FSumReturn"));// 应退合计
				/**end---huhuichao 2013-8-1 BUG  8926*/
			}
			if (dbl.isFieldExist(rs, "FRefundDate")) {
				book.setRefundDate(rs.getDate("FRefundDate"));// 退款日期
			}
			if (dbl.isFieldExist(rs, "FExchangeRate")) {
				book.setExchangeRate(rs.getDouble("FExchangeRate"));// 换汇汇率
			}
			if (dbl.isFieldExist(rs, "FOrderCode")) {
				book.setOrderCode(rs.getString("FOrderCode"));// 排序编号
			}
			if (dbl.isFieldExist(rs, "FGradeType1")) {
				book.setGradeType1(rs.getString("FGradeType1"));// 分级类型1
			}
			if (dbl.isFieldExist(rs, "FGradeType2")) {
				book.setGradeType2(rs.getString("FGradeType2"));// 分级类型2
			}
			if (dbl.isFieldExist(rs, "FGradeType3")) {
				book.setGradeType3(rs.getString("FGradeType3"));// 分机类型3
			}
			if (dbl.isFieldExist(rs, "FExRate1")) {
				book.setExRate1(rs.getDouble("FExRate1"));// 第一次补票汇率
			}
			if (dbl.isFieldExist(rs, "FExRate2")) {
				book.setExRate2(rs.getDouble("FExRate2"));// 第二次补票汇率
			}
			if (dbl.isFieldExist(rs, "FExRate3")) {
				book.setExRate3(rs.getDouble("FExRate3"));// 第三次补票汇率
			}
			if (dbl.isFieldExist(rs, "FExRate4")) {
				book.setExRate4(rs.getDouble("FExRate4"));// 第四次补票汇率
			}
			if (dbl.isFieldExist(rs, "FExRate5")) {
				book.setExRate5(rs.getDouble("FExRate5"));// 第五次补票汇率
			}
			if (dbl.isFieldExist(rs, "FMustExRate")) {
				book.setMustExRate(rs.getDouble("FMustExRate"));// 强制处理汇率
			}
			if (dbl.isFieldExist(rs, "FFactExRate")) {
				book.setFactExRate(rs.getDouble("FFactExRate"));// 实际汇率
			}
			if (dbl.isFieldExist(rs, "FExRateDate")) {
				book.setExRateDate(rs.getDate("FExRateDate"));// 换汇日期
			}
			if (dbl.isFieldExist(rs, "FTradeNum")) {
				book.setTradeNum(rs.getString("FTradeNum"));// 成交编号
			}
			if (dbl.isFieldExist(rs, "FRateType")) {
				book.setRateType(rs.getString("FRateType") != null ? 
						rs.getString("FRateType") : " ");// 汇率类型t+1或者t+4
			}
			if (dbl.isFieldExist(rs, "FMarkType")) {
				book.setMarkType(rs.getString("FMarkType"));// 标志类型 time =实时补票
				// ,difference = 钆差补票
			}

		} catch (Exception e) {
			throw new YssException("往台账设置bean中赋值出错！", e);
		}
	}
	
	/** shashijie 2012-01-12 STORY 1789 华夏要求赎回台账显示为正数   */
	private void setTempBook2(TempBook book, ResultSet rs) throws Exception {
		book.setFNum(rs.getString("FNum"));//申请编号	VARCHAR(20)
		book.setFBuyDate(rs.getDate("FBuyDate"));//申购日期	Date
		book.setFBs(rs.getString("fBs"));//交易类型	VARCHAR(1)
		book.setFPortCode(rs.getString("FPortCode"));//组合代码	VARCHAR(20)
		book.setFSecurityCode(rs.getString("fSecurityCode"));//证券代码	VARCHAR(20)
		book.setFStockHolderCode(rs.getString("FStockHolderCode"));//股东代码	VARCHAR(20)
		book.setFBrokerCode(rs.getString("fBrokerCode"));//参与券商	VARCHAR(20)
		book.setFSeatCode(rs.getString("FSeatCode"));//席位号	VARCHAR(20)
		book.setFMakeUpAmount(Math.abs(rs.getDouble("FMakeUpAmount")));//补票数量	NUMBER(18,0)
		book.setFUnitCost(rs.getDouble("FUnitCost"));//单位成本	NUMBER(30,15)
		book.setFReplaceCash(Math.abs(rs.getDouble("fReplaceCash")));//替代金额	NUMBER(18,4)
		book.setFCanReplaceCash(Math.abs(rs.getDouble("fCanReplaceCash")));//可退替代款	NUMBER(18,4)
		book.setFRemaindAmount(rs.getDouble("fRemaindAmount"));//剩余数量	NUMBER(18,0)
		//book.setFSumReturn(YssD.mul(rs.getDouble("fSumReturn")));//应退合计	NUMBER(18,4)
		book.setFSumReturn((rs.getDouble("fSumReturn")));//应退合计	NUMBER(18,4)// //edit by zhaoxianlin 20121024  
		book.setFRefundDate(rs.getDate("fRefundDate"));//退款日期	Date
		book.setFExchangeRate(rs.getDouble("fExchangeRate"));//申述数据对应的汇率	NUMBER(18,15)
		book.setFOrderCode(rs.getString("fOrderCode"));//排序编号	VARCHAR(200)
		book.setFGradeType1(rs.getString("fGradeType1"));//分级类型1	VARCHAR(40)
		book.setFGradeType2(rs.getString("fGradeType2"));//分级类型2	VARCHAR(40)
		book.setFGradeType3(rs.getString("fGradeType3"));//分级类型3	VARCHAR(40)
		book.setFMakeUpDate1(rs.getDate("fMakeUpDate1"));//第一次补票的日期	Date
		book.setFMakeUpAmount1(Math.abs(rs.getDouble("fMakeUpAmount1")));//第一次补票的数量	Number(18,0)
		book.setFMakeUpUnitCost1(rs.getDouble("fMakeUpUnitCost1"));//第一次补票的单位成本	Number(30,15)
		book.setFOMakeUpCost1(rs.getDouble("fOMakeUpCost1"));//第一次补票的总成本（原币）	Number(18,4)
		book.setFHMakeUpCost1(rs.getDouble("fHMakeUpCost1"));//第一次补票的总成本（本币）	Number(18,4)
		book.setFMakeUpRepCash1(Math.abs(rs.getDouble("fMakeUpRepCash1")));//第一次补票的应付替代款	Number(18,4)
		book.setFCanMkUpRepCash1(Math.abs(rs.getDouble("fCanMkUpRepCash1")));//第一次补票的可退替代款	Number(18,4)
		book.setFExRate1(rs.getDouble("fExRate1"));//第一次补票汇率	Number(18,15)
		book.setFTradeUnitCost1(rs.getDouble("fTradeUnitCost1"));//第一次补票的成交单价	Number(30,15)
		book.setFFeeUnitCost1(rs.getDouble("fFeeUnitCost1"));//第一次补票的费用单价	Number(30,15)
		
	}

}