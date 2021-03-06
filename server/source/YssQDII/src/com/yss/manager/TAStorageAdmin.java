package com.yss.manager;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.EachRateOper;
import com.yss.dsub.*;
import com.yss.main.operdeal.valuation.LeverGradeFundCfg;
import com.yss.main.storagemanage.*;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.*;

public class TAStorageAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    public TAStorageAdmin() {
    }

    public void addList(TAStorageBean tastorage) {
        this.addList.add(tastorage);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, boolean bReCost
        ) throws YssException {
        insert(dStartDate, dEndDate, ports, "", "", "", "", bReCost);
    }

    public void insert(java.util.Date dStartDate, java.util.Date dEndDate,
                       String ports, String invMgr,
                       String broker, String SellNetCode, String cury,
                       boolean bReCost
        ) throws YssException {
        String strSql = "";
        TAStorageBean tastorage = null;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
//      boolean bTrans = false;
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        int i = 0;
        try {
//         conn.setAutoCommit(false);
//         bTrans = true;
            strSql = "delete from " + pub.yssGetTableName("Tb_Stock_TA") +
                " where FStorageDate between " + dbl.sqlDate(dStartDate) +
                " and " + dbl.sqlDate(dEndDate) +
                ( (ports == null || ports.length() == 0) ? " " :
                 " and FPortCode in (" + this.operSql.sqlCodes(ports) + ")") +
                ( (invMgr == null || invMgr.length() == 0) ? " " :
                 " and FAnalysisCode1 = " + dbl.sqlString(invMgr)) +
                ( (broker == null || broker.length() == 0) ? " " :
                 " and FAnalysisCode2 = " + dbl.sqlString(broker)) +
                ( (SellNetCode == null || SellNetCode.length() == 0) ? " " :
                 " and FSecurityCode = " +
                 dbl.sqlString(SellNetCode)) +
                ( (cury == null || cury.length() == 0) ? " " :
                 " and FCuryCode = " + dbl.sqlString(cury));
//               " and FStorageInd <> 2";//MS00308 QDV4赢时胜上海2009年3月11日01_B 将所有状态的库存数据都删除 modify by shenjie
            if (dStartDate != null && dEndDate != null && dStartDate.equals(dEndDate)) {
                strSql = strSql + " and FYearMonth<>'" +
                    new Integer(YssFun.getYear(dStartDate)).toString() + "00'";
            }
            dbl.executeSql(strSql);
//            pst = conn.prepareStatement(
//                "insert into " +
//                pub.yssGetTableName("Tb_Stock_TA") +
//                "(FPortClsCode, FYearMonth, FStorageDate, FPortCode, FCuryCode, FStorageAmount, FCost, FPortCuryCost," +
//                " FBaseCuryCost, FBaseCuryRate, FPortCuryRate, FCuryUnpl, FCuryPl, " +
//                " FPortCuryUnpl, FPortCuryPl, FBaseCuryUnpl, FBaseCurypl, FAnalysisCode1, FAnalysisCode2, " +
//                //xuqiji 20091111  MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
//                " FAnalysisCode3, FStorageInd, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,fconvertnum" +
//                /**shashijie 2011-10-31 STORY 1589 */
//                ",FPortStorageAmount " +
//                /**end*/
//                " )" +
//                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            yssPst = dbl.getYssPreparedStatement(
                "insert into " +
                pub.yssGetTableName("Tb_Stock_TA") +
                "(FPortClsCode, FYearMonth, FStorageDate, FPortCode, FCuryCode, FStorageAmount, FCost, FPortCuryCost," +
                " FBaseCuryCost, FBaseCuryRate, FPortCuryRate, FCuryUnpl, FCuryPl, " +
                " FPortCuryUnpl, FPortCuryPl, FBaseCuryUnpl, FBaseCurypl, FAnalysisCode1, FAnalysisCode2, " +
                //xuqiji 20091111  MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                " FAnalysisCode3, FStorageInd, FCheckState, FCreator, FCreateTime, FCheckUser, FCheckTime,fconvertnum" +
                /**shashijie 2011-10-31 STORY 1589 */
                ",FPortStorageAmount " +                
                /**end*/
                ", FfjzhzsStorageAmount" +//fj
                " )" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            for (i = 0; i < this.addList.size(); i++) {

                tastorage = (TAStorageBean) addList.get(i);
                yssPst.setString(1, (tastorage.getPortClsCode() == null ? " " : tastorage.getPortClsCode()));
                yssPst.setString(2,
                              YssFun.formatDate(tastorage.getStorageDate(),
                                                "yyyyMM"));

                yssPst.setDate(3,
                            YssFun.toSqlDate(tastorage.getStorageDate()));
                yssPst.setString(4, tastorage.getPortCode());
                yssPst.setString(5, tastorage.getCuryCode());
                yssPst.setDouble(6,
                              tastorage.getStorageAmount());
                yssPst.setDouble(7, tastorage.getCost());
                yssPst.setDouble(8, tastorage.getPortCuryCost());
                yssPst.setDouble(9, tastorage.getBaseCuryCost());

                yssPst.setDouble(10,
                              tastorage.getBaseCuryRate());
                yssPst.setDouble(11,
                              tastorage.getPortCuryRate());
                yssPst.setDouble(12,
                              tastorage.getCuryUnPl());
                yssPst.setDouble(13,
                              tastorage.getCuryPl());

                yssPst.setDouble(14,
                              tastorage.getPortCuryUnpl());
                yssPst.setDouble(15,
                              tastorage.getPortCuryPl());
                yssPst.setDouble(16,
                              tastorage.getBaseCuryUnpl());
                yssPst.setDouble(17,
                              tastorage.getBaseCuryPl());
                yssPst.setString(18, tastorage.getAnalysisCode1().trim().length() == 0 ? " " : tastorage.getAnalysisCode1());
                yssPst.setString(19, tastorage.getAnalysisCode2().trim().length() == 0 ? " " : tastorage.getAnalysisCode2());
                yssPst.setString(20, tastorage.getAnalysisCode3().trim().length() == 0 ? " " : tastorage.getAnalysisCode3());
                // pst.setInt(21, 0);
                yssPst.setInt(21, 0); //库存状态 ： 0－自动计算（未锁定）
                yssPst.setInt(22, 1);
                yssPst.setString(23, pub.getUserCode());
                yssPst.setString(24, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(25, pub.getUserCode());
                yssPst.setString(26, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setDouble(27, tastorage.getConvertMoney());
                /**shashijie 2011-10-31 STORY 1589 */
                yssPst.setDouble(28, tastorage.getFPortStorageAmount());
                /**end*/
                yssPst.setDouble(29, tastorage.getClsPortStorageAmount()); //fj
                yssPst.executeUpdate();

            }
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("系统保存TA库存金额时出现异常!" + "\n", e); // by 曹丞 2009.02.01 保存TA库存金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeStatementFinal(yssPst);

        }
    }
    
    
    /**
     *story3759 份额折算
     *add by yeshenghong 20130519
     *杠杆基金份额折算方法  插入TA交易数据
     * @throws YssException 
     */
    public void leverGradeFundShareDisCount(Date dDate,String portCode) throws YssException
    {
		ResultSet rs = null;
		String strSql = "";
		String discountType = "";
		String curyCode = "";
		String cashAccCode = "";
		Connection conn = null;
		boolean bTrans = false;
		Date baseDate = dDate;
		try {
			conn = dbl.getConnection();
			conn.setAutoCommit(bTrans);
			bTrans = true;
			// 向TA 交易数据表Tb_XXX_TA_Trade中插入数据
			// 插入数据前，先删除数据，条件： 日期和组合
			this.deleteFundDevidentData(dDate, portCode);
			
			strSql = " select t.fcurycode, t.fcashacccode from "
					+ pub.yssGetTableName("tb_ta_trade") + " t where "
					+ " t.fselltype = '00' and t.fcheckstate = 1 and fportcode = "
					+ dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);

			if (rs.next()) {
				curyCode = rs.getString("fcurycode");
				cashAccCode = rs.getString("fcashacccode");
			}
			dbl.closeResultSetFinal(rs);
			
			boolean discountAll = true;
			strSql = " select distinct t.fconversiontype,t.fbaseDate,t.fportclscode from "
				+ pub.yssGetTableName("tb_ta_levershare") + " t  "
				+ " where t.fcheckstate = 1 and t.fconversiondate =  " + dbl.sqlDate(dDate)
				+ " and t.FPortcode = " + dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				discountType = rs.getString("fconversiontype");
				baseDate = rs.getDate("fbaseDate");
				if(rs.getString("fportclscode")!=null&&!rs.getString("fportclscode").trim().equals(""))
				{
					discountAll = false;
				}
			}
			dbl.closeResultSetFinal(rs);
			
			
			this.insetGradeFundShareDisCount(dDate,baseDate, portCode, curyCode, cashAccCode,discountType,discountAll);
			
			conn.commit();
			conn.setAutoCommit(bTrans);
		    bTrans = false;
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.endTransFinal(bTrans);
			dbl.closeResultSetFinal(rs);
		}
    }
    
    /*
     * 插入份额折算数据到TA表中
     * modified by yeshenghong story 3759
     * 20130609
     * */
	private void insetGradeFundShareDisCount(Date dDate, Date baseDate,String portCode,
			String curyCode, String cashAccCode,String discountType,boolean discountAll) throws YssException {
		String strSql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String strNumDate = "";
		long sNum = 0;
		double netAValue = 0;
		double tradeAmount = 0;
		double preNav = 0;
		String portClsCode = "";
		String insertTASql = "";
		boolean updated = false;
		try {
			// 基础汇率
			double baseCuryRate = this.getSettingOper().getCuryRate(dDate,
					curyCode, portCode, YssOperCons.YSS_RATE_BASE);

			EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
			rateOper.setYssPub(pub);
			rateOper.getInnerPortRate(dDate, curyCode, portCode);
			double portCuryRate = rateOper.getDPortRate();// 组合汇率

			// --------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(dDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub.yssGetTableName("tb_ta_trade"),
							dbl.sqlRight("FNUM", 6), "000000",
							" where FNum like 'T" + strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			// --------------------------------end--------------------------//
			insertTASql = " insert into "
				+ pub.yssGetTableName("Tb_TA_Trade")
				+ " (FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2, "
				+ " FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode, "
				+ " FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator, "
				+ " FCreateTime,FCheckUser,FCheckTime,FCONVERTTYPE, FSPLITNETVALUE) "
				+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = dbl.openPreparedStatement(insertTASql);

			pstmt.setDate(2, YssFun.toSqlDate(dDate)); // 交易日期
			pstmt.setDate(3, YssFun.toSqlDate(baseDate)); // 基准日期
			pstmt.setString(4, portCode); // 组合代码
			// pstmt.setString(5, portClsCode); // 组合分级代码 A份额
			// 为基金代码
			pstmt.setString(6, " "); // 销售网点代码
			pstmt.setString(7, "09"); // 销售类型 基金拆分
			//pstmt.setString(8, curyCode); // 销售货币
			pstmt.setString(9, " "); // 分析代码1
			pstmt.setString(10, " "); // 分析代码2
			pstmt.setString(11, " "); // 分析代码3

			// pstmt.setDouble(12, netAValue * tradeAmount); //
			// 销售金额(净金额)=单位净值*销售数量
			pstmt.setDouble(13, 0); // 基准金额
			// pstmt.setDouble(14, tradeAmount); // 销售数量
			// pstmt.setDouble(15, netAValue); // 销售价格
			pstmt.setDouble(16, 0); // 未实现损益平准金
			pstmt.setDouble(17, 0); // 损益平准金

			//pstmt.setString(18, cashAccCode); // 现金帐户
			pstmt.setDate(19, YssFun.toSqlDate(dDate)); // 确认日期
			pstmt.setDate(20, YssFun.toSqlDate(dDate)); // 结算日期
			pstmt.setDouble(21, 0); // 结算金额
			pstmt.setDouble(22, portCuryRate); // 组合汇率
			pstmt.setDouble(23, baseCuryRate); // 基础汇率
			pstmt.setInt(24, 0); // 结算状态
			pstmt.setString(25, ""); // 描述
			pstmt.setInt(26, 1); // 审核状态
			pstmt.setString(27, pub.getUserCode()); // 创建人、修改人
			pstmt.setString(28, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
			pstmt.setString(29, pub.getUserCode()); // 复核人
			pstmt.setString(30, YssFun.formatDatetime(new java.util.Date())); // 复核时间
			// pstmt.setString(31, "xiachai");// 现金差额结转日期
			// pstmt.setDouble(32, preNav);//拆分前单位净值

			 //查询获取基金份额的份额类别   FNAVFormula 净值公式, FDiscountFormula 折算公式
            strSql = " select distinct t.FPortClsCode,t.FShareCategory,t.FAfterDiscountNav,t.FAfterDiscountAmount  from " + 
            		 pub.yssGetTableName("tb_ta_portcls") +  " t " +
            		 (!discountAll ? " join (select fportclscode from " + pub.yssGetTableName("tb_ta_levershare") + " where fcheckstate = 1 ) l " +
            		 " on t.FPortClsCode = l.fportclscode " : "  " ) +
            		 " where t.fportcode  = " + dbl.sqlString(portCode) + 
            		 " and t.FAfterDiscountAmount is not null and t.FCheckState = 1 ";
           
            rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if(rs.getString("FAfterDiscountAmount")==null||rs.getString("FAfterDiscountAmount").trim().equals(""))//如果没有设置折算公式 直接跳过 
				{
					continue;
				}
				// --------------------拼接交易编号---------------------
				sNum++;
				String tmp = "";
				for (int t = 0; t < s.length() - String.valueOf(sNum).length(); t++) {
					tmp += "0";
				}
				strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
				// ------------------------end--------------------------//
			    LeverGradeFundCfg leverCfg = new LeverGradeFundCfg();
		        leverCfg.setYssPub(pub);
				portClsCode = rs.getString("FPortClsCode");
				preNav = this.getPortClsNav(portClsCode, portCode, dDate);
				
				leverCfg.init(rs.getString("FAfterDiscountNav"), portCode,
						portClsCode, dDate);
				netAValue = leverCfg.calcGradeFundNetValue();// 获取分级基金的折算后单位净值

				leverCfg.init(rs.getString("FAfterDiscountAmount"), portCode,
						portClsCode, dDate);
				tradeAmount = leverCfg.calcGradeFundNetValue();
				pstmt.setString(1, strNumDate); // 编号

				pstmt.setString(5, portClsCode); // 组合分级代码 A份额
				pstmt.setDouble(12, netAValue * tradeAmount); // 销售金额(净金额)=单位净值*销售数量

				pstmt.setDouble(14, tradeAmount); // 销售数量
				pstmt.setDouble(15, netAValue); // 销售价格

				pstmt.setString(31, discountType);//拆分类型
				pstmt.setDouble(32, preNav);// 拆分前单位净值
				
				
				strSql = " select t.fcurycode, t.fcashacccode from "
						+ pub.yssGetTableName("tb_ta_trade") + " t where "
						+ " t.fselltype = '00' and t.fcheckstate = 1 and fportcode = "
						+ dbl.sqlString(portCode) + " and fportclscode=" + dbl.sqlString(portClsCode) ;
				rs1 = dbl.openResultSet(strSql);
		
				if (rs1.next()) {
					curyCode = rs1.getString("fcurycode");
					cashAccCode = rs1.getString("fcashacccode");
				}
				dbl.closeResultSetFinal(rs1);
				
				pstmt.setString(8, curyCode); // 销售货币
				pstmt.setString(18, cashAccCode); // 现金帐户
				
				if(tradeAmount!=0)
				{
					updated = true;
					pstmt.addBatch();
				}
			}
			if(updated)
			{
				pstmt.executeBatch();
			}
		} catch (Exception e) {
			throw new YssException(e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    
    /**
     *story3759 份额折算
     *add by yeshenghong 20130519
     *分级基金份额折算日的单位净值
     * @throws YssException 
     */
    private  double getPortClsNav(String portClsCode,String portCode,Date dDate) throws YssException
    {
    	ResultSet rs = null;
        String strSql = "";
        double curNav = 0;
        try {
            //查询获取基金份额的份额类别   FDailyNav 净值公式, FConvention 折算公式
                strSql = " select distinct t.FPortClsCode,t.FShareCategory,t.FConvention,t.FPeriod,t.FDailyNav,t.FAfterDiscountNav," +
                		" t.FAfterDiscountAmount, s.FBeanId, t.fshowitem,t.FOFFSET  from " + pub.yssGetTableName("tb_ta_portcls") +  
                		" t join Tb_Base_CalcInsMetic b on t.FDailyNav = b.FCIMCode " +
                		" join  tb_fun_spinginvoke s on b.fspicode = s.fsicode"+
                		" where t.fportcode  = " + dbl.sqlString(portCode) + " and FPortClsCode = " + dbl.sqlString(portClsCode) + 
                		" and t.FCheckState = 1 order by FOFFSET, fportclscode ";
                rs = dbl.openResultSet(strSql);
                
                LeverGradeFundCfg leverCfg = null;
                if (rs.next()) {
                	 if (rs.getString("FBeanId") != null && rs.getString("FBeanId").length() > 0) {
    	            	 leverCfg = (LeverGradeFundCfg) pub.getOperDealCtx().getBean(
    	                         rs.
    	                         getString("FBeanId"));
    	            	 leverCfg.setYssPub(pub);
    	 	    		 leverCfg.init(rs.getString("FDailyNav"),portCode,portClsCode,dDate);
    	            }else
    	            {
    	            	throw new YssException("请设置【基础参数模块的Spring调用】,引用BeanId: LeverGradeFundCfg!");
    	            }
    	            
                	curNav = leverCfg.calcGradeFundNetValue();
                }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return curNav;
    }
    
    
    /**
     * story3759 份额折算 add by yeshenghong 20130519
     * 根据TA基金权益信息表中的信息，执行TA基金拆分，在TA交易数据表中产生TA基金拆分数据
     * @throws YssException
     */
    public void createFundDivident(Date dDate,String sPortCode) throws YssException {
        String sFNum = "";
        String strSql = "";
        String strSqlInsert = "";
        String strSqlDelete = "";
        ResultSet rs = null;
        TaTradeBean tradeBean = null;
        YssPreparedStatement pst = null;
        ArrayList list = new ArrayList();
        HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值

        try {
        	
        	this.deleteFundDevidentData(dDate,sPortCode);
            // 查询语句：在估值日当天：查询TA基金权益信息表中权益类型为基金拆分的记录
            strSql = "SELECT FR.*,CA.FCuryCode,round(NVl(ta.FPrice,1)/FR.FRightUnitMoney,9) as sss, ta2.FPortMarketValue as amount FROM " +
		            "(SELECT * FROM " + pub.yssGetTableName("Tb_TA_FundRight") +
		            " WHERE FCHECKSTATE = 1 AND FRightType= 'Reinvest' AND FRightDate = " +
		            dbl.sqlDate(dDate) + " AND FPortCode = " + dbl.sqlString(sPortCode) + ") FR " +
		            " left join (select FPortCode,FPortCury As FCuryCode from " +
		            pub.yssGetTableName("Tb_Para_Portfolio") +
		            " where FCheckState = 1) CA on CA.FPortCode = FR.FPortCode " +
		            " left join (select * from " + pub.yssGetTableName("tb_data_navdata") + " where FReTypeCode = 'Total' and FKeyCode = 'Unit') ta on FR.FRIGHTDATE = ta.FNavDate and FR.FPORTCODE = ta.fportcode" +
		            " left join (select * from " + pub.yssGetTableName("tb_data_navdata") + " where FReTypeCode = 'Total' and FKeyCode = 'TotalAmount') ta2 on FR.FRIGHTDATE = ta2.FNavDate and FR.FPORTCODE = ta2.fportcode " ;
            rs = dbl.queryByPreparedStatement(strSql);
            // 循环记录集，将tradeBean对象添加到list中
            while (rs.next()) {
                tradeBean = setTaTradeBean(rs,dDate);
            	//20120605 added by liubo.Story #2683
                //===========================
                tradeBean.setdSplitRatio(rs.getDouble("sss"));
                //============end===============
                
                list.add(tradeBean);
            }
            //====================end=====================
            // 插入语句：向TA交易数据表中插入数据-
           this.insertFundDevidentData(dDate, list);
            
        } catch (Exception ex) {
            throw new YssException("在TA交易数据表中产生TA基金拆分数据出现异常！", ex);
        } finally {
            dbl.closeStatementFinal(pst);
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /*
     * modified by yeshenghong story 3759  20130609
     * 在向TA交易数据表中插入数据之前先删除相应的记录
     * 
     */
    private void deleteFundDevidentData(Date dDate,String portCode) throws YssException
    {
    	// 向TA 交易数据表Tb_XXX_TA_Trade中插入数据
		// 插入数据前，先删除数据，条件： 日期和组合
    	String strSqlDelete = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
					+ " where FTradeDate =" + dbl.sqlDate(dDate)
					+ " and FPortCode =" + dbl.sqlString(portCode) + " and FSellType = '09' ";
    	 try {
			dbl.executeSql(strSqlDelete);
		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			throw new YssException("在TA交易数据表中删除TA基金拆分数据出现异常！", ex);
		} // 执行删除语句
    }
    
    /*
     * modified by yeshenghong story 3759  20130609
     * 在向TA交易数据表中插入数据
     * 
     */
	private void insertFundDevidentData(Date dDate, ArrayList list)
			throws YssException {
		ResultSet rs = null;
		TaTradeBean tradeBean = null;
		YssPreparedStatement pst = null;
		String sFNum = "";
		String strSql = "";
		String strSqlInsert = "";
		String strSqlDelete = "";
		HashMap htDiffDate = new HashMap(); // 存放不同日期的 Max FNum 值
		try {
			// 插入语句：向TA交易数据表中插入数据-
			strSqlInsert = "insert into "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ "(FNUM,FMarkDate,FTradeDate,FPORTCODE,FSellNetCode,FSellType,FCuryCode,"
					+ " FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FSellMoney,"
					+ " FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FConfimDate,FSettleDate,FBASECURYRATE,FPortCuryRate,"
					+ " FFeeCode1, FTradeFee1, FFeeCode2, FTradeFee2, FFeeCode3, FTradeFee3, FFeeCode4, FTradeFee4,"
					+ " FFeeCode5, FTradeFee5, FFeeCode6, FTradeFee6, FFeeCode7, FTradeFee7, FFeeCode8, FTradeFee8"
					+ ", FDesc, FCheckState, FCreator, FCreateTime,FCheckUser,FCheckTime,FSettleState,FPortClsCode,FSettleMoney"
					+ ",FBeMarkMoney"
					+ ", FSPLITRATIO"
					+ // 20120607 added by liubo.Story #2683.拆分比例
					")" + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "?,?,?,?,?,?,?,?,?,?,?)";
			pst = dbl.getYssPreparedStatement(strSqlInsert);

			// 循环向TA交易数据表中插入数据
			for (int i = 0; i < list.size(); i++) {
				tradeBean = (TaTradeBean) list.get(i);
				if (i == 0) {
					sFNum = getNum(tradeBean); // 只取一次
					htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
				}
				if (htDiffDate.get(tradeBean.getDSettleDate()) == null) {
					// 如果本次的日期与上一次的日期不同的话就得再取一次编号
					sFNum = getNum(tradeBean);
					htDiffDate.put(tradeBean.getDSettleDate(), sFNum);
				}
				pst.setString(1, sFNum); // 将多次获取编号的方法改为只取一次,以提高效率
				pst.setDate(2, YssFun.toSqlDate(tradeBean.getDMarkDate()));
				pst.setDate(3, YssFun.toSqlDate(tradeBean.getDTradeDate()));
				pst.setString(4, tradeBean.getStrPortCode());
				pst.setString(5, tradeBean.getStrSellNetCode().trim()
								.length() == 0 ? " " : tradeBean
										.getStrSellNetCode());
				pst.setString(6, tradeBean.getStrSellTypeCode());
				pst.setString(7, tradeBean.getStrCuryCode());
				pst.setString(8, tradeBean.getStrAnalysisCode1().trim()
						.length() == 0 ? " " : tradeBean.getStrAnalysisCode1());
				pst.setString(9, tradeBean.getStrAnalysisCode2().trim()
						.length() == 0 ? " " : tradeBean.getStrAnalysisCode2());
				pst.setString(10, tradeBean.getStrAnalysisCode3().trim()
						.length() == 0 ? " " : tradeBean.getStrAnalysisCode3());
				pst.setString(11, tradeBean.getStrCashAcctCode());
				pst.setDouble(12, tradeBean.getDSellMoney());
				pst.setDouble(13, tradeBean.getDSellAmount());
				pst.setDouble(14, tradeBean.getDSellPrice());
				pst.setDouble(15, tradeBean.getDIncomeNotBal());
				pst.setDouble(16, tradeBean.getDIncomeBal());
				pst.setDate(17, YssFun.toSqlDate(tradeBean.getDConfimDate()));
				pst.setDate(18, YssFun.toSqlDate(tradeBean.getDSettleDate()));
				pst.setDouble(19, tradeBean.getDBaseCuryRate());
				pst.setDouble(20, tradeBean.getDPortCuryRate());
				pst.setString(21, " ");
				pst.setDouble(22, 0);
				pst.setString(23, " ");
				pst.setDouble(24, 0);
				pst.setString(25, " ");
				pst.setDouble(26, 0);
				pst.setString(27, " ");
				pst.setDouble(28, 0);
				pst.setString(29, " ");
				pst.setDouble(30, 0);
				pst.setString(31, " ");
				pst.setDouble(32, 0);
				pst.setString(33, " ");
				pst.setDouble(34, 0);
				pst.setString(35, " ");
				pst.setDouble(36, 0);
				pst.setString(37, " "); // 描述
				pst.setInt(38, 1); // 设置审核状态为已审核
				pst.setString(39, pub.getUserCode());
				pst.setString(40, YssFun.formatDatetime(new java.util.Date()));
				pst.setString(41, pub.getUserCode());
				pst.setString(42, YssFun.formatDatetime(new java.util.Date()));
				pst.setInt(43, tradeBean.getSettleState()); // 结算方式
				pst.setString(44, tradeBean.getSPortClsCode()); // 分级组合代码
				pst.setDouble(45, tradeBean.getDSettleMoney() == 0 ? tradeBean
						.getDSellMoney() : tradeBean.getDSettleMoney());
				pst.setDouble(46, tradeBean.getBeMarkMoney() == 0 ? tradeBean
						.getDSellMoney() : tradeBean.getBeMarkMoney());
				pst.setDouble(47, tradeBean.getdSplitRatio());
				pst.addBatch();
			}
			pst.executeBatch();

		} catch (Exception ex) {
			throw new YssException("在TA交易数据表中产生TA基金拆分数据出现异常！", ex);
		} finally {
			dbl.closeStatementFinal(pst);
		}
	}
	
    
    /**
     * story3759 份额折算 add by yeshenghong 20130519
     * 设置TA交易数据对象，并返回此对象
     * @param rs ResultSet
     * @return TaTradeBean
     * @throws YssException
     */
    private TaTradeBean setTaTradeBean(ResultSet rs,Date dDate) throws YssException {
        TaTradeBean taTradeBean = new TaTradeBean();
        double BaseCuryRate = 0;
        double PortCuryRate = 0;
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        try {

            BaseCuryRate = this.getSettingOper().getCuryRate(dDate, //获取当日的基础汇率
                rs.getString("FCuryCode"), rs.getString("FPortCode"), YssOperCons.YSS_RATE_BASE);

            rateOper.setYssPub(pub);
            rateOper.getInnerPortRate(dDate, rs.getString("FCuryCode"), rs.getString("FPortCode"));
            PortCuryRate = rateOper.getDPortRate();                 //获取当日的组合汇率

            taTradeBean.setDMarkDate(rs.getDate("FRightDate"));     // 设置基准日期
            taTradeBean.setDTradeDate(rs.getDate("FRightDate"));    // 设置交易日期
            taTradeBean.setDConfimDate(rs.getDate("FRightDate"));   // 设置确认日期
            taTradeBean.setDSettleDate(rs.getDate("FSettledate"));   // 设置结算日期
            taTradeBean.setStrSellTypeCode("09");                   // 设置销售类型:09(分红拆分)
            taTradeBean.setStrCuryCode(rs.getString("FCuryCode"));
            taTradeBean.setStrPortCode(rs.getString("FPortCode"));
            taTradeBean.setSPortClsCode(rs.getString("FPortClsCode"));
            taTradeBean.setStrCashAcctCode(rs.getString("FCashAccount") == null ? " " : rs.getString("FCashAccount"));
            taTradeBean.setDBaseCuryRate(BaseCuryRate);
            taTradeBean.setDPortCuryRate(PortCuryRate);
//            taTradeBean.setDSellAmount(rs.getDouble("FRightAmount")); // 设置销售份额 rs.getDouble("sss")
            taTradeBean.setDSellAmount(YssFun.roundIt((rs.getDouble("sss")-1) * rs.getDouble("amount"), 9));//modified by yeshenghong story 3759 20130515
            taTradeBean.setDSellPrice(rs.getDouble("FRightUnitMoney"));//权益价格
            taTradeBean.setDSellMoney(rs.getDouble("FRightMoney"));//权益金额
            
        } catch (Exception e) {
            throw new YssException("设置除TA交易数据时出现异常！", e);
        }
        return taTradeBean;
    }

    
    /**
     * story3759 份额折算 add by yeshenghong 20130519
     * 产生编号，并返回此编号
     * @param taTrade TaTradeBean
     * @return String
     * @throws YssException
     */
    private String getNum(TaTradeBean taTrade) throws YssException {
        String sFNum = "";
        sFNum = "T" +
            YssFun.formatDatetime(taTrade.getDSettleDate()).
            substring(0, 8) +
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_TA_Trade"),
                                   dbl.sqlRight("FNUM", 6), "000001",
                                   " where FSettleDate = " +
                                   dbl.sqlDate(taTrade.getDSettleDate()));
        return sFNum;
    }
    
    
}
