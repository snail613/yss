package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.DaoPretreatFieldBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;

public class RepPretreatFieldBean extends BaseDataSettingBean implements
		IDataSetting {

	//~Properties 
	private String dPDsCode;
    private String dsField;
    private String targetField;
    private int orderIndex;
    private String oldDPDsCode;
    private String pretType = "";
    private String pretTypeName = "";
    private String springCode = "";
    private String springName = "";
    private RepPretreatFieldBean filterType;
	
	public String getdPDsCode() {
		return dPDsCode;
	}

	public void setdPDsCode(String dPDsCode) {
		this.dPDsCode = dPDsCode;
	}

	public String getDsField() {
		return dsField;
	}

	public void setDsField(String dsField) {
		this.dsField = dsField;
	}

	public String getTargetField() {
		return targetField;
	}

	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public void setOrderIndex(int orderIndex) {
		this.orderIndex = orderIndex;
	}

	public String getOldDPDsCode() {
		return oldDPDsCode;
	}

	public void setOldDPDsCode(String oldDPDsCode) {
		this.oldDPDsCode = oldDPDsCode;
	}

	public String getPretType() {
		return pretType;
	}

	public void setPretType(String pretType) {
		this.pretType = pretType;
	}

	public String getPretTypeName() {
		return pretTypeName;
	}

	public void setPretTypeName(String pretTypeName) {
		this.pretTypeName = pretTypeName;
	}

	public String getSpringCode() {
		return springCode;
	}

	public void setSpringCode(String springCode) {
		this.springCode = springCode;
	}

	public String getSpringName() {
		return springName;
	}

	public void setSpringName(String springName) {
		this.springName = springName;
	}

	public RepPretreatFieldBean getFilterType() {
		return filterType;
	}

	public void setFilterType(RepPretreatFieldBean filterType) {
		this.filterType = filterType;
	}
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	 //~Constructors
    public RepPretreatFieldBean() {
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
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
        buf.append(super.buildRecLog());
        return buf.toString();
    }

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
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
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepPretreatFieldBean();
                    ;
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析预处理字段设置信息出错", e);
        }
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
	        this.orderIndex = rs.getInt("FOrderIndex");
	        this.targetField = rs.getString("FTargetField");
	        this.pretType = rs.getString("FPretType");
	        this.pretTypeName = rs.getString("FPretTypeName");
	        this.springCode = rs.getString("FSICode");
	        this.springName = rs.getString("FSIName");

	    }

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	

	public String getListViewData1() throws YssException {
        int i = 0;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "n.FSIName as FSIName,m.Fvocname as FPretTypeName from " +
                pub.yssGetTableName("Tb_Rep_PretreatField") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary m on " + dbl.sqlToChar("a.FPretType") + " = m.FVocCode and m.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_DAO_PRETTYPE) +
                " left join (select FSICode,FSIName from TB_FUN_SPINGINVOKE) n on a.FSICode=n.FSICode " +
                buildFilterSql() + " order by a.FOrderIndex ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FDsField")).append("\t");
                bufShow.append(rs.getString("FTargetField")).append("\t");
                bufShow.append(rs.getString("FSICode")).append("\t").append(YssCons.YSS_LINESPLITMARK);
                
                setResultSetAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                i++;
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols(); 
        } catch (Exception e) {
            throw new YssException("获取预处理字段设置信息出错", e);
        } finally {
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
	
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException { //add liyu 接口导出要用
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Rep_PretreatField") +
                " where FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
                " and upper(FTargetField)=" + dbl.sqlString(this.targetField.toUpperCase());
            rs = dbl.openResultSet(strSql); //by ly 将其大写,防止出现大小写问题 080310
            while (rs.next()) {
                this.dPDsCode = rs.getString("FDPDsCode");
                this.springCode = rs.getString("FSICode");
                this.pretType = rs.getString("FPretType");
                this.dsField = rs.getString("FDsField");
                this.targetField = rs.getString("FTargetField");
                this.springCode = rs.getString("FSiCode");
            }
        } catch (Exception e) {
            throw new YssException("获取预处理字段出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;

    }

   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowStrAry = null;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        int num = 0;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            sMutilRowStrAry = sMutilRowStr.split("\f\f");
            this.parseRowStr(sMutilRowStrAry[0]);
            strSql = "delete from " + pub.yssGetTableName("Tb_Rep_PretreatField") +
                " where FDPDsCode = " +
                dbl.sqlString(this.dPDsCode);
            dbl.executeSql(strSql);

            strSql = "insert into " + pub.yssGetTableName("Tb_Rep_PretreatField") +
                "(FDPDsCode, FOrderIndex, FDsField, FTargetField," +
                " FPretType,FSICode," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values (?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(strSql);
            for (int i = 0; i < sMutilRowStrAry.length; i++) {
                num = i + 1;
                this.parseRowStr(sMutilRowStrAry[i]);
                pstmt.setString(1, this.dPDsCode);
                pstmt.setInt(2, num);
                pstmt.setString(3, this.dsField);
                pstmt.setString(4, this.targetField);
                pstmt.setInt(5, Integer.parseInt(pretType));
                pstmt.setString(6, this.springCode);
                pstmt.setInt(7, (pub.getSysCheckState() ? 0 : 1));
                pstmt.setString(8, this.creatorCode);
                pstmt.setString(9, this.creatorTime);
                pstmt.setString(10,
                                (pub.getSysCheckState() ? " " : this.creatorCode));
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (SQLException e) {
            throw new YssException("保存预处理字段设置信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }

    }


	public void delSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}



	


}
