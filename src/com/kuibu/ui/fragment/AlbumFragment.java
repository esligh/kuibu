package com.kuibu.ui.fragment;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.AlbumAdapter;
import com.kuibu.module.presenter.AlbumPresenterImpl;
import com.kuibu.module.presenter.interfaces.AlbumPresenter;
import com.kuibu.ui.activity.AlbumPListActivity;
import com.kuibu.ui.activity.AlbumWListActivity;
import com.kuibu.ui.activity.CreateAlbumActivity;
import com.kuibu.ui.view.interfaces.AlbumView;

public class AlbumFragment extends Fragment implements AlbumView{

	private FButton createPackBtn;
	private ListView albumList;
	private AlbumAdapter albumAdapter;	 
	private AlbumPresenter mPresenter ; 
	
	@Override
	public void onAttach(Context context){
		super.onAttach(context);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.collectpack_listview,
				container, false);
		createPackBtn = (FButton) rootView
				.findViewById(R.id.create_collectpack_bt);
		albumList = (ListView) rootView.findViewById(R.id.collectpack_lv);		
		createPackBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(),CreateAlbumActivity.class);
				intent.putExtra("OPER", "CREATE");
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}	
		});
				
		albumList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpterView, View view, int position,
					long id) {
				CollectPackBean item = (CollectPackBean)adpterView.getAdapter()
												.getItem(position);				
				Intent intent = new Intent();
				if(StaticValue.SERMODLE.PACK_TYPE_PIC.equals(item.pack_type)){
					intent.setClass(getActivity(),AlbumPListActivity.class);										
				}else{
					intent.setClass(getActivity(),AlbumWListActivity.class);
				}
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID,item.getPack_id());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME, item.getPack_name());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT,item.getCollect_count());
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
		});
		
		albumList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adpterView, View view,
					final int position, long id) {
				final CollectPackBean item = (CollectPackBean)adpterView.getAdapter()
												.getItem(position);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle(getActivity().getString(R.string.operator));
				builder.setItems(getResources().getStringArray(R.array.collectpack_operator),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int pos) {
								switch (pos) {
								case 0:
									Intent intent = new Intent(getActivity(),CreateAlbumActivity.class);
									intent.putExtra("OPER", "MODIFY");
									intent.putExtra("_id", item._id);
									intent.putExtra(StaticValue.COLLECTPACK.PACK_ID, item.getPack_id());
									getActivity().startActivity(intent);
									break;
								case 1:					
									mPresenter.delAlbum(item);							
									break;
								}
							}
						});
				builder.show();
				return true;
			}			
		});		
		albumAdapter = new AlbumAdapter(getActivity(), null,R.layout.collectpack_list_item);
		albumList.setAdapter(albumAdapter);
		mPresenter = new AlbumPresenterImpl(this);
		
		return rootView;
	}
		
	@Override
	public void onResume() {
		super.onResume();
		mPresenter.queryAlbum();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mPresenter.closeDbConn();
	}

	@Override
	public Context getInstance() {
		// TODO Auto-generated method stub
		return getActivity();
	}

	@Override
	public void refreshList(List<CollectPackBean> data) {
		// TODO Auto-generated method stub
		albumAdapter.refreshView(data);
	}	
}
