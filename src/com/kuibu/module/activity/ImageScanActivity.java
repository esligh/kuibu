package com.kuibu.module.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.ImageBean;
import com.kuibu.module.adapter.GroupImageAdapter;

public class ImageScanActivity extends ActionBarActivity {
	private HashMap<String, List<String>> mGroupMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();

	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupImageAdapter adapter;
	private GridView mGroupGridView; 
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				// 关闭进度条
				mProgressDialog.dismiss();
				adapter = new GroupImageAdapter(ImageScanActivity.this,
						list = subGroupOfImage(mGroupMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_scan_main_view);
		mGroupGridView = (GridView) findViewById(R.id.image_scan_grid_id);

		getImages();
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGroupMap.get(list.get(position)
						.getFolderName());
				Intent mIntent = new Intent(ImageScanActivity.this,
						ShowImageActivity.class);

				mIntent.putStringArrayListExtra("data",
						(ArrayList<String>) childList);
				Intent intent =getIntent();
				Bundle b = intent.getExtras();
				int max = b.getInt("max");
				mIntent.putExtra("max",max);
				startActivityForResult(mIntent, 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode){
			case 1:
				if(data != null){
					String action = data.getAction();
					if(StaticValue.CHOOSE_PIC_OVER.equals(action)){
						finish();
					}	
				}			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getImages() {
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageScanActivity.this
						.getContentResolver();
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + " =? or "
								+ MediaStore.Images.Media.MIME_TYPE + " =? ",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);
				if (mCursor == null)
					return;
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					String parentName = new File(path).getParentFile()
							.getName();

					if (!mGroupMap.containsKey(parentName)) {
						List<String> childList = new ArrayList<String>();
						childList.add(path);
						mGroupMap.put(parentName, childList);
					} else {
						mGroupMap.get(parentName).add(path);
					}
				}

				mHandler.sendEmptyMessage(SCAN_OK);
				mCursor.close();
			}
		}).start();
	}

	private List<ImageBean> subGroupOfImage(
			HashMap<String, List<String>> mGroupMap) {
		if (mGroupMap.size() == 0)
			return null;
		List<ImageBean> list = new ArrayList<ImageBean>();
		Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));
			list.add(mImageBean);
		}
		return list;
	}

}
