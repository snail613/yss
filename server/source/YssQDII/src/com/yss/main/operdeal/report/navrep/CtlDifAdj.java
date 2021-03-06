/**
 * 
 */
package com.yss.main.operdeal.report.navrep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import com.yss.base.BaseAPOperValue;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.operdata.CashPecPayBean;
import com.yss.main.operdata.InvestPayRecBean;
import com.yss.main.operdata.SecIntegratedBean;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdeal.stgstat.BaseStgStatDeal;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * @author ysstech
 *基金TA尾差调整，MS00917,分组估值需求，需要对明细组合进行尾差调整
 *主要有调整尾差，取消调整功能
 *如：当明细组合A,B,C各项汇总之和不等于汇总组合D时，就需要尾差调整。
 *调整公式为：总组合D各项实际数据 — （组合A+组合B+组合C）
 *实际数[产生三笔应收应付数据：估值增值、汇兑损益、费用]
 */
public class CtlDifAdj extends BaseAPOperValue implements IClientOperRequest {
	private Object obj = null;
    private java.util.Date dDate = null;
    private String portCode = "";
    private String doOperType = "";//操作类型：查询、调整尾差、取消调整

    
	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public java.util.Date getdDate() {
		return dDate;
	}

	public void setdDate(java.util.Date dDate) {
		this.dDate = dDate;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getDoOperType() {
		return doOperType;
	}

	public void setDoOperType(String doOperType) {
		this.doOperType = doOperType;
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
        this.dDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[1].split("\r");
        this.portCode = reqAry1[1];
        reqAry1 = reqAry[2].split("\r");
        this.doOperType = reqAry1[1];       
    }
	
	public Object invokeOperMothed() throws YssException {
		String sqlStr = "";
        ResultSet rs = null;
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        boolean bOperFlag = false;//标识是否进行了调整或取消调整，只要存在对表数据进行改写，则为TRUE，此后依此来重新库存统计
        String sqlTaiDif ="";
        PreparedStatement pst1 = null;
        PreparedStatement pst2 = null;
        StringBuffer buffarrSec = new StringBuffer();
        StringBuffer buffarrSecResPay = new StringBuffer();
        StringBuffer buffarrCash = new StringBuffer();
        StringBuffer buffarrCashResPay = new StringBuffer();
        StringBuffer buffarrInvestRecPay = new StringBuffer();
        StringBuffer buffInteger = new StringBuffer();
		double tailFBail =0;//原币尾差金额
		double tailFBaseBail =0;//基础货币尾差金额
		double tailFPortBail =0;//本位币尾差金额
		try {
			if ("adjust".equalsIgnoreCase(doOperType)) {// 调整尾差
				conn.setAutoCommit(false);
	            bTrans = true;
	            
	            //删除证券应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_data_SecRecPay")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
				//删除现金应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_CashPayRec")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
				//删除运营应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_InvestPayRec")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
	            
				//删除综合业务数据
				sqlStr = "delete from "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FExchangeDate = " + dbl.sqlDate(this.dDate)
					+ " and FTradeTypeCode = '82'";
				// 执行sql语句
				dbl.executeSql(sqlStr);
	            
	            //插入前先删除数据
	            sqlTaiDif = " delete from " + pub.yssGetTableName("Tb_Data_TADifAdj") +
	            			" where FDATE = " + dbl.sqlDate(this.dDate);
	            dbl.executeSql(sqlTaiDif);
	            
	            sqlTaiDif = " delete from " + pub.yssGetTableName("tb_data_difadj") +
    						" where FADJDATE = " + dbl.sqlDate(this.dDate);
	            
	            dbl.executeSql(sqlTaiDif);
    
				// 要将调整数据保存到尾差调整数据表中
				sqlTaiDif = "insert into "
						+ pub.yssGetTableName("Tb_Data_TADifAdj")
						+ " (FKEYCODE,FTSFTYPECODE,FDATE,FSumBAL,FSumPortBAL,FSumAmount,FDetFBAL,FDetPortBAL,FDetAmount,"
						+ " FADJBAL,FPORTADJBAL,FAdjAmount,FAdjState,FADJDATE,FadjBalY,FADJPortBalY,FtypeId,"
						+ "FCREATOR,FCREATETIME,FATTRCLSCODE ) values (" // 增加分类代码 by qiuxufeng 20110125
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
				pst = conn.prepareStatement(sqlTaiDif);
				
				sqlTaiDif = " select * from Tb_Data_TADifAdj" +"_" + pub.getUserCode();
				
				rs = dbl.openResultSet(sqlTaiDif);
				
				while(rs.next()){
					pst.setString(1,rs.getString("FKEYCODE"));
					pst.setString(2,rs.getString("FTSFTYPEName"));
					pst.setDate(3,YssFun.toSqlDate(this.dDate));
					pst.setDouble(4,rs.getDouble("SumFBAL"));
					pst.setDouble(5,rs.getDouble("SumPortCuryBAL"));
					pst.setDouble(6,rs.getDouble("SumStoAmount"));
					pst.setDouble(7,rs.getDouble("DetFBAL"));
					pst.setDouble(8,rs.getDouble("DetFPortCuryBAL"));
					pst.setDouble(9,rs.getDouble("DetStoAmount"));
					pst.setDouble(10,rs.getDouble("diffbal"));
					pst.setDouble(11,rs.getDouble("difPORTcuryBAL"));
					pst.setDouble(12,rs.getDouble("difstoAmout"));
					pst.setString(13,rs.getString("FAdjState"));
					pst.setDate(14,rs.getDate("FADJDATE")!=null?rs.getDate("FADJDATE"):YssFun.toSqlDate(this.dDate));
					pst.setDouble(15,rs.getDouble("FadjBalY"));
					pst.setDouble(16,rs.getDouble("FADJPortBalY"));
					pst.setInt(17,YssFun.toInt(rs.getString("typeId")));
					pst.setString(18,pub.getUserCode());
					pst.setString(19,YssFun.formatDatetime(new Date()));
					//--------- 增加分类代码 by qiuxufeng 20110125
					pst.setString(20,null == rs.getString("fattrclscode") ? " " : rs.getString("fattrclscode"));
					//--------- end
					
					pst.executeUpdate();
				}

				conn.commit();
				conn.setAutoCommit(true);
	            bTrans = false;
	            dbl.closeResultSetFinal(rs);
				dbl.endTransFinal(conn,bTrans);
				
				conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("Tb_Data_TADifAdj"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
				sqlTaiDif = " update " + pub.yssGetTableName("Tb_Data_TADifAdj") + " set FADJDATE = ?,FadjBalY = ?,FADJPortBalY = ?,FAdjState =? where "+
							" FKEYCODE = ? and FTSFTYPECODE = ? and FDATE = ?";
				pst = conn.prepareStatement(sqlTaiDif);
				//----------------增加一张调整或者取消调整后状态的存储表----------------------------//
				sqlTaiDif = "insert into "
									+ pub.yssGetTableName("TB_DATA_DIFADJ")
									+ " (FKEYCODE,FTSFTYPECODE,FPORTCODE,FAdjState,FADJDATE,FadjBalY,FADJPortBalY,FtypeId,"
									+ " FCREATOR,FCREATETIME) values ("
									+ "?,?,?,?,?,?,?,?,?,?)";
				pst1 = conn.prepareStatement(sqlTaiDif);
				
				//---add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
				ArrayList alKey = new ArrayList();
				ArrayList alExistKey = new ArrayList();
				
				String stSql = " select FKeyCode, FTsfTypeCode, FDate from " + pub.yssGetTableName("Tb_Data_TADifAdj") + 
				" where FDate = " + dbl.sqlDate(dDate);
				rs = dbl.openResultSet(stSql);
				while(rs.next()){
					alKey.add(rs.getString("FKeyCode") + "," + rs.getString("FTsfTypeCode") + "," + YssFun.toSqlDate(this.dDate));
				}
				
				StringBuffer strBuf = new StringBuffer();
				strBuf
				.append(" select difData.FKeyCode, difData.ftsftypecode, difData.fsubtsftypecode, ")
				.append(" difData.FTsfTypeName, difData.sumFBal, difData.sumPortCuryBal, sumStoAmount, ")
				.append(" difData.detFBAL, difData.detfportcurybal, difData.detStoAmount, difData.difFBal, ")
				.append(" difData.difPortCuryBal, difStoAmout, 'admin' as FCREATOR, ")
				.append(" (select sysdate from dual) as FCREATETIME, difData.typeId as FTypeId, difData.fattrclscode, ")
				.append(" case when e.ftradecury is null then f.fcurycode else e.ftradecury end as ftradecury, ")
				.append(" e.fcatcode, e.fsubcatcode, g.fcurycode ")
				.append(" from (select m.fsecuritycode as FKeyCode, m.ftsftypecode as ftsftypecode, ")
				.append(" m.fsubtsftypecode as fsubtsftypecode, case when m.typeid in ('2') then (select fivpaycatname from tb_base_investpaycat ")				
				.append(" where fcheckstate = 1 and fivpaycatcode = m.fsecuritycode) else ")
				.append(" tranType.Ftsftypename end as Ftsftypename, nvl(m.sumFBAL, 0) as sumFBal, ")
				.append(" nvl(m.sumfportcurybal, 0) as sumPortCuryBal, nvl(sumStoSec.FStorageAmount, 0) as sumStoAmount, ")
				.append(" nvl(m.detFBAL, 0) as detFBAL, nvl(m.detfportcurybal, 0) as detfportcurybal, ")
				.append(" nvl(detStoSec.FStorageAmount, 0) as detStoAmount, (nvl(m.sumFBAL, 0) - nvl(m.detFBAL, 0)) as difFBal, ")
				.append(" (nvl(m.sumfportcurybal, 0) - nvl(m.detfportcurybal, 0)) as difPortCuryBal, ")
				.append(" (nvl(sumStoSec.FStorageAmount, 0) - nvl(detStoSec.FStorageAmount, 0)) as difStoAmout,m.typeId as typeId,  ")				
				.append(" m.fattrclscode from ((select case when detMoney.fsecuritycode is null then sumMoney.fsecuritycode ")
				.append(" else detMoney.fsecuritycode end as fsecuritycode, case when detMoney.ftsftypecode is null then ")
				.append(" sumMoney.ftsftypecode else detMoney.ftsftypecode end as ftsftypecode, case ")
				.append(" when detMoney.fsubtsftypecode is null then sumMoney.fsubtsftypecode ")
				.append(" else detMoney.fsubtsftypecode end as fsubtsftypecode, case when detMoney.detFSTORAGEDATE is null then ")
				.append(" sumMoney.sumFSTORAGEDATE else detMoney.detFSTORAGEDATE end as FSTORAGEDATE, sumMoney.sumFBAL, ")				
				.append(" sumMoney.sumfportcurybal, detMoney.detFBAL, detMoney.detfportcurybal, case when detMoney.typeId is null")
				.append("  then sumMoney.typeId else detMoney.typeId end as typeId, sumMoney.fattrclscode from ")
				.append(" (select fsecuritycode, FSTORAGEDATE as sumFSTORAGEDATE, ftsftypecode, fsubtsftypecode, ")
				.append(" sum(FBAL) as sumFBAL, sum(fportcurybal) as sumfportcurybal, typeId, fattrclscode from (  ")
				.append(" (select a.fsecuritycode, a.FStorageDate, '05' as ftsftypecode, '0501' as fsubtsftypecode, ")
				.append(" a.FStorageCost as FBAL, a.FPortCuryCost as fportcurybal, '0' as typeId, a.fattrclscode ")				
				.append(" from ").append(pub.yssGetTableName("tb_stock_security")).append(" a where a.FStorageDate = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in (select b.fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.Fstorageind <> 1  and a.fcheckstate = 1) ")
				.append(" union (select a.fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode,a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL,sum(a.fportcurybal) as fportcurybal,'0' as typeId,a.fattrclscode from ")
				.append(pub.yssGetTableName("Tb_stock_secrecpay")).append(" a where a.ftsftypecode in ('09') and a.FSTORAGEDATE = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) ")
				.append(" and a.fcheckstate = 1 and a.Fstorageind <> 1 group by a.FSTORAGEDATE,  ")
				.append(" a.ftsftypecode, a.fsubtsftypecode, fsecuritycode, a.fattrclscode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId, ")
				.append(" a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_secrecpay"))
				.append(" a where a.ftsftypecode in ('99') ").append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" a.fsecuritycode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, a.FAccBalance as FBAL, a.FPortCuryBal, '1' as typeId, ")
				.append(" a.fattrclscode from ").append(pub.yssGetTableName("Tb_Stock_Cash"))
				.append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1) union (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('06') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('07') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('99') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.fsecuritycode, a.fstoragedate, '05' as ftsftypecode, '0501' as fsubtsftypecode, ")
				.append(" a.FBAL,a.fportcurybal,'2' as typeId,a.fattrclscode from (select a.fivPayCatCode as fsecuritycode, ")
				.append(" a.FSTORAGEDATE, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, a.fattrclscode from ")
				.append(pub.yssGetTableName("Tb_stock_investpayrec")).append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除费用数据,公共类科目不影响此产净值,只取负债类 */
				.append(" And a.FTsfTypeCode <> '03' ")
				/**end*/
				.append(" and a.fportcode in(select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.fIvPayCatCode, a.fattrclscode ")
				.append(" order by a.fIvPayCatCode) a)) sumSto group by sumSto.FSTORAGEDATE, ")
				.append(" sumSto.ftsftypecode, sumSto.fsubtsftypecode, sumSto.fsecuritycode, ")
				.append(" typeId, fattrclscode order By typeId, sumSto.fsecuritycode, sumSto.ftsftypecode, sumSto.fsubtsftypecode) sumMoney right join ")
				.append(" (select fsecuritycode, FSTORAGEDATE as detFSTORAGEDATE, ftsftypecode, fsubtsftypecode, sum(FBAL) as detFBAL, ")
				.append(" sum(fportcurybal) as detfportcurybal, typeId from ( (select a.fsecuritycode, a.FStorageDate, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, sum(a.FStorageCost) as FBAL, sum(a.FPortCuryCost) as fportcurybal, '0' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_security")).append(" a where a.FStorageDate = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除年初库存 */
				.append(" And a.FYearMonth not like '%00' ")
				/**end*/
				.append(" and a.fportcode in (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" where fporttype = 1 and fcheckstate = 1)) ")
				.append(" and a.Fstorageind <> 1 and a.fcheckstate = 1 group by a.FStorageDate, a.fsecuritycode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_secrecpay")).append(" a where a.ftsftypecode in ('09') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, a.fsecuritycode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId ")
				.append(" from " + pub.yssGetTableName("Tb_stock_secrecpay") + " a where a.ftsftypecode in ('99') and a.FSTORAGEDATE = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in (select fsubcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")).append(" where frelatype = 'PortLink' ")
				.append(" and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, a.fsecuritycode) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, sum(a.FAccBalance) as FBAL, sum(a.FPortCuryBal) as FPortCuryBal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_Stock_Cash"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append("  m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, m.fcashacccode) c on b.FBankAccount = c.FBankAccount ")
				.append(" where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除年初库存 */
				.append(" And a.FYearMonth not like '%00' ")
				/**end*/
				.append(" and a.fportcode in (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by c.fcashacccode, a.FSTORAGEDATE) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ")
				.append(dbl.sqlDate(this.dDate)).append(" group by m.fcashacccode, m.FBankAccount, m.fportcode ")
				.append(" order by m.FBankAccount, m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode ")
				.append(" and a.fportcode = b.fportcode left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('06') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('07') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select c.fcashacccode as fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode,a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '1' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in(select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('99') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select a.fsecuritycode, a.fstoragedate,'05' as ftsftypecode,'0501' as fsubtsftypecode, ")
				.append(" a.FBAL, a.fportcurybal, '2' as typeId from (select a.fivPayCatCode as fsecuritycode, ")
				.append(" a.FSTORAGEDATE, sum(a.FBAL) as FBAL, sum(a.fbasecurybal) as fbasecurybal, ")
				.append(" sum(a.fportcurybal) as fportcurybal from ").append(pub.yssGetTableName("Tb_stock_investpayrec"))
				.append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除费用数据,公共类科目不影响此产净值,只取负债类 */
				.append(" And a.FTsfTypeCode <> '03' ")
				/**end*/
				.append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.fIvPayCatCode order by a.fIvPayCatCode) a)) detSto ")
				.append(" group by detSto.FSTORAGEDATE, detSto.ftsftypecode, detSto.fsubtsftypecode, ")
				.append(" detSto.fsecuritycode, typeId order By typeId, detSto.fsecuritycode, detSto.ftsftypecode, detSto.fsubtsftypecode) detMoney ")
				.append(" on sumMoney.ftsftypecode = detMoney.ftsftypecode and sumMoney.fsubtsftypecode = detMoney.fsubtsftypecode ")
				.append(" and sumMoney.fsecuritycode = detMoney.fsecuritycode) union (")
				.append(" select case when sumMoneys.fsecuritycode is null then detMoneys.fsecuritycode ")
				.append(" else sumMoneys.fsecuritycode end as fsecuritycode, case when sumMoneys.ftsftypecode ")
				.append(" is null then detMoneys.ftsftypecode else sumMoneys.ftsftypecode end as ftsftypecode, ")
				.append(" case when sumMoneys.fsubtsftypecode is null then detMoneys.fsubtsftypecode else ")
				.append(" sumMoneys.fsubtsftypecode end as fsubtsftypecode, case when sumMoneys.sumFSTORAGEDATE ")
				.append(" is null then detMoneys.detFSTORAGEDATE else sumMoneys.sumFSTORAGEDATE end as FSTORAGEDATE, ")
				.append(" sumMoneys.sumFBAL, sumMoneys.sumfportcurybal, detMoneys.detFBAL, detMoneys.detfportcurybal, ")
				.append(" case when sumMoneys.typeId is null then detMoneys.typeId else sumMoneys.typeId end as typeId, ")
				.append(" sumMoneys.fattrclscode from ")
				.append(" (select fsecuritycode, FSTORAGEDATE as sumFSTORAGEDATE, ftsftypecode, fsubtsftypecode, ")
				.append(" sum(FBAL) as sumFBAL, sum(fportcurybal) as sumfportcurybal, typeId, fattrclscode from (  ")
				.append(" (select a.fsecuritycode, a.FStorageDate, '05' as ftsftypecode, '0501' as fsubtsftypecode, ")
				.append(" a.FStorageCost as FBAL, a.FPortCuryCost as fportcurybal, '0' as typeId, a.fattrclscode ")				
				.append(" from ").append(pub.yssGetTableName("tb_stock_security")).append(" a where a.FStorageDate = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in (select b.fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.Fstorageind <> 1  and a.fcheckstate = 1) ")
				.append(" union (select a.fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode,a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL,sum(a.fportcurybal) as fportcurybal,'0' as typeId,a.fattrclscode from ")
				.append(pub.yssGetTableName("Tb_stock_secrecpay")).append(" a where a.ftsftypecode in ('09') and a.FSTORAGEDATE = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) ")
				.append(" and a.fcheckstate = 1 and a.Fstorageind <> 1 group by a.FSTORAGEDATE,  ")
				.append(" a.ftsftypecode, a.fsubtsftypecode, fsecuritycode, a.fattrclscode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId, ")
				.append(" a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_secrecpay"))
				.append(" a where a.ftsftypecode in ('99') ").append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" a.fsecuritycode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, a.FAccBalance as FBAL, a.FPortCuryBal, '1' as typeId, ")
				.append(" a.fattrclscode from ").append(pub.yssGetTableName("Tb_Stock_Cash"))
				.append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1) union (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('06') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('07') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.FCashAccCode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId, a.fattrclscode from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a where a.ftsftypecode in ('99') and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, a.FCashAccCode, a.fattrclscode) union ")
				.append(" (select a.fsecuritycode, a.fstoragedate, '05' as ftsftypecode, '0501' as fsubtsftypecode, ")
				.append(" a.FBAL,a.fportcurybal,'2' as typeId,a.fattrclscode from (select a.fivPayCatCode as fsecuritycode, ")
				.append(" a.FSTORAGEDATE, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, a.fattrclscode from ")
				.append(pub.yssGetTableName("Tb_stock_investpayrec")).append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除费用数据,公共类科目不影响此产净值,只取负债类 */
				.append(" And a.FTsfTypeCode <> '03' ")
				/**end*/
				.append(" and a.fportcode in(select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by a.FSTORAGEDATE, a.fIvPayCatCode, a.fattrclscode ")
				.append(" order by a.fIvPayCatCode) a)) sumSto group by sumSto.FSTORAGEDATE, ")
				.append(" sumSto.ftsftypecode, sumSto.fsubtsftypecode, sumSto.fsecuritycode, ")
				.append(" typeId, fattrclscode order By typeId, sumSto.fsecuritycode, sumSto.ftsftypecode, sumSto.fsubtsftypecode) sumMoneys left join ")
				.append(" (select fsecuritycode, FSTORAGEDATE as detFSTORAGEDATE, ftsftypecode, fsubtsftypecode, sum(FBAL) as detFBAL, ")
				.append(" sum(fportcurybal) as detfportcurybal, typeId from ( (select a.fsecuritycode, a.FStorageDate, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, sum(a.FStorageCost) as FBAL, sum(a.FPortCuryCost) as fportcurybal, '0' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_security")).append(" a where a.FStorageDate = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" where fporttype = 1 and fcheckstate = 1)) ")
				.append(" and a.Fstorageind <> 1 and a.fcheckstate = 1 group by a.FStorageDate, a.fsecuritycode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_secrecpay")).append(" a where a.ftsftypecode in ('09') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, a.fsecuritycode) union ")
				.append(" (select a.fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '0' as typeId ")
				.append(" from " + pub.yssGetTableName("Tb_stock_secrecpay") + " a where a.ftsftypecode in ('99') and a.FSTORAGEDATE = ")
				.append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in (select fsubcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")).append(" where frelatype = 'PortLink' ")
				.append(" and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode, a.fsecuritycode) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, '05' as ftsftypecode, ")
				.append(" '0501' as fsubtsftypecode, sum(a.FAccBalance) as FBAL, sum(a.FPortCuryBal) as FPortCuryBal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_Stock_Cash"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append("  m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, m.fcashacccode) c on b.FBankAccount = c.FBankAccount ")
				.append(" where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				.append(" and a.fportcode in (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 ")
				.append(" and a.Fstorageind <> 1 group by c.fcashacccode, a.FSTORAGEDATE) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ")
				.append(dbl.sqlDate(this.dDate)).append(" group by m.fcashacccode, m.FBankAccount, m.fportcode ")
				.append(" order by m.FBankAccount, m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode ")
				.append(" and a.fportcode = b.fportcode left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('06') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select c.fcashacccode as fsecuritycode, a.FSTORAGEDATE, a.ftsftypecode, ")
				.append(" a.fsubtsftypecode, sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, ")
				.append(" '1' as typeId from ").append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('07') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select c.fcashacccode as fsecuritycode,a.FSTORAGEDATE,a.ftsftypecode,a.fsubtsftypecode, ")
				.append(" sum(a.FBAL) as FBAL, sum(a.fportcurybal) as fportcurybal, '1' as typeId from ")
				.append(pub.yssGetTableName("Tb_stock_cashpayrec"))
				.append(" a left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" group by m.fcashacccode, m.FBankAccount, m.fportcode order by m.FBankAccount, ")
				.append(" m.fportcode, m.fcashacccode) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode left join ")
				.append(" (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_CashAccount")).append(" m where m.FStartDate <= ").append(dbl.sqlDate(this.dDate))
				.append(" and fportcode in(select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" b where b.fporttype = '1' and b.fcheckstate = 1) group by m.fcashacccode, ")
				.append(" m.FBankAccount, m.fportcode order by m.FBankAccount, m.fportcode, ")
				.append(" m.fcashacccode) c on b.FBankAccount = c.FBankAccount where a.ftsftypecode in ('99') ")
				.append(" and a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in(select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by c.fcashacccode, a.FSTORAGEDATE, a.ftsftypecode, a.fsubtsftypecode) union ")
				.append(" (select a.fsecuritycode, a.fstoragedate,'05' as ftsftypecode,'0501' as fsubtsftypecode, ")
				.append(" a.FBAL, a.fportcurybal, '2' as typeId from (select a.fivPayCatCode as fsecuritycode, ")
				.append(" a.FSTORAGEDATE, sum(a.FBAL) as FBAL, sum(a.fbasecurybal) as fbasecurybal, ")
				.append(" sum(a.fportcurybal) as fportcurybal from ").append(pub.yssGetTableName("Tb_stock_investpayrec"))
				.append(" a where a.FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate))
				/**shashijie 2012-8-21 BUG 5229 排除费用数据,公共类科目不影响此产净值,只取负债类 */
				.append(" And a.FTsfTypeCode <> '03' ")
				/**end*/
				.append(" and a.fportcode in ")
				.append(" (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
				.append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from ")
				.append(pub.yssGetTableName("Tb_Para_Portfolio"))
				.append(" where fporttype = 1 and fcheckstate = 1)) and a.fcheckstate = 1 and a.Fstorageind <> 1 ")
				.append(" group by a.FSTORAGEDATE, a.fIvPayCatCode order by a.fIvPayCatCode) a)) detSto ")
				.append(" group by detSto.FSTORAGEDATE, detSto.ftsftypecode, detSto.fsubtsftypecode, ")
				.append(" detSto.fsecuritycode, typeId order By typeId, detSto.fsecuritycode, detSto.ftsftypecode, detSto.fsubtsftypecode) detMoneys ")
				.append(" on sumMoneys.ftsftypecode = detMoneys.ftsftypecode and sumMoneys.fsubtsftypecode = detMoneys.fsubtsftypecode ")
				.append(" and sumMoneys.fsecuritycode = detMoneys.fsecuritycode ")
				.append(" )order by typeId, fsecuritycode, ftsftypecode, fsubtsftypecode) m ")
				.append(" left join (select FTsfTypeCode, FTsfTypeName from Tb_Base_TransferType where fcheckstate = 1) ")
				.append(" tranType on tranType.FTsfTypeCode = m.FTsfTypeCode left join ")
				.append(" (select fsecuritycode, fstoragedate, sum(FStorageAmount) as FStorageAmount, ")
				.append(" '05' as FTSFTYPECODE, '0501' as fsubtsftypecode from ").append(pub.yssGetTableName("Tb_stock_security"))
				.append(" where FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and fportcode in ( ")
				.append(" select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" b where b.fportcode in ")
				.append(" (select fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" where fporttype = '1' ")
				.append(" and fcheckstate = 1)) and Fstorageind <> 1  and fcheckstate = 1 ")
				.append(" group by fsecuritycode, fstoragedate) sumStoSec on sumStoSec.fsecuritycode = m.fsecuritycode ")
				.append(" and sumStoSec.FTSFTYPECODE = m.FTSFTYPECODE and sumStoSec.FSUBTSFTYPECODE = m.FSUBTSFTYPECODE left join ")
				.append(" (select fsecuritycode, fstoragedate, sum(FStorageAmount) as FStorageAmount, ")
				.append(" '05' as FTSFTYPECODE, '0501' as FSUBTSFTYPECODE from ").append(pub.yssGetTableName("Tb_stock_security"))
				.append(" where FSTORAGEDATE = ").append(dbl.sqlDate(this.dDate)).append(" and fportcode in ")
				.append(" (select b.fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio")).append(" b where b.fcheckstate = 1 ")
				.append(" and b.fportcode in (select fsubcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio_RelaShip"))
		        .append(" where frelatype = 'PortLink' and FCheckState = 1 and fportcode in ")
		        .append(" (select fportcode from ").append(pub.yssGetTableName("Tb_Para_Portfolio"))
		        .append(" where fporttype = 1 and fcheckstate = 1))) ")
		        .append(" and Fstorageind <> 1 and fcheckstate = 1 group by fsecuritycode, fstoragedate) detStoSec ")
		        .append(" on detStoSec.fsecuritycode = m.fsecuritycode and detStoSec.FTSFTYPECODE = m.FTSFTYPECODE ")
		        .append(" and detStoSec.FSUBTSFTYPECODE = m.FSUBTSFTYPECODE ")
		        .append(" where ((nvl(m.sumFBAL, 0) - nvl(m.detFBAL, 0) <> 0 or nvl(m.sumfportcurybal, 0) - nvl(m.detfportcurybal, 0) <> 0)) ")
		        .append(" order by typeId, FKeyCode, ftsftypecode, fsubtsftypecode) difData ")
		        .append(" left join (select FSecurityCode, FTradecury, fcatcode, fsubcatcode from ").append(pub.yssGetTableName("tb_para_security"))
		        .append(" where FCheckState = 1) e on difData.fkeycode = e.FSecurityCode ")
		        .append(" left join (select * from ").append(pub.yssGetTableName("tb_para_cashaccount"))
		        .append(" where FCheckState = 1) f on difData.fkeycode = f.FCashAcccode ")
		        .append(" left join (select k.fivpaycatcode,k.fcashacccode,j.fcurycode from (select c.fivpaycatcode, c.fcashacccode from ")
		        .append(" (select max(a.fstartdate) as fstartdate,a.fivpaycatcode, a.fportcode from ")
		        .append(pub.yssGetTableName("Tb_Para_InvestPay"))
		        .append(" a where a.FCheckState = 1 and a.fstartdate <= ").append(dbl.sqlDate(this.dDate))
		        .append(" and a.fportcode = ").append(dbl.sqlString(this.portCode))
		        .append(" group by a.fivpaycatcode, a.fportcode) b join ")
		        .append(" (select * from ").append(pub.yssGetTableName("Tb_Para_InvestPay"))
		        .append(" where FCheckState = 1 and fportcode = ").append(dbl.sqlString(this.portCode))
		        .append(" ) c on c.fstartdate = b.fstartdate and c.fivpaycatcode = b.fivpaycatcode ) k ")
		        .append(" join (select v.fcashacccode, v.fcurycode from ")
		        .append(" (select max(x.fstartdate) as fstartdate,x.fcashacccode from ").append(pub.yssGetTableName("Tb_Para_CashAccount"))
		        .append(" x where x.FCheckState = 1 and x.fstartdate <= ").append(dbl.sqlDate(this.dDate))
		        .append(" group by x.fcashacccode) y join ")
		        .append(" (select fcashacccode,fstartdate,fcurycode from ").append(pub.yssGetTableName("Tb_Para_CashAccount"))
		        .append(" where FCheckState = 1 ) v ")
		        .append(" on v.fstartdate = y.fstartdate and v.fcashacccode = y.fcashacccode) j ")
		        .append(" on k.fcashacccode = j.fcashacccode )  g on g.fivpaycatcode = difData.fkeycode ")
		        .append("where difData.fkeycode is not null ");
				sqlStr = strBuf.toString();
				//---add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
				//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//				sqlStr = " select a.fkeycode,a.ftsftypecode,a.fdate,a.fsumbal, a.fsumportbal,a.fsumamount,a.fdetfbal,a.fdetportbal,a.fdetamount," +
//						 " a.fadjbal,a.fportadjbal,a.fadjamount,a.fadjstate,a.fadjbaly,a.fadjportbaly,a.ftypeid,"+
//						 " a.fattrclscode," + // 增加所属分类代码  by qiuxufeng 20110125
//						 "case when e.ftradecury is null then f.fcurycode else e.ftradecury end as ftradecury,e.fcatcode,e.fsubcatcode,g.fcurycode from " +
//						 pub.yssGetTableName("Tb_Data_TADifAdj") 
//						 +" a left join (select FSecurityCode, FTradecury, fcatcode, fsubcatcode from "
//						 + pub.yssGetTableName("tb_para_security")
//						 +" where FCheckState = 1) e on a.fkeycode = e.FSecurityCode"
//						 +" left join (select * from " + pub.yssGetTableName(" ")
//						 +" where FCheckState = 1) f on a.fkeycode = f.FCashAcccode "
//						 //------ modify by wangzuochun 2011.03.03  BUG #1159 待摊业务处理开始日期进行业务处理后，不会产生资金调拨和运营应收应付数据 
//						 //------ 特此说明：Tb_Para_InvestPay中的fcurycode字段已经不用但字段还保留，如果要取得货币代码必须要根据现金帐户字段去关联现金帐户表取得货币代码；
//						 +" left join " 
//						 +"(select k.fivpaycatcode,k.fcashacccode,j.fcurycode from "
//						 +"(select c.fivpaycatcode, c.fcashacccode from (select max(a.fstartdate) as fstartdate,a.fivpaycatcode,"
//						 +" a.fportcode from " + pub.yssGetTableName("Tb_Para_InvestPay")
//						 +" a where a.FCheckState = 1 and a.fstartdate <= " + dbl.sqlDate(this.dDate)
//						 +" and a.fportcode = " + dbl.sqlString(this.portCode)
//						 +" group by a.fivpaycatcode, a.fportcode) b join (select * from " + pub.yssGetTableName("Tb_Para_InvestPay")
//						 +" where FCheckState = 1 and fportcode =  "+ dbl.sqlString(this.portCode)
//						 +" ) c on c.fstartdate = b.fstartdate and c.fivpaycatcode = b.fivpaycatcode) k " 
//						 +" join (select v.fcashacccode, v.fcurycode from " 
//						 +"(select max(x.fstartdate) as fstartdate,x.fcashacccode from " + pub.yssGetTableName("Tb_Para_CashAccount") 
//						 +" x where x.FCheckState = 1 and x.fstartdate <= " + dbl.sqlDate(this.dDate)
//						 +" group by x.fcashacccode)y join (select fcashacccode,fstartdate,fcurycode from " + pub.yssGetTableName("Tb_Para_CashAccount")
//						 + " where FCheckState = 1 ) v on v.fstartdate = y.fstartdate and v.fcashacccode = y.fcashacccode) j on k.fcashacccode = j.fcashacccode"
//						 + ")  g on g.fivpaycatcode = a.fkeycode"
//						 + " where FDATE = " + dbl.sqlDate(dDate) + " order by a.FTYPEID";
//						 //------------------------------- BUG #1159 待摊业务处理开始日期进行业务处理后，不会产生资金调拨和运营应收应付数据 ---------------------------------------//
				//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
				/**shashijie 2012-8-24 BUG 5334 */
				//System.out.println(sqlStr);
				/**end*/
				rs = dbl.openResultSet(sqlStr);
				while(rs.next()){
					bOperFlag=true;//标明执行了操作
					SecPecPayBean secPecPayData = new SecPecPayBean(); //对应　证券应收应付款　表
					CashPecPayBean cashPayData = new CashPecPayBean();//对应 现金应收应付款 表
					InvestPayRecBean investPayData = new InvestPayRecBean();//对应 运营应收应付款表
					secPecPayData.setYssPub(this.pub);
					cashPayData.setYssPub(pub);
					investPayData.setYssPub(pub);
					//---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
					tailFBail = rs.getDouble("DIFFBAL");//原币差额
					tailFPortBail = rs.getDouble("DIFPORTCURYBAL");//本币差额
					//---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
	
					if(tailFBail!=0 ||tailFPortBail!=0){//原币或者本币尾差不等于0，需要调整。
						if(0==rs.getInt("FTYPEID")){//需要调整证券应收应付
							SecPecPayBean secFilter = new SecPecPayBean();//查询条件
							secFilter.setStrSecurityCode(rs.getString("FKeyCode"));
							secFilter.setTransDate(this.dDate);
							secFilter.setStrPortCode(this.portCode);//通过页面传来的组合代码，表明将尾差调入该组合
							//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//							String strSql = " select FSubTsfTypeCode from " + pub.yssGetTableName("tb_stock_secrecpay") + " where FCheckState =1" +
//											" and FStorageDate = " + dbl.sqlDate(YssFun.addDay(this.dDate,0)) +
//											" and FSecurityCode =" + dbl.sqlString(rs.getString("FKeyCode")) +
//											" and FPortCode =" + dbl.sqlString(this.portCode);
							//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
							//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
							if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("成本")){
								secFilter.setStrTsfTypeCode("05");
								secFilter.setStrSubTsfTypeCode("05" + rs.getString("fcatcode"));
                            //edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
							}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("估值增值")){
								secFilter.setStrTsfTypeCode("09");
								//add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
								secFilter.setStrSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
								//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//								strSql += " and FTsfTypeCode = '09'";
//								ResultSet rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									secFilter.setStrSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									secFilter.setStrSubTsfTypeCode("09" + rs.getString("fcatcode"));
//								}
//								dbl.closeResultSetFinal(rst);
								//---delete by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
                            //edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
							}else if (rs.getString("FTSFTYPENAME").equalsIgnoreCase("应收款项")){//add by yanghaiming 20101016 MS01690 QDV4汇添富2010年09月03日01_A 
								secFilter.setStrTsfTypeCode("06");
								//add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
								secFilter.setStrSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
//								strSql += " and FTsfTypeCode = '06'";
//								ResultSet rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									secFilter.setStrSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									secFilter.setStrSubTsfTypeCode("06" + rs.getString("fcatcode"));
//								}
//								dbl.closeResultSetFinal(rst);
							}else{
								//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
								secFilter.setStrTsfTypeCode(rs.getString("FTsfTypeCode"));
								//add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
								secFilter.setStrSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//								strSql += " and FTsfTypeCode = '99'";
//								ResultSet rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									secFilter.setStrSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									secFilter.setStrSubTsfTypeCode("9905"+ rs.getString("fcatcode"));
//								}
//								dbl.closeResultSetFinal(rst);
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
							}
                            //edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
					        if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("成本")){
					        	SecIntegratedBean secInteData = new SecIntegratedBean();
					        	secInteData.setSTsfTypeCode("05");
					        	secInteData.setSSubTsfTypeCode(secFilter.getStrSubTsfTypeCode());
					        	this.setIntegratedBeanData(rs,secInteData);//设置证券成本
					        	
					        	buffarrSec.append(secInteData.buildRowStrForParse()).append("\f\f");
					        }else{
					        	secFilter.setStrCuryCode(rs.getString("ftradecury"));
								secFilter.setStrFStockInd(-1);//入账标识
								secPecPayData.setFilterType(secFilter);
								secPecPayData.getSetting();//先查询要插入的数据，如果已经存在，则要先删除
								if(secPecPayData.getStrNum()!=null&&secPecPayData.getStrNum().trim().length()>0){
									//有重复数据，先删除掉
									sqlStr = "delete from " +
			                        pub.yssGetTableName("Tb_data_SecRecPay") +
			                        " where FNum = " + dbl.sqlString(secPecPayData.getStrNum());
			                    //执行sql语句
			                    dbl.executeSql(sqlStr);
								}
								
								secPecPayData.setStrNum("");//插入新的应收应付数据，编号为空，系统会自动生成新的编号
								secPecPayData.setStrSecurityCode(rs.getString("FKeyCode"));
								secPecPayData.setTransDate(this.dDate);
								secPecPayData.setOldTransDate(this.dDate);
								secPecPayData.setStartDate(this.dDate);
								secPecPayData.setEndDate(this.dDate);
								secPecPayData.setStrPortCode(this.portCode);//通过页面传来的组合代码，表明将尾差调入该组合
								secPecPayData.setStrTsfTypeCode(secFilter.getStrTsfTypeCode());
								secPecPayData.setStrSubTsfTypeCode(secFilter.getStrSubTsfTypeCode());
								secPecPayData.setStrCuryCode(rs.getString("ftradecury"));
								secPecPayData.setStrFStockInd(-1);//入账标识
								secPecPayData.setDesc("基金TA尾差调整数据");//加上描述信息
								secPecPayData.setInOutType(1);//方向为流入。因为是将尾差调整入该组合
								secPecPayData.setMoney(tailFBail);
								secPecPayData.setMMoney(tailFBail);
								secPecPayData.setVMoney(tailFBail);
								secPecPayData.setBaseCuryMoney(tailFBaseBail);
								secPecPayData.setMBaseCuryMoney(tailFBaseBail);
								secPecPayData.setVBaseCuryMoney(tailFBaseBail);
								secPecPayData.setPortCuryMoney(tailFPortBail);
								secPecPayData.setMPortCuryMoney(tailFPortBail);
								secPecPayData.setVPortCuryMoney(tailFPortBail);
								//secPecPayData.setInvestType("C");//xuqiji 20100610 
								secPecPayData.setCheckState(1);//调整后的数据为已审核状态
								secPecPayData.checkStateId =1;
								secPecPayData.creatorCode = pub.getUserCode();
						        secPecPayData.creatorTime = YssFun.formatDate(new java.util.Date());
						        secPecPayData.checkUserCode = pub.getUserCode();
						        secPecPayData.checkTime = YssFun.formatDate(new java.util.Date());
						        
					        	buffarrSecResPay.append(secPecPayData.buildRowStrForParse()).append("\f\f");
					        }
					        
						}else if(1==rs.getInt("FTYPEID")){//需要调整现金应收应付表

					        //--------- 增加分类代码 by qiuxufeng 20110125
					        cashPayData.setStrAttrClsCode(rs.getString("FATTRCLSCODE"));
					        //-----------end
					        
							//-------------------总账的现金账户去调整分账组合的现金账户信息-------------------//
							String sSql = " select c.* from (select b.fcashacccode,b.fcurycode,b.fbankaccount from (select max(m.fstartdate) as fstartdate," +
										  " m.fcashacccode,FBankAccount,fportcode from " +
										  pub.yssGetTableName("Tb_Para_CashAccount") +
										  " m where m.FStartDate <= " + dbl.sqlDate(this.dDate) +
										  " and m.fcheckstate = 1 and m.fportcode = " + dbl.sqlString(this.portCode) +
										  " group by m.fcashacccode, m.FBankAccount, m.fportcode " +
										  " order by m.FBankAccount, m.fportcode, m.fcashacccode) a " +
										  " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
										  " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portCode) +
										  " ) b on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode and a.fstartdate = b.fstartdate) c" +
										  " join (select * from " + pub.yssGetTableName("Tb_Para_CashAccount") +
										  " where fcheckstate = 1 and fcashacccode = " + dbl.sqlString(rs.getString("FKeyCode")) +
										  " ) d on c.FBankAccount = d.FBankAccount";
							
							ResultSet rst = dbl.openResultSet(sSql);
							String sCahsAccCode = "";
							String sCuryCode = "";
							if(rst.next()){
								sCahsAccCode = rst.getString("fcashacccode");
								sCuryCode = rst.getString("fcurycode");
							}else{
								sCahsAccCode = rs.getString("FKeyCode");
								sCuryCode = rs.getString("ftradecury");
							}
							dbl.closeResultSetFinal(rst);
							//----------------------------end--------------------------//
							CashPecPayBean cashFilter = new CashPecPayBean();
							cashFilter.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
							cashFilter.setTradeDate(this.dDate);
							cashFilter.setCashAccCode(sCahsAccCode);
							
							String strSql = " select FSubTsfTypeCode from " + pub.yssGetTableName("tb_stock_cashpayrec") + " where FCheckState =1" +
											" and FStorageDate = " + dbl.sqlDate(YssFun.addDay(this.dDate,0)) +
											" and FCashAcccode =" + dbl.sqlString(sCahsAccCode) +
											" and FPortCode =" + dbl.sqlString(this.portCode);
							//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
							if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("成本")){
								//cashFilter.setTsfTypeCode("05");
								//cashFilter.setSubTsfTypeCode("05DE");
							//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B	
							}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("汇兑损益")){
								cashFilter.setTsfTypeCode("99");
								//add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
								cashFilter.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
								//------ modify by wangzuochun 2010.09.10  MS01695    TA尾差调整报表在调整现金汇兑损益时有问题    QDV4赢时胜上海2010年9月3日01_B    
					        	//------ 汇兑损益99类型下有3种子类型分别为：9905DE,9906DE,9907DE,
								//------ 以下处理是将总帐户99，9905DE本币与分帐户99，9905DE汇总的本币进行比较,若存在尾差则产生一笔99，9905DE类型的现金应收应付；
								//------ 同理，99，9906DE；99，9907DE，也做同样处理；
								
								//strSql += " and FTsfTypeCode = '99'";
								
//								rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									cashFilter.setSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									cashFilter.setSubTsfTypeCode("9905DE");
//								}
//								dbl.closeResultSetFinal(rst);
								
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//								String strSqlSum = null;
//								String strSqlDet = null;
//								
//								String strAry[] = {"'9905DE'","'9906DE'","'9907DE'","'9906OH'","'9907OH'",
//													"'9912'","'9913'","'9906PF'","'9907PF'","'9906TD'",
//													"'9906TD01'","'9906TD02'","'9906TD03'","'9907TD'",
//													//edit by songjie 2011.04.19 BUG 1696 QDV4汇添富2011年04月14日01_B 添加调拨子类型代码9906CE、9907CE
//													"'9907TD01'","'9907TD02'","'9907TD03'","'9906DV'","'9906CE'","'9907CE'"}; //------ modify by wangzuochun 2010.12.03 BUG #553  TA尾差调整报表中，对现金汇兑损益的调整缺少了“9906DV”-应收股利汇兑损益
//								ResultSet rsSum = null;
//								ResultSet rsDet = null;
//								BigDecimal detPortBal = null;
//								BigDecimal sumPortBal = null;
//								String sSumCahsAcc = rs.getString("FKeyCode");
//								
//								
//								for (int i = 0; i < strAry.length; i++ ){
//									//查询指定条件的总帐户数据，主要用来获取本币金额
//									strSqlSum = " select a.* from " + pub.yssGetTableName("Tb_stock_cashpayrec") + " a "
//											+ " where a.ftsftypecode = '99' and a.fsubtsftypecode = " + strAry[i]
//											+ " and a.FSTORAGEDATE = " + dbl.sqlDate(this.dDate) + " and a.fcashacccode = " + sSumCahsAcc
//											+ " and fportcode in (select b.fportcode from " + pub.yssGetTableName("Tb_Para_Portfolio") + " b "
//											+ " where b.fporttype = '1' and b.fcheckstate = 1) and a.fcheckstate = 1 and a.Fstorageind <> 1 ";
//									//查询汇总后的分账户数据，主要用来获取汇总后的分账户本币金额
//									strSqlDet = " select c.fcashacccode,a.fstoragedate,a.ftsftypecode,a.fsubtsftypecode,"
//										  + " sum(a.fbal) as FBal,sum(a.fportcurybal) as FPortCuryBal  from " 
//										  + pub.yssGetTableName("tb_stock_cashpayrec") + " a " 
//										  
//										  /* 根据现金帐户代码和组合代码查找出现金应收应付库存数据对应的银行帐号 */
//										  + " left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from "
//										  + pub.yssGetTableName("Tb_Para_CashAccount") + " m where m.FStartDate <= " + dbl.sqlDate(this.dDate)
//										  + " group by m.fcashacccode, m.FBankAccount, m.fportcode "
//										  + " order by m.FBankAccount, m.fportcode, m.fcashacccode) b "
//										  + " on a.fcashacccode = b.fcashacccode and a.fportcode = b.fportcode "
//										  
//										  /* 查找总账与分账账户对应的现金帐户数据，根据同一银行账号 */
//										  + " left join (select max(m.fstartdate), m.fcashacccode, FBankAccount, fportcode from "
//										  + pub.yssGetTableName("Tb_Para_CashAccount") + " m where m.FStartDate <= " + dbl.sqlDate(this.dDate)
//										  + " and fportcode in (select b.fportcode from " + pub.yssGetTableName("Tb_Para_Portfolio") + " b "
//										  + " where b.fporttype = '1' and b.fcheckstate = 1) "
//										  + " group by m.fcashacccode, m.FBankAccount, m.fportcode "
//										  + " order by m.FBankAccount, m.fportcode, m.fcashacccode) c on b.FBankAccount = c.FBankAccount "
//										  
//										  + " where a.ftsftypecode = '99' and a.fsubtsftypecode = " +  strAry[i]
//										  + " and a.FSTORAGEDATE = " + dbl.sqlDate(this.dDate) + " and c.fcashacccode = " + sSumCahsAcc
//										  + " and a.fportcode in (select fsubcode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
//										  + " where frelatype = 'PortLink' and FCheckState = 1 and fportcode in (select fportcode from "
//										  + pub.yssGetTableName("Tb_Para_Portfolio") + " where fporttype = 1 and fcheckstate = 1)) "
//										  + " and a.fcheckstate = 1 and a.fstorageind <> 1 " 
//										  + " group by c.fcashacccode, a.fstoragedate, a.ftsftypecode, a.fsubtsftypecode ";
//									
//									rsSum = dbl.openResultSet(strSqlSum);
//									//判断总帐户对应的主类型和子类型是否有数据；
//									if (rsSum.next()){
//										sumPortBal = new BigDecimal(Double.toString(rsSum.getDouble("FPortcurybal")));//获取总帐户本币金额
//									} else sumPortBal = new BigDecimal(0); //分账产生的汇兑损益在总账中无此类型记录则默认为0 by qiuxufeng 20110125
//										rsDet = dbl.openResultSet(strSqlDet);
//										//判断分账户对应的主类型和子类型是否有数据；
//										if (rsDet.next()){
//											detPortBal = new BigDecimal(Double.toString(rsDet.getDouble("FPortCuryBal")));//获取分账户汇总后的本币金额
//											//若总帐户本币金额不等于分账户汇总后的本币金额，则存在尾差，需产生相应类型的现金应收应付数据；
//											//edit by songjie 2011.04.20 BUG 1696 QDV4汇添富2011年04月14日01_B
//											if (detPortBal.subtract(sumPortBal).doubleValue() != 0){
//												cashFilter.setSubTsfTypeCode(rsDet.getString("FSubTsfTypeCode"));
//												
//												cashFilter.setCuryCode(sCuryCode);
//												cashFilter.setStockInd(-1);//入账标识
//												cashPayData.setFilterType(cashFilter);
//												cashPayData.getSetting();
//												if(cashPayData.getNum()!=null&&cashPayData.getNum().trim().length()>0){//已经存在数据，则先删除，避免重复产生数据
//													//删除现金应收应付的尾差调整数据
//													sqlStr = "delete from "
//															+ pub.yssGetTableName("Tb_Data_CashPayRec")
//															+ " where FNum = " +dbl.sqlString(cashPayData.getNum());										
//													// 执行sql语句
//													dbl.executeSql(sqlStr);
//												}
//												cashPayData.setNum("");//编号清空，插入时才会自动生成新的编号
//												cashPayData.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
//												cashPayData.setTradeDate(this.dDate);
//												cashPayData.setCashAccCode(sCahsAccCode);
//												cashPayData.setTsfTypeCode(cashFilter.getTsfTypeCode());
//												cashPayData.setSubTsfTypeCode(cashFilter.getSubTsfTypeCode());
//												cashPayData.setCuryCode(sCuryCode);
//												cashPayData.setStockInd(-1);//入账标识
//												cashPayData.setDesc("基金TA尾差调整数据");//加上描述信息
//												cashPayData.setInOutType(1);//方向为流入。因为是将尾差调整入该组合
//												cashPayData.setMoney(tailFBail);							
//												cashPayData.setBaseCuryMoney(tailFBaseBail);							
//												cashPayData.setPortCuryMoney(sumPortBal.subtract(detPortBal).doubleValue());							
//												cashPayData.checkStateId=1;//调整后的数据为已审核状态
//												cashPayData.creatorCode = pub.getUserCode();
//										        cashPayData.creatorTime = YssFun.formatDate(new java.util.Date());
//										        cashPayData.checkUserCode = pub.getUserCode();
//										        cashPayData.checkTime = YssFun.formatDate(new java.util.Date());
//										        
//										        buffarrCashResPay.append(this.buildRowcashPayStr(cashPayData)).append("\f\f");
//											}
//										}
//										//---edit by songjie 2011.04.20 BUG 1696 QDV4汇添富2011年04月14日01_B---//
//										else{
//											//如果总账中有数据，但分账中没数据的话，也要生成调整组合对应的调整数据
//											if(sumPortBal.doubleValue() != 0){
//												cashFilter.setSubTsfTypeCode(strAry[i].substring(1,strAry[i].length() -1));
//												
//												cashFilter.setCuryCode(sCuryCode);
//												cashFilter.setStockInd(-1);//入账标识
//												cashPayData.setFilterType(cashFilter);
//												cashPayData.getSetting();
//												if(cashPayData.getNum()!=null&&cashPayData.getNum().trim().length()>0){//已经存在数据，则先删除，避免重复产生数据
//													//删除现金应收应付的尾差调整数据
//													sqlStr = "delete from "
//															+ pub.yssGetTableName("Tb_Data_CashPayRec")
//															+ " where FNum = " +dbl.sqlString(cashPayData.getNum());										
//													// 执行sql语句
//													dbl.executeSql(sqlStr);
//												}
//												cashPayData.setNum("");//编号清空，插入时才会自动生成新的编号
//												cashPayData.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
//												cashPayData.setTradeDate(this.dDate);
//												cashPayData.setCashAccCode(sCahsAccCode);
//												cashPayData.setTsfTypeCode(cashFilter.getTsfTypeCode());
//												cashPayData.setSubTsfTypeCode(cashFilter.getSubTsfTypeCode());
//												cashPayData.setCuryCode(sCuryCode);
//												cashPayData.setStockInd(-1);//入账标识
//												cashPayData.setDesc("基金TA尾差调整数据");//加上描述信息
//												cashPayData.setInOutType(1);//方向为流入。因为是将尾差调整入该组合
//												cashPayData.setMoney(tailFBail);							
//												cashPayData.setBaseCuryMoney(tailFBaseBail);							
//												cashPayData.setPortCuryMoney(sumPortBal.doubleValue());							
//												cashPayData.checkStateId=1;//调整后的数据为已审核状态
//												cashPayData.creatorCode = pub.getUserCode();
//										        cashPayData.creatorTime = YssFun.formatDate(new java.util.Date());
//										        cashPayData.checkUserCode = pub.getUserCode();
//										        cashPayData.checkTime = YssFun.formatDate(new java.util.Date());
//										        
//										        buffarrCashResPay.append(this.buildRowcashPayStr(cashPayData)).append("\f\f");
//											}
//										}
//										//---edit by songjie 2011.04.20 BUG 1696 QDV4汇添富2011年04月14日01_B---//
//										dbl.closeResultSetFinal(rsDet);
////									}						
//									dbl.closeResultSetFinal(rsSum);
//								}
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
								//------------------------------------ MS01695 ------------------------------------//
							//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B	
							}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("应收款项")){
								cashFilter.setTsfTypeCode("06");
								//add by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B
								cashFilter.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//								strSql += " and FTsfTypeCode = '06'";
//								rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									cashFilter.setSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									cashFilter.setSubTsfTypeCode("06TD");
//								}
//								dbl.closeResultSetFinal(rst);
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
							}else{
								//edit by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B
								cashFilter.setTsfTypeCode(rs.getString("FTsfTypeCode"));
								//add by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B
								cashFilter.setSubTsfTypeCode(rs.getString("FSubTsfTypeCode"));
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//								strSql += " and FTsfTypeCode = '07'";
//								rst = dbl.openResultSet(strSql);
//								if(rst.next()){
//									cashFilter.setSubTsfTypeCode(rst.getString("FSubTsfTypeCode"));
//								}else{
//									cashFilter.setSubTsfTypeCode("07TD");
//								}
//								dbl.closeResultSetFinal(rst);
								//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
							}
                            //edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
					        if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("成本")){
//					        	TransferBean cashTrans = new TransferBean();
//					        	
//					        	cashTrans.setStrTsfTypeCode("01");
//					        	cashTrans.setStrSubTsfTypeCode("0002");
//					        	this.setTransferData(rs,cashTrans);//设置资金调拨
//					        	
//					        	TransferSetBean  cashSetBean = new TransferSetBean();
//					        	
//					        	this.setTransferSetData(rs,cashSetBean);//设置资金子调拨					        	
					        	
					        	//对于现金的成本调整，需要单独通过update语句实现
					        	
					        	buffarrCash.append("").append("\f\f");
					        	
					        	
					        }else{
					        	//------ modify by wangzuochun 2010.09.10  MS01695    TA尾差调整报表在调整现金汇兑损益时有问题    QDV4赢时胜上海2010年9月3日01_B    
					        	//------ 汇兑损益已经放到上面处理，此处排除汇兑损益的情况；
					        	//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
//					        	if (!"汇兑损益".equals(rs.getString("FTSFTYPECODE")))
//					        	{
					        	//---delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B---//
						        	cashFilter.setCuryCode(sCuryCode);
									cashFilter.setStockInd(-1);//入账标识
									cashPayData.setFilterType(cashFilter);
									cashPayData.getSetting();
									if(cashPayData.getNum()!=null&&cashPayData.getNum().trim().length()>0){//已经存在数据，则先删除，避免重复产生数据
										//删除现金应收应付的尾差调整数据
										sqlStr = "delete from "
												+ pub.yssGetTableName("Tb_Data_CashPayRec")
												+ " where FNum = " +dbl.sqlString(cashPayData.getNum());										
										// 执行sql语句
										dbl.executeSql(sqlStr);
									}
									cashPayData.setNum("");//编号清空，插入时才会自动生成新的编号
									cashPayData.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
									cashPayData.setTradeDate(this.dDate);
									cashPayData.setCashAccCode(sCahsAccCode);
									cashPayData.setTsfTypeCode(cashFilter.getTsfTypeCode());
									cashPayData.setSubTsfTypeCode(cashFilter.getSubTsfTypeCode());
									cashPayData.setCuryCode(sCuryCode);
									cashPayData.setStockInd(-1);//入账标识
									cashPayData.setDesc("基金TA尾差调整数据");//加上描述信息
									cashPayData.setInOutType(1);//方向为流入。因为是将尾差调整入该组合
									cashPayData.setMoney(tailFBail);							
									cashPayData.setBaseCuryMoney(tailFBaseBail);							
									cashPayData.setPortCuryMoney(tailFPortBail);							
									cashPayData.checkStateId=1;//调整后的数据为已审核状态
									cashPayData.creatorCode = pub.getUserCode();
							        cashPayData.creatorTime = YssFun.formatDate(new java.util.Date());
							        cashPayData.checkUserCode = pub.getUserCode();
							        cashPayData.checkTime = YssFun.formatDate(new java.util.Date());
							        
							        buffarrCashResPay.append(this.buildRowcashPayStr(cashPayData)).append("\f\f");
//					        	}//delete by songjie 2011.04.22 BUG 1759 QDV4汇添富2011年04月20日01_B
					        	//------------------------------------MS01695------------------------------------//
					        }
						}else if(2==rs.getInt("FTYPEID")){//需要调整运营应收应付表
							InvestPayRecBean investFilter = new InvestPayRecBean();
							investFilter.setFIVPayCatCode(rs.getString("FKeyCode"));
							investFilter.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
							investFilter.setTradeDate(this.dDate);	
							investFilter.setBeginTradeDate(this.dDate);
							investFilter.setEndTradeDate(this.dDate);
							
							investFilter.setTsftTypeCode("07");
							investFilter.setSubTsfTypeCode("07IV");
							
							//nvestFilter.setCuryCode(rs.getString("fcurycode"));
							investFilter.setFStockInd(-1);//入账标识
							investPayData.setFilterType(investFilter);
							investPayData.getSetting();
							if(investPayData.getNum()!=null&&investPayData.getNum().trim().length()>0){//先删除已经存在的数据，避免重复插入
								//删除运营应收应付的尾差调整数据
								sqlStr = "delete from "
										+ pub.yssGetTableName("Tb_Data_InvestPayRec")
										+ " where FNum = " + dbl.sqlString(investPayData.getNum());
								// 执行sql语句
								dbl.executeSql(sqlStr);
							}
							investPayData.setNum("");//新插入的数据，编号清空，才会自动生成新的编号
							investPayData.setFIVPayCatCode(rs.getString("FKeyCode"));
							investPayData.setPortCode(this.portCode);//将尾差数据调整到前台指定的组合
							investPayData.setTradeDate(this.dDate);							
							investPayData.setTsftTypeCode(investFilter.getTsftTypeCode());
							investPayData.setSubTsfTypeCode(investFilter.getSubTsfTypeCode());
							investPayData.setCuryCode(rs.getString("fcurycode"));
							investPayData.setFStockInd(-1);//入账标识
							investPayData.setDesc("基金TA尾差调整数据");//加上描述信息							
							investPayData.setMoney(tailFBail);							
							investPayData.setBaseCuryMoney(tailFBaseBail);							
							investPayData.setPortCuryMoney(tailFPortBail);							
							investPayData.checkStateId=1;//调整后的数据为已审核状态
							investPayData.creatorCode = pub.getUserCode();
					        investPayData.creatorTime = YssFun.formatDate(new java.util.Date());
					        investPayData.checkUserCode = pub.getUserCode();
					        investPayData.checkTime = YssFun.formatDate(new java.util.Date());
					        //----- 增加所属分类代码 by qiuxufeng 20110125
					        /**shashijie 2012-8-21 BUG 5334 修复这个BUG的时候发现的一并给改了,所属分类可能出现null值*/
					        investPayData.setStrAttrClsCode(rs.getString("FATTRCLSCODE")==null ? 
					        		" " : rs.getString("FATTRCLSCODE"));
							/**end*/
					        //----- end
					      
					        buffarrInvestRecPay.append(this.buildRowinvestPayStr(investPayData)).append("\f\f");
						}
						//---add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
						if(alKey.contains(rs.getString("FKeyCode") + "," + rs.getString("FTSFTYPENAME") + "," + YssFun.toSqlDate(dDate))){
							if(!alExistKey.contains(rs.getString("FKeyCode") + "," + rs.getString("FTSFTYPENAME") + "," + YssFun.toSqlDate(dDate))){
								alExistKey.add(rs.getString("FKeyCode") + "," + rs.getString("FTSFTYPENAME") + "," + YssFun.toSqlDate(dDate));
							}else{
								continue;
							}
						}else{
							continue;
						}
						//---add by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
						
						//现在开始将以上的尾差调整数据，保存入 尾差调整数据表中					
						pst.setDate(1, YssFun.toSqlDate(this.dDate));//调整日期:界面选中的日期，即是我要调整的日期
						pst.setDouble(2, tailFBail);
						pst.setDouble(3, tailFPortBail);						
						pst.setString(4, "调整成功");//审核状态，标记为已审核状态
						pst.setString(5, rs.getString("FKeyCode"));
						pst.setString(6, rs.getString("FTSFTYPECODE"));
						pst.setDate(7, YssFun.toSqlDate(dDate));//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
						pst.executeUpdate();
						
						//保存调整状态存储表
						pst1.setString(1,rs.getString("FKeyCode"));
						//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
						if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("成本")){
							pst1.setString(2,"05");
						//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
						}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("估值增值")){
							pst1.setString(2,"09");
						//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B	
						}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("汇兑损益")){
							pst1.setString(2,"99");
						//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
						}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("应收款项")){
							pst1.setString(2,"06");
						//edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B
						}else if(rs.getString("FTSFTYPENAME").equalsIgnoreCase("应付款项")){
							pst1.setString(2,"07");
						}else {
							pst1.setString(2,"05");
						}
						pst1.setString(3,this.portCode);
						pst1.setString(4,"调整成功");
						pst1.setDate(5,YssFun.toSqlDate(this.dDate));
						pst1.setDouble(6,tailFBail);
						pst1.setDouble(7,tailFPortBail);
						pst1.setInt(8,rs.getInt("FTYPEID"));
						pst1.setString(9,pub.getUserCode());
						pst1.setString(10,YssFun.formatDatetime(new Date()));
						
						pst1.executeUpdate();
					}
				}
							
				SecIntegratedBean secInteData = new SecIntegratedBean();
				secInteData.setYssPub(pub);
				secInteData.setSOperDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
				secInteData.setSExchangeDate(YssFun.formatDate(this.dDate,"yyyy-MM-dd"));
				secInteData.setSTradeTypeCode("82");
				secInteData.checkStateId =1;
				
				buffInteger.append(secInteData.buildRowStrForParse()).append("\r\t[null]\r\t");
				if(buffarrSec.toString().endsWith("\f\f")){
					buffarrSec.setLength(buffarrSec.length() -2);
					buffInteger.append(buffarrSec.toString()).append("\r\t");
				}else{
					buffInteger.append("").append("\r\t");
				}
				if(buffarrSecResPay.toString().endsWith("\f\f")){
					buffarrSecResPay.setLength(buffarrSecResPay.length() -2);
					buffInteger.append(buffarrSecResPay.toString()).append("\r\t");
				}else{
					buffInteger.append("").append("\r\t");
				}
				if(buffarrCash.toString().endsWith("\f\f")){
					buffarrCash.setLength(buffarrCash.length() -2);
					buffInteger.append(buffarrCash.toString()).append("\r\t");
				}else{
					buffInteger.append("").append("\r\t");
				}
				if(buffarrCashResPay.toString().endsWith("\f\f")){
					buffarrCashResPay.setLength(buffarrCashResPay.length() -2);
					buffInteger.append(buffarrCashResPay.toString()).append("\r\t");
				}else{
					buffInteger.append("").append("\r\t");
				}
				if(buffarrInvestRecPay.toString().endsWith("\f\f")){
					buffarrInvestRecPay.setLength(buffarrInvestRecPay.length() -2);
					buffInteger.append(buffarrInvestRecPay.toString());
				}else{
					buffInteger.append("").append("\r\t");
				}
				
				if(buffInteger.length() >0){
					pub.setbSysCheckState(false);
					secInteData.saveMutliSetting(buffInteger.toString());
					pub.setbSysCheckState(true);
				}
				
				conn.commit();
	            conn.setAutoCommit(true);
	            bTrans = false;
	            dbl.endTransFinal(conn,bTrans);
			} else if ("adjcancel".equalsIgnoreCase(doOperType)) {// 取消尾差调整
				sqlStr="select FKeyCode from "+ pub.yssGetTableName("Tb_Data_DIFADJ")
						+ " where FADJDATE = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = " + dbl.sqlString(this.portCode);
				rs = dbl.openResultSet(sqlStr);
				if (!rs.next()) {// 没有数据可取消时
					throw new YssException("所选组合下没有可“取消调整”的数据，请选择调整过数据的组合");
				}
				dbl.closeResultSetFinal(rs);
				bOperFlag=true;//标明执行了操作
				conn.setAutoCommit(false);
	            bTrans = true;
				//删除证券应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_data_SecRecPay")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
				//删除现金应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_CashPayRec")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
				//删除运营应收应付的尾差调整数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_InvestPayRec")
						+ " where FTransDate = " + dbl.sqlDate(this.dDate)
						+ " and FPortCode = "+dbl.sqlString(this.portCode)
						+ " and FDesc like '基金TA尾差调整数据%'";// -1标记的入账标识是尾差调整数据
				// 执行sql语句
				dbl.executeSql(sqlStr);
				//删除尾差调整数据表的数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_TADifAdj")
						+ " where FADJDATE = " + dbl.sqlDate(this.dDate);
				// 执行sql语句
				dbl.executeSql(sqlStr);
				
				//删除调整状态存储表数据
				sqlStr = "delete from "
						+ pub.yssGetTableName("Tb_Data_DIFADJ")
						+ " where FADJDATE = " + dbl.sqlDate(this.dDate);
				// 执行sql语句
				dbl.executeSql(sqlStr);
				
				//删除综合业务数据
				sqlStr = "delete from "
					+ pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FExchangeDate = " + dbl.sqlDate(this.dDate)
					+ " and FTradeTypeCode = '82'";
				// 执行sql语句
				dbl.executeSql(sqlStr);

				conn.commit();
	            conn.setAutoCommit(true);
	            bTrans = false;
			}
			if(bOperFlag){//执行了上面的操作，就要重新进行库存统计
				// ===调整了，或者取消调整了，都要重新进行一次库存统计=====================

				BaseStgStatDeal secstgstat = null;
				secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean(
						"SecRecPay"); // 证券应收应付库存
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(dDate, dDate, operSql
						.sqlCodes(this.portCode), true, false, false); // 这里要先统计证券应收应付
	
				secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean(
						"CashPayRec"); // 现金应收应付库存
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(dDate, dDate, operSql
						.sqlCodes(this.portCode), true, false, false);
	
				secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean(
						"InvestPayRec"); // 运营应收应付库存
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(dDate, dDate, operSql
						.sqlCodes(this.portCode), true, false, false);
				
				secstgstat = (BaseStgStatDeal) pub.getOperDealCtx().getBean("SecurityStorage");
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(this.dDate, this.dDate,operSql.sqlCodes(this.portCode));
				
				secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("CashStorage");
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(this.dDate, this.dDate,
                                   operSql.sqlCodes(this.portCode));
				
				secstgstat = (BaseStgStatDeal) pub.
                getOperDealCtx().getBean("InvestStorage");
				secstgstat.setYssPub(pub);
				secstgstat.stroageStat(this.dDate, this.dDate,
                                   operSql.sqlCodes(this.portCode));
				// =============================end===================================
			}
			if ("adjust".equalsIgnoreCase(doOperType)) {// 调整尾差
				//-------------------------调整现金成本数据----------------------------//
				dbl.endTransFinal(conn,bTrans);
				dbl.closeResultSetFinal(rs);
				conn.setAutoCommit(false);
				bTrans = true;
				
				dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_stock_cash"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
				sqlTaiDif = " update " + pub.yssGetTableName("tb_stock_cash") + " set FAccBalance = ?,FPortCuryBal = ? where "+
							" FCashAcccode = ? and FPortCode = ? and FStorageDATE = ?";
				
				pst2 = conn.prepareStatement(sqlTaiDif);
				
				sqlStr = " select * from (select e.fkeycode,e.ftsftypecode,e.fadjbaly,e.fadjportbaly,e.ftypeid,f.fbankaccount from " +
						 pub.yssGetTableName("tb_data_tadifadj") +
						 " e left join (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
						 " where fcheckstate = 1) f on e.fkeycode = f.fcashacccode where e.fdate = " + dbl.sqlDate(this.dDate) +
						 " and e.ftypeid = 1 and e.ftsftypecode like '成本%') g " +
						 " left join (select c.* from (select b.fcashacccode, b.fcurycode, b.fbankaccount from (select max(m.fstartdate) as fstartdate," +
						 " m.fcashacccode,FBankAccount,fportcode from " + pub.yssGetTableName("tb_para_cashaccount") +
						 " m where m.FStartDate <= " + dbl.sqlDate(this.dDate) +
						 " and m.fcheckstate = 1 and m.fportcode = " + dbl.sqlString(this.portCode) +
						 " group by m.fcashacccode, m.FBankAccount,m.fportcode order by m.FBankAccount, m.fportcode,m.fcashacccode) a" +
						 " join (select * from " + pub.yssGetTableName("tb_para_cashaccount") +
						 " where fcheckstate = 1 and fportcode = " + dbl.sqlString(this.portCode) +
						 " ) b on a.fcashacccode = b.fcashacccode and a.fportcode =b.fportcode and a.fstartdate = b.fstartdate) c) h on g.fbankaccount =h.fbankaccount";
				
				rs = dbl.openResultSet(sqlStr);
				
				while(rs.next()){
					tailFBail = rs.getDouble("fadjbaly");//原币差额
					tailFPortBail = rs.getDouble("fadjportbaly");//本币差额 
					String strSql = " select * from " + pub.yssGetTableName("tb_stock_cash") + " where FCheckState =1" +
									" and FStorageDate = " + dbl.sqlDate(this.dDate) +
									" and FCashAcccode =" + dbl.sqlString(rs.getString("fcashacccode")!= null?rs.getString("fcashacccode"):rs.getString("fkeycode")) +
									" and FPortCode =" + dbl.sqlString(this.portCode);
					
					ResultSet rst = dbl.openResultSet(strSql);
					
					if(rst.next()){
						pst2.setDouble(1,YssD.add(rst.getDouble("FAccBalance"),tailFBail));
						pst2.setDouble(2,YssD.add(rst.getDouble("FPortCuryBal"),tailFPortBail));
						pst2.setString(3,rs.getString("fcashacccode")!= null?rs.getString("fcashacccode"):rs.getString("fkeycode"));
						pst2.setString(4,this.portCode);
						pst2.setDate(5,YssFun.toSqlDate(this.dDate));
						
						pst2.executeUpdate();
					}
					dbl.closeResultSetFinal(rst);
				}
				conn.commit();
				conn.setAutoCommit(true);
				bTrans = false;
			}
		} catch (Exception e) {
			throw new YssException("TA基金尾差调整出错！", e);
		} finally {
			dbl.closeStatementFinal(pst);
		     //  关闭记录集
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
			dbl.closeStatementFinal(pst1);
			dbl.closeStatementFinal(pst2);
		}
		return null;
	}
	/**
	 * 拼接运营应收应付数据
	 * @param investPayData
	 * @return
	 */
	private String buildRowinvestPayStr(InvestPayRecBean investPayData) throws YssException{
		StringBuffer buf = new StringBuffer();
		try{
	        buf.append(investPayData.getNum()).append("\t");
	        buf.append(YssFun.formatDate(investPayData.getTradeDate())).append("\t");
	        buf.append(investPayData.getPortCode()).append("\t");
	        buf.append(investPayData.getAnalysisCode1()).append("\t");
	        buf.append(" ").append("\t");
	        buf.append(investPayData.getTsftTypeCode()).append("\t");
	        buf.append(" ").append("\t");
	        buf.append(investPayData.getSubTsfTypeCode()).append("\t");
	        buf.append(" ").append("\t");
	        buf.append(investPayData.getMoney()).append("\t");
	        buf.append(investPayData.getBaseCuryRate()).append("\t");
	        buf.append(investPayData.getBaseCuryMoney()).append("\t");
	        buf.append(investPayData.getPortCuryRate()).append("\t");
	        buf.append(investPayData.getPortCuryMoney()).append("\t");
	        buf.append("1").append("\t");
	        buf.append("1").append("\t");
	        buf.append(investPayData.getCuryCode()).append("\t");
	        buf.append(" ").append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(investPayData.getFIVPayCatCode()).append("\t");
	        buf.append(investPayData.getDesc()).append("\t"); 
	        buf.append(investPayData.getAnalysisCode2()).append("\t");
	        buf.append(investPayData.getAnalysisCode3()).append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(" ").append("\t"); 
	        buf.append(" ").append("\t"); 
	        if(investPayData.getStrAttrClsCode().length() == 0) { //所属分类代码 by qiuxufeng 20110124
	        	buf.append(" ").append("\t");
	        } else {
	        	buf.append(investPayData.getStrAttrClsCode()).append("\t");
	        }
	        /**shashijie 2012-8-10 BUG 5229 多传递一个空格,否则下标越界 */
	        buf.append(" ").append("\t");
			/**end*/
		}catch (Exception e) {
			throw new YssException("拼接运营应收应付数据出错！",e);
		}
		return buf.toString();
	}

	/**
	 * 拼接现金应收应付数据
	 * @param cashPayData
	 * @return
	 */
	private String buildRowcashPayStr(CashPecPayBean cashPayData) throws YssException{
		StringBuffer buf = new StringBuffer();
		try{
	        buf.append(cashPayData.getNum()).append("\t");
	        buf.append(YssFun.formatDate(cashPayData.getTradeDate())).append("\t");
	        buf.append(cashPayData.getPortCode()).append("\t");
	        buf.append(cashPayData.getInvestManagerCode()).append("\t");

	        buf.append(cashPayData.getBrokerCode()).append("\t");

	        buf.append(cashPayData.getCategoryCode()).append("\t");

	        buf.append(cashPayData.getCashAccCode()).append("\t");

	        buf.append(cashPayData.getTsfTypeCode()).append("\t");

	        buf.append(cashPayData.getSubTsfTypeCode()).append("\t");

	        buf.append(cashPayData.getCuryCode()).append("\t");

	        buf.append(cashPayData.getMoney()).append("\t");
	        buf.append(cashPayData.getBaseCuryRate()).append("\t");
	        buf.append(cashPayData.getBaseCuryMoney()).append("\t");
	        buf.append(cashPayData.getPortCuryRate()).append("\t");
	        buf.append(cashPayData.getPortCuryMoney()).append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(YssFun.formatDate(this.dDate)).append("\t");
	        buf.append(1).append("\t");
	        buf.append(cashPayData.getInOutType()).append("\t");
	        buf.append("1").append("\t");
	        buf.append(cashPayData.getDesc()).append("\t");   

	        buf.append(" ").append("\t");   //组合群代码
	        buf.append(" ").append("\t");   //组合群名称
	        //--------增加所属分类代码 by qiuxufeng 20110124
	        //---edit by songjie 2012.01.17 BUG 3649 QDV4汇添富2012年1月13日01_B start---//
	        //添加对null的判断
	        if(cashPayData.getStrAttrClsCode() == null || 
	          (cashPayData.getStrAttrClsCode() != null && 
	           cashPayData.getStrAttrClsCode().length() == 0)) { 
	        	buf.append(" ").append("\t");
	        } else {
	        	buf.append(cashPayData.getStrAttrClsCode()).append("\t");
	        }
	        //---edit by songjie 2012.01.17 BUG 3649 QDV4汇添富2012年1月13日01_B end---//
	        //--------end
	        
			
		}catch (Exception e) {
			throw new YssException("拼接现金应收应付数据出错！");
		}
		return buf.toString();
	}

	/**
	 * 拼接资金调拨子表数据
	 * @param cashSetBean
	 */
	private String buildRowCashTransSetStr(TransferSetBean cashSetBean) throws YssException{
		StringBuffer buf =null;
		try{
			buf = new StringBuffer();
	        buf.append(cashSetBean.getSNum()).append("\t");
	        buf.append(cashSetBean.getSSubNum()).append("\t");
	        buf.append(cashSetBean.getIInOut()).append("\t");
	        buf.append(cashSetBean.getSPortCode()).append("\t");
	        buf.append(cashSetBean.getSAnalysisCode1()).append("\t");
	        buf.append(cashSetBean.getSAnalysisCode2()).append("\t");
	        buf.append(cashSetBean.getSAnalysisCode3()).append("\t");
	        buf.append(cashSetBean.getSCashAccCode()).append("\t");
	        buf.append(cashSetBean.getDMoney()).append("\t");
	        buf.append(cashSetBean.getDBaseRate()).append("\t");
	        buf.append(cashSetBean.getDPortRate()).append("\t");
	        buf.append(cashSetBean.getCheckStateId()).append("\t");
	        buf.append(" ").append("\t");
	        buf.append(" ").append("\t"); 
		}catch (Exception e) {
			throw new YssException("拼接资金调拨子表数据出错！",e);
		}
		return buf.toString();
	}

	/**
	 * 拼接资金调拨数据
	 * @param cashTrans
	 */
	private String buildRowCashTransStr(TransferBean cashTrans) throws YssException{
		StringBuffer buf = new StringBuffer();
		try{
			buf.append(cashTrans.getStrNum()).append("\t");
	        buf.append(cashTrans.getStrTsfTypeCode()).append("\t");
	        buf.append(cashTrans.getStrSubTsfTypeCode()).append("\t");
	        buf.append(cashTrans.getStrAttrClsCode()).append("\t");
	        buf.append(cashTrans.getStrSecurityCode()).append("\t");
	        buf.append(YssFun.formatDate(cashTrans.getDtTransferDate())).append("\t");
	        buf.append(cashTrans.getStrTransferTime()).append("\t");
	        buf.append(YssFun.formatDate(cashTrans.getDtTransDate())).append("\t");
	        buf.append(cashTrans.getStrTradeNum()).append("\t");
	        buf.append(cashTrans.getStrDesc()).append("\t");
	        buf.append("1").append("\t");
	        buf.append(cashTrans.getCheckStateId()).append("\t");
	        buf.append(cashTrans.getStrOldNum()).append("\t");
	        buf.append(cashTrans.getStrDesc()).append("\t");
	        buf.append(cashTrans.getSrcCashAccCode()).append("\t");
	        buf.append(" ").append("\t");
	        
		}catch (Exception e) {
			throw new YssException("拼接资金调拨数据出错！",e);
		}
		return buf.toString();
	}

//	/**
//	 * 设置资金子调拨
//	 * @param rs
//	 * @param cashSetBean
//	 */
//	private void setTransferSetData(ResultSet rs, TransferSetBean cashSetBean) throws YssException{
//		try{
//			cashSetBean.setSCashAccCode(rs.getString("FKeyCode"));
//			cashSetBean.setSPortCode(this.portCode);
//			cashSetBean.setIInOut(1);
//			cashSetBean.setSAnalysisCode1(" ");
//			cashSetBean.setSAnalysisCode2(" ");
//			cashSetBean.setSAnalysisCode3(" ");
//			cashSetBean.setDMoney(rs.getDouble("FAdjBAL"));
//			cashSetBean.setDBaseRate(1);
//			cashSetBean.setDPortRate(1);
//			cashSetBean.setCheckStateId(1);
//		}catch (Exception e) {
//			throw new YssException("设置资金子调拨出错！",e);
//		}
//		
//	}

//	/**
//	 * 设置资金调拨数据
//	 * @param rs
//	 * @param cashTrans
//	 */
//	private void setTransferData(ResultSet rs, TransferBean cashTrans) throws YssException{
//		
//		try{
//			cashTrans.setStrPortCode(this.portCode);
//			cashTrans.setStrAttrClsCode(" ");
//			cashTrans.setDtTransDate(this.dDate);
//			cashTrans.setStrTransferTime("00:00:00");
//			cashTrans.setDtTransferDate(this.dDate);
//			cashTrans.setSrcCashAccCode(rs.getString("FKeyCode"));
//			cashTrans.setCheckStateId(1);
//			
//		}catch (Exception e) {
//			throw new YssException("设置资金调拨数据出错！",e);
//		}
//	}

	/**
	 * 设置综合业务数据
	 * @param rs
	 * @param secInteData
	 */
	private void setIntegratedBeanData(ResultSet rs, SecIntegratedBean secInteData) throws YssException{
		try{

          secInteData.setSExchangeDate(YssFun.formatDate(this.dDate, "yyyy-MM-dd")); 
          secInteData.setSTradeTypeCode("82"); // 调账
          secInteData.setSSecurityCode(rs.getString("FKeyCode"));
          secInteData.setSOperDate(YssFun.formatDate(this.dDate, "yyyy-MM-dd"));
          secInteData.setSPortCode(this.portCode);
          secInteData.setSAnalysisCode1(" ");
          secInteData.setSAnalysisCode2(" ");
          secInteData.setSAnalysisCode3(" ");
          secInteData.setDAmount(0);
          //---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
          secInteData.setDCost(rs.getDouble("DIFFBAL"));
          secInteData.setDMCost(rs.getDouble("DIFFBAL"));
          secInteData.setDVCost(rs.getDouble("DIFFBAL"));
        //---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
          secInteData.setDBaseCost(0);
          secInteData.setDMBaseCost(0);
          secInteData.setDVBaseCost(0);
        //---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
          secInteData.setDPortCost(rs.getDouble("DIFPORTCURYBAL"));
          secInteData.setDMPortCost(rs.getDouble("DIFPORTCURYBAL"));
          secInteData.setDVPortCost(rs.getDouble("DIFPORTCURYBAL"));
        //---edit by songjie 2011.04.24 BUG 1759 QDV4汇添富2011年04月20日01_B---//
          secInteData.setDBaseCuryRate(1);
          secInteData.setDPortCuryRate(1);
          secInteData.checkStateId = 1;
          secInteData.creatorCode = pub.getUserCode();
          secInteData.creatorTime = YssFun.formatDatetime(this.dDate);
          secInteData.checkUserCode = pub.getUserCode();
          secInteData.checkTime = YssFun.formatDatetime(this.dDate);
          secInteData.setAttrClsCode(" "); //所属分类

          secInteData.setIInOutType(1); // 流入
			
		}catch (Exception e) {
			throw new YssException("设置综合业务数据出错！",e);
		}		
	}

	public String checkRequest(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String doOperation(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		
	}

}
