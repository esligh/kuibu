package com.kuibu.ui.activity;

import java.util.List;

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
import com.kuibu.custom.widget.BorderScrollView;
import com.kuibu.custom.widget.BorderScrollView.OnBorderListener;
import com.kuibu.custom.widget.FButton;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.entity.CollectionItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.ImageGridAdapter;
import com.kuibu.module.presenter.FavoriteBoxInfoPresenterImpl;
import com.kuibu.module.presenter.interfaces.FavoriteBoxInfoPresenter;
import com.kuibu.ui.view.interfaces.FavoriteBoxInfoView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FavoriteBoxInfoActivity extends BaseActivity implements
		OnBorderListener ,FavoriteBoxInfoView{
	
	private ListView mList;
	private MultiColumnListView mCardList ; 
	private RelativeLayout focusLayout;
	private View footerView;
	private BorderScrollView borderScrollView;
	private CommonAdapter<CollectionItemBean> infoAdapter;
	private ImageGridAdapter mCardApdater ; 
	private TextView titleView;
	private TextView descView;
	private ImageView creatorPicView;
	private TextView creatorName, creatorSignature, followCount;
	private FButton focusBtn;
	private RelativeLayout tagLayout;
	private MultiStateView mMultiStateView;
	private FavoriteBoxInfoPresenter mPresenter;
	
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collectpack_infolist);
		mPresenter = new FavoriteBoxInfoPresenterImpl(this);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.loadBoxInfo();
			}   	
        });
		focusLayout = (RelativeLayout) findViewById(R.id.collect_pack_info_focus_rl);
		String uid = Session.getSession().getuId();
		String createBy = getIntent().getStringExtra("create_by");
		if (!TextUtils.isEmpty(uid) && uid.equals(createBy)){
			focusLayout.setVisibility(View.GONE);
		}else{
			focusLayout.setVisibility(View.VISIBLE);
		}		
		tagLayout = (RelativeLayout) findViewById(R.id.tags_layout);
		tagLayout.setVisibility(View.GONE);
		
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
		focusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Session.getSession().isLogin()) {
					mPresenter.followBox();
				} else {
					Toast.makeText(FavoriteBoxInfoActivity.this, getString(R.string.need_login),
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
				showUserview();
			}
		});		
		mList = (ListView) findViewById(R.id.collectpack_cards_list);
		mCardList = (MultiColumnListView)findViewById(R.id.grid_cards_list);
		if(StaticValue.EDITOR_VALUE.COLLECTION_IMAGE.equals(mPresenter.getType())){
			mCardList.setVisibility(View.VISIBLE);
			mCardList.setVerticalScrollBarEnabled(false);
			mCardApdater = new ImageGridAdapter(this, null,R.layout.item_grid_image,false);
			mCardList.setAdapter(mCardApdater);
			mCardList.addFooterView(footerView);
			mCardList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(PLA_AdapterView<?> viewAdapter, View view,
						int position, long id) {
					CollectionBean bean = (CollectionBean)viewAdapter.getAdapter().getItem(position);
					Intent intent = new Intent(FavoriteBoxInfoActivity.this,
							PCollectionDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, bean.getCid());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}				
			});
		}else{
			mList.setVisibility(View.VISIBLE);
			mList.addFooterView(footerView);
			infoAdapter = new CommonAdapter<CollectionItemBean>(this, null,
					R.layout.collectpack_info_list_item){
				@Override
				public void convert(ViewHolder holder, CollectionItemBean item) {
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
			mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> viewAdapter, View view,
						int position, long id) {
					Intent intent = new Intent(FavoriteBoxInfoActivity.this,
							WCollectionDetailActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, mPresenter.
							getDateItem(position).getId());	
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_CISN,mPresenter.
							getDateItem(position).getCisn());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}
			});
		}
		mPresenter.loadBoxInfo();
		mPresenter.loadUserinfo();
		mPresenter.loadBoxList();
	}

	private void showUserview() {
		Intent intent = new Intent(FavoriteBoxInfoActivity.this,
				UserInfoActivity.class);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.SHOWLAYOUT, true);
		intent.putExtra(StaticValue.USERINFO.USER_ID,
				(String) mPresenter.getUserinfo().get("uid"));
		intent.putExtra(StaticValue.USERINFO.USER_NAME,
				(String) mPresenter.getUserinfo().get("name"));
		intent.putExtra(StaticValue.USERINFO.USER_SIGNATURE,
				(String) mPresenter.getUserinfo().get("signature"));
		intent.putExtra(StaticValue.USERINFO.USER_PHOTO,
				(String) mPresenter.getUserinfo().get("photo"));
		intent.putExtra(StaticValue.USERINFO.USER_SEX,
				(String) mPresenter.getUserinfo().get("sex"));
		intent.putExtra(StaticValue.USERINFO.USER_ISFOLLOW, mPresenter.isFollow());
		startActivity(intent);
		overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		super.onBackPressed();
		overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
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
	public void onBottom() {
		footerView.setVisibility(View.VISIBLE);
		mPresenter.loadBoxList();
	}

	
	@Override
	public void onTop() {
		borderScrollView.loadComplete();
	}

	@Override
	public Intent getDataIntent() {
		// TODO Auto-generated method stub
		return getIntent();
	}

	@Override
	public void setBoxTitle(String title) {
		// TODO Auto-generated method stub
		titleView.setText(title);
	}

	@Override
	public void setBoxDesc(String desc) {
		// TODO Auto-generated method stub
		descView.setText(desc);
	}

	@Override
	public void setFollowCount(String count) {
		// TODO Auto-generated method stub
		followCount.setText(count);

	}

	@Override
	public void setFollowBtnColor(int color) {
		// TODO Auto-generated method stub
		focusBtn.setButtonColor(color);
	}

	@Override
	public void setFollowBtnText(String text) {
		// TODO Auto-generated method stub

		focusBtn.setText(text);
	}

	@Override
	public void refreshPList(List<CollectionBean> data) {
		// TODO Auto-generated method stub
		mCardApdater.refreshView(data);
		setListViewHeightBasedOnChildren(mCardList);
	}

	@Override
	public void refreshWList(List<CollectionItemBean> data) {
		// TODO Auto-generated method stub
		infoAdapter.refreshView(data);
	}

	@Override
	public void setUserName(String name) {
		// TODO Auto-generated method stub
		creatorName.setText(name);
	}

	@Override
	public void setUserPic(String url) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(url)) {								
			creatorPicView.setImageResource(R.drawable.default_pic_avata); 
		} else {								
			ImageLoader.getInstance().displayImage(url,creatorPicView);
		}
	}

	@Override
	public void setUserSignature(String signature) {
		// TODO Auto-generated method stub
		creatorSignature.setText(signature);	
	}

	@Override
	public void setBoxDescVisible(int visibility) {
		// TODO Auto-generated method stub
		descView.setVisibility(visibility);
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);

	}

	@Override
	public void loadComplete() {
		// TODO Auto-generated method stub
		borderScrollView.loadComplete();	
	}

	@Override
	public void setFooterViewVisible(int visibility) {
		// TODO Auto-generated method stub
		footerView.setVisibility(visibility);
	}

	@Override
	public String getFollowCount() {
		// TODO Auto-generated method stub
		return followCount.getText().toString().trim();
	}

	@Override
	public void setBarTitle(String title) {
		// TODO Auto-generated method stub
		
	}
	
}
