package com.yss.main.operdeal.report.reptab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.base.BaseAPOperValue;
import com.yss.main.dayfinish.OffAcctBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * <p>
 * Title:
 * </p>
 * 计算已兌現證券增值貶值及債券收益分佈表（报表15）
 * <p>
 * Description:
 * </p>
 * 此类只产生相应数据，要获取报表的值需要在数据源中配置获取。
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 *
 * @author not attributable
 * @version 1.0
 */
public class TabRealisedValue extends BaseAPOperValue {

	private java.util.Date dBeginDate;//开始日期
	private java.util.Date dEndDate;//结束日期
	private String portCode;//组合代码
	private boolean isCreate = false;//是否生成报表数据，若为否，则不产生数据，仅通过数据源获取
	
	public TabRealisedValue() {
		
	}

	public void init(Object bean) throws YssException {
	      String reqAry[] = null;
	      String reqAry1[] = null;
	      String sRowStr = (String) bean;
	      if (sRowStr.trim().length() == 0) {
	         return;
	      }
	      reqAry = sRowStr.split("\n");
	      reqAry1 = reqAry[0].split("\r");
	      this.dBeginDate = YssFun.toDate(reqAry1[1]);
	      reqAry1 = reqAry[1].split("\r");
	      this.dEndDate = YssFun.toDate(reqAry1[1]);
	      reqAry1 = reqAry[2].split("\r");
	      this.portCode = reqAry1[1];
	     // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出 
	      reqAry1 = reqAry[3].split("\r");
	      this.isCreate = reqAry1[1].equalsIgnoreCase("0")?false:true;
	      // --- MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出  end -------------------
	}

	public Object invokeOperMothed() throws YssException {
	      HashMap valueMap = null;
	      createMatchUpTable();//创建报表对应的存储表
	      valueMap = new HashMap();
	      try {
	    	 if(isCreate){
	    		 //===============增加封账状态的判断，如已封账，返回封账信息 edit by qiuxufeng 20101108 QDV4太平2010年09月16日03_A
				 OffAcctBean offAcct = new OffAcctBean();
				 offAcct.setYssPub(this.pub);
				 String tmpDate = YssFun.formatDate(this.dBeginDate, "yyyy-MM-dd") + "~n~" + YssFun.formatDate(this.dEndDate, "yyyy-MM-dd");
				 String tmpInfo = offAcct.getOffAcctInfo(tmpDate, this.portCode);
				 if(!tmpInfo.trim().equalsIgnoreCase("")) {
					 return "<OFFACCT>" + tmpInfo;
				 }
				 //=================end=================
	    		 getMatchUpValue(valueMap);//如果选择重新生成，调用此方法重新生成报表数据
	    	 }
	      } catch (Exception ex) {
	         throw new YssException(ex.getMessage());
	      }
	      return "";
	}

	/**
	 * 生成报表数据
	 * @param valueMap
	 */
	private void getMatchUpValue(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		try {
			conn.setAutoCommit(false);
			bTrans = true;

			deleteOldData(); // 先删除已有的数据。
			getDetalData(valueMap);//获取证券增值贬值及债券收益明细数据

			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException ex1) {
			throw new YssException(ex1.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/**
	 * 生成证券增值贬值及债券收益明细数据
	 * @param valueMap 
	 * @throws YssException
	 */
	private void getDetalData(HashMap valueMap) throws YssException {
		if (null == valueMap) {
			throw new YssException("未实例化Map！");
		}
		String sqlStr = "";
		String pstSql = "";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pstSql = "insert into " 
            // MS01748 QDV4太平2010年09月16日02_A  太平标准月报18张报表，需要保存，并能批量导出   
            	+ pub.yssGetTableName("Tb_Data_TPRepFifteen") 
            	+ "(FATTRCLSCODE,FCATCODE,FSUBCATCODE,FBARGAINDATE,FPORTCODE,FSECURITYCODE,FSECURITYNAME,FRATE,FTRADEAMOUNT,FACCRUEDINTEREST,FACCRUEDINTERESTB,FTZSY,FTZSYB,FHDSY)"
            	+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			pst = dbl.openPreparedStatement(pstSql);
			
			sqlStr = "select " 
				+ " a.FAttrclscode fattrclscode,b.fcatcode fcatcode,b.fsubcatcode fsubcatcode,to_char(a.FBargainDate, 'yyyy-mm-dd') FBargaindate," 
				+ " a.FPortCode fportcode,a.FSecurityCode fsecuritycode,b.FSecurityName fsecurityname,FRate frate,sum(a.FTradeAmount) ftradeamount,"
				+ " sum(a.FAccruedInterest) FAccruedInterest,sum(FAccruedInterestb) FAccruedInterestb,sum(a.FTZSY) FTZSY,sum(a.FTZSYB) FTZSYB,sum(FHDSY) as FHDSY"
				+ " from (select FAttrclscode,FBargaindate,FPortCode,FSecurityCode,FTradeAmount,FCashAccCode,FAccruedInterest,"
				+ " FBaseCuryRate / FPortCuryRate as FRate,FAccruedInterest * FBaseCuryRate / FPortCuryRate as FAccruedInterestB," 
				+ " (FFactSettleMoney - FAccruedInterest - FVCost) as FTZSY,(FFactSettleMoney - FAccruedInterest - FVCost) *FBaseCuryRate / FPortCuryRate as FTZSYB,"
				+ " (FVCost * FBaseCuryRate / FPortCuryRate - FVBaseCuryCost) as FHDSY from " 
				+ pub.yssGetTableName("tb_data_subtrade") 
				+ " where FCheckState = 1 and (FTradeTypeCode = '02' or FTradeTypeCode = '17')) a" 
				+ " join (select FSecurityCode,FSecurityName,FCatcode,case when fcatcode in ('EQ', 'TR') then substr(fsubcatcode, 1, 2)" 
				+ " when fsubcatcode in ('FI05', 'FI06', 'FI07', 'FI08', 'FI15','FI12', 'FI16', 'FI17', 'FI18') then fsubcatcode ELSE 'TFI' END AS FSUBCATCODE from "
				+ pub.yssGetTableName("tb_para_security") 
				+ " ) b on b.FSecurityCode = a.FSecurityCode left join (select FPortCode, FCashAccCode, FCuryCode from "
				+ pub.yssGetTableName("tb_para_cashaccount") 
				+ " where FCheckState = 1) c on c.FPortCode = a.FPortCode and c.FCashAccCode = a.FCashAccCode"
				+ " where FBargainDate between " + dbl.sqlDate(this.dBeginDate) 
				+ " and " + dbl.sqlDate(this.dEndDate)
				+ getCondition() 
				+ " and a.FPortCode = " + dbl.sqlString(this.portCode)
				+ " group by a.FAttrclscode, b.Fcatcode,b.fsubcatcode,a.FBargainDate,a.FPortCode,a.FSecurityCode,b.FSecurityName,FRate";
			rs = dbl.openResultSet(sqlStr);
			while(rs.next()){
	            pst.setString(1, rs.getString("FATTRCLSCODE"));
	            pst.setString(2, rs.getString("FCATCODE"));
	            pst.setString(3, rs.getString("FSUBCATCODE"));
	            pst.setString(4, rs.getString("FBARGAINDATE"));
	            pst.setString(5, rs.getString("FPORTCODE"));
	            pst.setString(6, rs.getString("FSECURITYCODE"));
	            pst.setString(7, rs.getString("FSECURITYNAME"));
	            pst.setDouble(8, rs.getDouble("FRATE"));
	            pst.setDouble(9, rs.getDouble("FTRADEAMOUNT"));
	            pst.setDouble(10, rs.getDouble("FACCRUEDINTEREST"));
	            pst.setDouble(11, rs.getDouble("FACCRUEDINTERESTB"));
	            pst.setDouble(12, rs.getDouble("FTZSY"));
	            pst.setDouble(13, rs.getDouble("FTZSYB"));
	            pst.setDouble(14, rs.getDouble("FHDSY"));
	            pst.executeUpdate();
			}
		} catch (YssException ex) {
			throw new YssException("生成证券增值贬值及债券收益明细数据出错！", ex);
		} catch (SQLException ex) {
			throw new YssException(ex.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}
	}

	/**
	 * 生成报表数据前，先删除报表已有数据
	 * @throws YssException
	 */
	private void deleteOldData() throws YssException {
		String sqlStr ="";
		try {
			sqlStr =" delete " + pub.yssGetTableName("Tb_Data_TPRepFifteen") +
					" where FPORTCODE=" + dbl.sqlString(this.portCode) + getCondition() +  
					" and to_date(fbargaindate,'yyyy-MM-dd') between " + dbl.sqlDate(this.dBeginDate) + 
					" and " + dbl.sqlDate(this.dEndDate);
			dbl.executeSql(sqlStr);
		} catch (Exception e) {
			throw new YssException("删除已有报表数据出错！",e);
		}
	}

	/**
	 * 创建报表对应的存储表
	 */
	private void createMatchUpTable() throws YssException {
		String strSql = "";
		try {
			if (!dbl.yssTableExist(pub.yssGetTableName("Tb_Data_TPRepFifteen"))) {//若表不存在则创建
				strSql = "create table "
						+ pub.yssGetTableName("Tb_Data_TPRepFifteen")
						+ " (FATTRCLSCODE      VARCHAR2(20),"
						+ " FCATCODE          VARCHAR2(20) not null,"
						+ " FSUBCATCODE       VARCHAR2(20),"
						+ " FBARGAINDATE      VARCHAR2(10),"
						+ " FPORTCODE         VARCHAR2(20),"
						+ " FSECURITYCODE     VARCHAR2(20) not null,"
						+ " FSECURITYNAME     VARCHAR2(100) not null,"
						+ " FRATE             NUMBER,"
						+ " FTRADEAMOUNT      NUMBER,"
						+ " FACCRUEDINTEREST  NUMBER,"
						+ " FACCRUEDINTERESTB NUMBER,"
						+ " FTZSY             NUMBER,"
						+ " FTZSYB            NUMBER,"
						+ " FHDSY             NUMBER)";
				dbl.executeSql(strSql);
			}
			
		} catch (Exception e) {
			throw new YssException("生成已兌現證券增值貶值及債券收益分佈报表对应的存储表出错!",e);
		}
	}

	/**
	 * 获取证券资产品种分类，用于删除和生成报表数据的条件
	 * @return
	 * @throws YssException
	 */
	private String getCondition() throws YssException {
		String sReturn = "";
		ResultSet rs = null;
		String strSql = "";
		try{
			strSql = "select distinct " + dbl.sqlDate(this.dBeginDate) 
				+ " as FBeginDate," + dbl.sqlDate(this.dEndDate) 
				+ " as FEndDate," + dbl.sqlString(this.portCode) 
				+ " as FPortCode,a.fattrclscode,case when b.fcatcode in ('EQ', 'TR') then substr(b.fsubcatcode, 1, 2)"
				+ " when b.fsubcatcode in ('FI05','FI06','FI07') then b.fsubcatcode ELSE 'TFI' END AS FSUBCATCODE from "
				+ pub.yssGetTableName("tb_data_subtrade") 
				+ " a left join "
				+ pub.yssGetTableName("tb_para_security")
				+ " b on a.fsecuritycode = b.fsecuritycode where fbargaindate between " + dbl.sqlDate(this.dBeginDate) 
				+ " and " + dbl.sqlDate(this.dEndDate) 
				+ " and a.fcheckstate = 1 and ftradetypecode = 02 and fportcode = " + dbl.sqlString(this.portCode);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				sReturn += "(fattrclscode = " + dbl.sqlString(rs.getString("fattrclscode")) + 
					" and fsubcatcode = " +  dbl.sqlString(rs.getString("FSUBCATCODE")) + ") or ";
			}
			if(sReturn.length() > 0){
				sReturn = sReturn.substring(0, sReturn.length() - 3);
				sReturn = " and (" + sReturn + ") ";
			}
		}catch(Exception e){
			throw new YssException("获取证券资产品种分类出错!",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sReturn;
	}
}
