package com.yss.main.etfoperation.etfaccbook.GcAndJqpj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.yss.commeach.EachExchangeHolidays;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.etfoperation.etfaccbook.CreateBookPretreatmentAdmin;
import com.yss.main.etfoperation.etfaccbook.CtlETFAccBook;
import com.yss.main.etfoperation.etfaccbook.PretValMktPriceAndExRate;
import com.yss.main.etfoperation.etfaccbook.timeandaverage.TempBook;
import com.yss.main.etfoperation.pojo.ETFEquityBean;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFSortedDetailBean;
import com.yss.main.etfoperation.pojo.ETFSubTradeBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailBean;
import com.yss.main.etfoperation.pojo.ETFTradeSettleDetailRefBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 此类做生成台账预处理，主要是生成交易明细和交易明细关联数据，
 * 保存到明细表（tb_etf_tradestldtl）和明细关联表（tb_etf_tradstldtlref）
 * @author fangjiang 2013.01.08 STORY #3402
 * fangjiang 2013.04.15 STORY #3848 ETF台账在处理权益时要用税前比率
 *
 */
public class CreateBookPre extends CtlETFAccBook{

	private ArrayList tradeSettleDetail = new ArrayList();//保存申赎明细数据	
	private HashMap mapRights = null;//当日权益信息	
	private BookDao bookDao = null;
	
	public CreateBookPre() {
		super();
	}
	
	/**解析前台传来的数据，实例化一些全局变量和类*/
	public void initData(Date tradeDate, Date bsDate, String portCodes, ETFParamSetBean paramSet,
			             PretValMktPriceAndExRate marketValue) {	
		super.initData(tradeDate, bsDate, portCodes, paramSet, marketValue);
		bookDao = new BookDao();
		bookDao.setYssPub(pub);
		bookDao.initData(tradeDate, bsDate, portCodes, paramSet, marketValue);
	}
	
	/**处理业务的入口方法*/
	public void doManageAll() throws YssException{
		try{	
			//删除数据
			deleteData();
			//处理申赎明细数据
			doBSDetail();
			//处理轧差补票数据
			doGcMakeUp();
			//处理权益数据
			doEquity();
			//处理非轧差补票数据
			doMakeUpExceptGc();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
	}
	
	public void deleteData() throws YssException{
		try{				
			if(null != this.bsDate){
				this.booPreAdmin.deleteGcMakeUp(this.bsDate, this.portCodes); //删除轧差补票数据
			}
			this.booPreAdmin.deleteEquity(this.tradeDate, this.portCodes); //删除权益数据
			this.booPreAdmin.deleteMakeUpExceptGc(this.tradeDate, this.portCodes); //删除非轧差补票数据
			if(null != this.bsDate){
				this.booPreAdmin.deleteBSDetail(this.bsDate, this.portCodes); //删除申赎明细数据
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}
	}
	
	/**
	 * 处理申赎明细数据,汇率要用申赎汇率
	 */
	public void doBSDetail() throws YssException{
		try{	
			if(null == this.bsDate){//如果没有申赎数据就不需要处理了
				return;
			}
			tradeSettleDetail.clear();
			
			List<ETFTradeSettleDetailBean> bOrsDetailList = this.bookDao.queryBOrSDetailList("");
			List<ETFSortedDetailBean> sortedBOrSDetailList = this.sortDetailList(bOrsDetailList, 1);
			List<ETFSortedDetailBean> sortedBDetailList = this.getSortedBOrSDetailList(sortedBOrSDetailList,"B");
			List<ETFSortedDetailBean> sortedSDetailList = this.getSortedBOrSDetailList(sortedBOrSDetailList,"S");
			doBDetail(sortedBDetailList);
			doSDetail(sortedSDetailList);
			
			booPreAdmin.insertBSDetail(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public List<ETFSortedDetailBean> sortDetailList(List<ETFTradeSettleDetailBean> detailList, int flag){
		if(null == detailList || detailList.isEmpty()){
			return null;
		}
		HashMap<String, ETFSortedDetailBean> sortedDetailMap = new LinkedHashMap<String, ETFSortedDetailBean>();
		Iterator<ETFTradeSettleDetailBean> iterator = detailList.iterator();
		while(iterator.hasNext()){
			ETFTradeSettleDetailBean next = iterator.next();
			Date bsDate = next.getBuyDate();
			String bs = next.getBs();
			String securityCode = next.getSecurityCode();			
			String key = bsDate + "\t" + bs + "\t" + securityCode; //modify by fangjiang 2013.05.20 要把申赎日期加到key上，否则处理多个申赎日期的权益时会有问题
			ETFSortedDetailBean sortedDetailBean = null;
			if(sortedDetailMap.containsKey(key)){
				sortedDetailBean = sortedDetailMap.get(key);
			}else{
				sortedDetailBean = new ETFSortedDetailBean();
				sortedDetailBean.setBsDate(bsDate);
				sortedDetailBean.setBs(bs);
				sortedDetailBean.setSecurityCode(securityCode);
				sortedDetailMap.put(key, sortedDetailBean);
			}
			if(" ".equalsIgnoreCase(next.getStockHolderCode())){
				sortedDetailBean.setParrentDetailBean(next);
			}else{
				List<ETFTradeSettleDetailBean> childDetailBeanList = null;
				if(null == sortedDetailBean.getChildDetailBeanList()){
					childDetailBeanList = new ArrayList<ETFTradeSettleDetailBean>();
				}else{
					childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
				}
				childDetailBeanList.add(next);
				sortedDetailBean.setChildDetailBeanList(childDetailBeanList);
				
				ETFTradeSettleDetailBean sumChildDetailBean = null;
				if(null == sortedDetailBean.getSumChildDetailBean()){
					sumChildDetailBean = new ETFTradeSettleDetailBean();
				}else{
					sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
				}
				if(1 == flag){
					this.sumChildForSS(sumChildDetailBean, next);
				}else if(2 == flag){
					this.sumChildForQY(sumChildDetailBean, next);
				}
				sortedDetailBean.setSumChildDetailBean(sumChildDetailBean);
			}
			iterator.remove();
		}
		ArrayList<ETFSortedDetailBean> sortedBOrSDetailList = new ArrayList<ETFSortedDetailBean>(sortedDetailMap.values());
		sortedBOrSDetailList.trimToSize();
		return sortedBOrSDetailList;		
	}
	
	public void sumChildForSS(ETFTradeSettleDetailBean sumChildDetailBean, ETFTradeSettleDetailBean next) {		
		sumChildDetailBean.setHReplaceCash(YssD.add(sumChildDetailBean.getHReplaceCash(), next.getHReplaceCash()));
		sumChildDetailBean.setHcReplaceCash(YssD.add(sumChildDetailBean.getHcReplaceCash(), next.getHcReplaceCash()));				
	}
	
	public void sumChildForQY(ETFTradeSettleDetailBean sumChildDetailBean, ETFTradeSettleDetailBean next) {
		ETFTradeSettleDetailRefBean refBeanOfSumChild = sumChildDetailBean.getTargetDelRef();
		if(null == refBeanOfSumChild){
			refBeanOfSumChild = new ETFTradeSettleDetailRefBean();
		}
		ETFTradeSettleDetailRefBean refBeanOfNext = next.getTargetDelRef();
		
		refBeanOfSumChild.setSumAmount(YssD.add(refBeanOfSumChild.getSumAmount(), refBeanOfNext.getSumAmount()));
		refBeanOfSumChild.setRealAmount(YssD.add(refBeanOfSumChild.getRealAmount(), refBeanOfNext.getRealAmount()));
		refBeanOfSumChild.setBbinterest(YssD.add(refBeanOfSumChild.getBbinterest(), refBeanOfNext.getBbinterest()));
		refBeanOfSumChild.setBbwarrantCost(YssD.add(refBeanOfSumChild.getBbwarrantCost(), refBeanOfNext.getBbwarrantCost()));	
		sumChildDetailBean.setTargetDelRef(refBeanOfSumChild);
	}
	
	public List<ETFSortedDetailBean> getSortedBOrSDetailList(List<ETFSortedDetailBean> sortedBOrSDetailList,String bs) throws YssException{
		if(null == sortedBOrSDetailList || sortedBOrSDetailList.isEmpty()){
			return null;
		}
		try{	
			List<ETFSortedDetailBean> list = new ArrayList<ETFSortedDetailBean>();
			
			for(ETFSortedDetailBean sortedBOrSDetailBean : sortedBOrSDetailList){
				if(bs.equalsIgnoreCase(sortedBOrSDetailBean.getBs())){
					list.add(sortedBOrSDetailBean);
				}
			}
			
			return list;		
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public void doBDetail(List<ETFSortedDetailBean> sortedBDetailList) throws YssException{
		if(null == sortedBDetailList || sortedBDetailList.isEmpty()){
			return;
		}
		try{
			for(ETFSortedDetailBean sortedDetailBean : sortedBDetailList){
				if(sortedBDetailList.indexOf(sortedDetailBean) == sortedBDetailList.size()-1){
					this.doBLast(sortedDetailBean,sortedBDetailList);					
				}else{
					this.doBPart(sortedDetailBean);
				}
				this.tradeSettleDetail.add(sortedDetailBean.getParrentDetailBean());
				this.tradeSettleDetail.addAll(sortedDetailBean.getChildDetailBeanList());
			}
			
			/*//先处理投资者的申购明细,最后一只券的投资者的替代金额倒轧,可退替代款重新计算
			LinkedHashMap<String, List<ETFTradeSettleDetailBean>> tzzBDetailMap = doTzzBDetail();
			//再处理证券的申购明细，证券的替代金额、可退替代款倒轧等于投资者之和
			doSecBDetail(tzzBDetailMap);		*/		
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public void doBPart(ETFSortedDetailBean sortedDetailBean) throws YssException{		
		try{	
			ETFTradeSettleDetailBean parrentDetailBean = sortedDetailBean.getParrentDetailBean();
			ETFTradeSettleDetailBean sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
			List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
			ETFTradeSettleDetailBean lastChildDetailBean = childDetailBeanList.get(childDetailBeanList.size()-1);
			
			parrentDetailBean.setHcReplaceCash(YssD.add(parrentDetailBean.getHcReplaceCash(), 
					YssD.sub(sumChildDetailBean.getHReplaceCash(), parrentDetailBean.getHReplaceCash())));	
			parrentDetailBean.setHReplaceCash(YssD.add(parrentDetailBean.getHReplaceCash(), 
					YssD.sub(sumChildDetailBean.getHReplaceCash(), parrentDetailBean.getHReplaceCash())));	
			
			lastChildDetailBean.setHcReplaceCash(YssD.add(lastChildDetailBean.getHcReplaceCash(), 
					YssD.sub(parrentDetailBean.getHcReplaceCash(), sumChildDetailBean.getHcReplaceCash())));	
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public void doBLast(ETFSortedDetailBean sortedDetailBean,List<ETFSortedDetailBean> sortedBDetailList) throws YssException{		
		try{
			HashMap<String, ETFTradeSettleDetailBean> sumChildBDetailMap = this.getSumChildBDetailMap(sortedBDetailList);
			HashMap<String, Double> sumChildBTdFromJsmxMap = this.bookDao.getSumChildBTdFromJsmx();
			
			ETFTradeSettleDetailBean parrentDetailBean = sortedDetailBean.getParrentDetailBean();
			ETFTradeSettleDetailBean sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
			List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
			ETFTradeSettleDetailBean lastChildDetailBean = childDetailBeanList.get(childDetailBeanList.size()-1);
			
			sumChildDetailBean.setHReplaceCash(0.0);
			sumChildDetailBean.setHcReplaceCash(0.0);
			for(ETFTradeSettleDetailBean childDetailBean : childDetailBeanList){
				String key = childDetailBean.getStockHolderCode();
				double sumChildTdFromJsmx = sumChildBTdFromJsmxMap.get(key);
				double sumChildTdFromDetail = sumChildBDetailMap.get(key).getHReplaceCash();
				childDetailBean.setHReplaceCash(YssD.add(childDetailBean.getHReplaceCash(), 
	                       YssD.sub(sumChildTdFromJsmx, sumChildTdFromDetail)));
				childDetailBean.setHcReplaceCash(YssD.add(childDetailBean.getHcReplaceCash(), 
	                        YssD.sub(sumChildTdFromJsmx, sumChildTdFromDetail)));	
				
				sumChildDetailBean.setHReplaceCash(YssD.add(sumChildDetailBean.getHReplaceCash(), childDetailBean.getHReplaceCash()));
				sumChildDetailBean.setHcReplaceCash(YssD.add(sumChildDetailBean.getHcReplaceCash(), childDetailBean.getHcReplaceCash()));
			}
			
			parrentDetailBean.setHcReplaceCash(YssD.add(parrentDetailBean.getHcReplaceCash(), 
					YssD.sub(sumChildDetailBean.getHReplaceCash(), parrentDetailBean.getHReplaceCash())));
			parrentDetailBean.setHReplaceCash(YssD.add(parrentDetailBean.getHReplaceCash(), 
					YssD.sub(sumChildDetailBean.getHReplaceCash(), parrentDetailBean.getHReplaceCash())));	
				
			lastChildDetailBean.setHcReplaceCash(YssD.add(lastChildDetailBean.getHcReplaceCash(), 
					YssD.sub(parrentDetailBean.getHcReplaceCash(), sumChildDetailBean.getHcReplaceCash())));					
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public HashMap<String, ETFTradeSettleDetailBean> getSumChildBDetailMap(List<ETFSortedDetailBean> sortedBDetailList) throws YssException{		
		try{
			HashMap<String, ETFTradeSettleDetailBean> sumChildBDetailMap = new HashMap<String, ETFTradeSettleDetailBean>();
			for(ETFSortedDetailBean sortedDetailBean : sortedBDetailList){
				List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
				for(ETFTradeSettleDetailBean childDetailBean : childDetailBeanList){
					String key = childDetailBean.getStockHolderCode();
					if(sumChildBDetailMap.containsKey(key)){
						ETFTradeSettleDetailBean sumChildBDetailBean = sumChildBDetailMap.get(key);
						sumChildBDetailBean.setHReplaceCash(YssD.add(
								sumChildBDetailBean.getHReplaceCash(), 
								childDetailBean.getHReplaceCash()));
					}else{
						ETFTradeSettleDetailBean childBDetailBeanClone = (ETFTradeSettleDetailBean)childDetailBean.clone();
						sumChildBDetailMap.put(key, childBDetailBeanClone);
					}
				}
			}
			
			return sumChildBDetailMap;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public LinkedHashMap<String, List<ETFTradeSettleDetailBean>> doTzzBDetail() throws YssException{
		try{
			tradeSettleDetail.clear();			
			
			HashMap<String, ETFTradeSettleDetailBean> sumTzzBDetailMap = new HashMap<String, ETFTradeSettleDetailBean>();
			LinkedHashMap<String, List<ETFTradeSettleDetailBean>> tzzBDetailMap = bookDao.queryTzzBOrSDetail("B");			
			List<ETFTradeSettleDetailBean> tzzBDetailList = null;
			Iterator<String> tzzBDetailIterator = tzzBDetailMap.keySet().iterator();
		    while(tzzBDetailIterator.hasNext()){
				String secKey = (String)tzzBDetailIterator.next();		
				tzzBDetailList = tzzBDetailMap.get(secKey);
				for(ETFTradeSettleDetailBean tzzBDetailBean : tzzBDetailList){
					String tzzKey = tzzBDetailBean.getStockHolderCode();
					if(sumTzzBDetailMap.containsKey(tzzKey)){
						ETFTradeSettleDetailBean sumTzzBDetailBean = sumTzzBDetailMap.get(tzzKey);
						sumTzzBDetailBean.setHReplaceCash(YssD.add(
								sumTzzBDetailBean.getHReplaceCash(), 
								tzzBDetailBean.getHReplaceCash()));
					}else{
						ETFTradeSettleDetailBean tzzBDetailBeanClone = (ETFTradeSettleDetailBean)tzzBDetailBean.clone();
						sumTzzBDetailMap.put(tzzKey, tzzBDetailBeanClone);
					}
				}
				tradeSettleDetail.addAll(tzzBDetailList);
			}
		    this.gcTzzOfLastSec(tzzBDetailList, sumTzzBDetailMap);
		    
			booPreAdmin.insertBSDetail(tradeSettleDetail);
			
			return tzzBDetailMap;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void gcTzzOfLastSec(List<ETFTradeSettleDetailBean> tzzBDetailList, HashMap<String, ETFTradeSettleDetailBean> sumTzzBDetailMap) throws YssException{		
		try{
			HashMap<String, Double> tzzSumTdFromJsmxMap = this.bookDao.getSumChildBTdFromJsmx();
			for(ETFTradeSettleDetailBean tzzBDetailBean : tzzBDetailList){
				String key = tzzBDetailBean.getStockHolderCode();
				double tzzSumTdFromJsmx = tzzSumTdFromJsmxMap.get(key);
				double tzzSumTdFromDetail = sumTzzBDetailMap.get(key).getHReplaceCash();
				tzzBDetailBean.setHReplaceCash(YssD.add(tzzBDetailBean.getHReplaceCash(), 
	                       YssD.sub(tzzSumTdFromJsmx, tzzSumTdFromDetail)));
				tzzBDetailBean.setHcReplaceCash(YssD.add(tzzBDetailBean.getHcReplaceCash(), 
	                        YssD.sub(tzzSumTdFromJsmx, tzzSumTdFromDetail)));	
			}	
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void doSecBDetail(LinkedHashMap<String, List<ETFTradeSettleDetailBean>> tzzBDetailMap) throws YssException{
		try{
			tradeSettleDetail.clear();			
			
			HashMap<String, ETFTradeSettleDetailBean> secBDetailMap = this.bookDao.querySecBOrSDetailMap("B");
			Iterator<String> tzzBDetailIterator = tzzBDetailMap.keySet().iterator();
		    while(tzzBDetailIterator.hasNext()){
		    	String secKey = (String)tzzBDetailIterator.next();
		    	ETFTradeSettleDetailBean secBDetailBean = secBDetailMap.get(secKey);
		    	secBDetailBean.setHReplaceCash(0.0);
		    	secBDetailBean.setHcReplaceCash(0.0);
		    	List<ETFTradeSettleDetailBean> tzzBDetailList = tzzBDetailMap.get(secKey);
		    	for(ETFTradeSettleDetailBean tzzBDetailBean : tzzBDetailList){
		    		secBDetailBean.setHReplaceCash(YssD.add(secBDetailBean.getHReplaceCash(), tzzBDetailBean.getHReplaceCash()));
		    		secBDetailBean.setHcReplaceCash(YssD.add(secBDetailBean.getHcReplaceCash(), tzzBDetailBean.getHcReplaceCash()));					
				}
		    	tradeSettleDetail.add(secBDetailBean);
		    }
			  
			booPreAdmin.insertBSDetail(tradeSettleDetail);
			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void doSDetail(List<ETFSortedDetailBean> sortedSDetailList) throws YssException{
		if(null == sortedSDetailList || sortedSDetailList.isEmpty()){
			return;
		}
		try{	
			for(ETFSortedDetailBean sortedDetailBean : sortedSDetailList){				
				this.doSAll(sortedDetailBean);	
				
				this.tradeSettleDetail.add(sortedDetailBean.getParrentDetailBean());
				this.tradeSettleDetail.addAll(sortedDetailBean.getChildDetailBeanList());
			}
			/*//先处理证券的赎回明细
			List<ETFTradeSettleDetailBean> secSDetail = doSecSDetail();
			//再处理投资者的赎回明细，最后一个投资者的替代金额、可退替代款倒轧
			doTzzSDetail(secSDetail);		*/		
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public void doSAll(ETFSortedDetailBean sortedDetailBean) throws YssException{		
		try{	
			ETFTradeSettleDetailBean parrentDetailBean = sortedDetailBean.getParrentDetailBean();
			ETFTradeSettleDetailBean sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
			List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
			ETFTradeSettleDetailBean lastChildDetailBean = childDetailBeanList.get(childDetailBeanList.size()-1);
			
			lastChildDetailBean.setHReplaceCash(YssD.add(lastChildDetailBean.getHReplaceCash(), 
					YssD.sub(parrentDetailBean.getHReplaceCash(), sumChildDetailBean.getHReplaceCash())));
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}		
	}
	
	public List<ETFTradeSettleDetailBean> doSecSDetail() throws YssException{
		try{
			tradeSettleDetail.clear();			
			
			//获得证券的赎回明细
			List<ETFTradeSettleDetailBean> secSDetailList = bookDao.querySecBOrSDetailList("S");
			
			tradeSettleDetail.addAll(secSDetailList);   
			booPreAdmin.insertBSDetail(tradeSettleDetail);
			
			return secSDetailList;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}

	public void doTzzSDetail(List<ETFTradeSettleDetailBean> secSDetailList) throws YssException{
		try{
			tradeSettleDetail.clear();			
			
			//获得投资者的申赎明细
			LinkedHashMap<String, List<ETFTradeSettleDetailBean>> tzzSDetailMap = bookDao.queryTzzBOrSDetail("S");
			//最后一个投资者的替代金额、可退替代款倒轧			
		    gcLastTzzDetail(secSDetailList, tzzSDetailMap);
		    
		    Iterator<String> tzzSDetailIterator = tzzSDetailMap.keySet().iterator();
		    while(tzzSDetailIterator.hasNext()){
				String key = (String)tzzSDetailIterator.next();		
				List<ETFTradeSettleDetailBean> tzzSDetailList = tzzSDetailMap.get(key);
				tradeSettleDetail.addAll(tzzSDetailList);
			}
		    booPreAdmin.insertBSDetail(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public void gcLastTzzDetail(List<ETFTradeSettleDetailBean> secSDetailList,HashMap<String, List<ETFTradeSettleDetailBean>> tzzSDetailMap) throws YssException{		
		try{	
			for(ETFTradeSettleDetailBean secSDetailBean : secSDetailList){
				double sumTzzTd = 0.0;
				double sumTzzKtTd = 0.0;
				String key = secSDetailBean.getBs() + "\t" + secSDetailBean.getSecurityCode();
				List<ETFTradeSettleDetailBean> tzzSDetailList = tzzSDetailMap.get(key);
				for(ETFTradeSettleDetailBean tzzSDetailBean : tzzSDetailList){
					sumTzzTd += tzzSDetailBean.getHReplaceCash();
				    sumTzzKtTd += tzzSDetailBean.getHcReplaceCash();
				}
				ETFTradeSettleDetailBean tzzSDetailBean = tzzSDetailList.get(tzzSDetailList.size()-1);
				tzzSDetailBean.setHReplaceCash(YssD.add(tzzSDetailBean.getHReplaceCash(), 
	                       					    YssD.sub(secSDetailBean.getHReplaceCash(), sumTzzTd)));
				tzzSDetailBean.setHcReplaceCash(YssD.add(tzzSDetailBean.getHcReplaceCash(), 
	                                             YssD.sub(secSDetailBean.getHcReplaceCash(), sumTzzKtTd)));				
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public List<ETFTradeSettleDetailBean> getSecBOrSDetailList(List<ETFTradeSettleDetailBean> secBSDetailList, String bs) throws YssException{
		try{	
			List<ETFTradeSettleDetailBean> secBOrSDetailList = new ArrayList<ETFTradeSettleDetailBean>();
			
			for(ETFTradeSettleDetailBean secBOrSDetailBean : secBSDetailList){
				if(bs.equalsIgnoreCase(secBOrSDetailBean.getBs())){
					secBOrSDetailList.add(secBOrSDetailBean);
				}
			}
			
			return secBOrSDetailList;			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
		
	/**
	 * 处理权益数据
	 * 涉及到的汇率暂时用申赎汇率
	 */
	public void doEquity() throws YssException{
		try{
			this.doHolidayEquity();
			this.doCommonEquity();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	/**
	 * T到T+1之间，境内为节假日，境外有权益的情况
	 * @throws YssException
	 */
	public void doHolidayEquity() throws YssException{
		try{
			if(null == this.bsDate){
				return;
			}
			int days = YssFun.dateDiff(YssFun.addDay(this.bsDate, 1),YssFun.addDay(this.tradeDate, -1));
			for(int i=0;i<=days;i++){//循环日期
				tradeSettleDetail.clear();				
				doRightRef(1,YssFun.addDay(YssFun.addDay(this.bsDate, 1), i));
				booPreAdmin.insertBSDetailRef(tradeSettleDetail);
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void doCommonEquity() throws YssException{
		try{
			tradeSettleDetail.clear();
			//权益处理(送股、分红、配股权证)
			doRightRef(0,this.tradeDate);
			//重算配股权证价值
			valRightsIssue();
			//保存权益数据
			booPreAdmin.insertBSDetailRef(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void doRightRef(int flag, Date exrightdate) throws YssException {
		try{
			String lastDayNumHD = (String)this.paramSet.getHoildaysRela().get("lastestdealdaynum");
			int lastDayNum = this.paramSet.getLastestDealDayNum(); //补票完成
			//最早申赎日期
			Date bsBeginDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate,lastDayNumHD,-lastDayNum,"", -1));
			//最晚申赎日期
			Date bsEndDate = YssFun.addDay(this.tradeDate, -1);	
			if(1 == flag){
				bsBeginDate = this.bsDate;
				bsEndDate = this.bsDate;
			}
			
			List<ETFTradeSettleDetailBean> detailList = this.bookDao.queryDetailForEquity(bsBeginDate, bsEndDate);
			Iterator<ETFTradeSettleDetailBean> iterator = detailList.iterator();
			while(iterator.hasNext()){
				ETFTradeSettleDetailBean detailBean = iterator.next();
				doEveryDetailBeanForEquity(detailBean, exrightdate);
				ETFTradeSettleDetailRefBean detailRefBean = detailBean.getTargetDelRef();
				if(detailRefBean.getSumAmount() <=0 && detailRefBean.getRealAmount() <=0 && 
				   detailRefBean.getBbinterest() <=0 && detailRefBean.getBbwarrantCost() <=0){
					iterator.remove();
				}
			}
			List<ETFSortedDetailBean> sortedEquityDetailList = this.sortDetailList(detailList, 2);
			if(null == sortedEquityDetailList || sortedEquityDetailList.isEmpty()){
				return;
			}
			for(ETFSortedDetailBean sortedDetailBean : sortedEquityDetailList){
				this.doEqutityAll(sortedDetailBean);
				this.tradeSettleDetail.add(sortedDetailBean.getParrentDetailBean());
				this.tradeSettleDetail.addAll(sortedDetailBean.getChildDetailBeanList());
			}
		}catch (Exception e) {
			throw new YssException("处理台帐权益出错！",e);
		}finally{
			
		}
	}
	
	public void doEveryDetailBeanForEquity(ETFTradeSettleDetailBean detailBean, Date exrightdate) throws YssException {
		HashMap<String, ETFEquityBean> equityMap = this.bookDao.queryEquityInfo(exrightdate);
		try{
			String key = detailBean.getSecurityCode();
			ETFEquityBean equityBean = equityMap.get(key);
			if(null == equityBean){
				return;
			}
			ETFTradeSettleDetailRefBean detailRefBean = detailBean.getTradeSettleDelRef();
			ETFTradeSettleDetailRefBean detailTargetRefBean = detailBean.getTargetDelRef();
			double sgTaxRatio = equityBean.getSgTaxRatio();
			double fhTaxRatio = equityBean.getFhTaxRatio();
			double pgTaxRatio = equityBean.getPgTaxRatio();
			double zgHq = equityBean.getZgHq();
			double pgPrice = equityBean.getPgPrice();
			double pgHq = equityBean.getPgHq();
			if(sgTaxRatio > 0){
				double sumAmount = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailRefBean.getSumAmount()), 
						                    sgTaxRatio);
				sumAmount = YssD.round(sumAmount, 0);
				detailTargetRefBean.setSumAmount(sumAmount);
				double remainAmount = YssD.add(detailBean.getReplaceAmount(), detailRefBean.getRealAmount(), -detailRefBean.getMakeUpAmount());
				double realAmount = YssD.mul(remainAmount, sgTaxRatio);
				realAmount = YssD.round(realAmount, 0);
				detailTargetRefBean.setRealAmount(realAmount);
			}
			if(fhTaxRatio > 0){
				double interest = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailRefBean.getSumAmount()), 
										   fhTaxRatio);
				double bbInterest = YssD.mul(interest, detailTargetRefBean.getRightRate());
				bbInterest = YssD.round(bbInterest, 2);
				detailTargetRefBean.setBbinterest(bbInterest);
			}
			if(pgTaxRatio > 0){
				 double warrantCost = 0.0;
				 double bbWarrantCost = 0.0;
				 if(pgHq > 0){
					 warrantCost = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailRefBean.getSumAmount()), 
							                pgTaxRatio,
							                zgHq);
				 }else{
					 if(pgPrice < zgHq){
						 warrantCost = YssD.mul(YssD.add(detailBean.getReplaceAmount(), detailRefBean.getSumAmount()), 
								                pgTaxRatio,
					                            YssD.sub(zgHq, pgPrice));
					 }
				 }
				 bbWarrantCost = YssD.mul(warrantCost, detailTargetRefBean.getRightRate());
				 bbWarrantCost = YssD.round(bbWarrantCost, 2);
				 detailTargetRefBean.setBbwarrantCost(bbWarrantCost);
				 //此次配股权证之前的总数量，方便之后配股权证价值的更新
				 detailTargetRefBean.setRemaindAmount(detailRefBean.getSumAmount());
			}
			detailTargetRefBean.setRefNum(
					    YssFun.formatDate(this.tradeDate, "yyyyMMdd") + 
						YssFun.formatDate(this.tradeDate, "yyyyMMdd"));
		}catch (Exception e) {
			throw new YssException("处理台帐权益出错！",e);
		}finally{
			
		}
	}
	
	public void doEqutityAll(ETFSortedDetailBean sortedDetailBean){
		ETFTradeSettleDetailBean parrentDetailBean = sortedDetailBean.getParrentDetailBean();
		ETFTradeSettleDetailRefBean parrentDetailRefBean = parrentDetailBean.getTargetDelRef();
		ETFTradeSettleDetailBean sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
		ETFTradeSettleDetailRefBean sumChildDetailRefBean = sumChildDetailBean.getTargetDelRef();
		List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
		ETFTradeSettleDetailBean lastChildDetailBean = childDetailBeanList.get(childDetailBeanList.size()-1);
		ETFTradeSettleDetailRefBean lastChildDetailRefBean = lastChildDetailBean.getTargetDelRef();
		
		lastChildDetailRefBean.setSumAmount(YssD.add(lastChildDetailRefBean.getSumAmount(), 
				YssD.sub(parrentDetailRefBean.getSumAmount(), sumChildDetailRefBean.getSumAmount())));
		lastChildDetailRefBean.setRealAmount(YssD.add(lastChildDetailRefBean.getRealAmount(), 
				YssD.sub(parrentDetailRefBean.getRealAmount(), sumChildDetailRefBean.getRealAmount())));
		lastChildDetailRefBean.setBbinterest(YssD.add(lastChildDetailRefBean.getBbinterest(), 
				YssD.sub(parrentDetailRefBean.getBbinterest(), sumChildDetailRefBean.getBbinterest())));
		lastChildDetailRefBean.setBbwarrantCost(YssD.add(lastChildDetailRefBean.getBbwarrantCost(), 
				YssD.sub(parrentDetailRefBean.getBbwarrantCost(), sumChildDetailRefBean.getBbwarrantCost())));	
	}
	
	/**
	 * 根据最新行情和汇率重算权证价值
	 * @param dDate
	 * @throws YssException
	 */
	public void valRightsIssue() throws YssException {				
		try{
			String lastDayNumHD = (String)this.paramSet.getHoildaysRela().get("lastestdealdaynum");
			int lastDayNum = this.paramSet.getLastestDealDayNum(); //补票完成
			//最早申赎日期
			Date bsBeginDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate,lastDayNumHD,-lastDayNum,"", -1));
			//最晚申赎日期
			Date bsEndDate = YssFun.addDay(this.tradeDate, -1);	
			
			List<ETFTradeSettleDetailBean> detailList = this.bookDao.queryDetailForUpdateQz(bsBeginDate, bsEndDate);
			List<ETFSortedDetailBean> sortedEquityDetailList = this.sortDetailList(detailList, 2);
			if(null == sortedEquityDetailList || sortedEquityDetailList.isEmpty()){
				return;
			}
			for(ETFSortedDetailBean sortedDetailBean : sortedEquityDetailList){
				this.doUpdateQzAll(sortedDetailBean);
				this.tradeSettleDetail.add(sortedDetailBean.getParrentDetailBean());
				this.tradeSettleDetail.addAll(sortedDetailBean.getChildDetailBeanList());
			}			
		}catch (Exception e) {
			throw new YssException("计算台帐权证价值出错！",e);
		}finally{
			
		}	
	}
	
	public void doUpdateQzAll(ETFSortedDetailBean sortedDetailBean){
		ETFTradeSettleDetailBean parrentDetailBean = sortedDetailBean.getParrentDetailBean();
		ETFTradeSettleDetailRefBean parrentDetailRefBean = parrentDetailBean.getTargetDelRef();
		ETFTradeSettleDetailBean sumChildDetailBean = sortedDetailBean.getSumChildDetailBean();
		ETFTradeSettleDetailRefBean sumChildDetailRefBean = sumChildDetailBean.getTargetDelRef();
		List<ETFTradeSettleDetailBean> childDetailBeanList = sortedDetailBean.getChildDetailBeanList();
		ETFTradeSettleDetailBean lastChildDetailBean = childDetailBeanList.get(childDetailBeanList.size()-1);
		ETFTradeSettleDetailRefBean lastChildDetailRefBean = lastChildDetailBean.getTargetDelRef();
		
		lastChildDetailRefBean.setBbwarrantCost(YssD.add(lastChildDetailRefBean.getBbwarrantCost(), 
				YssD.sub(parrentDetailRefBean.getBbwarrantCost(), sumChildDetailRefBean.getBbwarrantCost())));	
	}
	
	/**
	 * 处理补票数据
	 */
	public void doMakeUpExceptGc() throws YssException{
		try{	
			//以下T指申赎日期
			//T日轧差补票
			//doGcMakeUp(dDate);
			//T+1（共同工作日）、T+2（共同工作日）加权平均补票，需用到补票开始日和补票完成日，汇率要用补票汇率
			doJqpjMakeUp();
			//T+2（共同工作日）强制补票，需用到强制处理日，汇率要用补票汇率
			doQzMakeUp();
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	/**
	 * T日轧差补票，仅申购或仅赎回时不需处理
	 * 轧差补票要在权益之前处理，行情、汇率（估值汇率）用的是T日的
	 * 如果是T+1日轧差补票，则行情、汇率（估值汇率）用的是T+1日的，且要在权益之后处理
	 * @param dDate
	 * @throws YssException
	 */
	public void doGcMakeUp() throws YssException{
		try{
			if(null == this.bsDate){//如果没有申赎数据就不需要处理了
				return;
			}
			tradeSettleDetail.clear();
			
			//证券轧差补票
			List<ETFTradeSettleDetailBean> secGcMakeUpList = doSecGcMakeUp();
			//投资者轧差补票，最后一个投资者的可退替代款、应付替代款倒轧
			doTzzGcMakeUp(secGcMakeUpList);
					
			booPreAdmin.insertBSDetailRef(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public List<ETFTradeSettleDetailBean> doSecGcMakeUp() throws YssException{
		try{	
			//查询证券的申购明细
			HashMap<String, ETFTradeSettleDetailBean> secBDetailMap = bookDao.querySecBOrSDetailForGc("B");		
			//查询证券的赎回明细
			HashMap<String, ETFTradeSettleDetailBean> secSDetailMap = bookDao.querySecBOrSDetailForGc("S");		
			//根据证券的申购明细、证券的赎回明细获得证券的轧差补票数据
			List<ETFTradeSettleDetailBean> secGcMakeUpList = getSecGcMakeUp(secBDetailMap, secSDetailMap);
			if(!secGcMakeUpList.isEmpty()){
				tradeSettleDetail.addAll(secGcMakeUpList);	
			}
			return secGcMakeUpList;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public List<ETFTradeSettleDetailBean> getSecGcMakeUp(HashMap<String, ETFTradeSettleDetailBean> secBDetailMap, 
														  HashMap<String, ETFTradeSettleDetailBean> secSDetailMap) throws YssException{
		try{	
			List<ETFTradeSettleDetailBean> secGcMakeUpList = new ArrayList<ETFTradeSettleDetailBean>();
			if(secBDetailMap.isEmpty() || secSDetailMap.isEmpty()){
				return secGcMakeUpList;
			}
			Iterator<String> secBDetailIterator = secBDetailMap.keySet().iterator();
			while(secBDetailIterator.hasNext()){
				String secKey = secBDetailIterator.next();
				ETFTradeSettleDetailBean secBDetailBean = secBDetailMap.get(secKey);
				ETFTradeSettleDetailBean secSDetailBean = secSDetailMap.get(secKey);
				double bReplaceAmount = secBDetailBean.getReplaceAmount();
				double sReplaceAmount = secSDetailBean.getReplaceAmount();
				if(bReplaceAmount >= sReplaceAmount){
					secBDetailBean.getTargetDelRef().setMakeUpAmount(sReplaceAmount);
					secSDetailBean.getTargetDelRef().setMakeUpAmount(sReplaceAmount);
				}else{
					secBDetailBean.getTargetDelRef().setMakeUpAmount(bReplaceAmount);
					secSDetailBean.getTargetDelRef().setMakeUpAmount(bReplaceAmount);
				}
				setMakeUpData(secBDetailBean, "gc");
				setMakeUpData(secSDetailBean, "gc");
				
				secGcMakeUpList.add(secBDetailBean);
				secGcMakeUpList.add(secSDetailBean);
			}
			return secGcMakeUpList;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public void setMakeUpData(ETFTradeSettleDetailBean detailBean, String refNum) throws YssException {
		double tdje = 0.0; //替代金额
		double zkttdk = 0.0; //总的可退替代款
		double yckttdk = 0.0; //已冲可退替代款
		double kttdk = 0.0; //可退替代款
		double yftdk = 0.0; //应付替代款
		double zsl = 0.0;  //总数量
		double dbsl = 0.0; //待补数量
		double bpsl = 0.0; //补票数量
		double zpx = 0.0;  //总派息
		double zqzjz = 0.0; //总权证价值
		double dwcb = 0.0; //单位成本
		double rate = 0.0; //汇率
		try{
			tdje = detailBean.getHReplaceCash();
			dbsl = this.getDbsl(detailBean);
			bpsl = detailBean.getTargetDelRef().getMakeUpAmount();			
			dwcb = detailBean.getTargetDelRef().getUnitCost();		
			rate = detailBean.getTargetDelRef().getExchangeRate();
			zkttdk = detailBean.getHcReplaceCash();
			yckttdk = detailBean.getTradeSettleDelRef().getHcReplaceCash();
			zsl = YssD.add(detailBean.getReplaceAmount(), detailBean.getTradeSettleDelRef().getSumAmount());
			zpx = detailBean.getTradeSettleDelRef().getBbinterest();
			zqzjz = detailBean.getTradeSettleDelRef().getBbwarrantCost();
			
			if(dbsl == bpsl){
				if("S".equalsIgnoreCase(detailBean.getBs())){
					kttdk = YssD.sub(tdje, yckttdk);
				}else{
					kttdk = YssD.sub(zkttdk, yckttdk);
				}				
			}else{
				if("S".equalsIgnoreCase(detailBean.getBs())){
					kttdk = YssD.mul(tdje, YssD.div(bpsl, zsl));
				}else{
					kttdk = YssD.mul(zkttdk, YssD.div(bpsl, zsl));					
				}
				kttdk = YssD.round(kttdk,2);
			}
			if("gc".equalsIgnoreCase(refNum) || "99".equalsIgnoreCase(refNum)){
				yftdk = YssD.sub(YssD.mul(bpsl, YssD.div(YssD.sub(tdje, zpx, zqzjz), zsl)), YssD.round(YssD.mul(bpsl,dwcb,rate), 2));
			}else{
				yftdk = YssD.sub(YssD.mul(bpsl, YssD.div(YssD.sub(tdje, zpx, zqzjz), zsl)), YssD.round(YssD.mul(bpsl,dwcb,rate), 2));
			}		
			yftdk = YssD.round(yftdk, 2);
			if("S".equalsIgnoreCase(detailBean.getBs())){
				yftdk = YssD.mul(yftdk, -1);
			}
			
			detailBean.getTargetDelRef().setHcReplaceCash(kttdk);
			detailBean.getTargetDelRef().setHpReplaceCash(yftdk);
			detailBean.getTargetDelRef().setRefNum(refNum);
			Date bsDate = detailBean.getBuyDate();
			Date bRefundDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(bsDate, (String)this.paramSet.getHoildaysRela().get("sgdealreplace"),
					this.paramSet.getISGDealReplace(), (String)this.paramSet.getHoildaysRela().get("sgdealreplace2"), this.paramSet.getiSGDealReplace2()));
			Date sRefundDate =  YssFun.toDate(this.paramSetAdmin.getWorkDay(bsDate, (String)this.paramSet.getHoildaysRela().get("shdealreplace"),
					this.paramSet.getISHDealReplace(), (String)this.paramSet.getHoildaysRela().get("shdealreplace2"), this.paramSet.getiSHDealReplace2()));
			if("B".equalsIgnoreCase(detailBean.getBs())){
				detailBean.getTargetDelRef().setRefundDate(bRefundDate);
				detailBean.getTargetDelRef().setDataDirection("1");
			}else{
				detailBean.getTargetDelRef().setRefundDate(sRefundDate);
				detailBean.getTargetDelRef().setDataDirection("-1");
			}
			//原币补票成本
			if("gc".equalsIgnoreCase(refNum) || "99".equalsIgnoreCase(refNum)){
				detailBean.getTargetDelRef().setoMakeUpCost(YssD.round(YssD.mul(bpsl, dwcb), 2));
			}else{
				detailBean.getTargetDelRef().setoMakeUpCost(detailBean.getTargetDelRef().getFz());
			}
			//本币补票成本
			detailBean.getTargetDelRef().sethMakeUpCost(YssD.round(YssD.mul(bpsl, dwcb, rate), 2));
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public double getDbsl(ETFTradeSettleDetailBean detailBean) throws YssException {
		try{				
			double dbsl = 0.0; //待补数量
			dbsl = YssD.add(detailBean.getReplaceAmount(), 
					        detailBean.getTradeSettleDelRef().getRealAmount(), 
					        -detailBean.getTradeSettleDelRef().getMakeUpAmount());
			return dbsl;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}			
	}
	
	public void doTzzGcMakeUp(List<ETFTradeSettleDetailBean> secGcMakeUpList) throws YssException{
		try{	
			if(secGcMakeUpList.isEmpty()){
				return;
			}
			//查询投资者的申赎明细
			HashMap<String, List<ETFTradeSettleDetailBean>> tzzGcMakeUpMap = bookDao.queryTzzBSDetailForGc();			 
			for(ETFTradeSettleDetailBean secGcMakeUpBean : secGcMakeUpList){				
				String key = secGcMakeUpBean.getBs() + "\t" + secGcMakeUpBean.getSecurityCode();
				List<ETFTradeSettleDetailBean> tzzGcMakeUpList = tzzGcMakeUpMap.get(key);
				this.doTzzMakeUp(secGcMakeUpBean, tzzGcMakeUpList);
			}
			
			Iterator<String> tzzGcMakeUpIterator = tzzGcMakeUpMap.keySet().iterator();
		    while(tzzGcMakeUpIterator.hasNext()){
				String secKey = (String)tzzGcMakeUpIterator.next();		
				List<ETFTradeSettleDetailBean> tzzGcMakeUpList = tzzGcMakeUpMap.get(secKey);
				tradeSettleDetail.addAll(tzzGcMakeUpList);
			}			
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
			
		}	
	}
	
	public void doJqpjMakeUp() throws YssException{
		try{	
			tradeSettleDetail.clear();
			
			String dealDayNumHD = (String)this.paramSet.getHoildaysRela().get("dealdaynum");
			int dealDayNum = this.paramSet.getDealDayNum(); //补票完成
			int beginSupply = this.paramSet.getBeginSupply(); //补票开始
			int supplyTs = dealDayNum - beginSupply; //补票天数
			Date date = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate,dealDayNumHD,0,"",-1));
			if(YssFun.dateDiff(this.tradeDate,date) != 0){
				return;
			}
			//最早申赎日期
			Date bsBeginDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate,dealDayNumHD,-dealDayNum,"", -1));
			//最晚申赎日期
			Date bsEndDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate,dealDayNumHD,-supplyTs,"", -1));
					
			//证券加权平均补票
			doSecAndTzzJqpjMakeUp(bsBeginDate, bsEndDate, supplyTs+1);
			
			booPreAdmin.insertBSDetailRef(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}			
	}
	
	public void doSecAndTzzJqpjMakeUp(Date bsBeginDate, Date bsEndDate, int maxBpcs) throws YssException{
		try{
			HashMap<String, ETFSubTradeBean> bpDataMap = this.bookDao.queryBPData();
			HashMap<Date, List<ETFTradeSettleDetailBean>> jqpjMakeUpMap = this.bookDao.queryBSDetailForJqpj(bsBeginDate, bsEndDate);
			Iterator<Date> jqpjMakeUpIterator = jqpjMakeUpMap.keySet().iterator();
			while(jqpjMakeUpIterator.hasNext()){				
				Date key = jqpjMakeUpIterator.next();
				int bpcs = this.getBpcs(bsBeginDate, key, maxBpcs);
				List<ETFTradeSettleDetailBean> jqpjMakeUpList = jqpjMakeUpMap.get(key);
				List<ETFTradeSettleDetailBean> secJqpjMakeUpList = this.getSecJqpjMakeUpList(jqpjMakeUpList);
				HashMap<String, List<ETFTradeSettleDetailBean>> tzzJqpjMakeUpMap = this.getTzzJqpjMakeUpMap(jqpjMakeUpList);
				for(ETFTradeSettleDetailBean secJqpjMakeUpBean : secJqpjMakeUpList){
					String strkey = secJqpjMakeUpBean.getBs() + "\t" + secJqpjMakeUpBean.getSecurityCode();
					ETFSubTradeBean bpDataBean = bpDataMap.get(strkey);
					List<ETFTradeSettleDetailBean> tzzJqpjMakeUpList = tzzJqpjMakeUpMap.get(strkey); 
					if(null == bpDataBean || 0 == bpDataBean.getTradeAmount()){
						continue;
					}
					double bpsl = bpDataBean.getTradeAmount();
					double dbsl = this.getDbsl(secJqpjMakeUpBean);
					if(dbsl <= bpsl){
						secJqpjMakeUpBean.getTargetDelRef().setMakeUpAmount(dbsl);
						bpDataBean.setTradeAmount(YssD.sub(bpsl, dbsl));
					}else{
						secJqpjMakeUpBean.getTargetDelRef().setMakeUpAmount(bpsl);
						bpDataBean.setTradeAmount(0);
					}
					secJqpjMakeUpBean.getTargetDelRef().setUnitCost(YssD.div(bpDataBean.getFz(), bpDataBean.getFm()));
					secJqpjMakeUpBean.getTargetDelRef().setFz(bpDataBean.getFz());
					secJqpjMakeUpBean.getTargetDelRef().setFm(bpDataBean.getFm());
					this.setMakeUpData(secJqpjMakeUpBean, String.valueOf(bpcs));
					doTzzMakeUp(secJqpjMakeUpBean, tzzJqpjMakeUpList);
					
					tradeSettleDetail.add(secJqpjMakeUpBean);
					tradeSettleDetail.addAll(tzzJqpjMakeUpList);
				}				
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public int getBpcs(Date beginDate, Date endDate, int maxBpcs) throws YssException{
		try{
			BaseOperDeal operDeal = new BaseOperDeal();
			operDeal.setYssPub(this.pub);
			String sHoildayCode = (String)this.paramSet.getHoildaysRela().get("dealdaynum");
			int workDatediff = operDeal.workDateDiff(beginDate, endDate, sHoildayCode, 1);
			int result = maxBpcs - workDatediff;
			return result;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public List<ETFTradeSettleDetailBean> getSecJqpjMakeUpList(List<ETFTradeSettleDetailBean> jqpjMakeUpList) throws YssException{
		try{	
			List<ETFTradeSettleDetailBean> secJqpjMakeUpList = new ArrayList<ETFTradeSettleDetailBean>();
			for(ETFTradeSettleDetailBean jqpjMakeUpBean : jqpjMakeUpList){
				if(" ".equalsIgnoreCase(jqpjMakeUpBean.getStockHolderCode())){
					secJqpjMakeUpList.add(jqpjMakeUpBean);
				}
			}
			
			return secJqpjMakeUpList;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public HashMap<String, List<ETFTradeSettleDetailBean>> getTzzJqpjMakeUpMap(List<ETFTradeSettleDetailBean> jqpjMakeUpList) throws YssException{
		try{	
			HashMap<String, List<ETFTradeSettleDetailBean>> tzzJqpjMakeUpMap = new HashMap<String, List<ETFTradeSettleDetailBean>>();
			List<ETFTradeSettleDetailBean> tzzJqpjMakeUpList = null;
			for(ETFTradeSettleDetailBean tzzJqpjMakeUpBean : jqpjMakeUpList){
				if(" ".equalsIgnoreCase(tzzJqpjMakeUpBean.getStockHolderCode())){
					continue;
				}
				String key = tzzJqpjMakeUpBean.getBs() + "\t" + tzzJqpjMakeUpBean.getSecurityCode();
				if(tzzJqpjMakeUpMap.containsKey(key)){
					tzzJqpjMakeUpList = tzzJqpjMakeUpMap.get(key);
					tzzJqpjMakeUpList.add(tzzJqpjMakeUpBean);
				}else{
					tzzJqpjMakeUpList = new ArrayList<ETFTradeSettleDetailBean>();
					tzzJqpjMakeUpList.add(tzzJqpjMakeUpBean);
					tzzJqpjMakeUpMap.put(key, tzzJqpjMakeUpList);
				}
			}
			
			return tzzJqpjMakeUpMap;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public void doTzzMakeUp(ETFTradeSettleDetailBean secMakeUpBean, List<ETFTradeSettleDetailBean> tzzMakeUpList) throws YssException{
		try{	
			double sumKttdk = 0.0;
			double sumYftdk = 0.0;	
			double sumBpsl = 0.0;
			double sumYbbpcb = 0.0;
			double sumBbbpcb = 0.0;
			for(ETFTradeSettleDetailBean tzzMakeUpBean : tzzMakeUpList){
				double secDbsl = this.getDbsl(secMakeUpBean);
				double secBpsl = secMakeUpBean.getTargetDelRef().getMakeUpAmount();
				double tzzDbsl = this.getDbsl(tzzMakeUpBean);
				double tzzBpsl = YssD.mul(secBpsl, YssD.div(tzzDbsl, secDbsl));				
				tzzBpsl = YssD.round(tzzBpsl, 0, true);
				tzzMakeUpBean.getTargetDelRef().setMakeUpAmount(tzzBpsl);
				tzzMakeUpBean.getTargetDelRef().setUnitCost(secMakeUpBean.getTargetDelRef().getUnitCost());
				tzzMakeUpBean.getTargetDelRef().setFz(secMakeUpBean.getTargetDelRef().getFz());
				tzzMakeUpBean.getTargetDelRef().setFm(secMakeUpBean.getTargetDelRef().getFm());
				this.setMakeUpData(tzzMakeUpBean, secMakeUpBean.getTargetDelRef().getRefNum());
				sumKttdk += tzzMakeUpBean.getTargetDelRef().getHcReplaceCash();
				sumYftdk += tzzMakeUpBean.getTargetDelRef().getHpReplaceCash();
				sumBpsl += tzzMakeUpBean.getTargetDelRef().getMakeUpAmount();
				sumYbbpcb += tzzMakeUpBean.getTargetDelRef().getoMakeUpCost();
				sumBbbpcb += tzzMakeUpBean.getTargetDelRef().gethMakeUpCost();
			}
			//最后一个投资者的补票数量、可退替代款、应付替代款倒轧,如果最后一个投资者的补票数量大于待补数量，则多出来的数量重新分配
			ETFTradeSettleDetailBean lastTzzMakeUpBean = tzzMakeUpList.get(tzzMakeUpList.size()-1);
			double secBpsl = secMakeUpBean.getTargetDelRef().getMakeUpAmount();
			double secKttdk = secMakeUpBean.getTargetDelRef().getHcReplaceCash();
			double secYftdk = secMakeUpBean.getTargetDelRef().getHpReplaceCash();
			double secYbbpcb = secMakeUpBean.getTargetDelRef().getoMakeUpCost();
			double secBbbpcb = secMakeUpBean.getTargetDelRef().gethMakeUpCost();
			double lastTzzBpsl = lastTzzMakeUpBean.getTargetDelRef().getMakeUpAmount();
			double lastTzzDbsl = this.getDbsl(lastTzzMakeUpBean);
			double lastTzzYpsl = YssD.add(lastTzzBpsl, YssD.sub(secBpsl, sumBpsl));
			double lastTzzKttdk = lastTzzMakeUpBean.getTargetDelRef().getHcReplaceCash();
			double lastTzzYftdk = lastTzzMakeUpBean.getTargetDelRef().getHpReplaceCash();
			double lastTzzYbbpcb = lastTzzMakeUpBean.getTargetDelRef().getoMakeUpCost();
			double lastTzzBbbpcb = lastTzzMakeUpBean.getTargetDelRef().gethMakeUpCost();
			
			if(lastTzzYpsl <= lastTzzDbsl){
				lastTzzMakeUpBean.getTargetDelRef().setMakeUpAmount(lastTzzYpsl);
				lastTzzMakeUpBean.getTargetDelRef().setHcReplaceCash(YssD.add(lastTzzKttdk, 
						                                               YssD.sub(secKttdk, sumKttdk)));
				lastTzzMakeUpBean.getTargetDelRef().setHpReplaceCash(YssD.add(lastTzzYftdk, 
                                                                       YssD.sub(secYftdk, sumYftdk)));
				lastTzzMakeUpBean.getTargetDelRef().setoMakeUpCost(YssD.add(lastTzzYbbpcb, 
                                                                     YssD.sub(secYbbpcb, sumYbbpcb)));
				lastTzzMakeUpBean.getTargetDelRef().sethMakeUpCost(YssD.add(lastTzzBbbpcb, 
                        											 YssD.sub(secBbbpcb, sumBbbpcb)));
			}else{
				lastTzzMakeUpBean.getTargetDelRef().setMakeUpAmount(lastTzzDbsl);
				this.setMakeUpData(lastTzzMakeUpBean, secMakeUpBean.getTargetDelRef().getRefNum());
				//重新分配的补票数量
				double cfpBpsl = YssD.sub(lastTzzYpsl, lastTzzDbsl);
				//重新分配的可退替代款
				double cfpKttdk = YssD.sub(secKttdk, YssD.add(sumKttdk, -lastTzzKttdk, lastTzzMakeUpBean.getTargetDelRef().getHcReplaceCash()));
				//重新分配的应付替代款
				double cfpYftdk = YssD.sub(secYftdk, YssD.add(sumYftdk, -lastTzzYftdk, lastTzzMakeUpBean.getTargetDelRef().getHpReplaceCash()));
				//重新分配的原币补票成本
				double cfpYbbpcb = YssD.sub(secYbbpcb, YssD.add(sumYbbpcb, -lastTzzYbbpcb, lastTzzMakeUpBean.getTargetDelRef().getoMakeUpCost()));
				//重新分配的本币补票成本
				double cfpBbbpcb = YssD.sub(secBbbpcb, YssD.add(sumBbbpcb, -lastTzzBbbpcb, lastTzzMakeUpBean.getTargetDelRef().gethMakeUpCost()));
				for(ETFTradeSettleDetailBean tzzGcMakeUpBean : tzzMakeUpList){
					double tzzBpsl = tzzGcMakeUpBean.getTargetDelRef().getMakeUpAmount();
					double tzzDbsl = this.getDbsl(tzzGcMakeUpBean);
					double kttdk = tzzGcMakeUpBean.getTargetDelRef().getHcReplaceCash();
				    double yftdk = tzzGcMakeUpBean.getTargetDelRef().getHpReplaceCash();
				    double ybbpcb = tzzGcMakeUpBean.getTargetDelRef().getoMakeUpCost();
				    double bbbpcb = tzzGcMakeUpBean.getTargetDelRef().gethMakeUpCost();
					if(YssD.add(tzzBpsl, cfpBpsl) <= tzzDbsl){
						tzzGcMakeUpBean.getTargetDelRef().setMakeUpAmount(YssD.add(tzzBpsl, cfpBpsl));
						tzzGcMakeUpBean.getTargetDelRef().setHcReplaceCash(YssD.add(kttdk, cfpKttdk));
						tzzGcMakeUpBean.getTargetDelRef().setHpReplaceCash(YssD.add(yftdk, cfpYftdk));
						tzzGcMakeUpBean.getTargetDelRef().setoMakeUpCost(YssD.add(ybbpcb, cfpYbbpcb));
						tzzGcMakeUpBean.getTargetDelRef().sethMakeUpCost(YssD.add(bbbpcb, cfpBbbpcb));
						break;
					}else{
						tzzGcMakeUpBean.getTargetDelRef().setMakeUpAmount(tzzDbsl);
						this.setMakeUpData(tzzGcMakeUpBean, secMakeUpBean.getTargetDelRef().getRefNum());
						cfpBpsl = YssD.add(tzzBpsl, cfpBpsl, -tzzDbsl);
						cfpKttdk = YssD.sub(cfpKttdk, YssD.sub(tzzGcMakeUpBean.getTargetDelRef().getHcReplaceCash(), kttdk));
						cfpYftdk = YssD.sub(cfpYftdk, YssD.sub(tzzGcMakeUpBean.getTargetDelRef().getHpReplaceCash(), yftdk));
						cfpYbbpcb = YssD.sub(cfpYbbpcb, YssD.sub(tzzGcMakeUpBean.getTargetDelRef().getoMakeUpCost(), ybbpcb));
						cfpBbbpcb = YssD.sub(cfpBbbpcb, YssD.sub(tzzGcMakeUpBean.getTargetDelRef().gethMakeUpCost(), bbbpcb));
					}
				}
			}				
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public void doQzMakeUp() throws YssException{
		try{	
			tradeSettleDetail.clear();
			
			Date bsDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate, (String)this.paramSet.getHoildaysRela().get("lastestdealdaynum"),
					                    0, "", -1));
			if(YssFun.dateDiff(this.tradeDate,bsDate) != 0){
				return;
			}
			bsDate = YssFun.toDate(this.paramSetAdmin.getWorkDay(this.tradeDate, (String)this.paramSet.getHoildaysRela().get("lastestdealdaynum"),
					-this.paramSet.getLastestDealDayNum(), "", -1));
			//证券强制补票
			List<ETFTradeSettleDetailBean> secQzMakeUpList = doSecQzMakeUp(bsDate);
			//投资者强制补票，最后一个投资者的补票数量、可退替代款、应付替代款倒轧
			doTzzQzMakeUp(secQzMakeUpList,bsDate);
			
			booPreAdmin.insertBSDetailRef(tradeSettleDetail);
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}			
	}
	
	public List<ETFTradeSettleDetailBean> doSecQzMakeUp(Date bsDate) throws YssException{
		try{		
			List<ETFTradeSettleDetailBean> secQzMakeUpList = this.bookDao.querySecBSDetailForQz(bsDate);
			for(ETFTradeSettleDetailBean secQzMakeUpBean : secQzMakeUpList){
				double secDbsl = this.getDbsl(secQzMakeUpBean);
				secQzMakeUpBean.getTargetDelRef().setMakeUpAmount(secDbsl);
				this.setMakeUpData(secQzMakeUpBean, "99");
			}
			
			this.tradeSettleDetail.addAll(secQzMakeUpList);
			return secQzMakeUpList;
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}
	
	public void doTzzQzMakeUp(List<ETFTradeSettleDetailBean> secQzMakeUpList, Date bsDate) throws YssException{
		try{
			HashMap<String, List<ETFTradeSettleDetailBean>> tzzQzMakeUpMap = bookDao.queryTzzBSDetailForQz(bsDate);			 
			for(ETFTradeSettleDetailBean secQzMakeUpBean : secQzMakeUpList){
				double sumKttdk = 0.0;
				double sumYftdk = 0.0;
				double sumYbbpcb = 0.0;
				double sumBbbpcb = 0.0;
				String key = secQzMakeUpBean.getBs() + "\t" + secQzMakeUpBean.getSecurityCode();
				List<ETFTradeSettleDetailBean> tzzQzMakeUpList = tzzQzMakeUpMap.get(key);
				for(ETFTradeSettleDetailBean tzzQzMakeUpBean : tzzQzMakeUpList){
					double tzzDbsl = this.getDbsl(tzzQzMakeUpBean);
					tzzQzMakeUpBean.getTargetDelRef().setMakeUpAmount(tzzDbsl);
					this.setMakeUpData(tzzQzMakeUpBean, "99");
					sumKttdk += tzzQzMakeUpBean.getTargetDelRef().getHcReplaceCash();
					sumYftdk += tzzQzMakeUpBean.getTargetDelRef().getHpReplaceCash();
					sumYbbpcb += tzzQzMakeUpBean.getTargetDelRef().getoMakeUpCost();
					sumBbbpcb += tzzQzMakeUpBean.getTargetDelRef().gethMakeUpCost();
				}
				ETFTradeSettleDetailBean tzzQzMakeUpBean = tzzQzMakeUpList.get(tzzQzMakeUpList.size()-1);
				tzzQzMakeUpBean.getTargetDelRef().setHcReplaceCash(YssD.add(tzzQzMakeUpBean.getTargetDelRef().getHcReplaceCash(),
						YssD.sub(secQzMakeUpBean.getTargetDelRef().getHcReplaceCash(), sumKttdk)));
				tzzQzMakeUpBean.getTargetDelRef().setHpReplaceCash(YssD.add(tzzQzMakeUpBean.getTargetDelRef().getHpReplaceCash(),
						YssD.sub(secQzMakeUpBean.getTargetDelRef().getHpReplaceCash(), sumYftdk)));
				tzzQzMakeUpBean.getTargetDelRef().setoMakeUpCost(YssD.add(tzzQzMakeUpBean.getTargetDelRef().getoMakeUpCost(),
						YssD.sub(secQzMakeUpBean.getTargetDelRef().getoMakeUpCost(), sumYbbpcb)));
				tzzQzMakeUpBean.getTargetDelRef().sethMakeUpCost(YssD.add(tzzQzMakeUpBean.getTargetDelRef().gethMakeUpCost(),
						YssD.sub(secQzMakeUpBean.getTargetDelRef().gethMakeUpCost(), sumBbbpcb)));
			}
			
			Iterator<String> tzzQzMakeUpIterator = tzzQzMakeUpMap.keySet().iterator();
		    while(tzzQzMakeUpIterator.hasNext()){
				String secKey = (String)tzzQzMakeUpIterator.next();		
				List<ETFTradeSettleDetailBean> tzzQzMakeUpList = tzzQzMakeUpMap.get(secKey);
				tradeSettleDetail.addAll(tzzQzMakeUpList);
			}
		}catch (Exception e) {
			throw new YssException(e.getMessage());
		}finally{		
		
		}	
	}

}