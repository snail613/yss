package com.yss.main.operdata.futures;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.futures.pojo.FuturesHedgingSetBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * MS01133 现有版本增加国内期货业务及套期保值处理 QDV4深圳2010年04月28日01_A rujiangpeng 20100518
 * 
 * @author ru
 * 
 */
public class FuturesHedgingSetAdmin extends BaseDataSettingBean implements
		IDataSetting {
	FuturesHedgingSetBean futuresHedgingSet = new FuturesHedgingSetBean();
	private FuturesHedgingSetAdmin filterType;
	private String sRecycled = ""; // 回收站数据

	/*
	 * 新建数据
	 */
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_FuturesHedging") +
                "(FNum, FSecurityCode,FTradeAmount,FTradeMoney,FHedgingType,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
                "values(" +
                dbl.sqlString(this.futuresHedgingSet.getsFNum())+","+
                dbl.sqlString(this.futuresHedgingSet.getsSecurityCode())+","+
                this.futuresHedgingSet.getdFTradeAmount()+","+
                this.futuresHedgingSet.getdFTradeMoney()+","+
                dbl.sqlString(this.futuresHedgingSet.getsFHedgingType())+","+
                (pub.getSysCheckState() ? "0" : "1") + "," +  
                dbl.sqlString(this.creatorCode) + "," +   
                dbl.sqlString(this.creatorTime) + "," +
                //------ modify by nimengjing 2010.11.12 BUG #286 期货套期保值界面问题 
                (pub.getSysCheckState() ? "' '" :
                dbl.sqlString(this.checkUserCode))+","+
                (pub.getSysCheckState() ? "' '" :
                dbl.sqlString(this.checkTime))+")";
                //---------------------------------BUG #286------------------------------//
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            if(this.futuresHedgingSet.getsHedgSecurityData().trim().length() > 0){
            	FuturesHedgingSecuritySetAdmin hedgingSecurityAdmin = new  FuturesHedgingSecuritySetAdmin();
            	hedgingSecurityAdmin.setYssPub(pub);
            	hedgingSecurityAdmin.saveMutliSetting(this.futuresHedgingSet.getsHedgSecurityData(),this.futuresHedgingSet.getsFNum());
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加期货套期保值数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return this.buildRowStr();
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper, pub
				.yssGetTableName("Tb_Data_FuturesHedging"), "FNum",
				this.futuresHedgingSet.getsFNum(), this.futuresHedgingSet
						.getsOldFNum());
	}

	// 将回收站中的书库还原到未审核状态中
	public void checkSetting() throws YssException {
		StringBuffer buff = null;
		String[] array = null;
		boolean bTrans = true;
		Connection conn = dbl.loadConnection();
		Statement st = null;
		try {
			buff = new StringBuffer();
			if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) {
				array = sRecycled.split("\r\n");
				st = conn.createStatement();
				for (int i = 0; i < array.length; i++) {
					if (array[i].length() == 0) {
						continue;
					}
					this.parseRowStr(array[i]);
					buff.append(" update ");
					buff.append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
					buff.append(" set FCheckState =").append(this.checkStateId);
					buff.append(" ,FCheckUser =").append(
							dbl.sqlString(pub.getUserCode()));
					//------ modify by nimengjing 2010.11.12 BUG #286 期货套期保值界面问题 
					// edited by zhouxiang MS01515 期货套期保值信息设置新建数据保存后复核时间显示格式不正确
					 buff.append(" ,FCheckTime = '").append(YssFun.formatDatetime(new java.util.Date()));
					// end-- by zhouxiang MS01515 期货套期保值信息设置新建数据保存后复核时间显示格式不正确
					//---------------------------------BUG #286------------------------------//
					buff.append("' where FNum=").append(
							dbl.sqlString(this.futuresHedgingSet.getsFNum()));
					st.addBatch(buff.toString());
					buff.delete(0, buff.length());
				}
				st.executeBatch();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("还原保证金账户设置信息出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	// 将未审核中的数据删除放到回收站中
	public void delSetting() throws YssException {
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		StringBuffer buff = null;
		try {
			buff = new StringBuffer();
			buff.append(" update ");
			buff.append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
			buff.append(" set FCheckState =").append(this.checkStateId);
			buff.append(" ,FCheckUser =").append(
					dbl.sqlString(pub.getUserCode()));
			buff.append(" ,FCheckTime =").append(
					dbl.sqlDate(new java.util.Date()));
			buff.append("where FNum=").append(
					dbl.sqlString(this.futuresHedgingSet.getsFNum()));

			conn.setAutoCommit(false);
			dbl.executeSql(buff.toString());
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除数据出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// 将回收站中的数据删除
	public void deleteRecycleData() throws YssException {
		StringBuffer buff = null;
		String[] array = null;
		String strSql1 = "";
		boolean bTrans = true;
		Connection conn = dbl.loadConnection();
		Statement st = null;
		try {
			buff = new StringBuffer();
			if (null != sRecycled && !"".equalsIgnoreCase(sRecycled.trim())) {
				array = sRecycled.split("\r\n");
				st = conn.createStatement();
				for (int i = 0; i < array.length; i++) {
					if (array[i].length() == 0) {
						continue;
					}
					this.parseRowStr(array[i]);
					buff.append("delete from ");
					buff.append(pub.yssGetTableName("Tb_Data_FuturesHedging"));
					buff.append(" where FNum=").append(
							dbl.sqlString(this.futuresHedgingSet.getsFNum()));
					st.addBatch(buff.toString());
					buff.delete(0, buff.length());
					strSql1 = "delete from  "
							+ pub.yssGetTableName("TB_Data_FutHedgSecurity")
							+ " where FNUM = "
							+ dbl.sqlString(this.futuresHedgingSet
									.getsOldFNum());
					dbl.executeSql(strSql1);
				}
				st.executeBatch();
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除数据出错！", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	// 修改数据
	public String editSetting() throws YssException {
		String strSql = "";
		String strSql1 = "";
		String num = "";
		String strNumDate = "";
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			strSql = "delete from  "
					+ pub.yssGetTableName("Tb_Data_FuturesHedging")
					+ " where FNUM = "
					+ dbl.sqlString(this.futuresHedgingSet.getsOldFNum());

			strSql1 = "delete from  "
					+ pub.yssGetTableName("TB_Data_FutHedgSecurity")
					+ " where FNUM = "
					+ dbl.sqlString(this.futuresHedgingSet.getsOldFNum());

			conn.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			dbl.executeSql(strSql1);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			this.addSetting();

		} catch (Exception e) {
			throw new YssException("修改期货套期保值数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return this.buildRowStr();
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	/**
	 * 拼接数据
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buff = null;
		try {
			buff = new StringBuffer();
			buff.append(this.futuresHedgingSet.getsFNum()).append("\t");
			buff.append(this.futuresHedgingSet.getsTradeDate()).append("\t");
			buff.append(this.futuresHedgingSet.getsSecurityCode()).append("\t");
			buff.append(this.futuresHedgingSet.getsSecurityName()).append("\t");
			buff.append(this.futuresHedgingSet.getdFTradeAmount()).append("\t");
			buff.append(this.futuresHedgingSet.getdFTradeMoney()).append("\t");
			buff.append(this.futuresHedgingSet.getsFHedgingType()).append("\t");
			buff.append(this.futuresHedgingSet.getsTradeTypeCode())
					.append("\t");
			buff.append(this.futuresHedgingSet.getsTradeTypeName())
					.append("\t");
			buff.append(this.futuresHedgingSet.getsBrokeCode()).append("\t");
			buff.append(this.futuresHedgingSet.getsBrokeName()).append("\t");
			buff.append(this.futuresHedgingSet.getsInvMgrCode()).append("\t");
			buff.append(this.futuresHedgingSet.getsInvMgrName()).append("\t");
			buff.append(this.futuresHedgingSet.getsPortCode()).append("\t");
			buff.append(this.futuresHedgingSet.getsPortName()).append("\t");

			buff.append(super.buildRecLog());
		} catch (Exception e) {
			throw new YssException("拼接数据出错！", e);
		}
		return buff.toString();
	}

	public String getOperValue(String sType) throws YssException {
		String sretureData = "";
		try {
			if (sType != null && sType.indexOf("getBargaindate") != -1) {
				sretureData = getBargaindate();
			}
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return sretureData;
	}

	/**
	 * 解析数据
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
				if (sRowStr.split("\r\t").length == 3) {
					this.futuresHedgingSet.setsHedgSecurityData(sRowStr
							.split("\r\t")[2]);
				}
			} else {
				sTmpStr = sRowStr;
			}
			sRecycled = sRowStr; // 把未解析的字符串先赋给sRecycled
			reqAry = sTmpStr.split("\t");
			this.futuresHedgingSet.setsFNum(reqAry[0]);
			this.futuresHedgingSet.setdFTradeAmount(YssFun.toDouble(reqAry[1]));
			this.futuresHedgingSet.setdFTradeMoney(YssFun.toDouble(reqAry[2]));
			this.futuresHedgingSet.setsFHedgingType(reqAry[3]);
			this.futuresHedgingSet.setsSecurityCode(reqAry[4]);
			this.futuresHedgingSet.setsSecurityName(reqAry[5]);
			this.futuresHedgingSet.setsPortCode(reqAry[6]);
			this.futuresHedgingSet.setsPortName(reqAry[7]);
			this.futuresHedgingSet.setsTradeTypeCode(reqAry[8]);
			this.futuresHedgingSet.setsTradeTypeName(reqAry[9]);
			this.futuresHedgingSet.setsBrokeCode(reqAry[10]);
			this.futuresHedgingSet.setsBrokeName(reqAry[11]);
			this.futuresHedgingSet.setsInvMgrCode(reqAry[12]);
			this.futuresHedgingSet.setsInvMgrName(reqAry[13]);
			this.futuresHedgingSet.setsTradeDate(reqAry[14]);
			this.checkStateId = YssFun.toInt(reqAry[15]);
			this.futuresHedgingSet.setsOldFNum(reqAry[16]);

			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0
					&& (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]"))) {
				if (this.filterType == null) {
					this.filterType = new FuturesHedgingSetAdmin();
					this.filterType.setYssPub(pub);
				}
				this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
			}
		} catch (Exception e) {
			throw new YssException("解析期货套期保值数据出错", e);
		}
	}

	/*
	 * (non-Javadoc)查看期货套期保值数据表信息
	 * 
	 * @see com.yss.main.dao.IClientListView#getListViewData1()
	 */
	public String getListViewData1() throws YssException {
		String sqlStr = "";
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		ResultSet rs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = getListView1Headers();
			sqlStr = "select a.*,e.fbargaindate,e.fportcode,e.fbrokercode,e.finvmgrcode,e.ftradetypecode,"
					+ " b.fusername as fcreatorname,c.fusername as fcheckusername,d.fsecurityname,"
					+ " g.fportname,"
					+ " h.ftradetypename,"
					+ " i.fbrokername," + " j.finvmgrname" + " from "
					+ pub.yssGetTableName("tb_data_futureshedging")
					+ " a "
					+ " join (select * from "
					+ pub.yssGetTableName("tb_data_futurestrade")
					+ "  where FCheckState = 1) e on a.fnum =e.FNum"
					+ " left join (select fusercode, fusername from tb_sys_userlist) b on a.fcreator =b.fusercode"
					+ " left join (select fusercode, fusername from tb_sys_userlist) c on a.fcheckuser = c.fusercode"
					+ " join (select * from "
					+ pub.yssGetTableName("tb_para_security")
					+ "  where FCheckState = 1 and FCatCode = 'FU') d on a.fsecuritycode = d.fsecuritycode"
					// add by yangheng MS01675 QDV4赢时胜(32上线测试)2010年8月30日02_B
					// 2010.09.02
					// +
					// " join (select * from "+pub.yssGetTableName("tb_para_portfolio")
					// +"  where FCheckState = 1) g on e.fportcode =g.fportcode"
					+ " left join (select FPortCode, FPortName "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
					+ " from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
//					+ dbl.sqlDate(new java.util.Date())//delete by songjie 2011.03.15 不以最大的启用日期查询数据
					+ " FCheckState = 1 ) g on e.FPortCode = g.FPortCode "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
					// -------------------------

					+ " join (select * from tb_base_tradetype where FCheckState = 1) h on e.ftradetypecode = h.ftradetypecode"
					+ " left join (select * from "
					+ pub.yssGetTableName("tb_para_broker")
					+ "  where FCheckState = 1) i on e.fbrokercode = i.fbrokercode"
					+ " left join (select * from "
					+ pub.yssGetTableName("tb_para_investmanager")
					+ "  where FCheckState = 1)j on e.finvmgrcode=j.finvmgrcode "
					+ this.buildFilterSql();
			yssPageInationBean.setsQuerySQL(sqlStr);
			yssPageInationBean.setsTableName("futureshedging");
			rs = dbl.openResultSet(yssPageInationBean);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK); // "/f/f"
			}

			// 删除数据结尾的两个\f
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f"
			+ this.getListView1ShowCols() + "\r\f"
			+ yssPageInationBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException("新增期货套期保值数据出错", e);
		}finally
		{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(dbl.getProcStmt());
		}
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException,
			YssException, SQLException {
		this.futuresHedgingSet.setsFNum(rs.getString("FNum"));// 交易编号
		this.futuresHedgingSet.setsTradeDate(YssFun.formatDate(rs
				.getDate("fbargaindate"), "yyyy-MM-dd"));
		this.futuresHedgingSet.setdFTradeAmount(rs.getDouble("FTradeAmount"));// 交易数量
		this.futuresHedgingSet.setdFTradeMoney(rs.getDouble("FTradeMoney"));// 交易金额
		this.futuresHedgingSet.setsFHedgingType(rs.getString("FHedgingType"));// 套期类型
		this.futuresHedgingSet.setsSecurityCode(rs.getString("FSecurityCode"));// 交易证券
		this.futuresHedgingSet.setsSecurityName(rs.getString("FSecurityName"));
		this.futuresHedgingSet.setsInvMgrName(rs.getString("FInvMgrName"));// 投资经理
		this.futuresHedgingSet.setsInvMgrCode(rs.getString("FInvMgrCode"));
		this.futuresHedgingSet.setsBrokeName(rs.getString("fbrokername"));// 交易券商
		this.futuresHedgingSet.setsBrokeCode(rs.getString("FBrokerCode"));
		this.futuresHedgingSet
				.setsTradeTypeName(rs.getString("ftradetypename"));// 交易方式
		this.futuresHedgingSet
				.setsTradeTypeCode(rs.getString("ftradetypecode"));
		this.futuresHedgingSet.setsPortName(rs.getString("FPortName"));// 投资组合
		this.futuresHedgingSet.setsPortCode(rs.getString("FPortCode"));
		super.setRecLog(rs);
	}

	private String getBargaindate() throws YssException {
		String sBargaindate = "";
		StringBuffer buff = null;
		ResultSet rs = null;
		try {
			buff = new StringBuffer();

			buff.append(" select * from ").append(
					pub.yssGetTableName("tb_data_futurestrade"));
			buff.append(" where FCheckState =1 and FNum = ").append(
					dbl.sqlString(this.futuresHedgingSet.getsFNum()));

			rs = dbl.openResultSet(buff.toString());
			if (rs.next()) {
				sBargaindate = YssFun.formatDate(rs.getDate("FBargaindate"),
						"yyyy-MM-dd");
			}

		} catch (Exception e) {
			throw new YssException("通过编号获取期货成交日期出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sBargaindate;
	}

	// 根据条件拼接SQL语句
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if (this.filterType != null) {
			sResult = " where 1=1";
			if (this.filterType.futuresHedgingSet.getsFNum().length() != 0) {
				sResult = sResult
						+ " and a.FNum = '"
						+ this.filterType.futuresHedgingSet.getsFNum()
								.replaceAll("'", "''") + "'";
			}
			if (this.filterType.futuresHedgingSet.getsFHedgingType().length() != 0) {
				if (!this.filterType.futuresHedgingSet.getsFHedgingType()
						.equalsIgnoreCase("All"))
					sResult = sResult
							+ " and FHedgingType = '"
							+ this.filterType.futuresHedgingSet
									.getsFHedgingType().replaceAll("'", "''")
							+ "'";
			}
			if (!this.filterType.futuresHedgingSet.getsTradeDate()
					.equalsIgnoreCase("1900-01-01")) {
				sResult = sResult
						+ " and FBargainDate = to_date('"
						+ this.filterType.futuresHedgingSet.getsTradeDate()
								.replaceAll("'", "''") + "'," + "'yyyy-MM-dd')";
			}
			if (this.filterType.futuresHedgingSet.getsSecurityCode().length() != 0) {
				sResult = sResult
						+ " and FSecurityCode = '"
						+ this.filterType.futuresHedgingSet.getsSecurityCode()
								.replaceAll("'", "''") + "'";
			}
		}
		return sResult;
	}

}
