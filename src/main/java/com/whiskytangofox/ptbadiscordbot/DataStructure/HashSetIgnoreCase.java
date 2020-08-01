package com.whiskytangofox.ptbadiscordbot.DataStructure;

import com.whiskytangofox.ptbadiscordbot.Utils;

import java.util.HashSet;

public class HashSetIgnoreCase<V> extends HashSet<String> {

    public HashSetIgnoreCase() {
        super();
    }

    public boolean add(String key, V value) {
        return super.add(Utils.cleanAndTruncateString(key));
    }

    @Override
    public boolean contains(Object key) {
        return super.contains(Utils.cleanAndTruncateString(key.toString()));
    }
}
