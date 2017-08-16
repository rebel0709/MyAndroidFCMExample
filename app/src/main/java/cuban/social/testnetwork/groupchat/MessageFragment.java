package cuban.social.testnetwork.groupchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import butterknife.ButterKnife;
import cuban.social.testnetwork.DialogsFragment;
import cuban.social.testnetwork.R;
import cuban.social.testnetwork.groupchat.fragments.MyGroupsFragment;

/**
 * Created by Freelancer on 8/1/2017.
 */

public class MessageFragment extends Fragment {
    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }


    @Bind(R.id.viewpagertab)
    SmartTabLayout viewPagerTab;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    private FragmentPagerItemAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabLayout();
    }

    public void initTabLayout(){
        FragmentPagerItems pages = new FragmentPagerItems(getContext());
        if (getActivity() != null && isAdded()) {
            pages.add(FragmentPagerItem.of(getString(R.string.friends), DialogsFragment.class));
            pages.add(FragmentPagerItem.of(getString(R.string.group), MyGroupsFragment.class));

            mAdapter = new FragmentPagerItemAdapter(getChildFragmentManager(), pages);
            viewPager.setAdapter(mAdapter);
            viewPagerTab.setViewPager(viewPager);
        }
    }
}
