package com.denspark.utils.list_utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListUtils {
    public static Map<String,String> combineListsIntoOrderedMap (List<String> keys, List<String> values) throws IllegalArgumentException {
        if (keys.size() != values.size())
            throw new IllegalArgumentException("Cannot combine lists with dissimilar sizes");
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }
}
