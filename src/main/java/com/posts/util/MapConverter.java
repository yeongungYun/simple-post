package com.posts.util;

import java.util.Map;

public interface MapConverter<T> {

    public Map<String, T> convert(T value);
}
