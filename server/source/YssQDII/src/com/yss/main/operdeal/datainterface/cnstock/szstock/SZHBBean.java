package com.yss.main.operdeal.datainterface.cnstock.szstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.datainterface.cnstock.pojo.*;
import com.yss.util.*;

import java.sql.*;
import java.util.*;

import com.yss.main.operdeal.datainterface.cnstock.*;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;
import com.yss.main.operdeal.BaseOperDeal;
import java.math.BigDecimal;
import com.yss.main.operdata.BondInterestBean;

/**
 * 深圳回报库，主要储存交易数据
 * 用于处理深圳回报文件到系统的交易接口清算库中
 * created by songjie
 * 2009-06-04
 */
public class SZHBBean
    extends DataBase {
    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A start---//
    //HashMap hmTradeFee = null; //用于储存数据接口参数设置界面的交易费用计算方式分页的各种参数设置 key--组合群代码, 组合代码
	//HashMap hmReadType = null; //用于储存数据接口参数设置界面的读数处理方式分页的各种参数 key--组合群代码,组合代码
    //HashMap hmBrokerRate = null; //用于储存券商佣金利率 key--组合代码, 券商代码, 席位地点（上海或深圳）, 席位号, 品种类型
    //HashMap hmBrokerCode = null; //用于储存席位代码对应的券商代码
    //HashMap hmExchangeBond = null; //用于储存数据接口参数设置界面的交易所债券参数设置分页的各种参数 key--组合群代码, 组合代码, 市场, 品种
    //HashMap hmFeeWay = null; //用于储存数据接口参数设置界面的费用承担方向分页的各种参数设置 key--组合群代码, 组合代码, 券商代码, 席位代码
    //HashMap hmPortHolderSeat = null; //用于储存组合下对应的券商代码和席位代码
    //HashMap hmRateSpeciesType = null; //用于储存各种交易品种费率 key--费率类型, 费率品种
    //---delete by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A end---//

    HashMap hmSeatType = null; //用于储存席位代码对应的席位类型
    HashMap hmSubAssetType = null; //用于储存已选组合代码对应的资产子类型
    String checkstate = null; //审核状态
    java.util.Date date = null; //接口处理界面选择的时间
    String portCodes = null; //接口处理界面已选的组合代码
    //delete by songjie 2012.07.17 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
    //String assetGroupCode = null; //组合群代码

    ArrayList alZQInfo = new ArrayList(); //用于储存要存到债券利息表的数据
    ArrayList alZQCodes = new ArrayList(); //用于储存要存到债券利息表的债券代码

    HashMap hmSecsInfo = null;
    HashMap hmMTVInfo = null; //用于储存估值方法筛选条件数据对应的估值方法代码
    
	String cjmxSzZgfInfo = null; // 深圳证管费按成交明细计算的组合代码串
	//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
	String cjhzSzYjInfo = null;//深圳佣金按成交汇总计算的组合代码串

    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    ArrayList alShowHGZqdm = new ArrayList();
	
	//add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
	HashMap hmShowZqdm = new HashMap();
	
    public HashMap getHmShowZqdm() {
		return hmShowZqdm;
	}
    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
    
    //add by songjie 2010.03.22 国内：MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    ArrayList alShowZqdm = new ArrayList();
    
    public ArrayList getAlShowZqdm(){
    	return alShowZqdm;
    }
    //add by songjie 2010.03.22 国内：MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    public ArrayList getAlShowHGZqdm(){
    	return alShowHGZqdm;
    }
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
    
    /**
     * 构造函数
     */
    public SZHBBean() {
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

			// 获取深圳证管费按成交明细计算的组合代码串
			cjmxSzZgfInfo = (String) hmTradeFee.get("07cjmx");
			//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
			cjhzSzYjInfo = (String) hmTradeFee.get("10cjhz");
            
            //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
            hmFeeWay = (HashMap) hmParam.get("hmFeeWay");

            //获取交易费率品种设置界面设置的费率对应的HashMap
            hmRateSpeciesType = (HashMap) hmParam.get("hmRateSpeciesType");

            //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            hmBrokerRate = (HashMap) hmParam.get("hmBrokerRate");

            //获取所有已选组合代码对应的股东和席位代码对应的HashMap
            hmPortHolderSeat = (HashMap) hmParam.get("hmPortHolderSeat");

            //modify by nimengjing 2011.1.18 BUG #646 首次打开系统使用国内接口导入数据时，提示配置接口参数 
            //由于hmBrokerCode和fromTmpToSZHB()的位置引起在首次导入数据时 提示配置接口参数 
            //获取估值方法筛选条件对应的估值方法代码
            hmMTVInfo = (HashMap) hmParam.get("hmMTVInfo");

            //delete by songjie 2012.11.12 STORY #3214 需求深圳-[易方达基金]QDV4.0[紧急]20121030001
            //fromTmpToSZHB(); //将tmpSZ_hb表中的数据插入到SZHB表中
            
            //获取SZHB表中所有席位代码对应的券商代码对应的HashMap
            hmBrokerCode = super.getBrokerCode("subStr(HBHTXH, 0 , 6)", "SZHB", false, date);
            //-----------------------------------end bug646----------------------------------------------------
            fromSZHBToHzJkMx(); //从SZHB表到交易接口明细库

            insertIntoBondInterest(); //将债券利息数据中没有的债券利息数据插入到表中
        } catch (Exception e) {
            throw new YssException("处理深圳回报库数据到交易接口明细库出错", e);
        }
    }

    /**
     * 将债券利息数据中没有的债券利息数据插入到表中
     * @throws YssException
     */
    private void insertIntoBondInterest() throws YssException {
        String strSql = ""; //用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null; //声明PreparedStatement
        BondInterestBean bondInterest = null;
        Iterator iterator = null;
        String zqdms = "";
        String zqdm = null;
        try {
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

            pstmt = dbl.openPreparedStatement(strSql);

            iterator = alZQInfo.iterator();
            while (iterator.hasNext()) {
                bondInterest = (BondInterestBean) iterator.next(); //获取债券利息实例

                pstmt.setString(1, bondInterest.getSecurityCode()); //设置证券代码
                pstmt.setDate(2, YssFun.toSqlDate(date)); //设置业务日期
                pstmt.setDate(3, YssFun.toSqlDate("9998-12-31"));
                pstmt.setDate(4, YssFun.toSqlDate("9998-12-31"));
                pstmt.setBigDecimal(5, bondInterest.getIntAccPer100()); //设置税前百元利息
                pstmt.setInt(6, 0);
                pstmt.setBigDecimal(7, bondInterest.getSHIntAccPer100()); //设置税后百元利息
                //edit by songjie 2013.03.26 STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
                //数据来源改为 "ZD－自动计算"
                pstmt.setString(8, "ZD"); //表示是系统计算而得到的百元债券利息
                pstmt.setInt(9, 1);
                pstmt.setString(10, pub.getUserCode()); //创建人、修改人
                pstmt.setString(11, YssFun.formatDatetime(new java.util.Date())); //创建、修改时间

                pstmt.addBatch();
            }

            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        } catch (Exception e) {
            throw new YssException("将数据插入到债券利息表时出错！", e);
        } finally {
            dbl.closeStatementFinal(pstmt);
            dbl.endTransFinal(con, bTrans); //关闭连接
        }
    }

    /**
     * 将SZHB表的数据经过处理插入到交易接口明细库
     * @throws YssException
     */
    private void fromSZHBToHzJkMx() throws YssException {
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
        String zqdm = null; //声明证券代码
        String sqbh = null; //声明申请编号
        String bs = null; //声明买卖标志
        String gddm = null; //声明股东代码
        //add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB
        String cjhm = null;//声明成交号码

        String secInfo = null;
        String secType = null;
        String secTypes = null;
        String resultType = null;
        java.util.Date fDate = null; //储存交易日期

        String convertSecCode = null; //储存转换的证券代码格式
        String businessSign = null; //储存业务标志
        String securitySign = null; //储存证券标志

        String oldZqdm = null; //证券的原始代码
        String zqdmETF = null; //ETF基金代码
        String FTZBZ = null; //投资标志

        ReadTypeBean readType = null; //声明读数处理方式实体类
        FeeAttributeBean feeAttribute = null; //声明费用属性实体类
        String ywlb = null; //业务类别
		//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
        String jyfs = "";//交易方式
        String[] arrPortCodes = null;

        hmSeatType = new HashMap(); //用于储存席位代码对应的席位类型
        hmSubAssetType = new HashMap(); //用于储存已选组合代码对应的资产子类型
        
        boolean isGzlx = false;//判断交易接口明细表中的FGZLX字段取税前利息还是税后利息
        try {
            con.setAutoCommit(false); //开启事务
            bTrans = true;

            CtlStock ctlStock = new CtlStock();
            ctlStock.setYssPub(pub);

            arrPortCodes = portCodes.split(","); //拆分组合代码
            hmSeatType = getSeatType(); //获取席位代码对应的席位类型
            hmSubAssetType = judgeAssetType(portCodes,date); //获取已选组合代码对应的资产子类型

            for (int i = 0; i < arrPortCodes.length; i++) { //循环组合代码
                //获取组合代码对应的席位代码以及股东代码
                TSInfo = (String) hmPortHolderSeat.get(arrPortCodes[i]);

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
                strSql = " select * from SZHB where HBCJRQ = " + dbl.sqlDate(date);
                rs = dbl.openResultSet(strSql);
                if(!rs.next()){
                	throw new YssException("界面所选日期与接口文件日期不一致，请重新选择！");
                }
                
                dbl.closeResultSetFinal(rs);
                rs = null;
                //---add by songjie 2012.04.01 STORY #2437 QDV4华安基金2012年3月26日02_A end---//
                
                strSql = " select * from SZHB where HBGDDM in(" + operSql.sqlCodes(stockHolders) +
                    ") and subStr(HBHTXH, 0 , 6) in(" + operSql.sqlCodes(tradeSeats) +
                    ") and HBCJRQ = " + dbl.sqlDate(date);
                rs = dbl.openResultSet(strSql); //在SZHB表中查找相关股东和席位的数据

                while (rs.next()) {
                    cjjg = rs.getDouble("HBCJJG"); //成交价格
                    cjsl = rs.getInt("HBCJSL"); //成交数量
                    gddm = rs.getString("HBGDDM"); //股东代码
                    gsdm = YssFun.left(rs.getString("HBHTXH"), 6); //席位代码
                    zqdm = rs.getString("HBZQDM"); //证券代码
                    //add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB
                    cjhm = rs.getString("HBCJHM");//成交号码 
                    if(zqdm.startsWith("3")){
                    	boolean cyb = true;
                    }
                    sqbh = YssFun.right(rs.getString("HBHTXH"), 8); //申请编号
                    fDate = rs.getDate("HBCJRQ"); //成交日期
                    ywlb = rs.getString("HBYWLB"); //业务类别
					//add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
                    jyfs = rs.getString("JYFS");//交易方式：PT-普通交易、DZ-大宗交易
                    
                    //判断业务标志，证券标志以及转换后的证券代码
                    secInfo = judgeSecurityType(cjjg, cjsl, ywlb, gsdm, zqdm, sqbh, fDate, arrPortCodes[i]);

                    if(secInfo.equals("")){
                        continue;
                    }

                    if (secInfo.split("\t").length > 1) {
                        secTypes = secInfo.split("\t")[0]; //证券代码的格式 类似于1*****
                        secType = secTypes;
                        oldZqdm = zqdm; //获取转换前的证券代码

                        if (secType.indexOf(",") != -1) {
                            secTypes = secType.split(",")[0]; //转换前的证券代码的格式

                            //若为ETF基金申赎的股票，可根据同一申请编号获取到其ETF基金的代码，
                            //注意将股票数据的原始代码存为其ETF的基金代码。且其ETF基金现金替代部分的数据也需将
                            //根据同一申请编号获取到其ETF基金的代码，将其原始代码转为其ETF基金的代码。
                            zqdmETF = secType.split(",")[1]; //转换前的证券代码字段要设置的证券代码
                            //若为ETF基金申赎的股票，可根据同一申请编号获取到其ETF基金的代码，
                            //将股票数据的原始代码存为其ETF的基金代码
                            oldZqdm = zqdmETF;
                        }

                        resultType = secInfo.split("\t")[1]; //获取判断结果数据

                        //根据判断结果和转换前的证券代码的格式在convertRule.xml文件中
                        //查找相应的转换后的证券代码格式以及证券标志和业务标志

                        pubXMLRead.setSZHB(secTypes, resultType);
                    }

                    if (pubXMLRead.getSecSign() != null || pubXMLRead.getSecSign().trim().length() > 0) {
                        securitySign = pubXMLRead.getSecSign(); //证券标志
                    }
                    if (pubXMLRead.getBusinessSign() != null || pubXMLRead.getBusinessSign().trim().length() > 0) {
                        businessSign = pubXMLRead.getBusinessSign(); //业务标志
                    }
                    if (pubXMLRead.getConvertedSecCode() != null || pubXMLRead.getConvertedSecCode().trim().length() > 0) {
                        convertSecCode = pubXMLRead.getConvertedSecCode(); //获取转换后的证券代码格式
                    }

                    //----外部证券代码转换成内部证券代码----//
                    //将类似于51****的证券代码格式中的 * 去掉，只获取类似于51的字符串
                    convertSecCode = convertSecCode.substring(0, convertSecCode.indexOf("*"));
                    secType = secType.substring(0, secType.indexOf("*"));

                    //将外部代码（转换前的证券代码）转换为内部代码（转换后的证券代码）
                    if (!convertSecCode.equalsIgnoreCase(secType)) {
                        zqdm = convertSecCode + zqdm.substring(convertSecCode.length(), 6) + " CS";
                    }

                    if (zqdm.indexOf(" CS") == -1) {
                        zqdm = zqdm + " CS";
                    }

                    //----外部证券代码转换成内部证券代码----//

                    feeAttribute = new FeeAttributeBean(); //新建费用属性实例

                    feeAttribute.setSecuritySign(securitySign); //设置证券标志
                    feeAttribute.setBusinessSign(businessSign); //设置业务标志
                    feeAttribute.setZqdm(zqdm); //设置转换前的证券代码
                    feeAttribute.setPortCode(arrPortCodes[i]); //设置组合代码
                    feeAttribute.setGsdm(gsdm); //设置席位代码
                    feeAttribute.setDate(fDate); //设置交易日期
                    feeAttribute.setReadType(readType); //设置读数处理方式参数数据
                    feeAttribute.setCjsl(cjsl); //设置成交数量
                    feeAttribute.setCjjg(cjjg); //设置成交价格
                    //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//
                    feeAttribute.setCjhm(cjhm);//设置成交号码
                    feeAttribute.setOldZqdm(rs.getString("HBZQDM"));//设置转换前的证券代码
                    //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//

                    if (ywlb.indexOf("B") != -1) { //若业务类别中包含B
                        bs = "B"; //设置买卖标志为B
                    }
                    if (ywlb.indexOf("S") != -1) { //若业务类别中包含S
                        bs = "S"; //设置买卖标志为S
                    }

                    //业务标志为新债，证券标志为国债申购的业务的买卖标志为 买
                    if(securitySign.equals("XZ") && (businessSign.equals("SG_GZ") || 
                       businessSign.equals("SG_DFZFZ"))){
                        bs = "B"; //设置买卖标志为B
                    }

					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
                    //如果业务类别为 1S、1B 或 交易方式=DZ 则为大宗交易数据
                    if ((ywlb.equals("1S") || ywlb.equals("1B")) || jyfs.equals("DZ")) {
                        feeAttribute.setJyfs("DZ"); //表示大宗交易
                    } else {
                        feeAttribute.setJyfs("PT"); //表示普通交易
                    }

                    feeAttribute.setBs(bs); //设置买卖标志

                    //表示不是从交易接口明细库到交易接口清算库的处理中调用的计算费用的方法
                    feeAttribute.setComeFromQS(false);
                    
                    //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//
                    //若为回购、且不为回购到期、成交数量 = 0，则不处理，这种数据是用于获取回购利率的，不是实际的交易数据
                    if(feeAttribute.getSecuritySign().equalsIgnoreCase("HG") && 
                    !(feeAttribute.getBusinessSign().equalsIgnoreCase("MRHGDQ") 
                      || feeAttribute.getBusinessSign().equalsIgnoreCase("MCHGDQ")) && 
                      feeAttribute.getCjsl() == 0){
                    	continue;
                    }
                    //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//
                    
					//---add by songjie 2012.07.23 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A start---//
                    if(feeAttribute.getSecuritySign().equalsIgnoreCase("JJ") && feeAttribute.getCjjg() == 0){
                    	continue;
                    }
					//---add by songjie 2012.07.23 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A end---//
                    
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
                    
                    calculateFee(feeAttribute); //计算费用
                    
                    if(feeAttribute.getSecuritySign().equalsIgnoreCase("ZQ")){
                    	isGzlx = super.judgeGzlx(feeAttribute);
                    }
                    
                    strSql = " insert into " + pub.yssGetTableName("Tb_HzJkMx") + "(FDate, FZqdm, FSzsh, FGddm, FJyxwh, FBs, " +
                        " FCjsl, FCjjg, FCjje, FYhs, FJsf, FGhf, FZgf, FYj, FGzlx, Fhggain, FZqbz, Fywbz, FSqbh, " +
                        "Fqtf, Zqdm, Ffxj, Findate, FTZBZ, FPortCode, FJYFS, FSqGzlx, FCreator, FCreateTime,FJKDM)" +//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                        "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//edit by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                    pstmt = dbl.openPreparedStatement(strSql); //将SZHB表中处理后的数据储存到交易接口明细表中

                    pstmt.setDate(1, YssFun.toSqlDate(fDate)); //交易日期
                    pstmt.setString(2, zqdm); //转换后的证券代码
                    pstmt.setString(3, "CS"); //交易所代码
                    pstmt.setString(4, gddm); //股东代码
                    pstmt.setString(5, gsdm); //席位代码
                    if(bs == null || bs.equals("")){
                    	bs = " ";
                    }
                    pstmt.setString(6, bs); //买卖标志
                    pstmt.setDouble(7, cjsl); //成交数量
                    pstmt.setDouble(8, cjjg); //成交价格
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
                    pstmt.setDate(23, YssFun.toSqlDate(date)); //系统读数日期
                    pstmt.setString(24, FTZBZ); //投资标志
                    pstmt.setString(25, arrPortCodes[i]); //组合代码
                    pstmt.setString(26, feeAttribute.getJyfs()); //交易方式
                    pstmt.setDouble(27, feeAttribute.getFBeforeGzlx()); //税前的国债利息
                    pstmt.setString(28, pub.getUserCode()); //用户代码
                    pstmt.setString(29, YssFun.formatDatetime(new java.util.Date())); //创建日期
                    pstmt.setString(30, "SZHB");//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                    pstmt.execute();

                    dbl.closeStatementFinal(pstmt); //关闭pstmt
                }
                dbl.closeResultSetFinal(rs); //关闭结果集
            }

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        } catch (Exception e) {
            throw new YssException("将SZHB表的数据经过处理插入到交易接口明细库的处理出错！", e);
        } finally {
            dbl.endTransFinal(con, bTrans); //关闭连接
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
                businessSign.equals("MC") || businessSign.equals("MR") ||
                businessSign.equals("XJTD_SG") || businessSign.equals("XJTD_SH") || businessSign.equals("YYSG")) { //可转债股票, ETF基金股票, 普通股票
                subCategoryCode = "EQ01"; //普通股
            }

            if (businessSign.equals("MR_ZS") || businessSign.equals("MC_ZS") || businessSign.equals("YYSG_ZS")) { //指数股票买入 或 卖出 或 指数要约收购
                subCategoryCode = "EQ03"; //指数股票
            }

            if (businessSign.equals("MR_ZB") || businessSign.equals("MC_ZB") || businessSign.equals("YYSG_ZB")) { //指标股票买入 或 卖出 或 指标要约收购
                subCategoryCode = "EQ04"; //指标股票
            }
        }

        if (securitySign.equals("ZQ")) { //债券
            categoryCode = "FI";

            //可转债买入 或 卖出 或 可转债回售
            if (businessSign.equals("MR_KZZ") || businessSign.equals("MC_KZZ") || businessSign.equals("KZZHS") || businessSign.equals("KZZGP")) {
                subCategoryCode = "FI06"; //可转债
            }

            if (businessSign.equals("MR_GZ") || businessSign.equals("MC_GZ")) { //国债买入或卖出
                subCategoryCode = "FI12"; //国债
            }
            
            if (businessSign.equals("MR_DFZFZ") || businessSign.equals("MC_DFZFZ")) { //国债买入或卖出
                subCategoryCode = "FI16"; //地方政府债
            }

            if (businessSign.equals("MR_FLKZZ") || businessSign.equals("MC_FLKZZ")) { //分离可转债买入或卖出
                subCategoryCode = "FI07"; //分离可转债
            }

            if (businessSign.equals("MR_QYZQ") || businessSign.equals("MC_QYZQ")) { //企业债的买入或卖出
                subCategoryCode = "FI09"; //企业债
            }

            if (businessSign.equals("MR_QYZQ_GS") || businessSign.equals("MC_QYZQ_GS")) { //公司债的买入或卖出
                subCategoryCode = "FI08"; //公司债
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

            //封闭式基金的买入或买入以及封闭式基金认购
            if (businessSign.equals("MR_FBS") || businessSign.equals("MC_FBS") || businessSign.equals("RG_FBS")) {
                subCategoryCode = "TR01"; //封闭式基金
            }

            //ETF基金的申购或赎回，ETF基金申购或赎回的现金替代，ETF基金买入或卖出 或 ETF申赎现金差额 或 ETF现金退款 或 ETF基金认购
            if (businessSign.equals("SG_ETF") || businessSign.equals("SH_ETF") ||
                businessSign.equals("XJTD_SG") || businessSign.equals("XJTD_SH") ||
                businessSign.equals("MR_ETF") || businessSign.equals("MC_ETF") ||
                businessSign.equals("XJCE") || businessSign.equals("ETFTK") ||
                businessSign.equals("RG_ETF")) {
                subCategoryCode = "TR04"; //ETF基金
            }

            if (businessSign.equals("MR_LOF") || businessSign.equals("MC_LOF") || businessSign.equals("RG_LOF")) {
                subCategoryCode = "TR02"; //LOF基金 普通开放式基金 LOF基金认购
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

            if (businessSign.equals("MRHG") || businessSign.equals("MRHGDQ")) {
                subCategoryCode = "RE08"; //融券回购
            }

            if (businessSign.equals("MCHG") || businessSign.equals("MCHGDQ")) {
                subCategoryCode = "RE07"; //融资回购
            }
        }

        if (securitySign.equals("QY")) { //权益
            categoryCode = "EQ";
            subCategoryCode = "EQ06"; //配股
        }

        if (securitySign.equals("XG")) {
            categoryCode = "EQ";
            //---edit by songjie 2012.02.23 BUG 3898 QDV4赢时胜(上海开发部)2012年2月17日01_B start---//
            subCategoryCode = "EQ02"; //新股
            //subCategoryCode = "EQ01"; //新股子类型也为普通股，新股与普通股在属性分类中区分
            //---edit by songjie 2012.02.23 BUG 3898 QDV4赢时胜(上海开发部)2012年2月17日01_B end---//
        }

        if (securitySign.equals("XZ")) {
            categoryCode = "FI";
			//---edit by songjie 2012.02.16 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A start---//
            if(businessSign.equals("KZZXZ") || businessSign.equals("ZQ_KZZ") || 
               //edit by songjie 2012.10.08 导入新债、可转债申购 数据时 自动生成的证券信息设置数据没有品种子类型
               businessSign.equals("FK_KZZ") || businessSign.equals("SG_KZZ")){
            	subCategoryCode = "FI11"; //未上市可转债
            }
            if(businessSign.equals("SG_GZ")){
            	subCategoryCode = "FI13"; //未上市国债
            }
            if(businessSign.equals("ZQ_FLKZZ")){
            	subCategoryCode = "FI15"; //未上市可分离债
            }
            if(businessSign.equals("ZQ_QYZQ")){
            	subCategoryCode = "FI14"; //未上市企业债
            }
            if(businessSign.equals("SG_DFZFZ")){
            	subCategoryCode = "FI17"; //未上市地方政府债
            }
			//---edit by songjie 2012.02.16 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A end---//
        }
        if (securitySign.equals("B_GP")) { //B股 panjunfang add 20100422
            categoryCode = "EQ";//品种类型 = 股票
            subCategoryCode = "EQ01";//品种子类型= 普通股
        }
        return categoryCode + "\t" + subCategoryCode;
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
     * 指数股票的判断标准改为：1.看基金资产是否为指数型基金资产（基金资产的判断仍在商议） 2.看数据中的席位代码对应的是否是指数席位 若两个条件都满足，则表示为指数型股票
     * 指标股票的判断标准改为：1.看基金资产是否为指标型基金资产（基金资产的判断仍在商议） 2.看数据中的席位代码对应的是否是指数席位 若两个条件都满足，则表示为指标型股票
     * 若以上条件都不满足，则表示为普通股票
     */
    public String judgeSecurityType(double cjjg, double cjsl, String ywlb, String gsdm, String zqdm, String sqbh,
                                    java.util.Date fDate, String portCode) throws YssException {
        boolean haveETFSec = false;
        String seatType = ""; //席位类型
        String zqdmETF = ""; //ETF基金代码
        HashMap hmETFSec = null;
        try {
            seatType = (String) hmSeatType.get(gsdm); //席位类型

            //edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
            if (YssFun.left(zqdm, 2).equals("00") || YssFun.left(zqdm, 2).equals("30")) {
                if (ywlb.equals("KB") || ywlb.equals("KS") || ywlb.equals("ZB") || ywlb.equals("ZS")) {
                    hmETFSec = getETFSec(sqbh);
                    if ( ( (String) hmETFSec.get("haveETFSec")).equals("true")) {
                        haveETFSec = true;
                    }
                    zqdmETF = (String) hmETFSec.get("zqdmETF");
                }

                if (ywlb.equals("7B")) { //新股  新股申购
                	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                    return YssFun.left(zqdm, 2) + "****\tXG SG";
                } else if (ywlb.equals("KB")) {
                    if (haveETFSec) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****," + zqdmETF + "\tGP SH_ETF"; //股票  ETF基金赎回
                    }
                } else if (ywlb.equals("KS")) {
                    if (haveETFSec) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****," + zqdmETF + "\tGP SG_ETF"; //股票  ETF基金申购
                    }

                } else if (ywlb.equals("ZB")) {
                    if (haveETFSec) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****," + zqdmETF + "\tGP XJTD_SG"; //股票  ETF基金申购现金给付
                    }

                } else if (ywlb.equals("ZS")) {
                    if (haveETFSec) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****," + zqdmETF + "\tGP XJTD_SH"; //股票  ETF基金赎回现金赎回
                    }
                } else {
                    if (seatType.equals("INDEX") && ( (String) hmSubAssetType.get(portCode)).equals("0102")) {
                        //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
						if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MR_ZS"; //股票  指数股票买入
                        }
						//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                        if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MC_ZS"; //股票  指数股票卖出
                        }
                    } else if (seatType.equals("INDEX") && ( (String) hmSubAssetType.get(portCode)).equals("0103")) {
                        //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
						if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MR_ZB"; //股票  指标股票买入
                        }
                        //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                        if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MC_ZB"; //股票  指标股票卖出
                        }
                    } else {
					    //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                        if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MR"; //股票  普通股票买入
                        }
						//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                        if (ywlb.equals("0S") || ywlb.equals("1S")) {	
                        	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                            return YssFun.left(zqdm, 2) + "****\tGP MC"; //股票  普通股票卖出
                        }
                    }
                }
            }
            //edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
            if (YssFun.left(zqdm, 2).equals("07") || YssFun.left(zqdm, 2).equals("37")) {
                if (ywlb.equals("7B")) {
                    if (cjjg == 100) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****\tXZ SG_KZZ"; //新债  可转债申购
                    } else if (cjsl != 0 && judgeCommitInfo(zqdm, sqbh, fDate)) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****\tXG ZQ_PSZF"; //新股  配售增发中签
                    } else {
                    	//edit by songjie 2012.01.09 STORY #2104 QDV4赢时胜(上海开发部)2012年01月03日01_A
                        return YssFun.left(zqdm, 2) + "****\tXG SG"; //新股  新股申购
                    }
                }
            }
            if (YssFun.left(zqdm, 2).equals("08") || YssFun.left(zqdm, 2).equals("38")) {
                if (ywlb.equals("4B")) {
                    if (cjjg == 100) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        return YssFun.left(zqdm, 2) + "****\tXZ KZZXZ"; //新债--可转债中签
                    }
                    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                    if(judgePGJK(fDate, zqdm, seatType, portCode).equals("")){
                        if (YssFun.left(zqdm, 2).equals("08")) {
                        	if(hmShowZqdm.get("00" + YssFun.right(zqdm, 4) + " CS") == null){
                        		hmShowZqdm.put("00" + YssFun.right(zqdm, 4) + " CS", 
                        			  "00" + YssFun.right(zqdm, 4) + " CS " + "配股");
                        	}	
                        }
                      //edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        else if(YssFun.left(zqdm, 2).equals("38")){
                        	if(hmShowZqdm.get("30" + YssFun.right(zqdm, 4) + " CS") == null){
                        		hmShowZqdm.put("30" + YssFun.right(zqdm, 4) + " CS", 
                        			  "30" + YssFun.right(zqdm, 4) + " CS " + "配股");
                        	}
                        }
                      //edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                        else{
                        	if(hmShowZqdm.get(zqdm + " CS") == null){
                        		hmShowZqdm.put(zqdm + " CS", 
                          			  zqdm + " CS " + "配股");
                        	}
                        }
                    }
                    //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                    return judgePGJK(fDate, zqdm, seatType, portCode); //判断是否为配股缴款的类型
                }
            }

            if (YssFun.left(zqdm, 4).equals("1016") || YssFun.left(zqdm, 4).equals("1017")) {
            	//edit by songjie 2012.02.18
                return YssFun.left(zqdm, 4) + "**\tXZ SG_DFZFZ"; //新债--地方政府债申购
            }
            
            if (YssFun.left(zqdm, 3).equals("109")) {
            	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                	//edit by songjie 2012.02.18
                    return "109***\tZQ MR_DFZFZ"; //债券--地方政府债买入
                }
                
                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                	//edit by songjie 2012.02.18
                    return "109***\tZQ MC_DFZFZ"; //债券--地方政府债卖出
                }

            }
            
            if (YssFun.left(zqdm, 2).equals("10")) {
            	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return "10****\tZQ MR_GZ"; //债券--国债买入
                }
                
                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return "10****\tZQ MC_GZ"; //债券--国债卖出
                }
            }
            if (YssFun.left(zqdm, 2).equals("11")) {
                if (YssFun.left(zqdm, 3).equals("115")) {
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "115***\tZQ MR_FLKZZ"; //债券--分离式可转债买入
                    }
                	
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "115***\tZQ MC_FLKZZ"; //债券--分离式可转债卖出
                    }
                } else if (YssFun.left(zqdm, 3).equals("112")) {
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "112***\tZQ MR_QYZQ_GS"; //债券--公司债买入
                    }
                	
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "112***\tZQ MC_QYZQ_GS"; //债券--公司债卖出
                    }
                } else {
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "11****\tZQ MR_QYZQ"; //债券--企业债券买卖
                    }
                	
                	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                	if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "11****\tZQ MC_QYZQ"; //债券--企业债券买卖
                    }
                }
            }

            if (YssFun.left(zqdm, 2).equals("12")) {
			    //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
            	if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return "12****\tZQ MR_KZZ"; //债券--可转债买入
                }
                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
            	if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return "12****\tZQ MC_KZZ"; //债券--可转债卖出
                }
            }
            
            if (YssFun.left(zqdm, 2).equals("13")) {
			    //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return "13****\tHG MRHG"; //回购--融券回购
                }

                //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return "13****\tHG MCHG"; //回购--融资回购
                }

                if (ywlb.equals("9B")) {
                    return "13****\tHG MRHGDQ"; //回购--融券回购到期
                }

                if (ywlb.equals("9S")) {
                    return "13****\tHG MCHGDQ"; //回购--融资回购到期
                }
            }

            if (YssFun.left(zqdm, 2).equals("16")) {
			    //edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return "16****\tJJ MR_LOF"; //基金--LOF基金买入
                }
				//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return "16****\tJJ MC_LOF"; //基金--LOF基金卖出
                }
            }

            if (YssFun.left(zqdm, 2).equals("15")) {
                if (YssFun.left(zqdm, 4).equals("1599")) {
                    if (ywlb.equals("KB")) {
                        return "1599**\tJJ SG_ETF"; //基金--ETF申购
                    }

                    if (ywlb.equals("KS")) {
                        return "1599**\tJJ SH_ETF"; //基金--ETF赎回
                    }
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "1599**\tJJ MR_ETF"; //基金--ETF基金买入
                    }
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "1599**\tJJ MC_ETF"; //基金--ETF基金卖出
                    }
                } else {
				  	//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "15****\tJJ MR_LOF"; //基金--LOF基金买入
                    }
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "15****\tJJ MC_LOF"; //基金--LOF基金卖出
                    }
                }
            }
            if (YssFun.left(zqdm, 2).equals("18")) {
				//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return "18****\tJJ MR_FBS"; //基金--封闭式基金买入
                }
				//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return "18****\tJJ MC_FBS"; //基金--封闭式基金卖出
                }

                if (ywlb.equals("7B")) {
                    return "18****\tJJ RG_FBS"; //基金--封闭式基金认购
                }
            }
            if (YssFun.left(zqdm, 2).equals("03")) {
                int zqdmToInt = Integer.parseInt(YssFun.mid(zqdm, 1, 2));

                if (zqdmToInt <= 32 && zqdmToInt >= 30) {
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "03****\tQZ MR_RGQZ"; //权证--买入认购权证
                    }
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "03****\tQZ MC_RGQZ"; //权证--卖出认购权证
                    }
                }
                if (zqdmToInt <= 39 && zqdmToInt >= 38) {
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0B") || ywlb.equals("1B")) {
                        return "03****\tQZ MR_RZQZ"; //权证--买入认沽权证
                    }
					//edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 添加大宗交易业务类别 1B、1S
                    if (ywlb.equals("0S") || ywlb.equals("1S")) {
                        return "03****\tQZ MC_RZQZ"; //权证--卖出认沽权证
                    }
                }
            }
            if (YssFun.left(zqdm, 2).equals("20")) {//B股业务  panjunfang add 20100122
                if (ywlb.equals("0B") || ywlb.equals("1B")) {
                    return YssFun.left(zqdm, 2) + "****\tB_GP MR"; //股票  B股买入
                }
                if (ywlb.equals("0S") || ywlb.equals("1S")) {
                    return YssFun.left(zqdm, 2) + "****\tB_GP MC"; //股票  B股卖出
                }
            }
            return "";
        } catch (Exception e) {
            throw new YssException("判断证券数据的内部证券代码，证券类型，业务类型出错", e);
        }
    }

    /**
     * 根据证券代码，申请编号，交易日期，证券标志为新股，业务标志为可配售许可数据来查找交易接口明细库中是否包含上一工作日的老股东优先认购许可数据
     * @param zqdm String
     * @param sqbh String
     * @param workDay Date
     * @return boolean
     * @throws YssException
     */
    private boolean judgeCommitInfo(String zqdm, String sqbh, java.util.Date workDay) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean haveInfo = false;
        try {
            BaseOperDeal baseOperDeal = new BaseOperDeal(); //新建BaseOperDeal
            baseOperDeal.setYssPub(pub);
            ReadTypeBean readType = (ReadTypeBean)hmReadType.get(pub.getAssetGroupCode() + " " + this.sPort);
            //edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
            java.util.Date date = baseOperDeal.getWorkDay(readType.getHolidaysCode(), workDay, -1); //得到上一个工作日的日期

            //在交易接口明细库中查找上一个工作日的证券标志位新股，业务标志位可配售许可数据的数据
            strSql = " select * from " + pub.yssGetTableName("Tb_HzJkMx") + " where Zqdm = " +
                dbl.sqlString(zqdm) + " and FSqBh = " + dbl.sqlString(sqbh) + " and FDate = " +
                dbl.sqlDate(date) + " and Fzqbz = 'XG' and FYwbz = 'KPSL' ";
            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                haveInfo = true;
                break;
            }

            return haveInfo;
        } catch (Exception e) {
            throw new YssException("查找交易接口明细库中是否包含上一工作日的老股东优先认购许可数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 用于判断基金资产是否为指数型基金资产或指标型基金资产
     * 判断标准尚未指定，仍在设计中。
     * 返回值应该包括：1.INDEX(表示指数型基金资产) 2.ZB(表示指标型基金资产) 3.OTHER(表示非指标型或指数型基金资产)
     * @return String
     */
    private HashMap judgeAssetType(String portCode,java.util.Date tradeDate) throws YssException {
        String strSql = ""; //储存sql语句
        ResultSet rs = null;
        String subAssetType = null;
        String portCodee = null;
        try {
        	// edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
            //在组合设置表中查询当前组合的资产子类型数据

        	  strSql = " select FAssetType, FSubAssetType, FPortCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
              " A where FPortCode in (" + operSql.sqlCodes(portCode) + ") ";

        	
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
        //add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
        double originalFYj = 0;//未减去各种费用的佣金
        double Ffxj = 0; //初始化风险金
        double Fqtf = 0; //初始化结算费
        double Fhggain = 0; //初始化回购收益

        if (feeAttribute.isComeFromQS()) {
            Fhggain = feeAttribute.getFhggain();
        }

        double FBeforeGzlx = 0; //初始化税前债券利息
        double FGzlx = 0; //初始化税后债券利息
        double startMoney = 0; //起点金额
        double FJsfZgfRate = 0; //经手费和证管费总的费率

        HashMap hmZQRate = null; //用于储存债券的每百元债券利息，税后每百元债券利息，税前每百元债券利息
        HashMap hmPerZQRate = null; //用于储存债券利息表查询出的数据
        BondInterestBean bondInterest = null;
        String haveInfo = null;
        
        //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//
        String cjhm = feeAttribute.getCjhm();//成交号码
        String oldZqdm = feeAttribute.getOldZqdm();//转换前的证券代码
        //---add by songjie 2011.03.07 需求：750 QDV4赢时胜(上海)2011年3月6日01_AB---//
        
        String securitySign = feeAttribute.getSecuritySign(); //获取证券标志
        String businessSign = feeAttribute.getBusinessSign(); //获取业务标志
        String zqdm = feeAttribute.getZqdm(); //获取证券代码
        String portCode = feeAttribute.getPortCode(); //获取组合代码
        String gsdm = feeAttribute.getGsdm(); //获取席位代码
        //买卖标志 add by songjie 2009.12.21 MS00847 
        //QDV4赢时胜（北京）2009年11月30日03_B
        String bs = feeAttribute.getBs();
        java.util.Date date = feeAttribute.getDate(); //获取交易日期
        String tradeTypeCode = feeAttribute.getJyfs();//获取交易方式（大宗/普通）

        double cjje = 0; //初始化成交金额
        double cjjg = feeAttribute.getCjjg(); //初始化成交价格
        double cjsl = feeAttribute.getCjsl(); //初始化成交数量
        double jsCjje = 0;//结算金额 add by songjie 2012.04.19 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
        
        CtlStock ctlStock = new CtlStock();
        ctlStock.setYssPub(pub);

        //获取数据接口参数设置的读数处理方式界面设置的参数
        ReadTypeBean readType = feeAttribute.getReadType();

        //判断是否是明细库到清算库的处理过程中调用本方法
        boolean comeFromQs = feeAttribute.isComeFromQS();

        boolean yjParamNum = false; //判断经手费的计算公式中是否包含成交数量参数
        boolean fxjParamNum = false; //判断证管费的计算公式中是否包含成交数量参数

        if (hmBrokerCode == null) {
            //获取储存席位代码对应的券商代码的HashMap
            hmBrokerCode = feeAttribute.getHmBrokerCode();
        }
        if (hmFeeWay == null) {
            //获取储存数据接口参数设置的费用承担方向分页设置的HashMap
            hmFeeWay = feeAttribute.getHmFeeWay();
        }
        if (hmRateSpeciesType == null) {
            //获取储存交易费率品种设置界面设置的参数的HashMap
            hmRateSpeciesType = feeAttribute.getHmRateSpeciesType();
        }
        if (hmBrokerRate == null) {
            //获取储存券商佣金利率设置界面设置的参数的HashMap
            hmBrokerRate = feeAttribute.getHmBrokerRate();
        }
        if (hmExchangeBond == null) {
            //获取储存数据接口参数设置的交易所债券参数设置分页设置的参数的HashMap
            hmExchangeBond = feeAttribute.getHmExchangeBond();
        }
        
		//--- add by songjie 2012.11.13 BUG 6227 QDV4农业银行2012年11月06日01_B start---//
        if(hmTradeFee == null){
        	hmTradeFee = (HashMap) feeAttribute.getHmTradeFee();
        	this.cjhzSzYjInfo = (String)hmTradeFee.get("10cjhz");
        }
		//--- add by songjie 2012.11.13 BUG 6227 QDV4农业银行2012年11月06日01_B end---//

        String brokerCode = (String) hmBrokerCode.get(gsdm); //根据席位获取券商代码

        FeeWayBean feeWay = (FeeWayBean) hmFeeWay.get(pub.getAssetGroupCode() + " " + portCode +
            " " + brokerCode + " " + gsdm); //获取交易接口参数设置的费用承担方向分页的相关数据

        BrokerRateBean brokerRate = null;
        ExchangeBondBean exchangeBond = null;

        if (feeWay == null) {
            throw new YssException("请在交易接口参数设置界面设置已选组合的费用承担参数！");
        }
        String brokerBear = feeWay.getBrokerBear(); //获取由券商承担的费用数据

        ArrayList alBears = splitBrokerBear(brokerBear); //拆分费用数据
        try {
            //---股票
            if (securitySign.equalsIgnoreCase("GP")) {

                //成交金额：roundit (成交价格 * 成交数量, 2)
                cjje = YssFun.roundIt(YssD.mul(cjjg, cjsl), 2);

                if (! (businessSign.equalsIgnoreCase("XJTD_SG") || businessSign.equalsIgnoreCase("XJTD_SH"))) {
                    //ETF申购股票，ETF赎回股票，可转债股票的股票
                    if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                        businessSign.equalsIgnoreCase("KZZGP")) {

                        //ETF申购股票，ETF赎回股票,ETF申购现金替代的股票,ETF赎回现金替代的股票
                        if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF")) {
                            if (hmRateSpeciesType.get("1 SZ GP ETF GHF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳ETF股票的过户费费率数据！");
                            }
                            
                            	//=======  需求406. QDV4南方东英2010年12月14日01_A  增加股票面值字段用于计算过户费=======
                                //====修改过户费计算方式 ： 过户费= roundit(成交数量× 股票面额 × 深圳ETF股票过户费率,2)
                            /*
                             * 
                             * //过户费= roundit(成交数量× 深圳ETF股票过户费率,2)
                            		FGhf = YssFun.roundIt(YssD.mul(cjsl, ( (RateSpeciesTypeBean)
                                                                  hmRateSpeciesType.get("1 SZ GP ETF GHF")).getExchangeRate(), 0.01), 2);
                             * */
                            PublicMethodBean pmBean = new PublicMethodBean();
                            pmBean.setYssPub(pub);
                            
                            final  double dFaceAmount =pmBean.getFaceAmount(zqdm);
                            if (dFaceAmount == 0 || dFaceAmount == -1) {
							    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                	FGhf = YssFun.roundIt(YssD.mul(cjsl,1, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP ETF GHF")).getBigExchange(), 0.01), 2);
                            	}else{
                                	FGhf = YssFun.roundIt(YssD.mul(cjsl,1, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP ETF GHF")).getExchangeRate(), 0.01), 2);
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                            }else {
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                	FGhf = YssFun.roundIt(YssD.mul(cjsl,dFaceAmount, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP ETF GHF")).getBigExchange(), 0.01), 2);
                            	}else{
                                	FGhf = YssFun.roundIt(YssD.mul(cjsl,dFaceAmount, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP ETF GHF")).getExchangeRate(), 0.01), 2);
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            }
                            
                            //====END 需求406. QDV4南方东英2010年12月14日01_A lidaolong 20110112====================
                            
                            //深圳股票过户费起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ETF GHF")).getStartMoney();

                            //如果深圳股票过户费小于深圳股票过户费的起点金额，那么深圳股票的过户费就等于深圳股票过户费的起点金额
                            if (cjsl != 0 && startMoney != 0 && FGhf < startMoney) {
                                FGhf = startMoney;
                            }
                        }
                    } else {
                            if (hmRateSpeciesType.get("1 SZ GP JSF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳股票的经手费费率数据！");
                            }

                            if (hmRateSpeciesType.get("1 SZ GP ZGF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳股票的证管费费率数据！");
                            }
                            

                            //20130323 added by liubo.Story #3750
                            //深圳股票过户费
                            //============================
                            if (hmRateSpeciesType.get("1 SZ GP GHF") != null) {
                            	
                                if(tradeTypeCode.equals("DZ")){//大宗交易
                                	FGhf = YssFun.roundIt(YssD.mul(cjje, YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP GHF"))
                                            .getBigExchange(), 0.01)), 2);
                            	}else{
                            		FGhf = YssFun.roundIt(YssD.mul(cjje, YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP GHF"))
                                            .getExchangeRate(), 0.01)), 2);
                            	}
                            	
                            }
                            //=============end===============

                            //获取深圳股票的经手费率和证管费率之和
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                            	//--- edit by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B start---//
                                FJsfZgfRate = /*YssD.add*/(YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF"))
                                        .getBigExchange(), 0.01)/*,
                                        YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF"))
                                                 .getBigExchange(), 0.01)*/);
                                //--- edit by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B end---//
                            }else{
                            	//--- edit by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B start---//
                                FJsfZgfRate = /*YssD.add*/(YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF"))
                                        .getExchangeRate(), 0.01)/*,
                                        YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF"))
                                                 .getExchangeRate(), 0.01)*/);
                                //--- edit by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B end---//
                            }
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                            //经手费 = roundit（成交金额×（深圳股票经手费率+深圳股票证管费率）,2）
                            FJsf = YssFun.roundIt(YssD.mul(cjje, FJsfZgfRate), 2);

                            //深圳股票经手费和证管费起点金额
                            //edit by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getStartMoney()/* +
                                ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getStartMoney()*/;

                            //如果深圳股票经手费和证管费小于深圳股票经手费和证管费的起点金额，那么深圳股票的经手费和证管费就等于深圳股票经手费和证管费的起点金额
                            if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                                FJsf = startMoney;
                            }

                            //delete by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B
                            //if (comeFromQs || cjmxSzZgfInfo != null) {
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                    FZgf = YssFun.roundIt(YssD.mul(cjje, YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF"))
                                            .getBigExchange(), 0.01)), 2);
                            	}else{
                                    FZgf = YssFun.roundIt(YssD.mul(cjje, YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF"))
                                            .getExchangeRate(), 0.01)), 2);
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
				
                                //深圳股票经手费和证管费起点金额
                                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getStartMoney();

                                //如果深圳股票经手费和证管费小于深圳股票经手费和证管费的起点金额，那么深圳股票的经手费和证管费就等于深圳股票经手费和证管费的起点金额
                                if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                                    FZgf = startMoney;
                                }
                            //}//delete by songjie 2013.05.07 BUG 7936 QDV4嘉实2013年04月26日01_B

                        if (feeAttribute.getBs().equals("B")) {
                            if (hmRateSpeciesType.get("1 SZ GP B YHF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳股票的买印花税费率数据！");
                            }

                            //买入印花税= roundit(成交价格×成交数量×深圳股票买印花税费率,2)
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP B YHF")).getBigExchange(), 0.01), 2);
                            }else{
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP B YHF")).getExchangeRate(), 0.01), 2);
                            }
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                            //深圳股票买印花税起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP B YHF")).getStartMoney();

                            //如果深圳股票买印花税小于深圳股票买印花税的起点金额，那么深圳股票的买印花税就等于深圳股票买印花税的起点金额
                            if (cjjg != 0 && cjsl != 0 && startMoney != 0 && FYhs < startMoney) {
                                FYhs = startMoney;
                            }
                        }
                        if (feeAttribute.getBs().equals("S")) {
                            if (hmRateSpeciesType.get("1 SZ GP S YHF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳股票的卖印花税费率数据！");
                            }

                            //卖出印花税= roundit（成交价格×成交数量×深圳股票卖印花税费率，2）
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP S YHF")).getBigExchange(), 0.01), 2);
                            }else{
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP S YHF")).getExchangeRate(), 0.01), 2);
                            }
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                            //深圳股票卖印花税起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP S YHF")).getStartMoney();

                            //如果深圳股票卖印花税小于深圳股票卖印花税的起点金额，那么深圳股票的卖印花税就等于深圳股票卖印花税的起点金额
                            if (cjjg != 0 && cjsl != 0 && startMoney != 0 && FYhs < startMoney) {
                                FYhs = startMoney;
                            }
                        }
                    }
                    //story 2092 by zhouwei 20120106 深圳股票风险金 不计算
                    //----深圳股票风险金----//
//                    if (hmRateSpeciesType.get("1 SZ GP FXJ") == null) {
//                        throw new YssException("请在交易费率品种设置中设置深圳股票的风险金费率数据！");
//                    }
//
//                    //风险金=RoundIt(成交金额×深圳股票风险金利率), 2)
//                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
//                        hmRateSpeciesType.get("1 SZ GP FXJ")).getExchangeRate(), 0.01), 2);
//
//                    //深圳股票风险金起点金额
//                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP FXJ")).getStartMoney();
//
//                    //如果风险金小于深圳股票最低风险金金额，则重新调整风险金（风险金=深圳股票最低风险金金额）
//                    if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
//                        Ffxj = startMoney;
//                    }
                    //----深圳股票风险金----//

                    //----深圳股票佣金----//
                    brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                        brokerCode + " 1 " + gsdm + " EQ"); //获取深圳股票对应组合代码和券商代码的佣金利率设置实例

                    if (brokerRate == null) {
                        throw new YssException("请在券商佣金利率设置中设置深圳股票的佣金费率数据！");
                    }

                    //佣金=roundit(成交金额×佣金利率,2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                    	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                    }else{
                    	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    
                    originalFYj = FYj;//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
                    
                    //若股票的经手费 或 证券费 或 风险金 或 过户费 或 印花税 由券商承担
                    if (alBears.contains("01") || alBears.contains("05") || alBears.contains("15") ||
                        alBears.contains("14") || alBears.contains("10")) {
                        //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                    		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                    	}else{
                    		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    }
                    
					if (alBears.contains("01") && alBears.contains("05")) {// 若经手费，证管费都由券商承担
						// 佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
						// roundit(成交金额×(深圳经手费率 + 深圳证管费率),计算深圳佣金过程中费用小数点保留位数),2)
						if (!(businessSign.equalsIgnoreCase("SH_ETF")
								|| businessSign.equalsIgnoreCase("SG_ETF") || businessSign
								.equalsIgnoreCase("KZZGP"))) {
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
							if(tradeTypeCode.equals("DZ")){//大宗交易
								//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
								FYj = YssD.sub(FYj, 
										YssFun.roundIt(
												       YssD.mul(cjje,
										                        ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getBigExchange(), 
										                        0.01)
										               ,brokerRate.getYjCoursePreci()),
										YssFun.roundIt(
										               YssD.mul(cjje,
										        		        ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getBigExchange(),
										        		        0.01)
										               ,brokerRate.getYjCoursePreci())
										);
								//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
							}else{
								//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
								FYj = YssD.sub(FYj, 
										YssFun.roundIt(
												       YssD.mul(cjje, 
										                        ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getExchangeRate(),
										                        0.01)
										,brokerRate.getYjCoursePreci()),
										YssFun.roundIt(
												       YssD.mul(cjje,
														        ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getExchangeRate(),
														        0.01)
										,brokerRate.getYjCoursePreci())
										);
								//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
							}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
						}
					}
					
                    if (alBears.contains("01") && !alBears.contains("05")) { //表示经手费由券商承担，证管费不由券商承担
                        //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                        //roundit(成交金额×深圳经手费率,计算深圳佣金过程中费用小数点保留位数),2)
                        if (! (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                               businessSign.equalsIgnoreCase("KZZGP"))) {
						    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){//大宗交易
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP JSF")).getBigExchange(), 0.01),
                                        brokerRate.getYjCoursePreci()));
                        	}else{
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP JSF")).getExchangeRate(), 0.01),
                                        brokerRate.getYjCoursePreci()));
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                    }

                    if (alBears.contains("05") && !alBears.contains("01")) { //表示证管费由券商承担，经手费不由券商承担
                        //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数) -
                        //roundit(成交金额×深圳证管费率,计算深圳佣金过程中费用小数点保留位数),2)
                        if (! (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                               businessSign.equalsIgnoreCase("KZZGP"))) {
						    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){//大宗交易
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP ZGF")).getBigExchange(), 0.01),
                                        brokerRate.getYjCoursePreci()));                      		
                        	}else{
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP ZGF")).getExchangeRate(), 0.01),
                                        brokerRate.getYjCoursePreci()));                        		
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                    }

                    if(alBears.contains("15")){ //表示过户费由券商承担
                        if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF")) {
						    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                        	if(tradeTypeCode.equals("DZ")){//大宗交易
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP ETF GHF")).getBigExchange(), 0.01),
                                        brokerRate.getYjCoursePreci()));
                        	}else{
                                FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjsl, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ GP ETF GHF")).getExchangeRate(), 0.01),
                                        brokerRate.getYjCoursePreci()));
                        	}
							//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        }
                    }

                    if(alBears.contains("14")){ //表示印花税由券商承担
                        if (! (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                               businessSign.equalsIgnoreCase("KZZGP"))) {
                            if (feeAttribute.getBs().equals("B")) {
							    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                    FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP B YHF")).getBigExchange(), 0.01),
                                            brokerRate.getYjCoursePreci()));
                            	}else{
                                    FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP B YHF")).getExchangeRate(), 0.01),
                                            brokerRate.getYjCoursePreci()));
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            }
                            if (feeAttribute.getBs().equals("S")) {
							    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                    FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP S YHF")).getBigExchange(), 0.01),
                                            brokerRate.getYjCoursePreci()));
                            	}else{
                                    FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                            hmRateSpeciesType.get("1 SZ GP S YHF")).getExchangeRate(), 0.01),
                                            brokerRate.getYjCoursePreci()));
                            	}
								//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            }
                        }
                    }
                    //story 2092 by zhouwei 20120106 深圳股票风险金不计算 QDV4赢时胜(上海开发部)2011年12月29日01_A
//                    if(alBears.contains("10")){//若股票风险金由券商承担
//                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
//                               hmRateSpeciesType.get("1 SZ GP FXJ")).getExchangeRate(), 0.01),
//                               brokerRate.getYjCoursePreci()));
//                    }
                    //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                    ArrayList alReadType = (ArrayList) readType.getParameters();
                    if(alReadType.contains("05"))
   				    {
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                    		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    						FYj = YssD.add(FYj, 
    								       YssFun.roundIt(
    								    		          YssD.mul(cjje, 
    								    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getBigExchange(),
    								                               0.01)
    								                      ,brokerRate.getYjCoursePreci()),
    								       YssFun.roundIt(
    								    		          YssD.mul(cjje, 
    								    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getBigExchange(),
    								    		        		   0.01)
    								    		          ,brokerRate.getYjCoursePreci())		   
    								        );	
    						//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                    	}else{
                    		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    						FYj = YssD.add(FYj, 
    								       YssFun.roundIt(
    								    		          YssD.mul(cjje, 
    								                               ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP JSF")).getExchangeRate(),
    								                               0.01)
    								                      ,brokerRate.getYjCoursePreci()),
    								       YssFun.roundIt(
    	    								    		  YssD.mul(cjje, 
    	    								    				   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ GP ZGF")).getExchangeRate(),
    	    								    				   0.01)
    	    								    		  ,brokerRate.getYjCoursePreci())		   
    								       );	
    						//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
   				    }    				
   				    //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                   
                    //如果佣金低于深圳股票佣金的最小费用，则重新调整佣金（佣金=深圳股票深圳佣金）
                    if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney();
                    }
                    //----深圳股票佣金----//
                }
            }
            //---股票

            //---基金
            if (securitySign.equalsIgnoreCase("JJ")) { //基金
                //成交金额 = roundit (成交价格* 成交数量,2 )
                cjje = YssFun.roundIt(YssD.mul(cjjg, cjsl), 2);

                if (hmRateSpeciesType.get("1 SZ JJ JSF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳基金的经手费费率数据！");
                }

                if (hmRateSpeciesType.get("1 SZ JJ ZGF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳基金的证管费费率数据！");
                }

                //获取深圳基金的经手费率和证管费率之和
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                    FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).getBigExchange(), 0.01);
                            //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getBigExchange(), 0.01));
                    //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                }else{
                	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                    FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).getExchangeRate(), 0.01);
                            //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getExchangeRate(), 0.01));
                    //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //基金经手费 = roundIt（成交金额 * （基金经手费率 + 基金证管费率）, 2）
                FJsf = YssFun.roundIt(YssD.mul(cjje, FJsfZgfRate), 2);

                //深圳基金经手费(经手费和证管费之和)起点金额//
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).getStartMoney(); 
                //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                //+( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getStartMoney();

                //当计算的基金经手费小于深圳基金经手费设置的起点金额，重新调整基金经手费（基金经手费=深圳基金经手费起点金额）
                if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                    FJsf = startMoney;
                }
                //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                //if (comeFromQs || cjmxSzZgfInfo != null) {
				    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ ZGF")).getBigExchange(), 0.01), 2);
                	}else{
                        FZgf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ ZGF")).getExchangeRate(), 0.01), 2);
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //深圳基金经手费(经手费和证管费之和)起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getStartMoney();

                    //当计算的基金经手费小于深圳基金经手费设置的起点金额，重新调整基金经手费（基金经手费=深圳基金经手费起点金额）
                    if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                        FZgf = startMoney;
                    }
                //}//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B

                //-----深圳基金风险金------------------//
                if (hmRateSpeciesType.get("1 SZ JJ FXJ") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳基金的风险金费率数据！");
                }

                //风险金=RoundIt(成交金额×深圳基金风险金利率), 2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ JJ FXJ")).getBigExchange(), 0.01), 2);
                }else{
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ JJ FXJ")).getExchangeRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //深圳基金风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ FXJ")).getStartMoney();

                //当计算的基金风险金小于深圳基金风险金设置的起点金额，重新调整基金风险金（基金风险金 = 深圳基金风险金起点金额）
                if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                    Ffxj = startMoney;
                }
                //-----深圳基金风险金------------------//

                //----深圳基金佣金----//

                //获取深圳基金对应组合代码和券商代码的佣金利率设置实例
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " +
                    portCode + " " + brokerCode + " 1 " + gsdm + " TR"); //获取深圳基金对应组合代码和券商代码的佣金利率设置实例

                if (brokerRate == null) {
                    throw new YssException("请在券商佣金利率设置中设置深圳基金的佣金费率数据！");
                }

                //佣金=roundit(成交金额×佣金利率,2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                }else{
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                originalFYj = FYj;//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
                
                if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF")) { //ETF申购或ETF赎回
                    //佣金=成交数量×佣金利率
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		FYj = YssD.mul(cjsl, brokerRate.getBigYjRate(), 0.01);
                	}else{
                		FYj = YssD.mul(cjsl, brokerRate.getYjRate(), 0.01);
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                } else { //若为普通基金
                    //佣金=roundit(成交金额×佣金利率,2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                	}else{
                		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                //04--基金经手费由券商承担 07--基金证管费由券商承担 13--基金风险金由券商承担
                if (alBears.contains("04") || alBears.contains("07") || alBears.contains("13")) {
                    //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                	}else{
                		FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (alBears.contains("04")) { //04--基金经手费由券商承担
                    //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                    //roundit(成交金额×深圳经手费率,计算深圳佣金过程中费用小数点保留位数),2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).
                                getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).
                                getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (alBears.contains("07")) { //07--基金证管费由券商承担
                    //佣金=roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                    //roundit(成交金额×深圳证管费率,计算深圳佣金过程中费用小数点保留位数),2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ ZGF")).getBigExchange(), 0.01),
                                brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ ZGF")).getExchangeRate(), 0.01),
                                brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if(alBears.contains("13")){ //13--基金风险金由券商承担
				    //---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ FXJ")).getBigExchange(), 0.01),
                                brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ JJ FXJ")).getExchangeRate(), 0.01),
                                brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }
                
               //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05"))
				{
				 	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    					FYj = YssD.add(FYj, 
    							       YssFun.roundIt(
    							    		          YssD.mul(cjje, 
    							    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).getBigExchange(),
    							                               0.01)
    							                      ,brokerRate.getYjCoursePreci()),
    							       YssFun.roundIt(
    							    		          YssD.mul(cjje, 
    							    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getBigExchange(),
    							    		        		   0.01)
    							    		          ,brokerRate.getYjCoursePreci())		   
    							        );	
    					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//	      		
                	}else{
                		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    					FYj = YssD.add(FYj, 
							       YssFun.roundIt(
							    		          YssD.mul(cjje, 
							                               ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ JSF")).getExchangeRate(),
							                               0.01)
							                      ,brokerRate.getYjCoursePreci()),
							       YssFun.roundIt(
 								    		      YssD.mul(cjje, 
 								    				       ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ JJ ZGF")).getExchangeRate(),
 								    				       0.01)
 								    		      ,brokerRate.getYjCoursePreci())		   
							       );
    					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
			    }    				
				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                
                //如果佣金低于深圳基金佣金的最小费用，则重新调整佣金（佣金= 深圳基金佣金的最小费用）
                if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                    FYj = brokerRate.getStartMoney();
                }
                //----深圳基金佣金----//
            }
            //---基金

            //----权证
            if (securitySign.equalsIgnoreCase("QZ")) {

                //成交金额 = roundit (成交价格 * 成交数量,2 )
                cjje = YssFun.roundIt(YssD.mul(cjjg, cjsl), 2);

                if (hmRateSpeciesType.get("1 SZ QZ JSF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳权证的经手费费率数据！");
                }

                if (hmRateSpeciesType.get("1 SZ QZ ZGF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳权证的证管费费率数据！");
                }

                //获取深圳权证的经手费率和证管费率之和
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                    FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF")).getBigExchange(), 0.01);
                    					   //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF")).getBigExchange(), 0.01));
                    //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                }else{
                	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                    FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF")).getExchangeRate(), 0.01);
                                           //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF")).getExchangeRate(), 0.01));
                    //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //经手费 = roundit(成交金额×（深圳权证经手费率+深圳权证证管费率），2)
                FJsf = YssFun.roundIt(YssD.mul(cjje, FJsfZgfRate), 2);

                //深圳权证经手费起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF")).getStartMoney();
                //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                //+( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF")).getStartMoney();

                //当计算的权证经手费小于深圳权证经手费设置的起点金额，重新调整权证经手费（权证经手费=深圳权证经手费起点金额）
                if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                    FJsf = startMoney;
                }

                if (hmRateSpeciesType.get("1 SZ QZ JIESUANF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳权证的结算费费率数据！");
                }

                //结算费=RoundIt(成交金额 ×深圳权证结算费, 2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                    Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ QZ JIESUANF")).getBigExchange(), 0.01), 2);
                }else{
                    Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ QZ JIESUANF")).getExchangeRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //深圳权证结算费起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JIESUANF")).getStartMoney();

                //当计算的权证结算费小于深圳权证结算费设置的起点金额，重新调整权证结算费（权证结算费 = 深圳权证结算费起点金额）
                if (cjje != 0 && startMoney != 0 && Fqtf < startMoney) {
                    Fqtf = startMoney;
                }

                //----深圳权证佣金----//
                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 1 " + gsdm + " OP"); //获取深圳权证对应组合代码和券商代码的佣金利率设置实例

                if (brokerRate == null) {
                    throw new YssException("请在券商佣金利率设置中设置深圳权证的佣金费率数据！");
                }

                //佣金=roundit(成交金额×深圳权证佣金利率,深圳佣金保留位数)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                }else{
                	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                originalFYj = FYj;//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
                
                //若权证经手费或权证证管费或权证结算费由券商承担
                if (alBears.contains("16") || alBears.contains("17") || alBears.contains("08")) {
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                	}else{
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (alBears.contains("16")) { //17--权证经手费由券商承担
                    //佣金 = roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                    //roundit(成交金额×深圳权证经手费率,计算深圳佣金过程中费用小数点保留位数),2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF"))
                                .getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF"))
                                .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (alBears.contains("17")) { //18--权证证管费由券商承担
                    //佣金 = roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                    //roundit(成交金额×深圳权证证管费率,计算深圳佣金过程中费用小数点保留位数),2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF"))
                                .getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF"))
                                .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }

                if (alBears.contains("08")) { //08--权证结算费由券商承担
                    //佣金 = roundit(roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)-
                    //roundit(成交金额×深圳权证结算费率,计算深圳佣金过程中费用小数点保留位数),2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JIESUANF"))
                                .getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                	}else{
                        FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JIESUANF"))
                                .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                }
                
                //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05"))
				{
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    					FYj = YssD.add(FYj, 
    							       YssFun.roundIt(
    							    		          YssD.mul(cjje, 
    							    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF")).getBigExchange(),
    							                               0.01)
    							                      ,brokerRate.getYjCoursePreci()),
    							       YssFun.roundIt(
    							    		          YssD.mul(cjje, 
    							    		        		   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF")).getBigExchange(),
    							    		        		   0.01)
    							    		          ,brokerRate.getYjCoursePreci())		   
    							        );	
    					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	}else{
                		//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    					FYj = YssD.add(FYj, 
							           YssFun.roundIt(
							    		              YssD.mul(cjje, 
							                                   ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ JSF")).getExchangeRate(),
							                                   0.01)
							                          ,brokerRate.getYjCoursePreci()),
							           YssFun.roundIt(
 								    		          YssD.mul(cjje, 
 								    				           ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ ZGF")).getExchangeRate(),
 								    				           0.01)
 								    		          ,brokerRate.getYjCoursePreci())		   
							       );
    					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	}
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
			    }    				
				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                
                //如果佣金低于深圳权证佣金的最小费用，则重新调整佣金（佣金=深圳权证佣金的最小费用）
                if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                    FYj = brokerRate.getStartMoney();
                }
                //----深圳权证佣金----//

                //-----深圳权证风险金------------------//
                if (hmRateSpeciesType.get("1 SZ QZ FXJ") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳权证的风险金费率数据！");
                }

                //风险金=RoundIt(成交金额×深圳权证风险金利率), 2)
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                if(tradeTypeCode.equals("DZ")){//大宗交易
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ QZ FXJ")).getBigExchange(), 0.01), 2);	
                }else{
                    Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ QZ FXJ")).getExchangeRate(), 0.01), 2);
                }
				//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                //深圳权证风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ QZ FXJ")).getStartMoney();

                //当计算的权证风险金小于深圳权证风险金设置的起点金额，重新调整权证风险金（权证风险金=深圳权证风险金起点金额）
                if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                    Ffxj = startMoney;
                }
                //-----深圳权证风险金------------------//
            }
            //----权证

            //----回购
            if (securitySign.equalsIgnoreCase("HG") && ! (businessSign.equalsIgnoreCase("MRHGDQ") || businessSign.equalsIgnoreCase("MCHGDQ"))) {
                if (cjsl != 0) { //若成交数量不为零
                    if (feeAttribute.getSelectedFee() == null) {
                        cjje = YssD.mul(cjsl, 100); //回购金额＝成交数量×100
                    }

                    //如果回购金额<=1000000，则经手费＝0.1
                    if (cjje <= 1000000 && cjsl != 0) {
                        FJsf = 0.1;
                    }

                    //如果回购金额> 1000000，则经手费＝10
                    if (cjje > 1000000 && cjsl != 0) {
                        FJsf = 10;
                    }

                    if (!comeFromQs) {
                    	//delete by songjie 2010.04.02
//                        //回购收益＝round([(回购成交价格/100)×(成交数量×100)×回购天数]/360, 8)
//                        Fhggain = YssD.round(YssD.div(YssD.mul(YssD.div(cjjg, 100), YssD.mul(cjsl, 100), getHGDays(zqdm)), 360), 8);
                    	//根据通知修改深交所回购收益算法 edit by songjie 2010.04.02
                        //回购收益＝round( round(回购成交价格   / 100 × 回购天数  / 360, 8) × 成交数量   × 100, 2)
                    	//add by songjie 2011.03.07  需求：750 QDV4赢时胜(上海)2011年3月6日01_AB 将cjjg改为通过getHGCjjg方法获取
                        Fhggain = YssD.round(YssD.mul(YssD.round(YssD.div(YssD.mul(YssD.div(getHGCjjg(oldZqdm, cjhm, bs), 100),getHGDays(zqdm)),360),8),cjsl, 100),2);
                    }

                    //-----深圳回购风险金----//
                    if (hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ") == null) {
                        throw new YssException("请在交易费率品种设置中设置深圳" + zqdm.substring(0, 6) + "回购的风险金费率数据！");
                    }

                    //风险金=RoundIt(成交金额×深圳回购风险金利率), 2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ")).getBigExchange(), 0.01), 2);
                    }else{
                        Ffxj = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ")).getExchangeRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//

                    //深圳回购风险金起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ")).getStartMoney();

                    //当计算的回购风险金小于深圳回购风险金设置的起点金额，重新调整回购风险金（回购风险金 = 深圳回购风险金起点金额）
                    if (cjje != 0 && startMoney != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                    //-----深圳回购风险金----//

                    //----深圳回购佣金----//
                    brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                        brokerCode + " 1 " + gsdm + " " + zqdm.substring(0, 6)); //获取深圳回购对应组合代码和券商代码的佣金利率设置实例

                    if (brokerRate == null) {
                        throw new YssException("请在券商佣金利率设置中设置深圳" + zqdm.substring(0, 6) + "回购的佣金费率数据！");
                    }

                    //佣金=roundit(成交金额×深圳回购佣金利率,深圳佣金保留位数)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                    }else{
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    originalFYj = FYj;//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B
                    
                    if (alBears.contains("03")) { //03--回购经手费由券商承担
                    	//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                        FYj = YssD.sub(FYj, YssD.round(FJsf, brokerRate.getYjCoursePreci()));//佣金 = 佣金 - 经手费
                    }

                    if(alBears.contains("12")){//12--回购风险金由券商承担
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                    hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ")).getBigExchange(), 0.01), 
                                    brokerRate.getYjCoursePreci()));
                    	}else{
                            FYj = YssD.sub(FYj, YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                    hmRateSpeciesType.get("1 SZ "+zqdm.substring(0, 6)+" FXJ")).getExchangeRate(), 0.01), 
                                    brokerRate.getYjCoursePreci()));
                    	}
						//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    }
                    
                    //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                    ArrayList alReadType = (ArrayList) readType.getParameters();
                    if(alReadType.contains("05")&&!alReadType.contains("06"))////回购佣金包含经手费
    				{
                    	//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
    					FYj = YssD.add(FYj, YssD.round(FJsf, brokerRate.getYjCoursePreci()));					
    			    }    				
    				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
                    
                    //----深圳回购佣金----//
                }
            }
            //----回购

            //----债券
            if (securitySign.equalsIgnoreCase("ZQ")) {
                //若要处理可分离债券的费用，先要到数据接口参数设置的读数处理方式中查询可分离债券
                //归入企业债还是分离可转债，若归入企业债，就把可分离债券的业务标志改为企业债,
                //系统计算费用时会按企业债的算法计算可分离债券
                if ( (businessSign.equals("MR_FLKZZ") || businessSign.equals("MC_FLKZZ")) && readType.getWBSBelong().indexOf("01")>-1) {
                    if (businessSign.equals("MR_FLKZZ")) {
                        businessSign = "MR_QYZQ";
                    }
                    if (businessSign.equals("MC_FLKZZ")) {
                        businessSign = "MC_QYZQ";
                    }
                }

                //成交金额 = roundit (成交价格 * 成交数量, 2)
                cjje = YssFun.roundIt(YssD.mul(cjjg, cjsl), 2);

                brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                    brokerCode + " 1 " + gsdm + " FI"); //获取深圳债券对应组合代码和券商代码的佣金利率设置实例

                if (brokerRate == null) {
                    throw new YssException("请在券商佣金利率设置中设置深圳债券的佣金费率数据！");
                }
                
                hmPerZQRate = super.getPerHundredZQRate(zqdm, date); //在债券利息表中查询债券利息数据

                haveInfo = (String) hmPerZQRate.get("haveInfo"); //判断债券利息表中是否有当前债券利息数据

                if (haveInfo.equals("false")) { //表示债券利息表中没有当前债券利息数据
                    //查找债券信息设置表中的数据计算债券的税前每百元债券利息，税后每百元债券利息并储存到哈希表中
                	
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
                    
                    FBeforeGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjsl), 2);
                    FGzlx = YssFun.roundIt(YssD.mul(Double.parseDouble( (String) hmZQRate.get("GZLX")), cjsl), 2);

                    bondInterest = new BondInterestBean(); //新建债券利息实例
                    bondInterest.setSecurityCode(zqdm); //设置证券代码
                    bondInterest.setIntAccPer100(new BigDecimal( (String) hmZQRate.get("SQGZLX"))); //设置税前百元利息
                    bondInterest.setSHIntAccPer100(new BigDecimal( (String) hmZQRate.get("GZLX"))); //设置税后百元利息

                    if (comeFromQs) {
                        if (!alZQCodes.contains(zqdm)) {
                            alZQCodes.add(zqdm);
                            alZQInfo.add(bondInterest); //将债券利息实例添加到列表中
                        }
                    }
                }
                else{
				    //---add by songjie 201.02.17 start---//
                	hmZQRate = new HashMap();
                	hmZQRate.put("SQGZLX", hmPerZQRate.get("PerGZLX"));
                	hmZQRate.put("GZLX", hmPerZQRate.get("SHPerGZLX"));
					//---add by songjie 201.02.17 end---//
                	
                    FGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(
                            ( (String) hmPerZQRate.get("PerGZLX"))),readType.getExchangePreci()), cjsl),2);
                    FBeforeGzlx = YssFun.roundIt(YssD.mul(YssFun.roundIt(Double.parseDouble(
                            ( (String) hmPerZQRate.get("SHPerGZLX"))),readType.getExchangePreci()), cjsl),2);
                }
                
                //---add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A start---//
                //结算金额 = round(成交价格+税前百元利息) * 成交数量,2)
                jsCjje = YssD.mul(YssD.add(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjjg), cjsl);
                //---add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A end---//
                
                if (hmRateSpeciesType.get("1 SZ ZQ JSF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳债券的经手费费率数据！");
                }

                if (hmRateSpeciesType.get("1 SZ ZQ ZGF") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳债券的证管费费率数据！");
                }

                if (hmRateSpeciesType.get("1 SZ ZQ FXJ") == null) {
                    throw new YssException("请在交易费率品种设置中设置深圳债券的风险金费率数据！");
                }

                //可转债计算经手费时，是先取经手费率和证管费率之和计算费用，再减去证管费得到经手费的，
                //其他类型的债券，计算经手费时，直接取经手费率就可以了。
                if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                	if(tradeTypeCode.equals("DZ")){//大宗交易
                		//计算经手费率和证管费率之和
                		//---edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                		FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF")).getBigExchange(), 0.01);
                                           //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getBigExchange(), 0.01));
                		//---edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                	}else{
                		//计算经手费率和证管费率之和
                		//---edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                		FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF")).getExchangeRate(), 0.01);
                                           //YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getExchangeRate(), 0.01));
                		//---edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	}
                }

                //国债 以及 地方政府债
                if (businessSign.equalsIgnoreCase("MR_GZ") || businessSign.equalsIgnoreCase("MC_GZ") ||
                	businessSign.equalsIgnoreCase("MR_DFZFZ") || businessSign.equalsIgnoreCase("MC_DFZFZ")) { //国债、地方政府债买入或卖出
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 02 01");

                    if (exchangeBond == null) {
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置深交所国债的相关参数！");
                    }

                    FZgf = 0; //国债证管费 = 0
                }

                if (businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ")) { //企业债
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 02 02");

                    if (exchangeBond == null) {
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置深交所企业债的相关参数！");
                    }
                } else if (businessSign.equalsIgnoreCase("MR_FLKZZ") || businessSign.equalsIgnoreCase("MC_FLKZZ")) { //分离可转债

                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 02 04");

                    if (exchangeBond == null) {
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置深交所分离可转债的相关参数！");
                    }
                } else if (businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")) { //公司债
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 02 05");

                    if (exchangeBond == null) {
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置深交所公司债的相关参数！");
                    }

                    if (hmRateSpeciesType.get("1 SZ ZQ JIESUANF") == null) {
                        throw new YssException("请在交易费率品种设置中设置深圳债券的结算费费率数据！");
                    }

                    if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
                        //结算费= RoundIt((RoundIt（成交价格 + 税前每百元债券利息），净价结算价格位数)) × 成交数量 × 深圳公司债券结算费率, 2)，
                        //竞价结算价格位数默认为2
//                      Fqtf = YssD.mul(YssFun.roundIt(YssD.add(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjjg), 2),
//                      cjsl, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF"))
//                      .getExchangeRate(), 0.01);
                    	
                    	//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
                    	//结算费 = round(结算金额 * 深圳公司债券结算费率, 2);
                    	
                    	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                    		Fqtf = YssD.mul(jsCjje,( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF")).getBigExchange(), 0.01);
                    	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    	}else{
                    		Fqtf = YssD.mul(jsCjje,( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF")).getExchangeRate(), 0.01);
                    	}
                    	
                    }

                    if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
                        //结算费= RoundIt((RoundIt（成交价格，净价结算价格位数)) × 成交数量 × 深圳公司债券结算费率, 2)，
                        //竞价结算价格位数默认为2
                    	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            Fqtf = YssD.mul(YssFun.roundIt(cjjg, 2), cjsl,
                                    ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF"))
                                    .getBigExchange(), 0.01);
                    	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    	}else{
                    		Fqtf = YssD.mul(YssFun.roundIt(cjjg, 2), cjsl,
                    				( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF"))
                    				.getExchangeRate(), 0.01);
                    	}
                    }

                    //深圳债券结算费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF")).getStartMoney();

                    //若结算费小于深圳债券结算费起点金额，则结算费等于深圳债券结算费起点金额
                    if (cjjg != 0 && cjsl != 0 && startMoney != 0 && Fqtf < startMoney) {
                        Fqtf = startMoney;
                    }
                } else if (businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")) { //可转债
                    //获取数据接口参数设置的交易所债券参数设置页面设置相关参数实例
                    exchangeBond = (ExchangeBondBean) hmExchangeBond.get(pub.getAssetGroupCode() + " " + portCode + " 02 03");

                    if (exchangeBond == null) {
                        throw new YssException("请在数据接口参数设置的交易所债券参数设置界面设置深交所可转债的相关参数！");
                    }
                }

                if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
                	//add by zhangfa 20110106 BUG #781 国内接口读入深交所债券佣金计算错误 
                	if(exchangeBond.getCommisionType().equals("00")){//00-按净价计算佣金
                		//佣金 = 成交金额*费率	
                		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                    		FYj=YssFun.roundIt(YssD.mul(cjje,brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                		}else{
                			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    		FYj=YssFun.roundIt(YssD.mul(cjje,brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                		}
                	}
                	//--------------end 20110106--------------------------------------

                    if (businessSign.equalsIgnoreCase("MR_GZ") || businessSign.equalsIgnoreCase("MC_GZ") ||
                    	businessSign.equalsIgnoreCase("MR_DFZFZ") || businessSign.equalsIgnoreCase("MC_DFZFZ")) { //国债、地方政府债 买入或卖出
                    	
                    	//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
                    	if(YssFun.roundIt(jsCjje,2) <= 1000000){//若结算金额 <= 1000000
                    	//若全价金额小于等于1000000
//                    	if(YssD.mul(YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl) <= 1000000){
                    		FJsf = 0.1; //经手费= 0.1
                    	}
                    	else{
                    		FJsf = 10; //经手费= 10
                    	}
                    }

                    //企业债、公司债以及分离交易可转债的证管费=IIf(RoundIt(RoundIt(hbcjjg+税前每百元债券利息),净价结算价格位数）* hbcjsl,2)<=1000000,0.1,10)
                    //净价结算价格位数默认为2
                    if (businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ") ||
                        businessSign.equalsIgnoreCase("MR_FLKZZ") || businessSign.equalsIgnoreCase("MC_FLKZZ") ||
                        businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")) {
                    	
                    	//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A 若 结算金额  <= 1000000
                    	if(YssFun.roundIt(jsCjje,2) <= 1000000) {
                        //若(成交价格 + 税前每百元债券利息) × 成交数量 <= 1000000
//                        if (YssFun.roundIt(YssD.mul(YssFun.roundIt(YssD.add(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjjg), 2), cjsl), 2)
//                            <= 1000000) {
                            FJsf = 0.1; //经手费
             //---delete by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) start---//
//							if (!(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ")))
//								if (comeFromQs || cjmxSzZgfInfo != null) {
//									FZgf = 0.1; // 证管费
//								}
             //---delete by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) end---//
                        } else {
                            FJsf = 10; //经手费 = 经手费 + 证管费
             //---delete by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) start---//               
//                            if (!(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ"))){
//                                if (comeFromQs || cjmxSzZgfInfo != null) {
//                                    FZgf = 10; //证管费
//                                }
//                            }
             //---delete by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) end---//              
                        }
                    }

                    if(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ") ||
                       businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ") ||
             //---add by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) start---//
                       businessSign.equalsIgnoreCase("MR_FLKZZ") || businessSign.equalsIgnoreCase("MC_FLKZZ") ||
                       businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")){
             //---add by songjie 2012.02.16 分离可转债、企业债征管费算法 = roundIt((成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2) end---//       	
                    	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                    		//经手费 = roundit(结算金额  ×（深圳债券经手费率+深圳债券证管费率），2)
                    		FJsf = YssD.mul(jsCjje, FJsfZgfRate);//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
                    		
//                    		//经手费 = roundit((成交价格 + 税前每百元债券利息) × 成交数量  ×（深圳债券经手费率+深圳债券证管费率），2)
//                            FJsf = YssD.mul(YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl, FJsfZgfRate); 
                            
                    		//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                            //if (comeFromQs || cjmxSzZgfInfo != null) { //表示在数据从交易接口明细库到交易接口清算库的处理中再次调用本方法计算费用
                                //征管费 = roundIt(结算金额 × 深圳债券征管费率,2)
                            	//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                            	if(tradeTypeCode.equals("DZ")){//大宗交易
                                	FZgf = YssFun.roundIt(YssD.mul(jsCjje,( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                                            .getBigExchange(), 0.01), 2);
                            	}else{
                            		//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                                	FZgf = YssFun.roundIt(YssD.mul(jsCjje,( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                                            .getExchangeRate(), 0.01), 2);
                            	}

//                              //征管费 = roundIt( (成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2)
//                            	FZgf = YssFun.roundIt(YssD.mul(
//                            			YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg),
//                            			cjsl,( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
//                                        .getExchangeRate(), 0.01), 2);	
                            //}//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                    	}
                    	else{
                    		//征管费 = roundIt(结算金额  × 深圳债券征管费率,2)
                    		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    		if(tradeTypeCode.equals("DZ")){//大宗交易
                    			FZgf = YssD.mul(jsCjje, ( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                    					.getBigExchange(), 0.01);//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
                    		}else{
                    			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        		FZgf = YssD.mul(jsCjje, ( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                                        .getExchangeRate(), 0.01);//add by songjie 2012.04.18 STORY #2523 QDV4赢时胜(上海开发部)2012年04月18日01_A
                    		}
//                            //征管费 = roundIt( (成交价格 + 税前每百元债券利息) × 成交数量  × 深圳债券征管费率,2)
//                        	FZgf = YssD.mul(
//                        			YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg),
//                        			cjsl,( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ ZGF"))
//                                    .getExchangeRate(), 0.01);
                    	}
                    }

                    if (exchangeBond.getCommisionType().equals("01")) { //01-按净价加利息税计算佣金
                        //佣金 = (成交金额 + 税前债券利息 - 税后债券利息) * 佣金利率
                    	//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                            FYj = YssFun.roundIt(YssD.mul(
                                    YssD.add(cjje, YssD.sub(
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjsl),
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("GZLX")), cjsl))),
                                    brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                    	}else{
                    		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            FYj = YssFun.roundIt(YssD.mul(
                                    YssD.add(cjje, YssD.sub(
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("SQGZLX")), cjsl),
                                    YssD.mul(Double.parseDouble( (String) hmZQRate.get("GZLX")), cjsl))),
                                    brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                    	}
                    }

                    if (exchangeBond.getCommisionType().equals("02")) { //02-按全价计算佣金
                    	//佣金 = (成交价格 + 税前每百元债券利息) × 成交数量 × 佣金利率
                    	//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    	if(tradeTypeCode.equals("DZ")){//大宗交易
                        	FYj = YssFun.roundIt(YssD.mul(
                        			YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl, 
                        			brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                    	}else{
                    		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        	FYj = YssFun.roundIt(YssD.mul(
                        			YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl, 
                        			brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                    	}
                    }

                    //风险金 = RoundIt((成交价格 + 税前每百元债券利息) × 成交数量 × 深圳债券风险金利率), 2)
                    //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssD.mul(
                        		YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl,
                            ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ FXJ"))
                            .getBigExchange(), 0.01);
                    }else{
                    	//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        Ffxj = YssD.mul(
                        		YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl,
                            ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ FXJ"))
                            .getExchangeRate(), 0.01);
                    }


                    fxjParamNum = true;
                    yjParamNum = true; //表示佣金的计算公式中包含成交数量这个参数，因为佣金的计算公式中包含债券利息这个参数，
                    //而债券利息的计算公式中又包含成交数量这个参数，所以可以说若佣金的计算公式中包含债券利息这个参数的话，
                    //就表示佣金的计算公式中包含成交数量这个参数
                }

                if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
                	if(exchangeBond.getCommisionType().equals("00")){//00-按净价计算佣金
                        //佣金= roundit((成交金额 - 债券利息) * 深圳佣金利率, 计算深圳佣金小数点保留位数)
                		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                            FYj = YssFun.roundIt(YssD.mul(YssD.sub(cjje, FGzlx), brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());
                		}else{
                			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                            FYj = YssFun.roundIt(YssD.mul(YssD.sub(cjje, FGzlx), brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                		}
                	}else{
                        //佣金= roundit(成交金额 * 深圳佣金利率, 计算深圳佣金小数点保留位数)
                		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                			FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), brokerRate.getYjPreci());	
                		}else{
                			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                			FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());	
                		}
                	}

                    if (businessSign.equalsIgnoreCase("MR_GZ") || businessSign.equalsIgnoreCase("MC_GZ") ||
                    	businessSign.equalsIgnoreCase("MR_DFZFZ") || businessSign.equalsIgnoreCase("MC_DFZFZ")	) { //国债、地方政府债买入或卖出
                        if (cjje <= 1000000) { //若成交金额小于等于1000000
                            FJsf = 0.1; //经手费= 0.1
                        } else {
                            FJsf = 10; //经手费= 10
                        }
                    }

                    //企业债、公司债以及分离交易可转债的证管费=IIf(RoundIt(RoundIt(hbcjjg,净价结算价格位数)* hbcjsl,2)<=1000000,0.1,10)
                    //净价结算价格位数默认为2
                    if (businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ") ||
                        businessSign.equalsIgnoreCase("MR_FLKZZ") || businessSign.equalsIgnoreCase("MC_FLKZZ") ||
                        businessSign.equalsIgnoreCase("MR_QYZQ_GS") || businessSign.equalsIgnoreCase("MC_QYZQ_GS")) {

                        //若成交价格 × 成交数量 <= 1000000
                        if (YssFun.roundIt(YssD.mul(YssFun.roundIt(cjjg, 2), cjsl), 2)
                            <= 1000000) {
                            FJsf = 0.1;
                            if(!(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ"))){
	                            if (comeFromQs || cjmxSzZgfInfo != null) {
	                                FZgf = 0.1; //证管费
	                            }
                            }
                        } else {
                            FJsf = 10;
                            if(!(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ"))){
	                            if (comeFromQs || cjmxSzZgfInfo != null) {
	                                FZgf = 10; //证管费
	                            }
                            }
                        }
                    }
                    
                    if(businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ") ||
                       businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                    	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
	                        //经手费 = roundit(成交金额×（深圳债券经手费率+深圳债券证管费率），2)
	                        FJsf = YssD.mul(cjje, FJsfZgfRate);
	                        //---delete by songjie 2012.02.16 可转债 征管费 = 0 start---//
//	                        if (comeFromQs || cjmxSzZgfInfo != null) { //表示在数据从交易接口明细库到交易接口清算库的处理中再次调用本方法计算费用
//	                            FZgf = YssD.mul(cjje, ( (RateSpeciesTypeBean)
//	                                hmRateSpeciesType.get("1 SZ ZQ ZGF")).getExchangeRate(), 0.01);
//	                        }
	                        //---delete by songjie 2012.02.16 可转债 征管费 = 0 end---//
                    	}
                    	else{
                    		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    		if(tradeTypeCode.equals("DZ")){//大宗交易
                                FZgf = YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ ZQ ZGF")).getBigExchange(), 0.01);
                    		}else{
                    			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                                FZgf = YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                        hmRateSpeciesType.get("1 SZ ZQ ZGF")).getExchangeRate(), 0.01);
                    		}

                    	}
                    }

                    //风险金=RoundIt(成交金额 × 深圳债券风险金利率), 2)
                    //---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易
                        Ffxj = YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ ZQ FXJ")).getBigExchange(), 0.01);
                    }else{
                    	//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                        Ffxj = YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ ZQ FXJ")).getExchangeRate(), 0.01);
                    }
                }

                if (alBears.contains("02")) { //02--债券经手费由券商承担
//                	if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
//                        //佣金 = 佣金 -roundit(成交金额 × 深圳债券经手费率, 
//                		//计算深圳佣金过程中费用小数点保留位数),2)
//                        FYj -= YssFun.roundIt(YssD.mul(cjje, 
//                        		( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF"))
//                            .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
//                	if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
//                		//佣金 = 佣金 -roundit((成交价格 + 税前每百元债券利息) × 成交数量 
//                		//× 深圳债券经手费率, 计算深圳佣金过程中费用小数点保留位数),2)
//                        FYj -= YssFun.roundIt(YssD.mul(
//                        		YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl, 
//                        		( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF"))
//                            .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
                	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                    		FYj =YssD.sub(FYj, YssD.round(YssD.mul(cjje, ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF"))
                                    .getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                		}else{
                			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    		FYj =YssD.sub(FYj, YssD.round(YssD.mul(cjje, ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF"))
                                    .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                		}

                	}
                	else{
                		FYj =YssD.sub(FYj, YssD.round(FJsf, brokerRate.getYjCoursePreci()));
                	}
                }

                if (alBears.contains("06")) { //06--债券证管费由券商承担
//                	if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
//	                    //佣金-= roundit(成交金额×深圳债券证管费率,计算深圳佣金过程中费用小数点保留位数),2)
//	                    FYj -= YssFun.roundIt(YssD.mul(cjje,
//	                    		( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF"))
//	                        .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
//                	if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
//                        //佣金 -= roundit((成交价格 + 税前每百元债券利息) × 成交数量 
//                		//× 深圳债券证管费率,计算深圳佣金过程中费用小数点保留位数),2)
//                        FYj -= YssFun.roundIt(YssD.mul(
//                        		YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl, 
//                        		( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF"))
//                            .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
                	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                		//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                		if(tradeTypeCode.equals("DZ")){//大宗交易
                    		FYj =YssD.sub(FYj, YssD.round(YssD.mul(cjje, ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                                    .getBigExchange(), 0.01), brokerRate.getYjCoursePreci()));
                		}else{
                			//---add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    		FYj =YssD.sub(FYj, YssD.round(YssD.mul(cjje, ((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF"))
                                    .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci()));
                		}

                	}else{
                		FYj =YssD.sub(FYj, YssD.round(FZgf, brokerRate.getYjCoursePreci()));
                	}
                }

                if (alBears.contains("18")) { //18--债券结算费由券商承担
//                	if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
//	                    //佣金 -= roundit(成交金额×深圳债券结算费率,
//                		//计算深圳佣金过程中费用小数点保留位数),2)
//	                    FYj -= YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF"))
//	                        .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
//                	if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
//                        //佣金 -= roundit((成交价格 + 税前每百元债券利息) × 成交数量 
//                		//×深圳债券结算费率,计算深圳佣金过程中费用小数点保留位数),2)
//                        FYj -= YssFun.roundIt(YssD.mul(
//                        		YssD.add(Double.parseDouble((String)hmZQRate.get("SQGZLX")), cjjg), cjsl,
//                        		( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JIESUANF"))
//                            .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
                	FYj = YssD.sub(FYj, YssD.round(Fqtf, brokerRate.getYjCoursePreci()));
                }

                if(alBears.contains("11")){//11--债券风险金由券商承担
//                	if (exchangeBond.getBondTradeType().equals("01")) { //全价交易
//	                    //佣金 -= roundit(成交金额×深圳债券风险金费率,
//                		//计算深圳佣金过程中费用小数点保留位数),2)
//	                    FYj -= YssFun.roundIt(YssD.mul(cjje, 
//	                    		( (RateSpeciesTypeBean)hmRateSpeciesType.get("1 SZ ZQ FXJ"))
//	                        .getExchangeRate(), 0.01), brokerRate.getYjCoursePreci());
//                	}
//                	if (exchangeBond.getBondTradeType().equals("00")) { //净价交易
//                        //佣金 -= roundit((成交价格 + 税前每百元债券利息) × 成交数量 
//                		//×深圳债券风险金费率,计算深圳佣金过程中费用小数点保留位数),2)
//						FYj -= YssFun.roundIt(YssD.mul(
//								YssD.add(Double.parseDouble((String) hmZQRate.get("SQGZLX")),cjjg), cjsl,
//								((RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ FXJ")).getExchangeRate(),
//								0.01), brokerRate.getYjCoursePreci());
//                	}
                	FYj = YssD.sub(FYj, YssD.round(Ffxj, brokerRate.getYjCoursePreci()));
                }

                if (alBears.contains("02") || alBears.contains("06") || alBears.contains("18") || alBears.contains("11")) {
                    FYj = YssFun.roundIt(FYj, 2); //保留两位小数
                }

                if (yjParamNum) { //表示计算佣金的计算公式中包含成交数量这个参数
                    if (cjje != 0 && cjsl != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney(); //深圳债券佣金的起点金额
                    }
                } else { //表示计算佣金的计算公式中不包含成交数量这个参数
                    if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney(); //深圳债券佣金的起点金额
                    }
                }

                //深圳债券风险金起点金额
                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ FXJ")).getStartMoney();

                //如果风险金小于深圳债券风险金起点金额，则重新调整风险金（风险金 = 深圳债券风险金起点金额）
                if (fxjParamNum) {
                    if (cjje != 0 && cjsl != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                } else {
                    if (cjje != 0 && Ffxj < startMoney) {
                        Ffxj = startMoney;
                    }
                }
                
                if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ") ||
                   businessSign.equalsIgnoreCase("MR_QYZQ") || businessSign.equalsIgnoreCase("MC_QYZQ")){
                	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                        //深圳债券经手费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ JSF")).getStartMoney();
                        //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                            // + ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getStartMoney();
                        //若经手费小于深圳债券经手费起点金额，则经手费等于深圳债券经手费起点金额
                        if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                            FJsf = startMoney;
                        }
                	}

                	//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                    //if (comeFromQs || cjmxSzZgfInfo != null) { //表示在数据从交易接口明细库到交易接口清算库的处理中再次调用本方法计算费用
                        //深圳债券证管费起点金额
                        startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ ZQ ZGF")).getStartMoney();

                        //若证管费小于深圳债券证管费起点金额，则证管费等于深圳债券证管费起点金额
                        if (cjje != 0 && startMoney != 0 && FZgf < startMoney) {
                            FZgf = startMoney;
                        }
                    //}//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                }
                
                //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                ArrayList alReadType = (ArrayList) readType.getParameters();
                if(alReadType.contains("05"))
				{
                	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
					FYj = YssD.add(FYj, YssD.round(FJsf, brokerRate.getYjCoursePreci()));
					FYj = YssD.add(FYj, YssD.round(FZgf, brokerRate.getYjCoursePreci()));
					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
			    }    				
				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
             
            }
            //----债券

            //若证券标志为 新股 新债 权益
            if(securitySign.equalsIgnoreCase("XG") || securitySign.equalsIgnoreCase("XZ") || securitySign.equalsIgnoreCase("QY")){
            	cjje = YssD.mul(cjjg, cjsl);//成交金额 = 成交价格  * 成交数量
            }
                        
            if(securitySign.equals("B_GP")){//B股业务 panjunfang add 20100423

                //成交金额：roundit (成交价格 * 成交数量, 2)
                cjje = YssFun.roundIt(YssD.mul(cjjg, cjsl), 2);

                    //ETF申购股票，ETF赎回股票，可转债股票的股票
                    if (businessSign.equalsIgnoreCase("SH_ETF") || businessSign.equalsIgnoreCase("SG_ETF") ||
                        businessSign.equalsIgnoreCase("KZZGP")) {

                    } else {
                            if (hmRateSpeciesType.get("1 SZ B_GP JSF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳B股经手费费率数据！");
                            }

                            if (hmRateSpeciesType.get("1 SZ B_GP ZGF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳B股证管费费率数据！");
                            }

                            //获取深圳股票的经手费率和证管费率之和
                            if(tradeTypeCode.equals("DZ")){//大宗交易
                            	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                                FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF")).getBigExchange(), 0.01);
                                                      // YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getBigExchange(), 0.01));
                                //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                            }else{//普通交易
                            	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                                FJsfZgfRate = /*YssD.add(*/YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF")).getExchangeRate(), 0.01);
                                                      // YssD.mul( ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getExchangeRate(), 0.01));
                                //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                            }

                            //经手费 = roundit（成交金额×（深圳股票经手费率+深圳股票证管费率）,2）
                            FJsf = YssFun.roundIt(YssD.mul(cjje, FJsfZgfRate), 2);

                            //深圳股票经手费和证管费起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JSF")).getStartMoney();
                            //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                               // + ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getStartMoney();

                            //如果深圳股票经手费和证管费小于深圳股票经手费和证管费的起点金额，那么深圳股票的经手费和证管费就等于深圳股票经手费和证管费的起点金额
                            if (cjje != 0 && startMoney != 0 && FJsf < startMoney) {
                                FJsf = startMoney;
                            }

                            //delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                            //if (comeFromQs || cjmxSzZgfInfo != null) {
                            	if(tradeTypeCode.equals("DZ")){//大宗交易证管费 =汇总表中的成交金额 × 深圳B股证管费大宗交易费率
                                    FZgf = YssFun.roundIt(YssD.mul(feeAttribute.getCjje(), YssD.mul( ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SZ B_GP ZGF")).getBigExchange(), 0.01)), 2);
                            	}else{//普通证管费 =汇总表中的成交金额 × 深圳B股证管费普通交易费率
                                    FZgf = YssFun.roundIt(YssD.mul(feeAttribute.getCjje(), YssD.mul( ( (RateSpeciesTypeBean) 
                                    		hmRateSpeciesType.get("1 SZ B_GP ZGF")).getExchangeRate(), 0.01)), 2);	
                            	}

                                //深圳证管费起点金额
                                startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP ZGF")).getStartMoney();

                                //如果深圳股票证管费小于深圳股票证管费的起点金额，那么深圳股票证管费就等于深圳股票证管费的起点金额
                                if (feeAttribute.getCjje() != 0 && startMoney != 0 && FZgf < startMoney) {
                                    FZgf = startMoney;
                                }
                            //}//delete by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B

                        if (feeAttribute.getBs().equals("B")) {
                            if (hmRateSpeciesType.get("1 SZ B_GP B YHF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳B股买印花税费率数据！");
                            }
                            
                            if(tradeTypeCode.equals("DZ")){//大宗交易 ：买入印花税=roundit(成交价格×成交数量×深圳B股票买印花税大宗交易费率,2)
                            	FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                            			hmRateSpeciesType.get("1 SZ B_GP B YHF")).getBigExchange(), 0.01), 2);
                            }else{  //普通交易：买入印花税= roundit(成交价格×成交数量×深圳股票买印花税费率,2)
                            	FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                            			hmRateSpeciesType.get("1 SZ B_GP B YHF")).getExchangeRate(), 0.01), 2);
                            }
                            
                            //深圳B股买印花税起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP B YHF")).getStartMoney();

                            //如果深圳股票买印花税小于深圳股票买印花税的起点金额，那么深圳股票的买印花税就等于深圳股票买印花税的起点金额
                            if (cjjg != 0 && cjsl != 0 && startMoney != 0 && FYhs < startMoney) {
                                FYhs = startMoney;
                            }
                        }
                        if (feeAttribute.getBs().equals("S")) {
                            if (hmRateSpeciesType.get("1 SZ B_GP S YHF") == null) {
                                throw new YssException("请在交易费率品种设置中设置深圳B股卖印花税费率数据！");
                            }
                            
                            if(tradeTypeCode.equals("DZ")){//大宗交易 ：卖出印花税=roundit(成交价格×成交数量×深圳B股票卖印花税大宗交易费率,2)
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                    hmRateSpeciesType.get("1 SZ B_GP S YHF")).getBigExchange(), 0.01), 2);
                            }else{//普通交易：卖出印花税= roundit（成交价格×成交数量×深圳股票卖印花税费率，2）
                                FYhs = YssFun.roundIt(YssD.mul(cjjg, cjsl, ( (RateSpeciesTypeBean)
                                    hmRateSpeciesType.get("1 SZ B_GP S YHF")).getExchangeRate(), 0.01), 2);
                            }
                            
                            //深圳B股卖印花税起点金额
                            startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP S YHF")).getStartMoney();

                            //如果深圳B股卖印花税小于深圳股票卖印花税的起点金额，那么深圳股票的卖印花税就等于深圳股票卖印花税的起点金额
                            if (cjjg != 0 && cjsl != 0 && startMoney != 0 && FYhs < startMoney) {
                                FYhs = startMoney;
                            }
                        }
                    }

                    //----深圳股票结算费----//
                    if (hmRateSpeciesType.get("1 SZ B_GP JIESUANF") == null) {
                        throw new YssException("请在交易费率品种设置中设置深圳B股结算费费率数据！");
                    }

                    if(tradeTypeCode.equals("DZ")){//大宗交易 ：
                    	Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                                hmRateSpeciesType.get("1 SZ B_GP JIESUANF")).getBigExchange(), 0.01), 2);
                    }else{ //普通交易：结算费 =RoundIt(成交金额 × 深圳B股结算费率, 2)
                    	Fqtf = YssFun.roundIt(YssD.mul(cjje, ( (RateSpeciesTypeBean)
                            hmRateSpeciesType.get("1 SZ B_GP JIESUANF")).getExchangeRate(), 0.01), 2);
                    }

                    //深圳股票结算费起点金额
                    startMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JIESUANF")).getStartMoney();

                    //如果结算费小于深圳股票最低结算费金额，则重新调整结算费（结算费=深圳股票最低结算费金额）
                    if (cjje != 0 && startMoney != 0 && Fqtf < startMoney) {
                    	Fqtf = startMoney;
                    }
                    
                    //深圳股票结算费上限金额
                    double maxMoney = ( (RateSpeciesTypeBean) hmRateSpeciesType.get("1 SZ B_GP JIESUANF")).getUpperLimitS();

                    //如果结算费大于深圳股票上限金额，则重新调整结算费（结算费=深圳股票结算费上限金额）
                    if (cjje != 0 && maxMoney != 0 && Fqtf > maxMoney) {
                    	Fqtf = maxMoney;
                    }
                    //----深圳股票结算费----//

                    //----深圳股票佣金----//
                    brokerRate = (BrokerRateBean) hmBrokerRate.get(pub.getAssetGroupCode() + " " + portCode + " " +
                        brokerCode + " 1 " + gsdm + " EQ B"); //获取深圳股票对应组合代码和券商代码的佣金利率设置实例

                    if (brokerRate == null) {
                        throw new YssException("请在券商佣金利率设置中设置深圳B股佣金费率数据！");
                    }

                    //佣金=roundit(成交金额×佣金利率,2)
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 start---//
                    if(tradeTypeCode.equals("DZ")){//大宗交易 
                    	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getBigYjRate(), 0.01), 2);
                    }else{
                    	FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), 2);
                    }
					//---edit by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001 end---//
                    originalFYj = FYj;//add by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B 
                    
                    //如果佣金低于深圳股票佣金的最小费用，则重新调整佣金（佣金=深圳股票深圳佣金）
                    if (cjje != 0 && FYj < brokerRate.getStartMoney()) {
                        FYj = brokerRate.getStartMoney();
                    }
                    
                    //若股票的经手费 或 证券费 由券商承担,佣金=roundit(成交金额×佣金利率,计算深圳佣金时小数点保留位数)
                    if (alBears.contains("01") || alBears.contains("05")) {
                        FYj = YssFun.roundIt(YssD.mul(cjje, brokerRate.getYjRate(), 0.01), brokerRate.getYjPreci());
                        
                    }
                    
                    //若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----start
                    ArrayList alReadType = (ArrayList) readType.getParameters();
                    if(alReadType.contains("05"))
    				{
                    	//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
    					FYj = YssD.add(FYj, YssD.round(FJsf, brokerRate.getYjCoursePreci()));
    					FYj = YssD.add(FYj, YssD.round(FZgf, brokerRate.getYjCoursePreci()));
    					//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
    			    }    				
    				//若设置了参数‘佣金包含经手费，征管费’，则需在计算佣金时需加上经手费，征管费   update by guolongchao 20120217 STORY 2261-----end
               
                    //----深圳B股佣金----//
            }
            
            if (comeFromQs) { //表示在数据从交易接口明细库到交易接口清算库的处理中再次调用本方法计算费用
                if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FJsf")) {
                	//经手费 =经手费与证管费之和（按明细计算）-证管费（按汇总计算）
                	//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                	feeAttribute.setFJsf(/*YssD.sub(*/YssD.round(feeAttribute.getFJsf(),2)/*, YssD.round(FZgf,2))*/); 
                    return;
                }

                if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FZgf")) {
                    feeAttribute.setFZgf(YssD.round(FZgf,2));
                    if(feeAttribute.getSecuritySign() != null && feeAttribute.getSecuritySign().equals("B_GP")){//B股 panjunfang add 20100429
                    	//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                    	feeAttribute.setFJsf(/*YssD.sub(*/YssD.round(feeAttribute.getFJsf(),2)/*, YssD.round(FZgf,2))*/); //根据汇总数据重新计算证管费，同时计算经手费  = 经手费与证管费之和（按明细计算）-证管费（按汇总计算）
                    }
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
                    feeAttribute.setFqtf(YssD.round(Fqtf,2));
                    return;
                }

                if (feeAttribute.getSelectedFee() != null && feeAttribute.getSelectedFee().equals("FYj")) {
                	//---edit by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B start---//
                	//如果经手费、征管费由券商承担、且佣金通过成交汇总计算，则佣金 = 汇总成交金额 * 佣金利率 - 经手费 和 征管费之和（明细计算）
                	if(alBears.contains("01") && alBears.contains("05") && cjhzSzYjInfo != null && cjhzSzYjInfo.equals(feeAttribute.getPortCode())){
                		feeAttribute.setFYj(YssFun.roundIt(YssD.sub(originalFYj,
                				//--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B start---//
                				YssD.round(feeAttribute.getFJsf(), brokerRate.getYjCoursePreci()),
                				YssD.round(feeAttribute.getFZgf(), brokerRate.getYjCoursePreci())),2));
                		        //--- edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B end---//
                	}else{
                		feeAttribute.setFYj(YssFun.roundIt(FYj,2));	
                	}
                    //---edit by songjie 2012.11.07 BUG 6227 QDV4农业银行2012年11月06日01_B end---//
                    return;
                }
            } else {
            	if(securitySign.equalsIgnoreCase("ZQ")){
                	if(businessSign.equalsIgnoreCase("MR_KZZ") || businessSign.equalsIgnoreCase("MC_KZZ")){
                		//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                		feeAttribute.setFJsf(/*YssD.sub(*/YssD.round(FJsf,2)/*, YssD.round(FZgf,2))*/); //设置经手费
                	}
                	else{
                		feeAttribute.setFJsf(YssD.round(FJsf,2)); //设置经手费
                	}
            	}else{
            		//edit by songjie 2013.05.07 BUG 7639 QDV4嘉实2013年04月26日01_B
                    feeAttribute.setFJsf(/*YssD.sub(*/YssD.round(FJsf,2)/*, YssD.round(FZgf,2))*/); //设置经手费
            	}
            	
                feeAttribute.setFZgf(YssD.round(FZgf,2)); //设置证管费
            }

            feeAttribute.setCjsl(cjsl); //设置成交数量
            feeAttribute.setCjje(cjje); //设置成交金额
            feeAttribute.setFYhs(FYhs); //设置印花税
            feeAttribute.setFGhf(FGhf); //设置过户费
            feeAttribute.setFYj(YssFun.roundIt(FYj,2)); //设置佣金
            feeAttribute.setFfxj(YssD.round(Ffxj, 2)); //设置风险金
            feeAttribute.setFqtf(YssD.round(Fqtf,2)); //设置结算费
            feeAttribute.setFhggain(Fhggain); //设置回购收益
            feeAttribute.setFBeforeGzlx(FBeforeGzlx); //设置税前债券利息
            feeAttribute.setFGzlx(FGzlx); //设置税后债券利息
        } catch (Exception e) {
            throw new YssException("计算费用出错", e);
        }
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
                "(select FDepDurCode from " + pub.yssGetTableName("Tb_Para_Purchase") +
                " where FSecurityCode = " + dbl.sqlString(securityCode) + ") ";
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
     * 拆分券商承担的费用数据
     * @param brokerBear String
     * @return ArrayList
     */
    private ArrayList splitBrokerBear(String brokerBear) {
        String[] brokerBears = brokerBear.split(","); //用逗号拆分数据
        ArrayList alBears = new ArrayList(); //新建ArrayList
        for (int i = 0; i < brokerBears.length; i++) {
            alBears.add(brokerBears[i]); //将费用代码添加到alBears中
        }
        return alBears; //返回储存费用代码的ArrayList
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
        String seatCode = "";
        try {
        	//edit by songjie 2010.06.12
            strSql = "select FSeatType, FSeatNum from " + pub.yssGetTableName("Tb_Para_TradeSeat") + " where FCheckState = 1 " +
                "and FSeatNum in(select distinct subStr(HBHTXH, 0 , 6) from SZHB) ";
            rs = dbl.openResultSet(strSql); //根据席位代码查询席位类型
            //edit by songjie 2010.06.12
            while (rs.next()) {
                seatType = rs.getString("FSeatType"); //席位类型
                //edit by songjie 2010.06.12
                seatCode = rs.getString("FSeatNum"); //席位代码
                hmSeatType.put(seatCode, seatType);
            }
            return hmSeatType;
        } catch (Exception e) {
            throw new YssException("根据席位代码查询席位类型出错！", e);
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
            strSql = " select HBHTXH, HBZQDM from SZHB where HBHTXH like '%" + sqbh + "' and HBZQDM like '1599%' ";
            rs = dbl.openResultSet(strSql); //根据申请编号查询业务标志为ETF申购或赎回的证券代码
            while (rs.next()) {
                haveETFSec = "true"; //判断有ETF基金数据
                zqdmETF = rs.getString("HBZQDM"); //ETF基金证券代码
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
     * add by songjie 
     * 2011.03.07
     * 需求：750 
     * QDV4赢时胜(上海)2011年3月6日01_AB
     * 获取回购利率
     * @param zqdm
     * @param cjhm
     * @return
     * @throws YssException
     */
    private double getHGCjjg(String zqdm, String cjhm, String bs) throws YssException{
        String strSql = "";
        ResultSet rs = null;
    	double cjjg = 0;
    	try{
    		cjhm = Integer.parseInt(cjhm) + 1 + "";
    		//sql逻辑：若为回购业务，则获取相同证券代码、成交数量 = 0 、买卖标识相反、
    		//成交号码 = 成交号码 +1 的数据中的成交价格作为回购利率来计算回购收益
            strSql = " select HBZQDM, HBCJJG from SZHB where HBCJSL = 0 " + 
            " and HBCJHM = " + cjhm + " and HBZQDM = " + dbl.sqlString(zqdm);
            //delete by songjie 2012.07.25 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A 不根据HBYWLB查询回购成交价格数据
            //" and HBYWLB like " + dbl.sqlString("%" + bs);//(bs.equals("B")?"S":"B")  bug 4274 modify by zhouwei 20120412 不进行反转
            rs = dbl.openResultSet(strSql); //根据申请编号查询业务标志为ETF申购或赎回的证券代码
    		while(rs.next()){
    			cjjg = rs.getDouble("HBCJJG");
    		}
            return cjjg;
    	}catch(Exception e){
    		throw new YssException("计算回购收益时获取回购利率出错！", e);
    	}finally{
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
        try {
            //配股权益表中储存的证券代码为转换后的证券代码，convertRule.xml文件中的原始代码以‘08’打头的配股缴款数据转换后的证券代码是以'00'打头的数据
            if (YssFun.left(zqdm, 2).equals("08")) {
                convertedZqdm = "00" + YssFun.right(zqdm, 4) + " CS";
            }
            //add by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
            if(YssFun.left(zqdm, 2).equals("38")){
            	convertedZqdm = "30" + YssFun.right(zqdm, 4) + " CS";
            }
            //add by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB

            strSql = " select * from " + pub.yssGetTableName("Tb_Data_RightsIssue") +
                //edit by songjie 2010.03.12 MS00901 QDII4.1赢时胜上海2010年03月05日01_B    
                " where FTSecurityCode = " + dbl.sqlString(convertedZqdm) + " and FCheckState = 1 ";
            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                recordDate = rs.getDate("FRecordDate"); //登记日
                exRightDate = rs.getDate("FExRightDate"); //除权日
                expirationDate = rs.getDate("FExpirationDate"); //缴款截止日

                //若数据日期 >= 登记日
                if (fDate.after(recordDate) || fDate.equals(recordDate)) {
                    isPGJK = true;
                    break;
                }
            }

            if (isPGJK) {
                if (exRightDate.after(expirationDate) || exRightDate.equals(expirationDate)) { //若除权日大于等于缴款截止日
                    if (seatType.equals("INDEX") && ( (String) hmSubAssetType.get(portCode)).equals("0102")) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                    	return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK_ZS"; //权益 配股缴款--指数股
                    } else if (seatType.equals("INDEX") && ( (String) hmSubAssetType.get(portCode)).equals("0103")) {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                    	return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK_ZB"; //权益 配股缴款—指标股
                    } else {
                    	//edit by songjie 2010.03.10 MS00904 QDII4.1赢时胜上海2010年03月10日01_AB
                    	return YssFun.left(zqdm, 2) + "****\t" + "QY PGJK"; //权益 配股缴款--普通
                    }
                }
            }

            return "";
        } catch (Exception e) {
            throw new YssException("根据参数判断配股缴款的业务子类型出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    

}

