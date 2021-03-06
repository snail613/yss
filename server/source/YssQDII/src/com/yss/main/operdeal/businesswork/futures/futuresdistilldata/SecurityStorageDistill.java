package com.yss.main.operdeal.businesswork.futures.futuresdistilldata;

import com.yss.main.operdeal.businesswork.BaseBusinWork;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPort;
import com.yss.util.YssException;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.yss.main.operdata.SecIntegratedBean;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.util.YssFun;
import com.yss.main.parasetting.SecurityBean;
import com.yss.util.YssOperCons;
import java.sql.PreparedStatement;
import java.sql.Connection;
import com.yss.util.YssD;
import com.yss.commeach.EachRateOper; //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --

/**
 * <p>Title: </p>
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
public class SecurityStorageDistill
    extends BaseBusinWork {
    private String securityCodes = "";
    private String analysisCode1 = "";
    private String analysisCode2 = "";
    private String analysisCode3 = "";
    private String subCatCode = ""; // add by fangjiang 2010.08.23

    public SecurityStorageDistill() {
    }

    public String doOperation(String sType) throws YssException {
        ArrayList securityStorage = getSecurityStorageData();
        //------ MS00383 QDV4招商证券2009年04月16日01_B  ----------------------------
//      if (securityStorage.size() > 0) { //去除size的判断，在插入之前先以交易类型删除一遍(删除方法在saveSecurityStorageData)
        saveSecurityStorageData(securityStorage, workDate, portCodes);
//      }
        //-------------------------------------------------------------------------
        return "";
    }

    private ArrayList getSecurityStorageData() throws YssException {
        ArrayList curSecIntegrate = null;
        ResultSet rs = null;
        SecIntegratedBean secIntegrate = null;
        StringBuffer buf = new StringBuffer(1000);
        boolean analy1;
        boolean analy2;
        boolean analy3;
        String sqlStr = "";
        buf.append(" select FNum,");
        buf.append(" a.FSecurityCode,FPortCode,FBrokerCode, FInvMgrCode,FTradeTypeCode,FBegBailAcctCode,FChageBailAcctCode,");
        buf.append(" FBargainDate,FSettleDate,FTradeAmount,FSubCatCode,"); //add FSubCatCode by fangjiang 2010.08.23 
        buf.append(" (case when FBegBailMoney is null then 0 ");
        buf.append(" else FBegBailMoney end) as FBegBailMoney,");
        buf.append(
            " (case when FTradeFee1 is null then 0 else FTradeFee1 end) as FTradeFee1,");
        buf.append(
            " (case when FTradeFee2 is null then 0 else FTradeFee2 end) as FTradeFee2,");
        buf.append(
            " (case when FTradeFee3 is null then 0 else FTradeFee3 end) as FTradeFee3,");
        buf.append(
            " (case when FTradeFee4 is null then 0 else FTradeFee4 end) as FTradeFee4,");
        buf.append(
            " (case when FTradeFee5 is null then 0 else FTradeFee5 end) as FTradeFee5,");
        buf.append(
            " (case when FTradeFee6 is null then 0 else FTradeFee6 end) as FTradeFee6,");
        buf.append(
            " (case when FTradeFee7 is null then 0 else FTradeFee7 end) as FTradeFee7,");
        buf.append(
            " (case when FTradeFee8 is null then 0 else FTradeFee8 end) as FTradeFee8");
        buf.append(" from ( select * from ");
        //edit by songjie 2013.03.12 FuturesTrade 改为 FuturesTrade_Tmp
        buf.append(pub.yssGetTableName("TB_Data_FuturesTrade_Tmp"));
        buf.append(" where FCheckState = 1 ");
        buf.append(" and FPortCode in (");
        buf.append(this.portCodes);
        buf.append(") and FBargainDate = ");
        buf.append(dbl.sqlDate(this.getWorkDate()));
        // add by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
        buf.append(" ) a join ");
        buf.append(" ( select * from  ");
        buf.append(pub.yssGetTableName("TB_Para_IndexFutures"));
        buf.append(" where FCheckState = 1");
        buf.append(" ) b on a.FSecurityCode = b.FSecurityCode");
        //------------------
        sqlStr = buf.toString();
        try {
            rs = dbl.queryByPreparedStatement(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
        } catch (Exception e) {
            throw new YssException("获取关联数据出错！", e);
        }
        try {
    		// ---add by songjie 2012.12.07 STORY #3371
    		// 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
    		boolean contractType = false;
    		ParaWithPort para = new ParaWithPort();
    		para.setYssPub(pub);
    		contractType = para.getFutursPositionType(this.portCodes.replace("'", ""));
    		// ---add by songjie 2012.12.07 STORY #3371
    		// 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
        	
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
            curSecIntegrate = new ArrayList();
            while (rs.next()) {
			    //edit by songjie 2012.12.07 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
                secIntegrate = setSecIntegrate(rs, analy1, analy2, analy3,contractType);
                curSecIntegrate.add(secIntegrate);
            }
        } catch (Exception ex) {
            throw new YssException("设置关联数据出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return curSecIntegrate;
    }

    private void saveSecurityStorageData(ArrayList SecIntegrateData, java.util.Date dWorkDay, String sPortCodes) throws
        YssException {
        String filterAnalysisCode1 = "";
        String filterAnalysisCode2 = "";
        String filterAnalysisCode3 = "";
        String filterSecurityCode = "";
        //------ MS00383 QDV4招商证券2009年04月16日01_B  无论是否有值，一定要删除一次  ------------
//      if (SecIntegrateData.size() > 0) {
        if (analysisCode1.length() > 0 &&
            analysisCode1.endsWith(",")) {
            filterAnalysisCode1 = this.analysisCode1.substring(0,
                analysisCode1.length() - 1);
        }
        if (analysisCode2.length() > 0 &&
            analysisCode2.endsWith(",")) {
            filterAnalysisCode2 = this.analysisCode2.substring(0,
                analysisCode2.length() - 1);
        }
        if (analysisCode3.length() > 0 &&
            analysisCode3.endsWith(",")) {
            filterAnalysisCode3 = this.analysisCode3.substring(0,
                analysisCode3.length() - 1);
        }
        if (securityCodes.length() > 0 &&
            securityCodes.endsWith(",")) {
            filterSecurityCode = this.securityCodes.substring(0,
                securityCodes.length() - 1);
        }
        insertData(SecIntegrateData, filterSecurityCode, dWorkDay, sPortCodes, filterAnalysisCode1,
                   filterAnalysisCode2, filterAnalysisCode3);
//      }
//------------------------------------------------------------------------------------
    }
    
    //edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001
    private SecIntegratedBean setSecIntegrate(ResultSet rs, boolean analy1,
                                              boolean analy2, boolean analy3,boolean contractType) throws
        YssException {
        SecIntegratedBean secIntegrate = null;
        SecurityBean security = null;
        EachRateOper eachOper = null; //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
        double baseRate = 0.0;
        double portRate = 0.0;
        try {
        	// add by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
        	this.subCatCode += rs.getString("FSubCatCode") + ",";
        	//-------------------
            secIntegrate = new SecIntegratedBean();
            
            //---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
            if(contractType){
            	if(rs.getString("FTradeTypeCode").equalsIgnoreCase("01")){//买入
            		secIntegrate.setIInOutType(1);
            	}else if(rs.getString("FTradeTypeCode").equalsIgnoreCase("02")){//卖出
            		secIntegrate.setIInOutType(-1);
            	}
            }else{
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase("21")) { //ping cang
                    secIntegrate.setIInOutType(-1);
                } else {
                    secIntegrate.setIInOutType(1);
                }
            }
            //---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//

//         FNum += rs.getString("FNum") + ",";
            secIntegrate.setSSecurityCode(rs.getString("FSecurityCode"));
            securityCodes += secIntegrate.getSSecurityCode() + ",";
            secIntegrate.setSExchangeDate(YssFun.formatDate(rs.getDate(
                "FBargainDate"),
                "yyyy-MM-dd"));
            secIntegrate.setSOperDate(YssFun.formatDate(rs.getDate("FBargainDate"),
                "yyyy-MM-dd"));
//            secIntegrate.setSRelaNum(" ");
//            secIntegrate.setSNumType(" ");
            //======add by xuxuming,20091203.保存关联编号,以便有删除交易数据时,可以根据此编号来删除综合业务表=====
            secIntegrate.setSRelaNum(rs.getString("FNum"));//将交易编号作为关联编号
            secIntegrate.setSNumType("FutruesTrade");//编号类型为股指期货交易
            //================end============================================

            secIntegrate.setSTradeTypeCode(rs.getString("FTradeTypeCode"));

            secIntegrate.setSPortCode(rs.getString("FPortCode"));
            if (analy1) {
                secIntegrate.setSAnalysisCode1(rs.getString("FInvMgrCode"));
            } else {
                secIntegrate.setSAnalysisCode1(" ");
            }
            analysisCode1 += secIntegrate.getSAnalysisCode1() + ",";
            if (analy2) {
                secIntegrate.setSAnalysisCode2(rs.getString("FBrokerCode"));
            } else {
                secIntegrate.setSAnalysisCode2(" ");
            }
            analysisCode2 += secIntegrate.getSAnalysisCode2() + ",";
            if (analy3) {
                secIntegrate.setSAnalysisCode3(" ");
            } else {
                secIntegrate.setSAnalysisCode3(" ");
            }
            analysisCode3 += secIntegrate.getSAnalysisCode3() + ",";
            secIntegrate.setDAmount(YssD.mul(rs.getDouble("FTradeAmount"), secIntegrate.getIInOutType()));
            secIntegrate.setDCost(0); //所有的成本为0
            secIntegrate.setDMCost(0);
            secIntegrate.setDVCost(0);

            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(secIntegrate.getSSecurityCode());
            security.getSetting();
            baseRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
                security.getTradeCuryCode(), secIntegrate.getSPortCode(),
                YssOperCons.YSS_RATE_BASE);
            secIntegrate.setDBaseCuryRate(baseRate);
            secIntegrate.setDBaseCost(0);
            secIntegrate.setDMBaseCost(0);
            secIntegrate.setDVBaseCost(0);

            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
//         portRate = this.getSettingOper().getCuryRate(rs.getDate("FBargainDate"),
//               security.getTradeCuryCode(), secIntegrate.getSPortCode(),
//               YssOperCons.YSS_RATE_PORT);
            eachOper = new EachRateOper();
            eachOper.setYssPub(pub);
            eachOper.getInnerPortRate(rs.getDate("FBargainDate"),
                                      security.getTradeCuryCode(),
                                      secIntegrate.getSPortCode());
            portRate = eachOper.getDPortRate();
            //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 更改汇率获取方式 by leeyu --
            secIntegrate.setDPortCost(0);
            secIntegrate.setDMPortCost(0);
            secIntegrate.setDVPortCost(0);

            secIntegrate.setDPortCuryRate(portRate);

            secIntegrate.checkStateId = 1;
        } catch (Exception e) {
            throw new YssException("设置综合业务数据出错！", e);
        }
        return secIntegrate;
    }

    private void insertData(ArrayList list, String securityCodes, java.util.Date dWorkDay, String sPortCodes,
                            String analysisCode1, String analysisCode2,
                            String analysisCode3) throws YssException {
        String sqlStr = "";
        PreparedStatement pst = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        String sNewNum = "";
        SecIntegratedBean secIntegrade = null;
        int count=1;
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        // add by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A 
        String filterSubCatCode = "";
        if (this.subCatCode.length() > 0 && this.subCatCode.endsWith(",")) {
        	filterSubCatCode = this.subCatCode.substring(0,
        			this.subCatCode.length() - 1);
        }
    	String[] subCatCodeArr = filterSubCatCode.split(",");
    	//-------------------
    	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
    	OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        try {
        	//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
        	integrateAdmin.setYssPub(pub);
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");
        } catch (Exception ex) {
            throw new YssException("获取分析代码出现异常！", ex);
        }
        sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
        	//---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 start---//
            " a where " +
            //------ MS00383 QDV4招商证券2009年04月16日01_B  ----------------------------
//            " FSecurityCode in (" + operSql.sqlCodes(securityCodes) + ")" +
            " a.FTradeTypeCode in (" + dbl.sqlString("20") + "," + dbl.sqlString("21") + ",'01','02')" + //业务类型为20,21的数据都予以删除
            //-------------------------------------------------------------------------
            (analy1 ? " and a.FAnalysisCode1 in (" + operSql.sqlCodes(analysisCode1) + ")" : "") +
            (analy2 ? " and a.FAnalysisCode2 in (" + operSql.sqlCodes(analysisCode2) + ")" : "") +
            (analy3 ? " and a.FAnalysisCode3 in (" + operSql.sqlCodes(analysisCode3) + ")" : "") +
            " and a.FPortCode in(" + sPortCodes + ")" +
            " and a.FOperDate = " + dbl.sqlDate(dWorkDay) +
            " and exists (select sec.* from " + pub.yssGetTableName("Tb_Para_Security") + 
            " sec where sec.FCatCode = 'FU' and sec.FSecurityCode = a.FSecurityCode)";
            //---edit by songjie 2012.12.09 STORY #3371 需求上海-[海富通基金]QDIIV4.0[紧急]20121130001 end---//
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
        } catch (Exception e) {
            throw new YssException("删除综合业务出错！", e);
        }
        sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime, FTSFTYPECODE, FSUBTSFTYPECODE) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            pst = dbl.getPreparedStatement(sqlStr);

            for (int i = 0; i < list.size(); i++) {
                secIntegrade = (SecIntegratedBean) list.get(i);
                sNewNum = "E" +
                    YssFun.formatDate(this.getWorkDate(),
                                      "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Integrated"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001",
                                           " where FExchangeDate=" +
                                           dbl.sqlDate(this.getWorkDate()) +
                                           " or FExchangeDate=" +
                                           dbl.sqlDate("9998-12-31"));
                pst.setString(1, sNewNum);
                //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                pst.setString(2, integrateAdmin.getKeyNum());
                pst.setInt(3, secIntegrade.getIInOutType());
                pst.setString(4, secIntegrade.getSSecurityCode());
                pst.setDate(5, YssFun.toSqlDate(secIntegrade.getSExchangeDate()));
                pst.setDate(6, YssFun.toSqlDate(secIntegrade.getSOperDate()));
                pst.setString(7, secIntegrade.getSTradeTypeCode());
                pst.setString(8, secIntegrade.getSRelaNum()); //这里的 sRelaNum,sNumType都为' '
//                pst.setString(9, " ");
                pst.setString(9, secIntegrade.getSNumType());//add by xuxuming,20091203.保存编号类型为股指期货交易
                pst.setString(10, secIntegrade.getSPortCode());
                pst.setString(11, secIntegrade.getSAnalysisCode1());
                pst.setString(12, secIntegrade.getSAnalysisCode2());
                pst.setString(13, secIntegrade.getSAnalysisCode3());  
                pst.setDouble(14, secIntegrade.getDAmount());
                pst.setDouble(15, secIntegrade.getDCost());
                pst.setDouble(16, secIntegrade.getDMCost());
                pst.setDouble(17, secIntegrade.getDVCost());
                pst.setDouble(18, secIntegrade.getDBaseCost());
                pst.setDouble(19, secIntegrade.getDMBaseCost());
                pst.setDouble(20, secIntegrade.getDVBaseCost());
                pst.setDouble(21, secIntegrade.getDPortCost());
                pst.setDouble(22, secIntegrade.getDMPortCost());
                pst.setDouble(23, secIntegrade.getDVPortCost());
                pst.setDouble(24, secIntegrade.getDBaseCuryRate());
                pst.setDouble(25, secIntegrade.getDPortCuryRate());
                pst.setString(26, "");
                pst.setString(27, "");
                pst.setInt(28, secIntegrade.checkStateId);
                pst.setString(29, pub.getUserCode());
                pst.setString(30, YssFun.formatDatetime(new java.util.Date()));
                //---------2009.03.17 蒋锦 MS00273 QDV4中金2009年02月27日01_A -------//
                pst.setString(31, YssOperCons.YSS_ZJDBLX_Cost);
                // add by fangjiang 2010.08.23 MS01439 QDV4博时2010年7月14日02_A
                // modify by fangjiang 2011.02.15 STORY #462
                if ( "FU01".equals(subCatCodeArr[i]) ) {
                	pst.setString(32, YssOperCons.YSS_ZJDBZLX_FU01_COST);
                } else if ( "FU02".equals(subCatCodeArr[i]) ) {
                	pst.setString(32, YssOperCons.YSS_ZJDBZLX_FU02_COST);
                } else if ( "FU03".equals(subCatCodeArr[i]) ) {
                	pst.setString(32, YssOperCons.YSS_ZJDBZLX_FU03_COST);
                } else if ( "FU04".equals(subCatCodeArr[i]) ) { //add by huangqirong 2012-08-21 商品期货
                	pst.setString(32, YssOperCons.YSS_ZJDBZLX_FU04_COST);
                }
                //-------------------
                //-----------------------------------------------------------------//
                //pst.executeUpdate();//delete by jsc 不应该在循环里调用该方法，如果调用这个方法会造成游标越界问题
                pst.addBatch();
                if(count ==500){
                	pst.executeBatch();
                	count=1;
                	continue;
                }

                count++;
            }
			pst.executeBatch();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("插入综合业务出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pst);
        }
    }
}
