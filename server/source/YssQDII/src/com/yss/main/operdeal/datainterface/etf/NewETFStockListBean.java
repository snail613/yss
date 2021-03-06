package com.yss.main.operdeal.datainterface.etf;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * <p>
 * Title: xuqiji 20091012 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * </p>
 * <p>
 * Description: ETF基金接口导入股票篮数据，pcf定义2.0格式
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author xuqiji 20091020
 * 
 */
public class NewETFStockListBean extends DataBase {
	public NewETFStockListBean() {
	}

	/** 导入数据的入口方法 */
	public void inertData() throws YssException {
		Connection con = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;// 事务控制标识
		ResultSet rs = null;// 结果集声明
		PreparedStatement pst = null; // 声明PreparedStatement
		StringBuffer buff = null;// 做拼接SQL语句
		String sTmpData = "";// 保存从临时表中获取的数据
		String[] sSingleField = null;// 保存拆分后的每一条数据的每一个字段值
		
		try {
			buff = new StringBuffer();
			con.setAutoCommit(false);
			// 1.删除股票篮表Tb_ETF_StockList相关导入日期和组合代码的数据
			buff.append(" delete from ").append(pub.yssGetTableName("Tb_ETF_StockList"));
			buff.append(" where FDate =").append(dbl.sqlDate(this.sDate));
			buff.append(" and FPortCode in(").append(operSql.sqlCodes(this.sPort)).append(")");

			dbl.executeSql(buff.toString());
			buff.delete(0, buff.length());

			// 2.查询出股票篮临时表tmp_etf_stocklist中的数据
			buff.append(" select * from tmp_etf_stocklist_new");

			rs = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			
			// 3.向目标表Tb_ETF_StockList插入数据
			/**add---shashijie 2013-4-8 STORY 3822 增加替代金额字段*/
			buff = getInsertSql();
			/**end---shashijie 2013-4-8 STORY 3822*/

			pst = dbl.openPreparedStatement(buff.toString());
			buff.delete(0, buff.length());
			String[] arrPortCodes = this.sPort.split(","); // 拆分已选组合代码
			for (int i = 0; i < arrPortCodes.length; i++) {// 循环组合代码
				while (rs.next()) {
					sTmpData = rs.getString("stocklistfile").substring(1,rs.getString("stocklistfile").length());
					sSingleField = sTmpData.split("[|]");// 根据条件”|“拆分成每一条数据
					if(sSingleField.length == 7){
						/**add---shashijie 2013-4-8 STORY 3822 重构代码*/
						doPreStatment(pst,arrPortCodes[i],sSingleField);
						/**end---shashijie 2013-4-8 STORY 3822*/
					}
				}
				pst.executeBatch();
			}
			con.commit(); // 提交事务
			bTrans = false;
			con.setAutoCommit(true); // 设置可以自动提交

		} catch (Exception e) {
			throw new YssException("接口导入股票篮数据出错！", e);
		} finally {
			dbl.endTransFinal(con, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
	}

	/**shashijie 2013-4-8 STORY 3382 存入股票蓝表*/
	private void doPreStatment(PreparedStatement pst, String portCode,
			String[] sSingleField) throws YssException {
		try {
			//下面是给股票篮中的证券代码加上交易所代码
			String fsecuritycode = getSecurityCode(sSingleField[1].trim());
			//证券数量
			double Amount = Double.parseDouble(sSingleField[3].trim().length() > 0 ? sSingleField[3] : "0");
			
			//溢价比例
			double FPremiumScale = 
				Double.parseDouble(sSingleField[5].trim().length() > 0 ? sSingleField[5].trim() : "0");
			
			//替代标识6==必须现金替代
			String FReplaceMark = sSingleField[4];
			//总金额 = 替代金额 * （1+溢价比例）
			double FReplaceMoney = 
				Double.parseDouble(sSingleField[6].trim().length() > 0 ? sSingleField[6].trim() : "0");
			double FTotalMoney = 0;
			//若是必须现金替代,替代金额则不需要乘以溢价比例
			if (FReplaceMark.equals("6")) {
				FTotalMoney = FReplaceMoney;
			} else {
				FTotalMoney = YssD.mul(FReplaceMoney, YssD.add(1, FPremiumScale));
			}
			
			//设置插入对象
			setPstStatment(pst,portCode,fsecuritycode,sSingleField[0],Amount,FReplaceMark,FPremiumScale,FTotalMoney
					,"",YssFun.toSqlDate(this.sDate),1,pub.getUserCode(),YssFun.formatDatetime(new java.util.Date()),
					pub.getUserCode(),YssFun.formatDatetime(new java.util.Date()),FReplaceMoney);
			pst.addBatch();
		} catch (Exception e) {
			throw new YssException("添加股票篮数据出错！", e);
		} finally {
			
		}
		
	}

	/**shashijie 2013-4-8 STORY 3822 设置插入对象*/
	private void setPstStatment(PreparedStatement pst, String portCode,
			String fsecuritycode, String FISINCode, double amount, String FReplaceMark,
			double fPremiumScale, double fTotalMoney, String FDesc,
			Date sqlDate, int FCheckState, String userCode, String dDate,
			String userCode2, String dDate2, double fReplaceMoney) throws Exception {
		pst.setString(1, portCode); // 组合代码
		//下面是给股票篮中的证券代码加上交易所代码
		pst.setString(2, fsecuritycode);//证券代码
		
		pst.setString(3, FISINCode);//国际产品代码
		pst.setDouble(4, amount);//证券数量
		pst.setString(5, FReplaceMark); // 替代标志
		
		//溢价比例
		pst.setDouble(6, fPremiumScale);
		//总金额 = 替代金额 * （1+溢价比例）
		pst.setDouble(7, fTotalMoney); 
		
		pst.setString(8, FDesc); // 描述
		pst.setDate(9, sqlDate); // 导入日期
		pst.setInt(10, FCheckState); // 审核状态
		pst.setString(11, userCode); // 创建人
		pst.setString(12, dDate); // 创建时间
		pst.setString(13, userCode2); // 复审人
		pst.setString(14, dDate2); // 复审时间
		
		pst.setDouble(15, fReplaceMoney); //替代金额(不算上溢价比例)
	}

	/**shashijie 2013-4-8 STORY 3822 获取证券代码*/
	private String getSecurityCode(String SecurityCode) throws YssException {
		ResultSet rst = null;
		StringBuffer buff = new StringBuffer();
		String FSecurityCode = "";
		try {
			buff.append(" select s.fsecuritycode from ").append(pub.yssGetTableName("tb_para_security"));
			buff.append(" s where s.FCheckState = 1 and substr(s.fsecuritycode,1,length(s.fsecuritycode)-3) = ")
				.append(dbl.sqlString(SecurityCode));

			rst = dbl.openResultSet(buff.toString());
			buff.delete(0, buff.length());
			if (rst.next()) {
				FSecurityCode = rst.getString("fsecuritycode");
				dbl.closeResultSetFinal(rst);
			} else {
				throw new YssException("请检查系统证券信息设置中是否有股票篮中的证券信息【"+SecurityCode+"】！");
			}
		} catch (Exception e) {
			throw new YssException("请检查系统证券信息设置中是否有股票篮中的证券信息【"+SecurityCode+"】！");
		} finally {
			dbl.closeResultSetFinal(rst);
		}
		return FSecurityCode;
	}

	/**shashijie 2013-4-8 STORY 3822 获取SQL*/
	private StringBuffer getInsertSql() {
		StringBuffer buff = new StringBuffer();
		buff.append(" insert into ").append(pub.yssGetTableName("Tb_ETF_StockList"));
		buff.append(" (FPortCode,FSecurityCode,FISINCode,FAmount,FReplaceMark,FPremiumScale,FTotalMoney,FDesc,FDate,");
		buff.append(" FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FReplaceMoney").append(")");
		buff.append(" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		return buff;
	}
}
