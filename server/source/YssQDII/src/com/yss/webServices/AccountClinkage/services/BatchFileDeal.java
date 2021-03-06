package com.yss.webServices.AccountClinkage.services;

import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;
import com.yss.webServices.AccountClinkage.ncbs.client.NCBSWebService;

/**
 * add dongqingsong 2013-05-17 #story 3871 建行清算联动
 */

public class BatchFileDeal extends AbsService {

	@Override
	public void doReqBatch() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doReqLinkage() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		if(this.getRequestMsgXml() == null )
			return;
		
		/**
		 * 调用NCBS服务端
		 * */
		NCBSWebService service = new NCBSWebService();
		String asXml = service.getNCBSPort().excuteNCBSWebService(this.getTxcode(), this.getRequestMsgXml().asXML());		
		this.setResponesMsgXml(Console.parseXml(asXml));
	}

	@Override
	public void doReqOnLine() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doResBatch() {
		// TODO Auto-generated method stub
	}

	@Override
	public void doResLinkage() {
		
	}

	@Override
	public void doResOnLine() {
		// TODO Auto-generated method stub
	}
	

	@Override
	public void setDataType() {
		// TODO Auto-generated method stub
        this.dataType = 0 ; //联动
	}

	@Override
	public void setOperType() {
		this.operType = 0 ; //响应
	}
}
