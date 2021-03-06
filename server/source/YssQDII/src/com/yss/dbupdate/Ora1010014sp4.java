package com.yss.dbupdate;

import java.sql.*;
import com.yss.util.*;

public class Ora1010014sp4
    extends BaseDbUpdate {
    public Ora1010014sp4() {
    }

    //--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//
    public void addTableField(String sPre) throws YssException {
        try {
            dbl.executeSql(
                "ALTER TABLE TB_" + sPre + "_DATA_FUTTRADERELA add FPortCode VARCHAR2(20)");
        } catch (Exception e) {
            throw new YssException("版本1.0.1.0014增加表字段出错！", e);
        }
        //--MS00273 QDV4中金2009年02月27日01_A add by songjie 2009.03.23-----//
    }
}
