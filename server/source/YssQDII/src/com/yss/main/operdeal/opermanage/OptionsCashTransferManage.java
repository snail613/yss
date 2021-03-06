package com.yss.main.operdeal.opermanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.main.cashmanage.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;
import com.yss.manager.CashTransAdmin;

/**
 * <p>Title:by xuqiji 20090626 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持 </p>
 *
 * <p>Description: 对期权业务的处理-生成变动保证金与初始保证金之间的划转,主要是产生资金调拨数据</p>
 *
 * <P> xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OptionsCashTransferManage extends BaseOperManage{
    private ArrayList cashTransArr = null; //存放期权交易关联表数据
    private String securityCodes = ""; //存放期权代码
    public OptionsCashTransferManage() {
    }

    /**
     * 初始化信息
     * @param dDate Date 处理日期
     * @param portCode String 组合代码
     * @throws YssException
     */
    public void initOperManageInfo(Date dDate, String portCode) throws YssException {
        this.dDate = dDate; //调拨日期
        this.sPortCode = portCode; //组合
    }

    /**
     * 生成变动保证金与初始保证金之间的划转,主要是产生资金调拨数据,插入到表：Tb_XXX_Cash_Transfer
     * 和Tb_XXX_Cash_SubTransfer
     * @throws YssException
     */
    public void doOpertion() throws YssException {
        createCashTransfer(); //生成当日的资金调拨
    }

    /**
     * createCashTransfer 生成当日的资金调拨
     */
    private void createCashTransfer() throws YssException {
        cashTransArr = getTheDayTradeData();//查询期权交易关联表数据的方法，返回ArrayList
        if (cashTransArr.size() > 0) {//当天有交易，则产生资金调拨数据
            saveCashTransferData(cashTransArr);
        }
    }

    /**
     * saveCashTransferData 把数据插入到资金调拨表和资金调拨子表
     *
     * @param cashTransArr ArrayList
     */
    private void saveCashTransferData(ArrayList cashTransData) throws YssException {
        CashTransAdmin cashtrans = null;//初始化资金调拨操作类
        String filtersRelaNums = "";//保存交易编号
        boolean bTrans = false;//事务控制
        Connection conn = dbl.loadConnection();//获取连接
        try {
            if (cashTransData.size() > 0) {//是否当天有交易
                cashtrans = new CashTransAdmin();
                cashtrans.setYssPub(pub);
                cashtrans.addList(cashTransData);//子资金调拨的值放入TransferBean中的arrayList中
                //增加事务控制和锁，以免在多用户同时处理时出现调拨编号重复
                conn.setAutoCommit(false);//设置不自动提交事务
                bTrans = true;
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_Transfer"));//给操作的表加锁
                dbl.lockTableInEXCLUSIVE(pub.yssGetTableName("Tb_Cash_SubTransfer"));
                
                cashtrans.insert(this.dDate,YssOperCons.YSS_ZJDBZLX_COST_FACT,"", this.sPortCode,"");//插入数据到资金调拨表和子表中
                
                conn.commit();//提交事务
                conn.setAutoCommit(true);//设置为自动提交事务
                bTrans = false;
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 查询期权交易关联表数据，把数据set到TransferBean和TransferSetBean中
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getTheDayTradeData() throws YssException {
        ArrayList curCashTransArr = null;//保存资金调拨数据
        TransferBean transfer = null;//调拨类初始化
        TransferSetBean transferset = null;//调拨子类初始化
        ArrayList subtransfer = null;//保存资金子调拨
        ResultSet rs = null;
        boolean analy1;//分析代码1
        boolean analy2;//分析代码2
        boolean analy3;//分析代码3
        StringBuffer buff = null;
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  start
        EachGetPubPara pubPara = new EachGetPubPara();;
        pubPara.setYssPub(pub);
        String sCostAccount_Para="";//是否核算成本参数
        //STORY #863 香港、美国股指期权交易区别  期权需求变更 add by jiangshichao 2011.06.18  end
        try{
        	//分析代码
            analy1 = operSql.storageAnalysis("FAnalysisCode1", "Cash");
            analy2 = operSql.storageAnalysis("FAnalysisCode2", "Cash");
            analy3 = operSql.storageAnalysis("FAnalysisCode3", "Cash");
            buff=new StringBuffer();
            buff.append(" select * from ").append(pub.yssGetTableName("tb_data_optionstraderela"));//期权交易数据关联表
            //STORY #863 香港、美国股指期权交易区别  期权需求变更 modify by jiangshichao 2011.06.18  start
            // 这里把买入开仓，买入平仓数据也查询出来。通过sCostAccount_Para来控制，是否产生资金调拨
            buff.append(" where FCloseNum in ('01','02','03','04','05','06','07') and FPortCode in(")
            //STORY #863 香港、美国股指期权交易区别  期权需求变更 modify by jiangshichao 2011.06.18  end 
                .append(this.operSql.sqlCodes(this.sPortCode)).append(")");
            buff.append(" and FBargainDate=").append(dbl.sqlDate(this.dDate));

            rs=dbl.queryByPreparedStatement(buff.toString());//从期权交易关联表中获取数据
            buff.delete(0,buff.length());
            curCashTransArr = new ArrayList();
            while(rs.next()){
            	
            	pubPara.setSPortCode(rs.getString("FPortCode"));
                pubPara.setSPubPara(rs.getString("fsecuritycode"));
                pubPara.setsDate(YssFun.formatDate(rs.getDate("fbargaindate")));
                //20120816 added by liubo.Story #2754
                //与钱有关的项的判断，不在判断通参的“是否核算成本”的值，改为判断“是否有资金流动”的值
                //=================================
                pubPara.setCtlFlag("selTransfering");
                pubPara.setTradeType(rs.getString("FTradeTypeCode"));
                //==============end===================
                sCostAccount_Para = pubPara.getOptCostAccountSet();
                
                if((sCostAccount_Para.equalsIgnoreCase("true")&&rs.getString("FCloseNum").equals("01"))||(sCostAccount_Para.equalsIgnoreCase("true")&&rs.getString("FCloseNum").equals("02"))){
                	continue;
                }
                
                if(rs.getString("FCloseNum").equals("03")||rs.getString("FCloseNum").equals("01")){//状态为：卖出状态-开仓
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "IN");//设置调拨表的方法
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "KC");//设置调拨子表的方法
                    subtransfer.add(transferset);
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "KCWITHFEE");
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                }else{//状态为：卖出状态-平仓
                    subtransfer = new ArrayList();
                    transfer = setTransferAttr(rs, "IN");
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCBailIn");
                    subtransfer.add(transferset);
                    transferset = setTransferSetAttr(rs, analy1, analy2, analy3,
                        "PCBailOut");
                    subtransfer.add(transferset);
                    transfer.setSubTrans(subtransfer);
                    curCashTransArr.add(transfer);
                }               
            }
        }catch(Exception e){
            throw new YssException("查询期权交易关联表数据出错！\r\t",e);
        }finally{
        	dbl.closeResultSetFinal(rs);//关闭游标 by leeyu 20100909
        }
        return curCashTransArr;
    }

    /**
     * 设置资金调拨子表数据出错
     * @param rs ResultSet
     * @param analy1 boolean
     * @param analy2 boolean
     * @param analy3 boolean
     * @param type String
     * @return TransferSetBean
     * @throws YssException
     */
    private TransferSetBean setTransferSetAttr(ResultSet rs, boolean analy1,
                                               boolean analy2, boolean analy3,
                                               String type) throws YssException {
        TransferSetBean transferset = new TransferSetBean();
        double dBaseRate = 0;//基础汇率
        double dPortRate = 0;//组合汇率
        double money = 0.0;//调拨金额
        SecurityBean security = null;//证券信息类
        
        try {
        	
            dBaseRate = rs.getDouble("FBaseCuryRate");
            dPortRate = rs.getDouble("FPortCuryRate");

            security = new SecurityBean();
            security.setYssPub(pub);
            security.setSecurityCode(rs.getString("FSecurityCode"));//证券代码
            security.getSetting();

            transferset.setSPortCode(rs.getString("FPortCode"));//设置组合代码
            if (analy1) {
               transferset.setSAnalysisCode1(rs.getString("FInvMgrCode"));//投资经理
            }
            if (analy2) {
               transferset.setSAnalysisCode2(rs.getString("FBrokerCode"));//券商代码
            }
            if (type.equalsIgnoreCase("PCBailIN")) { //平仓变动保证金流入
                transferset.setSCashAccCode(rs.getString("FChageBailAcctCode"));
                money = rs.getDouble("FBegBailMoney");//调拨金额
                transferset.setIInOut(1);//调拨方向
            }
            else if (type.equalsIgnoreCase("PCBailOut")) { //平仓初始保证金流出
               transferset.setSCashAccCode(rs.getString("FBegBailAcctCode"));
               money = rs.getDouble("FBegBailMoney");
               transferset.setIInOut(-1);
           }else if (type.equalsIgnoreCase("KC")) { //开仓的设置，流出。
              transferset.setSCashAccCode(rs.getString("FChageBailAcctCode")); //变动保证金，流
              money = rs.getDouble("FBegBailMoney");
              
              
              transferset.setIInOut(-1);
           }
           else if (type.equalsIgnoreCase("KCWITHFEE")) { //开仓的设置，流入。
              transferset.setSCashAccCode(rs.getString("FBegBailAcctCode")); //初始保证金，流入
              money = rs.getDouble("FBegBailMoney");
              transferset.setIInOut(1);
           }
           transferset.setDMoney(money);
           transferset.setDBaseRate(dBaseRate);
           transferset.setDPortRate(dPortRate);

           transferset.checkStateId = 1;

        } catch (Exception e) {
            throw new YssException("设置资金调拨子表数据出错！", e);
        }
        return transferset;
    }

    /**
     * 设置调拨数据
     * @param rs ResultSet
     * @param type String 类型
     * @return TransferBean
     * @throws YssException
     */
    private TransferBean setTransferAttr(ResultSet rs, String type) throws
        YssException {
        TransferBean transfer = new TransferBean();
        try {
            transfer.setDtTransferDate(rs.getDate("FBargainDate"));//调拨日期
            transfer.setDtTransDate(rs.getDate("FBargainDate"));//业务日期
            if (type.equalsIgnoreCase("IN")) { //帐户间的内部流动设置。
                transfer.setStrTsfTypeCode("01");
                transfer.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_FACT);
            }
            transfer.setFRelaNum(rs.getString("FNum"));//编号
            transfer.setStrTradeNum(rs.getString("FNum"));
            transfer.setStrSecurityCode(rs.getString("FSecurityCode"));//证券代码
            securityCodes += transfer.getStrSecurityCode() + ",";

            transfer.checkStateId = 1;

        } catch (Exception ex) {
            throw new YssException("设置资金调拨数据出错!", ex);
        }
        return transfer;
    }
}
