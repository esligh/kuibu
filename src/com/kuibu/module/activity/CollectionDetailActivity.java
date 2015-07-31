package com.kuibu.module.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.core.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import us.feras.mdv.MarkdownView;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.AssetsUtils;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.DensityUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.custom.widget.ScrollViewExt;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.js.InJavaScriptObject;
import com.kuibu.model.js.WebViewClientExt;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.petebevin.markdown.MarkdownProcessor;

public class CollectionDetailActivity extends ActionBarActivity implements 
			ScrollViewExt.BottomListener, ScrollViewExt.onScrollListener {

	private static final int TOP_DISTANCE_Y = 120;
	private static final int TIME_ANIMATION = 300;
	private boolean isInTopDistance = true; 
	private TextView title_tv; 
	private LinearLayout layout_tools;
	private RelativeLayout layout_author; 
	private ScrollViewExt mScroller;
	private FrameLayout fl_top;
	private GestureDetector mGestureDetector;
    private Toolbar toolbar;
	private float viewSlop;
	private float lastY;
	private boolean isUpSlide;
	private boolean isToolHide;
	private boolean isTopHide = false;
	private boolean isMeasured = false;
	private MarkdownView contentView ;
	private CheckBox noIntrestcb,funcb;
	private ImageButton commentBtn,collectBtn ; 
	private TextView comment_text_tv; 
	private TextView author_name,author_desc,vote_count_tv; 
	private ImageView author_pic ; 	
	private String cid ; 
	private String create_by ;
	private MultiStateView mMultiStateView;
	private String cssFile ; 
	private boolean isDarkTheme ; 
	private boolean isCollected = false ; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences mPerferences = PreferenceManager
				.getDefaultSharedPreferences(this);		
		isDarkTheme= mPerferences.getBoolean(StaticValue.PrefKey.DARK_THEME_KEY, false);		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_collection_activity);
        mMultiStateView = (MultiStateView) findViewById(R.id.multiStateView);
        mMultiStateView.getView(MultiStateView.ViewState.ERROR).findViewById(R.id.retry)
        		.setOnClickListener(new View.OnClickListener() {
        	@Override
			public void onClick(View arg0) {              
				loadData();
				mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);				
			}
        });
		contentView =(MarkdownView)findViewById(R.id.show_text_content_tv);
		setUpWebViewDefaults();
		noIntrestcb = (CheckBox)findViewById(R.id.bottom_tools_no_intrest);
		funcb = (CheckBox)findViewById(R.id.bottom_tools_funny);
		commentBtn = (ImageButton)findViewById(R.id.bottom_tools_comment);
		comment_text_tv = (TextView)findViewById(R.id.comment_tv);
		collectBtn =(ImageButton)findViewById(R.id.bottom_tools_collect);
		vote_count_tv = (TextView)findViewById(R.id.show_text_vote_count);
        title_tv=(TextView)findViewById(R.id.show_text_title_tv);
        String title = getIntent().getStringExtra("title");
		title_tv.setText(title); 
		setTitle(title);		
        layout_author=(RelativeLayout)findViewById(R.id.layout_author_rl);
        author_name = (TextView)findViewById(R.id.show_text_author_tv);
        author_desc = (TextView)findViewById(R.id.show_text_author_desc);
        author_pic = (ImageView)findViewById(R.id.author_pic_iv);
		mScroller = (ScrollViewExt) findViewById(R.id.show_text_scroller);
		fl_top = (FrameLayout) findViewById(R.id.fl_top);
		toolbar=(Toolbar)findViewById(R.id.show_text_toolbar);	
		layout_tools = (LinearLayout)findViewById(R.id.layout_tools);
		create_by = getIntent().getStringExtra("create_by");
		if(Session.getSession().getuId().equals(create_by)){
			layout_tools.setVisibility(View.GONE);
		}
		setSupportActionBar(toolbar); 
        if(isDarkTheme){
        	setTheme(R.style.AppTheme_Dark);
			cssFile = Constants.WEBVIEW_DARK_CSSFILE;
        	layout_author.setBackgroundResource(R.drawable.layout_border_line_down_dark);
        	layout_tools.setBackgroundResource(R.drawable.layout_border_line_up_dark);
        	title_tv.setBackgroundColor(getResources().getColor(R.color.ground));
        }else{
			setTheme(R.style.AppTheme_Light);
			cssFile = Constants.WEBVIEW_LIGHT_CSSFILE;
        	layout_author.setBackgroundResource(R.drawable.layout_border_line_down_light);
        	layout_tools.setBackgroundResource(R.drawable.layout_border_line_up_light);
        	title_tv.setBackgroundColor(getResources().getColor(R.color.light_grey));
        }        				
		viewSlop = ViewConfiguration.get(this).getScaledTouchSlop();
		mGestureDetector = new GestureDetector(this, new DetailGestureListener());
		mScroller.setBottomListener(this);
		mScroller.setScrollListener(this);
		
		mScroller.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						lastY = event.getY();
						break;
					case MotionEvent.ACTION_MOVE:
						float disY = event.getY() - lastY;
						if (Math.abs(disY) > viewSlop) {
							isUpSlide = disY < 0;
							if (isUpSlide) { 
								if (!isToolHide)   
									hideTool();
							} else {
								if (isToolHide) 
									showTool();
							}
						}
						lastY = event.getY();
						break;
				}
				mGestureDetector.onTouchEvent(event);
				return false;
			}
		});		
		ViewTreeObserver viewTreeObserver = fl_top.getViewTreeObserver();
		viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!isMeasured) {
					FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
							.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					layoutParams.setMargins(0, toolbar.getHeight() + title_tv.getHeight(), 0, 0);
					layout_author.setLayoutParams(layoutParams);
					isMeasured = true;
					initData();
				}
				return true;
			}
		});		 
		
		commentBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CollectionDetailActivity.this,CommentActivity.class);
				intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, cid);
				intent.putExtra("create_by", create_by);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}
		});
		
		collectBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				if(Session.getSession().isLogin()){
					Intent intent = new Intent(CollectionDetailActivity.this,CollectFavoriteBoxActivity.class);
					intent.putExtra(StaticValue.SERMODLE.COLLECTION_ID, cid);
					intent.putExtra(StaticValue.COLLECTION.IS_COLLECTED, isCollected);
					startActivityForResult(intent,StaticValue.RequestCode.FAVORITE_BOX_REQCODE);
					overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
				}else{
					Toast.makeText(CollectionDetailActivity.this, "注册登录后才能收藏", Toast.LENGTH_SHORT).show();
				}	
			}
		});
		noIntrestcb.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				doAction(StaticValue.USER_ACTION.ACTION_OPPOSE_COLLECTION, noIntrestcb.isChecked());
			}
		});
		funcb.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				doAction(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION, funcb.isChecked());
			}
		});
		cid = getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setUpWebViewDefaults()
	{
		contentView.setBackgroundColor(0);
		contentView.getSettings().setJavaScriptEnabled(true);
		contentView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		InJavaScriptObject jsObj = new InJavaScriptObject(this);
		contentView.addJavascriptInterface(jsObj, "injectedObject");
		contentView.setWebViewClient(new WebViewClientExt(this));				
		contentView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);		
	}
	
	void initData()
	{
		cid = getIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);		
		String content = getIntent().getStringExtra("content");
		if(TextUtils.isEmpty(content)){
			loadData();
		}else{			
			String type = getIntent().getStringExtra("type");
			if(StaticValue.EDITOR_VALUE.COLLECTION_TEXTIMAGE.equals(type)){
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
				boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
				if(noPic){
					content = adjustMarkDownText(content);
				}
			}
			comment_text_tv.setText("评论 "+DataUtils.formatNumber(getIntent().getIntExtra("comment_count",0)));
			vote_count_tv.setText(DataUtils.formatNumber(getIntent().getIntExtra("vote_count",0)));
			author_name.setText(getIntent().getStringExtra("name"));
			author_desc.setText(getIntent().getStringExtra("signature"));
			
			MarkdownProcessor m = new MarkdownProcessor();
			content = m.markdown(contentWarpper(content));
			String template = AssetsUtils.loadText(this, Constants.TEMPLATE_DEF_URL);
			String html = template.replace("{cssFile}", cssFile);
			html = html.replace("{content}", content);			
			html = replaceImgTagFromHTML(html);
			contentView.loadDataWithBaseURL("http://", html, "text/html", "UTF-8", null);
			
//			MarkdownProcessor m = new MarkdownProcessor();
//			String html = m.markdown(contentWarpper(content));			
//			html = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" />"
//								+ html, cssFile);
//			replaceImgTagFromHTML(html);
//			contentView.loadDataWithBaseURL("http://", html, "text/html", "UTF-8", null);
			
			String url = getIntent().getStringExtra("photo");
			if(TextUtils.isEmpty(url) || url.equals("null")){
				String sex = getIntent().getStringExtra("sex");
				if(!TextUtils.isEmpty(sex) && sex.equals(StaticValue.SERMODLE.USER_SEX_MALE)){
					author_pic.setImageResource(R.drawable.default_pic_avatar_male);
				}else{
					author_pic.setImageResource(R.drawable.default_pic_avatar_female);
				}
			}else{
				ImageLoader.getInstance().displayImage(url, author_pic);
			}
			mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
		}
		loadActions();
	}
		
	private String replaceImgTagFromHTML(String html) {
		Document doc = Jsoup.parse(html);
		Elements es = doc.getElementsByTag("img");
		for (Element e : es) {
			String imgUrl = e.attr("src");
				e.attr("onclick", "openImage('" + imgUrl + "')");
		}
		return doc.html();
	}
	
	private void loadData()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", cid);
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/get_collectiondetail";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						JSONObject obj = new JSONObject(response.getString("result"));
						if(obj!=null){
							author_name.setText(obj.getString("name"));
							author_desc.setText(obj.getString("signature"));
							String content = obj.getString("content");
							contentView.loadMarkdown(contentWarpper(content),cssFile);								
							String url = obj.getString("photo");							
							if(TextUtils.isEmpty(url) || url.equals("null")){
								if(obj.getString("sex").equals(StaticValue.SERMODLE.USER_SEX_MALE)){
									author_pic.setImageResource(R.drawable.default_pic_avatar_male);
								}else{
									author_pic.setImageResource(R.drawable.default_pic_avatar_female);
								}
							}else{
								ImageLoader.getInstance().displayImage(url,
										author_pic);
							}				
							vote_count_tv.setText(DataUtils.formatNumber(obj.getInt("vote_count")));
							comment_text_tv.setText("评论 "+DataUtils.formatNumber(obj.getInt("comment_count")));														
						}
					}
					mMultiStateView.setViewState(MultiStateView.ViewState.CONTENT);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
                mMultiStateView.setViewState(MultiStateView.ViewState.ERROR);
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	public void loadActions()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", cid);
		final String URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/get_useraction";				
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						String astr = response.getString("actions");
						List<String> codes = null; 
						if(!TextUtils.isEmpty(astr)){
							String[] actions = astr.split(",");
							codes = Arrays.asList(actions);
							if(codes.contains(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION)){
								funcb.setChecked(true);
							}
							if(codes.contains(StaticValue.USER_ACTION.ACTION_OPPOSE_COLLECTION)){
								noIntrestcb.setChecked(true);
							}
							if(codes.contains(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION)){
								collectBtn.setImageDrawable(getResources().
										getDrawable(R.drawable.ic_collected));
								isCollected =true ; 
							}
						}						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		});
		KuibuApplication.getInstance().addToRequestQueue(req);
	}
	
	public void doAction(String action_type,boolean isChecked)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("action_type", action_type);
		params.put("obj_id", cid);
		String URL=""; 
		if(isChecked){
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/add_useraction";		
		}else{
			URL = Constants.Config.SERVER_URI
					+ Constants.Config.REST_API_VERSION + "/del_useraction";			
		}
		
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				VolleyLog.e("Error:", error.getCause());
				error.printStackTrace();
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String credentials = Session.getSession().getToken()
						+ ":unused";
				headers.put(
						"Authorization",
						"Basic "+ SafeEDcoderUtil.encryptBASE64(
										credentials.getBytes()).replaceAll(
										"\\s+", ""));
				return headers;
			}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);
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
	
	private class DetailGestureListener extends GestureDetector.SimpleOnGestureListener {
		
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		//完成一次单击，并确定没有二击事件后触发（300ms）
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (isTopHide && isToolHide) {
				showTop();
				showTool();
			} else if (!isToolHide && isTopHide) {
				
			} else if (!isTopHide && isToolHide) {
				
			} else {
				if (!isInTopDistance) {
					hideTop();
				}
			}
			return super.onSingleTapConfirmed(e);
		}
	}

	private void showTool() {
		int startY = getWindow().getDecorView()
				.getHeight() - getStatusHeight(this);
		ObjectAnimator anim = ObjectAnimator.ofFloat(layout_tools, "y", startY,
				startY - layout_tools.getHeight());
		anim.setDuration(TIME_ANIMATION);
		anim.start();
		isToolHide = false;
	}
	
	private void hideTool(){
		int startY = getWindow().getDecorView()
				.getHeight() - getStatusHeight(this);
		ObjectAnimator anim = ObjectAnimator.ofFloat(layout_tools, "y", 
				startY - layout_tools.getHeight(),startY);
		anim.setDuration(TIME_ANIMATION);
		anim.start();
		isToolHide = true;
	}
	
	private void showTop() {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(toolbar, "y", toolbar.getY(),
				0);
		anim1.setDuration(TIME_ANIMATION);
		anim1.start();
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(title_tv, "y", title_tv.getY(),
				toolbar.getHeight());
		anim2.setInterpolator(new DecelerateInterpolator());
		anim2.setDuration(TIME_ANIMATION + 200);
		anim2.start();
		ObjectAnimator anim4 = ObjectAnimator.ofFloat(fl_top, "y", fl_top.getY(),
				0);
		anim4.setDuration(TIME_ANIMATION);
		anim4.start();
		isTopHide = false;
	
	}
	
	private void hideTop() {
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(toolbar, "y", 0,
				-toolbar.getHeight());
		anim1.setDuration(TIME_ANIMATION);
		anim1.start();
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(title_tv, "y", title_tv.getY(),
				-title_tv.getHeight());
		anim2.setDuration(TIME_ANIMATION);
		anim2.start();

		ObjectAnimator anim4 = ObjectAnimator.ofFloat(fl_top, "y", 0,
				-(toolbar.getHeight() + title_tv.getHeight()));
		anim4.setDuration(TIME_ANIMATION);
		anim4.start();
		isTopHide = true;
	}
	
	
	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (t <= DensityUtils.dp2px(this, TOP_DISTANCE_Y)) { //当前Scorllbar垂直位置小于预置
			isInTopDistance = true;
		} else {
			isInTopDistance = false;
		}
		if (t <= DensityUtils.dp2px(this,TOP_DISTANCE_Y) && isTopHide) {//小于预置，且顶部被隐藏就将其显示出来
			showTop();
		} else if (t > DensityUtils.dp2px(this,TOP_DISTANCE_Y) && !isTopHide) { //大于预置，顶部没隐藏将其隐藏
			hideTop();
		}

	}
	
	@Override
	public void onBottom() {//Scorllbar到顶部显示底部tool
		if (isToolHide) {
			showTool();
		}
	}
	
	private int dp2px(int dp) {
		float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
	
	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeight = activity.getResources().getDimensionPixelSize(i5);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}
	
	private String adjustMarkDownText(String markdownText) {
		String pattern = "!\\[.*\\]\\(\\s*(http:.*)\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(markdownText);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "\n");		
		}
		m.appendTail(sb);
		return sb.toString();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode){
			case StaticValue.RequestCode.FAVORITE_BOX_REQCODE:
				if(data!=null){
					isCollected = data.getBooleanExtra("isCollected", false);
					if(isCollected){
						collectBtn.setImageDrawable(getResources().
								getDrawable(R.drawable.ic_collected));
					}else{
						collectBtn.setImageDrawable(getResources().
								getDrawable(R.drawable.ic_collect));
					}
				}				
			break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private String contentWarpper(String content)
	{
		int dpheight = fl_top.getHeight();
		int pxheight = (int)(DensityUtils.dp2px(this, dpheight) * (0.35f-title_tv.getLineCount()*0.01));
		StringBuffer buffer = new StringBuffer("<div style=\"height:").append(pxheight).append("px;\"></div>\n");
		buffer.append(content);
		buffer.append("\n<br/><br/><br/>");
		return buffer.toString();
	}
}
