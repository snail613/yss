package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class DaoFileNameBean
    extends BaseDataSettingBean implements IDataSetting {

    public DaoFileNameBean() {
    }

    private String cusCfgCode = ""; // 接口代码
    private String cusCfgName = ""; // 接口名称
    private String valueType = ""; // 值类型
    private String fileNameCls = ""; // 文件名类型
    private String fileNameClsName = "";
    private String fileNameConent = ""; // 文件名内容
    private String fileNameDictCode = ""; // 文件名字典
    private String fileNameDictName = ""; // 文件名字典名称
    private String tabFeildCode = ""; // 对应表字段
    private String tabFeildName = ""; // 对应表字段名称
    private String desc = ""; // 描述
    private String orderNum; // 排序号
    private String tabName = "";
    private String format = "";
    private int delayday=0;	//add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
    private String holidaycode="";
    private String holidayname="";
    private DaoFileNameBean filterType;
    private String oldcusCfgCode = "";

    /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-17 STORY 3366*/
	
    public String getTabName() {
        return tabName;
    }
    
  //add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode/holidayname
    public int getDelayDay() {
        return delayday;
    }
    public void setDelayDay(int delay){
    	this.delayday=delay;
    }
    public void setHolidaycode(String holiday) {
        this.holidaycode = holiday;
    }

    public String getHolidaycode() {
        return this.holidaycode;
    }
    public void setHolidayName(String holiday) {
        this.holidayname = holiday;
    }

    public String getHolidayName() {
        return this.holidayname;
    }
    //end by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getFileNameClsName() {
        return fileNameClsName;
    }

    public void setFileNameClsName(String fileNameClsName) {
        this.fileNameClsName = fileNameClsName;
    }

    public String getCusCfgCode() {
        return cusCfgCode;
    }

    public void setCusCfgCode(String cusCfgCode) {
        this.cusCfgCode = cusCfgCode;
    }

    public String CusCfgName() {
        return cusCfgName;
    }

    public void setCusCfgName(String cusCfgName) {
        this.cusCfgName = cusCfgName;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getFileNameCls() {
        return fileNameCls;
    }

    public void setFileNameCls(String fileNameCls) {
        this.fileNameCls = fileNameCls;
    }

    public String getFileNameConent() {
        return fileNameConent;
    }

    public void setFileNameConent(String fileNameConent) {
        this.fileNameConent = fileNameConent;
    }

    public String getFileNameDictCode() {
        return fileNameDictCode;
    }

    public void setFileNameDictCode(String fileNameDictCode) {
        this.fileNameDictCode = fileNameDictCode;
    }

    public String getTabFeildCode() {
        return tabFeildCode;
    }

    public void setTabFeildCode(String tabFeildCode) {
        this.tabFeildCode = tabFeildCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFileNameDictName() {
        return fileNameDictName;
    }

    public void setFileNameDictName(String fileNameDictName) {
        this.fileNameDictName = fileNameDictName;
    }

    public String getTabFeildName() {
        return tabFeildName;
    }

    public void setTabFeildName(String tabFeildName) {
        this.tabFeildName = tabFeildName;
    }

    public DaoFileNameBean getFilterType() {
        return filterType;
    }

    public void setValueType(DaoFileNameBean filterType) {
        this.filterType = filterType;
    }

    public String getOldcusCfgCode() {
        return oldcusCfgCode;
    }

    public void setOldcusCfgCode(String oldcusCfgCode) {
        this.oldcusCfgCode = oldcusCfgCode;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void checkInput(byte btOper) throws YssException {
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
            strSql = getInsertSql();
            pstmt = con.prepareStatement(strSql);
            while (rs.next()) {
                Num = Num + 1;
                super.parseRecLog();
                //设置对象赋值
                setPstmt(pstmt, this.cusCfgCode, Num, rs.getString("FValueType"), rs.getString("FFileNameCls"),
                		rs.getString("FFIleNameConent"), rs.getString("FFileNameDict"), rs.getString("FTabFeild"),
                		rs.getString("FDesc"), rs.getString("FFormat"), pub.getSysCheckState(), this.creatorCode,
                		this.creatorTime, this.checkTime, 0, "");
                
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            return "";
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception e) {
            throw new YssException("新增文件名称设置信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**shashijie 2013-2-18 STORY 3366 获取旧SQL语句*/
	private String getSelectSQLByCode() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " select * from " + pub.yssGetTableName("Tb_Dao_FileName");
		} else {
			sql = " select * from Tb_Dao_FileName ";
		}
		sql += " where FCusCfgCode=" + dbl.sqlString(this.oldcusCfgCode);
		return sql;
	}

	public void setDaoFileNameAttr(ResultSet rs) throws YssException {
        try {
            this.cusCfgCode = rs.getString("FcusCfgCode");
            this.valueType = rs.getString("FValueType");
            this.fileNameCls = rs.getString("FFileNameCls");
            this.fileNameClsName = rs.getString("FFileNameClsName");
            this.fileNameConent = rs.getString("FFIleNameConent");
            this.fileNameDictCode = rs.getString("FFileNameDict");
            this.fileNameDictName = rs.getString("FDictName");
            this.tabFeildCode = rs.getString("FTabFeild");
            this.tabFeildName = rs.getString("FFieldDesc");
            this.desc = rs.getString("FDesc");
            this.orderNum = rs.getString("FOrderNum");
            this.format = rs.getString("FFormat");
          //add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
            this.delayday=rs.getInt("delaydays");
            this.holidaycode=rs.getString("holidaycode");
            this.holidayname=rs.getString("holidayname");
          //add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileName") + " set FCusCfgCode = " +
                dbl.sqlString(this.cusCfgCode) + ", FValueType = " +
                this.valueType + ", FFileNameCls = " +
                this.fileNameCls + ", FDesc = " +
                dbl.sqlString(this.desc) + ", FTabFeild =" +
                dbl.sqlString(this.tabFeildCode) + ",FFormat=" +
                dbl.sqlString(this.format) + ",FFileNameDict=" +
                dbl.sqlString(this.fileNameDictCode) + ",FCheckState = " +
                (pub.getSysCheckState() ? "0" : "1") + ", FCreator = " +
                dbl.sqlString(this.creatorCode) + ", FCreateTime = " +
                dbl.sqlString(this.creatorTime) + ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ", FFIleNameConent = " + dbl.sqlString(this.fileNameConent) + " , where FCusCfgCode = " +
                dbl.sqlString(this.oldcusCfgCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新文件名称设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {

            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileName") + " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                " and FOrderNum = " + this.orderNum;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除文件名称设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Dao_FileName") + " set FCheckState = " +
                this.checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) + ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
                " and FOrderNum = " + this.orderNum;
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核文件名称设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
        //String Num = "";
        try {
            /*  Num = dbFun.getNextInnerCode(pub.yssGetTableName(
                      "Tb_Dao_FileName"),
                                     dbl.sqlRight("FOrderNum", 1), "1",
                                     " where 1=1", 1);*/
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            
            /**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //删除旧数据SQL
            sql = getDeleteFileNameByCode();
            dbl.executeSql(sql);
            //增加SQL语句
            sql = getInsertSql();
            pstmt = con.prepareStatement(sql);

            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                //---edit by songjie 2012.02.07 BUG 3830 QDV4农业银行2012年02月09日01_B start---//
                if (this.cusCfgCode.trim().length() > 0 &&
                    !(valueType.length() == 0 && fileNameCls.length() == 0 && fileNameConent.length() == 0 && 
                     fileNameDictCode.length() == 0 && tabFeildCode.length() == 0 && desc.length() == 0 && 
                     oldcusCfgCode.length() == 0 && tabName.length() == 0 && format.length() == 0 &&
                     holidaycode.length() == 0)) {
                	//---edit by songjie 2012.02.07 BUG 3830 QDV4农业银行2012年02月09日01_B end---//
                	//设置对象赋值
                	setPstmt(pstmt,this.cusCfgCode,i + 1,this.valueType,this.fileNameCls,this.fileNameConent,
                			this.fileNameDictCode,this.tabFeildCode,this.format,this.desc,pub.getSysCheckState()
                			,this.creatorCode,this.creatorTime,this.checkTime,this.delayday,this.holidaycode);
                    
                    pstmt.executeUpdate();
                }
            }
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception ex) {
            throw new YssException("保存文件名称设置信息出错\r\n" + ex.getMessage(),ex);
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    /**shashijie 2013-2-18 STORY 3366 重构代码,设置ps对象*/
	private void setPstmt(PreparedStatement ps, String cusCfgCode, int Num,
			String valueType, String fileNameCls, String fileNameConent,
			String fileNameDictCode, String tabFeildCode, String format,
			String desc, boolean sysCheckState, String creatorCode,
			String creatorTime, String checkTime, int delayday,
			String holidaycode) throws Exception {
		ps.setString(1, cusCfgCode);
        ps.setInt(2, Num);
        ps.setInt(3, YssFun.toInt(valueType));
        ps.setString(4, fileNameCls);
        ps.setString(5, fileNameConent);
        ps.setString(6, fileNameDictCode);
        ps.setString(7, tabFeildCode);
        ps.setString(8, format);
        ps.setString(9, desc);
        ps.setInt(10, (sysCheckState ? 0 : 1));
        ps.setString(11, creatorCode);
        ps.setString(12, creatorTime);
        ps.setString(13, (sysCheckState ? " " : creatorCode));
        ps.setString(14, checkTime);
        //add by zhouxiang 850 自定义接口配置需支持文件夹日期和数据日期不一致的情况  2010.11.15
        ps.setInt(15, delayday);
        ps.setString(16, holidaycode);
        //end by zhouxiang 850 自定义接口配置需支持文件夹日期和数据日期不一致的情况  2010.11.15
	}

	/**shashijie 2013-2-18 STORY 3366 获取SQL */
	private String getInsertSql() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (this.saveType.equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_FileName");
		} else {
			sql = " insert into Tb_Dao_FileName ";
		}
		sql += " (FCusCfgCode,FOrderNum," +
	        " FValueType,FFileNameCls,FFIleNameConent,FFileNameDict,FTabFeild,FFormat,FDesc," +
	        " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,delaydays,holidaycode)" +
	        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return sql;
	}

	/**shashijie 2013-2-18 STORY 3366 删除SQL语句*/
	private String getDeleteFileNameByCode() {
		String sql = "";
		//不选中处理组合群表,反之则处理公共表
		if (this.saveType.equals("0")) {
			sql = " delete from " + pub.yssGetTableName("Tb_Dao_FileName");
		} else {
			sql = " delete from Tb_Dao_FileName ";
		}
		sql += " where FCusCfgCode In (" + operSql.sqlCodes(this.cusCfgCode+","+this.oldcusCfgCode) + " ) ";
		return sql;
	}

	public IDataSetting getSetting() throws YssException {
        return null;
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

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.cusCfgCode.length() != 0) {
//            sResult = sResult + " and a.FCusCfgCode like '" +
//                  filterType.cusCfgCode.replaceAll("'", "''")+"'";//这里不能加%号，加了会误查多条数据bug MS00113 2008.12.30方浩
                //这是删的时候多删除的了个"'"导致报错    2009.01.09 方浩
                sResult = sResult + " and a.FCusCfgCode ='" +
                    filterType.cusCfgCode.replaceAll("'", "''") + "'"; //这里不能用模糊查询，应直接用原值 QDV4赢时胜(上海)2008年12月19日01_B MS00113 by leeyu 20090226
            }

        }
        return sResult;
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

    /**shashijie 2013-2-18 STORY 3366 获取SQL*/
	private String getSelectList(String saveType) throws YssException {
		String mainTableName = "";//主表
		String sql = " select distinct y.* from (select * from ";
		//若非选中处理组合群表,反之处理公共表
		if (saveType.equals("0")) {
        	sql += pub.yssGetTableName("Tb_Dao_FileName");
        	mainTableName = pub.yssGetTableName("Tb_Dao_FileName");
		} else {
			sql += " Tb_Dao_FileName ";
			mainTableName = " Tb_Dao_FileName ";
		}
		sql += " where FCheckState <> 2) x join" +
	        " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName , " +
	        " x1.fholidaysname as holidayname, " +
	        " d.FFieldDesc as FFieldDesc,f.FVocName as FVocName, g.FCusCfgName as FCusCfgName," +
	        " h.FDictName as FDictName,i.FVocName as FFileNameClsName from " +
	        mainTableName + " a" +
	        " left join (select FDictCode,FDictName from ";
		//若非选中处理组合群表,反之处理公共表
		if (saveType.equals("0")) {
			sql += pub.yssGetTableName("Tb_Dao_Dict");
		} else {
			sql += " Tb_Dao_Dict ";
		}
		
		sql += " ) h on a.FFileNameDict = h.FDictCode " +
			//add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段
	        " left join (select fholidayscode,fholidaysname  from Tb_Base_Holidays  ) x1 on a.holidaycode=x1.fholidayscode"+
	        " left join (select FCusCfgCode,FCusCfgName from ";
		
		//若非选中处理组合群表,反之处理公共表
		if (saveType.equals("0")) {
			sql += pub.yssGetTableName("Tb_Dao_CusConfig");
		} else {
			sql += " Tb_Dao_CusConfig ";
		}
		
		sql += " ) g on a.FCusCfgCode = g.FCusCfgCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join (select FFieldName,FFieldDesc from TB_FUN_DATADICT where  FTabName = " + 
	        dbl.sqlString(this.tabName) +
	        " ) d on d.FFieldName=a.FTabFeild " +
	        " left join Tb_Fun_Vocabulary f on a.FValueType = f.FVocCode and f.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_INFACE_VALUETYPE) +
	        " left join Tb_Fun_Vocabulary i on a.FFileNameCls = i.FVocCode and  i.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_INFACE_FILENAMECLS) +
	        buildFilterSql() +
	        " ) y on y.FCusCfgCode=x.FCusCfgCode and y.FOrderNum=x.FOrderNum ";
        //" order by y.FCusCfgCode,y.FOrderNum";
		return sql;
	}

	public String getListViewData2() throws YssException {
        return "";
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
                setDaoFileNameAttr(rs);
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
            //     sVocStr = vocabulary.getVoc(YssCons.YSS_INFACE_VALUETYPE+","+YssCons.YSS_INFACE_FILENAMECLS);//chenyibo 修改20070925
            //前台的值类型combo屏蔽，现在只有一个combo chenyb 20070927
            sVocStr = vocabulary.getVoc(YssCons.YSS_INFACE_FILENAMECLS);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        String strSql =
            "select * from " + pub.yssGetTableName("Tb_Dao_FileName") +
            " where 1=2"; //只是获取表头
        return builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
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
            //edit by songjie 2012.02.09 BUG 3830 QDV4农业银行2012年02月09日01_B
            if(reqAry[0] != null && reqAry[0].trim().length() >0){
            	this.cusCfgCode = reqAry[0];
            }
            //this.orderNum=reqAry[1];
            this.valueType = reqAry[1];
            this.fileNameCls = reqAry[2];
            this.fileNameConent = reqAry[3];
            this.fileNameDictCode = reqAry[4];
            this.tabFeildCode = reqAry[5];
            this.desc = reqAry[6];
            this.oldcusCfgCode = reqAry[7];
            this.checkStateId = YssFun.toInt(reqAry[8]);
            this.tabName = reqAry[9];
            this.format = reqAry[10];
            //add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
            this.delayday=YssFun.toInt(reqAry[11]);
            this.holidaycode=reqAry[12];
            //end by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new DaoFileNameBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("获取文件名称设置信息出错！");
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
        //fanghaoln 20100128 MS00947 QDV4赢时胜上海2010年01月22日01_B 
        buf.append(this.valueType).append("\t");//去除一个用不到的字段
        buf.append(this.fileNameCls).append("\t");//增加一个有用的字段
        buf.append(this.fileNameClsName).append("\t");
        //-------------end --MS00947--------------------------------------
        buf.append(this.fileNameConent).append("\t");
        buf.append(this.fileNameDictCode).append("\t");
        buf.append(this.fileNameDictName).append("\t");
        buf.append(this.tabFeildCode).append("\t");
        buf.append(this.tabFeildName).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.orderNum).append("\t");
        buf.append(this.format).append("\t");
    	//add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
        buf.append(this.delayday).append("\t");
        buf.append(this.holidaycode).append("\t");
        buf.append(this.holidayname).append("\t");
    	//add by zhouxiang 850 2010.11.15 自定义接口配置需支持文件夹日期和数据日期不一致的情况  新增两个字段：delayday/holidaycode
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
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

	/**add---shashijie 2013-2-18 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-18 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

}
