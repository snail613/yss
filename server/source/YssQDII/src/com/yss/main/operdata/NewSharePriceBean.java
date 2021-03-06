package com.yss.main.operdata;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

/**
 * <p>Title: NewSharePriceBean </p>
 * <p>Description: 新股价格数据 </p>
 * @author yanghaiming
 * @date 20100325
 */
public class NewSharePriceBean extends BaseDataSettingBean implements
		IDataSetting {
	private NewSharePriceBean FilterType = null;
	private String sRecycled = ""; //保存未解析前的字符串
	
	private String PortCode = "";//组合代码
    private String PortName = "";//组合名称
    private String SecurityCode = "";//证券代码
    private String SecurityName = "";//证券名称
    private String MktSrcCode = "";//价格来源代码
    private String MktSrcName = "";//价格来源名称
    private java.util.Date NSPriceDate = null;//价格日期
    private double NSPrice;//新股价格

    private String OldPortCode = "";//修改前的组合代码
    private String OldPortName = "";//修改前的组合名称
    private String OldSecurityCode = "";//修改前的证券代码
    private String OldSecurityName = "";//修改前的证券名称
    private String OldMktSrcCode = "";//修改前的价格来源代码
    private String OldMktSrcName = "";//修改前的价格来源名称
    private java.util.Date OldNSPriceDate = null;//修改前的价格日期
	private double OldNSPrice;//修改前的新股价格

    private boolean BShow = false;
    
	public NewSharePriceBean getFilterType() {
		return FilterType;
	}

	public void setFilterType(NewSharePriceBean filterType) {
		FilterType = filterType;
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

	public String getSecurityCode() {
		return SecurityCode;
	}

	public void setSecurityCode(String securityCode) {
		SecurityCode = securityCode;
	}

	public String getSecurityName() {
		return SecurityName;
	}

	public void setSecurityName(String securityName) {
		SecurityName = securityName;
	}

	public String getMktSrcCode() {
		return MktSrcCode;
	}

	public void setMktSrcCode(String mktSrcCode) {
		MktSrcCode = mktSrcCode;
	}

	public String getMktSrcName() {
		return MktSrcName;
	}

	public void setMktSrcName(String mktSrcName) {
		MktSrcName = mktSrcName;
	}

	public java.util.Date getNSPriceDate() {
		return NSPriceDate;
	}

	public void setNSPriceDate(java.util.Date nSPriceDate) {
		NSPriceDate = nSPriceDate;
	}

	public double getNSPrice() {
		return NSPrice;
	}

	public void setNSPrice(double nSPrice) {
		NSPrice = nSPrice;
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

	public String getOldSecurityCode() {
		return OldSecurityCode;
	}

	public void setOldSecurityCode(String oldSecurityCode) {
		OldSecurityCode = oldSecurityCode;
	}

	public String getOldSecurityName() {
		return OldSecurityName;
	}

	public void setOldSecurityName(String oldSecurityName) {
		OldSecurityName = oldSecurityName;
	}

	public String getOldMktSrcCode() {
		return OldMktSrcCode;
	}

	public void setOldMktSrcCode(String oldMktSrcCode) {
		OldMktSrcCode = oldMktSrcCode;
	}

	public String getOldMktSrcName() {
		return OldMktSrcName;
	}

	public void setOldMktSrcName(String oldMktSrcName) {
		OldMktSrcName = oldMktSrcName;
	}

	public java.util.Date getOldNSPriceDate() {
		return OldNSPriceDate;
	}

	public void setOldNSPriceDate(java.util.Date oldNSPriceDate) {
		OldNSPriceDate = oldNSPriceDate;
	}

	public double getOldNSPrice() {
		return OldNSPrice;
	}

	public void setOldNSPrice(double oldNSPrice) {
		OldNSPrice = oldNSPrice;
	}

	public boolean isBShow() {
		return BShow;
	}

	public void setBShow(boolean bShow) {
		BShow = bShow;
	}
	
	private NewSharePriceBean newSharePriceBean = null;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	
	//新增一条数据
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
	            strSql = "insert into " + pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
	            	+ " (FPORTCODE, FZQDM, FZQMC, FSZSH, FXGJG, FDATE,"
	            	+ " fcheckstate, fcreator, fcreatetime) "
	            	+ " values ("
	            	+ dbl.sqlString(this.PortCode.length() > 0 ? this.PortCode:" ") + ","
	            	+ dbl.sqlString(this.SecurityCode) + "," 
	            	+ dbl.sqlString(this.SecurityName) + ","
	            	+ dbl.sqlString(this.MktSrcCode.length() > 0 ? this.MktSrcCode : " ") + ","
	            	+ this.NSPrice + ","
	            	+ dbl.sqlDate(this.NSPriceDate) + "," 
	            	+ "0," 
	            	+ dbl.sqlString(this.creatorCode) + ","
	            	+ dbl.sqlString(this.creatorTime) + ")";
	
	            dbl.executeSql(strSql);
            
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增新股价格信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {	
		  //MS01274   add by zhangfa 2010.07.05   QDV4国内(测试)2010年06月02日01_AB
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("TB_DATA_NEWSHAREPRICE"),
                "FPortCode,FZQDM,FDATE,FSZSH",
                this.PortCode+","+this.SecurityCode+","+format.format(this.NSPriceDate)+","+this.MktSrcCode,
                this.OldPortCode+","+this.OldSecurityCode+","+format.format(this.OldNSPriceDate)+
                ","+this.OldMktSrcCode);
		//---------------------------------------------------------------------------------------

	}

	public void checkSetting() throws YssException {
		String strSql = ""; // 定义一个字符串来放SQL语句
		String[] arrData = null; // 定义一个字符数组来循环删除
		boolean bTrans = false; // 代表是否开始了事务
		Connection conn = dbl.loadConnection(); // 打开一个数据库联接
		try {
			conn.setAutoCommit(false); 
			bTrans = true; 
			if (sRecycled != null&&(!sRecycled.equalsIgnoreCase("")) ) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); 
				for (int i = 0; i < arrData.length; i++) {
					if (arrData[i].length() == 0) {
						continue;
					}
					this.parseRowStr(arrData[i]); 
					//------ modify by wangzuochun 2010.11.11 BUG #380 反审核新股价格设置信息后，审核人和审核时间会变成空值。
                    strSql = "update " + pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
                    	+ " set FCheckState = " + this.checkStateId 
                    	+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
                    	+ ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'"
                    	+ " where fportcode = " + dbl.sqlString(this.PortCode)
                    	+ " and FZQDM = " + dbl.sqlString(this.SecurityCode)
                    	+ " and FDATE = " + dbl.sqlDate(this.NSPriceDate)
						 //MS01274   add by zhangfa 2010.07.05   QDV4国内(测试)2010年06月02日01_AB
                         + " and FSZSH = " +dbl.sqlString(this.MktSrcCode);
                    //-----------------------BUG #380-----------------------//
					//----------------------------------------------------------------------- 
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
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
            
            strSql = "update " + pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where fportcode = " + dbl.sqlString(this.PortCode)
            	+ " and FZQDM = " + dbl.sqlString(this.SecurityCode)
            	+ " and FDATE = " + dbl.sqlDate(this.NSPriceDate)
				//MS01274   add by zhangfa 2010.07.05   QDV4国内(测试)2010年06月02日01_AB
                        + " and FSZSH = " +dbl.sqlString(this.MktSrcCode);
                        //----------------------------------------------------------------------
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除新股价格信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void deleteRecycleData() throws YssException {
		String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null&&!sRecycled.equalsIgnoreCase("")) {
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
                    strSql = "delete from " + pub.yssGetTableName("TB_DATA_NEWSHAREPRICE") 
	                	+ " where fportcode = " + dbl.sqlString(this.PortCode)
		            	+ " and FZQDM = " + dbl.sqlString(this.SecurityCode)
		            	+ " and FDATE = " + dbl.sqlDate(this.NSPriceDate)
						//MS01274   add by zhangfa 2010.07.05   QDV4国内(测试)2010年06月02日01_AB
                        + " and FSZSH = " +dbl.sqlString(this.MktSrcCode);
                        //----------------------------------------------------------------------
                    // 执行sql语句
                    dbl.executeSql(strSql);
                }
            }

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
            strSql = "update " + pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
            	+ " set FPORTCODE = " + dbl.sqlString(this.PortCode.length() > 0 ? this.PortCode : " ")
            	+ " , FZQDM = " + dbl.sqlString(this.SecurityCode)
            	+ " , FZQMC = " + dbl.sqlString(this.SecurityName)
            	+ " , FSZSH = " + dbl.sqlString(this.MktSrcCode.length() > 0 ? this.MktSrcCode : " ")
            	+ " , FXGJG = " + this.NSPrice
            	+ " , FDATE = " + dbl.sqlDate(this.NSPriceDate)
            	+ " , fcreator = " + dbl.sqlString(this.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.creatorTime)
				+ " where fportcode = " + dbl.sqlString(this.OldPortCode)
		        + " and FZQDM = " + dbl.sqlString(this.OldSecurityCode)
		        + " and FDATE = " + dbl.sqlDate(this.OldNSPriceDate)
            	//------ add by wangzuochun 2010.09.19  MS01753   新股价格设置，修改新股价格时会报错 
            	+ " and FSZSH = " + dbl.sqlString(this.OldMktSrcCode);
            	//------------------------ MS01753 ---------------------//
            
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
        
        buf.append(this.PortCode).append("\t");
        buf.append(this.PortName).append("\t");
        buf.append(this.SecurityCode).append("\t");
        buf.append(this.SecurityName).append("\t");
        buf.append(this.MktSrcCode).append("\t");
        buf.append(this.MktSrcName).append("\t");
        buf.append(format.format(this.NSPriceDate)).append("\t");
        buf.append(this.NSPrice).append("\t");
        
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (newSharePriceBean == null) {
			newSharePriceBean = new NewSharePriceBean();
			newSharePriceBean.setYssPub(pub);
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
            this.PortCode = reqAry[0];
            this.PortName = reqAry[1];
            this.SecurityCode = reqAry[2];
            this.SecurityName = reqAry[3];
            this.MktSrcCode = reqAry[4];
            this.MktSrcName = reqAry[5];
            this.NSPriceDate = YssFun.parseDate(reqAry[6].trim().length()==0?"9998-12-31":reqAry[6]);
            this.NSPrice = Double.parseDouble(reqAry[7]);
            
            this.OldPortCode = reqAry[8];
            this.OldPortName = reqAry[9];
            this.OldSecurityCode = reqAry[10];
            this.OldSecurityName = reqAry[11];
            this.OldMktSrcCode = reqAry[12];
            this.OldMktSrcName = reqAry[13];
            this.OldNSPriceDate = YssFun.parseDate(reqAry[14].trim().length()==0?"9998-12-31":reqAry[14]);
            this.OldNSPrice = Double.parseDouble(reqAry[15]);
            
            this.checkStateId = Integer.parseInt(reqAry[16]);//add by guojianhua 2010 09 09
            
            if (reqAry[17].equalsIgnoreCase("true")) {
                this.BShow = true;
            } else {
                this.BShow = false;
            } 
            
            super.parseRecLog();
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new NewSharePriceBean();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);    
            }
        } catch (Exception e) {
            throw new YssException("解析新股价格数据出错！", e);
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
        	
        	strSql = "select a.fportcode as Fportcode, b.fportname as FPortName, a.fzqdm as FZqdm, a.fzqmc as FZqmc, a.fszsh as FZzsh, c.FMKTSRCNAME as FMktSrcName,"
        		+ " a.fdate as FDate, a.fxgjg as FXgjg,a.FCheckState as FCheckState, a.fcreator as FCreator,d.FUserName as FCreatorName, a.fcreatetime as FCreateTime, a.fcheckuser as FCheckUser, d.fusername as FCheckUserName,"
        		+ " a.fchecktime as FCheckTime from "
        		+ pub.yssGetTableName("TB_DATA_NEWSHAREPRICE")
        		//edited by zhouxiang MS01369    新建新股价格时，组合代码相同组合名称和启用日期不同的数据会产生两笔相同的数据。    QDV4赢时胜(测试)2010年06月28日05_B  
        		+ " a left join (select k.fportcode,k.fportname from " + pub.yssGetTableName("tb_Para_Portfolio")+" k"
        		//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//        		+" join (select k.fportcode,max(k.fstartdate) as fstartdate from "+pub.yssGetTableName("tb_Para_Portfolio")+" k"
//        		+" where k.fcheckstate=1 and k.fstartdate <="+dbl.sqlDate(new java.util.Date())
//        		+" group by k.fportcode) m on m.fportcode = k.fportcode and m.fstartdate=k.fstartdate "
        		//----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
        		+" where k.fcheckstate=1 "//edit by songjie 2011.03.16 不以最大的启用日期查询数据
        		//----------------end---------------
        		+ " ) b on a.fportcode = b.fportcode "
        		+ " left join (select * from " + pub.yssGetTableName("Tb_para_MarketSource") + ") c on a.fszsh = c.FMKTSRCCODE "
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
                + " on a.FCreator = d.FUserCode "
        		+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) e " 
                + " on a.FCheckUser = e.FUserCode " 
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
        	throw new YssException("获取新股价格数据出错！" + "\r\n" + e.getMessage(), e);
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
			
    		if(this.FilterType!=null)
    		{
    			NewSharePriceBean filter = this.FilterType;
    			
    			if (filter.isBShow() == false) {
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
    			
    			if(!YssFun.formatDate(filter.getNSPriceDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"FDATE = "+dbl.sqlDate(filter.getNSPriceDate()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"FPORTCODE in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getSecurityCode() !=null && filter.getSecurityCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FZQDM = "+dbl.sqlString(filter.getSecurityCode().trim()));
    			}    
    			
    			if(filter.getMktSrcCode() != null && filter.getMktSrcCode().trim().length()>0)
    			{
    				alCon.add(prefix+"FSZSH = "+dbl.sqlString(filter.getMktSrcCode().trim()));
    			}
    			
    			if(filter.getNSPrice() > 0 )
    			{
    				alCon.add(prefix+"FXGJG = " + filter.getNSPrice());
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
        this.SecurityCode = rs.getString("FZqdm");
        this.SecurityName = rs.getString("FZqmc");
        this.MktSrcCode = rs.getString("FZzsh");
        this.MktSrcName = rs.getString("FMktSrcName");
        this.NSPriceDate = rs.getDate("FDate");
        this.NSPrice = rs.getDouble("FXgjg");
        
        super.setRecLog(rs);
    }
}
