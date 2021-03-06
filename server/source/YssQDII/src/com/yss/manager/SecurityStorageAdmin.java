package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

public class SecurityStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public SecurityStorageAdmin() {
    }

    public void addList(SecurityStorageBean securitystorage) {
        this.addList.add(securitystorage);
    }

   /**
    * 重载方法，添加证券代码作为条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011 合并太平版本代码
    * @param dStartDate
    * @param dEndDate
    * @param ports
    * @param bReCost
    * @param securityCode
    * @throws YssException
    */
   public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
		   			  String ports,boolean bReCost,String securityCode)
   throws YssException{
	   insert(dStartDate,dEndDate,ports,"","",securityCode,"");
   }
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, boolean bReCost
        ) throws YssException {
        insert(dStartDate, dEndDate, ports, "", "", "", "");
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr, boolean bReCost
        ) throws YssException {
        insert(dStartDate, dEndDate, ports, invMgr, "", "", "");
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String broker, String security, boolean bReCost
        ) throws YssException {
        insert(dStartDate, dEndDate, ports, "", broker, security, "");
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dStartDate Date
     * @param dEndDate Date
     * @param ports String
     * @param invMgr String
     * @param broker String
     * @param security String
     * @param cury String
     * @param sAttrClsCode String
     * @return String
     * @throws YssException
     */
    private String buildWhereSql(java.util.Date dStartDate, java.util.Date dEndDate,
                                 String ports, String invMgr,
                                 //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 添加投资类型
                                 String broker, String security, String cury, String sAttrClsCode, String investType
        ) throws YssException {
        String sResult = " where 1=1 ";
        if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) { //lzp  modify  FYearMonth<> 后面字符要用单引号  否则DB2报错
            sResult = sResult + " and FYearMonth<> '" + new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
        }
        if (dStartDate != null && dEndDate != null) {
            sResult += " and FStorageDate  between " +
                dbl.sqlDate(dStartDate) + " and " + dbl.sqlDate(dEndDate);
        }
        /*
          BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
          修改原因：if语句逻辑错误，if (ports.length() > 0 && ports !=null)。以下5条类似的if语句均为此问题
          改正：应先判断对象是否为空，再判断其长度if (ports !=null && ports.length() > 0)
          修改人：panjunfang
          修改时间：2009-05-19
         */
        if (ports != null && ports.length() > 0) {
            sResult += " and FPortCode in (" + operSql.sqlCodes(ports) + ")";
        }
        if (invMgr != null && invMgr.length() > 0) {
            if (invMgr.indexOf(",") > 0) {
                sResult += " and FAnalysisCode1 in (" + operSql.sqlCodes(invMgr) + ")"; //条件改为in,使得删除时的范围更大。sj edit
            } else {
                sResult += " and FAnalysisCode1 =" + dbl.sqlString(invMgr);
            }
        }
        if (broker != null && broker.length() > 0) {
            if (broker.indexOf(",") > 0) {
                sResult += " and FAnalysisCode2 in (" + operSql.sqlCodes(broker) + ")"; //条件改为in,使得删除时的范围更大。sj edit
            } else {
                sResult += " and FAnalysisCode2 = " + dbl.sqlString(broker);
            }
        }
        if (security != null && security.length() > 0) {
            if (security.indexOf(",") > 0) {
            	/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
            	* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//                sResult += " and FSecurityCode in (" + operSql.sqlCodes(security) +")"; //条件改为in,使得删除时的范围更大。sj edit
            	sResult += " and (" + operSql.getNumsDetail(security,"FSecurityCode",500) + ")";
            	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
            } else {
                sResult += " and FSecurityCode = " + dbl.sqlString(security);
            }
        }
        if (cury != null && cury.length() > 0) {
            if (cury.indexOf(",") > 0) {
                sResult += " and FCuryCode in (" + operSql.sqlCodes(cury) + ")"; //条件改为in,使得删除时的范围更大。sj edit
            } else {
                sResult += " and FCuryCode = " + dbl.sqlString(cury);
            }
        }
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        if (sAttrClsCode !=null && sAttrClsCode.length() >0){
            if(sAttrClsCode.indexOf(",") > 0){
                sResult += " and FAttrClsCode in (" + operSql.sqlCodes(sAttrClsCode) + ")";
            } else {
                sResult += " AND FAttrClsCode = " + dbl.sqlString(sAttrClsCode);
            }
        }
        //---add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B---//
        if (investType !=null && investType.length() >0){
            if(investType.indexOf(",") > 0){
                sResult += " and FInvestType in (" + operSql.sqlCodes(investType) + ")";
            } else {
                sResult += " AND FInvestType = " + dbl.sqlString(investType);
            }
        }
        //---add by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B---//
        return sResult;
    }

//删除
    public void delete(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String broker, String security, String cury
        ) throws YssException {
        String strSql = "";
        // ResultSet rs = null;
        String sWhereSql = "";
        PreparedStatement pst = null;
        try {
        	//edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
            sWhereSql = this.buildWhereSql(dStartDate, dEndDate, ports, invMgr, broker, security, cury, "", "");
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return;
            }
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Security") +
                sWhereSql;
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String broker, String security, String cury
        ) throws YssException {
        String strSql = "";
        SecurityStorageBean securitystorage = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        int i = 0;
        try {
//         conn.setAutoCommit(false);
//         bTrans = true;
//         strSql = "delete from " + pub.yssGetTableName("Tb_Stock_Security") +
//               " where FStorageDate between " + dbl.sqlDate(dStartDate) +
//               " and " + dbl.sqlDate(dEndDate) +
//               ( (ports == null || ports.length() == 0) ? " " :
//                " and FPortCode in (" + ports + ")") +
//               ( (invMgr == null || invMgr.length() == 0) ? " " :
//                " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
//               ( (broker == null || broker.length() == 0) ? " " :
//                " and FAnalysisCode2 = " + dbl.sqlString(broker)) +
//               ( (security == null || security.length() == 0) ? " " :
//                " and FSecurityCode = " +
//                dbl.sqlString(security)) +
//               ( (cury == null || cury.length() == 0) ? " " :
//                " and FCuryCode = " + dbl.sqlString(cury)) +
//               " and FStorageInd <> 2";
            //先删除
            this.delete(dStartDate, dEndDate, ports, invMgr, broker, security, cury);

            //添加实际利率字段 MS00656 QDV4赢时胜(上海)2009年8月24日01_A 2009.09.03 蒋锦
//            pst = conn.prepareStatement(
//                "insert into " +
//                pub.yssGetTableName("Tb_Stock_Security") +
//                "(FSecurityCode, FYearMonth, FStorageDate, FPortCode, FCuryCode, FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost, FFreezeAmount, FBaseCuryRate, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
//                " FPortCuryRate, FPortCuryCost, FMPortCuryCost, FVPortCuryCost, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3,FMarketPrice, FStorageInd," +
//                " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime," +
//                " FBailMoney,FCatType,FAttrClsCode,FInvestType,FEffectiveRate)" +
//                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            yssPst = dbl.getYssPreparedStatement(
                    "insert into " +
                    pub.yssGetTableName("Tb_Stock_Security") +
                    "(FSecurityCode, FYearMonth, FStorageDate, FPortCode, FCuryCode, FStorageAmount, FStorageCost, FMStorageCost, FVStorageCost, FFreezeAmount, FBaseCuryRate, FBaseCuryCost, FMBaseCuryCost, FVBaseCuryCost, " +
                    " FPortCuryRate, FPortCuryCost, FMPortCuryCost, FVPortCuryCost, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3,FMarketPrice, FStorageInd," +
                    " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime," +
                    " FBailMoney,FCatType,FAttrClsCode,FInvestType,FEffectiveRate)" +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for (i = 0; i < this.addList.size(); i++) {

                securitystorage = (SecurityStorageBean) addList.get(i);
                yssPst.setString(1, securitystorage.getStrSecurityCode());
                yssPst.setString(2,
                              YssFun.formatDate(securitystorage.getStrStorageDate(),
                                                "yyyyMM"));

                yssPst.setDate(3,
                            YssFun.toSqlDate(YssFun.toDate(securitystorage.getStrStorageDate())));
                yssPst.setString(4, securitystorage.getStrPortCode());
                yssPst.setString(5, securitystorage.getStrCuryCode());
                yssPst.setDouble(6,
                              YssFun.toDouble(securitystorage.getStrStorageAmount()));
                yssPst.setDouble(7, YssFun.toDouble(securitystorage.getStrStorageCost()));
                yssPst.setDouble(8, YssFun.toDouble(securitystorage.getStrMStorageCost()));
                yssPst.setDouble(9, YssFun.toDouble(securitystorage.getStrVStorageCost()));
                yssPst.setDouble(10,
                              YssFun.toDouble(securitystorage.getStrFreezeAmount()));
                yssPst.setDouble(11,
                              YssFun.toDouble(securitystorage.getStrBaseCuryRate()));
                yssPst.setDouble(12,
                              YssFun.toDouble(securitystorage.getStrBaseCuryCost()));
                yssPst.setDouble(13,
                              YssFun.toDouble(securitystorage.getStrMBaseCuryCost()));
                yssPst.setDouble(14,
                              YssFun.toDouble(securitystorage.getStrVBaseCuryCost()));

                yssPst.setDouble(15,
                              YssFun.toDouble(securitystorage.getStrPortCuryRate()));
                yssPst.setDouble(16,
                              YssFun.toDouble(securitystorage.getStrPortCuryCost()));
                yssPst.setDouble(17,
                              YssFun.toDouble(securitystorage.getStrMPortCuryCost()));
                yssPst.setDouble(18,
                              YssFun.toDouble(securitystorage.getStrVPortCuryCost()));
                yssPst.setString(19, securitystorage.getStrFAnalysisCode1());
                yssPst.setString(20, securitystorage.getStrFAnalysisCode2());
                yssPst.setString(21, securitystorage.getStrFAnalysisCode3());
                yssPst.setInt(22, 0);
                // pst.setInt(23, 0); //库存状态 ： 0－自动计算（未锁定）
                yssPst.setInt(23, securitystorage.getIntStorageState()); //库存状态
                // pst.setInt(24, 1);
                yssPst.setInt(24, securitystorage.checkStateId);
                yssPst.setString(25, pub.getUserCode());
                yssPst.setString(26, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(27, pub.getUserCode());
                yssPst.setString(28, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setDouble(29, securitystorage.getBailMoney());
                yssPst.setString(30, (securitystorage.getCatType() == null || securitystorage.getCatType().length() == 0) ? " " : securitystorage.getCatType()); //sj edit 20071204 逻辑有误
                yssPst.setString(31, (securitystorage.getAttrCode() == null || securitystorage.getAttrCode().length() == 0) ? " " : securitystorage.getAttrCode());
                yssPst.setString(32, (securitystorage.getInvestType() == null || securitystorage.getInvestType().length() == 0) ? "C" : securitystorage.getInvestType()); //--- modify by wangzuochun 2010.05.11  MS01157    “投资类型”字段无法正常赋予默认值’C’导致库存统计报错    QDV4国内（测试）2010年05月05日04_B   
                yssPst.setDouble(33, securitystorage.getEffectiveRate()); //实际利率
                yssPst.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存证券库存金额时出现异常!" + "\n", e); //by 曹丞 2009.02.02 保存证券库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }
    }

    public ArrayList getSecBeans(String ports
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(null, null,
                           ports, "",
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", "", "","",""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           "", "",
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", "", "","",""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate,
                                 String ports
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           ports, "",
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", "", "","",""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate,
                                 String ports, String security
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           ports, "",
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", security, "", "", ""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate, java.util.Date dEndDate,
                                 String ports, String security
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dEndDate,
                           ports, "",
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", security, "", "", ""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate,
                                 String ports, String invMgr,
                                 String security
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           ports, invMgr,
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           "", security, "", "", ""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate,
                                 String ports, String invMgr,
                                 String broker, String security
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           ports, invMgr,
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           broker, security, "", "", ""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate,
                                 String ports, String invMgr,
                                 String broker, String security, String cury
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(dStartDate, dStartDate,
                           ports, invMgr,
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           broker, security, cury, "", ""
            );
    }

    public ArrayList getSecBeans(
        String ports, String invMgr,
        String broker, String security, String cury
        ) throws YssException {
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        return getSecBeans(null, null,
                           ports, invMgr,
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           broker, security, cury, "", ""
            );
    }

    public ArrayList getSecBeans(java.util.Date dStartDate, java.util.Date dEndDate,
                                 String ports, String invMgr,
                                 String broker, String security
        ) throws YssException {
        return getSecBeans(dStartDate, dEndDate,
                           ports, invMgr,
                           //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B
                           broker, security, "", "", ""
            );
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * @param dStartDate Date
     * @param dEndDate Date
     * @param ports String
     * @param invMgr String
     * @param broker String
     * @param security String
     * @param cury String
     * @param sAttrClsCode String
     * @return ArrayList
     * @throws YssException
     */
    public ArrayList getSecBeans(java.util.Date dStartDate, java.util.Date dEndDate,
                                 String ports, String invMgr,
                                 //edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 添加投资类型
                                 String broker, String security, String cury, String sAttrClsCode, String investType
        ) throws YssException {
        String strSql = "";
        SecurityStorageBean securitystorage = null;
        ResultSet rs = null;
        int i = 0;
        String sWhereSql = "";
        ArrayList reArr = new ArrayList();
        try {
        	//edit by songjie 2011.05.18 BUG 1936 QDV4赢时胜(测试)2011年5月16日01_B 添加投资类型
            sWhereSql = this.buildWhereSql(dStartDate, dEndDate, ports, invMgr, broker, security, cury, sAttrClsCode, investType);
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return null;
            }
            strSql = "select FSecurityCode,FCuryCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth,FAttrClsCode," +
                "sum(" + dbl.sqlIsNull("FStorageAmount", "0") + ") as FStorageAmount," +
                "sum(" + dbl.sqlIsNull("FStorageCost", "0") + ") as FStorageCost," +
                "sum(" + dbl.sqlIsNull("FMStorageCost", "0") + ") as FMStorageCost," +
                "sum(" + dbl.sqlIsNull("FVStorageCost", "0") + ") as FVStorageCost," +
                "sum(FFreezeAmount) as FFreezeAmount,sum(FPortCuryCost) as FPortCuryCost," +
                "sum(FMPortCuryCost) as FMPortCuryCost,sum(FVPortCuryCost) as FVPortCuryCost,sum(FBaseCuryCost) as FBaseCuryCost," +
                "sum(FMBaseCuryCost)as FMBaseCuryCost,sum(FVBaseCuryCost) as FVBaseCuryCost," +
                "sum(" + dbl.sqlIsNull("FBailMoney", "0") + ") as FBailMoney " +
                " from "
                + pub.yssGetTableName("Tb_Stock_Security") + " " +
                sWhereSql +
                " group by FSecurityCode,FCuryCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth,FAttrClsCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                securitystorage = new SecurityStorageBean();
                securitystorage.setStrSecurityCode(rs.getString("FSecurityCode"));
                securitystorage.setStrFreezeAmount(rs.getString("FFreezeAmount"));
                securitystorage.setStrMStorageCost(rs.getString("FMStorageCost"));
                securitystorage.setStrFAnalysisCode1(rs.getString("FAnalysisCode1"));
                securitystorage.setStrFAnalysisCode2(rs.getString("FAnalysisCode2"));
                securitystorage.setStrFAnalysisCode3(rs.getString("FAnalysisCode3"));
                securitystorage.setStrPortCode(rs.getString("FPortCode"));

                securitystorage.setStrPortCuryCost(rs.getDouble("FPortCuryCost") + "");
                securitystorage.setStrMPortCuryCost(rs.getDouble("FMPortCuryCost") + "");
                securitystorage.setStrVPortCuryCost(rs.getDouble("FVPortCuryCost") + "");
                securitystorage.setStrBaseCuryCost(rs.getDouble("FBaseCuryCost") + "");
                securitystorage.setStrMBaseCuryCost(rs.getDouble("FMBaseCuryCost") + "");
                securitystorage.setStrVBaseCuryCost(rs.getDouble("FVBaseCuryCost") + "");
                securitystorage.setStrStorageAmount(rs.getDouble("FStorageAmount") + "");
                securitystorage.setStrStorageCost(rs.getDouble("FStorageCost") + "");
                securitystorage.setStrCuryCode(rs.getString("FCuryCode"));
                securitystorage.setStrStorageDate(YssFun.formatDate(rs.getDate("FStorageDate")));
                securitystorage.setStrYearMonth(rs.getString("FYearMonth"));
                securitystorage.setAttrCode(rs.getString("FAttrClsCode"));

                reArr.add(securitystorage);
            }
            return reArr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
