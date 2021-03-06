package com.yss.main.operdata.overthecounter;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.overthecounter.pojo.*;
import com.yss.util.*;
import com.yss.main.funsetting.*;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.operdeal.*;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.pojo.cache.YssCost;

/**
 *
 * <p>Title: </p>
 * 债券转托管业务后台操作类，完成增删改查等功能
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: yss</p>
 *
 * @author panjunfang create 20090723
 * @version 1.0
 */
public class DevolveTrusteeAdmin
    extends BaseDataSettingBean implements IDataSetting {
    private DevolveTrusteeBean devTrustee = null;
    private String sRecycled = "";

    public DevolveTrusteeAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {
    }

    public String addSetting() throws YssException {
        String strSql = "";
        String strNum = "";
        String strNumDate = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            if (this.devTrustee.getStrTradeNo().length() == 0) {
                strNumDate = YssFun.formatDatetime(YssFun.toDate(this.devTrustee.
                    getStrBargainDate())).substring(0, 8);
                strNum = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_DevTrustBond"),
                                           dbl.sqlRight("FNUM", 9),
                                           "000000000",
                                           " where FNum like 'DTB"
                                           + strNumDate + "%'", 1);
                strNum = "DTB" + strNum;
                this.devTrustee.setStrTradeNo(strNum);
            }
            conn.setAutoCommit(false);
            bTrans = true;
            if (this.devTrustee.getStrTradeNo().length() > 0) {

                strSql = "delete from " + pub.yssGetTableName("Tb_Data_DevTrustBond") +
                    " where FNum = " + dbl.sqlString(this.devTrustee.getStrTradeNo());
                dbl.executeSql(strSql);
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_DevTrustBond") +
                "(FNUM,FSECURITYCODE,FBARGAINDATE,FInvestType," +
                " FPORTCODE,FINVMGRCODE,FAttrClsCode,FOutExchangeCode,FInExchangeCode," +
                " FPORTCURYRATE,FBASECURYRATE,FAmount,FMoney," +
                " FApprec,FDiscount,FBondIns,FinSecurityCode,FInINVESTTYPE,FInATTRCLSCODE," +//add by zhouwei 20120419 转入债券
                " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FBondTradeType)" +//xuqiji 20100413
                " values(" + dbl.sqlString(this.devTrustee.getStrTradeNo()) + "," +
                dbl.sqlString(this.devTrustee.getStrSecurityCode()) + "," +
                dbl.sqlDate(this.devTrustee.getStrBargainDate()) + "," +
                dbl.sqlString(this.devTrustee.getStrInvestTypeCode()) + "," +
                dbl.sqlString(this.devTrustee.getStrPortCode()) + "," +
                dbl.sqlString(this.devTrustee.getStrInvMgrCode().length() == 0 ? " " :
                              this.devTrustee.getStrInvMgrCode())+ "," +
                dbl.sqlString(this.devTrustee.getStrAttrClsCode().length() == 0 ? " " :
                              this.devTrustee.getStrAttrClsCode())+ "," +
                dbl.sqlString(this.devTrustee.getStrSrcExchangeCode()) + "," +
                dbl.sqlString(this.devTrustee.getStrTgtExchangeCode()) + "," +
                this.devTrustee.getDbPortCuryRate() + "," +
                this.devTrustee.getDbBaseCuryRate() + "," +
                this.devTrustee.getDbOutAmount() + "," +
                this.devTrustee.getDbOutMoney() + "," +
                this.devTrustee.getDbOutValInc() + "," +
                this.devTrustee.getDbOutDiscount() + "," +
                this.devTrustee.getDbOutInverest() + "," +
                dbl.sqlString(this.devTrustee.getInSecurityCode())+","+
                dbl.sqlString(this.devTrustee.getInInvestTypeCode())+","+
                dbl.sqlString(this.devTrustee.getInAttrClsCode())+","+
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.devTrustee.creatorCode) + "," +
                dbl.sqlString(this.devTrustee.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.devTrustee.creatorCode)) + "," +
                (pub.getSysCheckState() ? "' '" :
                //-----------------------------xuqiji 20100413--------------------------// 
                dbl.sqlString(this.devTrustee.checkTime)) + "," 
                 + dbl.sqlString(this.devTrustee.getStrBondTradeType())+ ")";
            	//------------------------------end-----------------------------------//
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "";
        } catch (Exception e) {
            throw new YssException("新增债券转托管数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        //modify by nimengjing bug#526 修改债券转托管及上市业务的成交日期时，该界面下自动产生的交易编号没有被修改
        String strNum = "";
        String strNumDate = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
           if(this.devTrustee.getStrTradeNo().length()>0){
        	   strNumDate=YssFun.formatDatetime(YssFun.toDate(this.devTrustee.
        			   getStrBargainDate())).substring(0,8);
        	   strNum = strNumDate +
               dbFun.getNextInnerCode(pub.yssGetTableName(
                   "Tb_Data_DevTrustBond"),
                                      dbl.sqlRight("FNUM", 9),
                                      "000000000",
                                      " where FNum like 'DTB"
                                      + strNumDate + "%'", 1);
              strNum = "DTB" + strNum;
           }
            strSql = "update " + pub.yssGetTableName("Tb_Data_DevTrustBond") +

                " set FNum = " + dbl.sqlString(strNum) +
         //-------------------------end bug#526------------------------------------
                ",FSECURITYCODE = " + dbl.sqlString(this.devTrustee.getStrSecurityCode()) +
                ",FBARGAINDATE = " + dbl.sqlDate(this.devTrustee.getStrBargainDate()) +
                ",FInvestType = " + dbl.sqlString(this.devTrustee.getStrInvestTypeCode()) +
                ",FPORTCODE = " + dbl.sqlString(this.devTrustee.getStrPortCode()) +
                ",FINVMGRCODE = " +
                dbl.sqlString(this.devTrustee.getStrInvMgrCode().length() == 0 ? " " :
                              this.devTrustee.getStrInvMgrCode()) +
                ",FAttrClsCode = " +
                dbl.sqlString(this.devTrustee.getStrAttrClsCode().length() == 0 ? " " :
                              this.devTrustee.getStrAttrClsCode()) +
                ",FOutExchangeCode = " + dbl.sqlString(this.devTrustee.getStrSrcExchangeCode()) +
                ",FInExchangeCode = " + dbl.sqlString(this.devTrustee.getStrTgtExchangeCode()) +
                ",FPORTCURYRATE = " + this.devTrustee.getDbPortCuryRate() +
                ",FBASECURYRATE = " + this.devTrustee.getDbBaseCuryRate() +
                ",FAmount = " + this.devTrustee.getDbOutAmount() +
                ",FMoney = " + this.devTrustee.getDbOutMoney() +
                ",FApprec = " + this.devTrustee.getDbOutValInc() +
                ",FDiscount = " + this.devTrustee.getDbOutDiscount() +
                ",FBondIns = " + this.devTrustee.getDbOutInverest() +
                ",FinSecurityCode="+dbl.sqlString(this.devTrustee.getInSecurityCode())+
                ",FInINVESTTYPE="+dbl.sqlString(this.devTrustee.getInInvestTypeCode())+//add by zhouwe 20120424 增加所属分类和投资类型字段
                ",FInATTRCLSCODE="+dbl.sqlString(this.devTrustee.getInAttrClsCode())+
                ",FCREATOR = " + dbl.sqlString(this.devTrustee.creatorCode) +
                ",FCREATETIME = " + dbl.sqlString(this.devTrustee.creatorTime) +
                ",FBondTradeType = " + dbl.sqlString(this.devTrustee.getStrBondTradeType()) + //xuqiji 20100413
                " where FNUM = " + dbl.sqlString(this.devTrustee.getStrTradeNo());

            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return buildRowStr();
        } catch (Exception e) {
            throw new YssException("修改债券转托管数据信息出错", e);
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
            strSql = "update " + pub.yssGetTableName("Tb_Data_DevTrustBond") +
                " set FCheckState = " +
                this.devTrustee.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FNum = " +
                dbl.sqlString(this.devTrustee.getStrTradeNo());
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除债券转托管业务数据信息出错", e);
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
                    strSql = "update " + pub.yssGetTableName("Tb_Data_DevTrustBond") +
                        " set FCheckState = " + this.devTrustee.checkStateId;
                    // 如果是审核操作，则获取审核人代码和审核时间
                    if (this.devTrustee.checkStateId == 1) {
                        strSql += ", FCheckUser = '" +
                            pub.getUserCode() + "' , FCheckTime = '" +
                            YssFun.formatDatetime(new java.util.Date()) + "'";
                    }
                    strSql += " where FNum = " +
                        dbl.sqlString(this.devTrustee.getStrTradeNo());
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核债券转托管业务数据信息出错", e);
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
                        pub.yssGetTableName("Tb_Data_DevTrustBond") +
                        " where FNum = " +
                        dbl.sqlString(this.devTrustee.getStrTradeNo()); //SQL语句
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if (this.devTrustee.getStrTradeNo() != null && this.devTrustee.getStrTradeNo() != "") {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_DevTrustBond") +
                    " where FNum = " +
                    dbl.sqlString(this.devTrustee.getStrTradeNo()); //SQL语句
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
                " e.FSecurityName, f.FInvMgrName, k.FExchangeName as FOutExchangeName, m.FExchangeName as FInExchangeName, " +
                " vb.FVocName as FInvestTypeName,vb2.FVocName as FBondTradeTypeName,m.FATTRCLSNAME,insec.FSecurityName as FinSecurityName,inAttr.FATTRCLSNAME as FInAttrClsName from " + //xuqiji 20100413
                pub.yssGetTableName("Tb_Data_DevTrustBond") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select m.FPortCode,m.FPortName from " +
                //edite by zhouxiang MS01437
                pub.yssGetTableName("Tb_Para_Portfolio") +" m "+ //edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio")
//                +" where fcheckstate=1 and fstartdate<="+dbl.sqlDate(new java.util.Date())+
//                "group by fportcode) n on m.fportcode =n.fportcode and m.fstartdate=n.fstartdate"+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                //end-----------------------
                " where FCheckState = 1 " +  						//xuqiji 20100413
                ") d on a.Fportcode = d.Fportcode " +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") e on a.FSecurityCode = e.FSecurityCode " +
                //--------------------------------edited by zhouxiang MS01450----------------------------
                //add by zhouwei 20120419 转入证券信息
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") insec on a.FInSecurityCode = insec.FSecurityCode " +
                //-----------end---------------------
                " left join (select r.FInvMgrCode,r.FInvMgrName from " +
                pub.yssGetTableName("Tb_Para_InvestManager") +" r "+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//                " join (select finvmgrcode,max(fstartdate) as  fstartdate from "+
//                pub.yssGetTableName("Tb_Para_InvestManager")+" where fcheckstate=1 group by finvmgrcode)"+
//                " s on r.finvmgrcode=s.finvmgrcode and r.fstartdate=s.fstartdate"+
                //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
                " where  r.FCheckState = 1) f on a.FInvMgrCode = f.FInvMgrCode " +//edit by songjie 2011.03.16 不以最大的启用日期查询数据
                //-----------------------------------end by zhouxiang MS01450----------------------------
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange) k on a.FOutExchangeCode = k.FExchangeCode " +
                " left join (select FExchangeCode,FExchangeName from Tb_Base_Exchange) m on a.FInExchangeCode = m.FExchangeCode " +
                " left join Tb_Fun_Vocabulary vb on a.FInvestType = vb.FVocCode and vb.FVocTypeCode = " +
                dbl.sqlString(YssCons.YSS_InvestType) +
                //----------------------------------------xuqiji 20100413-------------------------------------//
                " left join Tb_Fun_Vocabulary vb2 on a.FBondTradeType = vb2.FVocCode and vb2.FVocTypeCode = " 
                + dbl.sqlString(YssCons.YSS_BondTradeType) +
                " left join(select FATTRCLSCODE,FATTRCLSNAME from " + pub.yssGetTableName("tb_para_attributeclass") + " where FCheckState = 1) m on a.FATTRCLSCODE = m.FATTRCLSCODE"+
                //---------------------------------------------end--------------------------------------------//
                //add by zhouwei 20120424 转入所属分类
                " left join(select FATTRCLSCODE,FATTRCLSNAME from " + pub.yssGetTableName("tb_para_attributeclass") + " where FCheckState = 1) inAttr on a.FInATTRCLSCODE = inAttr.FATTRCLSCODE"+
                buildFilterSql() +
                ") y order by y.FCheckState, y.FCreateTime desc";
        }catch(Exception e){
             throw new YssException("获取债券转托管数据出错！" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }

    /**
     * buildFilterSql
     *筛选条件
     * @return String
     */
    private String buildFilterSql() {
        String sResult = "";
    	//20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        DevolveTrusteeBean filterType = this.devTrustee.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            if(filterType.isBShow().equals("1")&& pub.isBrown()==false){	//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
                sResult = sResult + " and 1=2";
            }
            if (filterType.getStrTradeNo().length() != 0) {
                sResult = sResult + " and a.FNum like '" +
                    filterType.getStrTradeNo().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrSecurityCode().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.getStrSecurityCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrBargainDate().length() != 0 &&
                ! (filterType.getStrBargainDate().equals("9998-12-31") ||
                   filterType.getStrBargainDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(filterType.getStrBargainDate());
            }
            if (filterType.getStrInvestTypeCode().length() != 0 &&
                !filterType.getStrInvestTypeCode().equals("99")) {
                sResult = sResult + " and a.FInvestType = '" +
                    filterType.getStrInvestTypeCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getStrPortCode().length() != 0) {
                sResult = sResult + " and a.FPortCode like '" +
                    filterType.getStrPortCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrInvMgrCode().length() != 0) {
                sResult = sResult + " and a.FInvMgrCode like '" +
                    filterType.getStrInvMgrCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrSrcExchangeCode().length() != 0) {
                sResult = sResult + " and a.FOutExchangeCode = '" +
                    filterType.getStrSrcExchangeCode().replaceAll("'", "''") + "'";
            }
            if (filterType.getStrTgtExchangeCode().length() != 0) {
                sResult = sResult + " and a.FInExchangeCode = '" +
                    filterType.getStrTgtExchangeCode().replaceAll("'", "''") + "'";
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
            //-----------------------------------xuqiji 20100413---------------------------------//
            if (filterType.getStrBondTradeType().length() != 0 && !filterType.getStrBondTradeType().equals("99")) {
                    sResult = sResult + " and a.FBondTradeType = '" +
                        filterType.getStrBondTradeType().replaceAll("'", "''") + "'";
             }
            if (filterType.getStrAttrClsCode().length() != 0 && !filterType.getStrAttrClsCode().equals(" ")) {
                sResult = sResult + " and a.FATTRCLSCODE = '" +
                    filterType.getStrAttrClsCode().replaceAll("'", "''") + "'";
            }
            //---------------------------------------end-----------------------------------------//
        }
        return sResult;
    }

    /**
     * builderListViewData
     *
     * @param strSql String
     * @return String
     */
    private String builderListViewData (String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        DevolveTrusteeBean filterType = this.devTrustee.getFilterType();//fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType +"," + YssCons.YSS_BondTradeType);//xuqiji 20100413

            sHeader = this.getListView1Headers();
          //add by yangheng 20100818 MS01310 新建时报错							//20111027 modified by liubo. STORY #1285 
            if (strSql == ""||(filterType!=null&&filterType.isBShow().equals("1") && !(pub.isBrown()))) {//fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr +
                    "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;
            }
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
			// rs = dbl.openResultSet(strSql);
			yssPageInationBean.setsQuerySQL(strSql);
			yssPageInationBean.setsTableName("DevTrustBond");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                this.devTrustee.setDevolveTrusteeAttr(rs);
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
            throw new YssException("获取债券转托管数据出错", e);
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
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (devTrustee == null) {
            devTrustee = new DevolveTrusteeBean();
            devTrustee.setYssPub(pub);
        }
        devTrustee.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }

    public String buildRowStr() throws YssException {
        return devTrustee.buildRowStr();
    }

    public String getOperValue(String sType) throws YssException {
        if (sType != null && sType.equalsIgnoreCase("calBondInterest")) { //通过点击窗口按钮计算应计转出债券利息的方法
//            BaseBondOper bondOper = null;
//            BaseOperDeal operDeal = new BaseOperDeal();
//            operDeal.setYssPub(pub);
//            YssBondIns bondIns = new YssBondIns();
//            bondOper = operDeal.getSpringRe(this.devTrustee.getStrSecurityCode(), "Sell"); //生成BaseBondOper
//            bondIns.setInsType("Sell");
//            if (bondOper == null) {
//                return "";
//            }
//            bondIns.setSecurityCode(this.devTrustee.getStrSecurityCode());
//            bondIns.setInsDate(YssFun.toDate(this.devTrustee.getStrBargainDate()));
//            bondIns.setInsAmount(this.devTrustee.getDbOutAmount());//转出数量
//            bondIns.setPortCode(this.devTrustee.getStrPortCode());
//            bondOper.setYssPub(pub);
//            bondOper.init(bondIns);
        	//modify by zhouwei 20120419 加权平均计算债券利息
        	BaseAvgCostCalculate avgCostValInc = new BaseAvgCostCalculate();
            SecPecPayBean pay = null;
            avgCostValInc.setYssPub(pub);
            avgCostValInc.initCostCalcutate(YssFun.toDate(this.devTrustee.getStrBargainDate()),
                                            this.devTrustee.getStrPortCode(),
                                            this.devTrustee.getStrInvMgrCode(),
                                            "",this.devTrustee.getStrAttrClsCode());
            pay = avgCostValInc.getCarryRecPay(this.devTrustee.getStrSecurityCode(),
                                               this.devTrustee.getDbOutAmount(),
                                               this.devTrustee.getStrTradeNo(),
                                                 "","",YssOperCons.YSS_ZJDBLX_Rec,YssOperCons.YSS_ZJDBZLX_FI_RecInterest);
            if (pay == null) {
                return "";
            }
            this.devTrustee.setDbOutInverest(pay.getMoney());//转出债券利息  
        }else if(sType != null && sType.equalsIgnoreCase("calValInc")) {//通过点击窗口按钮计算债券转出估值增值的方法
            BaseAvgCostCalculate avgCostValInc = new BaseAvgCostCalculate();
            SecPecPayBean pay = null;
            avgCostValInc.setYssPub(pub);
            avgCostValInc.initCostCalcutate(YssFun.toDate(this.devTrustee.getStrBargainDate()),
                                            this.devTrustee.getStrPortCode(),
                                            this.devTrustee.getStrInvMgrCode(),
                                            "",this.devTrustee.getStrAttrClsCode());//xuqiji 20100414
            pay = avgCostValInc.getCarryRecPay(this.devTrustee.getStrSecurityCode(),
                                               this.devTrustee.getDbOutAmount(),
                                               this.devTrustee.getStrTradeNo(),
                                                 "","",YssOperCons.YSS_ZJDBLX_MV,"");
            if (pay == null) {
                return "";
            }
            this.devTrustee.setDbOutValInc(pay.getMoney());//转出估值增值
        }else if(sType != null && sType.equalsIgnoreCase("calCarryCost")){//转出成本
            if(this.devTrustee.getStrTradeNo().length() == 0){
                String strNumDate = YssFun.formatDatetime(YssFun.toDate(this.devTrustee.
                    getStrBargainDate())).substring(0, 8);
                String sNum = strNumDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_DevTrustBond"),
                                           dbl.sqlRight("FNUM", 9),
                                           "000000000",
                                           " where FNum like 'DTB"
                                           + strNumDate + "%'", 1);
                sNum = "DTB" + sNum;
                this.devTrustee.setStrTradeNo(sNum);
            }
            ICostCalculate costCal = (ICostCalculate) pub.getOperDealCtx().getBean(
                    "avgcostcalculate");

                costCal.initCostCalcutate(YssFun.toDate(devTrustee.getStrBargainDate()),
                                          devTrustee.getStrPortCode(),
                                          devTrustee.getStrInvMgrCode(),
                                          "",
                                          this.devTrustee.getStrAttrClsCode());//xuqiji 20100414
                costCal.setYssPub(pub);
                //获取冲减的成本
                YssCost cost = costCal.getCarryCost(devTrustee.getStrSecurityCode(),
                                                    devTrustee.getDbOutAmount(),
                                                    this.devTrustee.getStrTradeNo(),
                                                    null, //null参数：add by guolongchao STORY #1207 添加结算日期参数
                                                    "devTrustbond",
                                                    YssOperCons.YSS_JYLX_Buy + "," + YssOperCons.YSS_JYLX_Sale + "," + YssOperCons.YSS_JYLX_YHJZQCX);
                costCal.roundCost(cost, 2);
                devTrustee.setDbOutMoney(cost.getCost());
        //------------------------------------------------xuqiji 20100414------------------------------------//
        }else if(sType != null && sType.indexOf("getAttrClsStorage") != -1){
        	String AttrClsCode = getAttrClsStorage();
        	return AttrClsCode;
        }
        //-------------------------------------------------end----------------------------------------------//
        //add by zhouwei 20120424 获取证券的交易所
       else if("getExchangeCode".equalsIgnoreCase(sType)){
    	  String sql="";
    	  ResultSet rs=null;
    	  String exchangeCode="";
    	  try{
    		  sql="select FEXCHANGECODE from "+pub.yssGetTableName("Tb_Para_Security")
    		     +" where fsecurityCode="+dbl.sqlString(this.devTrustee.getStrSecurityCode());
    		  rs=dbl.openResultSet(sql);
    		  if(rs.next()){
    			  exchangeCode=rs.getString("FEXCHANGECODE");
    		  }
    	  }catch (Exception e) {
    		  throw new YssException("获取证券信息出错！",e);
    	  }finally{
    		  dbl.closeResultSetFinal(rs);
    	  }
    	   return exchangeCode;
       }
        return buildRowStr();
    }
    /**
     * 获取一笔证券的所有库存的所属分类 xuqiji 20100414
     * @return
     * @throws YssException 
     */
	private String getAttrClsStorage() throws YssException {
		String AttrClsCode = "";
		ResultSet rs = null;
		StringBuffer buff = null;
		try{
			buff = new StringBuffer(200);
			buff.append(" select FATTRCLSCODE from ").append(pub.yssGetTableName("tb_stock_security"));
			buff.append(" where FSecurityCode =").append(dbl.sqlString(this.devTrustee.getStrSecurityCode()));
			buff.append(" and FStorageDate = ").append(dbl.sqlDate(YssFun.addDay(YssFun.toDate(this.devTrustee.getStrBargainDate()),-1)));
			buff.append(" and FYearMonth <>").append(dbl.sqlString(YssFun.left(YssFun.formatDate(this.devTrustee.getStrBargainDate()), 4) + "00"));
			
			rs = dbl.openResultSet(buff.toString());
			while(rs.next()){
				AttrClsCode += rs.getString("FATTRCLSCODE") + "\f\f";
			}
			if(AttrClsCode.endsWith("\f\f")){
				AttrClsCode = AttrClsCode.substring(0,AttrClsCode.length() -2);
			}
		}catch (Exception e) {
			throw new YssException("获取一笔证券的所有库存的所属分类出错！",e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return AttrClsCode;
	}
	
	/**
	 * add by huangiqrong 2012-12-25 story #2328
	 * */
	public void setDevTrustee(DevolveTrusteeBean devTrustee){
		this.devTrustee = devTrustee;
	}
}




















