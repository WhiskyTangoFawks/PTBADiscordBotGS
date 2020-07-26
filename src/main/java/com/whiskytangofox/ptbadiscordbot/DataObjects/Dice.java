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

    public String getNotation(){
        String msg = num+"d"+size;
        /*
        if (adv){
            msg = msg + " adv";
        }
        if (dis){
            msg = msg + " dis";
        }
        */
        return msg;
    }
}
