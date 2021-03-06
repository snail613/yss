package com.yss.main.operdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class CPIValueBean extends BaseDataSettingBean implements IDataSetting {

	public CPIValueBean(){
		
	}
	private CPIValueBean cpiValueBean = null;
	private CPIValueBean FilterType = null;
	private String strPortCode = ""; // 组合代码
	private String strPortName = ""; // 组合名称
	private String cpiValueDate = "";//浮动CPI日期
//	private String isOnlyColumn = "";
	private double cpiPrice;//浮动CPI 
	private String strOldPortCode = "";
	private String oldCpiValueDate = "";
	//add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B
	private String sRecycled = "";
	
	
	public String getStrOldPortCode() {
		return strOldPortCode;
	}

	public void setStrOldPortCode(String strOldPortCode) {
		this.strOldPortCode = strOldPortCode;
	}

	public String getOldCpiValueDate() {
		return oldCpiValueDate;
	}

	public void setOldCpiValueDate(String oldCpiValueDate) {
		this.oldCpiValueDate = oldCpiValueDate;
	}

	public CPIValueBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(CPIValueBean filterType) {
		this.FilterType = filterType;
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

	public String getCpiValueDate() {
		return cpiValueDate;
	}

	public void setCpiValueDate(String cpiValueDate) {
		this.cpiValueDate = cpiValueDate;
	}

//	public String getisOnlyColumns() {
//		return isOnlyColumns;
//	}
//
//	public void setisOnlyColumns(String isOnlyColumns) {
//		this.isOnlyColumns = isOnlyColumns;
//	}

	public double getCpiPrice() {
		return cpiPrice;
	}

	public void setCpiPrice(double cpiPrice) {
		this.cpiPrice = cpiPrice;
	}
	

	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
	            strSql = "insert into " + pub.yssGetTableName("TB_DATA_CPIVALUE")
	            	+ " (FPORTCODE, FCPIVALUEDATE, FCPIPRICE,"
	            	+ " fcheckstate, fcreator, fcreatetime) "
	            	+ " values ("
	            	+ dbl.sqlString(this.strPortCode.length() > 0 ? this.strPortCode:" ") + ","
	            	+ dbl.sqlDate(this.cpiValueDate) + "," 
	            	+ this.cpiPrice + ", 0," 
	            	+ dbl.sqlString(this.creatorCode) + ","
	            	+ dbl.sqlString(this.creatorTime) + ")";
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增浮动CPI信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("TB_DATA_CPIVALUE"),
                "FPortCode,FCPIVALUEDATE",
                this.strPortCode+"," + this.cpiValueDate,
                this.strOldPortCode+"," + this.oldCpiValueDate);
	}

	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		//add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B
		String[] arrData = null;
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			//---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B start---//
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) {
            	arrData = sRecycled.split("\r\n");
            	for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B end---//
                    strSql = "update " + pub.yssGetTableName("TB_DATA_CPIVALUE")
            		+ " set FCheckState = " + this.checkStateId 
            		+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
            		+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
            		+ " where fportcode = " + dbl.sqlString(this.strPortCode.length() > 0 ? this.strPortCode : " ")
            		+ " and FCPIVALUEDATE = " + dbl.sqlDate(this.cpiValueDate);
					dbl.executeSql(strSql); // 执行更新操作
					//---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B start---//
            	}
            }
            //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B end---//
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核新股价格信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
	}

	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("TB_DATA_CPIVALUE")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where fportcode = " + dbl.sqlString(this.strPortCode.length() > 0 ? this.strPortCode : " ")
            	+ " and FCPIVALUEDATE = " + dbl.sqlDate(this.cpiValueDate);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除浮动CPI信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        boolean bTrans = false; // 代表是否开始了事务
        String[] arrData = null;
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try {
        	 //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B start---//
        	if (sRecycled != "" && sRecycled != null) {
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
                    //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B end---//
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_CPIVALUE") 
        			+ " where fportcode = " + dbl.sqlString(this.strPortCode.length() > 0 ? this.strPortCode : " ")
        			+ " and FCPIVALUEDATE = " + dbl.sqlDate(this.cpiValueDate);
                    dbl.executeSql(strSql);
                    //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B start---//
                }
        	}
        	 //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B end---//
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除新股价格信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }	
	}

	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("TB_DATA_CPIVALUE")
            	+ " set FPORTCODE = " + dbl.sqlString(this.strPortCode.length() > 0 ? this.strPortCode : " ")
            	+ " , FCPIPRICE = " + this.cpiPrice
            	+ " , FCPIVALUEDATE = " + dbl.sqlDate(this.cpiValueDate)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
				+ " where fportcode = " + dbl.sqlString(this.strOldPortCode.length() > 0 ? this.strOldPortCode : " ")
		        + " and FCPIVALUEDATE = " + dbl.sqlDate(this.oldCpiValueDate);
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改新股价格信息出错", e);
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
		return "";
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.strPortCode).append("\t");
        buf.append(this.strPortName).append("\t");
        buf.append(this.cpiValueDate).append("\t");
        buf.append(this.cpiPrice).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (cpiValueBean == null) {
			cpiValueBean = new CPIValueBean();
			cpiValueBean.setYssPub(pub);
        }
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
            //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B start---//
            if(sRowStr.indexOf("\r\n") > -1){
            	sRecycled = sRowStr;
            	return;
            }
            sRecycled = sRowStr;
            //---add by yangshaokai 2011.12.26 BUG 3429 QDV4赢时胜(测试)2011年12月16日05_B end---//
            reqAry = sTmpStr.split("\t");
            this.strPortCode = reqAry[0];
            this.strPortName = reqAry[1];
            this.isOnlyColumns = reqAry[2];
            this.cpiValueDate = reqAry[3];
            this.cpiPrice = Double.parseDouble(reqAry[4]);
            
            this.checkStateId = Integer.parseInt(reqAry[5]);
            this.strOldPortCode = reqAry[6];
            this.oldCpiValueDate = reqAry[7];
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new CPIValueBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析浮动CPI数据出错！", e);
        }
	}

	public String getListViewData1() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	sHeader = this.getListView1Headers();
        	
        	strSql = "select a.fportcode as Fportcode, b.fportname as FPortName, a.FCPIVALUEDATE as FCPIVALUEDATE, a.FCPIPRICE as FCPIPRICE,"
        		+ " a.FCheckState as FCheckState, a.fcreator as FCreator,d.FUserName as FCreatorName, a.fcreatetime as FCreateTime, a.fcheckuser as FCheckUser, e.fusername as FCheckUserName,"
        		+ " a.fchecktime as FCheckTime from "
        		+ pub.yssGetTableName("TB_DATA_CPIVALUE")
        		+ " a left join (select k.fportcode,k.fportname from " + pub.yssGetTableName("tb_Para_Portfolio")+" k"
        		//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//        		+" join (select k.fportcode,max(k.fstartdate) as fstartdate from "+pub.yssGetTableName("tb_Para_Portfolio")+" k"
//        		+" where k.fcheckstate=1 and k.fstartdate <="+dbl.sqlDate(new java.util.Date())
//        		+" group by k.fportcode) m on m.fportcode = k.fportcode and m.fstartdate=k.fstartdate "
        		//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
        		+" where k.fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		+ " ) b on a.fportcode = b.fportcode "
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
                + " on a.FCreator = d.FUserCode "
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) e " 
                + " on a.FCheckUser = e.FUserCode " 
        		+ " where " + buildFilterSql()
        		+ " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
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
        catch(Exception e){
        	throw new YssException("获取浮动CPI数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
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

	/**
	 * 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if(pub.isBrown()==true) //add by ysh 20111025 STORY 1285  如果要浏览数据，则直接返回
			return " where 1=1";
		if (this.FilterType != null) {
			sResult = "";
			if (this.FilterType.isOnlyColumns.equals("1")) {
				sResult = sResult + " 1 = 2 ";
				return sResult;
			}else{
				sResult = sResult + " 1 = 1 ";
			}
			if (this.FilterType.strPortCode.length() != 0) {
				sResult = sResult + " and a.FPortCode like '"
						+ FilterType.strPortCode.replaceAll("'", "''") + "%'";
			}
			if (!(this.FilterType.cpiPrice == 0)) {
				sResult = sResult + " and a.FCPIPRICE = "
						+ FilterType.cpiPrice;
			}
			// ---------------------------
			if (this.FilterType.cpiValueDate.length() != 0
					&& !this.FilterType.cpiValueDate.equals("9998-12-31")) {
				sResult = sResult + " and FCPIVALUEDATE = "
						+ dbl.sqlDate(FilterType.cpiValueDate);
			}
		}
		return sResult;
	}
	
	private void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	this.strPortCode = rs.getString("FPortCode");
        this.strPortName = rs.getString("FPortName");
        this.cpiValueDate = rs.getDate("FCPIVALUEDATE").toString();
        this.cpiPrice = rs.getDouble("FCPIPRICE");
        
        super.setRecLog(rs);
    }
}
