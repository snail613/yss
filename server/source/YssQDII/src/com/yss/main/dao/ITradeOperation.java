package com.yss.main.dao;

import com.yss.dsub.*;
import com.yss.util.*;

public interface ITradeOperation {
    public void initTradeOperation(String sSecurityCode, String sBrokerCode, String sUserCode,
                                   String sTradeType, String sOrderNum,
                                   java.util.Date dDate, String sSubType, String sPortCode) throws YssException;

    public void initTradeOperation(String sSecurityCode, String sBrokerCode, String sUserCode,
                                   String sTradeType, String sOrderNum,
                                   java.util.Date dDate, String sSubType) throws YssException;

    public void loadTradeData() throws YssException;

    public void setYssPub(YssPub pub);

    //获取某个用户所有有权限组合的某个证券在库存中的总数量
    public double getAllPortSumAmountInStorage() throws YssException;

    //获取某个用户所有有权限组合的某个证券在某日交易的总数量
    public double getAllPortSumAmountInTrade() throws YssException;

    //获取某个用户各个组合中某个证券的数量
    public double getPortAmountInStorage(String sPortCode) throws YssException;

    //获取某一个组合中某只证券占总数量的比例
    public double getPortAmountScale(String sPortCode) throws YssException;

    //获取某个用户所有有权限组合的某个证券在库存中的总金额
    public double getAllPortSumMoneyInStorage() throws YssException;

    //获取某个用户所有有权限组合的某个证券在某日交易的总金额
    public double getAllPortSumMoneyInTrade() throws YssException;

    //获取所有组合已清算的金额
    public double getAllPortSumMoneyInSettle() throws YssException;

    //获取某个用户各个组合中某个证券的金额
    public double getPortMoneyInStorage(String sPortCode) throws YssException;

    //获取某个组合已清算金额
    public double getPortMoneyInSettle(String sPortCode) throws YssException;

    //获取某一个组合中某只证券占总金额的比例
    public double getPortMoneyScale(String sPortCode) throws YssException;

    //获取某一个组合中某只证券占总净值的比例
    public double getPortNetValueScale(String sPortCode) throws YssException;

    //获取原始分配数量
    public double getOriginDistAmount(String sPortCode, double sTotalAmount) throws YssException;

    //获取去碎股后的分配数量
    public double getWipedDistAmount(String sPortCode, double dExchangeAmount, double dTotalAmount,
                                     double dPrice) throws YssException;

    //获取比例的尾差
    public double getTailScale() throws YssException;

    //获取去碎股数的尾差
    public double getTailAmount(double dSumAmount, double dPrice) throws YssException;

    //根据交易类型获取比例
    public double getScale(String sPortCode, String sTailPortCode) throws
        YssException;

    //获取计算过尾差的去碎股数
    public double getTailWipedDistAmount(String sPortCode, String sTailPortCode,
                                         double sTotalAmount, double dExchangeAmount, double dPrice) throws YssException;

    //根据交易类型获取各组合上持有现金或持有股数
    public double getHave(String sPortCode) throws YssException;

    //获取证券的冻结数量 MS00125
    public double getSecurityFreezeAmount() throws YssException;
}
