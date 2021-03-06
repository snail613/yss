package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.CLOB;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/***************************************************************
 * #352 希望实施人员能够自行添加所需函数，并在数据源中进行调用 2011.05.20
 * 
 * @author jiangshichao
 * 
 */
public class RepFunction extends BaseDataSettingBean implements IDataSetting {

	private String repFuncCode = "";
	private String repFuncName = "";
	private String repFuncType; // 函数类型(自定义类型：0 系统内部函数：1)
	private String repFuncTypeName= "";
	private String sDesc = "";
	private String sDataSource = ""; // 函数数据源

	private String sOldrepFuncCode="";
	private RepFunction FilterType = null;
	// ---------------------------------------------------------------------------------//

	public String getRepFunctionCode() {
		return repFuncCode;
	}

	public void setRepFunctionCode(String repFunctionCode) {
		this.repFuncCode = repFunctionCode;
	}

	public String getRepFunctionName() {
		return repFuncName;
	}

	public void setRepFunctionName(String repFunctionName) {
		this.repFuncName = repFunctionName;
	}

	public String getRepFunctionType() {
		return repFuncType;
	}

	public void setRepFunctionType(String repFunctionType) {
		this.repFuncType = repFunctionType;
	}

	public String getsDesc() {
		return sDesc;
	}

	public void setsDesc(String sDesc) {
		this.sDesc = sDesc;
	}

	public String getsDataSource() {
		return sDataSource;
	}

	public void setsDataSource(String sDataSource) {
		this.sDataSource = sDataSource;
	}
	
	public String getRepFuncTypeName() {
		return repFuncTypeName;
	}

	public void setRepFuncTypeName(String repFuncTypeName) {
		this.repFuncTypeName = repFuncTypeName;
	}

	public String getsOldrepFuncCode() {
		return sOldrepFuncCode;
	}

	public void setsOldrepFuncCode(String sOldrepFuncCode) {
		this.sOldrepFuncCode = sOldrepFuncCode;
	}

	public RepFunction getFilter() {
		return FilterType;
	}

	public void setFilter(RepFunction filter) {
		this.FilterType = filter;
	}
	
	//-----------------------------------------------------------------------//

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
            this.repFuncCode = reqAry[0];
            this.repFuncName = reqAry[1];
            this.repFuncType = reqAry[2];
            this.sDataSource = reqAry[3];
            this.sDesc = reqAry[4];
            this.sOldrepFuncCode = reqAry[5];
            this.checkStateId = YssFun.toInt(reqAry[6]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.FilterType == null) {
                    this.FilterType = new RepFunction();
                    this.FilterType.setYssPub(pub);
                }
                this.FilterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析报表函数信息出错", e);
        }

	}
	
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.repFuncCode).append("\t");
        buf.append(this.repFuncName).append("\t");
        buf.append(this.repFuncTypeName).append("\t");
        buf.append(this.sDesc).append("\t");
        buf.append(this.repFuncType).append("\t");
        buf.append(this.sDataSource.equalsIgnoreCase("EMPTY_CLOB()")?"":this.sDataSource).append("\t");
        

        buf.append(super.buildRecLog());
        return buf.toString();
	}
	
	public String getOperValue(String sType) throws YssException {
		
		if(sType.equalsIgnoreCase("checkFuncExist")){
			return checkFuncExist();
		}
		if(sType.equalsIgnoreCase("dealFunc")){
			return dealFunc();
		}
		
		return null;
	}
	
	public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.repFuncCode = rs.getString("FREPFUNCTIONCODE") + "";
        this.repFuncName = rs.getString("FREPFUNCTIONNAME") + "";
        this.repFuncType = rs.getInt("FFUNCTIONTYPE") + "";
        this.repFuncTypeName = rs.getString("FFUNCTIONName")+"";
        this.sDataSource = dbl.clobStrValue(rs.getClob("FDataSource")).replaceAll("\t", "   ");
        this.sDesc = rs.getString("FDesc") + "";
        
    }
	
	private String buildFilterSql() {
        String sResult = "";
        if (this.FilterType != null) {
            sResult = " where 1=1";
            if (this.FilterType.repFuncCode.length() != 0) {
                sResult = sResult + " and a.FREPFUNCTIONCODE like '" +
                    this.FilterType.repFuncCode.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.repFuncName.length() != 0) {
                sResult = sResult + " and a.FREPFUNCTIONNAME like '" +
                    this.FilterType.repFuncName.replaceAll("'", "''") + "%'";
            }
            if (this.FilterType.sDesc.length() != 0) {
                sResult = sResult + " and a.FDesc like '" +
                    this.FilterType.sDesc.replaceAll("'", "''") + "%'";
            }
            if (!this.FilterType.repFuncType.equalsIgnoreCase("99") && this.FilterType.repFuncType.length() != 0) {
                sResult = sResult + " and a.FFUNCTIONTYPE = " + this.FilterType.repFuncType;
            }
        }

        return sResult;
    }
	
	
	public String addSetting() throws YssException {
		Connection conn = dbl.loadConnection();
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
		PreparedStatement pst = null;
		//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		try {
			conn.setAutoCommit(false);
			bTrans = true;
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			pst = conn
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
					.prepareStatement("insert into "
							+ pub.yssGetTableName("Tb_REP_FUNCTION")
							+ "(FREPFUNCTIONCODE,FREPFUNCTIONNAME,FFUNCTIONTYPE,FDATASOURCE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
							+ " values(?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, this.repFuncCode);
			pst.setString(2, this.repFuncName);
			pst.setString(3, this.repFuncType);
			pst.setString(4, "EMPTY_CLOB()");
			pst.setString(5, this.sDesc);
			pst.setString(6, "1");
			pst.setString(7, this.creatorCode);
			pst.setString(8, this.creatorTime);
			pst.setString(9, this.creatorCode);
			pst.setString(10, this.creatorTime);
			pst.executeUpdate();
			
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(pst);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			
			strSql = "select * from " + pub.yssGetTableName("Tb_REP_FUNCTION")
					+ " where FREPFUNCTIONCODE ="
					+ dbl.sqlString(this.repFuncCode);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
				  // modify by jsc 20120809 连接池对大对象的特殊处理
				//CLOB clob = ((oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
				CLOB clob =   dbl.CastToCLOB(rs.getClob("FDataSource"));
				clob.putString(1, this.sDataSource);
				strSql = "update " + pub.yssGetTableName("Tb_REP_FUNCTION")
						+ " set FDataSource = ? where FREPFUNCTIONCODE="
						+ dbl.sqlString(this.repFuncCode);
				pst = conn.prepareStatement(strSql);
				pst.setClob(1, clob);
				pst.executeUpdate();
				pst.close();
			}
			dbl.closeResultSetFinal(rs);// 释放结果集
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("增加报表函数设置出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeStatementFinal(pst);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
		return null;
	}

	
	public void checkInput(byte btOper) throws YssException {
		if(btOper == YssCons.OP_DEL){
			return;
		}
		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_REP_FUNCTION"), "FREPFUNCTIONCODE",
                this.repFuncCode,
                this.sOldrepFuncCode);

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void delSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        String funcName = "";
		String[] temp=null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql =
                "delete from "+pub.yssGetTableName("Tb_REP_FUNCTION")+"  where FREPFUNCTIONCODE = " +dbl.sqlString(this.repFuncCode);
            dbl.executeSql(strSql);
            
            if(this.repFuncType.equalsIgnoreCase("1")&&this.sDataSource.length()>0){
            	temp = this.sDataSource.toUpperCase().split("FUNCTION");
    			funcName = temp[1].split(" ")[1];
                dbl.executeSql("drop function "+funcName);
            }
			
            conn.commit();
            bTrans = true;
            conn.setAutoCommit(true);
           
        } catch (Exception e) {
            throw new YssException("删除报表函数设置出错", e);
        }

	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	public String editSetting() throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;
        String ErrorMsg = "修改报表函数设置出错";
        try {
        	
        	
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update "+pub.yssGetTableName("Tb_REP_FUNCTION")+" set FREPFUNCTIONCODE = " + dbl.sqlString(this.repFuncCode) +
                ",FREPFUNCTIONNAME = " + dbl.sqlString(this.repFuncName) +
                ",FFUNCTIONTYPE = " + dbl.sqlString(this.repFuncType) +
                ",FDATASOURCE = EMPTY_CLOB()" + 
                ",FDESC = " + dbl.sqlString(this.sDesc) +
                " where FREPFUNCTIONCODE = " + dbl.sqlString(this.sOldrepFuncCode);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
			if (this.repFuncType.equalsIgnoreCase("1")
					&& this.sDataSource.length() > 0) {
				
				checkFuncExist();
	        	dealFunc();
				strSql = "select FDataSource from "
						+ pub.yssGetTableName("Tb_REP_FUNCTION")
						+ " where FREPFUNCTIONCODE ="
						+ dbl.sqlString(this.repFuncCode) ;
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
					  // modify by jsc 20120809 连接池对大对象的特殊处理
					//CLOB clob = ((oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
					CLOB clob =  dbl.CastToCLOB(rs.getClob("FDataSource"));
					clob.putString(1, this.sDataSource);
					strSql = "update " + pub.yssGetTableName("Tb_REP_FUNCTION")
							+ " set FDataSource = ? where FREPFUNCTIONCODE="
							+ dbl.sqlString(this.sOldrepFuncCode);
					PreparedStatement pst = conn.prepareStatement(strSql);
					pst.setClob(1, clob);
					pst.executeUpdate();
					pst.close();
				}
			}  
            
        dbl.closeResultSetFinal(rs);
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);    
        } catch (Exception e) {
            throw new YssException(ErrorMsg+"\r\n"+e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
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
		return null;
	}

     

	public String getListViewData1() throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = ""; //词汇类型对照字符串
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        StringBuffer bufAll = new StringBuffer(); //所有的属性
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            strSql =
                " select a.*,d.fvocname as FFUNCTIONName from " +pub.yssGetTableName("Tb_Rep_FUNCTION") + " a" +
                " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FFUNCTIONTYPE") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_REP_FUNCTYPE) +
                buildFilterSql() + " order by FREPFUNCTIONCODE ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {

                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setResultSetAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_REP_FUNCTYPE);

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取报表函数设置信息出错", e);
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
    
	//判断是否已经存在同名的函数
	public String checkFuncExist() throws YssException{
		StringBuffer buf = new StringBuffer();
		ResultSet rs = null;
		String funcName = "";
		String[] temp=null;
		String errorMsg = "";
		try{
			if(this.sDataSource.length()==0){
				return "";
			}
			temp = this.sDataSource.toUpperCase().split("FUNCTION");
			funcName = temp[1].split(" ")[1];
			buf.append(" select OBJECT_NAME from user_objects where OBJECT_TYPE='FUNCTION' ");
			buf.append(" AND object_name= "+dbl.sqlString(funcName.toUpperCase()));
			
			rs = dbl.openResultSet(buf.toString());
			if(rs.next()){
				errorMsg="【系统中 function："+funcName+" 已经存在，请换一个function名字】";
				throw new YssException(errorMsg);
			}
			return "";
		}catch(Exception e){
			throw new YssException(errorMsg.length()==0?"【检测报表函数时出错......】":errorMsg);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
//	private void deleteFunc() throws YssException{
//		StringBuffer buf = new StringBuffer();
//		ResultSet rs = null;
//		boolean isExists = false;
//		String funcName = "";
//		String[] temp=null;
//		try{
//			if(this.sDataSource.length()==0){
//				return ;
//			}
//			temp = this.sDataSource.toUpperCase().split("FUNCTION");
//			funcName = temp[1].split(" ")[1];
//			buf.append(" drop function "+funcName);
//			
//			
//			dbl.executeSql(buf.toString());
//			if(rs.next()){
//				isExists = true;
//			}
//		}catch(Exception e){
//			throw new YssException("【删除报表函数时出错......】");
//		}finally{
//			dbl.closeResultSetFinal(rs);
//		}
//		
//	}
//	
	
	public String dealFunc() throws YssException{
		
		try{
			
			dbl.executeSql(this.sDataSource);
			return "";
		}catch(Exception e){
			throw new YssException("执行报表函数出错，请检查创建的报表函数语句 是否正确.....");
		}
	}

	
}
