package com.yss.main.operdata.overthecounter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.overthecounter.pojo.InterBankBondTradeBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import com.yss.main.parasetting.FeeBean;
import com.yss.pojo.cache.YssCost;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;
/**
 *
 * <p>Title: </p>
 * 银行间债券交易后台操作类，完成增删改查等功能
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 *
 * @author panjunfang add 20090720
 * @version 1.0
 */
public class InterBankBondTradeAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private InterBankBondTradeBean bondTrade = null;
    private String sRecycled = "";
    
    private YssCost cost = new YssCost(); //by guyichuan 2011.07.15 STORY #1146 成本
    public InterBankBondTradeAdmin() {
    }

    /**
     * 检查数据合法性
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_IntBakBond"),
                               "FNUM",
                               this.bondTrade.getStrTradeNo(),
                               this.bondTrade.getStrOldTradeNo());
    }

    public String addSetting() throws YssException {
        String strSql = "";
        String strNum = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_IntBakBond") +
                "(FNUM,FSECURITYCODE,FTradeTypeCode,FBARGAINDATE,FInvestType," +
                " FPORTCODE,FINVMGRCODE,FAttrClsCode,FCASHACCCODE,FAffCorpCode,FSettleDate," +
                " FPORTCURYRATE,FBASECURYRATE,FTradeAmount,FTradeMoney," +
                " FBondIns,FFee,FSettleFee,FBankFee,FSettleMoney," +
                //edit by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 添加 FSecIssuerCode
                " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSecIssuerCode)" +
                " values(" + dbl.sqlString(this.bondTrade.getStrTradeNo()) + "," +
                dbl.sqlString(this.bondTrade.getStrSecurityCode()) + "," +
                dbl.sqlString(this.bondTrade.getStrBusTypeCode()) + "," +
                dbl.sqlDate(this.bondTrade.getStrBargainDate()) + "," +
                dbl.sqlString(this.bondTrade.getStrInvestTypeCode()) + "," +
                dbl.sqlString(this.bondTrade.getStrPortCode()) + "," +
                dbl.sqlString(this.bondTrade.getStrInvMgrCode().length() == 0 ? " " :
                              this.bondTrade.getStrInvMgrCode())+ "," +
                dbl.sqlString(this.bondTrade.getStrAttrClsCode().length() == 0 ? " " :
                              this.bondTrade.getStrAttrClsCode())+ "," +
                dbl.sqlString(this.bondTrade.getStrCashAcctCode().length() == 0 ? " " :
                              this.bondTrade.getStrCashAcctCode()) + "," +
                dbl.sqlString(this.bondTrade.getStrAffCorpCode().length() == 0 ? " " :
                              this.bondTrade.getStrAffCorpCode()) + "," +
                dbl.sqlDate(this.bondTrade.getStrSettleDate()) + "," +
                this.bondTrade.getDbPortCuryRate() + "," +
                this.bondTrade.getDbBaseCuryRate() + "," +
                this.bondTrade.getDbTradeNum() + "," +
                this.bondTrade.getDbTradeMoney() + "," +
                this.bondTrade.getDbBbondInterest() + "," +
                this.bondTrade.getDbPoundageFee() + "," +
                this.bondTrade.getDbSettlementFee() + "," +
                this.bondTrade.getDbBankFee() + "," +
                this.bondTrade.getDbSquareFee() + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.bondTrade.creatorCode) + "," +
                dbl.sqlString(this.bondTrade.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.bondTrade.creatorCode)) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.bondTrade.checkTime)) + "," +
                 //edit by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 添加 关联机构代码
                 dbl.sqlString(this.bondTrade.getStrSecIssuerCode()) + ")";

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增银行间债券交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_IntBakBond") +

                " set FNum = " + dbl.sqlString(this.bondTrade.getStrTradeNo()) +
                ",FSECURITYCODE = " + dbl.sqlString(this.bondTrade.getStrSecurityCode()) +
                ",FTradeTypeCode = " + dbl.sqlString(this.bondTrade.getStrBusTypeCode()) +
                ",FBARGAINDATE = " + dbl.sqlDate(this.bondTrade.getStrBargainDate()) +
                ",FInvestType = " + dbl.sqlString(this.bondTrade.getStrInvestTypeCode()) +
                ",FPORTCODE = " + dbl.sqlString(this.bondTrade.getStrPortCode()) +
                ",FINVMGRCODE = " +
                dbl.sqlString(this.bondTrade.getStrInvMgrCode().length() == 0 ? " " :
                              this.bondTrade.getStrInvMgrCode()) +
                ",FAttrClsCode = " +
                dbl.sqlString(this.bondTrade.getStrAttrClsCode().length() == 0 ? " " :
                              this.bondTrade.getStrAttrClsCode()) +
                ",FCASHACCCODE = " +
                dbl.sqlString(this.bondTrade.getStrCashAcctCode().length() == 0 ? " " :
                              this.bondTrade.getStrCashAcctCode()) +
                ",FAffCorpCode = " +
                dbl.sqlString(this.bondTrade.getStrAffCorpCode().length() == 0 ? " " :
                              this.bondTrade.getStrAffCorpCode()) +
                ",FSETTLEDATE = " + dbl.sqlDate(this.bondTrade.getStrSettleDate()) +
                ",FPORTCURYRATE = " + this.bondTrade.getDbPortCuryRate() +
                ",FBASECURYRATE = " + this.bondTrade.getDbBaseCuryRate() +
                ",FTradeAmount = " + this.bondTrade.getDbTradeNum() +
                ",FTradeMoney = " + this.bondTrade.getDbTradeMoney() +
                ",FBondIns = " + this.bondTrade.getDbBbondInterest() +
                ",FFee = " + this.bondTrade.getDbPoundageFee() +
                ",FSettleFee = " + this.bondTrade.getDbSettlementFee() +
                ",FBankFee = " + this.bondTrade.getDbBankFee() +
                ",FSettleMoney = " + this.bondTrade.getDbSquareFee() +
                ",FCREATOR = " + dbl.sqlString(this.bondTrade.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.bondTrade.creatorTime) +
                //add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A
                ",FSecIssuerCode = " + dbl.sqlString(this.bondTrade.getStrSecIssuerCode()) + 
                " where FNUM = " + dbl.sqlString(this.bondTrade.getStrOldTradeNo());

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改银行间债券交易数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_IntBakBond") +
                " set FCheckState = " +
                this.bondTrade.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FNum = " +
                dbl.sqlString(this.bondTrade.getStrTradeNo());
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除银行间债券交易业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

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
                    strSql = "update " + pub.yssGetTableName("Tb_Data_IntBakBond") +
                        " set FCheckState = " + this.bondTrade.checkStateId;
                    // 如果是审核操作，则获取审核人代码和审核时间
                    if (this.bondTrade.checkStateId == 1) {
                        strSql += ", FCheckUser = '" +
                            pub.getUserCode() + "' , FCheckTime = '" +
                            YssFun.formatDatetime(new java.util.Date()) + "'";
                    }
                    strSql += " where FNum = " +
                        dbl.sqlString(this.bondTrade.getStrTradeNo());
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核银行间债券交易业务数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
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
                        pub.yssGetTableName("Tb_Data_IntBakBond") +
                        " where FNum = " +
                        dbl.sqlString(this.bondTrade.getStrTradeNo()); //SQL语句
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if ( this.bondTrade.getStrTradeNo() != null && this.bondTrade.getStrTradeNo() != "") {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_IntBakBond") +
                    " where FNum = " +
                    dbl.sqlString(this.bondTrade.getStrTradeNo()); //SQL语句
                //执行sql语句
                dbl.executeSql(strSql);
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

    public String getListViewData1() throws YssException {
        String strSql = "";//定义一个存放sql语句的字符串
        try{
            strSql = "select y.* from " +
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName, " +
                " e.FSecurityName, f.FInvMgrName, k.FAffcorpName, m.FCashAccName, p.FTradeTypeName, " +
                //edit by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 添加 关联机构代码 关联机构名称
                " vb.FVocName as FInvestTypeName, aff.FAffCorpName as FSecIssuerName from " +
                pub.yssGetTableName("Tb_Data_IntBakBond") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select x.FPortCode,x.FPortName from " +
                pub.yssGetTableName("Tb_Para_Portfolio x") +
                //MS1270 开放式基金业务界面新建一笔认购数据，保存时在未审核界面产生两笔一模一样的数据    QDV4赢时胜(测试)2010年6月4日2_B    
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " left join ( select fportcode,max(fstartdate) as fstartdate from "+
//                pub.yssGetTableName("Tb_Para_Portfolio x")+
//                " where fcheckstate=1 group by fportcode) o on o.fportcode=x.fportcode and o.fstartdate=x.fstartdate"+
//                " and x.fstartdate<="+dbl.sqlDate(new java.util.Date())+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where x.fcheckstate='1' "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据  
                //------------end----------------------------
                ") d on a.Fportcode = d.Fportcode " +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") e on a.FSecurityCode = e.FSecurityCode " +
                //edited by zhouxiang MS01443----加载最大日期的投资经理-----------------
                " left join (select m.FInvMgrCode,m.FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager m ") +
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " join (select FInvMgrCode,max(fstartdate) as fstartdate  from "
//                + pub.yssGetTableName("Tb_Para_InvestManager")+
//                " where fcheckstate=1 and fstartdate<=" +dbl.sqlDate(new java.util.Date())+
//                " group by FInvMgrCode) n on m.FInvMgrCode=n.FInvMgrCode"+
//                " and m.fstartdate=n.fstartdate"+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
               //------------end------------------------------
                " where fcheckstate=1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据  
                
                //edited by zhouxiang MS01501   当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据
                "left join  (select m.FAffcorpCode, m.FAffcorpName  from "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " m join (select faffcorpcode, max(fstartdate) as fstartdate from  "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
//                " where fcheckstate = 1 and fstartdate <="+dbl.sqlDate(new java.util.Date())+
//                " group by faffcorpcode) n on m.faffcorpcode=n.faffcorpcode and m.fstartdate=n.fstartdate"+ 
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //end-- by zhouxiang MS01501    当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据 
                
                " m where fcheckstate = 1) k on a.FAffcorpCode = k.FAffcorpCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                " left join (select FCashAccCode,FCashAccName from " +
                pub.yssGetTableName("Tb_Para_CashAccount ") +
                //edit by songjie 2011.03.18 BUG: 1379 QDV4赢时胜(上海开发部)2011年3月11日01_B
                " where FCheckState = 1) m on a.FCashAccCode = m.FCashAccCode " +
                " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType) p on a.FTradeTypeCode = p.FTradeTypeCode " +
                " left join Tb_Fun_Vocabulary vb on a.FInvestType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) +
                //add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 左链接 关键机构设置 获取关联机构名称
                " left join (select FAffCorpCode,FAffCorpName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " where FCheckState = 1) aff on a.FSecIssuerCode = aff.FAffCorpCode " + 
                buildFilterSql() +
                ") y order by y.FCheckState, y.FCreateTime desc";
        }catch(Exception e){
             throw new YssException("获取银行间债券交易数据出错！" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }

    /**
     * builderListViewDatad
     *
     * @param strSql String
     * @return String
     */
    private String builderListViewData(String strSql) throws YssException{
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        InterBankBondTradeBean filterType = this.bondTrade.getFilterType();//fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType);

            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
          //add by yangheng 20100818 MS01310 新建时报错									//20111027 modified by liubo. STORY #1285 
            if (strSql == ""||(filterType!=null&&filterType.isBShow().equals("1") && !(pub.isBrown()))) {//优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            	//---------------------------------------end-------------MS01310--------
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("IntBakBond");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.bondTrade.setIntBakBondTradeAttr(rs);
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
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\f" + "voc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
        } catch (Exception e) {
            throw new YssException("获取银行间债券交易数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * buildFilterSql
     *帅选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
    	//20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        InterBankBondTradeBean filterType = this.bondTrade.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            if(filterType.isBShow().equals("1")&& pub.isBrown()==false){	//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
                sResult = sResult + " and 1=2 ";
            }
            if (filterType.getStrTradeNo().length() != 0 &&
                ! filterType.getStrTradeNo().substring(3).equals("99981231000000000")) {
                sResult = sResult + " and a.FNum like '" +
                    filterType.getStrTradeNo().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrSecurityCode().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.getStrSecurityCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrBusTypeCode().length() != 0) {
                sResult = sResult + " and a.FTradeTypeCode like '" +
                    filterType.getStrBusTypeCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrBargainDate().length() != 0 &&
                ! (filterType.getStrBargainDate().equals("9998-12-31") ||
                   filterType.getStrBargainDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(filterType.getStrBargainDate());
            }
            if (filterType.getStrInvestTypeCode().length() != 0 &&
                !filterType.getStrInvestTypeCode().equals("99")) {
                sResult = sResult + " and a.FInvestType like '" +
                    filterType.getStrInvestTypeCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrPortCode().length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.getStrPortCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrInvMgrCode().length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.getStrInvMgrCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrCashAcctCode().length() != 0) {
                sResult = sResult + " and a.FCashAccCode = '" +
                    filterType.getStrCashAcctCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getStrAffCorpCode().length() != 0) {
                sResult = sResult + " and a.FAffCorpCode = '" +
                    filterType.getStrAffCorpCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getStrSettleDate().length() != 0 &&
                ! (filterType.getStrSettleDate().equals("9998-12-31") ||
                   filterType.getStrSettleDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FSettleDate = " +
                    dbl.sqlDate(filterType.getStrSettleDate());
            }
            if (filterType.getStrStartDate().length() != 0 &&
                ! (filterType.getStrStartDate().equals("9998-12-31") ||
                   filterType.getStrStartDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate >= " +
                    dbl.sqlDate(filterType.getStrStartDate());
            }
            if (filterType.getStrEndDate().length() != 0 &&
                ! (filterType.getStrEndDate().equals("9998-12-31") ||
                   filterType.getStrEndDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate <= " +
                    dbl.sqlDate(filterType.getStrEndDate());
            }
            //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A start---//
            if (filterType.getStrSecIssuerCode().length() != 0) {
                sResult = sResult + " and a.FSecIssuerCode = '" +
                    filterType.getStrSecIssuerCode().replaceAll("'", "''") + "'";
            }
            //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A end---//
        }
        return sResult;
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
    	//add by nimengjing bug#BUG #485 修改银行间债券业务，查看日志，日志记录不正确 
    	String strSql="";
    	ResultSet rs=null;
    	InterBankBondTradeBean bond = null;
    	try{
    		strSql="select y.* from "+
    		"(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FPortName, " +
            " e.FSecurityName, f.FInvMgrName, k.FAffcorpName, m.FCashAccName, p.FTradeTypeName, " +
            //edit by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 添加 关联机构代码 关联机构名称
            " vb.FVocName as FInvestTypeName, aff.FAffCorpName as FSecIssuerName from " +
            pub.yssGetTableName("Tb_Data_IntBakBond") + " a " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            " left join (select x.FPortCode,x.FPortName from " +
            pub.yssGetTableName("Tb_Para_Portfolio x") +
            //MS1270 开放式基金业务界面新建一笔认购数据，保存时在未审核界面产生两笔一模一样的数据    QDV4赢时胜(测试)2010年6月4日2_B 
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            " left join ( select fportcode,max(fstartdate) as fstartdate from "+
//            pub.yssGetTableName("Tb_Para_Portfolio x")+
//            " where fcheckstate=1 group by fportcode) o on o.fportcode=x.fportcode and o.fstartdate=x.fstartdate"+
//            " and x.fstartdate<="+dbl.sqlDate(new java.util.Date())+
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            " where x.fcheckstate='1' "+//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //------------end----------------------------
            ") d on a.Fportcode = d.Fportcode " +
            " left join (select FSecurityCode,FSecurityName from " +
            pub.yssGetTableName("Tb_Para_Security") +
            ") e on a.FSecurityCode = e.FSecurityCode " +
            //edited by zhouxiang MS01443----加载最大日期的投资经理-----------------
            " left join (select m.FInvMgrCode,m.FInvMgrName from " +
            pub.yssGetTableName("Tb_Para_InvestManager m ") +
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            " join (select FInvMgrCode,max(fstartdate) as fstartdate  from "
//            + pub.yssGetTableName("Tb_Para_InvestManager")+
//            " where fcheckstate=1 and fstartdate<=" +dbl.sqlDate(new java.util.Date())+
//            " group by FInvMgrCode) n on m.FInvMgrCode=n.FInvMgrCode"+
//            " and m.fstartdate=n.fstartdate"+
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
           //------------end------------------------------
            " where fcheckstate = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            
            //edited by zhouxiang MS01501   当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据
            "left join  (select m.FAffcorpCode, m.FAffcorpName  from "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            " m join (select faffcorpcode, max(fstartdate) as fstartdate from  "+pub.yssGetTableName("Tb_Para_AffiliatedCorp")+
//            " where fcheckstate = 1 and fstartdate <="+dbl.sqlDate(new java.util.Date())+
//            " group by faffcorpcode) n on m.faffcorpcode=n.faffcorpcode and m.fstartdate=n.fstartdate"+ 
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            //end-- by zhouxiang MS01501    当存在客户名称代码相同，启用日期不同的多条数据时，新建银行费用信息也会产生多条数据 
            " m where fcheckstate = 1) k on a.FAffcorpCode = k.FAffcorpCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            " left join (select FCashAccCode,FCashAccName from " +
            pub.yssGetTableName("Tb_Para_CashAccount ") +
            ") m on a.FCashAccCode = m.FCashAccCode " +
            " left join (select FTradeTypeCode,FTradeTypeName from Tb_Base_TradeType) p on a.FTradeTypeCode = p.FTradeTypeCode " +
            " left join Tb_Fun_Vocabulary vb on a.FInvestType = vb.FVocCode and vb.FVocTypeCode = " +
            dbl.sqlString(YssCons.YSS_InvestType) +
            //add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A 左链接 关键机构设置 获取关联机构名称
            " left join (select FAffCorpCode,FAffCorpName from " + pub.yssGetTableName("Tb_Para_AffiliatedCorp") + " where FCheckState = 1) aff on a.FSecIssuerCode = aff.FAffCorpCode " + 
            ") y " +
            "  where y.FNum="+dbl.sqlString(this.bondTrade.getStrOldTradeNo())+
            " order by y.FCheckState, y.FCreateTime desc" ;
    		rs=dbl.openResultSet(strSql);
    		if (rs.next()){
    			
    			bond = new InterBankBondTradeBean();
    			
    			bond.setStrTradeNo(rs.getString("FNum") + "");
    	        bond.setStrSecurityCode(rs.getString("FSecurityCode") + "");
    	        bond.setStrSecurityName(rs.getString("FSecurityName") + "");
    	        bond.setStrBusTypeCode(rs.getString("FTradeTypeCode") + "");
    	        bond.setStrBusTypeName( rs.getString("FTradeTypeName") + "");
    	        bond.setStrBargainDate(rs.getDate("FBARGAINDATE") + "");
    	        bond.setStrInvestTypeCode(rs.getString("FInvestType") + "");
    	        bond.setStrPortCode( rs.getString("FPortCode") + "");
    	        bond.setStrPortName(rs.getString("FPortName") + "");
    	        bond.setStrInvMgrCode(rs.getString("FInvMgrCode") + "");
    	        bond.setStrInvMgrName(rs.getString("FInvMgrName") + "");
    	        bond.setStrCashAcctCode(rs.getString("FCashAccCode") + "");
    	        bond.setStrCashAcctName(rs.getString("FCashAccName") + "");
    	        bond.setStrAffCorpCode(rs.getString("FAffCorpCode") + "");
    	        bond.setStrAffCorpName(rs.getString("FAffCorpName") + "");
    	        bond.setStrSettleDate(rs.getDate("FSettleDate") + "");
    	        bond.setDbPortCuryRate(rs.getDouble("FPortCuryRate"));
    	        bond.setDbBaseCuryRate(rs.getDouble("FBaseCuryRate"));
    	        bond.setDbTradeNum(rs.getDouble("FTradeAmount"));
    	        bond.setDbTradeMoney(rs.getDouble("FTradeMoney"));
    	        bond.setDbBbondInterest(rs.getDouble("FBondIns"));
    	        bond.setDbPoundageFee(rs.getDouble("FFee"));
    	        bond.setDbSettlementFee(rs.getDouble("FSettleFee"));
    	        bond.setDbBankFee(rs.getDouble("FBankFee"));
    	        bond.setDbSquareFee(rs.getDouble("FSettleMoney"));
    	        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A start---//
    	        bond.setStrSecIssuerCode(rs.getString("FSecIssuerCode"));
    	        bond.setStrSecIssuerName(rs.getString("FSecIssuerName"));
    	        //---add by songjie 2012.02.06 STORY #2190 QDV4赢时胜(上海开发部)2012年02月03日03_A end---//
    	        bond.setRecLog(rs);
    		}
    		return bond.buildRowStr();
    	}catch (Exception e) {
    		throw new YssException("获取银行间债券交易数据出错\r\n" + e.getMessage(), e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		//---------------------------end bug#485------------------------------------------------------------------
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (bondTrade == null) {
            bondTrade = new InterBankBondTradeBean();
            bondTrade.setYssPub(pub);
        } 
        bondTrade.parseRowStr(sRowStr);
        this.checkStateId=bondTrade.checkStateId;//add by nimengjing 2010.12.6 BUG #485 修改银行间债券业务，查看日志，日志记录不正确 
        sRecycled = sRowStr;
    }

    public String buildRowStr() throws YssException {
        return bondTrade.buildRowStr();
    }

    public String getOperValue(String sType) throws YssException {
        if (sType != null && sType.equalsIgnoreCase("calBondInterest")) { //通过点击窗口按钮计算应计债券利息的方法
            BaseBondOper bondOper = null;
            BaseOperDeal operDeal = new BaseOperDeal();
            operDeal.setYssPub(pub);
            YssBondIns bondIns = new YssBondIns();
            if (this.bondTrade.getStrBusTypeCode().equals(YssOperCons.YSS_JYLX_Buy) ||
                this.bondTrade.getStrBusTypeCode().equalsIgnoreCase(YssOperCons.YSS_JYLX_YHJZQCX)) {
                bondOper = operDeal.getSpringRe(this.bondTrade.getStrSecurityCode(), "Buy"); //生成BaseBondOper
                bondIns.setInsType("Buy");
            } else if (this.bondTrade.getStrBusTypeCode().equals(YssOperCons.YSS_JYLX_Sale)) {
                bondOper = operDeal.getSpringRe(this.bondTrade.getStrSecurityCode(), "Sell"); //生成BaseBondOper
                bondIns.setInsType("Sell");
            }
            if (bondOper == null) {
                return "";
            }
            bondIns.setSecurityCode(this.bondTrade.getStrSecurityCode());
            //edit by songjie 2013.04.10 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
            bondIns.setInsDate(YssFun.toDate(this.bondTrade.getStrSettleDate()));
            bondIns.setInsAmount(this.bondTrade.getDbTradeNum()); //成交数量
            bondIns.setPortCode(this.bondTrade.getStrPortCode());
            //-----20100327 蒋锦 添加 传入交易结算日期 国内 MS00955
            bondIns.setSettleDate(YssFun.toDate(this.bondTrade.getStrSettleDate()));
            //--------------------------------------//
            bondOper.setYssPub(pub);
            bondOper.init(bondIns);
            this.bondTrade.setDbBbondInterest(bondOper.calBondInterest()); //债券利息
        } else if (sType != null && sType.equalsIgnoreCase("useTradeNum")) { //获取交易编号
            return getNum();
        } else if (sType.equalsIgnoreCase("calSettleMoney")) {//计算清算金额
            return calSettleMoney();
        } else if (sType.equalsIgnoreCase("getExCost")) {// by guyichuan 2011.07.15 STORY #1146 返回成本
            return getExCost();
        }
        /**shashijie 2012-11-28 STORY 3210 计算手续费 */
        else if (sType.equalsIgnoreCase("getPoundage")) {
        	return getPoundage();
		}
		/**end shashijie 2012-11-28 STORY */

        return buildRowStr();
    }

    /**shashijie 2012-11-28 STORY 3210 计算手续费  = 成交数量 * 100 * 比率公式中所设置的比率  / 1000000
	* 若该金额大于比率公式中所设置的金额范围，则该金额就等于所设置的金额范围 */
	private String getPoundage() throws YssException {
		//金额 = 成交数量 * 100 / 1000000
		double money = YssD.div(
			YssD.mul(bondTrade.getDbTradeNum(),100),
			1000000);
		////交易费用这里固化(写死),不通过费用联接取数(胡总要求),传入:费用代码,计算金额
		double moneyValue = getMoneyFee("YSS_YHJ_JYSXF",money);
		return String.valueOf(moneyValue);
	}

	/**shashijie 2012-11-28 STORY 3210 通过费用代码,计算金额,计算费用 */
	private double getMoneyFee(String feeCode,double money) throws YssException {
		//获取费用代码对象
		FeeBean fee = new FeeBean();
		fee.setYssPub(pub);
		fee.setFeeCode(feeCode);
		fee.getSetting();
		
		//公共方法计算费用;传入-比例公式代码,舍入设置,金额,日期
		double Poundage = this.getSettingOper().calMoneyByPerExp(
				fee.getPerExpCode(), fee.getRoundCode(), 
				money,YssFun.toDate(bondTrade.getStrBargainDate()));
		return Poundage;
	}

	/**
     * 计算清算金额，如果费用为当日交收则清算金额 = 交易金额 + 债券利息 +/- 交易费用
     * @return String：清算金额
     * @throws YssException
     */
    private String calSettleMoney() throws YssException{
        double dbSettleMoney = 0;
        HashMap hmSettleFee = null;
        HashMap hmTradeFee = null;
        HashMap hmBankFee = null;
        double dbSettleFee = 0;
        double dbTradeFee = 0;
        double dbBankFee = 0;
        CtlPubPara pubPara = new CtlPubPara();
        try {
            pubPara.setYssPub(pub);
            hmSettleFee = pubPara.getIntBakSettleFee();
            hmTradeFee = pubPara.getIntBakTradeFee();
            hmBankFee = pubPara.getIntBakBankFee();

            dbSettleMoney = YssD.add(bondTrade.getDbTradeMoney(), bondTrade.getDbBbondInterest());

            if (hmSettleFee.get(bondTrade.getStrPortCode()) != null) {
                dbSettleFee = bondTrade.getDbSettlementFee();
            }
            if (hmTradeFee.get(bondTrade.getStrPortCode()) != null) {
                dbTradeFee = bondTrade.getDbPoundageFee();
            }
            if (hmBankFee.get(bondTrade.getStrPortCode()) != null) {
                dbBankFee = bondTrade.getDbBankFee();
            }


            //卖出减费用，买入加费用
            if(bondTrade.getStrBusTypeCode().equalsIgnoreCase(YssOperCons.YSS_JYLX_Sale)){
                //判断费用是否当日结算
                dbSettleFee = YssD.mul(dbSettleFee, -1);
                dbTradeFee = YssD.mul(dbTradeFee, -1);
                dbBankFee = YssD.mul(dbBankFee, -1);
            }
            dbSettleMoney = YssD.add(dbSettleMoney, dbSettleFee, dbTradeFee, dbBankFee);
        } catch (Exception ex) {
            throw new YssException("计算清算金额出错！", ex);
        }
        return String.valueOf(dbSettleMoney);
    }

    /**
     * 获取交易编号，取业务日期对应的最大编号
     * modify huangqirong 2012-12-21 story #2328 private -> public 可供非this类也可以使用
     * @return String
     * @throws YssException
     */
    public String getNum() throws YssException {
    String sFNum = "";
    String strNumDate = this.bondTrade.getStrBargainDate().substring(0,4) + this.bondTrade.getStrBargainDate().substring(5,7) + this.bondTrade.getStrBargainDate().substring(8); //格式化为 yyyyMMdd
    try {
        sFNum = "IBB" +
            strNumDate +
            dbFun.getNextInnerCode(pub.yssGetTableName(
                "Tb_Data_IntBakBond"),
                                   dbl.sqlRight("FNUM", 9), "000000001",
                                   " where FBARGAINDATE = " +
                                   dbl.sqlDate(this.bondTrade.getStrBargainDate()));
        return sFNum;
    } catch (Exception e) {
        throw new YssException("生成交易编号出错", e);
    }
}
    /**
     * by guyichuan 2011.07.15 STORY #1146
     * 	QDV4易方达2011年5月24日01_A
     * 	返回成本
     * @throws SQLException 
     */
    public String getExCost()throws YssException{
    	Connection conn=null;
    	java.sql.PreparedStatement psmt = null;
    	ResultSet rs=null;
    	try {
    		StringBuffer bufSql=new StringBuffer();
    		bufSql.append(" select FOperDate,FExchangeCost,");
    		bufSql.append(" FMExCost,FVExCost,FPortExCost,FMPortExCost,");
    		bufSql.append(" FVPortExCost,FBaseExCost,");
    		bufSql.append(" FMBaseExCost,FVBaseExCost");
    		bufSql.append(" from "+pub.yssGetTableName("Tb_Data_Integrated"));
    		bufSql.append(" where FTradeTypeCode = '02'");
    		bufSql.append(" and FSecurityCode=?");
    		bufSql.append(" and FPortCode=?");
    		bufSql.append(" and FOperDate=?");
    		
    		conn=dbl.loadConnection();
    		psmt=conn.prepareStatement(bufSql.toString());
    		psmt.setString(1, dbl.sqlString(bondTrade.getStrSecurityCode()));
    		psmt.setString(2,dbl.sqlString(bondTrade.getStrPortCode()));
    		psmt.setString(3, dbl.sqlDate(bondTrade.getStrBargainDate()));
    		
    		conn.setAutoCommit(false);
    		rs=psmt.executeQuery();
    		while(rs.next()){
    			cost.setCost(rs.getDouble("FExchangeCost")); 		//原币核算成本
    		    cost.setMCost(rs.getDouble("FMExCost")); 			//原币管理成本
    		    cost.setVCost(rs.getDouble("FVExCost")) ; 			//原币估值成本
    		    
    		    cost.setBaseCost(rs.getDouble("FBaseExCost")); 		//基础货币核算成本
    		    cost.setBaseMCost(rs.getDouble("FMBaseExCost")); 	//基础货币管理成本
    		    cost.setBaseVCost(rs.getDouble("FVBaseExCost")); 	//基础货币估值成本
    		    
    		    cost.setPortCost(rs.getDouble("FPortExCost")) ; 	//组合货币核算成本
    		    cost.setPortMCost(rs.getDouble("FMPortExCost")) ;	//组合货币管理成本
    		    cost.setPortVCost(rs.getDouble("FVPortExCost")) ;  	//组合货币估值成本
    		}
    		conn.commit();
    		conn.setAutoCommit(true);
			return this.cost.buildRowStr();
			
		} catch (YssException e) {
			e.printStackTrace();
			throw new YssException(e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new YssException(e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
    
    /**
     * add by huangqirong 2012-12-21 story #2328
     * */
    public void setBondTrade(InterBankBondTradeBean bondTrade){
    	this.bondTrade = bondTrade ;
    }

}
