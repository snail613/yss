/**   
* @Title: FundLD.java 
* @Package com.yss.webServices.AccountClinkage.services 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-10 下午05:24:10 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.ncbs.client.NCBSWebService;

/** 
 * @ClassName: FundLD 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-10 下午05:24:10 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 *  资金流水查询
 */
public class FundLD extends AbsService {

	/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public FundLD() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResLinkage()
	 */
	@Override
	public void doResLinkage() {		
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yss.webServices.AccountClinkage.AbsService#doOnLine(java.lang.String)
	 */
	@Override
	public void doResOnLine() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yss.webServices.AccountClinkage.AbsService#doBatch(java.lang.String)
	 */
	@Override
	public void doResBatch() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqLinkage()
	 */
	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub
		/**
		 * 调用NCBS服务端
		 * */
		NCBSWebService service = new NCBSWebService();
		String asXml = service.getNCBSPort().excuteNCBSWebService(this.getTxcode(), this.getRequestMsgXml().asXML());
		this.setResponesMsgXml(Console.parseXml(asXml));		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqOnLine()
	 */
	@Override
	public void doReqOnLine() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqBatch()
	 */
	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setDataType()
	 * 
	 */
	@Override
	public void setDataType() {
		// TODO Auto-generated method stub
		this.dataType = 0; //联动
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setOperType()
	 * 
	 */
	@Override
	public void setOperType() {
		this.operType = 0; //请求
	}
}
