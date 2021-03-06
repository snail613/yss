package com.yss.commeach;

import com.yss.util.YssException;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssCons;
import com.yss.main.operdeal.platform.pfoper.pubpara.CtlPubPara;
import java.util.HashMap;

/**
 * <p>Title: 从词汇数据中根据指定的类型获取词汇 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EachVocabulary
    extends BaseCommEach {
    public EachVocabulary() {
    }

    public void parseRowStr(String sRowStr) throws YssException {

    }

    public String getOperValue(String sType) throws YssException {
        String sVocStr = "";
        VocabularyBean vocabulary = new VocabularyBean();
        try {
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(sType);
            //如果是获取编码类型，则把默认编码类型放到字符串开始位置
            if (sType.equalsIgnoreCase(YssCons.YSS_ENCODING_TYPE)) {
                StringBuffer buf = new StringBuffer();
                CtlPubPara pubPara = new CtlPubPara();
                pubPara.setYssPub(pub);
                String ent = pubPara.getEncodingType();
                String[] arryitems = sVocStr.split(YssCons.YSS_ITEMSPLITMARK2);
//            //如果设置参数有默认编码类型，则把默认编码类型放到字符串开始位置
//            if (ent != null && ent.trim().length() > 0) {
//               for (int i = 0; i < arryitems.length; i++) {
//                  String[] arry = arryitems[i].split("\t");
//                  if (arry[0].equalsIgnoreCase(ent)) {
//                     buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//                  }
//               }
//            }
//            //以下是按一定顺序排列编码类型
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("default")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("utf-7")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("utf-8")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("utf-16")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("utf-32")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("unicode")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            for (int i = 0; i < arryitems.length; i++) {
//               String[] arry = arryitems[i].split("\t");
//               if (arry[0].equalsIgnoreCase("ascii")) {
//                  buf.append(arryitems[i]).append(YssCons.YSS_ITEMSPLITMARK2);
//               }
//            }
//            if (buf.toString().length() > 1) {
//               buf = new StringBuffer(buf.toString().substring(0,
//                     buf.toString().length() - 1));
//            }
//            sVocStr = buf.toString();

                sVocStr = "";
                HashMap hm = new HashMap();
                int i;
                for (i = 0; i < arryitems.length; i++) {
                    String[] arry = arryitems[i].split("\t");
                    if (arry[0].equalsIgnoreCase(ent)) {
                        sVocStr = arryitems[i] + YssCons.YSS_ITEMSPLITMARK2;
                    } else if (arry[0].equalsIgnoreCase("default")) {
                        hm.put("0", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("utf-7")) {
                        hm.put("1", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("utf-8")) {
                        hm.put("2", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("utf-16")) {
                        hm.put("3", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("utf-32")) {
                        hm.put("4", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("unicode")) {
                        hm.put("5", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else if (arry[0].equalsIgnoreCase("ascii")) {
                        hm.put("6", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    } else {
                        hm.put(i + 6 + "", arryitems[i] + YssCons.YSS_ITEMSPLITMARK2);
                    }
                }
                for (int j = 0; j < i + 7; j++) {
                    if (hm.get(j + "") != null && hm.get(j + "").toString().length() > 0) {
                        sVocStr += hm.get(j + "").toString();
                    }
                }
                if (sVocStr.length() > 1) {
                    sVocStr = sVocStr.substring(0, sVocStr.length() - 1);
                }
            }
        } catch (Exception e) {
            throw new YssException("获取词汇对照信息出错", e);
        }
        return sVocStr;
    }
}
