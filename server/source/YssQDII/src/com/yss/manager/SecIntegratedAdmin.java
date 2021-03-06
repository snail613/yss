package com.yss.manager;

import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;

import java.sql.*;
import java.sql.*;

/**
 *
 * <p>Title: 2009-07-20 蒋锦 添加 综合业务的Admin 类</p>
 * MS00022 国内债券业务 QDV4.1赢时胜（上海）2009年4月20日22_A
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SecIntegratedAdmin extends BaseBean{

    private ArrayList addList  = new ArrayList();

    public SecIntegratedAdmin(YssPub pub) {
        setYssPub(pub);
    }

    public ArrayList getAddList() {
        return addList;
    }

    public void setAddList(ArrayList addList) {
        this.addList = addList;
    }

    public void addList(ArrayList list){
        addList.addAll(list);
    }

    private String buildWhereSql(java.util.Date dExchangeDate,
                                 java.util.Date dOperDate,
                                 String sSecurityCode,
                                 String sPortCode,
                                 String sTradeTypeCode,
                                 String sTsfTypeCode,
                                 String sSubTsfTypeCode,
                                 String sAnalysisCode1,
                                 String sAnalysisCode2,
                                 String sAnalysisCode3,
                                 String sAttrClsCode,
                                 String sNum,
                                 int iInOutType) throws YssException {
        String sResult = " WHERE 1=1 ";
        try {
            if (dExchangeDate != null){
                sResult += " AND FExchangeDate = " + dbl.sqlDate(dExchangeDate);
            }
            if (dOperDate != null){
                sResult += " AND FOperDate = " + dbl.sqlDate(dOperDate);
            }
            if(sSecurityCode != null && sSecurityCode.length() > 0){
                sResult += " AND FSecurityCode IN (" + operSql.sqlCodes(sSecurityCode) + ")";
            }
            if(sPortCode != null && sPortCode.length() > 0){
                sResult += " AND FPortCode IN (" + operSql.sqlCodes(sPortCode) + ")";
            }
            if(sTradeTypeCode != null && sTradeTypeCode.length() > 0){
                sResult += " AND FTradeTypeCode IN (" + operSql.sqlCodes(sTradeTypeCode) + ")";
            }
            if(sTsfTypeCode != null && sTsfTypeCode.length() > 0){
                sResult += " AND FTsfTypeCode IN (" + operSql.sqlCodes(sTsfTypeCode) + ")";
            }
            if(sSubTsfTypeCode != null && sSubTsfTypeCode.length() > 0){
                sResult += " AND FSubTsfTypeCode IN (" + operSql.sqlCodes(sSubTsfTypeCode) + ")";
            }
            if(sAnalysisCode1 != null && sAnalysisCode1.length() > 0){
                sResult += " AND FAnalysisCode1 IN (" + operSql.sqlCodes(sAnalysisCode1) + ")";
            }
            if(sAnalysisCode2 != null && sAnalysisCode2.length() > 0){
                sResult += " AND FAnalysisCode2 IN (" + operSql.sqlCodes(sAnalysisCode2) + ")";
            }
            if(sAnalysisCode3 != null && sAnalysisCode3.length() > 0){
                sResult += " AND FAnalysisCode3 IN (" + operSql.sqlCodes(sAnalysisCode3) + ")";
            }
            if(sAttrClsCode != null && sAttrClsCode.length() > 0){
                sResult += " AND FAttrClsCode IN (" + operSql.sqlCodes(sAttrClsCode) + ")";
            }
            if(sNum != null && sNum.length() > 0){
                sResult += " AND FNum IN (" + operSql.sqlCodes(sNum) + ")";
            }
            if(iInOutType != 0){
                sResult += " AND FInOutType = " + iInOutType;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
        return sResult;
    }

    public void delete(java.util.Date dExchangeDate,
                       java.util.Date dOperDate,
                       String sSecurityCode,
                       String sPortCode,
                       String sTradeTypeCode,
                       String sTsfTypeCode,
                       String sSubTsfTypeCode,
                       String sAnalysisCode1,
                       String sAnalysisCode2,
                       String sAnalysisCode3,
                       String sAttrClsCode,
                       String sNum,
                       int iInOutType) throws YssException{
        String strSql = "";
        try {
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Data_Integrated") +
                buildWhereSql(dExchangeDate,
                              dOperDate,
                              sSecurityCode,
                              sPortCode,
                              sTradeTypeCode,
                              sTsfTypeCode,
                              sSubTsfTypeCode,
                              sAnalysisCode1,
                              sAnalysisCode2,
                              sAnalysisCode3,
                              sAttrClsCode,
                              sNum,
                              iInOutType);
            dbl.executeSql(strSql);
        } catch (Exception ex) {
            throw new YssException("删除综合业务数据出错！", ex);
        }
    }

    public void insert(java.util.Date dOperDate,
                       String sPortCode,
                       String sTradeTypeCode,
                       boolean bAutoDel) throws YssException {
        insert(null, dOperDate, "", sPortCode, sTradeTypeCode, "", "", "", "", "", "", "", 0, bAutoDel);
    }
    
    //------- add by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 
    public void insert(java.util.Date dOperDate,
            		   String sPortCode,
            		   String sTradeTypeCode,
            		   boolean bAutoDel,
            		   String sNumType) throws YssException {
    	insert(null, dOperDate, "", sPortCode, sTradeTypeCode, "", "", "", "", "", "", "", 0, bAutoDel,sNumType);
    }
    //----------------------------MS01098--------------------------//
    
    public void insert(java.util.Date dExchangeDate,
                       java.util.Date dOperDate,
                       String sSecurityCode,
                       String sPortCode,
                       String sTradeTypeCode,
                       String sTsfTypeCode,
                       String sSubTsfTypeCode,
                       String sAnalysisCode1,
                       String sAnalysisCode2,
                       String sAnalysisCode3,
                       String sAttrClsCode,
                       String sNum,
                       int iInOutType,
                       boolean bAutoDel) throws YssException{
        String strSql = "";
        String sNewNum = "";
        SecIntegratedBean secIntegrade = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
            if(bAutoDel){
                delete(dExchangeDate,
                       dOperDate,
                       sSecurityCode,
                       sPortCode,
                       sTradeTypeCode,
                       sTsfTypeCode,
                       sSubTsfTypeCode,
                       sAnalysisCode1,
                       sAnalysisCode2,
                       sAnalysisCode3,
                       sAttrClsCode,
                       sNum,
                       iInOutType);
            }
            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Data_Integrated") +
                "(FNUM, FSUBNUM, FINOUTTYPE, FSECURITYCODE, FEXCHANGEDATE, FOPERDATE, FTRADETYPECODE, FRELANUM, FNUMTYPE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FAMOUNT, FEXCHANGECOST, FMEXCOST, FVEXCOST, FPORTEXCOST, FMPORTEXCOST, FVPORTEXCOST, FBASEEXCOST, FMBASEEXCOST, FVBASEEXCOST, FBASECURYRATE, FPORTCURYRATE, FTSFTYPECODE, FSUBTSFTYPECODE, FSECEXDESC, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME, FATTRCLSCODE, FInvestType)" +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for(int i = 0; i < addList.size(); i++){
                secIntegrade = (SecIntegratedBean) addList.get(i);
                sNewNum = "E" +
                    YssFun.formatDate(secIntegrade.getSOperDate(),
                                      "yyyyMMdd") +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Integrated"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000001",
                                           " where FExchangeDate=" +
                                           dbl.sqlDate(secIntegrade.getSOperDate()) +
                                           " or FExchangeDate=" +
                                           dbl.sqlDate("9998-12-31"));
               
                yssPst.setString(1, sNewNum);               
                //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                yssPst.setString(2, integrateAdmin.getKeyNum());                
                yssPst.setInt(3, secIntegrade.getIInOutType());
                yssPst.setString(4, secIntegrade.getSSecurityCode());
                yssPst.setDate(5, YssFun.toSqlDate(secIntegrade.getSExchangeDate()));
                yssPst.setDate(6, YssFun.toSqlDate(secIntegrade.getSOperDate()));
                yssPst.setString(7, secIntegrade.getSTradeTypeCode());
                yssPst.setString(8, secIntegrade.getSRelaNum());
                yssPst.setString(9, secIntegrade.getSNumType());
                yssPst.setString(10, secIntegrade.getSPortCode());
                yssPst.setString(11, secIntegrade.getSAnalysisCode1());
                yssPst.setString(12, secIntegrade.getSAnalysisCode2());
                yssPst.setString(13, secIntegrade.getSAnalysisCode3());
                yssPst.setDouble(14, secIntegrade.getDAmount());
                yssPst.setDouble(15, secIntegrade.getDCost());
                yssPst.setDouble(16, secIntegrade.getDMCost());
                yssPst.setDouble(17, secIntegrade.getDVCost());
                yssPst.setDouble(18, secIntegrade.getDPortCost());
                yssPst.setDouble(19, secIntegrade.getDMPortCost());
                yssPst.setDouble(20, secIntegrade.getDVPortCost());
                yssPst.setDouble(21, secIntegrade.getDBaseCost());
                yssPst.setDouble(22, secIntegrade.getDMBaseCost());
                yssPst.setDouble(23, secIntegrade.getDVBaseCost());
                yssPst.setDouble(24, secIntegrade.getDBaseCuryRate());
                yssPst.setDouble(25, secIntegrade.getDPortCuryRate());
                yssPst.setString(26, secIntegrade.getSTsfTypeCode());
                yssPst.setString(27, secIntegrade.getSSubTsfTypeCode());
                yssPst.setString(28, "");
                yssPst.setString(29, "");
                yssPst.setInt(30, secIntegrade.checkStateId);
                yssPst.setString(31, pub.getUserCode());
                yssPst.setString(32, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(33, pub.getUserCode());
                yssPst.setString(34, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(35, secIntegrade.getAttrClsCode());
                yssPst.setString(36, secIntegrade.getInvestType());
                yssPst.executeUpdate();
            }
        } catch (Exception ex) {
            throw new YssException("插入综合业务数据出错！", ex);
        }
    }
    
    /**
     * add by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 
     */
    public void insert(java.util.Date dExchangeDate, java.util.Date dOperDate,
			String sSecurityCode, String sPortCode, String sTradeTypeCode,
			String sTsfTypeCode, String sSubTsfTypeCode, String sAnalysisCode1,
			String sAnalysisCode2, String sAnalysisCode3, String sAttrClsCode,
			String sNum, int iInOutType, boolean bAutoDel,String sNumType) throws YssException {
		String strSql = "";
		String sNewNum = "";
		SecIntegratedBean secIntegrade = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
		//Connection conn = dbl.loadConnection();
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		try {
			if (bAutoDel) {
				delete(dExchangeDate, dOperDate, sSecurityCode, sPortCode,
						sTradeTypeCode, sTsfTypeCode, sSubTsfTypeCode,
						sAnalysisCode1, sAnalysisCode2, sAnalysisCode3,
						sAttrClsCode, sNum, iInOutType, sNumType);
			}
			strSql = "INSERT INTO "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ "(FNUM, FSUBNUM, FINOUTTYPE, FSECURITYCODE, FEXCHANGEDATE, FOPERDATE, FTRADETYPECODE, FRELANUM, FNUMTYPE, FPORTCODE, FANALYSISCODE1, FANALYSISCODE2, FANALYSISCODE3, FAMOUNT, FEXCHANGECOST, FMEXCOST, FVEXCOST, FPORTEXCOST, FMPORTEXCOST, FVPORTEXCOST, FBASEEXCOST, FMBASEEXCOST, FVBASEEXCOST, FBASECURYRATE, FPORTCURYRATE, FTSFTYPECODE, FSUBTSFTYPECODE, FSECEXDESC, FDESC, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME, FATTRCLSCODE, FInvestType)"
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//			pst = conn.prepareStatement(strSql);
			yssPst = dbl.getYssPreparedStatement(strSql);

			for (int i = 0; i < addList.size(); i++) {
				secIntegrade = (SecIntegratedBean) addList.get(i);
				sNewNum = "E"
						+ YssFun.formatDate(secIntegrade.getSOperDate(),
								"yyyyMMdd")
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_Integrated"), dbl
								.sqlRight("FNUM", 6), "000001",
								" where FExchangeDate="
										+ dbl.sqlDate(secIntegrade
												.getSOperDate())
										+ " or FExchangeDate="
										+ dbl.sqlDate("9998-12-31"));

				yssPst.setString(1, sNewNum);
				//add by guolongchao 20110819 STORY 1207 获取最大的编号
				//delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
				//int maxId=getSecIntergrateAdmin(sNewNum);		
				//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
				yssPst.setString(2, integrateAdmin.getKeyNum());
				yssPst.setInt(3, secIntegrade.getIInOutType());
				yssPst.setString(4, secIntegrade.getSSecurityCode());
				yssPst.setDate(5, YssFun
						.toSqlDate(secIntegrade.getSExchangeDate()));
				yssPst.setDate(6, YssFun.toSqlDate(secIntegrade.getSOperDate()));
				yssPst.setString(7, secIntegrade.getSTradeTypeCode());
				yssPst.setString(8, secIntegrade.getSRelaNum());
				yssPst.setString(9, secIntegrade.getSNumType());
				yssPst.setString(10, secIntegrade.getSPortCode());
				yssPst.setString(11, secIntegrade.getSAnalysisCode1());
				yssPst.setString(12, secIntegrade.getSAnalysisCode2());
				yssPst.setString(13, secIntegrade.getSAnalysisCode3());
				yssPst.setDouble(14, secIntegrade.getDAmount());
				yssPst.setDouble(15, secIntegrade.getDCost());
				yssPst.setDouble(16, secIntegrade.getDMCost());
				yssPst.setDouble(17, secIntegrade.getDVCost());
				yssPst.setDouble(18, secIntegrade.getDPortCost());
				yssPst.setDouble(19, secIntegrade.getDMPortCost());
				yssPst.setDouble(20, secIntegrade.getDVPortCost());
				yssPst.setDouble(21, secIntegrade.getDBaseCost());
				yssPst.setDouble(22, secIntegrade.getDMBaseCost());
				yssPst.setDouble(23, secIntegrade.getDVBaseCost());
				yssPst.setDouble(24, secIntegrade.getDBaseCuryRate());
				yssPst.setDouble(25, secIntegrade.getDPortCuryRate());
				yssPst.setString(26, secIntegrade.getSTsfTypeCode());
				yssPst.setString(27, secIntegrade.getSSubTsfTypeCode());
				yssPst.setString(28, "");
				yssPst.setString(29, "");
				yssPst.setInt(30, secIntegrade.checkStateId);
				yssPst.setString(31, pub.getUserCode());
				yssPst.setString(32, YssFun.formatDatetime(new java.util.Date()));
				yssPst.setString(33, pub.getUserCode());
				yssPst.setString(34, YssFun.formatDatetime(new java.util.Date()));
				yssPst.setString(35, secIntegrade.getAttrClsCode());
				yssPst.setString(36, secIntegrade.getInvestType());
				yssPst.executeUpdate();
			}
		} catch (Exception ex) {
			throw new YssException("插入综合业务数据出错！", ex);
		} 
		//---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B start---//
		finally{
			dbl.closeStatementFinal(yssPst);
		}
		//---edit by songjie 2011.10.28 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B end---//
	}
    
    /**
     * add by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 
     */
    public void delete(java.util.Date dExchangeDate, java.util.Date dOperDate,
			String sSecurityCode, String sPortCode, String sTradeTypeCode,
			String sTsfTypeCode, String sSubTsfTypeCode, String sAnalysisCode1,
			String sAnalysisCode2, String sAnalysisCode3, String sAttrClsCode,
			String sNum, int iInOutType, String sNumType) throws YssException {
		String strSql = "";
		try {
			strSql = "DELETE FROM "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ buildWhereSql(dExchangeDate, dOperDate, sSecurityCode,
							sPortCode, sTradeTypeCode, sTsfTypeCode,
							sSubTsfTypeCode, sAnalysisCode1, sAnalysisCode2,
							sAnalysisCode3, sAttrClsCode, sNum, iInOutType, sNumType);
			dbl.executeSql(strSql);
		} catch (Exception ex) {
			throw new YssException("删除综合业务数据出错！", ex);
		}
	}
    
    /**
     * add by wangzuochun 2010.05.12  MS01098  期权业务和国内各业务处理同时做的时候会误删除综合业务数据    QDV4国内（测试）2010年04月16日02_B 
     */
    private String buildWhereSql(java.util.Date dExchangeDate,
			java.util.Date dOperDate, String sSecurityCode, String sPortCode,
			String sTradeTypeCode, String sTsfTypeCode, String sSubTsfTypeCode,
			String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, String sAttrClsCode, String sNum,
			int iInOutType, String sNumType) throws YssException {
		String sResult = " WHERE 1=1 ";
		try {
			if (dExchangeDate != null) {
				sResult += " AND FExchangeDate = " + dbl.sqlDate(dExchangeDate);
			}
			if (dOperDate != null) {
				sResult += " AND FOperDate = " + dbl.sqlDate(dOperDate);
			}
			if (sSecurityCode != null && sSecurityCode.length() > 0) {
				sResult += " AND FSecurityCode IN ("
						+ operSql.sqlCodes(sSecurityCode) + ")";
			}
			if (sPortCode != null && sPortCode.length() > 0) {
				sResult += " AND FPortCode IN (" + operSql.sqlCodes(sPortCode)
						+ ")";
			}
			if (sTradeTypeCode != null && sTradeTypeCode.length() > 0) {
				sResult += " AND FTradeTypeCode IN ("
						+ operSql.sqlCodes(sTradeTypeCode) + ")";
			}
			if (sTsfTypeCode != null && sTsfTypeCode.length() > 0) {
				sResult += " AND FTsfTypeCode IN ("
						+ operSql.sqlCodes(sTsfTypeCode) + ")";
			}
			if (sSubTsfTypeCode != null && sSubTsfTypeCode.length() > 0) {
				sResult += " AND FSubTsfTypeCode IN ("
						+ operSql.sqlCodes(sSubTsfTypeCode) + ")";
			}
			if (sAnalysisCode1 != null && sAnalysisCode1.length() > 0) {
				sResult += " AND FAnalysisCode1 IN ("
						+ operSql.sqlCodes(sAnalysisCode1) + ")";
			}
			if (sAnalysisCode2 != null && sAnalysisCode2.length() > 0) {
				sResult += " AND FAnalysisCode2 IN ("
						+ operSql.sqlCodes(sAnalysisCode2) + ")";
			}
			if (sAnalysisCode3 != null && sAnalysisCode3.length() > 0) {
				sResult += " AND FAnalysisCode3 IN ("
						+ operSql.sqlCodes(sAnalysisCode3) + ")";
			}
			if (sAttrClsCode != null && sAttrClsCode.length() > 0) {
				sResult += " AND FAttrClsCode IN ("
						+ operSql.sqlCodes(sAttrClsCode) + ")";
			}
			if (sNum != null && sNum.length() > 0) {
				sResult += " AND FNum IN (" + operSql.sqlCodes(sNum) + ")";
			}
			if (iInOutType != 0) {
				sResult += " AND FInOutType = " + iInOutType;
			}
			if (sNumType != null && sNumType.length() > 0) {
				sResult += " AND FNumType IN ("
						+ operSql.sqlCodes(sNumType) + ")";
			}
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(), ex);
		}
		return sResult;
	}
    
    //add by guolongchao 20110819 STORY 1207 获取最大的编号
    private int getSecIntergrateAdmin(String sNewNum) throws YssException
    {
    	int max=0;
    	String sql="select max(FSubNum) from "+pub.yssGetTableName("Tb_Data_Integrated")+" where FNum='"+sNewNum+"'";    	
    	ResultSet rs=null;
    	String temp="";
    	try 
    	{
    		rs=dbl.openResultSet(sql);
			if(rs.next())			
				temp=rs.getString(1);
			if(temp!=null&&temp.length()>0)
				max=YssFun.toInt(temp.substring(temp.length()-5, temp.length()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		return max;
    }
    
}
