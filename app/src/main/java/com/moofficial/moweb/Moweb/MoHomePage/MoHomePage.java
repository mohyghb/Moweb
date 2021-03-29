package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;

import java.util.Objects;

public class MoHomePage implements MoSavable, MoLoadable, MoSelectableItem {

    private MoURL url;
    private boolean isActivated;
    private boolean isSelected;

    public MoHomePage(String url) {
        this.url = new MoURL(url);
    }

    public MoHomePage(String s, Context context) {
        this.load(s, context);
    }

    public boolean isValid() {
        return this.url.isValid();
    }

    public String getUrl() {
        return this.url.getUrlString();
    }

    public boolean isActivated() {
        return isActivated;
    }

    public MoHomePage setActivated(boolean activated) {
        isActivated = activated;
        return this;
    }

    public boolean isValidUrl() {
        return this.url.isValid();
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        this.url = new MoURL(c[0], context);
        this.isActivated = Boolean.parseBoolean(c[1]);
    }

    @Override
    public String getData() {
        return MoFile.getData(this.url, this.isActivated);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoHomePage)) return false;
        MoHomePage that = (MoHomePage) o;
        return url.getUrlString().equals(that.url.getUrlString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(url.getUrlString());
    }

    @Override
    public boolean onSelect() {
        this.isSelected = !this.isSelected;
        return this.isSelected;
    }

    @Override
    public void setSelected(boolean b) {
        this.isSelected = b;
    }

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }

}
