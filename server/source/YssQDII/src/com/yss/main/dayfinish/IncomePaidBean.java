package com.yss.main.dayfinish;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.dao.IYssConvert;
import com.yss.main.funsetting.FlowBean;
import com.yss.main.operdeal.income.paid.BaseIncomePaid;
import com.yss.main.operdeal.income.paid.PaidAccIncome;
import com.yss.main.operdeal.income.stat.BaseIncomeStatDeal;
import com.yss.pojo.dayfinish.AccPaid;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssGlobal;
import com.yss.util.YssOperCons;

public class IncomePaidBean
    extends BaseDataSettingBean implements IYssConvert, IClientOperRequest {
    private java.util.Date dDate; //起始日期
    private java.util.Date dEnd; //结束日期
    private String strPortCode = ""; //已选组合
    private String incomePaid = "";
    private String beanid = "";
    private String isAll = "";
    private String isCheckData="";//fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
    private String paidType = ""; //edit by jc
    // BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 fanghaoln 20090512
    private String assetGroupCode = ""; //组合群代码
    private String assetGroupName = ""; //组合群名称
    //--------------------------------------------------------------------------------
    //--- MS00003 QDV4.1-参数布局散乱不便操作 ---//
    private FlowBean flow = null;
	private int operType;
	private SingleLogOper logOper;
    //----------------------------------------//
    public IncomePaidBean() {
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
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        if (sType.equalsIgnoreCase("title")) {
            sResult = this.getListView1Headers() + "\r\f\r\f\r\f" +
                this.getListView1ShowCols();
        }
        /**shashijie 2012-11-29 STORY 3288 判断债券是否已有派息数据,若有给出提示以免产生重复数据*/
		if (sType.equalsIgnoreCase("isTradeDateR")) {
			sResult = isTradeDateR();
		}
		/**end shashijie 2012-11-29 STORY */
        return sResult;
    }

    /**shashijie 2012-11-29 STORY 3288 判断债券是否已有派息数据,若有给出提示以免产生重复数据 */
	private String isTradeDateR() throws YssException {
		String sResult = "";
		String groupCode = pub.getAssetGroupCode();
		try {
			//循环组合群组合
			String[] assetGroup = this.assetGroupCode.split("<-AGP->");
			//组合
			String[] portCodes = this.strPortCode.split("<-AGP->");
			//证券代码
			String securityCode = "";
			if (this.incomePaid!=null && !this.incomePaid.trim().equals("") && this.incomePaid.split("\t").length>=2) {
				securityCode = this.incomePaid.split("\t")[1];
			}
			
			for (int i = 0; i < assetGroup.length; i++) {
				pub.setAssetGroupCode(assetGroup[i]);
				if (getTradeRight(portCodes[i],this.dDate,securityCode,YssOperCons.YSS_JYLX_ZQPX).trim().equals("")) {
					sResult = "0";//没有派息
				} else {
					sResult = "1";//有派息数据
				}
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			pub.setAssetGroupCode(groupCode);
		}
		return sResult;
	}

	/**shashijie 2012-11-29 STORY 3288 获得当天的派息数据  */
	private String getTradeRight(String portCode, Date pDate,
			String securityCode,String TradeTypeCode) throws YssException {
		String sResturt = "";
		ResultSet rs = null;
		try {
			String query = getQuery(portCode,pDate,securityCode,TradeTypeCode);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				sResturt +=rs.getString("FSecurityCode");
			}
		} catch (Exception e) {
			throw new YssException("获得当天的派息数据出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sResturt;
	}

	/**shashijie 2012-11-29 STORY 3288 */
	private String getQuery(String portCode, Date pDate, String securityCode,
			String tradeTypeCode) {
		String sqlString = " Select * From "+pub.yssGetTableName("Tb_Data_Subtrade")+
			" a Where a.FCheckState = 1 " +
			" And a.Fbargaindate = "+dbl.sqlDate(pDate)+
			" And a.Fportcode In ("+operSql.sqlCodes(portCode)+" ) "+
			" And a.Ftradetypecode = "+dbl.sqlString(tradeTypeCode)+
			" And a.Fsecuritycode = "+dbl.sqlString(securityCode);
		return sqlString;
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
            /**shashijie 2012-11-29 STORY 3288 判断长度避免报错,左侧双击菜单弹出收益支付页面的时候也会调用此方法,
             * 不过由于当时开发人员的粗心,拼接的字符串是\t不是\n所以这里要判断 */
			if (reqAry==null || reqAry.length<8) {
				return;
			}
			/**end shashijie 2012-11-29 STORY 3288 */
			
            if (!reqAry[0].equalsIgnoreCase("")) {
                this.dDate = YssFun.toDate(reqAry[0]);
            }
            this.strPortCode = reqAry[1]; //由，间隔
            this.incomePaid = reqAry[2];
            this.beanid = reqAry[3];
            this.isAll = reqAry[4];
            this.paidType = reqAry[5];
            //MS00001《QDV4.1赢时胜（上海）2009年4月20日01_A》fanghaoln 20090514 跨组合群国内项目
            this.assetGroupCode = reqAry[6];
            this.isCheckData=reqAry[7];//fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
        } catch (Exception e) {
            throw new YssException("解析收益支付请求信息出错\r\n" + e.getMessage(), e);
        }

    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容
    public String checkRequest(String sType) throws YssException {
        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        String[] assetGroupCodes = this.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.strPortCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合代码
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                this.strPortCode = strPortCodes[i]; //得到一个组合群下的组合代码
                String sGroup = this.checkGroupRequest(sType); //调用以前的执行方法
                sAllGroup = sAllGroup + sGroup + YssCons.YSS_GROUPSPLITMARK; //组合得到的结果集
            }
            if (sAllGroup.length() > 7) { //去除尾部多余的组合群解析符
                sAllGroup = sAllGroup.substring(0, sAllGroup.length() - 7);
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
        }

        return sAllGroup; //把结果返回到前台进行显示

    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容
    public String doOperation(String sType) throws YssException {
        String sAllGroup = ""; //定义一个字符用来保存执行后的结果传到前台
        String sPrefixTB = pub.getPrefixTB(); //保存当前的组合群代码
        String[] assetGroupCodes = this.assetGroupCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合群代码
        String[] strPortCodes = this.strPortCode.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析组合代码
        String[] allincomePaid = this.incomePaid.split(YssCons.
            YSS_GROUPSPLITMARK); //按组合群的解析符解析选择对象
        try {
            for (int i = 0; i < assetGroupCodes.length; i++) { //循环遍历每一个组合群
                this.assetGroupCode = assetGroupCodes[i]; //得到一个组合群代码
                pub.setPrefixTB(this.assetGroupCode); //修改公共变量的当前组合群代码
                this.strPortCode = strPortCodes[i]; //得到一个组合群下的组合代码
                this.incomePaid = allincomePaid[i]; //得到一个组合群下的选择对象
                sAllGroup = this.doGroupOperation(sType); //调用以前的执行方法
            }
           //add by guojianhua 2010 09 25 添加操作类型日志
            operType =20;
            logOper = SingleLogOper.getInstance();
            this.setRefName("000127");
            this.setModuleName("dayfinish");
            this.setFunName("incomepaid");
            logOper.setIData(this, operType, pub);
            //----------------------------------------
        } catch (Exception e) {
            throw new YssException("日终处理收益收支出错", e);
        } finally {
            pub.setPrefixTB(sPrefixTB); //还原公共变的里的组合群代码
        }
        return sAllGroup; //把结果返回到前台进行显示
    }

    public String getAssetGroupCode() {
        return assetGroupCode;
    }

    public String getAssetGroupName() {
        return assetGroupName;
    }

    public void setAssetGroupCode(String assetGroupCode) {
        this.assetGroupCode = assetGroupCode;
    }

    public void setAssetGroupName(String assetGroupName) {
        this.assetGroupName = assetGroupName;
    }

    /// <summary>
    /// 修改人：fanghaoln
    /// 修改人时间:20090512
    /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
    /// 从后台加载出我们跨组合群的内容这是以前没有跨组合群的方法。
    public String checkGroupRequest(String sType) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        IYssConvert item = null;

        try {
            sHeader = this.getListView1Headers();

            BaseIncomePaid income = (BaseIncomePaid) pub.getOperDealCtx().getBean(beanid);
            income.setYssPub(pub);
            income.initIncomeCalculate(null, this.dDate, YssFun.toDate("1900-01-01"),
                                       this.strPortCode, this.isAll + "\t" + this.paidType); //edit by jc

            ArrayList incomes = income.getIncomes();
            if (incomes.size() > 0) {
                for (int i = 0; i < incomes.size(); i++) {
                    item = (IYssConvert) incomes.get(i);
                    bufShow.append(item.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(item.buildRowStr()).
                        append(YssCons.YSS_LINESPLITMARK);
                }
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException(e);
        }

    }

    /// <summary>
	/// 修改人：fanghaoln
	/// 修改人时间:20090512
	/// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
	/// 从后台加载出我们跨组合群的内容这是以前没有跨组合群的方法。
    public String doGroupOperation(String sType) throws YssException {
        String beanid1 = "";
        String beanid2 = "";
        String reStr = "false";
        String[] sPaidAry = null;
        IYssConvert bond = null;
        Object obj = null;
        ArrayList bondList = new ArrayList();
        String[] reqAry = null;
        logOper = SingleLogOper.getInstance();
        String subItem="";
        String operType="";//收益支付类型
  	    //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
  	    Date logStartTime = null;//业务子项开始时间
  	    String logSumCode = "";//日志汇总编号
  	    DayFinishLogBean df = new DayFinishLogBean();
  	    boolean isError = false;//是否报错
  	    String errorInfo = " ";//报错信息
  	    //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            //MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29 用于判断技能同一个组合群组合下是否有与当前用户相同的操作类型,有的话就跳出提示信息
            YssGlobal.judgeIfUniqueUserAndPort(YssCons.YSS_OPER_INCOMEPAID, strPortCode, pub);
            //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            df.setYssPub(pub);
            logSumCode = df.getLogSumCodes();
            logStartTime = new Date();
            //---add by songjie 2012.08.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            reqAry = this.beanid.split("\t");
            beanid1 = reqAry[0];
            operType=getOperType(beanid1);
            beanid2 = reqAry[1];
            String[] str = null;
            if (this.incomePaid.equals("null")) {
                this.beanid = beanid.substring(0, beanid.indexOf("\t"));
                str = checkRequest("").split("\r\f");
                if (str.length > 2) {
                    this.incomePaid = str[1];
                    this.incomePaid = this.incomePaid.substring(0,
                        incomePaid.lastIndexOf("\t"));
                    this.incomePaid += "\t" + this.isAll;
                } else {
                    return "false";
                }
            }

	        //支付前先清除全局变量中的编号 多线程并行并发优化处理 合并太平版本代码 by leeyu 20100701
	 	  	if(!YssGlobal.clearValNums){
	     	    YssGlobal.clearValNums =true;
	     	    YssGlobal.hmCashRecNums.clear();
	     	    YssGlobal.hmSecRecNums.clear();
	     	}
	 		//支付多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码 
            BaseIncomePaid income = (BaseIncomePaid) pub.getOperDealCtx().
                getBean(beanid1);
            income.setYssPub(pub);
            income.initIncomeCalculate(null, this.dDate,
                                       YssFun.toDate("1900-01-01"),
                                       this.strPortCode, "\t" + this.paidType); //edit by jc
            sPaidAry = this.incomePaid.split(YssCons.YSS_LINESPLITMARK);
            subItem=getSubItem(beanid1,sPaidAry);//获取处理项目的明细数据
            for (int i = 0; i < sPaidAry.length; i++) {
                obj = pub.getDayFinishCtx().getBean(beanid2);
                bond = (IYssConvert) obj;
                bond.parseRowStr(sPaidAry[i]);
                income.calculateIncome(bond);
                bondList.add(bond);
                //add by jsc 20120323 现金利息支付的同时把存款利息税也支付掉
                if(income instanceof PaidAccIncome && bond instanceof AccPaid ){
                	AccPaid accPaidBean = (AccPaid)bond;
                	
                	PaidAccIncome paidAccInCome = (PaidAccIncome)income;
                	HashMap valueMap = paidAccInCome.getPaidInterTax(bond);
                	double dInterTaxBal = (valueMap.get("07LXS_DE\tbal")==null?0:(Double)valueMap.get("07LXS_DE\tbal"));
                	if(dInterTaxBal >0 && accPaidBean.getTsfTypeCode().equalsIgnoreCase("06")){
                		bondList.add(paidAccInCome.PaidInterTax(bond,valueMap));
                	}
                }
            }
            income.isCheckData=this.isCheckData;//fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
            
            //--- add by songjie 2012.09.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            income.logInfo = "";
            income.logSumCode = logSumCode;//汇总日志编号
            income.logOper = this.logOper;//日志实例
            income.operType = operType;//操作类型
            income.saveIncome(bondList);
            //--- add by songjie 2012.09.20 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
            //20130216 added by liubo.Story #3414.支付两费时自动生成划款手续费
            //calcCommission方法返回为0，则表示没有两费支付的划款手续费生成，返回TRUE，在前台将弹出“收益支付统计完成”的提示框
            //若返回的值大于0，表示有两费支付的划款手续费生成，返回complete，在前台弹出“统计成功！  两费支付的划款手续费已生成！”的提示框
            //====================================
            int iReturn = income.calcCommission(bondList);	
            
            if (iReturn > 0)
            {
            	reStr = "complete";
            }
            else
            {
            	reStr = "true";
            }
            //===============end=====================
            
        } catch (Exception e) {
        	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        	try{
        		//修改异常信息为可被解析的信息 sunkey 20090204 QDV4.1-BugNO:MS00004 指示信息的解析处理
        		YssGlobal.clearValNums =false;//支付前先清除全局变量中的编号 多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码 
        		//add by songjie 2012.09.27 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        		isError = true;//设置为报错
                //获取报错信息 
        		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
        		errorInfo = (" \r\n收益支付出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
        		.replaceAll("&", "").replaceAll("\t", "").replaceAll("\f\f", "");
        		//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	
        	//---edit by songjie 2012.06.08 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    	 	throw new YssException("日终处理收益支付出错！\n", e);
        }
        //----MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29----//
        finally {
            //移除当前用户当前组合群组合下操作类型方面的信息
			YssGlobal.clearValNums =false;//支付前先清除全局变量中的编号 多线程并行并发优化处理 by leeyu 20100701 合并太平版本代码 
            YssGlobal.removeRefeUserInfo(YssCons.YSS_OPER_INCOMEPAID, strPortCode, pub);
            
            //--- add by songjie 2012.09.27 STORY #2344 QDV4建行2012年3月2日05_A start---//
            //edit by songjie 2012.11.20 添加非空判断
            if(logOper != null){
            	//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
            	if(logOper.getDayFinishLog().getAlPojo().size() == 0){
            		//20收益支付 ，operType是处理项目
                	logOper.setDayFinishIData(this, 20, " ", this.pub, isError,
                			this.strPortCode, dDate, dDate, dDate,errorInfo, 
                			logStartTime,logSumCode,new java.util.Date());
            	}
            	//---add by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            	
            	logOper.setDayFinishIData(this, 20, "sum", this.pub, isError,
            		" ", dDate, dDate, dDate,errorInfo,logStartTime,logSumCode, 
            		new java.util.Date());//20收益支付 ，operType是处理项目
            }
            //--- add by songjie 2012.09.27 STORY #2344 QDV4建行2012年3月2日05_A end---//
        }
        //----MS00006-QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29----//
        return reStr;
    }
    
    private String getSubItem(String beanid1, String[] sPaidAry) throws YssException {
    	if(sPaidAry.length<1){
    		return "";
    	}
    	String sReturn="";
    	if(beanid1.equalsIgnoreCase("investincomepaid")){//第三两费收支
			for(int i=0;i<sPaidAry.length;i++){
			    //edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[2]+YssCons.YSS_LINESPLITMARK;
			}
			BaseIncomeStatDeal statdeal=new BaseIncomeStatDeal();
			statdeal.setYssPub(pub);
			return statdeal.getSubItemFee(sReturn);
			
		}else if(beanid1.equalsIgnoreCase("fundincomepaid")){//第四基金万份收支
			for(int i=0;i<sPaidAry.length;i++){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[0]+"-"+sPaidAry[i].split("\t")[1]+YssCons.YSS_LINESPLITMARK;
			}
			//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		return sReturn.substring(0,sReturn.length() - 2);
		}else if(beanid1.equalsIgnoreCase("bondincomepaid")){//第一债券利息收支
			for(int i=0;i<sPaidAry.length;i++){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[1]+"-"+sPaidAry[i].split("\t")[2]+YssCons.YSS_LINESPLITMARK;
			}
			//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		return sReturn.substring(0,sReturn.length() - 2);
		}else if(beanid1.equalsIgnoreCase("accincomepaid")){//第二现金利息收支
			for(int i=0;i<sPaidAry.length;i++){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[13]+"-"+sPaidAry[i].split("\t")[14]+YssCons.YSS_LINESPLITMARK;
			}
			//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		return sReturn.substring(0,sReturn.length() - 2);
		}else if(beanid1.equalsIgnoreCase("seclendcomepaid")){//第二现金利息收支
			for(int i=0;i<sPaidAry.length;i++){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[1]+"-"+sPaidAry[i].split("\t")[2]+YssCons.YSS_LINESPLITMARK;
			}
			//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    		return sReturn.substring(0,sReturn.length() - 2);
		}/**shashijie 2011-09-30 STORY 1561 送股税金*/
		else if (beanid1.equalsIgnoreCase("BounsScot")) {
			for(int i=0;i<sPaidAry.length;i++){
				//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 修改分隔符
				sReturn+=sPaidAry[i].split("\t")[0]+"-"+sPaidAry[i].split("\t")[1]+YssCons.YSS_LINESPLITMARK;
			}
			//edit by songjie 2012.08.24 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			return sReturn.substring(0,sReturn.length() - 2);
		}
    	/**end*/
    	return "";
	}

	//使用收支ID的类型去判断操作类型 2011.01.06
    private String getOperType(String beanid1) {
		if(beanid1.equalsIgnoreCase("investincomepaid")){//第三两费收支
			return "investincomepaid";
		}else if(beanid1.equalsIgnoreCase("fundincomepaid")){//第四基金万份收支
			return "fundincomepaid";
		}else if(beanid1.equalsIgnoreCase("bondincomepaid")){//第一债券利息收支
			return "bondincomepaid";
		}else if(beanid1.equalsIgnoreCase("accincomepaid")){//第二现金利息收支
			return "accincomepaid";
		}else if(beanid1.equalsIgnoreCase("seclendcomepaid")){//证券借贷收支
			return "seclendcomepaid";
		}/**shashijie 2011-09-30 STORY 1561 */
		 else if(beanid1.equalsIgnoreCase("BounsScot")){
			return "BounsScot";//送股税金
		}/**end*/
    	return "";
	}

	//--------MS00003 QDV4.1-参数布局散乱不便操作---------//
    //-------2009.10.13 蒋锦 添加 beanid 字段需要配外部调用-------//
    public String getBeanid() {
        return beanid;
    }

    public String getStrPortCode() {
        return strPortCode;
    }

    public void setBeanid(String beanid) {
        this.beanid = beanid;
    }

    public void setStrPortCode(String strPortCode) {
        this.strPortCode = strPortCode;
    }
    //---------------------------------------------------------//
}
