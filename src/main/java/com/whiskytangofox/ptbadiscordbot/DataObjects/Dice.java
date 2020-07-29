package com.whiskytangofox.ptbadiscordbot.DataObjects;

public class Dice {

    public int size = 0;
    public int num = 0;
    public boolean adv = false;
    public boolean dis = false;

    public Dice(int num, int size) {
        this.size = size;
        this.num = num;
    }

    public String getNotation() {
        return num + "d" + size;
    }

    public Dice setAdv() {
        this.adv = true;
        return this;
    }

    public Dice setDis() {
        this.dis = true;
        return this;
    }
}
