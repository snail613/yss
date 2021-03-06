package com.yss.main.parasetting;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

import java.math.BigDecimal;
import java.sql.*;

public class InvestRelaSetBean extends BaseDataSettingBean implements IDataSetting {
	private String iVPayCatCode="";//运营收支品种代码
	private String iVPayCatName ="";
	private String catCode="";//品种代码
	private String catName="";
	private double fixRate=0;//固定费率
	private String roundCode="";//舍入条件
	private String roundName="";
	private String perExpCode="";//比率公式
	private String perExpName="";
	private String portCode="";//组合代码
	private String portname="";
	private String desc="";//描述
	private String analysisCode1="";//分析代码1
	private String analysisName1="";
	private String analysisCode2="";//分析代码2
	private String analysisName2="";
	private String analysisCode3="";//分析代码3
	private String analysisName3="";
	
	private InvestRelaSetBean filterType=null;

	public String getiVPayCatName() {
		return iVPayCatName;
	}

	public void setiVPayCatName(String iVPayCatName) {
		this.iVPayCatName = iVPayCatName;
	}


	public String getRoundName() {
		return roundName;
	}

	public String getCatCode() {
		return catCode;
	}

	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getAnalysisCode1() {
		return analysisCode1;
	}

	public void setAnalysisCode1(String analysisCode1) {
		this.analysisCode1 = analysisCode1;
	}

	public String getAnalysisName1() {
		return analysisName1;
	}

	public void setAnalysisName1(String analysisName1) {
		this.analysisName1 = analysisName1;
	}

	public String getAnalysisCode2() {
		return analysisCode2;
	}

	public void setAnalysisCode2(String analysisCode2) {
		this.analysisCode2 = analysisCode2;
	}

	public String getAnalysisName2() {
		return analysisName2;
	}

	public void setAnalysisName2(String analysisName2) {
		this.analysisName2 = analysisName2;
	}

	public String getAnalysisCode3() {
		return analysisCode3;
	}

	public void setAnalysisCode3(String analysisCode3) {
		this.analysisCode3 = analysisCode3;
	}

	public String getAnalysisName3() {
		return analysisName3;
	}

	public void setAnalysisName3(String analysisName3) {
		this.analysisName3 = analysisName3;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}

	public String getPerExpName() {
		return perExpName;
	}

	public void setPerExpName(String perExpName) {
		this.perExpName = perExpName;
	}

	public String getPortname() {
		return portname;
	}

	public void setPortname(String portname) {
		this.portname = portname;
	}

	

	public String getiVPayCatCode() {
		return iVPayCatCode;
	}

	public void setiVPayCatCode(String iVPayCatCode) {
		this.iVPayCatCode = iVPayCatCode;
	}
	
	public double getFixRate() {
		return fixRate;
	}

	public void setFixRate(double fixRate) {
		this.fixRate = fixRate;
	}

	public String getRoundCode() {
		return roundCode;
	}

	public void setRoundCode(String roundCode) {
		this.roundCode = roundCode;
	}

	public String getPerExpCode() {
		return perExpCode;
	}

	public void setPerExpCode(String perExpCode) {
		this.perExpCode = perExpCode;
	}

	public String getPortCode() {
		return portCode;
	}

	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public InvestRelaSetBean() {
		
	}

	
	public String addSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub

	}

	
	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		String sqlStr="";
		Connection conn =null;
		boolean bTrans = false;
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			sqlStr = "update "
				+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
				+" set FcheckState="+checkStateId+","
				+ " FCheckUser="+dbl.sqlString(pub.getUserCode())+","
				+" FCheckTime="+dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))
				+ " where FIVPayCatCode=" + dbl.sqlString(iVPayCatCode)+
				" and FPortCode="+dbl.sqlString(portCode)+
				" and FAnalySisCode1="+dbl.sqlString(analysisCode1)+
				" and FAnalySisCode2="+dbl.sqlString(analysisCode2)+
				" and FAnalySisCode3="+dbl.sqlString(analysisCode3);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	
	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		String sqlStr="";
		Connection conn =null;
		boolean bTrans = false;
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			sqlStr = "update "
				+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
				+" set FcheckState="+checkStateId
				+ " where FIVPayCatCode=" + dbl.sqlString(iVPayCatCode)+
				" and FPortCode="+dbl.sqlString(portCode)+
				" and FAnalySisCode1="+dbl.sqlString(analysisCode1)+
				" and FAnalySisCode2="+dbl.sqlString(analysisCode2)+
				" and FAnalySisCode3="+dbl.sqlString(analysisCode3);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	
	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		String sqlStr="";
		Connection conn =null;
		boolean bTrans = false;
		try{
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			sqlStr = "delete from "
				+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
				+ " where FIVPayCatCode=" + dbl.sqlString(iVPayCatCode)+
				" and FPortCode="+dbl.sqlString(portCode)+
				" and FAnalySisCode1="+dbl.sqlString(analysisCode1)+
				" and FAnalySisCode2="+dbl.sqlString(analysisCode2)+
				" and FAnalySisCode3="+dbl.sqlString(analysisCode3);
			dbl.executeSql(sqlStr);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.endTransFinal(conn, bTrans);
		}
	}

	
	public String editSetting() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
		Connection conn =null;
		boolean bTrans =false;
		String sqlStr="";
		PreparedStatement pst=null;
		InvestRelaSetBean investRela = null;
		String[] arrData=null;
		try{
			arrData = sMutilRowStr.split("\r\f");
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans = true;
			//先删除数据
			sqlStr = "delete from "
					+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
					+ " where FIVPayCatCode=" + dbl.sqlString(iVPayCatCode)+
					" and FPortCode="+dbl.sqlString(portCode)+
					" and FAnalySisCode1="+dbl.sqlString(analysisCode1)+
					" and FAnalySisCode2="+dbl.sqlString(analysisCode2)+
					" and FAnalySisCode3="+dbl.sqlString(analysisCode3);
			dbl.executeSql(sqlStr);
			//再插入数据
			sqlStr = "insert into "
					+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
					+ "(FIVPayCatCode,FCatCode,FPortCode,FAnalySisCode1,FAnalySisCode2,FAnalySisCode3,FFixRate,"
					+ "FRoundCode,FPerExpCode,FDesc,FCheckState,FCreator,FCreateTime) "
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
			pst =conn.prepareStatement(sqlStr);
			for(int i=0;i<arrData.length;i++){
				if(arrData[i].length()==0)
					continue;
				investRela = new InvestRelaSetBean();
				investRela.parseRowStr(arrData[i]);
				pst.setString(1,investRela.iVPayCatCode);
				pst.setString(2,investRela.catCode);
				pst.setString(3,investRela.portCode);
				pst.setString(4,investRela.analysisCode1.length()==0?" ":investRela.analysisCode1);
				pst.setString(5,investRela.analysisCode2.length()==0?" ":investRela.analysisCode2);
				pst.setString(6,investRela.analysisCode3.length()==0?" ":investRela.analysisCode3);
				pst.setDouble(7,investRela.fixRate);
				pst.setString(8,investRela.roundCode);
				pst.setString(9,investRela.perExpCode);
				pst.setString(10,investRela.desc);
				pst.setInt(11,investRela.checkStateId);
				pst.setString(12,pub.getUserCode());
				pst.setString(13,YssFun.formatDatetime(new java.util.Date()));
				pst.executeUpdate();
			}
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException("新增运营费用关联设置出错",ex);
		}finally{
			dbl.closeStatementFinal(pst);
			dbl.endTransFinal(conn, bTrans);
		}
		return null;
	}

	
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer();
		buf.append(iVPayCatCode).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(iVPayCatName).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(catCode).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(catName).append(YssCons.YSS_ITEMSPLITMARK1);
    		buf.append(fixRate).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(roundCode).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(roundName).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(perExpCode).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(perExpName).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(portCode).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(portname).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(desc).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisCode1).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisName1).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisCode2).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisName2).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisCode3).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(analysisName3).append(YssCons.YSS_ITEMSPLITMARK1);
    	buf.append(super.buildRecLog());
		return buf.toString();
	}

	
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		String reqAry[] = null;
        String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split(YssCons.YSS_ITEMSPLITMARK1);
            iVPayCatCode=reqAry[0];
        	catCode=reqAry[1];
        	if(YssFun.isNumeric(reqAry[2])){
        		fixRate= YssFun.toDouble(reqAry[2]);
        	}
        	roundCode=reqAry[3];
        	perExpCode=reqAry[4];
        	portCode=reqAry[5];
        	desc=reqAry[6];
        	analysisCode1=reqAry[7];
        	analysisCode2=reqAry[8];
        	analysisCode3=reqAry[9];
        	if(YssFun.isNumeric(reqAry[10])){
        		checkStateId = YssFun.toInt(reqAry[10]);
        	}
			if (sRowStr.indexOf("\r\t") >= 0) {
				if (this.filterType == null) {
					this.filterType = new InvestRelaSetBean();

					this.filterType.setYssPub(pub);
				}
				if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
					this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
				}
			}
        } catch (Exception e) {
            throw new YssException("解析投资运营收支设置信息出错！", e);
        }
	}

	
	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub;
		ResultSet rs =null;
		String sqlStr="";
		String sAry[] = null;
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
		try{
			sHeader = this.getListView1Headers();
			boolean analy1 = operSql.storageAnalysis("FAnalysisCode1",
					YssOperCons.YSS_KCLX_InvestPayRec); // 判断分析代码存不存在
			boolean analy2 = operSql.storageAnalysis("FAnalysisCode2",
					YssOperCons.YSS_KCLX_InvestPayRec);
			boolean analy3 = operSql.storageAnalysis("FAnalysisCode3",
					YssOperCons.YSS_KCLX_InvestPayRec);
			sAry = storageAnalysisSql(YssOperCons.YSS_KCLX_InvestPayRec); // 获得分析代码
			sqlStr = 
				   //"select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,d.FCatName,e.FPortName "
				    "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName,nvl(d.FAttrClsName,' ') as FAttrClsName,e.FPortName " //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
					+ sAry[0]
					+ " ,h.FIvPayCatName,g.FRoundName,f.FFormulaName  as FPerExpName from "
					+ pub.yssGetTableName("Tb_Para_InvestFeeRela")
					+ " a "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode "
					+ " left join (select FUserCode, FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode "
//					+ " left join (select FCatCode, FCatName from tb_base_category) d on a.FCatCode = d.FCatCode "
					 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
	                +" left join (select FAttrClsCode,FAttrClsName from " + pub.yssGetTableName("Tb_Para_AttributeClass") +
	                ") d on a.fcatcode = d.FAttrClsCode " 
	                //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
					+ " left join (select FPortCode, FPortName from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " ) e on a.FPortCode = e.FPortCode "
					+ " left join (select FIvPayCatCode, FIvPayCatName from Tb_Base_Investpaycat) h on a.FIvPayCatCode = h.FIvPayCatCode "
					+ " left join (select FRoundCode, FRoundName from "
					+ pub.yssGetTableName("Tb_Para_Rounding")
					+ " ) g on a.FRoundCode = g.FRoundCode "
					+ " left join (select FFormulaCode, FFormulaName from "+pub.yssGetTableName("Tb_Para_Performula")
					+ " ) f on a.FPerExpCode = f.FFormulaCode"
					+ sAry[1]
	                + buildFilterSql()
	                + " order by a.FCheckState, a.FCreateTime desc , a.FCheckTime desc";
			rs = dbl.openResultSet(sqlStr);
			while(rs.next()){
				bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
				setInvestPayRelaAttr(rs,analy1,analy2,analy3);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }
		}catch(Exception ex){
			throw new YssException("查询运营关联数据出错");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +  "\r\f" +  this.getListView1ShowCols();
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
	
	private String buildFilterSql() throws YssException{
		String filterSql="";
		if(filterType!=null){
			filterSql =" where 1=1 ";
			if(filterType.iVPayCatCode!=null && filterType.iVPayCatCode.trim().length()>0){
				filterSql +=" and a.FIvPayCatCode="+dbl.sqlString(filterType.iVPayCatCode.replaceAll("'", "''"));
			}
			if(filterType.catCode!=null && filterType.catCode.length()>0){
				filterSql +=" and a.FCatCode = '"+filterType.catCode.replaceAll("'", "''")+"'";
			}
			if(filterType.portCode!=null && filterType.portCode.length()>0){
				filterSql +=" and a.FPortCode = '"+filterType.portCode.replaceAll("'", "''")+"'";
			}
			if(filterType.analysisCode1!=null && filterType.analysisCode1.length()>0){
				filterSql +=" and a.FAnalySisCode1 = '"+filterType.analysisCode1.replaceAll("'", "''")+"'";
			}
			if(filterType.analysisCode2!=null && filterType.analysisCode2.length()>0){
				filterSql +=" and a.FAnalySisCode2 = '"+filterType.analysisCode2.replaceAll("'", "''")+"'";
			}
			if(filterType.analysisCode3!=null && filterType.analysisCode3.length()>0){
				filterSql +=" and a.FAnalySisCode3 = '"+filterType.analysisCode3.replaceAll("'", "''")+"'";
			}
		}
		return filterSql;
	}
	
	private void setInvestPayRelaAttr(ResultSet rs,boolean analy1,boolean analy2,boolean analy3)throws SQLException, YssException{
		iVPayCatCode = rs.getString("FIvPayCatCode");
		iVPayCatName = rs.getString("FIvPayCatName");
		catCode= rs.getString("FCatCode");
		 //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
		//catName= rs.getString("FCatName");
		catName= rs.getString("FAttrClsName");
		//--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao end ------------//
		fixRate= rs.getDouble("FFixRate");
		roundCode= rs.getString("FRoundCode");
		roundName= rs.getString("FRoundName");
		perExpCode= rs.getString("FPerExpCode");
		perExpName= rs.getString("FPerExpName");
		portCode= rs.getString("FPortCode");
		portname= rs.getString("FPortName");
		desc= rs.getString("FDesc");
		if(analy1){
			analysisCode1= rs.getString("FAnalySisCode1");
			analysisName1= rs.getString("FAnalySisName1");
		}else{
			analysisCode1= " ";
			analysisName1= " ";
		}
		if(analy2){
			analysisCode2= rs.getString("FAnalySisCode2");
			analysisName2= rs.getString("FAnalySisName2");
		}else{
			analysisCode2= " ";
			analysisName2= " ";
		}
		if(analy3){
			analysisCode3= rs.getString("FAnalySisCode3");
			analysisName3= rs.getString("FAnalySisName3");
		}else{
			analysisCode3= " ";
			analysisName3= " ";
		}
	}
	
	private String[] storageAnalysisSql(String sStorageType) throws YssException {
        String[] sResult = new String[2];
        String strSql = "";
        ResultSet rs = null;
        try {
            sResult[0] = "";
            sResult[1] = "";
            strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_StorageCfg") +
                " where FCheckState = 1 and FStorageType = '" + sStorageType +
                "'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                for (int i = 1; i <= 3; i++) {
                    if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                        rs.getString("FAnalysisCode" + String.valueOf(i)).
                        equalsIgnoreCase("002")) {
                        sResult[0] = sResult[0] + ", broker.FAnalysisName" + i +" as FAnalySisName"+i;
                        sResult[1] = sResult[1] +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                           /* " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                            i +
                            " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                            pub.yssGetTableName("tb_para_broker") +
                            " where FStartDate < " +
                            dbl.sqlDate(new java.util.Date()) +
                            " and FCheckState = 1 group by FBrokerCode )x " +
                            " join (select * from " +
                            pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker on a.FAnalysisCode" +
                            i + " = broker.FBrokerCode";*/
                        
                        " left join (select y.FBrokerCode ,y.FBrokerName  as FAnalysisName" +
                        i +
                        " from  " +
                        pub.yssGetTableName("tb_para_broker") +
                        " y where y.FCheckState = 1) broker on a.FAnalysisCode" +
                        i + " = broker.FBrokerCode";
                        //end by lidaolong
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("003")) {
                        sResult[0] = sResult[0] + " , exchange.FAnalysisName" + i +
                            " as FAnalySisName"+i;
                        sResult[1] = sResult[1] +
                            " left join (select FExchangeCode,FExchangeName as FAnalysisName" +
                            i +
                            " from tb_base_exchange) exchange on a.FAnalysisCode" +
                            i + " = exchange.FExchangeCode ";                        
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("001")) {
                        sResult[0] = sResult[0] + " , invmgr.FAnalysisName" + i +
                            " as FAnalySisName"+i;
                        sResult[1] = sResult[1] +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                           /* " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                            i +
                            "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                            pub.yssGetTableName("tb_para_investmanager") +
                            " where FStartDate < " +
                            dbl.sqlDate(new java.util.Date()) +
                            " and FCheckState = 1 group by FInvMgrCode )m " +
                            "join (select * from " +
                            pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr on a.FAnalysisCode" +
                            i + " = invmgr.FInvMgrCode ";*/
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName" +
                        i +
                        "  from  " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where n.FCheckState = 1) invmgr on a.FAnalysisCode" +
                        i + " = invmgr.FInvMgrCode ";
                        
                        //end by lidaolong 
                    } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                               rs.getString("FAnalysisCode" + String.valueOf(i)).
                               equalsIgnoreCase("004")) {
                        sResult[0] = sResult[0] + " , category.FCatName" +
                        " as FAnalySisName"+i;
                        sResult[1] = sResult[1] +
                            " left join (select FCatCode,FCatName from Tb_Base_Category) category on a.FAnalysisCode" +
                            i + " = category.FCatCode";
                    } else {
                        sResult[1] = sResult[1] +
                            " left join (select '' as FAnalysisNull , '' as FAnalysisName" +
                            i + " from  " +
                            pub.yssGetTableName("Tb_Para_StorageCfg") +
                            " where 1=2) tn" + i + " on a.FAnalysisCode" + i +
                            " = tn" +
                            i + ".FAnalysisNull ";
                    }
                }
            }
            dbl.closeResultSetFinal(rs);
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取库存配置SQL出错");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
