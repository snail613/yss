package com.yss.main.operdata.futures;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.main.operdata.*;
import com.yss.util.*;

/**
 * <p>Title:xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持</p>
 *
 * <p>Description: 期权业务处理：插入数据到综合业务表中</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsIntegratedAdmin
    extends SecIntegratedBean {
    private OptionsIntegratedAdmin optIntegrated;
    public OptionsIntegratedAdmin() {
    }

    /**
     *  清除数据 xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
     * @param dWorkDay Date 操作日期
     * @param sPortCode String 组合代码
     * @param analysisCode1 String 分析代码1
     * @param analysisCode2 String 分析代码2
     * @param analysisCode3 String 分析代码3
     * @param sNumType 业务类型
     * @throws YssException 异常
     */
    public void deleteData(Date dWorkDay, String sPortCode, String analysisCode1, String analysisCode2,
                           String analysisCode3,String sNumType) throws YssException {
        String strSql = "";
        boolean analy1 = false;
        boolean analy2 = false;
        boolean analy3 = false;
        boolean bTrans = true;
        Connection conn = dbl.loadConnection();
        try {
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Security");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Security");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Security");

            strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") +
                " where " +
                " FTradeTypeCode in (" + dbl.sqlString("01") + "," + dbl.sqlString("02") + "," + dbl.sqlString("32FP") + "," + dbl.sqlString("33FP") + "," + dbl.sqlString("34FP") + ")" + //业务类型为0,1,2的数据都予以删除
                (analy1 ? " and FAnalysisCode1 in (" + operSql.sqlCodes(analysisCode1) + ")" : "") +
                (analy2 ? " and FAnalysisCode2 in (" + operSql.sqlCodes(analysisCode2) + ")" : "") +
                (analy3 ? " and FAnalysisCode3 in (" + operSql.sqlCodes(analysisCode3) + ")" : "") +
                " and FPortCode in(" + this.operSql.sqlCodes(sPortCode) + ")" +
                " and FOperDate = " + dbl.sqlDate(dWorkDay) +
                " and FNUMTYPE = " + dbl.sqlString(sNumType);
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除综合业务表中期权交易关联数据出错\r\n", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 保存数据到综合业务表中
     * @param optionsTradeData ArrayList 保存数据的ArrayList
     * @param dWorkDay Date 操作日期
     * @return String
     */
    public String saveMutliSetting(ArrayList optionsTradeData, Date dWorkDay) throws YssException {
        String sqlStr = "";
        PreparedStatement pst = null;
        String sNewNum = "";
        try {

            sqlStr = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
                " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
                " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
                " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
                " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime, FTSFTYPECODE, FSUBTSFTYPECODE) " +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            pst = dbl.openPreparedStatement(sqlStr);

            for (int i = 0; i < optionsTradeData.size(); i++) {
                optIntegrated = (OptionsIntegratedAdmin) optionsTradeData.get(i);
                sNewNum = "E" +
                    YssFun.formatDate(dWorkDay, "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Integrated"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001",
                                           " where FExchangeDate=" +
                                           dbl.sqlDate(dWorkDay) +
                                           " or FExchangeDate=" +
                                           dbl.sqlDate("9998-12-31"));
                pst.setString(1, sNewNum);
                //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                pst.setString(2, getKeyNum());
                pst.setInt(3, optIntegrated.getIInOutType());
                pst.setString(4, optIntegrated.getSSecurityCode());
                pst.setDate(5, YssFun.toSqlDate(optIntegrated.getSExchangeDate()));
                pst.setDate(6, YssFun.toSqlDate(optIntegrated.getSOperDate()));
                pst.setString(7, optIntegrated.getSTradeTypeCode());
                pst.setString(8, optIntegrated.getSRelaNum()); //这里的 sRelaNum,sNumType都为' '
                pst.setString(9, optIntegrated.getSNumType());// xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
                pst.setString(10, optIntegrated.getSPortCode());
                pst.setString(11, optIntegrated.getSAnalysisCode1());
                pst.setString(12, optIntegrated.getSAnalysisCode2());
                pst.setString(13, optIntegrated.getSAnalysisCode3());
                pst.setDouble(14, optIntegrated.getDAmount());
                pst.setDouble(15, optIntegrated.getDCost());
                pst.setDouble(16, optIntegrated.getDMCost());
                pst.setDouble(17, optIntegrated.getDVCost());
                pst.setDouble(18, optIntegrated.getDBaseCost());
                pst.setDouble(19, optIntegrated.getDMBaseCost());
                pst.setDouble(20, optIntegrated.getDVBaseCost());
                pst.setDouble(21, optIntegrated.getDPortCost());
                pst.setDouble(22, optIntegrated.getDMPortCost());
                pst.setDouble(23, optIntegrated.getDVPortCost());
                pst.setDouble(24, optIntegrated.getDBaseCuryRate());
                pst.setDouble(25, optIntegrated.getDPortCuryRate());
                pst.setString(26, "");
                pst.setString(27, "");
                pst.setInt(28, optIntegrated.checkStateId);
                pst.setString(29, pub.getUserCode());
                pst.setString(30, YssFun.formatDatetime(new java.util.Date()));
                pst.setString(31, YssOperCons.YSS_ZJDBLX_Cost);
                pst.setString(32, YssOperCons.YSS_ZJDBZLX_FP01_COST); //modify by fangjiang 2010.11.09 BUG #264 期权业务时，综合业务中的调拨子类型错误

                pst.executeUpdate();
            }
        } catch (Exception e) {
            throw new YssException("插入综合业务数据出错！\r\t", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }

        return "";
    }
    
	/**
	 * add by songjie 2012.12.20 
	 * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
	 * 获取主键编号
	 * @return
	 * @throws YssException
	 */
	public String getKeyNum() throws YssException{
		String num = "";
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		int maxNum = 0;
		try{
			conn.setAutoCommit(false);
			
			if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Data_Integrated")){
				strSql = " select max(FSubNum) as FSubNum from " + pub.yssGetTableName("Tb_Data_Integrated") + 
				" where subStr(FNum,0,1) <> 'E' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FSubNum") != null && YssFun.isNumeric(rs.getString("FSubNum"))){
						maxNum = Integer.parseInt(rs.getString("FSubNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Data_Integrated " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
				
				dbl.executeSql(strSql);
			}
			
			strSql = " select trim(to_char(SEQ_" + pub.getPrefixTB() + 
			"_Data_Integrated.NextVal,'00000000000000000000')) as FNum from dual ";
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
    			num = rs.getString("FNum");
    		}
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			
			return num;
		}catch(Exception e){
			throw new YssException("获取最大编号出错!\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
	}
}
