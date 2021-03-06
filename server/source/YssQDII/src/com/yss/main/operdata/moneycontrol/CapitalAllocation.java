package com.yss.main.operdata.moneycontrol;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.NewSharePriceBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * <p>Title: capitalAllocation </p>
 * <p>Description: 资金划拨 </p>
 * @author guojianhua 
 * @date 20100827
 */
public class CapitalAllocation extends BaseDataSettingBean implements IDataSetting {
	private CapitalAllocation FilterType=null;
	private String Fnum= "";                    
    private String BargainDate =null;
    private String SettleDate = null;
    private String BeginDate = null;
    private String EndDate = null;
    private double AmountAllocated ;     
    private String PortCode = "";
    private String PortName = "";                 
    private String Desc = "";
    private String strIsOnlyColumns = "0";
    private CapitalAllocation filterType;
    private String sRecycled = "";  //保存未解析前的字符串
    private String multAuditString = ""; //批量处理数据
   
   
	private boolean Show=false;
	
	private CapitalAllocation capitalAllocation=null;
	private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
	

	
	public double getAmountAllocated() {
		return AmountAllocated;
	}
	public void setAmountAllocated(double amountAllocated) {
		AmountAllocated = amountAllocated;
	}
	public String getBargainDate() {
		return BargainDate;
	}
	public void setBargainDate(String bargainDate) {
		BargainDate = bargainDate;
	}
	public String getBeginDate() {
		return BeginDate;
	}
	public void setBeginDate(String beginDate) {
		BeginDate = beginDate;
	}
	public CapitalAllocation getCapitalAllocation() {
		return capitalAllocation;
	}
	public void setCapitalAllocation(CapitalAllocation capitalAllocation) {
		this.capitalAllocation = capitalAllocation;
	}
	public String getDesc() {
		return Desc;
	}
	public void setDesc(String desc) {
		Desc = desc;
	}
	public String getEndDate() {
		return EndDate;
	}
	public void setEndDate(String endDate) {
		EndDate = endDate;
	}
	public CapitalAllocation getFilterType() {
		return filterType;
	}
	public void setFilterType(CapitalAllocation filterType) {
		this.filterType = filterType;
	}
	public String getFnum() {
		return Fnum;
	}
	public void setFnum(String fnum) {
		Fnum = fnum;
	}
	public SimpleDateFormat getFormat() {
		return format;
	}
	public void setFormat(SimpleDateFormat format) {
		this.format = format;
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
	public String getSettleDate() {
		return SettleDate;
	}
	public void setSettleDate(String settleDate) {
		SettleDate = settleDate;
	}
	public boolean isShow() {
		return Show;
	}
	public void setShow(boolean show) {
		Show = show;
	}
	public String getSRecycled() {
		return sRecycled;
	}
	public void setSRecycled(String recycled) {
		sRecycled = recycled;
	}
	public String getStrIsOnlyColumns() {
		return strIsOnlyColumns;
	}
	public void setStrIsOnlyColumns(String strIsOnlyColumns) {
		this.strIsOnlyColumns = strIsOnlyColumns;
	}
	public String addSetting() throws YssException {
		String strSql="";
		boolean bTrans=false;//代表事务是否开始
		String nowDate="";
		Connection conn = dbl.loadConnection();
		try {
            conn.setAutoCommit(false);
            bTrans = true;
            //nowDate=YssFun.formatDate(new java.util.Date());
            nowDate=YssFun.formatDate(new java.util.Date(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
            this.Fnum = "T" + nowDate +"00000"+
            dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_CapitalAllocation"),
                                   dbl.sqlRight("FNum", 6), "000001",
                                   " where FNum like 'T"
                                   + nowDate + "%'", 1);
            strSql =
                " insert into " + pub.yssGetTableName("tb_cash_capitalallocation") + "(FNUM,FPORTCODE, FAmountAllocated, FBargainDate,FSettleDate," +
                "  FDESC,FCreator,FCheckState,FCreateTime) " + 
                " values(" +
                dbl.sqlString(this.Fnum) + "," +
                dbl.sqlString(this.PortCode)+","+
                (this.AmountAllocated) + "," +
                dbl.sqlDate(this.BargainDate) + "," +
                dbl.sqlDate(this.SettleDate) + "," +
                dbl.sqlString(this.Desc) + "," +
                dbl.sqlString(this.creatorCode) +","+
               (pub.getSysCheckState() ? "0" : "1") + 
                ", " + dbl.sqlString(this.creatorTime)+")";
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        return "";        
	            
            
        }
		catch (Exception e) {
            throw new YssException("新增资金划拨数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	/*
	 * 检查是否有主键冲突
	 */
	public void checkInput(byte btOper) throws YssException {
		
                
	}
	/* 
	 * 审核操作
	 */
	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try
		{
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase("")))// 判断传来的内容是否为空
			{
				arrData = sRecycled.split("\r\n"); 
				for(int i=0;i<arrData.length;i++)
				{
					if(arrData[i].length()==0)
					{
						continue;
					}
					this.parseRowStr(arrData[i]);
					strSql="update " +pub.yssGetTableName("Tb_Cash_CapitalAllocation")+
							" set fcheckstate = case fcheckstate when 0 then 1 else 0 end"+
							",fcheckuser = "+
							dbl.sqlString(pub.getUserCode())+
							", FCheckTime = '"+ 
							YssFun.formatDatetime(new java.util.Date()) + "'" +
							" where FNum = " + dbl.sqlString(this.Fnum)
							;
					dbl.executeSql(strSql); 
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		}
		catch (Exception e) {
			throw new YssException("审核资金划拨数据出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}

	}
	//删除操作,将数据放到回收站
	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try
        {
        	 conn.setAutoCommit(false);
             bTrans = true;
             strSql="update " + pub.yssGetTableName("Tb_Cash_CapitalAllocation")
             + " set FCheckState = 2 " 
             + ", FCheckUser = null " 
             + ", FCheckTime = null "
             +" where Fnum = " + dbl.sqlString(this.Fnum);
             
             dbl.executeSql(strSql);
             conn.commit();
             bTrans = false;
             conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("删除资金划拨数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}
	//清空回收站数据
	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try
        {
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
					strSql = "delete from "
							+ pub.yssGetTableName("Tb_Cash_CapitalAllocation")
							+ " where FNum = " + dbl.sqlString(this.Fnum);
					// 执行sql语句
					dbl.executeSql(strSql);
				}
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        	
        }
        catch (Exception e) {
            throw new YssException("清除资金划拨数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }	

	}
	//修改数据
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try
        {
        	 strSql = "update " + pub.yssGetTableName("tb_cash_capitalallocation") +
             " set FPORTCODE = " + dbl.sqlString(this.PortCode) +
             ",FAMOUNTALLOCATED = " + this.AmountAllocated +
             ",FSETTLEDATE = "  + dbl.sqlDate(this.SettleDate) +
             ",FBARGAINDATE = " + dbl.sqlDate(this.BargainDate) + 
             ",FDESC = " + dbl.sqlString(this.Desc) +
             ",FCheckState = " +this.checkStateId + ", FCheckUser = " +dbl.sqlString(pub.getUserCode()) +
             ", FCheckTime = '" +YssFun.formatDatetime(new java.util.Date()) + "'" +
             " where FNum = " +dbl.sqlString(this.Fnum) ;
            
        	dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        }
        catch (Exception e) {
            throw new YssException("修改资金划拨数据出错", e);
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

	public String saveMutliSetting(String mutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}
	/*
	 * 拼接字符串
	 */
	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
		buf.append(this.Fnum).append("\t");
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.BargainDate).append("\t");
        buf.append(this.SettleDate).append("\t");
        buf.append(YssFun.formatNumber(this.AmountAllocated,"#,##0.####")).append("\t");
        buf.append(this.Desc).append("\t");
        buf.append(super.buildRecLog());
		return buf.toString();
	}


	/*
	 * 解析前台字符串
	 */
	public void parseRowStr(String rowStr) throws YssException {
		if (capitalAllocation == null) {
			capitalAllocation = new CapitalAllocation();
			capitalAllocation.setYssPub(pub);
			}
		String reqAry[] = null;
        String sTmpStr = "";
        String sMutiAudit = ""; //批量处理的数据
        try
        {
        	if (rowStr.trim().length() == 0) {
                return;
            }
        	if (rowStr.indexOf("\f\n\f\n\f\n") >= 0) {
                sMutiAudit = rowStr.split("\f\n\f\n\f\n")[1];  //得到的是从前台传来需要审核与反审核的批量数据
                multAuditString = sMutiAudit;                   //保存在全局变量中
                rowStr = rowStr.split("\f\n\f\n\f\n")[0];     //前台传来的要更新的一些数据
            }
        	if (rowStr.indexOf("\r\t") >= 0) {
                sTmpStr = rowStr.split("\r\t")[0];
            } else {
                sTmpStr = rowStr;
            }
        	sRecycled = rowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
//            this.BargainDate = YssFun.parseDate(reqAry[0], "yyyy-MM-dd");
//            this.SettleDate= YssFun.parseDate(reqAry[1], "yyyy-MM-dd");
            this.BargainDate = reqAry[0];
            this.SettleDate= reqAry[1];
            this.AmountAllocated = YssFun.toDouble(reqAry[2]);
//            this.BeginDate=YssFun.parseDate(reqAry[3],"yyyy-MM-dd");
//            this.EndDate=YssFun.parseDate(reqAry[4],"yyyy-MM-dd");
            this.BeginDate=reqAry[3];
            this.EndDate=reqAry[4];
            this.checkStateId=YssFun.toInt(reqAry[5]);
            this.Fnum=reqAry[6];
            this.PortCode=reqAry[7];
            this.Desc = reqAry[8].replaceAll("【Enter】", "\r\n");
            this.strIsOnlyColumns = reqAry[9];
            if (reqAry[10].equalsIgnoreCase("true")) {
                this.Show = true;
            } else {
                this.Show = false;
            } 
            super.parseRecLog();
            if (rowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new CapitalAllocation();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(rowStr.split("\r\t")[1]);    
            }
            
        }
        catch (Exception e) {
            throw new YssException("解析资金划拨数据出错！", e);
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
        try
        {
        	sHeader = this.getListView1Headers();
        	strSql="select a.fnum as Fnum," +
			"a.fportcode as FPortCode," +
			"d.fportname as FPortName," +
			"b.fusername as FCreatorName," +
			"c.fusername as FCheckUserName,"+
			"a.famountAllocated as FAmountAllocated," +
			"a.fbargaindate as FBargainDate," +
			"a.fsettledate as FSettleDate," +
			"a.fdesc as FDesc," +
			"a.fcheckstate as FCheckState, " +
			"a.fcreator as FCreator,"+
			"a.fchecktime as FCheckTime," +
			"a.fcreatetime as FCreateTime, " +
			"a.fcheckuser as FCheckUser from "
		+ pub.yssGetTableName("TB_CASH_CAPITALALLOCATION")+
		//
		" a"+
		" left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "+
        " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "+
		" left join (select fportcode, FPORTNAME from "
				+pub.yssGetTableName("tb_Para_Portfolio")+
				" where FCheckState = 1) d"+" on a.fportcode=d.fportcode "
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
        	throw new YssException("获取资金划拨数据出错！" + "\r\n" + e.getMessage(), e);
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
	/*
	 * 生成筛选条件子句
	 */
	public String buildFilterStr(String prefix) throws YssException 
	{
		String str="";
    	
    	try {
 			ArrayList alCon=new ArrayList();
			
			alCon.add(" 1=1 ");
			
    		if(this.FilterType!=null)
    		{
    			CapitalAllocation filter = this.FilterType;
    			
    			if (filter.isShow() == false) {
    				alCon.add(" 1=2 ");
                }
    			
    			if(prefix==null)
    			{
    				prefix="";
    			}
    			else if(!prefix.trim().endsWith("."))
    			{
    				prefix+=".";
    			}
    			
    			if(!YssFun.formatDate(filter.getBargainDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FBARGAINDATE = "+dbl.sqlDate(filter.getBargainDate()));
    			}
    			if(!YssFun.formatDate(filter.getSettleDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FSETTLEDATE = "+dbl.sqlDate(filter.getSettleDate()));
    			}
    			
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPORTCODE in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getDesc()!= null&& filter.getDesc().trim().length()>0)
    			{
    				alCon.add(prefix+"FDESC = "+dbl.sqlString(filter.getDesc().trim()));
    			}
    			
    			if(filter.getAmountAllocated() > 0 )
    			{
    				alCon.add(prefix+"FAMOUNTALLOCATED= " + filter.getAmountAllocated());
    			}
    			if(filter.getDesc() != null && filter.getDesc().trim().length()!=0)
    			{
    				alCon.add(prefix + "FDesc = "
							+ dbl.sqlString(filter.getDesc().trim()));
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
		this.Fnum=rs.getString("FNum");
    	this.PortCode = rs.getString("FPortCode");
        this.PortName = rs.getString("FPortName");
        this.AmountAllocated=rs.getDouble("FAmountAllocated");
//        this.BargainDate=rs.getDate("FBargainDate");
//        this.SettleDate =rs.getDate("FSettleDate");
        this.BargainDate=rs.getString("FBargainDate");
        this.SettleDate =rs.getString("FSettleDate");
        this.Desc=rs.getString("FDesc");
        super.setRecLog(rs);
    }
	public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            //批量审核/反审核/删除
            if (sType.equalsIgnoreCase("multauditCapitalAllocation")) { //判断是否要进行批量审核与反审核
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
        CapitalAllocation tA = null; //创建一个pojo类
        String[] multAudit = null; //建一个字符串数组

        try {
            conn = dbl.loadConnection(); //和数据库进行连接
            //审核、反审核、删除汇率数据
            sqlStr = "update " + pub.yssGetTableName("Tb_Cash_CapitalAllocation") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = ? " ;
        	     //更新数据库审核与未审核的SQL语句
            
            psmt1 = conn.prepareStatement(sqlStr); //执行SQL语句


            if (multAuditString.length() > 0) {
                multAudit = sMutilRowStr.split("\f\f\f\f"); //拆分从前台传来的listview里面的条目
                if (multAudit.length > 0) { //判断传来的审核与反审核条目数量可大于0
                    for (int i = 0; i < multAudit.length; i++) { //循环遍历这些条目
                    	capitalAllocation = new CapitalAllocation(); //new 一个pojo类
                    	capitalAllocation.setYssPub(pub); //设置一些基础信息
                    	capitalAllocation.parseRowStr(multAudit[i]); //解析前台传来的单个条目信息
                    	psmt1.setString(1,capitalAllocation.Fnum);
                        psmt1.addBatch(); 
                    }
                }
                conn.setAutoCommit(false); //设置不自动回滚，这样才能开启事物
                psmt1.executeBatch();
                conn.commit(); //提交事物
                bTrans = false;
            }
        } catch (Exception e) {
            throw new YssException("批量审核资金划拨数据出错!");
        } finally
        {
        	dbl.closeStatementFinal(psmt1);
        }
        return "";
    }

}
