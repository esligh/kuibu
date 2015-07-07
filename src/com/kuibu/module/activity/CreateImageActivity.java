package com.kuibu.module.activity;


import java.io.File;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.kuibu.common.utils.Bimp;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.adapter.ImageScanGridAdapter;
import com.kuibu.module.iterf.ICamera;
import com.kuibu.module.menu.PopupWindowsMenu;

public class CreateImageActivity extends ActionBarActivity implements ICamera{
	
	private GridView gridView;
	private ImageScanGridAdapter adapter; // 适配器
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.gen_image_content);
        gridView = (GridView) findViewById(R.id.image_gridview);
        Bimp.clear();
		gridView = (GridView) findViewById(R.id.image_gridview);		
		adapter = new ImageScanGridAdapter(this,StaticValue.IMAGE_SCAN.IMAGE_MAX);
		adapter.update();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id)
			{
				if (position == Bimp.bmp.size())
				{
					new PopupWindowsMenu(CreateImageActivity.this, gridView,StaticValue.IMAGE_SCAN.IMAGE_MAX);
				} else
				{
					Intent intent = new Intent(CreateImageActivity.this,
							PhotoActivity.class);
					intent.putExtra("ID", position);
					startActivity(intent);
				}
			}
		});
	}
		
	@Override
	protected void onRestart()
	{	
		adapter.update();
		super.onRestart();
		
	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState) {
		// TODO Auto-generated method stub		
		super.onSaveInstanceState(saveInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Bimp.clear();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Bimp.clear();
	}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        //添加菜单项
	        MenuItem add=menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
	        		StaticValue.MENU_ITEM.SAVE_ID,StaticValue.MENU_ORDER.SAVE_ORDER_ID,"保存");
	        MenuItem cancel=menu.add(StaticValue.MENU_GROUP.CANCEL_ACTIONBAR_GROUP,
	        		StaticValue.MENU_ITEM.CANCEL_ID,StaticValue.MENU_ORDER.CANCEL_ORDER_ID,"取消");
	        //绑定到ActionBar 
	        add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
	        return true;
	 }
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        switch(item.getItemId()){
	        	case StaticValue.MENU_ITEM.SAVE_ID:
	        		Toast.makeText(getApplicationContext(), "menu_save", Toast.LENGTH_SHORT).show();
	        		break;
	        	case StaticValue.MENU_ITEM.CANCEL_ID:
	        		Toast.makeText(getApplicationContext(), "menu_cancel", Toast.LENGTH_SHORT).show();
	        		break;
	        	default:
	        		break;
	        }
	        return super.onOptionsItemSelected(item);
	 }

		private static final int TAKE_PICTURE = 0x000012;
		private String image_path = "";
			
		@Override
	    public void onConfigurationChanged(Configuration newConfig) 
		{
			super.onConfigurationChanged(newConfig);
	    }
		
		@Override
		public void takePhotoByCamera() {
			String status=Environment.getExternalStorageState(); 
			if(status.equals(Environment.MEDIA_MOUNTED)) 
			{
				File dir=new File(Environment.getExternalStorageDirectory() + "/caddy_image/"); 
				if(!dir.exists())
					dir.mkdirs(); 
				File file = new File(dir, String.valueOf(System.currentTimeMillis())
						+ ".jpg");
				image_path = file.getPath();
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}else{ 
				Toast.makeText(CreateImageActivity.this, "没有储存卡",Toast.LENGTH_LONG).show(); 
			}
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
			if(resultCode == RESULT_OK){
				switch (requestCode){
				case TAKE_PICTURE:
					if (Bimp.drr.size() < StaticValue.IMAGE_SCAN.IMAGE_TEXT_MAX
							&& resultCode == -1){
						Bimp.drr.add(image_path);
					}
					break;
				}
			}
			super.onActivityResult(requestCode, resultCode, data);
		}
}
