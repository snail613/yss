package com.yss.main.operdeal.compliance;

import java.sql.*;

import com.yss.base.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

public class BaseNavValue
    extends BaseAPOperValue {

    private java.util.Date dDate;
    private String portCode;
    private String curyCode;

    public BaseNavValue() {
    }

    /**
     * init
     *
     * @param bean BaseBean
     */
    public void init(Object bean) {
        YssCompValueParam ov = (YssCompValueParam) bean;
        this.dDate = ov.getDDate();
        this.portCode = ov.getPortCode();
        this.curyCode = ov.getCuryCode();
    }

    /**
     * getOperValue
     *
     * @return Object
     */
    public double getOperDoubleValue() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        double dResult = 0;
        try {
            strSql = "select sum(FBaseNetValue) as FBaseNetValue from " +
                pub.yssGetTableName("Tb_Data_NetValue") +
                " where FNAVDate = " +
                " (select max(FNAVDate) from " +
                pub.yssGetTableName("Tb_Data_NetValue") +
                " where FNavDate <= " +
                dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                ") and FPortCode = " + dbl.sqlString(portCode) +
                " and FCheckState = 1 and FType='01' AND FInvMgrCode = ' '"; //2008.07.16 蒋锦 添加投资经理为判断条件
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                dResult = rs.getDouble("FBaseNetValue");
            }
            return dResult;
        } catch (Exception e) {
            throw new YssException("获取净值出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * invokeOperMothed
     *
     * @return Object
     */
    public Object invokeOperMothed() {
        return "";
    }
}
