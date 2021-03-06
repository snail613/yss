package com.yss.main.operdata;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdata.pojo.*;
import com.yss.util.*;

import java.math.BigDecimal;

/**
 * <p>Title: </p>
 * 货币基金每万份收益率
 * <p>Description: </p>
 * MS00013 国内基金业务 QDV4.1赢时胜（上海）2009年4月20日13_A
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 * yss
 * @author panjunfang create 20090810
 * @version 1.0
 */
public class MonetaryFundInsAdmin
    extends BaseDataSettingBean implements IDataSetting{
    private MonetaryFundInsBean mFundIns = null;
    private String sRecycled = "";

    public MonetaryFundInsAdmin() {
    }

    /**
     * 检查数据合法性
     * @param btOper byte
     * @throws YssException
     */
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Data_MonFundInterest"),
                               "FSecurityCode,FBargainDate",
                               this.mFundIns.getStrSecurityCode() + "," + this.mFundIns.getStrBargainDate(),
                               this.mFundIns.getStrOldSecurityCode() + "," + this.mFundIns.getStrOldBargainDate());
    }

    /**
     * 新增货币基金每万份收益率
     * @return String
     * @throws YssException
     */
    public String addSetting() throws YssException {
        String strSql = "";
        String strNum = "";
        String strNumDate = "";
        boolean bTrans = true; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_MonFundInterest") +
                "(FSecurityCode,FReadDate,FBargainDate,FFundRate,FDesc," +
                " FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime)" +
                " values(" + dbl.sqlString(this.mFundIns.getStrSecurityCode()) + "," +
                dbl.sqlDate(this.mFundIns.getStrReadDate()) + "," +
                dbl.sqlDate(this.mFundIns.getStrBargainDate()) + "," +
                this.mFundIns.getBdFundRate() + "," +
                dbl.sqlString(this.mFundIns.getStrDesc()) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(this.mFundIns.creatorCode) + "," +
                dbl.sqlString(this.mFundIns.creatorTime) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.mFundIns.creatorCode)) + "," +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.mFundIns.checkTime)) + ")";
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("新增货币基金每万份收益率数据信息出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = true; //代表是否回滚事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Data_MonFundInterest") +
                " set FSecurityCode = " + dbl.sqlString(this.mFundIns.getStrSecurityCode()) +
                ", FReadDate = " + dbl.sqlDate(this.mFundIns.getStrReadDate()) +
                ", FBargainDate = " + dbl.sqlDate(this.mFundIns.getStrBargainDate()) +
                ", FFundRate = " + this.mFundIns.getBdFundRate() +
                ", FDesc = " + dbl.sqlString(this.mFundIns.getStrDesc()) +
                ", FCHECKSTATE = " + (pub.getSysCheckState() ? "0" : "1") +
                ", FCreator = " + dbl.sqlString(this.mFundIns.creatorCode) +
                ", FCreateTime = " + dbl.sqlString(this.mFundIns.creatorTime) +
                ", FCheckUser = " +
                (pub.getSysCheckState() ? "' '" :
                 dbl.sqlString(this.mFundIns.creatorCode)) +
                " where FSecurityCode = " + dbl.sqlString(this.mFundIns.getStrOldSecurityCode()) +
                " and FBargainDate = " + dbl.sqlDate(this.mFundIns.getStrOldBargainDate());
            //事物控制
            conn.setAutoCommit(false);
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改货币基金每万份收益率数据出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return null;
    }

    public void delSetting() throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("Tb_Data_MonFundInterest") +
                " set FCheckState = " +
                this.mFundIns.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FSecurityCode = " + dbl.sqlString(this.mFundIns.getStrSecurityCode()) +
                " and FBargainDate = " + dbl.sqlDate(this.mFundIns.getStrBargainDate());
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除货币基金每万份收益率数据出错", e);
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
            if ( sRecycled != null&&(!sRecycled.equalsIgnoreCase(""))) { //判断传来的内容是否为空
                arrData = sRecycled.split("\r\n"); //解析它，把它还原成条目放在数组里。
                for (int i = 0; i < arrData.length; i++) { //循环数组，也就是循环还原条目
                    if (arrData[i].length() == 0) {
                        continue; //如果数组里没有内容就执行下一个内容
                    }
                    this.parseRowStr(arrData[i]); //解析这个数组里的内容
                    strSql = "update " + pub.yssGetTableName("Tb_Data_MonFundInterest") +
                        " set FCheckState = " + this.mFundIns.checkStateId;
                    // 如果是审核操作，则获取审核人代码和审核时间
                    if (this.mFundIns.checkStateId == 1) {
                        strSql += ", FCheckUser = '" +
                            pub.getUserCode() + "' , FCheckTime = '" +
                            YssFun.formatDatetime(new java.util.Date()) + "'";
                        this.checkStateId=1;//modify add guojianhua 20100907
                    }
                    strSql += " where FSecurityCode = " +
                        dbl.sqlString(this.mFundIns.getStrSecurityCode()) +
                        " and FBargainDate = " + dbl.sqlDate(YssFun.toDate(YssFun.formatDate(this.mFundIns.getStrBargainDate(),"yyyy-MM-dd")));
                    dbl.executeSql(strSql); //执行更新操作
                }
            }
            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核货币基金每万份收益率数据出错", e);
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
                        pub.yssGetTableName("Tb_Data_MonFundInterest") +
                        " where FSecurityCode = " +
                        dbl.sqlString(this.mFundIns.getStrSecurityCode()) +
                        " and FBargainDate = " + dbl.sqlDate(YssFun.toDate(YssFun.formatDate(this.mFundIns.getStrBargainDate(),"yyyy-MM-dd")));//SQL语句
                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而num不为空，则按照num来执行sql语句
            else if ( this.mFundIns.getStrSecurityCode() != null && this.mFundIns.getStrSecurityCode() != "") {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Data_MonFundInterest") +
                    " where FSecurityCode = " +
                    dbl.sqlString(this.mFundIns.getStrSecurityCode()) +
                    " and FBargainDate = " + dbl.sqlDate(YssFun.toDate(YssFun.formatDate(this.mFundIns.getStrBargainDate(),"yyyy-MM-dd"))); //SQL语句
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
                "(select a.*, b.FUserName as FCreatorName, c.FUserName as FCheckUserName, d.FSecurityName from " +
                pub.yssGetTableName("Tb_Data_MonFundInterest") + " a " +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join (select FSecurityCode,FSecurityName from " +
                pub.yssGetTableName("Tb_Para_Security") +
                ") d on a.FSecurityCode = d.FSecurityCode " +
                buildFilterSql() +
                ") y order by y.FCheckState, y.FCreateTime desc";
        }catch(Exception e){
             throw new YssException("获取货币基金每万份收益率数据出错！" + "\r\n" + e.getMessage(), e);
        }
        return this.builderListViewData(strSql);
    }

    /**
     * builderListViewData
     *
     * @param strSql String
     * @return String
     */
    private String builderListViewData(String strSql) throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("MonFundInterest");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
            	//---add by songjie 2011.11.03 BUG 2372 QDV4赢时胜(测试)2011年8月3日02_B start---//
            	bufShow.append(rs.getString("FSecurityCode")).append("\t");
            	bufShow.append(rs.getString("FSecurityName")).append("\t");
            	bufShow.append(YssFun.formatDate(rs.getDate("FReadDate"))).append("\t");
            	bufShow.append(YssFun.formatDate(rs.getDate("FBargainDate"))).append("\t");
            	bufShow.append(rs.getBigDecimal("FFundRate") + "").append("\t");
            	bufShow.append(rs.getString("FDesc")).append("\t");	//modify huangqirong 2012-03-02 bug3773
            	bufShow.append(rs.getString("FCreator")).append("\t");//modify huangqirong 2012-03-02 bug3773
            	bufShow.append(rs.getString("FCreateTime")).append("\t");//modify huangqirong 2012-03-02 bug3773
            	bufShow.append(rs.getString("FCheckUser")).append("\t");
            	bufShow.append(rs.getString("FCheckTime")).append(YssCons.YSS_LINESPLITMARK);
            	//---add by songjie 2011.11.03 BUG 2372 QDV4赢时胜(测试)2011年8月3日02_B end---//
            	
            	//---delete by songjie 2011.11.03 BUG 2372 QDV4赢时胜(测试)2011年8月3日02_B start---//
//            	bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
//                    append(YssCons.YSS_LINESPLITMARK);
            	//---delete by songjie 2011.11.03 BUG 2372 QDV4赢时胜(测试)2011年8月3日02_B end---//
                this.mFundIns.setMonetaryFundInsAttr(rs);
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
            throw new YssException("获取货币基金每万份收益率数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    /**
     * buildFilterSql
     *
     * @return String
     */
    private String buildFilterSql() throws YssException{
        String sResult = "";
        MonetaryFundInsBean filterType = this.mFundIns.getFilterType();
        if (filterType != null) {
            sResult = " where 1=1";
            if (filterType.isBShow() == false) {
                sResult = sResult + " and 1=2";
            }
            if (filterType.getStrSecurityCode().length() != 0) {
                sResult = sResult + " and a.FSecurityCode like '" +
                    filterType.getStrSecurityCode().replaceAll("'", "''") + "%'";
            }
            if (filterType.getStrReadDate().length() != 0 &&
                ! (filterType.getStrReadDate().equals("9998-12-31") ||
                   filterType.getStrReadDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FReadDate = " +
                    dbl.sqlDate(filterType.getStrReadDate());
            }
            if (filterType.getStrBargainDate().length() != 0 &&
                ! (filterType.getStrBargainDate().equals("9998-12-31") ||
                   filterType.getStrBargainDate().equals("1900-01-01"))) {
                sResult = sResult + " and a.FBargainDate = " +
                    dbl.sqlDate(filterType.getStrBargainDate());
            }
            if (filterType.getBdFundRate() != null &&
                filterType.getBdFundRate().compareTo(new BigDecimal(0)) > 0) {
                sResult = sResult + " and a.FFundRate = " +
                    filterType.getBdFundRate();
            }
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
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (mFundIns == null) {
            mFundIns = new MonetaryFundInsBean();
            mFundIns.setYssPub(pub);
        }
        mFundIns.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }

    public String buildRowStr() throws YssException {
        return mFundIns.buildRowStr();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }
    
    /**返回符合条件的证券代码,用","号拼接
     * @return
     * @throws YssException
     * @author shashijie ,2011-9-14 , bug BUG2633业务平台->调度方案设置中的收益计提类中没有基金万份收益计提  
     * @modified
     */
    public String getIncomeTypeData(String formatDate, String sPort) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        StringBuffer strResult = new StringBuffer();
        try{
	        strSql = "select a.FSecurityCode From "+pub.yssGetTableName("Tb_Data_MonFundInterest")+" a "+
	        	" where a.FCheckState = 1 and a.FBargainDate = "+dbl.sqlDate(formatDate);
	        rs = dbl.openResultSet(strSql);
	        while(rs.next()){
	        	strResult.append(rs.getString("FSecurityCode")).append(",");
	        }
	        if(strResult.length() > 1){
        		strResult.delete(strResult.length() - 1, strResult.length());
        	}
	        return strResult.toString();
        } catch (Exception e) {
            throw new YssException("获取债券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
}
