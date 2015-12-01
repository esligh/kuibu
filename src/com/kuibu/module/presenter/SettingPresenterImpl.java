package com.kuibu.module.presenter;

import java.io.File;
import java.io.FileNotFoundException;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.kuibu.common.utils.FileUtils;
import com.kuibu.common.utils.KuibuUtils;
import com.kuibu.common.utils.PhoneUtils;
import com.kuibu.common.utils.SafeEDcoderUtil;
import com.kuibu.common.utils.StorageUtils;
import com.kuibu.data.global.Constants;
import com.kuibu.data.global.StaticValue;
import com.kuibu.model.SettingModelImpl;
import com.kuibu.model.interfaces.SettingModel;
import com.kuibu.module.activity.R;
import com.kuibu.module.presenter.interfaces.SettingPresenter;
import com.kuibu.module.request.listener.OnSettingListener;
import com.kuibu.ui.view.interfaces.SettingView;

public class SettingPresenterImpl implements SettingPresenter,OnSettingListener{
	
	private SettingView mView ;
	private SettingModel mModel ; 
	private int mNewVersionCode ; 
	private String mAppVersionName ;   
	private String mUpdateUrl; 
    private ProgressDialog progressDlg; 
    private final int MAX_PROGRESS = 100  ;  
    private String logPath ; 
    
	public SettingPresenterImpl(SettingView view)
	{
		this.mView = view ;
		this.mModel = new SettingModelImpl(this);		
	}
	
	@Override
	public void checkVersion() 	
	{	
		if(progressDlg == null){
			progressDlg = new ProgressDialog(mView.getInstantce());
			progressDlg.setCanceledOnTouchOutside(false);
			progressDlg.setMessage(KuibuUtils.getString(R.string.get_versioninfo));
		}	
		progressDlg.show();
		mModel.requestVersion();
	}
	
	private void updateApp() {	   
		if(progressDlg == null){
			progressDlg =  new ProgressDialog(mView.getInstantce());
		}
		progressDlg.setMax(MAX_PROGRESS);
		progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); 	
	
         Dialog dialog = new AlertDialog.Builder(mView.getInstantce())
         		 .setTitle(KuibuUtils.getString(R.string.update_version))
        		 .setMessage(KuibuUtils.getString(R.string.quest_update))
                 .setPositiveButton(KuibuUtils.getString(R.string.btn_confirm),    
                         new DialogInterface.OnClickListener() {    
                             @Override    
                             public void onClick(DialogInterface dialog,    
                                     int witch) {    
                            	 progressDlg.setTitle(KuibuUtils.getString(R.string.updating));   
                            	 progressDlg.show();
                            	 String apkPath = new StringBuilder(
                         				Environment.getExternalStorageDirectory().getAbsolutePath()) 
                         				.append('/').append(mAppVersionName).toString();
                         		 File file = new File(apkPath);
                         		 if (file.exists()) {
                         		 	file.delete();
                         		 }
                                 mModel.downLoadApp(mUpdateUrl, apkPath);    
                             }    
                         })    
                 .setNegativeButton(KuibuUtils.getString(R.string.btn_cancel),    
                         new DialogInterface.OnClickListener() {    
                             public void onClick(DialogInterface dialog,    
                                     int witch) {                                
                             }    
                         }).create();   
         dialog.show();    
	}

	@Override
	public void checkCrash() {
		// TODO Auto-generated method stub
		final String fileName = new StringBuilder("crash-")
		.append(SafeEDcoderUtil.MD5(PhoneUtils.getDeviceId(mView.getInstantce())))
		.append(".log").toString();
		logPath = new StringBuilder(StorageUtils.getFileDirectory(mView.getInstantce())
					.getAbsolutePath()).append(Constants.Config.CRASH_DIR)
					.append(fileName).toString(); 
		if(!FileUtils.isFileExist(logPath)){
				KuibuUtils.showText(R.string.no_upload_log, Toast.LENGTH_SHORT);
				return ; 
		}
		new AlertDialog.Builder(mView.getInstantce()).setTitle(R.string.exception)
		.setMessage(KuibuUtils.getString(R.string.question_upload_log))
		.setPositiveButton(KuibuUtils.getString(R.string.btn_confirm),
			new  DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				String URL = new StringBuilder(Constants.Config.SERVER_URI)
				.append(Constants.Config.REST_API_VERSION)
				.append("/crash_collector").toString();
				try {
						if(progressDlg == null){
							progressDlg = new ProgressDialog(mView.getInstantce());      							
						}
						progressDlg.setTitle(KuibuUtils.getString(R.string.preference_exception));   
						progressDlg.show();
		    			AjaxParams params = new AjaxParams();
		    			params.put("file_name", fileName);
						params.put("data", new File(logPath));
						mModel.uploadFile(URL, params);
				} catch (FileNotFoundException e) {
					//do nothing 
					e.printStackTrace();
				}		   	 			
	    	}
	  })
	  .setNegativeButton(KuibuUtils.getString(R.string.btn_cancel), null)
	  .show();	
	}

	@Override
	public void OnReqVersionResponse(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			String state = response.getString("state");
			if(StaticValue.RESPONSE_STATUS.OPER_SUCCESS.equals(state)){
				String data = response.getString("result");
				JSONArray arr = new JSONArray(data);
				String versionName =  "";
				for(int i =0;i<arr.length();i++){
					JSONObject obj = arr.getJSONObject(i);
					String key = obj.getString("cfg_key");
					if(StaticValue.SERMODLE.VERSION_CODE.equals(key)){
						mNewVersionCode = obj.getInt("cfg_value_a");
						versionName = obj.getString("cfg_name");
						mUpdateUrl = obj.getString("cfg_value_b");										
					}
				}
				mAppVersionName = new StringBuffer(Constants.APP_NAME).append("-").
						append(versionName).append(".apk").toString();
				int curVersionCode = PhoneUtils.getPackageInfo(
						mView.getInstantce()).versionCode;
				progressDlg.dismiss();
				if(mNewVersionCode > curVersionCode){
					updateApp();
				}else{							
					KuibuUtils.showText(R.string.no_new_version, Toast.LENGTH_SHORT);
				}	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void OnDownLoading(long count, long current) {
		// TODO Auto-generated method stub
		int progress = 0;
		if (current != count && current != 0) {
			progress = (int) (current / (float) count * MAX_PROGRESS);
		} else {
			progress = MAX_PROGRESS;
		}
		progressDlg.setProgress(progress);
	}

	@Override
	public void OnDownLoadSuccess() {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
		Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(new File(Environment  
                .getExternalStorageDirectory(), mAppVersionName)),  
                "application/vnd.android.package-archive");  
        mView.getInstantce().startActivity(intent);
	}

	@Override
	public void OnDownLoadFailed() {
		// TODO Auto-generated method stub
		KuibuUtils.showText(R.string.update_fail,Toast.LENGTH_SHORT);
		progressDlg.dismiss();
	}

	@Override
	public void OnUpLoading(long count, long current) {
		// TODO Auto-generated method stub
		int progress = 0;
		if (current != count && current != 0) {
			progress = (int) (current / (float) count * MAX_PROGRESS);
		} else {
			progress = MAX_PROGRESS;
		}
		progressDlg.setProgress(progress);		
	}

	@Override
	public void OnUpLoadSuccess() {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
		FileUtils.delFile(logPath);
		KuibuUtils.showText(R.string.upload_success, 
				Toast.LENGTH_SHORT);
	}

	@Override
	public void OnUpLoadFailed() {
		// TODO Auto-generated method stub
		progressDlg.dismiss();
		KuibuUtils.showText(R.string.upload_fail, Toast.LENGTH_SHORT);
	}

}
