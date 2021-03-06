package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.yss.commeach.EachRateOper;
import com.yss.main.dao.ICostCalculate;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.etfoperation.ETFParamSetAdmin;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.linkInfo.BaseLinkInfoDeal;
import com.yss.main.parasetting.CashAccountBean;
import com.yss.main.taoperation.TaCashAccLinkBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.pojo.cache.YssCost;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>Title: QDV4易方达基金2011年7月27日01_A </p>
 *
 * <p>Description:跨境EFT基金接口导入中登结算明细数据 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CrossborderETFJsmxBean
    extends DataBase {
	
	/**shashijie 2012-12-11 STORY 3328 使用新获取工作日方法,考虑境内境外*/
	ETFParamSetAdmin paramSetAdmin = new ETFParamSetAdmin();//实例化参数设置的操作类
	/**end shashijie 2012-12-11 STORY 3328 */
	
    public CrossborderETFJsmxBean() {
    }
    
    /**
     * 接口导入数据的入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {
    	/**shashijie 2012-12-11 STORY 3328 使用新获取工作日方法,考虑境内境外*/
		paramSetAdmin.setYssPub(pub);//设置pub
		/**end*/
		
    	insertIntoJsmxTable();
    	insertIntoRelaTable();
    	
    }
    
    /**
     * 从结算明细数据文件中读取数据至结算明细表
     * @throws YssException
     */
    private void insertIntoJsmxTable() throws YssException {
        Connection con = dbl.loadConnection(); // 新建连接
        boolean bTrans = true;//事务控制标识
        ResultSet rs = null;//结果集声明
        PreparedStatement pst = null; // 声明PreparedStatement
        StringBuffer buff=null;//做拼接SQL语句
        /**add---huhuichao 2013-11-15 STORY  13644 将新规则123-申购，124-赎回转换为旧规则102-申购，103-赎回*/
        String yWLX = " ";
        /**end---huhuichao 2013-11-15 STORY  13644 */
       try{
           buff = new StringBuffer();
           con.setAutoCommit(false);
           // 1.删除中登结算明细表Tb_ETF_JSMXInterface相关导入日期和组合代码的数据
           buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"));
           buff.append(" where FDate =").append(dbl.sqlDate(this.sDate));//接口数据日期为当前接口导入日期
           buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");

           dbl.executeSql(buff.toString());
           buff.delete(0, buff.length());

           // 2.筛选出临时表tmp_etf_jsmx中的数据
           buff.append(" select j.JLLX,j.QSRQ,j.JSRQ,j.SCDM,j.YWLX,j.QSBZ,j.GHLX,j.CJBH,j.XWH1,j.XWHY,j.ZQZH,j.ZQDM1,j.ZQDM2,")
        		.append("j.SL,j.CJSL,j.JG1,j.JG2,j.QSJE,j.SJSF,j.FJSM,j.ZQLB,j.JGDM,j.JYRQ,p.fportcode ")
        		
        		/**shashijie 2011-12-21 STORY 1434 */
        		.append(",j.JYFS, j.JSFS, j.JSBH, j.SQBH, j.WTBH, j.QTRQ, j.WTSJ, j.CJSJ, j.XWH2, j.JSHY, j.TGHY," +
        				" j.LTLX, j.QYLB, j.GPNF, j.MMBZ, j.ZJZH, j.BZ, j.YHS, j.JSF, j.GHF, j.ZGF, j.SXF, j.QTJE1," +
        				" j.QTJE2, j.QTJE3 ")
        		/**end*/
        		
           		.append(" from tmp_etf_jsmx j ")
           		.append(" left join (select fportcode,fetfseat from ").append(pub.yssGetTableName("Tb_ETF_Param"))//关联出ETF组合
           		.append(" where fcheckstate = 1 and fportcode in (").append(operSql.sqlCodes(this.sPort))
           		.append(")) p on 1 = 1")
           		.append(" where j.JLLX in ('002','003')")//002-交收通知，003-交收结果
           		.append(" and j.XWH1 <> p.fetfseat");//数据是双方的（投资者和管理人）,处理席位号不等于ETF席位（上海中盘ETF的申赎席位）的数据

           rs = dbl.openResultSet(buff.toString());
           buff.delete(0, buff.length());
           // 3.向目标表Tb_ETF_JSMXInterface插入数据
            buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_JSMXInterface"));
            buff.append(" (FPortCode,FClearDate,FSettleDate,FMarketCode,FTradeTypeCode,FClearMark,FChangeType,FTradeNum,FSeatNum,");
            buff.append(" FClearCode,FStockholderCode,FSecurityCode1,FSecurityCode2,FTradeAmount,FSettlePrice,FTradePrice,FClearMoney,");
            buff.append(" FTotalMoney,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,");
            buff.append(" FDate,FRecordType,FSecurityType,FResultCode,FBargainDate,FSettleAmount");
            
            /**shashijie 2011-12-21 STORY 1434 */
            buff.append(",FTransactionBs,FReceiptsBs,FReceiptsNum,FApplicationNum,FConsignNum,FOtherDate," +
            " FConsignDate,FTradDate,FProfessionUnit,FSettleNum,FClearNum,FCurrencyType,FRightsType,FExtensionYear," +
            " FBargainBs,FFundsBar,FCurrencyCode,FStampTax,FhandleTax,FTransferTax,FCanalTax,FProcedureTax," +
            " FOtherMoney1,FOtherMoney2,FOtherMoney3 ");
            /**end*/
            
            buff.append(" ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +//38
            		",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");//55

            pst=dbl.openPreparedStatement(buff.toString());
            buff.delete(0,buff.length());
            while (rs.next()) {
            	if(null == rs.getString("FPortCode")){//如果所选组合不是ETF组合则不处理
            		continue;
            	}
                pst.setString(1, rs.getString("FPortCode")); //组合代码
                
                /**shashijie 2013-1-14 STORY 3402 处理日期,针对0与8位日期的转换与判断,YssFun.isDate判断不了8位的日期*/
				Date tempDate = getChangeDate(rs.getString("QSRQ"));
				pst.setDate(2, YssFun.toSqlDate(tempDate)); // 清算日期
				tempDate = getChangeDate(rs.getString("JSRQ"));
                pst.setDate(3, YssFun.toSqlDate(tempDate)); //结算日期
                /**end shashijie 2013-1-14 STORY */
                
                pst.setString(4, rs.getString("SCDM")); //市场代码
                /**add---huhuichao 2013-11-15 STORY  13644 将新规则123-申购，124-赎回转换为旧规则102-申购，103-赎回*/
                if(rs.getString("YWLX").equals("123")){
                	yWLX = "102";
                }
                else if(rs.getString("YWLX").equals("124")){
                	yWLX = "103";
                }
                else{
                	yWLX = rs.getString("YWLX");
                }
                pst.setString(5,yWLX); //业务类型
                /**end---huhuichao 2013-11-15 STORY  13644*/
                pst.setString(6, rs.getString("QSBZ").trim().length() > 0 ? rs.getString("QSBZ") : " "); //清算标志
                pst.setString(7, rs.getString("GHLX").trim().length() > 0 ? rs.getString("GHLX") : " "); //变动类型
                pst.setString(8, rs.getString("CJBH").trim().length() > 0 ? rs.getString("CJBH") : " "); //成交编号
                pst.setString(9, rs.getString("XWH1").trim().length() > 0 ? rs.getString("XWH1") : " "); //席位号
                pst.setString(10, rs.getString("XWHY").trim().length() > 0 ? rs.getString("XWHY") : " "); //清算编号
                pst.setString(11, rs.getString("ZQZH").trim().length() > 0 ? rs.getString("ZQZH") : " "); //股东代码
                pst.setString(12, rs.getString("ZQDM1")); //证券代码1
                pst.setString(13, rs.getString("ZQDM2").trim().length() > 0 ? rs.getString("ZQDM2") : " "); //证券代码2
                pst.setDouble(14, Math.abs(Double.parseDouble(rs.getString("CJSL").trim().length() > 0 ? 
                									rs.getString("CJSL") : "0"))); //成交数量--如果为赎回方，数量为负，因此取绝对值
                pst.setDouble(15, Double.parseDouble(rs.getString("JG1").trim().length() > 0 ? rs.getString("JG1") : "0")); //结算价格
                pst.setDouble(16, Double.parseDouble(rs.getString("JG2").trim().length() > 0 ? rs.getString("JG2") : "0")); //交易成交价格
                if(rs.getString("QSBZ").trim().equals("279")){//现金差额
                    pst.setDouble(17, Double.parseDouble(rs.getString("QSJE").trim().length() > 0 ? 
													rs.getString("QSJE") : "0")); //清算金额 
                    pst.setDouble(18, Double.parseDouble(rs.getString("SJSF").trim().length() > 0 ? 
													rs.getString("SJSF") : "0")); //实收实付
                }else{
                    pst.setDouble(17, Math.abs(Double.parseDouble(rs.getString("QSJE").trim().length() > 0 ? 
													rs.getString("QSJE") : "0"))); //清算金额 --申购方，金额为负，因此取绝对值
                    pst.setDouble(18, Math.abs(Double.parseDouble(rs.getString("SJSF").trim().length() > 0 ? 
													rs.getString("SJSF") : "0"))); //实收实付--申购方，金额为负，因此取绝对值
                }
                pst.setString(19, rs.getString("FJSM")); //附加说明
                pst.setInt(20, 1); //审核状态
                pst.setString(21, pub.getUserCode()); //创建人
                pst.setString(22, YssFun.formatDatetime(new java.util.Date())); //创建时间
                pst.setString(23, pub.getUserCode()); //复审人
                pst.setString(24, YssFun.formatDatetime(new java.util.Date())); //复审时间
                pst.setDate(25, YssFun.toSqlDate(this.sDate));//接口文件日期，即数据导入日期
                pst.setString(26, rs.getString("JLLX"));//记录类型
                pst.setString(27, rs.getString("ZQLB").trim().length() > 0 ? rs.getString("ZQLB") : " ");//证券类别
                pst.setString(28, rs.getString("JGDM"));//结果代码
                pst.setDate(29, YssFun.toSqlDate(YssFun.parseDate(rs.getString("JYRQ"), "yyyyMMdd")));//交易日期
                pst.setDouble(30, Math.abs(Double.parseDouble(rs.getString("SL").trim().length() > 0 ? 
												rs.getString("SL") : "0"))); //交收数量--如果为赎回方，数量为负，因此取绝对值
                
                /**shashijie 2011-12-21 STORY 1434 */
                setETFJSMXInterface(pst,rs);
                /**end*/
                
                pst.addBatch();
           }
           pst.executeBatch();
           con.commit();//提交事务
           bTrans=false;
           con.setAutoCommit(true);//设置为自动提交事务
       }catch(Exception e){
           throw new YssException("接口导入中登结算明细数据出错！",e);
       }finally{
           dbl.closeResultSetFinal(rs);
           dbl.endTransFinal(con,bTrans);
           dbl.closeStatementFinal(pst);
       }
    }
    
    /**shashijie 2013-1-14 STORY 3402 处理日期,针对0与8位日期的转换与判断,YssFun.isDate判断不了8位的日期*/
	private Date getChangeDate(String sDate) throws YssException {
		Date vDate = YssFun.parseDate("99981231", "yyyyMMdd");
		try {
			//正常日期转换
			vDate = YssFun.parseDate(sDate, "yyyyMMdd");
		} catch (Exception e) {
			//若不能转换则默认等于9998年12月31日
			vDate = YssFun.parseDate("99981231", "yyyyMMdd");
		} 
		return vDate;
	}

	/**shashijie ,2011-12-21 , STORY 1434 设置中登结算明细表非关键性字段 */
	private void setETFJSMXInterface(PreparedStatement ps, ResultSet rs) throws Exception {
		ps.setString(31, rs.getString("JYFS").trim().length() > 0 ? rs.getString("JYFS") : " ");//交易方式
		ps.setString(32, rs.getString("JSFS").trim().length() > 0 ? rs.getString("JSFS") : " ");//交收方式
		ps.setString(33, rs.getString("JSBH").trim().length() > 0 ? rs.getString("JSBH") : " ");//交收编号
		ps.setString(34, rs.getString("SQBH").trim().length() > 0 ? rs.getString("SQBH") : " ");//申请编号
		ps.setString(35, rs.getString("WTBH").trim().length() > 0 ? rs.getString("WTBH") : " ");//委托编号
		ps.setDate(36, YssFun.toSqlDate(YssFun.parseDate(YssFun.isDate(rs.getString("QTRQ")) ? 
				rs.getString("QTRQ") : "99981231", "yyyyMMdd")));//其它日期
		ps.setString(37, rs.getString("WTSJ").trim().length() > 0 ? rs.getString("WTSJ") : " ");//委托时间
		ps.setString(38, rs.getString("CJSJ").trim().length() > 0 ? rs.getString("CJSJ") : " ");//成交时间
		ps.setString(39, rs.getString("XWH2").trim().length() > 0 ? rs.getString("XWH2") : " ");//业务单元2
		ps.setString(40, rs.getString("JSHY").trim().length() > 0 ? rs.getString("JSHY") : " ");//结算参与人的清算编号
		ps.setString(41, rs.getString("TGHY").trim().length() > 0 ? rs.getString("TGHY") : " ");//托管银行的清算编号
		ps.setString(42, rs.getString("LTLX").trim().length() > 0 ? rs.getString("LTLX") : " ");//流通类型
		ps.setString(43, rs.getString("QYLB").trim().length() > 0 ? rs.getString("QYLB") : " ");//权益类别
		ps.setString(44, rs.getString("GPNF").trim().length() > 0 ? rs.getString("GPNF") : " ");//挂牌年份
		ps.setString(45, rs.getString("MMBZ").trim().length() > 0 ? rs.getString("MMBZ") : " ");//买卖标志
		ps.setString(46, rs.getString("ZJZH").trim().length() > 0 ? rs.getString("ZJZH") : " ");//资金账号
		ps.setString(47, rs.getString("BZ").trim().length() > 0 ? rs.getString("BZ") : " ");//币种
		ps.setDouble(48, Double.parseDouble(rs.getString("YHS").trim().length() > 0 ? rs.getString("YHS") : "0"));//印花税
		ps.setDouble(49, Double.parseDouble(rs.getString("JSF").trim().length() > 0 ? rs.getString("JSF") : "0"));//经手费
		ps.setDouble(50, Double.parseDouble(rs.getString("GHF").trim().length() > 0 ? rs.getString("GHF") : "0"));//过户费
		ps.setDouble(51, Double.parseDouble(rs.getString("ZGF").trim().length() > 0 ? rs.getString("ZGF") : "0"));//证管费
		ps.setDouble(52, Double.parseDouble(rs.getString("SXF").trim().length() > 0 ? rs.getString("SXF") : "0"));//手续费
		ps.setDouble(53, Double.parseDouble(rs.getString("QTJE1").trim().length() > 0 ? rs.getString("QTJE1") : "0"));//其它金额1
		ps.setDouble(54, Double.parseDouble(rs.getString("QTJE2").trim().length() > 0 ? rs.getString("QTJE2") : "0"));//其它金额2
		ps.setDouble(55, Double.parseDouble(rs.getString("QTJE3").trim().length() > 0 ? rs.getString("QTJE3") : "0"));//其它金额3
	}
	
	/**易方达不读取过户库接口文件，此处将结算明细表数据处理至相关表
     * 等同于过户库接口处理过程，确保系统内部正常处理 */
	private void insertIntoRelaTable() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;//事物控制标识
		Date buyDate = null;//申赎日期
		String portCode = "";
		String sTSql = "";
		ResultSet trs = null;
		try {
			con.setAutoCommit(false);//设置为手动提交事物
			//
			sTSql = "SELECT DISTINCT FPortCode,FRecordType,FBargainDate FROM " + pub.yssGetTableName("Tb_ETF_JsmxInterface")
					+ " where fportcode in (" + (operSql.sqlCodes(this.sPort))
					+ " ) and fdate = " + dbl.sqlDate(this.sDate)
					/**add---huhuichao 2013-8-16 STORY  4098 登记结算数据接口更新*/
					+ " and FRecordType in ('002','003') and FTradeTypeCode in('102','103','123','124') " 
					// 去掉现金差额
					+ " and FReceiptsBs <> '101'  order by FPortCode,FRecordType,FBargainDate asc";
			        /**end---huhuichao 2013-8-16 STORY  4098*/
			trs = dbl.openResultSet(sTSql);
			while(trs.next()){
				portCode = trs.getString("FPortCode");
				buyDate = trs.getDate("FBargainDate");
				//生成过户库数据
				insertIntoGHTable(portCode , buyDate);
				//
				insertIntegratedTable(portCode , buyDate);
				//生成TA数据
				insertIntoTATable(portCode , buyDate);
			}
			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置可以自动提交
		} catch (Exception e) {
			throw new YssException("处理结算明细数据出错！", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(trs);
		}	
	}
	
	private void insertIntoGHTable(String portCode, Date buyDate) throws YssException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int iLinkNum = 1;
		
		try{
			// 插入数据前，先删除表Tb_ETF_GHInterface相关交易日期和组合代码的数据
			String deleteSql = " delete from " + pub.yssGetTableName("Tb_ETF_GHInterface")
					+ " where FBargainDate = " + dbl.sqlDate(buyDate)
					+ " and FPortCode  = " + dbl.sqlString(portCode);
			dbl.executeSql(deleteSql);
			
			// 查询出结算明细表中的相关数据
			String queryTmpSql = "SELECT FPortCode,FStockholderCode,FBargainDate,FTradeNum,FSeatNum,FSettleAmount,FSecurityCode1,"
						+ "FTradePrice,FTotalMoney,FTradeTypeCode from " + pub.yssGetTableName("Tb_ETF_JsmxInterface")
						+ " where fportcode = " + dbl.sqlString(portCode)
						+ " and fdate = " + dbl.sqlDate(this.sDate)
						+ " and fbargaindate = " + dbl.sqlDate(buyDate)
						/**add---huhuichao 2013-8-16 STORY  4098 登记结算数据接口更新*/
						+ " and FRecordType in ('002','003') and FTradeTypeCode in('102','103','123','124') "//取申赎通知、结果数据
						+ " and FReceiptsBs <> '101' " // 去掉279现金差额的数据
						/**end---huhuichao 2013-8-16 STORY  4098 登记结算数据接口更新*/
						+ " and FRESULTCODE = '0000'";//只处理正常交收的
			rs = dbl.openResultSet(queryTmpSql);
			
			// 向目标表Tb_ETF_GHInterface插入数据
			String insertGhSql = " insert into "
					+ pub.yssGetTableName("Tb_ETF_GHInterface")
					+ "(FPortCode,FStockholderCode,FBargainDate,FTradeNum,FSeatNum,FTradeAmount,FSecurityCode,FApplyTime,FBargainTime,"
					+ "FTradePrice,FTradeMoney,FAppNum,FMark,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc,FRelaNum,FOperType)" + 
					" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = dbl.openPreparedStatement(insertGhSql);

			while (rs.next()) {
				//设置二级市场记录
				setGHProperties(pstmt,rs,"2ndcode",iLinkNum);
				pstmt.addBatch();
				//设置资金记录
				setGHProperties(pstmt,rs,"cashcode",iLinkNum);
				pstmt.addBatch();
				//设置一级市场记录
				setGHProperties(pstmt,rs,"1stdcode",iLinkNum);
				pstmt.addBatch();

				iLinkNum++;
			}
			pstmt.executeBatch();
		} catch (Exception e) {
			throw new YssException("从结算明细表获取数据到过户库表中出错！", e);
		}finally{
			dbl.closeStatementFinal(pstmt);
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 设置过户库字段
	 */
	private void setGHProperties(PreparedStatement pstmt,
									ResultSet rs,String dataType,int iLinkNum) throws YssException {
		try{
			pstmt.setString(1, rs.getString("FPortCode")); // 组合代码
			pstmt.setString(2, rs.getString("FStockholderCode")); // 股东代码
			pstmt.setDate(3, rs.getDate("FBargainDate")); // 业务日期
			
			pstmt.setString(5, rs.getString("FSeatNum")); // 席位代码
			if(dataType.equals("cashcode")){
				pstmt.setString(4, rs.getString("FTradeNum") + "0"); // 成交编号
				pstmt.setDouble(6, 1); // 申赎数量
				pstmt.setDouble(10, Double.parseDouble(rs.getString("FTradePrice"))); // 成交价格
				pstmt.setDouble(11, Double.parseDouble(rs.getString("FTotalMoney"))); // 实收实付金额
			}else{
				if(dataType.equals("2ndcode")){
					pstmt.setString(4, rs.getString("FTradeNum")); // 成交编号
				}else{
					pstmt.setString(4, rs.getString("FTradeNum") + "1"); // 成交编号
				}
				pstmt.setDouble(6, rs.getDouble("FSettleAmount")); // 申赎交收数量
				pstmt.setDouble(10, 0); // 成交价格
				pstmt.setDouble(11, 0); // 实收实付金额
			}			
			pstmt.setString(7, rs.getString("FSecurityCode1")); // 证券代码
			pstmt.setString(8, "000000"); // 申报时间
			pstmt.setString(9, "000000"); // 成交时间
			pstmt.setString(12, "ETFJIJIN"); // 申请编号
			/**add---huhuichao 2013-11-12 接口导入最新的中登结算明细数据存在问题*/
			if (rs.getString("FTradeTypeCode").equals("123")
					|| rs.getString("FTradeTypeCode").equals("124")) {
				pstmt.setString(13, rs.getString("FTradeTypeCode")
						.equals("123") ? "S" : "B"); // 申赎标示
			} else {
				pstmt.setString(13, rs.getString("FTradeTypeCode")
						.equals("102") ? "S" : "B"); // 申赎标示
			}
			/**end---huhuichao 2013-11-12 */
			pstmt.setInt(14, 1); // 审核状态
			pstmt.setString(15, pub.getUserCode()); // 创建人、修改人
			pstmt.setString(16, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
			pstmt.setString(17, pub.getUserCode()); // 复核人
			pstmt.setString(18, YssFun.formatDatetime(new java.util.Date())); // 复核时间
			pstmt.setString(19, ""); // 描述
			pstmt.setInt(20, iLinkNum); // 关联编号
			pstmt.setString(21, dataType); // 业务标志
		} catch (Exception e) {
			throw new YssException("设置结算明细字段值出错！", e);
		}
	}
	
	private void insertIntegratedTable(String portCode, Date buyDate) throws YssException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		long sNum = 0;//用于拼接交易编号
		YssCost cost = null;//声明成本类
		double baseMoney = 0;// 基础货币核算成本
		double portMoney = 0;// 组合货币核算成本
		double money = 0;// 原币核算成本
		//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
		OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
		try{
			//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			integrateAdmin.setYssPub(pub);
			
			// 查询表Tb_ETF_GHInterface中的数据
			String queryGhSql = " select a.*,mk.fclosingprice from (select gh.*, se.ftradecury, pa.fmktsrccode  from "
					+ " (select sum(aa.ftradeamount) as FTotalAmount,aa.fportcode,aa.fbargaindate,aa.fsecuritycode,aa.fmark from "
					+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
					+ " aa group by aa.fportcode,aa.fbargaindate,aa.fsecuritycode,aa.fmark) gh join (select FTradeCury, fsecuritycode from "
					+ pub.yssGetTableName("Tb_Para_Security")//证券信息表
					+ " where FcheckState = 1) se on gh.fsecuritycode = se.fsecuritycode "
					+ " join (select FPortCode, FMktSrcCode,FOneGradeMktCode,FTwoGradeMktCode,FCapitalCode from "
					+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
					+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode and gh.fsecuritycode <> pa.fonegrademktcode"
					+ " and gh.fsecuritycode <> pa.ftwogrademktcode and gh.fsecuritycode <> pa.fcapitalcode ) a"
					+ " left join (select mk2.* from(select max(FMktValueDate) as FMktValueDate,FSecurityCode from "
					+ pub.yssGetTableName("Tb_Data_MarketValue")//行情表
					+ " where FCheckState = 1 and FMktValueDate <= "
					+ dbl.sqlDate(buyDate)
					+ " group by FSecurityCode ) mk1 join (select FClosingPrice,FMktSrcCode, FSecurityCode, fmktvaluedate from "
					+ pub.yssGetTableName("Tb_Data_MarketValue")//行情表
					+ " where FCheckState = 1 order by FMktValueDate desc) mk2 "
					+ " on mk1.fsecuritycode = mk2.fsecuritycode and mk1.fmktvaluedate = mk2.fmktvaluedate"
					+ ") mk on a.fmktsrccode = mk.FMktSrcCode and a.FSecurityCode = mk.fsecuritycode and a.FBargainDate = mk.FMktValueDate"
					+ " where fportcode = "
					+ dbl.sqlString(portCode)
					+ " and a.FBargainDate = " + dbl.sqlDate(buyDate);
			rs = dbl.openResultSet(queryGhSql);
			//给表加锁
			dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Integrated"));
			// 3.向证券变动表Tb_XXX_Data_Integrated中插入数据
			// 插入数据前，先删除数据，条件：日期，组合，和 数据来源类型
			String sDeleteSql = " delete from "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FOperDate =" + dbl.sqlDate(buyDate)
					+ " and FPortCode = " + dbl.sqlString(portCode)
					+ " and FDataOrigin = " + dbl.sqlString("ETFBS");
			dbl.executeSql(sDeleteSql);

			String insertDISql = " insert into "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ "(FNum,FSubNum,FInOutType,FExchangeDate,FOperDate,FSecurityCode,FRelaNum,FNumType,"
					+ "FTradeTypeCode,FPortCode,FTsfTypeCode,FSubTsfTypeCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,"
					+ "FExchangeCost,FMExCost,FVExCost,FPortExCost,FMPortExCost,FVPortExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,"
					+ "FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDataOrigin)"
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = dbl.openPreparedStatement(insertDISql);
			ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx()
					.getBean("avgcostcalculate");
			if (rs.next()) {
				// 自动编号和交易子编号
				String sNewNum = "E"
						+ YssFun.formatDate(rs.getDate("FBargainDate"),
								"yyyyMMdd")
						+ dbFun.getNextInnerCode(pub
								.yssGetTableName("Tb_Data_Integrated"), dbl
								.sqlRight("FNUM", 6), "000001",
								" where FExchangeDate="
										+ dbl.sqlDate(rs
												.getDate("FBargainDate"))
										+ " or FExchangeDate="
										+ dbl.sqlDate("9998-12-31")
										+ " or FNum like 'E"
										+ YssFun.formatDate(rs
												.getDate("FBargainDate"),
												"yyyyMMdd") + "%'");
				String ss = sNewNum.substring(9, sNewNum.length());
				sNum = Long.parseLong(ss);
				rs.beforeFirst();
				while (rs.next()) {
					// --------------------拼接交易编号---------------------
					sNum++;
					String tmp = "";
					for (int t = 0; t < ss.length()
							- String.valueOf(sNum).length(); t++) {
						tmp += "0";
					}
					sNewNum = sNewNum.substring(0, 9) + tmp + sNum;
					// ------------------------end--------------------------//
					// 兑换方向
					int inOutType = 0;
					String tradeType = " ";
					String bs = rs.getString("FMark");//申购赎回类型
					if (bs.equalsIgnoreCase("s")) {//赎回
						inOutType = -1;
						tradeType = YssOperCons.YSS_JYLX_ETFSHH;
					} else if (bs.equalsIgnoreCase("b")) {//申购
						inOutType = 1;
						tradeType = YssOperCons.YSS_JYLX_ETFSG;
					}
					//基础汇率
					double baseCuryRate = this.getSettingOper()
							.getCuryRate(rs.getDate("FBargainDate"),
									rs.getString("FTradeCury"),
									portCode,
									YssOperCons.YSS_RATE_BASE);

					EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
					rateOper.setYssPub(pub);
					rateOper.getInnerPortRate(rs.getDate("FBargainDate"),
							rs.getString("FTradeCury"), portCode);
					double portCuryRate = rateOper.getDPortRate();//组合汇率

					if (tradeType.equalsIgnoreCase(YssOperCons.YSS_JYLX_ETFSHH)) {//赎回
						// --------------以下用移动加权计算成本--------------------------//
						costCal.initCostCalcutate(rs
								.getDate("FBargainDate"), rs
								.getString("FPortCode"), " ", " "," ");
						costCal.setYssPub(pub);
						cost = costCal.getCarryCost(rs
								.getString("FSecurityCode"), rs
								.getDouble("FTotalAmount"), " ",
								baseCuryRate, portCuryRate);
						costCal.roundCost(cost, 2);
						// ----------------------------------------------------------//
						money = YssD.mul(cost.getCost(), inOutType);//原币成本
						baseMoney = YssD.mul(cost.getBaseCost(), inOutType);//基础货币成本
						portMoney = YssD.mul(cost.getPortCost(), inOutType);//组合货币成本

					} else {
						//原币成本
						money = YssD.round(YssD.mul(rs
								.getDouble("FClosingPrice"), Double
								.parseDouble(rs.getString("FTotalAmount")),
								inOutType), 2);
						// 计算组合货币核算成本和基础货币成本
						baseMoney = YssD.round(YssD.mul(YssD.mul(rs.getDouble("FClosingPrice"),
									Double.parseDouble(rs.getString("FTotalAmount"))),
														baseCuryRate,
														inOutType), 2);
						portMoney = YssD.round(YssD.div(YssD.mul(YssD.mul(rs.getDouble("FClosingPrice"),
										Double.parseDouble(rs.getString("FTotalAmount"))),
														baseCuryRate,
														inOutType),
														portCuryRate), 2);
					}
					//delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
					//String sSubNum = sNewNum + YssFun.formatNumber(sNum + 1, "00000");
					pstmt.setString(1, sNewNum); // 自动编号
					//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
					pstmt.setString(2, integrateAdmin.getKeyNum()); // 交易子编号
					pstmt.setInt(3, inOutType); // 兑换方向,判断Tb_XXX_ETF_GHInterface
												// 表中的字段FMark，‘B’为流入，‘S’为流出
					pstmt.setDate(4, rs.getDate("FBargainDate")); // 兑换日期,取Tb_XXX_ETF_GHInterface表中的字段FBargainDate，yyyy-MM-dd
					pstmt.setDate(5, rs.getDate("FBargainDate")); // 业务日期
					pstmt.setString(6, rs.getString("FSecurityCode")); // 证券代码
					pstmt.setString(7, " "); // 关联编号
					pstmt.setString(8, " "); // 编号类型
					pstmt.setString(9, tradeType); // 交易类型
					pstmt.setString(10, portCode); // 组合
					pstmt.setString(11, " "); // 调拨类型代码
					pstmt.setString(12, " "); // 调拨子类型代码
					pstmt.setString(13, " "); // 分析代码1
					pstmt.setString(14, " "); // 分析代码2
					pstmt.setString(15, " "); // 分析代码3
					pstmt.setDouble(16, YssD.mul(Double.parseDouble(rs.getString("FTotalAmount")), inOutType)); // 兑换数量
					pstmt.setDouble(17, money); // 核算成本
					pstmt.setDouble(18, money); // 管理成本
					pstmt.setDouble(19, money); // 估值成本
					pstmt.setDouble(20, portMoney); // 组合货币核算成本
					pstmt.setDouble(21, portMoney); // 组合货币管理成本
					pstmt.setDouble(22, portMoney); // 组合货币估值成本
					pstmt.setDouble(23, baseMoney); // 基础货币核算成本
					pstmt.setDouble(24, baseMoney); // 基础货币管理成本
					pstmt.setDouble(25, baseMoney); // 基础货币估值成本
					pstmt.setDouble(26, baseCuryRate); // 基础汇率
					pstmt.setDouble(27, portCuryRate); // 组合汇率
					pstmt.setString(28, ""); // 描述
					pstmt.setString(29, ""); // 描述
					pstmt.setInt(30, 1); // 审核状态
					pstmt.setString(31, pub.getUserCode()); // 创建人、修改人
					pstmt.setString(32, YssFun.formatDatetime(new java.util.Date())); // 创建、修改时间
					pstmt.setString(33, pub.getUserCode()); // 复核人
					pstmt.setString(34, YssFun.formatDatetime(new java.util.Date())); // 复核时间
					pstmt.setString(35, "ETFBS"); // 数据来源类型
					pstmt.addBatch();
				}
				pstmt.executeBatch();
			}
		} catch (Exception e) {
			throw new YssException("数据处理至综合业务表中出错！", e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pstmt);			
		}
	}
	
	private void insertIntoTATable(String portCode, Date buyDate) throws YssException {
		String sSql = "";
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		ResultSet rr = null;
		String strNumDate = "";//保存交易编号
		Date CashBalanceDate = null;//现金差额结转日期
		Date CashReplaceDate = null;// 现金替代结转日期
		String strCashAcctCode = "";//现金账户
		Date confirmDate = null; //申赎确认日期
		double dCashRepAmount = 0;// 现金替代金额
		long sNum = 0;//用于拼接交易编号
		
		/**shashijie 2012-12-11 STORY 3328 使用新获取工作日方法,考虑境内境外*/
		HashMap wordDayMap = paramSetAdmin.getWorkDay(portCode, buyDate);
		/**end*/
		
		try{
			/**add---huhuichao 2013-8-16 BUG  9057 测试跨境ETF，在导入JSMX库后，手工维护的产品销售分红数据会被删除掉 */
			// 插入数据前，先删除数据，条件： 日期和组合,销售类型
			sSql = " delete from " + pub.yssGetTableName("Tb_TA_Trade")
					+ " where FTradeDate =" + dbl.sqlDate(buyDate)
					+ " and FPortCode =" + dbl.sqlString(portCode)
					+ " and FSellType in ('01','02')";
			/**end---huhuichao 2013-8-16 BUG  9057*/
			dbl.executeSql(sSql);

			String insertTASql = " insert into "
					+ pub.yssGetTableName("Tb_TA_Trade")
					+ "(FNum,FTradeDate,FMarkDate,FPortCode,FPortClsCode,FSellNetCode,FSellType,FCuryCode,FAnalysisCode1,FAnalysisCode2,"
					+ "FAnalysisCode3,FSellMoney,FBeMarkMoney,FSellAmount,FSellPrice,FIncomeNotBal,FIncomeBal,FCashAccCode,"
					+ "FConfimDate,FSettleDate,FSettleMoney,FPortCuryRate,FBaseCuryRate,FSettleState,FDesc,FCheckState,FCreator,"
					+ "FCreateTime,FCheckUser,FCheckTime,FConvertNum,FCashRepAmount,FCashBalanceDate,FCashReplaceDate,FCASHBAL)" 
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pstmt = dbl.openPreparedStatement(insertTASql);

			sSql = " select aa.*,pf.*,nav.Fportmarketvalue,unit.fprice" 
					+ " from (select sum(FTradeAmount) as FTradeAmountAll,  fportcode, FCashAccCode, FMark,FNORMSCALE,FSupplyMode"
					+ " from (select gh.*, pa.fcashacccode,pa.FNORMSCALE,pa.FSupplyMode from "
					+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
					+ " gh join (select FOneGradeMktCode, fportcode, FCashAccCode,FNORMSCALE,FSupplyMode from "
					+ pub.yssGetTableName("Tb_ETF_Param")//参数设置表
					+ " where FCheckState = 1) pa on gh.fportcode = pa.fportcode /*and gh.fsecuritycode = pa.fonegrademktcode*/ "
					+ " where gh.FPortCode = "
					+ dbl.sqlString(portCode)
					+ " and gh.fbargaindate ="
					+ dbl.sqlDate(buyDate)
					+ " and gh.fopertype = '1stdcode') tt"
					+ " group by fportcode ,FCashAccCode, FMark,FNORMSCALE,FSupplyMode) aa "
					+ " left join (select FPortCode as Fport, FPortCury,FAssetCode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")//组合设置表
					+ " where FCheckState=1) pf on aa.Fportcode = pf.Fport" 
					+ " left join (select Fportmarketvalue,fportcode from " + pub.yssGetTableName("tb_etf_navdata")//获取前一日单位现金差额
					+ " where fnavdate = " + dbl.sqlDate(buyDate)
					+ " and fkeycode = 'UnitCashBal') nav on nav.fportcode = aa.fportcode" 
					+ " left join (select FPrice,fportcode from " + pub.yssGetTableName("tb_etf_navdata")//获取前一日单位现金差额
					+ " where fnavdate = " + dbl.sqlDate(buyDate)
					+ " and fkeycode = 'Unit') unit on unit.fportcode = aa.fportcode";

			rst = dbl.openResultSet(sSql);

			// --------------------拼接交易编号---------------------
			strNumDate = YssFun.formatDatetime(buyDate).substring(0, 8);
			strNumDate = strNumDate
					+ dbFun.getNextInnerCode(pub
							.yssGetTableName("tb_ta_trade"), dbl.sqlRight(
							"FNUM", 6), "000000", " where FNum like 'T"
							+ strNumDate + "%'", 1);
			strNumDate = "T" + strNumDate;
			String s = strNumDate.substring(9, strNumDate.length());
			sNum = Long.parseLong(s);
			// --------------------------------end--------------------------//
			while (rst.next()) {
				//基础汇率
				double baseCuryRate = this.getSettingOper().getCuryRate(
						buyDate, rst.getString("FPortCury"),
						portCode, YssOperCons.YSS_RATE_BASE);

				EachRateOper rateOper = new EachRateOper(); // 新建获取利率的通用类
				rateOper.setYssPub(pub);
				rateOper.getInnerPortRate(buyDate, rst
						.getString("FPortCury"), portCode);
				double portCuryRate = rateOper.getDPortRate();//组合汇率
				//单位篮子的销售金额 = round(单位净值 * 最小申赎份额,2)
				//销售金额 = 单位篮子的销售金额 × 篮子数
				double dSellMoney = YssD.mul(YssD.round(YssD.mul(rst.getDouble("FPrice"), rst.getDouble("FNORMSCALE")),2),
						YssD.div(rst.getDouble("FTradeAmountAll"), rst.getDouble("FNORMSCALE")));
				// --------------------拼接交易编号---------------------
				sNum++;
				String tmp = "";
				for (int t = 0; t < s.length()
						- String.valueOf(sNum).length(); t++) {
					tmp += "0";
				}
				strNumDate = strNumDate.substring(0, 9) + tmp + sNum;
				// ------------------------end--------------------------//
				// ------------------------处理现金替代金额-------------------//
				sSql = " select sum(FTrademoney) as FTotalMoney from "
						+ pub.yssGetTableName("Tb_ETF_GHInterface")//过户库
						+ " where FPortCode = "
						+ dbl.sqlString(portCode)
						+ " and FOperType = 'cashcode' and FBargaindate = "
						+ dbl.sqlDate(buyDate) + " and FMark ="
						+ dbl.sqlString(rst.getString("FMark"))
						+ " group by FPortCode,Fmark";
				rr = dbl.openResultSet(sSql);
				if (rr.next()) {
					dCashRepAmount = rr.getDouble("FTotalMoney");// 现金替代金额
				}
				dbl.closeResultSetFinal(rr);
				// ----------------------------end-----------------------------//
				pstmt.setString(1, strNumDate); // 编号
				pstmt.setDate(2, YssFun.toSqlDate(buyDate)); // 交易日期
				pstmt.setDate(3, YssFun.toSqlDate(buyDate)); // 基准日期
				pstmt.setString(4, portCode); // 组合代码
				pstmt.setString(5, rst.getString("FAssetCode")); // 组合分级代码
																	// 为基金代码
				pstmt.setString(6, " "); // 销售网点代码
				pstmt.setString(7, rst.getString("FMark").equalsIgnoreCase(
						"S") ? "01" : "02"); // 销售类型
				pstmt.setString(8, rst.getString("FPortCury")); // 销售货币
				pstmt.setString(9, " "); // 分析代码1
				pstmt.setString(10, " "); // 分析代码2
				pstmt.setString(11, " "); // 分析代码3
				
				/**shashijie 2013-1-16 STORY 3402 增加国泰补票方式 */
				//易方达申赎在T+1日确认 STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20110810
				if(rst.getString("FSupplyMode").equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)
						||rst.getString("FSupplyMode").equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
					pstmt.setDouble(12, dSellMoney); // 销售金额(净金额)=单位净值*销售数量
					pstmt.setDouble(13, dSellMoney); // 基准金额
					pstmt.setDouble(14, rst.getDouble("FTradeAmountAll")); // 销售数量
					pstmt.setDouble(15, rst.getDouble("FPrice")); // 销售价格
				}else{
					pstmt.setDouble(12, 0); // 销售金额(净金额)=单位净值*销售数量
					pstmt.setDouble(13, 0); // 基准金额
					pstmt.setDouble(14, rst.getDouble("FTradeAmountAll")); // 销售数量
					pstmt.setDouble(15, 0); // 销售价格
					pstmt.setDouble(16, 0); // 未实现损益平准金
					pstmt.setDouble(17, 0); // 损益平准金
				}
				/**end shashijie 2013-1-16 STORY */
				
				//-----------------获取现金账户，通过现金账户链接------------------------//
				 BaseLinkInfoDeal taCashAccOper = (BaseLinkInfoDeal) pub.
                    getOperDealCtx().getBean(
                        "TaCashLinkDeal");
                taCashAccOper.setYssPub(pub);
                TaCashAccLinkBean taCashAccLink = new TaCashAccLinkBean();
                taCashAccLink.setSellNetCode(" ");
                taCashAccLink.setPortClsCode(rst.getString("FAssetCode"));
                taCashAccLink.setPortCode(portCode);
                taCashAccLink.setSellTypeCode(rst.getString("FMark").equalsIgnoreCase("S") ? "01" : "02");
                taCashAccLink.setCuryCode(rst.getString("FPortCury"));
                taCashAccLink.setStartDate(YssFun.formatDate(buyDate));
                taCashAccOper.setLinkAttr(taCashAccLink);
                ArrayList reList = taCashAccOper.getLinkInfoBeans();
                if (reList != null) {
                	strCashAcctCode = ( (CashAccountBean) reList.get(0)).getStrCashAcctCode();
                }
				//------------------end--------------------------------------------//
				pstmt.setString(18,strCashAcctCode.trim().length()>0?strCashAcctCode:" "); // 现金帐户
				
				/**shashijie 2013-1-16 STORY 3402 增加国泰补票方式*/
				if(rst.getString("FSupplyMode").equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_ONE)
						||rst.getString("FSupplyMode").equalsIgnoreCase(YssOperCons.YSS_ETF_MAKEUP_GCJQPJ)){
					//易方达申赎在T+1日确认 STORY #1434 QDV4易方达基金2011年7月27日01_A panjunfang modify 20110810
					TaTradeBean ta = new TaTradeBean();
					ta.setYssPub(pub);
		            ta.setStrPortCode(portCode);
		            ta.setSPortClsCode(rst.getString("FAssetCode"));
		            ta.setDTradeDate(YssFun.toSqlDate(buyDate));
		            ta.setStrSellNetCode(" ");
		            ta.setStrAnalysisCode1(" ");
		            ta.setDSellAmount(rst.getDouble("FTradeAmountAll"));
		            ta.setBeMarkMoney(dSellMoney);
		            ta.getETFPL();
					confirmDate = ta.getConfirmDay(" ", rst.getString("FAssetCode"), portCode, 
									rst.getString("FMark").equalsIgnoreCase("S") ? "01" : "02", rst.getString("FPortCury"), buyDate);

					pstmt.setDouble(16, ta.getDIncomeNotBal()); // 未实现损益平准金
					pstmt.setDouble(17, ta.getDIncomeBal()); // 损益平准金			
					pstmt.setDate(19, YssFun.toSqlDate(confirmDate)); // 确认日期
					pstmt.setDate(20, YssFun.toSqlDate(confirmDate)); // 结算日期
				}else{
					pstmt.setDate(19, YssFun.toSqlDate(buyDate)); // 确认日期
					pstmt.setDate(20, YssFun.toSqlDate(buyDate)); // 结算日期
				}
				/**end shashijie 2013-1-16 STORY */
				
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
				pstmt.setDouble(31, 0);// 份额折算数量
				pstmt.setDouble(32, dCashRepAmount);// 现金替代金额
				
				/**shashijie 2012-12-11 STORY 3328 使用新获取工作日方法,考虑境内境外*/
				// ----------------------以下处理现金差额结转日期和现金替代结转日期------------------------//
				
				if(rst.getString("FMark").equalsIgnoreCase("S")){//申购
					//申购现金差额结转
					CashBalanceDate = YssFun.toSqlDate((String)wordDayMap.get("sgbalanceover"));
					//申购现金替代结转
					CashReplaceDate = YssFun.toSqlDate((String)wordDayMap.get("sgreplaceover"));
				} else {
					//赎回现金差额结转；
					CashBalanceDate = YssFun.toSqlDate((String)wordDayMap.get("shbalanceover"));
					//赎回现金替代结转；
					CashReplaceDate = YssFun.toSqlDate((String)wordDayMap.get("shreplaceover"));
				}
				
				/**end*/
				
				pstmt.setDate(33, (java.sql.Date) CashBalanceDate);// 现金差额结转日期
				pstmt.setDate(34, (java.sql.Date) CashReplaceDate);// 现金替代结转日期
				// ----------------------end-------------------------------------------------------//
				pstmt.setDouble(35, YssD.mul(rst.getDouble("Fportmarketvalue"),
												YssD.div(rst.getDouble("FTradeAmountAll"), rst.getDouble("FNORMSCALE"))));//现金差额
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (Exception e) {
			throw new YssException("数据处理至TA交易数据表中出错！", e);
		}finally{
			dbl.closeResultSetFinal(rst);
			dbl.closeResultSetFinal(rr);
			dbl.closeStatementFinal(pstmt);			
		}
	}
	
}