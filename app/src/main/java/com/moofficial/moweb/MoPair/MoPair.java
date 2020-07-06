package com.moofficial.moweb.MoPair;

import android.content.Context;

import androidx.core.util.Pair;

import com.moofficial.moweb.MoIO.MoFile;
import com.moofficial.moweb.MoIO.MoLoadable;
import com.moofficial.moweb.MoIO.MoSavable;

// a pair that can be saved
public class MoPair <F,S> implements MoSavable, MoLoadable {

    private final String SEP_KEY = "apoijs&dblkx&cmwye&*t";

    private Pair<F,S> pair;


    public MoPair(F first, S second){
        pair = new Pair<>(first,second);
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
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(pair.first,pair.second);
    }
}
