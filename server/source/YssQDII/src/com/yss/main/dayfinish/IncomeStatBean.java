package com.yss.main.dayfinish;

import java.sql.ResultSet;
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.income.stat.*;
import com.yss.util.*;

public class IncomeStatBean
    extends BaseDataSettingBean implements IYssConvert, IClientOperRequest {
    private java.util.Date startDate; //起始日期
    private java.util.Date endDate; //终止日期
    private String portCodes = ""; //已选组合
    private String SelCodes = ""; //已选的LISTVIEW中计提收益的数据
    private String beanid = "";
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------
    private String modeCode = ""; //计提模式代码 add by wangzuochun 2010.02.10 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
	private String sOtherParams = "";//其他参数 QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315

	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	public String logSumCode = "";//汇总日志编号
	public boolean comeFromDD = false;//通过调度方案调用
	public SingleLogOper logOper = null;//日志执行实例
	//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	
    public IncomeStatBean() {
    }
    
    
    
    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\n");
            if (!reqAry[0].equalsIgnoreCase("")) {
                this.startDate = YssFun.toDate(reqAry[0]);
            }
            if (!reqAry[1].equalsIgnoreCase("")) {
                this.endDate = YssFun.toDate(reqAry[1]);
            }
            this.portCodes = reqAry[2]; //由，间隔
            this.SelCodes = reqAry[3];
            this.beanid = reqAry[4];
            // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
            this.assetGroupCode = reqAry[5];
            this.modeCode = reqAry[6];
            //------ delete by wangzuochun 2010.10.25 BUG #184::调度方案执行，到收益计提项报错--QDV4海富通2010年10月22日01_B
			//this.sOtherParams = reqAry[7]; // QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
            //-----------------BUG #184--------------//
        } catch (Exception e) {
            throw new YssException("解析日终处理收益计提请求信息出错\r\n" + e.getMessage(), e);
        }

    }

    /**
     * checkData
     *
     * @return String
     */
    public String checkRequest(String sType) throws YssException {
        return "";
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 这里分批对组合群里的数据进行统计，循环统计组合群里的内容
    public String doOperation(String sType) throws YssException {
        String isRinght = ""; //传给前台的信息判断是否成功
        String sPrefixTB = pub.getPrefixTB(); //把组合群代码更新到PUB里面去
        String[] allAssetGroupCode = this.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK); //解析前台传来的组合群代码
        String[] allPortCodes = this.portCodes.split(YssCons.YSS_GROUPSPLITMARK); //解析前台传来的组合代码按组合群解析出来
        String[] allSelCodes = this.SelCodes.split(YssCons.YSS_GROUPSPLITMARK); //按组合群得到listview里表第一列的内容
        try {
            for (int i = 0; i < allAssetGroupCode.length; i++) { //循环组合群代码
                this.assetGroupCode = allAssetGroupCode[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //更新当前组合群代码
                this.portCodes = allPortCodes[i]; //得到当前组合群代码下的组合代码
                this.SelCodes = allSelCodes[i]; //得到当前组合群下表的第一个列内容
                //added by liubo.Bug #3878
                //在处理跨组合群执行调度方案时，系统根据组合群变更了pub对象中预存的组合群号，但是基础货币集合并没有变更，在此需要根据组合群代码变更基础货币集合
                //=====================================
                pub.setAssetGroupCode(assetGroupCode);
                pub.setPortBaseCury();
              //==================end===================
                isRinght = this.groupFinish(); //调用统计方法
            }
        }catch(YssException e){
        	throw new YssException(e.getMessage());
        }
        catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
            //added by liubo.Bug #3878
            //执行完毕之后，将组合群代码跟基础货币集合变更回当前组合群的状态
            //===============================
            pub.setAssetGroupCode(sPrefixTB);
            pub.setPortBaseCury();
            //================end===============
        }
        return isRinght;
    }

    //--------MS00003 QDV4.1-参数布局散乱不便操作---------//
    //-------2009.10.13 蒋锦 添加 beanid 字段需要配外部调用-------//
    //------- add by wangzuochun 2010.02.10 MS00895 申购款计息模式相关需求 QDV4南方2009年12月25日02_A
    public String getModeCode() {
        return modeCode;
    }
    
    public void setModeCode(String modeCode) {
        this.modeCode = modeCode;
    }
    //----------------- MS00895 -------------------//
    
    public String getBeanid() {
        return beanid;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setBeanid(String beanid) {
        this.beanid = beanid;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    //---------------------------------------------------//
    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 这个统计方法就是以前单个组合群的方法
    public String groupFinish() throws YssException {
        int operType = 0;
        String reStr = "false";
        ArrayList bondList = new ArrayList();
		//---add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        if(logOper == null){//添加空指针判断
        	logOper = SingleLogOper.getInstance();
        }
		//---add by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29 用于判断技能同一个组合群组合下是否有与当前用户相同的操作类型,有的话就跳出提示信息
            YssGlobal.judgeIfUniqueUserAndPort(YssCons.YSS_OPER_INCOMESTAT, portCodes, pub);
			//计息前先清除全局变量中的编号 多线程并行并发优化处理 合并太平版本代码 by leeyu 20100701
	   	  	if(!YssGlobal.clearValNums){
	       		  YssGlobal.clearValNums =true;
	       		  YssGlobal.hmCashRecNums.clear();
	       		  YssGlobal.hmSecRecNums.clear();
	       	 }
	   		 //计息多线程并行并发优化处理 合并太平版本代码 by leeyu 20100701
            BaseIncomeStatDeal incomestat = (BaseIncomeStatDeal) pub.getOperDealCtx().getBean(this.beanid);
            incomestat.setYssPub(pub);
            incomestat.initIncomeStat(this.startDate, this.endDate, this.portCodes,
            		this.SelCodes, this.modeCode,this.sOtherParams);//添加其他参数处理 QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
            
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            incomestat.comeFromDD = this.comeFromDD;
            incomestat.logSumCode = this.logSumCode;
            incomestat.logOper = this.logOper;//设置日志执行实例
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            bondList = incomestat.getIncomes();
            //delete by songjie 2012.09.26 STORY #2344 QDV4建行2012年3月2日05_A
            //incomestat.saveIncomes(bondList);
            reStr = "true";
         	//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
         	if(incomestat.getResultMes().length()>0){
        	 	reStr=incomestat.getResultMes();
         	}
         	//QDV4中保2010年03月03日01_A MS01009 by leeyu 20100315
            operType = 7;
            
            logOper.setIData(this, 7, pub);
            //----------------------------------------
            return reStr; //统计成功就会把true这个字符串传到前台
        } catch(YssException e){
        	throw new YssException(e.getMessage());
        }
        catch (Exception e) {
            try {
                logOper = SingleLogOper.getInstance();
                logOper.setIData(this, operType, pub);
            } catch (YssException ex) {
                ex.printStackTrace();
            }

            throw new YssException("日终处理收益计提出错!\n", e); //异常信息后面加\n ,满足异常信息的解析处理 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
        }
        //----MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29----//
        finally {
            //移除当前用户当前组合群组合下操作类型方面的信息
            YssGlobal.removeRefeUserInfo(YssCons.YSS_OPER_INCOMESTAT, portCodes, pub);
			YssGlobal.clearValNums =false;//计息前先清除全局变量中的编号 多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码
			//add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			this.comeFromDD = false;//调度方案调用
        }
    }

    /**
     * 这个方法是查找“指数费计提条件及方式设置”
     * 设置的参数信息 
     * add by baopingping 20110701 #story 1138
     * @return Value
     * @throws YssException 
     */
    public  String GetPara(String portCode,String FctlCode) throws YssException{
    	ResultSet rs=null;
    	ResultSet RowRs=null;
    	String sql=null;
    	String Value=null;
    	try{
	    	sql="select *from  "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+" where FpubParaCode='T_Math_Hand'"+
	    	" and FctlgRpCode='Math_Hand'"+
	    	" and FctlValue like '"+portCode+"%'" +
	    	"and FctlCode='selPort'";
	    	rs=dbl.openResultSet(sql);
    	   while(rs.next())
    	   {
		    		String strSql="select *from  "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+" where FpubParaCode='T_Math_Hand'"+
		    	    " and FctlgRpCode='Math_Hand'"+
		    	    " and FparaId= '"+rs.getString("FparaId")+"'" +
		    	    " and FctlCode='"+FctlCode+"'";
		    		RowRs=dbl.openResultSet(strSql);
		    		while(RowRs.next())
		    		{
		    			String FctlValue=RowRs.getString("FctlValue");
		        		Value=FctlValue;
		    		}
    	   }
    	 			return Value;
    		}catch(Exception e)
    		{
    	     throw new YssException("获取指数费计提条件及方式设置参数出错!\n",e);
            }finally{
            	dbl.closeResultSetFinal(RowRs);
            	dbl.closeResultSetFinal(rs);
            }
    } 
}
