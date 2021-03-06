package com.yss.main.operdeal.voucher.vchbuild;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.voucher.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: BaseVchBuild</p>
 * <p>Description: 凭证生成类的基类,包括一些生成凭证需要的公共方法,和需要实现的方法.
 * 以及需要的相关参数</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class BaseVchBuild
    extends BaseBean {
    private String portCodes = "";
    private String beginDate;
    private String endDate;
    private String currDate;		//当前处理的日期	
    private String vchTypes = "";
    private String otherParams = "";
    protected ArrayList alRepParam = new ArrayList();
	//--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
	private String logSumCode = "";
	
    public String getLogSumCode() {
		return logSumCode;
	}

	public void setLogSumCode(String logSumCode) {
		this.logSumCode = logSumCode;
	}
    //--- add by songjie 2012.09.03 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    public String getPortCodes() {
        return portCodes;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getVchTypes() {
        return vchTypes;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setVchTypes(String vchTypes) {
        this.vchTypes = vchTypes;
    }

    public void setOtherParams(String otherParams) {
        this.otherParams = otherParams;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getOtherParams() {
        return otherParams;
    }
    
    public String getCurrDate() {
    	return this.currDate;
    }
    
    public void setCurrDate(String currDate){
    	this.currDate = currDate;
    }

    public BaseVchBuild() {
    }

    public void init(String sPortCodes, String beginDate,
                     String endDate, String sVchTypes, String otherParams) throws
        YssException {
        this.portCodes = sPortCodes;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.vchTypes = sVchTypes; //凭证属性
        this.otherParams = otherParams;
    }

    public void doVchBuild() throws YssException {

    }

    /* public void parse() throws YssException {
            String[] sRepCtlParamAry = null;
            YssCommonRepCtl repParam = null;
            String[] tmpAry = null;
            StringBuffer buf = new StringBuffer();
            String reStr = "";
            String repStr = "";
            try {
               sRepCtlParamAry = new String[] {
     this.getBeginDate(), this.getEndDate(), this.getPortCodes()};
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
                     }
                     else {
                        buf.append("'" + tmpAry[0] + "'");
                        reStr = buf.toString();
                     }
                     repParam.setCtlValue(reStr);
                  }
                  alRepParam.add(repParam);
               }
            }
            catch (Exception e) {
               throw new YssException(e.getMessage(), e);
            }
         }*/

    public String buildVchDsSql(String sDsCode, String sPortCode) throws
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
            //-----------------单亮 2008-5-23 如果此处的数据源未空则要有相应的提示----------------------//
            if (dataSource == null || dataSource.length() == 0) {
                throw new YssException("请检查数据源是否审核,或者是否已经被删除!");
            }
            //------------------------------------------------------------------------------------//
            return buildSql(dataSource, sPortCode);
        } catch (Exception e) {
            throw new YssException("系统解析凭证数据源时出现异常!" + "\n", e); //by 曹丞 2009.02.02 获取凭证数据源异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String buildSql(String sDs, String sPortCode) throws
        YssException {
        //YssCommonRepCtl repParam = null;
        String sInd = "", sInd2 = ""; //参数的标识
        String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
        int iPos = 0;
        String sSqlValue = "";
        boolean bFlag = false;
        YssFinance cw = null;
        //解析工作日 sunkey@Modify 20091105
        sDs = parseFun(sDs);
        for (int i = 1; i <= 3; i++) { //一共就三个固定的参数 sj
            //repParam = (YssCommonRepCtl) alRepParam.get(i);
            //sInd = "<" + i + ">";
            //iPos = sDs.indexOf(sInd);
            //if (iPos <= 0) {
            //sInd = "<" + i + ">";
            //iPos = sDs.indexOf(sInd);
            //}
            //if (iPos > 1) {
            //sDataType = sDs.substring(iPos - 1, iPos);
            //if (this.beginDate == null || this.endDate == null ||
            //sPortCode.length() == 0) {
            //continue;
            //}
            if (sDs.indexOf("D<1>") > 0) {
                sDs = sDs.replaceAll("D<1>", dbl.sqlDate(this.beginDate));
            } else if (sDs.indexOf("D < 1 >") > 0) {
                sDs = sDs.replaceAll("D < 1 >", dbl.sqlDate(this.beginDate));
            } else if (sDs.indexOf("D< 1 >") > 0) {
                sDs = sDs.replaceAll("D< 1 >", dbl.sqlDate(this.beginDate));
            }
            //------------------------------------------------------------------
            if (sDs.indexOf("D<2>") > 0) {
                sDs = sDs.replaceAll("D<2>", dbl.sqlDate(this.endDate)); //将D<2>改为第二个日期 leeyu 080902
            } else if (sDs.indexOf("D < 2 >") > 0) {
                sDs = sDs.replaceAll("D < 2 >", dbl.sqlDate(this.endDate)); //将D<2>改为第二个日期 leeyu 080902
            } else if (sDs.indexOf("D< 2 >") > 0) {
                sDs = sDs.replaceAll("D< 2 >", dbl.sqlDate(this.endDate)); //将D<2>改为第二个日期 leeyu 080902
            }
            //------------------------------------------------------------------
            //D<C>为当前处理的日期 sunkey@Modify cause by ETF
            if (sDs.indexOf("D<C>") > 0) {
                sDs = sDs.replaceAll("D<C>", dbl.sqlDate(this.currDate)); 		
            } else if (sDs.indexOf("D < C >") > 0) {
                sDs = sDs.replaceAll("D < C >", dbl.sqlDate(this.currDate)); 	
            } else if (sDs.indexOf("D< C >") > 0) {
                sDs = sDs.replaceAll("D< C >", dbl.sqlDate(this.currDate)); 	
            }
            if (sDs.indexOf("N<3>") > 0) {
                sDs = sDs.replaceAll("N<3>", dbl.sqlString(sPortCode));
            } else if (sDs.indexOf("N < 3 >") > 0) {
                sDs = sDs.replaceAll("N < 3 >", dbl.sqlString(sPortCode));
            } else if (sDs.indexOf("N< 3 >") > 0) {
                sDs = sDs.replaceAll("N< 3 >", dbl.sqlString(sPortCode));
            }
            //------------------------------------------------------------------
            if (sDs.indexOf("S<3>") > 0) {
                sDs = sDs.replaceAll("S<3>", dbl.sqlString(sPortCode));
            } else if (sDs.indexOf("S < 3 >") > 0) {
                sDs = sDs.replaceAll("S < 3 >", dbl.sqlString(sPortCode));
            } else if (sDs.indexOf("S< 3 >") > 0) {
                sDs = sDs.replaceAll("S< 3 >", dbl.sqlString(sPortCode));
            }

            //else if (sDataType.equalsIgnoreCase("N")) {
            //转换代码，例如 001,002转换成'001','002'
            //sSqlValue = repParam.getCtlValue();
            //}
            //sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
            //}
            //}
            //sDs = wipeSqlCond(sDs);

        }
        // add by leeyu 080729
        if (sDs.indexOf("<U>") > 0) {
            sDs = sDs.replaceAll("<U>", pub.getUserCode());
        } else if (sDs.indexOf("< U >") > 0) {
            sDs = sDs.replaceAll("< U >", pub.getUserCode());
        }

        if (sDs.indexOf("<Year>") > 0) { //把"<Year>"的标识替换成结束日期的年份
            sDs = sDs.replaceAll("<Year>",
                                 YssFun.formatDate(this.beginDate, "yyyy"));
        } else if (sDs.indexOf("< Year >") > 0) { // add by leeyu 080729
            sDs = sDs.replaceAll("< Year >",
                                 YssFun.formatDate(this.beginDate, "yyyy"));
        }
        if (sDs.indexOf("<Set>") > 0) { //把"<Year>"的标识替换成套帐号
            cw = new YssFinance();
            cw.setYssPub(pub);
            sDs = sDs.replaceAll("<Set>", cw.getCWSetCode(sPortCode));
        } else if (sDs.indexOf("< Set >") > 0) { // add by leeyu 080729
            cw = new YssFinance();
            cw.setYssPub(pub);
            sDs = sDs.replaceAll("< Set >", cw.getCWSetCode(sPortCode));
        }
        if (sDs.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
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
     * getEntitySubject : 获取分录科目
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
    public String getEntitySubject(String sVchTplCode, String sEntityCode,
                                      ResultSet rsDs, HashMap hmDsFieldType,
                                      String sPortCode) throws //新增的
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
                            "FSubjectField").trim()),
                                                  sPortCode);
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
            throw new YssException("系统获取凭证分录科目时出现异常!" + "\n", e); //by 曹丞 2009.02.02 获取凭证分录科目异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getSubjectType(String dictCode, String indCode,
                                 String sPortCode) throws
        YssException {
        ResultSet rs = null;
        String strSql = "";
        StringBuffer buf = new StringBuffer();
        try {
            strSql =
                "select * from " +
                pub.yssGetTableName("Tb_Vch_Dict") +
                " where FDictCode=" + dbl.sqlString(dictCode) +
                " and FIndCode=" + dbl.sqlString(indCode) +
                " and ((FPortCode is null or FPortCode=' ' )or FPortCode=" +
                dbl.sqlString(sPortCode) + ")"; //添加专用组合条件 by liyu 不需要传空格进来
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                buf.append(rs.getString("FCnvConent"));
                return buf.toString(); //这里只取第一条的,因为为专用组合设置值后,可能会有多条,by leeyu 080430
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String getRsValue(ResultSet rsDs, String sFieldName,
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

    /**
     * getEntityResume : 获取分录摘要
     * @param sVchTplCode String ： 凭证模板代码
     * @param sEntityCode String ： 凭证分录代码
     * @param rsDs ResultSet ： 数据源记录集
     */
    public String getEntityResume(String sVchTplCode,
                                     String sEntityCode,
                                     ResultSet rsDs,
                                     HashMap hmDsFieldType, String sPortCode) throws //新增的
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
                            "FResumeField").trim()),
                                                  sPortCode);
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
            throw new YssException("系统获取凭证分录摘要时出现异常!" + "\n", e); //by 曹丞 2009.02.02 获取凭证分录摘要异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rsRes);
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
    public double getEntityMA(String sVchTplCode, String sEntityCode,
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
            throw new YssException("系统获取凭证分录金额时出现异常!" + "\n", e); //by 曹丞 2009.02.02 获取凭证分录金额异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getAssistant(String sVchTplCode,
                                  String sEntityCode,
                                  ResultSet rsDs,
                                  HashMap hmDsFieldType, String sPortCode) throws //新增的
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
                            "FAssistantField").trim()),
                                                  sPortCode);
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

    protected void setPortSet(String sLinkCode, VchDataBean vchData,
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
            strSql = " select distinct FPortCode, trim(to_char(fsetcode,'000')) as FBookSetCode,p.fportcury as FCuryCode " +
            		" from lsetlist l join " + pub.yssGetTableName("tb_para_portfolio") + " p on l.fsetid = p.fassetcode ";
//            	 "select a.*,b.FCuryCode from " +//modified by yeshenghong 20130428 BUG7486 
//                pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                " a left join (select * from " +
//                pub.yssGetTableName("tb_vch_bookset") +
//                " where FCheckState = 1) b on a.Fbooksetcode = b.FBookSetCode" +
//                " where FLinkCode = " + dbl.sqlString(sLinkCode) +
//                " and a.FCheckState = 1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                //------------------判断链接的组合是否为凭证自身的组合，若不是则跳过。 sj edit 20080328 ----
                if (!vchData.getPortCode().equalsIgnoreCase(rs.getString(
                    "FPortCode"))) {
                    continue;
                }
                //-----------------------------------------------------------------------------------
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

                    sAccCury = cw.getCWAccountCury(vchDataEntity.getSubjectCode(),
                        vchData.getVchDate(),
                        vchClone.getPortCode());

                    if (!vchDataEntity.isBSetBal()) { //没有设置本位币，则计算本位币金额
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

    //调整凭证的本位币尾差
    //处理方式：分别汇总借方金额和贷方金额，再用借方金额-贷方金额得到尾差，再把尾差放到第一笔贷方分录上  胡昆 20070918
    protected void adjustTail(ArrayList vchList) throws YssException {
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
                vchDataEntity = (VchDataEntityBean) vchData.getDataEntity().get(
                    j);
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
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingSet") || //记录下需要做轧差的分录，在最后设值  胡昆 20070920
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingAndSet")) { //添加轧差本位币，并将本位币赋值给原币功能，在下面计算 by leeyu 2008-12-20 QDIIV4.0赢时胜2008年12月30日01_B
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
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingSet") || //记录下需要做轧差的分录，在最后设值  胡昆 20070920
                        vchDataEntity.getCalcWay().equalsIgnoreCase("NettingAndSet")) { //添加轧差本位币，并将本位币赋值给原币功能,在下面计算 by leeyu 2008-12-20 QDIIV4.0赢时胜2008年12月30日01_B
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
                    //======增加轧差本位币，并将本位币金额赋值给原币的功能 by leeyu2008-12-30 QDIIV4.0赢时胜2008年12月30日01_B
                    else if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase("NettingAndSet")) {
                        vchDataEnyTmp.setBal( -dbTail);
                        vchDataEnyTmp.setSetBal( -dbTail);
                    }
                    //======2008-12-30
                }
                if (vchDataEnyTmp.getDcWay().equalsIgnoreCase("1")) { //贷方
                    if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase("Netting")) {
                        vchDataEnyTmp.setBal(dTail);
                        vchDataEnyTmp.setSetBal(dbTail);
                    } else if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase(
                        "NettingSet")) {
                        vchDataEnyTmp.setSetBal(dbTail);
                    }
                    //======增加轧差本位币，并将本位币金额赋值给原币的功能 by leeyu2008-12-30 QDIIV4.0赢时胜2008年12月30日01_B
                    else if (vchDataEnyTmp.getCalcWay().equalsIgnoreCase("NettingAndSet")) {
                        vchDataEnyTmp.setBal(dbTail);
                        vchDataEnyTmp.setSetBal(dbTail);
                    }
                    //======2008-12-30

                }
            }
        }
    }

    /**
     * 判断所有的分录的金额和数量是否为0,若是的话,则不录入这笔凭证.
     * @param subAddList ArrayList
     * @throws YssException
     * @return String
     */
    public String checkDataEntity(ArrayList subAddList) throws YssException {
        boolean isNothing = true;
        VchDataEntityBean vchDataEntity = null;
        try {
            for (int i = 0; i < subAddList.size(); i++) {
                vchDataEntity = (VchDataEntityBean) subAddList.get(i);
                if (vchDataEntity.getBal() != 0 ||
                    vchDataEntity.getSetBal() != 0 ||
                    vchDataEntity.getAmount() != 0) {
                    isNothing = false;
                    break;

                }
            }
            return Boolean.toString(isNothing);
        } catch (Exception e) {
            throw new YssException("检查凭证分录出错!" + "\n", e);
        }
    }

    /**
     * 添加对当前模块的所有凭证分录检查
     * QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
     * @param subAddList ArrayList
     * @throws YssException
     */
    public ArrayList checkDataEntitys(ArrayList subAddList) throws YssException {
        ArrayList subAddListClone = null;
        VchDataEntityBean vchDataEntity = null;
        try {
            subAddListClone = (ArrayList) subAddList.clone();
            subAddList.clear(); //清除所有的分录,好在下面的判断中依次再添加进去
            for (int i = 0; i < subAddListClone.size(); i++) {
                vchDataEntity = (VchDataEntityBean) subAddListClone.get(i);
                if (vchDataEntity.getAmount() == 0 && vchDataEntity.getBal() == 0 && vchDataEntity.getSetBal() == 0) {
                    int ii = 0;
                }
                if (vchDataEntity.getSAllow() != null && vchDataEntity.getSAllow().length() > 0 && !vchDataEntity.getSAllow().equalsIgnoreCase("0")) { //添加条件：如果sAllow有值且不为0，说明要进行特殊处理
                    if (vchDataEntity.getSAllow().equalsIgnoreCase("AisZero")) { //数量允许为0
                        //----MS00459 QDV4赢时胜（上海）2009年5月19日01_B sj 对此类型进行特殊处理---------------------------------
                        if (!vchDataEntity.getCalcWay().equalsIgnoreCase("Common")) { //当为轧差类型时,才放入,使之后的轧差计算得以进行。当为普通类型时，不保留分录信息
                            subAddList.add(vchDataEntity);
                        } else if (vchDataEntity.getBal() != 0 || vchDataEntity.getSetBal() != 0) { //当为普通类型时，判断金额
                            subAddList.add(vchDataEntity);
                        }
                        //-------------------------------------------------------------------------------------------------
                    } else if (vchDataEntity.getSAllow().equalsIgnoreCase("MisZero")) { //金额(原币与本位币)允许为0
                        if (vchDataEntity.getAmount() != 0) {
                            subAddList.add(vchDataEntity);
                        }
                    } else if (vchDataEntity.getSAllow().equalsIgnoreCase("AMisZero")) { //数量与金额(原币与本位币)都允许为0
                        subAddList.add(vchDataEntity);
                    } else { //其他情况，包括sAllow="0"的情况
                        if (vchDataEntity.getBal() != 0 || vchDataEntity.getSetBal() != 0 || vchDataEntity.getAmount() != 0) {
                            subAddList.add(vchDataEntity);
                        }
                    }
                } else { //其他情况需要进行判断，也就是常规情况
                    if (vchDataEntity.getBal() != 0 ||
                        vchDataEntity.getSetBal() != 0 ||
                        vchDataEntity.getAmount() != 0) {
                        subAddList.add(vchDataEntity);
                    }
                }
            }
            return subAddList;
        } catch (Exception e) {
            throw new YssException("检查凭证各分录出错!" + "\n", e);
        }
    }

    /**
     * 对轧差生成的凭证进行检查，不通过检查的去除此分录
     * @param subAddList ArrayList
     * @throws YssException
     * MS00459 QDV4赢时胜（上海）2009年5月19日01_B sj
     */
    protected void checkAdjustDataEntitys(ArrayList DataList) throws YssException {
        VchDataBean vchData = null; //凭证
        VchDataEntityBean vchDataEntity = null; //分录
        try {
            for (int i = 0; i < DataList.size(); i++) { //循环凭证
                vchData = (VchDataBean) DataList.get(i); //获取单个凭证
                for (int j = 0; j < vchData.getDataEntity().size(); j++) { //循环分录
                    vchDataEntity = (VchDataEntityBean) vchData.getDataEntity().get(j); //获取单个分录
                    if (vchDataEntity.getCalcWay().equalsIgnoreCase("Common")) { //如果为普通分录，不进行判断
                        continue; //执行下个分录的判断，不对此分录进行处理。
                    } else { //若为轧差类型的分录
                        if (vchDataEntity.getSAllow().equalsIgnoreCase("AisZero")) { //数量允许为0，原币或本位币任一不为0，保存原来分录
                            if (vchDataEntity.getBal() != 0 ||
                                vchDataEntity.getSetBal() != 0) {
                                continue;
                            } else { //不满足条件的
                                vchData.getDataEntity().remove(j); //将分录从凭证中去除
                            }
                        } else if (vchDataEntity.getSAllow().equalsIgnoreCase("MisZero")) { //金额(原币与本位币)允许为0,数量不为0，保存原来分录
                            if (vchDataEntity.getAmount() != 0) {
                                continue;
                            } else { //不满足条件的
                                vchData.getDataEntity().remove(j); //将分录从凭证中去除
                            }
                        } else if (vchDataEntity.getSAllow().equalsIgnoreCase(
                            "AMisZero")) { //数量与金额(原币与本位币)都允许为0，直接保存原有分录
                            continue;
                        } else { //其他情况，包括sAllow="0"的情况
                            if (vchDataEntity.getBal() != 0 ||
                                vchDataEntity.getSetBal() != 0 ||
                                vchDataEntity.getAmount() != 0) {
                                continue; //保存原有分录
                            } else { //不满足条件的
                                vchData.getDataEntity().remove(j); //从凭证中去除分录
                            }
                        }
                    } //end if
                } //end for j
            } //end for i
        } catch (Exception e) {
            throw new YssException("检查轧差凭证分录出错!" + "\n", e);
        }
    }
    
	private String parseFun(String funStr) throws YssException {

		if(funStr.indexOf("WDay[") < 0){
			return funStr;
		}
		try {
			// 取出开头
			String strBegin = funStr.substring(0, funStr.indexOf("WDay["));
			System.out.println(strBegin);

			// 取出结尾
			String strEnd = funStr.substring(strBegin.length() - 1);
			System.out.println(strEnd);

			// 取出函数
			String strFun = strEnd.substring(0, strEnd.indexOf("]") + 1);
			System.out.println(strFun);

			// 删除结尾里的函数
			strEnd = strEnd.substring(strEnd.indexOf("]") + 1);

			// 删除函数外壳，用来获取函数内容
			strFun = strFun.replaceFirst("WDay\\[", "").replaceFirst("\\]", "");

			// 截取函数数据
			String[] funContent = strFun.split(",");
			
			//这里的日期要独立取，不能使用通用的过程，因为通用的处理过程中会将D<>写成sqldate
			if (funContent[0].indexOf("D<1>") > 0) {
				funContent[0] = funContent[0].replaceAll("D<1>",this.beginDate);
            } else if (funContent[0].indexOf("D < 1 >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D < 1 >", this.beginDate);
            } else if (funContent[0].indexOf("D< 1 >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D< 1 >", this.beginDate);
            }

            if (funContent[0].indexOf("D<2>") > 0) {
            	funContent[0] = funContent[0].replaceAll("D<2>", this.endDate); 	//将D<2>改为第二个日期 leeyu 080902
            } else if (funContent[0].indexOf("D < 2 >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D < 2 >", this.endDate); //将D<2>改为第二个日期 leeyu 080902
            } else if (funContent[0].indexOf("D< 2 >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D< 2 >", this.endDate); 	//将D<2>改为第二个日期 leeyu 080902
            }
            
            //D<C>为当前处理的日期 sunkey@Modify cause by ETF
            if (funContent[0].indexOf("D<C>") > 0) {
            	funContent[0] = funContent[0].replaceAll("D<C>", this.currDate); 		
            } else if (funContent[0].indexOf("D < C >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D < C >", this.currDate); 	
            } else if (funContent[0].indexOf("D< C >") > 0) {
            	funContent[0] = funContent[0].replaceAll("D< C >", this.currDate); 	
            }

			// 替换 0.日期 1.节假日代码 2.+-天数
			strFun = dbl.sqlDate(getSettingOper().getWorkDay(funContent[1].trim(), YssFun.toDate(funContent[0].trim()), YssFun.toInt(funContent[2].trim())));

			// 拼装替换过的字符串
			funStr = strBegin + strFun + strEnd;

			if (funStr.indexOf("WDay[") > 0) {
				return parseFun(funStr);
			} else {
				return funStr;
			}
		} catch (Exception e) {
			throw new YssException("解析工作日函数出错！请检查函数是否使用正确WDay<日期，节假日代码,天数>", e);
		}
	}

}
