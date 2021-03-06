package com.yss.main.operdeal.datainterface;

import java.util.*;

import com.yss.main.datainterface.*;
import com.yss.util.*;

// 此方法用于处理通用接口XML格式文件的导出
// create 2007-10-16
public class ExpCusXMLInterface
    extends BaseDaoOperDeal {
    private static String sCusCfg = "";
    public ExpCusXMLInterface() {
    }

    public String doInterface() throws YssException {
        return "";
    }

    public String builderXML(DaoCusConfigureBean cusCfg, String sValue, String sStruct, String sFields) throws YssException {
        //根据XML的结构与数据来创建一个XML文件,
        // cusCfg 当前的接口 ,
        //sValue XML值 是普通值 多条数据用\r\n分隔 字段与字段用逗号分隔,
        // sStruct 是Xml的结构,
        //sFields 是最终表的字段集 与一条值对应 逗号分隔
        StringBuffer buf = new StringBuffer();
        String[] sOneValue = null;
        try {
            if (sStruct.trim().length() == 0) {
                throw new YssException("未找到XML的结构文件数据");
            }
            sOneValue = sValue.split("\r\n");
            for (int i = 0; i < sOneValue.length; i++) {
                HashMap hmValue = hmBuildValues(sFields, sOneValue[i]);
                String s = builderStruct(sStruct, hmValue, sFields);
                System.out.println("============xml===============");
                System.out.println(s);
                buf.append("\f\f");
            }
            if (buf.toString().length() > 2) {
                buf.delete(buf.length() - 2, buf.length());
            }
            return buf.toString();
        } catch (Exception e) {
            throw new YssException("解析XML文件出错", e);
        }
    }

    private HashMap hmBuildValues(String sField, String sValue) throws Exception {
        HashMap htRes = new HashMap();
        String[] field = sField.split(",");
        String[] value = sValue.split(",");
        try {
            for (int i = 0; i < field.length; i++) {
                htRes.put(field[i].toUpperCase(), value[i]);
            }
            return htRes; //将内容与字段绑定
        } catch (Exception e) {
            throw new Exception("内容与字段绑定失败", e);
        }
    }

    private String builderStruct(String sStruct, HashMap hmValue, String sField) throws YssException {
        //根据XML的结构写出它的结构图
        //返回一个XML的文件内容
        String[] Struct = null;
        String[] StrRoot = null;
        String sResult = "";
        String root = ""; //根结点
        try {
            Struct = sStruct.split(","); //将串分开,显示出它的结构
            StrRoot = new String[sField.split(",").length];
            root = Struct[0].substring(1, Struct[0].indexOf(">")); //取第一行的第一个,默认
            StrRoot = buileRoot(Struct, sField);
            sResult = writeXMLStruct(StrRoot, sField, hmValue, root);
            return sResult;
        } catch (Exception e) {
            throw new YssException("创建XML的结构格式出错", e);
        }
    }

    private String writeXMLStruct(String[] sRoots, String sField,
                                  HashMap hmValue, String Root) throws
        YssException {
        //写出XML文件的格式,并将值都赋上.目前还不能处理属性,多条交叉的数据
        //sRoots 各字段对应的路径 每一个为根节点 最后一个为最终节点 保存Text的值,
        //sField 为字段集 与sRoots里的记录对应,
        //hmValue 为单条值 key与 sField中的字段相同
        // Root 为根结点
        //返回 一个XML的内容文本 包括它的层次关系
        StringBuffer sResult = new StringBuffer();
        try {
            sResult.append("<" + Root + ">");
            sResult.append(writeOneRoot(sField, hmValue, sRoots));
            sResult.append("</" + Root + ">");
            return sResult.toString();
        } catch (Exception e) {
            throw new YssException("写XML格式出错", e);
        }
    }

    private String writeOneRoot(String sField, HashMap hmValue, String[] sRoots) throws
        YssException {
        //本方法是写一个XML的完整路径,包括子节点下加TAB等.
        //返回到前台的是一个完整的XML文件,此类是关键的一个类
        //sField 所有的字段,
        //hmValue 对应的值
        //sRoots 为所有的路径
        String[] field = sField.split(",");
        String sTmp = "";
        String sResult = "";
        int iTmp = 0;
        try {
            for (int i = 0; i < field.length; i++) {
                iTmp = 0;
                sTmp = sRoots[i].substring(0, sRoots[i].lastIndexOf("<"));
                if (sTmp.length() == 0) {
                    if (hmValue.get(field[i]).toString().trim().length() != 0) {
                        sResult += "\r\n<" + field[i] + ">" + (String) hmValue.get(field[i]) + "</" + field[i] + ">";
                    } else {
                        sResult += "\r\n<" + field[i] + "/>";
                    }
                    continue;
                }
                for (int j = 0; j < sRoots.length; j++) {
                    if (sRoots[j].indexOf(sTmp) > -1) {
                        iTmp++;
                    }
                }
                String[] arrTmp = new String[iTmp];
                String[] arrField = new String[iTmp];
                HashMap hmTmp = new HashMap();
                iTmp = 0;
                for (int j = 0; j < sRoots.length; j++) {
                    if (sRoots[j].indexOf(sTmp) > -1) {
                        arrField[iTmp] = field[j];
                        arrTmp[iTmp] = sRoots[j];
                        hmTmp.put(field[j], "" + iTmp++);
                    }
                }
                if (!hmTmp.get(field[i]).toString().equals("0")) {
                    continue; //相同路径下的字段只执行一次
                }
                sResult += writeSameRoot(arrField, arrTmp, hmValue);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("写一个XML格式出错", e);
        }
    }

    private String writeSameRoot(String[] sField, String[] sameRoot, HashMap hmValues) throws YssException {
        //这个方法是将相同交节点的所有子节点放在一起,可能写的有问题 add liyu 1023 这个方法需重写
        //这时可以顺序地将这些节点遍历一下，并写出它的字符串
        //如<SEQA><SEME> <SEQA><FUNCTION> <SEQA><DATE> <SEQA><SEQA1><LINK_REFER>
        //返回结果是\r\n<SEQA>\r\n<SEME/>\r\n<FUNCTION/>\r\n<SEQA1>\r\n<LINK_REFER/>\r\n</SEQA1>\r\n</SEQA>
        sameRoot[0] = sameRoot[0].substring(0, sameRoot[0].lastIndexOf("<")); //当前根节点
        String[] arrTmp = sameRoot[0].split(">");
        String sResult = "\r\n";
        String sTmpValue = "";
        try {
            for (int iTmp = 0; iTmp < arrTmp.length; iTmp++) {
                arrTmp[iTmp] = arrTmp[iTmp].replaceAll("<", "");
            }
            for (int j = 0; j < arrTmp.length; j++) {
                sResult += "<" + arrTmp[j] + ">\r\n";
            } //顺下
            for (int i = 0; i < sameRoot.length; i++) {
                sTmpValue = (String) hmValues.get(sField[i]);
                if (sTmpValue.trim().length() == 0) { //若无值
                    sResult += "<" + sField[i] + "/>\r\n";
                } else {
                    sResult += "<" + sField[i] + ">" + sTmpValue + "</" + sField[i] + ">\r\n";
                }
            } //取值,赋值
            for (int k = arrTmp.length - 1; k >= 0; k--) {
                sResult += "</" + arrTmp[k] + ">\r\n"; //+sResult;
            } //逆下
            if (sResult.length() > 2) {
                sResult = sResult.substring(0, sResult.length() - 2);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException("写同级XML格式出错", e);
        }
    }

    private String[] buileRoot(String[] strStruct, String sFileds) throws YssException {
        //本方法获得XML每个文本的路径
        //strStruct[] 为XML的路径集, 如<MT541><SWIFT><SEQA>  <SEQA><SEQA1>..
        //htValue为字段-值
        //sField为字段集
        //返回 [0]<SWIFT> [1]<SEQA><SEQA1><DATE> [2] <SEQA><SEQA1><FIN>...
        String[] field = sFileds.split(",");
        String[] sPath = new String[field.length]; //保存各个Text的完整路径
        String str = "";
        try {
            for (int i = 0; i < field.length; i++) {
                //此次找出最下一个节点,因为它是 filed[i] text 类型的
                //根据一个字段找出具有字段名的路径 如 DATE --><SEQA1><DATE><FIN><FIN_NAME>--><SEQA><SEQA1>--><MT541><SEQA>
                //每次都会根据第一个字段来找
                str = findAllPath(strStruct, field[i]); //str 获取完整的路径
                if (str.indexOf("<" + field[i] + ">") < str.length()) {
                    str = str.substring(0, str.indexOf("<" + field[i] + ">") + ("<" + field[i] + ">").length()); //去掉本路径后的内容,使成为一个真正的路径
                }
                //如 <MT541><SWIFT><SEQA><SEQA1><DATE><FIN><FIN_NAME> 如果是 <DATE> 得到 <SEQA><SEQA1><DATE>
                //删除具有Text的最底节点
                for (int j = 0; j < field.length; j++) {
                    str = str.replaceFirst( ("<" + field[j] + ">"), "");
                }
                str = str.substring(str.indexOf(">") + 1, str.length());
                sPath[i] = str + "<" + field[i] + ">";
            }
            return sPath;
        } catch (Exception e) {
            throw new YssException("处理XML内容到格式有误", e);
        }
    }

    private String findAllPath(String[] strStruct, String field) throws YssException {
        //本方法会根据 单一field在路径节段中查找路径,并拼出完整的路径,采用递归方法
        String sResult = "";
        String sFirst = ""; //获取第一个名称
        String sTmp = "";
        try {
            for (int i = 0; i < strStruct.length; i++) {
                if (strStruct[i].indexOf("<" + field + ">") > 0) {
                    sTmp = strStruct[i].substring(0, strStruct[i].indexOf(">") + 1) + "<" + field + ">"; //取出第一个与本身field
                    sFirst = sTmp.substring(1, strStruct[i].indexOf(">"));
                    sResult += sTmp.substring(sTmp.indexOf(">") + 1, sTmp.length()); //去掉第一个
                    sResult = findAllPath(strStruct, sFirst) + sResult;
                } else {
                    continue;
                }
            }
            if (sResult.length() == 0) {
                sResult = "<" + field + ">"; //获得顶级节点
            }
            return sResult; //返回的是 <MT541><SWIFT><SEQA><SEQA1><DATE><FIN><FIN_NAME> 如果是 <DATA>
        } catch (Exception e) {
            throw new YssException("查找XML字段出错", e);
        }
    }

} //end class
