package com.yss.pojo.cache;

import com.yss.util.*;

public class YssVoucher {
    private String vchNum = ""; //ƾ֤���
    private boolean JDBalence; //����Ƿ�ƽ��
    private boolean subject; //��Ŀ�Ƿ����
    private boolean bookSet; //�����Ƿ����

    public String getVchNum() {
        return vchNum;
    }

    public boolean isBookSet() {
        return bookSet;
    }

    public boolean isJDBalence() {
        return JDBalence;
    }

    public void setSubject(boolean subject) {
        this.subject = subject;
    }

    public void setVchNum(String vchNum) {
        this.vchNum = vchNum;
    }

    public void setBookSet(boolean bookSet) {
        this.bookSet = bookSet;
    }

    public void setJDBalence(boolean JDBalence) {
        this.JDBalence = JDBalence;
    }

    public boolean isSubject() {
        return subject;
    }

    public YssVoucher() {
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.subject).append("\t");
        buf.append(this.bookSet).append("\t");
        return buf.toString();
    }

}
