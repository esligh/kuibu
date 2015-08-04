package com.kuibu.module.activity;

import uk.co.senab.photoview.PhotoView;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.kuibu.data.global.StaticValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageDetailActivity extends BaseActivity{
    private PhotoView photo;
    private final Handler handler = new Handler();
    private boolean rotating = false;
	private DisplayImageOptions options;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		TypedArray typedArray = null;
		Resources.Theme theme = this.getTheme();
		if (isDarkTheme) {
			typedArray = theme.obtainStyledAttributes(R.style.Theme_Kuibu_AppTheme_Dark,
					new int[] {R.attr.listItemDefaultImage });
		} else {
			typedArray = theme.obtainStyledAttributes(R.style.Theme_Kuibu_AppTheme_Light,
					new int[] { R.attr.listItemDefaultImage });
		}

		int listItemDefaultImageId = typedArray.getResourceId(0, 0);
		typedArray.recycle();
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(this.getResources().getDrawable(listItemDefaultImageId))
		.showImageForEmptyUri(this.getResources().getDrawable(listItemDefaultImageId))
		.showImageOnFail(this.getResources().getDrawable(listItemDefaultImageId))
		.cacheInMemory(false)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();

        photo = new PhotoView(this);
        String imgUrl =  getIntent().getStringExtra(StaticValue.IMG_URL);
        ImageLoader.getInstance().displayImage(imgUrl,photo,options,null);        
        setContentView(photo);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "向右旋转");
        menu.add(Menu.NONE, 1, Menu.NONE, "向左旋转");
        menu.add(Menu.NONE, 2, Menu.NONE, "自由旋转");
        menu.add(Menu.NONE, 3, Menu.NONE, "翻转");
        menu.add(Menu.NONE, 4, Menu.NONE, "重置");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case android.R.id.home:
        		finish();
//        		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
			break;
			
            case 0:
                photo.setRotationBy(90);
                return true;
            case 1:
                  photo.setRotationBy(-90);
                return true;
            case 2:
            	toggleRotation();            	            	
                return true;
            case 3:
            	photo.setRotationBy(180);
                return true;
            case 4:
            	photo.setRotationTo(0);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleRotation() {
        if (rotating) {
            handler.removeCallbacksAndMessages(null);
        } else {
            rotateLoop();
        }
        rotating = !rotating;
    }

    private void rotateLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                photo.setRotationBy(1);
                rotateLoop();
            }
        }, 15);
    }

}

