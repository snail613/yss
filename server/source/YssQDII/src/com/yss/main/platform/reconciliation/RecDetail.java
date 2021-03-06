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

/**
 * MS01620 关于电子对账需求
 * @包名：com.yss.main.platform.reconciliation
 * @文件名：RecDetail.java
 * @创建人：qiuxufeng
 * @创建时间：2010-10-16
 * @版本号：v4.1
 * @说明：
 * <P> 
 * @修改记录
 * 日期        |   修改人       |   版本         |   说明<br>
 * ----------------------------------------------------------------<br>
 * 2010-10-16 | qiuxufeng | V4.1 |
 */
public class RecDetail 
			extends BaseDataSettingBean implements IDataSetting {

	private String strFsn = "";
	private String strFundName = "";
	private String strRecDate = "";
	private String strRecType = "";
	private String strAccountCode = "";
	private String strRowNum = "";

    private String listView1Headers = "";
    private String listView1ShowCols = "";
    private String listView2Headers = "";
    private String listView2ShowCols = "";
    private String listView3Headers = "";
    private String listView3ShowCols = "";
	
    
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

	public String getListView2Headers() {
		return listView2Headers;
	}

	public void setListView2Headers(String listView2Headers) {
		this.listView2Headers = listView2Headers;
	}

	public String getListView2ShowCols() {
		return listView2ShowCols;
	}

	public void setListView2ShowCols(String listView2ShowCols) {
		this.listView2ShowCols = listView2ShowCols;
	}

	public String getListView3Headers() {
		return listView3Headers;
	}

	public void setListView3Headers(String listView3Headers) {
		this.listView3Headers = listView3Headers;
	}

	public String getListView3ShowCols() {
		return listView3ShowCols;
	}

	public void setListView3ShowCols(String listView3ShowCols) {
		this.listView3ShowCols = listView3ShowCols;
	}

	public String getStrFsn() {
		return strFsn;
	}

	public void setStrFsn(String strFsn) {
		this.strFsn = strFsn;
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

	public String getStrRecType() {
		return strRecType;
	}

	public void setStrRecType(String strRecType) {
		this.strRecType = strRecType;
	}

	public String getStrAccountCode() {
		return strAccountCode;
	}

	public void setStrAccountCode(String strAccountCode) {
		this.strAccountCode = strAccountCode;
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
		buf.append(this.strFundName.trim()).append("\t");
		buf.append(this.strRecDate.trim()).append("\t");
		buf.append(this.strRecType.trim()).append("\t");
		buf.append(this.strAccountCode.trim()).append("\t");
		buf.append(this.strRowNum.trim()).append("\tnull");
		
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        try {
        	if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.strFsn = reqAry[0];
            this.strFundName = reqAry[1];
            this.strRecDate = reqAry[2];
            this.strRecType = reqAry[3];
            this.strAccountCode = reqAry[4];
            
		} catch (Exception e) {
			throw new YssException("解析数据请求出错", e);
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
        String strListViewShowCols = "";
        try {
        	if(strRecType.equalsIgnoreCase("1031") && strRecType.length() != 0) {
        		sHeader = this.getListView1Headers();
        		strListViewShowCols = this.getListView1ShowCols();
                strSql ="select ROWNUM, a.*, b.fportcode, b.FPORTNAME from TDzAccount a " +
                		" left join " + pub.yssGetTableName("Tb_para_portfolio") + " b on a.ffundcode = b.fassetcode " +
                		" where FSN = " +
                		dbl.sqlString(this.strFsn);
                
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    		append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
        	} else if(strRecType.equalsIgnoreCase("1011") && strRecType.length() != 0) {
        		sHeader = this.getListView2Headers();
        		strListViewShowCols = this.getListView2ShowCols();
                strSql ="select ROWNUM, a.*, a.FKmbm as FAcctCode, b.fportcode, b.FPORTNAME from TDzJJGZB a " +
                		" left join " + pub.yssGetTableName("Tb_para_portfolio") + " b on a.ffundcode = b.fassetcode " +
                		" where FSN = " +
                		dbl.sqlString(this.strFsn);
                
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(this.buildRowShowStr(rs, this.getListView2ShowCols())).
                    		append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
        	} else if(strRecType.equalsIgnoreCase("1001") && strRecType.length() != 0) {
        		sHeader = this.getListView3Headers();
        		strListViewShowCols = this.getListView3ShowCols();
                strSql ="select ROWNUM, a.*, b.fportcode, b.FPORTNAME from TDzbalance a " +
                		" left join " + pub.yssGetTableName("Tb_para_portfolio") + " b on a.ffundcode = b.fassetcode " +
                		" where FSN = " +
                		dbl.sqlString(this.strFsn);
                
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    bufShow.append(this.buildRowShowStr(rs, this.getListView3ShowCols())).
                    		append(YssCons.YSS_LINESPLITMARK);

                    setResultSetAttr(rs);
                    bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }
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
            		strListViewShowCols;
        } catch (Exception e) {
            throw new YssException("获取业务数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
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
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "待发送";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "已发送";
    	} else if(sFieldName.equalsIgnoreCase("ffiletype")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("1031")) sFieldValue = "科目信息表";
    		if(sFieldValue.equalsIgnoreCase("1011")) sFieldValue = "估值信息表";
    		if(sFieldValue.equalsIgnoreCase("1001")) sFieldValue = "余额信息表";
    	} else if(sFieldName.equalsIgnoreCase("FAcctClass")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "资产";
    		if(sFieldValue.equalsIgnoreCase("2")) sFieldValue = "负债";
    		if(sFieldValue.equalsIgnoreCase("3")) sFieldValue = "共同类";
    		if(sFieldValue.equalsIgnoreCase("4")) sFieldValue = "权益";
    		if(sFieldValue.equalsIgnoreCase("5")) sFieldValue = "损益";
    	} else if(sFieldName.equalsIgnoreCase("FBalDC")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "中性";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "借方";
    		if(sFieldValue.equalsIgnoreCase("-1")) sFieldValue = "贷方";
    	} else if(sFieldName.equalsIgnoreCase("FHqbz")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "平均价";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "收市价";
    	} else if(sFieldName.equalsIgnoreCase("FIsDetail")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("0")) sFieldValue = "汇总科目";
    		if(sFieldValue.equalsIgnoreCase("1")) sFieldValue = "明细科目";
    	} else if(sFieldName.equalsIgnoreCase("FCurCode")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("001")) sFieldValue = "人民币";
    		if(sFieldValue.equalsIgnoreCase("002")) sFieldValue = "美元";
    	} else if(sFieldName.equalsIgnoreCase("FRPTTYPE")) {
    		sFieldValue = rs.getString(sFieldName) + "";
    		if(sFieldValue.equalsIgnoreCase("01")) sFieldValue = "日报";
    		if(sFieldValue.equalsIgnoreCase("08")) sFieldValue = "统计报表";
    	} else {
    		sFieldValue = rs.getString(sFieldName) + "";
    	}
		return sFieldValue;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strFsn = rs.getString("FSN") + "";
        this.strFundName = rs.getString("fportname") + "";
        this.strRecDate = YssFun.formatDate(rs.getString("fedate")) + "";
        this.strRecType = rs.getString("FFILETYPE") + "";
        this.strAccountCode = rs.getString("FAcctCode") + "";
        this.strRowNum = rs.getString("ROWNUM") + "";
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
