package com.kuibu.model.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.kuibu.module.activity.KuibuMainActivity;
import com.kuibu.module.activity.R;

public class SixthGuideFragment extends BaseGuideFragment {

    private View mLayoutLogo;
    private LinearLayout goWorldLayout; 
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView =  inflater.inflate(R.layout.fragment_guide_sixth, container, false);
    	goWorldLayout = (LinearLayout)rootView.findViewById(R.id.goto_world);
    	goWorldLayout.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),KuibuMainActivity.class);
				startActivity(intent);				
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				getActivity().finish();
			}
		});
    	
    	return rootView ; 
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLayoutLogo = view.findViewById(R.id.guide_item_1);

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.splash_guide_logo);
        mLayoutLogo.startAnimation(anim);
    }

    @Override
    public int[] getChildViewIds() {
        return new int[]{};
    }

    @Override
    public int getRootViewId() {
        return R.id.layout_guide_sixth;
    }
}
