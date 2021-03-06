package com.yss.main.operdeal.report.platform.valcompare;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.Connection;

import com.yss.base.BaseAPOperValue;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.operdeal.report.platform.valcompare.ValCompFormula;
import com.yss.main.operdeal.report.platform.valcompare.pojo.ValCompDataBean;
import com.yss.main.platform.pfsystem.valcompare.ValCompareBean;
import com.yss.util.YssFun;
import java.util.HashMap;
import java.util.Set;
//add by xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
import java.sql.Clob;
import java.io.Reader;

//-----------------------------end-------------------------------//
/**
 * <p>Title: 生成财务估值核对结果</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ValCompareResult
    extends BaseAPOperValue {

    private java.util.Date compareDate; //核对时间
    private String portCode; //组合代码
    private String comProjectCode; //核对方案代码
    private boolean bIsCashCost; //是否比对现金类组合货币成本 true 为不比对

    public ValCompareResult() {
    }

    /**
     * 解析输入参数
     * @param bean Object
     * @throws YssException
     */
    public void init(Object bean) throws YssException {
        String[] sParams = ( (String) bean).split("\n");
        if (sParams.length == 0) {
            return;
        }
        this.compareDate = YssFun.toDate(sParams[0].split("\r")[1]);
        this.portCode = sParams[1].split("\r")[1];
        this.comProjectCode = sParams[2].split("\r")[1];
        //2008-12-11 蒋锦 修改 修改 bool 值的转换 编号：MS00071
        this.bIsCashCost = (new Boolean(sParams[3].split("\r")[1]).booleanValue());
    }

    public Object invokeOperMothed() throws YssException {
        try {
            doCompare();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return null;
    }

    /**
     * 执行核对
     * @return Object
     * @throws YssException
     */
    public Object doCompare() throws YssException {
        ValCompareBean valComp = null;
        ValCompFormula valFormula = null;
        ArrayList alCompResult = new ArrayList();
        Object objGZResult = null;
        Object objCWResult = null;
        String[] arrScript = null;
        String[] arrFunList = null;
        try {
            valComp = getValCompareInfo(this.comProjectCode);
            if (valComp == null) {
                throw new YssException("没有找到核对方案“" + this.comProjectCode + "”， 请确认核对方案是否被审核！");
            }
            arrScript = valComp.getComScript().split("\r\n");
            for (int i = 0; i < arrScript.length; i++) {
                arrFunList = arrScript[i].split("linked");
                if (arrFunList.length < 2) {
                    throw new YssException("脚本语法错误，请核对！");
                }
                valFormula = new ValCompFormula();
                valFormula.setYssPub(pub);
                valFormula.setCompareDate(this.compareDate);
                valFormula.setPortCode(this.portCode);
                valFormula.setCompProCode(this.comProjectCode);
                //估值
                valFormula.setScript(arrFunList[0].trim());
                objGZResult = valFormula.calcFormulaString();
                //财务
                valFormula.setScript(arrFunList[1].trim());
                objCWResult = valFormula.calcFormulaString();
                alCompResult.addAll(funResultCompare(objGZResult, objCWResult));
            }
            saveCompResult(alCompResult);
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return null;
    }

    /**
     * 根据核对方案代码获取和对方案的详细信息
     * @param projectCode: 方案代码
     * @return ValCompareBean: 核对方案设置的实体类
     * @throws YssException
     */
    public ValCompareBean getValCompareInfo(String projectCode) throws YssException {
        ValCompareBean valComp = null;
        String sqlStr = "";
        ResultSet rs = null;
        try {
            sqlStr = "SELECT FComProjectCode, FComProjectName, FComScript FROM TB_PFSys_ValCompare" +
                " WHERE FCheckState = 1 AND" +
                " FComProjectCode = " + dbl.sqlString(projectCode);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                valComp = new ValCompareBean();
                valComp.setComProjectCode(rs.getString("FComProjectCode"));
                valComp.setComProjectName(rs.getString("FComProjectName"));
                //add by xuqiji 20090611 QDV4建行2009年6月9日01_B MS00490 财务估值核对脚本下拉条不起作用
                Clob clob = (Clob) rs.getClob("FComScript");
                char[] c = new char[1024 * 1024 * 10];
                /* 读取CLOB字段内容到缓冲区 */
                int n = clob.getCharacterStream().read(c);
                /* 将读取的CLOB字段内容转换为字符串值 */
                valComp.setComScript(new String(c, 0, n));
                //----------------end---------------------------------------------//
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return valComp;
    }

    /**
     * 进行财务和估值的比对，part 函数使用哈希表的建进行比对，one 和 sum 函数直接合并数据
     * @param objGZ Object：装载净值表数据的 HashMap（part 函数） 或者 ArrayList（sum 和 one 函数）
     * @param objCW Object：装载财务估值表数据的 HashMap（part 函数） 或者 ArrayList（sum 和 one 函数）
     * @return ArrayList：比对完成数据合并好的 ArrayList
     * @throws YssException
     */
    public ArrayList funResultCompare(Object objGZ, Object objCW) throws YssException {
        ArrayList alCompData = new ArrayList();
        try {
            if (objGZ instanceof HashMap && objCW instanceof HashMap) {
                Set gzSet = ( (HashMap) objGZ).keySet();
                Object[] arrGZKey = gzSet.toArray();
                for (int i = 0; i < arrGZKey.length; i++) {
                    ValCompDataBean valData = (ValCompDataBean) ( (HashMap) objCW).get( (String) arrGZKey[i]);
                    if (valData != null) {
                        ValCompDataBean valGZData = (ValCompDataBean) ( (HashMap) objGZ).get( (String) arrGZKey[i]);
                        valGZData.setCwAmount(valData.getCwAmount());
                        valGZData.setCwCost(valData.getCwCost());
                        valGZData.setCwKeyCode(valData.getCwKeyCode());
                        valGZData.setCwKeyName(valData.getCwKeyName());
                        valGZData.setCwMarketValue(valData.getCwMarketValue());
                        valGZData.setCwPortCost(valData.getCwPortCost());
                        valGZData.setCwPortMarketValue(valData.getCwPortMarketValue());
                        ( (HashMap) objCW).remove( (String) arrGZKey[i]);
                    }
                }
                alCompData.addAll( ( (HashMap) objGZ).values());
                alCompData.addAll( ( (HashMap) objCW).values());
            } else if (objGZ instanceof ValCompDataBean && objCW instanceof ValCompDataBean) {
                ValCompDataBean valData = (ValCompDataBean) objGZ;
                ValCompDataBean valCWData = (ValCompDataBean) objCW;
                valData.setCwAmount(valCWData.getCwAmount());
                valData.setCwCost(valCWData.getCwCost());
                valData.setCwKeyCode(valCWData.getCwKeyCode());
                valData.setCwKeyName(valCWData.getCwKeyName());
                valData.setCwMarketValue(valCWData.getCwMarketValue());
                valData.setCwPortCost(valCWData.getCwPortCost());
                valData.setCwPortMarketValue(valCWData.getCwPortMarketValue());
                alCompData.add(valData);
            } else {
                throw new YssException("函数语法错误！");
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return alCompData;
    }
    /**
     * add by huhuichao 2013-07-30 story STORY 4254 不比对运营费用类本位币成本
     * 构造   将 ValCompDataBean 中的数据存入数据库 的 StringBuffer字符串
     * @param ValCompDataBean compData
     * @param StringBuffer bufSql
     * @param int i
     * @param int iDigit
     * @throws YssException
     */
	public StringBuffer buildStringBuffer(ValCompDataBean compData,
			StringBuffer bufSql, int i, int iDigit) throws YssException {
		try {
			bufSql.append(dbl.sqlString(this.portCode)).append(",");
			bufSql.append(dbl.sqlString(this.comProjectCode)).append(",");
			bufSql.append(
					dbl.sqlString(compData.getGzKeyCode().length() == 0 ? " "
							: compData.getGzKeyCode())).append(",");

			bufSql.append(
					dbl.sqlString(compData.getGzKeyName().length() == 0 ? " "
							: compData.getGzKeyName())).append(",");

			bufSql.append(
					dbl.sqlString(compData.getCwKeyCode().length() == 0 ? " "
							: compData.getCwKeyCode())).append(",");

			bufSql.append(
					dbl.sqlString(compData.getCwKeyName().length() == 0 ? " "
							: compData.getCwKeyName())).append(",");
			bufSql.append(compData.getGzCost()).append(",");
			bufSql.append("0").append(",");
			bufSql.append(compData.getGzMarketValue()).append(",");
			if ("AccumulateUnit".equals(compData.getGzKeyCode())
					|| "Unit".equals(compData.getGzKeyCode())) {
				// BUG #3880 :: 财务估值核对结果中累计净值及累计单位净值两边数据部一致
				// 单位净值 、累计单位净值需要考虑通参中的设置的保留位数
				bufSql.append(
						YssD.round(compData.getGzPortMarketValue(), iDigit))
						.append(",");
			} else {
				bufSql.append(compData.getGzPortMarketValue()).append(",");
			}
			bufSql.append(compData.getCwCost()).append(",");
			bufSql.append("0").append(",");
			bufSql.append(compData.getCwMarketValue()).append(",");
			bufSql.append(compData.getCwPortMarketValue()).append(",");
			bufSql.append(compData.getGzAmount()).append(",");
			bufSql.append(compData.getCwAmount()).append(",");
			bufSql.append(i).append(")");
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		}
		return bufSql;
	}
    
    /**
     * 将 ValCompDataBean 中的数据存入数据库
     * @param alCompResult ArrayList：装载 ValCompDataBean 的 ArrayList
     * @throws YssException
     */
    public void saveCompResult(ArrayList alCompResult) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String strSql = "";
        Connection conn = dbl.loadConnection();
        ValCompDataBean valComp = null;
        boolean bTrans = false;
        
		int iDigit = 0; // 累计净值保留位数
		CtlPubPara ctlPara = null; // 通用参数
		
        try {
        	
        	//BUG #3880 :: 财务估值核对结果中累计净值及累计单位净值两边数据部一致
        	// 通过通用参数来获取累计净值应该保留的小数位数
			ctlPara = new CtlPubPara();
			ctlPara.setYssPub(pub);
			iDigit = Integer.parseInt(ctlPara.getCashUnit(this.portCode));
			
            conn.setAutoCommit(false);
            bTrans = true;
            if (alCompResult.size() > 0) {
                ValCompDataBean compDeleteData = (ValCompDataBean) alCompResult.get(
                    0);
                strSql = "DELETE FROM " +
                    pub.yssGetTableName("TB_PFOper_ValCompData") +
                    " WHERE FPortCode = " + dbl.sqlString(this.portCode) +
                    " AND FComProjectCode = " +
                    dbl.sqlString(this.comProjectCode);
                dbl.executeSql(strSql);
            }
            for (int i = 0; i < alCompResult.size(); i++) {
                ValCompDataBean compData = (ValCompDataBean) alCompResult.get(i);
                if (compData.getGzKeyCode().length() == 0 && compData.getCwKeyCode().length() == 0) {
                    continue;
                }
                bufSql.append("INSERT INTO " +
                              pub.yssGetTableName("TB_PFOper_ValCompData" +
                //modify by zhangfa MS01853    财务估值核对因两个字段错位，插入数据时报主键冲突错    QDV4赢时胜深圳2010年10月12日01_B
                "(FPortCode,FCOMPROJECTCODE,FGZKEYCODE,FGZKEYNAME,FCWKEYCODE,FCWKEYNAME,FGZCOST," +
                "FGZPORTCOST,FGZMARKETVALUE,FGZPORTMARKETVALUE,FCWCOST,FCWPORTCOST,FCWMARKETVALUE," +
                "FCWPORTMARKETVALUE,FGZAMOUNT,FCWAMOUNT,FORDER)"       )      + 
                //------------------------------------------------------------------------------------------------------------              
                              " VALUES(");
                //2008.06.12 蒋锦 添加判断是否比对现金类项目的组合货币成本
				if (compData.getGZReTypeCode() != null
						&& compData.getGZReTypeCode().equalsIgnoreCase("cash")
						&& this.bIsCashCost) {
					/**add---huhuichao 2013-7-30 STORY  4254  不比对运营费用类本位币成本*/
					this.buildStringBuffer(compData, bufSql, i, iDigit);
				} else if (compData.getGZReTypeCode() != null
						&& compData.getGZReTypeCode()
								.equalsIgnoreCase("Invest") && this.bIsCashCost) {
					this.buildStringBuffer(compData, bufSql, i, iDigit);
					/**end---huhuichao 2013-7-30 STORY  4254*/
                }else {
                    bufSql.append(dbl.sqlString(this.portCode)).append(",");
                    bufSql.append(dbl.sqlString(this.comProjectCode)).append(",");
                    bufSql.append(dbl.sqlString(compData.getGzKeyCode().length() ==
                                                0 ? " " : compData.getGzKeyCode())).
                        append(",");
                    
                    bufSql.append(dbl.sqlString(compData.getGzKeyName().length() ==
                        0 ? " " : compData.getGzKeyName())).append(",");
                    bufSql.append(dbl.sqlString(compData.getCwKeyCode().length() ==
                        0 ? " " : compData.getCwKeyCode())).append(",");
                
                    bufSql.append(dbl.sqlString(compData.getCwKeyName().length() ==
                                                0 ? " " : compData.getCwKeyName())).
                        append(",");
                    bufSql.append(compData.getGzCost()).append(",");
                    bufSql.append(compData.getGzPortCost()).append(",");
                    bufSql.append(compData.getGzMarketValue()).append(",");
                    
                    if("AccumulateUnit".equals(compData.getGzKeyCode()) || "Unit".equals(compData.getGzKeyCode())){
                    	//BUG #3880 :: 财务估值核对结果中累计净值及累计单位净值两边数据部一致 
                    	//单位净值 、累计单位净值需要考虑通参中的设置的保留位数
                    	bufSql.append(YssD.round(compData.getGzPortMarketValue(),iDigit)).append(",");
                    }else{
                    	bufSql.append(compData.getGzPortMarketValue()).append(",");
                    }    
                    
                    bufSql.append(compData.getCwCost()).append(",");
                    bufSql.append(compData.getCwPortCost()).append(",");
                    bufSql.append(compData.getCwMarketValue()).append(",");
                    bufSql.append(compData.getCwPortMarketValue()).append(",");
                    bufSql.append(compData.getGzAmount()).append(",");
                    bufSql.append(compData.getCwAmount()).append(",");
                    bufSql.append(i).append(")");
                }
                dbl.executeSql(bufSql.toString());
                bufSql.delete(0, bufSql.length());
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("保存核对结果出错！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
