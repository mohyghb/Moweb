package com.moofficial.moweb.MoId;

import android.content.Context;


import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;

import java.util.HashSet;
import java.util.Random;


public class MoId implements MoSavable, MoLoadable {


    private static final HashSet<Integer> setOfIds = new HashSet<>();

    private int id;

    public MoId() {
        this.id = getRandomId();
        while(setOfIds.contains(id)){
            // making sure no id is a duplicate
            this.id = getRandomId();
        }
        setOfIds.add(this.id);
    }

    public int getId() {
        return id;
    }

    public String getSId(){
        return id+"";
    }

    public void setId(int id) {
        this.id = id;
    }

    public static int getRandomId()
    {
        return randInt(12,1003122340);
    }


    public static int randInt(int min, int max) {

        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        this.id = Integer.parseInt(data);
        setOfIds.add(this.id);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return this.id+"";
    }
}
