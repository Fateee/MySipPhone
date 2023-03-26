package com.ludashi.framework.utils.fp;

public interface Functor2<P1, P2, R> {
    R apply(P1 p1, P2 p2);
}
