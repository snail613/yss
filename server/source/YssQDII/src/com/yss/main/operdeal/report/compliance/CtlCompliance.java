package com.yss.main.operdeal.report.compliance;

import com.yss.base.*;
import com.yss.util.*;

import java.util.Date;
import java.util.ArrayList;
import java.sql.ResultSet;
import com.yss.main.compliance.CompIndexCfgBean;
import java.util.Hashtable;

public class CtlCompliance
    extends BaseAPOperValue {
    private Date dBeginDate; //开始时间
    private Date dEndDate; //结束时间
    private String sPortCodes = ""; //所选所有组合
    private boolean bIsFinal = false; //是否为日终监控
    private ArrayList liIdxDynamic = null; //所有动态指标   现这些集合都要按"组合群-组合"为键保存数据,直接用组合代码进去取就可以了 by leeyu 2008-12-11
    private ArrayList liIdxStatic = null; //所有固定数据源  现这些集合都要按"组合群-组合"为键保存数据,直接用组合代码进去取就可以了 by leeyu 2008-12-11
    private boolean isOverAssetGroup = false; //跨组合群标识 2008-12-11 by leeyu
    private ArrayList liEnableAsset = null; //可用组合群，从前台传过来的组合群,与 isOverAssetGroup 匹配用，当isOverAssetGroup为真时才用这个。 by leeyu 2008-12-11
    private Hashtable htLiIdxDynamic = new Hashtable(); //动态指标数据源 存放因跨组合群的数据 by leeyu 2008-12-15
    private Hashtable htLiIdxStatic = new Hashtable(); //固定指标数据源 存放因跨组合群的数据 by leeyu 2008-12-15
    public CtlCompliance() {
    }

    public void init(Object bean) throws YssException {
        String[] sParams = ( (String) bean).split("\n");
        if (sParams.length == 0) {
            return;
        }
        dBeginDate = YssFun.toDate(sParams[0].split("\r")[1]);
        dEndDate = YssFun.toDate(sParams[1].split("\r")[1]);
        sPortCodes = sParams[2].split("\r")[1];
        //2008-12-18 蒋锦 修改 组合代码不再需要删除最后的一个逗号
//      sPortCodes = sPortCodes.substring(0, sPortCodes.length() - 1);
        bIsFinal = Boolean.valueOf(sParams[3].split("\r")[1]).booleanValue();

        if (sPortCodes.indexOf("-") > 0) {
            isOverAssetGroup = true;
            liEnableAsset = new ArrayList();
            String[] arrPort = sPortCodes.split(",");
            for (int i = 0; i < arrPort.length; i++) {
                if (liEnableAsset.indexOf(arrPort[i].substring(0,
                    arrPort[i].indexOf("-"))) == -1) {
                    liEnableAsset.add(arrPort[i].substring(0, arrPort[i].indexOf("-")));
                }
            }
        }
        if (!isOverAssetGroup) {
            liEnableAsset = new ArrayList();
            liEnableAsset.add(pub.getAssetGroupCode());
        }
    }

    public Object invokeOperMothed() throws YssException {
        ArrayList liCompResult = null;
        try {
        	//判断监控日期下组合是否被确认
        	isReCheck();
        	//STORY #1509  add by jiangshichao  必须确认净值统计表后，才能产生监控结果，如未确认给出提示
        	doCheck();
        	
            //日终监控
            if (bIsFinal) {
                //查询监控指标
                getCompIndexCfg();
                //----------------动态指标数据源-----------------//
                BaseCompliance comp = new DynamicCfgComp();
                comp.setYssPub(pub);
                comp.setStartDate(this.dBeginDate);
                comp.setEndDate(this.dEndDate);
                comp.setPortCodes(this.sPortCodes);
                //2008-12-16 蒋锦 删除 跨组合群后已不需要此变量 编号：MS00036
                //comp.setLiIndex(this.liIdxDynamic);
                //===================//
                comp.setLiEnableAsset(liEnableAsset);
                comp.setIsOverAssetGroup(isOverAssetGroup);
                comp.setHtLiIdxDynamic(htLiIdxDynamic);
                //comp.setHtLiIdxStatic(htLiIdxStatic);//固定的要在下面调用
                //===================//
                liCompResult = comp.doCompliance();
                comp.savaCompResult(liCompResult);
                //---------------------------------------------//
                //----------------固定指标数据源-----------------//
                if (this.liIdxStatic.size() > 0) {
                    liCompResult.clear();
                    int iSatcLen = this.liIdxStatic.size();
                    for (int i = 0; i < iSatcLen; i++) {
                        CompIndexCfgBean indexCfg = (CompIndexCfgBean)this.liIdxStatic.get(i);
                        BaseCompliance compStatic = (BaseCompliance) pub.getOperDealCtx().getBean(indexCfg.getBeanID());
                        liCompResult = compStatic.doCompliance();
                        comp.savaCompResult(liCompResult);
                    }
                }
                //---------------------------------------------//
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return null;
    }

    /**
     * 查询监控指标配置表，将动态数据源和固定数据源的指标分别存入 List
     * @throws YssException
     */
    public void getCompIndexCfg() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        liIdxDynamic = new ArrayList();
        liIdxStatic = new ArrayList();

        CompIndexCfgBean indexCfg = null;
        try {
//         if(!isOverAssetGroup){
//            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Comp_IndexCfg") +
//                  " WHERE FCheckState = 1";
//            rs = dbl.openResultSet(strSql);
//            while (rs.next()) {
//               indexCfg = new CompIndexCfgBean();
//               indexCfg.setIndexCfgCode(rs.getString("FIndexCfgCode"));
//               indexCfg.setIndexCfgName(rs.getString("FIndexCfgName"));
//               indexCfg.setIndexType(rs.getString("FIndexType"));
//               indexCfg.setBeanID(rs.getString("FBeanId") == null ? "" :
//                                  rs.getString("FBeanId"));
//               indexCfg.setCompParam(rs.getString("FCompParam"));
//               indexCfg.setRepCode(rs.getString("FRepCode") == null ? "" :
//                                   rs.getString("FRepCode"));
//               indexCfg.setIndexDS(dbl.clobStrValue(rs.getClob("FIndexDS")) == null ?
//                                   "" : dbl.clobStrValue(rs.getClob("FIndexDS")));
//               indexCfg.setMemoyWay(rs.getString("FMemoyWay"));
//               indexCfg.setTgtTableView(rs.getString("FTgtTableView") == null ?
//                                        "" :
//                                        rs.getString("FTgtTableView"));
//               indexCfg.setBeforeComp(rs.getString("FBeforeComp") == null ? "" :
//                                      rs.getString("FBeforeComp"));
//               indexCfg.setFinalComp(rs.getString("FFinalComp") == null ? "" :
//                                     rs.getString("FFinalComp"));
//               indexCfg.setWarnAnalysis(rs.getString("FWarnAnalysis") == null ?
//                                        "" :
//                                        rs.getString("FWarnAnalysis"));
//               indexCfg.setViolateAnalysis(rs.getString("FViolateAnalysis") == null ?
//                                           "" : rs.getString("FViolateAnalysis"));
//               indexCfg.setForbidAnalysis(rs.getString("FForbidAnalysis") == null ?
//                                          "" : rs.getString("FForbidAnalysis"));
//
//               //动态指标数据源
//               if (indexCfg.getIndexType().equalsIgnoreCase("Dynamic")) {
//                  this.liIdxDynamic.add(indexCfg);
//               }
//               //固定指标数据源
//               else {
//                  this.liIdxStatic.add(indexCfg);
//               }
//            }
//         }
//         else{
            for (int i = 0; i < liEnableAsset.size(); i++) {
                strSql = "SELECT * FROM " +
                    pub.yssGetTableName("Tb_Comp_IndexCfg",
                                        (String) liEnableAsset.get(i)) +
                    " WHERE FCheckState = 1";
                rs = dbl.openResultSet(strSql);
                liIdxDynamic = new ArrayList();
                liIdxStatic = new ArrayList();
                while (rs.next()) {
                    indexCfg = new CompIndexCfgBean();
                    indexCfg.setIndexCfgCode(rs.getString("FIndexCfgCode"));
                    indexCfg.setIndexCfgName(rs.getString("FIndexCfgName"));
                    indexCfg.setIndexType(rs.getString("FIndexType"));
                    indexCfg.setBeanID(rs.getString("FBeanId") == null ? "" :
                                       rs.getString("FBeanId"));
                    indexCfg.setCompParam(rs.getString("FCompParam"));
                    indexCfg.setRepCode(rs.getString("FRepCode") == null ? "" :
                                        rs.getString("FRepCode"));
                    indexCfg.setIndexDS(dbl.clobStrValue(rs.getClob("FIndexDS")) == null ?
                                        "" : dbl.clobStrValue(rs.getClob("FIndexDS")));
                    indexCfg.setMemoyWay(rs.getString("FMemoyWay"));
                    indexCfg.setTgtTableView(rs.getString("FTgtTableView") == null ?
                                             "" : rs.getString("FTgtTableView"));
                    indexCfg.setBeforeComp(rs.getString("FBeforeComp") == null ? "" :
                                           rs.getString("FBeforeComp"));
                    indexCfg.setFinalComp(rs.getString("FFinalComp") == null ? "" :
                                          rs.getString("FFinalComp"));
                    indexCfg.setWarnAnalysis(rs.getString("FWarnAnalysis") == null ?
                                             "" : rs.getString("FWarnAnalysis"));
                    indexCfg.setViolateAnalysis(rs.getString("FViolateAnalysis") == null ?
                                                "" : rs.getString("FViolateAnalysis"));
                    indexCfg.setForbidAnalysis(rs.getString("FForbidAnalysis") == null ?
                                               "" : rs.getString("FForbidAnalysis"));
                    //动态指标数据源
                    if (indexCfg.getIndexType().equalsIgnoreCase("Dynamic")) {
                        this.liIdxDynamic.add(indexCfg);
                    }
                    //固定指标数据源
                    else {
                        this.liIdxStatic.add(indexCfg);
                    }
                }
                htLiIdxDynamic.put( (String) liEnableAsset.get(i), liIdxDynamic);
                htLiIdxStatic.put( (String) liEnableAsset.get(i), liIdxStatic);
            } //end for
//         }//end if
        } catch (Exception e) {
            throw new YssException("获取监控指标配置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**********************************************************
     * STORY #1509  add by jiangshichao
     * 必须确认净值统计表后，才能产生监控结果，如未确认给出提示
     * @throws YssException
     */
    private void doCheck()throws YssException{
    	
    	StringBuffer buff = new StringBuffer();
    	String sPortCodes = "";
    	String sAsset = "";
    	ResultSet rs = null;
    	int idays = 0;
    	StringBuffer errorMsg = new StringBuffer();
    	String oldPortCode="";
    	java.sql.Date oldDate=null,startDate=null;
    	try{
    		
    		idays = YssFun.dateDiff(this.dBeginDate, this.dEndDate)+1;
    		for (int iAsset = 0; iAsset < liEnableAsset.size(); iAsset++) {
    			  sAsset = (String) liEnableAsset.get(iAsset);
    			  sPortCodes = getSelectPortCodesByAssetGroupCode(sAsset);
    			  
    	          buff.append("select * from (select a1.*,a2.* from ").append("(select fportcode,FInceptionDate from ").append("Tb_"+sAsset+"_para_portfolio");
    	          buff.append(" where fcheckstate=1 and fportcode in( ").append(operSql.sqlCodes(sPortCodes)).append(" ))a1,");
    	          buff.append("(select ").append(dbl.sqlDate(this.dBeginDate)).append(" +rownum-1 as fnavdate from ");
    	          buff.append("Tb_"+sAsset+"_Data_Navdata").append(" where  ROWNUM<="+idays+")a2)a where not exists (");
    	          buff.append(" select b.fnavdate,b.fportcode from");
    	          buff.append(" (select  fnavdate ,fportcode from ").append("Tb_"+sAsset+"_Data_Navdata");
    	          buff.append("  where FKeyCode = 'confirm' and fnavdate between ").append(dbl.sqlDate(dBeginDate));
    	          buff.append(" and ").append(dbl.sqlDate(dEndDate)).append(" and fportcode in ( ").append(operSql.sqlCodes(sPortCodes)).append(" ))b");
    	          buff.append(" where a.fnavdate=b.fnavdate and a.fportcode = b.fportcode) order by fportcode ,fnavdate ");
    	    	  rs = dbl.openResultSet(buff.toString());
    	    	  
    	    	  
    	    	  while(rs.next()){
    	    		  //所选日期在基金成立日前，就不
    	    		  if(YssFun.dateDiff(rs.getDate("fnavdate"),rs.getDate("FInceptionDate"))>0){
    	    			  continue;
    	    		  }
    	    		  if(rs.getString("fportcode").equalsIgnoreCase(oldPortCode)){
    	    			  
    	    			  if(YssFun.dateDiff(oldDate, rs.getDate("fnavdate"))>1){
    	    				  startDate = rs.getDate("fnavdate"); 
    	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】未确认净值统计表...\r\n");
    	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("fnavdate"), "yyyy-MM-dd"));
    	    			  }
    	    			  oldDate = rs.getDate("fnavdate"); 
    	    			  
    	    		  }else{
    	    			  if(oldPortCode.length()==0 && oldDate==null){
    	    				  if(startDate==null ){
        	    				  startDate = rs.getDate("fnavdate"); 
        	    			  }
    	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("fnavdate"), "yyyy-MM-dd"));
    	        	    			  
    	    			  }else{
    	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】未确认净值统计表...\r\n");
    	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("fnavdate"), "yyyy-MM-dd"));
      	        	    	
    	    			  }
    	    			  oldPortCode = rs.getString("fportcode");
    	    			  oldDate = rs.getDate("fnavdate"); 
    	    		  }
    	    	  }
    	    	  dbl.closeResultSetFinal(rs);
    	    	  buff.setLength(0);
    			}
    		
    		
    		if(errorMsg.toString().length()>0){
    			if(YssFun.dateDiff(startDate,oldDate)>0){
    				errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】未确认净值统计表...\r\n");
    			}else if(YssFun.dateDiff(startDate,oldDate)==0){
    				errorMsg.append("】未确认净值统计表...\r\n");
    			}
    			
        		errorMsg.append("请先确认以上组合的净值统计表，再生成监控结果！");
    			throw new YssException(errorMsg.toString());
    		}
    		
    	}catch(Exception e){
    		
    		throw new YssException(errorMsg.toString().length()>0?errorMsg.toString():"监控结果检查报错......");
    	}
    }
    /*
     * STORY #1509  add by zhouwei
     * 确认 的监控结果不能生成
     * @throws YssException
     * */
    public void isReCheck() throws YssException {
		StringBuffer buff = new StringBuffer();
    	ResultSet rs = null;
    	String flag = "false";
    	StringBuffer errorMsg = new StringBuffer();
    	java.sql.Date oldDate=null,startDate=null;
    	String oldPortCode="";
    	try{
    		
    		buff.append(" select substr(fportcode,instr(fportcode,'-')+1) as fportcode,FCOMPDATE  from ").append(pub.yssGetTableName("tb_comp_resultdata"));
    		buff.append(" where FCOMPDATE between ").append(dbl.sqlDate(this.dBeginDate)).append(" and ").append(dbl.sqlDate(this.dEndDate)).append(" and FRECHECKSTATE=1");
    		buff.append(" and substr(fportcode,instr(fportcode,'-')+1) in(").append(operSql.sqlCodes(this.sPortCodes)).append(")");
    		buff.append(" order by substr(fportcode,instr(fportcode,'-')+1),FCOMPDATE");
    		
    		rs = dbl.openResultSet(buff.toString());
    		while(rs.next()){
//    			if(rs.getRow()==1){
//    				errorMsg = new StringBuffer();
//    			}
    			if(rs.getString("fportcode").equalsIgnoreCase(oldPortCode)){
	    			  
	    			  if(YssFun.dateDiff(oldDate, rs.getDate("FCOMPDATE"))>1){
	    				  startDate = rs.getDate("FCOMPDATE"); 
	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	    			  }
	    			  oldDate = rs.getDate("FCOMPDATE"); 
	    			  
	    		  }else{
	    			  if(oldPortCode.length()==0 && oldDate==null){
	    				  if(startDate==null ){
  	    				  startDate = rs.getDate("FCOMPDATE"); 
  	    			  }
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	        	    			  
	    			  }else{
	    				  errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
	    				  errorMsg.append("组合【"+rs.getString("fportcode")+"】对应的业务日期【"+YssFun.formatDate(rs.getDate("FCOMPDATE"), "yyyy-MM-dd"));
	        	    	
	    			  }
	    			  oldPortCode = rs.getString("fportcode");
	    			  oldDate = rs.getDate("FCOMPDATE"); 
	    		  }
    		}
    		if(errorMsg.toString().length()>0){
    			if(YssFun.dateDiff(startDate,oldDate)>0){
    				errorMsg.append("】至【"+YssFun.formatDate(oldDate,"yyyy-MM-dd")).append("】已确认监控结果...\r\n");
    			}else if(YssFun.dateDiff(startDate,oldDate)==0){
    				errorMsg.append("】已确认监控结果...\r\n");
    			}
    			
        		errorMsg.append("请先反确认以上组合的监控数据，再进行生成操作！");
    			throw new YssException(errorMsg.toString());
    		}
    		
    	}catch(Exception e){
    		throw new YssException(errorMsg.toString().length()>0?errorMsg.toString():"监控结果检查报错......");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	
	}
    /**
     * STORY #1509  add by jiangshichao
     *  必须确认净值统计表后，才能产生监控结果，如未确认给出提示
     * @param sAsset String：组合群代码
     * @return ArrayList：选中的组合
     * @throws YssException
     */
    public String getSelectPortCodesByAssetGroupCode(String sAsset) throws YssException {
        String[] arrSelectObj = null;
        StringBuffer sPortbuff = new StringBuffer();
        try {
            arrSelectObj = sPortCodes.split(",");
            for (int i = 0; i < arrSelectObj.length; i++) {
                int iBegin = arrSelectObj[i].indexOf("-");
                if (iBegin == -1) {
                	sPortbuff.append(arrSelectObj[i]).append(",");
                } else {
                    if (arrSelectObj[i].substring(0, iBegin).equalsIgnoreCase(sAsset)) {
                        sPortbuff.append(arrSelectObj[i].substring(iBegin + 1)).append(",");
                    }
                }
            }
            if(sPortbuff.toString().trim().length()>0){
            	return sPortbuff.toString().substring(0, sPortbuff.toString().length()-1);
            }else{
            	return "";
            }
        } catch (Exception e) {
            throw new YssException("获取已选组合群下的所有已选组合出错！\r\n" + e.getMessage());
        }
    }
    
    
}
