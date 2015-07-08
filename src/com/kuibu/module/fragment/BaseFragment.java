package com.kuibu.module.fragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {		
		
	public BaseFragment() {
        super();
        if (getArguments() == null)
            setArguments(new Bundle());
    }
	protected void onTabPageChanged(){}
}