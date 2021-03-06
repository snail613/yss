package com.yss.commeach;

import java.sql.Connection;
import java.sql.ResultSet;

import com.yss.util.YssException;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;

/**
 * 
 * add by wangzuochun 2009.10.28 MS00756 增加在生成新净值表后，执行某个接口群里的接口的功能  QDV4华夏2009年10月19日01_A 
 *
 */
public class ImpExpInterCodes extends BaseCommEach {
	
	private String sPortCode;
	
	public ImpExpInterCodes(){
		
	}
	
	public String getSPortCode() {
        return sPortCode;
    }
	
	public void setSPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }
	
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.sPortCode).append("\t");
        return buf.toString();
    }
	
	public void parseRowStr(String sRowStr) throws YssException {
        String reqAry[] = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            reqAry = sRowStr.split("\t");
            this.sPortCode = reqAry[0];
        } catch (Exception e) {
            throw new YssException("解析数据出错", e);
        }
    }
	
	public String getExpInterCodes() throws YssException {
    	String strGroupCode = "";
    	String strCusCodes = "";
    	String strSql = "";
    	ResultSet rs = null;
		/**shashijie 2012-7-2 STORY 2475 */
    	//Connection conn = dbl.loadConnection();
    	/**end*/
    	CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        strGroupCode = pubpara.getExpInterGroup(this.sPortCode);
        
        try{
        	if(!strGroupCode.equals("No")){
        		strSql = "select FCusconfigCodes from " + pub.yssGetTableName("Tb_Dao_Group") +
   			 			" where FGroupCode = " + dbl.sqlString(strGroupCode) + " and FGroupType = 'Cus' and FCheckState = 1";
   	
        		rs = dbl.openResultSet(strSql);
        		if (rs.next()){
        			strCusCodes = (rs.getString("FCusconfigCodes") == null ? "" : rs.getString("FCusconfigCodes"));
        		}
        	}
        return strCusCodes;
        }catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	public String getImpInterCodes() throws YssException {
    	String strGroupCode = "";
    	String strCusCodes = "";
    	String strSql = "";
    	ResultSet rs = null;
		/**shashijie 2012-7-2 STORY 2475 */
    	//Connection conn = dbl.loadConnection();
    	/**end*/
    	CtlPubPara pubpara = new CtlPubPara();
        pubpara.setYssPub(pub);
        strGroupCode = pubpara.getImpInterGroup(this.sPortCode);
        
        try{
        	if(!strGroupCode.equals("No")){
        		strSql = "select FCusconfigCodes from " + pub.yssGetTableName("Tb_Dao_Group") +
        			 	" where FGroupCode = " + dbl.sqlString(strGroupCode) + " and FGroupType = 'Cus' and FCheckState = 1";
        	
        		rs = dbl.openResultSet(strSql);
        		if (rs.next()){
        			strCusCodes = (rs.getString("FCusconfigCodes") == null ? "" : rs.getString("FCusconfigCodes"));
        		}
        	}
        return strCusCodes;
        }catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
	
	public String getOperValue(String sType) throws YssException {
        String reStr = "";
        if (sType.equalsIgnoreCase("getexpcodes")) {
        	reStr = getExpInterCodes();
        }
        else if (sType.equalsIgnoreCase("getimpcodes")) {
        	reStr = getImpInterCodes();
        }
        return reStr;
	}
}


