package com.kuibu.app.model.base;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.kuibu.data.global.KuibuApplication;
import com.kuibu.module.iterfaces.IEventHandler;

public abstract class BaseFragment extends Fragment implements IEventHandler{		
	
	public BaseFragment() {
        super();
        if (getArguments() == null)
            setArguments(new Bundle());
    }
		
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
	
}