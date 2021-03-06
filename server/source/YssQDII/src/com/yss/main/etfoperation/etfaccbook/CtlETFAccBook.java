package com.yss.main.etfoperation.etfaccbook;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IBuildReport;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.GcAndJqpj.CreateBook;
import com.yss.main.etfoperation.etfaccbook.GcAndJqpj.CreateBookPre;
import com.yss.main.etfoperation.etfaccbook.dealboshi.CreateRefDataOfMakeUpMust;
import com.yss.main.etfoperation.etfaccbook.easySquareReach.CreateStandingBook;
import com.yss.main.etfoperation.etfaccbook.easySquareReach.CreateTradeBook;
import com.yss.main.etfoperation.etfaccbook.gadoliniumETF.CalcSubStandingBook;
import com.yss.main.etfoperation.etfaccbook.gadoliniumETF.CreateSccBoook;
import com.yss.main.etfoperation.etfaccbook.gadoliniumETF.CreateTradeClose;
import com.yss.main.etfoperation.etfaccbook.timeandaverage.CreateAccBookData;
import com.yss.main.etfoperation.etfaccbook.timeandaverage.CreateAccBookPreparament;
import com.yss.main.etfoperation.etfaccbook.timeanddifference.CreateBookData;
import com.yss.main.etfoperation.etfaccbook.timeanddifference.CreateBookPretreatment;
import com.yss.main.etfoperation.etfaccbook.timeanddifference.DivideETFReplaceOrAction;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * ETF台账操作的控制类
 * @author 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 *
 */
public class CtlETFAccBook extends BaseBean implements IBuildReport, IDataSetting {
	protected String securityCodes = "";//证券代码
	protected java.util.Date startDate = null;//开始的申赎日期
	protected java.util.Date endDate = null;//结束的申赎日期	
	protected java.util.Date tradeDate = null;//估值日期
	protected String standingBookType = "";//台账类型
	protected String portCodes = "";//组合代码
	/**shashijie 2011-12-31 STORY 1789*/
	protected Date browDate = null;//浏览日期
	/**end*/
	
	//and by fangjiang 2013.01.08 STORY #3402
	protected ETFParamSetBean paramSet = null;//ETF参数的实体类	
	protected PretValMktPriceAndExRate marketValue = null;
	protected java.util.Date bsDate = null;//申赎日期
	protected CreateBookPretreatmentAdmin booPreAdmin = null;
	protected ETFParamSetAdmin paramSetAdmin = null;
	
	//分页显示用, add by yeshenghong 20120530 
	protected int dataPageCount = 0;
	protected int pageIndex = 0;
	protected int onePageSecs = 0;
	
	public void initData(Date tradeDate, Date bsDate, String portCodes, ETFParamSetBean paramSet, 
			             PretValMktPriceAndExRate marketValue) {	
		this.tradeDate = tradeDate;
		this.bsDate = bsDate;
		this.portCodes = portCodes;
		this.paramSet = paramSet;
		this.marketValue = marketValue;	
		
		paramSetAdmin = new ETFParamSetAdmin();
		paramSetAdmin.setYssPub(pub);
		this.booPreAdmin = new CreateBookPretreatmentAdmin();
		this.booPreAdmin.setYssPub(pub);
	}
	//end by fangjiang 2013.01.08 STORY #3402
	
	/**
	 * 构造函数
	 */
	public CtlETFAccBook(){
		
	}
	
	public java.util.Date getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(java.util.Date tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getPortCodes() {
		return portCodes;
	}

	public void setPortCodes(String portCodes) {
		this.portCodes = portCodes;
	}

	public void initBuildReport(BaseBean bean) throws YssException {

	}

	public String saveReport(String sReport) throws YssException {
		return null;
	}

	public String addSetting() throws YssException {
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		
	}

	public void checkSetting() throws YssException {
		
	}

	public void delSetting() throws YssException {
		
	}

	public void deleteRecycleData() throws YssException {
		
	}

	public String editSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return null;
	}

	public String getBeforeEditData() throws YssException {
		return null;
	}

	public String buildRowStr() throws YssException {
		return null;
	}
	
	public String buildReport(String sType) throws YssException {
		String sETFBookData="";//拼接好的ETF数据
		CreateAccBookRefData createAccBookRef = null;
		CreateAccBoook createAccBook = null;
		SearchAccBook search = null;
		HashMap hmMaxRightDate = null;
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
		String [] type=null;
		CreateBookPretreatment booPretreatment =null;//华宝明细数据和明细关联数据操作类
		CreateBookData createBook = null;//华宝生成台账操作类
		DivideETFReplaceOrAction divideETFSubTrade = null;
		CalcRefundData calReFund = null;
		CreateAccBookPreparament booaccPretreatment = null;
		CreateAccBookData accbookdata = null;
		try{
			if (sType == null)
			{
				return "";
			}
			type = sType.split("/t");//解析前台传来的数据
			this.parseRowStr(type[1]);//用基类的方法解析数据
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			if(!etfParam.containsKey(portCodes)){
				throw new YssException("请检查参数设置是否有数据或数据是否已经审核！");
			}
			paramSet = (ETFParamSetBean) etfParam.get(portCodes);
			if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_SUB)){//嘉实入口--钆差补票
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//生成台账数据
					createAccBookRef = new CreateAccBookRefData();
					createAccBookRef.setYssPub(pub);
					
					hmMaxRightDate = createAccBookRef.getMaxRightsInfo();
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					createAccBook.generateStandingBook(this.tradeDate,hmMaxRightDate,sType);

					//查询台账数据，并返回前台显示//分页显示用, add by yeshenghong 20120530 
					//search = new SearchAccBook();
					//search.setYssPub(pub);
					//sETFBookData = search.getETFBookData(sType);
					//return sETFBookData;
					return "true";
					
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getETFBookData(sType);
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					createAccBookRef = new CreateAccBookRefData();
					createAccBookRef.setYssPub(pub);
					hmMaxRightDate = createAccBookRef.getMaxRightsInfo();
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					createAccBook.generateStandingBook(this.tradeDate,hmMaxRightDate,sType);
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getBookDataPageCount(sType);
					return sETFBookData;
				}
			}
			if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_MUST)){//博时入口--钆差补票_强退
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//生成台账数据
					createAccBookRef = new CreateAccBookRefData();
					createAccBookRef.setYssPub(pub);
					
					hmMaxRightDate = createAccBookRef.getMaxRightsInfo();
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					createAccBook.generateStandingBook(this.tradeDate,hmMaxRightDate,sType);

					//查询台账数据，并返回前台显示  modified by  yeshenghong ETF台帐分页显示
//					search = new SearchAccBook();
//					search.setYssPub(pub);
//					sETFBookData = search.getETFBookData(sType);
//					return sETFBookData;
					return "true";
					
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getETFBookData(sType);
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					createAccBookRef = new CreateAccBookRefData();
					createAccBookRef.setYssPub(pub);
					hmMaxRightDate = createAccBookRef.getMaxRightsInfo();
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					createAccBook.generateStandingBook(this.tradeDate,hmMaxRightDate,sType);
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getBookDataPageCount(sType);
					return sETFBookData;
				}
			}
			else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMESUB)){//华宝入口-- 实时补票 + 钆差补票
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//处理明细和明细关联数据
					booPretreatment = new CreateBookPretreatment();
					booPretreatment.setYssPub(pub);
					booPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booPretreatment.doManageAll();
					//处理台账数据
					createBook = new CreateBookData();
					createBook.setYssPub(pub);
					createBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					createBook.doManageAll();
					
					//生成台帐子表数据
					calReFund = new CalcRefundData(pub);
					calReFund.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
					
					
					//拆分主动被动数据
					divideETFSubTrade = new DivideETFReplaceOrAction();
					divideETFSubTrade.setYssPub(pub);
					divideETFSubTrade.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					divideETFSubTrade.doManage();
					
					//查询台账数据，并返回前台显示 modifide by  yeshenghong  台帐报表分页显示   20130226
//					search = new SearchAccBook();
//					search.setYssPub(pub);
//					sETFBookData = search.getETFBookData(sType);
//					return sETFBookData;
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getETFBookData(sType);
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					//处理明细和明细关联数据
					booPretreatment = new CreateBookPretreatment();
					booPretreatment.setYssPub(pub);
					booPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booPretreatment.doManageAll();
					//处理台账数据
					createBook = new CreateBookData();
					createBook.setYssPub(pub);
					createBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					createBook.doManageAll();
					
					//生成台帐子表数据
					calReFund = new CalcRefundData(pub);
					calReFund.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
					
					
					//拆分主动被动数据
					divideETFSubTrade = new DivideETFReplaceOrAction();
					divideETFSubTrade.setYssPub(pub);
					divideETFSubTrade.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					divideETFSubTrade.doManage();
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getBookDataPageCount(sType);
					return sETFBookData;
				}	
			}/**shashijie 2013-1-18 STORY 3402 国泰也走这里查询台账入口*/
			else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)//华夏入口-实时+均摊
					|| paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)//国泰入口
				){
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//处理明细和明细关联数据
					booaccPretreatment = new CreateAccBookPreparament();
					booaccPretreatment.setYssPub(pub);
					booaccPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booaccPretreatment.doManageAll();
					//处理台账数据
					accbookdata = new CreateAccBookData();
					accbookdata.setYssPub(pub);
					accbookdata.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					accbookdata.doManageAll();
					//查询台账数据，并返回前台显示 modifide by  yeshenghong  台帐报表分页显示   20130226
//					search = new SearchAccBook();
//					search.setYssPub(pub);
//					sETFBookData = search.getETFBookData(sType);
//					return sETFBookData;
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					search = new SearchAccBook();
					search.setYssPub(pub);
					/**shashijie 2011-11-30 STORY 1789 */
					//如果浏览日期小于申赎确认日期,则查询临时台帐
					Date dConfirmdate = search.getConfirmdate(startDate,this.portCodes);
					if (null != dConfirmdate && YssFun.dateDiff(browDate,dConfirmdate) > 0) {
						sETFBookData = search.getTempETFBookData(sType);
					} else {
						sETFBookData = search.getETFBookData(sType);
						
						/**shashijie 2012-01-04 STORY 1789 需求变更,T+1日确认才有T日的台账数据  */
						if (sETFBookData.trim().equals("")) {//如果台账没有数据,则还是查临时台账
							sETFBookData = search.getTempETFBookData(sType);
						}
						/**end*/
						
					}
					/**end*/
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					//处理明细和明细关联数据
					booaccPretreatment = new CreateAccBookPreparament();
					booaccPretreatment.setYssPub(pub);
					booaccPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booaccPretreatment.doManageAll();
					//处理台账数据
					accbookdata = new CreateAccBookData();
					accbookdata.setYssPub(pub);
					accbookdata.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					accbookdata.doManageAll();

					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					
					Date dConfirmdate = search.getConfirmdate(startDate,this.portCodes);
					if (null != dConfirmdate && YssFun.dateDiff(browDate,dConfirmdate) > 0) {
						sETFBookData = search.getTempBookDataCount(sType);
					} else {
						sETFBookData = search.getBookDataPageCount(sType);
						
						/**shashijie 2012-01-04 STORY 1789 需求变更,T+1日确认才有T日的台账数据  */
						if (sETFBookData.trim().equals("")) {//如果台账没有数据,则还是查临时台账
							sETFBookData = search.getTempBookDataCount(sType);
						}
						/**end*/
						
					}
					/**end*/
					
					return sETFBookData;
				}	
			}
			/**end shashijie 2013-1-18 STORY 3402 */
			/**shashijie 2011.07.05 需求974*/
			//华宝入口   -- 钆差补票(先到先得)
			else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_SUB_ORDER)){
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//处理明细和明细关联数据
					//明细数据和明细关联数据操作类(华宝先到先得钆差补票)
					CreateTradeClose tradeClose = new CreateTradeClose();
					tradeClose.setYssPub(pub);
					tradeClose.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					tradeClose.doManageAll();
					
					//处理台账数据
					CreateSccBoook sccBook = new CreateSccBoook();//生成台账操作类(华宝先到先得钆差补票)
					sccBook.setYssPub(pub);
					sccBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					sccBook.doManageAll();
					
					//生成台帐子表数据
					//计算台帐应退款估值增值，并插入台帐关联表
					CalcSubStandingBook calBook = new CalcSubStandingBook(pub);
					calBook.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
					
					//查询台账数据，并返回前台显示 modifide by  yeshenghong  台帐报表分页显示   20130226
//					search = new SearchAccBook();
//					search.setYssPub(pub);
//					sETFBookData = search.getETFBookData(sType);
//					return sETFBookData;
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getETFBookData(sType);
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					//明细数据和明细关联数据操作类(华宝先到先得钆差补票)
					CreateTradeClose tradeClose = new CreateTradeClose();
					tradeClose.setYssPub(pub);
					tradeClose.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					tradeClose.doManageAll();
					
					//处理台账数据
					CreateSccBoook sccBook = new CreateSccBoook();//生成台账操作类(华宝先到先得钆差补票)
					sccBook.setYssPub(pub);
					sccBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					sccBook.doManageAll();
					
					//生成台帐子表数据
					//计算台帐应退款估值增值，并插入台帐关联表
					CalcSubStandingBook calBook = new CalcSubStandingBook(pub);
					calBook.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getBookDataPageCount(sType);
					return sETFBookData;
				}	
			}
			/**shashijie 2011.07.28 需求1434*/
			//易方达入口   -- 钆差补票(一次性)
			else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)){
				if(sType.indexOf("CreateBookData")!=-1){//生成台账
					//处理明细和明细关联数据
					countYiFangDaDoManageAll();
					//查询台账数据，并返回前台显示
//					search = new SearchAccBook();
//					search.setYssPub(pub);
//					sETFBookData = search.getETFBookData(sType);
//					return sETFBookData;
					return "true";//分页显示用, add by yeshenghong 20120530 
				}else if(sType!=null&&sType.indexOf("getETFBookData")!=-1){//查询台账
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getETFBookData(sType);
					return sETFBookData;
				}else if(sType!=null&&sType.indexOf("BattchCreateData")!=-1){//批量生成台账数据
					//批量生成台账数据
					countYiFangDaDoManageAll();
					return "true";
				}else if(sType!=null&&sType.indexOf("getETFPageCount")!=-1)
				{//分页显示用, add by yeshenghong 20120530 
					search = new SearchAccBook();
					search.setYssPub(pub);
					sETFBookData = search.getBookDataPageCount(sType);
					return sETFBookData;
				}	
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
		
		return ""; //------ modify by wangzuochun 2011.01.17 BUG #893 ETF台帐点击查询报错 
	}

	public String getOperValue(String sType) throws YssException {
		HashMap hmMaxRightDate = null;
		CreateAccBookRefData createAccBookRef = null;
		CreateAccBoook createAccBook = null;
		CreateRefDataOfMakeUpMust createMakeUpMustBook = null;
		String sReturnData="";
		/**shashijie 2013-1-17 没有用到所以注视 */
		/*String[] sTypes = null;
		String[] sData = null;*/
		/**end shashijie 2013-1-17 STORY */
		ETFParamSetBean paramSet = null;// ETF参数的实体类
		ETFParamSetAdmin paramSetAdmin = null;
		HashMap etfParam = null;
		String [] type=null;
		CreateBookPretreatment booPretreatment =null;//华宝明细数据和明细关联数据操作类
		CreateBookData createBook = null;//华宝生成台账操作类
		CreateAccBookPreparament booaccPretreatment = null;
		DivideETFReplaceOrAction divideETFSubTrade = null;
		CalcRefundData calReFund = null;
		CreateAccBookData accbookdata = null;
		
		try{
		if(sType != null){
			type = sType.split("/t");//解析前台传来的数据
			if(type.length > 1){
				this.parseRowStr(type[1]);//用基类的方法解析数据
			}
			//and by fangjiang 2013.01.08 STORY #3402			
			Date bsDate = null;
			PretValMktPriceAndExRate marketValue = new PretValMktPriceAndExRate();
			marketValue.setYssPub(this.pub);
			if(sType.equals("ETFValuation") && null != this.tradeDate){
				//申赎日期
				bsDate = this.getBSDate();
				//行情、汇率				
				marketValue.getValMktPriceAndExRateBy(this.portCodes, this.tradeDate);
			}
			//参数
			paramSetAdmin = new ETFParamSetAdmin();
			paramSetAdmin.setYssPub(pub);
			etfParam = paramSetAdmin.getETFParamInfo(portCodes); // 根据已选组合代码用于获取相关ETF参数数据
			if(!etfParam.containsKey(portCodes)){
				throw new YssException("请检查参数设置是否有数据或数据是否已经审核！");
			}
			paramSet = (ETFParamSetBean) etfParam.get(portCodes);
			if(paramSet.getSupplyMode() != null && paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_SUB)){//嘉实入口--钆差补票
				if(sType.equals("ETFValuation")){
					createAccBookRef = new CreateAccBookRefData();
					createAccBookRef.setYssPub(pub);
					
					createAccBookRef.doInsert(this.tradeDate,this.portCodes);
					
					hmMaxRightDate = createAccBookRef.getMaxRightsInfo();
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					//结合交易结算明细表和交易结算明细关联表将数据插入到台帐表
					createAccBook.insertIntoStandingBook(this.tradeDate,this.portCodes, hmMaxRightDate);
					
					//计算汇兑损益
					CalcRefundData refundData = new CalcRefundData(pub);
					refundData.createAccBookRefundMVBy(tradeDate, tradeDate, portCodes);
				}
				if(sType.indexOf("UpdateChangeRate")!=-1){//汇率更新
					
					//更新台帐表中的实际汇率
					UpdateBookRate updateRate=new UpdateBookRate();
					updateRate.setYssPub(pub);
					sReturnData = updateRate.UpdateChangeRate(sType);
				}
				/**add---shashijie 2013-3-5 STORY 3693 注释原因:重复代码*/
				/*if(sType.indexOf("UpdateRefundDate")!=-1){//更新退款日期
					//更新台帐表中的退款日期
					UpdateRefundDate updateRefundDate = new UpdateRefundDate();
					updateRefundDate.setYssPub(pub);
					sReturnData = updateRefundDate.UpdateRefundDate(sType);
				}*/
				/**end---shashijie 2013-3-5 STORY 3693 注释原因:重复代码*/
			}/**shashijie 2011.07.04  STORY #974 按照华宝兴业ETF的产品规则，定义出符合该产品的功能需求*/
			else if(paramSet.getSupplyMode() != null && //华宝入口--钆差补票(先到先得)
					paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_SUB_ORDER)){
				if(sType.equals("ETFValuation")){
					CreateTradeClose tradeClose = null;//明细数据和明细关联数据操作类(华宝先到先得钆差补票)
					CreateSccBoook sccBook = null;//生成台账操作类(华宝先到先得钆差补票)
					//处理明细和明细关联数据
					tradeClose = new CreateTradeClose();
					tradeClose.setYssPub(pub);
					tradeClose.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					tradeClose.doManageAll();
					
					//处理台账数据
					sccBook = new CreateSccBoook();
					sccBook.setYssPub(pub);
					sccBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					sccBook.doManageAll();
					
					//生成台帐子表数据
					//计算台帐应退款估值增值，并插入台帐关联表
					CalcSubStandingBook calBook = new CalcSubStandingBook(pub);
					calBook.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
				}
			/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
			}else if(paramSet.getSupplyMode() != null && paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMESUB)){//华宝入口 -- 实时补票 + 钆差补票
				if(sType.equals("ETFValuation")){
					//处理明细和明细关联数据
					booPretreatment = new CreateBookPretreatment();
					booPretreatment.setYssPub(pub);
					booPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booPretreatment.doManageAll();
					//处理台账数据
					createBook = new CreateBookData();
					createBook.setYssPub(pub);
					createBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					createBook.doManageAll();
					
					//生成台帐子表数据
					calReFund = new CalcRefundData(pub);
					calReFund.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);
					
					
					//拆分主动被动数据
					divideETFSubTrade = new DivideETFReplaceOrAction();
					divideETFSubTrade.setYssPub(pub);
					divideETFSubTrade.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					divideETFSubTrade.doManage();
				}
			}
			else if(paramSet.getSupplyMode() != null && paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_MUST)){//博时入口 
				if(sType.equals("ETFValuation")){
					createMakeUpMustBook = new CreateRefDataOfMakeUpMust();
					createMakeUpMustBook.setYssPub(pub);
					
					createMakeUpMustBook.doInsert(this.tradeDate,this.portCodes);
					
					createAccBook = new CreateAccBoook();
					createAccBook.setYssPub(pub);
					
					//结合交易结算明细表和交易结算明细关联表将数据插入到台帐表
					createAccBook.insertIntoStandingBook(this.tradeDate,this.portCodes, hmMaxRightDate);
					
					//计算汇兑损益
					CalcRefundData refundData = new CalcRefundData(pub);
					refundData.createAccBookRefundMVBy(tradeDate, tradeDate, portCodes);
				}
				if(sType.indexOf("UpdateChangeRate")!=-1){//汇率更新
					
					//更新台帐表中的实际汇率
					UpdateBookRate updateRate=new UpdateBookRate();
					updateRate.setYssPub(pub);
					sReturnData = updateRate.UpdateChangeRate(sType);
				}
				/**add---shashijie 2013-3-5 STORY 3693 注释原因:重复代码*/
				/*if(sType.indexOf("UpdateRefundDate")!=-1){//更新退款日期
					//更新台帐表中的退款日期
					UpdateRefundDate updateRefundDate = new UpdateRefundDate();
					updateRefundDate.setYssPub(pub);
					sReturnData = updateRefundDate.UpdateRefundDate(sType);
				}*/
				/**end---shashijie 2013-3-5 STORY 3693 注释原因:重复代码*/
			}else if(paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_TIMEAVERAGE)){//华夏入口 -  实时+均摊
				if(sType.equals("ETFValuation")){
					//处理明细和明细关联数据
					booaccPretreatment = new CreateAccBookPreparament();
					booaccPretreatment.setYssPub(pub);
					booaccPretreatment.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					booaccPretreatment.doManageAll();
					
					//处理台账数据
					accbookdata = new CreateAccBookData();
					accbookdata.setYssPub(pub);
					accbookdata.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
					accbookdata.doManageAll();
					
					//拆分主动被动数据

				}
			} /** shashijie 2011-07-28 STORY 1434 */
			else if (paramSet.getSupplyMode() != null && 
					paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)) {//易方达 - 钆差   一次性补完
				//处理业务流程
				if(sType.equals("ETFValuation")){
					countYiFangDaDoManageAll();
				}
			}/**~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~*/
			//and by fangjiang 2013.01.08 STORY #3402
			else if (paramSet.getSupplyMode() != null && 
					paramSet.getSupplyMode().equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)) {//轧差+加权平均
				if(sType.equals("ETFValuation")){
					//台账预处理
					CreateBookPre bookPre = new CreateBookPre();
					bookPre.setYssPub(pub);
					bookPre.initData(this.tradeDate, bsDate, this.portCodes, paramSet, marketValue);
					bookPre.doManageAll();
					
					//处理台账数据
					CreateBook book = new CreateBook();
					book.setYssPub(pub);
					book.initData(this.tradeDate, bsDate, this.portCodes, paramSet, marketValue);
					book.doManageAll();				
				}			
			}
			if(sType.indexOf("getSecurityCode")!=-1){//获取台帐证券代码
				SearchAccBook serch =new SearchAccBook();
				serch.setYssPub(pub);
				sReturnData=serch.getSecurityCode(sType,portCodes);
			}
			if(sType.indexOf("getETFParamDealDayNum")!=-1){//获取参数设置中的一些数据
				SearchAccBook serch =new SearchAccBook();
				serch.setYssPub(pub);
				sReturnData = serch.getETFParamDealDayNum(sType);
			}
			if(sType.indexOf("getSubStandingBookDataCount")!=-1){
				SearchAccBook serch =new SearchAccBook();
				serch.setYssPub(pub);
				sReturnData = Integer.toString(serch.getSubStandingBookDataCount(sType));
			}
			if(sType.indexOf("getBoshiSubStandingBookDataCount")!=-1){
				SearchAccBook serch =new SearchAccBook();
				serch.setYssPub(pub);
				sReturnData = Integer.toString(serch.getBoshiSubStandingBookDataCount(sType));
			}
			/**add---shashijie 2013-3-5 STORY 3693 更新退款日期*/
			if(sType.indexOf("UpdateRefundDate")!=-1){//更新退款日期
				//更新台帐表中的退款日期
				UpdateRefundDate updateRefundDate = new UpdateRefundDate();
				updateRefundDate.setYssPub(pub);
				sReturnData = updateRefundDate.updateRefundDate(sType);
			}
			/**end---shashijie 2013-3-5 STORY 3693*/
		}
		} catch(Exception e){
			throw new YssException(e.getMessage(), e);			
		}
		
		return sReturnData;
	}
	
	/**shashijie,2011-7-28 处理易方达业务流程*/
	private void countYiFangDaDoManageAll() throws YssException {
		//处理明细和明细关联数据
		CreateTradeBook tradeClose = new CreateTradeBook();//明细数据和明细关联数据操作类(华宝先到先得钆差补票)
		tradeClose.setYssPub(pub);
		tradeClose.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
		tradeClose.doManageAll();
		
		//处理台账数据
		CreateStandingBook sccBook = new CreateStandingBook();//生成台账操作类(华宝先到先得钆差补票)
		sccBook.setYssPub(pub);
		sccBook.initData(this.startDate,this.endDate,this.tradeDate,this.portCodes,this.standingBookType);
		sccBook.doManageAll();
		
/*		//生成台帐子表数据
		//计算台帐应退款估值增值，并插入台帐关联表
		CreateSubStandingBook calBook = new CreateSubStandingBook(pub);
		calBook.createAccBookRefundMVBy(this.startDate,this.endDate,this.portCodes);*/
	}
	public void parseRowStr(String sRowStr)throws YssException {
		String[] reqAry = null;
		String[] reqDate = null;
		String buyDates = "";//已选的所有申赎日期
		try {
			if(sRowStr.equals("")){
				return;
			}
			//WEBLOGIC下\f\f报错  修改为\b\b 20130411 yeshenghong
			if(sRowStr.indexOf("\b\b") != -1){
				reqAry = sRowStr.split("\b\b");
			//---end WEBLOGIC下\f\f报错  修改为\b\b 20130411 yeshenghong
				if(reqAry.length >= 2){
					buyDates = reqAry[0];
					
					if(buyDates.indexOf(",") != -1){
						reqDate = buyDates.split(",");
						if(reqDate.length >= 2){
							this.startDate = YssFun.toDate(reqDate[0]);//开始的申赎日期
							this.endDate = YssFun.toDate(reqDate[1]);//结束的申赎日期
						}
						/**shashijie 2011-12-31 STORY 1789*/
						if (reqDate.length >= 3) {
							this.browDate = YssFun.toDate(reqDate[2]);//浏览日期
						}
						/**end*/
					}
					this.portCodes = reqAry[1]; // 已选组合代码
				}	
				if(reqAry.length >= 3){
					// 台账类型 S -- 赎回 B -- 申购 ALL -- 全部
					if(reqAry[2].equalsIgnoreCase("B")){
						this.standingBookType = "B"; 
					}else if(reqAry[2].equalsIgnoreCase("S")){
						this.standingBookType = "S"; 
					}
				}
				/**add---shashijie 2013-3-4 STORY 3693 退款日期处理,未考虑多组合情况*/
				if (sRowStr.indexOf("RefundDate")>0 && reqAry.length == 5 &&
						reqAry[reqAry.length-1].equals("RefundDate")) {
					this.standingBookType = reqAry[1];//台账类型
					this.portCodes = reqAry[2];//已选组合代码
					return;
				}
				/**end---shashijie 2013-3-4 STORY 3693*/
//				if(reqAry.length >= 4){
//					// ETF台账界面已选的证券代码
//					this.securityCodes = reqAry[3]; 
//				}//分页显示用, add by yeshenghong 20120530 
				if(reqAry.length >= 7){//modified by yeshenghong  ETF台帐分页显示
					this.securityCodes = reqAry[3]; 
					this.dataPageCount = Integer.parseInt(reqAry[4]);
					this.onePageSecs = Integer.parseInt(reqAry[5]);
					this.pageIndex = Integer.parseInt(reqAry[6]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析台帐相关数据出错！", e);
		}
	}

	public String getListViewData1() throws YssException {
		return null;
	}

	public String getListViewData2() throws YssException {
		return null;
	}

	public String getListViewData3() throws YssException {
		return null;
	}

	public String getListViewData4() throws YssException {
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		return null;
	}

	public String getTreeViewData1() throws YssException {
		return null;
	}

	public String getTreeViewData2() throws YssException {
		return null;
	}

	public String getTreeViewData3() throws YssException {
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		return null;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public String getSecurityCodes() {
		return securityCodes;
	}

	public void setSecurityCodes(String securityCodes) {
		this.securityCodes = securityCodes;
	}

	public String getStandingBookType() {
		return standingBookType;
	}

	public void setStandingBookType(String standingBookType) {
		this.standingBookType = standingBookType;
	}

	public java.util.Date getStartDate() {
		return startDate;
	}

	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}

	//add by fangjiang 2010.12.22 STORY #301 需在进行现金头寸预测表查询之前，先对以下数据进行检查
    public String checkReportBeforeSearch(String sReportType){
    	return "";
    }

	public Date getBrowDate() {
		return browDate;
	}

	public void setBrowDate(Date browDate) {
		this.browDate = browDate;
	}
	
	/**
	 * and by fangjiang 2013.01.08 STORY #3402
	 * 根据申赎确认日期获取申赎申请日期
	 * @param dDate 确认日期
	 * @return
	 * @throws YssException
	 */
	protected Date getBSDate() throws YssException {
		Date dTradeDate = null;
		StringBuffer buffer = new StringBuffer(100);
		ResultSet rs = null;
		try{
			buffer.append("SELECT  t.FTradeDate FROM ").append(pub.yssGetTableName("Tb_TA_Trade"))
					.append(" t WHERE t.FConfimDate = ").append(dbl.sqlDate(this.tradeDate))//确认日
					.append(" AND t.FPortCode in ( ").append(operSql.sqlCodes(portCodes) + " ) ")//组合
					.append(" AND t.FSellType in ('01','02')")//销售类型为：申购、赎回
					.append(" AND t.FCheckState = 1");
			rs = dbl.openResultSet(buffer.toString());
			buffer.delete(0, buffer.length());			
			if (rs.next()){
				dTradeDate = rs.getDate("FTradeDate");
			}
		}catch (Exception e) {
			throw new YssException("获取申购、赎回申请日期出错 ！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dTradeDate;		
	}
}
