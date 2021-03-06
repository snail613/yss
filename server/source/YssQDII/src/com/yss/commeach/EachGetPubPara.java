package com.yss.commeach;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
import com.yss.vsub.YssOperFun;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.platform.pfoper.pubpara.PubParaBean;
/**
 * 
 * add by wangzuochun 2010.01.06 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A 
 *
 */
public class EachGetPubPara extends BaseCommEach {
	
	private String sPubPara = "";
	private String sPortCode = ""; //add by wangzuochun MS00881   国内回购业务，“成本浏览”显示该成本不正确   QDV4赢时胜（上海）2010年02月10日02_B 
	private String sDate = "";// add by jiangshichao 
	private String sTradeType = "";	//20120816 added by liubo.Story #2754.交易方式(期权)
	//20120816 added by liubo.Story #2754.
	//通参“同参期权成本核算设置”中具体控件的值，目前有两个选项:
	//cboAccountCost表示“是否核算成本”控件，selTransfering表示是否有资金流动控件
	//===================================
	private String sCtlFlag = "";	
	
	public String getCtlFlag() {
		return sCtlFlag;
	}
	public void setCtlFlag(String sCtlFlag) {
		this.sCtlFlag = sCtlFlag;
	}
	//===============end====================
	public String getTradeType() {
		return sTradeType;
	}
	public void setTradeType(String sTradeType) {
		this.sTradeType = sTradeType;
	}
	
	public EachGetPubPara(){
		
	}
	//----- add by wangzuochun MS00881   国内回购业务，“成本浏览”显示该成本不正确   QDV4赢时胜（上海）2010年02月10日02_B
	public String getSPortCode() {
        return sPortCode;
    }
	
	public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }
	//----------------MS00881------------------//
	
	public String getSPubPara() {
        return sPubPara;
    }
	
	public void setSPubPara(String sPubPara) {
        this.sPubPara = sPubPara;
    }
	
	public void setsDate (String sDate){
		this.sDate = sDate;
	}
	
	public String getSDate(){
		return sDate;
	}
	
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.sPubPara).append("\t");
        return buf.toString();
    }
	
	public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.sPubPara = reqAry[0];
            this.sPortCode = reqAry[1]; // add by wangzuochun MS00881   国内回购业务，“成本浏览”显示该成本不正确   QDV4赢时胜（上海）2010年02月10日02_B
            this.sDate = reqAry[2];
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }
	
	public String getInterestPara() throws YssException {

    	String strSql = "";
    	String strMode = "";
    	ResultSet rs = null;
		/**shashijie 2012-7-2 STORY 2475 */
    	//Connection conn = dbl.loadConnection();
		/**end*/
    	
    	CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        
        try{
        	strMode = pubpara.getInterestMode() + "";
        		
        	return strMode;
        }catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	/**
	 * add by wangzuochun MS00881   国内回购业务，“成本浏览”显示该成本不正确   QDV4赢时胜（上海）2010年02月10日02_B 
	 * @return
	 * @throws YssException
	 */
	public String getPurchasePara() throws YssException {
		
		String returnValue = null;
    	CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        
        try{
        	//------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
            returnValue = pubpara.getPurchasePortParams(this.sPortCode, "CboYesOrNo","inner_purchaseEIC"); //获取交易所回购交易费用入成本的通用参数设置
            
        	return returnValue;
        }catch (Exception e) {
            throw new YssException("");
        } finally {
        	
        }
    }
	
	//20110603 added by liubo.Story #1132
	//从后台取出“财务估值表停牌颜色设置”参数的参数值，返回给前台。	
	public String getGVColor() throws YssException {
        try{
			String returnValue = null;
	    	CtlPubPara pubpara = new CtlPubPara();
	        pubpara.setYssPub(pub);    	
            returnValue = pubpara.getGVColor(this.sPortCode);            
        	return returnValue;
        }catch (Exception e) {
            throw new YssException("");
        } finally {
        	
        }
    }
	
	
		/*******************************************************
	 *  StoryNo : #863 香港、美国股指期权交易区别
     *  Desc    : 获取期权成本核算设置
     *  author  : benson
     *  date    : 2011.06.15
	 * @return
	 * @throws YssException
	 */
	public String getOptCostAccountSet()throws YssException {
		
		String ErrorMsg = "";
		ResultSet rs = null;
		StringBuffer queryBuf= new StringBuffer();
		String sSecurityCode = "";
		String sExchangeCode = "";
		String sSubCateCode = "";
		try{
			
			queryBuf.append(" select fexchangecode,fsubcatcode from "+pub.yssGetTableName("Tb_Para_Security"));
			queryBuf.append(" where fcheckstate=1 and fsecuritycode="+dbl.sqlString(this.sPubPara));
			
			rs = dbl.openResultSet(queryBuf.toString());
			if (rs.next()){
				sExchangeCode = rs.getString("fexchangecode");
				sSubCateCode = rs.getString("fsubcatcode");
			}
			return getOptCostAccountSet(this.sPortCode,sExchangeCode,sSubCateCode);
		}catch(Exception e){
			ErrorMsg = e.getMessage();
			throw new YssException(ErrorMsg.length()>0?ErrorMsg:"获取期权是否核算成本设置时，解析出错......");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
	/*******************************************************
	 *  StoryNo : #863 香港、美国股指期权交易区别
     *  Desc    : 获取期权成本核算设置
     *  author  : benson
     *  date    : 2011.06.15
	 * @param sPortCode
	 * @param sExchangeCode
	 * @param sSubCateCode
	 * @return
	 * @throws YssException
	 */
	public String getOptCostAccountSet(String sPortCode,String sExchangeCode,String sSubCateCode) throws YssException{
		
		String returnValue = "true"; //默认值为true
		String sExchangePara=null,sPortPara=null,sSubCatePara=null,sDate1=null;
		String sCtlTradeType = "";	//20120816 added by liubo.Story #2754.交易方式
		String[] temp = null;
		String temp1=null,temp2;
    	PubParaBean pubpara = new PubParaBean();
    	Date date1=null,date2=null;
        pubpara.setYssPub(pub);
        
		try{
			pubpara.setParaGroupCode("option");
			pubpara.setPubParaCode("OptCostAccountSet");
			pubpara.setCtlGrpCode("OptCostAccountSet");
			temp2 = pubpara.getOperValue("allparas");
			
			if(temp2!=null && temp2.length()>0){
				temp = temp2.split("\f");
				for(int i=0;i<temp.length;i+=7){
					temp1= temp[i+2].split("\t")[1];
					sExchangePara = temp1.split("\\|")[0];
					temp1= temp[i+3].split("\t")[1];
					sPortPara = temp1.split("\\|")[0];
					temp1= temp[i+4].split("\t")[1];
					sSubCatePara = temp1.split("\\|")[0];
					temp1= temp[i].split("\t")[1];
					sDate1 = temp1.split("\\|")[0];
					
					//20120816 added by liubo.Story #2754.交易方式
					//=====================================
					temp1= temp[i+5].split("\t")[1];
					sCtlTradeType = temp1.split("\\|")[0];
					//================end=====================
					
					//20120816 modified by liubo.Story #2754.判断条件中增加交易方式
					//=====================================
					if(sTradeType.equals(sCtlTradeType) && sPortCode.equalsIgnoreCase(sPortPara)&& sExchangePara.indexOf(sExchangeCode)>=0 && sSubCatePara.equalsIgnoreCase(sSubCateCode)){
						date1 = YssFun.parseDate(sDate);//传进来的日期
						date2 = YssFun.parseDate(sDate1);//参数设定的启用日期
						if(YssFun.dateDiff(date2,date1)>=0){
							if (sCtlFlag.trim().equalsIgnoreCase("cboAccountCost"))		//cboAccountCost表示取出“是否核算成本”控件的值
							{
								temp1= temp[i+1].split("\t")[1];
							}
							else														//其他情况下取出“是否有资金流动”控件的值
							{
								temp1= temp[i+6].split("\t")[1];
							}
							returnValue = temp1.split(",")[0].equalsIgnoreCase("1")?"false":"true";
							break;
						}
						
					}
				}
			}
			
			return returnValue ;
		}catch(Exception e){
			throw new YssException("获取期权是否核算成本设置出错！！");
		}
	}
	
     /****************************************************************************
      *  StoryNo : #863 香港、美国股指期权交易区别
      *  Desc    : 通过组合代码获取期权成本核算设置参数
      *  author  : benson
      *  date    : 2011.06.15 
      * @param sPortCode
      * @return 交易所/t品种子类型/t启用日期
      * @throws YssException
      */
	public String getOptCostAccountSet(String sPortCode) throws YssException {

		String returnValue = "";
		String sFlag="true"; // 默认值为true
		String sExchangePara = null, sPortPara = null, sSubCatePara = null, sDate1 = null;
		String[] temp = null;
		String temp1 = null, temp2;
		PubParaBean pubpara = new PubParaBean();
		Date date1 = null, date2 = null;
		pubpara.setYssPub(pub);

		try {
			pubpara.setParaGroupCode("option");
			pubpara.setPubParaCode("OptCostAccountSet");
			pubpara.setCtlGrpCode("OptCostAccountSet");
			temp2 = pubpara.getOperValue("allparas");

			if (temp2 != null && temp2.length() > 0) {
				temp = temp2.split("\f");
				for (int i = 0; i < temp.length; i += 5) {
					
					temp1 = temp[i + 1].split("\t")[1];
					sFlag = temp1.split(",")[0]
							.equalsIgnoreCase("1") ? "false" : "true";
					temp1 = temp[i + 3].split("\t")[1];
					sPortPara = temp1.split("\\|")[0];
					if (sPortCode.equalsIgnoreCase(sPortPara)&&sFlag.equalsIgnoreCase("false")) {
						temp1 = temp[i].split("\t")[1];
						sDate1 = temp1.split("\\|")[0];
						temp1 = temp[i + 2].split("\t")[1];
						sExchangePara = temp1.split("\\|")[0];
						temp1 = temp[i + 4].split("\t")[1];
						sSubCatePara = temp1.split("\\|")[0];
						
						returnValue = sExchangePara+"\t"+sSubCatePara+"\t"+sDate1;
							break;
						

					}
				}
			}

			return (sFlag.equalsIgnoreCase("true")?"":returnValue);
		} catch (Exception e) {
			throw new YssException("获取期权是否核算成本设置出错！！");
		}
	}
	
	
	
	
	
	
	
	public String getOperValue(String sType) throws YssException {
        String reStr = "";
        if (sType.equalsIgnoreCase("getinterestpara")) {
        	reStr = getInterestPara();
        }
        //------ add by wangzuochun MS00881   国内回购业务，“成本浏览”显示该成本不正确   QDV4赢时胜（上海）2010年02月10日02_B  
        if (sType.equalsIgnoreCase("getpurchasepara")) {
        	reStr = getPurchasePara();
        }
        //20110603 Added by liubo.Stroy #1132.
        //取出“财务估值表停牌颜色设置”参数的参数值，返回前台
        if (sType.equalsIgnoreCase("GVColor")) {
        	reStr = getGVColor();
        } 
        //--------------------MS00881--------------------//
		
		 //---StoryNo : #863 香港、美国股指期权交易区别  add by jiangshichao 2011.06.15 ------------//
        if (sType.equalsIgnoreCase("optcostaccpara")){
        	reStr = getOptCostAccountSet();
        }
        //---StoryNo : #863 香港、美国股指期权交易区别  add by jiangshichao 2011.06.15 end------------//
        return reStr;
	}
	
	/***************************************************************
	 *  STORY #2024 系统可以自动按年、月、日、季支付佣金，且可以根据券商支付。 
	 *  add by jiangshichao 2012.02.15
	 * @param sPortCode
	 * @return
	 * @throws YssException
	 */
	public String getAutoPayCmsnSet(String sPortCode)throws YssException{
		
		String returnValue = ""; //默认值为true
		String sPortPara="",sSeatPara="",sHolidayPara="",sPayStylePara="",sDelayday = "",sBrokerPara="";
		String[] temp = null;
		String paras=null,temp2;
    	PubParaBean pubpara = new PubParaBean();
    	String errorMsg = "";
        pubpara.setYssPub(pub);
        
		try{
			pubpara.setParaGroupCode("operationDeal");
			pubpara.setPubParaCode("autopaycmsnset");
			pubpara.setCtlGrpCode("autopaycmsnset");
			paras = pubpara.getOperValue("allparas");
			
			if(paras!=null && paras.length()>0){
				temp = paras.split("\f");
				for(int i=0;i<temp.length;i+=6){
					
					temp2= temp[i+4].split("\t")[1];
					//只获取用户前台选定组合的设置
					if(temp2.split("\\|")[0].indexOf(sPortCode)<0){
						continue;
					}
					//保证按席位支付的在前面
					temp2= temp[i+5].split("\t")[1];
					if(temp2.split("\\|").length==0){
						//不按席位支付
						temp2= temp[i+4].split("\t")[1];
						sPortPara = sPortPara+temp2.split("\\|")[0]+",";
						
						temp2= temp[i].split("\t")[1];
						sDelayday = sDelayday+temp2.split(",")[0]+",";
						temp2= temp[i+1].split("\t")[1];
						sPayStylePara = sPayStylePara+temp2.split(",")[0]+",";
						temp2= temp[i+2].split("\t")[1];
						sBrokerPara = sBrokerPara+temp2.split("\\|")[0]+",";
						temp2= temp[i+3].split("\t")[1];
						sHolidayPara = sHolidayPara+temp2.split("\\|")[0]+",";
						
						temp2= temp[i+5].split("\t")[1];
						sSeatPara = sSeatPara+"space"+",";
					}else{
						//按席位支付
						temp2= temp[i+4].split("\t")[1];
						sPortPara = temp2.split("\\|")[0]+","+sPortPara;
						
						temp2= temp[i].split("\t")[1];
						sDelayday = temp2.split(",")[0]+","+sDelayday;
						temp2= temp[i+1].split("\t")[1];
						sPayStylePara = temp2.split(",")[0]+","+sPayStylePara;
						temp2= temp[i+2].split("\t")[1];
						sBrokerPara = temp2.split("\\|")[0]+","+sBrokerPara;
						temp2= temp[i+3].split("\t")[1];
						sHolidayPara = temp2.split("\\|")[0]+","+sHolidayPara;
						
						temp2= temp[i+5].split("\t")[1];
						sSeatPara = temp2.split("\\|")[0]+","+sSeatPara;
					}
					
					
				}
			}else{
				errorMsg = "请先到【业务平台-通用业务参数设置】页面进行自动支付佣金设置，再做佣金自动支付的操作";
				throw new YssException(errorMsg);
			}
			if(sDelayday.length()>0 ||sPayStylePara.length()>0||sHolidayPara.length()>0||sPortPara.length()>0||sSeatPara.length()>0){
				sDelayday = sDelayday.substring(0, sDelayday.length()-1);
				sPayStylePara = sPayStylePara.substring(0, sPayStylePara.length()-1);
				sHolidayPara = sHolidayPara.substring(0, sHolidayPara.length()-1);
				sPortPara = sPortPara.substring(0, sPortPara.length()-1);
				sSeatPara = sSeatPara.substring(0, sSeatPara.length()-1);
				sBrokerPara = sBrokerPara.substring(0, sBrokerPara.length()-1);
				returnValue = sDelayday+"\f"+sPayStylePara+"\f"+sBrokerPara+"\f"+sHolidayPara+"\f"+sPortPara+"\f"+sSeatPara;
			}
			return returnValue ;
		}catch(Exception e){
			throw new YssException(errorMsg.length()>0?errorMsg:"获取自动支付佣金设置出错！！");
		}
	}
	
	
}


