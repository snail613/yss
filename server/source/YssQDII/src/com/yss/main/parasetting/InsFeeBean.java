package com.yss.main.parasetting;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

/**
 * <p>Title:投连保险费信息设置</p>
 * add by zhangjun
 * 2011-12-02 
 * STORY #1273 支持保险业务中的投连全托管计提管理费和假设费用 
 **/
public class InsFeeBean 
	extends BaseDataSettingBean implements IDataSetting{
	private String portCode; //组合代码
    private String portName; //组合名称  
    private String periodType; //期间类型
    private String Type; //期间类型   
    private java.util.Date feeDate; //日期
    public java.util.Date getFeeDate() {
		return feeDate;
	}
	public void setFeeDate(java.util.Date feeDate) {
		this.feeDate = feeDate;
	}
	private String desc; //描述
    private String oldPortCode="";
    private java.util.Date oldfeeDate; //日期
    
    private InsFeeBean filterType;
    
    public java.util.Date getOldfeeDate() {
		return oldfeeDate;
	}
	public void setOldfeeDate(java.util.Date oldfeeDate) {
		this.oldfeeDate = oldfeeDate;
	}
	private String sRecycled = "";  
    
    public String getOldPortCode() {
		return oldPortCode;
	}
	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public InsFeeBean getFilterType() {
		return filterType;
	}
	public void setFilterType(InsFeeBean filterType) {
		this.filterType = filterType;
	}	
	
	/**
     * parseRowStr
     * 解析投连保险费设置数据
     * @param sRowStr String
     */
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.portCode = reqAry[0];
            this.portName = reqAry[1];            
            this.Type = reqAry[2];
            this.periodType = reqAry[3];
            //this.feeDate = reqAry[4];
            if (YssFun.isDate(reqAry[4])) {
                if (!reqAry[4].equalsIgnoreCase("0001-01-01")) {
                    this.feeDate = YssFun.parseDate(reqAry[4]);
                }
            }
            this.desc = reqAry[5];
            this.checkStateId = YssFun.toInt(reqAry[6]);
            this.oldPortCode = reqAry[7];
            if (YssFun.isDate(reqAry[8])) {
                if (!reqAry[8].equalsIgnoreCase("0001-01-01")) {
                    this.oldfeeDate = YssFun.parseDate(reqAry[8]);
                }
            }
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InsFeeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析投连保险费设置请求信息出错！", e);
        }		
	}    
	
	public String buildRowStr() throws YssException {
		StringBuffer buffer = new StringBuffer();
        buffer.append(this.portCode.trim()).append("\t");
        buffer.append(this.portName.trim()).append("\t"); 
        buffer.append(this.Type.trim()).append("\t");
        buffer.append(this.periodType.trim()).append("\t");        
        buffer.append(YssFun.formatDate(this.feeDate, YssCons.YSS_DATEFORMAT)).append("\t");         
        buffer.append(this.desc.trim()).append("\t");
        buffer.append(super.buildRecLog());
        return buffer.toString();
	}
	
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
        	strSql = "select * from " + pub.yssGetTableName("TB_DATA_InsuranceFee")
        		+ " where FPortCODE = " + dbl.sqlString(this.portCode) + "and FFeeDate = " + dbl.sqlDate(this.feeDate) ;        	
        	
        	ResultSet rs = dbl.openResultSet(strSql);
            if (rs.next()) {      		
        		throw new YssException( YssFun.getCheckStateName(rs.getInt("FCheckState")) + 
        				"中【"+this.feeDate +"】日【" + this.portCode + "】组合已经存在，请重新输入");      		
        	}
            //---------------------------------
            strSql = "insert into " + pub.yssGetTableName("TB_DATA_InsuranceFee")
              //组合代码，组合名称，期间类型，日期，描述，审核状态，创建人， 创建时间
            	+ " (FPortCODE,FPortName,FPeriodType,FFeeDate,FDesc,FCHECKSTATE,FCREATOR,FCREATETIME) "
            	+ " values ("
            	+ dbl.sqlString(this.portCode) + "," 
            	+ dbl.sqlString(this.portName) + ","  
            	+ dbl.sqlString(this.Type) + "," 
            	+ dbl.sqlDate(YssFun.toSqlDate(this.feeDate)) + ","   
            	+ dbl.sqlString(this.desc) + ","            	
            	+ "0"+","   //审核状态
            	+ dbl.sqlString(this.creatorCode)+","   //制作人
            	+ dbl.sqlString(this.creatorTime)//创建时间
            	//+ dbl.sqlString(this.checkUserCode) + ","//审核人            	
            	//+ dbl.sqlString(this.checkTime) 
            	+ ")";  //审核审核时间            
            dbl.executeSql(strSql);            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增投连保险费信息出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("TB_DATA_InsuranceFee"),
                "FPortCODE,FFeeDate",
                this.getPortCode() + "," + YssFun.formatDate(this.feeDate),
                this.getOldPortCode()+ "," + YssFun.formatDate(this.oldfeeDate));
		
	}
	public void checkSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            /**shashijie 2012-3-21 BUG 4007 原先是\r\t*/
            arrData = sRecycled.split("\r\n");
			/**end*/
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "update " + pub.yssGetTableName("TB_DATA_InsuranceFee") +
                    " set FCheckState=" + this.checkStateId + "," +
                    " FCheckUser = " + dbl.sqlString(this.checkUserCode) + "," +
                    " FCheckTime = " + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FPortCODE = " + dbl.sqlString(this.getPortCode()) + "and FFeeDate = "  + dbl.sqlDate(this.getFeeDate());
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("审核投连保险费信息出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }		
	}
	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}
	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\t");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "delete from " + pub.yssGetTableName("TB_DATA_InsuranceFee") +
                         " where FPortCODE = " + dbl.sqlString(this.getPortCode()) + "and FFeeDate = "  + dbl.sqlDate(this.getFeeDate());
                dbl.executeSql(sqlStr);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除投连保险费信息出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}
	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();        
        try {
            strSql = "UPDATE " + pub.yssGetTableName("TB_DATA_InsuranceFee") +
                " SET FPortCODE = " + dbl.sqlString(this.getPortCode()) + "," +   
                " FPortName = " +  dbl.sqlString(this.getPortName()) + "," + 
                " FPeriodType = " + dbl.sqlString(this.getType()) + "," +
                " FFeeDate = " + dbl.sqlDate(this.getFeeDate()) + "," +
                " FDesc = " + dbl.sqlString(this.getDesc()) +
                " where FPortCODE = " + dbl.sqlString(this.getOldPortCode()) + "and FFeeDate = "  + dbl.sqlDate(this.getOldfeeDate());
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("修改投连保险费信息设置出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
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
	
	
	
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		String strSql = "";
        ResultSet rs = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            strSql = "select a.*,fiv.FVocName AS FPeriodTypeName,c.FUserName as FCreatorName ,d.FUserName as FCheckUserName from "+pub.yssGetTableName("TB_DATA_InsuranceFee")+" a"	        	  
            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
            + " on a.FCreator = c.FUserCode "
            + " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
            + " on a.FCheckUser = d.FUserCode " 
            + " LEFT JOIN Tb_Fun_Vocabulary fiv on a.FPeriodType = fiv.FVocCode" 
            + " and fiv.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_PARA_InsPeriodType) 
            + " where " + buildFilterSql() 
            + " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setInsFeeAttr(rs);
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_PARA_InsPeriodType);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception ex) {
            throw new YssException("获取投连保险费信息出错！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	/**
     * 筛选条件
     * @return String
	 * @throws YssException 
     */
	private String buildFilterSql() throws YssException {
        String sResult = " 1=1 ";
        if (this.filterType != null) {
	        if (this.filterType.portCode != null && this.filterType.portCode.length() != 0  ) {
	            sResult = sResult + " and FPortCODE=" + dbl.sqlString(this.filterType.portCode);
	
	        }   //!monetaryFundBean.getFilterType().getClosedType().equalsIgnoreCase("99")
	        
	        if (this.filterType.Type != null && ! this.filterType.Type.equalsIgnoreCase("99")) {
	            sResult = sResult + " and FPeriodType=" + dbl.sqlString(this.filterType.Type);
	        }
	        
	        if (this.filterType.feeDate != null &&  !this.filterType.feeDate.equals(YssFun.parseDate("9998-12-31"))) {
	            sResult = sResult + " and FFeeDate=" + dbl.sqlDate(this.filterType.feeDate);
	
	        }     
	        
        }
        return sResult;
    }
	
	//组合代码，组合名称，期间类型，日期，描述，审核状态，创建人， 创建时间，审核人，审核时间
	//+ " (FPortCODE,FPortName,FPeriodType,FFeeDate,FDesc,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME) "
	public void setInsFeeAttr(ResultSet rs) throws SQLException, YssException
	{
		this.portCode = rs.getString("FPortCODE");          
		this.portName = rs.getString("FPortName");         
		this.Type = rs.getString("FPeriodType"); 
		this.periodType = rs.getString("FPeriodTypeName");
		this.feeDate = rs.getDate("FFeeDate");     
		this.desc = rs.getString("FDesc")==null?"":rs.getString("FDesc");         
		super.setRecLog(rs);
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
