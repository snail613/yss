package com.yss.main.operdeal.datainterface.function;

import java.sql.*;

import com.yss.util.*;

public class FieldIsNullFunction
    extends BaseFunction {
    /**
     * 查检表字段是否为空
     * 解释：判断表的字段值是否为空或值长度是否为0
     * 公式名称：FieldIsNull
     * 公式参数1：表名称
     * 公式参数2：表字段
     * ----------杂项参数-----------
     * 公式参数3：提示后是否继续
     * 公式参数4：对话框提示类型
     * 公式参数5：提示信息
     */
    public FieldIsNullFunction() {
    }

    public String FormulaFunctions() throws YssException {
        String sResult = "";
        try {
            if (PromtSource.getParams().length == 5) {
                message.setSMessageType(PromtSource.getParams()[3].split(",")[0]);
                message.setSContinue( (PromtSource.getParams()[2].split(",")[0]).equals("1") ? "true" : "false");
                message.setSResult(PromtSource.getParams()[4]);
                sResult = FormulaFunctions5(PromtSource.getParams());
            } else {
                //重载参数的方法的处理
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sResult;
    }

    private String FormulaFunctions5(String[] arrParams) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "select '1' from " + getYssTableName(arrParams[0].split("[|]")[0]) + " where " + arrParams[1].split("[|]")[0] +
//               " is null or "+dbl.sqlLen(arrParams[1].split("[|]")[0])+"=0";
                " is null or " + dbl.sqlLen(arrParams[1].split("[|]")[0]) + "=0 or " + arrParams[1].split("[|]")[0] + "=' '"; //添加等于空格的情况，因为临时表有空格的情况
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) { //实际查出结果来的值
//            message.setBShow(false); //当参数设置为显示无结果集提示，此时不向前台提示
                message.setBShow(true); //向前台抛出提示
            } else {
                message.setBShow(false);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return message.buildRowStr();
    }

}
