package com.yss.main.parasetting.FixInterest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;


/**
 * @author shashijie ,2011-12-20 下午06:15:39  债券信息浮动利率设置
 * STORY 1713 
 */
public class DriftRateBean extends BaseDataSettingBean implements IDataSetting {
	
	
	
	private String FSecurityCode = "";//证券代码
	private String FSecurityName = "";//证券名称
	private Date FStartDate = null;//起始日期
	private double FRate = 0;//利率
	private String FIsNewRate = "2";//整个付息期间均采用新利率
	private String OldFSecurityCode = "";//旧证券代码
	
	private DriftRateBean filterType;//自身对象
	
	
	
	/** shashijie 2011-12-20 STORY  */
	public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//先检查是否已有存在记录
        	if (isDriftRateInsert()) {
        		throw new YssException("需要增加债券信息浮动利率的数据已存在!");
			}
        	
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_DriftRate") +
                "(FSecurityCode, FStartDate, FRate ,FIsNewRate) values(" + 
                dbl.sqlString(this.FSecurityCode) + "," +
                dbl.sqlDate(this.FStartDate) + "," +
                this.FRate + ","+
                dbl.sqlString(this.FIsNewRate)+
                ")";
            
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch (Exception e) {
            throw new YssException("增加债券信息浮动利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
    
	}
	
	/**先检查是否已有存在记录
	 * @author shashijie ,2011-12-23 , STORY 1713
	 */
	private boolean isDriftRateInsert() throws YssException {
		ResultSet rs = null;
		
		try {
			String strSql = " select * from " + pub.yssGetTableName("Tb_Para_DriftRate") + 
	        	" where FSecurityCode = "+dbl.sqlString(this.FSecurityCode)+
	        	" And FStartDate = " + dbl.sqlDate(this.FStartDate);
	        rs = dbl.openResultSet(strSql);
            
	        if (rs.next()) {
	            return true;
	        } else {
				return false;
			}
            
		} catch (Exception e) {
			throw new YssException("先检查是否已有存在记录出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/** shashijie 2011-12-20 STORY 1713 */
	public void checkInput(byte btOper) throws YssException {
		/*dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_Para_DriftRate"),
                "FSecurityCode,FStartDate",
                this.FSecurityCode + "," + YssFun.formatDate(this.FStartDate),
                this.OldFSecurityCode + "," + YssFun.formatDate(this.OldFStartDate));*/
	}
	
	/** shashijie 2011-12-20 STORY 审核 */
	public void checkSetting() throws YssException {
		//审核不需要
	}
	
	/** shashijie 2011-12-20 STORY 删除数据，即放入回收站 */
	public void delSetting() throws YssException {
		
	}
	
	/** shashijie 2011-12-20 STORY 删除数据 */
	public void deleteRecycleData() throws YssException {
		boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
		
		try {
	        
	        conn.setAutoCommit(false);
            bTrans = true;
            
            if (!this.FSecurityCode.trim().equals("null") && this.FSecurityCode.trim().length() > 0) {
                String strSql = "delete from " + pub.yssGetTableName("Tb_Para_DriftRate") + " where FSecurityCode = "+
                	dbl.sqlString(this.FSecurityCode);
                
                	//this.FStartDate==null ? " And FStartDate = " + dbl.sqlDate(this.FStartDate) : " " ;
                
                dbl.executeSql(strSql);
            }
	        
	        conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
		} catch (Exception e) {
			//dbl.closeResultSetFinal(rs);
			throw new YssException("删除债券信息浮动利率出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/** shashijie 2011-12-20 STORY 债券信息浮动利率 */
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_DriftRate") +
                " set " +
                " FStartDate = " + dbl.sqlDate(this.FStartDate) + " ," +
                " FRate = " + this.FRate + " ," +
                " FIsNewRate = "+dbl.sqlString(this.FIsNewRate)+
                " where FSecurityCode = " + dbl.sqlString(this.OldFSecurityCode);
                //" And FStartDate = " + dbl.sqlDate(this.OldFStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改债券信息浮动利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getAllSetting() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public IDataSetting getSetting() throws YssException {
		return null;
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] strAry = null;
        try {
            strAry = sMutilRowStr.split("\f\f");
            for (int i = 0; i < strAry.length; i++) {
                this.parseRowStr(strAry[i]);
                
                if (i==0) {//第一次执行
                	//先删除已有记录
                    this.deleteRecycleData();
				}
                
                //添加记录
                this.addSetting();
            }
        } catch (Exception e) {
            throw new YssException("批量保存债券信息浮动利率出错", e);
        }
        return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getBeforeEditData() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.FSecurityCode).append("\t");
        buf.append(this.FSecurityName).append("\t");
        buf.append(YssFun.formatDate(this.FStartDate)).append("\t");
        buf.append(this.FRate).append("\t");
        buf.append(this.FIsNewRate).append("\t");
        buf.append(this.OldFSecurityCode).append("\t");
        
        return buf.toString();
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getOperValue(String sType) throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY 解析债券信息浮动利率 */
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
            reqAry = sTmpStr.split("\t");
            this.FSecurityCode = reqAry[0];
            this.FSecurityName = reqAry[1];
            this.FStartDate = YssFun.toDate(reqAry[2]);
            this.FRate = YssFun.isNumeric(reqAry[3]) ? Double.valueOf(reqAry[3]) : 0;
            this.FIsNewRate = reqAry[4];
            
            this.OldFSecurityCode = reqAry[5];
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DriftRateBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
            
        } catch (Exception e) {
            throw new YssException("解析债券信息浮动利率出错", e);
        }
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewData1() throws YssException {
		String strSql = "";
        strSql = "select a.* , " +
                " c.FSecurityName as FSecurityName " +
                " from " + pub.yssGetTableName("Tb_Para_DriftRate") + " a" +
                " left join ( select FSecurityCode,FSecurityName from " + 
                pub.yssGetTableName("Tb_Para_Security") + ") c" +
                " on a.FSecurityCode = c.FSecurityCode " +
                " where a.FSecurityCode = "+dbl.sqlString(this.FSecurityCode);
            
        return this.builderListViewData(strSql);
	}
	
	/**获取债券浮息信息
	 * @param strSql
	 * @author shashijie ,2011-12-23 , STORY 1713 
	 */
	private String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取债券浮息信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}

	/**设置属性值
	 * @param rs 
	 * @author shashijie ,2011-12-26 , STORY 1713 
	 */
	private void setSecurityAttr(ResultSet rs) throws Exception {
		this.FSecurityCode = rs.getString("FSecurityCode");
        this.FSecurityName = rs.getString("FSecurityName").trim().length() > 0 ? rs.getString("FSecurityName") : " " ;
        this.FRate = rs.getDouble("FRate");
        this.FStartDate = rs.getDate("FStartDate");
        this.FIsNewRate = rs.getString("FIsNewRate");
        //super.setRecLog(rs);
	}

	/**拼接条件
	 * @return 
	 * @author shashijie ,2011-12-23 , STORY 1713 
	 */
	public String buildFilterSql() {
		String sResult = "";
        if (this.filterType != null) {
            sResult += " where 1=1";
            //证券代码
            if (this.filterType.FSecurityCode.trim().length() != 0) {
                sResult += " and a.FSecurityCode like '" +
                    this.filterType.FSecurityCode.replaceAll("'", "''") + "%'";
            }
            //利率
            if (this.filterType.FRate != 0) {
                sResult += " and a.FRate = " +
                    this.filterType.FRate;
            }
            //利息日期
            if (this.filterType.FStartDate != null) {
                sResult += " and a.FStartDate = " + dbl.sqlDate(this.FStartDate);
            }
            //是否使用整个付息期间均采用新利率
            if (this.filterType.FIsNewRate.trim().length() != 0) {
				sResult += " and a.FIsNewRate = " + dbl.sqlString(this.FIsNewRate);
			}
        }
        return sResult;
	}

	/** shashijie 2011-12-20 STORY  */
	public String getListViewData2() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY 1713 前台list调用方法 */
	public String getListViewData3() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            
            sHeader = "起始日期\t利率\t整个付息期间均采用新利率";
            strSql = "select a.* , " +
                " c.FSecurityName as FSecurityName " +
                " from " + pub.yssGetTableName("Tb_Para_DriftRate") + " a" +
                " left join ( select FSecurityCode,FSecurityName from " + 
                pub.yssGetTableName("Tb_Para_Security") + ") c" +
                " on a.FSecurityCode = c.FSecurityCode " +
                " where a.FSecurityCode = "+dbl.sqlString(this.FSecurityCode);
            	
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	bufShow.append(rs.getDate("FStartDate")).append("\t");
            	bufShow.append(rs.getDouble("FRate")).append("\t");
            	bufShow.append(rs.getDouble("FIsNewRate")).append("\t");
            	bufShow.append(YssCons.YSS_LINESPLITMARK);
                
            	this.setSecurityAttr(rs);
            	
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
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;

        } catch (Exception e) {
            throw new YssException("获取数据时出错", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewData4() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewGroupData1() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewGroupData2() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewGroupData3() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewGroupData4() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getListViewGroupData5() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewData1() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewData2() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewData3() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewGroupData1() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewGroupData2() throws YssException {
		return "";
	}
	
	/** shashijie 2011-12-20 STORY  */
	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	/**shashijie 2011-12-20 
	 * @return 获取fSecurityCode的值
	 */
	public String getFSecurityCode() {
		return FSecurityCode;
	}

	/**shashijie 2011-12-20 
	 * @param 设置fSecurityCode为fSecurityCode的值
	 */
	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}

	/**shashijie 2011-12-20 
	 * @return 获取fStartDate的值
	 */
	public Date getFStartDate() {
		return FStartDate;
	}

	/**shashijie 2011-12-20 
	 * @param 设置fStartDate为fStartDate的值
	 */
	public void setFStartDate(Date fStartDate) {
		FStartDate = fStartDate;
	}

	/**shashijie 2011-12-20 
	 * @return 获取fRate的值
	 */
	public double getFRate() {
		return FRate;
	}

	/**shashijie 2011-12-20 
	 * @param 设置fRate为fRate的值
	 */
	public void setFRate(double fRate) {
		FRate = fRate;
	}

	/**shashijie 2011-12-23 
	 * @return 获取fSecurityName的值
	 */
	public String getFSecurityName() {
		return FSecurityName;
	}

	/**shashijie 2011-12-23 
	 * @param 设置fSecurityName为fSecurityName的值
	 */
	public void setFSecurityName(String fSecurityName) {
		FSecurityName = fSecurityName;
	}

	/**shashijie 2011-12-23 
	 * @return 获取oldFSecurityCode的值
	 */
	public String getOldFSecurityCode() {
		return OldFSecurityCode;
	}

	/**shashijie 2011-12-23 
	 * @param 设置oldFSecurityCode为oldFSecurityCode的值
	 */
	public void setOldFSecurityCode(String oldFSecurityCode) {
		OldFSecurityCode = oldFSecurityCode;
	}

	public String getFIsNewRate() {
		return FIsNewRate;
	}

	public void setFIsNewRate(String fIsNewRate) {
		FIsNewRate = fIsNewRate;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	

}

