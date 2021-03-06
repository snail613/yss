package com.yss.dsub;

import java.util.*;
import com.yss.main.dao.*;

public class BaseComparator
    implements Comparator {
    public BaseComparator() {
    }

    /**
     * equals
     *
     * @param obj Object
     * @return boolean
     */
    public boolean equals(Object obj) {
        return false;
    }

    /**
     * compare
     *
     * @param o1 Object
     * @param o2 Object
     * @return int
     */
    public int compare(Object o1, Object o2) {
        IKey k1 = null;
        IKey k2 = null;
        int iResult = 0;
        if (o1 instanceof IKey && o2 instanceof IKey) {
            k1 = (IKey) o1;
            k2 = (IKey) o2;
            if (k1.getKey() instanceof String && k2.getKey() instanceof String) {
                //iResult = ((String)k1.getKey()).hashCode() - ((String)k2.getKey()).hashCode();
                //2008.02.18 蒋锦 修改 使用字典比较代替原来的哈希玛比较
                iResult = ( (String) k1.getKey()).compareToIgnoreCase( (String) k2.getKey());
            }
            if (k1.getKey() instanceof Integer && k2.getKey() instanceof Integer) {
                iResult = ( (Integer) k1.getKey()).intValue() - ( (Integer) k2.getKey()).intValue();
            }
        }
        return iResult;
    }
}
