package com.yss.main.operdata;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**20130710 added by liubo.Story #4106.基金业绩比较基准*/
public class CompareBaselineBean extends BaseDataSettingBean implements IDataSetting 
{
	private String sPortCode = "";				//组合代码
	private String sPortName = "";				//组合名称
	private double dStandardValue = 0;			//业绩比较基准
	private String sDesc = "";					//描述
	private java.util.Date dStartDate = null;	//启用日期
	private java.util.Date dSearchBeginDate = null;	//listview查询时的开始日期
	private java.util.Date dSearchEndDate = null;	//listview查询时的结束日期
	
	private String sOldPortCode = "";				//组合代码旧值
	private java.util.Date dOldStartDate = null;	//启用日期旧值
	
	private String sRecycled = ""; //保存未解析前的字符串
	private CompareBaselineBean filterType;

	/**
	 * 增加数据
	 */
	public String addSetting() throws YssException 
	{
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try 
        {
            strSql = "insert into " +
                pub.yssGetTableName("TB_Data_CompareBaseline") +
                "( FPortCode,FStartDate,FStandardValue,FDesc,FCHECKSTATE,FCREATOR,FCREATETIME" +
                ") values ( " + 
                dbl.sqlString(this.sPortCode) + "," +
                dbl.sqlDate(this.dStartDate) + "," +
                this.dStandardValue + "," +
                dbl.sqlString(this.sDesc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(pub.getUserCode()) + "," +
                dbl.sqlString(YssFun.formatDate(new java.util.Date())) +
                " ) ";
            
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) 
        {
            throw new YssException("新增基金业绩比较基准出错：" + e.getMessage());
        } 
        finally 
        {
            dbl.endTransFinal(conn, bTrans);
        }

        return "";
	}

	/**
	 * 检查传入的数据是否已存在（主要检查主键数据）
	 */
	public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_Data_CompareBaseline"),
                               "FPortCode,FStartDate",
                               this.sPortCode + "," + YssFun.formatDate(this.dStartDate), 
                               this.sOldPortCode + "," + YssFun.formatDate(this.dOldStartDate));
		
	}

	/**
	 * 审核、反审核数据
	 */
	public void checkSetting() throws YssException 
	{

        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("TB_Data_CompareBaseline") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FPortCode = " +dbl.sqlString(this.sPortCode) + 
                        " and FStartDate = " + dbl.sqlDate(this.dStartDate);
                    
                    dbl.executeSql(strSql);
                }
            }
            else 
            {
                strSql = "update " + pub.yssGetTableName("tb_para_InterestTime") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FPortCode = " +dbl.sqlString(this.sPortCode) + 
                " and FStartDate = " + dbl.sqlDate(this.dStartDate);
                
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核/反审核基金业绩比较基准出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	/**
	 * 将数据放入回收站
	 */
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	strSql = "update " + pub.yssGetTableName("TB_Data_CompareBaseline") +
            " set FCheckState = " +
            this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "'" +
            " where FPortCode = " +dbl.sqlString(this.sPortCode) + 
            " and FStartDate = " + dbl.sqlDate(this.dStartDate);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("刪除基金业绩比较基准出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	/**
	 * 清空回收站
	 */
	public void deleteRecycleData() throws YssException 
	{
		boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
		ResultSet rs = null;
        String[] arrData = null;
        String strSql = "";
		
		try {
	        conn.setAutoCommit(false);
            bTrans = true;
            
            if (sRecycled != null && !sRecycled.equals("")) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    
                this.parseRowStr(arrData[i]);
                
                strSql = "delete from " + pub.yssGetTableName("TB_Data_CompareBaseline") + " " +
                		 " where FPortCode = "+ dbl.sqlString(this.sPortCode) + 
                		 " and FStartDate = " + dbl.sqlDate(this.dStartDate) + " and FCHECKSTATE = 2";
                
                dbl.executeSql(strSql);
                }
            }
            
	        
	        conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("清除基金业绩比较基准出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	/**
	 * 修改数据
	 */
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("TB_Data_CompareBaseline") +
                " set " +
                " FPortCode = " + dbl.sqlString(this.sPortCode) + " ," +
                " FStartDate = " + dbl.sqlDate(this.dStartDate) + " ," +
                " FStandardValue = " + this.dStandardValue + "," +
                " FDesc = " + dbl.sqlString(this.sDesc) + 
                " where FPortCode = " + dbl.sqlString(this.sOldPortCode) + 
                " and FStartDate = " + dbl.sqlDate(this.dOldStartDate) + " and FCHECKSTATE = 0"; 
                //" And FStartDate = " + dbl.sqlDate(this.OldFInsStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改基金业绩比较基准出错", e);
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

	/**
	 * 将查询出的数据包装成一个字符串返回
	 */
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
		
		buf.append(this.sPortCode).append("\t");
		buf.append(this.sPortName).append("\t");
		buf.append(YssFun.formatDate(this.dStartDate)).append("\t");
		buf.append(YssFun.formatNumber(this.dStandardValue, "#,##0.0000########")).append("\t");
		buf.append(this.sDesc).append("\t");
		buf.append(super.buildRecLog());
		
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 解析前台传来的请求字符串
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
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.sPortCode = reqAry[0];
            if (YssFun.isDate(reqAry[1])) {
            	this.dStartDate = YssFun.toDate(reqAry[1]);
            }
            if (YssFun.isNumeric(reqAry[2])) {
            	this.dStandardValue = YssFun.toDouble(reqAry[2]);
            }
            this.sDesc = reqAry[3];
            this.sOldPortCode = reqAry[4];
            if (YssFun.isDate(reqAry[5])) {
            	this.dOldStartDate = YssFun.toDate(reqAry[5]);
            }
            if (YssFun.isDate(reqAry[6])) {
            	this.dSearchBeginDate = YssFun.toDate(reqAry[6]);
            }
            if (YssFun.isDate(reqAry[7])) {
            	this.dSearchEndDate = YssFun.toDate(reqAry[7]);
            }
            super.checkStateId = Integer.parseInt(reqAry[8]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CompareBaselineBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析基金业绩比较基准信息出错", e);
        }

		
	}
	
	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = " where 1=1 ";
		
		if (this.filterType != null) 
		{
			if (this.filterType.sPortCode != null && this.filterType.sPortCode.length() != 0) 
			{
				sResult = sResult + " and a.FPortCode = " + dbl.sqlString(filterType.sPortCode);
			}
			if (this.filterType.dStandardValue != 0) 
			{
				sResult = sResult + " and a.FStandardValue = " + filterType.dStandardValue;
			}
			if (this.filterType.dStartDate != null && !YssFun.formatDate(this.filterType.dStartDate).equals("9998-12-31")) 
			{
				sResult = sResult + " and FStartDate = " + dbl.sqlDate(this.filterType.dStartDate);
			}
			if (this.filterType.sDesc != null && this.filterType.sDesc.length() != 0) 
			{
				sResult = sResult + " and a.FDesc like '" + filterType.sDesc + "%' ";
			}
			if (this.filterType.dSearchBeginDate != null 
					&& !YssFun.formatDate(this.filterType.dSearchBeginDate).equals("9998-12-31") 
					&& this.filterType.dSearchEndDate != null 
					&& !YssFun.formatDate(this.filterType.dSearchEndDate).equals("9998-12-31")) 
			{
				sResult = sResult + " and FStartDate between " + dbl.sqlDate(this.filterType.dSearchBeginDate)
						  + " and " + dbl.sqlDate(this.filterType.dSearchEndDate);
			}
		}
		return sResult;
	}

	/**
	 * 根据查询返回的resultset设置全局变量
	 * @param rs
	 * @throws Exception
	 */
	private void setResultSetAttr(ResultSet rs) throws Exception
	{
		this.sPortCode = rs.getString("FPortCode");
		this.sPortName = rs.getString("FPortName");
		this.dStartDate = rs.getDate("FStartDate");
		this.dStandardValue = rs.getDouble("FStandardValue");
		this.sDesc = rs.getString("FDesc");
		
		super.setRecLog(rs);
	}
	
	/**
	 * 筛选数据
	 */
	public String getListViewData1() throws YssException 
	{
		StringBuffer bufSql = new StringBuffer();
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try 
        {
        	sHeader = this.getListView1Headers();
        	
        	bufSql.append("select a.*,b.fportname,c.FCreatorName,d.FCheckUserName ")
        	.append(" from " + pub.yssGetTableName("tb_data_comparebaseline") + " a ")
        	.append(" left join (select FPortCode,FPortName from " + pub.yssGetTableName("tb_para_portfolio"))
        	.append(" where FCheckState = 1) b")
        	.append(" on a.FPortCode = b.fportcode")
        	.append(" left join (select FUserCode,FUserName as FCreatorName from tb_sys_userlist) c")
        	.append(" on a.fcreator = c.fusercode")
        	.append(" left join (select FUserCode,FUserName as FCheckUserName from tb_sys_userlist) d")
        	.append(" on a.fcheckuser = d.fusercode")
        	.append(buildFilterSql());
        	
            rs = dbl.openResultSet(bufSql.toString());
            
            while(rs.next()){
            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                	append(YssCons.YSS_LINESPLITMARK);
            	this.setResultSetAttr(rs);
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
            
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
            + "\r\f" + this.getListView1ShowCols();
        }
        catch(Exception e)
        {
        	throw new YssException("查询基金业绩比较基准数据出错：" + "\r\n" + e.getMessage(), e);
        }
        finally
        {
        	dbl.closeResultSetFinal(rs);
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

	public String getPortCode() {
		return sPortCode;
	}

	public void setPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public String getPortName() {
		return sPortName;
	}

	public void setPortName(String sPortName) {
		this.sPortName = sPortName;
	}

	public double getStandardValue() {
		return dStandardValue;
	}

	public void setStandardValue(double fStandardValue) {
		dStandardValue = fStandardValue;
	}

	public String getDesc() {
		return sDesc;
	}

	public void setDesc(String sDesc) {
		this.sDesc = sDesc;
	}

	public java.util.Date getStartDate() {
		return dStartDate;
	}

	public void setStartDate(java.util.Date dStartDate) {
		this.dStartDate = dStartDate;
	}

	public java.util.Date getSearchBeginDate() {
		return dSearchBeginDate;
	}

	public void setSearchBeginDate(java.util.Date dSearchBeginDate) {
		this.dSearchBeginDate = dSearchBeginDate;
	}

	public java.util.Date getSearchEndDate() {
		return dSearchEndDate;
	}

	public void setSearchEndDate(java.util.Date dSearchEndDate) {
		this.dSearchEndDate = dSearchEndDate;
	}


}
