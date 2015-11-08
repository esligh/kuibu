package com.kuibu.module.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectPackVo;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;
import com.kuibu.module.activity.LocalCollectionPListActivity;
import com.kuibu.module.activity.LocalCollectionWListActivity;
import com.kuibu.module.activity.OperCollectPackActivity;
import com.kuibu.module.activity.R;
import com.kuibu.module.adapter.CollectPackItemAdapter;

public class CollectionMainFragment extends Fragment {

	private static final int UPDATE_LIST_MSGCODE =  1; 
	private FButton createPackBtn;
	private ListView packList;
	private CollectPackItemAdapter packAdapter;
	private List<CollectPackBean> mData = new ArrayList<CollectPackBean>(); 
	private CollectPackVo packVo= null ; 
	private CollectionVo collectionVo = null ;
	private ImageLibVo imageVo = null ;	
	private Handler mHandler;
	
	
	@Override
	public void onAttach(Context context){
		super.onAttach(context);
		packVo = new CollectPackVo(context);
		collectionVo = new CollectionVo(context);
		imageVo = new ImageLibVo(context);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.collectpack_listview,
				container, false);
		createPackBtn = (FButton) rootView
				.findViewById(R.id.create_collectpack_bt);
		packList = (ListView) rootView.findViewById(R.id.collectpack_lv);		
		createPackBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(),OperCollectPackActivity.class);
				intent.putExtra("OPER", "CREATE");
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}	
		});
				
		packList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpterView, View view, int position,
					long id) {
				CollectPackBean item = (CollectPackBean)adpterView.getAdapter()
												.getItem(position);
				
				Intent intent = new Intent();
				if(StaticValue.SERMODLE.PACK_TYPE_PIC.equals(item.pack_type)){
					intent.setClass(getActivity(),LocalCollectionPListActivity.class);										
				}else{
					intent.setClass(getActivity(),LocalCollectionWListActivity.class);
				}
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID,item.getPack_id());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME, item.getPack_name());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT,item.getCollect_count());
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}			
		});
		
		packList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adpterView, View view,
					final int position, long id) {
				// TODO Auto-generated method stub
				final CollectPackBean item = (CollectPackBean)adpterView.getAdapter()
												.getItem(position);
				final String pid = item.getPack_id();
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle(getActivity().getString(R.string.operator));
				builder.setItems(getResources().getStringArray(R.array.collectpack_operator),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int pos) {
								switch (pos) {
								case 0:
									Intent intent = new Intent(getActivity(),OperCollectPackActivity.class);
									intent.putExtra("OPER", "MODIFY");
									intent.putExtra("_id", item._id);
									intent.putExtra(StaticValue.COLLECTPACK.PACK_ID, item.getPack_id());
									getActivity().startActivity(intent);
									break;
								case 1:
									final CollectPackBean old = mData.remove(position);
									requestDelpack(old);
									
									new Thread(){
										@Override
										public void run() {
											packVo.delete(old);
											
											Message msg = new Message();
											msg.what = UPDATE_LIST_MSGCODE; 
											mHandler.sendMessage(msg);
											
											collectionVo.delete(" pid= ? ", new String[]{String.valueOf(pid)});
					        				List<CollectionBean> list= collectionVo.queryWithcons("pid = ?", new String[]{String.valueOf(pid)});
					        				StringBuffer ids = new StringBuffer( " cid in( ");
					        				String[] cids = new String[list.size()];
						            		for(int i =0;i<list.size();i++){
						            			ids.append("?,");
						            			cids[i]=String.valueOf(list.get(i).get_id());
						            		}          		
						            		ids = new StringBuffer(ids.subSequence(0, ids.length()-1));
						            		ids.append(" ) ");
						            		imageVo.delete(ids.toString(), cids);						            		
										}										
									}.start(); 
									
									break;
								}
							}
						});
				builder.show();
				return true;
			}			
		});
		mHandler = new Handler(){
            @Override  
            public void handleMessage(Message msg) { //no leak,I know .   
                super.handleMessage(msg);
                switch(msg.what){
                	case UPDATE_LIST_MSGCODE:
                		showView();
                	break; 
                }
            }			
		};
		return rootView;
	}

	private void showView()
	{
		if(packAdapter==null){
			packAdapter = new CollectPackItemAdapter(getActivity(), mData);
			packList.setAdapter(packAdapter);
		}else{
			packAdapter.updateView(mData);
		}	
	}
	
	void requestDelpack(CollectPackBean old)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("pid", String.valueOf(old.pack_id));
		params.put("create_by", Session.getSession().getuId());
		final String URL = Constants.Config.SERVER_URI
				+ Constants.Config.REST_API_VERSION + "/del_collectpack";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String state = response.getString("state");
					if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
						
					}else{
						Toast.makeText(getActivity(), getActivity().getString(R.string.delete_fail),
								Toast.LENGTH_SHORT).show();
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
	 			return KuibuUtils.prepareReqHeader();
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	@Override
	public void onResume() {
		super.onResume();
		new Thread(){
			@Override
			public void run(){
				mData = packVo.queryAll(Session.getSession().getuId()) ;
				Message msg = new Message();
				msg.what = UPDATE_LIST_MSGCODE; 
				mHandler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	public void onDestroy() {
		packVo.closeDB();
		collectionVo.closeDB();
		imageVo.closeDB();
		super.onDestroy();
	}
	
}
