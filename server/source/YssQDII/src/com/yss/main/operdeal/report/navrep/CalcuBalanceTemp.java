/**
 * 
 */
package com.yss.main.operdeal.report.navrep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.omg.CORBA.PUBLIC_MEMBER;


import com.yss.base.BaseAPOperValue;
import com.yss.dsub.YssPub;
import com.yss.main.cashmanage.TransferBean;
import com.yss.main.cashmanage.TransferSetBean;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.funsetting.FlowBean;
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
public class CalcuBalanceTemp extends BaseAPOperValue implements IClientOperRequest {
	private Object obj = null;
    private java.util.Date dDate = null;
    private String portCode = "";
    private int showContent;
    String month = "";
    String accLen = "";
    int lMonth = 0;
    String  gstrBalBal = "tmpBalBal";        //获取临时表 20130827   add by liuxiaojun stroy 3899
    String YssTablePrefix = "";
    private String showBalanceTableDate;         //字符串格式的日期  add by liuxiaojun stroy 3899
    private String LTableName = ""; //使用那种表，是Balance还是临时表
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
	
	public int getShowContent() {
		return showContent;
	}

	public void setShowContent(int showContent) {
		this.showContent = showContent;
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
        showBalanceTableDate = reqAry1[1];
        this.month = reqAry1[1];
        reqAry1 = reqAry[1].split("\r");
        this.portCode = reqAry1[1];
    }
	
	public Object invokeOperMothed() throws YssException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// ---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
		// 参数布局散乱不便操作 MS00003
		// 判断是否在组合中执行
		if (pub.getFlow() != null
				&& pub.getFlow().keySet().contains(pub.getUserCode())) {
			// 插入已执行组合
			((FlowBean) pub.getFlow().get(pub.getUserCode()))
					.setFPortCodes(portCode);
		}
		int  lYear = Integer.parseInt(month.split("-")[0]);
		lMonth = Integer.parseInt(month.split("-")[1]);
		Date date = null;
		LTableName = gstrBalBal;
		try {
			date = format.parse(month);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			PostAccB(lYear,lMonth,date);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			getRsBalanceTable(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@SuppressWarnings("null")
	private void getRsBalanceTable(int lngSum) throws YssException, SQLException {
		Connection conn = dbl.loadConnection();
	    boolean bTrans = false;
		String strPre = "B";
		String strSum1 = "";
		String sTmp = "";
		String sTmp1 = "";
		String SqlStr = "";
		String ALLCUR = "***";
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement pst = null;
		// 产生各种情况下获取余额的sql语句，lngsum=1取出按科目类别（和币种）和余额方向汇总的数据，lngsum=2总计
		// 如果日期为空则返回空对象，因为第一次进入时如果如果以前没有登过帐则不显示
		if (showBalanceTableDate != null) {
			if (lngSum == 1) {
				strSum1 = "a.FAcctClass,a.fcurcode,";
			} else if (lngSum == 2) {
				strSum1 = " a.fcurcode,";
			}
			// '合计行

			// '余额表取数主体
			sTmp = "sum(case when FMonth=0 then F" + "" + "EndBal else 0 end) as ncye,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "StartBal end) as StartBal,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "Debit end) as Debit,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "Credit end) as Credit,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "AccDebit end) as AccDebit,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "AccCredit end) as AccCredit,"
					+ "sum(case when FMonth=0 then 0 else F" + "" + "EndBal end) as EndBal ,"
					+ "sum(case when FMonth=0 then F" + strPre + "EndBal else 0 end) as bncye,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "StartBal end) as bStartBal,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "Debit end) as bDebit,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "Credit end) as bCredit,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "AccDebit end) as bAccDebit,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "AccCredit end) as bAccCredit,"
					+ "sum(case when FMonth=0 then 0 else F" + strPre + "EndBal end) as bEndBal";

			if (lngSum > 0) {
				sTmp1 = strSum1 + "a.FBalDC,sum(b.ncye) as ncye,sum(b.StartBal) as StartBal,"
						+ "sum(b.Debit) as Debit,sum(b.Credit) as Credit,sum(b.AccDebit) as AccDebit,"
						+ "sum(b.AccCredit) as AccCredit, sum(b.EndBal) as EndBal"
						+ ",sum(b.bncye) as bncye,sum(b.bStartBal) as bStartBal,"
						+ "sum(b.bDebit) as bDebit,sum(b.bCredit) as bCredit,sum(b.bAccDebit) as bAccDebit,"
						+ "sum(b.bAccCredit) as bAccCredit, sum(b.bEndBal) as bEndBal from " + YssTablePrefix
						+ "LAccount a ,(select FAcctCode,";
			} else {
				sTmp1 = "a.FBalDC,a.FAcctLevel,a.FAcctName,a.FAcctDetail,a.fcurcode,a.FAuxiAcc,b.* from "
						+ YssTablePrefix + "LAccount a ,";
			}

			// 20120315 added by liubo.Story #2302
			// 客户要求按照股票代码从小到大进行排序，即按facctcode字段下划线后的辅助核算项明细项进行排序。
			// 在此增加两个临时字段TmpAuxiAccID和TmpAcctCode（不用更新表结构）
			// TmpAuxiAccID用于将facctcode字段中的辅助核算项明细剥离出来。若facctcode字段中没有辅助核算项明细，或者辅助核算项明细是字母形式的，则直接给1
			// 比如facctcode的值为1002，则TmpAuxiAccID给1；facctcode的值为11020101_TRV
			// US，则TmpAuxiAccID给1；facctcode的值为11020201_410
			// HK，则TmpAuxiAccID的值为410
			// TmpAcctCode用于装载剥离了辅助核算项明细后的facctcode的值
			// =================================
			String sTmpAuxiAccID = "(case when instr(Facctcode, '_') > 0 then "
					+ " substr(facctcode,instr(facctcode, '_') + 1,instr(facctcode, ' ') - instr(facctcode, '_') - 1) "
					+ " else ' ' end) as TmpAuxiAccID,";
			// modified by yeshenghong 20120503 BUG4439 BUG4433
			String sTmpAcctCode = "(case when instr(Facctcode,'_') > 0 then substr(facctcode," + "1,"
					+ "instr(facctcode, '_') - 1) else facctcode end) as TmpAcctCode ";
			// ==================end===============

			if (lngSum > 0) {
				// '汇总明细科目
				SqlStr = "select c.*,d.fcurname from (select " + sTmp1 + sTmp + " from " + LTableName + " where "
						+ "(FMonth=0 or FMonth="
						+ lMonth + ")"
						+ ((LTableName.equalsIgnoreCase(gstrBalBal)) ? " and FAddr='" + pub.getUserCode() + "'" : "")
						+ " group by FAcctCode) b where a.FAcctCode=b.FAcctCode" + " and a.FAcctDetail<>0 group by "
						+ strSum1 + "a.fBalDC order by " + strSum1 + "FBalDC Desc) c left join " + YssTablePrefix
						+ "lcurrency d on c.fcurcode=d.fcurcode  order by "
						+ (lngSum == 1 ? "c.FAcctClass,FBalDC desc,c.fcurcode," : "FBalDC desc,") + " d.fcurcode";
			} else {
				// 20120315 added by liubo.Story #2302
				// sTmpAuxiAccID变量用于存储TmpAuxiAccID字段的生成语句；sTmpAcctCode变量用于存储TmpAcctCode字段的生成语句
				// =============================
				SqlStr = "select c.*,d.fcurname,"
						+ sTmpAuxiAccID
						+ sTmpAcctCode
						+ " from (select "
						+ sTmp1
						+ "  ( "
						+
						// =============end================
						"select FAcctcode,fcurcode as bcurcode," + sTmp + ",' ' as Auxiaccname from " + LTableName
						+ "  where " + "(FMonth=0 or FMonth="
						+ lMonth + ")"
						+ ((LTableName.equalsIgnoreCase(gstrBalBal)) ? " and FAddr='" + pub.getUserCode() + "'" : "")
						+ " group by FAcctcode,fcurcode  " + " union all " + "select FAcctcode" + dbl.sqlJN()
						+ "'_'" + dbl.sqlJN() + " Case When " + dbl.sqlInstr("FAuxiAcc", "'|'") + ">2 Then  "
						+ dbl.sqlSubStr("FAuxiAcc", "3", dbl.sqlInstr("FAuxiAcc", "'|'") + "-3") + " Else "
						+ dbl.sqlSubStr("FAuxiAcc", "3", dbl.sqlLen("FAuxiAcc"))
						+ " End as FAcctcode,fcurcode as bcurcode," + sTmp + ",Auxiaccname from " + LTableName
						+ " e inner join " + YssTablePrefix + "Auxiaccset f on ("
						+ dbl.sqlSubStr("e.FAuxiAcc", "0", dbl.sqlInstr("e.FAuxiAcc", "'|'") + "-1")
						+ "=f.Auxiaccid or e.FAuxiAcc=f.Auxiaccid)where " + "(FMonth=0 or FMonth="
						+ lMonth + ")"
						+ ((LTableName.equalsIgnoreCase(gstrBalBal)) ? " and FAddr='" + pub.getUserCode() + "'" : "")
						+ " and " + dbl.sqlLen(dbl.sqlTrim("FAuxiAcc"))
						+ ">2 group by facctcode,fcurcode,fauxiacc,f.auxiaccname "
						+ " ) b where substr(b.FAcctCode,0,case when (" + dbl.sqlInstr("b.FAcctCode", "'_'")
						+ "-1)>0 then " + dbl.sqlInstr("b.FAcctCode", "'_'") + "-1 else "
						+ dbl.sqlLen("b.FAcctCode") + " end )=a.FAcctCode order by b.FAcctCode)c left join "
						+ YssTablePrefix + "lcurrency d on c.bcurcode=d.fcurcode  " +
						// 20120315 modified by liubo.Story #2302
						// ================================
						// " order by facctcode,d.fcurcode";
						" order by TmpAcctCode, TmpAuxiAccID, d.fcurcode";
				// ===============end=================
			}

			rs = dbl.openResultSet(SqlStr, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			String Sql = "";
			if (!dbl.yssTableExist("TMP_CW_BALANCE")) {
				Sql = "create table TMP_CW_BALANCE" + "( " 
						+ "  FMONTH      NUMBER(3) not null,"
						+ "  FACCTCODE   VARCHAR2(50) not null,"
						+ "  FCURCODE    VARCHAR2(3) not null,"
						+ "  FSTARTBAL   NUMBER(19,4) default 0 not null,"
						+ "  FNCYE   	  NUMBER(19,4) default 0 not null,"
						+ "  FDEBIT      NUMBER(19,4) default 0 not null,"
						+ "  FCREDIT     NUMBER(19,4) default 0 not null,"
						+ "  FACCDEBIT   NUMBER(19,4) default 0 not null,"
						+ "  FACCCREDIT  NUMBER(19,4) default 0 not null,"
						+ "  FENDBAL     NUMBER(19,4) default 0 not null,"
						+ "  FBSTARTBAL  NUMBER(19,4) default 0 not null,"
						+ "  FBNCYE      NUMBER(19,4) default 0 not null,"
						+ "  FBDEBIT     NUMBER(19,4) default 0 not null,"
						+ "  FBCREDIT    NUMBER(19,4) default 0 not null,"
						+ "  FBACCDEBIT  NUMBER(19,4) default 0 not null,"
						+ "  FBACCCREDIT NUMBER(19,4) default 0 not null,"
						+ "  FBENDBAL    NUMBER(19,4) default 0 not null,"
						+ "  FISDETAIL   NUMBER(3) not null,"
						+ "  FADDR       VARCHAR2(30) not null,"
						+ "  FAUXIACC    VARCHAR2(100) not null,"
						+ "  FACCTLEVEL  NUMBER(1) not null" + ")";
				
				dbl.executeSql(Sql);
			}

			Sql = "Delete from TMP_CW_BALANCE where FAddr='" + pub.getUserCode() + "'";
			dbl.executeSql(Sql);
			
			Sql = "insert into TMP_CW_BALANCE(FMONTH,FACCTCODE,FCURCODE,FSTARTBAL,FNCYE,FDEBIT,FCREDIT"
				+ ",FACCDEBIT,FACCCREDIT,FENDBAL,FBSTARTBAL,FBNCYE,FBDEBIT,FBCREDIT,FBACCDEBIT"
				+ ",FBACCCREDIT,FBENDBAL,FISDETAIL,FADDR,FAUXIACC,FACCTLEVEL)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			pst = conn.prepareStatement(Sql);
			while(rs.next())
			{
				int FIsDetail =0;
//				String str = "";
//				str = "select distinct FISDETAIL from "+gstrBalBal + " where FACCTCODE=" +dbl.sqlString(rs.getString("FACCTCODE"));
//				rs1 = dbl.openResultSet(str);
//				while (rs1.next()){
//					FIsDetail =  rs1.getInt("FISDETAIL");
//				}
//				dbl.closeResultSetFinal(rs1);
				
				
				pst.setInt(1, lMonth);
				pst.setString(2, rs.getString("FACCTCODE"));
				pst.setString(3, rs.getString("BCURCODE"));
				pst.setDouble(4, rs.getDouble("STARTBAL"));
				pst.setDouble(5, rs.getDouble("NCYE"));
				pst.setDouble(6, rs.getDouble("DEBIT"));
				pst.setDouble(7, rs.getDouble("CREDIT"));
				pst.setDouble(8, rs.getDouble("ACCDEBIT"));
				pst.setDouble(9, rs.getDouble("ACCCREDIT"));
				pst.setDouble(10, rs.getDouble("ENDBAL"));
				pst.setDouble(11, rs.getDouble("BSTARTBAL"));
				pst.setDouble(12, rs.getDouble("BNCYE"));
				pst.setDouble(13, rs.getDouble("BDEBIT"));
				pst.setDouble(14, rs.getDouble("BCREDIT"));
				pst.setDouble(15, rs.getDouble("BACCDEBIT"));
				pst.setDouble(16, rs.getDouble("BACCCREDIT"));
				pst.setDouble(17, rs.getDouble("BENDBAL"));
				pst.setInt(18, FIsDetail);
				pst.setString(19, pub.getUserCode());
				pst.setString(20, rs.getString("FAUXIACC"));
				pst.setInt(21, rs.getInt("FACCTLEVEL"));
				pst.addBatch();
			}
			pst.executeBatch();
			pst.close();// add by songjie 2013.08.23 BUG 9161
						// QDV4赢时胜（北京）2013年08月21日01_B
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);  
		}
	}

	   /**
	    * 余额表需要的临时登帐
	    * @param lMonth int   月份
	    * @param lDay int     天数
	    * @throws YssException
	    * @throws SQLException
	    */
	   public void PostAccB(int lYear,int lMonth, java.util.Date date) throws YssException,
	         SQLException {
	      String SqlStr = "";
	      String sTmp = "";
	      ResultSet rs = null;
	      int lset = 0;

	      SqlStr = "Delete from " + gstrBalBal + " where FAddr='" + pub.getUserCode() +
	            "'";
	      dbl.executeSql(SqlStr);
	      
	      /**add by liuxiaojun 20130827  stroy 3899 得到套账号*/
	      SqlStr = "select distinct FSetCode from lsetlist where FSetId = " 
	    	  + "(select FASSETCODE from "+ pub.yssGetTableName("Tb_Para_Portfolio")
	    	  + " where fportcode = " +"'"+ this.portCode +"'"+")";
	      rs = dbl.openResultSet(SqlStr);
	      if (rs.next())
	      {
	    	  lset = rs.getInt("FSetCode");
	      }
	      /**end stroy 3899*/
	      YssTablePrefix = getTablePrefix(true, true, lYear, lset);
	      accLen = getAccLen(lYear,lset);
	      //从余额表中导入年初数及上月数据到临时余额表中
	      SqlStr = "insert into " + gstrBalBal + " select '" + pub.getUserCode() +
	            "',a.*  from " + YssTablePrefix +
	            "lbalance a where fmonth=0" +
	            ( (lMonth > 1) ? " or fmonth=" + (lMonth - 1) : "");
	      //System.out.println("SqlStr======="+SqlStr);
	      dbl.executeSql(SqlStr);
	      
	      //'登记本月凭证到临时余额表中
	      sTmp = PostVch(lMonth, date, true);

	      if (sTmp.trim().length() != 0) {
	         throw new YssException("临时登账错误！\r\n" + sTmp);
	      }
	   }

	
	   
	 //以下是表名前缀函数系列*******************************************************
	   //表名前缀，这里为了使用static方法，不再使用默认lYear和lnSet
	   public static String getTablePrefix(boolean bYear, boolean bSet,
	                                          int lYear, int lnSet) {
	      String stmp;

	      if ((lYear > 999 || !bYear) && (lnSet != 0 || !bSet)) { //年份四位
	         stmp = "A" + (bYear ? "" + lYear : "") +
	               (bSet ? (new DecimalFormat("000")).format(lnSet) : "");
	         return (stmp.length() == 1) ? "" : stmp;
	      }
	      return "";
	   }

	public String PostVch(int lMonth, java.util.Date date, boolean bTemp) throws YssException, SQLException {// 20060825
		String sSql = "";
		String sTmp = "";
		String sBal = ""; // '如果是余额表，需要的多余字段
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();

		if (lMonth < 1) {
			return " "; // 这里可能不应该返回0，想想其它办法吧。先用空值代替。
		}

		if (bTemp) {
			sTmp = gstrBalBal;
			sBal = "'" + pub.getUserCode() + "',";
		} else {
			sTmp = YssTablePrefix + "LBalance";
		}
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			// 明细科目凭证登帐(为了oracle，改成case when....)
			sSql = "insert into "
					+ sTmp
					+ " select "
					+ sBal
					+ lMonth
					+ ","
					+ dbl.sqlIsNull("fkmh", "facctcode")
					+ ","
					+ dbl.sqlIsNull("fcyid", "fcurcode")
					+ ","
					+ dbl.sqlIsNull("fendbal", "0")
					+ ","
					+ dbl.sqlIsNull("fjje", "0")
					+ ","
					+ dbl.sqlIsNull("fdje", "0")
					+ ","
					+ dbl.sqlIsNull("fjje", "0")
					+ " + "
					+ dbl.sqlIsNull("faccdebit", "0")
					+ ","
					+ dbl.sqlIsNull("fdje", "0")
					+ " + "
					+ dbl.sqlIsNull("facccredit", "0")
					+ ","
					+ dbl.sqlIsNull("fendbal", "0")
					+ " + "
					+ dbl.sqlIsNull("fjje", "0")
					+ " - "
					+ dbl.sqlIsNull("fdje", "0")
					+ ","
					+ dbl.sqlIsNull("fbendbal", "0")
					+ ","
					+ dbl.sqlIsNull("fbjje", "0")
					+ ","
					+ dbl.sqlIsNull("fbdje", "0")
					+ ","
					+ dbl.sqlIsNull("fbjje", "0")
					+ " + "
					+ dbl.sqlIsNull("fbaccdebit", "0")
					+ ","
					+ dbl.sqlIsNull("fbdje", "0")
					+ " + "
					+ dbl.sqlIsNull("fbacccredit", "0")
					+ ","
					+ dbl.sqlIsNull("fbendbal", "0")
					+ " + "
					+ dbl.sqlIsNull("fbjje", "0")
					+ " - "
					+ dbl.sqlIsNull("fbdje", "0")
					+ ","
					+ dbl.sqlIsNull("faendbal", "0")
					+ ","
					+ dbl.sqlIsNull("fjsl", "0")
					+ ","
					+ dbl.sqlIsNull("fdsl", "0")
					+ ","
					+ dbl.sqlIsNull("fjsl", "0")
					+ " + "
					+ dbl.sqlIsNull("faaccdebit", "0")
					+ ","
					+ dbl.sqlIsNull("fdsl", "0")
					+ " + "
					+ dbl.sqlIsNull("faacccredit", "0")
					+ ","
					+ dbl.sqlIsNull("faendbal", "0")
					+ " + "
					+ dbl.sqlIsNull("fjsl", "0")
					+ " - "
					+ dbl.sqlIsNull("fdsl", "0")
					+ ",1 ,case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc "
					+ "from (select fkmh,fcyid, sum(case when fjd='J' then fbal else 0 end) as fjje,"
					+ "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl,"
					+ "sum(case when fjd='D' then fsl else 0 end) as fdsl,"
					+ "sum(case when fjd='J' then fbbal else 0 end) as fbjje,"
					+ "sum(case when fjd='D' then fbbal else 0 end) as fbdje, " + "FauxiAcc " + "from "
					+ YssTablePrefix + "fcwvch where fterm=" + lMonth
					+ " and (fconfirmer <> ' ' or fconfirmer is null) " + " and fdate" + "<=" + dbl.sqlDate(date)
					+ " group by fkmh,fcyid,FauxiAcc) a ";

			// '考虑余额表临时登帐，上月数据固定从余额表获取
			sSql = sSql + "full join (select c.facctcode,fmonth,c.fcurcode,faccdebit,facccredit,"
					+ "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from "
					+ "(select * from " + YssTablePrefix + "LBalance where fmonth=" + (lMonth - 1)
					+ ") c join (select facctcode,facctdetail from " + YssTablePrefix
					+ "laccount where facctdetail=1) d on c.facctcode=d.facctcode)";

			sSql = sSql + " b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode and a.fauxiacc=b.fauxiacc";
			dbl.executeSql(sSql);

			int kmLength = 0;
			String strSql = "";
			for (int i = 1; i <= accLenLevel(-1) - 1; i++) {
				kmLength = accLenLevel(i);
				strSql = "insert into "
						+ sTmp
						+ " select "
						+ sBal
						+ lMonth
						+ ",a.facctcode"
						+ ",b.fcurcode,sum(b.fstartbal),sum(b.fdebit),sum(b.fcredit),"
						+ "sum(b.faccdebit),sum(b.facccredit),sum(b.fendbal),sum(b.fbstartbal),sum(b.fbdebit),"
						+ "sum(b.fbcredit),sum(b.fbaccdebit),sum(b.fbacccredit),sum(b.fbendbal),sum(b.fastartbal),"
						+ "sum(b.fadebit),sum(b.facredit),sum(b.faaccdebit),sum(b.faacccredit),sum(b.faendbal),0 ,' ' from "
						+ YssTablePrefix + "laccount a join " + sTmp + " b on a.facctcode ="
						+ dbl.sqlLeft("b.facctcode", kmLength) + " where b.fmonth=" + lMonth + " and "
						+ dbl.sqlLen("a.facctcode") + "=" + kmLength + " and a.facctdetail=0  "
						+ ((bTemp) ? " and FAddr='" + pub.getUserCode() + "'" : "")
						+ " group by a.facctcode,b.fcurcode order by a.facctcode";

				dbl.executeSql(strSql);
			}

			conn.commit();
			bTrans = false;
			if (rs != null) {
				rs.getStatement().close();
			}
			return "";
		} catch (SQLException es) {
			throw new YssException("Error:\r\n", es);
		} finally {
			try {
				if (bTrans) {
					conn.rollback();
				}
			} catch (Exception e) {
			}
			dbl.closeResultSetFinal(rs);
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException(e.getMessage());
			}
		}
	}
	   
	
	/**
	 * 返回level级科目的总长度 如果partial=true的话只返回本级长度 level<=0，则返回科目级数;
	 */
	public int accLenLevel(int level, boolean partial) {
		int ltmp;
		int total = 0;

		ltmp = accLen.length();
		if (level <= 0)
			return ltmp; // 返回科目级数

		if (level > ltmp)
			level = ltmp;

		if (partial)
			return Integer.parseInt(accLen.substring(level - 1, level));
		for (; level > 0; level--) {
			total += Integer.parseInt(accLen.substring(level - 1, level));
		}
		return total;
	}
	
	public final int accLenLevel(int level) {
		return accLenLevel(level, false);
	}
	
	public String getAccLen(int lYear, int lnSet) throws YssException{
		ResultSet rstmp = null;
		try {
			rstmp = dbl.openResultSet("select * from lsetlist where fyear=" + lYear + " and fsetcode="
					+ lnSet);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		 try {
			while (rstmp.next()){
				 accLen = rstmp.getString("facclen");
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 dbl.closeResultSetFinal(rstmp);
		 return accLen;
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
