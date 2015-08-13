package com.kuibu.model.welcome;

import android.support.v4.app.Fragment;


public abstract class BaseGuideFragment extends Fragment {

    public abstract int[] getChildViewIds() ;

    public abstract int getRootViewId();
}
