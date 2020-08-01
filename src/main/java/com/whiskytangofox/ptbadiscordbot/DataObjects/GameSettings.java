package com.whiskytangofox.ptbadiscordbot.DataObjects;

import java.util.Properties;

public class GameSettings {

    Properties properties = new Properties();

    public enum KEY {
        commandchar,
        default_system_dice,
        fail_xp,
        stat_debility_tag,
    }

    public String get(KEY key) {
        return properties.getProperty(key.name());
    }

    public void set(KEY key, String value) {
        properties.put(key.name(), value);
    }


}
