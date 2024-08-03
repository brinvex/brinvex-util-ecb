package com.brinvex.util.ecb.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;

@SuppressWarnings("UnusedReturnValue")
public class CollectionUtil {

    public static <E> boolean removeAdjacentDuplicates(Collection<E> collection, BiPredicate<E, E> equalityPredicate) {
        Iterator<E> iterator = collection.iterator();

        if (!iterator.hasNext()) {
            return false;
        }

        boolean modified = false;
        E prev = iterator.next();
        while (iterator.hasNext()) {
            E current = iterator.next();
            if (equalityPredicate.test(prev, current)) {
                iterator.remove();
                modified = true;
            } else {
                prev = current;
            }
        }
        return modified;
    }

}
