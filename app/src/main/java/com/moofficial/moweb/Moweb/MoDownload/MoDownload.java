package com.moofficial.moweb.Moweb.MoDownload;



import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileExtension;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;

import java.io.File;

import static com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileExtension.*;

public class MoDownload implements MoSelectableItem, MoSearchableItem {




    private boolean selected, searched;
    private File file;
    private FileType type;


    public MoDownload(@NonNull File f) {
        this.file = f;
        this.type = MoFileExtension.getType(this.file);
    }

    public String getName() {
        return this.file.getName();
    }

    public String getDescription() {
        return this.file.length() + "";
    }

    public FileType getType() {
        return type;
    }

    @Override
    public void setSelected(boolean b) {
        this.selected = b;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public boolean updateSearchable(Object... objects) {
        this.searched = MoSearchableUtils.isSearchable(true,objects,this.file.getName());
        return isSearchable();
    }

    @Override
    public boolean isSearchable() {
        return this.searched;
    }

    @Override
    public void setSearchable(boolean b) {
        this.searched = b;
    }
}
