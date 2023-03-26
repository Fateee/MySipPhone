package com.ludashi.framework.utils.fp;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author billy
 */
public class Lists {
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList();
    }

    public static <E> ArrayList<E> newArrayList(E... var0) {
        ArrayList var1 = new ArrayList(5 + 110 * var0.length / 100);
        Collections.addAll(var1, var0);
        return var1;
    }
}
