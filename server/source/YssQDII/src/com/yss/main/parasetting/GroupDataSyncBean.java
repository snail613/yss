package com.yss.main.parasetting;

import java.math.*;
import java.sql.*;
import java.sql.Date;

import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.storagemanage.RollAssetBean;
import java.util.Calendar;
import com.yss.util.*;

/**
*by yanghaiming 20100722 MS01374 QDV4汇添富2010年06月28日01_AB
* <p>Title: PortfolioBean</p>
* <p>Description:分组数据同步 </p>
* <p>Copyright: Copyright (c) 2006</p>
* <p>Company: </p>
* @author not attributable
* @version 1.0
*/
public class GroupDataSyncBean 
	extends BaseDataSettingBean implements IDataSetting{
	private String mergePortCode = "";//汇总组合代码
	private String mergePortName = "";//汇总组合名称
	private String detailPortCode = "";//明细组合代码
	private String detailPortName = "";//名字组合名称
	private double scale = 0;//比例
	private String portCode = "";//轧差组合代码
	private String portName = "";//轧差组合名称
	private int splitType = 1;//拆分方式
	private String isCheck = "0";//是否已被设置
	boolean flag = false;
	
	public String getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(String isCheck) {
		this.isCheck = isCheck;
	}
	public String getMergePortCode() {
		return mergePortCode;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getPortName() {
		return portName;
	}
	public void setPortName(String portName) {
		this.portName = portName;
	}
	public int getSplitType() {
		return splitType;
	}
	public void setSplitType(int splitType) {
		this.splitType = splitType;
	}
	public void setMergePortCode(String mergePortCode) {
		this.mergePortCode = mergePortCode;
	}
	public String getMergePortName() {
		return mergePortName;
	}
	public void setMergePortName(String mergePortName) {
		this.mergePortName = mergePortName;
	}
	public String getDetailPortCode() {
		return detailPortCode;
	}
	public void setDetailPortCode(String detailPortCode) {
		this.detailPortCode = detailPortCode;
	}
	public String getDetailPortName() {
		return detailPortName;
	}
	public void setDetailPortName(String detailPortName) {
		this.detailPortName = detailPortName;
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
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
		
	}
	public void delSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}
	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub
		
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
		String strSql = "";
		PreparedStatement pst = null;
		boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        String reqAry[] = sMutilRowStr.split("\r\n"); 
        int result = 0;
        try{
        	//在保存拆分设置时先进行删除
        	conn.setAutoCommit(false);
            bTrans = true;
			strSql = "delete from "
				+ pub.yssGetTableName("Tb_para_groupdatasync")
				+ " where Fmergeportcode = '" + this.mergePortCode.toString() + "'";
			dbl.executeSql(strSql);
			strSql = "insert into " + pub.yssGetTableName("Tb_para_groupdatasync") +
					"(Fmergeportcode,Fdetailportcode,Fscale,FPORTCODE,FSPLITTYPE)" +
					"values(?,?,?,?,?)";
			pst = conn.prepareStatement(strSql);
			for(int i = 0; i < reqAry.length; i++){
				this.parseRowStr(reqAry[i]);
				pst.setString(1, this.mergePortCode);
				pst.setString(2, this.detailPortCode);
				pst.setDouble(3, this.scale);
				pst.setString(4, this.portCode);
				pst.setInt(5, this.splitType);
				result = pst.executeUpdate();
			}
			conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return result + "";
        }catch(Exception e){
        	throw new YssException("插入分组信息错误", e);
        }
	}
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public String buildRowStr() throws YssException {
		//add by yanghaiming 20100713 MS01374 QDV4汇添富2010年06月28日01_AB
		StringBuffer buf = new StringBuffer();
		buf.append(this.mergePortCode.trim()).append("\t");
		buf.append(this.mergePortName.trim()).append("\t");
		buf.append(this.detailPortCode.trim()).append("\t");
		buf.append(this.detailPortName.trim()).append("\t");
		buf.append(this.scale + "").append("\t");
		buf.append(this.portCode.trim()).append("\t");
		buf.append(this.portName.trim()).append("\t");
		buf.append(this.splitType + "").append("\t");
		buf.append(this.isCheck).append("\t");
		buf.append(super.buildRecLog());
		return buf.toString();
	}

	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public void parseRowStr(String sRowStr) throws YssException {
		String reqAry[] = null;
		String sTmpStr = "";
        try {
            if (sRowStr.equals("")) {
                return;
            }
            if (sRowStr.indexOf("\r\n") >= 0){
            	sRowStr = sRowStr.split("\r\n")[0];
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");
            this.mergePortCode = reqAry[0];
            this.mergePortName = reqAry[1];
            this.detailPortCode = reqAry[2];
            this.detailPortName = reqAry[3];
            this.scale = Double.parseDouble(reqAry[4]);
            this.portCode = reqAry[5];
            this.portName = reqAry[6];
            this.splitType = Integer.parseInt(reqAry[7]);
            if(Integer.parseInt(reqAry[7]) == 0){
            	this.flag = false;
            }else{
            	this.flag = true;
            }
        } catch (Exception e) {
            throw new YssException("解析汇总组合出错", e);
        }
	}
	//获取汇总组合相关的明细组合列表
	public String getListViewData1() throws YssException {
		String strSql = "";
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
        	//edit by yanghaiming 20100713 MS01374 QDV4汇添富2010年06月28日01_AB
			strSql = "select a.*, b.fportcode, b.fportname, b.fsplittype" +
					" from (select FdetailPortCode, b.FportName as Fdetailportname, FScale, '0' as fCheck from " +
					pub.yssGetTableName("Tb_para_groupdatasync") + " a" +
					 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
					
					/*" left join (select FportName, Fportcode, MAX(FSTARTDATE) from " +*/
					
					" left join (select FportName, Fportcode from " +
					
					pub.yssGetTableName("Tb_para_portfolio")+
					" ) b on a.fdetailportcode = b.fportcode" +
					" where a.fmergeportcode = '" + this.mergePortCode.toString() + "' " +
					" union select Fportcode as FdetailPortCode, FportName as Fdetailportname,"+
					" 0.00 as FScale, '1' as fCheck from "+
					pub.yssGetTableName("Tb_para_portfolio")+
					" e join (select fsubcode from " + pub.yssGetTableName("tb_para_portfolio_relaship") + 
        			" where frelatype = 'PortLink' and fcheckstate = 1 and fportcode = " + dbl.sqlString(this.mergePortCode) +
        			" ) f on e.fportcode = f.fsubcode where e.fcheckstate = 1  and fporttype = '0' and FPORTCODE not in "+
					" (select FdetailPortCode from " + pub.yssGetTableName("tb_para_groupdatasync") + ")" +
					" order by Fscale desc ) a left join (select c.fdetailportcode,c.fportcode,c.fsplittype,d.fportName from " +
					pub.yssGetTableName("Tb_para_groupdatasync") + " c" +
					/*" left join (select FportName, Fportcode, MAX(FSTARTDATE) from " +*/
					" left join (select FportName, Fportcode from " +
					
					pub.yssGetTableName("Tb_para_portfolio")+
					" ) d on c.fportcode = d.fportcode" +
					" where c.fmergeportcode = '" + this.mergePortCode.toString() + "' " +
					")b on a.fdetailportcode = b.fdetailportcode";
			
			//end by lidaolong
			if(flag){
				sHeader = this.getListView1Headers();
			}else{
				sHeader = "明细组合代码\t明细组合名称";
			}
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				if(flag){
					bufShow.append((this.mergePortCode + "").trim()).append("\t");
					bufShow.append((this.mergePortName + "").trim()).append("\t");
				}
				bufShow.append((rs.getString("FDetailportcode") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FDetailPortName") + "").trim())
						.append("\t");
				bufShow.append((rs.getDouble("FScale") + "").toString().trim())
				.append("\t");
				bufShow.append(YssCons.YSS_LINESPLITMARK);
				//add by yanghaiming 20100713 MS01374 QDV4汇添富2010年06月28日01_AB
				this.detailPortCode = rs.getString("FDetailportcode");
				this.detailPortName = rs.getString("FDetailPortName");
				this.scale = rs.getDouble("FScale");
				this.portCode = rs.getString("fportcode") == null ? "" : rs.getString("fportcode");
				this.portName = rs.getString("fportname") == null ? "" : rs.getString("fportname");
				this.splitType = rs.getInt("fsplittype");
				this.isCheck = rs.getString("fCheck");
				//add by yanghaiming 20100713 MS01374 QDV4汇添富2010年06月28日01_AB
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f";
        }catch(Exception e){
        	throw new YssException("获取组合设置数据出错", e);
        }finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	//获取汇总组合下所有的明细组合代码及名称
	public String getListViewData2() throws YssException {
		String strSql = "";
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
			strSql = "select FdetailPortCode, b.FportName as Fdetailportname, FScale from " +
					pub.yssGetTableName("Tb_para_groupdatasync") + " a" +
					 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
				/*	" left join (select FportName, Fportcode, MAX(FSTARTDATE) from " +*/
					" left join (select FportName, Fportcode from " +
					
					pub.yssGetTableName("Tb_para_portfolio")+
					" ) b on a.fdetailportcode = b.fportcode" +
					" where a.fmergeportcode = '" + this.mergePortCode.toString() + "' " +
					" union select Fportcode as FdetailPortCode, FportName as Fdetailportname,"+
					" 0.00 as FScale from "+
					pub.yssGetTableName("Tb_para_portfolio")+
					" where Fcheckstate = 1 and fporttype = '0' and FPORTCODE not in "+
					" (select FdetailPortCode from " + pub.yssGetTableName("tb_para_groupdatasync") + ")" +
					" order by Fscale desc";
			
			//end by lidaolong
			sHeader = this.getListView1Headers();
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				bufShow.append((rs.getString("FDetailportcode") + "").trim())
						.append("\t");
				bufShow.append((rs.getString("FDetailPortName") + "").trim())
						.append("\t");
				bufShow.append(YssCons.YSS_LINESPLITMARK);
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
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f";
        }catch(Exception e){
        	throw new YssException("获取组合设置数据出错", e);
        }finally {
            dbl.closeResultSetFinal(rs);
        }
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
	//处理resultset
	public void setPortfolioAttr(ResultSet rs) throws SQLException {
		this.mergePortCode = rs.getString("FMergeportcode") + "";
		this.mergePortName = rs.getString("FMergePortName") + "";
		this.detailPortCode = rs.getString("FDetailportcode") + "";
		this.detailPortName = rs.getString("FDetailPortName") + "";
		this.scale = rs.getDouble("FScale");
		//super.setRecLog(rs);
	}
}
