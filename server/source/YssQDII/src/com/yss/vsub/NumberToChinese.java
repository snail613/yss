package com.yss.vsub;

import com.yss.util.*;

public class NumberToChinese {
    public NumberToChinese() {
    }

    private final static String[] STR_NUMBER = {
        "零", "壹", "贰", "叁", "肆", "伍",
        "陆", "柒", "捌", "玖"};

    /** 定义数组存放位数的大写 */
    private final static String[] STR_MODIFY = {
        "元", "拾", "佰", "仟", "万", "拾",
        "佰", "仟", "亿", "拾", "佰", "仟", "万", "兆", "拾", "佰", "仟", "万"};

    /**
     * 转化整数部分
     *
     * @param tempString
     * @return 返回整数部分
     */
    private static String getInteger(String tempString) {
        /** 用来保存整数部分数字串 */
        String strInteger = null; //
        /** 记录"."所在位置 */
        int intDotPos = tempString.indexOf(".");
        int intSignPos = tempString.indexOf("-");
        if (intDotPos == -1) {
            intDotPos = tempString.length();
            /** 取出整数部分 */
        }
        strInteger = tempString.substring(intSignPos + 1, intDotPos);
        strInteger = new StringBuffer(strInteger).reverse().toString();

        StringBuffer sbResult = new StringBuffer();
        for (int i = 0; i < strInteger.length(); i++) {
            sbResult.append(STR_MODIFY[i]);
            sbResult.append(STR_NUMBER[strInteger.charAt(i) - 48]);
        }

        sbResult = sbResult.reverse();

        replace(sbResult, "零拾", "零");
        replace(sbResult, "零佰", "零");
        replace(sbResult, "零仟", "零");
        replace(sbResult, "零万", "万");
        replace(sbResult, "零亿", "亿");
        replace(sbResult, "零元", "元");

        replace(sbResult, "零零", "零");
        replace(sbResult, "零零零", "零");
        /** 这两句不能颠倒顺序 */
        replace(sbResult, "零零零零万", "");
        replace(sbResult, "零零零零", "");
        /** 这样读起来更习惯. */
        replace(sbResult, "壹拾兆", "拾兆");
        replace(sbResult, "壹拾亿", "拾亿");
        replace(sbResult, "壹拾万", "拾万");
        /** 删除多余的单位*/
        replace(sbResult, "兆亿", "兆");
        replace(sbResult, "兆亿万", "兆");
        replace(sbResult, "亿万", "亿");
        replace(sbResult, "万仟佰", "万");
        replace(sbResult, "万仟", "万");
        replace(sbResult, "角分", "角");
        /** 以"元"开头的*/
        if (sbResult.toString().startsWith("元")) {
            sbResult.delete(0, sbResult.length());
            sbResult.append("零元");
        }
        /** 删除个位上的零 */
        if (sbResult.charAt(sbResult.length() - 1) == '零'
            && sbResult.length() != 1) {
            sbResult.deleteCharAt(sbResult.length() - 1);

        }
        if (strInteger.length() == 2) {
            replace(sbResult, "壹拾", "拾");
        }
        /** 将结果反转回来. */
        return sbResult.toString();
    }

    /**
     * 转化小数部分 例：输入22.34返回叁肆
     *
     * @param tempString
     * @return
     */
    private static String getFraction(String tempString) {
        String strFraction = null;
        int intDotPos = tempString.indexOf(".");
        /** 没有点说明没有小数，直接返回 */
        if (intDotPos == -1) {
            return "";
        }
        strFraction = tempString.substring(intDotPos + 1);
        if (strFraction.equals("0") || strFraction.startsWith("00")) {
            return "";
        }
        if (strFraction.length() > 2) {
            strFraction = YssFun.roundIt(Double.parseDouble(strFraction) / 1000, 2) * 1000 + "";
        }
        StringBuffer sbResult = new StringBuffer();
        for (int i = 0; i < strFraction.length(); i++) {
            sbResult.append(STR_NUMBER[strFraction.charAt(i) - 48]);
            if (i == 0) {
                sbResult.append("角");
            }
            if (i == 1) {
                sbResult.append("分");
                break;
            }
        }
        replace(sbResult, "零分", "");
        return sbResult.toString();
    }

    /**
     * 判断传入的字符串中是否有.如果有则返回点
     *
     * @param tempString
     * @return
     */
    /*private static String getDot(String tempString) {
       return tempString.indexOf(".") != -1 ? "点" : "";
           }*/

    /**
     * 判断传入的字符串中是否有-如果有则返回负
     *
     * @param tempString
     * @return
     */
    private static String getSign(String tempString) {
        return tempString.indexOf("-") != -1 ? "负" : "";
    }

    /**
     * 将一个数字转化为金额
     *
     * @param tempNumber  传入一个 全数字的字符串
     * @param bZheng  是否有整字结尾 （如果不是到分，那么一定有整）
     * @return 返一个转换好的字符串
     */
    public static String numberToChinese(String tempNumber, boolean bZheng) {
        try {
            tempNumber = tempNumber.replaceAll(",", ""); //如果有","应先去掉
            tempNumber = YssFun.formatNumber(Double.parseDouble(tempNumber), "#.######");
            if (YssFun.isNumeric(tempNumber)) {
                StringBuffer sbResult = new StringBuffer(getSign(tempNumber)
                    + getInteger(tempNumber) +
                    //  getDot(pTemp) +
                    getFraction(tempNumber));
                if (bZheng && sbResult.indexOf("分") == -1) {
                    sbResult.append("整");
                }
                return sbResult.toString();
            }
            return "非法数据";
        } catch (Exception e) {
            return "非法数据";
        }
    }

    /**
     * 替代字符
     *
     * @param pValue
     * @param pSource
     * @param pDest
     */
    private static void replace(StringBuffer pValue, String pSource,
                                String pDest) {
        if (pValue == null || pSource == null || pDest == null) {
            return;
        }
        /** 记录pSource在pValue中的位置 */
        int intPos = 0;
        do {
            intPos = pValue.toString().indexOf(pSource);
            /** 没有找到pSource */
            if (intPos == -1) {
                break;
            }
            pValue.delete(intPos, intPos + pSource.length());
            pValue.insert(intPos, pDest);
        } while (true);
    }

}
