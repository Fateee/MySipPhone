package com.ludashi.framework.utils.fp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collections Utilities, Java Sucks
 *
 * @author Natsuki
 */
public class Collections {


    public static <ELE, OUT> OUT reduce(List<ELE> collections, OUT initialValue, Functor2<ELE, OUT, OUT> pred) {
        if (isEmpty(collections)) {
            return initialValue;
        }
        for (ELE ele : collections) {
            initialValue = pred.apply(ele, initialValue);
        }
        return initialValue;
    }


    /**
     * utility like sql groupby
     *
     * @param collections
     * @param predication
     * @return
     */
    public static <E, T> Map<T, List<E>> groupBy(List<E> collections, Functor1<E, T> predication) {
        HashMap<T, List<E>> map = new HashMap<T, List<E>>();
        for (E e : collections) {
            T t = predication.apply(e);
            if (map.get(t) != null) {
                map.get(t).add(e);
            } else {
                List<E> newList = new ArrayList<E>();
                newList.add(e);
                map.put(t, newList);
            }
        }
        return map;
    }

    public static <E> E find(Collection<E> coll, Functor1<E, Boolean> fn) {
        for (E e : coll) {
            if (fn.apply(e)) {
                return e;
            }
        }
        return null;
    }

    public static <E> List<E> filter(Collection<E> coll, Functor1<E, Boolean> fn) {
        List<E> results = new ArrayList<E>();
        for (E item : coll) {
            if (fn.apply(item)) {
                results.add(item);
            }
        }
        return results;
    }


    public static <K, V> Map<K, V> toMap(Collection<V> original, Functor1<V, K> fn) {
        Map<K, V> hash = new HashMap<>();
        for (V input : original) {
            hash.put(fn.apply(input), input);
        }
        return hash;
    }


    public static <I, O> List<O> transform(Collection<I> original, Functor1<I, O> fn) {
        if (isEmpty(original)) {
            return new ArrayList<>();
        }

        List<O> ret = new ArrayList<>();
        for (I input : original) {
            ret.add(fn.apply(input));
        }

        return ret;
    }

    /**
     * 求两个列表的交集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List intersectList(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        java.util.Collections.copy(list, ls);
        list.retainAll(ls2);
        return list;
    }

    /**
     * 求两个列表的并集
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List unionList(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        java.util.Collections.copy(list, ls);
        list.addAll(ls2);
        return list;
    }

    /**
     * 求两个列表的差集，返回在ls中而不在ls2中的所有项
     *
     * @param ls
     * @param ls2
     * @return
     */
    public static List diffList(List ls, List ls2) {
        List list = new ArrayList(Arrays.asList(new Object[ls.size()]));
        java.util.Collections.copy(list, ls);
        list.removeAll(ls2);
        return list;
    }

    /**
     * remove list substraced from total if eq, eq was defined in predicator
     *
     * @param total
     * @param substraced
     * @param predictor
     * @param <T>
     * @param <P>
     * @return
     */
    public static <T, P> List<T> minus(List<T> total, List<P> substraced, Functor2<T, P, Boolean> predictor) {
        if (isEmpty(total) || isEmpty(substraced)) {
            return total;
        }
        List<T> ret = new ArrayList<>(total);
        for (T item : total) {
            for (P sub : substraced) {
                if (predictor.apply(item, sub)) {
                    ret.remove(item);
                    break;
                }
            }
        }
        return ret;
    }

    public static <E> boolean in(E item, E... coll) {
        if (item == null || isEmpty(coll)) {
            return false;
        }

        for (E i : coll) {
            if (item.equals(i)) {
                return true;
            }
        }
        return false;
    }

    public static <E> E pick(List<E> items) {
        if (items == null || items.size() == 0) {
            return null;
        }
        int total = items.size();
        int idx = (int) (Math.random() * total);
        return items.get(idx);
    }


    public static <E> boolean any(Collection<E> collections, Functor1<E, Boolean> fn) {
        if (isEmpty(collections)) {
            return false;
        }
        for (E collection : collections) {
            if (fn.apply(collection)) {
                return true;
            }
        }
        return false;
    }


    public static <E> boolean isEmpty(Collection<E> e) {
        return e == null || e.isEmpty();
    }

    public static <E> boolean isEmpty(E[] ele) {
        return ele == null || ele.length == 0;
    }

}
