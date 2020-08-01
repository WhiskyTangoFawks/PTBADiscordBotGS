package com.whiskytangofox.ptbadiscordbot.DataStructure;

import com.whiskytangofox.ptbadiscordbot.Utils;

import java.util.HashMap;

public class HashMapIgnoreCase<V> extends HashMap<String, V> {

    public HashMapIgnoreCase() {
        super();
    }

    @Override
    public V get(Object key) {
        return super.get(Utils.cleanAndTruncateString(key.toString()));
    }

    @Override
    public V put(String key, V value) {
        return super.put(Utils.cleanAndTruncateString(key), value);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(Utils.cleanAndTruncateString(key.toString())) != null;
    }
}
