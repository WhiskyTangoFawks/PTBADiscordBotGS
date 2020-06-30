package com.whiskytangofox.ptbadiscordbot.wrappers;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.util.ArrayList;

public class PatriciaTrieIgnoreCase<E> extends PatriciaTrie<E> {

    @Override
    public E put(String key, E value) {
        return super.put(key.toLowerCase().replace(" ", ""), value);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String)key).toLowerCase().replace(" ", ""));
    }

    @Override
    public E get(Object k) {
        return super.get(((String)k).toLowerCase().replace(" ", ""));
    }

    public E getClosestMatch(String k) throws Exception {
        k = k.toLowerCase().replace(" ", "");
        if (this.containsKey(k)) {
            return this.get(k);
        } else if (this.prefixMap(k).size() == 1) {
            return this.get(this.prefixMap(k).firstKey());
        } else if (this.prefixMap(k).size() > 1){
            //TODO - replace with a custom exception
            throw new Exception("The key matches multiple entries: " + k);
        } else { //(this.prefixMap(k).size() ==0)
            return null;
        }
    }
}
