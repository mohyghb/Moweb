package com.moofficial.moweb.MoAppbar;

import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MoAppbarUtils {


    /**
     * transitions between the title inside the collapsing toolbar
     * and the title of the small toolbar
     * @param parent view which contains all of the items
     * @param appbarLayoutId id of the app bar
     * @param collapsingToolbarId id of the collapsing toolbar
     * @param bt id of the big title inside collapsing toolbar
     * @param st id of the small title inside the toolbar
     */
    public static void sync(View parent, int appbarLayoutId,int collapsingToolbarId,int bt,int st){
        AppBarLayout appBarLayout = parent.findViewById(appbarLayoutId);
        CollapsingToolbarLayout collapsingToolbar = parent.findViewById(collapsingToolbarId);
        TextView smallTitle = parent.findViewById(st);
        TextView bigTitle = parent.findViewById(bt);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            int col = collapsingToolbar.getScrimVisibleHeightTrigger();
            int h = collapsingToolbar.getHeight();
            float f = (float)(col-verticalOffset)/h;
            float actualF = verticalOffset==0?0f:Math.abs(verticalOffset)>col?1f:f;
            smallTitle.setAlpha(actualF);
            bigTitle.setAlpha(1f-actualF);
        });
    }

}
