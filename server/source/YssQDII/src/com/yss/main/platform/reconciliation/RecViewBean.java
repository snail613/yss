package com.yss.main.platform.reconciliation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.pojo.sys.YssCancel;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * MS01620 关于电子对账需求
 * @包名：com.yss.main.platform.reconciliation
 * @文件名：RecViewBean.java
 * @创建人：qiuxufeng
 * @创建时间：2010-10-13
 * @版本号：v4.1
 * @说明：
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-13 | qiuxufeng | V4.1 |
 */
public class RecViewBean 
		extends BaseDataSettingBean implements IDataSetting {

	private String strPortCode = "";//组合代码
	private String strDateFrom = "";//开始日期
	private String strDateTo = "";//结束日期
	private String strFisSend = "";//发送状态
	private String[] portCodeAry = null;
	private RecViewBean filterType = null;
	private String listView1Headers = "";
    private String listView1ShowCols = "";
    private String isOnlyColumn = "0";
    private String strFsn = "";
    private String strFileType = "";
    private String strRowNum = "";
    private String strFundName = "";
    private String strRecDate = "";
    private ArrayList recViewAry = new ArrayList();
    private String reInfo = "";
	SingleLogOper logOper = null;
	
	public String getListView1Headers() {
		return listView1Headers;
	}

	public void setListView1Headers(String listView1Headers) {
		this.listView1Headers = listView1Headers;
	}

	public String getListView1ShowCols() {
		return listView1ShowCols;
	}

	public void setListView1ShowCols(String listView1ShowCols) {
		this.listView1ShowCols = listView1ShowCols;
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

	public String getStrFisSend() {
		return strFisSend;
	}

	public void setStrFisSend(String strFisSend) {
		this.strFisSend = strFisSend;
	}

	public String getStrFsn() {
		return strFsn;
	}

	public void setStrFsn(String strFsn) {
		this.strFsn = strFsn;
	}

	public String getStrFileType() {
		return strFileType;
	}

	public void setStrFileType(String strFileType) {
		this.strFileType = strFileType;
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

	public String getStrFundName() {
		return strFundName;
	}

	public void setStrFundName(String strFundName) {
		this.strFundName = strFundName;
	}

	public String getStrRecDate() {
		return strRecDate;
	}

	public void setStrRecDate(String strRecDate) {
		this.strRecDate = strRecDate;
	}

	public void delSetting() throws YssException {
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "delete from TDZBBINFO where FSN = ?";
        	pst = dbl.openPreparedStatement(strSql);
        	for (int i = 0; i < recViewAry.size(); i++) {
        		RecViewBean tempRecViewBean = (RecViewBean)recViewAry.get(i);
				pst.setString(1, tempRecViewBean.strFsn);
				pst.addBatch();
			}
        	pst.executeBatch();
        	
        	for (int i = 0; i < recViewAry.size(); i++) {
        		String tbName = "";
        		RecViewBean tempRecViewBean = (RecViewBean)recViewAry.get(i);
				if(tempRecViewBean.strFileType.equalsIgnoreCase("1031")) {
					tbName = "TDzAccount";
				} else if(tempRecViewBean.strFileType.equalsIgnoreCase("1011")) {
					tbName = "TDzJJGZB";
				} else if(tempRecViewBean.strFileType.equalsIgnoreCase("1001")) {
					tbName = "TDzbalance";
				} else {
					return;
				}
				strSql = "delete from " + tbName +
						" where FSN = " + dbl.sqlString(tempRecViewBean.strFsn);
				dbl.executeSql(strSql);
			}
        	reInfo = getListViewData1();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

			logOper = SingleLogOper.getInstance();
            logOper.setIData(this, YssCons.OP_DEL, pub);
		} catch (Exception e) {
			logOper = SingleLogOper.getInstance();
            logOper.setIData(this, YssCons.OP_DEL, pub, true);
			throw new YssException("删除出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String editSetting() throws YssException {
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "update TDZBBINFO set FISSEND = 1 where FSN = ?";
        	pst = dbl.openPreparedStatement(strSql);
        	for (int i = 0; i < recViewAry.size(); i++) {
        		RecViewBean tempRecViewBean = (RecViewBean)recViewAry.get(i);
				pst.setString(1, tempRecViewBean.strFsn);
				pst.addBatch();
			}
        	pst.executeBatch();
        	
        	reInfo = getListViewData1();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

			logOper = SingleLogOper.getInstance();
            logOper.setIData(this, YssCons.OP_SEND, pub);
            return reInfo;
		} catch (Exception e) {
			logOper = SingleLogOper.getInstance();
            logOper.setIData(this, YssCons.OP_SEND, pub, true);
			throw new YssException("发送出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strFsn.trim()).append("\t");
		buf.append(this.strFisSend.trim()).append("\t");
		buf.append(this.strFileType.trim()).append("\t");
		buf.append(this.strRowNum.trim()).append("\t");
		buf.append(this.strFundName.trim()).append("\t");
		buf.append(this.strRecDate.trim()).append("\t");
		buf.append(this.strPortCode.trim()).append("\t"); // add by qiuxufeng 20110319 增加组合代码
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        int flag = 0;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                flag = sRowStr.split("\r\t").length;
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
//            if (reqAry.length < 28) {
//                return;
//            }
            this.strPortCode = reqAry[0];	//用逗号隔开
			this.strDateFrom = reqAry[1];
			this.strDateTo = reqAry[2];
			this.strFisSend = reqAry[3];
			this.strFsn = reqAry[4];
			this.strFileType = reqAry[5];
			this.isOnlyColumn = reqAry[6];//add by yanghaiming 20101025
			if(this.strPortCode.length() > 0) {
				this.portCodeAry = strPortCode.split(",");
			}
            
//            super.parseRecLog();
            if(flag >= 2) {
	            if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("null")) {
	                if (this.filterType == null) {
	                    this.filterType = new RecViewBean();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	            }
            }
            if(flag >= 3) {
	            if(!sRowStr.split("\r\t")[2].equalsIgnoreCase("null")) {
	            	String tempStr = sRowStr.split("\r\t")[2];
	            	String[] tempAry = tempStr.split("\f\f");
	            	for (int i = 0; i < tempAry.length; i++) {
	            		RecViewBean tempRecViewBean = new RecViewBean();
	            		tempRecViewBean.parseRowStr(tempAry[i]);
	            		recViewAry.add(tempRecViewBean);
					}
	            }
            }
        } catch (Exception e) {
            throw new YssException("解析业务数据设置请求出错", e);
        }
	}

	public String buildRowShowStr(ResultSet rs, String sShowFields) throws
    YssException {
	    StringBuffer buf = new StringBuffer();
	    String[] sFieldAry = sShowFields.split("\t");
	    HashMap hmFieldType = null;
	    String sFieldType = null;
	    String sResult = "";
	    YssCancel before = new YssCancel();
	    String sFieldName = "";
	    String sFieldFormat = "";
	    try {
	        hmFieldType = dbFun.getFieldsType(rs);
	        if (hmFieldType == null) {
	            return "";
	        }
	        for (int i = 0; i < sFieldAry.length; i++) {
	            before.setCancel(false);
	            beforeBuildRowShowStr(before, sFieldAry[i], rs, buf);
	            if (!before.isCancel()) {
	                sFieldFormat = "";
	                if (sFieldAry[i].indexOf(";") > 0) {
	                    sFieldName = sFieldAry[i].split(";")[0];
	                    sFieldFormat = sFieldAry[i].split(";")[1];
	                } else {
	                    sFieldName = sFieldAry[i];
	                }
	                sFieldType = (String) hmFieldType.get(sFieldName.toUpperCase());
	                if (sFieldType != null) {
	                    if ( (sFieldType).indexOf("DATE") > -1) {
	                        if (rs.getDate(sFieldName) != null) {
	                            buf.append(YssFun.formatDate(rs.getDate(sFieldName)));
	                        } else {
	                            buf.append("");
	                        }
	                    } else if ( (sFieldType).indexOf("NUMBER") > -1) {
	                        if (sFieldFormat.length() > 0) {
	                            buf.append(YssFun.formatNumber(rs.getDouble(sFieldName),
	                                sFieldFormat) + "");
	                        } else {
	                        	buf.append(buildMask(rs, sFieldName));
	                        }
	                    } else if ( (sFieldType).indexOf("CLOB") > -1) {
	                        buf.append(dbl.clobStrValue(rs.getClob(sFieldName)));
	                    } else {
	//                 rs.getClob()
	                        buf.append(buildMask(rs, sFieldName));
	                    }
	                    buf.append("\t");
	                }
	                if (isJudge(sFieldName)) {
	                    buildRowOtherShowStr(sFieldName, rs, buf);
	                }
	            }
	        }
	        sResult = buf.toString();
	        if (sResult.trim().length() > 1) {
	            sResult = sResult.substring(0, sResult.length() - 1);
	        }
	        return sResult;
	    } catch (Exception e) {
	        throw new YssException("生成显示数据出错");
	    }
	}

	public String buildMask(ResultSet rs, String sFieldName) throws YssException, SQLException {
		String sFieldValue = "";
    	if(sFieldName.equalsIgnoreCase("fissend")) {
    		sFieldValue = rs.getString(sFieldName);
    		//---edit by songjie 2011.05.26 BUG 1800 QDV4汇添富2011年04月26日01_B---//
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "生成";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "提交";
    		if(sFieldValue.equalsIgnoreCase("2")) sFieldValue = "报文系统已读入并准备发送";
    		if(sFieldValue.equalsIgnoreCase("3")) sFieldValue = "发送成功";
    		if(sFieldValue.equalsIgnoreCase("4")) sFieldValue = "收到回执";
    		if(sFieldValue.equalsIgnoreCase("5")) sFieldValue = "收到对账结果";
    		//---edit by songjie 2011.05.26 BUG 1800 QDV4汇添富2011年04月26日01_B---//
    	} else if(sFieldName.equalsIgnoreCase("ffiletype")) {
    		sFieldValue = rs.getString(sFieldName);
    		if(sFieldValue.equalsIgnoreCase("1031")) sFieldValue = "电子对账科目表";
    		if(sFieldValue.equalsIgnoreCase("1011")) sFieldValue = "电子对账估值表";
    		if(sFieldValue.equalsIgnoreCase("1001")) sFieldValue = "电子对账余额表";
    	} else {
    		sFieldValue = rs.getString(sFieldName + "");
    	}
		return sFieldValue;
	}
	
	public String getListViewData1() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql ="select ROWNUM, a.*, d.fportcode, d.fportname from " + // edit by qiuxufeng 20110319 增加组合代码
            			" (select * from (select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
            			" left join TDzJJGZB c on b.fsn = c.fsn where b.Ffiletype = '1011'" +
            			" union all" +
            			" select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
            			" left join TDzAccount c on b.fsn = c.fsn where b.Ffiletype = '1031'" +
            			" union all" +
            			" select DISTINCT b.*, c.ffundcode as fundcode from TDZBBINFO b" +
            			" left join TDzbalance c on b.fsn = c.fsn where b.Ffiletype = '1001'" +
            			" order by fundcode) order by fsn) a " +
            			" left join " + pub.yssGetTableName("Tb_para_portfolio") + " d on a.ffundcode = d.fassetcode" +
            			buildFilterSql() + 
            			" order by ROWNUM";
//               
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取业务数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	public String buildFilterSql() {
		String sResult = "";
        if (this.filterType != null) {
            if (this.filterType.isOnlyColumn.equals("1")) {
                sResult = " where 1=2";
                return sResult;
            }else{
            	sResult = " where 1=1";
            }
            if (this.filterType.strDateFrom.length() != 0 && this.filterType.strDateTo.length() != 0) {
            	sResult = sResult + " and a.FDATE >= " + 
            				dbl.sqlDate(this.filterType.strDateFrom) + 
            				" and a.FDATE <= " + 
            				dbl.sqlDate(this.filterType.strDateTo);
            } else if (this.filterType.strDateFrom.length() != 0) {
            	sResult = sResult + " and a.FDATE >= " + 
							dbl.sqlDate(this.filterType.strDateFrom);
            } else if (this.filterType.strDateTo.length() != 0) {
            	sResult = sResult + " and a.FDATE <= " + 
							dbl.sqlDate(this.filterType.strDateTo);
            }
            if(this.filterType.portCodeAry != null) {
            	String[] tempArray = this.filterType.portCodeAry;
            	sResult = sResult + " and (";
            	for (int i = 0; i < tempArray.length; i++) {
            		sResult = sResult + " d.FPortCode = " + 
					dbl.sqlString(tempArray[i].split("-")[1]);
            		sResult = sResult + " or";
				}
            	sResult = sResult.substring(0, sResult.length() - 2);
            	sResult = sResult + ")";
            }
            if(this.filterType.strFisSend.length() != 0) {
            	sResult = sResult + " and a.FISSEND = " + dbl.sqlToNumber(this.filterType.strFisSend);
            }
        }
		return sResult;
	}
	
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strFsn = rs.getString("FSN") + "";
        this.strFisSend = rs.getString("FISSEND") + "";
        this.strFileType = rs.getString("FFILETYPE") + "";
        this.strRowNum = rs.getString("ROWNUM") + "";
        this.strFundName = rs.getString("fportname") + "";
        this.strRecDate = YssFun.formatDate(rs.getString("fdate")) + "";
        this.strPortCode = rs.getString("FPortCode") + ""; // add by qiuxufeng 20110319 增加组合代码
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

}
