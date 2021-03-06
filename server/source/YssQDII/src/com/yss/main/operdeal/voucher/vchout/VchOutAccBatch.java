package com.yss.main.operdeal.voucher.vchout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.log.SingleLogOper;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssDbOperSql;
import com.yss.vsub.YssFinance;


/**
*
* <p>Title: VchOutAccBatch</p>
* <p>Description: 批量模式导出到财务系统</p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: ysstech</p>
* @author jsc
* @version 2.0
*/
public class VchOutAccBatch extends BaseVchOut {

	HashMap setMap = null; 
	
	public VchOutAccBatch() {}
	
	
	public String doInsert() throws YssException {
		StringBuffer insertBuf = new StringBuffer();
		StringBuffer delBuf = new StringBuffer();
		StringBuffer queryBuffer = new StringBuffer();
		PreparedStatement pst_insert = null;
		PreparedStatement pst_del = null;
		String[] aryPorts = null;
		String reStr = "";
		String fcwvch ;
		boolean bTrans = false;
		Connection conn = null;
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
		String logInfo = "";
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			setMap = initBookSetMap(this.portCodes);
			aryPorts = this.portCodes.split(",");
			conn = dbl.loadConnection();
			
			for (int ports = 0; ports < aryPorts.length; ports++) {
                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                logInfo += "    开始导入组合【" + aryPorts[ports] + "】的凭证... ...\r\n";
                
				runStatus.appendRunDesc("VchRun", "    开始导入组合【"+ aryPorts[ports] + "】的凭证... ...");
				fcwvch = "A"+YssFun.formatDate(this.beginDate, "yyyy")+setMap.get(aryPorts[ports]).toString().split("\t")[1]+"fcwvch";
				
				insertBuf.setLength(0);
				insertBuf.append(" insert into ").append(fcwvch).append("(Fterm,FvchclsId,Fvchpdh,Fvchbh,Fvchzy,Fkmh,FCyId,FRate,Fyhdzbz,FBal,Fjd,FBBal,");
				insertBuf.append(" Fsl,FBsl,Fdj,Fdate,Fywdate,Ffjzs,Fzdr,Fcheckr,Fxgr,Fgzr,Fgzbz,Fpzly,fzqjyfs,FMemo,fnumid,fcashid,");
				insertBuf.append(" Fpz1,Fpz2,FFromSet,FToLevel,FUpLoad,FAuxiAcc,FConfirmer,FVCHNUMRELA)");
				insertBuf.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst_insert = dbl.getPreparedStatement(insertBuf.toString());
				delete(aryPorts[ports]);
				setInsertPstValue(pst_insert,aryPorts[ports]);
				pst_insert.executeBatch();
				
                //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
				logInfo += "    导入组合【" + aryPorts[ports] + "】的凭证完成！... ...\r\n";
				runStatus.appendRunDesc("VchRun", "    导入组合【" + aryPorts[ports] + "】的凭证完成！... ...\r\n");
				//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			}
			
            //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
            if(logSumCode.trim().length() > 0){
        		//edit by songjie 2012.11.20 添加非空判断
        		if(logOper != null){
        			logOper.setDayFinishIData(this, 25, "凭证导出", pub, false, " ", 
        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
        					YssFun.toDate(this.endDate), logInfo, 
        					logStartTime, logSumCode, new java.util.Date());
        		}
            }
			//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            
			reStr = "true";
            return reStr;
		}catch(Exception e){
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			try{
				if(logSumCode.trim().length() > 0){
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 25, "凭证导出", pub, true, " ", 
	        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	        					YssFun.toDate(this.endDate), 
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	        					(logInfo + "\r\n凭证导出出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
	        					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""), 
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        					logStartTime, logSumCode, new java.util.Date());
	        		}
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//修改报错信息
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
				throw new YssException("凭证导出出错！", e);
			}
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		}finally{
			dbl.closeStatementFinal(pst_insert);
		}
				
	}

	
	private void setInsertPstValue(PreparedStatement pst_insert,String sPortCode) throws YssException{
		
		ResultSet rs = null;
		StringBuffer queryBuf = new StringBuffer();
		
		int vchNum = 1;
        int enId = 1;
        String sTmp = "";
        YssFinance cw = null;
        StringBuffer vchnum = new StringBuffer();
		try{
			cw = new YssFinance();
            cw.setYssPub(pub);
			queryBuf = builderInsertSql(sPortCode);
			rs = dbl.openResultSet(queryBuf.toString()); 
			
			String fcwvch = "A"+YssFun.formatDate(this.beginDate, "yyyy")+setMap.get(sPortCode).toString().split("\t")[1]+"fcwvch";
			sTmp = dbFun.getNextInnerCode(fcwvch,
                                              "FVchPdh", "1",
                                              " where FTerm=" +
                                              YssFun.formatDate(this.beginDate, "MM"), 1);
			vchNum = YssFun.toInt(sTmp);
			//pst_insert.setString(1,fcwvch);
			while(rs.next()){
				
				 if(vchnum.length()>0 && !vchnum.toString().equalsIgnoreCase(rs.getString("fvchnum"))){
					enId =1;
				}
				 if( vchnum.length()>0 && !vchnum.toString().equalsIgnoreCase(rs.getString("fvchnum"))){
					 vchNum ++;//凭证号自加
					}
				 
				pst_insert.setInt(1,YssFun.getMonth(rs.getDate("FVchDate")));
				pst_insert.setString(2, " ");
				pst_insert.setInt(3, vchNum);
				pst_insert.setInt(4, enId);
				pst_insert.setString(5, rs.getString("FResume"));
				pst_insert.setString(6, rs.getString("FSubjectCode"));
				pst_insert.setString(7, cw.getCWAccountCury(
						rs.getString("FSubjectCode"),
						rs.getDate("FVchdate"),
						rs.getString("FPortCode")));
				pst_insert.setDouble(8, rs.getDouble("FCuryRate"));
				pst_insert.setInt(9, 0);
				pst_insert.setDouble(10, rs.getDouble("FBal"));
				pst_insert.setString(
						11,
						(rs.getString("FDCWay").equalsIgnoreCase(
								"0")
								|| rs.getString("FDCWay")
										.equalsIgnoreCase("J") ? "J" : "D"));
				pst_insert.setDouble(12, rs.getDouble("FSetBal"));
				pst_insert.setDouble(13, rs.getDouble("FAmount"));
				pst_insert.setDouble(14, rs.getDouble("FAmount"));

				pst_insert.setDouble(15, rs.getDouble("FPrice"));
				pst_insert.setDate(16, rs.getDate("FVchDate"));
				pst_insert.setDate(17, rs.getDate("FVchDate"));
				pst_insert.setInt(18, 0);
				pst_insert.setString(19, pub.getUserName());
				pst_insert.setString(20, " "); // 导入到财务里面的凭证是为“未审核”状态
				pst_insert.setString(21, " ");
				pst_insert.setString(22, " ");
				pst_insert.setInt(23, 0);
				pst_insert.setString(24,
						(rs.getString("FVchInd") == null ? " "
								: rs.getString("FVchInd")));
				pst_insert.setString(25,
						(rs.getString("FVchTWay") == null ? " "
								: rs.getString("FVchTWay")));
				pst_insert.setString(26, " ");
				pst_insert.setLong(27, 1);
				pst_insert.setString(28, " ");
				pst_insert.setString(29, " ");
				pst_insert.setString(30, " ");
				pst_insert.setInt(31, 0);
				pst_insert.setInt(32, 0);
				pst_insert.setInt(33, 0);
				pst_insert.setString(34,
						(rs.getString("FAssistant") == null ? " "
								: rs.getString("FAssistant")));
				pst_insert.setString(35, pub.getUserName());
				pst_insert.setString(36, rs.getString("fvchnum"));
				
				
				enId++; //凭证分录编号自加
				
				 
				 vchnum.setLength(0);
				 vchnum.append(rs.getString("fvchnum"));
				 
				pst_insert.addBatch();
			}
			
			
		}catch( Exception e){
			throw new YssException(e.getMessage());
		}finally{
			dbl.closeResultSetFinal(rs);
			//dbl.closeStatementFinal(pst_insert);
		}
	}
	
	
	private StringBuffer builderInsertSql (String sPortCode ) throws YssException{
		
		StringBuffer insertBuf = new StringBuffer();
		String setCode ;
		String sPreTB ;
		try{
			//setCode = setMap.get(sPortCode).toString().split("\t")[0];
			sPreTB = YssFun.formatDate(this.beginDate, "yyyy")+setMap.get(sPortCode).toString().split("\t")[1];
			
			
			insertBuf.append(" select a.FVchNum,a.FVchDate,b.FVchInd,b.FVchTWay,a.FPortCode,c.* ");
			insertBuf.append(" from ").append(pub.yssGetTableName("Tb_Vch_Data")).append(" a ");
			insertBuf.append(" left join ");
			insertBuf.append(" (select b1.FVchTWay,b1.FVchTplCode, b2.FVchInd from ").append(pub.yssGetTableName("Tb_Vch_VchTpl")).append(" b1 ");
			insertBuf.append(" left join ");
			insertBuf.append(" (select FAttrCode, FVchInd from ").append(pub.yssGetTableName("Tb_Vch_Attr")).append(" where FCheckState = 1) b2 ");
			insertBuf.append(" on b1.FAttrCode = b2.FAttrCode  where FCheckState = 1) b on a.FVchTplCode = b.FVchTplCode ");
			insertBuf.append("  left join (select * from  ").append(pub.yssGetTableName("tb_vch_dataentity")).append(" ) c on a.FVchNum = c.FVchNum ");
			insertBuf.append("  left join (select FVchTplCode, FAttrCode from ").append(pub.yssGetTableName("tb_vch_vchtpl"));
			insertBuf.append(" ) d  on a.FVchTplCode = d.FVchTplCode ");
			insertBuf.append(" where a.FCheckState = 1 and a.FPortCode =").append(dbl.sqlString(sPortCode));
			//insertBuf.append(" and a.FBookSetCode = ").append(dbl.sqlString(setCode));
			insertBuf.append(" and a.FVchDate between ").append(dbl.sqlDate(this.beginDate)).append(" and ").append(dbl.sqlDate(this.endDate));
			insertBuf.append(" and a.FVchTplCode in (").append(isInData ? operSql.sqlCodes(getVchTpl()) : operSql.sqlCodes(this.vchTypes)).append(") ");
			insertBuf.append("  and not exists (select distinct fvchnumrela from A").append(sPreTB);
			insertBuf.append("fcwvch e where fpzly = 'HD'  and e.fvchnumrela = a.FVchNum)");
			insertBuf.append(" order by a.fvchnum asc ");
			
			return insertBuf;
		}catch( Exception e){
			throw new YssException(e.getMessage());
		}
	}
	
	
	
	
	/**
	 * 获取组合对应的套帐代码
	 * @param portcode
	 * @return
	 * @throws YssException
	 */
	private HashMap initBookSetMap(String portcode) throws YssException {

		ResultSet rs = null;
		StringBuffer queryBuf = new StringBuffer();
		StringBuffer valBuf = new StringBuffer();
		HashMap setMap = new HashMap();
		
		YssDbOperSql dbOper = new YssDbOperSql(pub);
		try {
			queryBuf.append(" select a.fportcode,b.fsetcode,trim(to_char(b.fsetcode,'000')) as fsetNum from ");
			queryBuf.append(" (select fportcode,fassetcode from  ").append(pub.yssGetTableName("tb_para_portfolio"));
			queryBuf.append(" where fcheckstate=1)a ");
			queryBuf.append(" left join ");
			queryBuf.append(" (select fsetid,fyear,fsetcode from lsetlist) b on a.fassetcode = b.fsetid ");
			queryBuf.append(" where a.fportcode in(").append(dbOper.sqlCodes(portcode)).append(")");
			queryBuf.append(" and b.fyear = ").append(YssFun.formatDate(this.beginDate, "yyyy"));
			rs = dbl.openResultSet(queryBuf.toString());
			while (rs.next()) {
				valBuf.setLength(0);
				valBuf.append(rs.getString("fsetcode")).append("\t").append(rs.getString("fsetNum"));
				setMap.put(rs.getString("fportcode").trim(), valBuf.toString());
				
			} 
			
			if(setMap.size()==0){
				throw new YssException("匹配不到组合【" + portcode + "】对于的套帐代码... ...");
			}
			return setMap;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			queryBuf.setLength(0);
			valBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	/**
	 * 修改方法的参数列表,加入组合的传入.为的是防止在多组合操作时避免误删除. sj edit 20080328
	 * 
	 * @param sportCode
	 *            String
	 * @throws YssException
	 */
	public void delete(String sportCode) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		YssFinance fc = new YssFinance();
		Connection conn = dbl.loadConnection();
		java.util.Date dDate;
		int iYearNum = 0;
		String sVchTWays = "", sVchInds = "";
		try {
			iYearNum = YssFun.getYear(YssFun.toDate(endDate))
					- YssFun.getYear(YssFun.toDate(beginDate));
			fc.setYssPub(pub);
			conn.setAutoCommit(false);
			// 获取删除凭证类型，不能根据凭证数据中存在的凭证类型来删除fazmm20071111
			strSql = "select distinct a.FVchTWay,b.FVchInd from "
					+
					
					pub.yssGetTableName("Tb_Vch_VchTpl")
					+
					" a left join (select FAttrCode,FVchInd from "
					+ pub.yssGetTableName("Tb_Vch_Attr")
					+ " where FCheckState = 1) b on a.FAttrCode = b.FAttrCode"
					+ " where a.FVchTplCode in (" + 
					(this.isInData ? operSql.sqlCodes(getVchTpl()) : operSql
							.sqlCodes(vchTypes)) + ")";
			rs = dbl.queryByPreparedStatement(strSql); 
			while (rs.next()) {
				sVchTWays += rs.getString("FVchTWay") + ",";
				sVchInds += rs.getString("FVchInd") + ",";
			}
			if (sVchTWays.length() > 0) {
				sVchTWays = sVchTWays.substring(0, sVchTWays.length() - 1);
				if (sVchTWays.equalsIgnoreCase("null")) {
					sVchTWays = " ";
				}
			}
			if (sVchInds.length() > 0) {
				sVchInds = sVchInds.substring(0, sVchInds.length() - 1);
				if (sVchInds.equalsIgnoreCase("null")) {
					sVchInds = " ";
				}
			}

			dbl.closeResultSetFinal(rs); 

			// 还需判断是否在该组合群中是否有设置帐套组合链接的 zml 2007.12.17
			strSql = "select distinct a.FBookSetCode from "
					+
					// -----------在此加入组合的查询条件，只查出需要的套帐 sj edit 20080328
					// -----------------------叶生红 bug6203
					"(select trim(to_char(FBookSetCode,'000')) as FBookSetCode, FCheckState, FVchTplCode  from "
					+ pub.yssGetTableName("Tb_Vch_Data")
					+ " where FPortCode = "
					+ dbl.sqlString(sportCode)
					/**Start---panjunfang 2013-6-28 BUG 8442 */
					//分析嘉实凭证数据表数据Tb_Vch_Data，发现套账字段存在两个值，推断出套账链接在某个时点被修改过。
					//因为废除套账链接设置后，会将两个套账号都查出来，但实际上只会有一个套账表存在，导致报表不存在错误。
					//此处只需筛选出当前选择业务期间的凭证数据即可,同时还能提高查询效率。
					+ " and FVchDate between " + dbl.sqlDate(beginDate)
					+ " and " + dbl.sqlDate(endDate)
					/**End---panjunfang 2013-6-28 BUG 8442*/					
					+ ")"
					+
					// ---------------------------------------------------------------------------------------
					// 这里也需根据前台操作方法判断：直接取凭证模板代码还是通过属性代码间接算凭证模板代码　QDV4招商证券2009年04月16日02_B
					// MS00384 by leeyu 20090417
					// " a where a.FVchTplCode in (" +
					// operSql.sqlCodes(vchTplCodes) +
					" a where a.FVchTplCode in ("
					+ (this.isInData ? operSql.sqlCodes(getVchTpl()) : operSql
							.sqlCodes(vchTypes))
					+ ") and a.FCheckState = 1 and exists(select * from lsetlist  "
					//+ pub.yssGetTableName("tb_vch_bookset")  //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
					+ " where FSetCode= to_number(a.FBookSetCode))"; // 导入财务时，应该只把凭证浏览里面已审核的凭证导入财务，20070919，杨
			rs = dbl.queryByPreparedStatement(strSql); // modify by fangjiang
														// 2011.08.14 STORY #788
			while (rs.next()) {
				dDate = YssFun.toDate(beginDate);
				for (int i = 0; i <= iYearNum; i++) {
					strSql = " delete from "
							+ fc.getCWTabName(dDate,
									rs.getString("FBookSetCode"), "fcwvch")
							+ " where 	Fdate between " + dbl.sqlDate(beginDate)
							+ " and " + dbl.sqlDate(endDate)
							+ " and FPzLy <> 'HD' " + " and FPzLy in ("
							+ operSql.sqlCodes(sVchInds) + ") and FZqJyFs in ("
							+ operSql.sqlCodes(sVchTWays) + ")";
					dbl.executeSql(strSql);
					dDate = YssFun.addYear(dDate, 1);
				}
			}
			bTrans = true;
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	
	
}
