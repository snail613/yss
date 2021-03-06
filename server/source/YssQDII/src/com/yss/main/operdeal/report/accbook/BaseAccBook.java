package com.yss.main.operdeal.report.accbook;

import java.util.Date;
import java.util.HashMap;
import java.sql.*;

import com.yss.main.operdeal.report.*;
import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.report.CommonRepBean;

public class BaseAccBook
    extends BaseBuildCommonRep {
    protected CommonRepBean repBean;
    protected String sAccBookDefine = ""; //台帐定义
    protected String sAccBookLink = ""; //查询链接
    protected Date dBeginDate; //查询开始日期
    protected Date dEndDate; //查询结束日期
    protected String sCheckItems = ""; //查询所选项
    protected String sReportType = ""; //报表类型
    protected String sAccBookDefineName = ""; //台帐定义名称
    protected String[] aryAccBookDefine = null; //将台帐定义解析后的数组
    protected String[] aryAccBookLink = null; //将查询链接解析后的数组
    protected String[] aryBookDefineName = null; //将台帐定义名称解析后的数组

    protected boolean bIsPort = false; //是否需要取组合货币
    protected boolean bIsRecPay = false; //链接中是否选择了应收应付

    protected HashMap hmSelectField; //储存链接代码对应的 SELECT 语句选取的字段
    protected HashMap hmFieldRela; //储存链接代码对应的字段
    protected HashMap hmFieldIndRela;
    protected HashMap hmTableRela; //储存关联表 SQL 语句

    public String getAccBookDefine() {
        return this.sAccBookDefine;
    }

    public String getAccBookLink() {
        return this.sAccBookLink;
    }

    public Date getBeginDate() {
        return this.dBeginDate;
    }

    public Date getEndDate() {
        return this.dEndDate;
    }

    public String getCheckItems() {
        return this.sCheckItems;
    }

    public String getReportType() {
        return this.sReportType;
    }

    public String getAccBookDefineName() {
        return this.sAccBookDefineName;
    }

    public void setAccBookDefine(String accBookDefine) {
        this.sAccBookDefine = accBookDefine;
    }

    public void setAccBookLink(String accBookLink) {
        this.sAccBookLink = accBookLink;
    }

    public void setBeginDate(Date beginDate) {
        this.dBeginDate = beginDate;
    }

    public void setEndDate(Date endDate) {
        this.dEndDate = endDate;
    }

    public void setCheckItems(String checkItems) {
        this.sCheckItems = checkItems;
    }

    public void setReportType(String reportType) {
        this.sReportType = reportType;
    }

    public void setAccBookDefineName(String accBookDefineName) {
        this.sAccBookDefineName = accBookDefineName;
    }

    public BaseAccBook() {
    }

    /**完成初始化
     * initBuildReport
     *
     * @param bean BaseBean: 通用报表类
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        //解析前台传入的条件字符串
        this.parseRowStr(this.repBean.getRepCtlParam());
        this.initHashTable();

    }

    /**
     * buildReport
     *
     * @param sType String
     * @return String
     */
    // public String buildReport(String sType) throws YssException {
    //   String sResult = "";
    //    return sResult;
    // }

    /**
     * 将前台传入的条件进行解析
     * @param sRowStr String: 使用协议包装的条件字符串
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        try {
            if (sRowStr.equals("")) {
                return;
            }
            String reqAry[] = null;
            reqAry = sRowStr.split("\n"); //这里是要获得参数
            dBeginDate = YssFun.toDate(reqAry[0].split("\r")[1]);
            dEndDate = YssFun.toDate(reqAry[1].split("\r")[1]);
            sAccBookDefine = reqAry[2].split("\r")[1];
            sAccBookDefineName = reqAry[3].split("\r")[1];
            sAccBookLink = reqAry[4].split("\r")[1];

            this.aryAccBookDefine = this.sAccBookDefine.split(";");
            this.aryAccBookLink = this.sAccBookLink.split("\f");
            this.aryBookDefineName = this.sAccBookDefineName.split("\f");

            //---------判断台帐链接中是否含有组合和应收应付---------//
            for (int i = 0; i < aryAccBookLink.length; i++) {
                if (i >= aryAccBookDefine.length) {
                    break;
                }
                if (aryAccBookDefine[i].equalsIgnoreCase("Port")) {
                    this.bIsPort = true;
                } else if (aryAccBookDefine[i].equalsIgnoreCase("TsfType") ||
                           aryAccBookDefine[i].equalsIgnoreCase("SubTsfType")) {
                    this.bIsRecPay = true;
                }
            }
            //-----------------------------------------------//
            //判断台帐链接中是否有应收应付

            //设置报表类型标志
            if (aryAccBookDefine != null && aryAccBookLink != null) {
                if (aryAccBookDefine.length > aryAccBookLink.length - 1) {
                    this.sReportType = "sum";
                } else {
                    this.sReportType = "detail";
                }
            }
        } catch (Exception e) {
            throw new YssException("解析报表创建条件出错！", e);
        }
    }

    /**
     * 获取结果字符串
     * @throws YssException
     */
    // public String getResultString() throws YssException{
    //   return "";
    // }

    /**
     * 完成哈希表的初始化工作
     * @throws YssException
     */
    public void initHashTable() throws YssException {

    }

}
