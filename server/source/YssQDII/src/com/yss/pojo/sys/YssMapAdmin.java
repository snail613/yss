package com.yss.pojo.sys;

import java.util.*;
import com.yss.util.*;
import com.yss.main.dao.*;

public class YssMapAdmin {
    private HashMap hm;
    private Comparator comp;
    public YssMapAdmin(HashMap hm) {
        this.hm = hm;
    }

    public YssMapAdmin(HashMap hm, Comparator comp) {
        this.hm = hm;
        this.comp = comp;
    }

    public ArrayList sortMap() throws YssException {
        ArrayList alResult = new ArrayList();
        alResult.addAll(hm.values());
        Collections.sort(alResult, comp);
        return alResult;
    }

    public String toYssString() throws YssException {
        return toYssString(true, YssCons.YSS_LINESPLITMARK);
    }

    public String toYssString(boolean bSort) throws YssException {
        return toYssString(bSort, YssCons.YSS_LINESPLITMARK);
    }

    public String toYssString(boolean bSort, String splitMark) throws YssException {
        ArrayList sortList = null;
        Object listItem = null;
        StringBuffer buf = new StringBuffer();
        String strReturn = "";
        if (!bSort) {
            sortList = sortMap();
        }
        //huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        if(sortList != null){
	        Iterator iter = sortList.iterator();
	        while (iter.hasNext()) {
	            listItem = iter.next();
	            if (listItem instanceof IDataSetting) {
	                buf.append( ( (IDataSetting) listItem).buildRowStr())
	                    .append(splitMark);
	            }
	        }
        }
        //---end---
        if (buf.toString().length() > splitMark.length()) {
            strReturn = buf.toString().substring(0,
                                                 buf.toString().length() - splitMark.length());
        }
        return strReturn;
    }

}
