package com.yss.main.operdeal.report.accbook.acctree;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IAccBookOper;
import java.sql.*;
import com.yss.util.*;
import java.util.*;
import com.yss.pojo.sys.YssTreeNode;
import java.util.Iterator;
import com.yss.dsub.*;

public class InvestAccTree
    extends BaseBean implements IAccBookOper {
    public InvestAccTree() {
    }

    /**
     * getBookSql
     *
     * @param sBookDefine String
     * @param sBookLink String
     * @param dBeginDate Date
     * @param dEndDate Date
     * @param iShowType int
     * @param sCheckItems String
     * @return String
     */
    public String getBookSql(String sBookDefine, String sBookLink,
                             java.util.Date dBeginDate, java.util.Date dEndDate,
                             int iShowType, String sCheckItems) {
        return "";
    }

    /**
     * setBookClassTable
     *
     * @param sBookDefine String
     * @param iShowType int
     */
    public void setBookClassTable(String sBookDefine, int iShowType) {
    }
}
