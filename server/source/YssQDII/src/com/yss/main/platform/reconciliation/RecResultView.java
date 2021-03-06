package com.yss.main.platform.reconciliation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.pojo.sys.YssCancel;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class RecResultView 
				extends BaseDataSettingBean implements IDataSetting {


    private String strFsn = "";
    private String strPortCodes = "";
    private String strCheckFlag = "";
    private String strFundName = "";
    private String strFlowNum = "";
    private String strResiveTime = "";
    private String strRecDate = "";
    private String strRecType = "";
    private RecResultView filterType = null;
    
	public String getStrFsn() {
		return strFsn;
	}

	public void setStrFsn(String strFsn) {
		this.strFsn = strFsn;
	}

	public String getStrPortCodes() {
		return strPortCodes;
	}

	public void setStrPortCodes(String strPortCodes) {
		this.strPortCodes = strPortCodes;
	}

	public String getStrCheckFlag() {
		return strCheckFlag;
	}

	public void setStrCheckFlag(String strCheckFlag) {
		this.strCheckFlag = strCheckFlag;
	}

	public String getStrFundName() {
		return strFundName;
	}

	public void setStrFundName(String strFundName) {
		this.strFundName = strFundName;
	}

	public String getStrFlowNum() {
		return strFlowNum;
	}

	public void setStrFlowNum(String strFlowNum) {
		this.strFlowNum = strFlowNum;
	}

	public String getStrResiveTime() {
		return strResiveTime;
	}

	public void setStrResiveTime(String strResiveTime) {
		this.strResiveTime = strResiveTime;
	}

	public String getStrRecDate() {
		return strRecDate;
	}

	public void setStrRecDate(String strRecDate) {
		this.strRecDate = strRecDate;
	}

	public String getStrRecType() {
		return strRecType;
	}

	public void setStrRecType(String strRecType) {
		this.strRecType = strRecType;
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
		buf.append(this.strPortCodes.trim()).append("\t");
		buf.append(this.strCheckFlag.trim()).append("\t");
		buf.append(this.strFundName.trim()).append("\t");
		buf.append(this.strFlowNum.trim()).append("\t");
		buf.append(this.strResiveTime.trim()).append("\t");
		buf.append(this.strRecDate.trim()).append("\t");
		buf.append(this.strRecType.trim()).append("\tnull");
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
            this.strFsn = reqAry[0];	//用逗号隔开
			this.strPortCodes = reqAry[1];
			this.strCheckFlag = reqAry[2];
            
            if(flag == 2) {
	            if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("null")) {
	                if (this.filterType == null) {
	                    this.filterType = new RecResultView();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
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
            strSql ="select a.*, b.fportcode, b.fportname from TDzResult a " +
						" left join " + pub.yssGetTableName("Tb_para_portfolio") + " b on a.FFUNDCODE = b.fassetcode" +
            			buildFilterSql();
            
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

	public void setResultSetAttr(ResultSet rs) throws SQLException {
        this.strFsn = rs.getString("FSN") + "";
        this.strPortCodes = rs.getString("FFUNDCODE") + "";
        this.strCheckFlag = rs.getString("check_flag") + "";
        this.strFundName  = rs.getString("FPORTNAME") + "";
        this.strFlowNum = rs.getString("FREFNO") + "";
        this.strResiveTime = rs.getString("JSTIME") + "";
        this.strRecDate = rs.getString("FEDATE") + "";
        String tempRecType = rs.getString("CHECK_FLAG");
        if(tempRecType.equalsIgnoreCase("0")) {
        	this.strRecType = "余额表";
        } else if(tempRecType.equalsIgnoreCase("1")) {
        	this.strRecType = "估值表";
        }
        
	}

	public String buildFilterSql() {
		String sResult = "";
		if(this.filterType != null) {
	        sResult = " where 1=1";
	        if (this.filterType.strFsn.length() != 0) {
	        	sResult = sResult + " and a.FSN = " + 
	        				dbl.sqlString(this.filterType.strFsn);
	        }
		}
        return sResult;
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

	private String buildMask(ResultSet rs, String sFieldName) throws YssException, SQLException {
		String sFieldValue = "";
    	if(sFieldName.equalsIgnoreCase("Ftime")) {
    		sFieldValue = rs.getString(sFieldName);
    		sFieldValue = YssFun.formatDate(sFieldValue);
    	} else if(sFieldName.equalsIgnoreCase("jsTime")) {
    		sFieldValue = rs.getString(sFieldName);
    		if(sFieldValue != null) sFieldValue = YssFun.formatDate(sFieldValue, "yyyy年MM月dd日hh时mm分");
    	} else if(sFieldName.equalsIgnoreCase("Fportname")) {
    		sFieldValue = rs.getString("FPORTCODE") + rs.getString(sFieldName);
    	} else if(sFieldName.equalsIgnoreCase("check_flag")) {
    		sFieldValue = rs.getString(sFieldName);
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "余额表";
    		else if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "估值表";
    	} else if(sFieldName.equalsIgnoreCase("Frpttype")) {
    		sFieldValue = "日报";
    	} else {
    		sFieldValue = rs.getString(sFieldName + "");
    	}
		return sFieldValue; 
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
