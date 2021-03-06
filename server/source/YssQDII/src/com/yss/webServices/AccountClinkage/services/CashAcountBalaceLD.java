/**   
* @Title: CashAcountBalaceLD.java 
* @Package com.yss.webServices.AccountClinkage.services 
* @Description: TODO( ) 
* @author KR
* @date 2013-5-22 下午03:53:40 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.ncbs.client.NCBSWebService;

/** 
 * @ClassName: CashAcountBalaceLD 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-22 下午03:53:40 
 * add by huangqirong 2013-05-21 需求北京-[建设银行]QDII系统[高]20130419001
 * 资金账户余额查询
 */
public class CashAcountBalaceLD extends AbsService {

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqBatch()
	 */
	@Override
	public void doReqBatch() {
		
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqLinkage()
	 */
	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		NCBSWebService service = new NCBSWebService();
		String asXml = service.getNCBSPort().excuteNCBSWebService(this.getTxcode(), this.getRequestMsgXml().asXML());
		this.setResponesMsgXml(Console.parseXml(asXml));
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doReqOnLine()
	 */
	@Override
	public void doReqOnLine() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResBatch()
	 */
	@Override
	public void doResBatch() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResLinkage()
	 */
	@Override
	public void doResLinkage() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#doResOnLine()
	 */
	@Override
	public void doResOnLine() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setDataType()
	 */
	@Override
	public void setDataType() {
		// TODO Auto-generated method stub
		this.dataType = 0;//联动
	}

	/* (non-Javadoc)
	 * @see com.yss.webServices.AccountClinkage.AbsService#setOperType()
	 */
	@Override
	public void setOperType() {
		// TODO Auto-generated method stub
		this.operType = 0 ; //请求
	}

}
