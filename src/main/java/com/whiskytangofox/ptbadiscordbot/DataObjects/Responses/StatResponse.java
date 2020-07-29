package com.whiskytangofox.ptbadiscordbot.DataObjects.Responses;

public class StatResponse {
    public final String stat;
    public int modStat;
    public final boolean isDebilitated;
    public String debilityTag;

    public StatResponse(String stat, int modStat, boolean isDebilitated) {
        this.stat = stat;
        this.modStat = modStat;
        this.isDebilitated = isDebilitated;
    }

    public StatResponse setDebilityTag(String tag) {
        this.debilityTag = tag;
        return this;
    }
}
