package com.kuibu.module.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.kuibu.common.utils.Bimp;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.adapter.ChildAdapter;

/**
 * @author ThinkPad
 *
 */
public class ImageChooseActivity extends ActionBarActivity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_scan_group_view);

		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");

		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// 添加菜单项
		MenuItem add = menu.add(StaticValue.MENU_GROUP.SAVE_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.SAVE_ID,
				StaticValue.MENU_ORDER.SAVE_ORDER_ID, "完成");
		MenuItem cancel = menu.add(
				StaticValue.MENU_GROUP.CANCEL_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.CANCEL_ID,
				StaticValue.MENU_ORDER.CANCEL_ORDER_ID, "取消");
		// 绑定到ActionBar
		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		cancel.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case StaticValue.MENU_ITEM.SAVE_ID:
			List<Integer> items = adapter.getSelectItems();
			int max = getIntent().getIntExtra("max", 8);
			for (int i = 0; i < items.size(); i++) {
				if (Bimp.drr.size() < max) {
					String path = adapter.getSelectedPath(items.get(i)
							.intValue());
					Bimp.drr.add(path);
				}
			}

			intent.setAction(StaticValue.CHOOSE_PIC_OVER);
			this.sendBroadcast(intent);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case StaticValue.MENU_ITEM.CANCEL_ID:
			intent.setAction(StaticValue.CHOOSE_PIC_CANCEL);
			setResult(RESULT_OK, intent);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}