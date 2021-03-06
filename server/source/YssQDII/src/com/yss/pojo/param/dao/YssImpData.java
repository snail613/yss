package com.yss.pojo.param.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.yss.main.dao.IYssConvert;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.util.YssException;

public class YssImpData
    implements IYssConvert {
    private String fileName = "";
    private String fileInfo = "";
    private String fileContent = "";
    HashSet hValue = new HashSet(); //从文件中读取的数据有可能是重复的数据所以要把重复数据屏蔽掉
    //因为现在提供的文件中有重复的数据,以后不会有这种情况,所以先
    //这样处理一下
    List list = new ArrayList();   //add by zhaoxianlin 20121105 #story 3159
    String[] arrSrcFileContentData = null;
    StringBuffer bufTarFileContentData = new StringBuffer();
    String fileContentData = "";
    public String getFileContent() {
        return fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileInfo(String fileInfo) {
        this.fileInfo = fileInfo;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileInfo() {
        return fileInfo;
    }

    public YssImpData() {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
        return "";
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) {
        return "";
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {
        //从前台传过来的数据不能把文件头信息分开，所以处理前台的数据时这里的fileInfo是空格
        String reqAry[] = null;
        String sTmpStr = "";
        String lineContent = ""; //20071019   chenyib
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sRowStr.split("<>");
            if (reqAry.length > 1) { //经多文件导入时测得，有时 reqAry的长度为两个，有时为三个，有时为一个长度 by liyu 1204
                this.fileName = reqAry[0];
            }
            if (reqAry.length > 2) {
                this.fileInfo = reqAry[1];
            }
            if (reqAry.length >= 3) {
                arrSrcFileContentData = reqAry[2].split("\r\t");
                for (int i = 0; i < arrSrcFileContentData.length; i++) {
                	//------ modify by wangzuochun 2011.02.22 BUG #1138 数据文件读入标普500指数成分股信息至证券信息表时报错；
                	//------ LINE COUNT（行数） 此行内容不添加到hValue中；
                    if (arrSrcFileContentData[i].trim().length() != 0 && !arrSrcFileContentData[i].startsWith("LINE COUNT")) { //判断如果此行数据为" "或为""就不要往哈希表中插入了,免得后面没处理报错 by leeyu 2008-12-17 MS00033
                        hValue.add(arrSrcFileContentData[i]);
                    }
                }
                Iterator it = hValue.iterator();
                while (it.hasNext()) {
                    lineContent = ( (String) it.next());
                    bufTarFileContentData.append(lineContent).append(" \r\t");
                }
                if (bufTarFileContentData.length() > 2) {
                    fileContentData = bufTarFileContentData.toString().substring(0,
                        bufTarFileContentData.toString().length() - 2);
                }
                this.fileContent = fileContentData;
            }
        } catch (Exception e) {
            throw new YssException("解悉数据出错", e);
        }

    }
    
    /**
     * add by songjie 2012.02.10
     * BUG 3732 QDV4农业银行2012年01月18日01_B
     * @param sRowStr
     * @param cusCfg
     * @throws YssException
     */
    public void parseRowStr(String sRowStr, DaoCusConfigureBean cusCfg) throws YssException {
        //从前台传过来的数据不能把文件头信息分开，所以处理前台的数据时这里的fileInfo是空格
        String reqAry[] = null;
        String sTmpStr = "";
        String lineContent = ""; //20071019   chenyib
        Iterator it =null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            } else {
                sTmpStr = sRowStr;
            }
            reqAry = sRowStr.split("<>");
            if (reqAry.length > 1) { //经多文件导入时测得，有时 reqAry的长度为两个，有时为三个，有时为一个长度 by liyu 1204
                this.fileName = reqAry[0];
            }
            if (reqAry.length > 2) {
                this.fileInfo = reqAry[1];
            }
            if (reqAry.length >= 3) {
            	//---edit by songjie 2012.02.01 BUG 3732 QDV4农业银行2012年01月18日01_B start---//
            	if(!reqAry[2].equals("") && cusCfg.getEndMark() != null && cusCfg.getEndMark().trim().length() > 0){
					//若设置了结束标志，则根据结束标志截取数据再读入到临时表中
					String fileContent = reqAry[2];//获取文件内容
					//根据结束标志获取下标
					int endMarkIndex = fileContent.lastIndexOf(cusCfg.getEndMark().trim());
					//根据结束标志下标截取数据
					arrSrcFileContentData = fileContent.substring(0,endMarkIndex).split("\r\t");
            	}else{
            		arrSrcFileContentData = reqAry[2].split("\r\t");
            	}
            	//---edit by songjie 2012.02.01 BUG 3732 QDV4农业银行2012年01月18日01_B end---//
                for (int i = 0; i < arrSrcFileContentData.length; i++) {
                		//------ modify by wangzuochun 2011.02.22 BUG #1138 数据文件读入标普500指数成分股信息至证券信息表时报错；
                    	//------ LINE COUNT（行数） 此行内容不添加到hValue中；
                    	//edit by zhaoxianlin 20120823 Story #2861 QDV4赢时胜（建信基金）2012年8月9日01_A #和* （行数） 此行内容不添加到hValue中；
                        if (arrSrcFileContentData[i].trim().length() != 0 && !arrSrcFileContentData[i].startsWith("LINE COUNT")) { //判断如果此行数据为" "或为""就不要往哈希表中插入了,免得后面没处理报错 by leeyu 2008-12-17 MS00033
                           // hValue.add(arrSrcFileContentData[i]);  //modified by zhaoxianlin 20121207 #STORY #3371 
                        	list.add(arrSrcFileContentData[i]);
                        }
                	}
                	  it = list.iterator();
                while (it.hasNext()) {
                    lineContent = ( (String) it.next());
                    bufTarFileContentData.append(lineContent).append(" \r\t");
                }
                if (bufTarFileContentData.length() > 2) {
                    fileContentData = bufTarFileContentData.toString().substring(0,
                        bufTarFileContentData.toString().length() - 2);
                }
                this.fileContent = fileContentData;
            }
        } catch (Exception e) {
            throw new YssException("解悉数据出错", e);
        }

    }
}
