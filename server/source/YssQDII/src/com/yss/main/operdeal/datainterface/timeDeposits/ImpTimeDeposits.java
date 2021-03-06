package com.yss.main.operdeal.datainterface.timeDeposits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.db2.jcc.a.db;
import com.yss.main.cashmanage.SavingBean;
import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.manager.CashTransAdmin;
import com.yss.util.YssException;
import com.yss.util.YssFun;


/*********************************************************** 
 * @author jiangshichao 2010.09.07
 * MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据
 */
public class ImpTimeDeposits extends DataBase {
    
	boolean Updateflag = false;//用于判断系统版本，合并版本为true,原始版本则为false
	SavingBean DepositsInBean = null;
	
	public ImpTimeDeposits(){
		
	}
	
	
	/**
     * 入口方法
     * @throws YssException
     */
    public void inertData() throws YssException {

    	//1. 判断系统是否为太平资产合并版本
    	checkVersion();
    	//2. 删除数据
    	delData();
    	//3. 导入数据
    	impData();
    	//4. 如果勾选了审核状态，则将导入到数据进行更新
    	if(this.checkState.equalsIgnoreCase("true")){
    		checkDate();
    	}
    	
    }

    /****************************************************
     * 由于定存处理方式太平资产原始版本与合并版本有差异，
     * 所以要根据不同的版本进行不同处理
     * 所以这里通过比较表结构来判断是否为合并版本。
     * @throws YssException
     */
    private void checkVersion()throws YssException{
    	ResultSet rs = null;
    	try{
    		rs = dbl.openResultSet("select * from "+pub.yssGetTableName("Tb_Cash_SavingInAcc")+" where 1=2");
    		Updateflag = dbl.isFieldExist(rs, "FTRADETYPE");
    		
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：核对表出错！！！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    
    /**************************************************
     * 根据导入的数据来查询库中是否已存在相应的数据
     * 原始版本 （删除存款业务数据和资金调拨数据）
     * 合并版本  （删除存款业务数据）
     * @throws YssException
     */
    private void delData() throws YssException{
    	
    	boolean bTrans = false;
    	Connection conn = dbl.loadConnection();
    	String strSql = "";
        String str = "",str1= "";
    	try{
    		//
    		if(YssFun.dateDiff(sDate,dealDate )>0){
    			return ;
    		}
    		 conn.setAutoCommit(false);
             bTrans = true;
    		//~~~~~  1. 获取存款业务编号
    		str = getDepositsNum();
    		//~~~~~  2. 删除定存业务数据
    		if(str.length()>0){
    			//2.1 根据自动编号 删除定期存款业务流入账户数据
    			strSql = " delete from  " + pub.yssGetTableName("tb_cash_savinginacc") +
                " where FNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);
                
                //2.2 根据流入账户编号 删除定期存款业务流出账户数据
                strSql = " delete from  " + pub.yssGetTableName("tb_cash_savingoutacc") +
                " where FInAccNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);
    		}
    		
    		//~~~~~　3. 如果是太平资产合并版本，还要把资金调拨数据给删除
    		if(!Updateflag && str.length()>0){
    			str1 = getCashTransNum(str);//3.1 根据定期存款业务编号获取资金调拨编号
    			if(str1.length()>0){
    				//3.2 根据资金调拨编号删除资金调拨主表数据
        			strSql = " delete from  " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum in (" + operSql.sqlCodes(str1) + ")";
                    dbl.executeSql(strSql);
                    
                    //3.3 根据资金调拨编号删除资金调拨子表数据
                    strSql = " delete from  " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum in (" + operSql.sqlCodes(str1) + ")";
                    dbl.executeSql(strSql);
    			}
    		}
    		 conn.commit();
             bTrans = false;
             
    		
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：删除数据出错！！！");
    	} finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    //获取存款业务数据的交易编号
    private String getDepositsNum() throws YssException{
    	
    	String query = "";
    	ResultSet rs = null;
    	StringBuffer buf = null;//存储存款业务的交易编号
    	String str="";
    	try{
//    		query= " select * from "+pub.yssGetTableName("tb_cash_savinginacc") +" a where FSavingDate ="+dbl.sqlDate(sDate)+" and fportcode="+dbl.sqlString(sPort)+
//    			   //" select * from "+pub.yssGetTableName("tb_cash_savinginacc") +" a where  "+
//    		       " exists (select * from tmp_data_timedeposits b where a.fcashacccode = b.fcashacccode and a.fanalysiscode1 = b.fmanagercode "+
//    		       " and a.fanalysiscode2 = b.fcatcode and to_char(a.fsavingdate,'yyyy-MM-dd') = b.fsavingdate and to_char(a.fmaturedate,'yyyy-MM-dd') = b.fmaturedate " +
//    		       " and a.finmoney = b.fmoney " +
//    		       "and a.fportcode = b.fportcode)";
    		query = " select fnum from "+pub.yssGetTableName("tb_cash_savinginacc")+" where FSavingDate between "+dbl.sqlDate(sDate)+" and "+dbl.sqlDate(endDate)+
    		        " and fportcode in("+operSql.sqlCodes(sPort)+") and fdatasource='0'";
    		rs = dbl.openResultSet(query);
    		int count =0;
    		while(rs.next()){
    			if(count ==0){
    				buf = new StringBuffer();
    				count++;
    			}
    		   	buf.append(rs.getString("fnum")).append(",");
    		}
    		
    		if(buf != null && buf.length()>2){
    			str=buf.toString().substring(0, buf.toString().length()-1);
    		}
          return str;
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：获取存款业务数据编号出错！！！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    } 
    
    //获取资金调拨数据编号
    private String getCashTransNum(String dePositsNum) throws YssException{
    	String str = "";
    	CashTransAdmin ctAdmin = null;
    	try{
    		 ctAdmin = new CashTransAdmin();
	         ctAdmin.setYssPub(pub);
	         str = ctAdmin.getTransNums("", dePositsNum, "");
	         
	         return str;
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：获取资金调拨数据编号出错！！！");
    	}
    }
    
    
    private void impData() throws YssException{
    	String query = "";
    	ResultSet rs = null;
    	try{
    		query = " select a.fcashacccode,nvl(a.fcatcode,' ') as fcatcode,nvl(a.fmanagercode,' ') as fmanagercode,a.fmaturedate,a.fsavingdate,a.fportcode,a.fmoney,nvl(a.froundcode,' ') as froundcode " +
    				" ,b.fdepdurcode,nvl(c.fformulacode,' ') as fformulacode,d.fcurycode, " +
    				//注意：中保上线版本普通定存为0，合并版本普通定存为4.在做表结构调整时，已将历史数据转换
    				" case when d.fsubacctype ='0102' and facctype='01' then 4 when d.fsubacctype ='0103' and facctype='01' then 2 when d.fsubacctype ='0104' and facctype='01' then 3 " +
    				" when  d.facctype='05' then 1 else -1 end as  FSAVINGTYPE from"+
    		        //" (select * from tmp_data_timedeposits where  fportcode in ("+operSql.sqlCodes(sPort)+") and fsavingdate between "+dbl.sqlDate(sDate)+" and "+dbl.sqlDate(endDate)+" ) a"+
    				" (select * from tmp_data_timedeposits where  fportcode in ("+operSql.sqlCodes(sPort)+") and fsavingdate = "+dbl.sqlDate(dealDate)+" ) a"+
    		        " left join "+
    		        //--- 期限天数需要转换为系统存款期限中对应的代码，但是Tb_Para_DepositDuration表主键为fdepdurcode，所以有可能会查询出多条数据
    		        " (select fdepdurcode,fduration from "+pub.yssGetTableName("Tb_Para_DepositDuration")+" where fcheckstate=1 )b "+
    		        " on a.fdepdays = b.fduration"+
    		        " left join "+
    		        //--- 计息利率需要转换为系统比率公式中对应的代码，但是Tb_Para_DepositDuration表主键为fdepdurcode，所以有可能会查询出多条数据
    		        " (select fformulacode,fformulaname from "+pub.yssGetTableName("Tb_Para_Performula")+" where fcheckstate=1)c "+
    		        " on a.frate = c.fformulacode "+ //QDV4太平2010年11月09日02_B
    		        " left join "+
    		        // 根据现金账户代码匹配相应的账户子类型，通过判断账户子类型来判断业务类型
    		        " (select fcashacccode,fsubacctype,fcurycode,facctype from "+pub.yssGetTableName("tb_para_cashaccount")+" where fcheckstate=1 and facctype='01' and fsubacctype in ('0101','0102','0103','0104'))d "+
    		        " on a.fcashacccode = d.fcashacccode ";
    		
    		
    		rs = dbl.openResultSet(query);
    		
    		int count =0;//用于计数，对SavingBean进行懒加载
    		while(rs.next()){
    			if(count ==0){
    				DepositsInBean = new SavingBean();
    				DepositsInBean.setYssPub(pub);
    				count++;
    			}
    			setResultSetAttr(rs, DepositsInBean);
    			DepositsInBean.addSetting();
    		}
    		
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：导入定存数据出错！！！");
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    
    //设置流入账户属性
    public void setResultSetAttr(ResultSet rs,SavingBean DepositsInBean) throws YssException, SQLException{
            
    	    //自动编号如果已存在，就不会再生成，而DepositsInBean在循环里只生成一次，所以这里需要进行清空自动编号。
    	    if(!DepositsInBean.getNum().equalsIgnoreCase("")){
    	    	DepositsInBean.setNum("");
    	    }
    	
    		DepositsInBean.setSavingDate(rs.getDate("fsavingdate"));//存入日期
    		DepositsInBean.setSavingTime("00:00:00");//存入时间默认为空
    		DepositsInBean.setCashAccCode(rs.getString("fcashacccode"));//现金账户
    		DepositsInBean.setPortCode(rs.getString("fportcode"));//组合代码
    		DepositsInBean.setInvMgrCode(rs.getString("fmanagercode"));//投资经理代码
    		DepositsInBean.setCatCode(rs.getString("fcatcode"));//品种代码
    		DepositsInBean.setBrokerCode(" ");//券商代码默认为空
    		DepositsInBean.setTransNum(" ");//调拨编号
    		DepositsInBean.setDataSource(0);//数据源类型
    		DepositsInBean.setInMoney(YssFun.toDouble(rs.getString("fmoney")));//存入金额
    		DepositsInBean.setRecInterest(0);//应收利息
    		DepositsInBean.setInterestAccCode(rs.getString("fcashacccode"));//应收利息账户
    		DepositsInBean.setDepDurCode(rs.getString("fdepdurcode"));//期限代码
    		DepositsInBean.setMatureDate(rs.getDate("fmaturedate"));//到期日期
    		DepositsInBean.setFormulaCode(rs.getString("fformulacode"));//计息公式
    		DepositsInBean.setRoundCode(rs.getString("froundcode"));//舍入公式
    		DepositsInBean.setCuryCode(rs.getString("fcurycode"));
    		DepositsInBean.setBaseCuryRate(YssFun.toDouble(DepositsInBean.getOperValue("baserate")));//获取存入日期的基础汇率
    		DepositsInBean.setPortCuryRate(YssFun.toDouble(DepositsInBean.getOperValue("portrate")));//获取存入日期的组合汇率
    		DepositsInBean.setAvgBaseCuryRate(1);
    		DepositsInBean.setAvgPortCuryRate(1);
    		DepositsInBean.setDesc("");
    		DepositsInBean.creatorCode = pub.getUserCode();
    		DepositsInBean.creatorTime = YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");
    		if(Updateflag){
    		  //合并版本
    			//1. 业务类型
    			if(rs.getInt("FSAVINGTYPE")==-1){
    				throw new YssException("请先指定【现金账户："+rs.getString("fcashacccode")+" 的账户子类型】，再进行定存数据导入......");
    			}else{
    				DepositsInBean.setSavingType(rs.getInt("FSAVINGTYPE")+"");//业务类型
    			}
    			DepositsInBean.setSTradeType("first");//交易类型
    			DepositsInBean.setSSaveNum("");//存单编号
    			DepositsInBean.setDIncludeInterest(0);//所含利息
    			DepositsInBean.setSCalcType("fixrate");//计息方式
    			DepositsInBean.setDBasicMoney(0);//基本额度
    			DepositsInBean.setDBasicRate(0);//基本利率
    			DepositsInBean.setICirculDays(0);//通知天数
    			DepositsInBean.setDSavingMoney(0);//存单面值
    		}else{
    		  //原始版本	
    			//1. 业务类型
    			if(rs.getInt("FSAVINGTYPE")==-1){
    				throw new YssException("请先指定账户："+rs.getString("fcashacccode")+" 的账户子类型，再进行定存数据导入......");
    			}else{
    				DepositsInBean.setSavingType(rs.getInt("FSAVINGTYPE")+"");//业务类型
    			}
    		}
    		
    		DepositsInBean.setStrSaving(mergerStr(rs));//保存流出账户信息
    }
    
    
    public String mergerStr(ResultSet rs)throws YssException{
    	  StringBuffer buf = new StringBuffer();
    	try{
    		buf.append("").append("\t"); 
    		buf.append(rs.getString("fcashacccode")).append("\t"); //现金账户
    		buf.append("").append("\t"); 
    		buf.append(rs.getString("fportcode")).append("\t"); //组合代码
    		buf.append("").append("\t"); 
    		buf.append(rs.getString("fmanagercode")).append("\t"); //分析代码1--- 投资经理
    		buf.append("").append("\t"); 
    		buf.append(rs.getString("fcatcode")).append("\t"); //分析代码2--- 品种类型
    		buf.append("").append("\t"); 
    		buf.append("").append("\t"); //分析代码3--- 券商
    		buf.append("").append("\t"); 
    		buf.append(rs.getDouble("fmoney")).append("\t"); //流出金额
    		buf.append("").append("\t"); 
    		buf.append("").append("\t"); 
    		buf.append(rs.getDate("fsavingdate")).append("\t"); 
    		buf.append("").append("\t"); 
    		buf.append("").append("\t"); 
    		buf.append("").append("\t"); 
    		buf.append("").append("\t"); 
    		buf.append("").append("\tnull");
    		
    		return buf.toString();
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口:拼接字符串报错!!");
    	}
    }

    
    public void checkDate()throws YssException{
    	boolean bTrans = false;
    	Connection conn = dbl.loadConnection();
    	String strSql = "";
        String str = "",str1= "";
    	try{
    		
    		 conn.setAutoCommit(false);
             bTrans = true;
    		//~~~~~  1. 获取存款业务编号
    		str = getDepositsNum();
    		//~~~~~  2. 审核定存业务数据
    		if(str.length()>0){
    			//2.1 根据自动编号 审核定期存款业务流入账户数据
    			strSql = " update  " + pub.yssGetTableName("tb_cash_savinginacc") +
    			         " set FCheckState= 1, FCheckUser = "+dbl.sqlString(pub.getUserCode())+
    			         " ,FCheckTime ='"+YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +"'"+
                         " where FNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);
                
                //2.2 根据流入账户编号 审核定期存款业务流出账户数据
                strSql = " update " + pub.yssGetTableName("tb_cash_savingoutacc") +
                         " set FCheckState= 1, FCheckUser = "+dbl.sqlString(pub.getUserCode())+
		                 " ,FCheckTime ='"+YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +"'"+
                         " where FInAccNum in (" + operSql.sqlCodes(str) + ")";
                dbl.executeSql(strSql);
    		}
    		
    		//~~~~~　3. 如果是太平资产合并版本，还要把资金调拨数据给审核
    		if(!Updateflag&&str.length()>0){
    			str1 = getCashTransNum(str);//3.1 根据定期存款业务编号获取资金调拨编号
    			if(str1.length()>0){
    				//3.2 根据资金调拨编号审核资金调拨主表数据
        			strSql = " update  " + pub.yssGetTableName("Tb_Cash_Transfer") +
        			         " set FCheckState= 1, FCheckUser = "+dbl.sqlString(pub.getUserCode())+
	                         " ,FCheckTime ='"+YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +"'"+
                             " where FNum in (" + operSql.sqlCodes(str1) + ")";
                    dbl.executeSql(strSql);
                    
                    //3.3 根据资金调拨编号审核资金调拨子表数据
                    strSql = " update  " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                             " set FCheckState= 1, FCheckUser = "+dbl.sqlString(pub.getUserCode())+
	                         " ,FCheckTime ='"+YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss") +"'"+
                             " where FNum in (" + operSql.sqlCodes(str1) + ")";
                    dbl.executeSql(strSql);
    			}
    		}
    		 conn.commit();
             bTrans = false;
             
    		
    	}catch(Exception e){
    		throw new YssException("定存数据导入接口：删除数据出错！！！");
    	} finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
}
