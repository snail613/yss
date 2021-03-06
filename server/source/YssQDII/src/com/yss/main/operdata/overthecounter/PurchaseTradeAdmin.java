package com.yss.main.operdata.overthecounter;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.overthecounter.pojo.*;
import com.yss.util.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.parasetting.InvestRelaSetBean;
import com.yss.main.parasetting.SecurityBean;

/**
 * add by wangzuochun 2009.06.22
 * MS00014  国内回购业务  QDV4.1赢时胜（上海）2009年4月20日14_A
 * <p>Title: PurchaseTradeAdmin</p>
 *
 * <p>Description: 回购业务</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 */
public class PurchaseTradeAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private PurchaseTradeBean purTradeBean = null;
    private String sRecycled = "";
    public PurchaseTradeAdmin() {
    }

    public PurchaseTradeBean getPurTradeBean() {
		return purTradeBean;
	}

	public void setPurTradeBean(PurchaseTradeBean purTradeBean) {
		this.purTradeBean = purTradeBean;
	}

	public void checkInput(byte btOper) throws YssException {

    }

    /**
     * 调用PurchaseTradeBean对象的拼接字符串方法来获取数据字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        return purTradeBean.buildRowStr();
    }

    /**
     * 调用PurchaseTradeBean对象的解析方法来解析前台发送来的字符串
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        if (purTradeBean == null) {
            purTradeBean = new PurchaseTradeBean();
            purTradeBean.setYssPub(pub);
        }
        purTradeBean.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }

    /**
     * 查询出回购业务表的数据并以一定格式显示，并显示回收站的数据
     * @return String
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        String strSql = "";
        strSql = "select y.* from " +
            "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName, " +
            //edit by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A 添加 关联机构代码 关联机构名称
            " e.FSecurityName, f.FInvMgrName, k.FAffcorpName, m.FCashAccName, p.FTradeTypeName,aff.FAffCorpName as FSecIssuerName from " +
            pub.yssGetTableName("Tb_Data_Purchase") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
			
            
            //------ modify by wangzuochun  2010.07.16  MS01449    组合代码相同而启用日期不同的组合时，新建买入证券据，进行库存统计后，现金库存会增倍 QDV4赢时胜(测试)2010年7月15日01_B 
            //----------------------------------------------------------------------------------------------------
            " left join (" +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            pub.yssGetTableName("Tb_Para_Portfolio") +
//            " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            pub.yssGetTableName("Tb_Para_Portfolio") + 
            " where FCheckState = 1) d on a.FPortCode = d.FPortCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //-------------------------------------------- MS01449 -------------------------------------------//
             
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") e on a.FSecurityCode = e.FSecurityCode " +
            " left join (select r.FInvMgrCode,r.FInvMgrName from " +
            //edited by zhouxiang MS01443    在回购页面和银行间债券页面中，新建信息时出现多条相同信息    
            pub.yssGetTableName("Tb_Para_InvestManager r")//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            +"where fcheckstate=1 and fstartdate <="+dbl.sqlDate(new java.util.Date())+" group by FInvMgrCode ) s on s.FInvMgrCode=r.FInvMgrCode and s.fstartdate=r.fstartdate" 
            +" where r.fcheckstate='1'"+ 
            //----------end----------------------------------------
            ") f on a.FInvMgrCode = f.FInvMgrCode " +
            
            //edited by zhouxiang MS01501   当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据
            "left join  (select m.FAffcorpCode, m.FAffcorpName  from "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            " m join (select faffcorpcode, max(fstartdate) as fstartdate from  "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
            " m where fcheckstate = 1 "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            " group by faffcorpcode) n on m.faffcorpcode=n.faffcorpcode and m.fstartdate=n.fstartdate"+ 
            //end-- by zhouxiang MS01501    当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据 
            
            ") k on a.FAffcorpCode = k.FAffcorpCode " +
            " left join (select p.FCashAccCode,p.FCashAccName from " +
            //modify by zhangfa MS01693   20100907 启用日期不同的现金账户，引起多比回购业务数据    QDV4赢时胜(上海开发部)2010年09月03日03_B    
            pub.yssGetTableName("Tb_Para_CashAccount ") +" p"+
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            " join (select FCashAccCode, max(FStartDate) as FStartDate  from "+pub.yssGetTableName("Tb_Para_CashAccount ")+
            " where  fcheckstate = 1 "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            "group by FCashAccCode) pp on  p.fcashacccode=pp.fcashacccode  and p.fstartdate=pp.fstartdate"+
            //--------------------------------------------------------------------------------------------------------------
            ") m on a.FCashAccCode = m.FCashAccCode " +
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType) p on a.FTradeTypeCode = p.FTradeTypeCode " +
            //add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 左链接 关键机构设置 获取关联机构名称
            " left join (select FAffCorpCode,FAffCorpName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " where FCheckState = 1) aff on a.FSecIssuerCode = aff.FAffCorpCode " +
            buildFilterSql() +
            ") y order by y.FCheckState, y.FCreateTime desc";
        return this.builderListViewData(strSql);
    }

    /**
     * 以字符串的形式返回回购业务listview中显示的回购业务的相关数据
     * @param strSql String
     * @return String
     * @throws YssException
     */
    public String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";

        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.purTradeBean.setPurchaseTradeAttr(rs);
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() -
                    2);
            }
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取回购业务设置数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
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

    /**
     * 增加回购业务操作，对应前台的“新建”
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNum = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (this.purTradeBean.getNum().length() == 0) {
                strNumDate = YssFun.formatDatetime(YssFun.toDate(this.purTradeBean.
                    getBargainDate())).substring(0, 8);
                strNum = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_Purchase"),
                                           dbl.sqlRight("FNUM", 6),
                                           "000000",
                                           " where FNum like 'T"
                                           + strNumDate + "%'", 1);
                strNum = "T" + strNum;
                this.purTradeBean.setNum(strNum);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.purTradeBean.getNum().length() > 0) {

                strSql = "delete from " + pub.yssGetTableName("Tb_Data_Purchase") +
                    " where FNum = " + dbl.sqlString(this.purTradeBean.getNum());
                dbl.executeSql(strSql);
            }

            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Purchase") +
                "(FNUM,FSECURITYCODE,FPORTCODE,FINVMGRCODE,FTRADETYPECODE," +
                " FCASHACCCODE,FAffCorpCode,FBARGAINDATE,FBARGAINTIME," +
                " FSETTLEDATE,FMatureDate,FMatureSettleDate,FPurchaseGain,FPORTCURYRATE,FBASECURYRATE," +
                " FTRADEMONEY,FTotalCost,FDesc," +
                " FTradeHandleFee,FBankHandleFee,FSetServiceFee," +
                //edit by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A 添加关联机构代码
				//edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B 添加 FFixNum
                " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FFixNum,FSecIssuerCode)" +
                " values(" + dbl.sqlString(this.purTradeBean.getNum()) + "," +
                dbl.sqlString(this.purTradeBean.getSecurityCode()) + "," +
                dbl.sqlString(this.purTradeBean.getPortCode()) + "," +
                dbl.sqlString(this.purTradeBean.getInvMgrCode()) + "," +
                dbl.sqlString(this.purTradeBean.getTradeCode()) + "," +
                dbl.sqlString(this.purTradeBean.getCashAcctCode()) + "," +
                dbl.sqlString(this.purTradeBean.getAffCorpCode()) + "," +
                dbl.sqlDate(this.purTradeBean.getBargainDate()) + "," +
                dbl.sqlString(this.purTradeBean.getBargainTime()) + "," +
                dbl.sqlDate(this.purTradeBean.getSettleDate()) + "," +
                dbl.sqlDate(this.purTradeBean.getMatureDate()) + "," +
                dbl.sqlDate(this.purTradeBean.getMatureSettleDate()) + "," +
                this.purTradeBean.getPurchaseGain() + "," +
                this.purTradeBean.getPortCuryRate() + "," +
                this.purTradeBean.getBaseCuryRate() + "," +
                this.purTradeBean.getTradeMoney() + "," +
                this.purTradeBean.getTotalCost() + "," +
                dbl.sqlString(this.purTradeBean.getDesc()) + "," +
                this.purTradeBean.getTradeHandleFee() + "," +
                this.purTradeBean.getBankHandleFee() + "," +
                this.purTradeBean.getSetServiceFee() + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.purTradeBean.creatorCode) + "," +
                dbl.sqlString(this.purTradeBean.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.purTradeBean.creatorCode)) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.purTradeBean.checkTime)) + "," +
                 //edit by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
                 this.purTradeBean.getFixNum() + "," +
                 //add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A
                 dbl.sqlString(this.purTradeBean.getStrSecIssuerCode()) +
                 ")";

            dbl.executeSql(strSql);
            
            //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
            FreezeSecSetAdmin relaFreezeSecBean = new FreezeSecSetAdmin();
            relaFreezeSecBean.setYssPub(pub);
            relaFreezeSecBean.setsOldFnum(strNum);
            relaFreezeSecBean.setsFnum(strNum);
            relaFreezeSecBean.saveMutliSetting(purTradeBean.getsRelaFreezeSec());// sRelaFreezeSec
            //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增回购业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 修改回购业务操作，对应前台的“修改”
     * @return String
     * @throws YssException
     */
    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
      //----by fanghao 20100428 MS01124 QDV4赢时胜上海2010年04月23日01_B    修改业务资料的交易日期后，业务资料的交易编号没有跟着修改-------
        String strBargainDate = null; //保存成交日期
        String strSubTradeDate = null; //保存交易拆分数据流水号中的日期
        String newNum = null; //保存新的交易拆分数据流水号
        //----------------------------End MS01124-------------------------------
        Connection conn = dbl.loadConnection();
        try {
        	//--------by fanghao 20100428 MS01124 QDV4赢时胜上海2010年04月23日01_B  修改业务资料的交易日期后，业务资料的交易编号没有跟着修改    ------------
            strBargainDate = YssFun.formatDatetime(YssFun.toDate(this.purTradeBean.
                    getBargainDate())).substring(0, 8); //得到成交日期
            strSubTradeDate = this.purTradeBean.getNum().substring(1, 9); //得到交易拆分数据流水号中的日期

            //判断成交日期是否等于交易拆分数据流水号中的日期
            if (!strBargainDate.equals(strSubTradeDate)) {
                newNum = "T" + strBargainDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Purchase"),
                                           dbl.sqlRight("FNUM", 6), "000000",
                                           " where FNum like 'T" + strBargainDate + "%'", 1);

//                //得到修改日期的交易拆分数据流水号
//                newNum = newNum +
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_SubTrade"),
//                                           dbl.sqlRight("FNUM", 5), "00000",
//                                           " where FNum like '" + newNum.replaceAll("'", "''") + "%'");
            }
            //----by fanghao 20100428 MS01124 QDV4赢时胜上海2010年04月23日01_B修改业务资料的交易日期后，业务资料的交易编号没有跟着修改    -----------------------------
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_Purchase") +
            	" set FNUM= " +
            	(strBargainDate.equals(strSubTradeDate) == true ?
            	dbl.sqlString(this.purTradeBean.getNum()) : dbl.sqlString(newNum)) + //当修改成交日期时，交易拆分数据流水号中的日期要保持和成交日期一致 by fanghaoln 20100428 MS01124 QDV4赢时胜上海2010年04月23日01_B修改业务资料的交易日期后，业务资料的交易编号没有跟着修改
             	",FSECURITYCODE = " +
                dbl.sqlString(this.purTradeBean.getSecurityCode()) +
                ",FPORTCODE = " + dbl.sqlString(this.purTradeBean.getPortCode()) +
                ",FINVMGRCODE = " + dbl.sqlString(this.purTradeBean.getInvMgrCode()) +
                ",FTRADETYPECODE = " + dbl.sqlString(this.purTradeBean.getTradeCode()) +
                ",FCASHACCCODE = " +
                dbl.sqlString(this.purTradeBean.getCashAcctCode().length() == 0 ? " " :
                              this.purTradeBean.getCashAcctCode()) +
                ",FAffCorpCode = " + dbl.sqlString(this.purTradeBean.getAffCorpCode()) +
                ",FBARGAINDATE = " + dbl.sqlDate(this.purTradeBean.getBargainDate()) +
                ",FBARGAINTIME = " + dbl.sqlString(this.purTradeBean.getBargainTime()) +
                ",FSETTLEDATE = " + dbl.sqlDate(this.purTradeBean.getSettleDate()) +
                ",FMatureDate = " + dbl.sqlDate(this.purTradeBean.getMatureDate()) +
                ",FMatureSettleDate = " + dbl.sqlDate(this.purTradeBean.getMatureSettleDate()) +
                ",FPurchaseGain = " + this.purTradeBean.getPurchaseGain() +
                ",FPORTCURYRATE = " + this.purTradeBean.getPortCuryRate() +
                ",FBASECURYRATE = " + this.purTradeBean.getBaseCuryRate() +
                ",FTRADEMONEY = " + this.purTradeBean.getTradeMoney() +
                ",FTOTALCOST = " + this.purTradeBean.getTotalCost() +
                ",FDESC = " + dbl.sqlString(this.purTradeBean.getDesc()) +
                ",FTradeHandleFee = " + this.purTradeBean.getTradeHandleFee() +
                ",FBankHandleFee = " + this.purTradeBean.getBankHandleFee() +
                ",FSetServiceFee = " + this.purTradeBean.getSetServiceFee() +
                ",FCREATOR = " + dbl.sqlString(this.purTradeBean.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.purTradeBean.creatorTime) +
				//add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B
                ",FFixNum = " + dbl.sqlString(this.purTradeBean.getFixNum()) +
                //add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A
                ",FSecIssuerCode = " + dbl.sqlString(this.purTradeBean.getStrSecIssuerCode()) +
                " where FNUM = " + dbl.sqlString(this.purTradeBean.getNum());

            dbl.executeSql(strSql);
            
            //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
            FreezeSecSetAdmin relaFreezeSecBean = new FreezeSecSetAdmin();
            relaFreezeSecBean.setYssPub(pub);
            relaFreezeSecBean.setsOldFnum(this.purTradeBean.getNum());
            relaFreezeSecBean.setsFnum(strBargainDate.equals(strSubTradeDate) == true?this.purTradeBean.getNum():newNum);
            relaFreezeSecBean.saveMutliSetting(purTradeBean.getsRelaFreezeSec());// sRelaFreezeSec
            //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改回购业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 删除回购业务操作，即放入回收站，对应前台的“删除”
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_Purchase") +
                " set FCheckState = " +
                this.purTradeBean.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FNum = " +
                dbl.sqlString(this.purTradeBean.getNum());
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除回购业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 从回收站彻底删除数据,单条和多条信息都可以
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        String strSql = ""; //定义一个放SQL语句的字符串
        String[] arrData = null; //定义一个字符数组来循环删除
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
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Data_Purchase") +
                        " where FNum = " +
                        dbl.sqlString(this.purTradeBean.getNum()); //SQL语句
                    //执行sql语句
                    dbl.executeSql(strSql);
                    //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
                    FreezeSecSetAdmin relaFreezeSecBean = new FreezeSecSetAdmin();
                    relaFreezeSecBean.setYssPub(pub);
                    relaFreezeSecBean.setsOldFnum(this.purTradeBean.getNum());
                    relaFreezeSecBean.setsFnum(this.purTradeBean.getNum());
                    relaFreezeSecBean.deleteRecycleData();// sRelaFreezeSec
                    //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (this.purTradeBean.getNum() != "" && this.purTradeBean.getNum() != null) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_Purchase") +
                    " where FNum = " +
                    dbl.sqlString(this.purTradeBean.getNum()); //SQL语句
                //执行sql语句
                dbl.executeSql(strSql);
                //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
                FreezeSecSetAdmin relaFreezeSecBean = new FreezeSecSetAdmin();
                relaFreezeSecBean.setYssPub(pub);
                relaFreezeSecBean.setsOldFnum(this.purTradeBean.getNum());
                relaFreezeSecBean.setsFnum(this.purTradeBean.getNum());
                relaFreezeSecBean.deleteRecycleData();// sRelaFreezeSec
                //STORY #1509 监控管理－监控结果 添加回购冻结证券数量设置 add by jiangshichao 2011.09.17 
            }
            conn.commit(); //提交事物
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    /**
     * 可以处理回购业务设置审核、反审核、回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        String strSql = ""; //定义一个字符串来放SQL语句
        String[] arrData = null; //定义一个字符数组来循环删除
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //打开一个数据库联接
        try {
            conn.setAutoCommit(false); //开启一个事务
            bTrans = true; //代表是否关闭事务
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && (!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update " + pub.yssGetTableName("Tb_Data_Purchase") +
                        " set FCheckState = " + this.purTradeBean.checkStateId;
                    // 如果是审核操作，则获取审核人代码和审核时间
                    if (this.purTradeBean.checkStateId == 1) {
                        strSql += ", FCheckUser = '" +
                            pub.getUserCode() + "' , FCheckTime = '" +
                            YssFun.formatDatetime(new java.util.Date()) + "'";
                    }
                    strSql += " where FNum = " +
                        dbl.sqlString(this.purTradeBean.getNum());
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核回购业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    /**
     * 筛选条件
     * @return String
     */
    private String buildFilterSql() throws YssException {
        String sResult = "";
    	//20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return sResult;
    	//=============end=================
        PurchaseTradeBean filterType = this.purTradeBean.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            if (filterType.getIsOnlyColumn().equals("1") && pub.isBrown()==false) {		//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
                sResult = sResult + " and 1=2 ";
                return sResult;
            }

            if (filterType.getSecurityCode().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.getSecurityCode().replaceAll("'", "''") + "%'";
            }

            if (filterType.getPortCode().length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.getPortCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getInvMgrCode().length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.getInvMgrCode().replaceAll("'", "''") + "%'";
            }

            if (filterType.getTradeCode().length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode = '" +
                    filterType.getTradeCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getCashAcctCode().length() != 0) {
                sResult = sResult + " and a.FCashAccCode = '" +
                    filterType.getCashAcctCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getAffCorpCode().length() != 0) {
                sResult = sResult + " and a.FAffCorpCode = '" +
                    filterType.getAffCorpCode().replaceAll("'", "''") + "'";
            }

            if (filterType.getBargainDate().length() != 0 &&
                ! (filterType.getBargainDate().equals("9998-12-31") ||
                   filterType.getBargainDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(filterType.getBargainDate());
            }

            if (filterType.getSettleDate().length() != 0 &&
                ! (filterType.getSettleDate().equals("9998-12-31") ||
                   filterType.getSettleDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FSettleDate = " +
                    dbl.sqlDate(filterType.getSettleDate());
            }

            if (filterType.getMatureDate().length() != 0 &&
                ! (filterType.getMatureDate().equals("9998-12-31") ||
                   filterType.getMatureDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FMatureDate = " +
                    dbl.sqlDate(filterType.getMatureDate());
            }

            if (filterType.getMatureSettleDate().length() != 0 &&
                ! (filterType.getMatureSettleDate().equals("9998-12-31") ||
                   filterType.getMatureSettleDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FMatureSettleDate = " +
                    dbl.sqlDate(filterType.getMatureSettleDate());
            }
			//---add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B start---//
            if(filterType.getFixNum().length() != 0 && !filterType.getFixNum().equals("ALL")){
                sResult = sResult + " and a.FFixNum = '" +
                filterType.getFixNum().replaceAll("'", "''") + "'";
            }
			//---add by songjie 2011.12.19 BUG 3389 QDV4赢时胜（深圳）2011年12月13日01_B end---//
            
            //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A start---//
            if (filterType.getStrSecIssuerCode().length() != 0) {
                sResult = sResult + " and a.FSecIssuerCode = '" +
                    filterType.getStrSecIssuerCode().replaceAll("'", "''") + "'";
            }
            //---add by songjie 2012.02.17 STORY #2262 QDV4赢时胜(上海开发部)2012年02月17日01_A end---//
        }
        return sResult;
    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
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

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("GetDurability")) {
            return this.getDurability();
        }
        return buildRowStr();
    }


    /**
     * 得回购债券的期限信息
     * modify by wangzuochun 2009.08.14
     * @return String
     */
    public String getDurability() throws
        YssException {
        ResultSet rs = null;
        int iDuration = 0;              //储存回购期限
        String strDurUnit = "";         //储存回购期限单位
        java.util.Date  dBargainDate;   //成交日期
        java.util.Date  dMatureDate;    //回购到期日期
        java.util.Date  dMatureSettleDate;  //到期结算日期
        java.util.Date  dSettleDate;      //结算日期
        StringBuffer buf = new StringBuffer();
        SecurityBean secBean = new SecurityBean();
        try {
            secBean.setYssPub(pub);
            secBean.setSecurityCode(this.purTradeBean.getSecurityCode());
            secBean.getSetting();
            String strSql = "SELECT a.*,b.FInBeginType " +
                "FROM " +
                pub.yssGetTableName("Tb_Para_DepositDuration") +
                " a " +
                "LEFT JOIN " +
                pub.yssGetTableName("Tb_Para_Purchase") +
                " b ON a.FDepDurCode = b.FDepDurCode " +
                "WHERE b.FSecurityCode = " +
                dbl.sqlString(this.purTradeBean.getSecurityCode());

            rs = dbl.openResultSet(strSql);

            dBargainDate = YssFun.toDate(this.purTradeBean.getBargainDate());   //获取交易日期

            if (rs.next()) {
                iDuration = rs.getInt("FDuration");     //期间
                strDurUnit = rs.getString("FDurUnit");  //期间单位
                //获取结算日期 by zhouwei 20120509 bug4507
                dSettleDate = getSettingOper().getWorkDay(secBean.getStrHolidaysCode(), dBargainDate, secBean.getIntSettleDays());
                //modify by zhouwei 20120509 银行间回购根据结算日期推算到期日期,交易所是根据交易日推算到期日
                //根据回购品种中设置的计息起始日
                if(rs.getString("FInBeginType").equals("trade")){//交易日
                	dMatureDate = addDate(dBargainDate, iDuration, strDurUnit); //获取到期日期
                }else{
                	dMatureDate = addDate(dSettleDate, iDuration, strDurUnit); //获取到期日期
                }
//                dMatureDate = addDate(dBargainDate, iDuration, strDurUnit); //获取到期日期
//                // 取各日期的最近工作日(包含自身)
                dMatureDate = getSettingOper().getWorkDay(secBean.getStrHolidaysCode(), dMatureDate, 0);
                dMatureSettleDate = getSettingOper().getWorkDay(secBean.getStrHolidaysCode(), dMatureDate, secBean.getIntSettleDays());
                buf.append(YssFun.formatDate(dMatureDate, "yyyy-MM-dd").toString()).append("\t");
                buf.append(YssFun.formatDate(dMatureSettleDate, "yyyy-MM-dd").toString()).append("\t");
                buf.append(YssFun.formatDate(dSettleDate, "yyyy-MM-dd").toString()).append("\tnull");
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("获取证券回购期限出错!" , e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据期间单位来对日期进行处理
     * @param MatureDate Date   要处理的日期
     * @param amount int        期间
     * @param field String      期间单位
     * @return Date
     */
    public static java.util.Date addDate(java.util.Date MatureDate, int amount, String field) {

        java.util.Date date = MatureDate;

        if (field.equalsIgnoreCase("3")) {
            //3代表期间单位为年，进行年份的算术运算
            date = YssFun.addYear(MatureDate,amount);

        } else if (field.equalsIgnoreCase("2")) {
            //2代表期间单位为月，进行月份的算术运算
            date = YssFun.addMonth(MatureDate,amount);

        } else if (field.equalsIgnoreCase("1")) {
            //1代表期间单位为周，采用天进行算术运算
            date = YssFun.addDay(MatureDate,amount * 7);

        } else if (field.equalsIgnoreCase("0")) {
            //0代表期间单位为日，直接进行日的算术运算
            date = YssFun.addDay(MatureDate,amount);
        }
        return date;
    }

}
