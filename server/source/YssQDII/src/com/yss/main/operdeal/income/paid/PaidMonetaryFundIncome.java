package com.yss.main.operdeal.income.paid;

import com.yss.util.YssException;
import java.util.ArrayList;
import com.yss.main.operdeal.opermanage.*;
import com.yss.main.parasetting.pojo.MonetaryFundBean;
import com.yss.main.parasetting.MonetaryFundAdmin;
import com.yss.manager.SecRecPayAdmin;
import com.yss.util.YssOperCons;

/**
 *
 * <p>Title: 支付货币没万份收益</p>
 *
 * <p>Description:  MS00013 QDV4.1赢时胜（上海）2009年4月20日13_A  国内基金业务 2009.06.16 蒋锦 添加</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PaidMonetaryFundIncome
    extends BaseIncomePaid {
    public PaidMonetaryFundIncome() {
    }

    /**
     *
     * @param alIncome ArrayList
     * @throws YssException
     */
    public void saveIncome(ArrayList alIncome) throws YssException {
        String sSelCodes = "";
        SecRecPayAdmin payAdmin = new SecRecPayAdmin();
        try{
            for(int i = 0; i < alIncome.size(); i++){
                MonetaryFundAdmin fund = (MonetaryFundAdmin)alIncome.get(i);
                sSelCodes += (fund.getMonetaryFundBean().getSecurityCode() + ",");
            }
            if(sSelCodes.length() > 0){
                sSelCodes = sSelCodes.substring(0, sSelCodes.length() - 1);
            }
            OpenFundManage fundManage = new OpenFundManage();
            fundManage.setYssPub(pub);
            fundManage.initOperManageInfo(dDate, portCodes);
            fundManage.getCalParams(portCodes);

            payAdmin.setYssPub(pub);
            payAdmin.addList(fundManage.getMonetaryFundIncome(sSelCodes));
            payAdmin.insert("",
                            dDate,
                            dDate,
                            "02",
                            "02TR",
                            portCodes,
                            "",
                            "",
                            sSelCodes,
                            "",
                            1,
                            true, 0, false,
                            "", "",
                            "");
        }
        catch(Exception ex){
            throw new YssException("支付货币基金收益出错！", ex);
        }

    }

}
