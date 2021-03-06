package com.yss.main.operdeal.opermanage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.dsub.YssPreparedStatement;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.manager.CashPayRecAdmin;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @author shashijie ,2011-09-05 上午10:24:15 STORY 1580 赎回款业务
 */
public class OperRedemptionFeeMoney extends BaseOperManage {
	
    CashTransAdmin cashtransAdmin = new CashTransAdmin(); //生成资金调拨控制类
    CashPayRecAdmin cashpayrecadmin = new CashPayRecAdmin(); //生成现金应收应付控制类
    
    public OperRedemptionFeeMoney() {
    }

    /**执行业务处理*/
    public void doOpertion() throws YssException {
    	//先删除历史记录,资金调拨
		delOldCashTransfer();
		//先删除旧数据,综合业务
		deleteSec();
    	//获取基准金额(总赎回金额 - 总申购金额)
    	double money = getFBeMarkMoney();
    	//若为零则不生成数据
    	if (money <= 0) {
    		this.sMsg="        当日无业务"; //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
    		return;}
    	//赎回款业务
    	double FeeMoney = doOpertionExchangeStock(money);
    	if (FeeMoney <= 0) {return;}
    	//此处产生资金调拨
        String cashFNum = createCashTransfer(FeeMoney);
        //产生综合业务
        createDataIntegrated(cashFNum);
        
      //【STORY #2435 业务处理时若无业务要准确提示】 add by jsc 20120409 
		//当日产生数据，则认为有业务。
        if(cashFNum== null || cashFNum.length()==0){
        	this.sMsg="        当日无业务";
        }
    }

	/** 处理赎回款业务
     * @author shashijie ,2011-09-05 , STORY 1580
     * @modified 
     */
    private double doOpertionExchangeStock(double money) throws YssException {
    	ResultSet rs = null;
    	double resulte = 0;//赎回款费用
    	try {
    		String strSql = getStrSql();//获取TA费用连接设置等关联
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		if (rs.next()) {
    			//验证数据完整性
    			if (rs.getString("FFormulaCode")==null || rs.getString("FRoundCode")==null) {
    				throw new YssException("费用设置,比率代码 或舍入代码未设置！");
				}
    			
    			//公共计算费用类
    			BaseOperDeal base = new BaseOperDeal();
    			base.setYssPub(pub);
    			//计算赎回款费用数据,传参:比率代码 ,舍入代码,金额,日期范围
    			resulte = base.calMoneyByPerExp(rs.getString("FFormulaCode"), 
    					rs.getString("FRoundCode"), money, dDate);
			}
		} catch (Exception e) {
			throw new YssException("处理赎回款时出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return resulte;
	}

	/**shashijie ,2011-09-05 , STORY 1580*/
	private String getStrSql() {
		String strSql = " select b.FPortCode, b.FFeeCode1,"+
			" c.FRoundCode, d.FFormulaCode From "+pub.yssGetTableName("Tb_TA_FeeLink")+" b "+//TA费用连接
			" Left Join "+pub.yssGetTableName("Tb_Para_Fee")+" c on b.FFeeCode1 = c.FFeeCode "+//费用设置
			" Left Join "+pub.yssGetTableName("tb_para_performula")+" d on c.FPerExpCode = d.FFormulaCode"+//比率设置
			" where b.FStartDate <= "+dbl.sqlDate(dDate)+" and b.FCheckState = 1 and b.FFeeType = 1 "+
			" and b.FPortCode = "+dbl.sqlString(sPortCode)+" and b.FSellTypeCode = '02' ";
			//operSql.sqlCodes(sPortCode)
		return strSql;
	}
	

	/**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate;//调拨日期
        this.sPortCode = portCode;//组合
        cashtransAdmin.setYssPub(pub);//资金调拨
        cashpayrecadmin.setYssPub(pub);//现金应收应付
    }

    /**生成资金调拨
     * @throws YssException
     * @author shashijie ,2011-09-05 , STORY 1580
     * @modified 
     */
    private String createCashTransfer(double feeMoney) throws YssException {
        ResultSet rs = null;
        String fNum = "";
    	try {
    		String arrCash = getArrCash(feeMoney);
    		//资金调拨类
    		TransferBean cashBean = new TransferBean();
    		cashBean.setYssPub(pub);
    		cashBean.parseRowStr(arrCash);
    		pub.setbSysCheckState(false);//状态已审核
    		cashBean.setFNumType("FeeSMoney");//编号类型
            cashBean.addSetting();
    		
            fNum = cashBean.getStrNum();
        } catch (Exception ex) {
            throw new YssException("生成资金调拨出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return fNum;
    }
    
    /**设置解析的字符串
     * @author shashijie ,2011-9-6 , STORY 1580
     * @modified
     */
    private String getArrCash(double money) throws YssException {
    	ResultSet rs = null;
    	String pramString = "";
    	String srcCashAccCode = " ";//现金账户代码(不考虑有多个账户情况)
    	//String srcCashAccName = " ";//现金账户名称(泰达那里只有一个组合一个现金账户)
    	double FBaseCuryRate = 1;//基础汇率
    	double FPortCuryRate = 1;//组合汇率
    	try {
			String strSql = "select a.FCashAccCode,b.FCashAccName,a.FCuryCode,a.FPortCode From "+
				pub.yssGetTableName("Tb_TA_Trade")+ 
				" a left join "+pub.yssGetTableName("Tb_Para_CashAccount")+" b on a.FCashAccCode = b.FCashAccCode " +
				" where a.FSettleDate = "+dbl.sqlDate(dDate)+" and a.FSellType = '02' and a.FCheckState = 1 " +
				" and a.FPortCode = "+dbl.sqlString(sPortCode);
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				srcCashAccCode = rs.getString("FCashAccCode");//现金账户
				/**shashijie 2011-11-15 BUG 3144 */
				//公共获取汇率类
				FBaseCuryRate = this.getSettingOper().getCuryRate( //基础汇率
						this.dDate, 
						rs.getString("FCuryCode"), 
						this.sPortCode, 
						YssOperCons.YSS_RATE_BASE); 
				FPortCuryRate = this.getSettingOper().getCuryRate( //组合汇率
						this.dDate, 
						"", 
						this.sPortCode, 
						YssOperCons.YSS_RATE_PORT);
				/**end*/
    		}
			/*boolean analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");//分析代码
			boolean analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
			boolean analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");*/
			
			//现金调拨编号\t调拨类型代码\t调拨子类型代码\t属性代码\t投资品种代码\t调拨日期\t调拨结算日期\t调拨时间\t业务日期\t业务结束日期\t交易记录\t
			//现金调拨描述\t在初始登陆时是否只显示标题，不查询数据\t审核状态\t旧资金调拨编号,添加时会级联删除\t现金调拨描述\t
			//来源帐户代码\t来源帐户名称
			//[null]前是主表解析字符串,后是子表解析字符串,汇率都是1原币即本币
			//主表编号\t子编号\t资金流向 1代表流入;-1代表流出\t组合代码\t分析代码1\t分析代码2\t分析代码3\t现金帐户代码\t
            //调拨金额\t基础汇率\t组合汇率\t审核状态\t旧编号\t描述\t所属分类
			/**add---huhuichao 2013-7-17 STORY  4051  工银：划款指令收款人可以区分需求*/
			pramString = " \t03\t0303\t \t \t"+YssFun.formatDate(dDate)+"\t"+YssFun.formatDate(dDate)+
			        "\t00:00:00\t"+YssFun.formatDate(dDate)+"\t"+YssFun.formatDate(dDate)+//主表
					"\t \t \t0\t1\t \t \t \t \t" + //主表
					"\r\t[null]\r\t" +
					" \t \t-1\t"+sPortCode+"\tnull\tnull\tnull\t"+//子表
					srcCashAccCode+"\t"+money+"\t"+FBaseCuryRate+"\t"+FPortCuryRate+"\t1\t \t \t \t";//子表
			/**end---huhuichao 2013-7-17 STORY  4051*/
    	} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("获取TA交易数据中的基准金额出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return pramString;
	}

    /** 删除调拨类型03,调拨子类型0303,的历史资金调拨数据 */
    private void delOldCashTransfer() throws YssException {
    	ResultSet rs = null;
        String strTransNum = ""; //资金调拨编号
        String strSql = "";
		try {
			//查询当天调拨类型03，调拨子类型0303的历史资金调拨数据
			strSql = " Select a.FNum From "+pub.yssGetTableName("Tb_cash_transfer")+" a "+
					" join "+pub.yssGetTableName("Tb_Cash_SubTransfer")+" b on a.FNum = b.FNum "+
					" Where a.FTsfTypeCode = '03' and a.FSubTsfTypeCode = '0303' and a.FNumType = 'FeeSMoney' "+
					" and a.FTransferDate = "+dbl.sqlDate(dDate)+" and a.FTransDate = "+dbl.sqlDate(dDate)+
					" and b.FPortCode = "+dbl.sqlString(sPortCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			// 把要删除的资金调拨编号拼接起来
			while (rs.next()) {
				strTransNum += rs.getString("FNum") + ",";
			}
			dbl.closeResultSetFinal(rs);
			
			//删除
			if (strTransNum.length() > 1) {
				//去结尾逗号
				strTransNum = strTransNum.substring(0, strTransNum.length() - 1);
				//封装:('编号1','编号2'...)
				strTransNum = operSql.sqlCodes(strTransNum);
				//删除资金调拨主表,子表
				if (strTransNum.trim().length() > 0) {
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_Transfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);

					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_SubTransfer")
							+ " where FNum in (" + strTransNum + ")";
					dbl.executeSql(strSql);
				}
			}
		} catch (Exception e) {
			throw new YssException("删除历史的资金调拨出错" + "\r\n" + e.getMessage(),e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
	}
    
    /**获取TA交易数据中的基准金额
     * @return
     * @author shashijie ,2011-9-5 , STORY 1580
     * @modified
     */
    private double getFBeMarkMoney() throws YssException {
    	ResultSet rs = null;
    	double money = 0;//基准金额
    	try {
    		/**add---huhuichao 2013-7-9 STORY  4051 工银：划款指令收款人可以区分需求*/
    		/**shashijie 2011-10-28 STORY 1796 针对1580 需求变更 */
    		//总赎回金额减去总申购金额
			String strSql = "select sell.FSellMoney,buy.FBuyMoney From (select NVL(sum(a.FSettleMoney), 0) " +
					" as FSellMoney From "+pub.yssGetTableName("Tb_TA_Trade")+" a where a.FSettleDate = " +
					dbl.sqlDate(dDate)+" and a.FSellType = '02' and a.FCheckState = 1 and a.FPortCode = " +
					dbl.sqlString(this.sPortCode)+" ) sell, (select NVL(sum(b.FSettleMoney), 0) as FBuyMoney "+
			        " From "+pub.yssGetTableName("Tb_TA_Trade")+" b where b.FSettleDate = "+
			        dbl.sqlDate(dDate)+" and b.FSellType = '01' and b.FCheckState = 1 and b.FPortCode = " +
			        dbl.sqlString(this.sPortCode)+" ) buy ";
			/**end*/
			/**end---huhuichao 2013-7-9 STORY  4051*/
			rs = dbl.queryByPreparedStatement(strSql);
			if (rs.next()) {
				double sell = rs.getDouble("FSellMoney");//总赎回款
				double buy = rs.getDouble("FBuyMoney");//总申购款
				money = YssD.sub(sell, buy);
			}
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			/**add---huhuichao 2013-7-9 STORY  4051 工银：划款指令收款人可以区分需求*/
			throw new YssException("获取TA交易数据中的结算金额出错!",e);
			/**end---huhuichao 2013-7-9 STORY  4051*/
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return money;
	}

    /**产生综合业务数据
     * @author shashijie ,2011-9-6 , STORY 1580  
     * @modified
     */
	private void createDataIntegrated(String cashFNum) throws YssException{
		//综合业务自动编号
		String sNewNum = "E" + YssFun.formatDate(dDate, "yyyyMMdd")+
				dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
                dbl.sqlRight("FNUM", 6),
                "000001",
                " where FExchangeDate=" + dbl.sqlDate(dDate) +
                " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
                " or FNum like 'E" + YssFun.formatDate(dDate, "yyyyMMdd") + "%'");
		
		saveRelaDatas("Cash", cashFNum, sNewNum);
		
	}

	/**生成综合业务数据
	 * @param sNumType 编号类型
	 * @param FRelaNum 关联编号(这里是资金调拨编号)
	 * @param sNewNum 交易子编号
	 * @throws YssException 
	 * @author shashijie ,2011-9-7 , STORY 1580
	 * @modified 
	 */
	private void saveRelaDatas(String sNumType, String FRelaNum,
            String sNewNum) throws YssException {
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pst = null;
		YssPreparedStatement pst = null;
        //=============end====================
        String strSql = "insert into " +
            pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc,FCheckState,FCreator," +
            " FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode) " +
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
			//modified by liubo.Story #2145
			//==============================
//            pst = dbl.getPreparedStatement(strSql);
        	pst = dbl.getYssPreparedStatement(strSql);
			//==============end================
            //交易子编号
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//            String sSubNum = sNewNum +
//                dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
//                                       dbl.sqlRight("FSubNUM", 5),
//                                       "00000",
//                                       " where FNum =" + dbl.sqlString(sNewNum));
        	//---delete by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            pst.setString(1, sNewNum);//取前面的
            //edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
            pst.setString(2, integrateAdmin.getKeyNum());
            pst.setInt(3, 0);//方向
            pst.setString(4, " ");//证券代码
            pst.setDate(5, YssFun.toSqlDate(dDate));//兑换日期(操作日期)
            pst.setDate(6, YssFun.toSqlDate(dDate));//业务日期
            pst.setString(7, "34");//设置业务类型为 34挂款手续费
            pst.setString(8, FRelaNum);//关联编号(这里是资金调拨编号)
            pst.setString(9, sNumType);//编号类型
            pst.setString(10, sPortCode);//组合
            pst.setString(11, " ");
            pst.setString(12, " ");
            pst.setString(13, " ");
            pst.setDouble(14, 0.0);
            pst.setDouble(15, 0.0);
            pst.setDouble(16, 0.0);
            pst.setDouble(17, 0.0);
            pst.setDouble(18, 0.0);
            pst.setDouble(19, 0.0);
            pst.setDouble(20, 0.0);
            pst.setDouble(21, 0.0);
            pst.setDouble(22, 0.0);
            pst.setDouble(23, 0.0);
            pst.setDouble(24, 0.0);
            pst.setDouble(25, 0.0);
            pst.setString(26, " ");
            pst.setString(27, " ");//描述
            pst.setInt(28, 1);//审核状态
            pst.setString(29, pub.getUserCode());//创建人
            pst.setString(30, YssFun.formatDatetime(new Date()));//创建时间
            pst.setString(31, " ");
            pst.setString(32, " ");

            pst.setString(33, " ");
            pst.executeUpdate();
        } catch (Exception e) {
            throw new YssException("保存综合业务表出错", e);
        } finally {
            dbl.closeStatementFinal(pst);
        }
	}

	/**删除就的历史数据,业务类型为34的挂款手续费类型
	 * @author shashijie ,2011-9-6 , STORY 1580
	 * @modified
	 */
	private void deleteSec() throws YssException {
		try {
			String strSql = "delete From " + pub.yssGetTableName("Tb_Data_Integrated") +
		        " where FOperDate = "+dbl.sqlDate(dDate)+" and FExchangeDate = "+dbl.sqlDate(dDate)+
		        " and FTradeTypeCode = '34' and FNumType = 'Cash' and FPortCode = "+dbl.sqlString(sPortCode);
		    //执行sql语句
		    dbl.executeSql(strSql);
		} catch (Exception e) {
			throw new YssException("删除旧的综合业务数据出错", e);
		} finally {
			//dbl.closeResultSetFinal(rs);
		}
		
	}

	
}
