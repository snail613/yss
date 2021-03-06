package com.yss.main.etfoperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.pojo.ETFUnDeliveryDateBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * ETF 非交收日设置
 * @author yanghaiming
 *
 */
public class ETFUnDeliveryDate extends BaseDataSettingBean implements
		IDataSetting {
	private ETFUnDeliveryDateBean etfUnDelivery = null;
	
	private String sRecycled = "";
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	public ETFUnDeliveryDate(){
		
	}

	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        java.util.Date beginDate = this.etfUnDelivery.getStartDeliveryDate();
        java.util.Date endDate = this.etfUnDelivery.getEndDeliveryDate();
        int days = YssFun.dateDiff(beginDate, endDate); //日期天数
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            for (int day = 0; day <= days; day++) { //循环日期
                beginDate = YssFun.addDay(beginDate, day == 0 ? 0 : 1); 
        	
	            strSql = "insert into " + pub.yssGetTableName("tb_etf_unsettledateset")
	            	+ " (FPortCode, FHolidayCode, FUnSettleDate, FDesc, "
	            	+ " fcheckstate, fcreator, fcreatetime) "
	            	+ " values ("
	            	+ dbl.sqlString(this.etfUnDelivery.getPortCode()) + ","
	            	+ dbl.sqlString(this.etfUnDelivery.getHolidayCode()) + "," 
	            	+ dbl.sqlDate(beginDate) + "," 
	            	+ dbl.sqlString(this.etfUnDelivery.getDesc()) + ",0," 
	            	+ dbl.sqlString(this.etfUnDelivery.creatorCode) + ","
	            	+ dbl.sqlString(this.etfUnDelivery.creatorTime) + ")";
	
	            dbl.executeSql(strSql);
            
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
            }
            return "";
        } catch (Exception e) {
            throw new YssException("新增ETF非交收日信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_ETF_unsettledateset"),
                "FPortCode,fholidaycode,funsettledate",
                this.etfUnDelivery.getPortCode()+","+this.etfUnDelivery.getHolidayCode()+","+format.format(this.etfUnDelivery.getStartDeliveryDate()),
                this.etfUnDelivery.getOldPortCode()+","+this.etfUnDelivery.getOldHolidayCode()+","+format.format(this.etfUnDelivery.getOldStartDeliveryDate()));

	}

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
					
                    strSql = "update " + pub.yssGetTableName("tb_etf_unsettledateset")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = case fcheckstate when 0 then " 
                    	+ dbl.sqlString(this.etfUnDelivery.checkUserCode) + " else null end"
                    	+ ", FCheckTime = case fcheckstate when 0 then "
                    	+ dbl.sqlString(this.etfUnDelivery.checkTime)
                    	+ " else null end " 
                    	+ " where fportcode = " + dbl.sqlString(this.etfUnDelivery.getPortCode())
                    	+ " and fholidaycode = " + dbl.sqlString(this.etfUnDelivery.getHolidayCode())
                    	+ " and funsettledate = " + dbl.sqlDate(this.etfUnDelivery.getStartDeliveryDate());
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核ETF非交收日设置信息出错", e);
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
            
            strSql = "update " + pub.yssGetTableName("tb_etf_unsettledateset")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
                + " where fportcode = " + dbl.sqlString(this.etfUnDelivery.getPortCode())
            	+ " and fholidaycode = " + dbl.sqlString(this.etfUnDelivery.getHolidayCode())
            	+ " and funsettledate = " + dbl.sqlDate(this.etfUnDelivery.getStartDeliveryDate());
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除ETF非交收日设置信息出错", e);
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
                    strSql = "delete from " + pub.yssGetTableName("Tb_ETF_unsettledateset") 
	                	+ " where fportcode = " + dbl.sqlString(this.etfUnDelivery.getPortCode())
	                	+ " and fholidaycode = " + dbl.sqlString(this.etfUnDelivery.getHolidayCode())
	                	+ " and funsettledate = " + dbl.sqlDate(this.etfUnDelivery.getStartDeliveryDate());
				
                    // 执行sql语句
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除ETF非交收日设置数据出错", e);
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
            strSql = "update " + pub.yssGetTableName("tb_etf_unsettledateset")
            	+ " set FPortCode = " + dbl.sqlString(this.etfUnDelivery.getPortCode())
            	+ " , FHolidayCode = " + dbl.sqlString(this.etfUnDelivery.getHolidayCode())
            	+ " , FUnSettleDate = " + dbl.sqlDate(this.etfUnDelivery.getStartDeliveryDate())
            	+ " , FDesc = " + dbl.sqlString(this.etfUnDelivery.getDesc())
            	+ " , fcreator = " + dbl.sqlString(this.etfUnDelivery.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.etfUnDelivery.creatorTime)
				+ " where fportcode = " + dbl.sqlString(this.etfUnDelivery.getOldPortCode())
				+ " and FHolidayCode = " + dbl.sqlString(this.etfUnDelivery.getOldHolidayCode())
				+ " and FUnSettleDate = " + dbl.sqlDate(this.etfUnDelivery.getOldStartDeliveryDate());
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改ETF非交收日信息出错", e);
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
		return etfUnDelivery.buildRowStr();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		if (etfUnDelivery == null) {
			etfUnDelivery = new ETFUnDeliveryDateBean();
			etfUnDelivery.setYssPub(pub);
        }
		etfUnDelivery.parseRowStr(sRowStr);
        sRecycled = sRowStr;

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
        	
            strSql = "select a.fportcode, b.fportname, a.fholidaycode, c.fholidaysname as FHolidayName, a.funsettledate, a.fdesc,"
            	+ " a.fcheckstate, a.fcreator, d.FUserName as FCreatorName, a.fcreatetime, a.fcheckuser, e.FUserName as FCheckUserName, a.fchecktime from " 
            	+ pub.yssGetTableName("TB_etf_unsettledateset")
            	+ " a left join (select * from " + pub.yssGetTableName("tb_Para_Portfolio")
            	+ " ) b on a.fportcode = b.fportcode "
            	+ " left join (select * from Tb_Base_Holidays) c on a.fholidaycode = c.fholidayscode"
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
            	
            	this.etfUnDelivery.setETFRateAttr(rs);
            	
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
        	throw new YssException("获取ETF非交收日设置数据出错！" + "\r\n" + e.getMessage(), e);
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
			
    		if(etfUnDelivery.getFilterType()!=null)
    		{
    			ETFUnDeliveryDateBean filter=etfUnDelivery.getFilterType();
    			
    			if (filter.BShow() == false) {
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
    			
    			if(!YssFun.formatDate(filter.getStartDeliveryDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"funsettledate = "+dbl.sqlDate(filter.getStartDeliveryDate()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"fportcode in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getHolidayCode()!=null && filter.getHolidayCode().trim().length()>0)
    			{
    				alCon.add(prefix+"fholidaycode = "+dbl.sqlString(filter.getHolidayCode().trim()));
    			}    
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    }
}
