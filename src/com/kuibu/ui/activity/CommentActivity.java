package com.kuibu.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kuibu.app.model.base.BaseActivity;
import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.MultiStateView.ViewState;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.entity.CommentItemBean;
import com.kuibu.module.activity.R;
import com.kuibu.module.net.PublicRequestor;
import com.kuibu.module.presenter.CommentPresenterImpl;
import com.kuibu.module.presenter.interfaces.CommentPresenter;
import com.kuibu.ui.view.interfaces.CommentView;

public class CommentActivity extends BaseActivity implements
	OnItemClickListener ,CommentView{
	
	private CommonAdapter<CommentItemBean> commentItemAdapter;	
	private PullToRefreshListView commentList;
	private ImageButton btnSend;
	private EditText editContent;
	private MenuItem cancelReply;
	private MultiStateView mMultiStateView;	
	private CommentPresenter mPresenter ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comment_list_detail);
		mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
		mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        .setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mMultiStateView.setViewState(MultiStateView.ViewState.LOADING);
				mPresenter.getCommentList();
			}   	
        });
		commentList = (PullToRefreshListView) findViewById(R.id.comment_list);
		commentList.setMode(Mode.PULL_FROM_END);
		commentList.setPullToRefreshOverScrollEnabled(false);
		commentList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				mPresenter.getCommentList();
				String label = DateUtils.formatDateTime(getApplicationContext(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
			}
		});
		editContent = (EditText) findViewById(R.id.edit_comment);
		btnSend = (ImageButton) findViewById(R.id.btn_send);
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if(Session.getSession().isLogin()){
					mPresenter.addComment();
					editContent.setText("");
				}else{
					Toast.makeText(CommentActivity.this, getString(R.string.need_login), 
							Toast.LENGTH_SHORT).show();
				}				
			}
		});
		commentItemAdapter = new CommonAdapter<CommentItemBean>(this, null,R.layout.comment_list_item){
			@Override
			public void convert(ViewHolder holder, CommentItemBean item) {
				// TODO Auto-generated method stub
				holder.setTvText(R.id.user_name_tv,item.getUserName());
				holder.setTvText(R.id.content_tv,item.getContent());
				holder.setTvText(R.id.date_tv,item.getGenDate());		
				String url = item.getUserPicUrl();
				
				if(TextUtils.isEmpty(url) || url.equals("null")){
					holder.setImageResource(R.id.user_photo_iv,R.drawable.default_pic_avata);						
				}else{
					holder.setImageByUrl(R.id.user_photo_iv, url,Constants.defaultAvataOptions);
				}		
			}				
		};
		commentList.setAdapter(commentItemAdapter);
		commentList.setOnItemClickListener(this);
		mPresenter = new CommentPresenterImpl(this);
		mPresenter.getCommentList();
	}
	
	@Override
	public void refreshList(List<CommentItemBean> datas) {
		commentItemAdapter.refreshView(datas);
	}

	@Override
	public void onItemClick(AdapterView<?> viewAdpater, View view,
			int position, long id) {
		mPresenter.setCurrentItem((CommentItemBean) viewAdpater.getAdapter()
				.getItem(position));
		mPresenter.setCurretPos(position);
		if(!Session.getSession().isLogin())
			return ; 
		AlertDialog.Builder builder = new Builder(CommentActivity.this);
		if (Session.getSession().getuId().equals(mPresenter.getCurrentItem().getCreateBy())) {
			builder.setItems(getResources().getStringArray(R.array.comment_menu_item1),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int position) {
							switch(position){
								case 0:
									mPresenter.delComment(mPresenter.getCurrentItem(),
											mPresenter.getCurrentPos()-1);
								break;
							}
						}
					});
		} else {
			// context
			builder.setItems(getResources().getStringArray(R.array.comment_menu_item0),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int position) {
							switch (position) {
							case 0: // reply
								if (Session.getSession().isLogin()) {
									editContent.setHint(new StringBuffer(getString(R.string.reply))
											.append(" ").append(mPresenter.getCurrentItem().getUserName()));
									cancelReply.setVisible(true);
									Map<String, String> tag = new HashMap<String, String>();
									tag.put("receiver_id", mPresenter.getCurrentItem().getCreateBy());
									tag.put("receiver_name", mPresenter.getCurrentItem().getUserName());
									editContent.setTag(tag);
								} else {
									Toast.makeText(CommentActivity.this,
											getString(R.string.need_login), Toast.LENGTH_SHORT)
											.show();
								}
								break;
							case 1:// report
								Map<String,String> params = new HashMap<String,String>();
								params.put("accuser_id", Session.getSession().getuId());
								params.put("defendant_id", mPresenter.getCurrentItem().getCreateBy());
								KuibuUtils.showReportView(CommentActivity.this, params);
								break;
							}
						}
					});

		}
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		cancelReply = menu.add(StaticValue.MENU_GROUP.CANCEL_ACTIONBAR_GROUP,
				StaticValue.MENU_ITEM.CANCEL_ID,
				StaticValue.MENU_ORDER.CANCEL_ORDER_ID, getString(R.string.cancel_reply));

		cancelReply.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		cancelReply.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.onBackPressed();
				break;			
			case StaticValue.MENU_ITEM.CANCEL_ID:
				cancelReply.setVisible(false);
				editContent.setHint(getString(R.string.write_down_comment));
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("comment_count", mPresenter.getCommentCount());
		setResult(RESULT_OK, intent);		
		super.onBackPressed(); //afeter setResult 
		overridePendingTransition(R.anim.anim_slide_out_right,
				R.anim.anim_slide_in_right);
	}


	@Override
	public Intent getDataIntent() {
		return getIntent();		
	}

	@Override
	public void setMultiStateView(ViewState state) {
		// TODO Auto-generated method stub
		mMultiStateView.setViewState(state);	
	}

	@Override
	public void stopRefresh() {
		commentList.onRefreshComplete();
	}

	@Override
	public String getComment() {
		return editContent.getText().toString();
	}

	@Override
	public void setCancelMenuVisibility(boolean visible) {
		// TODO Auto-generated method stub
		cancelReply.setVisible(visible);
	}

	@Override
	public boolean isCancelMenuVisible() {
		// TODO Auto-generated method stub
		return cancelReply.isVisible();
	}

	@Override
	public void setContentHint(String hint) {
		// TODO Auto-generated method stub
		editContent.setHint(hint);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getContentTag() {
		// TODO Auto-generated method stub
		return (Map<String,String>)editContent.getTag();
	}
	
}
