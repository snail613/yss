package com.yss.main.taoperation;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**liubo 20130514 STORY 3759 杠杆分级基金份额折算设置 */

public class LeverConversionBean extends BaseDataSettingBean implements IDataSetting 
{

    private String sPortCode = "";                    //组合代码
    private String sPortName = "";                    //组合名称
    private java.util.Date dConversionBaseDate = null;  //折算基准日
    private java.util.Date dConversionDate = null;      //折算日
    private String sConversionType = "0";             //折算类型
    private String sEachHandle = "0";                 //是否场内外份额分别处理
    private double dFloorShare = 0.0000;            //场内基础份额
    private double dOtcShare = 0.0000;              //场外基础份额
    private String sOldPortCode = "";

	private String sPortClsCode = "";//add by yeshenghong 20130724 story4151
    private String sPortClsName = "";//add by yeshenghong 20130724 story4151
    
    public String getsPortClsCode() {
		return sPortClsCode;
	}

	public void setsPortClsCode(String sPortClsCode) {
		this.sPortClsCode = sPortClsCode;
	}

	public String getsPortClsName() {
		return sPortClsName;
	}

	public void setsPortClsName(String sPortClsName) {
		this.sPortClsName = sPortClsName;
	}
	
    private java.util.Date dOldConversionDate = null;     
    private java.util.Date dOldConversionBaseDate = null;  
    
    public java.util.Date getOldConversionBaseDate() {
		return dOldConversionBaseDate;
	}

	public void setOldConversionBaseDate(java.util.Date dOldConversionBaseDate) {
		this.dOldConversionBaseDate = dOldConversionBaseDate;
	}

	public java.util.Date getOldConversionDate() {
		return dOldConversionDate;
	}

	public void setOldConversionDate(java.util.Date dOldConversionDate) {
		this.dOldConversionDate = dOldConversionDate;
	}

	public String getOldPortCode() {
		return sOldPortCode;
	}

	public void setsOldPortCode(String sOldPortCode) {
		this.sOldPortCode = sOldPortCode;
	}

	private String sRecycled = null; //保存未解析前的字符串
    private LeverConversionBean filterType;
    
	public String getPortCode() {
		return sPortCode;
	}

	public void setPortCode(String sPortCode) {
		this.sPortCode = sPortCode;
	}

	public String getPortName() {
		return sPortName;
	}

	public void setPortName(String sPortName) {
		this.sPortName = sPortName;
	}

	public java.util.Date getConversionBaseDate() {
		return dConversionBaseDate;
	}

	public void setConversionBaseDate(java.util.Date dConversionBaseDate) {
		this.dConversionBaseDate = dConversionBaseDate;
	}

	public java.util.Date getConversionDate() {
		return dConversionDate;
	}

	public void setConversionDate(java.util.Date sConversionDate) {
		this.dConversionDate = sConversionDate;
	}

	public String getConversionType() {
		return sConversionType;
	}

	public void setConversionType(String sConversionType) {
		this.sConversionType = sConversionType;
	}

	public String getEachHandle() {
		return sEachHandle;
	}

	public void setEachHandle(String sEachHandle) {
		this.sEachHandle = sEachHandle;
	}

	public double getFloorShare() {
		return dFloorShare;
	}

	public void setFloorShare(String sFloorShare) {
		this.dFloorShare = dFloorShare;
	}

	public double getOtcShare() {
		return dOtcShare;
	}

	public void setOtcShare(String sOtcShare) {
		this.dOtcShare = dOtcShare;
	}

	/**
	 * 增加数据
	 */
	public String addSetting() throws YssException {
		Connection con = dbl.loadConnection();
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        try {
            strSql = "insert into " + pub.yssGetTableName("TB_ta_LeverShare") +
                "(FPortCode,FBaseDate,FConversionDate,FConversionType,FEachHandle,FFloorShare,FOtcShare,FPORTCLSCODE," +//add by yeshenhgong 20130724 story4151
                " FCheckState,FCreator,FCreateTime)" +
                " values(" +
                dbl.sqlString(this.sPortCode) + "," +
                dbl.sqlDate(this.dConversionBaseDate) + "," +
                dbl.sqlDate(this.dConversionDate) + "," +
                dbl.sqlString(this.sConversionType) + "," +
                dbl.sqlString(this.sEachHandle) + "," +
                this.dFloorShare+","+
                this.dOtcShare+","+
                dbl.sqlString(this.sPortClsCode)+","+//add by yeshenhgong 20130724 story4151
                (pub.getSysCheckState() ? 0 : 1) + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + 
                ")";
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增杠杆分级份额折算设置信息出错：" + e.getMessage());
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";
	}

	/**
	 * 检查传入的数据是否已存在（主要检查主键数据）
	 */
	public void checkInput(byte btOper) throws YssException {

        dbFun.checkInputCommon(btOper, pub.yssGetTableName("TB_ta_LeverShare"),
                               "FPortCode,FBaseDate,FConversionDate",
                               this.sPortCode + "," + YssFun.formatDate(this.dConversionBaseDate) + "," + 
                               YssFun.formatDate(this.dConversionDate), 
                               this.sOldPortCode + "," + YssFun.formatDate(this.dOldConversionBaseDate) + "," +
                               YssFun.formatDate(this.dOldConversionDate));
		
	}

	/**
	 * 审核\反审核数据
	 */
	public void checkSetting() throws YssException {

        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("TB_ta_LeverShare") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FPortCode = " +dbl.sqlString(this.sPortCode) +
                        " and FBASEDATE = " + dbl.sqlDate(this.dConversionBaseDate) +
                        " and FCONVERSIONDATE = " + dbl.sqlDate(this.dConversionDate);
                    dbl.executeSql(strSql);
                }
            }
            else if (this.sPortCode != null && !sPortCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("TB_ta_LeverShare") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FSECURITYCODE = " +dbl.sqlString(this.sPortCode) +
                " and FBASEDATE = " + dbl.sqlDate(this.dConversionBaseDate) +
                " and FCONVERSIONDATE = " + dbl.sqlDate(this.dConversionDate);;
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核/反审核杠杆分级份额折算设置信息出错：" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	/**
	 * 将数据放入回收站
	 */
	public void delSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("TB_ta_LeverShare") +
                " set FCheckState = " + this.checkStateId +
                " where FPortCode = " + dbl.sqlString(this.sPortCode) +
                " and FBASEDATE = " + dbl.sqlDate(this.dConversionBaseDate) +
                " and FCONVERSIONDATE = " + dbl.sqlDate(this.dConversionDate);;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除杠杆分级份额折算设置信息出错：" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
		
	}

	/**
	 * 清除回收站中的数据（可以是多条）
	 */
	public void deleteRecycleData() throws YssException {
		boolean bTrans = false; 
        Connection conn = dbl.loadConnection();
		ResultSet rs = null;
        String[] arrData = null;
        String strSql = "";
		
		try {
	        conn.setAutoCommit(false);
            bTrans = true;
            
            if (sRecycled != null && !sRecycled.equals("")) {
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) 
                {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                strSql = "delete from " + pub.yssGetTableName("TB_ta_LeverShare") + " where FPortCode = "+
                dbl.sqlString(this.sPortCode) + "and FCHECKSTATE = 2" +
                " and FBASEDATE = " + dbl.sqlDate(this.dConversionBaseDate) +
                " and FCONVERSIONDATE = " + dbl.sqlDate(this.dConversionDate);;
                
                dbl.executeSql(strSql);
                }
            }
            
	        conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("删除杠杆分级份额折算设置信息出错：" + e.getMessage());
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	/**
	 * 修改数据
	 */
	public String editSetting() throws YssException {

		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("TB_ta_LeverShare") +
                " set FPortCode = " +
                dbl.sqlString(this.sPortCode) + ", FBaseDate = " +
                dbl.sqlDate(this.dConversionBaseDate) +", FConversionDate="+
                dbl.sqlDate(this.dConversionDate)+",FConversionType="+dbl.sqlString(this.sConversionType)
                + ", FEachHandle = " + dbl.sqlString(this.sEachHandle) 	
                + ", FFloorShare = " + this.dFloorShare + "," +
                " FOtcShare = " + this.dOtcShare + 
                ", FPORTCLSCODE = " + dbl.sqlString(this.sPortClsCode) +//add by yeshenhgong 20130724 story4151
                ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + 
                " where FPortCode = " +
                dbl.sqlString(this.sOldPortCode) +
                " and FBASEDATE = " + dbl.sqlDate(this.dOldConversionBaseDate) +
                " and FCONVERSIONDATE = " + dbl.sqlDate(this.dOldConversionDate);;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新杠杆分级份额折算设置信息出错：" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
	}

	public String getAllSetting() throws YssException {
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
		return null;
	}

	/**
	 * 将后台处理好的数据包装成为一个字符串返回给前台
	 */
	public String buildRowStr() throws YssException {

        StringBuilder buf = new StringBuilder();
        
        buf.append(this.sPortCode).append("\t");
        buf.append(this.sPortName).append("\t");
        buf.append(YssFun.formatDate(this.dConversionBaseDate)).append("\t");
        buf.append(YssFun.formatDate(this.dConversionDate)).append("\t");
        buf.append(this.sConversionType).append("\t");
        buf.append(this.sEachHandle).append("\t");
        buf.append(YssFun.formatNumber(this.dFloorShare, "#,##0.0000")).append("\t");
        buf.append(YssFun.formatNumber(this.dOtcShare, "#,##0.0000")).append("\t");
        buf.append(this.sPortClsCode).append("\t");//add by yeshenghong 20130724 story4151
        buf.append(this.sPortClsName).append("\t");//add by yeshenghong 20130724 story4151
        buf.append(super.buildRecLog());
        
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 解析前台传来的字符串
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
            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.sPortCode = reqAry[0];
            this.dConversionBaseDate = YssFun.toDate(reqAry[1]);
            this.dConversionDate = YssFun.toDate(reqAry[2]);
            this.sConversionType = reqAry[3];
            this.sEachHandle = reqAry[4];
            this.dFloorShare = YssFun.isNumeric(reqAry[5]) ? YssFun.toDouble(reqAry[5]) : 0.0000;
            this.dOtcShare = YssFun.isNumeric(reqAry[6]) ? YssFun.toDouble(reqAry[6]) : 0.0000;
            this.sOldPortCode = reqAry[7];
            this.dOldConversionBaseDate = YssFun.toDate(reqAry[8]);
            this.dOldConversionDate = YssFun.toDate(reqAry[9]);
            this.sPortClsCode = reqAry[10];//add by yeshenghong 20130724 story4151
            this.checkStateId = YssFun.toInt(reqAry[11]);
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new LeverConversionBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("杠杆分级份额折算设置出错：" + e.getMessage());
        }

	}
	
	/**
	 * 通过传入的ResultSet，为对象赋值
	 * @param rs 目标ResultSet
	 * @throws YssException
	 */
	public void setLeverAttr(ResultSet rs) throws YssException 
	{
		try
		{
			this.sPortCode = rs.getString("FPortCode");
			this.sPortName = rs.getString("FPortName");
			this.dConversionBaseDate = rs.getDate("FBaseDate");
			this.dConversionDate = rs.getDate("FConversionDate");
			this.sConversionType = rs.getString("FConversionType");
			this.sEachHandle = rs.getString("FEachHandle");
			this.dFloorShare = rs.getDouble("FFloorShare");
			this.dOtcShare = rs.getDouble("FOtcShare");
			this.sPortClsCode = rs.getString("FPortClsCode");//add by yeshenghong 20130724 story4151
			this.sPortClsName = rs.getString("FPortClsName");//add by yeshenghong 20130724 story4151
			super.setRecLog(rs);
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
	}
	
	/**
	 * 通过传入的Sql语句查询数据，并将查询结果进行包装，返回给前台
	 * @param strSql 传入的SQL语句
	 * @return	包装好的查询结果
	 * @throws YssException
	 */
	public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setLeverAttr(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	/**
	 * 杠杆分级基金份额折算设置主界面的查询方法，包括第一次进入主界面、筛选、刷新等功能
	 */
	public String getListViewData1() throws YssException {
		String strSql = "";
		
		strSql = "select a.*,b.FPortname,c.Fcreatorname,d.fcheckusername,p.FPORTCLSNAME " +
				 " from " + pub.yssGetTableName("tb_ta_levershare") + " a " +
				 " left join (select FPortCode,FPortName from " + pub.yssGetTableName("tb_para_portfolio") + 
				 " where FcheckState = 1) b on a.Fportcode = b.Fportcode " +
				 //add by yeshenghong 20130724 story4151  ---start
				 " left join (select FPORTCLSCODE,FPORTCLSNAME from " + pub.yssGetTableName("Tb_TA_PortCls") + 
				 " where FcheckState = 1) p on a.FPORTCLSCODE = p.FPORTCLSCODE " +
				 //add by yeshenghong 20130724 story4151 ---end
				 " left join (select FUserCode,FUserName as FCreatorName from tb_sys_userlist) c " +
				 " on a.Fcreator = c.fusercode " +
				 " left join (select FUserCode,FUserName as FCheckUserName from tb_sys_userlist) d " +
				 " on a.fcheckuser = d.fusercode" +
				 buildFilterSql() +
				 " order by a.FPortCode";
		
		return builderListViewData(strSql);
	}
	

	/**
	 * 设置筛选条件
	 * @return 用于执行筛选的where分支
	 * @throws YssException
	 */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.sPortCode != null && this.filterType.sPortCode.trim().length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.sPortCode.replaceAll("'", "''") + "%'";
            }
            //add by yeshenghong 20130724 story4151 ---start
            if (this.filterType.sPortClsCode != null && this.filterType.sPortClsCode.trim().length() != 0) {
                sResult = sResult + " and a.sPortClsCode like '" +
                    filterType.sPortClsCode.replaceAll("'", "''") + "%'";
            }
            //add by yeshenghong 20130724 story4151 ---end
            if (this.filterType.dConversionBaseDate != null && 
            		!(YssFun.formatDate(this.filterType.dConversionBaseDate).equals("9998-12-31"))) {
                sResult = sResult + " and FBaseDate = " + dbl.sqlDate(this.filterType.dConversionBaseDate);
            }
            if (this.filterType.dConversionDate != null &&
            		!(YssFun.formatDate(this.filterType.dConversionDate).equals("9998-12-31"))) {
                sResult += " and a.FConversionDate = " +
                    dbl.sqlDate(this.filterType.dConversionDate);
            }
            if (this.filterType.sConversionType != null && this.filterType.sConversionType.trim().length() != 0
            		&& !this.filterType.sConversionType.equals("4")) {
                sResult = sResult + " and a.FConversionType like '" +
                    filterType.sConversionType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.sEachHandle != null && this.filterType.sEachHandle.trim().length() != 0) {
                sResult = sResult + " and a.FEachHandle like '" +
                    filterType.sEachHandle.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.dFloorShare != 0)
            {
            	sResult = sResult + " and a.FFloorShare = " + this.filterType.dFloorShare;
            }
            if (this.filterType.dOtcShare != 0)
            {
            	sResult = sResult + " and a.FOtcShare = " + this.filterType.dOtcShare;
            }
        }
        return sResult;
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

}
