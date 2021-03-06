package com.yss.pojo.message;

import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * 处理与前台信息交互的BEAN
 */
public class PrepMessageBean
    implements IYssConvert {
    public PrepMessageBean() {
    }

    private String sMessageType = ""; //对话框提示类型，分为一般提示提示、警告、错误
    private String sContinue = "false"; //提示后是否继续，默认为不继续
    private String sResult = ""; //提示结果
    //add by songjie 2012.03.14 BUG 4033 QDV4赢时胜(测试)2012年3月14日01_B 将前台的按钮默认设置为“是”与“否”按钮
    private String sButtons = "YesNo"; //按钮的类型，取值 Yes:是、YesNo:是与否、OK：确定、RetryCancel:重试取消、OKCancel:确定取消
    private boolean bShow = true; //是否要在前台提示，默认为到前台提示

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        sButtons = "YesNo"; //将前台的按钮默认设置为“是”与“否”按钮 byleeyu 2009-1-16
        StringBuffer buf = new StringBuffer();
        buf.append(bShow ? "true" : "false").append("\f\f\b");
        buf.append(sMessageType).append("\f\f\b");
        buf.append(sButtons).append("\f\f\b");
        buf.append(sContinue).append("\f\f\b");
        buf.append(sResult).append("\f\f\bnull");
        return buf.toString();
    }
    //story 1536 add by zhouwei 20111013 设置提示类型的接口预处理的提示控件
    public String buildRowStrToPretreat() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(bShow ? "true" : "false").append("\f\f\b");
        buf.append(sMessageType).append("\f\f\b");
        buf.append(sButtons).append("\f\f\b");
        buf.append(sContinue).append("\f\f\b");
        buf.append(sResult).append("\f\f\bnull");
        return buf.toString();
    }
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String getSResult() {
        return sResult;
    }

    public String getSMessageType() {
        return sMessageType;
    }

    public void setSContinue(String sContinue) {
        this.sContinue = sContinue;
    }

    public void setSResult(String sResult) {
        this.sResult = sResult;
    }

    public void setSMessageType(String sMessageType) {
        this.sMessageType = sMessageType;
    }

    public void setSButtons(String sButtons) {
        this.sButtons = sButtons;
    }

    public void setBShow(boolean bShow) {
        this.bShow = bShow;
    }

    public String getSContinue() {
        return sContinue;
    }

    public String getSButtons() {
        return sButtons;
    }

    public boolean isBShow() {
        return bShow;
    }
}
