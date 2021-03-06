package com.yss.main.operdata.moneycontrol;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.ExchangeRateBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * <p>Title: BalanceBean </p>
 * <p>Description: 余额数据 </p>
 * @author yangheng
 * @date 20100908
 */
public class BalanceBean extends BaseDataSettingBean implements IDataSetting {
	
	private BalanceBean FilterType=null;
	private String sRecycled = ""; //保存未解析前的字符串
	private String multAuditString = ""; //批量处理数据
	
	private String PortCode="";   //组合代码
	private String PortName="";   //组合名字
	private double BalanceMoney;  //余额
	private java.util.Date BargainDate;//成交日期
	
	private String OldPortCode="";   //旧的组合代码
	private String OldPortName="";   //旧的组合名字
	private java.util.Date OldBargainDate;//旧的成交日期
	
	private boolean Show = false;
	
	public BalanceBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(BalanceBean filterType) {
		FilterType = filterType;
	}

	public String getSRecycled() {
		return sRecycled;
	}

	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}

	public String getMultAuditString() {
		return multAuditString;
	}

	public void setMultAuditString(String multAuditString) {
		this.multAuditString = multAuditString;
	}

	public String getPortCode() {
		return PortCode;
	}

	public void setPortCode(String portCode) {
		PortCode = portCode;
	}

	public String getPortName() {
		return PortName;
	}

	public void setPortName(String portName) {
		PortName = portName;
	}

	public double getBalanceMoney() {
		return BalanceMoney;
	}

	public void setBalanceMoney(double balanceMoney) {
		BalanceMoney = balanceMoney;
	}

	public java.util.Date getBargainDate() {
		return BargainDate;
	}

	public void setBargainDate(java.util.Date bargainDate) {
		BargainDate = bargainDate;
	}

	public String getOldPortCode() {
		return OldPortCode;
	}

	public void setOldPortCode(String oldPortCode) {
		OldPortCode = oldPortCode;
	}

	public String getOldPortName() {
		return OldPortName;
	}

	public void setOldPortName(String oldPortName) {
		OldPortName = oldPortName;
	}

	public java.util.Date getOldBargainDate() {
		return OldBargainDate;
	}

	public void setOldBargainDate(java.util.Date oldBargainDate) {
		OldBargainDate = oldBargainDate;
	}

	public boolean isShow() {
		return Show;
	}

	public void setShow(boolean show) {
		Show = show;
	}
	
	private BalanceBean balanceBean=null;
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	
	//新增一条数据
	public String addSetting() throws YssException {
		String strSql="";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
	            strSql = "insert into " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
	            	+ " (FBargainDate,FPortCode,FBanlance,"
	            	+ " FCheckState, FCreator, FCreateTime) "
	            	+ " values ("
	            	+ dbl.sqlDate(this.BargainDate) + ","
	            	+ dbl.sqlString(this.PortCode) + "," 
	            	+ this.BalanceMoney + "," 
	            	+ "0," 
	            	+ dbl.sqlString(this.creatorCode) + ","
	            	+ dbl.sqlString(this.creatorTime) + ")"
	                ;
	
	            dbl.executeSql(strSql);
            
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增余额信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	//检查是否有主键冲突
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE"),
                "FPortCode,FBargainDate",
                this.PortCode+","+format.format(this.BargainDate),
                this.OldPortCode+","+format.format(this.OldBargainDate));

	}
	//审核操作
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
					
                    strSql = "update " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = " 
                    	+ dbl.sqlString(this.checkUserCode)
                    	+ ", FCheckTime = "
                    	+ dbl.sqlString(this.checkTime)
                    	+ " where FPortCode = " + dbl.sqlString(this.PortCode)
                    	+ " and FBargainDate = " + dbl.sqlDate(this.BargainDate);
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核余额信息出错", e);
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
            
            strSql = "update " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
            	+ " where FPortCode = " + dbl.sqlString(this.PortCode)
            	+ " and FBargainDate = " + dbl.sqlDate(this.BargainDate);
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除余额信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}
	//清除操作
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
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE") 
            	+ " where FPortCode = " + dbl.sqlString(this.PortCode)
            	+ " and FBargainDate = " + dbl.sqlDate(this.BargainDate);
                    
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除余额信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
            	+ " set FPortCode = " + dbl.sqlString(this.PortCode)
            	+ " , FBanlance = " + this.BalanceMoney
            	+ " , FBargainDate = " + dbl.sqlDate(this.BargainDate)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
            	+ " where FPortCode = " + dbl.sqlString(this.OldPortCode)
            	+ " and FBargainDate = " + dbl.sqlDate(this.OldBargainDate);  
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改余额信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	public String getOperValue(String type) throws YssException {
		String sResult = "";
        try {
            //批量审核/反审核/删除
            if (type.equalsIgnoreCase("multauditBalanceSub")) { //判断是否要进行批量审核与反审核
                if (multAuditString.length() > 0) { //判断批量审核与反审核的内容是否为空
                    return this.auditMutli(this.multAuditString); //执行批量审核/反审核
                }
            }
            if(type.equalsIgnoreCase("searchBalance"))
            {
            	return this.getRecentDay();
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
            //审核、反审核、删除余额数据
            sqlStr = "update " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FPortCode = ? " 
        	    + " and FBargainDate = ? " ; //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	balanceBean = new BalanceBean(); //new 一个pojo类
                    	balanceBean.setYssPub(pub); //设置一些基础信息
                    	balanceBean.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,balanceBean.PortCode);
                    	psmt1.setDate(2,YssFun.toSqlDate(balanceBean.BargainDate));
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核余额数据出错!");
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

	public String saveMutliSetting(String mutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		BalanceBean befEditBean = new BalanceBean();
        String strSql = "";
        ResultSet rs = null;
        try {
        	strSql = "select a.FPortCode, k.FPortName, FBanlance, FBargainDate, FCheckState, FCreator, b.FUserName as FCreatorName," 
        		+ " FCreateTime, FCheckUser, c.FUserName as FCheckUserName, FCheckTime from "
        		+ pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
        		+
        		" a left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
//        		+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16 不以最大的启用日期查询数据
        		+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
        		+pub.yssGetTableName("tb_Para_Portfolio")+
        		" where fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
        		+") k "
        		+"on a.fportcode = k.fportcode "
        		+ "  left join (select FUserCode,FUserName from Tb_Sys_UserList) b " 
                + " on a.FCreator = b.FUserCode " 

        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
                + " on a.FCheckUser = c.FUserCode " 
                + " where a.FPortCode = " + dbl.sqlString(this.OldPortCode)
            	+ " and a.FBargainDate = " + dbl.sqlDate(this.OldBargainDate);
        	rs = dbl.openResultSet(strSql);
        	while(rs.next())
        	{
        		befEditBean.setResultSetAttr(rs);
        	}
        	return befEditBean.buildRowStr();
        	
            
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
	}
	//拼接字符串
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(format.format(this.BargainDate)).append("\t");
        buf.append(YssFun.formatNumber(this.BalanceMoney,"#,##0.##")).append("\t");
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	
	//解析字符串
	public void parseRowStr(String sRowStr) throws YssException {
		if (balanceBean == null) {
			balanceBean = new BalanceBean();
			balanceBean.setYssPub(pub);
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
            this.PortCode = reqAry[0];
            this.PortName = reqAry[1];
            this.BargainDate = YssFun.parseDate(reqAry[2].trim().length()==0?"9998-12-31":reqAry[2]);
            this.BalanceMoney = Double.parseDouble(reqAry[3].trim().length()==0?"0":reqAry[3]);
            
            this.OldPortCode = reqAry[4];
            this.OldPortName = reqAry[5];
            this.OldBargainDate = YssFun.parseDate(reqAry[6].trim().length()==0?"9998-12-31":reqAry[6]);
            this.checkStateId = YssFun.toInt(reqAry[7]);
            
            if (reqAry[8].equalsIgnoreCase("true")) {
                this.Show = true;
            } else {
                this.Show = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new BalanceBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析余额数据出错！", e);
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
        	
        	strSql = "select a.FPortCode, k.FPortName, FBanlance, FBargainDate, FCheckState, FCreator, b.FUserName as FCreatorName," 
        		+ " FCreateTime, FCheckUser, c.FUserName as FCheckUserName, FCheckTime from "
        		+ pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
        		+
        		" a left join (" //edit by songjie 2011.03.16不以最大的启用日期查询数据
//        		+pub.yssGetTableName("tb_Para_Portfolio")+//delete by songjie 2011.03.16不以最大的启用日期查询数据
        		+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		+pub.yssGetTableName("tb_Para_Portfolio")+
        		" where fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		+") k "
        		+"on a.fportcode = k.fportcode "
        		+ "  left join (select FUserCode,FUserName from Tb_Sys_UserList) b " 
                + " on a.FCreator = b.FUserCode " 

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
        	throw new YssException("获取余额数据出错！" + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	//向数据库查询一个组合最近一天的余额
	public String getRecentDay() throws YssException
	{	
		String strSql = ""; // 定义一个存放sql语句的字符串
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
		try
		{
			sHeader = this.getListView1Headers();
		strSql = "select a.FPortCode, k.FPortName, a.FBanlance, h.FBargainDate, FCheckState, FCreator, b.FUserName as FCreatorName," 
    		+ " FCreateTime, FCheckUser, c.FUserName as FCheckUserName, FCheckTime from "
    		+ pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
    		+" a right join (select max(fbargaindate) as fbargaindate from " + pub.yssGetTableName("TB_DATA_DIVINESKIWBALANCE")
    		+" where fportcode in "+dbl.sqlString(this.FilterType.getPortCode().trim())
    		+" ) h on  a.Fbargaindate = h.fbargaindate"
    		+"  left join ("//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
    		//delete by songjie 2011.03.16 不以最大的启用日期查询数据 
//    		+pub.yssGetTableName("tb_Para_Portfolio")+
    		+" select FSTARTDATE, fportcode, FPORTNAME from "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
    		+pub.yssGetTableName("tb_Para_Portfolio")+
    		" where fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据 
    		+") k "
    		+"on a.fportcode = k.fportcode "
    		+ "  left join (select FUserCode,FUserName from Tb_Sys_UserList) b " 
            + " on a.FCreator = b.FUserCode " 

    		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
            + " on a.FCheckUser = c.FUserCode " 
    		+ " where " + buildFilterStr("a")
    		+"and a.fcheckstate=1"
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
        	throw new YssException("获取最近余额数据出错！" + "\r\n" + e.getMessage(), e);
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
    			BalanceBean filter = this.FilterType;
    			
    			if (filter.isShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if (prefix==null)
    			{
    				prefix="";
    			} else if (!prefix.trim().endsWith("."))
    			{
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getBargainDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FBargainDate = "+dbl.sqlDate(filter.getBargainDate()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPortCode in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			  
    			
    			if (filter.getBalanceMoney()  > 0) { 
    				alCon.add(prefix+"FBanlance = " + filter.getBalanceMoney());
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
    	
    	this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.BalanceMoney = rs.getDouble("FBanlance");
        this.BargainDate = rs.getDate("FBargainDate");
        super.setRecLog(rs);
    }

}
