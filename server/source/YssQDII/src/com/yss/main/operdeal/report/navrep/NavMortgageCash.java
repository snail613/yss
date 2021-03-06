package com.yss.main.operdeal.report.navrep;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.util.*;

public class NavMortgageCash
    extends BaseNavRep {
	public String sBorker;
	public String sMortgage;
    public NavMortgageCash() {
    }

    public void initReport(java.util.Date dDate, String sPortCode,
                              String sBroker,String sMortgage) throws YssException {
        this.valDefine =
            "FAccType;FSubAccType;FCuryCode;FCashAccCode;FSubTsfTypeCode";
        this.dDate = dDate;
        this.portCode = sPortCode;
        this.sBorker = sBroker;
        this.sMortgage=sMortgage;
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
                rs = dbl.openResultSet(strDetailSql);
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
                    //-----------如果记帐本位币的值为0，则不需要将其录入 sj add 20080506 ----
                    if (navRep.getPortMarketValue() == 0 && navRep.getPortBookCost() == 0 && navRep.getPortexchangeValue() == 0) {
                        continue;
                    }
                    //------------------------------------------------------------------
                    navRep.setGradeType1(rs.getString("FAccType"));
                    navRep.setGradeType2(rs.getString("FSubAccType"));
                    navRep.setGradeType3(rs.getString("FCuryCode"));
                    navRep.setGradeType4(rs.getString("FCashAccCode"));
                    navRep.setGradeType5(rs.getString("FSubTsfTypeCode"));
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
                sGroupSql = " group by FPortCode,Fexternalcode," + groupStr;
                strSql = " select  dat.*," +
                    (Grade == 3 || Grade == 4 ?
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
                    ") as FSYPortCuryBal,Fexternalcode," +
                    ( (String) fieldsArr.get(Grade - 1)).split(",")[0] +
                    (Grade == fields.length - 1 ?
                     "," + ( (String) fieldsArr.get(Grade - 1)).split(",")[2] :
                     "") +
                    " from " + tempViewName + //modified by yeshenghong 20111111 BUG3104
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
                    (Grade == 3 || Grade == 4 ?
                     " left join (select FCuryCode as RFCuryCode,FBaseRate,FPortRate from " +
                     pub.yssGetTableName("Tb_Data_ValRate") +
                     " where FValDate = " + dbl.sqlDate(this.dDate) +
                     " and FPortCode = " + dbl.sqlString(this.portCode) +
                     " ) Rate on Rate.RFCuryCode = dat.FCuryCode" : "");
                //----------------------------------------------------------------------------------------
                rs = dbl.openResultSet(strSql);
                while (rs.next()) {
                    navRep = new NavRepBean();
                    navRep.setNavDate(this.dDate); //净值日期
                    navRep.setPortCode(this.portCode);
                    navRep.setOrderKeyCode(rs.getString("FOrderCode"));
                    if (Grade == 3 || Grade == 4) {
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
                            navRep.setKeyCode(rs.getString("FCuryCode") == null ?
                                              " " : rs.getString("FCuryCode"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FCuryName") == null ?
                                               " " : rs.getString("FCuryName")));
                            break;
                        case 4:
                            navRep.setKeyCode(rs.getString("FCashAccCode") == null ?
                                              " " : rs.getString("FCashAccCode"));
                            navRep.setKeyName(setBlo(Grade) +
                                              (rs.getString("FCashAccName") == null ?
                                               " " : rs.getString("FCashAccName")));
                            break;
                    }
                    if (Grade == 4) {
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
                        	navRep.setSedolCode(rs.getString("Fexternalcode"));
                            navRep.setGradeType1(gradeTypes[0]);
                            navRep.setGradeType2(gradeTypes[1]);
                            navRep.setGradeType3(gradeTypes[2]);
                            navRep.setGradeType4(gradeTypes[3]);

                            break;
                            //case 5:
                            //navRep.setGradeType1(gradeTypes[0]);
                            //navRep.setGradeType2(gradeTypes[1]);
                            //navRep.setGradeType3(gradeTypes[2]);
                            //navRep.setGradeType4(gradeTypes[3]);
                            //navRep.setGradeType5(gradeTypes[4]);
                            //break;
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
            String sqlMortgage=" and a.fcollateralcode="+dbl.sqlString(this.sMortgage);
            String sqlBroker="and a.fbrokercode ="+dbl.sqlString(this.sBorker);
        	strSql ="select para.FAccType, para.FSubAccType, para.FCuryCode, cash.*  from (select c.fcashacccode,"
            		+"a.fportcode,d.fbrokercode as FExternalCode,"+dbl.sqlDate(this.dDate)+" as FNAVDate, '9905DE' as  FSubTsfTypeCode,sum(NVL(c.faccbalance*a.finout, 0)) as FAccBalance," 
            		+"sum(NVL(c.fbasecurybal*a.finout, 0)) as FBaseCuryBal,sum(NVL(c.fportcurybal*a.finout, 0)) as FPortCuryBal, 0 as FSYPortCuryBal  from "+pub.yssGetTableName("tb_Data_CollateralAdd")
            		+" a  left join (select b.fcollateralcode,b.fcashacccode,to_date(b.ftransferdate, 'yyyy-MM-dd') as ftransferdate,"
            		+" b2.faccbalance,b2.fbasecurybal, b2.fportcurybal  from "
            		+pub.yssGetTableName("tb_data_collateralacc")
            		+" b left join (select b1.fcashacccode,b1.faccbalance,b1.fbasecurybal,b1.fportcurybal from "+pub.yssGetTableName("tb_stock_cash")
            		+" b1 where b1.fstoragedate ="+dbl.sqlDate(this.dDate)+" and b1.fportcode = "+dbl.sqlString(this.portCode)+") b2 on b.fcashacccode =b2.fcashacccode"
            		+" ) c on a.fcollateralcode =c.fcollateralcode  and a.ftransferdate = c.ftransferdate"
            		//取券商 
            		+" join (select a.fcollateralcode,a.fbrokercode from "+pub.yssGetTableName("Tb_Para_Collateral")
            		+" a where a.fcheckstate = 1  "+(this.sBorker.equalsIgnoreCase("total")? "":sqlBroker)+") " 
            		+" d on a.fcollateralcode =d.fcollateralcode"
            		+" where a.fcheckstate = 1  and a.ftransfertype = '现金' " 
            	    +(this.sMortgage.equalsIgnoreCase("total")? " ":sqlMortgage)
            		+"  and a.ftransferdate <= " +dbl.sqlDate(this.dDate)
            		+" group by c.fcashacccode, a.fportcode,d.fbrokercode" 
            		//unoin 组合类型的数据
            		+" union all select m.fcashacccode,m.fportcode, t.fbrokercode,m.fstoragedate,'9905DE' as FSubTsfTypeCode,sum(nvl(m.faccbalance*t.finout, 0)) as FAccBalance,"
            		+"sum(NVL(m.FBaseCuryBal*t.finout, 0)) as FBaseCuryBal,sum(NVL(m.FPortCuryBal*t.finout, 0)) as FPortCuryBal, 0 as FSYPortCuryBal from " 
            		+pub.yssGetTableName("tb_stock_cash")+" m join (select n.fbrokercode, a.fportcode,sum(a.finout) as finout from "
            		+pub.yssGetTableName("tb_Data_CollateralAdd")+" a join (select a.fcollateralcode, a.fbrokercode from "
            		+pub.yssGetTableName("Tb_Para_Collateral")+" a  where a.fcheckstate = 1 "+(this.sBorker.equalsIgnoreCase("total")?"":sqlBroker)
            		+") n on a.fcollateralcode = n.fcollateralcode where a.fcheckstate = 1  and a.ftransfertype = '组合'"
            		+(this.sMortgage.equalsIgnoreCase("total")? "":sqlMortgage)+" and a.ftransferdate <="+dbl.sqlDate(this.dDate)+"group by  n.fbrokercode, a.fportcode) t on t.fportcode =m.fportcode  where m.fstoragedate= "+dbl.sqlDate(this.dDate) 
            		+" group by m.fcashacccode, m.fportcode, m.fstoragedate,t.fbrokercode) cash"
            		
            		+" left join (select b.FCashAccCode, b.FAccType, b.FSubAccType, b.FCuryCode from (select max(FStartDate) as FStartDate, FCashAccCode"
            		+" from "+pub.yssGetTableName("Tb_Para_CashAccount")
            		+" where FCheckState = 1 and FStartDate <= "+dbl.sqlDate(this.dDate)+" group by FCashAccCode"
            		+" order by FCashAccCode) a join (select FCashAccCode,FAccType, FSubAccType,FCuryCode, FStartDate from "
            		+pub.yssGetTableName("Tb_Para_CashAccount")+" where FCheckState = 1 and FStartDate <= "+dbl.sqlDate(this.dDate)+")"
            		+" b on a.FCashAccCode = b.FCashAccCode and a.FStartDate = b.FStartDate) para on cash.FCashAccCode = para.FCashAccCode  where cash.FAccBalance>0";
            tempViewName = "V_Temp_Cash_" + pub.getUserCode();
            if (dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
            strFields = "FAccType,FSubAccType,FCuryCode,FCashAccCode,FPortCode,FEXternalCode,FNavDate,FSubTsfTypeCode,FAccBalance,FBaseCuryBal,FportCuryBal,FSyportCuryBal";
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
        leftSql = " left join (select FCuryCode as PFCuryCode,FCuryName from " +
            pub.yssGetTableName("tb_para_Currency") +
            " where FCheckState =1) para on dat.FCuryCode = para.PFCuryCode";
        fields = "FCuryCode,FCuryName";
        leftArr.add(2, leftSql);
        fieldsArr.add(2, fields);
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
        leftArr.add(3, leftSql);
        fieldsArr.add(3, fields);
        //---------------------------------------
    }

}
