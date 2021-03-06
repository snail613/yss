package com.yss.main.operdeal.opermanage;

import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssException;
import com.yss.util.YssOperCons;

import java.util.Date;

/**
 * <P> xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 * 
 * <p>xuqiji 20090810 QDV4招商证券2009年07月06日01_A  MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定</p>
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持 </p>
 *
 * <p>Description:处理期权业务的总控制类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsControlManage extends BaseOperManage{
    public OptionsControlManage() {
    }
    /**
    * 执行业务处理
    * @throws YssException
    */
   public  void doOpertion() throws YssException{
	   String sAccountType = "";//通用参数获取期权核算方式
       try{
           //通过组合代码获取期权核算方式
           sAccountType = getAccountTypeBy(this.sPortCode,"OptionAccountType");
    	   
           OptionsDivideTradeDataManage divide = new OptionsDivideTradeDataManage();
           divide.setYssPub(pub);
           divide.initOperManageInfo(this.dDate,this.sPortCode);//传入时间和组合代码
           divide.doOpertion();//操作方法
           //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
      	   //当日产生数据，则认为有业务。
           this.sMsg = divide.sMsg; 
           if(sAccountType.equalsIgnoreCase(YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG)){//移动加权
        	   OptionsCostAddValueManage costAddValue = new OptionsCostAddValueManage();
               costAddValue.setYssPub(pub);
               costAddValue.initOperManageInfo(this.dDate,this.sPortCode);
               costAddValue.doOpertion();
           }else{
        	   OptionsFIFOFirstInOutManage fifoManage = new OptionsFIFOFirstInOutManage();
        	   fifoManage.setYssPub(pub);
        	   fifoManage.initOperManageInfo(this.dDate,this.sPortCode);
        	   fifoManage.doOpertion();
           }
           OptionsIntegratedDataManage integratedData = new OptionsIntegratedDataManage();
           integratedData.setYssPub(pub);
           integratedData.initOperManageInfo(this.dDate,this.sPortCode);
           integratedData.doOpertion();
           OptionsAutoDropRight autoDropRight = new OptionsAutoDropRight();
           autoDropRight.setYssPub(pub);
           autoDropRight.initOperManageInfo(this.dDate,this.sPortCode);
           autoDropRight.doOpertion();
           OptionsCashTransferManage cashTransfer = new OptionsCashTransferManage();
           cashTransfer.setYssPub(pub);
           cashTransfer.initOperManageInfo(this.dDate,this.sPortCode);
           cashTransfer.doOpertion();
           OptionsBalanceDataManage balanceData = new OptionsBalanceDataManage();
           balanceData.setYssPub(pub);
           balanceData.initOperManageInfo(this.dDate,this.sPortCode);
           balanceData.doOpertion();
           /*这个类此需求MS00562 期权和期货结算估值的保证金账户需要独立的界面让用户指定，已经实现，暂时不用，不要删除
           xuqiji 20090807
           OptionsCalculateManage calculate=new OptionsCalculateManage();
           calculate.setYssPub(pub);
           calculate.initOperManageInfo(this.dDate,this.sPortCode);
           calculate.doOpertion();
           */
       }catch(Exception e){
           throw new YssException(e.getMessage());
       }
   };
        public void initOperManageInfo(Date dDate, String portCode) throws YssException {
            this.dDate=dDate;
            this.sPortCode=portCode;
        }
        /**
         * 通过组合代码获取期权保证金结算方式或者获取期权核算方式
         * @param sPortCode String：组合代码
         * @return String
         */
        private String getAccountTypeBy(String sPortCode,String sCtlParam) throws YssException {
        	java.util.Hashtable htAccountType = null;
        	String sResult ="";
        	try{
    	        CtlPubPara pubPara = new CtlPubPara();
    	        pubPara.setYssPub(pub);
    	        htAccountType = pubPara.getOptionBailCarryType(sCtlParam);//通用参数获取期权保证金结转类型，默认-平仓结转
    	        if(sCtlParam.equalsIgnoreCase("bailcarrytype")){
    	        	sResult = YssOperCons.YSS_TYCS_BAILMONEY_PCTRANSFER;
    		        String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.
    		        		YSS_TYCS_BAILMONEY_DAYTRANSFER);//获取value值
    		        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
    		            sResult = YssOperCons.YSS_TYCS_BAILMONEY_DAYTRANSFER;//每日结转
    		        }
    	        }else{
    	        	sResult = YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_MODAVG;//移动加权
    	        	String sTheDayFirstFIFO = (String) htAccountType.get(YssOperCons.
    	        			YSS_OPTIONS_ACCOUNTTYPE_FIFO);//获取value值
    		        if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
    		            sResult = YssOperCons.YSS_OPTIONS_ACCOUNTTYPE_FIFO;//先入先出
    		        }
    	        }
        	}catch (Exception e) {
    			throw new YssException("通过组合代码获取期权保证金结算方式或者获取期权核算方式出错！",e);
    		}
            return sResult;
        }
    }


