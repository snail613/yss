package com.yss.main.dayfinish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.DbBase;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class OffAcctBean 
				extends BaseDataSettingBean implements IDataSetting {

	private String strMonth = "";//关账年月
	private String strPortCode = "";//组合代码
	private String strPortName = "";//组合名称
	private String strStatus = "";//组合封账状态
	private ArrayList selectedAry = null;//要处理月份的组合
	
	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrPortCode() {
		return strPortCode;
	}

	public void setStrPortCode(String strPortCode) {
		this.strPortCode = strPortCode;
	}

	public String getStrPortName() {
		return strPortName;
	}

	public void setStrPortName(String strPortName) {
		this.strPortName = strPortName;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public ArrayList getSelectedAry() {
		return selectedAry;
	}

	public void setSelectedAry(ArrayList selectedAry) {
		this.selectedAry = selectedAry;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strMonth.trim()).append("\t");
		buf.append(this.strPortCode.trim()).append("\t");
		buf.append(this.strStatus.trim()).append("\tnull");
		return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String sTmpStr = "";
        String[] reqAry = null;

        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }

            reqAry = sTmpStr.split("\t");
            this.strMonth = reqAry[0];
            this.strPortCode = reqAry[1];
            this.strPortName = reqAry[2];
            this.strStatus = reqAry[3];

            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.selectedAry == null) {
                    this.selectedAry = new ArrayList();
                }
                String[] tempAry = sRowStr.split("\r\t")[1].split("\f\f");
                for (int i = 0; i < tempAry.length; i++) {
                    OffAcctBean tempOffAcctBean = new OffAcctBean();
                	tempOffAcctBean.parseRowStr(tempAry[i]);
                	selectedAry.add(tempOffAcctBean);//解析要处理的组合放到ArrayList中
				}
            }
        } catch (Exception e) {
            throw new YssException("解析封账请求数据出错", e);
        }
	}

	public String doOperation(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData1() throws YssException {
		String sHeader = "";//表头显示
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();//listview显示数据
        StringBuffer bufAll = new StringBuffer();//所有数据
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        ResultSet rs = null;
        boolean haveDateOfPort = true;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		PreparedStatement ps = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        try {
            conn.setAutoCommit(false);
			bTrans = true;
            sHeader = this.getListView1Headers();//获取表头数据
            strSql ="select a.FDate, a.FFundcode, b.fportcode, " +
            		" (case when(a.FStatus = 0) then '0' " +
            		" when(a.FStatus = 1) then '1' " +
            		" else '1' end) as FStatus, " +
            		" b.FPortname, " +
            		" (case when(FStatus = '0') then '已封账' " +
            		" when (FStatus = '1') then '未封账' " +
            		" else '未封账' end) as Status " +
            		" from " + pub.yssGetTableName("Tb_para_portfolio") + " b " +
            		" left join (select * from " + pub.yssGetTableName("TB_DATA_OFFACCT") + " where FDate = '" + this.strMonth + "') a " + 
            		" on a.ffundcode = b.fportcode where b.fcheckstate = 1 " +
            		//buildFilter() +
            		" order by a.FDate desc";
              
            rs = dbl.openResultSet(strSql);
            ArrayList tempPortAry = new ArrayList();
            String tempFdate = "";
            while(rs.next()) {
//            	haveDateOfPort = true;
            	tempFdate = rs.getString("fdate");
            	if(null == tempFdate || tempFdate.trim().length() != 6) {
            		haveDateOfPort = false;
            		tempPortAry.add(rs.getString("fportcode"));//如果是未进行封账操作的组合或新添加的组合，添加该月该组合的疯长信息
            	}
            }
            if(!haveDateOfPort) {
            	int i = 0;
            	String tempSql = "insert into " + pub.yssGetTableName("TB_DATA_OFFACCT") + " values(?, ?, ?)";
            	//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
				ps = dbl.openPreparedStatement(tempSql);
				//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
            	while(i < tempPortAry.size()) {
            		ps.setString(1, this.strMonth);
            		ps.setString(2, (String)tempPortAry.get(i));
            		ps.setInt(3, 1);
            		ps.addBatch();
            		i++;
            	}
            	ps.executeBatch();
            	ps.close();//add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B
            	conn.commit();
            }
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                		append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("获取封账信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(ps);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        }
        return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
        		this.getListView1ShowCols();
	}

	public String buildFilter() {
		String sResult = "";
        if (this.strMonth != "") {
        	sResult += "where a.FDate = " + dbl.sqlString(this.strMonth);
        }
		return sResult;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException {
		this.strMonth = rs.getString("FDate") + "";
		this.strPortCode = rs.getString("FFundcode") + "";
		this.strStatus = rs.getString("FStatus") + "";
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

	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
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

	public String getOperValue(String sType) throws YssException {
        String reStr = "";
		if(sType.equalsIgnoreCase("offacctset")) {
			Connection conn = dbl.loadConnection();
			PreparedStatement ps = null;
	        boolean bTrans = false;
	        String strSql = "";
	        try {
				conn.setAutoCommit(false);
				bTrans = true;
				strSql = "update " + pub.yssGetTableName("TB_DATA_OFFACCT") +
						" set FSTATUS = ? " +
						" where FDATE = " + 
						dbl.sqlString(this.strMonth) +
						" and FFUNDCODE = ?";
				ps = dbl.openPreparedStatement(strSql);
				OffAcctBean tempOffAcctBean = null;
				for (int i = 0; i < selectedAry.size(); i++) {
					tempOffAcctBean = (OffAcctBean)selectedAry.get(i);
//					if(this.strStatus.equalsIgnoreCase("0")) {//确认封账
//						ps.setInt(1, 0);
//					} else if(this.strStatus.equalsIgnoreCase("1")) {//取消封账
//						ps.setInt(1, 1);
//					}
					ps.setInt(1, Integer.parseInt(this.strStatus));//strStatus为0确认封账，为1取消封账
					ps.setString(2, tempOffAcctBean.strPortCode);
					ps.addBatch();
				}
				ps.executeBatch();
				dbl.closeStatementFinal(ps);//add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B
				conn.commit();
				reStr = "success";
				bTrans = false;
				conn.setAutoCommit(true);
			} catch (Exception e) {
				if(this.strStatus.equalsIgnoreCase("0")) {
					reStr = "确认封账出错：" + e.getMessage();
					throw new YssException("确认封账出错：" + e.getMessage(), e);
				} else if(this.strStatus.equalsIgnoreCase("1")) {
					reStr = "取消封账出错：" + e.getMessage();
					throw new YssException("取消封账出错：" + e.getMessage(), e);
				}
			} finally {
	            dbl.endTransFinal(conn, bTrans);
				//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
				dbl.closeStatementFinal(ps);
				//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			}
		} else if(sType.equalsIgnoreCase("getStatus")) {
			reStr = getOffAcctInfo(this.strMonth, this.strPortCode);
		}
		return reStr;
	}

	public String getOffAcctInfo(String strDates, String strPorts) throws YssException {
		String reStr = "";
		ResultSet rs = null;
        String strSql = "";
        String[] dates = null;
        String[] ports = null;
        //组合代码或业务日期为空返回空
        //if(strDates.trim().equalsIgnoreCase("") || strDates == null || strPorts.trim().equalsIgnoreCase("") || strPorts == null) return "";
        //edit by qiuxufeng 20101122 传递参数为null时，空指针错误
        if(null == strDates || strDates.trim().equalsIgnoreCase("") || null == strPorts || strPorts.trim().equalsIgnoreCase("")) return "";
        dates = strDates.split("~n~");//解析所选的组合的业务日期
        ports = strPorts.split(",");//解析所选的组合
        try {
        	HashSet hs = new HashSet(); 
        	if(dates.length == 2 && ports.length == 1) {//一个组合的一个时间段的判断，一合并到下面的else中
        		String tempBDate = YssFun.formatDate(dates[0], "yyyyMM");
        		String tempEDate = YssFun.formatDate(dates[1], "yyyyMM");
				strSql = "select * from " + pub.yssGetTableName("TB_DATA_OFFACCT") +
						" where FSTATUS = 0 and ffundcode = " + dbl.sqlString(ports[0]) +
						" and to_date(fdate,'yyyyMM') >= " + dbl.sqlDateS(tempBDate,"yyyyMM") +
						" and to_date(fdate,'yyyyMM') <= " + dbl.sqlDateS(tempEDate, "yyyyMM") +
						" order by fdate";
				rs = dbl.openResultSet(strSql);
				while(rs.next()) {
					String tempDate = rs.getString("fdate");//获取封账月份
					tempDate = tempDate.substring(0,4) + "-" + tempDate.substring(4,6) + "-01";//重新解析月份便于格式化月份为yyyy年MM月
					hs.add(YssFun.formatDate(tempDate, "yyyy年MM月") + "\f\f" + ports[0]);//将提示信息放到HashSet中，可以去除重复项
				}
				dbl.closeResultSetFinal(rs);//add by wangzuochun 关闭游标
        	} else {
	        	for (int i = 0; i < ports.length; i++) {
	        		if(ports[i].indexOf("-") == -1) {//单组合群
		        		if(dates[i].indexOf(",") != -1) {
		        			String[] tempDates = dates[i].split(",");
		        			String tempBDate = YssFun.formatDate(tempDates[0], "yyyyMM");
			        		String tempEDate = YssFun.formatDate(tempDates[1], "yyyyMM");
							strSql = "select * from " + pub.yssGetTableName("TB_DATA_OFFACCT") +
									" where FSTATUS = 0 and ffundcode = " + dbl.sqlString(ports[0]) +
									" and to_date(fdate,'yyyyMM') >= " + dbl.sqlDateS(tempBDate,"yyyyMM") +
									" and to_date(fdate,'yyyyMM') <= " + dbl.sqlDateS(tempEDate, "yyyyMM") +
									" order by fdate";
							rs = dbl.openResultSet(strSql);
							while(rs.next()) {
								String tempDate = rs.getString("fdate");//获取封账月份
								tempDate = tempDate.substring(0,4) + "-" + tempDate.substring(4,6) + "-01";//重新解析月份便于格式化月份为yyyy年MM月
								hs.add(YssFun.formatDate(tempDate, "yyyy年MM月") + "\f\f" + ports[i]);//将提示信息放到HashSet中，可以去除重复项
							}
							dbl.closeResultSetFinal(rs);//add by nimengjing 2011.1.31 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。
		        		} else {
			        		String tempDate = YssFun.formatDate(dates[i], "yyyyMM");
							strSql = "select * from " + pub.yssGetTableName("TB_DATA_OFFACCT") +
									" where FSTATUS = 0 and ffundcode = " + dbl.sqlString(ports[i]) +
									" and fdate = " + dbl.sqlString(tempDate);
							rs = dbl.openResultSet(strSql);
							if(rs.next()) {
								hs.add(YssFun.formatDate(dates[i], "yyyy年MM月") + "\f\f" + ports[i]);//将提示信息放到HashSet中，可以去除重复项
							}	
							dbl.closeResultSetFinal(rs);//add by nimengjing 2011.1.31 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。
		        		}
	        		} else {//跨组合群判断
	        			String[] overAssertCode = ports[i].split("-");
	        			if(dates[i].indexOf(",") != -1) {
		        			String[] tempDates = dates[i].split(",");
		        			String tempBDate = YssFun.formatDate(tempDates[0], "yyyyMM");
			        		String tempEDate = YssFun.formatDate(tempDates[1], "yyyyMM");
							strSql = "select * from TB_" + overAssertCode[0] + "_DATA_OFFACCT" +
									" where FSTATUS = 0 and ffundcode = " + dbl.sqlString(overAssertCode[1]) +
									" and to_date(fdate,'yyyyMM') >= " + dbl.sqlDateS(tempBDate,"yyyyMM") +
									" and to_date(fdate,'yyyyMM') <= " + dbl.sqlDateS(tempEDate, "yyyyMM") +
									" order by fdate";
							rs = dbl.openResultSet(strSql);
							while(rs.next()) {
								String tempDate = rs.getString("fdate");//获取封账月份
								tempDate = tempDate.substring(0,4) + "-" + tempDate.substring(4,6) + "-01";//重新解析月份便于格式化月份为yyyy年MM月
								hs.add(YssFun.formatDate(tempDate, "yyyy年MM月") + "\f\f" + ports[i]);//将提示信息放到HashSet中，可以去除重复项
							}
							dbl.closeResultSetFinal(rs);//add by nimengjing 2011.1.31 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。
		        		} else {
			        		String tempDate = YssFun.formatDate(dates[i], "yyyyMM");
							strSql = "select * from TB_" + overAssertCode[0] + "_DATA_OFFACCT" +
									" where FSTATUS = 0 and ffundcode = " + dbl.sqlString(overAssertCode[1]) +
									" and fdate = " + dbl.sqlString(tempDate);
							rs = dbl.openResultSet(strSql);
							if(rs.next()) {
								hs.add(YssFun.formatDate(dates[i], "yyyy年MM月") + "\f\f" + ports[i]);//将提示信息放到HashSet中，可以去除重复项
							}
							dbl.closeResultSetFinal(rs);//add by nimengjing 2011.1.31 BUG #1003 交易结算界面，同时对一千条数据进行结算时，系统报“超出打开游标的错误”。
		        		}
	        		}
				}
        	}

			for (Iterator iterator = hs.iterator(); iterator.hasNext();) {
				reStr += (String) iterator.next() + "\t";//将提示信息用分割符组成字符串
			}
        	if(reStr.length() > 2) {
        		reStr = reStr.substring(0, reStr.length() - 1);//去除多余分隔符
        	}
        	
		} catch (Exception e) {
			throw new YssException("查询组合封账状态信息出错：" + e.getMessage(), e);
		} finally {
            dbl.closeResultSetFinal(rs);
		}
		return reStr;
	}
}
