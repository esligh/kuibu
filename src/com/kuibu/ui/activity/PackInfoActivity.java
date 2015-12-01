package com.kuibu.ui.activity;

import java.util.List;
import java.util.Map;

import me.gujun.android.taggroup.TagGroup;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huewu.pla.lib.MultiColumnListView;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.ImageGridAdapter;
import com.kuibu.module.presenter.PackInfoPresenterImpl;
import com.kuibu.module.presenter.interfaces.PackInfoPresenter;
import com.kuibu.ui.view.interfaces.PackInfoView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PackInfoActivity extends BaseActivity implements
		OnBorderListener ,PackInfoView{
	
	private ListView mList;
	private MultiColumnListView mCardList ; 
	private RelativeLayout focusLayout;
	private View footerView;
	private BorderScrollView borderScrollView;
	private CommonAdapter<CollectionItemBean> infoAdapter;
	private ImageGridAdapter mCardApdater ;  
	private String packId;
	private TextView titleView;
	private TextView descView;
	private ImageView creatorPicView;
	private TextView creatorName, creatorSignature, followCount;
	private FButton focusBtn;
	private TagGroup tagGroup;
	private RelativeLayout tagLayout;
	private MultiStateView mMultiStateView;
	private PackInfoPresenter mPresenter ; 
	
	@SuppressWarnings("deprecation")
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collectpack_infolist);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadPackInfo(packId);
			}   	
        });
		focusLayout = (RelativeLayout) findViewById(R.id.collect_pack_info_focus_rl);
		boolean flag = getIntent().getBooleanExtra(
				StaticValue.HIDE_FOCUS, false);
		if (flag)
			focusLayout.setVisibility(View.GONE);
		tagGroup = (TagGroup) findViewById(R.id.topic_name_tags);
		
		if(isDarkTheme){
			int color =getResources().getColor(R.color.list_view_bg_dark);
			tagGroup.setTagBackGroundColor(color);
		}else{
			int color = getResources().getColor(R.color.white);
			tagGroup.setTagBackGroundColor(color);
		}
		tagLayout = (RelativeLayout) findViewById(R.id.tags_layout);
		tagLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {		
				Intent intent = new Intent(PackInfoActivity.this,
					TopicListActivity.class);
				intent.putExtra("topic_id", mPresenter.getTopids());
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		borderScrollView = (BorderScrollView) findViewById(R.id.collect_pack_scroll_view);
		borderScrollView.setOnBorderListener(this);
		footerView = LayoutInflater.from(this).inflate(R.layout.footer, null);
		footerView.setVisibility(View.GONE);
		titleView = (TextView) findViewById(R.id.collect_pack_title_tv);
		descView = (TextView) findViewById(R.id.collect_pack_desc_tv);
		creatorPicView = (ImageView) findViewById(R.id.pack_creator_pic_iv);
		creatorName = (TextView) findViewById(R.id.pack_creator_name_tv);
		creatorSignature = (TextView) findViewById(R.id.pack_creator_signature_tv);
		focusBtn = (FButton) findViewById(R.id.focus_collectpack_bt);
		followCount = (TextView) findViewById(R.id.follow_count_tv);
		packId = getIntent().getStringExtra("pack_id");
		focusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Session.getSession().isLogin()) {
					mPresenter.follow(packId);
				} else {
					Toast.makeText(PackInfoActivity.this, getString(R.string.need_login),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		creatorPicView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showUserview();
			}
		});
		creatorName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				showUserview();
			}
		});
		
		mList = (ListView) findViewById(R.id.collectpack_cards_list);
		mCardList = (MultiColumnListView)findViewById(R.id.grid_cards_list);
		mPresenter = new PackInfoPresenterImpl(this);
		mPresenter.loadPackInfo(packId);
	}
	
	
	
	public void setListViewHeightBasedOnChildren(MultiColumnListView listView) {    
        ListAdapter listAdapter = listView.getAdapter();    
        if (listAdapter == null) {  
            return;    
        }     
        int totalHeight =0; 
        int count = (listAdapter.getCount()+1)/2; 
        for (int i = 0; i < count; i++) {    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0);   
            totalHeight += listItem.getMeasuredHeight();    
        }      
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight;    
        listView.setLayoutParams(params);    
   }
	
	@Override
	protected void onStop(){
		// TODO Auto-generated method stub
		KuibuApplication.getInstance().cancelPendingRequests(this);
		super.onStop();		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
	}	

	@Override
	public void onBottom() {
		// TODO Auto-generated method stub
		footerView.setVisibility(View.VISIBLE);
		mPresenter.loadPackList(packId);
	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();
	}
	
	private void showUserview() {
		Intent intent = new Intent(PackInfoActivity.this,
				UserInfoActivity.class);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		Map<String,String> packInfo = mPresenter.getPackInfo();
		intent.putExtra(StaticValue.USERINFO.USER_ID,
				packInfo.get("create_by"));
		intent.putExtra(StaticValue.USERINFO.USER_NAME,
				packInfo.get("user_name"));
		intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
				packInfo.get("signature"));
		intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
				packInfo.get("photo"));
		intent.putExtra(StaticValue.USERINFO.USER_SEX,
				packInfo.get("sex"));		
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}

	@Override
	public void loadComplete() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();
	}

	@Override
	public void setFooterVisible(int visibility) {
		// TODO Auto-generated method stub
		footerView.setVisibility(visibility);
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);
		
	}

	@Override
	public void setPackInfoView(Map<String, String> info) {
		// TODO Auto-generated method stub
		if(info == null){
			return ; 
		}			
		titleView.setText(info.get("pack_name"));
		String desc = info.get("desc");
		if(TextUtils.isEmpty(info.get("desc"))){
			descView.setText(getString(R.string.no_desc));
		}else{
			descView.setText(desc);
		}
		if(Session.getSession().getuId().equals(info.get("create_by"))){
			focusLayout.setVisibility(View.GONE);
		}else{
			focusLayout.setVisibility(View.VISIBLE);			
			if (mPresenter.isFollowed()) {
				int btnColor = getResources().getColor(
						R.color.fbutton_color_concrete);
				focusBtn.setButtonColor(btnColor);
				focusBtn.setText(getString(R.string.btn_cancel_focus));
			}
			followCount.setText(DataUtils.formatNumber(info.get("focus_count")));
		}
									
		creatorName.setText(info.get("user_name"));		
		creatorSignature.setText(info.get("signature"));
		String url = info.get("photo");
		if (TextUtils.isEmpty(url)) {
			creatorPicView.setImageResource(R.drawable.default_pic_avata);	
		} else {
			ImageLoader.getInstance().displayImage(url,creatorPicView);
		}
		String topic_names = info.get("topic_names");
		if(!topic_names.equals("null")){
			tagGroup.setTags(topic_names.split(","));
		}else{
			tagGroup.setVisibility(View.GONE);  
		}			
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setFollowCount() {
		// TODO Auto-generated method stub
		int count = Integer.parseInt(followCount.getText().toString().trim());
		if (mPresenter.isFollowed()) {
			followCount.setText(DataUtils.formatNumber(count-1));
			
			int btnColor = getResources().getColor(
					R.color.fbutton_color_green_sea);
			focusBtn.setButtonColor(btnColor);
			focusBtn.setText(getString(R.string.btn_focus));
			mPresenter.setFollow(false);
			
		} else {
			followCount.setText(DataUtils.formatNumber(count+1));
			int btnColor = getResources().getColor(
					R.color.fbutton_color_concrete);
			focusBtn.setButtonColor(btnColor);
			focusBtn.setText(getString(R.string.btn_cancel_focus));
			mPresenter.setFollow(true);
		}
	}

	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		setTitle(title);
	}

	@Override
	public void refreshCardView(List<CollectionBean> data) {
		// TODO Auto-generated method stub
		mCardApdater.refreshView(data);
		setListViewHeightBasedOnChildren(mCardList);
	}

	@Override
	public void refreshListView(List<CollectionItemBean> data) {
		// TODO Auto-generated method stub
		infoAdapter.refreshView(data);
	}

	@Override
	public void showList(String type) {
		// TODO Auto-generated method stub
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(type)){
			mCardList.setVisibility(View.VISIBLE);
			mCardList.setVerticalScrollBarEnabled(false);
			mCardList.addFooterView(footerView); 			
			mCardApdater = new ImageGridAdapter(this, null,R.layout.item_grid_image,false);
			mCardList.setAdapter(mCardApdater);
			mCardList.addFooterView(footerView);
			mCardList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(PLA_AdapterView<?> viewAdapter, View view,
						int position, long id) {
					CollectionBean bean = (CollectionBean)viewAdapter.getAdapter().getItem(position);
					Intent intent = new Intent(PackInfoActivity.this,
							CollectionImageDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, bean.getCid());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}				
			});
		}else{
			mList.setVisibility(View.VISIBLE);
			mList.addFooterView(footerView);
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> viewAdapter, View view,
						int position, long id) {
					CollectionItemBean item = (CollectionItemBean)viewAdapter.
							getAdapter().getItem(position);
					Intent intent = new Intent(PackInfoActivity.this,
							CollectionDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, item.getId());
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN, item.getCisn());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
			infoAdapter = new CommonAdapter<CollectionItemBean>(this, null,
					R.layout.collectpack_info_list_item){
					@Override
					public void convert(ViewHolder holder,
							CollectionItemBean item) {
						// TODO Auto-generated method stub
						holder.setTvText(R.id.pack_info_collection_title,item.getTitle());
						holder.setTvText(R.id.pack_info_collection_vote_count,item.getVoteCount());

						String summary = item.getSummary().trim().replace("\n", "");
						if(TextUtils.isEmpty(summary) || summary.equals("null")){
							holder.setTvText(R.id.pack_info_collection_content,"(多图)");
						}else{
							holder.setTvText(R.id.pack_info_collection_content,summary);
						}
					}
				
			};
			mList.setAdapter(infoAdapter);
		}
	}	
}
