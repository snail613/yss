/**@author shashijie
*  @version 创建时间：2012-11-19 上午10:45:27 STORY 3187 余额表固定数据源
*  类说明:获取临时余额表数据Tmp_lbalance
*/
package com.yss.main.operdeal.report.repfix.RepOverage;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.report.CommonRepBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

/**
 * @author shashijie
 *
 */
public class RepOverage extends BaseBuildCommonRep{
	
	private CommonRepBean repBean;//报表对象
	    
    private String FStartDate = "";//起始日期
    private String FEndDate = "";//截止日期
    private String FPortCode = "";//组合代码(多选)
	
    
    /**程序入口 shashijie 2012-11-19 STORY 3187 */
    public String buildReport(String sType) throws YssException {
        String sResult = "";
        //生成临时余额表(临时登帐)
        YssFinance finance = new YssFinance();
        finance.setYssPub(pub);
        finance.PostAccB(YssFun.toDate(FStartDate), FPortCode);
        //获取报表内容
        sResult = getInfo();
        
        return sResult;
    }
    
    /**shashijie 2012-11-19 STORY 3187 拼接每行数据 */
    private String operionStr(String A, String B, String C,
			BigDecimal D,BigDecimal E,BigDecimal F,BigDecimal G,BigDecimal H,BigDecimal I,BigDecimal J,
			BigDecimal k,BigDecimal L,BigDecimal M,BigDecimal N,BigDecimal O,BigDecimal P) {
    	
		String str = "";
		
		str += A + "\t";//第一列
		str += B + "\t";//第二列
		str += C + "\t";//第三列
		/**原币金额*/
		str += getDebit(D) + "\t";//借贷平
		str += E.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += F.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += G.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += getDebit(H) + "\t";//借贷平
		str += I.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		/**本币金额*/
		str += J + "\t";//汇率
		str += getDebit(k) + "\t";//借贷平
		str += L.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += M.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += N.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		str += getDebit(O) + "\t";//借贷平
		str += P.setScale(2,BigDecimal.ROUND_HALF_UP) + "\t";
		
		try {
			str = buildRowCompResult(str)+"\r\n";
		} catch (Exception e) {
			str = "";
		}
		
		return str;
	}
	
    /**shashijie 2012-11-19 STORY 3187 获取'借''贷''平'*/
	private String getDebit(BigDecimal E) {
		String debit = "借";
		if (E.equals(0)) {
			debit = "平";
		} else if (E.equals(-1)) {
			debit = "贷";
		}
		return debit;
	}

	/**shashijie 2012-11-19 STORY 3187 获取报表内容*/
	private String getInfo() throws YssException {
		String str = "";
		ResultSet rs = null;
		//选保存当前年份
		int year = pub.getPrefixYear();
		pub.setPrefixYear(YssFun.getYear(YssFun.toDate(FStartDate)));
		try {
			//获取需要统计的科目
			String strSql = getInfoSQl(FStartDate,FPortCode);
			rs = dbl.openResultSet(strSql);
			//封装入集合中
			while (rs.next()) {
				//拼接
				str += doProcess(rs);
			}
		} catch (Exception e) {
			str = "";
			//throw new YssException("获取报表内容出错： \n");
		} finally {
			//还原系统年份
			pub.setPrefixYear(year);
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}
	
	/**shashijie 2012-11-19 STORY 3187 拼接内容 */
	private String doProcess(ResultSet rs) throws Exception {
		String str = "";
		//拼接报表具体内容
		str += operionStr(rs.getString("Facctcode"),rs.getString("FacctName"),rs.getString("FCurName"),
				rs.getBigDecimal("Fbaldc"),rs.getBigDecimal("Startbal"),rs.getBigDecimal("Debit"),
				rs.getBigDecimal("Credit"),rs.getBigDecimal("Fbaldc"),rs.getBigDecimal("Endbal"),
				rs.getBigDecimal("FCurRate"),rs.getBigDecimal("Fbaldc"),rs.getBigDecimal("Bstartbal"),
				rs.getBigDecimal("Bdebit"),rs.getBigDecimal("Bcredit"),rs.getBigDecimal("Fbaldc"),
				rs.getBigDecimal("Bendbal")
				);
		return str;
	}

	/** shashijie 2012-11-19 STORY 3187 获得余额表SQL */
	private String getInfoSQl(String dDate,String portCode) throws YssException {
		String SqlStr = "";
		String LTableName = "Tmp_lbalance";
		
		// 余额表取数主体
		String mainSql = 
			" sum(case when FMonth=0 then FEndBal else 0 end) as ncye,"
			+ "sum(case when FMonth=0 then 0 else FStartBal end) as StartBal,"
			+ "sum(case when FMonth=0 then 0 else FMAccdebit end) as Debit,"//借方月发生额
			+ "sum(case when FMonth=0 then 0 else FMAcccredit end) as Credit,"//贷方月发生额
			+ "sum(case when FMonth=0 then 0 else FYAccDebit end) as AccDebit,"//借方累计月发生额
			+ "sum(case when FMonth=0 then 0 else FYAccCredit end) as AccCredit,"//贷方累计月发生额
			+ "sum(case when FMonth=0 then 0 else FEndBal end) as EndBal ,"
			+ "sum(case when FMonth=0 then FBEndBal else 0 end) as bncye,"
			+ "sum(case when FMonth=0 then 0 else FBStartBal end) as bStartBal,"
			+ "sum(case when FMonth=0 then 0 else FBMAccdebit end) as bDebit,"//借方月发生额(本币)
			+ "sum(case when FMonth=0 then 0 else FBMAcccredit end) as bCredit,"//贷方月发生额(本币)
			+ "sum(case when FMonth=0 then 0 else FBYAccDebit end) as bAccDebit,"//借方累计月发生额(本币)
			+ "sum(case when FMonth=0 then 0 else FByAcccredit end) as bAccCredit,"//贷方累计月发生额(本币)
			+ "sum(case when FMonth=0 then 0 else FBEndBal end) as bEndBal ";
		
		SqlStr = "select c.*,d.FCurRate,d.Fcurname,"
				// 截取证券代码
				+ " (case when instr(Facctcode, '_') > 0 then "
				+ " substr(facctcode,instr(facctcode, '_') + 1,instr(facctcode, ' ') - instr(facctcode, '_') - 1) "
				+ " else ' ' end) as TmpAuxiAccID, "
				// 截取第一个下划线之前的科目代码
				+ " (case when instr(Facctcode,'_') > 0 then substr(facctcode,"
				+ "1,"
				+ "instr(facctcode, '_') - 1) else facctcode end) as TmpAcctCode "
				// 科目表
				+ " from (select "
				+ " a.FBalDC,a.FAcctLevel,a.FAcctName,a.FAcctDetail,a.fcurcode,a.FAuxiAcc,b.* from "
				+ pub.yssGetTableName("A<YEAR><SET>LAccount")
				+ " a , "
				+ "  ( "
				+ " select FAcctcode,fcurcode as bcurcode, "
				+ mainSql
				+ " ,' ' as Auxiaccname from "
				+ LTableName
				+ "  where "
				+ " ( FMonth=0 or FMonth="
				+ YssFun.getMonth(YssFun.toDate(dDate))
				+ " ) "
				+ " and FAddr= "
				+ dbl.sqlString(pub.getUserCode())
				+ " group by FAcctcode,fcurcode  "
				+ " union all "
				+ "select FAcctcode"
				+ dbl.sqlJN()
				+ "'_'"
				+ dbl.sqlJN()
				+ " Case When "
				+ dbl.sqlInstr("FAuxiAcc", "'|'")
				+ ">2 Then  "
				+ dbl.sqlSubStr("FAuxiAcc", "3", dbl
						.sqlInstr("FAuxiAcc", "'|'")
						+ "-3")
				+ " Else "
				+ dbl.sqlSubStr("FAuxiAcc", "3", dbl.sqlLen("FAuxiAcc"))
				+ " End as FAcctcode,fcurcode as bcurcode,"
				+ mainSql
				+ ",Auxiaccname from "
				+ LTableName
				+ " e inner join "
				//辅助核算设置
				+ pub.yssGetTableName("A<YEAR><SET>Auxiaccset")
				+ " f on ("
				+ dbl.sqlSubStr("e.FAuxiAcc", "0", dbl.sqlInstr("e.FAuxiAcc",
						"'|'")
						+ "-1")
				+ "=f.Auxiaccid or e.FAuxiAcc=f.Auxiaccid)where "
				+ "(FMonth=0 or FMonth="
				+ YssFun.getMonth(YssFun.toDate(dDate))
				+ ")"
				+ " and FAddr= "
				+ dbl.sqlString(pub.getUserCode())
				+ " and "
				+ dbl.sqlLen(dbl.sqlTrim("FAuxiAcc"))
				+ ">2 group by facctcode,fcurcode,fauxiacc,f.auxiaccname "
				+ " ) b where substr(b.FAcctCode,0,case when ("
				+ dbl.sqlInstr("b.FAcctCode", "'_'")
				+ "-1)>0 then "
				+ dbl.sqlInstr("b.FAcctCode", "'_'")
				+ "-1 else "
				+ dbl.sqlLen("b.FAcctCode")
				+ " end )=a.FAcctCode order by b.FAcctCode)c left join "
				//科目表
				+ pub.yssGetTableName("A<YEAR><SET>lcurrency")
				+ " d on c.bcurcode=d.fcurcode  "
				+ " order by TmpAcctCode, TmpAuxiAccID, d.fcurcode";
		
		return SqlStr;
	}
	 
    /**shashijie 2012-11-19 STORY 3187 把内容拼接上格式 */
	private String buildRowCompResult(String str) throws YssException {
        String strReturn = "";
        HashMap hmCellStyle = null;
        StringBuffer buf = new StringBuffer();
        String sKey = "";//报表格式HashMap的key
        RepTabCellBean rtc = null;//报表格式--单元格设置
        String[] sArry = null;
        try {
            sArry = str.split("\t");
            hmCellStyle = getCellStyles("Tmp_lbalance");
            for (int i = 0; i < sArry.length; i++) {
                sKey = "Tmp_lbalance" + "\tDSF\t-1\t" + i;
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
	
    /**初始数据方法*/
    public void initBuildReport(BaseBean bean) throws YssException {
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        
        FStartDate = reqAry[0].split("\r")[1];//起始日期
        FEndDate = reqAry[0].split("\r")[1];//截止日期
        FPortCode = reqAry[1].split("\r")[1];//组合
        
        
    }
    
	/**返回 fStartDate 的值*/
	public String getFStartDate() {
		return FStartDate;
	}
	/**传入fStartDate 设置  fStartDate 的值*/
	public void setFStartDate(String fStartDate) {
		FStartDate = fStartDate;
	}
	/**返回 fEndDate 的值*/
	public String getFEndDate() {
		return FEndDate;
	}
	/**传入fEndDate 设置  fEndDate 的值*/
	public void setFEndDate(String fEndDate) {
		FEndDate = fEndDate;
	}
	/**返回 fPortCode 的值*/
	public String getFPortCode() {
		return FPortCode;
	}
	/**传入fPortCode 设置  fPortCode 的值*/
	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}
    
}
