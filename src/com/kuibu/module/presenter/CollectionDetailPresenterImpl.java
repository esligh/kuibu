package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.AssetsUtils;
import com.kuibu.common.utils.DataUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.PreferencesUtils;
import com.kuibu.custom.widget.MultiStateView;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.CollectionDetailModelImpl;
import com.kuibu.model.interfaces.CollectionDetailModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.CollectionDetailPresenter;
import com.kuibu.module.request.listener.OnCollectionDetailListener;
import com.kuibu.ui.activity.CollectionDetailActivity;
import com.kuibu.ui.activity.ReportActivity;
import com.kuibu.ui.view.interfaces.CollectionDetailView;
import com.petebevin.markdown.MarkdownProcessor;

public class CollectionDetailPresenterImpl 
	implements CollectionDetailPresenter,OnCollectionDetailListener{

	private CollectionDetailModel mModel ; 
	private CollectionDetailView mView ; 
	
	private Handler mHandler  ; 
	private String cid ,cisn,createBy;
	private ArrayList<String> mDetailImageList = new ArrayList<String>();	
	private int messageCode ; 
	private int commentCount ; 
	private int voteCount ; 
	private boolean bReport =false ;
	private boolean isInFavorite = false;
	private boolean isSupport = false;
	
	@SuppressLint("HandlerLeak")
	public CollectionDetailPresenterImpl(CollectionDetailView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new CollectionDetailModelImpl(this);
		cid = mView.getDataIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_ID);
        cisn = mView.getDataIntent().getStringExtra(StaticValue.SERMODLE.COLLECTION_CISN);
        
        mHandler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) { //no leak,I know .   
                super.handleMessage(msg);  
                switch (msg.what) {  
                	case StaticValue.MSG_CODE.SHOW_TOOLS:                
						mView.setToolsVisible(true);
						break; 	 
                	case StaticValue.MSG_CODE.HIDE_TOOLS:
                		mView.setToolsVisible(false);
                		break;
                }  
            }  
        };  
	}
	
	@Override
	public void loadContent() 	
	{	
		Map<String, String> params = new HashMap<String, String>();
		params.put("cid", cid);
		mModel.requestContent(params);	
	}
	
	private void readFromJson(JSONObject obj)
	{
		try {
			createBy = obj.getString("create_by");
			String uid = Session.getSession().getuId(); 
	        if(uid != null && uid.equals(createBy)){
	        	messageCode = StaticValue.MSG_CODE.HIDE_TOOLS;
	        }else{
	        	messageCode = StaticValue.MSG_CODE.SHOW_TOOLS;
	        }
	        
	        String coverUrl = obj.getString("cover_url");	
	        if(!TextUtils.isEmpty(coverUrl) && !coverUrl.equals("null")){
	        	mView.setCover(coverUrl);
	        }
	     
	        commentCount = obj.getInt("comment_count");
	        if(commentCount>0){
	        	StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.comment_text))
	        	.append(" ").append(DataUtils.formatNumber(commentCount));
	        	mView.setCommentCount(buff.toString());
	        }
	        voteCount = obj.getInt("vote_count");
	        if(voteCount > 0){
	        	StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.like))
	        	.append(" ").append(DataUtils.formatNumber(voteCount));
	        	mView.setVoteCount(buff.toString());
	        }
			final String template = prepareHeader(obj);
			String type = obj.getString("type");
			String content = obj.getString("content");
			if(StaticValue.EDITOR_VALUE.COLLECTION_TEXTIMAGE.equals(type)){
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mView.getInstance());
				boolean noPic = pref.getBoolean(StaticValue.PrefKey.NO_PICTRUE_KEY, false);
				if(noPic){
					content = adjustMarkDownText(content);
				}
			}
			MarkdownProcessor m = new MarkdownProcessor();
			String html = new String(template);
			StringBuilder footer = new StringBuilder();							
			footer.append("<span class=\"time\">").append("编撰于 ").append(obj.getString("create_time")).append("</span><br>");
			html = html.replace("{footer}", footer.toString());
			content = m.markdown(content);		
			html = html.replace("{content}", content);
			html = replaceImgTagFromHTML(html);
			mView.setContent(html);
			mView.setActionBarAlpha(0);
			mView.setMultiStateView(MultiStateView.ViewState.CONTENT);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}      
	}
	

	@Override
	public void doVote(String action_type,boolean isVoted)
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("action_type", action_type);
		params.put("obj_id", cid);
		String URL=""; 
		if(!isVoted){
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/add_useraction").toString();		
		}else{
			URL = new StringBuilder(Constants.Config.SERVER_URI)
					.append(Constants.Config.REST_API_VERSION)
					.append("/del_useraction").toString();			
		}
		params.put("URL", URL);
		mModel.doReport(params);
	}
	
	@Override	
	public void loadActions()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", Session.getSession().getuId());
		params.put("obj_id", cid);
		mModel.requestUserActions(params);
	}	
	
	@SuppressWarnings("deprecation")
	@Override
	public void doReport() 
	{ 
		AlertDialog.Builder builder =null ; 	
		boolean isDarkTheme = PreferencesUtils.getBooleanByDefault(mView.getInstance(),
				StaticValue.PrefKey.DARK_THEME_KEY, false);
		if(isDarkTheme){
			builder = new Builder(mView.getInstance(),AlertDialog.THEME_HOLO_DARK);
		}else{
			builder = new Builder(mView.getInstance(),AlertDialog.THEME_HOLO_LIGHT);
		}		
		
		builder.setTitle(KuibuUtils.getString(R.string.report_reason));
		builder.setItems(mView.getInstance().getResources().getStringArray(R.array.report_content), 
				new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(
							DialogInterface dialog,
							int position) {
						Map<String,String> params = new HashMap<String,String>();
						params.put("accuser_id", Session.getSession().getuId());
						params.put("defendant_id", createBy);
						params.put("cid", cid);
						String[] items = mView.getInstance().getResources().getStringArray(
								R.array.report_content);
						
						if(items!=null && items.length>position)
							params.put("reason",items[position]);
						
						switch(position){
							case 0:	case 1: case 2: case 3: case 4:
								mModel.doReport(params);
								break;
							case 5:
								mView.setActionBarAlpha(CollectionDetailActivity.ALPHA_MAX);
								Intent intent = new Intent(mView.getInstance(),ReportActivity.class);
								intent.putExtra("defendant", createBy);
								((Activity)mView.getInstance()).startActivityForResult(intent, 
										StaticValue.RequestCode.REPORT_COMPLETE);							
								break;
						}
					}									
		});
		builder.show();
	}

	private String replaceImgTagFromHTML(String html) {
		Document doc = Jsoup.parse(html);
		Elements es = doc.getElementsByTag("img");
		for (Element e : es) {						
			String imgUrl = e.attr("src");			
			mDetailImageList.add(imgUrl);
			if(imgUrl.startsWith(Constants.URI_PREFIX)){
				e.attr("src_link",imgUrl);
			}else{
				String localImgPath = KuibuUtils.getCacheImgFilePath(mView.getInstance(), imgUrl);
				e.attr("src_link","file://" + localImgPath);				
			}              
			e.attr("ori_link",imgUrl); 
            if(!e.attr("class").equals("avatar")){
    			e.attr("src","file:///android_asset/img/default_image_loading.png");
    			e.attr("onclick", "openImage('" + imgUrl + "')");
            }			
		}
		return doc.html();
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

	private String prepareHeader(JSONObject obj) throws JSONException
	{
		StringBuilder sb = new StringBuilder();
		String url = obj.getString("photo");
		if(TextUtils.isEmpty(url) || url.equals("null")){			
				url = "file:///android_asset/img/default_pic_avata.png";
		}
		sb.append("<div><h2 class=\"headline-title\">").append(obj.getString("title")).append("</h2></div>")
				.append("<div class=\"meta\">")
				.append("<div class=\"author-pic\" ><img class=\"avatar\" style=\"vertical-align:middle\" src=\"").append(url).append("\"></div>")
				.append("<div class=\"author-info\">")
				.append("<span class=\"author\">").append(obj.getString("name")).append("&nbsp;<strong>·</strong></span>")
				.append("<span class=\"bio\">&nbsp;").append(obj.getString("signature")).append("</span>")					
				.append("</div></div>");
		
		String template = AssetsUtils.loadText(mView.getInstance(), Constants.TEMPLATE_DEF_URL);
		String html = template.replace("{cssFile}", mView.getCssFile());				
		html = html.replace("{place_holder}", sb.toString());
		return html ; 
	}

	@Override
	public void onLoadContentResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {				
				JSONObject obj = new JSONObject(response.getString("result"));
				if(obj!=null){							
					readFromJson(obj);		
					if(TextUtils.isEmpty(cisn)){
						cisn = obj.getString("cisn");
					}
					KuibuApplication.getCacheInstance().put(cisn, 
							obj, Constants.Config.CONTENT_CACHE_SAVE_TIME);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLoadActionsResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				String astr = response.getString("actions");
				List<String> codes = null; 
				if(!TextUtils.isEmpty(astr)){
					String[] actions = astr.split(",");
					codes = Arrays.asList(actions);
					if(codes.contains(StaticValue.USER_ACTION.ACTION_VOTE_COLLECTION)){
						mView.setVoteBtnDrawable(R.drawable.ab_support_active);
						isSupport = true; 
					}
					if(codes.contains(StaticValue.USER_ACTION.ACTION_COLLECT_COLLECTION)){
						mView.setFavMenuDrawable(R.drawable.ab_fav_active);
						isInFavorite = true;
					}
					
					if(codes.contains(StaticValue.USER_ACTION.ACTION_REPORT_COLLECTION)){
						mView.setReportMenuRrawable(R.drawable.ic_action_report_disabled);
						bReport = true ; 								
					}							
				}						
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVoteResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				if (isSupport) {
					voteCount -= 1; 
					mView.setVoteBtnDrawable(R.drawable.ab_support_normal);
					isSupport = false;						
				} else {
					voteCount += 1;
					mView.setVoteBtnDrawable(R.drawable.ab_support_active);
					isSupport = true;				
				}
				
				if(voteCount > 0){
					StringBuilder buff = new StringBuilder(KuibuUtils.getString(R.string.like))
		        	.append(" ").append(DataUtils.formatNumber(voteCount));
		        	mView.setVoteCount(buff.toString());
				}						
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onReportResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)) {
				bReport = true ;
				mView.setReportMenuRrawable(R.drawable.ic_action_report_disabled);
				Toast.makeText(KuibuApplication.getContext(),"感谢您的举报,我们会尽快处理",Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		mView.setMultiStateView(MultiStateView.ViewState.ERROR);
	}

	@Override
	public boolean isReport() {
		// TODO Auto-generated method stub
		return bReport;
	}

	@Override
	public boolean isInfavorite() {
		// TODO Auto-generated method stub
		return isInFavorite;
	}

	@Override
	public String getCid() {
		// TODO Auto-generated method stub
		return cid;
	}

	@Override
	public int getCommentCount() {
		// TODO Auto-generated method stub
		return commentCount;
	}

	@Override
	public String getCreateBy() {
		// TODO Auto-generated method stub
		return createBy;
	}

	@Override
	public List<String> getImageList() {
		// TODO Auto-generated method stub
		return mDetailImageList;
	}

	@Override
	public void lazyShowTools() {
		// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(messageCode);
			}
		}, 200);
	}

	@Override
	public void setReport(boolean state) {
		// TODO Auto-generated method stub
		this.bReport = state ; 
	}

	@Override
	public void setInFavorite(boolean state) {
		// TODO Auto-generated method stub
		this.isInFavorite = state; 
	}

	@Override
	public void setCommonCount(int count) {
		// TODO Auto-generated method stub
		this.commentCount = count; 
	}

	@Override
	public boolean isVoted() {
		// TODO Auto-generated method stub
		return isSupport;
	}

	@Override
	public void removeCallback() {
		// TODO Auto-generated method stub
    	mHandler.removeCallbacksAndMessages(null);
	}

}
