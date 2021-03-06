package com.yss.manager;

import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;
import java.sql.*;
import java.util.*;

public class SecRecPayStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public SecRecPayStorageAdmin() {  
    }

    public void addList(SecRecPayBalBean secrecpaybal) {
        this.addList.add(secrecpaybal);
    }

	/** 合并太平版本代码
    * 重载 insert方法，添加证券代码作为删除条件 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    * @param dStartDate
    * @param dEndDate
    * @param ports
    * @param security
    * @throws YssException
    */
   	public void insert(java.util.Date dStartDate,java.util.Date dEndDate,
			   			String ports,String security) throws YssException{
	   	this.insert(dStartDate, dEndDate, "","",ports,"","",security,"");
  	}
   
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports
        ) throws
        YssException {
         this.insert(dStartDate, dEndDate, "", "", ports, "", "", "", "");
        
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String tsfType,
                       String subTsfType, String ports, String invMgr,
                       String broker, String security, String cury
        ) throws
        YssException {
        String strSql = "";
        SecRecPayBalBean secrecpaybal = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        int i = 0;
        try {
            delete(dStartDate, dEndDate, tsfType, subTsfType, ports, invMgr,
                   broker, security, cury);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3," +
                " FSecurityCode, FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FMBal, FVBal, " +
                " FBaseCuryBal, FMBaseCuryBal, FVBaseCuryBal, FPortCuryBal, FMPortCuryBal, FVPortCuryBal, " +
                " FBalF, FBaseCuryBalF, FPortCuryBalF," + //2008.11.13 蒋锦 添加 保留8位小数的原币、基础货币、本位币 编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                " FStorageInd, FCatType,FAttrClsCode," +
                //MS00024 交易数据拆分 QDV4.1赢时胜（上海）2009年4月20日24_A 2009.08.21 蒋锦添加投资类型字段
                " FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime, FInvestType," +
                //modify  by zhangfa 20101124 证券借贷库存统计
                " FAmount "+
                
                ")" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                //-------end 证券借贷库存统计------------------ 
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                secrecpaybal = (SecRecPayBalBean) addList.get(i);
                /** add by zhaoxianlin 20121107 STORY #3208 银华基金：卖空业务  借入估增、借入成本存入借贷关联表中，不入净值统计 */
                if(secrecpaybal.getSTsfTypeCode().equals("10")||secrecpaybal.getSTsfTypeCode().equals("60")){
                  	 continue;
                   }
                /** -----end----- */
                yssPst.setString(1,
                              YssFun.formatDate(secrecpaybal.getDtStorageDate(),
                                                "yyyyMM"));
                yssPst.setDate(2, YssFun.toSqlDate(secrecpaybal.getDtStorageDate()));
                yssPst.setString(3, secrecpaybal.getSPortCode());
                yssPst.setString(4, secrecpaybal.getSAnalysisCode1());
                yssPst.setString(5, secrecpaybal.getSAnalysisCode2());
                yssPst.setString(6, secrecpaybal.getSAnalysisCode3());
                yssPst.setString(7, secrecpaybal.getSSecurityCode());
                yssPst.setString(8, secrecpaybal.getSTsfTypeCode());
                yssPst.setString(9, secrecpaybal.getSSubTsfTypeCode());
                yssPst.setString(10, secrecpaybal.getSCuryCode());
                yssPst.setDouble(11, secrecpaybal.getDBal());
                yssPst.setDouble(12, secrecpaybal.getDMBal());
                yssPst.setDouble(13, secrecpaybal.getDVBal());
                yssPst.setDouble(14, secrecpaybal.getDBaseBal());
                yssPst.setDouble(15, secrecpaybal.getDMBaseBal());
                yssPst.setDouble(16, secrecpaybal.getDVBaseBal());
                yssPst.setDouble(17, secrecpaybal.getDPortBal());
                yssPst.setDouble(18, secrecpaybal.getDMPortBal());
                yssPst.setDouble(19, secrecpaybal.getDVPortBal());
                //-----------2008.11.13 蒋锦 添加-------------//
                //储存保留8位小数的原币，基础货币，本位币金额
                //编号：MS00002 文档：《QDV4华夏2008年11月04日01_B》
                yssPst.setDouble(20, secrecpaybal.getBalF());
                yssPst.setDouble(21, secrecpaybal.getBaseBalF());
                yssPst.setDouble(22, secrecpaybal.getPortBalF());
                //-------------------------------------------//
                yssPst.setInt(23, 0);
                yssPst.setString(24,
                              secrecpaybal.getCatTypeCode().length() > 0 ?
                              secrecpaybal.getCatTypeCode() : " ");
                yssPst.setString(25,
                              secrecpaybal.getAttrClsCode().length() > 0 ?
                              secrecpaybal.getAttrClsCode() : " ");
                yssPst.setInt(26, 1);
                yssPst.setString(27, pub.getUserCode());
                yssPst.setString(28, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(29, pub.getUserCode());
                yssPst.setString(30, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(31, secrecpaybal.getSInvestType());
              //add  by zhangfa 20101124 证券借贷库存统计
                yssPst.setDouble(32, secrecpaybal.getAmount());
              //----------end   证券借贷库存统计------------
                yssPst.executeUpdate();

            }
//        conn.commit();
//        bTrans = false;
//        conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存证券应收应付库存金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 证券应收应付库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }
    }
	// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06 合并太平版本代码---------
	public void updateAvgRate( SecRecPayBalBean secrecpaybal,double basemoney,double portmoney)throws YssException {
		Connection conn = dbl.loadConnection();
		String strSql = "";
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
		try { 
			strSql = "update "
					+ pub.yssGetTableName("tb_data_secrecpay")
					+ " set fbasecuryrate=?,fportcuryrate=? , fbasecurymoney=?, fmbasecurymoney=? ,fvbasecurymoney=? , fportcurymoney=?, fmportcurymoney=? , fvportcurymoney=?"
					+ " where fportcode=? and fanalysiscode1=? and fanalysiscode2=? and FSecurityCode=? and FTsfTypeCode='02' and ftransdate =? and FInvestType =? and fcheckstate=1"; //调整，添加投资类型 合并太平版本代码
//			pst = conn.prepareStatement(strSql);
			
			yssPst = dbl.getYssPreparedStatement(strSql);
			
			yssPst.setDouble(1, secrecpaybal.getDBaseRate());
			yssPst.setDouble(2, secrecpaybal.getDPortRate());
			yssPst.setDouble(3, basemoney);
			yssPst.setDouble(4, basemoney);
			yssPst.setDouble(5, basemoney);
			yssPst.setDouble(6, portmoney);
			yssPst.setDouble(7, portmoney);
			yssPst.setDouble(8, portmoney);
			
			yssPst.setString(9, secrecpaybal.getSPortCode());
			yssPst.setString(10, secrecpaybal.getSAnalysisCode1());
			yssPst.setString(11, secrecpaybal.getSAnalysisCode2());
			yssPst.setString(12, secrecpaybal.getSSecurityCode());
			yssPst.setDate(13, YssFun.toSqlDate(secrecpaybal.getDtStorageDate()));
			yssPst.setString(14, secrecpaybal.getSInvestType());//添加投资类型代码，合并太平版本时调整

			yssPst.executeUpdate();
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("【更新證券應收應付匯率出錯】！" + "\n", e);
		} finally {
			dbl.closeStatementFinal(yssPst);
			conn = null;
		}
	}
    //删除
    public void delete(java.util.Date dStartDate, java.util.Date dEndDate,
                       String tsfType, String subTsfType, String ports,
                       String invMgr,
                       String broker, String security, String cury
        ) throws YssException {
        String strSql = "";
        // ResultSet rs = null;
        String sWhereSql = "";
        PreparedStatement pst = null;
        try {
            sWhereSql = this.buildWhereSql(dStartDate, dEndDate, tsfType,
                                           subTsfType, ports, invMgr, broker,
                                           security, cury, false, "", ""); //false是不为期初数的意思。
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return;
            }
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_SecRecPay") +
                sWhereSql;
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
    }

    /**
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.09.05 蒋锦 添加 投资类型 MS00656  QDV4赢时胜(上海)2009年8月24日01_A
     * @param dStartDate Date
     * @param dEndDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param ports String
     * @param invMgr String
     * @param broker String
     * @param security String
     * @param cury String
     * @param bIsQCData boolean
     * @return String
     * @throws YssException
     */
    private String buildWhereSql(java.util.Date dStartDate,
                                 java.util.Date dEndDate,
                                 String tsfType, String subTsfType,
                                 String ports, String invMgr,
                                 String broker, String security, String cury,
                                 boolean bIsQCData,
                                 String sAttrClsCode,
                                 String sInvestType) throws YssException {
        String sResult = " where 1=1 ";
        if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
            if (bIsQCData) {
                //当为一年的第一天时应该取期初数 by leeyu 080709
            	//fanghaoln 201006 MS00901 QDV4华夏2010年01月06日01_B 
                sResult = sResult + " and FYearMonth='" + YssFun.getYear(dStartDate) + "00'";
                //-----------------------------end --MS00901-----------------------------------------
            } else {
                sResult = sResult + " and FYearMonth<>'" +
                    new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
            }
        }
        if (dStartDate != null && dEndDate != null) {
        	//fanghaoln 201006 MS00901 QDV4华夏2010年01月06日01_B 
            //当为一年的第一天时应该取期初数 by leeyu 080709
//            if (bIsQCData) {
//                sResult += " and FStorageDate  between " +
//                    dbl.sqlDate(YssFun.addDay(dStartDate, 1)) + " and " + dbl.sqlDate(YssFun.addDay(dEndDate, 1));
//            } else {
                sResult += " and FStorageDate  between " +
                    dbl.sqlDate(dStartDate) + " and " + dbl.sqlDate(dEndDate);
            //}
            //-----------------------------end --MS00901-----------------------------------------
        }
		/**shashijie 2012-7-2 STORY 2475 */
        if (ports != null && ports.length() > 0) {
            sResult += " and FPortCode in (" + operSql.sqlCodes(ports) + ")";
        }
        if (invMgr != null && invMgr.length() > 0) {
            sResult += " and FAnalysisCode1 =" + dbl.sqlString(invMgr);
        }
        if (broker != null && broker.length() > 0) {
            sResult += " and FAnalysisCode2 =" + dbl.sqlString(broker);
        }
        if (security != null && security.length() > 0) {
    	 	if(security.indexOf(",")>-1)//添加对多证券代码作为条件的处理 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    	 		/**Start 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B
    	 		* 对一个很大in语句的参数进行拆分，拆分成多个OR IN语句。避免ORA-01795错误*/
//    		 	sResult += " and FSecurityCode in(" + operSql.sqlCodes(security) +")";//更改为多个证券代码 by leeyu 20100330 QDV4中保2010年03月03日03_A MS1011
    	 		sResult += " and (" + operSql.getNumsDetail(security,"FSecurityCode",500) + ")";
    	 	/**End 20140123.Bug #87919.QDV4赢时胜(上海)2014年01月20日01_B*/
    	 	else
            	sResult += " and FSecurityCode =" + dbl.sqlString(security);
        }
        if (cury != null && cury.length() > 0) {
            sResult += " and FCuryCode =" + dbl.sqlString(cury);
        }
        if (tsfType != null && tsfType.length() > 0) {
            sResult += " and FTsfTypeCode in (" + operSql.sqlCodes(tsfType) + ")";
        }
        if (subTsfType != null && subTsfType.length() > 0) {
            sResult += " and FSubTsfTypeCode in (" + operSql.sqlCodes(subTsfType) +
                ")";
        }
		/**end*/
        //2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
        if (sAttrClsCode != null && sAttrClsCode.length() > 0){
            sResult += " AND FAttrClsCode = " + dbl.sqlString(sAttrClsCode);
        }
        //2009.09.05 蒋锦 添加 投资类型 MS00656  QDV4赢时胜(上海)2009年8月24日01_A
        if (sInvestType != null && sInvestType.length() > 0){
            sResult += " AND FInvestType = " + dbl.sqlString(sInvestType);
        }
        return sResult;
    }

    public ArrayList getSecRecBeans(String ports
        ) throws YssException {
        return getSecRecBeans(null, null, "",
                              "", ports, "", "", "", "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              "", "", "", "",
                              "", "", "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String ports
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              "", "", ports, "",
                              "", "", "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String ports, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              "", "", ports, "",
                              "", security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    java.util.Date dEndDate,
                                    String ports, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dEndDate,
                              "", "", ports, "",
                              "", security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String ports, String invMgr,
                                    String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              "", "", ports, invMgr,
                              "", security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String ports, String invMgr,
                                    String broker, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              "", "", ports, invMgr,
                              broker, security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String tsfType,
                                    String ports, String invMgr,
                                    String broker, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              tsfType, "", ports, invMgr,
                              broker, security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    String tsfType, String subTsfType,
                                    String ports, String invMgr,
                                    String broker, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dStartDate,
                              tsfType, subTsfType, ports, invMgr,
                              broker, security, "", false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    java.util.Date dEndDate,
                                    String tsfType, String subTsfType,
                                    String ports, String invMgr,
                                    String broker, String security
        ) throws YssException {
        return getSecRecBeans(dStartDate, dEndDate,
                              tsfType, subTsfType, ports, invMgr,
                              broker, security, "", false
            );
    }

    public ArrayList getSecRecBeans(
        String tsfType, String subTsfType,
        String ports, String invMgr,
        String broker, String security, String cury
        ) throws YssException {
        return getSecRecBeans(null, null,
                              tsfType, subTsfType, ports, invMgr,
                              broker, security, cury, false
            );
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    java.util.Date dEndDate, String tsfType,
                                    String subTsfType, String ports,
                                    String invMgr,
                                    String broker, String security, String cury
        ) throws YssException {
        return getSecRecBeans(dStartDate, dEndDate, tsfType, subTsfType, ports,
                              invMgr, broker, security, cury, false);
    }

    public ArrayList getSecRecBeans(java.util.Date dStartDate,
                                    java.util.Date dEndDate, String tsfType,
                                    String subTsfType, String ports,
                                    String invMgr,
                                    String broker, String security, String cury,
                                    boolean bIsQCData) throws //增加对期初数的处理
        YssException {
        String strSql = "";
        SecRecPayBalBean secRecstorage = null;
        ResultSet rs = null;
        int i = 0;
        String sWhereSql = "";
        ArrayList reArr = new ArrayList();
        try {
            sWhereSql = this.buildWhereSql(dStartDate, dEndDate, tsfType,
                                           subTsfType, ports, invMgr, broker,
                                           security, cury, bIsQCData, "", "");
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return null;
            }
            strSql = "select FSecurityCode,FCuryCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth," +
                "FTsfTypeCode,FSubTsfTypeCode," +
                "sum(" + dbl.sqlIsNull("FBal", "0") + ") as FBal," +
                "sum(" + dbl.sqlIsNull("FMBal", "0") + ") as FMBal," +
                "sum(" + dbl.sqlIsNull("FVBal", "0") + ") as FVBal," +
                "sum(FPortCuryBal) as FPortCuryBal,sum(FMPortCuryBal) as FMPortCuryBal," +
                "sum(FVPortCuryBal) as FVPortCuryBal,sum(FBaseCuryBal) as FBaseCuryBal,sum(FMBaseCuryBal) as FMBaseCuryBal," +
                "sum(FVBaseCuryBal)as FVBaseCuryBal" +
                " from "
                + pub.yssGetTableName("Tb_Stock_SecRecPay") + " " +
                sWhereSql +
                " group by FSecurityCode,FCuryCode,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3," +
                "FStorageDate,FYearMonth,FTsfTypeCode,FSubTsfTypeCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                secRecstorage = new SecRecPayBalBean();
                secRecstorage.setSSecurityCode(rs.getString("FSecurityCode"));
                secRecstorage.setSAnalysisCode1(rs.getString("FAnalysisCode1"));
                secRecstorage.setSAnalysisCode2(rs.getString("FAnalysisCode2"));
                secRecstorage.setSAnalysisCode3(rs.getString("FAnalysisCode3"));
                secRecstorage.setSPortCode(rs.getString("FPortCode"));
                secRecstorage.setDBal(rs.getDouble("FBal"));
                secRecstorage.setDMBal(rs.getDouble("FMBal"));
                secRecstorage.setDVBal(rs.getDouble("FVBal"));
                secRecstorage.setDPortBal(rs.getDouble("FPortCuryBal"));
                secRecstorage.setDMPortBal(rs.getDouble("FMPortCuryBal"));
                secRecstorage.setDVPortBal(rs.getDouble("FVPortCuryBal"));
                secRecstorage.setDBaseBal(rs.getDouble("FBaseCuryBal"));
                secRecstorage.setDMBaseBal(rs.getDouble("FMBaseCuryBal"));
                secRecstorage.setDVBaseBal(rs.getDouble("FVBaseCuryBal"));
                secRecstorage.setDtStorageDate(rs.getDate("FStorageDate"));
                secRecstorage.setSYearMonth(rs.getString("FYearMonth"));
                reArr.add(secRecstorage);
            }
            return reArr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 获取封装了债券应收应付库存数据的bean
     * MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化
     * 2009.08.10 蒋锦 添加 属性分类字段 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
     * 2009.09.05 蒋锦 添加 投资类型
     * author : sunkey
     * date   : 20090425
     * @param dStartDate Date    开始日期
     * @param dEndDate Date      结束日期
     * @param tsfType String     调拨类型
     * @param subTsfType String  调拨子类型
     * @param ports String       组合代码，目前传入的还是一个，但支持传入多个
     * @param invMgr String      投资经理
     * @param broker String      券商
     * @param security String    证券代码
     * @param cury String        币种
     * @param bIsQCData boolean 是否为期初数
     * @param sAttrClsCode String 属性分类
     * @param sInvestType String 投资类型
     * @return ArrayList         存放了证券应收应付库存数据的bean集合
     * @throws YssException
     */
    public ArrayList getSecRecBeans(String security, java.util.Date dStartDate,
                                    java.util.Date dEndDate, String tsfType,
                                    String subTsfType,
                                    String ports, String invMgr, String broker,
                                    String cury,
                                    boolean bIsQCData,
                                    String sAttrClsCode,
                                    String sInvestType) throws YssException {

        String strSql = null; //存储SQL语句
        String[] port = ports.split(","); //存储ports中对应的每个port
        ResultSet rs = null; //结果集
        String sWhereSql = null; //存储sql语句的where条件语句
        ArrayList reArr = new ArrayList(); //存放债券应收应付
        SecRecPayBalBean secRecstorage = null; //存放应收应付数据，保存到reArr中
        HashMap tmpMap = null; //存储证券应收应付库存对应的SecRecPayBalBean

        //判断是否存在用户代码+组合群的key，如果有则将数据取出，暂不考虑跨组合群处理的情况
        if (YssGlobal.hmSecRecBeans.containsKey(pub.getUserCode() + pub.getAssetGroupCode())) {
            tmpMap = (HashMap) YssGlobal.hmSecRecBeans.get(pub.getUserCode() + pub.getAssetGroupCode());
        } else {
            tmpMap = new HashMap();
        }

        //首先判断要取的数据在HashMap中是否存在，如果存在直接组装就OK了，不必再次查询数据库
        if (tmpMap.size() > 0) {
            for (java.util.Date tmpDate = dStartDate; YssFun.dateDiff(tmpDate, dEndDate) >= 0; tmpDate = YssFun.addDay(tmpDate, 1)) {
                //要通过组合逐一查找
                for (int i = 0; i < port.length; i++) {
                    //如果哈希表中存储了债券的相关利息数据，则直接取出放到arraylist中
                    if (tmpMap.containsKey(security + YssFun.formatDate(tmpDate, "yyyyMMdd") +
                                           tsfType + subTsfType + port[i] + invMgr + broker + sAttrClsCode + sInvestType)) {
                        reArr.add(tmpMap.get(security + YssFun.formatDate(tmpDate, "yyyyMMdd") +
                                             tsfType + subTsfType + port[i] + invMgr + broker + sAttrClsCode + sInvestType));
                    }
                }
            }
        }
        //如果获取到了债券的应收应付库存数据，则直接将数据返回，不必继续操作
        if (reArr.size() > 0) {
            return reArr;
        }

        //根据传入的参数生成对应的SQL条件语句，证券代码、组合不作为筛选条件，因为要查询所有组合证券应收应付数据
        //证券代码和组合一次只传入一次，用来做哈希表中取数的key值的一部分
        sWhereSql = this.buildWhereSql(dStartDate, dEndDate, tsfType,
                                       subTsfType, "", invMgr,
                                       broker, "", cury, bIsQCData, sAttrClsCode, sInvestType);

        //如果生成的条件语句为“”或只有1=1时，直接返回null
        if (sWhereSql.trim().equals("") || sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
            return null;
        }

        //查询出证券应收应付库存数据
        strSql = "select FSecurityCode,FCuryCode,FPortCode,FAnalysisCode1," +
            "FAnalysisCode2,FAnalysisCode3,FStorageDate,FYearMonth," +
            "FTsfTypeCode,FSubTsfTypeCode, FAttrClsCode, " +
            dbl.sqlIsNull("FBal", "0") + " as FBal," +
            dbl.sqlIsNull("FMBal", "0") + " as FMBal," +
            dbl.sqlIsNull("FVBal", "0") + " as FVBal," +
            "FPortCuryBal,FMPortCuryBal," +
            "FVPortCuryBal,FBaseCuryBal,FMBaseCuryBal," +
            "FVBaseCuryBal" +
            " from " + pub.yssGetTableName("Tb_Stock_SecRecPay") + " " + sWhereSql;
        try {
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //将查询到的数据进行封装，并将对象保存到ArrayList中去
                secRecstorage = new SecRecPayBalBean();
                secRecstorage.setSSecurityCode(rs.getString("FSecurityCode")); //证券代码
                secRecstorage.setSAnalysisCode1(rs.getString("FAnalysisCode1")); //分析代码1
                secRecstorage.setSAnalysisCode2(rs.getString("FAnalysisCode2")); //分析代码2
                secRecstorage.setSAnalysisCode3(rs.getString("FAnalysisCode3")); //分析代码3
                secRecstorage.setSPortCode(rs.getString("FPortCode")); //组合代码
                secRecstorage.setDBal(rs.getDouble("FBal")); //原币余额
                secRecstorage.setDMBal(rs.getDouble("FMBal")); //原币管理余额
                secRecstorage.setDVBal(rs.getDouble("FVBal")); //运笔估值余额
                secRecstorage.setDPortBal(rs.getDouble("FPortCuryBal")); //组合货币余额
                secRecstorage.setDMPortBal(rs.getDouble("FMPortCuryBal")); //组合货币管理余额
                secRecstorage.setDVPortBal(rs.getDouble("FVPortCuryBal")); //组合货币估值余额
                secRecstorage.setDBaseBal(rs.getDouble("FBaseCuryBal")); //基础货币余额
                secRecstorage.setDMBaseBal(rs.getDouble("FMBaseCuryBal")); //基础货币管理余额
                secRecstorage.setDVBaseBal(rs.getDouble("FVBaseCuryBal")); //基础货币估值余额
                secRecstorage.setDtStorageDate(rs.getDate("FStorageDate")); //库存日期
                secRecstorage.setSYearMonth(rs.getString("FYearMonth")); //库存年月
                secRecstorage.setAttrClsCode(rs.getString("FAttrClsCode"));  //属性分类

                //将数据存放到HashTable表中,键：证券代码+库存日期+调拨类型+调拨子类型+组合代码+投资经理+券商
                tmpMap.put(rs.getString("FSecurityCode") +
                           YssFun.formatDate(rs.getDate("FStorageDate"), "yyyyMMdd") +
                           rs.getString("FTsfTypeCode") +
                           rs.getString("FSubTsfTypeCode")
                           + rs.getString("FPortCode") + invMgr + broker + sAttrClsCode + sInvestType,
                           secRecstorage);
            } // =======end while

            //将用户+组合的证券应收应付库存数据哈希表存放到全局变量，暂不考虑跨组合群
            YssGlobal.hmSecRecBeans.put(pub.getUserCode() + pub.getAssetGroupCode(), tmpMap);

            //因为上面会查询出所有数据，因此要进行过滤筛选出符合条件的信息返回
            if (tmpMap.size() > 0) {
                for (java.util.Date tmpDate = dStartDate;
                     YssFun.dateDiff(tmpDate, dEndDate) >= 0;
                     tmpDate = YssFun.addDay(tmpDate, 1)) {
                    //要通过组合逐一查找
                    for (int i = 0; i < port.length; i++) {
                        if (tmpMap.containsKey(security +
                                               YssFun.formatDate(tmpDate, "yyyyMMdd") +
                                               tsfType + subTsfType + port[i] + invMgr +
                                               broker + sAttrClsCode + sInvestType)) {
                            reArr.add(tmpMap.get(security +
                                                 YssFun.formatDate(tmpDate, "yyyyMMdd") +
                                                 tsfType + subTsfType + port[i] +
                                                 invMgr + broker + sAttrClsCode + sInvestType));
                        }
                    }
                }
            } //==End if
        } catch (Exception e) {
            throw new YssException("获取证券应收应付款库存出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs); //close the resultset at last
        }
        return reArr;
    } // End Method getSecRecBeans
}
