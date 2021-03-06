package com.yss.main.operdeal.report.compliance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.yss.dsub.BaseReportBean;
import com.yss.main.operdeal.report.compliance.pojo.CompResultBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BaseCompliance
    extends BaseReportBean {
    protected java.util.Date startDate; //起始日期
    protected java.util.Date endDate; //终止日期
    protected String portCodes = ""; //已选组合
    protected String compStation = ""; //监控位置  BeforeComp-事前监控  FinalComp-日终监控
    //protected ArrayList liIndex;  2008-12-16 蒋锦 删除 跨组合群后已不需要此变量 编号：MS00036
    protected boolean isOverAssetGroup = false; //跨组合群标识 by leeyu 2008-12-15
    protected ArrayList liEnableAsset = null; //可用组合群，从前台传过来的组合群,与 isOverAssetGroup 匹配用，当isOverAssetGroup为真时才用这个。 by leeyu 2008-12-11
    protected Hashtable htLiIdxDynamic = new Hashtable(); //动态指标数据源 存放因跨组合群的数据 by leeyu 2008-12-15
    protected Hashtable htLiIdxStatic = new Hashtable(); //固定指标数据源 存放因跨组合群的数据 by leeyu 2008-12-15
    protected Date compDate;//监控日期 add by zhaoxianlin 20130327 STORY #3786 监控结果生成及查询功能优化
 /** add by zhaoxianlin 20130327 STORY #3786 监控结果生成及查询功能优化-start*/
    public Date getCompDate() {
		return compDate;
	}

	public void setCompDate(Date compDate) {
		this.compDate = compDate;
	}
	/** add by zhaoxianlin 20130327 STORY #3786 监控结果生成及查询功能优化-end*/
    public String getCompStation() {
        return compStation;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public String getPortCodes() {
        return portCodes;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    //2008-12-16 蒋锦 删除 跨组合群后已不需要此变量 编号：MS00036
//   public ArrayList getLiIndex() {
//      return liIndex;
//   }

    public boolean getIsOverAssetGroup() {
        return isOverAssetGroup;
    }

    public Hashtable getHtLiIdxStatic() {
        return htLiIdxStatic;
    }

    public Hashtable getHtLiIdxDynamic() {
        return htLiIdxDynamic;
    }

    public ArrayList getLiEnableAsset() {
        return liEnableAsset;
    }

    public void setCompStation(String compStation) {
        this.compStation = compStation;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public void setPortCodes(String portCodes) {
        this.portCodes = portCodes;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    //2008-12-16 蒋锦 删除 跨组合群后已不需要此变量 编号：MS00036
//   public void setLiIndex(ArrayList liIndex) {
//      this.liIndex = liIndex;
//   }

    public void setIsOverAssetGroup(boolean isOverAssetGroup) {
        this.isOverAssetGroup = isOverAssetGroup;
    }

    public void setHtLiIdxStatic(Hashtable htLiIdxStatic) {
        this.htLiIdxStatic = htLiIdxStatic;
    }

    public void setHtLiIdxDynamic(Hashtable htLiIdxDynamic) {
        this.htLiIdxDynamic = htLiIdxDynamic;
    }

    public void setLiEnableAsset(ArrayList liEnableAsset) {
        this.liEnableAsset = liEnableAsset;
    }

    public BaseCompliance() {
    }

    public ArrayList doCompliance() throws YssException {
        return null;
    }

    /**
     * 通过组合群代码获取组合群下选中的组合代码
     * @param sAsset String：组合群代码
     * @return ArrayList：选中的组合
     * @throws YssException
     */
    public ArrayList getSelectPortCodesByAssetGroupCode(String sAsset) throws YssException {
        String[] arrSelectObj = null;
        ArrayList liPortCodes = new ArrayList();
        try {
            arrSelectObj = portCodes.split(",");
            for (int i = 0; i < arrSelectObj.length; i++) {
                int iBegin = arrSelectObj[i].indexOf("-");
                if (iBegin == -1) {
                    liPortCodes.add(arrSelectObj[i]);
                } else {
                    if (arrSelectObj[i].substring(0, iBegin).equalsIgnoreCase(sAsset)) {
                        liPortCodes.add(arrSelectObj[i].substring(iBegin + 1));
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("获取已选组合群下的所有已选组合出错！\r\n" + e.getMessage());
        }
        return liPortCodes;
    }

    /**
     * 将监控结果存入表中
     * @param compResultList ArrayList
     * @throws YssException
     */
    public void savaCompResult(ArrayList compResultList) throws YssException {
        String strSql = "";
        String strSqlDel = "";
        boolean bTrans = false;
        PreparedStatement pstmt = null;
        Connection conn = dbl.loadConnection();
        try {
            //BugId MS00040 20081127   王晓光   给表Tb_001_Comp_ResultData中添加 FNumerator、FDenominator、FFactRatio列
            //2009.02.10 蒋锦 修改 MS00195 QDV4建行2009年1月15日01_B 添加“生成日期”
        	
        	//20120209 modified by liubo.Bug #3526
        	//监控数据结果表中增加一个FSerialNo字段
        	//===================================
            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Comp_ResultData") +
               " (FCompDate, FCreateDate, FPortCode, FIndexCfgCode, FCompResult, FCheckState, FCreator, FCreateTime,FNUMERATOR,FDENOMINATOR,FFACTRATIO,fstate,frecheckstate,FSerialNo,FremindResult)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//modifid by zhaoxianlin 20130301 story #3688增加FremindResult字段
        	//===============end====================	
            pstmt = conn.prepareStatement(strSql);
            int iListLen = compResultList.size();

            conn.setAutoCommit(false);
            bTrans = true;
            //add by zhouwei req 1509 修正以前监控结果表中fportcode字段是组合群-组合号的存储方式为之存贮组合号
            String fport=parsePortCodes(portCodes);
            strSqlDel = "DELETE FROM " +
                pub.yssGetTableName("Tb_Comp_ResultData") +
                //-------2009.02.10 蒋锦 修改 MS00195 QDV4建行2009年1月15日01_B--------//
                //监控日期使用截至日期代替原来的系统当前日期
                //" WHERE FCompDate = " + dbl.sqlDate(new Date()) +
                //" WHERE FCompDate = " + dbl.sqlDate(endDate) +
                " WHERE  FCompDate between " + dbl.sqlDate(startDate) +" and " + dbl.sqlDate(this.endDate)+//modified by zhaoxianlin 20130327 STORY #3786 监控结果生成及查询功能优化
                //------------------------------------------------------------------//
                " AND FPortCode IN (" + operSql.sqlCodes(fport) + ") and fcheckstate<>2 and frecheckstate=0 and Fstate=0";//edit by zhouwei 20110926 req 1509
            dbl.executeSql(strSqlDel);

            for (int i = 0; i < iListLen; i++) {
                CompResultBean compResult = (CompResultBean) compResultList.get(i);
                pstmt.setDate(1, YssFun.toSqlDate(compResult.getCompDate()));
                //2009.02.10 蒋锦 修改 MS00195 QDV4建行2009年1月15日01_B 添加生成日期
                pstmt.setDate(2, YssFun.toSqlDate(compResult.getCreateDate()));
                pstmt.setString(3, compResult.getPortCode());
                pstmt.setString(4, compResult.getIndexCfgCode());
                pstmt.setString(5, compResult.getCompResult());
                pstmt.setInt(6, 1);//自动生成已 审核 1 修改 by zhouwei 20110926 req 1509
                pstmt.setString(7, pub.getUserCode());
                pstmt.setString(8, YssFun.formatDate(new Date()));
                pstmt.setDouble(9, compResult.getNumerator());
                pstmt.setDouble(10, compResult.getDenominator());
                pstmt.setDouble(11, compResult.getFactRatio());
                pstmt.setInt(12, 0);//自动生成 0 修改 by zhouwei 20110926 req 1509
                pstmt.setInt(13, 0);//未确认 0 修改 by zhouwei 20110926 req 1509

                //20120209 added by liubo.Bug #3526
                //序列号字段做为数据表主键的一部分，用来记录该次监控结果生成操作的序列号，无实际作用
                //==================================
                pstmt.setString(14, compResult.getSerialNo());
                //===============end===================
                /**add by zhaoxianlin 20130301 story #3688 start*/
                if(compResult.getRemindResult()!=null&&compResult.getRemindResult().length()>0){
                	pstmt.setString(15, compResult.getRemindResult());
                }else{
                	pstmt.setString(15, " ");
                }
                /**add by zhaoxianlin 20130301 story #3688 end*/
                pstmt.executeUpdate();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("储存监控结果出错", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(conn, bTrans);
        }
    }
  //add by zhouwei req 1509 取出组合号
    private String parsePortCodes(String assetport){
    	String[] assetports=assetport.split(",");
    	String ports=null;
    	for(int i=0;i<assetports.length;i++){
    		int beginInt=assetports[i].indexOf("-");
    		if(beginInt!=-1){
    			if(ports==null){
    				ports=assetports[i].substring(beginInt+1)+",";
    			}else{
        			ports+=assetports[i].substring(beginInt+1)+",";
    			}
    		}else{
    			if(ports==null){
    				ports=assetports[i]+",";
    			}else{
        			ports+=assetports[i]+",";
    			}   		
    		}
    	}
    	return ports;
    }

}
