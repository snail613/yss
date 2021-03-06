package com.yss.main.operdeal.opermanage.securitymanage;

import com.yss.dsub.*;
import com.yss.util.*;
import java.sql.*;
import java.math.*;
import com.yss.main.operdeal.bond.BondInsCfgFormula;
import java.util.HashMap;
import com.yss.main.operdeal.income.stat.StatBondInterest;

public class BondInterestManage extends BaseBean{
    public BondInterestManage(YssPub pub) {
        setYssPub(pub);
    }

    public void calBondInterestManage(java.util.Date dDate, String sPortCode) throws YssException{
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        boolean bTrans = false;
        PreparedStatement pst = null;
        //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
        HashMap hmRate = null;//储存百元债券利息
        BigDecimal sqPer100ZQRate = null;//税前百元债券利息
        BigDecimal per100ZQRate = null;//税后百元债券利息
        StatBondInterest bondInterest = null;
        //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
        //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
//        HashMap hmRoundScale = null;
//        ReadTypeBean readType = null;
//        BigDecimal bigPer100 = null;
        //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
        try
        {
        	//delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
//            CNInterfaceParamAdmin param = new CNInterfaceParamAdmin();
//            param.setYssPub(pub);
//            hmRoundScale = (HashMap)param.getReadTypeBean();
        	//delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            BondInsCfgFormula cfgFor = new BondInsCfgFormula();
            cfgFor.setYssPub(pub);
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " WHERE FDataSource = 'ZD' OR FCheckState <> 1";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSECURITYCODE, FRECORDDATE, FCURCPNDATE, FNEXTCPNDATE, FINTACCPER100, FSHIntAccPer100, FINTDAY, FDataSource, FCHECKSTATE, FCREATOR, FCREATETIME)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            //edit by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B 添加交易所代码字段
            strSql = "SELECT DISTINCT a.FExchangeCode,a.FHolidaysCode, d.* FROM " + pub.yssGetTableName("TB_Para_Security") + " a" +
                " JOIN (SELECT FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Stock_Security") +
                " WHERE FCheckState = 1" +
                " AND " + operSql.sqlStoragEve(dDate) +
                " UNION" +
                " SELECT FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_Subtrade") +
                " WHERE FCheckState = 1" +
                " AND FBargainDate = " + dbl.sqlDate(dDate) +
                " UNION" +
                " SELECT FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_Newissuetrade") +
                " WHERE FCheckState = 1" +
                " AND FTradeTypeCode IN ('43', '44')" +
                " AND FTransDate = " + dbl.sqlDate(dDate) +
                " UNION" +
                " SELECT FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_Devtrustbond") +
                " WHERE FCheckState = 1" +
                " AND FBargainDate = " + dbl.sqlDate(dDate) +
                " UNION" +
                " SELECT FSecurityCode" +
                " FROM " + pub.yssGetTableName("Tb_Data_Intbakbond") +
                " WHERE FCheckState = 1" +
                " AND FBargainDate = " + dbl.sqlDate(dDate) +
                ") b ON a.FSecurityCode = b.FSecurityCode" +
                " JOIN " + pub.yssGetTableName("Tb_Para_FixInterest") + " d ON a.FSecurityCode = d.FSecurityCode" +
                //" WHERE FCheckState = 1 AND (FExchangeCode = 'CS' OR FExchangeCode = 'CG' OR FExchangeCode = 'CY')" +
                //modify by nimengjing BUG #744 业务处理报错  2010.12.24
                " WHERE d.FCheckState = 1 AND (FExchangeCode = 'CS' OR FExchangeCode = 'CG' OR FExchangeCode = 'CY')" +//2010-9-9 仇旭峰 修改   未指定表别名出现 ORA-00918:未明确定义列 错误
                //-------------------------------end --BUG #744-------------------------------------
                " AND FCatCode = 'FI' " 
                +
                //" AND FSecurityCode NOT IN (SELECT FSecurityCode FROM " + pub.yssGetTableName("Tb_Data_BondInterest") + " c" +
                //" WHERE c.FRECORDDATE = " + dbl.sqlDate(dDate) + ")";
                
                // 性能优化，采用not exists可提高查询效率 by leeyu 20100819调整，合并太平版本时优化调整
                " and not exists(SELECT '1' FROM " + pub.yssGetTableName("Tb_Data_BondInterest") + " c"+
                " where c.FRECORDDATE = " + dbl.sqlDate(dDate) + " and c.FSecurityCode= a.FSecurityCode )"
                ;
            rs = dbl.queryByPreparedStatement(strSql);
            while(rs.next()){
            	//---add by songjie 2011.07.08 BUG 2190 QDV4易方达2011年6月29日01_B 合并深圳同事罗鹏程的代码---//
            	//add by luopengcheng from shenzhen apartment 计息日大于计息截止日，则不作处理
                if(YssFun.dateDiff(dDate, rs.getDate("FInsEndDate"))<0 ) {
                    continue;
                }
                //---add by songjie 2011.07.08 BUG 2190 QDV4易方达2011年6月29日01_B 合并深圳同事罗鹏程的代码---//
            	
                HashMap hmDate = new HashMap();
                int iDays = 0;//已计息天数
                cfgFor.getNextStartDateAndEndDate(dDate, rs, hmDate);
                //2009-1-14 蒋锦 修改 直接使用 dateDiff 会少计算一天计息日期，所以最后要加一天
                //QDV4赢时胜（上海）2010年1月12日02_B
                iDays = YssFun.dateDiff((java.util.Date)hmDate.get("InsStartDate"), dDate) + 1;
                
                //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
            	bondInterest = new StatBondInterest();
                bondInterest.setYssPub(pub);
                
                //默认取买入计息公式
              //modify by zhangfa 20100915 MS01724    国内银行间债券计提利息后，百元利息没有显示。    QDV4交银施罗德2010年9月10日01_B 
               //    FCalcInsMeticbuy改为FCalcInsMeticDay Buy 改为Day
                hmRate = bondInterest.domesticInnerCal(rs.getString("FSecurityCode"), 
                		rs.getString("FPeriodCode"), 
                		rs.getString("FCalcInsMeticbuy"),//add by zhouwei 20120219 买入利息
                		" ", 
                		rs.getString("FHolidaysCode"), 
                		dDate, 
                		0, 
                		" ",
                		" ",
                		"Buy",
                		" ",
                		sPortCode,
                		rs.getString("FExchangeCode"));
              //--------------------------------------------------------------------------------------------------------------------------------
                sqPer100ZQRate = new BigDecimal( (String) hmRate.get("before"));
                per100ZQRate = new BigDecimal( (String) hmRate.get("after"));
                //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                
                //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
//                readType = (ReadTypeBean)hmRoundScale.get(pub.getPrefixTB() + " " + sPortCode);
//                bigPer100 = YssD.mulD(YssD.divD(rs.getBigDecimal("FFaceRate"), new BigDecimal("365")),
//                                                 YssD.mulD(YssD.divD(rs.getBigDecimal("FFaceValue"), new BigDecimal("100")),
//                                                 new BigDecimal(iDays + "")));
//                if(rs.getString("FExchangeCode").equalsIgnoreCase(YssOperCons.YSS_JYSDM_YHJ)){
//                    //银行间默认保留12位
//                    bigPer100 = YssD.roundD(bigPer100, 12);
//                } else {
//                	//edit by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
//                    readType = (ReadTypeBean)hmRoundScale.get(pub.getPrefixTB() + " " + sPortCode);
//                    if(readType != null){
//                        bigPer100 = YssD.roundD(bigPer100, readType.getExchangePreci());
//                    } else {
//                        //交易所默认保留8位
//                        bigPer100 = YssD.roundD(bigPer100, 8);
//                    }
//                }
                //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                pst.setString(1, rs.getString("FSecurityCode"));
                pst.setDate(2, YssFun.toSqlDate(dDate));
                pst.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pst.setDate(4, YssFun.toSqlDate("9998-12-31"));
                //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                pst.setBigDecimal(5, sqPer100ZQRate);
                pst.setBigDecimal(6, per100ZQRate);
                //add by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
//                pst.setBigDecimal(5, bigPer100);
//                pst.setDouble(6, 0);
                //delete by songjie 2010.04.01 国内:MS00962 QDV4赢时胜（测试）2010年03月31日01_B
                pst.setInt(7, iDays);
                pst.setString(8, "ZD");
                pst.setInt(9, 1);
                pst.setString(10, pub.getUserCode());
                pst.setString(11, YssFun.formatDatetime(new java.util.Date()));
                pst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(false);
        } catch (Exception ex) {
            throw new YssException("计算每百元债券利息出错！", ex);
        } finally{
            dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
