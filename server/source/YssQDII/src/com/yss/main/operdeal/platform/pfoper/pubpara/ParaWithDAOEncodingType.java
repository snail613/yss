package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 * <p>Title: 接口处理中导出数据时的默认编码类型 </p>
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
public class ParaWithDAOEncodingType
    extends BasePubParaDeal {
    public ParaWithDAOEncodingType() {
    }

    public Object getParaResult() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String strResult = "";
        try {
            strSql = "SELECT FCtlValue FROM " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " WHERE FPubParaCode = " + dbl.sqlString("DAO_EncodingType") +
                " AND FParaGroupCode = " + dbl.sqlString("DataInterface") +
                " AND FParaID <> 0" +
                " AND FCtlCode = " + dbl.sqlString("selEncodingType");
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                strResult = rs.getString("FCtlValue");
            }
        } catch (Exception e) {
            throw new YssException("获取默认编码类型报错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return strResult;
    }

}
