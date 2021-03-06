package com.yss.main.operdeal.voucher.vchout;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.util.*;

/**
 *
 * <p>Title:BaseVchOut </p>
 * <p>Description: 凭证数据导入导出的基类</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class BaseVchOut
    extends BaseBean {
    protected String portCodes = "";
    protected String beginDate = "";
    protected String endDate = "";
    protected String vchTypes = ""; //一串凭证属性代码
    protected String vchTplCodes = ""; //一串凭证模板代码
    protected boolean isInData = false;
    //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    public String logSumCode = "";//日志汇总编号
    public SingleLogOper logOper;//日志实例
    //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    
    public BaseVchOut() {
    }

    public void init(String portCodes, String beginDate, String endDate, String vchTypes, boolean isInData) throws YssException {
        this.portCodes = portCodes;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.vchTypes = vchTypes;
        this.isInData = isInData;
        vchTplCodes = loadTplCodes(vchTypes);
    }

    //通过凭证属性获取凭证模板
    private String loadTplCodes(String vchTypes) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        StringBuffer buf = new StringBuffer();
        try {
            strSql = "select FVchTplCode from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FAttrCode in (" + operSql.sqlCodes(vchTypes) +
                ") and FCheckState = 1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                buf.append(rs.getString("FVchTplCode")).append(",");
            }
            if (buf.length() > 0) {
                buf.setLength(buf.length() - 1);
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * Insert入库，或mdb中。一开始的名字起得不好，只能将就了，以后再改。
     * @throws YssException
     * @return String
     */
    public String doInsert() throws YssException {
        return "";
    }

    public void delete() throws YssException {

    }

    public String getVchTpl() throws YssException {
        String reStr = "";
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select FVchTplCode,FAttrCode from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FAttrCode in (" + operSql.sqlCodes(vchTypes) + ")";
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                reStr += rs.getString("FVchTplCode") + ",";
            }
            if (reStr.length() > 0) {
                reStr = reStr.substring(0, reStr.length() - 1);
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
