package com.ludashi.framework.utils.fp;

public interface Functor3<P1, P2, P3, R> {
    R apply(P1 p1, P2 p2, P3 p3);
}
