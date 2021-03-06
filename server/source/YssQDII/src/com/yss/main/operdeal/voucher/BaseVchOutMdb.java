package com.yss.main.operdeal.voucher;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.voucher.*;
import com.yss.util.*;

public class BaseVchOutMdb
    extends BaseBean {
    private String vchTplCodes = "", vchEntityCodes = "";
    private String[] vchTplCode;

    public void getTplCode() {
        vchTplCode = vchTplCodes.split(",");
    }

    public BaseVchOutMdb() {
    }

    public void init(String vchTplCodes) {
        this.vchTplCodes = vchTplCodes;
    }

    public String getMdbData() throws YssException {
        String reStr = "";
        try {
            this.getTplCode();
            reStr = this.getOtherMdbData(vchTplCodes);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        return reStr;
    }

    private String setSuperRel() {
        StringBuffer buf = new StringBuffer();
        buf.append("1").append("\t");
        buf.append("").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ").append("\t");
        buf.append(" ");
        return buf.toString();
    }

    private void deleteDb(String sql) throws YssException {
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            throw new YssException(ex.getMessage() + "\r\n" + "删除表记录失败");
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    private String doNull(String str) {
        StringBuffer buf = new StringBuffer();
        String[] sArr = str.split("\t");
        for (int i = 0; i < sArr.length; i++) {
            if (sArr[i].length() == 0) {
                sArr[i] = " ";
            }
            buf.append(sArr[i]).append("\t");
        }
        buf.append("null");
        return buf.toString();
    }

    public String setFromMdbData(String strDatas) throws YssException { //从MDB导入到数据库
        String str = "", delSql = "";
        String[] sArr = strDatas.split("\b\b");
        String[] sTpls = sArr[0].split("\r\t");
        String[] sEntitys = sArr[1].split("\r\t");
        String[] sResumes = sArr[2].split("\r\t");
        String[] sSubjects = sArr[3].split("\r\t");
        String[] sDss = sArr[4].split("\r\t");
        String[] sFields = sArr[5].split("\r\t");
        String[] sAttrs = sArr[6].split("\r\t");
        String[] sLinks = sArr[7].split("\r\t");
        String[] sMas = sArr[8].split("\r\t");
        String[] sAssistants = sArr[9].split("\r\t");
        try {
            for (int i = 0; i < sTpls.length; i++) {
                VchTplBean tpl = new VchTplBean();
                tpl.setYssPub(pub);
                if (sTpls[i].equalsIgnoreCase("null")) {
                    continue;
                }
                tpl.parseRowStr(doNull(sTpls[i]));
                delSql = "delete from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                    " where FVchTplCode =" + dbl.sqlString(tpl.getVchTplCode());
                this.deleteDb(delSql);
                tpl.addSetting();
                tpl.checkStateId = 1;
                tpl.checkSetting();
                vchTplCodes += tpl.getVchTplCode() + ",";
            }
            if (vchTplCodes.length() > 0) {
                vchTplCodes = vchTplCodes.substring(0, vchTplCodes.length() - 1);
            }
            for (int i = 0; i < sEntitys.length; i++) {
                VchEntityBean entity = new VchEntityBean();
                entity.setYssPub(pub);
                if (sEntitys[i].equalsIgnoreCase("null")) {
                    continue;
                }
                entity.parseRowStr(doNull(sEntitys[i]));
                delSql = "delete from " + pub.yssGetTableName("Tb_Vch_Entity") +
                    " where FVchTplCode =" + dbl.sqlString(entity.getVchTplCode()) +
                    " and FEntityCode =" + dbl.sqlString(entity.getEntityCode());
                this.deleteDb(delSql);
                entity.addSetting();
                entity.checkStateId = 1;
                entity.checkSetting();
                vchEntityCodes += entity.getEntityCode() + ",";
            }
            if (vchEntityCodes.length() > 0) {
                vchEntityCodes = vchEntityCodes.substring(0,
                    vchEntityCodes.length() - 1);
            }

            delSql = "delete from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) + ")" +
                " and FEntityCode in (" + operSql.sqlCodes(vchEntityCodes) + ")";
            this.deleteDb(delSql);
            for (int i = 0; i < sResumes.length; i++) {
                VchEntityResumeBean resume = new VchEntityResumeBean();
                resume.setYssPub(pub);
                if (sResumes[i].equalsIgnoreCase("null")) {
                    continue;
                }
                resume.parseRowStr(doNull(sResumes[i]));
                String strSql = "insert into " +
                    pub.yssGetTableName("Tb_Vch_EntityResume") +
                    "(FVchTplCode,FEntityCode,FOrderNum," +
                    " FResumeDict,FResumeField,FResumeConent,FValueType,FDesc," +
                    " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values( " +
                    dbl.sqlString(resume.getVchTplCode().trim()) + "," +
                    dbl.sqlString(resume.getEntityCode().trim()) + "," +
                    (i + 1) + "," +
                    dbl.sqlString(resume.getDictCode().trim()) + "," +
                    dbl.sqlString(resume.getResumeFieldCode().trim()) + "," +
                    dbl.sqlString(resume.getResumeConent().trim()) + "," +
                    dbl.sqlString(resume.getValueType().trim()) + "," +
                    dbl.sqlString(resume.getDesc().trim()) + "," +
                    "1,' ',' ',' ' " + " )";
                dbl.executeSql(strSql);
                // resume.addSetting();
                //   resume.checkStateId=1;
                //  resume.checkSetting();
            }
            delSql = "delete from " + pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) + ")" +
                " and FEntityCode in (" + operSql.sqlCodes(vchEntityCodes) + ")";
            this.deleteDb(delSql);
            for (int i = 0; i < sSubjects.length; i++) {
                VchEntitySubjectBean subject = new VchEntitySubjectBean();
                subject.setYssPub(pub);
                if (sSubjects[i].equalsIgnoreCase("null")) {
                    continue;
                }
                subject.parseRowStr(doNull(sSubjects[i]));
                String strSql = "insert into " +
                    pub.yssGetTableName("Tb_Vch_EntitySubject") +
                    "(FVchTplCode,FEntityCode,FOrderNum," +
                    " FSubjectConent,FSubjectField,FSubjectDict,FValueType,FDesc," +
                    " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values( " +
                    dbl.sqlString(subject.getVchTplCode().trim()) + "," +
                    dbl.sqlString(subject.getEntityCode().trim()) + "," +
                    (i + 1) + "," +
                    dbl.sqlString(subject.getSubjectConnet().trim()) + "," +
                    dbl.sqlString(subject.getSubjectFieldCode().trim()) + "," +
                    dbl.sqlString(subject.getSubjectDictCode().trim()) + "," +
                    dbl.sqlString(subject.getValueType().trim()) + "," +
                    dbl.sqlString(subject.getDesc().trim()) + "," +
                    "1,' ',' ',' '" +
                    " )";
                dbl.executeSql(strSql);
                //subject.addSetting();
                //subject.checkStateId =1;
                // subject.checkSetting();
            }
            for (int i = 0; i < sDss.length; i++) {
                VchDataSourceBean ds = new VchDataSourceBean();
                ds.setYssPub(pub);
                if (sDss[i].equalsIgnoreCase("null")) {
                    continue;
                }
                ds.parseRowStr(doNull(sDss[i]));
                delSql = "delete from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                    " where FVchDsCode =" + dbl.sqlString(ds.getVchDsCode());
                this.deleteDb(delSql);
                ds.addSetting();
                ds.checkStateId = 1;
                ds.checkSetting();

            }
            for (int i = 0; i < sFields.length; i++) {
                VchDsTabFieldBean field = new VchDsTabFieldBean();
                field.setYssPub(pub);
                if (sFields[i].equalsIgnoreCase("null")) {
                    continue;
                }
                field.parseRowStr(doNull(sFields[i]));
                delSql = "delete from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                    " where FVchDsCode =" + dbl.sqlString(field.getDsVchDsCode()) +
                    " and FAliasName =" + dbl.sqlString(field.getAliasName());
                this.deleteDb(delSql);
                field.addSetting();
                field.checkStateId = 1;
                field.setOldFieldName(field.getFieldName());
                field.setOldVchDsCode(field.getDsTplCode());
                field.checkSetting();
            }
            for (int i = 0; i < sAttrs.length; i++) {
                VchAttrBean attr = new VchAttrBean();
                attr.setYssPub(pub);
                if (sAttrs[i].equalsIgnoreCase("null")) {
                    continue;
                }
                attr.parseRowStr(doNull(sAttrs[i]));
                delSql = "delete from " + pub.yssGetTableName("Tb_Vch_Attr") +
                    " where FAttrCode =" + dbl.sqlString(attr.getAttrCode());
                this.deleteDb(delSql);
                attr.addSetting();
                attr.checkStateId = 1;
                attr.setOldAttrCode(attr.getAttrCode());
                attr.checkSetting();
            }
            /*组合套帐链接不需要导入fazmm20071020
                      for (int i = 0; i < sLinks.length; i++) {
               VchPortSetLinkBean link = new VchPortSetLinkBean();
               link.setYssPub(pub);
               if (sLinks[i].equalsIgnoreCase("null"))continue;
               link.parseRowStr(doNull(sLinks[i]));
               delSql = "delete from " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
                     " where FLinkCode =" + dbl.sqlString(link.getLinkCode());
               this.deleteDb(delSql);
               String strSql = "insert into " +
                     pub.yssGetTableName("Tb_Vch_PortSetLink") +
                     " (FLinkCode,FLinkName,FPortCode,FBookSetCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser)" +
                     " values( " +
                     dbl.sqlString(link.getLinkCode()) + "," +
                     dbl.sqlString(link.getLinkName()) + "," +
                     dbl.sqlString(link.getPortCode()) + "," +
                     dbl.sqlString(link.getBookSetCode()) + "," +
                     dbl.sqlString(link.getDesc()) + "," +
                     "1,' ',' ',' '" + " )";
               dbl.executeSql(strSql);
               // link.addSetting();
               //   link.checkStateId =1;
               //  link.checkSetting();
                      }*/
            delSql = "delete from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) + ")" +
                " and FEntityCode in (" + operSql.sqlCodes(vchEntityCodes) + ")";
            this.deleteDb(delSql);
            for (int i = 0; i < sMas.length; i++) {
                VchEntityMABean ma = new VchEntityMABean();
                ma.setYssPub(pub);
                if (sMas[i].equalsIgnoreCase("null")) {
                    continue;
                }
                ma.parseRowStr(doNull(sMas[i]));
                // ma.addSetting();
                String strSql =
                    "insert into " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                    "(FVchTplCode,FEntityCode,FOrderNum,FType," +
                    " FMAConent,FMAField,FMADict,FOperSign,FValueType,FDesc," +
                    " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values( " +
                    dbl.sqlString(ma.getVchTplCode().trim()) + "," +
                    dbl.sqlString(ma.getEntityCode().trim()) + "," +
                    (i + 1) + "," +
                    dbl.sqlString(ma.getType().trim()) + "," +
                    ma.getMaConent() + "," +
                    dbl.sqlString(ma.getMaField().trim()) + "," +
                    dbl.sqlString(ma.getMaDictCode().trim()) + "," +
                    dbl.sqlString(ma.getOperSign().trim()) + "," +
                    dbl.sqlString(ma.getValueType().trim()) + "," +
                    dbl.sqlString(ma.getDesc().trim()) + "," +
                    "1,' ',' ',' ' " + " )";
                dbl.executeSql(strSql);
                //   ma.checkStateId =1;
                //  ma.checkSetting();
            }
            delSql = "delete from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) + ")" +
                " and FEntityCode in (" + operSql.sqlCodes(vchEntityCodes) + ")";
            this.deleteDb(delSql);
            for (int i = 0; i < sAssistants.length; i++) {
                VchAssistantBean assistant = new VchAssistantBean();
                assistant.setYssPub(pub);
                if (sAssistants[i].equalsIgnoreCase("null")) {
                    continue;
                }
                assistant.parseRowStr(doNull(sAssistants[i]));
                String strSql =
                    "insert into " + pub.yssGetTableName("Tb_Vch_Assistant") +
                    "(FVchTplCode,FEntityCode,FOrderNum,FValueType," +
                    " FAssistantConent,FAssistantField,FAssistantDict,FDesc," +
                    " FCheckState,FCreator,FCreateTime,FCheckUser)" +
                    " values( " +
                    dbl.sqlString(assistant.getVchTplCode().trim()) + "," +
                    dbl.sqlString(assistant.getEntityCode().trim()) + "," +
                    (i + 1) + "," +
                    dbl.sqlString(assistant.getValueType().trim()) + "," +
                    dbl.sqlString(assistant.getAssistantConent().trim()) + "," +
                    dbl.sqlString(assistant.getAssistantFieldCode().trim()) + "," +
                    dbl.sqlString(assistant.getAssistantDictCode().trim()) + "," +
                    dbl.sqlString(assistant.getDesc().trim()) + "," +
                    "1" + "," +
                    "' ',' ',' '" + " )";
                dbl.executeSql(strSql);
            }
            str = "true";
        } catch (Exception ex) {
            str = "false";
            throw new YssException(ex.getMessage());
        }
        return str;
    }

    public String getOtherMdbData(String vchTplCodes) throws YssException {
        VchTplBean tpl = null;
        VchEntityBean entity = null;
        VchEntityResumeBean resume = null;
        VchEntitySubjectBean subject = null;
        VchDataSourceBean source = null;
        VchDsTabFieldBean field = null;
        VchAttrBean attr = null;
        VchPortSetLinkBean link = null;
        VchEntityMABean ma = null;
        VchAssistantBean assistant = null;
        StringBuffer tmpBuf = new StringBuffer();
        StringBuffer tmpBuf1 = new StringBuffer();
        ResultSet rs = null;
        String strSql = "", strSql1 = "";
        String dsCode = "'all',", attrCode = "'all',", linkCode = "'all',",
            entityCode = "'all',";
        StringBuffer buf = new StringBuffer();
        try {
            //导出凭证模版Tb_XXX_Vch_VchTpl
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FVchTplCode in (" + operSql.sqlCodes(vchTplCodes) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                tpl = new VchTplBean();
                tpl.setYssPub(pub);

                tpl.setVchTplCode(rs.getString("FVchTplCode"));
                tpl.setVchTplName(rs.getString("FVchTplName"));
                tpl.setSrcCuryCode(rs.getString("FSrcCury"));
                tpl.setCuryCode(rs.getString("FCuryCode"));
                tpl.setMode(rs.getString("FMode"));
                tpl.setDateFieldCode(rs.getString("FDateField"));
                tpl.setDsCode(rs.getString("FDsCode"));
                tpl.setLinkCode(rs.getString("FLinkCode"));
                tpl.setFields(rs.getString("FFields"));
                tpl.setVchTWay(rs.getString("FVchTWay"));
                tpl.setAttrCode(rs.getString("FAttrCode"));
                tpl.setDesc(rs.getString("FDesc"));
                tpl.setStrPortCode(rs.getString("FPortcode")); //新增字段
                buf.append(tpl.buildRowStr());
                dsCode += rs.getString("FDsCode") + ",";
                linkCode += rs.getString("FLinkCode") + ",";
                attrCode += rs.getString("FAttrCode") + ",";
                buf.append("\r\f"); //注："\r\r"表与表；\r\f记录与记录
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            if (dsCode.length() > 0) {
                dsCode = dsCode.substring(0,
                                          dsCode.length() - 1);
            }
            if (linkCode.length() > 0) {
                linkCode = linkCode.substring(0,
                                              linkCode.length() - 1);
            }
            if (attrCode.length() > 0) {
                attrCode = attrCode.substring(0,
                                              attrCode.length() - 1);
            }

            buf.append("\r\r");
            //导出凭证模版Tb_XXX_Vch_VchTpl
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Entity") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1);  //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                entity = new VchEntityBean();
                entity.setYssPub(pub);
                entity.setVchTplCode(rs.getString("FVchTplCode"));
                entity.setEntityCode(rs.getString("FEntityCode"));
                entity.setEntityName(rs.getString("FEntityName"));
                entity.setDCalcWay(rs.getString("FCalcWay")); //add liyu 1014
                entity.setSetMoneyDesc(rs.getString("FSetMoneyDesc")); //add liyu 1014
                entity.setEnCuryCode(rs.getString("FEncuryCode")); //add liyu 1015
                entity.setDCWay(rs.getString("FDCWay"));
                entity.setPriceFieldCode(rs.getString("FPriceField"));
                entity.setResumeDesc(rs.getString("FResumeDesc"));
                entity.setSubjectDesc(rs.getString("FSubjectCode"));
                entity.setMoneyDesc(rs.getString("FMoneyDesc"));
                entity.setAmountDesc(rs.getString("FAmountDesc"));
                entity.setCondDesc(rs.getString("FCondDesc"));
                entity.setAssDesc(rs.getString("FAssistantDesc"));
                entity.setEntityInd(rs.getString("FEntityInd"));
                entity.setDesc(rs.getString("FDesc"));
                buf.append(entity.buildRowStr());
                entityCode += rs.getString("FEntityCode") + ",";
                buf.append("\r\f");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            if (entityCode.length() > 0) {
                entityCode = entityCode.substring(0,
                                                  entityCode.length() - 1);
            }

            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_EntityResume") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2"; // modify liyu 1015 考虑到部分审核可能不能关键相应的代码,因为导出时将导出审核的与未审核的.下同
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                resume = new VchEntityResumeBean();
                resume.setYssPub(pub);
                /*resume.setVchTplCode(rs.getString("FVchTplCode"));
                              resume.setEntityCode(rs.getString("FEntityCode"));
                              resume.setOrderIndex(rs.getString("FOrderNum"));
                              resume.setValueType(rs.getString("FValueType"));
                 resume.setResumeConent(rs.getString("FResumeConent"));
                 resume.setResumeFieldCode(rs.getString("FResumeField"));
                 resume.setResumeFieldCode(rs.getString("FResumeDict"));
                 */
                // resume.setDesc(rs.getString("FDesc"));
                buf.append("").append("\t");
                buf.append("").append("\t");
                buf.append(rs.getString("FResumeDict")).append("\t"); //修改 liyu 1014
                buf.append(rs.getString("FResumeField")).append("\t");
                buf.append(rs.getString("FResumeConent")).append("\t");
                buf.append(rs.getString("FDesc")).append("\t");
                buf.append("").append("\t");
                buf.append(rs.getString("FValueType")).append("\t");
                buf.append(setSuperRel());

                //  buf.append(resume.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(rs.getString("FVchTplCode")).append("\t");
                tmpBuf.append(rs.getString("FEntityCode")).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            buf.append("\r\r");
            strSql1 = "select * from " +
                pub.yssGetTableName("Tb_Vch_EntitySubject") +
                " where FVchTplCode in(" + operSql.sqlCodes(this.vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                subject = new VchEntitySubjectBean();
                subject.setYssPub(pub);
                subject.setVchTplCode(rs.getString("FVchTplCode"));
                subject.setEntityCode(rs.getString("FEntityCode"));
                subject.setOrderIndex(rs.getString("FOrderNum"));
                subject.setValueType(rs.getString("FValueType"));
                subject.setSubjectConnet(rs.getString("FSubjectConent"));
                subject.setSubjectFieldCode(rs.getString("FSubjectField"));
                subject.setSubjectDictCode(rs.getString("FSubjectDict"));
                subject.setDesc(rs.getString("FDesc"));
                buf.append(subject.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(subject.getVchTplCode()).append("\t");
                tmpBuf.append(subject.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_DataSource") +
                " where FVchDsCode in(" + operSql.sqlCodes(dsCode) + ")";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                source = new VchDataSourceBean();
                source.setYssPub(pub);
                source.setVchDsCode(rs.getString("FVchDsCode"));
                source.setVchDsName(rs.getString("FVchDsName"));
                source.setVchAttrCode(rs.getString("FAttrCode"));
                source.setDataSource(dbl.clobStrValue(rs.getClob("FDataSource")));
                source.setDesc(rs.getString("FDesc"));
                buf.append(source.buildRowStr());
                buf.append("\r\f");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            //    if(attrCode.length()>0) attrCode=attrCode.substring(0,attrCode.length()-1);

            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_DsTabField") +
                " where FVchDsCode in(" + operSql.sqlCodes(dsCode) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1);  //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                field = new VchDsTabFieldBean();
                field.setYssPub(pub);
                field.setDsVchDsCode(rs.getString("FVchDsCode"));
                field.setFieldName(rs.getString("FFieldName"));
                field.setAliasName(rs.getString("FAliasName"));
                field.setFunction(rs.getString("FFunction"));
                field.setDesc(rs.getString("FDesc"));
                buf.append(field.buildRowStr());
                buf.append("\r\f");
            }

            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " where FAttrCode in(" + operSql.sqlCodes(attrCode) + ")" +
                " and FCheckState=1";
            rs = dbl.queryByPreparedStatement(strSql1);  //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                attr = new VchAttrBean();
                attr.setYssPub(pub);
                attr.setAttrCode(rs.getString("FAttrCode"));
                attr.setAttrName(rs.getString("FAttrName"));
                attr.setDesc(rs.getString("FDesc"));
                attr.setVchInd(rs.getString("FVchInd"));
                attr.setHandCheck(rs.getInt("FHandCheck")); // add liyu 1015
                buf.append(attr.buildRowStr());
                buf.append("\r\f");
            }

            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            buf.append("\r\r");
          //modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_PortSetLink") +
//                " where FLinkCode in(" + operSql.sqlCodes(linkCode) + ")" +
//                " and FCheckState=1";
//            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
//            while (rs.next()) {
//                link = new VchPortSetLinkBean();
//                link.setYssPub(pub);
//                link.setLinkCode(rs.getString("FLinkCode"));
//                link.setLinkName(rs.getString("FLinkName"));
//                link.setPortCode(rs.getString("FPortCode"));
//                link.setBookSetCode(rs.getString("FBookSetCode"));
//                link.setDesc(rs.getString("FDesc"));
//                //  buf.append(link.getPortCode()).append("\t");
//                //    buf.append(link.getBookSetCode()).append("\t");
//                buf.append(link.buildRowStr());
//                tmpBuf1.append(link.getPortCode()).append("\t");
//                tmpBuf1.append(link.getBookSetCode()).append("\r\f");
//                buf.append("\r\f");
//            }
//
//            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
//            dbl.closeResultSetFinal(rs);
//            
//            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_EntityMA") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                ma = new VchEntityMABean();
                ma.setYssPub(pub);
                ma.setVchTplCode(rs.getString("FVchTplCode"));
                ma.setEntityCode(rs.getString("FEntityCode"));
                ma.setOrderIndex(rs.getString("FOrderNum"));
                ma.setType(rs.getString("FType"));
                ma.setValueType(rs.getString("FValueType"));
                ma.setMaConent(rs.getDouble("FMAConent"));
                ma.setMaField(rs.getString("FMAField"));
                ma.setMaDictCode(rs.getString("FMADict"));
                ma.setOperSign(rs.getString("FOperSign"));
                ma.setDesc(rs.getString("FDesc"));
                buf.append(ma.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(ma.getVchTplCode()).append("\t");
                tmpBuf.append(ma.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            buf.append("\r\r");
            strSql1 = "select * from " + pub.yssGetTableName("Tb_Vch_Assistant") +
                " where FVchTplCode in(" + operSql.sqlCodes(vchTplCodes) +
                ") and " +
                " FEntityCode in (" + operSql.sqlCodes(entityCode) + ")" +
                " and FCheckState<>2";
            rs = dbl.queryByPreparedStatement(strSql1); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                assistant = new VchAssistantBean();
                assistant.setYssPub(pub);
                assistant.setVchTplCode(rs.getString("FVchTplCode"));
                assistant.setEntityCode(rs.getString("FEntityCode"));
                assistant.setOrderIndex(rs.getString("FOrderNum"));
                assistant.setValueType(rs.getString("FValueType"));
                assistant.setAssistantConent(rs.getString("FAssistantConent"));
                assistant.setAssistantFieldCode(rs.getString("FAssistantField"));
                assistant.setAssistantDictCode(rs.getString("FAssistantDict"));
                assistant.setDesc(rs.getString("FDesc"));
                buf.append(assistant.buildRowStr());
                buf.append("\r\f");
                tmpBuf.append(assistant.getVchTplCode()).append("\t");
                tmpBuf.append(assistant.getEntityCode()).append("\r\n");
            }
            
            //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
            dbl.closeResultSetFinal(rs);
            
            tmpBuf.append("\r\r");
            buf.append("\r\r");
            rs.close();
            
            return buf.toString() + tmpBuf.toString() + "\r\r" + tmpBuf1.toString();
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
    }

}
