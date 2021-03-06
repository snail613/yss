package com.yss.main.operdeal.rightequity;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.dsub.*;
import com.yss.main.operdata.*;
import com.yss.main.operdata.overthecounter.OpenFundTradeAdmin;
import com.yss.main.operdata.overthecounter.pojo.OpenFundTradeBean;
import com.yss.manager.*;
import com.yss.util.*;

/**
 * <p>Title: BaseRightEquity </p>
 * <p>Description: 权益处理基类 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: www.ysstech.com </p>
 * @author not attributable
 * @version 1.0
 */
public class BaseRightEquity
    extends BaseBean {
    public String strOperStartDate = ""; //业务起始日期
    public String strOperEndDate = ""; //业务截止日期
    //xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
    //public String strCurDate = "";
    //public String strPortCode = ""; //组合代码"\t"间隔
    //------------------------end-------------------------//
    public String strOperType = ""; //业务类别
    public static String strDealInfo = "no"; //"yes"有业务，"no"无业务
    private String sAllDeleteNum="";//保存交易编号
    public BaseRightEquity() {
    }

    public ArrayList getDayRightEquitys(java.util.Date dDate,String sPortCode) throws
        YssException {
        return null;
    }
    /**
     * 此方法主要保存数据到交易主表，和交易子表中
     * @param alRightEquitys ArrayList 保存数据的集合
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
     */
    public void saveRightEquitys(ArrayList alRightEquitys,java.util.Date dDate,String sPortCode) throws YssException {
    	//---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//        Connection conn = null;
//        boolean bTrans = true;
    	//---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
        try {
            if (alRightEquitys != null && alRightEquitys.size() > 0) {
                TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
                tradeData.setYssPub(pub);
                tradeData.addAll(alRightEquitys);//添加数据
//                conn=dbl.loadConnection();//获取连接
//                conn.setAutoCommit(false); //设置为手动打开连接
//                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); //给操作表加锁
//                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); //给操作表加锁
                TradeBean tradefilter = filterBean(dDate, sPortCode);
                if (tradefilter != null) {
                    tradeData.insert(YssFun.parseDate(tradefilter.getBargainDate())
                                     , YssFun.parseDate(tradefilter.getBargainDate())
                                     , sPortCode
                                     , tradefilter.getTradeCode()
                                     ,tradefilter.getDsType());//保存数据
                }
                //---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                conn.commit();
//                conn.setAutoCommit(true);
//                bTrans = false;
                //---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } 
        //---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//        finally {
//            dbl.endTransFinal(conn, bTrans);
//        }
        //---delete by songjie 2012.12.21 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
    }
    //story 1574 add by zhouwei 20111108 将数据保存到开放式基金交易数据表（场外）
    public void saveBankRightEquitys(ArrayList alRightEquitys,java.util.Date dDate,String sPortCode) throws YssException {
        Connection conn = null;
        boolean bTrans = true;
        try {
	            if (alRightEquitys != null && alRightEquitys.size() > 0) {
	                OpenFundTradeAdmin openfund = new OpenFundTradeAdmin();//开放式交易数据操作类
	                openfund.setYssPub(pub);
	                openfund.addList(alRightEquitys);//添加数据
	                String securitycodes=openfund.getSecurityCodes(alRightEquitys);
	                conn=dbl.loadConnection();//获取连接
	                conn.setAutoCommit(false); //设置为手动打开连接
	                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_OpenFundTrade")); //给操作表加锁                           
	                openfund.insert("",dDate,dDate,securitycodes,sPortCode,"","06",true,false,"QY_CL");//保存数据
	                conn.commit();
	                conn.setAutoCommit(true);
	                bTrans = false;
	             }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    /**
     * 重载方法，此方法主要保存数据1.到交易主表，和交易子表中，2.到交易关联表中
     * @param alRightEquitys ArrayList 保存主表和子表中的数据的集合
     * @param tradeRealRightData ArrayList 保存交易关联表中的数据的集合
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @throws YssException 异常
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
     */
    public void saveRightEquitys(ArrayList alRightEquitys,ArrayList tradeRealRightData,java.util.Date dDate,String sPortCode) throws YssException {
       Connection conn = null;
       boolean bTrans = true;
       TradeRelaBean tradeReal=null;//交易关联表的javaBean
       String sSecurityCode = ""; //保存证券代码
       try {
           conn = dbl.loadConnection(); //获取连接
           conn.setAutoCommit(false); //设置为手动打开连接
           if (alRightEquitys != null && alRightEquitys.size() > 0) {//判断集合是否为空或NULL
               TradeDataAdmin tradeData = new TradeDataAdmin();//交易数据操作类
               tradeData.setYssPub(pub);
               tradeData.addAll(alRightEquitys);//添加数据
               dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_SubTrade")); //给操作表加锁
               dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_Trade")); //给操作表加锁
               TradeBean tradefilter = filterBean(dDate, sPortCode);//删除条件
               if (tradefilter != null) {
                   tradeData.insert(YssFun.parseDate(tradefilter.getBargainDate())
                                    , YssFun.parseDate(tradefilter.getBargainDate())
                                    , sPortCode
                                    , tradefilter.getTradeCode()
                                    ,tradefilter.getDsType());//保存交易表数据
               }
           }
           if(tradeRealRightData!=null&&tradeRealRightData.size()>0){//判断集合是否为空或NULL
               for(int i=0;i<tradeRealRightData.size();i++){
                   tradeReal=(TradeRelaBean)tradeRealRightData.get(i);
                   sSecurityCode+=tradeReal.getSSecurityCode()+",";
               }
               TradeRelaDataAdmin tradeRealData=new TradeRelaDataAdmin();//交易关联实体操作类
               tradeRealData.setYssPub(pub);
               tradeRealData.setList(tradeRealRightData);//添加数据
               dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Data_TradeRela")); //给交易关联表加锁
               TradeRelaBean tradeRealfilter=filterBean(sPortCode);//删除条件
               if(tradeRealfilter!=null){
                   tradeRealData.insert("",tradeRealfilter.getSRelaType(),sSecurityCode,sPortCode,"","","",true);//保存交易关联数据
               }
           }
           conn.commit();//提交事务
           conn.setAutoCommit(true);//设置为自动打开数据库连接
           bTrans = false;
       } catch (Exception e) {
           throw new YssException(e.getMessage());
       } finally {
           dbl.endTransFinal(conn, bTrans);
       }
   }
   /**
    * 删除交易主子表中数据
    * @param dDate Date 操作日期
    * @param sPortCode String 组合代码
    * @return TradeBean 返回值
    * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
    */
   public TradeBean filterBean(java.util.Date dDate,String sPortCode) throws YssException{
        return null;
    }

    /**
     * 删除条件，删除交易关联表中数据
     * @param sPortCode String 组合代码
     * @return TradeRelaBean 返回值
     * xuqiji 20090721 QDV4.1赢时胜（上海）2009年4月20日15_A MS00015    国内权益处理
     */
    public TradeRelaBean filterBean(String sPortCode) throws YssException{
        return null;
    }
    /**
     * 判断接口导入数据中有没有要处理的权益信息的业务资料数据
     * @param alRightEquitys ArrayList 保存权益信息数据的集合
     * @param sTradeType 交易类型
     * @param sDsType 操作类型 界面上输入：其他数据为：'HD_JK' FDataSouce=0，权益处理数据:'HD_QY' FDataSouce=0
     * 接口：读入其他数据'ZD_JK'   FDataSource=1 ，权益处理数据： 'ZD_QY'  FDataSource=1
     * @param dDate Date 操作日期
     * @param sPortCode String 组合代码
     * @param sSecurityCode String 证券代码
     * @return ArrayList 返回新的集合
     * @throws YssException
     */
    public ArrayList checkSubTradeHaveRightData(ArrayList alRightEquitys,String sTradeType,String sDsType,
                                                java.util.Date dDate,String sPortCode,String sSecurityCode)throws YssException{
        ResultSet rs=null;
        StringBuffer buff=null;
        TradeSubBean subTrade = null;//交易子表的javaBean
        try{
            buff=new StringBuffer();
            buff.append(" select * from ");
            buff.append(pub.yssGetTableName("tb_data_subtrade"));//交易子表
            buff.append(" where FCheckState =1 and FBargaindate =").append(dbl.sqlDate(dDate));//操作日期
            buff.append(" and FSecurityCode in(").append(this.operSql.sqlCodes(sSecurityCode)).append(")");//证券代码
            buff.append(" and FPortCode = ").append(dbl.sqlString(sPortCode));//组合代码
            buff.append(" and FTradeTypeCode =").append(dbl.sqlString(sTradeType));//交易类型
         // edit by lidaolong #536 有关国内接口数据处理顺序的变更
            buff.append(" and FDs in(").append(operSql.sqlCodes((sDsType))).append(")");//操作类型

            rs=dbl.openResultSet(buff.toString());
            buff.delete(0,buff.length());
            while (rs.next()) {
                for (int i = 0; i <alRightEquitys.size(); i++) {
                    if(alRightEquitys.size()==1){
                       subTrade = (TradeSubBean) alRightEquitys.get(0);
                    }else{
                        subTrade = (TradeSubBean) alRightEquitys.get(i);
                    }
                    //根据证券代码，组合代码，交易日期，交易类型，接口读入数据，五个条件进行判断，如果存在这样的接口导入数据，那么系统中的这条权益数据要删除掉，不用再产生权益数据了
                    if(subTrade.getSecurityCode().equals(rs.getString("FSecurityCode"))
                       &&subTrade.getPortCode().equals(rs.getString("FPortCode"))
                        &&subTrade.getBargainDate().equals(rs.getDate("FBargaindate").toString())
                         &&subTrade.getTradeCode().equals(rs.getString("FTradeTypeCode"))
                           // EDIT by lidaolong 20110407 #536 有关国内接口数据处理顺序的变更  
                          &&(rs.getString("FDS").equals("ZD_QY") || rs.getString("FDS").equals("ZD_QY_T+1"))){
                        sAllDeleteNum+=subTrade.getNum()+",";//交易编号赋值
                        alRightEquitys.remove(subTrade);
                        i--;
                        if(alRightEquitys.size()==0){
                            break;
                        }
                    }
                }
            }
        }catch(Exception e){
            throw new YssException("判断接口导入数据中有没有要处理的权益信息的业务资料数据出错！",e);
        }finally{
            dbl.closeResultSetFinal(rs);
        }
        return alRightEquitys;
    }

    /**
     * checkTradeRelaHaveRightData
     * 判断接口导入数据中有没有要处理的权益信息的交易关联数据
     * @param tradeRealRightData ArrayList 保存交易关联数据的集合
     * @return ArrayList
     */
    public ArrayList checkTradeRelaHaveRightData(ArrayList tradeRealRightData) throws YssException {
        TradeRelaBean tradeReal=null;//交易关联表的javaBean
        String[] sNum=null;
        try {
            for (int i = 0; i <= tradeRealRightData.size(); i++) {
                if (tradeRealRightData.size() == 1) {
                    tradeReal = (TradeRelaBean) tradeRealRightData.get(0);
                } else {
                    if (i == tradeRealRightData.size()) {
                        break;
                    }
                    tradeReal = (TradeRelaBean) tradeRealRightData.get(i);
                }
                if(sAllDeleteNum.endsWith(",")){
                    sAllDeleteNum=sAllDeleteNum.substring(0,sAllDeleteNum.length()-1);
                }
                sNum=sAllDeleteNum.split(",");
                for(int j=0;j<sNum.length;j++){
                    if (tradeReal.getSNum().equals(sNum[j])) {
                        tradeRealRightData.remove(i);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("判断接口导入数据中有没有要处理的权益信息的交易关联数据出错！", e);
        }
        return tradeRealRightData;
    }
}


