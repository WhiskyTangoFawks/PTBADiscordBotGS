package com.whiskytangofox.ptbadiscordbot.wrappers;

public class StatWrapper {
    public final String stat;
    public final int modStat;
    public final boolean isDebilitated;
    public String debilityTag;

    public StatWrapper(String stat, int modStat, boolean isDebilitated, String debilityTag) {
        this.stat = stat;
        this.modStat = modStat;
        this.isDebilitated = isDebilitated;
        this.debilityTag = debilityTag;
    }
}
