package com.moofficial.moweb.MoDate;

import android.content.Context;

;import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;


public class MoTime implements MoSavable, MoLoadable {


    private static final String SEP_KEY = "mo&t&im&e";


    private int hour;
    private int minute;
    private int second;


    public MoTime(int h,int m, int s){
        this.hour = h;
        this.minute = m;
        this.second = s;
    }

    public MoTime(){}

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] com = MoFile.loadable(data);
        this.hour = Integer.parseInt(com[0]);
        this.minute = Integer.parseInt(com[1]);
        this.second = Integer.parseInt(com[2]);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(hour,minute,second);
    }
}
