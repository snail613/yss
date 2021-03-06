package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class DaoFileContentBean
    extends BaseDataSettingBean implements IDataSetting {
    public DaoFileContentBean() {
    }

    private String cusCfgCode = ""; //接口代码
    private String cusCfgName = "";
    private String orderNum; //排序号
    private String beginRow; //读取行
    private String loadIndex; //读取位置
    private String loadLen; //读取长度
    // private String splitType; //分割类型
    // private String splitMark;//分割标记
    private String tabField = ""; //对应表的字段
    private String fieldDesc = "";
    private String format = ""; //转换格式
    private String fileInfoDict = ""; //文件头字典
    private String fileInfoDictName = "";
    private String desc = ""; //描述
    private DaoFileContentBean filterType;
    private String oldcusCfgCode = "";

    private String tabName = "";
    private String fileContentDict = ""; //文件转换内容
    private String AssetGroupCode = ""; //组合群代码 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
	private String fieldOrder="";//新增　排序字段，用于导出时排序用　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
    private int unExportState=0;//新增　不导出字段状态，用于导出时排序用　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
    
    /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-17 STORY 3366*/
    
    
	//新增　排序字段，用于导出时排序用　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	public int getUnExportState() {
		return unExportState;
	}
	// 新增　排序字段，用于导出时排序用　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	public void setUnExportState(int unExportState) {
		this.unExportState = unExportState;
	}
	// 字段排序　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	public String getFieldOrder() {
		return fieldOrder;
	}
	//字段排序　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	public void setFieldOrder(String fieldOrder) {
		this.fieldOrder = fieldOrder;
	}
//------------------------------------------------------------------------------
    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    /*public String getSplitMark()
        {
       return splitMark;
        }

        public void setSplitMark(String splitMark)
        {
       this.splitMark = splitMark;
        }

        public String getSplitType()
        {
       return splitType;
        }

        public void setSplitType(String splitType)
        {
       this.splitType = splitType;
        }*/

    public String getFileInfoDictName() {
        return fileInfoDictName;
    }

    public void setFileInfoDictName(String fileInfoDictName) {
        this.fileInfoDictName = fileInfoDictName;
    }

    public DaoFileContentBean getFilterType() {
        return filterType;
    }

    public void setFilterType(DaoFileContentBean filterType) {
        this.filterType = filterType;
    }

    public String getOldcusCfgCode() {
        return oldcusCfgCode;
    }

    public void setOldcusCfgCode(String oldcusCfgCode) {
        this.oldcusCfgCode = oldcusCfgCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFileInfoDict() {
        return fileInfoDict;
    }

    public void setFileInfoDict(String fileInfoDict) {
        this.fileInfoDict = fileInfoDict;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public String getTabField() {
        return tabField;
    }

    public void setTabField(String tabField) {
        this.tabField = tabField;
    }

    public String getLoadLen() {
        return loadLen;
    }

    public void setLoadLen(String loadLen) {
        this.loadLen = loadLen;
    }

    /* public String getloadLen()
      {
      return loadLen;
      }*/

    public String getLoadIndex() {
        return loadIndex;
    }

    public void setLoadIndex(String loadIndex) {
        this.loadIndex = loadIndex;
    }

    public String getBeginRow() {
        return beginRow;
    }

    public void setBeginRow(String beginRow) {
        this.beginRow = beginRow;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getCusCfgName() {
        return cusCfgName;
    }

    public void setCusCfgName(String cusCfgName) {
        this.cusCfgName = cusCfgName;
    }

    public String getCusCfgCode() {
        return cusCfgCode;
    }

    public String getFileContentDict() {
        return fileContentDict;
    }

    public void setCusCfgCode(String cusCfgCode) {
        this.cusCfgCode = cusCfgCode;
    }

    public void setFileContentDict(String fileContentDict) {
        this.fileContentDict = fileContentDict;
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
		String mainTableName = "";//主表
		String sql = " select distinct y.* from (select * from " ;
		//若非选中处理组合群表,反之处理公共表
        if (saveType.equals("0")) {
        	sql += pub.yssGetTableName("Tb_Dao_FileContent");
        	mainTableName = pub.yssGetTableName("Tb_Dao_FileContent");
		} else {
			sql += " Tb_Dao_FileContent ";
			mainTableName = " Tb_Dao_FileContent ";
		}
        sql += " where FCheckState <> 2) x join" +
        " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
        "  d.FFieldDesc as FFieldDesc,f.FVocName as FVocName, g.FCusCfgName as FCusCfgName,h.FDictName as FDictName from " +
        mainTableName + " a" +
        " left join (select FDictCode,FDictName from ";
        
        //若非选中处理组合群表,反之处理公共表
        if (saveType.equals("0")) {
        	sql += pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql += " Tb_Dao_Dict ";
		}
        
        sql += " ) h on a.FFileContentDict = h.FDictCode " +
        " left join (select FCusCfgCode,FCusCfgName from ";
        
        //若非选中处理组合群表,反之处理公共表
        if (saveType.equals("0")) {
        	sql += pub.yssGetTableName("Tb_Dao_CusConfig");
		} else {
			sql += " Tb_Dao_CusConfig ";
		}
		
		sql += "  ) g on a.FCusCfgCode = g.FCusCfgCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join(select FFieldName,FFieldDesc from TB_FUN_DATADICT where  FTabName = " + 
	        dbl.sqlString(this.tabName) +
	        " ) d on d.FFieldName=a.FTabFeild " +
	        " left join Tb_Fun_Vocabulary f on a.FFormat = f.FVocCode and f.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_FORMAT) +
	        buildFilterSql() +
	        " ) y on y.FCusCfgCode=x.FCusCfgCode and y.FOrderNum=x.FOrderNum ";
	        //" order by y.FCusCfgCode,y.FOrderNum";
		return sql;
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
                setDaoFileContentAttr(rs);
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

         sVocStr = vocabulary.getVoc(YssCons.YSS_FORMAT+","+YssCons.YSS_INFACE_ORDER+","+YssCons.YSS_VCH_EXINSERT);//增加字段排序词汇,导出状态词汇  by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setDaoFileContentAttr(ResultSet rs) throws YssException {
        try {
        	/**add---shashijie 2013-3-8 STORY 2869 公共方法,增加是否存在判断*/
            this.cusCfgCode = rs.getString("FcusCfgCode");
            this.cusCfgName = dbl.isFieldExist(rs, "FCusCfgName") ? rs.getString("FCusCfgName") : "";
            this.orderNum = String.valueOf(rs.getInt("ForderNum"));
            this.beginRow = Integer.toString(rs.getInt("FBeginRow"));
            this.loadIndex = Integer.toString(rs.getInt("FloadIndex"));
            this.loadLen = Integer.toString(rs.getInt("FloadLen"));
            this.tabField = dbl.isFieldExist(rs, "FTabFeild") ? rs.getString("FtabFeild") : "";
            this.format = rs.getString("Fformat");
            //字段描述
            if (dbl.isFieldExist(rs, "FFieldDesc")) {
            	this.fieldDesc = rs.getString("FFieldDesc");
			}
            //文件内容字典
            if (dbl.isFieldExist(rs, "FFileContentDict")) {
            	this.fileContentDict = rs.getString("FFileContentDict");
            	this.fileInfoDict = rs.getString("FFileContentDict");
			}
            //字典名称
            if (dbl.isFieldExist(rs, "FDictName")) {
            	this.fileInfoDictName = rs.getString("FDictName");
			}
            //描述
            if (dbl.isFieldExist(rs, "FDesc")) {
            	this.desc = rs.getString("FDesc");
			}
            //排序
            if (dbl.isFieldExist(rs, "FOrder")) {
            	//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
				this.fieldOrder = rs.getString("FOrder") == null ? "" : rs.getString("FOrder");
			}
            //导出状态
            if (dbl.isFieldExist(rs, "FUnExport")) {
            	//导出状态 by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
            	this.unExportState = rs.getInt("FUnExport");
			}
         	
         	/**end---shashijie 2013-3-8 STORY 2869*/
         	
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }

    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.cusCfgCode.length() != 0) {
//            sResult = sResult + " and a.FCusCfgCode like '" +
                //filterType.cusCfgCode.replaceAll("'", "''") + "%'";
                sResult = sResult + " and a.FCusCfgCode = '" +
                    filterType.cusCfgCode.replaceAll("'", "''") + "'"; //这里取全名，不能模糊查询 by leeyu 20090226 QDV4赢时胜(上海)2008年12月19日01_B MS00113
            }

        }
        return sResult;

    }

    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
        return "";
    }

    /**
     * getListViewData3
     *
     * @return String
     */
    public String getListViewData3() throws YssException {
        String strSql =
            "select * from " + pub.yssGetTableName("Tb_Dao_FileContent") +
            " where 1=2"; //只是获取表头
        return builderListViewData(strSql);
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
        /* String strSql=
             "select y.* from " +
                "(select FVchTplCode,FEntityCode,FCheckState from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " " +
                " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join" +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName ," +
                "  e.FDesc as FResumeFieldValue,d.FDictName as FResumeDictValue, f.FVocName as FValueTypeValue from " +
                pub.yssGetTableName("Tb_Vch_EntityResume") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join(select distinct FDictCode,FDictName from "+pub.yssGetTableName("Tb_Vch_Dict")+
                " ) d on d.FDictCode=a.FResumeDict"+
                " left join(select FAliasName,FDesc from "+pub.yssGetTableName("Tb_Vch_DsTabField")+
                " where FVchDsCode="+dbl.sqlString(this.dataSource)+") e on e.FAliasName=a.FResumeField "+
                " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
                 dbl.sqlString(YssCons.YSS_VALUETYPE) +
                buildFilterSql() +
                ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode" +
                " order by y.FVchTplCode,y.FEntityCode";
          return builderListViewData(strSql);*/
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        ResultSet rs = null;
        String strSql = "";
        PreparedStatement pstmt = null;
        int Num = 0;
        try {
        	
        	/**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	//查询旧数据SQL
        	strSql = getSelectSQLByCode();
        	rs = dbl.openResultSet(strSql);
        	
        	//增加SQL语句
            strSql = getInsert();
            
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                Num += 1;
                super.parseRecLog();
                //设置对象赋值
                setPstmt(pstmt, this.cusCfgCode, Num, rs.getString("FBeginRow"), rs.getString("FLoadIndex"), 
                		rs.getString("FLoadLen"), rs.getString("FTabFeild"), rs.getString("FFormat"), 
                		rs.getString("FFileContentDict"), rs.getString("FDesc"), pub.getSysCheckState(), this.creatorCode
                		, this.creatorTime, pub.getSysCheckState(), this.fieldOrder, this.unExportState);
                
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return "";
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("新增文件头设置信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**shashijie 2013-2-17 STORY 3366 获取SQL */
	private String getSelectSQLByCode() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = "select * from " + pub.yssGetTableName("Tb_Dao_FileContent");
		} else {
			sql = "select * from Tb_Dao_FileContent ";
		}
		sql += " where FCusCfgCode=" + dbl.sqlString(this.oldcusCfgCode);
		return sql;
	}
	
	/**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        /*String strSql = "";
              boolean bTrans = false; //代表是否开始了事务
              Connection conn = dbl.loadConnection();
              try {
           strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCusCfgCode = " +
                 dbl.sqlString(this.cusCfgCode) + ", FLoadRow = " +
                 this.loadRow + " , FLoadIndex = " +
                 this.loadIndex + ", FLoadLen = " +
                 this.loadLen + ", FDesc = " +
                 dbl.sqlString(this.desc) + ", FTabFeild =" +
                 dbl.sqlString(this.tabField) + ", FFormat =" +
                 dbl.sqlString(this.format) + ", FFileInfoDict =" +
                 dbl.sqlString(this.fileInfoDict) + ",FCheckState = " +
                 (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                 dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                 dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
                 (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                 " where FCusCfgCode = " +
                 dbl.sqlString(this.oldcusCfgCode);

           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
              }
              catch (Exception e) {
           throw new YssException("更新文件头设置信息出错", e);
              }
              finally {
           dbl.endTransFinal(conn, bTrans);
              }*/
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        /*String strSql = "";
              boolean bTrans = false; //代表是否开始了事务
              Connection conn = dbl.loadConnection();
              try {

              strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                        " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                        " and FOrderNum = " + this.orderNum ;


              conn.setAutoCommit(false);
              bTrans = true;
              dbl.executeSql(strSql);
              conn.commit();
              bTrans = false;
              conn.setAutoCommit(true);
         }
         catch (Exception e) {
            throw new YssException("删除文件头设置信息出错", e);
         }
         finally {
            dbl.endTransFinal(conn, bTrans);
         }*/

    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        /*String strSql = "";
           boolean bTrans = false; //代表是否开始了事务
           Connection conn = dbl.loadConnection();
           try {
           strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") + " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                           " and FOrderNum = " + this.orderNum ;


           conn.setAutoCommit(false);
           bTrans = true;
           dbl.executeSql(strSql);
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);
           }
           catch (Exception e) {
           throw new YssException("审核文件头设置信息出错", e);
           }
           finally {
           dbl.endTransFinal(conn, bTrans);
           }
         */
    }

    /**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";
        //boolean bTrans = false;
        try {

            /* Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                     "Tb_Dao_FileContent"),
                     dbl.sqlRight("FOrderNum", 1), "1",
                     " where 1=1", 1);*/
        	sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
        	
        	/**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        	//删除旧数据SQL
            sql = getDeleteSql(saveType);
            dbl.executeSql(sql);
        	
        	//增加SQL语句
        	sql = getInsert();
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                //Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                //      	"Tb_Dao_FileContent"),
                //          dbl.sqlRight("FOrderNum", 1), "1",
                //          " where 1=1", 1);  20070920陈一波修改

                this.parseRowStr(sMutilRowAry[i]);
                if (this.cusCfgCode.trim().length() > 0) { //这里判断,如果cusCfgCode=""时不添加 by leeyu 080721
                	//设置对象赋值
                	setPstmt(pstmt,this.cusCfgCode,i + 1,this.beginRow,this.loadIndex,this.loadLen,this.tabField,this.format
                			,this.fileInfoDict,this.desc,pub.getSysCheckState(),this.creatorCode,this.creatorTime,
                			pub.getSysCheckState(),this.fieldOrder,this.unExportState);
                    
                    pstmt.executeUpdate();
                }
            }
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception ex) {
            throw new YssException("保存文件内容信息出错\r\n" + ex.getMessage(),ex);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    /**shashijie 2013-2-17 STORY 3366 设置添加对象*/
	private void setPstmt(PreparedStatement pstmt,String cusCfgCode, int num, String beginRow,
			String loadIndex, String loadLen, String tabField,
			String format, String fileInfoDict, String desc,
			boolean sysCheckState, String creatorCode, String creatorTime,
			boolean sysCheckState2, String fieldOrder, int unExportState) throws Exception {
		pstmt.setString(1, cusCfgCode);
        pstmt.setInt(2, num);
        pstmt.setInt(3, YssFun.toInt(beginRow));
        pstmt.setInt(4, YssFun.toInt(loadIndex));
        pstmt.setInt(5, YssFun.toInt(loadLen));
        pstmt.setString(6, tabField);
        pstmt.setString(7, format);
        // pstmt.setInt(7,YssFun.toInt(this.splitType));
        //pstmt.setString(8,this.splitMark);
        pstmt.setString(8, fileInfoDict);
        pstmt.setString(9, desc);

        pstmt.setInt(10, (sysCheckState ? 0 : 1));
        pstmt.setString(11, creatorCode);
        pstmt.setString(12, creatorTime);
        pstmt.setString(13, (sysCheckState2 ? " " : creatorCode));
  		pstmt.setString(14, fieldOrder==null?"null":fieldOrder);//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
  		pstmt.setInt(15, unExportState);//by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	}
	
	/**shashijie 2013-2-17 STORY 3366 获取SQL*/
	private String getInsert() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = "insert into " + pub.yssGetTableName("Tb_Dao_FileContent");
		} else {
			sql = "insert into Tb_Dao_FileContent ";
		}
		sql += " (FCusCfgCode,FOrderNum, " +
	        " FBeginRow,FLoadIndex,FLoadLen,FTabFeild,FFormat,FFILECONTENTDICT,FDesc," +
	        //新增字段排序,导出状态　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	        " FCheckState,FCreator,FCreateTime,FCheckUser,FOrder,FUnExport)"+
	        //by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		return sql;
	}
	
	/**shashijie 2013-2-17 STORY 3366 获取删除SQL */
	private String getDeleteSql(String saveType) {
		String sqlString = "";
		//不选中处理组合群表反之处理公共表
		if (saveType.equals("0")) {
			sqlString = "delete from " + pub.yssGetTableName("Tb_Dao_FileContent");
		} else {
			sqlString = "delete from Tb_Dao_FileContent ";
		}
		sqlString += 
			" where FCusCfgCode In (" + operSql.sqlCodes(this.cusCfgCode+","+this.oldcusCfgCode) + " ) ";
		return sqlString;
	}
	
	/**
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = ""; // add liyu
        ResultSet rs = null;
        try {
        	/**add---shashijie 2013-3-8 STORY 2869 根据接口代码,对应表的字段获取文件内容*/
        	//组合群SQL
            strSql = getSelectFileContentByFCusCfgCodeAndFTabFeild("0");
            
            strSql += " Union ";
            //公共表SQL
            strSql += getSelectFileContentByFCusCfgCodeAndFTabFeild("1");
			/**end---shashijie 2013-3-8 STORY 2869*/
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	/**add---shashijie 2013-3-8 STORY 2869 重构代码,改调用公共方法*/
            	setDaoFileContentAttr(rs);
            	
                /*this.cusCfgCode = rs.getString("FCusCfgCode");
                this.orderNum = rs.getString("FOrderNum");
                this.beginRow = rs.getString("FBeginRow");
                this.loadIndex = rs.getString("FLoadIndex");
                this.loadLen = rs.getString("FLoadLen");
                this.tabField = rs.getString("FTabFeild");
                this.format = rs.getString("FFormat");
                this.fileContentDict = rs.getString("FFileContentDict");
         		this.fieldOrder=rs.getString("FOrder");//排序字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
         		this.unExportState = rs.getInt("FUnExport");//导出状态字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
                this.desc = rs.getString("FDesc");*/
            	/**end---shashijie 2013-3-8 STORY 2869*/
            }
        } catch (Exception e) {
            throw new YssException("获取文件内容信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    /**shashijie 2013-3-8 STORY 2869 获取SQL*/
	private String getSelectFileContentByFCusCfgCodeAndFTabFeild(String saveType) {
		String sql = " " ;
		//若非选中处理组合群表,反之处理公共表
        if (saveType.equals("0")) {
        	sql += " select * from " + pub.yssGetTableName("Tb_Dao_FileContent");
		} else {
			sql += " select * from Tb_Dao_FileContent ";
		}
        sql += " where FCusCfgCode=" + dbl.sqlString(this.cusCfgCode) + " and FTabFeild = " +
        	dbl.sqlString(this.tabField) + " and FCheckState = 1 ";
        return sql;
	}
	
	/**
     * getAllSetting
     *
     * @return String
     */
    public String getAllSetting() {
        return "";
    }

    /**
     * getTreeViewData1
     *
     * @return String
     */
    public String getTreeViewData1() {
        return "";
    }

    /**
     * getTreeViewData2
     *
     * @return String
     */
    public String getTreeViewData2() {
        return "";
    }

    /**
     * getTreeViewData3
     *
     * @return String
     */
    public String getTreeViewData3() {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
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
            this.cusCfgCode = reqAry[0];
            //this.orderNum=reqAry[1];
            this.beginRow = reqAry[1];
            this.loadIndex = reqAry[2];
            this.loadLen = reqAry[3];
            this.tabField = reqAry[4];
            this.format = reqAry[5];
            //this.splitType=reqAry[6];
            //this.splitMark = reqAry[7];
            this.fileInfoDict = reqAry[6];
            this.desc = reqAry[7];
            this.checkStateId = YssFun.toInt(reqAry[8]);
            this.oldcusCfgCode = reqAry[9];
            this.tabName = reqAry[10];
            this.AssetGroupCode = reqAry[11]; //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
			this.fieldOrder = reqAry[12];//排序字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
          	if(YssFun.isNumeric(reqAry[13]))
        	  	this.unExportState = YssFun.toInt(reqAry[13]);//导出状态字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223

            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoFileContentBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析分录摘要信息出错");
        }

    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();

        buf.append(this.cusCfgCode).append("\t");
        buf.append(this.cusCfgName).append("\t");
        //
        buf.append(this.beginRow).append("\t");
        buf.append(this.loadLen).append("\t");
        buf.append(this.loadIndex).append("\t");
        buf.append(this.tabField).append("\t");
        buf.append(this.fieldDesc).append("\t");
        buf.append(this.format).append("\t");
        //buf.append(this.splitType).append("\t");
        // buf.append(this.splitMark).append("\t");
        buf.append(this.fileInfoDict).append("\t");
        buf.append(this.fileInfoDictName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.orderNum).append("\t");
     	buf.append(this.fieldOrder).append("\t");//排序字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
     	buf.append(this.unExportState).append("\t");//导出状态字段　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
        buf.append(super.buildRecLog());
        return buf.toString();

    }

    /**
     * getOperValue
     *添加此方法由一个接口代码来获取列数与起始行数 add liyu 0920
     * sType="";
     * 返回参数 beginRow+"\t"+Cols
     */
    public String getOperValue(String sType) throws YssException {
        //Connection conn=dbl.loadConnection();//这个没有用 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        ResultSet rs = null;
        //boolean bTrans = false;//shashijie 2013-02-19 STORY 3366 无用删除
        int beginRow = 0, Cols = 0;
        String strSql = "";
        String str=""; 
        String fileRows="";//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
        String prefixTB = pub.getPrefixTB(); //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
        try {
            //2008.09.18 陈土强 修改
            //判断读取位置的设置是否为零，是零就采取原先的按顺序读取方法，不是零就按照设置的读取位置来读取
            //BUG: 0000474
            //strSql=" select count(FCusCFGCode) as FCusCFGCode,FBeginRow from "+pub.yssGetTableName("TB_Dao_FileContent")+
//       strSql = " select decode(sign(count(FCusCFGCode)- max(FLoadIndex)),1,count(FCusCFGCode),-1,max(FLoadIndex)) as FCusCFGCode,FBeginRow from " +
//                 pub.yssGetTableName("TB_Dao_FileContent") +
//                 " where FCusCFGCode= " + dbl.sqlString(this.cusCfgCode) +
//                 " group by FBeginRow ";
            //by leeyu 修改上面的SQL 为了适应不同的数据库处理 2008-10-10
            //因为在正式做导数据时是从第0列开始的，下面的语句中做了相关的修改 byleeyu
            if (this.AssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
                pub.setPrefixTB(this.AssetGroupCode);
            }
            strSql = " select case when (count(FCusCFGCode)- max(FLoadIndex)-1)>0 then count(FCusCFGCode) when (count(FCusCFGCode)- max(FLoadIndex)-1)< 0 then max(FLoadIndex)+1 end as FCusCFGCode,FBeginRow from " +
                pub.yssGetTableName("TB_Dao_FileContent") +
                " where FCusCFGCode= " + dbl.sqlString(this.cusCfgCode) +
                " group by FBeginRow ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                beginRow = rs.getInt("FCusCFGCode");
                Cols = rs.getInt("FBeginRow");
            }
            
            //add by songjie 2011.05.16 BUG 1892 QDV4海富通2011年05月10日01_B
            dbl.closeResultSetFinal(rs);
            
            str=" select FFileRows from " +
                pub.yssGetTableName("Tb_Dao_CusConfig") +
                " where Fcuscfgcode=" + dbl.sqlString(this.cusCfgCode) +
                " and FCheckState='1'";
            rs = dbl.openResultSet(str);
            while(rs.next()){
            	fileRows=rs.getString("FFileRows");
            }
            //add by songjie 2011.05.16 BUG 1892 QDV4海富通2011年05月10日01_B
            dbl.closeResultSetFinal(rs);
            //delete by songjie 2011.05.16 BUG 1892 QDV4海富通2011年05月10日01_B
//            rs.close();
        } catch (Exception e) {
            throw new YssException("获取接口行列值信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标 //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
            //dbl.endTransFinal(conn,bTrans);//这里不需要这个
            pub.setPrefixTB(prefixTB); //国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
        }
        return beginRow + "\t" + Cols + "\t" + fileRows;//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
    }

    public String getBeforeEditData() {
        return "";
    }

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
	/**add---shashijie 2013-2-17 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}
	/**add---shashijie 2013-2-17 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

}
