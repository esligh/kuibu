package com.kuibu.module.activity;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.StaticValue;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageDetailActivity extends BaseActivity{
    private PhotoView photo;
    private final Handler handler = new Handler();
    private boolean rotating = false;
	private String imgUrl ; 

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photo = new PhotoView(this);
        imgUrl =  getIntent().getStringExtra(StaticValue.IMG_URL);
        String path = KuibuUtils.getCacheImgFilePath(this, imgUrl);
        ImageLoader.getInstance().displayImage(Constants.URI_PREFIX+path,photo);
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
    	MenuItem save=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
        		StaticValue.MENU_ITEM.SAVE_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,"保存");	        
		save.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
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
			break;
        	case StaticValue.MENU_ITEM.SAVE_ID:    
        		File srcFile = null ; 
                String path = KuibuUtils.getCacheImgFilePath(this, imgUrl);
                if(FileUtils.fileIsExists(path)){
                	srcFile = new File(path);
                }else{
                    srcFile = ImageLoader.getInstance().getDiskCache().
            			get(getIntent().getStringExtra(StaticValue.IMG_URL));
                }
        		if(srcFile!=null){
        			File dir = new File(StorageUtils.getFileDirectory(getApplicationContext()).
        					getAbsolutePath()+Constants.Config.SAVE_IMG_DIR); 
        			if(!dir.exists())
        				dir.mkdirs();
        			
        			File newPath = new File(dir, String.valueOf(System.currentTimeMillis()) +".jpg");
        			boolean isOk = srcFile.renameTo(newPath);
        			if(isOk){ 	
        				Toast.makeText(this, newPath.getPath(), Toast.LENGTH_LONG).show();
        			}
        		}        		
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

