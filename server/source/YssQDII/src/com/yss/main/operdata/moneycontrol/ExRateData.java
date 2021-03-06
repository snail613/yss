package com.yss.main.operdata.moneycontrol;

import java.sql.*;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: ExRateData </p>
 * <p>Description: 汇率数据 </p>
 * @author fangjiang
 * @date 20100728
 */
public class ExRateData extends BaseDataSettingBean implements
		IDataSetting {
	
	private ExRateData FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	private String multAuditString = ""; //批量处理数据
	
	private String FCuryCode = "";//外币代码
    private String FCuryName = "";//外币名称
    private String DCuryCode = "";//本币代码
    private String DCuryName = "";//本币名称
    private java.util.Date ExRateDate;//汇率日期
    private BigDecimal ExRate;//汇率
    private String Desc = "";//描述

    private String OldFCuryCode = "";//修改前的外币代码
    private String OldFCuryName = "";//修改前的外币名称
    private String OldDCuryCode = "";//修改前的本币代码
    private String OldDCuryName = "";//修改前的本币名称
    private java.util.Date OldExRateDate;//修改前的汇率日期
	private BigDecimal OldExRate;//修改前的汇率
	private String OldDesc = "";//修改前的描述

    private boolean BShow = false;
    
    public ExRateData getFilterType() {
		return FilterType;
	}

	public void setFilterType(ExRateData filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getFCuryCode() {
		return FCuryCode;
	}

	public void setFCuryCode(String curyCode) {
		FCuryCode = curyCode;
	}

	public String getFCuryName() {
		return FCuryName;
	}

	public void setFCuryName(String curyName) {
		FCuryName = curyName;
	}

	public String getDCuryCode() {
		return DCuryCode;
	}

	public void setDCuryCode(String curyCode) {
		DCuryCode = curyCode;
	}

	public String getDCuryName() {
		return DCuryName;
	}

	public void setDCuryName(String curyName) {
		DCuryName = curyName;
	}

	public java.util.Date getExRateDate() {
		return ExRateDate;
	}

	public void setExRateDate(java.util.Date exRateDate) {
		ExRateDate = exRateDate;
	}

	public BigDecimal getExRate() {
		return ExRate;
	}

	public void setExRate(BigDecimal exRate) {
		ExRate = exRate;
	}

	public String getOldFCuryCode() {
		return OldFCuryCode;
	}

	public void setOldFCuryCode(String oldFCuryCode) {
		OldFCuryCode = oldFCuryCode;
	}

	public String getOldFCuryName() {
		return OldFCuryName;
	}

	public void setOldFCuryName(String oldFCuryName) {
		OldFCuryName = oldFCuryName;
	}

	public String getOldDCuryCode() {
		return OldDCuryCode;
	}

	public void setOldDCuryCode(String oldDCuryCode) {
		OldDCuryCode = oldDCuryCode;
	}

	public String getOldDCuryName() {
		return OldDCuryName;
	}

	public void setOldDCuryName(String oldDCuryName) {
		OldDCuryName = oldDCuryName;
	}

	public java.util.Date getOldExRateDate() {
		return OldExRateDate;
	}

	public void setOldExRateDate(java.util.Date oldExRateDate) {
		OldExRateDate = oldExRateDate;
	}

	public BigDecimal getOldExRate() {
		return OldExRate;
	}

	public void setOldExRate(BigDecimal oldExRate) {
		OldExRate = oldExRate;
	}

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean show) {
		BShow = show;
	}
	
	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getOldDesc() {
		return OldDesc;
	}

	public void setOldDesc(String oldDesc) {
		OldDesc = oldDesc;
	}
	
	private ExRateData exRateDataBean = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	
	//新增一条数据
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
	            strSql = "insert into " + pub.yssGetTableName("TB_DATA_DivineExRate")
	            	+ " (FFCuryCODE, FFCuryName, FDCuryCODE, FDCuryName, FExRate, FExRateDate,"
	            	+ " FCheckState, FCreator, FCreateTime,FDesc) "
	            	+ " values ("
	            	+ dbl.sqlString(this.FCuryCode) + ","
	            	+ dbl.sqlString(this.FCuryName) + "," 
	            	+ dbl.sqlString(this.DCuryCode) + ","
	            	+ dbl.sqlString(this.DCuryName) + ","
	            	+ this.ExRate + ","
	            	+ dbl.sqlDate(this.ExRateDate) + "," 
	            	+ "0," 
	            	+ dbl.sqlString(this.creatorCode) + ","
	            	+ dbl.sqlString(this.creatorTime) + ","
	                + dbl.sqlString(this.Desc) + ")";
	
	            dbl.executeSql(strSql);
            
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增汇率信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {	
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("TB_DATA_DivineExRate"),
                "FFCuryCode,FDCuryCode,FExRateDate",
                this.FCuryCode+","+this.DCuryCode+","+format.format(this.ExRateDate),
                this.OldFCuryCode+","+this.OldDCuryCode+","+format.format(this.OldExRateDate));
	}
	
	//审核
	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					
                    strSql = "update " + pub.yssGetTableName("TB_DATA_DivineExRate")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = " 
                    	+ dbl.sqlString(this.checkUserCode)
                    	+ ", FCheckTime = "
                    	+ dbl.sqlString(this.checkTime)
                    	+ " where FFCuryCode = " + dbl.sqlString(this.FCuryCode)
                    	+ " and FDCuryCode = " + dbl.sqlString(this.DCuryCode)
                    	+ " and FExRateDate = " + dbl.sqlDate(this.ExRateDate);
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核汇率信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}
	
	//删除
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("TB_DATA_DivineExRate")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FFCuryCode = " + dbl.sqlString(this.FCuryCode)
            	+ " and FDCuryCode = " + dbl.sqlString(this.DCuryCode)
            	+ " and FExRateDate = " + dbl.sqlDate(this.ExRateDate);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除汇率信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
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
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_DivineExRate") 
	                	+ " where FFCuryCode = " + dbl.sqlString(this.FCuryCode)
                    	+ " and FDCuryCode = " + dbl.sqlString(this.DCuryCode)
                    	+ " and FExRateDate = " + dbl.sqlDate(this.ExRateDate);
                    
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除汇率信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("TB_DATA_DivineExRate")
            	+ " set FFCuryCode = " + dbl.sqlString(this.FCuryCode)
            	+ " , FFCuryName = " + dbl.sqlString(this.FCuryName)
            	+ " , FDCuryCode = " + dbl.sqlString(this.DCuryCode)
            	+ " , FDCuryName = " + dbl.sqlString(this.DCuryName)
            	+ " , FExRate = " + this.ExRate
            	+ " , FExRateDate = " + dbl.sqlDate(this.ExRateDate)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
            	+ " , FDesc = " + dbl.sqlString(this.Desc)
				+ " where FFCuryCode = " + dbl.sqlString(this.OldFCuryCode)
		        + " and FDCuryCode = " + dbl.sqlString(this.OldDCuryCode)
		        + " and FExRateDate = " + dbl.sqlDate(this.OldExRateDate);  
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改汇率信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditExRateSub")) { //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }
	
	public String auditMutli(String sMutilRowStr) throws YssException {
        Connection conn = null; //建立一个数据库连接
        String sqlStr = ""; //创建一个字符串
        PreparedStatement psmt1 = null;
        boolean bTrans = true; //建一个boolean变量，默认自动回滚
        ExRateData exRateData = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除汇率数据
            sqlStr = "update " + pub.yssGetTableName("TB_DATA_DivineExRate") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FFCuryCode = ? " + " and FDCuryCode = ? "
        	    + " and FExRateDate = ? " ; //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	exRateData = new ExRateData(); //new 一个pojo类
                    	exRateData.setYssPub(pub); //设置一些基础信息
                    	exRateData.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,exRateData.FCuryCode);
                    	psmt1.setString(2,exRateData.DCuryCode);
                    	psmt1.setDate(3,YssFun.toSqlDate(exRateData.ExRateDate));
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核汇率数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
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
		return "";
	}

	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.FCuryCode).append("\t");
        buf.append(this.FCuryName).append("\t");
        buf.append(this.DCuryCode).append("\t");
        buf.append(this.DCuryName).append("\t");
        buf.append(this.ExRate).append("\t");
        buf.append(format.format(this.ExRateDate)).append("\t");
        buf.append(this.Desc).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (exRateDataBean == null) {
			exRateDataBean = new ExRateData();
			exRateDataBean.setYssPub(pub);
        }
		String reqAry[] = null;
        String sTmpStr = "";
        
        String sMutiAudit = ""; //批量处理的数据
        try {
        	
        	if (sRowStr.trim().length() == 0) {
                return;
            }
        	
        	if (sRowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = sRowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                sRowStr = sRowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }
        	
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.DCuryCode = reqAry[0];
            this.DCuryName = reqAry[1];
            this.FCuryCode = reqAry[2];
            this.FCuryName = reqAry[3];
            this.ExRate = new BigDecimal(reqAry[4].trim().length()==0?"0":reqAry[4]); 
            this.ExRateDate = YssFun.parseDate(reqAry[5].trim().length()==0?"9998-12-31":reqAry[5]);
            
            this.OldDCuryCode = reqAry[6];
            this.OldDCuryName = reqAry[7];
            this.OldFCuryCode = reqAry[8];
            this.OldFCuryName = reqAry[9];
            this.OldExRate = new BigDecimal(reqAry[10].trim().length()==0?"0":reqAry[10]); 
            this.OldExRateDate = YssFun.parseDate(reqAry[11].trim().length()==0?"9998-12-31":reqAry[11]);
            
            this.checkStateId = YssFun.toInt(reqAry[12]);
            this.Desc = reqAry[13];
            this.OldDesc = reqAry[14];
            
            if (reqAry[15].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new ExRateData();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析汇率数据出错！", e);
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
        	
        	strSql = "select FFCuryCode, FFCuryName, FDCuryCode, FDCuryName, FExRate, FExRateDate, FCheckState, FCreator, b.FUserName as FCreatorName," 
        		+ " FCreateTime, FCheckUser, c.FUserName as FCheckUserName, FCheckTime,FDesc from "
        		+ pub.yssGetTableName("TB_DATA_DivineExRate")

        		+ " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b " 
                + " on a.FCheckUser = b.FUserCode " 

        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
                + " on a.FCheckUser = c.FUserCode " 
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
            
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
            + "\r\f" + this.getListView1ShowCols();
        }
        catch(Exception e){
        	throw new YssException("获取汇率数据出错！" + "\r\n" + e.getMessage(), e);
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
     * 生成筛选条件子句
     * @param prefix
     * @return
     * @throws YssException
     */
    public String buildFilterStr(String prefix) throws YssException {
        String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if (this.FilterType!=null)
    		{
    			ExRateData filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null)
    			{
    				prefix="";
    			} else if (!prefix.trim().endsWith("."))
    			{
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getExRateDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FExRateDate = "+dbl.sqlDate(filter.getExRateDate()));
    			}
    			
    			if(filter.getFCuryCode()!=null&&filter.getFCuryCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FFCuryCode in ("+dbl.sqlString(filter.getFCuryCode().trim())+")");
    			} 
    			
    			if(filter.getDCuryCode() !=null && filter.getDCuryCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FDCuryCode = "+dbl.sqlString(filter.getDCuryCode().trim()));
    			}  
    			
    			if (filter.ExRate != null && filter.ExRate.compareTo(new BigDecimal(0)) > 0) { 
    				alCon.add(prefix+"FExRate = " + filter.getExRate());
                }
    			
    			if(filter.getDesc() !=null && filter.getDesc().trim().length()>0)
    			{
    				alCon.add(prefix+"FDesc = "+dbl.sqlString(filter.getDesc().trim()));
    			}   
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    } 
    
    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	
    	this.FCuryCode = rs.getString("FFCuryCode");
        this.FCuryName = rs.getString("FFCuryName");
        this.DCuryCode = rs.getString("FDCuryCode");
        this.DCuryName = rs.getString("FDCuryName");
        this.ExRate = rs.getBigDecimal("FExRate");
        this.ExRateDate = rs.getDate("FExRateDate");
        this.Desc = rs.getString("FDesc");
        
        super.setRecLog(rs);
    }

}
