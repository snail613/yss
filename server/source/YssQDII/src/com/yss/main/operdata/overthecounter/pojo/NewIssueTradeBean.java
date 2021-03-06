package com.yss.main.operdata.overthecounter.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

public class NewIssueTradeBean extends BaseDataSettingBean{
    private String num;                //交易编号
    private String tradeTypeCode;      //交易类型
    private String tradeTypeName;
    private String securityCode;       //证券代码
    private String securityName;
    private String portCode;           //组合代码
    private String portName;
    private java.util.Date bargainDate;     //申购日期
    private String securityType;       //证券类型
    private String securityTypeName;
    private String investType;         //投资类型
    private String investTypeName;
    private String invMgrCode;         //投资经理
    private String invMgrName;
    private String attrClsCode;        //属性分类
    private String attrClsName;
    private java.util.Date appTransDate;          //申请业务日期
    private java.util.Date lucklyTransDate;       //中签业务日期
    private java.util.Date returnTransDate;       //返款业务日期
    private java.util.Date currentTransDate;      //流通业务日期
    private java.util.Date lockBeginDate;   //锁定开始日期
    private java.util.Date lockEndDate;     //锁定结束日期
    private double appMoney;                       //申购金额
    private double lucklyMoney;                   //中签金额
    private double returnMoney;                   //返款金额
    private double currentMoney;                   //流通金额
    private double bondIns;                 //债券利息
    private double priceMoney;              //成本单价
    private String appCashAccCode;             //申购账户代码
    private String appCashAccName;
    private String returnCashAccCode;          //返款账户代码
    private String returnCashAccName;
    private double lucklyAmount;                  //中签数量
    private double lockAmount;                  //锁定数量
    private double currentAmount;                  //流通数量
    private int lockDays;                   //锁定天数
    private String isOnlyColumn;
    private String oldNum;
    private String recycled = null;
    private NewIssueTradeBean filterType;
    
    
    public int iCheckStateAPP;//BUG4879网下新股新债业务中的提示信息存在问题 
    public int iCheckStateLucky;//BUG4879网下新股新债业务中的提示信息存在问题 
    public int iCheckStateReturn;//BUG4879网下新股新债业务中的提示信息存在问题 

    /**shashijie 2012-7-5 BUG 4941 */
    private int chkApp = 0;//申购
    private int chkLuckly = 0;//中签
    private int chkReturn = 0;//返款
    private int chkLock = 0;//锁定
    private int chkCurrent = 0;//返款
	/**end*/
    
    private int directBallot = 0;//直接中签 story3395 20130131 yeshenghong
    
    public int getDirectBallot() {
		return directBallot;
	}

	public void setDirectBallot(int directBallot) {
		this.directBallot = directBallot;
	}

	public NewIssueTradeBean() {
    }

    public NewIssueTradeBean(YssPub pub){
        setYssPub(pub);
    }

    public String buildRowStr() throws YssException{
        StringBuffer buf = new StringBuffer();
        buf.append(num).append("\t");
        buf.append(tradeTypeCode).append("\t");
        buf.append(tradeTypeName).append("\t");
        buf.append(securityCode).append("\t");
        buf.append(securityName).append("\t");
        buf.append(portCode).append("\t");
        buf.append(portName).append("\t");
        buf.append(bargainDate).append("\t");
        buf.append(securityType).append("\t");
        buf.append(investType).append("\t");
        buf.append(invMgrCode).append("\t");
        buf.append(invMgrName).append("\t");
        buf.append(attrClsCode).append("\t");
        buf.append(attrClsName).append("\t");
        buf.append(appTransDate).append("\t");
        buf.append(lucklyTransDate).append("\t");
        buf.append(returnTransDate).append("\t");
        buf.append(currentTransDate).append("\t");
        buf.append(lockBeginDate).append("\t");
        buf.append(lockEndDate).append("\t");
        buf.append(appMoney).append("\t");
        buf.append(lucklyMoney).append("\t");
        buf.append(returnMoney).append("\t");
        buf.append(currentMoney).append("\t");
        buf.append(bondIns).append("\t");
        buf.append(priceMoney).append("\t");
        buf.append(appCashAccCode).append("\t");
        buf.append(appCashAccName).append("\t");
        buf.append(returnCashAccCode).append("\t");
        buf.append(returnCashAccName).append("\t");
        buf.append(lucklyAmount).append("\t");
        buf.append(lockAmount).append("\t");
        buf.append(currentAmount).append("\t");
        buf.append(lockDays).append("\t");
        buf.append(directBallot).append("\t");//直接中签 story3395 20130131 yeshenghong
        buf.append(super.buildRecLog());
        return buf.toString();

    }
    
    /**
     * add by zhangjun 2012.06.39
     * BUG4879
     * 对中签返款数据进行修改时，应该加载出相关联的申购交易数据中的申购金额。
     */
    public String strBuildRow() throws YssException{
        StringBuffer buf = new StringBuffer();
        
        buf.append(appTransDate).append("\t");
        buf.append(lucklyTransDate).append("\t");
        buf.append(returnTransDate).append("\t");
       
        buf.append(appMoney).append("\t");
        buf.append(lucklyMoney).append("\t");
        buf.append(returnMoney).append("\t");
        
        buf.append(appCashAccCode).append("\t");
        buf.append(appCashAccName).append("\t");
        buf.append(returnCashAccCode).append("\t");
        buf.append(returnCashAccName).append("\t");
        buf.append(lucklyAmount).append("\t");
        buf.append(iCheckStateAPP).append("\t");
        buf.append(iCheckStateLucky).append("\t");
        buf.append(iCheckStateReturn).append("\t");
        return buf.toString();

    }

    public void parseRowStr(String sRowStr) throws YssException{
        String sTmpStr = "";
        String[] tmpAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            recycled = sRowStr; //把未解析的字符串先赋给sRecycled
            tmpAry = sTmpStr.split("\t");
            num = tmpAry[0];
            //edit by songjie 2011.04.08 BUG 1557 QDV4赢时胜(测试)2011年03月23日2_B
            tradeTypeCode = (tmpAry[1].equals(""))?" ":tmpAry[1];
            securityCode = tmpAry[2];
            portCode = tmpAry[3];
            bargainDate = YssFun.toDate(tmpAry[4]);
            securityType = tmpAry[5];
            investType = tmpAry[6];
            invMgrCode = tmpAry[7];
            attrClsCode = tmpAry[8];
            appTransDate = YssFun.toDate(tmpAry[9]);
            lucklyTransDate = YssFun.toDate(tmpAry[10]);
            returnTransDate = YssFun.toDate(tmpAry[11]);
            currentTransDate = YssFun.toDate(tmpAry[12]);
            lockBeginDate = YssFun.toDate(tmpAry[13]);
            lockEndDate = YssFun.toDate(tmpAry[14]);
            appMoney = YssFun.toDouble(tmpAry[15]);
            lucklyMoney = YssFun.toDouble(tmpAry[16]);
            returnMoney = YssFun.toDouble(tmpAry[17]);
            currentMoney = YssFun.toDouble(tmpAry[18]);
            bondIns = YssFun.toDouble(tmpAry[19]);
            priceMoney = YssFun.toDouble(tmpAry[20]);
            appCashAccCode = tmpAry[21];
            returnCashAccCode = tmpAry[22];
            lucklyAmount = YssFun.toDouble(tmpAry[23]);
            lockAmount = YssFun.toDouble(tmpAry[24]);
            currentAmount = YssFun.toDouble(tmpAry[25]);
            lockDays = YssFun.toInt(tmpAry[26]);
            isOnlyColumn = tmpAry[27];
            oldNum = tmpAry[28];

            checkStateId = YssFun.toInt(tmpAry[29]);
            /**shashijie 2012-7-5 BUG 4941 是否可编辑状态 */
            chkApp = YssFun.toInt(tmpAry[30]);//申购
            chkLuckly = YssFun.toInt(tmpAry[31]);//中签
            chkReturn = YssFun.toInt(tmpAry[32]);//返款
            chkLock = YssFun.toInt(tmpAry[33]);//锁定
            chkCurrent = YssFun.toInt(tmpAry[34]);//返款
			/**end*/
            directBallot = YssFun.toInt(tmpAry[35]);//直接中签 story3395 20130131 yeshenghong
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new NewIssueTradeBean(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception ex) {
            throw new YssException("后台解析新股新债数据出错！", ex);
        }
    }

    /**
     * add by songjie 2012.09.26 BUG 5853 QDV4海富通2012年09月25日01_B
     * @param rs
     * @throws YssException
     */
    public void setNewIssueInfo(ResultSet rs) throws YssException{
    	try {
            this.num = rs.getString("FNum");
            tradeTypeCode = rs.getString("FTradeTypeCode"); //交易类型
            securityCode = rs.getString("FSecurityCode"); //证券代码
            securityName = rs.getString("FSecurityName");
            portCode = rs.getString("FPortCode"); //组合代码
            portName = rs.getString("FPortName"); //组合代码
            bargainDate = rs.getDate("FBARGAINDATE"); //申购日期
            securityType = rs.getString("FSecurityType"); //证券类型
            securityTypeName = rs.getString("FSecurityTypeName"); //证券类型
            investType = rs.getString("FInvestType"); //投资类型
            investTypeName = rs.getString("FInvestTypeName"); //投资类型
            invMgrCode = rs.getString("FInvMgrCode"); //投资经理
            invMgrName = rs.getString("FInvMgrName"); //投资经理
            attrClsCode = rs.getString("FAttrClsCode"); //属性分类
            attrClsName = rs.getString("FAttrClsName"); //属性分类
            appTransDate = rs.getDate("FSGTransDate"); //申请业务日期
            lucklyTransDate = rs.getDate("FZQTransDate"); //中签业务日期
            returnTransDate = rs.getDate("FFKTransDate"); //返款业务日期
            currentTransDate = rs.getDate("FLTTransDate"); //流通业务日期
            lockBeginDate = rs.getDate("FLockBeginDate"); //锁定开始日期
            lockEndDate = rs.getDate("FLockEndDate"); //锁定结束日期
            appMoney = rs.getDouble("FSGMoney");//申购金额
            lucklyMoney = rs.getDouble("FZQMoney"); //中签金额
            returnMoney = rs.getDouble("FFKMoney"); //返款金额
            currentMoney = rs.getDouble("FLTMoney"); //流通金额
            bondIns = rs.getDouble("FBondIns"); //债券利息
            priceMoney = rs.getDouble("FPriceMoney"); //成本单价
            appCashAccCode = rs.getString("FSGCashAccCode"); //申购账户代码
            appCashAccName = rs.getString("FSGCashAccName");
            returnCashAccCode = rs.getString("FFKCashAccCode"); //返款账户代码
            returnCashAccName = rs.getString("FFKCashAccName");
            lucklyAmount = rs.getDouble("FZQAmount"); //中签数量
            lockAmount = rs.getDouble("FSDAmount"); //锁定数量
            currentAmount = rs.getDouble("FLTAmount"); //流通数量
            lockDays = rs.getInt("FLockDays"); //锁定天数
            directBallot = rs.getInt("FDirBallot");//直接中签 story3395 20130131 yeshenghong
            this.checkStateId = rs.getInt("FCheckState");
            //add by songjie 2012.09.25  BUG 5853 QDV4海富通2012年09月25日01_B 网下新股新债业务非自审功能有问题
            super.setRecLog(rs);
        } catch (Exception ex) {
            throw new YssException(ex);
        }
    }
    
    public void setNewIssueAttr(ResultSet rs) throws YssException{
        try {
            this.num = rs.getString("FNum");
            tradeTypeCode = rs.getString("FTradeTypeCode"); //交易类型
            securityCode = rs.getString("FSecurityCode"); //证券代码
            securityName = rs.getString("FSecurityName");
            portCode = rs.getString("FPortCode"); //组合代码
            portName = rs.getString("FPortName"); //组合代码
            bargainDate = rs.getDate("FBARGAINDATE"); //申购日期
            securityType = rs.getString("FSecurityType"); //证券类型
            securityTypeName = rs.getString("FSecurityTypeName"); //证券类型
            investType = rs.getString("FInvestType"); //投资类型
            investTypeName = rs.getString("FInvestTypeName"); //投资类型
            invMgrCode = rs.getString("FInvMgrCode"); //投资经理
            invMgrName = rs.getString("FInvMgrName"); //投资经理
            attrClsCode = rs.getString("FAttrClsCode"); //属性分类
            attrClsName = rs.getString("FAttrClsName"); //属性分类
            appTransDate = rs.getDate("FSGTransDate"); //申请业务日期
            lucklyTransDate = rs.getDate("FZQTransDate"); //中签业务日期
            returnTransDate = rs.getDate("FFKTransDate"); //返款业务日期
            currentTransDate = rs.getDate("FLTTransDate"); //流通业务日期
            lockBeginDate = rs.getDate("FLockBeginDate"); //锁定开始日期
            lockEndDate = rs.getDate("FLockEndDate"); //锁定结束日期
            appMoney = rs.getDouble("FSGMoney");//申购金额
            lucklyMoney = rs.getDouble("FZQMoney"); //中签金额
            returnMoney = rs.getDouble("FFKMoney"); //返款金额
            currentMoney = rs.getDouble("FLTMoney"); //流通金额
            bondIns = rs.getDouble("FBondIns"); //债券利息
            priceMoney = rs.getDouble("FPriceMoney"); //成本单价
            appCashAccCode = rs.getString("FSGCashAccCode"); //申购账户代码
            appCashAccName = rs.getString("FSGCashAccName");
            returnCashAccCode = rs.getString("FFKCashAccCode"); //返款账户代码
            returnCashAccName = rs.getString("FFKCashAccName");
            lucklyAmount = rs.getDouble("FZQAmount"); //中签数量
            lockAmount = rs.getDouble("FSDAmount"); //锁定数量
            currentAmount = rs.getDouble("FLTAmount"); //流通数量
            lockDays = rs.getInt("FLockDays"); //锁定天数
            directBallot = rs.getInt("FDirBallot");//直接中签 story3395 20130131 yeshenghong
            this.checkStateId = rs.getInt("FCheckState");
        } catch (Exception ex) {
            throw new YssException(ex);
        }
    }

    public String getAttrClsCode() {
        return attrClsCode;
    }

    public java.util.Date getBargainDate() {
        return bargainDate;
    }

    public double getBondIns() {
        return bondIns;
    }

    public String getInvestType() {
        return investType;
    }

    public String getInvMgrCode() {
        return invMgrCode;
    }

    public java.util.Date getLockBeginDate() {
        return lockBeginDate;
    }

    public int getLockDays() {
        return lockDays;
    }

    public java.util.Date getLockEndDate() {
        return lockEndDate;
    }

    public String getNum() {
        return num;
    }

    public String getPortCode() {
        return portCode;
    }

    public double getPriceMoney() {
        return priceMoney;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public String getSecurityType() {
        return securityType;
    }

    public String getTradeTypeCode() {
        return tradeTypeCode;
    }

    public String getAppCashAccCode() {
        return appCashAccCode;
    }

    public double getAppMoney() {
        return appMoney;
    }

    public java.util.Date getAppTransDate() {
        return appTransDate;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public java.util.Date getCurrentTransDate() {
        return currentTransDate;
    }

    public double getCurrentMoney() {
        return currentMoney;
    }

    public double getLockAmount() {
        return lockAmount;
    }

    public double getLucklyAmount() {
        return lucklyAmount;
    }

    public double getLucklyMoney() {
        return lucklyMoney;
    }

    public java.util.Date getLucklyTransDate() {
        return lucklyTransDate;
    }

    public String getReturnCashAccCode() {
        return returnCashAccCode;
    }

    public double getReturnMoney() {
        return returnMoney;
    }

    public java.util.Date getReturnTransDate() {
        return returnTransDate;
    }

    public String getInvestTypeName() {
        return investTypeName;
    }

    public String getAttrClsName() {
        return attrClsName;
    }

    public String getInvMgrName() {
        return invMgrName;
    }

    public String getPortName() {
        return portName;
    }

    public String getReturnCashAccName() {
        return returnCashAccName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public String getSecurityTypeName() {
        return securityTypeName;
    }

    public String getTradeTypeName() {
        return tradeTypeName;
    }

    public String getAppCashAccName() {
        return appCashAccName;
    }

    public NewIssueTradeBean getFilterType() {
        return filterType;
    }

    public String getIsOnlyColumn() {
        return isOnlyColumn;
    }

    public String getRecycled() {
        return recycled;
    }

    public String getOldNum() {
        return oldNum;
    }

    public void setAttrClsCode(String attrClsCode) {
        this.attrClsCode = attrClsCode;
    }

    public void setBargainDate(java.util.Date bargainDate) {
        this.bargainDate = bargainDate;
    }

    public void setBondIns(double bondIns) {
        this.bondIns = bondIns;
    }

    public void setInvestType(String investType) {
        this.investType = investType;
    }

    public void setInvMgrCode(String invMgrCode) {
        this.invMgrCode = invMgrCode;
    }

    public void setLockBeginDate(java.util.Date lockBeginDate) {
        this.lockBeginDate = lockBeginDate;
    }

    public void setLockDays(int lockDays) {
        this.lockDays = lockDays;
    }

    public void setLockEndDate(java.util.Date lockEndDate) {
        this.lockEndDate = lockEndDate;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPriceMoney(double priceMoney) {
        this.priceMoney = priceMoney;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setSecurityType(String securityType) {
        this.securityType = securityType;
    }

    public void setTradeTypeCode(String tradeTypeCode) {
        this.tradeTypeCode = tradeTypeCode;
    }

    public void setAppCashAccCode(String appCashAccCode) {
        this.appCashAccCode = appCashAccCode;
    }

    public void setAppMoney(double appMoney) {
        this.appMoney = appMoney;
    }

    public void setAppTransDate(java.util.Date appTransDate) {
        this.appTransDate = appTransDate;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setCurrentTransDate(java.util.Date currentTransDate) {
        this.currentTransDate = currentTransDate;
    }

    public void setCurrentMoney(double currentMoney) {
        this.currentMoney = currentMoney;
    }

    public void setLockAmount(double lockAmount) {
        this.lockAmount = lockAmount;
    }

    public void setLucklyAmount(double lucklyAmount) {
        this.lucklyAmount = lucklyAmount;
    }

    public void setLucklyMoney(double lucklyMoney) {
        this.lucklyMoney = lucklyMoney;
    }

    public void setLucklyTransDate(java.util.Date lucklyTransDate) {
        this.lucklyTransDate = lucklyTransDate;
    }

    public void setReturnCashAccCode(String returnCashAccCode) {
        this.returnCashAccCode = returnCashAccCode;
    }

    public void setReturnMoney(double returnMoney) {
        this.returnMoney = returnMoney;
    }

    public void setReturnTransDate(java.util.Date returnTransDate) {
        this.returnTransDate = returnTransDate;
    }

    public void setInvestTypeName(String investTypeName) {
        this.investTypeName = investTypeName;
    }

    public void setAttrClsName(String attrClsName) {
        this.attrClsName = attrClsName;
    }

    public void setInvMgrName(String invMgrName) {
        this.invMgrName = invMgrName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setReturnCashAccName(String returnCashAccName) {
        this.returnCashAccName = returnCashAccName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public void setSecurityTypeName(String securityTypeName) {
        this.securityTypeName = securityTypeName;
    }

    public void setTradeTypeName(String tradeTypeName) {
        this.tradeTypeName = tradeTypeName;
    }

    public void setAppCashAccName(String appCashAccName) {
        this.appCashAccName = appCashAccName;
    }

    public void setFilterType(NewIssueTradeBean filterType) {
        this.filterType = filterType;
    }

    public void setIsOnlyColumn(String isOnlyColumn) {
        this.isOnlyColumn = isOnlyColumn;
    }

    public void setRecycled(String recycled) {
        this.recycled = recycled;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

	/**返回 chkApp 的值*/
	public int getChkApp() {
		return chkApp;
	}

	/**传入chkApp 设置  chkApp 的值*/
	public void setChkApp(int chkApp) {
		this.chkApp = chkApp;
	}

	/**返回 chkLuckly 的值*/
	public int getChkLuckly() {
		return chkLuckly;
	}

	/**传入chkLuckly 设置  chkLuckly 的值*/
	public void setChkLuckly(int chkLuckly) {
		this.chkLuckly = chkLuckly;
	}

	/**返回 chkReturn 的值*/
	public int getChkReturn() {
		return chkReturn;
	}

	/**传入chkReturn 设置  chkReturn 的值*/
	public void setChkReturn(int chkReturn) {
		this.chkReturn = chkReturn;
	}

	/**返回 chkLock 的值*/
	public int getChkLock() {
		return chkLock;
	}

	/**传入chkLock 设置  chkLock 的值*/
	public void setChkLock(int chkLock) {
		this.chkLock = chkLock;
	}

	/**返回 chkCurrent 的值*/
	public int getChkCurrent() {
		return chkCurrent;
	}

	/**传入chkCurrent 设置  chkCurrent 的值*/
	public void setChkCurrent(int chkCurrent) {
		this.chkCurrent = chkCurrent;
	}
}
