package com.yss.main.operdeal.datainterface.cnstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.main.operdeal.datainterface.cnstock.pojo.ReadTypeBean;
import com.yss.main.operdeal.datainterface.cnstock.shstock.*;
import com.yss.main.operdeal.datainterface.cnstock.szstock.*;
import com.yss.util.YssException;
import java.sql.ResultSet;
import java.util.HashMap;
import com.yss.main.datainterface.cnstock.BrokerRateBean;
import com.yss.main.datainterface.cnstock.CNInterfaceParamAdmin;
import com.yss.main.datainterface.cnstock.RateSpeciesTypeBean;
import java.util.ArrayList;
import java.util.Iterator;
import com.yss.util.YssFun;
import com.yss.vsub.YssFinance;
import com.yss.main.syssetting.DataDictBean;
import com.yss.main.voucher.VchAssistantSettingBean;
import com.yss.main.voucher.VchPortSetLinkBean;

import java.sql.PreparedStatement;
import java.sql.Connection;

/**
 * QDII国内：QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 国内接口处理
 * add by songjie
 * 2009-05-15
 * 用于处理国内接口数据从临时表到债券信息表的数据转换
 */
public class CtlStock
    extends DataBase {
    HashMap hmSecsInfo = null;
    HashMap hmMTVInfo = null;
    //add by songjie 2010.02.28 QDII4.1赢时胜上海2010年02月23日02_AB
    HashMap hmShowZqdms = new HashMap();
    //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
    ArrayList alShowZqdms = new ArrayList();
    //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
    ArrayList alShowHGZqdms = new ArrayList();
    /**
     * 构造函数
     */
    public CtlStock() {

    }

    /**
     *用于处理国内接口的数据
     */
    public void inertData() throws YssException {
        String strSql = null;//用于储存sql语句
        String assetGroupCode = ""; //组合群代码
        HashMap hmPortHolderSeat = null;//用于储存组合代码对应的股东代码和席位代码

        HashMap hmParam = new HashMap();//用于储存获取的参数设置数据或席位股东数据
        ArrayList alInterfaceCode = null;//用于储存已导入的自定义接口代码
        //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
        HashMap hmShowZqdm = null;
        //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
        ArrayList alShowZqdm = null;
        //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
        ArrayList alShowHGZqdm = null;
        HashMap hmCatSub = null;
        try {
            this.sPort = dealPortCodes();//将重复的组合代码处理成唯一的组合代码

            assetGroupCode = pub.getAssetGroupCode();//组合群代码

            CNInterfaceParamAdmin interfaceParam = new CNInterfaceParamAdmin(); //新建CNInterfaceParamAdmin
            interfaceParam.setYssPub(pub);

            //获取数据接口参数设置的读书处理方式界面设置的参数对应的HashMap
            hmReadType = (HashMap) interfaceParam.getReadTypeBean();

            //获取数据接口参数设置的交易所债券参数设置界面设置的参数对应的HashMap
            hmExchangeBond = (HashMap) interfaceParam.getExchangeBondBean();

            //获取数据接口参数设置的交易费用计算方式界面设置的参数对应的HashMap
            hmTradeFee = (HashMap) interfaceParam.getTradeFeeBean();

            //获取数据接口参数设置的费用承担方向界面设置的参数对应的HashMap
            hmFeeWay = (HashMap) interfaceParam.getFeeWayBean();

            RateSpeciesTypeBean rateSpeciesType = new RateSpeciesTypeBean();
            rateSpeciesType.setYssPub(pub);

            //获取交易费率品种设置界面设置的费率对应的HashMap
            //edit by songjie 2010.03.22 MS00924 QDV4赢时胜（测试）2010年03月19日02_B
            hmRateSpeciesType = (HashMap) rateSpeciesType.getRateSpeciesTypeBean(this.sDate);

            BrokerRateBean brokerRate = new BrokerRateBean();
            brokerRate.setYssPub(pub);

            //获取券商佣金利率设置界面设置的券商佣金利率对应的HashMap
            //edit by songjie 2010.03.22 MS00924 QDV4赢时胜（测试）2010年03月19日02_B
            hmBrokerRate = (HashMap) brokerRate.getBrokerReateBean(this.sDate);

            //获取所有已选组合代码对应的股东和席位代码对应的HashMap
            hmPortHolderSeat = getPStockHolderAndSeat(this.sPort);//获取已选组合对应的股东代码和席位代码

            hmMTVInfo = super.getMTVSelInfo();

            hmParam.put("hmReadType", hmReadType);
            hmParam.put("hmExchangeBond", hmExchangeBond);
            hmParam.put("hmTradeFee", hmTradeFee);
            hmParam.put("hmFeeWay", hmFeeWay);
            hmParam.put("hmRateSpeciesType", hmRateSpeciesType);
            hmParam.put("hmBrokerRate", hmBrokerRate);
            hmParam.put("hmPortHolderSeat", hmPortHolderSeat);
            hmParam.put("hmMTVInfo",hmMTVInfo);

            createTmpTable();//判断数据库中是否有相关的临时表，若没有，则创建临时表

            //judgeBusinessDate();//用于判断临时表中的成交日期是否等于系统读数日期，若不等于，则不执行相应库的预处理

            alInterfaceCode = SuccessInPutBean.alInterfaceCode;//获取已导入数据的自定义接口代码

            if(alInterfaceCode != null &&
               ((alInterfaceCode.contains("SH_zqbd_imp")) ||
                (alInterfaceCode.contains("SH_tmpgh_IMP")) ||
                (alInterfaceCode.contains("SH_tmpdgh_IMP")) ||  //上海B股大宗交易过户库  panjunfang add 20100426
                (alInterfaceCode.contains("SZ_sjsgf_IMP")) ||
                (alInterfaceCode.contains("SZ_sjsfx_IMP")) ||
                (alInterfaceCode.contains("SZ_sjshb_IMP")))){
            	
            	//edit by songjie 2012.01.13 BUG 3643 QDV4赢时胜(测试)2012年01月13日02_B
            	String jkdm = getDealType(alInterfaceCode);
            	 
                //在交易接口明细表中删除相关业务日期和已选组合代码的数据
                strSql = " delete from " + pub.yssGetTableName("Tb_HzJkMx") + " where FInDate = " +
                    dbl.sqlDate(this.sDate) + " and FPortCode in(" + operSql.sqlCodes(this.sPort) + ")" +
                    //edit by songjie 2011.12.09 BUG 3358 QDV4农业银行2011年12月08日01_B 根据导入的国内接口删除数据
                    ((jkdm.trim().length() >0) ? " and FJKDM in( " + operSql.sqlCodes(jkdm) + ")" : "");
                dbl.executeSql(strSql);
            }

            if(alInterfaceCode != null && alInterfaceCode.contains("SH_zqbd_imp")){
                //上海证券变动库
                SHZQBDBean shzqbd = new SHZQBDBean();
                shzqbd.setYssPub(pub);
                shzqbd.setAssetGroupCode(assetGroupCode);
                shzqbd.setDataBase(this);
                //将上海证券变动库的数据处理到交易接口明细表中
                //edit by songjie 2010.12.31 BUG:711 南方东英2010年12月16日01_B 
                //修改了inertData方法，加参数HashMap hmParam
                shzqbd.inertData(hmParam);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                hmShowZqdm = shzqbd.getHmShowZqdm();
                dealShowZqdm(hmShowZqdm);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
            }

            if(alInterfaceCode != null && (alInterfaceCode.contains("SH_tmpgh_IMP") || alInterfaceCode.contains("SH_tmpdgh_IMP"))){
                //上海过户库
                SHGHBean shgh = new SHGHBean();
                shgh.setYssPub(pub);
                shgh.setDataBase(this);
                //将上海过户库的数据处理到交易接口明细表中
                shgh.makeData(this.sDate, this.sPort, this.checkState, hmParam);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                hmShowZqdm = shgh.getHmShowZqdm();
                dealShowZqdm(hmShowZqdm);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                
                //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
                alShowZqdm = shgh.getAlShowZqdm();
                dealAlShowZqdm(alShowZqdm);
                //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
                
                //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
                alShowHGZqdm = shgh.getAlShowHGZqdm();
                dealAlShowHGZqdm(alShowHGZqdm);
                //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
            }
            
            //---add by songjie 2012.05.09 STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A start---//
            if(alInterfaceCode != null && alInterfaceCode.contains("JSMX_IMP")){
                //上海结算明细库
                SHJSMXBean shjsmx = new SHJSMXBean();
                shjsmx.setYssPub(pub);
                shjsmx.setDataBase(this);
                //将上海过户库的数据处理到交易接口明细表中
                shjsmx.makeData(this.sDate, this.sPort, this.checkState, hmParam);

                alShowZqdm = shjsmx.getAlShowZqdm();
                dealAlShowZqdm(alShowZqdm);  
            }
            //---add by songjie 2012.05.09 STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A end---//

            if(alInterfaceCode != null && alInterfaceCode.contains("SZ_sjsgf_IMP")){
                //深圳股份库
                SZGFBean szGf = new SZGFBean();
                szGf.setYssPub(pub);
                szGf.setAssetGroupCode(assetGroupCode);
                szGf.setHMHolderSeat(hmPortHolderSeat);
                szGf.setDataBase(this);
                //将深圳股份库的数据处理到交易接口明细表中
                //edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                szGf.inertData(hmParam);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                hmShowZqdm = szGf.getHmShowZqdm();
                dealShowZqdm(hmShowZqdm);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
            }

            if(alInterfaceCode != null && alInterfaceCode.contains("SZ_sjsfx_IMP")){
                //深圳发行库
                SZFXBean szFx = new SZFXBean();
                szFx.setYssPub(pub);
                szFx.setAssetGroupCode(assetGroupCode);
                szFx.setHMHolderSeat(hmPortHolderSeat);
                szFx.setDataBase(this);
                //将深圳发行库的数据处理到交易接口明细表中
                //edit by songjie 2010.02.24 MS00879 QDII4.1赢时胜上海2010年02月10日02_AB
                szFx.inertData(hmParam);
            }

            if(alInterfaceCode != null && alInterfaceCode.contains("SZ_sjshb_IMP")){
                //深圳回报库
                SZHBBean szhb = new SZHBBean();
                szhb.setYssPub(pub);
                szhb.setDataBase(this);
                //将深圳回报库的数据处理到交易接口明细表中
                szhb.makeData(this.sDate, this.sPort, this.checkState, hmParam);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
                hmShowZqdm = szhb.getHmShowZqdm();
                dealShowZqdm(hmShowZqdm);
                //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
            
                //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
                alShowZqdm = szhb.getAlShowZqdm();
                dealAlShowZqdm(alShowZqdm);
                //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
                
                //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
                alShowHGZqdm = szhb.getAlShowHGZqdm();
                dealAlShowHGZqdm(alShowHGZqdm);
                //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB 
            }
            
            hmCatSub = hzJkMxToParaSecurity(); //将证券信息设置中没有的，交易接口明细库的证券数据储存到证券信息设置表中
            insertIntoMTVLink(); //将深圳发行库,深圳股份库，深圳发行库，上海过户库的证券数据根据估值方法筛选条件插入到估值方法链接表中
            insertIntoAUXIACCSET(hmCatSub);//add by songjie 2010.02.23 插入财务系统的辅助核算表中国内业务相关证券代码及名称

            //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
            if(hmShowZqdms.size() > 0){
            	showUnInsertSecInfo();//提示客户设置相关的权益数据
            }
            //add by songjie 2010.02.28 MS00889 QDII4.1赢时胜上海2010年02月23日02_AB
            
            //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
            if(alShowZqdms.size() > 0){
            	showUnInsertZQInfo();//提示客户维护相关证券的债券信息
            }
            //add by songjie 2010.03.22 MS00925 QDV4赢时胜（测试）2010年03月19日03_AB
            
            //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
            if(alShowHGZqdms.size() > 0){
            	showUnInsertHGInfo();//提示客户维护相关证券的债券信息
            }    
            //add by songjie 2010.03.27 MS00946 QDV4赢时胜（测试）2010年03月25日11_AB
            
            if(alInterfaceCode != null &&
               (alInterfaceCode.contains("SH_zqbd_imp") ||
                alInterfaceCode.contains("SH_tmpgh_IMP") ||
                alInterfaceCode.contains("SH_tmpdgh_IMP") ||  //上海B股大宗交易过户库 panjunfang add 20100426
                alInterfaceCode.contains("SZ_sjsgf_IMP") ||
                alInterfaceCode.contains("SZ_sjsfx_IMP") ||
                alInterfaceCode.contains("SZ_sjshb_IMP") ||
                alInterfaceCode.contains("JSMX_IMP"))){//edit by songjie 2012.05.09 STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A
                //从交易接口明细库到交易接口清算库的数据处理
                MXToQSBean mxToQs = new MXToQSBean();
                mxToQs.setYssPub(pub);
                
                hmParam.put("dealDataType", getDealType(alInterfaceCode));//add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更	
                mxToQs.makeData(this.sDate, this.sPort, hmParam);

                // 从交易接口清算库到交易子表的数据处理
                QSToTradeDetailBean trade = new QSToTradeDetailBean();
                trade.setYssPub(pub);
                trade.setAssetGroupCode(assetGroupCode);
                trade.setHMHolderSeat(hmPortHolderSeat);
                trade.setCheckState(checkState);
                trade.setDataBase(this);
                trade.inertData(hmParam);
            }
        } catch (Exception e) {
        	if (e instanceof YssException){
        		throw new YssException(e.getMessage());//edit by yanghaiming 20100819
        	}else{
        		throw new YssException("国内接口导入出错", e);
        	}
        }
    }

  /**
   * //add by lidaolong 20110330 #536 有关国内接口数据处理顺序的变更
   * 判断用户选择了什么库,那么就处理相应的数据到交易数据表中
   * @param alInterfaceCode
   * @return
   */
    private String getDealType(ArrayList alInterfaceCode){
    	String strType="";
    	//上海证券变动库
    	if (alInterfaceCode.contains("SH_zqbd_imp")){
    		strType +="SHZQBD,";
    	}
    	
    	//上海过户库
    	if (alInterfaceCode.contains("SH_tmpgh_IMP") || alInterfaceCode.contains("SH_tmpdgh_IMP")){
    		strType +="SHGH,";
    	}
    	
    	//上海结算明细库
    	if (alInterfaceCode.contains("JSMX_IMP")){
    		strType +="SHJSMX,";
    	}
    	
    	//深圳股份库
    	if (alInterfaceCode.contains("SZ_sjsgf_IMP")){
    		strType +="SZGF,";
    	}
    	
    	//深圳发行库
    	if (alInterfaceCode.contains("SZ_sjsfx_IMP")){
    		strType +="SZFX,";
    	}
    	
    	//深圳回报库
    	if (alInterfaceCode.contains("SZ_sjshb_IMP")){
    		strType +="SZHB,";
    	}
    	
    	if (strType.length() > 0){
    		strType = strType.substring(0, strType.length()-1);
    	}
    	
    	return strType;
    }
    /**
     * 用于处理已选的组合代码，若已选的组合代码类型为'001,001,002',则处理后的已选组合代码为'001,002'
     * 将重复的组合代码处理成唯一的组合代码
     * @return String
     */
    private String dealPortCodes(){
        String[] splitPorts = null;
        ArrayList alPorts = new ArrayList();
        String selectedPorts = "";

        splitPorts = this.sPort.split(",");

        for(int i = 0; i <splitPorts.length; i++){
            if(!alPorts.contains(splitPorts[i])){
                alPorts.add(splitPorts[i]);
            }
        }

        Iterator iterator = alPorts.iterator();
        while(iterator.hasNext()){
            selectedPorts += (String)iterator.next() + ",";
        }

        if(selectedPorts.length() >= 1){
            selectedPorts = selectedPorts.substring(0, selectedPorts.length() - 1);
            }
        return selectedPorts;
    }

    /**
     * 用于获取组合代码对应的股东代码和席位代码对应的Hashmap
     * key-组合代码， value-席位代码 + "\t" + 股东代码
     * @param portCodes String
     * @return HashMap
     * @throws YssException
     */
    public HashMap getPStockHolderAndSeat(String portCodes) throws YssException {
        String strSql = ""; //储存sql语句
        String tradeSeats = ""; //储存席位代码
        String stockHolders = ""; //储存股东代码
        String TSInfo = ""; //储存席位代码和股东代码
        ResultSet rs = null; //声明结果集
        HashMap hmHolderSeat = new HashMap(); //用于储存组合代码对应的席位代码和股东代码数据
        String[] portcodes = portCodes.split(","); //拆分后的组合代码
        ArrayList alTradeSeat = new ArrayList();
        ArrayList alStockHolder = new ArrayList();
        try {
            for (int i = 0; i < portcodes.length; i++) { //循环组合代码
//                strSql = "select FSubCode,FReLaType from "+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") +
//                    " where FPortCode = " + dbl.sqlString(portcodes[i]) +
//                    " and FRelaType in('TradeSeat','Stockholder')"; //根据组合代码查找相应的席位代码和股东代码
//
//                rs = dbl.openResultSet(strSql);
//                while (rs.next()) {
//                    if (rs.getString("FRelaType").equals("TradeSeat")) { //若为席位类型数据
//                        if(!alTradeSeat.contains(rs.getString("FSubCode"))){
//                            alTradeSeat.add(rs.getString("FSubCode"));
//                            tradeSeats += rs.getString("FSubCode") + ","; //获取席位代码
//                        }
//                    }
//                    else { //若为股东类型数据
//                        if(!alStockHolder.contains(rs.getString("FSubCode"))){
//                            alStockHolder.add(rs.getString("FSubCode"));
//                            stockHolders += rs.getString("FSubCode") + ","; //获取股东代码
//                        }
//                    }
//                }
                
				strSql = "select FSubCode from "
						+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
						+ " where FPortCode = " + dbl.sqlString(portcodes[i])
						+ " and FRelaType = 'Stockholder' and FCheckState = 1 ";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (!alStockHolder.contains(rs.getString("FSubCode"))) {
						alStockHolder.add(rs.getString("FSubCode"));
						stockHolders += rs.getString("FSubCode") + ","; // 拼接股东代码数据
					}
				}
				dbl.closeResultSetFinal(rs);
				// edit by yanghaiming 20100610 MS01257
				// QDV4赢时胜(上海)2010年5月26日05_B 这里改取席位号
//				strSql = "select distinct b.fseatnum as fsubcode from "
//						+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
//						+ " a left join (select * from "
//						+ pub.yssGetTableName("tb_para_tradeseat")
//						+ ") b on a.fsubcode = b.fseatcode"
//						+ " where a.FPortCode = " + dbl.sqlString(portcodes[i])
//						+ " and a.FRelaType = 'TradeSeat' and a.FCheckState = 1 ";
				
				//---------edit by songjie 2010.06.12-------------//
				strSql = "select distinct FSeatNum from " + pub.yssGetTableName("tb_para_tradeseat") + 
				         " where FSeatCode in (select a.FSubCode from " + pub.yssGetTableName("Tb_Para_Portfolio_RelaShip") + 
				         " a where FRelaType = 'TradeSeat' and FPortCode = " + dbl.sqlString(portcodes[i]) + " and FCheckState = 1) and FCheckState = 1";
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					if (!alTradeSeat.contains(rs.getString("FSeatNum"))) {
						alTradeSeat.add(rs.getString("FSeatNum"));
						tradeSeats += rs.getString("FSeatNum") + ",";// 拼接席位代码数据
//						---------edit by songjie 2010.06.12-------------//
					}
				}

                if (tradeSeats.indexOf(",") != -1) {
                    tradeSeats = tradeSeats.substring(0, tradeSeats.length() - 1); //将最后的逗号去掉
                }
                else{
                    throw new YssException(" 请在组合设置中设置组合代码" + portcodes[i] + "对应的席位代码！");
                }

                if (stockHolders.indexOf(",") != -1) {
                    stockHolders = stockHolders.substring(0, stockHolders.length() - 1); //将最后的逗号去掉
                }
                else{
                    throw new YssException(" 请在组合设置中设置组合代码" + portcodes[i] + "对应的股东代码！");
                }

                TSInfo = tradeSeats + "\t" + stockHolders; //拼接席位代码数据和股东代码数据
                //储存到HashMap中，key--组合代码 value--席位代码数据和股东代码数据
                hmHolderSeat.put(portcodes[i], TSInfo);
            }
            return hmHolderSeat;
        } catch (Exception e) {
            throw new YssException("获取组合代码对应的股东代码和席位代码数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 判断数据库中是否有相关的临时表，若没有，则创建临时表
     * @throws YssException
     */
    private void createTmpTable()throws YssException{
        DataDictBean dataDict = null;//声明数据字典实例
        DataDictBean subDict = null;//声明数据字典实例
        try{
            if (!dbl.yssTableExist("SHGH")) {
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("SHGH".trim());//从数据字典表中取表名为SHGH的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("SHGH".trim());
                }
            }

            if(!dbl.yssTableExist("SZHB")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("SZHB".trim());//从数据字典表中取表名为SZHB的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("SZHB".trim());
                }
            }

            if(!dbl.yssTableExist("SHZQBD")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("SHZQBD".trim());//从数据字典表中取表名为SHZQBD的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("SHZQBD".trim());
                }
            }

            if(!dbl.yssTableExist("SZGF")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("SZGF".trim());//从数据字典表中取表名为SZGF的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("SZGF".trim());
                }
            }

            if(!dbl.yssTableExist("SZFX")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("SZFX".trim());//从数据字典表中取表名为SZFX的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("SZFX".trim());
                }
            }

            if(!dbl.yssTableExist("tmpSH_gh")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("tmpSH_gh".trim());//从数据字典表中取表名为tmpSH_gh的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("tmpSH_gh".trim());
                }
            }

            if(!dbl.yssTableExist("tmp_sjshb")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("tmp_sjshb".trim());//从数据字典表中取表名为tmp_sjshb的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("tmp_sjshb".trim());
                }
            }

            if(!dbl.yssTableExist("TMP_ZQBD")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("TMP_ZQBD".trim());//从数据字典表中取表名为TMP_ZQBD的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("TMP_ZQBD".trim());
                }
            }

            if(!dbl.yssTableExist("tmp_sjsFX")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("tmp_sjsFX".trim());//从数据字典表中取表名为tmp_sjsFX的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("tmp_sjsFX".trim());
                }
            }

            if(!dbl.yssTableExist("tmp_sjsgf")){
                dataDict = new DataDictBean();//新建实例
                dataDict.setYssPub(pub);
                dataDict.getTableInfo("tmp_sjsgf".trim());//从数据字典表中取表名为tmp_sjsgf的数据

                subDict = new DataDictBean();//新建实例
                subDict.setYssPub(pub);
                String[] lastInfo = dataDict.getSsubData().split("\f\f");//拆分获取的表结构数据
                subDict.protocolParse(lastInfo[lastInfo.length - 1]);
                if (subDict.getStrTableType().equalsIgnoreCase("1")) { //若为临时表,则建表.
                    dataDict.createTab("tmp_sjsgf".trim());
                }
            }
        }
        catch(Exception e){
            throw new YssException("创建临时表出错", e);
        }
    }

    /**
     * 将SHGH表数据插入到证券信息设置表
     * 条件为：若证券信息设置表中不包含SHGH表中的证券代码，则将相关证券信息插入到证券信息设置表中
     */
    public HashMap hzJkMxToParaSecurity() throws YssException {
        String strSql = null; //用于储存sql语句
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        PreparedStatement pstmt = null; //新建PreparedStatement

        String zqdm = null; //声明证券代码
        String szsh = null;//交易所代码
        String businessSign = null; //业务标志
        String securitySign = null; //证券标志

        String catInfo = null;
        String categoryCode = null; //品种类型
        String subCategoryCode = null; //品种子类型

        ArrayList alZqdm = new ArrayList();

        ArrayList alSecsInfo = null;
        String key = null; //用于获取品种类型 + 品种子类型 + 交易所代码
        ArrayList alKey = new ArrayList();
        ArrayList alSecs = new ArrayList();
        HashMap hmSec = null;//add by songjie 2010.02.23
        ReadTypeBean readType = null;//add by songjie 2010.02.24
        HashMap hmCatSub = new HashMap();//add by songjie 2010.03.05
        try {
        	hmSec = getSecurityName();//add by songjie 2010.02.23 在行情的临时表中获取证券代码和证券名称
            con.setAutoCommit(false); //开启事务
            bTrans = true;

            SHGHBean shgh = new SHGHBean();
            shgh.setYssPub(pub);

            SZHBBean szhb = new SZHBBean();
            szhb.setYssPub(pub);

            strSql = " insert into " + pub.yssGetTableName("Tb_Para_Security") + "(FSecurityCode, FSecurityName, FExchangeCode, FCatCode, " +
                " FSubCatCode, FISINCode, FExternalCode, FTradeCury, FSettleDayType, FHolidaysCode, FSettleDays, FTotalShare, " +
                " FCurrentShare, FHandAmount, FFactor, FStartDate, FMarketCode, FCheckState, FCreator, FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; //将交易接口明细库中
            pstmt = dbl.openPreparedStatement(strSql);

            strSql = " delete from " + pub.yssGetTableName("Tb_Para_Security") + 
            " where FCheckState <> 1 and FSecurityCode in (select distinct FZQDM from " + 
            pub.yssGetTableName("Tb_HzJkMx") + ") ";
            
            dbl.executeSql(strSql);
            
            strSql = " select * from " + pub.yssGetTableName("Tb_HzJkMx") + " where Fzqdm not in " +
                "(select distinct FSecurityCode from " + pub.yssGetTableName("Tb_Para_Security") +
                ") and FInDate = " + dbl.sqlDate(this.sDate);
            rs = dbl.openResultSet(strSql); //在交易接口明细库中查找系统读数日期当天的深圳的非证券信息设置表中的证券的数据

            hmSecsInfo = new HashMap();

            while (rs.next()) {
                zqdm = rs.getString("FZQDM"); //证券代码
                businessSign = rs.getString("FYwbz"); //业务标志
                securitySign = rs.getString("FZqbz"); //证券标志
                szsh = rs.getString("FSzSh");//交易所代码

                //add by songjie 2010.02.24
                readType = (ReadTypeBean)hmReadType.
                get(pub.getAssetGroupCode() + " " + rs.getString("FPortCode"));
                
                if (!alZqdm.contains(zqdm)) { //若alZqdm中不包含本证券代码
                    alZqdm.add(zqdm); //则将证券代码添加到alZqdm中
                } else { //若alZqdm中包含本证券代码
                    continue; //则执行下一个循环
                }

                //根据证券标志和业务标志判断品种类型和品种子类型
                if(szsh.equals("CG")){
                    catInfo = shgh.judgeCatAndSubCat(securitySign, businessSign);
                }
                if(szsh.equals("CS")){
                    catInfo = szhb.judgeCatAndSubCat(securitySign, businessSign);
                }

                categoryCode = catInfo.split("\t")[0]; //品种类型
                subCategoryCode = catInfo.split("\t")[1]; //品种子类型

                if(szsh.equals("CS") && zqdm.startsWith("3") && securitySign.equals("XG")){
                	subCategoryCode = "EQ07";//创业板股票
                }
                

                //获取品种类型代码 + 品种子类型代码 + 交易所代码 = hmSecsInfo的键
                key = categoryCode + "\t" + subCategoryCode + "\t" + szsh;

                if(hmCatSub.get(zqdm) == null){
                	hmCatSub.put(zqdm, categoryCode);
                }
                
                if (!alKey.contains(key)) {
                    alKey.add(key); //用于获取所有符合条件的hmSecsInfo的键
                }

                if (!alSecs.contains(zqdm)) {
                    alSecs.add(zqdm); //用于获取所有符合条件的证券代码
                }

                alSecsInfo = (ArrayList) hmSecsInfo.get(key);

                if (alSecsInfo == null) {
                    alSecsInfo = new ArrayList();
                    alSecsInfo.add(zqdm);
                    hmSecsInfo.put(key, alSecsInfo);
                } else {
                    if (!alSecsInfo.contains(zqdm)) {
                        alSecsInfo.add(zqdm);
                        hmSecsInfo.put(key, alSecsInfo);
                    }
                }

                pstmt.setString(1, zqdm); //证券代码
                //edit by songjie 2010.02.23
                if(hmSec != null && hmSec.get(YssFun.left(zqdm, 6)) != null){
                	pstmt.setString(2, (String)hmSec.get(YssFun.left(zqdm, 6))); //证券名称
                }else{
                	pstmt.setString(2, zqdm); //证券名称
                }
                //edit by songjie 2010.02.23
                pstmt.setString(3, szsh); //深交所
                pstmt.setString(4, categoryCode); //品种类型
                pstmt.setString(5, subCategoryCode); //品种子类型
                pstmt.setString(6, " "); //内部代码
                pstmt.setString(7, " "); //外部代码
                pstmt.setInt(9, 0); //结算日期类型
                //edit by songjie 2010.02.24
                
                if(securitySign.equals("B_GP") && szsh.equals("CG")){// 上海B股   panjunfang modify 20100421
                	if(readType.getCurrencyCodeSHB() == null){
                		throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的上海B股交易币种！");
                	}
                	pstmt.setString(8, readType.getCurrencyCodeSHB()); //交易币种
                	if(readType.getHolidaysCodeSH() == null){
                		throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的上海B股交收关联节假日群！");
                	}
                	pstmt.setString(10, readType.getHolidaysCodeSH()); //节假日代码
                	pstmt.setInt(11, readType.getDelayDateB()); //延迟天数
                }else if(securitySign.equals("B_GP") && szsh.equals("CS")){// 深交B股   panjunfang modify 20100421
                	if(readType.getCurrencyCodeSZB() == null){
                		throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的深圳B股交易币种！");
                	}
                	pstmt.setString(8, readType.getCurrencyCodeSZB()); //交易币种
                	if(readType.getHolidaysCodeSZ() == null){
                		throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的深圳B股交收关联节假日群！");
                	}
                	pstmt.setString(10, readType.getHolidaysCodeSZ()); //节假日代码
                	pstmt.setInt(11, readType.getDelayDateB()); //延迟天数
                }else{//国内业务A股对应的交易币种和延迟天数均通过参数来设置  panjunfang modify 20100421
                	if(readType.getCurrencyCodeA() == null){
                		throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的A股交易币种！");
                	}
                    pstmt.setString(8, readType.getCurrencyCodeA()); //交易币种   
                    if(readType.getHolidaysCode() == null){
                    	throw new YssException("请在【数据接口参数设置】的读数处理方式分页中设置已选组合的节假日群！");
                    }
                    pstmt.setString(10, readType.getHolidaysCode()); //节假日代码
                    pstmt.setInt(11, readType.getDelayDateA()); //延迟天数
                }
                pstmt.setDouble(12, 0);
                pstmt.setDouble(13, 0);
                pstmt.setDouble(14, 1);
                pstmt.setDouble(15, 1);
                pstmt.setDate(16, YssFun.toSqlDate("1900-01-01"));
                //edit by songjie 2010.06.10 MS01214 QDV4国内（测试）2010年05月28日01_B
                pstmt.setString(17,YssFun.left(zqdm, 6));
                if (this.checkState.equalsIgnoreCase("True")) { //审核状态
                    pstmt.setInt(18, 1);
                } else {
                    pstmt.setInt(18, 0);
                }
                pstmt.setString(19, pub.getUserCode()); //用户代码
                pstmt.setString(20, YssFun.formatDatetime(new java.util.Date())); //创建时间
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            if (alKey.size() != 0) {
                hmSecsInfo.put("key", alKey);
                hmSecsInfo.put("secCodes", alSecs);
            }

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置为自动提交
            
            return hmCatSub;
        } catch (Exception e) {
            throw new YssException("追加证券信息到系统证券信息设置中出错！", e);
        } finally {
            dbl.endTransFinal(con, bTrans);
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(pstmt);
        }
    }

    /**
     * 将深圳回报库的证券数据根据估值方法筛选条件插入到估值方法链接表中
     * @throws YssException
     */
    public void insertIntoMTVLink() throws YssException {
        ArrayList alMtvKey = null;
        ArrayList alSecKey = null;
        ArrayList alMtvCodes = null;
        ArrayList alSecCodes = null;
        ArrayList alMtvInfo = null;
        ArrayList alSecInfo = null;

        String mtvKey = null;
        String[] splitMtvKey = null;

        String secKey = null;

        String catCode = "";
        String subCatCode = "";
        String exchangeCode = "";

        HashMap hmInsertInfo = new HashMap();
        HashMap hmInsertInfos = null;

        Iterator secIterator = null;
        Iterator mtvIterator = null;
        Iterator keyIterator = null;
        Iterator mtvIter = null;
        Iterator secIter = null;
        Iterator iterator = null;
        String keys = "";

        String mtvCode = null;
        String secCode = null;

        ArrayList insertKey = new ArrayList();
        String insertKeys = null;
        String strSql = null; //用于储存sql语句
        ArrayList uniqueKey = new ArrayList();

        Connection conn = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        PreparedStatement pstmt = null; //声明PreparedStatement
        ResultSet rs = null; //声明ResultSet
        try {
            //若hmMTVInfo为空或hmMTVInfo没有数据或hmSecsInfo为空或hmSecsInfo没有数据则返回
            if (hmMTVInfo == null || hmMTVInfo.size() == 0 || hmSecsInfo == null || hmSecsInfo.size() == 0) {
                return;
            }

            //取出hmMTVInfo中的储存所有(品种类型代码 + 品种子类型代码 + 交易所代码)的列表
            alMtvKey = (ArrayList) hmMTVInfo.get("key");

            //取出hmMTVInfo中储存所有的估值方法代码的列表
            alMtvCodes = (ArrayList) hmMTVInfo.get("mtvCodes");

            if (alMtvCodes != null) { //若储存估值方法代码的列表不为空
                mtvIter = alMtvCodes.iterator(); //获取迭代器
                mtvCode = ""; //初始化储存估值方法代码的字符串
                while (mtvIter.hasNext()) {
                    //将所有的估值方法代码用逗号隔开储存到mtvCode中
                    mtvCode += (String) mtvIter.next() + ",";
                }
                if (mtvCode.length() >= 1) {
                    mtvCode = mtvCode.substring(0, mtvCode.length() - 1); //去掉mtvCode最后的逗号
                }
            }

            //获取hmSecsInfo中的储存所有(品种类型代码 + 品种子类型代码 + 交易所代码)的列表
            alSecKey = (ArrayList) hmSecsInfo.get("key");

            //取出hmSecsInfo中储存所有的证券代码的列表
            alSecCodes = (ArrayList) hmSecsInfo.get("secCodes");

            if (alSecCodes != null) { //若储存证券代码的列表不为空
                secIter = alSecCodes.iterator(); //获取迭代器
                secCode = ""; //初始化储存证券代码的字符串
                while (secIter.hasNext()) {
                    //将所有的证券代码用逗号隔开储存到secCode中
                    secCode += (String) secIter.next() + ",";
                }
                if (secCode.length() >= 1) {
                    secCode = secCode.substring(0, secCode.length() - 1); //去掉secCode最后的逗号
                }
            }

            //查询估值方法链接表中相关估值方法代码和证券代码的数据
            strSql = " select distinct FMtvCode, FLinkCode, FStartDate from " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                " where FMtvCode in(" + operSql.sqlCodes(mtvCode) + ") and FLinkCode in(" +
                operSql.sqlCodes(secCode) + ") and FStartDate = " + dbl.sqlDate("1901-01-01");

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                //若有相关的数据，则将相关数据的主键储存到uniqueKey中
                uniqueKey.add(rs.getString("FMtvCode") + " " + rs.getString("FLinkCode") + " " + rs.getDate("FStartDate").toString());
            }

            dbl.closeResultSetFinal(rs); //关闭结果集

            mtvIterator = alMtvKey.iterator(); //获取迭代器

            while (mtvIterator.hasNext()) {
                //获取品种类型代码 + 品种子类型代码 + 交易所代码 对应的键
                mtvKey = (String) mtvIterator.next();

                splitMtvKey = mtvKey.split("\t"); //拆分主键信息

                catCode = splitMtvKey[0]; //获取品种类型代码
                if(splitMtvKey.length >= 2){
                	subCatCode = splitMtvKey[1]; //获取品种子类型代码
                }
                if(splitMtvKey.length >= 3){
                exchangeCode = splitMtvKey[2]; //获取交易所代码
                }
                
                //若估值方法筛选条件中的品种类型代码，品种子类型代码，交易所代码都不为空
                if ((!subCatCode.equals("") && !subCatCode.equals("null")) && 
                		(!exchangeCode.equals("") && !exchangeCode.equals("null"))) {
                    //若证券信息中有符合估值方法筛选条件的数据
                    if (alSecKey.contains(mtvKey)) {
                        //根据估值方法筛选条件取出储存相应估值方法代码的列表
                        alMtvInfo = (ArrayList) hmMTVInfo.get(mtvKey);

                        //根据估值方法筛选条件取出储存相应证券代码的列表
                        alSecInfo = (ArrayList) hmSecsInfo.get(mtvKey);
                    }
                }

                //若估值方法筛选条件中的品种子类型代码为空
                if ((!subCatCode.equals("") && !subCatCode.equals("null")) && 
                		(exchangeCode.equals("") || exchangeCode.equals("null"))) {
                    //获取储存证券数据对应的筛选条件的列表的迭代器
                    secIterator = alSecKey.iterator();
                    while (secIterator.hasNext()) {
                        //获取证券数据对应的筛选条件数据
                        secKey = (String) secIterator.next();

                        //若证券数据中有对应估值方法中相关品种类型代码和品种子类型代码的数据
                        if (secKey.indexOf(catCode + "\t" + subCatCode) != -1) {

                            //根据估值方法筛选条件取出储存相应估值方法代码的列表
                            alMtvInfo = (ArrayList) hmMTVInfo.get(mtvKey);

                            //根据估值方法筛选条件取出储存相应证券代码的列表
                            alSecInfo = (ArrayList) hmSecsInfo.get(mtvKey);
							if (alSecInfo == null) {
								iterator = hmSecsInfo.keySet().iterator();
								while (iterator.hasNext()) {
									keys = (String) iterator.next();
									if (keys.startsWith(catCode) && keys.indexOf(subCatCode) != -1) {
										alSecInfo = (ArrayList) hmSecsInfo.get(keys);
										break;
									}
								}
							}
                        }
                    }
                }

                //若估值方法筛选条件中的交易所代码不为空 且品种子类型为空
                if ((subCatCode.equals("") || subCatCode.equals("null")) && 
                		(!exchangeCode.equals("") && !exchangeCode.equals("null"))) {
                    //获取储存证券数据对应的筛选条件的列表的迭代器
                    secIterator = alSecKey.iterator();
                    while (secIterator.hasNext()) {
                        //获取证券数据对应的筛选条件数据
                        secKey = (String) secIterator.next();

                        //若证券数据中有对应估值方法中相关品种类型代码和交易所代码的数据
                        if (secKey.indexOf(catCode) != -1 && secKey.indexOf(exchangeCode) != -1) {

                            //根据估值方法筛选条件取出储存相应估值方法代码的列表
                            alMtvInfo = (ArrayList) hmMTVInfo.get(mtvKey);

                            //根据估值方法筛选条件取出储存相应证券代码的列表
                            alSecInfo = (ArrayList) hmSecsInfo.get(mtvKey);
							if (alSecInfo == null) {
								iterator = hmSecsInfo.keySet().iterator();
								while (iterator.hasNext()) {
									keys = (String) iterator.next();
									if (keys.startsWith(catCode) && keys.endsWith(exchangeCode)) {
										alSecInfo = (ArrayList) hmSecsInfo.get(keys);
										break;
									}
								}
							}
                        }
                    }
                }

                //若估值方法筛选条件中的品种子类型代码，交易所代码都为空
                if ((subCatCode.equals("") || subCatCode.equals("null")) && 
                	(exchangeCode.equals("") || exchangeCode.equals("null"))) {

                    //获取储存证券数据对应的筛选条件的列表的迭代器
                    secIterator = alSecKey.iterator();
                    while (secIterator.hasNext()) {
                        //获取证券数据对应的筛选条件数据
                        secKey = (String) secIterator.next();

                        //若证券数据中有对应估值方法中相关品种类型代码和交易所代码的数据
                        if (secKey.indexOf(catCode) != -1) {

                            //根据估值方法筛选条件取出储存相应估值方法代码的列表
                            alMtvInfo = (ArrayList) hmMTVInfo.get(mtvKey);

                            //根据估值方法筛选条件取出储存相应证券代码的列表
                            alSecInfo = (ArrayList) hmSecsInfo.get(mtvKey);
                            
							if (alSecInfo == null) {
								iterator = hmSecsInfo.keySet().iterator();
								while (iterator.hasNext()) {
									keys = (String) iterator.next();
									if (keys.startsWith(catCode)) {
										alSecInfo = (ArrayList) hmSecsInfo.get(keys);
										break;
									}
								}
							}
                        }
                    }
                }

                //若根据估值方法筛选条件能查出对应的估值方法代码和证券代码
                if (alMtvInfo != null && alSecInfo != null) {
                    insertKey.add(mtvKey); //将估值方法筛选条件数据储存到insertKey中

                    hmInsertInfos = new HashMap(); //新建哈希表
                    hmInsertInfos.put("mtvCode", alMtvInfo); //储存估值方法代码对应的列表
                    hmInsertInfos.put("secCode", alSecInfo); //储存证券代码对应的列表

                    //储存估值方法筛选条件对应的估值方法代码和证券代码数据
                    hmInsertInfo.put(mtvKey, hmInsertInfos);
                }
            }

            conn.setAutoCommit(false); //开启事务
            bTrans = true;

            keyIterator = insertKey.iterator(); //获取迭代器

            while (keyIterator.hasNext()) {
                //获取估值方法筛选条件数据对应的key
                insertKeys = (String) keyIterator.next();

                //根据估值方法筛选条件
                hmInsertInfos = (HashMap) hmInsertInfo.get(insertKeys);

                //获取估值方法筛选条件对应的储存估值方法代码的列表
                alMtvInfo = (ArrayList) hmInsertInfos.get("mtvCode");

                //获取估值方法筛选条件对应的储存证券代码的列表
                alSecInfo = (ArrayList) hmInsertInfos.get("secCode");

                //给估值方法链接表插入相关数据
                strSql = " insert into " + pub.yssGetTableName("Tb_Para_MTVMethodLink") +
                    " (FMTVCode, FLinkCode, FStartDate, FCheckState, FCreator, FCreateTime) " +
                    " VALUES(?,?,?,?,?,?) ";

                pstmt = dbl.openPreparedStatement(strSql);
                
                //获取储存估值方法代码的列表的迭代器
                mtvIter = alMtvInfo.iterator();
                while (mtvIter.hasNext()) { //循环估值方法代码
                    //获取估值方法代码
                    mtvCode = (String) mtvIter.next();

                    //获取储存证券代码的列表的迭代器
                    secIter = alSecInfo.iterator();
                    while (secIter.hasNext()) { //循环证券代码
                        //获取证券代码
                        secCode = (String) secIter.next();

                        //判断在估值方法链接中是否有对应的证券代码，估值方法代码，启用日期的数据，若有，则执行下一个循环
                        if (uniqueKey.contains(mtvCode + " " + secCode + " 1901-01-01")) {
                            continue;
                        }

                        pstmt.setString(1, mtvCode); //估值方法代码
                        pstmt.setString(2, secCode); //证券代码
                        pstmt.setDate(3, YssFun.toSqlDate("1901-01-01")); //启用日期
                        pstmt.setInt(4, 1); //审核状态为已审核
                        pstmt.setString(5, pub.getUserCode()); //创建人
                        pstmt.setString(6, YssFun.formatDatetime(new java.util.Date())); //创建日期

                        pstmt.addBatch();
                    }
                }
            }

            if (pstmt != null) { //若pstmt不为空
                pstmt.executeBatch(); //则执行批处理操作
            }

            conn.commit(); //提交事务
            bTrans = false;
            conn.setAutoCommit(true); //设置为自动提交
        } catch (Exception e) {
            throw new YssException("将深圳回报库,深圳股份库，深圳发行库，上海过户库的相关证券数据插入到估值方法链接表出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pstmt);
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * add by songjie
     * 2010.02.23
     * 在上海行情的临时表sh_show2003，深圳行情的临时表tmp_sjshq中获取证券代码和证券名称
     */
    public HashMap getSecurityName()throws YssException{
    	String strSql = "";
    	ResultSet rs = null;
    	ResultSet rs1 = null;
    	ResultSet rs2 = null;
    	HashMap hmsec = null; 
    	boolean haveSHTable = false;
    	boolean haveSZTable = false;
    	try{
    		hmsec = new HashMap();
    		strSql = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString("sh_show2003") + ")";
    		rs2 = dbl.openResultSet(strSql);
    		while(rs2.next()){
    			haveSHTable = true;
    			break;
    		}
    		
    		dbl.closeResultSetFinal(rs2);
    		rs2 = null;
    		
    		strSql = "select * from user_col_comments where upper(table_name)=upper(" + dbl.sqlString("tmp_sjshq") + ")";
    		rs2 = dbl.openResultSet(strSql);
    		while(rs2.next()){
    			haveSZTable = true;
    			break;
    		}
    		
    		dbl.closeResultSetFinal(rs2);
    		rs2 = null;
    		
    		if(haveSHTable){
        		strSql = " select S1,S2 from sh_show2003 where S1 <> '000000' order by S1 ";
        		rs = dbl.openResultSet(strSql);
        		while(rs.next()){
        			hmsec.put(rs.getString("S1"),rs.getString("S2"));
        		}
    		}
    		
    		if(haveSZTable){
	    		strSql = " select HQZQDM, HQZQJC from tmp_sjshq where HQZQDM <> '000000' order by HQZQDM ";
	    		rs1 = dbl.openResultSet(strSql);
	    		while(rs1.next()){
	    			hmsec.put(rs1.getString("HQZQDM"),rs1.getString("HQZQJC"));
	    		}
    		}
    		
    		return hmsec;
    	}
    	catch(Exception e){
    		throw new YssException("查询行情数据中的证券代码和证券名称出错！", e);
    	}
    	finally{
    		dbl.closeResultSetFinal(rs,rs1,rs2);
    	}
    }
    
    /**
     * add by songjie
     * 2010.02.23
     * 插入财务系统的辅助核算表中
     * 国内业务相关证券代码及名称
     * @throws YssException
     */
    public void insertIntoAUXIACCSET(HashMap hmCatSub)throws YssException{
    	String strSql = "";
    	String YssTabPrefix = "";
    	int AccountingYear = 0;
    	ResultSet rs = null;
    	HashMap hmSec = null;
    	//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
//		Connection con = dbl.loadConnection(); // 新建连接
//		boolean bTrans = false;
//		PreparedStatement pstmt = null; //声明PreparedStatement
    	//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
		String bookCode = "";//套账代码
		String proCode = "";//项目代码
    	try{
    		//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
//			con.setAutoCommit(false); // 设置手动提交事务
//			bTrans = true;
    		//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
			
    		hmSec = getSecurityName();
    		
            AccountingYear = YssFun.getYear(this.sDate);
            
            //MS01189 QDV4赢时胜（深圳）2010年5月18日01_B 如果没有设置组合对应的套账链接，则不进行财务系统辅助核算表处理
            VchPortSetLinkBean vchPortSetLink = new VchPortSetLinkBean();
            vchPortSetLink.setYssPub(pub);
            bookCode = vchPortSetLink.getSet(this.sPort);
            if(bookCode == null || bookCode.length() == 0){
            	return;
            }
            //--------------MS01189 QDV4赢时胜（深圳）2010年5月18日01_B------------
            
            //通过组合代码获取套帐代码
            YssFinance yssFin = new YssFinance();
            yssFin.setYssPub(pub);
            bookCode = yssFin.getCWSetCode(this.sPort);
            
            if(bookCode == null || bookCode.length() == 0){ //panjunfang add 20100429
            	throw new YssException("组合【" + this.sPort + "】对应的财务套账不存在，请在财务系统的套账管理中增加该组合套账！");
            }
            YssTabPrefix = "A" + AccountingYear + bookCode + "AUXIACCSET";
    		
            if(!dbl.yssTableExist(YssTabPrefix)){
            	throw new YssException("套账【" + bookCode + "】没有设置" + AccountingYear + "年份套账信息，请在财务系统的套账管理中增加该年份套账！");
            }
            //---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
//            strSql = " insert into " + YssTabPrefix + 
//            "(AuxiAccID, AuxiAccName, Remark) VALUES(?,?,?) ";
//            
//            pstmt = dbl.openPreparedStatement(strSql);
            //---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
            
            strSql = " select a.FZQDM, b.FSecurityName, b.Fcatcode, b.FTradeCury, b.FExchangeCode from (select distinct FZQDM from " + pub.yssGetTableName("Tb_HzJkMx") + 
            " where FZQDM not in (select distinct substr(AUXIACCID,3) from " + YssTabPrefix + 
            " where AUXIACCID like '% CG' or AUXIACCID like '% CS') and FPortCode = " +  dbl.sqlString(this.sPort) +
            " and FInDate = " + dbl.sqlDate(this.sDate) + ") a left join " +
            " (select FSecurityCode,FSecurityName,FCatCode,FTradeCury,FExchangeCode from " + pub.yssGetTableName("Tb_Para_Security") + " where FCheckState = 1) b " + 
            " on a.FZQDM = b.Fsecuritycode ";
            
            rs = dbl.openResultSet(strSql); //在交易接口明细库中查找系统读数日期当天的深圳的非证券信息设置表中的证券的数据
            
            //---add by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
        	VchAssistantSettingBean assSetting = null;
        	assSetting = new VchAssistantSettingBean();
        	assSetting.setYssPub(pub);
        	//---add by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
        	
            while(rs.next()){
            	if(rs.getString("FCatCode") == null){
            		continue;
            	}

            	//---add by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
            	assSetting.setSettingCode("");
            	assSetting.setBookSetCode(rs.getString("FZQDM"));
            	assSetting.setBookSetName(rs.getString("FSecurityName"));
            	assSetting.setCuryCode(rs.getString("FTradeCury"));
            	assSetting.setCatCode("");
            	assSetting.setAuxiAccTB(AccountingYear + "");
            	assSetting.setAuxiAccID(rs.getString("FCatCode"));
            	assSetting.setDesc(rs.getString("FExchangeCode"));
            	assSetting.setExchange("");
            	assSetting.setOldSettingCode("");
            	assSetting.checkAssSettingForAudit();
            	//---add by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
            	//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
//				proCode = judgeProCode(rs.getString("FCatCode"));

//				pstmt.setString(1, proCode + rs.getString("FZQDM"));
//				if (hmSec != null && hmSec.get(YssFun.left(rs.getString("FZQDM"), 6)) != null) {
//					pstmt.setString(2, (String) hmSec.get(YssFun.left(rs.getString("FZQDM"), 6)));
//				} else {
//					pstmt.setString(2, rs.getString("FZQDM"));
//				}
//				pstmt.setString(3, " ");
//
//				pstmt.addBatch();
            }
            
//            pstmt.executeBatch();
//
//			con.commit(); // 提交事务
//			bTrans = false;
//			con.setAutoCommit(true); // 设置自动提交
            //---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
    	}catch(Exception e){
    		throw new YssException("插入辅助核算表中的国内业务相关的证券名称出错！", e);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B start---//
//			dbl.closeStatementFinal(pstmt);
//			dbl.endTransFinal(con, bTrans);
    		//---delete by songjie 2011.12.15 BUG 3379 QDV4农业银行2011年12月12日03_B end---//
    	}
    }
    
    /**
     * add by songjie
     * 2010.03.05
     * 根据品种代码 推出辅助核算表中的项目代码
     * @param catCode
     * @return
     * @throws YssException
     */
    private String judgeProCode(String catCode)throws YssException{
    	String proCode = "";//项目代码
    	try{
    		//若为债券 则财务辅助核算表中的项目代码为01
    		if(catCode.equals("FI")){
    			proCode = "01";
    		}
    		//若为回购 则财务辅助核算表中的项目代码为02
    		if(catCode.equals("RE")){
    			proCode = "02";
    		}
    		//若为基金 则财务辅助核算表中的项目代码为03
    		if(catCode.equals("TR")){
    			proCode = "03";
    		}
    		//若为权证 则财务辅助核算表中的项目代码为04
    		if(catCode.equals("OP")){
    			proCode = "04";
    		}
    		//若为股票 则财务辅助核算表中的项目代码为05
    		if(catCode.equals("EQ")){
    			proCode = "32";
    		}
    		return proCode;
    	}catch(Exception e){
    		throw new YssException("品种代码转换为财务辅助核算表中的项目代码出错！", e);
    	}
    }    
    /**
     * add by songjie
     * 2010.02.28
     * MS00889
     * QDII4.1赢时胜上海2010年02月23日02_AB
     * 获取国内接口中未储存到业务资料的导入数据
     * @param hmShowZqdm
     * @throws YssException
     */
    private void dealShowZqdm(HashMap hmShowZqdm)throws YssException{
    	Iterator iterator = null;
    	String zqdm = "";
    	try{
            if(hmShowZqdm != null && hmShowZqdm.size() > 0){
            	iterator = hmShowZqdm.keySet().iterator();
            	while(iterator.hasNext()){
            		zqdm = (String)iterator.next();
            		if(hmShowZqdms.get(zqdm) == null){
            			hmShowZqdms.put(zqdm, hmShowZqdm.get(zqdm));
            		}
            	}
            }
    	}catch(Exception e){
    		throw new YssException("获取国内接口中未储存到业务资料的导入数据出错！", e);
    	}
    }
    
    /**
     * add by songjie
     * 2010.03.22
     * QDII国内：MS00925
     * QDV4赢时胜（测试）2010年03月19日03_AB
     * @param alShowZqdm
     * @throws YssException
     */
    private void dealAlShowZqdm(ArrayList alShowZqdm)throws YssException{
    	Iterator iterator = null;
    	String zqdm = "";
    	try{
    		if(alShowZqdm != null && alShowZqdm.size() > 0){
    			iterator = alShowZqdm.iterator();
            	while(iterator.hasNext()){
            		zqdm = (String)iterator.next();
            		if(!alShowZqdms.contains(zqdm)){
            			alShowZqdms.add(zqdm);
            		}
            	}
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未维护债券信息的导入数据出错！",e);
    	}
    }
    
    /**
     * add by songjie
     * 2010.03.27
     * QDII国内：MS00946 
     * QDV4赢时胜（测试）2010年03月25日11_AB
     * @param alShowZqdm
     * @throws YssException
     */
    private void dealAlShowHGZqdm(ArrayList alShowHGZqdm)throws YssException{
    	Iterator iterator = null;
    	String zqdm = "";
    	try{
    		if(alShowHGZqdm != null && alShowHGZqdm.size() > 0){
    			iterator = alShowHGZqdm.iterator();
            	while(iterator.hasNext()){
            		zqdm = (String)iterator.next();
            		//edit by songjie 2012.07.26 STORY #2727 QDV4赢时胜(北京)2012年6月13日01_A
            		if(zqdm != null && !alShowHGZqdms.contains(zqdm)){
            			alShowHGZqdms.add(zqdm);
            		}
            	}
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未维护回购信息的导入数据出错！",e);
    	}
    }
    
    /**
     * add by songjie
     * 2010.02.28
     * MS00889
     * QDII4.1赢时胜上海2010年02月23日02_AB
     * 处理国内接口中未储存到业务资料的导入数据，
     * 提示客户设置相关的权益数据
     * @throws YssException
     */
    private void showUnInsertSecInfo()throws YssException{
    	Iterator iterator = null;
    	String showInfos = "";
    	String info = "";
    	try{
    		iterator = hmShowZqdms.values().iterator();
    		while(iterator.hasNext()){
    			info = (String)iterator.next();
    			showInfos += info + "\r\n";
    		}
    		
    		if(showInfos.length() > 2)
    		{
    			showInfos += "请维护或检查以上证券的相关权益信息_@_DomesticQY";
    		}
    		
    		if(!showInfos.equals("")){
    			throw new YssException(showInfos);
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未储存到业务资料的导入数据出错！", e);
    	}
    }
    
    /**
     * add by songjie
     * 2010.03.22
     * MS00925
     * QDV4赢时胜（测试）2010年03月19日03_AB
     * 处理国内接口中未维护债券信息的导入数据，
     * 提示客户维护相关的债券信息设置
     * @throws YssException
     */
    private void showUnInsertZQInfo()throws YssException{
    	Iterator iterator = null;
    	String showInfos = "";
    	String info = "";
    	try{
    		iterator = alShowZqdms.iterator();
    		while(iterator.hasNext()){
    			info = (String)iterator.next();
    			showInfos += info + ",";
    		}
    		
    		if(showInfos.length() > 2)
    		{
    			showInfos = showInfos.substring(0, showInfos.length() - 1);
    			showInfos = "请维护证券 " + showInfos + " 的相关债券信息_@_DomesticQY";
    		}
    		
    		if(!showInfos.equals("")){
    			throw new YssException(showInfos);
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未维护债券信息的导入数据出错！", e);
    	}
    }
    
    /**
     * add by songjie
     * 2010.03.27
     * MS00946 
     * QDV4赢时胜（测试）2010年03月25日11_AB
     * 处理国内接口中未维护回购信息的导入数据，
     * 提示客户维护相关的回购信息设置
     * @throws YssException
     */
    private void showUnInsertHGInfo()throws YssException{
    	Iterator iterator = null;
    	String showInfos = "";
    	String info = "";
    	try{
    		iterator = alShowHGZqdms.iterator();
    		while(iterator.hasNext()){
    			info = (String)iterator.next();
    			showInfos += info + ",";
    		}
    		
    		if(showInfos.length() > 2)
    		{
    			showInfos = showInfos.substring(0, showInfos.length() - 1);
    			showInfos = "请维护证券 " + showInfos + " 的相关回购信息_@_DomesticQY";
    		}
    		
    		if(!showInfos.equals("")){
    			throw new YssException(showInfos);
    		}
    	}catch(Exception e){
    		throw new YssException("处理国内接口中未维护回购信息的导入数据出错！", e);
    	}
    }
}
