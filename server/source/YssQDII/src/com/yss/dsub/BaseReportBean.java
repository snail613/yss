package com.yss.dsub;

import java.lang.reflect.*;
import com.yss.util.*;
import com.yss.pojo.sys.YssCancel;
import com.yss.base.BaseCalcFormula;
import java.util.ArrayList;
import java.sql.ResultSet;

//public class BaseReportBean extends BaseBean {
public class BaseReportBean
    extends BaseCalcFormula { //将继承类改为BaseCalcFormula 为了处理内部函数的解析 by liyu 080601

    private String reportHeaders1;
    private String reportHeaders2;
    private String reportHeaders3;
    private String reportFields1;
    private String reportFields2;
    private String reportFields3;
    
    //add by huangqirong 2012-03-01 story #2088 保留位数
    private int holdDigit = 0 ;	
    
    
    public int getHoldDigit() {
		return holdDigit;
	}

	public void setHoldDigit(int holdDigit) {
		this.holdDigit = holdDigit;
	}
	
	private String reportFields4;
		
	public String getReportFields4() {
		return reportFields4;
	}

	public void setReportFields4(String reportFields4) {
		this.reportFields4 = reportFields4;
	}
	//---end---
	

	public String getReportHeaders3() {
        return reportHeaders3;
    }

    public String getReportHeaders2() {
        return reportHeaders2;
    }

    public String getReportFields1() {
        return reportFields1;
    }

    public String getReportHeaders1() {
        return reportHeaders1;
    }

    public String getReportFields2() {
        return reportFields2;
    }

    public String getReportFields3() {
        return reportFields3;
    }

    public void setReportHeaders1(String reportHeaders1) {
        this.reportHeaders1 = reportHeaders1;
    }

    public void setReportHeaders3(String reportHeaders3) {
        this.reportHeaders3 = reportHeaders3;
    }

    public void setReportHeaders2(String reportHeaders2) {
        this.reportHeaders2 = reportHeaders2;
    }

    public void setReportFields1(String reportFields1) {
        this.reportFields1 = reportFields1;
    }

    public void setReportFields2(String reportFields2) {
        this.reportFields2 = reportFields2;
    }

    public void setReportFields3(String reportFields3) {
        this.reportFields3 = reportFields3;
    }

    public BaseReportBean() {
    }

    //配置BGrid显示列时对特殊列的操作
    public void beforeBuildRowShowStr(YssCancel bCancel, String sValName, Method method,
                                      StringBuffer buf) throws
        Exception {

    }

    //配置BGrid显示列时取出列数据后后续操作判断
    public boolean isJudge(String sColName) {
        return true;
    }

    //配置BGrid显示列时取出列数据后后续操作
    public void buildRowOtherShowStr(String sColName, Method method,
                                     StringBuffer buf) throws
        Exception {
    }

    public void autoParseRowStr(String sReqStr) throws YssException {
        Method method = null;
        Class[] paramClsAry = new Class[1];
        Object[] valueAry = new Object[1];
        String[] sFieldAry = null;
        String[] sReqAry = null;
        //2008.04.18 添加 蒋锦
        Class[] cls = new Class[0];
        try {
//         valueAry[0] = "";
            sFieldAry = reportFields1.split("\t");
            sReqAry = sReqStr.split("\t");
            Class ownerClass = this.getClass();
            for (int i = 0; i < sFieldAry.length; i++) {
                method = ownerClass.getMethod("get" + sFieldAry[i], cls);
                paramClsAry[0] = method.getReturnType();
                if (paramClsAry[0].getName().indexOf("Integer") >= 0) {
                    valueAry[0] = new Integer(sReqAry[i]);
                } else if (paramClsAry[0].getName().indexOf("Date") >= 0) {
                    valueAry[0] = YssFun.toDate(sReqAry[i]);
                } else if (paramClsAry[0].getName().indexOf("double") >= 0) {
                    valueAry[0] = new Double(sReqAry[i]);
                } else {
                    valueAry[0] = new String(sReqAry[i]);
                }
                method = ownerClass.getMethod("set" + sFieldAry[i], paramClsAry);
                method.invoke(this, valueAry);
            }
        } catch (Exception e) {
            throw new YssException("自动解析错误");
        }
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
        YssCancel before = new YssCancel();
        //-----------------2008.04.18 添加 蒋锦-------------------//
        Object[] obj = new Object[0];
        Class[] cls = new Class[0];
        //-------------------------------------------------------//

        try {
            ownerClass = this.getClass();
            sFieldAry = sShowFields.split("\t");
            for (int i = 0; i < sFieldAry.length; i++) {
                before.setCancel(false);
                sFieldFormat = "";
                if (sFieldAry[i].indexOf(";") > 0) {
                    sFieldName = sFieldAry[i].split(";")[0];
                    sFieldFormat = sFieldAry[i].split(";")[1];
                } else {
                    sFieldName = sFieldAry[i];
                }
                method = ownerClass.getMethod("get" + sFieldName, cls);
                reObj = method.invoke(this, obj);
                beforeBuildRowShowStr(before, sFieldName, method, buf);
                if (!before.isCancel()) {
                    reClsAry[0] = method.getReturnType();
                    if (reClsAry[0].getName().indexOf("Date") >= 0) {
                        reObj = YssFun.formatDate( (java.util.Date) reObj);
                    } else if (reClsAry[0].getName().indexOf("double") >= 0) {
                    	//add by huangqirong 2012-03-01 story #2088
                    	if (sFieldFormat.length() > 0 && sFieldFormat.trim().equalsIgnoreCase("CUSHOLDDIGIT")) {
                            reObj = this.getCusHoldDigit(reObj.toString(), this.holdDigit , "0" , false);
                        }else
                    	//---end---
                        if (sFieldFormat.length() > 0) {
                            reObj = YssFun.formatNumber(Double.parseDouble(reObj.toString()), sFieldFormat);
                        }
                    }
                    buf.append(reObj).append("\t");
                    if (this.isJudge(sFieldName)) {
                        buildRowOtherShowStr(sFieldName, method, buf);
                    }
                }
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
    
    /*
     * add by huangqirong 2012-03-01 story #2088  设置保留位数
     * 
     * fixChar :前后缀字符
     * preOrPostFix 是否前缀  : true 前缀  ，false 后缀 
     * 
     * */
    private String getCusHoldDigit(String value , int holdDigit , String fixChar , boolean preOrPostFix){
    	int index = value.indexOf(".");
    	if( index > -1 ){
    		int count = holdDigit - (value.length()- 1 - index) ;
    		String postfixs ="";
    		
    		for (int i = 0; i < count; i++) {
    			postfixs += fixChar ;
			}
    		
    		if(postfixs.length() > 0)
    			value = preOrPostFix ? postfixs + value  : value + postfixs;
    	}
    	return value;
    }

    /**
     * 入口,以解析自定义函数,及正则表达式的
     * @param sExPress String
     * @return String
     * @throws YssException
     */
    public String pretExpress(String sExPress) throws YssException {
        String sResult = "";
        this.formula = sExPress;
        this.sign = "#"; //还是根据 [] 起来的部分来解析
        System.out.println("--------------------------");
        sResult = replaceFormulaStr();
        sResult = sResult.replaceAll("#", " ");
        return sResult;
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = sKeyword;
        return objResult;
    }

    public Object getExpressValueEx(String sExpress, ArrayList alParams, String sEndStr) throws
        YssException {
        String sResult = "";
        try {
            if (sExpress.toLowerCase().endsWith("yssin")) {
                sResult = YssInFunction(sExpress, alParams);
                sResult += sEndStr;
            } else {
                //添加 else 方法 ,如果没有函数的话就直接返回一个定值 by leeyu 080604
                for (int i = 0; i < alParams.size(); i++) { //这里 alParams按顺序取值
                    sResult += ("[" + alParams.get(i) + "]");
                }
                sResult = sExpress + sResult;
                sResult = sResult + sEndStr;
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sResult;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws YssException {
        return getExpressValueEx(sExpress, alParams, "");
    }

    /**
     * 解析自定义函数 YssIn[] 方法
     * 说明: YssIn的"[]"起来的部分必须是可以返回一个串如:res1,res2,res3  ...形式的式子
     * @param sExpress String
     * @param alParams ArrayList
     * @return String
     * @throws YssException
     */
    private String YssInFunction(String sExpress, ArrayList alParams) throws YssException {
        String sResult = "";
        ResultSet rs = null;
        String sLastStr = "";
        try {
            for (int i = 0; i < alParams.size(); i++) {
                if (alParams.get(i).toString().lastIndexOf("]") > 0) {
                    sLastStr = alParams.get(i).toString().substring(alParams.get(i).
                        toString().lastIndexOf("]") + 1,
                        alParams.get(i).toString().length());
                    rs = dbl.openResultSet(alParams.get(i).toString().substring(0,
                        alParams.get(i).toString().lastIndexOf("]")));
                } else {
                    rs = dbl.openResultSet(alParams.get(i).toString());
                } while (rs.next()) {
                    sResult += rs.getString(1);
                    sResult += ",";
                }
            }
            sExpress = sExpress.replaceAll("YssIn", "in");
            sResult = sExpress + "(" + operSql.sqlCodes(sResult) + ")" + sLastStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
}
