package com.iuturakulov.vkvoicesuperappkit.model;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.iuturakulov.vkvoicesuperappkit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecordFragment recordFragment;
    private VoiceListFragment voiceListFragment;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recordFragment = new RecordFragment();
        voiceListFragment = new VoiceListFragment();
        viewPager = findViewById(R.id.viewPagerMain);
        tabLayout = findViewById(R.id.main_tabs_holder);
        setupTopBar();
    }

    private void setupTopBar() {
        tabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(recordFragment, "Records");
        viewPagerAdapter.addFragment(voiceListFragment, "List");
        viewPager.setAdapter(viewPagerAdapter);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.navigation);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_list);
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
