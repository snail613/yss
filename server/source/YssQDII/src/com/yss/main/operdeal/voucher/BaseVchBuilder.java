package com.yss.main.operdeal.voucher;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.operdeal.*;
import com.yss.main.voucher.*;
import com.yss.manager.*;
import com.yss.pojo.cache.*;
import com.yss.pojo.param.comp.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class BaseVchBuilder
    extends BaseBean {
    private String portCodes = "";
    private java.util.Date beginDate;
    private java.util.Date endDate;
    private String vchTypes = "";
    private String params = "";
    ArrayList alRepParam = new ArrayList();

    public BaseVchBuilder() {
    }

    public void init(String sPortCodes, java.util.Date beginDate,
                     java.util.Date endDate, String sVchTypes) throws
        YssException {
        this.portCodes = sPortCodes;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.vchTypes = sVchTypes; //凭证属性

        params = "1\r" + YssFun.formatDate(beginDate)
            + "\n2\r" + YssFun.formatDate(endDate)
            + "\n3\r" + sPortCodes;
    }

    //生成凭证的入口
    public void parse() throws YssException {
        String[] sRepCtlParamAry = null;
        YssCommonRepCtl repParam = null;
        String[] tmpAry = null;
        StringBuffer buf = new StringBuffer();
        String reStr = "";
        String repStr = "";
        try {
            sRepCtlParamAry = params.split("\n");
            for (int i = 0; i < sRepCtlParamAry.length; i++) {
                repParam = new YssCommonRepCtl();
                repParam.parseRowStr(sRepCtlParamAry[i]);
                repStr = repParam.getCtlValue();
                if (i == 2) {
                    tmpAry = repStr.split(",");
                    if (tmpAry.length > 1) {
                        for (int j = 0; j < tmpAry.length; j++) {
                            buf.append("'" + tmpAry[j] + "'").append(",");
                        }
                        if (buf.length() > 1) {
                            reStr = buf.toString().substring(0,
                                buf.toString().length() - 1);
                        }
                    } else {
                        buf.append("'" + tmpAry[0] + "'");
                        reStr = buf.toString();
                    }
                    repParam.setCtlValue(reStr);
                }
                alRepParam.add(repParam);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }

    public void doVchBuilder() throws YssException {
        //1.根据需要产生的凭证类型从凭证模板设置表中取出记录
        parse();
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsDs = null;
        HashMap hmDsFieldType = null;
        VoucherAdmin vchAdmin = new VoucherAdmin();
        String[] portAry = null;
        try {
            vchAdmin.setYssPub(pub);
            vchAdmin.deleteVoucher(this.vchTypes, this.beginDate, this.endDate,
                                   this.portCodes);
//         vchAdmin.deleteVoucherWithNoCheck(this.vchTypes, this.beginDate, this.endDate,
//                                this.portCodes);
            portAry = portCodes.split(",");
            for (int i = 0; i < portAry.length; i++) {
                strSql =
                    " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                    " where FAttrCode in (" + operSql.sqlCodes(vchTypes) +
                    ") and FCheckState = 1  order by fvchtplcode"; //做凭证时按照凭证编号的顺序排一下序   廖睿  20071220
                rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (rs.next()) {

                    strSql = buildVchDsSql(rs.getString("FDsCode"), portAry[i]);
                    rsDs = dbl.openResultSet(strSql);
                    hmDsFieldType = dbFun.getFieldsType(rsDs);
                    if (rs.getString("FMode").equalsIgnoreCase("Single")) { //单行取数模式
                        doSingleVch(rs.getString("FVchTplCode"), rsDs, hmDsFieldType);
                    } else if (rs.getString("FMode").equalsIgnoreCase("Multi")) { //多行取数模式
                        doMultiVch(rs.getString("FVchTplCode"), strSql, hmDsFieldType);
                    }
                    dbl.closeResultSetFinal(rsDs);
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected void doSingleVch(String sVchTplCode, ResultSet rsDs,
                               HashMap hmDsFieldType) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        ResultSet rsTpl = null;
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        boolean bTrans = false;
        VchDataBean vchData = null;
        VchDataEntityBean vchDataEntity = null;
        VoucherAdmin vchAdmin = new VoucherAdmin();
        ArrayList subAddList = null;
//      double dBaseRate = 1;
//      double dBaseRateSrc = 1;
//      double dPortRate = 1;

        try {

            BaseOperDeal operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);

            YssFinance fc = new YssFinance();
            fc.setYssPub(pub);

            vchAdmin.setYssPub(pub);
//         strSql = " select a.*,b.*,c.FInitRate from " +
//               pub.yssGetTableName("Tb_Vch_VchTpl") + " a" +
//               " join(select distinct FLinkCode from " +
//               pub.yssGetTableName("Tb_Vch_PortSetLink") +
//               " where FCheckState=1 and FPortCode in (" +
//               operSql.sqlCodes(this.portCodes) + ")" +
//               ")b on b.FLinkCode=a.FLinkCode" +
//               " left join(select FCuryCode,FInitRate from " +
//               pub.yssGetTableName("Tb_Para_Currency") +
//               " where FCheckState=1)c on c.FCuryCode=a.FCuryCode" +
//               " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
//               " and FCheckState =1 order by FVchTplCode";
//         rsTpl = dbl.openResultSet_antReadonly(strSql);

            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FVchTplCode = " +
                dbl.sqlString(sVchTplCode);
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
                    while (rs.next()) {
                        vchDataEntity = new VchDataEntityBean();
                        vchDataEntity.setYssPub(pub);
                        vchDataEntity.setEntityCode(rs.getString("FEntityCode"));
                        vchDataEntity.setSubjectCode(
                            this.getEntitySubject(rs.getString("FVchTplCode"),
                                                  rs.getString("FEntityCode"),
                                                  rsDs, hmDsFieldType));
                        vchDataEntity.setResume(
                            this.getEntityResume(rs.getString(
                                "FVchTplCode"),
                                                 rs.getString("FEntityCode"),
                                                 rsDs, hmDsFieldType));
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
                                rsDs, hmDsFieldType)
                            );
                        subAddList.add(vchDataEntity);
                    }
                    rs.beforeFirst();
                    vchData.setDataEntity(subAddList);
                    setPortSet(rsTpl.getString("FLinkCode"), vchData, vchAdmin);
//               vchAdmin.addList(vchData);
                }
                rsTpl.beforeFirst();
            }
            adjustTail(vchAdmin.getAddList());
            vchAdmin.insert();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rsTpl);
            dbl.closeResultSetFinal(rsDs);
        }
    }

    //调整凭证的本位币尾差
    //处理方式：分别汇总借方金额和贷方金额，再用借方金额-贷方金额得到尾差，再把尾差放到第一笔贷方分录上  胡昆 20070918
    private void adjustTail(ArrayList vchList) throws YssException {
        VchDataBean vchData = null;
        VchDataEntityBean vchDataEntity = null;
        VchDataEntityBean vchDataEnyTmp = null;
        double jje = 0, jbje = 0; //借原币金额，本位币金额  胡昆 20070920
        double dje = 0, dbje = 0; //贷原币金额，本位币金额  胡昆 20070920
        double dTail = 0, dbTail = 0; //原币尾差，本位币尾差  胡昆 20070920
        for (int i = 0; i < vchList.size(); i++) {
            vchData = (VchDataBean) vchList.get(i);
            dTail = 0;
            jbje = 0;
            dbje = 0;
            dbTail = 0;
            jje = 0;
            dje = 0;
            vchDataEnyTmp = null;
            for (int j = 0; j < vchData.getDataEntity().size(); j++) {
                vchDataEntity = (VchDataEntityBean) vchData.getDataEntity().get(j);
                //-------------------以下累计借方和贷方的总额,其中不累计计算方式为“轧差”的分录   胡昆 20070920
                if (vchDataEntity.getDcWay().equalsIgnoreCase("0")) { //借
                    if (vchDataEntity.getCalcWay().equalsIgnoreCase("Common")) { //当分录的计算方式为“普通”时累计原币和本位币  胡昆 20070920
                        jje += vchDataEntity.getBal();
                        jbje += vchDataEntity.getSetBal();
                    } else if (vchDataEntity.getCalcWay().equalsIgnoreCase(
                        "NettingSet")) { //当分录的计算方式为“轧差本位币”时累计只原币  胡昆 20070920
                        jje += vchDataEntity.getBal();
                    }
                    if (vchDataEntity.getCalcWay().equalsIgnoreCase("Netting") ||
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingSet")) { //记录下需要做轧差的分录，在最后设值  胡昆 20070920
                        vchDataEnyTmp = vchDataEntity;
                    }
                } else if (vchDataEntity.getDcWay().equalsIgnoreCase("1")) { //贷
                    if (vchDataEntity.getCalcWay().equalsIgnoreCase("Common")) { //当分录的计算方式为“普通”时累计原币和本位币  胡昆 20070920
                        dje += vchDataEntity.getBal();
                        dbje += vchDataEntity.getSetBal();
                    } else if (vchDataEntity.getCalcWay().equalsIgnoreCase(
                        "NettingSet")) { //当分录的计算方式为“轧差本位币”时累计只原币  胡昆 20070920
                        dje += vchDataEntity.getBal();
                    }
                    if (vchDataEntity.getCalcWay().equalsIgnoreCase("Netting") ||
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingSet")) { //记录下需要做轧差的分录，在最后设值  胡昆 20070920
                        vchDataEnyTmp = vchDataEntity;
                    }
                }
                //------------------
            }

            dTail = YssD.sub(YssD.round(jje, 2), YssD.round(dje, 2)); //借方减贷方得到轧差原币金额  胡昆 20070920
            dbTail = YssD.sub(YssD.round(jbje, 2), YssD.round(dbje, 2)); //借方减贷方得到轧差本位币金额  胡昆 20070920
            if ( (dTail != 0 || dbTail != 0) && vchDataEnyTmp != null) { //本位币轧差有金额时，也应该进入到这里面fazmm20070928
                if (vchDataEnyTmp.getDcWay().equalsIgnoreCase("0")) { //借方  轧差科目为借方时要取轧差金额的反数
                    if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase("Netting")) {
                        vchDataEnyTmp.setBal( -dTail);
                        vchDataEnyTmp.setSetBal( -dbTail);
                    } else if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase(
                        "NettingSet")) {
                        vchDataEnyTmp.setSetBal( -dbTail);
                    }
                }
                if (vchDataEnyTmp.getDcWay().equalsIgnoreCase("1")) { //贷方
                    if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase("Netting")) {
                        vchDataEnyTmp.setBal(dTail);
                        vchDataEnyTmp.setSetBal(dbTail);
                    } else if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase(
                        "NettingSet")) {
                        vchDataEnyTmp.setSetBal(dbTail);
                    }
                }
            }
        }
    }

    private void setPortSet(String sLinkCode, VchDataBean vchData,
                            VoucherAdmin vchAdmin) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        VchDataBean vchClone = null;

        double dBaseRate = 1;
        double dBaseRateSrc = 1;
        double dPortRate = 1;

        String sAccCury = "";
        VchDataEntityBean vchDataEntity = null;

        YssFinance cw = new YssFinance();
        double dRate = 1;
        try {
            cw.setYssPub(pub);
            strSql = " select distinct FPortCode, trim(to_char(fsetcode,'000'))  as FBookSetCode,p.fportcury as FCuryCode " +
    		" from lsetlist l join " + pub.yssGetTableName("tb_para_portfolio") + " p on l.fsetid = p.fassetcode ";
            //modified by yeshenghong 20130428 BUG7486 
//            strSql = "select a.*,b.FCuryCode from " +
//                pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                " a left join (select * from " +
//                pub.yssGetTableName("tb_vch_bookset") +
//                " where FCheckState = 1) b on a.Fbooksetcode = b.FBookSetCode" +
//                " where FLinkCode = " + dbl.sqlString(sLinkCode) +
//                " and a.FCheckState = 1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                vchClone = (VchDataBean) vchData.clone();
                vchClone.setPortCode(rs.getString("FPortCode"));
                vchClone.setBookSetCode(rs.getString("FBookSetCode"));
                dBaseRate = this.getSettingOper().getCuryRate(vchData.getVchDate(),
                    vchData.getSrcCuryCode(), vchClone.getPortCode(),
                    YssOperCons.YSS_RATE_BASE); //原币汇率

                vchClone.setCuryCode(rs.getString("FCuryCode"));
                dPortRate = this.getSettingOper().getCuryRate(vchData.getVchDate(),
                    vchClone.getCuryCode(), vchClone.getPortCode(),
                    //需注意南方（南方的基础货币和组合货币均为人民币），应该是不影响南方的fazmm20070927
                    //       YssOperCons.YSS_RATE_BASE); //本位币汇率
                    YssOperCons.YSS_RATE_PORT); //组合货币是PORT，杨文奇，0925
                vchClone.setCuryRate(YssFun.roundIt(YssD.div(dBaseRate, dPortRate),
                    15)); //hxqdii

                for (int i = 0; i < vchClone.getDataEntity().size(); i++) {
                    vchDataEntity = (VchDataEntityBean) vchClone.getDataEntity().get(
                        i);
//               dBaseRateSrc = this.getSettingOper().getCuryRate(vchData.
//                     getVchDate(),
//                     cw.getCWAccountCury(vchDataEntity.getSubjectCode(),
//                                         vchData.getVchDate(),
//                                         vchClone.getPortCode()),
//                     vchData.getPortCode(),
//                     YssOperCons.YSS_RATE_BASE); //原币汇率
//               dRate = YssD.round(YssD.div(dBaseRateSrc, dBaseRate),8);

                    sAccCury = cw.getCWAccountCury(vchDataEntity.getSubjectCode(),
                        vchData.getVchDate(),
                        vchClone.getPortCode());

//               vchDataEntity.setBal(this.getSettingOper().converMoney(
//                     vchClone.getSrcCuryCode(), sAccCury, vchDataEntity.getBal(),
//                     vchData.getVchDate(), vchClone.getPortCode()));

                    if (!vchDataEntity.isBSetBal()) { //没有设置本位币，则计算本位币金额
//                  vchDataEntity.setSetBal(YssD.round(YssD.mul(vchDataEntity.
//                        getBal(),
//                        dRate), 2));
                        vchDataEntity.setSetBal(this.getSettingOper().converMoney(
                            sAccCury, vchClone.getCuryCode(), vchDataEntity.getBal(),
                            vchData.getVchDate(), vchClone.getPortCode()));
                    }
                }

                vchAdmin.addList(vchClone);
            }
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    private String getRsValue(ResultSet rsDs, String sFieldName,
                              HashMap hmDsFieldType) throws YssException {
        String sResult = "";
        try {
            if ( ( (String) hmDsFieldType.get(sFieldName)).equalsIgnoreCase("DATE")) {
                sResult = YssFun.formatDate(rsDs.getString(sFieldName));
            } else {
                sResult = rsDs.getString(sFieldName);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    protected String getAssistant(String sVchTplCode,
                                  String sEntityCode,
                                  ResultSet rsDs, HashMap hmDsFieldType) throws
        YssException

    {
        ResultSet rsRes = null;
        String strSql = "";
        String tmpValue = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql =
                " select * from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(sEntityCode) +
                " order by FOrderNum ";
            rsRes = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rsRes.next()) {
                if (rsRes.getString("FValueType").equalsIgnoreCase("0")) { //动态取值
                    if (rsRes.getString("FAssistantDict") != null &&
                        rsRes.getString("FAssistantDict").trim().length() != 0 &&
                        !rsRes.getString("FAssistantDict").equalsIgnoreCase("null")) {
                        tmpValue = getSubjectType(rsRes.getString("FAssistantDict").
                                                  trim(),
                                                  rsDs.
                                                  getString(rsRes.getString(
                            "FAssistantField").trim()));
                    } else {
                        tmpValue = this.getRsValue(rsDs,
                            rsRes.getString("FAssistantField").
                            trim(),
                            hmDsFieldType);
                    }
                    buf.append(tmpValue);
                } else {
                    tmpValue = rsRes.getString("FAssistantConent");
                    buf.append(tmpValue);
                }
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rsRes);
        }
    }

    /**
     * getEntityResume : 获取分录摘要
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
    protected String getEntityResume(String sVchTplCode,
                                     String sEntityCode,
                                     ResultSet rsDs, HashMap hmDsFieldType) throws
        YssException {
        ResultSet rsRes = null;
        String strSql = "";
        String tmpValue = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql =
                " select * from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(sEntityCode) +
                " order by FOrderNum ";
            rsRes = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rsRes.next()) {
                if (rsRes.getString("FValueType").equalsIgnoreCase("0")) { //动态取值
                    if (rsRes.getString("FResumeDict") != null &&
                        rsRes.getString("FResumeDict").trim().length() != 0 &&
                        !rsRes.getString("FResumeDict").equalsIgnoreCase("null")) {
                        tmpValue = getSubjectType(rsRes.getString("FResumeDict").trim() +
                                                  "",
                                                  rsDs.
                                                  getString(rsRes.getString(
                            "FResumeField").trim()));
                    } else {
                        tmpValue = this.getRsValue(rsDs,
                            rsRes.getString("FResumeField").
                            trim(),
                            hmDsFieldType);
                    }
                    buf.append(tmpValue);
                } else {
                    tmpValue = rsRes.getString("FResumeConent");
                    buf.append(tmpValue);
                }
            }
            if (buf.toString().trim().length() == 0) {
                buf.append(" ");
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rsRes);
        }
    }

    /**
     * getEntitySubject : 获取分录科目
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
    protected String getEntitySubject(String sVchTplCode, String sEntityCode,
                                      ResultSet rsDs, HashMap hmDsFieldType) throws
        YssException {
        StringBuffer buf = new StringBuffer();
        String strSql = "";
        String tmpValue = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(sEntityCode) +
                " order by FOrderNum ";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (rs.getString("FValueType").equalsIgnoreCase("0")) { //动态取值
                    if (rs.getString("FSubjectDict") != null &&
                        rs.getString("FSubjectDict").trim().length() != 0 &&
                        !rs.getString("FSubjectDict").equalsIgnoreCase("null")) {
                        tmpValue = getSubjectType(rs.getString("FSubjectDict").trim(),
                                                  rsDs.
                                                  getString(rs.getString(
                            "FSubjectField").trim()));
                    } else {
                        tmpValue = this.getRsValue(rsDs,
                            rs.getString("FSubjectField").trim(),
                            hmDsFieldType);

                    }
                    buf.append(tmpValue);
                } else {
                    tmpValue = rs.getString("FSubjectConent").trim();
                    buf.append(tmpValue);
                }
            }
            if (buf.toString().trim().length() == 0) {
                buf.append(" ");
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getSubjectType(String dictCode, String indCode) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql =
                "select * from " +
                pub.yssGetTableName("Tb_Vch_Dict") +
                " where FDictCode=" + dbl.sqlString(dictCode) +
                " and FIndCode=" + dbl.sqlString(indCode);
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                buf.append(rs.getString("FCnvConent"));
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getEntityMoney : 获取分录金额
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param sType String ： 类型
     * @param rsDs ResultSet ： 数据源记录集
     */
    //把获取金额和获取数量合并成一个方法，用类型来区分开  胡昆 20070910
    protected double getEntityMA(String sVchTplCode, String sEntityCode,
                                 ResultSet rsDs, String sType,
                                 VchDataEntityBean vchDataEntity) throws
        YssException {
        double money = 0.0;
        ResultSet rs = null;
        String strSql = "";
        String sign = "";
        try {
            if (vchDataEntity != null) {
                vchDataEntity.setBSetBal(false);
            }
            strSql = "select a.*,b.FVocName as FOperSignValue from " +
                pub.yssGetTableName("Tb_Vch_EntityMA") + " a " +
                " left join Tb_Fun_Vocabulary b on a.FOperSign = b.FVocCode and b.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_OperSign) +
                " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
                " and FEntityCode=" + dbl.sqlString(sEntityCode) +
                " and FType=" + dbl.sqlString(sType);
            //2008.01.22 修改 蒋锦 原来使用dbl.openResultSet() 当使用rs.isFirst()方法时报错
            rs = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (vchDataEntity != null) {
                    vchDataEntity.setBSetBal(true);
                }
                if (rs.getString("FValueType").equalsIgnoreCase("0")) {
                    //以后会用到  tmpValue = getSubjectType(rs.getString("FMADict"),
                    //                rs.getString("FMAField"));
                    if (rs.isFirst()) {
                        money = rsDs.getDouble(rs.getString("FMAField"));
                        sign = rs.getString("FOperSignValue");
                    } else {
                        if (sign.equals("+")) {
                            money = money + rsDs.getDouble(rs.getString("FMAField"));
                            sign = rs.getString("FOperSignValue");
                        } else if (sign.equals("-")) {
                            money = money - rsDs.getDouble(rs.getString("FMAField"));
                            sign = rs.getString("FOperSignValue");
                        } else if (sign.equals("*")) {
                            money = money * rsDs.getDouble(rs.getString("FMAField"));
                            sign = rs.getString("FOperSignValue");
//                     money = YssD.round(money, 2);
                        } else if (sign.equals("/")) {
                            if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
                                money = money / rsDs.getDouble(rs.getString("FMAField"));
//                        money = YssD.round(money, 2);
                            }
                            sign = rs.getString("FOperSignValue");
                        }
                    }
                } else {
                    if (rs.isFirst()) {
                        if (rs.getString("FMAField") != null &&
                            !rs.getString("FMAField").equalsIgnoreCase("null") &&
                            rs.getString("FMAField").trim().length() > 0) { //当导入时可能将null转成了空格了，所以在这里判断一下 liyu 修改 1030
                            money = rsDs.getDouble(rs.getString("FMAField"));
                        } else {
                            money = rs.getDouble("FMAConent");
                        }
                        sign = rs.getString("FOperSignValue");
                    } else {
                        if (sign.equals("+")) {
//                     money = money + rs.getDouble("FMAConent");
                            if (rs.getString("FMAField") != null) {
                                money += rsDs.getDouble(rs.getString("FMAField"));
                            } else {
                                money += rs.getDouble("FMAConent");
                            }
                            sign = rs.getString("FOperSignValue");
                        } else if (sign.equals("-")) {
//                     money = money - rs.getDouble("FMAConent");
                            if (rs.getString("FMAField") != null) {
                                money -= rsDs.getDouble(rs.getString("FMAField"));
                            } else {
                                money -= rs.getDouble("FMAConent");
                            }
                            sign = rs.getString("FOperSignValue");
                        } else if (sign.equals("*")) {
//                     money = money * rs.getDouble("FMAConent");
                            if (rs.getString("FMAField") != null &&
                                !rs.getString("FMAField").equalsIgnoreCase("null")) {
                                money *= rsDs.getDouble(rs.getString("FMAField"));
                            } else {
                                money *= rs.getDouble("FMAConent");
                            }
//                     money = YssD.round(money, 2);
                            sign = rs.getString("FOperSignValue");
                        }

                        else if (sign.equals("/")) {
//                     if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                        money = money / rs.getDouble("FMAConent");
//                     }
                            if (rs.getString("FMAField") != null) {
                                if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
                                    money /= rsDs.getDouble(rs.getString("FMAField"));
                                }
                            } else {
                                if (rs.getDouble("FMAConent") != 0) {
                                    money /= rs.getDouble("FMAConent");
                                }
                            }
//                     money = YssD.round(money, 2);
                            sign = rs.getString("FOperSignValue");
                        }
                    }
                }
            }
            if (!sType.equalsIgnoreCase("Amount")) {
                money = YssD.round(money, 2);
            }
            return money;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getEntityMoney : 获取分录金额
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
//   protected double getEntityMoney(String sVchTplCode, String sEntityCode,
//                                   ResultSet rsDs) throws YssException {
//      double money = 0.0;
//      ResultSet rs = null;
//      String strSql = "";
//      String sign = "";
//      try {
//         strSql = "select a.*,b.FVocName as FOperSignValue from " +
//               pub.yssGetTableName("Tb_Vch_EntityMA") + " a " +
//               " left join Tb_Fun_Vocabulary b on a.FOperSign = b.FVocCode and b.FVocTypeCode = " +
//               dbl.sqlString(YssCons.YSS_OperSign) +
//               " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
//               " and FEntityCode=" + dbl.sqlString(sEntityCode) +
//               " and FType=" + dbl.sqlString("Money");
//         rs = dbl.openResultSet(strSql);
//         while (rs.next()) {
//
//            if (rs.getString("FValueType").equalsIgnoreCase("0")) {
//               //以后会用到  tmpValue = getSubjectType(rs.getString("FMADict"),
//               //                rs.getString("FMAField"));
//               if (rs.isFirst()) {
//                  money = rsDs.getDouble(rs.getString("FMAField"));
//                  sign = rs.getString("FOperSignValue");
//               }
//               else {
//                  if (sign.equals("+")) {
//                     money = money + rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSignValue");
//                  }
//                  else if (sign.equals("-")) {
//                     money = money - rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSignValue");
//                  }
//                  else if (sign.equals("*")) {
//                     money = money * rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSignValue");
//                  }
//                  else if (sign.equals("/")) {
//                     if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                        money = money / rsDs.getDouble(rs.getString("FMAField"));
//                     }
//                     sign = rs.getString("FOperSignValue");
//                  }
//               }
//            }
//            else {
//               if (rs.isFirst()) {
//                  if (rs.getString("FMAField") != null) {
//                     money = rsDs.getDouble(rs.getString("FMAField"));
//                  }
//                  else {
//                     money = rs.getDouble("FMAConent");
//                  }
//                  sign = rs.getString("FOperSignValue");
//               }
//               else {
//                  if (sign.equals("+")) {
//                     money = money + rs.getDouble("FMAConent");
//                     if (rs.getString("FMAField") != null) {
//                        money += rsDs.getDouble(rs.getString("FMAField"));
//                     }
//                     else {
//                        money += rs.getDouble("FMAConent");
//                     }
//                     sign = rs.getString("FOperSignValue");
//                  }
//                  else if (sign.equals("-")) {
//                     money = money - rs.getDouble("FMAConent");
//                     if (rs.getString("FMAField") != null) {
//                        money -= rsDs.getDouble(rs.getString("FMAField"));
//                     }
//                     else {
//                        money -= rs.getDouble("FMAConent");
//                     }
//                     sign = rs.getString("FOperSignValue");
//                  }
//                  else if (sign.equals("*")) {
//                     money = money * rs.getDouble("FMAConent");
//                     if (rs.getString("FMAField") != null) {
//                        money *= rsDs.getDouble(rs.getString("FMAField"));
//                     }
//                     else {
//                        money *= rs.getDouble("FMAConent");
//                     }
//                     sign = rs.getString("FOperSignValue");
//                  }
//
//                  else if (sign.equals("/")) {
//                     if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                        money = money / rs.getDouble("FMAConent");
//                     }
//                     if (rs.getString("FMAField") != null) {
//                        if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                           money /= rsDs.getDouble(rs.getString("FMAField"));
//                        }
//                     }
//                     else {
//                        if (rs.getDouble("FMAConent") != 0) {
//                           money /= rs.getDouble("FMAConent");
//                        }
//                     }
//                     sign = rs.getString("FOperSignValue");
//                  }
//               }
//            }
//         }
//      }
//      catch (Exception e) {
//         throw new YssException(e.getMessage(), e);
//      }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
//      return money;
//   }

    /**
     * getEntityAmount : 获取分录数量
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
//   protected double getEntityAmount(String sVchTplCode, String sEntityCode,
//                                    ResultSet rsDs) throws YssException {
//      ResultSet rs = null;
//      String strSql = "";
//      double amount = 0.0;
//      String sign = "";
//
//      try {
//         strSql = "select * from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
//               " where FVchTplCode=" + dbl.sqlString(sVchTplCode) +
//               " and FEntityCode=" + dbl.sqlString(sEntityCode) +
//               " and FType=" + dbl.sqlString("Amount");
//         rs = dbl.openResultSet(strSql);
//         while (rs.next()) {
//            if (rs.getString("FValueType").equalsIgnoreCase("0")) {
//               //以后会用到  tmpValue = getSubjectType(rs.getString("FMADict"),
//               //                         rs.getString("FMAField"));
//               if (rs.isFirst()) {
//                  amount = rsDs.getDouble(rs.getString("FMAField"));
//                  sign = rs.getString("FOperSign");
//               }
//               else {
//                  if (sign.equals("+")) {
//                     amount = amount + rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSign");
//                  }
//                  else if (sign.equals("-")) {
//                     amount = amount - rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSign");
//                  }
//                  else if (sign.equals("*")) {
//                     amount = amount * rsDs.getDouble(rs.getString("FMAField"));
//                     sign = rs.getString("FOperSign");
//                  }
//                  else if (sign.equals("/")) {
//                     if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                        amount = amount /
//                              rsDs.getDouble(rs.getString("FMAField"));
//                     }
//                     sign = rs.getString("FOperSign");
//                  }
//               }
//            }
//            else {
//               if (rs.isFirst()) {
//                  amount = rs.getDouble("FMAConent");
//                  sign = rs.getString("FOperSign");
//               }
//               if (sign.equals("+")) {
//                  amount = amount + rs.getDouble("FMAConent");
//                  sign = rs.getString("FOperSign");
//               }
//               else if (sign.equals("-")) {
//                  amount = amount - rs.getDouble("FMAConent");
//                  sign = rs.getString("FOperSign");
//               }
//               else if (sign.equals("*")) {
//                  amount = amount * rs.getDouble("FMAConent");
//                  sign = rs.getString("FOperSign");
//               }
//
//               else if (sign.equals("/")) {
//                  if (rsDs.getDouble(rs.getString("FMAField")) != 0) {
//                     amount = amount / rs.getDouble("FMAConent");
//                  }
//                  sign = rs.getString("FOperSign");
//               }
//            }
//         }
//      }
//      catch (Exception e) {
//         throw new YssException(e.getMessage(), e);
//      }
//      finally {
//         dbl.closeResultSetFinal(rs);
//      }
//      return amount;
//
//   }

    /**
     * buildVchDsSql : 根据模板中的数据源代码到凭证数据源表中获取SQL语句，关键是把数据源中对应的参数替换掉
     * @param sDsCode String ： 数据源代码
     * @param sPortCode String ： 组合代码
     */
    protected String buildVchDsSql(String sDsCode, String sPortCode) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String dataSource = "";
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String sSelectFields = "";
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDsCode=" + dbl.sqlString(sDsCode) +
                " and FCheckState = 1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                if (dbl.getDBType() == YssCons.DB_ORA) {
                    dataSource = dbl.clobStrValue(rs.getClob("FDataSource")).
                        replaceAll(
                            "\t", "   ");
                } else if (dbl.getDBType() == YssCons.DB_DB2) {
                    dataSource = rs.getString("FDataSource").replaceAll("\t", "   ");
                }
            }
            dbl.closeResultSetFinal(rs);
            /*如果通过凭证数据源设置的字段设置中获取字段的话，现在设置的字段存在没有该字段但是还放在数据表中的。
                      //在数据源字段设置中应该增加字段的判断，而不是随意新增的。fazmm20071019
             strSql = "select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                  " where FVchDsCode = " + dbl.sqlString(sDsCode) +
                  " and FCheckState = 1";
                      rs = dbl.openResultSet(strSql);
                      while (rs.next()){
               buf.append(rs.getString("FFieldName")).append(" as ").append(rs.getString("FAliasName")).append(",");
                      }
                      if (buf.toString().length()>0){
               sSelectFields = buf.toString().substring(0,buf.toString().length()-1);//把最后一个","去掉
                      }
             dataSource = "select " + sSelectFields + " from (" + dataSource + ")";*/

            return buildSqlTwo(dataSource, sPortCode);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildSqlTwo(String sDs, String sPortCode) throws
        YssException {
        YssCommonRepCtl repParam = null;
        String sInd = "", sInd2 = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        boolean bFlag = false;
        YssFinance cw = null;
        for (int i = 0; i < alRepParam.size(); i++) {
            repParam = (YssCommonRepCtl) alRepParam.get(i);
            sInd = "<" + (repParam.getCtlIndex()) + ">";
            iPos = sDs.indexOf(sInd);
            if (iPos <= 0) {
                sInd = " < " + (repParam.getCtlIndex()) + " >";
                iPos = sDs.indexOf(sInd);
            }
            if (iPos > 1) {
                sDataType = sDs.substring(iPos - 1, iPos);
                if (repParam.getCtlValue() == null) {
                    continue;
                }
                if (sDataType.equalsIgnoreCase("S")) {
                    //   sSqlValue = dbl.sqlString(repParam.getCtlValue());
                    sSqlValue = repParam.getCtlValue();
                } else if (sDataType.equalsIgnoreCase("I")) {
                    sSqlValue = repParam.getCtlValue();
                } else if (sDataType.equalsIgnoreCase("D")) {
                    //转换成日期
                    sSqlValue = dbl.sqlDate(YssFun.formatDate(repParam.getCtlValue()));
                } else if (sDataType.equalsIgnoreCase("N")) {
                    //转换代码，例如 001,002转换成'001','002'
                    sSqlValue = repParam.getCtlValue();
                }
                sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
            }
        }
        sDs = wipeSqlCond(sDs);
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }

        if (sDs.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
            sDs = sDs.replaceAll("<Year>",
                                 YssFun.formatDate( ( (YssCommonRepCtl) alRepParam.
                get(1)).getCtlValue(), "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
            cw = new YssFinance();
            cw.setYssPub(pub);
            sDs = sDs.replaceAll("<Set>", cw.getCWSetCode(sPortCode));
        }
//---------------------------------------------------------------------------------
        if (sDs.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群 sj edit 20080306
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
//---------------------------------------------------------------------------------
        sDs = sDs.replaceAll("~Base", "base");
        return sDs;
    }

    protected String wipeSqlCond(String strSql) throws YssException {
        int iBIndex = -1;
        int iEIndex = -1;
        int iLen = 0;
        int iBPos = 0;
        int iEPos = 0;
        String sCond = "";
        boolean bFlag = false;
        String sTmp = "";
        char[] chrAry = strSql.toCharArray();
        while (strSql.indexOf("[", iBPos) > 0) {
            iBPos = strSql.indexOf("[", iBPos);
            iEPos = strSql.indexOf("]", iBPos);
            sTmp = strSql.substring(iBPos, iEPos);
            if (sTmp.indexOf("S<") > -1 || sTmp.indexOf("S <") > -1 ||
                sTmp.indexOf("D<") > -1 ||
                sTmp.indexOf("D <") > -1 || sTmp.indexOf("N<") > -1 ||
                sTmp.indexOf("N <") > -1 ||
                sTmp.indexOf("I<") > -1 || sTmp.indexOf("I <") > -1) {
                sCond = YssFun.getStrParams(sTmp);
            }
            if (sCond.length() > 0) {
                for (int i = iBPos; i <= iEPos; i++) {
                    chrAry[i] = ' ';
                }
                sCond = "";
            } else {
                chrAry[iBPos] = ' ';
                chrAry[iEPos] = ' ';
            }
            strSql = String.valueOf(chrAry);

        }
        return strSql;
    }

    /**
     * addEntityCond : 加入凭证分录中的条件
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param sDsSql String ： 数据源SQL
     */
    protected String addEntityCond(String sVchTplCode, String sEntityCode,
                                   String sDsSql) throws YssException {
        ResultSet rs = null;
        String strSql = "";

        try {

        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return "";
    }

    private String sqlValue(HashMap hmDsFieldType, String sField, String sValue) {
        String sResult = "";
        if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("VARCHAR2")) {
            sResult = dbl.sqlString(sValue);
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("DATE")) {
            sResult = dbl.sqlDate(sValue);
        } else if ( ( (String) hmDsFieldType.get(sField)).equalsIgnoreCase("NUMBER")) {
            sResult = sValue;
        }
        return sResult;
    }

    //产生凭证的多行取数模式
    //处理方式：每产生一笔分录时都要打开一次数据源，数据源中加入分录条件   胡昆   20070916
    protected void doMultiVch(String sVchTplCode, String sDsSql,
                              HashMap hmDsFieldType) throws
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
                dbl.sqlString(sVchTplCode);
            rsTpl = dbl.queryByPreparedStatement(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY); //modify by fangjiang 2011.08.14 STORY #788
            if (rsTpl.next()) {
                sTplFields = rsTpl.getString("FFields");

                if (sTplFields != null) { //先获取凭证模板中的条件
                    strSql = "select distinct " + sTplFields +
                        " from (" + sDsSql + ")";
                    rsDs = dbl.openResultSet(strSql); //modify by fangjiang 2011.08.14 STORY #788
                    while (rsDs.next()) {
                        sTplFieldAry = sTplFields.split(",");
                        alCond.clear();
                        for (int i = 0; i < sTplFieldAry.length; i++) {
                            cond = new YssWhereCond();
                            cond.setField(sTplFieldAry[i]);
                            cond.setSign("=");
                            cond.setValue(rsDs.getString(cond.getField()));
                            cond.setValue(sqlValue(hmDsFieldType, cond.getField(),
                                rsDs.getString(cond.getField())));
//                     if (i < sTplFieldAry.length - 1) {
                            cond.setRela("and");
//                     }
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
                                                 hmDsFieldType);
                            }
                            //----------------------------------------------------------//
                            strSql = " select * from " +
                                pub.yssGetTableName("Tb_Vch_EntityCond") +
                                " where  FVchTplCode = " +
                                dbl.sqlString(sVchTplCode) +
                                " and FCheckState = 1 and FEntityCode = " +
                                dbl.sqlString(rs.getString("FEntityCode")) +
                                " order by FOrderIndex";
                            rsCond = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                            while (rsCond.next()) { //循环分录条件
                                cond = new YssWhereCond();
                                cond.setField(rsCond.getString("FFieldName"));
                                cond.setSign(rsCond.getString("FSign"));
//                        cond.setValue(rsCond.getString("FValue"));
                                cond.setValue(sqlValue(hmDsFieldType, cond.getField(),
                                    rsCond.getString("FValue")));
                                cond.setRela(rsCond.getString("FConRela"));
                                alSubCond.add(cond);
                            }
                            dbl.closeResultSetFinal(rsCond);

                            strSql = buildMuiltiSql(sDsSql, alSubCond);
                            rsDsSub = dbl.openResultSet(strSql); //modify by fangjiang 2011.08.14 STORY #788 //打开数据源记录集，SQL语句中加入凭证模板和单条分录上的条件
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
                                vchDataEntity.setEntityCode(rs.getString(
                                    "FEntityCode"));
                                vchDataEntity.setSubjectCode(
                                    this.getEntitySubject(rs.getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, hmDsFieldType));
                                vchDataEntity.setResume(
                                    this.getEntityResume(rs.getString(
                                        "FVchTplCode"),
                                    rs.getString("FEntityCode"),
                                    rsDsSub, hmDsFieldType));
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
                                        rsDsSub, hmDsFieldType)
                                    );
                                vchDataEntity.setCalcWay(rs.getString("FCALCWAY")); //为了在之后的计算轧差值,设置计算方式。sj edit 20080219
                                subAddList.add(vchDataEntity);
                                vchData.setDataEntity(subAddList);
                            }
                            dbl.closeResultSetFinal(rsDsSub);
                        }
                        
                        dbl.closeResultSetFinal(rs); //add by jsc20120409 释放游标
                        setPortSet(rsTpl.getString("FLinkCode"), vchData,
                                   vchAdmin);
                        //----------如果存在轧差分录对象，将其放入ArrayList，并将此凭证放入
                        //----------一个ArrayList，以便计算轧差值 sj --------------------//
                        if (gcVchDataEntity != null) {
                            subAddList.add(gcVchDataEntity);
                            aVchData = new ArrayList();
                            aVchData.add(vchData);
                            adjustTail(aVchData); //通过方法计算轧差值 sj 20080219
                        }
                        //------------------------------------------------------------//
                    }
                }
//            }
            }

            vchAdmin.insert();
        } catch (Exception e) {
            throw new YssException(e);
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
                buf.append(cond.toString());
            }
            sDsSql += buf.toString();
        }
        return sDsSql;
    }

    /**
     * 设置轧差分录的对象属性 sj 20080219
     * @param gcVchDataEntity VchDataEntityBean
     * @param gcVchData Date
     * @param rs ResultSet
     * @param hmDsFieldType HashMap
     * @throws SQLException
     * @throws SQLException
     * @throws YssException
     */
    private void setGcVchDataBean(VchDataEntityBean gcVchDataEntity,
                                  java.util.Date gcVchData,
                                  ResultSet rs, HashMap hmDsFieldType) throws
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
                                      null, hmDsFieldType));
            Resume = this.getEntityResume(rs.getString(
                "FVchTplCode"),
                                          rs.getString("FEntityCode"),
                                          null, hmDsFieldType).replaceAll("null",
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
                    null, hmDsFieldType)
                );
            gcVchDataEntity.setCalcWay(rs.getString("FCALCWAY"));
        } catch (Exception e) {
            throw new YssException("设置轧差分录数据出错！", e);
        }
    }

}
