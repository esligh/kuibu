package com.kuibu.module.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.data.global.Session;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.AlbumModeImpl;
import com.kuibu.model.db.AlbumVo;
import com.kuibu.model.db.CollectionVo;
import com.kuibu.model.db.ImageLibVo;
import com.kuibu.model.entity.CollectPackBean;
import com.kuibu.model.entity.CollectionBean;
import com.kuibu.model.interfaces.AlbumModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.AlbumPresenter;
import com.kuibu.module.request.listener.OnAlbumListener;
import com.kuibu.ui.view.interfaces.AlbumView;

public class AlbumPresenterImpl implements AlbumPresenter,OnAlbumListener{

	private static final int UPDATE_LIST_MSGCODE =  1; 

	private List<CollectPackBean> mData = new ArrayList<CollectPackBean>();
	private AlbumView mView ;
	private AlbumModel mModel; 	
	private AlbumVo packVo; 
	private CollectionVo collectionVo;
	private ImageLibVo imageVo;	
	private Handler mHandler;

	
	public AlbumPresenterImpl(AlbumView view) {
		// TODO Auto-generated constructor stub
		this.mView = view ;
		this.mModel = new AlbumModeImpl(this);
		
		packVo = new AlbumVo(mView.getInstance());
		collectionVo = new CollectionVo(mView.getInstance());
		imageVo = new ImageLibVo(mView.getInstance());
		
		handlerEvent();
	}
	
	private void handlerEvent()
	{
		mHandler = new Handler(){
            @Override  
            public void handleMessage(Message msg) {   
                super.handleMessage(msg);
                switch(msg.what){
                	case UPDATE_LIST_MSGCODE:
                		mView.refreshList(mData);
                	break; 
                }
            }			
		};
	}

	@Override
	public void delAlbum(CollectPackBean item) 
	{
		Map<String,String> params = new HashMap<String,String>();
		params.put("pid", String.valueOf(item.pack_id));
		params.put("create_by", Session.getSession().getuId());
		mModel.requestDelAlbum(params);
	}

	@Override
	public void OnDelAlbumResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
				
			}else{
				KuibuUtils.showText(R.string.delete_fail,Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void queryAlbum() {
		// TODO Auto-generated method stub
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
	public void closeDbConn() {
		// TODO Auto-generated method stub
		packVo.closeDB();
		collectionVo.closeDB();
		imageVo.closeDB();
	}

	@Override
	public void delLocalAlbum(int position) {
		// TODO Auto-generated method stub
		final CollectPackBean old = mData.remove(position);
		final String pid = old.getPack_id();
		mView.refreshList(mData);				
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
	}

}
