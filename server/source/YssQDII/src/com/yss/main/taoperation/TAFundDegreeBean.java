package com.yss.main.taoperation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2011-10-17 STORY 1589 TA多CLASS基金份额成本设置 实体bean*/
public class TAFundDegreeBean
    extends BaseDataSettingBean implements IDataSetting {

	private String FPortClsCode = "";//组合分级代码
	private String FPortClsName = "";//组合分级名称
	private String FPortCode = "";//组合代码
	private String FPortName = "";//组合名称
	private String FCuryCode = "";//销售货币
	private String FCuryName = "";//币种名称
	private double FDegreeCost = 0;//基金份额成本
	
	private String oldFPortClsCode = "";//组合分级代码
	private String oldFPortCode = "";//组合代码
	private String oldFCuryCode = "";//销售货币
	
    private TAFundDegreeBean filterType;
    private String sRecycled = null; //保存未解析前的字符串
    
    private Date dStartDate = null;		//20120611 added by liubo.启用日期
    private Date dOldStartDate = null;	//20120611 added by liubo.启用日期

    public TAFundDegreeBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_TA_ClassFundDegree"),
        					   //主键key
                               "FPortClsCode,FPortCode,FCuryCode,FStartDate",
                               //新值
                               this.FPortClsCode+","+this.FPortCode+","+this.FCuryCode + "," + YssFun.formatDate(dStartDate),
                               //旧值
                               this.oldFPortClsCode+","+this.oldFPortCode+","+this.oldFCuryCode + "," + YssFun.formatDate(dOldStartDate));
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " insert into " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
                " (FPortClsCode,FPortCode,FCuryCode,FDegreeCost,FStartDate,FCheckState,FCreator,FCreateTime) values("
                + dbl.sqlString(this.FPortClsCode) + ","
                + dbl.sqlString(this.FPortCode) + ","
                + dbl.sqlString(this.FCuryCode) + ","
                + this.FDegreeCost + ","
                + dbl.sqlDate(dStartDate) + ","
                + (pub.getSysCheckState() ? "0" : "1") + ","
                + dbl.sqlString(this.creatorCode) + ","
                + dbl.sqlString(this.creatorTime) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("增加基金份额成本设置设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
                " set FPortClsCode =" + dbl.sqlString(this.FPortClsCode) + "," +
                " FPortCode = " + dbl.sqlString(this.FPortCode) + "," +
                " FCuryCode = " + dbl.sqlString(this.FCuryCode) + "," +
                " FDegreeCost =" + this.FDegreeCost + "," +
                " FStartDate = " + dbl.sqlDate(dStartDate) + "," +
                " FCheckState =" + this.checkStateId + ","+
                " FCreateTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCreator = " + dbl.sqlString(pub.getUserCode()) +
                " where FPortClsCode =" + dbl.sqlString(this.oldFPortClsCode) +
                " and FPortCode = "+dbl.sqlString(this.oldFPortCode)+
                " and FCuryCode = "+dbl.sqlString(this.oldFCuryCode)+
                " and FStartDate = " + dbl.sqlDate(this.dOldStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改基金份额成本设置设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;

    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = " update " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
                " set FCheckState=" + this.checkStateId +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "', FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FPortClsCode =" + dbl.sqlString(this.oldFPortClsCode) +
                " and FPortCode = "+dbl.sqlString(this.oldFPortCode)+
                " and FCuryCode = "+dbl.sqlString(this.oldFCuryCode)+
            /**Start 20131012 added by liubo.Bug #81018.QDV4海富通2013年10月12日01_B
             * 清除条件增加启用日期，避免一次删除操作将所有相同组合和分级组合的记录全部删掉*/
                " and FStartDate = " + dbl.sqlDate(this.dOldStartDate);
            /**End 20131012 added by liubo.Bug #81018.QDV4海富通2013年10月12日01_B*/
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除基金份额成本设置设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void checkSetting() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            bTrans = true;
            conn.setAutoCommit(false);
			/**shashijie 2012-7-2 STORY 2475 */
            if (this.sRecycled != null || !this.sRecycled.equalsIgnoreCase("")) {
			/**end*/
                arrData = this.sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = " update " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
                        " set FCheckState=" + this.checkStateId +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "', FCheckUser = " +
                        dbl.sqlString(pub.getUserCode()) + 
                        " where FPortClsCode =" + dbl.sqlString(this.FPortClsCode) +
                        " and FPortCode = "+dbl.sqlString(this.FPortCode)+
                        " and FCuryCode = "+dbl.sqlString(this.FCuryCode) +
                        " and FStartDate = " + dbl.sqlDate(dStartDate);
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("TA基金份额成本设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
	            " where FPortClsCode =" + dbl.sqlString(this.oldFPortClsCode) +
	            " and FPortCode = "+dbl.sqlString(this.oldFPortCode)+
	            " and FCuryCode = "+dbl.sqlString(this.oldFCuryCode);
            rs = dbl.openResultSet(strSql);
            if(rs.next()){
            	this.FPortClsCode = rs.getString("FPortClsCode");
                this.FPortCode = rs.getString("FPortCode");
                this.FCuryCode = rs.getString("FCuryCode");
                this.FDegreeCost = rs.getDouble("FDegreeCost");
            }
            rs.close();
        } catch (Exception e) {
            throw new YssException(e.toString());
        }finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }
    
    public String getAllSetting() throws YssException {
        return "";
    }
    
    private void setSellTypeAttr(ResultSet rs) throws SQLException, YssException {
        this.FPortClsCode = rs.getString("FPortClsCode");
        this.FPortClsName = rs.getString("FPortClsName");
        this.FPortCode = rs.getString("FPortCode");
        this.FPortName = rs.getString("FPortName");
        this.FCuryCode = rs.getString("FCuryCode");
        this.FCuryName = rs.getString("FCuryName");
        this.FDegreeCost = rs.getDouble("FDegreeCost");
        this.dStartDate = rs.getDate("FStartDate");		 //20120611 added by liubo.启用日期
        super.setRecLog(rs);
    }
    
    private String FilterStr() {
        String str = "";
        if (this.filterType != null) {
            str = " where 1=1 ";
            if (this.filterType.FPortClsCode != null &&
            		this.filterType.FPortClsCode.length() > 0) {
                str += " and a.FPortClsCode like '" +
                    this.filterType.FPortClsCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.FPortCode != null &&
            		this.filterType.FPortCode.length() > 0) {
                str += " and a.FPortCode like '" +
                    this.filterType.FPortCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.FCuryCode != null && 
            		this.filterType.FCuryCode.length() > 0) {
                str += " and a.FCuryCode like '" +
                    this.filterType.FCuryCode.replaceAll("'", "''") + "%'";
            }
            //成本金额并非延迟天数,所以!=99不用判断
            if (this.filterType.FDegreeCost != 0) {
                str += " and FDegreeCost = " + this.filterType.FDegreeCost;
            }
        }
        return str;
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

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                " select a.*,b.FPortClsName,c.FPortName,d.fcuryname,e.FUserName as FCreatorName," +
                " f.FUserName as FCheckUserName From "+
                pub.yssGetTableName("Tb_TA_ClassFundDegree")+" a left join " +
                " (select b1.FPortClsCode,b1.FPortClsName From " +
                pub.yssGetTableName("Tb_TA_PortCls")+" b1 where b1.FCheckState = 1) b " +
                " on a.FPortClsCode = b.FPortClsCode left join (select c1.FPortCode,c1.FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio")+" c1 where c1.FCheckState = 1) c on a.FPortCode = c.FPortCode"+
                " left join (select d1.FCuryCode,d1.FCuryName from "+pub.yssGetTableName("Tb_Para_Currency")+
                " d1 where d1.FCheckState = 1) d on a.FCuryCode = d.FCuryCode "+
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) e on a.FCreator = e.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) f on a.FCheckUser = f.FUserCode" +
                this.FilterStr() +
                " order by a.FCheckState ,a.FCheckTime desc,a.FCreateTime desc ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSellTypeAttr(rs);
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
            //公共获取常用词汇类
            /*VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_TDT_CASHIND + "," +
                                        YssCons.YSS_TDT_AMOUNTIND);*/

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取TA基金份额成本设置设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
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
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.FPortClsCode = reqAry[0];
            this.FPortCode = reqAry[1];
            this.FCuryCode = reqAry[2];
            if (reqAry[3].length() > 0) {
                this.FDegreeCost = Double.valueOf((reqAry[3])).doubleValue();
            }
            this.checkStateId = Integer.parseInt(reqAry[4]);           
            this.oldFPortClsCode =  reqAry[5];
            this.oldFPortCode = reqAry[6];          
            this.oldFCuryCode = reqAry[7];
            this.dOldStartDate = YssFun.toDate(reqAry[8]);
            this.dStartDate = YssFun.toDate(reqAry[9]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new TAFundDegreeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析TA基金份额成本设置设置出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FPortClsCode).append("\t");
        buf.append(this.FPortClsName).append("\t");
        buf.append(this.FPortCode).append("\t");
        buf.append(this.FPortName).append("\t");
        buf.append(this.FCuryCode).append("\t");
        buf.append(this.FCuryName).append("\t");
        buf.append(this.FDegreeCost).append("\t");
        buf.append(YssFun.formatDate(dStartDate)).append("\t");		//20120611 added by liubo.启用日期
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        String[] arrData = null;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null || ! ("").equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData.length == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " + pub.yssGetTableName("Tb_TA_ClassFundDegree") +
	                    " where FPortClsCode =" + dbl.sqlString(this.FPortClsCode) +
	    	            " and FPortCode = "+dbl.sqlString(this.FPortCode)+
	    	            " and FCuryCode = "+dbl.sqlString(this.FCuryCode)+
	    	        /**Start 20131012 added by liubo.Bug #81018.QDV4海富通2013年10月12日01_B
	    	        * 清除条件增加启用日期，避免一次删除操作将所有相同组合和分级组合的记录全部删掉*/
	    	            " and FStartDate = " + dbl.sqlDate(this.dStartDate);
                    /**End 20131012 added by liubo.Bug #81018.QDV4海富通2013年10月12日01_B*/
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (NullPointerException ex) {
            throw new YssException(ex.getMessage()); 
        } catch (Exception e) {
            throw new YssException("TA基金份额成本设置清除出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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

	public String getFPortClsCode() {
		return FPortClsCode;
	}

	public void setFPortClsCode(String fPortClsCode) {
		FPortClsCode = fPortClsCode;
	}

	public String getFPortClsName() {
		return FPortClsName;
	}

	public void setFPortClsName(String fPortClsName) {
		FPortClsName = fPortClsName;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	public String getFPortName() {
		return FPortName;
	}

	public void setFPortName(String fPortName) {
		FPortName = fPortName;
	}

	public String getFCuryCode() {
		return FCuryCode;
	}

	public void setFCuryCode(String fCuryCode) {
		FCuryCode = fCuryCode;
	}

	public String getFCuryName() {
		return FCuryName;
	}

	public void setFCuryName(String fCuryName) {
		FCuryName = fCuryName;
	}

	public double getFDegreeCost() {
		return FDegreeCost;
	}

	public void setFDegreeCost(double fDegreeCost) {
		FDegreeCost = fDegreeCost;
	}

	public String getOldFPortClsCode() {
		return oldFPortClsCode;
	}

	public void setOldFPortClsCode(String oldFPortClsCode) {
		this.oldFPortClsCode = oldFPortClsCode;
	}

	public String getOldFPortCode() {
		return oldFPortCode;
	}

	public void setOldFPortCode(String oldFPortCode) {
		this.oldFPortCode = oldFPortCode;
	}

	public String getOldFCuryCode() {
		return oldFCuryCode;
	}

	public void setOldFCuryCode(String oldFCuryCode) {
		this.oldFCuryCode = oldFCuryCode;
	}
}
