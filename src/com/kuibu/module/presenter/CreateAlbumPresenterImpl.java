package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.CreateAlbumModelImpl;
import com.kuibu.model.db.AlbumVo;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.entity.TopicItemBean;
import com.kuibu.model.interfaces.CreateAlbumModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.CreateAlbumPresenter;
import com.kuibu.module.request.listener.OnCreateAlbumListener;
import com.kuibu.ui.view.interfaces.CreateAlbumView;

public class CreateAlbumPresenterImpl 
	implements CreateAlbumPresenter,OnCreateAlbumListener{

	private CreateAlbumModel mModel; 
	private CreateAlbumView mView ; 
	private List<TopicItemBean> datas = new ArrayList<TopicItemBean>();
	private List<TopicItemBean> tags = new LinkedList<TopicItemBean>();
	private AlbumVo packVo= null ; 
	private String pack_id ; 
	private String operType ; 
	private ProgressDialog progressDlg ; 
	
	
	public CreateAlbumPresenterImpl(CreateAlbumView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ; 
		this.mModel = new CreateAlbumModelImpl(this);
		packVo = new AlbumVo(mView.getInstance());
		operType = mView.getDataIntent().getStringExtra("OPER");
		pack_id = mView.getDataIntent().getStringExtra(StaticValue.COLLECTPACK.PACK_ID);
	}
		

	@Override
	public void addAlbum(final CollectPackBean item) {
		// TODO Auto-generated method stub
		if(progressDlg == null){
			progressDlg = new ProgressDialog(mView.getInstance());
			progressDlg.setCanceledOnTouchOutside(false);
			progressDlg.setMessage(KuibuUtils.getString(R.string.saving));			
		}
		progressDlg.show();
		
		Map<String,String> params = new HashMap<String,String>();
		params.put("pack_name", item.getPack_name());
		params.put("pack_type", item.getPack_type());
		params.put("pack_desc", item.getPack_desc());
		params.put("topic_id", item.getTopic_id());
		params.put("private", item.get_private()==1 ? "1":"");
		params.put("create_by", Session.getSession().getuId());
		String descript = new StringBuffer(Session.getSession().getuId()).append(":") 
				.append(item.getPack_name()).toString(); //csn 生成规则		
		params.put("csn", SafeEDcoderUtil.MD5(descript));
		mModel.requestAddAlbum(params,item);
	}

	@Override
	public void updateAlbum(final CollectPackBean item) {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("pack_id",item.getPack_id());
		params.put("pack_name", item.getPack_name());
		params.put("pack_desc", item.getPack_desc());
		params.put("pack_type", item.getPack_type());
		params.put("topic_id", item.getTopic_id());
		params.put("is_private", item.get_private()==1 ? "1":"");
		params.put("create_by", Session.getSession().getuId());
		mModel.requestUpdateAlbum(params,item);
	}

	@Override
	public void getTopicList(String query) {
		// TODO Auto-generated method stub
		Map<String, String> params = new HashMap<String, String>();
		params.put("slice", query);
		mModel.requestTopicList(params);
	}

	@Override
	public void loadAlbum() {
		// TODO Auto-generated method stub
		if(operType.equals("CREATE")){
			mView.setBarTitle(KuibuUtils.getString(R.string.action_create));
		}else if(operType.equals("MODIFY")){
			mView.setBarTitle(KuibuUtils.getString(R.string.action_modify));						
			Map<String, String> params = new HashMap<String, String>();
			params.put("pack_id", pack_id);
			params.put("uid", Session.getSession().getuId());
			mModel.requestAlbum(params);					
		}
	}

	@Override
	public void closeDbConn() {
		// TODO Auto-generated method stub
		packVo.closeDB();
	}


	@Override
	public String getOperType() {
		// TODO Auto-generated method stub
		return operType;
	}


	@Override
	public String getPackId() {
		// TODO Auto-generated method stub
		return pack_id;
	}


	@Override
	public List<TopicItemBean> getTopicTags() {
		// TODO Auto-generated method stub
		return tags;
	}


	@Override
	public void onAddAlbumResponse(JSONObject response,final CollectPackBean item) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
				CollectPackBean bean = new CollectPackBean();
				bean.setPack_id(response.getString("pack_id"));
				bean.setPack_name(item.pack_name);
				bean.setPack_type(item.pack_type);
				bean.setPack_desc(item.pack_desc);
				bean.setTopic_id(item.topic_id);
				bean.setCreate_by(item.create_by);
				bean.set_private(item.is_private);
				bean.set_sync(1);
				packVo.add(bean);
				KuibuUtils.showText(R.string.create_success,Toast.LENGTH_SHORT);			
			}else{
				KuibuUtils.showText(R.string.create_fail,Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		progressDlg.dismiss();
		mView.close();
	}

	@Override
	public void onUpdateAlbumResponse(JSONObject response,final CollectPackBean item) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
				packVo.update(item);
				KuibuUtils.showText(R.string.modify_success, Toast.LENGTH_SHORT);
			}else{
				KuibuUtils.showText(R.string.modify_fail, Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mView.close();
	}


	@Override
	public void onVolleyError(VolleyError error) {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
		KuibuUtils.showText(R.string.oper_fail,Toast.LENGTH_SHORT);
	}


	@Override
	public void onLoadAlbumResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				String data = response.getString("result");
				JSONObject obj = new JSONObject(data);
				mView.setAlbumName(obj.getString("pack_name"));
				if(StaticValue.SERMODLE.PACK_TYPE_WORD.equals(obj.getString("pack_type"))){
					mView.setAlbumType(R.id.collect_pack_type_word);
				}else{
					mView.setAlbumType(R.id.collect_pack_type_pic);
				}
				mView.setAlbumDesc(obj.getString("pack_desc"));
				mView.setAlbumPrivate(obj.getBoolean("is_private"));
				String[] tids = obj.getString("topic_id").split(",");
				String[] topic_names = obj.getString("topic_names").split(",");
				mView.setTopicTag(topic_names);
				for(int i=0;i<tids.length;i++){
					TopicItemBean item = new TopicItemBean();
					item.setId(tids[i]);
					item.setTopic(topic_names[i]);
					tags.add(item);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void onTopicListResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if (StaticValue.RESPONSE_STATUS.OPER_SUCCESS
					.equals(state)) {
				datas.clear();
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				for (int i = 0; i < arr.length(); i++) {
					JSONObject obj = arr.getJSONObject(i);
					TopicItemBean item = new TopicItemBean();
					item.setId(obj.getString("tid"));
					item.setTopic(obj.getString("topic_name"));
					item.setIntroduce(obj.getString("topic_desc"));
					item.setTopicPicUrl(obj.getString("topic_pic"));
					datas.add(item);
				}
				mView.refreshTopicList(datas);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
