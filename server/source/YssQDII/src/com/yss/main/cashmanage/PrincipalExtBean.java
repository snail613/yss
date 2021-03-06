package com.yss.main.cashmanage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

public class PrincipalExtBean extends BaseDataSettingBean implements
	IDataSetting  {
	
	private PrincipalExtBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	
	private String num = "";
    private String savingNum = "";//定存编号
	private String portCode = "";
    private String portName = "";
    private String outCashAccCode = "";
    private String outCashAccName = "";
    private String inCashAccCode = "";
    private String inCashAccName = "";
    private java.util.Date extDate = null;//提取日期
    private double outMoney;
    private String desc;
    
    private String strFAnalysisCode1 = ""; 
    private String strFAnalysisName1 = ""; 
    private String strFAnalysisCode2 = ""; 
    private String strFAnalysisName2 = ""; 
    private String strFAnalysisCode3 = ""; 
    private String strFAnalysisName3 = ""; 
    
    /**shashijie 2012-7-18 STORY 2796 */
	private String FTakeType = "";
	/**end*/
    
	private String oldNum;

    private boolean BShow = false;
    
    private PrincipalExtBean principal = null;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 

	public PrincipalExtBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(PrincipalExtBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getSavingNum() {
		return savingNum;
	}

	public void setSavingNum(String savingNum) {
		this.savingNum = savingNum;
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

	public String getOutCashAccCode() {
		return outCashAccCode;
	}

	public void setOutCashAccCode(String outCashAccCode) {
		this.outCashAccCode = outCashAccCode;
	}

	public String getOutCashAccName() {
		return outCashAccName;
	}

	public void setOutCashAccName(String outCashAccName) {
		this.outCashAccName = outCashAccName;
	}

	public String getInCashAccCode() {
		return inCashAccCode;
	}

	public void setInCashAccCode(String inCashAccCode) {
		this.inCashAccCode = inCashAccCode;
	}

	public String getInCashAccName() {
		return inCashAccName;
	}

	public void setInCashAccName(String inCashAccName) {
		this.inCashAccName = inCashAccName;
	}

	public java.util.Date getExtDate() {
		return extDate;
	}

	public void setExtDate(java.util.Date extDate) {
		this.extDate = extDate;
	}

	public double getOutMoney() {
		return outMoney;
	}

	public void setOutMoney(double outMoney) {
		this.outMoney = outMoney;
	}

	public String getOldNum() {
		return oldNum;
	}

	public void setOldNum(String oldNum) {
		this.oldNum = oldNum;
	}

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean show) {
		BShow = show;
	}
	
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.num).append("\t");
        buf.append(this.savingNum).append("\t");
        buf.append(this.outCashAccCode).append("\t");
        buf.append(this.outCashAccName).append("\t");
        buf.append(this.inCashAccCode).append("\t");
        buf.append(this.inCashAccName).append("\t");
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(format.format(this.extDate)).append("\t");
        buf.append(this.outMoney).append("\t");
        buf.append(this.desc).append("\t");
        
        buf.append(this.strFAnalysisCode1).append("\t");
        buf.append(this.strFAnalysisName1).append("\t");
        buf.append(this.strFAnalysisCode2).append("\t");
        buf.append(this.strFAnalysisName2).append("\t");
        buf.append(this.strFAnalysisCode3).append("\t");
        buf.append(this.strFAnalysisName3).append("\t");
        /**shashijie 2012-7-18 STORY 2796 */
		buf.append(this.FTakeType).append("\t");
		/**end*/
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (principal == null) {
			principal = new PrincipalExtBean();
			principal.setYssPub(pub);
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.num = reqAry[0];
            this.savingNum = reqAry[1];
            this.outCashAccCode = reqAry[2];
            this.inCashAccCode = reqAry[3];
            this.portCode = reqAry[4];
            this.extDate =  YssFun.parseDate(reqAry[5].trim().length()==0?"9998-12-31":reqAry[5]);
            this.outMoney = (reqAry[6].trim().length()==0?0:Double.parseDouble(reqAry[6]));          
            if (reqAry[7] != null ){
                if (reqAry[7].indexOf("【Enter】") >= 0){
                     this.desc = reqAry[7].replaceAll("【Enter】", "\r\n");
                }
                else {
                   this.desc = reqAry[7];
                }
            }
            
            this.strFAnalysisCode1 = reqAry[8];
            this.strFAnalysisName1 = reqAry[9];
            this.strFAnalysisCode2 = reqAry[10];
            this.strFAnalysisName2 = reqAry[11];
            this.strFAnalysisCode3 = reqAry[12];
            this.strFAnalysisName3 = reqAry[13];
            
            this.oldNum = reqAry[14];

            this.checkStateId = Integer.parseInt(reqAry[15]);
            
            if (reqAry[16].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            /**shashijie 2012-7-18 STORY 2796 */
			this.FTakeType = reqAry[17];
			/**end*/
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new PrincipalExtBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析本金提取数据出错！", e);
        }

	}
	
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            String nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
            this.num = "ET" + nowDate +
            dbFun.getNextInnerCode(pub.yssGetTableName("TB_Cash_Consavingpriext"),
                                   dbl.sqlRight("FNum", 5), "00001",
                                   " where FNum like 'ET"
                                   + nowDate + "%'", 1);
            strSql = "insert into " + pub.yssGetTableName("TB_Cash_Consavingpriext")
            	+ " (Fnum, FCONSAVINGNUM, FOUTCASHACCOUNTCODE, FOUTMONEY, FINCASHACCOUNTCODE, FPORTCODE,FEXTDATE,FDESC,"
            	+ " fcheckstate, fcreator, fcreatetime, FAnalysisCode1, FAnalysisCode2, FAnalysisCode3" +
            	/**shashijie 2012-7-18 STORY 2796 */
				" , FTakeType "+
				/**end*/		
            	" ) "
            	+ " values ("
            	+ dbl.sqlString(this.num) + ","
            	+ dbl.sqlString(this.savingNum) + "," 
            	+ dbl.sqlString(this.outCashAccCode) + ","
            	+ this.outMoney + ","
            	+ dbl.sqlString(this.inCashAccCode) + "," 
            	+ dbl.sqlString(this.portCode) + ","
            	+ dbl.sqlDate(this.extDate) + "," 
            	+ dbl.sqlString(this.desc) + "," 
            	+ "0," 
            	+ dbl.sqlString(this.creatorCode) + ","
            	+ dbl.sqlString(this.creatorTime) + ","
            
            	+ dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " :
                    this.strFAnalysisCode1) + ","
            	+ dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " :
                    this.strFAnalysisCode2) + ","
            	+ dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " :
                    this.strFAnalysisCode3) + 
                /**shashijie 2012-7-18 STORY 2796 */
				" , "+dbl.sqlString(this.FTakeType)+
				/**end*/    
                " ) ";

            dbl.executeSql(strSql);
        
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增本金提取信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public void checkInput(byte btOper) throws YssException {	
		 
	}
	
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("TB_Cash_Consavingpriext")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FNum = " + dbl.sqlString(this.num);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除本金提取信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public void checkSetting() throws YssException {
		
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase("")) ) { // 判断传来的内容是否为空 //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					strSql = "update " + pub.yssGetTableName("TB_Cash_Consavingpriext")
                	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                	+ ", FCheckUser = " 
                	+ dbl.sqlString(this.checkUserCode)
                	+ ", FCheckTime = "
                	+ dbl.sqlString(this.checkTime)
                	+ " where FNum = " + dbl.sqlString(this.num);
	
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核本金提取信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
	}
	
	//清除
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( sRecycled != null && !sRecycled.equalsIgnoreCase("")) { //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
                // 根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                // 循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("TB_Cash_Consavingpriext") 
	                	+ " where FNum = " + dbl.sqlString(this.num);
                    
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除本金提取信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }		

	}
	
	//修改
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("TB_Cash_Consavingpriext")
            
            	+ " set FCONSAVINGNUM = " + dbl.sqlString(this.savingNum)
            	+ " , FOUTCASHACCOUNTCODE = " + dbl.sqlString(this.outCashAccCode)
            	+ " , FOUTMONEY = " + this.outMoney
            	+ " , FINCASHACCOUNTCODE = " + dbl.sqlString(this.inCashAccCode)
            	+ " , FPORTCODE = " + dbl.sqlString(this.portCode)
            	+ " , FEXTDATE = " + dbl.sqlDate(this.extDate)
            	+ " , FDESC = " + dbl.sqlString(this.desc)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
            	
            	+ ", FAnalysisCode1 = " + dbl.sqlString(this.strFAnalysisCode1.length() == 0 ? " " : this.strFAnalysisCode1)
            	+ ", FAnalysisCode2 = " + dbl.sqlString(this.strFAnalysisCode2.length() == 0 ? " " : this.strFAnalysisCode2)
            	+ ", FAnalysisCode3 = " + dbl.sqlString(this.strFAnalysisCode3.length() == 0 ? " " : this.strFAnalysisCode3)
            	/**shashijie 2012-7-18 STORY 2796 */
				+ " ,FTakeType = "+dbl.sqlString(this.FTakeType)
				/**end*/
				+ " where FNum = " + dbl.sqlString(this.oldNum); 
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改本金提取信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
        /**shashijie 2012-7-18 STORY 2796 获取已计提的利息总和*/
		String strReturn = "";
		if (sType.trim().equals("getSumInterest")) {
			strReturn = getSumInterest();
		} //判断当天是否已经提取过利息或之前已经有过本金提取
		else if (sType.trim().equals("getHaveDraw")) {
			strReturn = getHaveDraw();
		}
		/**end*/
		return strReturn;

    }
	
	/**shashijie 2012-7-26 STORY 2796 判断当天是否已经提取过利息或之前已经有过本金提取 */
	private String getHaveDraw() throws YssException {
		ResultSet rs = null;
		String value = "false";
		try {
			String query = getHaveDrawQuery();
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = "true";
			}
		} catch (Exception e) {
			throw new YssException("获取已计提的利息总和出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2012-7-26 STORY 2796 */
	private String getHaveDrawQuery() {
		String query = "Select a.*" +
			" From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" a" +
			" Where a.Fconsavingnum = "+dbl.sqlString(this.num)+
			" And a.Fportcode = "+dbl.sqlString(this.portCode)+
			" And (" +
			" (a.Fextdate = "+dbl.sqlDate(this.extDate)+" And a.Ftaketype = 1)" +//当天有提取利息
			" Or (a.Fextdate <= "+dbl.sqlDate(this.extDate)+" And (a.Ftaketype = 0 Or a.Ftaketype Is Null))" +//之前有本金提取
			" )";
       return query;
	}

	/**shashijie 2012-7-18 STORY 获取已计提的利息总和 */
	private String getSumInterest() throws YssException{
		ResultSet rs = null;
		String value = "";
		try {
			String query = getSumInterestQuery();
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				value = String.valueOf(rs.getDouble("FMoney"));
			}
		} catch (Exception e) {
			throw new YssException("获取已计提的利息总和出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;
	}

	/**shashijie 2012-7-18 STORY 2796
	* @return*/
	private String getSumInterestQuery() {
		String value = " Select Nvl(Nvl(a.FMoney, 0) - Nvl(b.Foutmoney, 0), 0) FMoney" +
			" From (Select Nvl(Sum(A1.FMoney), 0) FMoney, A1.Frelanum" +
			" From "+pub.yssGetTableName("Tb_Data_Cashpayrec")+" A1" +
			" Where A1.Fportcode = "+dbl.sqlString(this.portCode)+
            " And A1.Fcashacccode = "+dbl.sqlString(this.outCashAccCode)+
            " And A1.Ftransdate <= "+dbl.sqlDate(this.extDate)+
            " And A1.Frelanum = "+dbl.sqlString(this.num)+
            " And A1.FTsfTypeCode = "+dbl.sqlString(YssOperCons.YSS_ZJDBLX_Rec)+
            " And A1.FSubTsfTypeCode = " +dbl.sqlString(YssOperCons.YSS_ZJDBZLX_DE_RecInterest)+
            " And A1.FCheckState = 1 "+
            " Group By A1.Frelanum) a" +
            " Left Join (Select Nvl(Sum(B1.Foutmoney), 0) Foutmoney, B1.Fconsavingnum" +
            " From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" B1" +
    		" Where B1.Fportcode = "+dbl.sqlString(this.portCode)+
            " And B1.Fextdate <= "+dbl.sqlDate(this.extDate)+
            " And B1.Fconsavingnum = "+dbl.sqlString(this.num)+
            " And B1.Fcheckstate = 1" +
            " And B1.Ftaketype = 1" +
            " Group By B1.Fconsavingnum) b On a.Frelanum = b.Fconsavingnum" +
            " Where a.Frelanum Not In" +
            " (Select C1.Fconsavingnum" +
            " From "+pub.yssGetTableName("Tb_Cash_Consavingpriext")+" C1" +
    		" Where C1.Fportcode = "+dbl.sqlString(this.portCode)+
            " And C1.Fextdate <= "+dbl.sqlDate(this.extDate)+
            " And C1.Fconsavingnum = "+dbl.sqlString(this.num)+
            " And C1.Fcheckstate = 1 " +
            " And C1.Ftaketype = 0)" +
            "";
		return value;
	}

	public String getListViewData1() throws YssException {
		String strSql = ""; // 定义一个存放sql语句的字符串
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        //String sAry[] = null;
        try {
            
        	sHeader = this.getListView1Headers();
        	
        	strSql="select a.*, b.fportname as fportname, c.FCashAccName as foutcashaccname," +
				   "d.FCashAccName as fincashaccname, g.FUserName as FCreatorName, h.fusername as FCheckUserName";
        	strSql = strSql +
	            (FilterSql().length() == 0 ?
	             ", ' ' as FAnalysisName1, ' ' as FAnalysisName2, ' ' as FAnalysisName3 " :
	             ", FAnalysisName1, FAnalysisName2, FAnalysisName3 ") +
	            " from " + pub.yssGetTableName("TB_Cash_Consavingpriext") + " a " +
            
				" left join (" //edit by songjie 2011.03.15 不以最大的启用日期查询数据
//				+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.15 不以最大的启用日期查询数据
				+ " select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+pub.yssGetTableName("tb_Para_Portfolio")+
				" where FCheckState = 1"//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+") b "
				+"on a.fportcode = b.fportcode "
				
				+ " left join ("// edit by songjie 2011.03.15 不以最大的启用日期查询数据
//				+pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.15 不以最大的启用日期查询数据
				+" select FSTARTDATE, FCashAccCode, FCashAccName from "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+pub.yssGetTableName("Tb_Para_CashAccount")+
				" where FCheckState = 1"//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+") c on a.FOUTCASHACCOUNTCODE = c.fcashacccode" 
				
				+ " left join (" // edit by songjie 2011.03.15 不以最大的启用日期查询数据
//				+pub.yssGetTableName("Tb_Para_CashAccount")+//delete by songjie 2011.03.15 不以最大的启用日期查询数据
				+" select FSTARTDATE, FCashAccCode, FCashAccName from "//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+pub.yssGetTableName("Tb_Para_CashAccount")+
				" where FCheckState = 1"//edit by songjie 2011.03.15 不以最大的启用日期查询数据
				+") d on a.FINCASHACCOUNTCODE = d.fcashacccode" 
				
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) g " 
		        + " on a.FCreator = g.FUserCode "
		        
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) h " 
		        + " on a.FCheckUser = h.FUserCode " 
		        
		        + FilterSql()
		        
				+ " where " + buildFilterStr("a")
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
            /**shashijie 2012-7-18 STORY 2796 */
            String sVocStr = ""; //存储的是存款类型的词汇类型
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_CSH_FTakeType);
			/**end*/
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
            + "\r\f" + this.getListView1ShowCols()
            /**shashijie 2012-7-18 STORY 2796 */
			+ "\r\f"+"voc" + sVocStr
			/**end*/
            ;
        }
        catch(Exception e){
        	throw new YssException("获取本金提取数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	public String buildFilterStr(String prefix) throws YssException {
        String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if (this.FilterType!=null) {
    			PrincipalExtBean filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null) {
    				prefix="";
    			} else if (!prefix.trim().endsWith(".")) {
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getExtDate()).equalsIgnoreCase("9998-12-31")) {
    				alCon.add(prefix+"FExtDate = "+dbl.sqlDate(filter.getExtDate()));
    			}
    			
    			if(filter.getSavingNum() !=null && filter.getSavingNum().trim().length()>0) {
    				alCon.add(prefix+"FCONSAVINGNUM = "+dbl.sqlString(filter.getSavingNum()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0) {
    				alCon.add(prefix+"FPortCode = "+dbl.sqlString(filter.getPortCode()));
    			} 
    			
    			if(filter.getOutCashAccCode() !=null && filter.getOutCashAccCode().trim().length()>0) {
    				alCon.add(prefix+"FOUTCASHACCOUNTCODE = "+dbl.sqlString(filter.getOutCashAccCode()));
    			}  
    			
    			if (filter.getInCashAccCode() != null && filter.getInCashAccCode().trim().length()>0 ) { 
    				alCon.add(prefix+"FINCASHACCOUNTCODE = " +dbl.sqlString(filter.getInCashAccCode()));
                }
    			
    			if (filter.getOutMoney()>0) { 
    				alCon.add(prefix+"FOUTMONEY = " + filter.getOutMoney());
                }
    			
    			if(filter.getDesc() !=null && filter.getDesc().trim().length()>0) {
    				alCon.add(prefix+"FDesc = "+dbl.sqlString(filter.getDesc().trim()));
    			}   
    			
    			if (filter.getStrFAnalysisCode1()!=null && filter.getStrFAnalysisCode1().trim().length()>0) {
    				alCon.add(prefix+"FAnalysisCode1 = "+dbl.sqlString(filter.getStrFAnalysisCode1().trim()));
                }
    			
    			if (filter.getStrFAnalysisCode2()!=null && filter.getStrFAnalysisCode2().trim().length()>0) {
    				alCon.add(prefix+"FAnalysisCode2 = "+dbl.sqlString(filter.getStrFAnalysisCode2().trim()));
                }
    			
    			if (filter.getStrFAnalysisCode3()!=null && filter.getStrFAnalysisCode3().trim().length()>0) {
    				alCon.add(prefix+"FAnalysisCod3 = "+dbl.sqlString(filter.getStrFAnalysisCode3().trim()));
                }
    			/**shashijie 2012-7-18 STORY 2796 */
				if (filter.FTakeType != null && filter.FTakeType.trim().length() > 0) {
					alCon.add(prefix+"FTakeType = "+dbl.sqlString(filter.FTakeType.trim()));
				}
				/**end*/
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    } 
	
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	
    	this.num = rs.getString("Fnum");
    	this.savingNum = rs.getString("FCONSAVINGNUM");
        this.outCashAccCode = rs.getString("FoutCashAccountCode");
        this.outCashAccName = rs.getString("FoutCashAccName");
        this.inCashAccCode = rs.getString("FinCashAccountCode");
        this.inCashAccName = rs.getString("FinCashAccName");
        this.portCode = rs.getString("FportCode");
        this.portName = rs.getString("FportName");
        this.extDate = rs.getDate("FextDate");
        this.outMoney = rs.getDouble("FoutMoney");
        
        this.strFAnalysisCode1 = rs.getString("FAnalysisCode1");
        this.strFAnalysisName1 = rs.getString("FAnalysisName1");
        this.strFAnalysisCode2 = rs.getString("FAnalysisCode2");
        this.strFAnalysisName2 = rs.getString("FAnalysisName2");
        this.strFAnalysisCode3 = rs.getString("FAnalysisCode3");
        this.strFAnalysisName3 = rs.getString("FAnalysisName3");
        
        this.desc = rs.getString("FDesc");
        /**shashijie 2012-7-18 STORY 2796 */
		this.FTakeType = rs.getString("FTakeType");
		/**end*/
        super.setRecLog(rs);
    }
	
	/**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    public String FilterSql() throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(YssOperCons.YSS_KCLX_Cash);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
            	if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("001")) {
            		sResult = sResult +
						 " left join ( select FInvMgrCode, FInvMgrName as FAnalysisName" + i +
						 " from " + pub.yssGetTableName("tb_para_investmanager") +
						 " where FCheckState = 1 ) invmgr on a.FAnalysisCode" +
						 i + " = invmgr.FInvMgrCode ";
            	} else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
            			rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("002")) {
                    sResult = sResult +
                        " left join ( select FBrokerCode, FBrokerName as FAnalysisName" + i +
                        " from " + pub.yssGetTableName("tb_para_broker") +
                        " where FCheckState = 1 ) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode ";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join ( select FExchangeCode,FExchangeName as FAnalysisName" + i +
                        " from " + pub.yssGetTableName("tb_base_exchange") +
                        " where FCheckState = 1 ) e on a.FAnalysisCode" +
                        i + " = e.FExchangeCode " ;
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).equalsIgnoreCase("004")) {
                    sResult = sResult +                    
                    	" left join (select FCatCode,FCatName as FAnalysisName" + i +
                    	" from " + pub.yssGetTableName("Tb_Base_Category") +
                    	" where FCheckState = 1 ) category on a.FAnalysisCode" +
                        i + " = category.FCatCode ";
                } else {
                    sResult = sResult +
                        " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) tn" + i + " on a.FAnalysisCode" + i + " = tn" +
                        i + ".FAnalysisNull ";
                }
            }
        }
        
        dbl.closeResultSetFinal(rs);

        return sResult;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStrFAnalysisCode1() {
		return strFAnalysisCode1;
	}

	public void setStrFAnalysisCode1(String strFAnalysisCode1) {
		this.strFAnalysisCode1 = strFAnalysisCode1;
	}

	public String getStrFAnalysisName1() {
		return strFAnalysisName1;
	}

	public void setStrFAnalysisName1(String strFAnalysisName1) {
		this.strFAnalysisName1 = strFAnalysisName1;
	}

	public String getStrFAnalysisCode2() {
		return strFAnalysisCode2;
	}

	public void setStrFAnalysisCode2(String strFAnalysisCode2) {
		this.strFAnalysisCode2 = strFAnalysisCode2;
	}

	public String getStrFAnalysisName2() {
		return strFAnalysisName2;
	}

	public void setStrFAnalysisName2(String strFAnalysisName2) {
		this.strFAnalysisName2 = strFAnalysisName2;
	}

	public String getStrFAnalysisCode3() {
		return strFAnalysisCode3;
	}

	public void setStrFAnalysisCode3(String strFAnalysisCode3) {
		this.strFAnalysisCode3 = strFAnalysisCode3;
	}

	public String getStrFAnalysisName3() {
		return strFAnalysisName3;
	}

	public void setStrFAnalysisName3(String strFAnalysisName3) {
		this.strFAnalysisName3 = strFAnalysisName3;
	}

	/**返回 fTakeType 的值*/
	public String getFTakeType() {
		return FTakeType;
	}

	/**传入fTakeType 设置  fTakeType 的值*/
	public void setFTakeType(String fTakeType) {
		FTakeType = fTakeType;
	}
}
