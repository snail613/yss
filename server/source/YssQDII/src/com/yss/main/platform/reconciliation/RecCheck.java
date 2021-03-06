package com.yss.main.platform.reconciliation;

import java.sql.Connection;
import java.sql.Date;
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

public class RecCheck 
			extends BaseDataSettingBean implements IDataSetting {

	private String strFsn = "";
	private String strPortCodes = "";
	private String strCheckFlag = "";
	private String strFundCode = "";
	private RecCheck filterType = null;
	private String isOnlyColumn = "0";
	private String strDateFrom = "";
	private String strDateTo = "";
	private String[] portCodeAry = null;
    private ArrayList recCheckAry = new ArrayList();
    private String reInfo = "";
    private String strRecDate = "";
    private String strFundName = "";
    private String strFlowNum = "";
    private String strRecType = "";
    private String strResiveDate = "";
    private String strResult = "";
	SingleLogOper logOper = null;
	
	public String getStrPortCodes() {
		return strPortCodes;
	}

	public void setStrPortCodes(String strPortCodes) {
		this.strPortCodes = strPortCodes;
	}

	public String getStrFundCode() {
		return strFundCode;
	}

	public void setStrFundCode(String strFundCode) {
		this.strFundCode = strFundCode;
	}

	public String getStrFsn() {
		return strFsn;
	}

	public void setStrFsn(String strFsn) {
		this.strFsn = strFsn;
	}

	public String getStrCheckFlag() {
		return strCheckFlag;
	}

	public void setStrCheckFlag(String strCheckFlag) {
		this.strCheckFlag = strCheckFlag;
	}

	public RecCheck getFilterType() {
		return filterType;
	}

	public void setFilterType(RecCheck filterType) {
		this.filterType = filterType;
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
		PreparedStatement pst = null;
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "delete from TDzResult " +
        			" where FFUNDCODE = ? " +
        			" and check_flag = ? " +
        			" and jstime = ? " +
        			" and fedate = to_date(?,'yyyy-MM-dd') " +
        			" and frefno = ? " +
        			" and fresult = ?";
        	pst = dbl.openPreparedStatement(strSql);
        	for (int i = 0; i < recCheckAry.size(); i++) {
        		RecCheck tempRecCheck = (RecCheck)recCheckAry.get(i);
				pst.setString(1, tempRecCheck.strFundCode);
				pst.setString(2, tempRecCheck.strCheckFlag);
				pst.setString(3, tempRecCheck.strResiveDate);
				pst.setString(4, tempRecCheck.strRecDate);
				pst.setString(5, tempRecCheck.strFlowNum);
				pst.setString(6, tempRecCheck.strResult);
				pst.addBatch();
			}
        	pst.executeBatch();
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
		//buf.append(this.strFsn.trim()).append("\t");
		buf.append(this.strRecDate.trim()).append("\t");
		buf.append(this.strFundName.trim()).append("\t");
		buf.append(this.strFlowNum.trim()).append("\t");
		//buf.append(this.strRecType.trim()).append("\t");
		buf.append(this.strResiveDate.trim()).append("\t");
		buf.append(this.strResult.trim()).append("\t");
		buf.append(this.strCheckFlag.trim()).append("\t");
		buf.append(this.strFundCode.trim()).append("\tnull");
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
            this.strPortCodes = reqAry[0];	//用逗号隔开
			this.strDateFrom = reqAry[1];
			this.strDateTo = reqAry[2];
			this.strFsn = reqAry[3];
			this.strFundCode = reqAry[4];
			this.strCheckFlag = reqAry[5];
			this.strRecDate = reqAry[6];
			this.strFundName = reqAry[7];
			this.strFlowNum = reqAry[8];
			this.strResiveDate = reqAry[9];
			this.strResult = reqAry[10];
			this.isOnlyColumn = reqAry[11];
			
			if(this.strPortCodes.length() > 0) {
				this.portCodeAry = strPortCodes.split(",");
			}
            
//            super.parseRecLog();
            if(flag == 2) {
	            if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("null")) {
	                if (this.filterType == null) {
	                    this.filterType = new RecCheck();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	            }
            }
            if(flag == 3) {
	            if(!sRowStr.split("\r\t")[2].equalsIgnoreCase("null")) {
	            	String tempStr = sRowStr.split("\r\t")[2];
	            	String[] tempAry = tempStr.split("\f\f");
	            	for (int i = 0; i < tempAry.length; i++) {
	            		RecCheck tempRecCheck = new RecCheck();
	            		tempRecCheck.parseRowStr(tempAry[i]);
	            		recCheckAry.add(tempRecCheck);
					}
	            }
            }
        } catch (Exception e) {
            throw new YssException("解析业务数据设置请求出错", e);
        }

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
            strSql ="select distinct j.ffundcode, j.fportname, j.check_flag, j.jstime, j.fedate, j.frefno," +
            		" (case when (j.fresult = '一致') then '一致' " +
            		" when (j.fresult = '0') then '一致' " +
            		" else '不一致' end " +
            		") as fresult " +            		
            		", count(*) as fdetailResult "
            		+"from " +
            			"(select a.*, i.fportname from " +
	            			"(select b.* from TDzResult b,TDzResult c " +
	            			" where b.ffundcode = c.ffundcode " +
	            			" and b.check_flag = c.check_flag " +
	            			" and b.jstime = c.jstime " +
	            			" and b.fedate = c.fedate " +
	            			//" and b.fresult = c.fresult " +
	            			" and b.frefno = c.frefno) a " +
							" left join " + pub.yssGetTableName("Tb_para_portfolio") + " i on a.ffundcode = i.fassetcode" +
							buildFilterSql() + ") j " +
							" group by j.ffundcode, j.fportname, j.check_flag, j.jstime, j.fedate, j.frefno,fresult "+//add by huangqirong 2011-06-28 story #1190  计算详细不一致条数
							"order by j.frefno, j.jstime, j.ffundcode";
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

	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        //this.strFsn = rs.getString("FSN") + "";
        //this.strPortCodes = rs.getString("FFundcode") + "";
        this.strCheckFlag = rs.getString("check_flag") + "";
        String tempDate = rs.getString("FEDATE");
        if(tempDate != null) 
        	this.strRecDate = YssFun.formatDate(tempDate) + "";
        this.strFundName = rs.getString("fportname") + "";
        this.strFlowNum = rs.getString("FREFNO") + "";
        //this.strRecType = rs.getString("FFILETYPE") + "";
        this.strResiveDate = rs.getString("JSTIME") + "";
        this.strResult = rs.getString("fresult") + "";
        this.strFundCode = rs.getString("ffundcode") + "";
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
            	sResult = sResult + " and a.FEDATE >= " + 
            				dbl.sqlDate(this.filterType.strDateFrom) + 
            				" and a.FBDATE <= " + 
            				dbl.sqlDate(this.filterType.strDateTo);
            } else if (this.filterType.strDateFrom.length() != 0) {
            	sResult = sResult + " and a.FEDATE >= " + 
							dbl.sqlDate(this.filterType.strDateFrom);
            } else if (this.filterType.strDateTo.length() != 0) {
            	sResult = sResult + " and a.FBDATE <= " + 
							dbl.sqlDate(this.filterType.strDateTo);
            }
            if(this.filterType.portCodeAry != null) {
            	String[] tempArray = this.filterType.portCodeAry;
            	sResult = sResult + " and (";
            	for (int i = 0; i < tempArray.length; i++) {
            		sResult = sResult + " i.fportcode = " + 
					dbl.sqlString(tempArray[i].split("-")[1]);
            		sResult = sResult + " or";
				}
            	sResult = sResult.substring(0, sResult.length() - 2);
            	sResult = sResult + ")";
            }
        }
		return sResult;
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
