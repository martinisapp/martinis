package com.chriswatnee.martinis.webservice.support;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ViewModelMapper {

    private ViewModelMapper() {
    }

    public static <T, R> List<R> mapList(List<T> items, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : items) {
            result.add(mapper.apply(item));
        }
        return result;
    }
}
