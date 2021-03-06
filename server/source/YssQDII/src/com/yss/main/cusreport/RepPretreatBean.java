package com.yss.main.cusreport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Hashtable;
import oracle.sql.CLOB;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.DaoPretreatBean;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/*************************************************
 * 预处理设置
 * @author benson
 *
 */
public class RepPretreatBean extends BaseDataSettingBean implements
		IDataSetting {
	//~Properties 
	private String dPDsCode = ""; //数据源代码
    private String dPDsName = ""; //数据源名称
    private int dsType = 99; //数据源类型
    private String targetTabCode = ""; //目标表
    private boolean bIsShow = false;
    private String targetTabName = "";
    private String beanId = ""; //配置的BeanID
    private String dataSource = ""; //数据源
    private String desc = ""; //描述
    private String dPreTreatRecycled = ""; //回收站
    private String oldDPDsCode = "";
    private String relaCompareCode = ""; //关联核对源代码 
    private RepPretreatBean filterType;

    private String tabType = ""; //表类型
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~以下参数用于报表预处理~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    private String portCodes = "";
    private java.util.Date beginDate;
    private java.util.Date endDate;
    
    /**shashijie 2012-2-10 BUG 3824 */
	private String checkAccLinks = "";//批量数据处理
	/**end*/

	//add by huangqirong 2012-01-05 story #1284
    private Hashtable targetTabFileds = new Hashtable();	//存储目标表所有字段
    private String rootFDsCode = "FROOTDSCODE";			//报表数据生成时 固化字段
    private String currentDataSourceCode = "";			//当前预处理所在的数据源代码
    
    
    public String getCurrentDataSourceCode() {
		return currentDataSourceCode;
	}

	public void setCurrentDataSourceCode(String currentDataSourceCode) {
		this.currentDataSourceCode = currentDataSourceCode;
	}

	public Hashtable getTargetTabFileds() {
		return targetTabFileds;
	}

	public void setTargetTabFileds(Hashtable targetTabFileds) {
		this.targetTabFileds = targetTabFileds;
	}

	public String getRootFDsCode() {
		return rootFDsCode;
	}

	public void setRootFDsCode(String rootFDsCode) {
		this.rootFDsCode = rootFDsCode;
	}
    
    public String getPortCodes() {
		return portCodes;
	}

	public void setPortCodes(String portCodes) {
		this.portCodes = portCodes;
	}

	public java.util.Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(java.util.Date beginDate) {
		this.beginDate = beginDate;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
    public String getDPDsCode() {
        return this.dPDsCode;
    }

    public void setDPDsCode(String dPDsCode) {
        this.dPDsCode = dPDsCode;
    }

    public String getDPDsName() {
        return this.dPDsName;
    }

    public void setDPDsName(String dPDsName) {
        this.dPDsName = dPDsName;
    }

    public int getDsType() {
        return this.dsType;
    }

    public void setDsType(int dsType) {
        this.dsType = dsType;
    }

    public String getTargetTabCode() {
        return this.targetTabCode;
    }

    public void setTargetTabCode(String targetTab) {
        this.targetTabCode = targetTab;
    }

    public String getBeanId() {
        return this.beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getOldDPDsCode() {
        return oldDPDsCode;
    }

    public String getTargetTabName() {
        return targetTabName;
    }

    public String getRelaCompareCode() {
        return relaCompareCode;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setOldDPDsCode(String oldDPDsCode) {
        this.oldDPDsCode = oldDPDsCode;
    }

    public void setTargetTabName(String targetTabName) {
        this.targetTabName = targetTabName;
    }

    public void setRelaCompareCode(String relaCompareCode) {
        this.relaCompareCode = relaCompareCode;
    }
    
    public String getTabType() {
		return tabType;
	}

	public void setTabType(String tabType) {
		this.tabType = tabType;
	}
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


	//~Constructors
    public RepPretreatBean() {
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    /**
     * add by huangqirong 2012-01-04 story #1284 
     * 查询目标表字段
     */
    public void getTgFields(String tgTabName) throws YssException {
    	
    	if(tgTabName == null || tgTabName.trim().length()==0)
    		return;
    	
    	String strSql ="";
    	ResultSet rs=null;
    	this.targetTabFileds.clear();//清除数据
    	try {
    	
	    	strSql = "select * from TB_FUN_DATADICT where FTABNAME = "+dbl.sqlString(tgTabName);			
	    	rs = dbl.openResultSet(strSql);
	    	while (rs.next()) {
	    		if(!this.targetTabFileds.containsKey(rs.getString("FFieldName"))){
	    			this.targetTabFileds.put(rs.getString("FFieldName"), rs.getString("FFieldName"));
	    		}
	    	}    	
    	} catch (Exception e) {
    		throw new YssException("查询目标表字段出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			//dbl.endTransFinal(conn, bTrans);
		}
    }
    
    /**
     * add by huangqirong 2012-01-04 story #1284 
     * 修改目标表添加字段
     * tgTabName 表名
     * fields	 字段信息包括 类型 大小
     */
    public void addTgField(String tgTabName , String fields, String other) throws YssException{
    	boolean bTrans = false; //代表是否开始了事务
    	Connection conn=dbl.loadConnection();    	
    	String sql="alter table " + tgTabName+" add " + fields + " " + other;    	
    	try {
    		boolean is = dbl.isFieldExist(dbl.openResultSet("select * from " + tgTabName), fields);
    		if(!is){
	    		dbl.executeSql(sql);
	    		conn.commit();            
	            conn.setAutoCommit(true); 
    		}
		} catch (Exception e) {
			throw new YssException("修改目标表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * buildSql
     * 功能：处理数据源中<D>,<S>之类的参数
     * @return String
     * 修改时间：2008年6月24号
     * 修改人：蒋春
     *
     * 修改时间：2008-6-30
     * 修改人：单亮
     * 修改原因：不支持以前的变量替换方式，现在修改为两种同时都可用
     */
    public String buildSql(String sDs) { //将此方法公共方法,在外面要调用 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        //替换老的标识 单亮 2008-6-30  begin
        String sInd = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        for (int i = 0; i < 100; i++) {
            sInd = "<" + (i + 1) + ">";
            iPos = sDs.indexOf(sInd);
            if (iPos <= 0) {
                sInd = " < " + (i + 1) + " >";
                iPos = sDs.indexOf(sInd);
            }
            if (iPos > 0) {
                sDataType = sDs.substring(iPos - 1, iPos);
                if (sDataType.equalsIgnoreCase("S")) {
                    sSqlValue = dbl.sqlString("");
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = "0";
                } else if (sDataType.equalsIgnoreCase("D")) {
                    sSqlValue = dbl.sqlDate("1900-01-01");
                } else if (sDataType.equalsIgnoreCase("N")) {
                    sSqlValue = "''";
                } 
                sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
            }
            sDs = parseSqlFuns(sDs); //2009-01-07 MS00175 解析接口公式
            sDs = replaceSplitStr(sDs); //添加对特殊字符的处理byleeyu QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
        }
        
        //添加对<PP>、<Port>函数的处理 <PP>代表组合所占比例<Port>代表当前处理的组合
		//MS00817:QDV4工银2009年11月17日01_A sunkey@Modify 20091125
        sDs = sDs.replaceAll("<PP>", "1").replaceAll("<Port>", "''");
        //解析轧差函数
        sDs = parseTOB(sDs);
        
        
        
        sDs = sDs.replace('[', ' ');
        sDs = sDs.replace(']', ' ');
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        }
        if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }
        if (sDs.indexOf("<s>") > 0) {
            sDs = sDs.replaceAll("<s>", "''");
        }
        if (sDs.indexOf("< s >") > 0) {
            sDs = sDs.replaceAll("< s >", "''");
        }
        if (sDs.indexOf("<S>") > 0) {
            sDs = sDs.replaceAll("<S>", "''");
        }
        if (sDs.indexOf("< S >") > 0) {
            sDs = sDs.replaceAll("< S >", "''");
        }
        if (sDs.indexOf("<s1>") > 0) {
            sDs = sDs.replaceAll("<s1>", "''");
        }
        if (sDs.indexOf("< s1 >") > 0) {
            sDs = sDs.replaceAll("< s1 >", "''");
        }
        if (sDs.indexOf("<S1>") > 0) {
            sDs = sDs.replaceAll("<S1>", "''");
        }
        if (sDs.indexOf("< S1 >") > 0) {
            sDs = sDs.replaceAll("< S1 >", "''");
        }
        //-------- add by wangzuochun 2009.11.23 MS00822 接口预处理时，对预处理数据源里的组合参数没有做解析 QDV4南方2009年11月19日01_B 
        if (sDs.indexOf("<s2>") > 0) {
            sDs = sDs.replaceAll("<s2>", "''");
        }
        if (sDs.indexOf("< s2 >") > 0) {
            sDs = sDs.replaceAll("< s2 >", "''");
        }
        if (sDs.indexOf("<S2>") > 0) {
            sDs = sDs.replaceAll("<S2>", "''");
        }
        if (sDs.indexOf("< S2 >") > 0) {
            sDs = sDs.replaceAll("< S2 >", "''");
        }
        if (sDs.indexOf("<s3>") > 0) {
            sDs = sDs.replaceAll("<s3>", "''");
        }
        if (sDs.indexOf("< s3 >") > 0) {
            sDs = sDs.replaceAll("< s3 >", "''");
        }
        if (sDs.indexOf("<S3>") > 0) {
            sDs = sDs.replaceAll("<S3>", "''");
        }
        if (sDs.indexOf("< S3 >") > 0) {
            sDs = sDs.replaceAll("< S3 >", "''");
        }
        //----------------------- MS00822 ---------------------//
        if (sDs.indexOf("<d>") > 0) {
            sDs = sDs.replaceAll("<d>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d >") > 0) {
            sDs = sDs.replaceAll("< d >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D>") > 0) {
            sDs = sDs.replaceAll("<D>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D >") > 0) {
            sDs = sDs.replaceAll("< D >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<d1>") > 0) {
            sDs = sDs.replaceAll("<d1>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d1 >") > 0) {
            sDs = sDs.replaceAll("< d1 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D1>") > 0) {
            sDs = sDs.replaceAll("<D1>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D1 >") > 0) {
            sDs = sDs.replaceAll("< D1 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<d2>") > 0) {
            sDs = sDs.replaceAll("<d2>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< d2 >") > 0) {
            sDs = sDs.replaceAll("< d2 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("<D2>") > 0) {
            sDs = sDs.replaceAll("<D2>", "to_date('1900-01-01','yyyy-MM-dd')");
        }
        if (sDs.indexOf("< D2 >") > 0) {
            sDs = sDs.replaceAll("< D2 >", "to_date('1900-01-01','yyyy-MM-dd')");
        }

        //add by leeyu 080729
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }

        if (sDs.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
            sDs = sDs.replaceAll("<Year>",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        } else if (sDs.indexOf("< Year >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Year >",
                                 YssFun.formatDate(new java.util.Date(), "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
            sDs = sDs.replaceAll("<Set>", "001");
        } else if (sDs.indexOf("< Set >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Set >", "001");
        }
        
        if (sDs.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
        
        //添加资产代码的处理 MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
        if(sDs.indexOf("<Asset>") != -1){
        	sDs = sDs.replaceAll("<Asset>", dbl.sqlString("001"));
        }
        //edit by licai 20110221 STORY #441 需优化现在的报表自定义模板	
        //增加了两个标记符号导致点击数据源字段配置报错,此处将标记写死,暂时未找到替代方案
        if(sDs.indexOf("<DynColumnCode>")>0){//组合代码等动态列条件
        	sDs=sDs.replaceAll("<DynColumnCode>", dbl.sqlString(""));
		}else if(sDs.indexOf("< DynColumnCode >")>0){
			sDs=sDs.replaceAll("< DynColumnCode >", dbl.sqlString(""));
		}
		if(sDs.indexOf("<DynRowCodes>")>0){//多券商代码等动态行条件
			sDs=sDs.replaceAll("<DynRowCodes>", "("+operSql.sqlCodes("")+")");
		}else if(sDs.indexOf("< DynRowCodes >")>0){
			sDs=sDs.replaceAll("< DynRowCodes >", "("+operSql.sqlCodes("")+")");
		}				
		if(sDs.indexOf("<RptStyle>")>0){//('报表格式代码','报表数据源')
			sDs=sDs.replaceAll("<RptStyle>", "("+operSql.sqlCodes("")+")");
		}else if(sDs.indexOf("< RptStyle >")>0){
			sDs=sDs.replaceAll("< RptStyle >", "("+operSql.sqlCodes("")+")");
		}		
		if(sDs.indexOf("<RptCode>")>0){//自定义报表代码
			sDs=sDs.replaceAll("<RptCode>",dbl.sqlString("") );
		}else if(sDs.indexOf("< RptCode >")>0){
			sDs=sDs.replaceAll("< RptCode >",dbl.sqlString("") );
		}
		//edit by licai 20110221 STORY #441=====================end        
        
        
        sDs = sDs.replaceAll("~Base", "0");
        return sDs;
    }
    
    
    /**
     * 解析公式的入口 MS00175 by leeyu
     * @param sSql String
     * @return String
     */
    private String parseSqlFuns(String sSql) {
        String sqls = "";
        sqls = WDay(sSql); //WDay
        return sqls;
    }
    
    /**
     * WDay公式部分 by leeyu 2009-01-07 MS00175
     * @param sSql String
     * @return String
     */
    private String WDay(String sSql) {
        String sFunCode = ""; //函数名
        String strReplace = ""; //要替代的字符串
        String strCalc = ""; //通过计算得到的字符串
        String sParams = ""; //相关参数字符串
        if (sSql.lastIndexOf("[") > 0 && sSql.lastIndexOf("]") > 0) {
            if (sSql.lastIndexOf("]") > sSql.lastIndexOf("[")) { //确保"]" 在"[" 的后面
                sParams = sSql.substring(sSql.lastIndexOf("[") + 1,
                                         sSql.lastIndexOf("]"));
                sFunCode = sSql.substring(sSql.lastIndexOf("[") - 4,
                                          sSql.lastIndexOf("["));
                if (sFunCode.equalsIgnoreCase("WDay")) {
                	//modify by huangqirong 2011-07-29 story #1173 这里的WDay 函数没有没被解析过来执行时报SQL语句错误(它执行sql语句获取字段报错)
                	//strReplace = "WDay" + "[\\[]" + sParams + "[\\]]";
                	strReplace = "WDay" + "\\[([^]]+)\\]";
                    //---end---
                    strCalc = dbl.sqlDate("2008-01-01"); //先给一个默认值
                }
            }
        }
        sSql = sSql.replaceAll(strReplace, strCalc);
        return sSql;
    }
    
    /**
     * 替换掉数据源中特定的转换字符 by leeyu
     * 如果Sql中包括 #[]# 这种字符，就替换掉
     * QDV4深圳2009年01月13日01_RA MS00192
     * 20090331
     * @param sDsSql String
     * @return String
     */
    private String replaceSplitStr(String sDsSql) {
        String sParam = ""; //保存#[]#内部的数据的
        String sStar = "", sEnd = ""; //保存#
        int iStarLen = 0, iEndLen = 0;
        if (sDsSql.lastIndexOf("]") > 0 && sDsSql.lastIndexOf("[") > 0) {
            iStarLen = sDsSql.lastIndexOf("[");
            iEndLen = sDsSql.lastIndexOf("]");
            sStar = sDsSql.substring(iStarLen - 1, iStarLen); //取最后一个的下一个字符
            if(iEndLen<sDsSql.length()-1)//add by huangqirong 2011-07-28 story #1173 影响 WDay函数在最后面引起超出索引错误
            	sEnd = sDsSql.substring(iEndLen + 1, iEndLen + 2); //取最后一个的下一个字符
            sParam = sDsSql.substring(iStarLen + 1, iEndLen);
            if (sStar.equalsIgnoreCase("#") && sEnd.equalsIgnoreCase("#")) {
                sParam = "#[\\[]" + sParam + "[\\]]#";
                sDsSql = sDsSql.replaceAll(sParam, "' '"); //将查出的数据替换成相应的SQL
            }
        }
        return sDsSql;
    }
    
    /**
     * 解析钆差函数
	 * MS00817:QDV4工银2009年11月17日01_A sunkey@Modify
     * @param sDs
     * @return
     */
    private String parseTOB(String sDs) {

		//先将特殊字符进行转换，主要是将[]转换成【】，将()替换（）,*换成#
		sDs = sDs.replaceAll("\\[", "【").replaceAll("\\]", "】").replaceAll("\\(", "（").replaceAll("\\)", "）").replaceAll("\\*", "#");
		
		// 此处的钆差函数仅用于字段处理，不需实际值隐藏可直接将函数擦掉
		while (sDs.indexOf("TOB【") != -1) {
			// 截取TOB[]中括号内的字段，因为可能存在嵌套的情况，因此要进行特别处理
			// 1.获取到TOB[后面的字符串
			String sTailDs = sDs.substring(sDs.indexOf("TOB【") + 4);
			// 2.根据[的个数判断从哪个]进行截断
			while (sTailDs.indexOf("【")!=-1 && sTailDs.indexOf("【") < sTailDs.indexOf("】")) {
				sTailDs = sTailDs.replaceFirst("【", "[").replaceFirst("】", "]");
			}
			// 3.获取函数
			String sFunPara = sTailDs.substring(0, sTailDs.indexOf("】"));
			sFunPara = sFunPara.replaceAll("\\[", "【").replaceAll("]", "】");

			// 4.使用函数进行替换
			sDs = sDs.replaceAll("TOB【" + sFunPara + "】", sFunPara.split(";")[1]);
		}
		
		//还原成原来的形式
		sDs = sDs.replaceAll("【", "[").replaceAll("】", "]").replaceAll("（", "(").replaceAll("）", ")").replaceAll("#", "*");
		return sDs;
	}
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public String getOperValue(String sType) throws YssException {
		 String strSql = "", sReturn = "", sError = "";
	        String sHeader = "", sShowDataStr = "";
	        ResultSet rs = null;
	        StringBuffer buf = new StringBuffer();
	        try {
	            if (sType.equalsIgnoreCase("getField")) {
	                sError = "获取预处理接口信息出错";
	                sHeader = "字段名称\t字段类型";
	                strSql = this.dataSource.trim();
	                strSql = buildSql(strSql);
	                rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
	                ResultSetMetaData rsmd = rs.getMetaData();
	                int FieldsCount = rsmd.getColumnCount(); //原始表字段数
	                int[] fDataType = new int[FieldsCount]; //记录数据字段数据类型
	                String[] fDataTypeName = new String[FieldsCount];
	                String[] fDataName = new String[FieldsCount]; //记录数据字段名称
	                for (int i = 0; i < FieldsCount; i++) {
	                    fDataName[i] = rsmd.getColumnName(i + 1);
	                    fDataTypeName[i] = rsmd.getColumnTypeName(i + 1);
	                    fDataType[i] = rsmd.getColumnType(i + 1);
	                    buf.append(fDataName[i]).append("\t");
	                    buf.append(fDataTypeName[i]).append(YssCons.YSS_LINESPLITMARK);
	                }
	               
	                if (buf.toString().length() > 2) {
	                    sShowDataStr = buf.toString().substring(0,
	                        buf.toString().length() - 2);
	                }
	                sReturn = sHeader + "\r\f" + sShowDataStr + "\r\f" + sShowDataStr;
	            }
	            /**
	             * 2008-5-28
	             * 单亮
	             * 在自定义接口浏览数据源时用到此方法
	             */
	            if (sType.equalsIgnoreCase("getPretreat")) {
	                String sqlStr = "";
	                String sAllDataStr = "";
	                String sVocStr = "";
	                StringBuffer bufShow = new StringBuffer();
	                StringBuffer bufAll = new StringBuffer();
	                //      try{
	                sHeader = getListView1Headers();
	                sqlStr =
	                    "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
	                    "d.FTableDesc as FTargetTabName,e.FVocName as FDsTypeCode from " +
	                    pub.yssGetTableName("Tb_Dao_Pretreat") + " a" +
	                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	                    " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
	                    " left join Tb_Fun_Vocabulary e on " +
	                    dbl.sqlToChar("a.FDsType") +
	                    " = e.FVocCode and e.FVocTypeCode = " +
	                    dbl.sqlString(YssCons.YSS_DAO_DSTYPE) + //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	                    "where a.FDPDsCode = " + dbl.sqlString(this.dPDsCode) + "" +
	                    " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
	                rs = dbl.openResultSet(sqlStr);
	                while (rs.next()) {
	                    bufShow.append(super.buildRowShowStr(rs,
	                        this.getListView1ShowCols())).
	                        append(YssCons.YSS_LINESPLITMARK);
	                    setVchDs(rs);
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
	                VocabularyBean vocabulary = new VocabularyBean();
	                vocabulary.setYssPub(pub);
	                sVocStr = vocabulary.getVoc(YssCons.YSS_DAO_DSTYPE + "," +
	                                            YssCons.YSS_DAO_PRETTYPE); //更改为接口数据源,与之前的报表数据源分开 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303

	                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
	                    "\r\f" +
	                    this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

	            }
	            return sReturn;

	        } catch (Exception e) {
	            throw new YssException(sError + "\n\n" + e.getMessage());
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
	}
    //拼接返回的字符串
	public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.dPDsCode).append("\t");
        buf.append(this.dPDsName).append("\t");
        buf.append(this.dsType).append("\t");
        buf.append(this.targetTabCode).append("\t");
        buf.append(this.targetTabName).append("\t");
        buf.append(this.beanId).append("\t");
        buf.append(this.dataSource).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.relaCompareCode).append("\t"); 
        buf.append(super.buildRecLog());
        return buf.toString();
    }
   //解析前台传进来的参数，并进行初始化
	 public void parseRowStr(String sRowStr) throws YssException {
	        String sTmpStr = "";
	        String[] reqAry = null;
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
	            
	            /**shashijie 2012-2-10 BUG 3824 */
	            if (sRowStr.indexOf("\r\n") > -1) {
	            	this.checkAccLinks = sRowStr;//批量处理数据
	            }
				/**end*/
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                sTmpStr = sRowStr.split("\r\t")[0];
	            } else {
	                sTmpStr = sRowStr;
	            }
	            reqAry = sTmpStr.split("\t");
	            this.dPreTreatRecycled = sRowStr;
	            this.dPDsCode = reqAry[0];
	            if (reqAry[0].length() == 0) {
	                this.dPDsCode = " ";
	            }
	            this.dPDsName = reqAry[1];
	            if (reqAry[2].length() > 0) {
	                this.dsType = Integer.parseInt(reqAry[2]);
	            }
	            this.targetTabCode = reqAry[3];
	            this.beanId = reqAry[4];
	            this.dataSource = reqAry[5];
	            this.desc = reqAry[6];
	            this.oldDPDsCode = reqAry[7];
	            this.checkStateId = YssFun.toInt(reqAry[8]);
	            if (reqAry[9].equalsIgnoreCase("true")) {
	                this.bIsShow = true;
	            } else {
	                this.bIsShow = false;
	            }
	            this.relaCompareCode = reqAry[10]; 
	            super.parseRecLog();
	            if (sRowStr.indexOf("\r\t") >= 0) {
	                if (this.filterType == null) {
	                    this.filterType = new RepPretreatBean();
	                    this.filterType.setYssPub(pub);
	                }
	                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
	            }
	        } catch (Exception e) {
	            throw new YssException("解析预处理接口信息出错", e);
	        }
	    }
	//把查询到的结果集赋值给对象的属性
	private void setVchDs(ResultSet rs) throws SQLException, YssException {
        this.dPDsCode = rs.getString("FDPDsCode");
        this.dPDsName = rs.getString("FDPDsName");
        this.beanId = rs.getString("FBeanID");
        this.targetTabCode = rs.getString("FTargetTab");
        this.targetTabName = rs.getString("FTargetTabName");

        this.dsType = rs.getInt("FDsType");

        this.desc = rs.getString("FDesc");
        this.relaCompareCode = rs.getString("FRelaCompCode") + ""; //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
        if (this.bIsShow) {
            this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource")).
                replaceAll("\t", "   ");
        } else {
            this.dataSource = "null";
        }
        super.setRecLog(rs);
    }
	
	public void checkInput(byte btOper) throws YssException {
		dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_Rep_Pretreat"),
                "FDPDsCode",
                this.dPDsCode,
                this.oldDPDsCode);

	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	//~ 显示
	//把前台传过来的查询条件处理成SQL语句的过滤条件
	private String builerFilter() {
        String sqlStr = "";
        if (this.filterType != null) {
            sqlStr = " where 1=1 ";
            if (filterType.dPDsCode != null && this.filterType.dPDsCode.trim().length() != 0) {
                if (filterType.dPDsCode.split(",").length > 0) { //如果前台传过来的是一组合预处理代码，则应采用 in来处理
                    sqlStr += " and a.FDPDsCode in (" +
                        operSql.sqlCodes(filterType.dPDsCode.replaceAll("'", "''")) +
                        ")";
                } else {
                    sqlStr += " and a.FDPDsCode like '" +
                        filterType.dPDsCode.replaceAll("'", "''") + "%'";
                }
            }
            if (filterType.dPDsName != null && filterType.dPDsName.length() != 0) {
                sqlStr += " and a.FDPDsName like '" +
                    filterType.dPDsName.replaceAll("'", "''") + "%'";
            }
            if (filterType.beanId != null && filterType.beanId.length() != 0) {
                sqlStr += " and a.FBeanID like '" +
                    filterType.beanId.replaceAll("'", "''") + "%'";
            }
            if (filterType.targetTabCode != null && filterType.targetTabCode.length() != 0) {
                sqlStr += " and a.FTargetTab like '" +
                    filterType.targetTabCode.replaceAll("'", "''") + "%'";
            }
            if (filterType.dsType != 99) {
                sqlStr += " and a.FDsType=" + filterType.dsType;
            }
            if (filterType.desc != null && filterType.desc.length() != 0 &&
                !filterType.desc.equalsIgnoreCase("null")) { 
                sqlStr += " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sqlStr;
    }
	//显示预处理分录
	public String getListViewData1() throws YssException {
		Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = getListView1Headers();//获取文件头
            conn = dbl.loadConnection();
            sqlStr =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                "d.FTableDesc as FTargetTabName,e.FVocName as FDsTypeCode from " +
                pub.yssGetTableName("Tb_Rep_Pretreat") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
                " left join Tb_Fun_Vocabulary e on " + dbl.sqlToChar("a.FDsType") +
                " = e.FVocCode and e.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_DAO_DSTYPE) + 
                builerFilter() +
                " order by a.FCheckState, a.FCheckTime desc, a.FCreateTime desc";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setVchDs(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_DAO_DSTYPE + "," +
                                        YssCons.YSS_DAO_PRETTYPE + "," +
                                        YssCons.YSS_FUNCTION_TYPE + "," + 
                                        YssCons.YSS_FUN_SHOWALL + "," +
                                        YssCons.YSS_FUN_CONTINUE + "," +
                                        YssCons.YSS_FUN_MESSAGE);
            //----------------------------------------end----------------------------------------------//
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\fvoc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //释放ResultSet 
            dbl.endTransFinal(conn, bTrans);
        }
	}

	public String getListViewData2() throws YssException {
		 Connection conn = null;
	        boolean bTrans = false;
	        ResultSet rs = null;
	        String sqlStr = "";
	        String sHeader = "";
	        String sShowDataStr = "";
	        String sAllDataStr = "";
	        StringBuffer bufShow = new StringBuffer();
	        StringBuffer bufAll = new StringBuffer();
	        try {
	            //sHeader="数据源代码\t数据源名称\t描述";
	            sHeader = "数据源代码\t数据源名称\t描述\t基准源"; //QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	            //sHeader = "数据源代码";
	            conn = dbl.loadConnection();
	            sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName from " +
	                pub.yssGetTableName("Tb_Rep_Pretreat") + " a" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
	                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
	                " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
	                //" where a.FCheckState =1 order by a.FDPDsCode "+
	                builerFilter() + //添加查询条件 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	                " and a.FCheckState=1 ";
	            rs = dbl.openResultSet(sqlStr);
	            while (rs.next()) {
	                bufShow.append(rs.getString("FDPDsCode")).append("\t");
	                bufShow.append(rs.getString("FDPDsName")).append("\t");
	                bufShow.append(rs.getString("FDesc")).append("\t");
	                bufShow.append("").append(YssCons.YSS_LINESPLITMARK); //添加的字段，这里取空 QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	                // bufShow.append(rs.getString("FDPDsCode")).append(YssCons.YSS_LINESPLITMARK);
	                setVchDs(rs);
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

	            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
	        } catch (Exception e) {
	            throw new YssException("获取预处理接口信息出错", e);
	        } finally {
	            dbl.closeResultSetFinal(rs); //关闭结果集  QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090303
	            dbl.endTransFinal(conn, bTrans);
	        }

	}

	public String getListViewData3() throws YssException {
		Connection conn = null;
        boolean bTrans = false;
        ResultSet rs = null;
        String sqlStr = "";
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
        	
            sHeader = "数据源代码\t数据源名称\t描述";
            // sHeader="数据源代码";
            conn = dbl.loadConnection();
            if (this.dPDsCode == null || this.dPDsCode.length() == 0) {
                sqlStr = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName from " +
                    pub.yssGetTableName("Tb_Rep_Pretreat") + " a" +
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
                }
            } else {
                String codes = operSql.sqlCodes(this.dPDsCode);
                String[] Arrcode = codes.split(","); //此次修改是用于处理数据按 dPCodes的顺序加载 by ly 080213
                sqlStr = " select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName from " +
                    pub.yssGetTableName("Tb_Rep_Pretreat") + " a" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                    " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode " +
                    " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT ) d on d.FTabName =a.FTargetTab " +
                    " where a.FCheckState =1 and FDPDsCode in (" + codes
                    + ") order by a.FDPDsCode ";
                rs = dbl.openResultSet_antReadonly(sqlStr);
                for (int i = 0; i < Arrcode.length; i++) {
                    while (rs.next()) {
                        if (Arrcode[i].equalsIgnoreCase(dbl.sqlString(rs.
                            getString("FDPDsCode")))) {
                            bufShow.append(rs.getString("FDPDsCode")).append(
                                "\t");
                            bufShow.append(rs.getString("FDPDsName")).append(
                                "\t");
                            bufShow.append(rs.getString("FDesc")).
                                append(YssCons.YSS_LINESPLITMARK);
                            // bufShow.append(rs.getString("FDPDsCode")).append(YssCons.YSS_LINESPLITMARK);
                            bufAll.append(rs.getString("FDPDsCode")).append(
                                "\t");
                            bufAll.append(rs.getString("FDPDsName")).append(
                                "\t");
                            bufAll.append(rs.getString("FDesc")).append(YssCons.
                                YSS_LINESPLITMARK);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
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
		String sShowDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer(); //用于显示的属性
        ResultSet rs = null;
        try {
            strSql = "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FTableDesc as FTargetTabName from " +
                pub.yssGetTableName("Tb_Rep_Pretreat") + " a" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select distinct FTabName,FTableDesc from TB_FUN_DATADICT )" +
                " d on d.FTabName = a.FTargetTab " +
                " where a.FDPDsCode = " + dbl.sqlString(this.oldDPDsCode) +
                " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.setVchDs(rs);
                bufShow.append(this.buildRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            return sShowDataStr; //返回单条数据源代码
        } catch (Exception e) {
            throw new YssException("获取预处理接口信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
	
	//~获取预处理设置详细数据
	public IDataSetting getSetting() throws YssException {
        //  IDataSetting s = new DaoPretreatBean();
        //  return s;

        String strSql = "";
        ResultSet rs = null;
        try {
        	//通过预处理代码和审核状态查询预处理详细设置
            strSql = " select * from " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                " where FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
                " and FCheckState=1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.dPDsCode = rs.getString("FDPDsCode");
                this.dPDsName = rs.getString("FDPDsName");
                this.dsType = rs.getInt("FDsType");
                this.targetTabCode = rs.getString("FTargetTab");
                this.beanId = rs.getString("FBeanId");
                this.dataSource = dbl.clobStrValue(rs.getClob("FDataSource"));
                this.relaCompareCode = rs.getString("FRelaCompCode"); 
            }
        } catch (Exception e) {
            throw new YssException("获取预处理数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return null;
    }

    public String getAllSetting() throws YssException {
        String s = "";
        return s;
    }
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	
	//~ 增、删、改、审核
	public String addSetting() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		boolean bTrans = false; //代表是否开始了事务
        Connection con = dbl.loadConnection();
        try{
        	//根据数据库类型选择相应
    		if(dbl.getDBType()== YssCons.DB_ORA){
    			strSql = "insert into " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                "(FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRelaCompCode)" + //添加核对关联预处理代码 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
                " values(" + dbl.sqlString(this.dPDsCode) + "," +
                dbl.sqlString(this.dPDsName) + "," +
                this.dsType + "," +
                dbl.sqlString(this.targetTabCode) + " ," +
                dbl.sqlString(this.beanId) + " ," +
                "EMPTY_CLOB()" + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                "," + dbl.sqlString(this.relaCompareCode) + ")"; //QDV4深圳2009年01月13日01_RA MS00192 by leeyu 20090402
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                " where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
            	  // modify by jsc 20120809 连接池对大对象的特殊处理
                //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
            	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                clob.putString(1, this.dataSource);
                strSql = "update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                    " set FDataSource = ? where FDPDsCode=" +
                    dbl.sqlString(this.dPDsCode);
                PreparedStatement pst = con.prepareStatement(strSql);
                pst.setClob(1, clob);
                pst.executeUpdate();
                pst.close();
            }
            dbl.closeResultSetFinal(rs);//释放结果集
    		}else if (dbl.getDBType() == YssCons.DB_DB2){
    			strSql = "insert into " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                "(FDPDsCode,FDPDsName,FDsType,FTargetTab,FBeanId,FDataSource," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FRelaCompCode)" + //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
                " values(" + dbl.sqlString(this.dPDsCode) + "," +
                dbl.sqlString(this.dPDsName) + "," +
                this.dsType + "," +
                dbl.sqlString(this.targetTabCode) + " ," +
                dbl.sqlString(this.beanId) + " ," +
                dbl.sqlString(this.dataSource) + "," +
                dbl.sqlString(this.desc) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.creatorCode)) +
                "," + dbl.sqlString(this.relaCompareCode) + ")"; //by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
    		}else{
    			throw new YssException("数据库访问错误！！数据库类型不明，或选择了非系统兼容的数据库！");
    		}
    		con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }catch(Exception ex){
        	throw new YssException("增加预处理设置信息出错", ex);
        }finally{
        	dbl.endTransFinal(con, bTrans);
        }
        return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void delSetting() throws YssException {
		Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            sqlStr = " update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Rep_PretreatField") +
                " where FDPDsCode=" + dbl.sqlString(this.oldDPDsCode);
            dbl.executeSql(sqlStr);
            sqlStr = " delete from " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
            dbl.executeSql(sqlStr); // liyu add 增加目标表删除条件处理
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除预处理设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	
	/**shashijie 2012-2-10 BUG 3824 清除数据*/
	public void deleteRecycleData() throws YssException {
		String strSql = "";
        String[] arrData = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (dPreTreatRecycled != null && dPreTreatRecycled.length() != 0) {
            	/**shashijie 2012-2-10 BUG 3824 原先竟然是\t\n,实在是让人无语 */
            	arrData = dPreTreatRecycled.split("\r\n");
				/**end*/
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Rep_Pretreat") +
                        " where FDPDsCode = " + dbl.sqlString(this.dPDsCode);
                    dbl.executeSql(strSql);
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Rep_PretreatField") +
                        " where FDPDsCode = " + dbl.sqlString(this.dPDsCode);
                    dbl.executeSql(strSql);
                    strSql = " delete from " +
                        pub.yssGetTableName("Tb_Rep_TgtTabCond") +
                        " where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
                    dbl.executeSql(strSql);
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}
	
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	
    public String editSetting() throws YssException {
    	Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            if (dbl.getDBType() == YssCons.DB_ORA) { 
                sqlStr = " update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                    "  set " +
                    " FDPDsCode =" + dbl.sqlString(this.dPDsCode) + "," +
                    " FDPDsName =" + dbl.sqlString(this.dPDsName) + "," +
                    " FDsType=" + this.dsType + "," +
                    " FTargetTab=" + dbl.sqlString(this.targetTabCode) + "," +
                    " FBeanId=" + dbl.sqlString(this.beanId) + "," +
                    " FDesc =" + dbl.sqlString(this.desc) + "," +
                    " FRelaCompCode = " + dbl.sqlString(this.relaCompareCode) + "," + //添加核对关联预处理代码字段
                    " FDataSource = EMPTY_CLOB()" +
                    " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
                sqlStr = "select FDataSource from " +
                    pub.yssGetTableName("Tb_Rep_Pretreat") +
                    " where FDPDsCode =" + dbl.sqlString(this.dPDsCode); //
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                	  // modify by jsc 20120809 连接池对大对象的特殊处理
                    //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FDataSource");
                	CLOB clob = dbl.CastToCLOB(rs.getClob("FDataSource"));
                    clob.putString(1, this.dataSource);
                    sqlStr = "update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                        " set FDataSource = ? where FDPDsCode=" +
                        dbl.sqlString(this.oldDPDsCode);
                    PreparedStatement pst = conn.prepareStatement(sqlStr);
                    pst.setClob(1, clob);
                    pst.executeUpdate();
                    pst.close();
                }
                dbl.closeResultSetFinal(rs);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                sqlStr = " update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
                    "  set " +
                    " FDPDsCode =" + dbl.sqlString(this.dPDsCode) + "," +
                    " FDPDsName =" + dbl.sqlString(this.dPDsName) + "," +
                    " FDsType=" + this.dsType + "," +
                    " FTargetTab=" + dbl.sqlString(this.targetTabCode) + "," +
                    " FBeanId=" + dbl.sqlString(this.beanId) + "," +
                    " FDesc =" + dbl.sqlString(this.desc) + "," +
                    " FRelaCompCode = " + dbl.sqlString(this.relaCompareCode) + "," + //添加核对关联预处理代码字段 by leeyu 090402 QDV4深圳2009年01月13日01_RA MS00192
                    " FDataSource = " + dbl.sqlString(this.dataSource) +
                    " where FDPDsCode =" + dbl.sqlString(this.oldDPDsCode);
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(sqlStr);
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            //修改预处理表里同时修改预处理字段表 add liyu 09118
            sqlStr = "update " + pub.yssGetTableName("tb_Rep_pretreatfield") +
                " set FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
                " where FDPDsCode=" + dbl.sqlString(this.oldDPDsCode);
            dbl.executeSql(sqlStr);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改预处理设置信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
	}

    /** shashijie 2012-2-10 BUG 3824 */
	public void checkSetting() throws YssException {
		if (!this.checkAccLinks.trim().equals("")) {//批量审核,反审核,删除 
			String[] sChkAccLinkAry = this.checkAccLinks.split("\r\n");
			for (int i = 0; i < sChkAccLinkAry.length; i++) {
				this.parseRowStr(sChkAccLinkAry[i]);
				checkDeleteDate();
			}
		} else {
			checkDeleteDate();//单个审核,反审核,删除 
		}
		
	}

	/**shashijie 2012-2-10 BUG 3824 审核,反审核,删除 */
	private void checkDeleteDate() throws YssException {
		Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            sqlStr = getUpdatePretreatSql();//修改预处理状态
            dbl.executeSql(sqlStr);
            sqlStr = getUpdatePretreatFieldSql();//预处理字段设置
            dbl.executeSql(sqlStr);
            sqlStr = getUpdateTgtTabCondSql();//目标表删除条件
            dbl.executeSql(sqlStr); // liyu add 增加目标表删除条件处理
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核凭证数据源出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	/**shashijie 2012-2-10 BUG 3824 目标表删除条件 */
	private String getUpdateTgtTabCondSql() {
		String sql = " update " + pub.yssGetTableName("Tb_Rep_TgtTabCond") +
	        " set FCheckState = " + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
	        "' where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
		return sql;
	}

	/**shashijie 2012-2-10 BUG 3824 预处理字段设置 */
	private String getUpdatePretreatFieldSql() {
		String sql = "update " + pub.yssGetTableName("Tb_Rep_PretreatField") +
	        " set FCheckState = " +
	        this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
	        "' where FDPDsCode = " +
	        dbl.sqlString(this.dPDsCode);
		return sql;
	}

	/**shashijie 2012-2-10 BUG 3824 预处理*/  
	private String getUpdatePretreatSql() {
		String Sql = " update " + pub.yssGetTableName("Tb_Rep_Pretreat") +
	        " set FCheckState =" + this.checkStateId +
	        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
	        ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
	        "'" +
	        " where FDPDsCode =" + dbl.sqlString(this.dPDsCode);
		return Sql;
	}

	public String getCheckAccLinks() {
		return checkAccLinks;
	}

	public void setCheckAccLinks(String checkAccLinks) {
		this.checkAccLinks = checkAccLinks;
	}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 分 割 线 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	
//	//执行数据预处理
//	public void doOnePretreat()throws YssException {
//		
//		//1. 预处理的目标表在这里创建
//		
//		//2. 根据数据源类型，进行数据预处理
//		if(this.dsType == 0){
//			//静态数据源   执行update操作
//			 this.exeDataSource();
//		}else if(this.dsType == 1){
//			//动态数据源   执行insert操作
//			delTgtTabData(); 	//step1 根据目标表删除条件删除目标表中的数据
//            insertTgtTabData(); //step2 根据预处理中的数据源插入到目标表中
//		}else if (this.dsType == 2){
//			//固定数据源  执行单独的一个javaBean  目前处理的是国内的数据接口
//			DataBase dtatBase = (DataBase) pub.getPretFunCtx().
//            getBean(pret.getBeanId()); //调用相应的  javaBean
//            dtatBase.setYssPub(pub);
//            dtatBase.setCheckState(check);
//            dtatBase.setCusCfgCode(this.cusCfgCode);
//            dtatBase.initDate(this.beginDate, "", this.portCodes);
//            dtatBase.inertData(); //调用 javaBean的插入方法
//		}else if (this.dsType == 3){
//			//参数数据源   从sql语句中的的到的数据作为参数
//			this.exeParamDataSource(pret);
//		}else if (this.dsType == 4){
//			//提示类型预处理数据源
//			sResult = promptPret(pret);
//		}
//	}
//
//	
//	/**
//     * 固定数据源
//     */
//    private void exeDataSource() throws YssException {
//        String strSql = "";
//        Connection conn = null;
//        boolean bTrans = true;
//        try {
//            conn = dbl.loadConnection();
//            conn.setAutoCommit(false);
//            strSql = this.buildDsSql(this.dataSource);
//            dbl.executeSql(strSql);
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true);
//        } catch (Exception e) {
//            throw new YssException("执行出错!");
//        } finally{
//        	dbl.endTransFinal(bTrans);
//        }
//    }
//    
//    
//    /*****************************************************
//     * 动态数据源：删除目标表数据
//     * 注意：如果没有设置删除条件，就不进行删除操作。
//     * @throws YssException
//     */
//    private void delTgtTabData() throws YssException {
//        String strSql = "";
//        ResultSet rs = null;
//        String delSql = ""; //删除目标表的sql语句
//        List dsTabSql = null; //原表
//        String targetTab = ""; //目标表
//        StringBuffer whereSqlBuf = new StringBuffer();
//        String whereSql = "";
//        Connection conn = null;
//        DataDictBean dictBean = null;
//        boolean bTrans = true;
//        int iTabType = 0;
//        try {
//        	//1. 获取目标表表名
//            dictBean = new DataDictBean(pub);
//            //获取表类型     
//            //0:系统表,1:临时表 -1:当前表不存在
//            iTabType = dictBean.getTabType(this.targetTabCode);
//            if (iTabType == 1) {
//                targetTab = this.targetTabCode;
//            } else if (iTabType == 0) {
//            	if(this.targetTabCode.toUpperCase().startsWith("A")){
//            		SetTabYearPre();
//            	}
//                targetTab = pub.yssGetTableName(this.targetTabCode);
//            } 
//            conn = dbl.loadConnection();
//            conn.setAutoCommit(false);
//            //2. 根据删除条件对目标表数据的删除。
//            dsTabSql = getDsSqlList(this.dataSource);
//            strSql = " select * from " +
//                pub.yssGetTableName("Tb_Rep_TgtTabCond") +
//                " where FDPDsCode=" + dbl.sqlString(this.dPDsCode) +
//                " and FCheckState=1" +
//                " order by FOrderIndex ";
//            rs = dbl.openResultSet(strSql);
//            while (rs.next()) {
//                whereSqlBuf.append("a.").append(rs.getString("FTargetField")).append("=")
//                           .append("b.").append(rs.getString("FDsField")).append(" and ");
//            }
//            if (whereSqlBuf.length() > 5) {
//                whereSql = whereSqlBuf.toString().substring(0,whereSqlBuf.toString().length() - 5);
//            }
//            if (whereSql.length() > 0) {
//				for (int i = 0; i < dsTabSql.size(); i++) {
//					delSql = " delete from " + targetTab + " a " + " where exists (select * from (" + dsTabSql.get(i).toString().split("\f\f")[1] + ") b " + " where " + whereSql + ")";
//					dbl.executeSql(delSql);
//				}
//                conn.commit();
//                bTrans = false;
//                conn.setAutoCommit(true);
//            }
//        } catch (Exception e) {
//            throw new YssException("删除目标表数据出错", e);
//        } finally {
//            dbl.closeResultSetFinal(rs);
//            dbl.endTransFinal(conn,bTrans);
//        }
//    }
//    
//    /**
//     * 动态数据源：根据预处理的数据源插入数据到目标表
//     * 
//     * @throws YssException
//     */
//    private void insertTgtTabData(DaoPretreatBean pret) throws
//        YssException {
//        Connection conn = dbl.loadConnection();
//        PreparedStatement pst = null;
//        String strSql = "";
//        ResultSet rsDs = null; //数据源的记录集
//        ResultSet rs = null; //该记录集用来打开接口预处理字段设置表
//        String strPretSql = "";
//        IOperValue operValue = null;
//        HashMap hmFieldType = null; //字段的字段名:字段的类型
//        int iPstOrder = 1; //pst的编号
//        Object sData = ""; //通过函数获取的数据处理
//        int tmpNum = 1; //暂时为了调试用的
//        String sDsFields = ""; //通过spring 的方式，去调用某个涵数时，
//        // 需要从数据源中取的一些字段做为参数用
//        String[] arrDsFields = null;
//        ArrayList alDsFieldValue = null; //通过spring调用的方式，把要传入的参数放入ArrayList中
//        DataDictBean dictBean = null;
//        boolean bTrans = true;
//        int iTabType = 0;
//        String sTabName = "";
//        
//        List sqlList = null;	//用于存储解析数据源获取到得sql组 sunkey@Modify 20091121
//        
//        try {
//            dictBean = new DataDictBean(pub);
//            iTabType = dictBean.getTabType(pret.getTargetTabCode());
//            if (iTabType == 0) {
//            	//--- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加  2009.12.24 --------------
//            	if(pret.getTargetTabCode().toUpperCase().startsWith("A")){
//            		//财务报表向后台传递年份参数
//            		SetTabYearPre();
//            	}
//            	//--- MS00878 QDV4赢时胜上海2009年12月21日01_AB end ---------------------------------
//                sTabName = pub.yssGetTableName(pret.getTargetTabCode());
//            } else if (iTabType == 1) {
//                sTabName = pret.getTargetTabCode();
//            } //判断表是临时表还是系统表 by liyu 080324
//            conn.setAutoCommit(false); // chenyibo  20071002
//            strSql = buildInsertTgtSql(pret); //生成插入到目标表的sql语句
//            pst = conn.prepareStatement(strSql);
//            //获取目标表的字段类型
//            hmFieldType = dbFun.getFieldsType(sTabName); // by liyu 080324
//
//            //根据预处理代码打开接口预处理字段设置，并按照顺序号 order by，
//            //用可滚动的游标打开
//            strPretSql =
//                " select * " +
//                " from " + pub.yssGetTableName("Tb_Dao_PretreatField") +
//                " where FDPDsCode=" + dbl.sqlString(pret.getDPDsCode()) +
//                " and FCheckState=1" +
//                " order by FOrderIndex ";
//            rs = dbl.openResultSet_antReadonly(strPretSql);
//
//			// 通过预处理中的数据源获得一个sql语句集合，如果数据源中不包含参数<PP>则一次性处理所有组合，否则将按组合逐个处理
//			sqlList = getDsSqlList(pret.getDataSource()); 
//			
//			// 循环处理每个组合群产生的数据源
//			for (int j = 0; j < sqlList.size(); j++) {
//				String [] portSql = sqlList.get(j).toString().split("\f\f");
//				portCode = portSql[0];
//				strSql = portSql[1];
//				rsDs = dbl.openResultSet(strSql); // 数据源的记录集
//				while (rsDs.next()) { // 循环数据源的记录集
//					tmpNum = tmpNum + 1; // 为了调试用
//					rs.beforeFirst();
//					iPstOrder = 1;
//					while (rs.next()) {
//						if (rs.getInt("FPretType") == 0) { // 数据源获取
//                    	Object objRes ="";
//                    	//这里增加功能：导入预处理 数据源获取时多个字段值往一个字段里插时，中间用逗号分隔 by leeyu 20091111
//                    	if(rs.getString("FDsField").indexOf(",")>1){
//                    		String[] arrField =rs.getString("FDsField").split(",");
//                    		for(int i=0;i<arrField.length;i++){
//                    			objRes = objRes+ (rsDs.getObject(arrField[i])+",");
//                    		}
//                    		if(String.valueOf(objRes).endsWith(",")){
//                    			objRes =String.valueOf(objRes).substring(0, String.valueOf(objRes).length()-1);
//                    		}
//                    	}else{
//                    		objRes =rsDs.getObject(rs.getString("FDsField"));
//                    	}
//                        //直接通过rsDs记录集和数据源字段取数,并做插入操作
////                        setPretPstValue(pst, iPstOrder,
////                                        hmFieldType,
////                                        rsDs.getObject(rs.getString("FDsField")),
////                                        rs.getString("FTargetField"));
//                    	setPretPstValue(pst, iPstOrder,
//                              hmFieldType,
//                              objRes,
//                              rs.getString("FTargetField"));
//                    	// by leeyu 20091111
//						} else if (rs.getInt("FPretType") == 1) { // 函数获取(通过spring的方式)
//							alDsFieldValue = new ArrayList();
//							SpringInvokeBean springInvoke = new SpringInvokeBean();
//							springInvoke.setYssPub(pub);
//							springInvoke.setSICode(rs.getString("FSICode")); // 设置Spring的调用代码
//							springInvoke.getSetting();
//							operValue = (IOperValue) pub.getDataInterfaceCtx().getBean(springInvoke.getBeanID()); // 通过beanId得到对象
//							
//							// 得到做为参数用的字段,用","分割 但是在配置的时候这里的参数要根据 具体涵数中的参数的顺序来进行配置 springInvoke.getParams()得到spring的参数如:calFee,getCashAcc等
//							sDsFields = rs.getString("FDsField"); 
//
//							arrDsFields = sDsFields.split(",");
//							for (int i = 0; i < arrDsFields.length; i++) {
//								alDsFieldValue.add(rsDs.getObject(arrDsFields[i]));
//							}
//							if (rs.getString("FTargetField").equalsIgnoreCase("FNum")) {
//								alDsFieldValue.add(this); // 20071114 chenyibo 处理交易编号的问题
//							}
//							alDsFieldValue.add(beginDate); 			// 将起始日期添加到list中,by liyu 080120
//							alDsFieldValue.add(endDate); 			// 将终止日期添加到list中,by liyu 080120
//							alDsFieldValue.add(portCode); 	// 将组合集添加到list中,by liyu 080120
//
//							// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
//							prepFunBean.setObj(alDsFieldValue);
//							operValue.init(prepFunBean);
//							operValue.setYssPub(pub);
//							sData = operValue.getTypeValue(springInvoke.getParams());
//							iPstOrder = setFunPstValue(pst, iPstOrder, hmFieldType, sData, rs.getString("FTargetField"));
//						}
//						iPstOrder++;
//					}
//					// 判断是否是a开头的表,另加上判断表是否为临时表,从数据字典里取的表类型 liyu 1218
//					if (!pret.getTargetTabCode().toLowerCase().startsWith("a") && iTabType == 0) {
//						setPstCommonValue(pst, iPstOrder);
//					}
//					pst.executeUpdate();
//				}
//			}
//            conn.commit();
//            bTrans = false;
//            conn.setAutoCommit(true); // chenyibo  20071002
//        } catch (Exception e) {
//            throw new YssException(e.getMessage());
//        } finally {
//            dbl.closeStatementFinal(pst);
//            dbl.closeResultSetFinal(rsDs,rs);
//            dbl.endTransFinal(conn,bTrans);
//        }
//    }
//
//    
//    
//    //--- MS00878 QDV4赢时胜上海2009年12月21日01_AB 蒋世超 添加  2009.12.24 --------------
//    private void SetTabYearPre()throws YssException{
//    	int beginYear ;
//    	int endYear ;
//    	beginYear = YssFun.getYear(super.beginDate);
//    	endYear = YssFun.getYear(super.endDate);
//    	if(beginYear == endYear){
//    		pub.setPrefixYear(beginYear);
//    	}else {
//    		throw new YssException("财务系统表不支持跨年份操作！");
//    	}
//    }
//    //--- MS00878 QDV4赢时胜上海2009年12月21日01_AB end  2009.12.24 -------------- 
}
