package com.kuibu.module.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuibu.app.model.base.CommonAdapter;
import com.kuibu.app.model.base.ViewHolder;
import com.kuibu.data.global.StaticValue;
import com.kuibu.module.activity.R;

public class FavoriteBoxAdapter extends CommonAdapter<Map<String,String>>{
	
	private  List<String> selIds ; 
	
	public FavoriteBoxAdapter(Context context, List<Map<String,String>> datas,
			List<String> selIds,int layoutId)
	{
		super(context,datas,layoutId); 
		this.selIds = selIds;
	}
	
	public void updateView(List<Map<String,String>> datas,List<String> selIds)
	{ 
		this.selIds = selIds; 
		refreshView(datas);
	}


	@Override
	public void convert(ViewHolder holder, Map<String, String> item) {
		// TODO Auto-generated method stub
		holder.setTvText(R.id.favorite_box_name_tv,item.get("box_name"));
		if(selIds!=null && selIds.contains(item.get("box_id"))){
			holder.setChecked(R.id.favorite_box_cb,true);
		}else{
			holder.setChecked(R.id.favorite_box_cb,false);
		}
		if(StaticValue.SERMODLE.BOX_TYPE_PIC.equals(item.get("box_type"))){
			holder.setImageResource(R.id.type_icon,R.drawable.pack_type_pic);
		}else{
			holder.setImageResource(R.id.type_icon,R.drawable.pack_type_word);
		}
	}
}
