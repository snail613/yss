package com.yss.main.operdeal.report.navrep;

import com.yss.util.YssException;
import com.yss.util.YssCons;
import com.yss.util.YssFun;
import java.util.ArrayList;
import com.yss.main.operdeal.report.navrep.pojo.NavRepBean;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yss.util.YssD;

public class NavInvest
    extends BaseNavRep {
    public NavInvest() {
    }

    protected void initReport(java.util.Date dDate, String sPortCode,
                              String sInvMgrCode) throws YssException {
        this.valDefine =
            "FPayType";
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.invMgrCode = sInvMgrCode;
    }

    protected ArrayList getGradeData(String groupStr, int Grade) throws
        YssException {
        NavRepBean navRep = null;
        ArrayList valInvestBeans = null;
        String strSql = "";
        String strDetailSql = "";
        String[] valCashDefineAry = null;
        ResultSet rs = null;
        ResultSet sumRs = null;
//        ArrayList leftArr = new ArrayList();
//        ArrayList fieldsArr = new ArrayList();
        String sGroupSql = "";
        String OrderStr = "";
		 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        String sKeyCode = "";
        String sKeyName = "";
		//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        try {
            valInvestBeans = new ArrayList();
            strDetailSql = " select a.*,b.FIVPayCatName as FIVPayCatName,c.FAttrClsName as FAttrClsName," +
                //dbl.sqlToChar(" a.FPayType ") + 
            	" (case when a.FPayType = 1 then case when tran.FTransition = 1 then 0 else 1 end else 0 end) " + 
                dbl.sqlJoinString() + dbl.sqlString("##") +
                dbl.sqlJoinString().trim() + " a.FCuryCode " + dbl.sqlJoinString().trim() + dbl.sqlString("##") + dbl.sqlJoinString().trim() + //增加了排序时的顺序排列字段，以便在多币种时能正确显示。sj edit 20080714
                //" a.FIVPayCatCode  as FOrderCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " a.FIVPayCatCode "+dbl.sqlJoinString().trim()+dbl.sqlString("##")+dbl.sqlJoinString().trim()+" a.fattrclscode  as FOrderCode " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                
                ", Nvl(tran.FTransition,0) as FTransition " +//20121121 added by liubo.Bug #6379 //关联运营收支设置表中的预提转待摊字段
                " from (select * from " +
                this.tempViewName + " where FNAVDate = " +
                dbl.sqlDate(this.dDate) + " and FPortCode = " +
                dbl.sqlString(this.portCode) +
                ") a " +
                " left join (select FIVPayCatCode,FIVPayCatName from Tb_Base_InvestPayCat where FCheckState = 1)" +
                " b on a.FIVPayCatCode = b.FIVPayCatCode "+
				//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " left join (select FAttrClsCode,FAttrClsName from " +
                pub.yssGetTableName("Tb_Para_AttributeClass") +
                " where fcheckstate=1)c on a.FAttrClsCode = c.FAttrClsCode " +
              //20121121 added by liubo.Bug #6379 //关联运营收支设置表中的预提转待摊字段
                " left join (select * from " + pub.yssGetTableName("tb_para_investpay") + 
                " where FStartDate <= " + dbl.sqlDate(dDate) + " and FACBeginDate <= " + dbl.sqlDate(dDate) + 
                " and FACEndDate >= " + dbl.sqlDate(dDate) + " and FTransitionDate <= " + dbl.sqlDate(dDate) + 
                //add by songjie 2013.01.24 应查询相关组合的数据
                " and FPortCode = " + dbl.sqlString(this.portCode) + 
                //20130227 added by liubo.Bug #7122
                //取运营费用设置数据是否启用预提转待摊时，需要根据启用日期明确取最近的一条数据。
                //避免在启用第二期运营收支设置的情况下，仍然取到上一期的数据
                //======================================
                " and FStartDate in (select max(FStartDate) from " + pub.yssGetTableName("tb_para_investpay") + " where FPortCode = " + dbl.sqlString(this.portCode) + ")" +
                //==================end====================
                " ) tran " +
                " on a.FIVPayCatCode = tran.FIVPayCatCode" ;
					//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            
            //20121121 modified by liubo.Bug #6379
            //在原有SQL语句的基础上加一个左连接，使用变量绑定方式查询会报SQL语句解析错误的异常
            //================================
//            rs = dbl.queryByPreparedStatement(strDetailSql); //modify by fangjiang 2011.08.14 STORY #788
            rs = dbl.openResultSet(strDetailSql);
            //=============end===================
            while (rs.next()) {
            	
            	//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
            	sKeyCode = (rs.getString("FIVPayCatCode") == null ?" " : rs.getString("FIVPayCatCode"))+(rs.getString("fattrclscode").trim().length() == 0 ?" " :"-"+ rs.getString("fattrclscode"));
            	sKeyName = setBlo(Grade) + (rs.getString("FIVPayCatName") == null ?" ":rs.getString("FIVPayCatName"))+(rs.getString("FAttrClsName") == null ?" ":"-"+rs.getString("FAttrClsName"));
            	//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
//                navRep.setKeyCode(rs.getString("FIVPayCatCode") == null ?
//                                  " " : rs.getString("FIVPayCatCode"));
//                navRep.setKeyName(setBlo(Grade) + (rs.getString("FIVPayCatName") == null ?
//                    " " :
//                    rs.getString("FIVPayCatName")));
                navRep.setKeyCode(sKeyCode);
                navRep.setKeyName(sKeyName);
					//--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                navRep.setDetail(0); //明细
                //------------------设置净值方向----------------------------------
                if (rs.getString("FPayType").indexOf("1") >= 0) {
                	
                	//20121121 added by liubo.Bug #6379 
                	//某个预提项启用的预提转待摊，在估值日期大于等于预提转待摊的转换日期时，该预提项应该是属于收入
                	//==================================
                	if (rs.getString("FTransition").equals("1"))
                	{
                		navRep.setInOut(1);
                	}
                	//==============end====================
                	else
                	{
                		navRep.setInOut( -1);
                	}
                } else {
                    navRep.setInOut(1);
                }
                //--------------------------------------------------------------

                navRep.setReTypeCode("Invest");
                //navRep.setCuryCode(" ");
                navRep.setCuryCode(rs.getString("FCuryCode").length() > 0 ?
                                   rs.getString("FCuryCode") : " "); //加入币种。sj edit 20080703.

                navRep.setBookCost(rs.getDouble("FAccBalance"));
                navRep.setMarketValue(YssD.sub(rs.getDouble("FAccBalance"),
                                               0));
                navRep.setPortBookCost(rs.getDouble("FPortCuryBal"));
                navRep.setPortexchangeValue(rs.getDouble("FSYPortCuryBal"));
                navRep.setPortMarketValue(YssD.add(navRep.
                    getPortBookCost(),
                    navRep.getPortexchangeValue()));
                navRep.setGradeType2(rs.getString("FIVPayCatCode"));
                navRep.setGradeType3(rs.getString("fattrclscode"));//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                navRep.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                navRep.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }

                valInvestBeans.add(navRep);
            }
//         strSql = " select a.*,b.FVocName as FPayTypeName from " +
//               " (select " +
//               " FPayType as FOrderCode," +
//               " sum(" + dbl.sqlIsNull("FAccBalance", "0") +
//               " ) as FAccBalance," +
//               " sum(" + dbl.sqlIsNull("FBaseCuryBal", "0") +
//               " ) as FBaseCuryBal," +
//               " sum(" + dbl.sqlIsNull("FPortCuryBal", "0") +
//               " ) as FPortCuryBal," +
//               " sum(" + dbl.sqlIsNull("FSYPortCuryBal", "0") +
//               " ) as FSYPortCuryBal," +
//               " FBaseCuryRate,FPortCuryRate,FPortCode,FPayType from " +
//               this.tempViewName +
//               " where FPortCode = " +
//               dbl.sqlString(this.portCode) +
//               " and FNAVDate = " + dbl.sqlDate(this.dDate) +
//               " group by FPortCode,FPayType,FBaseCuryRate,FPortCuryRate) a " +
//               " left join Tb_Fun_Vocabulary b on " +
//               dbl.sqlToChar("a.FPayType") +
//               " = b.FVocCode and b.FVocTypeCode = " +
//               dbl.sqlString(YssCons.Yss_INVESTPAYCAT_TYPE);
            strSql = " select a.*,b.FVocName as FPayTypeName " +
                ",FBaseRate as FBaseCuryRate,FPortRate as FPortCuryRate " +
                " from " +
                //20121218 modified by liubo.Bug #6379
                //某个预提项启用了预提转待摊，且当前日期大于等于转换日期，该预提项应计入收入
                //=======================================
                " (select FOrderCode,sum(FAccBalance) as FAccBalance,sum(FBaseCuryBal) as FBaseCuryBal,sum(FPortCuryBal) as FPortCuryBal,sum(FSyPortCuryBal) as FSyPortCuryBal, " +
                " FCuryCode,FPortCode,FPayType from (select " +
                " distinct " +	//20130227 added by liubo.Bug #7122
//                "a." + dbl.sqlToChar("FPayType") + 
                " (case when a.FPayType = 1 then case when tran.FTransition = 1 then 0 else 1 end else 0 end) " + 
                dbl.sqlJoinString().trim() + //DB2中连接FPayType和FCuryCode时由于类型不一致报错，现对FPayType修改  edit by jc
                dbl.sqlString("##") + dbl.sqlJoinString().trim() + "a.FCuryCode as FOrderCode," + //增加了排序时的顺序排列字段，以便在多币种时能正确显示,并且能够插入多条汇总项。sj edit 20080714
                " sum(" + dbl.sqlIsNull("a.FAccBalance", "0") +
                " ) as FAccBalance," +
                " sum(" + dbl.sqlIsNull("a.FBaseCuryBal", "0") +
                " ) as FBaseCuryBal," +
                " sum(" + dbl.sqlIsNull("a.FPortCuryBal", "0") +
                " ) as FPortCuryBal," +
                " sum(" + dbl.sqlIsNull("a.FSYPortCuryBal", "0") +
                " ) as FSYPortCuryBal," +
                " a.FCuryCode," +
                //" FBaseCuryRate,FPortCuryRate," +
                "a.FPortCode," +
                "(case when a.FPayType = 1 then case when tran.FTransition = 1 then 0 else 1 end else 0 end) as FPayType " +
                " from " +
                this.tempViewName + " a " +
                " left join (select distinct FIVPayCatCode,(case when FTransition = 1 then case when FTransitionDate  <= " + dbl.sqlDate(this.dDate) + " then 1 else 0 end else 0 end) as FTransition " +
                " from " + pub.yssGetTableName("tb_para_investpay") + " " +
                //20130227 added by liubo.Bug #7122
                //取运营费用设置数据是否启用预提转待摊时，需要根据启用日期明确取最近的一条数据。
                //避免在启用第二期运营收支设置的情况下，仍然取到上一期的数据
                //======================================
                " where FStartDate in (select max(FStartDate) from " + pub.yssGetTableName("tb_para_investpay") + " where FPortCode = " + dbl.sqlString(this.portCode) + ")" +
                //=================end=====================
                " ) tran on a.FIVPayCatCode = tran.FIVPayCatCode" +
                " where a.FPortCode = " +
                dbl.sqlString(this.portCode) +
                " and a.FNAVDate = " + dbl.sqlDate(this.dDate) +
                " group by a.FPortCode,a.FPayType" +
                ",a.FCuryCode,tran.FTransition " +
                //",FBaseCuryRate,FPortCuryRate" +
                ") group by FCuryCode,FPortCode,FPayType,FOrderCode) a " +
                //===================end====================
                " left join Tb_Fun_Vocabulary b on " +
                dbl.sqlToChar("a.FPayType") +
                " = b.FVocCode and b.FVocTypeCode = " +
                dbl.sqlString(YssCons.Yss_INVESTPAYCAT_TYPE) +
                " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " + //将汇率的获取提取出来。sj edit 20080714.
                pub.yssGetTableName("Tb_Data_ValRate") +
                " where FValDate = " + dbl.sqlDate(this.dDate) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " ) Rate on Rate.RFCuryCode = a.FCuryCode";
            sumRs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (sumRs.next()) {
                navRep = new NavRepBean();
                navRep.setNavDate(this.dDate); //净值日期
                navRep.setPortCode(this.portCode);
                navRep.setOrderKeyCode(sumRs.getString("FOrderCode"));
                navRep.setKeyCode(sumRs.getString("FPayType") == null ?
                                  " " : sumRs.getString("FPayType"));
                navRep.setKeyName(setBlo(Grade) + (sumRs.getString("FPayTypeName") == null ?
                    " " :
                    sumRs.getString("FPayTypeName")));
                navRep.setDetail(1); //汇总
                //------------------设置净值方向----------------------------------
                if (sumRs.getString("FPayType").indexOf("1") >= 0) {
                    navRep.setInOut( -1);
                } else {
                    navRep.setInOut(1);
                }
                //--------------------------------------------------------------
                navRep.setReTypeCode("Invest");
                //navRep.setCuryCode(" ");
                navRep.setCuryCode(sumRs.getString("FCuryCode").length() > 0 ?
                                   sumRs.getString("FCuryCode") : " "); //加入币种。sj edit 20080714.
                navRep.setBookCost(sumRs.getDouble("FAccBalance"));
                navRep.setMarketValue(YssD.sub(sumRs.getDouble("FAccBalance"),
                                               0));
                navRep.setPortBookCost(sumRs.getDouble("FPortCuryBal"));
                navRep.setPortexchangeValue(sumRs.getDouble("FSYPortCuryBal"));
                navRep.setPortMarketValue(YssD.add(navRep.
                    getPortBookCost(),
                    navRep.getPortexchangeValue()));
                navRep.setGradeType1(sumRs.getString("FPayType"));
                navRep.setBaseCuryRate(sumRs.getDouble("FBaseCuryRate"));
                navRep.setPortCuryRate(sumRs.getDouble("FPortCuryRate"));
                if (!this.invMgrCode.equalsIgnoreCase("total")) {
                    navRep.setInvMgrCode(this.invMgrCode);
                } else {
                    navRep.setInvMgrCode("total");
                }
                valInvestBeans.add(navRep);
            }
            return valInvestBeans;
        } catch (Exception e) {
            throw new YssException("自视图获取运营信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(sumRs);
        }

    }

    protected String buildRepView() throws
        YssException {
        String strSql = "";
        String tempViewName = "";
        String strFields = "";
        try {

            //------------------------------------------------------------------------
            strSql = "select a. *," +
                "FSYBal," +
                "FSYBaseCuryBal," +
                "FSYPortCuryBal" +
                " from (SELECT a1.*, a2.FBaseRate as FBaseCuryCode, a2.FPortRate AS FPortCuryCode" +
                " FROM (select FPortCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " a11.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                
                //20121123 modified by liubo.Bug #6379
                //查询出的预提项，需要关联投资运营收支设置中，该项是否启用预提转待摊，且转换日期是否大于等于当前日期
                //若符合上述条件，需要转为待摊进行处理
                //==================================
                " (case when FPayType = '1' then case when a13.FTRANSITION = '1' and a13.FTRANSITIONDATE <= " + dbl.sqlDate(dDate) + 
                " then '0' else '1' end else '0' end) as FPayType," +
                //===============end===================
                " FStorageDate as FNAVDate," +
                " a11.FIVPayCatCode," +
                " sum(FBal) as FAccBalance," +
                " sum(FBaseCuryBal) as FBaseCuryBal," +
                " sum(FPortCuryBal) as FPortCuryBal," +
                " FCuryCode" +
                " from " + pub.yssGetTableName("Tb_Stock_Invest") +
                " a11 join (select FIVPayCatCode, FPayType " +
                " from Tb_Base_InvestPayCat where 1 = 1 " +
                //" and FPayType = 1 " + // sj edit 20080730 需要收入类的。暂无bug
                " and " +
                " FCheckState = 1) a12 on a11.FIVPayCatCode = a12.FIVPayCatCode " +

                //20130229 modified by liubo.Bug #7122
                //关联预提转待摊信息时，需要明确启用日期
                //取距生成报表日期最近的一个启用日期的运营收支项目
                //==================================
                " left join (select b.FIVPayCatCode,b.FTRANSITION,b.FTRANSITIONDATE from " +
                " ((select Fivpaycatcode,max(FStartDate) as FStartDate from " + pub.yssGetTableName("tb_para_investpay") + "  where FStartDate <= " + dbl.sqlDate(this.dDate) + 
                " and FACBeginDate <= " + dbl.sqlDate(this.dDate) + 
                " and FACEndDate >= " + dbl.sqlDate(this.dDate) + 
                " and FCheckState = 1 " +
                " and FPortCode = " + dbl.sqlString(this.portCode) + " group by Fivpaycatcode) a  " +
                " left join " +
                " (select distinct FIVPayCatCode,FTRANSITION,FTRANSITIONDATE,FStartDate " +
                " from " + pub.yssGetTableName("tb_para_investpay") +
                " where FStartDate <= " + dbl.sqlDate(this.dDate) + 
                " and FACBeginDate <= " + dbl.sqlDate(this.dDate) + 
                " and FACEndDate >= " + dbl.sqlDate(this.dDate) + 
                " and FCheckState = 1 " +
                " and FPortCode = " + dbl.sqlString(this.portCode) + ") b on a.Fivpaycatcode = b.Fivpaycatcode and a.FStartDate = b.Fstartdate)) a13 " +
                //===============end===================
                " on a11.Fivpaycatcode = a13.FIVPayCatCode" +
                " where FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                //-------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------
                " and FCheckState = 1 " +
                " group by FPortCode, FPayType,a11.FIVPayCatCode,FCuryCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " a11.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "FStorageDate,a13.FTRANSITIONDATE,a13.FTRANSITION) a1" +
                " LEFT JOIN " + pub.yssGetTableName("Tb_Data_ValRate") +
                " a2 ON a1.FNAVDate = a2.Fvaldate" +
                " AND a1.FPortCode = a2.FPortCode AND a1.FCuryCode = a2.FCuryCode) a" +
                //---------------------------------------------------------------------------------------------获取汇兑损益的值
                " left join (select FPortCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " a21.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " FPayType,a21.FIVPayCatCode," +
                " sum(FBal) as FSYBal," +
                " sum(FBaseCuryBal) as FSYBaseCuryBal," +
                " sum(FPortCuryBal) as FSYPortCuryBal" +
                " from " + pub.yssGetTableName("Tb_Stock_InvestPayRec") +
                " a21 " +
                " join (select FIVPayCatCode," +
                " FPayType from Tb_Base_InvestPayCat where 1 = 1 " +
                //" and  FPayType = 1  " +  //sj edit 20080730  需要收入类的。暂无bug
                " and FCheckState = 1)" +
                " a22 on a21.FIVPayCatCode = a22.FIVPayCatCode " +
                " where FTsfTypeCode = '99' and FSubTsfTypeCode like '99%IV' and " +
                " FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                //-------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------
                " and FCheckState = 1" +
                " group by FPortCode,FPayType,a21.FIVPayCatCode" +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " , a21.fattrclscode) b"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " on a.FPortCode = b.FPortCode " +
                " and a.FPayType = b.FPayType and a.FIVPayCatCode = b.FIVPayCatCode"+
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and a.fattrclscode = b.fattrclscode";
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
            //} //在此处拼写所需数据的sql语句
            //------------------------------------------------------------------------
            tempViewName = "V_Temp_Invest_" + pub.getUserCode();
            if (dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
           //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 添加所属分类字段
            strFields = "FPortCode,FAttrclscode,FPayType,FNAVDate,FIVPayCatCode,FAccBalance,FBaseCuryBal,FPortCuryBal,FCuryCode,FBaseCuryRate,FPortCuryRate,FSyBal,FSyBaseCuryBal,FSyPortCuryBal";
            if (dbl.getDBType() == YssCons.DB_ORA) {
                String tempStr = "create view " + tempViewName + "(" + strFields +
                    ") as (" +
                    strSql +
                    ")";
                dbl.executeSql(tempStr);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                dbl.executeSql("create view " + tempViewName + "(" + strFields +
                               ") as " + strSql);
                //dbl.executeSql("insert into " + tempViewName + "(" + strSql +
                //")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
            return tempViewName;

        } catch (YssException e) {
            throw new YssException("生成运营视图出错！");
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	 throw new YssException("生成运营视图出错！");
		}
    }

}
