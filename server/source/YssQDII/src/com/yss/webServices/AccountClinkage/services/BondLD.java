/**   
* @Title:  
* @Package com.yss.webServices.AccountClinkage.services
* @Description: TODO( ) 
* @author 
* @date 2013-5-9 下午03:32:17 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import java.util.Hashtable;
import java.util.List;
import org.dom4j.Element;
import com.yss.webServices.AccountClinkage.AbsService;

/** 
 * @ClassName:
 * @Description: TODO(  ) 
 * @author 
 * @date 2013-5-9 下午03:32:17 
 *  add by huangqirong 2013-05-09 story #3871 需求北京-[建设银行]QDII系统[高]20130419001
 *  查询债券应收利息（联动）
 */
public class BondLD extends AbsService {

		/** 
	 * <p>Title: </p> 
	 * <p>Description: </p>  
	 */
	public BondLD() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * 设置响应操作类型
	 * @see com.yss.webServices.AccountClinkage.AbsService#setOperType()
	 */
	@Override
	public void setOperType() {
		this.operType = 1 ;			
	}
    
    /* (non-Javadoc)
     * 设置数据类型
     * @see com.yss.webServices.AccountClinkage.AbsService#setDataType()
     */
    @Override
    public void setDataType() {
    // TODO Auto-generated method stub
    	this.dataType = 0;
    }
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doLinkage(java.lang.String)
    */
    @Override
    public void doResLinkage() {
    	// TODO Auto-generated method stub
    	Element record = null;
    	String acctountBalance = "0.0";
    	String sql = "";
    	if(this.getRequestMsgXml() != null){
    		Element body = this.getRecodeEle(); 	//获取body标签
    		
    		if("1111".equalsIgnoreCase(this.getRequestMsgXml().getRootElement().element("head").elementText("replycode")))
				return;
    		
    		if(body != null){    		
    			List<Element> records = body.elements("ENTITY");
    			if(records.size() > 0){
    				    				
    				for (int i = 0; i < records.size(); i++) {
    					record = records.get(i);
    					String productCode = record.elementText("productCode"); //资产组合代码
    					String bondcode = record.elementText("bondcode");		//证券代码
    					String invstType = record.elementText("invstType");		//投资分类
    					String marketID = record.elementText("marketID");		//市场代码
    					String BalDate = record.elementText("BalDate");			//余额日期
    					
    	    			String [] groupPorts = this.getPortCodeBySetCode(productCode);
    	    			String portCode = groupPorts[1]; //关联组合
    					String groupCode = groupPorts[0]; //组合群
    					if(portCode == null || portCode.trim().length() == 0){
    						this.setDoSign("0");
    						//this.setReplyRemark(this.getReplyRemark() + "资产组合不存在\n");
    						break;
    					}else{
	    					Hashtable<String, String> markets =  this.getMarket(groupCode , "AC_DicMarket" ,
	    							                              "获取清核市场代码转换出错：");//转换市场代码 
	    					
	    					String markCode = "";
	    					if(markets != null)
	    						markCode = markets.get(marketID);
	    	    			if(markCode == null || markCode.trim().length() == 0)
	    	    				markCode = marketID ;
	    	    			String securityCode = this.getSecurity(groupPorts[0] , bondcode , markCode); //转换证券代码
	    	    			
	    	    			sql = " select sum(FPortCuryBal) as FPortCuryBal from " +
	    	    					"tb_" + groupCode + "_stock_secrecpay ssrp where ssrp.fportcode = " + 
	    	    					this.getPub().getDbLink().sqlString(portCode) + 
                                         ((invstType == null || invstType.trim() .length() == 0) ? ""
											: " and ssrp.finvesttype = "
													+ this.getPub().getDbLink()
															.sqlString(
																	invstType))
									+
	    	    					" and ssrp.fstoragedate = " +
	    	    					this.getPub().getDbLink().sqlDate(BalDate) + " and ssrp.fsecuritycode = " +
	    	    					this.getPub().getDbLink().sqlString(securityCode) + " and ssrp.ftsftypecode = '06' " +
	    	    																		" and ssrp.fsubtsftypecode = '06FI'";
	    	    			acctountBalance = this.getDatabySql(sql , "FPortCuryBal" , "查询债券应收利息出错："); //取本位币值
	    	    			if(acctountBalance == null || acctountBalance.trim().length() == 0 )
	    	    				acctountBalance = "0.0";
    					}
					}
    			}
    			/**
				 * 为body标签添加标签和值
				 * */
				this.buildReplyMsg(this.getDoSign() , this.getTxcode() , "" , "" , this.getReplyRemark() , false , false);
				Element bodyRs = this.getResponesMsgXml().getRootElement().addElement("body");
				if("0".equalsIgnoreCase(this.getDoSign())){ //数据正常才有body
					bodyRs.addElement("record").addElement("accountBalance").setText(acctountBalance);
				}
    		}
    	}
    }
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doOnLine(java.lang.String)
    */
    @Override
    public void doResOnLine() {
    	// TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doBatch(java.lang.String)
    */
    @Override
    public void doResBatch() {
    	// TODO Auto-generated method stub
    }	    
    
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doReqLinkage()
    */
    @Override
    public void doReqLinkage() {
    // TODO Auto-generated method stub
    
    }
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doReqOnLine()
    */
    @Override
    public void doReqOnLine() {
    // TODO Auto-generated method stub
    
    }
    
    /* (non-Javadoc)
    * @see com.yss.webServices.AccountClinkage.AbsService#doReqBatch()
    */
    @Override
    public void doReqBatch() {
    // TODO Auto-generated method stub
    
    }
}
