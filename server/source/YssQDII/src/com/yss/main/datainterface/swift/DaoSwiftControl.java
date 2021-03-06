package com.yss.main.datainterface.swift;

import java.util.HashMap;

import com.yss.dsub.*;
import com.yss.util.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.datainterface.swift.*;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * by leeyu 20090610
 * SWIFT 报文导入与导出的控制类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company:YssTech </p>
 *
 * @author by leeyu create
 * @version 1.0
 */
public class DaoSwiftControl
    extends BaseBean implements IDataInterface {
    private String sSwiftReflow = ""; //导入导出状态
    private String sStandard = ""; //报文标准
    private String sSwiftType = ""; //报文类型
    private String sOperType = ""; //业务类型
    private String beginDate = "1900-01-01"; //开始日期
    private String endDate = "1900-01-01"; //结束日期
    private String sPortCodes = ""; //组合代码集
    private String sSwiftDatas = ""; //SWIFT处理数据
    private String SSwiftStatus=""; //SWIFT报文内部状态 包括：NEWM、CANC、REDO 等
    private String sRelanum = "";//交易编号
    private String sTradeNums = "";//交易编号组，存储前台传进来的审核的已撤销报文的交易编号，用于重新生成报文
    
    private BaseSwiftInputOper swiftInput;
    private BaseSwiftOutputOper swiftOutput;
    public DaoSwiftControl() {
    }

    /**
     * 导入数据处理
     * @param sRequestStr String
     * @throws YssException
     */
    public void importData(String sRequestStr) throws YssException {
        parseRowStr(sRequestStr);
        swiftInput.parseReqsRow(sSwiftDatas, "import"); //这里需先解析后再处理
        swiftInput.parseSaveSWIFT();
    }

    /**
     * 导出数据处理
     * @param sRequestStr String
     * @return String
     * @throws YssException
     */
    public String exportData(String sRequestStr) throws YssException {
        parseRowStr("");
        //swiftOutput.buildSWIFT();
		swiftOutput.exportSwiftDatas();
        return "";
    }

    /**
     * 解析sRowStr中的数据
     * @param sRowStr String
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrReq = sRowStr.split("\f\n\f");
        this.sSwiftType = arrReq[0];
        this.sOperType = arrReq[1];
        this.sStandard = arrReq[2];
        this.sSwiftReflow = arrReq[3];
        if (arrReq[4].length() > 0 && YssFun.isDate(arrReq[4])) {
            this.beginDate = arrReq[4];
        }
        if (arrReq[5].length() > 0 && YssFun.isDate(arrReq[5])) {
            this.endDate = arrReq[5];
        }
        this.sPortCodes = arrReq[6];
        this.SSwiftStatus = arrReq[7]; //新增报文类型 by leeyu 20091110
        this.sSwiftDatas = arrReq[8];
        this.sTradeNums = arrReq[9];
        if(arrReq.length == 12){
        	this.sRelanum = arrReq[10];
        }
        
        initBeans();
    }

    /**
     * 编写一行数据
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        return "";
    }

    /**
     * 获取特定数据处理
     * @param sType String
     * @return String
     * @throws YssException
     */
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            if (sType != null) {
                if (sSwiftReflow.equalsIgnoreCase("in")) { //导入报文
                    swiftInput.parseReqsRow("", sType); //将数据解析一下
                    if (sType.equalsIgnoreCase("init")) { //初始化加载
                        sResult = swiftInput.initSWIFTListView();
                    } else if (sType.equalsIgnoreCase("load")) { //加载报文数据
                        sResult = swiftInput.loadSWIFTListView();
                    } else if (sType.equalsIgnoreCase("query")) { //查询报文
                        sResult = swiftInput.querySWIFTList();
                    } else {
                        sResult = "";
                    }
                } else if (sSwiftReflow.equalsIgnoreCase("out")) { //导出报文
                    swiftOutput.parseReqsRow("", sType);
                    if (sType.equalsIgnoreCase("init")) {//加载初始化
                    	sResult =swiftOutput.initSWIFTListView();
                    }else if(sType.equalsIgnoreCase("load")){	//查询报文
                    	sResult =swiftOutput.loadSWIFTListView();
                    }else if(sType.equalsIgnoreCase("markSwift")){ //生成报文
                    	sResult = swiftOutput.markAndExecute();
                    }else if(sType.equalsIgnoreCase("expSwift")){//导出报文
                    	sResult = swiftOutput.exportSwiftDatas();
                    }else if(sType.equalsIgnoreCase("searchData")){//查询报文对应的数据
                    	sResult = swiftOutput.getLoadRelaTrade();
                    }else if(sType.equalsIgnoreCase("audit")){ //审核反审核报文                    	
                    	sResult =swiftOutput.checkSetting();
                    }else if(sType.equalsIgnoreCase("canc")){ //产生撤消报文的过程
                    	sResult = swiftOutput.markCancSwift();
                    }
                    else if(sType.equalsIgnoreCase("delete")){ //添加删除功能  李道龙2009-11-17
                    	swiftOutput.deleteSetting();
                    }else if(sType.equalsIgnoreCase("sendStatus")){//收发状态查询  蒋世超 2010-03-05
                    	sResult = swiftOutput.sendStatus();
                    }
                    else {
                    	sResult ="";
                    }
                }
            } // end check is null
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
        return sResult;
    }

    /**
     * 创建具体窗体的方法
     * @throws YssException
     */
    private void initBeans() throws YssException {
        if (sSwiftReflow.equalsIgnoreCase("in")) {
            //这里可进行细分
            swiftInput = new SwiftInputISO15022();
            swiftInput.setYssPub(pub);
            swiftInput.setStartDate(YssFun.toDate(beginDate));
            swiftInput.setEndDate(YssFun.toDate(endDate));
            swiftInput.setPortCode(this.sPortCodes);
            swiftInput.setSwiftStandard(this.sStandard);
            swiftInput.setSwiftType(this.sSwiftType);
            swiftInput.setReflow(this.sSwiftReflow);
            swiftInput.setOperType(this.sOperType);
            swiftInput.setSwiftStatus("NEW");//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            swiftInput.initBean();
        } else {
            //这里可进行细分
            swiftOutput = new SwiftOutputISO15022();
            swiftOutput.setYssPub(pub);
            swiftOutput.setPortCode(sPortCodes);
            swiftOutput.setStartDate(YssFun.toDate(beginDate));
            swiftOutput.setEndDate(YssFun.toDate(endDate));
            swiftOutput.setSwiftStandard(this.sStandard);
            swiftOutput.setSwiftType(this.sSwiftType);
            swiftOutput.setReflow(this.sSwiftReflow);
            swiftOutput.setOperType(this.sOperType);
            swiftOutput.setOperDatas(this.sSwiftDatas);
            swiftOutput.setSwiftStatus(this.SSwiftStatus);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            swiftOutput.setsRelanum(this.sRelanum);
            //---- add by jiangshichao 2010.02.28--------------------------------------
            if(this.sTradeNums.length()>0){
            	HashMap hashMap = new HashMap();
            	String[] tradeNums = sTradeNums.split("\t");
            	for(int i=0;i<tradeNums.length;i++){
            		hashMap.put(tradeNums[i], tradeNums[i]);
            	}
            	swiftOutput.setTradeNums(hashMap);
            }
          //---------------------------------------------------------------------------//  
            swiftOutput.initBean();
        }
    }
}
