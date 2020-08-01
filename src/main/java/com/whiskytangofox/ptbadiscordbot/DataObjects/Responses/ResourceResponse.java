package com.whiskytangofox.ptbadiscordbot.DataObjects.Responses;

public class ResourceResponse {
    public final String resource;
    public final int oldValue;
    public final int newValue;
    public final int mod;

    public ResourceResponse(String resource, int oldValue, int mod, int newValue) {
        this.resource = resource;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.mod = mod;
    }

    public String getDescriptiveResult(){
        String modSign = (mod) < 0 ? " " : " + ";
        return resource + "("+oldValue + ")" + modSign + (mod) + " = " + newValue;
    }
}
