package com.yss.main.cusreport;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.util.*;
/**
 * add by guolongchao 201120110 STORY 1284
 * 报表文件名Pojo类
 */
public class RepFileNameBean extends BaseDataSettingBean implements IDataSetting 
{
    private String cusRepCode = "";        // 报表代码
    private String cusRepName = "";        // 报表名称       
	private String fileNameType = "";      // 文件名类型
    private String fileNameTypeValue = ""; // 文件名类型Value
    private String fileNameContent = "";   // 文件名内容
    private String format = "";  
	private int    delayDays=0;	
    private String holidaycode="";
    private String holidayname="";    
    private String desc = "";               // 描述
    private String orderNum;                // 排序号          
    private String oldRepCode = "";         // 报表代码
    
    private RepFileNameBean filterType;
   
    public String getCusRepCode() {
		return cusRepCode;
	}

	public void setCusRepCode(String cusRepCode) {
		this.cusRepCode = cusRepCode;
	}
	
	 public String getOldRepCode() {
		return oldRepCode;
	}

	public void setOldRepCode(String oldRepCode) {
		this.oldRepCode = oldRepCode;
	}
		
    public void checkInput(byte btOper) throws YssException {    	
    }
    
    public String addSetting() throws YssException {
          PreparedStatement pstmt = null;
          ResultSet rs = null;
          Connection con = dbl.loadConnection();
          int Num = 0;
          String sql = "";       
          try 
          {
              sql = "select * from " + pub.yssGetTableName("TB_Rep_FileName") +
                  " where FRepCode=" + dbl.sqlString(this.oldRepCode);
              rs = dbl.openResultSet(sql);

              sql = "insert into " + pub.yssGetTableName("TB_Rep_FileName") +
                  "(FRepCode,FOrderNum,FFileNameContent,FFileNameType,FFormat,FDesc," +
                  " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,delaydays,holidaycode)" +
                  " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
              pstmt = con.prepareStatement(sql);
              while (rs.next()) 
              {
                  Num = Num + 1;                 
                  pstmt.setString(1, this.cusRepCode);
                  pstmt.setInt(2, Num);
                  pstmt.setString(3, rs.getString("FFileNameContent"));
                  pstmt.setString(4, rs.getString("FFileNameType"));
                  pstmt.setString(5, rs.getString("FFormat"));
                  pstmt.setString(6, rs.getString("FDesc"));
                  pstmt.setInt(7,(pub.getSysCheckState() ? 0 : 1));
                  pstmt.setString(8, pub.getUserCode());
                  pstmt.setString(9, YssFun.formatDatetime(new java.util.Date()));
                  pstmt.setString(10,(pub.getSysCheckState() ? " " : pub.getUserCode()));
                  pstmt.setString(11,(pub.getSysCheckState() ? " " : YssFun.formatDatetime(new java.util.Date())));                   
                  pstmt.setInt(12, rs.getInt("delaydays"));
                  pstmt.setString(13, rs.getString("holidaycode"));                    
                  pstmt.addBatch();
              }                
              pstmt.executeBatch();
          } catch (Exception ex) {
              throw new YssException("保存报表文件名称信息出错\r\n" + ex.getMessage());
          } finally {
              dbl.closeStatementFinal(pstmt);
          }
          return "";
    }

    public void setAttr(ResultSet rs) throws YssException {
        try 
        {
            this.cusRepCode = rs.getString("FRepCode");     
            this.cusRepName=rs.getString("FRepName");    
            this.fileNameType = rs.getString("FFileNameType");
            this.fileNameTypeValue = rs.getString("FFileNameTypeValue");
            this.fileNameContent = rs.getString("FFileNameContent");        
            this.format = rs.getString("FFormat");         
            this.delayDays=rs.getInt("delaydays");
            this.holidaycode=rs.getString("holidaycode");
            this.holidayname=rs.getString("holidayname"); 
            this.desc = rs.getString("FDesc");
            this.orderNum = rs.getString("FOrderNum");  
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {

    }

    public void checkSetting() throws YssException {
    }

    /**
     * @param sMutilRowStr String
     * @return String
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        String[] sMutilRowAry = null;
        PreparedStatement pstmt = null;
        Connection con = dbl.loadConnection();
        String sql = "";       
        try 
        {
            sMutilRowAry = sMutilRowStr.split(YssCons.YSS_LINESPLITMARK);
            sql = "delete from " + pub.yssGetTableName("TB_Rep_FileName") +
                " where FRepCode=" + dbl.sqlString(this.oldRepCode);
            dbl.executeSql(sql);

            sql = "insert into " + pub.yssGetTableName("TB_Rep_FileName") +
                "(FRepCode,FOrderNum,FFileNameContent,FFileNameType,FFormat,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,delaydays,holidaycode)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < sMutilRowAry.length; i++) {
                this.parseRowStr(sMutilRowAry[i]);
                if (this.cusRepCode.trim().length() > 0) {
                    pstmt.setString(1, this.cusRepCode);
                    pstmt.setInt(2, i + 1);
                    pstmt.setString(3, this.fileNameContent);
                    pstmt.setString(4, this.fileNameType);
                    pstmt.setString(5, this.format);
                    pstmt.setString(6, this.desc);
                    pstmt.setInt(7,(pub.getSysCheckState() ? 0 : 1));
                    pstmt.setString(8, this.creatorCode);
                    pstmt.setString(9, this.creatorTime);
                    pstmt.setString(10,(pub.getSysCheckState() ? " " : this.creatorCode));
                    pstmt.setString(11,(pub.getSysCheckState() ? " " : this.checkTime));                   
                    pstmt.setInt(12, this.delayDays);
                    pstmt.setString(13, this.holidaycode);                    
                    pstmt.addBatch();
                }                
            }
            pstmt.executeBatch();
        } catch (Exception ex) {
            throw new YssException("保存报表文件名称信息出错\r\n" + ex.getMessage());
        } finally {
            dbl.closeStatementFinal(pstmt);
        }
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }

    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.oldRepCode.length() != 0) {
                sResult = sResult + " and a.FRepCode ='" + filterType.oldRepCode+ "'"; 
            }            
        }
        return sResult;
    }

    public String getListViewData1() throws YssException {
        String strSql =
            "select distinct y.* from " +
            " (select a.*,b.FUserName as FCreatorName, c.FUserName as FCheckUserName , x1.fholidaysname as holidayname, " +
            "  f.FVocName as FFileNameTypeValue, g.FCusRepName as FRepName from " +
              pub.yssGetTableName("TB_Rep_FileName") + " a" +            
            " left join (select fholidayscode,fholidaysname  from Tb_Base_Holidays  ) x1 on a.holidaycode=x1.fholidayscode"+
            " left join (select FCusRepCode,FCusRepName from " + pub.yssGetTableName("Tb_Rep_Custom") + ") g on a.FRepCode = g.FCusRepCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +          
            " left join  Tb_Fun_Vocabulary f on a.FFileNameType = f.FVocCode and f.FVocTypeCode = " +
              dbl.sqlString("report_FileName") +          
            buildFilterSql() +
            ")y order by y.FRepCode,y.FOrderNum";        
        return builderListViewData(strSql);
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        ResultSet rs = null;       
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                setAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            } 
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取报表文件头设置信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getListViewData3() throws YssException {
        String strSql = "select * from " + pub.yssGetTableName("TB_Rep_FileName") + " where 1=2";
        return builderListViewData(strSql);
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sTmpStr.split("\t");        
            this.cusRepCode = reqAry[0];                 
            this.fileNameType = reqAry[1];
            this.fileNameTypeValue = reqAry[2];
            this.fileNameContent = reqAry[3];
            this.format = reqAry[4];
            this.delayDays=YssFun.toInt(reqAry[5]);
            this.holidaycode=reqAry[6];   
            this.holidayname=reqAry[7];   
            this.desc = reqAry[8];   
            this.oldRepCode = reqAry[9];               
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new RepFileNameBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("获取文件名称设置信息出错！");
        }
    }

    public String buildRowStr() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.cusRepCode).append("\t"); 
        buf.append(this.cusRepName).append("\t");
        buf.append(this.fileNameType).append("\t");
        buf.append(this.fileNameTypeValue).append("\t");
        buf.append(this.fileNameContent).append("\t"); 
        buf.append(this.format).append("\t");  
        buf.append(this.delayDays).append("\t");
        buf.append(this.holidaycode).append("\t");
        buf.append(this.holidayname).append("\t");
        buf.append(this.desc).append("\t");
        buf.append(this.orderNum).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
    public void deleteRecycleData() {
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
	public String getBeforeEditData() throws YssException {		
		return null;
	}
}
