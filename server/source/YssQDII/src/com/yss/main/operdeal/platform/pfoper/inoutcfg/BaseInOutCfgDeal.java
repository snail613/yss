package com.yss.main.operdeal.platform.pfoper.inoutcfg;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.base.*;
import java.util.*;
import java.sql.*;

public class BaseInOutCfgDeal
    extends BaseCalcFormula {
    public BaseInOutCfgDeal() {
    }

    protected String sInOutCodes = ""; //通用导入参数编号
    protected String sAllData = ""; //可存导入的数据
    protected String sFlag = ""; //操作类型

    public void init(String sInOutCodes, String sFlag, String sAllData) {
        this.sInOutCodes = sInOutCodes;
        this.sFlag = sInOutCodes;
        this.sAllData = sAllData;
    }

    //获取一些参数
    public String getOperValue(String sType) throws YssException {
        return "";
    }

    //加载listview数据用的
    public String loadListView() throws YssException {
        return "";
    }

    //导入数据
    public void importData(String sRequestStr) throws YssException {
    }

    //导出数据
    public String exportData(String sRequestStr) throws YssException {
        return "";
    }

    //通用解析过程
    protected String pretExpress(String sExPress) throws YssException {
        String sResult = "";
        this.formula = buildSql(sExPress);
        this.sign = "[,]";
        //validateScript();
        sResult = (String) getFormulaValue(formula);
        return sResult;
    }

    //处理特定字符
    protected String buildSql(String sSql) {
        if (sSql.indexOf("<group>") > 0) {
            sSql = sSql.replaceAll("<group>", pub.getAssetGroupCode());
        }
        if (sSql.indexOf("<Group>") > 0) {
            sSql = sSql.replaceAll("<Group>", pub.getAssetGroupCode());
        }
        return sSql;
    }
    
    //此方法用于做特殊字符的替换 by leeyu  modify  by wangzuochun 2010.04.16 MS01081    系统增加通过通用导入导出来导词汇、菜单条、功能调用、权限等功能    QDV4赢时胜上海2010年03月12日01_AB
  //modify  by zhangfa 20101018 MS01742    接口自定义配置中包含分隔符~，业务平台导出时会报错    QDV4华夏2010年09月14日01_B 
    protected String replaceAll(String sDS,String flag) throws YssException {
        try {
            if (sDS.indexOf("◆") > -1) {
                sDS = sDS.replaceAll("◆", "\t");
            }
            if (sDS.indexOf("[tab]") > -1) { //根据通用导入前台传的前符号，将此转换成\t符 by leeyu 20090724 && this.tailPortCode.length() > 0) { //添加尾差帐户作为条件　QDV4赢时胜（上海）2009年4月28日04_B MS00422 by leeyu 200905012
                sDS = sDS.replaceAll("\\[tab\\]", "\t");
            }
            if(sDS.indexOf("[Enter]")>0){
            	sDS =sDS.replaceAll("\\[Enter\\]", "\r\n");//将前台的[Enter]转换成\r\n
        	}  
            if(flag.trim().equals("out")){
            	if(sDS.indexOf("~") > -1){
                	sDS = sDS.replaceAll("~", "▲");
                }
            }else if(flag.trim().equals("in")){
            	if(sDS.indexOf("▲") > -1){
            		sDS = sDS.replaceAll("▲", "~");
            	}
            }
            
            return sDS;
        } catch (Exception ex) {
            throw new YssException("替换与处理特殊字符出错", ex);
        }
    }
    //------------------------MS01742-----------------------------------------------------------------------------         
    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = sKeyword;
        return objResult;
    }

    // add by leeyu 080805
    public Object getExpressValueEx(String sExpress, ArrayList alParams, String sEndStr) throws YssException {
        String sResult = "";
        try {
            if (sExpress.toLowerCase().endsWith("yssin")) {
                sResult = YssIn(sExpress, alParams);
            }
            sResult += sEndStr;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
        return sResult;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws
        YssException {
        return getExpressValueEx(sExpress, alParams, "");
        /*String sResult = "";
               try{
           if(sExpress.toLowerCase().endsWith("yssin")){
              sResult = YssIn(sExpress,alParams);
           }
               }
               catch(Exception e){
           throw new YssException(e.getMessage());
               }
               return sResult;*/
    }

    /**
     * 获取一个表的全部字段
     * @param sTabName String
     * @return String
     * @throws YssException
     */
    protected String getTabField(String sSql) throws YssException {
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        try {
            rs = dbl.openResultSet(buildSql(sSql));
            rsmd = rs.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                buf.append(rsmd.getColumnName(i + 1).toLowerCase())
                    .append(",");
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取表字段信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
   /**
    * 此方法获取表的字段与字段类型
    * by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
    * @param sSql
    * @return
    * @throws YssException
    */
   protected String getTabFields(String sSql) throws YssException{
	      StringBuffer buf=new StringBuffer();
	      ResultSet rs = null;
	      ResultSetMetaData rsmd = null;
	      try{
	         rs =dbl.openResultSet(buildSql(sSql));
	         rsmd=rs.getMetaData();
	         for(int i=0;i<rsmd.getColumnCount();i++){
	            buf.append(rsmd.getColumnName(i+1).toLowerCase())
	            .append(";").append(rsmd.getColumnTypeName(i+1).toLowerCase())//添加字段类型 by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
	                  .append(",");
	         }
	         return buf.toString();
	      }catch(Exception e){
	         throw new YssException("获取表字段信息出错",e);
	      }finally{
	         dbl.closeResultSetFinal(rs);
	      }
	   }
   
   /**
   * 获取表的主键字段
   *QDV4交银施罗德2009年9月01日01_B MS00661 by leeyu 20100324
   **/
   protected String buildTabPKFiled(String tableName) throws YssException{
	   ResultSet rs =null;
	   String sqlStr="";
	   String sPKField="";
	   try{
		   if(dbl.dbType == YssCons.DB_ORA){
			   sqlStr="select Column_Name as FPKField from User_Cons_Columns "+
			   " where table_Name=upper('"+tableName+"') and Position > 0";			   
		   }else if(dbl.dbType == YssCons.DB_DB2){
			   
		   }
		   rs =dbl.openResultSet(buildSql(sqlStr));
		   while(rs.next()){
			   sPKField+=(rs.getString("FPKField")+",");
		   }
		   if(sPKField.endsWith(",")){
			   sPKField=sPKField.substring(0,sPKField.length()-1);
		   }
	   }catch(Exception ex){
		   throw new YssException("获取表【"+tableName+"】主键字段出错",ex);
	   }finally{
		   dbl.closeResultSetFinal(rs);
	   }
	   return sPKField;
   }
    /**
     * 获取一个表的字段信息,包括字段名,字段类型,字段长度
     * @param sTabName String
     * @return String
     * @throws YssException
     */
    protected String buildTabParam(String sSql) throws YssException {
        StringBuffer buf = new StringBuffer();
        String[] arrData = null;
        ResultSet rs = null;
        try {
            rs = dbl.openResultSet(buildSql(sSql));
            arrData = dbFun.getFieldsParam(rs);
            for (int i = 0; i < arrData.length; i++) {
                buf.append(arrData[i]).append(";");
            }
            return buf.toString().toLowerCase();
        } catch (Exception e) {
            throw new YssException("获取一个表的信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

}
