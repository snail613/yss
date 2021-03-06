package com.yss.main.parasetting;

import java.sql.*;
import java.math.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class CGTSetBean extends BaseDataSettingBean implements
		IDataSetting {

	private CGTSetBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	private String multAuditString = ""; //批量处理数据
	
	private String portCode = "";
    private String portName = "";
    private String secCode = "";
    private String secName = "";
    private String roundCode = "";
    private String roundName = "";
    private double rate;
    private String desc = "";

    private String oldPortCode = "";
    private String oldSecCode = "";

    private boolean BShow = false;
    
    private CGTSetBean CGTSetBeanBean = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	
	//add by zhouwei 20120409 证券品种
	private String categoryCode="";
	private String categoryName="";
	private String subCategoryCode="";
	private String subCategoryName="";
	private String oldCategoryCode="";
	private String oldSubCategoryCode="";
	//新增一条数据
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
	            strSql = "insert into " + pub.yssGetTableName("tb_data_cgt")
	            	+ " (FPORTCODE, FSECURITYCODE, FROUNDCODE, FRATE,FCatCode,FSubCatCode,"
	            	+ " FCheckState, FCreator, FCreateTime,FDesc) "
	            	+ " values ("
	            	+ dbl.sqlString(this.portCode) + ","
	            	+ dbl.sqlString(this.secCode) + "," 
	            	+ dbl.sqlString(this.roundCode) + ","
	            	+ this.rate + ","
	            	+dbl.sqlString(this.categoryCode)+","
	            	+dbl.sqlString(this.subCategoryCode)+","
	            	+(pub.getSysCheckState() ? "0" : "1")+","
	            	+ dbl.sqlString(this.creatorCode) + ","
	            	+ dbl.sqlString(this.creatorTime) + ","
	                + dbl.sqlString(this.desc) + ")";
	
	            dbl.executeSql(strSql);
            
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增资本利得税信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {	
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("tb_data_cgt"),
                "FPORTCODE,FSECURITYCODE,FCatCode,FSubCatCode",
                this.portCode+","+this.secCode+","+this.categoryCode+","+this.subCategoryCode,
                this.oldPortCode+","+this.oldSecCode+","+this.oldCategoryCode+","+this.oldSubCategoryCode);
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
			//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
			if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					
                    strSql = "update " + pub.yssGetTableName("tb_data_cgt")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = " 
                    	+ dbl.sqlString(this.checkUserCode)
                    	+ ", FCheckTime = "
                    	+ dbl.sqlString(this.checkTime)
                    	+ " where FPORTCODE = " + dbl.sqlString(this.portCode)
                    	+ " and FSECURITYCODE = " + dbl.sqlString(this.secCode)
                    	+ " and FCatCode="+dbl.sqlString(this.categoryCode)
                    	+ " and FSubCatCode="+dbl.sqlString(this.subCategoryCode);
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核资本利得税信息出错", e);
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
            
            strSql = "update " + pub.yssGetTableName("tb_data_cgt")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where FPORTCODE = " + dbl.sqlString(this.portCode)
            	+ " and FSECURITYCODE = " + dbl.sqlString(this.secCode)
            	+ " and FCatCode="+dbl.sqlString(this.categoryCode)
                + " and FSubCatCode="+dbl.sqlString(this.subCategoryCode);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除资本利得税信息出错", e);
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
        	//edit by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
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
                    strSql = "delete from " + pub.yssGetTableName("tb_data_cgt") 
	                	+ " where FPORTCODE = " + dbl.sqlString(this.portCode)
            	        + " and FSECURITYCODE = " + dbl.sqlString(this.secCode)
            	        + " and FCatCode="+dbl.sqlString(this.categoryCode)
                    	+ " and FSubCatCode="+dbl.sqlString(this.subCategoryCode);
                    
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除资本利得税信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("tb_data_cgt")
            	+ " set FPORTCODE = " + dbl.sqlString(this.portCode)
            	+ " , FSECURITYCODE = " + dbl.sqlString(this.secCode)
            	+ " , FROUNDCODE = " + dbl.sqlString(this.roundCode)
            	+ " , FRATE = " + this.rate
            	+ " ,FCatCode="+dbl.sqlString(this.categoryCode)
            	+ " ,FSubCatCode="+dbl.sqlString(this.subCategoryCode)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
            	+ " , FDesc = " + dbl.sqlString(this.desc)
            	//---edit by songjie 2011.12.12 BUG 2926 QDV4赢时胜(测试)2011年10月11日07_B start---//
            	//将portCode 改为 oldPortCode secCode 改为 oldSecCode
				+ " where FPORTCODE = " + dbl.sqlString(this.oldPortCode)
            	+ " and FSECURITYCODE = " + dbl.sqlString(this.oldSecCode)
            	+ " and FCatCode="+dbl.sqlString(this.oldCategoryCode)
            	+ " and FSubCatCode="+dbl.sqlString(this.oldSubCategoryCode);
                //---edit by songjie 2011.12.12 BUG 2926 QDV4赢时胜(测试)2011年10月11日07_B end---//
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改资本利得税信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditCGTSub")) { //判断是否要进行批量审核与反审核
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
        CGTSetBean CGTSetBean = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除资本利得税数据
            sqlStr = "update " + pub.yssGetTableName("tb_data_cgt") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FPORTCODE = ? " + " and FSECURITYCODE = ?  and FCatCode=? and FSubCatCode=?"; //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句

            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	CGTSetBean = new CGTSetBean(); //new 一个pojo类
                    	CGTSetBean.setYssPub(pub); //设置一些基础信息
                    	CGTSetBean.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,CGTSetBean.portCode);
                    	psmt1.setString(2,CGTSetBean.secCode);
                    	psmt1.setString(3, CGTSetBean.categoryCode);
                    	psmt1.setString(4, CGTSetBean.subCategoryCode);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核资本利得税数据出错!");
        } finally{
        	dbl.closeStatementFinal(psmt1);
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
		return "";
	}

	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.portCode).append("\t");
        buf.append(this.portName).append("\t");
        buf.append(this.secCode).append("\t");
        buf.append(this.secName).append("\t");
        buf.append(this.roundCode).append("\t");
        buf.append(this.roundName).append("\t");
        buf.append(this.rate).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.categoryCode).append("\t");
        buf.append(this.categoryName).append("\t");
        buf.append(this.subCategoryCode).append("\t");
        buf.append(this.subCategoryName).append("\t");
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (CGTSetBeanBean == null) {
			CGTSetBeanBean = new CGTSetBean();
			CGTSetBeanBean.setYssPub(pub);
        }
		String reqAry[] = null;
        String sTmpStr = "";
        
        String sMutiAudit = ""; //批量处理的数据
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
            this.portCode = reqAry[0];
            this.secCode = reqAry[1];
            this.roundCode = reqAry[2];
            this.rate = Double.parseDouble(reqAry[3].trim().length()==0?"0":reqAry[3]); 
            if (reqAry[4] != null ){
            	if (reqAry[4].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[4].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[4];
            	}
            }
            this.checkStateId = YssFun.toInt(reqAry[5]);
            this.oldPortCode = reqAry[6];
            this.oldSecCode = reqAry[7];
            
            if (reqAry[8].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            }
            //add by zhouwei 20120409
            this.categoryCode=reqAry[9];
            this.oldCategoryCode=reqAry[10];
            this.subCategoryCode=reqAry[11];
            this.oldSubCategoryCode=reqAry[12];
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new CGTSetBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析资本利得税数据出错！", e);
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
        	
        	strSql = " select a.FPortCode as FPortCode, d.FPortName as FPortName, " 
        			 + " a.FSecurityCode as FSecurityCode, e.FSecurityName as FSecurityName, " 
        			 + " a.FRate as FRate, a.FRoundCode as FRoundCode, f.FRoundName as FRoundName," 
        		     + " FCheckState, FCreator, b.FUserName as FCreatorName," 
        		     + " FCreateTime, FCheckUser, c.FUserName as FCheckUserName, FCheckTime,FDesc,"
        		     +"a.FCATCODE,a.FSUBCATCODE,cg.FCatName,scg.FSubCatName from "//add by zhouwei 20120409 证券品种
        		     + pub.yssGetTableName("tb_data_cgt")
        		     + " a left join (select FUserCode,FUserName from Tb_Sys_UserList) b " 
                     + " on a.FCheckUser = b.FUserCode " 
        		     + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
                     + " on a.FCheckUser = c.FUserCode " 
        		     + " left join (select FPortCode, FPortName from " + pub.yssGetTableName("tb_para_portfolio")
        		     + " where FCheckState =1) d on a.FPortCode = d.FPortCode "
        		     + " left join (select FSecurityCode, FSecurityName from " + pub.yssGetTableName("tb_para_security")
        		     + " where FCheckState =1) e on a.FSecurityCode = e.FSecurityCode "
        		     + " left join (select FRoundCode, FRoundName from " + pub.yssGetTableName("Tb_Para_Rounding")
        		     + " where FCheckState =1) f on a.FRoundCode = f.FRoundCode "     
        		     //add by zhouwei 20120409
        		     + " left join (select FCatCode,FCatName from Tb_Base_Category where fcheckstate=1) cg on a.FCatCode=cg.FCatCode"
        		     + " left join (select FSubCatCode,FSubCatName from Tb_Base_SubCategory where fcheckstate=1) scg on a.FSubCatCode=scg.FSubCatCode"
        		     + " where " + buildFilterStr("a")
        		     + " order by a.FCheckState, a.FCreateTime desc ";
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
        	throw new YssException("获取资本利得税数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
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
    			CGTSetBean filter = this.FilterType;
    			
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
    			
    			if(filter.getPortCode()!= null && filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPortCode in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getSecCode() !=null && filter.getSecCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FSecurityCode = "+dbl.sqlString(filter.getSecCode().trim()));
    			}  
    			
    			if(filter.getRoundCode() !=null && filter.getRoundCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FRoundCode = "+dbl.sqlString(filter.getRoundCode().trim()));
    			}
    			
    			if (filter.getRate() > 0 ) { 
    				alCon.add(prefix+"FRate = " + filter.getRate());
                }
    			
    			if(filter.getDesc() !=null && filter.getDesc().trim().length()>0)
    			{
    				alCon.add(prefix+"FDesc = "+dbl.sqlString(filter.getDesc().trim()));
    			}
    			//add by zhouwei 20120409
    			if(filter.getCategoryCode()!=null && filter.getCategoryCode().trim().length()>0){
    				alCon.add(prefix+"FCatCode="+dbl.sqlString(filter.getCategoryCode()));
    			}
    			if(filter.getSubCategoryCode()!=null && filter.getSubCategoryCode().trim().length()>0){
    				alCon.add(prefix+"FSubCatCode="+dbl.sqlString(filter.getSubCategoryCode()));
    			}
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    } 
    
    public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getSubCategoryCode() {
		return subCategoryCode;
	}

	public void setSubCategoryCode(String subCategoryCode) {
		this.subCategoryCode = subCategoryCode;
	}

	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
    	
    	this.portCode = rs.getString("FPortCode");
    	this.portName = rs.getString("FPortName");
    	this.secCode = rs.getString("FSecurityCode");
    	this.secName = rs.getString("FSecurityName");
        this.roundCode = rs.getString("FRoundCode");
        this.roundName = rs.getString("FRoundName");
        this.rate = rs.getDouble("FRate");
        this.desc = rs.getString("FDesc");
        this.categoryCode=rs.getString("FCATCODE");
        this.categoryName=rs.getString("FCatName");
        this.subCategoryCode=rs.getString("FSubCatCode");
        this.subCategoryName=rs.getString("FSubCatName");
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

	public CGTSetBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(CGTSetBean filterType) {
		FilterType = filterType;
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

	public String getMultAuditString() {
		return multAuditString;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
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

	public String getSecCode() {
		return secCode;
	}

	public void setSecCode(String secCode) {
		this.secCode = secCode;
	}

	public String getSecName() {
		return secName;
	}

	public void setSecName(String secName) {
		this.secName = secName;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getRoundName() {
		return roundName;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOldPortCode() {
		return oldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		this.oldPortCode = oldPortCode;
	}

	public String getOldSecCode() {
		return oldSecCode;
	}

	public void setOldSecCode(String oldSecCode) {
		this.oldSecCode = oldSecCode;
	}

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean bShow) {
		BShow = bShow;
	}
    
}
