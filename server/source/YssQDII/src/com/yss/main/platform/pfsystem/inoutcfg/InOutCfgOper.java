package com.yss.main.platform.pfsystem.inoutcfg;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.platform.pfoper.inoutcfg.*;
import com.yss.util.*;

public class InOutCfgOper
    extends BaseBean implements IDataInterface {
    private BaseInOutCfgDeal deal = null;
    private String sInOutCodes = "";
    private String sFlag = "";
    private String sAllData = "";
    public InOutCfgOper() {
    }

    public void importData(String sRequestStr) throws YssException {
        try {
            parseRowStr(sRequestStr);
            deal = new ImportCfgDataOper();
            deal.setYssPub(pub);
            deal.init(sInOutCodes, sFlag, sAllData);
            if (sFlag.equalsIgnoreCase("import")) {
                deal.importData(sAllData);
            }
        } catch (Exception ex) {
            throw new YssException("导出数据出错", ex);
        }

    }

    public String exportData(String sRequestStr) throws YssException {
        String sResult = "";
        try {
            parseRowStr(sRequestStr);
            deal = new ExportCfgDataOper();
            deal.setYssPub(pub);
            deal.init(sInOutCodes, sFlag, sAllData);
            if (sFlag.equalsIgnoreCase("listview")) {
                sResult = deal.loadListView();
            } else if (sFlag.equalsIgnoreCase("export")) {
                sResult = deal.exportData(sAllData);
            } else {
                sResult = getOperValue("");
            }
        } catch (Exception ex) {
            throw new YssException("导出数据出错", ex);
        }
        return sResult;
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] arrRes = sRowStr.split("\f\f\f");
        this.sInOutCodes = arrRes[0];
        this.sFlag = arrRes[1];
        this.sAllData = arrRes[2];

    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        //StringBuffer buf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String sResult = "";
        try {
            if (sFlag.equalsIgnoreCase("getTabParam")) {
                deal = new ImportCfgDataOper();
                deal.setYssPub(pub);
                deal.init(sInOutCodes, sFlag, sAllData);
                sResult = deal.getOperValue(sFlag);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sResult;
    }
}
