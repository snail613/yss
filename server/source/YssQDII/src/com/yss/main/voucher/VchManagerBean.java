package com.yss.main.voucher;

import com.yss.dsub.*;
import com.yss.main.dao.*;
import com.yss.main.operdeal.voucher.*;
import com.yss.util.*;

public class VchManagerBean
    extends BaseBean implements IClientOperRequest {

    private String strStartDate;
    private String strEndDate;
    private String portCodes = "";
    private String attrTypes = "";
    private String beanId = "";

    private String param = "";

    private String vchTplCodes = "";

    public VchManagerBean() {
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
        String[] reqAry = null;
        try {
            if (sRowStr.trim().length() == 0) {
                return;
            }

            this.vchTplCodes = sRowStr.split("\r\f")[1];

            reqAry = sRowStr.split("\r\f")[0].split("\t");

            this.strStartDate = reqAry[0];
            this.strEndDate = reqAry[1];
            this.portCodes = reqAry[2]; //由，间隔
            this.attrTypes = reqAry[3];
            this.beanId = reqAry[4];
        } catch (Exception e) {
            throw new YssException("生成凭证请求信息出错\r\n" + e.getMessage(), e);
        }
    }

    /**
     * checkRequest
     *
     * @return String
     */
    public String checkRequest(String sType) {
        return "";
    }

    /**
     * doOperation
     *
     * @param sType String
     * @return String
     */
    public String doOperation(String sType) throws YssException {
        String strError = "";
        java.util.Date dStartDate = null;
        java.util.Date dEndDate = null;
        String reResult = "";
        try {
            if (sType.equalsIgnoreCase("vchbuild")) { //生成凭证
                dStartDate = YssFun.toDate(this.strStartDate);
                dEndDate = YssFun.toDate(this.strEndDate);
                BaseVchBuilder vchBuilder = (BaseVchBuilder) pub.
                    getVoucherCtX().getBean(beanId);
                vchBuilder.setYssPub(pub);
                vchBuilder.init(portCodes, dStartDate, dEndDate, attrTypes);
                vchBuilder.doVchBuilder();
            } else if (sType.equalsIgnoreCase("vchcheck")) { //检查凭证
                BaseVchChecker vchChecker = (BaseVchChecker) pub.
                    getVoucherCtX().getBean(beanId);
                vchChecker.setYssPub(pub);
                vchChecker.init(this.vchTplCodes);
                reResult = vchChecker.doVchCheck();
            } else if (sType.equalsIgnoreCase("vchoutacc")) { //导入财务系统
                dStartDate = YssFun.toDate(this.strStartDate);
                dEndDate = YssFun.toDate(this.strEndDate);
                BaseVchOutAcc vchOutAcc = (BaseVchOutAcc) pub.
                    getVoucherCtX().getBean(beanId);
                vchOutAcc.setYssPub(pub);
                vchOutAcc.init(this.vchTplCodes, dStartDate, dEndDate);
                vchOutAcc.insert();
            } else if (sType.equalsIgnoreCase("vchoutmdb")) { //导出mdb文件
                BaseVchOutMdb vchOutMdb = (BaseVchOutMdb) pub.
                    getVoucherCtX().getBean(beanId);
                vchOutMdb.setYssPub(pub);
                String ret = "";
                vchOutMdb.init(this.vchTplCodes);
                ret = vchOutMdb.getMdbData();
                reResult += ret;
            } else if (sType.equalsIgnoreCase("vchfrommdb")) { //导入到系统数据库
                BaseVchOutMdb fromMdb = new BaseVchOutMdb();
                fromMdb.setYssPub(pub);
                reResult = fromMdb.setFromMdbData(this.vchTplCodes);
            }
            return reResult;
        } catch (Exception e) {
            throw new YssException(strError + "\r\n" + e.getMessage(), e);
        } finally {

        }

    }
}
