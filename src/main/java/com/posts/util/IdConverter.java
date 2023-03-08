package com.posts.util;

import java.util.Map;

public class IdConverter<T> implements MapConverter<T> {

    private static final String KEY = "id";

    @Override
    public Map<String, T> convert(T id) {
        return Map.of(KEY, id);
    }
}
