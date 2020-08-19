package com.moofficial.moweb.Moweb.MoTab.MoTabBitmap;

import com.moofficial.moessentials.MoEssentials.MoBitmap.MoBitmap;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoTabBitmap extends MoBitmap {
    @Override
    public String getDirName() {
        return MoTabsManager.TAB_BITMAP_DIRECTORY;
    }

    @Override
    public String getFileName() {
        return super.getBitmapId().stringify();
    }
}
