package com.yss.main.datainterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * 自定义接口的文件筛选条件设置
 * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
 * @author liyu
 *
 */
public class DaoFileFilterBean extends BaseDataSettingBean implements IDataSetting{

	private String sCusCfgCode="";
	private String sOldCusCfgCode="";
	private String sFieldCode="";
	private String sFieldType="";
	private String sFilterType="";
	private String sFieldTypeName="";
	private String sFilterContent="";
	private String sRelation="";
	private String sDesc="";
	private DaoFileFilterBean filterType=null;
	
	/**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-17 STORY 3366*/
	
	public String getsCusCfgCode() {
		return sCusCfgCode;
	}

	public void setsCusCfgCode(String sCusCfgCode) {
		this.sCusCfgCode = sCusCfgCode;
	}

	public String getsOldCusCfgCode() {
		return sOldCusCfgCode;
	}

	public void setsOldCusCfgCode(String sOldCusCfgCode) {
		this.sOldCusCfgCode = sOldCusCfgCode;
	}

	public String getsFieldCode() {
		return sFieldCode;
	}

	public void setsFieldCode(String sFieldCode) {
		this.sFieldCode = sFieldCode;
	}

	public String getsFieldType() {
		return sFieldType;
	}

	public void setsFieldType(String sFieldType) {
		this.sFieldType = sFieldType;
	}

	public String getsFilterType() {
		return sFilterType;
	}

	public void setsFilterType(String sFilterType) {
		this.sFilterType = sFilterType;
	}

	public String getsFieldTypeName() {
		return sFieldTypeName;
	}

	public void setsFieldTypeName(String sFieldTypeName) {
		this.sFieldTypeName = sFieldTypeName;
	}

	public String getsFilterContent() {
		return sFilterContent;
	}

	public void setsFilterContent(String sFilterContent) {
		this.sFilterContent = sFilterContent;
	}

	public String getsRelation() {
		return sRelation;
	}

	public void setsRelation(String sRelation) {
		this.sRelation = sRelation;
	}

	public String getsDesc() {
		return sDesc;
	}

	public void setsDesc(String sDesc) {
		this.sDesc = sDesc;
	}
	
	public String addSetting() throws YssException {
		
		return null;
	}

	public void checkInput(byte btOper) throws YssException {
		
		
	}

	public void checkSetting() throws YssException {
		
		
	}

	public void delSetting() throws YssException {
		
		
	}

	public void deleteRecycleData() throws YssException {
		
		
	}

	public String editSetting() throws YssException {
		
		return null;
	}

	public String getAllSetting() throws YssException {
		
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		
		String strSql = ""; 
        ResultSet rs = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Dao_FileFilter") +
                " where FCusCfgCode=" + dbl.sqlString(this.sCusCfgCode) + " and FFieldCode=" +
                dbl.sqlString(this.sFieldCode) +
                " and FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.sCusCfgCode = rs.getString("FCusCfgCode");
                this.sFieldCode = rs.getString("FFieldCode");
                this.sFieldType = rs.getString("FFieldType");
                this.sFilterContent = rs.getString("FContent");
                this.sDesc = rs.getString("FDesc");
            }
        } catch (Exception e) {
            throw new YssException("获取文件筛选条件信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		
		String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        //boolean bTrans = false;
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            /**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //删除旧数据SQL
            sql = getDeleteByCode();
            dbl.executeSql(sql);
            //增加SQL语句
            sql = getInsertSql();
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
            	if(sMutilRowAry[i].trim().length()==0)continue;
                this.parseRowStr(sMutilRowAry[i]);
                if (this.sCusCfgCode.trim().length() > 0) {
                	//设置对象赋值
                	setPstmt(pstmt,this.sCusCfgCode,this.sFieldCode,this.sFieldType,this.sFilterContent,this.sDesc,
                			pub.getSysCheckState(),this.creatorCode,this.creatorTime);
                    
                    pstmt.executeUpdate();
                }
            }
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception ex) {
            throw new YssException("保存文件筛选条件设置信息出错\r\n" + ex.getMessage(),ex);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
	}

	/**shashijie 2013-2-18 STORY 3366 设置对象*/
	private void setPstmt(PreparedStatement ps, String sCusCfgCode,
			String sFieldCode, String sFieldType, String sFilterContent,
			String sDesc, boolean sysCheckState, String creatorCode,
			String creatorTime) throws Exception {
		ps.setString(1, sCusCfgCode);
        ps.setString(2, sFieldCode);
        ps.setString(3, sFieldType);
        ps.setString(4, sFilterContent);
        ps.setString(5, sDesc);
        ps.setInt(6, (sysCheckState ? 0 : 1));
        ps.setString(7, creatorCode);
        ps.setString(8, creatorTime);
        ps.setString(9, (sysCheckState ? " " : creatorCode));
	}

	/**shashijie 2013-2-18 STORY 3366 获取SQL */
	private String getInsertSql() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (this.saveType.equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_FileFilter");
		} else {
			sql = " insert into Tb_Dao_FileFilter ";
		}
		sql += "(FCusCfgCode,FFieldCode," +
	        " FFieldType,FContent,FDesc," +
	        " FCheckState,FCreator,FCreateTime,FCheckUser)" +
	        " values(?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	/**shashijie 2013-2-18 STORY 3366 获取SQL */
	private String getDeleteByCode() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (this.saveType.equals("0")) {
			sql = " delete from " + pub.yssGetTableName("Tb_Dao_FileFilter");
		} else {
			sql = " delete from Tb_Dao_FileFilter ";
		}
		sql += " where FCusCfgCode In (" + operSql.sqlCodes(this.sCusCfgCode+","+this.sOldCusCfgCode) + " ) ";
		return sql;
	}

	public String getBeforeEditData() throws YssException {
		
		return null;
	}

	public String buildRowStr() throws YssException {
		
		StringBuffer buf = new StringBuffer();
        buf.append(this.sCusCfgCode).append("\t");
        buf.append(this.sFieldCode).append("\t");
        buf.append(this.sFieldType).append("\t");
        buf.append(this.sFilterContent).append("\t");
        buf.append(this.sDesc).append("\t");
        buf.append(this.sFieldTypeName).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		
		return null;
	}

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
            /**add---shashijie 2013-2-19 STORY 3366 增加非空判断 */
            if(reqAry[0] != null && reqAry[0].trim().length() >0){
            	this.sCusCfgCode = reqAry[0];
            }
			/**end---shashijie 2013-2-19 STORY 3366 */
            this.sFieldCode = reqAry[1];
            this.sFieldType = reqAry[2];
            this.sFilterContent = reqAry[3];
            this.sDesc = reqAry[4];
            if(YssFun.isNumeric(reqAry[5]))
            	this.checkStateId = YssFun.toInt(reqAry[5]);
            this.sOldCusCfgCode = reqAry[6];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoFileFilterBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析文件筛选条件信息出错");
        }
	}

	public String getListViewData1() throws YssException {
		String strSql = "";
    	/**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
		//组合群SQL
        strSql = getSelectList("0");
        
        strSql += " Union ";
        //公共表SQL
        strSql += getSelectList("1");
		/**end---shashijie 2013-2-18 STORY 3366 */
		return builderListViewData(strSql);
	}

	/**shashijie 2013-2-18 STORY 3366 获取SQL */
	private String getSelectList(String saveType) throws YssException {
		String sql = 
			" select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,f.FVocName as FFieldTypeName from ";
		//若非选中处理组合群表,反之处理公共表
		if (saveType.equals("0")) {
			sql += pub.yssGetTableName("Tb_Dao_FileFilter");
		} else {
			sql += " Tb_Dao_FileFilter ";
		}
		sql += " a "
			+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "
			+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
			+ " left join Tb_Fun_Vocabulary f on a.Ffieldtype = f.FVocCode and f.FVocTypeCode='"+
			YssCons.YSS_FUN_RETURNTYPE+"'"+
			buildFilterSql()+ " ";
			//" order by a.FCusCfgCode,a.FFieldCode";
		return sql;
	}

	public String getListViewData2() throws YssException {
		
		return null;
	}

	public String getListViewData3() throws YssException {
		
		String strSql =
            "select * from " + pub.yssGetTableName("Tb_Dao_FileFilter") +
            " where 1=2"; //只是获取表头
		return builderListViewData(strSql);
	}

	public String getListViewData4() throws YssException {
		
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		
		return null;
	}

	public String getTreeViewData1() throws YssException {
		
		return null;
	}

	public String getTreeViewData2() throws YssException {
		
		return null;
	}

	public String getTreeViewData3() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		
		return null;
	}
	
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
                setDaoFileFilterAttr(rs);
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

            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);

            sVocStr = vocabulary.getVoc(YssCons.YSS_INFACE_FILTERTYPE+","+YssCons.YSS_INFACE_RELATION+","+YssCons.YSS_FUN_RETURNTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1 and a.FCheckState<>2";
            if (this.filterType.sCusCfgCode.length() != 0) {
                sResult = sResult + " and a.FCusCfgCode = '" +
                    filterType.sCusCfgCode.replaceAll("'", "''") + "'"; 
            }
        }
        return sResult;
    }
	
	private void setDaoFileFilterAttr(ResultSet rs )throws YssException{
		try{
			this.sCusCfgCode = rs.getString("FCusCfgCode");
			this.sFieldCode = rs.getString("FFieldCode");
			this.sFieldType = rs.getString("FFieldType");
			this.sFieldTypeName =rs.getString("FFieldTypeName");
			this.sFilterContent = rs.getString("FContent");
			this.sDesc = rs.getString("FDesc");		
		}catch(Exception ex){
			throw new YssException(ex);
		}
	}

	/**add---shashijie 2013-2-18 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-18 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

}
