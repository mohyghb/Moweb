package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;

import androidx.annotation.IntDef;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;

import java.util.HashMap;

// coming soon moved to another time to add this feature
public class MoWebAutoFill implements MoSavable, MoLoadable {


    public static final HashMap<String,Integer> mapOfTypes = new HashMap<String,Integer>(){{
        put("email",TYPE_EMAIL);
        put("password",TYPE_PASSWORD);
        put("text",TYPE_TEXT);
    }};


    @IntDef(value = {TYPE_EMAIL,TYPE_PASSWORD,TYPE_TEXT,TYPE_OTHER})
    public @interface AutoFillType{}

    public static final int TYPE_EMAIL = 0;
    public static final int TYPE_PASSWORD = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_OTHER = 3;

    private String id;
    private String value;
    @AutoFillType private int type;
   // private MoWebAutoFillId fillId = new MoWebAutoFillId();

    public String getId() {
        return id;
    }

    public MoWebAutoFill setId(String id) {
        this.id = id;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MoWebAutoFill setValue(String value) {
        this.value = value;
        return this;
    }

    public int getType() {
        return type;
    }

    public boolean isPassword() {
        return this.type == TYPE_PASSWORD;
    }

    public boolean isEmail() {
        return this.type == TYPE_EMAIL;
    }

    public boolean isText() {
        return this.type == TYPE_TEXT;
    }

    /**
     * sets the type based on the type
     * that is passed from the input
     * field inside js
     * we convert it into an integer
     * for later use if the type is
     * applicable or give it TYPE_OTHER
     * if none of the other types make sense
     * for this type
     * @param type of the auto fill
     * @return this for nested calling
     */
    public MoWebAutoFill setType(String type) {
        Integer potential = mapOfTypes.get(type);
        if(potential!=null){
            this.type = potential;
        }else{
            this.type = TYPE_OTHER;
        }
        return this;
    }


    @Override
    public void load(String s, Context context) {
        String[] l = MoFile.loadable(s);
        this.id = l[0];
        this.value = l[1];
        this.type = Integer.parseInt(l[2]);
       // this.fillId.load(l[3],context);
    }

    @Override
    public String getData() {
        return MoFile.getData(id,value,type);
    }


}
