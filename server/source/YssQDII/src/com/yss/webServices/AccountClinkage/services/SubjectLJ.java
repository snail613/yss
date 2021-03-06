/**   
* @Title: SubjectPL.java 
* @Package com.yss.webServices.AccountClinkage.services 
* @Description: TODO( ) 
* @author dongqingsong
* @date 2013-5-10 下午05:22:00 
* @version V4.0   
*/
package com.yss.webServices.AccountClinkage.services;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import com.yss.webServices.AccountClinkage.AbsService;
import com.yss.webServices.AccountClinkage.Console;

/** 
 * @ClassName: SubjectPL 
 * @Description: TODO(  ) 
 * @author KR 
 * @date 2013-5-10 下午05:22:00 
 *  add by dongqingsong 2013-05-09 story #3871-6 需求北京-[建设银行]QDII系统[高]20130419001
 *  科目性质发生额数据接口(联机)
 */
public class SubjectLJ extends AbsService {
	
	@Override
	public void doResBatch() {
	}
	
	public void setDataType() {
		this.dataType = 1; //联机
	}
	
	@Override
	public void setOperType() {
		this.operType = 1; //响应
	}
	
	/**
	 * 获取凭证数据sql
	 * */
	private String getFWVsql(String year , String assetCode , String accountAttrs , String startDate , String endDate){
		String sql = " select fwv.*, alc.facctname as facctname from (select fw.fkmh as facctcode, " +
		  " al.facctattr as facctattr,fcyid, sum(decode(fjd, 'J', fw.fbbal, 0)) as FJmoney," +
		  " sum(decode(fjd, 'D', fw.fbbal, 0)) as FDmoney " +
		  " from a" + year + assetCode + "fcwvch fw left join  " +
		  " a" +  year + assetCode + "laccount" +
		  " al on fw.fkmh = al.facctcode where al.facctattr = " +
		  this.getPub().getDbLink().sqlString(accountAttrs) +  " and fw.fdate between " +
		  this.getPub().getDbLink().sqlDate(startDate) + " and " + 
		  this.getPub().getDbLink().sqlDate(endDate) + 
          " group by al.facctattr, fcyid, fw.fkmh " +
          " order by fkmh, fcyid) fwv  left join " +
          " a" +  year + assetCode + "laccount alc on fwv.facctcode = alc.facctcode";
		return sql;
	}
	
	public void doReqLinkage() {
	}
	
	@Override
	public void doReqOnLine() {
	}
	
	@Override
	public void doReqBatch() {
	}
	
	public SubjectLJ() {
	}
	
	/**
	 * 联机
	 * */
	@Override
	public void doResLinkage() {
		
	}
	
	@Override
	public void doResOnLine() {
		String sql = "";
		String year = "";
		ResultSet rs = null;
		boolean hasRs = true; //是否有数据
		Document docment = Console.createXml(null, "100", "100", "1.0", "", this.getTxcode(), false) ;
		Element body = docment.getRootElement().addElement("body");
		if(this.getRequestMsgXml() != null){
			
			if("1111".equalsIgnoreCase(this.getRequestMsgXml().getRootElement().element("head").elementText("replycode")))
				return;
			
			List<Element> entitys = this.getRequestMsgXml().getRootElement().element("body").elements("ENTITY"); 	//获取ENTITY标签
			
			try {
				for (int i = 0; i < entitys.size(); i++) { //遍历费用类型
					Element record = entitys.get(i);
					String setCode =  record.elementText("productCode");
					String startDate = record.elementText("startDate");
	    			String endDate = record.elementText("endDate");
	    			String accountAttrs = record.elementText("accountAttr");
	    			year = startDate.substring(0, 4);
	    			String [] groupPorts = this.getPortCodeBySetCode(setCode);//根据资产代码获取组合代码
	    			
	    			String fJMoney = "0";
					String fDMoney = "0";
	    			
					String setId = this.getSet(setCode, year); //找套帐
					
	    			//一下为俩个判断组合是否为空
	    			if(groupPorts == null || groupPorts[0] == null || groupPorts[1] == null){	    				
	    				break;
	    			}else if("".equalsIgnoreCase(groupPorts[0].trim()) || "".equalsIgnoreCase(groupPorts[1].trim())){
	    				break;
	    			}
	    			
	    			Hashtable<String, String> htCuryToNums = this.getMarket(groupPorts[0],
							"AC_DicCuryToNum","获取币种转编码出错：");
	    			
	    			sql = this.getFWVsql(year, setId, accountAttrs, startDate, endDate); //获取凭证数据sql
	    			
	    			if(!this.getPub().getDbLink().yssTableExist("a" + year + setId + "fcwvch"))
						continue;
					
					rs = this.getPub().getDbLink().openResultSet(sql);
					
					while(rs.next()){ //获取单套帐数据
						if(hasRs)
						    hasRs = false;
						String facctattr = rs.getString("facctattr")==null?"" : rs.getString("facctattr");
						
						String CuryCode = rs.getString("fcyid") == null ? "" : rs.getString("fcyid");//币种 需要加词汇转换
						
						String tmp = "";
						if(htCuryToNums != null)
						    tmp = htCuryToNums.get(CuryCode);
						if(tmp != null && tmp.trim().length() > 0)
							CuryCode = tmp ;
						else
							CuryCode = "156";
						
						String acctCode = rs.getString("facctcode") == null ? "" : rs.getString("facctcode");
						String facctname = rs.getString("facctname") == null ? "" : rs.getString("facctname");
						fJMoney = rs.getString("fJMoney") == null ? "" : rs.getString("fJMoney");
						fDMoney = rs.getString("fDMoney") == null ? "" : rs.getString("fDMoney");
						Element record0 = body.addElement("record");
						record0.addElement("productCode").setText(setCode);
						record0.addElement("startDate").setText(startDate);
						record0.addElement("endDate").setText(endDate);
						record0.addElement("curcode").setText(CuryCode);
						record0.addElement("accountattr").setText(accountAttrs);
						record0.addElement("accountcode").setText(acctCode);
						record0.addElement("accountname").setText(facctname);
						record0.addElement("debitamt").setText(fJMoney);
						record0.addElement("creditamt").setText(fDMoney);
					}
					
					if(hasRs){
						Element record0 = body.addElement("record");
						record0.addElement("productCode").setText(setCode);
						record0.addElement("startDate").setText(startDate);
						record0.addElement("endDate").setText(endDate);
						//record0.addElement("curcode").setText(CuryCode);
						record0.addElement("accountattr").setText(accountAttrs);
						//record0.addElement("accountcode").setText(acctCode);
						//record0.addElement("accountname").setText(facctname);
						record0.addElement("debitamt").setText(fJMoney);
						record0.addElement("creditamt").setText(fDMoney);
					}
				}
			} catch (Exception e) {
				this.setDoSign("1");
				this.setReplyRemark(this.getReplyRemark() + "查询科目性质发生额数据接口（联机）出错：" + e.getMessage() + "\n");
				System.out.println("查询科目性质发生额数据接口（联机）出错：" + e.getMessage());
			}finally{
				this.getPub().getDbLink().closeResultSetFinal(rs);
				this.setResponesMsgXml(docment);
			}
		}
	}
}
