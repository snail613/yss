package com.yss.main.etfoperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.etfoperation.pojo.ETFParamSetBean;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * <p>
 * Title: 20091116 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
 * </p>
 *
 * <p>
 * Description: 参数设置操作类
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ETFParamSetAdmin
    extends BaseDataSettingBean implements
    IDataSetting {
    private ETFParamSetBean etfParam = null;//参数设置实体bean
    private String sRecycled = "";//回收站删除数据用

    public ETFParamSetAdmin() {
    }
    
    /**输入检查：新增，修改，复制数据的检查数据是否存在主键数据 */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_ETF_Param"),
                               "FPortCode", this.etfParam.getPortCode(), this.etfParam
                               .getOldPortCode());
    }
    
    /**新增一天数据到数据库中 */
    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into "
                + pub.yssGetTableName("Tb_ETF_Param")
                + "(FPortCode,FMktSrcCode,FOneGradeMktCode,FTwoGradeMktCode,FNormScale,"
                + "FETFSeat,FSupplyMode,FCapitalCode,FCashAccCode,FBeginSupply,FDealDayNum,FLastestDealDayNum,"
                + " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSubscribe,FRight,FSupplyandForce,FOther,FRedeem,"
                + " FBookTotalType,FUnitdigit,FSGBalanceOver,FSGReplaceOver,FSHBalanceOver,FSHReplaceOver,FHolidaysCode,FSHDealReplace,"
                + " FSGDealReplace,FQuitMoneyValue,FCrossHolidaysCode,FClearAccCode" 
                //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
                + " ,FSGBALANCEOVER2,FSGREPLACEOVER2,FSGDEALREPLACE2,FSHBALANCEOVER2,FSHREPLACEOVER2 " 
                + " ,FSHDEALREPLACE2,FBEGINSUPPLY2,FDEALDAYNUM2,FLASTESTDEALDAYNUM2,FbaseRateSrcSSCode "
                /**shashijie 2012-12-6 STORY 3328 去掉了一个baseRateSrcSSCode,重复了*/
                + " ,FbaseRateSSCode,FportRateSrcSSCode,FportRateSSCode " 
				/**end shashijie 2012-12-6 STORY 3328 */
                + " ,FbaseRateSrcBPCode,FbaseRateBPCode,FportRateSrcBPCode,FportRateBPCode "
                //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
                + ",FClearNum"//add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B 添加字段 FClearNum
                + ")"
                + " values("
                + dbl.sqlString(this.etfParam.getPortCode())
                + ","
                + dbl.sqlString(this.etfParam.getMktSrcCode())
                + ","
                + dbl.sqlString(this.etfParam.getOneGradeMktCode())
                + ","
                + dbl.sqlString(this.etfParam.getTwoGradeMktCode())
                + ","
                + this.etfParam.getNormScale()
                + ","
                + dbl.sqlString(this.etfParam.getETFSeat())
                + ","
                + dbl.sqlString(this.etfParam.getSupplyMode())
                + ","
                + dbl.sqlString(this.etfParam.getCapitalCode())
                + ","
                + dbl.sqlString(this.etfParam.getCashAccCode())
                + ","
                + this.etfParam.getBeginSupply()
                + ","
                + this.etfParam.getDealDayNum()
                + ","
                + this.etfParam.getLastestDealDayNum()
                + ","
                + (pub.getSysCheckState() ? "0" : "1")
                + ","
                + dbl.sqlString(this.etfParam.creatorCode)
                + ","
                + dbl.sqlString(this.etfParam.creatorTime)
                + ","
                + (pub.getSysCheckState() ? "' '" : dbl
                   .sqlString(this.creatorCode))
                + ","
                + (pub.getSysCheckState() ? "' '" : dbl
                   .sqlString(this.creatorTime)) + ","
                + dbl.sqlString(this.etfParam.getSSubscribeData()) + ","
                + dbl.sqlString(this.etfParam.getSRightData()) + ","
                + dbl.sqlString(this.etfParam.getSSupplyAndForceData()) + ","
                + dbl.sqlString(this.etfParam.getSOtherData()) + ","
                + dbl.sqlString(this.etfParam.getSRedeemData())+","
                + dbl.sqlString(this.etfParam.getSBookTotalType()) + ","
                + this.etfParam.getSUnitdigit() + ","
                + this.etfParam.getSSGBalanceOver() + ","
                + this.etfParam.getSSGReplaceOver() + ","
                + this.etfParam.getSSHBalanceOver() + ","
                + this.etfParam.getSSHReplaceOver() + ","
                + dbl.sqlString(this.etfParam.getSHolidayCode()) + ","
                + this.etfParam.getISHDealReplace() + ","
                + this.etfParam.getISGDealReplace() + ","
                + dbl.sqlString(this.etfParam.getSQuitMoneyValue())+","
                +(etfParam.getsCrossHolidayCode()==""? "''" : dbl.sqlString(etfParam.getsCrossHolidayCode())) + ","
                + dbl.sqlString(this.etfParam.getClearAccCode())+","
                //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
                + this.etfParam.getsSGBalanceOver2() + ","
                + this.etfParam.getsSGReplaceOver2() + ","
                + this.etfParam.getiSGDealReplace2() + ","
                + this.etfParam.getsSHBalanceOver2() + ","
                + this.etfParam.getsSHReplaceOver2() + ","
                + this.etfParam.getiSHDealReplace2() + ","
                + this.etfParam.getBeginSupply2() + ","
                + this.etfParam.getDealDayNum2() + ","
                + this.etfParam.getLastestDealDayNum2() + ","
                + dbl.sqlString(this.etfParam.getBaseRateSrcSSCode()) + ","
                + dbl.sqlString(this.etfParam.getBaseRateSSCode()) + ","
                + dbl.sqlString(this.etfParam.getPortRateSrcSSCode()) + ","
                + dbl.sqlString(this.etfParam.getPortRateSSCode()) + ","
                + dbl.sqlString(this.etfParam.getBaseRateSrcBPCode()) + ","
                + dbl.sqlString(this.etfParam.getBaseRateBPCode()) + ","
                + dbl.sqlString(this.etfParam.getPortRateSrcBPCode()) + ","
                + dbl.sqlString(this.etfParam.getPortRateBPCode()) + ","
                //---add by songjie 2012.12.05 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
                //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B 添加字段 FClearNum
                + dbl.sqlString(this.etfParam.getClearNum())
                + ")";
            
            dbl.executeSql(strSql);
            
            /**shashijie 2012-12-6 STORY 3328 先删除再插入*/
            deleteParamhoildays(this.etfParam.getPortCode());
			/**end shashijie 2012-12-6 STORY */
            
            addParamHolidays(conn);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增ETF参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**shashijie 2012-12-6 STORY 3328 删除节假日群关联表 */
	private void deleteParamhoildays(String portCode) throws YssException {
		boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
		try {
			conn.setAutoCommit(false);
            bTrans = true;
            
			String query = "Delete From "+pub.yssGetTableName("Tb_ETF_Paramhoildays")+
				" where FPORTCODE="+dbl.sqlString(portCode);
			
			dbl.executeSql(query);
			
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("删除节假日群关联表出错!", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}
	
	/**
     * add by songjie 2012.12.05 
     * STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001
     * @param pst
     * @param etfParam
     * @throws YssException
     */
    private void setPst(PreparedStatement pst, ETFParamSetBean etfParam, String holiday, String operType) throws YssException{
    	try{
			pst.setString(1, this.etfParam.getPortCode());
			/**shashijie 2012-12-6 STORY 3328 */
			//pst.setString(2, etfParam.getLastestDealDayNumHD2().trim());
			pst.setString(2, holiday);
			/**end shashijie 2012-12-6 STORY */
			pst.setString(3, operType);
    		pst.setString(4, (pub.getSysCheckState() ? "0" : "1"));
    		pst.setString(5, this.etfParam.creatorCode);
    		pst.setString(6, this.etfParam.creatorTime);
    		pst.setString(7, pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode));
    		pst.setString(8, pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime));
    		
    		pst.addBatch();
    	}catch(Exception e){
    		throw new YssException("PreparedStatement赋值出错");
    	}
    }
    
    /**
     * 添加国内和跨境节假日的结转类型
     */
    //edit by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001
	public void addParamHolidays(Connection conn) throws YssException {
		PreparedStatement pst = null;
		String sql = "";
		try {
			sql = "insert into " + pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
				+ "(FPORTCODE,FHOLIDAYSCODE,FOVERTYPE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
				+ "values(?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(sql);
			
			if(etfParam.getsSGBalanceOverHD().trim().length() > 0){//申购现金差额 结转 节假日群
				setPst(pst,etfParam,etfParam.getsSGBalanceOverHD().trim(),"sgbalanceover");
			}
			if(etfParam.getsSGReplaceOverHD().trim().length() > 0){//申购现金替代款 结转 节假日群
				setPst(pst,etfParam,etfParam.getsSGReplaceOverHD().trim(),"sgreplaceover");
			}
			if(etfParam.getiSGDealReplaceHD().trim().length() > 0){//申购应付替代 结转 节假日群
				setPst(pst,etfParam,etfParam.getiSGDealReplaceHD().trim(),"sgdealreplace");
			}
			if(etfParam.getsSHBalanceOverHD().trim().length() > 0){//赎回现金差额的 结转 节假日群
				setPst(pst,etfParam,etfParam.getsSHBalanceOverHD().trim(),"shbalanceover");
			}
			if(etfParam.getsSHReplaceOverHD().trim().length() > 0){//赎回现金替代款 结转 节假日群
				setPst(pst,etfParam,etfParam.getsSHReplaceOverHD().trim(),"shreplaceover");
			}
			if(etfParam.getiSHDealReplaceHD().trim().length() > 0){//赎回应付替代款 结转 节假日群
				setPst(pst,etfParam,etfParam.getiSHDealReplaceHD().trim(),"shdealreplace");
			}
			if(etfParam.getBeginSupplyHD().trim().length() > 0){//开始补票 结转 节假日群
				setPst(pst,etfParam,etfParam.getBeginSupplyHD().trim(),"beginsupply");
			}
			if(etfParam.getDealDayNumHD().trim().length() > 0){//几个交易日内补票完成 结转 节假日群
				setPst(pst,etfParam,etfParam.getDealDayNumHD().trim(),"dealdaynum");
			}
			if(etfParam.getLastestDealDayNumHD().trim().length() > 0){//最长几个交易日内补票完成 结转 节假日群
				setPst(pst,etfParam,etfParam.getLastestDealDayNumHD().trim(),"lastestdealdaynum");
			}
			if(etfParam.getsSGBalanceOverHD2().trim().length() > 0){//申购现金差额 结转2 节假日群
				setPst(pst,etfParam,etfParam.getsSGBalanceOverHD2().trim(),"sgbalanceover2");
			}
			if(etfParam.getsSGReplaceOverHD2().trim().length() > 0){//申购现金替代款 结转2 节假日群
				setPst(pst,etfParam,etfParam.getsSGReplaceOverHD2().trim(),"sgreplaceover2");
			}
			if(etfParam.getiSGDealReplaceHD2().trim().length() > 0){//申购应付替代 结转2 节假日群
				setPst(pst,etfParam,etfParam.getiSGDealReplaceHD2().trim(),"sgdealreplace2");
			}
			if(etfParam.getsSHBalanceOverHD2().trim().length() > 0){//赎回现金差额的 结转2 节假日群
				setPst(pst,etfParam,etfParam.getsSHBalanceOverHD2().trim(),"shbalanceover2");
			}
			if(etfParam.getsSHReplaceOverHD2().trim().length() > 0){//赎回现金替代款 结转2 节假日群
				setPst(pst,etfParam,etfParam.getsSHReplaceOverHD2().trim(),"shreplaceover2");
			}
			if(etfParam.getiSHDealReplaceHD2().trim().length() > 0){//赎回应付替代款 结转2 节假日群
				setPst(pst,etfParam,etfParam.getiSHDealReplaceHD2().trim(),"shdealreplace2");
			}
			if(etfParam.getBeginSupplyHD2().trim().length() > 0){//开始补票 结转2 节假日群
				setPst(pst,etfParam,etfParam.getBeginSupplyHD2().trim(),"beginsupply2");
			}
			if(etfParam.getDealDayNumHD2().trim().length() > 0){//几个交易日内补票完成 结转2 节假日群
				setPst(pst,etfParam,etfParam.getDealDayNumHD2().trim(),"dealdaynum2");
			}
			if(etfParam.getLastestDealDayNumHD2().trim().length() > 0){//最长几个交易日内补票完成 结转2 节假日群
				setPst(pst,etfParam,etfParam.getLastestDealDayNumHD2().trim(),"lastestdealdaynum2");
			}
			
			pst.executeBatch();
			
//			if (!etfParam.getsDomesticOverType().equals("")) {
//				String[] domesticOverTypes = etfParam.getsDomesticOverType().split(",");
//				for (int t = 0; t < domesticOverTypes.length; t++) {
//					sql = "insert into "
//							+ pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
//							+ "(FPORTCODE,FHOLIDAYSCODE,FOVERTYPE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
//							+ " values(" + dbl.sqlString(this.etfParam.getPortCode()) + ","
//							+ dbl.sqlString(this.etfParam.getSHolidayCode()) + ","
//							+ dbl.sqlString(domesticOverTypes[t]) + "," + (pub.getSysCheckState() ? "0" : "1") + ","
//							+ dbl.sqlString(this.etfParam.creatorCode) + "," + dbl.sqlString(this.etfParam.creatorTime)
//							+ "," + (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ","
//							+ (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime))
//
//							+ ")";
//
//					pst.addBatch(sql);
//				}
//			}
//
//			if (etfParam.getsCrossHolidayCode() != null && !etfParam.getsCrossHolidayCode().equals("")) {
//
//				if (!etfParam.getsCrossOverType().equals("")) {
//					String[] crossOverTypes = etfParam.getsCrossOverType().split(",");
//
//					for (int s = 0; s < crossOverTypes.length; s++) {
//						sql = "insert into "
//								+ pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
//								+ "(FPORTCODE,FHOLIDAYSCODE,FOVERTYPE,FCHECKSTATE,FCREATOR,FCREATETIME,FCHECKUSER,FCHECKTIME)"
//								+ " values(" + dbl.sqlString(this.etfParam.getPortCode()) + ","
//								+ dbl.sqlString(this.etfParam.getsCrossHolidayCode()) + ","
//								+ dbl.sqlString(crossOverTypes[s]) + "," + (pub.getSysCheckState() ? "0" : "1") + ","
//								+ dbl.sqlString(this.etfParam.creatorCode) + ","
//								+ dbl.sqlString(this.etfParam.creatorTime) + ","
//								+ (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) + ","
//								+ (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorTime))
//
//								+ ")";
//
//						pst.addBatch(sql);
//					}
//				}
//			}
		} catch (Exception e) {
			throw new YssException("增加ETF参数信息出错", e);
		} finally{
			dbl.closeStatementFinal(pst);
		}
	}
    
    /**
     * 修改数据
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        try {
            //判断是否更新了补票次数
            strSql = "SELECT FDealDayNum FROM " + pub.yssGetTableName("TB_ETF_Param") +
                " WHERE FPortCode = " + dbl.sqlString(this.etfParam.getOldPortCode());
            rs = dbl.openResultSet(strSql);
            if(rs.next()){
                if(rs.getInt("FDealDayNum") != etfParam.getDealDayNum()){
                    dbl.closeResultSetFinal(rs);
                    //更新数据之前先判断台帐表里是否有数据，如果有数据则不能更新补票次数
                    strSql = "SELECT FBuyDate FROM " + pub.yssGetTableName("Tb_ETF_StandingBook") +
                        " WHERE FPortCode = " + dbl.sqlString(this.etfParam.getOldPortCode());
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        throw new YssException("对不起台帐表中已经存在数据，您不能修改补票次数！");
                    }
                }
            }

            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "update " + pub.yssGetTableName("Tb_ETF_Param")
                + " set FPortCode = " + dbl.sqlString(this.etfParam.getPortCode())
                + " ,FMktSrcCode = " + dbl.sqlString(this.etfParam.getMktSrcCode())
                + " ,FOneGradeMktCode = " + dbl.sqlString(this.etfParam.getOneGradeMktCode())
                + " ,FTwoGradeMktCode = " + dbl.sqlString(this.etfParam.getTwoGradeMktCode())
                + " ,FNormScale = " + this.etfParam.getNormScale()
                + " ,FETFSeat = " + dbl.sqlString(this.etfParam.getETFSeat())
                + " ,FSupplyMode = " + dbl.sqlString(this.etfParam.getSupplyMode())
                + " ,FCapitalCode = " + dbl.sqlString(this.etfParam.getCapitalCode())
                + " ,FCashAccCode = " + dbl.sqlString(this.etfParam.getCashAccCode())
                + " ,FClearAccCode = " + dbl.sqlString(this.etfParam.getClearAccCode())
                + " ,FBeginSupply = " + this.etfParam.getBeginSupply()
                + " ,FDealDayNum = " + this.etfParam.getDealDayNum()
                + " ,FLastestDealDayNum = " + this.etfParam.getLastestDealDayNum() 
                + " ,FCheckState = " + dbl.sqlString(pub.getSysCheckState() ? "0" : "1")
                + " ,FCreator = " + dbl.sqlString(this.etfParam.creatorCode)
                + " ,FCreateTime = " + dbl.sqlString(this.etfParam.creatorTime)
                + (this.etfParam.getSSubscribeData().trim().length()!=0?",FSubscribe = "+ dbl.sqlString(this.etfParam.getSSubscribeData()):"")
                + (this.etfParam.getSRightData().trim().length()!=0?" ,FRight = " + dbl.sqlString(this.etfParam.getSRightData()):"")
                + (this.etfParam.getSSupplyAndForceData().trim().length()!=0?" ,FSupplyandForce = " + dbl.sqlString(this.etfParam.getSSupplyAndForceData()):"")
                + (this.etfParam.getSOtherData().trim().length()!=0?" ,FOther = " + dbl.sqlString(this.etfParam.getSOtherData()):"")
                + (this.etfParam.getSRedeemData().trim().length()!=0?" ,FRedeem = " + dbl.sqlString(this.etfParam.getSRedeemData()):"")
                +" ,FBookTotalType = " + dbl.sqlString(this.etfParam.getSBookTotalType())
                +" ,FUnitdigit = " + this.etfParam.getSUnitdigit()
                +" ,FSGBalanceOver =" + this.etfParam.getSSGBalanceOver()
                +" ,FSGReplaceOver =" + this.etfParam.getSSGReplaceOver()
                +" ,FSHBalanceOver =" + this.etfParam.getSSHBalanceOver()
                +" ,FSHReplaceOver =" + this.etfParam.getSSHReplaceOver()
                +" ,FHolidaysCode =" + dbl.sqlString(this.etfParam.getSHolidayCode())
                +" ,FSHDealReplace ="+ this.etfParam.getISHDealReplace()
                +" ,FCrossHolidaysCode="+(etfParam.getsCrossHolidayCode()==""? "''" : dbl.sqlString(etfParam.getsCrossHolidayCode()))
                +" ,FSGDealReplace =" + this.etfParam.getISGDealReplace()
                + (this.etfParam.getSQuitMoneyValue().trim().length()!=0?" ,FQuitMoneyValue =" + dbl.sqlString(this.etfParam.getSQuitMoneyValue()):"")
                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
                + " ,FSGBALANCEOVER2 = " + this.etfParam.getsSGBalanceOver2()
                + " ,FSGREPLACEOVER2 = " + this.etfParam.getsSGReplaceOver2()
                + " ,FSGDEALREPLACE2 = " + this.etfParam.getiSGDealReplace2()
                + " ,FSHBALANCEOVER2 = " + this.etfParam.getsSHBalanceOver2()
                + " ,FSHREPLACEOVER2 = " + this.etfParam.getsSHReplaceOver2()
                + " ,FSHDEALREPLACE2 = " + this.etfParam.getiSHDealReplace2()
                + " ,FBEGINSUPPLY2 = " + this.etfParam.getBeginSupply2()
                + " ,FDEALDAYNUM2 = " + this.etfParam.getDealDayNum2()
                + " ,FLASTESTDEALDAYNUM2 = " + this.etfParam.getLastestDealDayNum2()
                + " ,FbaseRateSrcSSCode = " + dbl.sqlString(this.etfParam.getBaseRateSrcSSCode())
                + " ,FbaseRateSSCode = " + dbl.sqlString(this.etfParam.getBaseRateSSCode())
                + " ,FportRateSrcSSCode = " + dbl.sqlString(this.etfParam.getPortRateSrcSSCode())
                + " ,FportRateSSCode = " + dbl.sqlString(this.etfParam.getPortRateSSCode())
                + " ,FbaseRateSrcBPCode = " + dbl.sqlString(this.etfParam.getBaseRateSrcBPCode())
                + " ,FbaseRateBPCode = " + dbl.sqlString(this.etfParam.getBaseRateBPCode())
                + " ,FportRateSrcBPCode = " + dbl.sqlString(this.etfParam.getPortRateSrcBPCode())
                + " ,FportRateBPCode = " + dbl.sqlString(this.etfParam.getPortRateBPCode())
                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
                //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B 添加字段FClearNum
                + " ,FClearNum = " + dbl.sqlString(this.etfParam.getClearNum())
                + " where FPortCode = " + dbl.sqlString(this.etfParam.getOldPortCode());
            dbl.executeSql(strSql);
            
            /**shashijie 2012-12-6 STORY 3328 先删除再插入*/
            deleteParamhoildays(this.etfParam.getOldPortCode());
            /*strSql="delete from "+pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")+
             * " where FPORTCODE="+dbl.sqlString(this.etfParam.getOldPortCode());//修改时，先删除再插入
            dbl.executeSql(strSql); */
            /**end shashijie 2012-12-6 STORY */
            
            addParamHolidays(conn); 
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
           
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改ETF参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    /**
     * 删除数据，先放到回收站中
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_ETF_Param")
                + " set FCheckState = " + this.etfParam.checkStateId
                + ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
                + ", FCheckTime = '"
                + YssFun.formatDatetime(new java.util.Date()) + "'"
                + " where FPortCode = "
                + dbl.sqlString(this.etfParam.getPortCode());
            dbl.executeSql(strSql);
            
            strSql="update "+pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
            	+" set FCHECKSTATE= "+this.etfParam.checkStateId
            	+", FCHECKUSER= "+dbl.sqlString(pub.getUserCode())
            	+", FCHECKTIME= '"
            	+ YssFun.formatDatetime(new java.util.Date()) + "'"
            	+" where FPORTCODE= "
            	+dbl.sqlString(this.etfParam.getPortCode());
            	
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除ETF参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    /**回收站还原，以及审核，反审核功能 */
    public void checkSetting() throws YssException {
        String strSql = ""; // 定义一个字符串来放SQL语句
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        Connection conn = dbl.loadConnection(); // 打开一个数据库联接
        String subSql=""; //更新子表的sql
        
        try {
            conn.setAutoCommit(false); // 开启一个事务
            bTrans = true; // 代表是否关闭事务
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { // 判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); // 解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { // 循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; // 如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); // 解析这个数组里的内容
                    strSql = "update " + pub.yssGetTableName("Tb_ETF_Param")
                        + " set FCheckState = "
                        + this.etfParam.checkStateId;
                    
                    subSql="update " + pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
                    + " set FCHECKSTATE = "
                    + this.etfParam.checkStateId;
                    
                    
                    // 如果是审核操作，则获取审核人代码和审核时间
                    if (this.etfParam.checkStateId == 1) {
                        strSql += ", FCheckUser = '" + pub.getUserCode()
                            + "' , FCheckTime = '"
                            + YssFun.formatDatetime(new java.util.Date())
                            + "'";
                        
                        subSql+= ", FCHECKUSER = '" + pub.getUserCode()
                        + "' , FCHECKTIME = '"
                        + YssFun.formatDatetime(new java.util.Date())
                        + "'";
                        
                    }
                    strSql += " where FPortCode = "
                        + dbl.sqlString(this.etfParam.getPortCode());
                    
                    subSql += " where FPORTCODE = "
                        + dbl.sqlString(this.etfParam.getPortCode());
                    
                    
                    dbl.executeSql(strSql); // 执行更新操作
                    dbl.executeSql(subSql);
                }
            }
            conn.commit(); // 提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核ETF参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * add by songjie
     * 2009.10.13
     * V4.1_ETF:MS00002
     * QDV4.1赢时胜（上海）2009年9月28日01_A
     * 根据组合代码查询ETF参数设置数据
     * @param portCodes String
     * @return HashMap
     * @throws YssException
     */
    public HashMap getETFParamInfo(String portCodes) throws YssException {
        String strSql = "";
        ResultSet rs = null; //声明结果集
        HashMap hmETFParam = new HashMap();
        ETFParamSetBean paramSet = null;
        String sPortCode = "";
        try {
            strSql = " select a.*, b.FHolidaysCode as FHoliday, b.FOverType from " + pub.yssGetTableName("Tb_ETF_Param") + " a " +
            	" LEFT JOIN " + pub.yssGetTableName("TB_ETF_ParamHoildays") + " b " +
            	" ON a.FPortCode = b.FPortCode" +
                " Where a.FPortCode in (" + operSql.sqlCodes(portCodes) + ") and a.FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	if(!sPortCode.equalsIgnoreCase(rs.getString("FPortCode"))){
            		sPortCode = rs.getString("FPortCode");
	                
            		paramSet = new ETFParamSetBean(); //ETF参数对应的实体类
	
	                paramSet.setPortCode(sPortCode); //组合代码
	                paramSet.setOneGradeMktCode(rs.getString("FOneGradeMktCode")); //一级市场代码
	                paramSet.setTwoGradeMktCode(rs.getString("FTwoGradeMktCode")); //二级市场代码
	                paramSet.setCapitalCode(rs.getString("FCapitalCode")); //资金代码
	                paramSet.setNormScale(rs.getInt("FNormScale")); //基准比例
	                paramSet.setETFSeat(rs.getString("FETFSeat")); //ETF席位
	                paramSet.setSupplyMode(rs.getString("FSupplyMode")); //补票方式
	                
	                paramSet.setISGDealReplace(rs.getInt("FSGDealReplace")); //申购应付替代结转
	                paramSet.setISHDealReplace(rs.getInt("FSHDealReplace")); //赎回应付替代结转
	                paramSet.setSSGBalanceOver(rs.getInt("FSGBalanceOver"));//申购现金差额结转
	                paramSet.setSSGReplaceOver(rs.getInt("FSGReplaceOver"));//申购现金替代结转
	                paramSet.setSSHBalanceOver(rs.getInt("FSHBalanceOver"));//赎回现金差额结转
	                paramSet.setSSHReplaceOver(rs.getInt("FSHReplaceOver"));//赎回现金替代结转
	                paramSet.setBeginSupply(rs.getInt("FBEGINSUPPLY")); //几日开始补票
	                paramSet.setDealDayNum(rs.getInt("FDealDayNum")); //申购后几个交易日内补票完成
	                paramSet.setLastestDealDayNum(rs.getInt("FLASTESTDEALDAYNUM")); //最长几日补票完成
	                
	                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
	                paramSet.setiSGDealReplace2(rs.getInt("FSGDealReplace2")); //申购应付替代结转
	                paramSet.setiSHDealReplace2(rs.getInt("FSHDealReplace2")); //赎回应付替代结转
	                paramSet.setsSGBalanceOver2(rs.getInt("FSGBalanceOver2"));//申购现金差额结转
	                paramSet.setsSGReplaceOver2(rs.getInt("FSGReplaceOver2"));//申购现金替代结转
	                paramSet.setsSHBalanceOver2(rs.getInt("FSHBalanceOver2"));//赎回现金差额结转
	                paramSet.setsSHReplaceOver2(rs.getInt("FSHReplaceOver2"));//赎回现金替代结转
	                paramSet.setBeginSupply2(rs.getInt("FBEGINSUPPLY2")); //几日开始补票
	                paramSet.setDealDayNum2(rs.getInt("FDealDayNum2")); //申购后几个交易日内补票完成
	                paramSet.setLastestDealDayNum2(rs.getInt("FLASTESTDEALDAYNUM2")); //最长几日补票完成
	                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
	                
	                paramSet.setCashAccCode(rs.getString("FCashAccCode")); //现金替代款结转账户
	                paramSet.setClearAccCode(rs.getString("FClearAccCode"));//结算备付金账户

	                paramSet.setSBookTotalType(rs.getString("FBookTotalType"));//汇总方式
	                paramSet.setSHolidayCode(rs.getString("FHolidaysCode"));//节假日代码
	                /**shashijie 2011-08-07 STORY 1434*/
	                paramSet.setsCrossHolidayCode(rs.getString("FCrossHolidaysCode"));//国外节假日代码
	                /**end*/
	                paramSet.setSUnitdigit(rs.getInt("FUnitdigit"));//单位成本保留为数
	                
	                /**shashijie 2012-12-10 STORY 3328 新增字段赋值,共17个字段 */
	                paramSet.setBeginSupply2(rs.getInt("FBeginSupply2"));//开始补票 结转天数2
	                paramSet.setDealDayNum2(rs.getInt("FDealDayNum2"));//几个交易日内补票完成 结转天数2
	                paramSet.setLastestDealDayNum2(rs.getInt("FLastestDealDayNum2"));//最长几个交易日内补票完成 结转天数2
	                paramSet.setsSGBalanceOver2(rs.getInt("FSGBalanceOver2"));//申购现金差额 结转天数2
	                paramSet.setsSGReplaceOver2(rs.getInt("FSGReplaceOver2"));//申购现金替代款 结转天数2
	                paramSet.setiSGDealReplace2(rs.getInt("FSGDealReplace2"));//申购应付替代 结转天数2
	                paramSet.setsSHBalanceOver2(rs.getInt("FSHBalanceOver2"));//赎回现金差额的 结转天数2
	                paramSet.setsSHReplaceOver2(rs.getInt("FSHReplaceOver2"));//赎回现金替代款 结转天数2
	                paramSet.setiSHDealReplace2(rs.getInt("FSHDealReplace2"));//赎回应付替代款 结转天数2
					//申赎汇率来源
	                paramSet.setBaseRateSrcSSCode(rs.getString("FbaseRateSrcSSCode"));//基础汇率来源代码 申赎
	                paramSet.setBaseRateSSCode(rs.getString("FbaseRateSSCode"));//基础汇率行情 申赎
	                paramSet.setPortRateSrcSSCode(rs.getString("FportRateSrcSSCode"));//组合汇率来源代码 申赎
	                paramSet.setPortRateSSCode(rs.getString("FportRateSSCode"));//组合汇率行情 申赎
				    //补票汇率来源
	                paramSet.setBaseRateSrcBPCode(rs.getString("FbaseRateSrcBPCode"));//基础汇率来源代码 补票
				    paramSet.setBaseRateBPCode(rs.getString("FbaseRateBPCode"));//基础汇率行情 补票
				    paramSet.setPortRateSrcBPCode(rs.getString("FportRateSrcBPCode"));//组合汇率来源代码 补票
				    paramSet.setPortRateBPCode(rs.getString("FportRateBPCode"));//组合汇率行情 补票
					/**end shashijie 2012-12-10 STORY 3328 */
				    
	                //add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B
				    paramSet.setClearNum(rs.getString("FClearNum"));//清算编号
	                hmETFParam.put(sPortCode, paramSet);
            	}
                paramSet.getHoildaysRela().put(rs.getString("FOverType"), rs.getString("FHoliday"));
            }

            return hmETFParam;
        } catch (Exception e) {
            throw new YssException("根据组合代码查询ETF参数设置数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 回收站清除功能
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; // 定义一个放SQL语句的字符串
        String[] arrData = null; // 定义一个字符数组来循环删除
        boolean bTrans = false; // 代表是否开始了事务
        // 获取一个连接
        Connection conn = dbl.loadConnection();
        
        String subSql="";  //子表sql语句
        
        
        try {
            // 如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                // 根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                // 循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from "
                        + pub.yssGetTableName("Tb_ETF_Param")
                        + " where FPortCode = "
                        + dbl.sqlString(this.etfParam.getPortCode()); // SQL语句
                    
                    subSql="delete from "
                        + pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
                        + " where FPortCode = "
                        + dbl.sqlString(this.etfParam.getPortCode()); 
                    
                    
                    // 执行sql语句
                    dbl.executeSql(strSql);
                    dbl.executeSql(subSql);
                }
            }
            // sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (this.etfParam.getPortCode() != null
                     && this.etfParam.getPortCode() != "") {
            	
                strSql = "delete from " + pub.yssGetTableName("Tb_ETF_Param")
                    + " where FPortCode = "
                    + dbl.sqlString(this.etfParam.getPortCode()); // SQL语句
                
                subSql = "delete from " + pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
                + " where FPortCode = "
                + dbl.sqlString(this.etfParam.getPortCode()); 
                // 执行sql语句
                dbl.executeSql(strSql);
                dbl.executeSql(subSql);
            }
            conn.commit(); // 提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); // 释放资源
        }
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
     * 查询数据，按照一定规则拼接数据返回到前台
     */
    public String getListViewData1() throws YssException {
        String strSql = ""; // 定义一个存放sql语句的字符串
        try {
            strSql = "select y.* from "
                + " (select a.*,b.FPortName,c.FUserName as FCreatorName, d.FUserName as FCheckUserName," 
                + "hi.FHolidaysName as domesticHolidaysName,h.FHolidaysName as corssHolidaysname,"
                //edited by zhouxiang MS01553 TA库存，反勾选自定义中的字段，再次查询报错 
                + "'' as FCashBalance,'' as FCashReplace , '' as FDealReplace,"
                //edited by zhouxiang MS01553 TA库存，反勾选自定义中的字段，再次查询报错 
                
                + " vb.FVocName as FSupplyModeName,vb1.FVocName as FBookTotalTypeName,ca.FCashAccName," 
                //--- edit by songjie 2012.12.06 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
                + " cb.FClearAccName,mt.fmktsrcname " 
                + " ,exs1.FEXRATESRCNAME as BaseRateSrcSSName,exs2.FEXRATESRCNAME as baseRateSrcBPName "
                + " ,exs3.FEXRATESRCNAME as PortRateSrcSSName,exs4.FEXRATESRCNAME as PortRateSrcBPName from "
                //--- edit by songjie 2012.12.06 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
                + pub.yssGetTableName("Tb_ETF_Param")
                + " a left join (select FPortCode,FPortName from "
                + pub.yssGetTableName("Tb_Para_Portfolio")
                + " where FCheckState = 1) b on a.FPortCode = b.FPortCode"
                + " left join (select FCashAccCode,FCashAccName from "
                + pub.yssGetTableName("Tb_Para_CashAccount")
                + " where FCheckState = 1) ca on ca.FCashAccCode = a.FCashAccCode "
                + " left join (select FCashAccCode,FCashAccName as FClearAccName from "
                + pub.yssGetTableName("Tb_Para_CashAccount")
                + " where FCheckState = 1) cb on cb.FCashAccCode = a.FClearAccCode "
                + "left join (select FMktSrcCode, FMktSrcName from "
                + pub.yssGetTableName("tb_para_marketsource")
                + " where FCheckState = 1) mt on mt.FMktSrcCode = a.fmktsrccode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCreator = c.FUserCode"
                + " left join (select FUserCode,FUserName from Tb_Sys_UserList) d on a.FCheckUser = d.FUserCode"
                + " left join (select FHolidaysCode,FHolidaysName from "+pub.yssGetTableName("Tb_Base_Holidays")+" where FCheckState = 1)"
                + " hi on a.FHolidaysCode = hi.FHolidaysCode"
                + " left join (select FHolidaysCode,FHolidaysName from "+pub.yssGetTableName("Tb_Base_Holidays")+" where FCheckState = 1)"
                +" h on h.FHolidaysCode=a.fcrossholidayscode"
                + " left join (select * from Tb_Fun_Vocabulary where FCheckState = 1) vb on a.FSupplyMode = vb.FVocCode and vb.FVocTypeCode = " 
                + dbl.sqlString(YssCons.YSS_SUPPLY_MODE) 
                + " left join( select * from Tb_Fun_Vocabulary where FCheckState = 1) vb1 on a.FBookTotalType = vb1.FVocCode and vb1.FVocTypeCode =" 
                + dbl.sqlString(YssCons.YSS_ETF_BOOKGATHERMODE)
                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 start---//
                + " left join (select FEXRATESRCCODE,FEXRATESRCNAME from " + pub.yssGetTableName("TB_PARA_EXRATESOURCE") 
                + " where FCheckState = 1) exs1 on a.FbaseRateSrcSSCode = exs1.FEXRATESRCCODE "
                + " left join (select FEXRATESRCCODE,FEXRATESRCNAME from " + pub.yssGetTableName("TB_PARA_EXRATESOURCE")
                + " where FCheckState = 1) exs2 on a.FbaseRateSrcBPCode = exs2.FEXRATESRCCODE "
                + " left join (select FEXRATESRCCODE,FEXRATESRCNAME from " + pub.yssGetTableName("TB_PARA_EXRATESOURCE")
                + " where FCheckState = 1) exs3 on a.FportRateSrcSSCode = exs3.FEXRATESRCCODE "
                + " left join (select FEXRATESRCCODE,FEXRATESRCNAME from " + pub.yssGetTableName("TB_PARA_EXRATESOURCE")
                + " where FCheckState = 1) exs4 on a.FportRateSrcBPCode = exs4.FEXRATESRCCODE "
                //---add by songjie 2012.12.05 STORY #3328 需求深圳-[易方达基金]QDV4.0[高]20121123001 end---//
                + buildFilterSql()
                + ") y order by y.FCheckState, y.FCreateTime desc";

        } catch (Exception e) {
            throw new YssException("获取ETF参数数据出错！" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData1(strSql);
    }

    /**
     * buildFilterSql 筛选条件
     *
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
        ETFParamSetBean filterType = this.etfParam.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            if (filterType.getPortCode().length() != 0) {
                sResult = sResult + " and a.FPortCode like '"
                    + filterType.getPortCode().replaceAll("'", "''") + "%'";
            }

            if (filterType.getMktSrcCode().length() != 0) {
                sResult = sResult + " and a.FMktSrcCode = "
                    + filterType.getMktSrcCode();
            }

            if (filterType.getOneGradeMktCode().length() != 0) {
                sResult = sResult + " and a.FOneGradeMktCode = "
                    + filterType.getOneGradeMktCode();
            }

            if (filterType.getTwoGradeMktCode().length() != 0) {
                sResult = sResult + " and a.FTwoGradeMktCode = "
                    + filterType.getTwoGradeMktCode();
            }

            if (filterType.getCapitalCode().length() != 0) {
                sResult = sResult + " and a.FCapitalCode = "
                    + filterType.getCapitalCode();
            }

            if (filterType.getNormScale() != 0) {
                sResult = sResult + " and a.FNormScale = "
                    + filterType.getNormScale();
            }

            if (filterType.getETFSeat().length() != 0) {
                sResult = sResult + " and a.FETFSeat = "
                    + filterType.getETFSeat();
            }

            if (filterType.getSupplyMode().length() != 0
                && !filterType.getSupplyMode().equalsIgnoreCase("99")) {
                sResult = sResult + " and a.FSupplyMode = "
                    + filterType.getSupplyMode();
            }

            if (filterType.getCashAccCode().length() != 0) {
                sResult = sResult + " and a.FCashAccCode = "
                    + filterType.getCashAccCode();
            }
            if (filterType.getClearAccCode().length() != 0) {
                sResult = sResult + " and a.FClearAccCode = "
                    + filterType.getClearAccCode();
            }
            //--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B start---//
            if(filterType.getClearNum().length() != 0){
                sResult = sResult + " and a.FClearNum = "
                + dbl.sqlString(filterType.getClearNum());
            }
            //--- add by songjie 2013.05.07 BUG 7760 QDV4赢时胜(上海)2013年05月07日01_B end---//
        }
        return sResult;
    }

    /**
     * builderListViewData 拼接数据
     *
     * @param strSql
     *            String
     * @return String
     */
	private String builderListViewData1(String strSql) throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sVocStr = "";

		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		ResultSet rs = null;
		ResultSet results = null;
		try {
			sHeader = this.getListView1Headers();

			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);
			sVocStr = vocabulary.getVoc(YssCons.YSS_SUPPLY_MODE + "," + YssCons.YSS_ETF_BOOKGATHERMODE + "," + YssCons.YSS_MTV_EXCHANGERATE) +
			// add by songjie 2012.12.04 STORY
			// #3328需求深圳-[易方达基金]QDV4.0[高]20121123001
					this.getHolidayGroupInfo();
			
			/**shashijie 2012-12-6 STORY 3328 把=="" 判断改成 equals */
			if (strSql.equals("")) {
				return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols()
						+ "\r\f" + "voc" + sVocStr;
			}
			/**end shashijie 2012-12-6 STORY */
			
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);

				StringBuffer domesticOverType = new StringBuffer(); // 国内节假日结转类型
				StringBuffer crossOverType = new StringBuffer(); // 跨境节假日结转类型
				
				/**shashijie 2013-1-24 STORY 3402 FHolidaysCode	国内节假日代码,FCrossHolidaysCode	跨境节假日代码
				 * 这2个字段已用不到,这里不需要再向前台传值了*/
				//国内节假日代码
				if (rs.getString("FHolidaysCode") != null && !rs.getString("FHolidaysCode").equals("")) {
					/*String sql1 = "select * from (select a.FPORTCODE,a.FHOLIDAYSCODE,a.FOVERTYPE,b.fvocname,b.fvocCode from "
							+ pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
							+ " a left join (select * from Tb_Fun_Vocabulary where FCheckState = 1) b on a.fovertype=b.fvoccode"
							+ ") e where e.FHOLIDAYSCODE=" + dbl.sqlString(rs.getString("FHOLIDAYSCODE"))
							+ " and FPORTCODE=" + dbl.sqlString(rs.getString("FPORTCODE"));
					results = dbl.openResultSet(sql1);
					while (results.next()) {
						domesticOverType.append(results.getString("fvocCode") + "\f\n" + results.getString("fvocname"))
								.append(",");
					}
					
					dbl.closeResultSetFinal(results);
					
					if (domesticOverType.toString().length() > 0) {
						this.etfParam.setsDomesticOverType(domesticOverType.toString().substring(0,
								domesticOverType.toString().length() - 1));
					} else {
						this.etfParam.setsDomesticOverType("");
					}*/
				} else {
					this.etfParam.setsDomesticOverType(domesticOverType.toString());
				}
				
				//跨境节假日代码
				if (rs.getString("FCROSSHOLIDAYSCODE") != null && !rs.getString("FCROSSHOLIDAYSCODE").equals("")) {
					/*String sql2 = "select * from (select a.FPORTCODE,a.FHOLIDAYSCODE,a.FOVERTYPE,b.fvocname,b.fvocCode from "
							+ pub.yssGetTableName("TB_ETF_PARAMHOILDAYS")
							+ " a left join (select * from Tb_Fun_Vocabulary where FCheckState = 1) b on a.fovertype=b.fvoccode"
							+ ") e where e.FHOLIDAYSCODE="
							+ dbl.sqlString(rs.getString("FCROSSHOLIDAYSCODE"))
							+ " and FPORTCODE=" + dbl.sqlString(rs.getString("FPORTCODE"));
					results = dbl.openResultSet(sql2);
					while (results.next()) {
						crossOverType.append(results.getString("fvocCode") + "\f\n" + results.getString("fvocname"))
								.append(",");
					}
					
					dbl.closeResultSetFinal(results);

					if (crossOverType.toString().length() > 0) {
						this.etfParam.setsCrossOverType(crossOverType.toString().substring(0,
								crossOverType.toString().length() - 1));
					} else {
						this.etfParam.setsCrossOverType("");
					}*/
				} else {
					this.etfParam.setsCrossOverType(crossOverType.toString());
				}
				/**end shashijie 2013-1-24 STORY 3402 */
				
				//基本属性赋值
				this.etfParam.setETFParamAttr(rs);
				/**shashijie 2012-12-7 STORY 3328 查询各下拉框的值*/
				//先清空节假日代码,否则循环中会叠(累)加出其他组合的节假日
				this.etfParam.clearHoildays();
				//节假日下拉框赋值
				setEveryFHolidaysCode(rs.getString("FPORTCODE"));
				/**end shashijie 2012-12-7 STORY */
				
				bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
			}

			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0, bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
			}

			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" + this.getListView1ShowCols()
					+ "\r\f" + "voc" + sVocStr;
		} catch (Exception e) {
			throw new YssException("获取ETF参数出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(results);
		}
	}

    /**shashijie 2012-12-7 STORY 3328 查询各下拉框的值,并赋值 */
	private void setEveryFHolidaysCode(String FPortCode) throws YssException {
		ResultSet rs = null;
		try {
			String query = getQueryParamhoildays(FPortCode);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				this.etfParam.setETFParamAttrParamhoildays(rs);
			}
		} catch (Exception e) {
			throw new YssException("查询各下拉框的值,并赋值出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**shashijie 2012-12-7 STORY 3328 */
	private String getQueryParamhoildays(String fPortCode) {
		String sqlString = " Select a.Fportcode, a.Fholidayscode, a.Fovertype, b.Fvocname, b.Fvoccode " +
				" From "+pub.yssGetTableName("Tb_Etf_Paramhoildays")+" a " +
				" Left Join (Select * From Tb_Fun_Vocabulary Where Fcheckstate = 1) b On a.Fovertype = b.Fvoccode" +
				" Where a.Fportcode = "+dbl.sqlString(fPortCode);
		return sqlString;
	}
	
	public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
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

    public String getBeforeEditData() throws YssException {
        return "";
    }
    
    /**
     * 解析前台出来的数据
     */
    public void parseRowStr(String sRowStr) throws YssException {
        if (etfParam == null) {
            etfParam = new ETFParamSetBean();
            etfParam.setYssPub(pub);
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
        etfParam.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }
    
    /** 拼接一定格式的数据 */
    public String buildRowStr() throws YssException {
        return etfParam.buildRowStr();
    }
    
    /** 单独处理前台响应的功能 */
    public String getOperValue(String sType) throws YssException {
    	String strAccPortCode="";
    	try{
    		if(sType!=null&&sType.equalsIgnoreCase("checkACCPortCode")){
    			strAccPortCode=checkACCPortCode(sType);
    		}else if(sType!=null&&sType.equalsIgnoreCase("getETFBookVocabulate")){
    			strAccPortCode = getETFBookVocabulate();
    		}else if(sType!=null&&sType.equalsIgnoreCase("checkClearACCPortCode")){
    			strAccPortCode = checkClearACCPortCode(sType);
    		}
    		/**shashijie 2012-12-9 STORY 3328 获取节假日延迟天数后的工作日*/
    		else if (sType!=null&&sType.equalsIgnoreCase("getHolidayDay")) {
    			strAccPortCode = getHolidayDay();
			}
    		//获取汇率
    		else if (sType!=null&&sType.equalsIgnoreCase("getExchangeRate")) {
    			strAccPortCode = getExchangeRate();
			}
    		else if (sType!=null&&sType.equalsIgnoreCase("getHolidayGroupInfo")) {
    			strAccPortCode = getHolidayGroupInfo();
			}
			/**end shashijie 2012-12-9 STORY */
    		
    	}catch(Exception e){
    		throw new YssException(e.getMessage());
    	}
        return strAccPortCode;
    }
    
    /**shashijie 2012-12-9 STORY 3328 获取汇率
	* @return*/
	private String getExchangeRate() throws YssException {
		String rateValue = "";
		
		double aaa = getExchangeRateSS(this.etfParam.getPortCode(), new Date(), "HKD");
		
		double bbb = getExchangeRateBP(this.etfParam.getPortCode(), new Date(), "HKD");
		
		rateValue = aaa+ ","+bbb;
		
		return rateValue;
	}
	
	/**shashijie 2012-12-9 STORY 3328 公共获取汇率方法(申赎汇率) */
	public double getExchangeRateSS(String portCode, Date dDate,String FCuryCode) throws YssException {
		double rate = 1;
		ResultSet rs = null;
		
		try {
			String query = getQueryExchangeRateSql(portCode,dDate);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//申赎基础汇率
				double baseRate = getExchangeRateValue(dDate,rs.getString("FbaseRateSrcSSCode"),rs.getString("FbaseRateSSCode")
						,FCuryCode,portCode,YssOperCons.YSS_RATE_BASE);
				//申赎组合汇率
				double portRate = getExchangeRateValue(dDate,rs.getString("FportRateSrcSSCode"),rs.getString("FportRateSSCode")
						,"",portCode,YssOperCons.YSS_RATE_PORT);
				rate = YssD.mul(baseRate, portRate);
			}
		} catch (Exception e) {
			throw new YssException("获取汇率方法出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return rate;
	}
	
	/**shashijie 2012-12-9 STORY 3328 公共获取汇率方法(补票汇率) */
	public double getExchangeRateBP(String portCode, Date dDate,String FCuryCode) throws YssException {
		double rate = 1;
		ResultSet rs = null;
		
		try {
			String query = getQueryExchangeRateSql(portCode,dDate);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//补票基础汇率
				double baseRate = getExchangeRateValue(dDate,rs.getString("FbaseRateSrcBPCode"),rs.getString("FbaseRateBPCode")
						,FCuryCode,portCode,YssOperCons.YSS_RATE_BASE);
				//补票组合汇率
				double portRate = getExchangeRateValue(dDate,rs.getString("FportRateSrcBPCode"),rs.getString("FportRateBPCode")
						,"",portCode,YssOperCons.YSS_RATE_PORT);
				rate = YssD.mul(baseRate, portRate);
				
			}
		} catch (Exception e) {
			throw new YssException("获取汇率方法出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return rate;
	}
	
	/**shashijie 2012-12-9 STORY 3328  */
	private String getQueryExchangeRateSql(String portCode, Date dDate) {
		String sqlString = " Select a.Fportcode, a.FbaseRateSrcSSCode, a.FbaseRateSSCode, a.FportRateSrcSSCode," +
				" a.FportRateSSCode, a.FbaseRateSrcBPCode, a.FbaseRateBPCode, a.FportRateSrcBPCode, a.FportRateBPCode"+
				//ETF基础参数
				" From (Select A1.Fportcode, A1.FbaseRateSrcSSCode, A1.FbaseRateSSCode, A1.FportRateSrcSSCode," +
				" A1.FportRateSSCode, A1.FbaseRateSrcBPCode, A1.FbaseRateBPCode, A1.FportRateSrcBPCode, A1.FportRateBPCode" +
				" From "+pub.yssGetTableName("Tb_Etf_Param")+" A1 Where A1.Fcheckstate = 1" +
				" And A1.Fportcode = "+dbl.sqlString(portCode)+" ) a "+
				" Order By a.Fportcode " ;
		return sqlString;
	}
	
	/**shashijie 2012-12-9 STORY 3328 公共获取汇率方法 */
	public double getExchangeRateValue(Date dDate, String sSrcRate,
			String sFieldRate, String sCuryCode, String portCode, String yssRateBase) throws YssException {
		double rate = 1;
		
		//若没有设置汇率来源,则获取估值方法汇率表中的汇率
		if (sSrcRate==null || sSrcRate.trim().equals("")
				|| sSrcRate.trim().equalsIgnoreCase("null") ) {
			rate = getPretValRate(dDate,sCuryCode,portCode,yssRateBase);
			return rate;
		}
		
		//基础汇率
		if (yssRateBase.equals(YssOperCons.YSS_RATE_BASE)) {
			rate = this.getSettingOper().getCuryRate(//基础汇率
					dDate,//汇率日期
					sSrcRate == null ? "" :sSrcRate ,//基础汇率来源
					sFieldRate == null ? "" :sFieldRate ,//基础汇率来源字段
					"",//组合来源
					"",//组合来源字段
					sCuryCode,//币种(原币,本币)
					portCode,//组合
					yssRateBase);//汇率标示
		}else if (yssRateBase.equals(YssOperCons.YSS_RATE_PORT)) {
			rate = this.getSettingOper().getCuryRate(//基础汇率
					dDate, "", "",
					sSrcRate == null ? "" :sSrcRate ,//汇率来源
					sFieldRate == null ? "" :sFieldRate ,//汇率来源字段
					sCuryCode,
					portCode,
					yssRateBase);
		}
		return rate;
	}
	
	/**shashijie 2012-12-14 STORY 3328 获取估值方法汇率表中的汇率 */
	public double getPretValRate(Date dDate, String sCuryCode, String portCode,
			String yssRateBase) throws YssException {
		double rate = 1;
		ResultSet rs = null;
		try {
			String query = getQueryPretValRate(dDate,sCuryCode,portCode);
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				//基础汇率
				if (yssRateBase.equals(YssOperCons.YSS_RATE_BASE)) {
					rate = rs.getDouble("Fbaserate");
				}else {//组合汇率
					rate  = rs.getDouble("Fportrate");
				}
			}
		} catch (Exception e) {
			throw new YssException("获取估值方法汇率表中的汇率出错!",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return rate;
	}

	/**shashijie 2012-12-14 STORY 3328 */
	private String getQueryPretValRate(Date dDate, String sCuryCode,
			String portCode) {
		String sqlString = " Select Fbaserate, Fportrate, Fvaldate, Fcurycode, Fportcode, Fotbaserate1, " +
				" Fotbaserate2, Fotbaserate3 From "+pub.yssGetTableName("Tb_Data_Pretvalrate")+ 
				" Where Fcheckstate = 1 And Fportcode In ( "+operSql.sqlCodes(portCode)+" ) " +
				" And Fvaldate = "+dbl.sqlDate(dDate)+
				" And FCuryCode = "+dbl.sqlString(sCuryCode);
		return sqlString;
	}

	/**shashijie 2012-12-9 STORY 3328 获取节假日延迟天数后的工作日*/
	private String getHolidayDay() throws YssException {
		String workDay = "";
		String portCode = this.etfParam.getPortCode();
		System.out.println(portCode);
		//境内(申购现金替代)
		String hoDay = this.etfParam.getsSGReplaceOverHD();
		//境内延迟天数
		int dayint = this.etfParam.getSSGReplaceOver();
		//境外(申购现金替代)
		String hoDay2 = this.etfParam.getsSGReplaceOverHD2();
		//境外延迟天数
		int dayint2 = this.etfParam.getsSGReplaceOver2();
		//两种获取方式:1.直接获取
		workDay = getWorkDay(new Date(), hoDay, dayint, hoDay2, dayint2);
		//2.获取全部的MAP集合
		/*HashMap map = getWorkDay(portCode,new Date());
		workDay = (String) map.get("sgreplaceover");*/
		return workDay;
	}
	
	/**shashijie 2012-12-09 STORY 3328 公共获取工作日代码
	* @param dDate 日期
	* @param HolidayDay 境内节假日
	* @param dayInt 延迟天数
	* @param HolidayDay2 境外节假日
	* @param dayInt2 延迟天数
	* */
	public String getWorkDay(Date dDate,String HolidayDay,int dayInt,
			String HolidayDay2,int dayInt2) throws YssException {
		Date mDate = dDate;//工作日
		//公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
		operDeal.setYssPub(pub);
		
		//境内
		if (HolidayDay != null && !HolidayDay.trim().equals("")) {
	        mDate = operDeal.getWorkDay(HolidayDay, dDate, dayInt);
		}
		//境外
		if (HolidayDay2 != null && !HolidayDay2.trim().equals("")) {
			//若还考虑境外,则要先推出当天是境外节假日的那天才向后推
			mDate = operDeal.getWorkDay(HolidayDay2, mDate, 0);
			//获得里今天最近一天的境外工作日再向后推N个境外工作日
	        mDate = operDeal.getWorkDay(HolidayDay2, mDate, dayInt2);
		}
        return YssFun.formatDate(mDate);
	}

	/**shashijie 2012-12-9 STORY 3328 公共获取工作日代码,返回MAP,各个结转类型的延迟工作日*/
	public HashMap getWorkDay(String portCode,Date dDate) throws YssException {
		HashMap map = new HashMap();
		ResultSet rs = null;
		String valueDay = "";//推算出的工作日
		try {
			String query = getQueryHolidayDay(portCode);
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				/** Map中的key--对照表
				sgbalanceover - 申购现金差额结转
				sgreplaceover - 申购现金替代结转
				sgdealreplace - 申购应付替代结转
				shbalanceover - 赎回现金差额结转
				shreplaceover - 赎回现金替代结转
				shdealreplace - 赎回应付替代结转
				beginsupply - 补票起始日
				dealdaynum - 补票完成日
				lastestdealdaynum - 强制处理日
				*/
				//补票起始日
				if (isParam(rs.getString("Jnovertype"),"beginsupply")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fbeginsupply"), 
							rs.getString("JWdayscode"), rs.getInt("Fbeginsupply2"));
					map.put("beginsupply", valueDay);
				}
				//补票完成日
				if (isParam(rs.getString("Jnovertype"),"dealdaynum")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fdealdaynum"), 
							rs.getString("JWdayscode"), rs.getInt("Fdealdaynum2"));
					map.put("dealdaynum", valueDay);
				}
				//强制处理日
				if (isParam(rs.getString("Jnovertype"),"lastestdealdaynum")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Flastestdealdaynum"), 
							rs.getString("JWdayscode"), rs.getInt("Flastestdealdaynum2"));
					map.put("lastestdealdaynum", valueDay);
				}
				//申购现金差额结转
				if (isParam(rs.getString("Jnovertype"),"sgbalanceover")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fsgbalanceover"), 
							rs.getString("JWdayscode"), rs.getInt("Fsgbalanceover2"));
					map.put("sgbalanceover", valueDay);
				}
				//申购应付替代结转
				if (isParam(rs.getString("Jnovertype"),"sgdealreplace")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fsgdealreplace"), 
							rs.getString("JWdayscode"), rs.getInt("Fsgdealreplace2"));
					map.put("sgdealreplace", valueDay);
				}
				//申购现金替代结转
				if (isParam(rs.getString("Jnovertype"),"sgreplaceover")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fsgreplaceover"), 
							rs.getString("JWdayscode"), rs.getInt("Fsgreplaceover2"));
					map.put("sgreplaceover", valueDay);
				}
				//赎回现金差额结转
				if (isParam(rs.getString("Jnovertype"),"shbalanceover")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fshbalanceover"), 
							rs.getString("JWdayscode"), rs.getInt("Fshbalanceover2"));
					map.put("shbalanceover", valueDay);
				}
				//赎回应付替代结转
				if (isParam(rs.getString("Jnovertype"),"shdealreplace")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fshdealreplace"), 
							rs.getString("JWdayscode"), rs.getInt("Fshdealreplace2"));
					map.put("shdealreplace", valueDay);
				}
				//赎回现金替代结转
				if (isParam(rs.getString("Jnovertype"),"shreplaceover")) {
					valueDay = getWorkDay(dDate, rs.getString("Jndayscode"), rs.getInt("Fshreplaceover"), 
							rs.getString("JWdayscode"), rs.getInt("Fshreplaceover2"));
					map.put("shreplaceover", valueDay);
				}
			}
		} catch (Exception e) {
			throw new YssException("获取推算出的工作日出错！",e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return map;
	}
	
	/**shashijie 2012-12-12 STORY 3328 根据组合,日期,节假日代码KEY,获取节假日推出的工作日 */
	public String getWorkDay(String portCode,Date dDate,String key) throws YssException {
		Date mDate = dDate;
		//获取改组合下所有的节假日推出的工作日
		HashMap map = getWorkDay(portCode, dDate);
		if (map.containsKey(key)) {
			String sDate = (String)map.get(key);
			mDate = YssFun.toDate(sDate);
		}
        return YssFun.formatDate(mDate);
	}
	
	/**shashijie 2012-12-9 STORY 3328 */
	private String getQueryHolidayDay(String portCode) {
		String sqlString = " Select b.Jndayscode, b.Jnovertype, b.Jwdayscode, b.Jwovertype, a.Fbeginsupply," +
				" a.Fdealdaynum, a.Flastestdealdaynum, a.Fsgbalanceover, a.Fsgreplaceover, a.Fshbalanceover," +
				" a.Fshreplaceover, a.Fshdealreplace, a.Fsgdealreplace, a.Fbeginsupply2, a.Fdealdaynum2," +
				" a.Flastestdealdaynum2, a.Fsgbalanceover2, a.Fsgreplaceover2, a.Fshbalanceover2, a.Fshreplaceover2," +
				" a.Fshdealreplace2, a.Fsgdealreplace2 " +
				" From "+pub.yssGetTableName("Tb_Etf_Param")+" a " +
				" Left Join (Select c.Fholidayscode As Jndayscode, c.Fovertype As Jnovertype, " +
				" d.Fholidayscode As Jwdayscode, d.Fovertype As Jwovertype, c.Fportcode " +
				" From (Select C1.Fportcode, C1.Fholidayscode, C1.Fovertype From " +
				pub.yssGetTableName("Tb_Etf_Paramhoildays")+" C1 Where Instr(C1.Fovertype, '2') = 0 " +
				//审核状态暂时不考虑
				" And c1.FCheckState = 1 Order By C1.Fovertype, C1.Fportcode) c " +
				" Left Join (Select D1.Fportcode, D1.Fholidayscode, D1.Fovertype From " +
				pub.yssGetTableName("Tb_Etf_Paramhoildays")+" D1 Where Instr(D1.Fovertype, '2') > 0 " +
				//审核状态暂时不考虑
				" And d1.FCheckState = 1 Order By D1.Fovertype, D1.Fportcode) d On c.Fportcode =" +
				" d.Fportcode And c.Fovertype = Substr(d.Fovertype, 1, Length(d.Fovertype) - 1)" +
				" Where c.Fportcode = "+dbl.sqlString(portCode)+
				" Order By c.Fovertype, d.Fportcode) b On a.Fportcode = b.Fportcode ";
		return sqlString;
	}
	
	/**shashijie 2012-12-9 STORY 3328 字符串比较,相同返回true */
	private boolean isParam(String s1, String s2) {
		boolean flag = false;
		if (s1==null || s2==null) {
			return flag;
		}
		if (s1.equalsIgnoreCase(s2)) {
			flag = true;
		}
		return flag;
	}
	/**
     * 获取ETF台账报表列参数词汇
     * @return
     * @throws YssException 
     */
    private String getETFBookVocabulate() throws YssException {
    	String sETFBookVocabulate="";
		try{
			VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sETFBookVocabulate ="voc"+ vocabulary.getVoc(YssCons.YSS_ETFBOOK_SubscribeData+","+
            		                                     YssCons.YSS_ETFBOOK_RedeemData+","+
            		                                     YssCons.YSS_ETFBOOK_RightData+","+
            		                                     YssCons.YSS_ETFBOOK_SupplyAndForceData+","+
            		                                     YssCons.YSS_ETFBOOK_OtherData+","+
            		                                     YssCons.YSS_ETFBOOK_QUITMONEYVALUE + "," +
            		                                     YssCons.YSS_MTV_EXCHANGERATE)
                                     //add by songjie 2012.12.04 STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001
            		                 + getHolidayGroupInfo();//获取已审核节假日群数据
		}catch(Exception e){
			throw new YssException("获取ETF台账报表列参数词汇出错！",e);
		}
		return sETFBookVocabulate;
	}

    /**
     * add by songjie 2012.12.04
     * STORY #3328需求深圳-[易方达基金]QDV4.0[高]20121123001
     * 获取已审核的节假日群数据
     * 格式：节假日群代码-节假日群名称 
     * @return
     * @throws YssException
     */
    private String getHolidayGroupInfo() throws YssException{
    	StringBuffer holidayVocs = new StringBuffer();
    	String strVocs = "\f\f";
    	ResultSet rs = null;
    	String strSql = "";
    	try{
    		strSql = " select FHOLIDAYSCODE,FHOLIDAYSNAME from Tb_Base_Holidays where FCheckState = 1";
    		rs = dbl.openResultSet(strSql);
    		while(rs.next()){
    			holidayVocs.append(rs.getString("FHOLIDAYSCODE")).append("\t")
    			.append(rs.getString("FHOLIDAYSCODE")).append("-").append(rs.getString("FHOLIDAYSNAME")).append("\t")
    			.append("holiday\tnull\t1\t\t\t\t\t\t\t\n");
    		}
    		strVocs += holidayVocs.toString();
    		if(strVocs.length() > 1){
    			strVocs = strVocs.substring(0, strVocs.length() - 1);
    		}
    		return strVocs;
    	}catch(Exception e){
    		throw new YssException("获取节假日群数据出错");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
	/*检查前台选择的现金账户的组合是否是界面上当前设置的组合下的现金账户 */
    private String checkACCPortCode(String sType) throws YssException{
    	String strAccPortCode="";
    	ResultSet rs=null;
    	StringBuffer buff=null;
    	try{
    		buff=new StringBuffer(200);
    		buff.append(" select * from ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
    		buff.append(" where FCashAccCode =").append(dbl.sqlString(this.etfParam.getCashAccCode()));
    		
    		rs=dbl.openResultSet(buff.toString());
    		buff.delete(0,buff.length());
    		if(rs.next()){
    			strAccPortCode=rs.getString("FPortCode");
    		}
    	}catch(Exception e){
    		throw new YssException("检查前台选择的现金账户的组合是否是界面上当前设置的组合下的现金账户出错！",e);
    	}
        return strAccPortCode;
    }
    
    /*检查前台选择的现金账户的组合是否是界面上当前设置的组合下的现金账户 */
    private String checkClearACCPortCode(String sType) throws YssException{
    	String strAccPortCode="";
    	ResultSet rs=null;
    	StringBuffer buff=null;
    	try{
    		buff=new StringBuffer(200);
    		buff.append(" select * from ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
    		buff.append(" where FCashAccCode =").append(dbl.sqlString(this.etfParam.getClearAccCode()));
    		
    		rs=dbl.openResultSet(buff.toString());
    		buff.delete(0,buff.length());
    		if(rs.next()){
    			strAccPortCode=rs.getString("FPortCode");
    		}
    	}catch(Exception e){
    		throw new YssException("检查前台选择的现金账户的组合是否是界面上当前设置的组合下的现金账户出错！",e);
    	}
        return strAccPortCode;
    }
    
}









