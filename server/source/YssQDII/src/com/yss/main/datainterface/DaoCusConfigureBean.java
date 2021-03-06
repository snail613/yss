package com.yss.main.datainterface;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.util.*;

public class DaoCusConfigureBean
    extends BaseDataSettingBean implements
    IDataSetting {
    private String cusCfgCode = "";
    private String cusCfgName = "";
    private String cusCfgType = "";
    private String cusCfgTypeName = "";
    private String fileType = "";

    private String fileTypeValue = "";
    private String tabName = "";
    private String tabDesc = "";
    private String fileCusCfg = ""; //11.23 add lzp
    private String fileNameDesc = "";
    private String fileInfoDesc = "";
    private String fileCntDesc = "";
    private String fileTrailDesc = "";
    private String fileFilterDesc = "";//添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
    private String dPCodes = "";
    private String desc = "";
    private String groupCode = "";
    private String groupName = "";
    private String oldGroupCode = "";

    private String splitType = "";
    private String splitMark = "";
    private String endMark = "";
    //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
    private boolean autoFix;
    private String oldCusCfgCode = "";
    private String oldCusCfgName = "";
    private DaoCusConfigureBean filterType;
    private String sRecycled = ""; //为增加还原和删除功能加的一个中介字符串变量 bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
    private String allSetDatas = "";
    //  protected YssDbOperSql operSql = null ;
    //-----add by songjie 2009.12.28 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B-----//
    private String AssetGroupCode = ""; //组合群代码  
    private String AssetGroupName = ""; //组合群名称  
    private boolean isGroup = false; //判断是否为跨组合群 
   //-----add by songjie 2009.12.28 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B-----//
    private String sCgfENCode = "";//导出接口的编码格式  xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A
    private String fileRows="";//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
    private String currentPortGroupCode="";//add by guyichuan STORY #897
    private String menuBarCode = "";//菜单条代码--add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现 
    private String menuBarName = "";//菜单条名称--add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现 
    
    /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
	private String saveType = "0";//默认不选中状态
	/**end---shashijie 2013-2-17 STORY 3366*/
    
    public String getFileRows() {
		return fileRows;
	}

	public void setFileRows(String fileRows) {
		this.fileRows = fileRows;
	}
	//----add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A -------//
    private String exlSetPwd="";
    /**多一个字段:合并文件路径   shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 */
    private String merger = "";
    
	public String getMerger() {
		return merger;
	}

	public void setMerger(String merger) {
		this.merger = merger;
	}

	public String getExlSetPwd() {
		return exlSetPwd;
	}
	
	public void setExlSetPwd(String exlSetPwd) {
		this.exlSetPwd = exlSetPwd;
	}
    //------------end by wuweiqi 20101102----------------
	public DaoCusConfigureBean() {
    }

//------------------------------------------------------------------------------
    public boolean getAutoFix(){
    	return autoFix;
    }
    
    public void setAutoFix(boolean autoFix){
    	this.autoFix = autoFix;
    }
    
    public String getFileCusCfg() {
        return fileCusCfg;
    }
    //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
    public String getFileFilterDesc() {
		return fileFilterDesc;
	}
    //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	public void setFileFilterDesc(String fileFilterDesc) {
		this.fileFilterDesc = fileFilterDesc;
	}

    public void setFileCusCfg(String fileCusCfg) {
        this.fileCusCfg = fileCusCfg;
    }

//----------lzp  11.23 add
    public String getEndMark() {
        return endMark;
    }

    public void setEndMark(String endMark) {
        this.endMark = endMark;
    }

//----------
    public String getOldGroupCode() {
        return oldGroupCode;
    }

    public void setOldGroupCode(String oldGroupCode) {
        this.oldGroupCode = oldGroupCode;
    }

    public String getOldCusCfgName() {
        return oldCusCfgName;
    }

    public void setOldCusCfgName(String oldCusCfgName) {
        this.oldCusCfgName = oldCusCfgName;
    }

    public String getAllSetDatas() {
        return allSetDatas;
    }

    //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    public void setAllSetDatas(String allSetDatas) {
        this.allSetDatas = allSetDatas;
    }

    public DaoCusConfigureBean getFilterType() {
        return filterType;
    }

    public void setFilterType(DaoCusConfigureBean filterType) {
        this.filterType = filterType;
    }

    public String getCusCfgTypeName() {
        return cusCfgTypeName;
    }

    public void setCusCfgTypeName(String cusCfgTypeName) {
        this.cusCfgTypeName = cusCfgTypeName;
    }

    public String getSplitMark() {
        return splitMark;
    }

    public void setSplitMark(String splitMark) {
        this.splitMark = splitMark;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public String getTabDesc() {
        return tabDesc;
    }

    public void setTabDesc(String tabDesc) {
        this.tabDesc = tabDesc;
    }

    public String getOldCusCfgCode() {
        return oldCusCfgCode;
    }

    public void setOldCusCfgCode(String oldCusCfgCode) {
        this.oldCusCfgCode = oldCusCfgCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDPCodes() {
        return dPCodes;
    }

    public void setDPCodes(String dPCodes) {
        this.dPCodes = dPCodes;
    }

    public String getFileTrailDesc() {
        return fileTrailDesc;
    }

    public void setFileTrailDesc(String fileTrailDesc) {
        this.fileTrailDesc = fileTrailDesc;
    }

    public String getFileCntDesc() {
        return fileCntDesc;
    }

    public void setFileCntDesc(String fileCntDesc) {
        this.fileCntDesc = fileCntDesc;
    }

    public String getFileInfoDesc() {
        return fileInfoDesc;
    }

    public void setFileInfoDesc(String fileInfoDesc) {
        this.fileInfoDesc = fileInfoDesc;
    }

    public String getFileNameDesc() {
        return fileNameDesc;
    }

    public void setFileNameDesc(String fileNameDesc) {
        this.fileNameDesc = fileNameDesc;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getCusCfgType() {
        return cusCfgType;
    }

    public void setCusCfgType(String cusCfgType) {
        this.cusCfgType = cusCfgType;
    }

    public String getCusCfgCode() {
        return cusCfgCode;
    }

    public void setCusCfgCode(String cusCfgCode) {
        this.cusCfgCode = cusCfgCode;
    }

    public String getCusCfgName() {
        return cusCfgName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getFileTypeValue() {
        return fileTypeValue;
    }

    public void setCusCfgName(String cusCfgName) {
        this.cusCfgName = cusCfgName;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setFileTypeValue(String fileTypeValue) {
        this.fileTypeValue = fileTypeValue;
    }

    //------------------------------------------------------------------------------
    /**
     * 2008-5-27 修改
     * 修改人 单亮
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        //修改前的方法
        //--------------begin
//      String strSql =
//            "select  distinct y.* from " +
//            "(select FCusCfgCode,FCheckState from " +
//            pub.yssGetTableName("Tb_Dao_CusConfig") + " " +
//            " where FCheckState <> 2 group by FCusCfgCode,FCheckState) x join" +
//            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
//            " d.FVocName as FSplitTypeValue ,"+
//            " e.FVocName as FCusCfgTypeValue,"+
//            " f.FTABLEDESC as FTabDesc ,"+
//            " g.FVocName as  FFileTypeValue from "+
//            pub.yssGetTableName("Tb_Dao_CusConfig") + " a" +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
//            " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
//            " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FSplitType") + " = d.FVocCode and d.FVocTypeCode = " +
//            dbl.sqlString(YssCons.YSS_INFACE_SPLITTYPE) +
//            " left join Tb_Fun_Vocabulary e on a.FCusCfgType = e.FVocCode and e.FVocTypeCode = " +
//            dbl.sqlString(YssCons.YSS_INFACE_TYPE) +
//            " left join (select FTabName,FTABLEDESC from TB_FUN_DATADICT group by FTabName,FTableDesc) f on a.FTabName = f.FTabName"+
//            buildFilterSql()+
//            " left join Tb_Fun_Vocabulary g on a.FFileType = g.FVocCode and g.FVocTypeCode = " +
//            dbl.sqlString(YssCons.YSS_FILETYPE) +
//            ")y on y.FCusCfgCode=x.FCusCfgCode " +
//            " order by y.FCusCfgCode";
        //--------------------end
        //修改后的方法
        //--------------------begin
//         回收站的内容没有显示 bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
        String strSql = "";
//            不要屏掉回收站的内容
//            "select  distinct y.* from " +
//            "(select FCusCfgCode,FCheckState from " +
//            pub.yssGetTableName("Tb_Dao_CusConfig") + " " +
//            " where FCheckState <> 2 group by FCusCfgCode,FCheckState) x join" +
            
        
        /**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
        strSql = " select * from ( ";
		//组合群SQL
        strSql += getSelectList(pub.yssGetTableName("Tb_Dao_CusConfig"),"0");
        
        strSql += " Union All ";
        //公共表SQL
        strSql += getSelectList("Tb_Dao_CusConfig","1");
        strSql += " ) a Order By a.FCusCfgCode ";
		/**end---shashijie 2013-2-18 STORY 3366 */
        return builderListViewData(strSql);
    }

    /**shashijie 2013-2-18 STORY 3366 获取SQL */
	private String getSelectList(String tableName,String saveType) throws YssException {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.*," +
        "(case when (a.FEXCELPWD = '0') then '否'" +
        " when (a.FEXCELPWD = '1') then '是' " + //---add by wuweiqi 20101102  QDV4深圳赢时胜2010年10月8日01_A ----
        " else '' end) as EXCELPWD" +
        ",b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
        " d.FVocName as FSplitTypeValue ," +
        " e.FVocName as FCusCfgTypeValue," +
        " f.FTABLEDESC as FTabDesc ," +
        " h.FVocName as CfgENCode ," +//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A         
        " g.FVocName as  FFileTypeValue from " +
        tableName + " a" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
        " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
        " left join Tb_Fun_Vocabulary d on " + dbl.sqlToChar("a.FSplitType") + " = d.FVocCode and d.FVocTypeCode = " +
        dbl.sqlString(YssCons.YSS_INFACE_SPLITTYPE) +
        " left join Tb_Fun_Vocabulary e on a.FCusCfgType = e.FVocCode and e.FVocTypeCode = " +
        dbl.sqlString(YssCons.YSS_INFACE_TYPE) +
        " left join (select FTabName,FTABLEDESC from TB_FUN_DATADICT group by FTabName,FTableDesc) f on a.FTabName = f.FTabName" +
        " left join Tb_Fun_Vocabulary g on a.FFileType = g.FVocCode and g.FVocTypeCode = " +
        dbl.sqlString(YssCons.YSS_FILETYPE) +
        //---------------xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A -----------//
        " left join Tb_Fun_Vocabulary h on a.FCfgENCode = h.FVocCode and h.FVocTypeCode ="+
        dbl.sqlString(YssCons.YSS_ENCODING_TYPE)+
        //------------------------------end 20100303--------------------------//
        buildFilterSql() ;
		//" ) y on y.FCusCfgCode=x.FCusCfgCode " +
		//" order by y.FCusCfgCode";
        //" order by a.FCusCfgCode"; //不屏掉到回收站的功能把上面的XY去掉改成" order by a.FCusCfgCode"
		//--------------------end
		return sql;
	}

    public String getListViewData2() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        //add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B
        ResultSetMetaData rsmd = null;
        try {
            sHeader = "接口代码\t接口名称\t文件路径";//edit by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
            //----add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ----//
            if(isGroup){//若为跨组合群操作
            	sHeader = "接口代码\t接口名称\t组合群代码;k\t组合群名称;v";//则添加组合群代码和组合群名称两列
            }
            //----add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ----//
            
            //---add by songjie 2012.02.27 3864 QDV4赢时胜(测试)2012年2月13日01_B start---//
            if(!dbl.yssTableExist(pub.yssGetTableName("Tb_Dao_CusConfig"))){
            	throw new YssException("请更新完所有的组合群再进行多组合群操作！");
            }
            //---add by songjie 2012.02.27 3864 QDV4赢时胜(测试)2012年2月13日01_B end---//
            
            /**add---shashijie 2013-2-27 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
            String strSql = " Select * From ( "; 
        	strSql += getSelectListDao_Group(pub.yssGetTableName("Tb_Dao_CusConfig"),"0");
            
            strSql += " Union All ";
            //公共表SQL
            strSql += getSelectListDao_Group("Tb_Dao_CusConfig","1");
            strSql += " ) y order by y.FCusCfgCode,y.FCusCfgName ";
    		/**end---shashijie 2013-2-27 STORY 3366 */

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	//add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B//
            	if(isGroup){//若为跨组合群操作 则在显示到listview中的数据中添加组合群代码 和组合群名称两列
            		bufShow.append(rs.getString("FCusCfgCode")).append("\t")
                    .append(rs.getString("FCusCfgName")).append("\t")
					//add by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                    .append(rs.getString("FFileNameDesc")).append("\t")
                    .append(rs.getString("fassetgroupcode")).append("\t")
                    .append(rs.getString("fassetgroupname"))
                    .append(YssCons.YSS_LINESPLITMARK);
            	}
            	else{
            	   //add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B//
                    bufShow.append(rs.getString("FCusCfgCode")).append("\t")
                    .append(rs.getString("FCusCfgName"))
                    //add by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
                    .append("\t").append(rs.getString("FFileNameDesc"))
                    .append(YssCons.YSS_LINESPLITMARK);
            	}/*
            	//add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B//
                this.cusCfgCode = rs.getString("FCusCfgCode");
                this.cusCfgName = rs.getString("FCusCfgName");
                this.cusCfgType = rs.getString("FCusCfgType"); //将接口类型重新传一次 QDV4华夏2009年6月11日01_A MS00496 by leeyu
                //add by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
				this.fileNameDesc = rs.getString("FFileNameDesc");
				//---- add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ----//
				*/
				setDaoCusConfigAttr(rs);		//add by guyichuan 20110609 STORY #897
                if(isGroup){//若为跨组合群操作
                	rsmd = rs.getMetaData(); //得到结果集里的内容
                    for (int i = 1; i < rsmd.getColumnCount(); i++) { //循环字段名称
                        if (rsmd.getColumnName(i).equals("FASSETGROUPCODE")) { //把字段名称进行对比看是否有当前字段名称
                            this.AssetGroupCode = rs.getString("fassetgroupcode") + ""; //给组合群代码赋值
                            this.AssetGroupName = rs.getString("fassetgroupname") + ""; //给组合群名称赋值
                        }
                    }
                }
                //---- add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ----//
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            rs.close();
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
            throw new YssException("获取接口设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }
    
    /**shashijie 2013-2-27 STORY 3366 获取SQL*/
	private String getSelectListDao_Group(String tableName, String saveType) throws YssException {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select Distinct '组合群' as saveType , ";
		} else {
			sql = " select Distinct '公共' as saveType , ";
		}
		sql += 
		//edit by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B 保证查询出的数据不重复 添加组合群代码，组合群名称字段
	        " y.*, h.fassetgroupcode, h.fassetgroupname from " +
	        "(select FCusCfgCode,FCusCfgName,FCheckState,FFileNameDesc from " +
	        tableName + " " +
	        //edit by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B 改为查询出已审核的数据 
	        //edit by songjie 2009.08.28 添加文件路径字段 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A
	        " where FCheckState = 1 group by FCusCfgCode,FCusCfgName,FCheckState,FFileNameDesc) x join" +
	        " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
	        " d.FVocName as FVocName ,f.FTableDesc as FTabDesc,g.FGroupName as FGroupName from " +
	        tableName + " a" +
	        " left join (select FGroupCode,FGroupName from " +
	        pub.yssGetTableName("Tb_Dao_Group") +
	        ") g on a.FCusCfgType = g.FGroupCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
	        " left join Tb_Fun_Vocabulary d on " +
	        dbl.sqlToChar("a.FSplitType") +
	        " = d.FVocCode and d.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_INFACE_SPLITTYPE) +
	        " left join (select FTabName,FTableDesc from TB_FUN_DATADICT ) f on a.FTabName = f.FTabName" +
	        buildFilterSql() +
	        ")y on y.FCusCfgCode=x.FCusCfgCode and y.FCusCfgName=x.FCusCfgName" +
	        
	        //add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B 左连接系统组合群表 以获取组合群代码和组合群名称
	        " left join Tb_Sys_Assetgroup h on h.fassetgroupcode = " + dbl.sqlString(pub.getPrefixTB());
		return sql;
	}

	/**
     * add by guyichaun 20110609 STORY #897
     * QDV4海富通2011年04月07日01_A
     * 为各项变量赋值
     */
    public void setDaoCusConfigAttr(ResultSet rs) throws SQLException {
        this.cusCfgCode = rs.getString("FCusCfgCode");
        this.cusCfgName = rs.getString("FCusCfgName");
        this.cusCfgType = rs.getString("FCusCfgType"); 
		this.fileNameDesc = rs.getString("FFileNameDesc");
		/**add---shashijie 2013-2-18 STORY 3366 存储类型赋值*/
		setSaveTypeValue(rs);
		/**end---shashijie 2013-2-18 STORY 3366 */
		super.setRecLog(rs);
    }

    public String getListViewData3() throws YssException {
        /*Connection conn = null;
        boolean bTrans = false;*/
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	/**add---shashijie 2013-2-27 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            sHeader = "数据源代码\t数据源名称\t描述";
            //sHeader="数据源代码";
            //conn = dbl.loadConnection();
            //无用注释,这里的SQL查不出任何数据等于页面上不显示任何数据源
            if (this.dPCodes == null || this.dPCodes.length() == 0) {
                /*sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName from " +
                    pub.yssGetTableName("Tb_Dao_Pretreat") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
                    " where 1=2";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    bufShow.append(rs.getString("FDPDsCode")).append("\t");
                    bufShow.append(rs.getString("FDPDsName")).append("\t");
                    bufShow.append(rs.getString("FDesc")).
                        append(YssCons.YSS_LINESPLITMARK);
                    // bufShow.append(rs.getString("FDPDsCode")).append(YssCons.YSS_LINESPLITMARK);
                    bufAll.append(rs.getString("FDPDsCode")).append("\t");
                    bufAll.append(rs.getString("FDPDsName")).append("\t");
                    bufAll.append(rs.getString("FDesc")).append(YssCons.
                        YSS_LINESPLITMARK);

                    //bufAll.append(pretreat.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                }*/
            } else {
                String codes = operSql.sqlCodes(this.dPCodes);
                String[] Arrcode = codes.split(","); //此次修改是用于处理数据按 dPCodes的顺序加载 by ly 080213
                //获取SQL
                sqlStr = getSelectPreByFDPDsCode(codes);
                rs = dbl.openResultSet_antReadonly(sqlStr);
                for (int i = 0; i < Arrcode.length; i++) {
                    while (rs.next()) {
                        if (Arrcode[i].equalsIgnoreCase(dbl.sqlString(rs.getString("FDPDsCode")))) {
                            bufShow.append(rs.getString("FDPDsCode")).append("\t");
                            bufShow.append(rs.getString("FDPDsName")).append("\t");
                            bufShow.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);
                            // bufShow.append(rs.getString("FDPDsCode")).append(YssCons.YSS_LINESPLITMARK);
                            bufAll.append(rs.getString("FDPDsCode")).append("\t");
                            bufAll.append(rs.getString("FDPDsName")).append("\t");
                            bufAll.append(rs.getString("FDesc")).append(YssCons.YSS_LINESPLITMARK);
                        }
                        //bufAll.append(pretreat.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    }
                    rs.beforeFirst();
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }
            }
            /**end---shashijie 2013-2-27 STORY 3366 */
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            //dbl.endTransFinal(conn, bTrans);
        }

    }

    /**shashijie 2013-2-27 STORY 3366 获取SQL*/
	private String getSelectPreByFDPDsCode(String codes) {
		String sql = "";
		String tablePre = pub.yssGetTableName("Tb_Dao_Pretreat");
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
			tablePre = " Tb_Dao_Pretreat ";
		}
		sql += " a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName From " +
	        tablePre + " a" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	        " left join (select distinct FTabName,FTableDesc From Tb_Fun_DataDict ) d on d.FTabName =a.FTargetTab " +
	        " where a.FCheckState =1 and FDPDsCode in (" + codes + " ) order by a.FDPDsCode ";
		return sql;
	}

    //-----------------------------20071020   chenyibo   获取自定义接口的数据
    public String getListViewData4() throws YssException {
        VocabularyBean vocabulary = new VocabularyBean();
        vocabulary.setYssPub(pub);
        String sHeader = "";
        String sShowDataStr = "";
        ResultSet rs = null;
        String sVocStr = ""; //词汇类型对照字符串
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();

        try {
            sVocStr = vocabulary.getVoc(YssCons.Yss_REPORT_TYPE);
            //edit by songjie 2009.08.28 国内:MS00006 QDV4.1赢时胜（上海）2009年4月20日06_A 添加文件路径字段
            sHeader = "接口代码\t接口名称\t文件路径";
            String strSql =
                "select  distinct y.* from " +
                "(select FCusCfgCode,FCheckState from " +
                pub.yssGetTableName("Tb_Dao_CusConfig") + " " +
                " where FCheckState <> 2 group by FCusCfgCode,FCheckState) x join" +
                " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
                " d.FVocName as FSplitTypeValue ," +
                " e.FVocName as FCusCfgTypeValue," +
                " f.FTABLEDESC as FTabDesc ," +
                " h.FVocName as CfgENCode ," +//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
                " g.FVocName as  FFileTypeValue from " +
                pub.yssGetTableName("Tb_Dao_CusConfig") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary d on " +
                dbl.sqlToChar("a.FSplitType") +
                " = d.FVocCode and d.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_INFACE_SPLITTYPE) +
                " left join Tb_Fun_Vocabulary e on a.FCusCfgType = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_INFACE_TYPE) +
                " left join (select FTabName,FTABLEDESC from TB_FUN_DATADICT group by FTabName,FTableDesc) f on a.FTabName = f.FTabName" +
                buildFilterSql() +
                " left join Tb_Fun_Vocabulary g on a.FFileType = g.FVocCode and g.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_FILETYPE) +         
                //-----------xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A -------------//
                " left join Tb_Fun_Vocabulary h on a.FCfgENCode = h.FVocCode and h.FVocTypeCode ="+
                dbl.sqlString(YssCons.YSS_ENCODING_TYPE)+
                //------------------------------end 20100303--------------------------//
                ")y on y.FCusCfgCode=x.FCusCfgCode " +
                " order by y.FCusCfgCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FCusCfgCode")).append("\t");
                bufShow.append(rs.getString("FCusCfgName")).append("\t").append(
                    YssCons.YSS_LINESPLITMARK);
                this.setEntityAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" + "voc" + sVocStr;
        } catch (Exception e) {
            throw new YssException("获取接口数据信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs); //关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
        }
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
                bufShow.append(super.buildRowShowStr(rs,this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                setEntityAttr(rs);
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

            sVocStr = vocabulary.getVoc(YssCons.YSS_INFACE_SPLITTYPE + "," +
                                        YssCons.YSS_FILETYPE + "," +
                                        YssCons.YSS_INFACE_TYPE + "," + YssCons.YSS_ENCODING_TYPE);//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取接口信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public void setEntityAttr(ResultSet rs) throws SQLException {
//        this.groupCode =rs.getString("FGroupCode");
        //this.groupName = rs.getString("FGroupName");
        this.cusCfgCode = rs.getString("FcusCfgCode");
        this.cusCfgName = rs.getString("FcusCfgName");
        this.cusCfgType = rs.getString("FCusCfgType");
        //this.cusCfgTypeName = rs.getString("FGroupName");
        this.fileType = rs.getString("FFileType");
        this.tabName = rs.getString("FTabName");
        this.tabDesc = rs.getString("FTabDesc");
        this.fileNameDesc = rs.getString("FFileNameDesc");
        this.fileInfoDesc = rs.getString("FFileInfoDesc");
        this.fileCntDesc = rs.getString("FFileCntDesc");
        this.fileTrailDesc = rs.getString("FFileTrailDesc");
        this.dPCodes = rs.getString("FDPCodes");
        this.splitType = rs.getString("FSplitType");
        this.splitMark = rs.getString("FSplitMark");
        this.desc = rs.getString("FDesc") + "";
        this.endMark = rs.getString("FEndMark") + "";
		this.fileFilterDesc = rs.getString("FFileFilterDesc");//添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
        this.autoFix = rs.getString("FAutoFix").equalsIgnoreCase(YssOperCons.YSS_DAO_YES);
        this.sCgfENCode = rs.getString("FCfgENCode");//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
        this.exlSetPwd = rs.getString("FEXCELPWD");//add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A 
        this.fileRows=rs.getString("FfileRows")+ "";// //#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
        this.menuBarCode=rs.getString("FMenuBarCode");//add by guolongchao 20110905 STORY 1285 添加菜单条代码
        this.menuBarName=rs.getString("FMenuBarName");//add by guolongchao 20110905 STORY 1285 添加菜单条名称
        /**shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 */
        this.merger = rs.getString("FMerger") + "";
        /**add---shashijie 2013-2-18 STORY 3366 存储类型赋值*/
		setSaveTypeValue(rs);
		/**end---shashijie 2013-2-18 STORY 3366 */
        super.setRecLog(rs);
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

	/**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        
        try {
        	/**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
			//查询旧code中的预处理并赋值
        	setdPCodes();
        	
        	//增加sql语句
        	strSql = getInsterSql();
        	
        	con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
        	
            //增加关联表数据
            insertAssociate();
            
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception ex) {
            throw new YssException("新增自定义接口配置信息出错!",ex);//xuqiji 20100715 抛出异常代码写法不规范 MS00113
        } finally {
            dbl.endTransFinal(con, bTrans);
        }
        return "";

    }

    /**shashijie 2013-2-17 STORY 3366 增加关联表数据 */
	private void insertAssociate() throws YssException {
		String[] str = null;
		DaoFileInfoBean fileinfo = new DaoFileInfoBean();
        DaoFileContentBean filecontent = new DaoFileContentBean();
        DaoFileNameBean filename = new DaoFileNameBean();
        DaoFileFilterBean fileFilter =new DaoFileFilterBean();//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        /**shashijie 2011.03.23 STORY #557 希望优化追加数据的功能*/
        DaoFileMergerNameBean fileMerger = new DaoFileMergerNameBean();
        fileMerger.setYssPub(pub);
        /**end*/

        fileinfo.setYssPub(pub);
        filecontent.setYssPub(pub);
        filename.setYssPub(pub);
        fileFilter.setYssPub(pub);//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        
		if (this.allSetDatas.length() > 0) {
            str = allSetDatas.split("\r\f");
            if (str[0].length() > 0) {//文件头设置目前还没有用到，设置时保存出错，暂时屏蔽chenyb 20070927
                //fileinfo.setCusCfgCode(this.cusCfgCode);
                //fileinfo.saveMutliSetting(str[0]);
            } else {
                //fileinfo.setCusCfgCode(this.cusCfgCode);
                //fileinfo.setOldcusCfgCode(this.oldCusCfgCode);
                //fileinfo.addSetting();
            }
            //文件内容
            filecontent.setSaveType(this.saveType);
            if (str[1].length() > 0) {
                filecontent.setCusCfgCode(this.cusCfgCode);
                filecontent.saveMutliSetting(str[1]);
            } else {
                filecontent.setCusCfgCode(this.cusCfgCode);
                filecontent.setOldcusCfgCode(this.oldCusCfgCode);
                filecontent.addSetting();
            }
            //文件名
            filename.setSaveType(this.saveType);
            if (str[2].length() > 0) {
                filename.setCusCfgCode(this.cusCfgCode);
                //filename.setValueType("");
                filename.saveMutliSetting(str[2]);
            } else {
                filename.setCusCfgCode(this.cusCfgCode);
                filename.setOldcusCfgCode(this.oldCusCfgCode);
                filename.addSetting();
            }
            //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            fileFilter.setSaveType(this.saveType);
            if (str[3].length() > 0) {
            	fileFilter.setsCusCfgCode(this.cusCfgCode);
            	fileFilter.saveMutliSetting(str[3]);
            } else {
            	fileFilter.setsCusCfgCode(this.cusCfgCode);
            	fileFilter.setsOldCusCfgCode(this.oldCusCfgCode);
            	fileFilter.addSetting();
            }
            //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            
            /**shashijie 2011.03.23 STORY 557 合并文件名*/
            fileMerger.setSaveType(this.saveType);
            if (str[4].length() > 0) {
                fileMerger.setCusCfgCode(this.cusCfgCode);
                //filename.setValueType("");
                fileMerger.saveMutliSetting(str[4]);
            } else {
            	fileMerger.setCusCfgCode(this.cusCfgCode);
            	fileMerger.setOldcusCfgCode(this.oldCusCfgCode);
            	fileMerger.addSetting();
            }
            /**end*/
        }
	}

	/**shashijie 2013-2-17 STORY 3366 增加SQL语句 */
	private String getInsterSql() {
		String sql = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sql = " insert into " + pub.yssGetTableName("Tb_Dao_CusConfig");
		} else {
			sql = " insert into Tb_Dao_CusConfig ";
		}
		sql += " (FCusCfgCode,FCusCfgName,FCusCfgType," +
	        " FFileType,FTabName,FFileNameDesc," +
	        //添加文件筛选条件 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	        " FFileInfoDesc,FFileCntDesc,FFileTrailDesc,FDPCodes,FFileFilterDesc," +
	        //添加字段FEXCELPWD add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A 
	        " FSplitType,FSplitMark,FEndMark,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser," +
	        //20100303 xuqiji MS00948  QDV4易方达2010年1月22日01_A 2007.11.22 添加 蒋锦 添加FFileCusCfg字段，此字段不能为空
	        " FFileCusCfg,FAutoFix,FCfgENCode,FEXCELPWD,FFILEROWS " +
	        //add by guolongchao 20110905 STORY 1285 添加菜单条代码、菜单条名称
	        " ,FMenuBarCode,FMenuBarName" + 
	        //shashijie 2011.2.17 STORY 557 增加合并文件名字段 
	        " ,FMerger) " + 
	        " values( " +
	        dbl.sqlString(this.cusCfgCode) + "," +
	        dbl.sqlString(this.cusCfgName) + "," +
	        dbl.sqlString(this.cusCfgType) + "," +
	        dbl.sqlString(this.fileType) + "," +
	        dbl.sqlString(this.tabName) + "," +
	
	        dbl.sqlString(this.fileNameDesc) + "," +
	        dbl.sqlString(this.fileInfoDesc) + "," +
	        dbl.sqlString(this.fileCntDesc) + "," +
	        dbl.sqlString(this.fileTrailDesc) + "," +
	        dbl.sqlString(this.dPCodes == null ? "" : this.dPCodes) +
	        "," +
	        //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	        dbl.sqlString(this.fileFilterDesc)+","+
	        this.splitType + "," +
	        dbl.sqlString(this.splitMark) + "," +
	        dbl.sqlString(this.endMark) + "," +
	        dbl.sqlString(this.desc) + "," +
	        (pub.getSysCheckState() ? "0" : "1") + "," +
	        dbl.sqlString(this.creatorCode) + "," +
	        dbl.sqlString(this.creatorTime) + "," +
	        (pub.getSysCheckState() ? "' '" :
	        dbl.sqlString(this.creatorCode)) +
	        //2009.11.09 edit by songjie insert 语句出错 少加一个逗号 已经补上 
	        ", ' '," + //2007.11.22 添加 蒋锦 为 FFileCusCfg 字段输入一个空格
	        //xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A
	        dbl.sqlString(this.autoFix?YssOperCons.YSS_DAO_YES:YssOperCons.YSS_DAO_NO) + "," +
	        dbl.sqlString(this.sCgfENCode) + "," +  
	        dbl.sqlString(this.exlSetPwd) + "," +//add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A 
	        dbl.sqlString(this.fileRows)+ "," + //#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
	        dbl.sqlString(this.menuBarCode)+ "," + //add by guolongchao 20110905 STORY 1285 添加菜单条代码
	        dbl.sqlString(this.menuBarName)+ "," + //add by guolongchao 20110905 STORY 1285 添加菜单条名称
	    	dbl.sqlString(this.merger) + " ) ";//shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 
		return sql;
	}

	/**shashijie 2013-2-17 STORY 3366 查询旧code中的预处理代码并赋值 */
	private void setdPCodes() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = getDPCodesSql();
	        if (this.dPCodes == null || this.dPCodes.length() == 0) {
	            rs = dbl.openResultSet(strSql);
	            if (rs.next()) {
	                this.dPCodes = rs.getString("FDPCodes");
	            }
	        }
		} catch (Exception e) {
			throw new YssException("查询旧code中的预处理代码出错",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	

	/**shashijie 2013-2-17 STORY 3366 获取SQL */
	private String getDPCodesSql() {
		String sqlString = "";
		//未选中查组合群表,反之侧查询公共表
		if (this.saveType.trim().equals("0")) {
			sqlString = "select * from " + pub.yssGetTableName("Tb_Dao_CusConfig") +
        		" where FCusCfgCode=" + dbl.sqlString(this.oldCusCfgCode);
		} else {
			sqlString = "select * from Tb_Dao_CusConfig " +
    		" where FCusCfgCode=" + dbl.sqlString(this.oldCusCfgCode);
		}
		return sqlString;
	}
	

	/**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {

    	/**add---shashijie 2013-3-1 STORY 3366 不管新增还是修改都要判断公共表与组合群表中是否存在主键重复数据*/
        if (btOper == YssCons.OP_ADD) { //如果是新增与复制 by leeyu 20090226 QDV4赢时胜(上海)2008年12月19日01_B MS00113
			//公共表
        	dbFun.checkInputCommon(btOper, "Tb_Dao_CusConfig",
                    "FCusCfgCode,FCusCfgName",
                    this.cusCfgCode ,//+ "," + this.cusCfgName,//主键为cusCfgCode 注释掉了名字edited by zhouxiang
                    "" + "," + "");
        	//组合群表
        	dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Dao_CusConfig"),
                                   "FCusCfgCode,FCusCfgName",
                                   this.cusCfgCode ,//+ "," + this.cusCfgName,//主键为cusCfgCode 注释掉了名字edited by zhouxiang
                                   "" + "," + "");

        } else {//修改,审核等等
        	if (this.saveType.equals("1")) {
        		//公共表
            	dbFun.checkInputCommon(btOper, "Tb_Dao_CusConfig" ,    
                        "FCusCfgCode,FCusCfgName",
                        this.cusCfgCode+ "," + this.cusCfgName,
                        this.oldCusCfgCode + "," + this.oldCusCfgName);
			} else {
				//MS01397    在数据接口界面，修改接口名称，点击确定后报错    QDV4赢时胜(测试)2010年7月5日04_B
	            dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Dao_CusConfig"),    
	                                   "FCusCfgCode,FCusCfgName",
	                                   this.cusCfgCode+ "," + this.cusCfgName,
	                                   this.oldCusCfgCode + "," + this.oldCusCfgName);
			}
        }
        /**end---shashijie 2013-3-1 STORY 3366*/
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
//      String strSql = "";
//      boolean bTrans = false; //代表是否开始了事务
//      Connection con = dbl.loadConnection();
//      try {
//      con.setAutoCommit(false);
//      bTrans = true;
//      strSql =
//            "update " + pub.yssGetTableName("Tb_Dao_CusConfig") + " set FCheckState = " +
//             this.checkStateId +
//             ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//             ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//             "' where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode)+
//            "  and FCusCfgName=" + dbl.sqlString(this.cusCfgName);
//
//       dbl.executeSql(strSql);
//  //-----------------------审核子表------------------------------
//      strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") +
//      " set FCheckState = " +
//      this.checkStateId +
//      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//      ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//      "' where FCusCfgCode = "+dbl.sqlString(this.cusCfgCode);
//      dbl.executeSql(strSql);
//
//
//      strSql = "update " + pub.yssGetTableName("Tb_Dao_FileContent") +
//      " set FCheckState = " +
//      this.checkStateId +
//      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//      ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//      "' where FCusCfgCode = "+dbl.sqlString(this.cusCfgCode);
//      dbl.executeSql(strSql);
//
//      strSql = "update " + pub.yssGetTableName("Tb_Dao_FileName") +
//      " set FCheckState = " +
//      this.checkStateId +
//      ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//      ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//      "' where FCusCfgCode = "+dbl.sqlString(this.cusCfgCode);
//      dbl.executeSql(strSql);
//  //-------------------------------------------------------------------


//         bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
//         原方法功能：只能处理期间连接的审核和未审核的单条信息。
//         新方法功能：可以处理回购品种信息设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
//         @throws YssException
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环还原
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事物
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个条目
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    
                    /**add---shashijie 2013-2-19 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
                    //自定义接口配置
                    strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_CusConfig"));
                    strSql += "  and FCusCfgName=" + dbl.sqlString(this.cusCfgName);
                    dbl.executeSql(strSql);//更新自定义接口配置表里的内容

                    //-----------------------审核子表------------------------------
                    //文件头(暂时用不到)
                    /*strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileInfo"));
                    dbl.executeSql(strSql);*///更新文件头设置表中的内容
                    //文件内容
                    strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileContent"));
                    dbl.executeSql(strSql);//更新文件内容设置表中的内容
                    //文件名
                    strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileName"));
                    dbl.executeSql(strSql);//更新文件名设置表中的内容
                    
                    //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
                    strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileFilter"));
                    dbl.executeSql(strSql);//更新文件筛选条件
                    //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
                    
                    /**shashijie 2011.03.23 STORY 557 合并文件名*/
                    strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileMergerName"));
	                dbl.executeSql(strSql);//更新文件名设置表中的内容
	                /**end*/
	                /**end---shashijie 2013-2-19 STORY 3366 */
                }
            }
            //-------------------------------------------------------------------

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("审核自定义接口配置信息出错!");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**shashijie 2013-2-19 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection con = dbl.loadConnection();
        try {
            con.setAutoCommit(false);
            bTrans = true;
            
            /**add---shashijie 2013-2-19 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //接口自定义配置
            strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_CusConfig"));
            strSql += "  and FCusCfgName=" + dbl.sqlString(this.cusCfgName);
            dbl.executeSql(strSql);
			//删除只是放入回收站中，不能删除子表，只是更新相关内容。
			//bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
			//----------------------------------更新子表--------------------------------
            //文件头(暂时不用)
            /*strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileInfo"));
            dbl.executeSql(strSql);*/ //更新文件头设置表中的内容
            //文件内容
            strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileContent"));
            dbl.executeSql(strSql); //更新文件内容设置表中的内容
            //文件名
            strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileName"));
            dbl.executeSql(strSql);//更新文件名设置表中的内容
            
            //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileFilter"));
	        dbl.executeSql(strSql);//更新文件名设置表中的内容
            //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	        
	        /**shashijie 2011.03.23 STORY 557 合并文件名*/
	        strSql = getChenkSqlJoin(pub.yssGetTableName("Tb_Dao_FileMergerName"));
	        dbl.executeSql(strSql);//更新文件名设置表中的内容
	        /**end*/
	        /**end---shashijie 2013-2-19 STORY 3366*/
	        
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
//------------------------------------------------------
        } catch (Exception ex) {
            throw new YssException("删除自定义接口配置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
        }

    }

	/**shashijie 2013-2-19 STORY 3366 获取SQL*/
	private String getChenkSqlJoin(String tableName) {
		String tabName = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			tabName = tableName;
		} else {
			tabName = tableName.substring(0,3) + tableName.substring(7);;
		}
		String sql = " update " + tabName +
	        " set FCheckState = " + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" +
	        YssFun.formatDatetime(new java.util.Date()) +
	        "' where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode);
		return sql;
	}
	

	/**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection con = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        ResultSet rs = null;

        try {
            //add   chenyibo    20071008   原因:如果在修改的时候在前台没有点预处理那个分页,就不加载
            //                                 预处理代码,导致传到后台是空的
            //-------------------------------------------------------------------------
            /*if(this.dPCodes.trim().length()==0)    //dpcode:预处理代码组
                      {
             strSql=" select FDPCodes from "+pub.yssGetTableName("Tb_Dao_CusConfig")+
                   " where FCusCfgCode="+dbl.sqlString(this.oldCusCfgCode);
             rs=dbl.openResultSet(strSql);
             while(rs.next())
             {
                this.dPCodes=rs.getString("FDPCodes");
             }    //Modify by Mao Qiwen  20080806  bug:0000356
                      }*/
            //上述代码全部注释，因为程序运行中并无实际意义，程序的运行是调用了原来的预处理。
            if (this.dPCodes == null) {
                this.dPCodes = ""; //若接口中没有预处理时报空指针 add liyu 1105
            }
            //-------------------------------------------------------------------------
            con.setAutoCommit(false);
            bTrans = true;
            
            /**add---shashijie 2013-2-17 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
            //获取修改SQL
            strSql = getUpdateSql();
            dbl.executeSql(strSql);
            //修改关联表数据
            updateAssociate();
            
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
            /**end---shashijie 2013-2-17 STORY 3366 */
        } catch (Exception ex) {
            throw new YssException("更改自定义接口配置信息出错!");
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";

    }

    /**shashijie 2013-2-18 STORY 3366 修改关联表数据*/
	private void updateAssociate() throws Exception {
		String strSql = "";
		String[] str = null;
		
		DaoFileInfoBean fileinfo = new DaoFileInfoBean();
        DaoFileContentBean filecontent = new DaoFileContentBean();
        DaoFileNameBean filename = new DaoFileNameBean();
        DaoFileFilterBean fileFilter=new DaoFileFilterBean();//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        /**增加合并文件名设置表的bean类    shashijie 2011.03.23 STORY #557 希望优化追加数据的功能*/
        DaoFileMergerNameBean fileMerger = new DaoFileMergerNameBean();
        fileMerger.setYssPub(pub);
        /**end*/
        fileinfo.setYssPub(pub);
        filecontent.setYssPub(pub);
        filename.setYssPub(pub);
        fileFilter.setYssPub(pub);//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        
		if (this.allSetDatas.length() > 0) {
            str = allSetDatas.split("\r\f");
            //文件头设置目前还没有用到，设置时保存出错，暂时屏蔽
            /*if (str[0].length() > 0) {
                fileinfo.setCusCfgCode(this.cusCfgCode);
                fileinfo.saveMutliSetting(str[0]);
            } else {
                strSql = "update " + pub.yssGetTableName("Tb_Dao_FileInfo") +
                    "  set FCusCfgCode = " +
                    dbl.sqlString(this.cusCfgCode) +
                    "  where FCusCfgCode=" +
                    dbl.sqlString(this.oldCusCfgCode);

                dbl.executeSql(strSql);
            }*/
            /**add---shashijie 2013-2-19 STORY 3366 增加OldCusCfgCode赋值以免发生修改code值是关联子表中的数据任然存在*/
            //文件内容
            filecontent.setSaveType(this.saveType);
            filecontent.setOldcusCfgCode(this.oldCusCfgCode);
            if (str[1].length() > 0) {
                filecontent.setCusCfgCode(this.cusCfgCode);
                filecontent.saveMutliSetting(str[1]);
            } else {
                strSql = getUpdateSqlJoin(pub.yssGetTableName("Tb_Dao_FileContent"));
                dbl.executeSql(strSql);
            }
            //文件名
            filename.setSaveType(this.saveType);
            filename.setOldcusCfgCode(this.oldCusCfgCode);
            if (str[2].length() > 0) {
                filename.setCusCfgCode(this.cusCfgCode);
                filename.saveMutliSetting(str[2]);
            } else {
                strSql = getUpdateSqlJoin(pub.yssGetTableName("Tb_Dao_FileName"));
                //"  and FValueType="+dbl.sqlString("");
                dbl.executeSql(strSql);
            }
            //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            fileFilter.setSaveType(this.saveType);
            fileFilter.setsOldCusCfgCode(this.oldCusCfgCode);
            if (str[3].length() > 0) {
            	fileFilter.setsCusCfgCode(this.cusCfgCode);
                //filename.setValueType("");
            	fileFilter.saveMutliSetting(str[3]);
                //    }
            } else {
                strSql = getUpdateSqlJoin(pub.yssGetTableName("Tb_Dao_FileFilter"));
                //"  and FValueType="+dbl.sqlString("");
                dbl.executeSql(strSql);
            }
            //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            /**shashijie 2011.03.23 STORY 557 合并文件名*/
            fileMerger.setSaveType(this.saveType);
            fileMerger.setOldcusCfgCode(this.oldCusCfgCode);
            if (str[4].length() > 0) {
            	fileMerger.setCusCfgCode(this.cusCfgCode);
            	fileMerger.saveMutliSetting(str[4]);
            } else {
                strSql = getUpdateSqlJoin(pub.yssGetTableName("Tb_Dao_FileMergerName"));
                dbl.executeSql(strSql);
            }
            /**end*/
            /**end---shashijie 2013-2-19 STORY 3366*/
        }
	}

	/**shashijie 2013-2-18 STORY 3366 获取修改关联数据SQL*/
	private String getUpdateSqlJoin(String tableName) {
		String sql = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			sql = " update " + tableName;
		} else {
			sql = " update " + tableName.substring(0,3) + tableName.substring(7);
		}
		sql += " set FCusCfgCode = " +
	        dbl.sqlString(this.cusCfgCode) +
	        "  where FCusCfgCode=" +
	        dbl.sqlString(this.oldCusCfgCode);
		return sql;
	}

	/**shashijie 2013-2-18 STORY 3366 获取修改SQL */
	private String getUpdateSql() {
		String sql = "";
		//如果是非选中状态处理组合群表,反之则处理公共表
		if (saveType.equals("0")) {
			sql = " update " + pub.yssGetTableName("Tb_Dao_CusConfig");
		} else {
			sql = " update Tb_Dao_CusConfig ";
		}

		sql += " set FCusCfgCode = " + dbl.sqlString(this.cusCfgCode) +
	        ",FCusCfgName=" + dbl.sqlString(this.cusCfgName) +
	        ",FCusCfgType=" + dbl.sqlString(this.cusCfgType) +
	        ",FFileType=" + dbl.sqlString(this.fileType) +
	        ",FTabName=" + dbl.sqlString(this.tabName) +
	        ",FFileNameDesc=" + dbl.sqlString(this.fileNameDesc) +
	        ",FFileInfoDesc=" + dbl.sqlString(this.fileInfoDesc) +
	        ",FFileCntDesc=" + dbl.sqlString(this.fileCntDesc) +
	        ",FFileTrailDesc=" + dbl.sqlString(this.fileTrailDesc) +
	        ",FDPCodes=" + dbl.sqlString(this.dPCodes) +
	        ",FEndMark = " + dbl.sqlString(this.endMark) +
	        ",FDesc=" + dbl.sqlString(this.desc) +
	        //添加对文件筛选条件的处理 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
	        ",FFileFilterDesc="+dbl.sqlString(this.fileFilterDesc)+
	        ",FSplitType=" + this.splitType +
	        ",FSplitMark=" + dbl.sqlString(this.splitMark) +
	        //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
	        ",FAutoFix=" + dbl.sqlString(this.autoFix?YssOperCons.YSS_DAO_YES:YssOperCons.YSS_DAO_NO) +
	        ",FCheckstate= " + (pub.getSysCheckState() ? "0" : "1") +
	        ",FCreator = " +
	        dbl.sqlString(this.creatorCode) + ",FCreateTime = " +
	        dbl.sqlString(this.creatorTime) + ",FCheckUser = " +
	        (pub.getSysCheckState() ? "' '" :
	        dbl.sqlString(this.creatorCode)) +
	        ", FCfgENCode = " + dbl.sqlString(this.sCgfENCode)+//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
	        ",FEXCELPWD=" + dbl.sqlString(this.exlSetPwd)+// add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A 
	        ",FMenuBarCode=" + dbl.sqlString(this.menuBarCode)+// add by guolongchao 20110905 STORY　1285 添加菜单条代码
	        ",FMenuBarName=" + dbl.sqlString(this.menuBarName)+// add by guolongchao 20110905 STORY　1285 添加菜单条名称
	        ", FMerger = " + dbl.sqlString(this.merger)+/**shashijie 2011.2.17 STORY #557 希望优化追加数据的功能*/ 
	         ",FFILEROWS=" + dbl.sqlString(this.fileRows)+//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
	        " where FCusCfgCode = " + dbl.sqlString(this.oldCusCfgCode) +
	        " and FCusCfgName=" + dbl.sqlString(this.oldCusCfgName);
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
        	//20120215 added by liubo.Bug #3796
        	//调度方案执行界面，跨组合群处理接口时，获取接口信息只会获取当前组合群下的某个接口的信息。在此进行修改，根据实际执行的组合群进行处理
        	//===============================================
        	String sCurAssetGroup = "";
        	if (this.AssetGroupCode != null)
        	{
        		if(!this.AssetGroupCode.trim().equals(""))
        		{
        			sCurAssetGroup = (pub.getPrefixTB().equals(this.AssetGroupCode) ? pub.getPrefixTB() : this.AssetGroupCode);
        		}
        		else
        		{
        			sCurAssetGroup = pub.getPrefixTB();
        		}
        	}
        	else
        	{
        		sCurAssetGroup = pub.getPrefixTB();
        	}
        	//======================end=========================
        	
        	
        	/**add---shashijie 2013-2-28 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
    		//组合群SQL
        	strSql = " Select * From ( ";
        	strSql += getSelectListGetSetting("Tb_" +  sCurAssetGroup + "_Dao_CusConfig ","0");
            
            strSql += " Union All ";
            //公共表SQL
            strSql += getSelectListGetSetting("Tb_Dao_CusConfig","1");
            strSql += " ) y order by y.FCusCfgCode";
    		/**end---shashijie 2013-2-28 STORY 3366 */
            
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.cusCfgCode = rs.getString("FCusCfgCode");
                this.cusCfgName = rs.getString("FCusCfgName");
                this.cusCfgType = rs.getString("FCusCfgType");
                this.fileType = rs.getString("FFileType");
                this.fileTypeValue = rs.getString("FFileTypeValue");
                this.tabName = rs.getString("FTabName");
                this.dPCodes = rs.getString("FDPCodes");
                this.splitType = rs.getString("FSplitType");
                this.splitMark = rs.getString("FSplitMark");
                this.endMark = rs.getString("FEndMark");
                //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
                this.autoFix = rs.getString("FAutoFix").equalsIgnoreCase(YssOperCons.YSS_DAO_YES);
                this.sCgfENCode = rs.getString("FCfgENCode");//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
                this.exlSetPwd = rs.getString("FEXCELPWD");
                this.merger = rs.getString("FMerger");//shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 
                this.fileRows=rs.getString("FfileRows");//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
                /**add---shashijie 2013-3-1 STORY 3366 给存储类型赋值*/
                setSaveTypeValue(rs);
				/**end---shashijie 2013-3-1 STORY 3366*/
            }
        } catch (Exception e) {
            throw new YssException("获取接口信息出错!");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;

    }

	/**shashijie 2013-3-1 STORY 3366 获取SQL调度方案执行*/
	private String getSelectListGetSetting(String tableName, String saveType) throws YssException {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " y.* from " +
	        " ( select FCusCfgCode,FCheckState from " +
	        //20120215 modified by liubo.Bug #3796
	        //调度方案执行界面，跨组合群处理接口时，获取接口信息只会获取当前组合群下的某个接口的信息。在此进行修改，根据实际执行的组合群进行处理
	        //=================================
	        //pub.yssGetTableName("Tb_Dao_CusConfig") + " " +
	        tableName +
	     	//==================end===============
	        " where FCheckState =1 and FCusCfgCode= " +
	        dbl.sqlString(this.cusCfgCode) +
	        " group by FCusCfgCode,FCheckState) x join" +
	        " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName," +
	        " d.FVocName as FSplitTypeValue ,e.FVocName as FFileTypeValue , " +
	        " h.FVocName as CfgENCode ," +//xuqiji 20100303 MS00948  QDV4易方达2010年1月22日01_A 
	        " f.FTABLEDESC as FTabDesc from " +
	        //20120215 modified by liubo.Bug #3796
	        //调度方案执行界面，跨组合群处理接口时，获取接口信息只会获取当前组合群下的某个接口的信息。在此进行修改，根据实际执行的组合群进行处理
	        //=================================
	        //pub.yssGetTableName("Tb_Dao_CusConfig") + " a" +
	        tableName +" a" +
	        //==============end====================
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList ) b on a.FCreator = b.FUserCode" +
	        " left join (select FUserCode,FUserName from Tb_Sys_UserList ) c on a.FCheckUser = c.FUserCode" +
	        " left join Tb_Fun_Vocabulary d on a.FSplitType = d.FVocCode and d.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_INFACE_SPLITTYPE) +
	        " left join Tb_Fun_Vocabulary e on a.FFileType = e.FVocCode and e.FVocTypeCode = " +
	        dbl.sqlString(YssCons.YSS_FILETYPE) +
	        //----------------xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A -----------//
	        " left join Tb_Fun_Vocabulary h on a.FCfgENCode = h.FVocCode and h.FVocTypeCode ="+
	        dbl.sqlString(YssCons.YSS_ENCODING_TYPE)+
	        //------------------------------end 20100303--------------------------//
	        " left join (select FTabName,FTABLEDESC from TB_FUN_DATADICT where FCheckState=1) " +
	        " f on a.FTabName = f.FTabName" +
	        buildFilterSql() +
	        " ) y on y.FCusCfgCode=x.FCusCfgCode ";
		return sql;
	}
	

	/**
     * saveMutliSetting
     *
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) {
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.cusCfgCode).append("\t");
        buf.append(this.cusCfgName).append("\t");
        buf.append(this.cusCfgType).append("\t");

        buf.append(this.cusCfgTypeName).append("\t");

        buf.append(this.fileType).append("\t");
        buf.append(this.tabName).append("\t");

        buf.append(this.tabDesc).append("\t");
        buf.append(this.fileNameDesc).append("\t");
        buf.append(this.fileInfoDesc).append("\t");
        buf.append(this.fileCntDesc).append("\t");
        buf.append(this.fileTrailDesc).append("\t");

        buf.append(this.dPCodes).append("\t");
        buf.append(this.splitType).append("\t");
        buf.append(this.splitMark).append("\t");
        buf.append(this.desc).append("\t");
        // buf.append(this.groupCode).append("\t");
        // buf.append(this.groupName).append("\t");
        buf.append(this.endMark).append("\t");
        buf.append(this.fileCusCfg).append("\t"); //11.23 lzp add
		buf.append(this.fileFilterDesc).append("\t");//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
        //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
        buf.append(this.autoFix).append("\t");
        //----add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B
        //--------xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A -------//
        buf.append(this.sCgfENCode).append("\t");
        buf.append(this.isGroup).append("\t");
        //----------------------end 20100303---------------------//
        if(isGroup){//若为跨组合群操作
        	buf.append(this.AssetGroupCode).append("\t");//拼接组合群代码
            buf.append(this.AssetGroupName).append("\t");//拼接组合群名称
        }
        //-----add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A ----//
        //edit by licai 20101112 BUG #345 自定义接口配置修改文件名设置，不做修改，点击确定报错 
        this.exlSetPwd=this.exlSetPwd==null?"":this.exlSetPwd;
        //edit by licai 20101112 BUG #345 自定义接口配置修改文件名设置，不做修改，点击确定报错 ==end==
        buf.append(this.exlSetPwd.trim()).append("\t");
        this.fileRows=this.fileRows==null?"":this.fileRows;//#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
        buf.append(this.fileRows.trim()).append("\t");    
      //----add by songjie 2009.12.30 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B
        /**shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 */
        buf.append(this.merger).append("\t");
        buf.append(this.menuBarCode==null?"":this.menuBarCode).append("\t");// add by guolongchao 20110905 STORY 1285 添加菜单条代码
        buf.append(this.menuBarName==null?"":this.menuBarName).append("\t");// add by guolongchao 20110905 STORY 1285 添加菜单条名称
        
        /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
        buf.append(this.saveType.trim()).append("\t");
		/**end---shashijie 2013-2-17 STORY 3366*/
        
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        String strSql = "", sReturn = "", sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;
        String fsubreps[] = null;
        String fsubrep = "'all',";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            if (sType.equalsIgnoreCase("setsubrepcodes")) {
                //给报表组设置子报表
                sReturn = "false";
                strSql = "update " + pub.yssGetTableName("Tb_Rep_Group") +
                    " set FCusCfgCode = " + dbl.sqlString(this.cusCfgCode);

                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
                sReturn = "true";
                return sReturn;

            }

            if (sType.equalsIgnoreCase("getsubrepcodes")) {
                //获取报表组的子报表
                sHeader = "接口类型\t接口类型名称";
                if (!this.cusCfgCode.equalsIgnoreCase("")) {
                    fsubreps = this.cusCfgCode.split(",");
                    for (int i = 0; i < fsubreps.length; i++) {
                        fsubrep = fsubrep + dbl.sqlString(fsubreps[i]) +
                            ",";
                    }
                }
                if (fsubrep.length() > 0) {
                    fsubrep = YssFun.left(fsubrep, fsubrep.length() - 1);
                }

                strSql = "select y.* from " +
                    "(select FCusCfgCode,FCheckState from " +
                    pub.yssGetTableName("Tb_Dao_CusConfig") +
                    " where FCheckState <> 2 and FCusCfgCode in ("
                    + fsubrep +
                    " ) group by FCusCfgCode,FCheckState " +
                    ") x join" +
                    " (select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName,d.FRepFormatName," +
                    "e.FVocName as FRepTypeValue,f.FCtlGrpName,g.FRepDsName from " +
                    pub.yssGetTableName("Tb_Rep_Custom") +
                    " a " +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                    " left join (select FRepFormatCode,FRepFormatName from " +
                    pub.yssGetTableName("Tb_Rep_Format") +
                    " where FCheckState = 1) d on a.FRepFormatCode = d.FRepFormatCode" +
                    " left join Tb_Fun_Vocabulary e on a.FRepType = e.FVocCode and e.FVocTypeCode = " +
                    dbl.sqlString(YssCons.YSS_RCT_REPTYPE) +
                    " left join (select FCtlGrpCode,FCtlGrpName from " +
                    pub.yssGetTableName("Tb_Rep_ParamCtlGrp") +
                    " where FCheckState = 1) f on a.FCtlGrpCode = f.FCtlGrpCode" +
                    //--------------------------------------------------------------
                    " left join (select FRepDsCode,FRepDsName from " +
                    pub.yssGetTableName("Tb_Rep_DataSource") +
                    " where FCheckState =1) g on a.FParamSource=g.FRepDsCode" +
                    //--------------------------------------------------------------
                    " ) y on x.FCusRepCode = y.FCusRepCode" +
                    " order by  y.FCheckState, y.FCreateTime desc";

                rs = dbl.openResultSet(strSql);

                while (rs.next()) {
                    bufShow.append( (rs.getString("FCusRepCode") + "").trim()).
                        append("\t");
                    bufShow.append( (rs.getString("FCusRepName") + "").trim()).
                        append(YssCons.YSS_LINESPLITMARK);

                    DaoGroupSetBean group = new DaoGroupSetBean();
                    group.setYssPub(pub);
                    group.setResultSetAttr(rs);

                    bufAll.append(group.buildRowStr()).append(YssCons.
                        YSS_LINESPLITMARK);
                }

                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }
                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }

                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
            }
            /** 增加获取接口信息的方法 QDV4华夏2009年6月11日01_A MS00496 by leeyu 20090611 */
            if (sType.equalsIgnoreCase("getSetting")) {
                this.getSetting(); //获取接口类型
                return this.buildRowStr();
            }
            //添加获取通用参数配置的信息 QDV4华夏2009年6月15日01_A MS00509  by leeyu 20090616
            if (sType.equalsIgnoreCase("getPubPara")) {
                CtlPubPara ctlPara = new CtlPubPara();
                ctlPara.setYssPub(pub);
                sReturn = ctlPara.getAutoOperData();
                return sReturn;
            }
            //---add by songjie 2011.09.06 需求 1289 QDV4长盛基金2011年6月29日01_A start---//
            //获取自定义接口代码对应的文件路径
            if (sType.equalsIgnoreCase("getFileName")) {
                return getCfgFileName();
            }
            if (sType.equalsIgnoreCase("getFilePathType")) {
                return getFilePathType();
            }
            //---add by songjie 2011.09.06 需求 1289 QDV4长盛基金2011年6月29日01_A start---//
        } catch (Exception e) {
            throw new YssException("解析接口代码信息出错");
        } finally {
            dbl.closeResultSetFinal(rs);//关闭游标资源 modify by sunkey 20090604 MS00472:QDV4上海2009年6月02日01_B
            /**add---shashijie 2013-2-19 STORY 3366 增加关闭事物处理*/
            dbl.endTransFinal(conn, bTrans);
			/**end---shashijie 2013-2-19 STORY 3366*/
        }
        return "";

    }
    
    /**
     * add by songjie 
     * 2011.09.09
     * STORY #1289 
     * QDV4长盛基金2011年6月29日01_A
     * 获取自定义接口文件路径设置类型
     * @return
     * @throws YssException
     */
    private String getFilePathType() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	String fileType = "";
    	try{
    		strSql = " select FVocName from Tb_Fun_Vocabulary where " +
    		"FVocTypeCode = 'dao_Inface_FileNameC' and FCheckState = 1 and FVocCode <> 'No' " ;
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			fileType += "[" + rs.getString("FVocName") + "],";
    		}
    		
    		if(fileType.length() > 1){
    			fileType = fileType.substring(0,fileType.length() -1);
    		}
    		
    		return fileType;
    	}catch(Exception e){
    		throw new YssException("获取自定义接口文件路径类型出错");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    /**
     * add by songjie 
     * 2011.09.06
     * 需求 1289 QDV4长盛基金2011年6月29日01_A
     * 获取自定义接口代码对应的文件路径
     * @return
     * @throws YssException
     */
    private String getCfgFileName() throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	String fileName = "";
    	try{
    		/**add---shashijie 2013-2-18 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变
    		 * 目前接口处理没有传入某个接口是公共的还是组合群的,这里一旦两表中的主键有重复还是以公共表优先*/
    		//组合群SQL
    		strSql = getSelectCusconfigByFCusCfgCode(pub.yssGetTableName("Tb_Dao_Cusconfig"),"0");
            
    		strSql += " Union All ";
            //公共表SQL
    		strSql += getSelectCusconfigByFCusCfgCode("Tb_Dao_Cusconfig","1");
    		/**end---shashijie 2013-2-18 STORY 3366 */
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			fileName = rs.getString("FFileNameDesc");
    		}
    		
    		return fileName;
    	}catch(Exception e){
    		throw new YssException("获取自定义接口文件路径出错");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }

	/**shashijie 2013-2-28 STORY 3366 获取SQL*/
	private String getSelectCusconfigByFCusCfgCode(String tableName,
			String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " FFileNameDesc from " + tableName +
			" where FCheckState = 1 and FCusCfgCode = " + dbl.sqlString(this.cusCfgCode);
		return sql;
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
            }else if(sRowStr.indexOf("FENGKONG_CURRENTGROUPCODE")!=-1){//add by guyichuan STORY #897
            	this.currentPortGroupCode=sRowStr.split("\t")[1].trim();
            	this.filterType=new DaoCusConfigureBean();
            	filterType.cusCfgType=String.valueOf(2);         //风控-导出
            	return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                if (sRowStr.split("\r\t").length == 3) {
                    this.allSetDatas = sRowStr.split("\r\t")[2];
                }
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
            reqAry = sTmpStr.split("\t");

            this.cusCfgCode = reqAry[0];
            this.cusCfgName = reqAry[1];
            this.cusCfgType = reqAry[2];
            this.fileType = reqAry[3];

            this.tabName = reqAry[4];

            this.fileNameDesc = reqAry[5];
            this.fileInfoDesc = reqAry[6];
            this.fileCntDesc = reqAry[7];
            this.fileTrailDesc = reqAry[8];
            this.dPCodes = reqAry[9];

            this.splitType = reqAry[10];
            this.splitMark = reqAry[11];
            //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 
            if (reqAry[12] != null ){
            	if (reqAry[12].indexOf("【Enter】") >= 0){
            		this.desc = reqAry[12].replaceAll("【Enter】", "\r\n");
            	}
            	else{
            		this.desc = reqAry[12];
            	}
            }            
//            this.desc = reqAry[12];
          //edit by licai 20101112 BUG #374 接口字典设置描述含有回车的问题 ==end=            

            this.checkStateId = Integer.parseInt(reqAry[13]);
            this.oldCusCfgCode = reqAry[14];
            this.oldCusCfgName = reqAry[15];
            //  this.groupCode = reqAry[16];
            //  this.oldGroupCode = reqAry[17];
            this.endMark = reqAry[16];
            this.fileCusCfg = reqAry[17]; //11.23 lzp add
			this.fileFilterDesc = reqAry[18];//by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            //2009-10-29 蒋锦 添加 自动匹配后缀 MS00005 QDV4.1赢时胜（上海）2009年9月28日04_A
            this.autoFix = Boolean.valueOf(reqAry[19]).booleanValue();
            
            //----- add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ------//
            if(reqAry.length >= 21){
            	this.AssetGroupCode = reqAry[20];//组合群代码
            }
            //-------xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A -----------//
            this.sCgfENCode = reqAry[21]; 
            //----------------------end 20100303---------------------//
            this.exlSetPwd = reqAry[22];//---add by wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A---// 
            //----- add by songjie 2009.12.29 QDII维护:MS00890 QDV4赢时胜上海2009年12月24日02_B ------//
            /**shashijie 2011.2.17 STORY #557 希望优化追加数据的功能 */
            this.merger = reqAry[24];
            this.fileRows=reqAry[23]; //#2580:: add by wuweiqi 20110217 接口处理需支持按指定行数导出多个文件
            if (this.fileCusCfg.length() == 0 ||
                this.fileCusCfg.equalsIgnoreCase("null")) {
                this.fileCusCfg = " ";
            }
            this.menuBarCode=reqAry[25];//add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现 
            this.menuBarName=reqAry[26];//add by guolongchao 20110905 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            /**add---shashijie 2013-2-17 STORY 3366 增加存储类型-公共表复选框*/
            this.saveType = reqAry[27];
			/**end---shashijie 2013-2-17 STORY 3366*/
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new DaoCusConfigureBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析自定义接口配置信息出错", e);
        }
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * 2008-5-27  修改
     * 修改人 单亮
     * @return String
     * @throws YssException
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.cusCfgCode.length() != 0) {
                sResult = sResult + " and a.FCusCfgCode like '" +
                    filterType.cusCfgCode.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.cusCfgName.length() != 0) {
                sResult = sResult + " and a.FCusCfgName like '" +
                    filterType.cusCfgName.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.cusCfgType.equalsIgnoreCase("99") &&
                this.filterType.cusCfgType.length() != 0) { //2008-5-27 单亮 添加 !this.filterType.cusCfgType.equalsIgnoreCase("99") &&
                //这里采用in QDV4华夏2009年6月11日01_A MS00496 by leeyu 20090611
                sResult = sResult + " and a.FcusCfgType in (" +
                    operSql.sqlCodes(filterType.cusCfgType) + ")";
            }
            if (this.filterType.endMark.length() != 0) {
                sResult = sResult + " and a.FEndMark like '" +
                    filterType.endMark.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.splitMark.length() != 0) {
                sResult = sResult + " and a.FSplitMark like '" +
                    filterType.splitMark.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.splitType.equalsIgnoreCase("99") &&
                this.filterType.splitType.length() != 0) { //2008-5-27 单亮 添加 ! this.filterType.splitType.equalsIgnoreCase("99") &&
                sResult = sResult + " and a.FSplitType like '" +
                    filterType.splitType.replaceAll("'", "''") + "%'";
            }
            if (!this.filterType.fileType.equalsIgnoreCase("99") &&
                this.filterType.fileType.length() != 0) { //2008-5-27 单亮 添加! this.filterType.fileType.equalsIgnoreCase("99") &&
                sResult = sResult + " and a.FFileType like '" +
                    filterType.fileType.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.tabName.length() != 0) {
                sResult = sResult + " and a.FTabName like '" +
                    filterType.tabName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fileNameDesc.length() != 0) {
                sResult = sResult + " and a.FFileNameDesc like '" +
                    filterType.fileNameDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fileInfoDesc.length() != 0) {
                sResult = sResult + " and a.FFileInfoDesc like '" +
                    filterType.fileInfoDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fileCntDesc.length() != 0) {
                sResult = sResult + " and a.FFileCntDesc like '" +
                    filterType.fileCntDesc.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.fileTrailDesc.length() != 0) {
                sResult = sResult + " and a.FFileTrailDesc like '" +
                    filterType.fileTrailDesc.replaceAll("'", "''") + "%'";
            }
            //-----xuqiji 20100303MS00948  QDV4易方达2010年1月22日01_A --------//
            if(this.filterType.sCgfENCode.length() != 0&& !this.filterType.sCgfENCode.equalsIgnoreCase("99")){
            	sResult = sResult + " and a.FCfgENCode = '" + this.filterType.sCgfENCode.replaceAll("'","''") + "'";
            }
            //---------------------end 20100303----------------------//
            //-----wuweiqi 20101102 QDV4深圳赢时胜2010年10月8日01_A -------------------//
            //edit by licai 20101130 BUG #506 自定义接口设置界面问题 
            /*if(this.filterType.exlSetPwd.length()!=0)
            {
            	 sResult = sResult + " and a.FEXCELPWD like '" +
                 filterType.exlSetPwd.replaceAll("'", "''") + "%'";
            }*/
          //edit by licai 20101130==========================end
            //---------------------end 20101102----------------------//
            if (this.filterType.merger.length() != 0) {
                sResult = sResult + " and a.FMerger like '" +
                    filterType.merger.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }

    /**
     * bug MS00169  QDV4赢时胜上海2009年1月7日03_B.doc  2009.01.23 方浩
     * 回收站的删除功能调用此方法deleteRecycleData()
     * 从数据库删除数据，即彻底删除数据,可以多个一删除
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
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
                    
                    /**add---shashijie 2013-2-19 STORY 3366 重构代码,增加对公共表的添加SQL语句,其他不变*/
                    //自定义接口配置
                    strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_CusConfig"));
                    strSql += "  and FCusCfgName=" + dbl.sqlString(this.cusCfgName);
                    dbl.executeSql(strSql); //删除自定义接口配置表里的内容

                    //----------------------------------删除子表--------------------------------
                    //文件头(暂时无用)
                    /*strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_FileInfo"));
                    dbl.executeSql(strSql);*/ //删除文件头设置表中的内容
                    //文件内容
                    strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_FileContent"));
                    dbl.executeSql(strSql); //删除文件内容设置表中的内容
                    //文件名
                    strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_FileName"));
                    dbl.executeSql(strSql); //删除文件名设置表中的内容
                    //文件筛选条件
                    //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
                    strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_FileFilter"));
                    dbl.executeSql(strSql); //删除文件筛选条件设置表中的内容
                    //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
                    
                    /**shashijie 2011.03.23 STORY 557 合并文件名*/
                    strSql = getDeleteSQl(pub.yssGetTableName("Tb_Dao_FileMergerName"));
		            dbl.executeSql(strSql); //删除合并文件名设置表中的内容
                    /**end*/
					/**end---shashijie 2013-2-19 STORY 3366*/
		            
                }
            }

            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    /**shashijie 2013-2-19 STORY 3366 获取SQL */
	private String getDeleteSQl(String tableName) {
		String tabName = "";
		//非选中状态处理组合群表,反之处理公共表
		if (this.saveType.equals("0")) {
			tabName = tableName;
		} else {
			tabName = tableName.substring(0,3) + tableName.substring(7);;
		}
		String sql = " delete from " + tabName + " where FCusCfgCode = " + dbl.sqlString(this.cusCfgCode);
		return sql;
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

    /**
     * add by songjie 
     * 2009.12.28
     * QDII维护:MS00890
     * QDV4赢时胜上海2009年12月24日02_B
     * 查询出跨组合群操作中 多个组合群相关的风控接口导入导出的接口数据
     */
    public String getListViewGroupData1() throws YssException {
    	//modified by guyichuan 20110608 STORY #897 查询当前组合群下接口信息
        String sResult = "";
        String trueAssetGroupCode = "";//当前组合群代码
    	try{
    		trueAssetGroupCode = pub.getAssetGroupCode();//保存当前组合群代码
    		if(this.currentPortGroupCode!=null&&this.currentPortGroupCode.length()!=0){
    			//this.AssetGroupCode = this.currentPortGroupCode;//取得一个组合群代码
    			pub.setPrefixTB(this.currentPortGroupCode);//将该组合群代码设为表前缀
    		}
    			sResult = getListViewData2();//查询出循环中的组合群相关的接口数据
            return sResult;
            //--end-STORY #897--
    	}
    	catch(Exception e){
    		throw new YssException("获取风控接口设置信息出错！", e);
    	}
    	finally{
    		pub.setPrefixTB(trueAssetGroupCode);//将组合群代码设置回当前系统的组合群代码
    	}
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
    //-------xuqiji 20100303-MS00948  QDV4易方达2010年1月22日01_A -----//
	public String getSCgfENCode() {
		return sCgfENCode;
	}

	public void setSCgfENCode(String cgfENCode) {
		sCgfENCode = cgfENCode;
	}
	//-----------------------end 20100303--------------------//

	/**add---shashijie 2013-2-17 返回 saveType 的值*/
	public String getSaveType() {
		return saveType;
	}

	/**add---shashijie 2013-2-17 传入saveType 设置  saveType 的值*/
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	
	
	
}
