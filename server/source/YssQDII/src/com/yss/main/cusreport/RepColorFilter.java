package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class RepColorFilter extends BaseDataSettingBean implements IDataSetting {

	private String sRepDsCode="";          //数据源代码
	private String sOldRepDsCode="";
	private String sOldFieldName = "";
	private String sOldColor = "";
	private String sOldFilterContent = "";
	private String sFieldName="";          //字段名字
	private String sFieldType="";          //字段类型
	private String sDiscrepancy = "";      //误差值
	private String sColor = "";            //颜色
	private String sColorStyle = "";       //颜色格式
	private String sShow="";               //显示格式
	private String sFilterContent="";      //过滤内容
	private String sRelation="";           //关系
	private String sDesc="";
	private String sColorId = "";          
	private RepColorFilter filter=null;
	private String isOnlyColumn = "0";     
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分割线  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	public String getsRepDsCode() {
		return sRepDsCode;
	}

	public String getsColorId() {
		return sColorId;
	}

	public void setsColorId(String sColorId) {
		this.sColorId = sColorId;
	}

	public String getIsOnlyColumn() {
		return isOnlyColumn;
	}

	public void setIsOnlyColumn(String isOnlyColumn) {
		this.isOnlyColumn = isOnlyColumn;
	}

	public void setsRepDsCode(String sRepDsCode) {
		this.sRepDsCode = sRepDsCode;
	}

	public String getsOldRepDsCode() {
		return sOldRepDsCode;
	}

	public void setsOldRepDsCode(String sOldRepDsCode) {
		this.sOldRepDsCode = sOldRepDsCode;
	}

	public String getsFieldName() {
		return sFieldName;
	}

	public void setsFieldName(String sFieldName) {
		this.sFieldName = sFieldName;
	}

	public String getsFieldType() {
		return sFieldType;
	}

	public void setsFieldType(String sFieldType) {
		this.sFieldType = sFieldType;
	}

	public String getsDiscrepancy() {
		return sDiscrepancy;
	}

	public void setsDiscrepancy(String sDiscrepancy) {
		this.sDiscrepancy = sDiscrepancy;
	}

	public String getsColor() {
		return sColor;
	}

	public void setsColor(String sColor) {
		this.sColor = sColor;
	}

	public String getsColorStyle() {
		return sColorStyle;
	}

	public void setsColorStyle(String sColorStyle) {
		this.sColorStyle = sColorStyle;
	}

	public String getsShow() {
		return sShow;
	}

	public void setsShow(String sShow) {
		this.sShow = sShow;
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

	public RepColorFilter getFilter() {
		return filter;
	}

	public void setFilter(RepColorFilter filter) {
		this.filter = filter;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分割线  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
        buf.append(this.sRepDsCode).append("\t");
        buf.append(this.sFieldName).append("\t");
        buf.append(this.sFieldType).append("\t");
        buf.append(this.sDiscrepancy).append("\t");
        buf.append(this.sColor).append("\t");
        buf.append(this.sColorStyle).append("\t");
        buf.append(this.sShow).append("\t");
        buf.append(this.sFilterContent).append("\t");
        buf.append(this.sRelation).append("\t");
        buf.append(this.sDesc).append("\t");
       // buf.append(this.sColorId).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		if(sType.equalsIgnoreCase("fields")){
			return getFields();
		}
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
            this.sRepDsCode = reqAry[0];
            this.sFieldName = reqAry[1];
            this.sFieldType = reqAry[2];
            this.sDiscrepancy = reqAry[3];
            this.sColor = reqAry[4];
            this.sColorStyle = reqAry[5];
            this.sShow = reqAry[6];
            this.sFilterContent = reqAry[7];
            this.sRelation = reqAry[8];
            this.sDesc = reqAry[9];
            if(YssFun.isNumeric(reqAry[10]))
            	this.checkStateId = YssFun.toInt(reqAry[10]);
            this.sOldRepDsCode = reqAry[11];
            this.sOldFieldName = reqAry[12];
            this.sOldColor = reqAry[13];
            this.sOldFilterContent = reqAry[14];
            this.isOnlyColumn = reqAry[15];
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filter == null) {
                    this.filter = new RepColorFilter();
                    this.filter.setYssPub(pub);
                }
                this.filter.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析文件筛选条件信息出错");
        }

	}
	
	private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filter != null) {
            sResult = " where 1=1 and a.FCheckState<>2";
            if (this.filter.sRepDsCode.length() != 0) {
                sResult = sResult + " and a.frepdscode = '" +
                    filter.sRepDsCode.replaceAll("'", "''") + "'"; 
            }
        }
        return sResult;
    }
	
	private void setResultSetAttr(ResultSet rs )throws YssException{
		try{
			this.sRepDsCode = rs.getString("frepdscode");
			this.sFieldName = rs.getString("FFIELNAME");
			this.sFieldType = rs.getString("FFIELDTYPE");
			this.sDiscrepancy = rs.getString("FDISCREPANCY");
			this.sColor = rs.getString("FCOLOR");
			this.sColorStyle = rs.getString("FCOLORSTYLE");
			this.sShow = rs.getString("FSHOWSTYLE");
			this.sFilterContent = rs.getString("FCONTENT");
			//this.sColorId = rs.getString("fcolorfilterid");
			this.sRelation = rs.getString("FRELATION");
     		this.sDesc = rs.getString("FDesc");		
		}catch(Exception ex){
			throw new YssException(ex);
		}
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分割线  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	public IDataSetting getSetting() throws YssException {
		String strSql = ""; 
        ResultSet rs = null;
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_rep_colorfilter") +
                " where frepDscode=" + dbl.sqlString(this.sRepDsCode) ;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	StringBuffer buf = new StringBuffer();
                buf.append(this.sRepDsCode).append("\t");
                buf.append(this.sFieldName).append("\t");
                buf.append(this.sFieldType).append("\t");
                buf.append(this.sDiscrepancy).append("\t");
                buf.append(this.sColor).append("\t");
                buf.append(this.sColorStyle).append("\t");
                buf.append(this.sShow).append("\t");
                buf.append(this.sFilterContent).append("\t");
                buf.append(this.sRelation).append("\t");
                buf.append(this.sDesc).append("\t");
            }
        } catch (Exception e) {
            throw new YssException("获取文件筛选条件信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
	}
	
	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
	         strSql = "select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName from "
					+ pub.yssGetTableName("Tb_rep_colorfilter")
					+ " a "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "
					+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "+
					 " where frepDscode=" + dbl.sqlString(this.sRepDsCode) +" order by a.frepdscode,a.FFIELNAME";

	            rs = dbl.openResultSet(strSql);
	            while (rs.next()) {
	                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
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
	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols();
	        } catch (Exception e) {
	            throw new YssException("获取亮色筛选条件信息出错！", e);
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
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分割线  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private String createColorId()throws YssException  {
		String strSql = "";
		ResultSet rs = null;
		String sColorId = "";
		try{
			//1. 获取库中是否
			strSql = " select distinct fcolorfilterid from "+pub.yssGetTableName("Tb_rep_colorfilter")+
			         " where frepDscode=" + dbl.sqlString(this.sRepDsCode);
			
			rs = dbl.openResultSet(strSql);
			if(rs.next()){
				sColorId = rs.getString("fcolorfilterid");
			}else{
				dbl.closeResultSetFinal(rs);
				
				strSql =  " select distinct fcolorfilterid from "+pub.yssGetTableName("Tb_rep_colorfilter")+
				          " order by fcolorfilterid desc";
				
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					sColorId = rs.getString("fcolorfilterid");
				}
			}
			
			if(sColorId.equalsIgnoreCase("")){
				sColorId = "1";
			}
			return sColorId;
		}catch(Exception e){
			
			throw new YssException("创建索引编号出错!!!");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		
		
	}
	
	public String addSetting() throws YssException {
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        boolean bTrans = false;
        String sColorId = "";
        try {
            sql = "delete from " + pub.yssGetTableName("Tb_rep_colorfilter") +
                  " where FREPDSCODE=" + dbl.sqlString(this.sRepDsCode)+" and FFIELNAME ="+dbl.sqlString(this.sFieldName)+
                  " and FCOLOR ="+dbl.sqlString(this.sColor)+" and FCONTENT="+dbl.sqlString(this.sFilterContent);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_rep_colorfilter") +
                "(FREPDSCODE,FCOLORFILTERID," +
                " FFIELNAME,FFIELDTYPE,FCOLORSTYLE,FSHOWSTYLE,FCOLOR,FDISCREPANCY,FRELATION,FCONTENT,FDESC," +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);
            sColorId = createColorId();
                    pstmt.setString(1, this.sRepDsCode);
                    pstmt.setString(2, sColorId);
                    pstmt.setString(3, this.sFieldName);
                    pstmt.setString(4, this.sFieldType);
                    pstmt.setString(5, this.sColorStyle);
                    pstmt.setString(6, this.sShow);
                    pstmt.setString(7, this.sColor);
                    pstmt.setString(8, this.sDiscrepancy);
                    pstmt.setString(9, this.sRelation);
                    pstmt.setString(10, this.sFilterContent);
                    pstmt.setString(11, this.sDesc);
                    pstmt.setInt(12, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(13, this.creatorCode);
                    pstmt.setString(14, this.creatorTime);
                    pstmt.setString(15, (pub.getSysCheckState() ? " " : this.creatorCode));
                    
                    pstmt.executeUpdate();

        } catch (Exception ex) {
            throw new YssException("保存亮色筛选条件信息出错\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
	}

	public void checkInput(byte btOper) throws YssException {
		String strSql = "";
		ResultSet rs = null;
       try{   
			if (btOper == YssCons.OP_ADD) {
				strSql = " select FREPDSCODE,FFIELNAME,FCOLOR,FCONTENT from "
						+ pub.yssGetTableName("Tb_rep_colorfilter")
						+ " where FREPDSCODE=" + dbl.sqlString(this.sRepDsCode)
						+ " and FFIELNAME=" + dbl.sqlString(this.sFieldName)
						+ " and FCOLOR=" + dbl.sqlString(this.sColor)
						+ " and FCONTENT=" + dbl.sqlString(this.sFilterContent);

				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					throw new YssException("已存在【数据源：" + this.sRepDsCode
							+ ", 字段名:" + this.sFieldName + ", 颜色："
							+ this.sColor + " ,过滤条件：" + this.sFilterContent
							+ "】，请重新输入");
				}
			}
       }catch(Exception e){
    	   throw new YssException(e.getMessage());
       }finally{
    	   dbl.closeResultSetFinal(rs);
         }

	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	public void delSetting() throws YssException {
		 Connection conn = dbl.loadConnection();
	        boolean bTrans = false;
	        String strSql = "";
	        try {
	            strSql = "update " + pub.yssGetTableName("Tb_rep_colorfilter") +
	                " set FCheckState = " + this.checkStateId +
	                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +"'" +
	                " where FREPDSCODE=" + dbl.sqlString(this.sRepDsCode);
	               
	            conn.setAutoCommit(false);
	            bTrans = true;
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        } catch (Exception e) {
	            throw new YssException("删除 亮色筛选条件信息出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }
	}

	public void deleteRecycleData() throws YssException {
		 String strSql = "";
	        boolean bTrans = false; //代表是否开始了事务
	        //获取一个连接
	        Connection conn = dbl.loadConnection();
	        try {
	            strSql = " delete from " + pub.yssGetTableName("Tb_rep_colorfilter") +
                         " where FREPDSCODE=" + dbl.sqlString(this.sRepDsCode)+" and ffielname =" + dbl.sqlString(this.sFieldName)+
                         " and fcolor ="+dbl.sqlString(this.sColor)+" and fcontent="+dbl.sqlString(this.sFilterContent);
	            dbl.executeSql(strSql);
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
	        } catch (Exception e) {
	            throw new YssException("清除数据出错", e);
	        } finally {
	            dbl.endTransFinal(conn, bTrans);
	        }

	}

	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Rep_Colorfilter") +
            " set ffielname=" + dbl.sqlString(this.sFieldName) +
            //--------增加拆分数据可修改的部分。sj edit 20081016 暂无 bug ---//
            ",ffieldtype = " + dbl.sqlString(this.sFieldType) +
            //",fcolorstyle = " + dbl.sqlString(this.sColorStyle) +
            //",fshowstyle = " + dbl.sqlString(this.sShow) +
            ",fcolor = " +dbl.sqlString(this.sColor)+
            ",fdiscrepancy = " + dbl.sqlString(this.sDiscrepancy) +
            ",frelation = " + dbl.sqlString("") +
            ",fcontent = " + dbl.sqlString(this.sFilterContent) +
            //----------------------------------------------------------//
            ",FCheckState = " +this.checkStateId + 
            ", FCheckUser = " +dbl.sqlString(pub.getUserCode()) +
            " ,FCheckTime = '" +YssFun.formatDatetime(new java.util.Date()) + "'" +
            " where FREPDSCODE=" + dbl.sqlString(this.sOldRepDsCode)+" and ffielname =" + dbl.sqlString(this.sOldFieldName)+
            " and fcolor ="+dbl.sqlString(this.sOldColor)+" and fcontent="+dbl.sqlString(this.sOldFilterContent);
        dbl.executeSql(strSql);
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);
        return "";
    } catch (Exception e) {
        throw new YssException("修改交易数据信息出错", e);
    } finally {
        dbl.endTransFinal(conn, bTrans);
    }
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        boolean bTrans = false;
        try {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("Tb_rep_colorfilter") +
                " where FREPDSCODE=" + dbl.sqlString(this.sRepDsCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("Tb_rep_colorfilter") +
                "(FREPDSCODE,FCOLORFILTERID," +
                " FFIELNAME,FFIELDTYPE,FCOLORSTYLE,FSHOWSTYLE,FCOLOR,FDISCREPANCY,FRELATION,FCONTENT,FDESC" +
                " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
            	if(sMutilRowAry[i].trim().length()==0)continue;
                this.parseRowStr(sMutilRowAry[i]);
                if (this.sRepDsCode.trim().length() > 0) {
                    pstmt.setString(1, this.sRepDsCode);
                    pstmt.setString(2, this.sFieldName);
                    pstmt.setString(3, this.sFieldName);
                    pstmt.setString(4, this.sFieldType);
                    pstmt.setString(5, this.sFilterContent);
                    pstmt.setString(6, this.sColorStyle);
                    pstmt.setString(7, this.sShow);
                    pstmt.setString(8, this.sColor);
                    pstmt.setString(9, this.sDiscrepancy);
                    pstmt.setString(10, this.sRelation);
                    pstmt.setString(11, this.sFilterContent);
                    pstmt.setString(12, this.sDesc);
                    pstmt.setInt(13, (pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(14, this.creatorCode);
                    pstmt.setString(15, this.creatorTime);
                    pstmt.setString(16, (pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new YssException("保存亮色筛选条件信息出错\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getFields() throws YssException{
	 String sHeader = "";
	 String sShowDataStr = "";
	 String sAllDataStr = "";
	 StringBuffer bufShow = new StringBuffer();
	 StringBuffer bufAll = new StringBuffer();
	 ResultSet rs = null;
   	 String strSql = "";
   	 try {
   		 sHeader = "字段名";
   	     strSql ="select FDSFIELD from " + pub.yssGetTableName("Tb_Rep_DsField")+" where  frepdscode="+dbl.sqlString(this.sRepDsCode);
         rs = dbl.openResultSet(strSql);
         while (rs.next()) {
        	 bufShow.append(super.buildRowShowStr(rs, "FDSFIELD")).
             append(YssCons.YSS_LINESPLITMARK);
        	 this.sFieldName = rs.getString("FDSFIELD");
             bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
        }
       if (bufShow.toString().length() > 2) {
           sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
     }

     if (bufAll.toString().length() > 2) {
         sAllDataStr = bufAll.toString().substring(0,bufAll.toString().length() - 2);
     }
         return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +"FDSFIELD";
     } catch (Exception e) {
         throw new YssException("获取数据源字段设置信息出错", e);
     } finally {
         dbl.closeResultSetFinal(rs);
     }
		
	}

	

}
