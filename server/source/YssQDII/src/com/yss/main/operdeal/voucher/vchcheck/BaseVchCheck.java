package com.yss.main.operdeal.voucher.vchcheck;

import com.yss.dsub.BaseBean;
import com.yss.log.SingleLogOper;
import com.yss.pojo.param.comp.YssCommonRepCtl;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * <p>Title: BaseVchCheck</p>
 * <p>Description: 检查凭证相关正确性的基类，包括一些子类需要的方法和需要实现的方法。
 * 以及一些相关参数</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class BaseVchCheck
    extends BaseBean {
    protected String sportCode = "";
    protected String beginDate;
    protected String endDate;
    protected String vchTypes = "";
    protected boolean isInData = false;
    private String params = "";
    ArrayList alRepParam = new ArrayList();
    protected String strInvokeType = "";	//20120412 added by liubo.Story #2192.调用类型.通过凭证检查界面调用此类，值为FrmVchCheck，其他情况下均为""
    
    //--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
    public String logSumCode = "";//汇总日志编号
    public SingleLogOper logOper;//日志实例
    //--- add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
    
    public BaseVchCheck() {
    }
    
	//20120412 modified by liubo.Story #2192
	//将sInvokeType（调用类型）传给调用的BaseVchCheck对象
    public void init(String sportCode, String beginDate, String endDate, String vchTypes, boolean isInData, String sInvokeType) throws YssException {
        this.sportCode = sportCode;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.vchTypes = vchTypes;
        this.isInData = isInData;
        this.strInvokeType = sInvokeType;
        
        params = "1\r" + YssFun.formatDate(beginDate)
        + "\n2\r" + YssFun.formatDate(endDate)
        + "\n3\r" + sportCode;
        parse();
    }

    public String doCheck() throws YssException {
        return "";
    }

    protected String[] getBookSet(String sportCode) throws YssException {
        String[] bookSets = null;
        ResultSet rs = null;
        StringBuffer chkQuery = new StringBuffer();
        String booksets = "";
        try {
        	
        	
        	if(YssCons.YSS_VCH_CHECK_MODE.equalsIgnoreCase("batch")){
        		chkQuery.append(" select a.fportcode,b.fsetcode as FBookSetCode from ");
        		chkQuery.append(" (select fportcode,fassetcode from  ").append(pub.yssGetTableName("tb_para_portfolio"));
    			chkQuery.append(" where fcheckstate=1)a ");
    			chkQuery.append(" left join ");
    			chkQuery.append(" (select fsetid,fyear,fsetcode from lsetlist) b on a.fassetcode = b.fsetid ");
    			chkQuery.append(" where a.fportcode in(").append(operSql.sqlCodes(sportCode)).append(")");
    			chkQuery.append(" and b.fyear = ").append(YssFun.formatDate(this.beginDate, "yyyy"));
        	}else{
        		chkQuery.append(" select distinct FPortCode, trim(to_char(fsetcode,'000')) as FBookSetCode " ) 
        				.append( " from lsetlist l join " ).append("tb_para_portfolio").append("p on l.fsetid = p.fassetcode ");
        		chkQuery.append(" where p.FCheckState = 1 and p.FPortCode = ").append(dbl.sqlString(sportCode));//modified by yeshenghong 20130428 BUG7486 
//        		chkQuery.append(" select FPortCode,FBookSetCode from ").append(pub.yssGetTableName("Tb_Vch_PortSetLink"));
//        		chkQuery.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(sportCode));
        	}
            
            rs = dbl.queryByPreparedStatement(chkQuery.toString()); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                booksets += rs.getString("FBookSetCode") + ",";
            }
            if (booksets.length() > 0) {
                booksets = booksets.substring(0, booksets.length() - 1);
                if (booksets.indexOf(",") > 0) {
                    bookSets = booksets.split(",");
                } else {
                    bookSets = new String[] {
                        booksets};
                }
            }
            return bookSets;
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    protected String getVchTpls() throws YssException {
        String reStr = "";
        ResultSet rs = null;
        String sqlStr = "";
        try {
            sqlStr = "select FVchTplCode,FAttrCode from " +
                pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FAttrCode in (" +
                operSql.sqlCodes(vchTypes) + ")" +
                " and ((FPortCode is null or FPortCode='') or FPortCode=" +
                dbl.sqlString(sportCode) + ")"; //增加对专用组合的处理 by ly 080326
            rs = dbl.queryByPreparedStatement(sqlStr); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                reStr += rs.getString("FVchTplCode") + ",";
            }
            if (reStr.length() > 0) {
                reStr = reStr.substring(0, reStr.length() - 1);
            }

            return reStr;
        } catch (Exception e) {
            throw new YssException("", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private void parse() throws YssException {
    	String[] sRepCtlParamAry = null;
        YssCommonRepCtl repParam = null;
        String repStr = "";
        String[] tmpAry = null;
        StringBuffer buf = new StringBuffer();
        String reStr = "";
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
    }
    
    protected String buildDsSql(String sDs, String sPortCode) throws YssException {
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
        if (sDs.indexOf("<Group>") > 0) { //把"<Group>"的标识替换成群 sj edit 20080306
            sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
        } else if (sDs.indexOf("< Group >") > 0) {
            sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
        }
        sDs = sDs.replaceAll("~Base", "base");
        return sDs;
    }
    
    protected String wipeSqlCond(String strSql) throws YssException {
        int iBPos = 0;
        int iEPos = 0;
        String sCond = "";
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

}
