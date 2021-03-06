package com.yss.dbupdate.autoupdatetables.updatemain;

import java.util.*;

import com.yss.util.*;
import com.yss.dbupdate.BaseDbUpdate;
import com.yss.dsub.BaseBean;

/**
 *
 * <p>Title: 自动更新前的手工表结构调整</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BeforeUpdate
    extends BaseBean {
    public BeforeUpdate() {
    }

    BaseDbUpdate dbUpdate; //直接执行更新的类
    /**
     * 执行自动更新前的更新
     * modify by sunkey 20090810 实现此方法
     * @param hmInfo HashMap：返回的信息
     * @param sCurrtVerNum String：版本号
     * @return String
     * @throws YssException
     */
    public String beforeExecute(HashMap hmInfo, String sCurrtVerNum) throws YssException {
        String sResultTag = YssCons.YSS_DBUPDATE_SUCCESS;
        //记录所有涉及结构调整和数据调整的 SQL 语句
        StringBuffer sqlBuf = new StringBuffer(500000);
        //记录更新过程中出现的异常
        StringBuffer errBuf = new StringBuffer(10000);
        //记录完成更新的表名
        StringBuffer updTables = new StringBuffer(2000);
        try {
            sqlBuf = (StringBuffer) hmInfo.get("sqlinfo");
            errBuf = (StringBuffer) hmInfo.get("errinfo");
            updTables = (StringBuffer) hmInfo.get("updatetables");
            if (sCurrtVerNum.equalsIgnoreCase(".......")) {
                //do update...
            	sResultTag = "";//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            }
        } catch (Exception e) {
            errBuf.append(e);
            sResultTag = YssCons.YSS_DBUPDATE_FAIL;
        }
        return sResultTag;
    }
}
