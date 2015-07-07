package com.kuibu.module.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kuibu.module.activity.R;
import com.kuibu.model.bean.CollectorInfoItemBean;
import com.kuibu.model.bean.TopicItemBean;
import com.kuibu.module.adapter.CollectorInfoItemAdapter;
import com.kuibu.module.adapter.FocusTopicItemAdapter;

public class TopicMaterialActivity extends ActionBarActivity{
	ListView father_topic; 
	ListView best_collector; 
	List<TopicItemBean>  topic_datas ;
	List<CollectorInfoItemBean> collector_datas;
	Context context ; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context =this ;
		setContentView(R.layout.topic_material_activity);
		father_topic = (ListView)findViewById(R.id.topic_father_list);
		best_collector = (ListView)findViewById(R.id.topic_best_collector_list);
		initData();
		father_topic.setAdapter(new FocusTopicItemAdapter(this, topic_datas));	
		setListViewHeightBasedOnChildren(father_topic);
		
		father_topic.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				StartSelfActivity();
			}
			
		});
		
		best_collector.setAdapter(new CollectorInfoItemAdapter(this, collector_datas));
		setListViewHeightBasedOnChildren(best_collector);
		best_collector.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adpater, View view, int postion,
					long id) {
				// TODO Auto-generated method stub
				StartUserInfoActivity();
			}			
		});
	}
	
	private void StartSelfActivity()
	{
		Intent intent = new Intent(this,TopicMaterialActivity.class);
		startActivity(intent);
	}
	
	private void StartUserInfoActivity()
	{
		Intent intent = new Intent(this,UserInfoActivity.class);
		startActivity(intent);
	}
	
	private void initData()
	{
		topic_datas = new ArrayList<TopicItemBean>();
		collector_datas=new ArrayList<CollectorInfoItemBean>();
		for(int i =0;i<5;i++){
			TopicItemBean b = new TopicItemBean();
			b.setTopic("父话题#"+i);
			b.setIntroduce("**********描述*************"+i);
			topic_datas.add(b);
		}		
		
		for(int i =0;i<5;i++){
			CollectorInfoItemBean b = new CollectorInfoItemBean();
			b.setName("收集者#"+i);
			b.setIntroduce("不以物喜，不以己悲"+i);
			collector_datas.add(b);
		}
	}	
	
	
	/**
	 * @param listView
	 * 解决ListView和ScorllView冲突
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {    
        ListAdapter listAdapter = listView.getAdapter();    
        if (listAdapter == null) {    
            return;    
        }    
        int totalHeight = 0;    
        for (int i = 0; i < listAdapter.getCount(); i++) {    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0);   
            totalHeight += listItem.getMeasuredHeight();    
        }    
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight    
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+20;  
        listView.setLayoutParams(params);    
    }    
}
