package com.yss.main.operdeal.platform.pfoper.pubpara;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 *
 * <p>Title:取通用控件值的公共类 </p>
 *
 * <p>Description:MS00018 </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ParaWithPubBean
    extends BaseBean {

    //private String paraGroupCode = "";
    private String pubParaCode = "";
    //private String ctlGrpCode = "";
    private String ctlCode = "";
    private String ctlInd = "";
    private String params = "";

    private String ResultCtlInd = "";

    public String getCtlCode() {
        return ctlCode;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public void setCtlCode(String ctlCode) {
        this.ctlCode = ctlCode;
    }

    public void setCtlInd(String ctlInd) {
        this.ctlInd = ctlInd;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public void setResultCtlInd(String ResultCtlInd) {
        this.ResultCtlInd = ResultCtlInd;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getCtlInd() {
        return ctlInd;
    }

    public String getParams() {
        return params;
    }

    public String getResultCtlInd() {
        return ResultCtlInd;
    }

    public ParaWithPubBean() {
    }

    /**
     * 返回特定的控件值 选择控件的值
     * @return Object
     * @throws YssException
     */
    public Object getSelectParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId order by FParaID desc"; //,FParaId";  //modify huangqirong 2013-04-18 bug #7476 取参数ID最大的那个
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValue = ""; //恢复初始值.
                isCheck = false;
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue");
                    }
//             if (rsTest.getString("FCtlInd")!=null&&rsTest.getString("FCtlInd").equalsIgnoreCase(ResultCtlInd.trim().length() > 0?ResultCtlInd:ctlInd)
//                 &&(params.length()>0?
//                 rsTest.getString("FCtlValue").split("[|]")[0].equalsIgnoreCase( //如果满足条件
//                       params):true)) {
//                isCheck = true;
//             } //采用下面的代码方法判断数据，增加checkResult()方法 by leeyu 2009-01-04 MS00125
                    if (rsTest.getString("FCtlInd") != null && rsTest.getString("FCtlInd").equalsIgnoreCase(ResultCtlInd.trim().length() > 0 ? ResultCtlInd : ctlInd)//edit by lidaolong 20110317 #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
                        && (params.length() > 0 ?
                            checkResult(rsTest.getString("FCtlValue").split("[|]")[0], params) : true)) { //用此方法判断RS有多个参数的情况，这时就要将RS拆分了再比较 by leeyu 2009-01-04 MS00125
                        isCheck = true;
                    }
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            if (reStr.length() == 0) {
                return null;
            } else {
                return reStr;
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }

    /**
     * 返回特定的控件值 文本框控件的值
     * @return Object
     * @throws YssException
     */
    public Object getTextParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValue = ""; //恢复初始值.
                isCheck = false;
                sqlStr =
                    //edited by zhouxiang MS01612    外汇交易算法变更    //判断设置为空的情况
                	"select para.*,(case when face.FCtlInd is null then ' ' else face.FCtlInd end ) as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    //-----------------end-------20100903-------------
                	" from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode") != null && rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue");
                    }
					

						if (rsTest.getString("FCtlInd").equalsIgnoreCase(ctlInd)) { // 如果满足条件
							isCheck = true;
						}
					

                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            if (reStr.length() == 0) {
                return null;
            } else {
                return reStr;
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }
    }

    /**
     * 返回特定的控件值 下垃框控件的值
     * @return Object
     * @throws YssException
     */
    public Object getComboxParaResult() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValue = "";
        boolean isCheck = false;
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValue = ""; //恢复初始值.
                isCheck = false;
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode") != null && rsTest.getString("FCtlCode").equalsIgnoreCase(this.
                        ctlCode)) { //如果是需要返回的控件，则先将控件值保存
                        resultValue = rsTest.getString("FCtlValue").split(",")[0];
                    }
                    if (rsTest.getString("FCtlInd") != null && rsTest.getString("FCtlInd").equalsIgnoreCase(ctlInd)) { //如果满足条件
                        isCheck = true;
                    }
                    if (resultValue.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                        reStr = resultValue;
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            if (reStr.length() == 0) {
                return null;
            } else {
                return reStr;
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }

    /**
     * 返回特定的控件值 日期控件的值
     * @return Object
     * @throws YssException
     */
    public Object getDateParaResult() throws YssException {
        return getTextParaResult();
    }

    /**
     * 获取指定控件参数值最新值（编号最大）
     * 使用前要先设置参数编号、控件编号
     * @return Object 如果什么都没获取到，返回“”
     * @throws YssException
     * @version sunkey 20081208 BugNO:MS00051
     */
    public Object getLatestParaValue() throws YssException {
        String returnValue = "";
        StringBuffer bufSql = new StringBuffer();
        ResultSet rs = null;
        //按照参数编号、参数组编号、控件组编号，查询控件值最大的参数信息
        bufSql.append("SELECT FCTLVALUE FROM ")
            .append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE ='" + this.pubParaCode + "'")
            .append(" AND FCTLCODE = '" + this.ctlCode + "'")
            .append(" AND FPARAID = (SELECT MAX(FPARAID)")
            .append(" FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = '" + this.pubParaCode + "'")
            .append(" AND FCTLCODE = '" + this.ctlCode + "'")
            .append(")");
        try {
            rs = dbl.openResultSet(bufSql.toString());
            if (rs.next()) {
                returnValue = rs.getString("FCTLVALUE");
            }
            dbl.closeStatementFinal(rs.getStatement());
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return returnValue;

    }

    /**MS00125
     * 检查参数Param是否在参数列表sParams中，若在则返回true,不在返回false
     * @param sParams String 参数列表
     * @param param String
     * @return boolean
     * @throws YssException
     */
    private boolean checkResult(String sParams, String param) throws YssException {
        boolean bCheck = false;
        String[] arrParam = null;
        try {
            if (sParams.indexOf(",") > -1) { //采用“，”分隔的
                arrParam = sParams.split(",");
            } else if (sParams.indexOf("\t") > -1) { //采用“\t”分隔的
                arrParam = sParams.split("\t");
            } else { //这种情况是无任何分隔符的那种，就直接比较
                arrParam = new String[1];
                arrParam[0] = sParams;
            }
            for (int i = 0; i < arrParam.length; i++) {
                if (arrParam[i].equalsIgnoreCase(param)) {
                    bCheck = true;
                    return bCheck; //若有则直接返回
                }
            }
        } catch (Exception ex) {
            throw new YssException("分解参数失败");
        }
        return bCheck;
    }
    
    /**
     * 期权保证金结转方式
     * 2010-04-22 蒋锦 添加 期货保证金结转类型
     * MS01134 增加股票期权和股指期权业务
     * 南方东英期权业务需求
     * @return Object
     * @throws YssException
     */
    public Object getSelectOpationBailType() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValueType = "";
        String resultValuePort = "";
        boolean isCheck = false;
        String [] sPortCode = null;//前台可能传多个组合代码，格式用逗号隔开 
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValueType = ""; //恢复初始值.
                resultValuePort = "";//恢复初始值.
                isCheck = false;
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase("cboCarryType")) { //获取结转类型代码
                        resultValueType = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase("selPortCode")) { //获取结转类型对应的组合代码
                        resultValuePort = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlInd") != null && rsTest.getString("FCtlInd").equalsIgnoreCase(ctlInd)) {
                        isCheck = true;
                    }
                    if (resultValueType.length()>0&&resultValuePort.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                    	sPortCode = resultValuePort.split("[|]")[0].split(",");
                    	for(int i =0;i < sPortCode.length;i++){
                    		reStr += resultValueType.split(",")[0]+"|"+sPortCode[i]+",";//结果如：MODAVG|001,MODAVG|002,
                    	}
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            if (reStr.length() == 0) {
                return null;
            } else {
                return reStr;
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }
    
    //=========add by xuxuming,20091223.MS00886,无法用不同的方法对不同品种进行核算成本==========
    //原来的方法getSelectParaResult，只能获取最后一条记录的值。在对结果集循环时，resultValue值被后续的
    //值给覆盖了，故只能返回一条记录的值。当在获取股指期货核算方法时，有多组合时，只能获取到
    //最后一个组合的核算方法，其它组合的核算方法都没有获取到。
    //但为了不影响其它地方，故没有修改原来的方法，而是在些新写了一个方法。
    //很怀疑此类中获取选择控件值的几个方法的正确性,下一次while循环取出的值取代上一次的值，这明显不是程序的初衷
    /**
     * 返回股指期货类型的核算方法
     * @return Object
     * @throws YssException
     */
    public Object getSelectFurAccType() throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rsTest = null;
        ResultSet grpRs = null;
        String resultValueType = "";
        String resultValuePort = "";
        boolean isCheck = false;
        String [] sPortCode = null;//前台可能传多个组合代码，格式用逗号隔开  xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
        try {
            sqlStr = "select FParagroupCode,FPubParaCode,FParaId from " +
                pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaId <> 0" +
                " group by  FParagroupCode,FPubParaCode,FParaId";
            grpRs = dbl.openResultSet(sqlStr);
            while (grpRs.next()) {
                resultValueType = ""; //恢复初始值.
                resultValuePort = "";//恢复初始值.
                isCheck = false;
                sqlStr =
                    "select para.*,face.FCtlInd as FCtlInd,face.FCtlType as FCtlType from (select *" +
                    " from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
                    " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                    " and FParaId = " +
                    grpRs.getInt("FParaId") +
                    ") para left join (select * from Tb_PFSys_FaceCfgInfo where FCheckState = 1) face on " +
                    "  para.FCtlGrpCode = face.FCtlGrpCode and para.FCtlCode = face.FCtlCode";
                rsTest = dbl.openResultSet(sqlStr);
                while (rsTest.next()) {
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase("Combo")) { //获取核算方法代码
                        resultValueType = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlCode").equalsIgnoreCase("PortCode")) { //获取核算方法对应的组合代码
                        resultValuePort = rsTest.getString("FCtlValue");
                    }
                    if (rsTest.getString("FCtlInd") != null && rsTest.getString("FCtlInd").equalsIgnoreCase(ctlInd)) {
                        isCheck = true;
                    }
                    if (resultValueType.length()>0&&resultValuePort.length() > 0 && isCheck) { //如果需要返回的控件的控件值有值,而且满足条件
                    	//--------xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务---------//
                    	sPortCode = resultValuePort.split("[|]")[0].split(",");
                    	for(int i =0;i < sPortCode.length;i++){
                    		reStr += resultValueType.split(",")[0]+"|"+sPortCode[i]+",";//结果如：MODAVG|001,MODAVG|002,
                    	}
                    	//-------------------------------end-------------------------------------//
                        break;
                    }
                }
                dbl.closeResultSetFinal(rsTest);
            }
            if (reStr.length() == 0) {
                return null;
            } else {
                return reStr;
            }
        } catch (Exception e) {
            throw new YssException("获取公共参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(grpRs);
            dbl.closeResultSetFinal(rsTest);
        }

    }
    //==============end,xuxuming,20091223=================================

    /**shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
     *@param FCtlGrpCode 控件组编号
     *@param FCtlCode	 控件编号
     *@param FCtlValue   控件值(值,下标)
     *@param FParaId     参数值编号(排序)可为空
     * */
	public ResultSet getResultSetByLike(String FCtlGrpCode, String FCtlCode,
			String FCtlValue, String FParaId) throws YssException {
		String sqlStr = "";
        ResultSet rs = null;
		try {
			sqlStr = "SELECT * FROM " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +" "+
						"WHERE FCtlGrpCode = "+dbl.sqlString(FCtlGrpCode)+" " +
						"AND FCtlCode = "+dbl.sqlString(FCtlCode)+ " "+
						"AND FCtlValue LIKE "+dbl.sqlString(FCtlValue) + " " ;
			if (FParaId!=null) {
				sqlStr += " AND FParaId = " + dbl.sqlString(FParaId) ;
			}
			rs = dbl.openResultSet(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE);
			
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
            throw new YssException("获取公共参数出错！", e);
        } finally {
            //dbl.closeResultSetFinal(grpRs);
        }
		return rs;
	}
	
	
	
	//add by fangjiang 2011.10.26 STORY #1589
	public ResultSet getResultSetByLike(String FCtlGrpCode, String FParaId) throws YssException {
		String sqlStr = "";
        ResultSet rs = null;
		try {
			sqlStr = " select * from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") +
				     " where FCtlGrpCode = "+ dbl.sqlString(FCtlGrpCode) +
				     " and FParaId in (" + operSql.sqlCodes(FParaId) + ") order by FParaId";
			rs = dbl.openResultSet(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE);			
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
            throw new YssException("获取公共参数出错！", e);
        } 
		return rs;
	}
	
	/** shashijie 2012-9-26 BUG 5814 */
    public ResultSet getResultSetByLike(String Fpubparacode,String Fparagroupcode,
    		String Fctlgrpcode,String Fctlcode1,String Fctlvalue,String Fctlcode2) 
    		throws YssException {
    	String sqlStr = "";
        ResultSet rs = null;
		try {
			sqlStr = " Select * From "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+
				" Where Fpubparacode = "+dbl.sqlString(Fpubparacode)+
				" And Fparagroupcode = "+dbl.sqlString(Fparagroupcode)+
				" And Fctlgrpcode = "+dbl.sqlString(Fctlgrpcode)+
				" And Fparaid = (Select Distinct (Max(Fparaid))" +
				" From "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+
                " Where Fpubparacode = "+dbl.sqlString(Fpubparacode)+
			    " And Fparagroupcode = "+dbl.sqlString(Fparagroupcode)+
			    " And Fctlgrpcode = "+dbl.sqlString(Fctlgrpcode)+
                " And Fctlcode = "+dbl.sqlString(Fctlcode1)+
			    " And Fctlvalue Like "+dbl.sqlString(Fctlvalue)+" ) "+
			    " And Fctlcode = "+dbl.sqlString(Fctlcode2)+
			    "";
			rs = dbl.openResultSet(sqlStr,ResultSet.TYPE_SCROLL_INSENSITIVE);			
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
            throw new YssException("获取公共参数出错！", e);
        } 
		return rs;
	}
}
