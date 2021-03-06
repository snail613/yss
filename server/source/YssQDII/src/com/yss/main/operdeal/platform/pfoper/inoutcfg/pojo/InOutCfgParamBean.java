package com.yss.main.operdeal.platform.pfoper.inoutcfg.pojo;

import com.yss.util.*;
import com.yss.dsub.*;
import java.sql.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import com.yss.main.funsetting.RefInvokeBean;
import java.util.*;
import com.yss.main.dao.*;

public class InOutCfgParamBean
    extends BaseBean {
    //-----下面是解析 一张表的参数代码
    private Hashtable htOutParam = new Hashtable(); //装所有的参数信息
    private Hashtable htInParam = new Hashtable(); //装所有的参数信息
    private String sBeanId = ""; //对应的java的 BeanId号
    private String sModule = ""; //对应的XML配置文件
    private String sRevokeURL = ""; //查找后台的反射调用代码的参数
    //private String sPKVar="";            //保存数据库中的主键与后台变量的关系
    private String sSql = ""; //表的查询语句

    //-----下面是解析 sRevokeURL 的参数代码
    private String sRev_SerName = ""; //servlet Name
    private String sRev_BeanName = ""; //Bean Name
    private String sRev_ShowList = ""; //listview
    private String sRev_DllName = ""; //Dll Name
    private String sRev_FormName = ""; //Form Name
    private String sRev_PoJoName = ""; // PoJo Name

    //-----下面是解析 sPKVar 的参数代码
    //private String[] arrPKVar=null;

    public InOutCfgParamBean() {
    }

    /**
     * 解析, 根据一张表导出脚本信息解析
     * @param sResprStr String
     * @throws YssException
     */
    public void parseOutRowStr(String sResprStr) throws YssException {
        OutSourceParamBean outParamBean = null;
        //------ modify by wangzuochun 2010.11.17 BUG #389 导入 通用信息配置.mdb 时报错
        String[] arrRes = null;
        if(sResprStr.indexOf("\r\n") > 0){
        	arrRes = sResprStr.split("\r\n"); 
        }
        else if (sResprStr.indexOf("\n") > 0){
        	arrRes = sResprStr.split("\n");
        }
        if(arrRes ==null )
        {
        	return;
        }
        if (arrRes[0]!=null&&arrRes[0].trim().startsWith("[module]") && htOutParam.get("[module]") == null) {
        	htOutParam.put("[module]", arrRes[1]);
            sModule = arrRes[1];
        }
		//------------------- BUG #389 导入 通用信息配置.mdb 时报错 -----------------//
        if (arrRes[2].startsWith("[beanid]") && htOutParam.get("[beanid]") == null) {
            htOutParam.put("[beanid]", arrRes[3]);
            sBeanId = arrRes[3];
        }
        if (arrRes.length > 4) {
            for (int i = 4; i < arrRes.length; i += 3) {
                if (i > arrRes.length) {
                    break;
                }
                if (htOutParam.get(arrRes[i]) == null) {
                    outParamBean = new OutSourceParamBean();
                    outParamBean.setSTmpTabName(arrRes[i + 1]);
                    outParamBean.setSSqlSource(arrRes[i + 2]);
                    //htOutParam.put(arrRes[i],arrRes[i+1]+"\f\f"+arrRes[i+2]);
                    htOutParam.put(arrRes[i], outParamBean);
                }
            }
        }
    }

    /**
     * 解析, 根据一张表导入脚本信息解析
     * @param sResprStr String
     * @throws YssException
     */
    public void parseInRowStr(String sResprStr) throws YssException {
        InSourceParamBean inParamBean = null;
        /**shashijie 2012-3-9 BUG 3837 通用导入导出配置的问题 */
        String[] arrRes = sResprStr.split("\r\n");
        /**end*/
        for (int i = 0; i < arrRes.length; i += 3) {
            if (arrRes.length < i) {
                break;
            }
            if (htInParam.get(arrRes[i]) == null) {
                inParamBean = new InSourceParamBean();
                inParamBean.setSSysTab(arrRes[i + 1].split("=")[0].trim()); // modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
                inParamBean.setSTmpTab(arrRes[i + 1].split("=")[1].trim()); // modify by wangzuochun 2010.11.16  BUG #389 导入 通用信息配置.mdb 时报错 
                inParamBean.setSDelCond(arrRes[i + 2]);
                //htInParam.put(arrRes[i],arrRes[i+1]+"\f\f"+arrRes[i+2]);
                htInParam.put(arrRes[i], inParamBean);
            }
        }
    }

    /**
     * 本方法用于解析 功能调用的参数
     * @param sResprStr String
     * @throws YssException
     */
    public void parseResRowStr(String sResprStr) throws YssException {
        //"/parasetting?cmd=mtvmethod&flag=listview1,"
        //"YssOperParamSet.dll,"
        //"YssOperParamSet.FrmMTVMethod,"
        //"YssModulePojo.YssOperParamSet.ClsMTVMethod"
        String temp = "";
        String[] arrParam = null;
        arrParam = sResprStr.split(",");
        if (arrParam.length > 3) {
            temp = sResprStr.split("cmd=")[0];
            this.sRev_SerName = temp.substring(1, temp.length() - 1);
            this.sRev_BeanName = (sResprStr.split("cmd=")[1]).split("&")[0];
            this.sRev_ShowList = arrParam[0].substring(arrParam[0].indexOf("flag=") + 5);
            this.sRev_DllName = arrParam[1];
            this.sRev_FormName = arrParam[2];
            this.sRev_PoJoName = arrParam[3];
        }
    }

    //取一个JavaBean 返回一个object类型
    public Object getBean() throws YssException {
        String sTmpName = "";
        ApplicationContext ctx;
        Object obj = null;
        try {
            if (sModule.length() == 0) {
                sModule = (String) htOutParam.get("[module]");
            }
            if (sBeanId.length() == 0) {
                sBeanId = (String) htOutParam.get("[beanid]");
            }
            if (!sModule.endsWith(".xml")) {
                sModule += ".xml";
            }
            sTmpName = sModule.replaceAll(".xml", "");
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
            ctx = new FileSystemXmlApplicationContext(YssUtil.getAppConContextPath(YssCons.YSS_WebRealPath,sModule));
					
            if (sTmpName.equalsIgnoreCase("basesetting")) {
                pub.setBaseSettingCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("parasetting")) {
                pub.setParaSettingCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("accbook")) {
                pub.setAccBookCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("cashmanager")) {
                pub.setCashManagerCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("compliance")) {
                pub.setComplianceCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("cusreport")) {
                pub.setCusReportCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("datainterface")) {
                pub.setDataInterfaceCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("dayfinish")) {
                pub.setDayFinsihCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("funsetting")) {
                pub.setFunSettingCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("operdata")) {
                pub.setOperDataCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("orderadmin")) {
                pub.setOrderAdminCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("platform")) {
                pub.setPlatformCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("pretfun")) {
//                pub.setParaSettingCtx(ctx);
            	pub.setPretFunCtx(ctx);// add by xuxuming,20091209.MS00851
            } else if (sTmpName.equalsIgnoreCase("report")) {
                pub.setCusReportCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("settlement")) {
                pub.setSettlementCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("storagemanage")) {
//                pub.setParaSettingCtx(ctx);
                pub.setStorManageCtx(ctx);//add by xuxuming,20090928.MS00717,点击业务资料中的交易关联系统报错
            } else if (sTmpName.equalsIgnoreCase("taoperation")) {
                pub.setTaOperationCtx(ctx);
            } else if (sTmpName.equalsIgnoreCase("voucher")) {
                pub.setVoucherCtx(ctx);
            }
            obj = ctx.getBean(sBeanId);

        } catch (Exception ex) {
            throw new YssException("未找到关联的JavaBean", ex);
        }
        return obj;
    }

    /**
     * 获取一个反射调用代码下的调用参数
     * @return String
     * @throws YssException
     */
    public String getRevokeURL() throws YssException {
        String sURL = "";
        String sRevokeID = ""; //取反射调用代码
        BaseBean bean = null;
        Object obj = null;
        RefInvokeBean invoke = null;
        try {
            obj = getBean();
            bean = (BaseBean) obj;
            sRevokeID = bean.getRefName();
            invoke = new RefInvokeBean();
            invoke.setYssPub(pub);
            invoke.setInvokeCode(sRevokeID);
            invoke.getSetting();
            sURL = invoke.getParams();
            return sURL;
        } catch (Exception e) {
            throw new YssException("获取功能调用的参数出错", e);
        }
    }

    public void setSBeanId(String sBeanId) {
        this.sBeanId = sBeanId;
    }

    public void setSRevokeURL(String sRevokeURL) {
        this.sRevokeURL = sRevokeURL;
    }

    public void setSRev_ShowList(String sRev_ShowList) {
        this.sRev_ShowList = sRev_ShowList;
    }

    public void setSRev_SerName(String sRev_SerName) {
        this.sRev_SerName = sRev_SerName;
    }

    public void setSRev_PoJoName(String sRev_PoJoName) {
        this.sRev_PoJoName = sRev_PoJoName;
    }

    public void setSRev_FormName(String sRev_FormName) {
        this.sRev_FormName = sRev_FormName;
    }

    public void setSRev_DllName(String sRev_DllName) {
        this.sRev_DllName = sRev_DllName;
    }

    public void setSRev_BeanName(String sRev_BeanName) {
        this.sRev_BeanName = sRev_BeanName;
    }

    public void setSSql(String sSql) {
        this.sSql = sSql;
    }

    public void setSModule(String sModule) {
        this.sModule = sModule;
    }

    public void setHtOutParam(Hashtable htOutParam) {
        this.htOutParam = htOutParam;
    }

    public void setHtInParam(Hashtable htInParam) {
        this.htInParam = htInParam;
    }

    public String getSRevokeURL() {
        return sRevokeURL;
    }

    public String getSRev_ShowList() {
        return sRev_ShowList;
    }

    public String getSRev_SerName() {
        return sRev_SerName;
    }

    public String getSRev_PoJoName() {
        return sRev_PoJoName;
    }

    public String getSRev_FormName() {
        return sRev_FormName;
    }

    public String getSRev_DllName() {
        return sRev_DllName;
    }

    public String getSRev_BeanName() {
        return sRev_BeanName;
    }

    public String getSSql() {
        return sSql;
    }

    public String getSBeanId() {
        return sBeanId;
    }

    public String getSModule() {
        return sModule;
    }

    public Hashtable getHtOutParam() {
        return htOutParam;
    }

    public Hashtable getHtInParam() {
        return htInParam;
    }

}
