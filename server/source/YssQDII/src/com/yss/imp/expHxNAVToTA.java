package com.yss.imp;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.operdeal.datainterface.BaseDaoOperDeal;
import com.yss.util.YssException;
import java.sql.*;
import com.yss.util.YssFun;
import java.text.SimpleDateFormat;
import com.yss.main.operdeal.BaseOperDeal;

/**
 * <p>Title:华夏净值文件导出 </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author add liyu 0926
 * @version 1.0
 */
public class expHxNAVToTA
    extends BaseDataSettingBean {
    private String sPort = ""; //组合代码 格式为 x,y,z
    private String sDate = ""; //对应的系统时间
    private String sFundCode = ""; //基金代码,格式为 x,y,z
    private String sHolidayCode = ""; //节假日代码  add liyu 1112
    public void parseRowStr(String sRowStr) {
        if (sRowStr.trim().length() == 0) {
            return;
        }
        String[] tmpAry = sRowStr.split("\t");
        this.sPort = tmpAry[0];
        this.sDate = tmpAry[1];
        this.sFundCode = tmpAry[2];
        this.sHolidayCode = tmpAry[3];
    }

    /**
     * 导出数据方法
     * @return String  返回所有的参数  列标题,正文
     * 格式:列与列为 ^ 行与行 \r\n  标题与下文 \t\t 文件与文件 \f\f
     * @throws YssException
     */
    public String ExpToTxt() throws YssException {
        String reStr = "";
        boolean bTrans = false;
        ResultSet rs = null;
        String sResult = "";
        BaseOperDeal deal = new BaseOperDeal();
        String sTodayCost = "", sYesCost = "";
        StringBuffer buf = new StringBuffer();
        Connection conn = dbl.loadConnection();
        String[] sFun = this.sFundCode.split(",");
        deal.setYssPub(pub);
        String sHead =
            "FundCode^FundClass^NavDate^NAV^LastVav^CumulativeNAV^RedemManFee^" +
            "TotalManFee^TotalIncome^FundIncome^Yield^PurchPrice^RedeemPrice"; //列名
        try {
            for (int i = 0; i < sFun.length; i++) {
                /*reStr=//" select y.FStandardMoneyMarketValue+y.FStandardMoneyAppreciation+y.FStandardMoneyCost as FCost "+
                 " select y.FStandardMoneyMarketValue as FCost "+//20071010,导出净值应该为单位净值，杨文奇
                 " from "+pub.yssGetTableName("tb_rep_guessvalue")+" y left join "+
                      " (select a.FportCode,a.FAssetCode,b.FSetCode from "+pub.yssGetTableName("Tb_Para_Portfolio")+" a "+
                 " left join (select FSetID,FSetCode from lsetList )b on a.FAssetCode=b.FSetID "+
                      " where a.FAssetCode="+dbl.sqlString(sFun[i])+") x on y.FPortCode =x.FSetCode  "+
                      " where y.FDate="+dbl.sqlDate(deal.getWorkDay(this.sHolidayCode,YssFun.toDate(this.sDate),-2))+
                 " and FAcctName='单位净值' and FCurCode=' ' ";// 上一日 现确定为T-2日，取上一个工作日 liyu 修改 1112
                            rs=dbl.openResultSet(reStr);
                            while(rs.next()){
                  // sYesCost=YssFun.formatNumber(rs.getDouble("FCost"),"00000000000000.0000"); // modify liyu 1015 因文件格式不对
                   sYesCost=rs.getDouble("FCost")+"";
                            }
                 if(sYesCost.trim().length()==0) sYesCost="0.0";//"00000000000000.0000";
                            //==========================
                            reStr = //" select y.FStandardMoneyMarketValue+y.FStandardMoneyAppreciation+y.FStandardMoneyCost as FCost,y.Fdate,x.FAssetCode "+
                        " select y.FStandardMoneyMarketValue as FCost,y.Fdate,x.FAssetCode "+//20071010,导出净值应该为单位净值，杨文奇

                 "from "+pub.yssGetTableName("tb_rep_guessvalue")+" y left join "+
                    "  (select a.FportCode,a.FAssetCode,b.FSetCode from "+pub.yssGetTableName("Tb_Para_Portfolio")+" a "+
                   " left join (select FSetID,FSetCode from lsetList where FYear = " + YssFun.toInt(YssFun.formatDate(sDate))
                   + " )b on a.FAssetCode=b.FSetID "+
                 "  where a.FassetCode="+dbl.sqlString(sFun[i])+") x on y.FPortCode =x.FSetCode "+
                            " where y.FDate="+dbl.sqlDate(deal.getWorkDay(this.sHolidayCode,YssFun.toDate(this.sDate),-1))+" and FAcctName='单位净值' and FCurCode=' '";// 现确定为T-1日，取上一个工作日 liyu 修改 1112*/
                //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                reStr = " select y.FPortNetValue as FCost " +
                    " from (select FPortNetValue,FPortCode,FNAVDate from " +
                    pub.yssGetTableName("tb_Data_NetValue") + //将 财务估值表 替换成 资产净值表 sj
                    " where FType = '02') y left join " +
                    " (select a.FportCode,a.FAssetCode,b.FSetCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") + " a " +
                    //edit by songjie 2011.12.02 BUG 3131 QDV4赢时胜(测试)2011年11月11日11_B 报无效数字错误
                    " join (select FSetID,FSetCode || '' as FSetCode from lsetList )b on a.FAssetCode=b.FSetID " +
                    " where a.FAssetCode=" + dbl.sqlString(sFun[i]) +
                    ") x on y.FPortCode =x.FSetCode  " +
                    " where y.FNAVDate=" +
                    dbl.sqlDate(deal.getWorkDay(this.sHolidayCode,
                                                YssFun.toDate(this.sDate), -2));
                //+
                //" and FCurCode=' ' ";
                rs = dbl.openResultSet(reStr);
                while (rs.next()) {
                    sYesCost = YssFun.roundIt(rs.getDouble("FCost"), 3) + "";
                }
                if (sYesCost.trim().length() == 0) {
                    sYesCost = "0.0"; //"00000000000000.0000";
                    //==========================
                }
                reStr =
                    " select y.FPortNetValue as FCost,y.FNAVDate,x.FAssetCode " +
                    //----------MS00234 QDV4华夏2009年2月04日02_ B sj modified-----//
                    ",y.FInvMgrCode as FInvMgrCode " + //获取投资经理数据
                    //------------------------------------------------------------//
                    "from (select FPortNetValue,FPortCode,FNAVDate" +
                    //----------MS00234 QDV4华夏2009年2月04日02_ B sj modified-----//
                    ",FInvMgrCode " + //获取投资经理数据
                    //------------------------------------------------------------//
                    " from " +
                    pub.yssGetTableName("tb_Data_NetValue") +
                    " where FType = '02') y left join " +
                    "  (select a.FportCode,a.FAssetCode,b.FSetCode from " +
                    pub.yssGetTableName("Tb_Para_Portfolio") + " a " +
                    " join (select FSetID,FSetCode from lsetList where FYear = " +
                    YssFun.toInt(YssFun.formatDate(sDate))
                    + " )b on a.FAssetCode=b.FSetID " +
                    "  where a.FassetCode=" + dbl.sqlString(sFun[i]) +
                    ") x on y.FPortCode = TO_CHAR(x.FSetCode) " +  //shashijie : 这里要加上to_char()函数否则会报错
                    " where y.FNAVDate=" +
                    dbl.sqlDate(deal.getWorkDay(this.sHolidayCode,
                                                YssFun.toDate(this.sDate), -1)) +
                    //--------MS00234 QDV4华夏2009年2月04日02_ B sj modified-------//
                    " order by y.FInvMgrCode desc"; //为了将为空的投资经理的净值数据排列在最后。
                //------------------------------------------------------------
                //+
                //"  and FCurCode=' '";
                rs = dbl.openResultSet(reStr);
                while (rs.next()) {
                    //---------- MS00234 QDV4华夏2009年2月04日02_ B sj modified 为了去处重复的净值数据 -------------------------------------------------------------------//
                    if (rs.getString("FInvMgrCode").equalsIgnoreCase(" ") && rs.getRow() > 1) { //当前记录为空投资经理，并且在获取净值时存在多条记录时，将之前的投资经理的数据清除。
                        buf.delete(0, buf.length());
                    }
                    //------------------------------------------------------------------------------------------------------------------------------------------------//
                    sTodayCost = YssFun.roundIt(rs.getDouble("FCost"), 3) + "";
                    if (sTodayCost.trim().length() == 0) {
                        sTodayCost = "0.0"; //"00000000000000.0000";
                    }
                    buf.append(sFun[i]).append("^");
                    buf.append("1").append("^"); // 修改 liyu 0928
                    buf.append(YssFun.formatDate(rs.getDate("FNAVDate"), "yyyyMMdd")).
                        append("^");
                    buf.append(sTodayCost).append("^"); //当日净值
                    buf.append(sYesCost).append("^"); //前一天净值
                    //  buf.append("00000000000000.0000");
                    buf.append(sTodayCost).append("\r\n");
                    //  buf.append("00000000000000.0000");
                    //  buf.append("00000000000000.0000");
                    //  buf.append("00000000000000.0000");
                    //  buf.append("00000000.0000000");
                    //  buf.append("00000000.0000000");
                    //  buf.append("00000000000000.0000");
                    //  buf.append("00000000000000.0000").append("\r\n");
                }
                buf.append("\f\f");
                rs.close();
                sResult = buf.toString();
                if (sResult.length() >= 2) {
                    sResult = sResult.substring(0, sResult.length() - 2);
                }
            }
        }catch(YssException e){//by guyichuan 2011.07.25 BUG2202净值导出界面存在问题
        	throw new YssException(e.getMessage());
        }
        catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return sHead + "\t\t" + sResult;
    }

    /**
     * 根据组合查询关联的基金代码
     * @return String
     * @throws YssException
     */
    public String findFundCode() throws YssException {
        String sqlStr = "";
        String sResult = "";

        StringBuffer sRes = new StringBuffer();
		/**shashijie 2012-7-2 STORY 2475 */
        //Connection conn = dbl.loadConnection();
		/**end*/
        ResultSet rs = null;
        try {
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                " where FPortCode in(" + operSql.sqlCodes(this.sPort) + ")";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sRes.append(rs.getString("FAssetCode")).append("\f\f");
            }
            rs.close();
            sResult = sRes.toString();
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("获取基金代码出错");
        }
    }

    private String getFirstDate(String sDate) throws YssException {
        java.util.Date date = null;
        try {
            date = YssFun.toDate(sDate);
            date = YssFun.addDay(date, -1);
        } catch (Exception e) {

        }
        return YssFun.formatDate(date, "yyyy-MM-dd");
    }
}
