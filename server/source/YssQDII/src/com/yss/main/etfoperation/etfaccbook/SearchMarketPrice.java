package com.yss.main.etfoperation.etfaccbook;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.operdata.MarketValueBean;
import com.yss.main.parasetting.MTVMethodBean;
import com.yss.util.YssException;

public class SearchMarketPrice extends BaseDataSettingBean{
	HashMap mktOrExValueHm = null;
	
	/**
	 * 构造函数
	 */
	public SearchMarketPrice() {
		
	}
	
	/**
	 * 获取相关证券的行情数据
	 * @param basketCodes
	 *            String
	 * @throws YssException
	 */
	public HashMap getMarketPrice(String basketCodes,java.util.Date tradeDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		try {
			mktOrExValueHm = new HashMap();
			
			strSql = " select FClosingPrice,FSecurityCode,FMktSrcCode,FPortCode,FMktValueDate from " + pub.yssGetTableName("Tb_data_marketvalue") + 
			" where FMktValueDate = "+ dbl.sqlDate(tradeDate) + " and FSecurityCode in(" +
			operSql.sqlCodes(basketCodes) + ") and FCheckState = 1 order by FSecurityCode,FMktSrcCode,FPortCode";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				setMarketValue(rs); //设置行情对象的属性并将组合代码证券代码行情来源代码对应的行情实例储存到mktOrExValueHm中
			}
			
			return mktOrExValueHm;
		} catch (Exception e) {
			throw new YssException("查询行情出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 用于设置行情对象的属性
	 * 
	 * @param rs
	 *            ResultSet
	 */
	private void setMarketValue(ResultSet rs) throws YssException {
		try {
			MarketValueBean marketvalue = new MarketValueBean(); // 新建行情实例
	
			marketvalue.setStrMktSrcCode(rs.getString("FMktSrcCode")); // 设置行情来源代码
			marketvalue.setStrSecurityCode(rs.getString("FSecurityCode")); // 设置证券代码
			marketvalue.setStrMktValueDate(rs.getDate("FMktValueDate").toString()); // 设置行情日期
	
			marketvalue.setStrPortCode(rs.getString("FPortCode").trim()); // 设置组合代码
			marketvalue.setDblClosingPrice(rs.getDouble("FClosingPrice")); // 设置停盘价
			// 设置行情对象的属性并将组合代码证券代码行情来源代码对应的行情实例储存到mktOrExValueHm中
			mktOrExValueHm.put(rs.getString("FPortCode").trim() + rs.getString("FSecurityCode") + rs.getString("FMktSrcCode"), marketvalue); 
		}
		catch(Exception e){
			throw new YssException("给行情实体类赋值出错！", e);
		}
	}
	
	/**
	 * 查询相关证券的小于等于估值日期的大于等于申赎日期的最大行情日期的收盘价
	 * @param securityCode
	 * @param buyDate
	 * @return
	 * @throws YssException
	 */
	public HashMap getMaxDateMarketPrice(String securityCode, java.util.Date buyDate, java.util.Date tradeDate) throws YssException {
		String strSql = "";
		ResultSet rs = null;
		
		try {
			mktOrExValueHm = new HashMap();
			
			strSql = " select b.FPortCode,b.FSecurityCode,b.FMktSrcCode,b.FMktValueDate,b.FMktValueTime, "
					+ " b.FBargainAmount,b.FBargainMoney,b.FYClosePrice,b.FOpenPrice, "
					+ " b.FTopPrice,b.FLowPrice,b.FClosingPrice,b.FAveragePrice,b.FNewPrice, "
					+ " b.FMktPrice1,b.FMktPrice2,b.FMarketStatus,b.FDesc,b.FCheckState from "
					+ " (select FMktSrcCode,FSecurityCode,FPortCode,max(FMktValueDate) as fMktValueDate from " 
					+ pub.yssGetTableName("Tb_data_marketvalue") + " where FMktValueDate between " 
					+ dbl.sqlDate(tradeDate) + " and " + dbl.sqlDate(buyDate) + " and FSecurityCode = "
					+ dbl.sqlString(securityCode) + " and FCheckState = 1 group by FMktSrcCode,FSecurityCode," 
					+ "FPortCode order by FMktValueDate desc) a left join (select * from " 
					+ pub.yssGetTableName("tb_data_marketvalue") +" mkt where FSecurityCode = " + dbl.sqlString(securityCode)
					+ " and FCheckState = 1) b on a.fmktsrccode = b.fmktsrccode and a.fMktValueDate = b.fMktValueDate " 
					+ " and a.FSecurityCode = b.FSecurityCode and a.FPortCode = b.FPortCode " 
					+ " order by a.fsecuritycode,FMktValueDate ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				setMarketValue(rs); //设置行情对象的属性并将组合代码证券代码行情来源代码对应的行情实例储存到mktOrExValueHm中
			}
			return mktOrExValueHm;
		} catch (Exception e) {
			throw new YssException("查询行情出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}
	
	   /**
     * 获取所有组合代码的所有估值方法
     * @param portCode String
     * @throws YssException
     */
    public HashMap getMtvMethodsBy(String portCodes) throws YssException {
        String strSql = ""; //用于储存sql语句
        String mtvCode = ""; //用于储存估值方法代码
        String portCode = null; //用于储存组合代码
        String[] arrPortcodes = null; //用于储存组合代码对应的估值方法代码，key - 组合代码，value - 估值方法代码
        ResultSet rs = null; //结果集
        ArrayList mtvMethods = null; //储存估值方法代码对应的估值方法实例的ArrayList
        HashMap allMtvMethodHm = null; //储存组合代码对应的估值方法的实例 key - 组合代码 value - 组合代码对应的估值方法实例的ArrayList
        HashMap alPortMTVCode = null; //储存组合代码对应的估值方法信息，key - 组合代码，value - 组和代码对应的多个或一个估值方法信息的用"\t"隔开的字符串
        MTVMethodBean mtvMethod = null; //声明估值方法实例
        try {
            allMtvMethodHm = new HashMap();
            alPortMTVCode = new HashMap();

            //查询所有组合代码对应的估值方法代码，并按照估值方法的优先级从大到小排序
            strSql =
                " select a.FPortCode,b.FRelaGrade,b.FSubCode from (select FPortCode from " +
                pub.yssGetTableName("TB_Para_PortFolio_RelaShip") +
                " where FPortCode in (" + operSql.sqlCodes(portCodes) +
                ") order by FPortCode) a left join (select fportCode,FRelaGrade,FSubCode from " +
                pub.yssGetTableName("TB_Para_PortFolio_RelaShip") +
                " where FRelaType = 'MTV') b on a.FPortCode = b.FPortCode " +
                " group by a.FportCode,b.Frelagrade,b.Fsubcode ";

            rs = dbl.openResultSet(strSql);

            while (rs.next()) {
                if (alPortMTVCode.get(rs.getString("FPortCode")) != null) { //若alPortMTVCode中有当前组合代码对应的估值方法代码信息字符串
                    mtvCode = (String) alPortMTVCode.get(rs.getString("FPortCode")); //则取出当前组合代码对应的估值方法代码信息字符串
                    mtvCode += rs.getString("FSubCode") + "\t"; //给当前组合代码对应的估值方法信息字符串添加估值方法代码信息以"\t"隔开
                    alPortMTVCode.put(portCode, mtvCode); //更新alPortMTVCode中当前组和代码对应的估值方法信息，原先的组合代码对应的value会被覆盖掉
                } else { //若alPortMTVCode中没有当前组合代码对应的估值方法代码信息字符串
                    mtvCode = ""; //新建一个用于储存估值方法代码信息的字符串
                    mtvCode += rs.getString("FSubCode") + "\t"; //给估值方法代码信息字符串添加估值方法代码信息以"\t"隔开
                    portCode = rs.getString("FPortCode"); //获取当前组合代码信息
                    alPortMTVCode.put(portCode, mtvCode); //将组合代码和组合代码对应的估值方法代码信息字符串储存到alPortMTVCode中
                }
            }

            arrPortcodes = portCodes.split(","); //将组合代码字符串用逗号隔开

            for (int i = 0; i < arrPortcodes.length; i++) { //循环组合代码
                mtvCode = (String) alPortMTVCode.get(arrPortcodes[i]); //在alPortMTVCode中根据组合代码获取相应的估值方法代码字符串
                mtvCode = mtvCode.substring(0, mtvCode.length() - 1); //将估值方法代码字符串的最后的"\t"去掉
                mtvMethod = new MTVMethodBean(); //新建一个估值方法的Bean
                mtvMethod.setYssPub(pub); //设置pub
                mtvMethods = mtvMethod.getMTVInfo(mtvCode); //通过估值方法代码字符串得到对应的估值方法实例的arrayList
                allMtvMethodHm.put(arrPortcodes[i], mtvMethods); //将组合代码和组合代码对应的估值方法实例的arrayList储存到allMtvMethodHm中
            }
            
            return allMtvMethodHm;
        } catch (Exception e) {
            throw new YssException("获取组合代码对应的估值方法出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 获取国内当天证券代码对应的估值方法和行情
     * @param portCodes String 组合代码
     * @param ctlpubpara CtlPubPara 通用参数实例
     * @param dDate Date 估值日期
     * @throws YssException
     */
    public HashMap getMTVMethod(String portCode,HashMap portSecHm, HashMap allMtvMethodHm,HashMap mktOrExValueHm) throws
        YssException {
        String securityCode = "";//证券代码
        ArrayList alSec = null;
        HashMap hmSecurityPrice = null;
        try {
        	hmSecurityPrice = new HashMap();
        	
        	alSec = (ArrayList)portSecHm.get(portCode);
			ArrayList alMTV = (ArrayList) allMtvMethodHm.get(portCode); // 获取组合代码对应的估值方法实例的ArrayList
			
			if (alSec != null && alMTV != null) { 
				Iterator iterator = alSec.iterator();
				while(iterator.hasNext()){
					securityCode = (String)iterator.next();
					getSubMTVMethod(hmSecurityPrice, securityCode, alMTV, portCode,mktOrExValueHm); // 获取当天证券代码对应的估值方法和行情
				}
			}
			return hmSecurityPrice;
        } catch (Exception e) {
            throw new YssException("获取证券对应的估值方法出错", e);
        }
    }
    
    /**
     * 获取当天证券代码对应的估值方法和行情
     * @param secValue Iterator 组合代码对应的证券信息的迭代器
     * @param alMTV ArrayList 组合代码对应的估值方法的ArrayList
     * @param isACTV boolean 是否要储存行情状态的标志信息
     * @param portCode String 组合代码
     * @param dDate Date 估值日期
     * @throws YssException
     */
	private void getSubMTVMethod(HashMap hmSecurityPrice, String securityCode, ArrayList alMTV, String portCode,HashMap mktOrExValueHm) throws YssException {
		MTVMethodBean mtvMethod = null; // 声明估值方法实例
		MarketValueBean mktValues = null; // 声明行情实例
		try {
			for (int k = 0; k < alMTV.size(); k++) { // 循环估值方法
				// 获取当前估值方法实例
				mtvMethod = (MTVMethodBean) alMTV.get(k); 

				// 在最近的行情数据中查找带组合代码的对应证券代码和估值方法行情来源代码的行情实例
				mktValues = (MarketValueBean) mktOrExValueHm.get(portCode + securityCode + mtvMethod.getMktSrcCode());

				// 若没有带组合代码的对应证券代码和估值方法行情来源代码的行情实例
				if (mktValues == null) { 
					// 则查找不带组合代码的对应证券代码和估值方法行情来源代码的行情实例
					mktValues = (MarketValueBean) mktOrExValueHm.get("" + securityCode + mtvMethod.getMktSrcCode());
				}

				// 若找到对应的行情实例
				if (mktValues != null) {
					// 且行情实例中对应估值方法行情字段的信息不为空
					if (mktValues.getDblClosingPrice() != 0) {
						hmSecurityPrice.put(securityCode + portCode, mktValues);
						return;
					}
				}
			}
		} catch (Exception e) {
			throw new YssException("获取证券对应的估值方法出错", e);
		}
	}
}
