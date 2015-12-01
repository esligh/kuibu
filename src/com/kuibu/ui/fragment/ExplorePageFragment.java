package com.kuibu.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TabHotInfo;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TabPageViewAdapter;
import com.viewpagerindicator.SlidingTabIndicator;

public  class ExplorePageFragment extends Fragment 
{
	private final int DEFAULT_BACKUP_TABPAGE_NUM = 1; //close the pre-load action of viewpager	
	private List<TabHotInfo> exportpageTabTitle = null; 
	private TabPageViewAdapter adapter= null;
	private ViewPager pager ; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {					
			boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(getActivity(), StaticValue.PrefKey.DARK_THEME_KEY, false);
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
			prepareTabTitle();			
			adapter =  new TabPageViewAdapter(getChildFragmentManager(),
					exportpageTabTitle,new ExploreConstruct());
			pager.setAdapter(adapter);					    
			pager.setOffscreenPageLimit(DEFAULT_BACKUP_TABPAGE_NUM); 
			SlidingTabIndicator indicator = (SlidingTabIndicator)rootView.findViewById(R.id.indicator);
		    indicator.setViewPager(pager);
		   	indicator.setOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					
				}
			});
		   	
		    if(isDarkTheme){
		    	indicator.setBackgroundResource(R.drawable.abc_ab_solid_dark_holo);
		    	indicator.setCustomTabColorizer(new SlidingTabIndicator.TabColorizer() {
			            @Override
			            public int getIndicatorColor(int position) {
			                return ContextCompat.getColor(KuibuApplication.getContext(), 
			                		R.color.list_item_bg_dark_super_highlight);
			            }
			    });	
		    }else{
		    	indicator.setBackgroundColor(ContextCompat.getColor(KuibuApplication.getContext(),
		    			R.color.slidingtab_background_light));
		    }	    
		    return rootView;
	}
		
	public  void prepareTabTitle(){
		if(exportpageTabTitle == null){
			exportpageTabTitle = new ArrayList<TabHotInfo>();
			String[] itemTitle = getResources().getStringArray(R.array.home_tab_name);
			String[] itemTag = getResources().getStringArray(R.array.home_tab_tag);
			for (int i = 0; i < itemTitle.length; ++i) {
				exportpageTabTitle.add(new TabHotInfo(itemTitle[i],itemTag[i], null));
			}
		} 
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		exportpageTabTitle.clear();
	}	
}

