package com.kuibu.module.fragment;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.module.iterf.IEventHandler;

public abstract class BaseFragment extends Fragment implements IEventHandler{		
		
	public BaseFragment() {
        super();
        if (getArguments() == null)
            setArguments(new Bundle());
    }
	protected void onTabPageChanged(){}
	
	@Override
	public void eventResponse(JSONObject entity)
	{
	}
}