package com.whiskytangofox.ptbadiscordbot.DataStructure;

import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Utils;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.jetbrains.annotations.NotNull;

import java.util.SortedMap;

public class PatriciaTrieIgnoreCase<E> extends PatriciaTrie<E> {

    @Override
    public E put(@NotNull String key, E value) {
        /*TODO - error on add close key
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
        return super.put(Utils.cleanAndTruncateString(key), value);
    }

    @Override
    public SortedMap prefixMap(String key) {
        return super.prefixMap(Utils.cleanAndTruncateString(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(Utils.cleanAndTruncateString((String) key));
    }

    @Override
    public E get(Object k) {
        return super.get(Utils.cleanAndTruncateString((String) k));
    }

    public E getClosestMatch(String k) throws KeyConflictException {
        k = Utils.cleanAndTruncateString(k);
        if (this.containsKey(k)) {
            return this.get(k);
        } else if (this.prefixMap(k).size() == 1) {
            return this.get(this.prefixMap(k).firstKey());
        } else if (this.prefixMap(k).size() > 1) {
            throw new KeyConflictException("The key matches multiple entries: " + k);
        } else { //(this.prefixMap(k).size() ==0)
            //TODO - try getting all keys which contain the exact message
            //Test: damage should return Deal Damage, Slash should return Hack and Slash
            //ToDo - spelling mistakes?
            return null;
        }
    }


}
