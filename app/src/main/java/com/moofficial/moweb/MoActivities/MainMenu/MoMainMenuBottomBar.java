package com.moofficial.moweb.MoActivities.MainMenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabsButton;
import com.moofficial.moweb.R;

public class MoMainMenuBottomBar extends MoConstraint {

    private ImageButton more, add;
    private TabLayout tabLayout;
    private MoTabsButton tabsButton;
    private Runnable addNormalTab, addPrivateTab;
    private ViewPager2 pager2;

    public MoMainMenuBottomBar(Context context) {
        super(context);
    }

    public MoMainMenuBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoMainMenuBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_main_menu_bottom_tool_bar;
    }


    public MoMainMenuBottomBar setAddNormalTab(Runnable addNormalTab) {
        this.addNormalTab = addNormalTab;
        return this;
    }

    public MoMainMenuBottomBar setAddPrivateTab(Runnable addPrivateTab) {
        this.addPrivateTab = addPrivateTab;
        return this;
    }

    public MoMainMenuBottomBar setMoreClickListener(View.OnClickListener l) {
        more.setOnClickListener(l);
        return this;
    }

    public MoMainMenuBottomBar setTabsNumber(int number) {
        tabsButton.setNumberOfTabs(number);
        return this;
    }

    public MoMainMenuBottomBar setOnTabsClickListener(View.OnClickListener l) {
        tabsButton.setOnTabsButtonClicked(l);
        return this;
    }


    public MoMainMenuBottomBar syncWithViewPager2(ViewPager2 pager2) {
        this.pager2 = pager2;
        new TabLayoutMediator(this.tabLayout, pager2, (tab, position) -> {
            if (position == 0) {
                // this is the normal tabs section
                // tab.setIcon(R.drawable.ic_baseline_language_24).setText("Normal");
                tab.setCustomView(tabsButton);
            } else {
                // this is the private tabs section
                tab.setIcon(R.drawable.ic_baseline_vpn_lock_24);
            }
        }).attach();
        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    tabsButton.showActivated();
                } else {
                    tabsButton.showDeactivated();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        return this;
    }


    @Override
    public void initViews() {
        more = findViewById(R.id.mo_bottom_tool_bar_more);
        initAdd();
        tabLayout = findViewById(R.id.mo_bottom_tool_bar_tab_layout);
        tabsButton = new MoTabsButton(getContext());
    }

    private void initAdd() {
        add = findViewById(R.id.mo_bottom_tool_bar_add);
        this.add.setOnClickListener(view -> {
            if (pager2.getCurrentItem() == 0) {
                // add normal tab
                addNormalTab.run();
            } else {
                addPrivateTab.run();
            }
        });
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
