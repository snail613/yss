package com.yss.main.parasetting;

import java.sql.*;
import java.text.DecimalFormat;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.*;

/**
 * STORY #2459 统计参数监控的报表
 * @author Administrator
 *
 */

public class CMBParaBean
    extends BaseDataSettingBean  implements IDataSetting {
    private String sRecycled = ""; //保存未解析前的字符串
   

    public CMBParaBean() {
    }
    private String strSetCode="";//套帐代码
	private String strSetName="";//套帐名称
	private String strAccsetCode="";//资产代码

    //private String strGLRCode = "";//管理人代码
	private String strGLRName="";//管理人源名称
	//private String strWTRCode=""; //托管人代码
	private String strWTRame=""; //托管人名称
    //private String strSTRCode = ""; //受托人代码
    private String strSTRName = ""; //受托人名称

    private String strYYBranch = ""; //运营分行
    private String strLYBranch = "";//来源分行
    
    private String strYYBranchCode = ""; //运营分行代码
    private String strLYBranchCode = "";//来源分行代码


    private String strAssetType1 = "";//资产类别（一级）
    private String strAssetType2 = "";//资产类别（二级）
    private String strAssetType3 = "";//资产类别（三级）

    private String strInvType1 = "";//运营类别（一级）
    private String strInvType2 = "";//运营类别（二级）
    
    private String strAssetTypeCode1 = "";//资产类别（一级）代码
    private String strAssetTypeCode2 = "";//资产类别（二级）代码
    private String strAssetTypeCode3 = "";//资产类别（三级）代码

    private String strInvTypeCode1 = "";//运营类别（一级）代码
    private String strInvTypeCode2 = "";//运营类别（二级）代码
    
   


    private String strTZGLFS = "";//投资管理方式
    private String strXSQD = "";//销售渠道
    
    private String strTZGLFSCode = "";//投资管理方式
    private String strXSQDCode = "";//销售渠道
    
    private String strXSCode = "";//销售代码
    private String strTGZH = "";//托管账号
    private String strTGFL ;//托管费率

	private String dblStartDate = "";//成立日期
	private String dblEndDate = "";//终止日期
	
	private String strOldSetCode = "";
    private CMBParaBean filterType;
    
    //private String strGetKey;
    //private String strGetValue;
    private String strType = "";//资产类型联动处理
   
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet rSet = null;
        String sVocStr = "";
        String strUserName = "";
        
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufUserCode = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_AsserType_One  + 
            		"," + YssCons.YSS_AsserType_Two 
            		+"," + YssCons.YSS_AsserType_Three 
            		+"," + YssCons.YSS_InvestType_One  
            		+"," + YssCons.YSS_InvestType_Two  
            		+"," + YssCons.YSS_INVESTBRANCH  
            		+"," + YssCons.YSS_SourceBranch  
            		+"," + YssCons.YSS_InvestMagMode
            		+ "," + YssCons.YSS_SellChannel );    
            /*
            if (this.filterType.isOnlyColumns.equals("1")&&!(pub.isBrown())) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\fvoc" + sVocStr;
            
            }*/
            
            strSql = "select distinct a.Fusercode ,b.FUserName  from Tb_Sys_UserRight a " +
            	     " left join ( select FUserCode,FUserName from Tb_Sys_UserList  ) b  "+
            	     " on a.Fusercode = b.Fusercode " +
            	     " where a.frightind='Role' and a.FRightCode = 'R111' ";
	        rSet = dbl.openResultSet(strSql);
	        while (rSet.next()) {
	           //经办人
	        	bufUserCode.append(rSet.getString("FUserName")).append(",");
	        }
	        if(bufUserCode.toString().length() > 2){	        	
	        	
	        	strUserName = bufUserCode.toString().substring(0,
	        			bufUserCode.toString().length() - 1);
	        }
            
            strSql = "select to_char(a.Fsetcode,'000') as Fsetcode ,Fyyfh,Ftzglr,Fwtr,Fstr,Flyfh,"+
            		 "Fzclb1,Fzclb2,Fzclb3,Ftzglfs,Fxsqd,"+
            	     "Fxsdm,FYYLB1,FYYLB2,FTGZH,Fstartdate,Fenddate,to_char(Ftgfl) as Ftgfl " +
            		", c.FBookSetName as FSetName,c.FAssetCode ," +
            		" Vov1.FVocName as Fzclb1Name,Vov2.FVocName as Fzclb2Name,Vov3.FVocName as Fzclb3Name, " +
            		" Voc1.FVocName as FYYLB1Name,Voc2.FVocName as FYYLB2Name, " +
            		" Voc3.FVocName as FyyfhName,Voc4.FVocName as FlyfhName," +
            		" Voc5.FVocName as FtzglfsName,Voc6.FVocName as FxsqdName ,'" +
            		 strUserName + "' as FJBR from "
            +" StatisticalParameters a"	
//            +" left join ( select FBookSetCode,FBookSetName from  "+ pub.yssGetTableName("Tb_Vch_BookSet")  
//            +" ) b on a.FSetCode = b.FBookSetCode " 
            +" left join ( select distinct FPortCode, fsetcode as fsetcode2, FPortName as FBookSetName,fassetcode "
            +" from lsetlist l join " + pub.yssGetTableName("Tb_Para_Portfolio")  + " p on l.fsetid = p.fassetcode "  //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
            +" ) c on a.FSetCode = c.fsetcode2 "
//            +" left join ( select FPortCode , FAssetCode from  " + pub.yssGetTableName("Tb_Para_Portfolio")   //组合设置
//            +" ) d on c.FPortCode = d.FPortCode "            
            + " LEFT JOIN Tb_Fun_Vocabulary Vov1 on a.Fzclb1 = Vov1.FVocCode" 
            + " and Vov1.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_AsserType_One)            
            + " LEFT JOIN Tb_Fun_Vocabulary Vov2 on a.Fzclb2 = Vov2.FVocCode" 
            + " and Vov2.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_AsserType_Two)             
            + " LEFT JOIN Tb_Fun_Vocabulary Vov3 on a.Fzclb3 = Vov3.FVocCode" 
            + " and Vov3.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_AsserType_Three)
            
            
            + " LEFT JOIN Tb_Fun_Vocabulary Voc1 on a.FYYLB1 = Voc1.FVocCode" 
            + " and Voc1.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_InvestType_One)            
            + " LEFT JOIN Tb_Fun_Vocabulary Voc2 on a.FYYLB2 = Voc2.FVocCode" 
            + " and Voc2.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_InvestType_Two)
            
            + " LEFT JOIN Tb_Fun_Vocabulary Voc3 on a.Fyyfh = Voc3.FVocCode" 
            + " and Voc3.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_INVESTBRANCH)
            + " LEFT JOIN Tb_Fun_Vocabulary Voc4 on a.Flyfh = Voc4.FVocCode" 
            + " and Voc4.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SourceBranch)
            + " LEFT JOIN Tb_Fun_Vocabulary Voc5 on a.Ftzglfs = Voc5.FVocCode" 
            + " and Voc5.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_InvestMagMode)
            + " LEFT JOIN Tb_Fun_Vocabulary Voc6 on a.Fxsqd = Voc6.FVocCode" 
            + " and Voc6.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_SellChannel)
            
            + buildFilterSql() ;
            //+ " order by a.FCheckState, a.FCreateTime desc";
            rs = dbl.openResultSet(strSql);            
            
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setResultSetAttr(rs);
                //bufShow.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                
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
             this.getListView1ShowCols() + "\r\fvoc" + sVocStr;
            
        } catch (Exception e) {
            throw new YssException("获取统计参数信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rSet);
        }
    }

    public void setResultSetAttr(ResultSet rs) throws SQLException, YssException {
        this.strSetCode  = rs.getString("Fsetcode"); 
        this.strSetName  = rs.getString("FsetName") + "";
        this.strAccsetCode  = rs.getString("FAssetCode") + "";//------------
        this.strGLRName  = rs.getString("Ftzglr") + "";
        this.strWTRame  = rs.getString("Fwtr") + "";
        this.strSTRName  = rs.getString("Fstr") + "";
        this.strYYBranchCode  = rs.getString("Fyyfh") + "";
        //this.strDataSource=rs.getInt("FDataSource") + "";
        this.dblStartDate  = rs.getDate("Fstartdate") + "";
        this.dblEndDate  = rs.getDate("FEnddate") + "";
        
        this.strLYBranchCode  = rs.getString("Flyfh")+ "";
        this.strAssetTypeCode1  = rs.getString("Fzclb1")+ "";
        this.strAssetTypeCode2  = rs.getString("Fzclb2")+ "";
        this.strAssetTypeCode3  = rs.getString("Fzclb3")+ "";
        this.strInvTypeCode1  = rs.getString("FYYLB1")+ "";
        this.strInvTypeCode2  = rs.getString("FYYLB2")+ "";
        this.strTZGLFSCode  = rs.getString("Ftzglfs")+ ""; 
        
        this.strLYBranch  = rs.getString("FlyfhName")+ "";
        this.strAssetType1  = rs.getString("Fzclb1Name")+ "";
        this.strAssetType1  = rs.getString("Fzclb2Name")+ "";
        this.strAssetType3  = rs.getString("Fzclb3Name")+ "";
        this.strInvType1  = rs.getString("FYYLB1Name")+ "";
        this.strInvType2  = rs.getString("FYYLB2Name")+ "";
        this.strTZGLFS  = rs.getString("FtzglfsName")+ "";  
        this.strXSQD   = rs.getString("FxsqdName")+ "";
        this.strYYBranch  = rs.getString("FyyfhName") + "";
        
        this.strTGFL   = rs.getString("Ftgfl") ;
        this.strXSQDCode   = rs.getString("Fxsqd")+ "";
        this.strXSCode   = rs.getString("Fxsdm")+ "";
        this.strTGZH   = rs.getString("FTGZH")+ "";
        //super.setRecLog(rs);

    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if(pub.isBrown()==true) 
			return " where 1=1";
        if (this.filterType != null) {
            sResult = " where 1=1 ";
            /*
            if (this.filterType.isOnlyColumns.equals("1")) {
                sResult = sResult + " and 1 = 2 ";
                return sResult;
            }
            */
            if (this.filterType.strSetCode.length() != 0) {
                sResult = sResult + " and Fsetcode = " +
                    Double.parseDouble(filterType.strSetCode);
            }


            if (this.filterType.strGLRName.length() != 0) {
                sResult = sResult + " and a.Ftzglr  like '" +
                    filterType.strGLRName.replaceAll("'", "''") + "%'";
            }
            if (this.filterType.strWTRame.length() != 0) {
                sResult = sResult + " and a.Fwtr  like '" +
                    filterType.strWTRame.replaceAll("'", "''") + "%'";
            }
            
            if (this.filterType.strSTRName.length() != 0) {
                sResult = sResult + " and a.Fstr  like '" +
                    filterType.strSTRName.replaceAll("'", "''") + "%'";
            }

            if (this.filterType.dblStartDate.length() != 0 &&
                !this.filterType.dblStartDate.equals("9998-12-31")) {
                sResult = sResult + " and a.FStartDate = " +
                    dbl.sqlDate(filterType.dblStartDate);
            }
            if (this.filterType.dblEndDate.length() != 0 &&
                    !this.filterType.dblEndDate.equals("9998-12-31")) {
                    sResult = sResult + " and a.FEndDate = " +
                        dbl.sqlDate(filterType.dblEndDate);
                }
            
            
            if (this.filterType.strAssetTypeCode1.length() != 0 &&
                    !this.filterType.strAssetTypeCode1.equals("00")) {
                    sResult = sResult + " and a.Fzclb1 = " +
                        dbl.sqlString(this.filterType.strAssetTypeCode1 );
            }
            if (this.filterType.strAssetTypeCode2.length() != 0 &&
                    !this.filterType.strAssetTypeCode2.equals("00")) {
                    sResult = sResult + " and a.Fzclb2 = " +
                        dbl.sqlString(this.filterType.strAssetTypeCode2 );
            }
            if (this.filterType.strAssetTypeCode3.length() != 0 &&
                    !this.filterType.strAssetTypeCode3.equals("00")) {
                    sResult = sResult + " and a.Fzclb3 = " +
                        dbl.sqlString(this.filterType.strAssetTypeCode3 );
            }
            
            if (this.filterType.strYYBranchCode.length() != 0 &&
                    !this.filterType.strYYBranchCode.equals("99")) {
                    sResult = sResult + " and a.Fyyfh = " +
                        dbl.sqlString(this.filterType.strYYBranchCode );
            }
            if (this.filterType.strLYBranchCode.length() != 0 &&
                    !this.filterType.strLYBranchCode.equals("99")) {
                    sResult = sResult + " and a.Flyfh = " +
                        dbl.sqlString(this.filterType.strLYBranchCode );
            }
            
            if (this.filterType.strInvTypeCode1.length() != 0 &&
                    !this.filterType.strInvTypeCode1.equals("00")) {
                    sResult = sResult + " and a.FYYLB1  = " +
                        dbl.sqlString(this.filterType.strInvTypeCode1 );
            }
            if (this.filterType.strInvTypeCode2.length() != 0 &&
                    !this.filterType.strInvTypeCode2.equals("00")) {
                    sResult = sResult + " and a.FYYLB2 = " +
                        dbl.sqlString(this.filterType.strInvTypeCode2 );
            }
            if (this.filterType.strTZGLFSCode.length() != 0 &&
                    !this.filterType.strTZGLFSCode.equals("99")) {
                    sResult = sResult + " and a.Ftzglfs  = " +
                        dbl.sqlString(this.filterType.strTZGLFSCode );
            }
            
            if (this.filterType.strXSQDCode.length() != 0 &&
                    !this.filterType.strXSQDCode.equals("99")) {
                    sResult = sResult + " and a.Fxsqd = " +
                        dbl.sqlString(this.filterType.strXSQDCode );
            }
          
            
        }
        return sResult;

    }

    /**
     * getListViewData2
     *获取资产代码
     * @return String
     */
    public String getListViewData2() throws YssException {
       
        String strSql = "";
        ResultSet rs = null;       
        String strAssetCode = "";
        StringBuffer bufStr = new StringBuffer();
        try {
            
            strSql =
            	     " select d.FPortCode,trim(to_char(a.fsetcode,'000'))  as FBookSetCode ,d.FAssetCode ,d.FInceptionDate," +
            		 " d.FExpirationDate ,e1.FAffCorpName as FStrName,f1.FAffCorpName as Fglrname ," +
            		 " g.FFixRate ,j.FPerValue,j.FRangeDate from lsetlist a " //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//            		 + pub.yssGetTableName("Tb_Vch_PortSetLink")+ " a "   //组合套装链接设置
             		 +" left join ( select FPortCode , FAssetCode,FInceptionDate,FExpirationDate  from  " + pub.yssGetTableName("Tb_Para_Portfolio")   //组合设置
             		 +" ) d on a.fsetid = d.FAssetCode "              		 
             		 +" left join ( select FPortCode,FSubCode from "+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")  //资产组合关联信息表---受托人
             		 +" where FRelaType = 'Assignee' and FCheckState = 1 ) e on e.FPortCode = d.FPortCode "             		 
             		 +" left join ( select FAffCorpCode,FAffCorpName from "+ pub.yssGetTableName("Tb_Para_AffiliatedCorp")  //联系人设置表---受托人名称
             		 +" where  FCheckState = 1 ) e1 on e1.FAffCorpCode = e.FSubCode "
             		 
             		 +" left join ( select FPortCode,FSubCode from "+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")  //资产组合关联信息表---管理人pub.yssGetTableName("tb_para_manager")
            		 +" where FRelaType = 'Manager' and FCheckState = 1 ) f on f.FPortCode = d.FPortCode "             		 
            		 +" left join ( select FAffCorpCode,FAffCorpName from "+ pub.yssGetTableName("Tb_Para_AffiliatedCorp")  //联系人设置表---主托管人名称（管理人）
             		 +" where  FCheckState = 1 ) f1 on f1.FAffCorpCode = f.FSubCode "
            		 
            		 //费率取值时：优先查询“投资运营收支设置”中的固定比率；如固定比率值无效(为空值、空格、或为零)，则获取比率公式中的比率值。
            		 +" left join ( select FPortCode,FFixRate,FPerExpCode  from "+ pub.yssGetTableName("Tb_Para_InvestPay")  //投资运营收支设置表---固定比率
            		 +" where FIVPayCatCode = 'IV002'  and FCheckState = 1 and  FStartDate = (select max(FStartDate) from " +
                     pub.yssGetTableName("Tb_Para_InvestPay") + " where FIVPayCatCode = 'IV002' and FCheckState = 1 ) " 
                     + " ) g on g.FPortCode = d.FPortCode "  
             		 
            		 +" left join (select FFormulaCode, FFormulaName,FperType from " + pub.yssGetTableName("Tb_Para_Performula") //比率公式设置
                     +" where FCheckState = 1) i on g.FPerExpCode = i.FFormulaCode" 
                     +" left join ( select FFormulaCode,FPerType,FPerValue,FRangeDate from " +  pub.yssGetTableName("Tb_Para_Performula_Rela") 
                     +" where FCheckState = 1  ) j on i.FFormulaCode = j.FFormulaCode and i.FperType = j.FPerType "                     
            		 
             		 +" where trim(to_char(a.FSetCode,'000')) = '"+this.strSetCode+"' order by j.FRangeDate desc "; 
             		 
            rs = dbl.openResultSet(strSql);
           if (rs.next()) {
        	   if( rs.getString("FAssetCode") != null && rs.getString("FAssetCode").trim().length()>0 )
        	   {
        		   this.strAccsetCode = rs.getString("FAssetCode");
        	   }
        	   
        	   if(rs.getString("Fglrname") != null && rs.getString("Fglrname").trim().length()>0 )
        	   {
        		   this.strGLRName = rs.getString("Fglrname");
        	   }

        	   if(rs.getString("FStrName")!= null && rs.getString("FStrName").trim().length()>0 )
        	   {
        		   this.strSTRName = rs.getString("FStrName");
        	   }
        	   
        	   this.dblStartDate = rs.getDate("FInceptionDate")+"";
        	   this.dblEndDate = rs.getDate("FExpirationDate")+"";
        	   if(rs.getDouble("FFixRate") > 0)
        	   {
        		   this.strTGFL = ""+rs.getDouble("FFixRate");
        	   }
        	   else
        	   {
        		   /*strSql= " select FFormulaCode, FFormulaName,FperType from " + pub.yssGetTableName("Tb_Para_Performula") //比率公式设置
                   
                   +" a left join ( select FFormulaCode,FPerType,FPerValue,FRangeDate from " +  pub.yssGetTableName("Tb_Para_Performula_Rela") 
                   +" where FCheckState = 1  ) j on i.FFormulaCode = j.FFormulaCode and i.FperType = j.FPerType " 
                   +" where a.FCheckState = 1 and a.FFormulaCode = '"+rs.getString("FPerExpCode")+"'";*/
        		   
        		   this.strTGFL = ""+rs.getDouble("FPerValue");
        	   }
        	   
                 
            }
           strAssetCode = bufStr.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK).toString();
           
            return strAssetCode;
        } catch (Exception e) {
            throw new YssException("获取资产代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            
        }
       

    }

    /**
     * getListViewData3
     *
     * @return String
     */
    
   
    public String getListViewData3() throws YssException  {
    	
        String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet rSet = null;
        ResultSet rSet1 = null;
        String strUserCode = "";
        String sShowDataStr = "";
        //String sAllDataStr = "";
        
        StringBuffer bufShow = new StringBuffer();
        //StringBuffer bufAll = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
       
        try {
        	
            if("AssetType2".equalsIgnoreCase(this.strType)){
            	strSql = "select FVocCode,FVocName from  Tb_Fun_Vocabulary  a " +             			 
            			" where FVocTypeCode = 'CMB_AssetType_G2'  and FVocCode like '" + this.strAssetTypeCode1+"%' order by FVocCode";
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()) {
                     //bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                     
                     this.strAssetType2  = rs.getString("FVocName");
                     this.strAssetTypeCode2  = rs.getString("FVocCode"); 
                     bufShow.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                     //bufAll.append(this.buildRow()).append(YssCons.YSS_LINESPLITMARK);
                 }
            	
            }else if("AssetType3".equalsIgnoreCase(this.strType)){
            	strSql = "select FVocCode,FVocName from  Tb_Fun_Vocabulary  a " +             			 
    					" where FVocTypeCode = 'CMB_AssetType_G3'  and FVocCode like '" + this.strAssetTypeCode2+"%' order by FVocCode";
            	rSet = dbl.openResultSet(strSql);
            	while (rSet.next()) {
                    //bufShow.append(this.buildRowShowStr(rSet, this.getListView1ShowCols())).append(YssCons.YSS_LINESPLITMARK);
                    
                    this.strAssetType3  = rSet.getString("FVocName");
                    this.strAssetTypeCode3  = rSet.getString("FVocCode"); 
                    bufShow.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    //bufAll.append(this.buildRow()).append(YssCons.YSS_LINESPLITMARK);
                }
            	
            }else if("InvType2".equalsIgnoreCase(this.strType)){
            	strSql = "select FVocCode,FVocName from  Tb_Fun_Vocabulary  a " +             			 
    					" where FVocTypeCode = 'CMB_OperationType_G2'  and " +
    					"FVocCode like '" + this.strInvTypeCode1+"%' order by FVocCode ";
            	rSet1 = dbl.openResultSet(strSql);
            	while (rSet1.next()) {
                    //bufShow.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    
                    this.strInvType2  = rSet1.getString("FVocName");
                    this.strInvTypeCode2  = rSet1.getString("FVocCode"); 
                    bufShow.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
                    //bufAll.append(this.buildRow()).append(YssCons.YSS_LINESPLITMARK);
                }
            }
            
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }       
            return  sShowDataStr ;
            
        } catch (Exception e) {
            throw new YssException("获取统计参数信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rSet);
            dbl.closeResultSetFinal(rSet1);
           
        }
        
    }
   

    /**
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() throws YssException {
    	 String strSql = "";
         ResultSet rs = null;       
         String strAssetCode = "";
         StringBuffer bufStr = new StringBuffer();
         try {
             
             strSql =
             	     " select d.FPortCode,trim(to_char(a.fsetcode,'000'))  as FBookSetCode ,d.FAssetCode from lsetlist a" 
//             		 + pub.yssGetTableName("Tb_Vch_PortSetLink")+ " a "   //组合套装链接设置 //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
              		 +" left join ( select FPortCode , FAssetCode,FInceptionDate,FExpirationDate  from  " + pub.yssGetTableName("Tb_Para_Portfolio")   //组合设置
              		 +" ) d on a.FSetId = d.FAssetCode "  
             		 
              		 +" where a.FSetCode = to_number('"+this.strSetCode+"') "; 
              		 
             rs = dbl.openResultSet(strSql);
            if (rs.next()) {
         	   if( rs.getString("FAssetCode") != null && rs.getString("FAssetCode").trim().length()>0 )
         	   {
         		   this.strAccsetCode = rs.getString("FAssetCode");
         	   }
                  
             }
            strAssetCode = bufStr.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK).toString();
            
             return strAssetCode;
         } catch (Exception e) {
             throw new YssException("获取资产代码出错！", e);
         } finally {
             dbl.closeResultSetFinal(rs);
             
         }
        
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	 if (! checkSetCode(this.strSetCode))
             {
        		 strSql =
                     " insert into StatisticalParameters "  +
                     "(Fsetcode,Fyyfh,Ftzglr,Fwtr,Fstr,Flyfh,Fzclb1,Fzclb2,Fzclb3," +
                     "Ftzglfs,Fxsqd,Fxsdm,FYYLB1,FYYLB2," +
                     "FTGZH,Fstartdate,Fenddate,Ftgfl)" +
                     " values(" +this.strSetCode + "," +
                     dbl.sqlString(this.strYYBranchCode ) + "," +
                     dbl.sqlString(this.strGLRName  ) + "," +
                     dbl.sqlString(this.strWTRame ) + "," +
                     dbl.sqlString(this.strSTRName  ) + "," +
                     dbl.sqlString(this.strLYBranchCode ) + "," +
                     dbl.sqlString(this.strAssetTypeCode1  ) + "," +
                     dbl.sqlString(this.strAssetTypeCode2  ) + "," +
                     dbl.sqlString(this.strAssetTypeCode3  ) + "," +
                     this.strTZGLFSCode + "," +
                     this.strXSQDCode  + "," +
                     dbl.sqlString(this.strXSCode  ) + "," +
                     dbl.sqlString(this.strInvTypeCode1  ) + "," +
                     dbl.sqlString(this.strInvTypeCode2 ) + "," +
                     dbl.sqlString(this.strTGZH ) + "," +
                     
                     dbl.sqlDate(this.dblStartDate) + "," +
                     dbl.sqlDate(this.dblEndDate) + "," +
                     this.strTGFL + ")";

                 conn.setAutoCommit(false);
                 bTrans = true;
                 dbl.executeSql(strSql);
        	}
    	    else
    	    {
    	    	throw new YssException("已经存在套帐号为【"+this.strSetCode+"】的记录!"); 
    	    }
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("新增统计参数出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
    
    private boolean checkSetCode( String sSetCode) throws YssException
    {
    	String strSql = "";
    	ResultSet rs = null;
    	boolean bReturn = false;
    	try 
    	{
	        strSql = "select * from StatisticalParameters" + " where FSetCODE = " + this.strSetCode;
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				bReturn = true;
				break;
			}
	    	return bReturn;
    	}
    	
		catch(Exception e)
		{
			throw new YssException(e.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
    }

    /**
     * checkInput
     *
     * @param btOper byte
     
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, "StatisticalParameters",
                               "Fsetcode",
                               this.strSetCode);

    }
*/
   
    /*
    public void checkSetting() throws YssException {
       
        
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if ( (!sRecycled.equalsIgnoreCase("")) && sRecycled != null) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);

                    strSql = "update StatisticalParameters " +
                        " set FCheckState = " + this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" + " where FSetCode=" +
                        dbl.sqlString(this.strOldSetCode);
                       
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而strOldIndexCode不为空，则按照strOldIndexCode来执行sql语句
            else if ( (!strOldSetCode.equalsIgnoreCase("")) && strOldSetCode != null) {
                strSql = "update StatisticalParameters "  +
                    " set FCheckState = " + this.checkStateId +
                    ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) +
                    "'" + " where FSetCode=" +
                    dbl.sqlString(this.strOldSetCode) ;
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改统计参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }
*/
    /**
     * 将数据放入回收站
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
        	strSql = "delete from StatisticalParameters " +                       
            " where FSetCode=" + dbl.sqlString(this.strOldSetCode);
        	//执行sql语句
        	

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException("删除统计参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            strSql = "update StatisticalParameters " +
                " set Fsetcode = " + this.strSetCode +
                ",Fyyfh = " +   dbl.sqlString(this.strYYBranchCode) +
                
                ",Ftzglr=" +dbl.sqlString(this.strGLRName) +                
                ",Fwtr=" +dbl.sqlString(this.strWTRame) + 
                ",Fstr=" +dbl.sqlString(this.strSTRName)+
                
                ",Flyfh=" +dbl.sqlString(this.strLYBranchCode)+
                ",Fzclb1=" + dbl.sqlString(this.strAssetTypeCode1)+
                ",Fzclb2=" +dbl.sqlString(this.strAssetTypeCode2)+
                ",Fzclb3=" +dbl.sqlString(this.strAssetTypeCode3)+
                ",Ftzglfs=" + dbl.sqlString(this.strTZGLFSCode)+
                ",Fxsqd=" + dbl.sqlString(this.strXSQDCode)+
                ",Fxsdm=" + dbl.sqlString(this.strXSCode)+
                ",FYYLB1=" + dbl.sqlString(this.strInvTypeCode1)+
                ",FYYLB2 = "+ dbl.sqlString(this.strInvTypeCode2) +
                ",FTGZH = "+ dbl.sqlString(this.strTGZH) +
                
                ",Fstartdate=" +dbl.sqlDate(this.dblStartDate) +  
                ",Fenddate = "+dbl.sqlDate(this.dblEndDate) +                
                ",Ftgfl = "+ this.strTGFL +                
                " where Fsetcode=" + dbl.sqlString(this.strOldSetCode);
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改统计参数信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

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
        buf.append(this.strSetCode ).append("\t");
        buf.append(this.strSetName ).append("\t");
        buf.append(this.strAccsetCode ).append("\t");
        
        buf.append(this.strGLRName ).append("\t");
        buf.append(this.strWTRame ).append("\t");
        buf.append(this.strSTRName ).append("\t");
        
        buf.append(this.strYYBranch ).append("\t");
        buf.append(this.strLYBranch ).append("\t");
        
        buf.append(this.strAssetType1 ).append("\t");
        buf.append(this.strAssetType2).append("\t");
        buf.append(this.strAssetType3).append("\t");
        buf.append(this.strInvType1).append("\t");
        buf.append(this.strInvType2).append("\t");
        
        buf.append(this.strTZGLFS).append("\t");
        buf.append(this.strXSQD).append("\t");
        buf.append(this.strXSCode).append("\t");
        buf.append(this.strTGZH).append("\t");
        buf.append(this.strTGFL).append("\t");
        
        buf.append(this.dblStartDate ).append("\t");
        buf.append(this.dblEndDate ).append("\t");
        
        buf.append(this.strAssetTypeCode1 ).append("\t");
        buf.append(this.strAssetTypeCode2).append("\t");
        buf.append(this.strAssetTypeCode3).append("\t");
        buf.append(this.strInvTypeCode1).append("\t");
        buf.append(this.strInvTypeCode2).append("\t");
        
        buf.append(this.strYYBranchCode).append("\t");
        buf.append(this.strLYBranchCode).append("\t");
        buf.append(this.strTZGLFSCode).append("\t");
        buf.append(this.strXSQDCode).append("\t");
        
        //buf.append(super.buildRecLog());
        return buf.toString();

    }

    /*
    public String buildRow() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.strGetKey.trim() ).append("\t"); 
        buf.append(this.strGetValue.trim() ).append("\t");
        
        //buf.append(this.strType.trim()).append("\t");  
        
        buf.append(super.buildRecLog());
        return buf.toString();

    }
    */
    
    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            //20130110 added by liubo.Story #2839
            //<Logging>标签之前的数据为正常的传入数据，标签之后的数据为此次修改的数据变更内容
            //变更数据内容将被传入基类的sLoggingPositionData变量中，生成日志数据时插入FLogData4字段，表示本次修改内容
            //=====================================
            if (sRowStr.split("<Logging>").length >= 2)
            {
            	this.sLoggingPositionData = sRowStr.split("<Logging>")[1];
            }
            sRowStr = sRowStr.split("<Logging>")[0];
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.strSetCode  = reqAry[0];
            this.strSetName  = reqAry[1];
            this.strAccsetCode  = reqAry[2];
            
            this.strGLRName  = reqAry[3];
            this.strWTRame  = reqAry[4];
            this.strSTRName  = reqAry[5];
            
            this.strYYBranch  = reqAry[6];
            this.strLYBranch  = reqAry[7]; 
            
            this.strAssetType1  = reqAry[8];
            this.strAssetType2  = reqAry[9];            
            this.strAssetType3 = reqAry[10];            
            this.strInvType1 = reqAry[11];            
            this.strInvType2 = reqAry[12];
            
            this.strTZGLFS  = reqAry[13];
            this.strXSQD  = reqAry[14];    
            this.strXSCode   = reqAry[15];       
            this.strTGZH   = reqAry[16];       
            if (YssFun.isNumeric(reqAry[17])) {
                this.strTGFL  = reqAry[17];
            }
            
            this.dblStartDate = reqAry[18];
            this.dblEndDate = reqAry[19];
            
            
            //this.checkStateId = Integer.parseInt(reqAry[20]);
            this.strOldSetCode = reqAry[20];
            
            this.isOnlyColumns = reqAry[21];
            
            this.strInvTypeCode1 = reqAry[22];
            this.strInvTypeCode2 = reqAry[23];
            this.strAssetTypeCode1 = reqAry[24];
            this.strAssetTypeCode2 = reqAry[25];
            this.strAssetTypeCode3 = reqAry[26];
            
            this.strType = reqAry[27];
            
            this.strYYBranchCode = reqAry[28];
            this.strLYBranchCode = reqAry[29];
            this.strTZGLFSCode = reqAry[30];
            this.strXSQDCode = reqAry[31];
            
            //super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CMBParaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析统计参数信息出错", e);
        }

    }

    /*
    public void parseRow(String sRowStr) throws YssException {
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
            sRecycled = sRowStr; //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            
            
            this.strGetKey  = reqAry[0];
            this.strGetValue  = reqAry[1]; 
            this.strType = reqAry[2]; 
            
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CMBParaBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }

        } catch (Exception e) {
            throw new YssException("解析统计参数信息出错", e);
        }

    }
    */

    
 

    /**
     * 删除回收站的数据，即从数据库彻底删除数据
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != "" && sRecycled != null) {
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "delete from StatisticalParameters " +                       
                        " where FSetCode=" + dbl.sqlString(this.strOldSetCode);
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            else if (strOldSetCode != "" && strOldSetCode != null) {
                strSql = "delete from StatisticalParameters " +
                    
                " where FSetCode=" + dbl.sqlString(this.strOldSetCode);
                //执行sql语句
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        }

        catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
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

	public String getStrSetCode() {
		return strSetCode;
	}

	public void setStrSetCode(String strSetCode) {
		this.strSetCode = strSetCode;
	}

	public String getStrSetName() {
		return strSetName;
	}

	public void setStrSetName(String strSetName) {
		this.strSetName = strSetName;
	}

	public String getStrAccsetCode() {
		return strAccsetCode;
	}

	public void setStrAccsetCode(String strAccsetCode) {
		this.strAccsetCode = strAccsetCode;
	}

	public String getStrGLRName() {
		return strGLRName;
	}

	public void setStrGLRName(String strGLRName) {
		this.strGLRName = strGLRName;
	}

	public String getStrWTRame() {
		return strWTRame;
	}

	public void setStrWTRame(String strWTRame) {
		this.strWTRame = strWTRame;
	}

	public String getStrSTRName() {
		return strSTRName;
	}

	public void setStrSTRName(String strSTRName) {
		this.strSTRName = strSTRName;
	}

	public String getStrYYBranch() {
		return strYYBranch;
	}

	public void setStrYYBranch(String strYYBranch) {
		this.strYYBranch = strYYBranch;
	}

	public String getStrLYBranch() {
		return strLYBranch;
	}

	public void setStrLYBranch(String strLYBranch) {
		this.strLYBranch = strLYBranch;
	}

	public String getStrAssetType1() {
		return strAssetType1;
	}

	public void setStrAssetType1(String strAssetType1) {
		this.strAssetType1 = strAssetType1;
	}

	public String getStrAssetType2() {
		return strAssetType2;
	}

	public void setStrAssetType2(String strAssetType2) {
		this.strAssetType2 = strAssetType2;
	}

	public String getStrAssetType3() {
		return strAssetType3;
	}

	public void setStrAssetType3(String strAssetType3) {
		this.strAssetType3 = strAssetType3;
	}

	public String getStrInvType1() {
		return strInvType1;
	}

	public void setStrInvType1(String strInvType1) {
		this.strInvType1 = strInvType1;
	}

	public String getStrInvType2() {
		return strInvType2;
	}

	public void setStrInvType2(String strInvType2) {
		this.strInvType2 = strInvType2;
	}

	public String getStrTZGLFS() {
		return strTZGLFS;
	}

	public void setStrTZGLFS(String strTZGLFS) {
		this.strTZGLFS = strTZGLFS;
	}

	public String getStrXSQD() {
		return strXSQD;
	}

	public void setStrXSQD(String strXSQD) {
		this.strXSQD = strXSQD;
	}

	public String getStrXSCode() {
		return strXSCode;
	}

	public void setStrXSCode(String strXSCode) {
		this.strXSCode = strXSCode;
	}

	public String getStrTGZH() {
		return strTGZH;
	}

	public void setStrTGZH(String strTGZH) {
		this.strTGZH = strTGZH;
	}

	public String getStrTGFL() {
		return strTGFL;
	}

	public void setStrTGFL(String strTGFL) {
		this.strTGFL = strTGFL;
	}

	public String getDblStartDate() {
		return dblStartDate;
	}

	public void setDblStartDate(String dblStartDate) {
		this.dblStartDate = dblStartDate;
	}

	public String getDblEndDate() {
		return dblEndDate;
	}

	public void setDblEndDate(String dblEndDate) {
		this.dblEndDate = dblEndDate;
	}

	public String getStrAssetTypeCode1() {
		return strAssetTypeCode1;
	}

	public void setStrAssetTypeCode1(String strAssetTypeCode1) {
		this.strAssetTypeCode1 = strAssetTypeCode1;
	}

	public String getStrAssetTypeCode2() {
		return strAssetTypeCode2;
	}

	public void setStrAssetTypeCode2(String strAssetTypeCode2) {
		this.strAssetTypeCode2 = strAssetTypeCode2;
	}

	public String getStrAssetTypeCode3() {
		return strAssetTypeCode3;
	}

	public void setStrAssetTypeCode3(String strAssetTypeCode3) {
		this.strAssetTypeCode3 = strAssetTypeCode3;
	}

	public String getStrInvTypeCode1() {
		return strInvTypeCode1;
	}

	public void setStrInvTypeCode1(String strInvTypeCode1) {
		this.strInvTypeCode1 = strInvTypeCode1;
	}

	public String getStrInvTypeCode2() {
		return strInvTypeCode2;
	}

	public void setStrInvTypeCode2(String strInvTypeCode2) {
		this.strInvTypeCode2 = strInvTypeCode2;
	}

	public String getStrType() {
		return strType;
	}
	

	public String getStrYYBranchCode() {
		return strYYBranchCode;
	}

	public void setStrYYBranchCode(String strYYBranchCode) {
		this.strYYBranchCode = strYYBranchCode;
	}

	public String getStrLYBranchCode() {
		return strLYBranchCode;
	}

	public void setStrLYBranchCode(String strLYBranchCode) {
		this.strLYBranchCode = strLYBranchCode;
	}

	public String getStrTZGLFSCode() {
		return strTZGLFSCode;
	}

	public void setStrTZGLFSCode(String strTZGLFSCode) {
		this.strTZGLFSCode = strTZGLFSCode;
	}

	public String getStrXSQDCode() {
		return strXSQDCode;
	}

	public void setStrXSQDCode(String strXSQDCode) {
		this.strXSQDCode = strXSQDCode;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public void checkInput(byte btOper) throws YssException {
		// TODO Auto-generated method stub
		
	}

	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub
		
	}

	

	
	
    
    
}
