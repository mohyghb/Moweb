package com.moofficial.moweb.MoActivities.MainMenu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainMenuStateAdapter extends FragmentStateAdapter {

    private MoOnTabClickListener onTabClickListener;
    private List<MainMenuFragment> fragments = new ArrayList<>();



    public MainMenuStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MainMenuStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MainMenuStateAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public MainMenuStateAdapter setOnTabClickListener(MoOnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        MainMenuFragment mainMenuFragment;
        if (position == 1) {
            mainMenuFragment =  new MainMenuPrivateTabFragment();
        }else{
            mainMenuFragment = new MainMenuTabFragment();
        }
        mainMenuFragment.setOnTabClickListener(this.onTabClickListener);
        fragments.add(mainMenuFragment);
        return mainMenuFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


    public void notifyFragments() {
        for(MainMenuFragment f: fragments){
            f.notifyDataSetChanged();
        }
    }

}
