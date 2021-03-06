package com.kuibu.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.TabHotInfo;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.TabPageViewAdapter;
import com.viewpagerindicator.SlidingTabIndicator;

public class NotifyTabPageFragment extends Fragment{
	private final int DEFAULT_BACKUP_TABPAGE_NUM = 2;	
	private List<TabHotInfo> tabTitles = null; 
	private TabPageViewAdapter adapter= null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {			
			initData();						
			boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(getActivity(),
					StaticValue.PrefKey.DARK_THEME_KEY, false); 
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
			ViewPager pager = (ViewPager)rootView.findViewById(R.id.pager);
			if(adapter == null){
				adapter =  new TabPageViewAdapter(getActivity().getSupportFragmentManager(),
						tabTitles,new NotifyConstruct());
				pager.setAdapter(adapter);
			}
			else{
				adapter.updateView(tabTitles);
			}		    
			pager.setOffscreenPageLimit(DEFAULT_BACKUP_TABPAGE_NUM); 
			SlidingTabIndicator indicator = (SlidingTabIndicator)rootView.findViewById(R.id.indicator);
		    indicator.setViewPager(pager);		    
		    if(isDarkTheme){
		    	indicator.setBackgroundResource(R.drawable.abc_ab_solid_dark_holo);
		    	indicator.setCustomTabColorizer(new SlidingTabIndicator.TabColorizer() {
			            @Override
			            public int getIndicatorColor(int position) {
			            	return ContextCompat.getColor(getActivity(),
			            			R.color.list_item_bg_dark_super_highlight);
			            }
			    });	
		    }else{
		    	indicator.setBackgroundColor(ContextCompat.getColor(getActivity(),
		    			R.color.slidingtab_background_light));
		    }		    return rootView;
	}
	
	public  void initData(){
		if(tabTitles == null){
			tabTitles = new ArrayList<TabHotInfo>();
			String[] itemTitle = getResources().getStringArray(
					R.array.notify_message_name);
			String[] itemTag = getResources().getStringArray(
					R.array.notify_message_tag);
			for (int i = 0; i < itemTitle.length; ++i) {
				tabTitles.add(new TabHotInfo(itemTitle[i],
						itemTag[i], null));
			}
		} 
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(tabTitles!=null){
			tabTitles.clear();
			tabTitles = null ;
		}
	}
	
	
}
