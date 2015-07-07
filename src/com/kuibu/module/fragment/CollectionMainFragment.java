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
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.kuibu.module.activity.R;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.custom.widget.FButton;
import com.kuibu.data.global.KuibuApplication;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.bean.CollectPackBean;
import com.kuibu.model.bean.CollectionBean;
import com.kuibu.model.vo.CollectPackVo;
import com.kuibu.model.vo.CollectionVo;
import com.kuibu.model.vo.ImageLibVo;
import com.kuibu.module.activity.LocalCollectionListActivity;
import com.kuibu.module.activity.OperCollectPackActivity;
import com.kuibu.module.adapter.CollectPackItemAdapter;

public class CollectionMainFragment extends Fragment {
	private FButton createPackBtn;
	private ListView packList;
	private CollectPackItemAdapter packAdapter;
	private List<CollectPackBean> mData = new ArrayList<CollectPackBean>(); 
	private Context context =null; 
	private CollectPackVo packVo= null ; 
	private CollectionVo collectionVo = null ;
	private ImageLibVo imageVo = null ; 
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this.getActivity() ; 
		packVo = new CollectPackVo(this.getActivity());
		collectionVo = new CollectionVo(this.getActivity());
		imageVo = new ImageLibVo(this.getActivity());
		View rootView = inflater.inflate(R.layout.collectpack_listview,
				container, false);
		createPackBtn = (FButton) rootView
				.findViewById(R.id.create_collectpack_bt);
		packList = (ListView) rootView.findViewById(R.id.collectpack_lv);
		mData = packVo.queryAll(Session.getSession().getuId()) ;	
		packAdapter = new CollectPackItemAdapter(getActivity(), mData);
		packList.setAdapter(packAdapter);
		
		createPackBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context,OperCollectPackActivity.class);
				intent.putExtra("OPER", "CREATE");
				context.startActivity(intent);
				getActivity().overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
			}	
		});
		
		packList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpterView, View view, int position,
					long id) {
				CollectPackBean item = (CollectPackBean)adpterView.getAdapter()
												.getItem(position);
				Intent intent = new Intent(getActivity(),LocalCollectionListActivity.class);
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_ID,item.getPack_id());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECT_PACK_NAME, item.getPack_name());
				intent.putExtra(StaticValue.EDITOR_VALUE.COLLECTION_COUNT,item.collect_count);
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
				AlertDialog.Builder builder = new Builder(context);
				builder.setTitle("操作");
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
									CollectPackBean old = mData.remove(position);
									packVo.delete(old);
									packAdapter.updateView(mData);
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
				            		requestDelpack(old);
									break;
								case 2:
									final EditText input = new EditText(context);
									AlertDialog.Builder builder = new AlertDialog.Builder(context);
									builder.setTitle("请输入")
					        		.setView(input)
					                .setNegativeButton("取消", null)
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					        			public void onClick(DialogInterface dialog, int which) {
					        				String new_name = input.getText().toString().trim();
					        				if(TextUtils.isEmpty(new_name)){
					        					Toast.makeText(context, "名称不能为空",Toast.LENGTH_SHORT).show();
					        					return ; 
					        				}					        				
					        				packVo.update(" pack_name = ? "," _id = ? ", new String[]{new_name,String.valueOf(pid)});
					        				mData.get(position).setPack_name(new_name);
					        				packAdapter.updateView(mData);
					        			}
					        		}).show();
									break;
								}
							}
						});
				builder.show();
				return true;
			}			
		});
		return rootView;
	}

	
	void requestDelpack(CollectPackBean old)
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("pid", String.valueOf(old.pack_id));
		params.put("create_by", Session.getSession().getuId());
		final String URL = StaticValue.SERVER_INFO.SERVER_URI
				+ StaticValue.SERVER_INFO.REST_API_VERSION + "/del_collectpack";
		JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(
				params), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					String state = response.getString("state");
					if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
						Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
	 			String credentials = Session.getSession().getToken()+":unused";
	 			headers.put("Authorization","Basic "+
	 			SafeEDcoderUtil.encryptBASE64(credentials.getBytes()).replaceAll("\\s+", "")); 
	 			return headers;  
	 		}
		};
		KuibuApplication.getInstance().addToRequestQueue(req);	
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mData = packVo.queryAll(Session.getSession().getuId()) ;
		packAdapter.updateView(mData);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		packVo.closeDB();
		collectionVo.closeDB();
		imageVo.closeDB();
		super.onDestroy();
	}
}
