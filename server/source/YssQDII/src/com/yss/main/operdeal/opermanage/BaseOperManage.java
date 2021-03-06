package com.yss.main.operdeal.opermanage;

import java.util.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: 业务处理基类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 * sj MS00014  QDV4.1赢时胜（上海）2009年4月20日14_A
 */
public abstract class BaseOperManage
    extends BaseBean {
    protected Date dDate = null; //业务日期
    protected String sPortCode = null; //组合
    protected String sMsg = "";//【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    
    //add by songjie 2012.09.06 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
    public boolean comeFromBsnDeal = false;//由业务处理调用
    
    public BaseOperManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public abstract void initOperManageInfo(Date dDate, String portCode) throws YssException;

    /**
     * 执行业务处理
     * @throws YssException
     */
    public abstract void doOpertion() throws YssException;
    
    
  //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 start
    public void setMsg(String msg){
    	this.sMsg = msg;
    }

    public String getMsg(){
    	return sMsg;
    }
  //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 end 
}
