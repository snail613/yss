package com.yss.main.operdeal.report.reptab;

import com.yss.base.BaseAPOperValue;
import com.yss.util.*;

import java.sql.*;
import java.util.*;
import com.yss.pojo.sys.*;

//现金头寸预测表参数数据源临时表-华夏

public class TabCashFore_zy extends BaseAPOperValue {
   private java.util.Date dStartDate;
   private java.util.Date dEndDate;
   private String portCode;
   private String invmgrCode = "";

   public TabCashFore_zy() {
   }

   public void init(Object bean) throws YssException {
      String reqAry[] = null;
      String reqAry1[] = null;
      String sRowStr = (String) bean;
      if (sRowStr.trim().length() == 0)
	  	return;
      reqAry = sRowStr.split("\n");
      reqAry1 = reqAry[0].split("\r");
      this.dStartDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[1].split("\r");
      this.dEndDate = YssFun.toDate(reqAry1[1]);
      reqAry1 = reqAry[2].split("\r");
      this.portCode = reqAry1[1];
      if(reqAry.length==4){
         reqAry1 = reqAry[3].split("\r");
         this.invmgrCode = reqAry1[1];
      }
   }

   public Object invokeOperMothed() throws YssException {
      createTmpTable();
      setCashForeTable();
      return "";
   }

   protected void createTmpTable() throws YssException {
      String strSql = "";
      try {
         if (dbl.yssTableExist(pub.yssGetTableName("tb_Temp_CashForeTree_" 
		 	+ pub.getUserCode()))) {
        	 /**shashijie ,2011-10-12 , STORY 1698*/
            dbl.executeSql(dbl.doOperSqlDrop("drop table " +
                           pub.yssGetTableName("tb_Temp_CashForeTree_" +
                                               pub.getUserCode())));
            /**end*/
         }

         strSql = "create table " +
               pub.yssGetTableName("tb_Temp_CashForeTree_" 
			   + pub.getUserCode()) 
			   +" (FCode varchar2(70)," 
//			   +" FName varchar2(50)," 
			   // 账户名称字段长度为100 by qiuxufeng 20110210
				+" FName varchar2(100)," 
			   +" FPortCode varchar2(20)," 
			   +" FInvMgrCode varchar2(20)," 
			   +" FCuryCode varchar2(20),"
			   +" FCashAccCode varchar2(20)," 
			   +" FParentCode varchar2(70)," 
			   +" FOrderCode varchar2(20)," + " FDate Date)";
         dbl.executeSql(strSql);

      }
      catch (Exception e) {
         throw new YssException("生成现金头寸预测表临时表出错");
      }
   }

   protected void setCashForeTable() throws YssException {
      String strSql = "";
      ResultSet rs = null;
      int n = 0;
      int j = 0;
      java.util.Date fDate;
      HashMap hmIndex = new HashMap();
      HashMap hmData = new HashMap();
      String[] sValCashDefineAry = null;
      YssTreeNode tNode = null;
      int iOrder = 1;
      String sOrderIndex = "";
      String sValCashDefine = "currency;cashaccount";
      HashMap hmField = new HashMap();
      try {
         hmField.put("currency", "FCuryCode");
         hmField.put("cashaccount", "FCashAccCode");
         sValCashDefineAry = sValCashDefine.split(";");
         hmIndex.put("[root]", "001");

         n = YssFun.dateDiff(this.dStartDate,this.dEndDate);
         for(j = 0;j <= n;j ++){

         fDate = YssFun.addDay(this.dStartDate,j);

				strSql = "select distinct FCuryCode, FCashAccCode from "
						+ pub.yssGetTableName("Tb_Stock_Cash")
						+ "  where FCheckState = 1 and FStorageDate = "
						+ dbl.sqlDate(fDate)
						+ " and FYearMonth = "
						+ dbl.sqlString(YssFun.formatDate(fDate, "yyyyMM"))
						+ " union select distinct b2.FTradeCury, FCashAccCode from "
						+ " (select FSecurityCode, FCashAccCode from "
						+ pub.yssGetTableName("tb_data_subtrade")
						+ " where FCheckState = 1 and FBargainDate = "
						+ dbl.sqlDate(fDate)
						+ " ) b1 left join (select FTradeCury,FSecurityCode from "
						+ pub.yssGetTableName("tb_para_security")
						+ " where FCheckState = 1) b2 on b1.FSecurityCode = b2.FSecurityCode";

				rs = dbl.openResultSet(strSql);

				while (rs.next()) {
					for (int i = 0; i < sValCashDefineAry.length; i++) {
						tNode = new YssTreeNode();
						tNode.setCode(builderCode(rs, i + 1, hmField,
								sValCashDefine));
						if (i == 0) {
							tNode.setParentCode("[root]");
						} else {
							tNode.setParentCode(builderCode(rs, i, hmField,
									sValCashDefine));
						}
						tNode.setOrderCode((String) hmIndex.get(tNode
								.getParentCode()));
						if (!hmData.containsKey(tNode.getCode())) {
							hmData.put(tNode.getCode(), tNode);
							sOrderIndex = (String) hmIndex.get(tNode
									.getParentCode());
							iOrder = Integer.parseInt(YssFun.right(sOrderIndex,
									3));
							hmIndex.put(tNode.getCode(), sOrderIndex + "001");
							iOrder++;
							sOrderIndex = sOrderIndex.substring(0, sOrderIndex
									.length() - 3)
									+ YssFun.formatNumber(iOrder, "000");
							hmIndex.put(tNode.getParentCode(), sOrderIndex);
						}
					}
				}
				insertCashForeTable(hmData, fDate, "tb_Temp_CashForeTree_");
			}
			adjustCashForeTable("tb_Temp_CashForeTree_");
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	private String builderCode(ResultSet rs, int idx, HashMap hmField,
			String sDefine) throws YssException {
		String[] sDefineAry = null;
		String sField = "";
		StringBuffer buf = new StringBuffer();
		try {
			sDefineAry = sDefine.split(";");
			for (int i = 0; i < idx; i++) {
				sField = (String) hmField.get(sDefineAry[i]);
				buf.append(rs.getString(sField) + "").append("\f");
			}
			if (buf.length() > 0) {
				buf.setLength(buf.length() - 1);
			}
			return buf.toString();
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
	}

	protected void insertCashForeTable(HashMap hmData, java.util.Date fDate,
			String sTableName) throws YssException {
		String strSql = "";

		String sCuryCode = "";
		String sCashAccCode = "";
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement pstmt = null;
		YssTreeNode tNode = null;
		try {
			Iterator iter = hmData.values().iterator();
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "insert into "
					+ pub.yssGetTableName(sTableName + pub.getUserCode())
					+ " (FCode,FName,FPortCode,FInvMgrCode,FCuryCode,FCashAccCode,FParentCode,FOrderCode,FDate) values (?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(strSql);
			while (iter.hasNext()) {
				tNode = (YssTreeNode) iter.next();

				sCuryCode = this.getCuryCode(tNode.getCode());
				sCashAccCode = this.getCashAccCode(tNode.getCode());
				tNode.setName(this.getItemName(tNode.getCode()));

				pstmt.setString(1, tNode.getCode());
				pstmt.setString(2, tNode.getName());
				pstmt.setString(3, this.portCode);
				pstmt.setString(4, this.invmgrCode);
				pstmt.setString(5, sCuryCode);
				pstmt.setString(6, sCashAccCode);
				pstmt.setString(7, tNode.getParentCode());
				pstmt.setString(8, tNode.getOrderCode());
				pstmt.setDate(9, YssFun.toSqlDate(fDate));

				pstmt.executeUpdate();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeStatementFinal(pstmt);// add by rujiangpeng 20100603打开多张报表系统需重新登录
			dbl.endTransFinal(conn, bTrans);
		}
	}

	protected void adjustCashForeTable(String sTableName) throws YssException {
		String strSql = "";
		int j = 0;
		int n = 0;
		java.util.Date fDate;
		String sCuryCode = "";
		String sCashAccCode = "";
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		PreparedStatement pstmt = null;
		YssTreeNode tNode = null;
		try {

			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "insert into "
					+ pub.yssGetTableName(sTableName + pub.getUserCode())
					+ " (FCode,FName,FPortCode,FInvMgrCode,FCuryCode,FCashAccCode,FParentCode,FOrderCode,FDate) values (?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(strSql);

			n = YssFun.dateDiff(this.dStartDate, this.dEndDate);
			for (j = 0; j <= n; j++) {

				fDate = YssFun.addDay(this.dStartDate, j);

				pstmt.setString(1, " ");
				pstmt.setString(2, " ");
				pstmt.setString(3, " ");
				pstmt.setString(4, " ");
				pstmt.setString(5, " ");
				pstmt.setString(6, " ");
				pstmt.setString(7, " ");
				pstmt.setString(8, "0");
				pstmt.setDate(9, YssFun.toSqlDate(fDate));

				pstmt.executeUpdate();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	protected String getCuryCode(String sNodeCode) {
		String[] sNodeAry = null;
		String sCode = "";
		sNodeAry = sNodeCode.split("\f");
		sCode = sNodeAry[0];
		return sCode;
	}

	protected String getCashAccCode(String sNodeCode) {
		String[] sNodeAry = null;
		String sCode = "";
		sNodeAry = sNodeCode.split("\f");
		if (sNodeAry.length == 2) {
			sCode = sNodeAry[1];
		} else if (sNodeAry.length == 1) {
			sCode = " ";
		}
		return sCode;
	}

	protected String getItemName(String sNodeCode) throws YssException {
		String sCode = "";
		String sResult = "";
		String[] sNodeAry = null;
		String strSql = "";
		ResultSet rs = null;
		try {
			sNodeAry = sNodeCode.split("\f");
			sCode = sNodeAry[sNodeAry.length - 1];
			if (sNodeAry.length == 1) {
				// 币种从币种设置表中获取
				strSql = "select FCuryCode,FCuryName as FItemName from "
						+ pub.yssGetTableName("Tb_Para_Currency")
						+ " where FCheckState = 1 " + " and FCuryCode = "
						+ dbl.sqlString(sCode);
			} else if (sNodeAry.length == 2) {
				// 币种从币种设置表中获取
				strSql = "select FCashAccCode,FCashAccName as FItemName from "
						+ pub.yssGetTableName("Tb_Para_Cashaccount")
						+ " where FCheckState = 1 " + " and FCashAccCode = "
						+ dbl.sqlString(sCode);
			}
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				if (sNodeAry.length == 1) {
					sResult = "`" + rs.getString("FItemName") + "";
				} else if (sNodeAry.length == 2) {
					sResult = "`    " + rs.getString("FItemName") + "";
				}
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

}
