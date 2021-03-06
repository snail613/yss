package com.yss.main.operdeal.datainterface;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yss.log.SingleLogOper;
import com.yss.main.dao.IDataSetting;
import com.yss.main.dao.IOperValue;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.datainterface.DaoFileContentBean;
import com.yss.main.datainterface.DaoPretreatBean;
import com.yss.main.datainterface.DaoPretreatFieldBean;
import com.yss.main.funsetting.SpringInvokeBean;
import com.yss.main.operdeal.datainterface.function.CtlFunction;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.syssetting.DataDictBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class ExpCusInterface
    extends BaseDaoOperDeal implements IDataSetting {//-------------modify by guojianhua 2010 09 15增加了IDataSetting接口和其方法------------

    private static String sCusCfgCode = ""; //单个的接口代码
    //11.20 lzp修改 用于动态得到导出的参数
    private  String sPorts = "";
    private  String sStartDate = "";
    private  String sEndDate = "";
    private  String fileStr = "";//add by yanghaiming 20100311 券商，交易席位，销售网点，交易类型对应的字段名称已“,”分割
    private  String eBroker = "";//券商
    private  String eNet = "";//销售网点
    private  String eTradeseat = "";//交易席位
    private  String eTradetype = "";//交易类型
    private  String sAllDateResule = "";//结果集
    private  String eFilterType = "";//查询的过滤条件
    private  String eFilterBorker = "";//券商的过滤条件
    private  String eFilterNet = "";
    private  String eFilterTradeseat = "";
    private  String eFilterTradetype = "";
	private SingleLogOper logOper;
	private int operType;
    String sRunDescFront = "";// add by qixufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 导出数据记录数
    
    /**add---shashijie 2013-2-28 STORY 3366 增加字段组合群代码,多个组合群代码用逗号分割*/
	private String AssetGroupCodesWhere = " ";
	/**end---shashijie 2013-2-28 STORY 3366*/
	
	/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
	private String FPortCodesWhere = " ";
	/**end---shashijie 2013-3-8 STORY 2869*/
	
    public void setAllData(String allData) {
        String[] str = allData.split("\t");
        sCusCfgCode = str[0];
        this.sPorts = str[1];
        this.sStartDate = str[2];
        this.sEndDate = str[3];
        /**add---shashijie 2013-3-1 STORY 3366 增加组合群判断条件字段*/
		if (str.length > 4) {
			this.AssetGroupCodesWhere = str[4];
		}
		/**end---shashijie 2013-3-1 STORY 3366*/
		
		/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
		if (str.length > 5) {
			this.FPortCodesWhere = str[5];
		}
    	/**end---shashijie 2013-3-8 STORY 2869*/
    }

    /**
     * 处理接口的基本方法，在其它方法中调用
     * @param cusCfg DaoCusConfigureBean
     * @return String
     * @throws YssException
     * MS00298 QDV4海富通2009年3月9日01_AB
     */
    public String doInterfaceBase(DaoCusConfigureBean cusCfg) throws YssException {
//      DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
//      cusCfg.setYssPub(pub);
//      cusCfg.setCusCfgCode(sCusCfgCode);
//      cusCfg.getSetting();
    	
        String sAllData = "";
        //1:处理临时表中的数据
        //2读最终的临时表数据到外部文件。
        this.doPretreat(cusCfg); //做预处理
        if (cusCfg.getFileType().equalsIgnoreCase("xml")) {
            //李钰添加,用于处理XML文件格式 1022
            ExpCusXMLInterface xmlExp = new ExpCusXMLInterface();
            xmlExp.setYssPub(pub);
            String fields = buildField(cusCfg);
            String sValue = doFileContent(cusCfg);
            String sStruct = getXmlStruct(cusCfg);
            fields = fields.replaceAll("\"", "");
            sValue = sValue.replaceAll(formatSplit(cusCfg.getSplitMark()), ","); //将分隔符转换成 逗号 modify h//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
            sAllData = xmlExp.builderXML(cusCfg, sValue, sStruct, fields);
        } else {
            sAllData = doSelectedValue(cusCfg); //获取最终临时表中的数据
        }
        return sAllData;
    }

    /***
     * 根据接口来获取数据
     * 返回：文件路径及名称\f\f文件内容\f\f文件类型
     */
    public String doInterface() throws YssException {
        String sAllData = "";
        //----MS00298 QDV4海富通2009年3月9日01_AB --------------------------------------------
        initParams(); // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
        cusCfg.setYssPub(pub);
        cusCfg.setCusCfgCode(sCusCfgCode);
        cusCfg.getSetting();
//        String sAllData = "";
        this.fileStr = getParam(cusCfg);
        String[] params = null;
        //edited by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
        this.doPretreat(cusCfg); 
        //end--- by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
        //edit by yanghaiming 20100311 MS00904 调用递归方法查询数据
        if(!this.fileStr.equalsIgnoreCase("")){
        	this.sAllDateResule = "";
        	params = this.fileStr.split("\r\t");
        	this.eFilterType = "";
        	this.eFilterType += " where 1=1";
        	this.eFilterBorker = "";
        	this.eFilterNet = "";
        	this.eFilterTradeseat = "";
        	this.eFilterTradetype = "";
        	getAllData(params,0);
        	sAllData = this.sAllDateResule;
        }else{
        	if (judgeMultiDirector(cusCfg)) { //当有日期设置 edit by yanghaiming 20100311
                int days = YssFun.dateDiff(this.beginDate, this.endDate); //日期天数
                for (int day = 0; day <= days; day++) { //循环日期
                    this.beginDate = YssFun.addDay(this.beginDate, day == 0 ? 0 : 1); //日期递增，第一天不变
                    this.sStartDate = YssFun.formatDate(this.beginDate, "yyyy-MM-dd"); //解析beginDate
                    try {
                    	runStatus.appendRunDesc("ManualOperRun", "    正在读取【" + sStartDate + "】数据... ...");
						sAllData += doInterfaceBase(cusCfg) + "~@~"; //将不同日期的数据以分割符分割
						runStatus.appendRunDesc("ManualOperRun", "    导出成功！\r\n");
					} catch (Exception e) {
						runStatus.appendRunDesc("ManualOperRun", "    [color:Blue]导出失败[/color]！\r\n");
					}
                }
        		// edit by qiuxufeng 20110131
//        		this.sStartDate = YssFun.formatDate(this.beginDate, "yyyy-MM-dd"); //解析beginDate
//        		sAllData = doInterfaceBase(cusCfg);
            } else { //没有日期段设置
                try {
                    runStatus.appendRunDesc("ManualOperRun", "    正在读取【" + sStartDate + "】数据... ...");
					sAllData = doInterfaceBase(cusCfg);
					runStatus.appendRunDesc("ManualOperRun", "    导出成功！\r\n");
				} catch (Exception e) {
					runStatus.appendRunDesc("ManualOperRun", "    [color:Blue]导出失败[/color]！\r\n");
					throw new YssException(e.getMessage());
				}
            }
        } 
        if (sAllData.length() > 3 && sAllData.indexOf("~@~") != -1) {
            sAllData = sAllData.substring(0, sAllData.length() - 3);
        }
        
         // ---增加日志记录功能----guojianhua add 20100915-------//
            operType=10;
            logOper = SingleLogOper.getInstance();
            this.setFunName("interfacedeal");
            this.setModuleName("interface");
            this.setRefName("000222");
			logOper.setIData(this,operType, pub);
        // ---------------------end--------------------//
        
        
        //-------------------------------------------------------------------------
//      DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
//      cusCfg.setYssPub(pub);
//      cusCfg.setCusCfgCode(sCusCfgCode);
//      cusCfg.getSetting();
//      String sAllData = "";
//      //1:处理临时表中的数据
//      //2读最终的临时表数据到外部文件。
//      this.doPretreat(cusCfg); //做预处理
//      if (cusCfg.getFileType().equalsIgnoreCase("xml")) {
//         //李钰添加,用于处理XML文件格式 1022
//         ExpCusXMLInterface xmlExp = new ExpCusXMLInterface();
//         xmlExp.setYssPub(pub);
//         String fields = buildField(cusCfg);
//         String sValue = doFileContent(cusCfg);
//         String sStruct = getXmlStruct(cusCfg);
//         fields = fields.replaceAll("\"", "");
//         sValue.replaceAll(formatSplit(cusCfg.getSplitMark()), ","); //将分隔符转换成 逗号
//         sAllData = xmlExp.builderXML(cusCfg, sValue, sStruct, fields);
//      }
//      else {
//         sAllData = doSelectedValue(cusCfg); //获取最终临时表中的数据
//      }
        return sAllData;
    }

    private String getXmlStruct(DaoCusConfigureBean cusCfg) throws YssException {
        //本方法根据XML的配置获取XML的结构源 add liyu 1022
        String[] sProp = null;
        DaoPretreatBean pre = null;
        String sRes = "";
        try {
            if (cusCfg.getDPCodes().trim().length() > 0) {
                sProp = cusCfg.getDPCodes().split(",");
                for (int i = 0; i < sProp.length; i++) {
                    pre = new DaoPretreatBean();
                    pre.setDPDsCode(sProp[i]);
                    pre.setYssPub(pub);
                    pre.getSetting();
                    if (pre.getTargetTabCode().equalsIgnoreCase("XMLExpType")) {
                        //此处为固定写法,这个标记是专用来区分与其他的预处理的.
                        sRes = pre.getDataSource();
                    }
                }
            }
            return sRes;
        } catch (Exception e) {
            throw new YssException("获取XML的结构数据出错", e);
        }
    }

    /***
     * 1预处理
     */
    private void doPretreat(DaoCusConfigureBean cusCfg) throws YssException {
    	//风控报表余额表必须先登帐，登帐操作在此处理 add by yeshenghong 20121120 story3241
        if(sCusCfgCode.equals("INFO_CW_YEB_SJZX")||sCusCfgCode.equals("RC_LBalance"))
        {
	        YssFinance yssFinance = new YssFinance();
	        yssFinance.setYssPub(pub);
	        yssFinance.PostAccB(this.beginDate, portCodes);
        }
        String sDpCode = cusCfg.getDPCodes(); //预处理代码
        String[] sPret = null;
        if(sDpCode==null||sDpCode.length()==0) return;//xuqiji 20100329 MS00940 赢时胜(测试)2010年3月25日5_B 配置参数通用导入界面导入调拨类型的文件时报错
        if (sDpCode.indexOf(",") > 0) {
            sPret = sDpCode.split(",");
        } else {
            sPret = new String[1];
            sPret[0] = sDpCode;
        }
        try {
            for (int i = 0; i < sPret.length; i++) {
                doOnePretreat(sPret[i], cusCfg.getCusCfgCode()); //判断有多少个预处理
            }
        } catch (Exception ex) {
            throw new YssException("当前接口获取预处理代码出错", ex);
        }
    }
    /**
	 * modify by wangzuochun 2009.09.27 MS00687 在接口处理里执行风控接口时，需考虑【通用业务参数设置-自动导数据处理】设置的日期参数 QDV4华夏2009年09月08日02_A 
	 */
    private String doSelectedValue(DaoCusConfigureBean cusCfg) throws
        YssException {
        //1文件名称 \f\f
        //2文件头   \f\f
        //3文件正文  \f\f
        //4文件结尾   \f\f
        //5文件类型    \f\f
    	//6合并文件名\f\f  shashijie 2011.03.28 STORY #557 希望优化追加数据的功能
        String sSQLValue = "";
        if(cusCfg.getTabName().equalsIgnoreCase("NULL")||cusCfg.getTabName().trim().length()==0){
            return "";
        }else{
            sSQLValue += doFilePath(cusCfg) + "\f\f";//文件路径处理
            sSQLValue += doFileInfo(cusCfg) + "\f\f";//文件头部处理
            sSQLValue += doFileContent(cusCfg) + "\f\f";//文件内容处理
            sSQLValue += doFileEnd(cusCfg) + "\f\f";//文件结尾处理
            sSQLValue += doFileType(cusCfg) + "\f\f";//文件类型处理
            /**shashijie 2011.03.28 STORY #557 希望优化追加数据的功能*/
            sSQLValue += doFileMergerName(cusCfg)+"\f\f";//合并文件名处理
            /***/
            sSQLValue += doIsSerialNumber(cusCfg);//add by guyichuan 2011.06.22 STORY #1119 合并后的文件中需要依次编号
        }
        return sSQLValue;
    }
   
   /**
    * @author guyichuan 
    * 2011.06.22 STORY #1119
    * @param  cusCfg
    * @return sResult 是否有序号,序号开始值，序号字段前的总长度
    * 返回是否带有"序号"标识，及序号的起始值
    */
	private String doIsSerialNumber(DaoCusConfigureBean cusCfg)
			throws YssException {
		// Connection conn = null;
		ResultSet rs = null;
		String sResult = "";
		String strDesc = "";
		try {
			/** add---shashijie 2013-3-1 STORY 3366 公共表查询 */
			//组合群SQL
        	String strSql = getSelectFileContentNoCopy(pub.yssGetTableName("Tb_Dao_FileContent"), "0", cusCfg.getCusCfgCode());
            
        	strSql += " Union All ";
            //公共表SQL
        	strSql += getSelectFileContentNoCopy("Tb_Dao_FileContent", "1", cusCfg.getCusCfgCode());
			/** end---shashijie 2013-3-1 STORY 3366 */

			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				strDesc = rs.getString("FDesc").trim().replaceAll(",", "");
				Pattern pattern = Pattern.compile("^序号\\s*\\[\\s*\\d+\\s*\\]$");
				Matcher matcher = pattern.matcher(strDesc);
				if (matcher.matches()) {
					matcher = Pattern.compile("\\[\\d+\\]").matcher(strDesc);
					if (matcher.find())
						sResult = "true" + "," + matcher.group().replaceAll("\\[|\\]", "").trim() + 
							"," + rs.getInt("SumLoadLen");
				}
			}
			rs.close();
			return sResult;
		} catch (Exception ex) {
			throw new YssException("获取合并文件序号标识出错", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2013-3-1 STORY 3366 */
	private String getSelectFileContentNoCopy(String tableName,
			String saveType, String cusCfgCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.FLoadLen, a.FDesc,a.fordernum, b.SumLoadLen from (select FLoadLen, FDesc, FOrderNum, FCusCfgCode" +
			" From " + tableName + " where FCusCfgCode = " + dbl.sqlString(cusCfgCode) + 
			" and FDesc like '%序号%') a left join (select sum(FLoadLen) as SumLoadLen, FCusCfgCode from " + 
			tableName + " where FOrderNum < (select FOrderNum from " + tableName +" where FCusCfgCode = " + 
			dbl.sqlString(cusCfgCode) + " and FDesc like '%序号%') group by FCusCfgCode) b " +
			" on a.FCusCfgCode = b.FCusCfgCode ";
		return sql;
	}

	/**
     * 判断是否需要生成多个目录及相关数据
     * @param cusCfg DaoCusConfigureBean
     * @return boolean
     * @throws YssException
     * MS00298 QDV4海富通2009年3月9日01_AB
     */
	public boolean judgeMultiDirector(DaoCusConfigureBean cusCfg) throws YssException {
        boolean needMuiltDirector = false;
        String reStr = "";
        ResultSet rs = null;
        if (cusCfg == null) {
            throw new YssException("系统未能获取自定义接口信息！");
        }
        try {
            reStr = "select * from " + pub.yssGetTableName("TB_Dao_FileName") +
                " where FCusCfgCode=" + dbl.sqlString(cusCfg.getCusCfgCode()) +
                " order by FOrderNum";
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
                if (rs.getString("FFileNameCls") != null) {
                    if (!rs.getString("FFileNameCls").equalsIgnoreCase("Date")) { //日期  edit by yanghaiming 20100311
                        continue; // 不为日期段时直接进行下个判断
                    } else {
                        needMuiltDirector = true; //返回true
                        break; //跳出循环
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("判断是否需要多个目录及数据时系统出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return needMuiltDirector;
    }

    //add by lidaolong 20110328 #3753::“文件夹”和“文件”日期根据文件名设置中的日期调整导出文件
    private String getFileDate(String cusCfgCode,String tableName,int ordernum) throws YssException{
		ResultSet rs = null;
		ResultSet rs1 = null;
		//String reStr = "";
		String strDate = sStartDate;
    	
    	try{
    		/**add---shashijie 2013-3-1 STORY 3366 获取公共表查询*/
    		//组合群SQL
    		String strSql = getSelectFileNameByFCusCfgCodeAndFordernum(pub.yssGetTableName(tableName), "0", cusCfgCode,ordernum);
    		
    		strSql += " Union All ";
    		
    		//公共表SQL
    		strSql += getSelectFileNameByFCusCfgCodeAndFordernum(tableName, "1", cusCfgCode,ordernum);
			/**end---shashijie 2013-3-1 STORY 3366*/
    		
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			if (rs.getInt("DELAYDAYS")==0){//当日期调整为0时，直接返回sStartDate
    				return this.sStartDate;	
    			}
    			//当有节假日时，按节假日增减日期，当没有节假日时，直接在原来的日期加减
    			if (rs.getString("holidaycode")==null || rs.getString("holidaycode").equals("")){
    			
    				return YssFun.formatDate(
    					    	    YssFun.addDay(YssFun.toDate(sStartDate),
    						                       rs.getInt("DELAYDAYS"))
    						  ,"yyyy-MM-dd") ;
    			}else{
    				
    				//根据节假日代码取出节假日
    				strSql="select FDate from Tb_Base_ChildHoliday where FHolidaysCode ="
    						+ dbl.sqlString(rs.getString("holidaycode")) + " and FCheckState=1 ";
    				
    				String holidaysDate="";
					rs1 = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
    				while (rs1.next()) {
    					holidaysDate += YssFun.formatDate(rs1.getDate("FDate"),"yyyy-MM-dd") + ",";
    				}
    				dbl.closeResultSetFinal(rs1);//关闭结果集
    				
    				int dDay = rs.getInt("DELAYDAYS");//调整的天数
    				
    				//根据调整的天数推出日期
    				Date date = YssFun.toDate(strDate);
    				while (dDay > 0) {
    					date = YssFun.addDay(date, 1);
    					strDate = YssFun.formatDate(date, "yyyy-MM-dd");
    					if (holidaysDate.indexOf(strDate) == -1) {
    						dDay--;
    					}
    					if (dDay ==0 ){
    						break;
    					}
    					
    				}
    				
    				//根据调整的天数推出日期
    				while (dDay < 0) {
    					date = YssFun.addDay(date, -1);
    					strDate = YssFun.formatDate(date, "yyyy-MM-dd");
    					if (holidaysDate.indexOf(strDate) == -1) {
    						dDay++;
    					}
    					if (dDay ==0 ){
    						break;
    					}   					
    				}
    				
    				
    			}
    		}
    		
    	}catch(Exception ex){
    		throw new YssException("获取文件名称及路径出错",ex);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		
    	}
    	return strDate;
    }
    
    /**shashijie 2013-3-1 STORY 3366 获取SQL*/
	private String getSelectFileNameByFCusCfgCodeAndFordernum(
			String tableName, String saveType, String cusCfgCode,
			int ordernum) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName +
			" a where a.FCheckState =1  and a.FFileNamecls='Date' and a.FCusCfgCode = " + dbl.sqlString(cusCfgCode) +
			" and a.fordernum = " + ordernum;
		return sql;
	}

	private String doFilePath(DaoCusConfigureBean cusCfg) throws YssException {
        //文件路径处理
        //Connection conn = null;
        String reStr = "";
        ResultSet rs = null;
        String sResult = "";
        //String sFormat = "yyyy-MM-dd"; //默认
        //String sFile=""; //字典处理后的文件名路径   add by wuweiqi 20101125  QDV4赢时胜深圳2010年10月29日01_A
        //String sFfilenamedict="";//字典类型   add by wuweiqi 20101125  QDV4赢时胜深圳2010年10月29日01_A
        try {
        	/**add---shashijie 2013-3-1 STORY 3366 增加公共表查询*/
        	reStr = " select * from ( ";
        	//组合群SQL
        	reStr += getSelectFileNameByFCusCfgCode(pub.yssGetTableName("TB_Dao_FileName"),"0",cusCfg.getCusCfgCode());
			
        	reStr += " Union All ";
            //公共表SQL
        	reStr += getSelectFileNameByFCusCfgCode("TB_Dao_FileName","1",cusCfg.getCusCfgCode());
        	reStr += " ) a order by a.FOrderNum";
			/**end---shashijie 2013-3-1 STORY 3366*/
      
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
            	
            	   // add by lidaolong 20110328 #842 需要在导出文件时，“文件夹”的日期和“文件”日期能根据文件名设置中的日期调整来生成文件
            	//获取到文件及文件名中的日期
            	String strDate =  getFileDate(cusCfg.getCusCfgCode(),"TB_Dao_FileName",rs.getInt("fordernum"));
            	//end by lidaolong 
            	
            	
            	//与合并文件名调用同一方法 shashijie 2011.03.28
            	sResult += getFileOrMergerName(rs,cusCfg,strDate);///eidt by lidaolong 20110328 #3753::“文件夹”和“文件”日期根据文件名设置中的日期调整导出文件
            	//end
            }
            rs.close();
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取文件名称及路径出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
    }
    
/**shashijie 2013-3-1 STORY */
	/**shashijie 2013-3-1 STORY 3366 */
	private String getSelectFileNameByFCusCfgCode(String tableName,
			String saveType, String cusCfgCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName +
        	" a where a.FCusCfgCode = " + dbl.sqlString(cusCfgCode);
		return sql;
	}
	

	/***
	 * add by wuweiqi 20101125  导出文件名字典处理    QDV4赢时胜深圳2010年10月29日01_A
	 * @param cusCfg
	 * @return 返回转化后的文件名
	 * @throws YssException
	 */
    private String doSinglePortFile(DaoCusConfigureBean cusCfg,String sFfilenamedict) throws YssException {
		// 文件名字典处理
		//Connection conn = null;
		String reStr = "";
		ResultSet rs = null;
		String sResult = "";
		try {
			/**add---shashijie 2013-3-1 STORY 3366 增加公共表查询*/
        	//组合群SQL
        	reStr = getSelectDictByFdictcode(pub.yssGetTableName("Tb_Dao_Dict"),"0",sFfilenamedict);
			
        	reStr += " Union All ";
            //公共表SQL
        	reStr += getSelectDictByFdictcode("Tb_Dao_Dict","1",sFfilenamedict);
			/**end---shashijie 2013-3-1 STORY 3366*/
			
			rs = dbl.openResultSet(reStr);
			while (rs.next()) {
				if (rs.getString("FsrcConent") != null) {
					sResult = rs.getString("FcnvConent");
				} else {
					sResult = "";
				}
			}
			rs.close();
			return sResult;
		} catch (Exception ex) {
			throw new YssException("文件名称进行字典处理出错", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
    }

    /**shashijie 2013-3-1 STORY 3366*/
	private String getSelectDictByFdictcode(String tableName,
			String saveType, String sFfilenamedict) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName
			+ " a where a.Fdictcode=" + dbl.sqlString(sFfilenamedict)
			+ " and a.FsrcConent=" + dbl.sqlString(this.portCodes);
		return sql;
	}

	private String doFileInfo(DaoCusConfigureBean cusCfg) throws YssException {
        //文件头部处理
        return "";
    }

    private synchronized String getConnectDict(String sDictCode, String sSrcCont) throws
        YssException {
        //根据转换代码与原字段将转换的数据取出,实现数据的转换
        String sResult = "";
        ResultSet rss = null;
        String sqlStr = "";
        try {
            sqlStr = "select FCnvConent from " +
                pub.yssGetTableName("tb_dao_dict") +
                " where FDictCode=" + dbl.sqlString(sDictCode) +
                " and FSrcConent=" + dbl.sqlString(sSrcCont);
            rss = dbl.openResultSet(sqlStr);
            while (rss.next()) {
                sResult = rss.getString("FCnvConent");
            }
            rss.close();
            return sResult;
        } catch (Exception e) {
            throw new YssException("文件内容转换出错!", e);
        }
        //--- MS00456 QDV4海富通2009年05月18日01_AB 关闭游标 sj -----
        finally {
            dbl.closeResultSetFinal(rss);
        }
        //--------------------------------------------------------
    }

    private String doFileContent(DaoCusConfigureBean cusCfg) throws YssException {
        //文件内容处理    获取内容，并完成相关的 内容 格式转换工作 1003
        DaoFileContentBean contBean = new DaoFileContentBean();
        contBean.setYssPub(pub);
        contBean.setCusCfgCode(cusCfg.getCusCfgCode());
        String fields = buildField(cusCfg); //由接口表获取数据列
        double fBeginRow = 0, fLoadIndex = 0, fLoadLen = 0; //起始行，读取位置，读取长度
        String sFormat = "", sFileContentDict = ""; //转换格式，内容字典
        String[] field = fields.split(",");
        String fieldType = "";
        String sql = "";
        StringBuffer sResult = new StringBuffer();
        String sResCont = ""; //最终内容
        ResultSet rs = null;
        HashMap hmFieldType = null;
        try {
            hmFieldType = dbFun.getFieldsType(cusCfg.getTabName());
            sql = "select " + fields + " from " + cusCfg.getTabName();//+buildOrderByStr(cusCfg.getCusCfgCode());//添加排序条件  by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
            //add by yanghaiming 20100311 MS00904  增加查询的where条件
            if(!this.eFilterType.equalsIgnoreCase("")&&this.eFilterType.length()>0){
            	sql += this.eFilterType;
            }
            if(!this.eFilterBorker.equalsIgnoreCase("")&&this.eFilterBorker.length()>0){
            	sql += this.eFilterBorker;
            }
            if(!this.eFilterNet.equalsIgnoreCase("")&&this.eFilterNet.length()>0){
            	sql += this.eFilterNet;
            }
            
            //20130121 added by liubo.Story #3337
            //当导出的接口为“Info_GZ_Asset（DBF格式的净值文件)”时，交易席位字段会被当做资产代码来存值
            //而在临时表中最后会有一条总计的记录，这条记录的资产代码为空。
            //要完整显示包括总计的这条数据在内的所有数据，就不能让席位的判断条件生效
            //=================================
            if (!cusCfg.getCusCfgCode().equalsIgnoreCase("Info_GZ_Asset"))
            //===================end==============
            {
	            if(!this.eFilterTradeseat.equalsIgnoreCase("")&&this.eFilterTradeseat.length()>0){
	            	sql += this.eFilterTradeseat;
	            }
            }
            if(!this.eFilterTradetype.equalsIgnoreCase("")&&this.eFilterTradetype.length()>0){
            	sql += this.eFilterTradetype;
            }
            //edit by yanghaiming 20100928 MS01816 接口导出时，如果进行排序报错
            sql += buildOrderByStr(cusCfg.getCusCfgCode());//添加排序条件  by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
            //edit by yanghaiming 20100928 MS01816 接口导出时，如果进行排序报错
            rs = dbl.openResultSet(sql);
            // add by qixufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加接口处理状态的显示
            int recordCount = 0;
            // add by qixufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加接口处理状态的显示
            while (rs.next()) {
                //       contBean.setTabField(field[index++]);
                for (int i = 0; i < field.length; i++) {
                    field[i] = field[i].replaceAll("\"", ""); //添加用于处理数据库中的关键字 add liyu 1026
                    contBean.setTabField(field[i]);
                    contBean.getSetting();
                    if (YssFun.isNumeric(contBean.getBeginRow())) { // 增加判断功能 防止为空  add 1016
                        fBeginRow = (Double.parseDouble(contBean.getBeginRow()));
                    }
                    if (YssFun.isNumeric(contBean.getLoadIndex())) {
                        fLoadIndex = (Double.parseDouble(contBean.getLoadIndex()));
                    }
                    if (YssFun.isNumeric(contBean.getLoadLen())) {
                        fLoadLen = (Double.parseDouble(contBean.getLoadLen()));
                    }
                    sFormat = contBean.getFormat();
                    if (sFormat == null) {
                        sFormat = "";
                    }
                    sFileContentDict = contBean.getFileContentDict(); //转换内容
                    if (sFileContentDict == null) {
                        sFileContentDict = "";
                    }
                    if (sFileContentDict != null &&
                        sFileContentDict.trim().length() > 0 &&
                        !sFileContentDict.equalsIgnoreCase("null")) {
                        sFileContentDict = getConnectDict(sFileContentDict,
                            rs.getString(field[i]));
                    } else {
                        sFileContentDict = rs.getString(field[i]);
                    }
                    fieldType = (String) hmFieldType.get(field[i].trim().toUpperCase());
                    //if(cusCfg.getSplitMark()==null ||cusCfg.getSplitMark().trim().length()==0) cusCfg.setSplitMark(","); //分隔符默认","
                    if (cusCfg.getSplitType().length() != 0 &&
                        cusCfg.getSplitType().equals("0")) { //固定分割 ,modify liyu 1016
                        if (fieldType.indexOf("VARCHAR") > -1) {
                            if (sFileContentDict.trim().length() == 0) {
                                sFileContentDict = rs.getString(field[i]);
                            }
                            sResult.append(formatString(sFileContentDict,
                                (int) fLoadLen)); //将字符串加右空格
                        } else if (fieldType.indexOf("NUMBER") > -1) {
                            sResult.append(YssFun.formatNumber(rs.getDouble(field[i]),
                                buildNumberStr(fLoadLen))); //将数字类型加 0
                        } else if (fieldType.indexOf("DATE") > -1) {
                            if (sFormat.trim().length() == 0) {
                                sFormat = "yyyy-MM-dd"; //默认格式
                            }
                            sResult.append(YssFun.formatDate(rs.getDate(field[i]),
                                sFormat)); //将日期转换成相对应的格式
                        }
                        sResult.append(cusCfg.getSplitMark());
                    } else { //符号分割
                        if (fieldType.indexOf("DATE") > -1) {
                            if (sFormat == null || sFormat.trim().length() == 0 ||
                                sFormat.equalsIgnoreCase("null")) {
                                sFormat = "yyyy-MM-dd"; //默认格式,liyu 修改
                            }
                            sResult.append(YssFun.formatDate(rs.getDate(field[i]).
                                toString(), sFormat)); //将日期转换成相对应的格式
                        } else { //数字与字符的转换格式有待扩展
                        	//add by jiangshichao 字符类型的转换
                        	if(fieldType.equalsIgnoreCase("VARCHAR2")&& sFormat.length()>0&&!sFormat.equalsIgnoreCase("null")){
	                            if(YssFun.isNumeric(sFileContentDict)){
                        			sResult.append(YssFun.formatNumber(YssFun.toNumber(sFileContentDict),sFormat));
                        		}else{
                        			sResult.append(sFileContentDict.trim());
                        		}
                        	}else{
                        	   sResult.append(sFileContentDict.trim()); //by liyu 将临时表中的空格去掉 1204
                        	}
                        }
                        sResult.append(formatSplit(cusCfg.getSplitMark()));
                    }
                }
                if (sResult.toString().length() > 0) { // 李钰添加,用于去掉每行的最后一个分隔符 1023
                    if (cusCfg.getSplitMark() != null &&
                        cusCfg.getSplitMark().length() > 0) { //若是位取就不删除
                        sResult.delete(sResult.length() - 1, sResult.length());
                    }
                }
                sResult.append("\r\n");
                recordCount++;
            }
            sResCont = sResult.toString();
            if (sResCont.length() > 2) {
                sResCont = sResCont.substring(0, sResCont.length() - 2);
            }
            rs.close();
            if (cusCfg.getFileType().equalsIgnoreCase("dbf") || cusCfg.getFileType().equalsIgnoreCase("dbf2.x")) { //若为DBF文件 STORY #2236
                sResCont = getFileConfig(cusCfg, fields) + "\r\n" + sResCont; //这里把取的字段也传进去,让外部建表时一致.by liyu 080429
            } else if (cusCfg.getFileType().equalsIgnoreCase("mdb")) { // 增加对Access类型的操作,by leeyu
                sResCont = getFileConfig(cusCfg, fields) + "\r\n" +
                    cusCfg.getTabName() + "\r\n" + sResCont;
            }
            /**shashijie 2012-11-2 BUG 6155 从接口导出数据时,文件内容要以回车键结尾*/
            else if (cusCfg.getFileType().equalsIgnoreCase("txt") || 
            		cusCfg.getFileType().equalsIgnoreCase("no")||
            		cusCfg.getFileType().equalsIgnoreCase("dat")) {//这里目前只支持txt和no文件类型
            	sResCont += formatEndMark(cusCfg.getEndMark());
			}
			/**end shashijie 2012-11-2 BUG */
            // add by qixufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加接口处理状态的显示
            if(recordCount == 0) {
            	runStatus.appendRunDesc("ManualOperRun", "    导出[color:Blue]共有" + recordCount + "条数据[/color]！");
            } else {
                runStatus.appendRunDesc("ManualOperRun", "    导出共有" + recordCount + "条数据！");
            }
            // add by qixufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加接口处理状态的显示
            System.out.println(fBeginRow+ " , "+ fLoadIndex + "起始行，读取位置~~暂时无用");
            return sResCont.replaceAll("null", ""); //文件内容,将null去掉，liyu 修改 1121
        } catch (Exception ex) {
            throw new YssException("获取文件内容出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
    }

    private String getFileConfig(DaoCusConfigureBean cusCfg, String sFields) throws
        YssException {
        //获取字段，字段类型，字段长度， 中间用\t分隔，字段与字段间用\f\f分隔
        Connection conn = null;
        ResultSet rs = null;
        //ResultSetMetaData meta = null;
        String sqlStr = "";
        String sType = "";
        String length = "";
        StringBuffer buf = new StringBuffer();
        HashMap hmResult = new HashMap();
        String[] arrField = sFields.split(","); //这里才用从外部取字段的方式,以免字段顺序不对 by liyu 080429
        try {
            conn = dbl.loadConnection();
            
            /**add---shashijie 2013-3-1 STORY 3366 查询公共表*/
            sqlStr = " select * from ( ";
            //组合群SQL
            sqlStr += getSelectFileContentLeftJoinDataDict(pub.yssGetTableName("Tb_Dao_FileContent"), "0", 
            		cusCfg.getCusCfgCode(),cusCfg.getTabName());
            
        	sqlStr += " Union All ";
            //公共表SQL
        	sqlStr += getSelectFileContentLeftJoinDataDict("Tb_Dao_FileContent", "0", 
        			cusCfg.getCusCfgCode(),cusCfg.getTabName());
        	sqlStr += " ) a order by a.fordernum ";
			/**end---shashijie 2013-3-1 STORY 3366*/
        	
            rs = dbl.openResultSet(sqlStr);
            hmResult = dbFun.getFieldsType(cusCfg.getTabName());
            int i = 0;
            while (rs.next()) {
                //sType=rs.getString("FTabFeild").replaceAll("\"","");
                sType = arrField[i++];
                sType = (String) hmResult.get(sType.toUpperCase().trim());
                /**shashijie 2011.03.29 STORY #814 招商证券每日需导出净值文件上传给中登,与我们系统导出的数字类型不一致*/
                if (rs.getString("FFieldType")!=null && !rs.getString("FFieldType").trim().equals("")) {
					sType = getFieldStype(rs,sType);
				}
                /**end*/
                length = rs.getString("FFieldPre");
                if (rs.getString("FFieldPre").trim().length() == 0) {
                    if (sType.indexOf("VARCHAR") > -1) {
                        length = "20";
                    } else if (sType.indexOf("DATE") > -1) {
                        length = "8";
                    } else if (sType.indexOf("NUMBER") > -1) {
                        length = "18,4";
                    }
                }
                buf.append(rs.getString("FTabFeild")).append("\t").append(sType).
                    append("\t")
                    .append(length).append("\b\b");
            }
            if (buf.toString().length() > 2) {
                buf.delete(buf.length() - 2, buf.length());
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("提取字段与字段配置信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
            dbl.endTransFinal(conn, false);
        }
    }

	/**shashijie 2013-3-1 STORY 3366 */
	private String getSelectFileContentLeftJoinDataDict(String tableName,
			String saveType, String cusCfgCode,String FTabName) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.FTabFeild,b.FFieldPre ,b.FFieldType,a.Fordernum from " + //多查出一个字段"字段类型" shashijie 2011.03.29
			tableName + "  a " +
	        " left join (select * from Tb_Fun_DataDict where FTabName=" +
	        dbl.sqlString(FTabName) + ") b on " +
	        //如果此字段设置为不导出，则不导出 　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	        " a.FTabFeild = b.FFieldName where a.FUnExport<>1 and a.FCusCfgCode=" +
	        dbl.sqlString(cusCfgCode);
		return sql;
	}

	private String buildNumberStr(double d) throws YssException { //双精度型的格式化 0
        String sRes = "";
        int i1 = 0, i2 = 0;
        i1 = (int) d; //获取整数部分
        i2 = (int) ( (d - i1) * 10); //取一位小数部分
        for (int j1 = 0; j1 < i1; j1++) {
            sRes = "0" + sRes; //左加0
        }
        if (i2 > 0) {
            sRes += "."; //若有小数位为小数点
        }
        for (int j2 = 0; j2 < i2; j2++) {
            sRes += "0"; //右加0
        }
        return sRes;
    }

    private String formatString(String str, double length) { //处理汉字的问题 by liyu 1227
        int iChina = 0; //汉字的长度
        if ( ( (int) length) == -1) {
            return str; //若为-1 返回这个字符串
        }
        byte[] bStr = str.getBytes();
        for (int i = 0; i < bStr.length; i++) {
            if (bStr[i] < 0) {
                iChina++;
            }
        }
        if (bStr.length > length) {
            str = new String(bStr, 0, (int) length);
        } else {
            for (int i = str.length() + iChina / 2; i < (int) length; i++) {
                str += " "; //右加空格
            }
        }
     
        return str;
    }

    private String doFileEnd(DaoCusConfigureBean cusCfg) throws YssException {
        //文件结尾处理
        return "";
    }

    private String doFileType(DaoCusConfigureBean cusCfg) throws YssException {
        //文件类型处理
        String sql = "";
        String sResult = "";
        ResultSet rs = null;
        try {
            sql = " select FVocName from Tb_Fun_Vocabulary  " +
                " where  FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FILETYPE) +
                " and FVocCode=" + dbl.sqlString(cusCfg.getFileType());
            rs = dbl.openResultSet(sql);
            while (rs.next()) {
                sResult += rs.getString("FVocName");
            }
            rs.close();
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取文件类型出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
    }

    private String formatSplit(String split) { //将分隔符的字母表示转换成对应的分隔符号
        if (split.toLowerCase().indexOf("tab") > -1) {
            split = "\t";
        } else if (split.toLowerCase().indexOf("space") > -1) {
            split = " ";
        } else if (split.toLowerCase().indexOf("enter") > -1) {
            split = "\r\n";
        } else if (split.indexOf(",") > -1) {
            split = ",";
        }
        //add by zhangfa 20101011 MS01768    自定义接口配置采用"|"作为分割符存在问题    QDV4华夏2010年09月20日01_B    
        else if(split.indexOf("|") > -1){
        	 split = "|";
        }
        //--------------------------------------------------------------------------------------------------
        //add by zhangfa 20100920 MS01743    自定义接口配置包含分隔符~时，接口导出报错    QDV4华夏2010年09月14日02_B  
        else if(split.indexOf("~") > -1){
        	split = "~";
        }
        //----------------------------------------------------------------------------------------------------
        else {
            split = "\t"; //默认的分隔符
        }
        return split;
    }

    /**shashijie 2012-11-2 BUG 6155 从接口导出数据时,文件内容要以回车键结尾  */
    private String formatEndMark(String split) throws YssException {
    	if (split==null) {
			return "";
		}
    	//tab键
        split = split.replace("tab", "\t");
        //空格
        split = split.replace("space"," ");
        //回车
        split = split.replace("enter","\r\n");
        
        return split;
	}
    
   /**
	 * modify by wangzuochun 2009.09.27 MS00687 在接口处理里执行风控接口时，需考虑【通用业务参数设置-自动导数据处理】设置的日期参数 QDV4华夏2009年09月08日02_A 
	 */
    private void doOnePretreat(String pretreat, String cusCfgCode) throws
        YssException {
        DaoPretreatBean pret = new DaoPretreatBean();
        try{
            pret.setYssPub(pub);
            pret.setDPDsCode(pretreat);
            pret.getSetting();
            //----当预处理中的目标表不存在，则在此创建 sj modify 20081124 bug MS00037 -------------------------------------
            if ( (pret.getTargetTabCode() != null &&
                  pret.getTargetTabCode().trim().length() > 0) &&
                !dbl.yssTableExist(pret.getTargetTabCode())) { // 如果临时表不存在，就根据数据字典中的设置，动态建临时表
                DataDictBean dataDict = new DataDictBean();
                dataDict.setYssPub(pub);
                dataDict.getTableInfo(pret.getTargetTabCode().trim());
                //-----为了获取此表的类型 -------//
                DataDictBean subDict = new DataDictBean();
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab(pret.getTargetTabCode().trim());
                }
            }
            //--------------------------------------------------------------------------------------------------------

            if (pret.getDPDsCode().equalsIgnoreCase("xmlMT541_Exp")) {
                return; //李钰添加,用于处理XML的,此处固定
            }
            //xuqiji 20090709：QDV4中保2009年06月09日01_A  MS00497 中保接口需求-净值信息表
            //通过javaBean导出数据
            if (pret.getDsType() == 2) { //数据源类型为固定数据源
                DataBase dtatBase = (DataBase) pub.getPretFunCtx().
                    getBean(pret.getBeanId()); //调用相应的  javaBean
                dtatBase.setYssPub(pub);
                dtatBase.setCusCfgCode(cusCfgCode); //用于设置自定义接口配置代码
                dtatBase.initDate(this.beginDate, "", this.portCodes); //初始化
                dtatBase.inertData(); //调用 javaBean的插入方法
            } else if (pret.getDsType() == 0) { //数据源类型为静态数据源 by wzc 20090927
                exeDataSource(pret);
            //by zhouwei 20120110 提示数据源不做处理
            }else if(pret.getDsType() == 4){
            	return;
            } else {
                saveToTMP(pret, cusCfgCode, pret.getDPDsCode()); //将数据读并存到临时表
            }
        }catch(Exception ex){
            throw new YssException(ex);
        }
        //--------------------------end-------------------------//
    }
    /**
     * 添加执行SQL的方法 
	 * modify by wangzuochun 2009.09.27 MS00687 在接口处理里执行风控接口时，需考虑【通用业务参数设置-自动导数据处理】设置的日期参数 QDV4华夏2009年09月08日02_A 
     * @param pret DaoPretreatBean
     * @throws YssException
     */
    private void exeDataSource(DaoPretreatBean pret) throws YssException {
            String strSql = "";
            Connection conn = null;
            boolean bTrans = false;
            try {
                conn = dbl.loadConnection();
                conn.setAutoCommit(false);
                bTrans = true;
                strSql = this.buildStr(pret.getDataSource());
                dbl.executeSql(strSql);
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } catch (Exception e) {
                throw new YssException("执行出错!");
            }finally{
                dbl.endTransFinal(conn,bTrans);
            }
    }
    private void saveToTMP(DaoPretreatBean pretreat, String cusCfgCode,
                           String dpdsCode) throws YssException {
        //select
        //insert
        //  Connection conn=null;,
        //    Statement st=null;
        DaoPretreatFieldBean field = new DaoPretreatFieldBean();
        field.setYssPub(pub);
        field.setDPDsCode(dpdsCode);
        HashMap hmFieldType = null;
        String sSQLParams = ""; //values(?) 代表 ?
        String sDesNames = null; //目标表组
        //  String sSourceNames=""; //源表组
        String[] sFieldValue = null; //单行内容组
        //   String reSql="";
        String sTabName = pretreat.getTargetTabCode(); //目标表  也就是临时表
        try {
        	 //-------------xuqiji 20100329 MS00940 赢时胜(测试)2010年3月25日5_B----------//
            if(dbl.yssTableExist(pretreat.getTargetTabCode())){
            	/**shashijie ,2011-10-12 , STORY 1698*/
            	dbl.executeSql(dbl.doOperSqlDrop(" drop table " + pretreat.getTargetTabCode()));
            	/**end*/
            }
            if (!dbl.yssTableExist(pretreat.getTargetTabCode())) {
                CreateTmp(pretreat.getTargetTabCode());
            }
            //----------------------end 20100329-------------------------//
            //  conn=dbl.loadConnection();
            delData(pretreat); //删除对应的表内容
            sDesNames = buildField(sTabName); //获取字段集 目标表
            sDesNames = sDesNames.replaceAll("\"", ""); // add liyu 1026 处理数据库中的关键字
            //   sSourceNames=builderFName(pretreat.getDataSource());//获取数据源表的字段
            //  fields=sFieldName.split(",");
            hmFieldType = dbFun.getFieldsType(sTabName); //获取对应表的字段类型
            sFieldValue = getPreateData(pretreat.getDataSource(), sDesNames,
                                        relateField(pretreat, hmFieldType)).split(
                                            "\r\n"); //将数据源中的SQL语句获取，并根据SQL语句来获取数据据
            for (int i = 0; i < sFieldValue.length; i++) {
                // field.setTargetField(fields[i]);
                // field.getSetting();
                sSQLParams = insertData(hmFieldType, sFieldValue[i], sDesNames,
                                        field);
                //  将数据写入到临时表。
                insertData(sTabName, DBFieldFormat(sDesNames), sSQLParams); // 添加处理数据库关键字做为字段的问题 liyu 修改 1026
            }
        } catch (Exception e) {
            throw new YssException(e.toString());
        }
    }

    /*//无用注释
     * private String builderFName(String SQL) throws YssException {
        ResultSetMetaData data = null;
        ResultSet rs = null;
        StringBuffer sResult = new StringBuffer();
        try {
            rs = dbl.openResultSet(SQL);
            data = rs.getMetaData();
            for (int i = 1; i <= data.getColumnCount(); i++) {
                sResult.append(data.getColumnName(i)).append(",");
            }
            rs.close();
            if (sResult.toString().length() > 0) {
                sResult.delete(sResult.length() - 1, sResult.length());
            }
            return sResult.toString();
        } catch (Exception ex) {
            throw new YssException("请检查SQL语句是否正确", ex);
        }
        //--- MS00456 QDV4海富通2009年05月18日01_AB 关闭游标 sj --
        finally {
            dbl.closeResultSetFinal(rs);
        }
        //-----------------------------------------------------
    }*/

    private void insertData(String sTabName, String sFieldName, String params) throws
        YssException { //执行 insert SQL语句
        // sTabName 为完整的临时表名，如Tmp_para_index Tmp_Fun_dictdata
        //sFieldName 为相关的列名
        //params 为相关的values值。
        Connection conn = dbl.loadConnection();
        String strSQL = "";
        try {
            if (params == null || params.length() == 0) {
                return; // 增加对 params的判断，防止为空
            }
            strSQL = "insert into " + sTabName + "(" + sFieldName + ") values(" +
                params + ")";
            dbl.executeSql(strSQL);
        } catch (Exception ex) {
            throw new YssException("往临时表中添加数据出错", ex);
        } finally {
            dbl.endTransFinal(conn, false);
        }
    }

    /* //无用注释
     * private String callFunction(DaoPretreatFieldBean pretreatField,
                                String DpdsCode, String sField,
                                HashMap hmFieldType) throws YssException {
        //pretreatField 为 bean; DpdsCode  数据源代码 ;sField 相关字段;    hmFieldType 为全部字段的类型
        return "";
        //根据预处理字段中的函数调用方法来处理数据
    }*/

    private String callSpring(String springCode, String Params) throws
        YssException {
        //根据预处理字段中的Spring代码处理 Spring信息
        //springCode 为 springCode Params为当前源参数     将处理的结果全部用 string的方式传回
        SpringInvokeBean springInvoke = new SpringInvokeBean();
        springInvoke.setYssPub(pub);
        IOperValue oper = null;
        String sResult = ""; //结果
        ArrayList list = new ArrayList(); //这里修改成跟导入的一样,便于公共函数的使用 by ly 080317
        String[] arrParam = null;
        try {
            springInvoke.setSICode(springCode);
            springInvoke.getSetting();
            oper = (IOperValue) pub.getDataInterfaceCtx().getBean(springInvoke.
                getBeanID());
            oper.setYssPub(pub);
            arrParam = Params.split(",");
            for (int i = 0; i < arrParam.length; i++) {
                list.add(i, arrParam[i]);
            }
            list.add(sStartDate); //这里也加上起始日期,终止日期与组合群集
            list.add(sEndDate);
            list.add(sPorts);
            //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
//         oper.init(list);
            prepFunBean.setObj(list);
            oper.init(prepFunBean);
            //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
            sResult = (String) oper.getTypeValue(springInvoke.getParams());
            return sResult;
        } catch (Exception e) {
            throw new YssException("预处理JavaBean调用出错", e);
        }
    }

    //传入三个参数，FieldsNames为字段名组，FieldsValues为单条内容组,此时还是将数据存到临时表
    private String insertData(HashMap hmFieldType, String FieldsValues,
                              String FieldsNames, DaoPretreatFieldBean field) throws
        YssException { //FieldsValues是一组 xxx,yy,zz...
        //写 ?,?,? 目前 'abc',2,to_date('2007-12-12','yyyy-dd-MM') 共三种类型<字符串，数值，日期>
        //这里有两步操作
        //2:将数据与类型匹配
        //1:根据预处理的配置来获取新的数据
        StringBuffer buf = new StringBuffer();
        String[] sFieldValue = FieldsValues.split("\t"); //默认为tab，因为此时还是存入到临时表中还不用考虑分隔符
        String[] sFieldName = FieldsNames.split(","); //默认为,这是个固定设置
        String sType = ""; //字段类型
        String sTmpFValue = ""; //暂存目标字段的内容,
        String sResult = "";
        try {
            for (int i = 0; i < sFieldName.length; i++) { //遍历字段组,此时字段组与内容组数量一致。
                if (sFieldValue[i] == null || sFieldValue[i].length() == 0) {
                    return ""; //增加 如果由SQL语句查得系统表无值时，则不将值插入到临时表 add liyu 1120
                }
                field.setTargetField(sFieldName[i]);
                field.getSetting();
                if (field.getPretType().equals("0")) { //数据源获取
                    sTmpFValue = sFieldValue[i];
                } else if (field.getPretType().equals("1")) { //函数获取
                    sTmpFValue = callSpring(field.getSpringCode(), sFieldValue[i]);
                }
                sTmpFValue = sTmpFValue.replaceAll("'", "''"); //增加数据中有单引号的处理
                sType = (String) hmFieldType.get(sFieldName[i].trim().toUpperCase());
                if (sType.indexOf("VARCHAR") > -1) {
                    if (sTmpFValue.trim().length() > 0 && !sTmpFValue.equals("null")) {
                        buf.append("'").append(sTmpFValue).append("',");
                    } else {
                        buf.append(dbl.sqlString(" ")).append(",");
                    }
                } else if (sType.indexOf("NUMBER") > -1) {
                    if (sTmpFValue.trim().length() > 0 && !sTmpFValue.equals("null")) {
                        buf.append(sTmpFValue).append(",");
                    } else {
                        buf.append(0).append(",");
                    }
                } else if (sType.indexOf("DATE") > -1) {
                    if (sTmpFValue.trim().length() > 0 && sTmpFValue != null) {
                        buf.append(dbl.sqlDate(sTmpFValue.substring(0, 10))).append(
                            ",");
                    } else {
                        buf.append(dbl.sqlDate("1900-01-01")).append(",");
                    }
                }
            }
            if (buf.toString().length() > 1) {
                sResult = buf.toString().substring(0, buf.toString().length() - 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("处理数据、类型、转换关联时出错", e);
        }
    }

    private String buildField(String sTabName) throws YssException { //从Tb_Fun_DataDict 取临时表字段  结果为aaa,bbb,ccc...
        String reSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            reSql = "select FFieldName from Tb_Fun_DataDict where FTabName="
                + dbl.sqlString(sTabName);
            rs = dbl.openResultSet(reSql);
            while (rs.next()) {
                buf.append(rs.getString("FFieldName")).append(",");
            }
            sResult = buf.toString();
            if (sResult.trim().length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取临时表字段出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String buildField(DaoCusConfigureBean cusCfg) throws YssException { //从文件内容表中 取表字段  结果为aaa,bbb,ccc...
        String reSql = "";
        String sResult = "";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
        	/**add---shashijie 2013-3-1 STORY 3366 获取公共表数据*/
        	reSql = " select * from ( ";
        	//组合群
        	reSql += getSelectFileContentByFdictcode(pub.yssGetTableName("TB_Dao_FileContent"), "0", cusCfg.getCusCfgCode());
        	
        	reSql += " Union All ";
        	//公共表
        	reSql += getSelectFileContentByFdictcode("TB_Dao_FileContent", "1", cusCfg.getCusCfgCode());
        	
			/**end---shashijie 2013-3-1 STORY 3366*/
            reSql += " ) a order by a.fordernum";
            rs = dbl.openResultSet(reSql);
            while (rs.next()) {
                buf.append(rs.getString("FTabFeild")).append(",");
            }
            sResult = buf.toString();
            if (sResult.trim().length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }else{//by guyichuan 20110615 #897 抛出提示
            	throw new YssException("目标表字段为空！");
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取目标表字段出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**shashijie 2013-3-1 STORY 3366*/
	private String getSelectFileContentByFdictcode(String tableName,
			String saveType, String cusCfgCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.FTabFeild,a.Fordernum from " + tableName +
			" a where FCusCfgCode=" + dbl.sqlString(cusCfgCode)	+
			" and FUnExport <> 1 ";//如果此字段设置为不导出，则不导出 　by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
		return sql;
	}

	/**
     * 将SQL中的特定字符在此方法中处理
	 * modify by wangzuochun 2009.09.27 MS00687 在接口处理里执行风控接口时，需考虑【通用业务参数设置-自动导数据处理】设置的日期参数 QDV4华夏2009年09月08日02_A 
     * @param sql String
     * @return String
     * @throws YssException
     */
    private String buildStr(String sql) throws YssException{
        try{
            sql = pretSqlIns(sql); //做一下sql内部函数的处理
            sql = replaceAll(sql, "<S1>,<S2>,<S3>", "<S>");
            sql = sql.replaceAll("<S>", "(" + operSql.sqlCodes(sPorts) + ")");
            sql = replaceAll(sql, "<D>", "<D1>");
            sql = sql.replaceAll("<D1>", dbl.sqlDate(sStartDate));
            sql = sql.replaceAll("<D2>", dbl.sqlDate(sEndDate));
            // add by leeyu 080729
            if (sql.indexOf("<U>") > 0) {
                sql = sql.replaceAll("<U>", pub.getUserCode());
            } else if (sql.indexOf("< U >") > 0) {
                sql = sql.replaceAll("< U >", pub.getUserCode());
            }
            if (sql.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
                sql = sql.replaceAll("<Year>",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            } else if (sql.indexOf("< Year >") > 0) { // add by leeyu 080729
                sql = sql.replaceAll("< Year >",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            }
            if (sql.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                sql = sql.replaceAll("<Set>", cw.getCWSetCode(portCodes));
            } else if (sql.indexOf("< Set >") > 0) { // add by leeyu 080729
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                sql = sql.replaceAll("< Set >", cw.getCWSetCode(portCodes));
            }
            if (sql.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		sql = sql.replaceAll("<Group>", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		sql = sql.replaceAll("<Group>", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            } else if (sql.indexOf("< Group >") > 0) {
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		sql = sql.replaceAll("< Group >", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		sql = sql.replaceAll("< Group >", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            }
            /**add---shashijie 2013-3-1 STORY 3366 增加组合群判断*/
            if (sql.indexOf("<WGroups>") != -1) {
            	if (this.AssetGroupCodesWhere.trim().equals("")) {
            		sql = sql.replaceAll("<WGroups>", operSql.sqlCodes(pub.getAssetGroupCode()));
				} else {
					sql = sql.replaceAll("<WGroups>", operSql.sqlCodes(this.AssetGroupCodesWhere));
				}
    		}
    		/**end---shashijie 2013-3-1 STORY 3366*/
            /**add---shashijie 2013-3-8 STORY 2869 增加组合代码判断*/
    		if (sql.indexOf("<WFPortCodes>") != -1) {
    			if (this.FPortCodesWhere.trim().equals("")) {
    				sql = sql.replaceAll("<WFPortCodes>", operSql.sqlCodes(this.sPorts));
				} else {
					sql = sql.replaceAll("<WFPortCodes>", operSql.sqlCodes(this.FPortCodesWhere));
				}
    		}
        	/**end---shashijie 2013-3-8 STORY 2869*/
            sql = sql.replaceAll("~Base", "base");
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }
        return sql;
    }
    
	/**
	 * modify by wangzuochun 2009.09.27 MS00687 在接口处理里执行风控接口时，需考虑【通用业务参数设置-自动导数据处理】设置的日期参数 QDV4华夏2009年09月08日02_A 
	 */
    private String getPreateData(String sql, String sFields, HashMap hmField) throws
        YssException { //根据SQL语句取出数据来
        ResultSet rs = null;
        String sSplitType = ""; //分隔符
        String[] arrField = sFields.split(","); //将目标表字段分开
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        try {
            sSplitType = "\t"; //默认值，\t
            sql =buildStr(sql);//此方法处理SQL特定字符 by wzc 20090927
            rs = dbl.openResultSet(sql);
            String sTmpDsFields = ""; //定义临时字段保存源字段　MS00315	QDV4南方2009年3月11日02_B by leeyu 20090402
            
            /**这里通过判断源字段值是否有逗号，如果有逗号的话就分开处理，并编写成一个字段
             * MS00315	QDV4南方2009年3月11日02_B by leeyu */
            while (rs.next()) {
                for (int i = 0; i < arrField.length; i++) {
                    sTmpDsFields = String.valueOf(hmField.get(arrField[i].toUpperCase()));
                    if (sTmpDsFields.split(",").length == 1) { //如果字段中没有逗号的话就按原来的代码进行处理 by leeyu MS00315	QDV4南方2009年3月11日02_B
                        buf.append(rs.getString(sTmpDsFields)).append(sSplitType);
                    } else { //当字段中有逗号分隔的话，说明采用的是公式或其他方式计算的，则应该将各字段分别取值然后再串成一个字段
                        for (int iD = 0; iD < sTmpDsFields.split(",").length; iD++) {
                            buf.append(rs.getString(sTmpDsFields.split(",")[iD])).append(",");
                        } //end for
                        buf.setLength(buf.length() - 1); //去掉最后一个逗号
                        buf.append(sSplitType);
                    } //end if
                    //buf.append(rs.getString(hmField.get(arrField[i].toUpperCase()).
                    //                        toString())).append(sSplitType);
                }
                buf.append("\r\n"); //行间用\r\n隔开
            }
            if (buf.toString().length() > 2) {
                sResult = buf.toString().substring(0, buf.toString().length() - 2);
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("读取预处理数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //删除临时表中原有的数据
    private void delData(DaoPretreatBean pret) throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + pret.getTargetTabCode();
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除临时表信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private String DBFieldFormat(String sFields) throws YssException {
        //本方法用于处理DataBase中的关键字,如 Date,Function...字段
        //Oracle 中加 "Date"
        //sql server 中加 [Date]
        //db2 中加
        /*database,datafile,datafiles,date,function,functions,primary,private,public,procedure
         profile,create,table,identified by,order,or,group,groups
         */
        StringBuffer buf = new StringBuffer();
        String sResult = "";
        String sSplit = "";
        String[] arrField = null;
        try {
            if (sFields.indexOf(",") > -0) {
                arrField = sFields.split(","); //处理","分隔的字符串
                sSplit = ",";
            } else if (sFields.indexOf("\t") > 0) {
                arrField = sFields.split("\t"); //处理"\t"分隔的字符串
                sSplit = "\t";
            } else {
                arrField = new String[1];
                arrField[0] = sFields;
                sSplit = " "; //添加一个,防止在下面的处理中删除最后一个字符. by liyu 0118
            }
            for (int i = 0; i < arrField.length; i++) {
                if (dbl.dbType == YssCons.DB_ORA) {
                    if (arrField[i].equalsIgnoreCase("data")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("date")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("function")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("table")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("group")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("private")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("public")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("or")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else if (arrField[i].equalsIgnoreCase("by")) {
                        buf.append("\"").append(arrField[i]).append("\"");
                    } else {
                        buf.append(arrField[i]);
                    }
                }
                buf.append(sSplit);
            }
            sResult = buf.toString();
            if (sResult.length() > 0) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("处理数据库字段时出错", e);
        }
    }

    private HashMap relateField(DaoPretreatBean pre, HashMap hmDsField) throws
        YssException {
        //此方法用于将临时表字段与系统表字段做关联，根据临时表找到系统表字段  hmDsField 为临时表的字段集
        //返回 临时表字段----系统表字段  add 1122 晚
        HashMap hmField = new HashMap();
        ResultSet rs = null;
        String strSql = "";
        try {
        	/**add---shashijie 2013-3-1 STORY 3366 增加公共表查询*/
        	//组合群SQL
			strSql = getSelectPretreatfieldByFdpdscode(pub.yssGetTableName("Tb_Dao_Pretreatfield"),"0",pre.getDPDsCode());
			
			strSql += " Union All ";
            //公共表SQL
        	strSql += getSelectPretreatfieldByFdpdscode("Tb_Dao_Pretreatfield","1",pre.getDPDsCode());
			/**end---shashijie 2013-3-1 STORY 3366*/
        	
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (hmDsField.get(rs.getString("FTargetField").toUpperCase()) == null) {
                    continue; //考虑系统表通过SQL语句查询出的字段比临时表的多
                }
                hmField.put(rs.getString("FTargetField").toUpperCase(),
                            rs.getString("FDsField").toUpperCase());
            }
            return hmField;
        } catch (Exception e) {
            throw new YssException("关联字段出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**shashijie 2013-3-1 STORY 3366 获取SQL*/
	private String getSelectPretreatfieldByFdpdscode(String tableName,
			String saveType, String dpDsCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " FTargetField,FDsField from " +
			tableName +
	        " where fdpdscode=" + dbl.sqlString(dpDsCode);
		return sql;
	}

	private void CreateTmp(String sTabName) throws YssException {
        //添加自动创建临时表的功能 根据系统数据字典信息来建表 add 1127
        String sqlStr = "";
        String createSql = "";
        String sPK = "";
        StringBuffer buf = new StringBuffer();
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = dbl.loadConnection();
            sqlStr = " select FFieldName, case when FIsNull = '1' then 'NOT NULL' else 'NULL' end as FIsNull," +
                " FKey,FFieldPre,FDefaultValue, " +
                " CASE when FFieldType ='1' then 'Number' when FFieldType='2' then 'Date' when FFieldType='3' then 'varchar2' " +
                /**shashijie 2011.03.29 STORY #814 招商证券每日需导出净值文件上传给中登,与我们系统导出的数字类型不一致*/
                " WHEN FFieldType = '4' THEN 'Number' " + //这里原先是Float shashijie 2011.03.31 数据里永远放Number类型
                " WHEN FFieldType = '5' THEN 'Numeric' " + //增加numeric类型（导出DBF文件） panjunfang modify 20120210 STORY #2236
                /**end*/
                " ELSE 'varchar2' END as FFieldType" +
                "  FROM Tb_Fun_DataDict WHERE FTabName=" +
                dbl.sqlString(sTabName);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                buf.append(rs.getString("FFieldName")).append(" ");
                buf.append(rs.getString("FFieldType")).append(" ");
                buf.append( (!rs.getString("FFieldType").equalsIgnoreCase("Date")) ?
                           ("(" + rs.getString("FFieldPre") + ")") : "");
                buf.append(" ").append(rs.getString("FIsNull")).append(",");
                if (rs.getInt("FKey") > 0) {
                    sPK += rs.getString("FFieldName") + ",";
                }
            }
            if (buf.length() > 0) {
                buf.delete(buf.length() - 1, buf.length());
            }
            if (sPK.length() > 0) {
                sPK = sPK.substring(0, sPK.length() - 1);
            }
            if (buf.length() > 0) {
                createSql = "Create Table " + sTabName + " (" + buf.toString() +
                    ")";
                dbl.executeSql(createSql);
            }
            if (buf.length() > 0 && sPK.length() > 0) {
                sPK = "Alter table " + sTabName + " Add CONSTRAINT " + sTabName +
                    " Primary Key(" + sPK + ")";
                dbl.executeSql(sPK);
            }
            conn.commit();
            rs.close();
        } catch (Exception e) {
            throw new YssException("系统自动创建临时表出错");
        } finally {
            //-- MS00456 QDV4海富通2009年05月18日01_AB  关闭游标 sj
            dbl.closeResultSetFinal(rs);
            //--------------------------------------------------
            dbl.endTransFinal(conn, false);
        }
    }

    /****
     * regex的组合转换成 replacement类型 sSource 为源 add 1219
     */
    private String replaceAll(String sSource, String regex, String replacement) {
        if (regex.length() == 0) {
            return sSource;
        }
        if (replacement.length() == 0) {
            return sSource;
        }
        String[] arrReg = null;
        if (regex.indexOf(",") > 0 && regex.lastIndexOf(",") > 0) {
            arrReg = regex.split(","); //逗号分隔
        } else if (regex.indexOf("\t") > 0 && regex.lastIndexOf("\t") > 0) {
            arrReg = regex.split("\t"); //\t分隔
        } else if (regex.indexOf(";") > 0 && regex.lastIndexOf(";") > 0) {
            arrReg = regex.split(";"); //分号分隔
        } else {
            arrReg = new String[1];
            arrReg[0] = regex;
        }
        for (int i = 0; i < arrReg.length; i++) {
            sSource = sSource.replaceAll(arrReg[i], replacement);
        }
        return sSource;
    }

    public ExpCusInterface() {
    }

    /**
     * 处理函数 WDay[参数1,参数2,参数3]  取工作日前一天
     * 参数1:传入的日期    参数2: 节假日群   参数3:相差天数
     * @param sSql String
     * @return String
     * (WDay){1}[\\[](.)*[;](.)*[;](.)*[\\]]
     * @throws YssException
     */
    private String pretSqlIns(String sSql) throws YssException {
        String sFunCode = ""; //函数名
        String strReplace = ""; //要替代的字符串
        String strCalc = ""; //通过计算得到的字符串
        String sParams = ""; //相关参数字符串
        String[] arrParams = null; //相关参数
        try {
            if (sSql.lastIndexOf("[") > 0 && sSql.lastIndexOf("]") > 0) {
                if (sSql.lastIndexOf("]") > sSql.lastIndexOf("[")) { //确保"]" 在"[" 的后面
                    sParams = sSql.substring(sSql.lastIndexOf("[") + 1,
                                             sSql.lastIndexOf("]"));
                    arrParams = sParams.split(";");
                    sFunCode = sSql.substring(sSql.lastIndexOf("[") - 4,
                                              sSql.lastIndexOf("["));
                    if (sFunCode.equalsIgnoreCase("WDay")) {
                        strReplace = "WDay" + "[\\[]" + sParams + "[\\]]";
                        CommonPretFun fun = new CommonPretFun();
                        fun.setYssPub(pub);
                        ArrayList list = new ArrayList();
                        for (int i = 0; i < arrParams.length; i++) {
                            if (arrParams[i].equalsIgnoreCase("<D1>") ||
                                arrParams[i].equalsIgnoreCase("<D>")) {
                                arrParams[i] = sStartDate;
                            }
                            if (arrParams[i].equalsIgnoreCase("<D2>")) {
                                arrParams[i] = sEndDate;
                            }
                            list.add(arrParams[i]);
                        }
                        //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
//                  fun.init(list);
                        prepFunBean.setObj(list);
                        fun.init(prepFunBean);
                        //// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
                        strCalc = dbl.sqlDate(fun.getWorkDay());
                    }
                }
            }
            sSql = sSql.replaceAll(strReplace, strCalc);
            //-----------------MS00225 QDV4南方2009年02月04日01_B sj modified -----//
            if (sFunCode.equalsIgnoreCase("WDay")) { //此处只对WDay做处理，若增加了其他的函数，需增加判断。
                if (sSql.indexOf("WDay") > 0) { //判断是否还有WDay函数
                    sSql = pretSqlIns(sSql); //若还有函数，则做递归处理。
                }
            }
            //--------------------------------------------------------------------//
            return sSql;
        } catch (Exception e) {
            throw new YssException("解析SQL内部函数出错" + e.toString(), e);
        }
    }
    
    /**
     * add by yanghaiming
     * 2010.03.16
     * MS00904
     * QDV4赢时胜上海2010年01月05日01_B
     * @param cusCfg DaoCusConfigureBean
     * @return String
     * @throws YssException
     */
    private String getParam(DaoCusConfigureBean cusCfg) throws YssException{
    	//Connection conn = null;
        String reStr = "";
        ResultSet rs = null;
        /*String sResult = "";
        String sFormat = "yyyy-MM-dd";*/
        StringBuffer stringBFile = new StringBuffer();
        try {
        	/**add---shashijie 2013-3-1 STORY 3366 公共表查询*/
        	reStr = " select * from ( ";
        	//组合群SQL
        	reStr += getSelectFileNameByFCusCfgCode(pub.yssGetTableName("TB_Dao_FileName"), "0", cusCfg.getCusCfgCode());
            
        	reStr += " Union All ";
            //公共表SQL
        	reStr += getSelectFileNameByFCusCfgCode("TB_Dao_FileName", "1", cusCfg.getCusCfgCode());
			reStr += " ) a order by a.FOrderNum ";
			/**end---shashijie 2013-3-1 STORY 3366*/
                
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
                if (rs.getString("FFileNameCls") != null) { 
                    if (rs.getString("FFileNameCls").equalsIgnoreCase("broker")) { //券商
                    	stringBFile.append(rs.getString("Ftabfeild")).append("\t").append("broker").append("\r\t");
                    } else if (rs.getString("FFileNameCls").equalsIgnoreCase("net")) { //销售网点
                    	stringBFile.append(rs.getString("Ftabfeild")).append("\t").append("net").append("\r\t");
                    } else if (rs.getString("FFileNameCls").equalsIgnoreCase(
                        "tradeseat")) { //席位
                    	stringBFile.append(rs.getString("Ftabfeild")).append("\t").append("tradeseat").append("\r\t");
                    } else if (rs.getString("FFileNameCls").equalsIgnoreCase(
                        "tradetype")) { //交易类型
                    	stringBFile.append(rs.getString("Ftabfeild")).append("\t").append("tradetype").append("\r\t");
                    } else if (rs.getString("FFileNameCls").equalsIgnoreCase("Date")){//日期
                    	//edited by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
                    	stringBFile.append("Date").append("\t").append("Date").append("\r\t");
                    	//end--- by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
                    }
                }
            }
            rs.close();
            if (stringBFile.toString().indexOf("\r\t") != -1){
            	return stringBFile.toString().substring(0,stringBFile.toString().length()-2);
            }else{
            	return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取文件名称及路径出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
    }
    
    /**
     * add by yanghaimig 20100311 MS00904 
     * @param String[] args, int i
     * @throws YssException
     */
    private void getAllData(String[] args, int i) throws YssException{
    	String columName = args[i].split("\t")[0];//列名
    	String eTabfeild = args[i].split("\t")[1];//对应的路径设置的FFileNameCls
    	//Connection conn = null;
        String reStr = "";
        ResultSet rs = null;
        DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
        cusCfg.setYssPub(pub);
        cusCfg.setCusCfgCode(sCusCfgCode);
        cusCfg.getSetting();
        
        int count = i + 1;
		if (columName.equalsIgnoreCase("Date")) {
			int days = YssFun.dateDiff(this.beginDate, this.endDate); // 日期天数
			for (int day = 0; day <= days; day++) { // 循环日期
				this.beginDate = YssFun.addDay(this.beginDate, day == 0 ? 0 : 1);
				this.sStartDate = YssFun.formatDate(this.beginDate,"yyyy-MM-dd");
				if(count == args.length){
//					runStatus.appendRunDesc("ManualOperRun", "   导出成功！\r\n");
					// 458 QDV4国泰基金2010年12月22日01_A by qiuxufeng 20110131
					try {
						runStatus.appendRunDesc("ManualOperRun", "    正在读取【" + sStartDate + "】数据... ...");
						this.sAllDateResule += doInterfaceBase(cusCfg) + "~@~";// 将不同的数据以分割符分割
						runStatus.appendRunDesc("ManualOperRun", "    导出成功！\r\n");
					} catch (Exception e) {
						runStatus.appendRunDesc("ManualOperRun", "    [color:Blue]导出失败[/color]！\r\n");
						throw new YssException(e.getMessage());
					}
					// 458 QDV4国泰基金2010年12月22日01_A by qiuxufeng 20110131
				}else{
					getAllData(args,count);//递归调用
				}
			}
		}else{
			try {
				//edited by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
				this.doPretreat(cusCfg); 
				//edited by zhouxiang MS01518 当自定义接口配置了导出席位数据时系统无法导出数据 
	            reStr = "select " + columName + " from " + cusCfg.getTabName();
	            //-------在拼接SQL查询条件时，先清理筛选条件，再加上查询条件；
	            cleanFilter(eTabfeild);
	            if(!this.eFilterType.equalsIgnoreCase("")&&this.eFilterType.length()>0){
	            	reStr += this.eFilterType;
	            }
	            if(!this.eFilterBorker.equalsIgnoreCase("")&&this.eFilterBorker.length()>0){
	            	reStr += this.eFilterBorker;
	            }
	            if(!this.eFilterNet.equalsIgnoreCase("")&&this.eFilterNet.length()>0){
	            	reStr += this.eFilterNet;
	            }
	            if(!this.eFilterTradeseat.equalsIgnoreCase("")&&this.eFilterTradeseat.length()>0){
	            	reStr += this.eFilterTradeseat;
	            }
	            if(!this.eFilterTradetype.equalsIgnoreCase("")&&this.eFilterTradetype.length()>0){
	            	reStr += this.eFilterTradetype;
	            }
	            
	            reStr += " group by " + columName;

	            //20130121 added by liubo.Story #3337
	            //当导出的接口为“Info_GZ_Asset（DBF格式的净值文件)”时，在临时表中最后会有一条总计的记录，这条记录的资产代码（A1字段）为空。
	            //如果这里直接使用GROUP BY FCurAsset，那最终会导出成两个文件，一个文件的资产代码为正常的资产代码，一个文件的资产代码为NULL
	            //在这里加上FCurAsset字段的非空判断，可以避免这种情况
	            //============================
	            if("Info_GZ_Asset".equalsIgnoreCase(cusCfg.getCusCfgCode()))
	            {
	            	reStr += " having FCurAsset <> ' '";
	            }
	            //============end================
	            
	            //------------------------------------
	            rs = dbl.openResultSet(reStr);
	            while (rs.next()) {
		            
		            if (eTabfeild.equalsIgnoreCase("broker")){
		            	this.eBroker = rs.getString(columName);
		            	this.eFilterBorker = " and " + columName + "=" + dbl.sqlString(this.eBroker);//增加查询过滤的条件
		            }else if (eTabfeild.equalsIgnoreCase("net")){
		            	this.eNet = rs.getString(columName);
		            	this.eFilterNet = " and " + columName + "=" + dbl.sqlString(this.eNet);//增加查询过滤的条件
		            }else if (eTabfeild.equalsIgnoreCase("tradeseat")){
		            	this.eTradeseat = rs.getString(columName);
		            	this.eFilterTradeseat = " and " + columName + "=" + dbl.sqlString(this.eTradeseat);//增加查询过滤的条件
		            }else if (eTabfeild.equalsIgnoreCase("tradetype")){
		            	this.eTradetype = rs.getString(columName);
		            	this.eFilterTradetype = " and " + columName + "=" + dbl.sqlString(this.eTradetype);//增加查询过滤的条件
		            }
	            	if(i == args.length - 1){
//	            		this.sAllDateResule += doInterfaceBase(cusCfg) + "~@~";// 将不同的数据以分割符分割
	            		// 458 QDV4国泰基金2010年12月22日01_A by qiuxufeng 20110131
	            		try {
	            			runStatus.appendRunDesc("ManualOperRun", "    正在读取【" + sStartDate + "】数据... ...");
							this.sAllDateResule += doInterfaceBase(cusCfg) + "~@~";// 将不同的数据以分割符分割
							runStatus.appendRunDesc("ManualOperRun", "    导出成功！\r\n");
						} catch (Exception e) {
							runStatus.appendRunDesc("ManualOperRun", "    [color:Blue]导出失败[/color]！\r\n");
							throw new YssException(e.getMessage());
						}
						// 458 QDV4国泰基金2010年12月22日01_A by qiuxufeng 20110131
	            	}else{
	            		getAllData(args,count);//递归调用
	            	}
	            }
	            cleanFilter(eTabfeild);
	            rs.close();
	        } catch (Exception ex) {
	            throw new YssException("获取文件名称及路径出错", ex);
	        } finally {
	            dbl.closeResultSetFinal(rs);
	        }
		}	
    }
    
    //add by yanghaiming 20100312 MS00904 
    //清理过滤条件
    private void cleanFilter(String sFilter){
    	if (sFilter.equalsIgnoreCase("broker")){
        	this.eFilterBorker = "";
        }
        if (sFilter.equalsIgnoreCase("net")){
        	this.eFilterNet = "";
        }
        if (sFilter.equalsIgnoreCase("tradeseat")){
        	this.eFilterTradeseat = "";
        }
        if (sFilter.equalsIgnoreCase("tradetype")){
        	this.eFilterTradetype = "";
        }
    }

	public void checkInput(byte btOper) throws YssException {
		
	}

	public String addSetting() throws YssException {
		return null;
	}

	public String editSetting() throws YssException {
		return null;
	}

	public void delSetting() throws YssException {
		
	}

	public void checkSetting() throws YssException {
		
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		return null;
	}

	public String getAllSetting() throws YssException {
		return null;
	}

	public void deleteRecycleData() throws YssException {
		
	}

	public String getBeforeEditData() throws YssException {
		return "";
	}

	public void parseRowStr(String sRowStr) throws YssException {
		
	}

	public String buildRowStr() throws YssException {
		return "";
	}

	public String getOperValue(String sType) throws YssException {
		return null;
	}

	public String getListViewData1() throws YssException {
		return null;
	}

	public String getListViewData2() throws YssException {
		return null;
	}

	public String getListViewData3() throws YssException {
		return null;
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
	
	/**shashijie 获取合并文件名路径 2011.03.28 */
	private String doFileMergerName(DaoCusConfigureBean cusCfg) throws YssException {
		String reStr = "";
        ResultSet rs = null;
        String sResult = "";
        try {
        	/**add---shashijie 2013-3-1 STORY 3366 公共表查询*/
        	reStr = " select * from ( ";
        	//组合群SQL
        	reStr += getSelectFileMergerNameByFCusCfgCode(pub.yssGetTableName("Tb_Dao_FileMergerName"), "0", cusCfg.getCusCfgCode());
            
        	reStr += " Union All ";
            //公共表SQL
        	reStr += getSelectFileMergerNameByFCusCfgCode("Tb_Dao_FileMergerName", "1", cusCfg.getCusCfgCode());
        	reStr += " ) a order by a.FOrderNum";
			/**end---shashijie 2013-3-1 STORY 3366*/
    		
            rs = dbl.openResultSet(reStr);
            while (rs.next()) {
            	
            	// add by lidaolong 20110328 #842 需要在导出文件时，“文件夹”的日期和“文件”日期能根据文件名设置中的日期调整来生成文件
            	//获取到文件及文件名中的日期
            	String strDate =  getFileDate(cusCfg.getCusCfgCode(),"Tb_Dao_FileMergerName",rs.getInt("fordernum"));
            	//end by lidaolong 
            	
            	//与获取文件名调用同一方法 shashijie 2011.03.28
                sResult += getFileOrMergerName(rs,cusCfg,strDate);///edit by lidaolong 20110328 #3753::“文件夹”和“文件”日期根据文件名设置中的日期调整导出文件
            }
            dbl.closeResultSetFinal(rs);
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取文件名称及路径出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs); //在finally中关闭结果集 QDV4海富通2009年05月11日03_AB MS00442 by leeyu 20090514
        }
	}

	/**shashijie 2013-3-1 STORY 3366 */
	private Object getSelectFileMergerNameByFCusCfgCode(String tableName,
			String saveType, String cusCfgCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName + " a "+
        	" where a.FCusCfgCode=" + dbl.sqlString(cusCfgCode);
		return sql;
	}

	/**解析拼接合并文件名
	 * shashijie 2011.03.28
	 * /eidt  by lidaolong 20110328 #3753::“文件夹”和“文件”日期根据文件名设置中的日期调整导出文件
	 * 添加strDate日期参数:此日期是调整后的日期
	 */
	private String getFileOrMergerName(ResultSet rs,DaoCusConfigureBean cusCfg,String strDate) throws YssException{
		String sResult = "";
		String sFormat = "yyyy-MM-dd"; //默认
		String sFfilenamedict="";//字典类型   add by wuweiqi 20101125  QDV4赢时胜深圳2010年10月29日01_A
		String sFile=""; //字典处理后的文件名路径   add by wuweiqi 20101125  QDV4赢时胜深圳2010年10月29日01_A
		try {
        	
			if (rs.getString("FFileNameCls") != null) { //by liyu 1219 处理导出接口中有不同的参数
	            if (rs.getString("FFileNameCls").equalsIgnoreCase("no")) { //固定
	                sResult += rs.getString("FFileNameConent");
	            } else if (rs.getString("FFileNameCls").equalsIgnoreCase("date")) {
	                if (rs.getString("FFormat") != null) {
	                    sFormat = rs.getString("FFormat");
	                }
	                //--- MS01080 QDV4国泰2010年4月9日01_A 接口处理，要求能导出英文日期格式    add by jiangshichao 2010.04.27 ----
	                /**************************************************
	                 *  例如 2009年12月31日  对应日期格式为 : 31-Dec-09
	                 */
	                if(sFormat.split(",").length>1){
	                	if(sFormat.split(",")[1].toUpperCase().equalsIgnoreCase("ENGLISH")){
	                		//update by guolongchao STORY 2210 将原来写死的日期格式："dd-MMM-yyyy" 改为sFormat.split(",")[0]
                    		//SimpleDateFormat formatter  = new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH);
	                		SimpleDateFormat formatter  = new SimpleDateFormat(sFormat.split(",")[0],Locale.ENGLISH);
                        	/*sResult +=formatter.format(YssFun.toDate(this.sStartDate));*/
                    		sResult +=formatter.format(YssFun.toDate(strDate));// edit by lidaolong 20110328 #842 需要在导出文件时，“文件夹”的日期和“文件”日期能根据文件名设置中的日期调整来生成文件
                    	}else {
                    		/*sResult += YssFun.formatDate(this.sStartDate, sFormat.split(",")[0]);*/
                    		sResult += YssFun.formatDate(strDate, sFormat.split(",")[0]);// edit by lidaolong 20110328 #842 需要在导出文件时，“文件夹”的日期和“文件”日期能根据文件名设置中的日期调整来生成文件
                    	}
	                }
	                //------ modify by wangzuochun BUG #484 接口导出时：当导出路径中有日期时，路径出现两个日期 
					else {
						// edit by lidaolong 20110328 #842 需要在导出文件时，“文件夹”的日期和“文件”日期能根据文件名设置中的日期调整来生成文件
						// modify by nimengjing BUG #562 导出数据接口，在文件名称中如果有日期，日期格式设置无效
						if ("null".equalsIgnoreCase(sFormat)) {
							
							/*sResult += YssFun.formatDate(this.sStartDate,
									"yyyy-MM-dd");*/
							sResult += YssFun.formatDate(strDate,
							"yyyy-MM-dd");
						} else {
							/*sResult += YssFun.formatDate(this.sStartDate,
									sFormat);*/
							sResult += YssFun.formatDate(strDate,
									sFormat);
						}
						//end by lidaolong
						// --------------------------end  bug#562----------------------------------------------
					}
	                //--------------------------- BUG #484 --------------------------------//
	            } 
	            //begin-------edit by yanghaiming 20100312 MS00904 实现券商，销售网点，席位，交易类型拼接路径    
	            else if (rs.getString("FFileNameCls").equalsIgnoreCase("broker")) { //券商
	            	if (this.eBroker!= null && !this.eBroker.equalsIgnoreCase(" ") && this.eBroker.length() > 0){
	            		sResult += this.eBroker;
	            	}else{
	            		sResult += "null";
	            	}	
	            } else if (rs.getString("FFileNameCls").equalsIgnoreCase("net")) { //销售网点
	            	if (this.eNet!=null && !this.eNet.equalsIgnoreCase(" ") && this.eNet.length() > 0){
	            		sResult += this.eNet;
	            	}else{
	            		sResult += "null";
	            	}
	            } else if (rs.getString("FFileNameCls").equalsIgnoreCase("tradeseat")) { //席位

	                //20130121 added by liubo.Story #3337
	                //当导出的接口为“Info_GZ_Asset（DBF格式的净值文件)”时，文件名的交易席位字段会被当做资产代码来存值
	                //=================================
		            if (cusCfg.getCusCfgCode().equalsIgnoreCase("Info_GZ_Asset"))
		            {
		            	sResult += this.eTradeseat;
		            }
	                //===============end==================
		            else if (this.eTradeseat!=null && !this.eTradeseat.equalsIgnoreCase(" ") && this.eTradeseat.length() > 0){
	            		sResult += this.eTradeseat;
	            	}else{
	            		sResult += "null";
	            	}
	            } else if (rs.getString("FFileNameCls").equalsIgnoreCase("tradetype")) { //交易类型
	            	if (this.eTradetype!=null && !this.eTradetype.equalsIgnoreCase(" ") && this.eTradetype.length() > 0){
	            		sResult += this.eTradetype;
	            	}else{
	            		sResult += "null";
	            	}
	            }
	            //end-------------edit by yanghaiming 20100312 MS00904 实现券商，销售网点，席位，交易类型拼接路径    
	            else if (rs.getString("FFileNameCls").equalsIgnoreCase("port")) { //组合
	            	sResult += this.portCodes;//add by yanghaiming 20090115 MS00904 QDV4赢时胜上海2010年01月05日01_B
	            }
	            // ---QDV4海富通2009年3月9日01_AB ---------------------------------
	            
	            //------ add by wangzuochun 2010.07.07 MS01338    对于多组合情况，在进行接口处理的时，选择两个组合后，系统是一次只是对一个组合进行处理    QDV4赢时胜深圳2010年6月21日01_B  
	            else if (rs.getString("FFileNameCls").equalsIgnoreCase("SinglePortFile")) { //单组合单文件
	            //------ add by wuweiqi 20101125  导出文件名进行字典处理   QDV4赢时胜深圳2010年10月29日01_A----------------------//
	            	if(rs.getString("Ffilenamedict")!=null && !rs.getString("Ffilenamedict").equals(""))//判断文件名是否进行字典处理
	            	{
	            	    sFfilenamedict=rs.getString("Ffilenamedict");
	            	    sFile = doSinglePortFile(cusCfg,sFfilenamedict);
	                	if(!sFile.equals(""))
	                	{
	                		sResult += sFile;
	                	}
	                	else{
	                		sResult += this.portCodes;
	                	}
	            	}
	            	else
	            	{
	            		sResult += this.portCodes;
	            	}
	            //--------------------- end by wuweiqi 20101125 -----------------------------------------------------------------//	
	            } 
	            //-----------------------------MS01338----------------------------//
	            
	            else if (rs.getString("FFileNameCls").equalsIgnoreCase("dateparagraph")) { //日期段
	            	//fanghaoln 20100128 MS00947 QDV4赢时胜上海2010年01月22日01_B 
	                if (rs.getString("FFormat") != null && !rs.getString("FFormat").equalsIgnoreCase("null")) {
	                //-------------end --MS00947--------------------------------------
	                    sFormat = rs.getString("FFormat");
	                }
	                sResult += YssFun.formatDate(this.sStartDate, sFormat); //通过动态递增的日期来生成日期段数量的目录、文件
	            }
	            //---------------------------------------------------------------
				//---add by songjie 2011.09.27 需求 #1385 QDV4赢时胜上海2011年07月19日01_A start---//
	            else if (rs.getString("FFileNameCls").equalsIgnoreCase("AssetGroup")) {
	            	sResult += pub.getPrefixTB();
	            }
				//---add by songjie 2011.09.27 需求 #1385 QDV4赢时胜上海2011年07月19日01_A end---//
	            else {
	                return sResult; //若没有
	            }
	        }
		}
		catch (Exception e) {
			throw new YssException("获取合并文件名称及路径出错", e);
		}
		return sResult;
	}
	
	/**根据数据字典设置,获取相对类型number还是float*/
	private String getFieldStype(ResultSet rs,String sType) throws YssException{
		String type = sType;
		try {
			//虽然页面上是float,但是后台原先是当Number来处理的所以这里不变
			//这里虽然赋予Number但是等会传入前台判断时写死是转成float原因不明.
			if (rs.getString("FFieldType").trim().equals("1")) {
				type = "Number";
			} else if(rs.getString("FFieldType").trim().equals("4")) {
				//这里虽然赋值Float但是等会页面上会转成Number
				type = "Float";
			} else if(rs.getString("FFieldType").trim().equals("5")) {
				type = "Numeric";
			}
		} catch (SQLException e) {
			throw new YssException("获取数据字段类型出错", e);
		}
		return type;
	}
	//by zhouwei 20120110 重载方法，执行单个预处理命令，使导出可以使用提示源预处理
	public String doOnePretreat(String pretreatCode) throws YssException {
		DaoPretreatBean pret = new DaoPretreatBean();
		pret.setYssPub(pub);
		pret.setDPDsCode(pretreatCode);
		pret.getSetting();
		return doOnePretreat(pret);
	}
	//by zhouwei 20120110  只执行提示源预处理
	public String doOnePretreat(DaoPretreatBean pret) throws YssException{
        String sResult="";
        try{
        	if (pret.getDsType() == 4) { // 提示类型预处理数据源 
				sResult = promptPret(pret);
			}
        }catch(Exception ex){
            throw new YssException(ex);
        }
		return sResult;
	}
	/**
	 * 处理提示类型的预处理方法 
	 * 
	 * @param pret
	 *            DaoPretreatBean
	 * @return String
	 * @throws YssException
	 */
	private String promptPret(DaoPretreatBean pret) throws YssException {
		String sResult = "";
		CtlFunction funCtl = new CtlFunction();
		funCtl.setYssPub(pub);
		funCtl.init(pret.getDataSource());
		funCtl.setBaseOper(this);
		sResult = funCtl.doFunctions();
		return sResult;
	}
	/**
	 * 将自定义的数据源解析成标准的sql语句
	 * 
	 * @param sql
	 * @return
	 * @throws YssException
	 */
	public String buildDsSql(String sql) throws YssException {

        try{
            sql = pretSqlIns(sql); //做一下sql内部函数的处理
            sql = replaceAll(sql, "<S1>,<S2>,<S3>", "<S>");
            sql = sql.replaceAll("<S>", "(" + operSql.sqlCodes(this.portCodes) + ")");
            sql = replaceAll(sql, "<D>", "<D1>");
            sql = sql.replaceAll("<D1>", dbl.sqlDate(this.beginDate));
            sql = sql.replaceAll("<D2>", dbl.sqlDate(this.endDate));
            // add by leeyu 080729
            if (sql.indexOf("<U>") > 0) {
                sql = sql.replaceAll("<U>", pub.getUserCode());
            } else if (sql.indexOf("< U >") > 0) {
                sql = sql.replaceAll("< U >", pub.getUserCode());
            }
            if (sql.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
                sql = sql.replaceAll("<Year>",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            } else if (sql.indexOf("< Year >") > 0) { // add by leeyu 080729
                sql = sql.replaceAll("< Year >",
                                     YssFun.formatDate(this.beginDate, "yyyy"));
            }
            if (sql.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                sql = sql.replaceAll("<Set>", cw.getCWSetCode(portCodes));
            } else if (sql.indexOf("< Set >") > 0) { // add by leeyu 080729
                YssFinance cw = new YssFinance();
                cw.setYssPub(pub);
                sql = sql.replaceAll("< Set >", cw.getCWSetCode(portCodes));
            }
            if (sql.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		sql = sql.replaceAll("<Group>", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		sql = sql.replaceAll("<Group>", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            } else if (sql.indexOf("< Group >") > 0) {
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	//若为跨组合群操作 
            	if(pub.getPrefixTB() != null && !pub.getPrefixTB().equals(pub.getAssetGroupCode())){
            		//则<Group>替换为跨组合群操作的已选组合群代码
            		sql = sql.replaceAll("< Group >", pub.getPrefixTB());
            	}else{
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            		sql = sql.replaceAll("< Group >", pub.getAssetGroupCode());
            		//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            	}
            	//add by songjie 2010.02.04 MS00967 QDV4赢时胜上海2010年01月29日01_B
            }
            sql = sql.replaceAll("~Base", "base");
        }catch(Exception ex){
            throw new YssException(ex.getMessage());
        }
        return sql;
    
	}
	//----------------end -------------------by zhouwei----------------

	/**add---shashijie 2013-3-1 返回 assetGroupCodesWhere 的值*/
	public String getAssetGroupCodesWhere() {
		return AssetGroupCodesWhere;
	}

	/**add---shashijie 2013-3-1 传入assetGroupCodesWhere 设置  assetGroupCodesWhere 的值*/
	public void setAssetGroupCodesWhere(String assetGroupCodesWhere) {
		AssetGroupCodesWhere = assetGroupCodesWhere;
	}

	/**add---shashijie 2013-3-8 返回 fPortCodesWhere 的值*/
	public String getFPortCodesWhere() {
		return FPortCodesWhere;
	}

	/**add---shashijie 2013-3-8 传入fPortCodesWhere 设置  fPortCodesWhere 的值*/
	public void setFPortCodesWhere(String fPortCodesWhere) {
		FPortCodesWhere = fPortCodesWhere;
	}
}
