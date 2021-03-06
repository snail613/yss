package com.yss.main.report;
/**
 * 报表管理模块：外汇使用汇总表
 * add by guolongchao 20110822 STORY #1203 外汇使用汇总表
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.yss.dbupdate.BaseDbUpdate;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class WhsyBean extends BaseDbUpdate implements IDataSetting{
    
	private String startDate;              //起始日期
	private String endDate;                //截止日期
	private String groupCodes;             //组合群集合
	private String groupNames;             //组合群名称集合
	private String jwCashAccCodes;         //境外账户集合
	private String jwCashAccNames;         //境外账户集合名称
	private String purAmount;              //申购额度
	private String purAmountAdjusted;      //申购额度调整值
	private String usePurAmountAdjusted;   //已使用申购额度调整值
	private String drawStart;              //划出初始值
	private String includedStart;          //划入初始值
	private String jhStart;                //结汇初始值
	private String ghStart;                //购汇初始值
	
	/**
	 * 创建临时表TEMP_DATA_WHSY（主要用于存放本报表所需的13个参数）
	 * @throws YssException 
	 */
	private void createMyTable() throws YssException
	{

		StringBuffer sb=new StringBuffer();		   
		sb.append(" create table TEMP_DATA_WHSY (");
		sb.append("  STARTDATE            VARCHAR2(20),");    //起始日期
		sb.append("  ENDDATE              VARCHAR2(20),");    //截止日期
		sb.append("  GROUPCODES           VARCHAR2(4000),");  //组合群集合
		sb.append("  GROUPNAMES           VARCHAR2(4000),");  //组合群名称集合
		sb.append("  JWCASHACCCODES       VARCHAR2(4000),");  //境外账户集合
		sb.append("  JWCASHACCNAMES       VARCHAR2(4000),");  //境外账户集合名称
		sb.append("  PURAMOUNT            VARCHAR2(20),");    //申购额度
		sb.append("  PURAMOUNTADJUSTED    VARCHAR2(20),");    //申购额度调整值
		sb.append("  USEPURAMOUNTADJUSTED VARCHAR2(20),");    //已使用申购额度调整值
		sb.append("  DRAWSTART            VARCHAR2(20),");    //划出初始值
		sb.append("  INCLUDEDSTART        VARCHAR2(20),");    //划入初始值
		sb.append("  JHSTART              VARCHAR2(20),");    //结汇初始值
		sb.append("  GHSTART              VARCHAR2(20) ");    //购汇初始值
		sb.append(" )");		
		try 
		{
			if(dbl.yssTableExist("TEMP_DATA_WHSY")==false)//若TEMP_DATA_WHSY不存在，则创建它
			       this.dbl.executeSql(sb.toString());
		} catch (SQLException e) {			
			 throw new YssException(e.getMessage(), e);
		} catch (YssException e) {
			throw new YssException(e.getMessage(), e);
		}
	}

	public String addSetting() throws YssException {
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
		// TODO Auto-generated method stub
		return null;
	}
	public String getBeforeEditData() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public String buildRowStr() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}
	

	public void parseRowStr(String sRowStr) throws YssException {
		// TODO Auto-generated method stub
		if(sRowStr==null||sRowStr.length()==0)
			return;
		String[] temp=sRowStr.split("\t");
		if(temp.length>0)
		{
			this.startDate=temp[0];
			this.endDate=temp[1];                 
			this.groupCodes=temp[2];
			this.groupNames=temp[3];             //组合群名称集合
			this.jwCashAccCodes=temp[4];         //境外账户集合
			this.jwCashAccNames=temp[5];         //境外账户集合名称
			this.purAmount=temp[6];              //申购额度
			this.purAmountAdjusted=temp[7];      //申购额度调整值
			this.usePurAmountAdjusted=temp[8];   //已使用申购额度调整值
			this.drawStart=temp[9];              //划出初始值
			this.includedStart=temp[10];          //划入初始值
			this.jhStart=temp[11];                //结汇初始值
			this.ghStart=temp[12];                //购汇初始值
		}
	}
	
	//月份统计数据
	public String getListViewData1() throws YssException {
		// TODO Auto-generated method stub
		StringBuffer sb=new StringBuffer();
		StringBuffer sbRes=new StringBuffer();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs=null;		
		String str1=null;
		str1=getPrefix(true);
		if(str1!=null&&str1.length()>0)
		{
			String[] groups=str1.split("\t");
			if(groups!=null&&groups.length>0)
			{
				for(int i=0;i<groups.length;i++)
				{
					String jwzh=getJWZH(groups[i].trim());//组合群下的所有境外账户集合
					if(!jwzh.equals("")&&jwzh.length()>0)
					{
						//划出统计(从境内账户划到境外账户)	
						sb.append(" select ftabprefix,to_char(fdate,'yyyy-mm') as fdate,ftype,sum(fje) as fje from(");
						sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,a.ftradedate as fdate,'划出' as ftype,");
						//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
						   //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
						   //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
						sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
						sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
						sb.append(" where  a.FSCuryCode=a.FBCuryCode and a.fscashacccode not in("+jwzh+") and a.fbcashacccode in("+jwzh+")");
						//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
						   //sb.append("  and b.fmarkcury='USD' and b.fcheckstate=1");
						sb.append("  and a.ftradedate between to_date('"+this.startDate+"','yyyy-mm-dd') and to_date('"+this.endDate+"','yyyy-mm-dd') and a.fcheckstate=1");
						sb.append("   )group by ftabprefix,ftype,to_char(fdate,'yyyy-mm')");
						sb.append(" union all");
						//划入统计(从境外账户划到境内账户)	
						sb.append(" select ftabprefix,to_char(fdate,'yyyy-mm') as fdate,ftype,sum(fje) as fje from(");
						sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,a.ftradedate as fdate,'划入' as ftype,");
						//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
						   //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
						   //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
						sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
						sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
						sb.append(" where  a.FSCuryCode=a.FBCuryCode and a.fbcashacccode not in("+jwzh+") and a.fscashacccode in("+jwzh+")");
						   //sb.append("  and b.fmarkcury='USD' and b.fcheckstate=1");
						sb.append("  and a.ftradedate between to_date('"+this.startDate+"','yyyy-mm-dd') and to_date('"+this.endDate+"','yyyy-mm-dd') and a.fcheckstate=1");
						sb.append("   )group by ftabprefix,ftype,to_char(fdate,'yyyy-mm')");
						sb.append(" union all");
					}
				    //购汇统计（人民币购买外币）	
					sb.append(" select ftabprefix,to_char(fdate,'yyyy-mm') as fdate,ftype,sum(fje) as fje from(");
					sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,a.ftradedate as fdate,'购汇' as ftype,");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					   //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
					   //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
					sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
					sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
					sb.append(" where   a.FSCuryCode='CNY' and a.FBCuryCode!='CNY' ");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					   //sb.append(" and b.fmarkcury='USD' and b.fcheckstate=1");
					sb.append(" and a.ftradedate between to_date('"+this.startDate+"','yyyy-mm-dd') and to_date('"+this.endDate+"','yyyy-mm-dd') and a.fcheckstate=1");
					sb.append("  )group by ftabprefix,ftype,to_char(fdate,'yyyy-mm')");	
					sb.append(" union all");
					//结汇统计（外币购买人民币）	
					sb.append(" select ftabprefix,to_char(fdate,'yyyy-mm') as fdate,ftype,sum(fje) as fje from(");
					sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix, a.ftradedate as fdate,'结汇' as ftype,");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					//sb.append(" sum(a.FSMoney* case when a.FSCuryCode!='USD' then b.fexrate1 else 1 end) as fje");
					     //sb.append(" a.FSMoney*(case when (a.ftradedate=b.fexratedate and a.FSCuryCode=b.fcurycode and a.FSCuryCode<>'USD') then b.fexrate1  when a.FSCuryCode='USD' then 1 end) as fje");
					     //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
					sb.append(" a.FSMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FSCuryCode=b.fcurycode and a.FSCuryCode<>'USD') then b.fexrate  when a.FSCuryCode='USD' then 1 end) as fje");
					sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
					sb.append(" where a.FSCuryCode!='CNY' and a.FBCuryCode='CNY'");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					     //sb.append(" and b.fmarkcury='USD' and b.fcheckstate=1");
					sb.append(" and a.ftradedate between to_date('"+this.startDate+"','yyyy-mm-dd') and to_date('"+this.endDate+"','yyyy-mm-dd') and a.fcheckstate=1");
					sb.append("  )group by ftabprefix,ftype,to_char(fdate,'yyyy-mm')");
					if(i<groups.length-1)
					   sb.append(" union all");
				}
			}
			try {
				   conn=this.dbl.loadConnection();
				   stat = conn.createStatement();
				   rs = stat.executeQuery(sb.toString());
				   while(rs.next())
				   {
					   sbRes.append(rs.getString("ftabprefix")+rs.getString("ftype")+rs.getString("fdate")+"\r");					  			   
					   sbRes.append(YssFun.formatNumber(rs.getDouble("fje"), "#,##0.00")+"\t");					  
				   }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new YssException(e.getMessage(), e);
			}
			finally
			{
				try {
						if(rs!=null) rs.close();
						if(stat!=null) stat.close();
						if(conn!=null) conn.close();
					} catch (Exception e) {
						throw new YssException(e.getMessage(), e);
					}
					
			}
		}		
		if(sbRes.length()>0)
		     return sbRes.substring(0,sbRes.length()-1);		
		else
			return "";
	}
	
	
	//true：获取选中的组合群集合对应的表前缀
	//false: 获取系统中所有组合群对应的表前缀
	private String getPrefix(boolean flag) throws YssException
	{
		StringBuffer sb=new StringBuffer();
		ResultSet rs=null;		
		String strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1";
		if(flag==true)
			strSql+= " and FAssetGroupCode in("+groupCodes+") order by FAssetGroupCode ";
		else
			strSql+= " order by FAssetGroupCode ";
				       
		try {
			   rs = dbl.openResultSet(strSql);
			   while(rs.next())
			   {
				   sb.append(rs.getString("FTabPreFix")+"\t");
			   }
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
				
		}
		return sb.substring(0,sb.length()-1);
	}
	
	//所有组合群成立至今的合计数据
	public String getListViewData2() throws YssException {
		StringBuffer sb=new StringBuffer();
		StringBuffer sbRes=new StringBuffer();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs=null;		
		String str1=null;
		str1=getPrefix(false);
		if(str1!=null&&str1.length()>0)
		{
			String[] groups=str1.split("\t");
			if(groups!=null&&groups.length>0)
			{
				sb.append("select ftype,sum(fje) as fje from (");
				for(int i=0;i<groups.length;i++)
				{
					String jwzh=getJWZH(groups[i].trim());
					if(!jwzh.equals("")&&jwzh.length()>0)
					{
						//成立至今划出统计(从境内账户划到境外账户)
						sb.append(" select ftabprefix,ftype,sum(fje) as fje from(");
						sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,'划出' as ftype,");	
                        //update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率						
						    //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
						    //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
						sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
						sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
						sb.append(" where  a.FSCuryCode=a.FBCuryCode and a.fscashacccode not in("+jwzh+") and a.fbcashacccode in("+jwzh+")");
                         //update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率						
						   //sb.append("  and b.fmarkcury='USD' and b.fcheckstate=1");
						sb.append("  and a.fcheckstate=1");
						sb.append("  )group by ftabprefix,ftype");
						sb.append(" union all");
						//成立至今划入统计(从境外账户划到境内账户)
						sb.append(" select ftabprefix,ftype,sum(fje) as fje  from(");
						sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,'划入' as ftype,");
                        //update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率						
						    //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
						    //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
						sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
						sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
						sb.append(" where  a.FSCuryCode=a.FBCuryCode and a.fbcashacccode not in("+jwzh+") and a.fscashacccode in("+jwzh+")");						
						   //sb.append("  and b.fmarkcury='USD' and b.fcheckstate=1");
						sb.append("  and a.fcheckstate=1");
						sb.append("  )group by ftabprefix,ftype");
						sb.append(" union all");
					}					
				    //成立至今购汇统计（人民币购买外币）
					sb.append(" select ftabprefix,ftype,sum(fje) as fje  from(");
					sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,'购汇' as ftype,");	
                     //update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率					
					    //sb.append(" a.FBMoney*(case when (a.ftradedate=b.fexratedate and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate1  when a.fbcurycode='USD' then 1 end) as fje");
					    //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
					sb.append(" a.FBMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FBCuryCode=b.fcurycode and a.fbcurycode<>'USD') then b.fexrate  when a.fbcurycode='USD' then 1 end) as fje");
					sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
					sb.append(" where   a.FSCuryCode='CNY' and a.FBCuryCode!='CNY'");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					    //sb.append(" and b.fmarkcury='USD' and b.fcheckstate=1");
					sb.append(" and a.fcheckstate=1");		
					sb.append("  )group by ftabprefix,ftype");
					sb.append(" union all");
					//成立至今结汇统计（外币购买人民币）
					sb.append(" select ftabprefix,ftype,sum(fje) as fje  from(");
					sb.append(" select distinct fnum,'"+groups[i]+"' as ftabprefix,'结汇' as ftype,");		
                     //update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率					
					     //sb.append(" a.FSMoney*(case when (a.ftradedate=b.fexratedate and a.FSCuryCode=b.fcurycode and a.FSCuryCode<>'USD') then b.fexrate1  when a.FSCuryCode='USD' then 1 end) as fje");
					     //sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,tb_"+groups[i]+"_data_exchangerate b");
					sb.append(" a.FSMoney*(case when (to_char(a.ftradedate,'yyyy-mm')=to_char(b.fdate,'yyyy-mm') and a.FSCuryCode=b.fcurycode and a.FSCuryCode<>'USD') then b.fexrate  when a.FSCuryCode='USD' then 1 end) as fje");
					sb.append(" from  Tb_"+groups[i]+"_Data_RateTrade a,TMP_ForeignExchange b");
					sb.append(" where a.FSCuryCode!='CNY' and a.FBCuryCode='CNY' ");
					//update by guolongchao 20111228 bug 2828 修改外汇使用汇总表汇率的来源，改为从TMP_ForeignExchange取汇率
					    //sb.append("  and b.fmarkcury='USD' and b.fcheckstate=1");
					sb.append("  and a.fcheckstate=1");	
					sb.append("  )group by ftabprefix,ftype");
					if(i<groups.length-1)
					   sb.append(" union all");					
				}
				sb.append(") group by ftype");
			}			
			try {
				   conn=this.dbl.loadConnection();
				   stat = conn.createStatement();
				   rs = stat.executeQuery(sb.toString());
				   while(rs.next())
				   {				  
					   sbRes.append(rs.getString("ftype")+"\r");
					   sbRes.append(YssFun.formatNumber(rs.getDouble("fje"), "#,##0.00")+"\t");
				   }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new YssException(e.getMessage(), e);
			}
			finally
			{
				try {
						if(rs!=null) rs.close();
						if(stat!=null) stat.close();
						if(conn!=null) conn.close();
					} catch (Exception e) {
						throw new YssException(e.getMessage(), e);
					}
					
			}
		}
		if(sbRes.length()>0)
		    return sbRes.substring(0,sbRes.length()-1);
		else
			return "";
	}
	
	
	//获取临时表中保存的默认值
	public String getListViewData3() throws YssException {
		StringBuffer sb=new StringBuffer();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs=null;		
		String strSql = "select * from temp_data_whsy";	       
		try {
			  if(dbl.yssTableExist("TEMP_DATA_WHSY")==false)			
				  return "";			  
			   conn=this.dbl.loadConnection();
			   stat = conn.createStatement();
			   rs = stat.executeQuery(strSql);
			   if(rs.next())
			   {
				   sb.append(rs.getString("startDate")+"\t");  //起始日期
				   sb.append(rs.getString("endDate")+"\t");  //截止日期
				   sb.append(rs.getString("groupCodes")+"\t");  //组合群集合
				   sb.append(rs.getString("groupNames")+"\t");  //组合群名称集合
				   sb.append(rs.getString("jwCashAccCodes")+"\t");  //境外账户集合
				   sb.append(rs.getString("jwCashAccNames")+"\t");   //境外账户集合名称
				   sb.append(rs.getString("purAmount")+"\t");   //申购额度
				   sb.append(rs.getString("purAmountAdjusted")+"\t");   //申购额度调整值
				   sb.append(rs.getString("usePurAmountAdjusted")+"\t");   //已使用申购额度调整值
				   sb.append(rs.getString("drawStart")+"\t");  //划出初始值
				   sb.append(rs.getString("includedStart")+"\t");  //划入初始值
				   sb.append(rs.getString("jhStart")+"\t"); //结汇初始值
				   sb.append(rs.getString("ghStart"));  //购汇初始值           
			   }
		} catch (Exception e) {
			throw new YssException(e.getMessage(), e);
		}
		finally
		{
			try {
					if(rs!=null) rs.close();
					if(stat!=null) stat.close();
					if(conn!=null) conn.close();
				} catch (Exception e) {
					throw new YssException(e.getMessage(), e);
				}
				
		}
		return sb.toString();
	}
	

	/**
	 * 获取所有的境外现金账户集合
	 * groupCode: 组合群表前缀   如：001
	 */
	public String getJWZH(String groupCode) throws YssException {
		// TODO Auto-generated method stub
		StringBuffer sb=new StringBuffer();
		String str=null;
		Connection conn = null;
		Statement stat = null;
		ResultSet rs=null;		
		String strSql = "select jwCashAccCodes from temp_data_whsy";	       
		try {
			   conn=this.dbl.loadConnection();
			   stat = conn.createStatement();
			   rs = stat.executeQuery(strSql);
			   if(rs.next())
			   {
				   str=rs.getString("jwCashAccCodes"); //获取出的境外账户示例：001-472测试,001-020212,001-020231,001-020131,002-020217,002-020117,003-020232,004-020132,005-020208,008-020108
			   }
			   else//add by guolongchao 20120131 BUG3589工银外汇使用表点击查询报错 ------------start
			   {
				   throw new YssException("请先保存默认值");
			   }
			 //add by guolongchao 20120131 BUG3589工银外汇使用表点击查询报错 ----------------end
		} catch (Exception e) {			
			//update by guolongchao 20120131 BUG3589工银外汇使用表点击查询报错 ----------------start
			if(e.getMessage().indexOf("表或视图不存在")>=0)
				throw new YssException("请先保存默认值");
			else
			    throw new YssException(e.getMessage(), e);
			//update by guolongchao 20120131 BUG3589工银外汇使用表点击查询报错 ----------------end
		}
		finally
		{
			try {
					if(rs!=null) rs.close();
					if(stat!=null) stat.close();
					if(conn!=null) conn.close();
				} catch (Exception e) {
					throw new YssException(e.getMessage(), e);
				}
				
		}
		//将str进行拼接组成sql语句的in子句
		if(groupCode!=null&&groupCode.length()>0)
		{
			String[] temp=str.split(",");
			if(temp!=null)
			{
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i].startsWith(groupCode))//判断境外账户示例是否以组合群（如：001）开头
					{
						sb.append("'"+temp[i].substring(temp[i].indexOf("-")+1)+"',");
					}
				}
			}
		}
		//return sb.delete(sb.lastIndexOf(","), sb.length()-1).toString();
		if(sb.length()>0)
		    return sb.substring(0, sb.length()-1);
		else
			return "";
	}
	
	//保存默认值
	public String getListViewGroupData1() throws YssException {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stat = null;	
		String res="";
		String strSql1="delete from temp_data_whsy";
		String strSql2="insert into temp_data_whsy(startDate,endDate,groupCodes,groupNames,jwCashAccCodes,jwCashAccNames,purAmount,purAmountAdjusted,usePurAmountAdjusted,drawStart,includedStart,jhStart,ghStart) values('"
			           +startDate+"','"+endDate+"','"+groupCodes+"','"+groupNames+"','"+jwCashAccCodes+"','"+jwCashAccNames+"','"
			           +purAmount+"','"+purAmountAdjusted+"','"+usePurAmountAdjusted+"','"+drawStart+"','"
			           +includedStart+"','"+jhStart+"','"+ghStart+"')";
		try {
			   createMyTable();
			   conn=this.dbl.loadConnection();
			   stat = conn.createStatement();
			   stat.executeUpdate(strSql1);
			   stat.executeUpdate(strSql2);
			   res="默认值保存成功！";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new YssException(e.getMessage(), e);
		}
		finally{
			try {					
					if(stat!=null) stat.close();
					if(conn!=null) conn.close();
				} catch (Exception e) {
					throw new YssException(e.getMessage(), e);
				}
				
		}
		return res;
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
	
	//获取所有组合群下的现金账户（所有已审核非人民币账户）
	public String getTreeViewData1() throws YssException {
		    String strSql = "";
	        String sResult = "";
	        ResultSet rs = null;
	        ResultSet prs = null;
	        StringBuffer bufAll = new StringBuffer();
	        int GroupOrderCode = 0;
	        int CashOrderCode = 0;
	        String parentCode = "";
	        String NodeCode = "";
	        String NodeName = "";
	        String NodeOrderCode = "";
	        try {
	            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
	            rs = dbl.openResultSet(strSql);
	            while (rs.next()) 
	            {
	                NodeCode = rs.getString("FAssetGroupCode");
	                NodeName = rs.getString("FAssetGroupName");
	                NodeOrderCode = YssFun.formatNumber(GroupOrderCode++, "000");
	                parentCode = "[root]";
	                bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
	                bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
	                bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
	                bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);	               
	                bufAll.append(" ").append(YssCons.YSS_LINESPLITMARK);            
	                if (dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_CashAccount")) 
	                {
	                	strSql=" select a.fcashacccode,a.fcashaccname from Tb_"+ rs.getString("FTabPreFix") +"_Para_CashAccount a where a.fcheckstate=1 and a.fcurycode!='CNY'";
	                    prs = dbl.openResultSet(strSql);
	                    while (prs.next()) {
	                        NodeCode = rs.getString("FAssetGroupCode") + "-" + prs.getString("fcashacccode");
	                        NodeName = prs.getString("fcashaccname");
	                        NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1, "000") 
	                                        + YssFun.formatNumber(CashOrderCode++, "000");
	                        parentCode = rs.getString("FAssetGroupCode");
	                        bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
	                        bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
	                        bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
	                        bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
	                        bufAll.append(prs.getString("fcashaccname")).append(YssCons.YSS_LINESPLITMARK);
	                    }
	                    dbl.closeResultSetFinal(prs); 
	                }
	            } 
	            sResult = bufAll.toString();
	            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
	                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
	            }
	            //System.out.println(sResult);
	            return sResult;
	        } catch (Exception ex) {
	            throw new YssException("获取全部组合群全部组合信息出错", ex);
	        } finally {
	            bufAll = null;
	            dbl.closeResultSetFinal(prs, rs);
	        }
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
	public String getListViewData4() throws YssException {
		// TODO Auto-generated method stub
		return null;
	}	
}
