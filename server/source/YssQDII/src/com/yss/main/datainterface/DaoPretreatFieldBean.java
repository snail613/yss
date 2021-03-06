package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class DaoPretreatFieldBean
    extends BaseDataSettingBean implements IDataSetting {
    private String dPDsCode;
    private String dsField;
    private String targetField;
    private int orderIndex;
    private String oldDPDsCode;
    private String pretType = "";
    private String pretTypeName = "";
    private String springCode = "";
    private String springName = "";
    private DaoPretreatFieldBean filterType;
    
    /**add---shashijie 2013-2-26 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-26 STORY 3366*/
    
    public DaoPretreatFieldBean() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {
    }

    public void checkSetting() throws YssException {
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        //String[] sMutilRowAry = null;
        String[] sMutilRowStrAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        int num = 0;
        try {
        	/**add---shashijie 2013-2-26 STORY 3366  重构代码,增加对公共表的添加SQL语句,其他不变*/
            conn.setAutoCommit(false);
            bTrans = true;
            sMutilRowStrAry = sMutilRowStr.split("\f\f");
            //sMutilRowAry = sMutilRowStrAry[0].split(YssCons.YSS_LINESPLITMARK);
            this.parseRowStr(sMutilRowStrAry[0]);
            if(this.dPDsCode==null)//edited by zhouxiang MS01396    接口预处理字段配置界面，不设置数据源字段与目标字段的配置，在点击确定按钮后报错    
            	return null;
			else {
				//获取删除SQL
				strSql = getDeleteSql();
				dbl.executeSql(strSql);
				//新增SQL
				strSql = getInsertSql();
				pstmt = conn.prepareStatement(strSql);
				
				for (int i = 0; i < sMutilRowStrAry.length; i++) {
					// Sql="select max(FOrderIndex)as  FOrderIndex from "+pub.yssGetTableName("Tb_Dao_PretreatField");
					// rs= dbl.openResultSet(Sql); 20070920 陈一波修改
					// while(rs.next())
					// orderIndex=rs.getInt("FOrderIndex");
					num = i + 1;
					this.parseRowStr(sMutilRowStrAry[i]);
					//设置ps对象
					setPrepare(pstmt,this.dPDsCode,num,this.dsField,this.targetField,this.pretType,this.springCode,
							pub.getSysCheckState(),this.creatorCode,this.creatorTime);
					pstmt.executeUpdate();
				}
			}
            conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
            return "";
            /**end---shashijie 2013-2-26 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("保存预处理字段设置信息出错\r\n" + e.getMessage(), e);
        } finally {
        	/**add---shashijie 2013-2-26 STORY 3366 增加关闭的con对象*/
        	dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(conn, bTrans);
			/**end---shashijie 2013-2-26 STORY 3366 */
            //dbl.closeResultSetFinal(rs);//20070920  陈一波修改
        }
    }


	/**shashijie 2013-2-26 STORY 3366 */
	private void setPrepare(PreparedStatement ps, String dPDsCode, int num,
			String dsField, String targetField, String pretType,
			String springCode, boolean sysCheckState, String creatorCode,
			String creatorTime) throws Exception {
		if (ps == null) {
			return ;
		}
		ps.setString(1, dPDsCode);
		ps.setInt(2, num);
		ps.setString(3, dsField);
		ps.setString(4, targetField);
		ps.setInt(5, Integer.parseInt(pretType));
		ps.setString(6, springCode);
		ps.setInt(7, (sysCheckState ? 0 : 1));
		ps.setString(8, creatorCode);
		ps.setString(9, creatorTime);
		ps.setString(10, (sysCheckState ? " " : creatorCode));
	}

	/**shashijie 2013-2-26 STORY 3366 获取SQL */
	private String getInsertSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_PretreatField");
		} else {
			sql = " insert into Tb_Dao_PretreatField ";
		}
		sql += "(FDPDsCode, FOrderIndex, FDsField, FTargetField,"
			+ " FPretType,FSICode,"
			+ " FCheckState,FCreator,FCreateTime,FCheckUser)"
			+ " values (?,?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	/**shashijie 2013-2-26 STORY 3366 获取SQL */
	private String getDeleteSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " delete from " + pub.yssGetTableName("Tb_Dao_PretreatField");
		} else {
			sql = " delete from Tb_Dao_PretreatField ";
		}
		sql += " where FDPDsCode = " + dbl.sqlString(this.dPDsCode);
		return sql;
	}

	public IDataSetting getSetting() throws YssException { //add liyu 接口导出要用
        String strSql = "";
        ResultSet rs = null;
        try {
        	/**add---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	//组合群SQL
            strSql = getSelectByFDPDsCodeAndFTargetField(pub.yssGetTableName("Tb_Dao_PretreatField"),"0");
            
            strSql += " Union All ";
            //公共表SQL
        	strSql += getSelectByFDPDsCodeAndFTargetField("Tb_Dao_PretreatField","1");
            
            rs = dbl.openResultSet(strSql); //by ly 将其大写,防止出现大小写问题 080310
            while (rs.next()) {
            	//对象赋值
            	setResultSetAttr(rs);
            }
            /**end---shashijie 2013-2-25 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        } catch (Exception e) {
            throw new YssException("获取预处理字段出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;

    }

    /**shashijie 2013-2-26 STORY 3366*/
	private String getSelectByFDPDsCodeAndFTargetField(String tableName,String saveType) {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.FDPDSCODE,"+
			" a.FORDERINDEX,"+
			" a.FSICODE,"+
			" a.FPRETTYPE,"+
			" a.FDSFIELD,"+
			" a.FTARGETFIELD,"+
			" a.FDESC ,"+
			" a.FCREATOR ,"+
			" a.FCREATETIME,"+
			" a.FCHECKUSER,"+
			" a.FCHECKTIME,"+
			" a.FCHECKSTATE "+
  			" from " + tableName +" a where a.FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
        	" and upper(a.FTargetField)=" + dbl.sqlString(this.targetField.toUpperCase());
		return sql;
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

    private String buildFilterSql() {
        String reSql = "";
        if (this.filterType != null) {
            reSql = " where 1=1";
            if (this.filterType.dPDsCode != null && this.filterType.dPDsCode.length() != 0) {
                reSql += " and a.FDPDsCode = " + dbl.sqlString(filterType.dPDsCode); //modify 0918这里的参数必须为确切参数不能为模糊的
            }
        }
        return reSql;
    }

    private void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.dPDsCode = rs.getString("FDPDsCode");
        this.dsField = rs.getString("FDsField");
        this.orderIndex = dbl.isFieldExist(rs, "FOrderIndex") ? rs.getInt("FOrderIndex") : -1 ;
        this.targetField = rs.getString("FTargetField");
        this.pretType = rs.getString("FPretType");
        this.pretTypeName = dbl.isFieldExist(rs, "FPretTypeName") ? rs.getString("FPretTypeName") : "" ;
        this.springCode = rs.getString("FSICode");
        this.springName = dbl.isFieldExist(rs, "FSIName") ? rs.getString("FSIName") : "" ;
        /**add---shashijie 2013-2-25 STORY 3366 存储类型赋值*/
		setSaveTypeValue(rs);
		/**end---shashijie 2013-2-25 STORY 3366 */
    }

    /**shashijie 2013-2-18 STORY 3366 存储类型副职 */
	private void setSaveTypeValue(ResultSet rs) {
		try {
			String temp = rs.getString("saveType");
			if (temp.equalsIgnoreCase("公共")) {
				this.saveType = "1";
			} else {
				this.saveType = "0";
			}
		} catch (Exception e) {
			//默认是组合群数据,非选中状态
			this.saveType = "0";
		} finally {

		}
	}
	
    public String getListViewData1() throws YssException {
        int i = 0;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sqlStr = "";
        //String sVocStr = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            /**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            sqlStr = " Select * From ( ";
    		//组合群SQL
            sqlStr += getSelectList(pub.yssGetTableName("Tb_Dao_PretreatField"),"0");
            
            sqlStr += " Union All ";
            //公共表SQL
            sqlStr += getSelectList("Tb_Dao_PretreatField","1");
            
            sqlStr += " ) a Order By a.FOrderIndex ";
    		/**end---shashijie 2013-2-18 STORY 3366 */
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
            	//bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
            	//append(YssCons.YSS_LINESPLITMARK);
                bufShow.append(rs.getString("FDsField")).append("\t");
                bufShow.append(rs.getString("FTargetField")).append("\t");
                bufShow.append(rs.getString("FSICode")).append("\t")
                    .append(YssCons.
                            YSS_LINESPLITMARK);
                // bufShow.append(rs.getInt("FIsTotalInd")==1?"√":"").append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                i++;
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            //VocabularyBean vocabulary = new VocabularyBean();
            //vocabulary.setYssPub(pub);
            //sVocStr = vocabulary.getVoc(YssCons.YSS_FUNCTION);
            //sTabCellInfo = getTabCellInfo(i);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols(); //+"\r\f"+"voc"+sVocStr;
        } catch (Exception e) {
            throw new YssException("获取预处理字段设置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2013-2-26 STORY 3366 */
	private String getSelectList(String tableName,String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		// add dongqingsong 2013-05-30 bug 8044  将a.*改写成两个表对应的相同字段
		sql += " a.FDPDSCODE,a.FORDERINDEX,a.FDSFIELD,a.FTARGETFIELD,a.FPRETTYPE,a.FSICODE,b.FUserName as FCreatorName," +
				"c.FUserName as FCheckUserName," +
	    // end dongqingsong 2013-05-30 bug 8044     
		"n.FSIName as FSIName,m.Fvocname as FPretTypeName from " +
	        tableName + " a " +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FPretType") + " = m.FVocCode and m.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_DAO_PRETTYPE) +
	        " left join (select FSICode,FSIName from TB_FUN_SPINGINVOKE) n on a.FSICode=n.FSICode " +
	        buildFilterSql();
			//+" order by a.FOrderIndex ";
		return sql;
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
        String sTmpStr = "";
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.dPDsCode = reqAry[0];
            this.dsField = reqAry[1];
            this.targetField = reqAry[2];
            super.checkStateId = Integer.parseInt(reqAry[3]);
            this.oldDPDsCode = reqAry[4];
            this.pretType = reqAry[5];
            this.springCode = reqAry[6];
            /**add---shashijie 2013-2-26 STORY 3366 增加存储类型字段处理*/
            this.saveType = reqAry[7];
			/**end---shashijie 2013-2-26 STORY 3366*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoPretreatFieldBean();
                    ;
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析预处理字段设置信息出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dPDsCode).append("\t");
        buf.append(this.dsField).append("\t");
        buf.append(this.targetField).append("\t");
        buf.append(this.orderIndex).append("\t");
        buf.append(this.pretType).append("\t");
        buf.append(this.pretTypeName).append("\t");
        buf.append(this.springCode).append("\t");
        buf.append(this.springName).append("\t");
        /**add---shashijie 2013-2-25 STORY 3366 增加存储类型-公共表复选框*/
        buf.append(this.saveType.trim()).append("\t");
		/**end---shashijie 2013-2-25 STORY 3366*/
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    public void setDPDsCode(String dPDsCode) {
        this.dPDsCode = dPDsCode;
    }

    public void setDsField(String dsField) {
        this.dsField = dsField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public void setOldDPDsCode(String oldDPDsCode) {
        this.oldDPDsCode = oldDPDsCode;
    }

    public void setFilterType(DaoPretreatFieldBean filterType) {
        this.filterType = filterType;
    }

    public void setSpringName(String springName) {
        this.springName = springName;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }

    public void setPretType(String pretType) {
        this.pretType = pretType;
    }

    public void setPretTypeName(String pretTypeName) {
        this.pretTypeName = pretTypeName;
    }

    public String getDPDsCode() {
        return dPDsCode;
    }

    public String getDsField() {
        return dsField;
    }

    public String getTargetField() {
        return targetField;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getOldDPDsCode() {
        return oldDPDsCode;
    }

    public DaoPretreatFieldBean getFilterType() {
        return filterType;
    }

    public String getSpringName() {
        return springName;
    }

    public String getSpringCode() {
        return springCode;
    }

    public String getPretType() {
        return pretType;
    }

    public String getPretTypeName() {
        return pretTypeName;
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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

	/**add---shashijie 2013-2-26 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-26 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
}
