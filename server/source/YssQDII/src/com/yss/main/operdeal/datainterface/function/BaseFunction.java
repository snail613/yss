package com.yss.main.operdeal.datainterface.function;

import com.yss.main.operdeal.datainterface.*;
import com.yss.main.operdeal.datainterface.function.pojo.*;
import com.yss.main.syssetting.*;
import com.yss.pojo.message.*;
import com.yss.util.*;

public class BaseFunction
    extends BaseDaoOperDeal {
    protected PromptSourceBean PromtSource = null;
    protected PrepMessageBean message = null;
    protected BaseDaoOperDeal baseOper = null;
    public BaseFunction() {
    }

    public void init(PromptSourceBean promt) {
        this.PromtSource = promt;
        message = new PrepMessageBean();
    }

    //通过此方法来解析公式并获取公式的值
    //可按照公式名来建继承类，并根据具体的公式的参数来分别处理
    public String FormulaFunctions() throws YssException {
        return "";
    }

    //通过系统数据字典的表类型（系统表与临时表来判断表）来为表加前缀。
    protected String getYssTableName(String tableName) throws YssException {
        DataDictBean dict = new DataDictBean(pub);
        int type = dict.getTabType(tableName);
        if (type == 0) { //系统表
            tableName = pub.yssGetTableName(tableName);
        }
        return tableName;
    }

    public void setBaseOper(BaseDaoOperDeal baseOper) {
        this.baseOper = baseOper;
    }
}
