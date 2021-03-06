package com.yss.main.platform.reconciliation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.YssPreparedStatement;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**
 * MS01620 关于电子对账需求
 * @包名：com.yss.main.platform.reconciliation
 * @文件名：RecGeneratorBean.java
 * @创建人：qiuxufeng
 * @创建时间：2010-10-09
 * @版本号：V4.1
 * @说明：
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-11 | qiuxufeng | V4.1 |
 */
public class RecGeneratorBean 
		extends BaseDataSettingBean implements IClientOperRequest {

	private String strRecSelect = "";//需要生成的对账表
	private String strPortCode = "";//组合代码
	private String strDateFrom = "";//开始日期
	private String strDateTo = "";//结束日期
	private HashMap hmSelect = new HashMap();
	private HashMap hmPortCodeSet = null;
	private String[] portCodeAry = null;
	private String tbName = "";
	private String filterType = "";
	private String rpType = "";
	private String fsnNum = "";
	private String fsnSque = "00000";
	private boolean recTrans = false;
	private String strReq = "";
	SingleLogOper logOper = null;
	private String strAssetCode = ""; // 资产代码 add by qiuxufeng 20110215 581
	
	
	public String getStrRecSelect() {
		return strRecSelect;
	}

	public void setStrRecSelect(String strRecSelect) {
		this.strRecSelect = strRecSelect;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getStrDateFrom() {
		return strDateFrom;
	}

	public void setStrDateFrom(String strDateFrom) {
		this.strDateFrom = strDateFrom;
	}

	public String getStrDateTo() {
		return strDateTo;
	}

	public void setStrDateTo(String strDateTo) {
		this.strDateTo = strDateTo;
	}

	public String checkRequest(String sType) throws YssException {
		return null;
	}
	
	public String doOperation(String sType) throws YssException {
		
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			if(sType.equalsIgnoreCase("build")) {
				runStatus.appendRunDesc("RecGenRun", "开始生成对账数据...\r\n");
				if(hmSelect.containsKey("subjects") && !recTrans) {
					strReq = doRecBuild("subjects");
					if(!recTrans) {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDSUB, pub);
					} else {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDSUB, pub, true);
					}
				}
				if(hmSelect.containsKey("Valuation") && !recTrans) {
					strReq = doRecBuild("Valuation");
					if(!recTrans) {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDJJGZB, pub);
					} else {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDJJGZB, pub, true);
					}
				}
				if(hmSelect.containsKey("balance") && !recTrans) {
					strReq = doRecBuild("balance");
					if(!recTrans) {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDBAL, pub);
					} else {
						logOper = SingleLogOper.getInstance();
		                logOper.setIData(this, YssCons.OP_BUILDBAL, pub, true);
					}
				}
				runStatus.appendRunDesc("RecGenRun", "结束生成对账数据。\r\n");
			}
//			if(recTrans) {
//				runStatus.appendRunDesc("RecGenRun", "生成出错，操作失败！\r\n");
//				strReq = "error";
//			}
			dbl.endTransFinal(conn, recTrans);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			strReq = "false";
			throw new YssException(e);
		} finally {
            dbl.endTransFinal(conn, bTrans);
        }
		return strReq;
	}

	/**
	 * 查询对账设置信息	返回所选组合代码
	 * @方法名：checkExist
	 * @参数：strRec
	 * @返回类型：HashMap
	 * @说明：
	 */
	public HashMap checkSet(String strRec) throws YssException {
		String strSql = "";
		String strResGroupCode = "";
		String strResPortCode = "";
		ResultSet rs = null;
		int tempSelected = 0;
		try {
			strSql = "select * " +
						"from Tb_base_accountpara " +
						//"where selected = 1 and ACCOUNTTYPE = '" +
						"where ACCOUNTTYPE = '" +
						strRec + "'";
			rs = dbl.openResultSet(strSql);
			hmPortCodeSet = new HashMap();
			boolean hmflag = false;
			while(rs.next()) {
				hmflag = true;
				tempSelected = rs.getInt("selected");
				if(tempSelected == 1) {
					strResGroupCode = rs.getString("FASSETGROUPCODE");
					strResPortCode = rs.getString("FPORTCODE");
					hmPortCodeSet.put(strResGroupCode + "-" + strResPortCode, strResPortCode);
				}
			}
			if(!hmflag) {
				hmPortCodeSet = null;
			}
		} catch (Exception e) {
			throw new YssException("读取 对账设置出错！\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return hmPortCodeSet;
	}
	
	public boolean checkSendStatus(String sDate) throws YssException {
		return checkSendStatus(null, sDate, null);
	}
	
	/**
	 * 查询对账报文处理的数据，返回true有该数据、反之无该数据
	 * @方法名：checkSendStatus
	 * @参数：filterType String	
	 * @参数：sDate String
	 * @返回类型：boolean
	 * @说明：
	 */
	public boolean checkSendStatus(String filterType, String sDate, String sPortCode) throws YssException {
		boolean status = false;
		String strSql = "";
		ResultSet rs = null;
		try {
			if(null != sPortCode && null != filterType) {
				strSql = "select * from " +
				
							" (select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
			    			" left join TDzJJGZB c on b.fsn = c.fsn where b.Ffiletype = '1011'" +//edited by zhouxiang 2010.12.31 不需要查询重复列 , c.ffundcode
			    			" union all" +
			    			" select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
			    			" left join TDzAccount c on b.fsn = c.fsn where b.Ffiletype = '1031'" +
			    			" union all" +
			    			" select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
			    			" left join TDzbalance c on b.fsn = c.fsn where b.Ffiletype = '1001'" +
			    			" ) d " +
				
							"where d.FFiletype = " +
							dbl.sqlString(filterType) + 
							" and d.fundcode = " +
							//dbl.sqlString(sPortCode) +
							// edit by qiuxufeng 20110215 481 生成对账表数据时，组合代码需改为资产代码
							dbl.sqlString(strAssetCode) +
							" and FIssend = 0";
//				if(filterType.equalsIgnoreCase("1031")) {
					strSql = strSql + " and d.FDATE = to_date('" + sDate + "', 'yyyy-MM-dd') "+" order by d.fundcode";
//				}
				
				rs = dbl.openResultSet(strSql);
				if(rs.next()) {
					status = true;
				}
				dbl.closeResultSetFinal(rs);
			} else {
				strSql = "select * from TDZBBINFO " +
							"where FDATE = " +
							dbl.sqlDate(YssFun.toDate(sDate)) +
							" order by FSN desc";
				rs = dbl.openResultSet(strSql);
				if(rs.next()) {
					status = true;
					fsnSque = rs.getString("FSN");
				} else {
					fsnSque = "00000";
				}
				dbl.closeResultSetFinal(rs);
			}
		} catch (Exception e) {
			throw new YssException("查询对账报文处理信息出错！" + e.getMessage(), e);
		}
		return status;
	}
	
	public String doRecBuild(String strRec) throws YssException {
		return doRecBuild(strRec, YssFun.toDate(this.strDateFrom), YssFun.toDate(this.strDateTo));
	}
	
	public String doRecBuild(String recSel, Date dateBegin, Date dateEnd) throws YssException {
		try {
			if(recSel.equalsIgnoreCase("subjects")) {
				tbName = "TDzAccount";
				filterType = "1031";
				rpType = " ";//为空
			}
			else if(recSel.equalsIgnoreCase("Valuation")) {
				tbName = "TDzJJGZB";
				filterType = "1011";
				rpType = "01";
			}
			else if(recSel.equalsIgnoreCase("balance")) {
				tbName = "TDzbalance";
				filterType = "1001";
				rpType = "01";
			}
			else return "noRecSel";
			
//			if(checkSendStatus(filterType)) {
//				runStatus.appendRunDesc("RecGenRun", "【信息数据为待发送状态，不能重新生成！】\r\n");
//				recTrans = true;
//				return "noTB";//返回提示信息数据为待发送状态，不能重新生成
//			} else {
				runStatus.appendRunDesc("RecGenRun", "正在读取" + hmSelect.get(recSel) + "对账设置信息...\r\n");
				checkSet(recSel);//获取对账设置信息
				
				Connection conn = dbl.loadDZConnection(); //add by huangqirong 2012-09-06 bug #5553 添加加载旗舰版目标数据库连接[db_yssimsasORA]配置
				
				for (int i = 0; i < portCodeAry.length; i++) {
					if(!recTrans){
						String tempPortcode = portCodeAry[i].split("-")[1];
						strAssetCode = getAssetCode(tempPortcode); // add by qiuxufeng 581 生成对账表数据时，组合代码需改为资产代码
						int tempCtl = 1;
						if(null != hmPortCodeSet) {
							if(hmPortCodeSet.containsKey(portCodeAry[i])) {
								//有该组合
								tempPortcode = (String)hmPortCodeSet.get(portCodeAry[i]);
								tempCtl = 1;
							} else {
								//没有该组合
								tempPortcode = portCodeAry[i].split("-")[1];
								tempCtl = 0;
							}
						}
						if(recSel.equalsIgnoreCase("subjects")) {
							subBuild(tempPortcode,tempCtl);
							//modify by huangqirong 2012-09-06 bug #5553 判断是否已添加加载旗舰版目标数据库连接[db_yssimsasORA]配置
							if(conn != null){  
								conn.close();
								subBuildDz(tempPortcode,tempCtl); //add by zhangjun 2012.06.01 story#2420------------
							}
							//---end---
						}
						else if(recSel.equalsIgnoreCase("Valuation")) {
							valBuild(tempPortcode,tempCtl);
							//modify by huangqirong 2012-09-06 bug #5553 判断是否已添加加载旗舰版目标数据库连接[db_yssimsasORA]配置
							if(conn != null){								
								conn.close();
								valBuildDz(tempPortcode,tempCtl);//add by zhangjun 2012.06.01 story#2420------------
							}
							//---end---
						}
						else if(recSel.equalsIgnoreCase("balance")) {
							balBuild(tempPortcode,tempCtl);
							//modify by huangqirong 2012-09-06 bug #5553 判断是否已添加加载旗舰版目标数据库连接[db_yssimsasORA]配置
							if(conn != null) {
								conn.close();//modify by huangqirong 2012-09-06 关闭连接
								balBuildDz(tempPortcode,tempCtl);//add by zhangjun 2012.06.01 story#2420------------
							}
							//---end---//modify by huangqirong 2012-09-06 加上注释结尾
						}
					} else {
						return strReq;
					}
				}
//			}
				if(strReq.equalsIgnoreCase("")) {
					strReq = "error";
				}
			return strReq;
		} catch (Exception e) {
			throw new YssException("生成对账出错！", e);
		}
	}
	
	/**
	 * 生成FSN
	 * @throws YssException 
	 * @方法名：createFsn
	 * @参数：
	 * @返回类型：void
	 * @说明：
	 */
	public void createFsn(String date) throws YssException {
		String tempStr = "";
		if(fsnSque != "00000") {
			String fsn = fsnSque.substring(10, fsnSque.length());
			int tempFsn = Integer.parseInt(fsn);
			tempFsn++;
			fsn = String.valueOf(tempFsn);
			if(fsn.length() < 5) {
				for (int i = 0; i < 5 - fsn.length(); i++) {
					tempStr += "0";
				}
				fsn = tempStr + fsn;
			}
			fsnNum = "DZ" + YssFun.formatDate(date,"yyyyMMdd") + fsn;
		} else {
			fsnNum = "DZ" + YssFun.formatDate(date,"yyyyMMdd") + fsnSque;
		}
	}
	
//	private String getOwner() throws YssException {
//		int i;
//		String[] pa = null;
//		String dbUser = "";
//		pa = YssFun.loadTxtFile("/dbsetting.txt").split("\r\n");
//		for (i = 0; i < pa.length; i++) {
//            if (pa[i].trim().equalsIgnoreCase("[db_yssimsas]")) {
//                break;
//            }
//        }
//		dbUser = pa[i + 3].trim();//得到数据库用户名
//		return dbUser;
//	}
	
	/**
	 * 科目对账数据生成
	 * @throws YssException 
	 * @方法名：subBuild
	 * @参数：fun	int
	 * @返回类型：void
	 * @说明：
	 */
	public void subBuild(String portCodeSel, int fun) throws YssException {
		String strSql = "";
		String subTbName = "";
		ResultSet rs = null;
		YssFinance cw = new YssFinance();

        String set = "";
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCodeSel);
		subTbName = "A" + String.valueOf(YssFun.getYear(YssFun.toDate(this.strDateTo))) + set +"LACCOUNT";//构造对应的科目表名

		try {
			if(!checkSendStatus(filterType, this.strDateTo, portCodeSel)) {
//				strSql = "select * from all_all_tables where table_name = '" +
//							subTbName + "' and owner = '" +
//							getOwner().toUpperCase() + "'";
//				rs = dbl.openResultSet(strSql);//查询是否存在该表
				if(dbl.yssTableExist(subTbName)) {
					//对账所有科目，部分明细科目
					dbl.closeResultSetFinal(rs);
					if(fun == 1) {
						strSql = "select DISTINCT * from " + subTbName;//包括明细科目
					} else if(fun == 0) {
						strSql = "select DISTINCT * from " + subTbName + " where FACCTDETAIL <> 1";//不包括明细科目
					}
					rs = dbl.openResultSet(strSql);
	
					checkSendStatus(null, this.strDateTo, null);//获取最近的fsn
					createFsn(this.strDateTo);//生成FSN					
					
					strSql = "insert into " + tbName + "(FFILETYPE, FFUNDCODE, FRPTTYPE, FBDATE, FEDATE, FACCTCODE," +
								" FACCTNAME, FACCTLEVEL, FACCTPARENT, FACCTDETAIL, FACCTCLASS, FBALDC, FSN) values('" +
								filterType + "','" +
								//portCodeSel + "',''," +
								// edit by qiuxufeng 20110215 生成对账表数据时，组合代码需改为资产代码
								strAssetCode + "',''," +
								dbl.sqlDate(YssFun.toDate(this.strDateTo)) + "," +
								dbl.sqlDate(YssFun.toDate(this.strDateTo)) + ",?,?,?,?,?,?,?,'" +
								fsnNum + "')";
					PreparedStatement ps = dbl.openPreparedStatement(strSql);//批量执行插入
					boolean recordContr = false;
					String strAcctClass = null;
					while(rs.next()) {
						recordContr = true;
						ps.setString(1, rs.getString("FAcctCode"));
						ps.setString(2, rs.getString("FAcctName"));
						ps.setInt(3, rs.getInt(("FAcctLevel")));
						ps.setString(4, rs.getString("FAcctParent"));
						ps.setInt(5, rs.getInt("FAcctDetail"));
						//ps.setString(6, rs.getString("FAcctClass"));
						// 615 QDV4华安2011年2月23日01_AB by qiuxufeng 20110302 对账科目表中的FAcctClass字段数字形式
						strAcctClass = convertAcctClass(rs.getString("FAcctClass"));
						if(null == strAcctClass) {
							recTrans = true;
							throw new YssException("【" + rs.getString("FAcctClass") + "】没有对应数字代码！");
						}
						ps.setInt(6, Integer.parseInt(strAcctClass));
						// 615 QDV4华安2011年2月23日01_AB by qiuxufeng 20110302 对账科目表中的FAcctClass字段数字形式 end
						ps.setInt(7, rs.getInt("FBalDC"));
						//ps.setString(8, rs.getString(fsnNum));
						ps.addBatch();
					}
					runStatus.appendRunDesc("RecGenRun", "正在生成" + portCodeSel + "的科目对账数据...\r\n");
					ps.executeBatch();
					dbl.closeResultSetFinal(rs);
					dbl.closeStatementFinal(ps);
					if(recordContr) {						
						//往电子对账对账报文处理信息表中插入记录
						strSql = "insert into Tdzbbinfo(FSN, FDATE, FZZR, FSHR, FSH, FISSEND, FSDR, FFILETYPE, FRPTTYPE, FFundCode) values('" +
									fsnNum + "'," +
									dbl.sqlDate(YssFun.toDate(this.strDateTo)) + ",'" +
									pub.getUserCode() + "', '" +
									pub.getUserCode() + "', 1, 0, ' ', '" +
									filterType + "', '" +
									rpType + "', " +
									dbl.sqlString(strAssetCode) + ")"; // edit by qiuxufeng 20110215 581 报文信息表增加基金代码FFundCode
						dbl.executeSql(strSql);
						runStatus.appendRunDesc("RecGenRun", "生成" + portCodeSel + " " + this.strDateTo + "的科目对账数据...\r\n");
						strReq = "true";
					} else {
						runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + this.strDateTo +  "的科目对账数据...\r\n");
						recTrans = true;
						strReq = "noSJ\r\t" + "没有" + portCodeSel + "的科目对账数据";
						return;
					}
				} else {
					runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + this.strDateTo +  "的科目对账表！\r\n");
					strReq = "noTB\r\t没有" + portCodeSel +  "的科目对账表！";
					recTrans = true;
					return;
				}
			} else {
				runStatus.appendRunDesc("RecGenRun", "【科目对账】\r\n" + portCodeSel + " " + this.strDateTo + " 为待发送状态，不能重新生成！\r\n");
				recTrans = true;
				strReq = "1\r\t【科目对账】 " + portCodeSel + " " + this.strDateTo + " 为待发送状态，不能重新生成！";//返回提示信息数据为待发送状态，不能重新生成
				return;
			}
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun", "科目对账数据生成出错！\r\n");
			throw new YssException("科目对账数据生成出错！" + e.getMessage(), e);
		}
	}
	
	/**
	 * 估值对账数据生成
	 * @throws YssException 
	 * @方法名：valBuild
	 * @参数：fun	int
	 * @返回类型：void
	 * @说明：
	 */
	public void valBuild(String portCodeSel, int fun) throws YssException {
		String strSql = "";
		String subTbName = "";
		ResultSet rs = null;
		String strFsnDate = "";
		double markValue = 0;
		double amount = 0;
		double cost = 0;
		double markPrice = 0;
		HashMap rateHash = new HashMap(32);//存放各币种对应对账货币的汇率数据   by yanghaiming
		double baseRate = 0.0;//保存对账货币的基础汇率
		double rateResult = 0.0;//保存估值表中货币的基础汇率
		double insertPrice = 0.0;//计算对账货币金额用
		String acctCode = "";//保存上一条数据的科目代码
		//double val = 0;//净值
		double appreciation = 0;//估值增值
		double valToRatio = 0;//占净值比
		double costToRatio = 0;//成本比
		String strSql1 = "";
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		YssFinance cw = new YssFinance();
		String set = "";
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCodeSel);
		subTbName = "TB_" + pub.getPrefixTB() + "_REP_GUESSVALUE";//构造对应的估值表表名  modify by fangjiang 2011.04.24 BUG 1773 选择相应日期，选择相应组合，选择估值对账表，处理时报错
		String tempBZ = null; // 本位币币种代码  by qiuxufeng 20110302
		String sTbLacc = "a" + this.strDateFrom.substring(0, 4) + set + "laccount";

		try {
//			strSql = "select * from all_all_tables where table_name = '" +
//						subTbName + "' and owner = '" +
//						getOwner().toUpperCase() + "'";
//			rs = dbl.openResultSet(strSql);//查询是否存在该表
			if(dbl.yssTableExist(subTbName)) {				
								
				// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
				// 获取该组合的本位币币种代码
				strSql = "select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCodeSel);
				rs = dbl.openResultSet(strSql);
				while(rs.next()) {
					if(rs.getString("FPortCury").trim().length() > 0 && null != rs.getString("FPortCury")) {
						tempBZ = rs.getString("FPortCury");
					}
				}
				if(null == tempBZ || tempBZ.trim().length() == 0) {
					recTrans = true;
					throw new YssException("未设置组合货币！"); 
				}
				// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 end
				int iDate = YssFun.dateDiff(YssFun.toDate(this.strDateFrom), YssFun.toDate(this.strDateTo));
				for(int i = 0; i < iDate + 1; i++) {
					acctCode = "";
					strFsnDate = YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.strDateFrom), i));//当前日期处理
					if(!checkSendStatus(filterType, strFsnDate, portCodeSel)) {
						// 去除通用币种设置 by qiuxufeng 20110302
//						CtlPubPara tempCtlPubPara = new CtlPubPara();
//						tempCtlPubPara.setYssPub(pub);
//						String tempBZ = tempCtlPubPara.getRateCalculateType("RecContrlType", "SelectControl1", 4); // 获取通用币种设置信息
//						if(tempBZ.length() > 0 && null != tempBZ && tempBZ.indexOf("|") > 0) {
//							tempBZ = tempBZ.substring(0, tempBZ.indexOf("|")); // 获取通用币种设置的币种
//						} else {
//							runStatus.appendRunDesc("RecGenRun", "【估值对账】\r\n未设置通用币种\r\n");
//							recTrans = true;
//							strReq = "2\r\t未设置，请先设置通用币种！";//返回提示信息数据为待发送状态，不能重新生成
//							return;
//						}
						BaseOperDeal operDeal = new BaseOperDeal();
			            operDeal.setYssPub(pub);
			            baseRate = operDeal.getCuryRate(YssFun.toDate(strFsnDate), tempBZ,
			            		portCodeSel, "base");
						strSql = "select a.fcurcode from " + subTbName + " a " +
						" left join (" +
						"select b.*, c.FSETCODE from " + pub.yssGetTableName("Tb_para_portfolio") + " b" +
						" left join lsetlist c on b.FASSETCODE = c.FSETID) d " +
						" on a.fportcode = d.fsetcode " +
						" where FDATE = " +
						dbl.sqlDate(YssFun.toDate(strFsnDate)) +
						" and d.fportcode = " + dbl.sqlString(portCodeSel) +
						" group by a.fcurcode";
						rs1 = dbl.openResultSet(strSql);
						while (rs1.next()){
							rateResult = operDeal.getCuryRate(YssFun.toDate(strFsnDate), rs1.getString("fcurcode"),
				            		portCodeSel, "base");
							rateHash.put(rs1.getString("fcurcode"), String.valueOf(rateResult/baseRate)); // 保存所有币种换算成本位币的汇率
						}
						dbl.closeResultSetFinal(rs1);
						strSql = "select a.FAcctCode,a.FAcctName," +
								// edit by qiuxufeng 20110319 484 QDV4赢时胜深圳2011年1月7日02_A
								//" a.FMarketValue," +
								" (case when b.fcurcode = '***' then 0 when a.facctcode = '8700' then a.fcost else a.fmarketvalue end)" +
								" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue," +
								//" a.FCURCODE," + 
								" case when b.fcurcode = '***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode," +
								" a.FAcctClass," + // edit by qiuxufeng 20110319 484 QDV4赢时胜深圳2011年1月7日02_A
								//" a.FAmount," +
								" (case when b.fcurcode = '***' then 0 else a.famount end)" +
								" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount," +
								//" a.FCost," +
								" (case when b.fcurcode = '***' then 0 else a.fcost end)" +
								" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fcost," +
								" a.FMarketPrice,a.FAppreciation," + 
								//" a.FStandardMoneyCost," +
								" a.fstandardmoneycost" +
								" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost," +
								//" a.FStandardMoneyMarketValue," +
								" (case when a.facctcode = '8700' then a.fcost else a.fstandardmoneymarketvalue end)" +
								" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue," +
								// end edit by qiuxufeng 20110319 484 QDV4赢时胜深圳2011年1月7日02_A
								" a.FStandardMoneyAppreciation," +
								" a.FCostToNetRatio,a.FMarketValueToRatio,a.FAcctDetail from " + subTbName + " a " +
								" left join " + sTbLacc + " b on a.facctcode = b.facctcode" + // add by qiuxufeng 20110319
								" left join (select bb.fportcode,c.FSETCODE from " + pub.yssGetTableName("Tb_para_portfolio") + " bb" +
								" left join (select FSETCODE,FSETID from lsetlist group by FSETCODE,FSETID) c on bb.FASSETCODE = c.FSETID) d " +
								" on a.fportcode = d.fsetcode " +
								" where FDATE = " +
								dbl.sqlDate(YssFun.toDate(strFsnDate)) +
								// edit by qiuxufeng 20110319 484 QDV4赢时胜深圳2011年1月7日02_A
								" and ((a.facctclass = '合计' and length(Trim(a.fcurcode)) is null) or" +
								" (a.facctclass = '合计' and a.facctcode = '8600' and length(Trim(a.fcurcode)) is null) or" +
								" (a.facctclass <> '合计'))" +
								" and a.FAcctCode <> 'C100'" +
								" and a.FAcctCode not in ('8810', '8811', '8812')" +
								// end edit by qiuxufeng 20110319 484 QDV4赢时胜深圳2011年1月7日02_A
								" and d.fportcode = " + dbl.sqlString(portCodeSel) + " order by FACCTCODE";
						rs1 = dbl.openResultSet(strSql);
						
						checkSendStatus(null, strFsnDate, null);//获取最近的fsn
						createFsn(strFsnDate);//生成FSN
						
						strSql = "insert into " + tbName + " values('" +
									filterType + "','" +
									//portCodeSel + "','01'," +
									// edit by qiuxufeng 20110215 581 生成对账表数据时，组合代码需改为资产代码
									strAssetCode + "','01'," +
									dbl.sqlDate(strFsnDate) + "," +
									dbl.sqlDate(strFsnDate) + ",?,?,?,?,?,?,?,?,?,?,?,'" +
									fsnNum + "')";
						//System.out.println(strSql);
						PreparedStatement ps = dbl.openPreparedStatement(strSql);//批量执行插入
						boolean recordContr = false;
						while(rs1.next()) {
							recordContr = true;
							if(rs1.getString("FAcctClass").indexOf("class")>-1){//财务估值表的组合分级不生成
								continue;
							}
							if(!acctCode.equalsIgnoreCase(rs1.getString("FAcctCode")) && acctCode != ""){
								ps.addBatch();
							}
							
							if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){ // 币种为本位币，直接取值
								//val = rs1.getDouble("FCost");
								markPrice = rs1.getDouble("FMarketPrice");
							}else{ // 币种不为本位币，通过汇率换算成通用币种的金额
								//val = YssFun.roundIt(rs1.getDouble("FCost") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
								markPrice = rs1.getDouble(("FMarketPrice"))* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString());
							}
							//20111011 modified by liubo.Story #1598
							//资产类合计的科目号由8800变为0013，负债类合计的科目号由8801变为0014，资产净值的科目号由9000变为0015，单位净值的科目号由9600变为0016
							//=========================================
							
//							ps.setString(1, rs1.getString("FAcctCode"));
							
							if ("8800".equals(rs1.getString("FAcctCode")))
							{
								ps.setString(1, "0013");
							}
							else if ("8801".equals(rs1.getString("FAcctCode")))
							{
								ps.setString(1, "0014");
							}
							else if ("9000".equals(rs1.getString("FAcctCode")))
							{
								ps.setString(1, "0015");
							}
							else if ("9600".equals(rs1.getString("FAcctCode")))
							{
								ps.setString(1, "0016");
							}
							else
							{
								ps.setString(1, rs1.getString("FAcctCode"));
							}
							
							//================end=========================
							String xmlKmmc ="";// "<!CDATA[" + rs1.getString("FAcctName") + "]]>";
							//edit by zhouwei 20120323 发送报文时数据加密!CDATA引发问题，报文系统无法解析
							String facctName=rs1.getString("FAcctName");
							xmlKmmc=facctName;
							if(facctName.indexOf("&")>-1){
								xmlKmmc=facctName.replace("&", " ");
								
							}							
							//ps.setString(2, rs1.getString("FAcctName")); modified by yeshenghong # 1817 20111128
							ps.setString(2, xmlKmmc);
							if(acctCode.equalsIgnoreCase(rs1.getString("FAcctCode"))){ // 与前一条的科目代码相同，值累加
								valToRatio += rs1.getDouble("FMarketValueToRatio");
								costToRatio += rs1.getDouble("FCostToNetRatio");
								amount += rs1.getDouble("FAmount");
								if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){
									insertPrice += rs1.getDouble("FMarketPrice");
									//cost += rs1.getDouble("FCost");
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
									cost += rs1.getDouble("FStandardMoneyCost"); // 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost(本位币成本)的值
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									//appreciation += rs1.getDouble("FAppreciation");
									if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){ // 如果为债券，按照对账设置取值
										if(fun == 1) {
											//债券市值=持仓数量*债券面额
											//--------------市值计算,表中无债权面额，暂直接取成本------------
											//markValue = amount * cost/amount;
											strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
															+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
											rs2 = dbl.openResultSet(strSql1);
											while(rs2.next()){
												markValue += YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE"),4);
											}
											dbl.closeResultSetFinal(rs2);
										} else if(fun == 0) {
											//债券市值=持仓数量*市价
											markValue += YssFun.roundIt(amount * markPrice,4);
										}
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}else{ // 非债券直接取值
										//markValue += rs1.getDouble("FMarketValue");
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										markValue += rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
										appreciation += rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}
								}else{
									//appreciation += YssFun.roundIt(rs1.getDouble("FAppreciation") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									insertPrice += YssFun.roundIt(rs1.getDouble("FMarketPrice") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									//cost += YssFun.roundIt(rs1.getDouble("FCost") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
									// 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost的值
									cost += rs1.getDouble("FStandardMoneyCost");
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
										if(fun == 1) {
											//债券市值=持仓数量*债券面额
											//--------------市值计算,表中无债权面额，暂直接取成本------------
											//markValue = amount * cost/amount;
											strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
													+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
											rs2 = dbl.openResultSet(strSql1);
											while(rs2.next()){
												markValue += YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
											}
											dbl.closeResultSetFinal(rs2);
										} else if(fun == 0) {
											//债券市值=持仓数量*市价
											markValue += YssFun.roundIt(amount * markPrice,4);
										}
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}else{
										//markValue += YssFun.roundIt(rs1.getDouble("FMarketValue")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										markValue += rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
										appreciation += rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}
								}
								
							}else{
								valToRatio = rs1.getDouble("FMarketValueToRatio");
								costToRatio = rs1.getDouble("FCostToNetRatio");
								amount = rs1.getDouble("FAmount");
								if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){
									insertPrice = rs1.getDouble("FMarketPrice");
									//cost = rs1.getDouble("FCost");
									//appreciation = rs1.getDouble("FAppreciation");
									
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
									// 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost的值
									cost = rs1.getDouble("FStandardMoneyCost");
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									
									if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
										if(fun == 1) {
											//债券市值=持仓数量*债券面额
											//--------------市值计算,表中无债权面额，暂直接取成本------------
											//markValue = amount * cost/amount;
											strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
													+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
											rs2 = dbl.openResultSet(strSql1);
											while(rs2.next()){
												markValue = YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE"),4);
											}
											dbl.closeResultSetFinal(rs2);
										} else if(fun == 0) {
											//债券市值=持仓数量*市价
											markValue = amount * markPrice;
										}
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}else{
										//markValue = rs1.getDouble("FMarketValue");
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										markValue = rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
										appreciation = rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}
								}else{
									insertPrice = YssFun.roundIt(rs1.getDouble("FMarketPrice") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									//cost = YssFun.roundIt(rs1.getDouble("FCost") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									//appreciation = YssFun.roundIt(rs1.getDouble("FAppreciation") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
									
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
									// 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost的值
									cost = rs1.getDouble("FStandardMoneyCost");
									// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									
									if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
										if(fun == 1) {
											//债券市值=持仓数量*债券面额
											//--------------市值计算,表中无债权面额，暂直接取成本------------
											//markValue = amount * cost/amount;
											strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
													+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_')+1));
											rs2 = dbl.openResultSet(strSql1);
											while(rs2.next()){
												markValue = YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
											}
											dbl.closeResultSetFinal(rs2);
										} else if(fun == 0) {
											//债券市值=持仓数量*市价
											markValue = amount * markPrice;
										}
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}else{
										//markValue = YssFun.roundIt(rs1.getDouble("FMarketValue")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改
										markValue = rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
										appreciation = rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
										// 484 QDV4赢时胜深圳2011年1月7日02_A by qiuxufeng 20110302 电子对账取数规则需修改 end
									}
								}
								
								
							}
							
							//ps.setDouble(3, rs1.getDouble("FMarketValue"));
							ps.setDouble(3, YssFun.roundIt(insertPrice,4));
							ps.setString(4, "1");//0:平均价；1:收市价。可空
							ps.setDouble(5, YssFun.roundIt(amount,4));
							//ps.setDouble(6, rs1.getDouble("FCost"));
							ps.setDouble(6, YssFun.roundIt(cost,4));
							
							
//							if(fun == 1) {
//								//债券市值=持仓数量*债券面额
//								//--------------市值计算,表中无债权面额，暂直接取成本------------
//								//markValue = amount * cost/amount;
//								markValue = cost;
//							} else if(fun == 0) {
//								//债券市值=持仓数量*市价
//								markValue = amount * markPrice;
//							}
							
							ps.setDouble(7, YssFun.roundIt(markValue,4)); // 市值
							ps.setDouble(8, YssFun.roundIt(appreciation,4)); // 本位币估值增值
							ps.setDouble(9, costToRatio);
							ps.setDouble(10, valToRatio);
							ps.setInt(11, rs1.getInt("FAcctDetail"));

							acctCode = rs1.getString("FAcctCode");//保存上一条记录的科目代码 by yanghaiming 20101023
						}
						dbl.closeResultSetFinal(rs1);
						ps.executeBatch();
						dbl.closeStatementFinal(ps);
						
						if(recordContr) {
							//往电子对账对账报文处理信息表中插入记录
							strSql = "insert into Tdzbbinfo(FSN, FDATE, FZZR, FSHR, FSH, FISSEND, FSDR, FFILETYPE, FRPTTYPE, FFundCode) values('" +
										fsnNum + "'," +
										dbl.sqlDate(YssFun.toDate(strFsnDate)) + ",'" +
										pub.getUserCode() + "', '" +
										pub.getUserCode() + "', 1, 0, ' ', '" +
										filterType + "', '" +
										rpType + "', " +
										dbl.sqlString(strAssetCode) + ")"; // edit by qiuxufeng 20110215 581 报文信息表增加基金代码FFundCode
							dbl.executeSql(strSql);
							runStatus.appendRunDesc("RecGenRun", "生成" + portCodeSel + " " + strFsnDate +  "的估值对账数据...\r\n");
							strReq = "true";
						} else {
							runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + strFsnDate +  "的估值对账数据...\r\n");
							recTrans = true;
							strReq = "noSJ\r\t" + "没有" + portCodeSel + " " + strFsnDate +  "的估值对账数据";
							return;
						}
					} else {
						runStatus.appendRunDesc("RecGenRun", "【估值对账】\r\n" + portCodeSel + " " + strFsnDate + " 为待发送状态，不能重新生成！\r\n");
						recTrans = true;
						strReq = "2\r\t【估值对账】 " + portCodeSel + " " + strFsnDate + " 为待发送状态，不能重新生成！";//返回提示信息数据为待发送状态，不能重新生成
						return;
					}
				}
			} else {
				runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + strFsnDate +  "的估值对账表！\r\n");
				strReq = "noTB\r\t没有" + portCodeSel + " " + strFsnDate +  "的估值对账表！";
				recTrans = true;
				return;
			}
			dbl.closeResultSetFinal(rs);//add by yanghaiming 20101025
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun", "估值对账数据生成出错！\r\n");
			throw new YssException("估值对账数据生成出错！" + e.getMessage(), e);
		}
	}
	
	/**
	 * 余额对账数据生成
	 * @throws YssException 
	 * @方法名：balBuild
	 * @参数：fun	int
	 * @返回类型：void
	 * @说明：
	 */
	public void balBuild(String portCode, int fun) throws YssException {
		String strSql = "";
		String subTbName = "";
		String strFsnDate = "";//对账日期
		ResultSet rs = null;
		ResultSet rs1 = null;
        String set = "";
        int startMonth = 0;
        int lMonth = 0;
//		PreparedStatement ps = null;
        YssPreparedStatement ps = null;
        int kmLength = 4;
        YssFinance cw = new YssFinance();
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCode);
		//subTbName = "A" + String.valueOf(YssFun.getYear(YssFun.toDate(this.strDateTo))) + set +"LACCOUNT";//构造对应的余额表名
        //---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        String acctCode = "";//科目代码
		String strSql1 = "";
		boolean haveUpdateInfo = false;
		Statement st = null;
        //---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
		try {
			//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;
            //---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
            
			int iDate = YssFun.dateDiff(YssFun.toDate(this.strDateFrom), YssFun.toDate(this.strDateTo));
			for(int i = 0; i < iDate + 1; i++) {
				strFsnDate = YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.strDateFrom), i));
				if(!checkSendStatus(filterType, strFsnDate, portCode)) {
					CtlPubPara tempCtlPubPara = new CtlPubPara();
					tempCtlPubPara.setYssPub(pub);
//					String tempBZ = tempCtlPubPara.getRateCalculateType("RecContrlType", "SelectControl1", 4);
//					if(tempBZ.length() > 0 && null != tempBZ && tempBZ.indexOf("|") > 0) {
//						tempBZ = tempBZ.substring(0, tempBZ.indexOf("|"));
//					} else {
//						runStatus.appendRunDesc("RecGenRun", "【估值对账】\r\n未设置通用币种\r\n");
//						recTrans = true;
//						strReq = "2\r\t未设置，请先设置通用币种！";//返回提示信息数据为待发送状态，不能重新生成
//						return;
//					}
					String year = strFsnDate.substring(0, strFsnDate.indexOf("-"));//对账年份
					lMonth = Integer.parseInt(strFsnDate.substring(strFsnDate.indexOf("-")+1,strFsnDate.indexOf("-")+3));//对账月份
					subTbName = "A" + year + set;
					startMonth = getThisSetAccLen(set,Integer.parseInt(year));
//					strSql = "select * from all_all_tables where table_name = '" +
//								subTbName + "LBALANCE' and owner = '" +
//								getOwner().toUpperCase() + "'";
//					rs = dbl.openResultSet(strSql);//查询是否存在该年的余额表
					if(dbl.yssTableExist(subTbName + "LBALANCE")) {
						//add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B
						st = dbl.openStatement();
						//存在该年的对账余额表
//						strSql = "select * from " + subTbName;
//						rs = dbl.openResultSet(strSql);
						
						checkSendStatus(null, strFsnDate, null);//获取最近的FSN
						createFsn(strFsnDate);//生成FSN
						//edit by licai 20110225 BUG #841 生成余额对账表出现字段紊乱
						dbl.loadConnection().setAutoCommit(true);
	            		strSql = "Delete from tmpBalBal";
	            		dbl.executeSql(strSql);
	            		dbl.loadConnection().setAutoCommit(false);
	            		//edit by licai 20110225 BUG #841 =====================end
	            		
	            		
	            		//从余额表中导入年初数及上月数据到临时余额表中
	            		strSql = "insert into tmpBalBal select '001', a.*  from " + subTbName +
	            	            "lbalance a where fmonth=0" +
	            	            ( (lMonth > 1) ? " or fmonth=" + (lMonth - 1) : "");
	            		dbl.executeSql(strSql);
	            		
	            		strSql = "insert into tmpBalBal select '001'," + lMonth + "," +
	                    dbl.sqlIsNull("fkmh", "facctcode") + "," +
	                    dbl.sqlIsNull("fcyid", "fcurcode") + "," +
	                    dbl.sqlIsNull("fendbal", "0") + "," +
	                    dbl.sqlIsNull("fjje", "0") +
	                    "," +
	                    dbl.sqlIsNull("fdje", "0") + "," +
	                    dbl.sqlIsNull("fjje", "0") +
	                    " + " +
	                    dbl.sqlIsNull("faccdebit", "0") + "," +
	                    dbl.sqlIsNull("fdje", "0") +
	                    " + " +
	                    dbl.sqlIsNull("facccredit", "0") + "," +
	                    dbl.sqlIsNull("fendbal", "0") + " + " +
	                    dbl.sqlIsNull("fjje", "0") + " - " +
	                    dbl.sqlIsNull("fdje", "0") +
	                    "," +
	                    dbl.sqlIsNull("fbendbal", "0") + "," +
	                    dbl.sqlIsNull("fbjje", "0") +
	                    "," +
	                    dbl.sqlIsNull("fbdje", "0") + "," +
	                    dbl.sqlIsNull("fbjje", "0") +
	                    " + " +
	                    dbl.sqlIsNull("fbaccdebit", "0") + "," +
	                    dbl.sqlIsNull("fbdje", "0") + " + " +
	                    dbl.sqlIsNull("fbacccredit", "0") + "," +
	                    dbl.sqlIsNull("fbendbal", "0") + " + " +
	                    dbl.sqlIsNull("fbjje", "0") + " - " +
	                    dbl.sqlIsNull("fbdje", "0") +
	                    "," +
	                    dbl.sqlIsNull("faendbal", "0") + "," +
	                    dbl.sqlIsNull("fjsl", "0") +
	                    "," +
	                    dbl.sqlIsNull("fdsl", "0") + "," +
	                    dbl.sqlIsNull("fjsl", "0") +
	                    " + " +
	                    dbl.sqlIsNull("faaccdebit", "0") + "," +
	                    dbl.sqlIsNull("fdsl", "0") + " + " +
	                    dbl.sqlIsNull("faacccredit", "0") + "," +
	                    dbl.sqlIsNull("faendbal", "0") + " + " +
	                    dbl.sqlIsNull("fjsl", "0") + " - " +
	                    dbl.sqlIsNull("fdsl", "0") +
	                    ",1 ,case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc " +
	                    "from (select fkmh,fcyid, sum(case when fjd='J' then fbal else 0 end) as fjje," +
	                    "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl," +
	                    "sum(case when fjd='D' then fsl else 0 end) as fdsl," +
	                    "sum(case when fjd='J' then fbbal else 0 end) as fbjje," +
	                    "sum(case when fjd='D' then fbbal else 0 end) as fbdje, " +
	                    "FauxiAcc "+
	                    "from " + subTbName + "fcwvch where fterm=" + lMonth +
	                     " and fdate" + "<=" + dbl.sqlDate(strFsnDate) +
	                    " group by fkmh,fcyid,FauxiAcc) a ";
	
		              //'考虑余额表临时登帐，上月数据固定从余额表获取
	            		strSql = strSql +
		                    "full join (select c.facctcode,fmonth,c.fcurcode,faccdebit,facccredit," +
		                    "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from " +
		                    "(select * from " + subTbName + "LBalance where fmonth=" +
		                    ( (lMonth <= startMonth) ? 0 : lMonth - 1) +
		                    ") c join (select facctcode,facctdetail from " +
		                    subTbName +
		                    "laccount where facctdetail=1) d on c.facctcode=d.facctcode)";
		
	            		strSql = strSql + " b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode and a.fauxiacc=b.fauxiacc";
		                dbl.executeSql(strSql);
		                for (int j = 1; j <= 3; j++) {
		                    kmLength = kmLength + (j-1)*2;
		                    strSql = "insert into tmpBalBal select '001', " + lMonth + ",a.facctcode" +
		                            ",b.fcurcode,sum(b.fstartbal),sum(b.fdebit),sum(b.fcredit)," +
		                            "sum(b.faccdebit),sum(b.facccredit),sum(b.fendbal),sum(b.fbstartbal),sum(b.fbdebit)," +
		                            "sum(b.fbcredit),sum(b.fbaccdebit),sum(b.fbacccredit),sum(b.fbendbal),sum(b.fastartbal)," +
		                            "sum(b.fadebit),sum(b.facredit),sum(b.faaccdebit),sum(b.faacccredit),sum(b.faendbal),0 ,' ' from " +
		                            "A" + year + set + "laccount a join tmpBalBal b on a.facctcode =" + dbl.sqlLeft("b.facctcode", kmLength) + " where b.fmonth=" + lMonth +
		                          " and "+dbl.sqlLen("a.facctcode") + "=" + kmLength + " and a.facctdetail=0  " +
		                          " and FAddr='001'" + " group by a.facctcode,b.fcurcode order by a.facctcode";
		                    dbl.executeSql(strSql);
		                 }
		                
		                /**start add by huangqirong 2013-6-21 Story #3931 改进余额表生成的电子对账数据 */
		                //con.commit();
		                this.addbalace1(lMonth, subTbName, strFsnDate, startMonth, year, set);
		                		                
						/**end add by huangqirong 2013-6-21 Story #3931 改进余额表生成的电子对账数据 */
		                
						//20120329 modified by liubo.Bug #4058.  20120419 暂时取消此修改
						//====================================
						strSql = "select * from tmpbalbal where fmonth = " + lMonth;
//		                strSql = "select b.acctCodeCnt,a.* from tmpbalbal a left join " +
//		                		 " (select facctcode,count(facctcode) as acctCodeCnt from tmpbalbal  group by facctcode) b on a.facctcode = b.facctcode " +
//		                		 " order by a.facctcode";
						rs1 = dbl.openResultSet(strSql);
						//=================end===================
						
			            strSql = "insert into " + tbName + "(FFILETYPE,FFUNDCODE,FRPTTYPE,FBDATE,FEDATE," +
			            		"FACCTCODE,FCURCODE,FSTARTBAL,FDEBIT,FCREDIT,FENDBAL,FBSTARTBAL,FBDEBIT,FBCREDIT," +
			            		"FBENDBAL,FASTARTBAL,FADEBIT,FACREDIT,FAENDBAL,FISDETAIL,FSN ,F_J_TOLTAL_AMOUNT,F_D_TOLTAL_AMOUNT) values('" + //modify by huangqirong 2012-12-14 story #3334
									filterType + "','" +
									//portCode + "','01'," +
									// edit by qiuxufeng 20110215 581 生成余额对账表tdzbalance数据时，组合代码需改为资产代码
									strAssetCode + "','01'," +
									dbl.sqlDate(strFsnDate) + "," +
									dbl.sqlDate(strFsnDate) + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'" +
									fsnNum + "',?,?)";//插入余额对账表
//			            ps = dbl.openPreparedStatement(strSql);
			            ps = dbl.getYssPreparedStatement(strSql);
						boolean recordContr = false;
						String tempAccountCode = "";
						String tempCurCode = "";
						String tempCurNum = "";
						
						//20120329 added by liubo.Bug #4058.  20120419 暂时取消此修改
						//====================================
//						String tempBZ = "";
//						ResultSet rsTmpBz = null;
//						strSql = "select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCode);
//						rsTmpBz = dbl.openResultSet(strSql);
//						while(rsTmpBz.next()) {
//							if(rsTmpBz.getString("FPortCury").trim().length() > 0 && null != rsTmpBz.getString("FPortCury")) {
//								tempBZ = rsTmpBz.getString("FPortCury");
//							}
//						}
//						dbl.closeResultSetFinal(rsTmpBz);
						//================end====================
						
						while (rs1.next()){

							//20120329 added by liubo.Bug #4058.  20120419 暂时取消此修改
							//============================
//							if (rs1.getInt("acctCodeCnt") > 1)
//							{
//								if (!rs1.getString("FCurCode").equals(tempBZ))
//								{
//									continue;
//								}
//							}
							//============end================
							
							//20120326 added by liubo.Bug #4059
							//提出人认为生成的余额表中,存在数量，原币金额，本币金额均为零的记录，导致每日的余额表数据量偏多，需要设置过滤条件去除这些数据
							//====================================
							if (rs1.getDouble("FStartBal") == 0 && rs1.getDouble("FEndBal") == 0 && rs1.getDouble("FDebit") == 0 && rs1.getDouble("FCredit") == 0 &&				//原币期初、期末、借方、贷方
									rs1.getDouble("FBStartBal") == 0 && rs1.getDouble("FBEndBal") == 0 && rs1.getDouble("FBDebit") == 0 && rs1.getDouble("FBCredit") == 0 &&		//本位币期初、期末、借方、贷方
									rs1.getDouble("FAStartBal") == 0 && rs1.getDouble("FAEndBal") == 0 && rs1.getDouble("FADebit") == 0 && rs1.getDouble("FACredit") == 0)			//数量期初、期末、借方、贷方
							{
								continue;
							}
							//================end====================
							
							recordContr = true;
							tempAccountCode = rs1.getString("FAUXIACC");
							if(tempAccountCode.trim().length() > 0) {
								//20120326 added by liubo.Bug #4057
								//若辅助核算项有值，则该值由类别代码+明细组成。提出人要求去掉类别代码
								//==================================
								tempAccountCode = tempAccountCode.substring(2);
								//===============end===================
								tempAccountCode = rs1.getString("FacctCode") + "_" + tempAccountCode;
								ps.setString(1, tempAccountCode);
								acctCode = tempAccountCode;//获取最终的科目代码
							} else {
								ps.setString(1, rs1.getString("FacctCode"));
								acctCode = rs1.getString("FacctCode");//获取最终的科目代码
							}
//							ps.setString(1, rs1.getString("FacctCode"));
							
							tempCurCode = rs1.getString("FcurCode");
							tempCurNum = getCurNum(tempCurCode);
							if(tempCurNum.indexOf("noSJ") != -1) {
								return;
							}

//							if(recTrans) {
//								runStatus.appendRunDesc("RecGenRun", "【请先设置币种字典】\r\n");
//								strReq = "noSJ\r\t请先设置币种字典！";
//								return;
//							}
							
//							if(tempCurCode.trim().length() > 0 && null == tempCurCode) {
								ps.setString(2, tempCurNum);
//							}
							// edit by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B
							// 是否包含数量参数应只控制数量数据 不控制期初余额(金额)的生成
							ps.setDouble(3, rs1.getDouble(("FstartBal")));
							ps.setDouble(4, rs1.getDouble("FDebit"));
							ps.setDouble(5, rs1.getDouble("FCredit"));
							ps.setDouble(6, rs1.getDouble("FEndBal"));
							//edit by licai 20110224 BUG #841 生成余额对账表出现字段紊乱 
//							ps.setDouble(7, rs1.getDouble(("FStartBal")));
							ps.setDouble(7, rs1.getDouble("FBSTARTBAL"));
							//edit by licai 20110224 BUG #841=====================end
							ps.setDouble(8, rs1.getDouble("FBDebit"));
							ps.setDouble(9, rs1.getDouble("FBCredit"));
							ps.setDouble(10, rs1.getDouble("FBEndBal"));
							//delete by songjie 2011.07.07 BUG 2169  QDV4汇添富2011年06月27日01_B
							//是否包含数量参数应控制期初余额(数量)的生成
//							ps.setDouble(11, rs1.getDouble("FAStartBal"));
//								ps.setDouble(12, rs1.getDouble(("FADebit")));
//								ps.setDouble(13, rs1.getDouble(("FACredit")));
//								ps.setDouble(14, rs1.getDouble(("FAEndBal")));
							ps.setDouble(15, rs1.getDouble("FisDetail"));
							
							//---edit by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
							// 是否包含数量参数应只控制科目为实收基金的数量数据 不控制期初余额(金额)的生成
							if(acctCode.startsWith("4001")){
								if (fun == 1) {
									ps.setDouble(11, 0);
									ps.setDouble(12, 0);
									ps.setDouble(13, 0);
									ps.setDouble(14, 0);
								} else if (fun == 0) {
									ps.setDouble(11, rs1.getDouble("FAStartBal"));
									ps.setDouble(12, rs1.getDouble("FADebit"));
									ps.setDouble(13, rs1.getDouble("FACredit"));
									ps.setDouble(14, rs1.getDouble("FAEndBal"));
								}
							}else{
								ps.setDouble(11, rs1.getDouble("FAStartBal"));
								ps.setDouble(12, rs1.getDouble("FADebit"));
								ps.setDouble(13, rs1.getDouble("FACredit"));
								ps.setDouble(14, rs1.getDouble("FAEndBal"));
							}
							ps.setDouble(16, rs1.getDouble("FBAccdebit"));	//add by huangqirong 2012-12-14 story #3334
							ps.setDouble(17, rs1.getDouble("FBacccredit"));	//add by huangqirong 2012-12-14 story #3334
							//---edit by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
							ps.addBatch();
						}
						ps.executeBatch();
						dbl.closeResultSetFinal(rs1);
						dbl.closeStatementFinal(ps);
						
						//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
						//生成余额对账数据中的期末余额(原币)、期末余额(本位币)数据时应乘以余额方向得到最终结果
						strSql = " select a.*, acc.fbaldc from " + tbName + " a left join " + subTbName + 
						         "laccount acc on a.facctcode = acc.facctcode where FBDate = " + dbl.sqlDate(strFsnDate) +
						         " and fbaldc = -1 and FFundCode = " + dbl.sqlString(strAssetCode);
						rs1 = dbl.openResultSet(strSql);
						while(rs1.next()){
							//若科目余额方向为-1 则更新对账余额表相应期末余额(原币)、期末余额(本位币)数据
							strSql1 = " update " + tbName + " set FEndBal = -FEndBal, FBEndBal = -FBEndBal, " + 
							          " FstartBal = -FstartBal, FBStartBal = -FBStartBal, FAStartBal = -FAStartBal, FAEndBal = -FAEndBal" +
							          " where FSN = " + dbl.sqlString(rs1.getString("FSN")) + " and FFundCode = " +
							           dbl.sqlString(rs1.getString("FFundCode")) + " and FBDate = " + dbl.sqlDate(strFsnDate) +
							          " and FAcctCode = " + dbl.sqlString(rs1.getString("FAcctCode")) + " and FCurCode = " +
							          dbl.sqlString(rs1.getString("FCurCode"));
							st.addBatch(strSql1);
							haveUpdateInfo = true;
						}
						
						if(haveUpdateInfo){
							st.executeBatch();
						}
						
						dbl.closeResultSetFinal(rs1);
						dbl.closeStatementFinal(st);
						//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
						
						if(recordContr) {
							//往电子对账对账报文处理信息表中插入记录
							strSql = "insert into Tdzbbinfo(FSN, FDATE, FZZR, FSHR, FSH, FISSEND, FSDR, FFILETYPE, FRPTTYPE, FFundCode) values('" +
										fsnNum + "'," +
										dbl.sqlDate(YssFun.toDate(strFsnDate)) + ",'" +
										pub.getUserCode() + "', '" +
										pub.getUserCode() + "', 1, 0, ' ', '" +
										filterType + "', '" +
										rpType + "', " +
										dbl.sqlString(strAssetCode) + ")";// edit by qiuxufeng 20110215 581 报文信息表增加基金代码FFundCode
							dbl.executeSql(strSql);
							runStatus.appendRunDesc("RecGenRun", "生成" + portCode + " " + strFsnDate + "的余额对账数据...\r\n");
							strReq = "true";
						} else {
							runStatus.appendRunDesc("RecGenRun", "没有" + portCode + " " + strFsnDate +  "的余额对账数据...\r\n");
							recTrans = true;
							strReq = "noSJ\r\t" + "没有" + portCode + " " + strFsnDate +  "的余额对账数据";
							return;
						}
					} else {
						runStatus.appendRunDesc("RecGenRun", "没有" + portCode + " " + strFsnDate +  "的余额对账表！\r\n");
						strReq = "noTB\r\t没有" + portCode + " " + strFsnDate +  "的余额对账表！";
						recTrans = true;
						return;
					}
					dbl.closeResultSetFinal(rs);
				} else {
					runStatus.appendRunDesc("RecGenRun", "【余额对账】\r\n" + portCode + " " + strFsnDate + " 信息数据为待发送状态，不能重新生成！\r\n");
					recTrans = true;
					strReq = "3\r\t【余额对账】 " + portCode + " " + strFsnDate + " 信息数据为待发送状态，不能重新生成！";//返回提示信息数据为待发送状态，不能重新生成
					return;
				}
				//add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B
				haveUpdateInfo = false;
			}
			
			//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
            //---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun","余额对账数据生成出错！\r\n");
			throw new YssException("余额对账数据生成出错！" + e.getMessage(), e);
		} 
		//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
		finally{
			dbl.closeResultSetFinal(rs1);
			dbl.closeStatementFinal(st,ps);
			dbl.endTransFinal(con, bTrans); 
		}
		//---add by songjie 2011.07.07 BUG 2169 QDV4汇添富2011年06月27日01_B---//
	}
	
	public String getCurNum(String curCode) throws SQLException, YssException {
		String reCurNum = " ";
		String strSql = "";
		String assertCode = pub.getAssetGroupCode();
		ResultSet rs = null;
		boolean bCurCtl = false;
		
		String tbName = "Tb_" + assertCode + "_Dao_Dict";
		try {
			strSql = "select a.* from " + tbName + " a" +
						" where fdictcode = 'RecCurCode' " +
						" and FCHECKSTATE = 1 " +
						" and FSRCCONENT = " + dbl.sqlString(curCode.trim());
			rs = dbl.openResultSet(strSql);
			while(rs.next()) {
				bCurCtl = true;
				reCurNum = rs.getString("FCNVCONENT");
			}
			if(!bCurCtl) {
				//runStatus.appendRunDesc("RecGenRun", "【请先设置币种字典】\r\n");
				recTrans = true;
				//strReq = "noSJ\r\t请先设置币种字典！";
				// edit by qiuxufeng 20110316 提示明细的币种设置
				strReq = "noSJ\r\t请在字典【RecCurCode】中设置【" + curCode + "】的币种字典！";
				return strReq;
			}
		} catch (Exception e) {
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun","币种匹配出错！\r\n");
			throw new YssException("币种匹配出错！" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return reCurNum;
		
//		if(curCode.trim().equalsIgnoreCase("CNY")) {
//			reCurNum = "001";
//		} else if(curCode.trim().equalsIgnoreCase("USD")) {
//			reCurNum = "002";
//		} else if(curCode.trim().equalsIgnoreCase("HKD")) {
//			reCurNum = "003";
//		}
		
//		String[] strCurCod = {"CNY", "USD", "HKD"};
//		String[] strCurNum = {"001", "002", "003"};
//		if(curCode.trim().length() > 0 && null != curCode) {
//			for (int i = 0; i < strCurCod.length; i++) {
//				if(curCode.equalsIgnoreCase(strCurCod[i])) {
//					reCurNum = strCurNum[i];
//				}
//			}
//		}
	}
	
	private int getThisSetAccLen(String FSetCode,int FYear) throws YssException {
        //String tmp = "";
        ResultSet Rs = null;
        String sql = "";
        int YssStartMonth = 0;
        try{
           sql = "select facclen,fstartmonth From lsetlist where fsetcode=" + FSetCode + " and fyear=" + FYear;
           Rs = dbl.openResultSet(sql);
           if (Rs.next()){
              //tmp = Rs.getString("facclen");
              YssStartMonth = Rs.getInt("fstartmonth");
           }
           Rs.getStatement().close();
           Rs = null;
        }catch (SQLException ee){
        }finally{
           try{
              if (Rs != null) Rs.getStatement().close();
           }
           catch (SQLException ex){}
        }
        return YssStartMonth;
     }
	
	public String buildRowStr() throws YssException {
		return "";
	}

	public String getOperValue(String sType) throws YssException {
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		try {
			if(sRowStr.trim().length() == 0) {
				return;
			}
			reqAry = sRowStr.split("\r\f")[0].split("\t");
			this.strRecSelect = reqAry[0];	//用逗号隔开
			this.strPortCode = reqAry[1];	//用逗号隔开
			this.strDateFrom = reqAry[2];
			this.strDateTo = reqAry[3];
			if(this.strRecSelect.length() > 0) {
				String[] tempAry = strRecSelect.split(",");
				hmSelect = new HashMap();
				for (int i = 0; i < tempAry.length; i++) {
					String[] tempRecAry = tempAry[i].split("-");
					hmSelect.put(tempRecAry[0], tempRecAry[1]);
				}
			}
			if(this.strPortCode.length() > 0) {
				this.portCodeAry = strPortCode.split(",");
			}
		} catch (Exception e) {
			throw new YssException("请求生成对账信息解析出错\r\n" + e.getMessage(), e);
		}

	}
	
	private String getAssetCode(String portCode) throws YssException {
		String strSql = "";
		String strAssetCode = "";
		ResultSet rs = null;
		
		try {
			strSql = "select FPortCode, FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
					" where FCheckState = 1" +
					" and FPortCode = " + dbl.sqlString(portCode);
			rs = dbl.openResultSet(strSql);
			if(rs.next()) {
				strAssetCode = rs.getString("FAssetCode");
			} else {
				throw new YssException("获取资产代码出错！");
			}
		} catch (Exception e) {
			throw new YssException("获取资产代码出错！\r\n" + e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return strAssetCode;
	}
	
	/**
	 * 615 QDV4华安2011年2月23日01_AB by qiuxufeng 20110302
	 * 将科目类别转换成数字标识
	 * @方法名：convertAcctClass
	 * @参数：sAcctClass String
	 * @返回类型：String
	 */
	private String convertAcctClass(String sAcctClass) {
		if(sAcctClass.trim().equalsIgnoreCase("资产类")) {
			return "1";
		} else if(sAcctClass.trim().equalsIgnoreCase("负债类")) {
			return "2";
		} else if(sAcctClass.trim().equalsIgnoreCase("共同类")) {
			return "3";
		} else if(sAcctClass.trim().equalsIgnoreCase("权益类")) {
			return "4";
		} else if(sAcctClass.trim().equalsIgnoreCase("损益类")) {
			return "5";
		} else {
			return null;
		}
	}
	
	/**
	 * add by zhangjun 2012.06.01 
	 * STORY #2420 关于QD系统支持赢时胜直联电子对账系统V2.5的需求
	 * 对帐科目表
	 * @param portCodeSel
	 * @param fun
	 * @throws YssException
	 */
	public void subBuildDz(String portCodeSel, int fun) throws YssException {
		String strSql = "";
		String subTbName = "";
		ResultSet rs = null;
		YssFinance cw = new YssFinance();
		
		Connection dzConn = null; 

        String set = "";
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCodeSel);
		subTbName = "A" + String.valueOf(YssFun.getYear(YssFun.toDate(this.strDateTo))) + set +"LACCOUNT";//构造对应的科目表名

		try {
			
			if(dbl.yssTableExist(subTbName)) {
				//对账所有科目，部分明细科目
				dbl.closeResultSetFinal(rs);
				if(fun == 1) {
					strSql = "select DISTINCT * from " + subTbName;//包括明细科目
				} else if(fun == 0) {
					strSql = "select DISTINCT * from " + subTbName + " where FACCTDETAIL <> 1";//不包括明细科目
				}
				rs = dbl.openResultSet(strSql);		
				
				dzConn = dbl.loadDZConnection(); //链接电子对账目标数据库
				strSql = "delete from " + tbName +" where FFILETYPE = '"+filterType+"' and FFUNDCODE = '"+strAssetCode+"'" ;
						 //" and FBDATE = "+dbl.sqlDate(YssFun.toDate(this.strDateTo))+ " and FEDATE = " + dbl.sqlDate(YssFun.toDate(this.strDateTo));
				dzConn.createStatement().executeUpdate(strSql);			
				
				
				//对账目标数据库的“科目对账表（TDzAccount）、估值对账表（TDzJJGZB）、余额对账表（TDzbalance）”没有Fsn（报文序号）字段
				strSql = "insert into " + tbName + "(FFILETYPE, FFUNDCODE, FRPTTYPE, FBDATE, FEDATE, FACCTCODE," +
						" FACCTNAME, FACCTLEVEL, FACCTPARENT, FACCTDETAIL, FACCTCLASS, FBALDC) values('" +
						filterType + "','" +							
						strAssetCode + "',' '," +
						dbl.sqlDate(YssFun.toDate(this.strDateTo)) + "," +
						dbl.sqlDate(YssFun.toDate(this.strDateTo)) + ",?,?,?,?,?,?,?)";
				
				
				PreparedStatement psDz = dzConn.prepareStatement(strSql);//批量执行插入
				//boolean recordContr = false;
				String strAcctClass = null;
				dzConn.setAutoCommit(false);//add by huangqirong 2012-08-22 bug #5276
				while(rs.next()) {
					
					psDz.setString(1, rs.getString("FAcctCode"));
					psDz.setString(2, rs.getString("FAcctName"));
					psDz.setInt(3, rs.getInt(("FAcctLevel")));
					psDz.setString(4, rs.getString("FAcctParent"));
					psDz.setInt(5, rs.getInt("FAcctDetail"));
					
					strAcctClass = convertAcctClass(rs.getString("FAcctClass"));
					if(null == strAcctClass) {
						recTrans = true;
						throw new YssException("【" + rs.getString("FAcctClass") + "】没有对应数字代码！");
					}
					psDz.setInt(6, Integer.parseInt(strAcctClass));						
					psDz.setInt(7, rs.getInt("FBalDC"));						
					psDz.addBatch();					
				}
				runStatus.appendRunDesc("RecGenRun", "正在生成" + portCodeSel + "的科目对账数据...\r\n");
				
				psDz.executeBatch();
				dzConn.commit(); //add by huangqirong 2012-08-22 bug #5276
				dzConn.setAutoCommit(true);//add by huangqirong 2012-08-22 bug #5276				
				dbl.closeStatementFinal(psDz);
				dbl.closeResultSetFinal(rs);
				dzConn.close(); //add by huangqirong 2012-08-22 bug #5276
				
				/*  对账报文处理信息表不再使用
				if(recordContr) {
					//往电子对账对账报文处理信息表中插入记录
					strSql = "insert into Tdzbbinfo(FSN, FDATE, FZZR, FSHR, FSH, FISSEND, FSDR, FFILETYPE, FRPTTYPE, FFundCode) values('" +
								fsnNum + "'," +
								dbl.sqlDate(YssFun.toDate(this.strDateTo)) + ",'" +
								pub.getUserCode() + "', '" +
								pub.getUserCode() + "', 1, 0, ' ', '" +
								filterType + "', '" +
								rpType + "', " +
								dbl.sqlString(strAssetCode) + ")"; // edit by qiuxufeng 20110215 581 报文信息表增加基金代码FFundCode
					dbl.executeSql(strSql);
					runStatus.appendRunDesc("RecGenRun", "生成" + portCodeSel + " " + this.strDateTo + "的科目对账数据...\r\n");
					strReq = "true";
				} else {
					runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + this.strDateTo +  "的科目对账数据...\r\n");
					recTrans = true;
					strReq = "noSJ\r\t" + "没有" + portCodeSel + "的科目对账数据";
					return;
				}
				*/
			} else {
				runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + this.strDateTo +  "的科目对账表！\r\n");
				strReq = "noTB\r\t没有" + portCodeSel +  "的科目对账表！";
				recTrans = true;
				return;
			}
			
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun", "科目对账数据生成出错！\r\n");
			throw new YssException("科目对账数据生成出错！" + e.getMessage(), e);
		}
	}
	
	
	/**
	 * add by zhangjun 2012.06.01
	 * STORY #2420 关于QD系统支持赢时胜直联电子对账系统V2.5的需求
	 * @param portCodeSel
	 * @param fun
	 * @throws YssException
	 * 注意：对账数据向当前数据库插入没成功， 目标数据库依然要插入数据。
	 */
	public void valBuildDz(String portCodeSel, int fun) throws YssException {
		String strSql = "";
		String subTbName = "";
		ResultSet rs = null;
		String strFsnDate = "";
		double markValue = 0;
		double amount = 0;
		double cost = 0;
		double markPrice = 0;
		HashMap rateHash = new HashMap(32);//存放各币种对应对账货币的汇率数据 
		double baseRate = 0.0;//保存对账货币的基础汇率
		double rateResult = 0.0;//保存估值表中货币的基础汇率
		double insertPrice = 0.0;//计算对账货币金额用
		String acctCode = "";//保存上一条数据的科目代码
		//double val = 0;//净值
		double appreciation = 0;//估值增值
		double valToRatio = 0;//占净值比
		double costToRatio = 0;//成本比
		String strSql1 = "";
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		YssFinance cw = new YssFinance();
		String set = "";
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCodeSel);
		subTbName = "TB_" + pub.getPrefixTB() + "_REP_GUESSVALUE";
		String tempBZ = null; // 本位币币种代码
		String sTbLacc = "a" + this.strDateFrom.substring(0, 4) + set + "laccount";
		
		Connection dzConn = null; 
		PreparedStatement psDz = null;
		
		try {

			if(dbl.yssTableExist(subTbName)) {
				// 获取该组合的本位币币种代码
				strSql = "select FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where FCheckState = 1 and FPortCode = " + dbl.sqlString(portCodeSel);
				rs = dbl.openResultSet(strSql);
				while(rs.next()) {
					if(rs.getString("FPortCury").trim().length() > 0 && null != rs.getString("FPortCury")) {
						tempBZ = rs.getString("FPortCury");
					}
				}
				dbl.closeResultSetFinal(rs);
				if(null == tempBZ || tempBZ.trim().length() == 0) {
					recTrans = true;
					throw new YssException("未设置组合货币！"); 
				}
				//删除
				dzConn = dbl.loadDZConnection(); //链接电子对账目标数据库
				strSql = "delete from " + tbName +" where FFILETYPE = '"+filterType+"' and FFUNDCODE = '"+strAssetCode+"'" +
						 " and FBDATE >= "+dbl.sqlDate(YssFun.toDate(this.strDateFrom))+ " and FEDATE <= " + dbl.sqlDate(YssFun.toDate(this.strDateTo));
				dzConn.createStatement().executeUpdate(strSql);				
				
				int iDate = YssFun.dateDiff(YssFun.toDate(this.strDateFrom), YssFun.toDate(this.strDateTo));
				for(int i = 0; i < iDate + 1; i++) {
					acctCode = "";
					strFsnDate = YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.strDateFrom), i));//当前日期处理
						
					BaseOperDeal operDeal = new BaseOperDeal();
		            operDeal.setYssPub(pub);
		            baseRate = operDeal.getCuryRate(YssFun.toDate(strFsnDate), tempBZ,portCodeSel, "base");
					strSql = "select a.fcurcode from " + subTbName + " a " +
					" left join (" +
					"select b.*, c.FSETCODE from " + pub.yssGetTableName("Tb_para_portfolio") + " b" +
					" left join lsetlist c on b.FASSETCODE = c.FSETID) d " +
					" on a.fportcode = d.fsetcode " +
					" where FDATE = " +
					dbl.sqlDate(YssFun.toDate(strFsnDate)) +
					" and d.fportcode = " + dbl.sqlString(portCodeSel) +
					" group by a.fcurcode";
					rs1 = dbl.openResultSet(strSql);
					while (rs1.next()){
						rateResult = operDeal.getCuryRate(YssFun.toDate(strFsnDate), rs1.getString("fcurcode"),
			            		portCodeSel, "base");
						rateHash.put(rs1.getString("fcurcode"), String.valueOf(rateResult/baseRate)); // 保存所有币种换算成本位币的汇率
					}
					dbl.closeResultSetFinal(rs1);
					strSql = "select a.FAcctCode,a.FAcctName," +
							" (case when b.fcurcode = '***' then 0 when a.facctcode = '8700' then a.fcost else a.fmarketvalue end)" +
							" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fmarketvalue," +
							//" a.FCURCODE," + 
							" case when b.fcurcode = '***' then ' ' when b.fcurcode is null then a.fcurcode else b.fcurcode end as fcurcode," +
							" a.FAcctClass," + 
							" (case when b.fcurcode = '***' then 0 else a.famount end)" +
							" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as famount," +
							//" a.FCost," +
							" (case when b.fcurcode = '***' then 0 else a.fcost end)" +
							" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fcost," +
							" a.FMarketPrice,a.FAppreciation," + 
							
							" a.fstandardmoneycost" +
							" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneycost," +
							
							" (case when a.facctcode = '8700' then a.fcost else a.fstandardmoneymarketvalue end)" +
							" * (case when a.facctcode = '8801' then -1 when b.fbaldc is null then 1 else b.fbaldc end) as fstandardmoneymarketvalue," +
							
							" a.FStandardMoneyAppreciation," +
							" a.FCostToNetRatio,a.FMarketValueToRatio,a.FAcctDetail from " + subTbName + " a " +
							" left join " + sTbLacc + " b on a.facctcode = b.facctcode" + // add by qiuxufeng 20110319
							" left join (select bb.fportcode,c.FSETCODE from " + pub.yssGetTableName("Tb_para_portfolio") + " bb" +
							" left join (select FSETCODE,FSETID from lsetlist group by FSETCODE,FSETID) c on bb.FASSETCODE = c.FSETID) d " +
							" on a.fportcode = d.fsetcode " +
							" where FDATE = " +
							dbl.sqlDate(YssFun.toDate(strFsnDate)) +							
							" and ((a.facctclass = '合计' and length(Trim(a.fcurcode)) is null) or" +
							" (a.facctclass = '合计' and a.facctcode = '8600' and length(Trim(a.fcurcode)) is null) or" +
							" (a.facctclass <> '合计'))" +
							" and a.FAcctCode <> 'C100'" +
							" and a.FAcctCode not in ('8810', '8811', '8812')" +							
							" and d.fportcode = " + dbl.sqlString(portCodeSel) + " order by FACCTCODE";
					rs1 = dbl.openResultSet(strSql);
					
					strSql = "insert into " + tbName + " values('" +
								filterType + "','" +
								strAssetCode + "','01'," +
								dbl.sqlDate(strFsnDate) + "," +
								dbl.sqlDate(strFsnDate) + ",?,?,?,?,?,?,?,?,?,?,?)"; 
					
					
					psDz = dzConn.prepareStatement(strSql);//批量执行插入
					
					boolean recordContr = false;
					while(rs1.next()) {
						recordContr = true;
						if(rs1.getString("FAcctClass").indexOf("class")>-1){//财务估值表的组合分级不生成
							continue;
						}
						if(!acctCode.equalsIgnoreCase(rs1.getString("FAcctCode")) && acctCode != ""){
							psDz.addBatch();
						}
						
						if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){ // 币种为本位币，直接取值
							
							markPrice = rs1.getDouble("FMarketPrice");
						}else{ // 币种不为本位币，通过汇率换算成通用币种的金额
							markPrice = rs1.getDouble(("FMarketPrice"))* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString());
						}
						if ("8800".equals(rs1.getString("FAcctCode")))
						{
							psDz.setString(1, "0013");
						}
						else if ("8801".equals(rs1.getString("FAcctCode")))
						{
							psDz.setString(1, "0014");
						}
						else if ("9000".equals(rs1.getString("FAcctCode")))
						{
							psDz.setString(1, "0015");
						}
						else if ("9600".equals(rs1.getString("FAcctCode")))
						{
							psDz.setString(1, "0016");
						}
						else
						{
							psDz.setString(1, rs1.getString("FAcctCode"));
						}
						String xmlKmmc ="";// "<!CDATA[" + rs1.getString("FAcctName") + "]]>";
						//edit by zhouwei 20120323 发送报文时数据加密!CDATA引发问题，报文系统无法解析
						String facctName=rs1.getString("FAcctName");
						xmlKmmc=facctName;
						if(facctName.indexOf("&")>-1){
							xmlKmmc=facctName.replace("&", " ");
							
						}							
						
						psDz.setString(2, xmlKmmc);
						if(acctCode.equalsIgnoreCase(rs1.getString("FAcctCode"))){ // 与前一条的科目代码相同，值累加
							valToRatio += rs1.getDouble("FMarketValueToRatio");
							costToRatio += rs1.getDouble("FCostToNetRatio");
							amount += rs1.getDouble("FAmount");
							if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){
								insertPrice += rs1.getDouble("FMarketPrice");
								cost += rs1.getDouble("FStandardMoneyCost"); // 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost(本位币成本)的值
								if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){ // 如果为债券，按照对账设置取值
									if(fun == 1) {
										//债券市值=持仓数量*债券面额
										//--------------市值计算,表中无债权面额，暂直接取成本------------
										//markValue = amount * cost/amount;
										strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
														+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
										rs2 = dbl.openResultSet(strSql1);
										while(rs2.next()){
											markValue += YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE"),4);
										}
										dbl.closeResultSetFinal(rs2);
									} else if(fun == 0) {
										//债券市值=持仓数量*市价
										markValue += YssFun.roundIt(amount * markPrice,4);
									}
									appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
								}else{ // 非债券直接取值
									markValue += rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
									appreciation += rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
								}
							}else{
								insertPrice += YssFun.roundIt(rs1.getDouble("FMarketPrice") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
								
								// 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost的值
								cost += rs1.getDouble("FStandardMoneyCost");								
								if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
									if(fun == 1) {
										//债券市值=持仓数量*债券面额
										//--------------市值计算,表中无债权面额，暂直接取成本------------
										//markValue = amount * cost/amount;
										strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
												+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
										rs2 = dbl.openResultSet(strSql1);
										while(rs2.next()){
											markValue += YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
										}
										dbl.closeResultSetFinal(rs2);
									} else if(fun == 0) {
										//债券市值=持仓数量*市价
										markValue += YssFun.roundIt(amount * markPrice,4);
									}
									appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
									
								}else{									
									markValue += rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
									appreciation += rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
								}
							}
								
						}else{
							valToRatio = rs1.getDouble("FMarketValueToRatio");
							costToRatio = rs1.getDouble("FCostToNetRatio");
							amount = rs1.getDouble("FAmount");
							if(rs1.getString("FCURCODE").equalsIgnoreCase(tempBZ)){
								insertPrice = rs1.getDouble("FMarketPrice");
								// 成本直接取Tb_XXX_Rep_GuessValue表中FStandardMoneyCost的值
								cost = rs1.getDouble("FStandardMoneyCost");
								
								if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
									if(fun == 1) {
										//债券市值=持仓数量*债券面额
										//--------------市值计算,表中无债权面额，暂直接取成本------------
										//markValue = amount * cost/amount;
										strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
												+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_') + 1));
										rs2 = dbl.openResultSet(strSql1);
										while(rs2.next()){
											markValue = YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE"),4);
										}
										dbl.closeResultSetFinal(rs2);
									} else if(fun == 0) {
										//债券市值=持仓数量*市价
										markValue = amount * markPrice;
									}
									appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本
									
								}else{
									markValue = rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
									appreciation = rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
								}
							}else{
								insertPrice = YssFun.roundIt(rs1.getDouble("FMarketPrice") * YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
								
								cost = rs1.getDouble("FStandardMoneyCost");
								if(rs1.getString("FAcctCode").substring(0,4).equalsIgnoreCase("1103")){
									if(fun == 1) {										
										strSql1 = "select FFACEVALUE from " + pub.yssGetTableName("Tb_Para_FixInterest") + " where FCHECKSTATE = 1 and FSECURITYCODE = "
												+ dbl.sqlString(rs1.getString("FAcctCode").substring(rs1.getString("FAcctCode").indexOf('_')+1));
										rs2 = dbl.openResultSet(strSql1);
										while(rs2.next()){
											markValue = YssFun.roundIt(amount * rs2.getDouble("FFACEVALUE")* YssFun.toDouble(rateHash.get(rs1.getString("FCURCODE")).toString()),4);
										}
										dbl.closeResultSetFinal(rs2);
									} else if(fun == 0) {
										//债券市值=持仓数量*市价
										markValue = amount * markPrice;
									}
									appreciation = markValue - cost; // 债券本位币估值增值 = 本位币市值 - 本位币成本									
								}else{
									markValue = rs1.getDouble("FStandardMoneyMarketValue"); // 市值直接取本位币市值
									appreciation = rs1.getDouble("FStandardMoneyAppreciation"); // 估增直接取本位币估增
								}
							}
						}
						psDz.setDouble(3, YssFun.roundIt(insertPrice,4));
						psDz.setString(4, "1");//0:平均价；1:收市价。可空
						psDz.setDouble(5, YssFun.roundIt(amount,4));						
						psDz.setDouble(6, YssFun.roundIt(cost,4));
						psDz.setDouble(7, YssFun.roundIt(markValue,4)); // 市值
						psDz.setDouble(8, YssFun.roundIt(appreciation,4)); // 本位币估值增值
						psDz.setDouble(9, costToRatio);
						psDz.setDouble(10, valToRatio);
						psDz.setInt(11, rs1.getInt("FAcctDetail"));
						acctCode = rs1.getString("FAcctCode");//保存上一条记录的科目代码 
					}
					dbl.closeResultSetFinal(rs1);
					psDz.executeBatch();
					dbl.closeStatementFinal(psDz);
				}
			} else {
				runStatus.appendRunDesc("RecGenRun", "没有" + portCodeSel + " " + strFsnDate +  "的估值对账表！\r\n");
				strReq = "noTB\r\t没有" + portCodeSel + " " + strFsnDate +  "的估值对账表！";
				recTrans = true;
				return;
			}			
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun", "估值对账数据生成出错！\r\n");
			throw new YssException("估值对账数据生成出错！" + e.getMessage(), e);
		}
	}
	
	/**
	 * add by zhangjun 2012.06.01
	 * STORY #2420 关于QD系统支持赢时胜直联电子对账系统V2.5的需求
	 * 余额对账数据生成
	 * @throws YssException 
	 * @方法名：balBuild
	 * @参数：fun	int
	 * @返回类型：void
	 * @说明：
	 */
	public void balBuildDz(String portCode, int fun) throws YssException {

		String strSql = "";
		String subTbName = "";
		String strFsnDate = "";//对账日期
		ResultSet rs = null;
		ResultSet rs1 = null;
        String set = "";
        int startMonth = 0;
        int lMonth = 0;        
        int kmLength = 4;
        YssFinance cw = new YssFinance();
        cw.setYssPub(pub);
        set = cw.getCWSetCode(portCode);
		
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        String acctCode = "";//科目代码
		String strSql1 = "";
		boolean haveUpdateInfo = false;
		Statement st = null;
		
		Connection dzConn = null; 
		PreparedStatement psDz = null;
		
		try {			
			con.setAutoCommit(true); //设置可以自动提交
			bTrans = false;
			
          //删除
			dzConn = dbl.loadDZConnection(); //链接电子对账目标数据库
			strSql = "delete from " + tbName +" where FFILETYPE = '"+filterType+"' and FFUNDCODE = '"+strAssetCode+"'" +
					 " and FBDATE >= "+dbl.sqlDate(YssFun.toDate(this.strDateFrom))+ " and FEDATE <= " + dbl.sqlDate(YssFun.toDate(this.strDateTo));
			dzConn.createStatement().executeUpdate(strSql);		
			
			//add by huangqirong 2012-08-28 bug #5276 增加字段
			PreparedStatement pareStat = dzConn.prepareStatement("select table_name from user_tables where table_name=upper('"+ tbName +"')");
			ResultSet rsDz = pareStat.executeQuery();
			if(rsDz.next()){
				Statement state = dzConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet rsSubDz = state.executeQuery("select * from " + tbName);
				if(!dbl.isFieldExist(rsSubDz, "F_J_TOLTAL_AMOUNT")){
					state.execute("alter table " + tbName + " add F_J_TOLTAL_AMOUNT  NUMBER(19,4) default 0 not null");
				}
				rsSubDz.close();
				rsSubDz = null;
				rsSubDz = state.executeQuery("select * from " + tbName);
				if(!dbl.isFieldExist(rsSubDz, "F_D_TOLTAL_AMOUNT")){
					state.execute("alter table " + tbName + " add F_D_TOLTAL_AMOUNT NUMBER(19,4) default 0 not null");
				}
				rsSubDz.close();
				state.close();
			}
			rsDz.close();
			pareStat.close();
			//---end---
			
			int iDate = YssFun.dateDiff(YssFun.toDate(this.strDateFrom), YssFun.toDate(this.strDateTo));
			for(int i = 0; i < iDate + 1; i++) {
				strFsnDate = YssFun.formatDate(YssFun.addDay(YssFun.toDate(this.strDateFrom), i));
				CtlPubPara tempCtlPubPara = new CtlPubPara();
				tempCtlPubPara.setYssPub(pub);

				String year = strFsnDate.substring(0, strFsnDate.indexOf("-"));//对账年份
				lMonth = Integer.parseInt(strFsnDate.substring(strFsnDate.indexOf("-")+1,strFsnDate.indexOf("-")+3));//对账月份
				subTbName = "A" + year + set;
				startMonth = getThisSetAccLen(set,Integer.parseInt(year));

				if(dbl.yssTableExist(subTbName + "LBALANCE")) {
						
					st = dbl.openStatement();
					dbl.loadConnection().setAutoCommit(true);
            		strSql = "Delete from tmpBalBal";
            		dbl.executeSql(strSql);
            		//dbl.loadConnection().setAutoCommit(false);
            		//从余额表中导入年初数及上月数据到临时余额表中
            		strSql = "insert into tmpBalBal select '001', a.*  from " + subTbName +
            	            "lbalance a where fmonth=0" +
            	            ( (lMonth > 1) ? " or fmonth=" + (lMonth - 1) : "");
            		dbl.executeSql(strSql);
            		
            		strSql = "insert into tmpBalBal select '001'," + lMonth + "," +
                    dbl.sqlIsNull("fkmh", "facctcode") + "," +
                    dbl.sqlIsNull("fcyid", "fcurcode") + "," +
                    dbl.sqlIsNull("fendbal", "0") + "," +
                    dbl.sqlIsNull("fjje", "0") +
                    "," +
                    dbl.sqlIsNull("fdje", "0") + "," +
                    dbl.sqlIsNull("fjje", "0") +
                    " + " +
                    dbl.sqlIsNull("faccdebit", "0") + "," +
                    dbl.sqlIsNull("fdje", "0") +
                    " + " +
                    dbl.sqlIsNull("facccredit", "0") + "," +
                    dbl.sqlIsNull("fendbal", "0") + " + " +
                    dbl.sqlIsNull("fjje", "0") + " - " +
                    dbl.sqlIsNull("fdje", "0") +
                    "," +
                    dbl.sqlIsNull("fbendbal", "0") + "," +
                    dbl.sqlIsNull("fbjje", "0") +
                    "," +
                    dbl.sqlIsNull("fbdje", "0") + "," +
                    dbl.sqlIsNull("fbjje", "0") +
                    " + " +
                    dbl.sqlIsNull("fbaccdebit", "0") + "," +
                    dbl.sqlIsNull("fbdje", "0") + " + " +
                    dbl.sqlIsNull("fbacccredit", "0") + "," +
                    dbl.sqlIsNull("fbendbal", "0") + " + " +
                    dbl.sqlIsNull("fbjje", "0") + " - " +
                    dbl.sqlIsNull("fbdje", "0") +
                    "," +
                    dbl.sqlIsNull("faendbal", "0") + "," +
                    dbl.sqlIsNull("fjsl", "0") +
                    "," +
                    dbl.sqlIsNull("fdsl", "0") + "," +
                    dbl.sqlIsNull("fjsl", "0") +
                    " + " +
                    dbl.sqlIsNull("faaccdebit", "0") + "," +
                    dbl.sqlIsNull("fdsl", "0") + " + " +
                    dbl.sqlIsNull("faacccredit", "0") + "," +
                    dbl.sqlIsNull("faendbal", "0") + " + " +
                    dbl.sqlIsNull("fjsl", "0") + " - " +
                    dbl.sqlIsNull("fdsl", "0") +
                    ",1 ,case when a.FAuxiAcc is null then b.FauxiAcc else a.FauxiAcc end as FauxiAcc " +
                    "from (select fkmh,fcyid, sum(case when fjd='J' then fbal else 0 end) as fjje," +
                    "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl," +
                    "sum(case when fjd='D' then fsl else 0 end) as fdsl," +
                    "sum(case when fjd='J' then fbbal else 0 end) as fbjje," +
                    "sum(case when fjd='D' then fbbal else 0 end) as fbdje, " +
                    "FauxiAcc "+
                    "from " + subTbName + "fcwvch where fterm=" + lMonth +
                     " and fdate" + "<=" + dbl.sqlDate(strFsnDate) +
                    " group by fkmh,fcyid,FauxiAcc) a ";

	              //'考虑余额表临时登帐，上月数据固定从余额表获取
            		strSql = strSql +
	                    "full join (select c.facctcode,fmonth,c.fcurcode,faccdebit,facccredit," +
	                    "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from " +
	                    "(select * from " + subTbName + "LBalance where fmonth=" +
	                    ( (lMonth <= startMonth) ? 0 : lMonth - 1) +
	                    ") c join (select facctcode,facctdetail from " + subTbName +
	                    "laccount where facctdetail=1) d on c.facctcode=d.facctcode)";
	
            		strSql = strSql + " b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode and a.fauxiacc=b.fauxiacc";
	                dbl.executeSql(strSql);
	                for (int j = 1; j <= 3; j++) {
	                    kmLength = kmLength + (j-1)*2;
	                    strSql = "insert into tmpBalBal select '001', " + lMonth + ",a.facctcode" +
	                            ",b.fcurcode,sum(b.fstartbal),sum(b.fdebit),sum(b.fcredit)," +
	                            "sum(b.faccdebit),sum(b.facccredit),sum(b.fendbal),sum(b.fbstartbal),sum(b.fbdebit)," +
	                            "sum(b.fbcredit),sum(b.fbaccdebit),sum(b.fbacccredit),sum(b.fbendbal),sum(b.fastartbal)," +
	                            "sum(b.fadebit),sum(b.facredit),sum(b.faaccdebit),sum(b.faacccredit),sum(b.faendbal),0 ,' ' from " +
	                            "A" + year + set + "laccount a join tmpBalBal b on a.facctcode =" + dbl.sqlLeft("b.facctcode", kmLength) + " where b.fmonth=" + lMonth +
	                          " and "+dbl.sqlLen("a.facctcode") + "=" + kmLength + " and a.facctdetail=0  " +
	                          " and FAddr='001'" + " group by a.facctcode,b.fcurcode order by a.facctcode";
	                    dbl.executeSql(strSql);
	                 }
	                
	                /**start add by huangqirong 2013-6-21 Story #3931 改进余额表生成的电子对账数据 */
	                //con.commit();
	                this.addbalace1(lMonth, subTbName, strFsnDate, startMonth, year, set);
					/**end add by huangqirong 2013-6-21 Story #3931 改进余额表生成的电子对账数据 */
	                
	                con.setAutoCommit(false); //设置手动提交事务
	                bTrans = true;   
	                
					strSql = "select * from tmpbalbal where fmonth = " + lMonth;                
					rs1 = dbl.openResultSet(strSql);					
		            strSql = "insert into " + tbName + "(FFILETYPE,FFUNDCODE,FRPTTYPE,FBDATE,FEDATE," +
		            		"FACCTCODE,FCURCODE,FSTARTBAL,FDEBIT,FCREDIT,FENDBAL,FBSTARTBAL,FBDEBIT," +
		            		"FBCREDIT,FBENDBAL,FASTARTBAL,FADEBIT,FACREDIT,FAENDBAL,FISDETAIL,F_J_TOLTAL_AMOUNT,F_D_TOLTAL_AMOUNT) values('" +
								filterType + "','" +
								strAssetCode + "','01'," +
								dbl.sqlDate(strFsnDate) + "," +
								dbl.sqlDate(strFsnDate) + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//插入余额对账表 
		            
					psDz = dzConn.prepareStatement(strSql);//批量执行插入
					
					//psDz = dbl.getYssPreparedStatement(strSql);
					String tempAccountCode = "";
					String tempCurCode = "";
					String tempCurNum = "";					
					
					while (rs1.next()){
						//提出人认为生成的余额表中,存在数量，原币金额，本币金额均为零的记录，导致每日的余额表数据量偏多，需要设置过滤条件去除这些数据
						
						if (rs1.getDouble("FStartBal") == 0 && rs1.getDouble("FEndBal") == 0 && rs1.getDouble("FDebit") == 0 && rs1.getDouble("FCredit") == 0 &&				//原币期初、期末、借方、贷方
								rs1.getDouble("FBStartBal") == 0 && rs1.getDouble("FBEndBal") == 0 && rs1.getDouble("FBDebit") == 0 && rs1.getDouble("FBCredit") == 0 &&		//本位币期初、期末、借方、贷方
								rs1.getDouble("FAStartBal") == 0 && rs1.getDouble("FAEndBal") == 0 && rs1.getDouble("FADebit") == 0 && rs1.getDouble("FACredit") == 0)			//数量期初、期末、借方、贷方
						{
							continue;
						}
						
						tempAccountCode = rs1.getString("FAUXIACC");
						if(tempAccountCode.trim().length() > 0) {								
							tempAccountCode = tempAccountCode.substring(2);								
							tempAccountCode = rs1.getString("FacctCode") + "_" + tempAccountCode;
							psDz.setString(1, tempAccountCode);
							acctCode = tempAccountCode;//获取最终的科目代码
						} else {
							psDz.setString(1, rs1.getString("FacctCode"));
							acctCode = rs1.getString("FacctCode");//获取最终的科目代码
						}
						tempCurCode = rs1.getString("FcurCode");
						tempCurNum = getCurNum(tempCurCode);
						if(tempCurNum.indexOf("noSJ") != -1) {
							return;
						}
						psDz.setString(2, tempCurNum);
						psDz.setDouble(3, rs1.getDouble(("FstartBal")));
						psDz.setDouble(4, rs1.getDouble("FDebit"));
						psDz.setDouble(5, rs1.getDouble("FCredit"));
						psDz.setDouble(6, rs1.getDouble("FEndBal"));						
						psDz.setDouble(7, rs1.getDouble("FBSTARTBAL"));						
						psDz.setDouble(8, rs1.getDouble("FBDebit"));
						psDz.setDouble(9, rs1.getDouble("FBCredit"));
						psDz.setDouble(10, rs1.getDouble("FBEndBal"));							
						psDz.setDouble(15, rs1.getDouble("FisDetail"));
						if(acctCode.startsWith("4001")){
							if (fun == 1) {
								psDz.setDouble(11, 0);
								psDz.setDouble(12, 0);
								psDz.setDouble(13, 0);
								psDz.setDouble(14, 0);
							} else if (fun == 0) {
								psDz.setDouble(11, rs1.getDouble("FAStartBal"));
								psDz.setDouble(12, rs1.getDouble("FADebit"));
								psDz.setDouble(13, rs1.getDouble("FACredit"));
								psDz.setDouble(14, rs1.getDouble("FAEndBal"));
							}
						}else{
							psDz.setDouble(11, rs1.getDouble("FAStartBal"));
							psDz.setDouble(12, rs1.getDouble("FADebit"));
							psDz.setDouble(13, rs1.getDouble("FACredit"));
							psDz.setDouble(14, rs1.getDouble("FAEndBal"));
						}
						psDz.setDouble(16, rs1.getDouble("FBAccDebit"));
						psDz.setDouble(17, rs1.getDouble("FBAccCredit"));
						
						psDz.addBatch();
					}
					psDz.executeBatch();
					 con.commit(); //提交事务
			         bTrans = false;
			         con.setAutoCommit(true); //设置可以自动提交
					dbl.closeResultSetFinal(rs1);
					dbl.closeStatementFinal(psDz);
					//生成余额对账数据中的期末余额(原币)、期末余额(本位币)数据时应乘以余额方向得到最终结果
					strSql = " select a.*, acc.fbaldc from " + tbName + " a left join " + subTbName + 
					         "laccount acc on a.facctcode = acc.facctcode where FBDate = " + dbl.sqlDate(strFsnDate) +
					         " and fbaldc = -1 and FFundCode = " + dbl.sqlString(strAssetCode);
					rs1 = dbl.openResultSet(strSql);
					while(rs1.next()){
						//若科目余额方向为-1 则更新对账余额表相应期末余额(原币)、期末余额(本位币)数据
						strSql1 = " update " + tbName + " set FEndBal = -FEndBal, FBEndBal = -FBEndBal, " + 
						          " FstartBal = -FstartBal, FBStartBal = -FBStartBal, FAStartBal = -FAStartBal, FAEndBal = -FAEndBal" +
						          " where FSN = " + dbl.sqlString(rs1.getString("FSN")) + " and FFundCode = " +
						           dbl.sqlString(rs1.getString("FFundCode")) + " and FBDate = " + dbl.sqlDate(strFsnDate) +
						          " and FAcctCode = " + dbl.sqlString(rs1.getString("FAcctCode")) + " and FCurCode = " +
						          dbl.sqlString(rs1.getString("FCurCode"));
						st.addBatch(strSql1);
						haveUpdateInfo = true;
					}
					if(haveUpdateInfo){
						st.executeBatch();
					}						
					dbl.closeResultSetFinal(rs1);
					dbl.closeStatementFinal(st);						
				} else {
					runStatus.appendRunDesc("RecGenRun", "没有" + portCode + " " + strFsnDate +  "的余额对账表！\r\n");
					strReq = "noTB\r\t没有" + portCode + " " + strFsnDate +  "的余额对账表！";
					recTrans = true;
					return;
				}
				dbl.closeResultSetFinal(rs);				
				haveUpdateInfo = false;
			}
           //con.commit(); //提交事务
           // bTrans = false;
           // con.setAutoCommit(true); //设置可以自动提交
		} catch(Exception e){
			recTrans = true;
			strReq = "error\r\t" + e.getMessage();
			runStatus.appendRunDesc("RecGenRun","余额对账数据生成出错！\r\n");
			throw new YssException("余额对账数据生成出错！" + e.getMessage(), e);
		}
		finally{
			dbl.closeResultSetFinal(rs1);
			dbl.closeStatementFinal(st);
			dbl.endTransFinal(con, bTrans); 
			dbl.endTransFinal(dzConn, bTrans); //modify huangqirong2012-08-22 bug#5276
		}
	
	}
	
	/**
	 * add by huangqirong 2013-06-21 story #3931
	 * 增加一级科目余额
	 * */
	private void addbalace1(int lMonth , String subTbName , String strFsnDate , int startMonth , String year ,
			String set){
		String strSql = "";
		/**Start 20131024 modified by liubo.Bug #81946. QDV4赢时胜（北京）2013年10月23日01_B
		 * 修改获取多币种的一级科目余额的SQL，之前的SQL是直接取的余额表中的一级科目余额，
		 * 因为可能一个数据有多条记录，导致产生违反主键约束的异常*/
		strSql = "insert into tmpBalBal select * from(select '001'," + lMonth + "," +
        dbl.sqlIsNull("fkmh", "facctcode") + " as FFactAcctCode," +
        "'***' as FCurCode," + //dbl.sqlIsNull("fcyid", "fcurcode") + "," +
        "0 as a, " + 
        "0 as b, " + 
        "0 as c, " + 
        "0 as e, " + 
        "0 as f, " + 
        "0 as g, " + 
        "sum(" + dbl.sqlIsNull("fbendbal", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fbjje", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fbdje", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fbjje", "0") + " + " +dbl.sqlIsNull("fbaccdebit", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fbdje", "0") + " + " + dbl.sqlIsNull("fbacccredit", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fbendbal", "0") + " + " + dbl.sqlIsNull("fbjje", "0") + " - " + dbl.sqlIsNull("fbdje", "0") + ")," +
        "sum(" + dbl.sqlIsNull("faendbal", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fjsl", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fdsl", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fjsl", "0") + " + " + dbl.sqlIsNull("faaccdebit", "0") + ")," +
        "sum(" + dbl.sqlIsNull("fdsl", "0") + " + " + dbl.sqlIsNull("faacccredit", "0") + ")," +
        "sum(" + dbl.sqlIsNull("faendbal", "0") + " + " + dbl.sqlIsNull("fjsl", "0") + " - " + dbl.sqlIsNull("fdsl", "0") +
        "),1 , ' ' " +
        " from (select fkmh, sum(case when fjd='J' then fbal else 0 end) as fjje," +
        "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl," +
        "sum(case when fjd='D' then fsl else 0 end) as fdsl," +
        "sum(case when fjd='J' then fbbal else 0 end) as fbjje," +
        "sum(case when fjd='D' then fbbal else 0 end) as fbdje " + 
        "from " + subTbName + "fcwvch where fterm=" + lMonth +
         " and fdate" + "<=" + dbl.sqlDate(strFsnDate) +
        " group by fkmh) a ";

      //'考虑余额表临时登帐，上月数据固定从余额表获取
		strSql = strSql +
            " full join (select c.facctcode,fmonth,faccdebit,facccredit," +
            "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from " +
            "(select * from " + subTbName + "LBalance where fmonth=" +
            ( (lMonth <= startMonth) ? 0 : lMonth - 1) +
            ") c join (select facctcode,facctdetail from " + subTbName +
            "laccount where facctlevel = 1) d on c.facctcode=d.facctcode)";

		strSql = strSql + " b on a.fkmh=b.facctcode group by fkmh,facctcode)" +
		" where FFactAcctCode not in (select FAcctCode from tmpBalBal where FCurCode = '***' and FMonth = " + lMonth + ")";
		
		/**End 20131024 modified by liubo.Bug #81946. QDV4赢时胜（北京）2013年10月23日01_B*/
		
		try {
			dbl.executeSql(strSql);
		} catch (Exception e) {
			System.out.println("执行sql出错：" + e.getMessage());
		}
        
        strSql = "insert into tmpBalBal select '001', " + lMonth + ",a.facctcode" +
                ",'***' as fcurcode,sum(0),sum(0),sum(0)," +
                "sum(0),sum(0),sum(0),sum(b.fbstartbal),sum(b.fbdebit)," +
                "sum(b.fbcredit),sum(b.fbaccdebit),sum(b.fbacccredit),sum(b.fbendbal),sum(b.fastartbal)," +
                "sum(b.fadebit),sum(b.facredit),sum(b.faaccdebit),sum(b.faacccredit),sum(b.faendbal),0 ,' ' from " +
                "A" + year + set + "laccount a join tmpBalBal b on a.facctcode = b.facctcode " + //+ dbl.sqlLeft("b.facctcode", 4) + 
                " where b.fmonth=" + lMonth +
              " and "+dbl.sqlLen("a.facctcode") + "= 4 and a.facctlevel = 1 " +
              " and FAddr='001'" + " group by a.facctcode order by a.facctcode";
        try {
        	dbl.executeSql(strSql);
		} catch (Exception e) {
			System.out.println("执行sql出错：" + e.getMessage());
		}         
	}
}
