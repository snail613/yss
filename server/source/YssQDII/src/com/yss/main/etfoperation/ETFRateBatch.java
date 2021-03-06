package com.yss.main.etfoperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.etfoperation.pojo.ETFRateBatchCreateBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * create by songjie 
 * 2009-11-17 
 * V4.1_ETF:MS00002
 * QDV4.1赢时胜（上海）2009年9月28日01_A
 * 台帐表对对应的汇率设置界面的操作方法
 */
public class ETFRateBatch extends BaseDataSettingBean implements IDataSetting{
	private ETFRateBatchCreateBean etfRate = null;
	private String sRecycled = "";
	/**
	 * 构造函数
	 */
	public ETFRateBatch() {
		
	}
	
	public void checkInput(byte btOper) throws YssException {
//        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_ETF_Param"),
//                               "FPortCode", this.etfParam.getPortCode(), this.etfParam
//                               .getOldPortCode());
    }
	
	public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
        	strSql = "select * from " + pub.yssGetTableName("tb_etf_bookexratedata")
        		+ " where fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
        		+ " and fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
        		+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
        		+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate());
        	
        	//modify by fangjiang 2010.09.30 MS01811 QDV4赢时胜(测试)2010年09月25日06_B 
        	ResultSet rs = dbl.openResultSet(strSql);
            if (rs.next()) {      		
        		throw new YssException( YssFun.getCheckStateName(rs.getInt("FCheckState")) + "中【" + this.etfRate.getPortCode() + ","
        				+ YssFun.formatDate(this.etfRate.getEtfBookDate()) + ","
        				+ YssFun.formatDate(this.etfRate.getChangeRateDate()) + "," 
        				+ (this.etfRate.getBookType().equalsIgnoreCase("B") ?"申购" :"赎回") 
        				+ "】已经存在，请重新输入");      		
        	}
            //---------------------------------
            strSql = "insert into " + pub.yssGetTableName("tb_etf_bookexratedata")
            	+ " (fportcode, fbooktype, fbuydate, fexratedate, fstockholdercode, fsecuritycode, "
            	+ " fexratevalue, fcheckstate, fcreator, fcreatetime) "
            	+ " values ("
            	+ dbl.sqlString(this.etfRate.getPortCode()) + ","
            	+ dbl.sqlString(this.etfRate.getBookType()) + "," 
            	+ dbl.sqlDate(this.etfRate.getEtfBookDate()) + "," 
            	+ dbl.sqlDate(this.etfRate.getChangeRateDate()) + ",' ',' '," 
            	+ this.etfRate.getChangeRate() + ",0," 
            	+ dbl.sqlString(this.etfRate.creatorCode) + ","
            	+ dbl.sqlString(this.etfRate.creatorTime) + ")";

            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增ETF汇率信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            if (!etfRate.getPortCode().equalsIgnoreCase(etfRate.getOldPortCode())
            	||!etfRate.getBookType().equalsIgnoreCase(etfRate.getOldBookType())
            	||!YssFun.formatDate(etfRate.getChangeRateDate()).equalsIgnoreCase(YssFun.formatDate(etfRate.getOldChangeRateDate()))
            	||!YssFun.formatDate(etfRate.getEtfBookDate()).equalsIgnoreCase(YssFun.formatDate(etfRate.getOldETFBookDate()))) {
            	
				strSql = "select * from "
						+ pub.yssGetTableName("tb_etf_bookexratedata")
						+ " where fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
						+ " and fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
						+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
						+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate());

				if (dbl.executeSqlwithReturnRows(strSql) > 0) {
					throw new YssException("信息已经存在，请重新输入！");
				}
			}
        	
            strSql = "update " + pub.yssGetTableName("tb_etf_bookexratedata")
            	+ " set fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
            	+ " , fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
            	+ " , fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
            	+ " , fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate())
            	+ " , fexratevalue = " + this.etfRate.getChangeRate()
            	+ " , fcreator = " + dbl.sqlString(this.etfRate.creatorCode)
            	+ " , fcreatetime = " + dbl.sqlString(this.etfRate.creatorTime)
				+ " where fportcode = " + dbl.sqlString(this.etfRate.getOldPortCode())
				+ " and fbooktype = " + dbl.sqlString(this.etfRate.getOldBookType())
				+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getOldETFBookDate())
				+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getOldChangeRateDate());
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("修改ETF汇率信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            
            strSql = "update " + pub.yssGetTableName("tb_etf_bookexratedata")
                + " set FCheckState = 2 " 
                + ", FCheckUser = null " 
                + ", FCheckTime = null "
				+ " where fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
				+ " and fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
				+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
				+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate());
            
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除ETF汇率信息出错", e);
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
			conn.setAutoCommit(false); // 开启一个事务
			bTrans = true; // 代表是否关闭事务
			// 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
			if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
				arrData = sRecycled.split("\r\n"); // 解析它，把它还原成条目放在数组里。
				for (int i = 0; i < arrData.length; i++) { // 循环数组，也就是循环还原条目
					if (arrData[i].length() == 0) {
						continue; // 如果数组里没有内容就执行下一个内容
					}
					this.parseRowStr(arrData[i]); // 解析这个数组里的内容
					
                    strSql = "update " + pub.yssGetTableName("tb_etf_bookexratedata")
                    	+ " set FCheckState = case fcheckstate when 0 then 1 else 0 end" 
                    	+ ", FCheckUser = case fcheckstate when 0 then " 
                    	+ dbl.sqlString(this.etfRate.checkUserCode) + " else null end"
                    	+ ", FCheckTime = case fcheckstate when 0 then "
                    	+ dbl.sqlString(this.etfRate.checkTime)
                    	+ " else null end " 
                    	+ " where fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
                    	+ " and fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
                    	+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
                    	+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate());
					
					dbl.executeSql(strSql); // 执行更新操作
				}
			}
			conn.commit(); // 提交事务
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核ETF汇率信息出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans); // 释放资源
		}
	}
	
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return "";
	}
	
	public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
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
        	
            strSql = "select a.FPortCode,a.FBookType,case FBookType when 'B' then '申购' else '赎回' end as FBookTypeName,a.FBuyDate,a.FExRateDate,a.FExRateValue, " 
            	+ " a.FCheckState, a.FCreator, a.FCreateTime, a.FCheckUser, a.FCheckTime, " 
            	+ " b.FPortName as FPortName,c.FUserName as FCreatorName, " 
            	+ " d.FUserName as FCheckUserName from " + pub.yssGetTableName("Tb_ETF_Bookexratedata")
                + " a left join (select FPortCode,FPortName from "
                + pub.yssGetTableName("Tb_Para_Portfolio")
                + " where FCheckState = 1) b on a.FPortCode = b.FPortCode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c " 
                + " on a.FCreator = c.FUserCode "
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) d " 
                + " on a.FCheckUser = d.FUserCode " 
                + " where " + buildFilterStr("a")
                + " order by a.FCheckState, a.FCreateTime desc";
            
            rs = dbl.openResultSet(strSql);
            while(rs.next()){
            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                append(YssCons.YSS_LINESPLITMARK);
            	
            	this.etfRate.setETFRateAttr(rs);
            	
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
        	throw new YssException("获取ETF汇率设置数据出错！" + "\r\n" + e.getMessage(), e);
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
			
    		if(etfRate.getFilterType()!=null)
    		{
    			ETFRateBatchCreateBean filter=etfRate.getFilterType();
    			
    			if (filter.isbShow() == false) {
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
    			
    			if(!YssFun.formatDate(filter.getChangeRateDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"fexratedate = "+dbl.sqlDate(filter.getChangeRateDate()));
    			}
    			
    			if(!YssFun.formatDate(filter.getEtfBookDate()).equalsIgnoreCase("9998-12-31"))
    			{
    				alCon.add(prefix+"fbuydate = "+dbl.sqlDate(filter.getEtfBookDate()));
    			}
    			
    			if(filter.getPortCode()!=null&&filter.getPortCode().trim().length()!=0)
    			{
    				alCon.add(prefix+"fportcode in ("+dbl.sqlString(filter.getPortCode().trim())+")");
    			} 
    			
    			if(filter.getBookType()!=null&&filter.getBookType().trim().length()!=0&&!filter.getBookType().trim().equalsIgnoreCase("99"))
    			{
    				alCon.add(prefix+"fbooktype = "+dbl.sqlString(filter.getBookType().trim()));
    			}    
    		}
    		
			str=YssFun.join((String[])alCon.toArray(new String[]{}), " and ");
        }
        catch(Exception e){
        	throw new YssException("生成筛选条件子句出错！", e);
        }
        
        return str;
    }
    
    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getListViewGroupData1() throws YssException {
        return "";
    }

    public String getListViewGroupData2() throws YssException {
        return "";
    }

    public String getListViewGroupData3() throws YssException {
        return "";
    }

    public String getListViewGroupData4() throws YssException {
        return "";
    }

    public String getListViewGroupData5() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }
    
    public void parseRowStr(String sRowStr) throws YssException {
        if (etfRate == null) {
        	etfRate = new ETFRateBatchCreateBean();
        	etfRate.setYssPub(pub);
        }
        etfRate.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }
    
    public String buildRowStr() throws YssException {
    	return etfRate.buildRowStr();
    }
    
    public String getOperValue(String sType) throws YssException {
    	if(sType != null && sType.equals("getMakeUpMode")){
    		return getMakeUpMode();
    	}
    	return "";
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
                    
                    strSql = "delete from " + pub.yssGetTableName("Tb_ETF_Bookexratedata") 
	                	+ " where fportcode = " + dbl.sqlString(this.etfRate.getPortCode())
	                	+ " and fbooktype = " + dbl.sqlString(this.etfRate.getBookType())
	                	+ " and fbuydate = " + dbl.sqlDate(this.etfRate.getEtfBookDate())
	                	+ " and fexratedate = " + dbl.sqlDate(this.etfRate.getChangeRateDate());
				
                    // 执行sql语句
                    dbl.executeSql(strSql);
                }
            }

            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除ETF汇率数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }		
	}
	
	/**
	 * 获取相关组合代码对应的已审核的ETF参数设置中的补票方式
	 * @return
	 * @throws YssException
	 */
	private String getMakeUpMode()throws YssException{
		String strSql = "";
		ResultSet rs = null;
		String makeUpMode = "";
		try{
			strSql = " select FSupplyMode from " + pub.yssGetTableName("Tb_ETF_Param") + 
			" where FPortCode = " + dbl.sqlString(etfRate.getPortCode()) + 
			" and FCheckState = 1 ";
			
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				makeUpMode = rs.getString("FSupplyMode");
			}
			
			return makeUpMode;
		}
		catch(Exception e){
			throw new YssException("清除ETF汇率数据出错", e);
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
	}
}
