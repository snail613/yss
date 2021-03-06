package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.storagemanage.*;
import com.yss.util.*;

public class CashPayRecStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    /* start add by huangqirong 2013-04-17 bug #7545*/
    public ArrayList getAddList() {
		return addList;
	}

	public void setAddList(ArrayList addList) {
		this.addList = addList;
	}
	/* end add by huangqirong 2013-04-17 bug #7545*/
    public CashPayRecStorageAdmin() {
    }

    public void addList(CashRecPayBalBean cashrecpaybal) {
        this.addList.add(cashrecpaybal);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports
        ) throws YssException {
        this.insert(dStartDate, dEndDate, "", "", ports, "", "", "", "","");
    }

    /**
     * add by huangqirong 2013-04-15 bug #7545 增加选中的现金账户
     * */
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate, String ports , String cashAcc) throws YssException {
    	this.insert(dStartDate, dEndDate, "", "", ports, "", "", cashAcc, "","");
	}
    
    /*************************************************************************
     *  NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
     * @param dStartDate
     * @param dEndDate
     * @param tsfType
     * @param subTsfType
     * @param ports
     * @param invMgr
     * @param cat
     * @param cashacc
     * @param cury
     * @param sAttrClsCode
     * @throws YssException
     */
    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String tsfType,
                       String subTsfType, String ports, String invMgr,
                       String cat, String cashacc, String cury,String sAttrClsCode
        ) throws
        YssException {
        String strSql = "";
        CashRecPayBalBean cashrecpaybal = null;
        
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
//    boolean bTrans =false;
        int i = 0;
        try {
//       conn.setAutoCommit(false);
//       bTrans=true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) +
                ( (tsfType == null || tsfType.length() == 0) ? " " :
                	" and FTsfTypeCode in (" + operSql.sqlCodes(tsfType) + ")") + //" and FTsfTypeCode = " + dbl.sqlString(tsfType)) +  //modify huangqirong 2013-04-17 bug #7545
                ( (subTsfType == null || subTsfType.length() == 0) ? " " :
                	" and FSubTsfTypeCode in ( " + operSql.sqlCodes(subTsfType) +")" ) +//" and FSubTsfTypeCode = " +	//modify huangqirong 2013-04-17 bug #7545
                 
                ( (ports == null || ports.length() == 0) ? " " :
                 " and FPortCode in (" + ports + ")") +
                ( (invMgr == null || invMgr.length() == 0) ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                ( (cat == null || cat.length() == 0) ? " " :
                 " and FAnalysisCode2 = " +
                 dbl.sqlString(cat)) +
                ( (cashacc == null || cashacc.length() == 0) ? " " :
                 " and FCashAccCode in ( " + operSql.sqlCodes(cashacc) + ")" )+  //modify by huangqirong 2013-04-15 bug #7545 选中的现金账户
                ( (cury == null || cury.length() == 0) ? " " :
                 " and FCuryCode = " + dbl.sqlString(cury))+
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
               (sAttrClsCode.trim().length()==0?"":" and FAttrClsCode="+dbl.sqlString(sAttrClsCode));
            //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
//               " and FStorageInd <> 2 ";//MS00308 QDV4赢时胜上海2009年3月11日01_B 将所有状态的库存数据都删除 modify by shenjie
            if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
                strSql = strSql + " and FYearMonth<>'" +
                    new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
            }
            
            dbl.executeSql(strSql);
            strSql = "insert into " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                "(FYearMonth, FStorageDate, FPortCode, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3," +
                " FCashAccCode, FTsfTypeCode, FSubTsfTypeCode, FCuryCode, FBal, FBaseCuryBal, FPortCuryBal, " +
                " FStorageInd, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,FAttrClsCode" +//NO.125 用户需要对组合按资本类别进行子组合的分类
                ", FIsDif" + // 444 QDV4汇添富2010年12月21日01_A 20110126 增加尾差标识字段，用于标识尾差调整数据 
                ")" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                cashrecpaybal = (CashRecPayBalBean) addList.get(i);

                yssPst.setString(1,
                              YssFun.formatDate(cashrecpaybal.getDtStorageDate(),
                                                "yyyyMM"));
                yssPst.setDate(2, YssFun.toSqlDate(cashrecpaybal.getDtStorageDate()));
                yssPst.setString(3, cashrecpaybal.getSPortCode());
                yssPst.setString(4, cashrecpaybal.getSAnalysisCode1());
                yssPst.setString(5, cashrecpaybal.getSAnalysisCode2());
                yssPst.setString(6, cashrecpaybal.getSAnalysisCode3());
                yssPst.setString(7, cashrecpaybal.getSCashAccCode());
                yssPst.setString(8, cashrecpaybal.getSTsfTypeCode());
                yssPst.setString(9, cashrecpaybal.getSSubTsfTypeCode());
                yssPst.setString(10, cashrecpaybal.getSCuryCode());
                yssPst.setDouble(11, cashrecpaybal.getDBal());
                yssPst.setDouble(12, cashrecpaybal.getDBaseBal());
                yssPst.setDouble(13, cashrecpaybal.getDPortBal());
                yssPst.setInt(14, 0);
                yssPst.setInt(15, 1);
                yssPst.setString(16, pub.getUserCode());
                yssPst.setString(17, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(18, pub.getUserCode());
                yssPst.setString(19, YssFun.formatDatetime(new java.util.Date()));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                yssPst.setString(20, (cashrecpaybal.getAttrClsCode().trim().length())!=0?cashrecpaybal.getAttrClsCode():" ");
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                // 444 QDV4汇添富2010年12月21日01_A 20110121 通过描述标识尾差数据
                yssPst.setString(21, cashrecpaybal.getsIsDif());
                // 444 QDV4汇添富2010年12月21日01_A 20110121
                
                yssPst.executeUpdate();

            }
//       conn.commit();
//       bTrans = false;
//       conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("系统保存现金应收应付库存金额时出现异常!" + "\n", e); //by 曹丞 2009.02.01 现金应收应付库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);
        }

    }
	// ----- QDV4中保2010年1月4日01_B add by jiangshichao 2010.01.06 合并太平版本代码---------
	public void updateAvgRate(CashRecPayBalBean cashrecpaybal,
			double basemoney, double portmoney) throws YssException {
		Connection conn = dbl.loadConnection();
		String strSql = "";
		PreparedStatement pst = null;
		try {

			// --------------------邵宏伟临时修改
			// 修改原因，在应收应付中是按分析代码分开的，但是外部传入的basemoney 和 portmoney
			// 是汇总数据，调整数据就会造成错误；
			// 修改方法，只使用传入的平均汇率进行更新，基础货币和组合货币采用原币和汇率进行运算；
			if (cashrecpaybal.getDBaseRate() == 0
					|| cashrecpaybal.getDPortRate() == 0) {
				return;
			}
			strSql = "update "
					+ pub.yssGetTableName("tb_data_cashpayrec")
					+ " set fbasecuryrate= "
					+ cashrecpaybal.getDBaseRate()
					+ ",fportcuryrate= "
					+ cashrecpaybal.getDPortRate()
					+ " , fbasecurymoney= round(Fmoney * "
					+ cashrecpaybal.getDBaseRate()
					+ " , 4)" //------ modify by wangzuochun 2010.10.26 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B）

					+ " ,fportcurymoney= round(Fmoney * "
					+ YssD.div(cashrecpaybal.getDBaseRate(), cashrecpaybal
							.getDPortRate())
					+ " , 4)"// 调整为用YssD.div方法  //------ modify by wangzuochun 2010.10.26 BUG #198 定存业务现金应收应付金额不对（QDV4太平2010年10月25日01_B）

					+ " where fportcode= "
					+ dbl.sqlString(cashrecpaybal.getSPortCode())
					+ " and fanalysiscode1= "
					+ dbl.sqlString(cashrecpaybal.getSAnalysisCode1())
					+ " and fanalysiscode2= "
					+ dbl.sqlString(cashrecpaybal.getSAnalysisCode2())
					+ " and FCashAccCode= "
					+ dbl.sqlString(cashrecpaybal.getSCashAccCode())
					+ " and FTsfTypeCode='02'   and ftransdate = "
					+ dbl.sqlDate(cashrecpaybal.getDtStorageDate())
					+ " and fcheckstate=1";
			dbl.executeSql(strSql);
			/*
			 * strSql = "update " + pub.yssGetTableName("tb_data_cashpayrec") +
			 * " set fbasecuryrate=?,fportcuryrate=? , fbasecurymoney=?, fportcurymoney=?"
			 * +
			 * " where fportcode=? and fanalysiscode1=? and fanalysiscode2=? and FCashAccCode=? and FTsfTypeCode='02'   and ftransdate =? and fcheckstate=1"
			 * ; pst = conn.prepareStatement(strSql); pst.setDouble(1,
			 * cashrecpaybal.getDBaseRate()); pst.setDouble(2,
			 * cashrecpaybal.getDPortRate()); pst.setDouble(3, basemoney);
			 * pst.setDouble(4, portmoney);
			 * 
			 * pst.setString(5, cashrecpaybal.getSPortCode()); pst.setString(6,
			 * cashrecpaybal.getSAnalysisCode1()); pst.setString(7,
			 * cashrecpaybal.getSAnalysisCode2()); pst.setString(8,
			 * cashrecpaybal.getSCashAccCode()); pst.setDate(9,
			 * YssFun.toSqlDate(cashrecpaybal.getDtStorageDate()));
			 * 
			 * pst.executeUpdate();
			 */
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("【更新現金應收應付匯率出錯】！" + "\n", e);
		} finally {
			dbl.closeStatementFinal(pst);
			conn = null;
		}
	}

}
