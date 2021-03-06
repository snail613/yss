package com.yss.main.operdeal.datainterface.swift;

import com.yss.main.datainterface.swift.*;
import com.yss.util.YssException;
import java.util.*;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * by leeyu 20090610
 * <p>Title: 导入MT545报文的处理类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SwiftInputISO15022MT545
    extends SwiftInputISO15022 {

    public SwiftInputISO15022MT545() {
    }
    /**
     * 说明：MT545报文较特殊，一个报文中可能会有多行记录情况：匹配方式为：
     * :16R:LINK块与下面的:16R:FIAC对应，采用且16R:LINK块中的:22F::LINK//XXX　与:16R:FIAC块中的:13B::CERT//XXX 对应
     * 若报文只有一行记录数据时，报文内容中就可以没有:13B::CERT//XXX，此时系统自动与:16R:LINK块关联
     */
    public SaveSwiftContentBean splitDatas(SaveSwiftContentBean saveContent, ArrayList alSwiftTemp) throws YssException {
        SaveSwiftContentBean saveSwift; //解析新的N条报文数据
        SaveSwiftContentBean firstSaveSwift = null; //第一条报文数据
        DaoSwiftEntitySet rowEntity; //每行报文模板
        //ArrayList alTempClone =null;   //报文模板的克隆
        String firstSwift = ""; //解析完的第一个报文记录
        String otherSwift = ""; //解析完的除第一个报文外的记录
        //String pubSwift="";     //公共的报文信息，
        StringBuffer pubBuf = new StringBuffer(); //公共的报文信息，
        String sRowStr = ""; //读出来的一行数据
        String[] arrData = null; //保存报文数据的，按\r\n将数据先分开

        ArrayList alMulSwift = new ArrayList();
        String mulLinkSwift = "";
        String mulFiacSwift = "";
        int beginRow = 0;
        try {
            swiftType = "MT545";
            if (saveContent.getSwiftText().toUpperCase().indexOf(":16R:LINK") == -1) {
                return saveContent;
            }
            //saveSwift =(SaveSwiftContentBean)saveContent.clone();//先将数据克隆过来
            //alTempClone =(ArrayList)alSwiftTemp.clone();//将模板信息也克隆出来
            arrData = saveContent.getSwiftText().split("\r\n");
            for (int iRow = 0; iRow < arrData.length; iRow++) {
                if (!arrData[iRow].startsWith(":")) { //如果数据不以":"开始的话就读下一行，因为本行数据是上一个模板的数据部分
                    continue;
                } else {
                    beginRow = iRow;
                }
                sRowStr = getRowContent(arrData, beginRow);
                rowEntity = (DaoSwiftEntitySet) parseEntityStr(sRowStr);
                if (!sRowStr.endsWith("\r\n")) {
                    sRowStr += "\r\n";
                }
                if (rowEntity.getSTag().equalsIgnoreCase("16R") &&
                    rowEntity.getSContent().equalsIgnoreCase("link")) {
                    //连续向下读一直到:16S:LINK
                    for (int iLink = iRow; iLink < arrData.length; iLink++) {
                        if (!arrData[iLink].startsWith(":")) {
                            continue;
                        } else {
                            beginRow = iLink;
                        }
                        sRowStr = getRowContent(arrData, beginRow);
                        rowEntity = (DaoSwiftEntitySet) parseEntityStr(sRowStr);
                        if (!sRowStr.endsWith("\r\n")) {
                            sRowStr += "\r\n";
                        }
                        if (sRowStr.startsWith(":22F::LINK")) {
                            mulLinkSwift = rowEntity.getSContent() + "\f\f" + mulLinkSwift;
                        }
                        if (sRowStr.startsWith(":16S:LINK")) {
                            mulLinkSwift = mulLinkSwift + arrData[iLink];
                            alMulSwift.add(mulLinkSwift);
                            sRowStr = "<link" + (alMulSwift.size() - 1) + ">";
                            mulLinkSwift = "";
                            iRow = beginRow;
                            break;
                        }
                        mulLinkSwift = mulLinkSwift + sRowStr;
                    } //end for
                } //end link;
                if (rowEntity.getSTag().equalsIgnoreCase("16R") &&
                    rowEntity.getSContent().equalsIgnoreCase("FIAC")) {
                    //连续向下读一直到:16S:FIAC
                    String sTmpMulStr = "";
                    int row = 0;
                    for (int iFiac = iRow; iFiac < arrData.length; iFiac++) {
                        if (!arrData[iFiac].startsWith(":")) {
                            continue;
                        } else {
                            beginRow = iFiac;
                        }
                        sRowStr = getRowContent(arrData, beginRow);
                        rowEntity = (DaoSwiftEntitySet) parseEntityStr(sRowStr);
                        if (!sRowStr.endsWith("\r\n")) {
                            sRowStr += "\r\n";
                        }
                        if (rowEntity.getSTag().equalsIgnoreCase("13B") &&
                            arrData[iFiac].startsWith(":13B::CERT")) {
                            String value = arrData[iFiac].substring(10);
                            for (row = 0; row < alMulSwift.size(); row++) {
                                sTmpMulStr = (String) alMulSwift.get(row);
                                String[] arrTmpMul = sTmpMulStr.split("\f\f");
                                if (arrTmpMul[0].equalsIgnoreCase(value)) {
                                    break;
                                }
                            }
                        }
                        if (arrData[iFiac].startsWith(":16S:FIAC")) {
                            mulFiacSwift += arrData[iFiac];
                            alMulSwift.remove(sTmpMulStr);
                            if(sTmpMulStr==null||sTmpMulStr.trim().length()==0){
                            	//如果此时本字符串为空，则说明报文中没有":13B::CERT",则直接取alMulSwift的第０个数据,此种情况只适用于报文中只有一行记录的情况
                            	// byleeyu 20100107
                            	sTmpMulStr=(String)alMulSwift.get(0);
                            	alMulSwift.remove(0);
                            }
                            sTmpMulStr = sTmpMulStr + "\f\f" + mulFiacSwift + "\f\fnull";
                            alMulSwift.add(sTmpMulStr);
                            sRowStr = "<fiac" + row + ">";
                            mulFiacSwift = "";
                            iRow = beginRow;
                            break;
                        }
                        mulFiacSwift = mulFiacSwift + sRowStr;
                    } //end for
                } //end fiac
                pubBuf.append(sRowStr); //重新将报文内容编起来
            }
            if (pubBuf.length() > 2) {
                pubBuf.setLength(pubBuf.length() - 2); //去掉最后面的\r\n
            }
            saveContent.setSwiftText(pubBuf.toString());
            String[] arrTmpStr = null; //临时变量
            for (int i = 0; i < alMulSwift.size(); i++) {
                sRowStr = (String) alMulSwift.get(i);
                arrTmpStr = sRowStr.split("\f\f");
                if (i == 0) { //当i==0时，为第一条
                    firstSaveSwift = (SaveSwiftContentBean) saveContent.clone(); //先将数据克隆过来
                    firstSwift = firstSaveSwift.getSwiftText();
                    firstSwift = firstSwift.replaceAll("<link0>", arrTmpStr[1] + "\r\n");
                    firstSwift = firstSwift.replaceAll("<fiac0>", arrTmpStr[2] + "\r\n");
                    firstSaveSwift.setSwiftText(replace(alMulSwift, 0, firstSwift));
                } else {
                    saveSwift = (SaveSwiftContentBean) saveContent.clone(); //先将数据克隆过来
                    otherSwift = saveSwift.getSwiftText();
                    otherSwift = otherSwift.replaceAll("<link" + i + ">", arrTmpStr[1] + "\r\n");
                    otherSwift = otherSwift.replaceAll("<fiac" + i + ">", arrTmpStr[2] + "\r\n");
                    saveSwift.setSwiftText(replace(alMulSwift, i, otherSwift));
                    alSavelist.add(saveSwift);
                }
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        }
        return firstSaveSwift;
    }

    /**
     * 替换掉除row序号的特殊符号
     * @param alSwift ArrayList
     * @param row int
     * @param rowSwift String
     * @return String
     */
    private String replace(ArrayList alSwift, int row, String rowSwift) {
        for (int i = 0; i < alSwift.size(); i++) {
            if (i != row) {
                rowSwift = rowSwift.replaceAll("<link" + i + ">", "");
                rowSwift = rowSwift.replaceAll("<fiac" + i + ">", "");
            }
        }
        return rowSwift;
    }

}
