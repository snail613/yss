package com.yss.dsub;

import com.yss.util.YssFun;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.yss.util.YssException;
import java.util.HashMap;
import com.yss.pojo.sys.YssCancel;
import java.io.*;
import com.yss.pojo.sys.YssPageInationBean;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu

public class BaseDataSettingBean
    extends BaseBean {
    public int checkStateId;   
	public String checkStateName = "";
    public String creatorCode = "";
    public String creatorName = "";
    public String creatorTime = "";
    public String checkUserCode = "";
    public String checkUserName = "";
    public String checkTime = "";

    private String listView1Headers = "";
	private String listView1ShowCols = "";
    private String listView3Headers = "";
    private String listView3ShowCols = "";
    public String isOnlyColumns = ""; //是否只读取列名的标志 add by ysh 20111028
    
    public String getIsOnlyColumns() {
		return isOnlyColumns;
	}
	public void setIsOnlyColumns(String isOnlyColumns) {
		this.isOnlyColumns = isOnlyColumns;
	}

	protected YssPageInationBean yssPageInationBean=null;//QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    
    //setting Method QDV4赢时胜上海2009年12月21日06_B MS00884 by leeyu
    public void setYssPageInationBean(YssPageInationBean yssPageInationBean){
    	this.yssPageInationBean = yssPageInationBean;
    }
    public String getListView1Headers() {
        return listView1Headers;
    }

    public void setListView1ShowCols(String listView1ShowCols) {
        this.listView1ShowCols = listView1ShowCols;
    }

    public void setListView1Headers(String listView1Headers) {
        this.listView1Headers = listView1Headers;
    }

    public void setListView3ShowCols(String listView3ShowCols) {
        this.listView3ShowCols = listView3ShowCols;
    }

    public void setListView3Headers(String listView3Headers) {
        this.listView3Headers = listView3Headers;
    }
   
	
    public String getListView1ShowCols() {
        return listView1ShowCols;
        /*
               String sResult = "";
               StringBuffer buf = new StringBuffer();
               String[] sShowColsAry = null;
               sResult = listView1ShowCols;

               if (sResult.indexOf(";") >= 0) {
           sShowColsAry = sResult.split("\t");
           for (int i = 0; i < sShowColsAry.length; i++) {
              if (sShowColsAry[i].indexOf(";") >= 0) {
                 buf.append(sShowColsAry[i].split(";")[0]).append("\t");
              }
              else {
                 buf.append(sShowColsAry[i]).append("\t");
              }
           }
           if (buf.toString().length() > 1) {
              sResult = buf.toString().substring(0,
                    buf.toString().length() - 1);
           }
               }
               return sResult;
         */
    }

    public String getListView3ShowCols() {
        return listView3ShowCols;
    }

    public String getListView3Headers() {
        return listView3Headers;
    }

    public BaseDataSettingBean() {
    }

    public void parseRecLog() {
        this.checkStateName = YssFun.getCheckStateName(this.checkStateId);
        this.creatorCode = pub.getUserCode();
        this.creatorName = pub.getUserName();
        this.checkUserCode = pub.getUserCode();
        this.creatorTime = YssFun.formatDatetime(new java.util.Date());
        this.checkTime = YssFun.formatDatetime(new java.util.Date());
    }

    public void setRecLog(ResultSet rs) throws SQLException {
        this.checkStateId = rs.getInt("FCheckState");
        this.checkStateName = YssFun.getCheckStateName(rs.getInt("FCheckState"));
        this.creatorCode = rs.getString("FCreator") + "";
        this.creatorName = rs.getString("FCreatorName") + "";
        this.creatorTime = rs.getString("FcreateTime") + "";
        this.checkUserCode = rs.getString("FCheckUser") + "";
        this.checkUserName = rs.getString("FCheckUserName") + "";
        this.checkTime = rs.getString("FCheckTime") + "";
    }

    public String buildRecLog() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.checkStateId).append("\t");
        buf.append(this.checkStateName.trim()).append("\t");
        buf.append(this.creatorCode.trim()).append("\t");
        buf.append(this.creatorName.trim()).append("\t");
        buf.append(this.creatorTime.trim()).append("\t");
        buf.append(this.checkUserCode.trim()).append("\t");
        buf.append(this.checkUserName.trim()).append("\t");
        buf.append(this.checkTime.trim());
        return buf.toString();
    }

    public String buildRecLog(ResultSet rs) throws SQLException {
        StringBuffer buf = new StringBuffer();
        buf.append(rs.getInt("FCheckState")).append("\t");
        buf.append(YssFun.getCheckStateName(rs.getInt("FCheckState"))).append(
            "\t");
        buf.append(rs.getString("FCreator")).append("\t");
        buf.append(rs.getString("FCreatorName")).append("\t");
        buf.append(rs.getString("FCreateTime")).append("\t");
        buf.append(rs.getString("FCheckUser")).append("\t");
        buf.append(rs.getString("FCheckUserName")).append("\t");
        buf.append(rs.getString("FCheckTime"));
        return buf.toString();
    }

    //配置ListView显示列时对特殊列的操作
    public void beforeBuildRowShowStr(YssCancel bCancel, String sColName,
                                      ResultSet rs, StringBuffer buf) throws
        Exception {

    }

    //配置ListView显示列时取出列数据后后续操作判断
    public boolean isJudge(String sColName) {
        return true;
    }

    //配置ListView显示列时取出列数据后后续操作
    public void buildRowOtherShowStr(String sColName, ResultSet rs,
                                     StringBuffer buf) throws
        Exception {
    }

    public String buildRowShowStr(ResultSet rs, String sShowFields) throws
        YssException {
        StringBuffer buf = new StringBuffer();
        String[] sFieldAry = sShowFields.split("\t");
        HashMap hmFieldType = null;
        String sFieldType = null;
        String sResult = "";
        YssCancel before = new YssCancel();
        String sFieldName = "";
        String sFieldFormat = "";
        try {
            hmFieldType = dbFun.getFieldsType(rs);
            if (hmFieldType == null) {
                return "";
            }
            for (int i = 0; i < sFieldAry.length; i++) {
                before.setCancel(false);
                beforeBuildRowShowStr(before, sFieldAry[i], rs, buf);
                if (!before.isCancel()) {
                    sFieldFormat = "";
                    if (sFieldAry[i].indexOf(";") > 0) {
                        sFieldName = sFieldAry[i].split(";")[0];
                        sFieldFormat = sFieldAry[i].split(";")[1];
                    } else {
                        sFieldName = sFieldAry[i];
                    }
                    sFieldType = (String) hmFieldType.get(sFieldName.toUpperCase());
                    if (sFieldType != null) {
                        if ( (sFieldType).indexOf("DATE") > -1) {
                            if (rs.getDate(sFieldName) != null) {
                                buf.append(YssFun.formatDate(rs.getDate(sFieldName)));
                            } else {
                                buf.append("");
                            }
                        }
                      //add by huangqirong 2011-06-28 story #1190 
                        else if(sFieldName.equalsIgnoreCase("fDetailResult")){
                            buf.append(rs.getString(sFieldName) + "条不一致");
                        }
                      //---end---
                        else if ( (sFieldType).indexOf("NUMBER") > -1) {
                            //add by huangqirong 2012-02-29 bug #3771 字段类型为 NUMBER 但不需要格式化显示 
                        	if(sFieldFormat.length() > 0 && sFieldFormat.trim().equalsIgnoreCase("NONFORMAT")){
                        		buf.append(rs.getString(sFieldName) + "");
                        	} else
                        	//---end---
                        	if (sFieldFormat.length() > 0) {
                                buf.append(YssFun.formatNumber(rs.getDouble(sFieldName),
                                    sFieldFormat) + "");
                            } else {
                                buf.append(rs.getDouble(sFieldName) + "");//add bylidaolong  20110428 BUG1817存款利率设置界面，新建利率0.8，在未审核和已审核界面显示的却只有“.8” 
                            }
                        } else if ( (sFieldType).indexOf("CLOB") > -1) {
                            buf.append(dbl.clobStrValue(rs.getClob(sFieldName)));
                        } else {
//                     rs.getClob()
                            buf.append(rs.getString(sFieldName) + "");                            
                        }
                        buf.append("\t");
                    }
                    if (isJudge(sFieldName)) {
                        buildRowOtherShowStr(sFieldName, rs, buf);
                    }
                }
            }
            sResult = buf.toString();
            if (sResult.trim().length() > 1) {
                sResult = sResult.substring(0, sResult.length() - 1);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("生成显示数据出错");
        }
    }
    
    //added by liubo.Story #1770
    //重载此方法，用于在跨组合群的LISTVIEW界面中，加载出以逗号分隔开的组合群代码所代表的组合群名称
    //如组合群代码为"001,002,003"，通过此重载的方法，加载出"组合群001,组合群002，组合群003"这样的组合群名称
    public String buildRowShowStr(ResultSet rs, String sShowFields, String FAssetGroupName) throws
    YssException {
    StringBuffer buf = new StringBuffer();
    String[] sFieldAry = sShowFields.split("\t");
    HashMap hmFieldType = null;
    String sFieldType = null;
    String sResult = "";
    YssCancel before = new YssCancel();
    String sFieldName = "";
    String sFieldFormat = "";
    try {
        hmFieldType = dbFun.getFieldsType(rs);
        if (hmFieldType == null) {
            return "";
        }
        for (int i = 0; i < sFieldAry.length; i++) {
            before.setCancel(false);
            beforeBuildRowShowStr(before, sFieldAry[i], rs, buf);
            if (!before.isCancel()) {
                sFieldFormat = "";
                if (sFieldAry[i].indexOf(";") > 0) {
                    sFieldName = sFieldAry[i].split(";")[0];
                    sFieldFormat = sFieldAry[i].split(";")[1];
                } else {
                    sFieldName = sFieldAry[i];
                }
                sFieldType = (String) hmFieldType.get(sFieldName.toUpperCase());
                if (sFieldType != null) {
                    if ( (sFieldType).indexOf("DATE") > -1) {
                        if (rs.getDate(sFieldName) != null) {
                            buf.append(YssFun.formatDate(rs.getDate(sFieldName)));
                        } else {
                            buf.append("");
                        }
                    }
                  //add by huangqirong 2011-06-28 story #1190 
                    else if(sFieldName.equalsIgnoreCase("fDetailResult")){
                        buf.append(rs.getString(sFieldName) + "条不一致");
                    }
                    else if(sFieldName.equalsIgnoreCase("FAssetGroupName"))
                    {
                    	buf.append(FAssetGroupName);
                    }
                  //---end---
                    else if ( (sFieldType).indexOf("NUMBER") > -1) {
                        if (sFieldFormat.length() > 0) {
                            buf.append(YssFun.formatNumber(rs.getDouble(sFieldName),
                                sFieldFormat) + "");
                        } else {
                            buf.append(rs.getDouble(sFieldName) + "");//add bylidaolong  20110428 BUG1817存款利率设置界面，新建利率0.8，在未审核和已审核界面显示的却只有“.8” 
                        }
                    } else if ( (sFieldType).indexOf("CLOB") > -1) {
                        buf.append(dbl.clobStrValue(rs.getClob(sFieldName)));
                    } else {
//                 rs.getClob()
                        buf.append(rs.getString(sFieldName) + "");                            
                    }
                    buf.append("\t");
                }
                if (isJudge(sFieldName)) {
                    buildRowOtherShowStr(sFieldName, rs, buf);
                }
            }
        }
        sResult = buf.toString();
        if (sResult.trim().length() > 1) {
            sResult = sResult.substring(0, sResult.length() - 1);
        }
        return sResult;
    } catch (Exception e) {
        throw new YssException("生成显示数据出错");
    }
}
}
