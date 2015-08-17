package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuibu.model.bean.TabTitleObject;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TabPageViewAdapter;
import com.viewpagerindicator.SlidingTabIndicator;

/**
 * 内容fragment
 */
public  class ExplorePageFragment extends Fragment 
{
	private List<TabTitleObject> exportpageTabTitle = null; 
	private TabPageViewAdapter adapter= null;
	private ViewPager pager ; 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {			
			initData();
			SharedPreferences mPerferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			boolean isDarkTheme = mPerferences.getBoolean("dark_theme", false); 
			Context contextTheme = null ; 
			if(isDarkTheme){
				 contextTheme = new ContextThemeWrapper(getActivity(),
			    		R.style.StyledIndicatorsDark);
			}else{
				contextTheme = new ContextThemeWrapper(getActivity(),
			    		R.style.StyledIndicatorsLight);
			}
		    LayoutInflater themeflater = inflater.cloneInContext(contextTheme);
			View rootView = themeflater.inflate(R.layout.frame_tabpage, container, false);
			pager = (ViewPager)rootView.findViewById(R.id.pager);
			if(adapter == null){
				adapter =  new TabPageViewAdapter(getActivity().getSupportFragmentManager(),
						exportpageTabTitle,new ExploreConstruct());
				pager.setAdapter(adapter);
			}
			else{
				adapter.updateView(exportpageTabTitle);
			}		    
			SlidingTabIndicator indicator = (SlidingTabIndicator)rootView.findViewById(R.id.indicator);
		    indicator.setViewPager(pager);
		   	indicator.setOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					// TODO Auto-generated method stub	
					FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter)pager.getAdapter();
					BaseFragment f = (BaseFragment)adapter.getItem(position);
					f.onTabPageChanged();
				}
			});
		   	
		    if(isDarkTheme){
		    	indicator.setBackgroundResource(R.drawable.abc_ab_solid_dark_holo);
		    	indicator.setCustomTabColorizer(new SlidingTabIndicator.TabColorizer() {
			            @Override
			            public int getIndicatorColor(int position) {
			                return getResources().getColor(R.color.list_item_bg_dark_super_highlight);
			            }
			    });	
		    }else{
		    	indicator.setBackgroundColor(getResources().getColor(R.color.slidingtab_background_light));
		    }	    
		    return rootView;
	}
	
	public  void initData(){
		if(exportpageTabTitle == null){
			exportpageTabTitle = new ArrayList<TabTitleObject>();
			String[] itemTitle = getResources().getStringArray(
					R.array.home_tab_name);
			String[] itemTag = getResources().getStringArray(
					R.array.home_tab_tag);
			for (int i = 0; i < itemTitle.length; ++i) {
				exportpageTabTitle.add(new TabTitleObject(itemTitle[i],
						itemTag[i], null));
			}
		} 
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		super.onSaveInstanceState(outState);
	}
	
}

