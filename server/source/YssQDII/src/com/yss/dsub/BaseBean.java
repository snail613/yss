package com.yss.dsub;

import java.lang.reflect.*;

import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.vsub.*;
import com.yss.main.operdeal.*;
import com.yss.pojo.sys.*;

public class BaseBean
    implements Cloneable {
    protected YssPub pub = null; //全局变量
    protected DbBase dbl = null; //数据连接已经处理
    //add by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A
    protected DbBase dblBLog = null; //业务日志数据联接
    protected YssDbFun dbFun = null;
    protected YssDbOperSql operSql = null;
    protected YssOperFun operFun = null;
    protected YssStatus runStatus = null;
    private BaseOperDeal settingOper;
    private String parseShowFields1;
    private String builderRowFields1;
    private String moduleName;
    private String funName;
    private String RefName;

    private String bSubData = ""; //操作前的子表数据

    private String aSubData = ""; //操作后的子表数据
    
    //add by songjie 2012.12.13 
    private String logData3= ""; //保存用户登录次数 和 录入错误密码的次数
    
    public String sLoggingPositionData = "";		//20130110 added by liubo.Story #2839.用于存储某次修改操作时，修改前与修改后的内容之间的差异数据。这些数据会在插入日志时存储到操作日志表

    public String getParseShowFields1() {
        return parseShowFields1;
    }

    public String getBuilderRowFields1() {
        return builderRowFields1;
    }

    public void setParseShowFields1(String parseShowFields1) {
        this.parseShowFields1 = parseShowFields1;
    }

    public void setBuilderRowFields1(String builderRowFields1) {
        this.builderRowFields1 = builderRowFields1;
    }

    public void setSettingOper(BaseOperDeal settingOper) {
        this.settingOper = settingOper;
        settingOper.setYssPub(pub);
    }

    public void setFunName(String funName) {
        this.funName = funName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setBSubData(String bSubData) {
        this.bSubData = bSubData;
    }

    public void setASubData(String aSubData) {
        this.aSubData = aSubData;
    }

    public void setRunStatus(YssStatus runStatus) {
        this.runStatus = runStatus;
    }

    public void setRefName(String RefName) {
        this.RefName = RefName;
    }

    public BaseOperDeal getSettingOper() {
        if (settingOper == null && pub != null) {//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
//            settingOper = (BaseOperDeal) pub.getOperDealCtx().getBean(
//                "baseoper");   直接实例化算了，不用这么麻烦了 hukun 2013.5.9
        	settingOper = new BaseOperDeal();
            settingOper.setYssPub(pub);
        }
        return settingOper;
    }

    public String getFunName() {
        return funName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getBSubData() {
        return bSubData;
    }

    public String getASubData() {
        return aSubData;
    }

    public YssStatus getRunStatus() {
        return runStatus;
    }

    public String getRefName() {
        return RefName;
    }

    public BaseBean() {

    }

    public BaseBean(YssPub ysspub) {
        setYssPub(ysspub);
    }

    public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
        //add by songjie 2012.11.09 STORY #2343 QDV4建行2012年3月2日04_A
        dblBLog = ysspub.getDbLinkBLog();//日志数据库链接
        dbFun = new YssDbFun(ysspub);
        operSql = new YssDbOperSql(ysspub);
        operFun = new YssOperFun(ysspub);
    }

    public void setYssRunStatus(YssStatus runStatus1) {
        runStatus = runStatus1;
    }

    public void autoParseRowStr(String sFields, String sReqStr) throws YssException {
        Method method = null;
        Class[] paramClsAry = new Class[1];
        Object[] valueAry = new Object[1];
        String[] sFieldAry = null;
        String[] sReqAry = null;
        Class[] cls = new Class[0]; //2004.04.18 添加 蒋锦
        try {
//         valueAry[0] = "";
            if (this.parseShowFields1 == null || this.parseShowFields1.length() == 0) {
                sFieldAry = sFields.split("\t");
            } else {
                sFieldAry = parseShowFields1.split("\t");
            }
            sReqAry = sReqStr.split("\t");
            Class ownerClass = this.getClass();
            for (int i = 0; i < sFieldAry.length; i++) {
                System.out.println(sFieldAry[i]);
                method = ownerClass.getMethod("get" + sFieldAry[i], cls);
                paramClsAry[0] = method.getReturnType();
                if (sReqAry[i].equalsIgnoreCase("null")) {
                    valueAry[0] = null;
                } else {
                    if (paramClsAry[0].getName().indexOf("int") >= 0) {
                        if (YssFun.isNumeric(sReqAry[i])) {
                            valueAry[0] = new Integer(sReqAry[i]);
                        } else {
                            valueAry[0] = new Integer(0);
                        }
                    } else if (paramClsAry[0].getName().indexOf("Date") >= 0) {
                        if (YssFun.isDate(sReqAry[i])) {
                            valueAry[0] = YssFun.toDate(sReqAry[i]);
                        } else {
                            valueAry[0] = null;
                        }
                    } else if (paramClsAry[0].getName().indexOf("double") >= 0) {
                        valueAry[0] = new Double(YssFun.toDouble(sReqAry[i]));
                    }
                    //20130412 added by liubo.Story #3528
                    //为boolean类型的值设置代入，用于避免boolean类型的值被全部转换为string，导致解析错误
                    //============================
                    else if(paramClsAry[0].getName().indexOf("boolean") >= 0)
                    {
                    	if (sReqAry[i].equalsIgnoreCase("True"))
                    	{
                    		valueAry[0] = new Boolean(true);
                    	}
                    	else
                    	{
                    		valueAry[0] = new Boolean(false);
                    	}
                    }
                    //==============end==============
                    else {
                        valueAry[0] = new String(sReqAry[i]);
                    }
                }
                method = ownerClass.getMethod("set" + sFieldAry[i], paramClsAry);
                method.invoke(this, valueAry);
            }
        } catch (Exception e) {
            throw new YssException("自动解析错误");
        }
    }

    public void autoParseRowStr(String sReqStr) throws YssException {
        autoParseRowStr(this.parseShowFields1, sReqStr);
    }

    public String autoBuildRowStr(String sShowFields) throws YssException {
        StringBuffer buf = new StringBuffer();
        Method method = null;
		/**shashijie 2012-7-2 STORY 2475 */
        //Object[] valueAry = new Object[1];
        Class[] reClsAry = new Class[1];
        String[] sFieldAry = null;
        //String[] sReqAry = null;
		/**end*/
        String sFieldName = "";
        String sFieldFormat = "";
        Class ownerClass = null;
        Object reObj = null;
        //-----------------2008.04.18 添加 蒋锦-------------------//
        Object[] obj = new Object[0];
        Class[] cls = new Class[0];
        //-------------------------------------------------------//
        try {
            ownerClass = this.getClass();
            sFieldAry = sShowFields.split("\t");
            for (int i = 0; i < sFieldAry.length; i++) {
                sFieldFormat = "";
                System.out.println(sFieldAry[i]);
                if (sFieldAry[i].indexOf(";") > 0) {
                    sFieldName = sFieldAry[i].split(";")[0];
                    sFieldFormat = sFieldAry[i].split(";")[1];
                } else {
                    sFieldName = sFieldAry[i];
                }
                method = ownerClass.getMethod("get" + sFieldName, cls);
                reObj = method.invoke(this, obj);
                if (reObj != null) {
                    reClsAry[0] = method.getReturnType();
                    if (reClsAry[0].getName().indexOf("Date") >= 0) {
                        reObj = YssFun.formatDate( (java.util.Date) reObj);
                    } else if (reClsAry[0].getName().indexOf("double") >= 0) {
                        if (sFieldFormat.length() > 0) {
                            reObj = YssFun.formatNumber(Double.parseDouble(reObj.
                                toString()), sFieldFormat);
                        }
                    }
                }
                buf.append(reObj).append("\t");
            }
            if (buf.length() > 1) {
                buf.setLength(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new YssException("自动解析错误");
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
