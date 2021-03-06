package com.yss.main.operdeal.income.paid;

import java.util.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.util.*;

public class BaseIncomePaid
    extends BaseBean {
    protected java.util.Date dDate;
    protected String portCodes = "";
    protected String invmgrField = "";
    protected String catField = "";
    protected String isAll = "";
    protected String paidType = "";
    //fanghaoln 20090803 MS00604 QDV4中金2009年7月28日02_B
    public String isCheckData="true";//fanghaoln 20090625 MS00537  QDV4海富通2009年06月21日01_AB 增加一个是否审核的功能
    //---add by songjie 2012.09.20 STORY #2344 QDV4建行2012年3月2日05_A start---//
    public String logInfo = "";//日志信息
    public String logSumCode = "";//汇总日志代码
    public SingleLogOper logOper = null;
    public String operType = "";//业务子类型
    //---add by songjie 2012.09.20 STORY #2344 QDV4建行2012年3月2日05_A end---//
    
    public BaseIncomePaid() {
    }

    /**
     * calculateIncome
     *
     * @param bean IYssConvert
     */
    public void calculateIncome(IYssConvert bean) throws YssException {
    }

    /**
     * getIncomes
     *
     * @return ArrayList
     */
    public ArrayList getIncomes() throws YssException {
        return null;
    }

    /**
     * initIncomeCalculate
     *
     * @param filterBean BaseBean
     * @param dBeginDate Date
     * @param dEndDate Date
     * @param sPortCodes String
     * @param sOtherParam String
     */
    public void initIncomeCalculate(BaseBean filterBean, Date dBeginDate,
                                    Date dEndDate, String sPortCodes,
                                    String sOtherParam) throws YssException {
        String[] others = sOtherParam.split("\t"); //edit by jc
        this.dDate = dBeginDate;
        this.portCodes = sPortCodes;
        this.isAll = others.length == 0 ? "" : others[0]; //没有其他参数时确保字段为空字符串 081017 edit by jc
        if (others.length > 1) { //edit by jc
            this.paidType = others[1];
        }
        invmgrField = this.getSettingOper().getStorageAnalysisField(YssOperCons.
            YSS_KCLX_Security, YssOperCons.YSS_KCPZ_InvMgr);
        catField = this.getSettingOper().getStorageAnalysisField(YssOperCons.
            YSS_KCLX_Cash, YssOperCons.YSS_KCPZ_CatType);
    }

    /**
     * saveIncome
     *
     * @param alIncome ArrayList
     */
    public void saveIncome(ArrayList alIncome) throws YssException {
    }
    
  //20130216 added by liubo.Story #3414.支付两费时自动生成划款手续费
    public int calcCommission(ArrayList alIncome) throws YssException
    {
    	return 0;
    }
}
