package com.kuibu.module.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kuibu.model.entity.TabHotInfo;
import com.kuibu.module.iterfaces.IConstructFragment;

public class TabPageViewAdapter extends FragmentStatePagerAdapter {	
	private List<TabHotInfo> tabList = null;
	private IConstructFragment constructer ; 
    public TabPageViewAdapter(FragmentManager fm,List<TabHotInfo> tabList,
    		IConstructFragment constructer) {
        super(fm);
        this.tabList = tabList;   
        this.constructer=constructer; 
    }
    
    public void updateView(List<TabHotInfo> datas) {
		this.tabList = datas;
		this.notifyDataSetChanged();
	}
    
	@Override
    public Fragment getItem(int position) {
        return constructer.newInstance(tabList.get(position).getTag());
    }

    @Override
    public CharSequence getPageTitle(int position) {    	
        return tabList.get(position).getTitle();
    }
    
    @Override
    public int getCount() {
        return tabList.size();
    }
}
