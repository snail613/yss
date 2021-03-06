package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import com.yss.util.YssFun;

//add by huangqirong 2011-07-13 story #1192 固定数据源 处理权益数据 Bean
public class EquityBean extends DataBase {
	public EquityBean() {
	}

	public void inertData() throws YssException {
		Connection conn = dbl.loadConnection(); // 新建连接
		boolean bTrans = true;// 事务控制标识
		ResultSet rs = null;// 结果集声明
		PreparedStatement pstDel = null; // 声明PreparedStatement
		PreparedStatement pstIns = null;
		StringBuffer buff = null;// 做拼接SQL语句
		String fieldName = "";// 保存上一个字段的名称

		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);

			buff = new StringBuffer("select fcontents from temp_equity");
			bTrans = true;
			rs = dbl.openResultSet(buff.toString());

			buff.setLength(0);

			while (rs.next()) {
				String contents = rs.getString("fcontents").trim();
				//modify by huangqirong 2011-08-25 bug 2535
				if (contents.startsWith("#"))
					continue;
				String[] sc = contents.split("\\|");
				if(sc.length<7)
					continue;
				if(sc[6].equals("D"))//第7个内容为D则不读取
					continue;
				//---end---
				if (sc.length > 5 && sc[5].toUpperCase().equals("DVD_CASH")) {// 分红表
					/**shashijie 2012-5-21 BUG 4581 */
					ClosePreparedStatement(pstDel,pstIns,conn);
					/**end*/
					// 删除分红目标表
					buff.append("delete from ").append(
							pub.yssGetTableName("tb_Data_Dividend")).append(
							" where exists (select * from ").append(
							pub.yssGetTableName("tb_Data_Dividend"));
					buff.append(" where FSECURITYCODE =? and FCURYCODE=? and FRECORDDATE=? and FDIVDENDTYPE=? and FDISTRIBUTEDATE=? and FASSETGROUPCODE =? and FPORTCODE=?)");
					buff.append(" and FSECURITYCODE =? and FCURYCODE=? and FRECORDDATE=? and FDIVDENDTYPE=? and FDISTRIBUTEDATE=? and FASSETGROUPCODE =? and FPORTCODE=?");//add by huangqirong 2011-08-24 BUG 2495 
					pstDel = dbl.openPreparedStatement(buff.toString());
					buff.setLength(0);

					// 插入分红表语句
					buff.append("insert into ").append(
							pub.yssGetTableName("tb_Data_Dividend"));
					buff.append("(FSecurityCode,FCuryCode,FRecordDate,FDivdendType,FDistributeDate,FPortCode,FAssetGroupcode,FDividendDate,FAfficheDate,FPreTaxRatio,FAfterTaxRatio,FRoundCode,FCheckState,FCreator,FCreateTime)");
					buff.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pstIns = dbl.openPreparedStatement(buff.toString());
					buff.setLength(0);
				} else if (sc.length > 5
						&& sc[5].toUpperCase().equals("DVD_STOCK")) {// 送股表
					/**shashijie 2012-5-21 BUG 4581 */
					ClosePreparedStatement(pstDel,pstIns,conn);
					/**end*/
					// 删除送股目标表
					buff.append("delete from ").append(
							pub.yssGetTableName("Tb_Data_BonusShare")).append(
							" where exists (select * from ").append(
							pub.yssGetTableName("Tb_Data_BonusShare"));
					buff.append(" where FTSECURITYCODE = ? and FRECORDDATE =? and FPORTCODE = ? and FASSETGROUPCODE =? and FPAYDATE=?)");
					buff.append(" and FTSECURITYCODE = ? and FRECORDDATE =? and FPORTCODE = ? and FASSETGROUPCODE =? and FPAYDATE=?");//add by huangqirong 2011-08-24 BUG 2495
					pstDel = dbl.openPreparedStatement(buff.toString());
					buff.setLength(0);

					// 插入送股表语句
					buff.append("insert into ").append(
							pub.yssGetTableName("Tb_Data_BonusShare"));
					buff.append("(FTSecurityCode,FSSecurityCode,FRecordDate,FASSETGROUPCODE,FPORTCODE,FExRightDate,FAfficheDate,FPayDate,FPreTaxRatio,FAfterTaxRatio,FCREATOR,FCREATETIME,Froundcode,Fcheckstate)");
					buff.append("values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pstIns = dbl.openPreparedStatement(buff.toString());
					buff.setLength(0);
				}

				for (int i = 0; i < sc.length; i++) {
					String fieldContent = sc[i];
					/*if (fieldContent.startsWith("#"))  modify by huangqirong 2011-08-25 bug 2535 这个判断换到上面去判断
						continue;					
					else */
						if (sc.length > 5
							&& sc[5].toUpperCase().equals("DVD_CASH")) {// 分红表
						if (i == 0) {// 证券代码
							pstDel.setString(1, getPartString(fieldContent, "Equity"));
							pstDel.setString(8, getPartString(fieldContent, "Equity"));//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setString(1, getPartString(fieldContent, "Equity"));
							continue;
						}

						if (fieldName.equals("CP_DVD_CRNCY")) {// 分红币种
							pstDel.setString(2, fieldContent);
							pstDel.setString(9, fieldContent);//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setString(2, fieldContent);
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals(
								"CP_DVD_CRNCY")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

						if (fieldName.equals("CP_RECORD_DT")) {// 登记日
							pstDel.setDate(3, YssFun
							//edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
									.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							pstDel.setDate(10, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setDate(3, YssFun
							//edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
									.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals(
								"CP_RECORD_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}
						System.out.println("fieldName:"+fieldName);
						System.out.println("fieldContent:"+fieldContent);
						if (fieldName.equals("CP_PAY_DT")) {// 到账日 派息日
						    //---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B start---//
							pstDel.setDate(5, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							pstDel.setDate(12, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setDate(5, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							//---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B end---//
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals("CP_PAY_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

						if (fieldName.equals("CP_ADJ_DT")) {// 除权日
					 	    //edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
							pstIns.setDate(8, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							pstIns.setDate(9, YssFun.toSqlDate(this.getStringByDate(new Date(this.getDate(fieldContent).getTime() - 1
									* 24 * 60 * 60 * 1000)))); // 公告日
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals("CP_ADJ_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

						if (fieldName.equals("CP_GROSS_AMT")) {// 税前权益比例
						    //edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
							pstIns.setDouble(10, Double.parseDouble(fieldContent.equals("N.A.")?"0":fieldContent));
							fieldName = "";
						} else if (fieldContent.toUpperCase().trim().equals(
								"CP_GROSS_AMT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

						if (fieldName.equals("CP_NET_AMT")) {// 税后权益比例
							//edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
							pstIns.setDouble(11, Double.parseDouble(fieldContent.equals("N.A.")?"0":fieldContent));
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals("CP_NET_AMT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

					} else if (sc.length > 5
							&& sc[5].toUpperCase().equals("DVD_STOCK")) {// 送股表
						if (i == 0) {// 证券代码
							pstDel.setString(1, getPartString(fieldContent, "Equity"));
							pstDel.setString(6, getPartString(fieldContent, "Equity"));//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setString(1, getPartString(fieldContent, "Equity"));

							pstIns.setString(2, getPartString(fieldContent, "Equity"));
							continue;
						}
						
						if (fieldName.equals("CP_RECORD_DT")) {// 登记日
							pstDel.setDate(2, YssFun
							//edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
									.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							pstDel.setDate(7, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));//add by huangqirong 2011-08-24 BUG 2495
							pstIns.setDate(3, YssFun
							//edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
									.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals(
								"CP_RECORD_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}
						
						if (fieldName.equals("CP_ADJ_DT")) {// 除权日
						    //---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B start---//
							pstIns.setDate(6, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							pstIns.setDate(7, YssFun.toSqlDate(this.getStringByDate(new Date(this.getDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent).getTime() - 1
							//---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B end---//
											* 24 * 60 * 60 * 1000)))); // 公告日
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals("CP_ADJ_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}
						
						if (fieldName.equals("CP_PAY_DT")) {// 到账日
							pstDel.setDate(5, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));//add by huangqirong 2011-08-24 BUG 2495
							pstDel.setDate(10, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));//add by huangqirong 2011-08-24 BUG 2495
						    //edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B
							pstIns.setDate(8, YssFun.toSqlDate(this.getStringDate(fieldContent.equals("N.A.")?"1900-01-01":fieldContent)));
							fieldName = "";
							continue;							
						} else if (fieldContent.toUpperCase().trim().equals("CP_PAY_DT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}						

						if (fieldName.equals("CP_AMT")) {// 税前权益比例
						    //---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B start---//
							pstIns.setDouble(9, Double.parseDouble(fieldContent.equals("N.A.")?"0":fieldContent));
							pstIns.setDouble(10, Double.parseDouble(fieldContent.equals("N.A.")?"0":fieldContent));
							//---edit by chashichun 2011.08.19 BUG 2455 QDV4长盛2011年08月16日02_B end---//
							fieldName = "";
							continue;
						} else if (fieldContent.toUpperCase().trim().equals(
								"CP_AMT")) {
							fieldName = fieldContent.toUpperCase().trim();
							continue;
						}

					} else
						continue;
				}

				if (sc.length > 5 && sc[5].toUpperCase().equals("DVD_CASH")) {// 分红表					
					pstDel.setInt(4, 1); // 分红类型						
					pstDel.setString(6, " "); // 组合群
					pstDel.setString(7, " "); // 组合
					//add by huangqirong 2011-08-24 BUG 2495 
					pstDel.setInt(11, 1); // 分红类型	
					pstDel.setString(13, " "); // 组合群
					pstDel.setString(14, " "); // 组合
					//---end---
					
					pstIns.setInt(4, 1); //modify by huangqirong 2011-08-24 BUG 2495  换个位置以防看错
					pstIns.setString(6, " "); // 组合群
					pstIns.setString(7, " "); // 组合
					pstIns.setString(12, "R001");// 舍入设置
					pstIns.setInt(13, 1); // 审核状态
					pstIns.setString(14, pub.getUserCode());// 创建人
					pstIns.setString(15, new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()));// 创建时间
					pstDel.addBatch();
					pstDel.executeBatch();
					conn.commit();

					pstIns.addBatch();
					pstIns.executeBatch();
					conn.commit();
				} else if (sc.length > 5
						&& sc[5].toUpperCase().equals("DVD_STOCK")) {// 送股表
					pstDel.setString(3, " "); // 组合群
					pstDel.setString(4, " "); // 组合
					//add by huangqirong 2011-08-24 BUG 2495 
					pstDel.setString(8, " "); // 组合群
					pstDel.setString(9, " "); // 组合
					//---end---
					
					pstIns.setString(4, " "); // 组合群
					pstIns.setString(5, " "); // 组合
					pstIns.setString(11, pub.getUserCode());// 创建人
					pstIns.setString(12, new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss").format(new Date()));// 创建时间
					pstIns.setString(13, "R001");// 舍入设置
					pstIns.setInt(14, 1);// 审核状态

					pstDel.addBatch();
					pstDel.executeBatch();
					conn.commit();
					
					pstIns.addBatch();
					pstIns.executeBatch();
					conn.commit();
				}

			}
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("权益数据预处理执行出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pstDel);
			dbl.closeStatementFinal(pstIns);
		}
	}

	/**shashijie 2012-5-21 BUG 4581 关闭游标 PreparedStatement
	* @param pstDel
	* @param pstIns
	* @param conn*/
	private void ClosePreparedStatement(PreparedStatement pstDel,
			PreparedStatement pstIns, Connection conn) throws Exception {
		if (pstDel==null || pstIns == null) {
			return;
		}
		
		try {
			pstDel.addBatch();
			pstDel.executeBatch();
			
			pstIns.addBatch();
			pstIns.executeBatch();
			
			conn.commit();
		} catch (Exception e) {
			throw new YssException("关闭游标PreparedStatement出错!", e);
		} finally {
			dbl.closeStatementFinal(pstDel);
			dbl.closeStatementFinal(pstIns);
		}
	}

	// 截取字符串 去除以某部分字符串结尾
	private String getPartString(String source, String regex) {
		if (source.endsWith(regex)) {
			source = new StringBuffer(source).reverse().toString().substring(
					regex.length()).toString().trim();
		}
		return new StringBuffer(source).reverse().toString();
	}

	// 转换字符串日期格式为标准格式yyyy-MM-dd
	private String getStringDate(String sDate) throws ParseException {		
			sDate = new SimpleDateFormat("yyyy-MM-dd").format(this.getDate(sDate));
		return sDate;
	}
	
	private String getStringByDate(Date date) throws ParseException {
		String sDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
	return sDate;
}
	
	// 转换字符串日期格式为标准格式 设置格式  modify by huangqirong 2011-08-24 BUG 2495 为以后备用日期转换
	private Date getStringDate(String sDate,String format) throws ParseException {		
		Date date=null;
		date =new SimpleDateFormat(format).parse(sDate);
		return date;
	}
	
	//转换成日期
	private Date getDate(String sDate) throws ParseException {
		Date date=null;
		if (sDate.length() == 8)
			date =new SimpleDateFormat("yyyyMMdd").parse(sDate);
		else if(sDate.length() == 10){
			if(sDate.indexOf("/")>-1)
				date =new SimpleDateFormat("MM/dd/yyyy").parse(sDate);
			else if(sDate.indexOf("-")>-1)
				date =new SimpleDateFormat("yyyy-MM-dd").parse(sDate);
			}		
		return date;
	}
	
	
}
