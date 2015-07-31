package com.kuibu.model.welcome;

import android.animation.ArgbEvaluator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextSwitcher;

import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.KuibuMainActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.activity.RegisterActivity;
import com.viewpagerindicator.CirclePageIndicator;

public class WelcomeActivity extends ActionBarActivity {

    final float PARALLAX_COEFFICIENT = 1.2f;
    final float DISTANCE_COEFFICIENT = 0.5f;

    private TextSwitcher mTextSwitcher;
    private ViewPager mPager;
    private CirclePageIndicator mPagerIndicator;

    private FragmentAdapter mAdapter;
    private Button loginBtn , regBtn ; 
    private SparseArray<int[]> mLayoutViewIdsMap = new SparseArray<int[]>();

    private void addGuide(BaseGuideFragment fragment) {
        mAdapter.addItem(fragment);
        mLayoutViewIdsMap.put(fragment.getRootViewId(), fragment.getChildViewIds());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide);
        loginBtn = (Button)findViewById(R.id.login);
        loginBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,KuibuMainActivity.class);
				intent.putExtra(StaticValue.MAINWITHDLG, true);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);				
				finish();
			}
		});
        regBtn = (Button)findViewById(R.id.register);
        regBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WelcomeActivity.this,RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
        mTextSwitcher = (TextSwitcher) findViewById(R.id.tip);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mAdapter = new FragmentAdapter(getSupportFragmentManager());
        addGuide(new FirstGuideFragment());
        addGuide(new SecondGuideFragment());
        addGuide(new ThirdGuideFragment());
        addGuide(new FourthGuideFragment());
        addGuide(new FifthGuideFragment());
        addGuide(new SixthGuideFragment());

        mPager.setAdapter(mAdapter);
        mPagerIndicator.setViewPager(mPager);

        mPager.setPageTransformer(true, new ParallaxTransformer(PARALLAX_COEFFICIENT, DISTANCE_COEFFICIENT));
        mPagerIndicator.setOnPageChangeListener(new GuidePageChangeListener());
    }

    class ParallaxTransformer implements ViewPager.PageTransformer {

        float parallaxCoefficient;
        float distanceCoefficient;

        public ParallaxTransformer(float parallaxCoefficient, float distanceCoefficient) {
            this.parallaxCoefficient = parallaxCoefficient;
            this.distanceCoefficient = distanceCoefficient;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void transformPage(View page, float position) {
            float scrollXOffset = page.getWidth() * parallaxCoefficient;

            ViewGroup pageViewWrapper = (ViewGroup) page;
            int[] layer = mLayoutViewIdsMap.get(pageViewWrapper.getChildAt(0).getId());
            for (int id : layer) {
                View view = page.findViewById(id);
                if (view != null) {
                    view.setTranslationX(scrollXOffset * position);
                }
                scrollXOffset *= distanceCoefficient;
            }
        }
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        ArgbEvaluator mColorEvaluator;

        int mPageWidth, mTotalScrollWidth;

        int mGuideStartBackgroundColor, mGuideEndBackgroundColor;

        String[] mGuideTips;

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public GuidePageChangeListener() {
            mColorEvaluator = new ArgbEvaluator();
    		DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            mPageWidth = dm.widthPixels;
            mTotalScrollWidth = mPageWidth * mAdapter.getCount();

            mGuideStartBackgroundColor = getResources().getColor(R.color.guide_start_background);
            mGuideEndBackgroundColor = getResources().getColor(R.color.guide_end_background);

            mGuideTips = getResources().getStringArray(R.array.array_guide_tips);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            float ratio = (mPageWidth * position + positionOffsetPixels) / (float) mTotalScrollWidth;
            Integer color = (Integer) mColorEvaluator.evaluate(ratio, mGuideStartBackgroundColor, mGuideEndBackgroundColor);
            mPager.setBackgroundColor(color);
        }

        @Override
        public void onPageSelected(int position) {
            if (mGuideTips != null && mGuideTips.length > position) {
                mTextSwitcher.setText(mGuideTips[position]);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
