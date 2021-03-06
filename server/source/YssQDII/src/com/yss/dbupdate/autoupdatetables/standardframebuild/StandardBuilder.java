package com.yss.dbupdate.autoupdatetables.standardframebuild;

import com.yss.dsub.BaseBean;
import com.yss.util.*;

/**
 *
 * <p>Title: 标准字典表的创建</p>
 *
 * <p>Description: 通过读取标准表结构数据，创建标准字典表</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StandardBuilder
    extends BaseBean {
    public StandardBuilder() {
    }

    /**
     * 通过读取更新文件，创建标准自定义字典表
     * @param sVerNum：当前更新版本
     * @throws YssException
     */
    public void build(String sVerNum) throws YssException {
        try {
            ImportTableFrameData imp = new ImportTableFrameData();
            imp.setYssPub(pub);
            imp.importData(sVerNum);
        } catch (Exception e) {
            throw new YssException("创建标准表结构数据出错！\r\n", e);
        }
    }

    /**
     * 读取当前数据库管理系统的数据字典表，并转换为 XML 格式
     * @return String
     * @throws YssException
     */
    public String exportData() throws YssException {
        String dataStr = "";
        try {
            ExportTableFrameData exp = new ExportTableFrameData();
            exp.setYssPub(pub);
            dataStr = exp.exportData();
        } catch (Exception e) {
            throw new YssException("导出标准表结构数据出错！\n", e);
        }
        return dataStr;
    }
}
