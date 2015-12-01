package com.kuibu.module.adapter;

import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.kuibu.app.model.base.BaseFragment;
import com.kuibu.model.entity.TabHotInfo;
import com.kuibu.module.iterfaces.IConstructFragment;

public class TabPageViewAdapter extends FragmentPagerAdapter {	
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
    
	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	 * return POSITION_NONE indicates  we want fragment pagers recreate .
	 */
	@Override
	public int getItemPosition(Object object) {
		
		return PagerAdapter.POSITION_NONE;
	}

	@Override
    public BaseFragment getItem(int position) {
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
