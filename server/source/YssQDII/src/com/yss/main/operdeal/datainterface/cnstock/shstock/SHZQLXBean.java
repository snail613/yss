package com.yss.main.operdeal.datainterface.cnstock.shstock;
import com.yss.dsub.*;
import com.yss.util.YssException;
import java.sql.*;
import com.yss.main.operdeal.datainterface.pretfun.*;
import com.yss.main.operdeal.datainterface.CommonPretFun;
import com.yss.util.YssFun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.util.YssD;

/**
 * QDII国内：MS00011
 * QDV4.1赢时胜（上海）2009年4月20日11_A
 * add by songjie 2009.05.14
 * 依据国债利息库接口处理_产品需求规格说明书
 * 用于处理临时表中上交所提供的债券数据，将处理后的数据插入到债券信息表中
 */
public class SHZQLXBean extends DataBase {
    //add by songjie 2010.03.22 国内：MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    ArrayList alShowZqdm = new ArrayList();
    
    public ArrayList getAlShowZqdm(){
    	return alShowZqdm;
    }
    //add by songjie 2010.03.22 国内：MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
	
    /**
     * 构造函数
     */
    public SHZQLXBean() {
    }

    /**
     * 用于将临时表中的数据经过处理然后储存到债券信息表中
     * @throws YssException
     */
    public void inertData() throws YssException {
       Connection con = dbl.loadConnection();//获取数据库连接
       ResultSet rs = null;//声明结果集
       boolean bTrans = false;//用于做事务是否开启的标志
       PreparedStatement pstmt = null;//声明PreparedStatement
       String localDate="";//用于储存临时表中债券的登记日
       String strSql="";//用于储存sql语句
       CommonPretFun pret=new CommonPretFun();

       HashMap hmZQRate = null;
       double FGzlx = 0;
       try{
          pret.setYssPub(pub);//设置pub
          con.setAutoCommit(false);
          bTrans = true;
          strSql = "delete from "+pub.yssGetTableName("Tb_Data_BondInterest")+
                   " where FSecurityCode like '% CG%' and FRecordDate = " +
                   dbl.sqlDate(YssFun.formatDate(this.sDate,"yyyy-MM-dd"));//删除债券信息表中与临时表有相同的债券代码和登记日的债券数据
          dbl.executeSql(strSql);

          strSql = " select * from tmpSH_gzlx where jxrq = "+dbl.sqlString(YssFun.formatDate(this.sDate,"yyyyMMdd"));//查询所有临时表中的债券信息
          rs=dbl.openResultSet(strSql);

          strSql=" insert into " + pub.yssGetTableName("Tb_Data_BondInterest") + "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,"+
                 " FIntAccPer100,FIntDay,FCheckState,FCreator,FCreateTime,FDataSource,FSHIntAccPer100) values(?,?,?,?,?,?,?,?,?,'IF',?)";//将临时表中的数据经过处理插入到债券信息表中
          pstmt=dbl.openPreparedStatement(strSql);

          while(rs.next())//循环所有临时表中的债券信息
          {
              localDate =pret.getDateConvert(rs.getString("JXRQ"));
              pstmt.setString(1,rs.getString("GZDM")+" CG"); //将临时表中的证券代码加空格加"CG"储存到债券信息的证券代码字段
              pstmt.setDate(2,YssFun.toSqlDate(localDate));//储存登记日信息
              pstmt.setDate(3,YssFun.toSqlDate("9998-12-31"));
              pstmt.setDate(4,YssFun.toSqlDate("9998-12-31"));
              pstmt.setDouble(5,rs.getDouble("YJLX"));//储存应记利息信息
              pstmt.setInt(6,0);//储存已计提天数信息
              pstmt.setInt(7,1);
              pstmt.setString(8, pub.getUserCode()); //创建人、修改人
              pstmt.setString(9,YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

              //edit by songjie 2009.12.21 MS00847 
              //QDV4赢时胜（北京）2009年11月30日03_B 由传入两个参数改为传入三个参数 
              hmZQRate = super.calculateZQRate(rs.getString("GZDM")+" CG",this.sDate, "Day", this.sPort);

              //add by songjie 2010.03.22 QDII国内：MS00925 
              //QDV4赢时胜（测试）2010年03月19日03_AB
              if(((String)hmZQRate.get("haveInfo")).equals("false")){
              	if(!alShowZqdm.contains(rs.getString("GZDM")+" CG")){
              		alShowZqdm.add(rs.getString("GZDM")+" CG");
              	}
              }
              //add by songjie 2010.03.22 QDII国内：MS00925 
              //QDV4赢时胜（测试）2010年03月19日03_AB
              
              FGzlx = Double.parseDouble((String)hmZQRate.get("GZLX"));

              pstmt.setDouble(10,FGzlx);//税后百元利息是根据债券信息设置表中的税后票面利率代入公式计算得到的

              pstmt.addBatch();
          }
          pstmt.executeBatch();

          con.commit();
          bTrans = false;
          con.setAutoCommit(true);
          
          //add by songjie 2010.03.22 MS00925 
          //QDV4赢时胜（测试）2010年03月19日03_AB
          showUnInsertZQInfo();
       }catch(Exception e)
       {
          throw new YssException("插入上交所国债利息数据出错",e);
       }
       finally{
          dbl.closeStatementFinal(pstmt);
          dbl.closeResultSetFinal(rs);
          dbl.endTransFinal(con, bTrans);
       }
   }
    
    /**
     * add by songjie
     * 2010.03.22
     * MS00925
     * QDV4赢时胜（测试）2010年03月19日03_AB
     * @throws YssException
     */
    private void showUnInsertZQInfo()throws YssException{
    	Iterator iterator = null;
    	String info = "";
    	String showInfos = "";
    	try{
    		if(alShowZqdm.size() > 0){
    			iterator = alShowZqdm.iterator();
    			while(iterator.hasNext()){
        			info = (String)iterator.next();
        			showInfos += info + ",";
    			}
    			
        		if(showInfos.length() > 2)
        		{
        			showInfos = showInfos.substring(0, showInfos.length() - 1);
        			showInfos = "请维护证券 " + showInfos + " 的相关债券信息";
        		}
        		
        		if(!showInfos.equals("")){
        			throw new YssException(showInfos);
        		}
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未维护债券信息的导入数据出错！",e);
    	}
    }
}
