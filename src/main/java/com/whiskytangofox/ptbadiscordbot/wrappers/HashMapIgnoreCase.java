package com.whiskytangofox.ptbadiscordbot.wrappers;

import java.util.HashMap;

public class HashMapIgnoreCase<V> extends HashMap<String, V> {

    public HashMapIgnoreCase(){
        super();
    }

    private String cleanKey(String key){
        return key.toLowerCase().replace(" ", "");
    }

    @Override
    public V get(Object key){
        return super.get(cleanKey(key.toString()));
    }

    @Override
    public V put(String key, V value){
        return super.put(cleanKey(key), value);
    }

    @Override
    public boolean containsKey(Object key) {
        return get(cleanKey(key.toString())) != null;
    }
}
