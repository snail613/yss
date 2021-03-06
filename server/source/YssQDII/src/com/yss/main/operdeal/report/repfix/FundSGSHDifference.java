package com.yss.main.operdeal.report.repfix;

import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseBean;
import com.yss.main.cusreport.RepTabCellBean;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.main.operdeal.report.netvalueviewpl.FixPub;
import com.yss.main.report.CommonRepBean;
import com.yss.main.taoperation.TaTradeBean;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * 此类处理招商基金投资基金申购赎回轧差报表
 * @author xuqiji 20100323 MS01038 QDV4招商基金2010年3月18日01_A  
 *
 */
public class FundSGSHDifference extends BaseBuildCommonRep{
	private CommonRepBean repBean;
	private TaTradeBean taTrade;
    private String sTheDate = ""; //查询日期
    private String sPortCode = ""; //组合代码
	public FundSGSHDifference() {
		// TODO Auto-generated constructor stub
	}
    /**
     * initBuildReport
     * 初始化数据
     * @param bean BaseBean
     */
    public void initBuildReport(BaseBean bean) throws YssException {
        repBean = (CommonRepBean) bean;
        taTrade = new TaTradeBean();
        taTrade.setYssPub(pub);
        //taTrade.parseRowStr(repBean.getRepCtlParam());
        String reqAry[] = null;
        repBean = (CommonRepBean) bean;
        reqAry = repBean.getRepCtlParam().split("\n"); //这里是要获得参数
        sTheDate = reqAry[0].split("\r")[1];
        sPortCode = reqAry[1].split("\r")[1];
        taTrade.setDSettleDate(YssFun.toDate(sTheDate));
        taTrade.setStrPortCode(sPortCode);
   }
    /**
     * buildReport
     * 拼接报表数据的入口方法
     * @param sType String
     * @return String
     */
    public String buildReport(String sType) throws YssException {
    	String sResult = "";
    	try{
    		sResult = buildShowData();
    	}catch (Exception e) {
			throw new YssException(e.getMessage());
		}
        return sResult;
    }
    /**
     * 具体获取报表数据并按照一定格式拼接的方法
     * @return
     * @throws YssException 
     */
	private String buildShowData() throws YssException {
		StringBuffer buff = null;
		StringBuffer bufSql = null;
		ResultSet rs = null;
		String sFundCode = "";//基金代码
		String sFundName = "";//基金名称
		double dSGMoney = 0;//申购金额
		double dChangeInMoney = 0;//转入金额
		double dSHMoney = 0;//赎回金额
		double dSHFee = 0;//赎回费用（不归基金资产）
		double dChangeOutMoney = 0;//转出金额
		double dChangeOutFee = 0;//转出费用（不归基金资产）
		double dBackSGFee = 0;//后端申购费
		double dDividendMoney = 0;//分红金额
		double dInflowCountMoney = 0;//流入小计
		double dOutflowCountMoney =0;//流出小计
		double dDifferenceMoney = 0;//轧差金额（负数为划出款）
		String strSql = "";
		HashMap hmCellStyle = null;
		String sKey = "";
		RepTabCellBean rtc = null;
		int i =0;
		try{
			bufSql = new StringBuffer(500);
			buff = new StringBuffer(500);
			
			bufSql.append(" select * from ").append(pub.yssGetTableName("tb_para_Portfolio"));
			bufSql.append(" where FPortCode = ").append(dbl.sqlString(taTrade.getStrPortCode()));
			
			rs = dbl.openResultSet(bufSql.toString());
			bufSql.delete(0,bufSql.length());
			
			if(rs.next()){
				sFundCode = rs.getString("FAssetCode");
				sFundName = rs.getString("FPortName");
			}
			dbl.closeResultSetFinal(rs);
			buff.append(YssFun.formatDate(this.sTheDate,"yyyy/MM/dd")).append("\t").append(" ").append("\t").append(" ").append("\t").append(" ").append("\t");
			buff.append(" ").append("\t").append(" ").append("\r\n");
			buff.append("基金名称").append("\t").append(sFundName).append("\t");
			buff.append(" ").append("\t").append("基金代码").append("\t");
			
			//-------------------------处理申购金额数据-------------------------//
			dSGMoney = this.getSGOrChangeInMoneyData("01",sFundCode);
			//-------------------------处理转入金额数据------------------------------//
			dChangeInMoney = this.getSGOrChangeInMoneyData("04",sFundCode);
			//------------------------处理基金分红金额-------------------------------//
			dDividendMoney = this.getSGOrChangeInMoneyData("03",sFundCode);
			//-------------------------处理赎回金额，费用数据-------------------------//
			double [] dSH = this.getSHOrChangeInMoneyData("02",sFundCode);
			dSHMoney = dSH[0];
			dSHFee = dSH[1];
			//-------------------------处理转出金额，转出费用数据----------------------//
			double [] dChangeOut = this.getSHOrChangeInMoneyData("05",sFundCode);
			dChangeOutMoney = dChangeOut[0];
			dChangeOutFee = dChangeOut[1];
			
			dInflowCountMoney = YssD.add(dSGMoney,dChangeInMoney);//流入小计
			
			dOutflowCountMoney = YssD.add(YssD.add(dSHMoney,dSHFee,dChangeOutMoney),dChangeOutFee,dDividendMoney);//流出小计
			
			dDifferenceMoney = YssD.sub(dInflowCountMoney,dOutflowCountMoney);
			
			buff.append(sFundCode).append("\t").append(" ").append("\r\n");
			buff.append("申购金额").append("\t").append(YssFun.formatNumber(dSGMoney,"#,###.##")).append("\t").append(" ").append("\t").append("赎回金额").append("\t");
			buff.append(YssFun.formatNumber(dSHMoney,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append("转入金额").append("\t").append(YssFun.formatNumber(dChangeInMoney,"#,###.##")).append("\t").append(" ").append("\t").append("赎回费(不归基金资产)").append("\t");
			buff.append(YssFun.formatNumber(dSHFee,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append("转出金额").append("\t");
			buff.append(YssFun.formatNumber(dChangeOutMoney,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append("转出费(不归基金资产)").append("\t");
			buff.append(YssFun.formatNumber(dChangeOutFee,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append("后端申购费").append("\t");
			buff.append(0.0).append("\t").append(" ").append("\r\n");
			buff.append(" ").append("\t").append(" ").append("\t").append(" ").append("\t").append("分红金额").append("\t");
			buff.append(YssFun.formatNumber(dDividendMoney,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append("流入小计").append("\t").append(YssFun.formatNumber(dInflowCountMoney,"#,###.##")).append("\t").append(" ").append("\t");
			buff.append("流出小计").append("\t").append(YssFun.formatNumber(dOutflowCountMoney,"#,###.##")).append("\t").append(" ").append("\r\n");
			buff.append("钆差(负数为划出款)").append("\t").append(YssFun.formatNumber(dDifferenceMoney,"#,###.##")).append("\t").append(" ").append("\t").append(" ").append("\t");
			buff.append(" ").append("\t").append(" ").append("\r\n");

			hmCellStyle = getCellStyles("DSSGSHDifference");
			String tempStr = buff.toString();
            if(buff.toString().endsWith("\r\n")){
            	tempStr = buff.toString().substring(0,buff.length() - 2);
            }
            String [] s = tempStr.split("\r\n");
            String [] ss;
            buff.delete(0,buff.length());
            for(int j = 0; j < s.length; j++){
            	ss = s[j].split("\t");
                strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
                " where FRepDsCode = " + dbl.sqlString("DSSGSHDifference") +
                " and FCheckState = 1 order by FOrderIndex";
                rs = dbl.openResultSet(strSql);
                while(rs.next()){
               	 //获得样式
                   sKey = "DSSGSHDifference" + "\tDSF\t-1\t" +
                       rs.getString("FOrderIndex");

                   rtc = (RepTabCellBean) hmCellStyle.get(sKey);
                   buff.append(rtc.buildRowStr()).append("\n");
                   if(i != 5){
                	   buff.append(ss[i]).append("\t");
                   }else{
                	   buff.append(ss[i]).append("\r\n");
                   }
                   i++;
               }
                i = 0;
                if(buff.toString().endsWith("\t")){
                	buff.setLength(buff.length()-1);
                	String[] dd = buff.toString().split("\t");
                	buff.append("\r\n");
                }
                
                dbl.closeResultSetFinal(rs);
            }
		}catch (Exception e) {
			throw new YssException("获取报表数据并按照一定格式拼接出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return buff.toString();
	}
	/**
	 * 获取TA申购、转入和分红交易类型数据
	 * @param sTradeType 交易类型
	 * @param sFundCode	基金代码
	 * @return
	 * @throws YssException
	 */
	private double getSGOrChangeInMoneyData(String sTradeType,String sFundCode) throws YssException{
		double dReturnMoney = 0;
		StringBuffer bufSql = null;
		ResultSet rs = null;
		try{
			bufSql = new StringBuffer(500);
			
			bufSql.append(" select a.fsettledate,a.fcurycode,b.fassetcode,sum(a.FSettleMoney) as FSettleMoney from ");
			bufSql.append(pub.yssGetTableName("tb_ta_trade")).append(" a ");
			bufSql.append(" left join ").append(pub.yssGetTableName("tb_para_portfolio")).append(" b ");
			bufSql.append(" on a.fportcode = b.fportcode where a.FCheckState = 1 and b.fcheckstate =1");
			bufSql.append(" and a.fsettledate = ").append(dbl.sqlDate(taTrade.getDSettleDate()));
			bufSql.append(" and a.FPortCode = ").append(dbl.sqlString(taTrade.getStrPortCode()));
			bufSql.append(" and a.FSellType = ").append(dbl.sqlString(sTradeType)).append("and b.fassetcode = ").append(dbl.sqlString(sFundCode));
			bufSql.append(" group by a.fsettledate, a.fcurycode, b.fassetcode");
			
			rs = dbl.openResultSet(bufSql.toString());
			if(rs.next()){
				dReturnMoney = rs.getDouble("FSettleMoney");
			}
		}catch (Exception e) {
			throw new YssException("获取TA申购、转入和分红交易类型数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dReturnMoney;
	}
	/**
	 * 获取TA赎回、转出类型数据
	 * @param sTradeType 交易类型
	 * @param sFundCode	基金代码
	 * @return
	 * @throws YssException
	 */
	private double[] getSHOrChangeInMoneyData(String sTradeType,String sFundCode) throws YssException{
		double [] dReturnMoney = new double[2];
		StringBuffer bufSql = null;
		ResultSet rs = null;
		try{
			bufSql = new StringBuffer(500);
			
			bufSql.append(" select a.fsettledate,a.fcurycode,b.fassetcode,sum(a.FSettleMoney) as FSettleMoney,sum(a.ftradefee1) as dFee from ");
			bufSql.append(pub.yssGetTableName("tb_ta_trade")).append(" a ");
			bufSql.append(" left join ").append(pub.yssGetTableName("tb_para_portfolio")).append(" b ");
			bufSql.append(" on a.fportcode = b.fportcode where a.FCheckState = 1 and b.fcheckstate =1");
			bufSql.append(" and a.fsettledate = ").append(dbl.sqlDate(taTrade.getDSettleDate()));
			bufSql.append(" and a.FPortCode = ").append(dbl.sqlString(taTrade.getStrPortCode()));
			bufSql.append(" and a.FSellType = ").append(dbl.sqlString(sTradeType)).append("and b.fassetcode = ").append(dbl.sqlString(sFundCode));
			bufSql.append(" group by a.fsettledate, a.fcurycode, b.fassetcode");
			
			rs = dbl.openResultSet(bufSql.toString());
			if(rs.next()){
				dReturnMoney[0] = rs.getDouble("FSettleMoney");
				dReturnMoney[1] = rs.getDouble("dFee");
			}
		}catch (Exception e) {
			throw new YssException("获取TA赎回、转出类型数据数据出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return dReturnMoney;
	}
}














