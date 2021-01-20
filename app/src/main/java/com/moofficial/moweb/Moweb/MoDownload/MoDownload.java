package com.moofficial.moweb.Moweb.MoDownload;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;

import java.io.File;

public class MoDownload implements MoSelectableItem {

    private boolean selected;
    private File file;

    public MoDownload(File f) {
        this.file = f;
    }

    @Override
    public void setSelected(boolean b) {
        this.selected = b;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }
}
