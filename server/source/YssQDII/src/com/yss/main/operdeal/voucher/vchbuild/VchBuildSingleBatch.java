package com.yss.main.operdeal.voucher.vchbuild;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import com.yss.log.SingleLogOper;
import com.yss.main.voucher.VchTplBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;


/**
 * 凭证模块优化
 * 20120522 
 * @author benson
 *
 */
public class VchBuildSingleBatch extends BaseVchBuild {

	private HashMap setCache = null;//凭证模板设置缓存
	private HashMap hmDsFieldType = null;//凭证数据源字段类型信息
	private HashMap hmDsFieldSize=null;//凭证数据源字段长度信息
	//add by songjie 2013.01.17 用于保存插入凭证数据表的sql语句到log4j文件
	private static Logger log = Logger.getLogger("D");
	public VchBuildSingleBatch() {}
	
	
	public void doVchBuild() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsDs = null;
		String[] portAry = null;
		boolean showText = true;
		boolean showEnd = false;

		// ---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		String logInfo = "";
		SingleLogOper logOper = SingleLogOper.getInstance();
		Date logStartTime = null;// 业务子项开始时间
		String portCode = "";// 组合代码
		String vchTplCode = "";// 凭证模板代码
		// ---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
		try {
			portAry = this.getPortCodes().split(",");
			for (int i = 0; i < portAry.length; i++) {

				strSql = " select a.* from " + pub.yssGetTableName("Tb_Vch_VchTpl") + " a" + " left join "
						+ pub.yssGetTableName("Tb_Vch_Attr") + " b" + " on a.FAttrCode = b.FAttrCode"
						+ " where a.FAttrCode in (" + operSql.sqlCodes(this.getVchTypes()) + ")"
						+ " and a.FCheckState = 1" + " and b.FCheckState = 1" + " and a.FMode = 'Single'  "
						+ " and (a.FPortCode=" + dbl.sqlString(portAry[i])
						+ " or (a.FPortCode is null or a.FPortCode='' or a.FPortCode=' '))"
						+ " order by b.FSort, a.FSort";
				rs = dbl.queryByPreparedStatement(strSql);
				while (rs.next()) {
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	            	logInfo = "";
	            	logStartTime = new Date();
	            	vchTplCode = "凭证模版【" + rs.getString("FVchTplCode") +"】";
	            	portCode = portAry[i];
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					
					if (showText) {
						runStatus.appendRunDesc("VchRun", "开始生成组合【" + portAry[i] + "】的凭证... ...\r\n", "      ");
						showText = false;
						showEnd = true;
					}

					if (isExistsSpecialMode(rs.getString("FAttrCode"), portAry[i], "Single", rs
							.getString("fvchtplcode"), rs.getString("FDsCode"), rs.getString("FVchtWay"))) {
						continue;
					}
					
                    //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logInfo += "开始生成【" + rs.getString("FVchTplName") + "】模版的凭证... ...\r\n";
                    
					runStatus.appendRunDesc("VchRun", "开始生成【" + rs.getString("FVchTplName") + "】模版的凭证... ...", "      ");

					doSingleVch(rs.getString("FVchTplCode"), portAry[i], rs.getString("FDsCode"));

					runStatus.appendRunDesc("VchRun", "      【" + rs.getString("FVchTplName") + "】模版凭证已生成\r\n\r\n", "      ");
					
                    //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logInfo += "【" +rs.getString("FVchTplName") +"】模版的凭证已生成\r\n\r\n";
                    
                    if(this.getLogSumCode().trim().length() > 0){
                		//edit by songjie 2012.11.20 添加非空判断
                		if(logOper != null){
                			logOper.setDayFinishIData(this, 8, vchTplCode, pub, false, 
                    			portCode, YssFun.toDate(this.getBeginDate()),
                    			YssFun.toDate(this.getBeginDate()),
                    	        YssFun.toDate(this.getEndDate()),
                    	        logInfo,logStartTime,this.getLogSumCode(),new Date());
                		}
                    }
                    //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
					
				}
				delNoExistsDate();// 最后清除没有凭证分录的凭证主表数据。
				if (showEnd) {
					runStatus.appendRunDesc("VchRun", "生成组合【" + portAry[i] + "】的单行取数凭证完成！");
					showText = true;
					showEnd = false;
				}
			}
		} catch (Exception e) {
			//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			try{
				if(this.getLogSumCode().trim().length() > 0){
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 8,vchTplCode, pub, true, portCode, 
							YssFun.toDate(this.getBeginDate()),YssFun.toDate(this.getBeginDate()),
							YssFun.toDate(this.getEndDate()),
							//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
							(logInfo + " \r\n 组合【" + portCode + "】的单行取数凭证生成失败  \r\n " + //处理日志信息 除去特殊符号
                			e.getMessage()).replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
                			//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
                			logStartTime,this.getLogSumCode(),new Date());
	        		}
				}
				
				runStatus.appendRunDesc("VchRun", "生成组合【" + portCode + "】的单行取数模版凭证失败!... ...");
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
			//edit by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
				throw new YssException(e.getMessage(),e);
			}
			//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

    
	/**
	 * 最后清除没有凭证分录的凭证主表数据。
	 * @throws YssException
	 */
	private void delNoExistsDate()throws YssException{
		
		StringBuffer delBuf = new StringBuffer();
		try{
			/**
			 * 通过组合+日期关联,查看凭证流水号匹配情况来找出没有凭证分录的凭证主表数据
			 */
			delBuf.append(" delete from ").append(pub.yssGetTableName("tb_vch_data")).append(" a ");
			delBuf.append("  where not exists ( select b.fvchnum from ");
			delBuf.append("  (select b1.fvchnum from ").append(pub.yssGetTableName("tb_vch_data")).append("  b1 ");
			delBuf.append(" join  ").append(pub.yssGetTableName("tb_vch_dataentity")).append(" b2 on b1.fvchnum=b2.fvchnum ");
			delBuf.append(" where b1.fvchdate between ").append(dbl.sqlDate(this.getBeginDate())).append(" and ").append(dbl.sqlDate(this.getEndDate()));
			delBuf.append(" and b1.fportcode in(").append(operSql.sqlCodes(this.getPortCodes())).append(") )b where a.fvchnum = b.fvchnum )");
			delBuf.append(" and a.fportcode in(").append(operSql.sqlCodes(this.getPortCodes())).append(")");
			delBuf.append("and a.fvchdate between ").append(dbl.sqlDate(this.getBeginDate())).append(" and ").append(dbl.sqlDate(this.getEndDate()));
			
			dbl.executeSql(delBuf.toString());
		}catch(Exception e){
			throw new YssException();
		}
	}
	
	 /**
     *  jiangshichao 20120507
     *  批量生成单行取数模式的凭证数据
     *   1. 使用凭证数据源查询数据，将结果保存到临时表中。
     *   2. 根据凭证模板配置，动态拼接SQL语句，批量插入到凭证主表和凭证子表。
     *   3. 处理轧差分录
     *  @param sVchTplCode  凭证模板
     *  @param portCode     组合代码
     *  @param sDsCode      数据源代码
     * @throws YssException
     */
    protected void doSingleVch(String sVchTplCode,String portCode,String sDsCode) throws YssException{
    	
    	Connection conn = dbl.loadConnection();
    	boolean bTrans = false;//事务状态标识
    	boolean bCreState = false;//凭证生成成功标识
    	String tabName ="DsDataTranTmp_"+pub.getUserCode();//定义临时表【DsDataTranTmp_用户代码】。所以不允许同一个用户同时生成凭证。
    	String createTime ;
    	try{
    		
    		/**Start 20130927 deleted by liubo.Bug #79927.QDV4赢时胜(上海开发)2013年9月23日01*/
//    		 conn.setAutoCommit(false);
//             bTrans = true;
    		/**End 20130927 deleted by liubo.Bug #79927.QDV4赢时胜(上海开发)2013年9月23日01*/
            
             //1. 使用凭证数据源查询数据，并保存到临时表。
             //如果数据源查询不到数据，就不再执行下去。
             if(CopyDsDataToTranTmp(portCode,sDsCode,sVchTplCode)){
            	 
            	 /**Start 20130927 deleted by liubo.Bug #79927.QDV4赢时胜(上海开发)2013年9月23日01
            	  * 根据测试，貌似在DB2的环境下，使用conn.setAutoCommit(true)，conn.Commit提交事件时
            	  * 会将所有的有dbl.loadconnetion引申出来的对象，包括Statement，ResultSet都关闭掉，原因不详
            	  * 因此在这段话中做个判断*/
            	 conn.setAutoCommit(false);
            	 
            	 createTime = YssFun.formatDatetime(new java.util.Date()); //统一凭证创建时间
                 //2.根据凭证模板配置，动态拼接SQL语句，批量插入到凭证主表和凭证子表。
                 bCreState = CreVchBatchMode(tabName,sVchTplCode,portCode,createTime);//返回凭证生成状态
                 conn.commit();
                 if (dbl.dbType == YssCons.DB_DB2)
                 {
                	 bTrans = false;
                 }
                 else
                 {
                	 bTrans = true;
                 }
                 conn.setAutoCommit(bTrans);
                 /**End 20130927 deleted by liubo.Bug #79927.QDV4赢时胜(上海开发)2013年9月23日01*/
        		
                 //3. 轧差处理
                 ProcessTail(sVchTplCode,portCode,tabName,createTime);
             }else{
            	 bCreState = false;
             }
             if(bCreState){
             	runStatus.appendRunDesc("VchRun", "生成凭证成功!"); 
             }else{
             	runStatus.appendRunDesc("VchRun", "当日无业务!"); 
             }
    		
    	}catch(Exception e ){
    		throw new YssException(e.getMessage());
    	}finally{
    		if(setCache != null){
    			setCache.clear();
        		setCache = null;
    		}
    		if(hmDsFieldType != null && hmDsFieldSize != null){
    			hmDsFieldType.clear();
        		hmDsFieldSize.clear();
    		}
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
   
    
    private void ProcessTail(String sVchTplCode,String portCode,String tabName,String createTime) throws YssException{
    	
    	//edit by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B 添加 rs2
    	ResultSet rs = null,rs1=null,rs2 = null;
    	StringBuffer queryBuf = new StringBuffer();
    	StringBuffer sEnCuryCode = new StringBuffer();//FENCURYCODE
    	String sSrcCodeCol = "";//原币币种字段
    	String sDateCol = "";//日期字段
    	
    	StringBuffer FieldBuf =  new StringBuffer();
    	StringBuffer joinBuf =  new StringBuffer();
    	StringBuffer whereBuf = new StringBuffer();
    	StringBuffer insertBuf = new StringBuffer();
    	StringBuffer tailSql = new StringBuffer();
    	//--- add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B start---//
    	StringBuffer checkBuf = new StringBuffer();
    	String sSetCode = "";
    	//--- add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B end---//
    	int count = 0;//add by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B
    	try{
    		//add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
    		sSetCode = this.formatNum(this.getBookSet(portCode), "000");//获取套帐代码
    		
    		//查询轧差分录设置
    		queryBuf.append(" select * from ").append(pub.yssGetTableName("tb_vch_entity"));
    		queryBuf.append(" where fvchtplcode=").append(dbl.sqlString(sVchTplCode)).append(" and FCheckState = 1 and fcalcway<>'Common'");
    		//--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
			//修改结果集类型
    		rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B end---//
    		if(!rs.next()){
    			//没有轧差分录，就直接返回
    			return;
    		}
    		
    		//--- add by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
    		//判断凭证模板是否有多个钆差本位币 的分录，有就提示 只能设置一个
    		rs.beforeFirst();	
    		while(rs.next()){
    			if(rs.getString("FCalcWay").equals("NettingSet") || 
    	    	   rs.getString("FCalcWay").equals("NettingAndSet")){
    	    		count ++;
    	    	}
    		}
    		
    		if(count >= 2){
    			throw new YssException("凭证模板【" + sVchTplCode + "】有多个钆差本位币分录，只能设置一个钆差本位币分录，请重新设置凭证模板！");
    		}
    		
    		rs.beforeFirst();	
    		//--- add by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//

    		// 获取原币币种字段名和日期字段名
    		queryBuf.setLength(0);
    		queryBuf.append(" select * from ").append(pub.yssGetTableName("Tb_Vch_VchTpl"));
    		queryBuf.append(" where FCheckState = 1 and FVchTplCode = ").append(dbl.sqlString(sVchTplCode));
    		queryBuf.append(" and ((FPortCode is null or FPortCode='' or FPortCode=' ')or FPortCode=").append(dbl.sqlString(portCode)).append(")");
                
            rs1 = dbl.openResultSet(queryBuf.toString());
            if(rs1.next()){
                	sSrcCodeCol = rs1.getString("FSRCCURY");
                	sDateCol = rs1.getString("FDATEFIELD");
                }
           dbl.closeResultSetFinal(rs1);
           queryBuf.setLength(0);

   		   rs.beforeFirst();	
    	   while(rs.next()){
    		   //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
    		   //根据每个分录的钆差类型 拼接 钆差金额对应的sql
    		   //~~~~~~~~~ 有轧差分录 ~~~~~~~~~~~~~~~~~~~~~~~
    		   //1. 拼接出轧差金额。
    		   tailSql = getDealTailSql(sVchTplCode,portCode,rs.getString("FCalcWay"));
       		   //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B end---//
    		   whereBuf.setLength(0);
               sEnCuryCode.setLength(0);
               insertBuf.setLength(0);
               FieldBuf.setLength(0);
               joinBuf.setLength(0);
               //add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
               checkBuf.setLength(0);
               fetchValforCache(rs.getString("FENTITYCODE"),FieldBuf,joinBuf);
               
               if(rs.getString("FENCURYCODE")==null||rs.getString("FENCURYCODE").trim().length()==0||rs.getString("FENCURYCODE").equalsIgnoreCase("null")){
             		sEnCuryCode.append(sSrcCodeCol);
               }else{
             		sEnCuryCode.append(rs.getString("FENCURYCODE"));
               }
               
               if(null!=rs.getString("fallow")||rs.getString("fallow").trim().length()>0){
              	   if(rs.getString("fallow").equalsIgnoreCase("AisZero")){
              		 //数量允许为0，原币或本位币任一不为0，保存原来分录
              		 //当为轧差类型时,才放入,使之后的轧差计算得以进行。当为普通类型时，不保留分录信息
              	      whereBuf.append(" where (entity.money<>0 or entity.fbtail<>0) and  entity.amount=0 ");
              		   
              		   
              	   }else if(rs.getString("fallow").equalsIgnoreCase("MisZero")){
              		 //金额(原币与本位币)允许为0,数量不为0，保存原来分录
              		   whereBuf.append(" where (entity.money=0 or entity.fbtail=0) and  entity.amount<>0 ");
              	   }else if(rs.getString("fallow").equalsIgnoreCase("AMisZero")){
              		 //数量与金额(原币与本位币)都允许为0，直接保存原有分录  
              		   whereBuf.append("");
              	   }else {
              		   whereBuf.append(" where entity.money<>0 or entity.fbtail<>0 or entity.amount<>0 ");
              	   }
              	    
      			}else{
      				whereBuf.append(" where entity.money<>0 or entity.fbtail<>0 or entity.amount<>0 ");
      			}
               
               //--- add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B start---//
               checkBuf.append(" select FVCHNUM,").append(dbl.sqlString(rs.getString("FENTITYCODE"))).append(",FCURYRATE,fsetcode,").append(dbl.sqlString(rs.getString("FDCWAY"))).append(",1,");
               checkBuf.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(createTime));
               checkBuf.append(rs.getString("fcalcway").equalsIgnoreCase("NettingSet")?",money as ftail , ":",entity.ftail,");
               checkBuf.append(" entity.fbtail,amount,assistant,subject,resumename,");
               checkBuf.append(" case when ").append(rs.getString("FPRICEFIELD")).append("=null then 0 else ").append(rs.getString("FPRICEFIELD")).append(" end as FPRICEFIELD from ");
               checkBuf.append(" (select valrate.fbaserate as FCURYRATE,c.ftail,c.fbtail,").append(FieldBuf.toString()).append(" ds.* ").append(" from ");
               checkBuf.append(tailSql.toString());
               checkBuf.append(" left join ");
               checkBuf.append("(").append(tabName).append(")ds").append(" on ds.fvchnum=c.fvchnum");
               checkBuf.append(" left join ");
               checkBuf.append(" (select fvaldate,fcurycode,fbaserate/fportrate as fbaserate from ").append(pub.yssGetTableName("tb_data_valrate"));
               checkBuf.append(" where fcheckstate=1 and fportcode=").append(dbl.sqlString(portCode)).append(")valrate  ");
               //insertBuf.append(" on ds.").append(sDateCol).append("= valrate.fvaldate and ds.").append(sEnCuryCode.toString()).append("=valrate.fcurycode");
               //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
               if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
            	   //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
            	   checkBuf.append(" on ds.").append(sDateCol).append("= valrate.fvaldate and ds.").append(sEnCuryCode.toString()).append("=valrate.fcurycode");
               }else{
            	   checkBuf.append(" on to_date(ds.").append(sDateCol).append(",'yyyy-MM-dd')= valrate.fvaldate and ds.").append(sEnCuryCode.toString()).append("=valrate.fcurycode");
               }
               checkBuf.append(joinBuf.toString()).append(")entity").append(whereBuf.toString());
               rs2 = dbl.openResultSet(checkBuf.toString());
               while(rs2.next()){
            	   if(rs2.getObject("Subject") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的科目，请检查凭证模板【" + 
            				   sVchTplCode + "】的科目设置 以及 科目设置相关凭证字典！");
            	   }
            	   if(rs2.getObject("Subject") != null && rs2.getObject("FCURYRATE") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的货币汇率，请检查套帐【" + 
            				   sSetCode + "】科目【" + rs2.getString("subject") + "】是否设置！");
            	   }
            	   if(rs2.getObject("ResumeName") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的摘要，请检查凭证模板【" + 
            				   sVchTplCode + "】的摘要设置 以及摘要设置相关凭证字典！");
            	   }
               }
               dbl.closeResultSetFinal(rs2);
               //--- add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B end---//
               
               insertBuf.append(" insert into ").append(pub.yssGetTableName("tb_vch_dataentity"));
               insertBuf.append("(FVCHNUM,FENTITYNUM,FCURYRATE,FBOOKSETCODE,FDCWAY,FCHECKSTATE,FCREATOR,FCREATETIME,");
               insertBuf.append(" FBAL,FSETBAL,FAMOUNT,FASSISTANT,FSUBJECTCODE,FRESUME,FPRICE)");
               insertBuf.append(" select FVCHNUM,").append(dbl.sqlString(rs.getString("FENTITYCODE"))).append(",FCURYRATE,fsetcode,");
               insertBuf.append(dbl.sqlString(rs.getString("FDCWAY"))).append(",1,");
               insertBuf.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(createTime));
               //判断轧差方式，进行区分处理
               insertBuf.append(rs.getString("fcalcway").equalsIgnoreCase("NettingSet")?",money as ftail , ":",entity.ftail,");
               insertBuf.append(" entity.fbtail,amount,assistant,subject,resumename,");
               insertBuf.append(" case when ").append(rs.getString("FPRICEFIELD")).append("=null then 0 else ").append(rs.getString("FPRICEFIELD"));
               insertBuf.append(" end as FPRICEFIELD from ");
               insertBuf.append(" (select valrate.fbaserate as FCURYRATE,c.ftail,c.fbtail,").append(FieldBuf.toString()).append(" ds.* ").append(" from ");
               insertBuf.append(tailSql.toString());
               insertBuf.append(" left join ");
               //insertBuf.append("(").append(tabName).append(")ds").append(" on ds.fvchnum=c.fvchnum");9i不支持这种写法
               insertBuf.append(tabName).append(" ds").append(" on ds.fvchnum=c.fvchnum");
               insertBuf.append(" left join ");
               //估值汇率表
               insertBuf.append(" (select fvaldate,fcurycode,fbaserate/fportrate as fbaserate from ").append(pub.yssGetTableName("tb_data_valrate"));
               insertBuf.append(" where fcheckstate=1 and fportcode=").append(dbl.sqlString(portCode)).append(")valrate  ");
               //判断日期字段值的数据类型，是日期还是varchar2类型
               //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
               if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
            	   //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 end---//
            	   insertBuf.append(" on ds.").append(sDateCol).append("= valrate.fvaldate and ds.").append(sEnCuryCode.toString());
            	   insertBuf.append("=valrate.fcurycode");
               }else{
            	   insertBuf.append(" on to_date(ds.").append(sDateCol).append(",'yyyy-MM-dd')= valrate.fvaldate and ds.").append(sEnCuryCode.toString());
            	   insertBuf.append("=valrate.fcurycode");
               }
               insertBuf.append(joinBuf.toString()).append(")entity").append(whereBuf.toString());
               
               //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
        	   //add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
        	   log.info(insertBuf.toString());
               dbl.executeSql(insertBuf.toString());  
               //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B end---//
    	   }	

    	   
    	}catch(Exception e){
    		//edit by songjie 2013.03.29 处理多条钆差分录报错问题
    		throw new YssException("批量轧差出错......",e);
    	}finally{
    		 whereBuf.setLength(0);
             sEnCuryCode.setLength(0);
             insertBuf.setLength(0);
             FieldBuf.setLength(0);
             joinBuf.setLength(0);
             //edit by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
             dbl.closeResultSetFinal(rs,rs1,rs2);
    	}
    }

    /**
     * 根据轧差分录设置,拼接轧差分录处理SQL
     * @param sVchTplCode
     * @param sPortCode
     * @param calcWay 钆差方式
     * @return
     * @throws YssException
     */
    //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
    //添加参数 calcWay 钆差方式
    private StringBuffer getDealTailSql(String sVchTplCode,String sPortCode,String calcWay)throws YssException{
    //--- edit by songjie 2013.07.26 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B end---//	
    	String sDcWay = "";//借贷方向
    	String sCalWay = "";//轧差方式
    	StringBuffer queryColBuf = new StringBuffer();
    	StringBuffer queryBuf = new StringBuffer();
    	StringBuffer sqlBuf = new StringBuffer();
    	ResultSet rs = null;
    	try{
    		queryBuf.append(" select * from ").append(pub.yssGetTableName("tb_vch_entity"));
    		queryBuf.append(" where fvchtplcode=").append(dbl.sqlString(sVchTplCode))
    		//--- edit by songjie 2013.07.02 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B start---//
    		//根据明细分录的钆差方式 获取数据  拼接钆差金额对应sql
    		.append(" and fcalcway = " + dbl.sqlString(calcWay) + " and FCheckState = 1  ");
    		//--- edit by songjie 2013.07.02 BUG 8441 QDV4赢时胜(上海开发)2013年6月26日03_B end---//
    		rs = dbl.queryByPreparedStatement(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    		while(rs.next()){
    			sDcWay = rs.getString("fdcway");
    			sCalWay = rs.getString("fcalcway");
    			//计算轧差分录金额
    			//~~~~~~~~~~~~ 主表拼接   start ~~~~~~~~~~~~~~~~~~~~~~
    			queryBuf.setLength(0);
    			queryBuf.append(" (select nvl(c1.fvchnum,c2.fvchnum) as fvchnum,");
    			/**
    			 * sDcWay = 0    轧差科目在借方 
        		 *   主表为贷方汇总；
        		 *   
        		 * sDcWay = 1  轧差科目在贷方
        		 *   主表为借方汇总；
    			 */
    			queryBuf.append(sDcWay.equalsIgnoreCase("0")?"nvl(c1.fdbal,0) as fdbal,nvl(c1.fdsetbal,0) as fdsetbal,":"nvl(c1.fjbal,0) as fjbal,nvl(c1.fjsetbal,0) as fjsetbal, ");
        		queryBuf.append(sDcWay.equalsIgnoreCase("0")?"nvl(c2.fjbal,0) as fjbal,nvl(c2.fjsetbal,0)as fjsetbal":"nvl(c2.fdbal,0) as fdbal,nvl(c2.fdsetbal,0)as fdsetbal");
        		queryBuf.append(" from (select fvchnum,");
    			queryBuf.append(sDcWay.equalsIgnoreCase("0")?"sum(fbal) as fdbal,sum(fsetbal) as fdsetbal":"sum(fbal) as fjbal,sum(fsetbal) as fjsetbal ");
    			queryBuf.append(" from ").append(pub.yssGetTableName("tb_vch_dataentity")).append(" a where "); 
    			queryBuf.append(" exists(select fvchnum from ").append(pub.yssGetTableName("tb_vch_data")).append(" b ");
    			queryBuf.append(" where b.fportcode =").append(dbl.sqlString(sPortCode)).append(" and fvchdate between ");
    			queryBuf.append(dbl.sqlDate(this.getBeginDate())).append(" and ").append(dbl.sqlDate(this.getEndDate()));
    			queryBuf.append(" and fvchtplcode = ").append(dbl.sqlString(sVchTplCode)).append(" and a.fvchnum= b.fvchnum) ");
    			queryBuf.append(sDcWay.equalsIgnoreCase("0")?" and fdcway='1' ":" and fdcway='0' ");
    			queryBuf.append(" group by fvchnum)c1 ");
    			//~~~~~~~~~~~~ 主表拼接   end ~~~~~~~~~~~~~~~~~~~~~~
    			queryBuf.append(" full join ");//特殊的凭证：都在借方，但是其中一条分录为负，一条为借
    			//~~~~~~~~~~~~ 次表 拼接   start ~~~~~~~~~~~~~~~~~~~~~~
    			queryBuf.append(" (select fvchnum,");
    			/**
    			 * sDcWay = 0    轧差科目在借方 
        		 *   次表为贷方汇总；
        		 *   
        		 * sDcWay = 1  轧差科目在贷方
        		 *   次表为借方汇总；
    			 */
    			queryBuf.append(sDcWay.equalsIgnoreCase("0")?"sum(fbal) as fjbal,sum(fsetbal) as fjsetbal ":"sum(fbal) as fdbal,sum(fsetbal) as fdsetbal");
    			queryBuf.append(" from ").append(pub.yssGetTableName("tb_vch_dataentity")).append(" a where "); 
    			queryBuf.append(" exists(select fvchnum from ").append(pub.yssGetTableName("tb_vch_data")).append(" b where ");
    			queryBuf.append("  b.fportcode =").append(dbl.sqlString(sPortCode)).append(" and fvchdate between ").append(dbl.sqlDate(this.getBeginDate()));
    			queryBuf.append(" and ").append(dbl.sqlDate(this.getEndDate()));
    			queryBuf.append(" and fvchtplcode = ").append(dbl.sqlString(sVchTplCode)).append(" and a.fvchnum= b.fvchnum)");
    			queryBuf.append(sDcWay.equalsIgnoreCase("0")?" and fdcway='0' ":" and fdcway='1' ");
    			queryBuf.append(" group by fvchnum)c2 ");
    			//~~~~~~~~~~~~ 次表 拼接   end ~~~~~~~~~~~~~~~~~~~~~~
        		queryBuf.append(" on c1.fvchnum = c2.fvchnum )c3 ");
        		
        		/**   
        		 * 根据轧差方式  拼接更新字段
        		 *    fcalcway = Netting        轧差本币和原币     
        		 *    fcalcway = NettingAndSet 	轧差本位币，并将本位币金额赋值给原币
        		 *    fcalcway = NettingSet   	轧差本币
        		 *    
        		 *    
        		 * sDcWay = 0    轧差科目在借方 
        		 *   轧差金额 = 贷方方汇总金额 - 借方汇总金额(借方汇总的是除轧差分录的金额 )
        		 *   
        		 * sDcWay = 1  轧差科目在贷方
        		 *   轧差金额 = 借方汇总金额 - 贷方汇总金额(贷方汇总的是除轧差分录的金额)
        		 */
        		if(sCalWay.equalsIgnoreCase("NettingSet")){
        			//这里不需要原币轧差，给个默认值0 ，这样处理是为了对SQL语句的简化处理，在这里统计字段数，那么在最外层就不再去通过轧差类型拼接查询字段。
        			queryColBuf.append(" (select c3.fvchnum,").append(sDcWay.equalsIgnoreCase("0")?" 0 as ftail,nvl(c3.fdsetbal - c3.fjsetbal,0) as fbtail":" 0 as ftail,nvl(c3.fjsetbal - c3.fdsetbal,0) as fbtail ").append(" from ");
        		}else if(sCalWay.equalsIgnoreCase("NettingAndSet")){
        			//fcalcway = NettingAndSet 	轧差本位币，并将本位币金额赋值给原币
    	            queryColBuf.append(" (select c3.fvchnum,");
    	            queryColBuf.append(sDcWay.equalsIgnoreCase("0")?" nvl(c3.fdsetbal - c3.fjsetbal,0) as ftail,nvl(c3.fdsetbal - c3.fjsetbal,0) as fbtail":" nvl(c3.fjsetbal - c3.fdsetbal,0) as ftail,nvl(c3.fjsetbal - c3.fdsetbal,0) as fbtail ");
    	            queryColBuf.append(" from ");
        			
        		}else if(sCalWay.equalsIgnoreCase("Netting")){
    	            queryColBuf.append(" (select c3.fvchnum,");
    	            queryColBuf.append(sDcWay.equalsIgnoreCase("0")?" nvl(c3.fdbal - c3.fjbal,0) as ftail,nvl(c3.fdsetbal - c3.fjsetbal,0) as fbtail":" nvl(c3.fjbal - c3.fdbal,0) as ftail,nvl(c3.fjsetbal - c3.fdsetbal,0) as fbtail ");
    	            queryColBuf.append(" from ");
        		}
    		}
    		return sqlBuf.append(queryColBuf.toString()).append(queryBuf.toString()).append(")c ");
    	}catch(Exception e ){
    		//edit by songjie 2013.03.29 处理多条钆差数据出错问题
    		throw new YssException("拼接钆差数据出错！",e);
    	}finally{
    		queryColBuf.setLength(0);
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    
    /**
     * 根据凭证模板设置，生成凭证主表批量处理SQL
     * 根据凭证分录设置，生成凭证子表数据非轧差分录批量处理SQL
     * @param tabName
     * @param sVchTplCode
     * @param portCode
     * @param createTime
     * @return
     * @throws YssException
     */
    private boolean CreVchBatchMode(String tabName,String sVchTplCode,String portCode,String createTime) throws YssException{

    	StringBuffer insertBuf = null;
    	//指定hashMap长度。采用默认16位，会发生设置数据丢失问题。
    	setCache = new HashMap(300);//凭证设置缓存(摘要设置，科目代码设置，原币/本币/数量设置，条件设置 )
    	boolean bCreState = false;//凭证是否生成成功标识
    	
    	try{
             //初始化凭证设置缓存数据
             //【key:分录号+设置类别   】
             initSetCache(sVchTplCode,setCache , portCode); //modify huangqirong 2013-04-18 bug #7519 增加组合参数
             /*
              * 如果凭证科目、辅助核算项、摘要设置的凭证字段不存在，那么生成的凭证是一张毫无意义的凭证
              * 所以这里添加检查凭证科目、摘要、辅助核算项的用到的凭证字典设置
              */
             //delete by songjie 2013.01.21 如果未设置凭证字典  则只给出文本框提示信息，而不抛出异常。
             //ChkDictSet(sVchTplCode,tabName);
           //3. 生成凭证数据
             bCreState = builderDataBatchSql(sVchTplCode,portCode,tabName,createTime);

    		return bCreState;//返回
    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
    }
    
    /***********************************************
     * 检查凭证分录设置的凭证字典是否已经设置
     * @param sVchTplCode
     * @param tabName
     * @throws YssException
     */
    private void ChkDictSet(String sVchTplCode,String tabName)throws YssException{
    	
    	StringBuffer sqlBuf = new StringBuffer();
    	StringBuffer chkBuf = new StringBuffer();
    	StringBuffer sRunStatus = new StringBuffer();
    	StringBuffer msgInfo = new StringBuffer();
    	ResultSet rs = null,rs1 = null;
    	String sVchTplName = "";
    	try{

    		/*
    		 *  查询出凭证模板的有凭证字典的凭证分录设置
    		 */
    		sqlBuf.append(" select tpl.fvchtplname,set1.* from ");
    		//凭证科目设置
    		sqlBuf.append(" (select fvchtplcode,'sub' as ftype,fsubjectfield as ffield,fsubjectdict as fdict,fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entitysubject"));
    		sqlBuf.append(" where fcheckstate = 1 and fvaluetype=0 and fsubjectdict is not null and fsubjectdict <> 'null' and fsubjectdict <>' '");
    		sqlBuf.append(" and fvchtplcode =").append(dbl.sqlString(sVchTplCode));
    		/**add---huhuichao 2013-10-12 BUG  81016 凭证优化执行调度方案提示*/
    		sqlBuf.append(" and fentitycode in (select distinct fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entity")).append(" where fvchtplcode = ");
    		sqlBuf.append(dbl.sqlString(sVchTplCode)).append(" )");
			/**end---huhuichao 2013-10-12 BUG  81016*/
    		sqlBuf.append(" union all ");
    		//凭证摘要设置
    		sqlBuf.append(" select fvchtplcode,'resume' as ftype,fresumefield as ffield,fresumedict as fdict,fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entityresume"));
    		sqlBuf.append(" where fcheckstate = 1 and fvaluetype=0 and fresumedict is not null and fresumedict <> 'null' and fresumedict <>' '");
    		sqlBuf.append(" and fvchtplcode =").append(dbl.sqlString(sVchTplCode));
    		/**add---huhuichao 2013-10-12 BUG  81016 凭证优化执行调度方案提示*/
    		sqlBuf.append(" and fentitycode in (select distinct fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entity")).append(" where fvchtplcode = ");
    		sqlBuf.append(dbl.sqlString(sVchTplCode)).append(" )");
			/**end---huhuichao 2013-10-12 BUG  81016*/
    		sqlBuf.append(" union all ");
    		//辅助核算项设置
    		sqlBuf.append(" select fvchtplcode,'assistant' as ftype, fassistantfield as ffield,fassistantdict as fdict,fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_assistant"));
    		sqlBuf.append(" where fcheckstate = 1 and fvaluetype=0 and fassistantdict is not null and fassistantdict <> 'null' and fassistantdict <>' '");
    		sqlBuf.append(" and fvchtplcode =").append(dbl.sqlString(sVchTplCode));
    		/**add---huhuichao 2013-10-12 BUG  81016 凭证优化执行调度方案提示*/
    		sqlBuf.append(" and fentitycode in (select distinct fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entity")).append(" where fvchtplcode = ");
    		sqlBuf.append(dbl.sqlString(sVchTplCode)).append(" )").append(") set1 ");
			/**end---huhuichao 2013-10-12 BUG  81016*/

    		sqlBuf.append(" left join ");
    		//凭证模板
    		sqlBuf.append("(select fvchtplcode,fvchtplname from ").append(pub.yssGetTableName("tb_vch_vchtpl")).append("  where fcheckstate=1)tpl");
    		sqlBuf.append(" on tpl.fvchtplcode = set1.fvchtplcode");
     		rs = dbl.openResultSet(sqlBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
             while(rs.next()){
            	 
            	 if(rs.isFirst()){
            		 sVchTplName = rs.getString("fvchtplname");//保存凭证模板名称，用于提示信息。
            	 }
            	 
            	/*
            	 *  循环凭证分录设置，
            	 *  检查临时表中需要转换的字段在凭证字典中是否已有了对应的转换关系
            	 */
            	chkBuf.append(" select ds.").append(rs.getString("ffield")).append(" as ffield ,dict.fcnvconent,dict1.fdictname from ");
            	//凭证数据源结果集临时表
            	chkBuf.append(" (select * from ").append(tabName).append(" )ds ");
            	chkBuf.append(" left join ");
            	//凭证字典表
            	chkBuf.append(" ( select fdictcode,findcode, fcnvconent,fdictname from ").append(pub.yssGetTableName("tb_vch_dict")).append(" where fcheckstate=1 ");
            	chkBuf.append(" and fdictcode = ").append(dbl.sqlString(rs.getString("fdict"))).append(" ) dict");
            	chkBuf.append(" on ds.").append(rs.getString("ffield")).append(" = dict.findcode");
            	chkBuf.append(" left join ");
            	//关联凭证字典名称
            	chkBuf.append(" ( select fdictname from ").append(pub.yssGetTableName("tb_vch_dict")).append(" where fcheckstate=1 ");
            	chkBuf.append(" and fdictcode = ").append(dbl.sqlString(rs.getString("fdict"))).append(" ) dict1");
            	chkBuf.append(" on 1=1");
            	chkBuf.append(" group by ds.").append(rs.getString("ffield")).append(", dict.fcnvconent,dict1.fdictname ");
            	chkBuf.append(" having dict.fcnvconent is null");
            	rs1 = dbl.openResultSet(chkBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
            	while(rs1.next()){
            		
            		if(rs1.isFirst()){
            			msgInfo.append("\t分录号【	").append(rs.getString("fentitycode")).append("】的");
            			if(rs.getString("ftype").equalsIgnoreCase("sub")){
            				msgInfo.append(" 【科目】设置有问题：\r\n");
            			}else if(rs.getString("ftype").equalsIgnoreCase("resume")){
            				msgInfo.append(" 【摘要】设置有问题：\r\n");
            			}else if(rs.getString("ftype").equalsIgnoreCase("assistant")){
            				msgInfo.append(" 【辅助核算项】设置有问题：\r\n");
            			}
            		}
            		
            		msgInfo.append("\t\t未在凭证字典【").append(rs.getString("fdict")).append("-").append(rs1.getString("fdictname")).append("】中设置【 ");
            		msgInfo.append(rs1.getString("ffield")).append(" 】的对应关系 ！\r\n");
            	}
            	chkBuf.setLength(0);
            	dbl.closeResultSetFinal(rs1);
             }
             
             
             if(msgInfo.length()>0){
            	 sRunStatus.append("凭证模板【").append(sVchTplCode).append(" - ").append(sVchTplName).append("】\r\n").append(msgInfo.toString()).append("\r\n");

            	 throw new YssException(sRunStatus.toString());
             }
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
         	chkBuf.setLength(0);
         	sqlBuf.setLength(0);
         	if(rs != null){
         		dbl.closeResultSetFinal(rs);
         		rs = null;
         	}
         	if(rs1 != null){
         		dbl.closeResultSetFinal(rs1);
         		rs1 = null;
         	}
    	}
    }
    
    /**
     * 检查凭证分录是否存在凭证字典设置
     * @param sVchTplCode
     * @param sEntityNum
     * @return 存在 ：true; 不存在： false;
     * @throws YssException
     */
    private boolean ExistDictSet(String sVchTplCode,String sEntityNum) throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer sqlBuf = new StringBuffer();
    	boolean existFlag = false;
    	try{
    		rs = dbl.openResultSet(sqlBuf.toString());
    		if(rs.next()){
    			existFlag = true;
    		}
    		
    		return existFlag;
    	}catch(Exception e ){
    		throw new YssException();
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		sqlBuf.setLength(0);
    	}
    }
    
    
    /**
     * 创建临时表，来存储凭证数据源查询出来的结果集数据.
     * 将凭证数据存储到临时表中，用来保证主表数据的固定顺序
     * @throws YssException
     */
    private boolean CopyDsDataToTranTmp(String portCode,String sDsCode,String sVchTplCode)throws YssException{
    	
    	StringBuffer creTabBuf= new StringBuffer();//建表Sql语句
    	StringBuffer insertBuf = new StringBuffer();
    	StringBuffer insertBuf1 = new StringBuffer();
        String tabName = "DsDataTranTmp_"+pub.getUserCode();
        String strSql; 
        ResultSet rsDs = null;
        
        PreparedStatement pst = null;
        HashMap<String,String> fieldMap = null;
        StringBuffer keyBuf = new StringBuffer();
        boolean bFlag= false;
        StringBuffer queryBuf = new StringBuffer();
        StringBuffer errorMsg = new StringBuffer();
    	try{
    		//1.解析凭证数据源语句
    		strSql = buildVchDsSql(sDsCode, portCode);
            
            rsDs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
    		
            //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B start---//
            //如果没有根据数据源查询到数据，返回前，先获取数据源字段 和 字段类型
    		hmDsFieldType = dbFun.getFieldsType(rsDs);//获取数据源字段【KEY:字段名   value:字段类型】
            hmDsFieldSize = dbFun.getFieldsSizeInfo(rsDs);//获取数据源字段精度/长度 【KEY:字段名\t字段类型   value:精度/长度】
            //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
            
            //2.如果查询不到数据，直接返回
    		if(!rsDs.next()){
    			return false;
    		}else{
    			rsDs.beforeFirst();
    		}
    		
          
    		//3. 判断是否已存在临时表
    		if(dbl.yssTableExist(tabName)){
    			dbl.executeSql("drop table "+tabName);
    		}
    		
       		/*
    		 * 考虑到字段配置设置的历史遗留问题，直接把数据复制到临时表会报错的问题。
    		 * 报错原因分析：
    		 *  数据源字段与字段配置的字段不一致。
    		 *    譬如，凭证数据源字段把某个用不到的字段给去掉，但是在字段配置仍然保留那个去掉的字段。
    		 * 解决方案：
    		 *   只复制凭证模板要用到的字段到临时表中。
    		 */
    		
    		fieldMap = getVchEntiSetField(sVchTplCode,hmDsFieldType);
    		
    		creTabBuf.append(" create table ").append(tabName).append("(fvchnum  varchar2(20),fsetcode varchar2(20)");
    		insertBuf.append(" insert into ").append(tabName).append("(fvchnum,fsetcode");
    		insertBuf1.append(" values(?,?");
    		
    		//动态拼接建表和数据插入语句。
    		for(Map.Entry<String,String> m: fieldMap.entrySet()){ 
    			   //System.out.println(m.getKey()+"---"+m.getValue()); 
    			   keyBuf.setLength(0);
    			   keyBuf.append(m.getKey()).append("\t").append(m.getValue());
    			   //拼接建表语句，日期类型直接拼接，而varchar 和 number 类型都需要长度和精度值
    			   if(m.getValue().toLowerCase().equalsIgnoreCase("date")){
    				   creTabBuf.append(",").append(m.getKey()).append(" ").append(m.getValue());
    			   }else{
    				   creTabBuf.append(",").append(m.getKey()).append(" ").append(m.getValue()).append((String)hmDsFieldSize.get(keyBuf.toString()));
    			   }
    			   
    			   insertBuf.append(",").append(m.getKey());
    			   insertBuf1.append(",").append("?");
    		} 
    		 creTabBuf.append(" )");
			 insertBuf.append(" )").append(insertBuf1.toString()).append(" )");
			 //建临时表
    		dbl.executeSql(creTabBuf.toString());
    		
    		
    		pst = dbl.getPreparedStatement(insertBuf.toString());
    		int count =1;//批量执行计数，每500条记录执行一次
    		int i=1;
    		int j=3;//字段计数
    		String  sSetCode= "";
    		StringBuffer vchNum = new StringBuffer();
    		sSetCode = getBookSet(portCode);//根据组合代码获取套帐代码
    		//动态给插入语句赋值
    		while(rsDs.next()){
    			bFlag = true;//用于判断是否有数据
    			pst.setString(1, getLsh());//获取凭证流水号
    			pst.setString(2, sSetCode);
    			j=3;
    			for(Map.Entry<String,String> m: fieldMap.entrySet()){ 
     			    if(m.getValue().toLowerCase().equalsIgnoreCase("varchar2")||m.getValue().toLowerCase().equalsIgnoreCase("char")){
     			    	pst.setString(j, rsDs.getString(m.getKey()));
     			    }else if(m.getValue().toLowerCase().equalsIgnoreCase("number")){
     			    	pst.setDouble(j, rsDs.getDouble(m.getKey()));
     			    }else if(m.getValue().toLowerCase().equalsIgnoreCase("date")){
     			    	pst.setDate(j, rsDs.getDate(m.getKey()));
     			    }
     			    j++;
     		    } 
    			pst.addBatch();
    			count++;
    			//每500行批量插入数据
    			if(count==500){
    				pst.executeBatch();
    				count=1;
    			}
    			i++;
    			
    		}
    		pst.executeBatch();
    		dbl.loadConnection().commit();
    		
    		return bFlag;
    	}catch(Exception e){
    		
    		if(e.getMessage().indexOf("ORA-00918")>=0){
    			errorMsg.append("【凭证模板：").append(sVchTplCode).append(" —— 凭证数据源：").append(sDsCode).append("】未明确定义列,请核对凭证数据源... ...");
    		}else{
    			errorMsg.append("复制凭证数据源数据到临时表出错......\r\n").append(e.getMessage());
    		}
    		
    		throw new YssException(errorMsg.toString());
    	}finally{
    		creTabBuf.setLength(0);
    		
    		if(pst != null){
    			dbl.closeStatementFinal(pst);
    		}
    		
    		dbl.closeResultSetFinal(rsDs);
    	}
    	
    }
       
    private String formatNum(String snum,String format) throws YssException{
    	
    	String sNum = "";
    	sNum = snum;
    	int numLen = sNum.length();
    	
    	for(int i=0;i<format.length()-numLen;i++){
    		sNum ="0"+sNum;
    	}
    	
    	return sNum;
    }
    
    private String getBookSet(String portcode)throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	String bookSet="";
    	try{
    		queryBuf.append(" select trim(to_char(b.fsetcode,'000')) fsetcode from ");//modified by yeshenghong 20130506  bug7729
    		queryBuf.append(" (select fportcode,fassetcode from  " ).append(pub.yssGetTableName("tb_para_portfolio")).append(" where fcheckstate=1)a ");
            queryBuf.append(" left join ");
            queryBuf.append(" (select fsetid,fyear,fsetcode from lsetlist) b on a.fassetcode = b.fsetid ");
            queryBuf.append(" where a.fportcode=").append(dbl.sqlString(portcode));
            queryBuf.append(" order by  b.fyear desc");
            rs = dbl.openResultSet(queryBuf.toString());
            if(rs.next()){
            	bookSet = rs.getString("fsetcode");
            }else{
            	throw new YssException("匹配不到组合【"+portcode+"】对于的套帐代码... ...");
            }
           
            return bookSet;
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
    		 queryBuf.setLength(0);
             dbl.closeResultSetFinal(rs);
    	}
    }
    
    private String getDateFiled(String sVchTplCode) throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	String sDateFiled="";
    	StringBuffer sErrMsg = new StringBuffer();
    	try{
    		
    		queryBuf.append(" select fdatefield from  ").append(pub.yssGetTableName("tb_vch_vchtpl"));
			queryBuf.append(" where fcheckstate=1 and fvchtplcode=").append(dbl.sqlString(sVchTplCode));
			rs = dbl.openResultSet(queryBuf.toString());
			if(rs.next()){
				sDateFiled = rs.getString("fdatefield");
			}else{
				sErrMsg.append("请检查【凭证模板:"+sVchTplCode+"】日期字段是否正确设置 或者 凭证模板是否审核... ...");
				throw new YssException(sErrMsg.toString());
			}
			
			return sDateFiled;
    		
    	}catch(Exception e ){
    		throw new YssException(e);
    	}finally{
   		 queryBuf.setLength(0);
         dbl.closeResultSetFinal(rs);
	    }
    }
    
    /**
     * 获取凭证流水号
     * @return
     * @throws YssException
     */
    private String getLsh()throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	String sLsNum="";
    	StringBuffer sErrMsg = new StringBuffer();
    	try{
    		
    		queryBuf.append("select trim(to_char(SEQ_VCH_DATA.NextVal,'00000000000000000000')) as runNO from  dual");
    		
    		rs = dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			sLsNum= rs.getString("runNO");
			}
    		return sLsNum;
    	}catch(Exception e){
    		throw new YssException("获取凭证编号出错... ...",e);
    	}finally{
    		 queryBuf.setLength(0);
             dbl.closeResultSetFinal(rs);
    	}
    }
    
     private String BuiderFieldSql(String sDsCode,String sVchTplCode,String portCode)throws YssException{
    	
    	StringBuffer queryBuf = new StringBuffer();
    	StringBuffer filedBuf = null;
    	StringBuffer sErrMsg  = new StringBuffer();
    	String sLinkCode="";
    	ResultSet rs = null;
    	try{
    		
    		//1. 拼接查询数据源字段SQL语句
    		//filedBuf = getVchEntiSetField(sVchTplCode);
    		
    		//1. 到最后才处理凭证编号【T+年月日+seq(6)+rowNum(5)】和套帐号
    		if(filedBuf.length()>0){
    			//1. 处理凭证编号
    			//1.1 获取日期字段名
    			queryBuf.append(" select fdatefield,FLINKCODE from  ").append(pub.yssGetTableName("tb_vch_vchtpl"));
    			queryBuf.append(" where fcheckstate=1 and fvchtplcode=").append(dbl.sqlString(sVchTplCode));
    			rs = dbl.openResultSet(queryBuf.toString());
    			if(rs.next()){
    				sLinkCode = rs.getString("FLINKCODE");
    				filedBuf.append(",").append("'T'||").append("to_char(").append(rs.getString("fdatefield").trim()).append(",'yyyyMMdd')").append("||");
    			}else{
    				sErrMsg.append("请检查【凭证模板:"+sVchTplCode+"】日期字段是否正确设置");
    				throw new YssException(sErrMsg.toString());
    			}
    			queryBuf.setLength(0);
        		dbl.closeResultSetFinal(rs);
    			//1.2获取流水号(定长为6位)
        		queryBuf.append("select SEQ_VCH_DATA.NextVal as runNO from  dual");
    			rs = dbl.openResultSet(queryBuf.toString());
    			if(rs.next()){
    				filedBuf.append("trim(to_char(").append(rs.getInt("runNO")).append(",'0000000'))").append("||trim(to_char(rownum,'0000')) as fvchnum");
    			}else{
    				sErrMsg.append("请检查【凭证数据 sequence】是否创建");
    				throw new YssException(sErrMsg.toString());
    			}
    			queryBuf.setLength(0);
        		dbl.closeResultSetFinal(rs);
        		
        		//2. 添加套帐号
    			return filedBuf.toString();//在最后加上凭证编号
    		}else{
    			sErrMsg.append("请检查【数据源："+sDsCode+"】是否设置了字段");
    			throw new YssException(sErrMsg.toString());
    		}
    		
    	}catch(Exception e){
    		throw new YssException(sErrMsg.length()>0?sErrMsg.toString():"拼接数据源查询字段出错......");
    	}finally{
    		queryBuf.setLength(0);
    		filedBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    
    /**
     * 获取实际用到的字段 
     * @return
     * @throws YssException
     */
    private HashMap getVchEntiSetField(String sVchtplcode,HashMap DsFieldType)throws YssException{
    	
    	
    	StringBuffer queryBuf = new StringBuffer();
    	StringBuffer updateBuf = new StringBuffer();
    	ResultSet rs = null;
    	HashMap valMap = new HashMap();
    	try{
    		//1.必输项：凭证模板的原币、日期字段
    		queryBuf.append(" select fsrccury,fdatefield from ").append(pub.yssGetTableName("tb_vch_vchtpl"));
    		queryBuf.append(" where fcheckstate=1 and fvchtplcode=").append(dbl.sqlString(sVchtplcode));
    		
    		rs = dbl.openResultSet(queryBuf.toString());
    		if(rs.next()){
    			valMap.put(rs.getString("fsrccury").toUpperCase().trim(), DsFieldType.get(rs.getString("fsrccury").toUpperCase().trim()));
    			valMap.put(rs.getString("fdatefield").toUpperCase().trim(), DsFieldType.get(rs.getString("fdatefield").toUpperCase().trim()));
         	}else{
    			throw new YssException("请检查【模板:"+sVchtplcode+"】的原币币种字段和日期字段配置是否正确... ...");
    		}
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		
    		//2.必输项：凭证分录科目 设置的历史遗留问题。所以先查询出除回收站里的设置 ，再通过判断未审核设置的正确性。
    		/******
    		 * 历史数据问题分析：
    		 *   在导入凭证模板的基础上调整相应凭证设置的，导致库中有些废弃的设置，没删除干净。
             *   譬如，通过复制某张凭证模板，然后修改成新的凭证模板代码，这凭证模板的废弃的设置。
             *        
    		 *  字段说明：
    		 *  fsubjectfield   科目字段
    		 *  fvaluetype      值类型
    		 *  fentitycode     分录代码
    		 *  
    		 *  说明：这个过滤条件使用 fcheckstate<>2 是因为凭证分录设置使用的时候未区分审核与未审核状态
    		 */
    		queryBuf.append(" select distinct fsubjectfield,fvaluetype,fentitycode  from ").append(pub.yssGetTableName("tb_vch_entitysubject"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" order by  fvaluetype");
    		
    		rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
    		if(!rs.next()){
    			throw new YssException("请检查【模板:"+sVchtplcode+"】的科目分录是否正确... ...");
    			
    		}
    		rs.beforeFirst();
    		
    		while(rs.next()){
    			//静态的设置不需要处理
    			if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
    				continue;
    			}
    			//缓存里面如果已存在，就不进行重复设置
    			if(!valMap.containsKey(rs.getString("fsubjectfield").toUpperCase().trim())){
    				//值类型为动态的 ，但是科目字段值又为null的认为是无效设置，这里直接把无效设置回收到回收站
    				if( rs.getString("fvaluetype").equalsIgnoreCase("0") && DsFieldType.get(rs.getString("fsubjectfield").toUpperCase().trim())==null){
    					updateBuf.append(" update ").append(pub.yssGetTableName("tb_vch_entitysubject")).append(" set fcheckstate=2 , fdesc=' 错误的历史设置......'");
    					updateBuf.append(" where fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" and fsubjectfield=").append(dbl.sqlString(rs.getString("fsubjectfield").toUpperCase().trim()));
    					
    					dbl.executeSql(updateBuf.toString());
    					updateBuf.setLength(0);
    					continue;
    				}
    				valMap.put(rs.getString("fsubjectfield").toUpperCase().trim(), DsFieldType.get(rs.getString("fsubjectfield").toUpperCase().trim()));
    			}
    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		
    		//3.必输项：凭证摘要
    		queryBuf.append(" select distinct fresumefield,fvaluetype,fentitycode  from ").append(pub.yssGetTableName("tb_vch_entityresume"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" order by  fvaluetype");
    		
    		rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
    		if(!rs.next()){
    			throw new YssException("请检查【模板:"+sVchtplcode+"】的摘要配置是否正确... ...");
    		}
    		rs.beforeFirst();
    		
    		while(rs.next()){
    			if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
    				continue;
    			}
    			if(!valMap.containsKey(rs.getString("fresumefield").toUpperCase().trim())){
    				
    				if( rs.getString("fvaluetype").equalsIgnoreCase("0") && DsFieldType.get(rs.getString("fresumefield").toUpperCase().trim())==null){
    					//历史数据问题，这里不进行对历史数据删除，而是将历史设置更新为回收站数据。
    					updateBuf.append(" update ").append(pub.yssGetTableName("tb_vch_entityresume")).append(" set fcheckstate=2 , fdesc=' 错误的历史设置......'");
    					updateBuf.append(" where fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" and fresumefield=").append(dbl.sqlString(rs.getString("fresumefield").toUpperCase().trim()));
    					
    					dbl.executeSql(updateBuf.toString());
    					updateBuf.setLength(0);
    					continue;
    				}
    				valMap.put(rs.getString("fresumefield").toUpperCase().trim(), DsFieldType.get(rs.getString("fresumefield").toUpperCase().trim()));
    			}

    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		
    		//4. 必输项：原币 
    		queryBuf.append(" select distinct fmafield ,fvaluetype,fentitycode from ").append(pub.yssGetTableName("tb_vch_entityma"));
    		queryBuf.append(" where  fcheckstate<>2 and ftype='Money'  and fvchtplcode=").append(dbl.sqlString(sVchtplcode));
    		queryBuf.append(" order by  fvaluetype");
    		rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
    		if(!rs.next()){
    			
    			throw new YssException("请检查【凭证模板:"+sVchtplcode+"】原币配置是否正确... ...");
    		}
    		rs.beforeFirst();
    		
    		while(rs.next()){
    			
    			if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
    				continue;
    			}
    			
                if(!valMap.containsKey(rs.getString("fmafield").toUpperCase().trim())){
                	if( rs.getString("fvaluetype").equalsIgnoreCase("0") && DsFieldType.get(rs.getString("fmafield").toUpperCase().trim())==null){
                		//历史数据问题，这里不进行对历史数据删除，而是将历史设置更新为回收站数据。
    					updateBuf.append(" update ").append(pub.yssGetTableName("tb_vch_entityma")).append(" set fcheckstate=2 , fdesc=' 错误的历史设置......'");
    					updateBuf.append(" where ftype='Money' and fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" and fmafield=").append(dbl.sqlString(rs.getString("fmafield").toUpperCase().trim()));
    					
    					dbl.executeSql(updateBuf.toString());
    					updateBuf.setLength(0);
    					continue;
    				}
    				valMap.put(rs.getString("fmafield").toUpperCase().trim(), DsFieldType.get(rs.getString("fmafield").toUpperCase().trim()));
    			}
    			
    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		//5. 可选项：（本币、数量）
    		queryBuf.append(" select distinct fmafield,fvaluetype  from ").append(pub.yssGetTableName("tb_vch_entityma"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchtplcode));
    		queryBuf.append(" order by  fvaluetype");
    		
    		rs = dbl.openResultSet(queryBuf.toString());
    		
    		while(rs.next()){
    			if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
    				continue;
    			}

    			 if(!valMap.containsKey(rs.getString("fmafield").toUpperCase().trim())){
    				 if( rs.getString("fvaluetype").equalsIgnoreCase("0") && DsFieldType.get(rs.getString("fmafield").toUpperCase().trim())==null){
                 		//历史数据问题，这里不进行对历史数据删除，而是将历史设置更新为回收站数据。
     					updateBuf.append(" update ").append(pub.yssGetTableName("tb_vch_entityma")).append(" set fcheckstate=2 , fdesc=' 错误的历史设置......'");
     					updateBuf.append(" where  fvchtplcode=").append(dbl.sqlString(sVchtplcode)).append(" and fmafield=").append(dbl.sqlString(rs.getString("fmafield").toUpperCase().trim()));
     					
     					dbl.executeSql(updateBuf.toString());
     					updateBuf.setLength(0);
     					continue;
     				}
     				valMap.put(rs.getString("fmafield").toUpperCase().trim(), DsFieldType.get(rs.getString("fmafield").toUpperCase().trim()));
     			}
    			
    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		//5. 可选项：模板 价格、货币字段
    		queryBuf.append(" select fpricefield,fencurycode from  ").append(pub.yssGetTableName("tb_vch_entity"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchtplcode));
    		rs = dbl.openResultSet(queryBuf.toString());
    		
    		while(rs.next()){
    			
    			 if(rs.getString("fpricefield")!=null && rs.getString("fpricefield").trim().length()!=0&&!valMap.containsKey(rs.getString("fpricefield").toUpperCase().trim())){
     				
     				valMap.put(rs.getString("fpricefield").toUpperCase().trim(), DsFieldType.get(rs.getString("fpricefield").toUpperCase().trim()));
     			}
    			 

    			 if(rs.getString("fencurycode")!=null && rs.getString("fencurycode").trim().length()!=0&&!valMap.containsKey(rs.getString("fencurycode").toUpperCase().trim())){

      				valMap.put(rs.getString("fencurycode").toUpperCase().trim(), DsFieldType.get(rs.getString("fencurycode").toUpperCase().trim()));
      			}
    			
    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		//6. 可选项：辅助核算项
    		queryBuf.append(" select distinct fassistantfield,fvaluetype  from ").append(pub.yssGetTableName("tb_vch_assistant"));
    		queryBuf.append(" where   fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchtplcode));
    		queryBuf.append(" order by  fvaluetype");
    		rs = dbl.openResultSet(queryBuf.toString());
    		
    		while(rs.next()){
    			if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
    				continue;
    			}
    			
               if(!valMap.containsKey(rs.getString("fassistantfield").toUpperCase().trim())){

    				valMap.put(rs.getString("fassistantfield").toUpperCase().trim(), DsFieldType.get(rs.getString("fassistantfield").toUpperCase().trim()));
    			}
    		}
    		
    		queryBuf.setLength(0);
    		dbl.closeResultSetFinal(rs);
    		//7. 可选项：条件 单行模式没用到这块，先不处理。预留
    		
    		return valMap;
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    	}
    }
    
    
    

    
    /**
     *  生成批量插入凭证数据的SQL.
     *  
     * @param sVchTplCode  凭证模块代码   
     * @param portCode     组合代码
     * @param tabName      临时表名   
     * @param sCreateTime  创建时间
     * @return
     * @throws YssException
     */
    private boolean builderDataBatchSql(String sVchTplCode,String portCode,String tabName,String createTime)throws YssException{
    	
    	StringBuffer insertBuf = new StringBuffer();//批量插入数据的sql语句
    	//---add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B start---//
    	StringBuffer checkBuf = new StringBuffer();
    	ResultSet rs = null;
    	//---add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B end---//
    	StringBuffer sqlBuf  = new StringBuffer();
    	StringBuffer FieldBuf =  new StringBuffer();
    	StringBuffer joinBuf =  new StringBuffer();
    	StringBuffer whereBuf = new StringBuffer();
		//edit by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B 
    	ResultSet rs1 = null;
    	String sCuryCode = "";//本币币种
    	StringBuffer sEnCuryCode = new StringBuffer();//FENCURYCODE
    	String sSrcCodeCol = "";//原币币种字段
    	String sDateCol = "";//日期字段
    	Statement st = null;
    	boolean bState = false;
    	String sSetCode="";
    	String sPreTB = "";
    	try{
    		//add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B 
    		sSetCode = this.formatNum(this.getBookSet(portCode), "000");
    		sPreTB = YssFun.formatDate(this.getBeginDate(), "yyyy")+this.formatNum(this.getBookSet(portCode), "000");
    		 st = dbl.openStatement();
    		 
    		
    		// 获取原币币种字段名和日期字段名 
    		sqlBuf.append(" select * from ").append(pub.yssGetTableName("Tb_Vch_VchTpl"));
    		sqlBuf.append(" where FCheckState = 1 and FVchTplCode = ").append(dbl.sqlString(sVchTplCode));
    		sqlBuf.append(" and ((FPortCode is null or FPortCode='' or FPortCode=' ')or FPortCode=").append(dbl.sqlString(portCode)).append(")");
            
            rs = dbl.openResultSet(sqlBuf.toString());
            if(rs.next()){
            	sSrcCodeCol = rs.getString("FSRCCURY");
            	sDateCol = rs.getString("FDATEFIELD");
            }
            dbl.closeResultSetFinal(rs);
            sqlBuf.setLength(0);
    		
    		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 生成批量插入凭证数据表的Sql start ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    		// 获取本币币种
    		sqlBuf.append("select fportcury from  ").append(pub.yssGetTableName("tb_para_portfolio")).append(" where fportcode=").append(dbl.sqlString(portCode));
    		rs = dbl.openResultSet(sqlBuf.toString());
    		if(rs.next()){
    			sCuryCode = rs.getString("fportcury");
    		}
    		dbl.closeResultSetFinal(rs);
    		sqlBuf.setLength(0);
    		
    		//根据凭证模板设置生成批量生成凭证主表的SQL
            insertBuf.append(" insert into ").append(pub.yssGetTableName("tb_vch_data"));
            insertBuf.append("(FVCHNUM,FVCHTPLCODE,FVCHDATE,FPORTCODE,FBOOKSETCODE,FCURYCODE,FSRCCURY,");
            insertBuf.append("FCURYRATE,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME)");
            insertBuf.append(" select ds.FVCHNUM,").append(dbl.sqlString(sVchTplCode));
            //考虑到数据源的日期字段有些用到是日期字段，有些用得是varchar2，所以这里添加类型判断
            //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
            if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
            	//--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
            	insertBuf.append(",ds.").append(sDateCol).append(",");
            }else{
            	insertBuf.append(",to_date(ds.").append(sDateCol).append(",'yyyy-MM-dd'),");
            }
            
            insertBuf.append(dbl.sqlString(portCode)).append(",ds.fsetcode,").append(dbl.sqlString(sCuryCode)).append(",ds.").append(sSrcCodeCol).append(",").append("nvl(valrate.fbaserate,1)");
            insertBuf.append(",' ',0,").append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(createTime)).append(" from ");
            //insertBuf.append("(").append(tabName).append(")ds"); 9i不支持这种写法
            insertBuf.append(tabName).append(" ds");
            insertBuf.append(" left join ");
          //用到的汇率是估值汇率，
            insertBuf.append(" (select fvaldate,fcurycode,fbaserate/fportrate as fbaserate from ").append(pub.yssGetTableName("tb_data_valrate"));
            insertBuf.append(" where fcheckstate=1 and fportcode=").append(dbl.sqlString(portCode)).append(")valrate on ");
            //考虑到数据源的日期字段有些用到是日期字段，有些用得是varchar2，所以这里添加类型判断
            //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
            if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
            	//--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
            	insertBuf.append(" ds.").append(sDateCol).append(" = valrate.fvaldate ");
            }else{
            	insertBuf.append(" to_date(ds.").append(sDateCol).append(",'yyyy-MM-dd')= valrate.fvaldate ");
            }
            insertBuf.append("  and ds.").append(sSrcCodeCol).append("=valrate.fcurycode");
            //st.execute(insertBuf.toString());
            //add by songjie 2013.01.17 用于保存插入凭证数据表的sql语句到log4j文件
            log.info(insertBuf.toString());
            st.addBatch(insertBuf.toString());
            System.out.println(insertBuf.toString());
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 生成批量插入凭证数据表的Sql end ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            
            
           //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 生成批量插入凭证分录数据表的Sql start ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            sqlBuf.append(" select * from ").append(pub.yssGetTableName("Tb_Vch_Entity"));
            sqlBuf.append(" where  FVchTplCode = ").append(dbl.sqlString(sVchTplCode));
            sqlBuf.append(" and FCheckState = 1 and fcalcway='Common' order by FEntityCode"); //第一次插入的时候只插入【普通分录】

    		rs = dbl.openResultSet(sqlBuf.toString());
            while(rs.next()){
            	
            	whereBuf.setLength(0);
            	sEnCuryCode.setLength(0);
            	insertBuf.setLength(0);
            	FieldBuf.setLength(0);
            	joinBuf.setLength(0);
            	//add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
            	checkBuf.setLength(0);
            	fetchValforCache(rs.getString("FENTITYCODE"),FieldBuf,joinBuf); //获取缓存数据
            	
            	//判断凭证分录指定币种就取指定币种，否则取的是科目币种
            	if(rs.getString("FENCURYCODE")==null||rs.getString("FENCURYCODE").trim().length()==0||rs.getString("FENCURYCODE").equalsIgnoreCase("null")){
            		sEnCuryCode.append(sSrcCodeCol);
            	}else{
            		sEnCuryCode.append(rs.getString("FENCURYCODE"));
            	}
            	
               if(null!=rs.getString("fallow")&&rs.getString("fallow").trim().length()>0){
            	   if(rs.getString("fallow").equalsIgnoreCase("AisZero")){
            		 //数量允许为0，原币或本位币任一不为0，保存原来分录
            		 //当为轧差类型时,才放入,使之后的轧差计算得以进行。当为普通类型时，不保留分录信息
            	      whereBuf.append(" where (entity.money<>0 or entity.setmoney<>0) and  entity.amount=0 ");
            		   
            		   
            	   }else if(rs.getString("fallow").equalsIgnoreCase("MisZero")){
            		 //金额(原币与本位币)允许为0,数量不为0，保存原来分录
            		   whereBuf.append(" where (entity.money=0 or entity.setmoney=0) and  entity.amount<>0 ");
            	   }else if(rs.getString("fallow").equalsIgnoreCase("AMisZero")){
            		 //数量与金额(原币与本位币)都允许为0，直接保存原有分录  
            		   whereBuf.append("");
            	   }else {
            		   whereBuf.append(" where entity.money<>0 or entity.setmoney<>0 or entity.amount<>0 ");
            	   }
            	    
    			}else{
    				whereBuf.append(" where entity.money<>0 or entity.setmoney<>0 or entity.amount<>0 ");
    			}
               
               //--- edit by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
               checkDictSetting(tabName,sVchTplCode);//add by zhaoxianlin 20130311 BUG7274
               //--- edit by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
               
               //---add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B start---//
               checkBuf.append(" select FVCHNUM,").append(dbl.sqlString(rs.getString("FENTITYCODE")));
               checkBuf.append(", FCURYRATE,fsetcode,").append(dbl.sqlString(rs.getString("FDCWAY"))).append(",1,");
               checkBuf.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(createTime));
               checkBuf.append(",round(money,2) as money, case when fstate='recal' then round(money * FCURYRATE,2) ");
               checkBuf.append(" else  round(setmoney,2) end as setmoney,amount,assistant,subject,resumename,");
               checkBuf.append(" case when ").append(rs.getString("FPRICEFIELD")).append("=null then 0 else ");
               checkBuf.append(rs.getString("FPRICEFIELD")).append(" end as FPRICEFIELD from ");
               checkBuf.append(" (select vch.*,valrate.fchangeRate as FCURYRATE  from ");
               checkBuf.append(" (select entity1.*,acc.facccurcode from ");
               checkBuf.append(" (select ").append(FieldBuf.toString()).append(" ds.* ").append(" from ");
               checkBuf.append("(").append(tabName).append(")ds");
               checkBuf.append(joinBuf.toString()).append(") entity1");
               checkBuf.append(" left join ");
               checkBuf.append(" ( select facctcode,fcurcode as facccurcode from a").append(sPreTB).append("laccount where facctdetail=1 )acc");
               checkBuf.append(" on acc.facctcode = entity1.subject)vch ");
               checkBuf.append(" left join ");
               checkBuf.append(" (select fvaldate,fcurycode,fbaserate/fportrate as fchangeRate from ").append(pub.yssGetTableName("tb_data_valrate"));
               checkBuf.append(" where fcheckstate=1 and fportcode=").append(dbl.sqlString(portCode)).append(")valrate  ");
               //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
               if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
            	 //--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
            	   checkBuf.append(" on vch.").append(sDateCol).append(" = valrate.fvaldate ");
               }else{
            	   checkBuf.append(" on to_date(vch.").append(sDateCol).append(",'yyyy-MM-dd')= valrate.fvaldate ");
               }
               checkBuf.append(" and vch.facccurcode=valrate.fcurycode )entity").append(whereBuf.toString());
               
               rs1 = dbl.openResultSet(checkBuf.toString());
               while(rs1.next()){
            	   if(rs1.getObject("Subject") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的科目，请检查凭证模板【" + 
            				   sVchTplCode + "】的科目设置 以及 科目设置相关凭证字典！");
            	   }
            	   if(rs1.getObject("Subject") != null && rs1.getObject("FCURYRATE") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的货币汇率，请检查套帐【" + 
            				   sSetCode + "】科目【" + rs1.getString("subject") + "】是否设置 或 科目币种相关汇率是否设置！");
            	   }
            	   if(rs1.getObject("ResumeName") == null){
            		   throw new YssException("获取不到凭证模版【" + sVchTplCode + "】相关凭证的摘要，请检查凭证模板【" + 
            				   sVchTplCode + "】的摘要设置 以及摘要设置相关凭证字典！");
            	   }
               }
               
               dbl.closeResultSetFinal(rs1);
               //---add by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B end---//
               
            	insertBuf.append(" insert into ").append(pub.yssGetTableName("tb_vch_dataentity"));
                insertBuf.append("(FVCHNUM,FENTITYNUM,FCURYRATE,FBOOKSETCODE,FDCWAY,FCHECKSTATE,FCREATOR,FCREATETIME,");
                insertBuf.append(" FBAL,FSETBAL,FAMOUNT,FASSISTANT,FSUBJECTCODE,FRESUME,FPRICE)");
                
				/**add---shashijie 2013-6-9 BUG 8133 重构获取批量插入的SQL语句,封装起来*/
				String IvchSQL = getInsertDataEntitySQL(rs.getString("FEntitycode"),rs.getString("FDCWAY"),
						createTime,rs.getString("FPricefield"),FieldBuf.toString(),tabName,joinBuf.toString(),
						sPreTB,portCode,sDateCol,whereBuf.toString());
				insertBuf.append(IvchSQL);
				//处理数据,判断是否有必输项为空字段的数据并给出提示
				doOperionInfoError(rs.getString("FEntitycode"),rs.getString("FDCWAY"),
						createTime,rs.getString("FPricefield"),FieldBuf.toString(),tabName,joinBuf.toString(),
						sPreTB,portCode,sDateCol,whereBuf.toString(),sVchTplCode);
				/**end---shashijie 2013-6-9 BUG 8133*/
                //st.execute(insertBuf.toString());
                //add by songjie 2013.01.17 BUG 6946 QDV4南方2013年01月18日01_B     
                log.info(insertBuf.toString());
                st.addBatch(insertBuf.toString());
                System.out.println(insertBuf.toString());
            }
            
            //通过执行返回的记录数来判断是否有数据
            int j =0;
            //st.executeBatch();
            int[] it = st.executeBatch();
            for(int i =0;i<it.length;i++){
            	j+=it[i];
            }
            bState = j>0?true:false;
			//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 生成批量插入凭证分录数据表的Sql end ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            
    		//StringBuffer delBuf = new StringBuffer();//无用注释
    		
    		return bState;
    	}catch(Exception e){
    		//edit by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
    		throw new YssException(sVchTplCode+"	生成批量插入凭证数据的Sql出错......", e);
    	}finally{
		    //edit by songjie 2013.01.21 BUG 6946 QDV4南方2013年01月18日01_B
    		dbl.closeResultSetFinal(rs,rs1);
    		sqlBuf.setLength(0);
    		FieldBuf.setLength(0);
    		joinBuf.setLength(0);
    		try {
				st.clearBatch();
				st.close();
	    		st = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		
    	}
    }
    
    /**shashijie 2013-6-9 BUG 8133 处理数据,判断是否有必输项为空字段的数据并给出提示 
     * @param FEntityCode 分录代码
     * @param FDCWay	借贷方向
     * @param createTime 创建时间
     * @param FPriceField	价格字段
     * @param FieldBuf 查询SQL字段
     * @param tabName 临时表名
     * @param joinBuf 关联查询SQL
     * @param sPreTB 财务表拼接年份套帐
     * @param portCode     组合代码
     * @param sDateCol 日期字段
     * @param whereBuf 总查询条件
     * @param sVchTplCode 凭证模块代码
     * */
	private void doOperionInfoError(String FEntityCode,String FDcway,String createTime,String FPriceField,
			String FieldBuf,String tabName,String joinBuf,String sPreTB,String portCode,String sDateCol,String whereBuf
			,String sVchTplCode) throws YssException {
		ResultSet rs = null;//定义游标
		try {
			//获取凭证模版对象
			VchTplBean vch = new VchTplBean();
			vch.setYssPub(pub);
			vch.setVchTplCode(sVchTplCode);//设置凭证模版代码
			vch.getSetting();
			//获取 SQL 
			String query = getInsertDataEntitySQL(FEntityCode, FDcway, createTime, FPriceField, FieldBuf, 
					tabName, joinBuf, sPreTB, portCode, sDateCol, whereBuf);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//判断是否有必输项为空字段的数据并给出提示
				//分录编号
				if (rs.getString("FEntityNum")==null) {
					setInfoRunStatus(vch.getVchTplCode(),vch.getVchTplName(),"分录编号");
				}
				//科目代码
				if (rs.getString("Subject")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",分录编号:"+rs.getString("FEntityNum"),
							"科目代码");
				}
				//汇率
				if (rs.getString("FCuryRate")==null) {
					setInfoRunStatus(vch.getVchTplCode(),vch.getVchTplName(),rs.getString("FEntityNum")
							,rs.getString("Subject"));
				}
				//摘要
				if (rs.getString("ResumeName")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",分录编号:"+rs.getString("FEntityNum"),
							"摘要");
				}
				//借贷方向
				if (rs.getString("FDCWay")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",分录编号:"+rs.getString("FEntityNum"),
							"借贷方向");
				}
				//套帐代码
				if (rs.getString("Fsetcode")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",组合代码:"+portCode,
							"套帐代码");
				}
				//原币金额
				if (rs.getString("Money")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",分录编号:"+rs.getString("FEntityNum"),
							"原币金额");
				}
				//本位币金额
				if (rs.getString("Setmoney")==null) {
					setInfoRunStatus(vch.getVchTplCode(),
							vch.getVchTplName()+",分录编号:"+rs.getString("FEntityNum"),
							"本位币金额");
				}
			}
		} catch (Exception e) {
			throw new YssException("\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
	}

	/**shashijie 2013-6-9 BUG 8133 设置提示信息 */
	private void setInfoRunStatus(String vchTplCode, String vchTplName,
			String FEntityNum, String FSubjectCode) {
		String info = "\r\n\r\n";
		
		info += "凭证模板【"+vchTplCode+"】"+vchTplName+",分录号:" +FEntityNum+",科目代码:"+FSubjectCode;
		info += ",的币种汇率没有设置";
		info += ",请检查该科目的币种是否正确!";
		info += "\r\n";
		
		runStatus.appendRunDesc("SchRun", info);
	}


	/**shashijie 2013-6-9 BUG 8133 设置提示信息 */
	private void setInfoRunStatus(String vchTplCode, String vchTplName,
			String message) {
		String info = "\r\n\r\n";
		
		info += "凭证模板【"+vchTplCode+"】"+vchTplName+",的"+message+"没有设置";
		info += ",请检查该凭证的"+message+"是否正确!";
		info += "\r\n";
		
		runStatus.appendRunDesc("SchRun", info);
	}


	/**shashijie 2013-6-9 BUG 8133 重构获取批量插入的查询SQL语句
     * @param FEntityCode 分录代码
     * @param FDCWay	借贷方向
     * @param createTime 创建时间
     * @param FPriceField	价格字段
     * @param FieldBuf 查询SQL字段
     * @param tabName 临时表名
     * @param joinBuf 关联查询SQL
     * @param sPreTB 财务表拼接年份套帐
     * @param portCode     组合代码
     * @param sDateCol 日期字段
     * @param whereBuf 总查询条件
     * */
	private String getInsertDataEntitySQL(String FEntityCode,String FDcway,String createTime,String FPriceField,
			String FieldBuf,String tabName,String joinBuf,String sPreTB,String portCode,String sDateCol,String whereBuf
			) {
		StringBuffer insertBuf = new StringBuffer();//批量插入数据的sql语句
		insertBuf.append(" Select FVCHNUM,");//凭证编号
		insertBuf.append(dbl.sqlString(FEntityCode) + " As Fentitynum ");//分录编号
		insertBuf.append(", FCURYRATE");//汇率
		insertBuf.append(", FSetCode,");//套帐代码
		insertBuf.append(dbl.sqlString(FDcway) + " As FDCWay ");//借贷方向
		insertBuf.append(",1,");//--审核状态
		insertBuf.append(dbl.sqlString(pub.getUserCode())).append(",").append(dbl.sqlString(createTime));
		insertBuf.append(",round(money,2) as Money");//原币金额
		insertBuf.append(", Case when fstate='recal' then round(money * FCURYRATE,2) ");
		insertBuf.append(" else  round(setmoney,2) end as setmoney");//本位币金额
		insertBuf.append(",amount");
		insertBuf.append(",assistant");
		insertBuf.append(",subject");//科目代码
		insertBuf.append(",resumename,");//摘要
		//某个字段=null就取0否则就取这个字段
		insertBuf.append(" case when ").append(FPriceField).append("=null then 0 else ").append(FPriceField);
		insertBuf.append(" end as FPRICEFIELD ");//价格字段
        //~ 以上是field 字段，主要是对本币的计算，如果没有设置本币，则重新计算的处理
        
        //判断本币计算方式，如果直接取值的话，就不拼接估值汇率表，
        //这样处理一是性能考虑，二是避免造成fbaserate列同名报未定义列的错误
        insertBuf.append(" From (select vch.*,valrate.fchangeRate as FCURYRATE From ");
        insertBuf.append(" (select entity1.*,acc.facccurcode From ");
    	insertBuf.append(" (select ").append(FieldBuf).append(" ds.* ").append(" From ");
        //insertBuf.append(" ( ").append(tabName).append(" ) ds");//9i 不支持这种写法方法
		insertBuf.append(tabName).append("  ds");
        insertBuf.append(joinBuf).append(") entity1");
        insertBuf.append(" Left Join ");
        //凭证多币种的情况：为了兼容性考虑，这里统一汇率币种取数来源，都取自科目表。
		insertBuf.append(" ( select facctcode,fcurcode as facccurcode From A");
		insertBuf.append(sPreTB);//财务系统表拼接"科目表"
		insertBuf.append("laccount where facctdetail=1 ) acc ");
		insertBuf.append(" on acc.facctcode = entity1.subject ) vch ");
		insertBuf.append(" Left Join ");
		insertBuf.append(" (select fvaldate,fcurycode,fbaserate/fportrate as fchangeRate From ");
		insertBuf.append(pub.yssGetTableName("tb_data_valrate"));
		insertBuf.append(" where fcheckstate=1 and fportcode=");
		insertBuf.append(dbl.sqlString(portCode)).append(" ) valrate ");
		//--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B 空指针异常 start---//
		//if(hmDsFieldType.get(sDateCol).toString().toLowerCase().equalsIgnoreCase("date")){
		if(hmDsFieldType.get(sDateCol.toUpperCase()).toString().toLowerCase().equalsIgnoreCase("date")){
		//--- edit by songjie 2013.05.27 BUG 7786 QDV4建信2013年05月09日01_B end---//
        	insertBuf.append(" on vch.").append(sDateCol).append(" = valrate.fvaldate ");
        }else{
        	insertBuf.append(" on to_date(vch.").append(sDateCol).append(",'yyyy-MM-dd')= valrate.fvaldate ");
        }
        insertBuf.append(" And vch.facccurcode = valrate.fcurycode ) entity ").append(whereBuf.toString());
        
        return insertBuf.toString();
	}

    /**
     * add by zhaoxianlin 20130311 BUG7274
     * @param tabName
     * @param joinBuf
     * @throws YssException
     */
	//edit by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B 添加参数 sVchTplCode 删除参数 joinBuf
    public void checkDictSetting(String tabName,String sVchTplCode) throws YssException{
    	//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
    	StringBuffer errorInfo = new StringBuffer();//用于保存提示信息
    	//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
		try{
            //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
			checkVchDict(sVchTplCode, tabName, errorInfo,1);
			checkVchDict(sVchTplCode, tabName, errorInfo,2);
			checkVchDict(sVchTplCode, tabName, errorInfo,3);
			
            if(errorInfo.toString().trim().length() > 0){
            	throw new YssException("请检查凭证模板【" + sVchTplCode + "】如下凭证字典是否设置：\r\n" + 
            			errorInfo.toString());
            }
            //--- add by songjie 2013.05.28 QDV4建信2013年05月09日01_B end---//
		}catch(Exception e){
			throw new YssException("检查数据字典标示符设置出错！\n", e);
		}	
	}
    
    /**
     * add by songjie 2013.05.28
     * BUG 7786 QDV4建信2013年05月09日01_B
     * 
     * 检查凭证模板 科目、摘要、辅助核算 设置 对应的凭证字典是否设置相应的数据
     * 
     * @param sVchTplCode 凭证模板代码
     * @param tabName 凭证数据源结果集对应的临时表
     * @param errorInfo 提示信息
     * @param mode 模式   
     * 
     * 模式 == 1 则检查 凭证模板科目设置
     * 模式 == 2 则检查 凭证模板摘要设置
     * 模式 == 3 则检查 凭证模板辅助核算设置
     * 
     * @throws YssException
     */
    private void checkVchDict(String sVchTplCode, String tabName,StringBuffer errorInfo,int mode) throws YssException{
    	StringBuffer sqlBuf = new StringBuffer();
    	StringBuffer key = new StringBuffer();
    	ResultSet rstemp = null;
    	String fieldSql = "";
    	String joinSql = "";
    	SetBean pojo = null;
    	HashMap hmSql = null;
    	String sourceData = "";//用于保存需用凭证字典转换的数据源字段值
    	HashSet stError = new HashSet();
    	String field = "";
    	String dictField = "";
    	String tableName = "";
    	String showItem = "";
    	String keyWord = "";
    	try{
    		//如果mode == 1 则判断凭证模板科目设置对应的凭证字典是否需要设置转换数据
    		if(mode == 1){
    			field = "FSubjectField";
    			dictField = "FSubjectDict";
    			tableName = "Tb_Vch_EntitySubject";
    			keyWord = "subject";
    			showItem = "科目";
    		}
    		//如果mode == 1 则判断凭证模板摘要设置对应的凭证字典是否需要设置转换数据
    		if(mode == 2){
    			field = "FResumeField";
    			dictField = "FResumeDict";
    			tableName = "Tb_Vch_EntityResume";
    			keyWord = "resume";
    			showItem = "摘要";
    		}
    		//如果mode == 1 则判断凭证模板辅助核算设置对应的凭证字典是否需要设置转换数据
    		if(mode == 3){
    			field = "FAssistantField";
    			dictField = "FAssistantDict";
    			tableName = "Tb_Vch_Assistant";
    			keyWord = "assistant";
    			showItem = "辅助核算";
    		}
    		
    		//查询凭证分录 科目/ 摘要/ 辅助核算 设置表中 当前凭证模版对应的 设置了凭证字典的动态类型的 数据
            sqlBuf.append(" select distinct FEntityCode, " + field + ", " + dictField + " from ")
            .append(pub.yssGetTableName(tableName))
            .append(" where fcheckstate = 1 and fvchtplcode = ").append(dbl.sqlString(sVchTplCode))
            .append(" and FValueType = 0 and " + dictField + " is not null and " + dictField + " <> 'null'");
            /**add---huhuichao 2013-10-16 BUG  81315 凭证优化执行调度方案提示*/
            sqlBuf.append(" and fentitycode in (select distinct fentitycode from ");
    		sqlBuf.append(pub.yssGetTableName("tb_vch_entity")).append(" where fvchtplcode = ");
    		sqlBuf.append(dbl.sqlString(sVchTplCode)).append(" )");
			/**end---huhuichao 2013-10-16 BUG  81315*/
            
            rstemp = dbl.openResultSet(sqlBuf.toString());
            sqlBuf.setLength(0);
            while(rstemp.next()){
            	fieldSql = "";
            	joinSql = "";
            	key.setLength(0);
            	
            	key.append(rstemp.getString("FEntityCode")).append("\t").append(keyWord);
            	pojo= (SetBean)setCache.get(key.toString());
            	
            	if(pojo != null){
            		hmSql = (HashMap)pojo.hmSql.get(keyWord + " " + rstemp.getString(field) + 
            				" " + rstemp.getString(dictField));
            		if(hmSql != null){
            			fieldSql = (String)hmSql.get("fieldSql");//获取根据原数据查询到的转换数据
            			joinSql = (String)hmSql.get("joinSql");//获取用于关联凭证字典数据的sql
            		}
            	}
            	
            	if(fieldSql.trim().length() > 0 && joinSql.trim().length() > 0){
            		sourceData = judgeHaveConvertData(rstemp, field, fieldSql, tabName, joinSql);
                	buildErrorInfo(sourceData, stError, showItem, rstemp, dictField);
            	}
            }
            
            buildStringBuffer(stError, errorInfo);
    	}catch(Exception e){
    		throw new YssException("检查凭证模版  " + showItem + "设置  对应的凭证字典设置出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(rstemp);
    	}
    }
    
    /**
     * add by songjie 2013.05.28
     * BUG 7786 QDV4建信2013年05月09日01_B
     * 
     * 获取未设置凭证字典信息
     * @param rstemp
     * @param field
     * @param fieldSql
     * @param tabName
     * @param joinSql
     * @return
     * @throws YssException
     */
    private String judgeHaveConvertData(ResultSet rstemp, String field, String fieldSql, 
    		                            String tabName, String joinSql) throws YssException{
    	ResultSet Rsdict = null;
    	StringBuffer sqlBuf = new StringBuffer();
    	StringBuffer sourceData = new StringBuffer();
    	HashSet stOriginal = new HashSet();
    	String originalStr = "";
    	try{
    		//查询凭证模板中 凭证数据源某字段的数据 以及 通过凭证字典转换后的数据
    		sqlBuf.append(" select ds." + rstemp.getString(field) + "," + fieldSql)
    		      .append(" as FCnvconent from " + tabName + " ds " + joinSql);
        	Rsdict = dbl.openResultSet(sqlBuf.toString());
        	
        	while(Rsdict.next()){
        		//如果原数据不为空 且 找不到对应的转换数据，则提示用户设置
        		if(Rsdict.getString(rstemp.getString(field)) != null && Rsdict.getString("FCnvConent") == null){
        			originalStr = Rsdict.getString(rstemp.getString(field));
        			stOriginal.add(originalStr + ",");
        		}
        	}
        	
        	buildStringBuffer(stOriginal, sourceData);
        	
        	return sourceData.toString();
    	}catch(Exception e){
    		throw new YssException("获取未设置凭证字典信息出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(Rsdict);
    	}
    }
    
    /**
     * add by songjie 2013.05.28
     * BUG 7786 QDV4建信2013年05月09日01_B
     * 
     * 拼接 StringBuffer 
     * 
     * @param stOriginal
     * @param sourceData
     */
    private void buildStringBuffer(HashSet stOriginal, StringBuffer sourceData){
    	Iterator it = null;
    	if(stOriginal.size() > 0){
    		it = stOriginal.iterator();
    		while(it.hasNext()){
    			sourceData.append((String)it.next());
    		}
    	}
    }
    
    /**
     * add by songjie 2013.05.28
     * BUG 7786 QDV4建信2013年05月09日01_B
     * 
     * 拼接凭证模版提示信息
     * 
     * @param sourceData
     * @param alError
     * @param errorInfo
     * @param showItem
     * @param rstemp
     * @param dictField
     * @throws YssException
     */
    private void buildErrorInfo(String sourceData, HashSet stError,String showItem, 
    		                    ResultSet rstemp, String dictField) throws YssException{
    	StringBuffer errorBuf = null;
    	try{
        	if(sourceData.trim().length() > 0){
        		//拼接提示信息
        		sourceData = sourceData.substring(0,sourceData.length() - 1);
        		errorBuf = new StringBuffer();
        		errorBuf.append("【" + showItem + "设置】凭证字典【")
						.append(rstemp.getString(dictField))
						.append("】原数据【")
						.append(sourceData)
						.append("】的转换内容;\r\n");
        			
        		stError.add(errorBuf.toString());
        	}
    	}catch(Exception e){
    		throw new YssException("拼接凭证模版提示信息出错！", e);
    	}
    }
   
    /**
     *  根据
     * @throws YssException
     */
    public void fetchValforCache(String fentitycode,StringBuffer FieldBuf,StringBuffer joinBuf )throws YssException{
    	
    	StringBuffer key = new StringBuffer();
    	SetBean pojo = null;
    	Iterator it = null;
    	StringBuffer errBuf = new StringBuffer();
    	try{
    		
    		//原币 FBAL,FSETBAL,FAMOUNT,FASSISTANT,FSUBJECTCODE,FRESUME
    		key.append(fentitycode).append("\t").append("Money");
    		pojo= (SetBean)setCache.get(key.toString());
    		FieldBuf.append(pojo.fieldBuf.toString()).append(" as money,");
    		
    		//本币
    		key.setLength(0);
    		key.append(fentitycode).append("\t").append("SetMoney");
    		pojo= (SetBean)setCache.get(key.toString());
    		if(pojo!=null){
    			FieldBuf.append(pojo.fieldBuf.toString()).append(" as setmoney,' 'as fstate,");
    		}else{
    			FieldBuf.append(" 0 as setmoney,'recal' as fstate,");
    		}
    		
    		
    		//数量
    		key.setLength(0);
    		key.append(fentitycode).append("\t").append("Amount");
    		pojo= (SetBean)setCache.get(key.toString());
    		FieldBuf.append(pojo==null?"0":pojo.fieldBuf.toString()).append(" as amount,");
    		
    		//辅助核算项
    		key.setLength(0);
    		key.append(fentitycode).append("\t").append("assistant");
    		pojo= (SetBean)setCache.get(key.toString());
    		if(pojo!=null){
    			FieldBuf.append(pojo.fieldBuf.toString()).append(" as assistant,");
        		if(pojo.joinSqlArr != null){
        			it =  pojo.joinSqlArr.iterator();
        			while(it.hasNext()){
        				joinBuf.append((String)it.next()).append(" ");
        			}
        		}
    		}else{
    			FieldBuf.append(" ' ' as assistant,");
    		}
    		
    		//科目
    		key.setLength(0);
    		key.append(fentitycode).append("\t").append("subject");
    		pojo= (SetBean)setCache.get(key.toString());
			if (pojo != null) {
				FieldBuf.append(pojo.fieldBuf.toString())
						.append(" as subject,");
				if (pojo.joinSqlArr != null) {
					it = pojo.joinSqlArr.iterator();
					while (it.hasNext()) {
						joinBuf.append((String) it.next()).append(" ");
					}
				}
			}else{
    			throw new YssException(errBuf.append("获取分录号为：").append(fentitycode).append(" 获取科目缓存数据出错.....").toString());
    		}
    		
            //摘要字段
    		key.setLength(0);
    		key.append(fentitycode).append("\t").append("resume");
    		pojo= (SetBean)setCache.get(key.toString());
    		FieldBuf.append(pojo.fieldBuf.toString()).append(" as resumename,");
			if (pojo != null) {
				if (pojo.joinSqlArr != null) {
					it = pojo.joinSqlArr.iterator();
					while (it.hasNext()) {
						joinBuf.append((String) it.next()).append(" ");
					}
				}
			}else {
    			throw new YssException(errBuf.append("获取分录号为：").append(fentitycode).append(" 获取摘要缓存数据出错.....").toString());
    		}
    		
    	}catch(Exception e){
    		throw new YssException(errBuf.length()>0?errBuf.append(e).toString():"获取缓存数据出错......"+e);
    	}
    	
    	
    }

    /**
     *  初始化凭证设置的缓存数据
     * @param sVchTplCode
     * @param setCache
     * @return
     * @throws YssException
     */
    private void initSetCache(String sVchTplCode,HashMap setCache , String portCode)throws YssException{ //modify huangqirong 2013-04-18 bug #7519 增加组合参数
    	
    	//setCache = new HashMap();
    	StringBuffer queryBuf = new StringBuffer();
    	ResultSet rs = null;
    	try{
    		checkVchTpl(sVchTplCode);
    		SetMapVal("subject",sVchTplCode,setCache , portCode);  //1. 初始化科目代码设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		SetMapVal("resume",sVchTplCode,setCache, portCode);   //2. 初始化摘要设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		SetMapVal("Money",sVchTplCode,setCache, portCode);    //3. 初始化原币设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		SetMapVal("SetMoney",sVchTplCode,setCache, portCode); //4. 初始化本币设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		SetMapVal("Amount",sVchTplCode,setCache, portCode);   //5. 初始化数量设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		//SetMapVal("cond",sVchTplCode,setCache);   //6. 初始化条件设置
    		SetMapVal("assistant",sVchTplCode,setCache, portCode);   //7. 初始化辅助核算项设置	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
    		
    		
    		//return setCache;
    	}catch(Exception e){
    		throw new YssException(" 初始化凭证模板【"+sVchTplCode+"】设置数据出错...... \r\n"+e.getMessage());
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		queryBuf.setLength(0);
    	}
    }
    

	private void SetMapVal(String sSetType,String sVchTplCode,HashMap setCache , String portCode) throws YssException {	//modify huangqirong 2013-04-18 bug #7519 增加组合参数

		StringBuffer query = null;
		
		try{
			query = BuilderSetQuery(sSetType,sVchTplCode);
			
			if(sSetType.equalsIgnoreCase("subject")||sSetType.equalsIgnoreCase("resume")){
				
				SetResOrSubVal(query,setCache,sSetType , portCode );//缓存科目和摘要设置数据	//modify huangqirong 2013-04-18 bug #7519 增加组合参数
				
			}else if(sSetType.equalsIgnoreCase("Money")||sSetType.equalsIgnoreCase("SetMoney")||sSetType.equalsIgnoreCase("Amount")){
				
				SetMaVal(query,setCache,sSetType);//缓存原币、本币、数量设置数据
				
			}else if(sSetType.equalsIgnoreCase("assistant")){
				
				SetAssistantVal(query,setCache,sSetType);//缓存辅助核算设置数据
				
			}else if(sSetType.equalsIgnoreCase("cond")){
				
				SetCondVal(query,setCache,sSetType);//缓存条件设置数据
				
			}
			
		}catch(Exception e){
			throw new YssException(e.getMessage());
		}finally{
			query.setLength(0);
		}
	}
    
	
	private void checkVchTpl(String sVchTplCode)throws YssException{
		
		StringBuffer queryBuf = new StringBuffer();
		StringBuffer errorBuf = new StringBuffer();
		ResultSet rs = null;
		try{
			
			//20130312 modified by liubo.Bug #7296
			//要求不要检查未审核的分录
			//======================================
			queryBuf.append(" select a1.*,b1.fvchtplcode from ");
			queryBuf.append(" (select fvchtplcode,fentitycode,fentityname from ").append(pub.yssGetTableName("tb_vch_entity"));
			queryBuf.append(" where fcheckstate = 1 and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")a1 ");
			queryBuf.append(" left join ");
			queryBuf.append(" (select fvchtplcode,fentitycode from ").append(pub.yssGetTableName("tb_vch_entityresume"));;
			queryBuf.append(" where fcheckstate <> 2  and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")b1 ");
			queryBuf.append(" on a1.fvchtplcode = b1.fvchtplcode and a1.fentitycode = b1.fentitycode ");
			queryBuf.append(" where b1.fvchtplcode is null order by a1.fentitycode ");
			
			rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
			while(rs.next()){
				if(rs.isFirst()){
					errorBuf.append("【分录号：").append(rs.getString("fentitycode"));
				}else{
					errorBuf.append("、").append(rs.getString("fentitycode"));
				}
				
				if(rs.isLast()){
					errorBuf.append("】凭证摘要的凭证元素未设置，请检查... ... \r\n");
				}
			}
			queryBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
			
			queryBuf.append(" select a1.*,b1.fvchtplcode from ");
			queryBuf.append(" (select fvchtplcode,fentitycode,fentityname from ").append(pub.yssGetTableName("tb_vch_entity"));
			queryBuf.append(" where fcheckstate = 1 and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")a1 ");
			queryBuf.append(" left join ");
			queryBuf.append(" (select fvchtplcode,fentitycode from ").append(pub.yssGetTableName("tb_vch_entitysubject"));;
			queryBuf.append(" where fcheckstate <> 2  and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")b1 ");
			queryBuf.append(" on a1.fvchtplcode = b1.fvchtplcode and a1.fentitycode = b1.fentitycode ");
			queryBuf.append(" where b1.fvchtplcode is null order by a1.fentitycode");
			
			rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
			while(rs.next()){
				if(rs.isFirst()){
					errorBuf.append("【分录号：").append(rs.getString("fentitycode"));
				}else{
					errorBuf.append("、").append(rs.getString("fentitycode"));
				}
				
				if(rs.isLast()){
					errorBuf.append("】科目代码的凭证元素未设置，请检查... ...\r\n");
				}
			}
			queryBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
			
			queryBuf.append(" select a1.*,b1.fvchtplcode from ");
			queryBuf.append(" (select fvchtplcode,fentitycode,fentityname from ").append(pub.yssGetTableName("tb_vch_entity"));
			queryBuf.append(" where fcheckstate = 1 and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")a1 ");
			queryBuf.append(" left join ");
			queryBuf.append(" (select fvchtplcode,fentitycode from ").append(pub.yssGetTableName("tb_vch_entityma"));;
			queryBuf.append(" where fcheckstate <> 2 and ftype='Money' and fvchtplcode =").append(dbl.sqlString(sVchTplCode)).append(")b1 ");
			queryBuf.append(" on a1.fvchtplcode = b1.fvchtplcode and a1.fentitycode = b1.fentitycode ");
			queryBuf.append(" where b1.fvchtplcode is null order by a1.fentitycode");
			
			rs = dbl.openResultSet(queryBuf.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);
			while(rs.next()){
				if(rs.isFirst()){
					errorBuf.append("【分录号：").append(rs.getString("fentitycode"));
				}else{
					errorBuf.append("、").append(rs.getString("fentitycode"));
				}
				
				if(rs.isLast()){
					errorBuf.append("】原币的凭证元素未设置，请检查... ...\r\n");
				}
			}
			//=================end=====================
			
			if(errorBuf.toString().length()>0){
				throw new Exception(errorBuf.toString());
			}
			
		}catch(Exception e){
			throw new YssException(errorBuf.toString().length()>0?" 检测到凭证模板【"+sVchTplCode+"】分录设置有以下异常：\r\n"+errorBuf.toString():e.getMessage());
		}finally{
             dbl.closeResultSetFinal(rs);
		}
	}
	
	
	
    /**
     * 缓存科目和摘要设置数据
     * 静态：
     *   拼接值
     * 动态：
     *   没有凭证字典： 
     *     拼接字段名
     *   设置凭证字典：
     *     拼接字典的字段名，并且拼接出关联凭证字典的sql语句，
     * @param query
     * @param setCache
     * @param sSetType 
     * @throws YssException
     */
	private void SetResOrSubVal(StringBuffer query,HashMap setCache,String sSetType , String portCode)throws YssException{ //modify huangqirong 2013-04-18 bug #7519 增加组合参数
		
		ResultSet rs = null;
		StringBuffer key = new StringBuffer();
		StringBuffer val = new StringBuffer();
		StringBuffer joinBuf = new StringBuffer();
		SetBean pojo = null;
		//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
		HashMap hmDetail = null;//用于保存 joinSql 和 fieldSql
		String fieldCode = "";//数据源字段代码
		String dictCode = "";//凭证字典代码
		StringBuffer fieldBuf = new StringBuffer();//用于保存 通过凭证字典转换的获取数据源字段的 sql 
		//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
		try {

			rs = dbl.openResultSet(query.toString());

			while (rs.next()) {
				fieldBuf.setLength(0);//add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B
				//key ： 分录号\t设置类型
				key.append(rs.getString("fentitycode")).append("\t").append(sSetType);
				
				//获取设置Bean
				pojo = (SetBean)setCache.get(key.toString());
				if(pojo == null){
					pojo = new SetBean();
				}
				
				//value:
				if(rs.getString("fvaluetype").equalsIgnoreCase("0")){
					//科目 动态
					if(pojo.fieldBuf.length()>0){
						pojo.fieldBuf.append("||");
					}
					
					if((sSetType.equalsIgnoreCase("subject") ? rs.getString("FSUBJECTDICT") : rs.getString("FRESUMEDICT")) != null && 
					  !(sSetType.equalsIgnoreCase("subject") ? rs.getString("FSUBJECTDICT") : rs.getString("FRESUMEDICT")).equalsIgnoreCase("null") &&
					   (sSetType.equalsIgnoreCase("subject") ? rs.getString("FSUBJECTDICT") : rs.getString("FRESUMEDICT")).trim().length()>0){
						hmDetail = new HashMap();//add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B
						
						//start modify huangqirong 2013-04-18 bug #7519 增加专用组合
						if("subject".equalsIgnoreCase(sSetType)){
							//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
							fieldCode = rs.getString("FSUBJECTFIELD");
							dictCode = rs.getString("FSUBJECTDICT");
							
							fieldBuf
							.append("(case when subdict" + pojo.count +".fcnvconent is not null then subdict" + (pojo.count))
							.append(".fcnvconent else subdict").append(pojo.count+1).append(".fcnvconent end )");
							//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
							
							pojo.fieldBuf
							.append("(case when subdict" + pojo.count +".fcnvconent is not null then subdict" + (pojo.count))
							.append(".fcnvconent else subdict").append(pojo.count+1).append(".fcnvconent end )");
						}else{
							//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
							fieldCode = rs.getString("FRESUMEFIELD");
							dictCode = rs.getString("FRESUMEDICT");
							//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
							
							//根据凭证字典取值，
							fieldBuf.append(sSetType.equalsIgnoreCase("subject")?"subdict":"resdict").append(pojo.count).append(".fcnvconent");
							
							pojo.fieldBuf.append(sSetType.equalsIgnoreCase("subject")?"subdict":"resdict").append(pojo.count).append(".fcnvconent");
						}
						
						//拼接连接语句
						joinBuf.setLength(0);
						
						if("subject".equalsIgnoreCase(sSetType)){
							joinBuf.append(" left join (select fcnvconent, findcode , FPortCode ");
							joinBuf.append(" from " + pub.yssGetTableName("tb_vch_dict") + " where fcheckstate = 1 "); 
							joinBuf.append(" and fdictcode =" + dbl.sqlString(rs.getString("FSUBJECTDICT"))); 
							joinBuf.append(" and FPortCode = ").append(dbl.sqlString(portCode));
							joinBuf.append(" ) subdict" + pojo.count + " on subdict" + pojo.count + ".findcode = ds." + rs.getString("FSUBJECTFIELD"));
							pojo.count++;
						}
						//end modify huangqirong 2013-04-18 bug #7519 增加专用组合
						joinBuf.append(" left join ");
						joinBuf.append(" (").append(" select fcnvconent,findcode from ").append(pub.yssGetTableName("tb_vch_dict"));
						joinBuf.append(" where fcheckstate=1 and fdictcode=")
						.append(dbl.sqlString(sSetType.equalsIgnoreCase("subject")?rs.getString("FSUBJECTDICT"):rs.getString("FRESUMEDICT")));
						joinBuf.append(sSetType.equalsIgnoreCase("subject") ? " and (FPortCode = ' ' or FPortCode is null )" : " ");//add huangqirong 2013-04-18 bug #7519
						joinBuf.append(" ) ").append(sSetType.equalsIgnoreCase("subject")?"subdict":"resdict").append(pojo.count).append(" on ");
						joinBuf.append(sSetType.equalsIgnoreCase("subject")?"subdict":"resdict").append(pojo.count).append(".findcode=ds.");
						joinBuf.append(sSetType.equalsIgnoreCase("subject")?rs.getString("FSUBJECTFIELD"):rs.getString("FRESUMEFIELD"));
						
						pojo.joinSqlArr.add(joinBuf.toString());
						
					    pojo.count++;
					    
					    //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
					    hmDetail.put("fieldSql", fieldBuf.toString());
					    hmDetail.put("joinSql", joinBuf.toString());
					    
					    pojo.hmSql.put(sSetType + " " + fieldCode + " " + dictCode, hmDetail);
					    //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
					}else{
						
						if(sSetType.equalsIgnoreCase("resume")&&((String)hmDsFieldType.get(rs.getString("FRESUMEFIELD").trim())).toLowerCase().equalsIgnoreCase("date")){
							pojo.fieldBuf.append(" to_char(").append(rs.getString("FRESUMEFIELD")).append(",'yyyy-MM-dd')");
						}else{
							pojo.fieldBuf.append(sSetType.equalsIgnoreCase("subject")?rs.getString("FSUBJECTFIELD"):rs.getString("FRESUMEFIELD"));
						}
					}
					
				}else if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
					//科目 静态
					if(pojo.fieldBuf.length()>0){
						pojo.fieldBuf.append("||");
					}
					
					pojo.fieldBuf.append("'").append(sSetType.equalsIgnoreCase("subject")?rs.getString("FSUBJECTCONENT"):rs.getString("FRESUMECONENT")).append("'");
				}
				
				//将pojo放到HashMap中,并置空Key值
				setCache.put(key.toString(), pojo);
				key.setLength(0);
			}
		} catch (Exception e) {
			throw new YssException("缓存科目和摘要设置数据出错......");
		}finally{
			key.setLength(0);
			val.setLength(0);
			joinBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 缓存原币、本币、数量设置数据
	 * @param query
	 * @param setCache
	 * @param sSetType
	 * @throws YssException
	 */
	private void SetMaVal(StringBuffer query,HashMap setCache,String sSetType)throws YssException{
		
		ResultSet rs = null;
		StringBuffer key = new StringBuffer();
		StringBuffer operSignValue = new StringBuffer();
		StringBuffer errBuf = new StringBuffer();
		SetBean pojo = null;
		try {

			rs = dbl.openResultSet(query.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE);

			if(!rs.next() && sSetType.equalsIgnoreCase("Money")){
				errBuf.append("系统查询不到原币设置数据，请核对后再生成凭证！");
				throw new YssException(errBuf.toString());
			}
			rs.beforeFirst();
			while (rs.next()) {

				//key ： 分录号\t设置类型
				key.append(rs.getString("fentitycode")).append("\t").append(sSetType);
				
				//获取设置Bean
				pojo = (SetBean)setCache.get(key.toString());
				if(pojo == null){
					pojo = new SetBean();
					operSignValue.setLength(0);
				}else{
					pojo.fieldBuf.append(operSignValue.toString());
					operSignValue.setLength(0);
				}
				
				//value:
				if(rs.getString("fvaluetype").equalsIgnoreCase("0")){
					//动态取值
					pojo.fieldBuf.append(" ").append(rs.getString("FMAFIELD")).append(" ");
					operSignValue.append(rs.getString("FOperSignValue"));
				}else if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
					//静态取值
					pojo.fieldBuf.append(" ").append(rs.getString("FMAConent")).append(" ");
					operSignValue.append(rs.getString("FOperSignValue"));
				}
				
				//将pojo放到HashMap中,并置空Key值
				setCache.put(key.toString(), pojo);
				key.setLength(0);
				
			}
		} catch (Exception e) {
			throw new YssException(errBuf.length()>0?errBuf.toString():"缓存原币、本币、数量设置数据出错......"+"\t "+e.getMessage());
		}finally{
			key.setLength(0);
			operSignValue.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
    /**
     * 缓存辅助核算设置数据
     * @param query
     * @param setCache
     * @param sSetType
     * @throws YssException
     */
	private void SetAssistantVal(StringBuffer query, HashMap setCache, String sSetType)
			throws YssException {
		ResultSet rs = null;
		StringBuffer key = new StringBuffer();
		StringBuffer joinBuf = new StringBuffer();
		SetBean pojo = null;
		//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
		HashMap hmDetail = null;//用于保存 joinSql 和 fieldSql
		String fieldCode = "";//数据源字段代码
		String dictCode = "";//凭证字典代码
		StringBuffer fieldBuf = new StringBuffer();//用于保存 通过凭证字典转换的获取数据源字段的 sql 
		//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
		try {
			rs = dbl.openResultSet(query.toString());
			while (rs.next()) {
				fieldBuf.setLength(0);//add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B
				
				//key ： 分录号\t设置类型
				key.append(rs.getString("fentitycode")).append("\t").append(sSetType);
				//获取设置Bean
				pojo = (SetBean)setCache.get(key.toString());
				if(pojo == null){
					pojo = new SetBean();
				}
				
				//value: 
				
				if(pojo.fieldBuf.length()>0){
					pojo.fieldBuf.append("||");
				}
				
				if(rs.getString("fvaluetype").equalsIgnoreCase("0")){
					if(rs.getString("fassistantdict") != null && 
					   !rs.getString("fassistantdict").equalsIgnoreCase("null") && 
					   rs.getString("fassistantdict").trim().length()>0){
						//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
						hmDetail = new HashMap();
						fieldCode = rs.getString("FAssistantField");
						dictCode = rs.getString("FAssistantDict");
						
						fieldBuf.append("assdict").append(pojo.count).append(".fcnvconent");
						//--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
						
						//根据凭证字典取值，
						pojo.fieldBuf.append("assdict").append(pojo.count).append(".fcnvconent");
						
						//拼接连接语句
						joinBuf.setLength(0);
						joinBuf.append(" left join ");
						joinBuf.append(" (").append(" select fcnvconent,findcode from ").append(pub.yssGetTableName("tb_vch_dict"));
						joinBuf.append(" where fcheckstate=1 and fdictcode=").append(dbl.sqlString(rs.getString("fassistantdict")));
						joinBuf.append(" ) assdict").append(pojo.count).append(" on assdict").append(pojo.count).append(".findcode=ds.");
						joinBuf.append(rs.getString("fassistantfield"));
						
						pojo.joinSqlArr.add(joinBuf.toString());
						
					    pojo.count++;
					    
					    //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
					    hmDetail.put("fieldSql", fieldBuf.toString());
					    hmDetail.put("joinSql", joinBuf.toString());
					    
					    pojo.hmSql.put(sSetType + " " + fieldCode + " " + dictCode, hmDetail);
					    //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B end---//
					}else{
						pojo.fieldBuf.append(rs.getString("fassistantfield"));
					}
				}else if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
					//静态直接取值
					pojo.fieldBuf.append("'").append(rs.getString("fassistantconent")).append("'");
				}
				
				//将pojo放到HashMap中,并置空Key值
				setCache.put(key.toString(), pojo);
				key.setLength(0);
			}
		} catch (Exception e) {
			throw new YssException("缓存辅助核算设置数据出错......");
		}finally{
			key.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
		
	}
	
    /**
     * 暂时不给出实现。这块在单行模式下没有用到
     * 缓存条件设置数据
     * @param query
     * @param setCache
     * @param sSetType
     * @throws YssException
     */
	private void SetCondVal(StringBuffer query, HashMap setCache, String sSetType)
			throws YssException {

		ResultSet rs = null;
		StringBuffer key = new StringBuffer();
		StringBuffer val = new StringBuffer();
		StringBuffer joinBuf = new StringBuffer();
		SetBean pojo = null;
		try {

			rs = dbl.openResultSet(query.toString());

			while (rs.next()) {
				//key ： 分录号\t设置类型
				key.append(rs.getString("fentitycode")).append("\t").append(sSetType);
				//获取设置Bean
				pojo = (SetBean)setCache.get(key.toString());
				if(pojo == null){
					pojo = new SetBean();
				}
				
				//value:
				if(rs.getString("fvaluetype").equalsIgnoreCase("0")){
					
					
					
					
				}else if(rs.getString("fvaluetype").equalsIgnoreCase("1")){
					
				}
				//将pojo放到HashMap中,并置空Key值
				setCache.put(key.toString(), pojo);
				key.setLength(0);
			}
		} catch (Exception e) {
			throw new YssException("缓存条件设置数据出错......");
		}finally{
			key.setLength(0);
			val.setLength(0);
			joinBuf.setLength(0);
			dbl.closeResultSetFinal(rs);
		}
		
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * 凭证设置内部类 
	 */
	private class SetBean {
		// ~ 凭证设置类
        private StringBuffer fieldBuf = new StringBuffer(); //字段
        private StringBuffer whereBuf = new StringBuffer();; //过滤
        private ArrayList joinSqlArr = new ArrayList();;  //连接SQL语句
        //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
        private HashMap hmSql = new HashMap();//用于保存 凭证设置类型 + 数据源代码 + 凭证字典代码  对应的 jionSql 语句  和 fieldSql 语句
        //--- add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B start---//
        private int count =1;//用于对连接SQL语句的计数
        
		public void SetBean() {
			
		}

		public void clean(){
			fieldBuf.setLength(0);
			whereBuf.setLength(0);
			joinSqlArr.clear();
			hmSql.clear();//add by songjie 2013.05.28 BUG 7786 QDV4建信2013年05月09日01_B
		}
	}
	
	
	
    /**
     *  生成凭证设置的查询语句
     * @param sSetType
     * subject: 	科目设置
     * resume：   	摘要设置
     * money：           	原币
     * setmoney：   	本币
     * amount：    	数量
     * assistant：	辅助核算项
     * cond:        过滤条件
     * @return
     */
    private StringBuffer BuilderSetQuery(String sSetType,String sVchTplCode){
    	
    	StringBuffer queryBuf = new StringBuffer();

    	if(sSetType.equalsIgnoreCase("subject")||sSetType.equalsIgnoreCase("resume")){
    		//获取凭证科目、凭证摘要设置
    		queryBuf.append(" select * from  ").append(pub.yssGetTableName(sSetType.equalsIgnoreCase("subject")?"tb_vch_entitysubject":"tb_vch_entityresume"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchTplCode)).append(" order by fentitycode,fordernum ");
    	}else if(sSetType.equalsIgnoreCase("money")||sSetType.equalsIgnoreCase("setmoney")||sSetType.equalsIgnoreCase("amount")){
    		//获取凭证原币、本币、数量设置
    		queryBuf.append(" select a.*,b.FVocName as FOperSignValue from ").append(pub.yssGetTableName("tb_vch_entityma")).append(" a ");
    		queryBuf.append("  left join Tb_Fun_Vocabulary b on a.FOperSign = b.FVocCode and b.FVocTypeCode =").append(dbl.sqlString(YssCons.YSS_OperSign));
    		queryBuf.append(" where  a.fcheckstate<>2 and a.fvchtplcode=").append(dbl.sqlString(sVchTplCode));
    		queryBuf.append(" and a.ftype=").append(dbl.sqlString(sSetType)).append(" order by a.fentitycode,a.fordernum ");
    	}else if(sSetType.equalsIgnoreCase("assistant")){
    		//获取凭证辅助核算项设置
    		queryBuf.append(" select fentitycode,fordernum,fvaluetype,fassistantconent,fassistantfield,fassistantdict from ");
    		queryBuf.append(pub.yssGetTableName("tb_vch_assistant"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchTplCode)).append(" order by fentitycode,fordernum");
    	}else if(sSetType.equalsIgnoreCase("cond")){
    		//获取凭证分录过滤条件设置
    		queryBuf.append(" select fentitycode,forderindex,fconrela,ffieldname,fsign,fvaluesource,fvalue from ");
    		queryBuf.append(pub.yssGetTableName("tb_Vch_EntityCond"));
    		queryBuf.append(" where  fcheckstate<>2 and fvchtplcode=").append(dbl.sqlString(sVchTplCode)).append(" order by fentitycode,forderindex");
    	}
    	
    	return queryBuf;
    }

	
	/**
	 *  初始化凭证模板设置
	 * @return
	 * @throws YssException
	 */
	private HashMap initVchTpl() throws YssException{
		
		HashMap vchTplSetMap = new HashMap();
		StringBuffer queryBuf = new StringBuffer();
		StringBuffer valuleBuf = new StringBuffer();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String[] vchTypes = null;
		try{
			vchTypes = this.getVchTypes().split(",");
			queryBuf.append(" select a.* from ").append(pub.yssGetTableName("Tb_Vch_VchTpl")).append(" a ");
			queryBuf.append(" left join ");
			queryBuf.append(pub.yssGetTableName("Tb_Vch_Attr")).append(" b ");
			queryBuf.append(" on a.FAttrCode = b.FAttrCode ");
			queryBuf.append(" where a.FCheckState = 1  and b.FCheckState = 1  and a.FMode = 'Single' ");
			queryBuf.append(" a.FAttrCode in ( ");
			for( int i =0;i<vchTypes.length;i++){
				queryBuf.append(i>0?",":"").append("?");
			}
			queryBuf.append(" )");
			queryBuf.append(" order by b.FSort, a.FSort ");
			
			pst = dbl.getPreparedStatement(queryBuf.toString());
			rs = pst.executeQuery();
			for( int i =1;i<=vchTypes.length;i++){
				pst.setString(i, vchTypes[i]);
			}
			while(rs.next()){
				valuleBuf.append(rs.getString("FAttrCode")).append("\t").append(rs.getString("FVchtWay")).append("\t").append(rs.getString("FDsCode"));
				vchTplSetMap.put(rs.getString("fvchtplcode").trim(), valuleBuf.toString());
			}
			
			return vchTplSetMap;
		}catch(Exception e){
			throw new YssException("获取凭证凭证模板出错......",e);
		}finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeStatementFinal(pst);
			
		}
	}
	
	/**
	 *  获取指定组合的凭证模板设置
	 * @param sPortCode
	 * @return key:模板代码  value: 凭证数据源
	 * @throws YssException
	 */
	private HashMap getVchTplPort(String sPortCode,HashMap valMap) throws YssException{
		
               return valMap;
	}
	
    /****************************************************************
     * add by jiangshichao 2011.11.23  鹏华基金2011年11月22日01_B 
     * @param attrCode
     * @param portCode
     * @param mode
     * @param vchtplcode
     * @return
     * @throws YssException
     */
    private boolean isExistsSpecialMode(String attrCode, String portCode,String mode, String vchtplcode,String dscode,String vchtWay) throws YssException{
    	
    	StringBuffer  buf = new StringBuffer();
    	ResultSet rs = null;
    	boolean flag=false;
    	String specialVchtplCode = "";
    	try{
    		if(vchtWay.indexOf("_")>-1){
    			vchtWay = vchtWay.substring(0, vchtWay.indexOf("_"));
    		}
    		
    		//这里暂时通过数据源和凭证属性来作为判断凭证的类别
    		buf.append(" select a.fvchtplcode,a.fvchtplname,a.fportcode,a.fattrcode,a.fmode,a.fdscode,a.FVchtWay from ").append(pub.yssGetTableName("Tb_Vch_VchTpl"));
    		buf.append(" a where a.FCheckState = 1").append(" and a.FAttrCode =").append(dbl.sqlString(attrCode)).append(" and a.FMode =").append(dbl.sqlString(mode));
    		buf.append(" and a.fdscode=").append(dbl.sqlString(dscode)).append(" AND a.FVchtWay like'"+vchtWay+"%'");
    		
    		rs = dbl.openResultSet(buf.toString());
    		
    		while(rs.next()){
    			//当前组合有专用模板,获取专用凭证模板代码
    			if(portCode.equalsIgnoreCase(rs.getString("fportcode"))  ){
    				specialVchtplCode = rs.getString("fvchtplcode");
    			}
    		}
    		//当前组合有专用代码且如果当前执行的凭证模板不是专用模板，则跳过不执行凭证生成操作
    		if(specialVchtplCode.length()>0&& !vchtplcode.equalsIgnoreCase(specialVchtplCode)){
    			flag = true;
    		}
    		return flag;
    	}catch(Exception e ){
    		throw new YssException("检测组合专用模板出错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
	
	
}
