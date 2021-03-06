package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 *
 * <p>Title: 在计算估值增值做 价格 * 数量 的时候是否四舍五入</p>
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
public class ParaWithMVRound
    extends BasePubParaDeal {
    public ParaWithMVRound() {
    }

    public Object getParaResult() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "1,1"; //默认为四舍五入
        try {
            strSql = "SELECT FCtlValue FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " WHERE FPubParaCode = " + dbl.sqlString("finish_MVIsRound2") +
                " AND FParaGroupCode = " + dbl.sqlString("dayfinish") +
                " AND FParaID <> 0" +
                " AND FCtlCode = " + dbl.sqlString("cboYesOrNo") +
                " ORDER BY FParaID DESC";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString("FCtlValue");
            }
        } catch (Exception e) {
            throw new YssException("获取估值增值是否四舍五入报错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
}
