package com.yss.main.operdeal.bond;

import com.yss.util.*;

/**
 * <p>Title: </p>
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
public class BondBuildDynmic {
    public BondBuildDynmic() {
    }

    public String buildDynmic(String CalcFormula) throws YssException {
        String calcStr = "";
        StringBuffer buf = new StringBuffer();
        buf.append("package com.yss.base;").append("\n\n");
        buf.append("import com.yss.util.YssFun;").append("\n");
        buf.append("import com.yss.util.YssException;").append("\n");
        buf.append("import java.sql.SQLException;").append("\n");
        buf.append("import com.yss.dsub.YssPub;").append("\n");
        buf.append("import java.sql.ResultSet;").append("\n");
        buf.append("import com.yss.dsub.BaseBean;").append("\n\n");

        buf.append("public class CalcBond extends BaseBean{").append("\n");
        //buf.append("private YssPub pub = null;").append("\n");
        buf.append("public CalcBond() {").append("\n");
        buf.append("}").append("\n\n");

        buf.append("public void setPub(YssPub pub)").append("\n");
        buf.append("{").append("\n");
        buf.append(" setYssPub(pub);").append("\n");
        buf.append("}").append("\n");

        buf.append("public double reBondValue() throws YssException,SQLException ").append("\n");
        buf.append("{").append("\n");

        //buf.append("String reStr = \"\";").append("\n");
        buf.append("ResultSet rs = null;").append("\n");
        buf.append("double result = 0.0;").append("\n");

        buf.append(CalcFormula);
        buf.append("").append("\n");
        buf.append(" if(rs.next())").append("\n");
        buf.append("{").append("\n");
        buf.append("result = rs.getDouble(1);").append("\n");
        buf.append("}").append("\n");
        buf.append(" dbl.closeResultSetFinal(rs);").append("\n");
        buf.append("return result;").append("\n");
        buf.append("}").append("\n");

        buf.append("}");

        calcStr = buf.toString();
        buf.delete(0, buf.length());
        return replaceSqlRs(calcStr);
    }

    private String replaceSqlRs(String calcStr) {
        String reStr = "";
        String tabName = "Tb_Sys_UserList";
        reStr = calcStr.replaceAll("]", " from " + tabName + "\")");
        reStr = reStr.replaceAll("sqlRs", "rs = dbl.openResultSet(\"select ");
        reStr = reStr.replace('[', ' ');
        return reStr;
    }

}
