package com.yss.main.operdeal.businesswork;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class BaseBusinWork
    extends BaseBean implements IClientOperRequest {
    //处理日期
    protected java.util.Date workDate;
    //以 SQL IN 形式存在的组合代码
    protected String portCodes;
    //单个的组合代码
    protected String portCode;

    public Date getWorkDate() {
        return workDate;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public String getPortCode() {
        return portCode;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public BaseBusinWork() {
    }

    public void parseRowStr(String sRowStr) throws YssException {

    }

    public String buildRowStr() {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public String checkRequest(String sType) throws YssException {
        return "";
    }

    public String doOperation(String sType) throws YssException {
        return "";
    }

    public void insertValMktPrice(ArrayList alValMktPrice) throws YssException {
        Connection conn = dbl.loadConnection();
        String sqlDel = "";
        String sqlInsert = "";
        PreparedStatement pstDel = null;
        PreparedStatement pstInsert = null;
        ValMktPriceBean mktPrice = null;
        boolean bTrans = false;
        try {
            sqlDel = "delete from " +
                pub.yssGetTableName("Tb_Data_ValMktPrice") +
                " where FValDate = ? and " +
                " FPortCode = ? and " +
                " FSecurityCode = ?";
            sqlInsert = "insert into " + pub.yssGetTableName("Tb_Data_ValMktPrice") +
                " (FValDate,FPortCode,FSecurityCode,FPrice,FOTPrice1,FOTPrice2,FOTPrice3,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FMarketStatus) " + //新增 行情状态，by leeyu 2008-10-17
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstDel = conn.prepareStatement(sqlDel);
            pstInsert = conn.prepareStatement(sqlInsert);

            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < alValMktPrice.size(); i++) {
                mktPrice = (ValMktPriceBean) alValMktPrice.get(i);
                pstDel.setDate(1, YssFun.toSqlDate(mktPrice.getValDate()));
                pstDel.setString(2, mktPrice.getPortCode());
                pstDel.setString(3, mktPrice.getSecurityCode());
                pstDel.executeUpdate();

                pstInsert.setDate(1, YssFun.toSqlDate(mktPrice.getValDate()));
                pstInsert.setString(2, mktPrice.getPortCode());
                pstInsert.setString(3, mktPrice.getSecurityCode());
                pstInsert.setDouble(4, mktPrice.getPrice());
                pstInsert.setDouble(5, mktPrice.getOtPrice1());
                pstInsert.setDouble(6, mktPrice.getOtPrice2());
                pstInsert.setDouble(7, mktPrice.getOtPrice3());
                pstInsert.setInt(8, 1);
                pstInsert.setString(9, pub.getUserCode());
                pstInsert.setString(10, YssFun.formatDatetime(new java.util.Date()));
                pstInsert.setString(11, pub.getUserCode());
                pstInsert.setString(12, YssFun.formatDatetime(new java.util.Date()));
                pstInsert.setString(13, mktPrice.getMarketStatus()); //新增 行情状态，by leeyu 2008-10-17
                pstInsert.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新估值行情表出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeStatementFinal(pstDel);
            dbl.closeStatementFinal(pstInsert);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * getValuationMethods
     *
     * @return ArrayList
     */
    public ArrayList getValuationMethods() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String exRateSrcCode = "";
        String exRateCode = "";
        MTVMethodBean vMethod = null;
        ArrayList alResult = new ArrayList();
        try {
            //获取估值方法信息
            strSql = " select a.*, b.* from " +
                "(select m.* from " + pub.yssGetTableName("Tb_Para_MTVMethod") +
                " m join (select FMTVCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_MTVMethod") +
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1 group by FMTVCode) n on m.FMTVCode = n.FMTVCode and m.FStartDate = n.FStartDate " +
                ") a join (select FSubCode, FPortCode, FRelaGrade from " +
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " where FRelaType = 'MTV' and FPortCode IN (" +
                portCodes +
                ") and FCheckState = 1) b on a.FMTVCode = b.FSubCode where a.FCheckState = 1 order by b.FRelaGrade desc";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (rs.next()) {
                rs.beforeFirst();
                while (rs.next()) {
                    vMethod = new MTVMethodBean();
                    vMethod.setMTVCode(rs.getString("FMTVCode") + "");
                    vMethod.setMktSrcCode(rs.getString("FMktSrcCode") + "");
                    vMethod.setMktPriceCode(rs.getString("FMktPriceCode") + "");
                    vMethod.setMTVMethod(rs.getString("FMTVMethod") + "");
                    vMethod.setBaseRateSrcCode(rs.getString("FBaseRateSrcCode") + "");
                    vMethod.setBaseRateCode(rs.getString("FBaseRateCode") + "");
                    vMethod.setPortRateSrcCode(rs.getString("FPortRateSrcCode") + "");
                    vMethod.setPortRateCode(rs.getString("FPortRateCode") + "");

                    alResult.add(vMethod);
                }
            }
            return alResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
