package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.App;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.SortedMap;

public class PatriciaTrieIgnoreCase<E> extends PatriciaTrie<E> {

    @Override
    public E put(@NotNull String key, E value) {
        /*
        String checkKey = key.length() > 4 ? key.substring(0, 4) : key;
        if (!prefixMap(checkKey).isEmpty()){
            StringBuffer conflict = new StringBuffer();
            if (prefixMap(checkKey).size() == 1 && !prefixMap(checkKey).containsKey(key)) {
                App.logger.warn("Potential TRIE conflict on loading " + key);
            } else {
                prefixMap(checkKey).keySet().forEach( conflictingKey -> {
                    conflict.append(conflictingKey + " ");
                });
                App.logger.warn("Potential TRIE conflict on loading " + conflict.toString());
            }
        }
         */
        key = key.toLowerCase().replace(" ", "");
        return super.put(key, value);
    }

    @Override
    public SortedMap prefixMap(String key) {
        return super.prefixMap(key.toLowerCase().replace(" ", ""));
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String)key).toLowerCase().replace(" ", ""));
    }

    @Override
    public E get(Object k) {
        return super.get(((String)k).toLowerCase().replace(" ", ""));
    }

    public E getClosestMatch(String k) throws KeyConflictException {
        k = k.toLowerCase().replace(" ", "");
        if (this.containsKey(k)) {
            return this.get(k);
        } else if (this.prefixMap(k).size() == 1) {
            return this.get(this.prefixMap(k).firstKey());
        } else if (this.prefixMap(k).size() > 1){
            throw new KeyConflictException("The key matches multiple entries: " + k);
        } else { //(this.prefixMap(k).size() ==0)
            return null;
        }
    }
}
