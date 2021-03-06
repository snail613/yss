package com.yss.main.datainterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseBean;
import com.yss.log.DayFinishLogBean;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.IClientOperRequest;
import com.yss.main.operdeal.datainterface.BaseDaoOperDeal;
import com.yss.main.operdeal.datainterface.ExpCusInterface;
import com.yss.main.operdeal.datainterface.ImpCusInterface;
import com.yss.main.operdeal.datainterface.cnstock.SuccessInPutBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class DaoInterfaceManageBean
    extends BaseBean implements IClientOperRequest {

    private java.util.Date startDate;
    private java.util.Date endDate;
    private String sPorts = "";
    private String beanId = ""; //导入导出时使用不同的bean

//   private String operType="";
    private String cusConfigCode = ""; //一个自定义接口代码
    private String allData = ""; //一个自定义接口中对应的所有数据，可能是多个文件数据
    private String check = ""; //chenyibo   20071107    是否需要审核
    private String tradeSeat = ""; //chenyibo  20071205   交易席位
    private String sPrepared = ""; //增加预处理代码的传输设置 MS00032
    private String sAssetGroupCode = ""; //组合群代码 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
    private java.util.Date DealDate;//处理日期 MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 add by jiangshichao 2010.09.10---
    
    //add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现(为了避免混淆，仅供STORY1285读数完成后浏览数据使用)
    private String sCusConfigCode1285 = "";//自定义接口代码
    private String sAssetGroupCode1285 = "";//组合群代码
    //add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现(为了避免混淆，仅供STORY1285读数完成后浏览数据使用)--end
    
    //---add by songjie 2012.12.18 STORY #2343 QDV4建行2012年3月2日04_A start---//
    private SingleLogOper logOper;//设置日志实例
    private String[] transInfo = null; //获取前台日志数据
    //---add by songjie 2012.12.18 STORY #2343 QDV4建行2012年3月2日04_A end---//
    
    /**add---shashijie 2013-2-28 STORY 3366 增加字段组合群代码,多个组合群代码用逗号分割*/
	private String AssetGroupCodesWhere = " ";
	/**end---shashijie 2013-2-28 STORY 3366*/
	
	/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
	private String FPortCodesWhere = " ";
	/**end---shashijie 2013-3-8 STORY 2869*/
	
    public DaoInterfaceManageBean() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
//   public String getOperValue(String sType) {
    public String getOperValue(String sType) throws YssException {
        //==========//
        BaseDaoOperDeal daoOperDeal = null;
        String reResult = "";
        if (sType != null && sType.equalsIgnoreCase("doPretreat")) { //如果是执行预处理的请求的话
            daoOperDeal = (BaseDaoOperDeal) pub.getDataInterfaceCtx().getBean(
                beanId);
            daoOperDeal.setYssPub(pub);
            daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
                             cusConfigCode, this.check, this.tradeSeat); //20071107   chenyibo   增加导入数据的时候是否要审核
            if (daoOperDeal instanceof ImpCusInterface) { //如果是导入操作
                daoOperDeal.setAllData(this.allData);
				daoOperDeal.setDealDate(DealDate);//add by jiangshichao  MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据 add by jiangshichao 2010.09.10---
            }
            if (this.sAssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
                String prefixTB = pub.getPrefixTB();//得到原有表前缀
                pub.setPrefixTB(this.sAssetGroupCode);//将当前组合群设为表前缀
                daoOperDeal.setYssPub(pub);
                try {
                    reResult = daoOperDeal.doOnePretreat(sPrepared);//将预处理代码传进去，目前导出还没有重写这个方法
                } catch (Exception e) {
                    throw new YssException(e);
                } finally {
                    pub.setPrefixTB(prefixTB);//设回表前缀
                }
            } else {
                reResult = daoOperDeal.doOnePretreat(sPrepared); //将预处理代码传进去，目前导出还没有重写这个方法
            }
            return reResult;
        }
        //==========//
        // add by qiuxufeng 20110131 458 QDV4国泰基金2010年12月22日01_A
        else if(sType != null && sType.equalsIgnoreCase("judgeMultiDirector")) {
        	try {
				ExpCusInterface expCusInterface = (ExpCusInterface)pub.getDataInterfaceCtx().getBean(beanId);
				expCusInterface.setYssPub(pub);
				DaoCusConfigureBean cusCfg = new DaoCusConfigureBean();
				cusCfg.setCusCfgCode(this.cusConfigCode);
				if(expCusInterface.judgeMultiDirector(cusCfg)) {
					return "true";
				} else {
					return "false";
				}
			} catch (Exception e) {
				return "error";
			}
        }
        // add by qiuxufeng 20110131 458 QDV4国泰基金2010年12月22日01_A
        //add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
        else if(sType != null && sType.equalsIgnoreCase("getInvokeFormParams")) 
        {
        	ResultSet rs=null;
        	ResultSet rs1=null;
        	StringBuffer sb=new StringBuffer();
        	String strSql=" select a.fmenubarcode from "+pub.yssGetTableName("Tb_Dao_CusConfig") 
        	              +" a where  a.fcuscfgcode ="+dbl.sqlString(this.sCusConfigCode1285);
        	 try 
        	 {
        		 rs=dbl.openResultSet(strSql);
        		 if(rs.next())
        		 {
        			 String menubarcodes=rs.getString("fmenubarcode");
        			 String[] fmenuBarCodes= (menubarcodes == null) ? null: menubarcodes.split(",");
        			 if(fmenuBarCodes!=null&&fmenuBarCodes.length>0)
        			 {
        				 for(int i=0;i<fmenuBarCodes.length;i++)
        				 {
        					 strSql=" select b.fdllname ,b.fclassname ,b.fmethodname ,b.fparams ,a.fbarcode ,a.fbarname ,a.FTabMainCode" +
   			                        " from  Tb_Fun_Menubar a left join TB_FUN_REFINVOKE b  on a.frefinvokecode = b.frefinvokecode " +          			      
   			                        " where a.fbarcode = "+dbl.sqlString(fmenuBarCodes[i]);
        					 rs1=dbl.openResultSet(strSql);
        					 if(rs1.next())
        					 {
        						 sb.append(rs1.getString("fdllname")+"\t");
        						 sb.append(rs1.getString("fclassname")+"\t");
        						 sb.append(rs1.getString("fmethodname")+"\t");
        						 sb.append(rs1.getString("fparams")+"\t");
        						 sb.append(rs1.getString("fbarcode")+"\t");
        						 sb.append(rs1.getString("fbarname")+"\t");  
        						 if(i!=fmenuBarCodes.length-1)
        						     sb.append(rs1.getString("FTabMainCode")+"\t");  
        						 else
        							 sb.append(rs1.getString("FTabMainCode"));  
        					 }
        					 dbl.closeResultSetFinal(rs1);
        				 }        
        			 }
        		 }
			 } 
        	 catch (SQLException e) {
        		 throw new YssException(e.getMessage(), e);
			 }
        	 finally{
        		 dbl.closeResultSetFinal(rs);
        		 dbl.closeResultSetFinal(rs1);
        	 }
        	 return sb.toString();
        }
        //add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现---end
		//add by zhouwei 20120110 获取自定接口的预处理命令 STORY #1434 
		//接口导出支持提示数据源
        else if(sType!=null && sType.equalsIgnoreCase("getPretreatCode")){
        	ResultSet rs=null;
        	String reStr="";
        	//add by songjie 2012.09.29 BUG 5857 QDV4赢时胜(上海)2012年09月26日02_B
        	String tableName = "";
        	try{
        		//--- add by songjie 2012.09.29 BUG 5857 QDV4赢时胜(上海)2012年09月26日02_B start---//
        		//若已选组合群非当前登录组合群，则根据已选组合群拼接表名
        		if(this.sAssetGroupCode.trim().length() > 0 && !this.sAssetGroupCode.equals(pub.getPrefixTB())){
        			tableName = "Tb_" + this.sAssetGroupCode + "_dao_cusconfig";
        		}else{
        			tableName = pub.yssGetTableName("tb_dao_cusconfig");
        		}
        		//--- add by songjie 2012.09.29 BUG 5857 QDV4赢时胜(上海)2012年09月26日02_B end---//
        		
        		//edit by songjie 2012.09.29 BUG 5857 QDV4赢时胜(上海)2012年09月26日02_B
        		
        		/**add---shashijie 2013-3-1 STORY 3366  增加公共表的获取SQL*/
        		String sql = getSelectCusConfigByfcuscfgcode(tableName,"0");
                
            	sql += " Union All ";
                //公共表SQL
                sql += getSelectCusConfigByfcuscfgcode("tb_dao_cusconfig","1");
    			/**end---shashijie 2013-3-1 STORY 3366 */
        		rs=dbl.openResultSet(sql);
        		if(rs.next()){
        			reStr=rs.getString("fdpcodes");//预处理代码
        		}
        		return reStr;
        	}catch (Exception e) {
        		 throw new YssException(e.getMessage(), e);
			}finally{
				dbl.closeResultSetFinal(rs);
			}
        }
		//add by zhouwei 20120503 查找国内接口处理的交易接口明细库的转换前和转换后不一致的证券代码
        else if("getSecurityCodeOfSubTrade".equalsIgnoreCase(sType)) {
        	String sql="";
        	ResultSet rs=null;
        	StringBuffer sb=new StringBuffer();
        	try{
        		sql="SELECT DISTINCT FZqdm,Zqdm FROM "+pub.yssGetTableName("TB_HzJkMx")
        		   +" WHERE FDate >="+dbl.sqlDate(this.startDate)+" AND FDate <="+dbl.sqlDate(this.endDate)
        		   +" and substr(FZqdm,0,length(FZqdm)-3)<>Zqdm";   // 转换后与转换前不一致的证券代码		
        		rs=dbl.openResultSet(sql);
        		while(rs.next()){
        			sb.append(rs.getString("Zqdm")).append("\t")
        			.append(rs.getString("FZqdm")).append("\r\n");
        		}
        		if(sb.length()>0){
        			return "导入的证券代码\t转换的证券代码\t已转换的证券代码"+"\r\f"+sb.toString().substring(0, sb.length()-2);
        		}else{
        			return "";
        		}
        	}catch (Exception e) {
        		 throw new YssException(e.getMessage(), e);
			}finally{
				dbl.closeResultSetFinal(rs);
			}
        }else if("changeSecurityCode".equalsIgnoreCase(sType)){//证券代码转换
        	if(changeSecurityCode()){
        		return "success";//转换成功
        	}else{
        		return "fail";
        	}
        //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A start---//
        }else if("getLogSumCode".equalsIgnoreCase(sType)){//获取日志汇总编号
            DayFinishLogBean df = new DayFinishLogBean();
            df.setYssPub(pub);
    		return df.getLogSumCodes();
        }else if(sType.equals("createSumLog")){//生成汇总日志数据
			createSumLog();
		}
        //---add by songjie 2012.12.17 STORY #2343 QDV4建行2012年3月2日04_A end---//
        return "";
    }
    
	/**shashijie 2013-3-1 STORY */
	/**shashijie 2013-3-1 STORY 3366 获取SQL */
	private String getSelectCusConfigByfcuscfgcode(String tableName, String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* from " + tableName + " a where fcheckstate=1 " +
			" and fcuscfgcode ="+dbl.sqlString(this.cusConfigCode);
		return sql;
	}
	

	/**
	 * add by songjie 2012.12.27
	 * STORY #2343 QDV4建行2012年3月2日04_A
	 * 生成汇总日志数据
	 * @throws YssException
	 */
	private void createSumLog() throws YssException{
		String strSql = "";
		String errorInfo = " ";
		boolean isError = false;//状态  接口处理成功 或 失败
		int operType = 9;// 接口处理类型：导入 或 导出
		String logSumCode = "";//汇总日志编号
		java.util.Date operStartDate = null;//接口处理开始日期
		java.util.Date operEndDate = null;//接口处理结束日期
		java.util.Date startTime = null;//开始时间
		String assetGroupCode = "";//组合群代码
		String portCodes = "";//组合代码
		String cusCfgCode = "";//接口代码
		String currentAssetGroupCode = pub.getPrefixTB();//当前组合群代码
		//boolean comeFromDD = false;
		try{
			//业务日期、结束日期、开始时间、汇总编号、状态（成功 或 失败）、操作类型（9 - 导入、10 - 导出）、
			//组合群代码、组合代码、接口代码、报错信息（错误详细信息）
			
			operStartDate = YssFun.parseDate(this.transInfo[0],"yyyy-MM-dd");
			operEndDate = YssFun.parseDate(this.transInfo[1],"yyyy-MM-dd");
			startTime = YssFun.parseDate(this.transInfo[2],"yyyy-MM-dd hh:mm:ss");
			logSumCode = this.transInfo[3];
			isError = Boolean.valueOf(this.transInfo[4]);
			operType = Integer.valueOf(this.transInfo[5]);
			assetGroupCode = this.transInfo[6];
			if(assetGroupCode != null && !assetGroupCode.equals("null") && assetGroupCode.trim().length() > 0){
				pub.setPrefixTB(assetGroupCode);
			}
			portCodes = this.transInfo[7];
			cusCfgCode = this.transInfo[8];
			
			if(this.transInfo.length >= 10){
				errorInfo = this.transInfo[9];//异常信息
			}
			
			if(errorInfo.trim().length() == 0){
				if(!isError){
					if(operType == 9){
						errorInfo = "接口导入成功";
					}else{
						errorInfo = "接口导出成功";
					}
				}else{
					if(operType == 9){
						errorInfo = "接口导入失败";
					}else{
						errorInfo = "接口导出失败";
					}
				}
			}
			
			strSql = " delete from T_Plugin_Log where C_REF_NUM = '[root]' and FLOGSUMCODE = 'sum:" + logSumCode + "'";
			dblBLog.executeSql(strSql);
			
			this.logOper = SingleLogOper.getInstance();
			this.setFunName("interfacedeal");//接口处理
    		if(logOper != null){
    			// 9 - 导入、 10 - 导出
    			if(cusCfgCode.equals("sum")){//生成汇总日志数据
	    			logOper.setDayFinishIData(this, operType, "sum", pub, isError, 
	    					" ", operStartDate, operStartDate, operEndDate, " ", 
	            		    startTime, logSumCode, new java.util.Date());
    			}else{
        			if(YssFun.dateDiff(operStartDate, operEndDate) != 0){
        				java.util.Date dealDate = null;
						//循环业务日期保存日志数据
        				for(int i = 0; i <= YssFun.dateDiff(operStartDate, operEndDate); i++){
        					dealDate = YssFun.addDay(operStartDate, i);
            	    		logOper.setDayFinishIData(this, operType, cusCfgCode, pub, isError, 
            	    				portCodes, dealDate, dealDate, dealDate, errorInfo, 
            	            		startTime, logSumCode, new java.util.Date());
        				}
        			}else{
    		    		logOper.setDayFinishIData(this, operType, cusCfgCode, pub, isError, 
    		    				portCodes, operStartDate, operStartDate, operStartDate, errorInfo, 
    		            		startTime, logSumCode, new java.util.Date());
        			}
    			}
    		}
		}catch(Exception e){
			throw new YssException("生成汇总日志数据出错！");
		}finally{
			pub.setPrefixTB(currentAssetGroupCode);
		}
	}
    
    /** add by zhouwei 20120504 进行证券代码的变换
    * @Title: changeSecurityCode 
    * @Description: TODO
    * @param @throws YssException    设定文件 
    * @return void    返回类型 
    * @throws 
    */
    private boolean changeSecurityCode() throws YssException{
    	Connection conn=dbl.loadConnection();
    	boolean bTrans=false;
    	ResultSet rs=null;
    	String sql="";
    	PreparedStatement pst=null;
    	boolean isSuccess=false;
    	try{
    		bTrans=true;
    		conn.setAutoCommit(false);
    		String[] arrSecs=this.allData.split("\r\f");
        	for(int i=0;i<arrSecs.length;i++){
        		if(arrSecs[i].equals("")){
        			continue;
        		}
    			String[] arrSec=arrSecs[i].split("\t");
    			String secAfter=arrSec[1];//转换后证券代码
    			String secBefore=arrSec[0];//转换前证券
    			if(secBefore.equals(secAfter)){
    				continue;
    			}
    			sql="select FSECURITYCODE from "+pub.yssGetTableName("tb_para_security")
    			   +" where FSECURITYCODE="+dbl.sqlString(secAfter);
    			rs=dbl.openResultSet(sql);
    			if(!rs.next()){//不存在转换后的证券,将所有的交易数据更新为转换后证券
    				//变更证券信息
    				sql="update "+pub.yssGetTableName("tb_para_security")+" set FSecurityCode="+dbl.sqlString(secAfter)
    				   +" where FSecurityCode="+dbl.sqlString(secBefore);
    				dbl.executeSql(sql);
    				//变更债券信息
    				sql="update "+pub.yssGetTableName("tb_Para_FixInterest")+" set FSecurityCode="+dbl.sqlString(secAfter)
 				       +" where FSecurityCode="+dbl.sqlString(secBefore);
    				dbl.executeSql(sql);
    				//变更估值方法连接
    				sql="update "+pub.yssGetTableName("tb_Para_MTVMethodLink")+" set FLinkCode="+dbl.sqlString(secAfter)
				       +" where FLinkCode="+dbl.sqlString(secBefore);
    				dbl.executeSql(sql);
    				//变更交易数据
    				sql="update "+pub.yssGetTableName("tb_Data_Trade")+" set FSecurityCode="+dbl.sqlString(secAfter)
				       +",FBeforeSecurityCode="+dbl.sqlString(secBefore)+" where FSecurityCode="+dbl.sqlString(secBefore);
    				dbl.executeSql(sql);
    				//变更交易数据子表
    				sql="update "+pub.yssGetTableName("tb_Data_Subtrade")+" set FSecurityCode="+dbl.sqlString(secAfter)
				       +",FBeforeSecurityCode="+dbl.sqlString(secBefore)+" where FSecurityCode="+dbl.sqlString(secBefore);
    				dbl.executeSql(sql);
        			dbl.closeResultSetFinal(rs);
    				continue;
    			}
    			dbl.closeResultSetFinal(rs);
    			//如果已经存在转换后的证券信息，需要根据时间，接口导入，具有变更特征，将变更后的交易数据删除，再进行重新变更
    			int days=YssFun.dateDiff(this.startDate, this.endDate);
    			for(int j=0;j<=days;j++){//循环日期
    				Date bargainDate=YssFun.addDay(this.startDate, j);
    				//变更交易数据子表
    				sql="select FNum from "+pub.yssGetTableName("tb_Data_Subtrade")
    				  //变更记录
    				   +" where FSecurityCode="+dbl.sqlString(secAfter)+" and FBeforeSecurityCode="+dbl.sqlString(secBefore)
    				   +" and FDS='ZD_JK' and FBargainDate="+dbl.sqlDate(bargainDate);
    				rs=dbl.openResultSet(sql);
    				sql="delete from "+pub.yssGetTableName("tb_Data_Subtrade")+" where fnum=?";
    				pst=dbl.getPreparedStatement(sql);
    				while(rs.next()){
    					pst.setString(1, rs.getString("FNum"));
    					pst.addBatch();
    				}
    				pst.executeBatch();
    				dbl.closeResultSetFinal(rs);
    				dbl.closeStatementFinal(pst);
    				//变更交易数据
    				sql="select FNum from "+pub.yssGetTableName("tb_Data_Trade")
    				  //变更记录
    				   +" where FSecurityCode="+dbl.sqlString(secAfter)+" and FBeforeSecurityCode="+dbl.sqlString(secBefore)
    				   +" AND FBargainDate="+dbl.sqlDate(bargainDate);
    				rs=dbl.openResultSet(sql);
    				sql="delete from "+pub.yssGetTableName("tb_Data_Trade")+" where fnum=?";
    				pst=dbl.getPreparedStatement(sql);
    				while(rs.next()){
    					pst.setString(1, rs.getString("FNum"));
    					pst.addBatch();
    				}
    				pst.executeBatch();
    				dbl.closeResultSetFinal(rs);
    				dbl.closeStatementFinal(pst);    				
    			}
    			//变更证券信息表
				sql="delete from "+pub.yssGetTableName("tb_para_security")+" where FSecurityCode="+dbl.sqlString(secBefore);
				dbl.executeSql(sql);
				//变更债券信息表
				sql="delete from "+pub.yssGetTableName("tb_Para_FixInterest")+" where FSecurityCode="+dbl.sqlString(secBefore);
				dbl.executeSql(sql);
				//变更估值方法连接
				sql="delete from "+pub.yssGetTableName("tb_Para_MTVMethodLink")+" where FLinkCode="+dbl.sqlString(secBefore);
				dbl.executeSql(sql);
				//变更交易数据
				sql="update "+pub.yssGetTableName("tb_Data_Trade")+" set FSecurityCode="+dbl.sqlString(secAfter)
			       +",FBeforeSecurityCode="+dbl.sqlString(secBefore)+" where FSecurityCode="+dbl.sqlString(secBefore);
				dbl.executeSql(sql);
				//变更交易数据子表
				sql="update "+pub.yssGetTableName("tb_Data_Subtrade")+" set FSecurityCode="+dbl.sqlString(secAfter)
			       +",FBeforeSecurityCode="+dbl.sqlString(secBefore)+" where FSecurityCode="+dbl.sqlString(secBefore);
				dbl.executeSql(sql);
        	}
        	conn.commit();
        	conn.setAutoCommit(true);
        	bTrans=false;
        	isSuccess=true;
        	return isSuccess;
    	}catch (Exception e) {
    		 throw new YssException("证券转换出错！"+e, e);
		}finally{
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
		}    	
    }
    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        //String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            } else {
                //sTmpStr = sRowStr;
            }
            
			//---add by songjie 2012.12.27 STORY #2343 QDV4建行2012年3月2日04_A start---//
            //--- edit by songjie 2013.05.20 修改 国泰接口导出 报  生成汇总日志数据出错问题 start---//
            if(sRowStr.indexOf("createSumLog") != -1){//防止报数组越界错误
            //--- edit by songjie 2013.05.20 修改 国泰接口导出 报  生成汇总日志数据出错问题 end---//
				transInfo = sRowStr.split("\t");//获取前台日志数据
				return;
			}
            //---add by songjie 2012.12.27 STORY #2343 QDV4建行2012年3月2日04_A end---//
            
            reqAry = sRowStr.split("\n"); //前台采用"\n"分割 chenyb 20070927

            if(reqAry[0].trim().length() > 0 && YssFun.isDate(reqAry[0]))
                  this.startDate = YssFun.toDate(reqAry[0]);
            if(reqAry[1].trim().length() > 0 && YssFun.isDate(reqAry[1]))
                  this.endDate = YssFun.toDate(reqAry[1]);
            this.cusConfigCode = reqAry[2];
            this.sPorts = reqAry[3];
            this.beanId = reqAry[4];
            this.allData = reqAry[5];
            this.check = reqAry[6]; //20071107   chenyibo    导入的数据是否需要审核
            this.tradeSeat = reqAry[7];
            this.sPrepared = reqAry[8]; //MS00032
            this.sAssetGroupCode = reqAry[9]; //组合群代码 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
            if(reqAry[10].trim().length() > 0 && YssFun.isDate(reqAry[10])){
        	      this.DealDate =YssFun.toDate(reqAry[10]);        	
            }
            this.sCusConfigCode1285=reqAry[12];//add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
        	this.sAssetGroupCode1285=reqAry[13];//add by guolongchao 20110906 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
        	/**add---shashijie 2013-2-28 STORY 3366 增加字段组合群代码,多个组合群代码用逗号分割*/
        	this.AssetGroupCodesWhere = reqAry[14];
        	/**end---shashijie 2013-2-28 STORY 3366*/
        	
        	/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
        	this.FPortCodesWhere = reqAry[15];
        	/**end---shashijie 2013-3-8 STORY 2869*/
        	
        } catch (Exception e) {
            throw new YssException("解析接口请求出错", e);
        }
    }

    /**
     * doOperation
     *
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        String strError = "";
        String reResult = "";
        BaseDaoOperDeal daoOperDeal = null;
        //YssImpData impData = null;
        
        //------ add by wangzuochun 2010.07.07 MS01338    对于多组合情况，在进行接口处理的时，选择两个组合后，系统是一次只是对一个组合进行处理    QDV4赢时胜深圳2010年6月21日01_B 
        String strSql = "";
        String strReturn = "";
        ResultSet rs = null;
        //--------MS01338-------//
        try {
        	//------ modify by wangzuochun 2010.07.07 MS01338    对于多组合情况，在进行接口处理的时，选择两个组合后，系统是一次只是对一个组合进行处理    QDV4赢时胜深圳2010年6月21日01_B 
        	
        	/**add---shashijie 2013-3-1 STORY 3366 增加公共表的获取SQL*/
        	strSql = getSelectFileName(pub.yssGetTableName("Tb_Dao_FileName"),"0");
            
            strSql += " Union All ";
            //公共表SQL
            strSql += getSelectFileName("Tb_Dao_FileName","1");
			/**end---shashijie 2013-3-1 STORY 3366*/
        	
        	rs = dbl.openResultSet(strSql);
        	
        	if (rs.next()) {
				daoOperDeal = (BaseDaoOperDeal)pub.getDataInterfaceCtx().getBean(
							beanId);
				daoOperDeal.setYssPub(pub);
				daoOperDeal.setYssRunStatus(runStatus); // add by qiuxufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加状态显示
				
				if (daoOperDeal instanceof ExpCusInterface) {
					String[] arrPort = this.sPorts.split(",");
					for (int i = 0; i < arrPort.length; i++) {
						
						this.sPorts = arrPort[i];
						daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
										 cusConfigCode, this.check, this.tradeSeat); //20071107   chenyibo   增加导入数据的时候是否要审核

						//如果是导出操作 add liyu
						String str = cusConfigCode + "\t" + sPorts + "\t" + YssFun.formatDate(startDate) + "\t" + YssFun.formatDate(endDate);//11.20 lzp修改 用于动态的导出的参数
						/**add---shashijie 2013-3-1 STORY 3366 增加组合群条件字段*/
						str += "\t" + this.AssetGroupCodesWhere;
						/**end---shashijie 2013-3-1 STORY 3366*/
						
						/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
						str += "\t" + this.FPortCodesWhere;
			        	/**end---shashijie 2013-3-8 STORY 2869*/
			        	
						daoOperDeal.setAllData(str); //增加对预处理中日期，组合的动态处理

						if (this.sAssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
							String prefixTB = pub.getPrefixTB();//取得原有表前缀
							pub.setPrefixTB(this.sAssetGroupCode);//将当前组合群设为表前缀
							daoOperDeal.setYssPub(pub);
							try {
								reResult = daoOperDeal.doInterface();//调用处理方法
								strReturn += reResult + "~@~";
							} catch (Exception e) {
								throw new YssException(e);
							} finally {
								pub.setPrefixTB(prefixTB);//设回表前缀
							}
						}
						else {
							reResult = daoOperDeal.doInterface();
							strReturn += reResult + "~@~";
						}
					}
					reResult = strReturn.substring(0, strReturn.length() - 3);
				}
				else {
					daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
									 cusConfigCode, this.check, this.tradeSeat); //20071107   chenyibo   增加导入数据的时候是否要审核
					if (daoOperDeal instanceof ImpCusInterface) { //如果是导入操作
						daoOperDeal.setAllData(this.allData);
					}
					
					if (this.sAssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
						String prefixTB = pub.getPrefixTB();//取得原有表前缀
						pub.setPrefixTB(this.sAssetGroupCode);//将当前组合群设为表前缀
						daoOperDeal.setYssPub(pub);
						try {
							reResult = daoOperDeal.doInterface();//调用处理方法
						} catch (Exception e) {
							throw new YssException(e);
						} finally {
							pub.setPrefixTB(prefixTB);//设回表前缀
						}
					}
					else {
						reResult = daoOperDeal.doInterface();
					}
				}
        	}
        	else {
        		daoOperDeal = (BaseDaoOperDeal) pub.getDataInterfaceCtx().getBean(
                        beanId);
                    daoOperDeal.setYssPub(pub);
    				daoOperDeal.setYssRunStatus(runStatus); // add by qiuxufeng 20110128 458 QDV4国泰基金2010年12月22日01_A 增加状态显示
                    daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
                                     cusConfigCode, this.check, this.tradeSeat); //20071107   chenyibo   增加导入数据的时候是否要审核
                    if (daoOperDeal instanceof ImpCusInterface) { //如果是导入操作
                        daoOperDeal.setAllData(this.allData);
                    } else if (daoOperDeal instanceof ExpCusInterface) { //如果是导出操作 add liyu
                    	//11.20 lzp修改 用于动态的导出的参数
                        String str = cusConfigCode + "\t" + sPorts + "\t" + YssFun.formatDate(startDate) + "\t" + YssFun.formatDate(endDate);
                        /**add---shashijie 2013-3-1 STORY 3366 增加组合群条件字段*/
						str += "\t" + this.AssetGroupCodesWhere;
						/**end---shashijie 2013-3-1 STORY 3366*/
						
						/**add---shashijie 2013-3-8 STORY 2869 增加字段组合代码,多个组合代码用逗号分割*/
						str += "\t" + this.FPortCodesWhere;
			        	/**end---shashijie 2013-3-8 STORY 2869*/
						
                        daoOperDeal.setAllData(str); //增加对预处理中日期，组合的动态处理
                    }
                    if (this.sAssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
                        String prefixTB = pub.getPrefixTB();//取得原有表前缀
                        pub.setPrefixTB(this.sAssetGroupCode);//将当前组合群设为表前缀
                        daoOperDeal.setYssPub(pub);
                        try {
                            reResult = daoOperDeal.doInterface();//调用处理方法
                        } catch (Exception e) {
                            throw new YssException(e);
                        } finally {
                            pub.setPrefixTB(prefixTB);//设回表前缀
                        }
                    } else {
                        reResult = daoOperDeal.doInterface();
                    }
        	}
            return reResult;
        } catch (Exception e) {
        	reResult = "error";
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        }
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //------------------- MS01338  ----------------------------//

    }

    /**shashijie 2013-3-1 STORY */
	/**shashijie 2013-3-1 STORY 3366 获取sql*/
	private String getSelectFileName(String tableName, String saveType) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select '组合群' as saveType , ";
		} else {
			sql = " select '公共' as saveType , ";
		}
		sql += " a.* From " 
			+ tableName + " a "
			+ " where FCusCfgCode = " + dbl.sqlString(cusConfigCode)
			+ " and FFileNameCls = 'SinglePortFile' and FCheckState = 1";
		return sql;
	}
	

	/**
     * checkRequest
     *
     * @return String
     */
    public String checkRequest(String sType) throws YssException {
        BaseDaoOperDeal daoOperDeal = null;
        String sResult = "";
        try {
            //add by lidaolong  #536 有关国内接口数据处理顺序的变更
            if (sType != null && "clearImpData".equals(sType)){
            	SuccessInPutBean.alInterfaceCode.clear();
            	return "success";
            }//end by lidaolong
            daoOperDeal = (BaseDaoOperDeal) pub.getDataInterfaceCtx().getBean(
                beanId);
            

            if (daoOperDeal instanceof ImpCusInterface) { //如果是导入操作，就通过自定义接口的配置获取文件名
                daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
                                 cusConfigCode, this.tradeSeat);
                daoOperDeal.setYssPub(pub);
                //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
                if(sType!=null && sType.equalsIgnoreCase("filefilter")){//当类型为查询文件筛选条件时
                	DaoCusConfigureBean cusCfgBean =new DaoCusConfigureBean();
                	cusCfgBean.setYssPub(pub);
                	cusCfgBean.setCusCfgCode(cusConfigCode);
                	cusCfgBean.getSetting();
                	return daoOperDeal.getFileFilterStr(cusCfgBean);
                }else{
	                if (this.sAssetGroupCode.trim().length() > 0) { //如果组合群代码不为空即跨组合群 国内：MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群 panjunfang add  2009-06-01
	                    String prefixTB = pub.getPrefixTB();//获取原有表前缀
	                    pub.setPrefixTB(this.sAssetGroupCode);//将当前组合群代码设为表前缀
	                    daoOperDeal.setYssPub(pub);
	                    try {
	                        sResult = daoOperDeal.getImpPathFileName();//调用处理方法
	                    } catch (Exception e) {
	                        throw new YssException(e);
	                    } finally {
	                        pub.setPrefixTB(prefixTB);//设回表前缀
	                    }
	                } else {
	                    daoOperDeal.setYssPub(pub);
	                    sResult = daoOperDeal.getImpPathFileName();
	                }
                }
                //by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
            }
            /*  else if(daoOperDeal instanceof ExpCusInterface){  //如果是导出操作，0927 add liyu
                 daoOperDeal.init(this.startDate, this.endDate, this.sPorts,
                                  cusConfigCode);
                 daoOperDeal.setYssPub(pub);
                 sResult = daoOperDeal.getImpPathFileName();

              }*/
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }

    }

    //--------MS00003 QDV4.1-参数布局散乱不便操作---------//
    //-------2009.10.13 蒋锦 添加 cusConfigCode 字段需要配外部调用-------//
    public String getCusConfigCode() {
        return cusConfigCode;
    }

    public String getPorts() {
        return sPorts;
    }

    public void setCusConfigCode(String cusConfigCode) {
        this.cusConfigCode = cusConfigCode;
    }

    public void setPorts(String sPorts) {
        this.sPorts = sPorts;
    }
    //----------------------------------------------------------------//
    
    public String getFileMerger() throws YssException{
		String mergerFile = "";
		if (this.cusConfigCode==null || this.cusConfigCode.trim().equals("")) {
			return "";
		}
		String strReturn = "";
		String strSql = "";
		ResultSet rs = null;
		try {
			strSql = "SELECT FMerger FROM " + pub.yssGetTableName("Tb_Dao_CusConfig") 
					+" WHERE fcuscfgcode = " + dbl.sqlString(this.cusConfigCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				strReturn = rs.getString("FMerger");
				mergerFile = getMergerFile(strReturn);
			}
		} catch (Exception e) {
			throw new YssException("获取自定义配置接口对应的合并路径报错 !");
		} finally {
            dbl.closeResultSetFinal(rs);
        }
		return mergerFile;
	}

    /**
     * 获取编译后的"合并文件路径"名字 ,目前只针对日期
     * @author shashijie ,2011-3-7
     */
	private String getMergerFile(String strReturn) throws YssException{
		String mergerFile = "";
		if (strReturn!=null && !strReturn.trim().equals("") && !strReturn.trim().equals("null")) {
			int days = YssFun.dateDiff(this.startDate, this.endDate); //日期天数
			for (int i = 0; i <= days; i++) { //这里以后可以扩展成传入什么参数贴什么值
				Date DealDate = YssFun.addDay(this.startDate, i == 0 ? 0 : 1); //日期递增，第一天不变
				mergerFile += getSubFileName(strReturn,YssFun.formatDate(DealDate, "yyyyMMdd")) + ",";
			}
		}
		return mergerFile;
	}

	/**
	 * 获取编译后的"合并文件路径"名字 ,目前只针对日期
	 * TODO <Method comments>
	 * @param strReturn
	 * @author shashijie ,2011-3-7
	 */
	public static String getSubFileName(String strReturn,String tag) throws YssException{
		boolean strSub = false;
		if (strReturn==null || strReturn.trim().equalsIgnoreCase("")) {
			return null;
		}
		int index = strReturn.indexOf("[日期]");/*strReturn.indexOf("[");*/
		if (index != -1) {
			strSub = true;
		}
		/**将字符串中的	"[中的内容全部解析]"	*/
		while (strSub) {
			try {
				//System.out.println(strReturn+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~strReturn");
				int endIdx = strReturn.indexOf("]");
				String tagName = strReturn.substring(index,endIdx+1);
				//System.out.println(tagName+"~~~~~~~~~~~~tagName");
				if (tagName.equals("[日期]")) {
					strReturn = strReturn.substring(0,index) + tag + strReturn.substring(endIdx+1);
					//System.out.println(strReturn+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~strReturn2222222");
				}
				if(strReturn.indexOf("[日期]") == -1/*strReturn.indexOf("[") == -1*/){
					strSub = false;
				} else {
					index = strReturn.indexOf("[");
					endIdx = strReturn.indexOf("]");
				}
			} catch (Exception e) {
				throw new YssException("解析合并路径报错 !");
			}
		}
		return strReturn;
	}

	/**add---shashijie 2013-2-28 返回 assetGroupCodesWhere 的值*/
	public String getAssetGroupCodesWhere() {
		return AssetGroupCodesWhere;
	}

	/**add---shashijie 2013-2-28 传入assetGroupCodesWhere 设置  assetGroupCodesWhere 的值*/
	public void setAssetGroupCodesWhere(String assetGroupCodesWhere) {
		AssetGroupCodesWhere = assetGroupCodesWhere;
	}

	/**add---shashijie 2013-3-1 返回 sAssetGroupCode1285 的值*/
	public String getsAssetGroupCode1285() {
		return sAssetGroupCode1285;
	}

	/**add---shashijie 2013-3-1 传入sAssetGroupCode1285 设置  sAssetGroupCode1285 的值*/
	public void setsAssetGroupCode1285(String sAssetGroupCode1285) {
		this.sAssetGroupCode1285 = sAssetGroupCode1285;
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
