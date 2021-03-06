package com.yss.main.operdeal.voucher;

import com.yss.dsub.*;
import com.yss.manager.*;
import com.yss.util.*;

public class BaseVchOutAcc
    extends BaseBean {

    private java.util.Date startDate;
    private java.util.Date endDate;
    private String vchTplCodes = "";

    public BaseVchOutAcc() {
    }

    public void init(String vchTplCodes,
                     java.util.Date startDate,
                     java.util.Date endDate) throws YssException {
        this.startDate = startDate;
        this.endDate = endDate;
        this.vchTplCodes = vchTplCodes;
    }

    public void insert() throws YssException {
        try {
            VoucherAdmin vchAdmin = new VoucherAdmin();
            vchAdmin.setYssPub(pub);
            vchAdmin.insert(vchTplCodes, startDate, endDate);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
    }
}
