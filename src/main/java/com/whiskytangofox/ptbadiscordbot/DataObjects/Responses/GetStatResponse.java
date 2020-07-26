package com.whiskytangofox.ptbadiscordbot.DataObjects.Responses;

public class GetStatResponse {
    public final String stat;
    public final int modStat;
    public final boolean isDebilitated;
    public String debilityTag;

    public GetStatResponse(String stat, int modStat, boolean isDebilitated) {
        this.stat = stat;
        this.modStat = modStat;
        this.isDebilitated = isDebilitated;
    }
}
