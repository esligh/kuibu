package com.kuibu.app.model.base;
import org.json.JSONObject;

import android.support.v4.app.Fragment;

import com.kuibu.data.global.KuibuApplication;
import com.kuibu.module.iterfaces.IEventHandler;

public abstract class BaseFragment extends Fragment implements IEventHandler{		
	
	protected boolean isVisible ; 
			
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onDetach();
	}

	@Override
	public void eventResponse(JSONObject entity){
		return ; 
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if(getUserVisibleHint()){
			isVisible = true;
			onVisible();
		}else{
			isVisible = false;
			onInvisible();
		}
	}
	
	protected void onVisible()
	{
		lazyLoad();
	}
	
	protected void onInvisible()
	{
		
	}
		
	protected void lazyLoad()
	{
		
	};
}