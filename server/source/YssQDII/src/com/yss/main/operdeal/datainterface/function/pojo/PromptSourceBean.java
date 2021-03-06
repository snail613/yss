package com.yss.main.operdeal.datainterface.function.pojo;

import java.util.*;

import com.yss.dsub.*;

/**
 * 解析提示的数据源的类
 * <p>Title: </p>
 *
 */

public class PromptSourceBean
    extends BaseBean {
    public PromptSourceBean() {
    }

    private String FunctionType = ""; //公式类型
    private String FunctionName = ""; //公式名称
    private String[] Params = null; //公式参数列表

    //解析数据源的方法
    public void parseSource(String sSource) {
        if (sSource.length() == 0) {
            return;
        }
        //采用截断的方式取字符串的值，以避免参数中有\r\n转义符
        FunctionType = sSource.substring(sSource.indexOf("[FunctionType]") + 14, sSource.indexOf("[FunctionName]"));
        if (sSource.indexOf("[Params1]") > 0) {
            FunctionName = sSource.substring(sSource.indexOf("[FunctionName]") + 14, sSource.indexOf("[Params1]"));
        } else {
            FunctionName = sSource.substring(sSource.indexOf("[FunctionName]") + 14);
        }
        if (FunctionType.length() > 2 && FunctionType.endsWith("\r\n")) {
            FunctionType = FunctionType.replaceAll("\r\n", "");
        }
        if (FunctionName.length() > 2 && FunctionName.endsWith("\r\n")) {
            FunctionName = FunctionName.replaceAll("\r\n", "");
        }
        FunctionName = FunctionName.split("[|]")[0];
        parseParams(sSource);
    }

    //解析公式参数的方法
    public void parseParams(String sSource) {
        ArrayList alParams = new ArrayList();
        for (int i = 1; i <= 50; i++) {
            if (sSource.indexOf("[Params" + i + "]") > 0) {
                if (sSource.indexOf("[Params" + (i + 1) + "]") > 0) {
                    alParams.add(sSource.substring(sSource.indexOf("[Params" + i + "]") + ("[Params" + i + "]").length(), sSource.indexOf("[Params" + (i + 1) + "]")));
                } else {
                    alParams.add(sSource.substring(sSource.indexOf("[Params" + i + "]") + ("[Params" + i + "]").length()));
                }
            }
        }
        //将取的公式按顺序重新放回到字符串数组中
        Params = new String[alParams.size()];
        for (int i = 0; i < alParams.size(); i++) {
            Params[i] = ( (String) alParams.get(i));
            if (Params[i].endsWith("\r\n")) {
                Params[i] = Params[i].substring(0, Params[i].length() - 2);
            }
        }
    }

    public void setParams(String[] Params) {
        this.Params = Params;
    }

    public String[] getParams() {
        return Params;
    }

    public void setFunctionType(String FunctionType) {
        this.FunctionType = FunctionType;
    }

    public String getFunctionType() {
        return FunctionType;
    }

    public void setFunctionName(String FunctionName) {
        this.FunctionName = FunctionName;
    }

    public String getFunctionName() {
        return FunctionName;
    }
}
