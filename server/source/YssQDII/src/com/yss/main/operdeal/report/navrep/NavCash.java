package com.yss.main.operdeal.report.navrep;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.util.*;

public class NavCash
    extends BaseNavRep {
    public NavCash() {
    }

    protected void initReport(java.util.Date dDate, String sPortCode,
                              String sInvMgrCode) throws YssException {
    	//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 添加所属分类FAttrClsCode
        this.valDefine =
            "FAccType;FSubAccType;FAttrClsCode;FCuryCode;FCashAccCode;FSubTsfTypeCode";
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.invMgrCode = sInvMgrCode;
    }

    protected ArrayList getGradeData(String groupStr, int Grade) throws
        YssException {
        NavRepBean navRep = null;
        ArrayList valCashBeans = null;
        String strSql = "";
        String strDetailSql = "";
        String[] valCashDefineAry = null;
        ResultSet rs = null;
        ArrayList leftArr = new ArrayList();
        ArrayList fieldsArr = new ArrayList();
        String sGroupSql = "";
        String OrderStr = "";
        String[] gradeTypes = null;
        try {
            buildLeftSql(leftArr, fieldsArr);
            valCashBeans = new ArrayList();
            if (Grade == fields.length) {
                OrderStr = buildOrderStr(groupStr) + " as FOrderCode,";
                strDetailSql = " select " + OrderStr +
                    " V.FSubTsfTypeCode," +
                    " V.FAccType," +
                    " V.FSubAccType," +
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    " V.FAttrClsCode,"+
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    " V.FCuryCode," +
                    " V.FCashAccCode," +
                    " Trans.FSubTsfTypeName," +
                    " V.FCuryCode," +
                    " Rate.FBaseRate as FBaseCuryRate," +
                    " Rate.FPortRate as FPortCuryRate," +
                    dbl.sqlIsNull("FAccBalance", "0") +
                    " as FAccBalance," +
                    dbl.sqlIsNull("FPortCuryBal", "0") +
                    " as FPortCuryBal," +
                    dbl.sqlIsNull("FSYPortCuryBal", "0") +
                    " as FSYPortCuryBal from " +
                    tempViewName + " v " +
                    " left join (select FSubTsfTypeCode as TFSubTsfTypeCode,FSubTsfTypeName from Tb_Base_SubTransferType where FCheckState = 1) Trans on " +
                    " v.FSubTsfTypeCode = Trans.TFSubTsfTypeCode " +
                    " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " +
                    pub.yssGetTableName("Tb_Data_ValRate") +
                    " where FValDate = " + dbl.sqlDate(this.dDate) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " ) Rate on Rate.RFCuryCode = V.FCuryCode" +
                    " where FSubTsfTypeCode <> '9905DE' and V.FNAVDate = " +
                    dbl.sqlDate(this.dDate) + " and FPortCode = " +
                    dbl.sqlString(this.portCode);
                rs = dbl.queryByPreparedStatement(strDetailSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    /*navRep.setKeyCode(rs.getString("FSubTsfTypeCode") == null ?
                                      " " : rs.getString("FSubTsfTypeCode"));*/
                    navRep.setKeyCode( (rs.getString("FSubTsfTypeCode") == null &&
                                        rs.getString("FCashAccCode") == null) ?
                                      " " :
                                      rs.getString("FCashAccCode") + "-" +
                                      rs.getString("FSubTsfTypeCode"));
                    navRep.setKeyName(setBlo(Grade) +
                                      (rs.getString("FSubTsfTypeName") == null ?
                                       " " : rs.getString("FSubTsfTypeName")));
                    navRep.setDetail(0); //明细
                    //------------------设置净值方向----------------------------------
                    if (rs.getString("FSubTsfTypeCode").indexOf("07") >= 0) {
                        navRep.setInOut( -1);
                    } else if (rs.getString("FSubTsfTypeCode").indexOf("06") >= 0) {
                        navRep.setInOut(1);
                    }
                    //--------------------------------------------------------------
                    if (rs.getString("FSubTsfTypeCode").indexOf("07") >= 0) {
                        navRep.setInOut( -1);
                    } else if (rs.getString("FSubTsfTypeCode").indexOf("06") >= 0) {
                        navRep.setInOut(1);
                    }

                    navRep.setReTypeCode("Cash");
                    navRep.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                    navRep.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    navRep.setCuryCode(rs.getString("FCuryCode"));
                    navRep.setBookCost(rs.getDouble("FAccBalance"));
                    navRep.setMarketValue(YssD.sub(rs.getDouble("FAccBalance"),
                        0));
                    navRep.setPortBookCost(rs.getDouble("FPortCuryBal"));
                    navRep.setPortexchangeValue(rs.getDouble("FSYPortCuryBal"));
                    navRep.setPortMarketValue(YssD.add(navRep.
                        getPortBookCost(),
                        navRep.getPortexchangeValue()));
                    //-----------------modify by wuweiqi 20101224 QDV4易方达2010年11月17日01_A----------------------------//
                    // 如果原币的值为0，则不需要将其录入
                    if (navRep.getBookCost() == 0 && navRep.getMarketValue()==0 && navRep.getPayValue() == 0 ) {
                       continue;   
                     }
                    //----------------------end by wuweiqi 20101224  ----------------------------------------------------//
                    navRep.setGradeType1(rs.getString("FAccType"));
                    navRep.setGradeType2(rs.getString("FSubAccType"));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    navRep.setGradeType3(rs.getString("FAttrClsCode"));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    navRep.setGradeType4(rs.getString("FCuryCode"));
                    navRep.setGradeType5(rs.getString("FCashAccCode"));
                    navRep.setGradeType6(rs.getString("FSubTsfTypeCode"));
                    if (!this.invMgrCode.equalsIgnoreCase("total")) {
                        navRep.setInvMgrCode(this.invMgrCode);
                    } else {
                        navRep.setInvMgrCode("total");
                    }
                    valCashBeans.add(navRep);
                }
                return valCashBeans;
            } else {
                OrderStr = buildOrderStr(groupStr) + " as FOrderCode,";
                sGroupSql = " group by FPortCode," + groupStr;
                strSql = " select  dat.*," +
                   // (Grade == 3 || Grade == 4 ?
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    (Grade == 4 || Grade == 5 ?  //添加了所属分类项目级别
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                     "Rate.FBaseRate as FBaseCuryRate,Rate.FPortRate as FPortCuryRate,"
                     : "") +
                    fieldsArr.get(Grade - 1) +
                    " from (select " +
                    OrderStr +
                    " sum (" + dbl.sqlIsNull("FAccBalance", "0") +
                    ") as FAccBalance," +
                    " sum (" + dbl.sqlIsNull("FPortCuryBal", "0") +
                    ") as FPortCuryBal," +
                    //(Grade == 3 || Grade == 4 ?
                    // " FBaseCuryRate," + " FPortCuryRate ," : "") +
                    " sum (" + dbl.sqlIsNull("FSYPortCuryBal", "0") +
                    ") as FSYPortCuryBal," +
                    ( (String) fieldsArr.get(Grade - 1)).split(",")[0] +
                    (Grade == fields.length - 1 ?
                     "," + ( (String) fieldsArr.get(Grade - 1)).split(",")[2] :
                     "") +
                    " from " + tempViewName + // modified by yeshenghong 20111111 BUG 3104
                    " dat where FSubTsfTypeCode = '9905DE' or FSubTsfTypeCode = '' or FSubTsfTypeCode is null" +
                    //" or FSubAccType is null " + //temp edit
                    " and FNAVDate = " +
                    dbl.sqlDate(this.dDate) + " and FPortCode = " +
                    dbl.sqlString(this.portCode) +
                    //" and FPortCuryRate <> 0 and FBaseCuryRate <> 0 " +
                    sGroupSql +
                    //(Grade == 3 || Grade == 4 ?
                    //", FBaseCuryRate," + " FPortCuryRate " : "") +
                    ") dat " +
                    leftArr.get(Grade - 1) +
                    //(Grade == 3 || Grade == 4 ?
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    (Grade == 4 || Grade == 5 ?  //添加了所属分类项目级别
                   //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                     " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " +
                     pub.yssGetTableName("Tb_Data_ValRate") +
                     " where FValDate = " + dbl.sqlDate(this.dDate) +
                     " and FPortCode = " + dbl.sqlString(this.portCode) +
                     " ) Rate on Rate.RFCuryCode = dat.FCuryCode" : "");
                //----------------------------------------------------------------------------------------
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    //if (Grade == 3 || Grade == 4) {
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if (Grade == 4 || Grade == 5) {  //添加了所属分类项目级别
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                        navRep.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
                        navRep.setPortCuryRate(rs.getDouble("FPortCuryRate"));
                    }
                    switch (Grade) {
                        case 1:
                            navRep.setKeyCode(rs.getString("FAccType") == null ?
                                              " " : rs.getString("FAccType"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FAccTypeName") == null ?
                                               " " : rs.getString("FAccTypeName")));
                            break;
                        case 2:
                            navRep.setKeyCode(rs.getString("FSubAccType") == null ?
                                              " " : rs.getString("FSubAccType"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FSubAccTypeName") == null ?
                                               " " :
                                               rs.getString("FSubAccTypeName")));
                            break;
                        case 3:
//                            navRep.setKeyCode(rs.getString("FCuryCode") == null ?
//                                              " " : rs.getString("FCuryCode"));
//                            navRep.setKeyName(setBlo(Grade) +
//                                              (rs.getString("FCuryName") == null ?
//                                               " " : rs.getString("FCuryName")));
                        	navRep.setKeyCode(rs.getString("FAttrClsCode") == null ?
                                              " " : rs.getString("FAttrClsCode"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FAttrClsName") == null ?
                                               " " :
                                               rs.getString("FAttrClsName")));
                            break;
                        case 4:
//                            navRep.setKeyCode(rs.getString("FCashAccCode") == null ?
//                                              " " : rs.getString("FCashAccCode"));
//                            navRep.setKeyName(setBlo(Grade) +
//                                              (rs.getString("FCashAccName") == null ?
//                                               " " : rs.getString("FCashAccName")));
						    navRep.setKeyCode(rs.getString("FCuryCode") == null ?
						    		          " ": rs.getString("FCuryCode"));
						    navRep.setKeyName(setBlo(Grade)+ 
						    		          (rs.getString("FCuryName") == null ?
						    		           " " : rs.getString("FCuryName")));
                  
                            break;
                        case 5:
                            navRep.setKeyCode(rs.getString("FCashAccCode") == null ?
                                              " " : rs.getString("FCashAccCode"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FCashAccName") == null ?
                                               " " : rs.getString("FCashAccName")));
                            break;    
                    }
                    //if (Grade == 4) {
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    if (Grade == 5) {  //添加了所属分类项目级别
                  //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                        navRep.setDetail(0);
                        if (rs.getDouble("FAccAttr") == -1) {
                            navRep.setInOut( -1);
                        }
                    } else {
                        navRep.setDetail(Grade); //汇总
                    }
                    navRep.setReTypeCode("Cash");
                    if (Grade == fields.length - 1 || Grade == fields.length - 2) {
                        navRep.setCuryCode(rs.getString("FCuryCode") == null ?
                                           "" : rs.getString("FCuryCode"));
                    } else {
                        navRep.setCuryCode("汇总：");
                    }
                    navRep.setBookCost(rs.getDouble("FAccBalance"));
                    navRep.setMarketValue(YssD.sub(rs.getDouble("FAccBalance"),
                        0));
                    navRep.setPortBookCost(rs.getDouble("FPortCuryBal"));
                    navRep.setPortexchangeValue(rs.getDouble("FSYPortCuryBal"));
                    navRep.setPortMarketValue(YssD.add(navRep.
                        getPortBookCost(),
                        navRep.getPortexchangeValue()));
                    gradeTypes = navRep.getOrderKeyCode().split("##");
                    switch (gradeTypes.length) {
                        case 1:
                            navRep.setGradeType1(gradeTypes[0]);
                            break;
                        case 2:
                            navRep.setGradeType1(gradeTypes[0]);
                            navRep.setGradeType2(gradeTypes[1]);
                            break;
                        case 3:
                            navRep.setGradeType1(gradeTypes[0]);
                            navRep.setGradeType2(gradeTypes[1]);
                            navRep.setGradeType3(gradeTypes[2]);

                            break;
                        case 4:
                            navRep.setGradeType1(gradeTypes[0]);
                            navRep.setGradeType2(gradeTypes[1]);
                            navRep.setGradeType3(gradeTypes[2]);
                            navRep.setGradeType4(gradeTypes[3]);

                            break;
                        case 5:
                            navRep.setGradeType1(gradeTypes[0]);
                            navRep.setGradeType2(gradeTypes[1]);
                            navRep.setGradeType3(gradeTypes[2]);
                            navRep.setGradeType4(gradeTypes[3]);
                            navRep.setGradeType5(gradeTypes[4]);
                            
                            break;
                    }
                    if (!this.invMgrCode.equalsIgnoreCase("total")) {
                        navRep.setInvMgrCode(this.invMgrCode);
                    } else {
                        navRep.setInvMgrCode("total");
                    }
                    valCashBeans.add(navRep);
                }
                return valCashBeans;
            }
        } catch (Exception e) {
            throw new YssException("自视图获取现金信息出错！");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildRepView() throws
        YssException {
        String strSql = "";
        String tempViewName = "";
        String strFields = "";
        try {

            //------------------------------------------------------------------------
            strSql =
                "select  para.FAccType,para.FSubAccType,para.FCuryCode,cash.* from (" +
                " select a1.FCashAccCode as FCashAccCode, " +
                " a1.FPortCode       as FPortCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                "  a1.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                //" FBaseCuryRate," +
                //" FPortCuryRate," +
                " FNAVDate," +
                " a2.FSubTsfTypeCode as FSubTsfTypeCode," +
                " a1.FAccBalance            as FACCBalance," +
                " a1.FBaseCuryBal    as FBaseCuryBal," +
                " a1.FPortCuryBal    as FPortCuryBal," +
                dbl.sqlIsNull("a2.FRPPortCUryBal", "0") + " as FSYPortCuryBal " +
                //" NVL(a2.FRPPortCUryBal, 0) FSYPortCuryBal " +
                " from ( " +
                //------------------------------------------------------------------------------------获取现金的库存余额
                " select a11.FCashAccCode as FCashAccCode," +
                " FPortCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                "   a11.FAttrclsCode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                //" FBaseCuryRate," +
                //" FPortCuryRate," +
                " FStorageDate as FNAVDate," +
                " sum( " +
                dbl.sqlIsNull("FAccBalance", "0") + " ) as FAccBalance," +
                " sum(" +
                dbl.sqlIsNull("FBaseCuryBal", "0") + " ) as FBaseCuryBal," +
                " sum(" +
                dbl.sqlIsNull("FPortCuryBal", "0") + ") as FPortCuryBal" +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
                //------xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B -------------//
                " a11 join (select b.FCashAccCode, b.FCashAccName" +
                " from (select max(FStartDate) as FStartDate,FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FAccType <> '04' and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " group by FCashAccCode order by FCashAccCode) a join (select FCashAccCode, FCashAccName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1" +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " ) b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate " +
                //----------------------------------end 20100711-----------------------------------------//
                ") a12 on a11.FCashAccCode = a12.FCashAccCode" +
                " where FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //--------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------------
                " group by FPortCode, a11.FCashAccCode" +
                //", FBaseCuryRate, FPortCuryRate" +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                ",FATTRCLSCODE"+//添加所属分类
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                ", FStorageDate) a1" +
                //--------------------------------------------------------------------------------------获取库存余额的汇兑损益
                " left join (select a21.FCashAccCode as FCashAccCode2," +
                " FPortCode as FPortCode2," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                "   a21.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                dbl.sqlIsNull("FSubTsfTypeCode", "'9905DE'") + " as FSubTsfTypeCode," +
                " sum(" +
                dbl.sqlIsNull("FBal", "0") + ") as FRPBal," +
                " sum(" +
                dbl.sqlIsNull("FBaseCuryBal", "0") + ") as FRPBaseCuryBal," +
                " sum(" +
                dbl.sqlIsNull("FPortCuryBal", "0") + ") as FRPPortCuryBal" +
                //" NVL(FSubTsfTypeCode,'9905DE') as FSubTsfTypeCode," +
                //" sum(NVL(FBal,0)) as FRPBal," +
                //" sum(NVL(FBaseCuryBal,0)) as FRPBaseCuryBal," +
                //" sum(NVL(FPortCuryBal,0)) as FRPPortCuryBal" +
                " from " + pub.yssGetTableName("Tb_Stock_CashPayRec") +
                //----xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B -----------//
                " a21 join (select b.FCashAccCode, b.FCashAccName" +
                " from (select max(FStartDate) as FStartDate,FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FAccType <> '04' and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " group by FCashAccCode order by FCashAccCode) a join (select FCashAccCode, FCashAccName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1" +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " ) b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate " +
                //---------------------------------end 20100711----------------------------------------//
                " ) a22 on a21.FCashAccCode = a22.FCashAccCode " +
                "  where FTsfTypeCode = '99' and " +
                " FSubTsfTypeCode like  '9905%' and " +
                " FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //--------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------------
                " group by FPortCode, a21.FCashAccCode,  FSubTsfTypeCode, " +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                "   a21.fattrclscode,"+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "FStorageDate) " +
                " a2 on a1.FPortCode = a2.FPortCode2 and a1.FCashAccCode = a2.FCashAccCode2 and a1.fattrclscode = a2.fattrclscode " +//NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 
                //------------------------------------------------------------------------------------------------------
                " union " + //以下union的是应收应付的数据
                //------------------------------------------------------------------------------------------------------获取现金应收应付的数据
                " select b1.FCashAccCode    as FCashAccCode," +
                " b1.FPortCode       as FPortCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " b1.fattrclscode, "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                //" 0                  as FBaseCuryRate," +
                //" 0                  as FPortCuryRate," +
                " FNAVDate," +
                " b1.FSubTsfTypeCode as FSubTsfTypeCode," +
                " b1.FBal            as FACCBalance," +
                " b1.FBaseCuryBal    as FBaseCuryBal," +
                " b1.FPortCuryBal    as FPortCuryBal," +
                " T.FPortCuryBal    as FSYPortCuryBal" +
                "  from (select b11.FCashAccCode as FCashAccCode," +
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " b11.fattrclscode, "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " b11.FPortCode as FPortCode," +
                " FSubTsfTypeCode," +
                " FStorageDate as FNAVDate," +
                " sum(" +
                dbl.sqlIsNull("FBal", "0") + ") as FBal," +
                " sum(" +
                dbl.sqlIsNull("FBaseCuryBal", "0") + ") as FBaseCuryBal," +
                " sum(" +
                dbl.sqlIsNull("FPortCuryBal", "0") + ") as FPortCuryBal" +
                //" sum(NVL(FBal, 0)) as FBal," +
                //" sum(NVL(FBaseCuryBal, 0)) as FBaseCuryBal," +
                //" sum(NVL(FPortCuryBal, 0)) as FPortCuryBal" +
                " from " + pub.yssGetTableName("Tb_Stock_CashPayRec") + " b11 " +
                
                //----xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B ---------//
                " join (select b.FCashAccCode, b.FCashAccName" +
                " from (select max(FStartDate) as FStartDate,FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FAccType <> '04' and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " group by FCashAccCode order by FCashAccCode) a join (select FCashAccCode, FCashAccName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1" +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " ) b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate " +
                //----------------------------------end 20100711----------------------------------------//   
                ") b12 on b11.FCashAccCode = b12.FCashAccCode " +
                " where b11.FTsfTypeCode in ('06', '07','09') and " +//增加可退替代款估值增值的调拨类型09和调拨子类型09CR panjunfang add 20091015， MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
                /**增加 ,06GZ应收款项-挂账,06XZ应收款项-销账,07GZ应付款项-挂账,07XZ应付款项-销账,shashijie 2011-08-31 STORY 1327*/
                /**增加 ,07SE应付税金,shashijie 2011-09-08 STORY 1447,1561 */
                " (b11.FSubTsfTypeCode in ('07SE', " +
                " '06GZ','06XZ','07GZ','07XZ','06DE', '06TD', '06CE', '06OT', '06DV', '06TA','06LE','07LE'," + //增加股指期货的调拨类型。sj edit 20080921
                /** 1447,1561 end*/
                /** 1327 end*/
                //2010.05.26 jiangshichao 增加调拨子类型  ‘06LE’应收证券借贷收益，'07LE'应付证券借贷费用  MS01175  国泰2010年5月13日02_A 
				//2009.08.15 蒋锦 增加调拨子类型 应收申购款 06AP_EQ， 06AP_FI, 07FE02 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                //2009.09.15 蒋锦 修改 增加了 06TR 基金审购款的调拨类型 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                //xuqiji 20091107 增加ETF基金的估值增值 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
                //修改ETF基金的估值增值为'07TA_ID_HD'
				//添加07TA_IDS_MAY，07TA_CR_2，华夏ETF赎回区分必须、允许现金替代
                " '07RE01','07RE02','07RE03',"+//添加回购费用的显示 by leeyu 20100327 添加'07RE02','07RE03' by fangjiang 2010.11.05 BUG #214 
                //增加06FU02 fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A  增加'06LDID','07BDID',edited by zhouxiang 2010.11.22 证券借贷--净值统计表现金库存取数
                //增加06FU03 fangjiang 2011.02.15 STORY #462
                //增加07LI by guyichuan 20110522 STORY #561
                //增加9906GZ 应收款项-挂账汇兑损益  ,9907GZ 应付款项-挂账汇兑损益 shashijie 2011-08-26 STORY 1327
                //增加9907SE应付税金,shashijie 2011-09-08 STORY 1447,1561 
                //add by songjie 2012.06.27 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A 添加 '06CM','02CM'
                " '9907SE','9906GZ','9907GZ','07LI','06OT', '07DE','07TD', '07LX', '07TA', '07OT','07CE','06CM','02CM'," +
                " '07FE','06FE','06IM','06PF','06FU01','06FU02','06FU03','06FU04','06FD','06LDID'," + //modify huangqirong 2012-08-21  商品期货
                " '07BDID','07FD','06AP_FI','06AP_EQ','07FE02','06TR','06TA_CB','07TA_CB','06TA_CR','07TA_Fee'," + //多币种应付赎回费、款分为多项  添加07TA_Fee  20130312  yeshenghong
                " '07TA_CR','07TA_IDS','07TA_IDB','07SB'," +//添加了07SB 短期借款 #2541::华泰证券新股申购业务，申购时可以向券商借款  add by jiangshichao 2011.02.16
                //edit by songjie 2011.05.20 BUG 1959 QDV4博时2011年05月19日03_B 
                //添加 07FE01 应付银行间债券交易手续费 07FE02 应付银行间债券银行手续费 07FE03 应付银行间债券结算手续费
                //edit by songjie 2011.08.16 BUG 2406 QDV4赢时胜（深圳）2011年8月8日01_B 添加 06AP_TR 应收基金申购款
                " '07TA_CBCB','07TA_CBCS','09CR','09CR_B','09CR_S','09ETFValue','07TA_JYSHR_SG','07TA_JYSHR_SH'," +
                //edit by songjie 2011.10.24 BUG 2997 QDV4赢时胜(测试)2011年10月21日01_B 添加 07AP_EQ 应付股票申购款                              //edit by zhouwei 20120228 07LXS_FI	债券利息税,07LXS_DE	存款利息税,07TF固定交易费用
                " '07TA_CR_2','07TA_IDS_MAY','06CF_TR','07FE01','07FE02','07FE03','06AP_TR', '07AP_EQ', '07AP_FI'," +
                //add by songjie 2012.10.26 STORY #3184 需求北京-[建信基金]QDV4.0[中]20121023001 添加 06TAZR,07TAZC 应收转入款、应付转出款
                " '06TAZR','07TAZC', " +
                " '06IDB','06IDS','06CB','07CR','07CB', '06CBCB', '06CR') or  b11.FSubTsfTypeCode like '07LXS%' or  b11.FSubTsfTypeCode like '07CGT%' or b11.FSubTsfTypeCode like '07TF%') and " + //edit by guolongchao 20111103 BUG 3001  添加 07AP_FI 应付债券申购款  
                //添加了06CF_TR应收赎回款//添加了07FE，06FE的调拨类型 sj edit 20080324//添加了06PF sj edit 20080523//添加了06FD 07FD QDV4海富通2009年4月8日01_B MS00355 by leeyu 20090408
                //添加了'06IDB','06IDS','06CB','07CR','07CB','06CBCB','06CR' add by fangjiang story 2565 联接基金
                " b11.FPortCode = " + dbl.sqlString(this.portCode) +
                " and b11.FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and b11.FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //--------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------------
                " group by b11.FCashAccCode, b11.FSubTsfTypeCode,b11.FPortCode,b11.fsubtsftypecode," +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " b11.fattrclscode, "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "b11.FStorageDate) " +
                " b1 " +
                //------------------------------------------------------------------------------------------------------获取应收应付的汇兑损益
                " left join ( select FCashAccCode2,FPortCode2," +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " fattrclscode, "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                "FBal,FBaseCuryBal,FPortCuryBal," +
                //-----------------当子类型为股指期货，即位数为8位就取6位。sj edit 20080921 ----------------------------------------//
                " (case when FSubTsfTypeCode2 in ('9906FU01', '9906FU02', '9906FU03', '9906FU04') then " + //modify by fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A  modify by fangjiang 2011.02.15 STORY #462 //modify huangqirong 2012-08-21  商品期货
                dbl.sqlRight("FSubTsfTypeCode2", 6) + //在没有打补丁的Oracle中不能在where中放置，只能在select中先期执行 sj 20080228
                " when FSubTsfTypeCode2 in  ('9907TA_Fee') then  decode(8,   0,  '', substr(FSubTsfTypeCode2, - (8)))" +//modified by yeshenghong  多币种赎回费分项统计 20130313
                " else " +
                dbl.sqlRight("FSubTsfTypeCode2", 4) +
                " end)" +
                //--------------------------------------------------------------------------------------------------------------
                " as FSubTsfTypeCode2 from " +
                " (select b21.FCashAccCode as FCashAccCode2," +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " b21.fattrclscode, "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " b21.FPortCode as FPortCode2,  b21.FSubTsfTypeCode as FSubTsfTypeCode2 , " +
                " sum(" +
                dbl.sqlIsNull("b21.FBal", "0") + ") as FBal," +
                " sum(" +
                dbl.sqlIsNull("b21.FBaseCuryBal", "0") + ") as FBaseCuryBal," +
                " sum(" +
                dbl.sqlIsNull("b21.FPortCuryBal", "0") + ") as FPortCuryBal" +
                //"sum(NVL(b21.FBal,0)) as FBal," +
                //" sum(NVL(b21.FBaseCuryBal,0)) as FBaseCuryBal," +
                //" sum(NVL(b21.FPortCuryBal,0)) as FPortCuryBal" +
                " from " + pub.yssGetTableName("Tb_Stock_CashPayRec") + " b21 " +
                //--xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B -------------//
                " join (select b.FCashAccCode, b.FCashAccName" +
                " from (select max(FStartDate) as FStartDate,FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FAccType <> '04' and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " group by FCashAccCode order by FCashAccCode) a join (select FCashAccCode, FCashAccName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1" +
                " and FAccType <> '04'" + //为了屏蔽掉所有的虚拟账户的金额，使其不参与计算净值。sj edit 20081118
                " and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " ) b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate " +
                //-------------------------------------------end 20100711----------------------------------------//    
                ") b22 on b21.FCashAccCode = b22.FCashAccCode " +
                "  where b21.FTsfTypeCode = '99' and  ( b21.FSubTsfTypeCode in('9906DE', '9907DE', '9906TD', '9907TD', '9906DV', '9907DV', '9906CE','9906LE','9907LE','9907SB'," +//添加了9907SB 短期借款汇兑损益   #2541::华泰证券新股申购业务，申购时可以向券商借款  add by jiangshichao 2011.02.16
                //2010.05.26 jiangshichao 增加调拨子类型 '9906LE'证券借贷收益汇兑损益，‘9907LE’证券借贷费用汇兑损益
                //2009.08.15 蒋锦 增加调拨子类型 应收申购款 9906AP_EQ， 9906AP_FI, 9907FE02 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                //2009.09.15 蒋锦 修改 增加了 9906TR 基金审购款汇兑损益的调拨类型 MS00022 QDV4.1赢时胜（上海）2009年4月20日22_A
                " '9907SE'," + //add by huangqirong 2012-08-27 bug #5324 漏掉了这种调拨子类型
                " '9907RE01','9907RE02','9907RE03',"+//添加回购费用的显示 by leeyu 20100327 添加'9907RE02','9907RE03' by fangjiang 2010.11.05 BUG #214 
                //增加9906FU02 fangjiang 2010.08.30  MS01439 QDV4博时2010年7月14日02_A 
                //增加9906FU03 fangjiang 2011.02.15 STORY #462     //'9907TA_Fee' add by yeshenghong  多币种资产估值 赎回费分开统计20130313                                                                                                                                               //edit by zhouwei 20120228 增加9907LXS_开头的利息税                                     
                " '9907CE', '9907OT','9907TA','9907TA_Fee','9906OT','9906TA','9907FE','9906FE','9906IM','9906PF','9906FU01','9906FU02','9906FU03','9906FU04','9906AP_FI','9906AP_EQ', '9907FE02', '9906TR'" + //modify huangqirong 2012-08-21 商品期货
                " ,'9907FD') or b21.FSubTsfTypeCode like '9907LXS%' or  b21.FSubTsfTypeCode like '9907CGT%' or  b21.FSubTsfTypeCode like '9907TF%') and " + //添加了9906PF sj edit 20080523
                //增加了9906FE,9907FE sj edit 20080325 //增加股指期货汇兑损益的调拨类型。sj edit 20080921
                //增加了9907FD story 2565 fj 2012.05.14

                //'9906DE','9907DE','9906TD','9907TD','9906DV','9907DV','9906CE','9907CE','9907OT'
                " FPortCode = " + dbl.sqlString(this.portCode) +
                " and FStorageDate = " + dbl.sqlDate(this.dDate) +
                " and FYearMonth = " +
                dbl.sqlString(YssFun.formatDate(dDate, "yyyyMM")) +
                " and FCheckState = 1 " + //添加where条件 BugNo:0000381 edit by jc
                //-------------------------------------------------------------------------------------
                (!this.invMgrCode.equalsIgnoreCase("total") ?
                 " and FAnalysisCode1 = " + dbl.sqlString(this.invMgrCode) : "") +
                //--------------------------------------------------------------------------------------
                "  group by b21.FPortCode,b21.FCashAccCode,FSubTsfTypeCode, " +
               //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " b21.fattrclscode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " ) FX ) T on b1.FPortCode = T.FPortCode2 and b1.FCashAccCode = T.FCashAccCode2" +
                " and b1.FSubTsfTypeCode = T.FSubTsfTypeCode2 " +
              //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                " and b1.fattrclscode = T.fattrclscode "+
                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                " ) cash " +
                //--------------------------------------------------------------------------------------------获取帐户信息
                
                //----xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B --------------//
                " left join (select b.FCashAccCode, b.FAccType, b.FSubAccType, b.FCuryCode " +
                " from (select max(FStartDate) as FStartDate,FCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " group by FCashAccCode order by FCashAccCode) a join (select FCashAccCode,FAccType,FSubAccType,FCuryCode,FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1" +
                " and FStartDate <= " + dbl.sqlDate(this.dDate) +
                " ) b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate " +
                " ) para on cash.FCashAccCode = para.FCashAccCode ";
//                " left join (select FCashAccCode,FAccType,FSubAccType,FCuryCode from " +
//                pub.yssGetTableName("Tb_Para_Cashaccount") +
//                " where FCheckState = 1 ) para on cash.FCashAccCode = para.FCashAccCode "; //这里查询已审核的数据 by leeyu 2008-11-20
            // " where ( FPortCuryBal <> 0 and FSyPortCuryBal <> 0) "; // 组合货币金额和汇兑损益不等于0 sj edit 20080421
            //" where  cash.FACCBalance <> 0 or cash.FBaseCuryBal <> 0 or" +
            //" cash.FPortCuryBal <> 0 or  FSYPortCuryBal <> 0";
            //} //在此处拼写所需数据的sql语句
            //------------------------------------------------------------------------
               //------------------------end 20100711-----------------------------//
       //  synchronized(YssGlobal.objSecRecLock){//add by lidaolong 20110422 BUG #4606 :: 系统里在建临时的表和视图时，系统就会报错
            tempViewName = "V_Temp_Cash_" + pub.getUserCode();
            if (dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
            //NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22  添加所属分类字段 FAttrclscode
            strFields = "FAccType,FSubAccType,FCuryCode,FCashAccCode,FPortCode,FAttrclscode,FNavDate,FSubTsfTypeCode,FAccBalance,FBaseCuryBal,FportCuryBal,FSyportCuryBal";
            if (dbl.getDBType() == YssCons.DB_ORA) {
                String tempStr = "create view " + tempViewName + "(" + strFields + ") as (" +
                    strSql +
                    ")";
                dbl.executeSql(tempStr);
            } else if (dbl.getDBType() == YssCons.DB_DB2) {
                String temp = "create view " + tempViewName + "( " + strFields + " ) as " + strSql;
                dbl.executeSql(temp);
                //dbl.executeSql("insert into " + tempViewName + "(" + strSql +
                //")");
            } else {
                throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
            }
        //  }  
            return tempViewName;

        } catch (Exception e) {
            throw new YssException("生成现金视图出错！");
        }
    }

    public void buildLeftSql(ArrayList leftArr, ArrayList fieldsArr) {
        String leftSql = "";
        String fields = "";
        //----------------------------------------
        leftSql = " left join (select FAccTypeCode,FAccTypeName from Tb_Base_AccountType where FCheckState = 1)" +
            " para on dat.FAccType = para.FAccTypeCode";
        fields = "dat.FAccType,FAccTypeName"; // modified by yeshenghong 20111111 BUG3104
        leftArr.add(0, leftSql);
        fieldsArr.add(0, fields);
        //-----------------------------------------
        leftSql = " left join (select FSubAccTypeCode,FSubAccTypeName from Tb_Base_SubAccountType where FCheckState = 1)" +
            " para on dat.FSubAccType = para.FSubAccTypeCode";
        fields = "FSubAccType,FSubAccTypeName";
        leftArr.add(1, leftSql);
        fieldsArr.add(1, fields);
        //---------------------------------------
        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
        leftSql =
            " left join (select FAttrClsCode as PFAttrClsCode,FAttrClsName from  " +pub.yssGetTableName("Tb_Para_AttributeClass") +
            " where FCheckState =1 ) para on dat.FAttrClsCode = para.PFAttrClsCode";
        //--------------------------------------------end 20100711--------------------------------------------------//
        fields = "FATTRCLSCODE,FATTRCLSName";
        leftArr.add(2, leftSql);
        fieldsArr.add(2, fields);
       //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
        //---------------------------------------
        leftSql = " left join (select FCuryCode as PFCuryCode,FCuryName from " +
            pub.yssGetTableName("tb_para_Currency") +
            " where FCheckState =1) para on dat.FCuryCode = para.PFCuryCode";
        fields = "FCuryCode,FCuryName";
        leftArr.add(3, leftSql);
        fieldsArr.add(3, fields);
        //----------------------------------------
        //-----xuqiji 20100711 MS01426 现金账户设置中设置启用日期和银行账号不一致 QDV4赢时胜(测试)2010年07月8日02_B --------------//
        leftSql =
            " left join (select b.PFCashAccCode, b.FCashAccName, b.FAccAttr from " +
            " (select max(FStartDate) as FStartDate, FCashAccCode from " +
            pub.yssGetTableName("Tb_para_CashAccount") +
            " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(this.dDate) +
            " group by FCashAccCode order by FCashAccCode) a " +
            " join (select FCashAccCode as PFCashAccCode,FCashAccName,FAccAttr,FStartDate from " +
            pub.yssGetTableName("Tb_para_CashAccount") +
            " where FCheckState = 1 and FStartDate <= " + dbl.sqlDate(this.dDate) +
            " ) b on a.FCashAccCode = b.PFCashAccCode and a.FStartDate = b.FStartDate " +
            " ) para on dat.FCashAccCode = para.PFCashAccCode";
        //--------------------------------------------end 20100711--------------------------------------------------//
        fields = "FCashAccCode,FCashAccName,FCuryCode,FAccAttr";
        leftArr.add(4, leftSql);
        fieldsArr.add(4, fields);
        //---------------------------------------
    }

}
