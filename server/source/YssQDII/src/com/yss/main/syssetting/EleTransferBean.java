package com.yss.main.syssetting;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdata.CashConsiderationBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * add by qiuxufeng 2010.09.17 
 *
 * <p>Title: BaseDataSettingBean</p>
 *
 * <p>Description: </p>
 *
 */
public class EleTransferBean 
	extends BaseDataSettingBean implements IDataSetting {

	private String strTrustBank = "";//托管人（托管行简称）
    private String strTransferType = "";//划款类型
    private String strTransferCode = "";//划款代码
    private String strMessageType = "";//报文类型
    private String strCheckState = "";//审核标志
    private String strCreator = "";//制作人
    private String strCheckUser = "";//审核人
    private String strCreateTime = "";//制作时间
    private String strCheckedTime = "";//审核时间
    private String strDesc = "";//描述
    private String sRecycled = ""; //保存未解析前的字符串
    private EleTransferBean filterType;
    private String strIsOnlyColumns = "0"; //打开窗体时是否加载数据，默认加载数据
    private String strOldTrustBank = "";
    private String strOldTransferType = "";
    private String strOldTransferCode = "";
	
	public String getStrTrustBank() {
		return strTrustBank;
	}

	public void setStrTrustBank(String strTrustBank) {
		this.strTrustBank = strTrustBank;
	}

	public String getStrTransferType() {
		return strTransferType;
	}

	public void setStrTransferType(String strTransferType) {
		this.strTransferType = strTransferType;
	}

	public String getStrTransferCode() {
		return strTransferCode;
	}

	public void setStrTransferCode(String strTransferCode) {
		this.strTransferCode = strTransferCode;
	}

	public String getStrMessageType() {
		return strMessageType;
	}

	public void setStrMessageType(String strMessageType) {
		this.strMessageType = strMessageType;
	}

	public String getStrCheckState() {
		return strCheckState;
	}

	public void setStrCheckState(String strCheckState) {
		this.strCheckState = strCheckState;
	}

	public String getStrCreator() {
		return strCreator;
	}

	public void setStrCreator(String strCreator) {
		this.strCreator = strCreator;
	}

	public String getStrCheckUser() {
		return strCheckUser;
	}

	public void setStrCheckUser(String strCheckUser) {
		this.strCheckUser = strCheckUser;
	}

	public String getStrCreateTime() {
		return strCreateTime;
	}

	public void setStrCreateTime(String strCreateTime) {
		this.strCreateTime = strCreateTime;
	}

	public String getStrCheckedTime() {
		return strCheckedTime;
	}

	public void setStrCheckedTime(String strCheckedTime) {
		this.strCheckedTime = strCheckedTime;
	}

	public String getStrDesc() {
		return strDesc;
	}

	public void setStrDesc(String strDesc) {
		this.strDesc = strDesc;
	}

	public String getsRecycled() {
		return sRecycled;
	}

	public void setsRecycled(String sRecycled) {
		this.sRecycled = sRecycled;
	}

	public EleTransferBean getFilterType() {
		return filterType;
	}

	public void setFilterType(EleTransferBean filterType) {
		this.filterType = filterType;
	}

	public String getStrIsOnlyColumns() {
		return strIsOnlyColumns;
	}

	public void setStrIsOnlyColumns(String strIsOnlyColumns) {
		this.strIsOnlyColumns = strIsOnlyColumns;
	}

	public String getStrOldTrustBank() {
		return strOldTrustBank;
	}

	public void setStrOldTrustBank(String strOldTrustBank) {
		this.strOldTrustBank = strOldTrustBank;
	}

	public String getStrOldTransferType() {
		return strOldTransferType;
	}

	public void setStrOldTransferType(String strOldTransferType) {
		this.strOldTransferType = strOldTransferType;
	}

	public String getStrOldTransferCode() {
		return strOldTransferCode;
	}

	public void setStrOldTransferCode(String strOldTransferCode) {
		this.strOldTransferCode = strOldTransferCode;
	}

	public String addSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "insert into TDzTypeCodePP" +
        		"(FTgR,FHKType,FHKcode,FBwType,FCheckState,FCreator,FCheckUser,FCreateTime,FCheckTime,FDesc)" +
        		" values(" +
        		dbl.sqlString(this.strTrustBank) + "," +
        		dbl.sqlString(this.strTransferType) + "," +
        		dbl.sqlString(this.strTransferCode) + "," +
        		dbl.sqlString(this.strMessageType) + "," +
        		"0" + "," +
        		dbl.sqlString(pub.getUserCode()) + "," +
        		(this.checkStateId==1 ? dbl.sqlString(this.creatorCode):"' '" )+ "," +//edited by zhouxiang MS01628 新建出现审核人的情况
        		dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + "," +
        		(this.checkStateId==1  ? dbl.sqlString(YssFun.formatDatetime(new java.util.Date())):"' '" )
        		 + "," +
        		dbl.sqlString(this.strDesc) + 
        		")";
        
        	conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
		} catch (Exception e) {
			throw new YssException("新建出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public void checkInput(byte btOper) throws YssException {
		/**shashijie 2011.05.27 BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
			dbFun.checkInputCommon(btOper, "TDzTypeCodePP",
	                "FHKcode",
	                this.strTransferCode,this.strOldTransferCode);
        /**end*/
	}

	public void checkSetting() throws YssException {
		String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            /**shashijie 2012-7-2 STORY 2475 */
			if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //SQL更新语句：更新表中审核、反审核、还原操作对应的记录，重新设置审核状态字段FCheckState
                    strSql = "update TDzTypeCodePP" +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
            	        "' where FHKcode = " + dbl.sqlString(this.strTransferCode) +
            	        " and FTgr = " + dbl.sqlString(this.strTrustBank) +
            	        " and FHKType = " + dbl.sqlString(this.strTransferType);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核划款类型设置数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}

	public void delSetting() throws YssException {
        String[] arrData = null;
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
			/**shashijie 2012-7-2 STORY 2475 */
        	if ( sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) {
			/**end*/
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                }
                this.parseRowStr(arrData[i]);
	        	strSql = "update TDzTypeCodePP" +
		        " set FCheckState = " + this.checkStateId +
		        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
		        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
		        "' where FHKcode = " + dbl.sqlString(this.strTransferCode) +
		        " and FTgr = " + dbl.sqlString(this.strTrustBank) +
		        " and FHKType = " + dbl.sqlString(this.strTransferType);
	            dbl.executeSql(strSql);
                }
        	}
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}

	}

	public void deleteRecycleData() throws YssException {
		String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
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
                    //SQL删除语句：从现金对价表中删除清除操作所对应的记录
                    strSql = "delete from TDzTypeCodePP" +
                        " where FHKcode = " + dbl.sqlString(this.strTransferCode);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("清除划款类型数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}

	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "update TDzTypeCodePP" +
        		" set FTgR = " + dbl.sqlString(this.strTrustBank) +
        		",FHKType = " + dbl.sqlString(this.strTransferType) +
        		",FHKcode = " + dbl.sqlString(this.strTransferCode) +
        		",FBwType = " + dbl.sqlString(this.strMessageType) +
        		",FDesc = " + dbl.sqlString(this.strDesc) +
        		" where FHKcode = " + dbl.sqlString(this.strOldTransferCode) +
    	        " and FTgr = " + dbl.sqlString(this.strOldTrustBank) +
    	        " and FHKType = " + dbl.sqlString(this.strOldTransferType);
        	
        	conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
		} catch (Exception e) {
			throw new YssException("修改出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

    /**
     * 通过拼接字符串来获取数据字符串
     * @return String
     */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(this.strTrustBank).append("\t");
		buf.append(this.strTransferType).append("\t");
		buf.append(this.strTransferCode).append("\t");
		buf.append(this.strMessageType).append("\t");
		buf.append(this.checkStateId).append("\t");
		buf.append(this.strCreator).append("\t");
		buf.append(this.strCheckUser).append("\t");
		buf.append(this.strCreateTime).append("\t");
		buf.append(this.strCheckedTime).append("\t");
		buf.append(this.strDesc).append("\t");
		buf.append(this.strOldTrustBank).append("\t");
		buf.append(this.strOldTransferType).append("\t");
		buf.append(this.strOldTransferCode).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}
	
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	/**
     * 解析前台发送来的字符串
     * @param sRowStr String
     * @throws YssException
     */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
        String sTmpStr = "";
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
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.strTrustBank = reqAry[0];
            this.strTransferType = reqAry[1];
            this.strTransferCode = reqAry[2];
            this.strMessageType = reqAry[3];
            this.checkStateId = Integer.parseInt(reqAry[4]);
            this.strCreator = reqAry[5];
            this.strCheckUser = reqAry[6];
            this.strCreateTime = reqAry[7];
            this.strCheckedTime = reqAry[8];
            this.strDesc = reqAry[9].replaceAll("【Enter】", "\r\n");;
            this.strOldTrustBank = reqAry[10];
            this.strOldTransferType = reqAry[11];
            this.strOldTransferCode = reqAry[12];
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new EleTransferBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        	} catch (Exception e) {
        		throw new YssException("解析划款类型数据信息出错", e);
        }
	}
	
	private String buildFilterSql() throws YssException {
		String sFilter = "";
		if(this.filterType != null) {
			sFilter = "where 1=1";
//			if(this.filterType.equals("1")) {
			if("1".equals(this.filterType.isOnlyColumns)) {//findbugs风险调整，比较对象的类型不附和  胡坤 20120613 需测试
				sFilter += " and 1 = 2";
				return sFilter;
			}
			if(this.filterType.strTransferCode.trim().length() != 0) {
				sFilter += " and a.FHKcode like '" +
						this.filterType.strTransferCode.replaceAll("'", "''") + "%'";
			}
			if(this.filterType.strTransferType.trim().length() != 0) {
				sFilter += " and a.FHKType like '" +
						this.filterType.strTransferType.replaceAll("'", "''") + "%'";
			}
			if(this.filterType.strTrustBank.trim().length() != 0) {
				sFilter += " and a.FTgR like '" +
						this.filterType.strTrustBank.replaceAll("'", "''") + "%'";
			}
			if(this.filterType.strMessageType.trim().length() != 0) {
				sFilter += "and a.FBwType like '" +
						this.filterType.strMessageType.replaceAll("'", "''") + "%'";
			}
		}
		return sFilter;
	}
	
	private void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
		this.strTrustBank = rs.getString("FTgR") + "";
		this.strTransferType = rs.getString("FHKType") + "";
		this.strTransferCode = rs.getString("FHKcode") + "";
		this.strMessageType = rs.getString("FBwType") + "";
		this.checkStateId = Integer.parseInt(rs.getString("FCheckState"));
		this.strCreator = rs.getString("FCreator") + "";
		this.strCheckUser = rs.getString("FCheckUser") + "";
		this.strCreateTime = rs.getString("FCreateTime") + "";
		this.strCheckedTime = rs.getString("FCheckTime") + "";
		this.strDesc = rs.getString("FDesc") + "";
		this.strOldTransferCode = this.strTransferCode + "";
		this.strOldTransferType = this.strTransferType + "";
		this.strOldTrustBank = this.strTrustBank + "";
		//super.setRecLog(rs);
	}
	
	public String getListViewData1() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	sHeader = this.getListView1Headers();
        	if (this.strIsOnlyColumns.equals("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
            }
        	
        	strSql = "select a.*,b.fusername,b.fusername as FCheckUserName,b.fusername as FCreatorName"
        			+ " from TDzTypeCodePP a "
        			+ " left join (select fusercode, fusername from "
					+ pub.yssGetTableName("Tb_Sys_UserList")
					+ " ) b on a.FCheckUser = b.fusercode "
        			+ buildFilterSql() +
        			" order by a.FCheckState, a.FCreateTime desc";
//        	System.out.println("strSql=" + strSql);
//        	yssPageInationBean.setsQuerySQL(strSql);
//            yssPageInationBean.setsTableName("TDzTypeCodePP");
            rs =dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FTgR")).append("\t");
                bufShow.append(rs.getString("FHKType")).append("\t");
                bufShow.append(rs.getString("FHKcode")).append("\t");
                bufShow.append(rs.getString("FBwType")).append("\t");
                bufShow.append(rs.getString("FCreator")).append("\t");
                bufShow.append(rs.getString("FCheckUser")).append("\t");
                bufShow.append(rs.getString("FCreateTime")).append("\t");
                bufShow.append(rs.getString("FCheckTime")).append("\t");
                bufShow.append(rs.getString("FDesc"));
                bufShow.append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                
//                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                append(YssCons.YSS_LINESPLITMARK);
//
//	            setResultSetAttr(rs);
//	            bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }

            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            this.getListView1ShowCols();// + "\r\f" + yssPageInationBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException("获取划款类型数据信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	public String getListView1ShowCols() {
		StringBuffer buf = new StringBuffer();
		buf.append("FTgR").append("\t");
        buf.append("FHKType").append("\t");
        buf.append("FHKcode").append("\t");
        buf.append("FBwType").append("\t");
        buf.append("FCreator").append("\t");
        buf.append("FCheckUser").append("\t");
        buf.append("FCreateTime").append("\t");
        buf.append("FCheckTime").append("\t");
        buf.append("FDesc");
        return buf.toString();
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

	 /**划拨类型设置小按钮的实现
     * @author zhouxiang 2010.09.26
     */
    public String getListViewData2() throws YssException {
    	 String sHeader = "";
         String sShowDataStr = "";
         String sAllDataStr = "";
         StringBuffer bufShow = new StringBuffer();
         StringBuffer bufAll = new StringBuffer();
         ResultSet rs = null;
         String strSql = "";
		try {
			//------ modify by wangzuochun 2011.05.23 BUG 1914 在划款类型维护界面上缺少划款指令名称字段
			sHeader = "托管行简称\t电子划拨代码\t划拨类型\t报文类型";// #50 电子指令需求变更，要求提供证券信息和基金信息2010.11.2 edited by zhouxiang 
			/**shashijie 2011.05.30 ,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
			strSql = "select * from TDzTypeCodePP m where fcheckstate = 1";
			/**end*/
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("ftgr") + "").trim()).append(
						"\t");
				bufShow.append((rs.getString("fhkcode") + "").trim()).append(
						"\t");
				bufShow.append((rs.getString("fhktype") + "").trim()).append(
						"\t");
				bufShow.append((rs.getString("fbwtype") + "").trim()).append(
						YssCons.YSS_LINESPLITMARK);    // #50 电子指令需求变更，要求提供证券信息和基金信息2010.11.2 edited by zhouxiang 
				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			//-------------------- BUG 1914 -------------------//
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		} catch (Exception e) {
			//------ modify by wangzuochun 2011.05.23 BUG 1914 在划款类型维护界面上缺少划款指令名称字段
			throw new YssException("获取划款类型数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }

}
