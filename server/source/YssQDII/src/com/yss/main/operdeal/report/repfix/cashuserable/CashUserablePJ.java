package com.yss.main.operdeal.report.repfix.cashuserable;

import java.sql.*;
import java.util.*;
import com.yss.dsub.*;
import com.yss.main.operdeal.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.netvalueviewpl.*;
import com.yss.main.operdeal.report.repfix.cashuserable.pojo.*;
import com.yss.main.parasetting.*;
import com.yss.main.report.*;
import com.yss.util.*;
import java.lang.Math.*;

public class CashUserablePJ
      extends BaseBuildCommonRep {
   public CashUserablePJ() {
   }

   private CommonRepBean repBean;
   private java.util.Date startDate = null; //期初日期
   private String sPort = ""; //组合代码
   private FixPub fixPub = null;
   private String cury="";
   private String cash = "";//次托的账户代码

   /**
    * buildReport
    *
    * @param sType String
    * @return String
    */
   public String buildReport(String sType) throws YssException {
      String sResult = "";
      sResult = buildResult(this.startDate, this.sPort);
      return sResult;
   }

   /**
    * initBuildReport
    *
    * @param bean BaseBean
    */
   public void initBuildReport(BaseBean bean) throws YssException {
      fixPub = new FixPub();
      fixPub.setYssPub(pub);
      repBean = (CommonRepBean) bean;
      this.parse(repBean.getRepCtlParam());
   }

   public void parse(String str) throws YssException {
      String[] sReq = str.split("\n");
      try {
         this.startDate = YssFun.toDate(sReq[0].split("\r")[1]);
         this.sPort = sReq[1].split("\r")[1];
      }catch (Exception e) {
         throw new YssException("解析参数出错", e);
      }
   }

   protected String buildResult(java.util.Date startDate,
                                String sPort) throws
         YssException {
      String strResult = "";
      ResultSet rs = null;
      String strSql = "";
      String result[] = null;
      BaseOperDeal deal = new BaseOperDeal();
      deal.setYssPub(pub);
      java.util.Date titleDate = null;
      StringBuffer buf = null;
      boolean flag = false;
      StringBuffer finBuf = new StringBuffer();
      try {
            List list=this.getstorageCury();
            for(int i=0;i<list.size();i++){
        	  	flag =false;
                if(((String)list.get(i)).endsWith("其中：股票")){
                    result=this.getStock((String)list.get(i));
                }/*else if(((String)list.get(i)).endsWith("债券")){
                     result=this.getBond((String)list.get(i));
                }*/else if(((String)list.get(i)).endsWith("存款")){
                    result=this.getCash((String)list.get(i));
                    flag =true;
               }else{
                    this.cury=(String)list.get(i);
                    result=this.getTotal();
               }
                for (int j = 0; j < result.length; j++) {
                	finBuf.append(fixPub.buildRowCompResult(result[j], "DSPJ")).
                       append("\r\n");
                 	if(flag){
                 		finBuf.append(fixPub.buildRowCompResult("", "DSPJ")).
                 		append("\r\n");
                 	}
                }
            }
         if (finBuf.toString().length() > 2) {
            strResult = finBuf.toString().substring(0,
                  finBuf.toString().length() - 2);
         }
         return strResult;
      }catch (Exception e) {
         throw new YssException(e.getMessage());
      }finally {
         dbl.closeResultSetFinal(rs);
      }
   }
   public String[] getTotal()throws YssException
   {
       String[] arrResult= new String[11];
       String[] arrEQ = new String[11];
       String[] arrCash = new String[11];
       StringBuffer buf=new StringBuffer();
       String rate="";
       double ii = 0.0;
       ResultSet rs=null;
       try{
           rate = getRate();
           
           buf.append(this.cury).append(",");
           buf.append(rate).append(",");
           
           arrEQ = this.getStock("股票")[0].split(","); 
           arrCash = this.getCash("存款")[0].split(",");
    		   
    	   for(int j = 2;j<arrEQ.length;j++){
    		   ii =YssFun.toDouble(arrEQ[j].toString()) + YssFun.toDouble(arrCash[j].toString());
    		   if(j == arrEQ.length-1){
    	           buf.append(String.valueOf(ii)).append("\f\f");
    		   }else{
    	           buf.append(String.valueOf(ii)).append(",");
    		   }
    	   }
       arrResult = buf.toString().split("\f\f");
       }catch(Exception e){
           throw new YssException(e.getMessage());
       }finally{
           dbl.closeResultSetFinal(rs);
       }
       return arrResult;
   }
   public String[] getStock(String key)throws YssException
{
       String[] arrResult=null;
      StringBuffer buf=new StringBuffer();
      String strSql="";
      ResultSet rs=null;
      try
      {
         /* strSql=" select m.fportcost,m.fmarketvalue,m.rate,m.fportmarketvalue,m.totodaywss,n.totomorrywss,m.totodaywss-"+
           " n.totomorrywss as todaywss "+
           " from(select  a.fportcode,a.fnavdate,a.fportcost,a.FMARKETVALUE,round(round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)/a.fmarketvalue,4) as rate,"+
           " round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE,2) as fportmarketvalue,"+
           " round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE,2)-a.fportcost as totodaywss,a.fcurycode from"+
           " (select fportcode,fnavdate,fcurycode, FPORTCOST,FMARKETVALUE from " +pub.yssGetTableName("tb_data_navdata")+
           " where FNAVDATE="+dbl.sqlDate(this.startDate)+" and  FKEYCODE="+dbl.sqlString(this.cury)+" and fretypecode='Security' "+
           " and fdetail=4 and fcurycode="+dbl.sqlString(this.cury)+"and Fportcode="+dbl.sqlString(this.sPort)+" and FORDERCODE like 'EQ%')a"+
           " left join "+pub.yssGetTableName("tb_data_valrate")+" b on b.FVALDATE=a.fnavdate and b.fcurycode=a.fcurycode"+
           " )m left join(select a.fportcode,a.fnavdate,a.fportcost,a.FMARKETVALUE,round(round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)/a.fmarketvalue,4) as rate,"+
           " round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE) as fportmarketvalue,round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)-a.fportcost as totomorrywss"+
           " from(select fportcode,fnavdate,fcurycode, FPORTCOST,FMARKETVALUE from "+pub.yssGetTableName("tb_data_navdata")+
           " where FNAVDATE="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+" AND FKEYCODE="+dbl.sqlString(this.cury)+"and fretypecode='Security' "+
           " and fdetail=4 and fcurycode="+dbl.sqlString(this.cury)+"and Fportcode="+dbl.sqlString(this.sPort)+" and FORDERCODE like 'EQ%')a"+
           " left join "+pub.yssGetTableName("tb_data_valrate")+" b on b.FVALDATE=a.fnavdate and b.fcurycode=a.fcurycode"+
           " )n on n.fportcode= m.fportcode";*/
    	  strSql="select m.fportcost, m.fmarketvalue,m.fportmarketvalue,m.toTdaywss,n.toYdaywss,m.toTdaywss - n.toYdaywss as todaywss " +
    	  		"from (select fportcode, fnavdate, fcurycode, sum(FPORTCOST) as FPORTCOST,sum(FMARKETVALUE) as FMARKETVALUE,sum(FPortMarketValue) as FPortMarketValue,sum(FFXValue) as toTdaywss from " +pub.yssGetTableName("tb_data_navdata")+
    	  		" where FNAVDATE="+dbl.sqlDate(this.startDate)+" and FKEYCODE ="+dbl.sqlString(this.cury)+" and FORDERCODE like 'EQ%' and fretypecode = 'Security' " +
    	  		"and fdetail = 4 and fcurycode ="+dbl.sqlString(this.cury)+" and Fportcode="+dbl.sqlString(this.sPort)+" group by fportcode,fnavdate,fcurycode) m " +
    	  		"left join (select fportcode, fnavdate, fcurycode, sum(FPORTCOST) as FPORTCOST,sum(FMARKETVALUE) as FMARKETVALUE,sum(FPortMarketValue) as FPortMarketValue,sum(FFXValue) as toYdaywss from " +pub.yssGetTableName("tb_data_navdata")+
    	  		" where FNAVDATE="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+" AND FKEYCODE ="+dbl.sqlString(this.cury)+" and FORDERCODE like 'EQ%' and fretypecode = 'Security' " +
    	  		"and fdetail = 4 and fcurycode  ="+dbl.sqlString(this.cury)+" and Fportcode="+dbl.sqlString(this.sPort)+" group by fportcode,fnavdate,fcurycode) n on n.fportcode =  m.fportcode";
      rs=dbl.openResultSet(strSql);
      if(rs.next())
      {
          buf.append(key).append(",");
          buf.append(getRate()).append(",");
          buf.append(rs.getDouble("toTdaywss")).append(",");
          buf.append(rs.getDouble("toYdaywss")).append(",");
          buf.append(rs.getDouble("todaywss")).append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append("\f\f");
      }else{
          buf.append(key).append(",");
          buf.append(getRate()).append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append("\f\f");
      }
      arrResult = buf.toString().split("\f\f");
      }catch(Exception e){
          throw new YssException(e.getMessage());
      }finally{
          dbl.closeResultSetFinal(rs);
      }
      return arrResult;

}
public String[] getBond(String key)throws YssException
{
    String[] arrResult=null;
      StringBuffer buf=new StringBuffer();
      String strSql="";
      ResultSet rs=null;
      try
      {
          strSql=" select m.fportcost,m.fmarketvalue,m.rate,m.fportmarketvalue,m.totodaywss,n.totomorrywss,m.totodaywss- n.totomorrywss as todaywss from" +
          			"(select  a.fportcode,a.fnavdate,a.fportcost,a.FMARKETVALUE,round(round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)/a.fmarketvalue,4) as rate,"+
          			" round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE,2) as fportmarketvalue,round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE,2)-a.fportcost as totodaywss," +
          			"a.fcurycode from " +
          				"(select fportcode,fnavdate,fcurycode, FPORTCOST,FMARKETVALUE from " +pub.yssGetTableName("tb_data_navdata")+
          				" where FNAVDATE="+dbl.sqlDate(this.startDate)+" and  FKEYCODE="+dbl.sqlString(this.cury)+" and fretypecode='Security' "+
          				" and fdetail=4 and fcurycode="+dbl.sqlString(this.cury)+"and Fportcode="+dbl.sqlString(this.sPort)+" and FORDERCODE like 'FI%')a"+
          			" left join "+pub.yssGetTableName("tb_data_valrate")+" b on b.FVALDATE=a.fnavdate and b.fcurycode=a.fcurycode " +
          			")m " +
          			"left join(select a.fportcode,a.fnavdate,a.fportcost,a.FMARKETVALUE,round(round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)/a.fmarketvalue,4) as rate,"+
          				" round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE) as fportmarketvalue,round(a.fmarketvalue*b.FBASERATE/b.FPORTRATE)-a.fportcost as totomorrywss"+
          				" from(select fportcode,fnavdate,fcurycode, FPORTCOST,FMARKETVALUE from "+pub.yssGetTableName("tb_data_navdata")+
          					" where FNAVDATE="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+" AND FKEYCODE="+dbl.sqlString(this.cury)+"and fretypecode='Security' "+
          					" and fdetail=4 and fcurycode="+dbl.sqlString(this.cury)+"and Fportcode="+dbl.sqlString(this.sPort)+" and FORDERCODE like 'FI%')a"+
          				" left join "+pub.yssGetTableName("tb_data_valrate")+" b on b.FVALDATE=a.fnavdate and b.fcurycode=a.fcurycode"+
          					" )n on n.fportcode= m.fportcode";
      rs=dbl.openResultSet(strSql);
      while(rs.next())
      {
          buf.append(key).append(",");
          buf.append(getRate()).append(",");
          buf.append(rs.getDouble("totodaywss")).append(",");
          buf.append(rs.getDouble("totomorrywss")).append(",");
          buf.append(rs.getDouble("todaywss")).append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append(",");
          buf.append("0.00").append("\f\f");
      }
      
      	arrResult = buf.toString().split("\f\f");
      }catch(Exception e){
          throw new YssException(e.getMessage());
      }finally{
          dbl.closeResultSetFinal(rs);
      }
      return arrResult;

   }

public String[] getCash(String key)throws YssException{
    String[] arrResult=null;
    StringBuffer buf=new StringBuffer();
    String strSql="";
    ResultSet rs=null;
    try{
    	 strSql="select m.toTdaywss,n.toYdaywss,m.toTdaywss - n.toYdaywss as todaywss,m.fmarketvalue,m.fportmarketvalue," +
    	 		"ysFMARKETVALUE,ysFPortMarketValue,yfFMARKETVALUE,yfFPortMarketValue from " +
    	 		"(select fportcode, fnavdate, fcurycode, FPORTCOST, FMARKETVALUE,FPortMarketValue,FFXValue as toTdaywss from "+pub.yssGetTableName("tb_data_navdata")
    	 		+" a where a.fnavdate="+dbl.sqlDate(this.startDate)+" and a.fretypecode='Cash' and Fportcode="+dbl.sqlString(this.sPort)
    	 		+" and fcurycode= "+dbl.sqlString(this.cury)+" and  a.fkeycode in ("+this.cash+")) m " +
    	 		"left join (select fportcode, fnavdate, fcurycode, FPORTCOST, FMARKETVALUE,FPortMarketValue,FFXValue as toYdaywss from "+pub.yssGetTableName("tb_data_navdata")
    	 		+"  a where a.fnavdate="+dbl.sqlDate(YssFun.addDay(this.startDate,-1))+" and a.fretypecode='Cash' and Fportcode="+dbl.sqlString(this.sPort)
    	 		+" and a.fkeycode in ("+this.cash+") and fcurycode= "+dbl.sqlString(this.cury)+") n on n.fportcode =  m.fportcode " +
    	 		"full join (select nvl(ys.fportcode,yf.fportcode) as fportcode, nvl(ys.fnavdate,yf.fnavdate) as fnavdate,nvl(ys.fcurycode,yf.fcurycode) as fcurycode," +
    	 		"nvl(ys.FMARKETVALUE,0) as ysFMARKETVALUE,nvl(ys.FPortMarketValue,0) as ysFPortMarketValue, nvl(yf.FMARKETVALUE,0) as yfFMARKETVALUE," +
    	 		"nvl(yf.FPortMarketValue,0) as yfFPortMarketValue from " +
    	 		"(select fportcode, fnavdate, fcurycode, FPORTCOST, FMARKETVALUE,FPortMarketValue from "+pub.yssGetTableName("tb_data_navdata")
    	 		//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05
    	 		+"  a  where a.fnavdate="+dbl.sqlDate(this.startDate)+" and a.fretypecode='Cash' and a.fgradetype5 in ("+this.cash+") and Fportcode="+dbl.sqlString(this.sPort)
    	 		+"  and a.fgradetype6 ='06TD' and a.fcurycode="+dbl.sqlString(this.cury)+") ys " +
    	 		"FULL join (select fportcode, fnavdate, fcurycode, FPORTCOST, FMARKETVALUE,FPortMarketValue from "+pub.yssGetTableName("tb_data_navdata")
    	 		+"  a  where a.fnavdate="+dbl.sqlDate(this.startDate)+" and a.fretypecode='Cash' and a.fgradetype5 in ("+this.cash+") and Fportcode="+dbl.sqlString(this.sPort)
    	 		+"  and a.fgradetype6 ='07TD' and a.fcurycode="+dbl.sqlString(this.cury)+") yf  " +
    	 		//#1203 净值统计表汇兑损益汇总项少了现金类按照币种汇总的汇兑损益科目  add by jiangshichao 2011.03.05 end 
    	 		"on ys.fportcode=yf.fportcode and ys.fnavdate=yf.fnavdate and ys.fcurycode=yf.fcurycode) ysyf " +
    	 		"on m.fnavdate = ysyf.fnavdate and m.fcurycode = ysyf.fcurycode and m.fportcode =  ysyf.fportcode";
    	 rs=dbl.openResultSet(strSql);
         if(rs.next())
         {
             buf.append(key).append(",");
             buf.append(getRate()).append(",");
             buf.append(rs.getDouble("toTdaywss")).append(",");
             buf.append(rs.getDouble("toYdaywss")).append(",");
             buf.append(rs.getDouble("todaywss")).append(",");
             buf.append(rs.getDouble("fmarketvalue")).append(",");
             buf.append(rs.getDouble("fportmarketvalue")).append(",");
             buf.append(rs.getDouble("ysFMARKETVALUE")).append(",");
             buf.append(rs.getDouble("ysFPortMarketValue")).append(",");
             buf.append(rs.getDouble("yfFMARKETVALUE")).append(",");
             buf.append(rs.getDouble("yfFPortMarketValue")).append("\f\f");
         }else{
             buf.append(key).append(",");
             buf.append(getRate()).append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append(",");
             buf.append("0.00").append("\f\f");
         }
         rs.close();        
         arrResult = buf.toString().split("\f\f");
    }catch(Exception e){
        throw new YssException(e.getMessage());
    }finally{
        dbl.closeResultSetFinal(rs);
    }
    return arrResult;
}
   public List getstorageCury()throws YssException
   {
       String strSql="";
       ResultSet rs=null;
       List list=new ArrayList();
       try
       {//仅对次托账户币种：经由组合设置托管人的关联机构来链接到次托的活期存款帐户
    	   strSql="select distinct a.fcurycode,a.fcashacccode from "+pub.yssGetTableName("tb_para_cashaccount")+" a where a.fbankcode =  (select fsubcode from "
    	   		  +pub.yssGetTableName("tb_Para_Portfolio_RelaShip")+" where frelatype = 'Trustee' and FRelaGrade = 'secondary' and fcheckstate = 1) " +
    	   		  " and fcheckstate = 1 and a.facctype='01' and a.fportcode = "+dbl.sqlString(this.sPort);
           /*trSql="select distinct a.fcurycode from "+pub.yssGetTableName("tb_para_cashaccount")+" a where a.fcashaccname like 'HSBC%' and a.fportcode="
           +dbl.sqlString(this.sPort);*/
           rs=dbl.openResultSet(strSql);
           while(rs.next()){
               //if(!rs.getString("fcurycode").equalsIgnoreCase("IDR")){——不记得是为啥了
                   list.add(rs.getString("fcurycode"));
                   list.add("其中：股票");
                  // list.add("债券");
                   list.add("存款");
                   cash = cash+"'"+rs.getString("fcashacccode")+"',";
               //}
           }
           cash = cash.substring(0, cash.length()-1);
       }catch(Exception e){
           throw new YssException(e.getMessage());
       }finally{
           dbl.closeResultSetFinal(rs);
       }
       return list;
   }
   
   public String getRate()throws YssException
   {
       String strSql="",rate="";
       ResultSet rs=null;
       try
       {
           strSql="select FBASERATE,FPORTRATE,round(FBASERATE/FPORTRATE,4) as rate from "+pub.yssGetTableName("tb_data_valrate")+" where FVALDATE = "+dbl.sqlDate(this.startDate)+" and fcurycode="+dbl.sqlString(this.cury);
           rs=dbl.openResultSet(strSql);
           if(rs.next()){
        	   rate = rs.getString("rate");
           }
       }catch(Exception e){
           throw new YssException(e.getMessage());
       }finally{
           dbl.closeResultSetFinal(rs);
       }
       return rate;
   }
}
