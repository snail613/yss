package com.yss.main.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

//import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import java.util.Hashtable;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.voucher.CtlVchExcuteBean;
import com.yss.main.operdeal.voucher.vchbuild.VchBuildSingle;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.main.voucher.VchDataBean;
import com.yss.main.voucher.VchDataEntityBean;
import com.yss.manager.VoucherAdmin;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;

public class GVDataModifyingBean extends BaseDataSettingBean implements IDataSetting{
	
	private String sAcctCode = "";			//科目代码
	private String sAcctName = "";			//科目名称
	private double dCost = 0;				//原币成本
	private double dStandardMoneyCost = 0;			//本位币成本
	private double dMarketValue = 0;		//原币市值
	private double dStandardMoneyMarketValue = 0;	//本位币市值
	private double dAppreciation = 0;		//原币估值增值
	private double dStandardMoneyAppreciation = 0; 	//本位币估值增值
	private double dMarketValuedifference = 0;		//原币估值增值差异
	private double dStandardMoneydifference = 0;	//本位币估值增值差异
	private String sSetCode = "";					//资产代码
	private Date dDate = null;						//估值表日期
	private String sParentAcctCode = "";			//上级科目代码
	private String sAcctDetail = "";				//是否明细科目
	private String sCury = "";						//币种
	private String sAttrStr = "";					//操作参数（操作维护数据表时用到）
	private double dOriginalSMMarketValue = 0.0;	//20120615 added by liubo.Bug #4812.原始本位币市值
	private double dOriginalOMMarketValue = 0.0;	//20120615 added by liubo.Bug #4812.原始原币市值
	
	

	public double getOriginalOMMarketValue() {
		return dOriginalOMMarketValue;
	}

	public void setOriginalOMMarketValue(double dOriginalOMMarketValue) {
		this.dOriginalOMMarketValue = dOriginalOMMarketValue;
	}

	public double getOriginalSMMarketValue() {
		return dOriginalSMMarketValue;
	}

	public void setOriginalSMMarketValue(double dOriginalSMMarketValue) {
		this.dOriginalSMMarketValue = dOriginalSMMarketValue;
	}

	public String addSetting() throws YssException {
		String strSql = "";
		boolean bDoInsertOperation = true;	//20120615 added by liubo.Bug #4812.是否需要进行插入操作。

		PortfolioBean port = new PortfolioBean();
		YssFinance yfiance = new YssFinance();
		
		try
		{
			strSql = "Delete  from " + pub.yssGetTableName("Tb_rep_GVDataModifying") + " where FPortCode = " + dbl.sqlString(sSetCode) + " and FDate = " + dbl.sqlDate(dDate);
			dbl.executeSql(strSql);
			
			if (sAttrStr == null)
			{
//				return "";
				bDoInsertOperation = false;
			}
			else
			{
				if (sAttrStr.trim().equals(""))
				{
//					return "";
					bDoInsertOperation = false;
				}
			}
			
			if (bDoInsertOperation)
			{
				String[] sDataList = sAttrStr.split("\r\f");
				
				for (int i = 0; i < sDataList.length; i++)
				{
					String[] sAttrDetail = sDataList[i].split("\t");
					String[] sAcctCode = sAttrDetail[0].split("_");
					String sAuxiAccID = "";
					String sParentAcct = "";
					
					sParentAcct = sAcctCode[0];
					sParentAcct = sParentAcct.substring(0, sParentAcct.length() - 2);
					if (sAcctCode.length > 1)
					{
						sAuxiAccID = sAcctCode[1];
					}
					else
					{
						sAuxiAccID = " ";
					}
					
					
					strSql = "insert into " + pub.yssGetTableName("Tb_rep_GVDataModifying") + " values(" +
							 dbl.sqlString(sSetCode) + ", " +
							 dbl.sqlDate(dDate) + "," +
							 dbl.sqlString(sAttrDetail[0]) + "," +
							 dbl.sqlString(sAttrDetail[1]) + "," +
							 dbl.sqlString(sParentAcct) + "," +
							 dbl.sqlString(sAuxiAccID) + "," +
							 dbl.sqlString(sAttrDetail[10]) + "," +
							 sAttrDetail[2] + "," +
							 sAttrDetail[3] + "," +
							 sAttrDetail[4] + "," +
							 sAttrDetail[5] + "," +
							 sAttrDetail[6] + "," +
							 sAttrDetail[7] + "," +
							 sAttrDetail[8] + "," +
							 sAttrDetail[9] + "," +
							 dbl.sqlString(pub.getUserCode()) + "," +
							 dbl.sqlString(YssFun.formatDate(new Date())) + "," +
							 dbl.sqlString(" ") + "," +
							 dbl.sqlString(" ") + "," + 
							 sAttrDetail[11] + "," +
							 sAttrDetail[12] + ")";
					
					dbl.executeSql(strSql);
				}
			}
			
			YssStatus runStatusTmp = new YssStatus();
			
			CtlVchExcuteBean bean = new CtlVchExcuteBean();
			port.setYssPub(pub);
			bean.setYssPub(pub);
			yfiance.setYssPub(pub);
			
			port.setPortCode(yfiance.getPortCode(sSetCode));
            port.getSetting();
			
			bean.setFreeParams("808");
			bean.setVchTypes("808");
			bean.setPortCodes(port.getPortCode());
			bean.setBeginDate(YssFun.formatDate(dDate));
			bean.setEndDate(YssFun.formatDate(dDate));
			bean.setYssRunStatus(runStatusTmp);
			bean.doOperation("build");
			
			strSql = "Update " + pub.yssGetTableName("Tb_Vch_Data") +
					 " set FCheckState = 1 where FVCHTPLCODE = '808' and FVCHDATE = " + dbl.sqlDate(dDate) + " and FPORTCODE = " + dbl.sqlString(yfiance.getPortCode(sSetCode));
			dbl.executeSql(strSql);
			
			bean.doOperation("outacc");
		
		}
		catch(Exception ye)
		{
			throw new YssException("处理估值表手工维护信息出错：" + ye.getMessage());
		}

		return "";
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String editSetting() throws YssException {
		
		return "";
	}

	public String getAllSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataSetting getSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(sAcctCode).append("\t");
			buf.append(sAcctName).append("\t");
			buf.append(YssFun.formatNumber(dCost,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dStandardMoneyCost,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dMarketValue,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dStandardMoneyMarketValue,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dAppreciation,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dStandardMoneyAppreciation,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dMarketValuedifference,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dStandardMoneydifference,"0.0###")).append("\t");
			buf.append(sParentAcctCode).append("\t");
			buf.append(sAcctDetail).append("\t");
			buf.append(sCury).append("\t");
			buf.append(YssFun.formatNumber(dOriginalOMMarketValue,"0.0###")).append("\t");
			buf.append(YssFun.formatNumber(dOriginalSMMarketValue,"0.0###"));
			
			return buf.toString();
		}
		catch(Exception ye)
		{
			throw new YssException("拼接字符串出错：" + ye.getMessage());
		}
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		
		try
		{
			String reqAry[] = null;
			reqAry = sRowStr.split("\f\f");
			
			dDate = YssFun.toDate(reqAry[0]);
			sSetCode = reqAry[1];
			sAttrStr = reqAry[2];
		}
		catch(Exception ye)
		{
			throw new YssException("解析字符串出错：" + ye.getMessage());
		}
		
	}

	public String getListViewData1() throws YssException {
		

		
		String sReturn = "";
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsAttr = null;
		YssFinance yfiance = new YssFinance();
		String YssTabPrefix = "";
		Hashtable haMARKETVALUE = new Hashtable();
		Hashtable haSTANDARDMONEYMARKETVALUE = new Hashtable();
		Hashtable haAPPRECIATION = new Hashtable();
		Hashtable haSTANDARDMONEYAPPRECIATION = new Hashtable();
		Hashtable haMarketValuedifference = new Hashtable();
		Hashtable haStandardMoneydifference = new Hashtable();
		Hashtable haOriginalOMMarketValue = new Hashtable();	//20120615 added by liubo.Bug #4812.原始原币市值
		Hashtable haOriginalSMMarketValue = new Hashtable();	//20120615 added by liubo.Bug #4812.原始本位币市值
		
		try
		{
			strSql = "select * from " + pub.yssGetTableName("Tb_rep_GVDataModifying") + " where FPortCode = " + dbl.sqlString(sSetCode) + " and FDate = " + dbl.sqlDate(dDate);
			rsAttr = dbl.queryByPreparedStatement(strSql);
			while(rsAttr.next())
			{
				haMARKETVALUE.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FMarketValue"));
				haSTANDARDMONEYMARKETVALUE.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FStandardMoneyMarketValue"));
				haAPPRECIATION.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FAppreciation"));
				haSTANDARDMONEYAPPRECIATION.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FStandardMoneyAppreciation"));
				haMarketValuedifference.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FOMdifference"));
				haStandardMoneydifference.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FSMdifference"));
				haOriginalOMMarketValue.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FOriginalOMMarketValue"));	//20120615 added by liubo.Bug #4812.原始原币市值
				haOriginalSMMarketValue.put(rsAttr.getString("FAcctCode"), rsAttr.getDouble("FOriginalSMMarketValue"));	//20120615 added by liubo.Bug #4812.原始本位币市值
			}
			
			yfiance.setYssPub(pub);
			
			String sBookSetCode = yfiance.getPortCode(sSetCode);
			sBookSetCode = yfiance.getCWSetCode(sBookSetCode);
			
			YssTabPrefix = "A" + YssFun.formatDate(dDate,"yyyy") + sBookSetCode + "LAccount";
			
			strSql = "select a.*,substr(a.facctcode,0,4) as ccc,b.FAcctParent from " + pub.yssGetTableName("tb_rep_guessvalue") + " a left join " + YssTabPrefix + " b on (case when substr(a.fAcctcode,0,instr(a.facctcode, '_') - 1) is null then a.facctcode else substr(a.fAcctcode,0,instr(a.facctcode, '_') - 1) end) = b.facctcode" +
					 " where a.fdate = " + dbl.sqlDate(dDate) + " and FPortCode = " + dbl.sqlString(sSetCode) +
					 " and substr(a.facctcode,0,4) in ('1102','1103','1104','1105','1106','1107') " +
					 " order by ccc, b.facctclass,a.facctlevel desc, a.FAcctCode";
			
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				this.sAcctCode = rs.getString("FAcctCode");
				this.sAcctName = rs.getString("FAcctName");
				
				this.dCost = rs.getDouble("FCost");
				this.dStandardMoneyCost = rs.getDouble("FStandardMoneyCost");
				this.dMarketValue = (haMARKETVALUE.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FMARKETVALUE") : Double.parseDouble(String.valueOf((haMARKETVALUE.get(rs.getString("FAcctCode"))))));
				this.dStandardMoneyMarketValue = (haSTANDARDMONEYMARKETVALUE.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FSTANDARDMONEYMARKETVALUE") : Double.parseDouble(String.valueOf((haSTANDARDMONEYMARKETVALUE.get(rs.getString("FAcctCode"))))));
				this.dAppreciation = (haAPPRECIATION.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FAPPRECIATION") : Double.parseDouble(String.valueOf((haAPPRECIATION.get(rs.getString("FAcctCode"))))));
				this.dStandardMoneyAppreciation = (haSTANDARDMONEYAPPRECIATION.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FSTANDARDMONEYAPPRECIATION") : Double.parseDouble(String.valueOf((haSTANDARDMONEYAPPRECIATION.get(rs.getString("FAcctCode"))))));
				
				this.dMarketValuedifference = (haMarketValuedifference.get(rs.getString("FAcctCode")) == null ? 0.0 : Double.parseDouble(String.valueOf((haMarketValuedifference.get(rs.getString("FAcctCode"))))));
				this.dStandardMoneydifference = (haStandardMoneydifference.get(rs.getString("FAcctCode")) == null ? 0.0 : Double.parseDouble(String.valueOf((haStandardMoneydifference.get(rs.getString("FAcctCode"))))));
				
				this.sParentAcctCode = (rs.getString("FAcctParent") == null ? " " : rs.getString("FAcctParent"));
				this.sAcctDetail = rs.getString("FAcctDetail");
				
				this.sCury = rs.getString("FCurCode");
				
				this.dOriginalOMMarketValue = (haOriginalOMMarketValue.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FMARKETVALUE") : Double.parseDouble(String.valueOf((haOriginalOMMarketValue.get(rs.getString("FAcctCode"))))));

				this.dOriginalSMMarketValue = (haOriginalSMMarketValue.get(rs.getString("FAcctCode")) == null ? rs.getDouble("FSTANDARDMONEYMARKETVALUE") : Double.parseDouble(String.valueOf((haOriginalSMMarketValue.get(rs.getString("FAcctCode"))))));
				
				if (dOriginalOMMarketValue == 0)
				{
					dOriginalOMMarketValue = rs.getDouble("FMARKETVALUE");
				}
				
				if (dOriginalSMMarketValue == 0)
				{
					dOriginalSMMarketValue = rs.getDouble("FSTANDARDMONEYMARKETVALUE");
				}
				
				sReturn += this.buildRowStr() + "\r\f";
				
			}
			
			return sReturn;
		}
		catch(Exception ye)
		{
			throw new YssException("获取信息出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs,rsAttr);
		}
	}

	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getListViewGroupData5() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTreeViewGroupData3() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
