package com.yss.main.operdata.futures;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class FuturesTradeAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private FuturesTradeBean ftb = new FuturesTradeBean();
    private FuturesTradeAdmin filterType;
    public static ArrayList tradeNumPool = null; //交易编号池，存放一组待使用的交易编号 sunkey 20081118 BugID:MS00013
    public FuturesTradeAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("TB_Data_FuturesTrade"),
                               "FNum", this.ftb.getNum(), this.ftb.getOldNum());
    }

    public String addSetting() throws YssException {
        String strSql = "";
//        String strNumDate = ""; 这两个变量无用，下次版本删除sunkey@Delete
//        String num = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                "(FNum, FSecurityCode,FPortCode,FBrokerCode,FInvMgrCode,FTradeTypeCode," +
                "FBegBailAcctCode,FChageBailAcctCode,FBargainDate,FBargainTime,FSettleDate,FSettleTime," +
                "FSettleType,FTradeAmount,FTradePrice,FTradeMoney,FBegBailMoney,FSettleMoney," +
                "FSettleState,FBaseCuryRate,FPortCuryRate," +
                "FFeeCode1,FTradeFee1,FFeeCode2,FTradeFee2,FFeeCode3,FTradeFee3,FFeeCode4,FTradeFee4," +
                "FFeeCode5,FTradeFee5,FFeeCode6,FTradeFee6,FFeeCode7,FTradeFee7,FFeeCode8,FTradeFee8," +
                "FDesc,FCheckState,FCreator,FCreateTime,FCheckUser) " +
                "values(" +
                dbl.sqlString(this.ftb.getNum()) + "," +
                dbl.sqlString(this.ftb.getSecurityCode()) + "," +
                dbl.sqlString(this.ftb.getPortCode()) + "," +
                dbl.sqlString(this.ftb.getBrokerCode()) + "," +
                dbl.sqlString( (this.ftb.getInvMgrCode().length() == 0) ? " " :
                              this.ftb.getInvMgrCode()) + "," +
                dbl.sqlString(this.ftb.getTradeTypeCode()) + "," +
                dbl.sqlString(this.ftb.getBegBailAcctCode()) + "," +
                dbl.sqlString(this.ftb.getChageBailAcctCode()) + "," +
                dbl.sqlDate(YssFun.toDate(this.ftb.getBargainDate())) + "," +
                dbl.sqlString(this.ftb.getBargainTime()) + "," +
                dbl.sqlDate(YssFun.toDate(this.ftb.getSettleDate())) + "," +
                dbl.sqlString(this.ftb.getSettleTime()) + "," +
                this.ftb.getSettleType() + "," +
                this.ftb.getTradeAmount() + "," +
                this.ftb.getTradePrice() + "," +
                this.ftb.getTradeMoney() + "," +
                this.ftb.getBegBailMoney() + "," +
                this.ftb.getSettleMoney() + "," +
                this.ftb.getSettleState() + "," +
                this.ftb.getBaseCuryRate() + "," +
                this.ftb.getPortCuryRate() + "," +
                this.operSql.buildSaveFeesSql(YssCons.OP_ADD, this.ftb.getFees()) +
                dbl.sqlString(this.ftb.getDesc()) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                ")";

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增股指期货交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return this.buildRowStr();
    }

    public String editSetting() throws YssException {
        String strSql = "";
        String num = "";
        String strNumDate = "";
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //判断如果修改了交易日期，则交易编号也要相应的重新生成
            if (!this.ftb.getBargainDate().equalsIgnoreCase(this.ftb.
                getOldBargainDate())) {
                //交易编号不再重新生成 因为前台将根据日期生成编号，这里生成交易编号取消
                //sunkey 20081126 BugID:MS00013
//            strNumDate = YssFun.formatDatetime(YssFun.toDate(this.ftb.
//                  getBargainDate())).substring(0, 8);
//            num = strNumDate +
//                  dbFun.getNextInnerCode(pub.yssGetTableName(
//                        "TB_Data_FuturesTrade"),
//                                         dbl.sqlRight("FNUM", 6), "000000",
//                                         " where FNum like 'T"
//                                         + strNumDate + "%'", 1);
//            num = "T" + num;
//            this.ftb.setNum(num);

                //资金调拨表中如果有以该交易编号做关联编号的记录，则一同修改成新编号
                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FRelaNum = " + dbl.sqlString(this.ftb.getNum()) +
                    " where FRelaNum = " + dbl.sqlString(this.ftb.getOldNum());
                dbl.executeSql(strSql);
            }
            strSql = "update " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                " set FNum = " + dbl.sqlString(this.ftb.getNum()) +
                ",FSecurityCode = " + dbl.sqlString(this.ftb.getSecurityCode()) +
                ",FPortCode = " + dbl.sqlString(this.ftb.getPortCode()) +
                ",FBrokerCode = " + dbl.sqlString(this.ftb.getBrokerCode()) +
                ",FInvMgrCode = " +
                dbl.sqlString(this.ftb.getInvMgrCode().trim().length() == 0 ?
                              " " : this.ftb.getInvMgrCode()) +
                ",FTradeTypeCode = " + dbl.sqlString(this.ftb.getTradeTypeCode()) +
                ",FBegBailAcctCode = " +
                dbl.sqlString(this.ftb.getBegBailAcctCode()) +
                ",FChageBailAcctCode = " +
                dbl.sqlString(this.ftb.getChageBailAcctCode()) +
                ",FBargainDate = " + dbl.sqlDate(this.ftb.getBargainDate()) +
                ",FBargainTime = " + dbl.sqlString(this.ftb.getBargainTime()) +
                ",FSettleDate = " + dbl.sqlDate(this.ftb.getSettleDate()) +
                ",FSettleTime = " + dbl.sqlString(this.ftb.getSettleTime()) +
                ",FSettleType = " + this.ftb.getSettleType() +
                ",FTradeAmount = " + this.ftb.getTradeAmount() +
                ",FTradePrice = " + this.ftb.getTradePrice() +
                ",FTradeMoney = " + this.ftb.getTradeMoney() +
                ",FBegBailMoney = " + this.ftb.getBegBailMoney() +
                ",FSettleMoney = " + this.ftb.getSettleMoney() +
                ",FSettleState = " + this.ftb.getSettleState() +
                ",FBaseCuryRate = " + this.ftb.getBaseCuryRate() +
                ",FPortCuryRate = " + this.ftb.getPortCuryRate() +
                "," +
                this.operSql.buildSaveFeesSql(YssCons.OP_EDIT, this.ftb.getFees()) +
                " FDESC = " + dbl.sqlString(this.ftb.getDesc()) +
                ",FCREATOR = " + dbl.sqlString(this.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.creatorTime) +
                " where FNUM = " + dbl.sqlString(this.ftb.getOldNum());
            dbl.executeSql(strSql);

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改股指期货交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return this.buildRowStr();
    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum = " + dbl.sqlString(this.ftb.getNum());
            bTrans = true;
            dbl.executeSql(strSql);
            //资金调拨表中如果有以该交易编号做关联编号的记录，则一同删除
            strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FRelaNum = " + dbl.sqlString(this.ftb.getNum());
            dbl.executeSql(strSql);
            //===add by xuxuming,20091125.MS00830    删除股指期货交易数据时没有删除资金调拨子表中的数据    QDV4华夏2009年11月23日01_B=====
            strSql = "update "+pub.yssGetTableName("Tb_Cash_SubTransfer")+
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
            "' where FNum in ( select FNum from "+ pub.yssGetTableName("Tb_Cash_Transfer") +
            " where FRelaNum = "+ dbl.sqlString(this.ftb.getNum())+" )";
            dbl.executeSql(strSql);//删除资金调拨子表
            //======end=================================================
            //=========add by xuxuming,20091125.MS00831    删除股指期货交易数据时没有删除综合业务中的数据    QDV4华夏2009年11月23日02_B======
            strSql = "update " + pub.yssGetTableName("Tb_Data_Integrated") +
            " set FCheckState = " + this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date())+"' "+
            " where FRelaNum = " + dbl.sqlString(this.ftb.getNum())+
            " and FNumType='FutruesTrade'";//按关联编号和编号类型来删除
            dbl.executeSql(strSql);//删除综合业务表中相关数据
            //=========end=============================================================
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除股指期货交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        Connection conn = dbl.loadConnection(); //获取一个连接
        boolean bTrans = false; //代表是否开始了事务
        String strSql = "";
        String[] arrData = null;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            arrData = this.ftb.getRecycled().split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "update " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FNum = " + dbl.sqlString(this.ftb.getNum());
                dbl.executeSql(strSql);
                //资金调拨表中如果有以该交易编号做关联编号的记录，则一同审核或反审核
                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " set FCheckState = " +
                    this.checkStateId + ", FCheckUser = " +
                    dbl.sqlString(pub.getUserCode()) +
                    ", FCheckTime = '" +
                    YssFun.formatDatetime(new java.util.Date()) + "'" +
                    " where FRelaNum = " + dbl.sqlString(this.ftb.getNum());
//                    " where FTradeNum = " + dbl.sqlString(this.ftb.getNum());//改为以交易编号来删除
                dbl.executeSql(strSql);
                //===add by xuxuming,20091125.MS00830    删除股指期货交易数据时没有删除资金调拨子表中的数据    QDV4华夏2009年11月23日01_B=====
                strSql = "update "+pub.yssGetTableName("Tb_Cash_SubTransfer")+
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
                "' where FNum in ( select FNum from "+ pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum = "+ dbl.sqlString(this.ftb.getNum())+" )";
                dbl.executeSql(strSql);//删除资金调拨子表
                //======end=================================================
                //=========add by xuxuming,20091125.MS00831    删除股指期货交易数据时没有删除综合业务中的数据    QDV4华夏2009年11月23日02_B======
                strSql = "update " + pub.yssGetTableName("Tb_Data_Integrated") +
                " set FCheckState = " + this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +"' "+
                " where FRelaNum = " + dbl.sqlString(this.ftb.getNum())+
                " and FNumType='FutruesTrade'";//按关联编号和编号类型来删除
                dbl.executeSql(strSql);//删除综合业务表中相关数据
                //=========end=============================================================
//              //===add by xuxuming,20091203.删除股指期货交易数据时,要删除资金调拨子表和资金调拨表中估值增值类型的数据==========
//                strSql = "update "+pub.yssGetTableName("Tb_Cash_SubTransfer")+
//                " set FCheckState = " + this.checkStateId +
//                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +"' "+
//                " where FNum in ( select FNum from "+ pub.yssGetTableName("Tb_Cash_Transfer") +
//                " where FTradeNum = " + dbl.sqlString(this.ftb.getSecurityCode())+//因为估值增值类型的数据,交易编号中保存的是证券代码
//                " and FTransDate ="+dbl.sqlDate(YssFun.formatDate(this.ftb.getBargainDate()))+
//                " and FSubTsfTypeCode = '09FU01'"+" )";
//                dbl.executeSql(strSql);//删除资金调拨子表            
//                strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
//                " set FCheckState = " + this.checkStateId +
//                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
//                ", FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +"' "+
//                    " where FTradeNum = " + dbl.sqlString(this.ftb.getSecurityCode())+//因为估值增值类型的数据,交易编号中保存的是证券代码
//                " and FTransDate ="+dbl.sqlDate(YssFun.formatDate(this.ftb.getBargainDate()))+
//                " and FSubTsfTypeCode = '09FU01'";//因为即使当天有多条交易数据,估值时会将数据合并,只产生一笔数据.故以此条件来删除
//                    dbl.executeSql(strSql);//删除资金调拨表
//                //===========end=================================================================
            }
            deleteCashTrans();  //删除资金调拨数据           add by zhaoxianlin 20121207 #STORY #3371 股指期货需求变更
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核股指期货交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
 /**
     *   add by zhaoxianlin 20121207 #STORY #3371 股指期货需求变更
     * @param num
     * @throws YssException
     */
    public void deleteCashTrans() throws YssException {
		String sql = "";
		String strSql= "";
		boolean bTrans = false; // 代表是否开始了事务
	    Connection conn = dbl.loadConnection();
	    ResultSet rs = null;
	    ResultSet rs2 = null;
		try{
			conn.setAutoCommit(false);
	        bTrans = true;
			sql="select Fnum  from " + pub.yssGetTableName("TB_DATA_FUTTRADESPLIT")+" where FNum like "+dbl.sqlString(this.ftb.getNum()+"%");
			rs=dbl.openResultSet(sql);
			while(rs.next()){
				deleteSplitDate(rs.getString("FNum"));//删除期货拆分表数据
				strSql="select Fnum  from " + pub.yssGetTableName("Tb_Cash_Transfer")+" where FTradeNum ="+dbl.sqlString(rs.getString("Fnum"));
				rs2 =dbl.openResultSet(strSql); 
				while(rs2.next()){
					strSql = "delete from "+ pub.yssGetTableName("Tb_Cash_SubTransfer")+" where FNum="+dbl.sqlString(rs2.getString("Fnum"));
					dbl.executeSql(strSql);
					strSql = "delete from "+ pub.yssGetTableName("Tb_Cash_Transfer")+" where FNum="+dbl.sqlString(rs2.getString("Fnum"));
					dbl.executeSql(strSql);
				}
			}
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除资金调拨数据出错",e);
		}finally{
			  dbl.closeResultSetFinal(rs);
			  dbl.endTransFinal(conn, bTrans);
		}
	}	
    /**
     *   add by zhaoxianlin 20121207 #STORY #3371 股指期货需求变更
     * @param num
     * @throws YssException
     */
    public void deleteSplitDate(String num) throws YssException {
		String strSql= "";
		boolean bTrans = false; // 代表是否开始了事务
	    Connection conn = dbl.loadConnection();
		try{
			conn.setAutoCommit(false);
	        bTrans = true;
	        strSql = "delete from "+ pub.yssGetTableName("TB_DATA_FUTTRADESPLIT")+" where FNum="+dbl.sqlString(num);
			dbl.executeSql(strSql);
			conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
		}catch(Exception e){
			throw new YssException("删除期货拆分关联表数据出错",e);
		}finally{
			  dbl.endTransFinal(conn, bTrans);
		}
	}	
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select * from " + pub.yssGetTableName("TB_Data_FuturesTrade") +
                " where FNum = " + dbl.sqlString(this.ftb.getNum());
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                this.setFuturesTradeSet(rs);
            }
            return this;
        } catch (Exception e) {
            throw new YssException("获取股指期货交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
        String strSql = "";
        Connection conn = null;
        boolean bTrans = false;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            arrData = this.ftb.getRecycled().split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                
                strSql = "delete from " +
                    pub.yssGetTableName("TB_Data_FuturesTrade") +
                    " where FNum = " + dbl.sqlString(this.ftb.getNum());
                dbl.executeSql(strSql);
              //===add by xuxuming,20091125.MS00830    删除股指期货交易数据时没有删除资金调拨子表中的数据    QDV4华夏2009年11月23日01_B=====
                strSql = "delete from "+pub.yssGetTableName("Tb_Cash_SubTransfer")+
                " where FNum in ( select FNum from "+ pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum = "+ dbl.sqlString(this.ftb.getNum())+" )";
                dbl.executeSql(strSql);//删除资金调拨子表
                //======end=================================================
                //资金调拨表中如果有以该交易编号做关联编号的记录，则一同删除
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FRelaNum = " + dbl.sqlString(this.ftb.getNum());//根据FRelaNum删除
                dbl.executeSql(strSql);                   
                //=========add by xuxuming,20091125.MS00831    删除股指期货交易数据时没有删除综合业务中的数据    QDV4华夏2009年11月23日02_B======
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
                " where FRelaNum = " + dbl.sqlString(this.ftb.getNum())+
                " and FNumType='FutruesTrade'";//按关联编号和编号类型来删除
                dbl.executeSql(strSql);//删除综合业务表中相关数据
                //=========end=============================================================
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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

    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (this.filterType != null && this.filterType.ftb.getIsOnlyColumn().equals("1")) {
            	return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql =
                "select a.*,b.FUserName as FCreatorName,c.FUserName as FCheckUserName," +
                " d.FPortName,e.FSecurityName,f.FInvMgrName,g.FTradeTypeName,h.FBrokerName as FBrokerName," +
                " o.FCashAccName as FBegBailAcctName,p.FCashAccName as FChageBailAcctName " +
                " from " + pub.yssGetTableName("TB_DATA_FUTURESTRADE") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                //----------------------------------------------------------------------------------------------------
                // modify by fangjiang 2010.08.20 选取启用日期最大的组合
                " left join " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") + " where FStartDate <= " + dbl.sqlDate(new java.util.Date()).toString() +
//                " and FCheckState = 1 group by FPortCode) da join " +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " (select FPortCode, FPortName, FStartDate, FPortCury from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FCheckState = 1) d on a.FPortCode = d.FPortCode" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----------------------------------------------------------------------------------------------------
                " left join (select FSecurityCode,FSecurityName,FStartDate,FHandAmount,FFactor " +
                " from " + pub.yssGetTableName("Tb_Para_Security") + //modify by sunkey BugNO:MS00306 将写死的TB_001修改成动态生成
                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                " and FCheckState = 1) e on a.FSecurityCode = e.FSecurityCode " +
                //----------------------------------------------------------------------------------------------------
                
                //edited by zhouxiang MS01450 新建信息时，出现多条信息-------------------------------------------------
                " left join (select r.FInvMgrCode, r.FInvMgrName, r.FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_InvestManager") + " r "+
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                " join (select finvmgrcode,max(fstartdate) as  fstartdate from "+
//                pub.yssGetTableName("Tb_Para_InvestManager")+" where fcheckstate=1 group by finvmgrcode)"+
//                " s on r.finvmgrcode=s.finvmgrcode and r.fstartdate=s.fstartdate"+
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " +
                " r.FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //edn    by zhouxiang MS01450 新建信息时，出现多条信息-------------------------------------------------
                
                //----------------------------------------------------------------------------------------------------
                " left join (select FTradeTypeCode, FTradeTypeName " +
                " from Tb_Base_TradeType " +
                " where FCheckState = 1) g on a.FTradeTypeCode = g.FTradeTypeCode " +
                //----------------------------------------------------------------------------------------------------
                " left join (select FBrokerCode, FBrokerName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_Broker") + //modify by sunkey BugNO:MS00306 将写死的TB_001修改成动态生成
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " +
                " FCheckState = 1) h on a.FBrokerCode = h.FBrokerCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //----------------------------------------------------------------------------------------------------
                " left join (select FCashAccCode, FCashAccName, FStartDate " +
                " from " + pub.yssGetTableName("Tb_Para_CashAccount") + //modify by sunkey BugNO:MS00306 将写死的TB_001修改成动态生成
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " +
                " FCheckState = 1) o on a.FBegBailAcctCode = o.FCashAccCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                //----------------------------------------------------------------------------------------------------
                " left join (select FCashAccCode, FCashAccName, FStartDate " +
                " from  " + pub.yssGetTableName("Tb_Para_CashAccount") + //modify by sunkey BugNO:MS00306 将写死的TB_001修改成动态生成
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " where " +
                " FCheckState = 1) p on a.fChageBailAcctCode = p.FCashAccCode " +
                //----edit by songjie 2011.03.15 不以最大的启用日期查询数据----//
                this.buildFilterSql() + //" order by a.FPortCode";
                "order by a.FbrokerCode,a.Fnum";//modified by zhaoxianlin 20121207 #STORY #3371 股指期货需求变更
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
           
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("FUTURESTRADE");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                //---------------------------------------------------------------------
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.setFuturesTradeSet(rs);
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
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr();//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取股指期货交易数据信息出错：" + e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
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
            this.ftb.setRecycled(sRowStr); //把未解析的字符串先赋给sRecycled
            reqAry = sTmpStr.split("\t");
            this.ftb.setNum(reqAry[0]);
            this.ftb.setSecurityCode(reqAry[1]);
            this.ftb.setPortCode(reqAry[2]);
            this.ftb.setBrokerCode(reqAry[3]);
            this.ftb.setInvMgrCode(reqAry[4]);
            this.ftb.setTradeTypeCode(reqAry[5]);
            this.ftb.setBegBailAcctCode(reqAry[6]);
            this.ftb.setChageBailAcctCode(reqAry[7]);
            this.ftb.setBargainDate(reqAry[8]);
            this.ftb.setBargainTime(reqAry[9]);
            this.ftb.setSettleDate(reqAry[10]);
            this.ftb.setSettleTime(reqAry[11]);
            if (YssFun.isNumeric(reqAry[12])) {
                this.ftb.setSettleType(Integer.parseInt(reqAry[12]));
            }
            if (YssFun.isNumeric(reqAry[13])) {
                this.ftb.setTradeAmount(Double.parseDouble(reqAry[13]));
            }
            if (YssFun.isNumeric(reqAry[14])) {
                this.ftb.setTradePrice(Double.parseDouble(reqAry[14]));
            }
            if (YssFun.isNumeric(reqAry[15])) {
                this.ftb.setTradeMoney(Double.parseDouble(reqAry[15]));
            }

            if (YssFun.isNumeric(reqAry[16])) {
                this.ftb.setBegBailMoney(Double.parseDouble(reqAry[16]));
            }
            if (YssFun.isNumeric(reqAry[17])) {
                this.ftb.setSettleMoney(Double.parseDouble(reqAry[17]));
            }
            if (YssFun.isNumeric(reqAry[18])) {
                this.ftb.setSettleState(Integer.parseInt(reqAry[18]));
            }
            if (YssFun.isNumeric(reqAry[19])) {
                this.ftb.setBaseCuryRate(Double.parseDouble(reqAry[19]));
            }
            if (YssFun.isNumeric(reqAry[20])) {
                this.ftb.setPortCuryRate(Double.parseDouble(reqAry[20]));
            }
            this.ftb.setFees(reqAry[21].replaceAll("~", "\t"));
            this.ftb.setDesc(reqAry[22]);
            this.ftb.setOldNum(reqAry[23]);
            this.ftb.setOldBargainDate(reqAry[24]);
            this.ftb.setIsOnlyColumn(reqAry[25]);
            this.checkStateId = YssFun.toInt(reqAry[26]);
            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new FuturesTradeAdmin();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析股指期货交易数据信息出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.ftb.getNum()).append("\t");
        buf.append(this.ftb.getSecurityCode()).append("\t");
        buf.append(this.ftb.getSecurityName()).append("\t");
        buf.append(this.ftb.getPortCode()).append("\t");
        buf.append(this.ftb.getPortName()).append("\t");
        buf.append(this.ftb.getBrokerCode()).append("\t");
        buf.append(this.ftb.getBrokerName()).append("\t");
        buf.append(this.ftb.getInvMgrCode()).append("\t");
        buf.append(this.ftb.getInvMgrName()).append("\t");
        buf.append(this.ftb.getTradeTypeCode()).append("\t");
        buf.append(this.ftb.getTradeTypeName()).append("\t");
        buf.append(this.ftb.getBegBailAcctCode()).append("\t");
        buf.append(this.ftb.getBegBailAcctName()).append("\t");
        buf.append(this.ftb.getChageBailAcctCode()).append("\t");
        buf.append(this.ftb.getChageBailAcctName()).append("\t");
        buf.append(this.ftb.getBargainDate()).append("\t");
        buf.append(this.ftb.getBargainTime()).append("\t");
        buf.append(this.ftb.getSettleDate()).append("\t");
        buf.append(this.ftb.getSettleTime()).append("\t");
        buf.append(this.ftb.getSettleType()).append("\t");
        buf.append(this.ftb.getTradeAmount()).append("\t");
        buf.append(this.ftb.getTradePrice()).append("\t");
        buf.append(this.ftb.getTradeMoney()).append("\t");
        buf.append(this.ftb.getBegBailMoney()).append("\t");
        buf.append(this.ftb.getSettleMoney()).append("\t");
        buf.append(this.ftb.getSettleState()).append("\t");
        buf.append(this.ftb.getBaseCuryRate()).append("\t");
        buf.append(this.ftb.getPortCuryRate()).append("\t");
        buf.append(this.ftb.getFees()).append("\t");
        buf.append(this.ftb.getDesc()).append("\t");
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    public String getOperValue(String sType) throws YssException {
        if (sType != null && sType.equalsIgnoreCase("getPC_BZJ")) { //取平仓保证金 by leeyu080907
            return getPCBZJ(ftb) + "";
        } else if (sType != null && sType.equalsIgnoreCase("calcPCSY")) { //计算平仓收益
            return getPCSY(ftb) + "";
        } else if (sType != null && sType.equalsIgnoreCase("useTradeNum")) { //获取使用交易编号 sunkey 20081118 BugID:MS00013
            return getTradeNum(false);
        } else if (sType != null && sType.equalsIgnoreCase("getCost")){//add by yanghaiming 20101027 QDV4深圳赢时胜2010年9月28日01_A   获取单笔交易成本
        	return getCost();
        } else if (sType != null && sType.equalsIgnoreCase("getType")){
        	return getType();
        }
        /**shashijie 2012-8-1 STORY 2830 获取费用*/
        else if (sType != null && sType.equalsIgnoreCase("getSelect")) {
        	return this.getSelectFee();
		}
		/**end*/
        return "";
    }

    /**shashijie 2012-8-1 STORY 2830 */
    public String getSelectFee() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ArrayList arr = new ArrayList();
		StringBuffer bufShow = new StringBuffer();
		StringBuffer bufAll = new StringBuffer();
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);
		String sShowDataStr = "";
		String sAllDataStr = "";
		String sHeader = "费用名称\t费用金额"; // 标题
		double dFeeMoney;
		double dTotalFee = 0;
		DecimalFormat format = new DecimalFormat("#,##0.##");
		try {
			strSql = "select * from " + pub.yssGetTableName("TB_Data_FuturesTrade")
					+ " where FNum = "+dbl.sqlString(this.ftb.getNum()) ;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				for (int i = 1; i <= 8; i++) {
					if (rs.getString("FFeeCode" + i) != null
							&& rs.getString("FFeeCode" + i).trim().length() > 0) {
						fee.setFeeCode(rs.getString("FFeeCode" + i));
						fee.getSetting();
						if (fee != null) {
							arr.add(fee);
						}
						dFeeMoney = rs.getDouble("FTradeFee" + i);
						dTotalFee = YssD.add(dTotalFee, dFeeMoney);
						bufShow.append(fee.getFeeName()).append("\t");
						bufShow.append(format.format(dFeeMoney)).append(
								YssCons.YSS_LINESPLITMARK);
						bufAll.append(fee.getFeeCode()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll.append(fee.getFeeName()).append(
								YssCons.YSS_ITEMSPLITMARK2);
						bufAll
								.append(
										YssFun.formatNumber(dFeeMoney,
												"###0.##")).append(
										YssCons.YSS_ITEMSPLITMARK2);
						bufAll.append(fee.buildRowStr()).append(
								YssCons.YSS_LINESPLITMARK);
					}
				}
			}
			if (arr.size() != 0) {
				bufShow.append("Total: ").append("\t");
				bufShow.append(format.format(dTotalFee)).append(
						YssCons.YSS_LINESPLITMARK);
				bufAll.append("total").append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append("Total: ").append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append(YssFun.formatNumber(dTotalFee, "###0.##"))
						.append(YssCons.YSS_ITEMSPLITMARK2);
				bufAll.append("").append(YssCons.YSS_LINESPLITMARK);
			}
			if (bufShow.toString().length() > 2) {
				sShowDataStr = bufShow.toString().substring(0,
						bufShow.toString().length() - 2);
			}
			if (bufAll.toString().length() > 2) {
				sAllDataStr = bufAll.toString().substring(0,
						bufAll.toString().length() - 2);
			}
			return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
    
    private String buildFilterSql() throws YssException {
        String sResult = "";
        if (this.filterType != null) {
            sResult = " where 1=1";
            if (this.filterType.ftb.getIsOnlyColumn().equals("1")) {
                sResult = sResult + " and 1=2 ";
                return sResult;
            }
            if (this.filterType.ftb.getTradeTypeCode().length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.ftb.getTradeTypeCode().replaceAll("'", "''") + "'";
            }
            if (this.filterType.ftb.getBargainDate().length() != 0 &&
                !this.filterType.ftb.getBargainDate().equals("9998-12-31")) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(YssFun.toDate(this.filterType.ftb.getBargainDate()));
            }
            if (this.filterType.ftb.getSecurityCode().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.ftb.getSecurityCode().replaceAll("'", "''") + "'";
            }
            if (this.filterType.ftb.getInvMgrCode().length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.ftb.getInvMgrCode().replaceAll("'", "''") + "%'";
            }
            //=== add by guyichuan  20110316  #552 南方东英2011年01月19日01_A
            if (this.filterType.ftb.getPortCode().length() !=0){
                sResult = sResult + " and a.FPortCode = '" +
                filterType.ftb.getPortCode().replaceAll("'", "''") + "'";
            }
        }
        return sResult;
    }

    private void setFuturesTradeSet(ResultSet rs) throws SQLException,
        YssException {
        this.ftb.setNum(rs.getString("FNum"));
        this.ftb.setSecurityCode(rs.getString("FSecurityCode"));
        this.ftb.setSecurityName(rs.getString("FSecurityName"));
        this.ftb.setPortCode(rs.getString("FPortCode"));
        this.ftb.setPortName(rs.getString("FPortName"));
        this.ftb.setBrokerCode(rs.getString("FBrokerCode"));
        this.ftb.setBrokerName(rs.getString("FBrokerName"));
        this.ftb.setInvMgrCode(rs.getString("FInvMgrCode"));
        this.ftb.setInvMgrName(rs.getString("FInvMgrName"));
        this.ftb.setTradeTypeCode(rs.getString("FTradeTypeCode"));
        this.ftb.setTradeTypeName(rs.getString("FTradeTypeName"));
        this.ftb.setBegBailAcctCode(rs.getString("FBegBailAcctCode"));
        this.ftb.setBegBailAcctName(rs.getString("FBegBailAcctName"));
        this.ftb.setChageBailAcctCode(rs.getString("FChageBailAcctCode"));
        this.ftb.setChageBailAcctName(rs.getString("FChageBailAcctName"));
        this.ftb.setBargainDate(rs.getString("FBargainDate"));
        this.ftb.setBargainTime(rs.getString("FBargainTime"));
        this.ftb.setSettleDate(rs.getString("FSettleDate"));
        this.ftb.setSettleTime(rs.getString("FSettleTime"));
        this.ftb.setSettleType(rs.getInt("FSettleType"));
        this.ftb.setTradeAmount(rs.getDouble("FTradeAmount"));
        this.ftb.setTradePrice(rs.getDouble("FTradePrice"));
        this.ftb.setTradeMoney(rs.getDouble("FTradeMoney"));
        this.ftb.setBegBailMoney(rs.getDouble("FBegBailMoney"));
        this.ftb.setSettleMoney(rs.getDouble("FSettleMoney"));
        this.ftb.setSettleState(rs.getInt("FSettleState"));
        this.ftb.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
        this.ftb.setPortCuryRate(rs.getDouble("FPortCuryRate"));
        this.loadFees(rs);
        this.ftb.setDesc(rs.getString("FDesc"));
        super.setRecLog(rs);
    }

    public void loadFees(ResultSet rs) throws SQLException, YssException {
        String sName = "";
        double dFeeMoney = 0;
        double dTotalFee = 0;
        StringBuffer buf = new StringBuffer();
        FeeBean fee = new FeeBean();
        fee.setYssPub(pub);

        for (int i = 1; i <= 8; i++) {
            if (rs.getString("FFeeCode" + i) != null &&
                rs.getString("FFeeCode" + i).trim().length() > 0) {
                fee.setFeeCode(rs.getString("FFeeCode" + i));
                fee.getSetting();
                //------ add by wangzuochun 2010.09.11 MS01708    交易结算未结算中进行结算时会报错    QDV4建行2010年09月08日01_B    
                //------ 根据交易子表中的费用代码去查费用设置中的费用，若费用不存在，则跳过此次循环；
                if (fee.getFeeCode() == null){
                	continue;
                }
                //----------MS01708-----------//
                sName = fee.getFeeName();
                if (rs.getString("FTradeFee" + i) != null) {
                    dFeeMoney = rs.getDouble("FTradeFee" + i);
                }
                dTotalFee = YssD.add(dTotalFee, dFeeMoney);
                buf.append(rs.getString("FFeeCode" + i)).append("\n");
                buf.append(sName).append("\n");
                buf.append(dFeeMoney).append("\n");
                buf.append(fee.buildRowStr().replaceAll("\t", "~")).append("\f\n");
            }
        }
        if (buf.toString().length() > 2) {
            buf.append("total").append("\n");
            buf.append("Total: ").append("\n");
            buf.append(dTotalFee).append("\n");
            fee.setAccountingWay("0"); //不计入成本
            buf.append(fee.buildRowStr().replaceAll("\t", "~"));
            this.ftb.setFees(buf.toString());
        } else {
            this.ftb.setFees("");
        }
    }

    /**
     * 计算平仓收益
     * @param closeTrade FuturesTradeBean
     * @return double
     * @throws YssException
     */
    private double getPCSY(FuturesTradeBean closeTrade) throws YssException {
        double dbQSK = 0; //清算款
        ArrayList alTrade = null;
        FuturesTradeBean trade = null;
        String strSql = "";
        ResultSet rs = null;
        String sFuType = ""; //期货类型
        double dbMultiple = 0; //放大倍数
        try {
            //获取库存余额
            alTrade = getLastStock(YssFun.toDate(closeTrade.getBargainDate()), closeTrade.getPortCode(), closeTrade.getSecurityCode());
            //--------------获取期货品种信息-------------//
            strSql = "SELECT * FROM " + pub.yssGetTableName("Tb_Para_IndexFutures") +
                " WHERE FSecurityCode = " + dbl.sqlString(closeTrade.getSecurityCode());
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sFuType = rs.getString("FFUType");
                dbMultiple = rs.getDouble("FMultiple");
            } else {
                throw new YssException("期货【" + closeTrade.getSecurityCode() + "】品种信息已被删除，请确认！");
            }
            dbl.closeResultSetFinal(rs);
            //-----------------------------------------//
            if (alTrade.size() == 0) {
                throw new YssException("期货【" + closeTrade.getSecurityCode() + "】库存不足，请检查期货库存！");
            }
            //平仓数量
            double dbCloseAmount = closeTrade.getTradeAmount();
            for (int i = alTrade.size() - 1; i >= 0; i--) {
                trade = (FuturesTradeBean) alTrade.get(i);
                if (trade.getTradeAmount() >= dbCloseAmount) {
                    //多头
                    if (sFuType.equalsIgnoreCase("BuyAM")) {
                        //原币投资收益 = (平仓价格 - 开仓价格) * 平仓数量 * 放大倍数
                        dbQSK = YssD.add(dbQSK,
                                         YssD.round(YssD.mul(YssD.mul(YssD.sub(
                                             closeTrade.
                                             getTradePrice(), trade.getTradePrice()),
                            dbMultiple), dbCloseAmount), 2));
                    }
                    //空头
                    else {
                        //原币投资收益 = (开仓价格 - 平仓价格) * 平仓数量 * 放大倍数
                        dbQSK = YssD.add(dbQSK,
                                         YssD.round(YssD.mul(YssD.mul(YssD.sub(trade.
                            getTradePrice(),
                            closeTrade.getTradePrice()),
                            dbMultiple), dbCloseAmount), 2));
                    }
                    dbCloseAmount = 0;
                    break;
                }
                //如果平仓数量大于开仓交易的库存数量
                else {
                    //平仓数量减去开仓数量
                    dbCloseAmount = YssD.sub(dbCloseAmount, trade.getTradeAmount());
                    //多头
                    if (sFuType.equalsIgnoreCase("BuyAM")) {
                        dbQSK = YssD.add(dbQSK,
                                         YssD.round(YssD.mul(
                                             YssD.mul(YssD.sub(closeTrade.
                            getTradePrice(),
                            trade.getTradePrice()),
                            dbMultiple),
                                             trade.getTradeAmount()), 2));
                    }
                    //空头
                    else {
                        dbQSK = YssD.add(dbQSK,
                                         YssD.round(YssD.mul(
                                             YssD.mul(YssD.sub(trade.getTradePrice(),
                            closeTrade.getTradePrice()),
                            dbMultiple),
                                             trade.getTradeAmount()), 2));
                    }
                    //开仓交易被完全平仓，库存数量为 0
                    trade.setCloseAmount(0);
                }
            }
            if (dbCloseAmount > 0) {
                throw new YssException("平仓数量大于库存数量，请检查交易库存！");
            }
        } catch (Exception e) {
            throw new YssException("计算平仓收益出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dbQSK;
    }

    /**
     * 计算保证金
     * @param trade FuturesTradeBean
     * @return double
     * @throws YssException
     * xuqiji 20090710:QDV4中金2009年06月03日01_A  MS00481  满足能随时调整股指期货初始保证金金额的功能
     */
    public double getPCBZJ(FuturesTradeBean closeTrade) throws YssException {
        double dBZJ = 0; //保证金额
        String sql = "";
        ResultSet rs = null;
        try {
            sql = this.getTheDatePCData(closeTrade); //获取当天股指期货交易数据,当天有初始保证金调整,

            rs = dbl.openResultSet(sql);
            if (rs.next()) {
				//变动保证金不为0，并且是每手固定保证金才进行保证金计算
            	/**start modify by huangqirong 2013-7-5 Bug #8439   FChangeMoney -> FBailFix */
            	//if(rs.getDouble("FChangeMoney")!=0&&rs.getString("FBailType").equalsIgnoreCase("Fix")){
                if(rs.getDouble("FBailFix")!=0&&rs.getString("FBailType").equalsIgnoreCase("Fix")){				
                    //dBZJ = YssD.mul(closeTrade.getTradeAmount(), rs.getDouble("FChangeMoney")); //保证金=数量*调整保证金每首固定
                    dBZJ = YssD.mul(closeTrade.getTradeAmount(), rs.getDouble("FBailFix")); //保证金=数量*调整保证金每首固定
                /**end modify by huangqirong 2013-7-5 Bug #8439 */
                } else {
                    if (rs.getString("FBailType").equals("Scale")) { //保证金=交易数量 * 成交价格 * 放大倍数 * 保证金比例
                        dBZJ = YssD.mul(closeTrade.getTradeAmount(), closeTrade.getTradePrice(),
                                        rs.getDouble("FMultiple"), rs.getDouble("FBailScale"));
                    } else { //保证金=数量*每首固定，
                        dBZJ = YssD.mul(closeTrade.getTradeAmount(), rs.getDouble("FBailFix"));
                    }
                }
            }
        } catch (Exception ex) {
            throw new YssException("计算保证金出错\n" + ex.toString());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return dBZJ;
    }

    /**
     * 获取当天初始保证金调整数据和期货信息中数据
     * @param closeTrade FuturesTradeBean
     * @return String
     * xuqiji 20090710:QDV4中金2009年06月03日01_A  MS00481  满足能随时调整股指期货初始保证金金额的功能
     */
    private String getTheDatePCData(FuturesTradeBean closeTrade) throws YssException {
        StringBuffer buff = null;
        try {
            //逻辑：
            //1.通过期货信息表左连接保证金调整表，从而确保能查询出期货的信息
            //2.通过保证金调整表自连查询出最近一次的保证金调整，保证之前的调整有效
            //3.保证金调整表查询出的数据通过日期、组合代码筛选
            //modify by sunkey 20090714
            buff = new StringBuffer();
            buff.append(" select ft.*,fu.* from ");
            buff.append(pub.yssGetTableName("tb_para_indexfutures")).append(" ft ");//期货信息设置表
            buff.append(" left join (select e2.* from  ");
            buff.append(" (select max(FChangeDate) as FChangeDate,fsecuritycode, FportCode from ");
            buff.append(pub.yssGetTableName("tb_data_futurebailchange"));//保证金调整表
            buff.append(" where FCheckState = 1 and FChangeDate <=").append(dbl.sqlDate(closeTrade.getBargainDate()));
            buff.append(" and FportCode =").append(dbl.sqlString(closeTrade.getPortCode()));
            buff.append(" and FSecurityCode =").append(dbl.sqlString(closeTrade.getSecurityCode()));
            buff.append(" group by fsecuritycode, FportCode) e1");
            buff.append(" join (select * from ");
            buff.append(pub.yssGetTableName("tb_data_futurebailchange"));//保证金调整表
            buff.append(" where FCheckState = 1 ");
            buff.append(" and FportCode =").append(dbl.sqlString(closeTrade.getPortCode()));
            buff.append(" and FSecurityCode =").append(dbl.sqlString(closeTrade.getSecurityCode()));
            buff.append(" ) e2 on e1.FChangeDate =e2.FChangeDate");
            buff.append(" and e1.fsecuritycode =e2.fsecuritycode and e1.FportCode = e2.fportcode");
            buff.append(" ) fu on ft.fsecuritycode =fu.fsecuritycode");
            buff.append(" where  ft.fsecuritycode =").append(dbl.sqlString(closeTrade.getSecurityCode()));

        } catch (Exception e) {
            throw new YssException("获取当天初始保证金调整数据和期货信息中数据！", e);
        }
        return buff.toString();
    }

    /**
     * 生成获取当日的交易记录的 SQL 语句
     * @param dWorkDate Date：业务日期
     * @param sPortCode String：组合代码
     * @param sSecurityCode String：期货代码
     * @param sAccountType String：核算类型
     * @return String
     * @throws YssException
     */
    private String createSQLForGetTheDayTrade(java.util.Date dWorkDate,
                                              String sPortCode,
                                              String sSecurityCode,
                                              String sAccountType) throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        try {
            sqlBuf.append("SELECT FNum, FSecurityCode, FPortCode, FTradeAmount, FBegBailMoney, FTradeTypeCode");
            sqlBuf.append(" FROM " + pub.yssGetTableName("TB_Data_FuturesTrade"));
            sqlBuf.append(" WHERE FCheckState = 1");
            sqlBuf.append(" AND FBargainDate = " + dbl.sqlDate(dWorkDate));
            sqlBuf.append(" AND FPortCode = " + dbl.sqlString(sPortCode));
            sqlBuf.append(" AND FSecurityCode = " + dbl.sqlString(sSecurityCode));
            if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
                //如果使用当天优先先入先出法，默认将开仓交易都放在前面计算
                sqlBuf.append(" ORDER BY FTradeTypeCode, FNUM");
            } else {
                sqlBuf.append(" ORDER BY FNUM");
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sqlBuf.toString();
    }

    /**
     * 生成获取昨日库存的 SQL 语句
     * @param dWorkDate Date：T 日
     * @param sPortCode String：组合代码
     * @param sSecurityCode String：期货代码
     * @param sAccountType String：核算类型
     * @return String：SQL
     * @throws YssException
     */
    private String createSQLForGetYesterdayStock(java.util.Date dWorkDate,
                                                 String sPortCode,
                                                 String sSecurityCode,
                                                 String sAccountType) throws YssException {
        StringBuffer sqlBuf = new StringBuffer();
        try {
            if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG)) {
                sqlBuf.append(
                    " SELECT FNum, FTsfTypeCode, FTransDate, FStorageAmount, FBailMoney, ");
                sqlBuf.append(" FNum AS FSecurityCode ");
                sqlBuf.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Futtraderela"));
                sqlBuf.append(" WHERE FTransDate = ").append(dbl.sqlDate(YssFun.addDay(dWorkDate, -1)));
                sqlBuf.append(" AND FSettleState = 1 ");
                sqlBuf.append(" AND FTsfTypeCode = ").append(dbl.sqlString(YssOperCons.YSS_ZJDBZLX_FU01_COST));
                sqlBuf.append(" AND FStorageAmount <> 0 ");
                sqlBuf.append(" AND FNum = ").append(dbl.sqlString(sSecurityCode));
                sqlBuf.append(" AND FPortCode = ").append(dbl.sqlString(sPortCode));
            } else {
                sqlBuf.append("SELECT *");
                sqlBuf.append(
                    " FROM (SELECT FNum, FTsfTypeCode, FTransDate, FStorageAmount, FBailMoney");
                sqlBuf.append(" FROM ").append(pub.yssGetTableName(
                    "Tb_Data_Futtraderela"));
                sqlBuf.append(" WHERE FTransDate = ").append(dbl.sqlDate(YssFun.
                    addDay(dWorkDate, -1)));
                sqlBuf.append(" AND FSettleState = 1"); //已结算
                sqlBuf.append(" AND FTsfTypeCode = ").append(dbl.sqlString(
                    YssOperCons.YSS_ZJDBZLX_FU01_MV)); //股指期货估值增值
                sqlBuf.append(" AND FStorageAmount <> 0) rela");
                sqlBuf.append(" JOIN (SELECT FNUM, FSecurityCode, FTradePrice");
                sqlBuf.append(" FROM ").append(pub.yssGetTableName(
                    "TB_Data_FuturesTrade"));
                sqlBuf.append(" WHERE FCheckState = 1");
                sqlBuf.append(" AND FPortCode = ").append(dbl.sqlString(
                    sPortCode));
                sqlBuf.append(" AND FSecurityCode = ").append(dbl.sqlString(
                    sSecurityCode));
                sqlBuf.append(" ) trade ON rela.FNum = trade.FNum");
                sqlBuf.append(" ORDER BY rela.FNUM DESC");
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sqlBuf.toString();
    }

    /**
     * 使用组合代码获取期货核算类型
     * MS00321 QDV4招商证券2009年03月17日01_A
     * 2009.03.18 蒋锦 添加
     * @param sPortCode String：组合代码
     * @param htPortAccountType Hashtable：核算类型组合对
     * @return String：核算类型
     * @throws YssException
     */
    private String getAccountTypeBy(String sPortCode) throws YssException {
        //默认使用先入先出
        String sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_FIFO;
        String sModAvg = "";
        //存放组合、核算代码对
        Hashtable htPortAccountType = new Hashtable();
        String sTheDayFirstFIFO = "";
        try {
            CtlPubPara pubPara = new CtlPubPara();
            pubPara.setYssPub(pub);
//            htPortAccountType = pubPara.getFuturesAccountType();
            htPortAccountType = pubPara.getFurAccountType("AccoutType");//add by xuxuming,20091223.MS00886,无法用不同的方法对不同品种进行核算成本
            sTheDayFirstFIFO = (String) htPortAccountType.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO);
            sModAvg = (String) htPortAccountType.get(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG);
            if (sTheDayFirstFIFO != null && sTheDayFirstFIFO.indexOf(sPortCode) != -1) {
                sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO;
            } else if (sModAvg != null && sModAvg.indexOf(sPortCode) != -1) {
                sAccountType = YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG;
            }

        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sAccountType;
    }

    /**
     * 计算股指期货当前剩余库存
     * @param dWorkDate Date：计算日期
     * @param sPortCode String：组合代码
     * @param sSecurityCode String：期货代码
     * @return ArrayList
     * @throws YssException
     */
    private ArrayList getLastStock(java.util.Date dWorkDate, String sPortCode, String sSecurityCode) throws YssException {
        ArrayList alResult = new ArrayList();
        FuturesTradeBean trade = null;
        String strSql = "";
        //期货核算类型
        String sAccountType = "";
        ResultSet rs = null;
        try {
            sAccountType = getAccountTypeBy(sPortCode);
            strSql = createSQLForGetYesterdayStock(dWorkDate, sPortCode, sSecurityCode, sAccountType);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                trade = new FuturesTradeBean();
                trade.setNum(rs.getString("FNum")); //交易编号
                trade.setTradeAmount(rs.getDouble("FStorageAmount")); //库存数量
                trade.setBegBailMoney(rs.getDouble("FBailMoney")); //保证金
                alResult.add(trade);
                trade = null;
            }
            dbl.closeResultSetFinal(rs);

            strSql = createSQLForGetTheDayTrade(dWorkDate,
                                                sPortCode,
                                                sSecurityCode,
                                                sAccountType);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                if (rs.getString("FTradeTypeCode").equalsIgnoreCase(YssOperCons.YSS_JYLX_KC)) { //开仓
                    //使用加权平均法计算保证金获取的是昨日库存和今天开仓交易的保证金总数来计算平仓保证金
                    if (sAccountType.equalsIgnoreCase(YssOperCons.YSS_FUTURES_ACCOUNTTYPE_MODAVG) &&
                        alResult.size() > 0) {
                        trade = (FuturesTradeBean) alResult.get(0);
                        trade.setTradeAmount(YssD.add(rs.getDouble("FTradeAmount"), trade.getTradeAmount()));
                        trade.setBegBailMoney(YssD.add(rs.getDouble("FBegBailMoney"), trade.getBegBailMoney()));
                    } else {
                        trade = new FuturesTradeBean();
                        trade.setNum(rs.getString("FNum")); //交易编号
                        trade.setTradeAmount(rs.getDouble("FTradeAmount")); //库存
                        trade.setBegBailMoney(rs.getDouble("FBegBailMoney")); //保证金
                        if (sAccountType.equalsIgnoreCase(YssOperCons.
                            YSS_FUTURES_ACCOUNTTYPE_THEDAYFIRSTFIFO)) {
                            alResult.add(alResult.size() - (rs.getRow() - 1),
                                         trade);
                        } else {
                            alResult.add(0, trade);
                        }
                        trade = null;
                    }
                } else { //平仓
                    double dbCloseAmount = rs.getDouble("FTradeAmount"); //交易数量
                    double dbMoney = rs.getDouble("FBegBailMoney"); //初始保证金金额
                    for (int i = alResult.size() - 1; i >= 0; i--) {
                        trade = (FuturesTradeBean) alResult.get(i);
                        if (trade.getTradeAmount() < dbCloseAmount) { //如果平仓的交易数量>交易数量
                            alResult.remove(i);
                            dbCloseAmount -= trade.getTradeAmount(); //库存 = 平仓数量-原有数量
                            dbMoney -= trade.getBegBailMoney(); //初始保证金 = 平仓初始保证金 - 原有的初始保证金
                        } else if (trade.getTradeAmount() > dbCloseAmount) {
                            trade.setBegBailMoney(trade.getBegBailMoney() - dbMoney); //
                            trade.setTradeAmount(trade.getTradeAmount() - dbCloseAmount);
                        } else {
                            alResult.remove(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("计算期货【" + sSecurityCode + "】" +
                                   YssFun.formatDate(dWorkDate, "yyyy-MM-dd") +
                                   " 日库存出错！\r\n" + e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alResult;
    }

    /**
     * 获取成交编号，自动生成,格式 T+yyyyMMdd+000000
     * 为了避免用户同时操作时造成编号的同时使用，采用编号池原理，
     * 每个成交编号从编号池中取，如果用户取消了操作，将成交编号退回到编号池
     * 编号池原理：从数据库总找到下一个编号，然后再生成4个编号，将编号保存到编号池
     * 每次从编号池中取最小的编号，如果编号池中为空，则重新创建编号。
     * @param bReadOnly boolean 标识该编号是只读的，还是可以作为成交编号实际使用的。
     * @return String 成交编号
     * @throws YssException
     * @author sunkey 20081118 BugID:MS00013
     */
    public String getTradeNum(boolean bReadOnly) throws YssException {
        if (tradeNumPool == null) {
            tradeNumPool = new ArrayList();
        }
        String strNumDate = YssFun.formatDatetime(new java.util.Date()).substring(0, 8); //格式化过的日期 yyyyMMdd
        //如果编号池中存在非当日的编号，将非当日的编号删除
        for (int i = 0; i < tradeNumPool.size(); i++) {
            if (!tradeNumPool.get(i).toString().substring(1, 9).equals(strNumDate)) {
                tradeNumPool.remove(i);
            }
        }
        //判断交易编号池中是否还有编号存在，如果没有则获取新的编号5个
        if (tradeNumPool.size() == 0) {
            int tmpNum = 0;
            String num = dbFun.getNextInnerCode(pub.yssGetTableName(
                "TB_Data_FuturesTrade"), dbl.sqlRight("FNUM", 6), "000000",
                                                " where FNum like 'T" + strNumDate + "%'", 10); //编号
            tmpNum = YssFun.toInt(num);
            for (int i = 0; i < 5; i++) {
                tradeNumPool.add("T" + strNumDate + YssFun.formatNumber(tmpNum, "000000"));
                tmpNum += 10;
            }
        }
        //将交易编号池中最小的数据返回
        String tmpTradeNum = tradeNumPool.get(0).toString();
        int minFlag = 0;
        int minNum = YssFun.toInt(tmpTradeNum.substring(tmpTradeNum.length() - 6, tmpTradeNum.length()));
        for (int i = 0; i < tradeNumPool.size(); i++) {
            tmpTradeNum = tradeNumPool.get(i).toString();
            if (minNum > YssFun.toInt(tmpTradeNum.substring(tmpTradeNum.length() - 6, tmpTradeNum.length()))) {
                minNum = YssFun.toInt(tmpTradeNum.substring(tmpTradeNum.length() - 6, tmpTradeNum.length()));
                minFlag = i;
            }
        }
        tmpTradeNum = tradeNumPool.get(minFlag).toString(); //获取编号池中最小的编号
        if (!bReadOnly) { //如果获取的不是只读，要进行实际使用时，从编号池中删除
            tradeNumPool.remove(minFlag); //将编号从编号池中删除
        }
        return tmpTradeNum;
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
    
    
    private String getCost() throws YssException{
    	StringBuffer strResult = new StringBuffer();
    	String strSql = "";
    	ResultSet rs = null;
    	try{
    		strSql = "select * from " + pub.yssGetTableName("TB_Data_FutTradeRela")
    				+ " where FCLOSENUM = " + dbl.sqlString(this.ftb.getNum()); //modify by fangjiang 2011.02.21
    		rs = dbl.openResultSet(strSql);
    		if (rs.next()){
    			strResult.append(rs.getDouble("FMONEY")).append("\t");
    			strResult.append(rs.getDouble("FBASECURYMONEY")).append("\t");
    			strResult.append(rs.getDouble("FPORTCURYMONEY"));
    		}
    		return strResult.toString();
    	}catch (Exception e){
    		throw new YssException(e.getMessage());
    	}finally {
            dbl.closeResultSetFinal(rs);
        }    	
    }
    
  //add by yanghaiming 20101120 
    private String getType() throws YssException{
    	String strResult = "0";
    	String strSql = "";
    	ResultSet rs = null;
    	try{
    		strSql = "select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    		" where FPubParaCode = 'AccountType' and FParaGroupCode = 'futures' and FCtlGrpCode = 'FuturesAccountType'" +
		    		" and FParaId = 1 and FCTLCODE = 'Combo' and FPARAID = (select max(FPARAID) from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
		    		" where FPubParaCode = 'AccountType' and FParaGroupCode = 'futures' and FCtlGrpCode = 'FuturesAccountType' and substr(FCTLVALUE,0,instr(FCTLVALUE,'|')-1) = " + 
		    		dbl.sqlString(this.ftb.getPortCode()) + ")";
    		rs = dbl.openResultSet(strSql);
    		if (rs.next()){
    			if("MODAVG,1".equalsIgnoreCase(rs.getString("FCTLVALUE"))){
    				strResult = "1";//如果为移动加权则返回1
    			}
    		}
    		return strResult;
    	}catch (Exception e){
    		throw new YssException(e.getMessage());
    	}finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
