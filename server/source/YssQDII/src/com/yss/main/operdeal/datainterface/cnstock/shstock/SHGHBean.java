
package com.yss.main.operdeal.datainterface.cnstock.shstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.util.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.main.operdeal.datainterface.cnstock.*;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;
import com.yss.main.operdata.BondInterestBean;
import java.math.BigDecimal;

/**
 * 上海过户库储存上交所交易数据
 * 将上海过户库文件的数据经过处理最终读入到系统的交易接口清算库中
 * created by songjie
 * QDV4.1赢时胜（上海）2009年4月20日04_A
 * MS00004
 * 2009-06-04
 */
public class SHGHBean
    extends DataBase {

    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
    //HashMap hmTradeFee = null; //用于储存数据接口参数设置界面的交易费用计算方式分页的各种参数设置 key--组合群代码, 组合代码
	//HashMap hmBrokerCode = null; //用于储存席位代码对应的券商代码
    //HashMap hmBrokerRate = null; //用于储存券商佣金利率 key--组合代码, 券商代码, 席位地点（上海或深圳）, 席位号, 品种类型
    //HashMap hmExchangeBond = null; //用于储存数据接口参数设置界面的交易所债券参数设置分页的各种参数 key--组合群代码, 组合代码, 市场, 品种
    //HashMap hmFeeWay = null; //用于储存数据接口参数设置界面的费用承担方向分页的各种参数设置 key--组合群代码, 组合代码, 券商代码, 席位代码
    //HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码
    //HashMap hmRateSpeciesType = null; //用于储存各种交易品种费率 key--费率类型, 费率品种
    //HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//
    HashMap hmSeatType = null;//用于储存席位代码对应的席位类型
    HashMap hmSubAssetType = null;//用于储存已选组合代码对应的资产子类型
    String checkstate = null; //审核状态
    java.util.Date date = null; //接口处理界面选择的时间
    String portCodes = null; //接口处理界面已选的组合代码
    //delete by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    //String assetGroupCode = null; //组合群代码

    ArrayList alZQInfo = new ArrayList();//用于储存要存到债券利息表的数据
    ArrayList alZQCodes = new ArrayList();//用于储存要存到债券利息表的债券代码

    HashMap hmSecsInfo = null;
    HashMap hmMTVInfo = null;//用于储存估值方法筛选条件数据对应的估值方法代码
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    ArrayList alShowHGZqdm = new ArrayList();
    //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    ArrayList alShowZqdm = new ArrayList();
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    HashMap hmShowZqdm = new HashMap();
    
    public HashMap getHmShowZqdm() {
		return hmShowZqdm;
	}
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    
    //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    public ArrayList getAlShowZqdm(){
    	return alShowZqdm;
    }
    //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    public ArrayList getAlShowHGZqdm(){
    	return alShowHGZqdm;
    }
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    
	/**
     * 构造函数
     */
    public SHGHBean() {

    }

    public void makeData(java.util.Date tradeDate, String portCode, String checkState, HashMap hmParam) throws YssException {
        try {
            checkstate = checkState; //接口处理界面的审核状态
            date = tradeDate; //接口数据界面选择的日期
            portCodes = portCode; //接口数据界面选择的组合代码
            assetGroupCode = pub.getAssetGroupCode(); //当前组合群代码

            //获取数据接口参数设置的读数处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) hmParam.get("hmReadType");

            //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
            hmExchangeBond = (HashMap) hmParam.get("hmExchangeBond");

            //获取数据接口参数设置的交易费用计算方式界面设置的参数对应的HashMap
            hmTradeFee = (HashMap) hmParam.get("hmTradeFee");

            //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
            hmFeeWay = (HashMap) hmParam.get("hmFeeWay");

            //获取交易费率品种设置界面设置的费率对应的HashMap
            hmRateSpeciesType = (HashMap) hmParam.get("hmRateSpeciesType");

            //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            hmBrokerRate = (HashMap) hmParam.get("hmBrokerRate");

            //获取所有已选组合代码对应的股东和席位代码对应的HashMap
            hmPortHolderSeat = (HashMap) hmParam.get("hmPortHolderSeat");

            //获取SHGH表中所有席位代码对应的券商代码对应的HashMap
            hmBrokerCode = getBrokerCode("GSDM", "SHGH", false, date,portCodes);//edited by zhouxiang MS1299

            //获取估值方法筛选条件对应的估值方法代码
            hmMTVInfo = (HashMap) hmParam.get("hmMTVInfo");

            fromSHGHToHzJkMx(); //从SHGH表到交易接口明细库

            insertIntoBondInterest();//将债券利息数据中没有的债券利息数据插入到表中
        } catch (Exception e) {
            throw new YssException("处理上海过户库数据到交易接口清算库出错", e);
        }
    }

    /**
     * 将债券利息数据中没有的债券利息数据插入到表中
     * @throws YssException
     */
    public void insertIntoBondInterest() throws YssException {
        String strSql = "";//用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null;//声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
        try{
            iterator = alZQCodes.iterator();//获取迭代器

            while(iterator.hasNext()){
                zqdm = (String)iterator.next();//获取证券代码
                zqdms += zqdm + ",";//拼接证券代码
            }

            if(zqdms.length() >= 1){
                zqdms = zqdms.substring(0, zqdms.length() - 1);//去掉字符串最后逗号
            }

            con.setAutoCommit(false); //设置手动提交事务
            bTrans = true;

            //先在债券利息表中删除需要插入到债券利息表中的债券数据
            strSql = " delete from " + pub.yssGetTableName("Tb_Data_BondInterest") +
                " where FSecurityCode in(" + operSql.sqlCodes(zqdms) +
                //edit by songjie 2010.03.18 MS00920 QDV4赢时胜（测试）2010年03月18日06_B
                ") and FRecordDate = " + dbl.sqlDate(date);

            dbl.executeSql(strSql);

            //添加数据到债券利息表中
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_BondInterest") +
                "(FSecurityCode,FRecordDate,FCurCpnDate,FNextCpnDate,FIntAccPer100," +
                "FIntDay,FSHIntAccPer100,FDataSource,FCheckState,FCreator,FCreateTime)" +
                "values(?,?,?,?,?,?,?,?,?,?,?)";

            pstmt=dbl.openPreparedStatement(strSql);

            iterator = alZQInfo.iterator();
            while(iterator.hasNext()){
                bondInterest = (BondInterestBean)iterator.next();//获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode());//设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(date));//设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100());//设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100());//设置税后百元利息
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
                //数据来源改为 "ZD－自动计算"
                pstmt.setString(8, "ZD");//表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
            }

            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将数据插入到债券利息表时出错！",e);
        }
        finally{
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }

    /**
     * 从SHGH表到交易接口明细表
     * @throws YssException
     */
    private void fromSHGHToHzJkMx() throws YssException {
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String strSql = null; //储存sql语句
        PreparedStatement pstmt = null; //声明PreparedStatement

        String tradeSeats = null; //交易席位
        String stockHolders = null; //用于储存股东代码

        String TSInfo = null; //组合代码相关的股东代码和席位代码
        String[] subTSInfo = null; //储存拆分后的股东代码和席位代码数据

        double cjje = 0; //初始化成交金额
        double cjjg = 0; //初始化成交价格
        int cjsl = 0; //初始化成交数量

        String gsdm = null; //声明席位代码
        String seatcode=null;//声明席位代码   //edited by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
        String zqdm = null; //声明证券代码
        String sqbh = null; //声明申请编号
        String bs = null; //声明买卖标志
        String gddm = null; //声明股东代码
        String jyfs = null;//交易方式：大宗交易（DZ)  普通交易（PT）panjunfang add 20100426

        String secInfo = null;
        String secType = null;
        String secTypes = null;
        String resultType = null;
        java.util.Date fDate = null; //储存交易日期

        String convertSecCode = null;
        String businessSign = null;
        String securitySign = null;

        String oldZqdm = null; //证券的原始代码
        String zqdmETF = null; //ETF基金代码
        String FTZBZ = null; //投资标志

        ReadTypeBean readType = null; //声明读数处理方式实体类
        FeeAttributeBean feeAttribute = null; //声明费用属性实体类
        String[] arrPortCodes = null; //储存拆分的组合代码

        hmSeatType = new HashMap();//用于储存席位代码对应的席位类型
        hmSubAssetType = new HashMap();//用于储存已选组合代码对应的资产子类型

        boolean canInsert = false;//判断权益数据是否能够处理到交易接口明细库
        DataBase dataBase = new DataBase();
        
        boolean isGzlx = false;//判断交易接口明细表中的FGZLX字段取税前利息还是税后利息
        try {
            con.setAutoCommit(false); //开启事务
            bTrans = true;

            arrPortCodes = portCodes.split(","); //储存拆分的组合代码

            hmSeatType = getSeatType();//获取席位代码对应的席位类型
            hmSubAssetType = judgeAssetType(portCodes, date);//获取已选组合代码对应的资产子类型

            for (int i = 0; i < arrPortCodes.length; i++) { //循环组合代码
                TSInfo = (String) hmPortHolderSeat.get(arrPortCodes[i]); //获取组合代码对应的席位代码以及股东代码
                //根据组合群代码和组合代码获取对应的读数处理方式参数数据
                readType = (ReadTypeBean) hmReadType.get(assetGroupCode + " " + arrPortCodes[i]);

                if (readType == null) {
                    throw new YssException("请在交易接口参数设置中设置已选组合的读数处理方式！");
                }
                FTZBZ = readType.getAssetClass(); //获取组合代码对应的资产标志参数

                if (FTZBZ.equals("01")) {
                    FTZBZ = "C"; //表示交易类金融资产类
                }
                if (FTZBZ.equals("02")) {
                    FTZBZ = "S"; //表示可供出售类金融资产类
                }
                if (FTZBZ.equals("03")) {
                    FTZBZ = "F"; //表示持有到期类金融资产类
                }

                subTSInfo = TSInfo.split("\t"); //拆分组合代码对应的股东代码数据和席位代码数据

                if (subTSInfo.length > 1) {
                    tradeSeats = subTSInfo[0]; //获取席位代码数据
                    stockHolders = subTSInfo[1]; //获取股东代码数据
                }

                //---add by songjie 2012.04.01 STORY #2437 QDV4华安基金2012年3月26日02_A start---//
                //若数据日期 和 界面所选日期不一致 则提示重新选择
                strSql = " select * from SHGH where FDate = " + dbl.sqlDate(this.sDate);
                rs = dbl.openResultSet(strSql);
                if(!rs.next()){
                	throw new YssException("界面所选日期与接口文件日期不一致，请重新选择！");
                }
                
                dbl.closeResultSetFinal(rs);
                rs = null;
                //---add by songjie 2012.04.01 STORY #2437 QDV4华安基金2012年3月26日02_A end---//
                
                //edited by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
                //根据过户库中的席位号将对应的席位代码查找出来 此处没有考虑一个席位号对应多个席位代码的可能
                //edit by songjie 2011.01.14 需求：406 QDV4南方东英2010年12月14日01_A 链接席位设置已审核的数据
                strSql = " select * from SHGH a left join (select * from "+ pub.yssGetTableName("Tb_Para_TradeSeat") + 
                " where FCheckState = 1) b on a.gsdm=b.fseatnum " +
                " where GDDM  in(" + operSql.sqlCodes(stockHolders) +
                ") and GSDM in(" + operSql.sqlCodes(tradeSeats) + ") and FDate = " + dbl.sqlDate(this.sDate);
                rs = dbl.openResultSet(strSql); //在SHGH表中查找相关股东和席位的数据

                while (rs.next()) {
                    cjje = rs.getDouble("CJJE"); //成交金额
                    cjjg = rs.getDouble("CJJG"); //成交价格
                    cjsl = rs.getInt("CJSL"); //成交数量
                    gddm = rs.getString("GDDM"); //股东代码
                    gsdm = rs.getString("GSDM"); //席位号
                    seatcode=rs.getString("FSEATCODE");//席位代码//add by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
                    zqdm = rs.getString("ZQDM"); //证券代码
                    sqbh = rs.getString("SQBH"); //申请编号
                    bs = rs.getString("BS"); //买卖B or S
                    fDate = rs.getDate("FDate");
                    jyfs = rs.getString("JYFS");//交易方式-大宗交易、普通交易

                    //若证券代码以'704','764','733','783'开头，需要通过convertRule.xml文件中的转换后的证券代码来判断证券类型和业务类型
                    if (YssFun.left(zqdm, 3).equals("704") || YssFun.left(zqdm, 3).equals("764") ||
                        YssFun.left(zqdm, 3).equals("733") || YssFun.left(zqdm, 3).equals("783")) {
                        bs = "B";//设置新债的可转债中签，新债的可转债新债的老股东配售的买卖标志为--买
                        pubXMLRead.setSHGH(YssFun.left(zqdm, 3) + "***", null);
                    } else {
                        //设置业务标志为新债的企业债申购，新债的国债申购,新股的新股增发,新股的新股中签,新股的新股市值中签的买卖标志为--买
                        if(YssFun.left(zqdm, 4).equals("7519") ||
                           YssFun.left(zqdm, 3).equals("731") || YssFun.left(zqdm, 3).equals("781") ||
                           YssFun.left(zqdm, 3).equals("730") || YssFun.left(zqdm, 3).equals("780") ||
                           YssFun.left(zqdm, 3).equals("737") || YssFun.left(zqdm, 3).equals("739")){
                            bs = "B";
                        }
                        //判断业务标志，证券标志以及转换后的证券代码
                        secInfo = judgeSecurityType(cjje, cjjg, gsdm, zqdm, sqbh, bs, fDate, arrPortCodes[i]);
                        if(secInfo.equals("")){
                            continue;
                        }
                        if (secInfo.split("\t").length > 1) {
                            secTypes = secInfo.split("\t")[0]; //证券代码的类型 类似于1*****
                            secType = secTypes;
                            oldZqdm = zqdm; //获取转换前的证券代码

                            if (secType.indexOf(",") != -1) {
                                secTypes = secType.split(",")[0]; //转换前的证券代码的格式

                                //若为ETF基金申赎的股票，可根据同一申请编号获取到其ETF基金的代码，
                                //注意将股票数据的原始代码存为其ETF的基金代码。且其ETF基金现金替代部分的数据也需将
                                //根据同一申请编号获取到其ETF基金的代码，将其原始代码转为其ETF基金的代码。
                                zqdmETF = secType.split(",")[1];

                                //若为ETF基金申赎的股票，可根据同一申请编号获取到其ETF基金的代码，
                                //将股票数据的原始代码存为其ETF的基金代码
                                oldZqdm = zqdmETF;
                            }
                            resultType = secInfo.split("\t")[1]; //获取判断结果数据

                            //根据判断结果和转换前的证券代码的格式在convertRule.xml文件中
                            //查找相应的转换后的证券代码格式以及证券标志和业务标志
                            pubXMLRead.setSHGH(secTypes, resultType);
                        }
                    }

                    if (pubXMLRead.getSecSign() != null || pubXMLRead.getSecSign().trim().length() > 0) {
                        securitySign = pubXMLRead.getSecSign();//证券标志
                    }
                    if (pubXMLRead.getBusinessSign() != null || pubXMLRead.getBusinessSign().trim().length() > 0) {
                        businessSign = pubXMLRead.getBusinessSign();//业务标志
                    }
                    if (pubXMLRead.getConvertedSecCode() != null || pubXMLRead.getConvertedSecCode().trim().length() > 0) {
                        convertSecCode = pubXMLRead.getConvertedSecCode();//获取转换后的证券代码格式
                    }

                    //----外部证券代码转换成内部证券代码----//
                    //将类似于51****的证券代码格式中的 * 去掉，只获取类似于51的字符串
                    if (convertSecCode.indexOf("*") == -1 && convertSecCode.length() == 6) {
                        zqdm = zqdm + " CG";
                    } else {
                        convertSecCode = convertSecCode.substring(0, convertSecCode.indexOf("*"));
                        if (! (YssFun.left(zqdm, 3).equals("743") || YssFun.left(zqdm, 3).equals("793") ||
                               YssFun.left(zqdm, 3).equals("704") || YssFun.left(zqdm, 3).equals("764") ||
                               YssFun.left(zqdm, 3).equals("733") || YssFun.left(zqdm, 3).equals("783"))) {
                            secType = secType.substring(0, secType.indexOf("*"));
                        } else {
                            secType = YssFun.left(zqdm, 3);
                            oldZqdm = zqdm;
                        }
                        //将外部代码（转换前的证券代码）转换为内部代码（转换后的证券代码）
                        if (!convertSecCode.equalsIgnoreCase(secType)) {
                            zqdm = convertSecCode + zqdm.substring(convertSecCode.length(), 6) + " CG";
                        }

                        if (zqdm.indexOf(" CG") == -1) {
                            zqdm = zqdm + " CG";
                        }
                    }

                    //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                    if(YssFun.left(zqdm, 3).equals("002") && 
                    securitySign.equals("XG") && businessSign.equals("ZQ_SZ")){
                    	zqdm  = YssFun.left(zqdm, 6) + " CS";
                    }
                    //add by songjie 2010.02.25 MS00887 QDII4.1赢时胜上海2010年02月23日03_AB
                    
                    //设置新股的配售增发中签业务,权益业务的买卖标志为 "买"
                    if((securitySign.equals("XG") && businessSign.equals("ZQ_PSZF")) || securitySign.equals("QY")){
                        bs = "B";
                    }
                    //==========add by xuxuming,20091112.国内回购，买卖方向设置================
                    //	若为融资回购，金额、数量及费用均计入卖类型。减费用
                	//  若为融券回购，金额、数量及费用均计入买类型。加费用
                    //因为导入数据所用的表中，逆回购　是　计为"S",在此处要反向。这样才能保证费用计算正确
                    if (securitySign.equalsIgnoreCase("HG")){
                    	if("B".equalsIgnoreCase(bs)) {
                    		bs="S";
                    	}else if("S".equalsIgnoreCase(bs)) {
                    		bs="B";
                    	}
                    }
                    //================end====================================================
                    //----外部证券代码转换成内部证券代码----//F
                    feeAttribute = new FeeAttributeBean(); //新建费用属性实例

                    feeAttribute.setSecuritySign(securitySign); //设置证券标志
                    feeAttribute.setBusinessSign(businessSign); //设置业务标志
                    feeAttribute.setBs(bs); //设置买卖标志
                    feeAttribute.setZqdm(zqdm); //设置转换后的证券代码
                    feeAttribute.setPortCode(arrPortCodes[i]); //设置组合代码
                    feeAttribute.setGsdm(gsdm); //设置席位号
                    feeAttribute.setDate(fDate); //设置交易日期
                    feeAttribute.setReadType(readType); //设置读数处理方式参数数据
                    feeAttribute.setCjje(cjje); //设置成交金额
                    feeAttribute.setCjsl(cjsl); //设置成交数量
                    feeAttribute.setCjjg(cjjg); //设置成交价格
                    feeAttribute.setJyfs(jyfs);//设置交易方式
                    //modify by zhangfa MS01679    交易所回购，已设置费用承担方式参数，但是导入过户库仍提示    QDV4赢时胜（上海）2010年8月18日01_B    
                    feeAttribute.setSeatCode(gsdm);//设置交易席位代码
                    if(hmBrokerCode.isEmpty()){
                    	feeAttribute.setHmBrokerCode(getBrokerCode("GSDM", "SHGH", false, fDate,arrPortCodes[i]));
                    }
                    //add by zhouxiang MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B  
                    //feeAttribute.setSeatCode(seatcode);//设置交易席位代码
                    
                    //--------------------------------------------------------------------------------------------------
                    if(securitySign.equals("QY")){
                        dataBase.setYssPub(pub);
                        canInsert = dataBase.judgeQYInfo(feeAttribute); //判断权益数据是否能够处理到交易接口明细库

                        if (!canInsert) { //不能处理，则不插入交易接口明细库
                            continue;
                        }
                    }

                    //add by songjie 2010.03.18 
                    //MS00916 QDII4.1赢时胜上海2010年03月18日02_B
                    //上海过户库中若价格及金额均无的数据无须处理
                    //（不含可转债的债转股业务数据及ETF基金申赎的股票数据）
                    if(cjjg == 0 && cjje == 0 
                       && !(businessSign.equals("KZZGP") 
                    		|| (securitySign.equals("GP") 
                    			&& (businessSign.equals("SH_ETF") 
                    				|| businessSign.equals("SG_ETF"))))){
                    	continue;
                    }
                    
                    /**Start 20130807 added by liubo.Bug #8970.QDV4赢时胜(北京)2013年08月05日02_B
                     * 需求中要求在生成债券计息期间设置之后，需要正确计算交易数据的应计利息
                     * 因此在计算利息之前生成债券计息期间设置数据*/
                    if (feeAttribute.getSecuritySign().equalsIgnoreCase("ZQ"))
                    {
                    	CtlInterestTime interest = new CtlInterestTime();
                    	interest.setYssPub(pub);
                    	
                    	interest.inertData(feeAttribute.getZqdm(),fDate);
                    }
                    /**End 20130807 added by liubo.Bug #8970.QDV4赢时胜(北京)2013年08月05日02_B*/
                    
                    //add by songjie 2010.03.18 
                    //MS00916 QDII4.1赢时胜上海2010年03月18日02_B
                    
                    calculateFee(feeAttribute); //计算费用
                    
                    if(feeAttribute.getSecuritySign().equalsIgnoreCase("ZQ")){
                    	isGzlx = super.judgeGzlx(feeAttribute);
                    }
                    
                    strSql = " insert into " + pub.yssGetTableName("Tb_HzJkMx") +"(FDate, FZqdm, FSzsh, FGddm, FJyxwh, " +
                        " FBs, FCjsl, FCjjg, FCjje, FYhs, FJsf, FGhf, FZgf, FYj, FGzlx, Fhggain, FZqbz, Fywbz, " +
                        " FSqbh, Fqtf, Zqdm, Ffxj, Findate, FTZBZ, FPortCode, FJYFS, FSqGzlx, FCreator, FCreateTime,FJKDM)" +//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更
                        "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更
                    pstmt = dbl.openPreparedStatement(strSql); //将SHGH表中处理后的数据储存到交易接口明细表中

                    pstmt.setDate(1, YssFun.toSqlDate(fDate)); //交易日期
                    pstmt.setString(2, zqdm); //转换后的证券代码
                    pstmt.setString(3, "CG"); //交易所代码
                    pstmt.setString(4, gddm); //股东代码
                    //modify by zhangfa MS01673     交易所回购业务，过户库导入报错    QDV4赢时胜(32上线测试)2010年8月27日01_B    
                    // pstmt.setString(5, seatcode); //席位代码 edited by zhouxiang MS1299
                    pstmt.setString(5, gsdm);
                    //------------------------------------------------------------------------------------------------
                    if(bs == null || bs.equals("")){
                    	bs = " ";
                    }
                    pstmt.setString(6, bs); //买卖标志
                    pstmt.setDouble(7, feeAttribute.getCjsl()); //成交数量
                    pstmt.setDouble(8, feeAttribute.getCjjg()); //成交价格
                    pstmt.setDouble(9, feeAttribute.getCjje()); //成交金额
                    pstmt.setDouble(10, feeAttribute.getFYhs()); //印花税
                    pstmt.setDouble(11, feeAttribute.getFJsf()); //经手费
                    pstmt.setDouble(12, feeAttribute.getFGhf()); //过户费
                    pstmt.setDouble(13, feeAttribute.getFZgf()); //证管费
                    pstmt.setDouble(14, feeAttribute.getFYj()); //佣金
                    if(isGzlx){
                    	pstmt.setDouble(15, feeAttribute.getFGzlx()); //税后的国债利息
                    }else{
                    	pstmt.setDouble(15, feeAttribute.getFBeforeGzlx()); //税前的国债利息
                    }
                    pstmt.setDouble(16, feeAttribute.getFhggain()); //回购收益
                    pstmt.setString(17, feeAttribute.getSecuritySign()); //证券标志
                    pstmt.setString(18, feeAttribute.getBusinessSign()); //业务标志
                    pstmt.setString(19, sqbh); //申请编号
                    pstmt.setDouble(20, feeAttribute.getFqtf()); //结算费
                    pstmt.setString(21, oldZqdm); //转换前的证券代码
                    pstmt.setDouble(22, feeAttribute.getFfxj()); //风险金
                    pstmt.setDate(23, YssFun.toSqlDate(fDate)); //系统读数日期
                    pstmt.setString(24, FTZBZ); //投资标志
                    pstmt.setString(25, arrPortCodes[i]); //组合代码
                    pstmt.setString(26, feeAttribute.getJyfs());//交易方式（大宗交易 、 普通交易） panjunfang modify 20100426
                    pstmt.setDouble(27, feeAttribute.getFBeforeGzlx()); //税前的国债利息
                    pstmt.setString(28, pub.getUserCode()); //用户代码
                    pstmt.setString(29, YssFun.formatDatetime(new java.util.Date())); //创建日期
                    pstmt.setString(30, "SHGH");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                    pstmt.execute();

                    dbl.closeStatementFinal(pstmt); //关闭pstmt
                }
                dbl.closeResultSetFinal(rs); //关闭结果集
            }

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        } catch (Exception e) {
            throw new YssException("将上海过户表的数据处理到交易接口明细库出错！", e);
        } finally {
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeStatementFinal(pstmt); //关闭pstmt
            dbl.closeResultSetFinal(rs); //关闭结果集
        }
    }

    /**
     * 用于判断证券数据的内部证券代码，证券类型，业务类型
     * @param cjje String
     * @param cjjg String
     * @param gsdm String
     * @param zqdm String
     * @param sqbh String
     * @param bs String
     * 在判断证券数据的业务类型时，需要特别注意指数股票和指标股票的判断，
     * 指数股票的判断标准改为：1.看基金资产是否为指数型基金资产（基金资产的判断仍在商议）
     *                      2.看数据中的席位代码对应的是否是指数席位
     * 若两个条件都满足，则表示为指数型股票
     * 指标股票的判断标准改为：1.看基金资产是否为指标型基金资产（基金资产的判断仍在商议）
     *                      2.看数据中的席位代码对应的是否是指数席位
     * 若两个条件都满足，则表示为指标型股票
     * 若以上条件都不满足，则表示为普通股票
     */
    public String judgeSecurityType(double cjje, double cjjg, String gsdm, String zqdm, String sqbh,
                                    String bs, java.util.Date fDate, String portCode) throws YssException {
        boolean haveConvertSec = false;
        boolean haveETFSec = false;

        String seatType = "";
        String zqdmETF = "";
        String oldZqdm = "";

        HashMap hmZZGSec = null;
        HashMap hmETFSec = null;
        try {
            seatType = (String)hmSeatType.get(gsdm); //席位类型

            if (YssFun.left(zqdm, 1).equals("6")) {
                if (cjje == 0 && cjjg == 0) {
                    hmZZGSec = getZZGSec(sqbh); //根据申请编号查询业务标志为债转股债券的证券代码

                    //获取是否有债转股债券的判断标志
                    if ( ( (String) hmZZGSec.get("haveConvertSec")).equals("true")) {
                        haveConvertSec = true;
                    }
                    oldZqdm = (String) hmZZGSec.get("oldZqdm"); //获取债转股债券的证券代码

                    hmETFSec = getETFSec(sqbh); //根据申请编号查询业务标志为ETF申购或赎回的证券代码

                    //获取是否有ETF申购或赎回的基金的判断标志
                    if ( ( (String) hmETFSec.get("haveETFSec")).equals("true")) {
                        haveETFSec = true;
                    }

                    zqdmETF = (String) hmETFSec.get("zqdmETF"); //获取ETF基金申购赎回的证券代码

                    if (haveConvertSec) {
                        return "6*****," + oldZqdm + "\t" + "GP KZZGP"; //可转债股票
                    }
                    if (haveETFSec) {
                        if (bs.equals("B")) {
                            return "6*****," + zqdmETF + "\t" + "GP SH_ETF"; //股票 ETF基金赎回
                        }

                        if (bs.equals("S")) {
                            return "6*****," + zqdmETF + "\t" + "GP SG_ETF"; //股票 ETF基金申购
                        }
                    }
                }

                else if (seatType.equals("INDEX") && ((String)hmSubAssetType.get(portCode)).equals("0102")) {
                    if (bs.equals("B")) {
                        return "6*****\t" + "GP MR_ZS"; //指数股票买入
                    }

                    if (bs.equals("S")) {
                        return "6*****\t" + "GP MC_ZS"; //指数股票卖出
                    }
                } else if (seatType.equals("INDEX") && ((String)hmSubAssetType.get(portCode)).equals("0103")) {
                    if (bs.equals("B")) {
                        return "6*****\t" + "GP MR_ZB"; //指标股票买入
                    }

                    if (bs.equals("S")) {
                        return "6*****\t" + "GP MC_ZB"; //指标股票卖出
                    }
                } else {
                    if (bs.equals("B")) {
                        return "6*****\t" + "GP MR"; //普通股买入
                    }

                    if (bs.equals("S")) {
                        return "6*****" + "\t" + "GP MC"; //普通股卖出
                    }
                }
            }

            if (YssFun.left(zqdm, 2).equals("11") || YssFun.left(zqdm, 2).equals("10")) {
                if (cjje == 0 && cjjg == 0) {
                    haveConvertSec = getETFGP(sqbh); //用于判断是否有ETF申购赎回的股票数据

                    if (haveConvertSec) {
                        return YssFun.left(zqdm, 2) + "****\t" + "ZQ KZZGP"; //可转债债券
                    }
                } else {
                    if (bs.equals("B")) {
                        return YssFun.left(zqdm, 2) + "****\t" + "ZQ MR_KZZ"; //债券 可转债买入
                    }

                    if (bs.equals("S")) {
                        return YssFun.left(zqdm, 2) + "****\t" + "ZQ MC_KZZ"; //债券 可转债卖出
                    }
                }
            }

            if (YssFun.left(zqdm, 3).equals("130") && cjjg != 0) { //地方政府债
                if (bs.equals("B")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "ZQ MR_DFZFZ"; //地方政府债买入
                }

                if (bs.equals("S")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "ZQ MC_DFZFZ"; //地方政府债卖出
                }
            }

            if (YssFun.left(zqdm, 1).equals("0")) {
                if (bs.equals("B")) {
                    return "0*****\t" + "ZQ MR_GZ"; //国债买入
                }

                if (bs.equals("S")) {
                    return "0*****\t" + "ZQ MC_GZ"; //国债卖出
                }
            }

            if (YssFun.left(zqdm, 1).equals("1") && !YssFun.left(zqdm, 2).equals("11") && !YssFun.left(zqdm, 2).equals("10")) {
                if (YssFun.left(zqdm, 3).equals("122")) {
                    if (bs.equals("B")) {
                        return "122***\t" + "ZQ MR_QYZQ_GS"; //公司债买入
                    }

                    if (bs.equals("S")) {
                        return "122***\t" + "ZQ MC_QYZQ_GS"; //公司债卖出
                    }
                } else if (YssFun.left(zqdm, 3).equals("121")) {
                    if (bs.equals("B")) {
                        return "121***\t" + "ZQ MR_ZCZQ"; //资产证券化产品买入
                    }

                    if (bs.equals("S")) {
                        return "121***\t" + "ZQ MC_ZCZQ"; ///资产证券化产品卖出
                    }
                } else if (YssFun.left(zqdm, 3).equals("126")) {
                    if (bs.equals("B")) {
                        return "126***\t" + "ZQ MR_FLKZZ"; //分离可转债买入
                    }

                    if (bs.equals("S")) {
                        return "126***\t" + "ZQ MC_FLKZZ"; //分离可转债卖出
                    }
                } else {
                    if (bs.equals("B")) {
                        return "1*****\t" + "ZQ MR_QYZQ"; //企业债买入
                    }

                    if (bs.equals("S")) {
                        return "1*****\t" + "ZQ MC_QYZQ"; //企业债卖出
                    }
                }
            }

            if (YssFun.left(zqdm, 1).equals("5")) {
                if (YssFun.left(zqdm, 3).equals("580")) {
                    if (Integer.parseInt(YssFun.right(zqdm, 3)) < 800) {
                        if (bs.equals("B")) {
                            return "580***\t" + "QZ MR_RGQZ"; //认购权证买入
                        } else if (bs.equals("S")) {
                            return "580***\t" + "QZ MC_RGQZ"; //认购权证卖出
                        }
                    } else {
                        if (bs.equals("B")) {
                            return "580***\t" + "QZ MR_RZQZ"; //认沽权证买入
                        } else if (bs.equals("S")) {
                            return "580***\t" + "QZ MC_RZQZ"; //认沽权证卖出
                        }
                    }
                }
                if (YssFun.left(zqdm, 3).equals("582")) {
                    if (Integer.parseInt(YssFun.right(zqdm, 3)) < 800) {
                        return "582***\t" + "QZ XQ_RGQZ"; //认购权证行权
                    } else {
                        return "582***\t" + "QZ XQ_RZQZ"; //认沽权证行权
                    }
                }
                if (!YssFun.left(zqdm, 2).equals("51") && !YssFun.left(zqdm, 3).equals("582") && !YssFun.left(zqdm, 3).equals("580")) {
                    if (bs.equals("B")) {
                        return "5*****\t" + "JJ MR_FBS"; //买入封闭式基金
                    } else if (bs.equals("S")) {
                        return "5*****\t" + "JJ MC_FBS"; //卖出封闭式基金
                    }
                }
                if (YssFun.left(zqdm, 2).equals("51")) {
                    if (zqdm.equals("510050") || zqdm.equals("510180") || zqdm.equals("510880")) {
                        if (bs.equals("B")) {
                            return zqdm + "\t" + "JJ SG_ETF"; //ETF申购
                        } else if (bs.equals("S")) {
                            return zqdm + "\t" + "JJ SH_ETF"; //ETF赎回
                        }
                    }
                    //ETF基金ETF申购或赎回的现金替代
                    else if (zqdm.equals("510052") || zqdm.equals("510182") || zqdm.equals("510882")) {
                        hmETFSec = getETFSecForXJTD(zqdm, sqbh);

                        if ( ( (String) hmETFSec.get("haveConvertSec")).equals("true")) {
                            haveConvertSec = true;
                        }

                        zqdmETF = (String) hmETFSec.get("zqdmETF");

                        if (haveConvertSec) {
                            if (bs.equals("B")) {
                                return zqdm + "," + zqdmETF + "\t" + "JJ XJTD_SG"; //ETF申购的现金替代
                            } else if (bs.equals("S")) {
                                return zqdm + "," + zqdmETF + "\t" + "JJ XJTD_SH"; //ETF赎回的现金替代
                            }
                        }
                    } else {
                        if (bs.equals("B")) {
                            return "51****\t" + "JJ MR_ETF"; //ETF买入
                        } else if (bs.equals("S")) {
                            return "51****\t" + "JJ MC_ETF"; //ETF卖出
                        }
                    }
                }
            }

            if (YssFun.left(zqdm, 3).equals("202")) {
                if (bs.equals("B")) {
                    return "202***\t" + "HG MCHG_QYZQ"; //企业债融资回购
                }

                if (bs.equals("S")) {
                    return "202***\t" + "HG MRHG_QYZQ"; //企业债融券回购
                }
            }

            if (YssFun.left(zqdm, 3).equals("203")) {
                if (bs.equals("B")) {
                    return "203***\t" + "HG MDMCHG"; //买断式融资回购
                }

                if (bs.equals("S")) {
                    return "203***\t" + "HG MDMRHG"; //买断式融券回购
                }
            }

            if (YssFun.left(zqdm, 3).equals("201") || YssFun.left(zqdm, 3).equals("204")) {
                if (bs.equals("B")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "HG MCHG"; //融资回购
                }

                if (bs.equals("S")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "HG MRHG"; //融券回购
                }
            }

            if (YssFun.left(zqdm, 2).equals("70") || YssFun.left(zqdm, 2).equals("76")) {
                return judgePGJK(fDate, zqdm, seatType, portCode);
            }

            //edit by songjie 2012.04.25 STORY #2556 QDV4赢时胜(上海开发部)2012年04月24日01_A 添加“734XXX”类证券处理
            if (YssFun.left(zqdm, 3).equals("740") || YssFun.left(zqdm, 3).equals("790") || YssFun.left(zqdm, 3).equals("734")) {
                if (bs.equals("B")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "XG SG"; //新股申购
                }

                if (bs.equals("S")) {
                    return YssFun.left(zqdm, 3) + "***\t" + "XG FK"; //新股--申购返款
                }
            }

            if (YssFun.left(zqdm, 3).equals("731") || YssFun.left(zqdm, 3).equals("781")) {
                return YssFun.left(zqdm, 3) + "***\t" + "XG XGZF"; //新股 新股增发
            }

            //edit by songjie 2012.04.25 STORY #2556 QDV4赢时胜(上海开发部)2012年04月24日01_A 添加“732XXX”类证券处理
            if (YssFun.left(zqdm, 3).equals("730") || YssFun.left(zqdm, 3).equals("780") || YssFun.left(zqdm, 3).equals("732")) {
                return YssFun.left(zqdm, 3) + "***\t" + "XG ZQ"; //新股 新股中签
            }

            if (YssFun.left(zqdm, 3).equals("737") || YssFun.left(zqdm, 3).equals("739")) {
                return YssFun.left(zqdm, 3) + "***\t" + "XG ZQ_SZ"; //新股 新股市值中签
            }

            if (YssFun.left(zqdm, 3).equals("743")) {
                if (bs.equals("B")) {
                    return "743***\t" + "XZ SG_KZZ"; //新债 可转债申购
                }

                if (bs.equals("S")) {
                    return "743***\t" + "XZ FK_KZZ"; //新债 可转债返款
                }
            }

            if (YssFun.left(zqdm, 3).equals("793")) {
                if (bs.equals("B")) {
                    return "793***\t" + "XZ SG_KZZ"; //新债 可转债申购
                }

                if (bs.equals("S")) {
                    return "793***\t" + "XZ FK_KZZ"; //新债 可转债返款
                }
            }

            if(YssFun.left(zqdm,4).equals("7519")){
                int num = Integer.parseInt(YssFun.right(zqdm,2));

                if(num >= 0 && num <= 69){
                    return "7519**\t" + "XZ SG_DFZFZ";//新债 国债申购（地方政府债申购）
                }

                if(num >= 70 && num <= 99){
                	//edit by songjie 2012.04.27 BUG 4431 QDV4赢时胜(测试)2012年04月27日02_B
                    return "7519**\t" + "XZ ZQ_QYZQ";//新债 企业债中签
                }
            }
            
            if(YssFun.left(zqdm,3).equals("900")){//B股
                if (bs.equals("B")) {
                    return "900***\t" + "B_GP MR"; //买入
                }

                if (bs.equals("S")) {
                    return "900***\t" + "B_GP MC"; //卖出
                }
            }
            return "";
            //throw new YssException("对不起，证券代码为" + zqdm + "的证券数据无效!");
        } catch (Exception e) {
            throw new YssException("判断证券数据的内部证券代码，证券类型，业务类型出错", e);
        }
    }

//    /**
//     * 根据证券代码获取票面金额
//     * @param securityCode String
//     * @return double
//     * @throws YssException
//     */
//    private double getFaceAmount(String securityCode) throws YssException {
//        String strSql = "";
//        ResultSet rs = null;
//        double faceAmount = 0;
//        try {
//            strSql = " select FFaceAmount from " + pub.yssGetTableName("Tb_Para_Security") + " where FSecurityCode = " + dbl.sqlString(securityCode);
//            rs = dbl.openResultSet(strSql); //根据证券代码在证券信息设置中获取相关证券代码的票面金额
//            while (rs.next()) {
//                faceAmount = rs.getDouble("FFaceAmount"); //票面金额
//            }
//            return faceAmount;
//        } catch (Exception e) {
//            throw new YssException("根据证券代码获取票面金额出错", e);
//        } finally {
//            dbl.closeResultSetFinal(rs);
//        }
//    }

    /**
     * 拆分券商承担的费用数据
     * @param brokerBear String
     * @return ArrayList
     */
    public ArrayList splitBrokerBear(String brokerBear) {
        String[] brokerBears = brokerBear.split(","); //用逗号拆分数据
        ArrayList alBears = new ArrayList(); //新建ArrayList
        for (int i = 0; i < brokerBears.length; i++) {
            alBears.add(brokerBears[i]); //将费用代码添加到alBears中
        }
        return alBears; //返回储存费用代码的ArrayList
    }

    /**
     * 根据回购证券代码获取回购天数
     * @param securityCode String
     * @return int
     * @throws YssException
     */
    private int getHGDays(String securityCode) throws YssException {
        String strSql = ""; //声明sql语句
        ResultSet rs = null; //声明结果集
        int hgDays = 0; //声明回购天数
        //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
        boolean haveInfo = false;
        try {
            //查询证券代码对应的回购天数
            strSql = " select FDuration from " + pub.yssGetTableName("Tb_Para_DepositDuration") + " where FDepDurCode in" +
                "(select FDepDurCode from " + pub.yssGetTableName("Tb_Para_Purchase") + " where FSecurityCode = " +
                dbl.sqlString(securityCode) + ") ";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	//add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
            	haveInfo = true;
                hgDays = rs.getInt("FDuration");
            }
            
            //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
            if(!haveInfo && !alShowHGZqdm.contains(securityCode)){
            	alShowHGZqdm.add(securityCode);
            }
            //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
            
            return hgDays; //返回回购天数
        } catch (Exception e) {
            throw new YssException("根据回购证券代码获取回购天数出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 用于判断基金资产是否为指数型基金资产或指标型基金资产
     * 返回值应该包括：1.INDEX(表示指数型基金资产) 2.ZB(表示指标型基金资产) 3.OTHER(表示非指标型或指数型基金资产)
     * @return String
     */
    private HashMap judgeAssetType(String portCode, java.util.Date tradeDate) throws YssException {
        String strSql = ""; //储存sql语句
        ResultSet rs = null;
        String subAssetType = null;
        String portCodee = null;
        try {
          	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            //在组合设置表中查询当前组合的资产子类型数据
   
        	  strSql = " select FAssetType, FSubAssetType, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
              "  where FPortCode in (" + operSql.sqlCodes(portCode) + ") ";

            
            //end by lidaolong
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                subAssetType = rs.getString("FSubAssetType"); //资产子类型
                portCodee = rs.getString("FPortCode"); //组合代码
                hmSubAssetType.put(portCodee, subAssetType);
            }

            return hmSubAssetType;
        } catch (Exception e) {
            throw new YssException("在组合设置表中查询相关组合代码的数据出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据证券标志和业务标志判断品种类型和品种子类型
     * @param securitySign String
     * @param businessSign String
     * @return String
     */
    public String judgeCatAndSubCat(String securitySign, String businessSign) {
        String categoryCode = null;
        String subCategoryCode = null;
        //转换的品种类型和品种子类型也可以通过XML配置（可后期考虑） 胡坤 20090625
        if (securitySign.equals("GP")) { //股票
            categoryCode = "EQ";
            if (businessSign.equals("KZZGP") || businessSign.equals("SH_ETF") || businessSign.equals("SG_ETF") ||
                businessSign.equals("MC") || businessSign.equals("MR")) { //可转债股票,ETF基金股票的申购和赎回,普通股票买入或卖出
                subCategoryCode = "EQ01"; //普通股
            }
            if (businessSign.equals("MR_ZS") || businessSign.equals("MC_ZS")) { //指数股票买入或卖出
                subCategoryCode = "EQ03"; //指数股票
            }
            if (businessSign.equals("MR_ZB") || businessSign.equals("MC_ZB")) { //指标股票买入或卖出
                subCategoryCode = "EQ04"; //指标股票
            }
        }
        if (securitySign.equals("ZQ")) { //债券
            categoryCode = "FI";
            if (businessSign.equals("KZZGP") || businessSign.equals("MC_KZZ") || businessSign.equals("MR_KZZ")) { //可转债债券，可转债买入或卖出
                subCategoryCode = "FI06"; //可转债
            }
            if (businessSign.equals("MR_GZ") || businessSign.equals("MC_GZ")) { //国债买入或卖出
                subCategoryCode = "FI12"; //国债
            }
            if (businessSign.equals("MR_DFZFZ") || businessSign.equals("MC_DFZFZ")) { //国债买入或卖出
                subCategoryCode = "FI16"; //地方政府债
            }
            if (businessSign.equals("MR_FLKZZ") || businessSign.equals("MC_FLKZZ")) { //分离可转债的买入或卖出
                subCategoryCode = "FI07"; //分离可转债
            }
            if (businessSign.equals("MR_QYZQ") || businessSign.equals("MC_QYZQ")) { //企业债的买入或卖出
                subCategoryCode = "FI09"; //企业债
            }
            if (businessSign.equals("MR_QYZQ_GS") || businessSign.equals("MC_QYZQ_GS")) { //公司债的买入或卖出
                subCategoryCode = "FI08"; //公司债
            }
            if (businessSign.equals("MR_ZCZQ") || businessSign.equals("MC_ZCZQ")) { //资产证券化产品的买入或卖出
                subCategoryCode = "FI10"; //资产证券化产品
            }
        }
        
        if (securitySign.equals("QZ")) { //权证
            categoryCode = "OP";
            if (businessSign.equals("MR_RGQZ") || businessSign.equals("MC_RGQZ")) { //认购权证的买入或卖出
                subCategoryCode = "OP03"; //认购权证
            }
            if (businessSign.equals("MR_RZQZ") || businessSign.equals("MC_RZQZ")) { //认沽权证的买入或卖出
                subCategoryCode = "OP04"; //认沽权证
            }
            if (businessSign.equals("XQ_RGQZ")) {
                subCategoryCode = "OP05"; //认购权证行权
            }
            if (businessSign.equals("XQ_RZQZ")) {
                subCategoryCode = "OP06"; //认沽权证行权
            }
        }
        if (securitySign.equals("JJ")) { //基金
            categoryCode = "TR";
            if (businessSign.equals("MR_FBS") || businessSign.equals("MC_FBS")) { //封闭式基金的买入或卖出
                subCategoryCode = "TR01"; //封闭式基金
            }
            if (businessSign.equals("SG_ETF") || businessSign.equals("SH_ETF") || //ETF基金的申购或赎回
                businessSign.equals("XJTD_SG") || businessSign.equals("XJTD_SH") || //EFT基金的申购或赎回的现金替代
                businessSign.equals("MR_ETF") || businessSign.equals("MC_ETF")) { //ETF基金的买入或卖出
                subCategoryCode = "TR04"; //ETF基金
            }
        }
        if (securitySign.equals("HG")) {
            categoryCode = "RE";
            if (businessSign.equals("MRHG_QYZQ")) {
                subCategoryCode = "RE04"; //企业债融券回购
            }
            if (businessSign.equals("MCHG_QYZQ")) {
                subCategoryCode = "RE03"; //企业债融资回购
            }
            if (businessSign.equals("MDMRHG")) {
                subCategoryCode = "RE05"; //买断式融券回购
            }
            if (businessSign.equals("MDMCHG")) {
                subCategoryCode = "RE06"; //买断式融资回购
            }
            if (businessSign.equals("MRHG")) {
                subCategoryCode = "RE08"; //融券回购
            }
            if (businessSign.equals("MCHG")) {
                subCategoryCode = "RE07"; //融资回购
            }
        }
        if (securitySign.equals("QY")) { //权益
            categoryCode = "EQ";
            subCategoryCode = "EQ06"; //配股
        }
        if (securitySign.equals("XG")) {
            categoryCode = "EQ";
            subCategoryCode = "EQ02"; //新股
        }
        if (securitySign.equals("XZ")) {
            categoryCode = "FI";
			//---edit by songjie 2012.02.16 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A start---//
            if(businessSign.equals("SG_KZZ") || businessSign.equals("FK_KZZ")){
            	subCategoryCode = "FI11"; //未上市可转债
            }
            if(businessSign.equals("SG_GZ")){
            	subCategoryCode = "FI13"; //未上市国债
            }
            if(businessSign.equals("SG_DFZFZ")){
            	subCategoryCode = "FI17"; //未上市地方政府债
            }
            if(businessSign.equals("SG_QYZQ")){
            	subCategoryCode = "FI14"; //未上市企业债
            }
            //---edit by songjie 2012.02.16 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A end---//
            subCategoryCode = "FI11"; //新债
        }
        if (securitySign.equals("B_GP")) { //B股
            categoryCode = "EQ";
            subCategoryCode = "EQ01";
        }
        return categoryCode + "\t" + subCategoryCode;
    }

    /**
     * 计算费用
     * @param feeAttribute FeeAttributeBean
     * @throws YssException
     */
    public void calculateFee(FeeAttributeBean feeAttribute) throws YssException {
        double FJsf = 0; //初始化经手费
        double FZgf = 0; //初始化证管费
        double FYhs = 0; //初始化印花税
        double FGhf = 0; //初始化过户费
        double FYj = 0; //初始化佣金
        double Ffxj = 0; //初始化风险金
        double Fqtf = 0; //初始化结算费
        double Fhggain = 0; //初始化回购收益
        double FBeforeGzlx = 0; //初始化税前债券利息
        double FGzlx = 0; //初始化税后债券利息
        double startMoney = 0; //起点金额

        if (feeAttribute.isComeFromQS()) {
            Fhggain = feeAttribute.getFhggain();
        }

        if(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj")){
            FJsf = feeAttribute.getFJsf();
            FZgf = feeAttribute.getFZgf();
            FGhf = feeAttribute.getFGhf();
            Fqtf = feeAttribute.getFqtf();
        }

        HashMap hmZQRate = null;// 用于储存债券的每百元债券利息
        HashMap hmPerZQRate = null;//用于储存债券利息表查询出的数据
        BondInterestBean bondInterest = null;
        String haveInfo = null;

        String securitySign = feeAttribute.getSecuritySign(); //获取证券标志
        String businessSign = feeAttribute.getBusinessSign(); //获取业务标志
        String zqdm = feeAttribute.getZqdm(); //获取证券代码
        String portCode = feeAttribute.getPortCode(); //获取组合代码
        String gsdm = feeAttribute.getGsdm(); //获取席位号
        String seatcode=feeAttribute.getSeatCode();//获取席位代码 add zhouxiang MS1299
        java.util.Date date = feeAttribute.getDate(); //获取交易日期
        //获取买卖标志 add by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
        String bs = feeAttribute.getBs(); 
        String tradeTypeCode = feeAttribute.getJyfs();//交易方式  ：大宗交易/普通交易

        double cjje = feeAttribute.getCjje(); //获取成交金额
        double cjjg = feeAttribute.getCjjg(); //获取成交价格
        double cjsl = feeAttribute.getCjsl(); //获取成交数量

        boolean jsfParamNum = false;//用于判断经手费的计算公式中是否包含国债利息参数
        boolean zgfParamNum = false;//用于判断证管费的计算公式中是否包含国债利息参数
        boolean yjParamNum = false;//用于判断佣金的计算公式中是否包含国债利息参数
        boolean fxjParamNum = false;//用于判断风险金的计算公式中是否包含国债利息参数

        CtlStock ctlStock = new CtlStock();
        ctlStock.setYssPub(pub);

        //获取数据接口参数设置的读数处理方式界面设置的参数
        ReadTypeBean readType = feeAttribute.getReadType();

        if (hmBrokerCode == null) {
            hmBrokerCode = feeAttribute.getHmBrokerCode();
        }
        if (hmFeeWay == null) {
            hmFeeWay = feeAttribute.getHmFeeWay();
        }
        if (hmRateSpeciesType == null) {
            hmRateSpeciesType = feeAttribute.getHmRateSpeciesType();
        }
        if (hmBrokerRate == null) {
            hmBrokerRate = feeAttribute.getHmBrokerRate();
        }
        if (hmExchangeBond == null) {
            hmExchangeBond = feeAttribute.getHmExchangeBond();
        }
        //  String brokerCode = (String) hmBrokerCode.get(gsdm);
        String brokerCode = (String) hmBrokerCode.get(seatcode); //edited by zhouxiang根据席位获取券商代码 MS01299    接口处理界面导入上海过户库时出现提示信息    QDV4赢时胜(测试)2010年6月12日2_B    
        FeeWayBean feeWay = (FeeWayBean) hmFeeWay.get(pub.getAssetGroupCode() + " " +
            portCode + " " + brokerCode + " " + seatcode); //获取交易接口参数设置的费用承担方向分页的相关数据

        if(feeWay == null){
            throw new YssException("请在交易接口参数设置界面设置已选组合的费用承担参数！");
        }

        BrokerRateBean brokerRate = null;
        ExchangeBondBean exchangeBond = null;

        String brokerBear = feeWay.getBrokerBear(); //获取由券商承担的费用数据
        ArrayList alBears = splitBrokerBear(brokerBear); //拆分费用数据
        
        //---需求406 QDV4南方东英2010年12月14日01_A lidaolong 20110114---//
        PublicMethodBean pmBean = new PublicMethodBean();
        pmBean.setYssPub(pub);
        //---需求406 QDV4南方东英2010年12月14日01_A lidaolong 20110114---//
        try {
            //-----------------------------计算各种费用
            if (securitySign.equalsIgnoreCase("GP")) {
                if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                    businessSign.equalsIgnoreCase("KZZGP")) { //ETF申购或赎回的股票或可转债股票

                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        FJsf = 0;
                        FZgf = 0;
                    }

                    FYhs = 0;

                    if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF")) { //ETF申购或赎回的股票
                        if (hmRateSpeciesType.get("1 SH GP ETF GHF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海ETF股票的过户费费率数据！");
                        }
                        //=====end
                       //需求406 QDV4南方东英2010年12月14日01_A lidaolong 20110114
                       final double dFaceAmount =pmBean.getFaceAmount(zqdm);
                        if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        	  //已把方法getFaceAmount移到com.yss.main.operdeal.datainterface.cnstock.pojo.PublicMethodBean中
                        	//=====需求406 QDV4南方东英2010年12月14日01_A lidaolong 20110114======
                        	if (dFaceAmount == 0 || dFaceAmount == -1) {
                                //过户费= roundit(成交数量× 股票面额 × 上海ETF股票过户费率,2)
                        		//若根据证券代码在证券信息设置表中查不到相关股票的票面金额的话，就默认股票面额为1
                        		//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
								if(tradeTypeCode.equals("DZ")){
                                    FGhf = YssFun.roundIt(YssD.mul(cjsl, 1, ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SH GP ETF GHF")).getBigExchange(), 0.01), 2); 
                        		}else{
                                    FGhf = YssFun.roundIt(YssD.mul(cjsl, 1, ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SH GP ETF GHF")).getExchangeRate(), 0.01), 2); 
                        		}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            } else {
                                //过户费= roundit(成交数量× 股票面额 × 上海ETF股票过户费率,2)
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){
                                    FGhf = YssFun.roundIt(YssD.mul(cjsl, dFaceAmount, ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SH GP ETF GHF")).getBigExchange(), 0.01), 2);
                            	}else{
                                    FGhf = YssFun.roundIt(YssD.mul(cjsl, dFaceAmount, ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SH GP ETF GHF")).getExchangeRate(), 0.01), 2);
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            }

                            //上海股票过户费起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP ETF GHF")).getStartMoney();

                            //如果上海股票过户费小于上海股票过户费的起点金额，那么上海股票的过户费就等于上海股票过户费的起点金额
                            if (cjsl != 0 && startMoney != 0 && FGhf < startMoney) {
                                FGhf = startMoney;
                            }
                        }
                    } else {
                        if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                            FGhf = 0; //如果业务标志为债转股，则过户费=0
                        }
                    }
                } else {
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        if (hmRateSpeciesType.get("1 SH GP JSF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海股票的经手费费率数据！");
                        }

                        //经手费=roundit（成交金额×上海股票经手费率,2）
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP JSF")).getBigExchange(), 0.01), 2);
                        }else{
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP JSF")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海股票经手费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP JSF")).getStartMoney();

                        //如果上海股票经手费小于上海股票经手费的起点金额，那么上海股票的经手费就等于上海股票经手费的起点金额
                        if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                            FJsf = startMoney;
                        }

                        if (hmRateSpeciesType.get("1 SH GP ZGF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海股票的证管费费率数据！");
                        }

                        //证管费=roundit（成交金额×上海股票证管费率,2）
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){
                            FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP ZGF")).getBigExchange(), 0.01), 2);
                        }else{
                            FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP ZGF")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海股票证管费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP ZGF")).getStartMoney();

                        //如果上海股票证管费小于上海股票证管费的起点金额，那么上海股票的证管费就等于上海股票证管费的起点金额
                        if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                            FZgf = startMoney;
                        }
                    }

                    if (bs.equals("B")) {
                        if (hmRateSpeciesType.get("1 SH GP B YHS") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海股票的买印花税费率数据！");
                        }

                        //买入印花税= roundit(买入成交金额×上海股票买印花税费率,2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){
                            FYhs = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP B YHS")).getBigExchange(), 0.01), 2);
                        }else{
                            FYhs = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP B YHS")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海股票买印花税起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP B YHS")).getStartMoney();

                        //如果上海股票买印花税小于上海股票买印花税的起点金额，那么上海股票的买印花税就等于上海股票买印花税的起点金额
                        if (cjje != 0 && startMoney != 0 && FYhs < startMoney) {
                            FYhs = startMoney;
                        }
                    }

                    if (bs.equals("S")) {
                        if (hmRateSpeciesType.get("1 SH GP S YHS") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海股票的卖印花税费率数据！");
                        }

                        //卖出印花税= roundit（卖出成交金额×上海股票卖印花税费率，2）
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){
                            FYhs = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP S YHS")).getBigExchange(), 0.01), 2);
                        }else{
                            FYhs = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                            		hmRateSpeciesType.get("1 SH GP S YHS")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海股票卖印花税起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP S YHS")).getStartMoney();

                        //如果上海股票卖印花税小于上海股票卖印花税的起点金额，那么上海股票的卖印花税就等于上海股票卖印花税的起点金额
                        if (cjje != 0 && startMoney != 0 && FYhs < startMoney) {
                            FYhs = startMoney;
                        }
                    }

                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        if (hmRateSpeciesType.get("1 SH GP GHF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海股票的过户费费率数据！");
                        }
                        //已把方法getFaceAmount移到com.yss.main.operdeal.datainterface.cnstock.pojo.PublicMethodBean中
                        //需求406  QDV4南方东英2010年12月14日01_A  lidaolong 20110114
                        final  double dFaceAmount =pmBean.getFaceAmount(zqdm);
                        //过户费= roundit（成交数量×面值×上海过户费率,2）
                        if (dFaceAmount == 0 || dFaceAmount == -1) {
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){
                                FGhf = YssFun.roundIt(YssD.mul(cjsl, 1,( (RateSpeciesTypeBean) 
                                        hmRateSpeciesType.get("1 SH GP GHF")).getBigExchange(), 0.01), 2);
                        	}else{
                                FGhf = YssFun.roundIt(YssD.mul(cjsl, 1,( (RateSpeciesTypeBean) 
                                        hmRateSpeciesType.get("1 SH GP GHF")).getExchangeRate(), 0.01), 2);
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        } else {
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){
                                FGhf = YssFun.roundIt(YssD.mul(cjsl, dFaceAmount,( (RateSpeciesTypeBean) 
                                        hmRateSpeciesType.get("1 SH GP GHF")).getBigExchange(), 0.01), 2);
                        	}else{
                                FGhf = YssFun.roundIt(YssD.mul(cjsl, dFaceAmount,( (RateSpeciesTypeBean) 
                                        hmRateSpeciesType.get("1 SH GP GHF")).getExchangeRate(), 0.01), 2);
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }

                        //上海股票过户费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP GHF")).getStartMoney();

                        //当计算的过户费小于上海股票过户费设置的起点金额，重新调整过户费（过户费=上海股票过户费起点金额）
                        if (cjsl != 0 && startMoney != 0 && FGhf < startMoney) {
                            FGhf = startMoney;
                        }
                    }
                }
                //story 2092 by zhouwei 20120106 上海股票风险金 不计算
                //----上海股票风险金----//
//                if(hmRateSpeciesType.get("1 SH GP FXJ") == null){
//                    throw new YssException("请在交易费率品种设置中设置上海股票的风险金费率数据！");
//                }
//
//                //风险金=RoundIt(成交金额×上海股票风险金利率), 2)
//                Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP FXJ")).getExchangeRate(), 0.01), 2);
//
//                //上海股票风险金起点金额
//                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH GP FXJ")).getStartMoney();
//
//                //当计算的风险金小于上海股票风险金设置的起点金额，重新调整风险金（风险金=上海股票风险金起点金额）
//                if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
//                    Ffxj = startMoney;
//                }
                //----上海股票风险金----//

                //----上海股票佣金----//
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 2 " + gsdm + " EQ"); ////获取上海股票对应组合代码和券商代码的佣金利率设置实例

                //业务标志不为（ETF申购或ETF赎回）的股票，才计算佣金
                if(!(businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF"))){
                    if(brokerRate == null){
                        throw new YssException("请在券商佣金利率设置中设置上海股票的佣金费率数据！");
                    }

                    //佣金=roundit(成交金额×佣金利率,2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                    }else{
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    if (alBears.contains("01")) { //01--股票经手费由券商承担
                        FYj = YssD.sub(FYj, FJsf); //佣金 = 佣金 - 经手费
                    }

                    if (alBears.contains("05")) { //05--股票证管费由券商承担
                        FYj = YssD.sub(FYj, FZgf); //佣金 = 佣金 - 证管费
                    }

                    if (alBears.contains("15")) { //15--股票过户费由券商承担
                        FYj = YssD.sub(FYj, FGhf); //佣金 = 佣金 - 过户费
                    }

                    if(alBears.contains("10")){ //10--股票风险金由券商承担
                        FYj = YssD.sub(FYj, Ffxj);
                    }

                    if(alBears.contains("14")){ //14--印花税由券商承担
                        FYj = YssD.sub(FYj, FYhs);
                    }
                    
                    //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                    ArrayList alReadType = (ArrayList) readType.getParameters();
                    if(alReadType.contains("05"))
    				{
    					FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
    					FYj = YssD.add(FYj, FZgf); //佣金 = 佣金 + 征管费
    				}    				
    				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                    
                    //如果佣金低于上海股票佣金的最小费用，则重新调整佣金（佣金=上海股票起点佣金）
                    if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney();
                    }
                    //----上海股票佣金----//
                }
            }

            if (securitySign.equalsIgnoreCase("JJ")) { //基金
                //不为ETF申购或赎回或ETF申购的现金替代或ETF赎回的现金替代
                if (!(businessSign.equalsIgnoreCase("SG_ETF") || businessSign.equalsIgnoreCase("SH_ETF") ||
                      businessSign.equalsIgnoreCase("XJTD_SG") || businessSign.equalsIgnoreCase("XJTD_SH"))) {
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        if (hmRateSpeciesType.get("1 SH JJ JSF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海基金的经手费费率数据！");
                        }

                        //经手费=roundit(成交金额×上海基金经手费率，2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){//大宗交易
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH JJ JSF")).getBigExchange(), 0.01), 2);
                        }else{
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH JJ JSF")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海基金经手费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH JJ JSF")).getStartMoney();

                        //当计算的基金经手费小于上海基金经手费设置的起点金额，重新调整基金经手费（基金经手费=上海基金经手费起点金额）
                        if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                            FJsf = startMoney;
                        }

                        if (hmRateSpeciesType.get("1 SH JJ ZGF") == null) {
                            throw new YssException("请在交易费率品种设置中设置上海基金的证管费费率数据！");
                        }

                        //证管费=roundit(成交金额×上海基金证管费率，2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        if(tradeTypeCode.equals("DZ")){//大宗交易
                            FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH JJ ZGF")).getBigExchange(), 0.01), 2);
                        }else{
                            FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH JJ ZGF")).getExchangeRate(), 0.01), 2);
                        }
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海基金证管费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH JJ ZGF")).getStartMoney();

                        //当计算的基金证管费小于上海基金证管费设置的起点金额，重新调整基金证管费（基金证管费=上海基金证管费起点金额）
                        if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                            FZgf = startMoney;
                        }
                    }
                }

                //不为ETF申购的现金替代或ETF赎回的现金替代
                if(!(businessSign.equalsIgnoreCase("XJTD_SG") || businessSign.equalsIgnoreCase("XJTD_SH"))){
                    //-----基金风险金------------------//
                    if(hmRateSpeciesType.get("1 SH JJ FXJ") == null){
                        throw new YssException("请在交易费率品种设置中设置上海基金的风险金费率数据！");
                    }

                    //风险金=RoundIt(成交金额×上海股票风险金利率), 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH JJ FXJ")).getBigExchange(), 0.01), 2);
                    }else{
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH JJ FXJ")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //上海基金风险金起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH JJ FXJ")).getStartMoney();

                    //当计算的基金风险金小于上海基金风险金设置的起点金额，重新调整基金风险金（基金风险金=上海基金风险金起点金额）
                    if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                     //-----基金风险金------------------//

                    //-------基金佣金-----------------//
                     brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                         brokerCode + " 2 " + gsdm + " TR"); //获取上海基金对应组合代码和券商代码的佣金利率设置实例

                     if(brokerRate == null){
                         throw new YssException("请在券商佣金利率设置中设置上海基金的佣金费率数据！");
                     }

                     //佣金=roundit(成交金额×佣金利率,2)
					 //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                     if(tradeTypeCode.equals("DZ")){//大宗交易
                    	 FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                     }else{
                    	 FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                     }
					 //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                     if (alBears.contains("04")) { //04--基金经手费由券商承担
                         FYj = YssD.sub(FYj, FJsf); //佣金 = 佣金 - 经手费
                     }

                     if (alBears.contains("07")) { //07--基金证管费由券商承担
                         FYj = YssD.sub(FYj, FZgf); //佣金 = 佣金 - 证管费
                     }

                     if(alBears.contains("13")){ //13--基金风险金由券商承担
                         FYj = YssD.sub(FYj, Ffxj);
                     }
                     
                     //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                     ArrayList alReadType = (ArrayList) readType.getParameters();
                     if(alReadType.contains("05"))
     				 {
     					FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
     					FYj = YssD.add(FYj, FZgf); //佣金 = 佣金 + 征管费
     				 }    				
     				 //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                    
                     //如果佣金低于上海基金佣金的最小费用，则重新调整佣金（佣金=上海基金起点佣金）
                     if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                         FYj = brokerRate.getStartMoney();
                     }
                     //-------基金佣金-----------------//
                }
            }

            if (securitySign.equalsIgnoreCase("QZ")) { //权证
                if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                    if (hmRateSpeciesType.get("1 SH QZ JSF") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海权证的经手费费率数据！");
                    }

                    //经手费=roundit(成交金额×上海权证经手费率，2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ JSF")).getBigExchange(), 0.01), 2);
                    }else{
                        FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ JSF")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //上海权证经手费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH QZ JSF")).getStartMoney();

                    //当计算的权证经手费小于上海权证经手费设置的起点金额，重新调整权证经手费（权证经手费=上海权证经手费起点金额）
                    if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                        FJsf = startMoney;
                    }

                    if (hmRateSpeciesType.get("1 SH QZ ZGF") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海权证的证管费费率数据！");
                    }

                    //证管费=roundit(成交金额×上海权证证管费率，2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ ZGF")).getBigExchange(), 0.01), 2);
                    }else{
                        FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ ZGF")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //上海权证证管费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH QZ ZGF")).getStartMoney();

                    //当计算的权证证管费小于上海权证证管费设置的起点金额，重新调整权证证管费（权证证管费=上海权证证管费起点金额）
                    if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                        FZgf = startMoney;
                    }

                    if (hmRateSpeciesType.get("1 SH QZ JIESUANF") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海权证的结算费费率数据！");
                    }

                    //结算费=RoundIt(成交金额 × 上海权证结算费, 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ JIESUANF")).getBigExchange(), 0.01), 2);
                    }else{
                        Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH QZ JIESUANF")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //上海权证结算费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH QZ JIESUANF")).getStartMoney();

                    //当计算的权证结算费小于上海权证结算费设置的起点金额，重新调整权证结算费（权证结算费=上海权证结算费起点金额）
                    if (cjje != 0 && startMoney != 0 && Fqtf < startMoney) {
                        Fqtf = startMoney;
                    }
                }

                //-------权证佣金-----------------//
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 2 " + gsdm + " OP"); //获取上海权证对应组合代码和券商代码的佣金利率设置实例

                if(brokerRate == null){
                    throw new YssException("请在券商佣金利率设置中设置上海权证的佣金费率数据！");
                }

                //佣金=roundit(成交金额×上海权证佣金利率,2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                }else{
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                if (alBears.contains("16")) { //16--权证经手费由券商承担
                    FYj = YssD.sub(FYj, FJsf); //佣金 = 佣金 - 经手费
                }
                if (alBears.contains("17")) { //17--权证证管费由券商承担
                    FYj = YssD.sub(FYj, FZgf); //佣金 = 佣金 - 证管费
                }
                if (alBears.contains("08")) { //08--权证结算费由券商承担
                    FYj = YssD.sub(FYj, Fqtf); //佣金 = 佣金 - 结算费
                }
                
                //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05"))
				 {
					FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
					FYj = YssD.add(FYj, FZgf); //佣金 = 佣金 + 征管费
				 }    				
				 //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                
                //如果佣金低于上海权证佣金的最小费用，则重新调整佣金（佣金=上海权证起点佣金）
                if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                    FYj = brokerRate.getStartMoney();
                }
                //-------权证佣金-----------------//

                //-----权证风险金------------------//
                if (hmRateSpeciesType.get("1 SH QZ FXJ") == null) {
                    throw new YssException("请在交易费率品种设置中设置上海权证的风险金费率数据！");
                }

                //风险金=RoundIt(成交金额×上海权证风险金利率), 2)
                Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType
                    .get("1 SH QZ FXJ")).getExchangeRate(), 0.01), 2);

                //上海权证风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH QZ FXJ")).getStartMoney();

                //当计算的权证风险金小于上海权证风险金设置的起点金额，重新调整权证风险金（权证风险金=上海权证风险金起点金额）
                if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                    Ffxj = startMoney;
                }
                //-----权证风险金------------------//
            }

            //------------------------------------------------------------------回购
            if (securitySign.equalsIgnoreCase("HG")) {
                //非买断式回购（包括非买断式融资回购和非买断式融券回购）
                if (!businessSign.equalsIgnoreCase("MDMRHG") || !businessSign.equalsIgnoreCase("MDMCHG")) {
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                    	if (hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" JSF") == null) {//证券代码前６位是回购品种代码。add by xuxuming,20091030.国内	MS00004
                            throw new YssException("请在交易费率品种设置中设置上海" + zqdm.substring(0, 6) + "回购的经手费费率数据！");
                        }

                        //经手费=roundit(成交金额×上海回购经手费率，2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" JSF")).getBigExchange(), 0.01), 2);
                    	}else{
                            FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                             	   hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" JSF")).getExchangeRate(), 0.01), 2);
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        //上海回购经手费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" JSF")).getStartMoney();

                        //当计算的回购经手费小于上海回购经手费设置的起点金额，重新调整回购经手费（回购经手费=上海回购经手费起点金额）
                        if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                            FJsf = startMoney;
                        }
                    }
                    int iExchangeFhggain=getiiExchangeFhggain(feeAttribute.getPortCode());//fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                    if(!feeAttribute.isComeFromQS()){
                        //回购收益= RoundIt(RoundIt((成交价格 / 100) / 360× 回购天数, 回购收益保留位数) × 成交数量 × 1000, 2)
                        Fhggain = YssFun.roundIt(YssD.mul(YssFun.roundIt(YssD.mul(YssD.
                        		//fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 	
                        	//edit by songjie 2011.09.05 BUG 2583 QDV4嘉实2011年08月26日01_B 
                        	//若回购计算保留位数为15位，则计算结果差一分钱
                            div(cjjg, 36000, 18), getHGDays(zqdm)), iExchangeFhggain), cjsl, 1000), 2);
                        //-------------------------------------end ---------------------------------
                    }
                }

                //对于买断式回购的收益是通过过户债券来计算，即取结算明细库中的回购收益,
                //因为没有结算明细库的记录，所以没有写相关代码对于买断式回购的经手费取结算明细库中的经手费字段。
                //结算明细库为中登的另一数据文件jsmxXXXXX.mdd（XXXXX 为清算编号），有JSF字段。该数据文件目前还未进行接口处理。
                else {
                    cjje = 5000;
                    FJsf = 120;
                    Fhggain = 100;
                }

                //-----回购风险金------------------//
                if(hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" FXJ") == null){
                    throw new YssException("请在交易费率品种设置中设置上海" + zqdm.substring(0, 6) + "回购的风险金费率数据！");
                }

                //风险金=RoundIt(成交金额×上海回购风险金利率), 2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType
                            .get("1 SH "+zqdm.substring(0, 6)+" FXJ")).getBigExchange(), 0.01), 2);
                }else{
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType
                            .get("1 SH "+zqdm.substring(0, 6)+" FXJ")).getExchangeRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //上海回购风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH "+zqdm.substring(0, 6)+" FXJ")).getStartMoney();

                //当计算的回购风险金小于上海回购风险金设置的起点金额，重新调整回购风险金（回购风险金=上海回购风险金起点金额）
                if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                    Ffxj = startMoney;
                }
                //-----回购风险金------------------//

                //-------回购佣金-----------------//
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 2 " + gsdm + " " + zqdm.substring(0, 6)); //获取上海回购对应组合代码和券商代码的佣金利率设置实例

                if(brokerRate == null){
                    throw new YssException("请在券商佣金利率设置中设置上海" + zqdm.substring(0, 6) + "回购的佣金费率数据！");
                }

                //佣金=roundit(成交金额×上海回购佣金利率,2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                }else{
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                if (alBears.contains("03")) { //03--回购经手费由券商承担
                    FYj = YssD.sub(FYj, FJsf); //佣金 = 佣金 - 经手费
                }

                if(alBears.contains("12")){//12--回购风险金由券商承担
                    FYj = YssD.sub(FYj, Ffxj); //佣金 = 佣金 - 风险金
                }
                
                //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05")&&!alReadType.contains("06"))////回购佣金包含经手费
				{
				   FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
				}    				
				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                
                //如果佣金低于上海回购佣金的最小费用，则重新调整佣金（佣金=上海回购起点佣金）
                if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                    FYj = brokerRate.getStartMoney();
                }
                //-------回购佣金-----------------//
            }

            //--------------------------------------------------------------------------------债券
            if (securitySign.equalsIgnoreCase("ZQ")) {
                //若要处理可分离债券的费用，先要到数据接口参数设置的读数处理方式中查询可分离债券
                //归入企业债还是分离可转债，若归入企业债，就把可分离债券的业务标志改为企业债,
                //系统计算费用时会按企业债的算法计算可分离债券
                if((businessSign.equals("MR_FLKZZ") || businessSign.equals("MC_FLKZZ")) && readType.getWBSBelong().indexOf("01")>-1){
                    if(businessSign.equals("MR_FLKZZ")){
                        businessSign = "MR_QYZQ";
                    }
                    if(businessSign.equals("MC_FLKZZ")){
                        businessSign = "MC_QYZQ";
                    }
                }

                //成交数量：过户库的cjsl×10
                if (feeAttribute.getSelectedFee() == null) {
                    cjsl = (int) (YssD.mul(cjsl, 10));
                }

                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 2 " + gsdm + " FI"); //获取上海债券对应组合代码和券商代码的佣金利率设置实例

                if(brokerRate == null){
                    throw new YssException("请在券商佣金利率设置中设置上海债券的佣金费率数据！");
                }

                hmPerZQRate = super.getPerHundredZQRate(zqdm,date);//在债券利息表中查询债券利息数据

                haveInfo = (String)hmPerZQRate.get("haveInfo");//判断债券利息表中是否有当前债券利息数据

                if(haveInfo.equals("false")){ //表示债券利息表中没有当前债券利息数据
                    //计算债券的税前每百元债券利息，税后每百元债券利息并储存到哈希表中
                	
                	//修改calculateZQRate方法的参数，由传入两个参数改为传入三个参数 
                	//edit by songjie 2009.12.21 MS00847 QDV4赢时胜（北京）2009年11月30日03_B
                    hmZQRate = super.calculateZQRate(zqdm, date, bs, portCode);

                    //add by songjie 2010.03.22 QDII国内：MS00925 
                    //QDV4赢时胜（测试）2010年03月19日03_AB
                    if(((String)hmZQRate.get("haveInfo")).equals("false")){
                    	if(!alShowZqdm.contains(zqdm)){
                    		alShowZqdm.add(zqdm);
                    	}
                    }
                    //add by songjie 2010.03.22 QDII国内：MS00925 
                    //QDV4赢时胜（测试）2010年03月19日03_AB
                    
					FBeforeGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("SQGZLX")), cjsl), 2);
					FGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble((String) hmZQRate.get("GZLX")), cjsl), 2);

                    bondInterest = new BondInterestBean();//新建债券利息实例
                    bondInterest.setSecurityCode(zqdm);//设置证券代码
                    bondInterest.setIntAccPer100(new BigDecimal((String)hmZQRate.get("SQGZLX")));//设置税前百元利息
                    bondInterest.setSHIntAccPer100(new BigDecimal((String)hmZQRate.get("GZLX")));//设置税后百元利息

                    if(feeAttribute.getSelectedFee() == null){
                        if (!alZQCodes.contains(zqdm)) {
                            alZQCodes.add(zqdm);
                            alZQInfo.add(bondInterest);//将债券利息实例添加到列表中
                        }
                    }
                }
                else{
					FGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(((String) hmPerZQRate
									.get("PerGZLX"))), readType.getExchangePreci()), cjsl), 2);
					FBeforeGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(((String) hmPerZQRate
									.get("SHPerGZLX"))), readType.getExchangePreci()), cjsl), 2);
                }

                if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                    if (hmRateSpeciesType.get("1 SH ZQ JSF") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海债券的经手费费率数据！");
                    }

                    //经手费=RoundIt(成交金额 × 上海债券经手费率, 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH ZQ JSF")).getBigExchange(), 0.01), 2);
                    }else{
                        FJsf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH ZQ JSF")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    if (hmRateSpeciesType.get("1 SH ZQ ZGF") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海债券的证管费费率数据！");
                    }

                    if (!(businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ"))) {
	                    //证管费=RoundIt(成交金额 × 上海债券证管费率, 2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
    	                    FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
    	                    		hmRateSpeciesType.get("1 SH ZQ ZGF")).getBigExchange(), 0.01), 2);
                    	}else{
    	                    FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
    	                    		hmRateSpeciesType.get("1 SH ZQ ZGF")).getExchangeRate(), 0.01), 2);
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    }
                    if (hmRateSpeciesType.get("1 SH ZQ FXJ") == null) {
                        throw new YssException("请在交易费率品种设置中设置上海债券的风险金费率数据！");
                    }
                }

                //佣金 =  RoundIt(成交金额 × 上海债券佣金利率), 2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                }else{
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //国债
                if (businessSign.equalsIgnoreCase("MR_GZ") || businessSign.equalsIgnoreCase("MC_GZ") ||
                	businessSign.equalsIgnoreCase("MR_DFZFZ") || businessSign.equalsIgnoreCase("MC_DFZFZ")) { //国债、地方政府债的买入或卖出
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 01");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所国债的相关参数！");
                    }
                }

                //企业债
                if (businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ")) { //企业债券的买入或卖出
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 02");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所企业债的相关参数！");
                    }
                }

                //分离可转债
                if (businessSign.equalsIgnoreCase("MR_FLKZZ") || businessSign.equalsIgnoreCase("MC_FLKZZ")) { //分离可转债的买入或卖出
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 04");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所分离可转债的相关参数！");
                    }
                }

                //公司债
                if (businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")) { //公司债的买入或卖出
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 05");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所公司债的相关参数！");
                    }
                }

                if (businessSign.equalsIgnoreCase("KZZGP") || businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")) {
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 03");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所可转债的相关参数！");
                    }
                }

                if (businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ")) {
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 01 06");

                    if(exchangeBond == null){
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置上交所资产证券化产品的相关参数！");
                    }
                }

                if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        //若债券的交易方式为净价交易，则计算费用时涉及到的成交金额 = 成交金额 + 税前债券利息
                        //经手费=RoundIt(（成交金额 + 税前债券利息）×上海债券经手费率, 2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            FJsf = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
                                    hmRateSpeciesType.get("1 SH ZQ JSF")).getBigExchange(), 0.01), 2);
                    	}else{
                            FJsf = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
                                    hmRateSpeciesType.get("1 SH ZQ JSF")).getExchangeRate(), 0.01), 2);
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                        jsfParamNum = true;
                        if (!(businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ"))) {
	                        //证管费=RoundIt(（成交金额 + 税前债券利息）×上海债券证管费率, 2)
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){//大宗交易
    	                        FZgf = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
    		                               hmRateSpeciesType.get("1 SH ZQ ZGF")).getBigExchange(), 0.01), 2);
                        	}else{
    	                        FZgf = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
    		                               hmRateSpeciesType.get("1 SH ZQ ZGF")).getExchangeRate(), 0.01), 2);
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                        zgfParamNum = true;
                    }

                    //资产证券买入或卖出
                    if (businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ")) {
                        if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                            if (hmRateSpeciesType.get("1 SH ZQ JIESUANF") == null) {
                                throw new YssException("请在交易费率品种设置中设置上海债券的结算费费率数据！");
                            }

                            //结算费 = RoundIt((成交金额 + 税前债券利息) × 上海债券结算费, 2)
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                                Fqtf = YssFun.roundIt(YssD.mul(cjje + FBeforeGzlx, ( (RateSpeciesTypeBean) 
                                 	   hmRateSpeciesType.get("1 SH ZQ JIESUANF")).getBigExchange(), 0.01), 2);
                            }else{
                                Fqtf = YssFun.roundIt(YssD.mul(cjje + FBeforeGzlx, ( (RateSpeciesTypeBean) 
                                 	   hmRateSpeciesType.get("1 SH ZQ JIESUANF")).getExchangeRate(), 0.01), 2);
                            }
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                    }

                    if (exchangeBond.getCommisionType().equals("01")) { //01-按净价加利息税计算佣金
                        //佣金= RoundIt(（成交金额+国债利息20%利息税）×上海债券佣金利率), 2) 
                    	//   = RoundIt((成交金额 + 税前国债利息 - 税后债券利息) * 上海债券佣金利率)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                        	FYj = YssFun.roundIt(YssD.mul(
                            	    YssD.add(cjje, YssD.sub(
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjsl),
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("GZLX")), cjsl))),
                                brokerRate.getBigYjRate(), 0.01), 2);
                    	}else{
                        	FYj = YssFun.roundIt(YssD.mul(
                            	    YssD.add(cjje, YssD.sub(
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjsl),
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("GZLX")), cjsl))),
                                brokerRate.getYjRate(), 0.01), 2);
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        yjParamNum = true;
                    }

                    if (exchangeBond.getCommisionType().equals("02")) { //02-按全价计算佣金
                        //佣金= RoundIt(（成交金额 + 国债利息）× 上海债券佣金利率), 2)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            FYj = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),
                                    brokerRate.getBigYjRate(), 0.01), 2);
                    	}else{
                            FYj = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),
                                    brokerRate.getYjRate(), 0.01), 2);
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        yjParamNum = true;
                    }

                    //风险金=RoundIt(（成交金额 + 国债利息）× 上海债券风险金利率), 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
                                hmRateSpeciesType.get("1 SH ZQ FXJ")).getBigExchange(), 0.01), 2);
                    }else{
                        Ffxj = YssFun.roundIt(YssD.mul(YssD.add(cjje, FBeforeGzlx),( (RateSpeciesTypeBean) 
                                hmRateSpeciesType.get("1 SH ZQ FXJ")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    fxjParamNum = true;
                }

                if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
                	if (exchangeBond.getCommisionType().equals("00")) { //02-按净价计算佣金
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                    		FYj = YssFun.roundIt(YssD.mul(YssD.sub(cjje, FGzlx),
                                    brokerRate.getBigYjRate(), 0.01), 2);
                		}else{
                    		FYj = YssFun.roundIt(YssD.mul(YssD.sub(cjje, FGzlx),
                                    brokerRate.getYjRate(), 0.01), 2);
                		}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                	}
                    //资产证券买入或卖出
                    if (businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ")) {
                        if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                            if (hmRateSpeciesType.get("1 SH ZQ JIESUANF") == null) {
                                throw new YssException("请在交易费率品种设置中设置上海债券的结算费费率数据！");
                            }

                            //结算费 = RoundIt(成交金额 × 上海债券结算费, 2)
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                                Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                                 	   hmRateSpeciesType.get("1 SH ZQ JIESUANF")).getBigExchange(), 0.01), 2);
                            }else{
                                Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                                 	   hmRateSpeciesType.get("1 SH ZQ JIESUANF")).getExchangeRate(), 0.01), 2);
                            }
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                    }

                    //风险金=RoundIt(成交金额×上海债券风险金利率), 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH ZQ FXJ")).getBigExchange(), 0.01), 2);
                    }else{
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) 
                         	   hmRateSpeciesType.get("1 SH ZQ FXJ")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")) { //公司债
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        //如果是以122开头的债券（公司债），经手费单笔上限金额<150，即大于150时取150，小于等于150时取实际值。
                        if (FJsf >= 150) {
                            FJsf = 150;
                        }

                        //获取上海债券经手费的起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH ZQ JSF")).getStartMoney();

                        //若成交金额不为零 且 起点金额不为零 且 起点金额小于150 且 经手费小于起点金额
                        if (cjje != 0 && startMoney != 0 && startMoney <= 150 && FJsf < startMoney) {
                            FJsf = startMoney;
                        }
                    }
                }

                if (businessSign.equalsIgnoreCase("MR_ZCZQ") || businessSign.equalsIgnoreCase("MC_ZCZQ")) {
                    if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                        //上海债券结算费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH ZQ JIESUANF")).getStartMoney();

                        //若结算费小于上海债券结算费起点金额，则结算费等于上海债券结算费起点金额
                        if (cjje != 0 && startMoney != 0 && Fqtf < startMoney) {
                            Fqtf = startMoney;
                        }
                    }
                }

                //上海债券风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH ZQ FXJ")).getStartMoney();

                //如果风险金小于上海债券风险金起点金额，则重新调整风险金（风险金=上海债券风险金起点金额）
                if(fxjParamNum){
                    if (cjje != 0 && cjsl != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                }
                else{
                    if (cjje != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                }

                if(!(feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj"))){
                    //上海债券经手费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH ZQ JSF")).getStartMoney();

                    //若经手费小于上海债券经手费起点金额，则经手费等于上海债券经手费起点金额
                    if (jsfParamNum) {
                        if (cjje != 0 && cjsl != 0 && startMoney != 0 && FJsf < startMoney && ! (businessSign.equalsIgnoreCase("MR_QYZQ_GS") ||
                            businessSign.equalsIgnoreCase("MC_QYZQ_GS"))) {
                            FJsf = startMoney;
                        }
                    } else {
                        if (cjje != 0 && startMoney != 0 && FJsf < startMoney && ! (businessSign.equalsIgnoreCase("MR_QYZQ_GS") ||
                            businessSign.equalsIgnoreCase("MC_QYZQ_GS"))) {
                            FJsf = startMoney;
                        }
                    }

                    //上海债券证管费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SH ZQ ZGF")).getStartMoney();

                    //若证管费小于上海债券证管费起点金额，则证管费等于上海债券证管费起点金额
                    if (zgfParamNum) {
                        if (cjje != 0 && cjsl != 0 && startMoney != 0 && FZgf < startMoney) {
                            FZgf = startMoney;
                        }
                    } else {
                        if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                            FZgf = startMoney;
                        }
                    }
                }

                if (alBears.contains("02")) { //02--债券经手费由券商承担
                    FYj = YssD.sub(FYj, FJsf); //佣金 = 佣金 - 经手费
                }

                if (alBears.contains("06")) { //06--债券证管费由券商承担
                    FYj = YssD.sub(FYj, FZgf); //佣金 = 佣金 - 证管费
                }

                if (alBears.contains("18")) { //18--债券结算费由券商承担
                    FYj = YssD.sub(FYj, Fqtf); //佣金 = 佣金 - 结算费
                }

                if(alBears.contains("11")){//11--债券风险金由券商承担
                    FYj = YssD.sub(FYj, Ffxj); //佣金 = 佣金 - 风险金
                }
                
                //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05"))
				 {
					FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
					FYj = YssD.add(FYj, FZgf); //佣金 = 佣金 + 征管费
				 }    				
				 //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end

                //如果上海债券佣金小于上海债券佣金的起点金额，则上海债券佣金等于上海债券佣金的起点金额
                if(yjParamNum){
                    if (cjje != 0 && cjsl != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney();
                    }
                }
                else{
                    if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney();
                    }
                }
            }
            //--------------------------------------------------------------------------------债券
            
            if (securitySign.equalsIgnoreCase("B_GP")) {//B股
				if (!(feeAttribute.getSelectedFee() != null && feeAttribute
						.getSelectedFee().equals("FYj"))) {
					if (hmRateSpeciesType.get("1 SH B_GP JSF") == null) {
						throw new YssException("请在交易费率品种设置中设置上海B股经手费费率！");
					}

					//经手费
					if(tradeTypeCode.equals("DZ")){
						//大宗交易经手费 = roundit（成交金额×上海B股大宗经手费率,2）
						FJsf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP JSF")).getBigExchange(),
								0.01), 2);
					}else{
						// 普通交易经手费=roundit（成交金额×上海B股经手费率,2）
						FJsf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP JSF")).getExchangeRate(),
								0.01), 2);
					}

					// 上海B股经手费起点金额
					startMoney = ((RateSpeciesTypeBean) hmRateSpeciesType
							.get("1 SH B_GP JSF")).getStartMoney();

					// 如果上海B股经手费小于上海B股经手费的起点金额，那么上海B股的经手费就等于上海B股经手费的起点金额
					if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
						FJsf = startMoney;
					}

					if (hmRateSpeciesType.get("1 SH B_GP ZGF") == null) {
						throw new YssException("请在交易费率品种设置中设置上海B股证管费费率！");
					}

					if(tradeTypeCode.equals("DZ")){
						// 大宗交易证管费=roundit（成交金额×上海B股证管费大宗交易费率,2）
						FZgf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP ZGF")).getBigExchange(),
								0.01), 2);
					}else{
						// 普通交易证管费=roundit（成交金额×上海B股证管费率,2）
						FZgf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP ZGF")).getExchangeRate(),
								0.01), 2);
					}

					// 上海B股证管费起点金额
					startMoney = ((RateSpeciesTypeBean) hmRateSpeciesType
							.get("1 SH B_GP ZGF")).getStartMoney();

					// 如果上海B股证管费小于上海B股证管费的起点金额，那么上海B股的证管费就等于上海B股证管费的起点金额
					if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
						FZgf = startMoney;
					}
					
					//上海B股结算费
					if (hmRateSpeciesType.get("1 SH B_GP JIESUANF") == null) {
						throw new YssException("请在交易费率品种设置中设置上海B股结算费费率！");
					}
					
					if(tradeTypeCode.equals("DZ")){
						// 大宗交易结算费=RoundIt(成交金额 × 上海B股结算费大宗交易费率, 2)
						Fqtf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP JIESUANF")).getBigExchange(),
								0.01), 2);
					}else{
						// 普通结算费=RoundIt(成交金额 × 上海B股结算费率, 2)
						Fqtf = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP JIESUANF")).getExchangeRate(),
								0.01), 2);
					}

					// 上海B股结算费起点金额
					startMoney = ((RateSpeciesTypeBean) hmRateSpeciesType
							.get("1 SH B_GP JIESUANF")).getStartMoney();

					// 如果上海B股结算费小于上海B股结算费的起点金额，那么上海B股结算费就等于上海B股结算费的起点金额
					if (cjje != 0 && startMoney != 0 && Fqtf < startMoney) {
						Fqtf = startMoney;
					}
				}

				if (bs.equals("B")) {
					if (hmRateSpeciesType.get("1 SH B_GP B YHS") == null) {
						throw new YssException("请在交易费率品种设置中设置上海B股买印花税费率！");
					}

					if(tradeTypeCode.equals("DZ")){
						// 大宗交易印花税= roundit(买入成交金额×上海B股票买印花税大宗交易费率,2)
						FYhs = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP B YHS")).getBigExchange(),
								0.01), 2);
					}else{
						// 印花税= roundit(买入成交金额×上海B股票买印花税费率,2)
						FYhs = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP B YHS")).getExchangeRate(),
								0.01), 2);
					}

					// 上海B股买印花税起点金额
					startMoney = ((RateSpeciesTypeBean) hmRateSpeciesType
							.get("1 SH B_GP B YHS")).getStartMoney();

					// 如果上海B股买印花税小于上海B股买印花税的起点金额，那么上海B股的买印花税就等于上海B股买印花税的起点金额
					if (cjje != 0 && startMoney != 0 && FYhs < startMoney) {
						FYhs = startMoney;
					}
				}

				if (bs.equals("S")) {
					if (hmRateSpeciesType.get("1 SH B_GP S YHS") == null) {
						throw new YssException("请在交易费率品种设置中设置上海B股卖印花税费率！");
					}

					if(tradeTypeCode.equals("DZ")){
						// 大宗交易卖出印花税= roundit（卖出成交金额×	上海B股票卖印花税大宗交易费率,2)
						FYhs = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP S YHS")).getBigExchange(),
								0.01), 2);
					}else{
						// 普通交易卖出印花税= roundit（卖出成交金额×	上海B股票卖印花税费率,2)
						FYhs = YssFun.roundIt(YssD.mul(cjje,
								((RateSpeciesTypeBean) hmRateSpeciesType
										.get("1 SH B_GP S YHS")).getExchangeRate(),
								0.01), 2);
					}

					// 上海B股卖印花税起点金额
					startMoney = ((RateSpeciesTypeBean) hmRateSpeciesType
							.get("1 SH B_GP S YHS")).getStartMoney();

					// 如果上海B股卖印花税小于上海B股卖印花税的起点金额，那么上海B股的卖印花税就等于上海B股卖印花税的起点金额
					if (cjje != 0 && startMoney != 0 && FYhs < startMoney) {
						FYhs = startMoney;
					}
				}

                //----上海B股佣金----//
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 2 " + gsdm + " EQ B"); ////获取上海B股对应组合代码和券商代码的佣金利率设置实例

				if (brokerRate == null) {
					throw new YssException("请在券商佣金利率设置中设置上海B股佣金费率数据！");
				}

				// 佣金=roundit(成交金额×上海B股佣金利率,2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
				if(tradeTypeCode.equals("DZ")){
					FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(),0.01), 2);
				}else{
					FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(),0.01), 2);
				}
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

				if (alBears.contains("01")) { // 01--股票经手费由券商承担
					FYj = YssD.sub(FYj, FJsf); // 佣金 = 佣金 - 经手费
				}

				if (alBears.contains("05")) { // 05--股票证管费由券商承担
					FYj = YssD.sub(FYj, FZgf); // 佣金 = 佣金 - 证管费
				}

				if (alBears.contains("19")) { // 19--结算费由券商承担
					FYj = YssD.sub(FYj, Fqtf);//佣金 = 佣金 - 结算费
				}
                
				 //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                 ArrayList alReadType = (ArrayList) readType.getParameters();
                 if(alReadType.contains("05"))
				 {
					FYj = YssD.add(FYj, FJsf); //佣金 = 佣金 + 经手费
					FYj = YssD.add(FYj, FZgf); //佣金 = 佣金 + 征管费
				 }    				
				 //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                
				// 如果佣金低于上海B股佣金的最小费用，则重新调整佣金（佣金=上海B股起点佣金）
				if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
					FYj = brokerRate.getStartMoney();
				}
				// ----上海B股佣金----//
            }
            
            //-----------------------------计算各种费用

            //---add by songjie 2012.02.14 BUG 3854 QDV4赢时胜(测试)2012年02月12日01_B start---//
            if(securitySign.equals("XZ") && (businessSign.equals("ZQ_KZZ") || businessSign.equals("KZZXZ"))){//若为新债、新债中签    modify by zhouwei 20120417老股东配售bug4183
            //成交数量：过户库的cjsl×10
            	if (feeAttribute.getSelectedFee() == null) {
            		cjsl = (int) (YssD.mul(cjsl, 10));
            	}
            }
            //---add by songjie 2012.02.14 BUG 3854 QDV4赢时胜(测试)2012年02月12日01_B end---//
            
            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FJsf")) {
                feeAttribute.setFJsf(FJsf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FZgf")) {
                feeAttribute.setFZgf(FZgf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FGhf")) {
                feeAttribute.setFGhf(FGhf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYhs")) {
                feeAttribute.setFYhs(FYhs);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("Fqtf")) {
                feeAttribute.setFqtf(Fqtf);
                return;
            }

            if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj")) {
                feeAttribute.setFYj(FYj);
                return;
            }

            feeAttribute.setCjje(cjje); //设置成交金额
            feeAttribute.setCjsl(cjsl); //设置成交数量
            feeAttribute.setFJsf(FJsf); //设置经手费
            feeAttribute.setFZgf(FZgf); //设置证管费
            feeAttribute.setFYhs(FYhs); //设置印花税
            feeAttribute.setFGhf(FGhf); //设置过户费
            feeAttribute.setFYj(FYj); //设置佣金
            feeAttribute.setFfxj(Ffxj); //设置风险金
            feeAttribute.setFqtf(Fqtf); //设置结算费
            feeAttribute.setFhggain(Fhggain); //设置回购收益
            feeAttribute.setFBeforeGzlx(FBeforeGzlx); //设置税前债券利息
            feeAttribute.setFGzlx(FGzlx); //设置税后债券利息
        } catch (Exception e) {
            throw new YssException("计算费用出错", e);
        }
    }

    /**
     * 根据席位代码查询席位类型
     * @param seatCode String
     * @return String
     * @throws YssException
     */
    public HashMap getSeatType() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String seatType = "";
        String sseatCode = "";
        try {
//        	edit by songjie 2010.06.12 将FSeatCode 改为FSeatNum
            strSql = "select FSeatType, FSeatNum from " + pub.yssGetTableName("Tb_Para_TradeSeat") +
                " where FCheckState = 1 and FSeatNum in(select distinct GSDM from SHGH) ";
            rs = dbl.openResultSet(strSql); //根据席位代码查询席位类型
            while (rs.next()) {
                seatType = rs.getString("FSeatType"); //席位类型
                //edit by songjie 2010.06.12 将FSeatCode改为FSeatNum
                sseatCode = rs.getString("FSeatNum");//席位代码
                hmSeatType.put(sseatCode, seatType);
            }
            return hmSeatType;
        } catch (Exception e) {
            throw new YssException("根据席位代码查询席位类型出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据申请编号查询业务标志为债转股债券的证券代码
     * @param sqbh String
     * @return HashMap
     * @throws YssException
     */
    private HashMap getZZGSec(String sqbh) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String haveConvertSec = "false";
        String oldZqdm = "";
        HashMap hmZZGSec = new HashMap();
        try {
            strSql = " select sqbh,zqdm from SHGH where SQBH = " + dbl.sqlString(sqbh) +
                " and (zqdm like '11%' or zqdm like '10%') and cjje = 0 and cjjg = 0 ";
            rs = dbl.openResultSet(strSql); //根据申请编号查询业务标志为ETF申购或赎回的证券代码
            while (rs.next()) {
                haveConvertSec = "true";
                oldZqdm = rs.getString("ZQDM");
            }

            hmZZGSec.put("haveConvertSec", haveConvertSec); //将判断是否有债转股债券数据的值插入hmZZGSec
            hmZZGSec.put("oldZqdm", oldZqdm); //将债转股债券的证券代码插入hmZZGSec

            return hmZZGSec;
        } catch (Exception e) {
            throw new YssException("根据申请编号查询业务标志为债转股债券的证券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 用于判断是否有ETF申购赎回的股票数据
     * @param sqbh String
     * @return boolean
     * @throws YssException
     */
    private boolean getETFGP(String sqbh) throws YssException {
        boolean haveConvertSec = false;
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = " select sqbh from SHGH where SQBH = " + dbl.sqlString(sqbh) +
                " and zqdm like '6%' and cjje = 0 and cjjg = 0 "; //在SHGH表中查询相同申请编号的债转股股票的数据
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                haveConvertSec = true; //表示有债转股股票的数据
                break;
            }

            return haveConvertSec; //返回判断结果
        } catch (Exception e) {
            throw new YssException("根据申请编号查询业务标志为ETF申购或赎回的股票的证券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据申请编号查询业务标志为ETF申购或赎回的证券代码
     * @param sqbh String
     * @return String
     * @throws YssException
     */
    private HashMap getETFSec(String sqbh) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String haveETFSec = "false";
        String zqdmETF = "";
        HashMap hmETFSec = new HashMap();
        try {
            strSql = " select sqbh,zqdm from SHGH where SQBH = " + dbl.sqlString(sqbh) +
                " and zqdm in('510050','510180','510880')";
            rs = dbl.openResultSet(strSql); //根据申请编号查询业务标志为ETF申购或赎回的证券代码
            while (rs.next()) {
                haveETFSec = "true"; //判断有ETF基金数据
                zqdmETF = rs.getString("ZQDM"); //ETF基金证券代码
            }

            hmETFSec.put("haveETFSec", haveETFSec); //将判断是否有ETF基金数据的值插入hmETFSec
            hmETFSec.put("zqdmETF", zqdmETF); //将ETF证券代码插入hmETFSec

            return hmETFSec;
        } catch (Exception e) {
            throw new YssException("根据申请编号查询业务标志为ETF申购或赎回的证券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 若证券类型为ETF现金替代的话，查询ETF申购或赎回的证券代码
     * @param zqdm String
     * @param sqbh String
     * @return HashMap
     * @throws YssException
     */
    private HashMap getETFSecForXJTD(String zqdm, String sqbh) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String haveConvertSec = "false";
        String zqdmETF = "";
        HashMap hmETFSec = new HashMap();
        try {
            strSql = " select sqbh,zqdm from SHGH where ZQDM <> " + dbl.sqlString(zqdm) +
                " and ZQDM in ('510050','510180','510880') and SQBH = " + dbl.sqlString(sqbh);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                haveConvertSec = "true";
                zqdmETF = rs.getString("zqdm");
                break;
            }

            hmETFSec.put("haveConvertSec", haveConvertSec); //将判断是否有ETF基金数据的值插入hmETFSec
            hmETFSec.put("zqdmETF", zqdmETF); //将ETF证券代码插入hmETFSec

            return hmETFSec;
        } catch (Exception e) {
            throw new YssException("根据申请编号查询业务标志为ETF申购或赎回的证券代码出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 根据参数判断配股缴款的业务子类型
     * @param fDate Date
     * @param zqdm String
     * @param seatType String
     * @param portCode String
     * @return String
     * @throws YssException
     */
    private String judgePGJK(java.util.Date fDate, String zqdm, String seatType, String portCode) throws YssException {
        String strSql = null;
        ResultSet rs = null;
        String convertedZqdm = "";
        boolean isPGJK = false;

        java.util.Date recordDate = null;
        java.util.Date exRightDate = null;
        java.util.Date expirationDate = null;

        boolean havePGInfo = false;
        try {
            //配股权益表中储存的证券代码为转换后的证券代码，convertRule.xml文件中的原始代码以‘70’打头的配股缴款数据转换后的证券代码是以'600'打头的数据
            if (YssFun.left(zqdm, 2).equals("70")) {
                convertedZqdm = "600" + YssFun.right(zqdm, 3) + " CG"; //对证券代码进行转换
            }

            //配股权益表中储存的证券代码为转换后的证券代码，convertRule.xml文件中的原始代码以‘76’打头的配股缴款数据转换后的证券代码是以'601'打头的数据
            if (YssFun.left(zqdm, 2).equals("76")) {
                convertedZqdm = "601" + YssFun.right(zqdm, 3) + " CG"; //对证券代码进行转换
            }

            strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                //edit by songjie 2010.03.12 MS00901 QDII4.1赢时胜上海2010年03月05日01_B
                " where FTSecurityCode = " + dbl.sqlString(convertedZqdm) + " and FCheckState = 1 "
				//---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
                +" and FRecordDate<="+dbl.sqlDate(fDate)+" and FExpirationDate>="+dbl.sqlDate(fDate)
                +" and FExpirationDate<FExRightDate order by FRecordDate";//先缴款后除权
			    //---add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            rs = dbl.openResultSet(strSql); //在配股权益表中查询相关证券代码的数据
			//edit by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
            if (rs.next()) {
                havePGInfo = true;
				//add by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A
                isPGJK = true; //表示为配股缴款
                recordDate = rs.getDate("FRecordDate"); //登记日
                exRightDate = rs.getDate("FExRightDate"); //除权日
                expirationDate = rs.getDate("FExpirationDate"); //缴款截止日
				//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A start---//
//                //若数据日期 >= 权益登记日
//                if (fDate.after(recordDate) || fDate.equals(recordDate)) {
//                    isPGJK = true; //表示为配股缴款
//                    break;
//                }
				//---delete by zhouwei 2012.05.17 STORY 2538 QDV4赢时胜(上海开发部)2012年04月21日01_A end---//
            }

            if (isPGJK) {
                if (exRightDate.before(expirationDate)) { //若除权日小于缴款截止日
                    return YssFun.left(zqdm, 2) + "****\t" + "QY PG"; //配股
                } else { //若除权日大于等于缴款截止日
                    if (seatType.equals("INDEX") && ((String)hmSubAssetType.get(portCode)).equals("0102")) {
                        return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK_ZS"; //配股缴款—指数股
                    } else if (seatType.equals("INDEX") && ((String)hmSubAssetType.get(portCode)).equals("0103")) {
                        return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK_ZB"; //配股缴款—指标股
                    } else {
                        return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK"; //配股缴款--普通
                    }
                }
            }
            else if(havePGInfo && !isPGJK){
                return YssFun.left(zqdm, 2) + "****\t" + "XG ZQ_PSZF"; //新股--配售增发中签
            }
            else{
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                if(hmShowZqdm.get(convertedZqdm) == null){
                	hmShowZqdm.put(convertedZqdm , convertedZqdm + " 配股");
                }
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                return "";
            }
        } catch (Exception e) {
            throw new YssException("根据参数判断配股缴款的业务子类型出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * 得到设置的回购收益的小数位数
     * @author fanghaoln 
     * @serialData 20100427
     * @see MS01079 QDV4招商基金2010年4月9日01_B 
     * @param portCode String
     * @return int
     * @throws YssException
     */
    public int getiiExchangeFhggain(String portCode) throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        String errmsg = "";
        int iExchangeFhggain=5;
        try {
                strSql = "select * from "+ pub.yssGetTableName("TB_DAO_ReadType") + " where FPortCode = " +
                dbl.sqlString(portCode) + " and FAssetGroupCode = " +
                    dbl.sqlString(pub.getAssetGroupCode());
                rs = dbl.openResultSet(strSql);//查出设置的小数位数
                while (rs.next()) {
                    //fanghaoln 20100427 MS01079 QDV4招商基金2010年4月9日01_B 
                	iExchangeFhggain= rs.getInt("FExchangeFhggain"); //回购收益保留位数
                    //---------------------------end ---MS01079---------------------------
                }
        } catch (Exception e) {
        	throw new YssException(errmsg, e);
        } finally {
        	 dbl.closeResultSetFinal(rs);
        }
    	return iExchangeFhggain;
    }
}
