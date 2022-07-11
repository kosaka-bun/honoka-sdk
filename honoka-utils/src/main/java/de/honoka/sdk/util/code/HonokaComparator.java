package de.honoka.sdk.util.code;

import java.util.Comparator;

public interface HonokaComparator<T> extends Comparator<T> {

    /**
     * 和一个传入的对象相匹配，返回较大的那个对象，一样大返回null
     */
    T getBigger(T o1, T o2);

    /**
     * o1比o2大，返回正数，o1比o2小，返回负数，一样大返回0
     */
    @Override
    default int compare(T o1, T o2) {
        T bigger = getBigger(o1, o2);
        if(bigger == o1) return 1;
        else if(bigger == o2) return -1;
        return 0;
    }

    default HonokaComparator<T> desc() {
        HonokaComparator<T> superComparator = this;
        return (o1, o2) -> {
            T bigger = superComparator.getBigger(o1, o2);
            return bigger == null ? null :
                    bigger == o1 ? o2 : o1;
        };
    }
}
