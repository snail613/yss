package com.yss.util;

import java.util.HashMap;
import java.util.ArrayList;
import com.yss.dsub.*;

/**
 * <p>Title:全局类 </p>
 *
 * <p>
 * Description:
 * 1.用于存储全局使用的静态变量的类
 * 2.用于存储全局使用的静态方法
 * </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: Ysstech 20090425</p>
 *
 * @author sunkey
 * @version 1.0
 */
public class YssGlobal {
    public YssGlobal() {
    }

    public static boolean clearValNums=false; //估值时是否清空证券应收应付，现金应收应付等的编号 by leeyu 20100520 合并太平版本代码
    public static HashMap hmCashRecNums =new HashMap();//估值时保存现金应收应付编号,并行处理优化 by leeyu 20100520 合并太平版本代码
    public static HashMap hmSecRecNums = new HashMap();//估值时保存证券应收应付编号,并行处理优化 by leeyu 20100520 合并太平版本代码
    public static Object objSecRecLock =new Object(); //用于在生成证券应收应付时锁对象用,并行处理优化 by leeyu 20100520 合并太平版本代码
    public static Object objCashRecLock =new Object();//用于在生成现金应收应付时锁对象用,并行处理优化 by leeyu 20100520 合并太平版本代码
    //====MS00006-QDV4.1赢时胜上海2009年2月1日05_A  QDV4.1赢时胜上海2009年2月1日05_A 多用户并发处理优化 add by sunkey====
    //按照键:用户+组合群,值:SecRecPayBalBean对应的哈希表
    public static HashMap hmSecRecBeans = new HashMap();
    //=============================End MS00006-QDV4.1赢时胜上海2009年2月1日05_A ====================================

    //MS00006-QDV4.1赢时胜上海2009年2月1日05_A -QDV4.1赢时胜上海2009年2月1日05_A  add by songjie 2009-04-29 key - 操作类型，组合群代码，组合代码 value - 用户代码
    //用于储存操作类型，组合群代码，组合代码对应的用户代码
    public static HashMap hmOperAssetUserInfo = new HashMap();
    
    public static Object objCashTsfLock =new Object(); //用于在多组合并发生成资金调拨时锁对象用, add by wangzuochun 2011.02.12 BUG #1047 多组合并发交易结算，产生资金调拨数据时，系统提示违反唯一约束。

    /**
     * MS00006-QDV4.1赢时胜上海2009年2月1日05_A -QDV4.1赢时胜上海2009年2月1日05_A  add by songjie
     * 用于判断同一操作类型，组合群代码，组合代码下的用户代码是否唯一
     * QDV4.1赢时胜上海2009年2月1日05_A
     * 2009-04-29
     * @param assetGroupCode String
     * @param userCode String
     * @param portCodes String
     */
    public static void judgeIfUniqueUserAndPort(String operTypeCode, String portCodes, YssPub pub) throws YssException {
        String[] arrPortCode = null; //用于储存拆分后的组和代码
        String oldUserCode = null; //原先的用户代码
        String userCode = null; //用户代码
        String assetGroupCode = null; //组合群代码
        ArrayList alUserCode = new ArrayList(); //

        String strPortCodes = ""; //用于储存组和代码
        String strUserCodes = ""; //用于储存用户代码

        assetGroupCode = pub.getAssetGroupCode();
        arrPortCode = portCodes.split(",");

        for (int i = 0; i < arrPortCode.length; i++) { //循环组合代码
            //若hmOperAssetUserInfo包含操作类型，组合群代码，组合代码为键的信息
            if (hmOperAssetUserInfo.containsKey(operTypeCode + "_" + assetGroupCode + "_" + arrPortCode[i])) {
                //则取出操作类型，组合群代码，组合代码对应的用户代码
                oldUserCode = (String) hmOperAssetUserInfo.get(operTypeCode + "_" + assetGroupCode + "_" + arrPortCode[i]);
                //若alUserCode中不包含oldUserCode
                if (!alUserCode.contains(oldUserCode)) {
                    alUserCode.add(oldUserCode); //则添加oldUserCode到alUserCode
                    strUserCodes += oldUserCode + ","; //添加oldUserCode到储存用户代码信息的字符串中，用逗号隔开
                }
                strPortCodes += arrPortCode[i] + ","; //添加oldUserCode对应的组合代码到储存组和代码信息的字符串中用逗号隔开
            }
        }
        //若strUserCodes和strPortCodes都有信息
        if (strUserCodes.length() > 1 && strPortCodes.length() > 1) {
            strUserCodes = strUserCodes.substring(0, strUserCodes.length() - 1); //则删除字符串最后的逗号
            strPortCodes = strPortCodes.substring(0, strPortCodes.length() - 1); //则删除字符串最后的逗号
            //若操作类型为收益支付，则抛出相应异常信息
            if (operTypeCode.equals(YssCons.YSS_OPER_INCOMEPAID)) {
                throw new YssException("用户【" + strUserCodes + "】正在做组合群代码为" +
                                       assetGroupCode + "组和代码为【" + strPortCodes
                                       + "】的收益支付，多个用户不能同时做同一个组合的收益支付");
            }
            //若操作类型为收益计提，则抛出相应异常信息
            else if (operTypeCode.equals(YssCons.YSS_OPER_INCOMESTAT)) {
                throw new YssException("用户【" + strUserCodes + "】正在做组合群代码为" +
                                       assetGroupCode + "组和代码为【" + strPortCodes
                                       + "】的收益计提，多个用户不能同时做同一个组合的收益计提");
            }
            //若操作类型为库存统计，则抛出相应异常信息
            else if (operTypeCode.equals(YssCons.YSS_OPER_STORAGESTAT)) {
                throw new YssException("用户【" + strUserCodes + "】正在做组合群代码为" +
                                       assetGroupCode + "组和代码为【" + strPortCodes
                                       + "】的库存统计，多个用户不能同时做同一个组合的库存统计");
            }
            //若操作类型为资产估值，则抛出相应异常信息
            else if (operTypeCode.equals(YssCons.YSS_OPER_VALUATION)) {
                throw new YssException("用户【" + strUserCodes + "】正在估组合群代码为" +
                                       assetGroupCode + "组合代码为【" + strPortCodes
                                       + "】的净值，多个用户不能同时估同一个组合的净值");
            }
        }
        userCode = pub.getUserCode(); //获取当前用户代码
        for (int i = 0; i < arrPortCode.length; i++) { //循环组和代码
            //在hmOperAssetUserInfo中插入用户在相应组合群组合下操作的信息
            hmOperAssetUserInfo.put(operTypeCode + "_" + assetGroupCode + "_" + arrPortCode[i], userCode);
        }

        alUserCode.clear(); //清空alUserCode
    }

    /**
     * MS00006-QDV4.1赢时胜上海2009年2月1日05_A -QDV4.1赢时胜上海2009年2月1日05_A  add by songjie
     * 用于移除当前用户当前组合群组合下操作类型方面的信息
     * QDV4.1赢时胜上海2009年2月1日05_A
     * 2009-04-29
     * @param operTypeCode String
     * @param portCodes String
     * @param pub YssPub
     * @throws YssException
     */
    public static void removeRefeUserInfo(String operTypeCode, String portCodes, YssPub pub) throws YssException {
        String[] arrPortCode = null; //用于储存拆分后的组合代码
        String assetGroupCode = pub.getAssetGroupCode(); //获取当前组合群代码

        arrPortCode = portCodes.split(","); //拆分组和代码

        for (int i = 0; i < arrPortCode.length; i++) { //循环组和代码
            hmOperAssetUserInfo.remove(operTypeCode + "_" + assetGroupCode + "_" + arrPortCode[i]); //依次移除操作代码，组合群代码，组合代码方面的信息
        }
    }
}
