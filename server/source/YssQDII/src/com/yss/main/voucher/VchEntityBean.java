package com.yss.main.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class VchEntityBean extends BaseDataSettingBean implements IDataSetting {
	private String vchTplCode = "";
	private String entityCode = "";
	private String entityName = "";
	private String dCWay = "";
	private String dCalcWay = "";

	private String priceFieldCode = "";
	private String priceFieldName = "";

	private String resumeDesc = "";
	private String subjectDesc = "";
	private String moneyDesc = "";
	private String setMoneyDesc = "";
	private String amountDesc = "";
	private String condDesc = "";
	private String assDesc = "";

	private String entityInd = "";
	private String sAllows = ""; // QDV4深圳2009年01月15日02_B MS00194
	private String desc = "";

	private String oldVchTplCode = "";
	private String oldEntityCode = "";

	private VchEntityBean filterType;

	private String enCuryCode = "";
	private String enCuryName = "";
	private String allSetDatas = "";
	private String sRecycled = ""; // 增加回收站处理功能的字段 by leeyu 2008-10-21
									// BUG:0000491

	public String getDesc() {
		return desc;
	}

	public String getDCWay() {
		return dCWay;
	}

	public VchEntityBean getFilterType() {
		return filterType;
	}

	public String getEntityName() {
		return entityName;
	}

	public String getEntityInd() {
		return entityInd;
	}

	public String getOldEntityCode() {
		return oldEntityCode;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public String getOldVchTplCode() {
		return oldVchTplCode;
	}

	public void setVchTplCode(String vchTplCode) {
		this.vchTplCode = vchTplCode;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDCWay(String dCWay) {
		this.dCWay = dCWay;
	}

	public void setFilterType(VchEntityBean filterType) {
		this.filterType = filterType;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public void setEntityInd(String entityInd) {
		this.entityInd = entityInd;
	}

	public void setOldEntityCode(String oldEntityCode) {
		this.oldEntityCode = oldEntityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public void setOldVchTplCode(String oldVchTplCode) {
		this.oldVchTplCode = oldVchTplCode;
	}

	public void setSubjectDesc(String subjectDesc) {
		this.subjectDesc = subjectDesc;
	}

	public void setResumeDesc(String resumeDesc) {
		this.resumeDesc = resumeDesc;
	}

	public void setPriceFieldName(String priceFieldName) {
		this.priceFieldName = priceFieldName;
	}

	public void setPriceFieldCode(String priceFieldCode) {
		this.priceFieldCode = priceFieldCode;
	}

	public void setCondDesc(String condDesc) {
		this.condDesc = condDesc;
	}

	public void setAssDesc(String assDesc) {
		this.assDesc = assDesc;
	}

	public void setAmountDesc(String amountDesc) {
		this.amountDesc = amountDesc;
	}

	public void setAllSetDatas(String allSetDatas) {
		this.allSetDatas = allSetDatas;
	}

	public void setMoneyDesc(String moneyDesc) {
		this.moneyDesc = moneyDesc;
	}

	public void setSetMoneyDesc(String setMoneyDesc) {
		this.setMoneyDesc = setMoneyDesc;
	}

	public void setDCalcWay(String dCalcWay) {
		this.dCalcWay = dCalcWay;
	}

	public void setEnCuryCode(String enCuryCode) {
		this.enCuryCode = enCuryCode;
	}

	public void setEnCuryName(String enCuryName) {
		this.enCuryName = enCuryName;
	}

	/**
	 * QDV4深圳2009年01月15日02_B MS00194
	 * 
	 * @param sAllows
	 *            String
	 */
	public void setSAllows(String sAllows) {
		this.sAllows = sAllows;
	}

	public String getVchTplCode() {
		return vchTplCode;
	}

	public String getSubjectDesc() {
		return subjectDesc;
	}

	public String getResumeDesc() {
		return resumeDesc;
	}

	public String getPriceFieldName() {
		return priceFieldName;
	}

	public String getPriceFieldCode() {
		return priceFieldCode;
	}

	public String getCondDesc() {
		return condDesc;
	}

	public String getAssDesc() {
		return assDesc;
	}

	public String getAmountDesc() {
		return amountDesc;
	}

	public String getAllSetDatas() {
		return allSetDatas;
	}

	public String getMoneyDesc() {
		return moneyDesc;
	}

	public String getSetMoneyDesc() {
		return setMoneyDesc;
	}

	public String getDCalcWay() {
		return dCalcWay;
	}

	public String getEnCuryCode() {
		return enCuryCode;
	}

	public String getEnCuryName() {
		return enCuryName;
	}

	/**
	 * QDV4深圳2009年01月15日02_B MS00194
	 * 
	 * @return String
	 */
	public String getSAllows() {
		return sAllows;
	}

	public VchEntityBean() {
	}

	/**
	 * getListViewData1
	 * 
	 * @return String
	 */
	public String getListViewData1() throws YssException {
		//add by nimengjing 2010.12.16 BUG #685 凭证模板的分录设置筛选时报错 
		if("".equals(this.vchTplCode)){
			this.vchTplCode=this.filterType.vchTplCode;
		}
		//---------------------end BUG #685----------------------------------------
		String strSql = "select y.* from "
				+ "(select FVchTplCode,FEntityCode,FCheckState from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " "
				+
				// modify nimengjing 2010 12 15 BUG #663 还原凭证模板分录设置信息后，分录设置信息中会显示全部的信息。
				"where  FVchTplCode="
				+ dbl.sqlString(this.vchTplCode) +
				// " where FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join"
				// +
				"  group by FVchTplCode,FEntityCode,FCheckState) x join"
				+ // 将删除的数据也显示到前台 by leeyu 2008-10-21 BUG:0000491
				" (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
				+ " d.FVocName as DCWayValue ,e.FDesc as FPriceFieldName,g.FDesc as FEnCuryName from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " a"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ " left join Tb_Fun_Vocabulary d on a.FDCWay = d.FVocCode and d.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_DCWay)
				+ " left join Tb_Fun_Vocabulary f on a.FDCWay = f.FVocCode and f.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_CalcWay)
				+ // wdy add
				" left join (select FAliasName,FDesc from "
				+ pub.yssGetTableName("Tb_Vch_DsTabField")
				+ " where FCheckState=1 and FVchDsCode = (select FDsCode from "
				+ pub.yssGetTableName("Tb_Vch_VchTpl")
				+ " where FVchTplCode ="
				+ dbl.sqlString(this.vchTplCode)
				+ "))e on e.FAliasName=a.FPriceField"
				+ " left join (select FAliasName,FDesc from "
				+ pub.yssGetTableName("Tb_Vch_DsTabField")
				+ " where FCheckState=1 and FVchDsCode = (select FDsCode from "
				+ pub.yssGetTableName("Tb_Vch_VchTpl")
				+ " where FVchTplCode ="
				+ dbl.sqlString(this.vchTplCode)
				+ "))g on g.FAliasName=a.FEnCuryCode"
				+ buildFilterSql()
				+ ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode "
				+ " order by y.FVchTplCode,y.FEntityCode";
		// ----------------end BUG #663---------------------------------------------------------------------------------
		return builderListViewData(strSql);
	}

	/**
	 * getListViewData2
	 * 
	 * @return String
	 */
	public String getListViewData2() {
		return "";
	}

	/**
	 * getListViewData3
	 * 
	 * @return String
	 */
	public String getListViewData3() throws YssException {
		String strSql = "select y.* from "
				+ "(select FVchTplCode,FEntityCode,FCheckState from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " "
				+ " where FVchTplCode ="
				+ dbl.sqlString(this.vchTplCode)
				+ " and FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join"
				+ " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
				+ " d.FVocName as DCWayValue ,e.FDesc as FPriceFieldName from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " a"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ " left join Tb_Fun_Vocabulary d on a.FDCWay = d.FVocCode and d.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_DCWay)
				+ " left join (select FAliasName,FDesc from "
				+ pub.yssGetTableName("Tb_Vch_DsTabField")
				+ " where FCheckState=1)e on e.FAliasName=a.FPriceField"
				+ ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode"
				+ " order by y.FVchTplCode,y.FEntityCode";

		return builderListViewData(strSql);
	}

	/**
	 * getListViewData4
	 * 
	 * @return String
	 */
	public String getListViewData4() throws YssException {
		String strSql = "select y.* from "
				+ "(select FVchTplCode,FEntityCode,FCheckState from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " "
				+ " where FVchTplCode ="
				+ dbl.sqlString(this.vchTplCode)
				+
				// " and FCheckState <> 2 group by FVchTplCode,FEntityCode,FCheckState) x join"
				// +
				" group by FVchTplCode,FEntityCode,FCheckState) x join"
				+ // 将删除的数据也显示到前台 by leeyu 2008-10-27 BUG:0000491
				" (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName,"
				+ " d.FVocName as DCWayValue ,e.FDesc as FPriceFieldName,g.FDesc as FEnCuryName from "
				+ pub.yssGetTableName("Tb_Vch_Entity")
				+ " a"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode"
				+ " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode"
				+ " left join Tb_Fun_Vocabulary d on a.FDCWay = d.FVocCode and d.FVocTypeCode = "
				+ dbl.sqlString(YssCons.YSS_DCWay)
				+ " left join (select FAliasName,FDesc from "
				+ pub.yssGetTableName("Tb_Vch_DsTabField")
				+ " where FCheckState=1 and FVchDsCode = "
				+ " (select FDsCode from "
				+ pub.yssGetTableName("Tb_Vch_VchTpl")
				+
				// modify by nimengjing 2010 12 13 bug#507
				" where FVchTplCode = "
				+ dbl.sqlString(this.vchTplCode)
				+ "))e on e.FAliasName=a.FPriceField"
				+ " left join (select FAliasName,FDesc from "
				+ pub.yssGetTableName("Tb_Vch_DsTabField")
				+ " where FCheckState= 1 and FVchDsCode = "
				+ " (select FDsCode from "
				+ pub.yssGetTableName("Tb_Vch_VchTpl")
				+ " where FVchTplCode = "
				+
				// -------------end bug#507---------------------------------
				dbl.sqlString(this.vchTplCode)
				+ "))g on g.FAliasName=a.FEnCuryCode"
				+ ")y on y.FVchTplCode=x.FVchTplCode and y.FEntityCode=x.FEntityCode"
				+ " order by y.FVchTplCode,y.FEntityCode";
		return builderListViewData(strSql);

	}

	public String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
		String sShowDataStr = "";
		String sAllDataStr = "";
		String accountStr = "";
		ResultSet rs = null;
		String sVocStr = "";
		ResultSet subrs = null;
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		try {
			sHeader = this.getListView1Headers();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append(
						super.buildRowShowStr(rs, this.getListView1ShowCols()))
						.append(YssCons.YSS_LINESPLITMARK);
				setEntityAttr(rs);
				// edit by licai 20101207 BUG #477 凭证模板的辅助核算项现实有问题
				// edited by zhouxiang MS01467 凭证模板： 分录设置的辅助核算信息和辅助核算设置不一致
				/*
				 * String temp="";//以凭证号索引数据源中的字段表，索引核算信息表中核算字段对应的核算字段代码
				 * accountStr="  select m.fdesc from "+
				 * pub.yssGetTableName("Tb_Vch_DsTabField")+" m  " +
				 * " join (select a.* from "
				 * +pub.yssGetTableName("tb_vch_Assistant")+"  a where "+
				 * " a.fvchtplcode = "+dbl.sqlString(this.vchTplCode)+
				 * ") n on m.FAliasName=n.fassistantfield where m.FCheckState = 1"
				 * + " and m.FVchDsCode =(select FDsCode from "+
				 * pub.yssGetTableName("Tb_Vch_VchTpl")+" where "+
				 * " FVchTplCode = "+dbl.sqlString(this.vchTplCode)+") ";
				 * subrs=dbl.openResultSet(accountStr); while(subrs.next()) {
				 * temp+="<"+subrs.getString("fdesc")+">_"; }
				 * if(!temp.equals("")&&temp.length()>0){
				 * temp=temp.substring(0,temp.length()-1); } else{
				 * temp=rs.getString("fassistantdesc"); } this.assDesc=temp;
				 * dbl.closeResultSetFinal(subrs);// add by wangzuochun
				 * 2010.11.10 BUG #239 凭证生成时报游标越界错误
				 */// end by zhouxiang MS01467 凭证模板： 分录设置的辅助核算信息和辅助核算设置不一致
				// edit by licai 20101207 BUG
				// #477========================================================end
				bufAll.append(this.buildRowStr()).append(
						YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}

			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}

			VocabularyBean vocabulary = new VocabularyBean();
			vocabulary.setYssPub(pub);

			// sVocStr =
			// vocabulary.getVoc(YssCons.YSS_DCWay+","+YssCons.YSS_CalcWay);
			sVocStr = vocabulary.getVoc(YssCons.YSS_DCWay + ","
					+ YssCons.YSS_CalcWay + "," + YssCons.YSS_VCH_ALLOWS); // QDV4深圳2009年01月15日02_B
																			// MS00194
																			// by
																			// leeyu
																			// 20090209
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr
					+ "\r\f" + this.getListView1ShowCols() + "\r\f" + "voc"
					+ sVocStr;

		} catch (Exception e) {
			throw new YssException("获取证券分录信息出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
			// edit by licai 20101207 BUG #477 凭证模板的辅助核算项现实有问题
			// dbl.closeResultSetFinal(subrs); //add by fangjiang 2010.11.08 BUG
			// #239 凭证生成时报游标越界错误
			// edit by licai 20101207 BUG #477===========================end
		}
	}

	public void setEntityAttr(ResultSet rs) throws SQLException {

		this.vchTplCode = rs.getString("FVchTplCode") + "";
		this.entityCode = rs.getString("FEntityCode") + "";
		this.entityName = rs.getString("FEntityName") + "";
		this.dCWay = rs.getString("FDCWay");
		this.dCalcWay = rs.getString("FCalcWay"); // 计算方式
		this.priceFieldCode = rs.getString("FPriceField");
		this.priceFieldName = rs.getString("FPriceFieldName");
		this.resumeDesc = rs.getString("FResumeDesc");
		this.subjectDesc = rs.getString("FSubjectCode");
		this.moneyDesc = rs.getString("FMoneyDesc") + "";
		this.amountDesc = rs.getString("FAmountDesc") + "";
		this.setMoneyDesc = rs.getString("FSetMoneyDesc") + "";
		this.condDesc = rs.getString("FCondDesc");
		this.assDesc = rs.getString("FAssistantDesc");
		this.entityInd = rs.getString("FEntityInd");
		this.assDesc = rs.getString("FAssistantDesc");
		this.enCuryCode = rs.getString("FEnCuryCode");
		this.enCuryName = rs.getString("FEnCuryName");
		this.desc = rs.getString("FDesc") + "";
		this.sAllows = rs.getString("FAllow"); // QDV4深圳2009年01月15日02_B MS00194
												// by leeyu 20092009
		super.setRecLog(rs);
	}

	/**
	 * addSetting
	 * 
	 * @return String
	 */
	public String addSetting() throws YssException {
		Connection con = dbl.loadConnection();
		String[] str = null;
		boolean bTrans = false;
		String strSql = "";
		VchEntityResumeBean resume = new VchEntityResumeBean();
		VchEntitySubjectBean subject = new VchEntitySubjectBean();
		VchEntityMABean m = new VchEntityMABean();
		VchEntityMABean a = new VchEntityMABean();
		VchEntityMABean s = new VchEntityMABean();
		VchEntityCondBean cond = new VchEntityCondBean();
		VchAssistantBean ass = new VchAssistantBean();

		resume.setYssPub(pub);
		subject.setYssPub(pub);
		m.setYssPub(pub);
		a.setYssPub(pub);
		s.setYssPub(pub);
		cond.setYssPub(pub);
		ass.setYssPub(pub);
		//add by nimengjing 2010.12.16 BUG #669 凭证模板复制分录信息保存时，系统报错。 
		if ((this.oldVchTplCode != null && this.oldEntityCode != null)) {
	          this.oldVchTplCode="";
	          this.oldEntityCode="";
	          this.checkInput(YssCons.OP_ADD);
		}
		//----------------------------end BUG #669----------------------------------
		try {
			strSql = " insert into "
					+ pub.yssGetTableName("Tb_Vch_Entity")
					+ " (FVchTplCode,FEntityCode,FEntityName,"
					+ " FDCWay,FCalcWay,FEnCuryCode,FPriceField,FResumeDesc,"
					+ " FSubjectCode,FMoneyDesc,FAmountDesc,FSetMoneyDesc,FCondDesc,FAssistantDesc,FEntityInd,"
					+ " FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FAllow)"
					+ // 添加新字段FAllow QDV4深圳2009年01月15日02_B MS00194 by leeyu
						// 20090209
					" values("
					+ dbl.sqlString(this.vchTplCode)
					+ ","
					+ dbl.sqlString(this.entityCode)
					+ ","
					+ dbl.sqlString(this.entityName)
					+ ","
					+ dbl.sqlString(this.dCWay)
					+ ","
					+ dbl.sqlString(this.dCalcWay)
					+ ","
					+ dbl.sqlString(this.enCuryCode)
					+ ","
					+ dbl.sqlString(this.priceFieldCode.trim())
					+ ","
					+

					dbl.sqlString(this.resumeDesc)
					+ ","
					+ dbl.sqlString(this.subjectDesc)
					+ ","
					+ dbl.sqlString(this.moneyDesc)
					+ ","
					+ dbl.sqlString(this.amountDesc)
					+ ","
					+ dbl.sqlString(this.setMoneyDesc)
					+ ","
					+ dbl.sqlString(this.condDesc)
					+ ","
					+ dbl.sqlString(this.assDesc)
					+ ","
					+ dbl.sqlString(this.entityInd)
					+ ","
					+ dbl.sqlString(this.desc)
					+ ","
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ","
					+ dbl.sqlString(this.creatorCode)
					+ ","
					+ dbl.sqlString(this.creatorTime)
					+ ","
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode)) + ","
					+ dbl.sqlString(this.sAllows) + // QDV4深圳2009年01月15日02_B
													// MS00194 by leeyu 20090209
					")";
			con.setAutoCommit(false);
			bTrans = true;
			dbl.executeSql(strSql);
			if (this.allSetDatas.length() > 0) {
				str = allSetDatas.split("\r\f");
				if (str[0].length() > 0) {
					resume.setTmpVchTplCode(this.vchTplCode);
					resume.setTmpEntityCode(this.entityCode);
					resume.saveMutliSetting(str[0]);
				} else {
					resume.setVchTplCode(this.vchTplCode);
					resume.setEntityCode(this.entityCode);
					resume.setTmpVchTplCode(this.oldVchTplCode);
					resume.setTmpEntityCode(this.oldEntityCode);
					resume.addSetting();
				}
				if (str[1].length() > 0) {
					subject.setTmpVchTplCode(this.vchTplCode);
					subject.setTmpEntityCode(this.entityCode);
					subject.saveMutliSetting(str[1]);
				} else {
					subject.setVchTplCode(this.vchTplCode);
					subject.setEntityCode(this.entityCode);
					subject.setTmpVchTplCode(this.oldVchTplCode);
					subject.setTmpEntityCode(this.oldEntityCode);
					subject.addSetting();
				}
				if (str[2].length() > 0) {
					m.setTmpVchTplCode(this.vchTplCode);
					m.setTmpEntityCode(this.entityCode);
					m.setTmpType("Money");
					m.saveMutliSetting(str[2]);
				} else {
					m.setVchTplCode(this.vchTplCode);
					m.setEntityCode(this.entityCode);
					m.setTmpVchTplCode(this.oldVchTplCode);
					m.setTmpEntityCode(this.oldEntityCode);
					m.setTmpType("Money");
					m.addSetting();
				}
				if (str[3].length() > 0) {
					if (str[3].split("\t")[3].length() > 0) {

						a.setTmpVchTplCode(this.vchTplCode);
						a.setTmpEntityCode(this.entityCode);
						a.setTmpType("Amount");
						a.saveMutliSetting(str[3]);
					}
				} else {
					a.setVchTplCode(this.vchTplCode);
					a.setEntityCode(this.entityCode);
					a.setTmpVchTplCode(this.oldVchTplCode);
					a.setTmpEntityCode(this.oldEntityCode);
					a.setTmpType("Amount");
					a.addSetting();
				}
				if (str[4].length() > 0) {
					if (str[4].split("\t")[3].length() > 0) {

						s.setTmpVchTplCode(this.vchTplCode);
						s.setTmpEntityCode(this.entityCode);
						s.setTmpType("SetMoney");
						s.saveMutliSetting(str[4]);
					}
				} else {
					s.setVchTplCode(this.vchTplCode);
					s.setEntityCode(this.entityCode);
					s.setTmpVchTplCode(this.oldVchTplCode);
					s.setTmpEntityCode(this.oldEntityCode);
					s.setTmpType("SetMoney");
					s.addSetting();
				}

				if (str[5].length() > 0) {
					if (str[5].split("\t")[3].length() > 0) {
						cond.setTmpVchTplCode(this.vchTplCode);
						cond.setTmpEntityCode(this.entityCode);
						cond.saveMutliSetting(str[5]);
					}
				} else {
					cond.setVchTplCode(this.vchTplCode);
					cond.setEntityCode(this.entityCode);
					cond.setTmpVchTplCode(this.oldVchTplCode);
					cond.setTmpEntityCode(this.oldEntityCode);
					cond.addSetting();
				}
				if (str[6].length() > 0) {
					ass.setTmpVchTplCode(this.vchTplCode);
					ass.setTmpEntityCode(this.entityCode);
					ass.saveMutliSetting(str[6]);
				} else {
					ass.setVchTplCode(this.vchTplCode);
					ass.setEntityCode(this.entityCode);
					ass.setTmpVchTplCode(this.oldVchTplCode);
					ass.setTmpEntityCode(this.oldEntityCode);
					ass.addSetting();
				}
			}
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		}

		catch (Exception ex) {
			throw new YssException("新增凭证模板信息出错!", ex);
		} finally {
			dbl.endTransFinal(con, bTrans);
		}
		return "";

	}

	/**
	 * checkInput
	 * 
	 * @param btOper
	 *            byte
	 */
	public void checkInput(byte btOper) throws YssException {

		dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Vch_Entity"),
				"FVchTplCode,FEntityCode", this.vchTplCode + ","
						+ this.entityCode,
				// edit by licai 20101206 BUG #477 凭证模板的辅助核算项现实有问题
				this.oldVchTplCode + "," + this.oldEntityCode);
	}

	/**
	 * checkSetting
	 */
	public void checkSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection con = dbl.loadConnection();
		try {
			con.setAutoCommit(false);
			bTrans = true;
			// ====增加回收站处理功能 by leeyu 2008-10-21 BUG:0000491
			String[] arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "update " + pub.yssGetTableName("Tb_Vch_Entity")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + "  and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);
				// -----------------------审核子表------------------------------
				strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityResume")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "update "
						+ pub.yssGetTableName("Tb_Vch_EntitySubject")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityCond")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "update " + pub.yssGetTableName("Tb_Vch_Assistant")
						+ " set FCheckState = " + this.checkStateId
						+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
						+ ", FCheckTime = '"
						+ YssFun.formatDatetime(new java.util.Date())
						+ "' where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);
				// -------------------------------------------------------------------
			}
			// ====2008-10-21
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("审核凭证分录信息出错!");
		} finally {
			dbl.endTransFinal(con, bTrans);
		}
	}

	/**
	 * delSetting
	 */
	public void delSetting() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection con = dbl.loadConnection();
		try {
			con.setAutoCommit(false);
			bTrans = true;
			strSql = "update " + pub.yssGetTableName("Tb_Vch_Entity")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date())
					+ "' where FVchTplCode = " + dbl.sqlString(this.vchTplCode)
					+ "  and FEntityCode=" + dbl.sqlString(this.entityCode);
			dbl.executeSql(strSql);

			// ----------------------------------删除子表--------------------------------
			// 将delete 改为update，删除数据在回收站中处理 by leeyu BUG:000491
			// strSql = " delete from " +
			// pub.yssGetTableName("Tb_Vch_EntityResume") +
			strSql = " update " + pub.yssGetTableName("Tb_Vch_EntityResume")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FVchTplCode=" + dbl.sqlString(this.vchTplCode)
					+ " and FEntityCode=" + dbl.sqlString(this.entityCode);
			dbl.executeSql(strSql);

			// strSql = " delete from " +
			// pub.yssGetTableName("Tb_Vch_EntitySubject") +
			strSql = " update " + pub.yssGetTableName("Tb_Vch_EntitySubject")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FVchTplCode=" + dbl.sqlString(this.vchTplCode)
					+ " and FEntityCode=" + dbl.sqlString(this.entityCode);
			dbl.executeSql(strSql);

			// strSql = " delete from " + pub.yssGetTableName("Tb_Vch_EntityMA")
			// +
			strSql = " update " + pub.yssGetTableName("Tb_Vch_EntityMA")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FVchTplCode=" + dbl.sqlString(this.vchTplCode)
					+ " and FEntityCode=" + dbl.sqlString(this.entityCode);
			dbl.executeSql(strSql);

			// strSql = " delete from " +
			// pub.yssGetTableName("Tb_Vch_EntityCond") +
			strSql = " update " + pub.yssGetTableName("Tb_Vch_EntityCond")
					+ " set FCheckState = " + this.checkStateId
					+ ", FCheckUser = " + dbl.sqlString(pub.getUserCode())
					+ ", FCheckTime = '"
					+ YssFun.formatDatetime(new java.util.Date()) + "'"
					+ " where FVchTplCode=" + dbl.sqlString(this.vchTplCode)
					+ " and FEntityCode=" + dbl.sqlString(this.entityCode);
			dbl.executeSql(strSql);
			// ============2008-10-27
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception ex) {
			throw new YssException("删除凭证模板信息出错!");
		} finally {
			dbl.endTransFinal(con, bTrans);
		}

	}

	/**
	 * editSetting
	 * 
	 * @return String
	 */
	public String editSetting() throws YssException {
		Connection con = dbl.loadConnection();
		boolean bTrans = false;
		String strSql = "";
		String[] str = null;
		VchEntityResumeBean resume = new VchEntityResumeBean();
		VchEntitySubjectBean subject = new VchEntitySubjectBean();
		VchEntityMABean m = new VchEntityMABean();
		VchEntityMABean a = new VchEntityMABean();
		VchEntityMABean s = new VchEntityMABean();
		VchEntityCondBean cond = new VchEntityCondBean();
		VchAssistantBean ass = new VchAssistantBean();
		resume.setYssPub(pub);
		subject.setYssPub(pub);
		m.setYssPub(pub);
		a.setYssPub(pub);
		s.setYssPub(pub);
		cond.setYssPub(pub);
		ass.setYssPub(pub);
		try {
			con.setAutoCommit(false);
			bTrans = true;
			strSql = "update "
					+ pub.yssGetTableName("Tb_Vch_Entity")
					+ " set FVchTplCode = "
					+ dbl.sqlString(this.vchTplCode)
					+ ",FEntityCode="
					+ dbl.sqlString(this.entityCode)
					+ ",FEntityName="
					+ dbl.sqlString(this.entityName)
					+ ",FDCWay="
					+ dbl.sqlString(this.dCWay)
					+ ",FCalcWay="
					+ dbl.sqlString(this.dCalcWay)
					+ // by sunny
					",FEnCuryCode="
					+ dbl.sqlString(this.enCuryCode)
					+ ",FPriceField="
					+ dbl.sqlString(this.priceFieldCode)
					+ ",FResumeDesc="
					+ dbl.sqlString(this.resumeDesc)
					+ ",FSubjectCode="
					+ dbl.sqlString(this.subjectDesc)
					+ ",FMoneyDesc="
					+ dbl.sqlString(this.moneyDesc)
					+ ",FAmountDesc="
					+ dbl.sqlString(this.amountDesc)
					+ ",FSetMoneyDesc="
					+ dbl.sqlString(this.setMoneyDesc)
					+ ",FCondDesc="
					+ dbl.sqlString(this.condDesc)
					+ ",FEntityInd="
					+ dbl.sqlString(this.entityInd)
					+ ",FDesc="
					+ dbl.sqlString(this.desc)
					+ ",FAssistantDesc="
					+ dbl.sqlString(this.assDesc)
					+ // add by licai 20101207 BUG #477 凭证模板的辅助核算项现实有问题
					",FAllow="
					+ dbl.sqlString(this.sAllows)
					+ // QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
					",FCheckstate= "
					+ (pub.getSysCheckState() ? "0" : "1")
					+ ",FCreator = "
					+ dbl.sqlString(this.creatorCode)
					+ ",FCreateTime = "
					+ dbl.sqlString(this.creatorTime)
					+ ",FCheckUser = "
					+ (pub.getSysCheckState() ? "' '" : dbl
							.sqlString(this.creatorCode))
					+ " where FVchTplCode = "
					+ dbl.sqlString(this.oldVchTplCode) + " and FEntityCode="
					+ dbl.sqlString(this.oldEntityCode);
			dbl.executeSql(strSql);

			if (this.allSetDatas.length() > 0) {
				str = allSetDatas.split("\r\f");
				if (str[0].length() > 0) {
					resume.setTmpVchTplCode(this.vchTplCode);
					resume.setTmpEntityCode(this.entityCode);
					resume.saveMutliSetting(str[0]);
				} else {
					strSql = "update "
							+ pub.yssGetTableName("Tb_Vch_EntityResume")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode);
					dbl.executeSql(strSql);
				}
				if (str[1].length() > 0) {
					subject.setTmpVchTplCode(this.vchTplCode);
					subject.setTmpEntityCode(this.entityCode);
					subject.saveMutliSetting(str[1]);
				} else {
					strSql = "update "
							+ pub.yssGetTableName("Tb_Vch_EntitySubject")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode);
					dbl.executeSql(strSql);
				}

				if (str[2].length() > 0) {
					// if(str[2].split("\t")[2].equals("Money"))
					// {
					m.setTmpVchTplCode(this.vchTplCode);
					m.setTmpEntityCode(this.entityCode);
					m.setTmpType("Money");
					m.saveMutliSetting(str[2]);
					// }
				} else {
					strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode)
							+ "  and FType=" + dbl.sqlString("Money");
					dbl.executeSql(strSql);

				}
				if (str[3].length() > 0) {
					// if(str[3].split("\t")[2].equals("Amount"))
					// {
					a.setTmpVchTplCode(this.vchTplCode);
					a.setTmpEntityCode(this.entityCode);
					a.setTmpType("Amount");
					a.saveMutliSetting(str[3]);
					// }
				} else {
					strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode)
							+ "  and FType=" + dbl.sqlString("Amount");
					dbl.executeSql(strSql);
				}
				if (str[4].length() > 0) {
					s.setTmpVchTplCode(this.vchTplCode);
					s.setTmpEntityCode(this.entityCode);
					s.setTmpType("SetMoney");
					s.saveMutliSetting(str[4]);
				} else {
					strSql = "update " + pub.yssGetTableName("Tb_Vch_EntityMA")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode)
							+ "  and FType=" + dbl.sqlString("SetMoney");
					dbl.executeSql(strSql);
				}

				if (str[5].length() > 0) {
					if (str[5].split("\t")[3].length() > 0) {
						cond.setTmpVchTplCode(this.vchTplCode);
						cond.setTmpEntityCode(this.entityCode);
						cond.saveMutliSetting(str[5]);
					}
				} else {
					strSql = "update "
							+ pub.yssGetTableName("Tb_Vch_EntityCond")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode);
					dbl.executeSql(strSql);
				}
				if (str[6].length() > 0) {
					ass.setTmpVchTplCode(this.vchTplCode);
					ass.setTmpEntityCode(this.entityCode);
					ass.saveMutliSetting(str[6]);
				} else {
					strSql = "update "
							+ pub.yssGetTableName("Tb_Vch_Assistant")
							+ "  set FVchTplCode = "
							+ dbl.sqlString(this.vchTplCode)
							+ "  ,FEntityCode ="
							+ dbl.sqlString(this.entityCode)
							+ "  where FVchTplCode="
							+ dbl.sqlString(this.oldVchTplCode)
							+ "  and FEntityCode="
							+ dbl.sqlString(this.oldEntityCode);
					dbl.executeSql(strSql);
				}

			}
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception ex) {
			throw new YssException("更改凭证分录信息出错!");
		} finally {
			dbl.endTransFinal(con, bTrans);
		}
		return "";

	}

	/**
	 * getAllSetting
	 * 
	 * @return String
	 */
	public String getAllSetting() {
		return "";
	}

	/**
	 * getSetting
	 * 
	 * @return IDataSetting
	 */
	public IDataSetting getSetting() {
		return null;
	}

	/**
	 * saveMutliSetting
	 * 
	 * @param sMutilRowStr
	 *            String
	 * @return String
	 */
	public String saveMutliSetting(String sMutilRowStr) {
		return "";
	}

	/**
	 * getTreeViewData1
	 * 
	 * @return String
	 */
	public String getTreeViewData1() {
		return "";
	}

	/**
	 * getTreeViewData2
	 * 
	 * @return String
	 */
	public String getTreeViewData2() {
		return "";
	}

	/**
	 * getTreeViewData3
	 * 
	 * @return String
	 */
	public String getTreeViewData3() {
		return "";
	}

	/**
	 * buildRowStr
	 * 
	 * @return String
	 */
	public String buildRowStr() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.vchTplCode).append("\t");
		buf.append(this.entityCode).append("\t");
		buf.append(this.entityName).append("\t");

		buf.append(this.dCWay).append("\t");

		buf.append(this.priceFieldCode).append("\t");
		buf.append(this.priceFieldName).append("\t");

		buf.append(this.resumeDesc).append("\t");
		buf.append(this.subjectDesc).append("\t");
		buf.append(this.moneyDesc).append("\t");
		buf.append(this.amountDesc).append("\t");
		buf.append(this.condDesc).append("\t");

		buf.append(this.entityInd).append("\t");
		buf.append(this.desc).append("\t");
		buf.append(this.assDesc).append("\t");
		buf.append(this.setMoneyDesc).append("\t");
		buf.append(this.dCalcWay).append("\t");
		buf.append(this.enCuryCode).append("\t");
		buf.append(this.enCuryName).append("\t");
		buf.append(this.sAllows).append("\t"); // QDV4深圳2009年01月15日02_B MS00194
												// by leeyu 20090209
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	/**
	 * getOperValue
	 * 
	 * @param sType
	 *            String
	 * @return String
	 */
	public String getOperValue(String sType) {
		return "";

	}

	/**
	 * parseRowStr
	 * 
	 * @param sRowStr
	 *            String
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		String sTmpStr = "";
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			if (sRowStr.indexOf("\r\t") >= 0) {
				sTmpStr = sRowStr.split("\r\t")[0];
				if (sRowStr.split("\r\t").length == 3) {
					this.allSetDatas = sRowStr.split("\r\t")[2];
				}
			} else {
				sTmpStr = sRowStr;
			}
			reqAry = sTmpStr.split("\t");
			sRecycled = sTmpStr; // 回收站处理字段赋值 by leeyu BUG:0000491
			this.vchTplCode = reqAry[0];
			this.entityCode = reqAry[1];
			this.entityName = reqAry[2];
			this.dCWay = reqAry[3];

			this.priceFieldCode = reqAry[4];

			this.resumeDesc = reqAry[5];
			this.subjectDesc = reqAry[6];
			this.moneyDesc = reqAry[7];
			this.amountDesc = reqAry[8];
			this.condDesc = reqAry[9];

			this.entityInd = reqAry[10];
			this.desc = reqAry[11];

			this.checkStateId = Integer.parseInt(reqAry[12]);
			this.oldVchTplCode = reqAry[13];
			this.oldEntityCode = reqAry[14];
			this.assDesc = reqAry[15];
			this.setMoneyDesc = reqAry[16];

			this.dCalcWay = reqAry[17];
			this.enCuryCode = reqAry[18];
			this.sAllows = reqAry[19]; // QDV4深圳2009年01月15日02_B MS00194 by leeyu
										// 20090209
			super.parseRecLog();
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					if (this.filterType == null) {
						this.filterType = new VchEntityBean();
						this.filterType.setYssPub(pub);
					}
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
		} catch (Exception e) {
			throw new YssException("解析凭证分录信息出错", e);
		}
	}

	/**
	 * getBeforeEditData
	 * 
	 * @return String
	 */
	public String getBeforeEditData() {
		return "";
	}

	/**
	 * buildFilterSql 筛选条件
	 * 
	 * @return String
	 */
	private String buildFilterSql() throws YssException {
		String sResult = "";
		if (this.filterType != null) {
			sResult = " where 1=1";
			if (this.filterType.vchTplCode.length() != 0) {
				sResult = sResult + " and a.FVchTplCode like '"
						+ filterType.vchTplCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.entityCode.length() != 0) {
				sResult = sResult + " and a.FEntityCode like '"
						+ filterType.entityCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.entityName.length() != 0) {
				sResult = sResult + " and a.FEntityName like '"
						+ filterType.entityName.replaceAll("'", "''") + "%'";
			}
			//BUG2934 QD系统在weblogic环境下进行测试发现，默认不是所有，选择‘所有’筛选不出数据 add by jiangshichao 2011.10.18
			if (this.filterType.dCWay.length() != 0 && !filterType.dCWay.equalsIgnoreCase("99")) {
				sResult = sResult + " and a.FDCWay like '"
						+ filterType.dCWay.replaceAll("'", "''") + "%'";
			}
			//BUG2934 QD系统在weblogic环境下进行测试发现，默认不是所有，选择‘所有’筛选不出数据 add by jiangshichao 2011.10.18
			if (this.filterType.dCalcWay.length() != 0 && !filterType.dCalcWay.equalsIgnoreCase("99")) {
				sResult = sResult + " and a.FCalcWay like '"
						+ filterType.dCalcWay.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.enCuryCode.length() != 0) {
				sResult = sResult + " and a.FEnCuryCode like '"
						+ filterType.enCuryCode.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.priceFieldCode.length() != 0) {
				sResult = sResult + " and a.FPriceField like '"
						+ filterType.priceFieldCode.replaceAll("'", "''")
						+ "%'";
			}
			if (this.filterType.resumeDesc.length() != 0) {
				sResult = sResult + " and a.FResumeCode like '"
						+ filterType.resumeDesc.replaceAll("'", "''") + "%'";
			}

			if (this.filterType.subjectDesc.length() != 0) {
				sResult = sResult + " and a.FSubjectCode like '"
						+ filterType.subjectDesc.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.moneyDesc.length() != 0) {
				sResult = sResult + " and a.FMoneyCode like '"
						+ filterType.moneyDesc.replaceAll("'", "''") + "%'";
			}

			if (this.filterType.amountDesc.length() != 0) {
				sResult = sResult + " and a.FAmountCode like '"
						+ filterType.amountDesc.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.condDesc.length() != 0) {
				sResult = sResult + " and a.FCondCode like '"
						+ filterType.condDesc.replaceAll("'", "''") + "%'";
			}

			if (this.filterType.entityInd.length() != 0) {
				sResult = sResult + " and a.FEntityInd like '"
						+ filterType.entityInd.replaceAll("'", "''") + "%'";
			}
			if (this.filterType.desc.length() != 0) {
				sResult = sResult + " and a.FDesc like '"
						+ filterType.desc.replaceAll("'", "''") + "%'";
			}
			/**
			 * QDV4深圳2009年01月15日02_B MS00194 by leeyu 20090209
			 */
			//BUG2934 QD系统在weblogic环境下进行测试发现，默认不是所有，选择‘所有’筛选不出数据 add by jiangshichao 2011.10.18
			if (filterType.sAllows.length() != 0 && !filterType.sAllows.equalsIgnoreCase("99")) {
				sResult = sResult + " and a.FAllow ='"
						+ filterType.sAllows.replaceAll("'", "''") + "'"; // 因为前台是下拉框不用like
			}
		}
		return sResult;
	}

	/**
	 * deleteRecycleData 完善回收站功能 by leeyu 2008-10-21 BUG:0000491
	 */
	public void deleteRecycleData() throws YssException {
		String strSql = "";
		boolean bTrans = false; // 代表是否开始了事务
		Connection con = dbl.loadConnection();
		try {
			con.setAutoCommit(false);
			bTrans = true;
			String[] arrData = sRecycled.split("\r\n");
			for (int i = 0; i < arrData.length; i++) {
				if (arrData[i].length() == 0) {
					continue;
				}
				this.parseRowStr(arrData[i]);
				strSql = "delete " + pub.yssGetTableName("Tb_Vch_Entity")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + "  and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);
				// -----------------------审核子表------------------------------
				strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityResume")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "delete "
						+ pub.yssGetTableName("Tb_Vch_EntitySubject")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityMA")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "delete " + pub.yssGetTableName("Tb_Vch_EntityCond")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);

				strSql = "delete " + pub.yssGetTableName("Tb_Vch_Assistant")
						+ " where FVchTplCode = "
						+ dbl.sqlString(this.vchTplCode) + " and FEntityCode="
						+ dbl.sqlString(this.entityCode);
				dbl.executeSql(strSql);
			}
			con.commit();
			bTrans = false;
			con.setAutoCommit(true);
		} catch (Exception e) {
			throw new YssException("清除凭证分录信息出错!");
		} finally {
			dbl.endTransFinal(con, bTrans);
		}
	}

	public String getTreeViewGroupData1() throws YssException {
		return "";
	}

	public String getTreeViewGroupData2() throws YssException {
		return "";
	}

	public String getTreeViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData1() throws YssException {
		return "";
	}

	public String getListViewGroupData2() throws YssException {
		return "";
	}

	public String getListViewGroupData3() throws YssException {
		return "";
	}

	public String getListViewGroupData4() throws YssException {
		return "";
	}

	public String getListViewGroupData5() throws YssException {
		return "";
	}

}
