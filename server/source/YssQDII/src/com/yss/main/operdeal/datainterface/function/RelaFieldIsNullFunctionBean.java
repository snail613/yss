package com.yss.main.operdeal.datainterface.function;

import java.sql.*;

import com.yss.util.*;

/**
 * 关联表字段值为空公式
 * 解释：查找原表的字段在目标表的字段里关联是否为空
 * 公式名称：RelaFieldIsNull
 * 公式参数1：原表名
 * 公式参数2：原表字段
 * 公式参数3：目标表名
 * 公式参数4：目标表字段
 * ----------杂项参数-----------
 * 公式参数5：是否在前台提示所有结果集
 * 公式参数6：提示后是否继续
 * 公式参数7：对话框提示类型
 * 公式参数8：提示信息
 */
public class RelaFieldIsNullFunctionBean
    extends BaseFunction {
    public RelaFieldIsNullFunctionBean() {
    }

    public String FormulaFunctions() throws YssException {
        String sResult = "";
        try {
            if (PromtSource.getFunctionName().equals("RelaFieldIsNull")) { //根据类型进行公式的判断 xuqiji 20090522  QDV4交银施罗德2009年4月29日01_AB MS00426 MS00426    数据源检查与改为多字段的检查
                message.setSMessageType(PromtSource.getParams()[6].split(",")[0]);
                message.setSContinue( (PromtSource.getParams()[5].split(",")[0]).equals("1") ? "true" : "false");
                sResult = FormulaFunctions8(PromtSource.getParams());
            }
            //add by xuqiji 20090522  QDV4交银施罗德2009年4月29日01_AB MS00426 MS00426    数据源检查与改为多字段的检查 --
            //判断公式类型是否为配置多字段的公式类型
            else if (PromtSource.getFunctionName().equals("RelaFieldIsNullX")) {
                message.setSMessageType(PromtSource.getParams()[5].split(",")[0]);
                message.setSContinue( (PromtSource.getParams()[4].split(",")[0]).equals("1") ? "true" : "false");
                sResult = RelaFieldIsNullX(PromtSource.getParams());
                //------------------------------------end-----------------------------------------------------//
            } else {
                //重载参数的方法的处理
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sResult;
    }

    private String FormulaFunctions8(String[] params) throws YssException {
        StringBuffer bufResult = new StringBuffer();
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select a." + params[1].split("[|]")[0] + " from " + getYssTableName(params[0].split("[|]")[0]) + " a where not exists " +
                " (select 1 from " + getYssTableName(params[2].split("[|]")[0]) + " where a." + params[1].split("[|]")[0] + " = " + params[3].split("[|]")[0] + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                if (params[4].split(",")[0].equalsIgnoreCase("0")) { //只往前台送一条
                    bufResult.append(rs.getString(params[1].split("[|]")[0]));
                    break;
                }
//            bufResult.append(rs.getString(params[1].split("[|]")[0])).append("\t");
                bufResult.append(rs.getString(params[1].split("[|]")[0])).append(","); //将上面的tab键改为，分隔 byleeyu 2009-1-15
            }
            SetResultAsParams8(params, bufResult.toString());
            message.setSResult(params[7]);
        } catch (Exception ex) {
            throw new YssException("公式【RelaFieldIsNull】计算数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return message.buildRowStr(); //返回信息参数到前台
    }

    //处理参数为8个的固定信息提示，将提示中的特定字符转换成具体的值
    private void SetResultAsParams8(String[] params, String sResult) throws YssException {
        String replaceStr = "";
//      if(sResult.endsWith("\t"))
        if (sResult.endsWith(",")) {
            sResult = sResult.substring(0, sResult.length() - 1);
        }
        if (sResult.trim().length() == 0) {
            message.setBShow(false); //不用到前台显示了
        }
        if (params[7].indexOf("[") > 0 && params[7].indexOf("]") > 0) { //这里的提示信息只能处理一组［］的字符串
            replaceStr = params[7].substring(0, params[7].indexOf("]"));
            replaceStr = replaceStr.substring(replaceStr.indexOf("[") + 1);
            if (params[1].split("[|]")[0].equals(replaceStr.trim()) || params[3].split("[|]")[0].equals(replaceStr.trim())) {
                params[7] = params[7].replaceAll(replaceStr, sResult);
            }
        }
    }

    /**
     * 此方法为查询出有差异的字段
     * @param params String[] 参数数组
     * @return String 返回有差异的字段
     * @throws YssException 异常
     * add by xuqiji 20090522 QDV4交银施罗德2009年4月29日01_AB MS00426 MS00426    数据源检查与改为多字段的检查
     */
    private String RelaFieldIsNullX(String[] params) throws YssException {
        StringBuffer bufResult = new StringBuffer();
        StringBuffer buff = new StringBuffer();
        ResultSet rs = null;
        try {
            buff.append("select ");
            for (int i = 6; i < params.length; i++) {
                if (i != params.length - 1) {
                    buff.append("a.").append(params[i].split(",")[0]).append(",");
                } else {
                    buff.append("a.").append(params[i].split(",")[0]);
                }
            }
            buff.append(" from ");
            buff.append(getYssTableName(params[1].split("[|]")[0]));
            buff.append(" a where not exists ( select '1' from ");
            buff.append(getYssTableName(params[2].split("[|]")[0]));
            buff.append(" b where ");
            for (int i = 6; i < params.length; i++) {
                if (i != params.length - 1) {
                    buff.append("a.").append(params[i].split(",")[0]).append(" = ");
                    buff.append(" b.").append(params[i].split(",")[2]).append(" and ");
                } else {
                    buff.append("a.").append(params[i].split(",")[0]).append(" = ");
                    buff.append(" b.").append(params[i].split(",")[2]);
                }
            }
            buff.append(" ) ");
            rs = dbl.openResultSet(buff.toString());
            while (rs.next()) {
                if (params[3].split(",")[0].equalsIgnoreCase("0")) { //只往前台送一条
                    for (int i = 6; i < params.length; i++) {
                        if (i != params.length - 1) {
                            bufResult.append(rs.getString(params[i].split(",")[0])).
                                append("-");
                        } else {
                            bufResult.append(rs.getString(params[i].split(",")[0])).
                                append(",");
                        }
                    }
                    break;
                }
                for (int i = 6; i < params.length; i++) {
                    if (i != params.length - 1) {
                        bufResult.append(rs.getString(params[i].split(",")[0])).append("-");
                    } else {
                        bufResult.append(rs.getString(params[i].split(",")[0])).append(",");
                    }
                }
            }
            SetRelaFieldIsNullX(params, bufResult.toString());
            message.setSResult(params[0]);
        } catch (Exception ex) {
            throw new YssException("公式【RelaFieldIsNullX】计算数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return message.buildRowStr(); //返回信息参数到前台
    }

    /**
     * 处理公式为RelaFieldIsNullX的固定信息提示，将提示中的特定字符转换成具体的值
     * @param params String[] 参数数组
     * @param sResult String 查询出有差异的字段的结果
     * @throws YssException 异常
     * add by xuqiji 20090522 QDV4交银施罗德2009年4月29日01_AB MS00426 MS00426    数据源检查与改为多字段的检查
     */
    private void SetRelaFieldIsNullX(String[] params, String sResult) throws YssException {
        String replaceStr = "";
        String paramStr = "";
        try {
            if (sResult.length() == 0) {
                message.setBShow(false); //不用到前台显示了
            }
            if (sResult.endsWith(",")) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            replaceStr = params[0].substring(0, params[0].indexOf("]"));
            replaceStr = replaceStr.substring(replaceStr.indexOf("[") + 1);
            params[0] = params[0].replaceAll(replaceStr, sResult);
        } catch (Exception e) {
            throw new YssException("处理公式为RelaFieldIsNullX的固定信息提示出错！", e);
        }
    }
}
