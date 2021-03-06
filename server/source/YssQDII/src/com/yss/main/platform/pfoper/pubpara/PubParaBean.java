package com.yss.main.platform.pfoper.pubpara;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdeal.platform.pfoper.pubpara.ParaWithPubBean;
import com.yss.main.platform.pfsystem.facecfg.pojo.FaceCfgParamBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;

//TB_XXX_PARA_PUBPARA通用参数类型设定
public class PubParaBean
    extends BaseDataSettingBean implements IDataSetting {
    private String pubParaCode = ""; //参数编号
    private String pubParaName = ""; //参数名称
    private String paraGroupCode = ""; //参数组编号
    private String paraGroupName = ""; //参数组名称
    private int paraId; //参数值编号
    private String ctlGrpCode = ""; //控件组代码
    private String ctlGrpName = ""; //控件组名称

    //private int iDetail; //是否为叶节点
    private String orderCode = ""; //排序编号
    private String desc = ""; //描述
    private String ctlParas = ""; //---
    private ArrayList hCtlCodes = new ArrayList(); //控件集合
    private String oldPubParaCode = "";
    private String oldParaGroupCode = "";
    private String oldOrderCode = "";
	private int oldParaId;
	
	/**add---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到 */
	//private String searchConditions = ""; //搜索通参 的搜索条件
	/**end---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到*/
	
    private PubParaBean filterType = null;

    public PubParaBean() {
    }
    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "参数代码\t参数名称\t结点描述";
            //strSql = "select distinct(fpubparacode) as fpubparacode, fpubparaname from "+
            //pub.yssGetTableName("tb_pfoper_pubpara")+
            //" where FparagroupCode='[root]'";//下面改为将整个树都显示出来 by leeyu 080601
            strSql = "select c.*,d.Fpubparaname,case when c.Fparagroupcode='[root]' then '根结点' else '' end as FparaGroupName from ( " +
                " select a.FpubParaCode,a.FParaGroupCode,b.Fordercode from " +
                pub.yssGetTableName("tb_pfoper_pubpara") + " a " +
                " left join (select Fordercode from " +
                pub.yssGetTableName("tb_pfoper_pubpara") +
                " ) b on a.fordercode =b.fordercode " +
                " group by a.Fparagroupcode,a.FpubParaCode,b.fordercode )c " +
                " left join (select * from " +
                pub.yssGetTableName("tb_pfoper_pubpara") +
                " where FParaID=0 ) d on c.fpubparacode = d.fpubparacode " +
                " order by c.Fordercode ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("Fpubparacode")).append("\t");
                bufShow.append(rs.getString("FpubparaName")).append("\t");
                bufShow.append(rs.getString("FparaGroupName")).
                    append(YssCons.YSS_LINESPLITMARK);
                this.pubParaCode = rs.getString("FpubparaCode");
                this.pubParaName = rs.getString("FpubparaName");
                this.checkStateId = 1;
                //super.setRecLog(rs);
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

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                "FPubParaCode\tFpubParaName\tFparaGroupName";

        } catch (Exception ex) {
            throw new YssException("获取通用参数设置数据出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
    public String getListViewData3() {
        return "";
    }

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "insert into " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                "(FPubParaCode,FPubParaName,FParaGroupCode,FParaId,FCtlGrpCode," +
                "FCtlCode,FCtlValue,FOrderCode,FDesc) values(" +
                dbl.sqlString(this.pubParaCode) + "," +
                dbl.sqlString(this.pubParaName) + "," +
                dbl.sqlString(this.paraGroupCode) + "," +
                this.paraId + "," + dbl.sqlString(this.ctlGrpCode) + "," +
                dbl.sqlString(" ") + "," + dbl.sqlString(" ") + "," +
                //this.iDetail + "," +
                dbl.sqlString(dbFun.treeBuildOrderCode(
                    pub.yssGetTableName("TB_PFOper_PUBPARA"), "FPubParaCode",
                    this.paraGroupCode, Integer.parseInt(orderCode))) + "," +
                dbl.sqlString(this.desc) + ")";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) throws YssException {
        String strSql = "";
        String tmpValue = "";
        if (!this.pubParaCode.equalsIgnoreCase(this.oldPubParaCode)) {
            strSql = "select FPubParaCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = '" + this.pubParaCode + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("参数编号【" + this.pubParaCode + "】已被参数编号【" +
                                       tmpValue + "】占用，请重新输入参数编号");
            }
        }
        if (!this.orderCode.equalsIgnoreCase(oldOrderCode)) {
            strSql = "select FOrderCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FOrderCode = '" + dbFun.treeBuildOrderCode(
                    pub.yssGetTableName("TB_PFOper_PUBPARA"), "FPubParaCode",
                    this.paraGroupCode, Integer.parseInt(orderCode)) + "'";
            tmpValue = dbFun.GetValuebySql(strSql);
            if (tmpValue.trim().length() > 0) {
                throw new YssException("排序号【" + this.orderCode +
                                       "】已被【" + tmpValue + "】占用，请重新输入排序号");
            }
        }
    }

    /**
     * checkSetting
     */
    public void checkSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "";
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * delSetting
     */
    public void delSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode=" + dbl.sqlString(this.oldPubParaCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = null;
        String strSql = "";
        boolean bTrans = false;
        if (!this.orderCode.equalsIgnoreCase(oldOrderCode)) {
            dbFun.treeAdjustOrder(pub.yssGetTableName("TB_PFOper_PUBPARA"),
                                  "FPubParaCode",
                                  this.oldPubParaCode, Integer.parseInt(orderCode));
        }
        dbFun.treeAdjustParentCode(pub.yssGetTableName("TB_PFOper_PUBPARA"),
                                   "FParaGroupCode",
                                   this.oldPubParaCode, this.pubParaCode);
        try {
            conn = dbl.loadConnection();
            strSql = "delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode=" + dbl.sqlString(this.oldPubParaCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            addSetting();
        } catch (Exception e) {
            throw new YssException("修改通用参数类型设定出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void setAttr(ResultSet rs) throws SQLException {
        this.pubParaCode = rs.getString("FPubParaCode");
        this.pubParaName = rs.getString("FPubParaName");
        this.paraGroupCode = rs.getString("FParaGroupCode");
        this.paraId = rs.getInt("FParaId");
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        //this.ctlCode = rs.getString("FCtlCode");
        //this.ctlValue = rs.getString("FCtlValue");
        //this.iDetail = rs.getInt("FDetail");
        this.orderCode = rs.getString("FOrderCode");
        this.desc = rs.getString("FDesc");
        //this.ctlParas =
        //super.setRecLog(rs);
    }

    public void setManagerAttr(ResultSet rs) throws SQLException {
        this.pubParaCode = rs.getString("FPubParaCode");
        this.pubParaName = rs.getString("FPubParaName");
        this.paraGroupCode = rs.getString("FParaGroupCode");
        this.paraId = rs.getInt("FParaId");
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        //this.ctlCode = rs.getString("FCtlCode");
        //this.ctlValue = rs.getString("FCtlValue");
        //this.iDetail = rs.getInt("FDetail");
        this.orderCode = rs.getString("FOrderCode");
        this.desc = rs.getString("FDesc");
        //this.ctlParas =
        //super.setRecLog(rs);
    }

    /**shashijie 2013-06-27 BUG 8395 通用业务参数设置三级以下节点搜索不到
     * 筛选条件
     * @return String
     * 无用注释
     */
    /*private String buildFilterSql() {
        String sResult = " where 1=1 ";
        if (this.filterType != null) {
            if (this.filterType.pubParaCode.length() != 0) { //参数编号
                sResult = sResult + " and a.FPubParaCode = " +
                    dbl.sqlString(this.pubParaCode);
            }
            if (this.filterType.pubParaName.length() != 0) { //参数名称
                sResult = sResult + " and a.FPubParaName like '" +
                    filterType.pubParaName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.paraGroupCode.length() != 0) { //参数组编号
                sResult = sResult + " and a.FParaGroupCode = " +
                    dbl.sqlString(this.paraGroupCode);
            }
            if (this.filterType.paraId != 0) { //参数值编号
                sResult = sResult + " and a.FParaId = " + this.paraId;
            }
            if (this.filterType.ctlGrpCode.length() != 0) { //控件组代码
                sResult = sResult + " and a.FCtlGrpCode = " +
                    dbl.sqlString(this.ctlGrpCode);
            }
            //if (this.filterType.iDetail != 0) { //是否为叶节点
            //sResult = sResult + " and a.FDetail = " + this.iDetail;
            //}
            if (this.filterType.orderCode.length() != 0) { //排序编号
                sResult = sResult + " and a.FOrderCode = " +
                    dbl.sqlString(this.orderCode);
            }
            if (this.filterType.desc.length() != 0) { //描述
                sResult = sResult + " and a.FDesc like '" +
                    filterType.desc.replaceAll("'", "''") + "%'";
            }
        }
        return sResult;
    }*/

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
     * @param sMutilRowStr String
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
    public String getTreeViewData1() throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        try {
            strSql = "select distinct(a.FPubParaCode) as FPubParaCode,b.FPubParaName as FPubParaName,a.FParaGroupCode as FParaGroupCode" +
                ",b.FCtlGrpCode as FCtlGrpCode,b.FOrderCode as FOrderCode,b.FDesc as FDesc,b.FParaId as FParaId" +
                ",c.FPubParaName as FParaGroupName,d.FCtlGrpName as FCtlGrpName from " +
                "(select FPubParaCode,FParaGroupCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") + 
                /**add---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到 */
                //this.searchConditions + //modify huangqriong 2013-01-26 添加通参搜索条件
				/**end---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到*/
                " group by FPubParaCode,FParaGroupCode) a left join " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " b on a.FPubParaCode = b.FPubParaCode left join (select distinct (e.FPubParaCode), f.FPubParaName from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " e left join (select FPubParaCode, FPubParaName from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode in (select distinct (FParaGroupCode) from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FParaGroupCode in (select FPubParaCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                "))) f on f.FPubParaCode = e.FParaGroupCode" +
                ") c on a.FPubParaCode = c.FPubParaCode left join (select FCtlGrpCode,FCtlGrpName from Tb_PFSys_FaceCfgInfo" +
                " where FCtlGrpCode in (select FCtlGrpCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                ") group by FCtlGrpCode,FCtlGrpName) d on b.FCtlGrpCode = d.FCtlGrpCode where b.FParaId = 0 order by b.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.pubParaCode = rs.getString("FPubParaCode") + "";
                this.pubParaName = rs.getString("FPubParaName") + "";
                this.paraGroupCode = rs.getString("FParaGroupCode") + "";
                this.ctlGrpCode = rs.getString("FCtlGrpCode");
                //this.iDetail = rs.getInt("FDetail");
                this.paraId = rs.getInt("FParaId");
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString(
                    "FOrderCode").length() - 3)) + "";
                this.desc = rs.getString("FDesc") + "";
                this.paraGroupName = rs.getString("FParaGroupName") + "";
                this.ctlGrpName = rs.getString("FCtlGrpName") + "";
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取所有通用参数类型设定出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * getTreeViewData1
     * add by yeshenghong story 2917 20121107
     * @return String
     */
    private String getTreeViewData(String groupCode) throws YssException {
        String strSql = "";
        String result = "";
        ResultSet rs = null;
        try {
            strSql = "select distinct(a.FPubParaCode) as FPubParaCode,b.FPubParaName as FPubParaName,a.FParaGroupCode as FParaGroupCode" +
                ",b.FCtlGrpCode as FCtlGrpCode,b.FOrderCode as FOrderCode,b.FDesc as FDesc,b.FParaId as FParaId" +
                ",c.FPubParaName as FParaGroupName,d.FCtlGrpName as FCtlGrpName from " +
                "(select FPubParaCode,FParaGroupCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FBarGroupCode = " + dbl.sqlString(groupCode) + " group by FPubParaCode,FParaGroupCode) a left join " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " b on a.FPubParaCode = b.FPubParaCode left join (select distinct (e.FPubParaCode), f.FPubParaName from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " e left join (select FPubParaCode, FPubParaName from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode in (select distinct (FParaGroupCode) from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FParaGroupCode in (select FPubParaCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                "))) f on f.FPubParaCode = e.FParaGroupCode" +
                ") c on a.FPubParaCode = c.FPubParaCode left join (select FCtlGrpCode,FCtlGrpName from Tb_PFSys_FaceCfgInfo" +
                " where FCtlGrpCode in (select FCtlGrpCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                ") group by FCtlGrpCode,FCtlGrpName) d on b.FCtlGrpCode = d.FCtlGrpCode where b.FParaId = 0 order by b.FOrderCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.pubParaCode = rs.getString("FPubParaCode") + "";
                this.pubParaName = rs.getString("FPubParaName") + "";
                this.paraGroupCode = rs.getString("FParaGroupCode") + "";
                this.ctlGrpCode = rs.getString("FCtlGrpCode");
                //this.iDetail = rs.getInt("FDetail");
                this.paraId = rs.getInt("FParaId");
                this.orderCode = Integer.parseInt(rs.getString("FOrderCode").
                                                  substring(rs.getString(
                    "FOrderCode").length() - 3)) + "";
                this.desc = rs.getString("FDesc") + "";
                this.paraGroupName = rs.getString("FParaGroupName") + "";
                this.ctlGrpName = rs.getString("FCtlGrpName") + "";
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
                return result.substring(0, result.length() - 2);
            } else {
                return "";
            }
        } catch (Exception ex) {
            throw new YssException("获取所有通用参数类型设定出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
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
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.pubParaCode).append("\t");
        buf.append(this.pubParaName).append("\t");
        buf.append(this.paraGroupCode).append("\t");
        buf.append(this.paraGroupName).append("\t");
        buf.append(this.paraId).append("\t");
        buf.append(this.ctlGrpCode).append("\t");
        buf.append(this.ctlGrpName).append("\t");
        //buf.append(this.iDetail).append("\t");
        buf.append(this.orderCode).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(buildCtlCodes(hCtlCodes));
        //buf.append(this.ctlParas).append("\t");
        return buf.toString();
    }

    private String buildCtlCodes(ArrayList ctlCodes) throws YssException {
        CtlParaBean ctlPara = null;
        StringBuffer buf = null;
        String reStr = "";
        try {
            if (ctlCodes.size() == 0) {
                return "";
            }
            buf = new StringBuffer();
            for (int i = 0; i < ctlCodes.size(); i++) {
                ctlPara = (CtlParaBean) ctlCodes.get(i);
                buf.append(ctlPara.buildRowStr()).append("\r\n");
            }
            if (buf.length() > 0) {
                reStr = buf.toString();
            }
            reStr = reStr.substring(0, reStr.length() - 2);
        } catch (Exception e) {
            throw new YssException("编译通用参数类型设定出错", e);
        }
        return reStr;
    }
    
    //add by 黄啟荣 2011-06-21 story #1103 
    private String getPubPara() throws YssException{
    	//Connection conn = null;//无用注释
		ResultSet rs = null;
		String strSql = "";
		//boolean bTrans = false;//无用注释
		CtlParaBean ctlPara=null;
		try {
			strSql="select FpubParaCode,FparaGroupCode,FctlCode,FctlGrpCode,FctlValue,fparaid,fpubparaname,fordercode,fdesc"
				+" from "+pub.yssGetTableName("TB_PFOper_PUBPARA")
		       +" where FpubParaCode="+dbl.sqlString(this.pubParaCode)
		       +" and FParaGroupCode="+ dbl.sqlString(this.paraGroupCode)
		       +" and FctlGrpCode="+dbl.sqlString(this.ctlGrpCode)
		       +" and fparaid="
		       +"(select distinct(max(fparaid)) from "+pub.yssGetTableName("TB_PFOper_PUBPARA")
		        +" where FpubParaCode="+dbl.sqlString(this.pubParaCode)
		       +" and FParaGroupCode="+ dbl.sqlString(this.paraGroupCode)		       
		       +" and FctlGrpCode="+dbl.sqlString(this.ctlGrpCode);
			if(this.hCtlCodes.size()>0){
				ctlPara=(CtlParaBean)this.hCtlCodes.get(0);
			       if(!ctlPara.getCtlValue().equals(""))
			    	   strSql+=" and FctlValue like "+dbl.sqlString(ctlPara.getCtlValue());			       
			}
			   strSql+=")";

			//bTrans = true;//无用注释
			this.hCtlCodes.clear();//把传过来加到集合里的清掉
			rs = dbl.openResultSet(strSql);
			while(rs.next()){				
					this.pubParaCode=rs.getString("FpubParaCode");
					this.paraGroupCode=rs.getString("FparaGroupCode");
					this.ctlGrpCode=rs.getString("FctlGrpCode");
					ctlPara = new CtlParaBean();
	                ctlPara.setCtlCode(rs.getString("FCtlCode"));
	                ctlPara.setCtlValue(rs.getString("FCtlValue"));                
	                hCtlCodes.add(ctlPara);
			}

		} catch (Exception e) {
			throw new YssException("按空间条件查询出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);//add by jsc 20120424
		}
		return this.buildRowStr();
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String reStr = "";
        try {
            if (sType.equalsIgnoreCase("showfirst")) {
                reStr = showFirst();
            } else if (sType.equalsIgnoreCase("show")) { //显示含控件及其值的listView的item
                reStr = show();
            } else if (sType.equalsIgnoreCase("add")) {
                addCtls();
                reStr = show();
            } else if (sType.equalsIgnoreCase("del")) {
                delCtls(true);
                reStr = show();
            } else if (sType.equalsIgnoreCase("edit")) {
                editCtls();
                reStr = show();
            } else if (sType.equalsIgnoreCase("lastest")) { //获取最新的参数值 sunkey 20081203 bugNO:MS00051
                reStr = latestInfo();
            }
            //获取最新的参数值，返回的数据是以标准形式组装，前台提供解析的方法。 sunkey 20081215 BugNO:MS00072
            else if (sType.equalsIgnoreCase("lastest_standard")) {
                reStr = latestInfo_Standard();
            }
            //add by 黄啟荣 2011-06-17 story #1103
            else if(sType.equalsIgnoreCase("getPubPara")){
            	return this.getPubPara();
            }
            //---end---
			//---start STORY #863 香港、美国股指期权交易区别  add by jiangshichao 2011.06.15 --------------//
			else if (sType.equalsIgnoreCase("allparas")){
            	reStr = getAllParas();
            }
			//---end STORY #863 香港、美国股指期权交易区别  add by jiangshichao 2011.06.15 --------------//
            /**shashijie 2012-6-5 BUG 4727 判断是否导入通参*/
			else if (sType.equalsIgnoreCase("isHaveParas")) {
				reStr = isHaveParas();
			}
            /**end*/
            //add by huangqirong 2013-01-26 搜索通参
			else if("searchpub".equalsIgnoreCase(sType)){
				/**add---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到 */
				if (!YssUtil.isNullOrEmpty(this.pubParaName)) {
					reStr = this.getSearchpub();
				}
				/**end---shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到*/
			}
            //---end---
            /*注意：此段代码为需求2917添加，按业务模块查询获取业务参数，
             * 因sType用来作为判断模块的表识，不确定，所以未使用else if 判断项，
             * 后续开发人员若有添加修改代码，请一定在本段代码前添加，并加
             * else if 判断项   add by yeshenghong story2917*/
			else
			{
				reStr = this.getTreeViewData(sType);
			}
            /**yeshenghong story2917 20121107*/
            
            return reStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }

    }

    /**shashijie 2013-6-27 BUG 8395 通用业务参数设置三级以下节点搜索不到*/
	private String getSearchpub() throws YssException {
        String result = "";
        ResultSet rs = null;
        try {
        	String strSql = getPubparaSQL()+
				" And a.Fpubparaname Like "+dbl.sqlString("%"+this.pubParaName+"%");
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //递归调用父节点
                if(!rs.getString("FParagroupCode").equalsIgnoreCase("[root]")//父节点(参数组编号)
                		&& !YssUtil.isNullOrEmpty(rs.getString("FParagroupCode"))) {
                	result += getFatherNode(rs.getString("FParagroupCode"));
                }
                //设置对象
                setPubParaObject(rs);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
            if (result.length() > 2) {
				result = YssFun.left(result, result.length() - 2);
            }
        } catch (Exception ex) {
            throw new YssException("获取所有通用参数类型设定出错", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
	}
	
	/**shashijie 2013-6-27 BUG 8395 递归获取父节点 */
	private String getFatherNode(String FParagroupCode) throws YssException {
		ResultSet rs = null;//定义游标
		String result = "";
		//为了优化这里多加一个判断是否是根父节点,是的话直接返回空
		if (isFatherNode(FParagroupCode)) {
			return result;
		}
		try {
			String strSql = getPubparaSQL()+
				" And a.fpubparacode = "+dbl.sqlString(FParagroupCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
             	//若不是父节点则继续查找父节点
                FParagroupCode = rs.getString("FParagroupCode");
	            if (!isFatherNode(FParagroupCode)) {
	            	result += getFatherNode(FParagroupCode);
				}
	            //设置对象
                setPubParaObject(rs);
                result += buildRowStr() + YssCons.YSS_LINESPLITMARK;
            }
		} catch (Exception e) {
			throw new YssException("\r\n", e);
		} finally {
			dbl.closeResultSetFinal(rs);//关闭游标
		}
		return result;
	}
	
	/**shashijie 2013-6-27 BUG 8395 判断是否是根父节点,是的话直接返回true*/
	private boolean isFatherNode(String fParagroupCode) {
		boolean flag = false;
		if (fParagroupCode.equalsIgnoreCase("[root]")//父节点(参数组编号)
				|| YssUtil.isNullOrEmpty(fParagroupCode)) {
			flag = true;
		}
		return flag;
	}
	
	/**shashijie 2013-6-27 BUG 8395 获取通参SQL */
	private String getPubparaSQL() {
		String Sql = " Select a.* " +
			" From "+pub.yssGetTableName("Tb_Pfoper_Pubpara")+" a" +
			" Where a.Fparaid = 0" ;
		return Sql;
	}
	
	/**shashijie 2013-6-27 BUG 8395 设置通参对象 */
	private void setPubParaObject(ResultSet rs) throws Exception {
		this.pubParaCode = rs.getString("FPubParaCode") + "";
        this.pubParaName = rs.getString("FPubParaName") + "";
        this.paraGroupCode = rs.getString("FParaGroupCode") + "";
        this.ctlGrpCode = rs.getString("FCtlGrpCode");
        this.paraId = rs.getInt("FParaId");
		this.orderCode = Integer.parseInt(rs.getString("FOrderCode").substring(
				rs.getString("FOrderCode").length() - 3))
				+ "";
        this.desc = rs.getString("FDesc") + "";
        
        if (dbl.isFieldExist(rs, "FParaGroupName")) {
        	this.paraGroupName = rs.getString("FParaGroupName") + "";
        }
        //判断结果中有无存在的字段
        if (dbl.isFieldExist(rs, "FCtlGrpName")) {
        	this.ctlGrpName = rs.getString("FCtlGrpName") + "";
        }
	}
	
	/**shashijie 2012-6-5 BUG 4727 判断是否导入通参  没有返回字符串"false"*/
	private String isHaveParas() throws YssException {
		StringBuffer buf = new StringBuffer(); //用来储存要返回到客户端的信息
        StringBuffer bufSql = new StringBuffer(); //用来储存Sql语句
        ResultSet rs = null;

        bufSql.append("Select ")
            .append(" FParaId,FPubparaName,FCtlCode,")
            .append(" FCtlValue,FOrderCode,FDesc")
            .append(" FROM ").append(pub.yssGetTableName("Tb_PFoper_Pubpara"))
            .append(" a WHERE FPubparaCode =").append(dbl.sqlString(this.pubParaCode))
            .append(" AND FParagroupCode =").append(dbl.sqlString(this.paraGroupCode))
            .append(" AND FCtlgrpCode = ").append(dbl.sqlString(this.ctlGrpCode))
            .append(" AND FParaId = 0 ");
        try {
            rs = dbl.openResultSet(bufSql.toString());
            if(rs.next()) {
                buf.append("true");
            } else {
            	buf.append("false");
			}
            dbl.closeStatementFinal(rs.getStatement());
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString().trim();
	}
	
	private String showFirst() throws YssException {
        String reStr = "";
        String header = "序号";
        reStr = header + "\r\f" + "" + "\r\f" + "";
        return reStr;
    }

    private String show() throws YssException, SQLException {
        String sqlStr = "";
        ResultSet rs = null;
        ResultSet groupRs = null;
        String sqlGroupStr = "";
        String Header = "";
        String subHeader = "";
        String subshowData = "";
        String showData = "";
        String sAllDataStr = "";
        //String ctls = "";//无用注释
        StringBuffer bufShow = new StringBuffer();
        StringBuffer allData = new StringBuffer();
        CtlParaBean ctlPara = null;
        boolean showHeader = false;
        FaceCfgParamBean face = null;
        try {
            Header = "序号";
            this.hCtlCodes.clear();
            sqlGroupStr = "select FPubParaCode,FPubParaName,FParaGroupCode,FCtlGrpCode,FDesc,FParaId,FOrderCode from " +
                pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = " +
                dbl.sqlString(this.filterType.pubParaCode) +
                " and FParaGroupCode = " +
                dbl.sqlString(this.filterType.paraGroupCode) +
                " and FCtlGrpCode = " + dbl.sqlString(this.filterType.ctlGrpCode) +
                //" and FParaId <> 0 " +
                " group by FPubParaCode,FPubParaName,FParaGroupCode,FCtlGrpCode,FDesc,FParaId,FOrderCode order by FParaId";
            groupRs = dbl.openResultSet(sqlGroupStr); //为了以参数值编号进行循环
//---------------------------------------------------------------------------------------------------------------
            while (groupRs.next()) {
                //if (groupRs.getInt("FParaId") != 0) {
                //bufShow.append(super.buildRowShowStr(groupRs,
                //this.getListView1ShowCols())).
                //append(YssCons.
                //YSS_ITEMSPLITMARK1); //只包括那些固定的列
                //}
                if (groupRs.getInt("FParaId") == 0) {
                    continue;
                } else {
                    //bufShow.append(super.buildRowShowStr(groupRs,
                    //this.getListView1ShowCols())); //只包括那些固定的列
                    bufShow.append(groupRs.getInt("FParaId")).append(YssCons.YSS_ITEMSPLITMARK1);
                }
                //----------------------------------------------------------------------------------
                //开始以每个参数值编号获取控件和其值
                /*sqlStr = "select FPubParaCode,FPubParaName,FParaGroupCode,FParaId," +
                      "pubpara.FCtlGrpCode,FDesc," +
                      dbl.sqlIsNull("pubpara.FCtlCode", "face.FCtlCode") +
                      " as FCtlCode, " +
                      dbl.sqlIsNull("pubpara.FCtlValue", dbl.sqlString(" ")) +
                      " as FCtlValue from " +
                 "(select * from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                      " where FPubParaCode = " +
                      dbl.sqlString(this.filterType.pubParaCode) +
                      " and FParaGroupCode = " +
                      dbl.sqlString(this.filterType.paraGroupCode) +
                      " and FCtlGrpCode = " +
                      dbl.sqlString(this.filterType.ctlGrpCode) +
                      " and FParaId = " + groupRs.getInt("FParaId") +
                      ") pubpara left join " +
                 " (select FCtlGrpCode,FCtlCode from Tb_PFSys_FaceCfgInfo where FCheckState =1 )" +
                      " face on pubpara.FCtlGrpCode = face.FCtlGrpCode";*/
                sqlStr = "select pub.*,face.FCtlType as FCtlType from (select * from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                    " where FPubParaCode = " +
                    dbl.sqlString(this.filterType.pubParaCode) +
                    " and FParaGroupCode = " +
                    dbl.sqlString(this.filterType.paraGroupCode) +
                    " and FCtlGrpCode = " +
                    dbl.sqlString(this.filterType.ctlGrpCode) +
                    " and FParaId = " + groupRs.getInt("FParaId") +
                    ") pub " +
                    " left join (select * from Tb_PFSys_FaceCfgInfo where FCtlGrpCode = " + dbl.sqlString(this.filterType.ctlGrpCode) +
                    " ) face on pub.FCtlCOde = face.FCtlCode " +
                    " order by face.FParamIndex";
                if (rs != null) {
                    dbl.closeResultSetFinal(rs);
                }
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    face = new FaceCfgParamBean();
                    face.setSCtlGrpCode(this.filterType.ctlGrpCode);
                    face.setSCtlCode(rs.getString("FCtlCode"));
                    face.setYssPub(pub);
                    face.getSetting();
                    //face.getSShowText();
                    //getComboxText
                    subHeader +=
                        //"控件" + rs.getString("FCtlCode") +
                        //YssCons.YSS_ITEMSPLITMARK1 +
                        (face.getSShowText().trim().endsWith("：") || face.getSShowText().trim().endsWith(":") ?
                         face.getSShowText().trim().substring(0, face.getSShowText().trim().length() - 1) :
                         face.getSShowText()) +
                        YssCons.YSS_ITEMSPLITMARK1; //循环获取控件的个数,以确定列数.
                    subshowData +=
                        (rs.getInt("FCtlType") == 2 ? getComboxText(this.filterType.ctlGrpCode, rs.getString("FCtlCode"), rs.getString("FCtlValue")) :
                         rs.getString("FCtlValue")) + YssCons.YSS_ITEMSPLITMARK1; //循环获取需要显示的控件和其值.
                    //------------------控件和其值,放入ctls属性,在前台时解析成hashtable放入listview的tag中。
                    ctlPara = new CtlParaBean();
                    ctlPara.setCtlCode(rs.getString("FCtlCode"));
                    ctlPara.setCtlValue(rs.getString("FCtlValue"));
                    ctlPara.setCtlInd("");
                    hCtlCodes.add(ctlPara);
                    //------------------------------------------------------------------------------
                }
                if (subHeader.length() > 0 && !showHeader) {
                    subHeader = subHeader.substring(0, subHeader.length() - 1);
                    Header = Header + YssCons.YSS_ITEMSPLITMARK1 + subHeader;
                    showHeader = true;
                    //subshowData = subshowData.substring(0, subshowData.length() - 1);
                    //this.ctlParas = ctls.substring(0, ctls.length() - 2); //为了在tag中能获取。
                }
                if (subshowData.length() > 0) {
                    subshowData = subshowData.substring(0, subshowData.length() - 1);
                }
                //if (subHeader.length() > 0 && showHeader) { //将固定的列和循环获取的列相衔接。
                //Header = Header + YssCons.YSS_ITEMSPLITMARK1 + subHeader;
                //}
                setAttr(groupRs);
                bufShow.append(subshowData).append(YssCons.YSS_LINESPLITMARK); //将控件和其值加入listView中。
                allData.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
                this.hCtlCodes.clear();
                subshowData = "";
            }
//--------------------------------------------------------------------------------------------------------
            if (bufShow.toString().length() > 2) {
                showData = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (allData.toString().length() > 2) {
                sAllDataStr = allData.toString().substring(0,
                    allData.toString().length() - 2);
            }
            return Header + "\r\f" + showData + "\r\f" + sAllDataStr;
            // + "\r\f" +
            // this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("显示数据出错！");
        } finally {
            dbl.closeResultSetFinal(groupRs);
            dbl.closeResultSetFinal(rs);
        }
    }
    
    private void addCtls() throws YssException {
        String sqlStr = "";
        java.sql.PreparedStatement ptmt = null;
        java.sql.Connection conn = dbl.loadConnection();
        CtlParaBean ctlpara = null;
        boolean bTrans = false;
        //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
    	String portCode = "";
    	String feeType = "";
    	String tmportCode="";
    	String KeMuCode="";
    	String filterSql="";
    	
    	ResultSet rs = null;
    	ArrayList alCash = new ArrayList();
    	HashMap hmFeeType = new HashMap();
    	
    	//add by guyichuan 2011.06.30 STORY #1183 对于同一组合下的每一个科目只能设置一条记录
    	if("Is_Gusspara02".equalsIgnoreCase(this.pubParaCode)){
    	String portAndKeMuCode=getPortAndKeMuCode();
    	if(portAndKeMuCode !=null && portAndKeMuCode.length()!=0){
    		tmportCode=portAndKeMuCode.split("\t")[0];
    		KeMuCode=portAndKeMuCode.split("\t")[1];
    	}else return;
    	if(KeMuCode !=null && KeMuCode.indexOf("*") !=-1){//科目代码包含*
			filterSql=" and FctlValue like "+dbl.sqlString(KeMuCode.substring(0,4)+"%*%");
		}else{
			filterSql=" and FctlValue="+dbl.sqlString(KeMuCode);
		}
    	StringBuffer bfSql=new StringBuffer();
    	bfSql.append(" select a.portCode, b.kemuCode");
    	bfSql.append(" from (select FctlValue as portCode, FParaId");
    	bfSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
    	bfSql.append(" where FPubParaCode = 'Is_Gusspara02'");
    	bfSql.append(" and FParaGroupCode = 'Gusspara'");
    	bfSql.append(" and FctlCode = 'selPort' and FctlValue = "+dbl.sqlString(tmportCode)+") a");
    	bfSql.append(" join (select FctlValue as kemuCode, FParaId");
    	bfSql.append(" from "+pub.yssGetTableName("TB_PFOper_PUBPARA"));
    	bfSql.append(" where FPubParaCode = 'Is_Gusspara02'");
    	bfSql.append(" and FParaGroupCode = 'Gusspara'");
    	bfSql.append(" and FctlCode = 'txtTip' "+filterSql+" ) b on b.FParaId = a.FParaId");
    	
    	ResultSet filterRs=null;
    	try{
    		 filterRs =dbl.openResultSet(bfSql.toString());
    		 if(filterRs.next()){
    			 throw new YssException("此组合下已设置科目代码【"+filterRs.getString("kemuCode")+"】");
    		 }
    	}catch(YssException ee){
    		throw new YssException(ee.getMessage());
    	}catch(Exception e){
    		 throw new YssException(e.getMessage(),e);
    	 }finally{
    		 dbl.closeResultSetFinal(filterRs);
    	 }
    	}
    	//--end-STORY #1183----
    	
    	//---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
        /***shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
    	 * 如果是远期业务参数,这里需要判断有无重复记录,有则给出提示*/
    	if ("SettingDecimal".equals(this.ctlGrpCode)) {
			if (_isRepeatPortAndCury()) {
				throw new YssException("该组合下已有对该币种的设置！");
			}
		}
    	/**~~~~~~~~~~~~~~~~~~~~end~~~~~~~~~~~~~~~~~~~~~**/
        try {
        	//---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
        	if("ValCashCommand".equals(this.ctlGrpCode)){
        		sqlStr = " select distinct a1.Fparaid, b.Fportcode, b1.ffeetype " + 
        		" from (select a.Fpubparacode, a.Fparaid, a.fctlcode, a.fctlvalue from " + 
        		pub.yssGetTableName("TB_PFOper_PUBPARA") + " a where a.Fpubparacode = 'CashCommand_fee') a1 " + 
        		" left join (select fpubparacode, FCtlCode, FCtlValue as FPortCode, fparaid from " +
        		pub.yssGetTableName("TB_PFOper_PUBPARA") + " where fctlcode = '组合') b " +
        		" on a1.fpubparacode = b.fpubparacode and a1.fparaid = b.fparaid " +
        		" left join (select fpubparacode, FCtlCode, FCtlValue as FFeeType, fparaid from " +
        		pub.yssGetTableName("TB_PFOper_PUBPARA") + " where fctlcode = '费用类型') b1 on " +
        		" a1.fpubparacode = b1.fpubparacode and a1.fparaid = b1.fparaid " +
        		" where a1.fctlcode in ('组合','费用类型') ";
        		rs =dbl.openResultSet(sqlStr);
        		while(rs.next()){
        			if(!alCash.contains(rs.getString("FPortCode") + "," + rs.getString("FFeeType"))){
        				alCash.add(rs.getString("FPortCode") + "," + rs.getString("FFeeType"));
        			}
        		}
        		
        		dbl.closeResultSetFinal(rs);
        		
        		sqlStr = " select * from Tb_Fun_Vocabulary a where a.FVocTypeCode = 'feetype_cashCommand' ";
        		rs = dbl.openResultSet(sqlStr);
        		while(rs.next()){
        			hmFeeType.put(rs.getString("FVocCode"), rs.getString("FVocName"));
        		}
        		dbl.closeResultSetFinal(rs);//modified by yeshenghong 20120320BUG3958
        		for (int paras = 0; paras < hCtlCodes.size(); paras++) {
        			ctlpara = (CtlParaBean) hCtlCodes.get(paras);
        			if("组合".equals(ctlpara.getCtlCode())){
        				portCode = ctlpara.getCtlValue();
        			}
        			if("费用类型".equals(ctlpara.getCtlCode())){
        				feeType = ctlpara.getCtlValue();
        			}
        		}
        		
        		if(alCash.contains(portCode + "," + feeType)){
        			throw new YssException("已设置了组合【" + portCode.substring(0,portCode.indexOf('|')) + "】、费用类型【" + (String)hmFeeType.get(feeType.split(",")[0]) + "】对应的费用划款指令设置参数，不能重复设置");
        		}
        	}
        	//---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "insert into " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " (FPubParaCode,FPubParaName,FParaGroupCode,FParaId,FCtlGrpCode," +
                "FCtlCode,FCtlValue,FOrderCode,FDesc) values(" +
                "?,?,?,?,?,?,?,?,?)";
            ptmt = conn.prepareStatement(sqlStr);
            //this.parseCtlCodes(this.ctlParas);//获取所有的控件及其值，以便循环的插入含控件及其值得记录
            //if (hCtlCodes.size() > 0) {
            for (int paras = 0; paras < hCtlCodes.size(); paras++) {
                //Iterator it = hCtlCodes.iterator();
                //while (it.hasNext()) {
                ctlpara = (CtlParaBean) hCtlCodes.get(paras);
                ptmt.setString(1, this.pubParaCode); //每条记录的相同的字段
                ptmt.setString(2, this.pubParaName);
                ptmt.setString(3, this.paraGroupCode);
                ptmt.setInt(4, this.paraId);
                ptmt.setString(5, this.ctlGrpCode);
               
                //edit by licai 20110217  STORY #355 净值统计表和财务估值表列表视图导出时保存的默认文件名称可配置，初始默认为标题名+估值日期 
                ptmt.setString(6, (ctlpara.getCtlCode().trim().length()>0)
                										?ctlpara.getCtlCode()
                												:((this.paraGroupCode.equals("CtrlDataExport"))
                														?"txtFileName"
                																:ctlpara.getCtlCode()));
                //edit by licai 20110217  STORY #355 ==============================================================================end 
                
                ptmt.setString(7, ctlpara.getCtlValue());
                //ptmt.setInt(8, this.iDetail);
                ptmt.setString(8, dbFun.treeBuildOrderCode(
                    pub.yssGetTableName("TB_PFOper_PUBPARA"), "FPubParaCode",
                    this.paraGroupCode, Integer.parseInt(orderCode)));
                ptmt.setString(9, this.desc == null ? "" : this.desc);
                ptmt.executeUpdate();
                //}
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("插入控件值出错！", e);
        } finally {
            dbl.closeStatementFinal(ptmt);
            dbl.closeResultSetFinal(rs);//add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A
            dbl.endTransFinal(conn, bTrans);
        }
    }
    /**
     * add by guyichuan 2011.06.30 STORY #1183
     * QDV4银华2011年06月03日01_A 
     * 获取控件参数设置的组合代码及提示的科目代码
     */
    public String getPortAndKeMuCode()throws YssException{
    	String portCode=null;
    	String KeMuCode=null;
    	if(this.ctlParas !=null && this.ctlParas.length() !=0){
    		String[]strArray=this.ctlParas.split("\r\n");
    		for(int i=0;i<strArray.length ;i++){
    			if(strArray[i].indexOf("selPort")!=-1)
    				portCode=strArray[i].split("\b")[1];
    			else if(strArray[i].indexOf("txtTip")!=-1)
    				KeMuCode=strArray[i].split("\b")[1];
    		}
    	}
    	 //基金投资科目中才允许带*号
    	if(KeMuCode !=null && KeMuCode.indexOf("1105")==-1 && KeMuCode.indexOf("*") !=-1){
    		throw new YssException("非基金投资的科目代码不能带*号！");
    	}
    	 Pattern pattern = Pattern.compile("^\\d{4,}\\*{0,1}$");
    	 Matcher matcher = pattern.matcher(KeMuCode);
         if(!matcher.find()){
        	 throw new YssException("请输入【1105*,1105,110501,...】这样格式的科目代码！");
         }
    	return portCode+"\t"+KeMuCode;
    }

    /**判断是否有重复记录,有则返回true	
     * shashijie 2011.03.11 TASK #3129::希望根据参数设置远期外汇交易界面价格显示位数
     */
    private boolean _isRepeatPortAndCury() throws YssException {
    	CtlParaBean ctl = null;
    	ResultSet rs = null;
    	ResultSet cury = null;
    	List list = new ArrayList();
    	String curyType = "";
		for (int i = 0; i < hCtlCodes.size(); i++) {
			ctl = (CtlParaBean) hCtlCodes.get(i);
			if ("DecimalPlaces".equals(ctl.getCtlCode())) {
				continue;//如果是输入保留位数的记录的不判断
			} else {
				ParaWithPubBean para = new ParaWithPubBean();
		    	para.setYssPub(pub);
		    	try {//如果是组合则查出这个组合下的所有对应币种包括不设置币种的币种
			    	if ("portSel".equals(ctl.getCtlCode())) {
			    		rs = para.getResultSetByLike(this.ctlGrpCode, ctl.getCtlCode(), ctl.getCtlValue(), null);
			    		while (rs.next()) {
							cury = para.getResultSetByLike(this.ctlGrpCode, "SetCurrency", "%", rs.getString("FParaId"));
							if (cury.next()) {
								list.add(cury.getString("FCtlValue"));
							}
							dbl.closeResultSetFinal(cury);
						}
			    		dbl.closeResultSetFinal(rs);
					} else if ("SetCurrency".equals(ctl.getCtlCode())) {
						curyType = ctl.getCtlValue();//存储币种
					}
				}  catch (Exception e) {
		            throw new YssException("查通用业务参数重复值出错！");
		        } finally {
		            dbl.closeResultSetFinal(rs,cury);
		        }
			}
			dbl.closeResultSetFinal(rs,cury);
		}
		//返回集合中是否包含这个币种
		return list.contains(curyType);
	}

	private void delCtls(boolean commit) throws YssException {
        java.sql.Connection conn = dbl.loadConnection();
        String sqlStr = "";
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "Delete from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                " where FPubParaCode = " + dbl.sqlString(this.pubParaCode) +
                " and FParaGroupCode = " + dbl.sqlString(this.paraGroupCode) +
                " and FCtlGrpCode = " + dbl.sqlString(this.ctlGrpCode) +
                " and FParaId = " + this.paraId;
            dbl.executeSql(sqlStr);
            if (commit) {
                conn.commit();
            } else {
                conn.rollback();
            }
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除控件值出错！");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private void editCtls() throws YssException {
        try {
            delCtls(true);
            addCtls();
        } catch(YssException e){//add by guyichuan 1183 2011.07.13
        	throw new YssException(e.getMessage());}
        catch (Exception e) {
            throw new YssException("修改控件值出错！");
        }
    }

    private String getComboxText(String ctlGrpCode, String CtlCode, String CtlValue) throws YssException {
        String reStr = "";
        String sqlStr = "";
        ResultSet rs = null;
        String[] paras = null;
        String type = "";
        try {
            sqlStr = "select FCtlCode,FParam from Tb_PFSys_FaceCfgInfo where FCtlGrpCode = " + dbl.sqlString(ctlGrpCode) +
                " and FCtlCode = " + dbl.sqlString(CtlCode);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                paras = rs.getString("FParam").split("\n");
                if (paras.length > 0) {
                    type = paras[0];
                }
            }
            dbl.closeResultSetFinal(rs);
            sqlStr = "select FVocCode,FVocName from Tb_Fun_Vocabulary where FVocTypeCode = " + dbl.sqlString(type.trim()) + //modify huangqirong 2013-04-18 bug #7476 有换行则去除
                " and FVocCode = " + dbl.sqlString(CtlValue.split(",")[0]);
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                reStr = rs.getString("FVocName");
            }
            return reStr;
        } catch (Exception e) {
            throw new YssException("获取ComBox值出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
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
            reqAry = sTmpStr.split("\t");
            this.pubParaCode = reqAry[0];
            this.pubParaName = reqAry[1];
            this.paraGroupCode = reqAry[2];
            this.paraId = (reqAry[3].length() > 0 ? Integer.parseInt(reqAry[3]) :
                           0);
            this.ctlGrpCode = reqAry[4];
            //this.iDetail = (reqAry[5].length() > 0 ? Integer.parseInt(reqAry[5]) :
            //0);
            this.orderCode = reqAry[5];
            this.desc = reqAry[6];
            this.ctlParas = reqAry[7];
            this.oldPubParaCode = reqAry[8];
            this.oldParaGroupCode = reqAry[9];
            this.oldParaId = (reqAry[10].length() > 0 ? Integer.parseInt(reqAry[10]) :
                              0);
            this.oldOrderCode = reqAry[11];
            this.parseCtlCodes(this.ctlParas);
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    if (this.filterType == null) {
                        this.filterType = new PubParaBean();
                        this.filterType.setYssPub(pub);
                    }
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
        } catch (Exception e) {
            throw new YssException("解析通用参数类型设定出错", e);
        }
    }

    private void parseCtlCodes(String ctls) throws YssException {
        CtlParaBean ctlPara = null;
        try {
            if (ctls.length() == 0) {
                return;
            }
            String[] tmpAry = ctls.split("\r\n");
            for (int i = 0; i < tmpAry.length; i++) {
                ctlPara = new CtlParaBean();
                ctlPara.parseRowStr(tmpAry[i]);
                hCtlCodes.add(ctlPara);
            }
        } catch (Exception e) {
            throw new YssException("解析通用参数类型设定出错", e);
        }
    }

    /******************************************************************************
     *  StoryNo : #863 香港、美国股指期权交易区别
     *  Desc    : 获取指定的参数的所有参数值
     *  author  : benson
     *  date    : 2011.06.15
     */
    private String getAllParas()throws YssException{
    	 StringBuffer buf = new StringBuffer(); //用来储存要返回到客户端的信息
         StringBuffer bufSql = new StringBuffer(); //用来储存Sql语句
         ResultSet rs = null;

         //按照参数编号、参数组编号、控件组编号，查询控件值最大的参数信息
         bufSql.append("SELECT ")
             .append(" FPARAID,FPUBPARANAME,FCTLCODE,")
             .append(" FCTLVALUE,FORDERCODE,FDESC")
             .append(" FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
             .append(" a WHERE FPUBPARACODE =").append(dbl.sqlString(this.pubParaCode))
             .append(" AND FPARAGROUPCODE =").append(dbl.sqlString(this.paraGroupCode))
             .append(" AND FCTLGRPCODE = ").append(dbl.sqlString(this.ctlGrpCode))
             .append(" AND fparaid<>0 ")
             .append(" AND exists (SELECT FPARAID")
             .append(" FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
             .append(" b WHERE FPUBPARACODE = ").append(dbl.sqlString(this.pubParaCode))
             .append(" AND FPARAGROUPCODE = ").append(dbl.sqlString(this.paraGroupCode))
             .append(" AND FCTLGRPCODE = ").append(dbl.sqlString(this.ctlGrpCode))
             .append(" and a.FPARAID=b.FPARAID) order by fparaid,fctlcode ");
         try {
             rs = dbl.openResultSet(bufSql.toString());
             while (rs.next()) {
                     buf.append(rs.getString("FCTLCODE")).append("\t");
                     buf.append(rs.getString("FCTLVALUE")).append("\f");
                 }
             
             dbl.closeStatementFinal(rs.getStatement());
         } catch (Exception ex) {
             throw new YssException(ex.getMessage());
         } finally {
             dbl.closeResultSetFinal(rs);
         }
         return buf.toString();
    }
    
    
    
    /**
     * 获取指定的参数的最新值
     * author: sunkey
     * date  : 20081203
     * bugNO : MS00051
     * @return String
     */
    private String latestInfo() throws YssException {
        StringBuffer buf = new StringBuffer(); //用来储存要返回到客户端的信息
        StringBuffer bufSql = new StringBuffer(); //用来储存Sql语句
        ResultSet rs = null;

        //按照参数编号、参数组编号、控件组编号，查询控件值最大的参数信息
        bufSql.append("SELECT ")
            .append("FPARAID,FPUBPARANAME,FCTLCODE,")
            .append("FCTLVALUE,FORDERCODE,FDESC")
            .append(" FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE =").append(dbl.sqlString(this.pubParaCode))
            .append(" AND FPARAGROUPCODE =").append(dbl.sqlString(this.paraGroupCode))
            .append(" AND FCTLGRPCODE = ").append(dbl.sqlString(this.ctlGrpCode))
            .append(" AND FPARAID = (SELECT MAX(FPARAID)")
            .append(" FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = ").append(dbl.sqlString(this.pubParaCode))
            .append(" AND FPARAGROUPCODE = ").append(dbl.sqlString(this.paraGroupCode))
            .append(" AND FCTLGRPCODE = ").append(dbl.sqlString(this.ctlGrpCode))
            .append(")");
        try {
            rs = dbl.openResultSet(bufSql.toString());
            if (rs.next()) {
                buf.append(rs.getString("FPARAID")).append("\t");
                buf.append(rs.getString("FPUBPARANAME")).append("\t");
                buf.append(rs.getString("FORDERCODE")).append("\t");
                buf.append(rs.getString("FDESC")).append("\r\n");
                //控件编号和值存在多笔记录，所以要单独提取
                buf.append(rs.getString("FCTLCODE")).append("\t");
                buf.append(rs.getString("FCTLVALUE")).append("\f");
                while (rs.next()) {
                    buf.append(rs.getString("FCTLCODE")).append("\t");
                    buf.append(rs.getString("FCTLVALUE")).append("\f");
                }
            }
            dbl.closeStatementFinal(rs.getStatement());
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString();
    }

    /**
     * 获取指定的参数最新值的信息,获取之前要先设置参数代码、参数组代码和控件组代码
     * 为了匹配通用的解析方法，组装的数据按照以下格式：
     * 参数编号\t\参数名称\t\参数组编号\t参数组名称\t参数值编号\t控件组编号\t控件组名称\t排序编号\t描述\t控件信息
     * 控件信息:控件编号\b控件值\b控件标识\r\n控件编号\b控件值\b控件标识
     * author: sunkey
     * date  : 20081212
     * bugNO : MS00072
     * @return String
     */
    private String latestInfo_Standard() throws YssException {
        StringBuffer buf = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        StringBuffer bufCtl = null; //用来存储控件名、控件值组装称的SQL条件
        ResultSet rs = null;

        //获取控件信息，并组装成查询条件，按照控件编号=。。。并且控件值=。。。
        if (hCtlCodes.size() > 0) {
            bufCtl = new StringBuffer(); //实例化StringBuffer对象
            CtlParaBean ctlPara = (CtlParaBean) hCtlCodes.get(0); //获取第一个控件信息
            //目前系统转换日期的方法无法转换2008-12-9这种类型的格式，所以要先对日期进行转换
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String sCtlValue = null;
            try {
                sCtlValue = buildPortCtlValue(ctlPara.getCtlValue(),
                                              YssFun.left(format.format(format.parse(ctlPara.getCtlInd())), 4)); //如果是组合参数并且值是套账号，将其转换称标准的控件值形式 portCode|portName,CtlInd这里将作为时间传入
            } catch (ParseException ex1) {
                throw new YssException("日期转换错误!");
            }
            bufCtl.append("FCTLCODE='").append(ctlPara.getCtlCode()).append("' and "); //组装控件编号SQL条件语句
            bufCtl.append("FCTLVALUE='").append(sCtlValue == null ? ctlPara.getCtlValue() : sCtlValue).append("'"); //组装控件值SQL条件语句
        }

        /**
         * 原理：
         * 1、从参数表根据参数编号、参数组编号、控件组编号取最大参数信息
         * 2、从参数表根据参数编号、参数组编号、控件组编号、控件编号、控件值编号取最大参数信息，并根据最大参数信息取参数信息
         * 3、从空间信息表中查询通用参数中的控件组是否审核
         * 4、通过左连接取数据，对数据进行判断处理
         */
        bufSql.append("SELECT A.*, B.FCtlInd, B.FCTLGRPNAME, B.FCHECKSTATE,C.* ")
            .append("FROM (SELECT DISTINCT FPubParaCode,FOrderCode,")
            .append("FPubParaName,FParaGroupCode,FCtlGrpCode,FPARAID AS MAXPARAID ")
            .append("FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = '" + this.pubParaCode + "' ")
            .append("AND FPARAGROUPCODE = '" + this.paraGroupCode + "' ")
            .append("AND FCTLGRPCODE = '" + this.ctlGrpCode)
            .append("' AND FPARAID = (SELECT MAX(FPARAID) ")
            .append("FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = '" + this.pubParaCode + "' ")
            .append("AND FPARAGROUPCODE = '" + this.paraGroupCode + "' ")
            .append("AND FCTLGRPCODE = '" + this.ctlGrpCode + "' ")
            .append(")) A ")
            .append("LEFT JOIN (SELECT FPubParaCode,")
            .append("FParaId,FCtlCode,FCtlValue,FDesc ")
            .append("FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = '" + this.pubParaCode + "' ")
            .append("AND FPARAGROUPCODE = '" + this.paraGroupCode + "' ")
            .append("AND FCTLGRPCODE = '" + this.ctlGrpCode + "' ")
            .append("AND FPARAID = ")
            .append("(SELECT MAX(FPARAID) ")
            .append("FROM ").append(pub.yssGetTableName("TB_PFOPER_PUBPARA"))
            .append(" WHERE FPUBPARACODE = '" + this.pubParaCode + "' ")
            .append("AND FPARAGROUPCODE = '" + this.paraGroupCode + "' ")
            .append("AND FCTLGRPCODE = '" + this.ctlGrpCode + "' ")
            .append(bufCtl == null ? "" : "AND " + bufCtl)
            .append(")) C ON A.FPUBPARACODE = C.FPUBPARACODE ")
            .append("LEFT JOIN Tb_PFSys_FaceCfgInfo B ON A.FCtlGrpCode = B.FCtlGrpCode ")
            .append("AND C.FCTLCODE = B.FCTLCODE");

        try {
            rs = dbl.openResultSet(bufSql.toString());
            
            if (rs.next()) {
                //如果没有审核控件组信息,直接返回
            	
        		if (rs.getString("FCHECKSTATE") != null && rs.getInt("FCHECKSTATE") != 1) {
                	throw new YssException("请检查【系统平台】-【通用参数控件信息配置】菜单下控件组【"+rs.getString("FCTLGRPCODE")+"】的信息是否审核！");
                    //return buf.toString();//BUG4868再次设置参数查看估值表点击参数设置会提示财务估值表的参数设置信息已被删除  modify by zhangjun 2012.07.02
                }
            	
                
                buf.append(rs.getString("FPubParaCode")).append("\t"); //参数编号
                buf.append(rs.getString("FPubParaName")).append("\t"); //参数名称
                buf.append(rs.getString("FParaGroupCode")).append("\t"); //参数组编号
                buf.append("").append("\t"); //参数组名称，在此处无用，为了提高查询效率，不处理
                //参数值编号，如果设置了控件值，则编号就是参数值编号，如果没有，则取最大值+1，这样在前台处理的时候可以一律当做修改来进行处理。
                buf.append(rs.getInt("FParaId") == 0 ? rs.getInt("MAXPARAID") + 1 : rs.getInt("FParaId")).append("\t");
                buf.append(rs.getString("FCtlGrpCode")).append("\t"); //控件组编号
                buf.append(rs.getString("FCTLGRPNAME")).append("\t"); //控件组名称
                buf.append(rs.getString("FOrderCode")).append("\t"); //排序编号
                buf.append(rs.getString("FDESC")).append("\t"); //描述
                //控件编号和值存在多笔，所以要单独提取
                buf.append(rs.getString("FCTLCODE")).append("\b"); //控件编号
                buf.append(rs.getString("FCTLVALUE")).append("\b"); //控件值
                buf.append(rs.getString("FCtlInd")).append("\r\n"); //控件标识
                while (rs.next()) {
                    buf.append(rs.getString("FCTLCODE")).append("\b"); //控件编号
                    buf.append(rs.getString("FCTLVALUE")).append("\b"); //控件值
                    buf.append(rs.getString("FCtlInd")).append("\r\n"); //控件标识
                }
            }
            else if(this.pubParaCode.equalsIgnoreCase("finish_ParaFVT")
            		&& this.ctlGrpCode.equalsIgnoreCase("ctlGrpFVT") )//BUG4868再次设置参数查看估值表点击参数设置会提示财务估值表的参数设置信息已被删除  modify by zhangjun 2012.07.02                
            {
            	throw new YssException("对不起，请检查财务估值表参数配置信息【"+this.pubParaCode+"】是否设置，若参数已设置请检查控件组是否设置为【"+this.ctlGrpCode+"】！");
            }
            
            //如果Buf中有数据，要删除最后的\r\n，避免前台解析的时候出错
            if (buf.length() > 2) {
                buf.delete(buf.length() - 2, buf.length());
            }
            dbl.closeStatementFinal(rs.getStatement());
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString();
    }

    /**
     * 根据套帐编号、年份,获取符合要求的组合值 portCode|portName
     * 此法主要是为了处理财务估值表参数中，只有套账号没有组合号的情况
     * @param sPortID String
     * @param sYear String
     * @return String
     * @throws YssException
     * @version sunkey 20081217 BugNO:MS00072
     */
    private String buildPortCtlValue(String sPortID, String sYear) throws YssException {
        ResultSet rs = null;
        String sqlStr = "";
        String sResult = null;
        try {
            //根据指定的年份和套账号去取组合号和组合名称
            sqlStr = "SELECT DISTINCT FSETID,FSETNAME FROM LSETLIST WHERE FSETCODE=" + sPortID + " AND FYEAR=" + sYear;
            rs = dbl.openResultSet(sqlStr);

            if (rs.next()) {
                //将组合号和组合名称组装成标准形式的控件值信息
                sResult = rs.getString("FSETID") + "|" + rs.getString("FSETNAME");
            }
        } catch (Exception ex) {
            throw new YssException("取组合信息出错,请检查设置是否正确", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sResult;
    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    public String getCtlGrpCode() {
        return ctlGrpCode;
    }

    public String getCtlGrpName() {
        return ctlGrpName;
    }

    public void setCtlGrpCode(String ctlGrpCode) {
        this.ctlGrpCode = ctlGrpCode;
    }

    public void setCtlGrpName(String ctlGrpName) {
        this.ctlGrpName = ctlGrpName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList getHCtlCodes() {
        return hCtlCodes;
    }

    public void setHCtlCodes(ArrayList hCtlCodes) {
        this.hCtlCodes = hCtlCodes;
    }

    //public int getIDetail() {
    //return iDetail;
    //}

    //public void setIDetail(int iDetail) {
    //this.iDetail = iDetail;
    //}

    public String getOldParaGroupCode() {
        return oldParaGroupCode;
    }

    public void setOldParaGroupCode(String oldParaGroupCode) {
        this.oldParaGroupCode = oldParaGroupCode;
    }

    public int getOldParaId() {
        return oldParaId;
    }

    public void setOldParaId(int oldParaId) {
        this.oldParaId = oldParaId;
    }

    public String getOldPubParaCode() {
        return oldPubParaCode;
    }

    public void setOldPubParaCode(String oldPubParaCode) {
        this.oldPubParaCode = oldPubParaCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOldOrderCode(String oldOrderCode) {
        this.oldOrderCode = oldOrderCode;
    }

    public String getOldOrderCode() {
        return oldOrderCode;
    }

    public String getParaGroupCode() {
        return paraGroupCode;
    }

    public void setParaGroupCode(String paraGroupCode) {
        this.paraGroupCode = paraGroupCode;
    }

    public void setParaGroupName(String paraGroupName) {
        this.paraGroupName = paraGroupName;
    }

    public String getParaGroupName() {
        return paraGroupName;
    }

    public int getParaId() {
        return paraId;
    }

    public void setParaId(int paraId) {
        this.paraId = paraId;
    }

    public void setPubParaCode(String pubParaCode) {
        this.pubParaCode = pubParaCode;
    }

    public String getPubParaCode() {
        return pubParaCode;
    }

    public String getPubParaName() {
        return pubParaName;
    }

    public void setPubParaName(String pubParaName) {
        this.pubParaName = pubParaName;
    }

    public String getCtlParas() {
        return ctlParas;
    }

    public void setCtlParas(String ctlParas) {
        this.ctlParas = ctlParas;
    }

    public PubParaBean getFilterType() {
        return filterType;
    }

    public void setFilterType(PubParaBean filterType) {
        this.filterType = filterType;
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
   
    /**
     * 此方法主要查询 TB_XXX_PFOper_PUBPARA表中是否存在按指定条件查询的数据
     * @return String
     * 市值法 成本法
     * @throws YssException
     * @version baopingping 20110714 #story 1183
     * @throws YssException 
    */
    public String getGuess(String PortCode,String Type) throws YssException
    {
    	String count="0";
    	String Guess=null;
    	ResultSet rs=null;
    	ResultSet Rowrs=null;
    	String sql=null;
    	String Rowsql=null;
    	String Rowsqls=null;
    	ResultSet Rowrss=null;
    	String PortType=null;
    	if(Type.equalsIgnoreCase("F"))
    	{
    		PortType="0";
    	}else if(Type.equalsIgnoreCase("S")){
    		PortType="1";
    	}else if(Type.equalsIgnoreCase("C")){
    		PortType="2";
    	}
    	try{
	    	sql="select *from "+ 
	    	pub.yssGetTableName("TB_PFOper_PUBPARA")+
	    	" where FPubParaCode='RS_Value' and FctlGrpCode" +
	    	"='R_Value' and FctLValue like '"+PortCode+"%'";
	    	rs=dbl.openResultSet(sql);
	    	while(rs.next())
	    	{
	    		Rowsql="select *from "+
	    		pub.yssGetTableName("TB_PFOper_PUBPARA")+
	    		" where FPubParaCode='RS_Value' and " +
	    		"FctlGrpCode='R_Value' and FctlValue like '"+PortType+"%' "+
	    		" and FCtlCode='cboType' "+
	    		"and FparaId='"+rs.getString("FparaId")+"'";
	    		Rowrs=dbl.openResultSet(Rowsql);
	    		while(Rowrs.next())
	    		{
	    			Rowsqls="select *from "+
		    		pub.yssGetTableName("TB_PFOper_PUBPARA")+
		    		" where FPubParaCode='RS_Value' and " +
		    		"FctlGrpCode='R_Value'"
		    		+" and FCtlCode='cbxType'"+
		    		"and FparaId='"+Rowrs.getString("FparaId")+"'";
	    			Rowrss=dbl.openResultSet(Rowsqls);
	    			while(Rowrss.next())
	    			{
	    				Guess=Rowrss.getString("FctlValue");
	    				count=Guess.split(",")[0];
	    			}
	    		}
	    		    
	    	}
	    	return count;
    	}catch(Exception e)
    	{
    		throw new YssException("获取参数信息出错！");
    	}finally{
    		dbl.closeResultSetFinal(Rowrss);
    		dbl.closeResultSetFinal(Rowrs);
    		dbl.closeResultSetFinal(rs);
    	}    	    
    } 
    
    
    /**
     * 此方法主要查询 TB_XXX_PFOper_PUBPARA表中是否存在按指定条件查询的数据
     * @return String
     * 全价，净值
     * @throws YssException
     * @version baopingping 20110714 #story 1183
     * @throws YssException 
    */
    public String getGeussValue(String PortCode,String VartyCode,String Excheng) throws YssException
    {
    	String count="none";
    	String Guess=null;
    	ResultSet rs=null;
    	ResultSet Rowrs=null;
    	String sql=null;
    	String Rowsql=null;
    	String Rowsqls=null;
    	ResultSet Rowrss=null;
    	String Rowsqlw=null;
    	ResultSet Rowrsw=null;
    	try{
    	sql="select *from "+ 
    	pub.yssGetTableName("TB_PFOper_PUBPARA")+
    	" where FPubParaCode='RGuess' and FctlGrpCode" +
    	"='R_Guess' and FctLValue like '"+PortCode+"%'";
    	rs=dbl.openResultSet(sql);
    	while(rs.next())
    	{
    		Rowsql="select *from "+
    		pub.yssGetTableName("TB_PFOper_PUBPARA")+
    		" where FPubParaCode='RGuess' and " +
    		"FctlGrpCode='R_Guess' and FctlValue like '"+VartyCode+"%' "+
    		" and FCtlCode='Category' "+
    		"and FparaId='"+rs.getString("FparaId")+"'";
    		Rowrs=dbl.openResultSet(Rowsql);
    		while(Rowrs.next())
    		{
    			Rowsqls="select *from "+
	    		pub.yssGetTableName("TB_PFOper_PUBPARA")+
	    		" where FPubParaCode='RGuess' and " +
	    		"FctlValue like '"+Excheng+"%' "+
	    		" and FctlGrpCode='R_Guess'"
	    		+" and FCtlCode='Exchange'"+
	    		"and FparaId='"+Rowrs.getString("FparaId")+"'";
    			Rowrss=dbl.openResultSet(Rowsqls);
    			while(Rowrss.next())
    			{
    				Rowsqlw="select *from "+
    	    		pub.yssGetTableName("TB_PFOper_PUBPARA")+
    	    		" where FPubParaCode='RGuess' and " +
    	    		"FctlGrpCode='R_Guess'"
    	    		+" and FCtlCode='cbxType'"+
    	    		"and FparaId='"+Rowrss.getString("FparaId")+"'";
    				Rowrsw=dbl.openResultSet(Rowsqlw);
    				while(Rowrsw.next()){
    					Guess=Rowrsw.getString("FctlValue");
	    				count=Guess.split(",")[0];
    					}
    				}
    			}
    		}
    	return count;
    	}catch(Exception e){
    		throw new YssException("获取参数信息出错！");
    	}finally{
    		dbl.closeResultSetFinal(Rowrsw);
    		dbl.closeResultSetFinal(Rowrss);
    		dbl.closeResultSetFinal(Rowrs);
    		dbl.closeResultSetFinal(rs);
    	}
    	
    }
}
