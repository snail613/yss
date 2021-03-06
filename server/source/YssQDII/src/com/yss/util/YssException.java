package com.yss.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * <p>Title: </p>
 * <p>Description: 异常类，所有程序抛出的异常都用这个异常类<br>
 * 需要向上传递的异常应该</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author alex
 * @version 1.0
 */

public final class YssException
    extends Exception {
    private String errMsg = null; //综合错误信息
    private String information = null; //存贮自定义的系统异常
    private String detailInformation = null;
    private String detail = null; //存储错误的具体描述
    private boolean isYss = false; //是否为运行时异常
   
	private Logger log = Logger.getLogger("E");
	
	static {
		 //============ 将日志写入到log文件    2010.02.08 add by jiangshichao ==================================== 
        Logger log = Logger.getLogger("D"); 
        //------------------------------------------------------------------
	}
	
    public YssException() {
    }

    /**
     * 在抛出这个异常时可以指定错误信息msg
     * @param msg：指定的确定错误信息
     */
    public YssException(String msg, Exception ex) {
        buildMsg(msg, ex);
        ex.printStackTrace();
        log.error(errMsg);
        information = msg;
        initCause(ex);
    }

    /**
     * 自行抛出一个错误信息
     * @param msg String
     */
    public YssException(String msg) {
        errMsg = "&" + msg; //手动抛出的异常以"&"开头,区别系统异常 by caocheng 2009.02.06 MS00004 QDV4.1-2009.2.1_09A
        log.error("信息："+errMsg);
    }

    /**
     * 没有自定义消息，直接抛出成YssException，后续可自行用insertMessage添加消息
     * @param e Exception
     */
    public YssException(Exception e) {
        buildMsg("", e);
        log.error("信息："+errMsg);
        information = "";
        initCause(e);
    }

    /**
     * 添加自定义错误信息，一般和YssException(Exception)配套使用
     * @param msg String
     */
    public void insertMessage(String msg) {
        errMsg = msg + YssCons.YSS_LINESPLITMARK + errMsg;
    }

//返回自动格式化的错误信息
    public String getMessage() {
        return errMsg;

    }

    private void buildMsg(String msg, Exception ex) {
        StackTraceElement[] ss = ex.getStackTrace();

        //具体异常信息
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getClassName().indexOf("com.yss") >= 0) {
                errMsg = ss[i].toString();
                break;
            }
        }
        if (errMsg == null || errMsg.length() == 0) {
            errMsg = ex.getMessage();
        } else {
            errMsg += "\r\n\t" + ex.getMessage();
        }

        //组合成整个异常信息
        if (msg.length() == 0) {
            errMsg = "Error Information：" + errMsg;

        } else {
            errMsg = msg + "\r\n\r\nDetail：" + errMsg;

        }
    }

    /**
     * 拼接运行时错误信息。
     * @param ex Throwable
     * @return String
     */
    private String buildLinkMsg(Throwable ex) {
        StackTraceElement[] ss = ex.getStackTrace();
        String detialMsg = "";
//若需全部的具体异常信息，则用屏蔽的代码
//      for (int i = 0; i < ss.length; i++) {
//         if (ss[i].getClassName().indexOf("com.yss") >= 0) {
//            if (detialMsg.length() == 0){
//               detialMsg = ss[i].toString();
//            }
//            else{
//               detialMsg += "\r\n" + ss[i].toString();
//            }
//         }
//      }
        for (int i = 0; i < ss.length; i++) {
            if (ss[i].getClassName().indexOf("com.yss") >= 0) {
                detialMsg += "ClassName:" + ss[i].getClassName() + "\r\n";
                detialMsg += "MethodName:" + ss[i].getMethodName() + "\r\n";
                detialMsg += "LineNumber:" + ss[i].getLineNumber() + "\r\n";
                detialMsg += "Message:" + ex.getMessage();
                break;
            }
        }
        return detialMsg.length() > 0 ? "Detail: \r\n" + detialMsg : "";
    }

    public String toString() { //异常信息传给客户端时做处理
    	log.error("信息："+errMsg);
        return errMsg;
    }

    /**
     * 拼凑异常信息描述字符串
     * MS00004 QDV4.1-2009.2.1_09A by caocheng 2009.02.06
     * @return String
     */
    public String getLocalizedMessage() {
        Throwable lastCause = null; //最底层异常
        String linkDetail = "";
        lastCause = getException(this); //递归调用,遍历Exception获得每一层的出错信息

        if (detail.indexOf("[") > -1 && detail.indexOf("]") > -1) { //处理系统异常的详细显示信息(只显示最底层异常的描述信息)
            detail = replaceBlank(detail);
            detail = replace(detail);
        }

        if (detail.indexOf("&") > -1) { //如果detail中有&,是人为抛出异常,截取&以后的部分显示,没有&则是系统异常
            //detail = detail.substring(detail.indexOf("&")+1, detail.length());
            detail = detail.substring(detail.lastIndexOf("&") + 1, detail.length()); //QDV4赢时胜（上海）2009年4月16日02_B MS00388 获取最后一个&下的信息，不然前台提示信息中就会包括 &
        }
        linkDetail = buildLinkMsg(lastCause);
        return isYss ? detail : detail + "~@~" + linkDetail; //如果有系统异常,则把具体出错信息拼接到字符串中。
    }

    private Throwable getLastCause(Throwable ex) {
        if (ex.getCause() == null) { //如果为Null,则是最底层异常
            return ex;
        }
        return getLastCause(ex.getCause()); //不是则递归调用
    }

    /**
     *递归调用此方法,直至获得最内层的异常
     * MS00004 QDV4.1-2009.2.1_09A by caocheng 2009.02.06
     * @param ex Throwable
     * @return Throwable
     */
    private Throwable getException(Throwable ex) {
        if (null == ex.getCause()) { //判断是否为最底层异常
            if (ex instanceof YssException) { //判断异常是否为YssException的实例
                isYss = true;
                detail += ex.getMessage(); //获得描述信息
            } else {
                isYss = false;
            }
            return ex;
        } else {
            if (null == detail) { //当detail为null
                detail = "";
            }
            detail += ( (YssException) ex).getInformation();
            return getException(ex.getCause()); //递归调用此方法,直至获得最内层的异常信息描述
        }
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    /**
     * 异常信息的特殊字符处理
     * MS00004 QDV4.1-2009.2.1_09A by caocheng 2009.02.06
     * @param msg String
     * @return String
     */
    public String replaceBlank(String msg) {
        int start = msg.indexOf("["); // 第一个"["出现的位置
        int end = msg.indexOf("]"); //第一个"]"出现的位置
        StringBuffer buf = new StringBuffer(msg); //将字符串转为"StringBuffer"类型

        if (buf.toString().indexOf("]") == buf.toString().lastIndexOf("]")) { //如果第一个"]"和最后一个"]"出现的位置相同,返回字符串
            return buf.toString();
        } else { //用""替换[]中的字符
            msg = buf.replace(start, end + 1, "").toString().trim();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
			return replaceBlank(msg.toString()); //递归调用此方法,直到字符串中只有一对[]为止
        }
    }

    /**
     * 处理系统异常中的"["和"]"
     * @param msg String
     * @return String
     */
    public String replace(String msg) {
        msg = msg.replace('[', ' '); //用空格替代字符串中的"["
        msg = msg.replace(']', ' '); //用空格替代字符串中的"]"
        msg = msg.replaceAll(" ", ""); //去掉字符串中的空格
        return msg;
    }
}
