package com.yss.main.operdeal.compliance;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.compliance.*;
import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;

public class BaseCompDeal
    extends BaseBean implements IComplianceDeal {
    private String portCode = ""; //组合代码
    private String invMgrCode = ""; //投资经理
    private String brokerCode = ""; //券商代码
    private String securityCode = ""; //证券代码
    private String catCode = ""; //品种代码
    private String compPoint = ""; //监控点
    private String cashAccCode = ""; //现金帐户

    public java.util.Date dDate;

    public BaseCompDeal() {
    }

    /**
     * init
     *
     * @param bean BaseBean
     */
    public void init(BaseBean bean) {
        YssCompDeal cd = (YssCompDeal) bean;
        this.portCode = cd.getPortCode();
        this.invMgrCode = cd.getInvMgrCode();
        this.brokerCode = cd.getBrokerCode();
        this.securityCode = cd.getSecurityCode();

        this.compPoint = cd.getCompPoint();
        this.cashAccCode = cd.getCashAccCode();
        this.dDate = cd.getDDate();
    }

    /**
     * doCompIndex
     *
     * @return String
     */
    public HashMap doCompliance() throws YssException {
        ArrayList alTemps = null;
        HashMap hmResult = new HashMap();
        IndexTemplateBean template = null;
        try {
            if (this.securityCode.length() != 0) {
                SecurityBean sec = new SecurityBean();
                sec.setYssPub(pub);
                sec.setSecurityCode(this.securityCode);
                sec.getSetting();
                if (sec.getCusCatCode() == null ||
                    sec.getCusCatCode().trim().equalsIgnoreCase("null") ||
                    sec.getCusCatCode().trim().length() == 0) {
                    this.catCode = sec.getCategoryCode();
                } else {
                    this.catCode = YssFun.right(sec.getCusCatCode(), 2);
                }
            }

            alTemps = getCompTemplates();
            for (int i = 0; i < alTemps.size(); i++) {
                template = (IndexTemplateBean) alTemps.get(i);
                if (template.getIndexTemplateCode().equalsIgnoreCase("103")) {
                    int e = 1;
                }
                doCompTemplate(template, hmResult);
            }
            return hmResult;
        } catch (Exception e) {
            throw new YssException("获取【" + YssFun.formatDate(dDate) + "】投资组合【" +
                                   portCode + "】的监控结果出错： \n" +
                                   e.getMessage());
        }
    }

    //获取监控模板，根据组合和监控点
    protected ArrayList getCompTemplates() throws YssException {
        ResultSet rs = null;
        String strSql = "";
        ArrayList alTemps = new ArrayList();
        IndexTemplateBean template = null;
        try {
            strSql = "select * from " +
                pub.yssGetTableName("Tb_Para_Portfolio_Relaship") +
                " where FCheckState = 1 and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FRelaType = 'Template' order by FSubCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                template = new IndexTemplateBean();
                template.setYssPub(pub);
                template.setIndexTemplateCode(rs.getString("FSubCode") + "");
                template.setStartDate(dDate);
                template.getSetting();
                if (template.getIndexTemplateName().length() > 0) {
                    alTemps.add(template);
                }
            }
            return alTemps;
        } catch (Exception e) {
            throw new YssException("获取监控模版列表出错： \n" +
                                   e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    //获取某个监控模板的监控指标，根据监控点
    protected ArrayList getCompIndexs(IndexTemplateBean template) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        ArrayList alCompIndexs = new ArrayList();
        CompIndexBean compIndex = null;
        try {
            strSql =
                "select a.*, ' ' as EndCompGradeName, ' ' as FCreatorName," +
                " ' ' as FCheckUserName from " +
                pub.yssGetTableName("Tb_Comp_Index") + " a " +
                " where a.FCheckState = 1 and a.FIndexTempCode = " +
                dbl.sqlString(template.getIndexTemplateCode()) +
                (compPoint.length() > 0 ? " and " + compPoint + " = 1" : "") +
                " order by a.FIndexCode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                compIndex = new CompIndexBean();
                compIndex.setYssPub(pub);
                compIndex.setCompIndexAttr(rs);
                alCompIndexs.add(compIndex);
            }
            return alCompIndexs;
        } catch (Exception e) {
            throw new YssException("获取监控模版【" + template.getIndexTemplateCode() +
                                   "】的监控指标列表出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取某个监控指标的监控条件，根据监控类型、属性类型
    protected ArrayList getCompConds(CompIndexBean compIndex, int iAttrType,
                                     int iCompType) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        ArrayList alCompConds = new ArrayList();
        CompIndexCondBean compCond = null;
        try {
            strSql =
                "select a.*, ' ' as FAttrName, ' ' as FDenominaAttrName," +
                " ' ' as FCreatorName, ' ' as FCheckUserName from " +
                pub.yssGetTableName("Tb_Comp_IndexCondition") + " a " +
                " where a.FCheckState = 1 and a.FIndexTempCode = " +
                dbl.sqlString(compIndex.getIndexTempCode()) +
                " and a.FIndexCode = " + dbl.sqlString(compIndex.getIndexCode()) +
                (iAttrType == 99 ? "" :
                 " and a.FAttrType = " + String.valueOf(iAttrType)) +
                (iCompType == 99 ? "" : " and a.FCompType = "
                 + String.valueOf(iCompType)) +
                " order by a.FConNum";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                compCond = new CompIndexCondBean();
                compCond.setYssPub(pub);
                compCond.setCompIndexConditionAttr(rs);
                compCond.getAttrParam();
                compCond.getAttrDenParam();
                alCompConds.add(compCond);
            }
            return alCompConds;
        } catch (Exception e) {
            throw new YssException("获取监控指标【" + compIndex.getIndexCode() +
                                   "】的监控条件列表出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取某个监控指标的监控条件，根据属性类型
    protected ArrayList getCompConds(CompIndexBean compIndex, int iAttrType) throws
        YssException {
        return getCompConds(compIndex, iAttrType, 99);
    }

    //获取某个监控指标的监控条件
    protected ArrayList getCompConds(CompIndexBean compIndex) throws
        YssException {
        return getCompConds(compIndex, 99, 99);
    }

    protected String buildUnPassHint(ResultSet rs, String sHint) throws
        YssException {
        String[] sParamAry = null;
        String sFieldValues = "";
        String sTmp = "";
        try {
            sTmp = YssFun.getStrParams(sHint + "");
            if (sTmp.indexOf(",") > 0) {
                sParamAry = sTmp.split(",");
                for (int i = 0; i < sParamAry.length; i++) {
                    rs.beforeFirst();
                    sFieldValues = "";
                    while (rs.next()) {
                        sFieldValues += rs.getString(sParamAry[i]) + ",";
                    }
                    if (sFieldValues.length() > 1) {
                        sFieldValues = sFieldValues.substring(0,
                            sFieldValues.length() - 1);
                    }
                    sHint = sHint.replaceAll("<" + sParamAry[i] + ">",
                                             sFieldValues);
                }
            }
            return sHint;
        } catch (Exception e) {
            throw new YssException(e);
        }

//      sHint.indexOf()
    }

    //获取某个监控模版下的违规监控指标，根据组合、日期、指标
    protected void doCompTemplate(IndexTemplateBean template, HashMap hmResult) throws
        YssException {
        CompIndexBean compIndex = null;
        String strSql = "";
        ResultSet rs = null;
        String sKey = "";
        ArrayList alIndexs = null;
        HashMap hm = null;
        try {
            alIndexs = getCompIndexs(template);
            for (int i = 0; i < alIndexs.size(); i++) {
                compIndex = (CompIndexBean) alIndexs.get(i);
                if (compIndex.getCompDealWay().equalsIgnoreCase("common")) {
                    strSql = getCompIndexSql(template, compIndex);
                    rs = dbl.openResultSet_antReadonly(strSql);
                    if (rs.next()) {
                        sKey = YssFun.formatDate(dDate, "yyyyMMdd") + "\f" +
                            this.portCode +
                            "\f" + template.getIndexTemplateCode() + "\f" +
                            compIndex.getIndexCode();
                        compIndex.setUnPassHint(buildUnPassHint(rs,
                            compIndex.getUnPassHint()));
                        hmResult.put(sKey, compIndex);
                    }
                } else {
                    IComplianceDeal compDeal = (IComplianceDeal) pub.getOperDealCtx().
                        getBean(compIndex.getCompDealWay());
                    YssCompDeal initParam = new YssCompDeal();
                    initParam.setPortCode(portCode);
                    initParam.setDDate(dDate);
                    initParam.setCompIndex(compIndex);
                    initParam.setCompPoint(compPoint);
                    initParam.setAccTypes(template.getAccTypes());
                    initParam.setSecTypes(template.getSecTypes());
                    compDeal.setYssPub(pub);
                    compDeal.init(initParam);
                    hm = compDeal.doCompliance();
                    if (hm != null) {
                        hmResult.putAll(hm);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取监控模版【" + template.getIndexTemplateCode() +
                                   "】的监控结果出错： \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //获取某个监控模版下的违规监控指标的SQL语句，根据指标
    protected String getCompIndexSql(IndexTemplateBean template,
                                     CompIndexBean compIndex) throws
        YssException {
        String sHaveSql = "";
        String sTdSql = "";
        String sCatSql = "";
        String sSubCatSql = "";
        String strSql = "";
        boolean bHaveFlag = false; //判断是否有持有类SQL语句
        boolean bTdFlag = false; //判断是否有交易类SQL语句
        boolean bCatFlag = false; //判断是否有品种类SQL语句
        boolean bBreakFlag = false;

        sHaveSql = getHaveAttrSql(template, compIndex);
        if (sHaveSql.length() > 0) {
            bBreakFlag = YssFun.right(sHaveSql, 1).equalsIgnoreCase("#");
            sHaveSql = sHaveSql.replaceAll("#", "");
            bHaveFlag = YssFun.right(sHaveSql, 1).equalsIgnoreCase("@");
            sHaveSql = sHaveSql.replaceAll("@", "");
        }
        if (!bBreakFlag) {
            sTdSql = getTradeAttrSql(template, compIndex, bHaveFlag);
            if (sTdSql.length() > 0) {
                bBreakFlag = YssFun.right(sTdSql, 1).equalsIgnoreCase("#");
                sTdSql = sTdSql.replaceAll("#", "");
                bTdFlag = YssFun.right(sTdSql, 1).equalsIgnoreCase("@");
                sTdSql = sTdSql.replaceAll("@", "");
            }

            if (!bBreakFlag) {
                sCatSql = getCategoryAttrSql(template, compIndex, bHaveFlag,
                                             bTdFlag);
                if (sCatSql.length() > 0) {
                    bBreakFlag = YssFun.right(sCatSql, 1).equalsIgnoreCase("#");
                    sCatSql = sCatSql.replaceAll("#", "");
                    bCatFlag = YssFun.right(sCatSql, 1).equalsIgnoreCase("@");
                    sCatSql = sCatSql.replaceAll("@", "");
                }

                if (!bBreakFlag) {
                    sSubCatSql = getSubCatAttrSql(template, compIndex, bHaveFlag,
                                                  bTdFlag, bCatFlag);
                }
            }
        }

        if (compIndex.getRangeType() == 0) {
            //监控单个品种的sql
            strSql = "select * from ";
            strSql += sHaveSql + sTdSql + sCatSql + sSubCatSql;
        } else if (compIndex.getRangeType() == 1) {
            //如果是监控品种集合，那么在单个品种的sql基础上作sum的操作
            strSql = getGrpSql(template, compIndex,
                               sHaveSql + sTdSql + sCatSql + sSubCatSql);
        }
        return strSql;
    }

    protected String getGrpSql(IndexTemplateBean template,
                               CompIndexBean compIndex, String sPerSql) throws
        YssException {
        String strSql = "";
        String sWhereSql = " where ";
        String sRatioValueSql = "";
        ArrayList alConds = null;
        CompIndexCondBean cond = null;
        YssCompAttrParam attrParam = null;
        YssCompAttrParam attrDenParam = null;
        IOperValue operValue = null;
        String sGrpBySql = "";
        YssCompValueParam initParam = null;
        String[] sValueAry = null;
        String sValue = "";
        double dblOperValue = 0;
        boolean blTemp = true;

        try {
            strSql = " select main.* @4 from (" +
                " select @1 sum(FHaveAmount) as FHaveAmount, sum(FHaveCost) as FHaveCost," +
                " sum(FHaveMCost) as FHaveMCost, sum(FHaveVCost) as FHaveVCost," +
                " sum(FHaveBaseCost) as FHaveBaseCost, sum(FHaveMBaseCost) as FHaveMBaseCost," +
                " sum(FHaveVBaseCost) as FHaveVBaseCost, sum(FHavePortCost) as FHavePortCost," +
                " sum(FHaveMPortCost) as FHaveMPortCost, sum(FHaveVPortCost) as FHaveVPortCost," +
                " sum(FMarketBaseValue) as FMarketBaseValue,sum(FCurBaseMoney) as FCurBaseMoney from ";
            strSql += sPerSql + " @3) main @2";
            alConds = this.getCompConds(compIndex);
            for (int i = 0; i < alConds.size(); i++) {
                //生成where语句
                blTemp = true;
                cond = (CompIndexCondBean) alConds.get(i);
                attrParam = cond.getAttrParam();
                if (!cond.getSign().equalsIgnoreCase("with same")) {
                    if (attrParam.getRangeType().equalsIgnoreCase("group")) {
                        if (cond.getCompType() == 2 || cond.getCompType() == 3) { //比例监控，比例范围监控
                            attrDenParam = cond.getAttrDenParam();
                            operValue = (IOperValue) pub.getOperDealCtx().
                                getBean(attrDenParam.getBeanId());
                            operValue.setYssPub(pub);
                            initParam = new YssCompValueParam();
                            initParam.setDDate(dDate);
                            initParam.setPortCode(portCode);
                            initParam.setSecTypes(template.getSecTypes());
                            initParam.setCashTypes(template.getAccTypes());
                            initParam.setCuryCode(pub.getPortBaseCury(portCode));// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            operValue.init(initParam);
                            dblOperValue = operValue.getOperDoubleValue();
                            if (dblOperValue != 0) {
                                sWhereSql += " " + attrParam.getField() + "/" +
                                    dblOperValue + " " +
                                    cond.getSign() + " ";
                                sRatioValueSql = ",round(" + attrParam.getField() + "/" +
                                    dblOperValue +
                                    "*100,2) as FRatioValue"; //比例值按百分比计算，并舍入第2位
                            } else {
                                sWhereSql += " 1 = 2 ";
                                blTemp = false;
                            }
                        } else {
                            sWhereSql += " " + attrParam.getField() + " " +
                                cond.getSign() + " ";
                        }

                        if (blTemp) {
                            if (cond.getSign().indexOf("in") >= 0) {
                                sValueAry = cond.getValue().split(",");
                                sValue = "";
                                for (int j = 0; j < sValueAry.length; j++) {
                                    sValue += "'" + sValueAry[j] + "'";
                                    if (j < sValueAry.length - 1) {
                                        sValue += ",";
                                    }
                                }
                                if (sValue.length() == 0) {
                                    sValue = "' '";
                                }
                                sWhereSql += " (" + sValue + ") ";
                            } else if (cond.getSign().equalsIgnoreCase("between")) {
                                sValueAry = cond.getValue().split(",");
                                if (sValueAry.length == 1) {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[0]) + " ";
                                } else {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[1]) + " ";
                                }
                            } else {
                                sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                    cond.getValue()) + " ";
                            }
                        }

//               if (cond.getConRela().equalsIgnoreCase("end")) {
//                  break;
//               }

                        if (i < alConds.size() - 1 &&
                            !cond.getConRela().equalsIgnoreCase("end")) {
                            sWhereSql += " " + cond.getConRela() + " ";
                        }
                    }
                } else {
                    sGrpBySql += attrParam.getField() + ",";
                }
            }

            if (sWhereSql.equalsIgnoreCase(" where ")) {
                sWhereSql = "";
            }

            strSql = strSql.replaceAll("@1", sGrpBySql);
            strSql = strSql.replaceAll("@2", sWhereSql);
            if (sGrpBySql.length() > 0) {
                sGrpBySql = sGrpBySql.substring(0, sGrpBySql.length() - 1); //去掉最后一个“,”
                sGrpBySql = " group by " + sGrpBySql;
            }
            strSql = strSql.replaceAll("@3", sGrpBySql);
            strSql = strSql.replaceAll("@4", sRatioValueSql);
            if (strSql.trim().endsWith("AND")) {
                strSql = strSql.trim().substring(0, strSql.trim().length() - 3);
            } else if (strSql.trim().endsWith("OR")) {
                strSql = strSql.trim().substring(0, strSql.trim().length() - 2);
            }
            return strSql;
        } catch (Exception e) {
            throw new YssException("获取按品种集合监控的SQL语句出错： \n" + e.getMessage());
        }

    }

    //获取统计某组合某监控模版下某监控指标所有属性类型为持有类型的属性值的SQL语句
    protected String getHaveAttrSql(IndexTemplateBean template,
                                    CompIndexBean compIndex) throws YssException {
//      String sResult = "";
//      if (compIndex.getRangeType() == 0) {
        String strSql = "";
        String sWhereSql = "";
        CompIndexCondBean cond = null;
        String sTmp = "";
        ArrayList alHaveConds = null;

        alHaveConds = getCompConds(compIndex, 0);

        if (alHaveConds.size() > 0) {
            createHaveTmpTable();
            if (template.getSecTypes().length() > 0) {
                setPerSecHaveAttrData(template.getSecTypes(), compIndex);
            }
            if (template.getAccTypes().length() > 0) {
                setPerCashHaveAttrData(template.getAccTypes(), compIndex);
            }

            sWhereSql = this.buildPerFilterSql(alHaveConds);
            strSql = "(select * from Tb_tmp_Comp_Have_" + pub.getUserCode() +
                sWhereSql + ") have";
            cond = (CompIndexCondBean) alHaveConds.get(alHaveConds.size() - 1);
            if (cond.getConRela().equalsIgnoreCase("and")) {
                sTmp = " join @";
            } else if (cond.getConRela().equalsIgnoreCase("or")) {
                sTmp = " full join @";
            } else if (cond.getConRela().equalsIgnoreCase("end")) {
                sTmp = " #";
            }
            strSql = strSql + sTmp;
        }
        return strSql;

//      sResult = getPerHaveAttrSql(template, alHaveConds);
//      }
//      return sResult;
    }

    //获取统计某组合某监控模版下某监控指标所有属性类型为交易类型的属性值的SQL语句
    protected String getTradeAttrSql(IndexTemplateBean template,
                                     CompIndexBean compIndex,
                                     boolean bHaveJoinSql) throws YssException {
//      String sResult = "";
//      if (compIndex.getRangeType() == 0) {
        String strSql = "";
        String sWhereSql = "";
        CompIndexCondBean cond = null;
        String sTmp = "";
        ArrayList alTdConds = null;

        alTdConds = getCompConds(compIndex, 1);

        if (alTdConds.size() > 0) {
            createTradeTmpTable();
            if (template.getSecTypes().length() > 0) {
                setPerSecTradeAttrData(template.getSecTypes(), compIndex);
            }
            if (template.getAccTypes().length() > 0) {
                setPerCashTradeAttrData(template.getAccTypes(), compIndex);
            }

            sWhereSql = this.buildPerFilterSql(alTdConds);
            strSql = " (select * from Tb_tmp_Comp_Trade_" + pub.getUserCode() +
                sWhereSql + ") trade ";

            if (bHaveJoinSql) {
                strSql +=
                    " on have.FJoinCode1 = trade.FJoinCode1 and have.FJoinCode2 = trade.FJoinCode2 ";
            }

            cond = (CompIndexCondBean) alTdConds.get(alTdConds.size() - 1);
            if (cond.getConRela().equalsIgnoreCase("and")) {
                sTmp = " join @";
            } else if (cond.getConRela().equalsIgnoreCase("or")) {
                sTmp = " full join @";
            } else if (cond.getConRela().equalsIgnoreCase("end")) {
                sTmp = " #";
            }

            strSql = strSql + sTmp;
        }
        return strSql;

//      sResult = getPerTradeAttrSql(template, alTdConds, bHaveJoinSql);
//      }
//      return sResult;
    }

    //获取统计某组合某监控模版下某监控指标所有属性类型为品种类型的属性值的SQL语句
    protected String getCategoryAttrSql(IndexTemplateBean template,
                                        CompIndexBean compIndex,
                                        boolean bHaveJoinSql,
                                        boolean bTdJoinSql) throws YssException {
//      String sResult = "";
//      if (compIndex.getRangeType() == 0) {
        String strSql = "";
        CompIndexCondBean cond = null;
        String sTmp = "";
        String sWhereSql = "";
        ArrayList alCatConds = null;

        alCatConds = getCompConds(compIndex, 2);

        if (alCatConds.size() > 0) {
            createCategoryTmpTable();
            if (template.getSecTypes().length() > 0) {
                setPerSecCatAttrData(template.getSecTypes(), compIndex);
            }
            if (template.getAccTypes().length() > 0) {
                setPerCashCatAttrData(template.getAccTypes(), compIndex);
            }

            sWhereSql = this.buildPerFilterSql(alCatConds);
            strSql = " (select * from Tb_tmp_Comp_Category_" + pub.getUserCode() +
                sWhereSql + ") cat ";

            if (bHaveJoinSql) {
                strSql +=
                    " on have.FJoinCode1 = cat.FJoinCode1 and have.FJoinCode2 = cat.FJoinCode2";
            }
            if (bTdJoinSql) {
                strSql += (bHaveJoinSql ? " and " : " on ") +
                    " trade.FJoinCode1 = cat.FJoinCode1 and trade.FJoinCode2 = cat.FJoinCode2";
            }

            cond = (CompIndexCondBean) alCatConds.get(alCatConds.size() - 1);
            if (cond.getConRela().equalsIgnoreCase("and")) {
                sTmp = " join @";
            } else if (cond.getConRela().equalsIgnoreCase("or")) {
                sTmp = " full join @";
            } else if (cond.getConRela().equalsIgnoreCase("end")) {
                sTmp = " #";
            }

            strSql = strSql + sTmp;

        }
        return strSql;

//      sResult = getPerCategoryAttrSql(template, alCatConds, bHaveJoinSql,
//                                      bTdJoinSql);
//      }
//      return sResult;
    }

    //获取统计某组合某监控模版下某监控指标所有属性类型为品种子类型的属性值的SQL语句
    protected String getSubCatAttrSql(IndexTemplateBean template,
                                      CompIndexBean compIndex,
                                      boolean bHaveJoinSql,
                                      boolean bTdJoinSql, boolean bCatJoinSql) throws
        YssException {
        String strSql = "";
        CompIndexCondBean cond = null;
        String sTmp = "";
        String sWhereSql = "";
        ArrayList alSubCatConds = null;

        alSubCatConds = getCompConds(compIndex, 4);

        if (alSubCatConds.size() > 0) {
            createSubCatTmpTable();
            if (template.getSecTypes().length() > 0) {
                setPerSecSubCatAttrData(template.getSecTypes(), compIndex);
            }

            sWhereSql = this.buildPerFilterSql(alSubCatConds);
            strSql = " (select * from Tb_tmp_Comp_SubCat_" + pub.getUserCode() +
                sWhereSql + ") subcat ";

            if (bHaveJoinSql) {
                strSql +=
                    " on have.FJoinCode1 = subcat.FJoinCode1 and have.FJoinCode2 = subcat.FJoinCode2";
            }
            if (bTdJoinSql) {
                strSql += (bHaveJoinSql ? " and " : " on ") +
                    " trade.FJoinCode1 = subcat.FJoinCode1 and trade.FJoinCode2 = subcat.FJoinCode2";
            }
            if (bCatJoinSql) {
                strSql += ( (bHaveJoinSql || bTdJoinSql) ? " and " : " on ") +
                    " cat.FJoinCode1 = subcat.FJoinCode1 and cat.FJoinCode2 = subcat.FJoinCode2";
            }

        }
        return strSql;
    }

    //创建持有类属性的临时表
    protected void createHaveTmpTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist("Tb_tmp_Comp_Have_" +
                                  pub.getUserCode())) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_Comp_Have_" +
                               pub.getUserCode()));
                /**end*/
            }

            strSql = "create table Tb_tmp_Comp_Have_" + pub.getUserCode() +
                " (FJoinCode1 varchar2(20)," +
                " FPortCode varchar2(20)," +
                " FInvMgrCode varchar2(20)," +
                " FBrokerCode varchar2(20)," +
                " FCatCode varchar2(20)," +
                " FHaveAmount DECIMAL(18, 4)," +
                " FHaveCost DECIMAL(18, 4)," +
                " FHaveMCost DECIMAL(18, 4)," +
                " FHaveVCost DECIMAL(18, 4)," +
                " FHaveBaseCost DECIMAL(18, 4)," +
                " FHaveMBaseCost DECIMAL(18, 4)," +
                " FHaveVBaseCost DECIMAL(18, 4)," +
                " FHavePortCost DECIMAL(18, 4)," +
                " FHaveMPortCost DECIMAL(18, 4)," +
                " FHaveVPortCost DECIMAL(18, 4)," +
                " FMarketBaseValue DECIMAL(18, 4)," +
                " FCurBaseMoney DECIMAL(18, 4)," +
                " FJoinCode2 varchar2(10)," +
                " FHaveProportion DECIMAL(18, 4)" +
//               " FScaleField1 DECIMAL(18, 12)," +
//               " FScaleField2 DECIMAL(18, 12)," +
//               " FScaleField3 DECIMAL(18, 12)," +
//               " FScaleField4 DECIMAL(18, 12)," +
//               " FScaleField5 DECIMAL(18, 12)," +
                ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建持有类属性的临时表出错！");
        }
    }

    //创建交易类属性的临时表
    protected void createTradeTmpTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist("Tb_tmp_Comp_Trade_" +
                                  pub.getUserCode())) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_Comp_Trade_" +
                               pub.getUserCode()));
                /**end*/
            }

            strSql = "create table Tb_tmp_Comp_Trade_" + pub.getUserCode() +
                " (FJoinCode1 varchar2(20)," +
                " FPortCode varchar2(20)," +
                " FInvMgrCode varchar2(20)," +
                " FCatCode varchar2(20)," +
                " FBrokerCode varchar2(20)," +
                " FTradeAmount DECIMAL(18, 4)," +
                " FAccruedinterest DECIMAL(18, 4)," +
                " FTdCost DECIMAL(18, 4)," +
                " FTdMCost DECIMAL(18, 4)," +
                " FTdVCost DECIMAL(18, 4)," +
                " FTdBaseCuryCost DECIMAL(18, 4)," +
                " FTdMBaseCuryCost DECIMAL(18, 4)," +
                " FTdVBaseCuryCost DECIMAL(18, 4)," +
                " FTdPortCuryCost DECIMAL(18, 4)," +
                " FTdMPortCuryCost DECIMAL(18, 4)," +
                " FTdVPortCuryCost DECIMAL(18, 4)," +
                " FJoinCode2 varchar2(10)" +
                ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建交易类属性的临时表出错！");
        }
    }

    //创建品种类属性的临时表
    protected void createCategoryTmpTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist("Tb_tmp_Comp_Category_" +
                                  pub.getUserCode())) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_Comp_Category_" +
                               pub.getUserCode()));
                /**end*/
            }

            strSql = "create table Tb_tmp_Comp_Category_" + pub.getUserCode() +
                " (FJoinCode1 varchar2(20)," +
                " FExchangeCode varchar2(20)," +
                " FMarketCode varchar2(20)," +
                " FCatCode varchar2(20)," +
                " FSubCatCode varchar2(20)," +
                " FCuryCode varchar2(20)," +
                " FSectorCode varchar2(20)," +
                " FTotalShare DECIMAL(18, 4)," +
                " FCurrentShare DECIMAL(18, 4)," +
                " FBankCode varchar2(20)," +
                " FBankAccount varchar2(50)," +
                " FMatureDate Date," +
                " FSubAccType varchar2(20)," +
                " FJoinCode2 varchar2(20)," +
                " FIssueDate Date," +
                " FIssuePrice DECIMAL(18, 4)," +
                " FFaceRate DECIMAL(18, 12)," +
                " FInsFrequency DECIMAL(18, 4)," +
                " FCreditLevel varchar2(20)," +
                " FFaceValue DECIMAL(18, 4)," +
                " FInsStartDate Date," +
                " FInsEndDate Date," +
                " FInsCashDate Date," +
                " FIssueCorpCode varchar2(20)," +
                " FODDayNum DECIMAL(18, 4)" + //剩余期限
                ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建品种类属性的临时表出错！");
        }
    }

    //创建品种子类属性的临时表
    protected void createSubCatTmpTable() throws YssException {
        String strSql = "";
        try {
            if (dbl.yssTableExist("Tb_tmp_Comp_SubCat_" +
                                  pub.getUserCode())) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table Tb_tmp_Comp_SubCat_" +
                               pub.getUserCode()));
                /**end*/
            }

            strSql = "create table Tb_tmp_Comp_SubCat_" + pub.getUserCode() +
                " (FJoinCode1 varchar2(20)," +
                " FOrganCode varchar2(20)," +
                " FCreditLevelCode varchar2(20)," +
                " FLevel number(3)," +
                " FJoinCode2 varchar2(20)" +
                ")";
            dbl.executeSql(strSql);
        } catch (Exception e) {
            throw new YssException("创建品种子类属性的临时表出错！");
        }
    }

    //获取证券库存或现金库存的sql,根据监控模板的范围，持有类型属性条件
//   protected String getPerHaveAttrSql(IndexTemplateBean template,
//                                      ArrayList alHaveConds) throws
//         YssException {
//      String strSql = "";
//      String sWhereSql = "";
//      CompIndexCondBean cond = null;
//      String sTmp = "";
//      if (alHaveConds.size() > 0) {
//         createHaveTmpTable();
//         if (template.getSecTypes().length() > 0) {
//            setPerSecHaveAttrData(template.getSecTypes());
//         }
//         if (template.getAccTypes().length() > 0) {
//            setPerCashHaveAttrData(template.getAccTypes());
//         }
//
//         sWhereSql = this.buildPerFilterSql(alHaveConds);
//         strSql = "(select * from Tb_tmp_Comp_Have_" + pub.getUserCode() +
//               sWhereSql + ") have";
//         cond = (CompIndexCondBean) alHaveConds.get(alHaveConds.size() - 1);
//         if (cond.getConRela().equalsIgnoreCase("and")) {
//            sTmp = " join @";
//         }
//         else if (cond.getConRela().equalsIgnoreCase("or")) {
//            sTmp = " full join @";
//         }
//         else if (cond.getConRela().equalsIgnoreCase("end")) {
//            sTmp = " #";
//         }
//         strSql = strSql + sTmp;
//      }
//      return strSql;
//   }

    public void setPerSecHaveAttrData(String sSecTypes, CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        double dMValue = 0;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into Tb_tmp_Comp_Have_" + pub.getUserCode() +
                " (FJoinCode1,FPortCode,FInvMgrCode,FBrokerCode,FCatCode,FHaveAmount,FHaveCost," +
                " FHaveMCost,FHaveVCost,FHaveBaseCost,FHaveMBaseCost,FHaveVBaseCost," +
                " FHavePortCost,FHaveMPortCost,FHaveVPortCost,FJoinCode2,FMarketBaseValue,FHaveProportion)" + //2008.01.09 添加字段 FHaveProportion 蒋锦
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);
            //2008.01.09 添加字段 FHaveProportion 蒋锦
            strSql = " select k.*, e.*, CASE WHEN e.FTotalShare = 0 THEN 0 ELSE (k.FHaveAmount / e.FTotalShare) END AS FHaveProportion from (" +
                " select ck.*," +
                dbl.sqlIsNull("ck.FSecurityCode", "t.FSecurityCode") +
                " as FHSecurityCode," +
                dbl.sqlIsNull("ck.FPortCode", "t.FPortCode") + " as FHPortCode," +
                dbl.sqlIsNull("ck.FAnalysisCode1", "t.FInvMgrCode") +
                "  as FHInvMgrCode," +
                dbl.sqlIsNull("ck.FAnalysisCode2", "t.FBrokerCode") +
                "  as FHBrokerCode," +
                dbl.sqlIsNull("ck.FMarketPrice", "t.FTradePrice") +
                "  as FHMarketPrice," +
                dbl.sqlIsNull("ck.FBaseCuryRate", "t.FBaseCuryRate") +
                "  as FHBaseCuryRate," +
                " ' ' as FHCatCode," +
                " (" + dbl.sqlIsNull("ck.FStorageAmount", "0") + " + " +
                dbl.sqlIsNull("t.FTradeAmount", "0") + ") as FHaveAmount," +
                " (" + dbl.sqlIsNull("ck.FStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FCost", "0") + ") as FHaveCost," +
                " (" + dbl.sqlIsNull("ck.FMStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FMCost", "0") + ") as FHaveMCost," +
                " (" + dbl.sqlIsNull("ck.FVStorageCost", "0") + " + " +
                dbl.sqlIsNull("t.FVCost", "0") + ") as FHaveVCost," +
                " (" + dbl.sqlIsNull("ck.FBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FBaseCuryCost", "0") + ") as FHaveBaseCost," +
                " (" + dbl.sqlIsNull("ck.FMBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FMBaseCuryCost", "0") + ") as FHaveMBaseCost," +
                " (" + dbl.sqlIsNull("ck.FVBaseCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FVBaseCuryCost", "0") + ") as FHaveVBaseCost," +
                " (" + dbl.sqlIsNull("ck.FPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FPortCuryCost", "0") + ") as FHavePortCost," +
                " (" + dbl.sqlIsNull("ck.FMPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FMPortCuryCost", "0") + ") as FHaveMPortCost," +
                " (" + dbl.sqlIsNull("ck.FVPortCuryCost", "0") + " + " +
                dbl.sqlIsNull("t.FVPortCuryCost", "0") + ") as FHaveVPortCost," +
                " 'Sec' as FHaveType from " +
                //------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradePrice,FBaseCuryRate," +
                " sum(FTradeAmount*FAmountInd) as FTradeAmount," +
                " sum(FAccruedinterest*FAmountInd) as FAccruedinterest, " +
                " sum(FCost*FAmountInd) as FCost, sum(FMCost*FAmountInd) as FMCost, sum(FVCost*FAmountInd) as FVCost," +
                " sum(FBaseCuryCost*FAmountInd) as FBaseCuryCost, sum(FMBaseCuryCost*FAmountInd) as FMBaseCuryCost,sum(FVBaseCuryCost*FAmountInd) as FVBaseCuryCost," +
                " sum(FPortCuryCost*FAmountInd) as FPortCuryCost, sum(FMPortCuryCost*FAmountInd) as FMPortCuryCost,sum(FVPortCuryCost*FAmountInd) as FVPortCuryCost from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode" +
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                orderWhereSql("Tb_Data_SubTrade", compIndex) +
                " and (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = "
                + dbl.sqlString(pub.getUserCode()) + "))" +
                " group by FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradePrice,FBaseCuryRate" +
                " )t full join " +
                //------------------------------------------------------------------
                " (select FSecurityCode,FPortCode,FStorageAmount,FStorageCost,FMStorageCost," +
                " FVStorageCost,FPortCuryCost,FMPortCuryCost,FVPortCuryCost,FBaseCuryCost," +
                " FMBaseCuryCost,FVBaseCuryCost,FAnalysisCode1," +
                " FAnalysisCode2,FAnalysisCode3,FMarketPrice,FBaseCuryRate" +
                " from " + pub.yssGetTableName("Tb_Stock_Security") +
                " where FCheckState = 1 and FPortCode = " +
                dbl.sqlString(portCode) +
                orderWhereSql("Tb_Stock_Security", compIndex) +
                " and " + operSql.sqlStoragEve(dDate) +
                " )ck on ck.FSecurityCode = t.FSecurityCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FInvMgrCode" +
                " and ck.FAnalysisCode2 = t.FBrokerCode) k" +
                //------------------------------------------------------------------
                " join (select eb.*, ea.FTotalShare from (select FSecurityCode, max(FStartDate) as FStartDate, FTotalShare from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 " +
                " group by FSecurityCode, FTotalShare) ea " +
                " join (select FSecurityCode as FSecurityCode2, FStartDate, FCatCode, FFactor from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode in (" + this.operSql.sqlCodes(sSecTypes) +
                ")) eb " +
                " on ea.FSecurityCode = eb.FSecurityCode2 and ea.FStartDate = eb.FStartDate) e " +
                " on k.FHSecurityCode = e.FSecurityCode2 ";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FHSecurityCode"));
                pst.setString(2, rs.getString("FHPortCode"));
                pst.setString(3, rs.getString("FHInvMgrCode"));
                pst.setString(4, rs.getString("FHBrokerCode"));
                pst.setString(5, rs.getString("FHCatCode"));
                pst.setDouble(6, rs.getDouble("FHaveAmount"));
                pst.setDouble(7, rs.getDouble("FHaveCost"));
                pst.setDouble(8, rs.getDouble("FHaveMCost"));
                pst.setDouble(9, rs.getDouble("FHaveVCost"));
                pst.setDouble(10, rs.getDouble("FHaveBaseCost"));
                pst.setDouble(11, rs.getDouble("FHaveMBaseCost"));
                pst.setDouble(12, rs.getDouble("FHaveVBaseCost"));
                pst.setDouble(13, rs.getDouble("FHavePortCost"));
                pst.setDouble(14, rs.getDouble("FHaveMPortCost"));
                pst.setDouble(15, rs.getDouble("FHaveVPortCost"));
                pst.setString(16, rs.getString("FHaveType"));
                if (rs.getDouble("FFactor") != 0) {
                    dMValue = YssD.div(rs.getDouble("FHaveAmount"),
                                       rs.getDouble("FFactor"));
                } else {
                    dMValue = rs.getDouble("FHaveAmount");
                }
                dMValue = YssD.mul(dMValue,
                                   rs.getDouble("FHMarketPrice"));
                dMValue = YssD.mul(dMValue, rs.getDouble("FHBaseCuryRate"));
                dMValue = YssD.round(dMValue, 2);
                pst.setDouble(17, dMValue);
                pst.setDouble(18, rs.getDouble("FHaveProportion"));
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("设置持有类属性证券数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
//      return strSql;
    }

    public void setPerCashHaveAttrData(String sCashTypes,
                                       CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        String sWhereSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        double dCurBaseMoney = 0;
        double dRate = 0;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into Tb_tmp_Comp_Have_" + pub.getUserCode() +
                " (FJoinCode1,FPortCode,FInvMgrCode,FBrokerCode,FCatCode,FHaveAmount,FHaveCost," +
                " FHaveMCost,FHaveVCost,FHaveBaseCost,FHaveMBaseCost,FHaveVBaseCost," +
                " FHavePortCost,FHaveMPortCost,FHaveVPortCost,FJoinCode2,FMarketBaseValue,FCurBaseMoney)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql = " select * from (" +
                " select ck.*, " +
                dbl.sqlIsNull("ck.FCashAccCode", "t.FCashAccCode") +
                " as FHCashAccCode," +
                dbl.sqlIsNull("ck.FPortCode", "t.FPortCode") + " as FHPortCode," +
                dbl.sqlIsNull("ck.FAnalysisCode1", "t.FAnalysisCode1") +
                "  as FHInvMgrCode," +
                " ' ' as FHBrokerCode," +
                dbl.sqlIsNull("ck.FAnalysisCode2", "t.FAnalysisCode2") +
                "  as FHCatCode," +
                " 0 as FHaveAmount," +
                " (" + dbl.sqlIsNull("ck.FAccBalance", "0") + "+" +
                dbl.sqlIsNull("t.FMoney", "0") + ") as FHaveCost," +
                " 0 as FHaveMCost," +
                " 0 as FHaveVCost," +
                " (" + dbl.sqlIsNull("ck.FBaseCuryBal", "0") + "+" +
                dbl.sqlIsNull("t.FBaseMoney", "0") + ") as FHaveBaseCost," +
                " 0 as FHaveMBaseCost," +
                " 0 as FHaveVBaseCost," +
                " (" + dbl.sqlIsNull("ck.FPortCuryBal", "0") + "+" +
                dbl.sqlIsNull("t.FPortMoney", "0") + ") as FHavePortCost," +
                " 0 as FHaveMPortCost," +
                " 0 as FHaveVPortCost," +
                " 'Cash' as FHaveType from " +
                //------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2," +
                " sum(FMoney*FInOut) as FMoney,sum(FMoney*FBaseCuryRate*FInOut) as FBaseMoney," +
                " sum(FMoney*FBaseCuryRate/FPortCuryRate*FInOut) as FPortMoney from " +
                " (select a.FTransDate, a.FTransferDate, b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " b on a.FNum = b.FNum where (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = " +
                dbl.sqlString(pub.getUserCode()) + ")) and (" +
                dbl.sqlDate(dDate)
                + " between FTransDate and FTransferDate )" + //取可用头寸
                " and FPortCode = " + dbl.sqlString(portCode) +
                orderWhereSql("Tb_Cash_SubTransfer", compIndex) +
                " ) group by FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2)" +
                " t full join " +
                //------------------------------------------------------------------
                " (select FCashAccCode,FPortCode,FAccBalance," +
                " FPortCuryBal,FBaseCuryBal,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3" +
                " from " + pub.yssGetTableName("Tb_Stock_Cash") +
                " where FPortCode = " + dbl.sqlString(portCode) +
                orderWhereSql("Tb_Stock_Cash", compIndex) +
                " and FCheckState = 1" +
                " and " + operSql.sqlStoragEve(dDate) +
                " ) ck on ck.FCashAccCode = t.FCashAccCode and " +
                " ck.FPortCode = t.FPortCode and ck.FAnalysisCode1 = t.FAnalysisCode1" +
                " and ck.FAnalysisCode2 = t.FAnalysisCode2) k" +
                //------------------------------------------------------------------
                " join (select eb.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 " +
                " group by FCashAccCode) ea " +
                " join (select FCashAccCode as FCashAccCode2, FStartDate,FCuryCode, FAccType from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FAccType in (" + this.operSql.sqlCodes(sCashTypes) +
                ") " + orderWhereSql("Tb_Para_CashAccount", compIndex) +
                " ) eb " +
                " on ea.FCashAccCode = eb.FCashAccCode2 and ea.FStartDate = eb.FStartDate) e " +
                " on k.FHCashAccCode = e.FCashAccCode2";

            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dCurBaseMoney = 0;
                pst.setString(1, rs.getString("FHCashAccCode"));
                pst.setString(2, rs.getString("FHPortCode"));
                pst.setString(3, rs.getString("FHInvMgrCode"));
                pst.setString(4, rs.getString("FHBrokerCode"));
                pst.setString(5, rs.getString("FHCatCode"));
                pst.setDouble(6, rs.getDouble("FHaveAmount"));
                pst.setDouble(7, rs.getDouble("FHaveCost"));
                pst.setDouble(8, rs.getDouble("FHaveMCost"));
                pst.setDouble(9, rs.getDouble("FHaveVCost"));
                pst.setDouble(10, rs.getDouble("FHaveBaseCost"));
                pst.setDouble(11, rs.getDouble("FHaveMBaseCost"));
                pst.setDouble(12, rs.getDouble("FHaveVBaseCost"));
                pst.setDouble(13, rs.getDouble("FHavePortCost"));
                pst.setDouble(14, rs.getDouble("FHaveMPortCost"));
                pst.setDouble(15, rs.getDouble("FHaveVPortCost"));
                pst.setString(16, rs.getString("FHaveType"));
                pst.setDouble(17, rs.getDouble("FHaveBaseCost"));
                dRate = this.getSettingOper().getCuryRate(dDate, rs.getString("FCuryCode"), this.portCode, YssOperCons.YSS_RATE_BASE);
                dCurBaseMoney = YssD.round(YssD.mul(rs.getDouble("FHaveCost"), dRate), 2);
                pst.setDouble(18, dCurBaseMoney);
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("设置持有类属性现金数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

//      return strSql;
    }

//   protected String getPerTradeAttrSql(IndexTemplateBean template,
//                                       ArrayList alTdConds,
//                                       boolean bHaveJoinSql) throws
//         YssException {
//      String strSql = "";
//      String sWhereSql = "";
//      CompIndexCondBean cond = null;
//      String sTmp = "";
//      if (alTdConds.size() > 0) {
//         createTradeTmpTable();
//         if (template.getSecTypes().length() > 0) {
//            setPerSecTradeAttrData(template.getSecTypes());
//         }
//         if (template.getAccTypes().length() > 0) {
//            setPerCashTradeAttrData(template.getAccTypes());
//         }
//
//         sWhereSql = this.buildPerFilterSql(alTdConds);
//         strSql = " (select * from Tb_tmp_Comp_Trade_" + pub.getUserCode() +
//               sWhereSql + ") trade ";
//
//         if (bHaveJoinSql) {
//            strSql +=
//                  " on have.FJoinCode1 = trade.FJoinCode1 and have.FJoinCode2 = trade.FJoinCode2 ";
//         }
//
//         cond = (CompIndexCondBean) alTdConds.get(alTdConds.size() - 1);
//         if (cond.getConRela().equalsIgnoreCase("and")) {
//            sTmp = " join @";
//         }
//         else if (cond.getConRela().equalsIgnoreCase("or")) {
//            sTmp = " full join @";
//         }
//         else if (cond.getConRela().equalsIgnoreCase("end")) {
//            sTmp = " #";
//         }
//
//         strSql = strSql + sTmp;
//      }
//      return strSql;
//   }

    /**
     * getPerCashTdAttrSql
     *
     * @param string String
     * @param alTdConds ArrayList
     * @return String
     */
    public void setPerCashTradeAttrData(String sCashTypes,
                                        CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into Tb_tmp_Comp_Trade_" + pub.getUserCode() +
                " (FJoinCode1,FPortCode,FInvMgrCode,FCatCode,FBrokerCode,FTradeAmount,FAccruedinterest," +
                " FTdCost,FTdMCost,FTdVCost,FTdBaseCuryCost,FTdMBaseCuryCost,FTdVBaseCuryCost," +
                " FTdPortCuryCost,FTdMPortCuryCost,FTdVPortCuryCost,FJoinCode2)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql =
                " select FCashAccCode as FTCashAccCode,FPortCode as FTPortCode," +
                " FAnalysisCode1 as FTInvMgrCode,FAnalysisCode2 as FTCatCode, ' ' as FTBrokerCode," +
                " 0 as FTradeAmount, 0 as FAccruedinterest," +
                " sum(FMoney*FInOut) as FTdCost, 0 as FTdMCost, 0 as FTdVCost," +
                " sum(FMoney*FBaseCuryRate*FInOut) as FTdBaseCuryCost, " +
                " 0 as FTdMBaseCuryCost, 0 as FTdVBaseCuryCost," +
                " sum(FMoney*FBaseCuryRate/FPortCuryRate*FInOut) as FTdPortCuryCost," +
                " 0 as FTdMPortCuryCost, 0 as FTdVPortCuryCost," +
                " 'Cash' as FJoinCode2 from " +
                " (select a.FTransDate, a.FTransferDate, b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " b on a.FNum = b.FNum " +
                //------------------------------------------------------------------------------------
                " join (select eb.* from (select FCashAccCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 " +
                " group by FCashAccCode) ea " +
                " join (select FCashAccCode as FCashAccCode2, FStartDate, FAccType from " +
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FAccType in (" + this.operSql.sqlCodes(sCashTypes) +
                ") " + orderWhereSql("Tb_Para_CashAccount", compIndex) +
                " ) eb " +
                " on ea.FCashAccCode = eb.FCashAccCode2 and ea.FStartDate = eb.FStartDate) e " +
                " on b.FCashAccCode = e.FCashAccCode2" +
                //------------------------------------------------------------------------------------
                " where (" + dbl.sqlDate(dDate) +
                " between FTransDate and FTransferDate)" + //取可用头寸
                orderWhereSql("Tb_Cash_SubTransfer", compIndex) +
                " and FPortCode = " + dbl.sqlString(this.portCode) +
                " and a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = " +
                dbl.sqlString(pub.getUserCode()) +
                ")) group by FCashAccCode,FPortCode,FAnalysisCode1,FAnalysisCode2,' '";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FTCashAccCode"));
                pst.setString(2, rs.getString("FTPortCode"));
                pst.setString(3, rs.getString("FTInvMgrCode"));
                pst.setString(4, rs.getString("FTCatCode"));
                pst.setString(5, rs.getString("FTBrokerCode"));
                pst.setDouble(6, rs.getDouble("FTradeAmount"));
                pst.setDouble(7, rs.getDouble("FAccruedinterest"));
                pst.setDouble(8, rs.getDouble("FTdCost"));
                pst.setDouble(9, rs.getDouble("FTdMCost"));
                pst.setDouble(10, rs.getDouble("FTdVCost"));
                pst.setDouble(11, rs.getDouble("FTdBaseCuryCost"));
                pst.setDouble(12, rs.getDouble("FTdMBaseCuryCost"));
                pst.setDouble(13, rs.getDouble("FTdVBaseCuryCost"));
                pst.setDouble(14, rs.getDouble("FTdPortCuryCost"));
                pst.setDouble(15, rs.getDouble("FTdMPortCuryCost"));
                pst.setDouble(16, rs.getDouble("FTdVPortCuryCost"));
                pst.setString(17, rs.getString("FJoinCode2"));
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException("设置交易类属性现金数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * getPerSecTdAttrSql
     *
     * @param string String
     * @param alTdConds ArrayList
     * @return String
     */
    public void setPerSecTradeAttrData(String sSecTypes, CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into Tb_tmp_Comp_Trade_" + pub.getUserCode() +
                " (FJoinCode1,FPortCode,FInvMgrCode,FCatCode,FBrokerCode,FTradeAmount,FAccruedinterest," +
                " FTdCost,FTdMCost,FTdVCost,FTdBaseCuryCost,FTdMBaseCuryCost,FTdVBaseCuryCost," +
                " FTdPortCuryCost,FTdMPortCuryCost,FTdVPortCuryCost,FJoinCode2)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql = " select FSecurityCode as FJoinCode1,FPortCode as FTPortCode,FInvMgrCode as FTInvMgrCode," +
                " ' ' as FTCatCode,FBrokerCode as FTBrokerCode," +
                " sum(FTradeAmount*FAmountInd) as FTradeAmount," +
                " sum(FAccruedinterest*FAmountInd) as FAccruedinterest, " +
                " sum(FCost*FAmountInd) as FTdCost, sum(FMCost*FAmountInd) as FTdMCost, sum(FVCost*FAmountInd) as FTdVCost," +
                " sum(FBaseCuryCost*FAmountInd) as FTdBaseCuryCost, sum(FMBaseCuryCost*FAmountInd) as FTdMBaseCuryCost," +
                " sum(FVBaseCuryCost*FAmountInd) as FTdVBaseCuryCost," +
                " sum(FPortCuryCost*FAmountInd) as FTdPortCuryCost, sum(FMPortCuryCost*FAmountInd) as FTdMPortCuryCost," +
                " sum(FVPortCuryCost*FAmountInd) as FTdVPortCuryCost, 'Sec' as FJoinCode2 from " +
                pub.yssGetTableName("Tb_Data_SubTrade") +
                " a left join (select * from Tb_Base_TradeType where FCheckState = 1) b " +
                " on a.FTradeTypeCode = b.FTradeTypeCode" +
                //------------------------------------------------------------------------------------
                " join (select eb.* from (select FSecurityCode, max(FStartDate) as FStartDate from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FStartDate <= " + dbl.sqlDate(dDate) +
                " and FCheckState = 1 " +
                " group by FSecurityCode) ea " +
                " join (select FSecurityCode as FSecurityCode2, FStartDate, FCatCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " where FCatCode in (" + this.operSql.sqlCodes(sSecTypes) +
                ")) eb " +
                " on ea.FSecurityCode = eb.FSecurityCode2 and ea.FStartDate = eb.FStartDate) e " +
                " on a.FSecurityCode = e.FSecurityCode2" +
                //------------------------------------------------------------------------------------
                " where FBargainDate = " + dbl.sqlDate(dDate) +
                orderWhereSql("Tb_Data_SubTrade", compIndex) +
                " and FPortCode = " + dbl.sqlString(portCode) +
                " and (a.FCheckState = 1 or (a.FCheckState = 3 and a.FCreator = " +
                dbl.sqlString(pub.getUserCode()) + ")) " +
                " group by FSecurityCode,FPortCode,FInvMgrCode,' ',FBrokerCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FJoinCode1"));
                pst.setString(2, rs.getString("FTPortCode"));
                pst.setString(3, rs.getString("FTInvMgrCode"));
                pst.setString(4, rs.getString("FTCatCode"));
                pst.setString(5, rs.getString("FTBrokerCode"));
                pst.setDouble(6, rs.getDouble("FTradeAmount"));
                pst.setDouble(7, rs.getDouble("FAccruedinterest"));
                pst.setDouble(8, rs.getDouble("FTdCost"));
                pst.setDouble(9, rs.getDouble("FTdMCost"));
                pst.setDouble(10, rs.getDouble("FTdVCost"));
                pst.setDouble(11, rs.getDouble("FTdBaseCuryCost"));
                pst.setDouble(12, rs.getDouble("FTdMBaseCuryCost"));
                pst.setDouble(13, rs.getDouble("FTdVBaseCuryCost"));
                pst.setDouble(14, rs.getDouble("FTdPortCuryCost"));
                pst.setDouble(15, rs.getDouble("FTdMPortCuryCost"));
                pst.setDouble(16, rs.getDouble("FTdVPortCuryCost"));
                pst.setString(17, rs.getString("FJoinCode2"));
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException("设置交易类属性证券数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

//   protected String getPerCategoryAttrSql(IndexTemplateBean template,
//                                          ArrayList alCatConds,
//                                          boolean bHaveJoinSql,
//                                          boolean bTdJoinSql) throws
//         YssException {
//      String strSql = "";
//      CompIndexCondBean cond = null;
//      String sWhereSql = "";
//
//      if (alCatConds.size() > 0) {
//         createCategoryTmpTable();
//         if (template.getSecTypes().length() > 0) {
//            setPerSecCatAttrData(template.getSecTypes());
//         }
//         if (template.getAccTypes().length() > 0) {
//            setPerCashCatAttrData(template.getAccTypes());
//         }
//
//         sWhereSql = this.buildPerFilterSql(alCatConds);
//         strSql = " (select * from Tb_tmp_Comp_Category_" + pub.getUserCode() +
//               sWhereSql + ") cat ";
//
//         if (bHaveJoinSql) {
//            strSql +=
//                  " on have.FJoinCode1 = cat.FJoinCode1 and have.FJoinCode2 = cat.FJoinCode2";
//         }
//         if (bTdJoinSql) {
//            strSql += (bHaveJoinSql ? " and " : " on ") +
//                  " trade.FJoinCode1 = cat.FJoinCode1 and trade.FJoinCode2 = cat.FJoinCode2";
//         }
//      }
//      return strSql;
//   }

    /**
     * getPerCashCatAttrSql
     *
     * @param string String
     * @param alCatConds ArrayList
     * @return String
     */
    public void setPerCashCatAttrData(String sAccTypes, CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        double dODDayNum = 0;
        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "insert into Tb_tmp_Comp_Category_" + pub.getUserCode() +
                " (FJoinCode1,FExchangeCode,FMarketCode,FCatCode,FSubCatCode,FCuryCode,FSectorCode," +
                " FTotalShare,FCurrentShare,FBankCode,FBankAccount,FMatureDate,FSubAccType," +
                " FJoinCode2,FIssueDate,FIssuePrice,FFaceRate,FInsFrequency,FCreditLevel," +
                " FFaceValue,FInsStartDate,FInsEndDate,FInsCashDate,FODDayNum)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql = "select FCashAccCode as FJoinCode1," +
                " null as FExchangeCode, null as FMarketCode, null as FCatCode, null as FSubCatCode,FCuryCode," +
                " null as FSectorCode, null as FTotalShare, null as FCurrentShare," +
                " FBankCode," +
                " FBankAccount,FMatureDate,FSubAccType,'Cash' as FJoinCode2," +
                " null as FIssueDate,null as FIssuePrice,null as FFaceRate,null as FInsFrequency," +
                " null as FCreditLevel,null as FFaceValue,null as FInsStartDate,null as FInsEndDate," +
                " null as FInsCashDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FAccType in (" + operSql.sqlCodes(sAccTypes) + ") and " +
                " FCheckState = 1" +
                orderWhereSql("Tb_Para_CashAccount", compIndex);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FJoinCode1"));
                pst.setString(2, rs.getString("FExchangeCode"));
                pst.setString(3, rs.getString("FMarketCode"));
                pst.setString(4, rs.getString("FCatCode"));
                pst.setString(5, rs.getString("FSubCatCode"));
                pst.setString(6, rs.getString("FCuryCode"));
                pst.setString(7, rs.getString("FSectorCode"));
                pst.setDouble(8, rs.getDouble("FTotalShare"));
                pst.setDouble(9, rs.getDouble("FCurrentShare"));
                pst.setString(10, rs.getString("FBankCode"));
                pst.setString(11, rs.getString("FBankAccount"));
                pst.setDate(12, rs.getDate("FMatureDate"));
                pst.setString(13, rs.getString("FSubAccType"));
                pst.setString(14, rs.getString("FJoinCode2"));
                pst.setDate(15, rs.getDate("FIssueDate"));
                pst.setDouble(16, rs.getDouble("FIssuePrice"));
                pst.setDouble(17, rs.getDouble("FFaceRate"));
                pst.setDouble(18, rs.getDouble("FInsFrequency"));
                pst.setString(19, rs.getString("FCreditLevel"));
                pst.setDouble(20, rs.getDouble("FFaceValue"));
                pst.setDate(21, rs.getDate("FInsStartDate"));
                pst.setDate(22, rs.getDate("FInsEndDate"));
                pst.setDate(23, rs.getDate("FInsCashDate"));
                dODDayNum = 0;
                if (rs.getDate("FMatureDate") != null) {
                    dODDayNum = YssFun.dateDiff(dDate, rs.getDate("FMatureDate"));
                }
                pst.setDouble(24, dODDayNum);
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException("设置品种类属性现金数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * getPerSecCatAttrSql
     *
     * @param string String
     * @param alCatConds ArrayList
     * @return String
     */
    public void setPerSecCatAttrData(String sSecTypes, CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;
        double dODDayNum = 0;
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "insert into Tb_tmp_Comp_Category_" + pub.getUserCode() +
                " (FJoinCode1,FExchangeCode,FMarketCode,FCatCode,FSubCatCode,FCuryCode,FSectorCode," +
                " FTotalShare,FCurrentShare,FBankCode,FBankAccount,FMatureDate,FSubAccType," +
                " FJoinCode2,FIssueDate,FIssuePrice,FFaceRate,FInsFrequency,FCreditLevel," +
                " FFaceValue,FInsStartDate,FInsEndDate,FInsCashDate,FIssueCorpCode,FODDayNum)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql = " select a.FSecurityCode as FJoinCode1,FExchangeCode,FMarketCode,FCatCode,FSubCatCode," +
                " FTradeCury as FCuryCode,FSectorCode,FTotalShare,FCurrentShare," +
                " null as FBankCode ,null as FBankAccount," +
                " null as FMatureDate,null as FSubAccType," +
                " 'Sec' as FJoinCode2," +
                " b.FIssueDate,b.FIssuePrice,b.FFaceRate,b.FInsFrequency," +
                " b.FCreditLevel,b.FFaceValue,b.FInsStartDate,b.FInsEndDate," +
                " b.FInsCashDate,a.FIssueCorpCode from " +
                pub.yssGetTableName("Tb_Para_Security") +
                " a left join (select * from " +
                pub.yssGetTableName("Tb_Para_FixInterest") + ") b" +
                " on a.FSecurityCode = b.FSecurityCode" +
                " where FCatCode in (" + operSql.sqlCodes(sSecTypes) + ") " +
                this.orderWhereSql("Tb_Para_Security", compIndex) +
                " and a.FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FJoinCode1"));
                pst.setString(2, rs.getString("FExchangeCode"));
                pst.setString(3, rs.getString("FMarketCode"));
                pst.setString(4, rs.getString("FCatCode"));
                pst.setString(5, rs.getString("FSubCatCode"));
                pst.setString(6, rs.getString("FCuryCode"));
                pst.setString(7, rs.getString("FSectorCode"));
                pst.setDouble(8, rs.getDouble("FTotalShare"));
                pst.setDouble(9, rs.getDouble("FCurrentShare"));
                pst.setString(10, rs.getString("FBankCode"));
                pst.setString(11, rs.getString("FBankAccount"));
                pst.setDate(12, rs.getDate("FMatureDate"));
                pst.setString(13, rs.getString("FSubAccType"));
                pst.setString(14, rs.getString("FJoinCode2"));
                pst.setDate(15, rs.getDate("FIssueDate"));
                pst.setDouble(16, rs.getDouble("FIssuePrice"));
                pst.setDouble(17, rs.getDouble("FFaceRate"));
                pst.setDouble(18, rs.getDouble("FInsFrequency"));
                pst.setString(19, rs.getString("FCreditLevel"));
                pst.setDouble(20, rs.getDouble("FFaceValue"));
                pst.setDate(21, rs.getDate("FInsStartDate"));
                pst.setDate(22, rs.getDate("FInsEndDate"));
                pst.setDate(23, rs.getDate("FInsCashDate"));
                pst.setString(24, rs.getString("FIssueCorpCode"));
                dODDayNum = 0;
                if (rs.getDate("FInsCashDate") != null) {
                    dODDayNum = YssFun.dateDiff(dDate, rs.getDate("FInsCashDate"));
                }
                pst.setDouble(25, dODDayNum);
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException("设置品种类属性证券数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void setPerSecSubCatAttrData(String sSecTypes,
                                        CompIndexBean compIndex) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        ResultSet rs = null;
        boolean bTrans = false;

        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;

            strSql = "insert into Tb_tmp_Comp_SubCat_" + pub.getUserCode() +
                " (FJoinCode1,FOrganCode,FCreditLevelCode,FLevel,FJoinCode2)" +
                " values(?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            strSql = "select a.FSecurityCode as FJoinCode1," +
                "b.FOrganCode, b.FCreditLevel,b.FLevel,'Sec' as FJoinCode2 from " +
                pub.yssGetTableName("Tb_Para_FixInterest") +
                " a join (select FSecurityCode,FOrganCode,FCreditLevel,FLevel from " +

                " ( select b11.FSecurityCode,FOrganCode,FCreditLevel from " +
                " (select FSecurityCode,FOrganCode,FCreditLevel," +
                dbl.sqlDate(YssFun.toDate("1900-12-31")) +
                " as FCLevelDate from " +
                pub.yssGetTableName("Tb_Para_CreditLevel") +
                " where FCheckState = 1 union " +
                " select FSecurityCode, FOrganCode, FCreditLevel, FCLevelDate from " +
                pub.yssGetTableName("Tb_Data_CreditLevel") +
                " where FCheckState = 1 ) b11 join ( " +
                " select FSecurityCode,max(FCLevelDate) from " +
                " (select FSecurityCode,FOrganCode,FCreditLevel," +
                dbl.sqlDate(YssFun.toDate("1900-12-31")) +
                " as FCLevelDate from " +
                pub.yssGetTableName("Tb_Para_CreditLevel") +
                " where FCheckState = 1 union " +
                " select FSecurityCode, FOrganCode, FCreditLevel, FCLevelDate from " +
                pub.yssGetTableName("Tb_Data_CreditLevel") +
                " where FCheckState = 1) " +
                " group by FSecurityCode) b12 on b11.FSecurityCode = b12.FSecurityCode ) b1 " +
//               " (select FSecurityCode,FOrganCode,FCreditLevel from " +
//               pub.yssGetTableName("Tb_Para_CreditLevel") +
//               " where FCheckState = 1) b1 " +

                " left join (select FCreditLevelCode,FLevel from " +
                pub.yssGetTableName("Tb_Para_CreditLevelDict") +
                " where FCheckState = 1) b2 on b1.FCreditLevel = b2.FCreditLevelCode) b " +
                " on a.FSecurityCode = b.FSecurityCode" +
                " where a.FCheckState = 1" +
                this.orderWhereSql("Tb_Para_Security", compIndex);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                pst.setString(1, rs.getString("FJoinCode1"));
                pst.setString(2, rs.getString("FOrganCode"));
                pst.setString(3, rs.getString("FCreditLevel"));
                pst.setDouble(4, rs.getDouble("FLevel"));
                pst.setString(5, rs.getString("FJoinCode2"));
                pst.executeUpdate();
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;

        } catch (Exception e) {
            throw new YssException("设置品种子类属性证券数据临时表SQL出错: \n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pst);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    protected String buildPerFilterSql(ArrayList alConds) throws YssException {
        String sWhereSql = "";
        CompIndexCondBean cond = null;
        YssCompAttrParam attrParam = null;
        YssCompAttrParam attrDenParam = null;
        IOperValue operValue = null;
        YssCompValueParam initParam = null;
        String[] sValueAry = null;
        String sValue = "";
        double dblOperValue = 0;
        boolean blTemp = true;
        try {
            if (alConds.size() > 0) {
                sWhereSql = " where ";
            }
            for (int i = 0; i < alConds.size(); i++) {
                //生成where语句
                blTemp = true;
                cond = (CompIndexCondBean) alConds.get(i);
                attrParam = cond.getAttrParam();
                if (attrParam.getRangeType().equalsIgnoreCase("per")) {
                    if (!cond.getSign().equalsIgnoreCase("with same")) {

                        if (cond.getCompType() == 2 || cond.getCompType() == 3) { //比例监控，比例范围监控
                            attrDenParam = cond.getAttrDenParam();
                            operValue = (IOperValue) pub.getOperDealCtx().
                                getBean(attrDenParam.getBeanId());
                            operValue.setYssPub(pub);
                            initParam = new YssCompValueParam();
                            initParam.setDDate(dDate);
                            initParam.setPortCode(portCode);
                            initParam.setCuryCode(pub.getPortBaseCury(portCode));// edit by lidaolong 2011.01.24;QDV4上海2010年12月10日02_A
                            operValue.init(initParam);
                            dblOperValue = operValue.getOperDoubleValue();
                            if (dblOperValue != 0) {
                                sWhereSql += " " + attrParam.getField() + "/" +
                                    dblOperValue + " " +
                                    cond.getSign() + " ";
                            } else {
                                sWhereSql += " 1 = 2 ";
                                blTemp = false;
                            }
                        } else {
                            sWhereSql += " " + attrParam.getField() + " " +
                                cond.getSign() + " ";
                        }

                        if (blTemp) {
                            if (cond.getSign().indexOf("in") >= 0) {
                                sValueAry = cond.getValue().split(",");
                                sValue = "";
                                for (int j = 0; j < sValueAry.length; j++) {
                                    sValue += "'" + sValueAry[j] + "'";
                                    if (j < sValueAry.length - 1) {
                                        sValue += ",";
                                    }
                                }
                                if (sValue.length() == 0) {
                                    sValue = "' '";
                                }
                                sWhereSql += " (" + sValue + ") ";
                            } else if (cond.getSign().equalsIgnoreCase("between")) {
                                sValueAry = cond.getValue().split(",");
                                if (sValueAry.length == 1) {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[0]) + " ";
                                } else {
                                    sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                        sValueAry[0]) + " and " +
                                        sqlValue(attrParam.getDataType(),
                                                 sValueAry[1]) + " ";
                                }
                            } else {
                                sWhereSql += " " + sqlValue(attrParam.getDataType(),
                                    cond.getValue()) + " ";
                            }
                        }

                        if (cond.getConRela().equalsIgnoreCase("end")) {
                            break;
                        }

                        if (i < alConds.size() - 1 &&
                            !cond.getConRela().equalsIgnoreCase("end")) {
                            sWhereSql += " " + cond.getConRela() + " ";
                        }
                    }
//               else {
//                  sWhereSql += " 1 = 1 ";
//
//                  if (cond.getConRela().equalsIgnoreCase("end")) {
//                     break;
//                  }
//
//                  if (i < alConds.size() - 1 &&
//                      !cond.getConRela().equalsIgnoreCase("end")) {
//                     sWhereSql += " " + cond.getConRela() + " ";
//                  }
//               }
                }
            }
            if (sWhereSql.equalsIgnoreCase(" where ")) {
                sWhereSql = "";
            }
            return sWhereSql;
        } catch (Exception e) {
            throw new YssException("设置监控条件SQL语句出错！ \n" + e.getMessage());
        }
    }

    protected String orderWhereSql(String tableName, CompIndexBean compIndex) throws
        YssException {
        String strWhereSql = "";
        String sCatCode = "";
        if (compIndex != null&&compIndex.getBCompCond().equalsIgnoreCase("null")) {
            if (tableName.equalsIgnoreCase("Tb_Data_SubTrade")) {
                if (this.invMgrCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("InvMgr") > -1) {
                    strWhereSql += " and FInvMgrCode = " +
                        dbl.sqlString(this.invMgrCode) + " ";
                }
                if (this.brokerCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Broker") > -1) {
                    strWhereSql += " and FBrokerCode = " +
                        dbl.sqlString(this.brokerCode) + " ";
                }
                if (this.securityCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Sec") > -1) {
                    strWhereSql += " and FSecurityCode = " +
                        dbl.sqlString(this.securityCode) + " ";
                }
                if (this.cashAccCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("CA") > -1) {
                    strWhereSql += " and FCashAccCode = " +
                        dbl.sqlString(this.cashAccCode) + " ";
                }
            } else if (tableName.equalsIgnoreCase("Tb_Stock_Security")) {
                if (this.invMgrCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("InvMgr") > -1) {
                    strWhereSql += " and FAnalysisCode1 = " +
                        dbl.sqlString(this.invMgrCode) + " ";
                }
                if (this.brokerCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Broker") > -1) {
                    strWhereSql += " and FAnalysisCode2 = " +
                        dbl.sqlString(this.brokerCode) + " ";
                }
                if (this.securityCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Sec") > -1) {
                    strWhereSql += " and FSecurityCode = " +
                        dbl.sqlString(this.securityCode) + " ";
                }
            } else if (tableName.equalsIgnoreCase("Tb_Cash_SubTransfer")) {
                if (this.invMgrCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("InvMgr") > -1) {
                    strWhereSql += " and FAnalysisCode1 = " +
                        dbl.sqlString(this.invMgrCode) + " ";
                }
                if (this.securityCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Cat") > -1) {
                    strWhereSql += " and FAnalysisCode2 = " +
                        dbl.sqlString(catCode) + " ";
                }
                if (this.cashAccCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("CA") > -1) {
                    strWhereSql += " and FCashAccCode = " +
                        dbl.sqlString(this.cashAccCode) + " ";
                }
//         if (this.brokerCode.length() != 0)
//         {
//            strWhereSql += " and FAnalysisCode2 = " +
//                  dbl.sqlString(this.brokerCode) + " ";
//         }
//         if (this.securityCode.length() != 0)
//         {
//            strWhereSql += " and FSecurityCode = " +
//                  dbl.sqlString(this.securityCode) + " ";
//         }
            } else if (tableName.equalsIgnoreCase("Tb_Stock_Cash")) {
                if (this.invMgrCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("InvMgr") > -1) {
                    strWhereSql += " and FAnalysisCode1 = " +
                        dbl.sqlString(this.invMgrCode) + " ";
                }
                if (this.securityCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Cat") > -1) {
                    strWhereSql += " and FAnalysisCode2 = " +
                        dbl.sqlString(catCode) + " ";
                }
                if (this.cashAccCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("CA") > -1) {
                    strWhereSql += " and FCashAccCode = " +
                        dbl.sqlString(this.cashAccCode) + " ";
                }
//         if (this.brokerCode.length() != 0)
//         {
//            strWhereSql += " and FAnalysisCode2 = " +
//                  dbl.sqlString(this.brokerCode) + " ";
//         }
//         if (this.securityCode.length() != 0)
//         {
//            strWhereSql += " and FSecurityCode = " +
//                  dbl.sqlString(this.securityCode) + " ";
//         }
            } else if (tableName.equalsIgnoreCase("Tb_Para_Security")) {
                if (this.securityCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Issue") > -1) {
                    SecurityBean sec = new SecurityBean();
                    sec.setYssPub(pub);
                    sec.setSecurityCode(this.securityCode);
                    sec.getSetting();
                    strWhereSql += " and FIssueCorpCode = " +
                        dbl.sqlString(sec.getIssueCorpCode()) + " ";
                }
            } else if (tableName.equalsIgnoreCase("Tb_Para_CashAccount")) {
                if (this.cashAccCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("Bank") > -1) {
                    CashAccountBean ca = new CashAccountBean();
                    ca.setYssPub(pub);
                    ca.setStrCashAcctCode(this.cashAccCode);
                    ca.getSetting();
                    if (ca.getStrBankCode().length() != 0) {
                        strWhereSql += " and FBankCode = " +
                            dbl.sqlString(ca.getStrBankCode()) + " ";
                    }
                }
                if (this.cashAccCode.length() != 0 &&
                    compIndex.getBCompCond().indexOf("CA") > -1) {
                    strWhereSql += " and FCashAccCode = " +
                        dbl.sqlString(this.cashAccCode) + " ";
                }

            }
        }
        return strWhereSql;
    }

    protected String sqlValue(int iDataType, String sValue) {
        String reStr = "' '";
        if (iDataType == 0) {
            reStr = dbl.sqlString(sValue);
        } else if (iDataType == 1) {
            reStr = sValue;
        } else if (iDataType == 2) {
            reStr = sValue.replaceAll(",", "");
        } else if (iDataType == 3) {
            reStr = dbl.sqlDate(sValue);
        } else if (iDataType == 4) {
            reStr = sValue;
        }

        return reStr;
    }
}
