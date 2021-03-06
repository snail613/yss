package com.yss.main.operdeal.voucher.vchbuild;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.core.util.YssFun;
import com.yss.log.SingleLogOper;
import com.yss.main.operdeal.*;
import com.yss.main.voucher.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: VchBuildSingle</p>
 * <p>Description: 生成单行取数的凭证</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchBuildSingle
    extends BaseVchBuild {
    public VchBuildSingle() {
    }

    ArrayList alRepParam = new ArrayList();

    public void doVchBuild() throws YssException {
        //1.根据需要产生的凭证类型从凭证模板设置表中取出记录
        //parse();
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsDs = null;
        HashMap hmDsFieldType = null;
        VoucherAdmin vchAdmin = new VoucherAdmin();
        String[] portAry = null;
        boolean showText = true;
        boolean showEnd = false;
        
        //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        String logInfo = "";
        SingleLogOper logOper = SingleLogOper.getInstance();
        Date logStartTime = null;//业务子项开始时间
        String portCode = "";//组合代码
        String vchTplCode = "";//凭证模板代码
        String vchTplCode1 = "";
        //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        //
        try {
            vchAdmin.setYssPub(pub);
            portAry = this.getPortCodes().split(",");
            for (int i = 0; i < portAry.length; i++) {
			    //add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            	portCode = 	portAry[i];
//                strSql =
//                    " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
//                    " where FAttrCode in (" + operSql.sqlCodes(this.getVchTypes()) +
//                    ") and FCheckState = 1 and FMode = 'Single'  " +
//                    " and (FPortCode=" + dbl.sqlString(portAry[i]) +
//                    //" or (FPortCode is null or FPortCode=''))" +
//                    " or (FPortCode is null or FPortCode='' or FPortCode=' '))" +
//                    " order by fvchtplcode"; //增加对专用组合的处理 by ly BUG:0000349
            	// 357 QDV4赢时胜（深圳）2010年11月29日03_A by qiuxufeng 20110217
            	// 生成凭证顺序按照凭证属性和凭证模板的手动排序后的顺序生成，先按照凭证属性后按照凭证模板的排序顺序生成凭证
            	strSql =
                    " select a.* from " +
                    pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
                    " left join " +
                    pub.yssGetTableName("Tb_Vch_Attr") + " b" +
                    " on a.FAttrCode = b.FAttrCode" +
                    " where a.FAttrCode in (" + operSql.sqlCodes(this.getVchTypes()) + ")" +
                    " and a.FCheckState = 1" +
                    " and b.FCheckState = 1" +
                    " and a.FMode = 'Single'  " +
                    " and (a.FPortCode=" + dbl.sqlString(portAry[i]) +
                    " or (a.FPortCode is null or a.FPortCode='' or a.FPortCode=' '))" +
                    " order by b.FSort, a.FSort";
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	            	logInfo = "";
	            	logStartTime = new Date();
	            	vchTplCode = "凭证模版【" + rs.getString("FVchTplCode") +"】";
	            	vchTplCode1 = rs.getString("FVchTplCode");
	            	portCode = portAry[i];
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                	
                    if (showText) {
                        runStatus.appendRunDesc("VchRun",
                                                "开始生成组合【" + portAry[i] +
                                                "】的凭证... ...\r\n", "      ");
                        showText = false;
                        showEnd = true;
                    }
                    
                    //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logInfo += "开始生成【" + rs.getString("FVchTplName") + "】模版的凭证... ...\r\n";
                    
                    runStatus.appendRunDesc("VchRun", "开始生成【" + rs.getString("FVchTplName") + "】模版的凭证... ...", "      ");
                    
                    /**shashijie 2011.05.18 判断是否有专用组合的专用凭证,STORY #429 希望优化凭证模板的使用方案*/
//                    if (isHaveSpecialPortCode(rs.getString("FAttrCode"),portAry[i]
//                    		,"Single",rs.getString("FVchtWay"))) {
//						continue;
//					}
                    /**end*/
                 // add by jiangshichao 2011.11.23  鹏华基金2011年11月22日01_B start
					if (isExistsSpecialMode(rs.getString("FAttrCode"),
							portAry[i], "Single", rs.getString("fvchtplcode"),rs.getString("FDsCode"),rs.getString("FVchtWay"))) {
						continue;
					}
					
                    strSql = buildVchDsSql(rs.getString("FDsCode"), portAry[i]);
                    rsDs = dbl.openResultSet(strSql);
                    hmDsFieldType = dbFun.getFieldsType(rsDs);

                    doSingleVch(rs.getString("FVchTplCode"), rsDs, hmDsFieldType,
                                portAry[i]);
                    dbl.closeResultSetFinal(rsDs);
                    
                    //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    runStatus.appendRunDesc("VchRun","      【" +rs.getString("FVchTplName") +"】模版的凭证已生成\r\n\r\n", "      ");
                    logInfo += "【" +rs.getString("FVchTplName") +"】模版的凭证已生成\r\n\r\n";
                    
                    if(this.getLogSumCode().trim().length() > 0){
                		//edit by songjie 2012.11.20 添加非空判断
                		if(logOper != null){
                			logOper.setDayFinishIData(this, 8,vchTplCode, pub, false, portCode, 
                					YssFun.toDate(this.getBeginDate()),YssFun.toDate(this.getBeginDate()),
                					YssFun.toDate(this.getEndDate()),logInfo,logStartTime,
                					this.getLogSumCode(),new Date());
                		}
                    }
                    //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    //delete by jiangshichao  提示信息不准确  STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’
                    //runStatus.appendRunDesc("VchRun", "生成凭证成功!"); 
                }
                dbl.closeResultSetFinal(rs);
                if (showEnd) {
                	//edit by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A 添加 单行取数
                    runStatus.appendRunDesc("VchRun", "生成组合【" + portAry[i] + "】的单行取数凭证完成！\r\n");
                    //runStatus.appendRunDesc("VchRun","-----------------------------------------------------------");
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
            					(logInfo + " \r\n 组合【" + portCode + "】的单行取数凭证生成失败  \r\n " + e.getMessage())
            					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),////处理日志信息 除去特殊符号
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            					logStartTime, this.getLogSumCode(),new Date());
            		}
        		}
        		
        		runStatus.appendRunDesc("VchRun", "生成组合【" + portCode + "】模版【" + vchTplCode1 + "】的凭证失败!... ...\r\n");
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	
        	//edit by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
            finally{//添加 finally 保证可以抛出异常
                //by 曹丞 2009.02.02 批量生成单行取数凭证异常信息 MS00004 QDV4.1-2009.2.1_09A
                throw new YssException("系统批量生成单行取数凭证时出现异常!" + "\n", e); 
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

	protected void doSingleVch(String sVchTplCode, ResultSet rsDs,
                               HashMap hmDsFieldType, String portCode) throws
        YssException {
        String strSql = "";
        String sAllow = ""; //QDV4深圳2009年01月15日02_B MS00194 by leeyu
        ResultSet rs = null;
        ResultSet rsTpl = null;
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        VchDataBean vchData = null;
        VchDataEntityBean vchDataEntity = null;
        VoucherAdmin vchAdmin = new VoucherAdmin();
        ArrayList subAddList = null;
        
        try {

            BaseOperDeal operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);

            YssFinance fc = new YssFinance();
            fc.setYssPub(pub);

            vchAdmin.setYssPub(pub);
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FVchTplCode = " +
                dbl.sqlString(sVchTplCode) +
                //" and ((FPortCode is null or FPortCode='')or FPortCode=" +
                " and ((FPortCode is null or FPortCode='' or FPortCode=' ')or FPortCode=" +
                dbl.sqlString(portCode) + ")"; //添加对专用组合的处理 by liyu 080326 BUG:0000349
            rsTpl = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788

            strSql = " select * from " +
                pub.yssGetTableName("Tb_Vch_Entity") +
                " where  FVchTplCode = " + dbl.sqlString(sVchTplCode) +
                " and FCheckState = 1 order by FEntityCode";
            rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788

            //循环数据源中的记录，每条记录产生一张凭证
            while (rsDs.next()) {
                while (rsTpl.next()) {

                    vchData = new VchDataBean();
                    subAddList = new ArrayList();
                    vchData.setYssPub(pub);
                    vchData.setVchDate(rsDs.getDate(rsTpl.getString("FDateField")));
//               vchData.setPortCode(rsDs.getString("FPortCode")); //临时这样修改
                    vchData.setBookSetCode(fc.getCWSetCode(vchData.getPortCode()));
                    vchData.setSrcCuryCode(rsDs.getString(rsTpl.getString("FSrcCury")));

                    vchData.setTplCode(rsTpl.getString("FVchTplCode"));
                    //----------------------设置组合 sj ---------------------------//
                    vchData.setPortCode(portCode);
                    //------------------------------------------------------------
                    while (rs.next()) {
                        vchDataEntity = new VchDataEntityBean();
                        vchDataEntity.setYssPub(pub);
                        vchDataEntity.setSAllow(rs.getString("FAllow")); //将凭证分录中的允许执行的操作符传到方法里面 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
                        vchDataEntity.setEntityCode(rs.getString("FEntityCode"));
                        vchDataEntity.setSubjectCode(
                            this.getEntitySubject(rs.getString("FVchTplCode"),
                                                  rs.getString("FEntityCode"),
                                                  rsDs, hmDsFieldType, portCode)); //新增的
                        vchDataEntity.setResume(
                            this.getEntityResume(rs.getString(
                                "FVchTplCode"),
                                                 rs.getString("FEntityCode"),
                                                 rsDs, hmDsFieldType, portCode)); //新增的
                        vchDataEntity.setCalcWay(rs.getString("FCalcWay"));
                        vchDataEntity.setDcWay(rs.getString("FDCWay"));
//                  vchDataEntity.setBookSetCode(rsTpl.getString("FBookSetCode"));
//                  vchDataEntity.setCalcWay(rs.getString("FCalcWay"));//设置分录的计算方式
                        vchDataEntity.setBal(this.getEntityMA(rs.getString(
                            "FVchTplCode"),
                            rs.getString("FEntityCode"),
                            rsDs, "Money", null));

//                  dPortRate = this.getSettingOper().getCuryRate(vchData.getVchDate(),rs.getString("FCuryCode"),vchData.getPortCode(),YssOperCons.YSS_RATE_BASE);
                        vchDataEntity.setSetBal(this.getEntityMA(rs.getString(
                            "FVchTplCode"),
                            rs.getString("FEntityCode"),
                            rsDs, "SetMoney", vchDataEntity));
                      
                        vchDataEntity.setAmount(this.getEntityMA(rs.getString(
                            "FVchTplCode"),
                            rs.getString("FEntityCode"),
                            rsDs, "Amount", null));
                        //2008.01.22 修改 蒋锦 添加长度为0的判断
                        if (rs.getString("FPriceField") != null &&
                            rs.getString("FPriceField").trim().length() != 0) {
                            vchDataEntity.setPrice(rsDs.getDouble(rs.getString(
                                "FPriceField")));
                        }
                        vchDataEntity.setAssistant(
                            getAssistant(
                                rs.getString("FVchTplCode"),
                                rs.getString("FEntityCode"),
                                rsDs, hmDsFieldType, portCode) //新增的
                            );
                        subAddList.add(vchDataEntity);
                    }
                    rs.beforeFirst();
                    //========QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
//-------------------通过checkDataEntity方法判断所有的分录中的金额和数量是否都为零,若是则不录入这条凭证--sj edit 20080327
//               if (checkDataEntity(subAddList).equalsIgnoreCase("false")) {
//                  vchData.setDataEntity(subAddList);
//                  setPortSet(rsTpl.getString("FLinkCode"), vchData, vchAdmin);
//               }
                    subAddList = checkDataEntitys(subAddList); //先逐条检查处理凭证
                    if (subAddList.size() > 0) { //判断若分录个数大于0，再执行下面的操作
                        vchData.setDataEntity(subAddList);
                        setPortSet(rsTpl.getString("FLinkCode"), vchData, vchAdmin);
                    }
//--------------------------------------------------------------------------------------------------------------
                    //======QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
//               vchAdmin.addList(vchData);
                }
                rsTpl.beforeFirst();
                
            }
            adjustTail(vchAdmin.getAddList());
            //---- MS00459 QDV4赢时胜（上海）2009年5月19日01_B sj --------------------
            this.checkAdjustDataEntitys(vchAdmin.getAddList()); //检查轧差类型的分录数据,将不符合的分录去除。
            //---------------------------------------------------------------------

            //2009-05-26 蒋锦 添加 MS00006 QDV4.1赢时胜上海2009年2月1日05_A 多用户并发优化
            //为以下两张表表加锁，以免在多用户同时处理时出现交易编号重复
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Vch_Data"));
            dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Vch_DataEntity"));
            vchAdmin.insert();
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
           //--- STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’ add by jiangshichao 2011.08.20  start
            if(vchAdmin.getAddList().size()>0){
            	runStatus.appendRunDesc("VchRun", "生成凭证成功!"); 
            }else{
            	runStatus.appendRunDesc("VchRun", "当日无业务!"); 
            }
          //--- STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’ add by jiangshichao 2011.08.20   end 
            
        } catch (Exception e) {
            throw new YssException("系统生成单行取数凭证时出现异常!" + "\n", e); //by 曹丞 2009.02.02 单行取数凭证异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsTpl);
            dbl.closeResultSetFinal(rsDs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**shashijie,2011-5-18 判断是否有专用组合的专用凭证,STORY #429 希望优化凭证模板的使用方案
     * @param 参数: attrCode属性代码 
     * @param : portCode 组合
     * @param : mode 模式
     * @param : vchtWay 凭证交易方式*/
    private boolean isHaveSpecialPortCode(String attrCode, String portCode,
			String mode, String vchtWay) throws YssException {
		ResultSet rs = null;
		String strSql = "";
		try {
			strSql = " SELECT a.* FROM "+pub.yssGetTableName("Tb_Vch_VchTpl")+" a "+
				   " LEFT JOIN "+pub.yssGetTableName("Tb_Vch_Attr")+" b ON a.FAttrCode = b.FAttrCode "+
				   " WHERE a.FAttrCode = "+dbl.sqlString(attrCode)+
				   " AND a.FCheckState = 1 "+
				   " AND b.FCheckState = 1 "+
				   " AND a.FMode = "+dbl.sqlString(mode)+
				   " AND a.FPortCode = "+dbl.sqlString(portCode)+
				   " AND a.FVchtWay = "+dbl.sqlString(vchtWay+"_zy");
			rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			throw new YssException(e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return false;
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
