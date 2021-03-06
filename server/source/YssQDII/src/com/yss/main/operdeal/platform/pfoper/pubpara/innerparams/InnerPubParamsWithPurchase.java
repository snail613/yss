package com.yss.main.operdeal.platform.pfoper.pubpara.innerparams;

import java.util.*;

import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 * MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
 */
public class InnerPubParamsWithPurchase
    extends BaseInnerPubParamsDeal {
    public InnerPubParamsWithPurchase() {
    }

    /**
     * getInnerPubParams
     *
     * @param subParams HashMap
     * @param portCode String
     * @throws YssException
     * @todo Implement this
     *   com.yss.main.operdeal.platform.pfoper.pubpara.innerparams.BaseInnerPubParamsDeal
     *   method
     */
    public void getInnerPubParams(HashMap subParams, String portCode) throws
        YssException {
        String returnValue = null;
        CtlPubPara pubPara = null;
        pubPara = new CtlPubPara();
        pubPara.setYssPub(pub);

        //------ 银行间回购交易费用入成本的通用参数设置 -----------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "CboYesOrNo",
            "inner_purchaseBIC"); //获取银行间回购交易费用入成本的通用参数设置
        subParams.put(YssOperCons.YSS_INNER_PURCHASEBIC,
                      convertToBoolean(returnValue)); //以银行间回购交易费用入成本的key放入容器
        //----------------------------------------------------------------------------------------------------------------------------

        //------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "CboYesOrNo",
            "inner_purchaseEIC"); //获取交易所回购交易费用入成本的通用参数设置
        subParams.put(YssOperCons.YSS_INNER_PURCHASEEIC,
                      convertToBoolean(returnValue)); //以交易所回购交易费用入成本的key放入容器
        //-----------------------------------------------------------------------------------------------------------------------------

        //------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "cboHeadOrTrail",
            "inner_purchaseBnT"); //获取银行间回购计息方式的通用参数设置
        subParams.put(YssOperCons.YSS_INNER_PURCHASEBNT,
                      convertToBoolean(returnValue)); //以交易所回购交易费用入成本的key放入容器
        //-----------------------------------------------------------------------------------------------------------------------------

        //------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "cboHeadOrTrail",
            "inner_purchaseExT"); //获取交易所回购计息方式的通用参数设置
        subParams.put(YssOperCons.YSS_INNER_PURCHASEEXT,
                      convertToBoolean(returnValue)); //以交易所回购交易费用入成本的key放入容器
        //-----------------------------------------------------------------------------------------------------------------------------

        //------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "CboYesOrNo",
            "inner_purchaseWFee"); //回购（包括交易所和银行间）计息包含交易费用
        subParams.put(YssOperCons.YSS_INNER_PURCHASEWFEE,
                      convertToBoolean(returnValue)); //以交易所回购交易费用入成本的key放入容器
        //-----------------------------------------------------------------------------------------------------------------------------

        //------ 交易所回购交易费用入成本的通用参数设置 ------------------------------------------------------------------------------------
        returnValue = pubPara.getPurchasePortParams(portCode, "CboYesOrNo",
            "inner_purchasePED"); //回购计息凭证当日计提
        subParams.put(YssOperCons.YSS_INNER_PURCHASEPED,
                      convertToBoolean(returnValue)); //以交易所回购交易费用入成本的key放入容器
        //-----------------------------------------------------------------------------------------------------------------------------
    }
}
