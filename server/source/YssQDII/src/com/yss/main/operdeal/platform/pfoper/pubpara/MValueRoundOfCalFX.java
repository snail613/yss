package com.yss.main.operdeal.platform.pfoper.pubpara;

import com.yss.util.YssException;
import java.sql.ResultSet;

/**
 *
 * <p>Title: 在计算估值增值汇兑损益时，市值计算过程中间是否保留位数，即本位币市值 = round[数量*价格*基础汇率/组合汇率,2]</p>
 *
 * <p>Description: QDV4汇添富2011年01月10日01_A</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MValueRoundOfCalFX
    extends BasePubParaDeal {
    public MValueRoundOfCalFX() {
    }

    public Object getParaResult() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String sResult = "0,0"; //默认为中间保留位数
        try {
            strSql = "SELECT FCtlValue FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " WHERE FPubParaCode = " + dbl.sqlString("Round2OfCalFX") +
                " AND FParaGroupCode = " + dbl.sqlString("dayfinish") +
                " AND FParaID <> 0" +
                " AND FCtlCode = " + dbl.sqlString("cboYesOrNo") +
                " ORDER BY FParaID DESC";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString("FCtlValue");
            }
        } catch (Exception e) {
            throw new YssException("获取汇兑损益计算过程中市值最终保留两位小数通用参数报错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }
}
