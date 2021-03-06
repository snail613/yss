package com.yss.main.operdeal.voucher.vchbuild;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.core.util.YssFun;
import com.yss.log.SingleLogOper;
import com.yss.main.voucher.*;
import com.yss.manager.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: VchBuildMulti</p>
 * <p>Description: 生成多行取数的凭证</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchBuildMulti
    extends BaseVchBuild {
	public VchBuildMulti() {
    }

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
        //---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        String logInfo = "";
        SingleLogOper logOper = SingleLogOper.getInstance();
        Date logStartTime = null;//业务子项开始时间
        String portCode = "";//组合代码
        String vchTplCode = "";//凭证模板代码
        //---add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            vchAdmin.setYssPub(pub);
            portAry = this.getPortCodes().split(",");
            for (int i = 0; i < portAry.length; i++) {
//                strSql =
//                    " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
//                    " where FAttrCode in (" + operSql.sqlCodes(this.getVchTypes()) +
//                    ") and FCheckState = 1 and FMode = 'Multi'  " +
//                    //" and ((FPortCode is null or FPortCode='') or FPortCode=" +
//                    " and ((FPortCode is null or FPortCode='' or FPortCode=' ') or FPortCode=" + //BUG:0000349
//                    dbl.sqlString(portAry[i]) + ")" + //增加对专用组合的处理 by liyu 080326;
//                    " order by fvchtplcode";
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
                    " and a.FMode = 'Multi'  " +
                    " and (a.FPortCode=" + dbl.sqlString(portAry[i]) +
                    " or (a.FPortCode is null or a.FPortCode='' or a.FPortCode=' '))" +
                    " order by b.FSort, a.FSort";
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	            	logInfo = "";
	            	logStartTime = new Date();
	            	vchTplCode = "凭证模版【" + rs.getString("FVchTplCode") +"】";
	            	portCode = portAry[i];
	            	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
	            	
                    if (showText) {
                        runStatus.appendRunDesc("VchRun", "开始生成组合【" + portAry[i] +
                                                "】的凭证... ...\r\n");
                        showText = false;
                        showEnd = true;
                    }
                    
                    //add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    logInfo += "开始生成【" + rs.getString("FVchTplName") + "】模版的凭证... ...\r\n";
                    
                    runStatus.appendRunDesc("VchRun",
                                            "开始生成【" +
                                            rs.getString("FVchTplName") +
                                            "】模版的凭证... ...\r\n", "      ");
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
                    doMultiVch(rs.getString("FVchTplCode"), strSql, hmDsFieldType,
                               portAry[i]); //增加了组合设置 sj
                    dbl.closeResultSetFinal(rsDs);
                    //delete by jiangshichao  提示信息不准确  STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’
                    //runStatus.appendRunDesc("VchRun", "生成凭证成功！");
                    
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
                }
                dbl.closeResultSetFinal(rs);
                if (showEnd) {
                    runStatus.appendRunDesc("VchRun", "生成组合【" + portAry[i] + "】的凭证完成！");//modify by jsc 20120315
                    showText = true;
                    showEnd = false;
                }
                //runStatus.appendRunDesc("VchRun","-----------------------------------------------------------");
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
            					(logInfo + " \r\n 组合【" + portCode + "】的多行取数凭证生成失败  \r\n " + e.getMessage())
            					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),//处理日志信息 除去特殊符号
            					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
            					logStartTime,this.getLogSumCode(),new Date());
            		}
        		}
        		runStatus.appendRunDesc("VchRun", "生成模版的凭证失败！... ...\r\n");
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        	//---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
            finally{//添加 finally 保证可以抛出异常
                //by 曹丞 2009.02.02 批量生成多行取数凭证异常信息 MS00004 QDV4.1-2009.2.1_09A
                throw new YssException("系统批量生成多行取数凭证时出现异常!" + "\n", e); 
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void doMultiVch(String sVchTplCode, String sDsSql,
                              HashMap hmDsFieldType, String portCode) throws
        YssException {
        ResultSet rsTpl = null;
        ResultSet rsDs = null;
        ResultSet rsDsSub = null;
        ResultSet rs = null;
        ResultSet rsCond = null;
        String strSql = "";
        ArrayList alCond = new ArrayList();
        ArrayList alSubCond = new ArrayList();
        YssWhereCond cond = null;
        String sTplFields = "";
        String[] sTplFieldAry = null;
        ArrayList subAddList = null;

        VchDataEntityBean vchDataEntity = null;

        YssFinance fc = new YssFinance();

        VoucherAdmin vchAdmin = new VoucherAdmin();
        VchDataBean vchData = null;
        //-------轧差分录所需的字段 sj 20080219 -----//
        VchDataEntityBean gcVchDataEntity = null; //轧差所需的对象
        java.util.Date gcVchData = null; //设置分录摘要所需的日期
        ArrayList aVchData = null; //计算轧差数据时需要放置凭证的容器
        //------------------------------------------
        try {
            vchAdmin.setYssPub(pub);
            fc.setYssPub(pub);

            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FVchTplCode = " +
                dbl.sqlString(sVchTplCode) +
                //" and ((FPortCode is null or FPortCode='') or FPortCode=" +
                " and ((FPortCode is null or FPortCode='' or FPortCode=' ') or FPortCode=" +
                dbl.sqlString(portCode) + ")"; //增加对专用组合的处理 by liyu 080326;BUG:0000349
            rsTpl = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
            if (rsTpl.next()) {
                sTplFields = rsTpl.getString("FFields");

                if (sTplFields != null) { //先获取凭证模板中的条件
                    strSql = "select distinct " + sTplFields +
                        " from (" + sDsSql + ")";
                    rsDs = dbl.openResultSet(strSql);
                    while (rsDs.next()) {
                        sTplFieldAry = sTplFields.split(",");
                        alCond.clear();
                        for (int i = 0; i < sTplFieldAry.length; i++) {
                            cond = new YssWhereCond();
                            cond.setField(sTplFieldAry[i]);
                            cond.setSign("=");
                           // cond.setValue(rsDs.getString(cond.getField()));
                            cond.setValue(sqlValue(hmDsFieldType, cond.getField(),rsDs.getString(cond.getField()),
                                rsDs));//modify by jsc 20120315
		                    if (i < sTplFieldAry.length - 1) {
		                            cond.setRela("and");
		                    }
                            alCond.add(cond);
                        }

//                  strSql = buildMuiltiSql(sDsSql, alCond);
//                  rsDs = dbl.openResultSet(strSql); //打开数据源记录集，SQL语句中加入凭证模板上的条件

                        strSql = "select * from " +
                            pub.yssGetTableName("Tb_Vch_Entity") +
                            " where FCheckState = 1 and FVchTplCode = " +
                            dbl.sqlString(sVchTplCode);
                        rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788 //打开凭证分录设置表
//                  while (rsDs.next()) {
                        vchData = new VchDataBean();
                        subAddList = new ArrayList();
                        vchData.setYssPub(pub);
                        vchData.setTplCode(rsTpl.getString("FVchTplCode"));
                        //----------------------设置组合 sj ---------------------------//
                        vchData.setPortCode(portCode);
                        //------------------------------------------------------------
                        rs.beforeFirst();
                        while (rs.next()) { //循环分录设置
                            alSubCond.clear();
                            alSubCond.addAll(alCond);
                            //---------------计算方式为本位币轧差的分录的设置 sj -----------//
                            if (rs.getString("FCALCWAY").equalsIgnoreCase("NettingSet")) {
                                gcVchDataEntity = new VchDataEntityBean();
                                setGcVchDataBean(gcVchDataEntity,
                                                 gcVchData,
                                                 rs,
                                                 hmDsFieldType, portCode); //新增的
                                gcVchDataEntity.setSAllow(rs.getString("FAllow")); //将凭证分录中的允许执行的操作符传到方法里面 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
                            }
                            //----------------------------------------------------------//
                            strSql = " select * from " +
                                pub.yssGetTableName("Tb_Vch_EntityCond") +
                                " where  FVchTplCode = " +
                                dbl.sqlString(sVchTplCode) +
                                " and FCheckState = 1 and FEntityCode = " +
                                dbl.sqlString(rs.getString("FEntityCode")) +
                                " order by FOrderIndex";
                            rsCond = dbl.openResultSet(strSql);
                            while (rsCond.next()) { //循环分录条件
                                cond = new YssWhereCond();
                                cond.setField(rsCond.getString("FFieldName"));
                                
                                //20120917 added by liubo.Story #2782
                                //凭证分录里面的条件栏位，运算符号存取的是编号，而不是实际的符号，如此就可能造成条件里设置的是FF='ABC'，在生成凭证时实际取值取成FF0'ABC'的情况，从而造成无效运算符的错误
                                //======================================
//                                cond.setSign(rsCond.getString("FSign"));
                                cond.setSign(this.getSignValue(rsCond.getString("FSign")));
                              //================end======================
                                
//                        cond.setValue(rsCond.getString("FValue"));
                                cond.setValue(sqlValue(hmDsFieldType, cond.getField(),
                                    rsCond.getString("FValue")));
                                cond.setRela(rsCond.getString("FConRela"));
                                alSubCond.add(cond);
                            }
                            dbl.closeResultSetFinal(rsCond);

                            strSql = buildMuiltiSql(sDsSql, alSubCond);
                            rsDsSub = dbl.openResultSet(strSql); //打开数据源记录集，SQL语句中加入凭证模板和单条分录上的条件
                            while (rsDsSub.next()) {
                                vchData.setVchDate(rsDsSub.getDate(rsTpl.getString(
                                    "FDateField")));
                                if (gcVchData == null) {
                                    gcVchData = vchData.getVchDate();
                                }
//               vchData.setBookSetCode(fc.getCWSetCode(vchData.getPortCode()));
                                vchData.setSrcCuryCode(rsDsSub.getString(rsTpl.
                                    getString(
                                        "FSrcCury")));

                                vchDataEntity = new VchDataEntityBean();
                                vchDataEntity.setYssPub(pub);
                                vchDataEntity.setSAllow(rs.getString("FAllow")); //将凭证分录中的允许执行的操作符传到方法里面 QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
                                vchDataEntity.setEntityCode(rs.getString(
                                    "FEntityCode"));
                                vchDataEntity.setSubjectCode(
                                    this.getEntitySubject(rs.getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, hmDsFieldType, portCode)); //增加的
                                vchDataEntity.setResume(
                                    this.getEntityResume(rs.getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, hmDsFieldType, portCode)); //增加的
                                vchDataEntity.setDcWay(rs.getString("FDCWay"));
//                  vchDataEntity.setBookSetCode(rsTpl.getString("FBookSetCode"));
                                vchDataEntity.setBal(this.getEntityMA(rs.getString(
                                    "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, "Money", null));
//                  dPortRate = this.getSettingOper().getCuryRate(vchData.getVchDate(),rs.getString("FCuryCode"),vchData.getPortCode(),YssOperCons.YSS_RATE_BASE);
                                vchDataEntity.setSetBal(this.getEntityMA(rs.
                                    getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, "SetMoney", vchDataEntity));
                                
                                vchDataEntity.setAmount(this.getEntityMA(rs.
                                    getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, "Amount", null));
                                if (rs.getString("FPriceField") != null) {
                                    vchDataEntity.setPrice(rsDsSub.getDouble(rs.
                                        getString(
                                            "FPriceField")));
                                }
                                vchDataEntity.setAssistant(
                                    getAssistant(
                                        rs.getString("FVchTplCode"),
                                        rs.getString("FEntityCode"),
                                        rsDsSub, hmDsFieldType, portCode) // 增加的
                                    );
                                vchDataEntity.setCalcWay(rs.getString("FCALCWAY")); //为了在之后的计算轧差值,设置计算方式。sj edit 20080219
                                subAddList.add(vchDataEntity);
                                vchData.setDataEntity(subAddList);
                            }
                            dbl.closeResultSetFinal(rsDsSub);
                        }
                        //setPortSet(rsTpl.getString("FLinkCode"), vchData,
                        //vchAdmin);
                        //----------如果存在轧差分录对象，将其放入ArrayList，并将此凭证放入
                        //----------一个ArrayList，以便计算轧差值 sj --------------------//
                        if (gcVchDataEntity != null) {
                            subAddList.add(gcVchDataEntity);
                            aVchData = new ArrayList();
                            aVchData.add(vchData);
                            adjustTail(aVchData); //通过方法计算轧差值 sj 20080219
                        }
                        //==========QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
                        //------------------------------------------------------------//
//---------------通过checkDataEntity方法判断所有的分录中的金额和数量是否都为零,若是则不录入这条凭证--sj edit 20080327----
//                  if (checkDataEntity(subAddList).equalsIgnoreCase("false")) {
//                     vchData.setDataEntity(subAddList);
//                     setPortSet(rsTpl.getString("FLinkCode"), vchData, vchAdmin);
//                  }
                        subAddList = checkDataEntitys(subAddList); //用新方法逐条检查处理凭证
                        if (subAddList.size() > 0) { //判断若分录个数大于0，再执行下面的操作
                            vchData.setDataEntity(subAddList);
                            setPortSet(rsTpl.getString("FLinkCode"), vchData, vchAdmin);
                        }
//---------------------------------------------------------------------------------------------------------------
                        //===============QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209

                    }
                }
//            }
            }

            vchAdmin.insert();
            
          //--- STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’ add by jiangshichao 2011.08.20  start
            if(vchAdmin.getAddList().size()>0){
            	runStatus.appendRunDesc("VchRun", "生成凭证成功!"); 
            }else{
            	runStatus.appendRunDesc("VchRun", "当日无业务!"); 
            }
          //--- STORY #1287 建议是去掉凭证字典检查的过程，没有生成凭证数据的模板显示‘当日无业务’ add by jiangshichao 2011.08.20   end 
        } catch (Exception e) {
            throw new YssException("系统生成多行取数凭证时出现异常!" + "\n", e); //by 曹丞 2009.02.02 生成多行取数凭证异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rsTpl);
            dbl.closeResultSetFinal(rsDs);
            dbl.closeResultSetFinal(rsDsSub);
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsCond);
        }
    }

    //产生凭证的多行取数模式时生成分录的SQL语句   胡昆  20070916
    private String buildMuiltiSql(String sDsSql, ArrayList alCond) {
        YssWhereCond cond = null;
        StringBuffer buf = new StringBuffer();
        sDsSql = "select * " + " from (" + sDsSql + ")";
        if (alCond.size() > 0) {
            buf.append(" where ");
            for (int i = 0; i < alCond.size(); i++) {
                cond = (YssWhereCond) alCond.get(i);
                if(i>0){
                	buf.append(" and ");
                }
                buf.append(cond.toString());
            }
            sDsSql += buf.toString();
        }
        return sDsSql;
    }

    private void setGcVchDataBean(VchDataEntityBean gcVchDataEntity,
                                  java.util.Date gcVchData,
                                  ResultSet rs,
                                  HashMap hmDsFieldType, String sPortCode) throws //新增的
        SQLException,
        SQLException, YssException {
        String Resume = "";
        try {
            gcVchDataEntity.setEntityCode(rs.getString(
                "FEntityCode"));
            gcVchDataEntity.setSubjectCode(
                this.getEntitySubject(rs.getString(
                    "FVchTplCode"),
                                      rs.getString("FEntityCode"),
                                      null, hmDsFieldType, sPortCode)); // 新增的
            Resume = this.getEntityResume(rs.getString(
                "FVchTplCode"),
                                          rs.getString("FEntityCode"),
                                          null, hmDsFieldType, sPortCode).
                replaceAll("null", //新增的
                           "");
            gcVchDataEntity.setResume(YssFun.formatDate(gcVchData) +
                                      Resume);
            gcVchDataEntity.setDcWay(rs.getString("FDCWay"));

            gcVchDataEntity.setBal(this.getEntityMA(rs.getString(
                "FVchTplCode"),
                rs.getString("FEntityCode"),
                null, "Money", null));

            gcVchDataEntity.setSetBal(this.getEntityMA(rs.
                getString(
                    "FVchTplCode"),
                rs.getString("FEntityCode"),
                null, "SetMoney", gcVchDataEntity));
            gcVchDataEntity.setAmount(this.getEntityMA(rs.
                getString(
                    "FVchTplCode"),
                rs.getString("FEntityCode"),
                null, "Amount", null));
            gcVchDataEntity.setAssistant(
                getAssistant(
                    rs.getString("FVchTplCode"),
                    rs.getString("FEntityCode"),
                    null, hmDsFieldType, sPortCode) //新增的
                );
            gcVchDataEntity.setCalcWay(rs.getString("FCALCWAY"));
        } catch (Exception e) {
            throw new YssException("设置轧差分录数据出错!" + "\n", e);
        }
    }

    private String sqlValue(HashMap hmDsFieldType, String sField, String sValue) {
        String sResult = "";
        //20120916 modified by liubo.Story #2782
        //=======================================
        if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("VARCHAR2") ||  ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("Char")) {
            sResult = dbl.sqlString(sValue);
        //===================end====================
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("DATE")) {
            sResult = dbl.sqlDate(sValue);
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("NUMBER")) {
            sResult = sValue;
        }
        return sResult;
    }

    /***************************************************************************
     *  add by jsc 201203015  处理日期类型
     * @param hmDsFieldType
     * @param sField
     * @param sValue
     * @return
     * @throws SQLException 
     */
    private String sqlValue(HashMap hmDsFieldType, String sField, String sValue,ResultSet rs) throws SQLException {
        String sResult = "";
        if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("VARCHAR2")) {
            sResult = dbl.sqlString(sValue);
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("DATE")) {
            sResult = dbl.sqlDate(rs.getDate(sField));
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("NUMBER")) {
            sResult = sValue;
        }
        return sResult;
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
    
    //20120917 added by liubo.Story #2782
    //凭证分录里面的条件栏位，运算符号存取的是编号，而不是实际的符号，如此就可能造成条件里设置的是FF='ABC'，在生成凭证时实际取值取成FF0'ABC'的情况，从而造成无效运算符的错误
    //======================================
    private String getSignValue(String sFSign) throws YssException
    {
    	String sReturn = sFSign;
    	String strSql = "";
    	ResultSet rs = null;
    	
    	try
    	{
    		strSql = "select * from tb_fun_vocabulary where fvoctypecode = 'vch_Sign'";
    		
    		rs = dbl.queryByPreparedStatement(strSql);
    		
    		while(rs.next())
    		{
    			if (rs.getString("FVocCode").equals(sFSign))
    			{
    				sReturn = rs.getString("FVocName"); 
    				break;
    			}
    		}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException();
    	}
    	finally
    	{
    		dbl.closeResultSetFinal(rs);
    	}
    	
    	return sReturn;
    }
    //======================================
    
}
