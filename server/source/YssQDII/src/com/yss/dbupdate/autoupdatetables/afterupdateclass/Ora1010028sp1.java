package com.yss.dbupdate.autoupdatetables.afterupdateclass;

import java.sql.*;
import java.util.*;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.util.*;

public class Ora1010028sp1 extends BaseDbUpdate {
    public Ora1010028sp1() {
    }
    
    public void doUpdate(HashMap hmInfo) throws YssException {
        try {
            //更新运营收支品种设置表和投资运营收支设置表，MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A panjunfang add 20090815
            updateInvestPayCat(hmInfo);
        } catch (Exception ex) {
            throw new YssException("版本 1.0.1.0028sp1 更新出错！", ex);
        }
    }
    
    /**
     * 20090815 panjunfang add,MS00017  国内预提待摊  QDV4.1赢时胜（上海）2009年4月20日17_A
     * updateInvestPayCat
     * 更新运营收支品种设置表字段FIVType（运营品种类型），资产类型为资产的对应的运营品种类型更新为待摊，资产类型为负债的运营品种类型更新为预提，管理费和托管费更新为两费
     * 更新投资运营收支设置表中原有数据终止日期等于结束日期，计提方式为按每日资产净值。
     * @param hmInfo HashMap
     */
    public void updateInvestPayCat(HashMap hmInfo) throws YssException {
        StringBuffer sqlInfo = null;
        String strSql = "";
        String strPsql = "";
        String strPpSql = "";
        String strTmpPortCode = "";
        String strTmpIVPayCatCode = "";
        boolean bHaveData = false;
        java.util.Date tmpDate = null;
        ArrayList aList = new ArrayList();
        ResultSet rs = null;
        java.sql.PreparedStatement psmt = null;
        java.sql.PreparedStatement ppsmt = null;
        boolean updated = false;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        sqlInfo = (StringBuffer) hmInfo.get("sqlinfo"); //用于获取执行的sql语句
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            // modified by yeshenghong 20120221 BUG 3887  不存在才更新
            strSql = "select fivpaycatcode from TB_BASE_INVESTPAYCAT where fivtype in ('deferredFee', 'accruedFee','managetrusteeFee')";
            rs = dbl.openResultSet(strSql);
            if(rs.next())
            {
            	updated = true;
            }
            
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeResultSetFinal(rs);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            
            if(!updated) 
            {
            	//更新资产类型为资产(FPayType=0)的对应的运营品种类型为待摊
                //更新资产类型为负债(FPayType=1)的运营品种类型为预提
	            strSql = "UPDATE " + pub.yssGetTableName("TB_BASE_INVESTPAYCAT") +
	                " SET FIVType = CASE When FPayType = 0 then 'deferredFee'" +
	                " when FPayType = 1 then 'accruedFee' end";
	            sqlInfo.append(strSql).append("\n");
	            dbl.executeSql(strSql);
            
            //更新管理费和托管费为两费,运营品种类型为('IV001', 'IV002', 'IV014', 'IV015', 'IV016', 'IV017', 'IV025', 'IV026')
	            strSql = "UPDATE " + pub.yssGetTableName("TB_BASE_INVESTPAYCAT") +
	                " SET FIVType = 'managetrusteeFee' Where FIVPayCatCode IN ('IV001', 'IV002', 'IV014', 'IV015', 'IV016', 'IV017', 'IV025', 'IV026')";
	            sqlInfo.append(strSql).append("\n");
	            dbl.executeSql(strSql);
            }
            //更新投资运营收支设置表中:将新增字段终止日期等于结束日期;新增字段计提方式设为按每日资产净值。
            strSql = "UPDATE " + pub.yssGetTableNameForUpdTables("Tb_Para_Investpay") +
                " SET FEXPIRDATE=FACENDDATE,FAccrueType='EveDayNAV'";
            sqlInfo.append(strSql).append("\n");
            dbl.executeSql(strSql);

            //更新投资运营收支设置表，如果运营品种类型为预提待摊则将收支来源、计提方式、固定比率设置为null
            strSql = "UPDATE " + pub.yssGetTableNameForUpdTables("Tb_Para_Investpay") +
                " SET FPayOrigin = NULL,FFixRate = NULL,FAccrueType = NULL WHERE FIVPAYCATCODE NOT IN ('IV001', 'IV002', 'IV014', 'IV015', 'IV016', 'IV017', 'IV025', 'IV026')";
            sqlInfo.append(strSql).append("\n");
            dbl.executeSql(strSql);

            //更新投资运营收支设置表，如果运营品种类型为两费则将开始日期、结束日期、终止日期和总金额设置为null
            strSql = "UPDATE " + pub.yssGetTableNameForUpdTables("Tb_Para_Investpay") +
                " SET FACBeginDate = NULL,FACEndDate = NULL,FExpirDate = NULL,FACTotalMoney = NULL WHERE FIVPAYCATCODE IN ('IV001', 'IV002', 'IV014', 'IV015', 'IV016', 'IV017', 'IV025', 'IV026')";
            sqlInfo.append(strSql).append("\n");
            dbl.executeSql(strSql);

            //更新运营应收应付收支库存表：将运营品种类型为待摊的运营收支品种的应收款项余额更新为当日的库存余额，“应付”更新为“支出”，“应付运营收支款”更新为“待摊费用”
            //1、获取所有运营品种类型为待摊的运营收支品种代码
            strSql = "SELECT FIVPayCatCode FROM " + pub.yssGetTableName("TB_BASE_INVESTPAYCAT") +
                " WHERE FPayType = 0";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                aList.add(rs.getString("FIVPayCatCode"));
            }
            
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeResultSetFinal(rs);
            //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            
            //2、更新运营应收应付收支库存表的sql
            //应收款项余额更新为当日的库存余额的sql
            strPsql = "UPDATE " + pub.yssGetTableName("TB_STOCK_INVESTPAYREC") +
                " SET FBAL=?, FBaseCuryBal=?,FPortCuryBal=? WHERE FIVPayCatCode=?" +
                " AND FPortCode=?" +
                " AND FStorageDate=?" +
                " AND FTsfTypeCode = '" + YssOperCons.YSS_ZJDBLX_Rec + "'" +
                " AND FSubTsfTypeCode = '" + YssOperCons.YSS_ZJDBZLX_IV_Rec + "'";
            psmt = conn.prepareStatement(strPsql);
            //“应付”更新为“支出”，“应付运营收支款”更新为“待摊费用”的sql
            strPpSql = "UPDATE " + pub.yssGetTableName("TB_STOCK_INVESTPAYREC") +
                " SET FTsfTypeCode = '" + YssOperCons.YSS_ZJDBLX_PAYOUT + "'" +
                " , FSubTsfTypeCode = '" + YssOperCons.YSS_ZJDBLX_PRE_PAYOUT + "'" +
                " WHERE FIVPayCatCode = ?" +
                " AND FPortCode = ?" +
                " AND FStorageDate = ?" +
                " AND FTsfTypeCode = '" + YssOperCons.YSS_ZJDBLX_Pay + "'" +
                " AND FSubTsfTypeCode = '" + YssOperCons.YSS_ZJDBZLX_IV_Pay + "'";
            ppsmt = conn.prepareStatement(strPpSql);

            //3、判断待摊费用是否已平摊完成，获取未平摊完毕的运营收支品种代码，如果平摊完毕则不对此进行更新
            for (int i = 0; i < aList.size(); i++) {
                strSql = "SELECT a.*,b.* FROM (" +
                    " SELECT * FROM " + pub.yssGetTableName("TB_STOCK_INVEST") +
                    " WHERE FIVPAYCATCODE = " + dbl.sqlString( (String) aList.get(i)) +
                    " AND FBAL <> 0 AND FSTORAGEDATE = (SELECT MAX(FSTORAGEDATE) FROM " + pub.yssGetTableName("TB_STOCK_INVEST") + " WHERE FIVPAYCATCODE = " + dbl.sqlString( (String) aList.get(i)) + ")" +
                    " ) a LEFT JOIN (SELECT FIVPayCatCode,FPortCode,MAX(FStartDate) as FStartDate from " + pub.yssGetTableName("TB_Para_InvestPay") +
                    " GROUP BY FIVPayCatCode,FPortCode " +
                    " ) b ON a.FIVPayCatCode = b.FIVPayCatCode AND a.FPortCode = b.FPortCode";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bHaveData = true;
                    strTmpIVPayCatCode = rs.getString("FIVPayCatCode");
                    strTmpPortCode = rs.getString("FPortCode");
                    tmpDate = rs.getDate("FStartDate");
                }
                
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                dbl.closeResultSetFinal(rs);
                //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                
                if (bHaveData && tmpDate != null) {//中保库中待摊完毕后库存余额不为0，而对应的投资运营收支品种被删除，故添加判断tmpDate != null，如果对应的投资运营收支不存在就不对此库存做更新，避免更新时报错
                    strSql = "SELECT * FROM " + pub.yssGetTableName("TB_STOCK_INVEST") +
                        " WHERE FIVPAYCATCODE = '" + strTmpIVPayCatCode + "' AND FPortCode = '" + strTmpPortCode + "' AND FSTORAGEDATE >= " + dbl.sqlDate(tmpDate);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        psmt.setDouble(1, rs.getDouble("FBal"));
                        psmt.setDouble(2, rs.getDouble("FBaseCuryBal"));
                        psmt.setDouble(3, rs.getDouble("FPortCuryBal"));
                        psmt.setString(4, rs.getString("FIVPayCatCode"));
                        psmt.setString(5, rs.getString("FPortCode"));
                        psmt.setDate(6, rs.getDate("FSTORAGEDATE"));
                        psmt.addBatch();

                        ppsmt.setString(1, rs.getString("FIVPayCatCode"));
                        ppsmt.setString(2, rs.getString("FPortCode"));
                        ppsmt.setDate(3, rs.getDate("FSTORAGEDATE"));
                        ppsmt.addBatch();
                    }
                    bHaveData = false;
                    
                    //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
                    dbl.closeResultSetFinal(rs);
                    //--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
                }
            }
            psmt.executeBatch();
            ppsmt.executeBatch();

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException(ex);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(psmt);
            dbl.closeStatementFinal(ppsmt);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
