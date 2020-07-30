package com.whiskytangofox.ptbadiscordbot.DataStructure;

import java.util.HashSet;

public class HashSetIgnoreCase<V> extends HashSet<String> {

    public HashSetIgnoreCase() {
        super();
    }

    public String cleanKey(String key) {
        if (key.contains("(")) {
            key = key.substring(0, key.indexOf("(") - 1);
        }
        return key.toLowerCase().replace(" ", "");
    }

    public boolean add(String key, V value) {
        return super.add(cleanKey(key));
    }

    @Override
    public boolean contains(Object key) {
        return super.contains(cleanKey(key.toString()));
    }
}
