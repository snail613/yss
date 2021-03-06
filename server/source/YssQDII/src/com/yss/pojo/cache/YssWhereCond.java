package com.yss.pojo.cache;

public class YssWhereCond {
    private String field = "";
    private String sign = "";
    private String value = "";
    private String rela = "";
    public String getSign() {
        return sign;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    public void setRela(String rela) {
        this.rela = rela;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRela() {
        return rela;
    }

    public YssWhereCond() {
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(field).append(" ");
        buf.append(sign).append(" ");
        buf.append(value).append(" ");
        if (rela.equalsIgnoreCase("and") || rela.equalsIgnoreCase("or")) {
            buf.append(rela).append(" ");
        }
        return buf.toString();
    }
}
