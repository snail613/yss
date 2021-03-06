package com.yss.main.operdeal.report.compliance;

import com.yss.main.operdeal.report.compliance.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.regex.Pattern;

import com.yss.util.YssFun;
import com.yss.util.YssException;
import com.yss.main.compliance.CompIndexCfgBean;
import com.yss.main.platform.pfsystem.facecfg.pojo.FaceCfgParamBean;
import com.yss.main.operdeal.report.compliance.pojo.CompResultBean;
import com.yss.util.YssOperCons;
import com.yss.util.YssCons;

public class DynamicCfgComp
    extends BaseCompliance {
    public DynamicCfgComp() {
    }

    /**
     * 执行客户数据源和使用分析脚本解析
     * @return ArrayList：装载 CompResultBean
     */
    public ArrayList doCompliance() throws YssException {
        HashMap hmLink = null;
        ResultSet rs = null;
        CompResultBean compResult = null;
        ArrayList liResult = new ArrayList();
        String  dataSource = "";//存放未解析的数据源add by zhaoxianlin 20130320
        try {
        	//筛选出符合生成条件的组合信息add  by zhouwei 20110926 req 1509
        	hmLink=this.getMapOfPortIndex();
          //  hmLink = this.getAllPortIndexLink();
        	//没有符合生成条件的组合信息
        	if(hmLink==null || hmLink.size()==0){
        		return liResult;
        	}
            //循环所选组合群

        	int iSerialNo = 0;		//20120209 added by liubo.Bug #3526.每次生成操作时每条生成数据的序列号，用来填充监控数据结果表的FSerialNo字段，无实际意义
        	int idays = YssFun.dateDiff(this.startDate, this.endDate);//add by zhaoxianlin 20130327 STORY #3786
            for (int iAsset = 0; iAsset < liEnableAsset.size(); iAsset++) {
                //获取组合群下的所选组合
                ArrayList liPortCodes = getSelectPortCodesByAssetGroupCode( (String) liEnableAsset.get(iAsset));
                //获取组合群下的所有监控指标
                ArrayList liCompIndex = (ArrayList) htLiIdxDynamic.get( (String) liEnableAsset.get(iAsset));
                //增加日期循环以支持跨日期生成监控结果
            	for(int j=0;j<=idays;j++){//add by zhaoxianlin 20130327 STORY #3786 
            		
                    this.compDate = YssFun.addDay(this.startDate, j);//add by zhaoxianlin 20130327 STORY #3786
                //根据所选组合进行循环
                for (int iPort = 0; iPort < liPortCodes.size(); iPort++) {
                    //循环所有监控指标

                	
                    for (int iIdx = 0; iIdx < liCompIndex.size(); iIdx++) {
                        compResult = new CompResultBean();
                        CompIndexCfgBean indexCfg = null;
                        indexCfg = (CompIndexCfgBean) liCompIndex.get(iIdx);
                        if (indexCfg.getIndexDS().length() == 0) {
                            continue;
                        }
                        //判断组合和指标是否有关联关系  修改  by zhouwei 20110926 req 1509
                        if(hmLink.get(( (String) liPortCodes.get(iPort)).trim() + "\t" +
                                indexCfg.getIndexCfgCode().trim())==null){
                        	continue;
                        }              
                        dataSource = indexCfg.getIndexDS();//add by zhaoxianlin 20130320
                        //替换数据源中的标签
                        this.doReplaceParams(indexCfg, (String) liEnableAsset.get(iAsset), (String) liPortCodes.get(iPort));
                        //解析数据源中的函数
                        ScriptExpress script = new ScriptExpress();
                        script.setYssPub(pub);
                        indexCfg.setIndexDS( (String) script.getFormulaValue(indexCfg.
                            getIndexDS()));
                        //执行数据源
                        String ssss = indexCfg.getIndexCfgCode();
                        rs = dbl.openResultSet(indexCfg.getIndexDS());
                        //判断储存方式
                        if (indexCfg.getMemoyWay().equalsIgnoreCase(YssOperCons.
                            YSS_JKZBPZ_MEMOYWAY_TABLE)) {
                            //表储存
                            this.storeInTable(indexCfg.getTgtTableView(),
                                              indexCfg.getIndexDS());
                        } else if (indexCfg.getMemoyWay().equalsIgnoreCase(YssOperCons.
                            YSS_JKZBPZ_MEMOYWAY_VIEW)) {
                            //视图储存
                            this.storeInView(indexCfg.getTgtTableView(),
                                             indexCfg.getIndexDS());
                        }

                        script = new ScriptExpress(rs, indexCfg);
                        script.setYssPub(pub);
                        //执行脚本解析，返回监控结果
                        //BugId MS00040 20081127   王晓光   用于判断结果集中是否存在FSys_Numerator、FSys_Denominator、FSys_FactRatio列
//                        if (rs.next()) {
//                            if (dbl.isFieldExist(rs, "FSys_Numerator")) {
//                                compResult.setNumerator(rs.getDouble("FSys_Numerator"));
//                            }
//                            if (dbl.isFieldExist(rs, "FSys_Denominator")) {
//                                compResult.setDenominator(rs.getDouble(
//                                    "FSys_Denominator"));
//                            }
//                            if (dbl.isFieldExist(rs, "FSys_FactRatio")) {
//                                compResult.setFactRatio(rs.getDouble("FSys_FactRatio"));
//                            }
//                        }
//
//                        compResult.setCompResult(script.calcFormulaString());
//                        //--------2009.02.10 蒋锦 修改 MS00195 QDV4建行2009年1月15日01_B---------//
//                        //使用监控的结束日期作为监控日期
//                        //在创建日期中插入系统当前日期
//                        //compResult.setCompDate(new java.util.Date());
//                        compResult.setCompDate(this.endDate);
//                        compResult.setCreateDate(new java.util.Date());
//                        //--------------------------------------------------------------------//
//                        compResult.setPortCode((String) liPortCodes.get(iPort));//edit by zhouwei req 1509 监控结果保存组合号码
//                        compResult.setIndexCfgCode(indexCfg.getIndexCfgCode());
//                        compResult.setIndexCfgName(indexCfg.getIndexCfgName());

                        //20120209 modified by liubo.Bug #3526
                        //根据监控指标生成数据时，有可能会生成出两条或以上的数据。原始的解决方法只能一个指标生成一条数据
                        //---------------------------------------
                        while(rs.next())
                        {
	                        iSerialNo ++;
                            compResult = new CompResultBean();
	                        if (dbl.isFieldExist(rs, "FSys_Numerator")) 
	                        {	
	                        	compResult.setNumerator(rs.getDouble("FSys_Numerator"));
			                }
			                if (dbl.isFieldExist(rs, "FSys_Denominator")) {
			                    compResult.setDenominator(rs.getDouble("FSys_Denominator"));
			                }
			                if (dbl.isFieldExist(rs, "FSys_FactRatio")) {
			                    compResult.setFactRatio(rs.getDouble("FSys_FactRatio"));
		                    }
			                /**add by zhaoxianlin 20130301 bug #7482---start*/
			                if (dbl.isFieldExist(rs, "FremindResult")) {
			                    compResult.setRemindResult(rs.getString("FremindResult"));
		                    }
			                /**add by zhaoxianlin 20130301 bug #7482---end*/
			                compResult.setCompResult(script.calcFormulaString());
	                        //--------2009.02.10 蒋锦 修改 MS00195 QDV4建行2009年1月15日01_B---------//
	                        //使用监控的结束日期作为监控日期
	                        //在创建日期中插入系统当前日期
	                        //compResult.setCompDate(new java.util.Date());
                              /** modified by zhaoxianlin 20130327 STORY #3786 -start*/
    	                       // compResult.setCompDate(this.endDate);
    			                compResult.setCompDate(this.compDate);
    			                /**end*/
	                        compResult.setCreateDate(new java.util.Date());
	                        //--------------------------------------------------------------------//
	                        compResult.setPortCode((String) liPortCodes.get(iPort));//edit by zhouwei req 1509 监控结果保存组合号码
	                        compResult.setIndexCfgCode(indexCfg.getIndexCfgCode());
	                        compResult.setIndexCfgName(indexCfg.getIndexCfgName());
	                        //序列号字段做为数据表主键的一部分，用来记录该次监控结果生成操作的序列号，无实际作用
	                        //==================================
	                        compResult.setSerialNo(String.valueOf(iSerialNo));
	                        
                        	liResult.add(compResult);

	                        //==============end====================
                        }

                        //----------------end-----------------------
                         indexCfg.setIndexDS(dataSource);//add by zhaoxianlin 20130320 
                          dbl.closeResultSetFinal(rs);//每次循环后关闭结果集，避免超出游标打开的最大数
                            
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("解析监控数据源出错！请检查监控指标配置数据源和界面控件设置，配置是否正确！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return liResult;
    }
    /*
     * add by zhouwei 20110926 需求1509
     * 自动生成监控结果时，获取非审核状态 0,自动生成 0 的监控记录对应的监控日期，组合代码，监控事项
     * 来重新生成监控结果
     * */
    private HashMap getMapOfPortIndex() throws YssException{
    	HashMap mapPort=new HashMap();//保存可以自动生成过的组合信息
    	HashMap mapNotPort=new HashMap();//保存可以不能自动生成的组合信息
    	//HashMap mapCheckPort=new HashMap();//保存审核过的，或不是自动生成过的监控结果对应的组合信息
    	ResultSet rs=null;
    	try{

			String[] ports=portCodes.split(",");
    		for(int i=0;i<liEnableAsset.size();i++){
    			String assetcode=(String) liEnableAsset.get(i);
    			String fport=null;
    			for(int j=0;j<ports.length;j++){
    				int beginIndex=ports[j].indexOf("-");
    				if(beginIndex!=-1){
    					if(ports[j].substring(0, beginIndex).equalsIgnoreCase(assetcode)){    						
    						if(fport==null){//获取组合号 如（'001001','001002'）
    							fport="('"+ports[j].substring(beginIndex+1);
    						}else{
    							fport=fport+"','"+ports[j].substring(beginIndex+1);
							}
    					}
    				}else{
    					if(fport==null){//获取组合号 如（'001001','001002'）
							fport="('"+ports[j];
						}else{
							fport=fport+"','"+ports[j];
						}
    				}
    			}
    			fport=fport+"')";   			
    			//查询不能自动生成的监控结果    回收站， 确认，非自动
    			String sql="select a.FCompDate,a.FIndexCfgCode,a.fportcode from "
        			+pub.yssGetTableName("tb_comp_resultdata",assetcode)+" a where ( a.fcheckstate=2 or a.frecheckstate=1 or a.Fstate<>0 ) "
        			+" and a.FCompDate="+dbl.sqlDate(endDate)+" and a.fportcode in "+fport;
    			rs=dbl.openResultSet(sql);
    			while(rs.next()){//键值是组合号，监控事项
    				String port=rs.getString("fportcode");
    				String key=port.trim()+"\t"+rs.getString("FIndexCfgCode").trim();
    				if(!mapNotPort.containsKey(key)){
    					mapNotPort.put(key, key);
    				}
    			}
   			 	dbl.closeResultSetFinal(rs);
    			//筛选能自动生成的组合信息
    			sql = "Select Distinct FPortCode, FIndexCfgCode FROM " +
                pub.yssGetTableName("Tb_Comp_Portindexlink",assetcode) +
                " WHERE FCheckState = 1 and FPortCode in "+fport;
    			rs = dbl.openResultSet(sql);
    			while(rs.next()){//键值是组合群，组合号，监控事项
    				String port=rs.getString("fportcode");
    				String key=port.trim()+"\t"+rs.getString("FIndexCfgCode").trim();
    				if(!mapNotPort.containsKey(key)){
    					mapPort.put(key, key);
    				}
    			}
    			 dbl.closeResultSetFinal(rs);
    			 
    		}
    		
    	}catch(Exception e){
    		throw new YssException("验证组合指标关联出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    	return mapPort;
    }
    /**
     * 获取所有组合和指标的链接记录
     * @throws YssException
     * @return boolean：返回存有所有关系的哈希表
     */
    public HashMap getAllPortIndexLink() throws YssException {
        HashMap hmRelation = new HashMap();
        ResultSet rs = null;
        String strSql = "";
        try {
//         if(!isOverAssetGroup){
//            strSql = "Select Distinct FPortCode, FIndexCfgCode FROM " +
//                  pub.yssGetTableName("Tb_Comp_Portindexlink") +
//                  " WHERE FCheckState = 1";
//            rs = dbl.openResultSet(strSql);
//
//            while (rs.next()) {
//               hmRelation.put(rs.getString("FPortCode") + "\t" +
//                              rs.getString("FIndexCfgCode"), "1");
//            }
//         }
//         else{
            for (int i = 0; i < liEnableAsset.size(); i++) {
                strSql = "Select Distinct FPortCode, FIndexCfgCode FROM " +
                    pub.yssGetTableName("Tb_Comp_Portindexlink",
                                        (String) liEnableAsset.get(i)) +
                    " WHERE FCheckState = 1";
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    hmRelation.put( (String) liEnableAsset.get(i) + "\t" +
                                   rs.getString("FPortCode") + "\t" +
                                   rs.getString("FIndexCfgCode"),
                                   "1");
                }
                dbl.closeResultSetFinal(rs);
            }
//         }
        } catch (Exception e) {
            throw new YssException("验证组合指标关联出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmRelation;
    }

    /**
     * 替换数据源中的自定义标签
     * @param indexCfg CompIndexCfgBean：监控指标
     * @param sAssetGroup String：组合群代码 2008-12-15 蒋锦 添加
     * @param sPortCode String：组合代码
     * @throws YssException
     */
    public void doReplaceParams(CompIndexCfgBean indexCfg, String sAssetGroup, String sPortCode) throws
        YssException {
        String strSql = "";
        String strCltValue = "";
        ResultSet rs = null;
        try {
            //替换数据源中的组合标示
            indexCfg.setIndexDS(indexCfg.getIndexDS().replaceAll("S<port>",
                dbl.sqlString(sPortCode)));
            //替换开始时间
            indexCfg.setIndexDS(indexCfg.getIndexDS().replaceAll("D<startdate>",
                dbl.sqlDate(this.startDate)));
            //替换结束时间
            indexCfg.setIndexDS(indexCfg.getIndexDS().replaceAll("D<enddate>",
                dbl.sqlDate(this.endDate)));
            //替换组合群标记 2008.06.16 蒋锦 添加
            indexCfg.setIndexDS(Pattern.compile("<group>",
                                                Pattern.CASE_INSENSITIVE).matcher(
                indexCfg.getIndexDS()).replaceAll(sAssetGroup));
            /** add by zhaoxianlin 20130327 STORY #3786 监控结果生成及查询功能优化-start*/
            //替换监控日期
              indexCfg.setIndexDS(indexCfg.getIndexDS().replaceAll("D<compDate>",
                  dbl.sqlDate(this.compDate)));
              /**end*/
            strSql = "SELECT a.FPortCode,a.FIndexCfgCode,a.FCtlCode,a.FCtlValue,b.FParamIndex,b.FCtlType,b.FParam,b.FCtlInd " +
                "FROM (SELECT FPortCode,FIndexCfgCode,FCtlGrpCode,FCtlCode,FCtlValue,FCheckState " +
                "FROM " + pub.yssGetTableName("Tb_Comp_PortIndexLink", sAssetGroup) +
                " WHERE FCheckState = 1) a " +
                "LEFT JOIN (SELECT FCtlGrpCode,FCtlCode,FParamIndex,FCtlType,FParam,FCtlInd,FFunModules,FCheckState " +
                "FROM Tb_PFSys_FaceCfgInfo " +
                "WHERE FCheckState = 1 " +
                "AND FFunModules = 'CompParam') b ON a.FCtlGrpCode = b.FCtlGrpCode " +
                "AND a.FCtlCode = b.FCtlCode " +
                "WHERE a.FPortCode = " + dbl.sqlString(sPortCode) +
                " AND a.FIndexCfgCode = " +
                dbl.sqlString(indexCfg.getIndexCfgCode()) +
                " ORDER BY a.FCtlGrpCode, a.FCtlCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //替换指标数据源
                indexCfg.setIndexDS(this.replaceMark(indexCfg.getIndexDS(), rs));
                //预警分析脚本
                indexCfg.setWarnAnalysis(this.replaceMark(indexCfg.getWarnAnalysis(),
                    rs));
                //违规分析脚本
                indexCfg.setViolateAnalysis(this.replaceMark(indexCfg.
                    getViolateAnalysis(), rs));
                //禁止分析脚本
                indexCfg.setForbidAnalysis(this.replaceMark(indexCfg.
                    getForbidAnalysis(), rs));
            }
        } catch (Exception e) {
            throw new YssException("客户数据源标识解析出错，请检查数据源语法和界面控件设置！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 具体的标签替换
     * @param str String：需要替换的字符串
     * @param rs ResultSet：查找参数控件组的结果集
     * @return String：修改后的字符串
     */
    public String replaceMark(String str, ResultSet rs) throws YssException {
        String sParamIndex = "";
        String sSqlValue = "";
        FaceCfgParamBean facdCfg = new FaceCfgParamBean();
        try {
            sParamIndex = String.valueOf(rs.getInt("FParamIndex"));
            String sMark = "<" + sParamIndex + ">";
            int iLoc = str.indexOf(sMark);
            if (iLoc < 0) {
                sMark = "< " + sParamIndex + " >";
            }
            iLoc = str.indexOf(sMark);
            //如果有类似D<1>,S<2>的标识
            if (iLoc >= 0) {
                String sDataType = str.substring(iLoc - 1,
                                                 iLoc);
                if (sDataType.equalsIgnoreCase("S")) {
                    sSqlValue = dbl.sqlString(dbl.clobStrValue(rs.getClob(
                        "FCtlValue")));
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = dbl.clobStrValue(rs.getClob("FCtlValue"));
                } else if (sDataType.equalsIgnoreCase("D")) {
                    //转换成日期
                    sSqlValue = dbl.sqlDate(YssFun.formatDate(dbl.clobStrValue(rs.
                        getClob("FCtlValue"))));
                } else if (sDataType.equalsIgnoreCase("N")) {
                    //2008.06.16 蒋锦 添加, 我们的选择控件是使用 | 号分割代码和名称
                    String sValue = dbl.clobStrValue(rs.getClob("FCtlValue"));
                    if (sValue.indexOf("|") > -1) {
                        sValue = sValue.split("[|]")[0];
                    }
                    //转换代码，例如 001,002转换成'001','002'
                    sSqlValue = operSql.sqlCodes(sValue);
                }
                str = str.replaceAll(sDataType + sMark, sSqlValue);
            }
            //如果控件类型为日期型控件
            if (rs.getInt("FCtlType") == 3) {
                if (rs.getString("FCtlInd") != null) {
                    //将控件值进行日期型的转换
                    str = str.replaceAll(rs.
                                         getString("FCtlInd"),
                                         dbl.sqlDate(dbl.clobStrValue(rs.getClob(
                                             "FCtlValue"))));
                }
            }
            //2008.07.18 蒋锦 修改，添加下拉框控件的替换
            //下拉框
            if (rs.getInt("FCtlType") == 2) {
                String sValue = dbl.clobStrValue(rs.getClob("FCtlValue"));
                if (sValue.indexOf(",") > -1) {
                    sValue = sValue.split(",")[0];
                }
                //取控件标示
                String sInd = rs.getString("FCtlInd");
                str = str.replaceAll(rs.getString("FCtlInd"), dbl.sqlString(sValue));
            }
            //选择控件
            else if (rs.getInt("FCtlType") == 4) {
                //2008.06.16 蒋锦 添加, 我们的选择控件是使用 | 号分割代码和名称
                //取出控件值
                String sValue = dbl.clobStrValue(rs.getClob("FCtlValue"));
                //如果有分割，分割代码和名称
                if (sValue.indexOf("|") > -1) {
                    sValue = sValue.split("[|]")[0];
                }
                //取控件标示
                String sInd = rs.getString("FCtlInd");
                if (sInd != null) {
                    //如果是 N 开头，使用 WHERE IN SQL 函数
                    if (sInd.substring(0, 1).equalsIgnoreCase("N")) {
                        str = str.replaceAll(rs.getString("FCtlInd"),
                                             operSql.sqlCodes(sValue));
                    } else {
                        str = str.replaceAll(rs.getString("FCtlInd"),
                                             dbl.sqlString(sValue));
                    }
                }
            } else {
                if (rs.getString("FParam") != null) {
	/**add---shashijie 2013-2-21 BUG 7108 之前传入的是组合关联表中的FCtlValue控件值
                	 * 应该传入控件配置表中的控件类型Fctltype*/
                	//解析控件参数，以判断数值型还是字符型
                	facdCfg.getCtlParams(rs.getString("FParam"),rs.getInt("FCtlType"));
                    /*facdCfg.getCtlParams(rs.getString("FParam"),
                                         new Double(dbl.clobStrValue(rs.getClob("FCtlValue"))).intValue());*/
                	/**end---shashijie 2013-2-21 BUG 7108*/
                }
                //如果不是数值型
                if (!facdCfg.isBIsNumeric()) {
                    if (rs.getString("FCtlInd") != null) {
                        //将控件值进行字符型转换
                        str = str.replaceAll(rs.
                                             getString("FCtlInd"),
                                             dbl.sqlString(dbl.clobStrValue(rs.
                            getClob("FCtlValue"))));
                    }
                } else {
                    if (rs.getString("FCtlInd") != null) {
                        str = str.replaceAll(rs.
                                             getString("FCtlInd"),
                                             dbl.clobStrValue(rs.getClob("FCtlValue")));
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return str;
    }

    /**
     * 将数据源执行后查询出的数据存入指定表中
     * @param sTableName String：目标表名
     * @param sSourceSql String：数据源 SQL 语句
     * @throws YssException
     */
    public void storeInTable(String sTableName, String sSourceSql) throws
        YssException {
        String strSql = "";
        String strInsert = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (dbl.yssTableExist(sTableName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("DROP TABLE " + sTableName));
                /**end*/
            }
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "CREATE TABLE " + sTableName + " AS " +
                    "(" + sSourceSql + ")";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } else {
                strSql = "Create Table " + sTableName + " AS " + "(" + sSourceSql +
                    ")" + " definition only";
                strInsert = "INSERT INTO " + sTableName + " (" + sSourceSql + ") ";
                conn.setAutoCommit(false);
                bTrans = true;
                dbl.executeSql(strSql);
                dbl.executeSql(strInsert);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new YssException("表储存出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 使用数据源 SQL 创建视图
     * @param sViewName String：视图名称
     * @param sSourceSql String：数据源 SQL
     * @throws YssException
     */
    public void storeInView(String sViewName, String sSourceSql) throws
        YssException {
        ResultSet rsTmp = null;
        String strSql = "";
        String sAllSelectFiled = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            if (dbl.yssViewExist(sViewName)) {
                dbl.executeSql("DROP VIEW " + sViewName);
            }
            //-----------------提取数据源中 Select 的字段------------------//
            rsTmp = dbl.openResultSet(sSourceSql,
                                      ResultSet.TYPE_SCROLL_INSENSITIVE);
            ResultSetMetaData rsmd = rsTmp.getMetaData();
            int iFiledCount = rsmd.getColumnCount();
            for (int i = 0; i < iFiledCount; i++) {
                sAllSelectFiled += rsmd.getColumnName(i + 1) + ",";
            }
            sAllSelectFiled = sAllSelectFiled.substring(0,
                sAllSelectFiled.length() - 1);
            dbl.closeResultSetFinal(rsTmp);
            //----------------------------------------------------------//
            if (dbl.dbType == YssCons.DB_ORA) {
                strSql = "CREATE VIEW " + sViewName + " (" + sAllSelectFiled + ") " +
                    " AS (" + sSourceSql + ")";
            } else {
                strSql = "CREATE VIEW " + sViewName + " (" + sAllSelectFiled + ") " +
                    " AS " + sSourceSql;
            }
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("视图储存出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rsTmp);
        }
    }
}
