package com.yss.main.account.accsetting.pojo;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ItemInfoBean
    extends BaseDataSettingBean {
    private String code=""; //代码
    private String name=""; //名称
    private String desc=""; //描述
    private String type=""; //类型
    private String value="";//值
    public ItemInfoBean() {
    }

    /**
     * 从记录集中获取字段值
     * @param rs ResultSet
     * @throws YssException
     */
    public void setValues(ResultSet rs) throws YssException {
        try {
            this.code = rs.getString("FCode");
            this.name = rs.getString("FName");
            this.desc = rs.getString("FDesc");
        } catch (SQLException sex) {
            throw new YssException(sex.toString());
        }
    }

    /**
     * 将字段值合成字符串
     * @return String
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.code).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.name).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.desc).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.type).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append(this.value).append(YssCons.YSS_ITEMSPLITMARK1);
        buf.append("null");
        return buf.toString();
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
