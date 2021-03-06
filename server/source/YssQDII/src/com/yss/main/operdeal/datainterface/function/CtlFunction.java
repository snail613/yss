package com.yss.main.operdeal.datainterface.function;

import com.yss.dsub.*;
import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.function.pojo.*;
import com.yss.util.*;

public class CtlFunction
    extends BaseBean {
    private PromptSourceBean promtSource = null;
    private BaseDaoOperDeal baseOper = null;
    public CtlFunction() {
    }

    public void init(String sSource) {
        promtSource = new PromptSourceBean();
        promtSource.parseSource(sSource);
    }

    public String doFunctions() throws YssException {
        String sResult = "";
        BaseFunction function = null;
        //可根据公式来处理
        if (promtSource.getFunctionName().equals("RelaFieldIsNull")) {
            function = new RelaFieldIsNullFunctionBean();
        } else if (promtSource.getFunctionName().equals("CheckResult")) {
            function = new CheckResultFunctionBean();
        } else if (promtSource.getFunctionName().equals("FieldIsNull")) {
            function = new FieldIsNullFunction();
            //add by xuqiji 20090522 QDV4交银施罗德2009年4月29日01_AB MS00426 数据源检查与改为多字段的检查---
        } else if (promtSource.getFunctionName().equals("RelaFieldIsNullX")) {
            function = new RelaFieldIsNullFunctionBean();
            //---------------------------------end----------------------------------------//
        } else {
            function = new BaseFunction();
        }
        //
        function.init(promtSource);
        function.setBaseOper(baseOper);
        function.setYssPub(pub);
        sResult = function.FormulaFunctions();
        return sResult;
    }

    public void setBaseOper(BaseDaoOperDeal baseOper) {
        this.baseOper = baseOper;
    }
}
