package com.yss.main.operdeal.businesswork.futures.futuresvaluation;

import java.sql.*;
import java.util.*;

import com.yss.main.operdata.futures.*;
import com.yss.main.operdeal.businesswork.*;
import com.yss.util.*;

public class FuturesValuation
    extends BaseBusinWork {
    public FuturesValuation() {
    }

    public String doOperation(String sType) throws YssException {
        ArrayList alMtvMethod = new ArrayList();
        ArrayList alStockRela = new ArrayList();
        FuturesTradeRelaAdmin futTrdAdmin = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            futTrdAdmin = new FuturesTradeRelaAdmin();
            futTrdAdmin.setYssPub(pub);
            FuturesTradeRelaCal relaCal = new FuturesTradeRelaCal();
            relaCal.setYssPub(pub);

            bTrans = true;
            conn.setAutoCommit(false);
            futTrdAdmin.deleteData(this.workDate,this.portCodes);//add by xxm,MS00930，加上组合代码，不能全部删除

            //获取估值方法
            alMtvMethod = getValuationMethods();
            //计算开仓交易库存余额和平仓交易投资收益
            alStockRela = relaCal.getOpenTradeAmountAndCloseTradeIncome(this.workDate, this.portCodes, alMtvMethod);
            futTrdAdmin.saveMutliSetting(alStockRela, conn);
            conn.commit();

            //计算开仓交易的估值增值
            relaCal.getTodayStockFutruesRelaData(this.workDate, this.portCodes, alMtvMethod);
            conn.commit();

            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

}
