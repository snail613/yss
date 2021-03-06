package com.yss.main.operdeal.report.repfix.Chinabank;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**shashijie 2012-04-12 STORY 2472 中行,配置新报表，统计产品某日证券的账面库存持仓及实际交割持仓并优化证券的排序*/
public class PracticeSecurity extends BaseBuildCommonRep {
    
    private CommonRepBean repBean;//报表对象
    
    private String FStartDate = "";//日期
    private String FPortCode = "";//组合代码(多选)
    private String FSecurityCode = "";//证券代码
    
    private FixPub fixPub = null;//获取基金成立日那天的金额
    public PracticeSecurity() {
    }

	/**初始数据方法 shashijie 2012-4-12 STORY 2472*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//日期
        if(reqAry.length>1){
        	if (reqAry[1].split("\r")[0].equals("2")) {
        		FPortCode = reqAry[1].split("\r")[1];//组合
			}else {
				FSecurityCode = reqAry[1].split("\r")[1];//证券代码
			}
        }
        if (reqAry.length>2) {
        	FSecurityCode = reqAry[2].split("\r")[1];//证券代码
		}
        
        fixPub = new FixPub();
        fixPub.setYssPub(pub);
    }
    
    /**程序入口 shashijie 2012-4-12 STORY 2472*/
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        
        //获取报表内容
        sResult += getInfo();
        
        return sResult;
    }

	/**shashijie 2012-4-12 STORY 2472 */
	private String getInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		try {
			String query = getSql();
			rs = dbl.openResultSet(query);
			while (rs.next()) {
				//计算已交割的值
				String FAmount = getFAmount(rs.getBigDecimal("Fstorageamount"),
						rs.getBigDecimal("Ftradeamount1"),rs.getBigDecimal("Ftradeamount2"));
				String Fstoragecost = getFAmount(rs.getBigDecimal("Fstoragecost"), 
						rs.getBigDecimal("Fcost1"), rs.getBigDecimal("Fcost2"));
				String Fbasecurycost = getFAmount(rs.getBigDecimal("Fbasecurycost"), 
						rs.getBigDecimal("Fbasecurycost1"), rs.getBigDecimal("Fbasecurycost2"));
				String Fportcurycost = getFAmount(rs.getBigDecimal("Fportcurycost"), 
						rs.getBigDecimal("Fportcurycost1"), rs.getBigDecimal("Fportcurycost2"));
				//填充单元格
				str += operionStr(YssFun.formatDate(rs.getDate("Fstoragedate")), 
						rs.getString("Fportcode"), rs.getString("Fportname"), 
						rs.getString("Fsecuritycode"), rs.getString("FSecurityName"), rs.getString("Fexchangecode"), 
						rs.getString("Fcurycode"), rs.getString("Fcuryname"), 
						rs.getBigDecimal("Fbasecuryrate").toString(),//基础汇率
						rs.getBigDecimal("Fportcuryrate").toString(),//组合汇率
						rs.getString("Fstorageamount"), rs.getString("Fstoragecost"), 
						rs.getString("Fbasecurycost"), rs.getString("Fportcurycost"), FAmount, Fstoragecost, 
						Fbasecurycost, Fportcurycost);
				
			}
		} catch (Exception e) {
			throw new YssException("填充内容错误!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		
		return str;
	}

	/**shashijie 2012-4-13 STORY 2472 计算已交割的值,1-2-3
	* @param double1
	* @param double2
	* @param double3
	* @return*/
	private String getFAmount(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
		BigDecimal value = YssD.subD(v1, v2, v3);
		return value.toString();
	}

	/**shashijie 2012-4-13 STORY 2472
	* @return*/
	private String getSql() {
		String query = " Select  b.Fstoragedate, b.Fportcode, C1.Fportname, b.Fsecuritycode, D1.FSecurityName,"+
			" D1.Fexchangecode, b.Fcurycode, E1.Fcuryname, b.Fbasecuryrate, b.Fportcuryrate, b.Fstorageamount," +
			" b.Fstoragecost, b.Fbasecurycost, b.Fportcurycost, Nvl(A1.Ftradeamount, 0) As Ftradeamount1, " +
			" Nvl(A1.Fcost, 0) As Fcost1, Nvl(A1.Fbasecurycost, 0) As Fbasecurycost1," +
			" Nvl(A1.Fportcurycost, 0) As Fportcurycost1, Nvl(F1.Ftradeamount, 0) As Ftradeamount2," +
			" Nvl(F1.Fcost, 0) As Fcost2, Nvl(F1.Fbasecurycost, 0) As Fbasecurycost2, " +
			" Nvl(F1.Fportcurycost, 0) As Fportcurycost2 From "+pub.yssGetTableName("Tb_Stock_Security")+" b" +
			" Left Join (Select c.Fportcode, c.Fportname From "+pub.yssGetTableName("Tb_Para_Portfolio")+
			" c) C1 On b.Fportcode = C1.Fportcode" +
			" Left Join (Select d.Fsecuritycode, d.Fsecurityname, d.Fexchangecode From "+
			pub.yssGetTableName("Tb_Para_Security")+" d) D1 On b.Fsecuritycode = D1.Fsecuritycode" +
			" Left Join (Select e.Fcurycode, e.Fcuryname From "+pub.yssGetTableName("Tb_Para_Currency")+
			" e) E1 On b.Fcurycode = E1.Fcurycode" +
			" Left Join ( Select a.Fsecuritycode, Sum(a.Ftradeamount * g.Famountind) As Ftradeamount," +
			" Sum(a.Fcost * g.Famountind) As Fcost, Sum(a.Fbasecurycost * g.Famountind) As Fbasecurycost," +
			" Sum(a.Fportcurycost * g.Famountind) As Fportcurycost From "+pub.yssGetTableName("Tb_Data_Subtrade")+
			" a Join Tb_Base_Tradetype g On a.Ftradetypecode = g.Ftradetypecode Where a.Fbargaindate <= " +
			dbl.sqlDate(this.FStartDate)+" And "+dbl.sqlDate(FStartDate)+" < a.Fsettledate " +
			" And a.FCheckState = 1 "+
			((this.FPortCode == null || this.FPortCode.trim().equals(""))? "" : " And a.FPortcode = "+dbl.sqlString(FPortCode) )+
			" Group By a.Fsecuritycode" +
			" ) A1 On A1.Fsecuritycode = b.Fsecuritycode Left Join (Select a.Fsecuritycode, " +
			" Sum(a.Ftradeamount * g.Famountind) As Ftradeamount, Sum(a.Fcost * g.Famountind) As Fcost," +
			" Sum(a.Fbasecurycost * g.Famountind) As Fbasecurycost, " +
			" Sum(a.Fportcurycost * g.Famountind) As Fportcurycost From "+pub.yssGetTableName("Tb_Data_Subtrade")+
			" a Join Tb_Base_Tradetype g On a.Ftradetypecode = g.Ftradetypecode Where a.Fsettledate <= " +
			dbl.sqlDate(FStartDate)+" And a.Fsecuritydelaysettlestate = 1 " +
			((this.FPortCode == null || this.FPortCode.trim().equals(""))? "" : " And a.FPortcode = "+dbl.sqlString(FPortCode) )+
			" Group By a.Fsecuritycode) F1 " +
			" On F1.Fsecuritycode = b.Fsecuritycode Where b.Fcheckstate = 1 And b.Fstoragedate = " +
			dbl.sqlDate(FStartDate);
		//条件
		if (this.FPortCode!=null && !FPortCode.trim().equals("") && !FPortCode.trim().equals("null")) {
			query += " And b.Fportcode = "+dbl.sqlString(FPortCode);
		}
		if (this.FSecurityCode!=null && !FSecurityCode.trim().equals("") && !FSecurityCode.trim().equals("null")) {
			query += " And b.Fsecuritycode = "+dbl.sqlString(FSecurityCode);
		}
		//排序
		query += " Order By b.Fstoragedate , D1.Fexchangecode," +//库存日期,交易所
				//证券代码,按数字排序'001 HK',再按字母排序'abc HK',不考虑字符串数字混乱镶嵌模式,如'CRA1V FH'
				" /*rpad(to_char(b.Fsecuritycode),20,'0'),*/ "+
				" to_number(translate(b.Fsecuritycode, '0123456789' || b.Fsecuritycode , '0123456789')) " +
				" , NLSSORT(b.Fsecuritycode,'NLS_SORT = SCHINESE_PINYIN_M') ";
		
		return query;
	}

	public String getFStartDate() {
		return FStartDate;
	}

	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}

	public String getFSecurityCode() {
		return FSecurityCode;
	}

	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}

	/**shashijie 2012-4-13 STORY 2472 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("ZhengQuanKuCun");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "ZhengQuanKuCun" + "\tDSF\t-1\t" + i;
                if (hmCellStyle.containsKey(sKey)) {
                    rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                    buf.append(rtc.buildRowStr()).append("\n");
                }
                buf.append(sArry[i]).append("\t");
            }
            if (buf.toString().trim().length() > 1) {
            	strReturn = YssFun.getSubString(buf.toString());
            }
            
            return strReturn + "\t\t";
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            //dbl.closeResultSetFinal(rs);
        }
	}
	
	/**shashijie 2012-4-13 STORY 2472 拼接18列数据
	* @param row1 第一列
	* @param row2 第二列...
	* @return*/
	private String operionStr(String row1, String row2, String row3,String row4,String row5,String row6,
			String row7,String row8,String row9,String row10,String row11,String row12,
			String row13,String row14,String row15,String row16,String row17,String row18) {
		String str = "";
		
		str += row1 + "\t";//第一列
		str += row2 + "\t";//第二列...
		str += row3 + "\t";
		str += row4 + "\t";
		str += row5 + "\t";
		str += row6 + "\t";
		str += row7 + "\t";
		str += row8 + "\t";
		str += row9 + "\t";
		str += row10+ "\t";
		str += row11+ "\t";
		str += row12+ "\t";
		str += row13+ "\t";
		str += row14+ "\t";
		str += row15+ "\t";
		str += row16+ "\t";
		str += row17+ "\t";
		str += row18+ "\t";
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
}
