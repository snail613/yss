package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.util.YssException;
import java.sql.*;

/**
 * <p>Title: 取单位净值的小数位数 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ParaWithDigit
    extends BasePubParaDeal {
    private String sPortCode = "";
    public ParaWithDigit() {
    }

    /**
     * 取单位净值的保留位数
     * @return Object
     * @throws YssException
     */
    public Object getParaResult() throws YssException {
        String sqlStr = "";
        String sResult = "3"; //默认保留三位小数
        ResultSet rs = null;
        try {
            sqlStr = "select * from " + pub.yssGetTableName("Tb_PFoper_PubPara") +
                " where FPubParaCode=" + dbl.sqlString(pubParaCode) +
                " and FParaGroupCode=" + dbl.sqlString(paraGroupCode) +
                " and FParaID=(select FParaID from " + pub.yssGetTableName("tb_pfoper_pubpara") +
                " where FPubParaCode=" + dbl.sqlString(pubParaCode) +
                " and FParaGroupCode=" + dbl.sqlString(paraGroupCode) + " and FCtlValue like '" + sPortCode + "|%')" +
                " and FCtlCode='txtdigit'";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sResult = rs.getString("FCtlValue");
            }
        } catch (Exception ex) {
            throw new YssException("获取数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public String getSPortCode() {
        return sPortCode;
    }
}
