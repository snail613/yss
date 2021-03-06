package com.yss.base;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.BaseBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.operdeal.bond.BondInsCfgFormulaN;
import com.yss.main.operdeal.cashmanage.CommandCfgForMula;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.operdeal.derivative.BaseDerivativeOper;
import com.yss.main.operdeal.invest.BaseInvestOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPubBean;
import com.yss.main.operdeal.valuation.LeverGradeFundCfg;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BaseCalcFormula
    extends BaseBean {
    HashMap hmFormula = new HashMap();
   	public HashMap hmFormulaValue=new HashMap();	//20131114 modified by liubo.招行使用调度方案执行计提债券利息的问题
    protected ArrayList alFormula = new ArrayList();
	//edit by songjie 2013.04.17 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 protected 改为 public
    public String formula = "";
    protected String sign = "";
    /**shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数**/
    private String FPortCode;//组合
    private String FCuryCode;;//币种(远期对应的卖出币种)
    
    private String securityCode;
    //--- add by songjie 2013.04.11 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 start---//
    private boolean calPerHundred = false;
    
    public void setCalPerHundred(boolean calPerHundred){
    	this.calPerHundred = calPerHundred;
    }
    
    public boolean getCalPerHundred(){
    	return this.calPerHundred;
    }
    //--- add by songjie 2013.04.11 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 end---//
    
    //--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 start---//
    private String calProcess = "";//债券利息计算过程
    public String getCalProcess(){
    	return calProcess;
    }
    
    public void setCalProcess(String calProcess){
    	this.calProcess = calProcess;
    }
    //--- add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002 end---//
    
    public String getSecurityCode() {
		return "'" + securityCode + "'";
	}

	public void setSecurityCode(String securityCode) {
		this.securityCode = securityCode;
	}

	public String getFCuryCode() {
		return FCuryCode;
	}

	public void setFCuryCode(String fCuryCode) {
		FCuryCode = fCuryCode;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}
	/**~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~~~*/
	
	public String getSign() {
        return sign;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFormula() {
        return formula;
    }

    public BaseCalcFormula() {
    }

    public void parseFormulaV1() {
        StringBuffer buf = new StringBuffer();
        String[] arrSign = sign.split(",");
        String sTmpSign = "";
        ArrayList liFormula = new ArrayList();
        liFormula.add(formula);
        //循环关键标记
        for (int i = 0; i < arrSign.length; i++) {
            //每拆分1次，将拆分后的字符串数组装入 List
            if (arrSign[i].length() == 1) {
                if (arrSign[i].equalsIgnoreCase("[") ||
                    arrSign[i].equalsIgnoreCase("]")) {
                    arrSign[i] = "\\" + arrSign[i];
                }
                sTmpSign = "[" + arrSign[i] + "]";
            } else {
                sTmpSign = arrSign[i];
            }
            for (int iLi = 0; iLi < liFormula.size(); iLi++) {
                String[] sTmp = ( (String) liFormula.get(iLi)).split(sTmpSign);
                ArrayList liTemp = new ArrayList();
                if (sTmp.length <= 1) {
                    if (sTmp.length == 0 ||
                        sTmp[0].length() == ( (String) liFormula.get(iLi)).length()) {
                        continue;
                    }
                }
                for (int iT = 0; iT < sTmp.length; iT++) {
                    liTemp.add(sTmp[iT]);
                    liTemp.add(arrSign[i]);
                }
                if (liTemp.size() > 2) {
                    liTemp.remove(liTemp.size() - 1);
                }
                liFormula.remove(iLi);
                liFormula.addAll(iLi, liTemp);
                iLi += liTemp.size();
            }
        }
        this.alFormula = liFormula;
    }

    public void parseFormula() throws YssException {
        char[] chrAry = formula.toCharArray();
        String sTmp = "";
        StringBuffer buf = new StringBuffer();
        this.alFormula.clear();//DB2报错 modified by yeshenghong 20130627
        for (int i = 0; i < chrAry.length; i++) {
            sTmp = String.valueOf(chrAry[i]);
            if (sign.indexOf(sTmp) >= 0 && !sTmp.equalsIgnoreCase(";")) {
                if (buf.toString().length() > 0) {
                    hmFormula.put(buf.toString(), null);
                    alFormula.add(buf.toString());
                    alFormula.add(new String(sTmp));
//               System.out.println(buf.toString());
                    buf.setLength(0);
                } else if (buf.toString().length() == 0 && sign.indexOf(sTmp) >= 0) {
                    alFormula.add(new String(sTmp));
                }
            } else {
                buf.append(sTmp);
            }
        }
        if (buf.length() > 0) {
            hmFormula.put(buf.toString(), null);
            alFormula.add(buf.toString());
            buf.setLength(0);
        }
    }

    public String replaceFormulaStr() throws YssException {
        Object objValue = null;
        String sKeyVal = "";
        String sReplacement = "";
        StringBuffer bufRes = new StringBuffer();
        try {
            parseFormulaV1();
            for (int i = 0; i < this.alFormula.size(); i++) {
                sKeyVal = (String) alFormula.get(i);
                objValue = getFormulaValue(sKeyVal);
                if (objValue instanceof Double) {
                    sReplacement = String.valueOf( ( (Double) objValue).doubleValue());
                } else if (objValue instanceof java.util.Date) {
                    sReplacement = dbl.sqlDate( (java.util.Date) objValue);
                } else if (objValue instanceof Long) {
                    sReplacement = String.valueOf( ( (Long) objValue).longValue());
                } else if (objValue instanceof String) {
                    sReplacement = String.valueOf(objValue);
                }
                bufRes.append(sReplacement);
            }
        } catch (Exception ex) {
            throw new YssException("数据替换解析内部函数出错", ex);
        }
        return bufRes.toString();
    }

    public double calcFormulaDouble() throws YssException {
        Iterator iter = null;
        String sKey = "";
        //Double dblValue = null;
        ResultSet rs = null;
        String strSql = "";
        double dResult = 0;
        Object objValue = null;
        String sReplacement = "";
        StringBuffer buf = new StringBuffer();
        String tmp = "";
        try {
            parseFormula();
//         iter = hmFormula.keySet().iterator();
//         while (iter.hasNext()) {
//            sKey = (String) iter.next();
//            objValue = getFormulaValue(sKey);
//            hmFormula.put(sKey, objValue);
//
//            if (objValue instanceof Double){
//               sReplacement = String.valueOf(((Double)objValue).doubleValue());
//            }else if (objValue instanceof java.util.Date){
//               sReplacement = dbl.sqlDate((java.util.Date)objValue);
//            }else if (objValue instanceof Long){
//               sReplacement = String.valueOf(((Long)objValue).longValue());
//            }else if (objValue instanceof String){
//               sReplacement = String.valueOf(objValue);
//            }
//            //formula.replaceAll(sKey, String.valueOf(dblValue.doubleValue()));
//              formula = formula.replaceAll(sKey,sReplacement);
//         }
            for (int i = 0; i < alFormula.size(); i++) {
                sKey = (String) alFormula.get(i);
                objValue = getFormulaValue(sKey.trim());
                System.out.println(sKey);
                if (objValue instanceof Double) {
                    sReplacement = String.valueOf( ( (Double) objValue).doubleValue());
                } else if (objValue instanceof java.util.Date) {
                    sReplacement = dbl.sqlDate( (java.util.Date) objValue);
                } else if (objValue instanceof Long) {
                    sReplacement = String.valueOf( ( (Long) objValue).longValue());
                } else if (objValue instanceof String) {
                    sReplacement = String.valueOf(objValue);
                }
                //----------- 添加一个对boolean值的处理 sj modify  MS00052 ------------------------------------
                else if (objValue instanceof Boolean) {
                    sReplacement = String.valueOf( ( (Boolean) objValue).booleanValue());
                }
                //---------------添加对BigDecimal的处理 QDV4赢时胜上海2009年1月14日01_B MS00188 by leeyu 20090311
                else if (objValue instanceof BigDecimal) { //添加对BigDecimal的处理
                    sReplacement = String.valueOf( (BigDecimal) objValue);
                }
                buf.append(sReplacement);
            }
            
            //--- add by songjie 2013.04.11 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 start---//
            if(this instanceof BondInsCfgFormulaN){
            	return 0;
            }
            //--- add by songjie 2013.04.11 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001 end---//
            
            if (formula.length() > 0) {
//            strSql = "select " + buf.toString() + " from Tb_Sys_UserList";
//            rs = dbl.openResultSet(strSql);//这里打开Tb_Sys_UserList表只是为了带括号的运算
//            if (rs.next()){
//               dResult = rs.getDouble(1);
//            }

//            DynamicLoader loader = new DynamicLoader();
//            BondBuildDynmic build = new BondBuildDynmic();
//            loader.setRealName("com.yss.base.CalcBond");
//            loader.dynamicCompile(build.buildDynmic(buf.toString()),"CalcBond");
//            Class c = loader.findClass("com\\yss\\base\\CalcBond");
//
//            Object o = c.newInstance();
//
//            Method setPub = c.getMethod("setPub", new Class[] {YssPub.class});
//            setPub.invoke(o, new Object[] {pub});
//
//            Method reString = c.getMethod("reBondValue", new Class[] {});
//            Object reObj = reString.invoke(o, new Object[] {});
//            if (reObj != null) {
//               dResult = ((Double)reObj).doubleValue();
//            }
            	/**shashijie 2012-2-14 STORY 1713 增加对系统表的组合群解析 */
            	tmp = buf.toString().replaceAll("<group>", pub.getPrefixTB());
            	tmp = tmp.replaceAll("<securityCode>", this.getSecurityCode());
                strSql = buildDynmic(tmp);
                /**end*/
				calProcess = strSql;//add by songjie 2013.06.13 STORY #3959 需求深圳-(YSS_SZ)QDIIV4.0(高)20130514002
                dbl.executeSql(strSql);
                if (this instanceof BaseBondOper) { //在为债券计息时调用get_val函数来计算 sj modified 20081210 MS00052
                	//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
                	if(((BaseBondOper)this).getFromDomestic()){
                		strSql = "select get_DaoVal_"+pub.getUserCode()+" from Tb_Sys_UserList"; //获取专门用于国内接口处理的函数的调用值。//多用户的并发 合太平版本
                	}
                	else{
                		//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
                		strSql = "select get_val_"+pub.getUserCode()+" from Tb_Sys_UserList"; //获取函数的调用值。//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
                		//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
                	}
                	//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
                } else if (this instanceof BaseInvestOper) { //在为运营时调用get_InvestOper函数来计算 sj modified 20081210 MS00052
                	//strSql = "select get_InvestVal from Tb_Sys_UserList"; //获取函数的调用值。
                	strSql = "select get_InvestVal_"+pub.getUserCode()+" from Tb_Sys_UserList"; //获取函数的调用值。//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
                }
                //----------MS00236 QDV4南方2009年02月05日01_B sj modified -----//
                else if (this instanceof BaseDerivativeOper) { //增加对远期交易的处理
					//strSql = "select get_FWVal from Tb_Sys_UserList";
               		strSql = "select get_FWVal_"+pub.getUserCode()+" from Tb_Sys_UserList";//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
                }
                //------------------------------------------------------------//
				//add by yeshenghong story 3759 20130512
                else if (this instanceof LeverGradeFundCfg)
                {
                	strSql = "select get_LGFVal_"+pub.getUserCode()+" from Tb_Sys_UserList";
                }
				//---end add by yeshenghong story 3759 20130512
                else if(this instanceof CommandCfgForMula) {
                	strSql = "select get_Ta_Trade_"+pub.getUserCode()+" from Tb_Sys_UserList";	
                }
                    rs = dbl.openResultSet(strSql);
                    if (rs.next()) {
                        dResult = rs.getDouble(1);
                    }
                }
            
            return dResult;
        } catch (Exception e) {
            throw new YssException("系统通过公式计算收益时出现异常!" + "\n", e); //by 曹丞 2009.02.01 通过公式计算收益异常信息 MS00004 QDV4.1-2009.2.1_09A
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public Object getFormulaValue(String sKeyword) throws YssException {
	   	//计息公式的优化 by leeyu 20100414
	  	 if(hmFormulaValue.get(sKeyword)!=null){
		   return hmFormulaValue.get(sKeyword);
	   	}
	   	//by leeyu 20100414
        int iPos = 0;
        int ePos = 0;
        ArrayList alParams = null;
        String sExpress = "";
        String sParams = "";
        String sEndStr = "";
        Object objResult = null;
        //------------获取[]符号的位置，以便判断它们之间的先后关系。sj edit 20080919 暂无bug
        int begin = 0;
        int end = 0;
        //--------------------------------------------------------------------------
        sKeyword = sKeyword.replaceAll("~", "-");//将～号转换为-号
        begin = sKeyword.indexOf("[");
        end = sKeyword.indexOf("]");
        if (begin >= 0 && end >= 0 && end > begin) { //处理公式 //只有当符号[在符号]之前的情况之下，才是一个完整的公式，需要以公式来处理。sj edit 20080919 暂无bug
            iPos = sKeyword.indexOf("[");
            ePos = sKeyword.lastIndexOf("]");
            if (sKeyword.lastIndexOf("]") < sKeyword.length()) {
                sEndStr = sKeyword.substring(ePos + 1); //判断,如果"]"后面还有字符串的情况 by leeyu 0602
            }
            sParams = sKeyword.substring(iPos + 1, ePos);
            sExpress = sKeyword.substring(0, iPos);
            alParams = getExpressParams(sParams);
            if (sEndStr.length() > 0) {
                objResult = getExpressValueEx(sExpress, alParams, sEndStr);
            } else {
                objResult = getExpressValue(sExpress, alParams);
            }
        } else {
            objResult = getKeywordValue(sKeyword);
        }
     	hmFormulaValue.put(sKeyword, objResult);//计息公式的优化 by leeyu add 20100414
        return objResult;
    }

    public ArrayList getExpressParams(String sParams) throws YssException {
        ArrayList arr = new ArrayList();
        Object objResult = null;
        String[] aFormula = null;
        String operStrs = "";
        try {
            int begin = 0;
            int end = 0;
            //--------------------------------------------------------------------------
            begin = sParams.indexOf("[");
            end = sParams.indexOf("]");
//            if (begin >= 0 && end >= 0 && end > begin&&sParams.endsWith("]"))//modified by  yeshenghong 添加对多参数函数的支持  story4151  20130806
//            {//当前结合实际情况 只支持一个参数  yield[getClsPortCode[1;USD;0],0]
//            	arr.add(getFormulaValue(sParams));
//            }else 
            if (begin >= 0 && end >= 0 && end > begin)//类似于classStaticValue[getClsPortCode[1;USD;0];getFixedDate[investDate;-1];38]的
            {
            	aFormula = sParams.split(";");//对符号参数的解析
            	if (aFormula.length > 0) {
	                for (int i = 0; i < aFormula.length; i++) {
	                    objResult = getFormulaValue(aFormula[i]);
	                    if (objResult != null) {
	                        arr.add(i, objResult);
	                    } else {
	                        arr.add(i, aFormula[i]);
	                    }
	                }
	            }
            } 
            else
            {
            	if(sParams.indexOf(":")>0)
            	{
            		aFormula = sParams.split(":");
            	}else
            	{
            		aFormula = sParams.split(";");
            	}
	            if (aFormula.length > 0) {
	                for (int i = 0; i < aFormula.length; i++) {
	                    objResult = getFormulaValue(aFormula[i]);
	                    if (objResult != null) {
	                        arr.add(i, objResult);
	                    } else {
	                        arr.add(i, aFormula[i]);
	                    }
	                }
	            }
            }
            return arr;
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /**
     * 验证脚本的执行结果
     * @throws YssException
     * @return boolean：脚本代入 SQL 语句中执行后，如果有结果集返回 true，没有结果集返回 false
     */
    public boolean validateScript() throws YssException {
        boolean bResult = false;
        String strSql = "";
        String sKey = "";
        String sReplacement = "";
        Object objValue = null;
        StringBuffer buf = new StringBuffer();
        String strSqlWhere = "";
        ResultSet rs = null;
        try {
            this.parseFormulaV1();
            for (int j = 0; j < this.alFormula.size(); j++) {
                sKey = (String) alFormula.get(j);
                objValue = getFormulaValue(sKey);
                if (objValue instanceof Double) {
                    sReplacement = String.valueOf( ( (Double) objValue).
                                                  doubleValue());
                } else if (objValue instanceof java.util.Date) {
                    sReplacement = dbl.sqlDate( (java.util.Date) objValue);
                } else if (objValue instanceof Long) {
                    sReplacement = String.valueOf( ( (Long) objValue).longValue());
                } else if (objValue instanceof String) {
                    sReplacement = String.valueOf(objValue);
                }
                buf.append(sReplacement);
            }
            strSqlWhere = buf.toString().replaceAll("!", "-");
            strSqlWhere = strSqlWhere.replaceAll("！", "-");
            strSql = "SELECT * FROM Tb_Sys_UserList WHERE " + buf.toString();
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                bResult = true;
            } else {
                bResult = false;
            }
        } catch (Exception e) {
            throw new YssException("验证脚本出错!", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bResult;
    }

    protected String YssIn(String sExpress, ArrayList alParams) throws
        YssException {
        String sResult = "";
        ResultSet rs = null;
        String sLastStr = "";
        try {
            for (int i = 0; i < alParams.size(); i++) {
                if (alParams.get(i).toString().lastIndexOf("]") > 0) {
                    sLastStr = alParams.get(i).toString().substring(alParams.get(i).
                        toString().lastIndexOf("]") + 1,
                        alParams.get(i).toString().length());
                    rs = dbl.openResultSet(alParams.get(i).toString().substring(0,
                        alParams.get(i).toString().lastIndexOf("]")));
                } else {
                    rs = dbl.openResultSet(alParams.get(i).toString());
                } while (rs.next()) {
                    sResult += rs.getString(1);
                    sResult += ",";
                }
                if (sResult.endsWith(",")) {
                    sResult = YssFun.left(sResult, sResult.length() - 1);
                }
            }
            sExpress = sExpress.toLowerCase().replaceAll("yssin", "in");
            sResult = sExpress + "(" + operSql.sqlCodes(sResult) + ")" + sLastStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * 扩展getExpressValue()方法,这里可以处理串尾的数据
     * @param sExpress String
     * @param alParams ArrayList
     * @param sEndStr String
     * @return Object
     * @throws YssException
     */
    public Object getExpressValueEx(String sExpress, ArrayList alParams,
                                    String sEndStr) throws YssException {
        return null;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        return null;
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        return null;
    }

    /**
     * 编写创建函数所需的字符窜。
     * @param CalcFormula String
     * @return String
     * @throws YssException
     */
    public String buildDynmic(String CalcFormula) throws YssException {
        String calcStr = "";
        StringBuffer buf = new StringBuffer();
        if (this instanceof BaseBondOper) { //在为债券计息时生成get_val函数来计算 sj modified 20081210 MS00052
        	//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        	if(((BaseBondOper)this).getFromDomestic()){
        		buf.append("create or replace function get_DaoVal_").append(pub.getUserCode()).append("\n");//多用户并发优化 合并太平版本代码
        	}
        	else{
        		//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
				//buf.append("create or replace function get_val").append("\n");
         		buf.append("create or replace function get_val_").append(pub.getUserCode()).append("\n");//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
        		//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        	}
        	//----add by songjie 2009.12.17 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        } else if (this instanceof BaseInvestOper) { //在为运营计算时生成get_InvestVal函数来计算 sj modified 20081210 MS00052
         	//buf.append("create or replace function get_InvestVal").append("\n");
    	  	buf.append("create or replace function get_InvestVal_").append(pub.getUserCode()).append("\n");//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
        }
        //----------MS00236 QDV4南方2009年02月05日01_B sj modified ----------//
        else if (this instanceof BaseDerivativeOper) { //增加对远期交易的处理
         	//buf.append("create or replace function get_FWVal").append("\n");
         	buf.append("create or replace function get_FWVal_").append(pub.getUserCode()).append("\n");//优化，处理并行业务数据 by leeyu 20100423 合并太平版本代码
        }
        //------------------------------------------------------------------//
		//add by yeshenghong story 3759 20130512
        else if (this instanceof LeverGradeFundCfg)
        {
         	buf.append("create or replace function get_LGFVal_").append(pub.getUserCode()).append("\n");
        }
		//---end add by yeshenghong story 3759 20130512
		 else if(this instanceof CommandCfgForMula) {
        	//---add by liuxiaojun  20130803  story 4094
        	buf.append("create or replace function get_Ta_Trade_").append(pub.getUserCode()).append("\n");
        }
        buf.append("return number is ").append("\n");
        //add by songjie 2010.01.14 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        if((this instanceof BaseBondOper) &&((BaseBondOper)this).getFromDomestic()){
        	if(((BaseBondOper)this).getPortCode()!= null && !((BaseBondOper)this).getPortCode().equals("")){
                CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
                interfaceParam.setYssPub(pub);

                //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
                HashMap hmReadType = (HashMap) interfaceParam.getReadTypeBean();
                ReadTypeBean readType = (ReadTypeBean)hmReadType.get(pub.getAssetGroupCode() + 
                		" " + ((BaseBondOper)this).getPortCode());
                if(readType != null){
                	int exchangePreci = readType.getExchangePreci();
                	 buf.append("v_value number(24," + String.valueOf(exchangePreci) + ");").append("\n");
                }
                else{
                	buf.append("v_value number(24,4);").append("\n");
                }
        	}
        	else{
        		buf.append("v_value number(24,4);").append("\n");
        	}
        }
        //---add by songjie 2013.04.11 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 start---//
        else if(this.getCalPerHundred()){
        	buf.append("v_value number(30,15);").append("\n");
        }
        //---add by songjie 2013.04.11 STORY 3806 需求深圳-(南方基金)QDII估值系统V4.0(高)20130402001 end---//
        
        /**shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数**/
        else if(this instanceof BaseDerivativeOper && this.FPortCode!=null && !this.FPortCode.trim().equals("")){//增加对远期交易的处理
        	String value = _getValueNumber();
        	buf.append(value).append("\n");
        }
        /**~~~~~~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~*/
        else if("CY".equalsIgnoreCase(getJYSBySecurityCode(this.securityCode))
        		|| "CS".equalsIgnoreCase(getJYSBySecurityCode(this.securityCode))
        		|| "CG".equalsIgnoreCase(getJYSBySecurityCode(this.securityCode))){
        	buf.append("v_value number(30,15);").append("\n");
        /*added by yeshenghong 2013-5-18 Story 3759 */
        } else if(this instanceof LeverGradeFundCfg){//增加对杠杆基金的处理
        	buf.append("v_value number(30,15);").append("\n");
        }
		/*end by yeshenghong 2013-5-18 Story 3759 */
        else{
        	//add by songjie 2010.01.14 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        	buf.append("v_value number(24,4);").append("\n");
        	//add by songjie 2010.01.14 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        }
        //add by songjie 2010.01.14 QDII国内：MS00847 QDV4赢时胜（北京）2009年11月30日03_B 用于判断是否为国内接口部分的调用-----//
        buf.append("begin").append("\n");

        buf.append(CalcFormula).append("\n");
        buf.append("return v_value;").append("\n");
        buf.append("end;");
        //add by fangjiang stroy 1713 2012.02.01
        if(buf.indexOf("cursor")>1){
        	buf.append("end;");
        }
        //-----------end stroy 1713------------
        calcStr = buf.toString();
        buf.delete(0, buf.length());
        return replaceSqlRs(calcStr);
    }

    /** 查询通用业务参数获取保留位数
     * shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数**/
    private String _getValueNumber() throws YssException {
    	ParaWithPubBean para = new ParaWithPubBean();
    	para.setYssPub(pub);
    	ResultSet groupRs = null;//所有组
    	ResultSet curyRs = null;//组下对应币种
    	ResultSet valueRs = null;//币种下对应保留位数
    	String FCtlValue = "";//保留位数
    	String value = "v_value number(24,4);";//最后返回值,最先默认4位
    	try {
    		//组合下所有参数
    		groupRs = para.getResultSetByLike("SettingDecimal","portSel","%"+this.FPortCode+"|%",null);
        	while (groupRs.next()) {
        		try {
        			//组合下币种参数
        			curyRs = para.getResultSetByLike("SettingDecimal", "SetCurrency", "%"+this.FCuryCode+"|%", groupRs.getString("FParaId"));
    				if (curyRs.next()) {
    					//组合下(有)币种的保留位数
    					valueRs = para.getResultSetByLike("SettingDecimal", "DecimalPlaces", "%", curyRs.getString("FParaId"));
    					if(valueRs.next()){
	    					FCtlValue = valueRs.getString("FCtlValue");
	    					value = "v_value number(38,"+FCtlValue.split(",")[0]+");";
	    					return value;
    					}
					} 
    				dbl.closeResultSetFinal(curyRs);
    				dbl.closeResultSetFinal(valueRs);
    				//如果组合下没有这个币种,则查找组合下没有设置币种的参数
    				if (FCtlValue.trim().equals("") && groupRs.isLast()) {
    					curyRs = para.getResultSetByLike("SettingDecimal", "SetCurrency", "|", null);
    					if (curyRs.next()) {
    						//有组合无币种的保留位数
    						valueRs = para.getResultSetByLike("SettingDecimal", "DecimalPlaces", "%", curyRs.getString("FParaId"));
    						if (valueRs.next()) {
    							FCtlValue = valueRs.getString("FCtlValue");
        						value = "v_value number(38,"+FCtlValue.split(",")[0]+");";
            					return value;
							}
						}
					}
    				dbl.closeResultSetFinal(curyRs);
    				dbl.closeResultSetFinal(valueRs);
				} catch (Exception e) {
					throw new YssException("查通用业务参数保留位数出错！");
				}
				//dbl.closeResultSetFinal(curyRs,valueRs);
			}
		} catch (Exception e) {
			throw new YssException("配置远期计算公式出错！", e);
		}finally {
            dbl.closeResultSetFinal(groupRs,curyRs,valueRs);
        }
		return value;
	}

	/**
     * 替换函数中的一些标示或一些错误字符。
     * @param calcStr String
     * @return String
     */
    private String replaceSqlRs(String calcStr) {
        String[] fStr = null;
        StringBuffer buf = new StringBuffer();
        String tempStr = "";
        String tChar = "";
        String reStr = "";
        String tabName = "Tb_Sys_UserList";
        reStr = calcStr.replaceAll("]",
                                   " as val into v_value from " + tabName); //将值放入返回的参数中
        reStr = reStr.replaceAll("sqlRs", "select distinct "); //固定返回一个值。
        reStr = reStr.replace('[', ' ');
        reStr = reStr.replaceAll("zero",
                                 "select distinct 0 as val into v_value from " + tabName + " "); //默认返回值。
        //reStr = reStr.replaceAll(""," ");
        fStr = reStr.split(";", -1); //为了去掉一些不知所谓的字符。
        for (int subStr = 0; subStr < fStr.length - 1; subStr++) {
            tempStr = fStr[subStr];
            tempStr = tempStr.trim();
            if (tempStr.lastIndexOf(";") < 0) {
                //tempStr.substring(0,tempStr.length());
                buf.append(tempStr).append(";");
            }

        }
        reStr = buf.toString();
        buf.delete(0, buf.length());
        fStr = reStr.split("then", -1); //为了去掉一些不知所谓的字符。
        for (int subStr = 0; subStr < fStr.length; subStr++) {
            tempStr = fStr[subStr];
            tempStr = tempStr.trim();
            if (subStr < fStr.length - 1) {
                buf.append(tempStr).append(" then ");
            } else {
                buf.append(tempStr);
            }
        }
        reStr = buf.toString();
        reStr = reStr.replaceAll("\r\n", "  "); //在数据库中不能辨别\r\n这样的字符。所以把它转换。sj edit 20080919 暂无bug.
        return reStr;
    }
    
    private String getJYSBySecurityCode(String securityCode) throws YssException{
       ResultSet rs = null;
  	   String sqlStr= "";
  	   String result = ""; 
  	   try{
  		   sqlStr = " select FExchangeCode from "+pub.yssGetTableName("Tb_Para_Security")+
  		            " where FCheckState = 1 and FSecurityCode = " + dbl.sqlString(securityCode);
  		   rs =dbl.openResultSet(sqlStr);
  		   if(rs.next()){
  			   result = rs.getString("FExchangeCode");
  		   }
  	   }catch(Exception ex){
  		   throw new YssException(ex.getMessage());
  	   }finally{
  		   dbl.closeResultSetFinal(rs);
  	   }
  	   return result;
    }
    

}
