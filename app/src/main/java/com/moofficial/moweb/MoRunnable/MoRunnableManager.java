package com.moofficial.moweb.MoRunnable;

public class MoRunnableManager {


    /**
     * returns a runnable that does nothing when ran
     * @return
     */
    public static Runnable nullRunnable(){
        return () -> {};
    }

}
